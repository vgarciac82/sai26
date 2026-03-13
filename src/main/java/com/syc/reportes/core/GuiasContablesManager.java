package com.syc.reportes.core;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiasContablesManager {

    public static final Logger log = LoggerFactory.getLogger(GuiasContablesManager.class);

    public static void GuiasContablesManager(Connection conn, String fechaInicio, int idversion_sig, int nivel, int id_guia, String usuario) throws Exception {
        CallableStatement cs1 = null;
        ResultSet rs1 = null;
        String query1 = "";
        if (nivel == 4)
            query1 = "{call sp_guias_contabilizadoras( ?, ?, ?, ?, ? )}";
        else if (nivel == 17)
            query1 = "{call sp_guias_contabilizadoras_n15( ?, ?, ?, ?, ? )}";
        try {
            cs1 = conn.prepareCall(query1);
            cs1.setInt(1, idversion_sig);
            cs1.setInt(2, 1);
            cs1.setInt(3, 0);
            cs1.setInt(4, nivel);
            cs1.setInt(5, id_guia);
            rs1 = cs1.executeQuery();
        } finally {
            CloseObject.closeObject(rs1, false);
            CloseObject.closeObject(cs1, false);
        }
    }

    public static void cosultaVersiones(Connection conn, HttpServletResponse resp, String reportPath, String ruta, Map<String, Object> parms) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(reportPath);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            resp.setContentType("application/pdf");
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }

    public static int InsertaVersion(Connection conn, String fechaInicio, int idversion_sig, String usuario, int id_tipo, int id_subTipo) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        int cont = 0;
        try {
            cstmt = conn.prepareCall("{call sp_insertaVersion ( ?, ?, ?, ?, ? )}");
            cstmt.setInt(1, idversion_sig);
            cstmt.setInt(2, id_tipo);
            cstmt.setInt(3, id_subTipo);
            cstmt.setString(4, usuario);
            cstmt.setString(5, fechaInicio);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                cont = (rs.getInt(1));
            }
            return cont;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    public static void cosultaTemporal(Connection conn, HttpServletResponse resp, String ruta, Map<String, Object> parms, String reportPath) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(reportPath);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            resp.setContentType("application/pdf");
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }

    public static List<Integer> CatalogoNivel(Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> l = new ArrayList<Integer>();
        String query = "SELECT idNivel FROM tCatalogoNivelGuia where nActivo = 1 ";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                l.add(rs.getInt("idNivel"));
            }
            return l;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static void ManejoCuentas(Connection conn, String fechaInicio, int idversion_sig, int nivel, int id_ManejoCuentas) throws Exception {
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        String query2 = "";
        if (nivel == 4)
            query2 = "{call sp_manejoDeCuentas( ?, ?, ?, ?, ? )}";
        else if (nivel == 17)
            query2 = "{call sp_manejoDeCuentas_n15( ?, ?, ?, ?, ? )}";
        try {
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, idversion_sig);
            cs2.setInt(2, 1);
            cs2.setInt(3, 0);
            cs2.setInt(4, nivel);
            cs2.setInt(5, id_ManejoCuentas);
            rs2 = cs2.executeQuery();
        } finally {
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    public static void PlanCuentas(Connection conn, String fechaInicio, int idversion_sig, int nivel) throws Exception {
        CallableStatement cs3 = null, cs4 = null;
        ResultSet rs3 = null, rs4 = null;
        String query3 = "";
        String query4 = "";
        if (nivel == 4) {
            query3 = "{call sp_plan_cuentas( ?, ?, ?, ? )}";
            query4 = "{call sp_plan_cuentas_def( ?, ?, ?, ? )}";
        } else if (nivel == 17) {
            query3 = "{call sp_plan_cuentas_n15( ?, ?, ?, ? )}";
            query4 = "{call sp_plan_cuentas_def_n15( ?, ?, ?, ? )}";
        }
        try {
            cs3 = conn.prepareCall(query3);
            cs3.setInt(1, idversion_sig);
            cs3.setInt(2, 1);
            cs3.setInt(3, 0);
            cs3.setInt(4, nivel);
            rs3 = cs3.executeQuery();
            cs4 = conn.prepareCall(query4);
            cs4.setInt(1, idversion_sig);
            cs4.setInt(2, 1);
            cs4.setInt(3, 0);
            cs4.setInt(4, nivel);
            rs4 = cs4.executeQuery();
        } finally {
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(cs3, false);
            CloseObject.closeObject(rs4, false);
            CloseObject.closeObject(cs4, false);
        }
    }

    public static int Complementos(Connection conn, String fechaInicio, int idversion_complemento, int id_complemento, String usuario) throws Exception {
        CallableStatement cs5 = null;
        PreparedStatement ps = null;
        ResultSet rs5 = null;
        int cont = 0;
        String query5 = "{call sp_complementoManual( ?, ?, ?, ? )}";
        String query6 = "INSERT INTO tComplementoVersion (idVersion, idComplemento, U_LOGIN, fAplicacion) VALUES ( ?, ?, ?, ? )";
        try {
            cs5 = conn.prepareCall(query5);
            cs5.setInt(1, idversion_complemento);
            cs5.setInt(2, 1);
            cs5.setInt(3, 0);
            cs5.setInt(4, id_complemento);
            rs5 = cs5.executeQuery();
            ps = conn.prepareStatement(query6);
            ps.setInt(1, idversion_complemento);
            ps.setInt(2, id_complemento);
            ps.setString(3, usuario);
            ps.setString(4, fechaInicio);
            ps.executeUpdate();
            cont = 1;
        } finally {
            CloseObject.closeObject(rs5, false);
            CloseObject.closeObject(cs5, false);
            CloseObject.closeObject(ps, false);
        }
        return cont;
    }

    public static int InsertaVersionMasivo(Connection conn, String fechaInicio, String usuario, int id_tipo) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        int cont = 0;
        try {
            cstmt = conn.prepareCall("{call sp_insertaVersionGeneral ( ?, ?, ? )}");
            cstmt.setInt(1, id_tipo);
            cstmt.setString(2, usuario);
            cstmt.setString(3, fechaInicio);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                cont = (rs.getInt(1));
            }
            return cont;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }
}
