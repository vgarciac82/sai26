package com.axtel.contratos.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.LogManager;
import org.json.JSONObject;
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CuestionarioContrato", urlPatterns = { "/contratos/RegistraCuestionario" })
public class CuestionarioContratoServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 312507485274882929L;

    private static final Logger log = LogManager.getLogger(CuestionarioContratoServlet.class);

    private String jniName;

    private String reportPath;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        boolean success = false;
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        ContractQuestionnaire questionnaire = null;
        QuestionnaireBussinessLogic contractBL = null;
        try {
            if (session == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            questionnaire = new ContractQuestionnaire();
            questionnaire.setEmployeeNumber(req.getParameter("empleadoFirmante"));
            questionnaire.setIdRequest(req.getParameter("requestID"));
            contractBL = new QuestionnaireBussinessLogic(jniName, questionnaire);
            contractBL.setDocument(ContractQuestionnaire.APPLICATION);
            contractBL.setUsuario(u);
            contractBL.reSendEmail();
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("errorMsg", e.toString());
        } finally {
            questionnaire = null;
            contractBL = null;
        }
        try {
            result.put("success", success);
            JSONObject resultJSON = Util.toJson(result);
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.println(resultJSON.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        boolean success = false;
        ContractQuestionnaire questionnaire = null;
        QuestionnaireBussinessLogic contractBL = null;
        try {
            if (session == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            questionnaire = new ContractQuestionnaire();
            questionnaire.setStatus(SolicitudFirmaElectronica.AUT_SICOP);
            questionnaire.setEmployeeNumber(req.getParameter("empleadoFirmante"));
            questionnaire.setIdRequest(req.getParameter("requestID"));
            questionnaire.setCaptureEmployeeLogin(u.getLogin());
            String[] answersArr = req.getParameterValues("answer");
            if (answersArr != null && answersArr.length > 0) {
                List<QuestionnaireAnswer> answers = QuestionnaireAnswer.instanceList(answersArr);
                questionnaire.setAnswers(answers);
                questionnaire.setApply15D("1".equals(req.getParameter("aplicaArt15D")));
                questionnaire.setApplyQuestionnaire(true);
            } else {
                //No aplica cuestionario
                questionnaire.setApply15D(false);
                questionnaire.setApplyQuestionnaire(false);
            }
            contractBL = new QuestionnaireBussinessLogic(jniName, questionnaire);
            contractBL.setUsuario(u);
            contractBL.setReportPath(reportPath);
            contractBL.setDocument(ContractQuestionnaire.APPLICATION);
            contractBL.setCargaMasiva(false);
            contractBL.setFolder("Adjuntos");
            contractBL.setFileExtension("pdf");
            contractBL.setRefirma(false);
            contractBL.sendQuestionnaireSign();
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("errorMsg", e.toString());
        } finally {
            contractBL = null;
            questionnaire = null;
        }
        try {
            result.put("success", success);
            JSONObject resultJSON = Util.toJson(result);
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.println(resultJSON.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
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
            reportPath = getServletContext().getRealPath("Reportes");
        } catch (Exception exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
