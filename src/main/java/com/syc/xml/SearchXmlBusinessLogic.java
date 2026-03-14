package com.syc.xml;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.BitacoraCaso;
import com.syc.gestion.core.BitacoraTotal;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import java.sql.Connection;
import java.sql.SQLException;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SearchXmlBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(SearchXmlBusinessLogic.class);

    private String jniNameSearchXml;

    public SearchXmlBusinessLogic(String jniName) {
        jniNameSearchXml = jniName;
        super.init(jniName);
    }

    public String find(String AttrName, int idCaso) throws GestionException {
        Caso c = new Caso();
        Connection conn = null;
        try {
            conn = getConnection();
            c.setIdCaso(idCaso);
            c = CasoManager.select(conn, c);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            throw new GestionException(exc);
        }
        return find(AttrName, c);
    }

    public String find(String AttrName, Caso c) throws GestionException {
        Connection conn = null;
        String Valor = null;
        String PathFile = null;
        try {
            if (c != null) {
                conn = getConnection();
                BitacoraTotal b = c.getBitacora();
                BitacoraCaso bc = b.getCaso();
                PathFile = XmlFileSearchManager.select(conn, bc.getTituloAplicacion(), c.getIdGabinete());
                if (PathFile != null) {
                    Valor = XmlFileManager.getAttributeValue(AttrName, PathFile, 1);
                }
                conn.commit();
            } else {
                log.error("No se hay información del caso ");
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            throw new GestionException(exc);
        } catch (JDOMException jdome) {
            log.error("Obteniendo valor del atributo", jdome);
            throw new GestionException(jdome);
        } catch (Exception e) {
            log.error("Obteniendo valor del atributo", e);
            throw new GestionException(e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException exc) {
            log.warn("Cerrando conexion a base de datos", exc);
        }
        conn = null;
        return Valor;
    }

    public String find(String AttrName, String tituloAplicacion, int id_gabinete) throws GestionException {
        Connection conn = null;
        String Valor = null;
        String PathFile = null;
        try {
            conn = getConnection();
            PathFile = XmlFileSearchManager.select(conn, tituloAplicacion, id_gabinete);
            Valor = XmlFileManager.getAttributeValue(AttrName, PathFile, 1);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            throw new GestionException(exc);
        } catch (JDOMException jdome) {
            log.error("Obteniendo valor del atributo", jdome);
            throw new GestionException(jdome);
        } catch (Exception e) {
            log.error("Obteniendo valor del atributo", e);
            throw new GestionException(e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return Valor;
    }
}
