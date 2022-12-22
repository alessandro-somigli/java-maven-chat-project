package com.chat.client.ui.factory;

import com.chat.client.chat.Chat;
import com.chat.client.ui.ClientInterface;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

/**
 * A factory class for creating {@link ListCell}s that display {@link Chat} names. <br>
 * This class implements the {@link Callback} interface and is used to create list cells for
 * the {@link ListView} of available chats.
 *
 * @see ChatMessageLabelFactory
 */
public class ChatLabelFactory implements Callback<ListView<Chat>, ListCell<Chat>> {
    /**
     * Creates a new {@link ListCell} for the specified {@link ListView}.
     *
     * @param _listView The list view for which the list cell is being created.
     * @return The newly created list cell.
     */
    @Override
    public ListCell<Chat> call(ListView<Chat> _listView) {

        ListCell<Chat> listCell = new ListCell<>() {

            /**
             * Updates the {@link ListCell} to display the specified {@link Chat}.
             *
             * @param _chat The chat to display in the list cell.
             * @param _empty A flag indicating whether the list cell is empty or not.
             */
            @Override
            public void updateItem(Chat _chat, boolean _empty) {
                super.updateItem(_chat, _empty);

                if (_chat != null) {
                    Label chatLabel = new Label(_chat.getName());

                    chatLabel.setTextFill((super.isSelected())? ClientInterface.MESSAGE_GRADIENT : ClientInterface.COLOR_TEXT);
                    chatLabel.setFont((super.isSelected())?
                            Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.ITALIC, 18) :
                            Font.font(Font.getDefault().toString(), FontWeight.NORMAL, FontPosture.REGULAR, 17));

                    chatLabel.setPadding(new Insets(10, 15, 10, 15));

                    Platform.runLater(() -> {
                        if (!_empty) this.setGraphic(chatLabel);
                        else this.setGraphic(null);
                    });
                } else Platform.runLater(() -> this.setGraphic(null));
            }

        };

        listCell.setBackground(Background.EMPTY);

        return listCell;
    }
}
