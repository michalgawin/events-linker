package pl.gdansk.eventslinker.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarEventsResponse {

	@Getter
	private final LocalDate date;
	@Getter
	private final String name1;
	@Getter
	private final String name2;

	public CalendarEventsResponse(CalendarEvent calendarEvent1, CalendarEvent calendarEvent2) {
		this(calendarEvent1.getStartDate(), calendarEvent1.getSummary(), calendarEvent2.getSummary());
	}

}
