package tech.intellibio.augi4.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class ProjectDTO {

    private Long id;

    @Size(max = 255)
    private String file;

    @NotNull
    private String goal;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private String organisationName;

    @NotNull
    @Size(max = 255)
    private String status =  "In Progess";

    @NotNull
    private String summary;

    private Long user;

    private Long progam;

    private Long project1;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(final String goal) {
        this.goal = goal;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(final Long user) {
        this.user = user;
    }

    public Long getProgam() {
        return progam;
    }

    public void setProgam(final Long progam) {
        this.progam = progam;
    }

    public Long getProject1() {
        return project1;
    }

    public void setProject1(final Long project1) {
        this.project1 = project1;
    }

}
