package tech.intellibio.augi4.feedback;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class FeedbackDTO {

    private Integer id;

    @NotNull
    private String summary;

    @NotNull
    @Size(max = 255)
    private String topics;

    private Long user;

    private Long product;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(final String topics) {
        this.topics = topics;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(final Long user) {
        this.user = user;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(final Long product) {
        this.product = product;
    }

}
