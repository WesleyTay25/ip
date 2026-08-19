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
        String[] tasks = new String[100];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i] + " 🏀");
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command + " 🏀");
            }
            System.out.println(separator);
        }
    }
}
