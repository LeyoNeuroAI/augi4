package tech.intellibio.augi4.professional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.List;
import java.util.Optional;


public class ProMessageDTO {

    @NotNull
    private String message;
    private String sessionId;
//    private Long product;
     @Null
     private String sender;
     
     @Null
     
     private List <String> prompt;

    public List<String> getPrompt() {
        return prompt;
    }

    public void setPrompt(List<String> prompt) {
        this.prompt = prompt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

   

    
  
//
//    public Long getProduct() {
//        return product;
//    }
//
//    public void setProduct(Long product) {
//        this.product = product;
//    }
    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    

}
