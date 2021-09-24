package com.stocktrader.market.model.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValuesArePositiveValidator.class)
@Documented
public @interface ValuesArePositiveConstraint {
    String message() default "This map cannot have negative values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
