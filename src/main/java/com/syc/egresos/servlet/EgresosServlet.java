package com.syc.egresos.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.axtel.contratos.core.QuestionnaireAnswer;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.syc.egresos.ResponseJSON;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoPAGODIRECTOEncabezado;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class EgresosServlet extends HttpServlet implements GestionInterface {

	private static final String ACTUALIZA_MONTO_RETENCIONES = "updateMontoRetenciones";
	private static final String ACTUALIZA_RETENCION = "actualizaRetencion";
	private static final Object APARTADO_PAGO = "apartadoPago";
	private static final String AUTORIZA_PAGO = "AutorizaPago";
	private static final String AVANZA_ESTATUS = "avanzaEstatus";
	private static final Object CONSULTA_FIRMANTE = "ConsultaFirmante";
	private static final String ELIMINA_RETENCION = "eliminaRetencion";
	private static final String GENERA_CONTRARECIBO = "generaContrarecibo";
	private static final String GENERA_RETENCIONES = "generaRetenciones";
	private static final String GUARDA_CALENDARIO = "guardaCalendario";
	private static final String IMPRIME_SOLICITUD = "imprimeSolPago";
	private static final String VALIDA_FOLIO_REPSE = "validaREPSE";
	private static final String VALIDA_MONTO_TOTALIZADO = "validaMontoTotalizado";
	
	private static final String SAVE_QUESTIONNAIRE = "saveQuestionnaire";
	public static final Logger log = Logger.getLogger(EgresosServlet.class);
	private static final String RECHAZO_PAGO = "RechazaPago";
	public static String reportPath = "";
	private static final String RESUMEN_CALENDARIO = "resumenCalendario";
	private static final String RESUMEN_CONCEPTO = "resumenConcepto";
	private static final String RESUMEN_FINAL_PAGO = "resumenFinalPago";
	private static final String RESUMEN_RETENCIONES = "resumenRetenciones";
	private static final String SAVE = "saveHeader";
	private static final String GUARDA_PAGO = "guardarPago";
	private static final long serialVersionUID = 6135237145437183227L;
	private static final String UPDATE = "updateHeader";

	private static final String VALIDA_PAAS = "validaPAAS";
	private String jniName = "";

	private static final Gson gson = new GsonBuilder().create();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);

		if (session == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
			return;
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
			return;
		}

		String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
		String tipoEgreso = c.getTipoCaso().getGavetaAsociada();
		
		int folio = Util.folio(c);

		try {

			if (VALIDA_PAAS.equals(accion)) {
				EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
				ebl.setTipoEgreso(tipoEgreso);
				ebl.setFolioEgreso(folio);

				EgresoPAGODIRECTOEncabezado epde = (EgresoPAGODIRECTOEncabezado) ebl.cargaEncabezadoEgreso(req);
				List<String> errores = epde.validaCapturaPAAS();
				ResponseJSON responseJSON = new ResponseJSON((errores.size() == 0), errores, null);
				sendJSONResponse(resp, responseJSON);
			} else if (RESUMEN_CONCEPTO.equals(accion)) {
				EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
				ebl.setTipoEgreso(tipoEgreso);
				ebl.setFolioEgreso(folio);
				EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso(req);

				Map<String, String> resumen = ebl.resumenConcepto(encabezado);
				JSONObject obj = new JSONObject();
				obj.put("success", true);
				obj.put("resumen", resumen);
				sendJSONResponse(resp, obj);

			} else if (RESUMEN_RETENCIONES.equals(accion)) {
				String valor= req.getParameter( "cRegimenFiscal" );
				if (valor == null || valor == "") 	
					valor = "0";
				int regimen = Integer.parseInt(valor);
				
				EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
				ebl.setTipoEgreso(tipoEgreso);
				ebl.setFolioEgreso(folio);
				ebl.setRegimenFiscal( regimen );
				EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				encabezado.setRegimenFiscal( regimen );
				List<Map<String, String>> resumen = ebl.resumenRetenciones(encabezado);
				JSONObject obj = new JSONObject();
				obj.put("success", true);
				obj.put("resumen", Util.toJSONArray(resumen));
				sendJSONResponse(resp, obj);

			} else if (RESUMEN_CALENDARIO.equals(accion)) {
				EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
				ebl.setTipoEgreso(tipoEgreso);
				ebl.setFolioEgreso(folio);
				EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);

				List<Map<String, String>> resumen = ebl.resumenCalendario(encabezado);
				JSONObject obj = new JSONObject();
				obj.put("success", true);
				obj.put("resumen", Util.toJSONArray(resumen));
				sendJSONResponse(resp, obj);

			} else if (RESUMEN_FINAL_PAGO.equals(accion)) {

				EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
				ebl.setTipoEgreso(tipoEgreso);
				ebl.setFolioEgreso(folio);
				EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();

				Map<String, String> resumen = ebl.resumenPago(encabezado);
				JSONObject obj = new JSONObject();
				obj.put("success", true);
				obj.put("resumen", resumen);
				sendJSONResponse(resp, obj);

			} else if (CONSULTA_FIRMANTE.equals(accion)) {
				String tipoFirmante = req.getParameter("tipoFirmanteConsulta");
				boolean esSuplente = "S".equalsIgnoreCase(req.getParameter("esSuplente"));
				if (StringUtils.isBlank(tipoFirmante))
					throw new Exception("No se recibio el tipo de firmante a consultar.");

				EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
				ebl.setTipoEgreso(tipoEgreso);
				ebl.setFolioEgreso(folio);
				EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();

				Firmante firmante = null;
				if (esSuplente) {
					firmante = ebl.consultaFirmanteSuplente(encabezado, tipoFirmante);
				} else
					firmante = ebl.consultaFirmante(encabezado, tipoFirmante);

				if (firmante == null) {
					firmante = new Firmante();
				}

				JSONObject obj = new JSONObject();
				obj.put("success", true);
				if (esSuplente)
					obj.put("firmante", Util.toJson((FirmanteSuplente) firmante));
				else
					obj.put("firmante", Util.toJson(firmante));
				sendJSONResponse(resp, obj);
			} else if (IMPRIME_SOLICITUD.equals(accion)) {
				try {
					EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
					ebl.setTipoEgreso(tipoEgreso);
					ebl.setFolioEgreso(folio);
					EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();
					File solPagoFile = encabezado.generaSolicitudPago(reportPath);
					String mimeType = getServletContext().getMimeType(solPagoFile.getName());
					Util.doDownload(resp, solPagoFile.getAbsolutePath(),
							"Solicitud de Pago" + "." + Util.getFileExtencion(solPagoFile.getAbsolutePath()), mimeType);
				} catch (Exception e) {
					log.error(e, e);
					notificaError(resp, e);
				}
			} else if (VALIDA_FOLIO_REPSE.equals(accion)) {
				try {
					EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
					ebl.setTipoEgreso(tipoEgreso);
					ebl.setFolioEgreso(folio);
					EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();
					
					boolean numeroREPSECapturado = encabezado.numeroREPSECapturado();
					
					ResponseJSON responseJSON = new ResponseJSON(true, null,
							Arrays.asList((new String[] { String.valueOf(numeroREPSECapturado) })));
					
					sendJSONResponse(resp, responseJSON);
				} catch (Exception e) {
					log.error(e, e);
					notificaError(resp, e);
				}
			}else if (VALIDA_MONTO_TOTALIZADO.equals(accion)) {
				try {
					
					EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
					ebl.setTipoEgreso(tipoEgreso);
					ebl.setFolioEgreso(folio);
					EgresoEncabezado encabezado = ebl.cargaEncabezadoEgreso();
					
					String rfc = StringUtils.trimToNull(   req.getParameter("cIDRFC") );
					
					List<EgresoExcedeUMA> partidaExcede = ebl.validaTopeUMASUnidad(encabezado,  rfc == null? encabezado.getRfc():rfc );
					
					JsonObject obj = new JsonObject();
					obj.addProperty("success", true);
					
					JsonArray arr = new JsonArray();
					
					for( EgresoExcedeUMA egresoExcedeUMA:partidaExcede) {
						arr.add( gson.toJsonTree( egresoExcedeUMA, EgresoExcedeUMA.class ) );
					}
					
					obj.add("partidasExcenden", arr);
					
					sendGoogleJSONResponse(resp, obj);
					
				} catch (Exception e) {
					log.error(e, e);
					notificaError(resp, e);
				}
			}

		} catch (Exception e) {
			log.error("Ocurrio el siguiente error: " + e, e);
			List<String> errores = new ArrayList<String>();
			errores.add(e.toString());
			ResponseJSON responseJSON = new ResponseJSON(false, errores, null);
			try {
				sendJSONResponse(resp, responseJSON);
			} catch (Exception e2) {
				throw new ServletException(e2);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);

		if (session == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
			return;
		}

		Caso c = (Caso) session.getAttribute(ATT_CASE);
		if (c == null) {
			ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
			return;
		}

		String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
		String tipoEgreso = c.getTipoCaso().getGavetaAsociada();
		
 		int folio = Util.folio(c);

		EgresosBusinessLogic ebl = new EgresosBusinessLogic(jniName);
		ebl.setTipoEgreso(tipoEgreso);
		ebl.setFolioEgreso(folio);
		EgresoEncabezado encabezado;
		try {

			if (SAVE.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				int insertados = ebl.saveHeader(encabezado);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(insertados) })));
				sendJSONResponse(resp, responseJSON);   
			} else if (UPDATE.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				int insertados = ebl.actualizaHeader(encabezado);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(insertados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (AVANZA_ESTATUS.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				int actualizados = ebl.avanzaEstatus(encabezado);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(actualizados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (GENERA_RETENCIONES.equals(accion)) {
				int regimen = Integer.parseInt( req.getParameter( "cRegimenFiscal" ));
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				encabezado.setRegimenFiscal( regimen );
				int insertados = ebl.generaRetenciones(encabezado);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(insertados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (ELIMINA_RETENCION.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				int idTipoRetencion = Integer.parseInt(req.getParameter("idRetencion"));
				int eliminados = ebl.eliminaRetencion(encabezado, idTipoRetencion);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(eliminados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (ACTUALIZA_RETENCION.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				int idTipoRetencion = Integer.parseInt(req.getParameter("idRetencion"));
				BigDecimal valorRetencion = new BigDecimal(req.getParameter("retencionEditada"));
				int actualizados = ebl.actualizaRetencion(encabezado, idTipoRetencion, valorRetencion);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(actualizados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (ACTUALIZA_MONTO_RETENCIONES.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				int actualizados = ebl.actualizaMontosRetencion(encabezado);
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(actualizados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (GUARDA_CALENDARIO.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setJniName( jniName );
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				BigDecimal importeBruto = new BigDecimal(req.getParameter("importeCalendario"));
				String ep = req.getParameter("epCalendario");
				String cuentaOrigen = req.getParameter("cuentaOrigen");
				int insetados = ebl.insertaCalendario(encabezado, ep, importeBruto, cuentaOrigen);

				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(insetados) })));
				sendJSONResponse(resp, responseJSON);
			} else if (GENERA_CONTRARECIBO.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso(req);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);

				String cxp = "";
				if (StringUtils.isBlank(encabezado.getContrarecibo()))
					cxp = ebl.generaContrarecibo(encabezado);
				else
					cxp = encabezado.getContrarecibo();

				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(cxp) })));
				sendJSONResponse(resp, responseJSON);
			} else if (RECHAZO_PAGO.equals(accion)) {

				String motivoRechazo = req.getParameter("cMotivoRechazo");
				if (StringUtils.isBlank(motivoRechazo))
					throw new Exception("No se recibio motivo de rechazo.");

				encabezado = ebl.cargaEncabezadoEgreso();
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);

				ebl.rechazaPago(encabezado, motivoRechazo);

				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(true) })));
				sendJSONResponse(resp, responseJSON);

			} else if (AUTORIZA_PAGO.equals(accion)) {
				encabezado = ebl.cargaEncabezadoEgreso();
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);		
				String tipo = req.getParameter("directoNomina" ) == null ?  req.getParameter("cTipoPago" ) : req.getParameter("directoNomina" );
				
				if("NOMINA".equals( tipo ))
					ebl.autorizaPagoNomina(encabezado, u);
				else if("PAGODIRECTO".equals( tipo ))
					ebl.autorizaPagoDirecto(encabezado, u);
				else 
					ebl.autorizaPago(encabezado, u);

				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(true) })));
				sendJSONResponse(resp, responseJSON);
			} else if (APARTADO_PAGO.equals(accion)) {

				encabezado = ebl.cargaEncabezadoEgreso();
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);

				ebl.apartaPago(encabezado);

				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(encabezado.getContrarecibo()) })));
				sendJSONResponse(resp, responseJSON);

			} else if (SAVE_QUESTIONNAIRE.equals(accion)) {
				log.debug(req.getParameter("answers"));

				List<QuestionnaireAnswer> answers = QuestionnaireAnswer.instanceList(req.getParameter("answers").split(";"));

				encabezado = ebl.generaInstancia(tipoEgreso, folio);
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);
				encabezado.setQuestionnaireAnswers(answers);
				encabezado.setLogin(u.getLogin());
				encabezado.setAplica15D("1".equals(req.getParameter("aplicaArt15D")));
				ebl.saveAnswers(encabezado);

				log.debug(req.getParameter("answers"));
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList(new String[] { "Cuestionario registrado exitosamente." }));
				sendJSONResponse(resp, responseJSON);
			} else if (GUARDA_PAGO.equals(accion)) {

				encabezado = ebl.cargaEncabezadoEgreso();
				encabezado.setTipoPago(tipoEgreso);
				encabezado.setFolioPago(folio);

				ebl.guardaPago(encabezado);

				
				
				ResponseJSON responseJSON = new ResponseJSON(true, null,
						Arrays.asList((new String[] { String.valueOf(encabezado.getContrarecibo()) })));
				sendJSONResponse(resp, responseJSON);

			}

		} catch (Exception e) {
			log.error("Ocurrio el siguiente error: " + e, e);
			List<String> errores = new ArrayList<String>();
			errores.add(e.toString());
			ResponseJSON responseJSON = new ResponseJSON(false, errores, null);
			try {
				sendJSONResponse(resp, responseJSON);
			} catch (Exception e2) {
				throw new ServletException(e2);
			}
		}

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			EgresosServlet.reportPath = getServletContext().getRealPath("Reportes" + File.separator);
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
	}

	private void notificaError(HttpServletResponse resp, Exception e) {
		try {
			ServletOutputStream out = resp.getOutputStream();
			out.println("<html>");
			out.println("<body>");

			out.println("<h1>Reporte de Error</h1><br>");
			out.println("Ocurrio el siguiente error mientras se ejecutaba la operacion:<br/>");
			out.println("<textarea rows=\"4\" cols=\"50\">");
			out.println(e.toString());
			out.println("</textarea><br>");
			out.println("Notifique al administrador del sistema");
			out.println("<br>");

			out.println("</body>");
			out.println("</html>");
		} catch (Exception e2) {

		}
	}

	private void sendJSONResponse(HttpServletResponse resp, JSONObject responseJSON) throws Exception {
		PrintWriter out = null;
		try {
			out = resp.getWriter();
			resp.getWriter();
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			out.print(responseJSON.toString());
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e2) {
				log.warn("Problemas cerrando flujo: " + e2);
			}
		}
	}
	
	public void sendGoogleJSONResponse(HttpServletResponse resp, JsonObject responseJSON) throws Exception {
		PrintWriter out = null;
		try {
			out = resp.getWriter();
			resp.getWriter();
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			out.print(responseJSON.toString());
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e2) {
				log.warn("Problemas cerrando flujo: " + e2);
			}
		}
	}

	private void sendJSONResponse(HttpServletResponse resp, ResponseJSON responseJSON) throws Exception {
		PrintWriter out = null;
		try {
			out = resp.getWriter();
			resp.getWriter();
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			out.print(responseJSON.toJSON());
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e2) {
				log.warn("Problemas cerrando flujo: " + e2);
			}
		}
	}
}
