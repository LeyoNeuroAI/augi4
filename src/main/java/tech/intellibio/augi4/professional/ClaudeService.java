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
import java.net.http.HttpClient;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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
import tech.intellibio.augi4.prompt.Prompt;
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
        

//        this.systemPrompt = entity.getSystemPrompt();
        this.chatSessionService = chatSessionService;
        this.productRepository = productRepository;

    }
    
    ObjectMapper mapper = new ObjectMapper();
    
//     List<Map<String, String>> messages = new ArrayList<>();
    
   

    public SseEmitter streamResponse(String sessionId, String content, User user,  Prompt prompts) throws SQLException {

//        Product product = productRepository.findByName("GrantGenius")
//                .orElseThrow(() -> new RuntimeException("Product not found"));
        //System.out.println( chatData1.size());
        
                ChatSession session = sessionRepository.findBySessionId(sessionId);

        
         ChatMessage chatMessage = messageRepository.findBySession(session)
                                          .orElseGet(() -> {
                                              ChatMessage newChatMessage = new ChatMessage();
                                              newChatMessage.setSession(session);
                                              newChatMessage.setMessage(new ArrayList<>());
                                              return newChatMessage;
                                          });
         
          List<Map<String, String>> messages = chatMessage.getMessage();
        Map<String, String> chatData1 = new HashMap<>();
        
        chatData1.put("role", "user");
        chatData1.put("content", content);


        messages.add(chatData1);
        
//        // Convert the list to a JsonNode
//List<JsonNode> jsonMessages = mapper.valueToTree(messages);

chatMessage.setMessage(messages);
        messageRepository.save(chatMessage); // persist the updated object to the database


//        appendChatHistory(session, chatData1);

        return callClaudeAPI(chatMessage, prompts);

    }

    public SseEmitter callClaudeAPI(ChatMessage chatMessage, Prompt prompts) {
        SseEmitter emitter = new SseEmitter(-1L); // No timeout
        
       StringBuilder assistantContent = new StringBuilder();

        String systemPrompt = prompts.getSystemPrompt();


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Set up headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("x-api-key", apiKey);
                headers.set("anthropic-version", apiVersion);
                headers.set("Accept", "text/event-stream");
                
                List<Map<String, String>> messages = chatMessage.getMessage();
                //printMessagesAsJson( messages);

                // Create request body
                Map<String, Object> requestBody = Map.of(
                        "model", "claude-3-sonnet-20240229",
                        "max_tokens", 2000,
                        "temperature", 0.2,
                        "stream", true, // Important for streaming
                        "system", systemPrompt,
                        "messages", chatMessage.getMessage()
                );

              
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
                                processStreamData(line, emitter, assistantContent);
                            }
                        }

                        emitter.complete();
                        
                        //System.out.println(assistantContent.toString());
                        Map<String, String> chatData2 = new HashMap<>();
                        chatData2.put("role", "assistant");
                        chatData2.put("content", assistantContent.toString());
                        //appendChatHistory(chatSession, chatData2);
                        messages.add(chatData2);
                        messageRepository.save(chatMessage); 

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
    
    
    
    public static void printMessagesAsJson(List<Map<String, String>> messages) {
        if (messages.isEmpty()) {
            System.out.println("\nNo messages yet!");
            return;
        }
        
        System.out.println("{\n    \"messages\": [");
        for (int i = 0; i < messages.size(); i++) {
            Map<String, String> message = messages.get(i);
            System.out.println("        {");
            System.out.println("            \"role\": \"" + message.get("role") + "\",");
            System.out.println("            \"content\": \"" + message.get("content") + "\"");
            System.out.print("        }");
            if (i < messages.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        System.out.println("    ]\n}");
    }
//
// @Transactional
//public void appendChatHistory(ChatSession session, Map<String, Object> chatData) {
//    try {
//       ChatMessage chatMessage = messageRepository.findBySession(session)
//                                          .orElseGet(() -> {
//                                              ChatMessage newChatMessage = new ChatMessage();
//                                              newChatMessage.setSession(session);
//                                              newChatMessage.setMessage(new ArrayList<>());
//                                              return newChatMessage;
//                                          });
//
//        String jsonString;
//        try {
//            jsonString = objectMapper.writeValueAsString(chatData);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error converting chat data to JSON", e);
//        }
//        List<String> messages = chatMessage.getMessage();
//        if (messages == null) {
//            messages = new ArrayList<>();
//            chatMessage.setMessage(messages);
//        }
//        messages.add(jsonString);
//        messageRepository.save(chatMessage); // persist the updated object to the database
//       
//    } catch (Exception e) {
//        throw new RuntimeException("Error appending chat data to ChatMessage entity", e);
//    }
//}



//    public List<Map<String, Object>> getChatHistory(ChatSession session) {
//        
//        ChatMessage chatMessage = messageRepository.findBySession(session)
//                                          .orElseGet(() -> {
//                                              ChatMessage newChatMessage = new ChatMessage();
//                                              newChatMessage.setSession(session);
//                                              newChatMessage.setMessage(new ArrayList<>());
//                                              return newChatMessage;
//                                          });
//        List<Map<String, Object>> chatHistory = new ArrayList<>();
//
//        for (String jsonString : chatMessage.getMessage()) {
//            try {
//                Map<String, Object> chatData = objectMapper.readValue(jsonString, Map.class);
//                chatHistory.add(chatData);
//            } catch (Exception e) {
//                throw new RuntimeException("Error parsing chat message JSON", e);
//            }
//        }
//
//        return chatHistory;
//    }

    private void processStreamData(String line, SseEmitter emitter, StringBuilder assistantContent) {
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

    public ChatSession createNewSession(Product product, User user, int tokenCount, String sessionId) {

        //String sessionId = UUID.randomUUID().toString();
        ChatSession chatSession = new ChatSession();

        chatSession.setSessionId(sessionId);

        chatSession.setProduct(product);
        chatSession.setUser(user);
        chatSession.setTokenCount(tokenCount);
        return sessionRepository.save(chatSession);

    }


   
}
