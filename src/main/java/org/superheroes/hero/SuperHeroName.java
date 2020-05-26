package org.superheroes.hero;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = SuperHeroName.Validator.class)
@Size(min = 1, max = 30)
@Pattern(regexp = "\\w+")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface SuperHeroName {

    String message() default "{org.superheroes.hero.SuperHeroName.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** dummy validator. the real action happens in the annotations above */
    class Validator implements ConstraintValidator<SuperHeroName, String> {
        @Override public boolean isValid(String value, ConstraintValidatorContext context) { return true; }
    }
}
