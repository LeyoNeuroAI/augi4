package tech.intellibio.augi4.chat_message;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project_file.ProjectFile;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.util.NotFoundException;


@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ProjectFileRepository projectFileRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;

    public ChatMessageService(final ChatMessageRepository chatMessageRepository,
            final ProjectFileRepository projectFileRepository,
            final ChatSessionRepository chatSessionRepository,
            final ProjectRepository projectRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.projectFileRepository = projectFileRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.projectRepository = projectRepository;
    }

    public List<ChatMessageDTO> findAll() {
        final List<ChatMessage> chatMessages = chatMessageRepository.findAll(Sort.by("id"));
        return chatMessages.stream()
                .map(chatMessage -> mapToDTO(chatMessage, new ChatMessageDTO()))
                .toList();
    }

    public ChatMessageDTO get(final Integer id) {
        return chatMessageRepository.findById(id)
                .map(chatMessage -> mapToDTO(chatMessage, new ChatMessageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ChatMessageDTO chatMessageDTO) {
        final ChatMessage chatMessage = new ChatMessage();
        mapToEntity(chatMessageDTO, chatMessage);
        return chatMessageRepository.save(chatMessage).getId();
    }

    public void update(final Integer id, final ChatMessageDTO chatMessageDTO) {
        final ChatMessage chatMessage = chatMessageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(chatMessageDTO, chatMessage);
        chatMessageRepository.save(chatMessage);
    }

    public void delete(final Integer id) {
        chatMessageRepository.deleteById(id);
    }

    private ChatMessageDTO mapToDTO(final ChatMessage chatMessage,
            final ChatMessageDTO chatMessageDTO) {
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setMessage(chatMessage.getMessage());
        chatMessageDTO.setTokens(chatMessage.getTokens());
        chatMessageDTO.setChapter(chatMessage.getChapter() == null ? null : chatMessage.getChapter().getId());
        chatMessageDTO.setSession(chatMessage.getSession() == null ? null : chatMessage.getSession().getId());
        chatMessageDTO.setProject(chatMessage.getProject() == null ? null : chatMessage.getProject().getId());
        return chatMessageDTO;
    }

    private ChatMessage mapToEntity(final ChatMessageDTO chatMessageDTO,
            final ChatMessage chatMessage) {
        chatMessage.setMessage(chatMessageDTO.getMessage());
        chatMessage.setTokens(chatMessageDTO.getTokens());
        final ProjectFile chapter = chatMessageDTO.getChapter() == null ? null : projectFileRepository.findById(chatMessageDTO.getChapter())
                .orElseThrow(() -> new NotFoundException("chapter not found"));
        chatMessage.setChapter(chapter);
        final ChatSession session = chatMessageDTO.getSession() == null ? null : chatSessionRepository.findById(chatMessageDTO.getSession())
                .orElseThrow(() -> new NotFoundException("session not found"));
        chatMessage.setSession(session);
        final Project project = chatMessageDTO.getProject() == null ? null : projectRepository.findById(chatMessageDTO.getProject())
                .orElseThrow(() -> new NotFoundException("project not found"));
        chatMessage.setProject(project);
        return chatMessage;
    }

}
