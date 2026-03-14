package com.syc.fortimax.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.draggerco.pdf.test.ImageManipulator;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.dbms.DBMS;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.util.Util;
import com.syc.info.cfdi.CFDIMaskGenerator;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentoManager extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(DocumentoManager.class);

    /**
     * Cambia la referencia a la ruta fisica de una pagina.
     *
     * @param conn
     *            COnexio activa a la DB
     * @param d
     *            Documento a actualizar.
     * @throws SQLException
     */
    public static void actualizaRutaPagina(Connection conn, Documento d) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE	imx_pagina ");
        queryUpdate.append("   SET	NOM_ARCHIVO_VOL = ?, ");
        queryUpdate.append("      	NOM_ARCHIVO_ORG = ? ");
        queryUpdate.append("WHERE  titulo_aplicacion = ? ");
        queryUpdate.append("       AND id_gabinete = ? ");
        queryUpdate.append("       AND id_carpeta_padre = ? ");
        queryUpdate.append("       AND id_documento = ? ");
        PreparedStatement psUpdate = null;
        try {
            log.debug("Object: {}", "actualizando Documento: " + d);
            psUpdate = conn.prepareStatement(queryUpdate.toString());
            psUpdate.setString(1, d.getPaginaDocumento(0).getNomArchivoVol());
            psUpdate.setString(2, d.getPaginaDocumento(0).getNomArchivoOrg());
            psUpdate.setString(3, d.getTituloAplicacion());
            psUpdate.setInt(4, d.getIdGabinete());
            psUpdate.setInt(5, d.getIdCarpetaPadre());
            psUpdate.setInt(6, d.getIdDocumento());
            log.trace("Object: {}", "Ejecutando: " + queryUpdate.toString());
            int actualizados = psUpdate.executeUpdate();
            log.info("Object: {}", "Se actualizaron " + actualizados + " paginas de documento");
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static synchronized Documento buscaDocumento(Connection conn, Fortimax nodo) throws Exception {
        Documento d = selectDocumento(conn, nodo.getTituloAplicacion(), nodo.getIdGabinete(), nodo.getIdCarpeta(), nodo.getIdDocumento());
        return d;
    }

    public static synchronized Documento buscaDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento) throws Exception {
        Documento d = selectDocumento(conn, titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento);
        return d;
    }

    public static Documento buscaDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int idCarpetaPadre, String doc_nombre) throws SQLException {
        Documento d = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        pstmnt = conn.prepareStatement("SELECT * FROM imx_documento WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? AND id_carpeta_padre = ? AND nombre_documento = ?");
        pstmnt.setString(1, titulo_aplicacion);
        pstmnt.setInt(2, id_gabinete);
        pstmnt.setInt(3, idCarpetaPadre);
        pstmnt.setString(4, doc_nombre);
        rs = pstmnt.executeQuery();
        if (rs.next())
            d = selectDocumento(conn, rs.getString("titulo_aplicacion"), rs.getInt("id_gabinete"), rs.getInt("id_carpeta_padre"), rs.getInt("id_documento"));
        return d;
    }

    public static Documento buscaDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, String doc_nombre) throws SQLException {
        Documento d = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        pstmnt = conn.prepareStatement("SELECT * FROM imx_documento WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? AND nombre_documento = ?");
        pstmnt.setString(1, titulo_aplicacion);
        pstmnt.setInt(2, id_gabinete);
        pstmnt.setString(3, doc_nombre);
        rs = pstmnt.executeQuery();
        if (rs.next())
            d = selectDocumento(conn, rs.getString("titulo_aplicacion"), rs.getInt("id_gabinete"), rs.getInt("id_carpeta_padre"), rs.getInt("id_documento"));
        return d;
    }

    public static Documento cambiaNombreDocumento(Connection conn, String tituloAplicacion, int idGabinete, int idCarpeta, int idDocumento, String nombreDocumento) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE imx_documento ");
        queryUpdate.append("   SET nombre_documento = ? ");
        queryUpdate.append(" WHERE titulo_aplicacion = ? ");
        queryUpdate.append("   AND id_gabinete = ? ");
        queryUpdate.append("   AND id_carpeta_padre = ? ");
        queryUpdate.append("   AND id_documento = ? ");
        PreparedStatement psUpdate = null;
        try {
            psUpdate = conn.prepareStatement(queryUpdate.toString());
            psUpdate.setString(1, nombreDocumento);
            psUpdate.setString(2, tituloAplicacion);
            psUpdate.setInt(3, idGabinete);
            psUpdate.setInt(4, idCarpeta);
            psUpdate.setInt(5, idDocumento);
            int afectados = psUpdate.executeUpdate();
            log.info("Object: {}", " Se cambio el nombre a [" + afectados + "] documentos");
            return DocumentoManager.selectDocumento(conn, tituloAplicacion, idGabinete, idCarpeta, idDocumento);
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static Documento creaDocumento(Connection conn, Carpeta parentFolder, String documentName, String fileExtension, String user) throws FortimaxException {
        Documento d = new Documento();
        d.setTituloAplicacion(parentFolder.getTituloAplicacion());
        d.setIdGabinete(parentFolder.getIdGabinete());
        d.setIdCarpetaPadre(parentFolder.getIdCarpeta());
        if ("imx".equals(fileExtension))
            d.setNombreTipoDocto("IMAX_FILE");
        else
            d.setNombreTipoDocto("EXTERNO");
        d.setNombreDocumento(documentName);
        d.setNombreUsuario(user);
        d.setExtension(fileExtension);
        DocumentoManager.insertDocumento(conn, d);
        d = DocumentoManager.getDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
        d.setExtension(fileExtension);
        return d;
    }

    public static int delete(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM imx_documento WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setInt(3, id_carpeta_padre);
            pstmnt.setInt(4, id_documento);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int esViatico(Connection conn, String folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        int retorno = 0;
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) FROM tRELACIONGASTOSDetalle WHERE SUBSTRING(EP,32,2) = 37 AND cIdRelacion LIKE 'VIATICO%' AND nFolioRELACIONGASTOS = ?");
            pst.setString(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                retorno = rs.getInt(1);
            }
            return retorno;
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
    }

    /**
     * Valida si un documento existe en una carpeta especifica del expediente
     *
     * @param conn
     *            Conexion activa a la DB
     * @param titulo_aplicacion
     *            Gaveta
     * @param id_gabinete
     *            Gabinete
     * @param id_carpeta_padre
     *            Carpeta Padre
     * @param doc_nombre
     *            Nombre del documento
     * @return true si el documento existe en el expediente.
     * @throws SQLException
     */
    public static boolean existeDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, String doc_nombre) throws FortimaxException {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT COUNT(*) AS existe FROM imx_documento WITH(NOLOCK) WHERE titulo_aplicacion = ?  AND id_gabinete = ? AND id_carpeta_padre = ? AND nombre_documento = ? ");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setInt(3, id_carpeta_padre);
            pstmnt.setString(4, doc_nombre);
            rs = pstmnt.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    /**
     * Valida si un documento existe en todo el expediente sin importar en que
     * carpeta.
     *
     * @param conn
     *            Conexion activa a la DB
     * @param titulo_aplicacion
     *            Gaveta
     * @param id_gabinete
     *            Gabinete
     * @param doc_nombre
     *            Nombre del documento
     * @return true si el documento existe en el expediente.
     * @throws SQLException
     */
    public static boolean existeDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, String doc_nombre) throws Exception {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT COUNT(*) AS existe FROM imx_documento WHERE titulo_aplicacion = ?  AND id_gabinete = ? AND nombre_documento = ? ");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setString(3, doc_nombre);
            rs = pstmnt.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    /**
     * Valida si un documento existe y tiene contenido en la carpeta.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param titulo_aplicacion
     *            Titulo aplicacion
     * @param id_gabinete
     *            ID Gabinete
     * @param nombreCarpeta
     *            nombre de la carpeta padre del documento
     * @param doc_nombre
     *            Nombre del documento a buscar
     * @return true si y solo si existe el documento con paginas en la carpeta.
     * @throws Exception
     */
    public static boolean existeDocumentoCapturado(Connection conn, String titulo_aplicacion, int id_gabinete, int idCarpeta, int idDocumento) throws FortimaxException {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        StringBuilder queryBusqueda = new StringBuilder();
        queryBusqueda.append("SELECT documento.id_documento, ");
        queryBusqueda.append("       Count(pagina.numero_pagina) AS paginas ");
        queryBusqueda.append("FROM   imx_documento documento WITH( nolock)  ");
        queryBusqueda.append("       LEFT OUTER JOIN imx_carpeta carpeta WITH( nolock) ");
        queryBusqueda.append("                    ON documento.titulo_aplicacion = carpeta.titulo_aplicacion ");
        queryBusqueda.append("                       AND documento.id_gabinete = carpeta.id_gabinete  ");
        queryBusqueda.append("                       AND documento.id_carpeta_padre = carpeta.id_carpeta ");
        queryBusqueda.append("       LEFT OUTER JOIN imx_pagina pagina WITH(nolock)  ");
        queryBusqueda.append("                    ON documento.titulo_aplicacion = pagina.titulo_aplicacion ");
        queryBusqueda.append("                       AND documento.id_gabinete = pagina.id_gabinete  ");
        queryBusqueda.append("                       AND documento.id_carpeta_padre = pagina.id_carpeta_padre ");
        queryBusqueda.append("                       AND documento.id_documento = pagina.id_documento  " + "WHERE  documento.titulo_aplicacion = ?  ");
        queryBusqueda.append("       AND documento.id_gabinete = ?  ");
        queryBusqueda.append("       AND documento.id_documento  = ?  ");
        queryBusqueda.append("       AND documento.id_carpeta_padre = ? ");
        queryBusqueda.append(" GROUP  BY documento.titulo_aplicacion, documento.id_gabinete, documento.id_carpeta_padre,  documento.id_documento  ");
        queryBusqueda.append(" HAVING Count(pagina.numero_pagina) > 0   ");
        try {
            pstmnt = conn.prepareStatement(queryBusqueda.toString());
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setInt(3, idDocumento);
            pstmnt.setInt(4, idCarpeta);
            rs = pstmnt.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    /**
     * Valida si un documento existe y tiene contenido en la carpeta.
     *
     * @param conn
     *            Conexion activa a a base de datos
     * @param titulo_aplicacion
     *            Aplicacion
     * @param id_gabinete
     *            Gabinete
     * @param idCarpeta
     *            ID de la carpeta padre
     * @param doc_nombre
     *            Nombre del documento a buscar
     * @return true si y solo si existe un documento con el nombre especificado
     *         en la carpeta con al menos una pagina capturada.
     * @throws Exception
     */
    public static boolean existeDocumentoCapturado(Connection conn, String titulo_aplicacion, int id_gabinete, int idCarpeta, String doc_nombre) throws FortimaxException {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        StringBuilder queryBusqueda = new StringBuilder();
        queryBusqueda.append("SELECT documento.id_documento, ");
        queryBusqueda.append("       Count(pagina.numero_pagina) AS paginas ");
        queryBusqueda.append("FROM   imx_documento documento WITH( nolock)  ");
        queryBusqueda.append("       LEFT OUTER JOIN imx_carpeta carpeta WITH( nolock) ");
        queryBusqueda.append("                    ON documento.titulo_aplicacion = carpeta.titulo_aplicacion ");
        queryBusqueda.append("                       AND documento.id_gabinete = carpeta.id_gabinete  ");
        queryBusqueda.append("                       AND documento.id_carpeta_padre = carpeta.id_carpeta ");
        queryBusqueda.append("       LEFT OUTER JOIN imx_pagina pagina WITH(nolock)  ");
        queryBusqueda.append("                    ON documento.titulo_aplicacion = pagina.titulo_aplicacion ");
        queryBusqueda.append("                       AND documento.id_gabinete = pagina.id_gabinete  ");
        queryBusqueda.append("                       AND documento.id_carpeta_padre = pagina.id_carpeta_padre ");
        queryBusqueda.append("                       AND documento.id_documento = pagina.id_documento  ");
        queryBusqueda.append("WHERE  documento.titulo_aplicacion = ?  ");
        queryBusqueda.append("       AND documento.id_gabinete = ?  ");
        queryBusqueda.append("       AND nombre_documento = ?  ");
        queryBusqueda.append("       AND carpeta.id_carpeta = ? ");
        queryBusqueda.append("GROUP  BY documento.titulo_aplicacion,  ");
        queryBusqueda.append("          documento.id_gabinete,  ");
        queryBusqueda.append("          documento.id_carpeta_padre, ");
        queryBusqueda.append("          documento.id_documento  ");
        queryBusqueda.append("HAVING Count(pagina.numero_pagina) > 0   ");
        try {
            pstmnt = conn.prepareStatement(queryBusqueda.toString());
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setString(3, doc_nombre);
            pstmnt.setInt(4, idCarpeta);
            rs = pstmnt.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    /**
     * Valida si un documento existe y tiene contenido en todo el expediente sin
     * importar en que carpeta.
     *
     * @param conn
     *            Conexion activa a la DB
     * @param titulo_aplicacion
     *            Gaveta
     * @param id_gabinete
     *            Gabinete
     * @param doc_nombre
     *            Nombre del documento
     * @return true si el documento existe en el expediente.
     * @throws SQLException
     */
    public static boolean existeDocumentoCapturado(Connection conn, String titulo_aplicacion, int id_gabinete, String doc_nombre) throws Exception {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String queryBusqueda = "SELECT documento.id_documento, " + "       Count(pagina.numero_pagina) as paginas " + "FROM   imx_documento documento WITH( nolock)  " + "       LEFT OUTER JOIN imx_pagina pagina WITH(nolock) " + "                    ON documento.titulo_aplicacion = pagina.titulo_aplicacion " + "                       AND documento.id_gabinete = pagina.id_gabinete  " + "                       AND documento.id_carpeta_padre = pagina.id_carpeta_padre " + "                       AND documento.id_documento = pagina.id_documento " + "WHERE  documento.titulo_aplicacion = ?  " + "       AND documento.id_gabinete = ?  " + "       AND nombre_documento = ? " + "GROUP  BY documento.titulo_aplicacion, " + "          documento.id_gabinete, " + "          documento.id_carpeta_padre, " + "          documento.id_documento " + "HAVING Count(pagina.numero_pagina) > 0 ";
        try {
            pstmnt = conn.prepareStatement(queryBusqueda);
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setString(3, doc_nombre);
            rs = pstmnt.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    /**
     * Valida si un documento existe y tiene contenido en la carpeta.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param titulo_aplicacion
     *            Titulo aplicacion
     * @param id_gabinete
     *            ID Gabinete
     * @param nombreCarpeta
     *            nombre de la carpeta padre del documento
     * @param doc_nombre
     *            Nombre del documento a buscar
     * @return true si y solo si existe el documento con paginas en la carpeta.
     * @throws Exception
     */
    public static boolean existeDocumentoCapturado(Connection conn, String titulo_aplicacion, int id_gabinete, String nombreCarpeta, String doc_nombre) throws Exception {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT documento.id_documento, ");
        query.append("       Count(pagina.numero_pagina) AS paginas ");
        query.append("FROM   imx_documento documento WITH( nolock)  ");
        query.append("       LEFT OUTER JOIN imx_carpeta carpeta WITH( nolock) ");
        query.append("                    ON documento.titulo_aplicacion = carpeta.titulo_aplicacion ");
        query.append("                       AND documento.id_gabinete = carpeta.id_gabinete  ");
        query.append("                       AND documento.id_carpeta_padre = carpeta.id_carpeta ");
        query.append("       LEFT OUTER JOIN imx_pagina pagina WITH(nolock)  ");
        query.append("                    ON documento.titulo_aplicacion = pagina.titulo_aplicacion ");
        query.append("                       AND documento.id_gabinete = pagina.id_gabinete  ");
        query.append("                       AND documento.id_carpeta_padre = pagina.id_carpeta_padre ");
        query.append("                       AND documento.id_documento = pagina.id_documento  ");
        query.append("WHERE  documento.titulo_aplicacion = ?  ");
        query.append("       AND documento.id_gabinete = ?  ");
        query.append("       AND nombre_documento = ?  ");
        query.append("       AND nombre_carpeta = ? ");
        query.append("GROUP  BY documento.titulo_aplicacion,  ");
        query.append("          documento.id_gabinete,  ");
        query.append("          documento.id_carpeta_padre, ");
        query.append("          documento.id_documento  ");
        query.append("HAVING Count(pagina.numero_pagina) > 0   ");
        try {
            pstmnt = conn.prepareStatement(query.toString());
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setString(3, doc_nombre);
            pstmnt.setString(4, nombreCarpeta);
            rs = pstmnt.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    public static Documento getDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, String filename) throws FortimaxException {
        DBMS db = new DBMS(conn);
        StringBuilder query = new StringBuilder();
        query.append("SELECT d.titulo_aplicacion, d.id_gabinete, d.id_carpeta_padre, ");
        query.append("       MAX(d.id_documento) id_documento, ");
        query.append("       d.nombre_documento, ");
        query.append("       MIN(p.numero_pagina) numero_pagina ");
        query.append("  FROM imx_documento d  with (nolock)");
        query.append("       LEFT OUTER JOIN ");
        query.append("       imx_pagina p  with (nolock) ON ");
        query.append("       (");
        query.append("            p.titulo_aplicacion = d.titulo_aplicacion ");
        query.append("        AND p.id_gabinete = d.id_gabinete ");
        query.append("        AND p.id_carpeta_padre = d.id_carpeta_padre ");
        query.append("        AND p.id_documento = d.id_documento ");
        query.append("       ) ");
        query.append(" WHERE d.titulo_aplicacion = ? ");
        query.append("   AND d.id_gabinete = ? ");
        query.append("   AND d.id_carpeta_padre = ? ");
        query.append("   AND ").append(db.lower("d.nombre_documento")).append(" = ? ");
        query.append(" GROUP BY d.titulo_aplicacion, d.id_gabinete, d.id_carpeta_padre, d.nombre_documento");
        Documento rd = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(query.toString());
            int colIdx = 1;
            pstmnt.setString(colIdx++, titulo_aplicacion);
            pstmnt.setInt(colIdx++, id_gabinete);
            pstmnt.setInt(colIdx++, id_carpeta_padre);
            pstmnt.setString(colIdx++, filename);
            rs = pstmnt.executeQuery();
            if (rs.next())
                rd = selectDocumento(conn, titulo_aplicacion, id_gabinete, id_carpeta_padre, rs.getInt("id_documento"));
            return rd;
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
    }

    public static Documento getDocumentoPrecompromiso(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        Documento rd = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            //DBMS db = new DBMS( conn );
            pstmnt = conn.prepareStatement("SELECT d.titulo_aplicacion, d.id_gabinete, d.id_carpeta_padre,  id_documento, d.nombre_documento FROM imx_documento d  " + " WHERE d.titulo_aplicacion = ? AND d.id_gabinete = ? ");
            int colIdx = 1;
            pstmnt.setString(colIdx++, titulo_aplicacion);
            pstmnt.setInt(colIdx++, id_gabinete);
            rs = pstmnt.executeQuery();
            if (rs.next())
                rd = selectDocumento(conn, titulo_aplicacion, id_gabinete, rs.getInt("id_carpeta_padre"), rs.getInt("id_documento"));
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rd;
    }

    public static Documento getDocumentoRequisicion(Connection conn, String titulo_aplicacion, int id_gabinete, String nombreDocumento) throws SQLException {
        Documento rd = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            //DBMS db = new DBMS( conn );
            pstmnt = conn.prepareStatement("SELECT d.titulo_aplicacion, d.id_gabinete, d.id_carpeta_padre,  id_documento, d.nombre_documento FROM imx_documento d  " + " WHERE d.titulo_aplicacion = ? AND d.id_gabinete = ? and NOMBRE_DOCUMENTO = ?  ");
            //int colIdx = 1;
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setString(3, nombreDocumento);
            rs = pstmnt.executeQuery();
            if (rs.next())
                rd = selectDocumento(conn, titulo_aplicacion, id_gabinete, rs.getInt("id_carpeta_padre"), rs.getInt("id_documento"));
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return rd;
    }

    // Ethiel, regresa todos los documentos de una carpeta
    public static ArrayList<Documento> getDocumentosDeCarpeta(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre) throws SQLException {
        ArrayList<Documento> docs = new ArrayList<Documento>();
        Documento d = null;
        PreparedStatement pstmnt0 = null, pstmnt1 = null;
        ResultSet rs0 = null, rs1 = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("SELECT d.id_documento,d.nombre_documento, d.nombre_usuario, d.prioridad, ");
            query.append("d.id_tipo_docto, d.fh_creacion, d.fh_modificacion, d.numero_accesos, d.numero_paginas, ");
            query.append("d.titulo, d.autor, d.materia, d.descripcion, d.clase_documento, d.estado_documento, ");
            query.append("d.tamano_bytes, t.nombre_tipo_docto, d.compartir, d.token_compartir,fh_vigencia ");
            query.append("FROM imx_documento d, imx_tipo_documento t WHERE t.titulo_aplicacion = d.titulo_aplicacion ");
            query.append("AND t.id_tipo_docto = d.id_tipo_docto AND d.titulo_aplicacion = ? AND d.id_gabinete = ? ");
            query.append("AND d.id_carpeta_padre = ?");
            pstmnt0 = conn.prepareStatement(query.toString());
            pstmnt0.setString(1, titulo_aplicacion);
            pstmnt0.setInt(2, id_gabinete);
            pstmnt0.setInt(3, id_carpeta_padre);
            rs0 = pstmnt0.executeQuery();
            while (rs0.next()) {
                d = new Documento();
                d.setTituloAplicacion(titulo_aplicacion);
                d.setIdGabinete(id_gabinete);
                d.setIdCarpetaPadre(id_carpeta_padre);
                d.setIdDocumento(rs0.getInt("id_documento"));
                d.setNombreDocumento(rs0.getString("nombre_documento"));
                d.setNombreUsuario(rs0.getString("nombre_usuario"));
                d.setPrioridad(rs0.getInt("prioridad"));
                d.setIdTipoDocto(rs0.getInt("id_tipo_docto"));
                d.setNombreTipoDocto(rs0.getString("nombre_tipo_docto"));
                d.setFechaCreacion(new Date(rs0.getTimestamp("fh_creacion").getTime()));
                d.setFechaModificacion(new Date(rs0.getTimestamp("fh_modificacion").getTime()));
                d.setNumeroAccesos(rs0.getInt("numero_accesos"));
                d.setNumeroPaginas(rs0.getInt("numero_paginas"));
                d.setTitulo(rs0.getString("titulo"));
                d.setAutor(rs0.getString("autor"));
                d.setMateria(rs0.getString("materia"));
                d.setDescripcion(rs0.getString("descripcion"));
                d.setClaseDocumento(rs0.getInt("clase_documento"));
                d.setEstadoDocumento(rs0.getString("estado_documento"));
                d.setTamanoBytes(rs0.getDouble("tamano_bytes"));
                d.setCompartir(rs0.getString("compartir"));
                d.setTokenCompartir(rs0.getString("token_compartir"));
                d.setFh_vigencia(rs0.getTimestamp("fh_vigencia"));
                pstmnt1 = conn.prepareStatement("SELECT p.numero_pagina, p.tipo_pagina, p.volumen, p.tipo_volumen, " + "v.unidad_disco, v.ruta_base, v.ruta_directorio, p.nom_archivo_vol, p.nom_archivo_org, " + "p.estado_pagina, p.tamano_bytes FROM imx_pagina p, imx_volumen v WITH (NOLOCK)" + "WHERE v.volumen = p.volumen AND p.titulo_aplicacion = ? AND p.id_gabinete = ? " + "AND p.id_carpeta_padre = ? AND p.id_documento = ? ORDER BY p.numero_pagina");
                pstmnt1.setString(1, titulo_aplicacion);
                pstmnt1.setInt(2, id_gabinete);
                pstmnt1.setInt(3, id_carpeta_padre);
                pstmnt1.setInt(4, rs0.getInt("id_documento"));
                rs1 = pstmnt1.executeQuery();
                boolean prvez = true;
                List lp = new ArrayList<>();
                while (rs1.next()) {
                    Pagina p = new Pagina();
                    p.setTituloAplicacion(titulo_aplicacion);
                    p.setIdGabinete(id_gabinete);
                    p.setIdCarpetaPadre(id_carpeta_padre);
                    p.setIdDocumento(rs0.getInt("id_documento"));
                    p.setNumeroPagina(rs1.getInt("numero_pagina"));
                    p.setVolumen(rs1.getString("volumen"));
                    p.setTipoVolumen(rs1.getString("tipo_volumen"));
                    p.setNomArchivoVol(rs1.getString("nom_archivo_vol").trim());
                    p.setNomArchivoOrg(rs1.getString("nom_archivo_org").trim());
                    p.setTipoPagina(rs1.getString("tipo_pagina"));
                    p.setEstadoPagina(rs1.getString("estado_pagina"));
                    p.setTamanoBytes(rs1.getDouble("tamano_bytes"));
                    p.setUnidadDisco(rs1.getString("unidad_disco").trim());
                    p.setRutaBase(rs1.getString("ruta_base").trim());
                    p.setRutaDirectorio(rs1.getString("ruta_directorio").trim());
                    lp.add(p);
                    if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs1.getString("nom_archivo_org") != null)) {
                        prvez = false;
                        int pos = rs1.getString("nom_archivo_org").lastIndexOf(".");
                        d.setExtension((pos > -1) ? rs1.getString("nom_archivo_org").substring(pos + 1) : "");
                    }
                }
                d.setNumeroPaginas(lp.size());
                Pagina[] pages = new Pagina[lp.size()];
                for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
                d.setPaginasDocumento(pages);
                docs.add(d);
            }
        } finally {
            CloseObject.closeObject(rs0);
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(pstmnt0);
        }
        return docs;
    }

    /**
     * EJRV Este algoritmo genera un identificador unico de 32 caracteres
     *
     * Se basa en la concatenacion de los hexadecimales de: - La direccion IP
     * del equipo dode se este ejecutando - La fecha y hora en milisegundos La
     * unidad de tiempo devuelta es en milisegundos, la exactitud del valor
     * depende del SO en el que se ejecute, pues muchos SO calculan la unidad de
     * tiempo en decimas de milisegundos - Un numero random seguro Un numero
     * random seguro produce una salida no determinista y por lo tanto se
     * requiere de una semilla no predecible y la salida de un random seguro es
     * una secuencia criptograficamente solida como se describe en RFC1750
     * (Randomness Recommendations for Security) - El Hash de identidad del
     * objeto El metodo "System.identityHashCode(Object)" regresa enteros
     * distintos para cada objeto
     */
    public static String getNextFilename(Documento d, String userCode) throws FortimaxException {
        String strRetVal = "";
        String strTemp = "";
        try {
            // Obtiene direccion IP
            InetAddress addr = InetAddress.getLocalHost();
            byte[] ipaddr = addr.getAddress();
            for (int i = 0; i < ipaddr.length; i++) {
                Byte b = new Byte(ipaddr[i]);
                strTemp = Integer.toHexString(b.intValue() & 0x000000ff);
                while (strTemp.length() < 2) strTemp = '0' + strTemp;
                strRetVal += strTemp;
            }
            // Obtiene tiempo actual con milisegundos
            strTemp = Long.toHexString(System.currentTimeMillis());
            while (strTemp.length() < 12) strTemp = '0' + strTemp;
            strRetVal += strTemp;
            // Obtiene un numero random
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            strTemp = Integer.toHexString(prng.nextInt());
            while (strTemp.length() < 8) strTemp = '0' + strTemp;
            strRetVal += strTemp.substring(4);
            // Obtiene el hash de identidad del objeto
            strTemp = Long.toHexString(System.identityHashCode((Object) new String()));
            while (strTemp.length() < 8) strTemp = '0' + strTemp;
            strRetVal += strTemp;
        } catch (UnknownHostException ex) {
            throw new FortimaxException("Excepcion host desconocido: " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new FortimaxException("Excepcion no se encontro el algoritmo: " + ex.getMessage());
        }
        return userCode + strRetVal.toLowerCase();
    }

    public static synchronized int getNextIdDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_documento) FROM imx_documento " + "WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ?");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setInt(3, id_carpeta_padre);
            rs = pstmnt.executeQuery();
            if (rs.next())
                retval = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retval;
    }

    public static boolean insertDocumento(Connection conn, Documento d) throws FortimaxException {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        PreparedStatement stmUpdtFoler = null;
        PreparedStatement psTipoDoc = null;
        PreparedStatement stTp = null;
        ResultSet rsDocto = null;
        ResultSet rsTipoDocto = null;
        ResultSet rsDocto2 = null;
        boolean retVal = false;
        try {
            if (d.getIdDocumento() == -1) {
                pstmnt0 = conn.prepareStatement("SELECT MAX(id_documento) FROM imx_documento WITH(NOLOCK)" + "WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ?");
                pstmnt0.setString(1, d.getTituloAplicacion());
                pstmnt0.setInt(2, d.getIdGabinete());
                pstmnt0.setInt(3, d.getIdCarpetaPadre());
                rsDocto = pstmnt0.executeQuery();
                if (!rsDocto.next())
                    throw new SQLException("No se encontro documento titulo_aplicacion (" + d.getTituloAplicacion() + "), id_gabinete(" + d.getIdGabinete() + "), id_carpeta_padre(" + d.getIdCarpetaPadre() + ")");
                d.setIdDocumento(rsDocto.getInt(1) + 1);
            }
            psTipoDoc = conn.prepareStatement("SELECT id_tipo_docto FROM imx_tipo_documento WITH(NOLOCK) " + "WHERE titulo_aplicacion = ? AND nombre_tipo_docto = ?");
            psTipoDoc.setString(1, d.getTituloAplicacion());
            psTipoDoc.setString(2, d.getNombreTipoDocto());
            rsTipoDocto = psTipoDoc.executeQuery();
            if (!rsTipoDocto.next()) {
                throw new FortimaxException("No se encontro el tipo de documento (" + d.getTituloAplicacion() + "), nombre_tipo_docto(" + d.getNombreTipoDocto() + ")");
            }
            d.setIdTipoDocto(rsTipoDocto.getInt(1));
            if ((d.getIdTipoDocto() != -1) || (d.getIdTipoDocto() != -2)) {
                stTp = conn.prepareStatement("SELECT prioridad FROM imx_tipo_documento  WITH(NOLOCK) " + "WHERE titulo_aplicacion = ? AND id_tipo_docto = ?");
                stTp.setString(1, d.getTituloAplicacion());
                stTp.setInt(2, d.getIdTipoDocto());
                rsDocto2 = stTp.executeQuery();
                if (rsDocto2.next())
                    d.setPrioridad(rsDocto2.getInt(1));
            } else
                d.setIdTipoDocto(-1);
            d.setNumeroAccesos(0);
            d.setNumeroPaginas(0);
            d.setTitulo(null);
            d.setAutor(null);
            d.setMateria("ORIGINAL");
            d.setClaseDocumento(0);
            d.setEstadoDocumento("V");
            d.setTamanoBytes(0);
            d.setCompartir("N");
            d.setTokenCompartir(null);
            pstmnt1 = conn.prepareStatement("INSERT INTO imx_documento ( titulo_aplicacion, id_gabinete, " + "id_carpeta_padre, id_documento, nombre_documento, nombre_usuario, " + "prioridad, id_tipo_docto, fh_creacion, fh_modificacion, numero_accesos, " + "numero_paginas, titulo, autor, materia, descripcion, clase_documento, " + "estado_documento, tamano_bytes, fh_vigencia, iEsVersion) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt1.setString(1, d.getTituloAplicacion());
            pstmnt1.setInt(2, d.getIdGabinete());
            pstmnt1.setInt(3, d.getIdCarpetaPadre());
            pstmnt1.setInt(4, d.getIdDocumento());
            pstmnt1.setString(5, d.getNombreDocumento());
            pstmnt1.setString(6, d.getNombreUsuario());
            pstmnt1.setInt(7, d.getPrioridad());
            pstmnt1.setInt(8, d.getIdTipoDocto());
            pstmnt1.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            pstmnt1.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            pstmnt1.setInt(11, d.getNumeroAccesos());
            pstmnt1.setInt(12, d.getNumeroPaginas());
            pstmnt1.setString(13, d.getTitulo());
            pstmnt1.setString(14, d.getAutor());
            pstmnt1.setString(15, d.getMateria());
            pstmnt1.setString(16, d.getDescripcion());
            pstmnt1.setInt(17, d.getClaseDocumento());
            pstmnt1.setString(18, d.getEstadoDocumento());
            pstmnt1.setDouble(19, d.getTamanoBytes());
            if (d.getFh_vigencia() != null) {
                pstmnt1.setTimestamp(20, d.getFh_vigencia());
            } else
                pstmnt1.setTimestamp(20, null);
            pstmnt1.setInt(21, d.getEsVersion());
            pstmnt1.executeUpdate();
            stmUpdtFoler = conn.prepareStatement("UPDATE imx_carpeta SET numero_documentos = numero_documentos + 1, " + "fh_modificacion = ? WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta = ?");
            stmUpdtFoler.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmUpdtFoler.setString(2, d.getTituloAplicacion());
            stmUpdtFoler.setInt(3, d.getIdGabinete());
            stmUpdtFoler.setInt(4, d.getIdCarpetaPadre());
            stmUpdtFoler.executeUpdate();
            retVal = true;
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(pstmnt0);
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(stmUpdtFoler);
            CloseObject.closeObject(rsDocto);
            CloseObject.closeObject(psTipoDoc);
            CloseObject.closeObject(rsTipoDocto);
            CloseObject.closeObject(stTp);
            CloseObject.closeObject(rsDocto2);
        }
        return retVal;
    }

    public static Pagina insertPaginaDocumento(Connection conn, Volumen v, Caso c, Documento d, String tipo_pagina, double tamano_bytes, String OCRProgramPath, String OCRParameter1, String OCRParameter2, String luceneDbPath, String luceneStopwordsPath, int luceneMergeFactor, int luceneMaxMergeDocs) throws Exception {
        PreparedStatement pstmnt0 = null;
        ResultSet rs = null;
        PreparedStatement pstmnt1 = null;
        Pagina p = null;
        try {
            pstmnt0 = conn.prepareStatement("SELECT MAX(numero_pagina) FROM imx_pagina WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
            pstmnt0.setString(1, d.getTituloAplicacion());
            pstmnt0.setInt(2, d.getIdGabinete());
            pstmnt0.setInt(3, d.getIdCarpetaPadre());
            pstmnt0.setInt(4, d.getIdDocumento());
            rs = pstmnt0.executeQuery();
            if (rs.next()) {
                int numeroPagina = rs.getInt(1) + 1;
                String prefijo = (d.getExtension().equals("xml")) ? "xml" : "ges";
                String filename = DocumentoManager.getNextFilename(d, d.getTituloAplicacion() + "_G" + d.getIdGabinete() + "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento() + "P" + numeroPagina) + ".tif";
                int dot = filename.indexOf(".");
                String filenameWithOutExt = filename.substring(0, dot);
                String nombreArchivoOriginal = filenameWithOutExt + "." + d.getExtension();
                pstmnt1 = conn.prepareStatement("INSERT INTO imx_pagina (titulo_aplicacion, id_gabinete, " + "id_carpeta_padre, id_documento, numero_pagina, volumen, tipo_volumen, " + "nom_archivo_vol, nom_archivo_org, tipo_pagina, estado_pagina, " + "tamano_bytes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                pstmnt1.setString(1, d.getTituloAplicacion());
                pstmnt1.setInt(2, d.getIdGabinete());
                pstmnt1.setInt(3, d.getIdCarpetaPadre());
                pstmnt1.setInt(4, d.getIdDocumento());
                pstmnt1.setInt(5, numeroPagina);
                pstmnt1.setString(6, v.getVolumen());
                pstmnt1.setString(7, v.getTipoVolumen());
                pstmnt1.setString(8, filename);
                pstmnt1.setString(9, nombreArchivoOriginal);
                pstmnt1.setString(10, tipo_pagina);
                pstmnt1.setString(11, "V");
                pstmnt1.setDouble(12, tamano_bytes);
                pstmnt1.executeUpdate();
                Pagina[] pags = d.getPaginasDocumento();
                if (pags.length == 0) {
                    p = new Pagina();
                    p.setTituloAplicacion(d.getTituloAplicacion());
                    p.setIdGabinete(d.getIdGabinete());
                    p.setIdCarpetaPadre(d.getIdCarpetaPadre());
                    p.setIdDocumento(d.getIdDocumento());
                    p.setNumeroPagina(1);
                    p.setVolumen(v.getVolumen());
                    p.setTipoVolumen(v.getTipoVolumen());
                    p.setNomArchivoVol(filename);
                    p.setNomArchivoOrg(nombreArchivoOriginal);
                    p.setTipoPagina(tipo_pagina);
                    p.setEstadoPagina("V");
                    p.setTamanoBytes(tamano_bytes);
                    p.setUnidadDisco(v.getUnidad());
                    p.setRutaBase(v.getRutaBase());
                    p.setRutaDirectorio(v.getRutaDirectorio());
                    pags = new Pagina[1];
                    pags[0] = p;
                    d.setPaginasDocumento(pags);
                }
                d.setNumeroPaginas(d.getNumeroPaginas() + 1);
                // Aqui inserta el registro para el indice de lucene
                // TEXT RETRIEVAL GAF 20071016
                PaginaIndexManager pim = new PaginaIndexManager(OCRProgramPath, OCRParameter1, OCRParameter2, luceneDbPath, luceneStopwordsPath, luceneMergeFactor, luceneMaxMergeDocs);
                pim.insertPaginaIndex(conn, d, nombreArchivoOriginal);
                long time = System.currentTimeMillis();
                d.setFechaModificacion(new Date(time));
                time += (1000L * 60L);
                String[] files = d.getFullPathFilesNames();
                for (int i = 0; i < files.length; i++) {
                    File f = new File(files[i]);
                    if (f.exists()) {
                        f.setLastModified(time);
                        time += (1000L * 60L);
                    }
                }
                updateDocumento(conn, d);
            }
        } finally {
            if (pstmnt1 != null)
                pstmnt1.close();
            if (rs != null)
                rs.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs = null;
            pstmnt0 = null;
            pstmnt1 = null;
        }
        return p;
    }

    public static final void insertaDocumento(Connection conn, String tituloAplicacion, int idGabinete, int idCarpeta, String nombreDocumento, String ext, String nombreUsuario, String archivo) throws Exception {
        OutputStream fos = null;
        InputStream in = null;
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            Documento d = DocumentoManager.getDocumento(conn, tituloAplicacion, idGabinete, idCarpeta, nombreDocumento);
            if (d == null) {
                d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(idCarpeta);
                if ("imx".equals(ext))
                    d.setNombreTipoDocto("IMAX_FILE");
                else
                    d.setNombreTipoDocto("EXTERNO");
                d.setNombreDocumento(nombreDocumento);
                d.setNombreUsuario(nombreUsuario);
                d.setExtension(ext);
                DocumentoManager.insertDocumento(conn, d);
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
            }
            String filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
            if ((d.getExtension() == null) || ("".equals(d.getExtension())))
                d.setExtension(ext);
            fos = new FileOutputStream(filename);
            in = new FileInputStream(new File(archivo));
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                fos.write(buffer, 0, length);
                fileLength += length;
            }
            fos.flush();
            String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
            String[] filenames = d.getFilesNames();
            for (int i = 0; i < filenames.length; i++) {
                if (lowerFilename.equals(filenames[i].toLowerCase())) {
                    Pagina p = d.getPaginaDocumento(i);
                    p.setTamanoBytes(fileLength);
                    PaginaManager.updatePagina(conn, p);
                    break;
                }
            }
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {
                    log.warn("Problemas intentando cerrar el flujo de salida: " + e, e);
                }
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Problemas intentando cerrar el flujo de salida: " + e, e);
                }
            fos = null;
            in = null;
        }
    }

    public static void insertPaginaDocumento(Connection conn, Volumen v, Documento d, String tipo_pagina, double tamano_bytes) throws FortimaxException {
        PreparedStatement pstmnt0 = null;
        ResultSet rs = null;
        PreparedStatement pstmnt1 = null;
        try {
            pstmnt0 = conn.prepareStatement("SELECT MAX(numero_pagina) FROM imx_pagina WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
            pstmnt0.setString(1, d.getTituloAplicacion());
            pstmnt0.setInt(2, d.getIdGabinete());
            pstmnt0.setInt(3, d.getIdCarpetaPadre());
            pstmnt0.setInt(4, d.getIdDocumento());
            rs = pstmnt0.executeQuery();
            if (rs.next()) {
                int numeroPagina = rs.getInt(1) + 1;
                String filename = DocumentoManager.getNextFilename(d, d.getTituloAplicacion() + "_G" + d.getIdGabinete() + "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento() + "P" + numeroPagina) + ".tif";
                int dot = filename.indexOf(".");
                String filenameWithOutExt = filename.substring(0, dot);
                pstmnt1 = conn.prepareStatement("INSERT INTO imx_pagina (titulo_aplicacion, id_gabinete, " + "id_carpeta_padre, id_documento, numero_pagina, volumen, tipo_volumen, " + "nom_archivo_vol, nom_archivo_org, tipo_pagina, estado_pagina, " + "tamano_bytes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                pstmnt1.setString(1, d.getTituloAplicacion());
                pstmnt1.setInt(2, d.getIdGabinete());
                pstmnt1.setInt(3, d.getIdCarpetaPadre());
                pstmnt1.setInt(4, d.getIdDocumento());
                pstmnt1.setInt(5, numeroPagina);
                pstmnt1.setString(6, v.getVolumen());
                pstmnt1.setString(7, v.getTipoVolumen());
                pstmnt1.setString(8, filename);
                pstmnt1.setString(9, filenameWithOutExt + "." + d.getExtension());
                pstmnt1.setString(10, tipo_pagina);
                pstmnt1.setString(11, "V");
                pstmnt1.setDouble(12, tamano_bytes);
                pstmnt1.executeUpdate();
                d.setNumeroPaginas(d.getNumeroPaginas() + 1);
                long time = System.currentTimeMillis();
                d.setFechaModificacion(new Date(time));
                time += (1000L * 60L);
                String[] files = d.getFullPathFilesNames();
                for (int i = 0; i < files.length; i++) {
                    File f = new File(files[i]);
                    f.setLastModified(time);
                    time += (1000L * 60L);
                }
                updateDocumento(conn, d);
            }
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt0);
        }
    }

    public static String insertPaginaDocumentoNuevo(Connection conn, Volumen v, Documento d, String tipo_pagina, double tamano_bytes) throws SQLException {
        PreparedStatement pstmnt1 = null;
        String retVal = "";
        try {
            int numeroPagina = 1;
            String filename = DocumentoManager.getNextFilename(d, d.getTituloAplicacion() + "_G" + d.getIdGabinete() + "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento() + "P" + numeroPagina);
            int dot = filename.indexOf(".");
            String filenameWithOutExt = filename.substring(0, dot);
            pstmnt1 = conn.prepareStatement("INSERT INTO imx_pagina (titulo_aplicacion, id_gabinete, " + "id_carpeta_padre, id_documento, numero_pagina, volumen, tipo_volumen, " + "nom_archivo_vol, nom_archivo_org, tipo_pagina, estado_pagina, " + "tamano_bytes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt1.setString(1, d.getTituloAplicacion());
            pstmnt1.setInt(2, d.getIdGabinete());
            pstmnt1.setInt(3, d.getIdCarpetaPadre());
            pstmnt1.setInt(4, d.getIdDocumento());
            pstmnt1.setInt(5, numeroPagina);
            pstmnt1.setString(6, v.getVolumen());
            pstmnt1.setString(7, v.getTipoVolumen());
            pstmnt1.setString(8, filename);
            pstmnt1.setString(9, d.getNombreDocumento() + "." + d.getExtension());
            pstmnt1.setString(10, tipo_pagina);
            pstmnt1.setString(11, "V");
            pstmnt1.setDouble(12, tamano_bytes);
            pstmnt1.executeUpdate();
            d.setNumeroPaginas(d.getNumeroPaginas() + 1);
            long time = System.currentTimeMillis();
            d.setFechaModificacion(new Date(time));
            time += (1000L * 60L);
            String[] files = d.getFullPathFilesNames();
            for (int i = 0; i < files.length; i++) {
                File f = new File(files[i]);
                f.setLastModified(time);
                time += (1000L * 60L);
            }
            updateDocumento(conn, d);
            retVal = filename;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (pstmnt1 != null)
                pstmnt1.close();
            pstmnt1 = null;
        }
        return retVal;
    }

    public static void limpiaDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento) throws Exception {
        Documento d = DocumentoManager.selectDocumento(conn, titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento);
        if (d != null) {
            Pagina[] p = d.getPaginasDocumento();
            if (p != null && p.length > 0)
                for (int i = 0; i < p.length; i++) PaginaManager.deletePagina(conn, p[i]);
        } else
            throw new GestionException("No existe el documento con ID [" + id_documento + "] en la carpeta con ID " + id_carpeta_padre + "] en el gabinete [" + id_gabinete + "] de la aplicacion [" + titulo_aplicacion + "]");
    }

    public static List<Documento> listaDocumentoExportar(Connection conn, String raiz, String tituloAplicacion, int idGabinete, String nombreDocumento) throws Exception {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs0 = null;
        ResultSet rs1 = null;
        List<Documento> l = new ArrayList<Documento>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT carpeta.id_carpeta         AS id_carpeta, ");
        sb.append("       Max(docto.id_documento)    AS ID_DOCUMENTO, ");
        sb.append("       docto.id_carpeta_padre, ");
        sb.append("       carpeta.nombre_carpeta + '/'");
        sb.append("       + docto.nombre_documento   AS nombre_documento, ");
        sb.append("       docto.nombre_usuario, ");
        sb.append("       docto.prioridad,");
        sb.append("       docto.id_tipo_docto,");
        sb.append("       Max(docto.fh_creacion)     AS fh_creacion,");
        sb.append("       Max(docto.fh_modificacion) AS fh_modificacion,");
        sb.append("       docto.numero_accesos,");
        sb.append("       COUNT(*)                   AS numero_paginas,");
        sb.append("       docto.titulo,");
        sb.append("       docto.autor,");
        sb.append("       docto.materia,");
        sb.append("       docto.descripcion,");
        sb.append("       docto.clase_documento,");
        sb.append("       docto.estado_documento,");
        sb.append("       docto.tamano_bytes,");
        sb.append("       t.nombre_tipo_docto,");
        sb.append("       docto.compartir,");
        sb.append("       docto.token_compartir,");
        sb.append("       fh_vigencia,");
        sb.append("       iesversion ");
        sb.append("  FROM   imx_documento docto ");
        sb.append("       INNER JOIN imx_carpeta carpeta ");
        sb.append("               ON docto.titulo_aplicacion = carpeta.titulo_aplicacion ");
        sb.append("                  AND docto.id_gabinete = carpeta.id_gabinete ");
        sb.append("                  AND docto.id_carpeta_padre = carpeta.id_carpeta ");
        sb.append("       INNER JOIN imx_pagina pagina ");
        sb.append("               ON docto.titulo_aplicacion = pagina.titulo_aplicacion ");
        sb.append("                  AND docto.id_gabinete = pagina.id_gabinete ");
        sb.append("                  AND docto.id_carpeta_padre = pagina.id_carpeta_padre ");
        sb.append("                  AND docto.id_documento = pagina.id_documento ");
        sb.append("       INNER JOIN imx_volumen volumen ");
        sb.append("               ON pagina.volumen = volumen.volumen ");
        sb.append("       INNER JOIN imx_tipo_documento t WITH(nolock) ");
        sb.append("               ON t.titulo_aplicacion = docto.titulo_aplicacion ");
        sb.append("                  AND t.id_tipo_docto = docto.id_tipo_docto ");
        sb.append("WHERE  docto.titulo_aplicacion = ? ");
        sb.append("       AND docto.id_gabinete = ? ");
        sb.append("       AND docto.NOMBRE_DOCUMENTO = ? ");
        sb.append("GROUP  BY carpeta.id_carpeta,");
        sb.append("          nombre_carpeta,");
        sb.append("          docto.nombre_documento,");
        sb.append("          docto.nombre_usuario,");
        sb.append("          docto.prioridad,");
        sb.append("          docto.id_tipo_docto,");
        sb.append("          docto.numero_accesos,");
        sb.append("          docto.titulo,");
        sb.append("          docto.autor,");
        sb.append("          docto.materia,");
        sb.append("          docto.descripcion,");
        sb.append("          docto.clase_documento, ");
        sb.append("          docto.estado_documento, ");
        sb.append("          docto.tamano_bytes, ");
        sb.append("          t.nombre_tipo_docto, ");
        sb.append("          docto.compartir, ");
        sb.append("          docto.token_compartir, ");
        sb.append("          fh_vigencia, ");
        sb.append("          iesversion, ");
        sb.append("          docto.id_carpeta_padre  ");
        // log.debug( sb );
        StringBuilder queryPagina = new StringBuilder();
        queryPagina.append("SELECT p.numero_pagina,  ");
        queryPagina.append("        p.tipo_pagina,  ");
        queryPagina.append("        p.volumen,  ");
        queryPagina.append("        p.tipo_volumen,  ");
        queryPagina.append("        v.unidad_disco,  ");
        queryPagina.append("        v.ruta_base,  ");
        queryPagina.append("        v.ruta_directorio,  ");
        queryPagina.append("        p.nom_archivo_vol,  ");
        queryPagina.append("        p.nom_archivo_org,  ");
        queryPagina.append("        p.estado_pagina,  ");
        queryPagina.append("        p.tamano_bytes  ");
        queryPagina.append(" FROM   imx_pagina p,  ");
        queryPagina.append("        imx_volumen v WITH (nolock)  ");
        queryPagina.append(" WHERE  v.volumen = p.volumen  ");
        queryPagina.append("        AND p.titulo_aplicacion = ?  ");
        queryPagina.append("        AND p.id_gabinete = ?  ");
        queryPagina.append("        AND p.id_carpeta_padre = ?  ");
        queryPagina.append("        AND p.id_documento = ?  ");
        queryPagina.append(" ORDER  BY p.numero_pagina   ");
        try {
            pstmnt0 = conn.prepareStatement(sb.toString());
            pstmnt1 = conn.prepareStatement(queryPagina.toString());
            pstmnt0.setString(1, tituloAplicacion);
            pstmnt0.setInt(2, idGabinete);
            pstmnt0.setString(3, nombreDocumento);
            rs0 = pstmnt0.executeQuery();
            if (rs0.next()) {
                Documento d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(rs0.getInt("id_carpeta"));
                d.setIdDocumento(rs0.getInt("id_documento"));
                d.setNombreDocumento(raiz + "/" + rs0.getString("nombre_documento"));
                d.setNombreUsuario(rs0.getString("nombre_usuario"));
                d.setPrioridad(rs0.getInt("prioridad"));
                d.setIdTipoDocto(rs0.getInt("id_tipo_docto"));
                d.setNombreTipoDocto(rs0.getString("nombre_tipo_docto"));
                d.setFechaCreacion(new Date(rs0.getTimestamp("fh_creacion").getTime()));
                d.setFechaModificacion(new Date(rs0.getTimestamp("fh_modificacion").getTime()));
                d.setNumeroAccesos(rs0.getInt("numero_accesos"));
                d.setNumeroPaginas(rs0.getInt("numero_paginas"));
                d.setTitulo(rs0.getString("titulo"));
                d.setAutor(rs0.getString("autor"));
                d.setMateria(rs0.getString("materia"));
                d.setDescripcion(rs0.getString("descripcion"));
                d.setClaseDocumento(rs0.getInt("clase_documento"));
                d.setEstadoDocumento(rs0.getString("estado_documento"));
                d.setTamanoBytes(rs0.getDouble("tamano_bytes"));
                d.setCompartir(rs0.getString("compartir"));
                d.setTokenCompartir(rs0.getString("token_compartir"));
                d.setFh_vigencia(rs0.getTimestamp("fh_vigencia"));
                d.setEsVersion(rs0.getInt("iEsVersion"));
                pstmnt1.setString(1, d.getTituloAplicacion());
                pstmnt1.setInt(2, d.getIdGabinete());
                pstmnt1.setInt(3, d.getIdCarpetaPadre());
                pstmnt1.setInt(4, d.getIdDocumento());
                rs1 = pstmnt1.executeQuery();
                boolean prvez = true;
                List<Pagina> lp = new ArrayList<Pagina>();
                if (rs1.next()) {
                    Pagina p = new Pagina();
                    p.setTituloAplicacion(d.getTituloAplicacion());
                    p.setIdGabinete(d.getIdGabinete());
                    p.setIdCarpetaPadre(d.getIdCarpetaPadre());
                    p.setIdDocumento(d.getIdDocumento());
                    p.setNumeroPagina(rs1.getInt("numero_pagina"));
                    p.setVolumen(rs1.getString("volumen"));
                    p.setTipoVolumen(rs1.getString("tipo_volumen"));
                    p.setNomArchivoVol(rs1.getString("nom_archivo_vol").trim());
                    p.setNomArchivoOrg(rs1.getString("nom_archivo_org").trim());
                    p.setTipoPagina(rs1.getString("tipo_pagina"));
                    p.setEstadoPagina(rs1.getString("estado_pagina"));
                    p.setTamanoBytes(rs1.getDouble("tamano_bytes"));
                    p.setUnidadDisco(rs1.getString("unidad_disco").trim());
                    p.setRutaBase(rs1.getString("ruta_base").trim());
                    p.setRutaDirectorio(rs1.getString("ruta_directorio").trim());
                    lp.add(p);
                    if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs1.getString("nom_archivo_org") != null)) {
                        prvez = false;
                        int pos = rs1.getString("nom_archivo_org").lastIndexOf(".");
                        d.setExtension((pos > -1) ? rs1.getString("nom_archivo_org").substring(pos + 1) : "");
                    }
                }
                rs1.close();
                rs1 = null;
                d.setNumeroPaginas(lp.size());
                Pagina[] pages = new Pagina[lp.size()];
                for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
                d.setPaginasDocumento(pages);
                l.add(d);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(rs0);
            CloseObject.closeObject(pstmnt0);
        }
    }

    public static List<Documento> listaDocumentosExportar(Connection conn, String tituloAplicacion, int idGabinete, String raiz) throws Exception {
        return listaDocumentosExportar(conn, tituloAplicacion, idGabinete, raiz, -1);
    }

    public static List<Documento> listaDocumentosExportar(Connection conn, String tituloAplicacion, int idGabinete, String raiz, int idCarpeta) throws Exception {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs0 = null;
        ResultSet rs1 = null;
        List<Documento> l = new ArrayList<Documento>();
        String query = "SELECT  carpeta.ID_CARPETA AS id_carpeta," + "        MAX(d.ID_DOCUMENTO) AS ID_DOCUMENTO," + "        nombre_carpeta + '/' + d.nombre_documento AS nombre_documento, " + "        d.nombre_usuario, " + "        d.prioridad, " + "        d.id_tipo_docto, " + "        MAX(d.fh_creacion) AS fh_creacion, " + "        MAX(d.fh_modificacion) AS fh_modificacion, " + "        d.numero_accesos, " + "        Sum(CASE " + "              WHEN pagina.numero_pagina IS NULL THEN 0 " + "              ELSE 1 " + "            END)                                  AS numero_paginas, " + "        d.titulo, " + "        d.autor, " + "        d.materia, " + "        d.descripcion, " + "        d.clase_documento, " + "        d.estado_documento, " + "        d.tamano_bytes, " + "        t.nombre_tipo_docto, " + "        d.compartir, " + "        d.token_compartir, " + "        fh_vigencia, " + "        iesversion " + " FROM   imx_documento d WITH(nolock) " + "        INNER JOIN imx_tipo_documento t WITH(nolock) " + "                ON t.titulo_aplicacion = d.titulo_aplicacion " + "                   AND t.id_tipo_docto = d.id_tipo_docto " + "        LEFT OUTER JOIN dbo.imx_pagina pagina WITH(nolock) " + "                     ON d.titulo_aplicacion = pagina.titulo_aplicacion " + "                        AND d.id_gabinete = pagina.id_gabinete " + "                        AND d.id_carpeta_padre = pagina.id_carpeta_padre " + "                        AND d.id_documento = pagina.id_documento " + "        LEFT OUTER JOIN imx_carpeta carpeta WITH(nolock) " + "                     ON d.titulo_aplicacion = carpeta.titulo_aplicacion " + "                        AND d.id_gabinete = carpeta.id_gabinete " + "                        AND d.id_carpeta_padre = carpeta.id_carpeta " + " WHERE  d.titulo_aplicacion = ? " + "        AND d.id_gabinete = ? " + "        AND pagina.titulo_aplicacion IS NOT NULL " + "        AND d.id_carpeta_padre <> 0 " + (idCarpeta > 0 ? "        AND carpeta.id_carpeta = ?" : "") + " GROUP  BY carpeta.ID_CARPETA," + "           nombre_carpeta, " + "           d.nombre_documento, " + "           d.nombre_usuario, " + "           d.prioridad, " + "           d.id_tipo_docto, " + "           d.numero_accesos, " + "           d.titulo, " + "           d.autor, " + "           d.materia, " + "           d.descripcion, " + "           d.clase_documento, " + "           d.estado_documento, " + "           d.tamano_bytes, " + "           t.nombre_tipo_docto, " + "           d.compartir, " + "           d.token_compartir, " + "           fh_vigencia, " + "           iesversion   ";
        String queryPagina = "SELECT p.numero_pagina,  " + "        p.tipo_pagina,  " + "        p.volumen,  " + "        p.tipo_volumen,  " + "        v.unidad_disco,  " + "        v.ruta_base,  " + "        v.ruta_directorio,  " + "        p.nom_archivo_vol,  " + "        p.nom_archivo_org,  " + "        p.estado_pagina,  " + "        p.tamano_bytes  " + " FROM   imx_pagina p,  " + "        imx_volumen v WITH (nolock)  " + " WHERE  v.volumen = p.volumen  " + "        AND p.titulo_aplicacion = ?  " + "        AND p.id_gabinete = ?  " + "        AND p.id_carpeta_padre = ?  " + "        AND p.id_documento = ?  " + " ORDER  BY p.numero_pagina   ";
        try {
            pstmnt0 = conn.prepareStatement(query);
            pstmnt1 = conn.prepareStatement(queryPagina);
            pstmnt0.setString(1, tituloAplicacion);
            pstmnt0.setInt(2, idGabinete);
            if (idCarpeta > 0)
                pstmnt0.setInt(3, idCarpeta);
            rs0 = pstmnt0.executeQuery();
            while (rs0.next()) {
                Documento d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(rs0.getInt("id_carpeta"));
                d.setIdDocumento(rs0.getInt("id_documento"));
                d.setNombreDocumento(raiz + "/" + rs0.getString("nombre_documento"));
                d.setNombreUsuario(rs0.getString("nombre_usuario"));
                d.setPrioridad(rs0.getInt("prioridad"));
                d.setIdTipoDocto(rs0.getInt("id_tipo_docto"));
                d.setNombreTipoDocto(rs0.getString("nombre_tipo_docto"));
                d.setFechaCreacion(new Date(rs0.getTimestamp("fh_creacion").getTime()));
                d.setFechaModificacion(new Date(rs0.getTimestamp("fh_modificacion").getTime()));
                d.setNumeroAccesos(rs0.getInt("numero_accesos"));
                d.setNumeroPaginas(rs0.getInt("numero_paginas"));
                d.setTitulo(rs0.getString("titulo"));
                d.setAutor(rs0.getString("autor"));
                d.setMateria(rs0.getString("materia"));
                d.setDescripcion(rs0.getString("descripcion"));
                d.setClaseDocumento(rs0.getInt("clase_documento"));
                d.setEstadoDocumento(rs0.getString("estado_documento"));
                d.setTamanoBytes(rs0.getDouble("tamano_bytes"));
                d.setCompartir(rs0.getString("compartir"));
                d.setTokenCompartir(rs0.getString("token_compartir"));
                d.setFh_vigencia(rs0.getTimestamp("fh_vigencia"));
                d.setEsVersion(rs0.getInt("iEsVersion"));
                pstmnt1.setString(1, d.getTituloAplicacion());
                pstmnt1.setInt(2, d.getIdGabinete());
                pstmnt1.setInt(3, d.getIdCarpetaPadre());
                pstmnt1.setInt(4, d.getIdDocumento());
                rs1 = pstmnt1.executeQuery();
                boolean prvez = true;
                List<Pagina> lp = new ArrayList<Pagina>();
                while (rs1.next()) {
                    Pagina p = new Pagina();
                    p.setTituloAplicacion(d.getTituloAplicacion());
                    p.setIdGabinete(d.getIdGabinete());
                    p.setIdCarpetaPadre(d.getIdCarpetaPadre());
                    p.setIdDocumento(d.getIdDocumento());
                    p.setNumeroPagina(rs1.getInt("numero_pagina"));
                    p.setVolumen(rs1.getString("volumen"));
                    p.setTipoVolumen(rs1.getString("tipo_volumen"));
                    p.setNomArchivoVol(rs1.getString("nom_archivo_vol").trim());
                    p.setNomArchivoOrg(rs1.getString("nom_archivo_org").trim());
                    p.setTipoPagina(rs1.getString("tipo_pagina"));
                    p.setEstadoPagina(rs1.getString("estado_pagina"));
                    p.setTamanoBytes(rs1.getDouble("tamano_bytes"));
                    p.setUnidadDisco(rs1.getString("unidad_disco").trim());
                    p.setRutaBase(rs1.getString("ruta_base").trim());
                    p.setRutaDirectorio(rs1.getString("ruta_directorio").trim());
                    lp.add(p);
                    if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs1.getString("nom_archivo_org") != null)) {
                        prvez = false;
                        int pos = rs1.getString("nom_archivo_org").lastIndexOf(".");
                        d.setExtension((pos > -1) ? rs1.getString("nom_archivo_org").substring(pos + 1) : "");
                    }
                }
                rs1.close();
                rs1 = null;
                d.setNumeroPaginas(lp.size());
                Pagina[] pages = new Pagina[lp.size()];
                for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
                d.setPaginasDocumento(pages);
                l.add(d);
            }
            return l;
        } finally {
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(rs0);
            CloseObject.closeObject(pstmnt0);
        }
    }

    public static List<Documento> listaDocumentosExportarAlimentacion(Connection conn, String tituloAplicacion, int idGabinete, String nombreCarpeta, File[] listaDocumentosReemplazo) throws Exception {
        List<Documento> l = new ArrayList<Documento>();
        for (int cnt = 0; cnt < listaDocumentosReemplazo.length; cnt++) {
            Documento d = new Documento();
            File f = listaDocumentosReemplazo[cnt];
            d.setTituloAplicacion(tituloAplicacion);
            d.setIdGabinete(idGabinete);
            d.setIdCarpetaPadre(0);
            d.setIdDocumento(cnt + 1);
            d.setNombreDocumento(nombreCarpeta + "/" + CFDIUtils.getFileWithoutExtencion(f.getName()));
            d.setExtension(CFDIUtils.getFileExtencion(f.getName()));
            d.setNombreUsuario("");
            d.setPrioridad(1);
            d.setIdTipoDocto(3);
            d.setNombreTipoDocto("");
            d.setFechaCreacion(new Date(System.currentTimeMillis()));
            d.setFechaModificacion(new Date(System.currentTimeMillis()));
            d.setNumeroAccesos(0);
            d.setNumeroPaginas(1);
            d.setTitulo(CFDIUtils.getFileWithoutExtencion(f.getName()));
            d.setAutor("");
            d.setMateria("");
            d.setDescripcion(CFDIUtils.getFileWithoutExtencion(f.getName()));
            d.setClaseDocumento(0);
            d.setEstadoDocumento("V");
            d.setTamanoBytes(0);
            d.setCompartir("N");
            d.setTokenCompartir(null);
            d.setFh_vigencia(null);
            d.setEsVersion(0);
            List<Pagina> lp = new ArrayList<Pagina>();
            Pagina p = new Pagina();
            p.setTituloAplicacion(d.getTituloAplicacion());
            p.setIdGabinete(d.getIdGabinete());
            p.setIdCarpetaPadre(d.getIdCarpetaPadre());
            p.setIdDocumento(d.getIdDocumento());
            p.setNumeroPagina(1);
            p.setNomArchivoVol(f.getAbsolutePath());
            lp.add(p);
            Pagina[] pages = new Pagina[lp.size()];
            for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
            d.setPaginasDocumento(pages);
            l.add(d);
        }
        return l;
    }

    public static List<Documento> listaDocumentosExportarAuditoria(Connection conn, String tituloAplicacion, int idGabinete, String carpetaRaiz, String nombreCarpeta, String nombreDocumento, String concepto) throws Exception {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs0 = null;
        ResultSet rs1 = null;
        List<Documento> l = new ArrayList<Documento>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT  carpeta.ID_CARPETA AS id_carpeta,");
        query.append("        MAX(d.ID_DOCUMENTO) AS ID_DOCUMENTO,");
        query.append("        nombre_carpeta + '/' + d.nombre_documento AS nombre_documento, ");
        query.append("        d.nombre_usuario, ");
        query.append("        d.prioridad, ");
        query.append("        d.id_tipo_docto, ");
        query.append("        MAX(d.fh_creacion) AS fh_creacion, ");
        query.append("        MAX(d.fh_modificacion) AS fh_modificacion, ");
        query.append("        d.numero_accesos, ");
        query.append("        Sum(CASE ");
        query.append("              WHEN pagina.numero_pagina IS NULL THEN 0 ");
        query.append("              ELSE 1 ");
        query.append("            END)                                  AS numero_paginas, ");
        query.append("        d.titulo, ");
        query.append("        d.autor, ");
        query.append("        d.materia, ");
        query.append("        d.descripcion, ");
        query.append("        d.clase_documento, ");
        query.append("        d.estado_documento, ");
        query.append("        d.tamano_bytes, ");
        query.append("        t.nombre_tipo_docto, ");
        query.append("        d.compartir, ");
        query.append("        d.token_compartir, ");
        query.append("        fh_vigencia, ");
        query.append("        iesversion ");
        query.append(" FROM   imx_documento d WITH(nolock) ");
        query.append("        INNER JOIN imx_tipo_documento t WITH(nolock) ");
        query.append("                ON t.titulo_aplicacion = d.titulo_aplicacion ");
        query.append("                   AND t.id_tipo_docto = d.id_tipo_docto ");
        query.append("        LEFT OUTER JOIN dbo.imx_pagina pagina WITH(nolock) ");
        query.append("                     ON d.titulo_aplicacion = pagina.titulo_aplicacion ");
        query.append("                        AND d.id_gabinete = pagina.id_gabinete ");
        query.append("                        AND d.id_carpeta_padre = pagina.id_carpeta_padre ");
        query.append("                        AND d.id_documento = pagina.id_documento ");
        query.append("        LEFT OUTER JOIN imx_carpeta carpeta WITH(nolock) ");
        query.append("                     ON d.titulo_aplicacion = carpeta.titulo_aplicacion ");
        query.append("                        AND d.id_gabinete = carpeta.id_gabinete ");
        query.append("                        AND d.id_carpeta_padre = carpeta.id_carpeta ");
        query.append(" WHERE  d.titulo_aplicacion = ? ");
        query.append("        AND d.id_gabinete = ? ");
        query.append("        AND nombre_carpeta = ? ");
        query.append((StringUtils.isEmpty(nombreDocumento) ? "" : " AND d.NOMBRE_DOCUMENTO like ? + '%' "));
        query.append("        AND pagina.titulo_aplicacion IS NOT NULL ");
        query.append("        AND d.id_carpeta_padre <> 0 ");
        query.append("        AND iesversion = 0");
        query.append(" GROUP  BY carpeta.ID_CARPETA,");
        query.append("           nombre_carpeta, ");
        query.append("           d.nombre_documento, ");
        query.append("           d.nombre_usuario, ");
        query.append("           d.prioridad, ");
        query.append("           d.id_tipo_docto, ");
        query.append("           d.numero_accesos, ");
        query.append("           d.titulo, ");
        query.append("           d.autor, ");
        query.append("           d.materia, ");
        query.append("           d.descripcion, ");
        query.append("           d.clase_documento, ");
        query.append("           d.estado_documento, ");
        query.append("           d.tamano_bytes, ");
        query.append("           t.nombre_tipo_docto, ");
        query.append("           d.compartir, ");
        query.append("           d.token_compartir, ");
        query.append("           fh_vigencia, ");
        query.append("           iesversion   ");
        StringBuilder queryPagina = new StringBuilder();
        queryPagina.append("SELECT p.numero_pagina,  ");
        queryPagina.append("        p.tipo_pagina,  ");
        queryPagina.append("        p.volumen,  ");
        queryPagina.append("        p.tipo_volumen,  ");
        queryPagina.append("        v.unidad_disco,  ");
        queryPagina.append("        v.ruta_base,  ");
        queryPagina.append("        v.ruta_directorio,  ");
        queryPagina.append("        p.nom_archivo_vol,  ");
        queryPagina.append("        p.nom_archivo_org,  ");
        queryPagina.append("        p.estado_pagina,  ");
        queryPagina.append("        p.tamano_bytes  ");
        queryPagina.append(" FROM   imx_pagina p,  ");
        queryPagina.append("        imx_volumen v WITH (nolock)  ");
        queryPagina.append(" WHERE  v.volumen = p.volumen  ");
        queryPagina.append("        AND p.titulo_aplicacion = ?  ");
        queryPagina.append("        AND p.id_gabinete = ?  ");
        queryPagina.append("        AND p.id_carpeta_padre = ?  ");
        queryPagina.append("        AND p.id_documento = ?  ");
        queryPagina.append(" ORDER  BY p.numero_pagina   ");
        try {
            pstmnt0 = conn.prepareStatement(query.toString());
            pstmnt1 = conn.prepareStatement(queryPagina.toString());
            pstmnt0.setString(1, tituloAplicacion);
            pstmnt0.setInt(2, idGabinete);
            pstmnt0.setString(3, nombreCarpeta);
            if (!StringUtils.isEmpty(nombreDocumento))
                pstmnt0.setString(4, nombreDocumento);
            rs0 = pstmnt0.executeQuery();
            while (rs0.next()) {
                if (rs0.getString("nombre_documento").toLowerCase().indexOf("acuse") >= 0)
                    continue;
                Documento d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(rs0.getInt("id_carpeta"));
                d.setIdDocumento(rs0.getInt("id_documento"));
                if (rs0.getString("nombre_documento").contains("Solicitud de Pago/Solicitud de Pago Firmada"))
                    d.setNombreDocumento(carpetaRaiz + "/Informe de la comisi\u00F3n/Informe de la comisi\u00F3n.pdf");
                else
                    d.setNombreDocumento(carpetaRaiz + "/" + rs0.getString("nombre_documento"));
                d.setNombreUsuario(rs0.getString("nombre_usuario"));
                d.setPrioridad(rs0.getInt("prioridad"));
                d.setIdTipoDocto(rs0.getInt("id_tipo_docto"));
                d.setNombreTipoDocto(rs0.getString("nombre_tipo_docto"));
                d.setFechaCreacion(new Date(rs0.getTimestamp("fh_creacion").getTime()));
                d.setFechaModificacion(new Date(rs0.getTimestamp("fh_modificacion").getTime()));
                d.setNumeroAccesos(rs0.getInt("numero_accesos"));
                d.setNumeroPaginas(rs0.getInt("numero_paginas"));
                d.setTitulo(rs0.getString("titulo"));
                d.setAutor(rs0.getString("autor"));
                d.setMateria(rs0.getString("materia"));
                d.setDescripcion(rs0.getString("descripcion"));
                d.setClaseDocumento(rs0.getInt("clase_documento"));
                d.setEstadoDocumento(rs0.getString("estado_documento"));
                d.setTamanoBytes(rs0.getDouble("tamano_bytes"));
                d.setCompartir(rs0.getString("compartir"));
                d.setTokenCompartir(rs0.getString("token_compartir"));
                d.setFh_vigencia(rs0.getTimestamp("fh_vigencia"));
                d.setEsVersion(rs0.getInt("iEsVersion"));
                pstmnt1.setString(1, d.getTituloAplicacion());
                pstmnt1.setInt(2, d.getIdGabinete());
                pstmnt1.setInt(3, d.getIdCarpetaPadre());
                pstmnt1.setInt(4, d.getIdDocumento());
                rs1 = pstmnt1.executeQuery();
                boolean prvez = true;
                List<Pagina> lp = new ArrayList<Pagina>();
                while (rs1.next()) {
                    Pagina p = new Pagina();
                    p.setTituloAplicacion(d.getTituloAplicacion());
                    p.setIdGabinete(d.getIdGabinete());
                    p.setIdCarpetaPadre(d.getIdCarpetaPadre());
                    p.setIdDocumento(d.getIdDocumento());
                    p.setNumeroPagina(rs1.getInt("numero_pagina"));
                    p.setVolumen(rs1.getString("volumen"));
                    p.setTipoVolumen(rs1.getString("tipo_volumen"));
                    p.setNomArchivoVol(rs1.getString("nom_archivo_vol").trim());
                    p.setNomArchivoOrg(rs1.getString("nom_archivo_org").trim());
                    p.setTipoPagina(rs1.getString("tipo_pagina"));
                    p.setEstadoPagina(rs1.getString("estado_pagina"));
                    p.setTamanoBytes(rs1.getDouble("tamano_bytes"));
                    p.setUnidadDisco(rs1.getString("unidad_disco").trim());
                    p.setRutaBase(rs1.getString("ruta_base").trim());
                    p.setRutaDirectorio(rs1.getString("ruta_directorio").trim());
                    int pos = rs1.getString("nom_archivo_org").lastIndexOf(".");
                    String extensionArchivo = (pos > -1) ? rs1.getString("nom_archivo_org").substring(pos + 1) : "";
                    d.setExtension(extensionArchivo);
                    if ("Solicitud de Pago Firmada".equalsIgnoreCase(nombreDocumento)) {
                        ImageManipulator im = new ImageManipulator();
                        if (StringUtils.isEmpty(d.getExtension()))
                            d.setExtension("pdf");
                        if ("pdf".equalsIgnoreCase(d.getExtension()))
                            p.setNomArchivoVol(im.protegeSolicitudPago(p.getFullPathFileName(), concepto));
                        else {
                            p.setNomArchivoVol(im.protegeSolicitudPagoImg(p.getFullPathFileName(), extensionArchivo));
                            d.setExtension("pdf");
                            extensionArchivo = "pdf";
                        }
                        p.setUnidadDisco("");
                        p.setRutaBase("");
                        p.setRutaDirectorio("");
                    }
                    lp.add(p);
                    if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs1.getString("nom_archivo_org") != null)) {
                        prvez = false;
                        d.setExtension(extensionArchivo);
                    }
                }
                rs1.close();
                rs1 = null;
                d.setNumeroPaginas(lp.size());
                Pagina[] pages = new Pagina[lp.size()];
                for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
                d.setPaginasDocumento(pages);
                l.add(d);
                /* Aqui llamar al jasper para generar la version editable */
            }
            return l;
        } finally {
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(rs0);
            CloseObject.closeObject(pstmnt0);
        }
    }

    public static List<Documento> listaDocumentosExportarAuditoriaCFDI(Connection conn, CFDIMaskGenerator maskPDF, String tituloAplicacion, int idGabinete, String carpetaRaiz, String nombreCarpeta, String nombreDocumento, String concepto) throws Exception {
        PreparedStatement pstmnt0 = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs0 = null;
        ResultSet rs1 = null;
        List<Documento> l = new ArrayList<Documento>();
        String query = "SELECT  carpeta.ID_CARPETA AS id_carpeta," + "        MAX(d.ID_DOCUMENTO) AS ID_DOCUMENTO," + "        nombre_carpeta + '/' + d.nombre_documento AS nombre_documento, " + "        d.nombre_usuario, " + "        d.prioridad, " + "        d.id_tipo_docto, " + "        MAX(d.fh_creacion) AS fh_creacion, " + "        MAX(d.fh_modificacion) AS fh_modificacion, " + "        d.numero_accesos, " + "        Sum(CASE " + "              WHEN pagina.numero_pagina IS NULL THEN 0 " + "              ELSE 1 " + "            END)                                  AS numero_paginas, " + "        d.titulo, " + "        d.autor, " + "        d.materia, " + "        d.descripcion, " + "        d.clase_documento, " + "        d.estado_documento, " + "        d.tamano_bytes, " + "        t.nombre_tipo_docto, " + "        d.compartir, " + "        d.token_compartir, " + "        fh_vigencia, " + "        iesversion " + " FROM   imx_documento d WITH(nolock) " + "        INNER JOIN imx_tipo_documento t WITH(nolock) " + "                ON t.titulo_aplicacion = d.titulo_aplicacion " + "                   AND t.id_tipo_docto = d.id_tipo_docto " + "        LEFT OUTER JOIN dbo.imx_pagina pagina WITH(nolock) " + "                     ON d.titulo_aplicacion = pagina.titulo_aplicacion " + "                        AND d.id_gabinete = pagina.id_gabinete " + "                        AND d.id_carpeta_padre = pagina.id_carpeta_padre " + "                        AND d.id_documento = pagina.id_documento " + "        LEFT OUTER JOIN imx_carpeta carpeta WITH(nolock) " + "                     ON d.titulo_aplicacion = carpeta.titulo_aplicacion " + "                        AND d.id_gabinete = carpeta.id_gabinete " + "                        AND d.id_carpeta_padre = carpeta.id_carpeta " + " WHERE  d.titulo_aplicacion = ? " + "        AND d.id_gabinete = ? " + "        AND nombre_carpeta = ? " + (StringUtils.isEmpty(nombreDocumento) ? "" : " AND d.NOMBRE_DOCUMENTO like ? + '%' ") + "        AND pagina.titulo_aplicacion IS NOT NULL " + "        AND d.id_carpeta_padre <> 0 " + "        AND iesversion = 0" + "         AND NOMBRE_DOCUMENTO LIKE '%.xml'" + " GROUP  BY carpeta.ID_CARPETA," + "           nombre_carpeta, " + "           d.nombre_documento, " + "           d.nombre_usuario, " + "           d.prioridad, " + "           d.id_tipo_docto, " + "           d.numero_accesos, " + "           d.titulo, " + "           d.autor, " + "           d.materia, " + "           d.descripcion, " + "           d.clase_documento, " + "           d.estado_documento, " + "           d.tamano_bytes, " + "           t.nombre_tipo_docto, " + "           d.compartir, " + "           d.token_compartir, " + "           fh_vigencia, " + "           iesversion   ";
        String queryPagina = "SELECT p.numero_pagina,  " + "        p.tipo_pagina,  " + "        p.volumen,  " + "        p.tipo_volumen,  " + "        v.unidad_disco,  " + "        v.ruta_base,  " + "        v.ruta_directorio,  " + "        p.nom_archivo_vol,  " + "        p.nom_archivo_org,  " + "        p.estado_pagina,  " + "        p.tamano_bytes  " + " FROM   imx_pagina p,  " + "        imx_volumen v WITH (nolock)  " + " WHERE  v.volumen = p.volumen  " + "        AND p.titulo_aplicacion = ?  " + "        AND p.id_gabinete = ?  " + "        AND p.id_carpeta_padre = ?  " + "        AND p.id_documento = ?  " + " ORDER  BY p.numero_pagina   ";
        try {
            pstmnt0 = conn.prepareStatement(query);
            pstmnt1 = conn.prepareStatement(queryPagina);
            pstmnt0.setString(1, tituloAplicacion);
            pstmnt0.setInt(2, idGabinete);
            pstmnt0.setString(3, nombreCarpeta);
            if (!StringUtils.isEmpty(nombreDocumento))
                pstmnt0.setString(4, nombreDocumento);
            rs0 = pstmnt0.executeQuery();
            while (rs0.next()) {
                Documento d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(rs0.getInt("id_carpeta"));
                d.setIdDocumento(rs0.getInt("id_documento"));
                d.setNombreDocumento(carpetaRaiz + "/" + CFDIUtils.getFileWithoutExtencion(rs0.getString("nombre_documento")) + ".pdf");
                d.setNombreUsuario(rs0.getString("nombre_usuario"));
                d.setPrioridad(rs0.getInt("prioridad"));
                d.setIdTipoDocto(rs0.getInt("id_tipo_docto"));
                d.setNombreTipoDocto(rs0.getString("nombre_tipo_docto"));
                d.setFechaCreacion(new Date(rs0.getTimestamp("fh_creacion").getTime()));
                d.setFechaModificacion(new Date(rs0.getTimestamp("fh_modificacion").getTime()));
                d.setNumeroAccesos(rs0.getInt("numero_accesos"));
                d.setNumeroPaginas(rs0.getInt("numero_paginas"));
                d.setTitulo(rs0.getString("titulo"));
                d.setAutor(rs0.getString("autor"));
                d.setMateria(rs0.getString("materia"));
                d.setDescripcion(rs0.getString("descripcion"));
                d.setClaseDocumento(rs0.getInt("clase_documento"));
                d.setEstadoDocumento(rs0.getString("estado_documento"));
                d.setTamanoBytes(rs0.getDouble("tamano_bytes"));
                d.setCompartir(rs0.getString("compartir"));
                d.setTokenCompartir(rs0.getString("token_compartir"));
                d.setFh_vigencia(rs0.getTimestamp("fh_vigencia"));
                d.setEsVersion(rs0.getInt("iEsVersion"));
                pstmnt1.setString(1, d.getTituloAplicacion());
                pstmnt1.setInt(2, d.getIdGabinete());
                pstmnt1.setInt(3, d.getIdCarpetaPadre());
                pstmnt1.setInt(4, d.getIdDocumento());
                rs1 = pstmnt1.executeQuery();
                boolean prvez = true;
                List<Pagina> lp = new ArrayList<Pagina>();
                while (rs1.next()) {
                    Pagina p = new Pagina();
                    p.setTituloAplicacion(d.getTituloAplicacion());
                    p.setIdGabinete(d.getIdGabinete());
                    p.setIdCarpetaPadre(d.getIdCarpetaPadre());
                    p.setIdDocumento(d.getIdDocumento());
                    p.setNumeroPagina(rs1.getInt("numero_pagina"));
                    p.setVolumen(rs1.getString("volumen"));
                    p.setTipoVolumen(rs1.getString("tipo_volumen"));
                    p.setNomArchivoVol(rs1.getString("nom_archivo_vol").trim());
                    p.setNomArchivoOrg(rs1.getString("nom_archivo_org").trim());
                    p.setTipoPagina(rs1.getString("tipo_pagina"));
                    p.setEstadoPagina(rs1.getString("estado_pagina"));
                    p.setTamanoBytes(rs1.getDouble("tamano_bytes"));
                    p.setUnidadDisco(rs1.getString("unidad_disco").trim());
                    p.setRutaBase(rs1.getString("ruta_base").trim());
                    p.setRutaDirectorio(rs1.getString("ruta_directorio").trim());
                    //int pos = rs1.getString( "nom_archivo_org" ).lastIndexOf( "." );
                    //String extensionArchivo = ( pos > -1 ) ? rs1.getString( "nom_archivo_org" ).substring( pos + 1 ) : "";
                    d.setExtension("pdf");
                    p.setNomArchivoVol(maskPDF.generaPDFFactura(p.getUnidadDisco() + p.getRutaBase() + p.getRutaDirectorio() + p.getNomArchivoVol()));
                    p.setUnidadDisco("");
                    p.setRutaBase("");
                    p.setRutaDirectorio("");
                    lp.add(p);
                    if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs1.getString("nom_archivo_org") != null)) {
                        prvez = false;
                        // d.setExtension(extensionArchivo);
                    }
                    d.setNumeroPaginas(lp.size());
                    Pagina[] pages = new Pagina[lp.size()];
                    for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
                    d.setPaginasDocumento(pages);
                    l.add(d);
                }
                rs1.close();
                rs1 = null;
            }
            return l;
        } finally {
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(pstmnt1);
            CloseObject.closeObject(rs0);
            CloseObject.closeObject(pstmnt0);
        }
    }

    public static int marcaDocumentoVersion(Connection conn, Documento d) throws SQLException {
        StringBuilder querUpdate = new StringBuilder();
        querUpdate.append("UPDATE imx_documento SET iEsVersion = 1 WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(querUpdate.toString());
            ps.setString(1, d.getTituloAplicacion());
            ps.setInt(2, d.getIdGabinete());
            ps.setInt(3, d.getIdCarpetaPadre());
            ps.setInt(4, d.getIdDocumento());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    /**
     * Respalda la pagina previo a su eliminacion/modificacion.
     *
     * @param conn
     *            Conexion a base de datos
     * @param d
     *            Documento con pagina a respaldar.
     * @throws SQLException
     */
    public static void respaldaPagina(Connection conn, Documento d) throws SQLException {
        log.debug("Object: {}", "Respaldando paginas de documento: " + d);
        StringBuilder queryInsertaLogPagina = new StringBuilder();
        queryInsertaLogPagina.append("INSERT INTO dbo.imx_pagina_borrada ");
        queryInsertaLogPagina.append("            (titulo_aplicacion, ");
        queryInsertaLogPagina.append("             id_carpeta_padre, ");
        queryInsertaLogPagina.append("             id_documento, ");
        queryInsertaLogPagina.append("             numero_pagina, ");
        queryInsertaLogPagina.append("             volumen, ");
        queryInsertaLogPagina.append("             tipo_volumen, ");
        queryInsertaLogPagina.append("             nom_archivo_vol, ");
        queryInsertaLogPagina.append("             nom_archivo_org, ");
        queryInsertaLogPagina.append("             tipo_pagina, ");
        queryInsertaLogPagina.append("             anotaciones, ");
        queryInsertaLogPagina.append("             estado_pagina, ");
        queryInsertaLogPagina.append("             tamano_bytes, ");
        queryInsertaLogPagina.append("             id_gabinete, ");
        queryInsertaLogPagina.append("             fecha_creacion, ");
        queryInsertaLogPagina.append("             hora_creacion, ");
        queryInsertaLogPagina.append("             rowid, ");
        queryInsertaLogPagina.append("             fecha_borrado) ");
        queryInsertaLogPagina.append("SELECT titulo_aplicacion, ");
        queryInsertaLogPagina.append("       id_carpeta_padre, ");
        queryInsertaLogPagina.append("       id_documento, ");
        queryInsertaLogPagina.append("       numero_pagina, ");
        queryInsertaLogPagina.append("       volumen, ");
        queryInsertaLogPagina.append("       tipo_volumen, ");
        queryInsertaLogPagina.append("       nom_archivo_vol, ");
        queryInsertaLogPagina.append("       nom_archivo_org, ");
        queryInsertaLogPagina.append("       tipo_pagina, ");
        queryInsertaLogPagina.append("       anotaciones, ");
        queryInsertaLogPagina.append("       estado_pagina, ");
        queryInsertaLogPagina.append("       tamano_bytes, ");
        queryInsertaLogPagina.append("       id_gabinete, ");
        queryInsertaLogPagina.append("       fecha_creacion, ");
        queryInsertaLogPagina.append("       hora_creacion, ");
        queryInsertaLogPagina.append("       rowid, ");
        queryInsertaLogPagina.append("       Getdate() ");
        queryInsertaLogPagina.append("FROM   imx_pagina ");
        queryInsertaLogPagina.append("WHERE  titulo_aplicacion = ? ");
        queryInsertaLogPagina.append("       AND id_gabinete = ? ");
        queryInsertaLogPagina.append("       AND id_carpeta_padre = ? ");
        queryInsertaLogPagina.append("       AND id_documento = ? ");
        PreparedStatement psInsert = null;
        try {
            log.trace("Object: {}", "Se ejecutara: " + queryInsertaLogPagina.toString());
            psInsert = conn.prepareStatement(queryInsertaLogPagina.toString());
            psInsert.setString(1, d.getTituloAplicacion());
            psInsert.setInt(2, d.getIdGabinete());
            psInsert.setInt(3, d.getIdCarpetaPadre());
            psInsert.setInt(4, d.getIdDocumento());
            int insertados = psInsert.executeUpdate();
            log.info("Object: {}", "Se respaldaron: " + insertados + " paginas de respaldo.");
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static Documento selectDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento) throws SQLException {
        Documento d = null;
        PreparedStatement pstmnt0 = null, pstmnt1 = null;
        ResultSet rs0 = null, rs1 = null;
        try {
            pstmnt0 = conn.prepareStatement("SELECT d.nombre_documento, d.nombre_usuario, d.prioridad, " + "d.id_tipo_docto, d.fh_creacion, d.fh_modificacion, d.numero_accesos, d.numero_paginas, " + "d.titulo, d.autor, d.materia, d.descripcion, d.clase_documento, d.estado_documento, " + "d.tamano_bytes, t.nombre_tipo_docto, d.compartir, d.token_compartir, fh_vigencia, iEsVersion " + "FROM imx_documento d, imx_tipo_documento t WHERE t.titulo_aplicacion = d.titulo_aplicacion " + "AND t.id_tipo_docto = d.id_tipo_docto AND d.titulo_aplicacion = ? AND d.id_gabinete = ? " + "AND d.id_carpeta_padre = ? AND d.id_documento = ?");
            pstmnt0.setString(1, titulo_aplicacion);
            pstmnt0.setInt(2, id_gabinete);
            pstmnt0.setInt(3, id_carpeta_padre);
            pstmnt0.setInt(4, id_documento);
            rs0 = pstmnt0.executeQuery();
            if (rs0.next()) {
                d = new Documento();
                d.setTituloAplicacion(titulo_aplicacion);
                d.setIdGabinete(id_gabinete);
                d.setIdCarpetaPadre(id_carpeta_padre);
                d.setIdDocumento(id_documento);
                d.setNombreDocumento(rs0.getString("nombre_documento"));
                d.setNombreUsuario(rs0.getString("nombre_usuario"));
                d.setPrioridad(rs0.getInt("prioridad"));
                d.setIdTipoDocto(rs0.getInt("id_tipo_docto"));
                d.setNombreTipoDocto(rs0.getString("nombre_tipo_docto"));
                d.setFechaCreacion(new Date(rs0.getTimestamp("fh_creacion").getTime()));
                d.setFechaModificacion(new Date(rs0.getTimestamp("fh_modificacion").getTime()));
                d.setNumeroAccesos(rs0.getInt("numero_accesos"));
                d.setNumeroPaginas(rs0.getInt("numero_paginas"));
                d.setTitulo(rs0.getString("titulo"));
                d.setAutor(rs0.getString("autor"));
                d.setMateria(rs0.getString("materia"));
                d.setDescripcion(rs0.getString("descripcion"));
                d.setClaseDocumento(rs0.getInt("clase_documento"));
                d.setEstadoDocumento(rs0.getString("estado_documento"));
                d.setTamanoBytes(rs0.getDouble("tamano_bytes"));
                d.setCompartir(rs0.getString("compartir"));
                d.setTokenCompartir(rs0.getString("token_compartir"));
                d.setFh_vigencia(rs0.getTimestamp("fh_vigencia"));
                d.setEsVersion(rs0.getInt("iEsVersion"));
                pstmnt1 = conn.prepareStatement("SELECT p.numero_pagina, p.tipo_pagina, p.volumen, p.tipo_volumen, " + "v.unidad_disco, v.ruta_base, v.ruta_directorio, p.nom_archivo_vol, p.nom_archivo_org, " + "p.estado_pagina, p.tamano_bytes FROM imx_pagina p, imx_volumen v WITH (NOLOCK)" + "WHERE v.volumen = p.volumen AND p.titulo_aplicacion = ? AND p.id_gabinete = ? " + "AND p.id_carpeta_padre = ? AND p.id_documento = ? ORDER BY p.numero_pagina");
                pstmnt1.setString(1, titulo_aplicacion);
                pstmnt1.setInt(2, id_gabinete);
                pstmnt1.setInt(3, id_carpeta_padre);
                pstmnt1.setInt(4, id_documento);
                rs1 = pstmnt1.executeQuery();
                boolean prvez = true;
                List<Pagina> lp = new ArrayList<Pagina>();
                while (rs1.next()) {
                    Pagina p = new Pagina();
                    p.setTituloAplicacion(titulo_aplicacion);
                    p.setIdGabinete(id_gabinete);
                    p.setIdCarpetaPadre(id_carpeta_padre);
                    p.setIdDocumento(id_documento);
                    p.setNumeroPagina(rs1.getInt("numero_pagina"));
                    p.setVolumen(rs1.getString("volumen"));
                    p.setTipoVolumen(rs1.getString("tipo_volumen"));
                    p.setNomArchivoVol(rs1.getString("nom_archivo_vol").trim());
                    p.setNomArchivoOrg(rs1.getString("nom_archivo_org").trim());
                    p.setTipoPagina(rs1.getString("tipo_pagina"));
                    p.setEstadoPagina(rs1.getString("estado_pagina"));
                    p.setTamanoBytes(rs1.getDouble("tamano_bytes"));
                    p.setUnidadDisco(rs1.getString("unidad_disco").trim());
                    p.setRutaBase(rs1.getString("ruta_base").trim());
                    p.setRutaDirectorio(rs1.getString("ruta_directorio").trim());
                    lp.add(p);
                    if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs1.getString("nom_archivo_org") != null)) {
                        prvez = false;
                        int pos = rs1.getString("nom_archivo_org").lastIndexOf(".");
                        d.setExtension((pos > -1) ? rs1.getString("nom_archivo_org").substring(pos + 1) : "");
                    }
                }
                d.setNumeroPaginas(lp.size());
                Pagina[] pages = new Pagina[lp.size()];
                for (int i = 0; i < lp.size(); i++) pages[i] = (Pagina) lp.get(i);
                d.setPaginasDocumento(pages);
            }
        } finally {
            if (rs1 != null)
                rs1.close();
            if (pstmnt1 != null)
                pstmnt1.close();
            if (rs0 != null)
                rs0.close();
            if (pstmnt0 != null)
                pstmnt0.close();
            rs1 = null;
            pstmnt1 = null;
            rs0 = null;
            pstmnt0 = null;
        }
        return d;
    }

    public static boolean upateDocumento(Connection conn, String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento, String nombre_tipo_docto) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean retVal = false;
        try {
            pstmnt = conn.prepareStatement("SELECT id_tipo_docto FROM imx_tipo_documento WITH (NOLOCK) WHERE titulo_aplicacion = ? AND nombre_tipo_docto = ?");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setString(2, nombre_tipo_docto);
            rs = pstmnt.executeQuery();
            if (!rs.next())
                throw new SQLException("Tipo de documento \"" + nombre_tipo_docto + "\" no localizado");
            int id_tipo_docto = rs.getInt(1);
            pstmnt = conn.prepareStatement("UPDATE imx_documento SET id_tipo_docto = ? WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
            pstmnt.setInt(1, id_tipo_docto);
            pstmnt.setString(2, titulo_aplicacion);
            pstmnt.setInt(3, id_gabinete);
            pstmnt.setInt(4, id_carpeta_padre);
            pstmnt.setInt(5, id_documento);
            pstmnt.executeUpdate();
            retVal = true;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }

    public static boolean updateDocumento(Connection conn, Documento d) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean retVal = false;
        try {
            pstmnt = conn.prepareStatement("UPDATE imx_documento SET nombre_documento = ?, " + "nombre_usuario = ?, prioridad = ?, id_tipo_docto = ?, fh_modificacion = ?, " + "numero_accesos = ?, numero_paginas = ?, titulo = ?, autor = ?, " + "materia = ?, descripcion = ?, clase_documento = ?, estado_documento = ?, " + "tamano_bytes = ?, compartir = ?, token_compartir = ? WHERE titulo_aplicacion = ? " + "AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
            pstmnt.setString(1, d.getNombreDocumento());
            pstmnt.setString(2, d.getNombreUsuario());
            pstmnt.setInt(3, d.getPrioridad());
            pstmnt.setInt(4, d.getIdTipoDocto());
            pstmnt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmnt.setInt(6, 0);
            pstmnt.setInt(7, d.getNumeroPaginas());
            pstmnt.setString(8, d.getTitulo());
            pstmnt.setString(9, d.getAutor());
            pstmnt.setString(10, d.getMateria());
            pstmnt.setString(11, d.getDescripcion());
            pstmnt.setInt(12, d.getClaseDocumento());
            pstmnt.setString(13, String.valueOf(d.getEstadoDocumento()));
            pstmnt.setDouble(14, d.getTamanoBytes());
            pstmnt.setString(15, d.getCompartir());
            pstmnt.setString(16, d.getTokenCompartir());
            pstmnt.setString(17, d.getTituloAplicacion());
            pstmnt.setInt(18, d.getIdGabinete());
            pstmnt.setInt(19, d.getIdCarpetaPadre());
            pstmnt.setInt(20, d.getIdDocumento());
            pstmnt.executeUpdate();
            retVal = true;
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retVal;
    }

    public static int updateMaxFilename(Connection conn, String prefijo, int consecutivo) throws SQLException {
        int affectedRecords = 0;
        PreparedStatement pstmnt = null, pstmnt1 = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE IMX_MAXFILENAME set consecutivo=? WHERE prefijo = ?");
            pstmnt.setInt(1, consecutivo);
            pstmnt.setString(2, prefijo);
            affectedRecords = pstmnt.executeUpdate();
            if (affectedRecords == 0) {
                pstmnt1 = conn.prepareStatement("INSERT INTO IMX_MAXFILENAME values (?, ?)");
                pstmnt1.setString(1, prefijo);
                pstmnt1.setInt(2, consecutivo);
                affectedRecords = pstmnt.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(pstmnt1);
        }
        return affectedRecords;
    }

    public DocumentoManager() {
        // Se agrega este constructor para que jale la
        // connection en el metodo que sigue
        super.init("jdbc/gestion");
    }

    public synchronized Documento buscaDocumento(String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento) throws SQLException {
        Connection conn = this.getConnection();
        Documento d = selectDocumento(conn, titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento);
        try {
            if (conn != null)
                conn.close();
            conn = null;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.error("Cerrando conexion a base de datos" + exc, exc);
            }
            conn = null;
        }
        return d;
    }

    public static Documento insertPaginaDocumento(Connection conn, Documento d, File evidencia) throws FortimaxException, SQLException, IOException {
        Volumen vol = VolumenManager.getVolumen(conn);
        if (DocumentoManager.existeDocumentoCapturado(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getNombreDocumento()))
            throw new FortimaxException("El documento no esta vacio. No se puede crear pagina");
        String extension = Util.getFileExtencion(evidencia.getName());
        d.setExtension(extension);
        DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", evidencia.length());
        d = DocumentoManager.selectDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
        Util.copiaArchivo(evidencia.getAbsolutePath(), d.getPaginaDocumento(0).getFullPathFileName());
        return d;
    }
}
