/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Date or time by which the task must be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
