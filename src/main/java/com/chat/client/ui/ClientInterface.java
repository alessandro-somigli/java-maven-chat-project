package com.chat.client.ui;

import com.chat.client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * A JavaFX graphical interface for a {@link Client} in a chat {@link Application}. <br>
 * The ClientInterface class extends the Application class and provides a Scene with a UI.
 *
 * @see ConnectController
 * @see ChatController
 */
public class ClientInterface extends Application {
    public static final String APPLICATION_NAME = "Chilly Chat";

    public static final int MIN_WINDOW_WIDTH   = 600;
    public static final int MIN_WINDOW_HEIGHT  = 400;

    public static final String CONNECT_PAGE_PATH     = "connect_page.fxml";
    public static final String CHAT_PAGE_PATH        = "chat_page.fxml";

    public static final String APPLICATION_LOGO_PATH = "images/mountain_logo.png";

    public static final Color COLOR_ALERT    = Color.DARKRED;
    public static final Color COLOR_WARNING  = Color.GOLD;
    public static final Color COLOR_SUCCESS  = Color.DARKGREEN;

    public static final Color COLOR_TEXT     = Color.WHITE;

    public static final Color DARK_DULL      = Color.web("#222425");
    public static final Color DARK_DIM       = Color.web("#1E2021");
    public static final Color DARK_SHADOW    = Color.web("#1A1C1D");
    public static final Color DARK_PITCH     = Color.web("#161819");

    public static final Color CHEMICAL_PINK  = Color.web("#E782FC");
    public static final Color SKY_BLUE       = Color.web("#80D6FE");

    public static final LinearGradient MESSAGE_GRADIENT = new LinearGradient(0, 0, 1, 0, true,
            CycleMethod.NO_CYCLE,
            new Stop(0, SKY_BLUE),
            new Stop(1, CHEMICAL_PINK) );

    private final Client client = new Client();

    private Stage mainStage = null;

    /** runs the {@link Application}. */
    public void run() { Application.launch(); }

    /** This method is called before the {@link Application} launches. */
    @Override public void init() { Platform.setImplicitExit(true); }

    /**
     * This method is called after the {@link Application} launches.
     *
     * @param stage the stage initialized by the application
     * @throws IOException when an I/O Exception occurs
     */
    @Override public void start(@NotNull Stage stage) throws IOException {
        mainStage = stage;
        mainStage.getIcons().add( new Image( Objects.requireNonNull( ClientInterface.class.getResourceAsStream(APPLICATION_LOGO_PATH) ) ) );
        mainStage.setTitle(APPLICATION_NAME);
        mainStage.setScene( this.initScene() );
        mainStage.setResizable(false);
        mainStage.show();
    }

    /** This method is called when the {@link Application} stops. */
    @Override public void stop() { if (client.isOpen()) client.sendExitMessage(); }

    /**
     * Initializes and returns the {@link Scene} for the {@link Application}.
     *
     * @return The initialized scene for the application.
     * @throws IOException If an exception occurs loading the FXML files.
     */
    public Scene initScene() throws IOException {
        FXMLLoader chatPageLoader = new FXMLLoader(ClientInterface.class.getResource(CHAT_PAGE_PATH));
        chatPageLoader.setControllerFactory(c -> new ChatController(client));

        Scene chatPageScene = new Scene(chatPageLoader.load());

        FXMLLoader connectPageLoader = new FXMLLoader(ClientInterface.class.getResource(CONNECT_PAGE_PATH));

        ConnectController connectPageController = new ConnectController(client);
        connectPageController.setTransitionEvent(() -> Platform.runLater(() -> {
            mainStage.setResizable(true);
            mainStage.setMinWidth(MIN_WINDOW_WIDTH);
            mainStage.setMinHeight(MIN_WINDOW_HEIGHT);
            mainStage.setScene(chatPageScene);
        }));
        connectPageLoader.setControllerFactory(c -> connectPageController);

        return new Scene(connectPageLoader.load());
    }
}
