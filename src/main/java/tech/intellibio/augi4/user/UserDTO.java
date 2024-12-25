package tech.intellibio.augi4.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


public class UserDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @UserEmailUnique
    private String email;

    @NotNull
    @JsonProperty("isActive")
    private Boolean isActive;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String organisation;

    @NotNull
    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String stripeCustomerId;

    @Size(max = 255)
    private String stripeSubscriptionId;

    @Size(max = 255)
    private String subscriptionStatus;

    @Size(max = 255)
    private String refer;

    @NotNull
    private Boolean terms;

    private Long plan;

    @NotNull
    private Long role;

    private Long country;
    
     private long tokensUsed;
    
 
    private LocalDate lastResetDate;

    public long getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(long tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Long getPlan() {
        return plan;
    }

    public void setPlan(final Long plan) {
        this.plan = plan;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(final Long role) {
        this.role = role;
    }

    public Long getCountry() {
        return country;
    }

    public void setCountry(final Long country) {
        this.country = country;
    }

}
