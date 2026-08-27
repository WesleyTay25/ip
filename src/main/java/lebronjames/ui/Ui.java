package lebronjames.ui;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;

/**
 * Deals with all interactions with the user: reading commands from the keyboard
 * and printing messages to the screen.
 *
 * <p>Gathering the input and output here means the rest of the chatbot never
 * calls {@code System.out} directly. Wording and layout can then be changed in
 * one place, and the task-handling classes stay free of display details.
 */
public class Ui {
    /** Horizontal rule printed above and below every reply. */
    private static final String SEPARATOR = "_".repeat(60);

    /** Basketball drawn at start-up. */
    private static final String BANNER = "       .-\"\"\"-.       \n"
            + "     .'  \\ | /  '.     \n"
            + "    /     \\|/     \\    \n"
            + "   ;-------+-------;   \n"
            + "    \\     /|\\     /    \n"
            + "     '.  / | \\  .'     \n"
            + "       '-...-'";

    /** Format used when echoing back the date asked about by the on command. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Reader for the user's commands.
     *
     * <p>The character set is stated explicitly rather than left to the
     * platform. {@link lebronjames.storage.Storage} always writes the save file
     * as UTF-8, so reading input as anything else would turn a description such
     * as "café run" into "cafÃ© run" on its way to disk.
     */
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    /**
     * Prints the greeting, the basketball banner, and the accepted task formats.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println("Hello! I'm Lebron James.");
        System.out.println("What can I do for you?");
        showTaskInstructions();
    }

    /**
     * Prints the accepted formats for adding each type of task.
     */
    public void showTaskInstructions() {
        System.out.println("Add tasks using one of these formats:");
        System.out.println("1. Todo: todo <task>");
        System.out.println("2. Deadline: deadline <task> /by <date>");
        System.out.println("3. Event: event <task> /from <date> /to <date>");
        System.out.println("Dates use " + TaskDateTime.ACCEPTED_FORMATS + ".");
        System.out.println("See what is scheduled for one day with: on <date>");
        System.out.println("Search your tasks with: find <keyword>");
    }

    /**
     * Prints the horizontal rule that separates one reply from the next.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether the user has typed another command.
     *
     * @return Whether more input is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command typed by the user.
     *
     * @return Line of input, exactly as typed.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints an error message, such as the explanation carried by a
     * {@link lebronjames.LebronJamesException LebronJamesException}.
     *
     * @param message Explanation to show the user.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Reports that saved tasks were restored, staying silent when there were none.
     *
     * @param taskCount Number of tasks restored from the save file.
     */
    public void showLoaded(int taskCount) {
        if (taskCount > 0) {
            System.out.println("I loaded " + taskCount + " saved task(s). Type list to see them.");
        }
    }

    /**
     * Reports that the save file could not be read and that the list starts empty.
     *
     * @param message Explanation of what went wrong.
     */
    public void showLoadingError(String message) {
        System.out.println(message);
        System.out.println("Starting with an empty list. Saving will overwrite that file.");
    }

    /**
     * Prints the whole task list, numbered from one.
     *
     * @param tasks Tasks currently stored.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Prints confirmation that a task was added and reports the new list size.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks currently stored.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task + " 🏀");
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints confirmation that a task was removed and reports the new list size.
     *
     * @param task Task that was removed.
     * @param taskCount Number of tasks still stored.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task + " 🏀");
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice one bro! This task is done:");
        System.out.println("  " + task + " 🏀");
    }

    /**
     * Prints confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("Oops this task is not done yet:");
        System.out.println("  " + task + " 🏀");
    }

    /**
     * Prints the tasks that fall on the given date, or a note that the day is free.
     *
     * @param tasks Tasks scheduled on that date.
     * @param date Date the user asked about.
     */
    public void showTasksOn(List<Task> tasks, LocalDate date) {
        if (tasks.isEmpty()) {
            System.out.println("Nothing scheduled on " + date.format(DISPLAY_DATE_FORMAT) + ". Enjoy the day off!");
            return;
        }

        System.out.println("Here is what you have on " + date.format(DISPLAY_DATE_FORMAT) + ":");
        showNumberedTasks(tasks);
    }

    /**
     * Prints the tasks that matched a search, or a note that none did.
     *
     * @param tasks Tasks whose descriptions contain the keyword.
     */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No matching tasks in your list. Try another keyword!");
            return;
        }

        System.out.println("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Prints the farewell message.
     */
    public void showGoodbye() {
        System.out.println("Goodbye, I love basketball btw! 🏀");
    }

    /**
     * Prints the given tasks as a numbered list starting at one.
     *
     * @param tasks Tasks to print, in list order.
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i) + " 🏀");
        }
    }
}
