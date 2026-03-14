package com.syc.ldap;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.util.Base64;

public class UsuarioLDAP {

    private String givenname = null;

    private String sn = null;

    private String mail = null;

    private String dominio = null;

    private String userattr = null;

    private String base = null;

    private String memberof = null;

    private String provider_url = null;

    private String nopriv = null;

    private String noprivenfx = null;

    private String pasincoodes = null;

    private String ctainactpasexp = null;

    private boolean use_password = false;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getUserattr() {
        return userattr;
    }

    public void setUserattr(String userattr) {
        this.userattr = userattr;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getMemberof() {
        return memberof;
    }

    public void setMemberof(String memberof) {
        this.memberof = memberof;
    }

    Control[] connCtls = new Control[] { new FastBindConnectionControl() };

    private void putgivenname(String pgivenname) {
        this.givenname = pgivenname;
    }

    private void putsn(String psn) {
        this.sn = psn;
    }

    public String getgivenname() {
        return this.givenname;
    }

    public String getsn() {
        return this.sn;
    }

    public UsuarioLDAP() throws IOException {
        use_password = true;
        try {
            //Ethiel 10/05/2007 codigo para leer url desde ldap.xml
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File("ldap.xml"));
            // normalize text representation
            doc.getDocumentElement().normalize();
            NodeList listOfUrls = doc.getElementsByTagName("provider");
            //deje el for por si hubiera mas de un url, considerar si no se usa
            for (int s = 0; s < listOfUrls.getLength(); s++) {
                Node firstProviderNode = listOfUrls.item(s);
                if (firstProviderNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstProviderElement = (Element) firstProviderNode;
                    NodeList firstURLList = firstProviderElement.getElementsByTagName("url");
                    Element firstURLElement = (Element) firstURLList.item(0);
                    NodeList textURLList = firstURLElement.getChildNodes();
                    provider_url = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("userattr");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    userattr = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("dominio");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    dominio = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("base");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    base = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("memberof");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    memberof = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("nopriv");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    nopriv = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("noprivenfx");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    noprivenfx = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("pasincoodes");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    pasincoodes = ((Node) textURLList.item(0)).getNodeValue().trim();
                    firstProviderElement = (Element) firstProviderNode;
                    firstURLList = firstProviderElement.getElementsByTagName("ctainactpasexp");
                    firstURLElement = (Element) firstURLList.item(0);
                    textURLList = firstURLElement.getChildNodes();
                    ctainactpasexp = ((Node) textURLList.item(0)).getNodeValue().trim();
                }
                //end of if clause
            }
            //end of for loop with s var
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean validaUsuario(Connection conn, String u_login, String passwd) throws Exception {
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.PROVIDER_URL, provider_url);
            LdapContext ctx = null;
            try {
                ctx = new InitialLdapContext(env, connCtls);
            } catch (NamingException e) {
                System.out.println("Naming exception " + e);
            }
            boolean IsAuthenticated = false;
            LdapFastBind autctx = new LdapFastBind(ctx);
            IsAuthenticated = autctx.Authenticate(dominio + "\\" + u_login, passwd);
            //Si el usuario es autenticado hay que revisar que pertenezca al grupo que definan, POR EL MOMENTO NO
            //tambien hay que ver si el usuario ya esta en la BD local y si no hay que agregarlo pero por el momento tampoco pues falta informacion
            /*		
			if (IsAuthenticated){
				//Viendo si el usuario existe en BD local
				Usuario u = new Usuario();
				u.setLogin(u_login);
				u = UsuarioManager.select(conn, u);
			
				
				if(u==null){
					try{
						Attributes matchAttrs = new BasicAttributes(true); // ignore case
						matchAttrs.put(new BasicAttribute(userattr, u_login));
						SearchControls scTemp = new SearchControls() ;
						scTemp.setSearchScope(SearchControls.SUBTREE_SCOPE);
						scTemp.setReturningAttributes(new String [] { "givenName","sn","mail"}) ;
						NamingEnumeration ne1=autctx.ctx.search(base, userattr+"="+u_login, scTemp);
						
						for(NamingEnumeration ne=autctx.ctx.search(base, userattr+"="+u_login, scTemp); 
						ne.hasMore();
						) {
							SearchResult srTemp = (SearchResult) ne.next() ;
							Attributes attrsTemp = srTemp.getAttributes() ;
				
							System.out.println("  givenname: '" + attrsTemp.get("givenname").get() + "'");
							this.putgivenname(attrsTemp.get("givenname").get().toString());
							System.out.println("  sn: '" + attrsTemp.get("sn").get() + "'") ;
							this.putsn(attrsTemp.get("sn").get().toString());
							System.out.println("  mail: '" + attrsTemp.get("mail").get() + "'") ;
							this.setMail(attrsTemp.get("mail").get().toString());
							
							//Ethiel, aqui deberiamos crear el usuario pero por el momento no lo haremos ya que no estan todos 
							//los datos necesarios en el LDAP, como el area, nivel, etc se le informa al cliente que debera agregarlos manualmente
							
							//Ethiel, por el momento no hay grupos en LDAP de Turirmo, esto no se usara, lo edjo comentado como referencia
							
							String memberofldap =null;
							boolean esmiembro=false;
							try{memberofldap = new String(attrsTemp.get("memberOf").get().toString());}
							catch(Exception e){
								System.out.println("No es parte de ningun grupo ");
								IsAuthenticated=false; e.printStackTrace(System.out);
								esmiembro=false;
								throw new UsuarioLDAPException(this.nopriv);//"La cuenta no tiene privilegios para el grupo FORIMAX en LDAP, consulte al administrador."
							}
							memberofldap=memberofldap.toLowerCase().trim();
							//saber si el usuario es miembro del grupo fortimaxusers
							esmiembro=memberof.equals(memberofldap);
							if(!esmiembro){
								System.out.println("No es parte del grupo fortimax");
								throw new UsuarioLDAPException(this.noprivenfx);//"La cuenta no tiene privilegios para el grupo FORIMAX en LDAP, consulte al administrador."
								//return false;
							}
						}
	
					}
					catch(PartialResultException e){
						return true;
					} //no hace nada, sirve para detener el hasmore
				}
			}*/
            return IsAuthenticated;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new Exception(e.getMessage());
            //return false;
        }
    }
}
