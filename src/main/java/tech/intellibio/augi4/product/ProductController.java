package tech.intellibio.augi4.product;

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
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.util.CustomCollectors;
import tech.intellibio.augi4.util.ReferencedWarning;
import tech.intellibio.augi4.util.WebUtils;


@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final PromptRepository promptRepository;

    public ProductController(final ProductService productService,
            final PromptRepository promptRepository) {
        this.productService = productService;
        this.promptRepository = promptRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("promptValues", promptRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Prompt::getId, Prompt::getId)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final Page<ProductDTO> products = productService.findAll(filter, pageable);
        model.addAttribute("products", products);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(products));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("product") final ProductDTO productDTO) {
        return "product/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("product") @Valid final ProductDTO productDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "product/add";
        }
        productService.create(productDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("product.create.success"));
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("product", productService.get(id));
        return "product/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("product") @Valid final ProductDTO productDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "product/edit";
        }
        productService.update(id, productDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("product.update.success"));
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = productService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            productService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("product.delete.success"));
        }
        return "redirect:/products";
    }

}
