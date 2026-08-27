package lebronjames.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}, and through it the completion handling that {@link Task}
 * provides to every kind of task.
 */
public class TodoTest {
    @Test
    public void toString_newTodo_shownAsNotDone() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_afterMarkAsDone_shownAsDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_previouslyDone_backToNotDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void markAsDone_calledTwice_stillDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void toFileFormat_notDone_threeFieldsWithZeroFlag() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_done_flagIsOne() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void isOn_anyDate_false() {
        // A to-do carries no date, so it belongs to no particular day.
        assertFalse(new Todo("read book").isOn(LocalDate.of(2019, 12, 2)));
    }
}
