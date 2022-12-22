package com.chat.client.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A chat is a collection of {@link ChatMessage}s that can be written to and read from. <br>
 * Each chat has a name and a list of messages.
 *
 * @see ChatMessage
 * @see MessageAuthor
 * @see MessageScope
 */
public class Chat {
    private final List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
    private String name;

    /**
     * Constructs a new chat with the given name.
     *
     * @param _name the name of the chat.
     */
    public Chat(String _name) {
        name = _name;
    }

    /**
     * Writes a new {@link ChatMessage} to this chat.
     *
     * @param _message the message's text
     * @param _writer the name of the writer
     * @param _scope whether the chat is public or private
     * @param _author the message author
     */
    public void write(String _message, String _writer, MessageScope _scope, MessageAuthor _author) {
        messages.add( new ChatMessage(_message, _writer, _scope, _author) );
    }

    /**
     * Sets the name of this chat.
     *
     * @param _name the new name of the chat
     */
    public void setName(String _name) { name = _name; }

    /**
     * @return the name of this chat
     */
    public String getName() { return name; }

    /**
     * @return the list of {@link com.chat.utils.message.Message Message}s in this chat
     */
    public List<ChatMessage> getMessages() { return messages; }
}
