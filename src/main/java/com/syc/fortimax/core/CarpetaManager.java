package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetaManager {

    private static final Logger log = LoggerFactory.getLogger(CarpetaManager.class);

    public static Carpeta createFolder(Connection conn, Fortimax fortimax, String folderName, String userName) throws SQLException, FortimaxException {
        String processName = fortimax.getTituloAplicacion();
        Carpeta folder = new Carpeta();
        folder.setTituloAplicacion(processName);
        folder.setIdGabinete(fortimax.getIdGabinete());
        folder.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, processName, fortimax.getIdGabinete()));
        folder.setNombreCarpeta(folderName);
        folder.setNombreUsuario(userName);
        folder.setBanderaRaiz("N");
        folder.setDescripcion(folderName);
        folder.setPassword("-1");
        folder = CarpetaManager.insertaCarpeta(conn, folder);
        OrgCarpeta oc = new OrgCarpeta();
        oc.setIdCarpetaHija(folder.getIdCarpeta());
        oc.setIdCarpetaPadre(0);
        oc.setIdGabinete(fortimax.getIdGabinete());
        oc.setNombreHija(folderName);
        oc.setTituloAplicacion(processName);
        OrgCarpetaManager.insert(conn, oc);
        log.trace("Object: {}", "Folder [" + folderName + "] created successfully");
        return folder;
    }

    public static Carpeta getCarpeta(Connection conn, String gavetaAsociada, int idGabinete, int idCarpeta) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT titulo_aplicacion, ");
        query.append("       id_gabinete, ");
        query.append("       id_carpeta, ");
        query.append("       nombre_carpeta, ");
        query.append("       nombre_usuario, ");
        query.append("       bandera_raiz, ");
        query.append("       fh_creacion, ");
        query.append("       fh_modificacion, ");
        query.append("       numero_accesos, ");
        query.append("       numero_carpetas, ");
        query.append("       numero_documentos, ");
        query.append("       descripcion, ");
        query.append("       password ");
        query.append("FROM   imx_carpeta WITH (NOLOCK) ");
        query.append("WHERE  bandera_raiz <> 'S' ");
        query.append("       AND id_carpeta = ? ");
        query.append("       AND id_gabinete = ? ");
        query.append("       AND titulo_aplicacion = ?");
        ResultSet rs = null;
        PreparedStatement ps = null;
        Carpeta c = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idCarpeta);
            ps.setInt(2, idGabinete);
            ps.setString(3, gavetaAsociada);
            rs = ps.executeQuery();
            if (rs.next()) {
                c = new Carpeta();
                c.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                c.setIdGabinete(rs.getInt("id_gabinete"));
                c.setIdCarpeta(rs.getInt("id_carpeta"));
                c.setNombreCarpeta(rs.getString("nombre_carpeta"));
                c.setNombreUsuario(rs.getString("nombre_usuario"));
                c.setBanderaRaiz(rs.getString("bandera_raiz"));
                c.setFechaCreacion(rs.getTimestamp("fh_creacion"));
                c.setFechaModificacion(rs.getTimestamp("fh_modificacion"));
                c.setNumeroAccesos(rs.getInt("numero_accesos"));
                c.setNumeroCarpetas(rs.getInt("numero_carpetas"));
                c.setNumeroDocumentos(rs.getInt("numero_documentos"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setPassword(rs.getString("password"));
            }
            return c;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static Carpeta getCarpetaByName(Connection conn, String gavetaAsociada, int idGabinete, String nombreCarpeta) throws FortimaxException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT titulo_aplicacion, ");
        query.append("       id_gabinete, ");
        query.append("       id_carpeta, ");
        query.append("       nombre_carpeta, ");
        query.append("       nombre_usuario, ");
        query.append("       bandera_raiz, ");
        query.append("       fh_creacion, ");
        query.append("       fh_modificacion, ");
        query.append("       numero_accesos, ");
        query.append("       numero_carpetas, ");
        query.append("       numero_documentos, ");
        query.append("       descripcion, ");
        query.append("       password ");
        query.append("FROM   imx_carpeta WITH (NOLOCK) ");
        query.append("WHERE  bandera_raiz <> 'S' ");
        query.append("       AND nombre_carpeta = ? ");
        query.append("       AND id_gabinete = ? ");
        query.append("       AND titulo_aplicacion = ?");
        ResultSet rs = null;
        PreparedStatement ps = null;
        Carpeta c = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, nombreCarpeta);
            ps.setInt(2, idGabinete);
            ps.setString(3, gavetaAsociada);
            rs = ps.executeQuery();
            if (rs.next()) {
                c = new Carpeta();
                c.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                c.setIdGabinete(rs.getInt("id_gabinete"));
                c.setIdCarpeta(rs.getInt("id_carpeta"));
                c.setNombreCarpeta(rs.getString("nombre_carpeta"));
                c.setNombreUsuario(rs.getString("nombre_usuario"));
                c.setBanderaRaiz(rs.getString("bandera_raiz"));
                c.setFechaCreacion(rs.getTimestamp("fh_creacion"));
                c.setFechaModificacion(rs.getTimestamp("fh_modificacion"));
                c.setNumeroAccesos(rs.getInt("numero_accesos"));
                c.setNumeroCarpetas(rs.getInt("numero_carpetas"));
                c.setNumeroDocumentos(rs.getInt("numero_documentos"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setPassword(rs.getString("password"));
            }
            return c;
        } catch (SQLException e) {
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static Carpeta getCarpetaRaiz(Connection conn, String gavetaAsociada, int idGabinete) throws Exception {
        String query = "SELECT titulo_aplicacion, " + "       id_gabinete, " + "       id_carpeta, " + "       nombre_carpeta, " + "       nombre_usuario, " + "       bandera_raiz, " + "       fh_creacion, " + "       fh_modificacion, " + "       numero_accesos, " + "       numero_carpetas, " + "       numero_documentos, " + "       descripcion, " + "       password " + "FROM   imx_carpeta WITH (NOLOCK) " + "WHERE  bandera_raiz = 'S' " + "       AND id_gabinete = ? " + "       AND titulo_aplicacion = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        Carpeta c = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idGabinete);
            ps.setString(2, gavetaAsociada);
            rs = ps.executeQuery();
            if (rs.next()) {
                c = new Carpeta();
                c.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                c.setIdGabinete(rs.getInt("id_gabinete"));
                c.setIdCarpeta(rs.getInt("id_carpeta"));
                c.setNombreCarpeta(rs.getString("nombre_carpeta"));
                c.setNombreUsuario(rs.getString("nombre_usuario"));
                c.setBanderaRaiz(rs.getString("bandera_raiz"));
                c.setFechaCreacion(rs.getTimestamp("fh_creacion"));
                c.setFechaModificacion(rs.getTimestamp("fh_modificacion"));
                c.setNumeroAccesos(rs.getInt("numero_accesos"));
                c.setNumeroCarpetas(rs.getInt("numero_carpetas"));
                c.setNumeroDocumentos(rs.getInt("numero_documentos"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setPassword(rs.getString("password"));
            }
            return c;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int getIdCarpetaPadre(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT id_carpeta FROM imx_carpeta WITH (NOLOCK) " + "WHERE titulo_aplicacion = ? AND id_gabinete = ? AND bandera_raiz = ?");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            pstmnt.setString(3, "S");
            rs = pstmnt.executeQuery();
            if (rs.next())
                retval = rs.getInt(1);
            else
                throw new SQLException("No se localizo la carpeta raiz. titulo_aplicacion(" + titulo_aplicacion + "), id_gabinete(" + id_gabinete + ")");
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

    public static synchronized int getNextIdCarpeta(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_carpeta) FROM imx_carpeta WITH (NOLOCK) WHERE titulo_aplicacion = ? " + "AND id_gabinete = ?");
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            rs = pstmnt.executeQuery();
            if (rs.next())
                retval = rs.getInt(1) + 1;
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static synchronized int getTotalDocumentosCarpeta(Connection conn, String tituloAplicacion, int idGabinete, String nombreCarpeta) throws Exception {
        int totalDocumentos = 0;
        String query = "SELECT	carpeta.TITULO_APLICACION, " + "			carpeta.ID_GABINETE,  " + "			carpeta.ID_CARPETA,  " + "			carpeta.NOMBRE_CARPETA,  " + "			documento.ID_DOCUMENTO, " + "			COUNT(pagina.NUMERO_PAGINA) AS paginas " + "  FROM	IMX_CARPETA carpeta WITH(NOLOCK) " + "        LEFT OUTER JOIN dbo.IMX_DOCUMENTO documento WITH(NOLOCK) " + "			 ON	carpeta.TITULO_APLICACION = documento.TITULO_APLICACION " + "			AND	carpeta.ID_GABINETE = documento.ID_GABINETE  " + "			AND	carpeta.ID_CARPETA = documento.ID_CARPETA_PADRE " + "		LEFT OUTER JOIN dbo.IMX_PAGINA pagina WITH(NOLOCK) " + "			 ON documento.TITULO_APLICACION = pagina.TITULO_APLICACION " + "			AND	documento.ID_GABINETE = pagina.ID_GABINETE  " + "			AND	documento.ID_CARPETA_PADRE = pagina.ID_CARPETA_PADRE " + "			AND documento.ID_DOCUMENTO = pagina.ID_DOCUMENTO " + " WHERE	carpeta.TITULO_APLICACION = ? " + "   AND	carpeta.ID_GABINETE = ? " + "   AND	iEsVersion = 0 " + ("Oficios".equalsIgnoreCase(nombreCarpeta) ? "   AND	carpeta.NOMBRE_CARPETA LIKE ? + '%'" : "   AND	carpeta.NOMBRE_CARPETA = ? ") + "GROUP BY	carpeta.TITULO_APLICACION,  " + "			carpeta.ID_GABINETE,  " + "			carpeta.ID_CARPETA,  " + "			carpeta.NOMBRE_CARPETA, " + "			documento.ID_DOCUMENTO " + "HAVING COUNT(pagina.NUMERO_PAGINA) > 0 " + "ORDER BY carpeta.ID_CARPETA";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, tituloAplicacion);
            ps.setInt(2, idGabinete);
            ps.setString(3, nombreCarpeta);
            rs = ps.executeQuery();
            while (rs.next()) totalDocumentos++;
            return totalDocumentos;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int insert(Connection conn, Carpeta c) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO imx_carpeta " + "(titulo_aplicacion, id_gabinete, id_carpeta, nombre_carpeta, nombre_usuario, " + "bandera_raiz, fh_creacion, fh_modificacion, numero_accesos, numero_carpetas, " + "numero_documentos, descripcion, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt.setString(1, c.getTituloAplicacion());
            pstmnt.setInt(2, c.getIdGabinete());
            pstmnt.setInt(3, c.getIdCarpeta());
            pstmnt.setString(4, c.getNombreCarpeta());
            pstmnt.setString(5, c.getNombreUsuario());
            pstmnt.setString(6, c.getBanderaRaiz());
            pstmnt.setTimestamp(7, c.getFechaCreacion());
            pstmnt.setTimestamp(8, c.getFechaModificacion());
            pstmnt.setInt(9, c.getNumeroAccesos());
            pstmnt.setInt(10, c.getNumeroCarpetas());
            pstmnt.setInt(11, c.getNumeroDocumentos());
            pstmnt.setString(12, c.getDescripcion());
            pstmnt.setString(13, c.getPassword());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Carpeta insertaCarpeta(Connection conn, Carpeta c) throws SQLException, FortimaxException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO imx_carpeta ");
        query.append("            (titulo_aplicacion, ");
        query.append("             id_gabinete, ");
        query.append("             id_carpeta, ");
        query.append("             nombre_carpeta, ");
        query.append("             nombre_usuario, ");
        query.append("             bandera_raiz, ");
        query.append("             fh_creacion, ");
        query.append("             fh_modificacion, ");
        query.append("             numero_accesos, ");
        query.append("             numero_carpetas, ");
        query.append("             numero_documentos, ");
        query.append("             descripcion, ");
        query.append("             password) ");
        query.append("VALUES     ( ?, ");
        query.append("             ?, ");
        query.append("             ?, ");
        query.append("             ?, ");
        query.append("             ?, ");
        query.append("             ?, ");
        query.append("             Getdate(), ");
        query.append("             Getdate(), ");
        query.append("             0, ");
        query.append("             0, ");
        query.append("             0, ");
        query.append("             ?, ");
        query.append("             ? ) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, c.getTituloAplicacion());
            ps.setInt(2, c.getIdGabinete());
            ps.setInt(3, c.getIdCarpeta());
            ps.setString(4, c.getNombreCarpeta());
            ps.setString(5, c.getNombreUsuario());
            ps.setString(6, c.getBanderaRaiz());
            ps.setString(7, c.getDescripcion());
            ps.setString(8, c.getPassword());
            ps.executeUpdate();
            return getCarpetaByName(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getNombreCarpeta());
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static Carpeta getCarpeta(Connection conn, Fortimax fortimax) throws SQLException {
        return getCarpeta(conn, fortimax.getTituloAplicacion(), fortimax.getIdGabinete(), fortimax.getIdCarp());
    }
}
