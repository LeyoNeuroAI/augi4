package tech.intellibio.augi4.chat_message;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tech.intellibio.augi4.chat_session.ChatSession;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project_file.ProjectFile;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.JsonStringFormatter;
import tech.intellibio.augi4.util.WebUtils;


@Controller
@RequestMapping("/chatMessages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    private final ProjectFileRepository projectFileRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;

    public ChatMessageController(final ChatMessageService chatMessageService,
            final ObjectMapper objectMapper, final ProjectFileRepository projectFileRepository,
            final ChatSessionRepository chatSessionRepository,
            final ProjectRepository projectRepository) {
        this.chatMessageService = chatMessageService;
        this.objectMapper = objectMapper;
        this.projectFileRepository = projectFileRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.projectRepository = projectRepository;
    }

    @InitBinder
    public void jsonFormatting(final WebDataBinder binder) {
        binder.addCustomFormatter(new JsonStringFormatter<List<String>>(objectMapper) {
        }, "message");
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("chapterValues", projectFileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ProjectFile::getId, ProjectFile::getName)));
        model.addAttribute("sessionValues", chatSessionRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ChatSession::getId, ChatSession::getId)));
        model.addAttribute("projectValues", projectRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Project::getId, Project::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("chatMessages", chatMessageService.findAll());
        return "chatMessage/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("chatMessage") final ChatMessageDTO chatMessageDTO) {
        return "chatMessage/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("chatMessage") @Valid final ChatMessageDTO chatMessageDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chatMessage/add";
        }
        chatMessageService.create(chatMessageDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chatMessage.create.success"));
        return "redirect:/chatMessages";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Integer id, final Model model) {
        model.addAttribute("chatMessage", chatMessageService.get(id));
        return "chatMessage/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Integer id,
            @ModelAttribute("chatMessage") @Valid final ChatMessageDTO chatMessageDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chatMessage/edit";
        }
        chatMessageService.update(id, chatMessageDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chatMessage.update.success"));
        return "redirect:/chatMessages";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Integer id,
            final RedirectAttributes redirectAttributes) {
        chatMessageService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("chatMessage.delete.success"));
        return "redirect:/chatMessages";
    }

}
