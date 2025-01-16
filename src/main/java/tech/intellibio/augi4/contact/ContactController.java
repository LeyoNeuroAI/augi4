package tech.intellibio.augi4.contact;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
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
import tech.intellibio.augi4.util.JsonStringFormatter;
import tech.intellibio.augi4.util.WebUtils;


@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;
    private final ObjectMapper objectMapper;

    public ContactController(final ContactService contactService, final ObjectMapper objectMapper) {
        this.contactService = contactService;
        this.objectMapper = objectMapper;
    }

    @InitBinder
    public void jsonFormatting(final WebDataBinder binder) {
        binder.addCustomFormatter(new JsonStringFormatter<List<String>>(objectMapper) {
        }, "subject");
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("contacts", contactService.findAll());
        return "contact/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("contact") final ContactDTO contactDTO) {
        return "contact/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("contact") @Valid final ContactDTO contactDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact/add";
        }
        contactService.create(contactDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contact.create.success"));
        return "redirect:/contacts";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("contact", contactService.get(id));
        return "contact/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("contact") @Valid final ContactDTO contactDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact/edit";
        }
        contactService.update(id, contactDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contact.update.success"));
        return "redirect:/contacts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        contactService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("contact.delete.success"));
        return "redirect:/contacts";
    }

}
