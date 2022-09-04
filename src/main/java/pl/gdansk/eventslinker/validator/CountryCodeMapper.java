package pl.gdansk.eventslinker.validator;

import java.util.Optional;
import java.util.function.Function;

import pl.gdansk.eventslinker.domain.CalendarName;

public class CountryCodeMapper implements Function<String, Optional<CalendarName>> {

	@Override
	public Optional<CalendarName> apply(String code) {
		return CalendarName.of(code.toUpperCase());
	}

}
