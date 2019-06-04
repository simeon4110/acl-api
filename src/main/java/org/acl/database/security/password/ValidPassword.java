package org.acl.database.security.password;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Decorator to easily implement password validation to a DTO field.
 *
 * @author Josh Harkema
 */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid Password.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
