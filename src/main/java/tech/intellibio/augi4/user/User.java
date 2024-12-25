package tech.intellibio.augi4.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.document.Document;
import tech.intellibio.augi4.feedback.Feedback;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.role.Role;


@Entity
@Table(name = "\"User\"")
@EntityListeners(AuditingEntityListener.class)
public class User {

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String organisation;

    @Column(nullable = false)
    private String password;

    @Column
    private String stripeCustomerId;

    @Column
    private String stripeSubscriptionId;

    @Column
    private String subscriptionStatus;

    @Column
    private String refer;

    @Column(nullable = false)
    private Boolean terms;
    
   
    private long tokensUsed;
    
 
    private LocalDate lastResetDate;

   

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "user")
    private Set<Project> userProjects;

    @OneToMany(mappedBy = "user")
    private Set<Program> userPrograms;

    @OneToMany(mappedBy = "user")
    private Set<Feedback> userFeedbacks;

    @OneToMany(mappedBy = "user")
    private Set<Prompt> userPrompts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "user")
    private Set<ChatSession> userChatSessions;

    @OneToMany(mappedBy = "user")
    private Set<Document> document;

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

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOrganisation() {
        return organisation;
    }
    
     public long getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(long tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public void setOrganisation(final String organisation) {
        this.organisation = organisation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(final String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(final String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(final String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(final String refer) {
        this.refer = refer;
    }

    public Boolean getTerms() {
        return terms;
    }

    public void setTerms(final Boolean terms) {
        this.terms = terms;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(final Plan plan) {
        this.plan = plan;
    }

    public Set<Project> getUserProjects() {
        return userProjects;
    }

    public void setUserProjects(final Set<Project> userProjects) {
        this.userProjects = userProjects;
    }

    public Set<Program> getUserPrograms() {
        return userPrograms;
    }

    public void setUserPrograms(final Set<Program> userPrograms) {
        this.userPrograms = userPrograms;
    }

    public Set<Feedback> getUserFeedbacks() {
        return userFeedbacks;
    }

    public void setUserFeedbacks(final Set<Feedback> userFeedbacks) {
        this.userFeedbacks = userFeedbacks;
    }

    public Set<Prompt> getUserPrompts() {
        return userPrompts;
    }

    public void setUserPrompts(final Set<Prompt> userPrompts) {
        this.userPrompts = userPrompts;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    public Set<ChatSession> getUserChatSessions() {
        return userChatSessions;
    }

    public void setUserChatSessions(final Set<ChatSession> userChatSessions) {
        this.userChatSessions = userChatSessions;
    }

    public Set<Document> getDocument() {
        return document;
    }

    public void setDocument(final Set<Document> document) {
        this.document = document;
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
