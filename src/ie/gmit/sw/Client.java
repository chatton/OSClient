package ie.gmit.sw;

import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.Scanner;

class Client {

    private final Scanner scanner;
    private final String address;
    private final int port;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    Client(final String address, final int port, final Scanner scanner) {
        this.address = address;
        this.port = port;
        this.scanner = scanner;
    }

    void connect() throws IOException {
        final Socket socket = new Socket(address, port);
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    private void sendCode(Code code) {
        sendMessage(new Message("", code));
    }

    private String readPassword() {
        final Console console = System.console();
        if (console != null) { // console will be null if not being read from actual console. EG in IDE or virtual console
            final char[] passChars = System.console().readPassword(); // disables echoing
            return new String(passChars);
        }
        return scanner.nextLine(); // just use the scanner instead if not executing in console.
    }

    boolean login() {
        sendCode(Code.LOGIN); // prompt server to start login process.

        final Message userNameMessage = readMessage();
        printServerMessage(userNameMessage); // enter user name.
        sendText(scanner.nextLine()); // send user name.

        final Message passwordMessage = readMessage();
        printServerMessage(passwordMessage); // enter password.

        sendText(readPassword()); // send password

        final Message status = readMessage();
        printServerMessage(status);
        return status.ok();
    }

    private void printServerMessage(Message msg) {
        System.out.println("server> " + msg.message());
    }

    boolean addMealRecord() {
        sendCode(Code.ADD_MEAL);
        Message msg = readMessage();
        printServerMessage(msg); // " enter mode "
        if (msg.code() == Code.FORBIDDEN) {
            return false; // can't add a record if not logged in.
        }

        sendText(scanner.nextLine()); // send meal description

        msg = readMessage();
        printServerMessage(msg); // "enter duration"
        sendText(scanner.nextLine()); // send duration

        final Message reply = readMessage();
        printServerMessage(reply);
        return reply.ok();

    }

    boolean requestFitnessRecords() {
        sendCode(Code.REQUEST_FITNESS);
        System.out.println(readMessage().message());
        return true;
    }

    boolean addFitnessRecord() {
        sendCode(Code.ADD_FITNESS);

        Message msg = readMessage();
        printServerMessage(msg); // " enter mode " or forbidden message
        if (msg.code() == Code.FORBIDDEN) {
            return false; // can't add a record if not logged in.
        }

        sendText(scanner.nextLine()); // send mode

        msg = readMessage();
        printServerMessage(msg); // "enter duration"
        sendText(scanner.nextLine()); // send duration
        final Message reply = readMessage();
        printServerMessage(reply);
        return reply.ok();
    }

    boolean register() {
        sendCode(Code.REGISTER); // start registration process.

        Message message = readMessage();
        printServerMessage(message); // "enter user name"
        sendText(scanner.nextLine()); // send user name

        message = readMessage();
        printServerMessage(message); // "user name accepted"

        if (!message.ok()) {
            return false; // registration failed.
        }

        message = readMessage();
        printServerMessage(message); // "enter password"

        sendText(readPassword()); // send password to server

        message = readMessage();
        printServerMessage(message); // "password accepted"

        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        printServerMessage(message); // "Enter address"
        sendText(scanner.nextLine());

        message = readMessage();
        printServerMessage(message); // "address okay"

        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        printServerMessage(message); // "Enter ppsn"
        sendText(scanner.nextLine()); // send ppsn

        message = readMessage();
        printServerMessage(message); // "valid ppsn"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        printServerMessage(message); // "enter height"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        printServerMessage(message); // "height okay"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        printServerMessage(message); // "enter weight"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        printServerMessage(message); // "weight okay"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        printServerMessage(message); // "enter age"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        printServerMessage(message); // "age okay"
        return message.ok() && readMessage().ok(); // second message is the final notification for if it was successfully added to db

    }

    void exit() {
        sendCode(Code.EXIT);
    }

    private void sendMessage(final Message message) {
        try {
            objOut.writeObject(message);
            objOut.flush();
        } catch (IOException e) {
            System.out.println("Error sending message. Error: " + e.getMessage());
        }
    }

    private Message readMessage() {
        try {
            return (Message) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading message: " + e.getMessage());
            return new Message("Couldn't read from server", Code.BAD);
        }
    }

    private void sendText(String text) {
        sendMessage(new Message(text, Code.MESSAGE));
    }

    void deleteRecord() {
        sendCode(Code.DELETE);
        Message msg = readMessage();
        printServerMessage(msg);
        if (msg.code() == Code.FORBIDDEN) {
            return;
        }
        sendText(scanner.nextLine());
        msg = readMessage();
        printServerMessage(msg);
    }

    boolean requestMenu() {
        sendCode(Code.MENU);
        final Message menuMsg = readMessage();
        System.out.println(menuMsg.message()); // don't display "server>" when printing menu.
        return menuMsg.ok();
    }

    void requestMealRecords() {
        sendCode(Code.REQUEST_MEAL);
        final Message msg = readMessage();
        System.out.println(msg.message()); //  error or actual records.
    }
}
