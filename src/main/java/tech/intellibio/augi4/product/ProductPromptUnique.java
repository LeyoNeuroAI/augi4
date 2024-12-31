package tech.intellibio.augi4.product;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the id value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ProductPromptUnique.ProductPromptUniqueValidator.class
)
public @interface ProductPromptUnique {

    String message() default "{Exists.product.prompt}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ProductPromptUniqueValidator implements ConstraintValidator<ProductPromptUnique, Long> {

        private final ProductService productService;
        private final HttpServletRequest request;

        public ProductPromptUniqueValidator(final ProductService productService,
                final HttpServletRequest request) {
            this.productService = productService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Long value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(productService.get(Long.parseLong(currentId)).getPrompt())) {
                // value hasn't changed
                return true;
            }
            return !productService.promptExists(value);
        }

    }

}
