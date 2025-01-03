/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.feedback.FeedbackDTO;
import tech.intellibio.augi4.feedback.FeedbackService;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.user.User;
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


  @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
        model.addAttribute("productValues", productRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Product::getId, Product::getName)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<FeedbackDTO> feedbacks = feedbackService.findAll(filter, pageable);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(feedbacks));
        return "feedback/list";
    }

private final UserService userService;

    public ProfessionalController(final UserService userService, tech.intellibio.augi4.feedback.FeedbackService feedbackService, tech.intellibio.augi4.user.UserRepository userRepository, tech.intellibio.augi4.product.ProductRepository productRepository) {
      this.userService = userService;
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
 
    
     @GetMapping("/professional/terms")
    public String terms() {
        return "professional/terms";
    }
    
     @GetMapping("/professional/index")
    public String index() {
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
   
     @GetMapping("professional/user/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("user", userService.get(id));
        return "professional/user_edit";
    }
    
    
    
    

}
