package com.syc.gestion.reportes.core;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import  com.syc.admin.servlet.SeguridadCatalogos;
import com.syc.gestion.reportes.ReporteBussinesLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultaPolizasSelec extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConsultaPolizasSelec.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ConsultaPolizasSeleccionadas(request, response);
            response.sendRedirect("../admin/ConsultaPolizasResultado.jsp");
        } catch (Exception exc) {
            log.error("Consultando Polizas Seleccionadas", exc);
            throw new ServletException(exc);
        }
    }

    private void ConsultaPolizasSeleccionadas(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recibe parametros
        //		String strnoAcuerdo = request.getParameter("noAcuerdo").trim();
        //		String strnoFolioSiaff = request.getParameter("noFolioSiaff").trim();
        //		String strOpcion = request.getParameter("sOp").trim();
        String[] selection = request.getParameterValues("selection");
        ReporteBussinesLogic sbl = new ReporteBussinesLogic(jndiName);
        String num_checkbox = "";
        //		log.info("aplicaModifAMF - NoAcuerdo " + strnoAcuerdo );
        //		log.info("aplicaModifAMF - NoFolio " + strnoFolioSiaff );
        //		log.info("aplicaModifAMF - opcion " + strOpcion );
        if (selection != null)
            for (int i = 0; i < selection.length; i++) {
                num_checkbox = selection[i];
                log.info("ConsultaPolizasSeleccionadas - num_checkbox " + num_checkbox);
                if (num_checkbox == null) {
                    log.warn("No se encontro num_checkbox para " + selection[i]);
                    continue;
                }
                //				SeguridadCatalogos( request,  response);
                //				var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=PolizaConagua.jasper<%=paramPoliza%>";
                //				var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
                try {
                    log.info("Entrando a strOpcion=1, modificaSOL con la Solicitud " + num_checkbox);
                    //				          sbl.modificaSOL(num_checkbox, strnoAcuerdo, strnoFolioSiaff);
                } catch (Exception exc) {
                    log.error("aplicaModifAMF", exc);
                    throw new ServletException(exc);
                }
            }
    }
}
