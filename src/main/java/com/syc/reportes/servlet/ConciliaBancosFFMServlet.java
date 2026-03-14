package com.syc.reportes.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConciliaBancosFFMServlet", urlPatterns = { "/servlet/ConciliaBancosFFMServlet" })
public class ConciliaBancosFFMServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConciliaBancosFFMServlet.class);

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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = null;
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
            String resp = layoutBanco(request, response, session);
            response.sendRedirect("../Generador/ConciliaBancosFFM.jsp" + resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            session.setAttribute(GestionInterface.ATT_MSG, e.toString());
            try {
                response.sendRedirect("../Generador/ConciliaBancosFFM.jsp?error=SI");
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
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
        String cuenta = "", fecha = "", strCuenta = "", strCC = "", query = "", strEdoCta = "";
        Integer nMes = 0, idEdoCta = 0, idAux = 0, nSIIWEB = 0;
        String rutaRemoto = "";
        String[] splitRuta = null;
        String dominio = "";
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        try {
            fileItems = Util.parseRequest(request, ConciliaBancosFFMServlet.tempDir, -1);
            iter = fileItems.iterator();
            String nombreArchivo = "";
            conn = DataSourceManager.getConnection(jndiName);
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
            nMes = Integer.parseInt(fecha.substring(3, 5), 10);
            strCuenta = CLABE(conn, cuenta);
            System.out.println("Cuenta: " + strCuenta);
            strCC = "10";
            System.out.println("Centro Contable: " + strCC);
            usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            query = "{call sp_j_tmpEdoCta( ?, ?, ?, ? )}";
            cs = conn.prepareCall(query);
            cs.setString(1, strCuenta);
            cs.setInt(2, nMes);
            cs.setString(3, usuario.getLogin());
            cs.setString(4, dominio + splitRuta[3] + "\\" + urlArch);
            rs = cs.executeQuery();
            if (rs.next()) {
                strEdoCta = rs.getString(1);
                idEdoCta = rs.getInt(2);
            } else {
                throw new Exception("Error al importar el estado de cuenta. Notifique al administrador");
            }
            if ("S".equals(strEdoCta)) {
                System.out.println("Se importo Estado de Cuenta.");
            } else {
                System.out.println("No importo Estado de Cuenta, Saldos diferentes.");
                throw new Exception("Error al Importar el Estado de Cuenta. Validar el Archivo TXT");
            }
            query = "{call sp_j_tmpAuxiliar( ?, ?, ?)}";
            cs2 = conn.prepareCall(query);
            cs2.setString(1, strCuenta);
            cs2.setInt(2, nMes);
            cs2.setString(3, usuario.getLogin());
            rs2 = cs2.executeQuery();
            if (rs2.next()) {
                idAux = rs2.getInt(2);
            } else {
                throw new Exception("Fallo al generar el auxiliar contable. Notifique al administrador");
            }
            System.out.println("Auxilar Generado.");
            if (creaConciliacion(conn, strCuenta, nMes, idEdoCta, idAux, strCC, nSIIWEB, usuario.getLogin())) {
                System.out.println("Conciliacion Creada.");
            } else {
                System.out.println("Fallo al crear el encabezado de la Conciliacion.");
            }
            return "?importado=S";
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    log.warn("Error occurred", "Error en rollback " + e1);
                }
            throw e;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(cs);
            CloseObject.closeObject(conn);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(cs2);
        }
    }

    private static boolean creaConciliacion(Connection conn, String Cuenta, Integer Mes, Integer idEdoCta, Integer idAux, String cCC, Integer SIIWEB, String strUsuario) throws Exception {
        PreparedStatement pstm_cta = null;
        ResultSet nConciliacion = null;
        Integer idConciliacion = 0, nConc = 0;
        String cDescripcion = "Conciliacion de la Cuenta " + Cuenta + " del Mes " + Mes.toString();
        String cCentroContable = cCC;
        AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String ef = adbl.obtenEjercicioFiscal();
        pstm_cta = conn.prepareStatement("INSERT INTO dbo.tConciliacionFFM(nEdocta,nAuxiliar,nCban,nMes,fConciliacion,mSaldoFinal,cUsuario,cDescripcion,nFinal, cCentroContable, mSaldoLibros, pdfOriginal, pdfFirmas, nModalidad, nFolioCuenta, nEjercicio, mSumaAbonos) " + "VALUES ( " + idEdoCta + "," + idAux + " ,'" + Cuenta + "'," + Mes + " ,GETDATE(),0,SUBSTRING('" + strUsuario + "',1,10) ,'" + cDescripcion + "' ,0,'" + cCentroContable + "',0,0,0,0," + SIIWEB + "," + ef + ",0 )");
        nConc = pstm_cta.executeUpdate();
        if (nConc.equals(1)) {
            pstm_cta = conn.prepareStatement("Select MAX(nConciliacion) as nConciliacion From dbo.tConciliacionFFM WITH (NOLOCK) Where nEdoCta=" + idEdoCta + " AND nAuxiliar=" + idAux + " AND nCban ='" + Cuenta + "' AND nMes=" + Mes + " AND nFinal = 0;");
            nConciliacion = pstm_cta.executeQuery();
            if (nConciliacion.next()) {
                idConciliacion = nConciliacion.getInt("nConciliacion");
                System.out.println("Conciliacion Creada. ID: " + idConciliacion.toString());
                return true;
            } else {
                return false;
            }
        } else {
            throw new Exception("No se genero la conciliacion. Notifique al administrador");
        }
    }

    /*private static boolean validaConciliacion(Connection conn, String Cuenta, Integer Mes) throws Exception {
		
		PreparedStatement pstm_valida = null;
		ResultSet valida = null;
		String strResultado = "";
		
		pstm_valida = conn.prepareStatement("SELECT CASE WHEN COUNT(*) = 1 THEN 'S' ELSE 'N' END RESP FROM dbo.tConciliacionFFM WITH (NOLOCK) WHERE nMes = " + Mes + " AND nCban LIKE '%" + Cuenta + "%' AND nFinal = 1");
		valida = pstm_valida.executeQuery();

		if (valida.next()) {
			strResultado = valida.getString("RESP");
		} else {
			throw new Exception("Error al buscar conciliación finalizada del mes "+ Mes +" para la cuenta "+ Cuenta +". Notifique al Administrador");
		}
		
		System.out.println("Conciliacion procedente: " + strResultado);
		if ( "S".equals(strResultado)){
			return true;
		}else{
			return false;
		}
		
	}
	*/
    private String CLABE(Connection conn, String Cuenta) throws Exception {
        PreparedStatement pstm_cuenta = null;
        ResultSet cuenta = null;
        String strCta = "";
        //busco la cuenta de FID-Banorte
        pstm_cuenta = conn.prepareStatement("SELECT subCuentaBancaria FROM dbo.tBeneficiarioCuentasBancarias (NOLOCK) WHERE dRFC = 'BMN930209927' AND  subCuentaBancaria LIKE '%" + Cuenta + "%'");
        cuenta = pstm_cuenta.executeQuery();
        if (cuenta.next()) {
            strCta = cuenta.getString("subCuentaBancaria");
        } else {
            throw new Exception("No se encontro la cuenta bancaria en la base de datos. Notifique al administrador");
        }
        return strCta;
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public static synchronized String generaNombre(String extension, String UE) {
        String idRandom = String.valueOf(Math.round((1 + Math.random()) * 10000));
        String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring(0, GestionInterface.PREFIX_TEMP.length() - idRandom.length()) + idRandom;
        String nombreDestino = "LAYOUTBANCOFFM_" + UE + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
        return nombreDestino;
    }
}
