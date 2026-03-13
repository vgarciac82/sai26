package com.syc.xml;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XXXmlFileManager {

    private static boolean MSG_DEBUG = false;

    private static final Logger log = LoggerFactory.getLogger(XXXmlFileManager.class);

    public XXXmlFileManager() {
    }

    public static String getAttributeValue(String[] AttrName, String[] ValueXml, String PathFile, int opc) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false);
        StringBuffer str = new StringBuffer();
        FileWriter archivo = null;
        PrintWriter scbr = null;
        String attrN = null;
        String valueN = null;
        String valor = null;
        Document doc = null;
        File f = null;
        if (opc == 0) {
            doc = builder.build(PathFile);
        }
        f = new File(PathFile);
        if (f.exists()) {
            doc = builder.build(new DataInputStream(new FileInputStream(f)));
        } else {
            System.out.println((new StringBuilder("No existe el archivo [ ")).append(PathFile).append("]").toString());
        }
        Element raiz = doc.getRootElement();
        List plantilla = raiz.getChildren("plantilla");
        Iterator i = plantilla.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            List data = e.getChildren("data");
            Iterator p = data.iterator();
            while (p.hasNext()) {
                Element ee = (Element) p.next();
                Element value = ee.getChild("value");
                if (ee.getAttributeValue("id") != null) {
                    for (int b = 0; b < AttrName.length; b++) {
                        attrN = AttrName[b];
                        if (attrN.equals(ee.getAttributeValue("id"))) {
                            for (int bb = 0; bb < ValueXml.length; bb++) {
                                valueN = ValueXml[bb];
                                str.append("<data id=\"" + ee.getAttributeValue("id") + "\"><value>" + valueN + "</value></data>");
                            }
                        } else {
                            str.append("<data id=\"" + ee.getAttributeValue("id") + "\"><value>" + value.getText() + "</value></data>");
                        }
                    }
                }
            }
            String nuevoXml = str.toString();
            archivo = new FileWriter(f, true);
            scbr = new PrintWriter(archivo);
            scbr.println(nuevoXml);
            archivo.close();
        }
        doc = null;
        f = null;
        if (MSG_DEBUG) {
            System.out.println((new StringBuilder("   Id < ")).append(AttrName).append(" > ").toString());
            System.out.println((new StringBuilder("Value < ")).append(valor).append(" >").toString());
        }
        return valor;
    }

    public static int updateCasoDatoXml(Connection conn, String PathFile, String IdCaso, int opc) throws JDOMException, IOException {
        int retval = -1;
        SAXBuilder builder = new SAXBuilder(false);
        String valueN = null;
        String AttrName = "DPC_f_limite";
        Document doc = null;
        File f = null;
        try {
            if (opc == 0) {
                doc = builder.build(PathFile);
            }
            f = new File(PathFile);
            try {
                if (f.exists()) {
                    doc = builder.build(new DataInputStream(new FileInputStream(f)));
                } else {
                    System.out.println((new StringBuilder("No existe el archivo [ ")).append(PathFile).append("]").toString());
                }
                Element raiz = doc.getRootElement();
                List plantilla = raiz.getChildren("plantilla");
                Iterator i = plantilla.iterator();
                while (i.hasNext()) {
                    Element e = (Element) i.next();
                    List data = e.getChildren("data");
                    Iterator p = data.iterator();
                    while (p.hasNext()) {
                        Element ee = (Element) p.next();
                        //Element value = ee.getChild("value");
                        if (ee.getAttributeValue("id") != null) {
                            if (AttrName.equals(ee.getAttributeValue("id"))) {
                                valueN = ee.getValue();
                                System.out.println("El valor que esta recuperando es : " + valueN);
                                retval = XXXmlFileSearchManager.updateCasoDato(conn, IdCaso, valueN);
                            }
                        }
                    }
                }
            } catch (JDOMException e) {
                System.out.println("Documento XML mal formado o incorrecto.");
                System.out.println(e.getMessage());
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
        }
        doc = null;
        f = null;
        return retval;
    }
}
