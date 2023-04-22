package edu.gatech.cs6310.authn.principals;

import java.security.Principal;
import java.util.Objects;


public class SecurityPrincipal implements Principal, java.io.Serializable{
    private String name;

    //create samplePrincipal with username

    public SecurityPrincipal(String name){
        if (name == null)
            throw new NullPointerException("Null username");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //Compares the Object with SamplePrincipal for equality
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (this == o) return true;
        SecurityPrincipal that = (SecurityPrincipal) o;
        return Objects.equals(name, that.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }

}
