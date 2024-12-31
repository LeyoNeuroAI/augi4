package tech.intellibio.augi4.program;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.ReferencedWarning;
import tech.intellibio.augi4.util.WebUtils;


@Controller
@RequestMapping("/programs")
public class ProgramController {

    private final ProgramService programService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public ProgramController(final ProgramService programService,
            final UserRepository userRepository, final CountryRepository countryRepository) {
        this.programService = programService;
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
        model.addAttribute("countryValues", countryRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Country::getId, Country::getCode)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<ProgramDTO> programs = programService.findAll(filter, pageable);
        model.addAttribute("programs", programs);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(programs));
        return "program/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("program") final ProgramDTO programDTO) {
        return "program/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("program") @Valid final ProgramDTO programDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "program/add";
        }
        programService.create(programDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("program.create.success"));
        return "redirect:/programs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("program", programService.get(id));
        return "program/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("program") @Valid final ProgramDTO programDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "program/edit";
        }
        programService.update(id, programDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("program.update.success"));
        return "redirect:/programs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = programService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            programService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("program.delete.success"));
        }
        return "redirect:/programs";
    }

}
