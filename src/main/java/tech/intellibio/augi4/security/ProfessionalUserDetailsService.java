package tech.intellibio.augi4.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;


@Service
public class ProfessionalUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(ProfessionalUserDetailsService.class);

    private final UserRepository userRepository;

    public ProfessionalUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ProfessionalUserDetails loadUserByUsername(final String username) {
        final User user = userRepository.findByEmailIgnoreCase(username);
        if (user == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
        return new ProfessionalUserDetails(user.getId(), username, user.getPassword(), authorities);
    }

}
