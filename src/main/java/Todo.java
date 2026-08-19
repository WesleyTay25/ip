/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
