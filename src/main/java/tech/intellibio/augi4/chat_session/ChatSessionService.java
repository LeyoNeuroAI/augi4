package tech.intellibio.augi4.chat_session;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.chat_message.ChatMessage;
import tech.intellibio.augi4.chat_message.ChatMessageRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatSessionService(final ChatSessionRepository chatSessionRepository,
            final ProductRepository productRepository, final UserRepository userRepository,
            final ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public Page<ChatSessionDTO> findAll(final String filter, final Pageable pageable) {
        Page<ChatSession> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = chatSessionRepository.findAllById(integerFilter, pageable);
        } else {
            page = chatSessionRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(chatSession -> mapToDTO(chatSession, new ChatSessionDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ChatSessionDTO get(final Integer id) {
        return chatSessionRepository.findById(id)
                .map(chatSession -> mapToDTO(chatSession, new ChatSessionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ChatSessionDTO chatSessionDTO) {
        final ChatSession chatSession = new ChatSession();
        mapToEntity(chatSessionDTO, chatSession);
        return chatSessionRepository.save(chatSession).getId();
    }

    public void update(final Integer id, final ChatSessionDTO chatSessionDTO) {
        final ChatSession chatSession = chatSessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(chatSessionDTO, chatSession);
        chatSessionRepository.save(chatSession);
    }

    public void delete(final Integer id) {
        chatSessionRepository.deleteById(id);
    }

    private ChatSessionDTO mapToDTO(final ChatSession chatSession,
            final ChatSessionDTO chatSessionDTO) {
        chatSessionDTO.setId(chatSession.getId());
        chatSessionDTO.setSessionId(chatSession.getSessionId());
        chatSessionDTO.setTokenCount(chatSession.getTokenCount());
        chatSessionDTO.setProduct(chatSession.getProduct() == null ? null : chatSession.getProduct().getId());
        chatSessionDTO.setUser(chatSession.getUser() == null ? null : chatSession.getUser().getId());
        return chatSessionDTO;
    }

    private ChatSession mapToEntity(final ChatSessionDTO chatSessionDTO,
            final ChatSession chatSession) {
        chatSession.setSessionId(chatSessionDTO.getSessionId());
        chatSession.setTokenCount(chatSessionDTO.getTokenCount());
        final Product product = chatSessionDTO.getProduct() == null ? null : productRepository.findById(chatSessionDTO.getProduct())
                .orElseThrow(() -> new NotFoundException("product not found"));
        chatSession.setProduct(product);
        final User user = chatSessionDTO.getUser() == null ? null : userRepository.findById(chatSessionDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        chatSession.setUser(user);
        return chatSession;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ChatSession chatSession = chatSessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ChatMessage sessionChatMessage = chatMessageRepository.findFirstBySession(chatSession);
        if (sessionChatMessage != null) {
            referencedWarning.setKey("chatSession.chatMessage.session.referenced");
            referencedWarning.addParam(sessionChatMessage.getId());
            return referencedWarning;
        }
        return null;
    }

}
