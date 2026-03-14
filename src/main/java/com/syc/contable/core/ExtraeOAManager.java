package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;

public class ExtraeOAManager {

    public ExtraeOAManager() {
        super();
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String szTemp, String szTabla) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        szTemp = (szTemp != "") ? szTemp : "";
        String Sql = " exec.dbo.OperacionesAjenas_2 " + szTemp;
        if (szTabla.equals("REPAJENASFALTANTES")) {
            Sql = " exec.dbo.OperacionesAjenas_SinAplic " + szTemp;
        }
        if (szTabla.equals("ExtractorPagos")) {
            Sql = " SELECT * FROM vConsultaPagosPlus " + szTemp;
        }
        pstmntH = conn.prepareStatement(Sql);
        //System.out.println(Sql);
        //pstmntH.setString(1, listaIds);
        rs = pstmntH.executeQuery();
        while (rs.next()) {
            String var_01 = (rs.getString(1) != null) ? rs.getString(1).trim().trim() : "";
            String var_02 = (rs.getString(2) != null) ? rs.getString(2).trim().trim() : "";
            String var_03 = (rs.getString(3) != null) ? rs.getString(3).trim().trim() : "";
            String var_04 = (rs.getString(4) != null) ? rs.getString(4).trim().trim() : "";
            String var_05 = (rs.getString(5) != null) ? rs.getString(5).trim().trim() : "";
            String var_06 = (rs.getString(6) != null) ? rs.getString(6).trim().trim() : "";
            String var_07 = (rs.getString(7) != null) ? rs.getString(7).trim().trim() : "";
            String var_08 = (rs.getString(8) != null) ? rs.getString(8).trim().trim() : "";
            String var_09 = (rs.getString(9) != null) ? rs.getString(9).trim().trim() : "";
            String var_10 = (rs.getString(10) != null) ? rs.getString(10).trim().trim() : "";
            String var_11 = (rs.getString(11) != null) ? rs.getString(11).trim().trim() : "";
            String var_12 = (rs.getString(12) != null) ? rs.getString(12).trim().trim() : "";
            String var_13 = (rs.getString(13) != null) ? rs.getString(13).trim().trim() : "";
            String var_14 = (rs.getString(14) != null) ? rs.getString(14).trim().trim() : "";
            String var_15 = (rs.getString(15) != null) ? rs.getString(15).trim().trim() : "";
            String var_16 = (rs.getString(16) != null) ? rs.getString(16).trim().trim() : "";
            String var_17 = (rs.getString(17) != null) ? rs.getString(17).trim().trim() : "";
            String var_18 = "", var_19 = "", var_20 = "", var_21 = "", var_22 = "", var_23 = "", var_24 = "", var_25 = "", var_26 = "", var_27 = "";
            String var_28 = "", var_29 = "", var_30 = "", var_31 = "", var_32 = "", var_33 = "", var_34 = "", var_35 = "", var_36 = "", var_37 = "";
            String var_38 = "", var_39 = "", var_40 = "";
            if (szTabla.equals("ExtractorPagos")) {
                var_18 = (rs.getString(18) != null) ? rs.getString(18).trim().trim() : "";
            }
            if (szTabla.equals("REPAJENAS")) {
                var_18 = (rs.getString(18) != null) ? rs.getString(18).trim().trim() : "";
                var_19 = (rs.getString(19) != null) ? rs.getString(19).trim().trim() : "";
                var_20 = (rs.getString(20) != null) ? rs.getString(20).trim().trim() : "";
                var_21 = (rs.getString(21) != null) ? rs.getString(21).trim().trim() : "";
                var_22 = (rs.getString(22) != null) ? rs.getString(22).trim().trim() : "";
                var_23 = (rs.getString(23) != null) ? rs.getString(23).trim().trim() : "";
                var_24 = (rs.getString(24) != null) ? rs.getString(24).trim().trim() : "";
                var_25 = (rs.getString(25) != null) ? rs.getString(25).trim().trim() : "";
                var_26 = (rs.getString(26) != null) ? rs.getString(26).trim().trim() : "";
                var_27 = (rs.getString(27) != null) ? rs.getString(27).trim().trim() : "";
                var_28 = (rs.getString(28) != null) ? rs.getString(28).trim().trim() : "";
                var_29 = (rs.getString(29) != null) ? rs.getString(29).trim().trim() : "";
                var_30 = (rs.getString(30) != null) ? rs.getString(30).trim().trim() : "";
                var_31 = (rs.getString(31) != null) ? rs.getString(31).trim().trim() : "";
                var_32 = (rs.getString(32) != null) ? rs.getString(32).trim().trim() : "";
                var_33 = (rs.getString(33) != null) ? rs.getString(33).trim().trim() : "";
                var_34 = (rs.getString(34) != null) ? rs.getString(34).trim().trim() : "";
                var_35 = (rs.getString(35) != null) ? rs.getString(35).trim().trim() : "";
                var_36 = (rs.getString(36) != null) ? rs.getString(36).trim().trim() : "";
                var_37 = (rs.getString(37) != null) ? rs.getString(37).trim().trim() : "";
                var_38 = (rs.getString(38) != null) ? rs.getString(38).trim().trim() : "";
                var_39 = (rs.getString(39) != null) ? rs.getString(39).trim().trim() : "";
                var_40 = (rs.getString(40) != null) ? rs.getString(40).trim().trim() : "";
            }
            String encabezado = var_01 + "|" + var_02 + "|" + var_03 + "|" + var_04 + "|" + var_05 + "|" + var_06 + "|" + var_07 + "|" + var_08 + "|" + var_09 + "|" + var_10 + "|" + var_11 + "|" + var_12 + "|" + var_13 + "|" + var_14 + "|" + var_15 + "|" + var_16 + "|" + var_17;
            if (szTabla.equals("ExtractorPagos")) {
                encabezado = encabezado + "|" + var_18 + "|";
            }
            if (szTabla.equals("REPAJENAS")) {
                encabezado = encabezado + "|" + var_18 + "|" + var_19 + "|" + var_20 + "|" + var_21 + "|" + var_22 + "|" + var_23 + "|" + var_24 + "|" + var_25 + "|" + var_26 + "|" + var_27 + "|" + var_28 + "|" + var_29 + "|" + var_30 + "|" + var_31 + "|" + var_32 + "|" + var_33 + "|" + var_34 + "|" + var_35 + "|" + var_36 + "|" + var_37 + "|" + var_38 + "|" + var_39 + "|" + var_40;
            }
            encabezado = encabezado + "\r\n";
            arrListaComp.add(encabezado);
        }
        if (rs != null) {
            rs.close();
        }
        if (pstmntH != null) {
            pstmntH.close();
        }
        return arrListaComp;
    }
}
