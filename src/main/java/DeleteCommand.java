/**
 * Removes a task from the list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that removes the task with the given number.
     *
     * @param taskNumber Number of the task as shown by the list command.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LebronJamesException {
        Task removedTask = tasks.remove(taskNumber);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.asList());
    }
}
