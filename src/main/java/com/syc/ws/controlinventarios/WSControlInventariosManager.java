package com.syc.ws.controlinventarios;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class WSControlInventariosManager {

    private static Logger log = LoggerFactory.getLogger(WSControlInventariosManager.class);

    public static int sendCtrlInventory(Connection conn, int iMonth, int iYear, int iType) throws Exception {
        PreparedStatement psInfoPago = null;
        ResultSet rsInfoPago = null;
        int resultado = 0;
        try {
            //Validar si el ws está encendido
            int iWSActivo = validaEstatusWS(conn);
            if (iWSActivo != 0) {
                log.info("El Web service se encuentra configurado para que no se ejecute.");
                return -2;
            }
            resultado = sendTypeCtrlInventoryJson(conn, iMonth, iYear, iType);
            return resultado;
        } finally {
            CloseObject.closeObject(rsInfoPago);
            CloseObject.closeObject(psInfoPago);
        }
    }

    private static int sendTypeCtrlInventoryJson(Connection conn, int iMonth, int iYear, int iType) throws Exception {
        //"http://10.61.0.9:8080/conciliacion-inventory-ws-app/services/sai/getMonthlyConciliationData";
        String url = urlWS_Ctrl_Inventarios(conn, iType);
        String metodo = "GET", tipoRespuesta = "application/json;charset=UTF-8";
        JSONArray respWS = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("monthSearch", iMonth);
        jsonObj.put("yearSearch", iYear);
        jsonObj.put("typeSearch", iType);
        url = url + "/" + iMonth + "/" + iYear + "/" + iType;
        // Conexion al Web Service
        WSConnectionControlInventarios ctrl_InvExt = new WSConnectionControlInventarios();
        respWS = ctrl_InvExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
        //if(jsonrespWS.getInt("code") == 0){
        if (respWS.length() > 0) {
            insertarControlInventarios(conn, iMonth, iYear, iType, respWS);
        } else {
            log.error("No se pudo Obtener datos del Web Service...");
            return -1;
        }
        log.info("Respuesta Web Service Correcta ");
        return respWS.length();
    }

    private static int validaEstatusWS(Connection conn) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        //WS inactivo
        int resp = -2;
        ResultSet rs = null;
        try {
            String query = " SELECT * FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) " + " WHERE G_NOMBRE = 'WS_CONTROL_INVENTARIO' " + " AND GP_NOMBRE = 'Activa_WS_CtrlInventario' AND GP_VALOR = 'TRUE'";
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                return 0;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            rs = null;
        }
        return resp;
    }

    private static String urlWS_Ctrl_Inventarios(Connection conn, int iType) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        String url = "";
        String GP_NOMBRE = "Ruta_WS_CtrlInventario";
        if (iType == 6 || iType == 7) {
            GP_NOMBRE = "Ruta_WS_CtrlInventario2";
        }
        ResultSet rs = null;
        try {
            String query = " SELECT * FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) " + " WHERE G_NOMBRE = 'WS_CONTROL_INVENTARIO' " + " AND GP_NOMBRE = ? ";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, GP_NOMBRE);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                url = rs.getString("GP_VALOR");
            }
            System.out.println("URL: " + url);
            log.info("Object: {}", "URL: " + url);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            rs = null;
        }
        return url;
    }

    private static int insertarControlInventarios(Connection conn, int iMonth, int iYear, int iType, JSONArray jsonarray) throws Exception, JSONException {
        int iResultado = 0;
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonObj = (JSONObject) jsonarray.get(i);
            long lFAplicacion = jsonObj.getLong("fAplicacion");
            Date fApl = new Date(lFAplicacion);
            String cPartida = jsonObj.getString("cPartida");
            double mImporte = jsonObj.getDouble("mImporte");
            int nFolioPoliza = jsonObj.getInt("nFolioPoliza");
            String cTipoPoliza = jsonObj.getString("cTipoPoliza");
            String cEvento = jsonObj.getString("cEvento");
            iResultado = insertaCtrlInventario(conn, iMonth, iYear, iType, fApl, cPartida, mImporte, nFolioPoliza, cTipoPoliza, cEvento);
        }
        return iResultado;
    }

    private static int insertaCtrlInventario(Connection conn, int iMonth, int iYear, int iType, Date fAplicacion, String cPartida, double mImporte, int nFolioPoliza, String cTipoPoliza, String cEvento) throws Exception {
        PreparedStatement ps = null;
        int iResultado = 0;
        int nAcumulada = 0;
        String sSql = " INSERT INTO tSistemaControlInventarios (idTipoConciliacion, nMes, fAplicacion, mImporte, nFolioPoliza, cPartida, cTipoPoliza, cEvento, nAcumulada) " + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        if ("NA".equals(cEvento)) {
            fAplicacion = devuelveFechaMesAnterior(conn, iMonth, iYear);
            nFolioPoliza = 0;
            nAcumulada = 1;
        }
        try {
            if (validaRowCtrlInventario(conn, iMonth, iType, cPartida, nFolioPoliza, mImporte, nAcumulada, cTipoPoliza) == 0) {
                ps = conn.prepareStatement(sSql);
                ps.setInt(1, iType);
                ps.setInt(2, iMonth);
                ps.setDate(3, fAplicacion);
                ps.setDouble(4, mImporte);
                ps.setInt(5, nFolioPoliza);
                ps.setString(6, cPartida);
                ps.setString(7, cTipoPoliza);
                ps.setString(8, cEvento);
                ps.setInt(9, nAcumulada);
                iResultado = ps.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return iResultado;
    }

    private static int validaRowCtrlInventario(Connection conn, int iMonth, int iType, String cPartida, int nFolioPoliza, double mImporte, int nAcumulada, String cTipoPoliza) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int iResultado = 0;
        String sSql = " SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS existe FROM tSistemaControlInventarios WITH (NOLOCK) " + " WHERE idTipoConciliacion = ? AND nMes = ? AND nFolioPoliza = ? AND cPartida = ? AND mImporte = ? AND nAcumulada = ? AND cTipoPoliza = ? ";
        try {
            ps = conn.prepareStatement(sSql);
            ps.setInt(1, iType);
            ps.setInt(2, iMonth);
            ps.setInt(3, nFolioPoliza);
            ps.setString(4, cPartida);
            ps.setDouble(5, mImporte);
            ps.setInt(6, nAcumulada);
            ps.setString(7, cTipoPoliza);
            rs = ps.executeQuery();
            if (rs.next()) {
                iResultado = rs.getInt("existe");
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return iResultado;
    }

    private static Date devuelveFechaMesAnterior(Connection conn, int iMonth, int iYear) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Date fecha = null;
        String sSql = " DECLARE @mydate DATETIME " + " SET @mydate = CONVERT(DATETIME, '" + Integer.toString(iYear) + "-" + Integer.toString(iMonth) + "-1', 120) " + " SELECT DATEADD(dd,-(DAY(@mydate)),@mydate) AS Fecha ";
        try {
            ps = conn.prepareStatement(sSql);
            rs = ps.executeQuery();
            if (rs.next()) {
                fecha = rs.getDate("Fecha");
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return fecha;
    }
}
