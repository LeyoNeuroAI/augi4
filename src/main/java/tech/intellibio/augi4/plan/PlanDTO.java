package tech.intellibio.augi4.plan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class PlanDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    private Long document;

    private Long storage;

    @NotNull
    private Long noOftoken;

    @NotNull
    private Integer project;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getDocument() {
        return document;
    }

    public void setDocument(final Long document) {
        this.document = document;
    }

    public Long getStorage() {
        return storage;
    }

    public void setStorage(final Long storage) {
        this.storage = storage;
    }

    public Long getNoOftoken() {
        return noOftoken;
    }

    public void setNoOftoken(final Long noOftoken) {
        this.noOftoken = noOftoken;
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(final Integer project) {
        this.project = project;
    }

}
