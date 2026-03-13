package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.axtel.contratos.ContractStatus;
import com.syc.adquisiciones.ContratoCap4Interface;
import com.syc.adquisiciones.core.ContratoCap4Impl;
import com.syc.adquisiciones.core.DatosContratoCap4;
import com.syc.adquisiciones.core.FechasContratacion;
import com.syc.adquisiciones.core.PartidasContratoCap4;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class ContratoCap4Servlet extends HttpServlet {
	
	private static final long	serialVersionUID	= -8349642683144876100L;
	private static String jndiName = null;
	private static Logger log = Logger.getLogger(ContratoCap4Servlet.class);
	private Connection conn = null;
	private JSONArray arrayObj;
	private JSONObject jsonObj;
	private PrintWriter out = null;
	private String folioGenerator = null;
	
	public ContratoCap4Servlet() {
		super();
	}
	public void destroy() {
		super.destroy(); 
	}	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		boolean isModuloPluris=false;
		try {
			if (session == null) {
				log.warn("No hay sesion");
				response.sendRedirect("../index.jsp");
				return;
			}
			session.setAttribute(GestionInterface.ATT_EstatusContratCap4, request.getParameter("nIdEstatus"));
			session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, request.getParameter("cIdContratoDefinitivo"));
			session.setAttribute(GestionInterface.ATT_ContratCap4Abierto, request.getParameter("nesAbierto"));
			isModuloPluris=StringUtils.isBlank(request.getParameter("esModuloPluris"))?false:true;
			if(isModuloPluris) {
				response.sendRedirect("../Generador/SAICYS/ContratoPlurianualCap4.jsp?tab=2");
			}else {
				response.sendRedirect("../Generador/SAICYS/ContratoCap4.jsp?tab=2");
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
			e.printStackTrace();
			
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String mensaje="";
		boolean respuesta=false;
		try {
			if (session == null) {
				log.warn("No hay sesion");
				response.sendRedirect("../index.jsp");
				return;
			}
			ContratoCap4Interface cont=new ContratoCap4Impl();
			Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
			conn = DataSourceManager.getConnection(jndiName);
			DatosContratoCap4 datosCont=fullObject(request);
			arrayObj = new JSONArray();
			jsonObj = new JSONObject();
			out = response.getWriter();
			int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
			log.debug("operacion: " + tipoOperacion);
			String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
			switch(tipoOperacion){
				case 0://Nuevo
					mensaje="Contrato creado correctamente.";
					respuesta=cont.nuevo(conn,session, usuario, datosCont);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 1+"");
						session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, datosCont.getcIdcontratoDefinitivo());
						session.setAttribute(GestionInterface.ATT_ContratCap4Abierto, 0+"");
					}
					break;
				case 1://Guarda caratula del Contrato
					mensaje="El contrato se guardo correctamente";
					ArrayList<FechasContratacion> arrayDates=setArrayDates(request);
					datosCont.setArrayFechas(arrayDates);
					respuesta=cont.guardaContrato(conn, usuario, datosCont);
					break;
				case 2://Guardar Partidas
					mensaje="Partidas guardadas correctamente";
					ArrayList<PartidasContratoCap4> arrayItems=setArrayItems(request);
					datosCont.setArrayPartidas(arrayItems);
					respuesta=cont.guardaContratoPartidas(conn, usuario, datosCont);
					break;
				case 3://Aprueba el Contrato
					mensaje=new String("El contrato se aprobó  correctamente".getBytes("UTF-8"),"ISO-8859-1");
					respuesta=cont.apruebaContrato(conn, request, usuario, datosCont, folioGenerator, jndiName,prefixPath);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 2+"");
					}
					break;
				case 4://devulve el contrato
					mensaje=new String("El contrato se devolvió  correctamente".getBytes("UTF-8"),"ISO-8859-1");
					respuesta=cont.devuelveContrato(conn, usuario, datosCont);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 1+"");
					}
					break;
				case 5://Precopromete
					mensaje="Precompromiso creado correctamente";
					String cadTabla=request.getParameter("tablaDatos");
					String arrayTabla[]=cadTabla.split(",");
					ArrayList<List<String>> tabla=Util.creaArray(arrayTabla);
					respuesta=cont.precomprometer(conn, request, usuario, datosCont, tabla, prefixPath, jndiName);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 3+"");
					}
					break;
				case 6://Devolver pre-compromiso
					mensaje="El precompromiso se cancelo correctamente";
					respuesta=cont.devuelvePrecompromiso(conn, request, usuario, datosCont, prefixPath, jndiName);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 2+"");
					}
					break;
				case 7://Compromete
					mensaje="Compromiso creado correctamente";
					respuesta=cont.comprometer(conn,request, usuario, datosCont,jndiName,prefixPath);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 4+"");
					}
					break;
				case 8://Aprueba Ampliación
					mensaje=new String("La  ampliación se aprobó correctamente".getBytes("UTF-8"),"ISO-8859-1");
					respuesta=cont.apruebaAmpliacionContrato(conn, usuario, datosCont);
					break;
				case 9://devuelve la ampliaion
					mensaje=new String("La ampliación del contrato se devolvió correctamente".getBytes("UTF-8"),"ISO-8859-1");
					respuesta=cont.devuelveAmpliacion(conn, usuario, datosCont);
					break;
				case 10://pre-compromete el recurso de la ampliación
					mensaje=new String("Precompromiso creado correctamente".getBytes("UTF-8"),"ISO-8859-1") ;
					String cadTbla=request.getParameter("tablaDatos");
					String arrayTbla[]=cadTbla.split(",");
					ArrayList<List<String>> tbla=Util.creaArray(arrayTbla);
					respuesta=cont.precomprometeAmpliacion(conn, request, tbla, usuario, datosCont, jndiName, prefixPath);
					break;
				case 11://devolver pre-compromiso de una ampliación
					mensaje="El precompromiso se cancelo correctamente";
					respuesta=cont.devuelvePrecompromisoAmpliacion(conn, request, usuario, datosCont, jndiName, prefixPath);
					break;
				case 12://compromiso de una ampliaion
					mensaje="Compromiso creado correctamente";
					respuesta=cont.comprometeAmpliacion(conn, request, usuario, datosCont, jndiName, prefixPath);
					break;
				case 13://Nuevo convenio
					mensaje="Convenio Creado correctamente";
					respuesta=cont.nuevoConvenio( conn, session, usuario, datosCont );
					break;
				case 14://Actualiza convenio
					mensaje="Convenio Actualizado correctamente";
					respuesta=cont.updateConvenio( conn, session, usuario, datosCont );
					break;
				case 15://Elimina convenio
					mensaje="Convenio Eliminado correctamente";
					respuesta=cont.deleteConvenio( conn, session, usuario, datosCont );
					break;
				case 16://Migrar Contrato plurianual
					mensaje="Contrato migrado correctamente";
					respuesta=cont.migraContratoPlurianual( conn,session, usuario, datosCont );
					session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, datosCont.getcIdcontratoDefinitivo());
					break;
				case 17://Save Partidas Contrato plurianual
					mensaje="Datos Guardados";
					ArrayList<PartidasContratoCap4> array=setArrayItems(request);
					datosCont.setArrayPartidas(array);
					respuesta=cont.savePartidasContPlurianual( conn,session, usuario, datosCont );
					break;
				case 18://Aprueba el Contrato plurianual cap 4
					mensaje=new String("El contrato se aprobó  correctamente".getBytes("UTF-8"),"ISO-8859-1");
					respuesta=cont.apruebaContratoPluri(conn, request, usuario, datosCont, folioGenerator, jndiName,prefixPath);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.BUDGET);
					}
					break;
				case 19://devulve el contrato
					mensaje=new String("El contrato se devolvió  correctamente".getBytes("UTF-8"),"ISO-8859-1");
					respuesta=cont.devuelveContratoPluri(conn, usuario, datosCont);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.CAPTURED);
					}
					break;
				case 20://Precopromete
					mensaje="Precompromiso creado correctamente";
					String cadTabl=request.getParameter("tablaDatos");
					String arrayTabl[]=cadTabl.split(",");
					ArrayList<List<String>> table=Util.creaArray(arrayTabl);
					respuesta=cont.precomprometerContratoPluri(conn, request, usuario, datosCont, table, prefixPath, jndiName);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 3+"");
					}
					break;
				case 21://Devolver pre-compromiso
					mensaje="El precompromiso se cancelo correctamente";
					respuesta=cont.devuelvePrecompromisoContratoPluri(conn, request, usuario, datosCont, prefixPath, jndiName);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.BUDGET);
					}
					break;
				case 22://Compromete
					mensaje="Compromiso creado correctamente";
					respuesta=cont.comprometerContratoPluri(conn,request, usuario, datosCont,jndiName,prefixPath);
					if(respuesta){
						session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.APPROVED);
					}
					break;
				default:
					log.warn("Operación incorrecta");
					break;
			}
			if(respuesta){
				conn.commit();
			}else{
				mensaje="Error. Notifique a soporte t\u00e9nico SAI.";
				conn.rollback();
			}
		} catch (Exception e) {
			// TODO: handle exception
			try {
				respuesta=false;
				conn.rollback();
				e.printStackTrace();
				mensaje= "Error: "+ new String(e.getMessage().getBytes("UTF-8"),"ISO-8859-1");
				log.error(e.getMessage());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				mensaje= "Error: "+ new String(e1.getMessage().getBytes("UTF-8"),"ISO-8859-1");
				log.error(e1.getMessage());
			}
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				conn=null;
				jsonObj.put("RESPUESTA", respuesta);
				jsonObj.put("MENSAJE",mensaje);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
			}catch(JSONException ex){
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
			String destino = arrayObj.put(jsonObj).toString();
			out.println(destino);
		}
	}
	
	private DatosContratoCap4 fullObject (HttpServletRequest request) throws Exception{
		DatosContratoCap4 datosCont=new DatosContratoCap4();
				
		datosCont.setcIdcontratoDefinitivo(request.getParameter("cIdContratoDefinitivo")==null? "": request.getParameter("cIdContratoDefinitivo"));
		datosCont.setcIdUnidadEjecutora(request.getParameter("cIdUnidadEjecutora")==null? "":request.getParameter("cIdUnidadEjecutora"));
		datosCont.setcActividadEconomica(request.getParameter("actEconomContratoCap4")==null?1:Integer.parseInt(request.getParameter("actEconomContratoCap4")));
		datosCont.setcDescripcion(request.getParameter("cDescripcion")==null?"":request.getParameter("cDescripcion") );
		datosCont.setcNoContCNET(request.getParameter("cnumCompranet")==null?"":request.getParameter("cnumCompranet"));
		
		datosCont.setcNoProcedimientoCNET(request.getParameter("cnumProcedimientoCompranet")==null?"":request.getParameter("cnumProcedimientoCompranet"));
		datosCont.setnCondContratoCNET(request.getParameter("nCodContratoCNET")==null?"":request.getParameter("nCodContratoCNET"));
		datosCont.setnCondExpedienteCNET(request.getParameter("nCodExpedienteCNET")==null?"":request.getParameter("nCodExpedienteCNET"));
		
		datosCont.setcFolioMASCP(request.getParameter("cFolioMASCP")==null?"":request.getParameter("cFolioMASCP"));
		datosCont.setcOficioDG(request.getParameter("cOficioDG")==null?"":request.getParameter("cOficioDG"));
		
		datosCont.setnEsPlurianual(request.getParameter("isPlurianual")==null?0:Integer.parseInt(request.getParameter("isPlurianual")));
		datosCont.setnEsAbierto(request.getParameter("isAbierto")==null?0:Integer.parseInt(request.getParameter("isAbierto")));
		datosCont.setTotalPlurianual(request.getParameter("montoTotalPluri")==null?0.0:Double.parseDouble(request.getParameter("montoTotalPluri")));
		datosCont.setcCuentaDisponible(request.getParameter("cCuentaDisponible")==null? "82106":request.getParameter("cCuentaDisponible"));
		datosCont.setcEjercicio(Util.obtieneEjercicioFiscalActivo( conn ));//String.valueOf(anio)
		datosCont.setcIdRFC(request.getParameter("cIdRFC")==null? "":request.getParameter("cIdRFC"));
		datosCont.setnEsDescentralizado(request.getParameter("nEsDescentralizado")==null? 0:Integer.parseInt(request.getParameter("nEsDescentralizado")));
		datosCont.setnIdCategoria(request.getParameter("nIdCategoria")==null? 1:Integer.parseInt(request.getParameter("nIdCategoria")));
		datosCont.setnIdFundamentoLeg(request.getParameter("nIdFundamentoLeg")==null? 2:Integer.parseInt(request.getParameter("nIdFundamentoLeg")));
		datosCont.setDescripPoliza(request.getParameter("descripPoliza")==null? "":request.getParameter("descripPoliza"));
		datosCont.setnIdAmpliacion(request.getParameter("nIdAmpliacion")==null? -1 :Integer.parseInt(request.getParameter("nIdAmpliacion")));
		datosCont.setnComprometeMax(request.getParameter("nComprometeMax")==null? 0 :Integer.parseInt(request.getParameter("nComprometeMax")));
		return datosCont;
		
	}
	private ArrayList<FechasContratacion> setArrayDates(HttpServletRequest request) throws Exception{
		ArrayList<FechasContratacion> arrayFechas=new ArrayList<FechasContratacion>();
		FechasContratacion fechasCont=null;
		log.debug("Armando el array de fechas");
		String cadenaFechas=request.getParameter("cadenaFechas");
		String arrayTabla[]=cadenaFechas.split(",");
		//1-02/05/2016,2-03/05/2016,4-04/05/2016,12-05/05/2016,17-06/05/2016,18-09/05/2016
		String arrayValores[]=null;
		for(int i=0; i<arrayTabla.length;i++){
			fechasCont=new FechasContratacion();
			arrayValores=arrayTabla[i].split("-");
			fechasCont.setIdFecha(Integer.parseInt(arrayValores[0]));
			fechasCont.setValue(arrayValores[1]);
			log.info("IdFecha="+arrayValores[0]+" fecha="+arrayValores[1]);
			arrayFechas.add(fechasCont);
			fechasCont=null;
			arrayValores=null;
		}
		return arrayFechas;
	}
	private ArrayList<PartidasContratoCap4> setArrayItems(HttpServletRequest request) throws Exception{
		ArrayList<PartidasContratoCap4> arrayPartida=new ArrayList<PartidasContratoCap4>();
		PartidasContratoCap4 partidaCont=null;
		log.debug("Armando el array de partidas");
		//cadenaPartidas+=aData[0]+"-"+$("#cDescripAdi_"+aData[0]).val()+"-"+cantMin+"-"+cantMax+"-"+precioU+"-"+precioUMax
		//             +"-"+mMontoNetoLine+"-"+mMontoNetoMin+"-"+mMontoNetoMax+"-"+mMontoNetoPluri+"-"+nIdIVA+"-"+mMontoNetoLineOrig+"-"+mMontoNetoMaxOrig+"-"+cidUniMed;
		
		String cadenaPartidas=request.getParameter("cadenaPartidas");
		log.info("Cadena : "+cadenaPartidas);
		String arrayTupla[]=cadenaPartidas.split(",");
		String arrayValores[]=null;
		for(int i=0;i<arrayTupla.length;i++){
			partidaCont=new  PartidasContratoCap4();
			arrayValores=arrayTupla[i].split("-");
			partidaCont.setnIdContatoPartida(Integer.parseInt(arrayValores[0]));
			partidaCont.setcDescripAdi(arrayValores[1]);
			partidaCont.setnCantidadMin(Integer.parseInt(arrayValores[2]));
			partidaCont.setnCantidadMax(Integer.parseInt(arrayValores[3]));
			partidaCont.setmPrecioU( Math.round( Double.parseDouble(arrayValores[4])*100)/100.0d );
			partidaCont.setmPrecioUMax( Math.round( Double.parseDouble(arrayValores[5])*100)/100.0d  );
			partidaCont.setmMontoNetoLinea( Math.round( Double.parseDouble(arrayValores[6])*100)/100.0d );
			partidaCont.setmMontoNetoMin(Math.round( Double.parseDouble(arrayValores[7])*100)/100.0d );
			partidaCont.setmMontoNetoMax(Math.round( Double.parseDouble(arrayValores[8])*100)/100.0d);
			partidaCont.setmMontoNetoPluri(Math.round( Double.parseDouble(arrayValores[9])*100)/100.0d  );
			partidaCont.setnIdIVA(Integer.parseInt(arrayValores[10]));
			partidaCont.setmMontoNetoLineaOrig( Math.round( Double.parseDouble(arrayValores[11])*100)/100.0d);
			partidaCont.setmMontoNetoMaxOrig( Math.round( Double.parseDouble(arrayValores[12])*100)/100.0d);
			partidaCont.setcIdUnidadMedida(arrayValores[13]);
			
			log.info("IdContatoPartida="+arrayValores[0]+" DescripAdi="+arrayValores[1]+" CantidadMin="+arrayValores[2]+" CantidadMax="+arrayValores[3]+" PrecioU="+arrayValores[4]
				+" PrecioUMa="+arrayValores[5]+" MontoNetoLinea="+arrayValores[6]+" MontoNetoMin="+arrayValores[7]+" MontoNetoMax="+arrayValores[8]+" MontoNetoPluri="+arrayValores[9]
					+" nIdIVA="+arrayValores[10]+" MontoNetoLineaOrig="+arrayValores[11]+" MontoNetoMaxOrig="+arrayValores[12]+" cidUniMed="+arrayValores[13]);
			
			arrayPartida.add(partidaCont);
			arrayValores=null;
			partidaCont=null;
		}
		
		return arrayPartida;
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init(ServletConfig config) throws ServletException {
		//Crea la conexión a BD
		super.init(config);
		try {
			InitialContext ic = new InitialContext();
			jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
			if (jndiName == null) {
				jndiName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \""+ jndiName + "\"");
			} else
				log.info("dataSourceRefName=" + jndiName);
		} catch (NamingException exc) {
			jndiName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \""+ jndiName + "\"");
		}
		// Put your code here
		try {
			InitialContext ic = new InitialContext();
			folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
			if (folioGenerator == null) {
				folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
				log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \""+ folioGenerator + "\"");
			} else
				log.info("folioGeneratorInterface=" + folioGenerator);
		} catch (NamingException exc) {
			folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
			log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \""+ folioGenerator + "\"");
		}
	}

}
