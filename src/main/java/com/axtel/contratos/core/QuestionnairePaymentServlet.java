package com.axtel.contratos.core;

import java.io.IOException;
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
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "QuestionnarirePaymentSerlvet", urlPatterns = { "/questionnarire/PaymentAnswers" })
public class QuestionnairePaymentServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -1644166077419107778L;

    private static final Logger log = LogManager.getLogger(QuestionnairePaymentServlet.class);

    private String jniName;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        try {
            if (session == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            Caso c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null) {
                throw new Exception("Su sesion ha terminado.");
            }
            String tipoEgreso = c.getTipoCaso().getGavetaAsociada();
            int folio = Util.folio(c);
            EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
            QuestionnaireBussinessLogic qbl = new QuestionnaireBussinessLogic();
            EgresoEncabezado header = ebl.generaInstancia(tipoEgreso, folio);
            qbl.readAnswersPayments(header);
            List<QuestionnaireAnswer> answers = header.getQuestionnaireAnswers();
            Map<String, Object> answerMap = new LinkedHashMap<>();
            answerMap.put("success", true);
            answerMap.put("answers", answers);
            Util.sendJSONResponse(resp, answerMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Map<String, Object> answerMap = new LinkedHashMap<>();
            answerMap.put("success", false);
            answerMap.put("errorMsg", e.toString());
            Util.sendJSONResponse(resp, answerMap);
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
        } catch (Exception exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
