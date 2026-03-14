package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.FortimaxBusinessLogic;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "GestionResultadoAplicacionServlet", urlPatterns = { "/showexpedients" })
public class GestionResultadoAplicacionServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionResultadoAplicacionServlet.class);

    private String jniName = null;

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
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");
        HttpSession session = req.getSession(false);
        if (session == null) {
            out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
            out.flush();
            out.close();
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
            out.flush();
            out.close();
            return;
        }
        String selectId = req.getParameter("select");
        if (selectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
            return;
        }
        boolean withGabinete = "true".equals(req.getParameter("expedient"));
        Fortimax fimx = new Fortimax(selectId);
        //Ethiel, le cambie de no a yes para que se puedan regresar al resultado de la busqueda ya que es muy incomodo estar llenando simepre
        resp.setHeader("Cache-Control", "yes-cache");
        //Ethiel, le cambie de no a yes para que se puedan regresar al resultado de la busqueda ya que es muy incomodo estar llenando simepre
        resp.setHeader("Pragma", "yes-cache");
        resp.setHeader("Expires", "-1");
        try {
            FortimaxBusinessLogic fbl = new FortimaxBusinessLogic(jniName);
            Descripcion[] dsc = fbl.getDescripcion(fimx.getTituloAplicacion());
            Map map = new HashMap();
            if (withGabinete) {
                String[] values = { "4", null, String.valueOf(fimx.getIdGabinete()) };
                map.put("id_gabinete", values);
            } else {
                for (int i = 0; i < dsc.length; i++) {
                    String value = new String();
                    String name = dsc[i].getNombreCampoLower();
                    switch(dsc[i].getIdTipoDatos()) {
                        // Small Integer
                        case 3:
                        // Long Integer
                        case 4:
                        // Decimal
                        case 5:
                        // Double, Float
                        case 7:
                        // String
                        case 10:
                        case // Long String
                        12:
                            value = req.getParameter(dsc[i].getNombreCampoLower());
                            if (value == null)
                                value = new String();
                            break;
                        case // Fecha
                        8:
                            String dia = req.getParameter("_day_" + dsc[i].getNombreCampoLower());
                            String mes = req.getParameter("_mes_" + dsc[i].getNombreCampoLower());
                            String year = req.getParameter("_year_" + dsc[i].getNombreCampoLower());
                    }
                    String[] values = { String.valueOf(dsc[i].getIdTipoDatos()), String.valueOf(dsc[i].getIndiceTipo()), value };
                    map.put(name, values);
                }
            }
            //String[][] data = fbl.getQueryByExampleAplicacionData(fimx.getTituloAplicacion(), map);//Ethiel se cambia por la linea d eabajo
            String[][] data = fbl.getQueryByExampleAplicacionDataEnConsulta(fimx.getTituloAplicacion(), map);
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Resultado de Consulta Expediente</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/fortimax_sistema.css\">");
            out.println("<style type=\"text/css\" title=\"currentStyle\">");
            out.println("@import \"../../../../../WebContent/Generador/css/demo_page.css\";");
            out.println("@import \"../../../../../WebContent/Generador/css/demo_table_jui.css\";");
            out.println("@import \"../../../../../WebContent/Generador/themes/smoothness/jquery-ui-1.8.4.custom.css\";");
            out.println("</style>");
            out.println("<link type=\"text/css\" href=\"../../../../../css/gestion.css\" rel=\"stylesheet\">");
            out.println("<link type=\"text/css\" href=\"../../../../../css/scrolltable.css\" rel=\"stylesheet\">");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery-1.6.2.min.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.jeditable-1.6.2.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.dataTables.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.dataTables.editable-1.3.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.ui.datepicker.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery-ui-1.8.16.custom.min.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.ui.core\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.ui.widget.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.ui.tabs.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.formatCurrency.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery.formatCurrency.all.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"../../../../../WebContent/Generador/js/jquery-1.2.6.js\"></script>");
            if (dsc == null) {
                out.println("</head>");
                out.println("<body leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\" marginwidth=\"0\" " + "marginheight=\"0\" scroll=\"no\">");
                out.println("<table>");
                out.println("<tr>");
                out.println("<td>");
                out.println("<h3>La gaveta no tiene campos definidos</h3>");
                out.println("</td>");
                out.println("</tr>");
                out.println("</table>");
                out.println("</body>");
                out.println("</html>");
                out.flush();
                out.close();
                return;
            }
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/scrolltable.css\">");
            out.println("<script type=\"text/javascript\">");
            out.println("$(document).ready(function() {");
            out.println("var oTable = $('#dt_consulta').dataTable({");
            out.println("aaSorting: [[1, \"asc\"]],");
            out.println("bJQueryUI: true,");
            out.println("iDisplayLength: 20,");
            out.println("sPaginationType: \"full_numbers\",");
            out.println("sScrollY: 457,");
            out.println("oLanguage: {");
            out.println("sProcessing: \"Procesando...\",");
            out.println("sLengthMenu: \"Mostrar _MENU_ registros\",");
            out.println("sZeroRecords: \"No hay registros a mostrar\",");
            out.println("sEmptyTable: \"No hay datos en la tabla\",");
            out.println("sLoadingRecords: \"Cargando...\",");
            out.println("sInfo: \"Registros _START_ al _END_ de _TOTAL_\",");
            out.println("sInfoEmpty: \"Registro 0 al 0 de 0\",");
            out.println("sInfoFiltered: \"(filtado de _MAX_ registros)\",");
            out.println("sInfoPostFix: \"\",");
            out.println("sInfoThousands: \",\",");
            out.println("sSearch: \"Buscar:\",");
            out.println("oPaginate: {");
            out.println("sFirst:    \"Primero\",");
            out.println("sPrevious: \"Ant.\",");
            out.println("sNext:     \"Sigte.\",");
            out.println("sLast:     \"&Uacute;ltimo\"}");
            out.println("	}");
            out.println("	});");
            out.println("});");
            out.println("</script>");
            /*out.println("<style>");
			out.println("div.tableContainer {");
			if (withGabinete)
				out.println("	height: 96%;");
			else
				out.println("	height: 100%;");
			out.println("}");
			out.println("</style>");*/
            out.println("</head>");
            out.println("<body leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\" marginwidth=\"0\" " + "marginheight=\"0\">");
            if (withGabinete) {
                // if (UsuarioManager.tienePermisos(fimx.getTituloAplicacion(),
                // u.getNombreUsuario(), 4)) {
                out.println("<table align=\"right\">");
                out.println("<tr>");
                out.println("<td>");
                out.println("<a href=\"javascript:if (confirm('Realmente desea eliminar este expediente?')){" + "window.open('delexpedient?select=" + selectId + "&id_gabinete=" + fimx.getIdGabinete() + "','main')}\" onmouseover=\"self.status='Eliminar Expediente'; return true;\">Eliminar");
                out.println("</a>");
                out.println("</td>");
                out.println("<td>");
                out.println("<a href=\"getdrawer?select=" + selectId + "&id_gabinete=" + fimx.getIdGabinete() + "\" onmouseover=\"self.status='Modificar Expediente'; return true;\">Modificar");
                out.println("</a>");
                out.println("</td>");
                out.println("</tr>");
                out.println("</table>");
                // }
            }
            out.println("<div id=\"tableContainer\" class=\"tableContainer\">");
            out.println("<table id=\"dt_consulta\" class=\"display\">");
            out.println("<thead>");
            out.println("<tr>");
            for (int i = 0; i < dsc.length; i++) {
                out.println("<th>" + dsc[i].getNombreColumna() + "</th>");
            }
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody class=\"display\">");
            if (data.length == 0) {
                out.println("<tr>");
                out.println("<td align=\"center\" colspan=\"" + dsc.length + "\">Sin informaci&oacute;n</td>");
                out.println("</tr>");
            }
            for (int i = 0; i < data.length; i++) {
                out.println("<tr>");
                Map m = new Hashtable();
                try {
                    //if(Integer.parseInt(data[i][0])!=-1)//Ethiel, los que ya tienen gabinete consulta el caso como siempre lo habia hecho
                    m = fbl.consultaCasoOperacion(fimx.getTituloAplicacion(), Integer.parseInt(data[i][0]));
                    //					else{ //los que aun no tienen gabinete le ponemos directamente el id_caso y id_caso_oper
                    //						m.put("id_caso", data[i][2].trim());
                    //						m.put("id_caso_oper", 1); //Cuando no han guardado siempre estan en la oper 1
                    //					}
                } catch (GestionException exc) {
                    log.warn("No se recupero caso operacion para (usr[" + u.getLogin() + "], app[" + fimx.getTituloAplicacion() + "], idGab[" + data[i][0] + "])", exc);
                    continue;
                } catch (NumberFormatException exc) {
                    log.warn("idGab no valido [" + data[i][0] + "]", exc);
                    continue;
                }
                if (m.isEmpty()) {
                    log.warn("Object: {}", "No se recupero caso operacion para (usr[" + u.getLogin() + "], app[" + fimx.getTituloAplicacion() + "], idGab[" + data[i][0] + "])");
                    continue;
                }
                for (int j = 1; j < data[i].length; j++) {
                    //System.out.println("data[i][0]++++++"+data[i].length);
                    out.println("<td align=\"center\">" + ((withGabinete) ? "" : "<a href=\"caso/show-caso?" + PRM_CASE + "=" + m.get("id_caso") + "&" + PRM_CASE_OPER + "=" + m.get("id_caso_oper") + "\">") + ((data[i][j] == null || (Integer.parseInt(data[i][0]) == -1 && j == 2)) ? "" : data[i][j]) + ((withGabinete) ? "" : "</a>") + "</td>");
                }
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        } catch (GestionException exc) {
            throw new ServletException(exc);
        } finally {
            out.close();
        }
    }
}
