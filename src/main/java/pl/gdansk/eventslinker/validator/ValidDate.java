package pl.gdansk.eventslinker.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = DateValidator.class)
@Target( { ElementType.FIELD, ElementType.PARAMETER } )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {

	String message() default "Use valid date format (yyyy-MM-dd)";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
