package tech.intellibio.augi4.prompt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class PromptService {

    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProgramRepository programRepository;

    public PromptService(final PromptRepository promptRepository,
            final UserRepository userRepository, final ProductRepository productRepository,
            final ProgramRepository programRepository) {
        this.promptRepository = promptRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.programRepository = programRepository;
    }

    public Page<PromptDTO> findAll(final String filter, final Pageable pageable) {
        Page<Prompt> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = promptRepository.findAllById(longFilter, pageable);
        } else {
            page = promptRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(prompt -> mapToDTO(prompt, new PromptDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public PromptDTO get(final Long id) {
        return promptRepository.findById(id)
                .map(prompt -> mapToDTO(prompt, new PromptDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PromptDTO promptDTO) {
        final Prompt prompt = new Prompt();
        mapToEntity(promptDTO, prompt);
        return promptRepository.save(prompt).getId();
    }

    public void update(final Long id, final PromptDTO promptDTO) {
        final Prompt prompt = promptRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(promptDTO, prompt);
        promptRepository.save(prompt);
    }

    public void delete(final Long id) {
        promptRepository.deleteById(id);
    }

    private PromptDTO mapToDTO(final Prompt prompt, final PromptDTO promptDTO) {
        promptDTO.setId(prompt.getId());
        promptDTO.setChapterNo(prompt.getChapterNo());
        promptDTO.setInvisiblePrompt(prompt.getInvisiblePrompt());
        promptDTO.setSystemPrompt(prompt.getSystemPrompt());
        promptDTO.setVersion(prompt.getVersion());
        promptDTO.setVisiblePrompt(prompt.getVisiblePrompt());
        promptDTO.setUser(prompt.getUser() == null ? null : prompt.getUser().getId());
        promptDTO.setPromptProducts(prompt.getPromptProducts() == null ? null : prompt.getPromptProducts().getId());
        return promptDTO;
    }

    private Prompt mapToEntity(final PromptDTO promptDTO, final Prompt prompt) {
        prompt.setChapterNo(promptDTO.getChapterNo());
        prompt.setInvisiblePrompt(promptDTO.getInvisiblePrompt());
        prompt.setSystemPrompt(promptDTO.getSystemPrompt());
        prompt.setVersion(promptDTO.getVersion());
        prompt.setVisiblePrompt(promptDTO.getVisiblePrompt());
        final User user = promptDTO.getUser() == null ? null : userRepository.findById(promptDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        prompt.setUser(user);
        final Product promptProducts = promptDTO.getPromptProducts() == null ? null : productRepository.findById(promptDTO.getPromptProducts())
                .orElseThrow(() -> new NotFoundException("promptProducts not found"));
        prompt.setPromptProducts(promptProducts);
        return prompt;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Prompt prompt = promptRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Program promptProgram = programRepository.findFirstByPrompt(prompt);
        if (promptProgram != null) {
            referencedWarning.setKey("prompt.program.prompt.referenced");
            referencedWarning.addParam(promptProgram.getId());
            return referencedWarning;
        }
        return null;
    }

}
