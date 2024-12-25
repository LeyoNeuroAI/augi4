package tech.intellibio.augi4.security;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.util.WebUtils;


@Controller
public class AdminRegistrationController {

    private final AdminRegistrationService adminRegistrationService;

    public AdminRegistrationController(final AdminRegistrationService adminRegistrationService) {
        this.adminRegistrationService = adminRegistrationService;
    }

    @GetMapping("/admin/register")
    public String register(
            @ModelAttribute("registrationRequest") final AdminRegistrationRequest registrationRequest) {
        return "adminRegistration/register";
    }

    @PostMapping("/admin/register")
    public String register(
            @ModelAttribute("registrationRequest") @Valid final AdminRegistrationRequest registrationRequest,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "adminRegistration/register";
        }
        adminRegistrationService.register(registrationRequest);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("registration.register.success"));
        return "redirect:/admin/login";
    }

}
