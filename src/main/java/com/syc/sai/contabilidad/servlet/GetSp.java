package com.syc.sai.contabilidad.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import com.syc.admin.servlet.ReportsException;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class GetSp extends DataSourceManager {

    public GetSp() {
        System.out.println("en EXEC");
        Connection con = null;
        try {
            ExecSp(getConnection());
        } catch (Exception e) {
            ;
        }
    }

    public GetSp(String sp) {
        try {
            ExecSp(getConnection());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //GetSp
    public static void ExecSp(Connection conn) {
        Connection con = conn;
        try {
            // con=getConnection();
            //cs= conn.prepareCall("{call sp_BuscaDatoCamposTabla_syc('%contrato%', '', '', '''%SGAPDS-OCLSP-JAL-09-127-rf-lp-PASIVO%''', 'ccentrocontable', 'entidadcontable', '', '''%''')}");
            CallableStatement cs = con.prepareCall("{call sp_BuscaDatoCamposTabla_syc ( ?, ?, ?, ?, ?, ?, ?, ?)}");
            //cs.registerOutParameter("@SALIDA", Types.VARCHAR);
            cs.setString("@campo", "%contrato%");
            cs.setString("@campo2", "");
            cs.setString("@campo3", "");
            cs.setString("@valor", "%SGAPDS-OCLSP-JAL-09-127-rf-lp-PASIVO%");
            cs.setString("@campowhere", "ccentrocontable");
            cs.setString("@campowhere2", "entidadcontable");
            cs.setString("@campowhere3", "");
            cs.setString("@valorwhere", "%");
            boolean hayRes = cs.execute();
            System.out.println("Hay Resultados>>>" + hayRes);
            ResultSet rs = cs.getResultSet();
            ResultSetMetaData rsmd = null;
            //ResultSet rs = (ResultSet) cs.getObject(1);
            //System.out.println(cs.getString(1));
            int i = 0;
            if (rs == null) {
                System.out.println(rs);
            } else {
                rsmd = rs.getMetaData();
                while (rs.next()) {
                    for (i = 1; i <= rsmd.getColumnCount(); i++) {
                        System.out.print(rs.getString(i));
                    }
                    System.out.println();
                }
            }
            //else temporal
            while (cs.getMoreResults()) {
                rs = cs.getResultSet();
                rsmd = rs.getMetaData();
                while (rs.next()) {
                    for (i = 1; i <= rsmd.getColumnCount(); i++) {
                        System.out.print(rs.getString(i));
                    }
                    System.out.println();
                }
            }
            //if
        } catch (Exception e) {
            System.out.println("TAAA MAA..: " + e);
            //throw new ReportsException(e);
        } finally {
            try {
                //	CloseObject.closeObject(rs, false);
                //CloseObject.closeObject(cs, false);
            } catch (Exception e) {
            }
        }
    }
    //metodo
}
