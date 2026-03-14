package com.syc.ldap;

import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Base64;

public class LdapFastBind {

    public Hashtable env = null;

    public LdapContext ctx = null;

    public Control[] connCtls = null;

    public LdapFastBind(LdapContext pctx) {
        this.ctx = pctx;
    }

    public boolean Authenticate(String username, String password) throws Exception {
        boolean auth = false;
        try {
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, username);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(connCtls);
            System.out.println(username + " is authenticated");
            auth = true;
        } catch (AuthenticationException e) {
            System.out.println(username + " is not authenticated");
            auth = false;
            //throw new AuthenticationException();
        } catch (NamingException e) {
            System.out.println(username + " is not authenticated");
            auth = false;
            //throw new NamingException();
        }
        return auth;
    }

    public void finito() {
        try {
            ctx.close();
            System.out.println("Context is closed");
        } catch (NamingException e) {
            System.out.println("Context close failure " + e);
        }
    }
}
