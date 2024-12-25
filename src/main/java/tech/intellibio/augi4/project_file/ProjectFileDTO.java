package tech.intellibio.augi4.project_file;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class ProjectFileDTO {

    private Long id;

    @NotNull
    private Integer chapterNo;

    private String content;

    @NotNull
    @Size(max = 255)
    private String name;

    private Long file;

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

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getFile() {
        return file;
    }

    public void setFile(final Long file) {
        this.file = file;
    }

}
