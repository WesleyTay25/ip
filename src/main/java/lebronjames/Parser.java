package lebronjames;

/**
 * Makes sense of the text the user types, turning it into a {@link Command}.
 *
 * <p>All the string handling lives here: finding the command word, splitting a
 * deadline at {@code /by}, checking that a task number is a whole number, and
 * complaining when any of that fails. The command classes therefore receive
 * ready-made values such as a {@link Task} or an {@code int}, and never have to
 * look at raw input.
 *
 * <p>The methods are static because parsing depends only on its input; a Parser
 * object would have nothing to remember between calls.
 */
public class Parser {
    /** Character reserved as the field separator inside the save file. */
    private static final String RESERVED_CHARACTER = "|";

    /**
     * Understands one line of user input.
     *
     * @param fullCommand Line typed by the user.
     * @return Command described by that line.
     * @throws LebronJamesException If the line is blank, unrecognised, or uses a
     *     recognised command word in the wrong format.
     */
    public static Command parse(String fullCommand) throws LebronJamesException {
        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        }

        // The save file uses '|' to separate fields, so a task containing one
        // could not be read back correctly. Rejecting it early keeps the file valid.
        if (fullCommand.contains(RESERVED_CHARACTER)) {
            throw new LebronJamesException(
                    "Oops! '|' is reserved for saving tasks, so it cannot be used in a command.");
        }

        if (fullCommand.equals("list")) {
            return new ListCommand();
        }
        if (isCommand(fullCommand, "mark")) {
            return new MarkCommand(parseTaskNumber(fullCommand, "mark"), true);
        }
        if (isCommand(fullCommand, "unmark")) {
            return new MarkCommand(parseTaskNumber(fullCommand, "unmark"), false);
        }
        if (isCommand(fullCommand, "delete")) {
            return new DeleteCommand(parseTaskNumber(fullCommand, "delete"));
        }
        if (isCommand(fullCommand, "todo")) {
            return new AddCommand(parseTodo(fullCommand));
        }
        if (isCommand(fullCommand, "deadline")) {
            return new AddCommand(parseDeadline(fullCommand));
        }
        if (isCommand(fullCommand, "event")) {
            return new AddCommand(parseEvent(fullCommand));
        }
        if (isCommand(fullCommand, "on")) {
            return parseOn(fullCommand);
        }
        if (fullCommand.isBlank()) {
            throw new LebronJamesException("Oops! Please enter a command.");
        }
        throw new LebronJamesException("Sorry, I don't recognise that command.\n"
                + "Please categorise tasks as todo, deadline, or event using the formats above.");
    }

    /**
     * Returns whether the input starts with the given command word.
     *
     * <p>The word must stand alone or be followed by a space, so that
     * {@code "todolist"} is not mistaken for a {@code todo} command.
     *
     * @param fullCommand Line typed by the user.
     * @param commandName Command word to look for.
     * @return Whether the line uses that command.
     */
    private static boolean isCommand(String fullCommand, String commandName) {
        return fullCommand.equals(commandName) || fullCommand.startsWith(commandName + " ");
    }

    /**
     * Builds the to-do described by a {@code todo} command.
     *
     * @param fullCommand Line typed by the user.
     * @return To-do to add.
     * @throws LebronJamesException If no description was given.
     */
    private static Todo parseTodo(String fullCommand) throws LebronJamesException {
        String description = fullCommand.length() > 5 ? fullCommand.substring(5).trim() : "";
        if (description.isEmpty()) {
            throw new LebronJamesException("Oops! A todo needs a description. Try: todo <task>");
        }
        return new Todo(description);
    }

    /**
     * Builds the deadline described by a {@code deadline} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Deadline to add.
     * @throws LebronJamesException If {@code /by} is missing, or either part is empty.
     */
    private static Deadline parseDeadline(String fullCommand) throws LebronJamesException {
        int byIndex = fullCommand.indexOf(" /by ");
        if (byIndex == -1) {
            throw new LebronJamesException("Oops! Use this deadline format: deadline <task> /by <deadline>");
        }

        String description = byIndex < 9 ? "" : fullCommand.substring(9, byIndex).trim();
        String by = fullCommand.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new LebronJamesException("Oops! A deadline needs a task description.");
        }
        if (by.isEmpty()) {
            throw new LebronJamesException("Oops! A deadline needs a date or time after /by.");
        }
        return new Deadline(description, TaskDateTime.parse(by));
    }

    /**
     * Builds the event described by an {@code event} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Event to add.
     * @throws LebronJamesException If {@code /from} or {@code /to} is missing, or any part is empty.
     */
    private static Event parseEvent(String fullCommand) throws LebronJamesException {
        int fromIndex = fullCommand.indexOf(" /from ");
        int toIndex = fromIndex == -1 ? -1 : fullCommand.indexOf(" /to ", fromIndex + 7);
        if (fromIndex == -1 || toIndex == -1) {
            throw new LebronJamesException("Oops! Use this event format: event <task> /from <start> /to <end>");
        }

        String description = fromIndex < 6 ? "" : fullCommand.substring(6, fromIndex).trim();
        String from = fullCommand.substring(fromIndex + 7, toIndex).trim();
        String to = fullCommand.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new LebronJamesException("Oops! An event needs a description.");
        }
        if (from.isEmpty()) {
            throw new LebronJamesException("Oops! An event needs a start date or time after /from.");
        }
        if (to.isEmpty()) {
            throw new LebronJamesException("Oops! An event needs an end date or time after /to.");
        }
        return new Event(description, TaskDateTime.parse(from), TaskDateTime.parse(to));
    }

    /**
     * Builds the command described by an {@code on} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Command that reports what is scheduled on the given date.
     * @throws LebronJamesException If the date is missing or not in an accepted format.
     */
    private static OnCommand parseOn(String fullCommand) throws LebronJamesException {
        String dateText = fullCommand.substring(2).trim();
        if (dateText.isEmpty()) {
            throw new LebronJamesException("Oops! The on command needs a date. Try: on 2019-12-02");
        }
        return new OnCommand(TaskDateTime.parse(dateText).getDate());
    }

    /**
     * Extracts the one-based task number supplied with a command.
     *
     * <p>Only the text itself is checked here. Whether a task with that number
     * actually exists is decided by {@link TaskList}, which is the object that
     * knows how long the list is.
     *
     * @param fullCommand Line typed by the user.
     * @param commandName Command word preceding the task number.
     * @return Task number as typed by the user, counting from one.
     * @throws LebronJamesException If the task number is missing or not a whole number.
     */
    private static int parseTaskNumber(String fullCommand, String commandName) throws LebronJamesException {
        String numberText = fullCommand.substring(commandName.length()).trim();
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
