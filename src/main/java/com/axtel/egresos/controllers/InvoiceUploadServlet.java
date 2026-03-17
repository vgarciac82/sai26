package com.axtel.egresos.controllers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import com.axtel.egresos.entities.InvoiceSubmissionRequest;
import com.axtel.egresos.entities.MassPaymentResult;
import com.axtel.egresos.services.MassPaymentSupplierService;
import com.axtel.egresos.services.impl.MassPaymentSupplierServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet("/payments/masivePayment")
@MultipartConfig
public class InvoiceUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 7619330898481941197L;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private MassPaymentSupplierService paymentProcessor;

    private UsuarioBusinessLogic userService;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(InvoiceUploadServlet.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        paymentProcessor = new MassPaymentSupplierServiceImpl(jniName);
        userService = new UsuarioBusinessLogic(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.info("Recepción de archivo iniciada");
            Part invoicesFilePart = request.getPart("invoices");
            File fileInvoices = Util.saveFile(invoicesFilePart);
            log.info("Object: {}", "Archivo guardado en " + fileInvoices.getAbsolutePath());
            Part opinionsFilePart = request.getPart("opinions");
            File fileOpinions = Util.saveFile(opinionsFilePart);
            log.info("Object: {}", "Archivo guardado en " + fileOpinions.getAbsolutePath());
            String budgetItem = request.getParameter("budgetItem");
            String employeeLoading = request.getParameter("employeeLoading");
            int authEmployeeNumber = Integer.parseInt(request.getParameter("authEmployeeNumber"));
            int voBoEmployeeNumber = Integer.parseInt(request.getParameter("voBoEmployeeNumber"));
            log.debug("Object: " + String.valueOf("authEmployeeNumber=" + authEmployeeNumber + " voBoEmployeeNumber={" + voBoEmployeeNumber + "}"));
            Usuario user = new Usuario(employeeLoading);
            user = userService.getUsuario(user);
            Firmante authFirmante = new Firmante(authEmployeeNumber);
            Firmante voBoFirmante = new Firmante(voBoEmployeeNumber);
            FirmanteSuplente[] suplentes = new FirmanteSuplente[2];
            if (Boolean.parseBoolean(request.getParameter("isSubtitutionVoBo"))) {
                suplentes[0] = createFirmanteSuplente(request, "substitApproval");
            }
            if (Boolean.parseBoolean(request.getParameter("isSubtitutionAuth"))) {
                suplentes[1] = createFirmanteSuplente(request, "substitAuthorization");
            }
            log.debug("Firmantes suplentes procesados");
            InvoiceSubmissionRequest submission = new InvoiceSubmissionRequest(fileInvoices, fileOpinions, authFirmante, voBoFirmante, suplentes);
            submission.setBudgetItem(budgetItem);
            log.info("Procesando envío de pago masivo");
            List<MassPaymentResult> logResults = paymentProcessor.processSubmission(submission, user);
            log.info("Object: {}", "Procesamiento completado, {" + logResults.size() + "} resultados obtenidos");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(logResults));
        } catch (Exception e) {
            log.error("Error procesando la solicitud", e);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Error procesando la solicitud: " + e.getMessage() + "\"}");
        }
    }

    private FirmanteSuplente createFirmanteSuplente(HttpServletRequest request, String prefix) throws ParseException {
        int numeroEmpleado = Integer.parseInt(request.getParameter(prefix + "EmployeeNumber"));
        String folioOficio = request.getParameter(prefix + "OfficeNumber");
        Date fechaOficio = DATE_FORMAT.parse(request.getParameter(prefix + "OfficeDate"));
        String motivoSuplencia = request.getParameter(prefix + "Reason");
        FirmanteSuplente suplente = new FirmanteSuplente();
        suplente.setNumeroEmpleado(numeroEmpleado);
        suplente.setFolioOficio(folioOficio);
        suplente.setFechaOficio(fechaOficio);
        suplente.setMotivoSuplencia(motivoSuplencia);
        suplente.setTipoSuplencia(motivoSuplencia);
        return suplente;
    }
}
