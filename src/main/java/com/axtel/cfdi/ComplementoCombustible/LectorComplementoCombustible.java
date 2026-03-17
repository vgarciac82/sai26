package com.axtel.cfdi.ComplementoCombustible;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.axtel.cfdi.ComplementoCombustible.custom.AdendaECC;
import com.axtel.cfdi.exceptions.CFDIReadException;
import java.util.Base64;

public class LectorComplementoCombustible {

    private static final String XPATH_EXPRESSION = "//edr:Concepto";

    private class ContextReader implements javax.xml.namespace.NamespaceContext {

        public String getNamespaceURI(String prefix) {
            String uri;
            if (prefix.equals("edr"))
                uri = "http://www.edenred.com.mx/cfdi/3/";
            else
                uri = null;
            return uri;
        }

        public String getPrefix(String uri) {
            return null;
        }

        public java.util.Iterator getPrefixes(String val) {
            return null;
        }
    }

    DocumentBuilderFactory factory;

    DocumentBuilder builder;

    XPath xpath;

    public LectorComplementoCombustible() throws CFDIReadException {
        try {
            javax.xml.namespace.NamespaceContext ctx = new ContextReader();
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(ctx);
        } catch (ParserConfigurationException e) {
            throw new CFDIReadException("Problemas ejecutando Parse al cfdi: " + e, e);
        }
    }

    public Bonificacion readBonificacion(File f) throws CFDIReadException, ParserConfigurationException {
        try {
            BigDecimal importeBonificacion = new BigDecimal(0.00);
            BigDecimal impuestos = new BigDecimal(0.0);
            Document documento = builder.parse(f);
            NodeList nodos = (NodeList) xpath.evaluate(LectorComplementoCombustible.XPATH_EXPRESSION, documento, XPathConstants.NODESET);
            for (int i = 0; i < nodos.getLength(); i++) {
                String descripcion = nodos.item(i).getNodeName() + " : " + nodos.item(i).getAttributes().getNamedItem("Descripcion");
                if (descripcion != null && descripcion.indexOf("BONIF.") > 0) {
                    Node nodo = nodos.item(i);
                    if (nodo.getAttributes().getNamedItem("Importe") != null)
                        importeBonificacion = importeBonificacion.add(new BigDecimal(nodo.getAttributes().getNamedItem("Importe").getNodeValue()));
                    Element docElement = (Element) nodo;
                    NodeList traslados = docElement.getElementsByTagName("edr:Traslado");
                    if (traslados != null && traslados.getLength() > 0) {
                        for (int j = 0; j < traslados.getLength(); j++) {
                            //
                            Node nodeTraslado = traslados.item(j).getAttributes().getNamedItem("Importe");
                            if (nodeTraslado != null) {
                                impuestos = impuestos.add(new BigDecimal(nodeTraslado.getNodeValue()));
                            }
                        }
                    }
                }
            }
            return new Bonificacion(importeBonificacion, impuestos);
        } catch (XPathExpressionException e) {
            throw new CFDIReadException("Problemas evaluando expresion de busqueda XPath." + e, e);
        } catch (SAXException e) {
            throw new CFDIReadException("Problemas con el parseo del XML con SAX: " + e, e);
        } catch (IOException e) {
            throw new CFDIReadException("Problemas de E/S leyendo CFDI: " + e, e);
        }
    }

    public AdendaECC readMontosCFDI(File f) throws CFDIReadException, ParserConfigurationException {
        List<CargoECC> cargos = new ArrayList<CargoECC>();
        List<Bonificacion> bonificaciones = new ArrayList<Bonificacion>();
        try {
            Document documento = builder.parse(f);
            NodeList nodos = (NodeList) xpath.evaluate(LectorComplementoCombustible.XPATH_EXPRESSION, documento, XPathConstants.NODESET);
            for (int i = 0; i < nodos.getLength(); i++) {
                BigDecimal importe = new BigDecimal(0.00);
                BigDecimal impuestos = new BigDecimal(0.0);
                BigDecimal descuentos = new BigDecimal(0.0);
                Node nodo = nodos.item(i);
                String descripcion = nodo.getNodeName() + " : " + nodo.getAttributes().getNamedItem("Descripcion");
                if (nodo.getAttributes().getNamedItem("Importe") != null)
                    importe = importe.add(new BigDecimal(nodo.getAttributes().getNamedItem("Importe").getNodeValue()));
                if (nodo.getAttributes().getNamedItem("Descuento") != null)
                    descuentos = descuentos.add(new BigDecimal(nodo.getAttributes().getNamedItem("Descuento").getNodeValue()));
                Element docElement = (Element) nodo;
                NodeList traslados = docElement.getElementsByTagName("edr:Traslado");
                if (traslados != null && traslados.getLength() > 0) {
                    for (int j = 0; j < traslados.getLength(); j++) {
                        //
                        Node nodeTraslado = traslados.item(j).getAttributes().getNamedItem("Importe");
                        if (nodeTraslado != null) {
                            impuestos = impuestos.add(new BigDecimal(nodeTraslado.getNodeValue()));
                        }
                    }
                }
                if (descripcion != null && descripcion.indexOf("BONIF.") > 0) {
                    bonificaciones.add(new Bonificacion(importe, impuestos, descripcion));
                } else
                    cargos.add(new CargoECC(importe, impuestos, descuentos, descripcion));
            }
            return new AdendaECC(bonificaciones, cargos);
        } catch (XPathExpressionException e) {
            throw new CFDIReadException("Problemas evaluando expresion de busqueda XPath." + e, e);
        } catch (SAXException e) {
            throw new CFDIReadException("Problemas con el parseo del XML con SAX: " + e, e);
        } catch (IOException e) {
            throw new CFDIReadException("Problemas de E/S leyendo CFDI: " + e, e);
        }
    }
}
