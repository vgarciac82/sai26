package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.syc.gestion.util.PaginaData;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GestionResultadoBusquedaAvanzada", urlPatterns = { "/showexpedientsBusquedaAvanzada" })
public class GestionResultadoBusquedaAvanzada extends HttpServlet implements GestionInterface {

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
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        String param_consulta = "";
        String fecha_de = "";
        String fecha_a = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        resp.setContentType("text/html");
        String u_login = req.getParameter("u_login");
        log.info("u_login=" + u_login);
        param_consulta = "u_login=" + u_login;
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
        param_consulta += "&select=" + selectId;
        boolean withGabinete = "true".equals(req.getParameter("expedient"));
        param_consulta += "&expedient=" + req.getParameter("expedient");
        Fortimax fimx = new Fortimax(selectId);
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "-1");
        String tipoasunto = req.getParameter("tipoasunto");
        param_consulta += "&tipoasunto=" + req.getParameter("tipoasunto");
        PaginaData param_pd = new PaginaData();
        param_pd.setTamanoPaginas(80);
        param_pd.setNumeroPagina(Integer.parseInt(req.getParameter("pagina")));
        try {
            // 1. Genera Lista de campos de la Gaveta (para los que indico un valor en el criterio de seleccion)
            FortimaxBusinessLogic fbl = new FortimaxBusinessLogic(jniName);
            Map map = new HashMap();
            Descripcion[] dsc;
            dsc = fbl.getDescripcionAvanzada(fimx.getTituloAplicacion(), tipoasunto);
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
                            value = req.getParameter(dsc[i].getNombreCampoLower());
                            if (value == null || "".equals(value)) {
                            } else {
                                //aqui convertimos la fecha de dd/mm/yyy a yyyy/mm/dd
                                Date dfechaInicio = new Date();
                                Date dfechaFin = new Date();
                                fecha_de = req.getParameter(dsc[i].getNombreCampoLower());
                                fecha_a = req.getParameter(dsc[i].getNombreCampoLower() + "_a");
                                try {
                                    dfechaInicio = sdf1.parse(fecha_de);
                                    dfechaFin = sdf1.parse(fecha_a);
                                } catch (ParseException pe) {
                                    //do nothing!!!
                                    pe.printStackTrace();
                                }
                                fecha_de = sdf2.format(dfechaInicio);
                                fecha_a = sdf2.format(dfechaFin);
                            }
                            break;
                    }
                    String[] values = { String.valueOf(dsc[i].getIdTipoDatos()), String.valueOf(dsc[i].getIndiceTipo()), value };
                    if (dsc[i].getIdTipoDatos() == 8) {
                        param_consulta += "&" + name + "=" + fecha_de;
                        param_consulta += "&" + name + "_a=" + fecha_a;
                    } else {
                        param_consulta += "&" + name + "=" + value;
                    }
                    map.put(name, values);
                }
            }
            //param_consulta += "&pagina=";
            // 2. Obtiene lista de Expedientes en la Gaveta con el criterio seleccionado
            //String[][] data = fbl.getQueryByExampleAplicacionDataAvanzada(fimx.getTituloAplicacion(), u_login, map, fecha_de, fecha_a, tipoasunto);
            //PARA OBTENER LA CONSULTA COMO XML!!!
            //data[0][0] = fbl.getQBEAplicacionDataAvanzadaXML(fimx.getTituloAplicacion(), u_login, map, fecha_de, fecha_a, tipoasunto);
            PaginaData pd = fbl.getQueryByExampleAvanzadaData(fimx.getTituloAplicacion(), u_login, map, fecha_de, fecha_a, tipoasunto, param_pd);
            pd.setTamanoPaginas(param_pd.getTamanoPaginas());
            String[][] data = pd.getData();
            pd.setParamConsulta(param_consulta);
            // 3. Produce HTML de respuesta (con la lista de expedientes)
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Resultado de Consulta Expediente</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/fortimax_sistema.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/scrolltable.css\"/>");
            out.println("<script language=\"javascript\" src=\"js/scrolltable.js\"/>");
            out.println("<script language=\"javascript\" type=\"text/javascript\" src=\"js/fortimax_sistema.js\"/>");
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
            out.println("<script language=\"javascript\" src=\"js/scrolltable.js\">");
            out.println("function doRegresar()");
            out.println("{");
            out.println("	var accion = parseInt(document.datawork.h_accion.value);");
            out.println("	var content_iframe = parent;");
            out.println("	if (accion == 0)");
            out.println("		content_iframe.window.location.reload();");
            out.println("	else");
            out.println("		datawork.window.location.reload();");
            out.println("}");
            out.println("</script>");
            out.println("<style>");
            out.println("div.tableContainer {");
            if (withGabinete)
                out.println("	height: 96%;");
            else
                out.println("	height: 98%;");
            out.println("}");
            out.println("</style>");
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
            if (data.length > 0) {
                //Script para avanzar en las pginas
                //Paginacion p = new Paginacion();
                out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" heigth=\"18\">");
                out.println("<thead >");
                out.println("<tr>");
                out.println((param_pd.getNumeroPagina() == 0) ? "<th><<&nbsp</th>" : "<th><a href=\"./showexpedientsBusquedaAvanzada?" + pd.getParamConsulta() + "&pagina=0\" target=\"_self\"><<&nbsp</a></th>");
                out.println("<th>&nbsp;</th>");
                out.println("<th>&nbsp<a href=\"./showexpedientsBusquedaAvanzada?" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() > 0) ? param_pd.getNumeroPagina() - 1 : 0) + "\" target=\"_self\"><</a>&nbsp&nbsp</th>");
                for (int i = 0; i < pd.getTotalPaginas() - 1; i++) {
                    out.println((param_pd.getNumeroPagina() == i) ? "<th>&nbsp;" + (i + 1) + "</th>" : "<th>&nbsp;<a href=\"./showexpedientsBusquedaAvanzada?" + pd.getParamConsulta() + "&pagina=" + i + "\" target=\"_self\">" + (i + 1) + "</a></th>");
                }
                out.println("<th>&nbsp&nbsp<a href=\"./showexpedientsBusquedaAvanzada?" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() < pd.getTotalPaginas()) ? param_pd.getNumeroPagina() + 1 : pd.getTotalPaginas()) + "\" target=\"_self\">></a>&nbsp</th>");
                out.println("<th>&nbsp;</th>");
                out.println((param_pd.getNumeroPagina() == pd.getTotalPaginas()) ? "<th>&nbsp>></th>" : "<th><a href=\"./showexpedientsBusquedaAvanzada?" + pd.getParamConsulta() + "&pagina=" + pd.getTotalPaginas() + "\" target=\"_self\">&nbsp>></th>");
                out.println("</thead>");
                out.println("</table>");
                //out.println(Paginacion.);
                //out.println(getEncabezadoPaginacion( pd, param_pd).toString());
            }
            out.println("<div id=\"tableContainer\" class=\"tableContainer\">");
            out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"scrollTable\">");
            out.println("<thead class=\"fixedHeader\" id=\"fixedHeader\">");
            out.println("<tr>");
            out.println("<th style=\"font-weight: bold;\"><font size=\"1\">&nbsp;</th>");
            for (int i = 0; i < dsc.length; i++) {
                out.println("<th align=\"center\" style=\"font-weight: bold;\"><font size=\"1\">" + dsc[i].getNombreColumna().toUpperCase() + "</font></th>");
                //out.println("<th align=\"center\" >" + (("".equals(GestionInterface.APP_LST_CAMPOS_AVANZADA[1][i])? dsc[i].getNombreColumna().toUpperCase(): GestionInterface.APP_LST_CAMPOS_AVANZADA[1][i]) + "</th>");
            }
            //GAF 2010-05-06
            out.println("<th align=\"center\" style=\"font-weight: bold;\"><font size=\"1\">&Uacute;LTIMA FECHA<br>DE ENV&Iacute;O</font></th>");
            out.println("<th>&nbsp;</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody class=\"scrollContent\">");
            if (data.length == 0) {
                out.println("<tr>");
                out.println("<td align=\"center\" colspan=\"" + dsc.length + "\">Sin informaci&oacute;n</td>");
                out.println("</tr>");
            }
            out.println("<tr>");
            out.println("</tr>");
            for (int i = 0; i < data.length; i++) {
                out.println("<tr class=\"" + (((i % 2) == 0) ? "alternateRow" : "normalRow") + "\">");
                out.println("<td align=\"center\"><font size=\"1\">" + String.valueOf(i + 1 + (param_pd.getNumeroPagina() * param_pd.getTamanoPaginas())) + "</font></td>");
                for (int j = 1; j < data[i].length; j++) {
                    Map m = new Hashtable();
                    try {
                        m = fbl.consultaCasoOperacion(fimx.getTituloAplicacion(), Integer.parseInt(data[i][0]));
                    } catch (GestionException exc) {
                        log.warn("No se recupero caso operacion para (usr[" + u.getLogin() + "], app[" + fimx.getTituloAplicacion() + "], idGab[" + data[i][0] + "])", exc);
                        continue;
                    } catch (NumberFormatException exc) {
                        log.warn("idGab no valido [" + data[i][0] + "]", exc);
                        continue;
                    }
                    if (m.isEmpty()) {
                        log.warn("No se recupero caso operacion para (usr[" + u.getLogin() + "], app[" + fimx.getTituloAplicacion() + "], idGab[" + data[i][0] + "])");
                        continue;
                    }
                    // Miguel
                    // Fecha: 19/01/2010
                    // formato para la fechas
                    if (j == 10 || j == 11 || j == 12 || j == 13) {
                        data[i][j] = fnFormatDate(data[i][j], false);
                    }
                    if (j == data[i].length - 1) {
                        //CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
                        //Caso c = cbl.getCaso(data[i][1]);
                        String URLseguimiento = req.getScheme() + "://" + req.getServerName() + ((req.getServerPort() == 80) ? "" : ":" + req.getServerPort()) + req.getContextPath() + "/caso/seguimiento.jsp?" + "mg=true" + "&tc=" + m.get("id_tc") + "&c=" + m.get("id_caso");
                        out.println("<td align=\"center\">" + ((withGabinete) ? "" : "<a target=\"_blank\" href=\"" + URLseguimiento + "\"><font size=\"1\">seguimiento</font>") + ((withGabinete) ? "" : "</a>") + "</td>");
                    } else {
                        out.println("<td align=\"center\">" + ((withGabinete) ? "" : "<a target=\"_blank\" href=\"caso/show-caso?" + PRM_CASE + "=" + m.get("id_caso") + "&" + PRM_CASE_OPER + "=" + m.get("id_caso_oper") + "\"><font size=\"1\">") + ((data[i][j] == null) ? "" : java.net.URLDecoder.decode(java.net.URLEncoder.encode(data[i][j], resp.getCharacterEncoding()), resp.getCharacterEncoding())) + ((withGabinete) ? "" : "</font></a>") + "</td>");
                    }
                }
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("<input type=\"submit\" value=\"Regresar\" " + "title=\"Dejar en pausa esta operaci&oacute;n\">");
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

    public static String fnFormatDate(String strDate, boolean addTime) {
        strDate = strDate.replaceAll("/", "-");
        String[] strTemp = null;
        String[] strDia = null;
        String[] strHora = null;
        if (strDate.equals("")) {
            return strDate;
        }
        strTemp = strDate.split(" ");
        strDia = strTemp[0].split("-");
        if (strDate.indexOf(":") != -1) {
            strHora = strTemp[1].split(":");
        }
        if (strDia[0].length() != 4) {
            return strDate;
        }
        strHora[2] = strHora[2].substring(0, strHora[2].indexOf("."));
        strDate = strDia[2] + "-" + strDia[1] + "-" + strDia[0] + " ";
        if (addTime) {
            strDate += strHora[0] + ":" + strHora[1] + ":" + strHora[2];
        }
        return strDate;
    }

    public StringBuffer getEncabezadoPaginacion(PaginaData pd, PaginaData param_pd) {
        StringBuffer sb = new StringBuffer();
        sb.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" heigth=\"18\">");
        sb.append("<thead >");
        sb.append("<tr>");
        sb.append((param_pd.getNumeroPagina() == 0) ? "<th><<&nbsp</th>" : "<th><a href=\"" + pd.getParamConsulta() + "&pagina=0\" target=\"content-iframe\"><<&nbsp</a></th>");
        sb.append("<th>&nbsp;</th>");
        sb.append("<th>&nbsp<a href=\"" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() > 0) ? param_pd.getNumeroPagina() - 1 : 0) + "\" target=\"content-iframe\"><</a>&nbsp&nbsp</th>");
        if (pd.getTotalPaginas() > 0) {
            for (int i = 0; i <= pd.getTotalPaginas(); i++) {
                sb.append((param_pd.getNumeroPagina() == i) ? "<th>&nbsp;" + (i + 1) + "</th>" : "<th>&nbsp;<a href=\"" + pd.getParamConsulta() + "&pagina=" + i + "\" target=\"content-iframe\">" + (i + 1) + "</a></th>");
            }
        } else
            sb.append("<th>&nbsp;1</th>");
        sb.append("<th>&nbsp&nbsp<a href=\"" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() < pd.getTotalPaginas()) ? param_pd.getNumeroPagina() + 1 : pd.getTotalPaginas()) + "\" target=\"content-iframe\">></a>&nbsp</th>");
        sb.append("<th>&nbsp;</th>");
        sb.append((param_pd.getNumeroPagina() == pd.getTotalPaginas()) ? "<th>&nbsp>></th>" : "<th><a href=\"" + pd.getParamConsulta() + "&pagina=" + pd.getTotalPaginas() + "\" target=\"content-iframe\">&nbsp>></th>");
        sb.append("</thead>");
        sb.append("</table>");
        return sb;
    }
}
