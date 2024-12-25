package tech.intellibio.augi4.security;

import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.plan.PlanRepository;
import tech.intellibio.augi4.role.RoleRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;

@Service
public class ProfessionalRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(ProfessionalRegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final PlanRepository planRepository;

    public ProfessionalRegistrationService(final UserRepository userRepository, final PasswordEncoder passwordEncoder, 
            final RoleRepository roleRepository, final CountryRepository countryRepository, tech.intellibio.augi4.plan.PlanRepository planRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.countryRepository = countryRepository;
        this.planRepository = planRepository;
    }

    public void register(final ProfessionalRegistrationRequest professionalRegistrationRequest) {
        log.info("registering new user: {}", professionalRegistrationRequest.getEmail());

        final User user = new User();
        user.setEmail(professionalRegistrationRequest.getEmail());
        user.setIsActive(true);
        user.setName(professionalRegistrationRequest.getName());
        user.setOrganisation(professionalRegistrationRequest.getOrganisation());
        user.setPassword(passwordEncoder.encode(professionalRegistrationRequest.getPassword()));
        user.setStripeCustomerId("1");
        user.setStripeSubscriptionId("1");
        user.setSubscriptionStatus("1");
        user.setRefer(professionalRegistrationRequest.getRefer());
        user.setTerms(professionalRegistrationRequest.getTerms());
        user.setTokensUsed(0);
        user.setLastResetDate(LocalDate.now());
        Country country = countryRepository.findById(professionalRegistrationRequest.getCountry())
                .orElseThrow(() -> new RuntimeException("Country not found"));
        Plan plan = planRepository.findById(professionalRegistrationRequest.getPlan())
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        user.setPlan(plan);
        user.setCountry(country);
        // assign default role
        user.setRole(roleRepository.findTopByName(UserRoles.PROFESSIONAL));
        userRepository.save(user);
    }

}
