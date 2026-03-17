<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
String cCentroContable = "", mensaje = "";

if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

int id_oper = -1;
if (request.getParameter("id_oper") != null){
	id_oper = new Integer(request.getParameter("id_oper")).intValue();
}else{
	id_oper = c.getCasoOperacion(0).getIdOperacion();
}

String DATE_FORMAT = "dd/MM/yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today = sdf.format(c1.getTime());

if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
}
if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
	mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}

String cUR = usuario.getU_UR();
String u_login = usuario.getLogin();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
       
    <title>Registro Diario Banco</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
		.lectura { 	background-color: #E9E9E9;  }
		
		.tbl{
				/*estilo de el cuerpo de nuestra tabla*/
				/*background-color: #F8F8FF;*/
				background-color: #F5F5F5;
				
				width:95%;
				margin:24px auto;
				border-left: 1px solid #ccc;
				border-right: 1px solid #ccc;
				
				border-color: 1px solid #ccc;
				font-family:helvetica,arial,sans-serif;
				font-weight:normal;
				/*text-transform: uppercase;*/
		}
		.ui-dialog-titlebar-close {  visibility: hidden;}
		
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
	
<script type="text/javascript">	
$(document).ready( function(){
		
	$("#tabs").tabs({} );
	$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
	$("#tblGuardar").hide();
	$("#divImprime").hide();
	$("#divImprimeE").hide();
	$("#chckNuevo").click(function(){ validarfolioRegistro(); });
	$("#chckNuevoE").click(function(){ validarfolioRegistro(); });
	$("#folioIR").change(function(){ existeFolio($("#folioIR").val()); });
	$("#folioER").change(function(){ existeFolio($("#folioER").val()); });
	$("#divEliminarE").hide();
	$("#divEliminarI").hide();
	
	$("#sDocumentoOrigenI").hide(); 
	$("#sDocumentoOrigenII").hide();
	$("#sDocumentoOrigenE").hide();
	$("#sDocumentoOrigenEE").hide();
	$("#cIdCuentasBancariasE").attr("disabled","disabled");
	$("#cIdCuentasBancariasI").attr("disabled","disabled");
	
	$("#tBancosRDB").change(function(){ querySelectPost("cuentasBancariasRBD", "cuentaBancaria", {async: false });   });

	// querySelectPost("UnidadresponsableRead","sOrigenDeposito", {async: false });
	queryFormPost("cEjercicioRead",{async: false });
	
	//Para Ingreso
	querySelectPost("tRdbCat_DocumentoOrigen","sDocumentoOrigenI", {async: false });
	querySelectPost("tRdbCat_Concepto","sConceptoI", {async: false });
	querySelectPost("tRdbCat_MedioPago","sMedioPagoI", {async: false });
	querySelectPost("tRdbCat_OrigenDeposito","sOrigenDepositoI", {async: false });
	
	// Para Egreso
	querySelectPost("tRdbCat_DocumentoOrigen","sDocumentoOrigenE", {async: false });
	querySelectPost("tRdbCat_Concepto","sConceptoE", {async: false });
	querySelectPost("tRdbCat_MedioPago","sMedioPagoE", {async: false });
	querySelectPost("tRdbCat_OrigenDeposito","sOrigenDepositoE", {async: false });
	querySelectPost("tCuentasBancariasURRead", "cIdCuentasBancariasE", {async: false });
	//querySelectPost("tCuentasBancariasDURRead", "cIdCuentasBancariasDE", {async: false });
	
	querySelectPost("tCuentasBancariasURRead", "cIdCuentasBancariasI", {async: false });
	//querySelectPost("tCuentasBancariasDURRead", "cIdCuentasBancariasDI", {async: false });
	
	$("#cIDRFC").change(function(){ $("#sBeneficiario").val($("#cIDRFC").val());  $("#sConceptoI").val(0); $("#cIdCuentasBancarias").val(); $('#cIdCuentasBancariasI').append('<option value="s" selected="selected">SELECCIONE</option>'); });
	$("#cIDRFCSesion").change(function(){ $("#sBeneficiario").val($("#cIDRFCSesion").val());  $("#sConceptoE").val(0); $("#cIdCuentasBancarias").val(); $('#cIdCuentasBancariasE').append('<option value="s" selected="selected">SELECCIONE</option>'); });
	$("#sConceptoI").change(function() { $("#txtConceptoMedioPago").val($("#sConceptoI").val());  querySelectPost("tRdbCat_ConceptoMedioPago", "sMedioPagoI", {async: false });  });
	$("#sConceptoE").change(function() { $("#txtConceptoMedioPago").val($("#sConceptoE").val());  querySelectPost("tRdbCat_ConceptoMedioPago", "sMedioPagoE", {async: false }); });
	
	$("#sConceptoE").change(function(){ 
		
		//queryFormPost("tCatConceptoEventoE", {async: true });  validarBeneficiario("EGRESO"); 
		queryFormPost({	queryName : "tCatConceptoEventoE", async : false, callback : function(){ validarBeneficiario("EGRESO"); } });
	});
	
	$("#cIdCuentasBancariasE").change(function () { $("#cIdCuentasBancarias").val($("#cIdCuentasBancariasE").val());  });
	$("#cIdCuentasBancariasI").change(function () { $("#cIdCuentasBancarias").val($("#cIdCuentasBancariasI").val());  });
	
	//querySelectPost("tCuentasBancariasURRead", "cIdCuentasBancariasE", {async: false });
	//querySelectPost("tCuentasBancariasURRead", "cIdCuentasBancariasI", {async: false });
	
	$('#cIdCuentasBancariasI').append('<option value="s" selected="selected">SELECCIONE</option>');
	$('#cIdCuentasBancariasE').append('<option value="s" selected="selected">SELECCIONE</option>');
	
	$("#sConceptoI").change(function(){
		
		//queryFormPost("tCatConceptoEventoI", {async: true });  validarBeneficiario("INGRESO"); });
		queryFormPost({ queryName : "tCatConceptoEventoI", async : false, callback : function(){ validarBeneficiario("INGRESO"); } });
	
	});
	$("#tableRDB tbody").click(function(event) {
		
					$(tableRDB.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
	});
	
	$("#mImporteI").change(function(){
				
					 if($("#nuevo").val() == "nuevo"){ 
							
							$("#mImporteSinFormato").val($("#mImporteI").val()); 
							queryFormPost("importeLetra", {
							
									async: true,
									callback : function() {
											
												$("#mImporteI").formatCurrency();  
										}			
							});  
				}   
	});
	
	$("#mImporteE").change(function(){ 
					
					if($("#nuevo").val() == "nuevo"){ 
							$("#mImporteSinFormato").val($("#mImporteE").val()); 
							queryFormPost("importeLetraE", {
										
										async: true,
										callback : function() {
											
												$("#mImporteE").formatCurrency(); 
										}			
							 }); 
					}   
	});
	
	var nFolioInt = "<%=c.getFolio()%>";
	nF =  nFolioInt.split("-");
	$("#tblGuardar").hide();
	//$("#tblImprimir").hide();
	$("#btnBuscar").click(function(){ buscarInformacion(); });
	$("#btnAgregar").click(function(){ 
					
					$("#dialog-nuevo" ).dialog( "open" );
					//agregarInformacion();
		});
	//$("#btnImprimir").click(function(){ imprimir(); });
	
	if(<%=id_oper == 4 %> ){ campoConsulta();}
		
    var tableRDB = $('#tableRDB').dataTable({         
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
								sSearch: "Buscar:",
								oPaginate: {
									sFirst:    "Primero",
									sPrevious: "Ant.",
									sNext:     "Sigte.",
									sLast:     "&Uacute;ltimo"
								}
							},
							bServerSide: true,
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_RdbCargaArchivoRDB&qw=" + " idCargaArchivoRDB = 0 ",
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: false,
				    		iDisplayLength: 10,
				      			sScrollY: "500px", 
				      			sScrollX: "1220px",
				      			Height: "500px",
				      			Width: "1000px"
	 });	
			
	$("#tableRDB tbody").dblclick(function(event){
		
			if(confirm("¿ Desea Capturar Informacion en el Registro ?")){
				
				var aPos = tableRDB.fnGetPosition( event.target.parentNode );
     			var aData = tableRDB.fnGetData( aPos );
     			var idCargaArchivo = aData[0];
     			var folioSai = aData[1];
     			var folio = aData[2];
     			var tipoMovi = aData[10];
     			
     			var zTabla = "TRDBCARGAARCHIVO_RDB";
				var camposWhere = " WHERE idCargaArchivoRDB = " +idCargaArchivo; 
				var param = "";
				
				if(folioSai > 0 && folio != null){
				
					alert("El Movimiento Ya Esta en Proceso de Captura");
					return;
				
				}
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
													
							for(var i = 0; i < j.length; i++ ){
							
									parent.document.getElementById("pb_save").style.visibility='visible'; // Guardar doSave()
									parent.document.getElementById("pb_send").disabled=true;              // Enviar
									
									$("#tipoMovi").val(tipoMovi);
									$("#dvGrid").hide();
									$("#tabMovimiento").hide();
									$("#folioSai").val(j[i].Col14);
									
									if(tipoMovi == "INGRESO"){
									
										$("#tabIngreso").show();
										$("#dvIngreso").show();
										$("#tabIngreso").click();
										
										$("#folioI").val(j[i].Col0);
										$("#idCargaArchivoRDB").val(j[i].Col0);
										$("#sReferenciaI").val(j[i].Col1);
										$("#dFechaI").val(j[i].Col2);
										$("#mImporteI").val(j[i].Col3);
										$("#mImporteSinFormato").val(j[i].Col4);
										$("#cIDRFC").val(j[i].Col5);
										$("#cnombre").val(j[i].Col6);
										
										$("#sOrigenDepositoI").val(j[i].Col7);
										$("#sDocumentoOrigenI").val(j[i].Col8);
										$("#sConceptoI").val(j[i].Col9);
										$("#sMedioPagoI").val(j[i].Col10);
										$("#sEntidadContableI").val(j[i].Col11);
										$("#sNotasI").val(j[i].Col12);
										$("#folioIR").val(j[i].Col14);
										$("#nFolioI").val(j[i].Col16);
										$("#sSucursalPlazaI").val(j[i].Col17); 
										$("#sTipoPagoI").val(j[i].Col18);
										$("#sReferenciaPagoI").val(j[i].Col19);
										
										$("#sBancoI").val(j[i].Col20); 
										$("#sTipoBancomerI").val(j[i].Col21);
										$("#sMovimientoI").val(j[i].Col22);
										$("#sCuentaBancariaI").val(j[i].Col23);
										$("#sTransaccionDescripcionI").val(j[i].Col24);
										
										queryFormPost("importeLetra", {async: false });
										
									}else{
									
										$("#tabEgreso").show();
										$("#dvEgreso").show();
										$("#tabEgreso").click();
										
										$("#folioE").val(j[i].Col0);
										$("#idCargaArchivoRDB").val(j[i].Col0);
										$("#sReferenciaE").val(j[i].Col1);
										$("#dFechaE").val(j[i].Col2);
										$("#mImporteE").val(j[i].Col3);
										$("#mImporteSinFormato").val(j[i].Col4);
										$("#cIDRFCSesion").val(j[i].Col5);
										$("#NombreSesion").val(j[i].Col6);
										$("#sOrigenDepositoE").val(j[i].Col7);
										$("#sDocumentoOrigenE").val(j[i].Col8);
										$("#sConceptoE").val(j[i].Col9);
										$("#sMedioPagoE").val(j[i].Col10);
										$("#sEntidadContableE").val(j[i].Col11);
										$("#sNotasE").val(j[i].Col12);
										$("#folioIR").val(j[i].Col14);
										$("#nFolioE").val(j[i].Col16);
										$("#sSucursalPlazaE").val(j[i].Col17);
										$("#sTipoPagoE").val(j[i].Col18);
										$("#sReferenciaPagoE").val(j[i].Col19);
										
										$("#sBancoE").val(j[i].Col20); 
										$("#sTipoBancomerE").val(j[i].Col21);
										$("#sMovimientoE").val(j[i].Col22);
										
										$("#sCuentaBancariaE").val(j[i].Col23);
										$("#sTransaccionDescripcionE").val(j[i].Col24);
										
										queryFormPost("importeLetraE", {async: false });
									
									}
							}
				});
		    }	
	});
		
	$("#fecha").datepicker({
			showOn: "button",
			dateFormat:"dd/mm/yy",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
	});
	
	$("#fecha").val("<%=today%>");
	
	$( "#dialog" ).dialog({
		
				autoOpen: false,
				height: 280,
				width: 550,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							
							guardarDocumentoSoporte();
							$( this ).dialog( "close" );
						},
						
						"Cancelar": function() {
							
							 $("#tblDocumentoSoporte").remove();
							 $( this ).dialog( "close" );
							
						}
				},
				close: function() {
						
						$("#tblDocumentoSoporte").remove();
				}							
	});
	
	$( "#dialog-nuevo" ).dialog({
		
				autoOpen: false,
				height: 280,
				width: 550,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							
							agregarInformacion();
							
						},
						
						"Cancelar": function() {
							
							 
							 $( this ).dialog( "close" );
							
						}
				},
				close: function() {
						
						$("#tblDocumentoSoporte").remove();
				}							
	});
	
	$('#dialog-carga').dialog({
	    autoOpen: false,
	    modal: true,
	    resizable: false,
	    width: 230,
	    heigth: 135,
	    title: 'Guardando Informacion',
	    show: "blind",
	    hide: "scale",
	    closeOnEscape: false,
	    beforeClose: function( event, ui ) {
					return false;			
		},
	    overlay: { backgroundColor: '#FFF',
				   opacity: 6.5   
		}
	});	
});

function onPostDisplay(){
	
		parent.document.getElementById("pb_send").disabled = false;
		
		
}

function onPostSubmit(id_oper){
  		
		$("#dialog-carga" ).dialog( "open" );
		
		// Si Lo Autorizan Guardar Encabezado y Detalle
		if(id_oper == 3){
			
			guardarEncabezadoDetalle();
			
		}
  		parent.document.getElementById("pb_send").disabled=true;        
  		parent.document.getElementById("pb_leave").disabled=true;  
  		
  		return true;
}

function onLoadPlantilla(){
		
		if(<%=id_oper == 1 %>){
			parent.document.getElementById("pb_save").style.visibility='hidden'; // Guardar doSave()
			parent.document.getElementById("pb_send").disabled=true;              // Enviar
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; 
			parent.document.getElementById("pb_cancel").disabled=true;          // Descartar
			
			tipoDocumentoInformacion($("#FOLIO").val());
			
			
			/* Validacion de Ejecucion del Envio 

			parent.document.getElementById("pb_save").style.visibility='hidden';
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			$("#tabIngreso").hide();
			$("#dvIngreso").hide();
			$("#tabEgreso").hide();
			$("#dvEgreso").hide();
			$("#divImprime").hide();
			$("#divImprimeE").hide();
			$("#sEstatus").val("REVISION");

			*/
		}else if(<%=id_oper == 2 %> ){
			 
			parent.document.getElementById("pb_save").style.visibility='visible'; // Guardar doSave()
			parent.document.getElementById("pb_send").disabled=true;              // Enviar
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; 
			parent.document.getElementById("pb_cancel").disabled=true;          // Descartar
			
			$("#chckNuevo").hide();
			$("#chckNuevoE").hide();
			tipoDocumentoInformacion($("#FOLIO").val());
			$("#sEstatus").val("REVISION");
						
		}else if(<%=id_oper == 3 %> ){
		
			tipoDocumentoInformacion($("#FOLIO").val());
			
			parent.document.getElementById("pb_save").style.visibility='visible'; 
			parent.document.getElementById("pb_send").disabled=true;              
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; 
			parent.document.getElementById("pb_cancel").disabled=true; 
			$("#chckNuevo").hide();
			$("#chckNuevoE").hide();
			$("#sEstatus").val("AUTORIZACION");       
		
		}else if(<%=id_oper == 4 %> ){
		
			tipoDocumentoInformacion($("#FOLIO").val());
			
			parent.document.getElementById("pb_save").style.visibility='hidden'; 
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;   
			$("#chckNuevo").hide();
			$("#chckNuevoE").hide();
			$("#sEstatus").val("CONSULTA");
			
		}
}
	
function onSubmit(id_oper){
	
		var p = window.parent;
  		var valida_campos = true;
  		
  		try{
  				
  				p.gestion.setFolio( $("#FOLIO").val() );
  				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Apertura Cuentas Bancarias");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#fechaToday").val() );
	
				var nretval = cmdGuardar();
				
				if(nretval == "guardado") {
					
					if(<%=id_oper > 0 %>){
					
						parent.document.getElementById("pb_save").disabled=true; 
						parent.document.getElementById("pb_send").disabled=false;       
						parent.document.getElementById("pb_send").style.visibility='visible';
						//$("#tblImprimir").show();
						
						($("#tipoMovi").val() == "EGRESO") ? $("#divImprimeE").show() : $("#divImprime").show();
						
					}
				}else if(nretval == "sinConfirmar"){
					
					return false;
					
				}else{
					// sinGuardar
					alert("No se Puedo Guardar La Informacion, Intente de Nuevo");
					return false;
				}
				
		}catch(e){
				window.alert("onSubmit: Error: " + e.message);
				return false;
		}

		return valida_campos;
}

function cmdGuardar(){
	
	var valor = "sinGuardar";
	var nuevo = $("#nuevo").val();
	var sinFolio = $("#folioSai").val();
	
	if(confirm("¿ Esta Seguro de Guardar Informacion ?")){
			
			if($("#folioSai").val() == "-"){
			
				if($("#tipoMovi").val() == "INGRESO"){
					
						getNextSequenceVal({seqName: "REGISTRO_DIARIO_BANCO" , async: false, callback: setSequenceVal});
						
						$("#folioIR").val($("#folioSai").val());
						$("#nFolio").val( $("#FOLIO").val());
				}else{
				
						getNextSequenceVal({seqName: "REGISTRO_DIARIO_BANCO_EGRESO" , async: false, callback: setSequenceValE});
						$("#folioER").val($("#folioSai").val());
						$("#nFolioE").val( $("#FOLIO").val());
				}
				
			}
			
			// Si es Nuevo Inserta en caso else solo actualiza
			var checkNombre = ($("#tipoMovi").val() == "INGRESO")? "chckNuevo" : "chckNuevoE";
			
			if(nuevo == "nuevo" && (sinFolio == "-" || $("#"+checkNombre).is(":checked") ) ){
				
					
					if($("#tipoMovi").val() == "INGRESO"){
						
							$("#divEliminarI").show();						
							var fe = $("#dFechaI").val().split("/");
							fecha = fe[2]+"-"+fe[1]+"-"+fe[0];
							$("#dFechaI").val(fecha);
							$("#nFolio").val( $("#FOLIO").val());
							if($("#sBancoI").val() == "BANORTE"){ $("#dFechaOperacionValor").val(fecha) }
							
							queryFormPost({
										queryName : "tRdbCargaArchivoIRDB_Create",
										async : false,
										callback : function() {
											
												alert("Guardado Correctamente: \n - Folio Registro: "+$("#folioSai").val()+" \n - Folio SAI: "+$("#FOLIO").val());
												valor = "guardado";
										}		
							});
					}else{
						
							$("#divEliminarE").show();	
							var fe = $("#dFechaE").val().split("/");
							fecha = fe[2]+"-"+fe[1]+"-"+fe[0];
							$("#dFechaE").val(fecha);
							$("#nFolioE").val( $("#FOLIO").val());
							if($("#sBancoE").val() == "BANORTE"){ $("#dFechaOperacionValor").val(fecha) }
							
							queryFormPost({
										queryName : "tRdbCargaArchivoERDB_Create",
										async : false,
										callback : function() {
											
												alert("Guardado Correctamente: \n - Folio Registro: "+$("#folioSai").val()+" \n - Folio SAI: "+$("#FOLIO").val());
												valor = "guardado";
										}		
							});
					
					}
	
			}else{
			
					if($("#tipoMovi").val() == "INGRESO"){
							
							$("#divEliminarI").show();	
							queryFormPost({
										queryName : "tRdbCargaArchivoIRDB_Update",
										async : false,
										callback : function() {
											
												alert("Guardado Correctamente: \n - Folio Registro: "+$("#folioSai").val()+" \n - Folio SAI: "+$("#FOLIO").val());
												valor = "guardado";
										}		
							});
					}else{
					
							$("#divEliminarE").show();	
							queryFormPost({
										queryName : "tRdbCargaArchivoERDB_Update",
										async : false,
										callback : function() {
											
												alert("Guardado Correctamente: \n - Folio Registro: "+$("#folioSai").val()+" \n - Folio SAI: "+$("#FOLIO").val());
												valor = "guardado";
										}		
							});
					
					}
			}
	}else{
		
		valor = "sinConfirmar";
	}
	
	return valor;
}

function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		 if(id_oper == 1){
  			 
  			return "REVISION_RDB";
  		 
  		 }else if(id_oper == 2){
  			 
  			 return "AUTORIZACION_RDB";
  			 
  		 }else if(id_oper == 3 ){
  			 
  			 return "CONSULTA_RDB";
  		 }	
}

function OperacionSiguiente(id_oper){
		
  		if(id_oper == 1){
  			
  			return "revision_rdb";
  		
  		}else if(id_oper == 2){
  			
  			return "autorizacion_rdb";
  			
  		}else if(id_oper == 3 ){
  			
  			return "consulta_rdb";
  		}
}

function buscarInformacion(){
	
	var fecha = $("#fecha").val();
	var tipoMov = $("#movimiento").val();
	var whereTipoMov = (tipoMov != "SELECCIONE")? " AND tipoMovimiento = '"+tipoMov+"'" : "";
	
	$('#tableRDB').dataTable({         
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
								sSearch: "Buscar:",
								oPaginate: {
									sFirst:    "Primero",
									sPrevious: "Ant.",
									sNext:     "Sigte.",
									sLast:     "&Uacute;ltimo"
								}
							},
							bServerSide: true,
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_RdbCargaArchivoRDB&qw=" + "fechaCarga = '"+fecha+"' "+whereTipoMov ,
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : false,
							bRetrive: true,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
				      			sScrollY: "500px", 
				      			sScrollX: "1220px",
				      			Height: "500px",
				      			Width: "1220px",
							aaSorting: [[ 0, "asc" ]] ,
							aoColumns: [
							    { sName: "idCargaArchivoRDB", 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"50px"},
								{ sName: "folioSai",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"30px"},
								{ sName: "nFolio",				    bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"100px"},	
								{ sName: "banco",	 				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"50px"},
								{ sName: "tipoBancomer",	 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"30px"},
								{ sName: "sSucursalPlaza",	 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"50px"},
								{ sName: "sMovimiento",	 			bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"50px"},
								{ sName: "cuentaBancaria", 			bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignRight",  sWidth:"40px"},
								{ sName: "sTransaccionDescripcion",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"60px"},
								{ sName: "dFecha",					bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignLeft",  sWidth:"30px" },
								{ sName: "tipoMovimiento",  		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"22px"},
								{ sName: "mImporte",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"30px"}
							]
							
		        });	
}

function setSequenceVal(seqValue){

		//seqValue = seqValue;
		//seqValue = seqValue.substr(seqValue.length - 6);
		//seqValue = seqValue;
		$("#folioSai").val( seqValue );
}

function setSequenceValE(seqValue){

		//seqValue = seqValue;
		//seqValue = seqValue.substr(seqValue.length - 6);
		//seqValue = seqValue;
		
		$("#folioSai").val( seqValue );
}

function valida_concepto(e){
	
		var nChars = $("#aperturaCuentaComentario").val();
		nChars = nChars.length;
		tecla = (document.all) ? e.keyCode : e.which;
		
		if (tecla==8) {
			$("#nChars").val( --nChars );
			if (nChars < 0)
				$("#nChars").val( 0 );
			return true;
		}
		
		if (nChars >= 500) {
			return false;
		}
		$("#nChars").val( ++nChars );
		patron =/[A-Za-z.\d\s\\. `,$-_%&]/;
		te = String.fromCharCode(tecla);
		return true; // patron.test(te);
}

// Muestra informacion cuando se selecciona el caso en inbox
function tipoDocumentoInformacion(Folio){

	var zTabla = "TRDBCARGAARCHIVO_RDB";
	var camposWhere = " WHERE nFolio = '" +Folio +"'"; 
	var param = "";
	var datos = "sinDatos";
				
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
													
							for(var i = 0; i < j.length; i++ ){
							
									var tipoMovi = j[i].Col13;
									$("#tipoMovi").val(tipoMovi);
									$("#folioSai").val(j[i].Col14);
									$("#sBeneficiario").val(j[i].Col5);
									datos = "datos";
												
									if(tipoMovi == "INGRESO"){
									
										$("#tabIngreso").show();
										$("#dvIngreso").show();
										$("#tabIngreso").click();
										
										$("#tabEgreso").hide();
										$("#dvEgreso").hide();
										
										$("#tabMovimiento").hide();
										$("#dvGrid").hide();
										
										$("#folioI").val(j[i].Col0);
										$("#idCargaArchivoRDB").val(j[i].Col0);
										$("#sReferenciaI").val(j[i].Col1);
										$("#dFechaI").val(j[i].Col2);
										$("#mImporteI").val(j[i].Col3);
										$("#mImporteSinFormato").val(j[i].Col4);
										$("#cIDRFC").val(j[i].Col5);
										$("#cnombre").val(j[i].Col6);
										
										$("#sOrigenDepositoI").val(j[i].Col7);
										$("#sDocumentoOrigenI").val(j[i].Col8);
										$("#sConceptoI").val(j[i].Col9);
										$("#sMedioPagoI").val(j[i].Col10);
										$("#sEntidadContableI").val(j[i].Col11);
										$("#sNotasI").val(j[i].Col12);
										$("#folioIR").val(j[i].Col14);
										$("#sNombreFirmanteI").val(j[i].Col15);
										
										$("#nFolio").val(j[i].Col16);
										$("#sSucursalPlaza").val(j[i].Col17);
										$("#sTipoPagoI").val(j[i].Col18);
										$("#sReferenciaPagoI").val(j[i].Col19);
										
										$("#sBancoI").val(j[i].Col20); 
										$("#sTipoBancomerI").val(j[i].Col21);
										$("#sMovimientoI").val(j[i].Col22);
										
										$("#sCuentaBancariaI").val(j[i].Col23);
										$("#sTransaccionDescripcionI").val(j[i].Col24);
										$("#cIdCuentasBancarias").val(j[i].Col25);
										$("#cIdCuentasBancariasI").val(j[i].Col25);
										
										queryFormPost("importeLetra", {async: false });
										queryFormPost("tCatConceptoEventoI", {async: false });
										$("#txtConceptoMedioPago").val($("#sConceptoI").val());
										
										if($("#cEvento").val() == "800_50_27C" || $("#cEvento").val() == "800_50_27A"){ $("#cIdCuentasBancariasI").removeAttr("disabled","disabled"); }
										
									}else{
									
										$("#tabEgreso").show();
										$("#dvEgreso").show();
										$("#tabEgreso").click();
										
										$("#tabIngreso").hide();
										$("#dvIngreso").hide();
										
										$("#tabMovimiento").hide();
										$("#dvGrid").hide();
										
										$("#folioE").val(j[i].Col0);
										$("#idCargaArchivoRDB").val(j[i].Col0);
										$("#sReferenciaE").val(j[i].Col1);
										$("#dFechaE").val(j[i].Col2);
										$("#mImporteE").val(j[i].Col3);
										$("#mImporteSinFormato").val(j[i].Col4);
										$("#cIDRFCSesion").val(j[i].Col5);
										$("#NombreSesion").val(j[i].Col6);
										$("#sOrigenDepositoE").val(j[i].Col7);
										$("#sDocumentoOrigenE").val(j[i].Col8);
										$("#sConceptoE").val(j[i].Col9);
										$("#sMedioPagoE").val(j[i].Col10);
										$("#sEntidadContableE").val(j[i].Col11);
										$("#sNotasE").val(j[i].Col12);
										$("#folioER").val(j[i].Col14);
										$("#sNombreFirmanteE").val(j[i].Col15);
										
										$("#nFolioE").val(j[i].Col16);
										$("#sSucursalPlazaE").val(j[i].Col17);
										$("#sTipoPagoE").val(j[i].Col18);
										$("#sReferenciaPagoE").val(j[i].Col19);
										
										$("#sBancoE").val(j[i].Col20); 
										$("#sTipoBancomerE").val(j[i].Col21);
										$("#sMovimientoE").val(j[i].Col22);
										
										$("#sCuentaBancariaE").val(j[i].Col23);
										$("#sTransaccionDescripcionE").val(j[i].Col24);
										$("#cIdCuentasBancarias").val(j[i].Col25);
										$("#cIdCuentasBancariasE").val(j[i].Col25);
										
										queryFormPost("importeLetraE", {async: false });
										queryFormPost("tCatConceptoEventoE", {async: false });
										$("#txtConceptoMedioPago").val($("#sConceptoE").val());
										
										if($("#cEvento").val() == "800_50_27C" || $("#cEvento").val() == "800_50_27A"){ $("#cIdCuentasBancariasE").removeAttr("disabled","disabled"); }
										
									}
							}
							if(datos == "sinDatos"){
								
								$("#dvGrid").show();
								$("#tabMovimiento").show();
								$("#dvGrid").click();
								$("#tabIngreso").hide();
								
							}else{
								
								parent.document.getElementById("pb_save").style.visibility='visible';
							}
				});

	$("#tabMovimiento").hide();
	$("#dvGrid").hide();
	$("#tabEgreso").hide();
	$("#dvEgreso").hide();

}
	
function campoConsulta(){

	$("#cIDRFC").attr("class", "lectura");
	$("#cIDRFC").attr("readonly", "readonly");
	
	$("#cnombre").attr("class", "lectura");
	$("#cnombre").attr("readonly", "readonly");
	
	$("#sOrigenDepositoI").attr("class", "lectura");
	$("#sOrigenDepositoI").attr("disabled", "disabled");
	
	$("#sDocumentoOrigenI").attr("class", "lectura");
	$("#sDocumentoOrigenI").attr("disabled", "disabled");
	
	$("#sConceptoI").attr("class", "lectura");
	$("#sConceptoI").attr("disabled", "disabled");
	
	$("#sMedioPagoI").attr("class", "lectura");
	$("#sMedioPagoI").attr("disabled", "disabled");
	
	$("#sNotasI").attr("class", "lectura");
	$("#sNotasI").attr("readonly", "readonly");
	
	$("#sEntidadContableI").attr("class", "lectura");
	$("#sEntidadContableI").attr("readonly", "readonly");
	
	$("#sTipoPagoI").attr("disabled", "disabled");
	$("#sReferenciaPagoI").attr("class", "lectura");
	$("#sReferenciaPagoI").attr("readonly", "readonly");
	
	$("#cIDRFCSesion").attr("class", "lectura");
	$("#cIDRFCSesion").attr("readonly", "readonly");
	
	$("#NombreSesion").attr("class", "lectura");
	$("#NombreSesion").attr("readonly", "readonly");
	
	$("#sOrigenDepositoE").attr("class", "lectura");
	$("#sOrigenDepositoE").attr("disabled", "disabled");
	
	$("#sDocumentoOrigenE").attr("class", "lectura");
	$("#sDocumentoOrigenE").attr("disabled", "disabled");
	
	$("#sConceptoE").attr("class", "lectura");
	$("#sConceptoE").attr("disabled", "disabled");
	
	$("#sMedioPagoE").attr("class", "lectura");
	$("#sMedioPagoE").attr("disabled", "disabled");
	
	$("#sNotasE").attr("class", "lectura");
	$("#sNotasE").attr("readonly", "readonly");
	
	$("#sEntidadContableE").attr("class", "lectura");
	$("#sEntidadContableE").attr("readonly", "readonly");
	
	$("#sTipoPagoE").attr("disabled", "disabled");
	$("#sReferenciaPagoE").attr("class", "lectura");
	$("#sReferenciaPagoE").attr("readonly", "readonly");
	
	$("#divImprime").show();
	$("#divImprimeE").show();
	
}		
	
function agregarInformacion(){

	var tipoMovi = $("#movimientoNuevo").val();
	var banco = $("#tBancosRDB").val();
	var numCuenta = $("#cuentaBancaria").val();
	
	if(tipoMovi != "SELECCIONE" && banco != "" && numCuenta != ""){
	
			if(confirm("¿ Agregar Nuevo Movimiento ?")){
			
				$("#dialog-nuevo" ).dialog( "close" );
				
				$("#tabMovimiento").hide();
				$("#dvGrid").hide();
				$("#nuevo").val("nuevo");
				$("#folioSai").val("-"); // Para crear un nuevo folio al guardar
				$("#tipoMovi").val(tipoMovi);
				$("#ingresado").val("INGRESADO");
				$("#divImprime").hide();
				
				parent.document.getElementById("pb_save").style.visibility='visible'; // Guardar doSave()
				parent.document.getElementById("pb_send").disabled=true;              // Enviar
				parent.document.getElementById("pb_send").style.visibility='hidden';
				
				if(tipoMovi == "EGRESO"){
					
					$("#tabEgreso").show();
					$("#dvEgreso").show();
					$("#tabIngreso").hide();
					$("#dvIngreso").hide();
					$("#tabEgreso").click();
					$("#dFechaE").datepicker({ showOn: "button", dateFormat:"dd/mm/yy",  buttonImage: "images/calendar.gif", buttonImageOnly: true });
					$("#dFechaE").val("<%=today%>");
					
					$("#sBancoE").val(banco);
					$("#sCuentaBancariaE").val(numCuenta);
					
					$("#cIDRFCSesion").removeAttr("class","");
					document.getElementById("cIDRFCSesion").removeAttribute("readonly",0);
					
					$("#NombreSesion").removeAttr("class","");
					document.getElementById("NombreSesion").removeAttribute("readonly",0);
					
					$("#sReferenciaE").removeAttr("class","");
					document.getElementById("sReferenciaE").removeAttribute("readonly",0);
					
					$("#dFechaE").attr("class","");
					document.getElementById("dFechaE").removeAttribute("readonly",0);
					
					$("#mImporteE").attr("class","");
					document.getElementById("mImporteE").removeAttribute("readonly",0);
				
				}else{
				
					$("#tabIngreso").show();
					$("#dvIngreso").show();
					$("#tabEgreso").hide();
					$("#dvEgreso").hide();
					$("#tabIngreso").click();
					$("#divImprime").hide();
					
					$("#dFechaI").datepicker({ showOn: "button", dateFormat:"dd/mm/yy", buttonImage: "images/calendar.gif", buttonImageOnly: true });
					$("#dFechaI").val("<%=today%>");
					
					$("#sBancoI").val(banco);
					$("#sCuentaBancariaI").val(numCuenta);
					
					$("#cIDRFC").removeAttr("class","");
					document.getElementById("cIDRFC").removeAttribute("readonly",0);
					
					$("#cnombre").removeAttr("class","");
					document.getElementById("cnombre").removeAttribute("readonly",0);
					
					$("#sReferenciaI").removeAttr("class","");
					document.getElementById("sReferenciaI").removeAttribute("readonly",0);
					
					$("#dFechaI").attr("class","");
					document.getElementById("dFechaI").removeAttribute("readonly",0);
					
					$("#mImporteI").attr("class","");
					document.getElementById("mImporteI").removeAttribute("readonly",0);
				}
			}
	}else{
	
		alert("Ingresar Toda La Informacion");
		return;
	}
}

function imprimir(){

	window.open(	"../admin/SeguridadCatalogos?"
					+ "catalogo=RDB"
					+ "&accion=run"
					+ "&rn=RegistroDiarioBancos.jasper"
					//+ "&whereFolio=folioSai="+$("#folioSai").val(),
					+ "&whereFolio=ca.nFolio='"+$("#FOLIO").val()+"'",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768"
		 );
}

function onlyNumbers(evt) 
{
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 47) { return false; }
		return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

var linea = 0;

function documentalSoporte(){
	
	if(<%=id_oper == 4 %> ){
	
		imprimir();
			
	}else{	
	
			var camposWhere = " WHERE activo = 1 ";
			var param = "";
			$("#tblDocumentoSoporte").remove();
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TRDBCAT_DOCUMENTOSOPORTE", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
															
						var campo = '<table id="tblDocumentoSoporte" name="tblDocumentoSoporte" >';
						$("#dialog").append(campo);		
						for(var i = 0; i < j.length; i++ ){
									
									var checked = "";
									linea = linea + 1;
									idDocSoporte = j[i].Col0;
									descripcion = j[i].Col1;
									$("#descripcion").val(j[i].Col1);
																	
									queryFormPost({
										queryName : "tRdbCargaArchivoRDBDetalle_Read",
										async : false,
										callback : function() {
												
												if($("#existeDocumentoSoporte").val() == j[i].Col1){
													
													checked = "checked='checked'";
												}
										}		
									});
									
									campo += '<tr><td><input type="checkbox" name="check"  id="check_'+linea+'" '+checked+' /></td><td><input type="text" name="descrip_'+linea+'" id="descrip_'+linea+'"  value="'+descripcion+'" size="50" readonly="readonly"/></td></tr>';
												
									
									
						}
						$("#dialog").append(campo);
						var tableFin = '</table>';
						$("#dialog").append(tableFin);
						$("#dialog" ).dialog( "open" );
			});
			
			
	}
	
}

// Elimina y Guarda Informacion de Documento Soporte

function guardarDocumentoSoporte(){

	queryFormPost({ queryName : "tRdbCargaArchivoDetalleRDB_Delete", 
					async : true,
					callback : function(){
					
								for(var ii = 0; ii <= linea; ii++){
	
									if($("#check_"+ii).is(':checked')){
									
										$("#documentoSoporte").val($("#descrip_"+ii).val());
										queryFormPost({ queryName : "tRdbCargaArchivoDetalleRDB_Create", async : false });
										
									}
								}
								imprimir();
					}		 
	 });
	 
}

function limpiarRegistro(){
	
	var tipoMovi = $("#tipoMovi").val();
	var folioRegistro =  (tipoMovi == "EGRESO")?  $("#folioER").val() : $("#folioIR").val();
	
	if(confirm("Esta Seguro de Limpiar el Registro para Otra Captura")){
		
		// Actualiza informacion a null
		queryFormPost({ queryName : "tRdbCargaArchivoLimpiarRDB_Update", async : false });
		
		// Borra Documento Soporte
		queryFormPost({ queryName : "tRdbCargaArchivoDetalleRDB_Delete", async : false });
		
		// Actualiza caso a CAPTURA_RDB
		queryFormPost({ queryName : "tActualizaCASOInEg_Update", async : false });
		
		alert("El Folio Registro "+folioRegistro+" Se Puede Ingresar Para Captura Nueva");
		location.reload();
		//parent.location.href = "../caso/inbox.jsp";
		
	}
}

function validarfolioRegistro(){
	
	if($("#tipoMovi").val() == "INGRESO"){
		
			if($("#chckNuevo").is(":checked")){
				
				if(confirm("¿ Desea Ingresar Nuevo Folio Registro ?")){
					
					$("#folioIR").val("");
					$("#folioIR").removeAttr("class","");
					document.getElementById("folioIR").removeAttribute("readonly",0);
					
				}else{
				
					$("#folioIR").val("-");
					$("#folioIR").attr("class","lectura");
					document.getElementById("folioIR").attribute("readonly",0);
					$("#folioIR").attr("readonly","readonly");
					$("#chckNuevo").removeAttr("checked");	
				}
			}else{
				
					$("#folioIR").val("-");
					$("#folioIR").attr("class","lectura");
					$("#folioIR").attr("readonly","readonly");
					$("#chckNuevo").removeAttr("checked");	
			}
	}else{
		
		if($("#chckNuevoE").is(":checked")){
				
				if(confirm("¿ Desea Ingresar Nuevo Folio Registro ?")){
					
					$("#folioER").val("");
					$("#folioER").removeAttr("class","");
					document.getElementById("folioER").removeAttribute("readonly",0);
					
				}else{
				
					$("#folioER").val("-");
					$("#folioER").attr("class","lectura");
					$("#folioER").attr("readonly","readonly");
					$("#chckNuevoE").removeAttr("checked");	
				}
			}else{
				
					$("#folioER").val("-");
					$("#folioER").attr("class","lectura");
					$("#folioER").attr("readonly","readonly");
					$("#chckNuevoE").removeAttr("checked");	
			}
		
	}
}									

function existeFolio(folioRegistro){
	
	var tipoMovi = $("#tipoMovi").val();
	var campoFolReg = (tipoMovi == "INGRESO")? campoFolReg = "folioIR" : campoFolReg = "folioER";
	
	if(folioRegistro != "-"){
		
		var camposWhere = " WHERE tipoMovimiento = '"+tipoMovi+"' AND folioSai = "+folioRegistro;
		var param = "";
		var datos = "sinDatos";
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TRDBCAT_EXISTEFOLIO", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
															
						for(var i = 0; i < j.length; i++ ){
									
							datos = "datos";
														
						}
						
						if(datos == "datos"){
							
							alert("El Folio Registro Ingresado Ya Existe, Intente Con Otro");
							$("#"+campoFolReg).val("");
							$("#"+campoFolReg).focus();
							return;
							
						}
						
							camposWhere = " WHERE tipoMovimiento = '"+tipoMovi+"'";
							
							$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TRDBCAT_FOLIOMAXIMO", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
															
										for(var ii = 0; ii < j.length; ii++ ){
													
											var max = j[i].Col0;
											alert(max);							
										}
										if( folioRegistro > max ){
											
											alert("El Folio Registro Ingresado Es Mayor a los Existentes, Intente Con Otro Menor");
											$("#"+campoFolReg).val("");
											$("#"+campoFolReg).focus();
											return;
											
										}else{
											
											$("#folioSai").val($("#"+campoFolReg).val());
										}
										
							});
						
						
			});
	}
}

function guardarEncabezadoDetalle(){
	
	var f = $("#fecha").val().split("/");
	var fecha = "'"+f[2]+"-"+f[1]+"-"+f[0]+"'";
	var cTipoPoliza = $("#cTipoPoliza").val();
	var cuentaBancaria = ($("#tipoMovi").val() == "EGRESO") ? "sCuentaBancariaE" : "sCuentaBancariaI" ;
	
	// *** Para Generar Poliza Dependiendo si es DI IN *** //
	// var camposWhere = " WHERE fAplicacion = "+fecha+" AND cTipoPoliza = '"+cTipoPoliza+"' and cDocumentoHAplicado is null GROUP BY tE.fAplicacion, tE.nFolioRDB, tE.caNoRDB  ";
	
	// *** Para Guardar Una Poliza *** //
	
	var camposWhere = " WHERE fAplicacion = "+fecha+" and cDocumentoHAplicado is null GROUP BY tE.fAplicacion, tE.nFolioRDB, tE.caNoRDB  ";
	var param = "";
	$("#cMes").val(f[1]);
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"tRDBEncabezadoExiste", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
											
				($("#tipoMovi").val() == "EGRESO")? $("#sCuentaBancaria").val($("#sCuentaBancariaE").val()) : $("#sCuentaBancaria").val($("#sCuentaBancariaI").val()) ;
				$("#mImporteNegativo").val($("#mImporteSinFormato").val() * -1);
				
				if(j.length == 0){
					
					getNextSequenceVal({seqName: "REGISTRO_DIARIO_BANCO_RDB" , async: false, callback: setSequenceValca});
					$("#nDocRenglon").val(1);
					queryFormPost("tRdbEncabezado_Create", {async: false });
					queryFormPost("tRdbDetalle_Create", {async: false });
					
				/*** Ingreso ***/
					
					if($("#cEvento").val() == "800_50_27C" ){
						
						$("#cEvento").val("800_50_27A");
						$("#sCuentaBancaria").val($("#cIdCuentasBancarias").val());
						$("#nDocRenglon").val($("#nDocRenglon").val() + 1);
						
						queryFormPost("tRdbDetalle_Create", {async: false });
					}
					
				/*** Egreso ***/ //Guarda Evento 800_50_27AA para no aplicar detalle.
					
					/*if( $("#cEvento").val() == "800_50_27A"){
						
						$("#cEvento").val("");
						$("#sCuentaBancaria").val($("#cIdCuentasBancarias").val());
						$("#nDocRenglon").val($("#nDocRenglon").val() + 1);
						
						queryFormPost("tRdbDetalle_Create", {async: false });
					}*/
					
				}else{
					
					$("#nFolioRDB").val(j[0].Col1);
					$("#nDocRenglon").val(j[0].Col2);
					
					//Guarda Detalle 
					
					queryFormPost("tRdbDetalle_Create", {async: false });
					
					// Si es concepto TRASPASO ENTRE CUENTAS (INGRESO) 800_50_27C ingresa otro Detalle
					
					if($("#cEvento").val() == "800_50_27C"){
						
						$("#cEvento").val("800_50_27A");
						$("#nDocRenglon").val(parseInt($("#nDocRenglon").val()) + 1);
						$("#sCuentaBancaria").val($("#cIdCuentasBancarias").val());
					
						queryFormPost("tRdbDetalle_Create", {async: false });
					}
					
				}
	});
}

function setSequenceValca(seqValuee) 
{		
		seqValue = "000000" + seqValuee;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioRDB").val( seqValuee );
		
		seqValue = "<%=cCentroContable%>" + "RD" + $("#cEjercicio").val() + seqValue;
		$("#caNoRDB").val( seqValue );
}

function validarBeneficiario(tipoMov){
	
	var beneRFC = (tipoMov == "EGRESO") ? "cIDRFCSesion" : "cIDRFC" ;
	var beneName = (tipoMov == "EGRESO") ? "NombreSesion" : "cnombre" ;
	var concepto = (tipoMov == "EGRESO") ? "sConceptoE" : "sConceptoI" ;
	var ctaOrigen = (tipoMov == "EGRESO") ? "sCuentaOrigenE" : "sCuentaOrigenI";
	var cIdCuentasBancarias = (tipoMov == "EGRESO") ? "cIdCuentasBancariasE" : "cIdCuentasBancariasI";
	
	var cEvento = $("#cEvento").val();
	
	if(cEvento == "800_10_10" || cEvento == "800_10_19"){
		
		/*if($("#"+beneRFC).val() != "CNA890116SF2"){
			
			alert("Para Este Concepto Se Ingresa CNA890116SF2 COMISIÓN NACIONAL DEL AGUA ");
			$("#"+beneRFC).val("CNA890116SF2");
			$("#"+beneName).val("COMISIÓN NACIONAL DEL AGUA");
				
		}*/
		$("#"+ctaOrigen).attr("disabled","disabled");
		$("#"+cIdCuentasBancarias).attr("disabled","disabled");
		$("#cIdCuentasBancarias").val("");
		$("#"+ctaOrigen).val("");
			
	}else if(cEvento == "800_50_27C" || cEvento == "800_50_27AA"){
		
		$("#"+ctaOrigen).removeAttr("disabled");
		$("#"+cIdCuentasBancarias).removeAttr("disabled");
		$("#cIdCuentasBancarias").val($("#"+cIdCuentasBancarias).val());
		
	}else{ 
		
		$("#"+ctaOrigen).attr("disabled","disabled");
		$("#"+cIdCuentasBancarias).attr("disabled","disabled");
		$("#cIdCuentasBancarias").val("");
		$("#"+ctaOrigen).val("");
		
 	}
	
	/*if($("#"+beneRFC).val() != "" ){
		
		$("#"+concepto).val(0);
		
	}*/
}

//$("#cnombreI").attr("id","new_name");
//document.getElementById("cIDRFCI").setAttribute("id","cIDRFC");
//document.getElementById("cIDRFCI").removeAttribute("id","");
//$("#tabIngreso").css("display", "none");			
//$(".dvIngreso").addClass("active");		
//$("#tab2").removeClass("active");
//var fe = fecha.split("/");
//fecha = "'"+fe[2]+"-"+fe[1]+"-"+fe[0]+"'";
//var fechaWhere = (fecha != "") ? " AND fechaCarga = "fecha : "";
//<td><input type="button" value="guardar" id="btnGuardar" name="btnGuardar" onclick="cmdGuardar();"/></td>
//$("#cnombreI").attr("id","new_name");


</script>
</head>
<body id="dt_example">
	<form id="frmRDB" name="frmRDB" >
			<div id="container" class="container SyCData" style="width:1300px; align:center">
				<h1>Registro Diario de Bancos<label style="font-size: 9pt"></label></h1>
				
				<table id="tblGuardar">
   					<tr>	
   							<td><input type="hidden" name="btnBoton" id="btnBoton" value="Guardar" onclick="guardarEncabezadoDetalle();"/></td>
			   				<td>id_oper<input type="hidden" name="id_oper" id="id_oper" value="<%=id_oper%>" /></td>
			   				<td>nFolioCaso<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /></td>
			   				<td>operador<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /></td>
			   				<td>caso<input type="hidden" id="caso" name="caso" value="<%=c.getCasoOperacion(0).getOperacion()%>" /></td>
					</tr>
					<tr>		
							<td>fecha<input type="hidden" name="fechaToday" id="fechaToday" value="<%=today%>" /></td>
							<td>ejercicio<input type="hidden" name="cEjercicio" id="cEjercicio"  /></td>
							<td>idCaso<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getIdCaso()%>" /></td>
					</tr>
					<tr>		
			   				<td>importeSinF<input type="hidden" name="mImporteSinFormato" id="mImporteSinFormato" /></td>
			   				<td>sBeneficiario<input type="hidden" name="sBeneficiario" id="sBeneficiario" /></td>
			   				<td>idCargaArchivoRDB<input type="hidden" name="idCargaArchivoRDB" id="idCargaArchivoRDB" /></td>
			   				<td>sEstatus<input type="hidden" name="sEstatus" id="sEstatus" /></td>
			   				
					</tr>
					<tr>
							<td>folioSai<input type="hidden" name="folioSai" id="folioSai" /></td>
			   				<td>tipoMovi<input type="hidden" name="tipoMovi" id="tipoMovi" /></td>
			   				<td>nuevo<input type="hidden" name="nuevo" id="nuevo" /></td>
			   				<td>IngresadoNuevo<input type="text" name="ingresado" id="ingresado" /></td>
					</tr>
					<tr>
							<td>Nombre Firmante<input type="hidden" name="sNombreFirmanteE" id="sNombreFirmanteE" maxlength="100" /></td>
							<td>Nombre Firmante<input type="hidden" name="sNombreFirmanteI" id="sNombreFirmanteI" maxlength="100" /></td>
							<td>DocumentoSoporte<input type="hidden" name="documentoSoporte" id="documentoSoporte" /></td>
							<td>ExisteDocSop<input type="hidden" name="existeDocumentoSoporte" id="existeDocumentoSoporte" /></td>
							<td>descripcionDocSop<input type="hidden" name="descripcion" id="descripcion" /></td>
					</tr>
					<tr>
							<td>cBanco<input type="hidden" id="cBanco" name="cBanco" /></td>
							<td>cResponsable<input type="hidden" id="cResponsable" name="cResponsable" value="CAPTURA_RDB" /></td>
							<td>idCaso<input type="hidden" id="id_caso" name="id_caso" value="<%=c.getIdCaso()%>"  /></td>
							<td>ConceptoMedioPago<input type="hidden" id="txtConceptoMedioPago" name="txtConceptoMedioPago" /></td>
					</tr>
					<tr>
							<td>centroConta<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /></td>
							<td>cRamo<input type="hidden" id="cRamo" name="cRamo" value="16" /></td>
							<td>UR<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /></td>
							<td>u_login<input type="hidden" name="loginUsuarioCaptura" id="loginUsuarioCaptura" value="<%=u_login%>" /></td>
							<td>cEvento<input type="text" name="cEvento" id="cEvento" /></td>
							<td>nFolioRDB<input type="hidden" name="nFolioRDB" id="nFolioRDB" /></td>
							<td>caNoRDB<input type="hidden" name="caNoRDB" id="caNoRDB" /></td>
					</tr>
					<tr>
							<td>nDocRenglon<input type="hidden" name="nDocRenglon" id="nDocRenglon" /></td>
							<td>cTipoPoliza<input type="text" name="cTipoPoliza" id="cTipoPoliza" value="DI"/></td>
							<td>sCuentaBancaria<input type="hidden" name="sCuentaBancaria" id="sCuentaBancaria" /></td>
							<td>mImporteNegativo<input type="hidden" name="mImporteNegativo" id="mImporteNegativo" /></td>
							<td>cMes<input type="hidden" name="cMes" id="cMes" /></td>
							<td>cURC<input type="hidden" name="cUnidadResponsableContable" id="cUnidadResponsableContable" value="RHQ" /></td>
							<td>FechaOper<input type="hidden" name="dFechaOperacionValor" id="dFechaOperacionValor" value=""/></td>
					</tr>
					<tr>
							<td><input type="text"  id="cIdCuentasBancarias" name="cIdCuentasBancarias" /></td>
					</tr>
				</table>
													
				<div id="tabs">
				
							<ul>
								<li><a href="#dvGrid" id="tabMovimiento">Movimientos Ingresos - Egresos</a></li>
								<li><a href="#dvIngreso" id="tabIngreso">Movimientos de Ingresos</a></li>
								<li><a href="#dvEgreso" id="tabEgreso">Movimientos de Egresos</a></li>
							</ul>
						
						<div id="dvGrid">
				
							<fieldset> 
		   								   						
			   						<table id="tblCuentaBancaria" width="500px" height ="80px" align="center">
			   						
					   						<tr>
												<td>Fecha</td><td><input type="text" name="fecha" id="fecha" size="15"></td>
												<td>Movimiento</td><td>
													<select name="movimiento" id="movimiento">
															<option value="SELECCIONE">SELECCIONE</option>
															<option value="EGRESO">EGRESO</option>
															<option value="INGRESO">INGRESO</option>	
													 </select>
												</td>
												<td><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" ></td>
												<td><input type="button" name="btnAgregar" id="btnAgregar" value="Agregar Nuevo" ></td>
											</tr>
												
									</table>
									
									<table id="tableRDB" width="1240px"  >
									<tbody>
										<thead>
											<tr align="center">
												<th>Folio Carga</th>
												<th>Folio Registro</th>	
												<th>Folio SAI</th>	
												<th>Banco</th>
												<th>COM / NETCASH</th>	
												<th>Sucursal</th>
												<th>Movimiento</th>
												<th>Cuenta Bancaria</th>
												<th>Descripcion</th>
												<th>Fecha</th>
												<th>Movimiento</th>
												<th>Importe</th>
												
											</tr>
										</thead>		
									</tbody>
									</table>
		   					
		   					</fieldset> 
				
				</div>
				<div id="dvIngreso">
						<fieldset> 
	   						<legend>Captura de Ingresos</legend>
	   						
	   							<div id="divImprime" align="right"><img src="imagenes/Imprimir.png" width="25" height="21" onClick="documentalSoporte();"></div>
		   						
		   						<table id="tblCuentaBancariaIngreso" width="900px" height ="150px" align="center" class="tbl">
		   							<tr><td colspan="6">
		   										<div id="divEliminarI" align="right"><img src="../images/b_eliminar.gif" width="25" height="25" onClick="limpiarRegistro();"></div>
		   								</td>
		   							</tr>
		   							<tr>
		   								<td>Folio Carga</td><td><input type="text" name="folioI" id="folioI" size="13" class="lectura" readonly="readonly" /></td>
		   								<td>Folio Registro</td><td><input type="text" name="folioIR" id="folioIR" size="13" class="lectura" readonly="readonly" onKeyPress="return(onlyNumbers(event))" /><input type="checkbox" id="chckNuevo" name="chckNuevo" title="Agregar Nuevo Folio"/></td>
		   								<td>Folio SAI</td><td><input type="text" name="nFolio" id="nFolio" size="13" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Banco</td><td><input type="text" name="sBancoI" id="sBancoI" size="15" class="lectura" readonly="readonly" /></td>
		   								<td>COM / NETCASH</td><td><input type="text" name="sTipoBancomerI" id="sTipoBancomerI" size="13" class="lectura" readonly="readonly" /></td>
		   								<td>Movimiento</td><td><input type="text" name="sMovimientoI" id="sMovimientoI" size="13" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Cuenta</td><td><input type="text" name="sCuentaBancariaI" id="sCuentaBancariaI" size="25" class="lectura" readonly="readonly" /></td>
		   								<td>Descripcion</td><td colspan="3"><input type="text" name="sTransaccionDescripcionI" id="sTransaccionDescripcionI" size="72" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Referencia</td><td><input type="text" name="sReferenciaI" id="sReferenciaI" maxlength="20" size="15" /></td>
		   								<td>Sucursal / Plaza</td><td><input type="text" name="sSucursalPlaza" id="sSucursalPlaza" size="13" class="lectura" readonly="readonly" /></td>
		   								<td>Fecha Banco</td><td><input type="text" name="dFechaI" id="dFechaI" size="13" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Importe</td><td><input type="text" name="mImporteI" id="mImporteI" size="25" class="lectura" readonly="readonly" onKeyPress="return(onlyNumbers(event))" /></td>
		   								<td colspan="6"><input type="text" name="letraImporteI" id="letraImporteI" size="80" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Recibimos de</td><td><input type="text" name="sBeneficiarioI" id="cIDRFC" size="25" class="AyudaSyC" readonly="readonly" /></td>
		   								<td colspan="6"><input type="text" name="beneficiarioI" id="cnombre" size="80" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Concepto</td><td><select name="sConceptoI" id="sConceptoI" style="width:300px" ></select></td>
		   								
		   								<!-- td>Medio de Pago</td--><td colspan="6"><select style="visibility: hidden" name="sMedioPagoI" id="sMedioPagoI" style="width:300px" ></select></td>
		   							</tr>
		   							<tr>
		   								<td>Cta Origen</td><td><select id="cIdCuentasBancariasI" name="cIdCuentasBancariasI"><option></option></select></td>
		   								<!-- >td>Transferir a:</td><td colspan="6"><select id="cIdCuentasBancariasDI" name="cIdCuentasBancariasDI" style="width:450px"><option></option></select></td-->
		   							</tr>
		   							<tr>
		   								<td>Origen Deposito</td><td><select name="sOrigenDepositoI" id="sOrigenDepositoI" style="width:300px" ></select></td>
		   								<td id="sDocumentoOrigenII">Documento Origen</td><td colspan="6"><select name="sDocumentoOrigenI" id="sDocumentoOrigenI" style="width:300px" ></select></td>
		   								
		   							</tr>
		   							
		   							<tr>
		   								<td>Tipo Pago</td><td><select name="sTipoPagoI" id="sTipoPagoI" />
		   															<option value="SELECCIONE">SELECCIONE</option>
		   															<option value="CHEQUE">CHEQUE</option>
		   															<option value="TRANSFERENCIA">TRANSFERENCIA</option>
		   													  </select></td>
		   								<td>Referencia Pago</td><td colspan="6"><input type="text" name="sReferenciaPagoI" id="sReferenciaPagoI" maxlength="50" size="50" />					  
		   							</tr>
		   							<tr>
		   								<td>Cuenta / Entidad Contable</td><td><input type="text" name="sEntidadContableI" id="sEntidadContableI" /></td>
		   							</tr>
		   							<tr><td>&nbsp;</td></tr>
		   						
		   						</table>
		   						
		   						<div id="divNotas" align="center">
			   							
	   									<fieldset style="width:800px;">
	   										<legend>Notas</legend>
	   						
						   							<table id="tblAutoriza" width="500px" height ="50px">
						   								<tr><td>&nbsp;</td></tr>
						   								<tr><td><textarea name="sNotasI" id="sNotasI" cols="90" rows="7" style="font-size: 10.5pt" onkeydown="return valida_concepto(event)" ></textarea></td></tr>
							   						</table>
							   						
	   				   					</fieldset>
	   				   				
   				   				</div>	
		   						
		   				</fieldset>		
					</div>
					<div id="dvEgreso" >
						<fieldset> 
	   						<legend>Captura de Egresos</legend>
	   							
	   							<div id="divImprimeE" align="right"><img src="imagenes/Imprimir.png" width="25" height="21" onClick="documentalSoporte();"></div>
		   						
		   						<table id="tblCuentaBancariaEgreso" width="900px" height ="150px" align="center" class="tbl">
		   							<tr><td colspan="6">
		   										<div id="divEliminarE" align="right"><img src="../images/b_eliminar.gif" width="25" height="25" onClick="limpiarRegistro()"></div>
		   								</td>
		   							</tr>
		   							<tr>
		   								<td>Folio Carga</td><td><input type="text" name="folioE" id="folioE" size="13" class="lectura" readonly="readonly" /></td>
		   								<td>Folio Registro</td><td><input type="text" name="folioER" id="folioER" size="13" class="lectura" readonly="readonly" onKeyPress="return(onlyNumbers(event))" /><input type="checkbox" id="chckNuevoE"/></td>
		   								<td>Folio SAI</td><td><input type="text" name="nFolioE" id="nFolioE" size="13" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Banco</td><td><input type="text" name="sBancoE" id="sBancoE" size="15" class="lectura" readonly="readonly" /></td>
		   								<td>COM / NETCASH</td><td><input type="text" name="sTipoBancomerE" id="sTipoBancomerE" size="13" class="lectura" readonly="readonly" /></td>
		   								<td>Movimiento</td><td><input type="text" name="sMovimientoE" id="sMovimientoE" size="13" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Cuenta</td><td><input type="text" name="sCuentaBancariaE" id="sCuentaBancariaE" size="25" class="lectura" readonly="readonly" /></td>
		   								<td>Descripcion</td><td colspan="3"><input type="text" name="sTransaccionDescripcionE" id="sTransaccionDescripcionE" size="72" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Referencia</td><td><input type="text" name="sReferenciaE" id="sReferenciaE" maxlength="20" size="15" /></td>
		   								<td>Sucursal / Plaza</td><td><input type="text" name="sSucursalPlazaE" id="sSucursalPlazaE" size="13" class="lectura" readonly="readonly" /></td>
		   								<td>Fecha Banco</td><td><input type="text" name="dFechaE" id="dFechaE" size="12" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Importe</td><td><input type="text" name="mImporteE" id="mImporteE" size="25" class="lectura" readonly="readonly" onKeyPress="return(onlyNumbers(event))" /></td>
		   								<td colspan="6"><input type="text" name="letraImporteE" id="letraImporteE" size="80" class="lectura" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Beneficiario</td><td><input type="text" name="sBeneficiarioE" id="cIDRFCSesion" size="25" class="AyudaSyC" readonly="readonly" /></td>
		   								<td colspan="6"><input type="text" name="beneficiarioE" id="NombreSesion" size="80" readonly="readonly" /></td>
		   							</tr>
		   							<tr>
		   								<td>Concepto</td><td><select name="sConceptoE" id="sConceptoE" style="width:300px" ></select></td>
		   								
		   								
		   								<!-- td>Medio de Pago</td --><td colspan="6"><select style="visibility: hidden" name="sMedioPagoE" id="sMedioPagoE" style="width:300px" ></select></td>
		   							</tr>
		   							<tr>
		   								<td>Cuenta Bancaria:</td><td><select id="cIdCuentasBancariasE" name="cIdCuentasBancariasE"><option></option></select></td>
		   								<!-- >td>Transferir a:</td><td colspan="6"><select id="cIdCuentasBancariasDE" name="cIdCuentasBancariasDE" style="width:450px"><option></option></select></td-->
		   							</tr>
		   							<tr>
		   								<!-- td>Aplicacion Egreso</td><td><select name="sAplicacionEgreso" id="sAplicacionEgreso" ></select></td -->
		   								<td>Aplicacion Egreso</td><td><select name="sOrigenDepositoE" id="sOrigenDepositoE" style="width:300px" ></select></td>
		   								<td id="sDocumentoOrigenEE">Documento Origen</td><td colspan="6"><select name="sDocumentoOrigenE" id="sDocumentoOrigenE" style="width:300px" ></select></td>
		   							</tr>
		   							<tr>
		   								<td>Tipo Pago</td><td><select name="sTipoPagoE" id="sTipoPagoE" />
		   															<option value="SELECCIONE">SELECCIONE</option>
		   															<option value="CHEQUE">CHEQUE</option>
		   															<option value="TRANSFERENCIA">TRANSFERENCIA</option>
		   													  </select></td>
		   								<td>Referencia Pago</td><td colspan="6"><input type="text" name="sReferenciaPagoE" id="sReferenciaPagoE" maxlength="50" size="50" />	
		   							</tr>
		   							<tr>
		   								<td>Cuenta / Entidad Contable</td><td><input type="text" name="sEntidadContableE" id="sEntidadContableE" /></td>
		   							</tr>
		   							<tr><td>&nbsp;</td></tr>
		   						
		   						</table>
		   						
		   						<div id="divNotas" align="center">
			   							
	   									<fieldset style="width:800px;">
	   										<legend>Notas</legend>
	   						
						   							<table id="tblAutoriza" width="500px" height ="50px">
						   								<tr><td>&nbsp;</td></tr>
						   								<tr><td><textarea name="sNotasE" id="sNotasE" cols="90" rows="7" style="font-size: 10.5pt" onkeydown="return valida_concepto(event)" ></textarea></td></tr>
							   						</table>
							   						
	   				   					</fieldset>
	   				   				
   				   				</div>	
		   				
		   				</fieldset>		
					</div>
					
				</div>
			</div>
			<div id="dialog" title="Documento Soporte">
						
						
			</div>
			
			<div id="dialog-nuevo" title="Agregar Movimiento">
						<table>
							<tr>
								<td>Movimiento</td>
								<td><select name="movimientoNuevo" id="movimientoNuevo">
												<option value="SELECCIONE">SELECCIONE</option>
												<option value="EGRESO">EGRESO</option>
												<option value="INGRESO">INGRESO</option>	
									 </select>
								</td>
							</tr>
							<tr>
								<td>Banco</td>
								<td><input type="text" id="tBancosRDB" name="tBancosRDB" class="AyudaSyC" readonly='readonly' /></td>
							</tr>
							<tr>
								<td>Cuenta Bancaria</td>
								<td><select id="cuentaBancaria" name="cuentaBancaria"></select></td>
							</tr>
							
						</table>
			</div>
			
			<div id="dialog-carga">
				<div id="esperar" align="center">Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
	</form>	
</body>
</html>
