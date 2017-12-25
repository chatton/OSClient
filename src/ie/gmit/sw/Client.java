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
        serverMessage(userNameMessage); // enter user name.
        sendText(scanner.nextLine()); // send user name.

        final Message passwordMessage = readMessage();
        serverMessage(passwordMessage); // enter password.

        sendText(readPassword()); // send password

        final Message status = readMessage();
        serverMessage(status);
        return status.ok();
    }

    private void serverMessage(Message msg) {
        System.out.println("server> " + msg.message());
    }

    boolean addMealRecord() {
        sendCode(Code.ADD_MEAL);
        Message msg = readMessage();
        serverMessage(msg); // " enter mode "
        if (msg.code() == Code.FORBIDDEN) {
            return false; // can't add a record if not logged in.
        }

        sendText(scanner.nextLine()); // send meal description

        msg = readMessage();
        serverMessage(msg); // "enter duration"
        sendText(scanner.nextLine()); // send duration

        return readMessage().ok();

    }

    boolean requestFitnessRecords() {
        sendCode(Code.REQUEST_FITNESS);
        System.out.println(readMessage().message());
        return true;
    }

    boolean addFitnessRecord() {
        sendCode(Code.ADD_FITNESS);

        Message msg = readMessage();
        serverMessage(msg); // " enter mode "
        if (msg.code() == Code.FORBIDDEN) {
            return false; // can't add a record if not logged in.
        }

        sendText(scanner.nextLine()); // send mode

        msg = readMessage();
        serverMessage(msg); // "enter duration"
        sendText(scanner.nextLine()); // send duration

        return readMessage().ok();
    }

    boolean register() {
        sendCode(Code.REGISTER); // start registration process.

        Message message = readMessage();
        serverMessage(message); // "enter user name"
        sendText(scanner.nextLine()); // send user name

        message = readMessage();
        serverMessage(message); // "user name accepted"

        if (!message.ok()) {
            return false; // registration failed.
        }

        message = readMessage();
        serverMessage(message); // "enter password"

        sendText(readPassword()); // send password to server

        message = readMessage();
        serverMessage(message); // "password accepted"

        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        serverMessage(message); // "Enter address"
        sendText(scanner.nextLine());

        message = readMessage();
        serverMessage(message); // "address okay"

        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        serverMessage(message); // "Enter ppsn"
        sendText(scanner.nextLine()); // send ppsn

        message = readMessage();
        serverMessage(message); // "valid ppsn"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        serverMessage(message); // "enter height"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        serverMessage(message); // "height okay"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        serverMessage(message); // "enter weight"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        serverMessage(message); // "weight okay"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        serverMessage(message); // "enter age"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        serverMessage(message); // "age okay"
        return message.ok() && readMessage().ok(); // second message is the final notification for if it was successfully added to db

    }

    void exit() {
        sendCode(Code.EXIT);
    }

    private void sendMessage(Message message) {
        try {
            objOut.writeObject(message);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message readMessage() {
        try {
            return (Message) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new Message("Couldn't read from server", Code.BAD);
        }
    }

    private void sendText(String text) {
        sendMessage(new Message(text, Code.MESSAGE));
    }

    void deleteRecord() {
        sendCode(Code.DELETE);
        Message msg = readMessage();
        serverMessage(msg);
        if (msg.code() == Code.FORBIDDEN) {
            return;
        }
        sendText(scanner.nextLine());
        msg = readMessage();
        serverMessage(msg);
    }

    boolean requestMenu() {
        sendCode(Code.MENU);
        final Message menu = readMessage();
        System.out.println(menu.message());
        return menu.ok();
    }

    void requestMealRecords() {
        sendCode(Code.REQUEST_MEAL);
        final Message msg = readMessage();
        System.out.println(msg.message()); //  error or actual records.
    }
}
