package com.axtel.sai.sicove.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import com.axtel.sai.sicove.entities.MonthlyAssinationSummary;
import com.axtel.sai.sicove.entities.MonthlyDetailAssinationSummary;
import com.axtel.sai.sicove.repositories.impl.JDBCMonthlyAssinationRepository;
import com.axtel.sai.sicove.services.MonthlyAssinationService;
import com.axtel.sai.sicove.services.impl.JDBCMonthlyAssinationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;

@WebServlet(urlPatterns = { "/SICOVE/MonthlyAssinationSummary", "/SICOVE/ExportMonthlyAssinationSummary" })
public class MonthlyAssinationSummaryController extends HttpServlet {

    private static final long serialVersionUID = -2431056139471120267L;

    private MonthlyAssinationService monthlyAssinationService;

    private String jniName = "";

    private static final Logger log = LoggerFactory.getLogger(MonthlyAssinationSummaryController.class);

    private static final String SUMMARY = "MonthlyAssinationSummary";

    private static final String DOWNLOAD_SUMARY = "ExportMonthlyAssinationSummary";

    private ObjectMapper mapper = new ObjectMapper();

    private static File TEMPLATE_REPORT = null;

    @Override
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
            if (TEMPLATE_REPORT == null) {
                log.info("Loading Fueal assignation Summary template");
                URL resource = getClass().getClassLoader().getResource("ExcelTemplates/CedulaAsignacionCombustible.xls");
                if (resource == null) {
                    throw new IllegalArgumentException("file CedulaAsignacionCombustible.xls not found!");
                } else {
                    TEMPLATE_REPORT = new File(resource.toURI());
                }
            }
        } catch (NamingException | URISyntaxException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        monthlyAssinationService = new JDBCMonthlyAssinationService(jniName, new JDBCMonthlyAssinationRepository());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        if (SUMMARY.equals(action))
            getSummary(req, resp);
        else if (DOWNLOAD_SUMARY.equals(action)) {
            try {
                doDownload(req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Util.sendHTMLErrorMsg(resp, e);
            }
        }
    }

    private void doDownload(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int month = Integer.parseInt(req.getParameter("month"));
        int account = Integer.parseInt(req.getParameter("account"));
        log.info("Object: {}", "Generating report for account id " + account + " month " + month);
        File workBook = Util.copyFile(TEMPLATE_REPORT, Util.createTempFile("monthly_fuel_asignation_", ".xls"));
        monthlyAssinationService.generateAssginationReport(workBook, account, month);
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(workBook.getName());
        Util.doDownload(resp, workBook.getAbsolutePath(), "Reporte_Asignacion_Mensual_" + Util.nombreDeMes(month) + ".xls", mimetype);
    }

    private void getSummary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int month = Integer.parseInt(req.getParameter("month"));
            int account = Integer.parseInt(req.getParameter("account"));
            Map<String, Object> result = new HashMap<>();
            log.info("Object: {}", "Querying Assignation Summary for : Month " + month + " Account " + account);
            MonthlyAssinationSummary monthlySummary = monthlyAssinationService.getMonthlySummary(account, month);
            List<MonthlyDetailAssinationSummary> monthlyDetail = monthlyAssinationService.getMonthlyDetailSummary(account, month);
            result.put("summary", monthlySummary);
            result.put("detail", monthlyDetail);
            Util.sendJSON(resp, result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(resp, e);
        }
    }
}
