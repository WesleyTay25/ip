package lebronjames.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;

/**
 * Tests {@link Event}, whose {@code isOn} spans a range of dates rather than
 * matching a single one.
 */
public class EventTest {
    /**
     * Builds an event running between the two given dates or times.
     *
     * @param from Start, in any accepted format.
     * @param to End, in any accepted format.
     * @return Event under test.
     * @throws LebronJamesException If either date cannot be parsed.
     */
    private static Event eventFrom(String from, String to) throws LebronJamesException {
        return new Event("camp", TaskDateTime.parse(from), TaskDateTime.parse(to));
    }

    @Test
    public void toString_withTimes_bothEndsShown() throws LebronJamesException {
        assertEquals("[E][ ] camp (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm)",
                eventFrom("2019-08-06 1400", "2019-08-06 1600").toString());
    }

    @Test
    public void toString_datesOnly_noTimesShown() throws LebronJamesException {
        assertEquals("[E][ ] camp (from: Aug 06 2019 to: Aug 09 2019)",
                eventFrom("2019-08-06", "2019-08-09").toString());
    }

    @Test
    public void toFileFormat_fiveFields() throws LebronJamesException {
        assertEquals("E | 0 | camp | 2019-08-06 1400 | 2019-08-06 1600",
                eventFrom("2019-08-06 1400", "2019-08-06 1600").toFileFormat());
    }

    @Test
    public void toFileFormat_done_flagIsOne() throws LebronJamesException {
        Event event = eventFrom("2019-08-06", "2019-08-09");

        event.markAsDone();

        assertEquals("E | 1 | camp | 2019-08-06 | 2019-08-09", event.toFileFormat());
    }

    @Test
    public void isOn_everyDayItSpans_true() throws LebronJamesException {
        Event event = eventFrom("2019-08-06", "2019-08-09");

        // Both ends are inclusive, and every day in between counts too.
        assertTrue(event.isOn(LocalDate.of(2019, 8, 6)), "first day");
        assertTrue(event.isOn(LocalDate.of(2019, 8, 7)), "middle day");
        assertTrue(event.isOn(LocalDate.of(2019, 8, 9)), "last day");
    }

    @Test
    public void isOn_dayOutsideTheRange_false() throws LebronJamesException {
        Event event = eventFrom("2019-08-06", "2019-08-09");

        assertFalse(event.isOn(LocalDate.of(2019, 8, 5)), "day before");
        assertFalse(event.isOn(LocalDate.of(2019, 8, 10)), "day after");
    }

    @Test
    public void isOn_singleDayEvent_onlyThatDay() throws LebronJamesException {
        Event event = eventFrom("2019-08-06 1400", "2019-08-06 1600");

        assertTrue(event.isOn(LocalDate.of(2019, 8, 6)));
        assertFalse(event.isOn(LocalDate.of(2019, 8, 7)));
    }
}
