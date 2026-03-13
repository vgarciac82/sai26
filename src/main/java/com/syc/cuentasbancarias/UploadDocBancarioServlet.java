package com.syc.cuentasbancarias;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "UploadDocBancarioServlet", urlPatterns = { "/UploadDocBancario" })
public class UploadDocBancarioServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -795967476374791070L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = Logger.getLogger(UploadDocBancarioServlet.class);

    private static String TEMP_DIR = "";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        String msgRetorno = "", cuenta = "", RFC = "", banco = "", plaza = "", idBanco = "", cuentaBan = "", digVerificador = "", sucursal = "", folio = "";
        int statusCuenta = 0, statusSICOP = 0;
        Usuario u = null;
        Caso c = null;
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null || u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        List<?> fileItems = null;
        Iterator<?> iter = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        try {
            if (c.getIdGabinete() == -1) {
                AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                c.getCasoDato("FOLIO").setValor(c.getFolio());
                c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                c.getCasoDato("EJERCICIO_FISCAL").setValor(adbl.obtenEjercicioFiscal());
                c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                c.setIdGabinete(cbl.creaExpediente(u.getLogin(), c));
            }
            fileItems = Util.parseRequest(req, UploadDocBancarioServlet.TEMP_DIR, -1);
            iter = fileItems.iterator();
            String nombreArchivo = "";
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("cIdRFCB2".equals(item.getFieldName()))
                        RFC = item.getString();
                    else if ("cBancoH2".equals(item.getFieldName()))
                        idBanco = item.getString();
                    else if ("txtPlaza".equals(item.getFieldName()))
                        plaza = item.getString();
                    else if ("txtCuentaBancaria".equals(item.getFieldName()))
                        cuentaBan = item.getString();
                    else if ("txtDigitoVerificador".equals(item.getFieldName()))
                        digVerificador = item.getString();
                    else if ("cStatusCuentaH2".equals(item.getFieldName()))
                        statusCuenta = Integer.parseInt(item.getString());
                    else if ("dBancoH2".equals(item.getFieldName()))
                        banco = item.getString();
                    else if ("txtSucursal".equals(item.getFieldName()))
                        sucursal = item.getString();
                    else if ("nBCBEnviadoSICOPH2".equals(item.getFieldName()))
                        statusSICOP = Integer.parseInt(item.getString());
                    else if ("cFolio2".equals(item.getFieldName()))
                        folio = item.getString();
                    item.delete();
                } else {
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    nombreArchivo = item.getName().lastIndexOf(java.io.File.separatorChar) > 0 ? item.getName().substring(item.getName().lastIndexOf(java.io.File.separatorChar) + 1, item.getName().length()) : item.getName();
                    String extension = Util.getFileExtencion(nombreArchivo);
                    nombreDestino = FacturaUtils.generaNombreArchivoTemporal(UploadDocBancarioServlet.TEMP_DIR, nombreArchivo, extension);
                    log.info("Copiando archivo :" + nombreArchivo);
                    Util.copiaArchivo(archivoCargaStream, nombreDestino);
                    item.delete();
                }
            }
            cuenta = banco + "-" + cuentaBan;
            CuentaBancaria cuentaBancaria = new CuentaBancaria(cuenta, RFC, banco, plaza, idBanco, cuentaBan, digVerificador, statusCuenta, sucursal, statusSICOP, folio);
            insertaArchivo(c, u, nombreDestino, cuenta, cuentaBancaria);
            ITree tree = cbl.getArbolCaso(c);
            session.setAttribute(ATT_CASE, c);
            session.setAttribute("tree.model", tree);
            msgRetorno = "Archivo cargado exitosamente";
        } catch (Exception e) {
            log.error(e, e);
            msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
        } finally {
            if (archivoCargaStream != null)
                try {
                    archivoCargaStream.close();
                } catch (Exception e) {
                    log.error("Error cerrando flujo DataInputStream" + e);
                }
            archivoCargaStream = null;
            if (!"".equals(nombreDestino)) {
                File toDelete = new File(nombreDestino);
                if (!toDelete.delete())
                    toDelete.deleteOnExit();
            }
            session.setAttribute("RESULT", msgRetorno);
            try {
                jsonObj.put("MSG", msgRetorno);
                jsonObj.put("cNombreCuenta", cuenta);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
                out.close();
                out = null;
                jsonObj = null;
                arrayObj = null;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void insertaArchivo(Caso c, Usuario u, String rutaArchivo, String cuenta, CuentaBancaria cuentaBancaria) throws Exception {
        DocBancarioBusinessLogic dbbl = new DocBancarioBusinessLogic(jniName);
        dbbl.insertaArchivo(c, u, rutaArchivo, cuenta, cuentaBancaria);
    }

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
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/Facturas/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/Facturas/";
            log.info("Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
    }
}
