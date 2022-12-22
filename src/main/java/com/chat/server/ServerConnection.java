package com.chat.server;

import com.chat.utils.Utils;
import com.chat.utils.message.ErrorType;
import com.chat.utils.message.Message;
import com.chat.utils.message.MessageType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The ServerConnection class represents a connection between the {@link Server Server} and a {@link com.chat.client.Client Client} in a chat application.
 *
 * <p>
 *     It contains methods for reading and writing as well as listening, handling and processing {@link Message Messages}.
 * </p>
 *
 * @see Server
 */
public class ServerConnection {
    private final int id;
    private String name;

    private final Socket socket;

    private boolean open = false;
    private boolean muted = false;

    private ConcurrentSkipListSet<ServerConnection> clients;

    private BufferedReader inputStream;
    private DataOutputStream outputStream;

    /**
     * Creates a new instance of ServerConnection.
     *
     * @param _socket The {@link Socket} representing the connection between the {@link com.chat.client.Client Client} and the {@link Server}.
     * @param _id The unique ID of the connection.
     */
    public ServerConnection(Socket _socket, int _id) {
        id = _id;
        socket = _socket;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Waits for a {@link String} from the {@link Socket}'s {@link BufferedReader} and reads it.
     *
     * <p>
     *     Closes the ServerConnection if a {@link SocketException} is thrown.
     * </p>
     *
     * @return the {@link Message} read from the BufferedReader, or null if an {@link Exception} occurred.
     */
    public Message read() {
        try { return Utils.serializeJson(inputStream.readLine()); }
        catch (SocketException e) { this.close(null); }
        catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
        return null;
    }

    /**
     * Writes a {@link Message} to the {@link Socket}'s {@link DataOutputStream}.
     *
     * @param _message The Message to write to the DataOutputStream.
     */
    public void write(Message _message) {
        try { outputStream.writeBytes(Utils.deserializeJson(_message) + '\n'); }
        catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Starts listening for {@link Message} and handles them.
     *
     * @param _clients the set of all current connections to the {@link Server}.
     */
    public void open(ConcurrentSkipListSet<ServerConnection> _clients) {
        open = true;
        clients = _clients;

        while (open) {
            Message message = this.read();

            if (name==null && message!=null) { this.submitName(message); }
            else { this.handle(message); }
        }
    }

    /**
     * Handles a {@link Message} by processing and routing it appropriately.
     *
     * @param _message the {@link Message} to be handled.
     */
    public void handle(Message _message) {
        switch ((_message!=null)? _message.type() : MessageType.NULL_MESSAGE) {
            case CLIENT_CLOSE:
                this.close(null);
                break;
            case CLIENT_SEND_PRIVATE:
                if (this.allowed()) {
                    clients.stream()
                        .filter(client -> client.name != null)
                        .filter(client -> client.name.equals(_message.receiver()) )
                        .findAny()
                        .ifPresentOrElse(
                            client -> client.write( new Message(MessageType.SERVER_SEND_PRIVATE, _message.payload(), _message.sender() ) ),
                            () -> this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.RECEIVER_NOT_FOUND.name() ) ) );
                } else { this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.CLIENT_MUTED.name() ) ); }
                break;
            case CLIENT_SEND_PUBLIC:
                if (this.allowed()) {
                    clients.stream()
                            .filter(client -> !client.name.equals(this.name))
                            .forEach(client -> client.write( new Message(
                        MessageType.SERVER_SEND_PUBLIC,
                        _message.payload(),
                        _message.sender() ) ) );
                } else { this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.CLIENT_MUTED.name()) ); }
                break;
            case CLIENT_SET_NAME:
                this.submitName(_message);
                break; 
        }
    }

    /**
     * Closes the connection to the {@link Server} and removes it from the set of current connections.
     *
     * <p>
     *     If a warning message is provided, it is sent to the {@link com.chat.client.Client Client} before the connection is closed.
     * </p>
     *
     * @param _warn the warning message to send to the client before closing the connection, or null if no warning message should be sent.
     */
    public void close(String _warn) {
        try {
            open = false;
            if (_warn != null) this.write(new Message(MessageType.SERVER_CLOSE, _warn));

            clients.remove(this);
            clients.stream()
                    .filter(client -> client.name != null)
                    .forEach(client -> client.write( new Message(MessageType.SERVER_REMOVE_CLIENT, this.name) ));

            socket.close();
        } catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Gets the name of the {@link com.chat.client.Client Client} associated with the connection.
     *
     * @return the name of the client.
     */
    public String getName() { return name; }

    /**
     * Processes a request to set or change the name of the {@link com.chat.client.Client Client} associated with this connection.
     *
     * <p>
     *     If the name is already in use, longer than 64 characters or not allowed, an error message is sent to the client. <br>
     *     If the name is approved, a message of approval is sent to the client and all clients are warned about the change.
     * </p>
     *
     * @param _message a {@link Message} containing the requested name and sender information.
     */
    public void submitName(@NotNull Message _message) {
        if (_message.type() == MessageType.CLIENT_SET_NAME) {
            final boolean exists = clients.stream()
                    .map(ServerConnection::getName)
                    .filter(Objects::nonNull)
                    .anyMatch(name -> name.equals(_message.payload()) );

            if (exists) {
                this.write(new Message(MessageType.SERVER_SEND_ERROR, ErrorType.NAME_ALREADY_SET.name()));
            } else if (
                    _message.payload().equals("") ||
                    _message.payload().equals("PUBLIC") ||
                    _message.payload().length() > 64) {
                this.write(new Message(MessageType.SERVER_SEND_ERROR, ErrorType.NAME_NOT_ALLOWED.name()));
            } else {
                name = _message.payload();
                List<String> names = clients.stream()
                        .map(ServerConnection::getName)
                        .filter(name -> name != null && !this.name.equals(name))
                        .toList();

                this.write( new Message(MessageType.SERVER_APPROVE_NAME, name) );

                this.write( new Message(MessageType.SERVER_SEND_CLIENTS, String.join(",", names)) );

                clients.stream()
                        .filter(client -> client.name != null && !client.name.equals(this.name))
                        .forEach(client -> client.write(
                                new Message( (_message.sender() == null)? MessageType.SERVER_ADD_CLIENT : MessageType.SERVER_RENAME_CLIENT,
                                        _message.payload(), _message.sender()) ));
            }
        } else { this.write( new Message(MessageType.SERVER_SEND_ERROR, ErrorType.NAME_NOT_SET.name()) ); }
    }

    /**
     * Gets the unique ID of the {@link com.chat.client.Client Client} associated with this connection.
     *
     * @return the unique ID of the client.
     */
    public int getId() { return id; }

    /**
     * Sets the mute status of the {@link com.chat.client.Client Client} associated with this connection.
     *
     * @param muted the new mute status of the client.
     */
    public void setMuted(boolean muted) { this.muted = muted; }

    /**
     * Determines whether the {@link com.chat.client.Client Client} associated with this connection is allowed to send {@link Message Messages}.
     *
     * <p>
     *     A client is allowed to send messages if they have a name set and are not muted.
     * </p>
     *
     * @return true if the client is allowed to send messages, false otherwise.
     */
    public boolean allowed() { return name!=null && !muted; }
}
