package tech.intellibio.augi4.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.user.User;


public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    Page<Feedback> findAllById(Integer id, Pageable pageable);

    Feedback findFirstByUser(User user);

    Feedback findFirstByProduct(Product product);

}
