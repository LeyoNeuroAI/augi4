package tech.intellibio.augi4.chat_message;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project_file.ProjectFile;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    ChatMessage findFirstByChapter(ProjectFile projectFile);

    ChatMessage findFirstBySession(ChatSession chatSession);

    ChatMessage findFirstByProject(Project project);
    
 ChatMessage findBySession(ChatSession chatSession);

}
