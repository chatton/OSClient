package ie.gmit.sw;

import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Scanner scanner;
    private final String address;
    private final int port;
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public Client(String address, int port, final Scanner scanner) {
        this.address = address;
        this.port = port;
        this.scanner = scanner;
    }

    public void connect() throws IOException {
        socket = new Socket(address, port);
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    public void sendCode(Code code) {
        sendMessage(new Message(null, code));
    }

    public boolean login() {
        sendCode(Code.LOGIN); // prompt server to start login process.

        final Message userNameMessage = readMessage();
        System.out.println(serverMessage(userNameMessage)); // enter user name.
        sendText(scanner.nextLine()); // send user name.

        final Message passwordMessage = readMessage();
        System.out.println(serverMessage(passwordMessage)); // enter password.
        sendText(scanner.nextLine()); // send password
        Message status = readMessage();
        System.out.println(status.message());
        return status.ok();
    }

    private String serverMessage(Message msg) {
        return "server> " + msg.message();
    }

    public boolean addMealRecord() {
        sendCode(Code.ADD_MEAL);
        Message msg = readMessage();
        System.out.println(serverMessage(msg)); // " enter mode "
        if (msg.code() == Code.FORBIDDEN) {
            return false; // can't add a record if not logged in.
        }

        sendText(scanner.nextLine()); // send meal description

        msg = readMessage();
        System.out.println(serverMessage(msg)); // "enter duration"
        sendText(scanner.nextLine()); // send duration

        return readMessage().ok();

    }

    public boolean requestFitnessRecords() {
        sendCode(Code.REQUEST_FITNESS);
        System.out.println(readMessage().message());
        return true;
    }

    public boolean addFitnessRecord() {
        sendCode(Code.ADD_FITNESS);

        Message msg = readMessage();
        System.out.println(serverMessage(msg)); // " enter mode "
        if (msg.code() == Code.FORBIDDEN) {
            return false; // can't add a record if not logged in.
        }

        sendText(scanner.nextLine()); // send mode

        msg = readMessage();
        System.out.println(serverMessage(msg)); // "enter duration"
        sendText(scanner.nextLine()); // send duration

        return readMessage().ok();
    }

    public boolean register() {
        sendCode(Code.REGISTER); // start registration process.

        Message message = readMessage();
        System.out.println(serverMessage(message)); // "enter user name"
        sendText(scanner.nextLine()); // send user name

        message = readMessage();
        System.out.println(serverMessage(message)); // "user name accepted"

        if (!message.ok()) {
            return false; // registration failed.
        }

        message = readMessage();
        System.out.println(serverMessage(message)); // "enter password"
        sendText(scanner.nextLine()); // send password to server

        message = readMessage();
        System.out.println(serverMessage(message)); // "password accepted"

        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        System.out.println(serverMessage(message)); // "Enter ppsn"
        sendText(scanner.nextLine()); // send ppsn

        message = readMessage();
        System.out.println(serverMessage(message)); // "valid ppsn"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        System.out.println(serverMessage(message)); // "enter height"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        System.out.println(serverMessage(message)); // "height okay"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        System.out.println(serverMessage(message)); // "enter weight"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        System.out.println(serverMessage(message)); // "weight okay"
        if (!message.ok()) {
            return false;
        }

        message = readMessage();
        System.out.println(serverMessage(message)); // "enter age"
        sendText(scanner.nextLine()); // send height

        message = readMessage();
        System.out.println(serverMessage(message)); // "age okay"
        return message.ok() && readMessage().ok(); // second message is the final notification for if it was successfully added to db

    }

    public void exit() {
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


    public void deleteRecord() {
        sendCode(Code.DELETE);
        Message msg = readMessage();
        System.out.println(serverMessage(msg));
        if (msg.code() == Code.FORBIDDEN) {
            return;
        }
        sendText(scanner.nextLine());
        msg = readMessage();
        System.out.println(serverMessage(msg));
    }

    public boolean requestMenu() {
        sendCode(Code.MENU);
        Message menu = readMessage();
        System.out.println(menu.message());
        return menu.ok();
    }

    public void requestMealRecords() {
        sendCode(Code.REQUEST_MEAL);
        Message msg = readMessage();
        System.out.println(msg.message()); //  error or actual records.
    }
}
