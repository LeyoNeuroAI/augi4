package tech.intellibio.augi4.document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class DocumentDTO {

    private Integer id;

    @NotNull
    private String content;

    @NotNull
    @Size(max = 255)
    private String embedding;

    @NotNull
    private String filename;

    private Integer sessionId;

    @NotNull
    private Long user;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(final String embedding) {
        this.embedding = embedding;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(final Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(final Long user) {
        this.user = user;
    }

}
