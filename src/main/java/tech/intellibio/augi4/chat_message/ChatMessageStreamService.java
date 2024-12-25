///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package tech.intellibio.augi4.chat_message;
//
//import java.util.ArrayList;
//import java.net.http.HttpClient;
//import java.sql.SQLException;
//import java.time.Duration;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RequestCallback;
//import org.springframework.web.client.ResponseExtractor;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import tech.intellibio.augi4.chat_session.ChatSession;
//import tech.intellibio.augi4.chat_session.ChatSessionRepository;
//import tech.intellibio.augi4.product.Product;
//import tech.intellibio.augi4.product.ProductRepository;
//import tech.intellibio.augi4.user.User;
//import tech.intellibio.augi4.user.UserRepository;
//
///**
// *
// * @author leonard
// */
//@Service
//public class ChatMessageStreamService {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${claude.max.tokens}")
//    private Integer maxTokens;
//
//    @Value("${anthropic.api.key}")
//    private String apiKey;
//
//    @Value("${anthropic.api.url}")
//    private String apiUrl;
//
//    @Value("${anthropic.api.version}")
//    private String apiVersion;
//
//
//
//    private final ObjectMapper objectMapper;
//    private final HttpClient httpClient;
//
//    private final ChatMessageRepository chatMessageRepository;
// 
//    private final ChatSessionRepository chatSessionRepository;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//
//   //private StringBuilder assistantContent;
//
//    public ChatMessageStreamService(final ChatMessageRepository chatMessageRepository,
//          
//            final ChatSessionRepository chatSessionRepository,
//            final UserRepository userRepository,
//         
//            final ProductRepository productRepository
//          
//
//    
//        ) {
//        this.chatMessageRepository = chatMessageRepository;
//       
//        this.chatSessionRepository = chatSessionRepository;
//        this.userRepository = userRepository;
//        this.restTemplate = new RestTemplate();
//        this.objectMapper = new ObjectMapper();
//        this.httpClient = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(10))
//                .build();
//     
//        this.productRepository = productRepository;
//       
//    }
//
//    private ChatSession createNewSession(User user, int sessionId, Product product) {
//        ChatSession newSession = new ChatSession();
//        newSession.setUser(user);
//        newSession.setSessionId(sessionId);
//        newSession.setProduct(product);
//
//        return chatSessionRepository.save(newSession);
//    }
//    
//    private StringBuilder assistantContent = new StringBuilder();
//
//    public SseEmitter streamResponse(Integer sessionId, String content, Long userId, Long productId) throws SQLException {
//        
//        
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")); // Fetch user
//
//        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//
//        ChatSession chatSession = chatSessionRepository.findBySessionIdAndUser(sessionId, user)
//                .orElseGet(() -> createNewSession(user, sessionId, product));
//
//        List<ChatMessage> messageHistory = getMessageHistory(chatSession);
//        //System.out.println("hi");
//
//        // Create new user message
//        ChatMessage userMessage = new ChatMessage();
//        //      userMessage.setProject(project);
//        userMessage.setRole("user");
//        userMessage.setContent(content);
//        userMessage.setSessionId(chatSession);
//
////        userMessage.setTimestamp(LocalDateTime.now());
////        userMessage.setSessionId(session.getId());
//        // Prepare messages for Claude API while respecting token limits
//        List<Map<String, String>> messages = prepareMessagesForApi(messageHistory, content);
//        //System.out.println(messages);
//
//        SseEmitter emitter = new SseEmitter(-1L); // No timeout
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            try {
////               
//                // Set up headers
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                headers.set("x-api-key", apiKey);
//                headers.set("anthropic-version", apiVersion);
//                headers.set("Accept", "text/event-stream");
//
//                // Create request body
//                Map<String, Object> requestBody = Map.of(
//                        "model", "claude-3-sonnet-20240229",
//                        "max_tokens", 2000,
//                        "temperature", 0.7,
//                        "stream", true, // Important for streaming
//                        "system", "You are a specialized AI biotech expert",
//                        "messages", messages
//                );
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                String jsonBody;
//
//                jsonBody = objectMapper.writeValueAsString(requestBody);
//
////                HttpEntity<Map<String, Object>> requestEntity = 
////                    new HttpEntity<>(requestBody, headers);
//                // Define RequestCallback
//                RequestCallback requestCallback = request -> {
//                    request.getHeaders().addAll(headers);
//                    request.getBody().write(jsonBody.getBytes());
//                };
//
//                // Define ResponseExtractor
//                ResponseExtractor<Void> responseExtractor = response -> {
//                    try (BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(response.getBody()))) {
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            if (line.startsWith("data: ")) {
//                                processStreamData(line, emitter);
//
//                            }
//                        }
//                        ChatMessage assistantMessage = new ChatMessage();
//                        assistantMessage.setRole("assistant");
//                        assistantMessage.setSessionId(chatSession);
//                        //System.out.println(assistantContent.toString());
//                        assistantMessage.setContent(assistantContent.toString());
//
//                        chatMessageRepository.save(assistantMessage);
//                        chatMessageRepository.save(userMessage);
//                        emitter.complete();
//                    }
//                    return null;
//                };
//
//                // Execute streaming request
//                restTemplate.execute(
//                        apiUrl,
//                        HttpMethod.POST,
//                        requestCallback,
//                        responseExtractor
//                );
//
//            } catch (Exception e) {
//                System.out.println("Error in stream: " + e.getMessage());
//                emitter.completeWithError(e);
//            } finally {
//                executor.shutdown();
//            }
//        });
//
//        //throw new RuntimeException("Unexpected response structure from Anthropic API");
//        return emitter;
//
//    }
//
//    private List<Map<String, String>> prepareMessagesForApi(
//            List<ChatMessage> history,
//            String newMessage) {
//        List<Map<String, String>> messages = new ArrayList<>();
//        int totalTokens = 0;
//
//        // Add new message first (it's the most important)
//        messages.add(Map.of(
//                "role", "user",
//                "content", newMessage
//        ));
//
//        // Add historical messages in reverse order until we hit token limit
//        for (int i = history.size() - 1; i >= 0; i--) {
//            ChatMessage msg = history.get(i);
//            int estimatedTokens = estimateTokens(msg.getContent());
//
//            if (totalTokens + estimatedTokens <= maxTokens) {
//                messages.add(0, Map.of(
//                        "role", msg.getRole(),
//                        "content", msg.getContent()
//                ));
//                totalTokens += estimatedTokens;
//            } else {
//                break;
//            }
//        }
//
//        return messages;
//    }
//
//    private void processStreamData(String line, SseEmitter emitter) {
//        
//         
//        try {
//            String jsonData = line.substring(6);
//
//            if (jsonData.equals("[DONE]")) {
//                return;
//            }
//
//            JsonNode root = objectMapper.readTree(jsonData);
//            String type = root.path("type").asText();
//
//            if ("content_block_delta".equals(type)) {
//                String deltaText = root.path("delta").path("text").asText();
//                if (!deltaText.isEmpty()) {
//                    emitter.send(deltaText);
//                    assistantContent.append(deltaText);
//
//                }
//            }
//        } catch (Exception e) {
//            try {
//                emitter.completeWithError(e);
//            } catch (Exception ignored) {
//            }
//        }
//
//    }
//
//    private int estimateTokens(String content) {
//        // Simple estimation: ~4 chars per token
//        return content.length() / 4;
//    }
//
//    private List<ChatMessage> getMessageHistory(ChatSession chatSession) {
//        return chatMessageRepository.findBySessionId(chatSession);
//    }
//}
