package com.syc.gestion.reportes.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.core.ReporteConf;
import com.syc.gestion.servlet.ActualizaAplicacionServlet;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteToChartServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ActualizaAplicacionServlet.class);

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
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("../index.jsp");
            // <script language="javascript">self.top.location.href =
            // "../index.jsp";</script>
            return;
        }
        String rpt_excel = (String) session.getAttribute(GestionInterface.ATT_EXP_BODY);
        //String rpt_excel = (String) request.getParameter("rptExcel");
        if (rpt_excel == null) {
            log.warn("Parametros incompletos: Reporte excel ");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        ReporteConf in_rc = new ReporteConf();
        String idreporte = req.getParameter("id");
        if (idreporte == null) {
            log.warn("Parámetros incompletos: id ");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        in_rc.setId(Integer.parseInt(idreporte));
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        String[] seriesNames = new String[] { "2001", "2002" };
        String[] categoryNames = new String[] { "First Quater", "Second Quater" };
        Number[][] categoryData = new Integer[][] { { new Integer(20), new Integer(35) }, { new Integer(40), new Integer(60) } };
        CategoryDataset categoryDataset = new DefaultCategoryDataset();
        //categoryDataset.setSeries(seriesNames);
        /*
		                                        (seriesNames,
		                                         categoryNames,
		                                         categoryData);
		                                         */
        /*
		JFreeChart chart = ChartFactory.createVerticalBarChart3D
		                     ("Sample Category Chart", // Title
		                      "Quarters",              // X-Axis label
		                      "Sales",                 // Y-Axis label
		                      categoryDataset,         // Dataset
		                      true                     // Show legend
		                     );
		                     */
    }
    /*
	private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {

		int length = 0;
		File f = new File(filename);
		ServletOutputStream out = resp.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(original_filename);

		resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		resp.setContentLength((int) f.length());

		//resp.addHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\";");
		//resp.addHeader("Content-Disposition", "attachement; filename=\"" + original_filename + "\";");
		resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");

		byte[] bbuf = new byte[5 * 1024]; // 5K buffer
		DataInputStream in = new DataInputStream(new FileInputStream(f));

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			out.write(bbuf, 0, length);
		}

		in.close();
		out.flush();
		out.close();
	}*/
}
