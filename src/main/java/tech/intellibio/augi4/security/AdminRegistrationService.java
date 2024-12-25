package tech.intellibio.augi4.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.role.RoleRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;


@Service
public class AdminRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(AdminRegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
     private final CountryRepository countryRepository;
      private final RoleRepository roleRepository;

    public AdminRegistrationService(final UserRepository userRepository, final PasswordEncoder passwordEncoder, 
            final CountryRepository countryRepository, tech.intellibio.augi4.role.RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.countryRepository = countryRepository;
        this.roleRepository = roleRepository;
    }

    public void register(final AdminRegistrationRequest adminRegistrationRequest) {
        log.info("registering new user: {}", adminRegistrationRequest.getEmail());

        final User user = new User();
        user.setEmail(adminRegistrationRequest.getEmail());
        user.setIsActive(adminRegistrationRequest.getIsActive());
        user.setName(adminRegistrationRequest.getName());
        user.setOrganisation(adminRegistrationRequest.getOrganisation());
        user.setPassword(passwordEncoder.encode(adminRegistrationRequest.getPassword()));
        user.setStripeCustomerId(adminRegistrationRequest.getStripeCustomerId());
        user.setStripeSubscriptionId(adminRegistrationRequest.getStripeSubscriptionId());
        user.setSubscriptionStatus(adminRegistrationRequest.getSubscriptionStatus());
        user.setRefer(adminRegistrationRequest.getRefer());
        user.setTerms(adminRegistrationRequest.getTerms());
         Country country = countryRepository.findByCode("NL")
                .orElseThrow(() -> new RuntimeException("Country not found"));
        user.setCountry(country);
         user.setRole(roleRepository.findTopByName(UserRoles.ADMIN));
        // TODO assign missing relations
        userRepository.save(user);
    }

}
