/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4;

import jakarta.validation.Valid;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.contact.ContactDTO;
import tech.intellibio.augi4.contact.ContactService;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.feedback.FeedbackDTO;
import tech.intellibio.augi4.feedback.FeedbackService;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.plan.PlanRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.professional.ClaudeService;
import tech.intellibio.augi4.project.ProjectDTO;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project.ProjectService;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.role.Role;
import tech.intellibio.augi4.role.RoleRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserDTO;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.user.UserService;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.WebUtils;

/**
 *
 * @author leonard
 */
@Controller

public class ProfessionalController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PlanRepository planRepository;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final ProjectService projectService;
    private final ClaudeService claudeService;
    private final PromptRepository promptRepository;
    private final ContactService contactService;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
        model.addAttribute("productValues", productRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Product::getId, Product::getName)));

        model.addAttribute("planValues", planRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Plan::getId, Plan::getName)));
        model.addAttribute("roleValues", roleRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Role::getId, Role::getDescription)));
        model.addAttribute("countryValues", countryRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Country::getId, Country::getCode)));
    }

    private final UserService userService;

    public ProfessionalController(final UserService userService, tech.intellibio.augi4.feedback.FeedbackService feedbackService, tech.intellibio.augi4.user.UserRepository userRepository, tech.intellibio.augi4.product.ProductRepository productRepository, tech.intellibio.augi4.plan.PlanRepository planRepository, tech.intellibio.augi4.role.RoleRepository roleRepository, tech.intellibio.augi4.country.CountryRepository countryRepository, tech.intellibio.augi4.project.ProjectService projectService, tech.intellibio.augi4.professional.ClaudeService claudeService, tech.intellibio.augi4.prompt.PromptRepository promptRepository, tech.intellibio.augi4.contact.ContactService contactService) {
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.planRepository = planRepository;
        this.roleRepository = roleRepository;
        this.countryRepository = countryRepository;
        this.projectService = projectService;

        this.claudeService = claudeService;
        this.promptRepository = promptRepository;
        this.contactService = contactService;
    }

  

    @GetMapping("/professional/terms")
    public String terms() {
        return "professional/terms";
    }
    
    // Assistant start

    @GetMapping("/assistant")
    public String assistant(Model model, @RequestParam String name, @ModelAttribute("contact") @Valid final ContactDTO contactDTO,
            final BindingResult bindingResult) {

        User user = userRepository.findByEmailIgnoreCase("leo@leo.in");

        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Generate a new session ID
        String sessionId = UUID.randomUUID().toString();

        claudeService.createNewSession(product, user, 0, sessionId);

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

//        model.addAttribute("subjects", Arrays.asList( Subject.values()));

        model.addAttribute("currentSessionId", sessionId);
        model.addAttribute("prompts", prompts.getVisiblePrompt());
        model.addAttribute("name", name);
        return "assistant";
    }
    
    
    @GetMapping(value = "/assistant/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat( @RequestParam String message,
            @RequestParam String sessionId,
            @RequestParam String name)
            throws SQLException {

        User user = userRepository.findByEmailIgnoreCase("leo@leo.in");

        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Prompt prompts = promptRepository.findFirstByPromptProducts(product);

        //System.out.println(sessionId);
        return claudeService.streamResponse(sessionId, message, user, prompts);
    }
    
    
     @PostMapping("/assistant")
    public String assistant(@ModelAttribute("contact") @Valid final ContactDTO contactDTO,
            final BindingResult bindingResult,  final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "assistant";
        }
        contactService.create(contactDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contact.create.success"));
        return "redirect:/assistant?name=assistant";
    }

    
    //  Assitant end 
    
    @GetMapping({"/professional", "/professional/index"})
    public String indexP(final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model, @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        final Page<ProjectDTO> projects = projectService.findByUser(user, pageable);

        model.addAttribute("projects", projects);
//        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(projects));

        return "professional/index";
    }

    @GetMapping("/professional/feedback")
    public String feedback(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        final FeedbackDTO feedbackDTO = new FeedbackDTO();

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
        feedbackDTO.setUser(user.getId());

        model.addAttribute("feedback", feedbackDTO);

        return "professional/feedback";
    }

    @PostMapping("/professional/feedback")
    public String add(@ModelAttribute("feedback") @Valid final FeedbackDTO feedbackDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/professional/feedback";
        }
        feedbackService.create(feedbackDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("feedback.create.success"));
        return "redirect:/professional/feedback";
    }

    @GetMapping("/professional/user/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("user", userService.get(id));
        return "professional/user_edit";
    }

    @PostMapping("/professional/user/{id}")
    public String editU(@PathVariable(name = "id") final Long id,
            @ModelAttribute("user") @Valid final UserDTO userDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "professional/user_edit";
        }
        userService.update(id, userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/professional/user/" + id;
    }

}

 
