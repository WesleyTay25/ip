package lebronjames;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import lebronjames.command.Command;
import lebronjames.parser.Parser;
import lebronjames.storage.Storage;
import lebronjames.task.Task;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Runs the Lebron James chatbot application.
 *
 * <p>An instance holds the three collaborators the chatbot needs: a {@link Ui}
 * to talk to the user, a {@link Storage} to remember tasks between runs, and a
 * {@link TaskList} holding the tasks themselves.
 *
 * <p>This class no longer knows what any individual command does. It reads a
 * line, asks {@link Parser} what it means, and tells the resulting
 * {@link Command} to execute itself, which keeps the loop short enough to read
 * at a glance.
 */
public class LebronJames {
    /**
     * Folder and file name of the save file, relative to the project root.
     * Kept as separate parts so {@link Storage} can join them with the
     * separator that suits the current operating system.
     */
    private static final String DATA_FOLDER = "data";
    private static final String DATA_FILE = "lebronjames.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates a chatbot that saves to, and loads from, the given file.
     *
     * <p>The greeting is printed before loading so that any problem with the
     * save file is reported as part of the start-up message.
     *
     * @param firstPathPart First name in the path to the save file.
     * @param remainingPathParts Remaining names in the path to the save file.
     */
    public LebronJames(String firstPathPart, String... remainingPathParts) {
        this.ui = new Ui();
        this.storage = new Storage(firstPathPart, remainingPathParts);
        ui.showWelcome();
        this.tasks = loadTasks(storage, ui);
        ui.showLine();
    }

    /**
     * Starts the chatbot, saving to and loading from the default data file.
     *
     * @param args Command line arguments, which the chatbot does not use.
     */
    public static void main(String[] args) {
        useUtf8Output();
        new LebronJames(DATA_FOLDER, DATA_FILE).run();
    }

    /**
     * Makes printed output UTF-8 regardless of the machine the app is run on.
     *
     * <p>By default System.out encodes using whatever character set the console
     * reports, so on a console that is not UTF-8 the basketball in every reply
     * arrives as a question mark. Replacing the stream once, here, keeps every
     * message readable without {@link lebronjames.ui.Ui} having to know
     * anything about character sets.
     *
     * <p>This is done in main rather than inside Ui so that tests can still
     * capture output with System.setOut in the ordinary way.
     */
    private static void useUtf8Output() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
    }

    /**
     * Reads and carries out commands until the user says bye or the input ends.
     */
    public void run() {
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (LebronJamesException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
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
}
