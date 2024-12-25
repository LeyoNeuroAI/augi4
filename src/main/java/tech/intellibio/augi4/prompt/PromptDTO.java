package tech.intellibio.augi4.prompt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


public class PromptDTO {

    private Long id;

    @NotNull
    private Integer chapterNo;

    @NotNull
    private String invisiblePrompt;

    @NotNull
    private String systemPrompt;

    @NotNull
    private Double version;

    @NotNull
    private List<@Size(max = 255) String> visiblePrompt;

    private Long user;

    @NotNull
    private Long promptProducts;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(final Integer chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getInvisiblePrompt() {
        return invisiblePrompt;
    }

    public void setInvisiblePrompt(final String invisiblePrompt) {
        this.invisiblePrompt = invisiblePrompt;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(final String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(final Double version) {
        this.version = version;
    }

    public List<String> getVisiblePrompt() {
        return visiblePrompt;
    }

    public void setVisiblePrompt(List<String> visiblePrompt) {
        this.visiblePrompt = visiblePrompt;
    }

  

    public Long getUser() {
        return user;
    }

    public void setUser(final Long user) {
        this.user = user;
    }

    public Long getPromptProducts() {
        return promptProducts;
    }

    public void setPromptProducts(final Long promptProducts) {
        this.promptProducts = promptProducts;
    }

}
