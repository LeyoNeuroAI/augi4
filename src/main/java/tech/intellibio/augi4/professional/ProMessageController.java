package tech.intellibio.augi4.professional;

import tech.intellibio.augi4.chat_message.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project_file.ProjectFile;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.security.ProfessionalUserDetailsService;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.JsonStringFormatter;

@Controller
@RequestMapping("/professional")
public class ProMessageController {

    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    private final ProjectFileRepository projectFileRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final ProductRepository productRepository;
    private final ContentService contentService;
    private final ClaudeService claudeService;

    private final UserRepository userRepository;

    private final GeniusService geniusService;

    public ProMessageController(final ChatMessageService chatMessageService, final ObjectMapper objectMapper,
            final ProjectFileRepository projectFileRepository, final ChatSessionRepository chatSessionRepository,
            final ProjectRepository projectRepository, final ContentService contentService,
            final CustomUserDetailsService customUserDetailsService,
            final ProfessionalUserDetailsService professionalUserDetailsService,
            final ProductRepository productRepository,
            final GeniusService geniusService,
            tech.intellibio.augi4.user.UserRepository userRepository,
            tech.intellibio.augi4.professional.ClaudeService claudeService
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

//        model.addAttribute("productValues", productRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(Product::getId, Product::getName)));
//        User user =  userRepository.findByEmailIgnoreCase(userDetails.getUsername());
//        
//        Plan plan = user.getPlan();
//        
//        System.out.println(plan.getProject());
//        
//           model.addAttribute("plan", plan);
    }
    private List<ProMessageDTO> chatMessages = new ArrayList<>();

    //Document search 
    @GetMapping("/document")
    public String dchat() {

        return "professional/document";
    }

 
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String message, @RequestParam String sessionId)
            throws SQLException {

            
       User user =  userRepository.findByEmailIgnoreCase(userDetails.getUsername());
       
      
        System.out.println(sessionId);
        
        return claudeService.streamResponse(sessionId, message,   user);
    }

    @GetMapping("/genius/{name}")
    public String chatPage(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable String name) {

        List<Prompt> prompts = geniusService.getPromptsByProductName(name);

        List<String> promptStrings = Arrays.asList(
                "This is the first prompt.",
                "This is the second prompt.",
                "This is the third prompt.",
                "This is the fourth prompt."
        );

        if (chatMessages.isEmpty()) {
            ProMessageDTO proMessageDTO = new ProMessageDTO();
            proMessageDTO.setMessage("Hello! How can I assist you today?"); // Initial greeting message
            proMessageDTO.setSender("Bot"); // Bot is the sender
            proMessageDTO.setSessionId("Bot1");
            proMessageDTO.setPrompt(promptStrings);

            chatMessages.add(proMessageDTO);
        }

        model.addAttribute("newMessage", new ProMessageDTO());
        model.addAttribute("chatMessages", chatMessages);

        return "professional/genius";
    }

//    @PostMapping("/stream")
//    public String add(@ModelAttribute("chatMessage")  final ProMessageDTO proMessageDTO,
//            final RedirectAttributes redirectAttributes,
//            @AuthenticationPrincipal UserDetails userDetails) throws SQLException {
//
////        if (bindingResult.hasErrors()) {
////            return "professional/genius";
////        }
//        try {
////            SseEmitter emitter = new SseEmitter(-1L); // No timeout
////
////            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
////            executor.schedule(() -> {
////                try {
////                    emitter.send("Hello, client!");
////                    emitter.send("This is another message.");
////                    emitter.complete();
////                } catch (Exception e) {
////                    emitter.completeWithError(e);
////                } finally {
////                    executor.shutdown();
////                }
////            }, 0, TimeUnit.SECONDS);
//
//     String sessionId=  proMessageDTO.getSessionId();
//       User user =  userRepository.findByEmailIgnoreCase(userDetails.getUsername());
//       
//      
//        
//        
//         claudeService.streamResponse(sessionId, proMessageDTO.getMessage(),   user);
//
//            return "redirect:/professional/genius";
//        } catch (TokenLimitReachedException e) {
//            System.out.println("limikt reached");
//            return "professional/genius";
//        }
//    }



}
