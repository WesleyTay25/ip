package lebronjames;

import java.time.LocalDate;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    /** Letter used to identify an event in the save file. */
    public static final String FILE_TYPE = "E";

    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Start date, and optionally time, of the event.
     * @param to End date, and optionally time, of the event.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event counts as occurring on a date if that date falls anywhere
     * between its start and end dates, so a multi-day event is listed on every
     * day it spans, not only on the day it begins.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    @Override
    public String toFileFormat() {
        return FILE_TYPE + " | " + getDoneFlag() + " | " + getDescription()
                + " | " + from.toFileFormat() + " | " + to.toFileFormat();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
