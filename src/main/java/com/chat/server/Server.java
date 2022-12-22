package com.chat.server;

import com.chat.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The Server class represents the server in a chat application.
 * It listens for incoming connections from clients, and handles the communication between them.
 *
 * <p>
 *     The server has a command-line interface (CLI) that allows the user to enter commands to control the server. <br>
 *     The following commands are available:
 * </p>
 *
 * <ul>
 *   <li>@help, @?: displays a list of all available commands</li>
 *   <li>@open: opens the server and begins listening for incoming {@link ServerConnection ServerConnections}</li>
 *   <li>@close: closes the server and all connections</li>
 *   <li>@exit: exits the CLI and closes the server</li>
 *   <li>@clients: displays a list of all currently connected {@link com.chat.client.Client Clients}</li>
 *   <li>@kick [name]: kicks the client with the given name from the chat</li>
 *   <li>@mute [name]: mutes the client with the given name</li>
 *   <li>@unmute [name]: unmutes the client with the given name</li>
 * </ul>
 *
 * @see ServerConnection
 */
public class Server {
    private static final int PORT = 7777;

    private boolean openServer = false;
    private boolean openCli = false;

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    private ServerSocket server;
    private final ConcurrentSkipListSet<ServerConnection> clients =
            new ConcurrentSkipListSet<>(Comparator.comparingInt(ServerConnection::getId));

    private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Opens the server and begins listening for incoming connections.
     */
    public void open() {
        if (!openServer) {
            try {
                openServer = true;
                server = new ServerSocket(Server.PORT);
                Utils.println("server is now listening on port " + Server.PORT);

                while (openServer) {
                    Socket socket = server.accept();
                    ServerConnection client = new ServerConnection(socket, idCounter.incrementAndGet());
                    clients.add(client);
                    new Thread(() -> client.open(clients)).start();
                }
            } catch (SocketException e) { Utils.println("server was closed"); }
            catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
        } else { Utils.println("server is already running"); }
    }

    /**
     * Closes the server and all {@link ServerConnection ServerConnections}.
     */
    public void close() {
        try {
            Utils.println("shutting server down");

            openServer = false;
            clients.forEach(client -> client.close("server is closing"));
            server.close();
        } catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Opens the command-line interface (CLI) and begins listening for user input.
     */
    public void cli() {
        try {
            openCli = true;
            Utils.println("server cli is open: ");
            Utils.println("type '@?' or '@help' to display commands");
            while (openCli) {
                Utils.print("# ");
                String[] message = keyboard.readLine().split(" ", 2);
                this.handle(message);
            }
            Utils.println("server cli was closed");
            if (server != null) this.close();
        } catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Handles a command entered by the user in the command-line interface (CLI).
     *
     * @param _message the command and any arguments entered by the user.
     * @throws IOException if an I/O error occurs.
     */
    public void handle(String[] _message) throws IOException {
        switch ((_message!=null)? _message[0] : "NULL_MESSAGE") {
            case "NULL_MESSAGE" -> Utils.println("error has occurred: message is null");
            case "" -> {}
            case "@?", "@help" -> Utils.println("""
                                commands:
                                @open           : open server
                                @close          : close server
                                @exit           : exit cli and close server
                                @clients        : get client list
                                @kick   [name]  : kick client from chat
                                @mute   [name]  : mute client
                                @unmute [name]  : unmute muted client""");
            case "@open" -> new Thread(this::open).start();
            case "@close" -> this.close();
            case "@exit" -> openCli = false;
            case "@clients" -> Utils.println("clients: " +
                    clients.stream()
                            .map(ServerConnection::getName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(", ")) );
            case "@kick" -> clients.stream()
                    .filter(client -> client.getName()!=null) // can technically remove this
                    .filter(client -> client.getName().equals(_message[1]))
                    .findAny()
                    .ifPresentOrElse(
                            client -> client.close("you have been kicked out"),
                            () -> Utils.println("client was not found") );
            case "@mute" -> clients.stream()
                    .filter(client -> client.getName()!=null)
                    .filter(client -> client.getName().equals(_message[1]))
                    .findAny()
                    .ifPresentOrElse(
                            client -> client.setMuted(true),
                            () -> Utils.println("client was not found") );
            case "@unmute" -> clients.stream()
                    .filter(client -> client.getName()!=null)
                    .filter(client -> client.getName().equals(_message[1])) // maybe add filter muted check
                    .findAny()
                    .ifPresentOrElse(
                            client -> client.setMuted(false),
                            () -> Utils.println("client was not found"));
            default -> Utils.println("unknown command, type '@?' or '@help' to display available commands");
        }
    }
}
