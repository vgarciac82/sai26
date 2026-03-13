package com.syc.reportes.servlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConsultaSaldosServlet", urlPatterns = { "/reportes/ConsultaSaldo" })
public class ConsultaSaldosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConsultaSaldosServlet.class);

    private Connection conn = null;

    private Statement pstmt = null;

    private ResultSet rs = null;

    private Usuario usuario;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        usuario = (Usuario) session.getAttribute(ATT_USER);
        if (usuario == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            response.sendRedirect("../index.jsp");
            return;
        }
        try {
            consultaSaldos(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private synchronized void consultaSaldos(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] param = request.getParameter("Param").toString().split(",");
        if (param.length < 3) {
            throw new Exception("Faltan datos para la consulta");
        }
        Integer mIni = Integer.parseInt(param[0]);
        Integer mFin = Integer.parseInt(param[1]);
        String cCuenta = param[2];
        String cCC = param[3];
        String cSubCuenta = "%";
        String Centro = "";
        if (param.length == 5) {
            cSubCuenta = param[4];
        }
        if (cCC.equals("0")) {
            Centro = "%";
        } else {
            Centro = cCC;
        }
        String sql = "";
        String sql_ini = "";
        String sql_cargos = "";
        String sql_abonos = "";
        conn = DataSourceManager.getConnection(jndiName);
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        PrintWriter out = response.getWriter();
        try {
            Integer count = mIni;
            if (mIni == 1) {
                sql_ini = "SUM(mSaldo0)";
            } else {
                sql_ini = "SUM(mSaldo" + Integer.toString(mIni - 1) + ")";
            }
            while (count <= mFin) {
                if (sql_cargos == "") {
                    sql_cargos = "";
                } else {
                    sql_cargos = sql_cargos + " + ";
                }
                sql_cargos = sql_cargos + "SUM(mDeber" + Integer.toString(count) + ")";
                if (sql_abonos == "") {
                    sql_abonos = "";
                } else {
                    sql_abonos = sql_abonos + " + ";
                }
                sql_abonos = sql_abonos + "SUM(mHaber" + Integer.toString(count) + ")";
                count += 1;
            }
            sql = "SELECT DISTINCT nCuenta " + ", REPLACE (cSubCuenta, '&', '&amp' )  cSubCuenta" + ", cCentroContable " + ", CONVERT(varchar, CAST( " + sql_ini + " AS money), 1) AS SaldoInicial " + ", CONVERT(varchar, CAST( " + sql_cargos + " AS money), 1) AS Cargos " + ", CONVERT(varchar, CAST( " + sql_abonos + " AS money), 1) AS Abonos " + ", CONVERT(varchar, CAST( " + sql_ini + " + (CASE WHEN naturalezaDeLaCuenta = 'D' THEN " + " ( " + sql_cargos + " ) - ( " + sql_abonos + " ) " + " ELSE " + " ( " + sql_abonos + " ) - ( " + sql_cargos + " )" + " END ) AS money), 1) AS SaldoFinal " + "FROM dbo.tSaldosVista (NOLOCK) " + " WHERE nCuenta LIKE '" + cCuenta + "' " + " AND cSubCuenta LIKE '" + cSubCuenta + "' " + " AND cCentroContable LIKE '" + Centro + "' " + "GROUP BY nCuenta, cSubCuenta, cCentroContable, naturalezaDeLaCuenta";
            log.info(sql);
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(sql);
            int nRenglon = 0;
            while (rs.next()) {
                JSONArray arrRenglon = new JSONArray();
                arrRenglon.put(0, rs.getString("nCuenta"));
                arrRenglon.put(1, rs.getString("cSubCuenta"));
                arrRenglon.put(2, rs.getInt("cCentroContable"));
                arrRenglon.put(3, rs.getString("SaldoInicial"));
                arrRenglon.put(4, rs.getString("Cargos"));
                arrRenglon.put(5, rs.getString("Abonos"));
                arrRenglon.put(6, rs.getString("SaldoFinal"));
                arrayObj.put(nRenglon, arrRenglon);
                nRenglon++;
            }
            if (nRenglon == 0) {
                jsonObj.put("ERROR", new String("La consulta no obtiene información - favor de rectificar!!!".getBytes("UTF-8"), "ISO-8859-1"));
                throw new Exception("La consulta no obtiene informaci\u00f3n - favor de rectificar!!!");
            }
            jsonObj.put("DATA", arrayObj);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            jsonObj.put("ERROR", new String(e.getMessage().getBytes("UTF-8"), "ISO-8859-1"));
            log.error(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmt);
            CloseObject.closeObject(conn);
            out.write(jsonObj.toString());
            out.flush();
            out.close();
            out = null;
        }
    }
}
