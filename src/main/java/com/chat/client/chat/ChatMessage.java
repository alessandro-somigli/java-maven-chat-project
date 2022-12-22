package com.chat.client.chat;

/**
 * A record representing a {@link Chat} message.
 *
 * @see Chat
 * @see MessageAuthor
 * @see MessageScope
 *
 * @param message the message's text
 * @param writer the name of the writer
 * @param scope whether the chat of this message is public or private
 * @param author the message author
 */
public record ChatMessage (String message, String writer, MessageScope scope, MessageAuthor author) {}
