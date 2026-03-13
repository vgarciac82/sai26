package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "AplicacionContableServlet", urlPatterns = { "/gstnmngr/AppCont" })
public class AplicacionContableServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = -1507407679501468251L;

    private static Logger log = Logger.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public AplicacionContableServlet() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        String cCentroContable = "";
        String mensaje = "";
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conn = null;
        try {
            conn = cbl.getConnection();
            //Motor ORIGINAL
            /*arrLResult = conInt.aplicarContable(conn, c, "", "", "", 0, "");//el commit se hace aqui adentro, si algo falla tambien el rollback se hace adentro*/
            //Motor NUEVO
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                conn.commit();
            } else {
                conn.rollback();
            }
            //Recargando el caso
            Caso sc = new Caso();
            sc.setIdCaso(c.getIdCaso());
            c = CasoManager.select(conn, sc);
            log.debug(c.getCasoDato("APLICADO_CONT").getValor());
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        log.debug("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>Aplicacion Presupuestal/Contable</TITLE></HEAD>");
        out.println("<script type=\"text/javascript\" for=\"window\" event=\"onunload\">");
        out.println("	if (!bClicBtn) {");
        out.println("		regresar();");
        out.println("	}");
        out.println("</script>");
        out.println("<SCRIPT languaje=\"javascript\">");
        out.println("	var bClicBtn = false;");
        out.println("  		   function fnIni(){");
        out.println("  					try {");
        out.println("  						parent.document.getElementById(\"divEspera\").style.visibility = 'hidden';");
        out.println("  					}catch(e){");
        out.println("  			   			}");
        out.println("  		   }");
        out.println("  		   function regresar(){");
        out.println("  				try {");
        out.println("  					document.getElementById(\"pbRegresa\").style.visibility=\"hidden\"; ");
        if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()) || mensaje.contains("APLICADO")) {
            if (c.getIdTC() == 13) {
                out.println("  					opener.parent.document.getElementById(\"pb_send\").disabled = false;");
                out.println("  					opener.comprobacionPoliza();");
                out.println("  					opener.parent.document.getElementById(\"pb_send\").click();");
            } else {
                out.println("  					opener.parent.document.getElementById(\"pb_send\").disabled = false;");
                out.println("  					opener.cmdImprimir('PolizaPago');");
                out.println("  					opener.parent.document.getElementById(\"pb_send\").click();");
            }
        } else {
            out.println("  					opener.location.reload(-1);");
            out.println("  					opener.parent.document.getElementById(\"pb_save\").disabled=false;");
        }
        out.println("  				}catch(e){");
        if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()) || mensaje.contains("APLICADO")) {
            if (c.getIdTC() == 13) {
                out.println("  					parent.parent.document.getElementById(\"pb_send\").disabled = false;");
                out.println("  					parent.comprobacionPoliza();");
                out.println("  					parent.parent.document.getElementById(\"pb_send\").click();");
            } else {
                out.println("  					parent.parent.document.getElementById(\"pb_send\").disabled = false;");
                out.println("  					parent.cmdImprimir('PolizaPago');");
                out.println("  					parent.parent.document.getElementById(\"pb_send\").click();");
            }
        } else {
            out.println("  					location.reload(-1);");
            out.println("  					parent.document.getElementById(\"pb_save\").disabled=false;");
        }
        out.println("  				}");
        out.println("  				window.close();");
        out.println("  		   }");
        out.println("</SCRIPT>");
        out.println("  <BODY onload='fnIni();' style='BORDER: white 1px solid; FONT-FAMILY: Verdana,Tahoma; FONT-SIZE: 10pt;'>");
        out.print(!"".equals(mensaje) ? mensaje : arrLResult.get(0));
        out.println("   <br><br><input id=\"pbRegresa\" type=\"button\" value=\"Regresar\" onclick=\"bClicBtn = true;regresar();\"/>");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }
}
