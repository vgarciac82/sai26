package com.syc.gestion.custom;

import java.sql.Connection;
import org.jdom.Element;
import com.syc.gestion.core.GestionException;
import java.util.Base64;

public interface XmlGeneratorInterface {

    public void usersProcess(Connection conn, Element el) throws GestionException;

    public void groupsProcess(Connection conn, Element el) throws GestionException;
}
