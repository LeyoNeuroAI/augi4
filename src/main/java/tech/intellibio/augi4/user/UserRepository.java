package tech.intellibio.augi4.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.role.Role;


public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "role")
    User findByEmailIgnoreCase(String email);

    Page<User> findAllById(Long id, Pageable pageable);

    boolean existsByEmailIgnoreCase(String email);

    User findFirstByPlan(Plan plan);

    User findFirstByRole(Role role);

    User findFirstByCountry(Country country);

}
