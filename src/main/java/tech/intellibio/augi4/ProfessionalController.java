/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.intellibio.augi4.user.UserService;

/**
 *
 * @author leonard
 */
@Controller

public class ProfessionalController {


//
//    @GetMapping("/genius")
//    public String showChatInterface(@ModelAttribute("geniusMessage")  final ChatMessageDTO chatMessageDTO
//                                    ) {
//    
//       
//        return "professional/genius";
//    }

private final UserService userService;

    public ProfessionalController(final UserService userService) {
      this.userService = userService;
    }
 
    
     @GetMapping("/professional/terms")
    public String terms() {
        return "professional/terms";
    }
    
     @GetMapping("/professional/index")
    public String index() {
        return "professional/index";
    }
   
     @GetMapping("professional/user/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("user", userService.get(id));
        return "professional/user_edit";
    }
    
    
    
    

}
