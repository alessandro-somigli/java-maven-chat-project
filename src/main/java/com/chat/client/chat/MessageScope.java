package com.chat.client.chat;

/**
 * An enum representing the scope of a {@link ChatMessage}.
 *
 * @see Chat
 * @see ChatMessage
 * @see MessageAuthor
 */
public enum MessageScope {
    /** Indicates that the {@link ChatMessage} was sent in a private {@link Chat}. */
    PRIVATE,

    /** Indicates that the {@link ChatMessage} was sent in the public {@link Chat}. */
    PUBLIC
}
