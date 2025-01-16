package tech.intellibio.augi4.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


public class ContactDTO {

    private Long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Subject subject;

    @NotNull
    private String summary;

   
    
    

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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

   

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }
    
    
    

}
