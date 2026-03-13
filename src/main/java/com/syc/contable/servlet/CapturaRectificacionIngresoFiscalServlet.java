package com.syc.contable.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.contable.core.RectificacionIngresoFiscalDetalle;
import com.syc.contable.core.RectificacionIngresoFiscalEncabezado;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CapturaRectificacionIngresoFiscal", urlPatterns = { "/gstnmngr/CapturaRectificacionIngresoFiscalServlet" })
public class CapturaRectificacionIngresoFiscalServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(CapturaRectificacionIngresoFiscalServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        RectificacionIngresoFiscalBusinessLogic rectIFiscalBL = new RectificacionIngresoFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        boolean regresaSinError = true;
        String remanentes = "";
        try {
            String strTotalDice = (request.getParameter("totalDiceValor") == null) ? "0.00" : request.getParameter("totalDiceValor").trim();
            String strTotalDebeDecir = (request.getParameter("totalDebeDecir") == null) ? "0.00" : request.getParameter("totalDebeDecir").trim();
            double dblTotalDice = Double.parseDouble(strTotalDice);
            double dblTotalDebeDecir = Double.parseDouble(strTotalDebeDecir);
            if (com.syc.contable.util.Math.truncate(dblTotalDice, 2) == com.syc.contable.util.Math.truncate(dblTotalDebeDecir, 2)) {
                String cEjercicio = request.getParameter("cEjercicio_C");
                String cRamo = request.getParameter("cRamo_C");
                String cUnidad = request.getParameter("cUnidad_C");
                String u_login = request.getParameter("u_login_C");
                String folio = request.getParameter("folio");
                int folioR = new Integer(folio.substring(folio.lastIndexOf('-') + 1)).intValue();
                String id_caso = request.getParameter("ID_Caso_C");
                java.sql.Date fExp;
                java.sql.Date fApl;
                try {
                    fExp = java.sql.Date.valueOf(request.getParameter("fExp"));
                    fApl = java.sql.Date.valueOf(request.getParameter("fApl"));
                } catch (IllegalArgumentException e) {
                    fExp = java.sql.Date.valueOf(today);
                    fApl = java.sql.Date.valueOf(today);
                }
                String nMes = request.getParameter("cMes_C");
                String cTipoMovto = request.getParameter("CatMovimientoRectificacion");
                String nOrigenPPTO = request.getParameter("origPresupuesto");
                String conceptoRectificacion = request.getParameter("concepto");
                String oficioRectif = request.getParameter("oficioRectif");
                String ctr_int = request.getParameter("ctr_int");
                String cCentroContable = request.getParameter("cCentroContable_C");
                String cTipoRectificacion = request.getParameter("dTipoPago");
                String nFolioSICOP = (request.getParameter("nFolioSICOP") == null) ? "" : request.getParameter("nFolioSICOP").trim();
                String caNoContrarrecibo = request.getParameter("caNoContrarrecibo");
                String nFolioPoliza = request.getParameter("nFolioPoliza");
                String cTipoPoliza = "DI";
                String cDescripcionPoliza = request.getParameter("cDescripcionPoliza");
                String cUnidadResponsableContable = request.getParameter("cUnidadResponsableContable");
                String nFolioSIAFF = request.getParameter("nFolioSIAFF");
                String totalDice = request.getParameter("totalDice");
                String totalDebe = request.getParameter("totalDebeDecir");
                String[] detalle = request.getParameter("info").split("!");
                ArrayList<RectificacionIngresoFiscalDetalle> rectificaciones = new ArrayList<RectificacionIngresoFiscalDetalle>();
                String evento = "";
                for (int i = 0; i < detalle.length; i += 6) {
                    RectificacionIngresoFiscalDetalle rd = new RectificacionIngresoFiscalDetalle();
                    rd.setnFolioRectificaIngreso(folioR);
                    rd.setnDocRenglon(Integer.parseInt(detalle[i].trim()));
                    rd.setcMes(Integer.parseInt(detalle[i + 1].trim()));
                    if (detalle[i + 3].trim().equals("DICE")) {
                        evento = rectIFiscalBL.getEventoRectificaIngresoFiscal(detalle[i + 2].trim(), "DICE");
                        rd.setcEvento(evento);
                    } else {
                        evento = rectIFiscalBL.getEventoRectificaIngresoFiscal(detalle[i + 2].trim(), "DEBE");
                        rd.setcEvento(evento);
                    }
                    rd.setEP(detalle[i + 2].trim());
                    rd.setmImporte(Double.parseDouble(detalle[i + 4].trim()));
                    rd.setmImporteNegativo(Double.parseDouble(detalle[i + 4].trim()) * -1);
                    rd.setcCentroContable(cCentroContable);
                    rd.setcaNoContrarrecibo(detalle[i + 5].trim());
                    if (rd.getcEvento().contains("DEBE")) {
                        String partida = rd.getEP().substring(31, 32);
                        if ("1".equals(partida)) {
                            remanentes += "No se puede hacer una rectificacion tomando partidas del capitulo mill";
                        }
                    }
                    if (rd.getcEvento().contains("DICE")) {
                        boolean banderaRemanente = false;
                        double remanente = 0;
                        int secuenciaCLC = rectIFiscalBL.secCLCRect(nFolioSIAFF, rd.getEP());
                        int[] docRenglon = rectIFiscalBL.getNDocRenglon(caNoContrarrecibo, rd.getEP(), rd.getcMes(), folioR);
                        if (docRenglon[0] == -1) {
                            log.warn("Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + caNoContrarrecibo + "\n");
                        } else {
                            int k = 0;
                            while (k < docRenglon.length && !banderaRemanente) {
                                remanente = rectIFiscalBL.getRemanente(rd.getEP(), caNoContrarrecibo, docRenglon[k]);
                                if (remanente >= rd.getmImporte()) {
                                    banderaRemanente = true;
                                }
                                k++;
                            }
                        }
                        if (banderaRemanente)
                            rectificaciones.add(rd);
                        else {
                            remanentes += "No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEP() + " renglon " + rd.getnDocRenglon() + " cxp " + caNoContrarrecibo + "\n";
                            log.warn("No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEP() + " renglon " + secuenciaCLC + " mes " + rd.getcMes() + " cxp " + caNoContrarrecibo + " remanente " + remanente + " importe a rectificar " + rd.getmImporte() + "\n");
                        }
                    } else
                        rectificaciones.add(rd);
                }
                boolean insertado = false;
                if (remanentes.length() == 0 && "".equals(remanentes)) {
                    RectificacionIngresoFiscalEncabezado res = rectIFiscalBL.getRectificacionEncabezado(folioR);
                    if (res != null) {
                        if (res.getOficioRectif() == null) {
                            if (!"".equals(nFolioSICOP))
                                insertado = rectIFiscalBL.insertarRectificacionEncabezado(folioR, id_caso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, rectificaciones);
                        }
                    } else {
                        if (!"".equals(nFolioSICOP))
                            insertado = rectIFiscalBL.insertarRectificacionEncabezado(folioR, id_caso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, rectificaciones);
                    }
                } else {
                    log.warn(remanentes);
                    throw new Exception(remanentes);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            regresaSinError = false;
            ResponseSender.sendClientSimpleMessage(response, true, "Ocurrio un error insertando la informacion: " + ex);
        } finally {
            if (regresaSinError)
                ResponseSender.sendClientSimpleMessage(response, true, "Correcto");
        }
    }
}
