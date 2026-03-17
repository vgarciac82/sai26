package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.ReintegrosAnexo1BusinessLogic;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.contable.ReintegrosMilBusinessLogic;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.ReintegroDetalle;
import com.syc.contable.core.ReintegroEncabezado;
import com.syc.contable.core.ReintegroEncabezadoMil;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "ReintegrosLayoutSicop", urlPatterns = { "/gstnmngr/ReintegrosLayoutSicop" })
public class LayoutReintegrosSicopServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    private static final long serialVersionUID = 1L;

    public LayoutReintegrosSicopServlet() {
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
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
		 * response.setContentType("text/html"); PrintWriter out =
		 * response.getWriter(); out.println(
		 * "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		 * out.println("<HTML>");
		 * out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		 * out.println("  <BODY>"); out.print("    This is ");
		 * out.print(this.getClass()); out.println(", using the GET method");
		 * out.println("  </BODY>"); out.println("</HTML>"); out.flush();
		 * out.close();
		 */
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    private static Logger log = LoggerFactory.getLogger(GestionFileReceiverServlet.class);

    private String jniName = null;

    private String tempDir = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String tipoLayout = request.getParameter("tipoLayout");
            if (//Layout para reintegros normales
            "1".equals(tipoLayout))
                creaSicop(request, response);
            if (//Layout para reintegros capitulo mil
            "2".equals(tipoLayout))
                creaSicopMil(request, response);
            if (//Layout para reintegros de Anexo 1 (Disponible Radicado-Disponible Neto)
            "3".equals(tipoLayout))
                creaSicopAnexo1(request, response);
            if (//Layout para decremento de compromiso en SICOP de RG
            "4".equals(tipoLayout))
                layoutCompromisos(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        //AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        ReintegrosBusinessLogic reintegroBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
        ReintegroEncabezado re = reintegroBL.getReintegroEncabezado(folio);
        ArrayList<ReintegroDetalle> reintegrosDetalle2 = new ArrayList<ReintegroDetalle>();
        try {
            reintegrosDetalle2 = reintegroBL.getReintegroLayout(folio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".csv\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String archivo = "";
        try {
            //A
            archivo = "H";
            archivo += ",";
            //B
            archivo += re.getfAplicacion() == null ? "" : sdf.format(new java.util.Date(re.getfAplicacion().replaceAll("-", "/")));
            archivo += ",";
            //C
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            //D
            archivo += "," + re.getcRamo().trim();
            //E
            archivo += "," + re.getcRamo().trim();
            //F
            archivo += "," + re.getcRamo().trim();
            //G
            archivo += "," + re.getcUnidadResponsableContable().trim();
            //H
            archivo += "," + re.getcUnidadResponsableContable().trim();
            //I
            archivo += "," + re.getcUnidadResponsableContable().trim();
            archivo += ",";
            //J
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            //K
            archivo += "," + re.getMovimiento();
            //L
            archivo += "," + ("");
            //M
            archivo += "," + ("N/A".equals(re.getTipoAviso()) ? "" : re.getTipoAviso());
            //N
            archivo += "," + ("N/A".equals(re.getCausaAviso()) ? "" : re.getCausaAviso());
            //O
            archivo += "," + ("");
            //P
            archivo += "," + ("");
            //Q
            archivo += "," + re.getObservaciones().replace(",", "");
            //R
            archivo += "," + re.getConcepto().replace(",", "");
            //S
            archivo += ",";
            //T
            archivo += ",";
            //U
            archivo += ",";
            //V
            archivo += ",";
            //W
            archivo += "," + file_name.replace(" ", "");
            //X
            archivo += "," + file_name.replace(" ", "");
            //Y
            archivo += ",";
        } catch (Exception jxlex) {
            jxlex.printStackTrace();
        }
        String celdaA = "";
        String celdaB = "";
        if (re.getTipoAviso() == 1) {
            celdaA = ",\r\n72";
            celdaB = "604_AVR_3";
        } else {
            celdaA = ",\r\n69";
            celdaB = "601_AVR_3";
        }
        try {
            for (int i = 0; i < reintegrosDetalle2.size(); i++) {
                //ReintegroDetalle reintegroDetalle = new ReintegroDetalle();
                ReintegroDetalle reintegroDetalleB = new ReintegroDetalle();
                //reintegroDetalle = reintegrosDetalle.get(i);
                reintegroDetalleB = reintegrosDetalle2.get(i);
                String[] claveEp = reintegroDetalleB.getEP().split("\\.");
                //A
                archivo += celdaA;
                //B
                archivo += "," + celdaB;
                //C
                archivo += "," + reintegroDetalleB.getClcSicop();
                //D
                archivo += "," + reintegroDetalleB.getSecCLC();
                //E
                archivo += "," + claveEp[1];
                //F
                archivo += "," + claveEp[2];
                //G
                archivo += "," + claveEp[0];
                //H
                archivo += "," + claveEp[3];
                //I
                archivo += "," + claveEp[4];
                //J
                archivo += "," + claveEp[5];
                //K
                archivo += "," + claveEp[6];
                //L
                archivo += "," + claveEp[7];
                //M
                archivo += "," + claveEp[8];
                //N
                archivo += "," + claveEp[9].substring(0, 1).trim();
                //O
                archivo += "," + claveEp[9].substring(1, 2).trim();
                //P
                archivo += "," + claveEp[9].substring(2, 3).trim();
                //Q
                archivo += "," + claveEp[9].substring(3, 5).trim();
                //R
                archivo += "," + claveEp[10];
                //S
                archivo += "," + claveEp[11];
                //T
                archivo += "," + claveEp[12];
                //U
                archivo += "," + claveEp[13].trim();
                //+claveEp[14];//V
                archivo += ",0000000000";
                //+reintegroDetalleB.getnCompromiso();//X ESTE REALMENTE ES LA cUNIDADNORMATIVA
                archivo += ",00";
                //W
                archivo += ",000";
                //X
                archivo += ",000";
                //Y
                archivo += ",00000";
                //Z
                archivo += ",00000";
                //AA
                archivo += ",0000000000";
                //AB
                archivo += "," + reintegroDetalleB.getnPartida();
                //AB
                ;
                //AC
                archivo += "," + reintegroDetalleB.getMes();
                //AD
                archivo += "," + reintegroDetalleB.getCxp();
                //AE
                archivo += "," + reintegroDetalleB.getBeneficiario();
                //AF
                archivo += "," + reintegroDetalleB.getNres();
                //AG
                archivo += "," + reintegroDetalleB.getSolOli();
                //AH
                archivo += "," + reintegroDetalleB.getTpag();
                //AI
                archivo += "," + reintegroDetalleB.getTipoDeCon();
                //AJ
                archivo += "," + reintegroDetalleB.getTipoCon();
                //AK
                archivo += ",0.00";
                //AL
                archivo += ",0.00";
                //AM
                archivo += ",0.00";
                //AN
                archivo += ",0.00";
                //AO
                archivo += ",";
                //AP
                archivo += "," + reintegroDetalleB.getSuficiencia();
            }
            bw.write(archivo);
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void creaSicopMil(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        //AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        ReintegrosMilBusinessLogic reintegroBL = new ReintegrosMilBusinessLogic(GestionInterface.ATT_CONEXION);
        ReintegroEncabezadoMil re = reintegroBL.getReintegroEncabezadoNuevo(folio);
        ArrayList<ReintegroDetalle> reintegrosDetalle2 = new ArrayList<ReintegroDetalle>();
        try {
            reintegrosDetalle2 = reintegroBL.getReintegroLayout(folio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".csv\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String archivo = "";
        try {
            //A
            archivo = "H";
            archivo += ",";
            archivo += re.getfAplicacion() == null ? "" : sdf.format(new java.util.Date(re.getfAplicacion().replaceAll("-", "/")));
            archivo += ",";
            //C
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            //D
            archivo += "," + re.getcRamo().trim();
            //E
            archivo += "," + re.getcRamo().trim();
            //F
            archivo += "," + re.getcRamo().trim();
            //G
            archivo += "," + re.getcUnidadResponsableContable().trim();
            //H
            archivo += "," + re.getcUnidadResponsableContable().trim();
            //I
            archivo += "," + re.getcUnidadResponsableContable().trim();
            archivo += ",";
            //C
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            //K
            archivo += "," + re.getMovimiento();
            //L
            archivo += "," + ("");
            //M
            archivo += "," + ("N/A".equals(re.getTipoAviso()) ? "" : re.getTipoAviso());
            //N
            archivo += "," + ("N/A".equals(re.getCausaAviso()) ? "" : re.getCausaAviso());
            //O
            archivo += "," + ("");
            //M
            archivo += "," + ("");
            //Q
            archivo += "," + re.getObservaciones().replace(",", "");
            //R
            archivo += "," + re.getConcepto().replace(",", "");
            //S
            archivo += ",";
            //T
            archivo += ",";
            //archivo+=",";
            //re.getfAcreditacion()==null?"": sdf.format( new java.util.Date(re.getfAcreditacion().replaceAll("-", "/")) );//C
            archivo += ",";
            //V
            archivo += ",";
            //W
            archivo += "," + file_name.replace(" ", "");
            //X
            archivo += "," + file_name.replace(" ", "");
            //Y
            archivo += ",";
        } catch (Exception jxlex) {
            jxlex.printStackTrace();
        }
        String celdaA = "";
        String celdaB = "";
        if ("1".equals(re.getTipoAviso())) {
            celdaA = ",\r\n72";
            celdaB = "604_AVR_3";
        } else {
            celdaA = ",\r\n69";
            celdaB = "601_AVR_3";
        }
        try {
            for (int i = 0; i < reintegrosDetalle2.size(); i++) {
                //ReintegroDetalle reintegroDetalle = new ReintegroDetalle();
                ReintegroDetalle reintegroDetalleB = new ReintegroDetalle();
                //reintegroDetalle = reintegrosDetalle.get(i);
                reintegroDetalleB = reintegrosDetalle2.get(i);
                String[] claveEp = reintegroDetalleB.getEP().split("\\.");
                //A
                archivo += celdaA;
                //B
                archivo += "," + celdaB;
                //C
                archivo += "," + reintegroDetalleB.getClcSicop();
                //D
                archivo += "," + reintegroDetalleB.getSecCLC();
                //E
                archivo += "," + claveEp[1];
                //F
                archivo += "," + claveEp[2];
                //G
                archivo += "," + claveEp[0];
                //H
                archivo += "," + claveEp[3];
                //I
                archivo += "," + claveEp[4];
                //J
                archivo += "," + claveEp[5];
                //K
                archivo += "," + claveEp[6];
                //L
                archivo += "," + claveEp[7];
                //M
                archivo += "," + claveEp[8];
                //N
                archivo += "," + claveEp[9].substring(0, 1).trim();
                //O
                archivo += "," + claveEp[9].substring(1, 2).trim();
                //P
                archivo += "," + claveEp[9].substring(2, 3).trim();
                //Q
                archivo += "," + claveEp[9].substring(3, 5).trim();
                //R
                archivo += "," + claveEp[10];
                //S
                archivo += "," + claveEp[11];
                //T
                archivo += "," + claveEp[12];
                //archivo+=",0";//
                //+claveEp[13].trim();//U
                archivo += ",00000000000";
                //+claveEp[14];//V
                archivo += ",0000000000";
                //+reintegroDetalleB.getnCompromiso();//W ESTE REALMENTE ES LA cUNIDADNORMATIVA
                archivo += ",00";
                //X
                archivo += ",000";
                //Y
                archivo += ",000";
                //Z
                archivo += ",00000";
                //AA
                archivo += ",00000";
                //AB
                archivo += ",0000000000";
                //AC
                archivo += "," + reintegroDetalleB.getnPartida();
                //AC
                ;
                //AD
                archivo += "," + reintegroDetalleB.getMes();
                //AE ******REVISAR
                archivo += "," + reintegroDetalleB.getCxp();
                //AF
                archivo += "," + reintegroDetalleB.getBeneficiario();
                //AG *****REVISAR
                archivo += "," + reintegroDetalleB.getNres();
                //AH
                archivo += "," + reintegroDetalleB.getSolOli();
                //AI
                archivo += "," + reintegroDetalleB.getTpag();
                //AJ
                archivo += "," + reintegroDetalleB.getTipoDeCon();
                //AK
                archivo += "," + reintegroDetalleB.getTipoCon();
                //archivo+=","+reintegroDetalle.getTipoDeCon();//AK
                //archivo+=","+reintegroDetalle.getMvto();//AL
                //archivo+=","+reintegroDetalleB.getIsr();//AM
                //archivo+=","+reintegroDetalleB.getIva();//AN
                //archivo+=","+reintegroDetalleB.getMillar();//AO
                //AL
                archivo += ",0.00";
                //AM
                archivo += ",0.00";
                //AN
                archivo += ",0.00";
                //AO
                archivo += ",0.00";
                //archivo+=","+re.getcUnidadResponsable();//AP
                //archivo+=","+c.getFolio().replace(" ", "");//AP
                //AP
                archivo += ",";
                //AQ
                archivo += "," + reintegroDetalleB.getSuficiencia();
            }
            bw.write(archivo);
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void creaSicopAnexo1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        ReintegrosAnexo1BusinessLogic reintegroBL = new ReintegrosAnexo1BusinessLogic(GestionInterface.ATT_CONEXION);
        ReintegroEncabezadoMil re = reintegroBL.getReintegroEncabezadoNuevo(folio);
        ArrayList<ReintegroDetalle> reintegrosDetalle2 = new ArrayList<ReintegroDetalle>();
        try {
            reintegrosDetalle2 = reintegroBL.getReintegroLayout(folio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".csv\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String archivo = "";
        try {
            //A
            archivo = "H";
            archivo += ",";
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            archivo += ",";
            //C
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            //D
            archivo += "," + re.getcRamo().trim();
            //E
            archivo += "," + re.getcRamo().trim();
            //F
            archivo += "," + re.getcRamo().trim();
            //G
            archivo += "," + re.getcUnidadResponsableContable().trim();
            //H
            archivo += "," + re.getcUnidadResponsableContable().trim();
            //I
            archivo += "," + re.getcUnidadResponsableContable().trim();
            archivo += ",";
            //C
            archivo += re.getfSolicitud() == null ? "" : sdf.format(new java.util.Date(re.getfSolicitud().replaceAll("-", "/")));
            //K
            archivo += "," + re.getMovimiento();
            //L
            archivo += "," + ("");
            //M
            archivo += "," + ("N/A".equals(re.getTipoAviso()) ? "" : re.getTipoAviso());
            //N
            archivo += "," + ("N/A".equals(re.getCausaAviso()) ? "" : re.getCausaAviso());
            //O
            archivo += "," + ("");
            //M
            archivo += "," + ("");
            //Q
            archivo += "," + re.getObservaciones().replace(",", "");
            //R
            archivo += "," + re.getConcepto().replace(",", "");
            //S
            archivo += ",";
            //T
            archivo += ",";
            //archivo+=",";
            //re.getfAcreditacion()==null?"": sdf.format( new java.util.Date(re.getfAcreditacion().replaceAll("-", "/")) );//C
            archivo += ",";
            //V
            archivo += ",";
            //W
            archivo += "," + file_name.replace(" ", "");
            //X
            archivo += "," + file_name.replace(" ", "");
            //Y
            archivo += ",";
        } catch (Exception jxlex) {
            jxlex.printStackTrace();
        }
        String celdaA = "";
        String celdaB = "";
        if ("1".equals(re.getTipoAviso())) {
            celdaA = ",\r\n72";
            celdaB = "604_AVR_3";
        } else {
            celdaA = ",\r\n69";
            celdaB = "601_AVR_3";
        }
        try {
            for (int i = 0; i < reintegrosDetalle2.size(); i++) {
                //ReintegroDetalle reintegroDetalle = new ReintegroDetalle();
                ReintegroDetalle reintegroDetalleB = new ReintegroDetalle();
                //reintegroDetalle = reintegrosDetalle.get(i);
                reintegroDetalleB = reintegrosDetalle2.get(i);
                String[] claveEp = reintegroDetalleB.getEP().split("\\.");
                //A
                archivo += celdaA;
                //B
                archivo += "," + celdaB;
                //C
                archivo += "," + reintegroDetalleB.getClcSicop();
                //D
                archivo += "," + reintegroDetalleB.getSecCLC();
                //E
                archivo += "," + claveEp[1];
                //F
                archivo += "," + claveEp[2];
                //G
                archivo += "," + claveEp[0];
                //H
                archivo += "," + claveEp[3];
                //I
                archivo += "," + claveEp[4];
                //J
                archivo += "," + claveEp[5];
                //K
                archivo += "," + claveEp[6];
                //L
                archivo += "," + claveEp[7];
                //M
                archivo += "," + claveEp[8];
                //N
                archivo += "," + claveEp[9].substring(0, 1).trim();
                //O
                archivo += "," + claveEp[9].substring(1, 2).trim();
                //P
                archivo += "," + claveEp[9].substring(2, 3).trim();
                //Q
                archivo += "," + claveEp[9].substring(3, 5).trim();
                //R
                archivo += "," + claveEp[10];
                //S
                archivo += "," + claveEp[11];
                //T
                archivo += "," + claveEp[12];
                //archivo+=",0";//
                //U
                archivo += "," + claveEp[13].trim();
                //+claveEp[14];//V
                archivo += ",0000000000";
                //+reintegroDetalleB.getnCompromiso();//W ESTE REALMENTE ES LA cUNIDADNORMATIVA
                archivo += ",00";
                //X
                archivo += ",000";
                //Y
                archivo += ",000";
                //Z
                archivo += ",00000";
                //AA
                archivo += ",00000";
                //AB
                archivo += ",0000000000";
                //AC
                archivo += "," + reintegroDetalleB.getnPartida();
                //AC
                ;
                //AD
                archivo += "," + reintegroDetalleB.getMes();
                //AE ******REVISAR
                archivo += "," + reintegroDetalleB.getCxp();
                //AF
                archivo += "," + reintegroDetalleB.getBeneficiario();
                //AG *****REVISAR
                archivo += "," + reintegroDetalleB.getNres();
                //AH
                archivo += "," + reintegroDetalleB.getSolOli();
                //AI
                archivo += "," + reintegroDetalleB.getTpag();
                //AJ
                archivo += "," + reintegroDetalleB.getTipoDeCon();
                //AK
                archivo += "," + reintegroDetalleB.getTipoCon();
                //archivo+=","+reintegroDetalle.getTipoDeCon();//AK
                //archivo+=","+reintegroDetalle.getMvto();//AL
                //archivo+=","+reintegroDetalleB.getIsr();//AM
                //archivo+=","+reintegroDetalleB.getIva();//AN
                //archivo+=","+reintegroDetalleB.getMillar();//AO
                //AL
                archivo += ",0.00";
                //AM
                archivo += ",0.00";
                //AN
                archivo += ",0.00";
                //AO
                archivo += ",0.00";
                //archivo+=","+re.getcUnidadResponsable();//AP
                //archivo+=","+c.getFolio().replace(" ", "");//AP
                //AP
                archivo += ",";
                //AQ
                archivo += "," + reintegroDetalleB.getSuficiencia();
            }
            bw.write(archivo);
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void layoutCompromisos(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        ReintegrosBusinessLogic reintegroBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
        String decrementoEncabezado = "";
        String decrementoDetalle = "";
        try {
            decrementoEncabezado = reintegroBL.getDecrementoEncabezado(folio, c.getFolio());
            decrementoDetalle = reintegroBL.getDecrementoDetalle(folio, c.getFolio());
            reintegroBL.actualizaEnvioSICOPCompromiso(c.getFolio());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        String file_name = "LayoutDecrementoCompromiso" + c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".csv\";");
        String archivo = "";
        try {
            archivo = decrementoEncabezado;
            ;
            archivo += decrementoDetalle;
            bw.write(archivo);
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
