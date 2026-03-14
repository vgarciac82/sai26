package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class DocumentoVersionManager {

    public static synchronized int siguienteVersionDocumento(Connection conn, String tituloAplicacion, int idGabinete, int idCarpetaPadre, String nombreDocumento) throws SQLException {
        StringBuilder querySelVer = new StringBuilder();
        querySelVer.append("SELECT Max(version_documento) ");
        querySelVer.append("FROM   imx_version_documento WITH(nolock) ");
        querySelVer.append("WHERE  titulo_aplicacion = ?  ");
        querySelVer.append("       AND id_gabinete = ?  ");
        querySelVer.append("       AND id_carpeta_padre = ?  ");
        querySelVer.append("       AND nombre_documento = ? 	   ");
        PreparedStatement psBuscaVersion = null;
        ResultSet rsVersion = null;
        try {
            psBuscaVersion = conn.prepareStatement(querySelVer.toString());
            psBuscaVersion.setString(1, tituloAplicacion);
            psBuscaVersion.setInt(2, idGabinete);
            psBuscaVersion.setInt(3, idCarpetaPadre);
            psBuscaVersion.setString(4, nombreDocumento);
            rsVersion = psBuscaVersion.executeQuery();
            int version = 1;
            if (rsVersion.next() && (rsVersion.getInt(1) > 0)) {
                version = rsVersion.getInt(1) + 1;
                actualizaVersion(conn, tituloAplicacion, idGabinete, idCarpetaPadre, nombreDocumento, version);
            } else {
                insertaVersion(conn, tituloAplicacion, idGabinete, idCarpetaPadre, nombreDocumento, version);
            }
            return version;
        } finally {
            CloseObject.closeObject(rsVersion);
            CloseObject.closeObject(psBuscaVersion);
        }
    }

    private static int insertaVersion(Connection conn, String tituloAplicacion, int idGabinete, int idCarpetaPadre, String nombreDocumento, int version) throws SQLException {
        StringBuilder queryInsert = new StringBuilder("INSERT INTO imx_version_documento(titulo_aplicacion, id_gabinete, id_carpeta_padre, nombre_documento, version_documento) ");
        queryInsert.append("VALUES(?,?,?,?,?)");
        PreparedStatement psInsert = null;
        try {
            psInsert = conn.prepareStatement(queryInsert.toString());
            psInsert.setString(1, tituloAplicacion);
            psInsert.setInt(2, idGabinete);
            psInsert.setInt(3, idCarpetaPadre);
            psInsert.setString(4, nombreDocumento);
            psInsert.setInt(5, version);
            return psInsert.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    private static int actualizaVersion(Connection conn, String tituloAplicacion, int idGabinete, int idCarpetaPadre, String nombreDocumento, int versionDocto) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder("UPDATE imx_version_documento ");
        queryUpdate.append("SET    version_documento = ? ");
        queryUpdate.append("WHERE  titulo_aplicacion = ?");
        queryUpdate.append("       AND id_gabinete = ?  ");
        queryUpdate.append("       AND id_carpeta_padre = ?");
        queryUpdate.append("       AND nombre_documento = ? ");
        PreparedStatement psUpdate = null;
        try {
            psUpdate = conn.prepareStatement(queryUpdate.toString());
            psUpdate.setInt(1, versionDocto);
            psUpdate.setString(2, tituloAplicacion);
            psUpdate.setInt(3, idGabinete);
            psUpdate.setInt(4, idCarpetaPadre);
            psUpdate.setString(5, nombreDocumento);
            return psUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }
}
