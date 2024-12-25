package tech.intellibio.augi4.plan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public PlanService(final PlanRepository planRepository, final UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    public Page<PlanDTO> findAll(final String filter, final Pageable pageable) {
        Page<Plan> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = planRepository.findAllById(longFilter, pageable);
        } else {
            page = planRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public PlanDTO get(final Long id) {
        return planRepository.findById(id)
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanDTO planDTO) {
        final Plan plan = new Plan();
        mapToEntity(planDTO, plan);
        return planRepository.save(plan).getId();
    }

    public void update(final Long id, final PlanDTO planDTO) {
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planDTO, plan);
        planRepository.save(plan);
    }

    public void delete(final Long id) {
        planRepository.deleteById(id);
    }

    private PlanDTO mapToDTO(final Plan plan, final PlanDTO planDTO) {
        planDTO.setId(plan.getId());
        planDTO.setName(plan.getName());
        planDTO.setDocument(plan.getDocument());
        planDTO.setStorage(plan.getStorage());
        planDTO.setNoOftoken(plan.getNoOftoken());
        planDTO.setProject(plan.getProject());
        return planDTO;
    }

    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setName(planDTO.getName());
        plan.setDocument(planDTO.getDocument());
        plan.setStorage(planDTO.getStorage());
        plan.setNoOftoken(planDTO.getNoOftoken());
        plan.setProject(planDTO.getProject());
        return plan;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final User planUser = userRepository.findFirstByPlan(plan);
        if (planUser != null) {
            referencedWarning.setKey("plan.user.plan.referenced");
            referencedWarning.addParam(planUser.getId());
            return referencedWarning;
        }
        return null;
    }

}
