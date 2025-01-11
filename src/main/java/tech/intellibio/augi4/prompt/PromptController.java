package tech.intellibio.augi4.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.JsonStringFormatter;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;
import tech.intellibio.augi4.util.WebUtils;

@Controller
@RequestMapping("/prompts")
public class PromptController {

    private final PromptService promptService;
    private final ObjectMapper objectMapper;
    private final ProgramRepository programRepository;
    private final PromptRepository promptRepository;

    public PromptController(final PromptService promptService, final ObjectMapper objectMapper, final ProgramRepository programRepository, tech.intellibio.augi4.prompt.PromptRepository promptRepository) {
        this.promptService = promptService;
        this.objectMapper = objectMapper;
        this.programRepository = programRepository;
        this.promptRepository = promptRepository;
    }

    @InitBinder
    public void jsonFormatting(final WebDataBinder binder) {
        binder.addCustomFormatter(new JsonStringFormatter<List<String>>(objectMapper) {
        }, "visiblePrompt");
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("programValues", programRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Program::getId, Program::getName)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<PromptDTO> prompts = promptService.findAll(filter, pageable);
        model.addAttribute("prompts", prompts);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(prompts));
        return "prompt/list";
    }

    @GetMapping("/add")

    public String add(@ModelAttribute("prompt") final PromptDTO promptDTO) {

        return "prompt/add";
    }

 
    @PostMapping("/add")
    public String add(@ModelAttribute("prompt") @Valid final PromptDTO promptDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "prompt/add";
        }

        Prompt prompt = new Prompt();
        prompt.setChapterNo(promptDTO.getChapterNo());
        prompt.setInvisiblePrompt(promptDTO.getInvisiblePrompt());
        prompt.setSystemPrompt(promptDTO.getSystemPrompt());
        prompt.setVersion(promptDTO.getVersion());
//        List<String> visiblePrompt = new ArrayList<>();
//        visiblePrompt.add("Item 1");
//        visiblePrompt.add("Item 2");
//        visiblePrompt.add("Item 3");
//
        prompt.setVisiblePrompt(promptDTO.getVisiblePrompt());
        final Program program = promptDTO.getProgram() == null ? null : programRepository.findById(promptDTO.getProgram())
                .orElseThrow(() -> new NotFoundException("program not found"));
        prompt.setProgram(program);
//        promptService.create(promptDTO);
        promptRepository.save(prompt);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("prompt.create.success"));
        return "redirect:/prompts";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("prompt", promptService.get(id));
        return "prompt/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("prompt") @Valid final PromptDTO promptDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "prompt/edit";
        }
        promptService.update(id, promptDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("prompt.update.success"));
        return "redirect:/prompts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = promptService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            promptService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("prompt.delete.success"));
        }
        return "redirect:/prompts";
    }

}
