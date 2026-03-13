package com.syc.ldap;

import javax.naming.ldap.Control;
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.ldap.*;
import javax.naming.directory.*;

public class FastBindConnectionControl implements Control {

	public byte[] getEncodedValue() {
    	return null;
}
	public String getID() {  
	return "1.2.840.113556.1.4.1781";
}
	public boolean isCritical() {
	return true;
}

}



