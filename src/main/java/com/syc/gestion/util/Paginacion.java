package com.syc.gestion.util;

import java.io.Serializable;
import java.util.Base64;

public class Paginacion implements Serializable {

    private final static long serialVersionUID = 1;

    public static StringBuffer getEncabezadoHTML(PaginaData pd, PaginaData param_pd) {
        StringBuffer sb = new StringBuffer();
        String filtro = ("".equals(pd.getBuscarFiltro()) ? "" : (pd.getBuscarFiltro() == null ? "" : "&bfolio=" + pd.getBuscarFiltro()));
        if (pd.getTotalPaginas() > 0) {
            sb.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" heigth=\"18\">");
            sb.append("<thead >");
            sb.append("<tr>");
            sb.append((param_pd.getNumeroPagina() == 0) ? "<th><<&nbsp</th>" : "<th><a href=\"" + pd.getParamConsulta() + "&pagina=0" + filtro + "\" target=\"content-iframe\"><<&nbsp</a></th>");
            sb.append("<th>&nbsp;</th>");
            sb.append("<th>&nbsp<a href=\"" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() > 0) ? param_pd.getNumeroPagina() - 1 : 0) + filtro + "\" target=\"content-iframe\"><</a>&nbsp&nbsp</th>");
            if (pd.getTotalPaginas() > 0) {
                for (int i = 0; i <= pd.getTotalPaginas(); i++) {
                    sb.append((param_pd.getNumeroPagina() == i) ? "<th>&nbsp;" + (i + 1) + "</th>" : "<th>&nbsp;<a href=\"" + pd.getParamConsulta() + "&pagina=" + i + filtro + "\" target=\"content-iframe\">" + (i + 1) + "</a></th>");
                }
            } else
                sb.append("<th>&nbsp;1</th>");
            sb.append("<th>&nbsp&nbsp<a href=\"" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() < pd.getTotalPaginas()) ? param_pd.getNumeroPagina() + 1 : pd.getTotalPaginas()) + filtro + "\" target=\"content-iframe\">></a>&nbsp</th>");
            sb.append("<th>&nbsp;</th>");
            sb.append((param_pd.getNumeroPagina() == pd.getTotalPaginas()) ? "<th>&nbsp>></th>" : "<th><a href=\"" + pd.getParamConsulta() + "&pagina=" + pd.getTotalPaginas() + filtro + "\" target=\"content-iframe\">&nbsp>></th>");
            sb.append("</thead>");
            sb.append("</table>");
        }
        return sb;
    }

    public static StringBuffer getEncabezadoCSS(PaginaData pd, PaginaData param_pd) {
        StringBuffer sb = new StringBuffer();
        String attr = " onmouseover=\"window.status='';return true;\"  onmouseout=\"window.status='';return true;\" ";
        String filtro = ("".equals(pd.getBuscarFiltro()) ? "" : (pd.getBuscarFiltro() == null ? "" : "&bfolio=" + pd.getBuscarFiltro()));
        String target = ("".equals(pd.getTarget()) ? "_self" : pd.getTarget());
        if (pd.getTotalPaginas() > 0) {
            sb.append("<div id=\"page_footer\">");
            sb.append("<div id=\"pag\">");
            sb.append("<a " + (param_pd.getNumeroPagina() == 0 ? "class=\"sel\"" : "class=\"pP\"") + " href=\"" + pd.getParamConsulta() + "&pagina=0" + filtro + "\"" + attr + " target=\"" + target + "\"><<</a>");
            sb.append("<a href=\"" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() > 0) ? param_pd.getNumeroPagina() - 1 : 0) + filtro + "\"" + attr + " target=\"" + target + "\"><</a>");
            if (pd.getTotalPaginas() > 0) {
                for (int i = 0; i <= pd.getTotalPaginas(); i++) {
                    sb.append("<a " + ((param_pd.getNumeroPagina() == i) ? "class=\"sel\"" : "") + " href=\"" + pd.getParamConsulta() + "&pagina=" + i + filtro + "\"" + attr + " target=\"" + target + "\">" + (i + 1) + "</a>");
                }
            } else
                sb.append("&nbsp;1");
            sb.append("<a href=\"" + pd.getParamConsulta() + "&pagina=" + ((param_pd.getNumeroPagina() < pd.getTotalPaginas()) ? param_pd.getNumeroPagina() + 1 : pd.getTotalPaginas()) + filtro + "\"" + attr + " target=\"" + target + "\">></a>");
            sb.append("<a class=\"dis\" href=\"#\"" + attr + " target=\"" + target + "\">" + (param_pd.getNumeroPagina() == 0 ? 1 : (pd.getTamanoPaginas() * param_pd.getNumeroPagina()) + 1) + "..." + (((param_pd.getNumeroPagina() + 1) * pd.getTamanoPaginas()) > pd.getNumeroRegistros() ? pd.getNumeroRegistros() : ((param_pd.getNumeroPagina() + 1) * pd.getTamanoPaginas())) + " de " + pd.getNumeroRegistros() + "</a>");
            sb.append("</div>");
            sb.append("</div>");
        }
        return sb;
    }
}
