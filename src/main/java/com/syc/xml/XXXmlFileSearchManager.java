package com.syc.xml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class XXXmlFileSearchManager {

    public XXXmlFileSearchManager() {
    }

    public static String select(Connection conn, String titulo_aplicacion, int id_caso) throws SQLException {
        PreparedStatement pstmnt = null;
        PreparedStatement pstmnt1 = null;
        PreparedStatement pstmnt2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String PathFile = null;
        StringBuffer Query = new StringBuffer();
        StringBuffer Query1 = new StringBuffer();
        StringBuffer Query2 = new StringBuffer();
        Query2.append("SELECT DISTINCT c_id_gabinete FROM cg_caso WHERE id_caso = ?");
        pstmnt2 = conn.prepareStatement(Query2.toString());
        pstmnt2.setInt(1, id_caso);
        rs2 = pstmnt2.executeQuery();
        int id_gabinete;
        if (rs2.next()) {
            id_gabinete = rs2.getInt("c_id_gabinete");
            Query.append("SELECT * " + "FROM imx_pagina a " + "WHERE a.titulo_aplicacion = ? " + "AND a.id_gabinete = ? " + "AND a.id_carpeta_padre = 0");
            pstmnt = conn.prepareStatement(Query.toString());
            pstmnt.setString(1, titulo_aplicacion);
            pstmnt.setInt(2, id_gabinete);
            rs = pstmnt.executeQuery();
            String Volumen = null;
            String NombreArchivoVol = null;
            if (rs.next()) {
                Volumen = rs.getString("volumen");
                NombreArchivoVol = rs.getString("nom_archivo_vol");
                Query1.append("SELECT volumen, unidad_disco, ruta_base, ruta_directorio FROM imx_volumen WITH (NOLOCK) WHERE volumen = ? ");
                pstmnt1 = conn.prepareStatement(Query1.toString());
                pstmnt1.setString(1, Volumen);
                rs1 = pstmnt1.executeQuery();
                if (rs1.next()) {
                    PathFile = (new StringBuilder(String.valueOf(rs1.getString("unidad_disco")))).append(rs1.getString("ruta_base")).append(rs1.getString("ruta_directorio")).append(NombreArchivoVol).toString();
                }
            }
        }
        if (rs2 != null) {
            rs2.close();
        }
        if (rs != null) {
            rs.close();
        }
        if (rs1 != null) {
            rs1.close();
        }
        if (pstmnt2 != null) {
            pstmnt2.close();
        }
        if (pstmnt != null) {
            pstmnt.close();
        }
        if (pstmnt1 != null) {
            pstmnt1.close();
        }
        rs = null;
        rs1 = null;
        rs2 = null;
        pstmnt = null;
        pstmnt1 = null;
        pstmnt2 = null;
        System.out.println(PathFile);
        return PathFile;
    }

    public static String[][] arrSelect(Connection conn, String[] IdGabinetes) throws SQLException {
        StringBuffer Query1 = new StringBuffer();
        PreparedStatement pstmnt1 = null;
        ResultSet rs1 = null;
        String[][] PathFile;
        String valueGabinete = null;
        int tam = IdGabinetes.length;
        PathFile = new String[tam][1];
        for (int a = 0; a < IdGabinetes.length; a++) {
            valueGabinete = IdGabinetes[a];
            Query1.append("SELECT * " + "FROM imx_pagina a " + "WHERE a.id_gabinete = ? " + "AND a.id_carpeta_padre = 0");
            pstmnt1 = conn.prepareStatement(Query1.toString());
            pstmnt1.setString(1, valueGabinete);
            rs1 = pstmnt1.executeQuery();
            String Volumen = null;
            String NombreArchivoVol = null;
            if (rs1.next()) {
                Volumen = rs1.getString("volumen");
                NombreArchivoVol = rs1.getString("nom_archivo_vol");
                Query1.append("SELECT volumen, unidad_disco, ruta_base, ruta_directorio FROM imx_volumen WITH (NOLOCK) WHERE volumen = ? ");
                pstmnt1 = conn.prepareStatement(Query1.toString());
                pstmnt1.setString(1, Volumen);
                rs1 = pstmnt1.executeQuery();
                if (rs1.next()) {
                    PathFile[a][0] = valueGabinete;
                    PathFile[a][1] = (new StringBuilder(String.valueOf(rs1.getString("unidad_disco")))).append(rs1.getString("ruta_base")).append(rs1.getString("ruta_directorio")).append(NombreArchivoVol).toString();
                }
            }
        }
        if (rs1 != null) {
            rs1.close();
        }
        if (pstmnt1 != null) {
            pstmnt1.close();
        }
        rs1 = null;
        pstmnt1 = null;
        return PathFile;
    }

    public static String[][] recuperaCasosSinFLimite(Connection conn) throws SQLException {
        final String ID_DPC_F_LIMITE = "13";
        final String TITULO_APLICACION = "EXPEDIENTES";
        final String TIPO_DOCTO_EXT = "1";
        String[][] arrIdCasos;
        int tam = 0;
        PreparedStatement pstmnt1 = null;
        PreparedStatement pstmnt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        StringBuffer Query1 = new StringBuffer();
        StringBuffer Query2 = new StringBuffer();
        Query1.append("SELECT  COUNT(*) FROM " + "(" + " SELECT  DISTINCT vr.id_caso AS ID_CASO" + ",		 v.unidad_disco + v.ruta_base + v.ruta_directorio + p.nom_archivo_vol AS ARCHIVO" + " FROM    vimx_reportes vr" + " INNER JOIN cg_caso_dato  cd" + " ON      (cd.id_caso = vr.id_caso)" + " INNER JOIN imx_pagina p" + " ON      (p.id_gabinete = vr.id_gabinete)" + " INNER JOIN imx_volumen v WITH (NOLOCK)" + " ON      (v.volumen = p.volumen)" + " WHERE   p.id_carpeta_padre  = 0" + " AND     p.titulo_aplicacion = '" + TITULO_APLICACION + "'" + " AND     p.id_documento      = " + TIPO_DOCTO_EXT + " AND     cd.id_cd            = " + ID_DPC_F_LIMITE + " AND     (cd.cd_valor IS NULL" + " OR      cd.cd_valor = '')" + ") AS cuantos");
        pstmnt1 = conn.prepareStatement(Query1.toString());
        rs1 = pstmnt1.executeQuery();
        if (rs1.next()) {
            tam = rs1.getInt(1);
            System.out.println("Longitud del Arreglo: " + tam);
        }
        Query2.append(" SELECT  DISTINCT vr.id_caso AS ID_CASO" + ",		 v.unidad_disco + v.ruta_base + v.ruta_directorio + p.nom_archivo_vol AS ARCHIVO" + " FROM    vimx_reportes vr" + " INNER JOIN cg_caso_dato  cd" + " ON      (cd.id_caso = vr.id_caso)" + " INNER JOIN imx_pagina p" + " ON      (p.id_gabinete = vr.id_gabinete)" + " INNER JOIN imx_volumen v WITH (NOLOCK)" + " ON      (v.volumen = p.volumen)" + " WHERE   p.id_carpeta_padre  = 0" + " AND     p.titulo_aplicacion = '" + TITULO_APLICACION + "'" + " AND     p.id_documento      = " + TIPO_DOCTO_EXT + " AND     cd.id_cd            = " + ID_DPC_F_LIMITE + " AND     (cd.cd_valor IS NULL" + " OR      cd.cd_valor = '')");
        pstmnt2 = conn.prepareStatement(Query2.toString());
        rs2 = pstmnt2.executeQuery();
        arrIdCasos = new String[tam][2];
        int j = 0;
        while (rs2.next()) {
            arrIdCasos[j][0] = rs2.getString(1);
            arrIdCasos[j][1] = rs2.getString(2);
            System.out.println("La ubicacion [0] contiene : " + arrIdCasos[j][0] + " y la Ubicacion [1] contiene : " + arrIdCasos[j][1]);
            j++;
        }
        if (rs2 != null) {
            rs2.close();
        }
        if (rs1 != null) {
            rs1.close();
        }
        if (pstmnt2 != null) {
            pstmnt2.close();
        }
        if (pstmnt1 != null) {
            pstmnt1.close();
        }
        rs1 = null;
        rs2 = null;
        pstmnt1 = null;
        pstmnt2 = null;
        return arrIdCasos;
    }

    public static String[] selectIdGabinete(Connection conn, String[] IdCasos) throws SQLException {
        String[] arrIdGabinetes;
        int tam = 0;
        PreparedStatement pstmnt1 = null;
        ResultSet rs1 = null;
        StringBuffer Query1 = new StringBuffer();
        tam = IdCasos.length;
        arrIdGabinetes = new String[tam];
        for (int a = 0; a < IdCasos.length; a++) {
            Query1.append("SELECT DISTINCT id_gabinete" + " FROM cg_caso" + " WHERE id_caso = ?");
            pstmnt1 = conn.prepareStatement(Query1.toString());
            pstmnt1.setString(1, IdCasos[a]);
            rs1 = pstmnt1.executeQuery();
            int j = 0;
            if (rs1.next()) {
                arrIdGabinetes[j] = rs1.getString(1);
                System.out.println("[j] = " + arrIdGabinetes[j]);
                j++;
            }
        }
        if (rs1 != null) {
            rs1.close();
        }
        if (pstmnt1 != null) {
            pstmnt1.close();
        }
        rs1 = null;
        pstmnt1 = null;
        return arrIdGabinetes;
    }

    public static int updateCasoDato(Connection conn, String valueCaso, String newFecha) throws SQLException {
        int retval = -1;
        StringBuffer Query2 = new StringBuffer();
        PreparedStatement pstmnt2 = null;
        ResultSet rs2 = null;
        Query2.append("UPDATE cg_caso_dato SET cd_valor = ? WHERE id_caso = ? AND id_cd = 13");
        pstmnt2 = conn.prepareStatement(Query2.toString());
        pstmnt2.setString(1, newFecha);
        pstmnt2.setString(2, valueCaso);
        retval = pstmnt2.executeUpdate();
        if (rs2 != null) {
            rs2.close();
        }
        if (pstmnt2 != null) {
            pstmnt2.close();
        }
        rs2 = null;
        pstmnt2 = null;
        return retval;
    }
}
