package com.chat.utils.message;

/**
 * The ErrorType enum represents the type of error message.
 *
 * @see Message
 * @see MessageType
 */
public enum ErrorType {
    /** An error message indicating that the name requested by the {@link com.chat.client.Client Client} is already used by another client. */
    NAME_ALREADY_SET,

    /** An error message indicating that the {@link com.chat.client.Client Client}'s requested name is not allowed. */
    NAME_NOT_ALLOWED,

    /** An error message indicating that the {@link com.chat.client.Client Client} does not have a name yet. */
    NAME_NOT_SET,

    /** An error message indicating that the {@link com.chat.client.Client Client} has been muted. */
    CLIENT_MUTED,

    /** An error message indicating that the recipient of the {@link Message} could not be found. */
    RECEIVER_NOT_FOUND
}
