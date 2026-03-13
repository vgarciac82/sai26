package com.syc.contable.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.contable.core.ReintegroEncabezado;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ProcesaReintegrosInsert", urlPatterns = { "/gstnmngr/ReintegrosInsert" })
public class ProcesaReintegrosInsert extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public ProcesaReintegrosInsert() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*response.setContentType("text/html");
	PrintWriter out = response.getWriter();
	out
		.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
	out.println("<HTML>");
	out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
	out.println("  <BODY>");
	out.print("    This is ");
	out.print(this.getClass());
	out.println(", using the GET method");
	out.println("  </BODY>");
	out.println("</HTML>");
	out.flush();
	out.close();*/
    }

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
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        ReintegrosBusinessLogic reintegroBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        //String tablaDatos = request.getParameter("info").trim();
        String tipoclc = request.getParameter("tipocausain").trim();
        if ("1".equals(tipoclc)) {
            tipoclc = "RD";
        } else if ("2".equals(tipoclc)) {
            tipoclc = "RC";
        } else {
            tipoclc = "REIN_TRAM_SPEI";
        }
        int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        String cRamo = usuario.getU_Ramo();
        //B00 para lo de edgar
        String cUnidadEjecutora = usuario.getU_UR();
        String cEjercicioFiscal = "";
        try {
            cEjercicioFiscal = adecProy.obtenEjercicioFiscal();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        String cTipoPoliza = "";
        String centro = request.getParameter("cCentro").trim();
        //ArrayList<ReintegroDetalle> reintegrosDetalle = null;
        try {
            //reintegrosDetalle = reintegroBL.getDetallesInsert(tablaDatos, folio);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        ReintegroEncabezado reintegroEncabezado = new ReintegroEncabezado();
        try {
            if (reintegroBL.getDetallePasoTotal(folio) > 0) {
                try {
                    cTipoPoliza = reintegroBL.getTipoPoliza("REINTEGRO");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                reintegroEncabezado.setnFolioReintegro(folio);
                reintegroEncabezado.setfSolicitud(today);
                reintegroEncabezado.setcTipoReintegro(request.getParameter("clctipoin"));
                reintegroEncabezado.setcRamo(cRamo);
                reintegroEncabezado.setcUnidadResponsable(cUnidadEjecutora);
                reintegroEncabezado.setcDocumentoHaplicado("N");
                reintegroEncabezado.setaEjercicioFiscal(cEjercicioFiscal);
                reintegroEncabezado.setObservaciones(request.getParameter("observacionesin"));
                reintegroEncabezado.setConcepto(request.getParameter("conceptoin"));
                reintegroEncabezado.setU_login(usuario.getLogin());
                if ("1.0".equals(request.getParameter("fAcreditin")))
                    reintegroEncabezado.setfAcreditacion(today);
                else
                    reintegroEncabezado.setfAcreditacion(request.getParameter("fAcreditin"));
                try {
                    reintegroEncabezado.setFormaDePago(Integer.parseInt(request.getParameter("formapagoin")));
                } catch (NumberFormatException nu) {
                    reintegroEncabezado.setFormaDePago(2);
                }
                reintegroEncabezado.setClvRastreo(request.getParameter("clvrastreoin"));
                reintegroEncabezado.setFichaDeposito(request.getParameter("fichadepositoin"));
                reintegroEncabezado.setClvBanco(request.getParameter("clvbancoin"));
                reintegroEncabezado.setCuentaBancaria(request.getParameter("cuentain"));
                reintegroEncabezado.setLc(request.getParameter("lineacapin"));
                reintegroEncabezado.setcTipoPoliza(cTipoPoliza);
                reintegroEncabezado.setcUnidadResponsableContable("RHQ");
                reintegroEncabezado.setMovimiento(request.getParameter("movimientoin"));
                reintegroEncabezado.setCuentaPorPagar(request.getParameter("cxpin"));
                try {
                    reintegroEncabezado.setFolioDependencia(Integer.parseInt(request.getParameter("folioDepin")));
                } catch (NumberFormatException nu) {
                    reintegroEncabezado.setFolioDependencia(0);
                }
                try {
                    reintegroEncabezado.setAviso(Integer.parseInt(request.getParameter("avisoin")));
                } catch (NumberFormatException nu) {
                    reintegroEncabezado.setAviso(1);
                }
                try {
                    reintegroEncabezado.setCausaAviso(Integer.parseInt(request.getParameter("causaavisoin")));
                } catch (NumberFormatException nu) {
                    reintegroEncabezado.setCausaAviso(1);
                }
                try {
                    reintegroEncabezado.setTipoAviso(Integer.parseInt(request.getParameter("tipocausain")));
                } catch (NumberFormatException nu) {
                    reintegroEncabezado.setTipoAviso(1);
                }
                reintegroEncabezado.setfAplicacion(today);
                if (request.getParameter("importein") != null && request.getParameter("importein") != "")
                    reintegroEncabezado.setImporteLC(request.getParameter("importein"));
                try {
                    if (reintegroEncabezado != null)
                        reintegroBL.insertaEncReintegro(folio, reintegroEncabezado, tipoclc, centro, reintegroEncabezado.getfAplicacion(), c);
                    //if(reintegrosDetalle!=null){
                    if (reintegroBL.getDetallePasoTotal(folio) > 0) {
                        reintegroBL.borraDetReintegro(folio);
                        reintegroBL.insertaDetReintegro(folio, null, tipoclc, centro);
                        reintegroBL.borraDetReintegroPaso(folio);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    response.sendRedirect(basePath + "plantillasCasos/reintegroPresupuestal.jsp?avanzai=1");
                }
            } else {
                response.sendRedirect(basePath + "plantillasCasos/reintegroPresupuestal.jsp?mensajeclc=si");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
