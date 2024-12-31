/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectDTO;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project.ProjectService;
import tech.intellibio.augi4.project_file.ProjectFile;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.WebUtils;

/**
 *
 * @author leonard
 */
@Controller
@RequestMapping("/professional/genie/")
public class projectControllers {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final ProgramRepository programRepository;
    private final PromptRepository promptRepository;
    private final ProjectFileRepository projectFilesRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public projectControllers(tech.intellibio.augi4.program.ProgramRepository programRepository, tech.intellibio.augi4.project.ProjectRepository projectRepository, tech.intellibio.augi4.prompt.PromptRepository promptRepository, final ProjectFileRepository projectFilesRepository, tech.intellibio.augi4.user.UserRepository userRepository, tech.intellibio.augi4.project.ProjectService projectService, tech.intellibio.augi4.country.CountryRepository countryRepository) {

        this.programRepository = programRepository;
        this.projectRepository = projectRepository;
        this.promptRepository = promptRepository;
        this.projectFilesRepository = projectFilesRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.countryRepository = countryRepository;

    }
    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getEmail)));
        model.addAttribute("progamValues", programRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Program::getId, Program::getName)));
        model.addAttribute("project1Values", countryRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Country::getId, Country::getCode)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<ProjectDTO> projects = projectService.findAll(filter, pageable);
        model.addAttribute("projects", projects);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(projects));
        return "project/list";
    }

    @GetMapping("/grant/add")
    public String add(@ModelAttribute("project") final ProjectDTO projectDTO) {
        return "professional/grantGenieAdd";
    }

    @PostMapping("/grant/add")
    public String add(@ModelAttribute("project") @Valid final ProjectDTO projectDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        Project project = createProject(projectDTO, user);
 // Redirect to the new entity's detail page
    redirectAttributes.addAttribute("id", project.getId());
    return "redirect:/professional/genie/grant/edit/{id}";
    }

    @GetMapping("/grant/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {

        Project project = projectRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Program not found"));

        List<ProjectFile> projectFiles = projectFilesRepository.findByFile(project);

        model.addAttribute("projectFiles", projectFiles);

        return "professional/edit";
    }

    private Project createProject(ProjectDTO projectDTO, User user) {
        System.out.println(projectDTO.getProgam());
        Program program = programRepository.findById(projectDTO.getProgam()).
                orElseThrow(() -> new RuntimeException("Program not found"));

        Project project = new Project();

//        
        project.setFile(projectDTO.getFile());
        project.setGoal(projectDTO.getGoal());
        project.setName(projectDTO.getName());
        project.setOrganisationName(projectDTO.getOrganisationName());
        project.setStatus(projectDTO.getStatus());
        project.setSummary(projectDTO.getSummary());

        project.setUser(user);

        project.setProgam(program);

        Project savedProject = new Project();

        try {
            // Save the project first
            savedProject = projectRepository.save(project);

            // Create files based on the number of chapters
            Integer noOfChapters = program.getNoOfChapters();
            List<ProjectFile> files = new ArrayList<>();
            if (noOfChapters != null && noOfChapters > 0) {
                for (int i = 0; i <= noOfChapters; i++) {
                    ProjectFile file = new ProjectFile();
                    System.out.println(i);
                    Prompt prompt = promptRepository.findByChapterNoAndProgram(i, program).
                            orElseThrow(() -> new RuntimeException("Prompt not found"));

                    String vPrompt = prompt.getVisiblePrompt().getFirst();

                    file.setName(vPrompt);
                    file.setContent("Content for Chapter " + i);
                    file.setChapterNo(i);
                    file.setFile(savedProject);
                    files.add(file);

                }

                List<ProjectFile> savedFiles = projectFilesRepository.saveAll(files);

//                // Save the project again to persist the files
//                savedProject = projectService.saveProject(savedProject);
                // Log the number of saved files for debugging
                System.out.println("Number of files saved: " + savedFiles.size());
            }

        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
        }
        return savedProject;
    }

}
