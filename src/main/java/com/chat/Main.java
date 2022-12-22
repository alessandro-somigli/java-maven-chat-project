package com.chat;

import com.chat.client.ui.ClientInterface;
import com.chat.server.Server;

/**
 * The Main class is the entry point for the application, it starts both the
 * {@link Server} and the {@link ClientInterface} in separate {@link Thread}s.
 */
public class Main {

    /**
     * The com.chat.MainServer class contains the main method for starting the {@link Server}.
     */
    public static class MainServer {
        public static void main(String[] args) { new Server().cli(); }
    }

    /**
     * The MainApplication class contains the main method for starting the {@link ClientInterface}.
     */
    public static class MainApplication {
        public static void main(String[] args) { new ClientInterface().run(); }
    }

    /**
     * The main method for the application, it starts both the {@link Server} and the {@link ClientInterface} in
     * separate {@link Thread}s.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Thread(() -> MainServer.main(null)).start();
        new Thread(() -> MainApplication.main(null)).start();
    }
}
