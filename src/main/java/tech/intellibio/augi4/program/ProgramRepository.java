package tech.intellibio.augi4.program;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.user.User;


public interface ProgramRepository extends JpaRepository<Program, Long> {

    Page<Program> findAllById(Long id, Pageable pageable);

    Program findFirstByUser(User user);

    Program findFirstByCountry(Country country);

    Program findFirstByPrompt(Prompt prompt);

    boolean existsByPromptId(Long id);

}
