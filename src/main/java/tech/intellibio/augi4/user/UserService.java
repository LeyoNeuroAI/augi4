package tech.intellibio.augi4.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.document.Document;
import tech.intellibio.augi4.document.DocumentRepository;
import tech.intellibio.augi4.feedback.Feedback;
import tech.intellibio.augi4.feedback.FeedbackRepository;
import tech.intellibio.augi4.plan.Plan;
import tech.intellibio.augi4.plan.PlanRepository;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.role.Role;
import tech.intellibio.augi4.role.RoleRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final ProgramRepository programRepository;
    private final FeedbackRepository feedbackRepository;
    private final PromptRepository promptRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final DocumentRepository documentRepository;

    public UserService(final UserRepository userRepository, final PlanRepository planRepository,
            final RoleRepository roleRepository, final CountryRepository countryRepository,
            final PasswordEncoder passwordEncoder, final ProjectRepository projectRepository,
            final ProgramRepository programRepository, final FeedbackRepository feedbackRepository,
            final PromptRepository promptRepository,
            final ChatSessionRepository chatSessionRepository,
            final DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.roleRepository = roleRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectRepository = projectRepository;
        this.programRepository = programRepository;
        this.feedbackRepository = feedbackRepository;
        this.promptRepository = promptRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.documentRepository = documentRepository;
    }

    public Page<UserDTO> findAll(final String filter, final Pageable pageable) {
        Page<User> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = userRepository.findAllById(longFilter, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setName(user.getName());
        userDTO.setOrganisation(user.getOrganisation());
        userDTO.setStripeCustomerId(user.getStripeCustomerId());
        userDTO.setStripeSubscriptionId(user.getStripeSubscriptionId());
        userDTO.setSubscriptionStatus(user.getSubscriptionStatus());
         userDTO.setTokensUsed(user.getTokensUsed());
   

     userDTO.setLastResetDate(user.getLastResetDate());
        userDTO.setRefer(user.getRefer());
        userDTO.setTerms(user.getTerms());
        userDTO.setPlan(user.getPlan() == null ? null : user.getPlan().getId());
        userDTO.setRole(user.getRole() == null ? null : user.getRole().getId());
        userDTO.setCountry(user.getCountry() == null ? null : user.getCountry().getId());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setEmail(userDTO.getEmail());
        user.setIsActive(userDTO.getIsActive());
        user.setName(userDTO.getName());
        user.setOrganisation(userDTO.getOrganisation());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setStripeCustomerId(userDTO.getStripeCustomerId());
        user.setStripeSubscriptionId(userDTO.getStripeSubscriptionId());
        user.setSubscriptionStatus(userDTO.getSubscriptionStatus());
        user.setRefer(userDTO.getRefer());
        user.setTerms(userDTO.getTerms());
        user.setTokensUsed(userDTO.getTokensUsed());
        user.setLastResetDate(userDTO.getLastResetDate());
        final Plan plan = userDTO.getPlan() == null ? null : planRepository.findById(userDTO.getPlan())
                .orElseThrow(() -> new NotFoundException("plan not found"));
        user.setPlan(plan);
        final Role role = userDTO.getRole() == null ? null : roleRepository.findById(userDTO.getRole())
                .orElseThrow(() -> new NotFoundException("role not found"));
        user.setRole(role);
        final Country country = userDTO.getCountry() == null ? null : countryRepository.findById(userDTO.getCountry())
                .orElseThrow(() -> new NotFoundException("country not found"));
        user.setCountry(country);
        return user;
    }

    public boolean emailExists(final String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Project userProject = projectRepository.findFirstByUser(user);
        if (userProject != null) {
            referencedWarning.setKey("user.project.user.referenced");
            referencedWarning.addParam(userProject.getId());
            return referencedWarning;
        }
        final Program userProgram = programRepository.findFirstByUser(user);
        if (userProgram != null) {
            referencedWarning.setKey("user.program.user.referenced");
            referencedWarning.addParam(userProgram.getId());
            return referencedWarning;
        }
        final Feedback userFeedback = feedbackRepository.findFirstByUser(user);
        if (userFeedback != null) {
            referencedWarning.setKey("user.feedback.user.referenced");
            referencedWarning.addParam(userFeedback.getId());
            return referencedWarning;
        }
       
        final ChatSession userChatSession = chatSessionRepository.findFirstByUser(user);
        if (userChatSession != null) {
            referencedWarning.setKey("user.chatSession.user.referenced");
            referencedWarning.addParam(userChatSession.getId());
            return referencedWarning;
        }
        final Document userDocument = documentRepository.findFirstByUser(user);
        if (userDocument != null) {
            referencedWarning.setKey("user.document.user.referenced");
            referencedWarning.addParam(userDocument.getId());
            return referencedWarning;
        }
        return null;
    }

}
