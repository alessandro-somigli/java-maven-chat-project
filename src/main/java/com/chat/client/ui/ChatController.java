package com.chat.client.ui;

import com.chat.client.Client;
import com.chat.client.chat.Chat;
import com.chat.client.chat.ChatMessage;
import com.chat.client.event.ClientEventType;
import com.chat.client.event.ConnectionEventType;
import com.chat.client.event.ServerEventType;
import com.chat.client.ui.factory.ChatLabelFactory;
import com.chat.client.ui.factory.ChatMessageLabelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * A controller class for the chat page of the {@link javafx.application.Application Application}. <br>
 * The ChatController class manages the UI elements related to the application's interface, such as displaying
 * available {@link Chat}s, {@link com.chat.utils.message.Message Message}s,
 * handling events emitted by the {@link Client}, and user input. <br>
 *
 * @see ClientInterface
 * @see ConnectController
 */
public class ChatController {
    private final Client client;
    private Chat selectedChat;

    @FXML public Label clientNameLabel;
    @FXML public Label serverIPLabel;
    @FXML public Label errorLabel;

    @FXML public ListView<Chat> chatsListView;
    @FXML public ListView<ChatMessage> messagesListView;
    
    @FXML public TextArea inputTextArea;
    @FXML public Button inputButton;

    @FXML public TextField changeNameTextField;
    @FXML public Button changeNameButton;

    /**
     * Creates a new instance of the ChatController class.
     *
     * @param _client The client instance to associate with this controller.
     */
    public ChatController(Client _client) { client = _client; }

    /**
     * Updates the list of available chats.
     */
    public void updateChats() {
        Chat selected = chatsListView.getSelectionModel().getSelectedItem();

        chatsListView.getItems().clear();
        chatsListView.getItems().add( client.getPublicChat() );
        chatsListView.getItems().addAll( client.getChats() );

        chatsListView.getSelectionModel().select(selected);
    }

    /**
     * Updates the messages in the selected chat.
     */
    public void updateMessages() {
        if (selectedChat != null) {
            messagesListView.getItems().clear();
            messagesListView.getItems().addAll( selectedChat.getMessages() );
            Platform.runLater(() -> messagesListView.scrollTo( messagesListView.getItems().size() - 1 ));
        }
    }

    /**
     * Initializes the UI elements and events for the {@link javafx.application.Application Application}'s interface.
     */
    @FXML public void initialize() {
        this.initComponents();
        this.initClientEvents();
    }

    /**
     * Initializes the UI elements for the {@link javafx.application.Application Application}'s interface.
     */
    public void initComponents() {
        clientNameLabel.setText("");
        serverIPLabel.setText("");

        messagesListView.cellFactoryProperty().set(new ChatMessageLabelFactory());
        messagesListView.getItems().removeAll();

        chatsListView.cellFactoryProperty().set(new ChatLabelFactory());
        chatsListView.getItems().clear();
        chatsListView.getItems().add(client.getPublicChat());
        chatsListView.setOnMouseClicked(event -> {
            Chat chat = chatsListView.getSelectionModel().getSelectedItem();

            if (chat!=null) {
                selectedChat = chat;
                messagesListView.getItems().setAll( selectedChat.getMessages() );
            }
        });
    }

    /**
     * Initializes the event listeners emitted by the client instance.
     */
    public void initClientEvents() {
        client.addConnectionEvent(
                ConnectionEventType.CONNECTION_SUCCESS,
                () -> Platform.runLater(() -> serverIPLabel.setText("IP: " + client.getServerAddress()))
        );

        client.addClientEvent(
                ClientEventType.CLIENT_PRIVATE_MESSAGE,
                message -> this.updateMessages()
        );
        client.addClientEvent(
                ClientEventType.CLIENT_PUBLIC_MESSAGE,
                message -> this.updateMessages()
        );

        client.addServerEvent(
                ServerEventType.SERVER_PRIVATE_MESSAGE,
                message -> {
                    if (selectedChat!=null && selectedChat.getName().equals(message.sender())) this.updateMessages();
                }
        );
        client.addServerEvent(
                ServerEventType.SERVER_PUBLIC_MESSAGE,
                message -> {
                    if (selectedChat!=null && selectedChat.getName().equals(Client.PUBLIC_CHAT_NAME)) this.updateMessages();
                }
        );
        client.addServerEvent(
                ServerEventType.SERVER_RENAME_MESSAGE,
                message -> Platform.runLater(() -> clientNameLabel.setText(client.getName()))
        );
        client.addServerEvent(
                ServerEventType.SERVER_ERROR_MESSAGE,
                message -> Platform.runLater(() -> {
                    errorLabel.setTextFill(ClientInterface.COLOR_ALERT);
                    errorLabel.setText("ERROR: " + message.payload());
                })
        );
        client.addServerEvent(
                ServerEventType.SERVER_CLOSE_MESSAGE,
                message -> Platform.runLater(() -> {
                    errorLabel.setTextFill(ClientInterface.COLOR_WARNING);
                    errorLabel.setText("Connection was closed: " + message.payload());
                })
        );
        client.addServerEvent(
                ServerEventType.SERVER_SET_CHATS,
                message -> this.updateChats()
        );
        client.addServerEvent(
                ServerEventType.SERVER_ADD_CHAT,
                message -> this.updateChats()
        );
        client.addServerEvent(
                ServerEventType.SERVER_RENAME_CHAT,
                message -> this.updateChats()
        );
        client.addServerEvent(
                ServerEventType.SERVER_REMOVE_CHAT,
                message -> this.updateChats()
        );
    }

    /**
     * This method is called when the send message button is clicked. <br>
     * If the input text area is not empty and a {@link Chat} is selected,
     * sends the {@link com.chat.utils.message.Message Message} to the selected chat.
     */
    @FXML public void onInputButtonClick() {
        if (!inputTextArea.getText().equals("") && selectedChat != null) {
            if (selectedChat.getName().equals(Client.PUBLIC_CHAT_NAME)) { client.sendPublicMessage(inputTextArea.getText()); }
            else { client.sendPrivateMessage(selectedChat.getName(), inputTextArea.getText()); }
            inputTextArea.setText("");
        }
    }

    /**
     * This method is called when the change name button is clicked. <br>
     * If the change name text field is not empty, sends a change name request to the {@link com.chat.server.Server Sevrer}.
     */
    @FXML public void onChangeNameButtonClick() {
        if (!changeNameTextField.getText().equals("")) {
            client.sendChangeNameMessage(changeNameTextField.getText());
            changeNameTextField.setText("");
        }
    }
}
