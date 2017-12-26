package ie.gmit.sw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FakeClientsRunner {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        if (args.length != 0) { // "18.217.200.242"
            hostName = args[0]; // hostname provided
        }

        final File dir = new File("bot_data");
        final File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                final Scanner sc = new Scanner(f);
                final Client client = new Client(hostName, 9090, sc);
                client.connect();
                boolean go = true;
                while (go) {
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
                            go = false;
                    }
                }
            }
        }
    }
}
