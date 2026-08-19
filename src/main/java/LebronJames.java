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
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);
            if (command.equals("bye")) {
                System.out.println("Goodbye, I love basketball btw! 🏀");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in you currently have:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i] + " 🏀");
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                Task task = tasks[taskNumber - 1];
                task.markAsDone();
                System.out.println("Nice one bro! This task is done:");
                System.out.println("  " + task + " 🏀");
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                Task task = tasks[taskNumber - 1];
                task.markAsNotDone();
                System.out.println("Oops this task is not done yet:");
                System.out.println("  " + task + " 🏀");
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("okay bro! task added: " + command + " 🏀");
            }
            System.out.println(separator);
        }
    }
}
