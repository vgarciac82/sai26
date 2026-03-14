package com.syc.xml;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import org.jdom.JDOMException;
import com.syc.gestion.core.BitacoraCaso;
import com.syc.gestion.core.BitacoraTotal;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.TipoCaso;
import com.syc.gestion.core.TipoCasoManager;
import java.util.Base64;

public class SearchXml2 {

    public SearchXml2() {
    }

    public String find(Connection conn, String AttrName, int idCaso) throws GestionException {
        Caso c = new Caso();
        try {
            TipoCaso tc = new TipoCaso();
            tc.setIdTC(8);
            tc = TipoCasoManager.select(conn, tc);
            c.setIdCaso(idCaso);
            c.setTipoCaso(tc);
            c = CasoManager.select(conn, c);
        } catch (SQLException exc) {
            System.out.println("Actualizando caso");
            exc.printStackTrace();
            throw new GestionException(exc);
        }
        return find(conn, AttrName, c);
    }

    public String find(Connection conn, String AttrName, Caso c) throws GestionException {
        String Valor = null;
        String PathFile = null;
        try {
            if (c != null) {
                BitacoraTotal b = c.getBitacora();
                BitacoraCaso bc = b.getCaso();
                PathFile = XmlFileSearchManager.select(conn, bc.getTituloAplicacion(), c.getIdGabinete());
                if (PathFile != null) {
                    Valor = XmlFileManager.getAttributeValue(AttrName, PathFile, 1);
                }
            } else {
                System.out.println("No se hay información del caso ");
            }
        } catch (SQLException exc) {
            System.out.println("Actualizando caso");
            exc.printStackTrace();
            throw new GestionException(exc);
        } catch (JDOMException jdome) {
            System.out.println("Obteniendo valor del atributo");
            jdome.printStackTrace();
            throw new GestionException(jdome);
        } catch (Exception e) {
            System.out.println("Obteniendo valor del atributo");
            e.printStackTrace();
            throw new GestionException(e);
        }
        return Valor;
    }

    public String find(Connection conn, String AttrName, String tituloAplicacion, int id_gabinete) throws GestionException {
        String Valor = null;
        String PathFile = null;
        try {
            PathFile = XmlFileSearchManager.select(conn, tituloAplicacion, id_gabinete);
            Valor = XmlFileManager.getAttributeValue(AttrName, PathFile, 1);
            //conn.commit();
        } catch (SQLException exc) {
            System.out.println("Actualizando caso");
            exc.printStackTrace();
            throw new GestionException(exc);
        } catch (JDOMException jdome) {
            System.out.println("Obteniendo valor del atributo");
            jdome.printStackTrace();
            throw new GestionException(jdome);
        } catch (Exception e) {
            System.out.println("Obteniendo valor del atributo");
            e.printStackTrace();
            throw new GestionException(e);
        }
        return Valor;
    }

    public Hashtable getAllElementsAsHashtable(Connection conn, Caso c, Caso c1, String charSetName) throws GestionException {
        Hashtable retVal = null;
        if (c != null) {
            BitacoraTotal b = c.getBitacora();
            BitacoraCaso bc = b.getCaso();
            retVal = getAllElementsAsHashtable(conn, bc.getTituloAplicacion(), bc.getIdGabinete(), charSetName);
        } else {
            System.out.println("No se hay información del caso ");
        }
        return retVal;
    }

    public Hashtable getAllElementsAsHashtable(Connection conn, String tituloAplicacion, int idGabinete, String charSetName) throws GestionException {
        Hashtable retVal = null;
        String pathFile = null;
        try {
            pathFile = XmlFileSearchManager.select(conn, tituloAplicacion, idGabinete);
            if (pathFile != null) {
                File xmlFile = new File(pathFile);
                if (xmlFile.exists() && xmlFile.length() > 0) {
                    String[] datosCaso = new String[5];
                    retVal = XmlFileManager.getAllElementsAsHashtable(xmlFile, charSetName, datosCaso);
                }
            }
        } catch (SQLException exc) {
            System.out.println("Actualizando caso");
            exc.printStackTrace();
            throw new GestionException(exc);
        } catch (Exception e) {
            System.out.println("Obteniendo valor del atributo");
            e.printStackTrace();
            throw new GestionException(e);
        }
        return retVal;
    }

    public Hashtable getAllElementsAsHashtable2(Connection conn, BitacoraCaso bc) throws GestionException {
        Hashtable retVal = null;
        String PathFile = null;
        try {
            PathFile = XmlFileSearchManager.select(conn, bc.getTituloAplicacion(), bc.getIdGabinete());
            if (PathFile != null) {
                retVal = XmlFileManager.getAllElementsAsHashtable2(PathFile, 1);
            }
        } catch (SQLException exc) {
            System.out.println("Actualizando caso");
            exc.printStackTrace();
            throw new GestionException(exc);
        } catch (JDOMException jdome) {
            System.out.println("Obteniendo valor del atributo");
            jdome.printStackTrace();
            throw new GestionException(jdome);
        } catch (Exception e) {
            System.out.println("Obteniendo valor del atributo");
            e.printStackTrace();
            throw new GestionException(e);
        }
        return retVal;
    }
}
