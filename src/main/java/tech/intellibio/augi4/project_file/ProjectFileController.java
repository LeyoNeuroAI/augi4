package tech.intellibio.augi4.project_file;

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
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.ReferencedWarning;
import tech.intellibio.augi4.util.WebUtils;


@Controller
@RequestMapping("/projectFiles")
public class ProjectFileController {

    private final ProjectFileService projectFileService;
    private final ProjectRepository projectRepository;

    public ProjectFileController(final ProjectFileService projectFileService,
            final ProjectRepository projectRepository) {
        this.projectFileService = projectFileService;
        this.projectRepository = projectRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("fileValues", projectRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Project::getId, Project::getName)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<ProjectFileDTO> projectFiles = projectFileService.findAll(filter, pageable);
        model.addAttribute("projectFiles", projectFiles);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(projectFiles));
        return "projectFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("projectFile") final ProjectFileDTO projectFileDTO) {
        return "projectFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("projectFile") @Valid final ProjectFileDTO projectFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "projectFile/add";
        }
        projectFileService.create(projectFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("projectFile.create.success"));
        return "redirect:/projectFiles";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("projectFile", projectFileService.get(id));
        return "projectFile/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("projectFile") @Valid final ProjectFileDTO projectFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "projectFile/edit";
        }
        projectFileService.update(id, projectFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("projectFile.update.success"));
        return "redirect:/projectFiles";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = projectFileService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            projectFileService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("projectFile.delete.success"));
        }
        return "redirect:/projectFiles";
    }

}
