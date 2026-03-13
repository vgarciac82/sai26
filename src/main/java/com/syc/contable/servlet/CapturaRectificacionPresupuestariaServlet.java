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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.syc.contable.RectificacionPresupuestariaBusinessLogic;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.contable.core.RectificacionDetalle;
import com.syc.contable.core.RectificacionEncabezado;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CapturaRectificacionPresupuestariaServlet", urlPatterns = { "/gstnmngr/CapturaRectificacionPresupuestaria" })
public class CapturaRectificacionPresupuestariaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(ReintegrosBusinessLogic.class);

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
        RectificacionPresupuestariaBusinessLogic recPresBL = new RectificacionPresupuestariaBusinessLogic(GestionInterface.ATT_CONEXION);
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
                String conceptoRectificacion = request.getParameter("concepto").replaceAll("[\n\r]", "");
                String oficioRectif = request.getParameter("oficioRectif");
                String ctr_int = request.getParameter("ctr_int");
                String cCentroContable = request.getParameter("cCentroContable_C");
                //Datos de la CLC PAGADA  a rectificar
                String cTipoRectificacion = request.getParameter("dTipoPago");
                String nFolioSICOP = (request.getParameter("nFolioSICOP") == null) ? "" : request.getParameter("nFolioSICOP").trim();
                String caNoContrarrecibo = request.getParameter("caNoContrarrecibo");
                String nFolioPoliza = request.getParameter("nFolioPoliza");
                String caNoContrarreciboOA = request.getParameter("cboContrarrecibo");
                //String cTipoPoliza = request.getParameter("cTipoPoliza");
                //RE NUEVO TIPO DE POLIZA PORQUE LOS DE CONTABILIDAD NO QUIEREN QUE PEGUE
                String cTipoPoliza = "RE";
                String cDescripcionPoliza = request.getParameter("cDescripcionPoliza");
                String cUnidadResponsableContable = request.getParameter("cUnidadResponsableContable");
                String nFolioSIAFF = request.getParameter("nFolioSIAFF");
                String totalDice = request.getParameter("totalDice");
                String totalDebe = request.getParameter("totalDebeDecir");
                String folioSIAFF = request.getParameter("nFolioSIAFF");
                String esIP = request.getParameter("cEsIP");
                //String eventoDICE = request.getParameter("eventoDICE");
                //String eventoDEBE_DECIR = request.getParameter("eventoDEBE_DECIR");
                String[] detalle = request.getParameter("info").split("!");
                int j = 1;
                ReintegrosBusinessLogic rbl = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
                ArrayList<RectificacionDetalle> rectificaciones = new ArrayList<RectificacionDetalle>();
                for (int i = 0; i < detalle.length; i += 8) {
                    RectificacionDetalle rd = new RectificacionDetalle();
                    rd.setFolio(folioR);
                    rd.setRenglon(Integer.parseInt(detalle[i].trim()));
                    rd.setMes(Integer.parseInt(detalle[i + 1].trim()));
                    if (detalle[i + 3].trim().equals("DICE")) {
                        rd.setEvento("DICE_TRA");
                        rd.setnidprograma(detalle[i + 5].trim());
                        rd.setcSubPrograma(detalle[i + 6].trim());
                        rd.setcaNoContrarrecibo(detalle[i + 7].trim());
                    }
                    if (detalle[i + 3].trim().equals("DEBE DECIR")) {
                        rd.setEvento("DEBE_DECIR_TRA_DI");
                        rd.setnidprograma(detalle[i + 5].trim());
                        rd.setcSubPrograma(detalle[i + 6].trim());
                        rd.setcaNoContrarrecibo(detalle[i + 7].trim());
                    }
                    rd.setEp(detalle[i + 2].trim());
                    if (StringUtils.isEmpty(rd.getEp()))
                        continue;
                    rd.setImporte(Double.parseDouble(detalle[i + 4].trim()));
                    rd.setImporteneg(Double.parseDouble(detalle[i + 4].trim()) * -1);
                    rd.setCentro(cCentroContable);
                    //rd.setCapitulo(rbl.getcPartida(rd.getEp()));
                    if (rd.getEvento().contains("DICE")) {
                        boolean banderaRemanente = false;
                        double remanente = 0;
                        int secuenciaCLC = recPresBL.secCLCRect(nFolioSIAFF, rd.getEp());
                        //if(secuenciaCLC==-1)
                        //throw new Exception("No se pudo obtener la secuencia para la EP: "+rd.getEp());
                        int[] docRenglon = rbl.getNDocRenglon(caNoContrarrecibo, rd.getEp(), rd.getMes(), folioR);
                        if (docRenglon[0] == -1) {
                            log.warn("Favor de revisar el mes para la EP " + rd.getEp() + " con CXP " + caNoContrarrecibo + "\n");
                        } else {
                            int k = 0;
                            while (k < docRenglon.length && !banderaRemanente) {
                                if (caNoContrarrecibo.contains("OA"))
                                    remanente = rbl.getRemanenteOA(rd.getEp(), caNoContrarrecibo, docRenglon[k], caNoContrarreciboOA);
                                else
                                    remanente = rbl.getRemanente(rd.getEp(), caNoContrarrecibo, docRenglon[k]);
                                if (remanente >= rd.getImporte()) {
                                    banderaRemanente = true;
                                }
                                k++;
                            }
                        }
                        if (banderaRemanente)
                            rectificaciones.add(rd);
                        else {
                            remanentes += "No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + rd.getRenglon() + " cxp " + caNoContrarrecibo + "\n";
                            log.warn("No hay suficiente remanente para poder aplicar la rectificacion para " + rd.getEp() + " renglon " + secuenciaCLC + " mes " + rd.getMes() + " cxp " + caNoContrarrecibo + " remanente " + remanente + " importe a reintegrar " + rd.getImporte() + "\n");
                            //throw new Exception(remanentes);
                        }
                    } else
                        rectificaciones.add(rd);
                    j++;
                }
                //ESTAS LINEAS SON PARA QUE NO TRUENE EN EL GUARDADO YA QUE SE MANDA INSERTAR AL GUARDAR ... FMC 30/OCT
                boolean insertado = false;
                if (remanentes.length() == 0 && "".equals(remanentes)) {
                    RectificacionEncabezado res = recPresBL.getRectificacionEncabezado(folioR);
                    if (res != null) {
                        if (res.getOficioRectif() == null) {
                            if (!"".equals(nFolioSICOP))
                                insertado = recPresBL.insertTRectificacionEncabezado(folioR, id_caso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, esIP);
                            recPresBL.insertRectificacionDetalle(rectificaciones);
                        }
                    } else {
                        if (!"".equals(nFolioSICOP))
                            insertado = recPresBL.insertTRectificacionEncabezado(folioR, id_caso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, esIP);
                        recPresBL.insertRectificacionDetalle(rectificaciones);
                    }
                } else {
                    session.setAttribute("mensaje", remanentes);
                    log.warn(remanentes);
                    throw new Exception(remanentes);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            regresaSinError = false;
            ResponseSender.sendClientSimpleMessage(response, false, "Ocurrio un error insertando la informacion.");
        } finally {
            if (regresaSinError)
                ResponseSender.sendClientSimpleMessage(response, true, "Correcto");
        }
    }
}
