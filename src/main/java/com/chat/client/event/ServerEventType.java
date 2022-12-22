package com.chat.client.event;

/**
 * An enum representing the different types of events that can be emitted by a {@link com.chat.client.Client Client} when it handles a
 * {@link com.chat.utils.message.Message Message} from the {@link  com.chat.server.Server Server}.
 *
 * @see ConnectionEventType
 * @see ClientEventType
 * @see ExceptionEventType
 */
public enum ServerEventType {
    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_SEND_PRIVATE SERVER_SEND_PRIVATE}. */
    SERVER_PRIVATE_MESSAGE,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_SEND_PUBLIC SERVER_SEND_PUBLIC}. */
    SERVER_PUBLIC_MESSAGE,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_SEND_ERROR SERVER_SEND_ERROR}. */
    SERVER_ERROR_MESSAGE,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_RENAME_CLIENT SERVER_RENAME_CLIENT}. */
    SERVER_RENAME_MESSAGE,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_CLOSE SERVER_CLOSE}. */
    SERVER_CLOSE_MESSAGE,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_SEND_CLIENTS SERVER_SEND_CLIENTS}. */
    SERVER_SET_CHATS,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_ADD_CLIENT SERVER_ADD_CLIENT}. */
    SERVER_ADD_CHAT,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_RENAME_CLIENT SERVER_RENAME_CLIENT}. */
    SERVER_RENAME_CHAT,

    /** An event type indicating that the handled {@link com.chat.utils.message.Message Message}
     * was of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#SERVER_REMOVE_CLIENT SERVER_REMOVE_CLIENT}. */
    SERVER_REMOVE_CHAT,

    /** A null event. */
    NULL_EVENT
}
