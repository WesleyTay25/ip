import java.util.Scanner;

/**
 * Starts the Lebron James chatbot application.
 */
public class LebronJames {
    public static void main(String[] args) {
        String separator = "_".repeat(60);
        String banner = "       .-\"\"\"-.       \n"
                + "     .'  \\ | /  '.     \n"
                + "    /     \\|/     \\    \n"
                + "   ;-------+-------;   \n"
                + "    \\     /|\\     /    \n"
                + "     '.  / | \\  .'     \n"
                + "       '-...-'";

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Lebron James.");
        System.out.println("What can I do for you?");
        printTaskInstructions();
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);
            try {
                if (command.equals("bye")) {
                    System.out.println("Goodbye, I love basketball btw! 🏀");
                    System.out.println(separator);
                    break;
                }

                if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i] + " 🏀");
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(command, "mark", taskCount);
                    Task task = tasks[taskIndex];
                    task.markAsDone();
                    System.out.println("Nice one bro! This task is done:");
                    System.out.println("  " + task + " 🏀");
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(command, "unmark", taskCount);
                    Task task = tasks[taskIndex];
                    task.markAsNotDone();
                    System.out.println("Oops this task is not done yet:");
                    System.out.println("  " + task + " 🏀");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.length() > 5 ? command.substring(5).trim() : "";
                    if (description.isEmpty()) {
                        throw new LebronJamesException("Oops! A todo needs a description. Try: todo <task>");
                    } else {
                        ensureTaskListHasSpace(taskCount, tasks.length);
                        Task task = new Todo(description);
                        tasks[taskCount] = task;
                        taskCount++;
                        printTaskAdded(task, taskCount);
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
                            ensureTaskListHasSpace(taskCount, tasks.length);
                            Task task = new Deadline(description, by);
                            tasks[taskCount] = task;
                            taskCount++;
                            printTaskAdded(task, taskCount);
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
                            ensureTaskListHasSpace(taskCount, tasks.length);
                            Task task = new Event(description, from, to);
                            tasks[taskCount] = task;
                            taskCount++;
                            printTaskAdded(task, taskCount);
                        }
                    }
                } else if (command.isBlank()) {
                    throw new LebronJamesException("Oops! Please enter a command.");
                } else {
                    throw new LebronJamesException("Sorry, I don't recognise that command.\n"
                            + "Please categorise tasks as todo, deadline, or event using the formats above.");
                }
            } catch (LebronJamesException exception) {
                System.out.println(exception.getMessage());
            }
            System.out.println(separator);
        }
    }

    /**
     * Prints the accepted formats for adding each type of task.
     */
    private static void printTaskInstructions() {
        System.out.println("Add tasks using one of these formats:");
        System.out.println("1. Todo: todo <task>");
        System.out.println("2. Deadline: deadline <task> /by <deadline>");
        System.out.println("3. Event: event <task> /from <start> /to <end>");
    }

    /**
     * Extracts and validates the one-based task number supplied with a command.
     *
     * @param command Full command entered by the user.
     * @param commandName Command word preceding the task number.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based task index.
     * @throws LebronJamesException If the task number is missing, non-numeric, or out of range.
     */
    private static int parseTaskIndex(String command, String commandName, int taskCount)
            throws LebronJamesException {
        String numberText = command.substring(commandName.length()).trim();
        if (numberText.isEmpty()) {
            throw new LebronJamesException("Oops! The " + commandName + " command needs a task number.");
        }

        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new LebronJamesException(
                        "Oops! Task " + taskNumber + " does not exist. Enter a number shown by list.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new LebronJamesException(
                    "Oops! The " + commandName + " command needs a whole-number task number.");
        }
    }

    /**
     * Ensures another task can be stored in the fixed-size task list.
     *
     * @param taskCount Number of tasks currently stored.
     * @param capacity Maximum number of tasks that can be stored.
     * @throws LebronJamesException If the task list is already full.
     */
    private static void ensureTaskListHasSpace(int taskCount, int capacity) throws LebronJamesException {
        if (taskCount >= capacity) {
            throw new LebronJamesException("Oops! Your task list is full, so I cannot add another task.");
        }
    }

    /**
     * Prints confirmation that a task was added and reports the new list size.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks currently stored.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task + " 🏀");
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
