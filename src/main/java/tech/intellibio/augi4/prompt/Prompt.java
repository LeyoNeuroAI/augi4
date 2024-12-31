package tech.intellibio.augi4.prompt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.program.Program;



@Entity
@EntityListeners(AuditingEntityListener.class)
public class Prompt {

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
    private Integer chapterNo;

    @Column(nullable = false, columnDefinition = "text")
    private String invisiblePrompt;

    @Column(nullable = false, columnDefinition = "text")
    private String systemPrompt;

    @Column(nullable = false)
    private Double version;

    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> visiblePrompt;

    @OneToOne(mappedBy = "prompt", fetch = FetchType.LAZY)
    private Product promptProducts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

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

    public void setVisiblePrompt(final List<String> visiblePrompt) {
        this.visiblePrompt = visiblePrompt;
    }

    public Product getPromptProducts() {
        return promptProducts;
    }

    public void setPromptProducts(final Product promptProducts) {
        this.promptProducts = promptProducts;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(final Program program) {
        this.program = program;
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
