package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.syc.contable.core.AplicacionContable;

public class SubirArchivosCadenasPServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AplicacionContable.class);

	/**
	 * Constructor of the object.
	 */
	public SubirArchivosCadenasPServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect("index.jsp");
			return;
		}
		List<FileItem> fileItems = new ArrayList<FileItem>();
		Map<String, String> fieldMap = new Hashtable<String, String>();
		List<FileItem> fileList = new ArrayList<FileItem>();

		String pathUrl = request.getContextPath();
		String pathBase = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + pathUrl + "/";
		String mensaje = "";
		try {

			fileItems = procesaArchivos(request);
			for (FileItem item : fileItems) {
				if (item.isFormField()) {
					fieldMap.put(item.getFieldName(), item.getString());
				} else {
					fileList.add(item);
				}
			}
			String tipoArchivo = request.getParameter("tipoArchivo");
			InputStream in = fileList.get(0).getInputStream();
			String valor = enviaRuta(in, tipoArchivo);

			if (valor == "guardado") {
				mensaje = "Archivo Cargado";
			} else if (valor.equals("no_guardado")) {
				mensaje = "Error: No Se Guardaron Los Registros Correctamente";
			} else {
				mensaje = "Error: En el Orden de Columnas. Favor de verificar la columna "
						+ valor;
			}
		} catch (Exception e) {
			log.error("Error: " + e);
			mensaje = "Error: No Se Guardaron Los Registros Correctamente";
		} finally {
			// if (in != null){ in.close(); }
			// in = null;
		}
		response.sendRedirect(pathBase
				+ "Generador/SubirArchivoCadenasP.jsp?mensaje=" + mensaje);
	}

	@SuppressWarnings("unchecked")
	public InputStream cargarArchivo(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String pathArchivo = "";
		try {
			// Se construye un objeto para que parsee la petición
			DiskFileUpload fu = new DiskFileUpload();
			// Tamaño máximo que aceptará el archivo
			fu.setSizeMax(-1); // El tamaño no importa
			fu.setSizeThreshold(1048576); // Si excede el 1 Gb en memoria lo
			// escribe a disco.

			pathArchivo = getServletContext().getRealPath(
					"upload/ejercidoPagado");
			File file = new File(pathArchivo);
			if (!file.exists()) {
				file.mkdirs();
			}
			// System.out.println("Ruta:" + pathArchivo);
			fu.setRepositoryPath(pathArchivo);

			List<FileItem> fileItems = fu.parseRequest(request);
			Iterator<FileItem> i = fileItems.iterator();
			FileItem actual = null;

			while (i.hasNext()) {
				actual = i.next();
				if (actual.isFormField())
					continue;

				return actual.getInputStream();
			}

		} catch (Exception e) {
			log.error("Error: " + e);
			throw new IOException(e);
		}
		throw new IOException("No se encontro archivo alguno en la peticion");
	}

	public String enviaRuta(InputStream in, String tipoArchivo)
			throws FileNotFoundException {

		boolean subirInformacion = false; // Si es true se agrega para ejecutar
		// el procedimeinto y si es false es
		// para insertar directamente en
		// columna
		//List<String> mapa = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String sCadenas = "";

		//Hashtable<String, Integer> nomColumna = new Hashtable<String, Integer>();
		String valorReturn = "";
		//boolean columnas = true;
		String guardaLinea = "";
		// String limpiaTabla = "";
		try {
			CargaArchivoCadenasP guardaSicop = new CargaArchivoCadenasP();
			int primerLinea = 1;
			//int commitCien = 1;

			while ((sCadenas = br.readLine()) != null) {
				System.out.println("linea:" + primerLinea);
				if (subirInformacion) {
					System.out.println("cajajajaja:" + sCadenas);
				}

				else {
					System.out.println("cadena:" + sCadenas);

					if (tipoArchivo.equals("ERRORES")) {

						sCadenas = sCadenas.replaceAll("@", " |");

						String[] celdas1 = sCadenas.split("[|]");
						System.out.println(celdas1);

						String RFC_0 = celdas1[0].trim();
						String caNoContrarrecibo_1 = celdas1[1].trim();
						String fEmision_2 = celdas1[2].trim();
						String fVencimiento_3 = celdas1[3].trim();
						String FIJO_01_4 = celdas1[4].trim();
						String FIJO_1_5 = celdas1[5].trim();
						String FIJO_N_6 = celdas1[6].trim();
						String tipoPago_7 = celdas1[7].trim();
						String cCentroContable_8 = celdas1[8].trim();
						String dato1_9 = celdas1[9].trim();
						String dato2_10 = celdas1[10].trim();
						String dato3_11 = celdas1[11].trim();
						String dato4_12 = celdas1[12].trim();
						String dato5_13 = celdas1[13].trim();
						String dato6_14 = celdas1[14].trim();

						//String cuantos = String.valueOf(commitCien);
						guardaLinea = guardaSicop.insertaLineaSiaff(RFC_0,
								caNoContrarrecibo_1, fEmision_2,
								fVencimiento_3, FIJO_01_4, FIJO_1_5, FIJO_N_6,
								tipoPago_7, cCentroContable_8, dato1_9,
								dato2_10, dato3_11, dato4_12, dato5_13,
								dato6_14);

						// if (guardaLinea.equals("no_guardado")) {
						// break;
						// }
						// System.out.println("guardaLineaS:" + guardaLinea);

					} else {

						String[] celdas = sCadenas.split(",");
						System.out.println(celdas);
						// Archivo SICOP
						if (primerLinea == 1) {
							if (!celdas[0].trim()
									.equals("Nombre del proveedor")) {
								guardaLinea = "Nombre del proveedor";
								break;
							}
							if (!celdas[1].trim()
									.equals("Número del documento")) {
								guardaLinea = "Número del documento";
								break;
							}
							if (!celdas[2].trim().equals("Número de Acuse")) {
								guardaLinea = "Número de Acuse";
								break;
							}
							if (!celdas[3].trim().equals("Fecha de Emisión")) {
								guardaLinea = "Fecha de Emisión";
								break;
							}
							if (!celdas[4].trim()
									.equals("Fecha de Vencimiento")) {
								guardaLinea = "Fecha de Vencimiento";
								break;
							}
							if (!celdas[5].trim().equals("Moneda")) {
								guardaLinea = "Moneda";
								break;
							}
							if (!celdas[6].trim().equals("Monto")) {
								guardaLinea = "Monto";
								break;
							}
							if (!celdas[7].trim().equals("Estatus")) {
								guardaLinea = "Estatus";
								break;
							}
							if (!celdas[8].trim().equals(
									"Intermediario Financiero")) {
								guardaLinea = "Intermediario Financiero";
								break;
							}
							if (!celdas[9].trim().equals("Número de solicitud")) {
								guardaLinea = "Número de solicitud";
								break;
							}
							if (!celdas[10].trim().equals("Entidad Contable")) {
								guardaLinea = "Entidad Contable";
								break;
							}
							if (!celdas[11].trim().equals("Campo Adicional 2")) {
								guardaLinea = "Campo Adicional 2";
								break;
							}
							if (!celdas[12].trim().equals("Campo Adicional 3")) {
								guardaLinea = "Campo Adicional 3";
								break;
							}
							if (!celdas[13].trim().equals("Campo Adicional 4")) {
								guardaLinea = "Campo Adicional 4";
								break;
							}
							if (!celdas[14].trim().equals("Campo Adicional 5")) {
								guardaLinea = "Campo Adicional 5";
								break;
							}
							if (!celdas[15].trim()
									.equals("Número de Proveedor")) {
								guardaLinea = "Número de Proveedor";
								break;
							}
							if (!celdas[16].trim().equals("Referencia")) {
								guardaLinea = "Referencia";
								break;
							}
							if (!celdas[17].trim().equals("Clave de Estatus")) {
								guardaLinea = "Clave de Estatus";
								break;
							}
							if (!celdas[18].trim().equals(
									"Porcentaje de Descuento")) {
								guardaLinea = "Porcentaje de Descuento";
								break;
							}
							if (!celdas[19].trim().equals("Monto a Descontar")) {
								guardaLinea = "Monto a Descontar";
								break;
							}
							if (!celdas[20].trim().equals(
									"Digito Identificador")) {
								guardaLinea = "Digito Identificador";
								break;
							}
							if (!celdas[21].trim().equals(
									"Fecha de Recepción de Bienes y Servicios")) {
								guardaLinea = "Fecha de Recepción de Bienes y Servicios";
								break;
							}
							if (!celdas[22].trim().equals(
									"Tipo de Compra (procedimiento)")) {
								guardaLinea = "Tipo de Compra (procedimiento)";
								break;
							}
							if (!celdas[23].trim().equals(
									"Clasificador por Objeto del Gasto")) {
								guardaLinea = "Clasificador por Objeto del Gasto";
								break;
							}
							if (!celdas[24].trim().equals("Plazo Máximo")) {
								guardaLinea = "Plazo Máximo";
								break;
							}

							// limpiaTabla =
							// guardaSicop.limpiarTabla(" tCadenasPDetalle ");
							// System.out.println(limpiaTabla);
							primerLinea = primerLinea + 1;
						} else {
							String cNombreProv_0 = celdas[0].trim();
							String caNoContrarrecibo_1 = celdas[1].trim();
							String nNumAcuse_2 = celdas[2].trim();
							String fEmision_3 = celdas[3].trim();
							String fVencimiento_4 = celdas[4].trim();
							String cMoneda_5 = celdas[5].trim();
							String mMonto_6 = celdas[6].trim();
							String cEstatus_7 = celdas[7].trim();
							String cIntermediarioF_8 = celdas[8].trim();
							String nNumSolicitud_9 = celdas[9].trim();
							String cCentroContable_10 = celdas[10].trim();
							String cCampoAdi2_11 = celdas[11].trim();
							String cCampoAdi3_12 = celdas[12].trim();
							String cCampoAdi4_13 = celdas[13].trim();
							String cCampoAdi5_14 = celdas[14].trim();
							String RFC_15 = celdas[15].trim();
							String cConcepto_16 = celdas[16].trim();
							String nClaveEstatus_17 = celdas[17].trim();
							String nPorcentajeDes_18 = celdas[18].trim();
							String mMontoDesc_19 = celdas[19].trim();
							String nNumIdentificador_20 = celdas[20].trim();
							String fRecepcion_21 = celdas[21].trim();
							String cTcompra_22 = celdas[22].trim();
							String cClasificador_23 = celdas[23].trim();
							String cPlazoMax_24 = celdas[24].trim();

							guardaLinea = guardaSicop.insertaLineaSicop(
									cNombreProv_0, caNoContrarrecibo_1,
									nNumAcuse_2, fEmision_3, fVencimiento_4,
									cMoneda_5, mMonto_6, cEstatus_7,
									cIntermediarioF_8, nNumSolicitud_9,
									cCentroContable_10, cCampoAdi2_11,
									cCampoAdi3_12, cCampoAdi4_13,
									cCampoAdi5_14, RFC_15, cConcepto_16,
									nClaveEstatus_17, nPorcentajeDes_18,
									mMontoDesc_19, nNumIdentificador_20,
									fRecepcion_21, cTcompra_22,
									cClasificador_23, cPlazoMax_24);
							if (guardaLinea.equals("no_guardado")) {
								break;
							}
							System.out.println("guardaLinea:" + guardaLinea);
						}
					}
				}
				primerLinea = primerLinea + 1;
				// commitCien = (commitCien >= 100) ? commitCien = 0 :
				// commitCien + 1;
				// System.out.println("linea:" + primerLinea);

			}

			// }
			valorReturn = guardaLinea;
		} catch (Exception se) {
			log.error("Error: " + se);
			se.printStackTrace();
			valorReturn = "Error";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception exc) {
				Log.warn("Cerrando BufferedReader", exc);
			}
			br = null;
		}
		// System.out.println("valorReturn:" + valorReturn);
		return valorReturn;
	}

	// #################################################### se lee el archivo
	// ingresado más las condiciones (campo accion
	// y##################################
	@SuppressWarnings("unchecked")
	public List<FileItem> procesaArchivos(HttpServletRequest request) {

		String szPath;
		List<FileItem> fileItems = new ArrayList<FileItem>();
		try {
			// Se construye un objeto para que parsee la petición
			DiskFileUpload fu = new DiskFileUpload();

			// Tamaño máximo que aceptará el archivo
			fu.setSizeMax(-1); // El tamaño no importa
			fu.setSizeThreshold(1048576); // Si excede el 1 Gb en memoria lo
			// escribe a disco
			szPath = getServletContext().getRealPath("/upload/ejercidoPagado");
			File file = new File(szPath);

			if (!file.exists()) {
				file.mkdirs();
			}

			fu.setRepositoryPath(szPath);
			fileItems = fu.parseRequest(request);
		} catch (Exception e) {
			log.error("Error: " + e);
			System.out.println("Error de Aplicación " + e.getMessage());
		}
		return fileItems;
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
