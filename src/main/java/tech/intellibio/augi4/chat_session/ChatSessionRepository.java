package tech.intellibio.augi4.chat_session;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.user.User;


public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    Page<ChatSession> findAllById(Integer id, Pageable pageable);

    ChatSession findFirstByProduct(Product product);

    ChatSession findFirstByUser(User user);
    
   Optional <ChatSession> findBySessionId (String sessionId);
                

}
