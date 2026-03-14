package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.cfdi.db.CloseObject;
import com.syc.sai.contabilidad.PasivoDiferidoManager;
import java.util.Base64;

public class PasivoDiferidoOperAjenaManager {

    public PasivoDiferidoOperAjenaManager() {
        super();
    }

    public static String buscaSolicitudes(Connection conn, String caNoContrarrecibo, String tipoRetencion) throws Exception {
        ResultSet rs = null;
        PreparedStatement pstmnt = null;
        boolean success = false;
        boolean aplicados = false;
        String nFolioRELACIONGASTOS = null;
        String mensaje = null;
        String query = "SELECT nFolioRELACIONGASTOS " + "FROM tOperAjenasEncabezado AS OAEnc WITH (NOLOCK)\r\n" + "JOIN tOperAjenasDetalle AS OADet WITH (NOLOCK) ON OAEnc.nFolioOperAjenas = OADet.nFolioOperAjenas\r\n" + "JOIN tRELACIONGASTOSEncabezado AS RGEnc WITH (NOLOCK) ON OADet.caNoContrarrecibo = RGEnc.caNoContrarrecibo\r\n" + "WHERE OAEnc.caNoContrarrecibo = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, caNoContrarrecibo);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                nFolioRELACIONGASTOS = rs.getString(1);
                aplicados = validaAplicados(conn, nFolioRELACIONGASTOS);
                if (!aplicados)
                    if ("7".equals(tipoRetencion) || "8".equals(tipoRetencion)) {
                        success = PasivoDiferidoManager.aplicaPasivoDiferidoRESICORG(conn, "RELACIONGASTOS", nFolioRELACIONGASTOS, "tRELACIONGASTOSEncabezado", "tRELACIONGASTOSDetalle", "nFolioRELACIONGASTOS");
                    } else if ("9".equals(tipoRetencion)) {
                        success = PasivoDiferidoManager.aplicaPasivoDiferidoLaudos(conn, "RELACIONGASTOS", nFolioRELACIONGASTOS, "tRELACIONGASTOSEncabezado", "tRELACIONGASTOSDetalle", "nFolioRELACIONGASTOS");
                    }
            }
            if (success) {
                conn.commit();
                mensaje = "Pasivo diferido aplicado";
            }
        } catch (Exception e) {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return mensaje;
    }

    private static boolean validaAplicados(Connection conn, String nFolioRELACIONGASTOS) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean aplicado = false;
        String query = " SELECT CASE WHEN nFolioPago IS NULL THEN 'N' ELSE 'S' END AS esAplicado FROM tPasivoDiferidoEncabezado WITH (NOLOCK) WHERE nFolioPAGO = ? AND cTipoPago = 'RELACIONGASTOS' ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nFolioRELACIONGASTOS);
            rs = ps.executeQuery();
            if (rs.next()) {
                aplicado = "S".equals(rs.getString("esAplicado"));
            }
        } catch (Exception e) {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return aplicado;
    }
}
