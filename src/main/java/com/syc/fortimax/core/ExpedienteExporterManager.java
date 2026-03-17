package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ExpedienteExporterManager {

    private static final Logger log = LoggerFactory.getLogger(ExpedienteExporterManager.class);

    public static String[] listaExportarAuditoria(Connection conn) throws Exception {
        String query = "SELECT DISTINCT viaticos.canocontrarrecibo, " + "	                        tramites.folio,  " + "	                        tramites.id_gabinete, " + "	                        Replace(viaticos.cComision, ';', '') AS cconcepto " + "	FROM   tviaticosTransparencia_V2 viaticos WITH(nolock)  " + "	       LEFT OUTER JOIN dbo.vtramitesexportar tramites WITH(nolock) " + "	                    ON viaticos.canocontrarrecibo = tramites.canocontrarrecibo " + "	                       AND tramites.cdocumento <> 'RECTIFICACION'  " + "	WHERE  1=1 " + "   AND  viaticos.caNoContrarrecibo <> ''" + "   AND  cExportado = 'N'";
        //	                 + "   AND  cOBGT = '38501'";
        //						+ "   AND    ctienecomision = 1  "
        //						+ "	  AND cExportado <> 'S' ";
        //						+ "   AND viaticos.caNoContrarrecibo like '26cp%'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            List<String> arr = new ArrayList<String>();
            while (rs.next()) {
                arr.add(rs.getString("canocontrarrecibo") + ";" + rs.getString("folio") + ";" + rs.getInt("id_gabinete") + ";" + rs.getString("cconcepto"));
            }
            return arr.toArray(new String[arr.size()]);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String[] listaExportarAuditoriaError(Connection conn) throws Exception {
        String query = "SELECT	caNoContrarrecibo, cFolio, idGabinete " + "  FROM   tTramitesExportarError WITH(nolock) " + " ORDER BY 1 ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            List<String> arr = new ArrayList<String>();
            while (rs.next()) {
                arr.add(rs.getString("caNoContrarrecibo") + ";" + rs.getString("cFolio") + ";" + rs.getInt("idGabinete"));
            }
            return arr.toArray(new String[arr.size()]);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void updateExportado(Connection conn, String tableName, String campo, char valor, String llave, String valorLlave) throws Exception {
        log.debug("Object: " + String.valueOf("Cambiando estatus en la tabla [" + tableName + "] Campo: [" + campo + "] Valor: [" + valor + "] PK: [" + llave + "] Valor:[" + valorLlave + "]"));
        Statement stmnt = null;
        String query = "UPDATE " + tableName + " WITH(ROWLOCK) SET " + campo + " = '" + valor + "' WHERE " + llave + " = '" + valorLlave + "'";
        try {
            stmnt = conn.createStatement();
            int r = stmnt.executeUpdate(query);
            log.debug("Object: " + String.valueOf("Se actualizaron: " + r + " registros exitosamente."));
        } finally {
            CloseObject.closeObject(stmnt);
        }
    }
}
