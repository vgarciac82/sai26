package com.axtel.proveedores.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import com.axtel.proveedores.exception.ProveedorException;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.cuentasbancarias.CuentaBancaria;
import com.syc.cuentasbancarias.DocBancarioBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = { "/altaProveedor/registraCuentaBancaria", "/altaProveedor/actualizaArbol", "/altaProveedor/cuentasCapturadas", "/altaProveedor/cuentasAutorizadas" })
@MultipartConfig
public class CuentaBancariaAltaProveedorController extends HttpServlet {

    private static final long serialVersionUID = -4826362853947679839L;

    private static final Logger log = LoggerFactory.getLogger(CuentaBancariaAltaProveedorController.class);

    private String jniName;

    private DocBancarioBusinessLogic dbbl;

    private CasoBusinessLogic cbl = null;

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
        dbbl = new DocBancarioBusinessLogic(jniName);
        cbl = new CasoBusinessLogic(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        log.info("Adding document to expedient.");
        try {
            HttpSession session = request.getSession(false);
            if (session == null)
                throw new RuntimeException("Sin session. Ingrese nuevamente al sistema");
            Usuario user = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (user == null)
                throw new RuntimeException("Sin session. Ingrese nuevamente al sistema");
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            if (c == null)
                throw new RuntimeException("No hay session. Ingrese nuevamente al sistema");
            CuentaBancaria cuentaBancaria = new CuentaBancaria(request.getParameter("clabe"), request.getParameter("rfc"));
            cuentaBancaria.setBanco(request.getParameter("bancoNombre"));
            cuentaBancaria.setSucursal(request.getParameter("sucursal") == null ? "000" : request.getParameter("sucursal"));
            cuentaBancaria.setFolio(c.getFolio());
            cuentaBancaria.setCuenta(cuentaBancaria.getBanco() + "-" + cuentaBancaria.getCuentaBancaria());
            log.info("Object: {}", "Registrando Cuenta: " + cuentaBancaria);
            Part filePart = request.getPart("formFile");
            String fileName = filePart.getSubmittedFileName();
            File file = File.createTempFile("upload_", "_" + fileName);
            FileUtils.copyInputStreamToFile(filePart.getInputStream(), file);
            dbbl.insertaArchivo(c, user, file.getAbsolutePath(), cuentaBancaria.getBanco() + "-" + cuentaBancaria.getCuentaBancaria(), cuentaBancaria);
            ITree tree = cbl.getArbolCaso(c);
            session.setAttribute(GestionInterface.ATT_CASE, c);
            session.setAttribute("tree.model", tree);
            Map<String, String> result = new HashMap<>();
            result.put("success", "true");
            result.put("message", "El archivo se adjuntó correctamente.");
            Util.sendJSON(response, result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            Util.sendJSONError(response, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
        log.info("Object: {}", "Action: " + action);
        try {
            if ("actualizaArbol".equals(action)) {
                HttpSession session = request.getSession(false);
                if (session == null)
                    throw new RuntimeException("Sin session. Ingrese nuevamente al sistema");
                Usuario user = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                if (user == null)
                    throw new RuntimeException("Sin session. Ingrese nuevamente al sistema");
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                if (c == null)
                    throw new RuntimeException("No hay session. Ingrese nuevamente al sistema");
                ITree tree;
                tree = cbl.getArbolCaso(c);
                session.setAttribute(GestionInterface.ATT_CASE, c);
                session.setAttribute("tree.model", tree);
                Map<String, String> result = new HashMap<>();
                result.put("success", "true");
                result.put("message", "El archivo se adjuntó correctamente.");
                Util.sendJSON(response, result);
            } else if ("cuentasCapturadas".equals(action)) {
                String folio = request.getParameter("folio");
                List<Map<String, String>> cuentas = dbbl.getCuentasTemporales(folio);
                Util.sendJSON(response, cuentas);
            } else if ("cuentasAutorizadas".equals(action)) {
                String rfc = request.getParameter("rfc");
                List<Map<String, String>> cuentas = dbbl.getCuentasBeneficiario(rfc);
                Util.sendJSON(response, cuentas);
            }
        } catch (GestionException | ProveedorException e) {
            log.error(e.getMessage(), e);
            Util.sendJSONError(response, e);
        }
    }
}
