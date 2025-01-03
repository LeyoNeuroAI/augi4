package tech.intellibio.augi4.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.chat_message.ChatMessage;
import tech.intellibio.augi4.chat_message.ChatMessageRepository;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.project_file.ProjectFile;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final CountryRepository countryRepository;
    private final ProjectFileRepository projectFileRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ProjectService(final ProjectRepository projectRepository,
            final UserRepository userRepository, final ProgramRepository programRepository,
            final CountryRepository countryRepository,
            final ProjectFileRepository projectFileRepository,
            final ChatMessageRepository chatMessageRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.programRepository = programRepository;
        this.countryRepository = countryRepository;
        this.projectFileRepository = projectFileRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public Page<ProjectDTO> findAll(final String filter, final Pageable pageable) {
        Page<Project> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = projectRepository.findAllById(longFilter, pageable);
        } else {
            page = projectRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(project -> mapToDTO(project, new ProjectDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }
    
    
    

    public ProjectDTO get(final Long id) {
        return projectRepository.findById(id)
                .map(project -> mapToDTO(project, new ProjectDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProjectDTO projectDTO) {
        final Project project = new Project();
        mapToEntity(projectDTO, project);
        return projectRepository.save(project).getId();
    }

    public void update(final Long id, final ProjectDTO projectDTO) {
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(projectDTO, project);
        projectRepository.save(project);
    }

    public void delete(final Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDTO mapToDTO(final Project project, final ProjectDTO projectDTO) {
        projectDTO.setId(project.getId());
        projectDTO.setFile(project.getFile());
        projectDTO.setGoal(project.getGoal());
        projectDTO.setName(project.getName());
        projectDTO.setOrganisationName(project.getOrganisationName());
        projectDTO.setStatus(project.getStatus());
        projectDTO.setSummary(project.getSummary());
        projectDTO.setUser(project.getUser() == null ? null : project.getUser().getId());
        projectDTO.setProgam(project.getProgam() == null ? null : project.getProgam().getId());
        projectDTO.setProject1(project.getProject1() == null ? null : project.getProject1().getId());
        return projectDTO;
    }

    private Project mapToEntity(final ProjectDTO projectDTO, final Project project) {
        project.setFile(projectDTO.getFile());
        project.setGoal(projectDTO.getGoal());
        project.setName(projectDTO.getName());
        project.setOrganisationName(projectDTO.getOrganisationName());
        project.setStatus(projectDTO.getStatus());
        project.setSummary(projectDTO.getSummary());
        final User user = projectDTO.getUser() == null ? null : userRepository.findById(projectDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        project.setUser(user);
        final Program progam = projectDTO.getProgam() == null ? null : programRepository.findById(projectDTO.getProgam())
                .orElseThrow(() -> new NotFoundException("progam not found"));
        project.setProgam(progam);
        final Country project1 = projectDTO.getProject1() == null ? null : countryRepository.findById(projectDTO.getProject1())
                .orElseThrow(() -> new NotFoundException("project1 not found"));
        project.setProject1(project1);
        return project;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ProjectFile fileProjectFile = projectFileRepository.findFirstByFile(project);
        if (fileProjectFile != null) {
            referencedWarning.setKey("project.projectFile.file.referenced");
            referencedWarning.addParam(fileProjectFile.getId());
            return referencedWarning;
        }
        final ChatMessage projectChatMessage = chatMessageRepository.findFirstByProject(project);
        if (projectChatMessage != null) {
            referencedWarning.setKey("project.chatMessage.project.referenced");
            referencedWarning.addParam(projectChatMessage.getId());
            return referencedWarning;
        }
        return null;
    }

}
