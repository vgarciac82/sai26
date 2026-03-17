<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.ContratoPlurianualBusinessLogic"%>
<%@page import="java.util.List;"%>


<%
	// Asi obtengo el usuario y el folio 
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cEjercicio;
	String msg = "";
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	int mesActual = c1.get(Calendar.MONTH) + 1;
	String today = fe.format(c1.getTime());
	String cCentroContable = "";
	String UR = usuario.getU_UR();
	String ramo = usuario.getU_Ramo();
	String cUnidadResponsableContable = "B00";
	String tipoPlurianual = "";
	String tipoSolicitud = "";
	String valContra = "";

	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	cEjercicio = adecProy.obtenEjercicioFiscal();

	ContratoPlurianualBusinessLogic plurianualBusinessLogic = new ContratoPlurianualBusinessLogic(
			usuario.getLogin());

	final int nFolioPurianual = new Integer(c.getFolio().substring(
			c.getFolio().lastIndexOf('-') + 1)).intValue();

	String añoFin = plurianualBusinessLogic.getAñoFinal(String
			.valueOf(nFolioPurianual));
	if (añoFin == null || "0".equals(añoFin)) {
		añoFin = cEjercicio;
	}
	String añoIni = plurianualBusinessLogic.getAñoInicial(String
			.valueOf(nFolioPurianual));
	String nmod="0";		
	List<String> modificaciones = plurianualBusinessLogic.listadoModificaciones(String.valueOf(nFolioPurianual));
	if(!modificaciones.isEmpty()){
		nmod=modificaciones.get(modificaciones.size()-1);
	}
			
	if (añoIni == null || "0".equals(añoIni)) {
		añoIni = cEjercicio;
	}
	if (request.getParameter("msg") != null) {
		msg = request.getParameter("msg");
	}
	if (request.getParameter("tipoPlurianual") != "") {
		tipoPlurianual = request.getParameter("tipoPlurianual");
	}
	if (request.getParameter("tipoSolicitud") != "") {
		tipoSolicitud = request.getParameter("tipoSolicitud");
	}
	if (request.getParameter("valContra") != "") {
		valContra = request.getParameter("valContra");
	}
	
		if("Especial".equals(tipoSolicitud))
	   	 	cEjercicio = String.valueOf(Integer.parseInt(cEjercicio) + 1);
%>






<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>
<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<style>
.notEditable {
	text-align: right;
	border: 1px solid #aaaaaa;
	background-color: #CCCCCC;
}
.notEditable2 {
	text-align: left;
	border: 1px solid #aaaaaa;
	background-color: #CCCCCC;
}
.monto {
	text-align: right;
	border: 1px solid #aaaaaa;
	color: #222222;
}

.porcentaje {
	text-align: right;
	border: 1px solid #aaaaaa;
	color: #222222;
}

.etiqueta {
	text-align: rigth;
	border: 1px solid #aaaaaa;
	background-color: #CCCCCC;
}

.textareaContainer {
	display: block;
	border: 2px solid #38c;
	padding: 10px;
}

textarea {
	width: 100%;
	margin: 0;
	padding: 0;
	border-width: 0;
}
</style>




<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
var oTable;
var oTableCalendario;
var nModificacion;
var oTablePres;
var gb_Realiza = false;
	function getParameterByName(name, url) {
	    if (!url) url = window.location.href;
	    name = name.replace(/[\[\]]/g, "\\$&");
	    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
	        results = regex.exec(url);
	    if (!results) return null;
	    if (!results[2]) return '';
	    return decodeURIComponent(results[2].replace(/\+/g, " "));
	};


	 //Inicia llamado de pagina
	$(document).ready(function() {
		
		
		VerificaTiposDePlurianualesDisponibles();
		
			var nModifica = <%=nmod%>;
			if(nModifica != "" && nModifica != null){
				$("#nmod").val(nModifica);
				nModificacion = 0;
				$("#titulo").html("<label>Solicitud de Contratos Plurianuales y Especiales-Modificación No."+nModifica+"</label>");
				
				parent.document.getElementById("pb_cancel").disabled=true;
				
			}else{
				$("#nModificacion").val("0");
				nModificacion = "0";
			}
	
		


		var nFolioPlurianual = getParameterByName("nFolioPlurianual");
		
		if(nFolioPlurianual != "" && nFolioPlurianual != null){
			$("#nFolioPurianual").val(nFolioPlurianual);
		}

		var FolioSAI = getParameterByName("FolioSAI");
		
		if(FolioSAI != "" && FolioSAI != null){
			$("#cFolioSAI").val(FolioSAI);
			$("#folio").val(FolioSAI);
		}

		$(".encabezado").click(function () {
		
			$encabezado = $(this);
		    $contenido = $encabezado.next();
		    $contenido.slideToggle(500, function () {});
		
		});
			
	  
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$("#TipoCambio").attr("disabled",true);
		$("#Select_Estatus").attr("disabled",true);  
		$("#h_folio").val('<%=c.getFolio()%>');
		$("#h_aEjercicioFiscal").val('<%=cEjercicio%>');
		$("#h_cRamo").val('<%=ramo%>');
		$("#h_cCentroContable").val('<%=cCentroContable%>');
		$("#h_cUnidadResponsable").val('<%=UR%>');
		$("#autoriza").hide(); 
		$("#cFolioMASCP").hide();
		$("#spanFolioMASCP").hide();
        var d = new Date();
	    var month = d.getMonth()+1;
	    $("#h_Mes").val(String(month));
	
		$("#h_nTipoCambio").val($('#TipoCambio').val());
		$("#tabs").tabs();
		var tipoSolicitud="<%=tipoSolicitud%>";
    	if(tipoSolicitud=="Especial"){
			var Fecha_ini =  '01/' + 01 + '/' + <%=añoIni%>;
		}else{
			//Fecha_ini ="+1M + 0Y+";
			var defaultDate = new Date();
			var  mes = defaultDate.getMonth() + 2;
			if(mes>12){
				Fecha_ini= '01/12/' + <%=añoIni%>; 
			}else{
				Fecha_ini= '01/' + mes + '/' + <%=añoIni%>; 
			}
			
		}
		var Fecha_fin =  '31/' + 12 + '/' + <%=añoIni%>; 
		
		if($("#nmod").val() =="0"|| $("#nmod").val() ==""){
			$(function() {
			$("#fInicio").datepicker( {
					 minDate: Fecha_ini, maxDate: Fecha_fin,  changeMonth: true, changeYear: false, showOn : "button",
					dateFormat : "dd/mm/yy",
					buttonImage : "images/calendar.gif",
					buttonImageOnly : true
					});
			});
		}
		
		
		var Fecha_i = '01/' + 01 + '/' + <%=añoFin%>;  
		var Fecha_f =  '31/' + 12 + '/' + <%=añoFin%>;  
		
		$(function() {
		$("#fFin").datepicker( {
				 minDate: Fecha_i, maxDate: Fecha_f,  changeMonth: true, changeYear: false, showOn : "button",
				dateFormat : "dd/mm/yy",
				buttonImage : "images/calendar.gif",
				buttonImageOnly : true
				});
		});
		
		oTablePres =$('#dt_clavepresup').dataTable({
				 "sScrollX" : "80%",
			  "sScrollY" : "50%",
			  "bPaginate" : false,
				"bScrollCollapse" : true,
				"paging":         false,
				"bFilter": false,
	     		"bSort": false,
	     		"bInfo": false
		});
		
			
		oTableCalendario=	$('#dt_Calendario').dataTable({
			  "sScrollX" : "80%",
			  "sScrollY" : "300px",
			  "bPaginate" : false,
		      "bScrollCollapse" : true,
			  "paging":         false
		});
		
	
       oTableCalendario.fnAdjustColumnSizing();
			
	oTable	=$("#dt_Montos").dataTable({
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			 sScrollX: "100%",
			"bAutoWidth" : true,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : false,
			"sScrollY" : "300",
			"fixedHeader": true
		});
		oTable = $('#dt_Montos').dataTable();
		oTable.fnAdjustColumnSizing();
			
		var nModifica = <%=nmod%>;
		
		if(nModifica != "" && nModifica != null){
			$("#nmod").val(nModifica);
			nModificacion = 0;
			$("#titulo").html("<label>Solicitud de Contratos Plurianuales y Especiales-Modificación No."+nModifica+"</label>");
			
		}else{
			$("#nModificacion").val("0");
			nModificacion = "0";
		}
		
		
	}); //Finaliza llamado de pagina

	function onPostDisplay(idOper) 
	{
		parent.document.getElementById("pb_send").disabled = false;
		
		return true;

	}

	function onPostSubmit(idOper)
	 {
	  var Continua = true;
	 	if(($("#checkbox").is(':checked')))
	 	{
		     
		         // Validando documentacion  
		         //DESCOMENTAR ANTES DE COMITEAR
				 var ValidaDoctos =  fn_CuentaDoctos(idOper) ;
			           	if (ValidaDoctos.length > 0 ){
			          		alert("Favor de Adjuntar la siguiente Información. \n" + ValidaDoctos);
			          		parent.document.getElementById("pb_send").disabled=true;
			                return false;
			         }
			     
			        
			     if (idOper == 4){
			          
			           var r=confirm("¿ Esta seguro que desea Autorizar (Libera el apartado)?");
 				       if (r==true){
 				            
 				          
 				           	alert("Inicia Proceso de liberación de Apartado, este tardará, espere a que Avance el Caso en automático........");
				         	$.blockUI( {
								message : "Avanzando    espere ......"
							});        
						    if ($("#cDocumentoHaplicado").val() == 'S'){
							         //Esta funcion liberando apartado	
							         						     
							       fn_CancelaApartado();
							        parent.document.getElementById("pb_send").disabled = true;
							        $("#cAutorizado").val("S");
							        queryFormPost({ queryName:"updatePlurianualEncabezado",async:false, callback:function(){}   }); 
							       
							    }
					       
	                              
		         }	
		         else{
		            return false;
		         }         
	            }
			         
		}	         
			         
			         
        
	 	parent.document.getElementById("pb_send").disabled=true;
		parent.document.getElementById("pb_save").disabled=true;
		parent.document.getElementById("pb_cancel").disabled=true;
		parent.document.getElementById("pb_leave").disabled=true;
		
		if(($("#checkbox").is(':checked')))	{
	 	$.blockUI( {
						message : "Avanzando    espere ......"
					});
		setTimeout($.unblockUI, 3000);			
		}
		else{
		$.blockUI( {
						message : "Regresando Espere ......"
					});
		setTimeout($.unblockUI, 3000);			
		
		
		
		}
		
					
		return true;
	}

	function onLoadPlantilla(id_oper) 
	{
		var bCarga = true;
		$("#h_nOperacion").val(id_oper);
		
		switch(id_oper)
			{
			case 1: //capturista
				  $("#autoriza").hide();
				  document.getElementById("plantilla").disabled=false;
				    $("#flsetCargaArchivo").show();
				    $("#cOperacion").val("Captura");
				    break;
			case 2: //reviza
			        document.getElementById("fileUploadButton").disabled=true;
				  document.getElementById("fileUpload").disabled=true;
				
			      $("#autoriza").show();
			      fn_Deshabilita();
			     $("#flsetCargaArchivo").hide();
			      $("#cOperacion").val("Revisor");
			      break;
			case 3: //Revisor Normativo
			      $("#autoriza").show();
			      fn_Deshabilita();
			      $("#flsetCargaArchivo").hide();
			      document.getElementById("fileUploadButton").disabled=true;
				  document.getElementById("fileUpload").disabled=true;
				  document.getElementById("Mostrar").disabled=true;
				  document.getElementById("plantilla").disabled=true;
				  $("#Select_Estatus").attr("disabled",true);
				   $("#cOperacion").val("Revisor Normativo");
				  break;
			 	case 4: //autorizador central
			      $("#autoriza").show();
			      fn_Deshabilita();
			      $("#spanFolioMASCP").show();
			      $("#cFolioMASCP").show();
			      $("#flsetCargaArchivo").hide();
			      document.getElementById("fileUploadButton").disabled=true;
				  document.getElementById("fileUpload").disabled=true;
				  document.getElementById("plantilla").disabled=true;
				  $("#Select_Estatus").attr("disabled",true);
				   $("#cOperacion").val("Autorizador Central");
			 	break;
	
			case 5:
			      fn_Deshabilita();
			      $("#cFolioMASCP").attr("disabled",true);
			      $("#autoriza").hide();
				  $("#motivoRechazo").hide();	
				  $("#spanFolioMASCP").show();	      
			      $("#cFolioMASCP").show();
			      $("#flsetCargaArchivo").hide();
			      $("#cOperacion").val("Consulta");
		    	  break;
			default:
			      $("#autoriza").hide();
				  break;      
			}
			
			
		  queryFormPost("folioPlurianualExistente", {
			async : false
		  });
			
		
	
	

		querySelectPost("Catalogo_PluriAnuales_tipoGasto", "select_TipoGasto",
				{
					async : false
				});
				
	     			
				
				
		querySelectPost("catalogo_PluriAnuales_TipoSolicitud",
				"Select_Solicitud", {
					async : false
				});
				
		
		querySelectPost("catalogo_PluriAnuales_TipoContrato","Select_TipoContrato", {async : false	});
		
		querySelectPost("catalogo_PluriAnuales_TipoMoneda",
				"Select_TipoMoneda", {
					async : false
				});
		querySelectPost("catalogo_Estatus",
				"Select_Estatus", {
					async : false
				});		

	    verRechazo();
	    
	    
	    queryFormPost("ConsultaEncabezadoPlurianual", {
				async : false
			});
		
				//En esta funcion cargamos las ep´s cargadas con el excel
 		
			if  ($("#cDocumentoHaplicado").val() == 'S'){
			     $("#TipoMovimiento").val("EN APARTADO");
			     document.getElementById("fileUploadButton").disabled=true;
				  document.getElementById("fileUpload").disabled=true;
				  document.getElementById("Mostrar").disabled=true;
				  if (id_oper != 5) parent.document.getElementById("pb_cancel").disabled=true;
				 }else if  ($("#cDocumentoHaplicado").val() == 'C'){
			
					$("#TipoMovimiento").val("LIBERADO").val();
			     
					if (id_oper != 5) parent.document.getElementById("pb_cancel").disabled=true;
				}else if ($("#cDocumentoHaplicado").val() == 'N'){
					 $("#TipoMovimiento").val("");
				}
				
			else{
			
				if (id_oper != 5){
					var nModifica = <%=nmod%>;
					if(nModifica != "" && nModifica != null){
						parent.document.getElementById("pb_cancel").disabled=true;
					}else{
						parent.document.getElementById("pb_cancel").disabled=false;
					}
				} 
			} 
			 
			 var sMensajeError = $("#MSG").val();
			 
			 if (typeof(sMensajeError) != "undefined" ){
			      if (sMensajeError != "ok"){
			         bCarga = false;
			         alert ("Validación." + $("#MSG").val());
			         $("#MSG").val();
			         fn_Deshabilita();
			         document.getElementById("fileUploadButton").disabled=false;
				  	 document.getElementById("fileUpload").disabled=false;
				  	 document.getElementById("Mostrar").disabled=false;
				  	 document.getElementById("plantilla").disabled=false;
			         }
			 }     
			 
			    
			 			 
			 if (bCarga){  
			     queryFormPost("TotalRegistrosImportados", {async : false });
				 querySelectPost("SelectAniosPlurianuales", "Select_Anio", {async : false	}); 
				 
			    BloqueaGrid();
			    
				if ($("#h_Existe").val() == 1) 
						{
			          		$("#h_nTipoCambio").val($('#TipoCambio').val());
							if ($("#h_lAbierto").val() == 'A') {
								document.getElementById("lCerrado").checked = false;
								document.getElementById("lAbierto").checked = true;
							} else {
								document.getElementById("lCerrado").checked = true;
								document.getElementById("lAbierto").checked = false;
							}
							CargarDetalle();
						}
		 				else 
						{   
					    //Primer vez
					      var mensajeC= "Cabe señalar, que la contratación bajo esta modalidad, no afectará negativamente la competencia económica del Sector Medio Ambiente y Recursos Naturales, toda vez que el contrato a realizar se llevará a cabo a través de una ('licitación pública' o 'Invitación a cuando menos tres personas') a fin de asegurar al Gobierno Federal las mejores condiciones disponibles en cuanto a precio, calidad,financiamiento, oportunidad y demás circunstancias pertinentes."; 
					         $("#cEtiqueta_c").val(mensajeC);
					    
					       var mensajeE= "(nombre del área solicitante) cuenta con los recursos económicos necesarios para solventar la contratación plurianual en el ejercicio fiscal "+ <%=cEjercicio%> +" y se compromete a realizar las previsiones de los recursos en los subsecuentes ejercicios fiscales, los cuales estarán sujetos para su ejercicio y pago al presupuesto que apruebe la H. Cámara de Diputados. Así mismo, es responsabilidad de la (nombre del área solicitante) que el ejercicio de los recursos correspondientes a la(s) partida(s) XXXXX de gasto, no contraviene las medidas de ahorro, austeridad y eficiencia,   con el fin de incrementar la productividad de la Administración Pública Federal, de conformidad con lo establecido en el artículo 16 del Decreto de Presupuesto de Egresos de la Federación para el Ejercicio Fiscal"+ <%=cEjercicio%> +", así como la responsabilidad de dar cumplimiento al Decreto y lineamientos para la aplicación y seguimiento de las medidas para el uso eficiente, transparente y eficaz de los recursos públicos y las acciones  de disciplina presupuestaria en el ejercicio del gasto público, así como, la modernización de la Administración Pública Federal, publicados en el Diario Oficial de la Federación el 10 de diciembre de 2012 y el 30 de enero de 2013, respectivamente." ;
					    $("#cEtiqueta_e").val(mensajeE);
				
					     
						$('#TipoCambio').val(1);
						$("#h_lAbierto").val("C");
						document.getElementById("lCerrado").checked = true;
						document.getElementById("lAbierto").checked = false;
						$("#Select_Solicitud option[value='PLU']").attr("selected", true);
						$("#Select_TipoMoneda option[value='MxN']").attr("selected", true);
			
						}
		
						if($("#Monto_Total").val()!=null && $("#Monto_Total").val()!="")
						{
						$("#Monto_Total").formatCurrency();
						fn_ObtenMonto();
			
					}
		
		         } // fin de la bandera bCarga;
		if ($("#desbloqueado_bEspecial").val() != $("#bEspecial").val() && $("#desbloqueado_bEspecial").val() != -1){
			alert("No podra avanzar el caso debido a que se encuentra cerrado el flujo para este tipo de contratos plurianuales.");
			fn_Deshabilita();
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_save").disabled=true;
		}
		
		if(id_oper == 1){
			var zTabla = "VALIDA_ES_MODIFICACION_PLURIANUAL";
			var camposWhere = " WHERE nFolioContratoPlurianual = " +$("#nFolioPurianual").val()+" " ;
			var param = "";
			var sOrder = " ";
			
																							
			$.getJSON("../catalogos/SelectJson.jsp?"+ new Date().getTime(),{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){ 
											
				var Modif;
				Modif = parseFloat(j[0].Col0);
	   			
	   			if(Modif != 0){
	   			  $("#TipoCambio").attr("disabled",true);
				  $("#fInicio").attr("disabled",true);
				  $("#Monto_Total").attr("disabled",true);
				  $("#Monto_Total_Pesos").attr("disabled",true);
				  $("#Monto_Total_Contrato").attr("disabled",true);
				  $("#tTotal").attr("disabled",true);
				  $("#ImporteComparartivoAnual").attr("disabled",true);
				  $("#Select_Estatus").attr("disabled",true);
				  $("#select_TipoGasto").attr("disabled",true);
				  $("#Select_TipoContrato").attr("disabled",true);
				  $("#Select_TipoMoneda").attr("disabled",true);
				  $("#Select_Solicitud").attr("disabled",true);
				  $("#Mostrar").attr("disabled",false);
 				  $("#lAbierto").attr("disabled",true);
				  $("#lCerrado").attr("disabled",true);
				  $("#flsetCargaArchivo").show(); 
				  $("#cDescripcion_Corta" ).addClass("notEditable2");
				  $("#cDescripcion_Corta").prop('readonly', true);
 				  $("#fileUpload").attr("disabled",false);
				  $("#fileUploadButton").attr("disabled",false);
				  $("#fFin").attr("disabled",false);
	   			}
	   			
	        }); 
	  	 }
			$("#lAbierto").prop( "disabled", true );
			$("#lCerrado").prop( "disabled", true );
			var tipoPlurianual="<%=tipoPlurianual%>";
			if(tipoPlurianual!="" && tipoPlurianual!="null" ){
				
				if(tipoPlurianual=="Abierto"){
	      			$("#lAbierto").prop( "checked" , true ); 
	      		 	$("#h_lAbierto").val("A");  
	      		 	$("#valContra").val("<%=valContra%>");
	      		 	
				}else{
	      			$("#lCerrado").prop( "checked" , true ); 
					$("#h_lAbierto").val("C");  
				}
			}
			var tipoSolicitud="<%=tipoSolicitud%>";
			$("#bEspecial").prop( "disabled", true );
			if(tipoSolicitud!="" && tipoSolicitud!="null"){
				
				if(tipoSolicitud=="Plurianualidad"){
				 	$("#Select_Solicitud").val("PLU");
				 	$("#bEspecial").val("0");
				}else{	
					$("#Select_Solicitud").val("ESP");
				 	$("#bEspecial").val("1");
				}
				
		   	}
		var nModifica = <%=nmod%>;
		if(nModifica != "" && nModifica != null){
			$("#nmod").val(nModifica);
			nModificacion = 0;
			$("#titulo").html("<label>Solicitud de Contratos Plurianuales y Especiales-Modificación No."+nModifica+"</label>");
			
		}else{
			$("#nModificacion").val("0");
			nModificacion = "0";
		}

		$("#tTipo").val($("#cClasificacionGasto").val());

	}
		
	
	function onSubmit(id_oper)
	{
	
		var correcto = true;
	
	   if (fn_Guardar(id_oper) != 1)
	    return;
	    
	     
	       if(($("#checkbox").is(':checked'))) //ojo sucede si no es rechazado
	 	    {
		   
			      if (id_oper == 2){
			            /*  verifica proceso de apartado   */
			              if ($("#cDocumentoHaplicado").val() != 'S' &&   $("#cDocumentoHaplicado").val() != 'C'){
				            //Esta funcion guarda y efectua el apartado
				          		var comboAnio = $("#Select_Anio").val();
				          		var arrDataEp =oTableCalendario.fnGetData();
				             	
				           if ( arrDataEp.length>0 ) {
				           
				           		$.blockUI({ message : "Generando Apartado Espere ......" });
				           		var bRealizado;
				           		
				           		if($("#bEspecial").val() == 0){
				           			bRealizado = fn_GuardaMatriz();
				              	} else {
				              		bRealizado = true;
				              	}
				              
				           		if (bRealizado){
				           			var iRegistrosTotales  = $("#h_DatosImportados").val();
				          			setTimeout($.unblockUI, 60 * iRegistrosTotales  ); 
				             		parent.document.getElementById("pb_cancel").disabled=true;
				                    queryFormPost("ConsultaEncabezadoPlurianual", {async : false	});
				                    if($("#cDocumentoHaplicado").val() == 'S'){
			    						$("#TipoMovimiento").val("EN APARTADO");
			   							document.getElementById("fileUploadButton").disabled=true;
										document.getElementById("fileUpload").disabled=true;
										parent.document.getElementById("pb_send").disabled = false;	
								    }
				             	}else{
				                         setTimeout($.unblockUI, 200 ); 
			   							correcto = false;
			                 
				              	} 
				            }  
				     	 
			             }//  Fin de $("#cDocumentoHaplicado").val() != 'S' &&   $("#cDocumentoHaplicado").val() != 'C'
			      
			      }//si id_oper= 2
		           
		}    
		
		//Realizar validaciones. Si regresa TRUE continua el proceso en caso contrario no guarda.
		var p = window.parent;
		p.gestion.setFolio('<%=c.getFolio()%>');
		p.gestion.setOperador('<%=usuario.getNombre()%>');
		p.gestion.setFechaDocumento('<%=today%>'); 
		p.gestion.setEjercicioFiscal('<%=cEjercicio%>');
		p.gestion.setMoneda($("#h_ctipoMoneda").val());
		return correcto;
		
	}
	
	
	function verRechazo()
	{
	
	 var num = parseInt($("#h_nOperacion").val());
	 var str  =$("#cMotivoRechazo").val();
 	switch(num)
			{
			case 1: //capturista
				  $("#autoriza").hide();
				  if ( str.length == 0)  $("#motivoRechazo").hide();
				break;		   
			case 2: //reviza
			      if ( str.length == 0) $("#motivoRechazo").hide();
			      else
			      $("#autoriza").show();
			  break;
			case 3: //Revisor Normativo
			   if ( str.length == 0) $("#motivoRechazo").hide();
				   else	
			      $("#autoriza").show();
			 break;
			 
			 case 4: //autorizador central
				   if ( str.length == 0) $("#motivoRechazo").hide();
				   else	
			      $("#autoriza").show();
			 break;
			default:
			      $("#autoriza").hide();
			break;      
			}
		
	
	
	}
	
	
	
	
	function ResponsableSiguiente(id_oper) 
	{
			if( id_oper == 1 )
			{
				return "REVISOR_PLURIANUALPRE";
			}
			else if ( id_oper == 2)
			{
				if( $("#checkbox").is(':checked') ) //y no es rechazado
				{ 
					return "AUTORIZADOR_PLURIANUALPRE";
				}
				else
				{
					return "CAPTURISTA_PLURIANUALPRE";
				}
			}
			else if ( id_oper == 3)
			{
				if( $("#checkbox").is(':checked') )
				{
					return "AUTORIZADOR_PLU_CENTRAL";
				}
				else
				{
					return "REVISOR_PLURIANUALPRE";
				}
			}
			
			else if ( id_oper == 4)
			{
				if( $("#checkbox").is(':checked') )
				{
					return "CONSULTA_PLURIANUALPRE";
				}
				else
				{
					return "AUTORIZADOR_PLURIANUALPRE";
				}
			}
	
	}

	function OperacionSiguiente(id_oper) 
	{
		if( id_oper == 1 )
			{
				return "revisa_plurianualpre";
			}
			else if ( id_oper == 2)
			{
				
				if($("#checkbox").is(':checked')) 
				{
					return "autoriza_plurianualpre";
				}
				else
				{
					return "captura_plurianualpre";
				}
			}
			else if ( id_oper == 3)
			{
				if($("#checkbox").is(':checked'))
				{
					return "autorizador_central";
				}
				else
				{
					return "revisa_plurianualpre";
				}
			}
			else if ( id_oper == 4)
			{
				if($("#checkbox").is(':checked'))
				{
					return "consulta_plurianualpre";
				}
				else
				{
					return "autoriza_plurianualpre";
				}
			}
	}
	
	

	
function fn_Comparar_Fechas(inicio, fin)
{  
		var xMonth=inicio.substring(3, 5);  
		var xDay=inicio.substring(0, 2);  
		var xYear=inicio.substring(6,10);  
		var yMonth=fin.substring(3, 5);  
		var yDay=fin.substring(0, 2);  
		var yYear=fin.substring(6,10);  
        if (xYear> yYear) 
          	return true;  
        else
        {  
	        if (xYear == yYear)
	        {   
				if (xMonth> yMonth) 
	            	return true; 
	            else{   
	              	if (xMonth == yMonth)
	              	{  
		                if (xDay> yDay) 
		                	return true;  
		                else  
		                	return false;  
	           		}  
	           		else  
	           			return false; 
	            }  
			} 
	        else  
	         return  false;  
        }
 }
    
    function fn_ValidaFechas(){
   		$("#h_FechaI").val($("#fInicio").val());
		$("#h_FechaF").val($("#fFin").val());
		
	      
	     if ( $("#h_FechaI").val() == null ||  $("#h_FechaI").val() == "" ){
	        alert("Indique la fecha de Inicio");
	         return false;
	     }
	      
	     if ($("#h_FechaF").val() == null || $("#h_FechaF").val() == "" )
	     {
	         alert("Indique la fecha Fin");
	         return false;
	     }   
	     
	     var yYear  = $("#h_FechaI").val();
	     var yYear  = yYear.substring(6,10);   
	     
	     if (fn_Comparar_Fechas($("#h_FechaI").val(), $("#h_FechaF").val() ) )
	      {  
			   alert("La Fecha Inicio debe  ser menor al al fecha Fin");  
			   $("#fInicio").val("");
			    $("#fFin").val("");
			   return false;
		  }   
	          
	    return true;
		
	}
	
	function fn_Valida(id_oper)
		{
			 var aClaves = $('#dt_clavepresup').dataTable().fnGetNodes();
		    
		   	fn_ObtenMonto();  
			//primero validar campos de fechas
			if (fn_ValidaFechas() != true) return;
			
			
             var aMontos = $('#dt_Montos').dataTable().fnGetNodes();
             var aCalendario = $('#dt_Calendario').dataTable().fnGetNodes();
  
  		     
	  		$("#h_folio").val('<%=c.getFolio()%>');
			if ($("#h_folio").val() == null || $("#h_folio").val() == "" ){
	         	alert("No se genero el folio");
	         	return -1 ;
	        }
	        
	       	if(id_oper==4) {
	        	$("#h_cFolioMASCP").val($("#cFolioMASCP").val()); 
           		if ($("#h_cFolioMASCP").val() == null || $("#h_cFolioMASCP").val() == "" ){
	         		alert("Indique el folio MASCP");
	         		return -1 ;
	         	}
	       	}  
	         
	        
	        if($("#h_bEspecial").val() != $("#desbloqueado_bEspecial").val() && $("#desbloqueado_bEspecial").val() != "-1"){
	        	var tipopluri;
	        	if($("#h_bEspecial").val() == 0)
	        		tipopluri = "normales";
	        	else
	        		tipopluri = "especiales";
	        	alert("Los pluriauales "+tipopluri+" se encuentra bloqueados por el administrador.");
	         	return -1;
	        
	        }
	         		
	        
	        
	        $("#h_Select_Estatus").val($("#Select_Estatus").val());
	             
	   		if (document.getElementById("lAbierto").checked)
		       $("#h_lAbierto").val($("#lAbierto").val());
		    else
		       $("#h_lAbierto").val($("#lCerrado").val());  
		  
		
				
			$("#h_cTipoSolicitud").val($("#Select_Solicitud").val());
			if ($("#h_cTipoSolicitud").val() == 0 || $("#h_cTipoSolicitud").val() == "" )
	         {
	         	alert("Seleccione el tipo de solicitud");
	         	return -1;
	         } 
			
		    $("#h_select_TipoGasto").val($("#select_TipoGasto").val());
			if ($("#h_select_TipoGasto").val() == 0 || $("#h_select_TipoGasto").val() == "" )
			 {
	         	alert("Seleccione el tipo de Gasto");
	         	return -1;
	         } 
			
			$("#h_Select_TipoContrato").val($("#Select_TipoContrato").val());
		    if ($("#h_Select_TipoContrato").val() == 0 || $("#h_Select_TipoContrato").val() == "" )
			{
	         	alert("Especifique el tipo de Contrato");
	         	return -1 ; 
		     }
		    
		    //Validaciones de las pestañas
		    $("#h_cDescripcion").val($("#cDescripcion").val());		
			$("#h_cJustificacionCompromiso").val($("#cJustificacion_compromiso").val());	
			$("#h_cJustificacionPlazo").val($("#cJustificacion_plazo").val());		
			$("#h_cFundamento").val($("#cFundamento").val());	
			
			 	if($("#h_cFundamento").val()==null || $("#h_cFundamento").val()==""){
				alert('Indique el fundamento del contrato en la pestaña correspondiente');
				return -1 ;
			}
			 	
			if($("#h_cDescripcion").val()==null || $("#h_cDescripcion").val()==""){
				alert('Indique la Especificación del Contrato en la pestaña correspondiente');
				return -1 ;
			}
			
			if($("#cDescripcion_Corta").val()==null || $("#cDescripcion_Corta").val()==""){
				alert('•	Indique el nombre del Contrato  la pestaña  de Especificación  correspondiente');
				return -1 ;
			}
				
			
			if($("#h_cJustificacionCompromiso").val()==null || $("#h_cJustificacionCompromiso").val()==""){
				alert('Indique la Justificación Ventajas  Económicas en la pestaña correspondiente');
				return -1 ;
			}
			
			if($("#h_cJustificacionPlazo").val()==null || $("#h_cJustificacionPlazo").val()==""){
				alert('Justifique el plazo del contrato en la pestaña correspondiente');
				return -1 ;
			}
			
			if($("#h_ImporteComparartivoAnual").val()==null || $("#h_ImporteComparartivoAnual").val()==""){
				alert('Indique el valor comparativo  Anual en la pestaña de Justificación Ventajas Económicas');
				return -1 ;
			}
            

			if (aMontos.length == 0 ){
		      alert('Genere los registros en la Pestaña de Importes Anuales  ');
		      return -1;
		     }
	        
		     
			var aTrs = $('#dt_Montos').dataTable().fnGetNodes();
			var nRenglones = (aTrs.length)-1;// Totales nop
		    var  tAvance =  parseFloat ($("#h_nAvanceTotal").val());
			 
			if ($("#h_nAvanceTotal").val() != 100.00){

	         	  	alert("La suma del Avance debe ser 100%");
		         	return -1;
			 } 
	    	         
       			
			$("#h_fAplicacion").val('<%=today%>'); 
     		$("#h_aEjercicioFiscal").val($('#cEjercicio').val()); 
     		
     		
		   if ($("#h_aEjercicioFiscal").val() == null || $("#h_aEjercicioFiscal").val() == "" )
		   {
	        	alert("Indique el Año del Contrato"); 
	         	return -1;
		   }
		   	
		   $("#h_ctipoMoneda").val($("#Select_TipoMoneda").val());
		   if ($("#h_ctipoMoneda").val() == '0' || $("#h_ctipoMoneda").val() == "" )
		   {
	         	alert("Indique el Tipo de Moneda");
	         	return -1;
		   }
		   
		   $("#h_nTipoCambio").val($('#TipoCambio').val());	
	       if ($("#h_nTipoCambio").val() == 0 || $("#h_nTipoCambio").val() == null || $("#h_nTipoCambio").val() == "" )
	       {
	         	alert("Indique el Tipo de Cambio");
	         	return -1 ;
	       }
	       if ($("#h_nMontoTotal").val() ==0 || $("#h_nMontoTotal").val() == null){
	   	    	alert("Indique EL Monto Total");
	        	return -1;
	       }
	     
	    	if ($("#h_nMontoTotal_Pesos").val() ==0 || $("#h_nMontoTotal_Pesos").val() == null){
	   	    	alert("El campo monto total en pesos no se calculo");
	         	return -1 ;
	     	}
	     
	      
	       if($.trim($("#h_Monto_Total_Contrato").val())=="" || $("#h_Monto_Total_Contrato").val() == null){
	     	alert("Indique el monto total por Contrato");  
			return false;
		    }      
	   
	        
	      		
		 // SI ES UN CONTRATO CERRADO VALIDAR CAMPOS DE MONTO TOTAL Y SI ES CONTRATO ABIERTO VALIDAR TOTALES MAXIMOS Y MINIMOS
// 		if  ($("#h_lAbierto").val() == 'C') 
// 		 {
		     if ($("#h_nMontoTotal").val() == null || $("#h_nMontoTotal").val() == 0 )
				 {
		         	alert("No se Calculo el monto Total");
		         	return -1;
			      }
			        
			      
		     if (parseFloat($("#h_Monto_Total_Contrato").val())  != parseFloat($("#h_nMontoTotalCalculado").val()))
				 {
				 	if($("#h_Select_TipoContrato").val() == "SRV"){
						if (parseFloat($("#h_Monto_Total_Contrato").val()) * 1.20 < parseFloat($("#h_nMontoTotalCalculado").val())){
							alert("El monto total para un contrato de servicios no puede superar el 120%, revise sus importes anuales.");
							return -1;
						}
					}else {
						if($("#h_Select_TipoContrato").val() == "OBR"){
							if (parseFloat($("#h_Monto_Total_Contrato").val())*1.25 < parseFloat($("#h_nMontoTotalCalculado").val())){
								alert("El monto total para un contrato de obra no puede superar el 125%, revise sus importes anuales.");
								return -1;
							}
						}else {
				            alert("El Monto Total por Contrato debe ser igual al campo(Totales) de la tabla Importes Anuales");
			         		return -1;
		         		}
		         	}
			     }    

		  //FIN  DE CONTRATO ABIERTO/CERRADO	
		    
		    $("#h_cUsuarioCaptura").val('<%=usuario.getLogin()%>');	
		if ($("#h_cUsuarioCaptura").val() == null || $("#h_cUsuarioCaptura").val() == "" )
		{
	         	alert("Se Desconoce el usuario de Captura");
	         	return -1;
		}
		
		if(id_oper==2 || id_oper==3 || id_oper==4)
			{
				if(!($("#checkbox").is(':checked')))
				{
					if($("#cMotivoRechazo").val()==null || $("#cMotivoRechazo").val()=="")
					{
						alert("Debe ingresar el motivo del rechazo");
						return -1;
					}
				}
			}
					
			//Asignamos valores a etiqueta
			 $("#cEtiqueta_a").val("Para la plurianualidad se tiene programado la aplicación de recursos correspondientes a " + $.trim($("#tTipo").val()));
			
	         
	       return 1;
	  
		} 
		
		
		
		
		
		function fn_CargaHidden()
		{
		        if (document.getElementById("lAbierto").checked)
		       		$("#h_lAbierto").val($("#lAbierto").val());
		      	else
		        	$("#h_lAbierto").val($("#lCerrado").val());
		          
		        $("#h_folio").val('<%=c.getFolio()%>');
		    	$("#h_cTipoSolicitud").val($("#Select_Solicitud").val());
			   	$("#h_select_TipoGasto").val($("#select_TipoGasto").val());
				$("#h_Select_TipoContrato").val($("#Select_TipoContrato").val());
				$("#h_Select_Estatus").val($("#Select_Estatus").val());
				$("#h_fAplicacion").val('<%=today%>'); 
				$("#h_aEjercicioFiscal").val($('#cEjercicio').val()); 
	           	$("#h_ctipoMoneda").val($("#Select_TipoMoneda").val());
		        $("#h_nTipoCambio").val($('#TipoCambio').val());	
	     		$("#h_cUsuarioCaptura").val('<%=usuario.getLogin()%>');	
				// falta validar Descripciones		
				$("#h_cDescripcion").val($("#cDescripcion").val());		
				$("#h_cJustificacionCompromiso").val($("#cJustificacion_compromiso").val());	
				$("#h_cJustificacionPlazo").val($("#cJustificacion_plazo").val());		
				$("#h_cFundamento").val($("#cFundamento").val());	
		} 
		
		
		
		
		function fn_RecargaObjetos()
		{
		        if ($("#h_lAbierto").val() =='A')
		       		("#lAbierto").val($("#h_lAbierto").val());
		      	else
		        	$("#lCerrado").val($("#h_lAbierto").val());
		          
		        $("#folio").val($("#h_folio").val());
		    	$("#Select_Solicitud").val($("#h_cTipoSolicitud"));
			   	$("#select_TipoGasto").val($("#h_select_TipoGasto").val());
				$("#Select_TipoContrato").val($("#h_Select_TipoContrato").val());
     			$("#h_fAplicacion").val('<%=today%>'); 
				$("#h_aEjercicioFiscal").val($('#cEjercicio').val()); 
	           	$("#Select_TipoMoneda").val($("#h_ctipoMoneda").val());
		        $("#TipoCambio").val($('#h_nTipoCambio').val());	
	     		$("#h_cUsuarioCaptura").val('<%=usuario.getLogin()%>');	
				// falta validar Descripciones		
				$("#h_cDescripcion").val($("#h_cDescripcion").val());		
				$("#cJustificacionCompromiso").val($("#h_cJustificacion_compromiso").val());	
				$("#cJustificacionPlazo").val($("#h_cJustificacion_plazo").val());		
				$("#cFundamento").val($("#h_cFundamento").val());	
		} 
		
	
		function fn_Deshabilita()
		{
		          document.getElementById("fileUploadButton").disabled=true;
				  document.getElementById("fileUpload").disabled=true;
				  document.getElementById("Mostrar").disabled=true;
				  document.getElementById("plantilla").disabled=true;
				  $("#TipoCambio").attr("disabled",true);
				  $("#fInicio").attr("disabled",true);
				  $("#fFin").attr("disabled",true);
				  $("#Monto_Total").attr("disabled",true);
				  $("#Monto_Total_Pesos").attr("disabled",true);
				  $("#Monto_Total_Contrato").attr("disabled",true);
				  $("#Monto_Total").attr("disabled",true);
				  $("#cDescripcion").attr("disabled",true);
				  $("#cDescripcion_Corta" ).addClass("notEditable2");
				  $("#cDescripcion_Corta").prop('readonly', true);
				  $("#cFundamento").attr("disabled",true);
			      $("#cJustificacion_compromiso").attr("disabled",true);
				  $("#cJustificacion_plazo").attr("disabled",true);
				  $("#tTotal").attr("disabled",true);
				  $("#ImporteComparartivoAnual").attr("disabled",true);
				  $("#Select_Estatus").attr("disabled",true);
				  $("#select_TipoGasto").attr("disabled",true);
				  $("#Select_TipoContrato").attr("disabled",true);
				  $("#Select_TipoMoneda").attr("disabled",true);
				  $("#Select_Solicitud").attr("disabled",true);
				  $('#fInicio').datepicker('disable');
 				  $('#fFin').datepicker('disable');
				  $("#lAbierto").attr("disabled",true);
				  $("#lCerrado").attr("disabled",true);
				  $("#bEspecial").attr("disabled",true);
				    
		}  
		
		
		
		
		function fn_InsertaEncabezado()
		{
		
		    var bInserto =false;

			queryFormPost({
		                     queryName:"deleteContratoPlurianualEncabezado",async:false,
		                     callback:function(){bInserto = true;}
	                        });
			queryFormPost({
		                     queryName:"InsertaPlurianualEncabezado",async:false,
		                     callback:function(){bInserto = true;}
	                        });
			                        
			if (bInserto == true) return 1;
		    	else  return -1;
					   
					   
		}
		
		
		
		function fn_UpdateEncabezado()
		{
		
		   
		    var bCambios =false;
		    
		    if(($("#checkbox").is(':checked')))
	 	    {
	 	        $("#cMotivoRechazo").val("");
		    }

					   queryFormPost({
				                     queryName:"updatePlurianualEncabezado",async:false,
				                     callback:function(){bCambios = true;}
			                        });
			                      
				if(bCambios == true)
				{
			       return 1;
				} 
		}
		
		
function fn_Guardar(id_oper)
{
		  var bExito ;
		  var bExitoEncabezado;
		  var bExitoDetalle;
		 
		  
		  bExito = fn_Valida(id_oper);
		    
		  if ($("#h_Existe").val() == 1) //update
		  { 
		               if (bExito == 1)
		               {
				           		bExitoEncabezado =fn_UpdateEncabezado();
				           		if      (bExitoEncabezado == 1) 
				           		         bExitoDetalle =fn_GuardaDetalle();
					  	        else if (bExitoEncabezado == 0) 
					  	             return 0;
					  	        else return -1;
			  	   		             
			  	        } 		
		            else   return -1;	 
		  }   
		 else  //Contrato nuevo
		  {
		       if  (bExito == 1) 
		            bExitoEncabezado =fn_InsertaEncabezado();
		       else  return -1;
			             
			   if  (bExitoEncabezado == 1)  
			        bExitoDetalle =fn_GuardaDetalle();
			        
			   else if (bExitoEncabezado == 0 )
			        return 0;  
			  	else  {
			  	    alert('Error en fn_Guardar().fn_InsertaEncabezado()');
			  	       return -1;
			  	 }
			}  	     	       
				 
			if    (bExitoEncabezado == 1 && bExitoDetalle == 1){
			        $("#h_Existe").val(1); 
			         alert('Guardado Exitoso');
			           
			         parent.document.getElementById("pb_send").disabled = true;
			  return 1;
			}
			else  
			{
			 alert ('Error En fn_Guardar');
			 return -1;
			 }
}


function fn_UpdateEP() {
    //Funcion que indica cuales EP´S son ocupadas para los calculos del contrato de todas las importadas por el excel
	var arrDataEp = oTableCalendario.fnGetData();
	var sumaTotal=0;	
	var revisa=false;
	var totalPesos=0;
	if(arrDataEp.length>0)
	{
		for(var i=0; i<arrDataEp.length-1; i++)
		{
			$("#h_EP").val(arrDataEp[i][1]);
			
			queryFormPost({queryName:"updatetContratoPlurianual_EP",async:false, callback:function(){revisa = true;} });
		}
		
	}
	return revisa;
}


function fn_AlmacenaMontos_Anuales() {
    //Funcion que genera un arreglo de años y montos por año  del grid importadas por el excel
	var arrDataEp = oTableCalendario.fnGetData();
	
	var Anio  ;
	var TotalAnios = 0;
	var sumaMonto = 0.00;
	var Temp2='';
	var revisa=false;
	 var Valor = 0.00;
	$("#Arreglo_Montos").val("");
	
	
	if(arrDataEp.length>0)
	{
		for(var i=0; i<arrDataEp.length-1; i++)
		{
		   //modifica
		     if($("#bEspecial").val()=="1"){
				             	
				             }
			            	
		    var Valor = $('#Campo_4_'+ i).val();
		        Valor =  parseFloat(fn_quitaFormato(Valor));
                   if (i==0){
		             Anio =  $('#Campo_0_'+ i).val();
		             TotalAnios +=1;
		           	 sumaMonto  += Valor;
		       	   }	 
		           if (i>0 )
		           { 
		             var temp =  $('#Campo_0_'+ i).val();
                        if (Anio ==  temp){
		                    sumaMonto  +=Valor;
		                  
		                 }
		                else
		                {
			              	if (Anio == '<%=cEjercicio%>'){
			              	    if ($("#h_Existe").val() == 0){
			              	    //la primera vez que se  carga el excel
			              	  		$("#h_nMontoTotal").val(parseFloat(sumaMonto),10);
				                	$("#Monto_Total").val(parseFloat(sumaMonto),10);
				              		$("#Monto_Total").formatCurrency();
				              		$("#Monto_Total_Pesos").val(parseFloat(sumaMonto),10);
				              		$("#Monto_Total_Pesos").formatCurrency();
				             	}
				             }
				            
			                Temp2 = Anio + ':' +sumaMonto + '//';
			            
			                Anio =  $('#Campo_0_'+ i).val();
			                TotalAnios +=1;
			              
			          	   	$("#Arreglo_Montos").val($("#Arreglo_Montos").val() +Temp2);
			              	
			              	var Valor = $('#Campo_4_'+ i).val();
			                sumaMonto =  parseFloat(fn_quitaFormato(Valor));
			                Temp2 = '';
		                }
		              }
		}//fin del for que barre el gri de la ep
         //Carga ultimo renglon 
               var Ultimo =  $('#Campo_0_'+ (i-1)).val();
        
		       if (Anio == Ultimo)
		           {
		              Temp = Anio + ':' +sumaMonto + '//';
		               $("#Arreglo_Montos").val($("#Arreglo_Montos").val() +Temp);
		             
		            }
		             else
		            {
		              Anio = $('#Campo_0_'+ (i-1)).val();
		              TotalAnios +=1;
		              Temp = Anio + ':' +sumaMonto + '//';
		              $("#Arreglo_Montos").val($("#Arreglo_Montos").val() +Temp);
		            }
		      
        
				       	 
		
   }
	$("#h_TotalAnios").val(TotalAnios); //Se quiere ocupar para limira la fecha fin
	
		
	return revisa;
}


		
function fn_CreaTabla() {
     
          //Falta validar que el excel contenga los años de fecha fin
         if (fn_ValidaFechas()== false) return;
         
         $('#dt_Montos').dataTable().fnClearTable();
         $('#Arreglo_Anio').val(''); 
         $("#h_ctipoMoneda").val($("#Select_TipoMoneda").val());
         $("#h_nTipoCambio").val($('#TipoCambio').val());
         $("#h_Select_TipoContrato").val($("#Select_TipoContrato").val());
         
         var str = $('#Arreglo_Montos').val();   
      
		 var Arreglo_montos= str.split("//"); 
        
         
         
         var FechaI = $("#h_FechaI").val();
      	 var FechaF = $("#h_FechaF").val();
      	 
      	  
      	 if($("#h_DatosImportados").val() == null || $("#h_DatosImportados").val() =='' || $("#h_DatosImportados").val() ==0)
		   {
			 alert("no se importo el excel o No se pudo leer");
			 return false;
		   }			 
       
      	  
	
	       if ($("#h_ctipoMoneda").val() !='MxN')
  			{
    			          if ($("#h_nTipoCambio").val() <= 0 || $("#h_nTipoCambio").val() == "1" ||  $("#h_nTipoCambio").val() == "" )
		 				{
						    $("#TipoCambio").attr("disabled",false);
						    $('#TipoCambio').val(0);
				         	alert("Indique el Tipo de Cambio");
				         	return false;
         				 }
          	}		             		
	       else
           {
	          $('#TipoCambio').val(1);
	          $("#TipoCambio").attr("disabled",true);
	          $("#h_nTipoCambio").val($('#TipoCambio').val());
           }
		   		
	      
	 	 var xYear=FechaI.substring(6,10);  
		 var yYear=FechaF.substring(6,10);
		 var i = 0;
		 var TotalAnios= 0;
		 var ImportePropuestoA = 0.00;
		 var ImportePropuestoC = 0.00;
		 var AnioFiscal =Number($("#h_aEjercicioFiscal").val());
		 var Anio ='';
		 
		 
		 
		 if ($("#h_lAbierto").val() =='C'){
		     ImportePropuestoA = 0;
		 }
		 else{
		    ImportePropuestoC = 0;
		 }
		TotalAnios = Number(yYear) - Number(xYear);
		
                   while (TotalAnios >= i)
			        	{  
				        	    Anio=Number(xYear) + i;
			        	        
			        	        $("#cEjercicioTemp").val(Anio);
			        	       queryFormPost("SumaMontosPlurianualesAnioSeleccionado", {	async : false  });

			        	        for(var j=0;j<Arreglo_montos.length;j++){

							    		ImportePropuestoC = $("#Monto_por_Anio").val();
	                             }
	                          
						
							
			        	     $('#dt_Montos').dataTable().fnAddData( [
				   	         '<td align="center" >' +  (Number(xYear) + i)+'</td>', //0
				             '<td align="center" > <input type="number"  class="notEditable" readonly="readonly" name="Monto_" '+  (Number(xYear) + i) + '  id="Monto_'+  (Number(xYear) + i) + '"  value="'+ImportePropuestoC+'"  onchange="fn_sumaMontos();" onKeyPress="return onlyNumberss(event);"></td>', //1
					         '<td align="center" > <input type="number"   class="monto"  name="MontoMin_" '+  (Number(xYear) + i) + '  id="MontoMin_'+  (Number(xYear) + i) + '"   onchange="fn_sumaMontosMinMax();"  value="0"   onKeyPress="return onlyNumberss(event);"></td>',//2
					         '<td align="center" > <input type="number"  class="monto"  name="MontoMax_" '+  (Number(xYear) + i) + '  id="MontoMax_'+  (Number(xYear) + i) + '" value="'+ImportePropuestoA+'"   onchange="fn_sumaMontosMinMax();"   onKeyPress="return onlyNumberss(event);"></td>',//3
					         '<td align="center" > <input type="number"  class="monto" onchange="fn_sumaAvances()"  name="Avance_"'+  (Number(xYear) + i) +  '  id="Avance_'+  (Number(xYear) + i) + '"></td>'//4
													] );
																			
							// almacena los años
							$('#Arreglo_Anio').val($('#Arreglo_Anio').val()+(Number(xYear) + i)+ '/');
							ImportePropuestoA = 0;
							ImportePropuestoC = 0;	 					
							 i++;
			        	}	
			        	
			        	
			
  if (i>0)   fn_CreaTotales_Ocultacolumnas(); 
          
  fnVerGrid("montos");   
 } //fin fn_CreaTabla() 
 
 function fn_CreaTotales_Ocultacolumnas() 
 {
 
 			   	          //Genera renglon de Totales
				        	$('#dt_Montos').dataTable().fnAddData( [
				              '<td align="center">Totales</td>',
				              '<td align="center"><input type="number" maxlength="20"   class="notEditable" name="MontoTotal" value="0" readonly="readonly" id="MontoTotal"></td>',
					          '<td align="center"><input type="number" maxlength="20"  class="notEditable" name="MontoTotalMin" value="0" readonly="readonly" id="MontoTotalMin"></td>',
					          '<td align="center"><input type="number" maxlength="20"  class="notEditable" name="MontoTotalMax" value="0" readonly="readonly" id="MontoTotalMax" ></td>',
					          '<td align="center"><input type="number" Id="AvanceTotal"  class="notEditable" name="AvanceTotal" value="0" readonly="readonly" ></td>'
													] ); 
				     
		 	  
 				     $('#dt_Montos').dataTable().fnSetColumnVis(1,true);
                     $('#dt_Montos').dataTable().fnSetColumnVis(2,false);
		     	     $('#dt_Montos').dataTable().fnSetColumnVis(3,false);
		        	 fn_sumaMontos();
			
     $("#h_Bandera").val("true");    
  }
 
	function fn_Select(Valor_Selccionado) {
			
			 $("#h_lAbierto").val(Valor_Selccionado);  
					
	}
			
function CargarDetalle()
{
			   $('#dt_Montos').dataTable().fnClearTable();
               $('#Arreglo_Anio').val(''); 
               $("#h_ctipoMoneda").val($("#Select_TipoMoneda").val());
               $("#h_nTipoCambio").val($('#TipoCambio').val());
               $("#h_Select_TipoContrato").val($("#Select_TipoContrato").val());
                                                                                     
				//$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg:"10", ajax: 'false'},function(j){   
				var zTabla = "TCONTRATOPLURIANUALDETALLE";
				var camposWhere = " WHERE nFolioContratoPlurianual = '" +$("#nFolioPurianual").val()+"' AND nModificacion = " + nModificacion; 
				var param = "";
				var sOrder = " ORDER BY nConsecutivo ASC ";
			
																							
			$.getJSON("../catalogos/SelectJson.jsp?"+ new Date().getTime(),{Tabla:"TCONTRATOPLURIANUALDETALLE", Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){ 
											
				 var sAnio;
				 var nMonto;
				 var nMontoMin;
	   			 var nMontoMax;
	   			 var nPeso;
	   			 var nPesoMin;
	   			 var nPesoMax;
	   			 var nAvance;
	   			 var sSololectura = '';
	   			 var nRenglones =0;
	   			 
	   			 var AnioActual = new Date().getFullYear();
	   					 
	   				 
	   				   
	   					for (var i = 0; i < j.length; i++)  
	   					{	
	   				
	   				    sAnio =j[i].Col0;
	   				   nMonto = parseFloat(j[i].Col1);
	   				   nMontoMin = parseFloat(j[i].Col2);
	   				   nMontoMax = parseFloat(j[i].Col3);
	   				   nAvance = parseFloat(j[i].Col7);
	   				   
	   				
	   				     
	   				 
	   				 			if ($("#h_nOperacion").val()== 5 || sAnio < AnioActual - 1){
					   					       $('#dt_Montos').dataTable().dataTable().fnAddData( [
								               '<td align="center">' + sAnio +'</td>' , 
								               '<td align="center"> <input type="number" maxlength="20" readonly="readonly"  class="notEditable" value="' + nMonto + '"name="Monto_" '+  sAnio + '  id="Monto_'+  sAnio + '"></td>', //1 
								               '<td align="center"> <input type="number" maxlength="20" readonly="readonly"  class="notEditable"  value="' + nMontoMin + '"name="MontoMin_" '+  sAnio + '  id="MontoMin_'+  sAnio + '"></td>',//2
									           '<td align="center"> <input type="number" maxlength="20" readonly="readonly"  class="notEditable"  value="' + nMontoMax + '"name="MontoMax_" '+  sAnio + '  id="MontoMax_'+  sAnio + '"></td>',//3
									           '<td align="center"> <input type="number" maxlength="3"  readonly="readonly"  class="notEditable"  value="' + nAvance + '"name="Avance_"'+  sAnio +  '  id="Avance_'+  sAnio + '"></td>'//7
												]);
				                }
				                else    {
				                  $('#dt_Montos').dataTable().dataTable().fnAddData( [
								               '<td align="center">' + sAnio +'</td>' , 
								               '<td align="center"> <input type="number" maxlength="20" readonly="readonly"  class="notEditable"  value="' + nMonto + '"name="Monto_" '+  sAnio + '  id="Monto_'+  sAnio + '"   onchange="fn_sumaMontos();" onKeyPress="return onlyNumberss(event);"></td>', //1 
								               '<td align="center"> <input type="number" maxlength="20" readonly="readonly"  class="notEditable"   value="' + nMontoMin + '"name="MontoMin_" '+  sAnio + '  id="MontoMin_'+  sAnio + '"  onchange="fn_sumaMontosMinMax();"    onKeyPress="return onlyNumberss(event);"></td>',//2
									           '<td align="center"> <input type="number" maxlength="20" readonly="readonly"  class="notEditable"   value="' + nMontoMax + '"name="MontoMax_" '+  sAnio + '  id="MontoMax_'+  sAnio + '"   onchange="fn_sumaMontosMinMax();"   onKeyPress="return onlyNumberss(event);"></td>',//3
									           '<td align="center"> <input type="number" maxlength="3"  readonly="readonly"  class="notEditable"  onchange="fn_sumaAvances()"  value="' + nAvance + '"name="Avance_"'+  sAnio +  '  id="Avance_'+  sAnio + '"></td>'//7
												]);
				                
				                
				                }
	   				
	   				        
	   	        		 	$('#Arreglo_Anio').val($('#Arreglo_Anio').val()+(sAnio)+ '/');	 
	   	        		   
	   					}
						
					 
	         	              $("#h_DetalleRecuperado").val("Ren.Detalle: " + nRenglones);
						
							 
							 if (j.length)  	
							 {
					     	 	fn_CreaTotales_Ocultacolumnas();
					     	 
						  	  		 fn_sumaMontos();
						 	  }      	
	         	            
	         	             	
						 	  	   fn_sumaAvances();	
					
	         	}); //fin addTabla
	   
	   
	                     
	
}

function CargarVistaGeneradaXExcel()
{                                     
				                               
				//$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg:"10", ajax: 'false'},function(j){   
				var zTabla = "TCONTRATOPLURIANUAL_EP";
				var camposWhere = " WHERE nFolioContratoPlurianual = '" +$("#nFolioPurianual").val()+"' AND nModificacion = 0" ; 
				var param = "";
				var sOrder = " ORDER BY CICLO ASC ";
																		
				$.getJSON("../catalogos/SelectJson.jsp?"+ new Date().getTime(),{Tabla:"TCONTRATOPLURIANUAL_EP", Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){ 

                   $('#dt_clavepresup').dataTable().fnClearTable();											
                        
						if (j.length> 0)
							
						{
						//	alert("mcf importados" + j.length);
						// $("#h_DatosImportados").val(j.length); 
						// alert(j.length);
				//		 $("#cDescripcionArchivo").val("Se importaron " +  j.length + " registros");
					//	 CargaSaldosDisponiblesEp();	
						 //ojo cambio temporal 15 de enero 2016 bjCargaSaldosDisponiblesEp();
							//if  ($("#cDocumentoHaplicado").val() == 'S'){
							  //   CargaSaldosDisponiblesEp();
							//}
							//else{
								// if ( ValidaClavesPresupuestarias()){
						        	//  CargaSaldosDisponiblesEp();
						 		 //}
							//}				
						}
	         	});  	
	         	
	         	
	         
	
}


//
function ValidaClavesPresupuestarias(id_oper)
{
			              
		              
	             var bValida = true;           
             	var zTabla = "VSALDOS_Y_CONTRATOSPLURIANAUALES_EP";
			//	var camposWhere = "where  nFolioContratoPlurianual ='" + $("#nFolioPurianual").val()+  "'  and ciclo <> " + <%=cEjercicio%>;  
				var camposWhere = "";
				var comboAnio = $("#Select_Anio").val();
				var param;
				// Si es mayor a 50 registros
				if ($("#h_DatosImportados").val() >50) {
				   param = " nFolioContratoPlurianual ='" + $("#nFolioPurianual").val()+  "'  and ciclo = '" + comboAnio + "'" ;
				}else{
					param = "   nFolioContratoPlurianual ='" + $("#nFolioPurianual").val()+  "'";// and CpEp.ciclo  = " + <%=cEjercicio%>;
				}          
				param = param + " AND nModificacion = " + nModificacion;
				var sOrder = "";
				var msgValida = "Validación: \n";
			
						$.getJSON("../catalogos/SelectJson.jsp?"+ new Date().getTime(),{Tabla:"VSALDOS_Y_CONTRATOSPLURIANAUALES_EP", Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "20", ajax:"true"}, function(j)
				{ 
				     var nMonto_disponible = 0;
				     var  Monto_Propuesto = 0;
				     var sumnImporteCons = 0;
				     var nImporteCons =0;
				     var sumnImporteInv =0;
				     var nImporteInv = 0;
				     var Anio = '';
				     var Ep;
				     var mPropuesto = 0;
				     var mDisponible = 0;
				     var sCadena = "";
				     var sTipoObra = "";
				     var bSinFormato = false;
				     var bConformato = true;
				     var TipoGasto = "";
				     var TipoGastoValida = "";
				     var sArreglo ;
				     
				   
				     
				   
					 if(j.length>0)
					 {    
	   					for (var i = 0; i < j.length; i++) 
	   					{	
	   					   
	   	        			    Anio = j[i].Col0;
	   	        			    sTipoObra    =  j[i].Col4;
	   	        			    Ep  = j[i].Col1;
	   	        	
	   	        			    if (i== 0) {
	   	        			    	    //Cargamos Campos Select
	   	        			    	    var EpInicial = j[i].Col1;
	   	        			    	    $("#cTipoGasto").val(Ep.substring(37,38)); 
	   	        			    	    $("#cPartida").val(Ep.substring(31,36));
	   	        			    	     $("#cConcepto").val(Ep.substring(31,33));
	   	        			    	  
	   	        			    	     // alert( $("#cPartida").val());
	   	        			    	    $("#cCapitulo").val(Ep.substring(31,32));
	   	        			    	    
	   	        			    	    alert("Capitulo: " +$("#cCapitulo").val());
	   	        			    	    alert("Concepto: " +$("#cConcepto").val());
	   	        			        
			   	        			    TipoGastoValida =  $("#cTipoGasto").val();
			   	        			    $("#select_TipoGasto").val($("#cTipoGasto").val());
			   	        			    queryFormPost("Clasifica_PluriAnuales_tipoGasto", {	async : false});
			   	        			    
			   	        			//    querySelectPost("catalogo_PluriAnuales_TipoContrato","Select_TipoContrato", {async : false	});
			   	        			 alert("Tipocontrato:" +$("#cCveTipoContrato").val());
			   	        			    $("#Select_TipoContrato").val($("#cCveTipoContrato").val()); 
			   	        			    
			   	        		//	    var sCaracteres = $("#cEtiqueta_a").val();
			   	        		//	    alert(sCaracteres.length()); 
			   	        		        $("#tTipo").val($("#cClasificacionGasto").val());
			   	        			    $("#ClasificaTipoGasto").val(TipoGasto);
			   	        				//Almacenamos el primer arreglo de validacion
			   	                         sArreglo = $.trim($("#cCapitulo").val()) + "-"+  $.trim($("#cTipoGasto").val()) + "-" + $.trim($("#cCveTipoContrato").val());
			   	        			     
	   	        			     }
	   	        			    
	   	        			    
	   	        			     if (i>1 ) {
	   	        			    	     //comparamos las combinaciones de la Ep
	   	        			    	    $("#cTipoGasto").val(Ep.substring(37,38)); 
	   	        			    	    $("#cPartida").val(Ep.substring(31,36));
	   	        			    	    $("#cCapitulo").val(Ep.substring(31,32)); 
	   	        			    	    $("#cConcepto").val(Ep.substring(31,33));
	   	        			    	//    queryFormPost("Clasifica_PluriAnuales_tipoGasto", {	async : false});
			   	        			  
   	        			    	         var sArregloValida =  $.trim($("#cCapitulo").val()) + "-"+  $.trim($("#cTipoGasto").val()) + "-" + $.trim($("#cCveTipoContrato").val());
			   	        			 
	   	        			           			    
	   	        			          
	   	        			                if ((sArreglo != sArregloValida && i>0 )){
	   	        			  		        	sCadena += " Las clave presupuestaria "+EpInicial + "\n," +
	   	        			  		        	           " es de diferente la especificación del contrato con respecto a la clave  " + Ep + ".\n";
												 bValida = false ; 
												 break;	        			  		                
	   	        			  		        }
	   	        			  	 }	        
	   	        		
	   	        	
	   	        			  		
	   	       		 }// fin del for
	   					
	   				}	
	   				else   
	   				{bValida = false ; }
	   				
	   				
	   				
						
					 if ($("#h_Existe").val() == 0){
						       
						 	   //Validacion
						      if  (sCadena.length > 0){
	   					         // $("#dt_Calendario").dataTable().fnClearTable();
	   					          sCadena += "Verifique el excel y vuelva a cargar" ; 
	   					          alert("Validación: \n\n" + sCadena);
	   					          bValida = false ;
	   					       
	   				          }
						 	   
						 	   else {  // paso las validaciones
						 		   bValida = true ;
						 		 
						 		 }
	   				}
					
					
					if ( bValida &&  j.length>0)
					 { 
						if (id_oper< 3){
						alert("Total de registros validados "+ $("#h_DatosImportados").val());	;
						}
							 		   
					}	 		   	
	         	});  
	         	
	return bValida;         	
	         
}





function CargaSaldosDisponiblesEp()
{
				var camposWhere ="";
               	
                var comboAnio = $("#Select_Anio").val();
			
				// Si es mayor a 50 registros
			               	
           	    // Aqui se trae lo datos de una vista vsaldosanuales
           	    //añoFin
           		var zTabla = "VSALDOS_Y_CONTRATOSPLURIANAUALES_EP";
           		if ($("#h_DatosImportados").val() >50) {   
           			var param = " nFolioContratoPlurianual ='" + $("#nFolioPurianual").val()+  "'and ciclo = '" +comboAnio +"'";
           		}
           		else{
           		   var param = " nFolioContratoPlurianual ='" + $("#nFolioPurianual").val()+  "' and ciclo = '" +comboAnio +"'";
           		}
          		param = param + " AND nModificacion = '0' " ;	
           
					      
				var sOrder = "";
			
	       		$("#dt_Calendario").dataTable().fnClearTable();  
				$.getJSON("../catalogos/SelectJson.jsp?"+ new Date().getTime(),{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "10"}, function(j){ 
				     var nMonto_disponible = 0;
				     var  Monto_Propuesto = 0;
				     var Monto_anioActual  =0;
					 if(j.length>0)
					 {    
						$.blockUI({
				            	message : "Espere cargando ..." +j.length + " Registros"
				               });
			    		      						 
	   					for (var i = 0; i < j.length; i++) 
	   					{	
	   					    	
	   					   	  //alert([j[i].Col0,j[i].Col1, j[i].Col4, j[i].Col5]) ;
	   					      $("#dt_Calendario").dataTable().fnAddData([j[i].Col0,j[i].Col1, j[i].Col4, j[i].Col5],false);
	   					}
	   					
	   					      $("#dt_Calendario").dataTable().fnAddData(['Renglones : ' + $('#h_DatosImportados').val(), 'Totales por todo el Contrato', $('#Monto_Total_Contrato').val(), $('#tTotal').val()]);
	   	       		
	   	       		  setTimeout($.unblockUI, 20 * j.length);
	   				}						
	         	});  
				
					
         			
					      
	}
	
	
	function BloqueaGrid(){
	
     queryFormPost("PlurianualMontosAnuales", {	async : false  });
   
	  queryFormPost("SumaMontosPlurianualesSoloAnioActual", {	async : false  }); 
	    					 
      if ($("#h_Existe").val() == 0){
		       
	     $("#h_MontoTotalDisponible").val($("#Monto_Total").val());
	     $("#h_InporteTotalPropuesto").val($("#Monto_Total_Contrato").val());
	     $("#Monto_Total_Pesos").val($("#Monto_Total").val());
	     
	     //NOTA //Obtenemos la partida el concepto y el tipo de gasto y el tipo de gasto , la partida , el concepto y el capitulo,  #cTipoGasto,  #cPartida #cConcepto #cCapitulo 
	     queryFormPost("PlurianualObtenCapituloyTipoGasto", {	async : false});
	     
	     $("#select_TipoGasto").val($("#cTipoGasto").val());
	     //Obtenemos el tipo de contrato  #cCveTipoContrato
		  queryFormPost("Clasifica_PluriAnuales_tipoGasto", {	async : false});
		  queryFormPost("PlurianualValidaTipoContrato", {	async : false});
		  if($("#valTipoGasto").val()!="1" && $("#cTipoGasto").val()!=""){
		  	alert("Tipo de contrato erroneo, favor de revisar el excel y cargarlo nuevamente.");
	  		parent.document.getElementById("pb_save").disabled=true;
		  }else{
		  	parent.document.getElementById("pb_save").disabled=false;
		  	$("#select_TipoGasto").attr("disabled",true);
		  	$("#Select_TipoContrato").attr("disabled",true);
		  }
		  $("#Select_TipoContrato").val($("#cCveTipoContrato").val());
		   
		  fn_SeleccionGasto();
		 
		     
	}
		 CargaSaldosDisponiblesEp();
}


function Fn_CargaExcel() 
		{	

			if ($("#fileUpload").val() == "")
				alert("Debe seleccionar un archivo a cargar.");
			else {
					
			    	$.blockUI({
				            	message : "Cargando Archivo. Por favor espere ......"
				               });
				    $('#dt_Calendario').dataTable().fnClearTable();            
				    $('#dt_Montos').dataTable().fnClearTable();
				    //fn_Limpia();
				    $("#FormContrato").prop("action" , "../servlet/ContratoPlurianualServlet?h_bEspecial="+$("#h_bEspecial").val()+"&nmod="+$("#nmod").val());
				    $("#FormContrato").submit();
				   
			      }
		}
		
function fn_creaCampo(col,renglon, Valor,Tamanio,bFormato){
	var NombreCampo = "";
	if (bFormato){ 
					var Valor = formatCurrency(Valor);
    }

    NombreCampo = '<td> <input type="number"  class="notEditable" size="'+ Tamanio +'"  name="Campo_'+col+'_'+renglon+ '" id="Campo_'+col+'_'+ renglon+'"  value=' + Valor +  '></td>';//4
     
return NombreCampo;
}		


function fn_ObtenCampo(renglon, col){
var ValorRetorno = "";

                  if ($('#Campo_'+ col + '_' + renglon).length >0){       
  			       ValorRetorno = $('#Campo_' + col + '_' + renglon).val();
                  }
                  else
                    ValorRetorno ='';
 		       
  			    
			   
 return ValorRetorno;               
}

function fn_GuardaDetalle()
		{
		   
			var str = $('#Arreglo_Anio').val();   
			var Arreglo= str.split("/"); 
			var nAnioFiscal ='';
			var nMonto = 0;
			var nMontoMin = 0;
			var nMontoMax = 0;
			var nPeso = 0;
			var nPesoMin = 0;
			var nPesoMax = 0;
			var nAvance = 0;
			var nValor_ConFormato ;
			var nValor_sinFormato ;
			var aTrs = $('#dt_Montos').dataTable().fnGetNodes();
			var nRenglones = (aTrs.length)-1;// Totales nop
			var TotalInsertados = 0;
			var bExito = false;
			
			
			 if ($("#h_Existe").val() == 1) {
			 	queryFormPost({ queryName:"deleteContratoPlurianualDetalle",async:false, callback:function(){bExito= true;}});
			 	
			 }
			 
			 if ($("#h_Existe").val() == 1 && bExito == false) {
			 alert ('Error en deleteContratoPlurianualDetalle');
			 return -1;
			 
			 }
			
			
		
			for (var i=0 ; i< nRenglones; i++){
				//  alert(Arreglo[i]); 
				
				bInsertado = false;  
				$("#h_nConsecutivo").val(i);
				$("#h_nAnioFiscal").val(Arreglo[i]);
		
				nValor_ConFormato = $('#Monto_'+Arreglo[i]).val();
				if (nValor_ConFormato == undefined) nValor_ConFormato  =0;
				
		
				nValor_sinFormato  = parseFloat(fn_quitaFormato(nValor_ConFormato));
		
				$("#h_nMonto").val(nValor_sinFormato);
		
				nValor_ConFormato = $('#MontoMin_'+Arreglo[i]).val();
				if (nValor_ConFormato == undefined) { nValor_ConFormato  = 0.0} ;
				
				
				nValor_sinFormato  = parseFloat(fn_quitaFormato(nValor_ConFormato));
				$("#h_nMontoMin").val(nValor_sinFormato);
		
				nValor_ConFormato = $('#MontoMax_'+Arreglo[i]).val();
				if (nValor_ConFormato == undefined) nValor_ConFormato  = 0.0;
				
				nValor_sinFormato  = parseFloat(fn_quitaFormato(nValor_ConFormato));
				
				$("#h_nMontoMax").val(nValor_sinFormato);
				nValor_ConFormato = $('#Peso_'+Arreglo[i]).val();
				
				if (nValor_ConFormato == undefined){ nValor_ConFormato  = 0.0;}
		
				nValor_sinFormato  = parseFloat(fn_quitaFormato(nValor_ConFormato));		
				$("#h_nPeso").val(nValor_sinFormato);
				nValor_ConFormato = $('#PesoMin_'+Arreglo[i]).val();
				
				if (nValor_ConFormato == undefined) nValor_ConFormato  = 0.0;
				
				nValor_sinFormato  = parseFloat(fn_quitaFormato(nValor_ConFormato));
				$("#h_nPesoMin").val(nValor_sinFormato);
		
				nValor_ConFormato = $('#PesoMax_'+Arreglo[i]).val();
		
				if (nValor_ConFormato == undefined) nValor_ConFormato  = 0.0;
				nValor_sinFormato  = parseFloat(fn_quitaFormato(nValor_ConFormato));
		
		
				$("#h_nPesoMax").val(nValor_sinFormato);
				$("#h_nAvance").val(parseFloat($('#Avance_'+Arreglo[i]).val()),10);
		
		
				if (isNaN($("#h_nMonto").val()))    $("#h_nMonto").val(0);
				if (isNaN($("#h_nMontoMin").val())) $("#h_nMontoMin").val(0);
				if (isNaN($("#h_nMontoMax").val()))  $("#h_nMontoMax").val(0);
				if (isNaN($("#h_nPeso").val()))      $("#h_nPeso").val(0);
				if (isNaN($("#h_nPesoMin").val()))   $("#h_nPesoMin").val(0);
				if (isNaN($("#h_nPesoMax").val()))  $("#h_nPesoMax").val(0);
				if (isNaN($("#h_nAvance").val()))   $("#h_nAvance").val(0);
				
		        $("#h_nImporte").val($("#h_nMonto").val());
		                
				queryFormPost({ queryName:"InsertaPlurianualDetalle",async:false, callback:function(){TotalInsertados ++;} });
			}
					
				if (TotalInsertados == nRenglones) return 1;
				else return -1;	 
					 
		}
		
			
		function LetrasNums(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode;
						if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241)
						{alert ("Solo se permiten Letras y Numeros");}
						
			if (keyPressed == 61 || keyPressed == 63 || keyPressed == 62
			|| keyPressed == 59 || keyPressed == 58 || keyPressed == 60
			|| keyPressed == 91 || keyPressed == 92 || keyPressed == 93
			|| keyPressed == 94 || keyPressed == 95 || keyPressed == 96) {
			return false;
			}
			return !(keyPressed > 32 && (keyPressed < 48 || keyPressed > 122) && keyPressed != 209 && keyPressed != 241);
			}


function onlyNumberss(evt)
 {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true; 
}

function fn_sumaAvances()
{
     //Funcion que suma los campos de avance
     var  str = $('#Arreglo_Anio').val();   
     var Arreglo= str.split("/"); 
   
     
     var aTrs = $('#dt_Montos').dataTable().fnGetNodes();
     var nSuma;     
     var nRenglones = (aTrs.length)-2;
     var nsumaAvances = 0.00;
     var nAvanceTotal =0.00;
    
     for (var i=0 ; i<= nRenglones; i++)
     {
     	
        nsumaAvances  =  parseFloat($('#Avance_'+Arreglo[i]).val().replace('%', ''),10);
        if (isNaN(nsumaAvances)) nsumaAvances= 0;
        
        var texto = $('#Avance_'+Arreglo[i]).val().replace('%', '');
        $('#Avance_'+Arreglo[i]).val(texto + '%');
        
        nAvanceTotal += nsumaAvances;
         
         
		
     }
     
     if (nAvanceTotal > 100.00 || nAvanceTotal <0)
         {	
			alert("El campo total del avance debe ser 100% ");   
		}
     
   // alert(nAvanceTotal);
      $("#AvanceTotal").val(nAvanceTotal+"%");
      
      $("#h_nAvanceTotal").val(nAvanceTotal);
     
    
}




function fn_sumaMontos()

{
    //Funcion que suma los campos de tipo monto 
    
     var  str = $('#Arreglo_Anio').val();   
     var Arreglo= str.split("/"); 
     var aTrs = $('#dt_Montos').dataTable().fnGetNodes();
     var nSuma;     
     var nRenglones = (aTrs.length)-1;
     var nTipoCambio = $("#h_nTipoCambio").val() ;
     var AnioFiscal =Number($("#h_aEjercicioFiscal").val());
    
     //Validamos si es moneda extranjera
      if ($("#h_ctipoMoneda").val() !='MxN')
      {
        if ($("#h_nTipoCambio").val() == null || $("#h_nTipoCambio").val() == ""  || $("#h_nTipoCambio").val() == 0){
	         	alert("Indique el Tipo de Cambio");
	           	return ;
	     }
		 else
        {
           nTipoCambio = 1 ;
           $("#h_nTipoCambio").val(nTipoCambio);
         }
     }
        
      nTipoCambio = parseFloat($("#h_nTipoCambio").val(),10);
     var nsumaMonto_Conformato ="";
     var nsumaMonto = 0.00;
     var nsumaMontoTotalPesos = 0.00;
     var n_MontoTotal = 0.00;
     for (var i=0 ; i< nRenglones; i++)
     
     {
        nsumaMonto_Conformato =  $('#Monto_'+Arreglo[i]).val();
        
         if (nsumaMonto_Conformato == undefined  || isNaN(nsumaMonto_Conformato)) {nMonto_conformato  =0;}
         nsumaMonto = parseFloat(fn_quitaFormato(nsumaMonto_Conformato),10);
        $('#Monto_'+Arreglo[i]).formatCurrency();
        if (isNaN(nsumaMonto)) nsumaMonto= 0.00;
                 
        n_MontoTotal += nsumaMonto;
        
        if (Number(Arreglo[i]) == AnioFiscal)
        $("#h_MontoCalculadoFiscal").val(parseFloat(nsumaMonto),10);

    
       //  nsumaMontoTotalPesos += parseFloat(nsumaMonto * nTipoCambio),10;
      }
      $("#MontoTotal").val(parseFloat(n_MontoTotal),10);
      $("#h_nMontoTotal").val(parseFloat(n_MontoTotal),10);
      $("#h_nMontoTotalCalculado").val(parseFloat(n_MontoTotal),10);
      $("#MontoTotal").formatCurrency();
      $("#h_nMontoTotalMin").val(0);
      $("#h_nMontoTotalMax").val(0);
   
 }



function fn_sumaMontosMinMax()
{
      //Funcion que suma los campos de tipo monto solo los campos monto maximo y monto minimo
      var str = $('#Arreglo_Anio').val(); 
      var nTipoCmbio = $("#h_nTipoCambio").val();  
      var Arreglo= str.split("/"); 
      var aTrs = $('#dt_Montos').dataTable().fnGetNodes();
      var nMonto_conformato;
      var AnioFiscal =Number($("#h_aEjercicioFiscal").val());
      var nMontoMin = 0; 
      var nMontoMax= 0; 
      var nRenglones = (aTrs.length)-1;
      var n_MontoTotalMin =0;
      var n_MontoTotalMax =0;
     for ( var i=0 ; i< nRenglones; i++ )  
     
     
     { 
         
         nMonto_conformato = $('#MontoMin_'+Arreglo[i]).val();
        if (nMonto_conformato == undefined || nMonto_conformato == '')  nMonto_conformato  =0;
         else nMontoMin=parseFloat(fn_quitaFormato(nMonto_conformato));
             
         n_MontoTotalMin += nMontoMin;
         $('#MontoMin_'+ Arreglo[i]).formatCurrency();
         nMonto_conformato = $('#MontoMax_'+Arreglo[i]).val();
         
         if (nMonto_conformato == undefined || nMonto_conformato == '')  {nMonto_conformato  =0;}
          nMontoMax = parseFloat(fn_quitaFormato(nMonto_conformato));
           
         $('#MontoMax_'+ Arreglo[i]).formatCurrency();
         n_MontoTotalMax += nMontoMax;
        
        if (Number(Arreglo[i]) == AnioFiscal)
        $("#h_MontoCalculadoFiscal").val(parseFloat(nMontoMax),10);

    
                
                        
     }
    
      
      
       
      parseFloat($("#MontoTotalMin").val(n_MontoTotalMin),10);
      parseFloat($("#h_nMontoTotalMin").val(n_MontoTotalMin),10);
      $("#MontoTotalMin").formatCurrency();
      
      parseFloat($("#MontoTotalMax").val(n_MontoTotalMax),10);
      parseFloat($("#h_nMontoTotalMax").val(n_MontoTotalMax),10);
      $("#MontoTotalMax").formatCurrency();    
         
     //Cuando el contrato plurianual es abierto inicializar totales
     $("#h_nMontoTotalCalculado").val(0);
     $("#h_nMontoTotal").val(0);
     // $("#h_nPesoTotal").val(0);
   	
	
}



function fn_quitaFormato(fld) 
{
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}

function cambiafrmt(fld) {
$("#" + fld.id).formatCurrency();
}



function fn_Seleccione()
{

     $("#h_ctipoMoneda").val($('#Select_TipoMoneda').val());
     $("#h_nTipoCambio").val($("#TipoCambio").val());
	 if ($("#h_ctipoMoneda").val() !='MxN')
        {
   
            if ($("#h_nTipoCambio").val() == null || $("#h_nTipoCambio").val() == ""  || $("#h_nTipoCambio").val() == 1) {
         	   alert("Indique el Tipo de Cambio");
         	   $("#TipoCambio").attr("disabled",false);
         	   return;
            }	
        }
      else{
            $("#TipoCambio").val(1);	
            $("#TipoCambio").attr("disabled",true); 
            fn_ObtenMonto();
      }
       
       
        
}

function fnVerGrid(tipo){

if (tipo == "montos"){
   	setTimeout(function(){oTable.fnAdjustColumnSizing();},1000);
	}
else{
    setTimeout(function(){oTableCalendario.fnAdjustColumnSizing();},1000);
    }
}


	 	
	function fn_CuentaDoctos(id_oper){
	//VERIFICAMOS SI HAY DOCUMENTACION ASOCIADA 26/08/2015    
	 var Mensaje = '';
	 var TotalDoctos = 0;
	 	  $("#h_total").val(0);
	 	  $("#hTotaldoctos").val(0) ; 
	 	  $("#hNombredocto").val("Memorando y Evaluacion");
		 
		  queryFormPost("ValidaDocumento", {async : false });
		  if (isNaN($("#h_total").val()) || $("#h_total").val() == ""  ) {
		    	Mensaje = "- No olvide anexar el documento de  'Memorando y Evaluación'  y el layout que cargo \n ";
		  
		  }
		  else if ( $("#h_total").val() == 0){
		  	Mensaje = "- No olvide anexar el documento de  'Memorando y Evaluación'   y el layout que cargo \n ";
		  }
		   
		 
		  var str  =$("#cMotivoRechazo").val();
		  if(str.length ==0)  {// Si procede la autorizacion validar docto de Autorizacion
		    if  (id_oper== 3){ //Revisor Normativo 
		          $("#h_total").val(0);  
		          $("#hNombredocto").val("Memorando de Revisor");
		  
			  	queryFormPost("ValidaDocumento", {async : false });
			  	if (isNaN($("#h_total").val()) || $("#h_total").val() == "" ) {
			    	  Mensaje += "- Falta anexar Memorando de Revisor. \n";
			     	//return Mensaje;
			     	}
			  	if ( $("#h_total").val() == 0){   
			    	 Mensaje += "- Falta anexar Memorando de Revisor. \n";
			     	return Mensaje;
			     }
		   }
		  
		  
		  
		  
			if  (id_oper== 4){ //Autorizacion Central 
			     $("#h_total").val(0);  
		         $("#hNombredocto").val("Dictamen  Mascp");
		  
			  	queryFormPost("ValidaDocumento", {async : false });
			  	if (isNaN($("#h_total").val()) || $("#h_total").val() == "" ) {
			    	  Mensaje += "- No olvide anexar Dictamen  Mascp \n";
			     	//return Mensaje;
			     	}
			  	if ( $("#h_total").val() == 0){   
			    	 Mensaje += "-  No olvide anexar Dictamen  Mascp \n";
			     	return Mensaje;
			     }
		   }
		}   	 
	     return   Mensaje;
      
	}
	




function fn_SeleccionGasto()
{
   
     var  tipo = $("#select_TipoGasto option:selected").html(); 
         $('#tTipo').val(tipo);
}


function fn_ObtenMonto(){
	var nMonto = 0;
	var nMontoPesos;
	var nTipoCambio;
	 
	  if  ($("#Monto_Total").val() != null || $("#Monto_Total").val() != "" ){
	 	    nMonto =parseFloat(fn_quitaFormato($("#Monto_Total").val()));
	  }
      if (nMonto > 0 && $("#TipoCambio").val()>0)
        {
	        		nTipoCambio = $("#TipoCambio").val();
	      		  $("#Monto_Total_Pesos").val(nMonto*nTipoCambio);
			      $("#h_nMontoTotal").val(nMonto); 
			      $("#h_nMontoTotal_Pesos").val($("#Monto_Total_Pesos").val());
			      $("#h_Monto_Total_Contrato").val(parseFloat(fn_quitaFormato($("#Monto_Total_Contrato").val())));
			      $("#Monto_Total").formatCurrency();
				  $("#Monto_Total_Pesos").formatCurrency(); 
				  $("#Monto_Total_Contrato").formatCurrency();
				  $("#ImporteComparativoPlurianual").val($("#Monto_Total_Contrato").val());
				  $("#h_ImporteComparartivoAnual").val($("#ImporteComparartivoAnual").val());
				  $("#ImporteComparartivoAnual").formatCurrency();
         };
 }  

function validaCheck()	{
	  
	 var str  =$("#cMotivoRechazo").val();
	    if (str.length>0){
	       var decision = confirm("Limpiar el Motivo de Rechazo");
		   if (decision){
  				  $("#cMotivoRechazo").val("");
		    }
	    }   
	   if($("#checkbox").is(':checked')){
		  $("#motivoRechazo").hide();
		  $("#cMotivoRechazo").val("");
	    }
	   else {
	    	$("#motivoRechazo").show();
	    }
}
	

function fn_validaImporteAnual()
{
	var arrDataEp = oTableCalendario.fnGetData();
	var sumaTotal=0;	
	var revisa=true;
	var totalPesos=0;
	if(arrDataEp.length>0)
	{
		totalPesos=parseFloat(fn_quitaFormato($("#Monto_Total_Pesos").val()));
		sumaTotal=0;
		for(var i=0; i<arrDataEp.length-1; i++)
		{
			//sumaTotal+=parseFloat(arrDataEp[i][3]);
			 var Valor = $('#Campo_5_'+ i).val();
			// alert(Valor);
			sumaTotal+=parseFloat(fn_quitaFormato(Valor));
			
			//alert("sumaTotal:" +sumaTotal);
		}
		if(totalPesos>sumaTotal)
		{
			revisa=false;
		} 
	}
	return revisa;
}


$("Ciclo").click(function() {
alert("click");

} );

function fn_Muestra(objet)
{
 
  
 
 switch(objet)
			{
			case 'Desglose': //capturista
				  if( !( $("#chkDesglose").is(':checked')) ){
                       $('#Mensaje_Desglose').text("Muestra Mensaje");
                       $("#Mensaje_Montos").hide();
                  }   
                  else{
      				 $("#Mensaje_Montos").show();
      				 $('#Mensaje_Desglose').text("Oculta Mensaje");
                  }  
				  break;
			case 'Montos':
			if( !( $("#chkMontos").is(':checked')) ){
                   $('#Montos').text("Muestra Mensaje");
                  }   
                  else{
      				 $("#Montos").show();
                  }  
				  break;	  
			default:
			      //$("#Mensaje_Montos").hide();
			       break;     
		

            } 
}



function fn_Print()
{
//alert($("#nFolioPurianual").val());
  //ar url = "../admin/SeguridadCatalogos?accion=run&rn=\EvaluacionPlurianaual.jasper&folioPlurianual='"+  $("#nFolioPurianual").val()+"'" ;



									window.open(	"../admin/SeguridadCatalogos?"
														+ "catalogo=REPORTE"
														+ "&accion=run"
														+ "&rn=EvaluacionPlurianaual.jasper"
														+ "&folioPlurianual=" +  $("#nFolioPurianual").val(),
														"popacuse",
														"scrollbars=1, resizable=yes, width=1024, height=768"
												   );



   
 																		 	
   
 
}

function fn_GuardaMatriz(){
                   //eliminar si ya hay apartado
                   queryFormPost("dContratoApartado", {async : false});
                   var iRegistrosTotales  = $("#h_DatosImportados").val();
					var Capitulo = $('#cCapitulo').val();
					var sFuncion = "GuardaMatrizDesglose";
					var nModificacion =  $("#nmod").val();
		
					 $.ajax({
					   			datatype:"json",
								type: "POST",
								url:"../servlet/ContratoPlurianualDetalleServlet",
								async: false,
								data: {sFuncion:sFuncion,nModificacion:nModificacion},
								success: function (data){
									var Ocurrio = data.Insertado
									if (Ocurrio == "1"){
									    $("#cRelizado").val("Si");
									    fn_RealizaApartado();
									    gb_Realiza = true;
									}
									else{
									
									alert(data.Insertado);
									      gb_Realiza = false;
									}
								},
								error: function (par) {alert("Error" + data);}
		                    });
		           
		           return gb_Realiza;

}


 function fn_RealizaApartado(){
      
                    $("#cRelizado").val("No");
      				var sFuncion = "RealizaApartado";
      				var Capitulo = $('#cCapitulo').val();
      
         				//eliminar si ya hay apartado
                 			queryFormPost("updatePlurianualEncabezadoApartado", {async : false});

                   $.ajax({
			   			datatype:"json",
						type: "POST",
						url:"../servlet/ContratoPlurianualDetalleServlet",
						beforeSend: function ( xhr ) {
				              $.blockUI( {	message : "Generando Apartado Espere ......" });
				        },
						data: {Capitulo:Capitulo, sFuncion:sFuncion},
						async: false,
						success: function (data){
							if (data.Insertado == 1){
							    $("#cRelizado").val("Si");
							}
							else{
						         $("#cRelizado").val("No");
               
						    }										},
						error: function (par) {alert('Ocurrio un error al efectual el Apartado');}
                    });
 
 			setTimeout($.unblockUI, 3000);
        
 
 }
 
 
 
 function fn_CancelaApartado(){
        var sFuncion = "CancelaApartado";
         $("#cRelizado").val("");
        var Capitulo = $('#cCapitulo').val();
        var iRegistrosTotales  = $("#h_DatosImportados").val();
	                                 
                                    
 
  $.ajax({
							   			datatype:"json",
										type: "POST",
										url:"../servlet/ContratoPlurianualDetalleServlet",
										data: {Capitulo:Capitulo, sFuncion:sFuncion,nFolioPoliza:$("#nFolioPoliza").val()},
										async: false,
										beforeSend: function ( xhr ) {
								              $.blockUI( {	message : "Liberando Apartado Espere ......" });
								        },
										success: function (data){
											if (data.Insertado == 1){
											    $("#cRelizado").val("Si");
											    setTimeout($.unblockUI, 300 * iRegistrosTotales );
											} 
											else{
											alert(data.Insertado);
										         $("#cRelizado").val("No");
										         setTimeout($.unblockUI, 300 * iRegistrosTotales );			
										    }
   										},
										error: function (par) {alert('Ocurrio un error al Cancelar Apartado');}
				                    });
 
 							       
 
 
 }
 
 
 
 
 
function VerificaTiposDePlurianualesDisponibles()
{
			        	                                                                                    
			var zTabla = "ESTATUS_TIPOS_PLURIANUALES";
			var camposWhere = ""; 
			var param = "";
			var sOrder = "";
			
																							
			$.getJSON("../catalogos/SelectJson.jsp?"+ new Date().getTime(),{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){ 
											
				 var pluriNormal;
				 var pluriEspecial;
	   				
	   			if (j.length > 0){
					pluriNormal = j[0].Col0;
	   				pluriEspecial = j[0].Col1;
				} 
				    	
				var seleccionar;
				if($("#h_FechaF").val() != "")
					seleccionar = 0;
				else
					seleccionar = 1;    	
				
	         	          
	         	if((pluriNormal == 1) && (pluriEspecial == 1)){
	         		alert("El flujo para contratos plurianuales se encuentra deshabilitado.");
	         		fn_Deshabilita();
	         	}
	         	if (pluriNormal == 0 && pluriEspecial == 0){
	         		$("#desbloqueado_bEspecial").val(-1);
	         		return;	
	         	}
	         	if (pluriNormal == 0 && pluriEspecial == 1){
	         		if(seleccionar==1){
		         		$("#bEspecial").val(0);
		         		$("#h_bEspecial").val(0);
	         		}
	         		$("#desbloqueado_bEspecial").val(0);
	         		$("#bEspecial").prop("disabled","true");
	         	}
	         	if (pluriNormal == 1 && pluriEspecial == 0){
	         		if(seleccionar==1){
		         		$("#bEspecial").val(1);
		         		$("#h_bEspecial").val(1);
	         		}
	         		$("#desbloqueado_bEspecial").val(1);
	         		$("#bEspecial").prop("disabled","true");
	         	} ;          
	         	            
	         });
};


 
function fn_Limpia(){
	$("#cOperacion").val("");
	$("#cFolioMASCP").val("");
	$("#TipoMovimiento").val("");
	$("#fInicio").val("");
	$("#fFin").val("");
	$("#Monto_Total").val("");
	$("#Monto_Total_Pesos").val("");
	$("#Monto_Total_Contrato").val("");
	$("#cMotivoRechazo").val("");
	$("#cFundamento").val("");
	$("#cDescripcion").val("");
	$("#cJustificacion_compromiso").val("");
	$("#ImporteComparativoPlurianual").val("");
	$("#cJustificacion_plazo").val("");
	$("#tTotal").val("");
	$("#MSG").val("");

 }
 
 
	function fn_maxLen(text, maxLen,NameText) {
         if (text.value.length > maxLen) {
           alert ("La descripción del campo " + NameText + " debe ser máximo de " + maxLen + " caractéres");
           text.focus();
           return false;
     }
     return true;    
}
 

function fn_SeleccionTipoPlurianual(){
	var seleccion =$("#bEspecial").val();
	$("#h_bEspecial").val(seleccion);
	if(seleccion=="0")
		$("#Select_Solicitud").val("PLU");
	else
		$("#Select_Solicitud").val("ESP");
		
}



</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato" method="Post"
		action="../servlet/ContratoPlurianualServlet"
		enctype="multipart/form-data">
		<!--   variables de registro -->
		<input type="hidden" name="nFolioPurianual" id="nFolioPurianual"
			value="<%=nFolioPurianual%>" /> <input type="hidden" name="h_EP"
			id="h_EP" /> <input type="hidden" name="h_Mes" id="h_Mes" /> <input
			type="hidden" Id="h_Bandera" name="h_Bandera"> <input
			type="hidden" Id="h_folio" name="h_folio"> <input
			type="hidden" Id="h_lAbierto" name="h_lAbierto"> <input
			type="hidden" Id="h_cTipoSolicitud" name="h_cTipoSolicitud">
		<input type="hidden" Id="h_select_TipoGasto" name="h_select_TipoGasto">
		<input type="hidden" Id="h_Select_TipoContrato"
			name="h_Select_TipoContrato"> <input type="hidden"
			Id="h_Select_Estatus" name="h_Select_Estatus"> <input
			type="hidden" Id="h_fAplicacion" name="h_fAplicacion"> <input
			type="hidden" Id="h_FechaI" name="h_FechaI"> <input
			type="hidden" Id="h_FechaF" name="h_FechaF"> <input
			type="hidden" Id="Arreglo_Anio" name="Arreglo_Anio"> <input
			type="hidden" Id="Arreglo_Montos" name="Arreglo_Montos" size="50">
		<input type="hidden" Id="h_aEjercicioFiscal" name="h_aEjercicioFiscal">
		<input type="hidden" Id="h_ctipoMoneda" name="h_ctipoMoneda">
		<input type="hidden" Id="h_nTipoCambio" name="h_nTipoCambio">
		<input type="hidden" Id="h_cDescripcion" name="h_cDescripcion">
		<input type="hidden" Id="h_cJustificacionCompromiso"
			name="h_cJustificacionCompromiso"> <input type="hidden"
			Id="h_cJustificacionPlazo" name="h_cJustificacionPlazo"> <input
			type="hidden" Id="h_cFundamento" name="h_cFundamento"> <input
			type="hidden" Id="h_cUsuarioCaptura" name="h_cUsuarioCaptura">
		<!--   variables de operacion -->
		<input type="hidden" name="OPERADOR"> <input type="hidden"
			name="FECHA_DOCUMENTO"> <input type="hidden"
			name="EJERCICIO_FISCAL"> <input type="hidden" name="MONEDA">
		<input type="hidden" name="StrFiltro"> <input type="hidden"
			Id="h_nConsecutivo" name="h_nConsecutivo"> <input
			type="hidden" Id="h_nMonto" name="h_nMonto"> <input
			type="hidden" Id="h_nMontoMin" name="h_nMontoMin"> <input
			type="hidden" Id="h_nMontoMax" name="h_nMontoMax"> <input
			type="hidden" Id="h_nPeso" name="h_nPeso"> <input
			type="hidden" Id="h_nPesoMin" name="h_nPesoMin"> <input
			type="hidden" Id="h_nPesoMax" name="h_nPesoMax"> <input
			type="hidden" Id="h_nAvance" name="h_nAvance"> <input
			type="hidden" Id="h_Temporal" name="h_Temporal"> <input
			type="hidden" Id="h_nMontoTotal" name="h_nMontoTotal"> <input
			type="hidden" Id="h_nMontoTotalCalculado"
			name="h_nMontoTotalCalculado"> <input type="hidden"
			Id="h_nMontoTotal_Pesos" name="h_nMontoTotal_Pesos"> <input
			type="hidden" Id="h_nMontoTotalMin" name="h_nMontoTotalMin">
		<input type="hidden" Id="h_nMontoTotalMax" name="h_nMontoTotalMax">
		<input type="hidden" Id="h_nPesoTotal" name="h_nPesoTotal"> <input
			type="hidden" Id="h_nPesoTotalMin" name="h_nPesoTotalMin"> <input
			type="hidden" Id="h_nPesoTotalMax" name="h_nPesoTotalMax"> <input
			type="hidden" Id="h_nAvanceTotal" name="h_nAvanceTotal"> <input
			type="hidden" Id="h_nAnioFiscal" name="h_nAnioFiscal"> <input
			type="hidden" Id="h_Existe" name="h_Existe"> <input
			type="hidden" Id="h_DetalleRecuperado" name="h_DetalleRecuperado">
		<input type="hidden" Id="h_cCentroContable" name="h_cCentroContable">
		<input type="hidden" Id="h_cRamo" name="h_cRamo"> <input
			type="hidden" Id="h_cUnidadResponsable" name="h_cUnidadResponsable">
		<input type="hidden" Id="h_nImporte" name="h_nImporte"> <input
			type="hidden" Id="h_MontoTotalDisponible"
			name="h_MontoTotalDisponible"> <input type="hidden"
			Id="h_InporteTotalPropuesto" name="h_InporteTotalPropuesto">
		<input type="hidden" Id="h_Monto_Total_Contrato"
			name="h_Monto_Total_Contrato"> <input type="hidden"
			Id="h_MontoCalculadoFiscal" name="h_MontoCalculadoFiscal"> <input
			type="hidden" Id="h_cFolioMASCP" name="h_cFolioMASCP"> <input
			type="hidden" Id="h_ImporteComparartivoAnual"
			name="h_ImporteComparartivoAnual"> <input type="hidden"
			Id="h_TotalAnios" name="h_TotalAnios"> <input type="hidden"
			Id="h_nOperacion" name="h_nOperacion"> <input type="hidden"
			Id="h_total" name="h_total"> <input type="hidden"
			Id="hTotaldoctos" name="hTotaldoctos"> <input type="hidden"
			Id="hNombredocto" name="hNombredocto"> <input type="hidden"
			name="hTitulo" id="hTitulo" value="PLURIANUALPRE" /> <input
			type="hidden" Id="FOLIO" name="FOLIO" value="<%=c.getFolio()%>">
		<input type="hidden" Id="StrFiltro" name="StrFiltro"> <input
			type="hidden" Id="TipoObra" name="TipoObra"> <input
			type="hidden" Id="cTipoGasto" name="cTipoGasto"> <input
			type="hidden" Id="cCapitulo" name="cCapitulo"> <input
			type="hidden" Id="cPartida" name="cPartida"> <input
			type="hidden" Id="cCveTipoContrato" name="cCveTipoContrato">
		<input type="hidden" Id="valTipoGasto" name="valTipoGasto"> <input
			type="hidden" Id="Ocultacolumnas" name="Ocultacolumnas" value="No">
		<input type="hidden" Id="cConcepto" name="cConcepto" value="No">
		<input type="hidden" Id="mes" name="mes" value="<%=mesActual%>">
		<input type="hidden" name="cUnidadResponsableContable"
			id="cUnidadResponsableContable"
			value="<%=cUnidadResponsableContable%>" /> <input type="hidden"
			value="" id="cDocumentoHaplicado" name="cDocumentoHaplicado">
		<input type="hidden" value="" id="nFolioPoliza" name="nFolioPoliza">
		<input type="hidden" value="" id="cAutorizado" name="cAutorizado">
		<input type="hidden" value="" id="cFolioSai" name="cFolioSai"
			value="<%=c.getFolio()%>"> <input type="hidden" value=""
			id="cRelizado" name="cRelizado"> <input type="hidden"
			value="" id="cEtiqueta_a" name="cEtiqueta_a"> <input
			type="hidden" value="" id="cClasificacionGasto"
			name="cClasificacionGasto"> <input type="hidden" value=""
			id="PrimeraVez" name="PrimeraVez" value=""> <input
			type="hidden" value="" id="totalConstruccion"
			name="totalConstruccion"> <input type="hidden" value=""
			id="totalSupervision" name="totalSupervision"> <input
			type="hidden" value="" id="cEjercicioTemp" name="cEjercicioTemp">
		<input type="hidden" value="" id="Monto_por_Anio"
			name="Monto_por_Anio"> <input type="hidden" value="0"
			id="h_bEspecial" name="h_bEspecial"> <input type="hidden"
			value="-1" id="desbloqueado_bEspecial" name="desbloqueado_bEspecial">
		<input type="hidden" value="0" id="nModificacion" name="nModificacion">
		<input type="hidden"  id="nmod" name="nmod">
		<input type="hidden" id="valContra" name="valContra">


		<div id="container" class="container" style="width:90%">
			<h1 id="titulo"><label>Solicitud de Contratos Plurianuales y Especiales</label></h1>
			<table>
				<tr>
					<td width="500px"></td>
					<td align="right">Año Fiscal:</td>
					<td><input style="width: 168px" name="cEjercicio" type="text"
						id="cEjercicio" size="17" class="notEditable" readonly="readonly"
						style="text-align:center" value="<%=cEjercicio%>">
					</td>
					<td><input style="width: 168px" name="cOperacion" type="text"
						id="cOperacion" size="17" class="notEditable" readonly="readonly"
						style="text-align:center">
					</td>
				</tr>
			</table>
			<br />
			<div id="flsetCargaArchivo"
				style="border:1px solid grey;padding:5px;">
				<div class="encabezado"
					style="position:relative;background-color:white;top:-15px;left:15px;z-index:1;cursor:pointer;width:100px;padding-left:5px;">Cargar
					Archivo</div>
				<div class="contenido">
					<table border="0">
						<tr>
							<td><input type="file" size="30" name="fileUpload"
								id="fileUpload"></td>
							<td><input type="button" value="Cargar Archivo"
								id="fileUploadButton" onclick="Fn_CargaExcel()"></td>
							<td><a id="plantilla" href="#"
								onclick="window.open('../Reportes/PlantillaPlurianuales.xls')">
									Plantilla</a></td>
							<td></td>
						</tr>
						<%
							if (msg.length() > 1) {
						%>
						<tr>
							<td align="left" colspan="7">Mensaje CargaExcel: <input
								type="hidden" Id="MSG" name="MSG" size="180" readonly="readonly"
								value="<%=msg%>">
							</td>
						</tr>
						<%
							}
						%>

					</table>
				</div>
			</div>
			<br />
			<div id="flsetCaptura" style="border:1px solid grey;padding:5px;">
				<div class="encabezado"
					style="position:relative;background-color:white;top:-15px;left:15px;z-index:1;cursor:pointer;width:55px;padding-left:5px;">
					Captura</div>
				<div class="contenido">
					<table width="100%" height="190" border="0">
						<tr>
							<td align="right">Folio SAI :</td>
							<td><input style="width: 168px" name="folio" type="text"
								id="folio" size="17" class="notEditable" readonly="readonly"
								style="text-align:center" value="<%=c.getFolio()%>"></td>
							<td align="right"><span id="spanFolioMASCP">* Folio
									MASCP:</span></td>
							<td><input style="width: 168px" id="cFolioMASCP"
								name="cFolioMASCP" type="text" maxlength="20"
								style="text-align:left" size="17">
							</td>

							<td align="right">Recurso:<input readonly type="text"
								id="TipoMovimiento" name="TipoMovimiento" readonly="readonly"
								class="notEditable" value="">
							</td>


						</tr>

						<tr>
							<td align="right" class="calendar_1">* Fecha Inicio:</td>
							<td style="padding-top: 3px" class="calendar_2"><input
								title="Se captura la Fecha Inicio del Contrato" type="text"
								id="fInicio" name="fInicio" readonly="readonly" size="10"
								maxlength="10">
							</td>
							<td align="right" class="calendar_1">* Fecha Fin:</td>
							<td style="padding-top: 3px" class="calendar_2"><input
								title="Se captura la Fecha fin del Contrato" type="text"
								id="fFin" name="fFin" readonly="readonly" size="10"
								maxlength="10">
							</td>
							<!-- <td><input style="width: 150px" align="right" type="text"  id="fFin" name="FFin"
								readonly="readonly" maxlength="10" size="17" />
							</td>-->


						</tr>
						<tr>
							<td align="right">* Contrato :</td>
							<td><input type="radio" id="lAbierto" name="AC" value="A"
								Onclick="fn_Select('A');">Abierto&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" id="lCerrado" name="AC" value="C"
								Onclick="fn_Select('C');">Cerrado</td>
							<td align="right">* Tipo de Gasto:</td>
							<td height="10px"><select style="width: 170px"
								id="select_TipoGasto" name="select_TipoGasto"
								onChange="fn_SeleccionGasto();">
							</select></td>

						</tr>
						<tr>
							<td align="right">* Solicitud:</td>

							<td><select style="width:170px" id="bEspecial"
								name="bEspecial" onChange="fn_SeleccionTipoPlurianual();">
									<option value="0">Plurianual</option>
									<option value="1">Especial</option>
							</select> <select style="WIDTH: 170px; visibility: hidden;"
								id="Select_Solicitud" name="Select_Solicitud">
							</select>
							</td>

							<td align="right">* Tipo de Contrato:</td>
							<td align="left"><select style="width: 170px"
								id="Select_TipoContrato" name="Select_TipoContrato">
							</select>
							</td>
							<!--  
	                          <td>	<input	type="button" value="Cancelar Apartado"   name="CancelaApartado" id="CancelaApartado" onclick="fn_CancelaApartado();" />
							</td>
	                         -->

						</tr>

						<tr>
							<td align="right">* Tipo de Moneda:</td>
							<td><select style="width: 170px" id="Select_TipoMoneda"
								name="Select_TipoMoneda" onChange="fn_Seleccione();">
							</select>
							</td>

							<td align="right">* Tipo de Cambio:</td>
							<td><input style="width: 168px" type="text" Id="TipoCambio"
								name="TipoCambio" onChange="fn_ObtenMonto()"
								onKeyPress="return onlyNumberss(event)">
							</td>
						</tr>
						<tr>
							<td align="right">* Importe año actual:</td>
							<td><input class="notEditable" type="text" Id="Monto_Total"
								name="Monto_Total" size="23" onblur="fn_ObtenMonto()"
								readonly="readonly">
							</td>
							<td align="right">* Monto total (Pesos) por Ejercicio:</td>
							<td><input type="text" Id="Monto_Total_Pesos" size="23"
								readonly="readonly" class="notEditable" name="Monto_Total_Pesos"
								onKeyPress="return onlyNumberss(event)">
							</td>
						</tr>
						<tr>
							<td align="right">* Monto total por Contrato:</td>
							<td><input type="text" Id="Monto_Total_Contrato"
								readonly="readonly" class="notEditable"
								name="Monto_Total_Contrato" size="23" onblur="fn_ObtenMonto()"
								onKeyPress="return onlyNumberss(event)">
							</td>
							<td align="right">* Estatus:</td>
							<td><select style="width: 170px" id="Select_Estatus"
								name="Select_Estatus">
							</select>
							</td>

							<!--  <td align="right">	
							 	<input	type="button" value="Imprime Reporte" style="visibility:visible"  name="print" id="print" onclick="fn_Print();" />	
								</td>-->

						</tr>
					</table>
				</div>
			</div>
			<div id="autoriza">
				Autoriza: <input name="checkbox" id="checkbox" type="checkbox"
					value="1" checked="checked" onclick="validaCheck();" />
			</div>
			<div id="motivoRechazo">
				Motivo del rechazo: <label class="textareaContainer"> <textarea
						id="cMotivoRechazo" name="cMotivoRechazo" rows="3" cols="50"></textarea>
				</label>
			</div>
			<br></br>
			<div id="tabs">

				<ul>
					<li><a href="#tabs-0">Fundamento y Motivación</a>
					</li>
					<li><a href="#tabs-1">a) Especificación </a>
					</li>
					<li><a href="#tabs-2">b) Justificación Ventajas Económicas</a>
					</li>
					<li><a href="#tabs-3">c) Justificacion del plazo</a>
					</li>
					<li><a href="#tabs-4" onclick="fnVerGrid('desglose');">d)
							Desglose del gasto </a>
					</li>
					<li><a href="#tabs-5" onclick="fnVerGrid('montos');">e)
							Importes Anuales</a>
					</li>
				</ul>
				<div id="tabs-0">
					<label class="textareaContainer"> <textarea
							id="cFundamento" name="cFundamento" rows="7"></textarea> </label>
				</div>
				<div id="tabs-1">
					<p>Nombre del Contrato:</p>
					<label class="textareaContainer"> <textarea
							id="cDescripcion_Corta" name="cDescripcion_Corta" rows="3"
							onBlur="fn_maxLen(this,600,'Descripción Corta');"></textarea> </label>
					<p>Especificación:</p>
					<p>
						<label class="textareaContainer"> <textarea
								id="cDescripcion" name="cDescripcion" rows="7"
								onBlur="fn_maxLen(this,4000,'Descripción Larga');" ></textarea>
						</label>
					</p>
					<label> Para la plurianualidad se tiene programado la
						aplicación de recursos correspondientes a <input
						style="width: 200px" class="notEditable" readonly="readonly"
						type="text" Id="tTipo" name="tTipo">. </label>
				</div>
				<div id="tabs-2">
					<label class="textareaContainer"> <textarea
							id="cJustificacion_compromiso" name="cJustificacion_compromiso"
							rows="8"
							onBlur="fn_maxLen(this,4000,'Justificación del compromiso')"></textarea>
					</label>
					<p>
						<br>COMPARA VALORES <br> <br>* Plurianual Maximo :
						<input type="text" class="notEditable" readonly="readonly"
							id="ImporteComparativoPlurianual"
							name="ImporteComparativoPlurianual"></input> *Valor Anual por
						número de Años:<input type="text" id="ImporteComparartivoAnual"
							name="ImporteComparartivoAnual" onblur="fn_ObtenMonto()"
							onKeyPress="return onlyNumberss(event)"></input>
					</p>
				</div>
				<div id="tabs-3">

					<label class="textareaContainer"> <textarea
							id="cJustificacion_plazo" name="cJustificacion_plazo" rows="8"
							onBlur="fn_maxLen(this,4000,'Justificación del plazo')"></textarea>
					</label>
					<p>
						<label class="textareaContainer"> <textarea
								id="cEtiqueta_c" name="cEtiqueta_c" rows="4" cols="160"
								onBlur="fn_maxLen(this,4000,'Etiqueta Justificación del plazo')"></textarea>
						</label>
					</p>

				</div>
				<div id="tabs-4" >
					<table border="0" align="right">
						<tr>
							<td align="right">Seleccione el Año:</td>
							<td align="center"><select style="width: 170px"
								id="Select_Anio" name="Select_Anio" onChange="BloqueaGrid()"></select>
							</td>
							<td align="right" style="visibility: hidden;">Total de Renglones</td>
							<td align="center"><input type="text" readonly="readonly"
								class="notEditable" Id="h_DatosImportados" style="visibility: hidden;"
								name="h_DatosImportados">
							</td>

						</tr>
						<tr>
							<td colspan="4">
								<table id="dt_Calendario" class="display" border="0">
									<CAPTION>IMPORTE POR CLAVES PRESUPUESTALES</CAPTION>
									<thead>
										<tr>
											<th id="Ciclo">Ciclo</th>
											<th id="CLAVE PRESUPUESTAL">Ep</th>
<!-- 											<th id="Construccion">Construcción</th> -->
<!-- 											<th id="Inversion">Supervisión</th> -->
											<th id="Importe1">Importe Requerido</th>
											<th id="Importe2">Importe Modificado</th>
										</tr>
									</thead>
									<tbody>
									</tbody>

								</table>
							</td>
						</tr>
						<tr>
							<td colspan="4">

								<table border="0" align="right">
									<tr>
										<td>Total Modificado anual:</td>
										<td><input type="text" id="tTotal" name="tTotal"/>
										</td>
									</tr>
									<tr>
										<td colspan="2"><input name="chkDesglose"
											id="chkDesglose" type="checkbox" value="1" checked="checked"
											onclick="fn_Muestra('Desglose');" /> 
											<label id="Mensaje_Desglose">Oculta
												Mensaje</label>
											<div id="Mensaje_Montos">
												<label class="textareaContainer"> <textarea
														id="cEtiqueta_e" name="cEtiqueta_e" rows="10" cols="160"
														onBlur="fn_maxLen(this,4000,'Etiqueta Importes')"></textarea>
												</label>
											</div>
										</td>
									</tr>



								</table>
							</td>
						</tr>
					</table>

					<div style="display: none;">
						<table id="dt_clavepresup" class="display" border="0">
							<CAPTION align="Left">
								<EM>REGISTROS IMPORTADOS DEL EXCEL</EM>
							</CAPTION>
							<thead>
								<tr>
									<th>Ciclo</th>
									<th>EP</th>
									<th>Importe</th>

								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>

				<div id="tabs-5" style="width:50%">
					<table align="center">

						<tr>

							<td align="Left" scope="col"><input type="button"
								value="Genera Registros" style="visibility:visible"
								name="Mostrar" id="Mostrar" onclick="fn_CreaTabla();" />
							</td>


						</tr>


					</table>

					<table id="dt_Montos" class="display" border="0">
						<CAPTION>IMPORTES ANUALES</CAPTION>
						<thead>
							<tr>
								<th id="ColumnaAño" scope="col">Año</th>
								<th id="ColumnaMonto">Monto</th>
								<th id="ColumnaMontoMin">Monto Mín.</th>
								<th id="ColumnaMontoMax">Monto Max.</th>
								<th id="ColumnaAvance">Avance</th>
							</tr>
						</thead>
					</table>
					<br></br>
				</div>







			</div>
			<!--  fin de todos los tabs -->
		</div>
	</form>


</body>
</html>