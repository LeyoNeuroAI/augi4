package tech.intellibio.augi4.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;


@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FeedbackService(final FeedbackRepository feedbackRepository,
            final UserRepository userRepository, final ProductRepository productRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Page<FeedbackDTO> findAll(final String filter, final Pageable pageable) {
        Page<Feedback> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = feedbackRepository.findAllById(integerFilter, pageable);
        } else {
            page = feedbackRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(feedback -> mapToDTO(feedback, new FeedbackDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public FeedbackDTO get(final Integer id) {
        return feedbackRepository.findById(id)
                .map(feedback -> mapToDTO(feedback, new FeedbackDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final FeedbackDTO feedbackDTO) {
        final Feedback feedback = new Feedback();
        mapToEntity(feedbackDTO, feedback);
        return feedbackRepository.save(feedback).getId();
    }

    public void update(final Integer id, final FeedbackDTO feedbackDTO) {
        final Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(feedbackDTO, feedback);
        feedbackRepository.save(feedback);
    }

    public void delete(final Integer id) {
        feedbackRepository.deleteById(id);
    }

    private FeedbackDTO mapToDTO(final Feedback feedback, final FeedbackDTO feedbackDTO) {
        feedbackDTO.setId(feedback.getId());
        feedbackDTO.setSummary(feedback.getSummary());
        feedbackDTO.setTopics(feedback.getTopics());
        feedbackDTO.setUser(feedback.getUser() == null ? null : feedback.getUser().getId());
        feedbackDTO.setProduct(feedback.getProduct() == null ? null : feedback.getProduct().getId());
        return feedbackDTO;
    }

    private Feedback mapToEntity(final FeedbackDTO feedbackDTO, final Feedback feedback) {
        feedback.setSummary(feedbackDTO.getSummary());
        feedback.setTopics(feedbackDTO.getTopics());
        final User user = feedbackDTO.getUser() == null ? null : userRepository.findById(feedbackDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        feedback.setUser(user);
        final Product product = feedbackDTO.getProduct() == null ? null : productRepository.findById(feedbackDTO.getProduct())
                .orElseThrow(() -> new NotFoundException("product not found"));
        feedback.setProduct(product);
        return feedback;
    }

}
