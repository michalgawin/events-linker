package pl.gdansk.eventslinker.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

	private static final CountryCodeMapper mapper = new CountryCodeMapper();

	@Override
	public void initialize(CountryCode constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Objects.nonNull(value) && mapper.apply(value).isPresent();
	}

}
