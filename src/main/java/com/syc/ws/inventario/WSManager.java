package com.syc.ws.inventario;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
//import org.apache.poi.hpsf.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.UnidadEjecutora;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.core.UnidadEjecutoraManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSManager {

    public static final String EXITO = "success";

    public static final String EXISTE = "exist";

    public static final String ERROR = "error";

    public static final String NOTEXIST = "not exist";

    private static Logger log = LoggerFactory.getLogger(WSManager.class);

    public static int sendAdquistion(Connection conn, String nFolio, int oper) throws Exception {
        String queryInfoPago = "SELECT	cfoliopagodiverso,	cIdRecepMat, cIdUsuarioCaptura ,substring(cIdRecepMat,4,3) as unidadEjecutora,nFolioPoliza,cCentroContable FROM	tPAGODIVERSOEncabezado with(nolock) WHERE	nFolioPAGODIVERSO = ?";
        PreparedStatement psInfoPago = null;
        ResultSet rsInfoPago = null;
        int resultado = 0;
        try {
            // Validar si el ws está encendido
            int WsActivo = validaEstatusWS(conn);
            if (WsActivo != 0) {
                log.info("El Web service se encuentra configurado para que no se ejecute.");
                return -2;
            }
            psInfoPago = conn.prepareStatement(queryInfoPago);
            psInfoPago.setInt(1, Integer.parseInt(nFolio));
            rsInfoPago = psInfoPago.executeQuery();
            String cIdContrato;
            String cIdRecepMat;
            String uLogin;
            String unidadEjecutora;
            String cCentroContable;
            int nFolioPoliza;
            if (rsInfoPago.next()) {
                cIdContrato = rsInfoPago.getString("cfoliopagodiverso").trim();
                cIdRecepMat = rsInfoPago.getString("cIdRecepMat").trim();
                uLogin = rsInfoPago.getString("cIdUsuarioCaptura").trim();
                unidadEjecutora = rsInfoPago.getString("unidadEjecutora").trim();
                cCentroContable = rsInfoPago.getString("cCentroContable").trim();
                nFolioPoliza = rsInfoPago.getInt("nFolioPoliza");
                Usuario u = new Usuario();
                u.setLogin(uLogin);
                u = UsuarioManager.select(conn, u);
                u = UsuarioManager.getRamoUR(conn, u);
                int entradaAlmacen = statusEntradaAlmacen(conn, nFolio);
                String capitulo = capituloPago(conn, nFolio);
                String rutaFortimax = obtenerRutaFortimax(conn, nFolio, cIdRecepMat);
                int capBienes = validaRecepcionBienes(conn, cIdRecepMat, cIdContrato);
                if (capBienes == -1) {
                    log.info("Recepción de servicio.");
                    if (oper == 2)
                        WSManager.cambiaEstatusRecepcion(conn, 2, Integer.parseInt(nFolio));
                    return 0;
                }
                switch(oper) {
                    case 0:
                        resultado = sendAdquisitionJson(conn, Integer.parseInt(nFolio), cIdContrato, cIdRecepMat, u, oper, unidadEjecutora, nFolioPoliza, cCentroContable, entradaAlmacen, capitulo, rutaFortimax);
                        break;
                    case 1:
                        resultado = sendPayAdquisitionJson(conn, cIdRecepMat, cIdContrato, u, oper, unidadEjecutora);
                        break;
                    case 2:
                        resultado = sendCancelAdquisitionJson(conn, Integer.parseInt(nFolio), cIdRecepMat, cIdContrato, u, oper, unidadEjecutora);
                        break;
                    default:
                        resultado = -1;
                        log.info("Operación erronea.");
                        break;
                }
                return resultado;
            } else {
                throw new Exception("No fue posible cargar la informacion del pago[ " + nFolio + "] para reportar la entrada a almacen. Notifique al administrador.");
            }
        } finally {
            CloseObject.closeObject(rsInfoPago);
            CloseObject.closeObject(psInfoPago);
        }
    }

    private static int sendAdquisitionJson(Connection conn, int nFolio, String cIDPagoDiverso, String cIdRecepMat, Usuario u, int oper, String unidadEjecutora, int nFolioPoliza, String cc, int entradaAlmacen, String capitulo, String rutaFortimax) throws Exception {
        Empleado e = new Empleado();
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        e.setClaveUsuario(u.getLogin());
        e = ebl.getEmpleado(e);
        EmpleadoArea ea = new EmpleadoArea();
        ea.setId(e.getClaveArea());
        ea = ebl.getEmpleadoArea(ea);
        UnidadEjecutora ue = UnidadEjecutoraManager.selectUnidadEjecutora(conn, u.getU_UR());
        String fullName = u.getNombre();
        String location = ue.getUe();
        String descLocation = ue.getDescripcion();
        String project = "[" + e.getClaveArea() + "] " + ea.getDescripcion();
        String userJob = e.getCargo();
        // "http://10.0.0.34:8080/inventory-ws-app/services/inventory/transaction/inputAcquisition";
        String url = urlWS(conn, oper);
        String metodo = "POST", tipoRespuesta = "application/json;charset=UTF-8";
        String respWS = "";
        JSONObject jsonObj = new JSONObject();
        Date date = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        jsonObj.put("centroContable", cc);
        jsonObj.put("nFolioPoliza", nFolioPoliza);
        jsonObj.put("unidadEjecutora", unidadEjecutora);
        jsonObj.put("purchaseFolio", cIDPagoDiverso);
        jsonObj.put("purchaseDate", formato.format(date));
        // cIdRecepMat
        jsonObj.put("purchaseReceptionCode", cIdRecepMat);
        jsonObj.put("nIdEntraAlmacen", entradaAlmacen);
        jsonObj.put("departure", capitulo);
        // json Fcturas
        JSONArray invoicesArray = jsonFacturas(conn, String.valueOf(nFolio));
        jsonObj.put("invoices", invoicesArray);
        JSONObject transactionRequestor = new JSONObject();
        transactionRequestor.put("fullName", fullName);
        transactionRequestor.put("location", location);
        transactionRequestor.put("management", descLocation);
        transactionRequestor.put("project", project);
        transactionRequestor.put("userJob", userJob);
        jsonObj.put("transactionRequestor", transactionRequestor);
        // Json Reepcion
        JSONArray transactionDetails = jsonRecepcion(conn, cIdRecepMat, cIDPagoDiverso);
        jsonObj.put("transactionDetails", transactionDetails);
        //Json Atenta Nota
        JSONObject attentiveNote = jsonNote(conn, nFolio, rutaFortimax);
        jsonObj.put("attentiveNote", attentiveNote);
        // Connection al ws
        GenericConnectionWS bienesExt = new GenericConnectionWS();
        // {"estatus":"Error","code":1}
        respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
        JSONObject jsonrespWS = new JSONObject(respWS);
        if (jsonrespWS.getInt("code") == 0) {
            guardaIdTransactionWS(conn, jsonrespWS.getLong("idTransaction"), nFolio, nFolioPoliza);
        } else {
            log.error("No se recibio el idTransaction");
            return -1;
        }
        log.info("Respuesta Web Service, estatus: " + jsonrespWS.getString("estatus"));
        return jsonrespWS.getInt("code");
    }

    private static int sendPayAdquisitionJson(Connection conn, String cIdRecepMat, String cIDPagoDiverso, Usuario u, int oper, String unidadEjecutora) throws Exception {
        Empleado e = new Empleado();
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        e.setClaveUsuario(u.getLogin());
        e = ebl.getEmpleado(e);
        EmpleadoArea ea = new EmpleadoArea();
        ea.setId(e.getClaveArea());
        ea = ebl.getEmpleadoArea(ea);
        UnidadEjecutora ue = UnidadEjecutoraManager.selectUnidadEjecutora(conn, u.getU_UR());
        String fullName = u.getNombre();
        String location = ue.getUe();
        String descLocation = ue.getDescripcion();
        String project = "[" + e.getClaveArea() + "] " + ea.getDescripcion();
        String userJob = e.getCargo();
        // "http://10.0.0.34:8080/inventory-ws-app/services/inventory/transaction/payAcquisition";
        String url = urlWS(conn, oper);
        String metodo = "POST", tipoRespuesta = "application/json;charset=UTF-8";
        String respWS = "";
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("unidadEjecutora", unidadEjecutora);
        jsonObj.put("purchaseFolio", cIDPagoDiverso);
        jsonObj.put("purchaseReceptionCode", cIdRecepMat);
        jsonObj.put("notes", "Cancelación de prueba.");
        JSONObject transactionRequestor = new JSONObject();
        transactionRequestor.put("fullName", fullName);
        transactionRequestor.put("location", location);
        transactionRequestor.put("management", descLocation);
        transactionRequestor.put("project", project);
        transactionRequestor.put("userJob", userJob);
        jsonObj.put("transactionRequestor", transactionRequestor);
        // Connection al ws
        GenericConnectionWS bienesExt = new GenericConnectionWS();
        respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
        JSONObject jsonrespWS = new JSONObject(respWS);
        log.info("Respuesta Web Service, estatus: " + jsonrespWS.getString("estatus"));
        return jsonrespWS.getInt("code");
    }

    private static int sendCancelAdquisitionJson(Connection conn, int nFolioPago, String cIdRecepMat, String cIDPagoDiverso, Usuario u, int oper, String unidadEjecutora) throws Exception {
        Empleado e = new Empleado();
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        e.setClaveUsuario(u.getLogin());
        e = ebl.getEmpleado(e);
        EmpleadoArea ea = new EmpleadoArea();
        ea.setId(e.getClaveArea());
        ea = ebl.getEmpleadoArea(ea);
        int idTransaction = getIdTransactionPayment(conn, nFolioPago);
        UnidadEjecutora ue = UnidadEjecutoraManager.selectUnidadEjecutora(conn, u.getU_UR());
        String fullName = u.getNombre();
        String location = ue.getUe();
        String descLocation = ue.getDescripcion();
        String project = "[" + e.getClaveArea() + "] " + ea.getDescripcion();
        String userJob = e.getCargo();
        // "http://10.0.0.34:8080/inventory-ws-app/services/inventory/transaction/cancelAcquisition";
        String url = urlWS(conn, oper);
        String metodo = "POST", tipoRespuesta = "application/json;charset=UTF-8";
        String respWS = "";
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("unidadEjecutora", unidadEjecutora);
        jsonObj.put("purchaseFolio", cIDPagoDiverso);
        // cIdRecepMat
        jsonObj.put("purchaseReceptionCode", cIdRecepMat);
        //idTransaction
        jsonObj.put("notes", "Cancelación de prueba.");
        jsonObj.put("idTransaction", idTransaction);
        JSONObject transactionRequestor = new JSONObject();
        transactionRequestor.put("fullName", fullName);
        transactionRequestor.put("location", location);
        transactionRequestor.put("management", descLocation);
        transactionRequestor.put("project", project);
        transactionRequestor.put("userJob", userJob);
        jsonObj.put("transactionRequestor", transactionRequestor);
        // Connection al ws
        GenericConnectionWS bienesExt = new GenericConnectionWS();
        respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
        JSONObject jsonrespWS = new JSONObject(respWS);
        log.info("Respuesta Web Service, estatus: " + jsonrespWS.getString("estatus"));
        WSManager.cambiaEstatusRecepcion(conn, 4, nFolioPago);
        return jsonrespWS.getInt("code");
    }

    private static int getIdTransactionPayment(Connection conn, int nFolio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        int idTransaction = 0;
        try {
            pst = conn.prepareStatement("select nIdTransactionWS from tPAGODIVERSOEncabezado with (NOLOCK) where nFolioPAGODIVERSO = ?");
            pst.setInt(1, nFolio);
            rs = pst.executeQuery();
            if (rs.next()) {
                idTransaction = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
        return idTransaction;
    }

    private static JSONArray jsonRecepcion(Connection conn, String cIdRecepMat, String cIDPagoDiverso) throws SQLException, JSONException, UnsupportedEncodingException {
        PreparedStatement pstmnt = null;
        String query = "select *from v_mInputAcquisitionWS with(Nolock) where cIdpedContDef=? and cIdRecepMat=?";
        JSONArray arrarTransactionDet = new JSONArray();
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIDPagoDiverso);
            pstmnt.setString(2, cIdRecepMat);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                JSONObject tupla = new JSONObject();
                tupla.put("cucop", rs.getString("cucop"));
                tupla.put("purchaseIndex", rs.getInt("purchaseIndex"));
                tupla.put("additionalDescription", rs.getString("additionalDescription"));
                tupla.put("percentageIva", rs.getInt("percentageIva"));
                tupla.put("indexIva", rs.getDouble("unitCostIva"));
                tupla.put("unitCost", rs.getDouble("unitCost"));
                tupla.put("indexTotal", rs.getDouble("unitCostInventory"));
                tupla.put("quantity", rs.getInt("quantity"));
                arrarTransactionDet.put(tupla);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return arrarTransactionDet;
    }

    private static JSONArray jsonFacturas(Connection conn, String nfoliopago) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        String query = "SELECT	cfactura AS folio, CONVERT(VARCHAR(10), Getdate(), 120) AS date," + " 		crfcfactura AS Proveedor, motrosimpuestos AS otherTaxes, mimporteiva AS totalIva, mimporteconiva as total, nfoliopago, cRazonSocial " + " FROM   tpagofactura WITH(nolock) " + " WHERE  ctipopago = 'PAGODIVERSO'  AND nfoliopago = ? ";
        ResultSet rs = null;
        JSONArray arrayTransactionDet = null;
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, nfoliopago);
            rs = pstmnt.executeQuery();
            arrayTransactionDet = new JSONArray();
            while (rs.next()) {
                JSONObject tupla = new JSONObject();
                tupla.put("folio", rs.getString("folio"));
                tupla.put("registeredDate", rs.getDate("date"));
                tupla.put("supplier", rs.getString("Proveedor"));
                tupla.put("supplier_name", rs.getString("cRazonSocial"));
                tupla.put("otherTaxes", rs.getDouble("otherTaxes"));
                tupla.put("totalIva", rs.getDouble("totalIva"));
                tupla.put("total", rs.getDouble("total"));
                arrayTransactionDet.put(tupla);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return arrayTransactionDet;
    }

    private static JSONObject jsonNote(Connection conn, int nFolio, String rutaFortimax) throws SQLException, JSONException, UnsupportedEncodingException {
        PreparedStatement pstmnt = null;
        String query = "select * from v_atentaNota WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ?";
        JSONObject nota = new JSONObject();
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nota.put("nIdNote", rs.getInt("nIDNota"));
                nota.put("cIdContract", rs.getString("cIDContrato"));
                nota.put("cNoteFolio", rs.getString("cFolioNota"));
                nota.put("nEmployee", rs.getInt("cNumeroEmpleado"));
                nota.put("fNoteDate", rs.getDate("dFechaNota"));
                nota.put("cHyperLink", rutaFortimax);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return nota;
    }

    private static int validaRecepcionBienes(Connection conn, String cIdRecepMat, String cIDPagoDiverso) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        int resp = 0;
        ResultSet rs = null;
        try {
            String query = "select *from v_mInputAcquisitionWS with(Nolock) where cIdpedContDef=? and cIdRecepMat=? " + " and (cidcapitulo  not in(2,5))";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIDPagoDiverso);
            pstmnt.setString(2, cIdRecepMat);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                return -1;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return resp;
    }

    private static int statusEntradaAlmacen(Connection conn, String folio) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        int estatus = 0;
        ResultSet rs = null;
        String query = "SELECT nIdEntraAlmacen FROM v_atentaNota WITH (NOLOCK) WHERE nFolioPAGODIVERSO = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, folio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                estatus = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return estatus;
    }

    private static String capituloPago(Connection conn, String folio) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        String capitulo = "";
        ResultSet rs = null;
        String query = "SELECT TOP 1 SUBSTRING(EP,32,1) FROM tPAGODIVERSOEncabezado pdiv with(nolock) INNER JOIN tPAGODIVERSODetalle pdet with(nolock) ON pdiv.nFolioPAGODIVERSO = pdet.nFolioPAGODIVERSO WHERE pdet.nFolioPAGODIVERSO = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, folio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                capitulo = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return capitulo;
    }

    private static String obtenerRutaFortimax(Connection conn, String nFolio, String recepcion) throws Exception {
        PreparedStatement pstmnt = null;
        String ruta = "";
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        try {
            int idGabinete = obtenerGabinete(conn, nFolio);
            query.append("SELECT TITULO_APLICACION + '_G' + CAST(id_gabinete as varchar(6)) + 'C' + CAST(ID_CARPETA_PADRE as varchar(6)) + 'D' +CAST(ID_DOCUMENTO as varchar(6)) rutaFortimax  ");
            query.append(" FROM IMX_DOCUMENTO WITH (NOLOCK) WHERE TITULO_APLICACION = 'CONTRATODIVERSO' and ID_GABINETE = ? and NOMBRE_DOCUMENTO like '%' + ? + '%' ");
            pstmnt = conn.prepareStatement(query.toString());
            pstmnt.setInt(1, idGabinete);
            pstmnt.setString(2, recepcion);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                ruta = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return ruta;
    }

    private static int obtenerGabinete(Connection conn, String nFolio) throws Exception {
        PreparedStatement pstmnt = null;
        int gabinete = 0;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append(" SELECT contrato.ID_GABINETE FROM v_atentanota nota WITH (NOLOCK) INNER JOIN pContratoDiverso div WITH (NOLOCK)  ON nota.cidContratoDefinitivo = div.cIdContrato ");
            query.append(" INNER JOIN IMXCONTRATODIVERSO contrato WITH (NOLOCK)  on  'CDIV-'+ cIdUnidadAdministrativa + '-' + CAST(div.id_caso AS varchar(4)) = contrato.FOLIO ");
            query.append(" where  nfoliopagodiverso = ?");
            pstmnt = conn.prepareStatement(query.toString());
            pstmnt.setString(1, nFolio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                gabinete = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return gabinete;
    }

    private static int cambiaEstatusRecepcion(Connection conn, int nStatusRecepcion, int nFolioPagoDiverso) throws Exception {
        String query = "UPDATE	mRecepcionpMat " + "   SET	nIdEstadoRecepMat = ? " + " WHERE nIdEstadoRecepMat = 3 " + "   AND	cIdRecepMat = (SELECT ISNULL(cIdRecepMat,'') FROM tPAGODIVERSOEncabezado WHERE nFolioPAGODIVERSO = ?) " + "	AND cIdpedContDef = (SELECT cFolioPAGODIVERSO FROM tPAGODIVERSOEncabezado WHERE nFolioPAGODIVERSO = ? )";
        PreparedStatement pstmnEnc = null;
        int afectados = 0;
        try {
            pstmnEnc = conn.prepareStatement(query);
            pstmnEnc.setInt(1, nStatusRecepcion);
            pstmnEnc.setInt(2, nFolioPagoDiverso);
            pstmnEnc.setInt(3, nFolioPagoDiverso);
            afectados = pstmnEnc.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(pstmnEnc);
        }
    }

    private static int guardaIdTransactionWS(Connection conn, long idTransactionWS, int nFolioPagoDiverso, int nFolioPoliza) throws Exception {
        String query = " update tPAGODIVERSOEncabezado set nIdTransactionWS=? where nFolioPAGODIVERSO=? and nFolioPoliza=? ";
        PreparedStatement pstmnEnc = null;
        int resp = -1;
        try {
            pstmnEnc = conn.prepareStatement(query);
            pstmnEnc.setLong(1, idTransactionWS);
            pstmnEnc.setInt(2, nFolioPagoDiverso);
            pstmnEnc.setInt(3, nFolioPoliza);
            resp = pstmnEnc.executeUpdate();
            return resp;
        } finally {
            CloseObject.closeObject(pstmnEnc);
        }
    }

    private static int validaEstatusWS(Connection conn) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        // WS inactivo
        int resp = -2;
        ResultSet rs = null;
        try {
            String query = "select * from CG_GRUPO_PROPIEDADES with(Nolock) where G_NOMBRE='WEBSERVICE_INVENTARIO' and GP_NOMBRE='Activa_ws_inventario' and GP_VALOR='TRUE'";
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                resp = 0;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return resp;
    }

    private static String urlWS(Connection conn, int oper) throws SQLException, JSONException {
        PreparedStatement pstmnt = null;
        String url = "";
        String GP_NOMBRE = "";
        ResultSet rs = null;
        try {
            switch(oper) {
                case 0:
                    GP_NOMBRE = "Ruta_ws_inventario_inputAcquisition";
                    break;
                case 1:
                    GP_NOMBRE = "Ruta_ws_inventario_payAcquisition";
                    break;
                case 2:
                    GP_NOMBRE = "Ruta_ws_inventario_cancelAcquisition";
                    break;
                case 3:
                    GP_NOMBRE = "Ruta_ws_inventario_nueva_obra";
                    break;
                case 4:
                    GP_NOMBRE = "Ruta_ws_inventario_obra_avance_fisico";
                    break;
                case 5:
                    GP_NOMBRE = "Ruta_ws_inventario_obra_capitalizacion";
                    break;
            }
            String query = "select *from CG_GRUPO_PROPIEDADES with(Nolock) where G_NOMBRE='WEBSERVICE_INVENTARIO' and GP_NOMBRE=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, GP_NOMBRE);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                url = rs.getString("GP_VALOR");
            }
            System.out.println("URL: " + url);
            log.info("URL: " + url);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return url;
    }

    public static RespuestaWS generatePublicWork(Connection conn, String nFolio, Usuario u) throws Exception {
        RespuestaWS respuestaWS = null;
        String query = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONObject jsonObj = null;
        JSONObject jsonrespWS = null;
        try {
            respuestaWS = new RespuestaWS();
            // la operacion 3 es la url ws de nueva obra
            int oper = 3;
            // "http://10.0.0.34:8080/inventory-ws-app/services/inventory/transaction/inputAcquisition";
            String url = urlWS(conn, oper);
            String metodo = "POST", tipoRespuesta = "application/json;charset=UTF-8";
            String respWS = "";
            // Validar si el ws está encendido
            //validaEstatusWS(conn);
            int WsActivo = 0;
            if (WsActivo != 0) {
                log.info("El Web service se encuentra configurado para que no se ejecute.");
                respuestaWS.setCode(-2);
                respuestaWS.setEstatus("El Web service se encuentra configurado para que no se ejecute.");
                return respuestaWS;
            }
            query = "select nFolioOPComHeader, isnull(nIdRealEstate,0) idRealEstate,cDescripcion,convert(date,fFechaIniContr)fFechaIniContr,convert(date,fFechaFinContr)fFechaFinContr,cCveContrato,nMontoConIVA,FolioSAI from tObraPublicaCompromisoEncabezado with(Nolock) where nFolioOPComHeader= " + nFolio;
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            jsonObj = new JSONObject();
            if (rs.next()) {
                jsonObj.put("idPublicWorkSAI", rs.getInt(1));
                jsonObj.put("idRealEstate", rs.getInt(2));
                jsonObj.put("description", rs.getString(3));
                jsonObj.put("plannedStartDate", rs.getString(4));
                jsonObj.put("plannedEndDate", rs.getString(5));
                jsonObj.put("contractNo", rs.getString(6));
                jsonObj.put("totalAmount", rs.getDouble(7));
                // Connection al ws
                GenericConnectionWS bienesExt = new GenericConnectionWS();
                respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
                jsonrespWS = new JSONObject(respWS);
                respuestaWS.setCode((null == jsonrespWS.get("code") || "".equalsIgnoreCase(jsonrespWS.getString("code"))) ? -1 : jsonrespWS.getInt("code"));
                respuestaWS.setEstatus(jsonrespWS.getString("estatus"));
                respuestaWS.setIdRePublicWork((null == jsonrespWS.get("idRePublicWork") || "".equalsIgnoreCase(jsonrespWS.getString("idRePublicWork"))) ? -1 : jsonrespWS.getInt("idRePublicWork"));
                //Guardar en bitacora.
                query = "insert into tBitacoraGeneratePublickWork (nIdPublicWorkSAI,nIdRealEstate,cNumContrato,cUrlWS,fFechaCaptura,nCode,cEstatus,nIdPublicWork,cLogin) " + " values(" + rs.getInt(1) + "," + rs.getInt(2) + ",'" + rs.getString(8) + "','" + url + "',GETDATE()," + jsonrespWS.getInt("code") + ",'" + jsonrespWS.getString("estatus") + "'," + jsonrespWS.getInt("idRePublicWork") + ",'" + u.getLogin() + "')";
                com.syc.adquisiciones.util.Util.updateQuery(query, conn);
                log.info("Respuesta Web Service, estatus: " + jsonrespWS.getString("estatus"));
            } else {
                respuestaWS.setCode(-1);
                respuestaWS.setEstatus("Error");
                respuestaWS.setIdRePublicWork(-1);
                throw new Exception("No se encontraron datos en la tabla tObraPublicaCompromisoEncabezado con el folio " + nFolio);
            }
            return respuestaWS;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static RespuestaWS generatePublicWorkPartial(Connection conn, String folioSAI, int numEstimacion, boolean esCapitalizable, boolean ultimaEstimacion, Usuario u) throws Exception {
        RespuestaWS respuestaWS = null;
        String query = "SELECT	compromisoObra.nidRePublicWork AS idRePublicWork," + "		compromisoObra.nFolioOPComHeader AS idPublicWorkSAI, " + "		compromisoObra.nIDRealEstate AS idRealEstate," + "		estimacionObra.noestimacion AS estimationNo," + "		 round(estimacionObra.mMontoEstimacionMasIva+estimacionObra.mMontoEstimacionRetencion,2) AS amount" + "  FROM	tObraPublicaCompromisoEncabezado compromisoObra WITH( nolock )" + "		INNER JOIN" + "		tobrapublicaavancefisico estimacionObra WITH(NOLOCK)" + "		ON compromisoObra.FolioSAI = estimacionObra.foliosai" + " WHERE	compromisoObra.FolioSAI = ? " + "   AND estimacionObra.noestimacion = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONObject jsonObj = null;
        JSONObject jsonrespWS = null;
        try {
            respuestaWS = new RespuestaWS();
            // la operacion 4 es la url ws de nueva estimacion.
            int oper = 4;
            String url = urlWS(conn, oper);
            String metodo = "POST", tipoRespuesta = "application/json;charset=UTF-8";
            String respWS = "";
            // Validar si el ws está encendido
            boolean activaWS = "true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "WS_OBRAPUBLICA", "Activa_WS_ObraPublica"));
            if (!activaWS) {
                log.info("El Web service se encuentra configurado para que no se ejecute.");
                respuestaWS.setCode(-2);
                respuestaWS.setEstatus("El Web service se encuentra configurado para que no se ejecute.");
                return respuestaWS;
            }
            log.info(query);
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            ps.setString(2, String.valueOf(numEstimacion));
            rs = ps.executeQuery();
            if (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("idRePublicWork", rs.getString("idRePublicWork"));
                jsonObj.put("idPublicWorkSAI", rs.getInt("idPublicWorkSAI"));
                jsonObj.put("idRealEstate", rs.getInt("idRealEstate"));
                jsonObj.put("estimationNo", rs.getInt("estimationNo"));
                jsonObj.put("amount", rs.getDouble("amount"));
                jsonObj.put("finalize", new Boolean(ultimaEstimacion));
                jsonObj.put("capitalizable", new Boolean(esCapitalizable));
                GenericConnectionWS bienesExt = new GenericConnectionWS();
                respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
                jsonrespWS = new JSONObject(respWS);
                respuestaWS.setCode(jsonrespWS.getInt("code"));
                respuestaWS.setEstatus(jsonrespWS.getString("estatus"));
                respuestaWS.setIdRePublicWorkPartial(jsonrespWS.getInt("idRePublicWork"));
                //Guardar en bitacora.
                query = "insert into tBitacoraGeneratePublickWork (nIdPublicWorkSAI,nIdRealEstate,cNumContrato,cUrlWS,fFechaCaptura,nCode,cEstatus,nIdPublicWork,nIdPublicWorkPartial,cLogin) " + " values(" + rs.getInt(1) + "," + rs.getInt(2) + ",'" + folioSAI + "','" + url + "',GETDATE()," + jsonrespWS.getInt("code") + ",'" + jsonrespWS.getString("estatus") + "'," + rs.getString("idRePublicWork") + "," + jsonrespWS.getInt("idRePublicWork") + ",'" + u.getLogin() + "')";
                com.syc.adquisiciones.util.Util.updateQuery(query, conn);
                log.info("Respuesta Web Service: " + respuestaWS);
            } else {
                throw new Exception("No se encontraron datos en la tabla tObraPublicaCompromisoEncabezado con el folio " + folioSAI + " y la estimacion numero " + numEstimacion);
            }
            return respuestaWS;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static RespuestaWS generatePublicWorkPartial(Connection conn, String caNoContrarrecibo, String folioSAI) throws Exception {
        RespuestaWS respuestaWS = null;
        StringBuffer query = new StringBuffer();
        query.append("	SELECT compromisoObra.nidRePublicWork AS idRePublicWork,");
        query.append("		compromisoObra.nFolioOPComHeader AS idPublicWorkSAI, ");
        query.append("		compromisoObra.nIDRealEstate AS idRealEstate,");
        query.append("		estimacionObra.noestimacion AS estimationNo,");
        query.append("		ROUND(estimacionObra.mMontoEstimacionMasIva+estimacionObra.mMontoEstimacionRetencion,2) AS amount,");
        query.append(" 	ultimaEstimacion,");
        query.append(" 	esCapitalizable");
        query.append("	FROM	tObraPublicaCompromisoEncabezado compromisoObra WITH (NOLOCK)");
        query.append("	INNER JOIN tobrapublicaavancefisico estimacionObra WITH (NOLOCK)");
        query.append("		ON compromisoObra.FolioSAI = estimacionObra.foliosai");
        query.append(" JOIN tPAGOOBRAEncabezado ENC WITH (NOLOCK)");
        query.append(" 	ON noestimacion = cNoEstimacion");
        query.append(" WHERE	compromisoObra.FolioSAI = ?");
        query.append("   AND 	caNoContrarrecibo = ?");
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null;
        JSONObject jsonObj = null;
        JSONObject jsonrespWS = null;
        JSONObject jsonCapitalizacion = null;
        try {
            respuestaWS = new RespuestaWS();
            // la operacion 4 es la url ws de nueva estimacion.
            int oper = 4;
            String url = urlWS(conn, oper);
            String metodo = "POST", tipoRespuesta = "application/json;charset=UTF-8";
            String respWS = "";
            // Validar si el ws está encendido
            boolean activaWS = "true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "WS_OBRAPUBLICA", "Activa_WS_ObraPublica"));
            if (!activaWS) {
                log.info("El Web service se encuentra configurado para que no se ejecute.");
                respuestaWS.setCode(-2);
                respuestaWS.setEstatus("El Web service se encuentra configurado para que no se ejecute.");
                return respuestaWS;
            }
            String sql = query.toString();
            log.info(sql);
            ps = conn.prepareStatement(sql);
            ps.setString(1, folioSAI);
            ps.setString(2, caNoContrarrecibo);
            rs = ps.executeQuery();
            if (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("idRePublicWork", rs.getString("idRePublicWork"));
                jsonObj.put("idPublicWorkSAI", rs.getInt("idPublicWorkSAI"));
                jsonObj.put("idRealEstate", rs.getInt("idRealEstate"));
                jsonObj.put("estimationNo", rs.getInt("estimationNo"));
                jsonObj.put("amount", rs.getDouble("amount"));
                jsonObj.put("finalize", rs.getInt("ultimaEstimacion") == 1 ? true : false);
                log.info(jsonObj);
                GenericConnectionWS bienesExt = new GenericConnectionWS();
                respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonObj);
                jsonrespWS = new JSONObject(respWS);
                respuestaWS.setCode(jsonrespWS.getInt("code"));
                respuestaWS.setEstatus(jsonrespWS.getString("estatus"));
                respuestaWS.setIdRePublicWorkPartial(jsonrespWS.getInt("idRePublicWork"));
                respuestaWS.setRegimen(jsonrespWS.getString("regimen"));
                respuestaWS.setLastAppraisalDate(jsonrespWS.getString("lastAppraisalDate"));
                //Guardar en bitacora.
                String queryI = "insert into tBitacoraGeneratePublickWork (nIdPublicWorkSAI,nIdRealEstate,cNumContrato,cUrlWS,fFechaCaptura,nCode,cEstatus,nIdPublicWork,nIdPublicWorkPartial,cLogin) " + " values(" + rs.getInt(1) + "," + rs.getInt(2) + ",'" + folioSAI + "','" + url + "',GETDATE()," + jsonrespWS.getInt("code") + ",'" + jsonrespWS.getString("estatus") + "'," + rs.getString("idRePublicWork") + "," + jsonrespWS.getInt("idRePublicWork") + ",'ADMIN')";
                com.syc.adquisiciones.util.Util.updateQuery(queryI, conn);
                if (respuestaWS.getCode() >= 0) {
                    if (WSManager.EXITO.equalsIgnoreCase(respuestaWS.getEstatus()) || WSManager.EXISTE.equalsIgnoreCase(respuestaWS.getEstatus())) {
                        //Actualiza idRePublicWorkPartial en el avance fisico
                        ObraPublicaManager.actualizaEstimacionInfoInmueble(conn, folioSAI, rs.getInt("estimationNo"), respuestaWS.getIdRePublicWorkPartial());
                        //Calcula Evento para la capitalizacion o el registro al gasto.
                        if (rs.getInt("ultimaEstimacion") == 1) {
                            int esCapitalizable = rs.getInt("esCapitalizable");
                            if (esCapitalizable == 1 && "otros".equals(respuestaWS.getRegimen())) {
                                //No aplica la capitalizacion para inmuebles de otro regimen que no sean propiedad o tramte, se cambio a no capitalizable
                                esCapitalizable = 0;
                                String queryUP = "UPDATE tobrapublicaavancefisico SET esCapitalizable = ? WHERE foliosai = ? AND ultimaEstimacion = 1";
                                ps2 = conn.prepareStatement(queryUP);
                                ps2.setInt(1, esCapitalizable);
                                ps2.setString(2, folioSAI);
                                ps2.executeUpdate();
                            }
                            String evento = ObraPublicaManager.calculaEvento(conn, folioSAI, respuestaWS.getRegimen(), respuestaWS.getLastAppraisalDate(), caNoContrarrecibo, esCapitalizable);
                            if (!"".equals(evento)) {
                                //Si no se obtiene el evento es porque ya existe un avaluo antes del pagado
                                jsonCapitalizacion = ObraPublicaManager.generaPoliza(conn, folioSAI, evento, caNoContrarrecibo, esCapitalizable);
                                // la operacion 5 es la url ws de la poliza de capitalizacion.
                                oper = 5;
                                url = urlWS(conn, oper);
                                respWS = bienesExt.connectionWebService(url, metodo, tipoRespuesta, jsonCapitalizacion);
                                jsonrespWS = new JSONObject(respWS);
                                respuestaWS.setCode(jsonrespWS.getInt("code"));
                                respuestaWS.setEstatus(jsonrespWS.getString("estatus"));
                                if (respuestaWS.getCode() == 2 || WSManager.ERROR.equalsIgnoreCase(respuestaWS.getEstatus())) {
                                    throw new Exception("El llamado al sistema de inmuebles esta activo pero no se guardo informacion");
                                }
                            } else {
                                throw new Exception("No se pudo calcular el evento.");
                            }
                        }
                    } else if (WSManager.ERROR.equalsIgnoreCase(respuestaWS.getEstatus())) {
                        throw new Exception("El llamado al sistema de inmuebles esta activo y regreso estatus de error. No se guardo informacion");
                    } else if (WSManager.NOTEXIST.equalsIgnoreCase(respuestaWS.getEstatus())) {
                        throw new Exception("El llamado al sistema de inmuebles esta activo pero no existe registro de la obra publica. No se guardo informacion");
                    }
                }
                log.info("Respuesta Web Service: " + respuestaWS);
            } else {
                throw new Exception("No se encontraron datos en la tabla tObraPublicaCompromisoEncabezado con el folio " + folioSAI + " y la estimacion numero " + rs.getInt("estimationNo"));
            }
            return respuestaWS;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
