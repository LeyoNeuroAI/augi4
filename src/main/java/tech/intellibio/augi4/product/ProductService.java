package tech.intellibio.augi4.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.feedback.Feedback;
import tech.intellibio.augi4.feedback.FeedbackRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final PromptRepository promptRepository;
    private final FeedbackRepository feedbackRepository;

    public ProductService(final ProductRepository productRepository,
            final ChatSessionRepository chatSessionRepository,
            final PromptRepository promptRepository, final FeedbackRepository feedbackRepository) {
        this.productRepository = productRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.promptRepository = promptRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public Page<ProductDTO> findAll(final String filter, final Pageable pageable) {
        Page<Product> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = productRepository.findAllById(longFilter, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(product -> mapToDTO(product, new ProductDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ProductDTO get(final Long id) {
        return productRepository.findById(id)
                .map(product -> mapToDTO(product, new ProductDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProductDTO productDTO) {
        final Product product = new Product();
        mapToEntity(productDTO, product);
        return productRepository.save(product).getId();
    }

    public void update(final Long id, final ProductDTO productDTO) {
        final Product product = productRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(productDTO, product);
        productRepository.save(product);
    }

    public void delete(final Long id) {
        productRepository.deleteById(id);
    }

    private ProductDTO mapToDTO(final Product product, final ProductDTO productDTO) {
        productDTO.setId(product.getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setName(product.getName());
        return productDTO;
    }

    private Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setDescription(productDTO.getDescription());
        product.setName(productDTO.getName());
        return product;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Product product = productRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ChatSession productChatSession = chatSessionRepository.findFirstByProduct(product);
        if (productChatSession != null) {
            referencedWarning.setKey("product.chatSession.product.referenced");
            referencedWarning.addParam(productChatSession.getId());
            return referencedWarning;
        }
        final Prompt promptProductsPrompt = promptRepository.findFirstByPromptProducts(product);
        if (promptProductsPrompt != null) {
            referencedWarning.setKey("product.prompt.promptProducts.referenced");
            referencedWarning.addParam(promptProductsPrompt.getId());
            return referencedWarning;
        }
        final Feedback productFeedback = feedbackRepository.findFirstByProduct(product);
        if (productFeedback != null) {
            referencedWarning.setKey("product.feedback.product.referenced");
            referencedWarning.addParam(productFeedback.getId());
            return referencedWarning;
        }
        return null;
    }

}
