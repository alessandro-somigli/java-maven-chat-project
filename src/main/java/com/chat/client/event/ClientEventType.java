package com.chat.client.event;

/**
 * An enum representing the different types of events that can be emitted by a {@link com.chat.client.Client Client} when it sends a message.
 *
 * @see ConnectionEventType
 * @see ServerEventType
 * @see ExceptionEventType
 */
public enum ClientEventType {
    /** An event type indicating that the client sent a {@link com.chat.utils.message.Message Message} to the
     * {@link com.chat.server.Server Server} of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#CLIENT_SEND_PRIVATE CLIENT_SEND_PRIVATE}. */
    CLIENT_PRIVATE_MESSAGE,

    /** An event type indicating that the client sent a {@link com.chat.utils.message.Message Message} to the
     * {@link com.chat.server.Server Server} of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#CLIENT_SEND_PUBLIC CLIENT_SEND_PUBLIC}. */
    CLIENT_PUBLIC_MESSAGE,

    /** An event type indicating that the client sent a {@link com.chat.utils.message.Message Message} to the
     * {@link com.chat.server.Server Server} of {@link com.chat.utils.message.MessageType MessageType}
     * {@link com.chat.utils.message.MessageType#CLIENT_SET_NAME CLIENT_SET_NAME}. */
    CLIENT_RENAME_CHAT,

    /** A null event. */
    NULL_EVENT
}
