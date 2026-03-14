package com.syc.xml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class XmlFileSearchManager {

    public XmlFileSearchManager() {
    }

    public static String select(Connection conn, String titulo_aplicacion, int id_gabinete) throws SQLException {
        PreparedStatement pstmnt = null;
        PreparedStatement pstmnt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        StringBuffer query = new StringBuffer();
        StringBuffer query1 = new StringBuffer();
        String PathFile = null;
        query.append("SELECT * FROM imx_pagina a WHERE a.titulo_aplicacion = ?    AND a.id_gabinete = ?    AND a.id_carpeta_padre = 0 ");
        pstmnt = conn.prepareStatement(query.toString());
        pstmnt.setString(1, titulo_aplicacion);
        pstmnt.setInt(2, id_gabinete);
        rs = pstmnt.executeQuery();
        if (rs.next()) {
            query1.append("SELECT volumen, unidad_disco, ruta_base, ruta_directorio FROM imx_volumen WITH (NOLOCK) WHERE volumen = ? ");
            pstmnt1 = conn.prepareStatement(query1.toString());
            pstmnt1.setString(1, rs.getString("volumen"));
            rs1 = pstmnt1.executeQuery();
            if (rs1.next()) {
                PathFile = (new StringBuilder(String.valueOf(rs1.getString("unidad_disco")))).append(rs1.getString("ruta_base")).append(rs1.getString("ruta_directorio")).append(rs.getString("nom_archivo_vol")).toString();
            }
        }
        if (rs != null)
            rs.close();
        if (rs1 != null)
            rs1.close();
        if (pstmnt != null)
            pstmnt.close();
        if (pstmnt1 != null)
            pstmnt1.close();
        rs = null;
        rs1 = null;
        pstmnt = null;
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
}
