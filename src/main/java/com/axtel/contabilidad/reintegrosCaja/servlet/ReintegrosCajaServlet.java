package com.axtel.contabilidad.reintegrosCaja.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Caso;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.axtel.contabilidad.reintegrosCaja.servlet.ReintegrosCajaServlet;
import com.axtel.contabilidad.reintegrosCaja.ReintegrosCajaBusinessLogic;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaDetalle;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaEncabezado;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Ana
 */
@WebServlet(name = "ReintegrosCajaServlet", urlPatterns = { "/contabilidad/ReintegrosCaja" })
public class ReintegrosCajaServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 8140857888553424437L;

    private static final Logger log = LoggerFactory.getLogger(ReintegrosCajaServlet.class);

    public ReintegrosCajaServlet() {
        super();
    }

    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ReintegrosCajaBusinessLogic reintegrosCajaBL = new ReintegrosCajaBusinessLogic(GestionInterface.ATT_CONEXION);
        ReintegrosCajaEncabezado rce = new ReintegrosCajaEncabezado();
        ReintegrosCajaDetalle rcd = new ReintegrosCajaDetalle();
        String idOper = request.getParameter("id_oper");
        String cEvento = request.getParameter("cEventoOrigen");
        String cFechaCaptura = request.getParameter("fechaCaptura");
        String cFechaAplicacion = request.getParameter("fechaAplicacion");
        int nFolioReinegroCaja = new Integer(request.getParameter("nFolioReintegrocaja")).intValue();
        int cEnviadoSICOP = 2;
        String tipoPoliza = null;
        String mensaje = null;
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        if ("S".equals(request.getParameter("cEsFirmaElectronica"))) {
            cEnviadoSICOP = -2;
        }
        try {
            if (idOper != null && !"".equals(idOper)) {
                if ("1".equals(idOper)) {
                    //CAPTURISTA - INSERTA DATOS CAPTURADOS
                    boolean retorno = false;
                    //BUSCA SI EXISTEN LOS DATOS EN BASE DE DATOS
                    String existe = reintegrosCajaBL.buscaDatos(nFolioReinegroCaja);
                    if (!"SI".equals(existe)) {
                        //SI NO EXISTEN DATOS EN BASE DE DATOS ENTRA A INSERTAR
                        double monto = Double.parseDouble(request.getParameter("mMonto"));
                        tipoPoliza = reintegrosCajaBL.getTipoPolizaEvento(cEvento);
                        /**
                         * ********************ENCABEZADO**********************
                         */
                        rce.setnFolioReintegrocaja(nFolioReinegroCaja);
                        rce.setfCreacion(cFechaCaptura);
                        rce.setfAplicacion(cFechaAplicacion);
                        rce.setcMes(Integer.parseInt(cFechaAplicacion.substring(3, 5)));
                        rce.setcTipoPoliza(tipoPoliza);
                        rce.setnFolioPoliza(0);
                        rce.setcDescripcionPoliza(new String(request.getParameter("cConcepto").getBytes("ISO-8859-1"), "UTF-8").replace("\n", "").replace("\r", " "));
                        rce.setU_LOGIN(request.getParameter("u_login"));
                        rce.setcDocumentohAplicado(null);
                        rce.setcMotivoRechazo(null);
                        rce.setcUnidadResponsableContable("RHQ");
                        rce.setnFolioPolizaCancelacion(0);
                        rce.setfCancelacion(null);
                        rce.setaEjercicioFiscal(request.getParameter("ejercicioFiscal"));
                        rce.setcRamo("16");
                        rce.setcUnidadEjecutora(request.getParameter("cUR"));
                        rce.setnNumEmpleadoElab(new Integer(request.getParameter("nNumEmpleadoElab")));
                        rce.setnEnviadoSICOP(cEnviadoSICOP);
                        rce.setcIdUsuarioCaptura(request.getParameter("u_login"));
                        rce.setID_CASO(new Integer(request.getParameter("id_caso")).intValue());
                        rce.setmMontoSolicitud(monto);
                        /**
                         * ********************DETALLE**********************
                         */
                        rcd.setnFolioReintegroCaja(nFolioReinegroCaja);
                        rcd.setnDocRenglon(1);
                        rcd.setcEvento(cEvento);
                        rcd.setcEventoDestino(request.getParameter("cEventoDestino"));
                        rcd.setmImporte(monto);
                        rcd.setmImporteNegativo(monto * -1);
                        rcd.setALM(null);
                        rcd.setCTAB(request.getParameter("CTA_TODAS"));
                        rcd.setRFC(request.getParameter("hbuscabeneficiario"));
                        rcd.setEP(null);
                        rcd.setnCuentaBeneficiario(request.getParameter("ctabBeneficiario"));
                        rcd.setFFM(request.getParameter("FFM"));
                        rcd.setcCentroContable(request.getParameter("cCentroContable"));
                        rcd.setcUnidadResponsable(request.getParameter("cUR"));
                        if (rce != null) {
                            retorno = reintegrosCajaBL.insertarDatos(rce, rcd);
                        }
                    } else {
                        //TRUE SI YA EXISTIAN LOS DATOS EN BASE DE DATOS
                        retorno = true;
                    }
                    if (retorno) {
                        ResponseSender.sendClientSimpleMessage(response, true, "Correcto");
                    } else
                        throw new Exception("Ocurrion un error al guardar la solicitud, consulte al administrador");
                } else //FIN CAPTURISTA - INSERTA DATOS CAPTURADOS
                if ("2".equals(idOper)) {
                    //REVISOR - APLICA PRIMER POLIZA
                    String tablaEncabezado = "tReintegroCajaEncabezado";
                    String tablaDetalle = "tReintegroCajaDetalle";
                    String documento = request.getParameter("TITULO_APLICACION");
                    String campoFolio = "nFolioReintegroCaja";
                    try {
                        mensaje = reintegrosCajaBL.aplicaReintegroCaja(c, cFechaAplicacion, tablaEncabezado, tablaDetalle, documento, campoFolio);
                    } catch (Exception ex) {
                        session.setAttribute("mensaje", ex.getMessage());
                        ResponseSender.sendClientSimpleMessage(response, false, "No se aplico la solicitud");
                    }
                } else //FIN REVISOR - APLICA PRIMER POLIZA
                if ("3".equals(idOper)) {
                    //AUTORIZADOR - INSETA DATOS APLICA SEGUNDA POLIZA
                    boolean retornoAut = false;
                    String existe = reintegrosCajaBL.buscaDatosAut(nFolioReinegroCaja);
                    String rechazaAutorizador = request.getParameter("rechazaAutorizador");
                    if ("SI".equals(rechazaAutorizador)) {
                        if (!"SI".equals(existe)) {
                            //SI NO EXISTEN LOS DATOS EN BASE DE DATOS PARA EL AUT ENTRA A INSERTAR
                            String tablaEncabezadoAut = "tReintegroCajaAutEncabezado";
                            String tablaDetalleAut = "tReintegroCajaAutDetalle";
                            String documentoAut = "REINTEGROCAJAAUT";
                            String campoFolioAut = "nFolioReintegroCajaAut";
                            tipoPoliza = reintegrosCajaBL.getTipoPolizaEvento(request.getParameter("cEventoDestino"));
                            retornoAut = reintegrosCajaBL.insertarDatosAut(tablaEncabezadoAut, tablaDetalleAut, tipoPoliza, request);
                            if (retornoAut) {
                                //SI SE INSERTARON CORRECTAMENTE ENTRA PARA APLICAR CONTABLEMNETE
                                try {
                                    mensaje = reintegrosCajaBL.aplicaReintegroCaja(c, cFechaAplicacion, tablaEncabezadoAut, tablaDetalleAut, documentoAut, campoFolioAut);
                                } catch (Exception ex) {
                                    session.setAttribute("mensaje", ex.getMessage());
                                    ResponseSender.sendClientSimpleMessage(response, false, "No se aplico la solicitud");
                                }
                            } else
                                throw new Exception("Ocurrion un error al guardar la solicitud, consulte al administrador");
                        } else {
                        }
                    } else if ("NO".equals(rechazaAutorizador)) {
                        mensaje = reintegrosCajaBL.cancelaSolicitud(c, String.valueOf(nFolioReinegroCaja), cFechaAplicacion);
                    }
                }
                //FIN AUTORIZADOR - INSETA DATOS APLICA SEGUNDA POLIZA
            }
        } catch (Exception ex) {
            ResponseSender.sendClientSimpleMessage(response, false, "Ocurrio un error insertando la informacion. Favor de avisar al administrador");
        }
    }

    public void init() throws ServletException {
    }
}
