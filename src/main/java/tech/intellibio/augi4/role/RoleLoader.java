package tech.intellibio.augi4.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class RoleLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleLoader.class);

    private final RoleRepository roleRepository;

    public RoleLoader(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (roleRepository.count() != 0) {
            return;
        }
        log.info("initializing roles");
        final Role professionalRole = new Role();
        professionalRole.setName("PROFESSIONAL");
        professionalRole.setDescription("Nulla facilisis.");
        roleRepository.save(professionalRole);
    }

}
