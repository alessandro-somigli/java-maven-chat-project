package com.chat.utils.message;

/**
 * Message is the POJO that gets serialized and deserialized to exchange information from {@link com.chat.server.Server Server}
 * to {@link com.chat.client.Client Client} and vice versa.
 *
 * @see MessageType
 * @see ErrorType
 *
 * @param type the {@link MessageType} represents what the message's purpose is
 * @param payload the effective contents of the message
 * @param sender the sender of the message
 * @param receiver the recipient of the message
 */
public record Message (MessageType type, String payload, String sender, String receiver) {
    public Message() { this(null, null, null, null); }
    public Message(MessageType type) { this(type, null, null, null); }
    public Message(MessageType type, String payload) { this(type, payload, null, null); }
    public Message(MessageType type, String payload, String sender) { this(type, payload, sender, null); }
}