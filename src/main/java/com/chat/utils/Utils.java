package com.chat.utils;

import com.chat.utils.message.Message;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Utils is a utility class that provides all the utility methods needed.
 * Utility methods are static by nature.
 */
public class Utils {
    private static final File log = new File("log.txt");

    private static final JsonMapper jsonMapper = new JsonMapper();
    private static final BufferedWriter consoleWriter = new BufferedWriter(new OutputStreamWriter(System.out));
    private static BufferedWriter logWriter;

    static {
        try {
            if ( !log.createNewFile() ) new FileWriter(log).close();

            logWriter = new BufferedWriter(new FileWriter(log, true));
        } catch (IOException e) {
            Utils.logln("exception: IO exception occurred; " +  e.getMessage());
        }
    }

    /**
     * Deserializes a {@link Message} into a {@link String}.
     *
     * @param _message The {@link Message} to deserialize from JSON to {@link String}.
     * @return The deserialized {@link String}.
     */
    public static @Nullable String deserializeJson(Message _message) {
        try { return jsonMapper.writeValueAsString(_message); }
        catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
        return null;
    }

    /**
     * Serializes a {@link String} into a {@link Message}.
     *
     * @param _string The {@link String} to serialize from JSON to a {@link Message}.
     * @return The serialized {@link Message}.
     */
    public static @Nullable Message serializeJson(String _string) {
        try { return jsonMapper.readValue(_string, Message.class); }
        catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
        return null;
    }

    /**
     * Prints a {@link String} on the console. <br>
     * Replaces {@link System#out#print(String)} to increase performance.
     *
     * @param _string The {@link String} to print on the console.
     */
    public static void print(String _string) {
        try {
            consoleWriter.write(_string);
            consoleWriter.flush();
        } catch (NullPointerException e) { Utils.logln("exception: tried to print null value. message: " + e.getMessage()); }
        catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Prints a {@link String} on the console and goes to new line. <br>
     * Replaces {@link System#out#println(String)} to increase performance.
     *
     * @param _string The {@link String} to print on the console.
     */
    public static void println(String _string) {
        try {
            consoleWriter.write(_string + '\n');
            consoleWriter.flush();
        } catch (NullPointerException e) { Utils.logln("exception: tried to print null value. message: " + e.getMessage()); }
        catch (IOException e) { Utils.logln("exception: IO exception occurred; " +  e.getMessage()); }
    }

    /**
     * Writes a {@link String} to the <strong>log.txt</strong> file.
     *
     * @param _string The {@link String} to write to the <strong>log.txt</strong> file.
     */
    public static void log(String _string) {
        try {
            logWriter.write(_string);
            logWriter.flush();
        } catch (NullPointerException | IOException e) { e.printStackTrace(); }
    }

    /**
     * Writes a {@link String} to the <strong>log.txt</strong> file and goes to new line.
     *
     * @param _string The {@link String} to write to the <strong>log.txt</strong> file.
     */
    public static void logln(String _string) {
        try {
            logWriter.write(_string + '\n');
            logWriter.flush();
        } catch (NullPointerException | IOException e) { e.printStackTrace(); }
    }
}