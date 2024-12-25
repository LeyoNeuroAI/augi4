package tech.intellibio.augi4.security;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tech.intellibio.augi4.user.UserEmailUnique;


public class AdminRegistrationRequest {

    @NotNull
    @Size(max = 255)
    @UserEmailUnique(message = "{registration.register.taken}")
    private String email;

    @NotNull
    private Boolean isActive;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String organisation;

    @NotNull
    @Size(max = 72)
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

}
