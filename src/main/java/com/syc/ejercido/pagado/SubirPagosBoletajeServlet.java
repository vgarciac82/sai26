package com.syc.ejercido.pagado;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.ejercido.pagado.core.BoletaAereoBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/*
	private void creaExcelVuelos(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		HttpSession session = req.getSession();
		Usuario u = null;
		
		String msgError = "";

		if (session == null){
			msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
		}else{
			u = (Usuario) session.getAttribute(ATT_USER);
			if (u == null) {
				msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
			}
		}
		if (StringUtils.isEmpty(msgError)) {
			try {
		
				String file_name = "Conciliacion_Vuelos.xls";
				resp.setContentType("application/vnd.ms-excel");
				resp.addHeader("Content-Disposition","inline; filename=\"" + file_name + "\";");				
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet hs = wb.createSheet();
				HSSFRow fila = hs.createRow(0);
												
				String[] row = req.getParameterValues("row[]");
				String[] boleto = req.getParameterValues("boleto[]");
				String[] rfc = req.getParameterValues("rfc[]");
				String[] nombre = req.getParameterValues("nombre[]");
				String[] ruta = req.getParameterValues("ruta[]");
				String[] fsalida = req.getParameterValues("fsalida[]");
				String[] fregreso = req.getParameterValues("fregreso[]");
				String[] status = req.getParameterValues("status[]");
				String[] pagado = req.getParameterValues("pagado[]");
				String[] folio = req.getParameterValues("folio[]");

				HSSFCell celda = fila.createCell(0); 
				celda.setCellValue("BOLETO");
				celda = fila.createCell(1);
				celda.setCellValue("RFC");
				celda = fila.createCell(2);
				celda.setCellValue("NOMBRE");
				celda = fila.createCell(3);
				celda.setCellValue("RUTA");
				celda = fila.createCell(4);
				celda.setCellValue("FECHA SALIDA");
				celda = fila.createCell(5);
				celda.setCellValue("FECHA REGRESO");
				celda = fila.createCell(6);
				celda.setCellValue("ESTATUS");
				celda = fila.createCell(7);
				celda.setCellValue("PAGADO");
				celda = fila.createCell(8);
				celda.setCellValue("FOLIO COMPROBACION");
								
				
				for( int i = 0; i < row.length; i++ ){
					fila = hs.createRow(i+1);
					
					celda = fila.createCell(0); 
					celda.setCellValue(boleto[i]);
					
					celda = fila.createCell(1);
					celda.setCellValue(rfc[i]);
					
					celda = fila.createCell(2);
					celda.setCellValue(nombre[i]);
					
					celda = fila.createCell(3);
					celda.setCellValue(ruta[i]);
					
					celda = fila.createCell(4);
					celda.setCellValue(fsalida[i]);
					
					celda = fila.createCell(5);
					celda.setCellValue(fregreso[i]);
					
					celda = fila.createCell(6);
					celda.setCellValue(status[i]);
					
					celda = fila.createCell(7);
					celda.setCellValue(pagado[i]);
					
					celda = fila.createCell(8);
					celda.setCellValue(folio[i]);
				}
				
				wb.write(resp.getOutputStream());
				
				ServletOutputStream out = resp.getOutputStream();
				
				Util.doDownload(out, file_name, file_name, "");
				
				out.flush();
				out.close();
				
				String mensaje = "Se genero correctamente el archivo de Vuelos.";
				ResponseSender.sendClientSimpleMessage(resp, true, mensaje);
			} catch (Exception e) {
				ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error generando el archivo de Vuelos: " + e.toString().replace("\"", "").replace("'", ""));
			}
		} else {
			ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error generando el archivo de Vuelos: " + msgError);
		}
	}
	*/
@WebServlet(name = "SubirPagosBoletajeServlet", urlPatterns = { "/layouts/cargaLayoutVuelos", "/vuelos/AgregaVuelosPago", "/vuelos/reporteVuelos" })
public class SubirPagosBoletajeServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 126320606200256721L;

    private static final Logger log = LoggerFactory.getLogger(SubirPagosBoletajeServlet.class);

    private String jniName = "";

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separatorChar + "ConBanTMP" + File.separatorChar;

    private static Map<String, String> plantillas = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getRequestURI().indexOf("AgregaVuelosPago") > 0 ? "AGREGAVUELOS" : "CARGALAYOUT";
        String generaExcel = req.getParameter("generaExcel");
        if ("1".equals(generaExcel)) {
            try {
                generaExcelVuelos(req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ServletOutputStream out = resp.getOutputStream();
                out.println("Ocurrio el siguiente eror mientras se generaba el reporte:<br>");
                out.println(e.getMessage());
                out.println("<br>");
                out.println(e.toString());
                out.println("<br>Intente nuevamente. Si el problema persiste notifique al administrador del sistema");
                out.flush();
                out.close();
                return;
            }
        } else {
            if ("AGREGAVUELOS".equals(accion)) {
                try {
                    agregaVuelos(req, resp);
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            } else {
                HttpSession session = req.getSession(false);
                String msgRetorno = "";
                Usuario u = null;
                if (session == null) {
                    msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
                } else {
                    u = (Usuario) session.getAttribute(ATT_USER);
                    if (u == null) {
                        msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
                    }
                }
                if ("".equals(msgRetorno)) {
                    /* Variables para la carga de archivo. */
                    List<?> fileItems = null;
                    Iterator<?> iter = null;
                    DataInputStream archivoCargaStream = null;
                    String nombreDestino = "";
                    try {
                        fileItems = Util.parseRequest(req, TEMP_DIR, -1);
                        iter = fileItems.iterator();
                        String login = "";
                        String cCentroContable = "";
                        int nFolioPago = 0;
                        String cNombreArchivo = "";
                        BigDecimal totalPago = new BigDecimal(0.0d);
                        String nombreArchivo;
                        String cEsPago = "";
                        while (iter.hasNext()) {
                            FileItem item = (FileItem) iter.next();
                            /* Lee los elementos del formulario */
                            if (item.isFormField()) {
                                if ("nFolioPagoDiversoVuelos".equals(item.getFieldName()))
                                    nFolioPago = Integer.parseInt(item.getString());
                                else if ("cCentroContableVuelos".equals(item.getFieldName()))
                                    cCentroContable = item.getString();
                                else if ("uLoginVuelos".equals(item.getFieldName()))
                                    login = item.getString();
                                else if ("cNameFile".equals(item.getFieldName()))
                                    cNombreArchivo = item.getString();
                                else if ("mImporteTotalVuelos".equals(item.getFieldName()))
                                    totalPago = new BigDecimal(StringUtils.isEmpty(item.getString()) ? "0.0" : item.getString());
                                else if ("cEsPago".equals(item.getFieldName()))
                                    cEsPago = item.getString();
                                item.delete();
                                continue;
                            } else {
                                /*
								 * Carga el archivo
								 */
                                archivoCargaStream = new DataInputStream(item.getInputStream());
                                nombreArchivo = item.getName();
                                String extension = Util.getFileExtencion(nombreArchivo);
                                nombreDestino = FacturaUtils.generaNombreArchivoTemporal(TEMP_DIR, nombreArchivo, extension);
                                log.info("Object: {}", "Copiando archivo: [" + nombreArchivo + "] a [" + nombreDestino + "]");
                                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                                item.delete();
                            }
                        }
                        CargaPagosBoletaje cpb = new CargaPagosBoletaje(jniName);
                        msgRetorno = cpb.procesaArchivo(nombreDestino, nFolioPago, cCentroContable, login, cNombreArchivo, totalPago, cEsPago);
                        if (StringUtils.isEmpty(msgRetorno)) {
                            msgRetorno = "Archivo cargado exitosamente.";
                        } else {
                            msgRetorno = msgRetorno + CargaPagosBoletajeManager.msgRetorno;
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
                    } finally {
                        if (archivoCargaStream != null)
                            try {
                                archivoCargaStream.close();
                            } catch (Exception e) {
                                log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                            }
                        archivoCargaStream = null;
                        if (!"".equals(nombreDestino)) {
                            File toDelete = new File(nombreDestino);
                            if (!toDelete.delete())
                                toDelete.deleteOnExit();
                        }
                    }
                } else {
                    resp.sendRedirect("Generador/CargaLayoutVuelos.jsp?msgError=" + msgRetorno);
                }
                session.setAttribute("RESULT", msgRetorno);
                resp.sendRedirect("../Generador/CargaLayoutVuelos.jsp?RESPUESTA=S");
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("FmtoRpteVuelos", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Vuelos.xls"));
            }
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se logro crear la carpeta temporal " + TEMP_DIR + " . Notifique a soporte.");
        } catch (Exception e) {
            throw new ServletException(e);
        }
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
    }

    private void agregaVuelos(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        Usuario u = null;
        String msgError = "";
        if (session == null) {
            msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
            }
        }
        if (StringUtils.isEmpty(msgError)) {
            try {
                String cTipoPago = req.getParameter("cTipoPago");
                int nFolioPago = Integer.parseInt(req.getParameter("nFolioPago"));
                int nFolioVuelos = Integer.parseInt(req.getParameter("nFolioVuelos"));
                String[] nDocRenglonVuelos = req.getParameterValues("nDocRenglon");
                BoletaAereoBusinessLogic babl = new BoletaAereoBusinessLogic(jniName);
                int insertados = babl.insertaBoletosTemporal(cTipoPago, nFolioPago, nFolioVuelos, nDocRenglonVuelos);
                String mensaje = "Se agregaron " + insertados + " vuelos al pago.";
                ResponseSender.sendClientSimpleMessage(resp, true, mensaje);
            } catch (Exception e) {
                ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error insertando la informacion: " + e.toString().replace("\"", "").replace("'", ""));
            }
        } else {
            ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error insertando la informacion: " + msgError);
        }
    }

    private void generaExcelVuelos(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        Usuario u = null;
        String msgError = "";
        if (session == null) {
            msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgError = "Su sesion ha terminado. Vuelva a ingresar al sistema.";
            }
        }
        if (StringUtils.isEmpty(msgError)) {
            BoletaAereoBusinessLogic babl = new BoletaAereoBusinessLogic(jniName);
            babl.generaExcelVuelos(req, resp, plantillas);
        } else {
            throw new Exception("Ocurrio el siguiente error generando el archivo Excel: " + msgError);
        }
    }
}
