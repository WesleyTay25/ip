package lebronjames;

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

    public static void main(String[] args) {
        new LebronJames(DATA_FOLDER, DATA_FILE).run();
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
