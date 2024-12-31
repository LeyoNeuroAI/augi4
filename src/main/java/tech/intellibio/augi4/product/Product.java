package tech.intellibio.augi4.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.feedback.Feedback;
import tech.intellibio.augi4.prompt.Prompt;


@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column
    private String description;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "product")
    private Set<ChatSession> session;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false, unique = true)
    private Prompt prompt;

    @OneToMany(mappedBy = "product")
    private Set<Feedback> productFeedbacks;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<ChatSession> getSession() {
        return session;
    }

    public void setSession(final Set<ChatSession> session) {
        this.session = session;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(final Prompt prompt) {
        this.prompt = prompt;
    }

    public Set<Feedback> getProductFeedbacks() {
        return productFeedbacks;
    }

    public void setProductFeedbacks(final Set<Feedback> productFeedbacks) {
        this.productFeedbacks = productFeedbacks;
    }

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
