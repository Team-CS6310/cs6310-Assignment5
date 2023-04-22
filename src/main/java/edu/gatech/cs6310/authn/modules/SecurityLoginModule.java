package edu.gatech.cs6310.authn.modules;

import edu.gatech.cs6310.authn.principals.SecurityPrincipal;
import java.util.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
public class SecurityLoginModule implements LoginModule{
    //initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;

    //configurable option
    private boolean debug = false;
    private boolean succeeded = false;
    private boolean commitSucceeded = false;
    private boolean isLoginIn = false;

    //username and password
    private String username;
    private char[] password;


    private SecurityPrincipal userPrincipal;

    public void initialize(Subject subject,
                          CallbackHandler callbackHandler,
                          Map<java.lang.String, ?> sharedState,
                          Map<java.lang.String,?> options){
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        //this.username = (String) options.get("username");
        //this.username = (String) options.get("password");
        // initialize any configured options
        debug = "true".equalsIgnoreCase((String)options.get("debug"));

    }

    private boolean canLogin(String username, char[] password){
        return username.equals("testUser") && password.equals("test");
    }

    //login entrance, whether success
    public boolean login() throws LoginException{
        //prompt for a user name and password
        if (callbackHandler == null)
            throw new LoginException("Error: no authentication information");

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("user name: ");
        callbacks[1] = new PasswordCallback("password: ", false);
        //callbacks[0] = new NameCallback()
        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            if (tmpPassword == null) {
                // treat a NULL password as an empty password
                tmpPassword = new char[0];
            }
            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0,
                    password, 0, tmpPassword.length);
            ((PasswordCallback)callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                    " not available to garner authentication information " +
                    "from the user");
        }
        // print debugging information
        if (debug) {
            System.out.println("\t\t[SampleLoginModule] " +
                    "user entered user name: " +
                    username);
            System.out.print("\t\t[SampleLoginModule] " +
                    "user entered password: ");
            for (int i = 0; i < password.length; i++)
                System.out.print(password[i]);
            System.out.println();
        }
/*
        if (canLogin(this.username, this.password)){
            //if login success, creat UsernamePrincipal
            this.principal = new SecurityPrincipal(username);
            //login status is true
            isLoginIn = true;
            return true;
        }
        return false;*/


        // verify the username/password
        boolean usernameCorrect = false;
        boolean passwordCorrect = false;
        if (username.equals("testUser"))
            usernameCorrect = true;
        if (usernameCorrect &&
                password.length == 12 &&
                password[0] == 't' &&
                password[1] == 'e' &&
                password[2] == 's' &&
                password[3] == 't' &&
                password[4] == 'P' &&
                password[5] == 'a' &&
                password[6] == 's' &&
                password[7] == 's' &&
                password[8] == 'w' &&
                password[9] == 'o' &&
                password[10] == 'r' &&
                password[11] == 'd') {

            // authentication succeeded!!!
            passwordCorrect = true;
            if (debug)
                System.out.println("\t\t[SampleLoginModule] " +
                        "authentication succeeded");
            succeeded = true;
            return true;
        } else {

            // authentication failed -- clean out state
            if (debug)
                System.out.println("\t\t[SampleLoginModule] " +
                        "authentication failed");
            succeeded = false;
            username = null;
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
            if (!usernameCorrect) {
                throw new FailedLoginException("User Name Incorrect");
            } else {
                throw new FailedLoginException("Password Incorrect");
            }
        }
    }

    //After login success
    /*
    public boolean commit() throws LoginException{
        if (subject == null){
            return false;
        }
        if (principal == null){
            return false;
        }
        if (!isLoginIn){
            return false;
        }
        //add new created principal to subject, then that subject has the principal right
        subject.getPrincipals().add(principal);
        return true;
    }
*/
    public boolean commit() throws LoginException {
        if (succeeded == false) {
            return false;
        } else {
            // add a Principal (authenticated identity)
            // to the Subject

            // assume the user we authenticated is the SamplePrincipal
            userPrincipal = new SecurityPrincipal(username);
            if (!subject.getPrincipals().contains(userPrincipal))
                subject.getPrincipals().add(userPrincipal);

            if (debug) {
                System.out.println("\t\t[SampleLoginModule] " +
                        "added SamplePrincipal to Subject");
            }

            // in any case, clean out state
            username = null;
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;

            commitSucceeded = true;
            return true;
        }
    }

    //end login
    public boolean abort() throws  LoginException {
        return logout();
    }

    //logout
    public boolean logout() throws LoginException {
        if (!isLoginIn){
            return false;
        }
        //remove principal from subject
        if (subject != null && userPrincipal != null){
            subject.getPrincipals().remove(userPrincipal);
        }
        userPrincipal = null;
        subject = null;
        isLoginIn = false;
        return true;
    }




}
