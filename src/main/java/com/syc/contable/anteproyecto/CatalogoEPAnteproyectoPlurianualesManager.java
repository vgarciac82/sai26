package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogoEPAnteproyectoPlurianualesManager {

    private static Logger log = LoggerFactory.getLogger(CatalogoEPAnteproyectoPlurianualesManager.class);

    public static int insertaRenglonEPAnteproyectoPlurianuales(Connection conn, Map<String, String> infoRenglon) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Iniciando insercion de renglon");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate(Util.genInsertFromMap("tvalidacion_anteproyecto_plurianuales", infoRenglon));
            log.trace("Se inserto " + r + "registros");
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public static List<String> validacionEPPlurianual(Connection conn) throws Exception {
        Statement stmnt = null;
        ResultSet rs = null;
        List<String> r = new ArrayList<String>();
        try {
            String sql = " EXEC dbo.sp_ValidaEPPlurianual ";
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(sql);
            while (rs.next()) {
                r.add(rs.getString("msg"));
            }
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
        return r;
    }
}
