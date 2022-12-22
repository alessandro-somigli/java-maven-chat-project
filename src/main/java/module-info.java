module com.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.jetbrains.annotations;

    opens com.chat.utils.message to com.fasterxml.jackson.databind;

    opens com.chat.client.ui to javafx.fxml;
    exports com.chat.client.ui to javafx.graphics;

    exports com.chat;

    exports com.chat.client;
    exports com.chat.client.chat;
    exports com.chat.client.event;

    exports com.chat.utils.message;
}