package com.creugrogasoft.biphone;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;
 
//Imports for changing password
import javax.naming.directory.ModificationItem;
import javax.naming.directory.BasicAttribute;
import javax.naming.ldap.StartTlsResponse;
import javax.naming.ldap.StartTlsRequest;
import javax.net.ssl.*;
 
//******************************************************************************
//**  ActiveDirectory
//*****************************************************************************/
/**
 *   Provides static methods to authenticate users, change passwords, etc. 
 *
 ******************************************************************************/
 
public class ActiveDirectory {
 
    private static String[] userAttributes = {
        "distinguishedName","cn","name","uid",
        "sn","givenname","memberOf","samaccountname",
        "userPrincipalName"
    };
 
    private ActiveDirectory(){}
 
 
  //**************************************************************************
  //** getConnection
  //*************************************************************************/
  /**  Used to authenticate a user given a username/password. Domain name is
   *   derived from the fully qualified domain name of the host machine.
   */
    public static LdapContext getConnection(String username, String password) throws NamingException {
        return getConnection(username, password, null, null);
    }
 
 
  //**************************************************************************
  //** getConnection
  //*************************************************************************/
  /**  Used to authenticate a user given a username/password and domain name.
   */
    public static LdapContext getConnection(String username, String password, String domainName) throws NamingException {
        return getConnection(username, password, domainName, null);
    }
 
 
  //**************************************************************************
  //** getConnection
  //*************************************************************************/
  /** Used to authenticate a user given a username/password and domain name.
   *  Provides an option to identify a specific a Active Directory server.
   */
    public static LdapContext getConnection(String username, String password, String domainName, String serverName) throws NamingException {
 
        if (domainName==null){
            try{
                String fqdn = java.net.InetAddress.getLocalHost().getCanonicalHostName();
                if (fqdn.split("\\.").length>1) domainName = fqdn.substring(fqdn.indexOf(".")+1);
            }
            catch(java.net.UnknownHostException e){}
        }
         
        //System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName);
 
        if (password!=null){
            password = password.trim();
            if (password.length()==0) password = null;
        }
 
        //bind by using the specified username/password
        Hashtable props = new Hashtable();
        String principalName = username + "@" + domainName;
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        if (password!=null) props.put(Context.SECURITY_CREDENTIALS, password);
 
 
        String ldapURL = "ldap://" + ((serverName==null)? domainName : serverName + "." + domainName) + '/';
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        try{
            return new InitialLdapContext(props, null);
        }
        catch(javax.naming.CommunicationException e){
            throw new NamingException("Failed to connect to " + domainName + ((serverName==null)? "" : " through " + serverName));
        }
        catch(NamingException e){
            throw new NamingException("Failed to authenticate " + username + "@" + domainName + ((serverName==null)? "" : " through " + serverName));
        }
    }
 
 
  //**************************************************************************
  //** getUser
  //*************************************************************************/
  /** Used to check whether a username is valid.
   *  @param username A username to validate (e.g. "peter", "peter@acme.com",
   *  or "ACME\peter").
   */
    public static User getUser(String username, LdapContext context) throws NamingException {
            String domainName = null;
            if (username.contains("@")){
                username = username.substring(0, username.indexOf("@"));
                domainName = username.substring(username.indexOf("@")+1);
            }
            else if(username.contains("\\")){
                username = username.substring(0, username.indexOf("\\"));
                domainName = username.substring(username.indexOf("\\")+1);
            }
            else{
                String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
                if (authenticatedUser.contains("@")){
                    domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
                }
            }
 
            if (domainName!=null){
                String principalName = username + "@" + domainName;
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SUBTREE_SCOPE);
                controls.setReturningAttributes(userAttributes);
                NamingEnumeration<SearchResult> answer = context.search( toDC(domainName), "(& (userPrincipalName="+principalName+")(objectClass=user))", controls);
                if (answer.hasMore()) {
                    Attributes attr = answer.next().getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    if (user!=null) return new User(attr);
                }
            }
        return null;
    }
 
 
  //**************************************************************************
  //** getUsers
  //*************************************************************************/
  /** Returns a list of users in the domain.
   */
    public static User[] getUsers(LdapContext context) throws NamingException {
 
        java.util.ArrayList<User> users = new java.util.ArrayList<User>();
        String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
        if (authenticatedUser.contains("@")){
            String domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            controls.setReturningAttributes(userAttributes);
            NamingEnumeration answer = context.search( toDC(domainName), "(objectClass=user)", controls);
            try{
                while(answer.hasMore()) {
                    Attributes attr = ((SearchResult) answer.next()).getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    if (user!=null){
                        users.add(new User(attr));
                    }
                }
            }
            catch(Exception e){}
        }
        return users.toArray(new User[users.size()]);
    }
 
 
    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if(token.length()==0)   continue;   // defensive check
            if(buf.length()>0)  buf.append(",");
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }
 
}