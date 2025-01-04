/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.country.Country;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.intellibio.augi4.professional.ClaudeService;

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
     private final ProductRepository productRepository;
      private final ClaudeService claudeService;

    public projectControllers(tech.intellibio.augi4.program.ProgramRepository programRepository, tech.intellibio.augi4.project.ProjectRepository projectRepository, tech.intellibio.augi4.prompt.PromptRepository promptRepository, final ProjectFileRepository projectFilesRepository, tech.intellibio.augi4.user.UserRepository userRepository, tech.intellibio.augi4.project.ProjectService projectService, tech.intellibio.augi4.country.CountryRepository countryRepository, tech.intellibio.augi4.product.ProductRepository productRepository, tech.intellibio.augi4.professional.ClaudeService claudeService) {

        this.programRepository = programRepository;
        this.projectRepository = projectRepository;
        this.promptRepository = promptRepository;
        this.projectFilesRepository = projectFilesRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
        this.claudeService = claudeService;

    }
    
   
    @GetMapping(value = "/gStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamResponse(@RequestParam String message,
           
            @RequestParam Integer fileId,
            @RequestParam Integer chapterNo,
            @RequestParam Long projectId,
            
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String sessionId,
           
            @RequestParam(required = false) Boolean chainPreviousChapter) throws JsonProcessingException, SQLException {

        String completePrompt;
        
        System.out.println(chainPreviousChapter);

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());


        Project project = projectRepository.findById(projectId).
                orElseThrow(() -> new RuntimeException("Project not found"));
       
      
       
        Prompt prompts = promptRepository.findByChapterNoAndProgram(chapterNo, project.getProgam())
                .orElseThrow(() -> new RuntimeException("Prompt not found"));
                
            
       
       

        String invisiblePrompt = prompts.getInvisiblePrompt();
        
         
        
        // Check if the optional parameter is present and its value
        if (chainPreviousChapter != null && chainPreviousChapter && chapterNo > 0) {
            // Handle the case where the checkbox is checked
            
            Integer previous = chapterNo - 1;
           ProjectFile previousFile = projectFilesRepository.findContentByFileAndChapterNo(project , previous);
           
          
            ProjectFile Chapter0 = projectFilesRepository.findContentByFileAndChapterNo(project , 0);
           
         
            //System.out.println("Chapter0: " + Chapter0);
            // You can further process the previous chapter content here

            completePrompt = invisiblePrompt + "\n" + message + "previous_chapter: " + previousFile.getContent() + "Chapter 0: " + Chapter0.getContent();
        } else {

            // Construct the complete prompt
            completePrompt = invisiblePrompt + "\n" + message;
        }
        
        System.out.println(completePrompt);
        
     return claudeService.streamResponse(sessionId, completePrompt, user, prompts);
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
        redirectAttributes.addAttribute("project", project);
        return "redirect:/professional/genie/grant/edit/{id}";
    }

    @GetMapping("/grant/edit/file/{id}/{fileId}")
    public String editFile(Model model, @PathVariable Long fileId, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        
        
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());

        Product product = productRepository.findByName("GrantGenie")
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Generate a new session ID
        String sessionId = UUID.randomUUID().toString();

        ChatSession newSession = claudeService.createNewSession(product, user, 0, sessionId);

       
        
        
         Prompt prompts = promptRepository.findFirstByPromptProducts(product);
         
        
         
   

        model.addAttribute("currentSessionId", sessionId );
        model.addAttribute("prompts", prompts.getVisiblePrompt());
        // Add the chat sessions to the model
        

        Project project = projectRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectFile> projectFiles = projectFilesRepository.findByFile(project);

        model.addAttribute("projectFiles", projectFiles);

        ProjectFile projectFile = projectFilesRepository.findById(fileId).
                orElseThrow(() -> new RuntimeException("File not found"));

        model.addAttribute("projectFile", projectFile);
        model.addAttribute("project", project);

        return "professional/edit";
    }

    @PostMapping("/grant/edit/file/{id}/{fileId}")
    public String editFilep(Model model, @PathVariable Long fileId, @PathVariable Long id, @RequestParam("content") String content) {
        Project project = projectRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectFile> projectFiles = projectFilesRepository.findByFile(project);

        model.addAttribute("projectFiles", projectFiles);

        ProjectFile projectFile = projectFilesRepository.findById(fileId).
                orElseThrow(() -> new RuntimeException("File not found"));

        projectFile.setContent(content);

        projectFilesRepository.save(projectFile);

        model.addAttribute("projectFile", projectFile);
        model.addAttribute("project", project);

        return "professional/edit";
    }


// File download
@GetMapping(value = "/grant/edit/file/{id}/{fileId}/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> exportToWord(@PathVariable Long id, HttpServletRequest request) throws TikaException, IOException {
        Project project = projectRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectFile> projectFiles = projectFilesRepository.findByFile(project);


      
        XWPFDocument document = new XWPFDocument();
        
        for (ProjectFile file : projectFiles) {
            // Add title
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setStyle("Heading1");
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(file.getName());
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            
            // Add content
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
             String cleanContent = file.getContent().replaceAll("<[^>]*>", "");
            contentRun.setText(cleanContent);
            
            // Add project name
//            XWPFParagraph projectParagraph = document.createParagraph();
//            XWPFRun projectRun = projectParagraph.createRun();
//            projectRun.setText("Project: " + file.getProject().getName());
//            projectRun.setItalic(true);
            
            // Add spacing
            document.createParagraph();
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment;filename=" + project.getName() + ".docx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }
    
   


    @GetMapping("/grant/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {

        Project project = projectRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectFile> projectFiles = projectFilesRepository.findByFile(project);

        model.addAttribute("projectFiles", projectFiles);
        model.addAttribute("project", project);

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
