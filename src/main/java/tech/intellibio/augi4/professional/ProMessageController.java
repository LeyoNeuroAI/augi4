package tech.intellibio.augi4.professional;

import tech.intellibio.augi4.chat_message.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xml.sax.SAXException;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project_file.ProjectFile;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.security.ProfessionalUserDetailsService;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.JsonStringFormatter;

@Controller
@RequestMapping("/professional")
@Transactional
public class ProMessageController {

    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    private final ProjectFileRepository projectFileRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final ProductRepository productRepository;
    private final ChatMessageRepository messageRepository;
    private final ContentService contentService;
    private final ClaudeService claudeService;

    private final UserRepository userRepository;

    private final GeniusService geniusService;
    private final PromptRepository promptRepository;
    private final RAGService documentService;

    public ProMessageController(final ChatMessageService chatMessageService,
            final ObjectMapper objectMapper, final ProjectFileRepository projectFileRepository,
            final ChatSessionRepository chatSessionRepository, final ProjectRepository projectRepository,
            final ContentService contentService, final CustomUserDetailsService customUserDetailsService,
            final ProfessionalUserDetailsService professionalUserDetailsService,
            final ProductRepository productRepository,
            final GeniusService geniusService, tech.intellibio.augi4.user.UserRepository userRepository,
            tech.intellibio.augi4.professional.ClaudeService claudeService, 
            tech.intellibio.augi4.chat_message.ChatMessageRepository messageRepository, 
            final PromptRepository promptRepository, tech.intellibio.augi4.professional.RAGService documentService 
           ) {
        this.chatMessageService = chatMessageService;
        this.objectMapper = objectMapper;
        this.projectFileRepository = projectFileRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.projectRepository = projectRepository;
        this.contentService = contentService;

        this.userRepository = userRepository;
        this.claudeService = claudeService;
        this.productRepository = productRepository;
        this.geniusService = geniusService;
        this.messageRepository = messageRepository;
        this.promptRepository = promptRepository;
        this.documentService = documentService;

    }

    @InitBinder
    public void jsonFormatting(final WebDataBinder binder) {
        binder.addCustomFormatter(new JsonStringFormatter<List<String>>(objectMapper) {
        }, "message");
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("chapterValues", projectFileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ProjectFile::getId, ProjectFile::getName)));
        model.addAttribute("sessionValues", chatSessionRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ChatSession::getId, ChatSession::getId)));
        model.addAttribute("projectValues", projectRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Project::getId, Project::getName)));


    }

    //Document search 
    @GetMapping("/document")
    public String dchat() {

        return "professional/document";
    }

    //Document search 
    @PostMapping("/document")
    public String dpchat(@RequestParam("files") MultipartFile[] files, @AuthenticationPrincipal UserDetails userDetails, Model model) throws SAXException, IOException {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        Product product = productRepository.findByName("DocumentSearch")
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

        String currentSessionId = documentService.createDocuments(files, user);

        claudeService.createNewSession(product, user, 0, currentSessionId);

        model.addAttribute("currentSessionId", currentSessionId);
        model.addAttribute("prompts", prompts.getVisiblePrompt());

        return "professional/dchat";
    }

    @GetMapping(value = "/dstream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter dchat(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String message, @RequestParam String sessionId)
            throws SQLException {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        Product product = productRepository.findByName("DocumentSearch")
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

        String newMessage = documentService.rag(message, sessionId);

        return claudeService.streamResponse(sessionId, newMessage, user, prompts);
    }

    //Genius
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String message,
            @RequestParam String sessionId,
            @RequestParam String name)
            throws SQLException {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

        //System.out.println(sessionId);
        return claudeService.streamResponse(sessionId, message, user, prompts);
    }

    @GetMapping("/genius")

    public String chatPage(Model model, @AuthenticationPrincipal UserDetails userDetails, @RequestParam String name) {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Generate a new session ID
        String sessionId = UUID.randomUUID().toString();

        claudeService.createNewSession(product, user, 0, sessionId);

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

        model.addAttribute("currentSessionId", sessionId);
        model.addAttribute("prompts", prompts.getVisiblePrompt());
        model.addAttribute("name", name);
        // Add the chat sessions to the model

        return "professional/genius";
    }

//History
    @GetMapping("/genius/history")
    public String getChatWindow(@RequestParam String name, @RequestParam String chatSessionId, Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        ChatSession chatsession = chatSessionRepository.findBySessionId(chatSessionId);

        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String currentSessionId = chatSessionId;

        ChatMessage chatMessage = messageRepository.findBySession(chatsession)
                .orElseGet(() -> {
                    ChatMessage newChatMessage = new ChatMessage();
                    newChatMessage.setSession(chatsession);
                    newChatMessage.setMessage(new ArrayList<>());
                    return newChatMessage;
                });

        List<ChatHistoryDTO> chatHistoryDTOL = chatHistDTO(chatMessage.getMessage());

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
        // Retrieve the user's chat sessions
        List<ChatSession> chatSessions = chatSessionRepository.findByProductAndUser(product, user)
                .stream()
                .sorted(Comparator.comparing(ChatSession::getDateCreated).reversed())
                .limit(10)
                .collect(Collectors.toList());

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

        model.addAttribute("currentSessionId", currentSessionId);

        model.addAttribute("prompts", prompts.getVisiblePrompt());
        // Add the chat sessions to the model
        model.addAttribute("chatSessions", chatSessions);

        model.addAttribute("name", name);

        model.addAttribute("chathistory", chatHistoryDTOL);
        return "professional/geniusHistory";
    }

    public List<ChatHistoryDTO> chatHistDTO(List<Map<String, String>> messages) {

        List<ChatHistoryDTO> chatHistoryDTOL = new ArrayList<>();

        if (messages.isEmpty()) {

            ChatHistoryDTO chatHistoryDTO = new ChatHistoryDTO();

            chatHistoryDTO.setRole("assistant");
            chatHistoryDTO.setContent("No History");
            chatHistoryDTOL.add(chatHistoryDTO);

            return chatHistoryDTOL;
        }

        for (int i = 0; i < messages.size(); i++) {
            Map<String, String> message = messages.get(i);
            ChatHistoryDTO chatHistoryDTO = new ChatHistoryDTO();

            chatHistoryDTO.setRole(message.get("role"));
//            System.out.println(chatHistoryDTO.getRole());
            chatHistoryDTO.setContent(message.get("content"));
//            System.out.println(chatHistoryDTO.getContent());
            chatHistoryDTOL.add(chatHistoryDTO);

        }

        return chatHistoryDTOL;

    }

}
