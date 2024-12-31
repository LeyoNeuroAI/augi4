package tech.intellibio.augi4.prompt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class PromptService {

    private final PromptRepository promptRepository;
    private final ProgramRepository programRepository;
    private final ProductRepository productRepository;

    public PromptService(final PromptRepository promptRepository,
            final ProgramRepository programRepository, final ProductRepository productRepository) {
        this.promptRepository = promptRepository;
        this.programRepository = programRepository;
        this.productRepository = productRepository;
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
        promptDTO.setProgram(prompt.getProgram() == null ? null : prompt.getProgram().getId());
        return promptDTO;
    }

    private Prompt mapToEntity(final PromptDTO promptDTO, final Prompt prompt) {
        prompt.setChapterNo(promptDTO.getChapterNo());
        prompt.setInvisiblePrompt(promptDTO.getInvisiblePrompt());
        prompt.setSystemPrompt(promptDTO.getSystemPrompt());
        prompt.setVersion(promptDTO.getVersion());
        prompt.setVisiblePrompt(promptDTO.getVisiblePrompt());
        final Program program = promptDTO.getProgram() == null ? null : programRepository.findById(promptDTO.getProgram())
                .orElseThrow(() -> new NotFoundException("program not found"));
        prompt.setProgram(program);
        return prompt;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Prompt prompt = promptRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Product promptProduct = productRepository.findFirstByPrompt(prompt);
        if (promptProduct != null) {
            referencedWarning.setKey("prompt.product.prompt.referenced");
            referencedWarning.addParam(promptProduct.getId());
            return referencedWarning;
        }
        return null;
    }

}
