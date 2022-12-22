package com.chat.utils.message;

/**
 * The message type enum represents the purpose of the {@link Message Message}.
 *
 * @see Message
 * @see ErrorType
 */
public enum MessageType {
    /**
     * A {@link Message} from a {@link com.chat.client.Client Client} requesting to set their name.
     * <ul>
     *     <li>payload: client's new name</li>
     *     <li>sender: client's old name</li>
     * </ul>
     * */
    CLIENT_SET_NAME,

    /**
     * A {@link Message} from a {@link com.chat.client.Client Client} requesting to send a private message to another client.
     * <ul>
     *     <li>payload: message contents</li>
     *     <li>sender: client's name</li>
     *     <li>receiver: recipient's name</li>
     * </ul>
     * */
    CLIENT_SEND_PRIVATE,

    /**
     * A {@link Message} from a {@link com.chat.client.Client Client} requesting to send a public message to all clients.
     * <ul>
     *     <li>payload: message contents</li>
     *     <li>sender: client's name</li>
     * </ul>
     * */
    CLIENT_SEND_PUBLIC,

    /**
     * A {@link Message} from a {@link com.chat.client.Client Client} requesting to close the connection.
     * */
    CLIENT_CLOSE,


    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} relaying a private message to a {@link com.chat.client.Client Client} from another client.
     * <ul>
     *     <li>payload: message contents</li>
     *     <li>sender: sender name</li>
     * </ul>
     * */
    SERVER_SEND_PRIVATE,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} relaying a public message to all {@link com.chat.client.Client Client}s.
     * <ul>
     *     <li>payload: message contents</li>
     *     <li>sender: sender name</li>
     * </ul>
     * */
    SERVER_SEND_PUBLIC,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} sending an error message to a {@link com.chat.client.Client Client}.
     * <ul>
     *     <li>payload: error type</li>
     * </ul>
     * */
    SERVER_SEND_ERROR,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} approving a {@link com.chat.client.Client Client}'s request to set their name.
     * <ul>
     *     <li>payload: name approved</li>
     * </ul>
     * */
    SERVER_APPROVE_NAME,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} sending a list of all connected clients to a {@link com.chat.client.Client Client}.
     * <ul>
     *     <li>payload: client names</li>
     * </ul>
     * */
    SERVER_SEND_CLIENTS,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} informing all clients that a new {@link com.chat.client.Client Client} was added to the list of clients.
     * <ul>
     *     <li>payload: client's new name</li>
     * </ul>
     * */
    SERVER_ADD_CLIENT,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} informing all clients that a {@link com.chat.client.Client Client} was renamed.
     * <ul>
     *     <li>payload: client's new name</li>
     *     <li>sender: client's old name</li>
     * </ul>
     * */
    SERVER_RENAME_CLIENT,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} informing all clients that a {@link com.chat.client.Client Client} was removed from the list of clients.
     * <ul>
     *     <li>payload: client's name</li>
     * </ul>
     * */
    SERVER_REMOVE_CLIENT,

    /**
     * A {@link Message} from the {@link com.chat.server.Server Server} warning all {@link com.chat.client.Client Clients} that the connection is about to be closed.
     * <ul>
     *     <li>payload: server's message to clients before closing</li>
     * </ul>
     * */
    SERVER_CLOSE,

    /**
     * A null message.
     * */
    NULL_MESSAGE
}
