package com.chat.client.ui.factory;

import com.chat.client.chat.ChatMessage;
import com.chat.client.chat.MessageAuthor;
import com.chat.client.chat.MessageScope;
import com.chat.client.ui.ClientInterface;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.text.*;
import javafx.util.Callback;

/**
 * A factory class for creating {@link ListCell}s that display {@link ChatMessage}s. <br>
 * This class implements the {@link Callback} interface and is used to create list cells for
 * the {@link ListView} of chat messages in a {@link com.chat.client.chat.Chat Chat} {@link javafx.application.Application Application}.
 *
 * @see ChatLabelFactory
 */
public class ChatMessageLabelFactory implements Callback<ListView<ChatMessage>, ListCell<ChatMessage>> {

    /**
     * Creates a new {@link ListCell} for the specified {@link ListView}.
     *
     * @param messageListView The list view for which the list cell is being created.
     * @return The newly created list cell.
     */
    @Override
    public ListCell<ChatMessage> call(ListView<ChatMessage> messageListView) {
        ListCell<ChatMessage> listCell = new ListCell<>() {

            /**
             * Updates the {@link ListCell} to display the specified {@link ChatMessage}.
             *
             * @param _message The chat message to display in the list cell.
             * @param _empty A flag indicating whether the list cell is empty or not.
             */
            @Override
            public void updateItem(ChatMessage _message, boolean _empty) {
                super.updateItem(_message, _empty);
                super.maxWidthProperty().bind( messageListView.widthProperty() );

                if (_message != null) {
                    super.setAlignment( (_message.author().equals(MessageAuthor.SENT))? Pos.CENTER_LEFT : Pos.CENTER_RIGHT );

                    Label messageLabel = new Label(( (_message.scope().equals(MessageScope.PUBLIC) &&
                            _message.author().equals(MessageAuthor.RECEIVED))?
                            _message.writer() + ": \n" : "") + _message.message());

                    messageLabel.setAlignment( (_message.author().equals(MessageAuthor.SENT))? Pos.CENTER_LEFT : Pos.CENTER_RIGHT );
                    messageLabel.maxWidthProperty().bind( super.widthProperty().divide(1.5) );
                    messageLabel.setWrapText(true);

                    messageLabel.setFont( Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 16) );

                    messageLabel.setPadding(new Insets(10, 15, 10, 15));

                    messageLabel.setTextFill(ClientInterface.COLOR_TEXT);
                    messageLabel.setBackground( Background.fill(ClientInterface.DARK_DULL) );

                    TextFlow textFlow = new TextFlow(messageLabel);
                    textFlow.maxWidthProperty().bind( super.widthProperty().divide(1.5) );
                    textFlow.setTextAlignment( (_message.author().equals(MessageAuthor.SENT))? TextAlignment.LEFT : TextAlignment.RIGHT);

                    Platform.runLater(() -> {
                        if (!_empty) this.setGraphic(textFlow);
                        else this.setGraphic(null);
                    });
                } else Platform.runLater(() -> this.setGraphic(null));
            }
        };

        listCell.setBackground(Background.EMPTY);

        return listCell;
    }
}
