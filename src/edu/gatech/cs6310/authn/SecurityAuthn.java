package edu.gatech.cs6310.authn;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.*;
import java.io.IOException;

public class SecurityAuthn {
    private static final String SECURITY_CONFIG_FILE = "security_jaas.config";
    public static void main(String[] args) throws IOException {
        //build LonginContext
        //LoginContext lc = null;
        /*URL resource = SecurityAuthn.class.getClassLoader().getResource(SECURITY_CONFIG_FILE);
        System.out.println(resource);
        if (resource == null) {
            System.out.println("Failed to load security config file");
            System.exit(1);
        }*/
        /*System.out.println(SecurityAuthn.class.getClass().getResourceAsStream(SECURITY_CONFIG_FILE));
        //String path = SecurityAuthn.class.getResourceAsStream(SECURITY_CONFIG_FILE).toString();
        String path = "src/main/java/edu/gatech/cs6310/authn/security_jaas.config";

        System.out.println("Read security config file: " + path);
*/
        System.setProperty("java.security.auth.login.config","/Users/estherwang/IdeaProjects/cs6310-Assignment5/src/edu/gatech/cs6310/resources/security_jaas.config");
        LoginContext lc = null;
        try {
            lc = new LoginContext("Sample", (CallbackHandler) new SecurityCallbackHandler());
        } catch (LoginException le) {
            System.err.println("fail to create Logincontext" + le.getMessage());
            System.exit(-1);
        } catch (SecurityException se) {
            System.err.println("fail to create LoginContext" + se.getMessage());
            System.exit(-1);
        }

        // 尝试 3 次
        int i;
        for (i = 0; i < 3; i++) {
            try {

                // attempt authentication
                lc.login();

                // if we return with no exception, authentication succeeded
                break;

            } catch (LoginException le) {

                System.err.println("Authentication failed:");
                System.err.println("  " + le.getMessage());
                try {
                    Thread.currentThread().sleep(3000);
                } catch (Exception e) {
                    // ignore
                }

            }
        }

        // did they fail three times?
        if (i == 3) {
            System.out.println("Sorry");
            System.exit(-1);
        }
        System.out.println("Authentication succeeded");
    }
}
