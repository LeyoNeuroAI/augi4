package tech.intellibio.augi4.program;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final ProjectRepository projectRepository;
    private final PromptRepository promptRepository;

    public ProgramService(final ProgramRepository programRepository,
            final UserRepository userRepository, final CountryRepository countryRepository,
            final ProjectRepository projectRepository, final PromptRepository promptRepository) {
        this.programRepository = programRepository;
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.projectRepository = projectRepository;
        this.promptRepository = promptRepository;
    }

    public Page<ProgramDTO> findAll(final String filter, final Pageable pageable) {
        Page<Program> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = programRepository.findAllById(longFilter, pageable);
        } else {
            page = programRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(program -> mapToDTO(program, new ProgramDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ProgramDTO get(final Long id) {
        return programRepository.findById(id)
                .map(program -> mapToDTO(program, new ProgramDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProgramDTO programDTO) {
        final Program program = new Program();
        mapToEntity(programDTO, program);
        return programRepository.save(program).getId();
    }

    public void update(final Long id, final ProgramDTO programDTO) {
        final Program program = programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(programDTO, program);
        programRepository.save(program);
    }

    public void delete(final Long id) {
        programRepository.deleteById(id);
    }

    private ProgramDTO mapToDTO(final Program program, final ProgramDTO programDTO) {
        programDTO.setId(program.getId());
        programDTO.setDescription(program.getDescription());
        programDTO.setEndDate(program.getEndDate());
        programDTO.setName(program.getName());
        programDTO.setNoOfChapters(program.getNoOfChapters());
        programDTO.setStartDate(program.getStartDate());
        programDTO.setStatus(program.getStatus());
        programDTO.setUser(program.getUser() == null ? null : program.getUser().getId());
        programDTO.setCountry(program.getCountry() == null ? null : program.getCountry().getId());
        return programDTO;
    }

    private Program mapToEntity(final ProgramDTO programDTO, final Program program) {
        program.setDescription(programDTO.getDescription());
        program.setEndDate(programDTO.getEndDate());
        program.setName(programDTO.getName());
        program.setNoOfChapters(programDTO.getNoOfChapters());
        program.setStartDate(programDTO.getStartDate());
        program.setStatus(programDTO.getStatus());
        final User user = programDTO.getUser() == null ? null : userRepository.findById(programDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        program.setUser(user);
        final Country country = programDTO.getCountry() == null ? null : countryRepository.findById(programDTO.getCountry())
                .orElseThrow(() -> new NotFoundException("country not found"));
        program.setCountry(country);
        return program;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Program program = programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Project progamProject = projectRepository.findFirstByProgam(program);
        if (progamProject != null) {
            referencedWarning.setKey("program.project.progam.referenced");
            referencedWarning.addParam(progamProject.getId());
            return referencedWarning;
        }
        final Prompt programPrompt = promptRepository.findFirstByProgram(program);
        if (programPrompt != null) {
            referencedWarning.setKey("program.prompt.program.referenced");
            referencedWarning.addParam(programPrompt.getId());
            return referencedWarning;
        }
        return null;
    }

}
