
/**
 * The com.chat.server package contains the resources needed to implement a TCP thread-safe server
 * that handles request from {@link com.chat.client client}s.
 *
 * <p>
 *     It consists of a {@link com.chat.server.Server Server} class that listens for incoming connections and
 *     a {@link com.chat.server.ServerConnection ServerConnection} class that handles the single client's requests.
 * </p>
 *
 * <p>
 *     It also features a command-line interface (CLI) in the {@link com.chat.server.Server Server} class
 *     that allows for more functionality when operating and managing the server.
 * </p>
 */
package com.chat.server;
