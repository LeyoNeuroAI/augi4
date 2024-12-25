package tech.intellibio.augi4.plan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.intellibio.augi4.user.User;


@Entity
@EntityListeners(AuditingEntityListener.class)
public class Plan {

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

    @Column(nullable = false)
    private String name;

    @Column
    private Long document;

    @Column
    private Long storage;

    @Column(nullable = false)
    private Long noOftoken;

    @Column(nullable = false)
    private Integer project;

    @OneToMany(mappedBy = "plan")
    private Set<User> user;

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

    public Set<User> getUser() {
        return user;
    }

    public void setUser(final Set<User> user) {
        this.user = user;
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
