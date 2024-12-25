package tech.intellibio.augi4.program;

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
        validatedBy = ProgramPromptUnique.ProgramPromptUniqueValidator.class
)
public @interface ProgramPromptUnique {

    String message() default "{Exists.program.prompt}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ProgramPromptUniqueValidator implements ConstraintValidator<ProgramPromptUnique, Long> {

        private final ProgramService programService;
        private final HttpServletRequest request;

        public ProgramPromptUniqueValidator(final ProgramService programService,
                final HttpServletRequest request) {
            this.programService = programService;
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
            if (currentId != null && value.equals(programService.get(Long.parseLong(currentId)).getPrompt())) {
                // value hasn't changed
                return true;
            }
            return !programService.promptExists(value);
        }

    }

}
