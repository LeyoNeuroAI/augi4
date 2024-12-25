package tech.intellibio.augi4.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.user.User;


public interface DocumentRepository extends JpaRepository<Document, Integer> {

    Page<Document> findAllById(Integer id, Pageable pageable);

    Document findFirstByUser(User user);

}
