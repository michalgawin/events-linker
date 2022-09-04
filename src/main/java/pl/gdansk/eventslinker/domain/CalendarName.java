package pl.gdansk.eventslinker.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CalendarName {
	PL("pl.polish#holiday@group.v.calendar.google.com"),
	AT("pl.austrian#holiday@group.v.calendar.google.com"),
	US("pl.usa#holiday@group.v.calendar.google.com"),
	UK("pl.uk#holiday@group.v.calendar.google.com");

	private static final Map<String, CalendarName> COUNTRY_CODE_TO_CALENDAR_MAP;

	static {
		COUNTRY_CODE_TO_CALENDAR_MAP = Arrays.stream(CalendarName.values())
				.collect(Collectors.toUnmodifiableMap(CalendarName::name, Function.identity()));
	}

	private final String country;

	CalendarName(String country) {
		this.country = country;
	}

	public String getCalendar() {
		return country;
	}

	public static Optional<CalendarName> of(String countryCode) {
		return Optional.ofNullable(COUNTRY_CODE_TO_CALENDAR_MAP.get(countryCode.toUpperCase()));
	}

}
