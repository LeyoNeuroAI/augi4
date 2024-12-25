package tech.intellibio.augi4.project_file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.chat_message.ChatMessage;
import tech.intellibio.augi4.chat_message.ChatMessageRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class ProjectFileService {

    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ProjectFileService(final ProjectFileRepository projectFileRepository,
            final ProjectRepository projectRepository,
            final ChatMessageRepository chatMessageRepository) {
        this.projectFileRepository = projectFileRepository;
        this.projectRepository = projectRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public Page<ProjectFileDTO> findAll(final String filter, final Pageable pageable) {
        Page<ProjectFile> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = projectFileRepository.findAllById(longFilter, pageable);
        } else {
            page = projectFileRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(projectFile -> mapToDTO(projectFile, new ProjectFileDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ProjectFileDTO get(final Long id) {
        return projectFileRepository.findById(id)
                .map(projectFile -> mapToDTO(projectFile, new ProjectFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProjectFileDTO projectFileDTO) {
        final ProjectFile projectFile = new ProjectFile();
        mapToEntity(projectFileDTO, projectFile);
        return projectFileRepository.save(projectFile).getId();
    }

    public void update(final Long id, final ProjectFileDTO projectFileDTO) {
        final ProjectFile projectFile = projectFileRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(projectFileDTO, projectFile);
        projectFileRepository.save(projectFile);
    }

    public void delete(final Long id) {
        projectFileRepository.deleteById(id);
    }

    private ProjectFileDTO mapToDTO(final ProjectFile projectFile,
            final ProjectFileDTO projectFileDTO) {
        projectFileDTO.setId(projectFile.getId());
        projectFileDTO.setChapterNo(projectFile.getChapterNo());
        projectFileDTO.setContent(projectFile.getContent());
        projectFileDTO.setName(projectFile.getName());
        projectFileDTO.setFile(projectFile.getFile() == null ? null : projectFile.getFile().getId());
        return projectFileDTO;
    }

    private ProjectFile mapToEntity(final ProjectFileDTO projectFileDTO,
            final ProjectFile projectFile) {
        projectFile.setChapterNo(projectFileDTO.getChapterNo());
        projectFile.setContent(projectFileDTO.getContent());
        projectFile.setName(projectFileDTO.getName());
        final Project file = projectFileDTO.getFile() == null ? null : projectRepository.findById(projectFileDTO.getFile())
                .orElseThrow(() -> new NotFoundException("file not found"));
        projectFile.setFile(file);
        return projectFile;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ProjectFile projectFile = projectFileRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ChatMessage chapterChatMessage = chatMessageRepository.findFirstByChapter(projectFile);
        if (chapterChatMessage != null) {
            referencedWarning.setKey("projectFile.chatMessage.chapter.referenced");
            referencedWarning.addParam(chapterChatMessage.getId());
            return referencedWarning;
        }
        return null;
    }

}
