package com.chat.client.ui;

import com.chat.client.Client;
import com.chat.client.event.ConnectionEventType;
import com.chat.client.event.ServerEventType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.concurrent.TimeUnit;

/**
 * A controller class for the connect page of the {@link javafx.application.Application Application}. <br>
 * The ConnectController class manages the input and event handling tasks for the connect page of the application. <br>
 * It does so by updating the UI based on the connection and server events emitted by the {@link Client}.
 *
 * @see ClientInterface
 * @see ChatController
 */
public class ConnectController {
    private final Client client;

    private static final String STATE_UNFULFILLED = "UNFULFILLED";
    private static final String STATE_FULFILLED = "FULFILLED";
    private static final String STATE_LOADING = "LOADING";

    private String ipState = STATE_UNFULFILLED;
    private String nameState = STATE_UNFULFILLED;

    private Runnable transitionEvent = () -> {};

    @FXML public TextField serverIPInput;
    @FXML public TextField loginNameInput;

    @FXML public Button serverIPButton;
    @FXML public Button loginNameButton;

    @FXML public Label loginPromptLabel;

    /**
     * Creates a new ConnectController with the given {@link Client}.
     *
     * @param _client The client to be used by the ConnectController.
     */
    public ConnectController(Client _client) { client = _client; }

    /**
     * Initializes the ConnectController.
     *
     * <p>
     *     This method is called by the {@link javafx.fxml.FXMLLoader FXMLLoader} when the connect page is loaded. <br>
     *     It sets up the UI elements and adds event listeners to the {@link Client}.
     * </p>
     */
    @FXML public void initialize() {
        loginPromptLabel.setText("");

        ipState = STATE_UNFULFILLED;
        nameState = STATE_UNFULFILLED;
        this.updateState();

        client.addConnectionEvent(
                ConnectionEventType.CONNECTION_SUCCESS,
                () -> {
                    Platform.runLater(() -> {
                        loginPromptLabel.setTextFill(ClientInterface.COLOR_SUCCESS);
                        loginPromptLabel.setText("server connection established");
                    });

                    ipState = STATE_FULFILLED;
                    this.updateState();
                }
        );
        client.addConnectionEvent(
                ConnectionEventType.CONNECTION_FAIL,
                () -> {
                    Platform.runLater(() -> {
                        loginPromptLabel.setTextFill(ClientInterface.COLOR_ALERT);
                        loginPromptLabel.setText("server connection failed");
                    });

                    ipState = STATE_UNFULFILLED;
                    this.updateState();
                }
        );
        client.addConnectionEvent(
                ConnectionEventType.CONNECTION_CLOSE,
                () -> {
                    Platform.runLater(() -> {
                        loginPromptLabel.setTextFill(ClientInterface.COLOR_WARNING);
                        loginPromptLabel.setText("connection with server was closed");
                    });

                    ipState = STATE_UNFULFILLED;
                    nameState = STATE_UNFULFILLED;
                    this.updateState();
                }
        );

        client.addServerEvent(
                ServerEventType.SERVER_RENAME_MESSAGE,
                message -> {
                    Platform.runLater(() -> {
                        loginPromptLabel.setTextFill(ClientInterface.COLOR_SUCCESS);
                        loginPromptLabel.setText("name accepted by server");
                    });

                    nameState = STATE_FULFILLED;
                    this.updateState();
                }
        );
        client.addServerEvent(
                ServerEventType.SERVER_ERROR_MESSAGE,
                message -> Platform.runLater(() -> {
                    loginPromptLabel.setTextFill(ClientInterface.COLOR_ALERT);
                    loginPromptLabel.setText("error: " + message.payload());

                    ipState = (ipState.equals(STATE_LOADING))? STATE_UNFULFILLED : ipState;
                    nameState = (nameState.equals(STATE_LOADING))? STATE_UNFULFILLED : nameState;
                })
        );
    }

    /**
     * This method is called when the form's IP button is clicked. <br>
     * It attempts to connect to the {@link com.chat.server.Server Server} using the IP address specified in the form's IP input field.
     *
     * <p>
     *     If the connection is successful, the name input field will be enabled and the server IP field will be disabled. <br>
     *     If the connection fails, an error message will be displayed.
     * </p>
     */
    @FXML public void onServerIPButtonClick() {
        if (!ipState.equals(STATE_LOADING)) {
            new Thread(() -> {
                ipState = STATE_LOADING;
                loginPromptLabel.setTextFill(ClientInterface.COLOR_WARNING);
                new Thread(() -> this.loadingAnimation(0)).start();

                String input = serverIPInput.getText();
                client.connect(input);
            }).start();
        }
    }

    /**
     * This method is called when the form's login name button is clicked. <br>
     * It sends a message to the {@link com.chat.server.Server Server} requesting to set the login name as the one specified in the form's name input field.
     *
     * <p>
     *     If the request is successful, the chat {@link javafx.scene.Scene Scene} will be displayed. <br>
     *     If the request fails, an error message will be displayed.
     * </p>
     */
    @FXML public void onLoginNameButtonClick() {
        if (!nameState.equals(STATE_LOADING)) {
            new Thread(() -> {
                nameState = STATE_LOADING;
                loginPromptLabel.setTextFill(ClientInterface.COLOR_WARNING);
                new Thread(() -> this.loadingAnimation(0)).start();

                String input = loginNameInput.getText();
                client.sendChangeNameMessage(input);
            }).start();
        }
    }

    /**
     * This method updates the state of the input fields based on the current connection and name states.
     *
     * <p>
     *     If the connection to the {@link com.chat.server.Server Server} has not been established,
     *     the name input field will be disabled and the IP input field will be enabled. <br>
     *     If the connection has been established, the name input field will be enabled and the IP input field will be disabled. <br>
     *     If both the connection and name are established, the chat {@link javafx.scene.Scene Scene} will be displayed.
     * </p>
     */
    public void updateState() {
        if (ipState.equals(STATE_FULFILLED)) {
            if (nameState.equals(STATE_FULFILLED)) {
                transitionEvent.run();
            } else  {
                serverIPInput.setDisable(true);
                serverIPButton.setDisable(true);

                loginNameInput.setDisable(false);
                loginNameButton.setDisable(false);
            }
        } else {
            serverIPInput.setDisable(false);
            serverIPButton.setDisable(false);

            loginNameInput.setDisable(true);
            loginNameButton.setDisable(true);
        }
    }

    /**
     * This method displays a loading animation in the prompt label while the connection to the {@link com.chat.server.Server Server}
     * or name request is being attempted. <br>
     * The animation consists of a waiting message and a series of dots ranging from 1 to 3, being added and removed after a short time delay.
     *
     * @param _counter the number of dots in the current animation iteration
     */
    public void loadingAnimation(int _counter) {
        try {
            if (ipState.equals(STATE_LOADING)) {
                int final_counter = _counter = (_counter < 3)? ++_counter : 1;
                Platform.runLater(() -> loginPromptLabel.setText("loading" + ".".repeat(final_counter)));
                TimeUnit.MILLISECONDS.sleep(500);
                this.loadingAnimation(_counter);
            }
        } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    /**
     * This method sets the event that will be triggered when the {@link javafx.stage.Stage Stage} will transition
     * between the connect {@link javafx.scene.Scene Scene} and the chat scene.
     *
     * @param _event the event to be triggered
     */
    public void setTransitionEvent(Runnable _event) { this.transitionEvent = _event; }
}
