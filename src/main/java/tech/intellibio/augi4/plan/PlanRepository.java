package tech.intellibio.augi4.plan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanRepository extends JpaRepository<Plan, Long> {

    Page<Plan> findAllById(Long id, Pageable pageable);

}
