package com.chat.client.event;

/**
 * An enum representing the different types of events that can be emitted by a {@link com.chat.client.Client Client} when its connection status changes.
 *
 * @see ClientEventType
 * @see ServerEventType
 * @see ExceptionEventType
 */
public enum ConnectionEventType {
    /** An event type indicating that the connection between {@link com.chat.client.Client Client} and
     * {@link com.chat.server.Server Server} was successfully established. */
    CONNECTION_SUCCESS,

    /** An event type indicating that the connection between {@link com.chat.client.Client Client} and
     * {@link com.chat.server.Server Server} failed. */
    CONNECTION_FAIL,

    /** An event type indicating that the connection between {@link com.chat.client.Client Client} and
     * {@link com.chat.server.Server Server} was closed. */
    CONNECTION_CLOSE,

    /** A null event. */
    NULL_EVENT
}
