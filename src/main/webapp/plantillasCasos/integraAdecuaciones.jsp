<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.contable.adecuaciones.Exception.IADEClavesNeteadasException"%>
<%@page import="java.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.ArchivoExcel"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%! private static Logger log = LoggerFactory.getLogger("com.syc.plantillas.casos.integraAdecuaciones.jsp"); %>
<%
	ArrayList arrDatsoGuardados = new ArrayList();
	ArrayList arrResultadoIntegrado = new ArrayList();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cAnioFiscal = ""; //c1.getTime().getYear();
	String cTipoAdecuacion="";
	String cMensajeErrorIntegra="";
	String nNivel="";
	String cCentroContable = "";
	String resultadoCancelacion = null;
	String cGrupoAutorizaAdec="";
	String nNumSicop = "";
	String cRecMotivSicop = "";
	String nNumMAP = "";
	String muchosFolios ="";
	int muchosFolios2 = 0;
	String cRecMotivMAP = "";
	String cFechaAplicacion = "";
	String bValidaSaldo="";
	String cSuperReduccion ="";
	String errMensajeVAdec="";
	String nFolioIntegraAdec="";
	String[] cFoliosIntegrados= null;
	String[] cTipoIntegrados= null;
	String[] cNivelIntegrados= null;
	String[] cMontoIntegrados= null;
	String[] cUusarioIntegrados= null;
	String[] cAplicacionIntegrados= null;
	String[] cUnidadIntegrados= null;
	String[] cDescartarIntegrados= null;
	String cRamo="";
	int bActivaAplicar = 0;
	int id_oper = -1;
	int nFolio =0;
	int nDesIntegrado=0;
	boolean motorNuevo = true;
	boolean bErrorArchivo = false;
	boolean bErrorAdec=false;
	boolean bIntegraGuardado=false;
	int nConsecutivoSICOP=0;
	String mensajeVAdec = "";
	String mensaje = "";
	ArrayList arrResultado = new ArrayList();
	ArrayList sResultado = new ArrayList();
	ArrayList arrmEpCalendario = new ArrayList();
	ArrayList<String> arrUnidades = new ArrayList<String>();
	String arrFolios = null;
	String cMensajeNivel=""	;
	String[] nNumCAL = new String[2];
	String[] NumSicop = new String[2];
	String[] NumMAP = new String[2];
	String neteos = "";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (c.getCasoDato("MENSAJE").getValor() != null) {
		mensaje = c.getCasoDato("MENSAJE").getValor();
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace("'", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	mensaje=Util.encodeJS(mensaje);

      	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();
	System.out.println(id_oper);
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoExcel = null;
	String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete();//se usa por separado abajo
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);

	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	if (request.getParameter("arrFolio") != null && request.getParameter("arrFolio") != ""){							
		muchosFolios = request.getParameter("arrFolio"); 
	}
		
	try{
		nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
		arrUnidades = adecua.getUnidades();
		cRamo=usuario.getU_Ramo();
		arrDatsoGuardados=adecua.buscaIntegrada(nFolio);
		arrResultadoIntegrado = adecua.buscaIntegracionAdec(c, nFolio, cTipoAdecuacion, usuario.getLogin(), nNivel, cRamo, cAnioFiscal );
		if (arrResultadoIntegrado.size() > 5){
			nConsecutivoSICOP=adecua.buscaConsecutivoSICOP(nFolio);
				if(session.getAttribute("objIntegraAdecuacion")!=null)
					 session.removeAttribute("objIntegraAdecuacion");
			session.setAttribute("objIntegraAdecuacion", arrResultadoIntegrado);
			bIntegraGuardado=true;
		}else{
				if(session.getAttribute("objConsecutivoSICOP")!=null)
					 session.removeAttribute("objConsecutivoSICOP");
				nConsecutivoSICOP=0;
		}
		if (nConsecutivoSICOP > 0 ){
				if(session.getAttribute("objConsecutivoSICOP")!=null)
					 session.removeAttribute("objConsecutivoSICOP");
			session.setAttribute("objConsecutivoSICOP", nConsecutivoSICOP);
		
		}

	}catch(Exception ex){
		log.warn(ex.getMessage(), ex);
		mensaje = Util.encodeJS(ex.getMessage());
	}
	//Valida Centro de Costos

	Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
	try{
		cAnioFiscal = adecua.obtenEjercicioFiscal();
		if (bIntegraGuardado){
			if (request.getParameter("nivelAdecu") != null && request.getParameter("tipoAdecu") != null){
				cTipoAdecuacion = request.getParameter("tipoAdecu");
				nNivel = request.getParameter("nivelAdecu");
			}
			ArrayList arrIntegradoDetalle = adecua.buscaIntegracionAdec(c, nFolio, cTipoAdecuacion, usuario.getLogin(), nNivel, cRamo, cAnioFiscal );
				if(session.getAttribute("objIntegraAdecuacion")!=null)
					 session.removeAttribute("objIntegraAdecuacion");
			session.setAttribute("objIntegraAdecuacion", arrIntegradoDetalle);
		}
	}catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			mensaje = Util.encodeJS(ex.getMessage());
	}
	if ( muchosFolios  != "" && request.getParameter("AgregaDatos") != null && "SI".equals(request.getParameter("AgregaDatos") )){
		nNumSicop = request.getParameter("nNumSicop");
		nNumMAP = request.getParameter("nNumMAP");

		try{
			if (nNumSicop == "" && nNumMAP == "" ){																					
				arrDatsoGuardados = adecua.integraAdecuaciones(nFolio , muchosFolios, usuario);			
			}else {
				String fFechaSicop=""; 
				String fFechaMAP="";
				String cMotSicop=""; 
				String cMotMAP="";
				if (request.getParameter("DPC_fFechaSicop") != null ){
					fFechaSicop=request.getParameter("DPC_fFechaSicop");
				}
				
				if (request.getParameter("DPC_fFechaMAP") != null ){
					fFechaMAP=request.getParameter("DPC_fFechaMAP");
				}				
				if (request.getParameter("cRecMotivSicop") != null ){
					cMotSicop=request.getParameter("cRecMotivSicop");
				}
				
				if (request.getParameter("cRecMotivMAP") != null ){
					cMotMAP=request.getParameter("cRecMotivMAP");
				}				
				
				arrDatsoGuardados.addAll(adecua.integraAdecuaciones(nFolio , nNumSicop, nNumMAP, usuario, fFechaSicop, fFechaMAP, cMotSicop, cMotMAP));
				cMensajeErrorIntegra = (String) arrDatsoGuardados.get(arrDatsoGuardados.size() -1);				
				arrDatsoGuardados.remove(arrDatsoGuardados.size() -1);
				if (!"".equals(cMensajeErrorIntegra)){
					bErrorAdec=true;
				}
			}
		}catch( IADEClavesNeteadasException iacne ){
			mensaje = iacne.getMessage();
		}catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			mensaje = Util.encodeJS(ex.getMessage());
		}
	}	// Autorizacion
	if (request.getParameter("cAplicaDocto") != null && "SI".equals(request.getParameter("cAplicaDocto"))) {
			System.out.println("antes de validar Aplicacion");
			nNumSicop = request.getParameter("nNumSicop");
			nNumMAP = request.getParameter("nNumMAP");
			String fFechaSicop=""; 
			String fFechaMAP="";
			String cMotSicop=""; 
			String cMotMAP="";
			if (request.getParameter("DPC_fFechaSicop") != null ){
				fFechaSicop=request.getParameter("fSICOP");				
			}
				
			if (request.getParameter("DPC_fFechaMAP") != null ){
				fFechaMAP=request.getParameter("fMAP");				
			}				
			if (("".equals(fFechaSicop) && "".equals(nNumSicop))&&("".equals(fFechaMAP) && "".equals(nNumMAP) )){
				mensaje = "Se requiere indique los datos de Autorizacion.";
			}else{
				ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
				boolean esAmbienteDesarrollo = "true".equalsIgnoreCase( cabl.getSystemSetting( "AMBIENTE_DESARROLLO" ) );
				if ( !esAmbienteDesarrollo )
					adecua.correoProduccion=true;
				adecua.autorizaIntegracion(nFolio, nNumSicop, fFechaSicop, cRecMotivSicop, nNumMAP, fFechaMAP, cRecMotivMAP, m, prefixPath, usuario, cSuperReduccion, cCentroContable, c);
			}
	}
	
	//CANCELACIÓN
	if (request.getParameter("cancelarDoc") != null && "SI".equals(request.getParameter("cancelarDoc"))) {
		nDesIntegrado=adecua.desIntegra(nFolio, c, usuario.getLogin(), m, prefixPath);
	}

	int iAdecuacion = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	nFolioIntegraAdec=c.getFolio();
	int nTotalSecuenciasArch = 0;
	try {
		if (arrDatsoGuardados.size() > 0){
			cFoliosIntegrados= new String[arrDatsoGuardados.size()];
			cTipoIntegrados= new String[arrDatsoGuardados.size()];
			cNivelIntegrados= new String[arrDatsoGuardados.size()];
			cMontoIntegrados= new String[arrDatsoGuardados.size()];
			cUusarioIntegrados= new String[arrDatsoGuardados.size()];
			cAplicacionIntegrados= new String[arrDatsoGuardados.size()];
			cUnidadIntegrados= new String[arrDatsoGuardados.size()];
			cDescartarIntegrados= new String[arrDatsoGuardados.size()];			
			
			int i=0; 
			while (arrDatsoGuardados.size()-1 >= i){
				ArrayList<String> arrPaso =  (ArrayList<String>) arrDatsoGuardados.get(i);
				cFoliosIntegrados[i]    = (String) arrPaso.get(0);
				cTipoIntegrados[i]      = (String) arrPaso.get(1);
				cNivelIntegrados[i]     = (String) arrPaso.get(2);
				cMontoIntegrados[i]     = (String) arrPaso.get(3);
				cUusarioIntegrados[i]   = (String) arrPaso.get(4);
				cAplicacionIntegrados[i]= (String) arrPaso.get(5);
				cUnidadIntegrados[i]    = (String) arrPaso.get(6);
				cDescartarIntegrados[i] = (String) arrPaso.get(7);				
				i++;
			}
		}
	} catch (Exception ex) {
		log.warn(ex.getMessage(), ex);
		adecua.ErrorAdecuacuines(c, ex.getMessage(), usuario.getLogin(), id_oper, m, prefixPath);
	}

	mensaje= Util.encodeJS(mensaje);
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Integración de Adecuaciones Presupuestales</title>
		
		<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
				
		<script type="text/javascript" charset="utf-8">
			var integracionSinError = true;
			var avanzaProcesando = false;
			var oTableBusIntg;
			var oTablePA;
			var id_oper = <%=id_oper%>;
			
			$(document).ready(
			    
				function() {
					
					integracionSinError = validaNeteo();
					$('#dialogMotor').dialog({
				      	autoOpen: false,
			  			width: 900,
			 			heigth: 2900
			    	});
			    
					$('.currency').blur(function(){
						$('.currency').formatCurrency();
					});
					
					oTableBusIntg = $('#grdBuscaIntegracion').dataTable({
							"bPaginate": false,
							"bLengthChange": false,
							"bFilter": true,
							"bSort": false,
							"bInfo": false,
							"bAutoWidth": false,
							"sScrollX": "100%",
							"bScrollCollapse": true,	
							"bJQueryUI": true,
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers",
							oLanguage: {
								sProcessing: "Procesando...",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
								sLoadingRecords: "Cargando...",
								sInfo: "Registros _START_ al _END_ de _TOTAL_",
								sInfoEmpty: "Registro 0 al 0 de 0",
								sInfoFiltered: "(filtered from _MAX_ total entries)",
								sInfoPostFix: "",
								sInfoThousands: ",",
								sSearch: "Filtro:",
								oPaginate: {
									sFirst:    "Primero",
									sPrevious: "Ant.",
									sNext:     "Sigte.",
									sLast:     "&Uacute;ltimo"
								}
							}
						});
	
					oTablePA = $('#grdIntegra').dataTable({
							"bPaginate": false,
							"bLengthChange": false,
							"bFilter": true,
							"bSort": false,
							"bInfo": false,
							"bAutoWidth": false,
							"sScrollX": "100%",
							"bScrollCollapse": true,	
							"bJQueryUI": true,
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers",
							oLanguage: {
								sProcessing: "Procesando...",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
								sLoadingRecords: "Cargando...",
								sInfo: "Registros _START_ al _END_ de _TOTAL_",
								sInfoEmpty: "Registro 0 al 0 de 0",
								sInfoFiltered: "(filtered from _MAX_ total entries)",
								sInfoPostFix: "",
								sInfoThousands: ",",
								sSearch: "Filtro:",
								oPaginate: {
									sFirst:    "Primero",
									sPrevious: "Ant.",
									sNext:     "Sigte.",
									sLast:     "&Uacute;ltimo"
								}
							}
						});
					
					/* Add a click handler to the rows - this could be used as a callback */
					$("#grdIntegra tbody").click(function(event) {
						$(oTablePA.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
							var aPos = oTablePA.fnGetPosition( this.nTr );
							var aData = oTablePA.fnGetData( aPos[0] );
						});
						
						$(event.target.parentNode).addClass('row_selected');
						
					});
	
					var $dlgError;
					
					$(function(){
						$('#dlgError').dialog({
						autoOpen: <%=bErrorAdec%>,
							width: 1200,
							heigth: 1800
						});
					});
					
					var $divBuscaIntegracion;
					
					$(function(){
						$('#divBuscaIntegracion').dialog({
						autoOpen: false,
							width: 1200,
							heigth: 900
						});
					});				
					
			    	$('#Exportar').button();
			    	$('#bBusca').button();
			    	$('#Calendario').button();		    	
			    	$('#bIntegrar').button();
			    	$('#cTodos').button();
			    	$('#cNinguno').button();	
			    	$("#Justificacion").button();
									
					queryFormPost( "readInfoCalendarioIntegracion", {async:false}  );
					queryFormPost( "readInfoSicopIntegracion", {async:false}  );
					queryFormPost( "readInfoMAPIntegracion", {async:false}  );
					queryFormPost( "readInfoJustificacionntegracion", {async:false} );
					
					var j=<%=arrDatsoGuardados.size()%>;
					
					<% int i=0; %>
					
					if ( j > 0 ){
					
						<% for (i = 0; i < arrDatsoGuardados.size(); i++ ){ %>
							if(integracionSinError)
								fnClickAddRowB(  "", "<%=cFoliosIntegrados[i]%>", "<%=cTipoIntegrados[i]%>", "<%=cNivelIntegrados[i]%>", "<%=cMontoIntegrados[i]%>", "<%=cUusarioIntegrados[i]%>", "" );
							else
								fnClickAddRowB(  "", "<%=cFoliosIntegrados[i]%>", "<%=cTipoIntegrados[i]%>", "<%=cNivelIntegrados[i]%>", "<%=cMontoIntegrados[i]%>", "<%=cUusarioIntegrados[i]%>", "", "",generaImagenEliminar("<%=cFoliosIntegrados[i]%>" ) );
						<%}%>
						
					}
					
					if (<%=nConsecutivoSICOP%> > 0 ){
						document.formIntegracion.cCancelar.disabled=false;
						document.formIntegracion.cAutorizar.disabled=false;
						document.getElementById("nivelAdecu").disabled=true;
						document.getElementById("tipoAdecu").disabled=true;
						document.getElementById("cUnidadResponsable").disabled=true;
						document.getElementById("bBusca").disabled=true;
						if(id_oper!=3 && id_oper!=4){
							parent.document.getElementById("pb_save").disabled=false;
						}if(id_oper==3 || id_oper==4){
							$("#cCancelar").attr("disabled","disabled");
							$("#cAutorizar").attr("disabled","disabled");
						}
					}else{
						parent.document.getElementById("pb_save").disabled=true;
						document.formIntegracion.cCancelar.disabled=true;
						document.formIntegracion.cAutorizar.disabled=true;
					}								
			});
			
			function onLoadPlantilla(id_oper){
				if(<%=c.getIdGabinete() == -1%>){
					$("#Exportar").attr('disabled','disabled');
					$("#Justificacion").attr('disabled','disabled');
				}				if(id_oper==3){
					parent.document.getElementById("pb_cancel").disabled=true;
					$("#dlgError").hidde();
					document.getElementById("nivelAdecu").disabled=true;
					document.getElementById("tipoAdecu").disabled=true;
					document.getElementById("cUnidadResponsable").disabled=true;
					document.getElementById("bBusca").disabled=true;
					$("#cCancelar").attr("disabled","disabled");
					$("#cAutorizar").attr("disabled","disabled");
					
				}
				
				if(id_oper!=3 && id_oper!=4){
					parent.document.getElementById("pb_send").disabled=true;
				}
										
			}
			
			function seleccionarTodos(){
				$('#grdBuscaIntegracion input:checkbox').each(
					function(){
						$(this).attr("checked",true);
					}
				);
			}
			
			function deseleccionarTodos(){
				$('#grdBuscaIntegracion input:checkbox').each(
					function(){
						$(this).removeAttr("checked");
					}
				);
			}
			
			function seleccionarTodosInt(){
				$('#grdIntegra input:checkbox').each(
					function(){
						$(this).attr("checked",true);
					}
				);
			}
			
			function generaImagenEliminar(folioAdecuacion){
				var cmdImg  = '<a href="#" onclick="return false;"><img border="0" src="../imagenes/cancelar.gif" width="25" height="21" alt="Descartar Adecuacion" onclick="eliminarAdecuacion(\'' + folioAdecuacion + '\');"/></a>';
				return cmdImg;			
			}
			
			function eliminarAdecuacion(folioAdecuacion){
				var nFolioAdecua = folioAdecuacion.substring( folioAdecuacion.lastIndexOf("-") + 1  );
				$( "#folioEliminar").val(  nFolioAdecua );
				
				var idxTbl = -1;
				var datosTabla = $("#grdIntegra").dataTable().fnGetData();
				for( i = 0; i < datosTabla.length; i++){
					if( datosTabla[i][1] == folioAdecuacion ){
						idxTbl = i;
						break;
					}
				}
				
				if( idxTbl >= 0 )
					queryFormPost({
						queryName:"deleteAdecuacionIntegracion,adecuacionSinConsolidacionUpdate",
						async:false,
						callback:function(){
							$( "#folioEliminar").val("");
							$("#grdIntegra").dataTable().fnDeleteRow(idxTbl);
						}
					});
			}
			
			function validaNeteo(){
				var exito = false;
				$.ajax({
						url : '../ValidacionIADEServlet',
						dataType : 'json',
						type : "GET",
						data : {
							"nFolioIADE" : $("#nFolioCONSOLIDACION").val()
						},
						async : false,
						success : function(json) {
							exito = "true" == json.success;
		
							if (!exito) {
								Swal.fire({ icon: 'error',
											text: json.data_1.result });								
							} else {
								exito = true;
							}
						},
						error : function(xhr, textStatus, errorThrown) {
							Swal.fire({ icon: 'warning',
										text: "Advertencia: " + xhr.responseText + "\nEstatus: "
												+ textStatus + "\n" + errorThrown });							
							exito = false;
						}
				});
				
				return exito;
			}
						
			function exportaIntegracion(){
				var cTipoExportascion=$("#exporta").val();
				var cyaEjecutado=$("#iEjecutoAntes").val();
				<% 
					if(session.getAttribute("objConsecutivoSICOP")!=null)
					 nConsecutivoSICOP= (Integer) session.getAttribute("objConsecutivoSICOP");
				%>
				if (<%=nConsecutivoSICOP%> == 0  && cTipoExportascion != 2){
					if (cyaEjecutado == 0  && cTipoExportascion != 2){
						Swal.fire({ icon: 'warning',
									text: "Requiere Generar Primero el Layout SICOP." });									
						return -1
					}
				}
				
				document.getElementById("iEjecutoAntes").value = "1";
				if (cTipoExportascion== ""){
					Swal.fire({ icon: 'warning',
								text: "Seleccione el tipo de Exportación" });							
					return -1
				}
				
				document.forms.ExportaExcel.action="../gstnmngr/IntegraAdecuaLayoutSicop?exporta=" + cTipoExportascion + "&nFolioIntegracion=" + $("#nFolioIntegracion").val() + "&nConsecutivoSICOP=" + $("#nConsecutivoSICOP").val();
				document.ExportaExcel.submit();
				recarga();
			}
	
			function get(name) {
				return document.getElementById(name).value;
			}
	
			function onSubmit(id_oper){
				var p = window.parent;
				var valida_campos = true;
				try{
					if (id_oper==1){
						parent.document.getElementById("pb_save").disabled=true;
						p.gestion.setFolio(get("nFolioIntegracion"));
						p.gestion.setOperador(get("OPERADOR"));
						p.gestion.setFechaDocumento("<%=today %>");
						p.gestion.setEjercicioFiscal("<%=cAnioFiscal %>");
						p.gestion.setConceptoMov("Adecuacion Presupuestal");
						p.gestion.setMoneda("MXP");												
					}				
						aplicaCont();
				}catch (e) {
					window.alert("onSubmit: Error: " + e.message);
					return false;
				}
				
				return valida_campos;			
			}
			
			function aplicaCont(){
				var p = window.parent;
				var i=0;
				var Tocken= ","
				var arrFolios="";
				var A=$("#nNumSicop").val();
				var B=$("#DPC_fFechaSicop").val();
				var C=$("#nNumMAP").val();
				var D=$("#DPC_fFechaMAP").val();
				var E=$("#cRecMotivSicop").val();
				var F=$("#cRecMotivMAP").val();				
								
					$('#grdIntegra input:checked').each(function(idx, elm){				
						if ( i > 0){
							arrFolios+=Tocken;
						}
						arrFolios+=$(this).parent('td').parent('tr').find('td:eq(1)').html();
						i++;
					});
					document.getElementById("arrFolio").value =arrFolios; 
				
					avanzaProcesando = true;
					if ( document.formIntegracion.cCancelar.checked && document.formIntegracion.cAutorizar.checked  ){
						Swal.fire({ icon: 'warning',
									text: "Seleccione solo Autorizar o Cancelar nunca los dos ." });						
						return;
					} 
					if ( document.formIntegracion.cAutorizar.checked ){
						var justificacionAmp = $("#cJustificacionAmp").val();
						var justificacionRed = $("#cJustificacionRed").val();
						if (justificacionAmp == "" || justificacionRed == ""){
							Swal.fire({ icon: 'warning',
										text: "Las justificaciones son campos obligatorios para poder autorizar la integración, favor de capturarlos." });								
							document.getElementById("cJustificacionAmp").focus();
							parent.document.getElementById("pb_save").disabled=false;
							parent.document.getElementById("pb_send").disabled=true;
							parent.document.getElementById("pb_cancel").disabled=true;
							return;
						} else{
							if (((A == "" ) || (B == "" ) ) || ( (C == "" ) || (D == "" ))) {
								Swal.fire({ icon: 'warning',
											text: "Indique los elementos de Autorizacion correspondientes (SICOP o MAP." });							
								return -1;
							}
							
							$("#fSICOP").val(B.split('-').reverse().join('/'));
							$("#fMAP").val(D.split('-').reverse().join('/'));
							
							p.gestion.setMensaje("Documento Autorizado");
							if (B != "" )
								p.gestion.setFechaApCont(B);
							if ( D != "")
								p.gestion.setFechaApCont(D);
							
							p.gestion.setAplicadoCont("true");
							p.gestion.setAutorizadoCont("true");
							
							parent.document.getElementById("pb_save").disabled=false;
							parent.document.getElementById("pb_save").click();
							document.forms.formIntegracion.action="integraAdecuaciones.jsp?cAplicaDocto=SI&folio=<%=nFolio%>";
							document.formIntegracion.submit();
							$.blockUI({
									message : "Por favor espere ......"
								  });													
							parent.document.getElementById("pb_send").disabled=false;
							parent.document.getElementById("pb_send").click();
							parent.document.getElementById("pb_save").disabled=true;
							parent.document.getElementById("pb_send").disabled=true;
						}
						
					} else if (document.formIntegracion.cCancelar.checked ){
						if ( D != "")
							p.gestion.setFechaApCont(D);					
						p.gestion.setMensaje("Documento Cancelado");
						if (B != "" )
							p.gestion.setFechaApCont(B);
						if ( D != "")
							p.gestion.setFechaApCont(D);
						if ( E != "")
							p.gestion.setMensaje("Documento Cancelado:"+ E);
						if ( F != "")
							p.gestion.setMensaje("Documento Cancelado:"+ F);
		
						p.gestion.setCanceladoCont("true");
						
						parent.document.getElementById("pb_save").disabled=false;
						parent.document.getElementById("pb_save").click();
						document.forms.formIntegracion.action="integraAdecuaciones.jsp?cancelarDoc=SI&folio=<%=nFolio%>";
						document.formIntegracion.submit();
						$.blockUI({
								message : "Por favor espere ......"
							  });
						parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_send").click();
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=true;
					}else{
						document.formIntegracion.submit();
				}
			}
	
			function onPostSubmit(id_oper){
				return true;
			}	
		
			function onPostDisplay(id_oper){
				guardaExp();
			}
		
			function guardaExp(){
				parent.document.getElementById("pb_save").disabled=false;
				parent.document.getElementById("pb_save").click();
				parent.document.getElementById("pb_cancel").disabled=true;
							
				if (<%=nConsecutivoSICOP %> > 0 ){
					document.getElementById("nConsecutivoSICOP").value = "<%=nConsecutivoSICOP%>"				
					document.getElementById("nivelAdecu").disabled=true;
					document.getElementById("tipoAdecu").disabled=true;
					document.getElementById("cUnidadResponsable").disabled=true;
					document.getElementById("bBusca").disabled=true;
				}
			}
		
			function actualizaFechaSICOPMAPIADE(){			
				queryFormPost("tAdecuacionEncabezadoFechaSICOPMAPUpdateIADE", {async:false});
				$("#fSICOP").val($("#DPC_fFechaSICOP").val().split('-').reverse().join('/'));
				$("#fMAP").val($("#DPC_fFechaMAP").val().split('-').reverse().join('/'));				
			}
		
			function ResponsableSiguiente(id_oper){
	
				if ( (document.formIntegracion.cAutorizar.checked) || (document.formIntegracion.cCancelar.checked )){
					if (($("#nNumSicop").val() != "" ) && ($("#DPC_fFechaSicop").val() == "" ) ) {
						Swal.fire({ icon: 'warning',
									text: "Indique la Fecha de Autorizacion SICOP." });						
						return "CAPTURISTA_INTEGADEC";
					}
				
					if ( ($("#nNumMAP").val() != "" ) && ($("#DPC_fFechaMAP").val() == "" )) {
						Swal.fire({ icon: 'warning',
									text: "Indique la Fecha de Autorizacion MAP." });						
						return "CAPTURISTA_INTEGADEC";
					}
					
					if(document.formIntegracion.cCancelar.checked){
						return "CONSULTA_INTEGRAADECUA";
						}
				}
				if(id_oper==1 )
					return "CONSULTA_INTEGRAADECUA";
				if(id_oper==2){
					return "CONSULTA_INTEGRAADECUA";
				}
			}
		
			function OperacionSiguiente(id_oper){
				if(id_oper==1 && document.formIntegracion.cCancelar.checked)
						return "consulta_integadec";
					if(id_oper==1)
						return "consulta_integadec";
					if(id_oper==2){
						return "consulta_integadec";
					}
			}
		
			function validaCamposLlave(){
				var msgAlert="";
				if(get("EJERCICIO_FISCAL")=="")
					msgAlert+="El ejercicio fiscal es requerido\n";
				if(get("FECHA_APLICACION_CONTABLE")=="")
					msgAlert+="La fecha de aplicación contable es requerida\n";
			
				if(	document.getElementById("uploadfile").value == "" ){//||
					msgAlert+="El archivo PDF de soporte de Adecuaciones Presupuestales es requerido";
				}
				if(msgAlert!=""){
					Swal.fire({ icon: 'warning',
								text: msgAlert });					
					return false;
				}
				else{
					document.adecform.submit();
					return true;
				}
			}
	
			function cancelarDoc(){
				if(!confirm("Enviara el Documento a Cancelar Contablemente.  \n \n  ¿desea continuar?")) {
					return false;
				}
			}
	
			function MostrarDialogError() {
				$('#dlgError').dialog('option', 'modal', true).dialog('open');
				return true;
			}
			
			function buscaAIntegrar(){
			 	var cDataQuery="";
			 	var cconcatenador=" AND ";
				if ($("#nivelAdecu").val() == "" && $("#tipoAdecu").val() == "" && $("#cUnidadResponsable").val() == "" ){
					Swal.fire({ icon: 'warning',
								text: "Es nesesario indicar alguno de los siguientes datos: Nivel, Tipo o Unidad." });					
					return;
				} 
				if ($("#nivelAdecu").val() != ""){
					cDataQuery=" nNivel = '"+$("#nivelAdecu").val()+"' "
				} 
				if ($("#tipoAdecu").val() != ""){
					if ($("#nivelAdecu").val() != "" ){
						cDataQuery+=cconcatenador;
					}
					cDataQuery+=" cTipoAdecuacion = '"+$("#tipoAdecu").val()+"' "
				} 
				if ($("#cUnidadResponsable").val() != "" ){
					if ( ($("#nivelAdecu").val() != "" ) || ($("#tipoAdecu").val() != "")){
						cDataQuery+=cconcatenador;
					}
					cDataQuery+=" cUnidadResponsable = '"+$("#cUnidadResponsable").val()+"'"
				}
				
				$("#grdBuscaIntegracion").dataTable({
					"bProcessing": true,
					"bServerSide": true,
					"bDestroy": true,
					"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vAdecuacion&qw="+cDataQuery,
					"bJQueryUI": true,
					"sScrollX": "100%",
					"bPaginate": false,
					"bAutoWidth": false,
					"bInfo": true,
					"aoColumns": [
						{ sName: "cDescartar"},
						{ sName: "nFolioAdecuacion"},
						{ sName: "cTipoAdecuacion" },
						{ sName: "nNivel" },
						{ sName: "mImporte"},
						{ sName: "cUsuarioCreador"},
						{ sName: "fAplicacion"},
						{ sName: "cUnidadResponsable"}
	    			]
				});
				$('#divBuscaIntegracion').dialog('option', 'modal', true).dialog('open');
				
			}
			
			function fnClickAddRowB(A, B, C,D,E,F,G,H, I) {
					if( !H )
						H="";
					if(!I)
						I="";
					$('#grdIntegra').dataTable().fnAddData( [ A,B,C,D,E,F,G,H, I ] );
					
			}
			
			function descartar(row){
					var total = 0.00;
					var valorEliminado = 0.00;
					var evento = row.find('td:eq(3) font').html();
					var index = row.index();
					aTrs = oTablePA.fnGetNodes();
					index = row.index();
					oTablePA.fnDeleteRow( index );
					$(row).remove();
					return false;
			}
	
			function fnIntegrar(){
				var info="";
				parent.document.getElementById("pb_cancel").disabled=false;
				$('#grdBuscaIntegracion input:checked').each(function(idx, elm){
					var A = $(this).parent('td').parent('tr').find('td:eq(0)').html();
					var B = $(this).parent('td').parent('tr').find('td:eq(1)').html();
					var C = $(this).parent('td').parent('tr').find('td:eq(2)').html();
				 	var D = $(this).parent('td').parent('tr').find('td:eq(3)').html();
				  	var E = $(this).parent('td').parent('tr').find('td:eq(4)').html();
				  	var F = $(this).parent('td').parent('tr').find('td:eq(5)').html();
				  	var G = $(this).parent('td').parent('tr').find('td:eq(6)').html();
				  	var I = $(this).parent('td').parent('tr').find('td:eq(7)').html();
				  	
				  	var H = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar Secuencia\" onClick=\"descartar($(this).parent('td').parent('tr'));\">";
				  	
				  	fnClickAddRowB(A,B,C,D,E,F,G,I,H);
				});
				
				seleccionarTodosInt();
				$('#divBuscaIntegracion').dialog('option', 'modal', true).dialog('close');	
				parent.document.getElementById("pb_save").disabled=false;
				
			}
			
			function marcaAutoriza(){
				if ( document.formIntegracion.cCancelar.checked ){
					document.formIntegracion.cCancelar.checked = false;
				} 
			}
			
			function marcaCancela(){
				if ( document.formIntegracion.cAutorizar.checked ){
					document.formIntegracion.cAutorizar.checked = false;
				} 
			}
			
			function recarga(){
				integracionSinError = validaNeteo();
				
				if(integracionSinError ){
					$.blockUI({
								message : "Por favor espere ......"
							  });
					document.forms.formIntegracion.action="integraAdecuaciones.jsp";
					document.formIntegracion.submit();
				}
			}
			
			function guardaCal(){	
				if ($("#nNumCAL").val() == "" || $("#FCAL").val() == "") {
						Swal.fire({ icon: 'warning',
									text: "Los campos de Calendario y Fecha son obligatorios" });					
					}	
				else {
					 queryFormPost({
					 	queryName:"actualizaCalendarioIntegracion",
					 	async:false,
					 	callback:function(){
					 		Swal.fire({ icon: 'success',
										text: "No. de Calendario actualizado exitosamente." });				 		
					 	}	
					 });				
				}		
			}
			
			function guardaJustificacion(){
				if ($("#cJustificacionAmp").val() == "" || $("#cJustificacionRed").val() == "") {
					Swal.fire({ icon: 'warning',
								text: "Los campos de Justificacion Ampliacion y Justificacion Reduccion son obligatorios" });					
				}	
			else {
				 queryFormPost({
				 	queryName:"actualizaJustificacionesIntegracion",
				 	async:false,
				 	callback:function(){
				 		Swal.fire({ icon: 'success',
									text: "La información fue actualizada exitosamente." });				 		
				 	}	
				 });				
				}						
			}
			
		</script>		
	</head>
<br/>
	<body id="dt_example">
		<div id="container" style="width: 80%" class="container">
			<div class="card-header"> <h3>Integración de Adecuaciones Presupuestarias</h3> </div>					
			<hr class="mt-3">
							
			<form id="ExportaExcel" name="ExportaExcel" action="../gstnmngr/IntegraAdecuaLayoutSicop" method="POST" target="_blank"></form>

			<form id="formIntegracion" name="formIntegracion" method="post" action="integraAdecuaciones.jsp?AgregaDatos=SI&folio=<%=nFolio%>" >
				<input type="hidden" name="folioEliminar" id="folioEliminar" value=""/>
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=usuario.getNombre() %>"/>
				<input type="hidden" name="arrFolio" id="arrFolio" value="" maxlength="800" size="80"/>
				<input type="hidden" name="iEjecutoAntes" id="iEjecutoAntes" value="0" maxlength="2" size="2"/>
				<input type="hidden" name="nFolioCONSOLIDACION" id="nFolioCONSOLIDACION" value="<%=nFolio%>" />
				<input type="hidden" name="fSICOP" id="fSICOP" value="" />
				<input type="hidden" name="fMAP" id="fMAP" value="" />
					
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="nFolioIntegracion" class="form-label"> Folio de Integración: </label>
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">																							
						<input type="text" id="nFolioIntegracion" name="nFolioIntegracion" class="form-control form-control-sm" style="width: 12em;" value="<%=c.getFolio() %>" readonly/>						
					</div>
				</div>

				&nbsp;
				
				<h5> Buscar </h5>
  				<hr class="mt-3">		
		
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="nivelAdecu" class="form-label"> Nivel de la Adecuacion: </label>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">													
						<select id="nivelAdecu" name="nivelAdecu" class="form-select form-select-sm">
							<option value=""></option>
							<option value="1">Interna</option>
							<option value="2">Interna SICOP</option>
							<option value="3">Interna SHCP</option>
							<option value="4">Externa SHCP sin restricci&oacute;n</option>
							<option value="5">Externa SHCP con restricci&oacute;n</option>
						</select>				
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="tipoAdecu" class="form-label"> Tipo de Adecuacion: </label>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<select id="tipoAdecu" name="tipoAdecu" class="form-select form-select-sm">
							<option value=""></option>
								<option value="Ampliación">Ampliación</option>
								<option value="Reducción">Reducción</option>
								<option value="Transferencia">Transferencia</option>
								<option value="Calendario">Calendario</option>
						</select>	
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<input type="button" name="bBusca" id="bBusca" value="Buscar"  onclick="buscaAIntegrar();" class="btn btn-secondary btn-sm"></input>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cUnidadResponsable" class="form-label"> Unidad Creadora: </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">												
						<select id="cUnidadResponsable" name="cUnidadResponsable" class="form-select form-select-sm">
							<option value=""></option>
								<% if (arrUnidades.size() > 0) { %>
								 	<% 	int j = 0;
								 		int iarrm=arrUnidades.size();
								 	 	while ( j < iarrm) {
								 	 %>
										<%=(String) arrUnidades.get(j)%>
									 <% 		j++;
										}
								} %>
						</select>
					</div>
				</div>
						
				&nbsp;
				
				<h5> Datos Integración </h5>
  				<hr class="mt-3">	
			
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="nConsecutivoSICOP" class="form-label"> Folio SICOP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="text" id="nConsecutivoSICOP" name="nConsecutivoSICOP" class="form-control form-control-sm" value="<%=nConsecutivoSICOP %>" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="exporta" class="form-label"> Tipo Archivo: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<select name="exporta" id="exporta" class="form-select form-select-sm">
							<option value="1">Archivo Excel</option>
							<option value="2">Layout SICOP</option>
							<option value="3">Formato FAP01</option>											
							<option value="4">Adecuaciones Integradas</option>
							<option value="5">Adecuaciones Integradas PDF</option>
						</select>							
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" name="Exportar" id="Exportar" value="Exportar"  onclick="exportaIntegracion();" class="btn btn-secondary btn-sm"></input>			
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="nNumSicop" class="form-label"> Autorizacion SICOP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="text" id="nNumSicop" name="nNumSicop" class="form-control form-control-sm" value="<%=StringUtils.isEmpty( NumSicop[0] )?"":NumSicop[0]%>"/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="DPC_fFechaSicop" class="form-label"> Fecha SICOP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="date" id="DPC_fFechaSicop" name="DPC_fFechaSicop" class="form-control form-control-sm" value="<%=StringUtils.isEmpty( nNumCAL[1] )?"":nNumCAL[1]%>"/>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="hidden" id="cRecMotivSicop" name="cRecMotivSicop" class="form-control form-control-sm" value=""/>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="nNumMAP" class="form-label"> Autorizacion MAP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="text" id="nNumMAP" name="nNumMAP" class="form-control form-control-sm" value="" onChange=""/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="DPC_fFechaMAP" class="form-label"> Fecha MAP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="date" id="DPC_fFechaMAP" name="DPC_fFechaMAP" class="form-control form-control-sm" onchange="actualizaFechaSICOPMAPIADE()"/>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="hidden" id="cRecMotivMAP" name="cRecMotivMAP" class="form-control form-control-sm" value=""/>
					</div>
				</div>
				
				<%							
					if (id_oper == 3) {
				%>
					<div class="row d-flex">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="nNumCAL" class="form-label"> Autorizacion Calendario: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<input type="text" id="nNumCAL" name="nNumCAL" class="form-control form-control-sm" value ="<%=StringUtils.isEmpty( nNumCAL[0] )?"":nNumCAL[0]%>"/>
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="FCAL" class="form-label"> Fecha Calendario: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<input type="date" id="FCAL" name="FCAL" class="form-control form-control-sm" value="<%=StringUtils.isEmpty( nNumCAL[1] )?"":nNumCAL[1]%>"/>							
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<input type="button" id="Calendario" name="Calendario" class="btn btn-secondary btn-sm" onclick="guardaCal();" value="Guarda Calendario"/>
						</div>
					</div>
				<%
					}											
				%>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cJustificacionAmp" class="form-label"> Justificación Ampliación: </label>
					</div>
					<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
						<textarea id="cJustificacionAmp" name="cJustificacionAmp" class="form-control form-control-sm" rows="4" cols="150"></textarea>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="button" id="Justificacion" name="Justificacion" class="btn btn-secondary btn-sm" onclick="guardaJustificacion();" value="Guarda Justificaciones"/>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cJustificacionRed" class="form-label"> Justificación Reducción: </label>
					</div>
					<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
						<textarea id="cJustificacionRed" name="cJustificacionRed" class="form-control form-control-sm" rows="4" cols="150"></textarea>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
						<input type="checkbox" name="cAutorizar" id="cAutorizar" class="form-check-input" value = "Autorizar" onclick="marcaAutoriza();"> &nbsp;Autorizar				
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
						<input type="checkbox" name="cCancelar" id="cCancelar" class="form-check-input" value = "Cancelar" onclick="marcaCancela();"> &nbsp;Des-Integrar				
					</div>
				</div>
				
				<div class="table-responsive">	    					
					<table  id="grdIntegra" class="table table-striped">
						<thead>
							<tr>
								<th>Sec.</th>
								<th>Folio Adecuacón</th>
								<th>Tipo</th>
								<th>Nivel</th>
								<th>$ Monto</th>
								<th>Usuario Creador</th>
								<th>Fecha Aplicacion</th>
								<th>Unidad Usuario</th>
								<th style="text-align: center;">Descartar Secuencia</th>
							</tr>
						</thead>											
					</table>							
				</div>
				
			</form>

		</div>
		
		<div id="divBuscaIntegracion" class="container" style="width: 80%">
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" name="bIntegrar" id="bIntegrar" value="Integrar"  onclick="fnIntegrar();" class="btn btn-secondary btn-sm"></input>					
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" value="Todos"  name="cTodos" id="cTodos"  onclick="seleccionarTodos()" class="btn btn-secondary btn-sm"></input>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" value="Ninguno" name="cNinguno" id="cNinguno"  onclick="deseleccionarTodos()" class="btn btn-secondary btn-sm"></input>
				</div>
			</div>
			
			<div class="table-responsive">	    					
				<table  id="grdBuscaIntegracion" class="table table-striped">			
					<thead>
						<tr>
							<th>Sec.</th>
							<th>Folio Adecuación</th>
							<th>Tipo</th>
							<th>Nivel</th>
							<th>$ Monto</th>
							<th>Usuario Creador</th>
							<th>Fecha Aplicacion</th>
							<th>Unidad Usuario</th>
						</tr>
					</thead>				
				</table>
			</div>
			
		</div>

		<div id="dlgError" title="Detalle de Errores de Adecuaciones Presupuestales">
			<p>Detalle de Errores </p> <a rel=""></a>
			
			<table   class="display" id="grdAnteProyecto">
				<tr> 
					<td>
						<textarea id="mensajeError" name="mensajeError" rows="16" cols="140"></textarea>
					</td>
				</tr>
			</table>
			
		</div>
	</body>
</html>