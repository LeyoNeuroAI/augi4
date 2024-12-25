package tech.intellibio.augi4.chat_message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


public class ChatMessageDTO {

    private Integer id;

    @NotNull
    private List<@Size(max = 255) String> message;

    private Integer tokens;

    private Long chapter;

    @NotNull
    private Integer session;

    private Long project;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(final List<String> message) {
        this.message = message;
    }

    public Integer getTokens() {
        return tokens;
    }

    public void setTokens(final Integer tokens) {
        this.tokens = tokens;
    }

    public Long getChapter() {
        return chapter;
    }

    public void setChapter(final Long chapter) {
        this.chapter = chapter;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(final Integer session) {
        this.session = session;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(final Long project) {
        this.project = project;
    }

}
