package lebronjames.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One speech bubble in the conversation: a picture of the speaker beside what
 * they said.
 *
 * <p>This is a custom control, meaning it loads its own layout
 * ({@code /view/DialogBox.fxml}) and acts as that layout's controller. Doing it
 * this way lets {@link MainWindow} treat a whole bubble as a single node it can
 * drop into the conversation, instead of assembling a label and an image every
 * time someone speaks.
 *
 * <p>Instances are made through {@link #getUserDialog} and
 * {@link #getLebronDialog} rather than a public constructor, so a caller cannot
 * forget to flip a reply round to face the other way.
 */
public class DialogBox extends HBox {
    /**
     * Diameter of the round speaker picture, in pixels.
     *
     * <p>The size is set from here rather than in the FXML so the picture and
     * the circle clipping it can never disagree: a clip smaller than the image
     * would slice its edges off, and a larger one would leave the corners
     * showing.
     */
    private static final double PICTURE_SIZE = 100.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a bubble showing the given text next to the given picture.
     *
     * @param text What the speaker said.
     * @param image Picture of the speaker.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the dialog box layout.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setViewport(squareCentreOf(image));
        displayPicture.setFitWidth(PICTURE_SIZE);
        displayPicture.setFitHeight(PICTURE_SIZE);
        // A circular clip turns the square viewport into a round avatar, which
        // reads as a chat app without needing the images themselves to be round.
        displayPicture.setClip(new Circle(PICTURE_SIZE / 2, PICTURE_SIZE / 2, PICTURE_SIZE / 2));
    }

    /**
     * Returns a bubble for something the user typed, shown on the right.
     *
     * @param text What the user typed.
     * @param image Picture of the user.
     * @return Bubble ready to be added to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a bubble for one of the chatbot's replies, shown on the left.
     *
     * @param text The chatbot's reply.
     * @param image Picture of the chatbot.
     * @return Bubble ready to be added to the conversation.
     */
    public static DialogBox getLebronDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns the largest square that fits in the middle of the given picture.
     *
     * <p>Showing a picture that is not square would otherwise go wrong twice
     * over: fitting it inside the avatar box letterboxes it, and the round clip
     * then keeps only a thin band across the middle of that. Cropping to a
     * square first means a photo of any shape fills the circle, and taking the
     * centre is what keeps the subject in frame.
     *
     * @param image Picture about to be shown.
     * @return Square region of the picture to display.
     */
    private static Rectangle2D squareCentreOf(Image image) {
        double side = Math.min(image.getWidth(), image.getHeight());
        double left = (image.getWidth() - side) / 2;
        double top = (image.getHeight() - side) / 2;
        return new Rectangle2D(left, top, side, side);
    }

    /**
     * Mirrors this bubble so the picture is on the left and the text on the
     * right, marking it as coming from the chatbot rather than the user.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
