package com.chat.client;

import com.chat.client.chat.Chat;
import com.chat.client.chat.MessageAuthor;
import com.chat.client.chat.MessageScope;
import com.chat.client.event.ClientEventType;
import com.chat.client.event.ConnectionEventType;
import com.chat.client.event.ExceptionEventType;
import com.chat.client.event.ServerEventType;
import com.chat.utils.Utils;
import com.chat.utils.message.Message;
import com.chat.utils.message.MessageType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Client is a class representing a client in a chat application. <br>
 * It includes methods for connecting and disconnecting to a {@link com.chat.server.Server Server}, sending and receiving {@link Message Message}s.
 *
 * <p>
 *     The client has some event listeners which can be used to attach handlers to it and expand its functionality. <br>
 *     The following event listeners are available:
 * </p>
 *
 *<ul>
 *     <li>server events: events fired when the server sends a message</li>
 *     <li>client events: events fired when the client sends a message</li>
 *     <li>exception events: events fired when an {@link Exception} occurs</li>
 *     <li>connection events: events fired when the connection status between client and server changes</li>
 *</ul>
 */
public class Client {
    public static final String PUBLIC_CHAT_NAME = "PUBLIC";

    private boolean open = false;

    private static final int SERVER_PORT = 7777;
    private String SERVER_ADDRESS;

    private Socket server;
    private String name;

    private BufferedReader inputStream;
    private DataOutputStream outputStream;

    private final HashSet<Chat> chats = new HashSet<>();
    private final Chat publicChat = new Chat(PUBLIC_CHAT_NAME);

    private final HashMap<ServerEventType, ArrayList<Consumer<Message>>> serverEvents = new HashMap<>(
            Arrays.stream(ServerEventType.values())
                    .collect( Collectors.toMap(type -> type, type -> new ArrayList<>() ) )
    );
    private final HashMap<ClientEventType, ArrayList<Consumer<Message>>> clientEvents = new HashMap<>(
            Arrays.stream(ClientEventType.values())
                    .collect( Collectors.toMap(type -> type, type -> new ArrayList<>()) )
    );
    private final HashMap<ExceptionEventType, ArrayList<Consumer<Exception>>> exceptionEvents = new HashMap<>(
            Arrays.stream(ExceptionEventType.values())
                    .collect( Collectors.toMap(type -> type, type -> new ArrayList<>() ) )
    );
    private final HashMap<ConnectionEventType, ArrayList<Runnable>> connectionEvents = new HashMap<>(
            Arrays.stream(ConnectionEventType.values())
                    .collect( Collectors.toMap(type -> type, type -> new ArrayList<>() ) )
    );

    /**
     * Adds an {@link Consumer event handler} to a specific {@link ServerEventType}.
     *
     * @param _type the ServerEventType.
     * @param _event the event handler to be added.
     */
    public void addServerEvent(ServerEventType _type, Consumer<Message> _event) { serverEvents.get(_type).add(_event); }

    /**
     * Calls all {@link Consumer event handler}s of a specific {@link ServerEventType}.
     *
     * @param _type the ServerEventType.
     * @param _message the {@link Message} that triggered the event.
     */
    public void callServerEvent(ServerEventType _type, Message _message) { serverEvents.get(_type).forEach(event -> event.accept(_message)); }

    /**
     * Adds an {@link Consumer event handler} for a specific {@link ClientEventType}.
     *
     * @param _type the ClientEventType.
     * @param _event the event handler to be added.
     */
    public void addClientEvent(ClientEventType _type, Consumer<Message> _event) { clientEvents.get(_type).add(_event); }

    /**
     * Calls all {@link Consumer event handler}s of a specific {@link ClientEventType}.
     *
     * @param _type the ClientEventType.
     * @param _message the {@link Message} that triggered the event.
     */
    public void callClientEvent(ClientEventType _type, Message _message) { clientEvents.get(_type).forEach(event -> event.accept(_message)); }

    /**
     * Adds an {@link Consumer event handler} for a specific {@link ExceptionEventType}.
     *
     * @param _type the ExceptionEventType.
     * @param _event the event handler to be added.
     */
    public void addExceptionEvent(ExceptionEventType _type, Consumer<Exception> _event) { exceptionEvents.get(_type).add(_event); }

    /**
     * Calls all {@link Consumer event handler}s of a specific {@link ExceptionEventType}.
     *
     * @param _type the ExceptionEventType.
     * @param _exception the {@link Exception} that triggered the event.
     */
    public void callExceptionEvent(ExceptionEventType _type, Exception _exception) { exceptionEvents.get(_type).forEach(event -> event.accept(_exception)); }

    /**
     * Adds an {@link Runnable event handler} for a specific {@link ConnectionEventType}.
     *
     * @param _type the ConnectionEventType.
     * @param _event the event handler to be added.
     */
    public void addConnectionEvent(ConnectionEventType _type, Runnable _event) { connectionEvents.get(_type).add(_event); }

    /**
     * Calls all  for a specif{@link Runnable event handler}s of a specific {@link ConnectionEventType}.
     *
     * @param _type the ConnectionEventType.
     */
    public void callConnectionEvent(ConnectionEventType _type) { connectionEvents.get(_type).forEach(Runnable::run); }

    /**
     * Connects the client to the specified {@link com.chat.server.Server Server}.
     *
     * @param _serverName the address of the server to connect to.
     */
    public void connect(String _serverName) {
        try {
            server = new Socket(_serverName, Client.SERVER_PORT);
            inputStream = new BufferedReader(new InputStreamReader(server.getInputStream()));
            outputStream = new DataOutputStream(server.getOutputStream());

            SERVER_ADDRESS = server.getInetAddress().toString();
            open = true;

            new Thread(() -> { while (open) this.listen(); } ).start();

            this.callConnectionEvent(ConnectionEventType.CONNECTION_SUCCESS);
        } catch (ConnectException | UnknownHostException e) { this.callConnectionEvent(ConnectionEventType.CONNECTION_FAIL); }
        catch (IOException e) { this.callExceptionEvent(ExceptionEventType.EXCEPTION_IO, e); }
    }

    /**
     * Disconnects the client from the {@link com.chat.server.Server Server}.
     *
     * @param _warn Whether to send a {@link Message} to the server indicating that the client is disconnecting.
     */
    public void disconnect(boolean _warn) {
        try {
            open = false;
            if (_warn) this.write( new Message(MessageType.CLIENT_CLOSE) );
            server.close();
            this.callConnectionEvent(ConnectionEventType.CONNECTION_CLOSE);
        } catch (IOException e) { this.callExceptionEvent(ExceptionEventType.EXCEPTION_IO, e); }
    }

    /**
     * Listens for incoming {@link Message}s from the {@link com.chat.server.Server Server} and handles them appropriately. <br>
     * The following is a list of the different cases that can be handled:
     *
     * <ul>
     *     <li>{@link MessageType#SERVER_SEND_PRIVATE SERVER_SEND_PRIVATE}: writes the message to the specified private {@link Chat}</li>
     *     <li>{@link MessageType#SERVER_SEND_PUBLIC SERVER_SEND_PUBLIC}: writes the message to the public chat</li>
     *     <li>{@link MessageType#SERVER_SEND_ERROR SERVER_SEND_ERROR}: calls the event to handle error messages</li>
     *     <li>{@link MessageType#SERVER_APPROVE_NAME SERVER_APPROVE_NAME}: updates the name of the client to the specified value</li>
     *     <li>{@link MessageType#SERVER_SEND_CLIENTS SERVER_SEND_CLIENTS}: updates the chats list to the specified values</li>
     *     <li>{@link MessageType#SERVER_ADD_CLIENT SERVER_ADD_CLIENT}: adds a new chat to the list of chats</li>
     *     <li>{@link MessageType#SERVER_RENAME_CLIENT SERVER_RENAME_CLIENT}: renames the specified chat</li>
     *     <li>{@link MessageType#SERVER_REMOVE_CLIENT SERVER_REMOVE_CLIENT}: removes a chat from the list of chats</li>
     *     <li>{@link MessageType#SERVER_CLOSE SERVER_CLOSE}: disconnects the client from the server</li>
     * </ul>
     */
    public void listen() {
        Message message = this.read();

        switch ((message!=null)? message.type() : MessageType.NULL_MESSAGE) {
            case SERVER_SEND_PRIVATE -> {
                chats.stream()
                        .filter(chat -> chat.getName().equals(message.sender()))
                        .findAny()
                        .ifPresent(chat -> chat.write(message.payload(), message.sender(), MessageScope.PRIVATE, MessageAuthor.RECEIVED));

                this.callServerEvent(ServerEventType.SERVER_PRIVATE_MESSAGE, message);
            } case SERVER_SEND_PUBLIC -> {
                publicChat.write(message.payload(), message.sender(), MessageScope.PUBLIC, MessageAuthor.RECEIVED);

                this.callServerEvent(ServerEventType.SERVER_PUBLIC_MESSAGE, message);
            } case SERVER_SEND_ERROR -> {
                this.callServerEvent(ServerEventType.SERVER_ERROR_MESSAGE, message);
            } case SERVER_APPROVE_NAME -> {
                name = message.payload();

                this.callServerEvent(ServerEventType.SERVER_RENAME_MESSAGE, message);
            } case SERVER_SEND_CLIENTS -> {
                chats.clear();
                if (!message.payload().equals("")) {
                    Arrays.stream(message.payload().split(","))
                            .forEach(name -> chats.add(new Chat(name)));
                }

                this.callServerEvent(ServerEventType.SERVER_SET_CHATS, message);
            } case SERVER_ADD_CLIENT -> {
                chats.add(new Chat(message.payload()));

                this.callServerEvent(ServerEventType.SERVER_ADD_CHAT, message);
            } case SERVER_RENAME_CLIENT -> {
                chats.stream()
                        .filter(chat -> chat.getName().equals(message.sender()))
                        .findAny()
                        .ifPresent(chat -> chat.setName(message.payload()));

                this.callServerEvent(ServerEventType.SERVER_RENAME_CHAT, message);
            } case SERVER_REMOVE_CLIENT -> {
                chats.stream()
                        .filter(chat -> chat.getName().equals(message.payload()))
                        .findAny()
                        .ifPresent(chats::remove);

                this.callServerEvent(ServerEventType.SERVER_REMOVE_CHAT, message);
            } case SERVER_CLOSE -> {
                this.disconnect(false);

                this.callServerEvent(ServerEventType.SERVER_CLOSE_MESSAGE, message);
            }
        }
    }

    /**
     * Waits for a {@link String} from the {@link Socket}'s {@link BufferedReader} and reads it.
     *
     * <p>
     *     Closes the Client if a {@link SocketException} is thrown.
     * </p>
     *
     * @return the {@link Message} read from the {@link BufferedReader}, or null if an {@link Exception} occurred.
     */
    public Message read() {
        try { return Utils.serializeJson(inputStream.readLine()); }
        catch (SocketException e) { this.callExceptionEvent(ExceptionEventType.EXCEPTION_SOCKET, e); }
        catch (IOException e) { this.callExceptionEvent(ExceptionEventType.EXCEPTION_IO, e); }
        return null;
    }

    /**
     * Writes a {@link Message} to the {@link Socket}'s {@link DataOutputStream}.
     *
     * @param _message the {@link Message} to write to the {@link DataOutputStream}.
     */
    public void write(Message _message) {
        try { outputStream.writeBytes(Utils.deserializeJson(_message) + '\n'); }
        catch (IOException e) { this.callExceptionEvent(ExceptionEventType.EXCEPTION_IO, e); }
    }

    /**
     * Sends a {@link Message} to the {@link com.chat.server.Server Server} requesting to change name.
     *
     * @param _payload the new name for the client.
     */
    public void sendChangeNameMessage(String _payload) {
        Message message = new Message(MessageType.CLIENT_SET_NAME, _payload, this.name);
        this.write(message);

        this.callClientEvent(ClientEventType.CLIENT_RENAME_CHAT, message);
    }

    /**
     * Sends a private {@link Message} to a specific client.
     *
     * @param _target the name of the client to send the message to.
     * @param _payload the message to be sent.
     */
    public void sendPrivateMessage(String _target, String _payload) {
        chats.stream()
                .filter(chat -> chat.getName().equals(_target))
                .findAny()
                .ifPresent(chat -> chat.write(_payload, this.name, MessageScope.PRIVATE, MessageAuthor.SENT));

        Message message = new Message(MessageType.CLIENT_SEND_PRIVATE, _payload, this.name, _target);
        this.write(message);

        this.callClientEvent(ClientEventType.CLIENT_PRIVATE_MESSAGE, message);
    }

    /**
     * Sends a public {@link Message} to all clients.
     *
     * @param _payload the message to be sent.
     */
    public void sendPublicMessage(String _payload) {
        publicChat.write(_payload, this.name, MessageScope.PUBLIC, MessageAuthor.SENT);

        Message message = new Message(MessageType.CLIENT_SEND_PUBLIC, _payload, this.name );
        this.write(message);

        this.callClientEvent(ClientEventType.CLIENT_PUBLIC_MESSAGE, message);
    }

    /**
     * Sends a {@link Message} to the {@link com.chat.server.Server Server} indicating that the client is disconnecting.
     */
    public void sendExitMessage() { this.disconnect(true); }

    /** @return true if the {@link com.chat.server.Server Server} is open, false otherwise */
    public boolean isOpen() { return open; }

    /** @return the name of the {@link com.chat.server.Server Server} */
    public String getName() { return name; }

    /** @return the port number of the {@link com.chat.server.Server Server} */
    public static int getServerPort() { return SERVER_PORT; }

    /** @return the address of the {@link com.chat.server.Server Server}. */
    public String getServerAddress() { return SERVER_ADDRESS; }

    /** @return the public chat */
    public Chat getPublicChat() { return publicChat; }

    /** @return a set of all the private chats */
    public HashSet<Chat> getChats() { return chats; }
}
