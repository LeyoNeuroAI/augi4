package tech.intellibio.augi4.product;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllById(Long id, Pageable pageable);
    
    Optional <Product> findByName(String Name);

}
