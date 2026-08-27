import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Starts the Lebron James chatbot application.
 */
public class LebronJames {
    /**
     * Folder and file name of the save file, relative to the project root.
     * Kept as separate parts so {@link Storage} can join them with the
     * separator that suits the current operating system.
     */
    private static final String DATA_FOLDER = "data";
    private static final String DATA_FILE = "lebronjames.txt";

    /** Character reserved as the field separator inside the save file. */
    private static final String RESERVED_CHARACTER = "|";

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage(DATA_FOLDER, DATA_FILE);
        TaskList tasks = loadTasks(storage, ui);
        ui.showLine();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            ui.showLine();
            try {
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    ui.showLine();
                    break;
                }

                // The save file uses '|' to separate fields, so a task containing one
                // could not be read back correctly. Rejecting it early keeps the file valid.
                if (command.contains(RESERVED_CHARACTER)) {
                    throw new LebronJamesException(
                            "Oops! '|' is reserved for saving tasks, so it cannot be used in a command.");
                }

                // Set to true by any branch that changes the task list, so the list
                // is written to disk exactly once per command.
                boolean hasTaskListChanged = false;

                if (command.equals("list")) {
                    ui.showTaskList(tasks.asList());
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    Task task = tasks.get(parseTaskNumber(command, "mark"));
                    task.markAsDone();
                    hasTaskListChanged = true;
                    ui.showTaskMarked(task);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    Task task = tasks.get(parseTaskNumber(command, "unmark"));
                    task.markAsNotDone();
                    hasTaskListChanged = true;
                    ui.showTaskUnmarked(task);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    Task removedTask = tasks.remove(parseTaskNumber(command, "delete"));
                    hasTaskListChanged = true;
                    ui.showTaskRemoved(removedTask, tasks.size());
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.length() > 5 ? command.substring(5).trim() : "";
                    if (description.isEmpty()) {
                        throw new LebronJamesException("Oops! A todo needs a description. Try: todo <task>");
                    } else {
                        Task task = new Todo(description);
                        tasks.add(task);
                        hasTaskListChanged = true;
                        ui.showTaskAdded(task, tasks.size());
                    }
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    int byIndex = command.indexOf(" /by ");
                    if (byIndex == -1) {
                        throw new LebronJamesException(
                                "Oops! Use this deadline format: deadline <task> /by <deadline>");
                    } else {
                        String description = byIndex < 9 ? "" : command.substring(9, byIndex).trim();
                        String by = command.substring(byIndex + 5).trim();
                        if (description.isEmpty()) {
                            throw new LebronJamesException("Oops! A deadline needs a task description.");
                        } else if (by.isEmpty()) {
                            throw new LebronJamesException("Oops! A deadline needs a date or time after /by.");
                        } else {
                            Task task = new Deadline(description, TaskDateTime.parse(by));
                            tasks.add(task);
                            hasTaskListChanged = true;
                            ui.showTaskAdded(task, tasks.size());
                        }
                    }
                } else if (command.equals("event") || command.startsWith("event ")) {
                    int fromIndex = command.indexOf(" /from ");
                    int toIndex = fromIndex == -1 ? -1 : command.indexOf(" /to ", fromIndex + 7);
                    if (fromIndex == -1 || toIndex == -1) {
                        throw new LebronJamesException(
                                "Oops! Use this event format: event <task> /from <start> /to <end>");
                    } else {
                        String description = fromIndex < 6 ? "" : command.substring(6, fromIndex).trim();
                        String from = command.substring(fromIndex + 7, toIndex).trim();
                        String to = command.substring(toIndex + 5).trim();
                        if (description.isEmpty()) {
                            throw new LebronJamesException("Oops! An event needs a description.");
                        } else if (from.isEmpty()) {
                            throw new LebronJamesException("Oops! An event needs a start date or time after /from.");
                        } else if (to.isEmpty()) {
                            throw new LebronJamesException("Oops! An event needs an end date or time after /to.");
                        } else {
                            Task task = new Event(description,
                                    TaskDateTime.parse(from), TaskDateTime.parse(to));
                            tasks.add(task);
                            hasTaskListChanged = true;
                            ui.showTaskAdded(task, tasks.size());
                        }
                    }
                } else if (command.equals("on") || command.startsWith("on ")) {
                    String dateText = command.substring(2).trim();
                    if (dateText.isEmpty()) {
                        throw new LebronJamesException("Oops! The on command needs a date. Try: on 2019-12-02");
                    }
                    LocalDate date = TaskDateTime.parse(dateText).getDate();
                    ui.showTasksOn(tasks.findTasksOn(date), date);
                } else if (command.isBlank()) {
                    throw new LebronJamesException("Oops! Please enter a command.");
                } else {
                    throw new LebronJamesException("Sorry, I don't recognise that command.\n"
                            + "Please categorise tasks as todo, deadline, or event using the formats above.");
                }

                if (hasTaskListChanged) {
                    storage.save(tasks.asList());
                }
            } catch (LebronJamesException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Loads the saved tasks and reports anything that went wrong.
     *
     * <p>Startup never fails because of the save file: if it cannot be read at
     * all, the chatbot warns the user and begins with an empty list.
     *
     * @param storage Storage to load from.
     * @param ui User interface used to report the outcome.
     * @return Tasks restored from disk, or an empty list if none could be read.
     */
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            ArrayList<Task> savedTasks = storage.load();
            ui.showLoaded(savedTasks.size());
            return new TaskList(savedTasks);
        } catch (LebronJamesException exception) {
            ui.showLoadingError(exception.getMessage());
            return new TaskList();
        }
    }

    /**
     * Extracts the one-based task number supplied with a command.
     *
     * <p>Only the text itself is checked here. Whether a task with that number
     * actually exists is decided by {@link TaskList}, which is the object that
     * knows how long the list is.
     *
     * @param command Full command entered by the user.
     * @param commandName Command word preceding the task number.
     * @return Task number as typed by the user, counting from one.
     * @throws LebronJamesException If the task number is missing or not a whole number.
     */
    private static int parseTaskNumber(String command, String commandName) throws LebronJamesException {
        String numberText = command.substring(commandName.length()).trim();
        if (numberText.isEmpty()) {
            throw new LebronJamesException("Oops! The " + commandName + " command needs a task number.");
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new LebronJamesException(
                    "Oops! The " + commandName + " command needs a whole-number task number.");
        }
    }
}
