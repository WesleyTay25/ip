package lebronjames.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;
import lebronjames.task.Deadline;
import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;
import lebronjames.task.Todo;

/**
 * Tests the parts of {@link Ui} that make a decision rather than simply
 * printing: whether to mention loaded tasks at all, and whether a day is shown
 * as busy or free.
 *
 * <p>Ui writes to {@code System.out}, so each test swaps in a stream it can read
 * back, and puts the real one back afterwards. Numbering is checked too, since
 * the numbers printed by {@code list} are what the user types into
 * {@code mark} and {@code delete}.
 */
public class UiTest {
    private final PrintStream realOut = System.out;
    private ByteArrayOutputStream captured;

    @BeforeEach
    public void redirectSystemOut() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreSystemOut() {
        System.setOut(realOut);
    }

    /**
     * Returns everything printed since this test started.
     *
     * @return Captured output.
     */
    private String printed() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void showLoaded_noTasks_saysNothing() {
        // Nothing was restored, so there is nothing worth telling the user.
        new Ui().showLoaded(0);

        assertEquals("", printed());
    }

    @Test
    public void showLoaded_someTasks_reportsHowMany() {
        new Ui().showLoaded(3);

        assertTrue(printed().contains("3"), "should say how many tasks were loaded");
    }

    @Test
    public void showTaskList_severalTasks_numberedFromOne() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        new Ui().showTaskList(tasks);

        // These numbers are what the user types into mark and delete, so
        // starting at 1 is not cosmetic.
        assertTrue(printed().contains("1.[T][ ] first"));
        assertTrue(printed().contains("2.[T][ ] second"));
    }

    @Test
    public void showTaskList_emptyList_headingOnly() {
        new Ui().showTaskList(new ArrayList<>());

        assertEquals("Here are the tasks in your list:", printed().strip());
    }

    @Test
    public void showTasksOn_noTasks_saysTheDayIsFree() {
        new Ui().showTasksOn(new ArrayList<>(), LocalDate.of(2019, 12, 2));

        String output = printed();
        assertTrue(output.contains("Nothing scheduled"), "should say the day is free");
        assertTrue(output.contains("Dec 02 2019"), "should name the date asked about");
    }

    @Test
    public void showTasksOn_someTasks_listedAndNumberedFromOne() throws LebronJamesException {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-12-02")));

        new Ui().showTasksOn(tasks, LocalDate.of(2019, 12, 2));

        String output = printed();
        assertFalse(output.contains("Nothing scheduled"));
        assertTrue(output.contains("1.[D][ ] return book"));
    }

    @Test
    public void showTaskAdded_reportsTheTaskAndTheNewCount() {
        new Ui().showTaskAdded(new Todo("read book"), 5);

        String output = printed();
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("5"), "should report the new list size");
    }

    @Test
    public void showTaskRemoved_reportsTheTaskAndTheRemainingCount() {
        new Ui().showTaskRemoved(new Todo("read book"), 2);

        String output = printed();
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("2"));
    }

    @Test
    public void showError_printsTheMessageUnchanged() {
        new Ui().showError("Oops! something went wrong.");

        assertEquals("Oops! something went wrong.", printed().strip());
    }

    @Test
    public void showLoadingError_explainsTheListStartsEmpty() {
        new Ui().showLoadingError("Oops! I could not read the file.");

        String output = printed();
        assertTrue(output.contains("Oops! I could not read the file."));
        assertTrue(output.contains("empty list"), "should say what happens next");
    }

    @Test
    public void showLine_printsADivider() {
        new Ui().showLine();

        assertEquals("_".repeat(60), printed().strip());
    }
}
