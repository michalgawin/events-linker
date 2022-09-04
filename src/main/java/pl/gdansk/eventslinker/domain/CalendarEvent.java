package pl.gdansk.eventslinker.domain;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class CalendarEvent implements Comparable<CalendarEvent> {

	@Getter
	private final String summary;
	@Getter
	private final LocalDate startDate;
	@Getter
	private final LocalDate endDate;

	@Override
	public int compareTo(CalendarEvent o) {
		return this.getStartDate().compareTo(o.getStartDate());
	}

}
