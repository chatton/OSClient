package ie.gmit.sw;

import java.io.IOException;
import java.util.Scanner;

public class ClientRunner {
    public static void main(String[] args) throws IOException {

        String hostName = "localhost";
        if (args.length != 0) { // "18.217.200.242"
            hostName = args[0]; // hostname provided
        }

//        Scanner sc = new Scanner(new File("bot_data/user1.dat"));
        Scanner sc = new Scanner(System.in);
        final Client client = new Client(hostName, 9090, sc);
        client.connect();

        while (true) {
            client.requestMenu(); // prompt for menu every time.
            switch (sc.nextLine()) {
                case ActionCodes.REGISTER:
                    client.register();
                    break;
                case ActionCodes.LOGIN:
                    client.login();
                    break;
                case ActionCodes.ADD_FITNESS_RECORD:
                    client.addFitnessRecord();
                    break;
                case ActionCodes.ADD_MEAL_RECORD:
                    client.addMealRecord();
                    break;
                case ActionCodes.REQUEST_MEAL_RECORDS:
                    client.requestMealRecords();
                    break;
                case ActionCodes.REQUEST_FITNESS_RECORDS:
                    client.requestFitnessRecords();
                    break;
                case ActionCodes.DELETE_RECORD:
                    client.deleteRecord();
                    break;
                case ActionCodes.EXIT:
                    client.exit();
                    return; // exits client application.
            }
        }

    }
}
