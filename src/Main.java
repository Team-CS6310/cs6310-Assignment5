import edu.gatech.cs6310.*;
import edu.gatech.cs6310.authn.SecurityAuthn;

import java.io.IOException;

public class Main {
    private static final String SECURITY_CONFIG_FILE = "security_jaas.config";
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the Grocery Express Delivery Service!");
        SecurityAuthn security = new SecurityAuthn();
        security.main(args);

        DeliveryService simulator = new DeliveryService();
        simulator.commandLoop();
    }
}
