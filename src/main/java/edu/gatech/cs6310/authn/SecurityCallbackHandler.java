package edu.gatech.cs6310.authn;

import javax.security.auth.callback.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class SecurityCallbackHandler implements CallbackHandler{
    /*implements 2 types of Callbacks:
    NameCallback to remind user to input username
    PasswordCallback to remind user to input password
     */
    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException{

        for (int i = 0; i <callbacks.length;i++){
            if (callbacks[i] instanceof NameCallback){
                //prompt the user for a username
                NameCallback namecallback = (NameCallback) callbacks[i];
                System.err.println(namecallback.getPrompt());
                System.err.flush();
                namecallback.setName((new BufferedReader(new InputStreamReader(System.in))).readLine());

            } else if (callbacks[i] instanceof PasswordCallback){
                PasswordCallback passwordCallback = (PasswordCallback) callbacks[i];
                System.err.print(passwordCallback.getPrompt());
                System.err.flush();
                passwordCallback.setPassword(System.console().readPassword());

            } else {
                throw new UnsupportedCallbackException
                        (callbacks[i], "Unrecognized Callback");
            }
        }


    }

}
