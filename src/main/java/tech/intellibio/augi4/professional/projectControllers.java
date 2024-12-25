/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.project.ProjectDTO;
import tech.intellibio.augi4.project.ProjectService;
import tech.intellibio.augi4.util.WebUtils;

/**
 *
 * @author leonard
 */

@Controller
@RequestMapping("/professional/genie/")
public class projectControllers {

   

    private final ProjectService projectService;
    
     public projectControllers(final ProjectService projectService) {
        this.projectService = projectService;
    }
     
    
    
    
     @GetMapping("/grant/add")
    public String add(@ModelAttribute("project") final ProjectDTO projectDTO) {
        return "professional/grantGenieAdd";
    }

    @PostMapping("/grant/add")
    public String add(@ModelAttribute("project") @Valid final ProjectDTO projectDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "professional/grantGenieAdd";
        }
        projectService.create(projectDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("project.create.success"));
        return "redirect:/projects";
    }
    
}
