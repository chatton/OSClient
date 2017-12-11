package ie.gmit.sw;

import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientRunner {
    public static void main(String[] args) throws IOException {

//        Scanner sc = new Scanner(new File("bot_data/user1.dat"));
        Scanner sc = new Scanner(System.in);
        final Client client = new Client("localhost", 9090, sc);
        client.connect();

        while (true) {
            client.requestMenu(); // prompt for menu every time.
            switch (sc.nextLine()) {
                case "1":
                    client.register();
                    break;
                case "2":
                    client.login();
                    break;
                case "3":
                    client.addFitnessRecord();
                    break;
                case "4":
                    client.addMealRecord();
                    break;
                case "5":
                    client.requestMealRecords();
                    break;
                case "6":
                    client.requestFitnessRecords();
                    break;
                case "7":
                    client.deleteRecord();
                    break;
                case "8":
                    client.exit();
                    return;
            }
        }

    }
}
