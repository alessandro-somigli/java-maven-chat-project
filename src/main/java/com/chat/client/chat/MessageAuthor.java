package com.chat.client.chat;

/**
 * An enum representing the author of the {@link ChatMessage}.
 *
 * @see Chat
 * @see ChatMessage
 * @see MessageScope
 */
public enum MessageAuthor {
    /** Indicates that the {@link ChatMessage} was sent by the {@link com.chat.client.Client Client}. */
    SENT,

    /** Indicates that the {@link ChatMessage} was received from another {@link com.chat.client.Client Client}. */
    RECEIVED
}
