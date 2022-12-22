# Java Server-Client TCP Chat

## Index
- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Running the Server](#running-the-server)
    - [Running the Client](#running-the-client)
- [Additional Project Characteristics](#additional-project-characteristics)
    - [Thread Safety](#thread-safety)
    - [Event Programming](#event-programming)
    - [Functional Programming](#functional-programming)
- [Why Java 19?](#why-java-19)
- [Documentation](#documentation)
- [License](#license)

## Introduction

This is a simple chat application implemented using the Java programming language and the TCP protocol. 
The chat server allows multiple clients to connect and exchange messages with each other in real-time.

Furthermore, the server features a command-line interface (CLI) that increases practicality, 
facilitates administration and allows for more features and functionality.

The client is characterized by a user interface (UI) implemented with the JavaFX library.

## Features

- Multiple clients can connect to the server simultaneously
- Clients can send public messages to the server, which are then broadcasted to all connected clients
- Clients can communicate privately too by sending private messages
- Clients can change their username at any time
- The server can supervise and manage the connected clients via some commands of the CLI, such as @kick or @mute

## Getting Started

### Prerequisites

- [Java](https://www.oracle.com/java/technologies/downloads/#java19) version 19 or higher ([understand why](#why-java-19))
- An IDE for Java development ([IntelliJ IDEA](https://www.jetbrains.com/idea/download/) is recommended)

### Running the Server

1. Open the `Main.java` file in your IDE
2. Run the `com.chat.MainServer` class as a Java application
3. The server will start the server CLI, type `@help` to show all commands
4. Type `@open` to open the server, it will start listening for incoming connections on the default port (7777)
5. Type `@close` if you want to temporarily close the server
6. When you are ready, you can exit the CLI with the `@exit` command

### Running the Client

1. Open the `Main.java` file in your IDE
2. Run the `MainApplication` class as a Java application
3. Enter the server IP address when prompted (e.g `localhost`, `127.0.0.1`)
4. Enter a username when prompted (`PUBLIC` and ` ` usernames will ***not*** be accepted)
5. If no error message is displayed, all the clients connected to the server should now be visible
6. The client can message another client by selecting its name on the left, writing a message in the text area and hitting the send button
7. The client can message all clients by selecting the `PUBLIC` chat
8. The client can change its name at any time right-clicking on your name displayed in the navbar

## Additional Project Characteristics

### Thread Safety

This chat project is considered to be completely thread safe by its contributors.

To achieve this some different [Collection](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html) 
implementations have been used such as [ConcurrentSkipListSet](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListSet.html)
in the server and [Collections.synchronizedList](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-)
in the client.

### Event Programming

Since the client side of the project handles both the messages received from the server and
User inputs, event programming is the perfect paradigm to base the client application on.

It consists of attaching events to the client that are then called when the event is emitted,
similar to how [Vue](https://vuejs.org/) [lifecycle hooks](https://vuejs.org/guide/essentials/lifecycle.html) work.

Some example events emitted by the client might be onClientReceivedMessage, onClientSendMessage, onConnectionSuccessful, ecc...

### Functional Programming

Functional programming is a programming paradigm that was first introduced in Java with the new set of functional interfaces 
in the [java.utils.function](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html) package.

In this project its primary employment is to enable the [stream api](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html), 
a set of methods that can be applied to any implementation of the 
[Collection](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html) interface.

These methods allow to query the Collection in a short, concise and more comprehensible syntax compared to a standard `for` loop.

Take into consideration that __pure functional programming in java is impossible__, this is because although
functional interfaces give the impression that we are passing an interface or even a function as argument to a method
that is not the case.

What is really being passed into the supposed method is an anonymous class that implements the 
functional interface and overrides its method.

## Why Java 19?

This project was seen by the main contributors as a great opportunity to explore new features
and aspects of the Java language, such as
[records](https://docs.oracle.com/en/java/javase/14/language/records.html),
[method references](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html),
[functional interfaces](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html),
[stream api](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
and improved syntax like the `var` keyword and enhanced `switch` statements.

Moreover, the project includes more complex programming patterns like [factories](https://en.wikipedia.org/wiki/Factory_method_pattern).

Since some of these features where introduced as late as Java 17 or 18 in some cases, 
we opted for the latest release to this date (Java 19) as the recommended Java version.

Please understand that this project is not meant for practical use, hence the odd recommended JDK version.

### What about LTS?

Java 17 is supported too, but was found unreliable since some features are only present in the 
[preview](https://docs.oracle.com/en/java/javase/17/language/preview-language-and-vm-features.html) version
and might work as they might not.

When running the project with Java 17 consider using the --enable-preview param via cmd or changing your
IDE configuration accordingly.

## Documentation

Javadoc documentation for the source code is available at: https://chat-javadoc.web.app/

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
