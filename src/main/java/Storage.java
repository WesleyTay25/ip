import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads the task list from, and writes it to, a text file on the hard disk.
 *
 * <p>Each task occupies one line, with fields separated by {@code " | "}:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | June 6th
 * E | 0 | project meeting | Aug 6th | 2-4pm
 * </pre>
 *
 * <p>The path is built with {@link Path#of(String, String...)} from separate
 * name components rather than a hard-coded string such as {@code "data/x.txt"},
 * so the correct separator is used on every operating system. The path is also
 * relative, so it resolves against whichever directory the program is run from
 * instead of pointing at one particular machine.
 */
public class Storage {
    private final Path filePath;

    /** Lines of the save file that could not be understood during the last load. */
    private final List<String> skippedLines = new ArrayList<>();

    /**
     * Creates storage backed by the given relative path.
     *
     * @param firstPathPart First name in the path, for example {@code "data"}.
     * @param remainingPathParts Remaining names, for example {@code "lebronjames.txt"}.
     */
    public Storage(String firstPathPart, String... remainingPathParts) {
        this.filePath = Path.of(firstPathPart, remainingPathParts);
    }

    /**
     * Loads the saved tasks.
     *
     * <p>A missing file (or missing folder) is not an error: it simply means
     * nothing has been saved yet, so an empty list is returned. Lines that are
     * not in the expected format are skipped rather than aborting the load, and
     * can be inspected afterwards with {@link #getSkippedLines()}.
     *
     * @return Tasks that were read successfully.
     * @throws LebronJamesException If the file exists but cannot be read.
     */
    public ArrayList<Task> load() throws LebronJamesException {
        skippedLines.clear();
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }
        if (!Files.isRegularFile(filePath)) {
            throw new LebronJamesException("Oops! " + filePath + " is not a file, so I could not load your tasks.");
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new LebronJamesException("Oops! I could not read " + filePath + ": " + exception.getMessage());
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (LebronJamesException exception) {
                skippedLines.add(line.strip());
            }
        }
        return tasks;
    }

    /**
     * Saves the given tasks, replacing any previously saved list.
     *
     * <p>The containing folder is created first if it does not exist yet, so the
     * chatbot works on a computer where it has never been run before.
     *
     * @param tasks Tasks to write, in list order.
     * @throws LebronJamesException If the tasks cannot be written to disk.
     */
    public void save(List<Task> tasks) throws LebronJamesException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileFormat());
        }

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new LebronJamesException(
                    "Oops! I could not save your tasks to " + filePath + ": " + exception.getMessage());
        }
    }

    /**
     * Returns the corrupted lines skipped by the most recent {@link #load()}.
     *
     * @return Unmodifiable view of the skipped lines, empty if the file was intact.
     */
    public List<String> getSkippedLines() {
        return Collections.unmodifiableList(skippedLines);
    }

    /**
     * Returns the path of the save file, for use in messages to the user.
     *
     * @return Relative path of the save file.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Converts one line of the save file back into a task.
     *
     * @param line Line to interpret.
     * @return Task described by the line.
     * @throws LebronJamesException If the line does not match the expected format.
     */
    private static Task parseTask(String line) throws LebronJamesException {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].strip();
        }

        if (fields.length < 3) {
            throw new LebronJamesException("Line has too few fields.");
        }

        String type = fields[0];
        boolean isDone = parseDoneFlag(fields[1]);
        String description = fields[2];
        if (description.isEmpty()) {
            throw new LebronJamesException("Line has an empty description.");
        }

        Task task;
        switch (type) {
        case Todo.FILE_TYPE:
            requireFieldCount(fields, 3);
            task = new Todo(description);
            break;
        case Deadline.FILE_TYPE:
            requireFieldCount(fields, 4);
            requireNonEmpty(fields[3]);
            task = new Deadline(description, fields[3]);
            break;
        case Event.FILE_TYPE:
            requireFieldCount(fields, 5);
            requireNonEmpty(fields[3]);
            requireNonEmpty(fields[4]);
            task = new Event(description, fields[3], fields[4]);
            break;
        default:
            throw new LebronJamesException("Unknown task type: " + type);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Interprets the saved completion flag.
     *
     * @param flag Field expected to be {@code 0} or {@code 1}.
     * @return Whether the task was completed.
     * @throws LebronJamesException If the flag is anything else.
     */
    private static boolean parseDoneFlag(String flag) throws LebronJamesException {
        if (flag.equals("1")) {
            return true;
        }
        if (flag.equals("0")) {
            return false;
        }
        throw new LebronJamesException("Completion flag must be 0 or 1 but was: " + flag);
    }

    /**
     * Checks that a line has exactly the number of fields its task type needs.
     *
     * @param fields Fields read from the line.
     * @param expectedCount Number of fields the task type requires.
     * @throws LebronJamesException If the count does not match.
     */
    private static void requireFieldCount(String[] fields, int expectedCount) throws LebronJamesException {
        if (fields.length != expectedCount) {
            throw new LebronJamesException(
                    "Expected " + expectedCount + " fields but found " + fields.length + ".");
        }
    }

    /**
     * Checks that a required date or time field was not left blank.
     *
     * @param field Field to check.
     * @throws LebronJamesException If the field is empty.
     */
    private static void requireNonEmpty(String field) throws LebronJamesException {
        if (field.isEmpty()) {
            throw new LebronJamesException("Line has an empty date or time field.");
        }
    }
}
