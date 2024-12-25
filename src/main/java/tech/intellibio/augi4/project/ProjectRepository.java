package tech.intellibio.augi4.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.user.User;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findAllById(Long id, Pageable pageable);

    Project findFirstByUser(User user);

    Project findFirstByProgam(Program program);

    Project findFirstByProject1(Country country);

}
