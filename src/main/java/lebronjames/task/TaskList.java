package lebronjames.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lebronjames.LebronJamesException;

/**
 * Holds the tasks the user is keeping track of, and the operations that change
 * or query that list.
 *
 * <p>This class wraps an {@code ArrayList<Task>} rather than extending it. That
 * keeps the chatbot's vocabulary small and deliberate: the rest of the program
 * can add, delete, and search tasks, but cannot reach in and, say, sort or
 * shuffle the list in ways the save file was never designed for.
 *
 * <p>It is also where a task number is turned into a task. Doing the range
 * check here means every command that names a task &mdash; mark, unmark,
 * delete &mdash; reports an out-of-range number the same way.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, typically the ones just
     * restored from the save file.
     *
     * @param tasks Tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task with the given one-based number.
     *
     * @param taskNumber Number of the task as shown by the list command.
     * @return Task that was removed.
     * @throws LebronJamesException If no task has that number.
     */
    public Task remove(int taskNumber) throws LebronJamesException {
        Task task = get(taskNumber);
        tasks.remove(taskNumber - 1);
        return task;
    }

    /**
     * Returns the task with the given one-based number.
     *
     * @param taskNumber Number of the task as shown by the list command.
     * @return Task with that number.
     * @throws LebronJamesException If no task has that number.
     */
    public Task get(int taskNumber) throws LebronJamesException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LebronJamesException(
                    "Oops! Task " + taskNumber + " does not exist. Enter a number shown by list.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns the tasks that fall on the given date.
     *
     * <p>To-dos are never included because they carry no date.
     *
     * @param date Date the user asked about.
     * @return Tasks scheduled on that date, in list order.
     */
    public List<Task> findTasksOn(LocalDate date) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOn(date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns all tasks, in list order, for displaying or saving.
     *
     * @return The tasks currently stored.
     */
    public List<Task> asList() {
        return tasks;
    }
}
