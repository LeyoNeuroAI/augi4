/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

/**
 *
 * @author leonard
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author leonard
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.net.http.HttpClient;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.intellibio.augi4.chat_message.ChatMessage;
import tech.intellibio.augi4.chat_message.ChatMessageRepository;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.chat_session.ChatSessionService;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.user.User;

@Service
public class ClaudeService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final ChatSessionService chatSessionService;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.api.version}")
    private String apiVersion;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

//   private String systemPrompt = "You are an AI biotech expert";
    // @Autowired
    private StringBuilder assistantContent;
    //private final String systemPrompt;

    public ClaudeService(ChatSessionRepository sessionRepository, ChatMessageRepository messageRepository,
            final ChatSessionService chatSessionService,
            final ProductRepository productRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.assistantContent = new StringBuilder();

//        this.systemPrompt = entity.getSystemPrompt();
        this.chatSessionService = chatSessionService;
        this.productRepository = productRepository;

    }

    public SseEmitter streamResponse(String sessionId, String content, User user) throws SQLException {

        Product product = productRepository.findByName("GrantGenius")
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ChatSession session = sessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    // Create a new ChatSession
                    ChatSession newSession = createNewSession(product, user, 0, sessionId);

                    // Create a new ChatMessage for the new session
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSession(newSession);

                   
                    messageRepository.save(chatMessage);

                  

                    return newSession;

                });
        
         

  
        Map<String, Object> chatData1 = new HashMap<>();
                    chatData1.put("role", "user");
                    chatData1.put("content", content);

                  

                  ChatMessage chatMessage = messageRepository.findBySession(session); 

                  appendChatHistory(chatMessage, chatData1);
    
         
        

        return callClaudeAPI(session);

    }

    public SseEmitter callClaudeAPI(ChatSession chatSession) {
        SseEmitter emitter = new SseEmitter(-1L); // No timeout

        String systemPrompt = "You are an AI expert";
        
        ChatMessage chatMessage = messageRepository.findBySession(chatSession);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Set up headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("x-api-key", apiKey);
                headers.set("anthropic-version", apiVersion);
                headers.set("Accept", "text/event-stream");

                // Create request body
                Map<String, Object> requestBody = Map.of(
                        "model", "claude-3-sonnet-20240229",
                        "max_tokens", 2000,
                        "temperature", 0.7,
                        "stream", true, // Important for streaming
                        "system", systemPrompt,
                        "messages",  getChatHistory(chatSession)
                );

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonBody = objectMapper.writeValueAsString(requestBody);

                // Define RequestCallback
                RequestCallback requestCallback = request -> {
                    request.getHeaders().addAll(headers);
                    request.getBody().write(jsonBody.getBytes());
                };

                // Define ResponseExtractor
                ResponseExtractor<Void> responseExtractor = response -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getBody()))) {
                        String line;
                       
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                processStreamData(line, emitter);
                            }
                        }

                        emitter.complete();

                      
                        
                        Map<String, Object> chatData2 = new HashMap<>();
                    chatData2.put("role", "assistant");
                    chatData2.put("content", assistantContent.toString());
                         appendChatHistory(chatMessage, chatData2);
                      

                       
                    }
                    return null;
                };

                // Execute streaming request
                restTemplate.execute(
                        apiUrl,
                        HttpMethod.POST,
                        requestCallback,
                        responseExtractor
                );

            } catch (Exception e) {
                System.out.println("Error calling Claude API: " + e.getMessage());
                emitter.completeWithError(e);
            } finally {
                executor.shutdown();
            }
        });

        return emitter;
    }

    @Transactional
    public void appendChatHistory(ChatMessage chatMessage, Map<String, Object> chatData) {
        try {
            
            if (chatMessage.getMessage() == null) {
                chatMessage.setMessage(new ArrayList<>());
            }
            String jsonString = objectMapper.writeValueAsString(chatData);
            chatMessage.getMessage().add(jsonString);
            messageRepository.save(chatMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error appending chat data to ChatMessage entity", e);
        }
    }

    public List<Map<String, Object>> getChatHistory(ChatSession chatSession) {
        ChatMessage chatMessage = messageRepository.findBySession(chatSession);
        List<Map<String, Object>> chatHistory = new ArrayList<>();

        for (String jsonString : chatMessage.getMessage()) {
            try {
                Map<String, Object> chatData = objectMapper.readValue(jsonString, Map.class);
                chatHistory.add(chatData);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing chat message JSON", e);
            }
        }

        return chatHistory;
    }

    private void processStreamData(String line, SseEmitter emitter) {
        try {
            String jsonData = line.substring(6);

            if (jsonData.equals("[DONE]")) {
                return;
            }

            JsonNode root = objectMapper.readTree(jsonData);
            String type = root.path("type").asText();

            if ("content_block_delta".equals(type)) {
                String deltaText = root.path("delta").path("text").asText();
                if (!deltaText.isEmpty()) {
                    emitter.send(deltaText);
                    assistantContent.append(deltaText);

                }
            }
        } catch (Exception e) {
            try {
                emitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }

    }

    private ChatSession createNewSession(Product product, User user, int tokenCount,  String sessionId) {

        //String sessionId = UUID.randomUUID().toString();
        ChatSession chatSession = new ChatSession();

        chatSession.setSessionId(sessionId);

        chatSession.setProduct(product);
        chatSession.setUser(user);
        chatSession.setTokenCount(tokenCount);
        return sessionRepository.save(chatSession);

    }

    private ChatMessage getMessageHistory(ChatSession chatSession) {
        return messageRepository.findBySession(chatSession);
    }

//        private int estimateTokens(String content) {
//        // Simple estimation: ~4 chars per token
//        return content.length() / 4;
//    }
    private List<Map<String, String>> prepareMessagesForApi(
            List<ChatMessage> history,
            String newMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        int totalTokens = 0;

        // Add new message first (it's the most important)
        messages.add(Map.of(
                "role", "user",
                "content", newMessage
        ));
//

        return messages;
    }
}
