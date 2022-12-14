package pl.gdansk.eventslinker.rest;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.collect.TreeMultimap;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import pl.gdansk.eventslinker.domain.CalendarEvent;
import pl.gdansk.eventslinker.domain.CalendarEventsResponse;
import pl.gdansk.eventslinker.domain.CalendarName;
import pl.gdansk.eventslinker.integration.calendar.google.GoogleCalendar;
import pl.gdansk.eventslinker.validator.CountryCode;
import pl.gdansk.eventslinker.validator.DateValidator;
import pl.gdansk.eventslinker.validator.ValidDate;

@RestController
@Validated
@RequestMapping(path = "/api",
		produces = "application/json")
public class EventController {

	private final GoogleCalendar googleCalendar;

	public EventController(GoogleCalendar googleCalendar) {
		this.googleCalendar = googleCalendar;
	}

	@Operation(summary = "Find event within the same date from two different countries")
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Event found",
					content = {
							@Content(mediaType = "application/json",
									schema = @Schema(implementation = CalendarEventsResponse.class)
							)
					}),
			@ApiResponse(
					responseCode = "404",
					description = "Event not found",
					content = @Content
			),
			@ApiResponse(
					responseCode = "503",
					description = "Cannot connect with calendar",
					content = @Content
			)
	})
	@GetMapping(path = "/events")
	public ResponseEntity<CalendarEventsResponse> getEvents(
			@ValidDate @RequestParam(value = "fromDate") String fromDate,
			@CountryCode @RequestParam(value = "cc1", defaultValue = "pl") String cc1,
			@CountryCode @RequestParam(value = "cc2", defaultValue = "at") String cc2) {
		final LocalDate oneDayAfter = DateValidator.parse(fromDate).get().plusDays(1);

		CalendarName calendar1 = CalendarName.valueOf(cc1.toUpperCase());
		CalendarName calendar2 = CalendarName.valueOf(cc2.toUpperCase());

		final CompletableFuture<TreeMultimap<LocalDate, CalendarEvent>> events1 = googleCalendar.getEvents(calendar1, oneDayAfter);
		final CompletableFuture<TreeMultimap<LocalDate, CalendarEvent>> events2 = googleCalendar.getEvents(calendar2, oneDayAfter);

		var combineEvents = this.createResponse(oneDayAfter);
		return events1.thenCombine(events2, combineEvents)
				.orTimeout(30, TimeUnit.SECONDS)
				.join();
	}

	private BiFunction<TreeMultimap<LocalDate, CalendarEvent>, TreeMultimap<LocalDate, CalendarEvent>,
			ResponseEntity<CalendarEventsResponse>> createResponse(LocalDate date) {
		return (holidays1, holidays2) -> {
			setCommonEvents(holidays1, holidays2);
			return getResponseEntity(holidays1, holidays2, date);
		};
	}

	private static ResponseEntity<CalendarEventsResponse> getResponseEntity(TreeMultimap<LocalDate, CalendarEvent> multimapForCc1,
			TreeMultimap<LocalDate, CalendarEvent> multimapForCc2,
			LocalDate date) {
		return multimapForCc1.asMap().entrySet().stream()
				.flatMap(entry -> entry.getValue().stream())
				.flatMap(event -> findSimilarCalendarEvent(event, multimapForCc2))
				.findFirst()
				.map(event -> ResponseEntity.ok().body(event))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find common events after " + date.toString()));
	}

	private static Stream<CalendarEventsResponse> findSimilarCalendarEvent(CalendarEvent calendarEvent, TreeMultimap<LocalDate, CalendarEvent> multimapForCc) {
		return multimapForCc.get(calendarEvent.getStartDate()).stream()
				.filter(ce -> calendarEvent.getEndDate().equals(ce.getEndDate()))
				.map(ce -> new CalendarEventsResponse(calendarEvent, ce));
	}

	private void setCommonEvents(TreeMultimap<LocalDate, CalendarEvent> multimapForCc1,
			TreeMultimap<LocalDate, CalendarEvent> multimapForCc2) {
		multimapForCc1.keySet().retainAll(multimapForCc2.keySet());
		multimapForCc2.keySet().retainAll(multimapForCc1.keySet());
	}

}
