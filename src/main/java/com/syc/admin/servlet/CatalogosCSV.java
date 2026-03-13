package com.syc.admin.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import com.syc.adquisiciones.businessLogic.ReportesBusinesLogic;
import com.syc.crud.dsmngr.DataSourceManager;

public class CatalogosCSV extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String jndiName = null;
	private static Logger log = Logger.getLogger(CatalogosCSV.class);
	private String folioGenerator = null;
	private CallableStatement cmst = null;
	
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

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request,response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String tipoArchivo=request.getParameter("rn");
			//System.out.println(tipoArchivo);
			if( tipoArchivo!=null && tipoArchivo.equals("rpt_mProgramaAnualOrdenadoPorPartida_xls.jasper"))
				mProgramaAnualOrdenadoPorPartida(request,response);
			else if( tipoArchivo!=null && tipoArchivo.equals("rpt_programaAnualResumidoPorCapitulosPartidasNacionalOarea.jasper")) {
				mProgramaAnualResumidoPorPartidaCapitulo(request,response);		
			}
			else if(tipoArchivo!=null && tipoArchivo.equals("rpt_mProgramaAnualOrdenadoPorPartidaAcumulado.jasper"))
				mProgramaAnualOrdenadoPorPartidaAcumulado(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("rpt_mProgramaAnualPorPartidayTrimestre.jasper"))
				mProgramaAnualPorPartidayTrimestre(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("fn_mProgramaAnualMontosPorCapituloReporte.jasper"))
				mProgramaAnualMontosPorCapituloReporte(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("rpt_CompraNET.jasper"))
				mProgramaAnualOrdenadoPorPartida_COMPRANET(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados.jasper"))
				rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("rpt_mMultiReporte.jasper")) ///////////////////////////////////////
				mMultiReporte(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("Pedido.jasper"))
				Pedido(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("rptPedidoAnexo1.jasper"))
				PedidoAnexo1(request,response);
			else if(tipoArchivo!=null && tipoArchivo.equals("rptPedidoAnexo2.jasper"))
				PedidoAnexo2(request,response);
			else if (tipoArchivo != null && tipoArchivo.equals("rptmConsolidadoCompranet.csv")){
				reporteConsolidadoCompranet(request, response);
			}
			else if (tipoArchivo != null && tipoArchivo.equals("rptmReporteEjercido.csv"))
				mReporteEjercido(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas.jasper"))
				rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("fn_mProgramaAnualConciliacionPresupuestal.jasper"))
				fn_mProgramaAnualConciliacionPresupuestal(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("rpt_mEjercido"))
					mReporteEjercidoMat(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("mConsiliacion_RC_Apartado_Precompromiso.jasper"))
				mConsiliacion_RC_Apartado_Precompromiso(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("mConsiliacion_AP_PRE_Comp_Eje.jasper"))
				mConsiliacion_AP_PRE_Comp_Eje(request, response);	
			else if (tipoArchivo != null && tipoArchivo.equals("mCatalogoInventarioArticulosdeAlmacen.jasper"))
				mCatalogoInventarioArticulosdeAlmacen(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("ReporteConsolidado.jasper"))
				ReporteConsolidado(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("rptContratoAnexo1.jasper"))
				ReporteContratoAnexo1(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("rptContratoAnexo.jasper"))
				ContratoClavesComplementarias(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("rptRequisiciones.jasper"))
				ReporteRequisiciones(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("rptPedidoClavesComplementarias.jasper"))
				ReportePedidoClaves(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("ReporteRequisiciones"))
				ReporteConsultaReq(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("ReporteConsolidados"))
				ReporteConsultaConsolidados(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("ReporteConsultaPedidos"))
				ReporteConsultaPedidos(request, response);
			else if (tipoArchivo != null && tipoArchivo.equals("ReporteConsultaContratos"))
				ReporteConsultaContratos(request, response);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOCDetallado.jasper"))
				ReporteOC(request, response, 0);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOCPartida.jasper"))
				ReporteOC(request, response, 1);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOCCUCOP.jasper"))
				ReporteOC(request, response, 2);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteBitacora.jasper"))
				ReporteConsultaBitacora(request, response);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOIC1.jasper"))
				ReporteConsultaOIC1(request, response);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteConvenioMod.jasper"))
				ReporteConvenio(request, response);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOCPartidaPedCont.jasper"))
				ReporteOC(request, response, 3);
			else if(tipoArchivo != null && tipoArchivo.equals("Reporte_Totalizado"))
				ReporteOC(request, response, 4);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOCDetalladoASF.jasper"))
				ReporteOC(request, response, 5);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteMIPyMes.jasper"))
				ReporteOC(request, response, 6);
			else if(tipoArchivo != null && tipoArchivo.equals("Formato7030"))
				ReporteOC(request, response, 7);
			else if(tipoArchivo != null && tipoArchivo.equals("reporteOCTotalizadoPorTipoRec.jasper"))
				ReporteOC(request, response, 8);
			else if(tipoArchivo != null && tipoArchivo.equals("ReporteCOCODI"))
				ReporteOC(request, response, 9);
			else if(tipoArchivo != null && tipoArchivo.equals("reportesConPlantillaXLS"))
				ReportesConPlantillaXLS(request, response);
			else{
				System.out.println("No se encontro el archivo "+tipoArchivo);
				log.warn("No se encontro el archivo "+tipoArchivo);
				throw new Exception("No se encontro el archivo "+tipoArchivo);
			}
				
		}catch ( Exception e ) {
			log.error("Error: "+e.getMessage());
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	private void ReportesConPlantillaXLS(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReportesBusinesLogic rbl=new ReportesBusinesLogic(jndiName);
		try {
			String plantillaPath=getServletContext().getRealPath("Reportes" + File.separator + "ApartadoPrecomComp.xls");
			rbl.conciliacionesGRM(response,plantillaPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw ( e );
		}
	}
	private void ReporteOC(HttpServletRequest request, HttpServletResponse response,int reporte) throws IOException, SQLException {
		String unidadE=(null==request.getParameter("cIdUnidadEjecutora") || "".equals( request.getParameter("cIdUnidadEjecutora") )?"":request.getParameter("cIdUnidadEjecutora"));
		int tipoIngreso=(null==request.getParameter("nTipoIngreso") || "".equals( request.getParameter("nTipoIngreso") )?-1:Integer.parseInt(request.getParameter("nTipoIngreso")));
		String fechaInicio=(null==request.getParameter("fechaInicio") || "".equals( request.getParameter("fechaInicio") )?"":request.getParameter("fechaInicio"));;
		String fechaFin=(null==request.getParameter("fechaFin") || "".equals( request.getParameter("fechaFin") )?"":request.getParameter("fechaFin"));;
		String query="";
		String filename=null;
		
		switch(reporte){
			case 0:
				String encabezadoD[]= {"_UnidadEjecutora","_AreaResponsable","_FundamentoLegal","_Requisicion","_PedidoContrato","_cNoContratoCNET","_claveProveedor",
					"_RazonSocial","_Pime","_DelegacionMucipioProveedor","_Colonia","_Calle","_NoExternoProveedor","_NoInternoProveedor","_RFC","_CURP","_DescripcionPedCont"
					,"_CUCOP","_FechaFormalizacionPedCont","_FechaCreaciónDocumento","_FechaInicio","_FechaFin","_Cotizador","EsPlurianual","_Monto-total-Pluri-Anual","_EP","_MontoNetoCompromiso_OrigEP"
					,"_MontoNetoMinimo_PedCont","_MontoNetoMaximo_PedCont","_MontoNetoPedContEP_AmpDelMax"
					,"_MontoNetoPedContEP_Conv","_MontoNetoPedContEP_Mod","_ImporteNetoPagado_EP","_ImporteReducido","_FueJustificadoTipoProcedimiento"
					,"_JustificaciónTipoProcedimiento","CodExpedienteCNET","CodContratoCNET"};
				filename = "OrdenesCompraDetallado.csv";
				query = "select *from v_mReporteOCDetallado with(Nolock) "+ " where 1=1 "+unidadE+
						" union select *from v_mReporteOCDetalladoPlu  with(Nolock) "+
						" where 1=1 "+unidadE;
				ReporteConsultaOC(filename, query, encabezadoD, request, response);
				break;
			case 1:
				String encabezadoPart[]={"_UnidadEjecutora","_AreaResponsable","_FundamentoLegal","_Requisicion","_PedidoContrato","_cNoContratoCNET"
					,"_claveProveedor","_RazonSocial","_Pime","_DelegacionMucipioProveedor","_Colonia"
					,"_Calle","_NoExternoProveedor","_NoInternoProveedor","_RFC","_CURP"
					,"_DescripcionPedCont","_CUCOP","_FechaFormalizacionPedCont","_Fecha-Creación-Documento","_Fecha-Inicio","_Fecha-Fin","_Cotizador","_EsPlurianual","_MontoTotalPlurianual","_Partida"
					,"_MontoNetoCompromiso_OrigPartida","_MontoNetoMinimo_Partida","_MontoNetoMaximo_Partida","_MontoNetoPartida_AmpDelMax","_MontoNetoPartida_Conv","_MontoNetoPartida_Mod"
					,"_ImporteNetoPagado_Partida","_ImporteReducido_Partida","_FueJustificadoTipoProcedimiento"
					,"_JustificaciónTipoProcedimiento","CodExpedienteCNET","CodContratoCNET"};
				filename = "OrdenesCompraPartidasContPed.csv";
				query = "select *from v_mReporteOCPartida with(Nolock) where 1=1 "+unidadE;
				ReporteConsultaOC(filename, query, encabezadoPart, request, response);
				break;
			case 2:
				String encabezadoCucop[]={"_UnidadEjecutora","_Area_Responsable","_FundamentoLegal","_Requisicion","_PedidoContrato"
					,"_cNoContratoCNET","_claveProveedor","_RazonSocial","_Pime","_DelegacionMucipioProveedor","_Colonia"
					,"_Calle","_NoExternoProveedor","_NoInternoProveedor","_RFC","_CURP","_DescripcionPedCont","_CUCOP"
					,"_FechaFormalizacionPedCont","_FechaDocumento","_Fecha-Inicio","_Fecha-Fin","_Cotizador","_EsPlurianual","_MontoTotalPlurianual"
					,"_MontoNetoCompromiso_OrigCucop","_TotalMinimo_Cucop","_TotalMaximo_Cucop","_FueJustificadoTipoProcedimiento"
					,"_JustificaciónTipoProcedimiento","CodExpedienteCNET","CodContratoCNET"};
				query = "select *from v_mReporteOCCUCOP with(Nolock) where 1=1 "+unidadE;
				filename = "OrdenesCompraCUCOP.csv";
				ReporteConsultaOC(filename, query, encabezadoCucop, request, response);
				break;
			case 3:
				String encabezadoPartContPed[]={"_UnidadEjecutora","_AreaResponsable","_FundamentoLegal","_Requisicion","_PedidoContrato","_cNoContratoCNET","_claveProveedor",
					"_RazonSocial","_Pime","_DelegacionMucipioProveedor","_Colonia","_Calle","_NoExternoProveedor","_NoInternoProveedor","_RFC","_CURP","_DescripcionPedCont"
					,"_CUCOP","_FechaFormalizacionPedCont","_FechaCreaciónDocumento","_FechaInicio","_FechaFin","_Cotizador","_PartidaPedCont","EsPlurianual","_Monto-total-Pluri-Anual"
					,"_MontoNetoCompromiso_Orig","_MontoNetoComprometido_porPartida","_MontoNetoMinimo_porPartida","_MontoNetoMaximo_PorPartida","_MontoNetoPorPartida_AmpDelMax"
					,"_MontoNetoPorPartida_Conv","_ImporteNetoPagado_PorLinea","_ImporteNetoPagado_Contrato","_FueJustificadoTipoProcedimiento"
					,"_JustificaciónTipoProcedimiento","CodExpedienteCNET","CodContratoCNET"};
				query = "select *from v_mReporteOCPartidaContPed with(Nolock) where 1=1 "+unidadE;
				filename = "OrdenesCompraPartidasContPed.csv";
				ReporteConsultaOC(filename, query, encabezadoPartContPed, request, response);
				break;
			case 4:
				String encabezadoTotalizado[]={"_Unidad_Ejecutora","_Area_Responsable","Ripo_Procedimiento","_Fundamento_Legal","_Requisición","_Pedido/Contrato","_cNoContratoCNET","_claveProveedor",
					"_RazonSocial","_Pime","_DelegacionMucipioProveedor","_Colonia","_Calle","_NoExternoProveedor","_NoInternoProveedor","_RFC","_CURP","_DescripcionPedCont"
					,"_CUCOP","_Partida_Presupuestal","_Tipo_Terminación_Contrato","_Fecha_Terminación_Contrato","_FechaFormalizacionPedCont","_FechaCreaciónDocumento","_FechaInicio","_FechaFin","_Cotizador","EsPlurianual","_Monto-total-Pluri-Anual"
					,"_MontoNetoCompromiso_Orig","_MontoNetoCompromiso_OrigRecFiscal","_MontoNetoCompromiso_OrigIngPropios","_MontoNetoMinimo","_MontoNetoMaximo","_MontoNeto_AmpDelMax"
					,"_MontoNeto_Conv","_ImporteNetoPagado_Contrato","_Deducciones/Descuentos_con_IVA","_Penas_Convencionales_con_IVA","_ImporteReducido_contrato","_ImporteAmpliado","_FueJustificadoTipoProcedimiento"
					,"_JustificaciónTipoProcedimiento","_CodExpedienteCNET","CodContratoCNET","_ImporteNetoPagado_Enero","_ImporteNetoPagado_Febrero","_ImporteNetoPagado_Marzo"
					,"_ImporteNetoPagado_Abril","_ImporteNetoPagado_Mayo","_ImporteNetoPagado_Junio","_ImporteNetoPagado_Julio","_ImporteNetoPagado_Agosto"
					,"_ImporteNetoPagado_Septiembre"
					,"_ImporteNetoPagado_Octubre"
					,"_ImporteNetoPagado_Noviembre"
					,"_ImporteNetoPagado_Diciembre"
					,"_Observaciones"};
						
					query = "select *from v_mReporteOCTotalizado with(Nolock) where 1=1 "+ unidadE
						+" union select *from v_mReporteOCTotalizadoPlu  with(Nolock) where 1=1 "+unidadE
						+" union select *from v_mreporteObraTotalizado  with(Nolock) where 1=1 "+unidadE
						+" union select *from v_mReporteOcTotalizadoConvEjerAnt  with(Nolock) where 1=1 "+unidadE
						+" union select *from v_mReporteOcTotalizadoRemanente  with(Nolock) where 1=1 "+unidadE
						;
				filename = "OrdenesCompraTotalizado.csv";
				ReporteConsultaOC(filename, query, encabezadoTotalizado, request, response);
				break;
			case 5:
				String encabezadoD_ASF[]= {"CENTRO DE TRABAJO","TIPO DE ADJUDICACIÓN","NÚMERO DE PEDIDO O CONTRATO",
					"PROVEEDOR O PRESTADOR DEL SERVICIO ","CONCEPTO DE COMPRA O SERVICIO"
					,"FECHA PEDIDO O CONTRATO","FECHA INICIO","FECHA FIN","CLAVE DEL PROGRAMA"
					,"IMPORTE CON  IVA POR CLAVE DEL PROGRAMA","IMPORTE PAGADO POR CLAVE DEL PROGRAMA"
					};
				filename = "REPORTE PEDIDOS-CONTRATOS.csv";
				query = "select areaRequirente,FundamentoLegal,cNoContratoCNET,RazonSocial,DescripcionPedCont,fechaDocumento,fechaInicio,fechaFin "
						+",EP,(MontoNetoPedContCompromiso_OrigEP+MontoNetoPedContEP_AmpDelMax+MontoNetoPedContEP_Conv) as importeContrato "
						+",ImporteNetoPagado_EP from v_mReporteOCDetallado with(Nolock) where 1=1 "+unidadE;
				ReporteConsultaOC(filename, query, encabezadoD_ASF, request, response);
				break;
			case 6:
				String encabezadoMIPyMes[]= {"Sector","Entidad","Tipo de procedimiento",
					"Descripción del Bien o Servicio","Clave CUCOP"
					,"Tipo","Monto Fallo","FECHA","Número de Procedimiento en COMPRANET","Número de registro"
					,"Nombre de la PYME Contratada","RFC","Teléfono","Correo Electrónico","Manifestación MIPYME","Contrato mayor a 300 salarios mínimos"
					};
				filename = "REPORTE MiPyme.csv";
				query = "select sector,entidad,tipoProced,replace(cConceptoPedido,',','') cConceptoPedido,CUCOP,tipo,mImporteTotal,fechaCreacion "
						+",numPrcedCNET,numRegistro,RazonSocial,RFC,telefono,cEmail,manifest,contratomayor300sal from v_mReporteMIPyMes with(Nolock) where 1=1 "+unidadE;
				ReporteConsultaOC(filename, query, encabezadoMIPyMes, request, response);
				break;
			case 7:
				String encabezado70_30[]= {"FO-70/30-01","Cálculo y determinación del porcentaje del 30% a que se refiere el artículo 42 de "
					+"la Ley de Adquisiciones, Arrendamientos y Servicios del Sector Público (Miles de pesos)","Dependencia o Entidad: _____"
					,"Periodo: _______","CONCEPTO"	,"PRESUPUESTO ANUAL AUTORIZADO","CONTRATACIONES FORMALIZADAS CON CONTRATO FIRMADO","CLAVE","DESCRIPCIÓN","PRESUPUESTO  ANUAL AUTORIZADO"
					,"(Incluye modificaciones, en su caso)","ENTRE DEPENDENCIAS Y ENTIDADES ","(Párrafo quinto del art.1 de la Ley)"," ARTÍCULO 42","ADJUDICACIÓN DIRECTA"
					,"INVITACIÓN A CUANDO MENOS TRES PERSONAS"," ARTÍCULO 41","PATENTE I","COSTOS ADICIONALES III","MARCA DETERMINADA VIII","OTROS  II, IV a VII y IX a XX"
					,"LICITACIÓN PÚBLICA","(Arts. 26, 26 Bis y 28 de la Ley)"
				};
				filename = "REPORTE 70_30.csv";
				if(tipoIngreso==4){
					filename = "REPORTE 70_30IP.csv";
					query = "SELECT *FROM fn_mReporte7030IP('"+fechaInicio+"','"+fechaFin+"',"+tipoIngreso+") order by capitulo";
				}else if(tipoIngreso==1){
					filename = "REPORTE 70_30Fiscal.csv";
					query = "SELECT *FROM fn_mReporte7030Fiscal('"+fechaInicio+"','"+fechaFin+"',"+tipoIngreso+") order by capitulo";
				}else if(tipoIngreso==0){
					filename = "REPORTE 70_30Fiscal_IP.csv";
					query = "SELECT *FROM fn_mReporte7030('"+fechaInicio+"','"+fechaFin+"') order by capitulo";
				}else{
					query = "SELECT *FROM fn_mReporte7030Fiscal('"+fechaInicio+"','"+fechaFin+"',-1) order by capitulo";
				}
				
				ReporteConsulta_70_30(filename, query, encabezado70_30, request, response);
				break;
			case 8:
				int ntipoRec= Integer.parseInt(request.getParameter("nTipoIngreso"));
				String[] encabezadoTotalizadoTipoRec={"_UnidadEjecutora","_AreaResponsable","_FundamentoLegal","_Requisicion","_PedidoContrato","_cNoContratoCNET","_claveProveedor",
					"_RazonSocial","_Pime","_DelegacionMucipioProveedor","_Colonia","_Calle","_NoExternoProveedor","_NoInternoProveedor","_RFC","_CURP","_DescripcionPedCont"
					,"_CUCOP","_FechaFormalizacionPedCont","_FechaCreaciónDocumento","_FechaInicio","_FechaFin","_Cotizador","EsPlurianual","_Monto-total-Pluri-Anual"
					,"_MontoNetoCompromiso_Orig","_MontoNetoMinimo","_MontoNetoMaximo","_MontoNeto_AmpDelMax"
					,"_MontoNeto_Conv","_ImporteNetoPagado_Contrato","_ImporteReducido_contrato","_ImporteAmpliado","_FueJustificadoTipoProcedimiento"
					,"_JustificaciónTipoProcedimiento","CodExpedienteCNET","CodContratoCNET"};
				
				query = "select *from fn_mReporteOCTotalizado ("+request.getParameter("nTipoIngreso")+") where 1=1 "+ unidadE
					+" union select *from fn_mReporteOCTotalizadoPlu("+request.getParameter("nTipoIngreso")+") where 1=1"+unidadE;
				
				filename = "TOTALIZADO_REURSO_FISCAL.csv";
				if(ntipoRec!=1){
					filename = "TOTALIZADO_INGRESOS_PROPIOS.csv";
				}
				ReporteConsultaOC(filename, query, encabezadoTotalizadoTipoRec, request, response);
				break;
			case 9:
				String encabezadoCOCODI[]= {"Capítulo","Artículo 1 (parrafo tercero) Entre dependencia y entidades","Excepciones (Artículo 42) Adjudicación directa"
					,"Excepciones (Artículo 42) Invitación a cuando menos tres personas","Contrataciones Dictaminadas (Artículo 41)"
					,"Licitación pública","TOTAL"
				};
				filename = "Reporte Doc de Gestion.csv";
				query = "select *from fn_mReporteCOCODI('"+request.getParameter("fechaInicio")+"','"+request.getParameter("fechaFin")
					+"') UNION select 'TOTAL'total,SUM(A)A,SUM(B)B,SUM(C)C,SUM(D)D,SUM(E)E,SUM(Total)total from fn_mReporteCOCODI('"+request.getParameter("fechaInicio")+"','"+request.getParameter("fechaFin")+"') ";
				ReporteConsultaOC(filename, query, encabezadoCOCODI, request, response);
				break;
			default:
				log.warn("Reporte no encontrado: ."+request.getParameter("rn"));
				break;
		
		}
		
	}
	private void ReporteConvenio (HttpServletRequest request, HttpServletResponse response) throws IOException {
		String filename = "ReporteConvenio.csv";
		String unidadE=request.getParameter("where");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		try {
			String aEncabezado[]={"_No","_AreaRequirente","_cIdContrato","_cIdContratoMod","_No.Convenio","_ObjetoConvenio"
				,"_Fecha_Modificación","_TotalAnterior","_TotalModificacion","_TotalNuevo","_Estatus","_PartidaContrato","_cIdSolicitud","_DEscripción"
				,"_DEscripcion_Adicional","_Cantidad","_Precio_Unitaario","_Monto_Neto_Mod_Partida"};
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			creaEncabezado(fw, aEncabezado);
			conn = DataSourceManager.getConnection(jndiName);
			String query = "select ROW_NUMBER()OVER(ORDER BY cmp.nIdLineaConsolidado) AS Row,"
				+"'['+cmp.cIdUnidadEjecutora+'] '+cue.D_DESCRIPCION as ueDescripcion"
				+",cm.cIdContratoDefinitivo,cm.cContratoDefinitivo AS cContratoDefinitivo"
				+",cNoConvenio as cNoConvenio,isnull(cm.cObjetoConvenio,'No hay descripción del convenio') as cObjetoConvenio"
				+",convert(varchar,cm.fMod) FechaMod,cm.mTotalAnterior,cm.mTotalModificacion"
				+",cm.mTotalNuevo,cec.cEstado,cmp.nIdLineaConsolidado,cmp.cIdSolicitud"
				+",cmp.cDescripcion,sl.cDescripcionAdicional,cmp.nCantidad as nCantidadLinea"
				+",cmp.mPrecioUnitario,cmp.mMontoNeto as montoNetoLinea"
				+" from mContratoModificado as cm with(nolock)"
				+" inner join mCatalogoEstadoContrato cec with(nolock) on cec.nIdEstado=cm.nEstado"
				+" inner join mContratoModificadoPartida as cmp with(nolock) on cmp.cIdContratoDefinitivo=cm.cIdContratoDefinitivo"
				+" and cmp.nConsecutivoModificacion=cm.nConsecutivoModificacion"
				+" inner join mSolicitudLineas as sl with(Nolock) on sl.cIdSolicitud=cmp.cIdSolicitud and cmp.cIdLineaSolicitud=sl.nIdLineaSolicitud"
				+" inner join tCatUnidadEjecutora as cue with(Nolock) on cue.cUnidadEjecutora=cmp.cIdUnidadEjecutora"
				+" where 1=1"+unidadE+" order by cmp.nIdLineaConsolidado";
			log.info("Query del reporte Convenio \n"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			log.info("Creando el detalle del Reporte "+filename);
			fw=creaDetalle(fw, rs, aEncabezado);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: "+e.getMessage());
			e.printStackTrace();
		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
		
	}
	private void ReporteConsultaOIC1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String filename = "ReporteOIC1.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		try {
			log.info("Creando el encabezado del reporte.");
			String aEncabezado[]={"Consecutivo","No Pedido o contrato","Número de control interno del contrato"
				,"Código del contrato en CompraNet en caso existir","Breve descripción del bien servicio u obra objeto de la contratación","Partida Presupuestal","Fecha de formalización del contrato",
				"Fecha de inicio del contrato","Fecha de término del contrato","MONTO DEL CONTRATO CON IVA","MONTO DEL CONTRATO SIN IVA","Tipo de procedimiento de contratación"
				,"Artículo (s) en que se fundamentó la contratación","Proveedor o contratista"};
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			creaEncabezado(fw, aEncabezado);
			conn = DataSourceManager.getConnection(jndiName);
			String query = "select ROW_NUMBER() OVER(ORDER BY cIdUnidadEjecutora asc) AS Row,* from v_mReporteOIC1 with(nolock) where 1=1 "+unidadE;
			log.info("Query del reporte OIC1 \n"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			log.info("Creando el detalle del Reporte "+filename);
			fw=creaDetalle(fw, rs, aEncabezado);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: "+e);
			e.printStackTrace();
		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
	}
	private void ReporteConsultaBitacora(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String filename = "Bitacora.csv";
		String documento=request.getParameter("documento_");
		String usuario=request.getParameter("usuario");
		String rangoFechas=request.getParameter("rangoFechas");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		try {
			log.info("Creando el encabezado del reporte.");
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("_Documento");fw.append(',');	
			fw.append("_Accion");fw.append(',');
			fw.append("_Usuario");fw.append(',');
			fw.append("_FechaRegistro");fw.append('\n');
			conn = DataSourceManager.getConnection(jndiName);
			String query = "select isnull(cIdDocumento,'') cIdDocumento,isnull(cAccion,'')cAccion,isnull(cIdUsuario,'')cIdUsuario,isnull(fRegistro,'')fRegistro from mBitacoraMovimientos where 1=1 "+documento+usuario+rangoFechas;
			log.info("Query del reporte de la Bitacora \n"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1).replaceAll(",", ""));
				fw.append(',');
				fw.append(rs.getString(2).replaceAll(",", ""));
				fw.append(',');
				fw.append(rs.getString(3).replaceAll(",", ""));
				fw.append(',');
				fw.append(rs.getString(4).replaceAll(",", ""));
				fw.append('\n');
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: "+e);
			e.printStackTrace();
		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
	}
	private void ReporteConsulta_70_30(String filename,String query,String encabezado[],HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException{
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		log.info("Encabezado del reporte.\n"+encabezado);
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		for(int i=0; i<11;i++){
			if(i==4){
				fw.append(encabezado[0]);
			}
			fw.append(',');
		}
		fw.append('\n');
		conn = DataSourceManager.getConnection(jndiName);
		log.info("query: "+query);
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		int cap=0;
		double suamA=0;
		double suamB=0;
		double suamC=0;
		double suamD=0;
		double suamE=0;
		double suamF=0;
		double suamG=0;
		double suamH=0;
		double suamI=0;
		while(rs.next()){
			if(cap!=rs.getInt(1)){
				if(rs.getInt(1)==2){
					fw.append(',');
					fw.append(rs.getString(2).replaceAll(",", ""));fw.append(',');
					fw.append("(A)");fw.append(',');
					fw.append("(B)");fw.append(',');
					fw.append("(C)");fw.append(',');
					fw.append("(D)");fw.append(',');
					fw.append("(E)");fw.append(',');
					fw.append("(F)");fw.append(',');
					fw.append("(G)");fw.append(',');
					fw.append("(H)");fw.append(',');
					fw.append("(I)");
				}else{
					//Sumatorias
					fw.append(',');
					fw.append("Total : ");fw.append(',');
					fw.append(suamA+"");fw.append(',');
					fw.append(suamB+"");fw.append(',');
					fw.append(suamC+"");fw.append(',');
					fw.append(suamD+"");fw.append(',');
					fw.append(suamE+"");fw.append(',');
					fw.append(suamF+"");fw.append(',');
					fw.append(suamG+"");fw.append(',');
					fw.append(suamH+"");fw.append(',');
					fw.append(suamI+"");
					fw.append('\n');
					fw.append(',');
					fw.append(rs.getString(2).replaceAll(",", ""));fw.append(',');
				}
				fw.append('\n');
				cap=rs.getInt(1);
				suamA=0;
				suamB=0;
				suamC=0;
				suamD=0;
				suamE=0;
				suamF=0;
				suamG=0;
				suamH=0;
				suamI=0;
			}
			suamA+=rs.getDouble(5);
			suamB+=rs.getDouble(6);
			suamC+=rs.getDouble(7);
			suamD+=rs.getDouble(8);
			suamE+=rs.getDouble(9);
			suamF+=rs.getDouble(10);
			suamG+=rs.getDouble(11);
			suamH+=rs.getDouble(12);
			suamI+=rs.getDouble(13);
			for(int i=3; i<14; i++){
				if(i==4){
					fw.append(rs.getString(i).replaceAll(",", ""));
				}else{
					fw.append(rs.getString(i));
				}
				fw.append(',');
			}
			fw.append('\n');
		}
		//Sumatorias
		fw.append(',');fw.append("Total : ");fw.append(',');
		fw.append(suamA+"");fw.append(',');
		fw.append(suamB+"");fw.append(',');
		fw.append(suamC+"");fw.append(',');
		fw.append(suamD+"");fw.append(',');
		fw.append(suamE+"");fw.append(',');
		fw.append(suamF+"");fw.append(',');
		fw.append(suamG+"");fw.append(',');
		fw.append(suamH+"");fw.append(',');
		fw.append(suamI+"");
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
	}
	private void ReporteConsultaOC(String filename,String query,String encabezado[],HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		log.info("Encabezado del reporte.\n"+encabezado);
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar+filename);
		for(int i=0; i<encabezado.length;i++){
			if(i!=0){
				fw.append(',');
			}
			fw.append(encabezado[i]);
		}
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		log.info("query: "+query);
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			for(int i=1; i<=encabezado.length;i++){
				if(i!=1){
					fw.append(',');
				}
				if(i==4 || i==18 || i==19){
					fw.append(rs.getString(i).replaceAll(",", "|"));
				}else{
					fw.append((null==rs.getString(i))?"":rs.getString(i).replaceAll(",", ""));
				}
			}
			fw.append('\n');
		}
		
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
	}
	private void mMultiReporte(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "mMultiReporte.csv";
		//String unidadE=request.getParameter("cIdUnidadEjecutora");
		String vFields=request.getParameter("encabezado");	//	Trae los campos a desplegar ya llegan separados por coma
		String query=request.getParameter("query");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		try {			
			System.out.println(vFields);
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append(vFields);
			fw.append('\n');
			conn = DataSourceManager.getConnection(jndiName);
			
			
			System.out.println("query= "+query); // +"unidadE= "+unidadE);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			
			int i = 0;
			int cuantasColumnas = 0;
			
			cuantasColumnas = rs.getMetaData().getColumnCount();
			// Nombre de columnas
			/*for (i = 1; i <= cuantasColumnas; i++) {
				fw.append("" + rs.getMetaData().getColumnName(i));
				if (i != cuantasColumnas)
					fw.append(',');
				else
					fw.append('\n');
			}*/
			while (rs.next()) {
				//renglon+=1;
				for (i = 1; i <= cuantasColumnas; i++) {
					
					String dummyStr = rs.getString(i);
										
				if (dummyStr != null ){
					 dummyStr = dummyStr.replaceAll("[\\,\\s]", " ");
					}
				
					else
					 dummyStr = " ";
					fw.append(dummyStr);
					if (i != cuantasColumnas)
						fw.append(',');
					else
						fw.append('\n');
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
		
		
			
	}
	private void Pedido(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "Pedido.csv";
		String cIdUnidadEjecutora=request.getParameter("cIdUnidadEjecutora");
		String cEjercicio=request.getParameter("cEjercicio");
		String cIdTipoPedido=request.getParameter("cIdTipoPedido");
		String nIdConsecutivo=request.getParameter("nIdConsecutivo");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("cEjercicio");
			fw.append(',');
			fw.append("cIdPedidoDefinitivo");
			fw.append(',');
			fw.append("fEntrega");
			fw.append(',');
			fw.append("cIdRFC");
			fw.append(',');
			fw.append("cRazonSocial");
			fw.append(',');
			fw.append("cCalle");
			fw.append(',');
			fw.append("Fijo");
			fw.append(',');
			fw.append("cOficio");
			fw.append(',');
			fw.append("cIdTipoProcedimiento");
			fw.append(',');
			fw.append("cIdProcedimiento");	
			fw.append(',');
			fw.append("cIdRFC");
			fw.append(',');
			fw.append("cCategoria");
			fw.append(',');
			fw.append("nIdPartida");
			fw.append(',');
			fw.append("nIdLineaConsolidado");
			fw.append(',');
			fw.append("Descripcion");
			fw.append(',');
			fw.append("Cantidad");
			fw.append(',');
			fw.append("Unidad");
			fw.append(',');
			fw.append("PrecioUnitario");
			fw.append(',');
			fw.append("PrecioNeto");
			fw.append(',');
			fw.append("GranTotalBruto");
			fw.append(',');
			fw.append("IVA");
			fw.append(',');
			fw.append("GranTotalNeto");
			fw.append(',');
			fw.append("CantidadLetra");
			
			fw.append('\n');
			
			
			conn = DataSourceManager.getConnection(jndiName);
			cmst = conn.prepareCall("{call rpt_mPedido(?,?,?,?)}");
	  		cmst.setString(1,cEjercicio );
	  		cmst.setString(2, cIdTipoPedido);
	  		cmst.setString(3, cIdUnidadEjecutora);
	  		cmst.setString(4, nIdConsecutivo);
	  		rs=cmst.executeQuery();
	  		
			//String query = "EXEC rpt_mPedido"+cEjercicio+","+cIdTipoPedido+","+cIdUnidadEjecutora+","+nIdConsecutivo;
			
			//stmt = conn.createStatement();
			//rs = stmt.executeQuery(query);
			while(rs.next())
			{
				
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				fw.append(',');
				fw.append(rs.getString(17));
				fw.append(',');
				fw.append(rs.getString(18));
				fw.append(',');
				fw.append(rs.getString(19));
				fw.append(',');
				fw.append(rs.getString(20));
				fw.append(',');
				fw.append(rs.getString(21));
				fw.append(',');
				fw.append(rs.getString(22));
				fw.append(',');
				fw.append(rs.getString(23));
				fw.append('\n');
	
			
			}
			if(cmst!=null)
				cmst.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
			
	}
	private void PedidoAnexo1(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "PedidoAnexo1.csv";
		String cIdUnidadEjecutora=request.getParameter("cIdUnidadEjecutora");
		String cEjercicio=request.getParameter("cEjercicio");
		String cIdTipoPedido=request.getParameter("cIdTipoPedido");
		String nIdConsecutivo=request.getParameter("nIdConsecutivo");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("cEjercicio");
			fw.append(',');
			fw.append("cConceptoPedido");
			fw.append(',');
			fw.append("cIdUnidadEjecutora");
			fw.append(',');
			fw.append("cUnidadEjecutora");
			fw.append(',');
			fw.append("cIdPedido");
			fw.append(',');
			fw.append("cIdContratoPedido");
			fw.append(',');
			fw.append("cEstado");
			fw.append(',');
			fw.append("cPuesto1");
			fw.append(',');
			fw.append("cNombre1");
			fw.append(',');
			fw.append("cPuesto2");
			fw.append(',');
			fw.append("cNombre2");
			fw.append(',');
			fw.append("cPuesto3");
			fw.append(',');
			fw.append("cNombre3");
			fw.append(',');
			fw.append("cIdAlmacenEntrega");
			fw.append(',');
			fw.append("cAlmacen");
			fw.append(',');
			fw.append("mSubtotal");
			fw.append(',');
			fw.append("mIVA");
			fw.append(',');
			fw.append("mTotal");
			fw.append(',');
			fw.append("mTotalPartida");
			fw.append(',');
			fw.append("montoLetra");
			fw.append('\n');
			
			
			conn = DataSourceManager.getConnection(jndiName);
			cmst = conn.prepareCall("{call rpt_mPedidoAnexo1(?,?,?,?)}");
	  		cmst.setString(1,cEjercicio );
	  		cmst.setString(2, cIdTipoPedido);
	  		cmst.setString(3, cIdUnidadEjecutora);
	  		cmst.setString(4, nIdConsecutivo);
	  		rs=cmst.executeQuery();
	  		
			//String query = "EXEC rpt_mPedido"+cEjercicio+","+cIdTipoPedido+","+cIdUnidadEjecutora+","+nIdConsecutivo;
			
			//stmt = conn.createStatement();
			//rs = stmt.executeQuery(query);
			while(rs.next())
			{
				
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				fw.append(',');
				fw.append(rs.getString(17));
				fw.append(',');
				fw.append(rs.getString(18));
				fw.append(',');
				fw.append(rs.getString(19));
				fw.append(',');
				fw.append(rs.getString(20));
				
				fw.append('\n');
	
			
			}
			if(cmst!=null)
				cmst.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
			
	}
	private void PedidoAnexo2(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "PedidoAnexo2.csv";
		String cIdUnidadEjecutora=request.getParameter("cIdUnidadEjecutora");
		String cEjercicio=request.getParameter("cEjercicio");
		String cIdTipoPedido=request.getParameter("cIdTipoPedido");
		String nIdConsecutivo=request.getParameter("nIdConsecutivo");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("cEjercicio");
			fw.append(',');
			fw.append("cConceptoPedido");
			fw.append(',');
			fw.append("cIdUnidadEjecutora");
			fw.append(',');
			fw.append("cIdPedido");
			fw.append(',');
			fw.append("cEstado");
			fw.append(',');
			fw.append("cIdEjercicio");
			fw.append(',');
			fw.append("cIdPedidoContrato");
			fw.append(',');
			fw.append("cPuesto1");
			fw.append(',');
			fw.append("cNombre1");
			fw.append(',');
			fw.append("cPuesto2");
			fw.append(',');
			fw.append("cNombre2");
			fw.append(',');
			fw.append("cPuesto3");
			fw.append(',');
			fw.append("cNombre3");
			fw.append(',');
			fw.append("cUnidadEjecutoraCons");
			fw.append(',');
			fw.append("cIdUnidadEjecutora");
			fw.append(',');
			fw.append("mSubtotal");
			fw.append(',');
			fw.append("mIVA");
			fw.append(',');
			fw.append("mTotal");
			fw.append(',');
			fw.append("mTotalPartida");
			fw.append(',');
			fw.append("montoLetra");
			fw.append(',');
			fw.append("cIdUnidadEjecutoraSolicitud");
			
			fw.append('\n');
			
			
			conn = DataSourceManager.getConnection(jndiName);
			cmst = conn.prepareCall("{call rpt_mPedidoAnexo2(?,?,?,?)}");
	  		cmst.setString(1,cEjercicio );
	  		cmst.setString(2, cIdTipoPedido);
	  		cmst.setString(3, cIdUnidadEjecutora);
	  		cmst.setString(4, nIdConsecutivo);
	  		rs=cmst.executeQuery();
	  		
			//String query = "EXEC rpt_mPedido"+cEjercicio+","+cIdTipoPedido+","+cIdUnidadEjecutora+","+nIdConsecutivo;
			
			//stmt = conn.createStatement();
			//rs = stmt.executeQuery(query);
			while(rs.next())
			{
				
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				fw.append(',');
				fw.append(rs.getString(17));
				fw.append(',');
				fw.append(rs.getString(18));
				fw.append(',');
				fw.append(rs.getString(19));
				fw.append(',');
				fw.append(rs.getString(20));
				fw.append(',');
				fw.append(rs.getString(21));
				fw.append('\n');
	
			
			}
			if(cmst!=null)
				cmst.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
			
	}

	private void mProgramaAnualOrdenadoPorPartida(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "ProgramaAnualOrdenadoPorPartida.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("_Ejercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_Unidad");
			fw.append(',');
			fw.append("_Entidad_Contable");
			fw.append(',');
			fw.append("_CABMS");
			fw.append(',');
			fw.append("_Descripcion_CABMS");
			fw.append(',');
			fw.append("_Parida");
			fw.append(',');
			fw.append("_Descripcion_Partida");
			fw.append(',');
			fw.append("_Unidad_Medida");
			fw.append(',');
			fw.append("_Cantidad_ITrimestre");
			fw.append(',');
			fw.append("_Cantidad_IITrimestre");
			fw.append(',');
			fw.append("_Cantidad_IIITrimestre");
			fw.append(',');
			fw.append("_Cantidad_IVTrimestre");
			fw.append(',');
			fw.append("_Cantidad_Total");
			fw.append(',');
			fw.append("_Precio_Unitario");
			fw.append(',');
			fw.append("_Monto_Bruto");
			fw.append(',');
			fw.append("_IVA");
			fw.append(',');
			fw.append("_Monto_Neto");
			fw.append(',');
			fw.append("_Monto_Bruto_Partida");
			fw.append(',');
			fw.append("_Monto_Neto_Partida");
			fw.append(',');
			fw.append("_mMontoNetoC2");
			fw.append(',');
			fw.append("_mMontoNetoC3");
			fw.append(',');
			fw.append("_mMontoNetoC5");
			
			fw.append('\n');
			
			//Class.forName(driver).newInstance();
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * FROM rpt_mProgramaAnualOrdenadoPorPartida() where 1=1 "+unidadE;
			//System.out.println("query= "+query+"unidadE= "+unidadE);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				fw.append(',');
				fw.append(rs.getString(17));
				fw.append(',');
				fw.append(rs.getString(18));
				fw.append(',');
				fw.append(rs.getString(19));
				fw.append(',');
				fw.append(rs.getString(20));
				fw.append(',');
				fw.append(rs.getString(21));
				fw.append(',');
				fw.append(rs.getString(22));
				fw.append(',');
				fw.append(rs.getString(23));
				fw.append('\n');
	
			
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
			
	}
	private void mProgramaAnualOrdenadoPorPartidaAcumulado(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "ProgramaAnualOrdenadoPorPartidaAcumulado.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
        try {
        	fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
        	fw.append("_Ejercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_Unidad");
			fw.append(',');
			fw.append("_Partida");
			fw.append(',');
			fw.append("_Descripcion_Partida");
			fw.append(',');
			fw.append("_Monto_Bruto_P");
			fw.append(',');
			fw.append("_Monto_Neto_P");
			fw.append(',');
			fw.append("_mMontoNetoC2");
			fw.append(',');
			fw.append("_mMontoNetoC3");
			fw.append(',');
			fw.append("_mMontoNetoC5");
			fw.append(',');
			fw.append("_mMontoTotalNetoC2");
			fw.append(',');
			fw.append("_mMontoTotalNetoC3");
			fw.append(',');
			fw.append("_mMontoTotalNetoC5");
			fw.append(',');
			fw.append("_mMontoTotalBruto_UE");
			fw.append(',');
			fw.append("_mMontoTotalNeto_UE");
			fw.append(',');
			fw.append("_mMontoTotalBruto_Ej");
			
			fw.append(',');
			fw.append("_mMontoTotalNeto_Ej");
			fw.append('\n');
			
			//Class.forName(driver).newInstance();
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * FROM rpt_mProgramaAnualOrdenadoPorPartidaAcumulado () where 1=1 "+unidadE;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			String var = "";
			while(rs.next())
			{
				if (!var.equals(rs.getString(4))) {
					var = rs.getString(4);
				
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				fw.append(',');
				fw.append(rs.getString(17));
				
				fw.append('\n');
				}
				
			}
        } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
	
	}
	
	private void mProgramaAnualPorPartidayTrimestre(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "ProgramaAnualPorPartidayTrimestre.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("_Ejercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_Unidad");
			fw.append(',');
			fw.append("_Entidad_Contable");
			fw.append(',');
			fw.append("_Partida");
			fw.append(',');
			fw.append("_Descripcion_Partida");
			fw.append(',');
			fw.append("_Monto_Bruto_IT");
			fw.append(',');
			fw.append("_Monto_Neto_IT");
			fw.append(',');
			fw.append("_Monto_Bruto_IIT");
			fw.append(',');
			fw.append("_Monto_Neto_IIT");
			fw.append(',');
			fw.append("_Monto_Bruto_IIIT");
			fw.append(',');
			fw.append("_Monto_Neto_IIIT");
			fw.append(',');
			fw.append("_Monto_Bruto_IVT");
			fw.append(',');
			fw.append("_Monto_Neto_IVT");
			fw.append(',');
			fw.append("_Monto_Bruto");
			fw.append(',');
			fw.append("_Monto_Neto");
			fw.append('\n');
			
			//Class.forName(driver).newInstance();
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * FROM v_mProgramaAnualPorPartidayTrimestre where 1=1 "+unidadE;
			System.out.println("query= "+query+"unidadE= "+unidadE);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				
				fw.append('\n');
	
			
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
		
	}
	private void mProgramaAnualMontosPorCapituloReporte(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		String filename = "MontosPorCapituloReporte.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("_cEjercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_Descripcion");
			fw.append(',');
			fw.append("mMontoC2");
			fw.append(',');
			fw.append("mMontoC3");
			fw.append(',');
			fw.append("mMontoC5");
			fw.append(',');
			fw.append("mMontoTotal");
			fw.append('\n');
			
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * FROM fn_mProgramaAnualMontosPorCapituloReporte() where 1=1 "+unidadE;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append('\n');
	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
		
	}
	private void mProgramaAnualOrdenadoPorPartida_COMPRANET(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String filename = "rpt_CompraNET.csv";
		//String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		String where ="where 1=1 ";
		where+=request.getParameter("cIdUnidadEjecutora");
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("UR");
			fw.append(',');
			fw.append("GRUPO");
			fw.append(',');
			fw.append("CLAVE_CUCOP+");
			fw.append(',');
			fw.append("DESCRIPCIÓN");
			fw.append(',');
			fw.append("PARTIDA_ESPECIFICA");
			fw.append(',');
			fw.append("VALOR_ESTIMADO");
			fw.append(',');
			fw.append("VALOR_MIPyMES");
			fw.append(',');
			fw.append("VALOR_NCTLC");
			fw.append(',');
			fw.append("CANTIDAD");
			fw.append(',');
			fw.append("UNIDAD_MEDIDA");
			fw.append(',');
			fw.append("CARACTER_PROCEDIMIENTO");
			fw.append(',');
			fw.append("ENTIDAD_FEDERATIVA");
			fw.append(',');
			fw.append("PORCENTAJE_TRIMESTRE1");
			fw.append(',');
			fw.append("PORCENTAJE_TRIMESTRE2");
			fw.append(',');
			fw.append("PORCENTAJE_TRIMESTRE3");
			fw.append(',');
			fw.append("PORCENTAJE_TRIMESTRE4");
			fw.append(',');
			fw.append("PLURIANUAL");
			fw.append(',');
			fw.append("AÑOS_PLURIANUALES");
			fw.append(',');
			fw.append("VALOR_TOTAL_PLURIANUAL");
			fw.append(',');
			fw.append("CLAVE_PROGRAMA_FEDERAL");
			fw.append(',');
			fw.append("FECHA_INICIO_OBRA");
			fw.append(',');
			fw.append("FECHA_FIN_OBRA");
			fw.append(',');
			fw.append("TIPO_PROCEDIMIENTO");
			fw.append(',');
			fw.append("JUSTIFICACIÓN_TIPO_PROCEDIMIENTO");
			fw.append(',');
			fw.append("CAPTURISTA");
			fw.append(',');
			fw.append("ERROR_UR_GRUPO");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append('\n');
			
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT *FROM [rpt_CompraNETRedondeado_GERARDO]() "+where;
			stmt = conn.createStatement();
			log.info( query );
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append(',');
				fw.append(rs.getString(14));
				fw.append(',');
				fw.append(rs.getString(15));
				fw.append(',');
				fw.append(rs.getString(16));
				fw.append(',');
				fw.append(rs.getString(17));
				fw.append(',');
				fw.append(rs.getString(18));
				fw.append(',');
				fw.append(rs.getString(19));
				fw.append(',');
				fw.append(rs.getString(20));
				fw.append(',');
				fw.append(rs.getString(21));
				fw.append(',');
				fw.append(rs.getString(22));
				fw.append(',');
				fw.append(rs.getString(23));
				fw.append(',');
				fw.append(rs.getString(24));
				fw.append(',');
				fw.append(rs.getString(25));
				fw.append(',');
				fw.append(rs.getString(26));
				fw.append('\n');
	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	}
	private void rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String filename = "CucopsSinDA.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("cEjercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_DescripcionUE");
			fw.append(',');
			fw.append("cIdSubPartida");
			fw.append(',');
			fw.append("_DescrpcionSubpartida");
			fw.append(',');
			fw.append("cIdCABM");
			fw.append(',');
			fw.append("cCABM");
			fw.append(',');
			fw.append("cPeriodo");
			fw.append(',');
			fw.append("nCantidadEnSolicitudes");
			fw.append(',');
			fw.append("mPrecioUnitario");
			fw.append(',');
			fw.append("mImporteBruto");
			fw.append(',');
			fw.append("mImporteNeto");
			fw.append(',');
			fw.append("mMontoDisponibilidad");
			fw.append('\n');
			
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * from fn_mProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados() where 1=1 "+unidadE;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
				fw.append(',');
				fw.append(rs.getString(11));
				fw.append(',');
				fw.append(rs.getString(12));
				fw.append(',');
				fw.append(rs.getString(13));
				fw.append('\n');
	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	}
	private void fn_mProgramaAnualConciliacionPresupuestal(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String filename = "rpt_ConciliacionPreupuestal.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("cEjercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_Unidad");
			fw.append(',');
			fw.append("cIdSubPartida");
			fw.append(',');
			fw.append("cSubPartida");
			fw.append(',');
			fw.append("_Programado");
			fw.append(',');
			fw.append("_Original");
			fw.append(',');
			fw.append("_Modificado");
			fw.append(',');
			fw.append("_M_P");
			fw.append(',');
			fw.append("A_P");	
			fw.append('\n');
			
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * from fn_mProgramaAnualConciliacionPresupuestal() where 1=1 "+unidadE;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));
			
				fw.append('\n');
	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	}
	
	
	
	private void mReporteEjercidoMat(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// GENERA REPORTE DE LO EJERCIDO CON LOS FILTROS PROPORCIONADOS
		String filename = "mReporteEjercidoMateriales.csv";
		String vFilters = request.getParameter("filter"); // FILTROS DEL WHERE
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		FileWriter fw = null;
		//int renglon = 0;
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			conn = DataSourceManager.getConnection(jndiName);
			//vFilters = vFilters.replace("~", " = ");
			String query = "SELECT * FROM mDataStorePagos " + vFilters;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			int i = 0;
			int cuantasColumnas = 0;
			
			cuantasColumnas = rs.getMetaData().getColumnCount();
			// Nombre de columnas
			for (i = 1; i <= cuantasColumnas; i++) {
				fw.append("" + rs.getMetaData().getColumnName(i));
				if (i != cuantasColumnas)
					fw.append(',');
				else
					fw.append('\n');
			}
			while (rs.next()) {
				//renglon+=1;
				for (i = 1; i <= cuantasColumnas; i++) {
					
					String dummyStr = rs.getString(i);
				if (dummyStr != null )
					// dummyStr = dummyStr.replaceAll(",", " ");
				 	dummyStr = dummyStr.replaceAll("[\\,\\s]", " ");
					else
					 dummyStr = " ";
					fw.append(dummyStr);
					if (i != cuantasColumnas)
						fw.append(',');
					else
						fw.append('\n');
				}
			}
		} catch (Exception e) {
			//System.out.println(renglon);
			e.printStackTrace();
		}
		mostrarArchivo(filename, conn, stmt, rs, fw, request, response);

	}
	
	
	private void mReporteEjercido(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// GENERA REPORTE DE LO EJERCIDO CON LOS FILTROS PROPORCIONADOS
		String filename = "mReporteEjercido.csv";
		String vFilters = request.getParameter("vFilters"); // FILTROS DEL WHERE
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		FileWriter fw = null;
		//int renglon = 0;
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			conn = DataSourceManager.getConnection(jndiName);
			vFilters = vFilters.replace("~", " = ");
			String query = "SELECT * FROM v_mDataStoreEjercido WHERE  1 = 1 " 
					+ vFilters;
//			System.out.println("Consulta mReporteEjercido " + query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			int i = 0;
			int cuantasColumnas = 0;
			
			cuantasColumnas = rs.getMetaData().getColumnCount();
			// Nombre de columnas
			for (i = 1; i <= cuantasColumnas; i++) {
				fw.append("" + rs.getMetaData().getColumnName(i));
				if (i != cuantasColumnas)
					fw.append(',');
				else
					fw.append('\n');
			}
			while (rs.next()) {
				//renglon+=1;
				for (i = 1; i <= cuantasColumnas; i++) {
					
					String dummyStr = rs.getString(i);
					if (dummyStr != null )
					 dummyStr = dummyStr.replaceAll(",", " ");
					else
					 dummyStr = " ";
					fw.append(dummyStr);
					if (i != cuantasColumnas)
						fw.append(',');
					else
						fw.append('\n');
				}
			}
		} catch (Exception e) {
			//System.out.println(renglon);
			e.printStackTrace();
		}
		mostrarArchivo(filename, conn, stmt, rs, fw, request, response);

	}

	private void rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "ProgramaAnualOrdenadoPorPartidaAcumulado.csv";

		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;


		FileWriter fw=null;
		
        try {
        	fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
        	fw.append("cEjercicio");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("d_descripcion");
			fw.append(',');
			fw.append("cIdSubPartida");
			fw.append(',');
			fw.append("DescSubPartida");
			fw.append(',');
			fw.append("MontoPC2");
			fw.append(',');
			fw.append("MontoPC3");
			fw.append(',');
			fw.append("MontoPC5");
			fw.append(',');
			fw.append("Total");
			fw.append('\n');
			//Class.forName(driver).newInstance();
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * FROM fn_mProgramaAnualMontosPorCapituloRestandoPartidas () where 1=1 "+unidadE;
			stmt = conn.createStatement();

			rs = stmt.executeQuery(query);
			while(rs.next())
			{

				fw.append(rs.getString(1));

				fw.append(',');
				fw.append(rs.getString(2));

				fw.append(',');
				fw.append(rs.getString(3));

				fw.append(',');
				fw.append(rs.getString(4));

				fw.append(',');
				fw.append(rs.getString(5));

				fw.append(',');
				fw.append(rs.getString(6));

				fw.append(',');
				fw.append(rs.getString(7));

				fw.append(',');
				fw.append(rs.getString(8));
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append('\n');
				
			}
        } catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);
	
	}
	
	
	private void mProgramaAnualResumidoPorPartidaCapitulo(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String filename = "ProgramaAnualResumidoPorPartidaCapitulo.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
			/*if (request.getParameter("cIdUnidadEjecutora").length()>5)
			{
				unidadE=  request.getParameter("cIdUnidadEjecutora").substring(17, 20);	
			}
			else
			{	
			unidadE="cero";	
			}	*/	
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
	    try {
	    	fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("ejercicio");
			fw.append(',');
			fw.append("unidad");
			fw.append(',');
			fw.append("descArea");
			fw.append(',');
			fw.append("subPartida");
			fw.append(',');
			fw.append("DescbPartida");
			fw.append(',');
			fw.append("nMontoC2");
			fw.append(',');
			fw.append("nMontoC3");
			fw.append(',');
			fw.append("nMontoC5");
			fw.append(',');
			fw.append("monto");	
			fw.append(',');
			fw.append("mMontoTotal");	
			fw.append('\n');
			
			//Class.forName(driver).newInstance();
			conn = DataSourceManager.getConnection(jndiName);
			String query = "select *from [fn_mProgramaAnualMontosPorCapituloPartidas]() where 1=1 "+unidadE;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append(',');
				fw.append(rs.getString(7));
				fw.append(',');
				fw.append(rs.getString(8));	
				fw.append(',');
				fw.append(rs.getString(9));
				fw.append(',');
				fw.append(rs.getString(10));	
				fw.append('\n');
				
			}
	    } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);

	}
	
	private void reporteConsolidadoCompranet(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String filename = "REQUERIMIENTOS ECÓNOMICOS.csv";
		
		String cIdConsolidado = request.getParameter("cIdConsolidado");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		ResultSet rsUM=null;
		ResultSet rsCons=null;
		FileWriter fw=null;
		
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar + filename);
			fw.append("Version");
			fw.append(',');
			fw.append("1.3.0");
			fw.append("\n");
			
			fw.append("Type");
			fw.append(',');
			fw.append("Reference");
			fw.append(',');
			fw.append("Description");
			fw.append(',');
			fw.append("Mandatory");
			fw.append(',');
			fw.append("Notes");
			fw.append(',');
			fw.append("Notes Visible");
			fw.append(',');
			fw.append("Unit of measure");
			fw.append(',');
			fw.append("Quantity");
			fw.append(',');
			fw.append("Show quantity (Y/N)");
			fw.append(',');
			fw.append("Inserted by (B/N)");
			fw.append(',');
			fw.append("QtyDefaultQuantity");
			fw.append(',');
			fw.append("Range1");
			fw.append(',');
			fw.append("Range2");
			fw.append(',');
			fw.append("...");
			fw.append(',');
			fw.append("...");
			fw.append(',');
			fw.append("...");
			fw.append("\n");
			
			fw.append("MCType");
			fw.append(',');
			fw.append("Title");
			fw.append(',');
			fw.append("Default Value");
			fw.append(',');
			fw.append("Mandatory");
			fw.append(',');
			fw.append("Seller Visibility");
			fw.append(',');
			fw.append("Value1");
			fw.append(',');
			fw.append("Value2");
			fw.append(',');
			fw.append(" ");
			fw.append(',');
			fw.append("...");
			fw.append(',');
			fw.append("...");
			fw.append(',');
			fw.append('\n');
			
			fw.append("Group");
			fw.append(',');
			fw.append(" ");
			fw.append(',');
			fw.append("Requerimientos Económicos");
			fw.append('\n');
			
			conn = DataSourceManager.getConnection(jndiName);
			stmt = conn.createStatement();
			
			String queryCons = "SELECT cNotas from mConsolidado where cIdConsolidado = '" + cIdConsolidado + "';";
			rsCons = stmt.executeQuery(queryCons);
			String notasCons = "";
			String notasBand = "N";
			if (rsCons.next()){
				notasCons = rsCons.getString(1);
				if (notasCons != null && notasCons.length() > 0){
					notasCons = notasCons.replaceAll(",", "");
					notasBand = "Y";
				}
				else {
					notasCons = "";
				}
			}
			
			String query = "SELECT * FROM fn_mConsolidadoConsultaLineasConsolidado('" + cIdConsolidado + "')";
			rs = stmt.executeQuery(query);
			
			//Array para almacenar la información del resulset principal de lineas del consolidado
			ArrayList<String[]> result = new ArrayList<String[]>(); 
			
			//Traer todas los CAMBS y sus Claves de Unidad de Medida que estén en el resulset de lineas de consolidado
			String queryUM = "select cc.cIdCABM , cc.cIdUnidadMedida , cu.cIdUnidadMedida , cu.cIdClave" 
							+ " from mCatalogoCABM cc with (NOLOCK) , mCatalogoUnidadMedida cu with (NOLOCK)" 
							+ " where cc.cIdUnidadMedida = cu.cIdUnidadMedida and   cc.cIdCABM in ( ";
			while(rs.next()) {
				queryUM += "'" + rs.getString(3) + "', ";
				String[] line = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(5)};
				result.add(line);
			}
			queryUM = queryUM.substring(0, queryUM.length() - 2) + ")";
			rsUM = stmt.executeQuery(queryUM);
			
			//Array para almacenar la información del resulset de Unidades de Medida
			ArrayList<String[]> resultUM = new ArrayList<String[]>();
			while(rsUM.next()) {
				String[] lineUM = {rsUM.getString(1), rsUM.getString(4)};
				resultUM.add(lineUM);
			}
			
			for (String[] line : result) {
				String description = line[1];
				if (description != null) description = description.replaceAll(",", "");
				String measureUnit = "";
				for (String[] lineUM : resultUM) {
					if (line[2].equals(lineUM[0])){
						measureUnit = lineUM[1];
						break;
					}
				}
				
				fw.append("Price");
				fw.append(',');
				fw.append(line[0]);
				fw.append(',');
				fw.append(description);
				fw.append(',');
				fw.append("N");
				fw.append(',');
				fw.append(notasCons);
				fw.append(',');
				fw.append(notasBand);
				fw.append(',');
				fw.append(measureUnit);
				fw.append(',');
				fw.append(line[3]);
				fw.append(',');
				fw.append("Y");
				fw.append(',');
				fw.append("B");
				fw.append('\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mostrarArchivo(filename, conn, stmt, rs, fw, request, response);
	}
private void mConsiliacion_RC_Apartado_Precompromiso(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		String filename = "ConsiliacionRC_Apartado_Precompromiso.csv";
		String unidadE=request.getParameter("cIdUnidadEjecutora");
		Connection conn = null;
	    Statement stmt=null;
		ResultSet rs=null;
		FileWriter fw=null;
		
		
		try {
			fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			fw.append("_cEjercicio");
			fw.append(',');
			fw.append("_cIdPedidoDefinitivo");
			fw.append(',');
			fw.append("_Unidad_RM");
			fw.append(',');
			fw.append("_cIdSubpartida");
			fw.append(',');
			fw.append("_TotalApartado");
			fw.append(',');
			fw.append("_TotalRC");
			fw.append(',');
			fw.append("_TotalPrecompromiso");
			
			fw.append('\n');
			
			conn = DataSourceManager.getConnection(jndiName);
			String query = "SELECT * FROM v_mConsiliacionSolicitudPrecompromiso where 1=1 "+unidadE;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				fw.append(rs.getString(1));
				fw.append(',');
				fw.append(rs.getString(2));
				fw.append(',');
				fw.append(rs.getString(3));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(4));
				fw.append(',');
				fw.append(rs.getString(5));
				fw.append(',');
				fw.append(rs.getString(6));
				fw.append('\n');
	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
		
	}
/////////////
private void mConsiliacion_AP_PRE_Comp_Eje(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "Consiliacion_AP_PRE_Comp_Eje.csv";
	String unidadE=request.getParameter("cIdUnidadEjecutora");
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_cEjercicio");
		fw.append(',');
		fw.append("_cIdPedidoDefinitivo");
		fw.append(',');
		fw.append("_Unidad_RM");
		fw.append(',');
		fw.append("_cIdSubPartida");
		fw.append(',');
		fw.append("_Total_RC");
		fw.append(',');
		fw.append("_Apartado");
		fw.append(',');
		fw.append("_ImportePrecompromiso");
		fw.append(',');
		fw.append("_EP");
		fw.append(',');
		fw.append("_TotalComprometido");
		fw.append(',');
		fw.append("_TotalPagado");
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "SELECT * FROM v_mSolicitudPrecompromisoComprometidoEjercido where 1=1 "+unidadE;
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append(',');
			fw.append(rs.getString(10));
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}
private void mCatalogoInventarioArticulosdeAlmacen(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "Inventario_De_Almacen.csv";
	String unidadE=request.getParameter("cIdUnidadEjecutora");
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_CVE_CABMS");
		fw.append(',');
		fw.append("_descripcion");
		fw.append(',');
		fw.append("_cve_almacen");
		fw.append(',');
		fw.append("_Unidad_De_Medida");
		fw.append(',');
		fw.append("_cantidad");
		fw.append(',');
		fw.append("_cve_uniadmin");
		fw.append(',');
		fw.append("_Unidad_RM");
		fw.append(',');
		fw.append("_AREAS");
		fw.append(',');
		fw.append("_capitulo");
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "SELECT * FROM [v_mCatalogoInventarioArticulos] where 1=1 "+unidadE;
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}

private void ReporteConsolidado(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "ReporteConsolidado.csv";
	String cIdConsolidado=request.getParameter("cIdCons");
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_Solicitud");
		fw.append(',');
		fw.append("_LineaSolicitud");
		fw.append(',');
		fw.append("_LineaConsolidado");
		fw.append(',');
		fw.append("_DescripcionSolicitud");
		fw.append(',');
		fw.append("_DescripcionConsolidado");
		fw.append(',');
		fw.append("_CUCOP");
		fw.append(',');
		fw.append("_SubPartida");
		fw.append(',');
		fw.append("_CantidadLineasConsolidado");
		fw.append(',');
		fw.append("_CantidadSolicitudes");
		fw.append(',');
		fw.append("_PrecioUnitario");
		fw.append(',');
		fw.append("_IVA");
		fw.append(',');
		fw.append("_SubTotalNetoSolicitudes");
		fw.append(',');
		fw.append("_MontoBrutoLineaConsolidado");
		fw.append(',');
		fw.append("_MontoNetoLineaConsolidado");
		fw.append(',');
		fw.append("_DescricionUE");
		fw.append(',');
		fw.append("_Unidad_RM");
		fw.append(',');
		fw.append("_Descripcion");
		fw.append(',');
		fw.append("_UnidadMedida");
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "SELECT * FROM [fn_mReporteConsolidado]('"+cIdConsolidado+"')";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append(',');
			fw.append(rs.getString(10));
			fw.append(',');
			fw.append(rs.getString(11));
			fw.append(',');
			fw.append(rs.getString(12));
			fw.append(',');
			fw.append(rs.getString(13));
			fw.append(',');
			fw.append(rs.getString(14));
			fw.append(',');
			fw.append(rs.getString(15));
			fw.append(',');
			fw.append(rs.getString(16));
			fw.append(',');
			fw.append(rs.getString(17));
			fw.append(',');
			fw.append(rs.getString(18));
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}

private void ReporteContratoAnexo1(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "ContratoAnexo1.csv";
	String cIdUnidadEjecutora=request.getParameter("cIdUnidadEjecutora");
	String cEjercicio=request.getParameter("cEjercicio");
	String cIdTipoContrato=request.getParameter("cIdTipoContrato");
	String nIdConsecutivo=request.getParameter("nIdConsecutivo");
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_cEjercicio");
		fw.append(',');
		fw.append("_cConceptoContrato");
		fw.append(',');
		fw.append("_UnidadEjecutora");
		fw.append(',');
		fw.append("_cIdContrato");
		fw.append(',');
		fw.append("_cEstado");
		fw.append(',');
		fw.append("_cEjercicio");
		fw.append(',');
		fw.append("_cIdContratoPedido");
		fw.append(',');
		fw.append("_fFallo");
		fw.append(',');
		fw.append("_fFormalización");
		fw.append(',');
		fw.append("_fInicio");
		fw.append(',');
		fw.append("_fFin");
		fw.append(',');
		fw.append("_c_RazonSocial");
		fw.append(',');
		fw.append("_CantidadPartida");
		fw.append(',');
		fw.append("_UnidadMedida");
		fw.append(',');
		fw.append("_PrecioUnitario");
		fw.append(',');
		fw.append("_MontoNeto");
				
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		cmst = conn.prepareCall("{call sp_rptContratoAnexo1(?,?,?,?)}");
  		cmst.setString(1,cEjercicio );
  		cmst.setString(2, cIdTipoContrato);
  		cmst.setString(3, cIdUnidadEjecutora);
  		cmst.setString(4, nIdConsecutivo);
  		rs=cmst.executeQuery();
  		
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append(',');
			fw.append(rs.getString(10));
			fw.append(',');
			fw.append(rs.getString(11));
			fw.append(',');
			fw.append(rs.getString(12));
			fw.append(',');
			fw.append(rs.getString(13));
			fw.append(',');
			fw.append(rs.getString(14));
			fw.append(',');
			fw.append(rs.getString(15));
			fw.append(',');
			fw.append(rs.getString(16));
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}
private void ContratoClavesComplementarias(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "ContratoClavesComplementarias.csv";
	String cidContrato=request.getParameter("cIdTipoContrato")+"-"+request.getParameter("cIdUnidadEjecutora")+"-"+request.getParameter("nIdConsecutivo");
	String cEjercicio=request.getParameter("cEjercicio");
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_EP");
		fw.append(',');
		fw.append("_Periodo");
		fw.append(',');
		fw.append("_Importe");
		
		
		
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "SELECT * FROM [fn_mContratoClavesComplementarias]('"+cidContrato+"','"+cEjercicio+"')";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}

private void ReporteRequisiciones(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "Requisicion.csv";
	
	String cEjercicio=request.getParameter("cEjercicio");
	String cIdUnidadEjecutora=request.getParameter("cIdUnidadEjecutora");
	String nIdConsecutivo=request.getParameter("nIdConsecutivo");
	String cIdTipoSolicitud=request.getParameter("cIdTipoSolicitud");
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_cEjercicio");
		fw.append(',');
		fw.append("_cIdUnidadEjecutora");
		fw.append(',');
		fw.append("_cIdSolicitud");
		fw.append(',');
		fw.append("_cIdSubPartida");
		fw.append(',');
		fw.append("_cIdCABM");
		fw.append(',');
		fw.append("_nIdLineaSolicitud");
		fw.append(',');
		fw.append("_cDescripcionLinea");
		fw.append(',');
		fw.append("_nCantidad");
		fw.append(',');
		fw.append("_cUnidadMedida");
		fw.append(',');
		fw.append("_mPrecioUnitario");
		fw.append(',');
		fw.append("_montoBruto");
		fw.append(',');
		fw.append("_montoIVA");
		fw.append(',');
		fw.append("_montoNeto");
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "SELECT * FROM [fn_mRequisicion]('"+cEjercicio+"','"+cIdUnidadEjecutora+"','"+nIdConsecutivo+"','"+cIdTipoSolicitud+"')";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append(',');
			fw.append(rs.getString(10));
			fw.append(',');
			fw.append(rs.getString(11));
			fw.append(',');
			fw.append(rs.getString(12));
			fw.append(',');
			fw.append(rs.getString(13));
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}

private void ReportePedidoClaves(HttpServletRequest request, HttpServletResponse response) throws IOException{
	
	String filename = "ClavesCompPedido.csv";
	
	String cEjercicio=request.getParameter("cEjercicio");
	String cIdUnidadEjecutora=request.getParameter("cIdUnidadEjecutora");
	String nIdConsecutivo=request.getParameter("nIdConsecutivo");
	String cIdTipoPedido=request.getParameter("cIdTipoPedido");
	String cIdPedido=cIdTipoPedido+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo;
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	
	
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_Pedido");
		fw.append(',');
		fw.append("_Unidad_RM");
		fw.append(',');
		fw.append("_EP");
		fw.append(',');
		fw.append("_Periodo");
		fw.append(',');
		fw.append("_ImporteEP");
		fw.append(',');
		fw.append("_Total");
	
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "SELECT * FROM [fn_mPedicoClavescomplementarias]('"+cIdPedido+"','"+cEjercicio+"')";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}
private void ReporteConsultaReq(HttpServletRequest request, HttpServletResponse response) throws IOException{
	String filename = "ReporteRequisiciones.csv";
	String where_=request.getParameter("where_");
	
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_Requisición");
		fw.append(',');
		fw.append("_Unidad_RM");
		fw.append(',');
		fw.append("_Tipo_Req");
		fw.append(',');
		fw.append("_Alcance");
		fw.append(',');
		fw.append("_Partida");
		fw.append(',');
		fw.append("_Descripción");
		fw.append(',');
		fw.append("_Estado_Req");
		fw.append(',');
		fw.append("_Periodo");
		fw.append(',');
		fw.append("_Monto_Bruto");
		fw.append(',');
		fw.append("_Monto_Neto");
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "select cIdSolicitud,cIdUnidadEjecutora,cIdTipoSolicitud"+
						",cAlcance,cIdSubPartida,cDescripcion,cEstado"+
						",cPeriodo,mBruto,mNeto from v_mSolicitud where "+where_+" order by cIdSolicitud";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9)!=null? rs.getString(9).replaceAll(",", "") : "$0.00");
			fw.append(',');
			fw.append(rs.getString(10)!=null? rs.getString(10).replaceAll(",", "") : "$0.00");
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}
private void ReporteConsultaConsolidados(HttpServletRequest request, HttpServletResponse response) throws IOException{
	String filename = "ReporteConsolidados.csv";
	String where_=request.getParameter("where_");
	
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_Consolidado");
		fw.append(',');
		fw.append("_Unidad_RM");
		fw.append(',');
		fw.append("_Tipo_Cons");
		fw.append(',');
		fw.append("_Alcance");
		fw.append(',');
		fw.append("_Descripción");
		fw.append(',');
		fw.append("_Estado_Con");
		fw.append(',');
		fw.append("_CantidadLineasCons");
		fw.append(',');
		fw.append("_Monto_Bruto");
		fw.append(',');
		fw.append("_Monto_Neto");
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = "select cIdConsolidadoCons,cIdUnidadEjecutoraCons,cIdTipoConsolidadoCons,cAlcanceCons"+
						",cDescripcionCons,cEstadoCons,nCantidadLineasCons,mMontoConsolidadoCons"+
						",mMontoConsolidadoIVACons from vconsultaConsolidado where "+where_+" order by cIdConsolidadoCons";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8)!=null? rs.getString(8).replaceAll(",", "") : "$0.00");
			fw.append(',');
			fw.append(rs.getString(9)!=null? rs.getString(9).replaceAll(",", "") : "$0.00");
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}
private void ReporteConsultaPedidos(HttpServletRequest request, HttpServletResponse response) throws IOException{
	String filename = "ReporteConsultaPedidos.csv";
	String where_=request.getParameter("where_");
	
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_IdDefinitivo");
		fw.append(',');
		fw.append("_IdPedido");
		fw.append(',');
		fw.append("_Ubidad_RM");
		fw.append(',');
		fw.append("_IdProcedimiento");
		fw.append(',');
		fw.append("_Descripción");
		fw.append(',');
		fw.append("_RFC");
		fw.append(',');
		fw.append("_Razón_Social");
		fw.append(',');
		fw.append("_fFormalización");
		fw.append(',');
		fw.append("_fFallo");
		fw.append(',');
		fw.append("_fEntrega");
		fw.append(',');
		fw.append("_EstadoPedido");
		fw.append(',');
		fw.append("_MontoBruto");
		fw.append(',');
		fw.append("_MontoNeto");
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = " select  cIdPedidoDefinitivo ,cIdPedido ,cIdUnidadEjecutora "+
						",cIdProcedimiento ,cConceptoPedido ,cIdRFC ,cRazonSocial ,convert(date,fFormalizacion)fFormalizacion "+
						",convert(date,fFallo)fFallo ,convert(date,fEntrega)fEntrega ,cEstado ,mPedidoMontoBruto ,mPedidoMontoNeto from v_consultaPedidos where "+where_+" order by cIdPedido";
		System.out.println("query :"+query);
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append(',');
			fw.append(rs.getString(10));
			fw.append(',');
			fw.append(rs.getString(11));
			fw.append(',');
			fw.append(rs.getString(12)!=null? rs.getString(12).replaceAll(",", "") : "$0.00");
			fw.append(',');
			fw.append(rs.getString(13)!=null? rs.getString(13).replaceAll(",", "") : "$0.00");
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
	
}
private void ReporteConsultaContratos(HttpServletRequest request, HttpServletResponse response) throws IOException{
	String filename = "ReporteConsultaPedidos.csv";
	String where_=request.getParameter("where_");
	
	Connection conn = null;
    Statement stmt=null;
	ResultSet rs=null;
	FileWriter fw=null;
	try {
		fw = new FileWriter(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
		fw.append("_IdDefinitivo");
		fw.append(',');
		fw.append("_IdContrato");
		fw.append(',');
		fw.append("_Ubidad_RM");
		fw.append(',');
		fw.append("_IdProcedimiento");
		fw.append(',');
		fw.append("_Descripción");
		fw.append(',');
		fw.append("_RFC");
		fw.append(',');
		fw.append("_Razón_Social");
		fw.append(',');
		fw.append("_fFormalización");
		fw.append(',');
		fw.append("fInicio");
		fw.append(',');
		fw.append("fFin");
		fw.append(',');
		fw.append("_fEntrega");
		fw.append(',');
		fw.append("_EstadoPedido");
		fw.append(',');
		fw.append("_MontoBruto");
		fw.append(',');
		fw.append("_MontoNeto");
		
		fw.append('\n');
		
		conn = DataSourceManager.getConnection(jndiName);
		String query = " select  cIdContratoDefinitivo ,cIdContrato ,cIdUnidadEjecutora "+
						",cIdProcedimiento ,cConceptoContrato ,cIdRFC ,cRazonSocial ,convert(date,fFormalizacion)fFormalizacion "+
						",convert(date,fFallo)fFallo ,convert(date,fInicio)fInicio ,convert(date,fFin)fEntrega "+
						",cEstado ,mContratoMontoBruto ,mContratoMontoNeto from v_consultaContratos WITH (NOLOCK) where "+where_+" order by cIdContrato";
		System.out.println("query :"+query);
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while(rs.next())
		{
			fw.append(rs.getString(1));
			fw.append(',');
			fw.append(rs.getString(2));
			fw.append(',');
			fw.append(rs.getString(3));
			fw.append(',');
			fw.append(rs.getString(4));
			fw.append(',');
			fw.append(rs.getString(5));
			fw.append(',');
			fw.append(rs.getString(6));
			fw.append(',');
			fw.append(rs.getString(7));
			fw.append(',');
			fw.append(rs.getString(8));
			fw.append(',');
			fw.append(rs.getString(9));
			fw.append(',');
			fw.append(rs.getString(10));
			fw.append(',');
			fw.append(rs.getString(11));
			fw.append(',');
			fw.append(rs.getString(12));
			fw.append(',');
			fw.append(rs.getString(13)!=null? rs.getString(13).replaceAll(",", "") : "$0.00");
			fw.append(',');
			fw.append(rs.getString(14)!=null? rs.getString(14).replaceAll(",", "") : "$0.00");
			
			fw.append('\n');

		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();

	}
	mostrarArchivo(filename,conn,stmt,rs,fw,request, response);	
}
	private void mostrarArchivo(String filename,Connection conn,Statement stmt,ResultSet rs,FileWriter fw,HttpServletRequest request, HttpServletResponse response)throws IOException{
		int leidos1=0;
		ServletOutputStream outS = null;
		FileInputStream fileInput1 =null;
        BufferedInputStream bufferedInput1 =null;
        BufferedOutputStream bufferedOutput=null;
		
		try {
			fw.flush();
			fw.close();
			//mostrarArchivo(response,filename,outS,fileInput1,bufferedInput1, bufferedOutput, leidos1);
			response.setContentType("application/csv");
		    String disposition = "attachment; fileName="+filename;
		    response.setHeader("Content-Disposition",disposition);
		    outS = response.getOutputStream();
		    fileInput1 = new FileInputStream(System.getProperty( "java.io.tmpdir" ) + File.separatorChar +filename);
			bufferedInput1 = new BufferedInputStream(fileInput1);
			bufferedOutput = new BufferedOutputStream(outS);

		   // Bucle para leer de un fichero y escribir en el otro.
			byte [] array1 = new byte[bufferedInput1.available()];
				leidos1 = bufferedInput1.read(array1);
				//leidos1=bufferedInput1.read();
				while (leidos1 > 0)
				{
					bufferedOutput.write(array1,0,leidos1);
					leidos1=bufferedInput1.read(array1);
				}
			
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				
			} catch (SQLException exc) {
				log.info("Cerrando conexion a base de datos", exc);
			}
			bufferedInput1.close();
			bufferedOutput.close();
			stmt = null;
			conn = null;
			rs=null;
		}
	}
	private void creaEncabezado(FileWriter fw,String arrayEncabezado[]) throws IOException{
		for(int i=0; i<arrayEncabezado.length;i++){
			fw.append(arrayEncabezado[i]);
			if(i!=arrayEncabezado.length-1){
				fw.append(',');	
			}else{
				fw.append('\n');
			}
		}
	}
	private FileWriter creaDetalle(FileWriter fw,ResultSet rs,String arrayEncabezado[]) throws IOException, SQLException{
		while(rs.next())
		{
			for(int i=0; i<arrayEncabezado.length;i++){
				fw.append(rs.getString(i+1).replaceAll(",", ""));	
				if(i!=arrayEncabezado.length-1){
					fw.append(',');	
				}else{
					fw.append('\n');
				}
			}
		}
		return fw;	
	}		
}