/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.plan.PlanDTO;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;

/**
 *
 * @author leonard
 */


@ControllerAdvice
public class layout {

   
    
    private final UserRepository userRepository;
    
     public layout(final UserRepository  userRepository) {
        this.userRepository = userRepository;
    }
    
     @Transactional
   @ModelAttribute("plan")
public PlanDTO getCurrentUserPlan(@AuthenticationPrincipal UserDetails userDetails) {
    // Check if the userDetails is not null (user is authenticated)
    if (userDetails != null) {
        // Retrieve the current user from the UserDetails object
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        // Check if the user has a plan
        if (user != null && user.getPlan() != null) {
            // Get the user's plan
            Plan plan = user.getPlan();

            // Print the project name for debugging purposes
            //System.out.println("Current user's plan project: " + plan.getProject());

            // Return the user's plan
            // Map the Plan entity to a PlanDTO
   PlanDTO planDTO =  new PlanDTO();
   
   planDTO.setProject(plan.getProject());
   return planDTO;
        }
    }

    // If the user is not authenticated or doesn't have a plan, return null
    return null;
}



    
}
