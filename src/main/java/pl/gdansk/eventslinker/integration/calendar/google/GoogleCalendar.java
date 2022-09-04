package pl.gdansk.eventslinker.integration.calendar.google;

import static pl.gdansk.eventslinker.integration.calendar.google.GoogleEventToCalendarEventMapper.convertToDateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import com.google.common.base.Strings;
import com.google.common.collect.TreeMultimap;

import pl.gdansk.eventslinker.domain.CalendarEvent;
import pl.gdansk.eventslinker.domain.CalendarName;
import pl.gdansk.eventslinker.security.TokenProperties;

@Service
public class GoogleCalendar {

	private static final String APPLICATION_NAME = "events-linker";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final int MAX_RESULTS = 100;
	private static final ResponseStatusException SERVICE_UNAVAILABLE_EXCEPTION =
			new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cannot connect to calendar service");

	private final TokenProperties tokenProperties;
	private final Calendar service;

	public GoogleCalendar(TokenProperties tokenProperties) throws GeneralSecurityException, IOException {
		this.tokenProperties = tokenProperties;
		this.service = getCalendarService();
	}

	@Async("asyncExecutor")
	public CompletableFuture<TreeMultimap<LocalDate, CalendarEvent>> getEvents(CalendarName calendar, LocalDate startDate) {
		return CompletableFuture.supplyAsync(() -> fetchEvents(calendar, convertToDateTime(startDate)));
	}

	public TreeMultimap<LocalDate, CalendarEvent> fetchEvents(CalendarName calendar, DateTime startDate) {
		try {
			return fetchEvents(calendar, startDate, null);
		} catch (IOException e) {
			throw SERVICE_UNAVAILABLE_EXCEPTION;
		}
	}

	private TreeMultimap<LocalDate, CalendarEvent> fetchEvents(CalendarName calendar, DateTime startDate, String pageToken)
			throws IOException {
		final GoogleEventToCalendarEventMapper eventMapper = new GoogleEventToCalendarEventMapper();

		TreeMultimap<LocalDate, CalendarEvent> events = TreeMultimap.create();

		do {
			Events eventList = getEvents(calendar, startDate, pageToken);
			pageToken = eventList.getNextPageToken();

			Logger.getGlobal().info("Events: " + eventList.getItems());
			events.putAll(eventMapper.apply(eventList.getItems()));
		} while (!Strings.isNullOrEmpty(pageToken));

		return events;
	}

	private Events getEvents(CalendarName calendar, DateTime startDate, String pageToken) throws IOException {
		return service.events()
				.list(calendar.getCalendar())
				.setPageToken(pageToken)
				.setKey(tokenProperties.getToken())
				.setTimeZone("Europe/Warsaw")
				.setTimeMin(startDate)
				.setMaxResults(MAX_RESULTS)
				.execute();
	}

	private static Calendar getCalendarService() throws GeneralSecurityException, IOException {
		return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

}
