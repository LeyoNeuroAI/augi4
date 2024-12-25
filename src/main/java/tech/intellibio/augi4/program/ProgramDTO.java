package tech.intellibio.augi4.program;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


public class ProgramDTO {

    private Long id;

    @Size(max = 255)
    private String description;

    @NotNull
    private LocalDate endDate;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private Integer noOfChapters;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Boolean status;

    @NotNull
    private Long user;

    @NotNull
    private Long country;

    @NotNull
    @ProgramPromptUnique
    private Long prompt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getNoOfChapters() {
        return noOfChapters;
    }

    public void setNoOfChapters(final Integer noOfChapters) {
        this.noOfChapters = noOfChapters;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(final Boolean status) {
        this.status = status;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(final Long user) {
        this.user = user;
    }

    public Long getCountry() {
        return country;
    }

    public void setCountry(final Long country) {
        this.country = country;
    }

    public Long getPrompt() {
        return prompt;
    }

    public void setPrompt(final Long prompt) {
        this.prompt = prompt;
    }

}
