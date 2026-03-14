package com.axtel.contratos.penalties.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.penalties.PenaltiesInterface;
import com.axtel.contratos.penalties.businessLogic.PenaltiesImplements;
import com.axtel.contratos.penalties.core.DeductionItems;
import com.axtel.contratos.penalties.core.PenaltyAndDeduction;
import com.axtel.contratos.penalties.core.PenaltyItems;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "PenaltiesServlet", urlPatterns = { "/servlet/PenaltiesServlet" })
public class PenaltiesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PenaltiesServlet.class);

    public PenaltiesServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario u = null;
        HttpSession session = null;
        PenaltiesInterface penaltyInterface = null;
        PenaltyAndDeduction penaltiDeduction = null;
        PrintWriter out = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        ArrayList<PenaltyItems> arrayPenaltyItems = null;
        ArrayList<DeductionItems> arrayItemsDeduction = null;
        Respuesta resp = null;
        try {
            session = request.getSession(false);
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            int tipoOperacion = (null == request.getParameter("operation") || "".equals(request.getParameter("operation"))) ? 0 : Integer.parseInt(request.getParameter("operation"));
            penaltyInterface = new PenaltiesImplements();
            out = response.getWriter();
            arrayObj = new JSONArray();
            penaltiDeduction = fillObjectData(request, u);
            switch(tipoOperacion) {
                case 0:
                    log.warn("No se recibió el tipo de operación");
                    break;
                case //query
                1:
                    jsonObj = penaltyInterface.queryPenalties(penaltiDeduction);
                    jsonObj.put("MENSAJE", "Consulta de datos");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //save
                2:
                    if (penaltiDeduction.getlDeduction() == 1) {
                        arrayItemsDeduction = setArrayItemsDeduction(request, penaltiDeduction.getcTipoContrato());
                        penaltiDeduction.setDeductionItems(arrayItemsDeduction);
                    }
                    if (penaltiDeduction.getlPenalty() == 1) {
                        arrayPenaltyItems = setArrayItemsPenalty(request, penaltiDeduction.getcTipoContrato());
                        penaltiDeduction.setPenaltyItems(arrayPenaltyItems);
                    }
                    penaltyInterface.savePenalties(penaltiDeduction);
                    jsonObj = penaltyInterface.queryPenalties(penaltiDeduction);
                    resp = penaltyInterface.areThereDocuments(penaltiDeduction);
                    jsonObj.put("MENSAJE", "Datos Guardados." + (resp.getMsg() == null ? "" : "\n" + resp.getMsg()));
                    jsonObj.put("RESPUESTA_DOC", resp.isResp());
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //delete
                3:
                    penaltyInterface.deletePenalties(penaltiDeduction);
                    jsonObj = penaltyInterface.queryPenalties(penaltiDeduction);
                    ;
                    jsonObj.put("MENSAJE", "Datos eliminados");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //send
                4:
                    penaltyInterface.sendPenalty(penaltiDeduction);
                    penaltyInterface.sendEmail(penaltiDeduction);
                    break;
                case //validate
                5:
                    penaltyInterface.validatePenalty(penaltiDeduction);
                    jsonObj = new JSONObject();
                    jsonObj.put("MENSAJE", "Datos guardados");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //Add row penalty item
                6:
                    jsonObj = new JSONObject();
                    if (penaltiDeduction.getlPenalty() == 1 && penaltiDeduction.getnIdPenaltyDeduction() > 0) {
                        arrayPenaltyItems = setArrayItemsPenalty(request, penaltiDeduction.getcTipoContrato());
                        penaltiDeduction.setPenaltyItems(arrayPenaltyItems);
                        penaltyInterface.addRowPenaltyItem(penaltiDeduction);
                        jsonObj.put("MENSAJE", "Registro agregado, favor de capturar los montos");
                        jsonObj.put("RESPUESTA", true);
                    } else {
                        jsonObj.put("MENSAJE", "Favor de primero capturar los montos de los registros que ya hay agregados y luego agregar los que se necesiten.");
                        jsonObj.put("RESPUESTA", false);
                    }
                    break;
                case //Add row deduction item
                7:
                    jsonObj = new JSONObject();
                    if (penaltiDeduction.getlDeduction() == 1 && penaltiDeduction.getnIdPenaltyDeduction() > 0) {
                        arrayItemsDeduction = setArrayItemsDeduction(request, penaltiDeduction.getcTipoContrato());
                        penaltiDeduction.setDeductionItems(arrayItemsDeduction);
                        penaltyInterface.addRowDeductionItem(penaltiDeduction);
                        jsonObj.put("MENSAJE", "Registro agregado, favor de capturar los montos");
                        jsonObj.put("RESPUESTA", true);
                    } else {
                        jsonObj.put("MENSAJE", "Registro agregado");
                        jsonObj.put("RESPUESTA", false);
                    }
                    break;
                default:
                    log.warn("Operación desconocida");
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jsonObj = new JSONObject();
            try {
                jsonObj.put("MENSAJE", e.getMessage());
                jsonObj.put("RESPUESTA", false);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } finally {
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            if (penaltiDeduction.getPenaltyItems() != null && !penaltiDeduction.getPenaltyItems().isEmpty()) {
                penaltiDeduction.getPenaltyItems().clear();
            }
            if (penaltiDeduction.getDeductionItems() != null && !penaltiDeduction.getDeductionItems().isEmpty()) {
                penaltiDeduction.getDeductionItems().clear();
            }
            penaltyInterface = null;
            u = null;
            session = null;
            penaltiDeduction = null;
            out = null;
            jsonObj = null;
            arrayObj = null;
            arrayPenaltyItems = null;
            arrayItemsDeduction = null;
        }
    }

    private PenaltyAndDeduction fillObjectData(HttpServletRequest request, Usuario u) throws Exception {
        PenaltyAndDeduction penaltiDeduction = new PenaltyAndDeduction();
        penaltiDeduction.setnIdPenaltyDeduction((null == request.getParameter("nIdPenaltyDeduction") || "".equals(request.getParameter("nIdPenaltyDeduction"))) ? -1 : Integer.parseInt(request.getParameter("nIdPenaltyDeduction")));
        penaltiDeduction.setcFolio(null == (request.getParameter("cFolio")) ? "" : request.getParameter("cFolio"));
        penaltiDeduction.setcCaptureUsser(u.getLogin());
        penaltiDeduction.setcIdContratoDefinitivo(null == (request.getParameter("cIdContratoDefinitivo")) ? "" : request.getParameter("cIdContratoDefinitivo"));
        penaltiDeduction.setcTipoContrato(null == (request.getParameter("cTipoContrato")) ? "" : request.getParameter("cTipoContrato"));
        penaltiDeduction.setlPenalty((null == request.getParameter("lPenalty") || "".equals(request.getParameter("lPenalty"))) ? 0 : Integer.parseInt(request.getParameter("lPenalty")));
        penaltiDeduction.setlDeduction((null == request.getParameter("lDeduction") || "".equals(request.getParameter("lDeduction"))) ? -1 : Integer.parseInt(request.getParameter("lDeduction")));
        penaltiDeduction.setnIdEstate((null == request.getParameter("nIdEstate") || "".equals(request.getParameter("nIdEstate"))) ? 1 : Integer.parseInt(request.getParameter("nIdEstate")));
        penaltiDeduction.setcDocumentHAplicado(null == (request.getParameter("cDocumentHAplicado")) ? "I" : request.getParameter("cDocumentHAplicado"));
        penaltiDeduction.setcObservations(null == (request.getParameter("cObservations")) ? "" : request.getParameter("cObservations"));
        penaltiDeduction.setcValidatingUser(u.getLogin());
        penaltiDeduction.setcConcepto(null == (request.getParameter("cConcepto")) ? "" : request.getParameter("cConcepto"));
        penaltiDeduction.setnPeriodo((null == request.getParameter("nPeriodo") || "".equals(request.getParameter("nPeriodo"))) ? 0 : Integer.parseInt(request.getParameter("nPeriodo")));
        penaltiDeduction.setcOficio(null == (request.getParameter("cOficio")) ? "" : request.getParameter("cOficio"));
        penaltiDeduction.setcNumContratoCNET(null == (request.getParameter("cNumCNET")) ? "" : request.getParameter("cNumCNET"));
        penaltiDeduction.setcProveedor(null == (request.getParameter("cProveedor")) ? "" : request.getParameter("cProveedor"));
        penaltiDeduction.setnIdOper((null == request.getParameter("nIdEstate") || "".equals(request.getParameter("nIdEstate"))) ? 1 : Integer.parseInt(request.getParameter("nIdEstate")));
        return penaltiDeduction;
    }

    private ArrayList<PenaltyItems> setArrayItemsPenalty(HttpServletRequest request, String cTipoContrato) throws Exception {
        ArrayList<PenaltyItems> arrayPartida = new ArrayList<PenaltyItems>();
        PenaltyItems items = null;
        log.debug("Armando el array de partidas");
        String cadenaPartidas = request.getParameter("penaltyItems");
        log.info("Object: {}", "Cadena : " + cadenaPartidas);
        String[] arrayTupla = cadenaPartidas.split(",");
        String[] arrayValores = null;
        for (int i = 0; i < arrayTupla.length; i++) {
            items = new PenaltyItems();
            arrayValores = arrayTupla[i].split("-");
            if ("CV".equalsIgnoreCase(cTipoContrato)) {
                items.setnIdItemContract(Integer.parseInt(arrayValores[0]));
                items.setnConsecutiveItem(Integer.parseInt(arrayValores[1]));
                items.setnPiecesElements(Integer.parseInt(arrayValores[2]));
                items.setmDelayAmount(Double.parseDouble(arrayValores[3]));
                items.setnDailyPenaltyPercentage(Float.parseFloat(arrayValores[4]));
                items.setmAmountDailyPenalty(Double.parseDouble(arrayValores[5]));
                items.setnDailyDays(Integer.parseInt(arrayValores[6]));
                items.setmTotalAmountPenalty(Double.parseDouble(arrayValores[7]));
                items.setnComplianceGuaranteePercentage(Float.parseFloat(arrayValores[8]));
                items.setnPenaltyDays(Float.parseFloat(arrayValores[9]));
                items.setmAmountPenalty(Double.parseDouble(arrayValores[10]));
            } else {
                items.setnIdItemContract(Integer.parseInt(arrayValores[0]));
                items.setnConsecutiveItem(Integer.parseInt(arrayValores[1]));
                items.setfItemDeliveryDate(arrayValores[2]);
                items.setnPiecesElements(Integer.parseInt(arrayValores[3]));
                items.setmItemAmount(Double.parseDouble(arrayValores[4]));
                items.setmDelayAmount(Double.parseDouble(arrayValores[5]));
                items.setnDailyPenaltyPercentage(Float.parseFloat(arrayValores[6]));
                items.setmAmountDailyPenalty(Double.parseDouble(arrayValores[7]));
                items.setnDailyDays(Integer.parseInt(arrayValores[8]));
                items.setmTotalAmountPenalty(Double.parseDouble(arrayValores[9]));
                items.setnComplianceGuaranteePercentage(Float.parseFloat(arrayValores[10]));
                items.setnPenaltyDays(Float.parseFloat(arrayValores[11]));
                items.setmAmountPenalty(Double.parseDouble(arrayValores[12]));
            }
            arrayPartida.add(items);
            arrayValores = null;
            items = null;
        }
        return arrayPartida;
    }

    private ArrayList<DeductionItems> setArrayItemsDeduction(HttpServletRequest request, String cTipoContrato) throws Exception {
        ArrayList<DeductionItems> arrayPartida = new ArrayList<DeductionItems>();
        DeductionItems items = null;
        log.debug("Armando el array de partidas");
        String cadenaPartidas = request.getParameter("deductionItems");
        log.info("Object: {}", "Cadena : " + cadenaPartidas);
        String[] arrayTupla = cadenaPartidas.split(",");
        String[] arrayValores = null;
        for (int i = 0; i < arrayTupla.length; i++) {
            items = new DeductionItems();
            arrayValores = arrayTupla[i].split("-");
            if ("CV".equalsIgnoreCase(cTipoContrato)) {
                items.setnIdItemContract(Integer.parseInt(arrayValores[0]));
                items.setnConsecutiveItem(Integer.parseInt(arrayValores[1]));
                items.setnPiecesElements(Integer.parseInt(arrayValores[2]));
                items.setmDelayAmount(Double.parseDouble(arrayValores[3]));
                items.setnDailyPenaltyPercentage(Float.parseFloat(arrayValores[4]));
                items.setmAmountDailyDeduction(Double.parseDouble(arrayValores[5]));
                items.setnDailyDays(Integer.parseInt(arrayValores[6]));
                items.setmTotalAmountDeduction(Double.parseDouble(arrayValores[7]));
                items.setnComplianceGuaranteePercentage(Float.parseFloat(arrayValores[8]));
                items.setnPenaltyDays(Float.parseFloat(arrayValores[9]));
                items.setmAmountDeduction(Double.parseDouble(arrayValores[10]));
            } else {
                items.setnIdItemContract(Integer.parseInt(arrayValores[0]));
                items.setnConsecutiveItem(Integer.parseInt(arrayValores[1]));
                items.setfItemDeliveryDate(arrayValores[2]);
                items.setnPiecesElements(Integer.parseInt(arrayValores[3]));
                items.setmItemAmount(Double.parseDouble(arrayValores[4]));
                items.setmDelayAmount(Double.parseDouble(arrayValores[5]));
                items.setnDailyPenaltyPercentage(Float.parseFloat(arrayValores[6]));
                items.setmAmountDailyDeduction(Double.parseDouble(arrayValores[7]));
                items.setnDailyDays(Integer.parseInt(arrayValores[8]));
                items.setmTotalAmountDeduction(Double.parseDouble(arrayValores[9]));
                items.setnComplianceGuaranteePercentage(Float.parseFloat(arrayValores[10]));
                items.setnPenaltyDays(Float.parseFloat(arrayValores[11]));
                items.setmAmountDeduction(Double.parseDouble(arrayValores[12]));
            }
            arrayPartida.add(items);
            arrayValores = null;
            items = null;
        }
        return arrayPartida;
    }
}
