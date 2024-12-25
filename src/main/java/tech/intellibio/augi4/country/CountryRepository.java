package tech.intellibio.augi4.country;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CountryRepository extends JpaRepository<Country, Long> {

    Page<Country> findAllById(Long id, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);
    
    Optional<Country> findByCode(String code);

    boolean existsByNameIgnoreCase(String name);

}
