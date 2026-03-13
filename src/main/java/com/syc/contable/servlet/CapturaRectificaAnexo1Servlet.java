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
import com.syc.contable.RectificacionAnexo1BusinessLogic;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.contable.core.RectificacionAnexo1Detalle;
import com.syc.contable.core.RectificacionAnexo1Encabezado;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CapturaRectificaAnexo1Servlet", urlPatterns = { "/gstnmngr/CapturaRectificaAnexo1" })
public class CapturaRectificaAnexo1Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ReintegrosBusinessLogic.class);

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        RectificacionAnexo1BusinessLogic recAnexo1BL = new RectificacionAnexo1BusinessLogic(GestionInterface.ATT_CONEXION);
        String porExcel = request.getParameter("subeExcel");
        //OBTENGO EL DIA DE HOY FMC 30/oct
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        //**********************************************
        //para saber qué redirect voy a usar, el del catch o el del finally
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
                //Datos de Encabezado del Oficio de Rectificación
                String folio = request.getParameter("folio");
                int folioR = new Integer(folio.substring(folio.lastIndexOf('-') + 1)).intValue();
                String id_caso = request.getParameter("ID_Caso_C");
                //METO TRY CATCH PARA QUE NO TRUENE EL PROCESO Y LE ASIGNO LAS FECHAS DE HOY SI HUBO ERROR FMC 30/oct
                java.sql.Date fExp;
                java.sql.Date fApl;
                try {
                    fExp = java.sql.Date.valueOf(request.getParameter("fExp"));
                    fApl = java.sql.Date.valueOf(request.getParameter("fApl"));
                } catch (IllegalArgumentException e) {
                    fExp = java.sql.Date.valueOf(today);
                    fApl = java.sql.Date.valueOf(today);
                }
                //*************************************************************
                String nMes = request.getParameter("cMes_C");
                String cTipoMovto = request.getParameter("CatMovimientoRectificacion");
                String nOrigenPPTO = request.getParameter("origPresupuesto");
                String conceptoRectificacion = request.getParameter("concepto");
                String oficioRectif = request.getParameter("oficioRectif");
                String ctr_int = request.getParameter("ctr_int");
                String cCentroContable = request.getParameter("cCentroContable_C");
                //Datos de la CLC PAGADA  a rectificar
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
                String folioSIAFF = request.getParameter("nFolioSIAFF");
                String[] detalle = request.getParameter("info").split("!");
                int j = 1;
                ReintegrosBusinessLogic rbl = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
                ArrayList<RectificacionAnexo1Detalle> rectificaciones = new ArrayList<RectificacionAnexo1Detalle>();
                String evento = "";
                for (int i = 0; i < detalle.length; i += 6) {
                    RectificacionAnexo1Detalle rd = new RectificacionAnexo1Detalle();
                    rd.setFolio(folioR);
                    rd.setRenglon(Integer.parseInt(detalle[i].trim()));
                    rd.setMes(Integer.parseInt(detalle[i + 1].trim()));
                    if (detalle[i + 3].trim().equals("DICE")) {
                        evento = recAnexo1BL.getEventoRectificacionAnexo1(detalle[i + 2].trim(), "DICE");
                        rd.setEvento(evento);
                    } else {
                        evento = recAnexo1BL.getEventoRectificacionAnexo1(detalle[i + 2].trim(), "DEBE");
                        rd.setEvento(evento);
                    }
                    rd.setEp(detalle[i + 2].trim());
                    rd.setImporte(Double.parseDouble(detalle[i + 4].trim()));
                    rd.setImporteneg(Double.parseDouble(detalle[i + 4].trim()) * -1);
                    rd.setCentro(cCentroContable);
                    rd.setcaNoContrarrecibo(detalle[i + 5].trim());
                    if (rd.getEvento().contains("DEBE")) {
                        String partida = rd.getEp().substring(31, 32);
                        if ("1".equals(partida)) {
                            remanentes += "No se puede hacer una rectificacion tomando partidas del capitulo mill";
                        }
                    }
                    if (rd.getEvento().contains("DICE")) {
                        boolean banderaRemanente = false;
                        double remanente = 0;
                        int secuenciaCLC = recAnexo1BL.secCLCRect(nFolioSIAFF, rd.getEp());
                        // JGDS.20171226 recAnexo1BL.getNDocRenglon(caNoContrarrecibo, rd.getEp(), rd.getMes(), folioR);
                        int[] docRenglon = new int[] { 1 };
                        if (docRenglon[0] == -1) {
                            log.warn("Favor de revisar el mes para la EP " + rd.getEp() + " con CXP " + caNoContrarrecibo + "\n");
                        } else {
                            //							JGDS.20171226 Janise hizo un cambio y en la vista ya no esta el docrenglon
                            //nueva validacion por clave y mes e integradora
                            //
                            //							int k = 0;
                            //							while (k < docRenglon.length && !banderaRemanente) {
                            //								remanente = recAnexo1BL.getRemanente(rd.getEp(), caNoContrarrecibo, docRenglon[k]);
                            //								if (remanente >= rd.getImporte()) {
                            //									banderaRemanente = true;
                            //								}
                            //								k++;
                            //							}
                            banderaRemanente = true;
                        }
                        if (banderaRemanente)
                            rectificaciones.add(rd);
                        else {
                            remanentes += "No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + rd.getRenglon() + " cxp " + caNoContrarrecibo + "\n";
                            log.warn("No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + secuenciaCLC + " mes " + rd.getMes() + " cxp " + caNoContrarrecibo + " remanente " + remanente + " importe a rectificar " + rd.getImporte() + "\n");
                            //throw new Exception(remanentes);
                        }
                    } else
                        rectificaciones.add(rd);
                    j++;
                }
                boolean insertado = false;
                if (remanentes.length() == 0 && "".equals(remanentes)) {
                    RectificacionAnexo1Encabezado res = recAnexo1BL.getRectificacionEncabezado(folioR);
                    if (res != null) {
                        if (res.getOficioRectif() == null) {
                            if (!"".equals(nFolioSICOP))
                                insertado = recAnexo1BL.insertTRectificacionEncabezado(folioR, id_caso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, rectificaciones);
                            //SE COMENTA Y SE INSERTA EL DETALLE EN EL MISMO METODO DEL ENCABEZADO POR EL COMMIT
                            //recAnexo1BL.insertRectificacionDetalle(rectificaciones,caNoContrarrecibo);
                        }
                    } else {
                        if (!"".equals(nFolioSICOP))
                            insertado = recAnexo1BL.insertTRectificacionEncabezado(folioR, id_caso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, rectificaciones);
                        //SE COMENTA Y SE INSERTA EL DETALLE EN EL MISMO METODO DEL ENCABEZADO POR EL COMMIT
                        //recAnexo1BL.insertRectificacionDetalle(rectificaciones,caNoContrarrecibo);
                    }
                } else {
                    //					session.setAttribute("mensaje", remanentes);
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
