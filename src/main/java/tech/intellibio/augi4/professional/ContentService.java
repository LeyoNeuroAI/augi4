/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.plan.PlanRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;

/**
 *
 * @author leonard
 */
@Service
public class ContentService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    public ContentService(UserRepository userRepository, PlanRepository planRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    public void submitContent(User user, String content) {
        int tokenCount = countTokens(content);
        Plan plan = planRepository.findById(user.getPlan().getId()).orElseThrow(() -> new IllegalArgumentException("Invalid plan"));

        // Check if it's a new month and reset the token usage if necessary
        if (isNewMonth(user.getLastResetDate())) {
            user.setTokensUsed(0);
            user.setLastResetDate(LocalDate.now());
            userRepository.save(user);
        }

        if (user.getTokensUsed() + tokenCount <= plan.getNoOftoken()) {
            user.setTokensUsed(user.getTokensUsed() + tokenCount);
            userRepository.save(user);
        } else {
            // Send a message back to the user indicating that the token limit has been reached
            throw new TokenLimitReachedException("Token limit reached for the current plan.");
        }
    }

    private int countTokens(String content) {
        // Implement your token counting logic here
        return content.split("\\s+").length;
    }

    private boolean isNewMonth(LocalDate lastResetDate) {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonthValue() != lastResetDate.getMonthValue() || currentDate.getYear() != lastResetDate.getYear();
    }
}



