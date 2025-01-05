package tech.intellibio.augi4.security;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.country.CountryService;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.plan.PlanRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.WebUtils;

@Controller
public class ProfessionalRegistrationController {

    private final ProfessionalRegistrationService professionalRegistrationService;

    private final CountryRepository countryRepository;
    private final PlanRepository planRepository;

    public ProfessionalRegistrationController(
            final ProfessionalRegistrationService professionalRegistrationService, 
            final CountryRepository countryRepository, 
            final PlanRepository planRepository) {
        this.professionalRegistrationService = professionalRegistrationService;
        this.countryRepository = countryRepository;
        this.planRepository = planRepository;
    }

    @ModelAttribute

    public void prepareContext(final Model model) {

        model.addAttribute("countryValues", countryRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Country::getId, Country::getName)));

        model.addAttribute("planValues", planRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Plan::getId, Plan::getName)));
    }

    @GetMapping("/professional/register")
    public String register(Model model,
            @ModelAttribute("registrationRequest") final ProfessionalRegistrationRequest registrationRequest) {
        // Fetch all countries
//        List<Country> countries = countryService.getAllCountries();
//
//      // Convert list of countries to a map (id -> name)
//    Map<Long, String> countryMap = countries.stream()
//        .collect(Collectors.toMap(Country::getId, Country::getName));
//
//    // Add the country map and registration request to the model
//    model.addAttribute("countries", countryMap);  //
        return "professionalRegistration/register";
    }

    @PostMapping("/professional/register")
    public String register(
            @ModelAttribute("registrationRequest") @Valid final ProfessionalRegistrationRequest registrationRequest,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "professionalRegistration/register";
        }
        professionalRegistrationService.register(registrationRequest);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("registration.register.success"));
        return "redirect:/professional/login";
    }

}
