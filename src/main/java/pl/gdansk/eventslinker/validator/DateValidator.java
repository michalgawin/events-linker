package pl.gdansk.eventslinker.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.vavr.control.Try;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void initialize(ValidDate constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Objects.nonNull(value) && parse(value).isSuccess();
	}

	public static Try<LocalDate> parse(String value) {
		return Try.of(() -> LocalDate.parse(value, FORMATTER));
	}

}
