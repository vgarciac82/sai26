package com.syc.xml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.xerces.parsers.DOMParser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlFileManager {

    private static boolean MSG_DEBUG = false;

    private static final Logger log = LoggerFactory.getLogger(XmlFileManager.class);

    public XmlFileManager() {
    }

    public static String getAttributeValue(String AttrName, String PathFile, int opc) throws JDOMException, IOException {
        String valor = null;
        Document doc = null;
        //String s = null;
        SAXBuilder builder = new SAXBuilder(false);
        if (opc == 0) {
            doc = builder.build(PathFile);
            //break MISSING_BLOCK_LABEL_116;
        }
        File f = new File(PathFile);
        if (f.exists()) {
            doc = builder.build(new DataInputStream(new FileInputStream(f)));
            //break MISSING_BLOCK_LABEL_116;
            Element raiz = doc.getRootElement();
            List plantilla = raiz.getChildren("plantilla");
            for (Iterator i = plantilla.iterator(); i.hasNext(); ) {
                Element e = (Element) i.next();
                List data = e.getChildren("data");
                for (Iterator ii = data.iterator(); ii.hasNext(); ) {
                    Element e_data = (Element) ii.next();
                    Element value = e_data.getChild("value");
                    if (AttrName.equals(e_data.getAttributeValue("id"))) {
                        valor = value.getText();
                        break;
                    }
                }
            }
        } else {
            System.out.println((new StringBuilder("No existe el archivo [ ")).append(PathFile).append("]").toString());
        }
        if (MSG_DEBUG) {
            System.out.println((new StringBuilder("   Id < ")).append(AttrName).append(" > ").toString());
            System.out.println((new StringBuilder("Value < ")).append(valor).append(" >").toString());
        }
        return valor;
    }

    public static Hashtable getAllElementsAsHashtable(File xmlFile, String charSetName, String[] datosCaso) {
        //GAF 2010-08-11
        //Para intentar varios charsets
        String[] charsetArray = { charSetName, "windows-1252", "ISO-8859-1" };
        Hashtable retVal = new Hashtable();
        Document doc = null;
        SAXBuilder builder = new SAXBuilder(false);
        for (int i = 0; i < charsetArray.length; i++) {
            System.out.println("Intenta archivo=[" + xmlFile.getAbsolutePath() + "] con el encoding=[" + charSetName + "]");
            try {
                CharsetDecoder decoder = Charset.forName(charsetArray[i]).newDecoder();
                doc = builder.build(new InputStreamReader(new FileInputStream(xmlFile), decoder));
                //SI CONSTRUYO EL DOCUMENTO, PUEDE SALIR
                break;
            } catch (Exception e) {
                System.out.println("Fallo con el encoding=[" + charSetName + "]");
                e.printStackTrace();
                continue;
            } catch (Throwable t) {
                System.out.println("Es un throwable! Fallo con el encoding=[" + charSetName + "]");
                t.printStackTrace();
                continue;
            }
        }
        if (doc == null) {
            System.out.println("El archivo=[" + datosCaso[3] + "] del caso=[" + datosCaso[0] + "], folio=[" + datosCaso[4] + "] Fallo con todos los encodings!");
            //throw new JDOMException("getAllElementsAsHashtable: NO FUNCIONO NINGUN ENCODING!");
        } else {
            try {
                Element raiz = doc.getRootElement();
                if (raiz != null) {
                    List plantilla = raiz.getChildren("plantilla");
                    if (plantilla != null) {
                        Iterator i = plantilla.iterator();
                        if (i != null) {
                            while (i.hasNext()) {
                                Element e = (Element) i.next();
                                List data = e.getChildren("data");
                                Iterator ii = data.iterator();
                                if (ii != null) {
                                    while (ii.hasNext()) {
                                        Element e_data = (Element) ii.next();
                                        String id = e_data.getAttributeValue("id");
                                        String valor = null;
                                        try {
                                            valor = e_data.getChild("value").getText();
                                        } catch (NullPointerException npe) {
                                            valor = "";
                                            //System.out.println("me vale el valor nulo del elemento ["+id+"]!");
                                        }
                                        retVal.put(id, valor);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Algo fallo al explorar el documento!");
                e.printStackTrace();
            } catch (Throwable t) {
                System.out.println("Es un throwable! Algo fallo al explorar el documento!");
                t.printStackTrace();
            }
        }
        return retVal;
    }

    public static Hashtable getAllElementsAsHashtable2(String PathFile, int opc) throws JDOMException, IOException {
        //24. TransformerFactory stateFactory = TransformerFactory.newInstance();
        //25. Transformer stateTransformer = stateFactory.newTransformer(new StreamSource("E:\\stateStyleSheet.xsl"));
        //26. JDOMSource stateSource = new JDOMSource(stateOriginaljdomDocument);
        //27. JDOMResult stateResult = new JDOMResult();
        //28. stateTransformer.transform(stateSource, stateResult);
        System.out.println("PathFile=[" + PathFile + "]");
        File f = new File(PathFile);
        DOMParser dp = new DOMParser();
        try {
            dp.setFeature("http://xml.org/sax/features/validation", false);
            dp.parse(f.toURL().toString());
        } catch (SAXNotRecognizedException saxnr) {
            saxnr.printStackTrace();
        } catch (SAXNotSupportedException saxns) {
            saxns.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }
        Hashtable retVal = new Hashtable();
        Document doc = null;
        SAXBuilder builder = new SAXBuilder(false);
        FileInputStream stateIS = new FileInputStream(f);
        BufferedInputStream stateBIS = new BufferedInputStream(stateIS);
        //StringBuffer StringBuffer1 = new StringBuffer("Java Forums rock");
        FileReader fr2 = new FileReader(f);
        BufferedReader br2 = new BufferedReader(fr2);
        String strTmpLine2 = br2.readLine();
        StringBuffer sb = new StringBuffer();
        while (strTmpLine2 != null) {
            sb.append(strTmpLine2);
            sb.append("\n");
            strTmpLine2 = br2.readLine();
        }
        String strTmpLine3 = sb.toString();
        if (sb.lastIndexOf("?xml") < 0) {
            strTmpLine3 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + strTmpLine3;
        }
        ByteArrayInputStream Bis1 = new ByteArrayInputStream(strTmpLine3.getBytes("ISO-8859-1"));
        if (opc == 0) {
            //doc = builder.build(PathFile);
            //doc = builder.build(stateBIS);
            doc = builder.build(Bis1);
            //break MISSING_BLOCK_LABEL_116;
        }
        if (f.exists() && f.length() > 0) {
            //doc = builder.build(new DataInputStream(new FileInputStream(f)));
            doc = builder.build(stateBIS);
            //break MISSING_BLOCK_LABEL_116;
            Element raiz = doc.getRootElement();
            List plantilla = raiz.getChildren("plantilla");
            for (Iterator i = plantilla.iterator(); i.hasNext(); ) {
                Element e = (Element) i.next();
                List data = e.getChildren("data");
                for (Iterator ii = data.iterator(); ii.hasNext(); ) {
                    Element e_data = (Element) ii.next();
                    String id = e_data.getAttributeValue("id");
                    String valor = null;
                    try {
                        valor = e_data.getChild("value").getText();
                        retVal.put(id, valor);
                    } catch (NullPointerException npe) {
                        //System.out.println("me vale el valor nulo del elemento ["+id+"]!");
                    }
                }
            }
        } else {
            System.out.println((new StringBuilder("No existe el archivo [ ")).append(PathFile).append("] o esta vacio!").toString());
        }
        return retVal;
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
