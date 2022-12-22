package com.chat.client.event;

/**
 * An enum representing the different types of events that can be emitted by a {@link com.chat.client.Client Client} when it throws an {@link Exception}.
 *
 * @see ConnectionEventType
 * @see ServerEventType
 * @see ClientEventType
 */
public enum ExceptionEventType {
    /** An event type indicating that an {@link java.io.IOException IOException} was caught. */
    EXCEPTION_IO,

    /** An event type indicating that a {@link java.net.SocketException SocketException} was caught. */
    EXCEPTION_SOCKET,

    /** An event type indicating that a generic {@link Exception} was caught. */
    EXCEPTION_GENERIC,

    /** A null event. */
    NULL_EVENT
}
