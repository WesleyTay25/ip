package lebronjames.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lebronjames.LebronJames;

/**
 * The JavaFX application itself: it builds the one window the chatbot uses.
 *
 * <p>The window's contents are described in {@code /view/MainWindow.fxml} rather
 * than built by hand here, so the layout can be changed without touching Java
 * code. This class only loads that file, hands the controller a chatbot to talk
 * to, and shows the result.
 *
 * <p>It is started by {@link lebronjames.Launcher} rather than having a
 * {@code main} of its own; see that class for why.
 */
public class Main extends Application {
    /** Smallest window that still shows a readable conversation. */
    private static final double MINIMUM_WIDTH = 417.0;
    private static final double MINIMUM_HEIGHT = 400.0;

    private final LebronJames lebronJames =
            new LebronJames(LebronJames.DATA_FOLDER, LebronJames.DATA_FILE);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Lebron James");
            stage.setMinWidth(MINIMUM_WIDTH);
            stage.setMinHeight(MINIMUM_HEIGHT);

            // The controller is created by the FXML loader, so the chatbot can
            // only be handed over after the file has been loaded.
            MainWindow controller = fxmlLoader.getController();
            controller.setLebronJames(lebronJames);
            controller.showWelcome();

            stage.show();
        } catch (IOException exception) {
            // A missing or broken FXML file is a packaging mistake, not
            // something the user can act on, so fail loudly during development
            // rather than opening an empty window.
            throw new IllegalStateException("Could not load the main window layout.", exception);
        }
    }
}
