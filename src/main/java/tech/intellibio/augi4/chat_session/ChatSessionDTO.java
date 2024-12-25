package tech.intellibio.augi4.chat_session;

import jakarta.validation.constraints.NotNull;


public class ChatSessionDTO {

    private Integer id;

    @NotNull
    private String sessionId;

    private Integer tokenCount;

    @NotNull
    private Long product;

    @NotNull
    private Long user;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(final Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(final Long product) {
        this.product = product;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(final Long user) {
        this.user = user;
    }

}
