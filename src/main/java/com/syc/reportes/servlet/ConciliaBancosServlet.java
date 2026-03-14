package com.syc.reportes.servlet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
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
import org.apache.commons.fileupload2.core.FileItem;
import com.syc.contable.core.AdecuacionManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.egresos.ResponseJSON;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConciliaBancosServlet", urlPatterns = { "/servlet/ConciliaBancosServlet" })
public class ConciliaBancosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConciliaBancosServlet.class);

    private Connection conn = null;

    private static String tempDir = "";

    private String folioGenerator = null;

    private Usuario usuario;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("Object: {}", "folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + "upload" + File.separator;
            // tempDir = config.getServletContext().getRealPath("/") + "." +
            // File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = null;
        repConciliacionBancoServlet rep = new repConciliacionBancoServlet();
        String mensajeRetorno = "";
        try {
            session = request.getSession(false);
            if (session == null) {
                log.info("no hay sessión");
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null) {
                log.info("no hay sessión");
                response.sendRedirect("../index.jsp");
                return;
            }
            mensajeRetorno = layoutBanco(request, response, session);
            //response.sendRedirect("../Generador/ConciliaBancos.jsp" + mensajeRetorno);
            ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensajeRetorno) })));
            rep.sendJSONResponse(response, responseJSON);
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error: " + e, e);
            mensajeRetorno = e.toString().replace("java.lang.Exception: ", "");
            mensajeRetorno = mensajeRetorno.replace("com.microsoft.sqlserver.jdbc.SQLServerException:", "");
            //response.sendRedirect("../Generador/ConciliaBancos.jsp");
            ResponseJSON responseJSON = new ResponseJSON(false, Arrays.asList((new String[] { String.valueOf(mensajeRetorno) })), null);
            try {
                rep.sendJSONResponse(response, responseJSON);
            } catch (Exception e2) {
                throw new ServletException(e2);
            }
        }
    }

    private String layoutBanco(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        List<?> fileItems = null;
        Iterator<?> iter = null;
        DataInputStream archivoCargaStream = null;
        DataInputStream archivoCargaStreamDB = null;
        String nombreDestino = "";
        String urlArch = "";
        String UE = "";
        String cuenta = "", fecha = "", strCuenta = "", strCC = "";
        Integer nMes = 0, idEdoCta = 0, idAux = 0, nSIIWEB = 0;
        String rutaRemoto = "";
        String[] splitRuta = null;
        String dominio = "";
        try {
            fileItems = Util.parseRequest(request, ConciliaBancosServlet.tempDir, -1);
            iter = fileItems.iterator();
            String nombreArchivo = "";
            conn = DataSourceManager.getConnection(jndiName);
            boolean esControlFonden = AdecuacionManager.esControlFonden(conn);
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    item.delete();
                    continue;
                }
                archivoCargaStream = new DataInputStream(item.getInputStream());
                archivoCargaStreamDB = new DataInputStream(item.getInputStream());
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                if (!"txt".equalsIgnoreCase(extension)) {
                    throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                }
                nombreDestino = generaNombre(extension, UE);
                if ("cargaArchivo".equals(item.getFieldName())) {
                    urlArch = nombreDestino;
                }
                log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                // Cargamos al Servidor de Aplicacion
                Util.copiaArchivo(archivoCargaStream, tempDir + "\\" + nombreDestino);
                rutaRemoto = ConfiguraAplicativoManager.obtenRutaRemoto(conn);
                splitRuta = rutaRemoto.split("/");
                dominio = ConfiguraAplicativoManager.obtenDominioRemoto(conn);
                dominio = dominio.replace(".", "");
                // Cargamos al Servidor de Base de Datos
                Util.uploadStreamServerBD(nombreDestino, archivoCargaStreamDB);
                item.delete();
            }
            cuenta = leeCSV(tempDir + "\\" + nombreDestino, 1);
            fecha = leeCSV(tempDir + "\\" + nombreDestino, 2);
            String[] fechaArr = fecha.split("/");
            nMes = Integer.parseInt(fechaArr[1]);
            if (conciliaBancosBusinessLogic.validaConciliacion(conn, cuenta, nMes - 1)) {
                System.out.println("Mes valido para la Conciliacion.");
                strCuenta = conciliaBancosBusinessLogic.CLABE(conn, cuenta);
                System.out.println("Cuenta: " + strCuenta);
                nSIIWEB = conciliaBancosBusinessLogic.SIIWEB(conn, cuenta);
                System.out.println("No SIIWEB: " + String.valueOf(nSIIWEB));
                strCC = conciliaBancosBusinessLogic.ctaCC(conn, cuenta);
                System.out.println("Centro Contable: " + strCC);
                usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                String rutaArch = dominio + splitRuta[3] + "\\" + urlArch;
                String login = usuario.getLogin();
                idEdoCta = conciliaBancosBusinessLogic.generaEstadoCuenta(conn, strCuenta, nMes, login, rutaArch);
                idAux = conciliaBancosBusinessLogic.generaAuxiliar(conn, strCuenta, nMes, login, esControlFonden);
                if (conciliaBancosBusinessLogic.creaConciliacion(conn, strCuenta, nMes, idEdoCta, idAux, strCC, nSIIWEB, login, esControlFonden)) {
                    System.out.println("Conciliacion Creada.");
                } else {
                    System.out.println("Fallo al crear el encabezado de la Conciliacion.");
                }
            } else {
                throw new Exception("Esta conciliacion no se puede procesar. El mes " + String.valueOf(nMes - 1) + " no se finalizo");
            }
            conn.commit();
            return "?importado=S";
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error: " + e, e);
            if (conn != null) {
                conn.rollback();
            }
            throw new Exception(e.toString());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private String leeCSV(String Archivo, Integer dato) {
        String csvFile = Archivo;
        BufferedReader br = null;
        String line = "/n";
        String cvsSplitBy = "|";
        Integer inicio = 0;
        String respuesta = "";
        Integer i = 0, contador = 0;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);
                if (inicio == 1) {
                    switch(dato) {
                        case 1:
                            i = 1;
                            break;
                        case 2:
                            i = 23;
                            break;
                        case 9:
                            for (int j = 1; j < br.readLine().length(); ) {
                                if (datos[j].equals("|")) {
                                    contador++;
                                    if (contador == dato) {
                                        i = j + 1;
                                        break;
                                    }
                                }
                                j++;
                            }
                            break;
                    }
                    do {
                        respuesta = respuesta + datos[i];
                        i += 1;
                    } while (!datos[i].equals("|"));
                    break;
                }
                inicio += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done");
        return respuesta;
    }

    public static synchronized String generaNombre(String extension, String UE) {
        String idRandom = String.valueOf(Math.round((1 + Math.random()) * 10000));
        String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring(0, GestionInterface.PREFIX_TEMP.length() - idRandom.length()) + idRandom;
        String nombreDestino = "LAYOUTBANCO_" + UE + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
        return nombreDestino;
    }
}
