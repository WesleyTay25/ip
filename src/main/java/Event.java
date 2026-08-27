/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    /** Letter used to identify an event in the save file. */
    public static final String FILE_TYPE = "E";

    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Start date or time of the event.
     * @param to End date or time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return FILE_TYPE + " | " + getDoneFlag() + " | " + getDescription()
                + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
