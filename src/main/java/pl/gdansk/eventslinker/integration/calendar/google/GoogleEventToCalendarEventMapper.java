package pl.gdansk.eventslinker.integration.calendar.google;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Function;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.common.collect.TreeMultimap;

import pl.gdansk.eventslinker.domain.CalendarEvent;

public class GoogleEventToCalendarEventMapper implements Function<List<Event>, TreeMultimap<LocalDate, CalendarEvent>> {

	@Override
	public TreeMultimap<LocalDate, CalendarEvent> apply(List<Event> events) {
		return events.stream()
				.map(this::toCalendarEvent)
				.collect(TreeMultimap::create, (acc, e) -> acc.put(e.getStartDate(), e), (c1, c2) -> c1.putAll(c2));
	}

	public static LocalDate convertToLocalDate(DateTime date) {
		return Instant.ofEpochMilli(date.getValue()).atZone(ZoneOffset.UTC).toLocalDate();
	}

	public static DateTime convertToDateTime(LocalDate date) {
		return new DateTime(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
	}

	private CalendarEvent toCalendarEvent(Event event) {
		return new CalendarEvent(event.getSummary(),
				convertToLocalDate(event.getStart().getDate()),
				convertToLocalDate(event.getEnd().getDate()));
	}

}
