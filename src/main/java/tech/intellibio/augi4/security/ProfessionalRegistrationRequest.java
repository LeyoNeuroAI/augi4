package tech.intellibio.augi4.security;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tech.intellibio.augi4.user.UserEmailUnique;


public class ProfessionalRegistrationRequest {

    @NotNull
    @Size(max = 255)
    @UserEmailUnique(message = "{registration.register.taken}")
    private String email;



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
    private String refer;

    @NotNull
    private Boolean terms;
    
    private Long country;
    
    private Long plan;

    public Long getPlan() {
        return plan;
    }

    public void setPlan(Long plan) {
        this.plan = plan;
    }
    
    

    public Long getCountry() {
        return country;
    }

    public void setCountry(Long country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
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
