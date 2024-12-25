/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author leonard
 */


@RestController
@RequestMapping(value = "/professional/api")
public class ProMessageResource {

   
    
      private final GeniusService geniusService;
      
      
       public ProMessageResource(tech.intellibio.augi4.professional.GeniusService geniusService) {
        this.geniusService = geniusService;
    }
    
    
//    
//     @GetMapping("/{name}/prompts")
//    public List<Prompt> getPromptsByProductName(@PathVariable String name) {
//        return geniusService.getPromptsByProductName(name);
//    }
    
}
