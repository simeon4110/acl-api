package com.sonnets.sonnet.tools;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("unused")
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid Password.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
