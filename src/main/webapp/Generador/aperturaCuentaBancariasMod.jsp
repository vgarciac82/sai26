<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
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

String DATE_FORMAT = "yyyy-MM-dd";
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
     
    <title>Captura de Cuentas Bancarias</title>
    
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
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
	
<script type="text/javascript">	
$(document).ready( function(){
	
	$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
	$("#btnBuscar").click(function () { informacionIgresada(); });
	queryFormPost("cEjercicioRead",{async: false });
	queryFormPost("BancosRead",{async: false });
	var nFolioInt = "<%=c.getFolio()%>";
	nF =  nFolioInt.split("-");
	$("#nFolioMod").val(nF[2]);
	informacionIgresadaMod(nF[2]);
	$("#tblGuardar").hide();
	$("#tblRechazo").hide();
	
	var tableCuentas = $('#tableCuentas').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bJQueryUI": true,
        			"bInfo": false 
    });
	
	$("#aperturaCuentaFecha").datepicker({
			showOn: "button",
			dateFormat:"dd/mm/yy",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
	});
			
	$("#tableCuentas tbody").dblclick(function(event) {
		
			if(confirm("¿ Desea Quitar el Registro ?")){
				var aPos = tableCuentas.fnGetPosition( event.target.parentNode );
		     	var aData = tableCuentas.fnGetData( aPos );
		     	tableCuentas.fnDeleteRow( aPos);
		     }	
		});
	
	$("#btnAgregar").click(function(){ agregarFirmante(); });
	$("#btnPrueba").click(function(){  var nRows = $("#tableCuentas tr").length -1; alert(nRows); /*cmdGuardarDetalle();*/ /*agregarFirmante(); cmdGuardar();*/ });
	
});

function onPostDisplay(){
	
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
		
}

function onPostSubmit(id_oper){
  		
  		return true;
}

function onLoadPlantilla(){
		
		if(<%=id_oper == 1%>){
			
			parent.document.getElementById("pb_save").style.visibility='hidden';
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			
		}else{
			
			parent.document.getElementById("pb_save").style.visibility='hidden'; // Guardar doSave()
			parent.document.getElementById("pb_send").style.visibility='hidden'; // Enviar
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; // Descartar
			
		}
}
	
function onSubmit(id_oper){
	
		//validaciones del boton guardar
  if(confirm(" ¿ Guardar Modificacion a Cuentas ?")){
	  
	  	var campo = "";
		var faltaDato = "";
		$(".valida").each(function(){
				
				if($(this).val() == ""){
					
					if(this.name == "aperturaCuentaBanco"){ campo = "Nombre de Banco"; 
					}else if(this.name == "aperturaCuentaTipoCuenta"){ campo = "Tipo de Cuenta"; 
					}else if(this.name == "aperturaCuentaClabe"){ campo = "CLABE"; 
					}else if(this.name == "aperturaCuentaNumeroCuenta"){ campo = "Numero de Cuenta"; 
					}else if(this.name == "aperturaCuentaBeneficiario"){ campo = "Beneficiario"; 
					}else if(this.name == "aperturaCuentaFecha"){ campo = "Fecha"; }
					
					faltaDato = faltaDato + campo+ ', ';
				}
			
		});
		if(faltaDato != ""){
			
			alert("Debe Ingresar Los Datos: "+faltaDato);
			return;
		}
		
		var p = window.parent;
  		var valida_campos = true;
  		
  		try{
				p.gestion.setFolio( $("#FOLIOMod").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Apertura Cuentas Bancarias");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#fechaToday").val() );
	
				var nretval = cmdGuardar();
				
				if(nretval == "guardado"){
					
					p.validaEnviar();
					
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
}

function cmdGuardar(){
	
	var valor = "sinGuardar";
	
	queryFormPost("tCuentaBancariaModEncabezadoRead",{async: false });
	
	queryFormPost({
				queryName : "tCuentaBancariaModEncabezadoF",
				async : false,
				callback : function() {
					
						cmdGuardarDetalle();
						alert("Modificaciones de Cuentas Guardado Correctamente");
						valor = "guardado";
				}		
	});
	
	return valor;
}

function cmdGuardarDetalle()
{
	
	var nRows = $("#tableCuentas tr").length -1;
	var oTable = $('#tableCuentas').dataTable();
	
	queryFormPost("tCuentaBancariasInfDetModUpdate",{async: false });
	
	for( var i=0 ; i < nRows ; i++ ) {
		
		var aData = oTable.fnGetData( i );
		
		$("#guardaTipoFirmante").val(aData[1]);
		$("#guardaNombreFirmante").val(aData[2]);
		
		queryFormPost("tCuentaBancariaFirmanteModDetalle",{async: false });
		
	}
	
}
function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		 if(id_oper == 1){
  			 
  			return "AUTORIZA_CUENTAS_MOD";
  		 
  		 }
}

function OperacionSiguiente(id_oper){
		
  		if(id_oper == 1){
  			
  			return "autoriza_cuentas_mod";
  		
  		}
}

function agregarFirmante(){
	
	var tipoFirmante = $('#txtTipoFirmante').val();
	var nombreFirmante = $('#txtNombreFirmante').val();
	
	if($("#txtTipoFirmante").val() != "" && $("#txtNombreFirmante").val() != "" ){
		
		nFol = $("#nFolio").val();
		tipoFirmante = $("#txtTipoFirmante").val();
		nombreFirmante = $("#txtNombreFirmante").val(); 
		
		$("#tableCuentas").dataTable().fnAddData( [ nFol, tipoFirmante, nombreFirmante ]);
		 
		$("#txtTipoFirmante").val("");
		$("#txtNombreFirmante").val("");
		 
	}	
}

function informacionIgresada(){
	
	$("#tableCuentas").dataTable().fnClearTable();
		
	var datos = "sinDatos";
	var datosExist = "sinDatos";
	var nFolioInt = $("#buscarFolio").val();
	nF =  nFolioInt.split("-");
	parent.document.getElementById("pb_save").style.visibility='hidden';
	
	if(nF[0] == "SRCB"){
		
			var camposExist = " TOP 1 confirmacionTesofe, activo ";
			// Valida si esta cancelada, 0 = esta cancelado sin terminar, 1 = esta cancelada y terminada, 2 = no esta en proceso de cancelacion
			 	camposExist+= " ,isnull((select top 1 confirmacionTesofe from tCuentaBancariaCanEncabezado where nFolioCuentaBancaria = "+nF[2]+" order by nDocRenglon desc ),2) as procesoCancelado ";
			//var paramExist = " NFOLIOCUENTABANCARIA = "+nF[2]+ " AND activo = 1 AND (NOT EXISTS(select * from tCuentaBancariaModEncabezado where NFOLIOCUENTABANCARIA = "+nF[2]+" and activo = 1 )) ";
			var paramExist = " NFOLIOCUENTABANCARIA = "+nF[2];
			var orderExist = " ORDER BY nDocRenglon DESC ";
					
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINF", Campos:camposExist, Param:paramExist, Order:orderExist, MaxReg: "1", ajax:"true" }, function(j){
				
					for(var i = 0; i < j.length; i++ ){
					
						datosExist = "datos";
						var confirmacionTesofe = j[i].Col0;
						var activo = j[i].Col1;
						var procesoCancelado = j[i].Col2;
					}
					//alert(datosExist +" / " +confirmacionTesofe+ " / "+activo+ " / " +procesoCancelado);
					
					if(datosExist == "datos" && confirmacionTesofe == 1 && activo == 1 && procesoCancelado != 0){
										
							var campos = " TOP 1 aperturaCuentaTesofe, aperturaCuentaComentario, aperturaCuentaAutorizar, envioTesofeAutorizar, envioTesofeFolio, envioTesofeComentario, aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario,  "
							    campos += " id_oper, nFolioCuentaBancaria, nDocRenglon ";
							var param = " NFOLIOCUENTABANCARIA = "+nF[2]+" AND confirmacionTesofe = 1 AND activo = 1 ";
							var order = " ORDER BY nDocRenglon DESC ";
							
							$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINF", Campos:campos, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
																			
									for(var i = 0; i < j.length; i++ ){
										
											datos = "datos";
											$("#aperturaCuentaTesofe").val(j[i].Col0);		
											$("#aperturaCuentaComentario").val(j[i].Col1);	
											$("#aperturaCuentaAutorizar").val(j[i].Col2);
											$("#envioTesofeAutorizar").val(j[i].Col3);
											$("#envioTesofeFolio").val(j[i].Col4);
											$("#envioTesofeComentario").val(j[i].Col5);
												
											$("#aperturaCuentaBanco").val(j[i].Col6);
											$("#aperturaCuentaClabe").val(j[i].Col7);
											$("#cIDRFC").val(j[i].Col8);
											
											var fe = j[i].Col9;
											if(fe != ""){
											
												fe = fe.split("-");
												fe = fe[2]+"/"+fe[1]+"/"+fe[0];
												$("#aperturaCuentaFecha").val(fe);
											}
											
											$("#aperturaCuentaTipoCuenta").val(j[i].Col10);
											$("#aperturaCuentaNumeroCuenta").val(j[i].Col11);
											$("#cnombre").val(j[i].Col12);
											$("#id_oper").val(j[i].Col13);
											$("#nFolio").val(j[i].Col14);
											$("#nDocRenglon").val(j[i].Col15);
										
									}
									
									if(datos == "datos"){
										
										parent.document.getElementById("pb_save").style.visibility='visible';
										
										// Detalles
										var camposDet = " nFolioCuentaBancaria, tipoFirmante, nombreFirmante ";
										var paramDet = " NFOLIOCUENTABANCARIA = "+nF[2]+" AND activo = 1 ";
										var orderDet = "";
										
										$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINFDET", Campos:camposDet, Param:paramDet, Order:orderDet, MaxReg: "1", ajax:"true" }, function(j){ 
								
												for(var i = 0; i < j.length; i++ ){
										
													$("#tableCuentas").dataTable().fnAddData( [ j[i].Col0, j[i].Col1, j[i].Col2 ]);
													
												}
										});
									}else{
										
										alert("No se Encontro Informacion Con el Folio Ingresado o No Se Termino Proceso de Cuenta Bancaria ");
										return;
									}
							});
						}else if(datosExist == "datos" && confirmacionTesofe == 0 && activo == 1 && procesoCancelado == 2){
							
							alert("Se Encuentra en Proceso de Apertura");
							return;
							
						}else if(datosExist == "datos" && confirmacionTesofe == 1 && activo == 1 && procesoCancelado == 0){
							
							alert("Se Encuentra en Proceso de Cancelacion ");
							return;
							
						}else if(datosExist == "datos" && confirmacionTesofe == 0 && activo == 1){
							
							alert("El Folio Ingresado ya esta en Proceso de Modificacion ");
							return;
							
						}else if(datosExist == "datos" && activo == 0){
							
							alert("Se Encuentra Cancelado el Folio");
							location.reload();
							return;
							
						}else{
							
							alert("No Existe Folio, Intente con Otro");
							location.reload();
							return;
						}
			});
	}else{
		alert("Informacion Incorrecta");
		return;
	}
			
}

function informacionIgresadaMod(folio){
	
			var campos = " TOP 1 aperturaCuentaTesofe, aperturaCuentaComentario, aperturaCuentaAutorizar, envioTesofeAutorizar, envioTesofeFolio, envioTesofeComentario, aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario, id_oper, nFolioCuentaBancaria, autorizacionAperturaContrato, autorizacionAperturaComentario  "
			var param = " NFOLIOCUENTABANCARIAMOD = "+folio+"  AND activo = 1" ;
			var order = " ORDER BY nDocRenglon DESC ";
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOMODINF", Campos:campos, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
															
					for(var i = 0; i < j.length; i++ ){
						
							$("#aperturaCuentaTesofe").val(j[i].Col0);		
							$("#aperturaCuentaComentario").val(j[i].Col1);	
							$("#aperturaCuentaAutorizar").val(j[i].Col2);
							$("#envioTesofeAutorizar").val(j[i].Col3);
							$("#envioTesofeFolio").val(j[i].Col4);
							$("#envioTesofeComentario").val(j[i].Col5);
								
							$("#aperturaCuentaBanco").val(j[i].Col6);
							$("#aperturaCuentaClabe").val(j[i].Col7);
							$("#cIDRFC").val(j[i].Col8);
							
							var fe = j[i].Col9;
							if(fe != ""){
							
								fe = fe.split("-");
								fe = fe[2]+"/"+fe[1]+"/"+fe[0];
								$("#aperturaCuentaFecha").val(fe);
							}
							
							$("#aperturaCuentaTipoCuenta").val(j[i].Col10);
							$("#aperturaCuentaNumeroCuenta").val(j[i].Col11);
							$("#cnombre").val(j[i].Col12);
							$("#id_oper").val(j[i].Col13);
							$("#nFolio").val(j[i].Col14);
							
							if(j[i].Col16 != ""){
							
								$("#txtFolio").html(j[i].Col15);		
								$("#txtComentario").html(j[i].Col16);
								$("#tblRechazo").show();
							}
							parent.document.getElementById("pb_save").style.visibility='visible';
							$("#btnBuscar").attr("disabled","disabled");
					}
					
					// Detalles
					var camposDet = " nFolioCuentaBancariaMod, tipoFirmante, nombreFirmante ";
					var paramDet = " NFOLIOCUENTABANCARIAMOD = "+folio+" AND activo = 1";
					var orderDet = "";
					
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOMODINFDET", Campos:camposDet, Param:paramDet, Order:orderDet, MaxReg: "1", ajax:"true" }, function(j){ 
			
							for(var i = 0; i < j.length; i++ ){
					
								$("#tableCuentas").dataTable().fnAddData( [ j[i].Col0, j[i].Col1, j[i].Col2 ]);
								
							}
					});
					
			});
	
}
</script>
</head>
<body id="dt_example">
	<form id="frmCuentasBancarias" name="frmCuentasBancarias" >
			<div id="container" class="container SyCData" style="width:800px; align:center">
				<h1>Solicitud de Modificación Cuenta / Firmantes<label style="font-size: 9pt"></label></h1>
   				
   				<table id="tblGuardar">
	   				<tr>
			   				<td>id_oper_mod<input type="text" name="id_oper_mod" id="id_oper_mod" value="<%=id_oper%>" /></td>
			   				<td>nFolio_mod<input type="text" name="nFolioMod" id="nFolioMod" /></td>
			   				<td>renglon<input type="text" id="nDocRenglon" name="nDocRenglon" /></td>
			   				<td>nFolio_mod<input type="text" name="FOLIOMod" id="FOLIOMod" value="<%=c.getFolio()%>" /></td>
					</tr>
					<tr>
							<td>id_oper<input type="text" name="id_oper" id="id_oper" value="" /></td>
							<td>nFolio<input type="text" name="nFolio" id="nFolio" value="" /></td>
					</tr>
					<tr>		
							<td>operador<input type="text" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /></td>
							<td>fecha<input type="text" name="fechaToday" id="fechaToday" value="<%=today%>" /></td>
							<td>ejercicio<input type="text" name="cEjercicio" id="cEjercicio"  /></td>
							<td>cDocumento<input type="text" id="cDocumento" name="cDocumento" value="<%=c.getIdCaso()%>" /></td>
					</tr>	
					<tr>
							<td>caso<input type="text" id="caso" name="caso" value="<%=c.getCasoOperacion(0).getOperacion()%>" /></td>
							<!-- td><input type="text" value="DIRECTO" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" /></td -->
							<td>centroConta<input type="text" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /></td>
							<td>UR<input type="text" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /></td>
							<td>login<input type="text" id="usuarioLogin" name="usuarioLogin" value="<%=u_login%>" /></td>
			   		</tr>		
			   		<tr>
							<td><input type="hidden" id="aperturaCuentaAutorizar" name="aperturaCuentaAutorizar" /></td>
							<td><input type="hidden" id="aperturaCuentaTesofe" name="aperturaCuentaTesofe" /></td>
							<td><input type="hidden" id="aperturaCuentaComentario" name="aperturaCuentaComentario" /></td>
					</tr>
					<tr>		
							<td><input type="hidden" id="envioTesofeAutorizar" name="envioTesofeAutorizar" /></td>
							<td><input type="hidden" id="envioTesofeFolio" name="envioTesofeFolio" /></td>
							<td><input type="hidden" id="envioTesofeComentario" name="envioTesofeComentario" /></td>
					</tr>		
							<!-- input type="hidden" id="aperturaCuentaBanco" name="aperturaCuentaBanco" />
							<input type="hidden" id="aperturaCuentaClabe" name="aperturaCuentaClabe" />
							<input type="hidden" id="aperturaCuentaRfc" name="aperturaCuentaRfc" />
							<input type="hidden" id="aperturaCuentaFecha" name="aperturaCuentaFecha" />
							<input type="hidden" id="aperturaCuentaTipoCuenta" name="aperturaCuentaTipoCuenta" />
							<input type="hidden" id="aperturaCuentaNumeroCuenta" name="aperturaCuentaNumeroCuenta" />
							<input type="hidden" id="aperturaCuentaBeneficiario" name="aperturaCuentaBeneficiario" / -->
					<tr>		
							<td><input type="hidden" id="autorizacionAperturaAutorizar" name="autorizacionAperturaAutorizar" /></td>
							<td><input type="hidden" id="autorizacionAperturaContrato" name="autorizacionAperturaContrato" /></td>
							<td><input type="hidden" id="autorizacionAperturaComentario" name="autorizacionAperturaComentario" /></td>
					</tr>		
					<tr>
							<td><input type="hidden" id="confirmacionEnvioTesofe" name="confirmacionEnvioTesofe" /></td>
					</tr>	
					<tr>	
							<td><input type="hidden" id="enviadoTesofeAutorizar" name="enviadoTesofeAutorizar" /></td>
							<td><input type="hidden" id="enviadoTesofeFolio" name="enviadoTesofeFolio" /></td>
							<td><input type="hidden" id="enviadoTesofeComentario" name="enviadoTesofeComentario" /></td>
					</tr>
					<tr>		
							<td><input type="hidden" id="confirmacionTesofe" name="confirmacionTesofe" /></td>
							
							<td><input  type="hidden" id="guardaTipoFirmante" name="guardaTipoFirmante" /></td>
							<td><input  type="hidden" id="guardaNombreFirmante" name="guardaNombreFirmante" /></td>
					</tr>
				</table>	
				<fieldset> 
					<table id="tblBusqueda" height ="50px">
						<tr>
							<td>Numero Folio</td><td><input type="text" name="buscarFolio" id="buscarFolio" size="20" maxlength="50" /></td>
							<td><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar"/></td>
						</tr>
					</table>
				</fieldset>
				 		
   				<fieldset> 
	   				<table id="tblEstatus" height ="150px">
						<tr>
							<td>Nombre de Banco</td><td><input type="text" name="aperturaCuentaBanco" id="aperturaCuentaBanco" size="45" maxlength="50" class="valida"/></td>
							<td>Tipo de Cuenta</td><td><input type="text" name="aperturaCuentaTipoCuenta" id="aperturaCuentaTipoCuenta" size="45" maxlength="20" class="valida"/></td>
						</tr>
						<tr>
							<td>CLABE</td><td><input type="text" name="aperturaCuentaClabe" id="aperturaCuentaClabe" size=45 maxlength="20" class="valida"/></td>
							<td>Numero de Cuenta</td><td><input type="text" name="aperturaCuentaNumeroCuenta" id="aperturaCuentaNumeroCuenta" size=45 maxlength="20" class="valida"/></td>
						</tr>
						<tr>
							<td>RFC</td><td><input type="text" name="aperturaCuentaRfc" id="cIDRFC" size="25" maxlength="20" class="AyudaSyC" readonly="readonly" /></td>
							<td>Fecha Apertura</td><td><input type="text" name="aperturaCuentaFecha" id="aperturaCuentaFecha" size=15 class="valida" readonly="readonly" /></td>
						</tr>
						<tr>
							<td>Beneficiario</td><td colspan="4"><input type="text" name="aperturaCuentaBeneficiario" id="cnombre" size=73 class="valida" readonly="readonly" /></td>
						</tr>
						
					</table>
				</fieldset> 
				
				<tr><td>&nbsp;</td></tr>
   				<fieldset> 
   					<legend>Agregar Firmantes</legend>
   						
   					<table id="tblCuentaBancaria" width="800px" height ="100px" style="text-align:center">
   						
   						<tr>
							<td>Tipo Firmante</td><td><input type="text" name="txtTipoFirmante" id="txtTipoFirmante" maxlength="80"></td>
							<td>Nombre Fimante</td><td><input type="text" name="txtNombreFirmante" id="txtNombreFirmante" maxlength="100" size="70"></td>
							<td><input type="button" name="btnAgregar" id="btnAgregar" value="Agregar" ></td>
						</tr>
							
					</table>
   					
   				</fieldset> 
   					
							<table id="tableCuentas" class="display" >
									
										<thead>
											<tr align="center">
												<th>Folio</th>	
												<th>Tipo Firmante</th>	
												<th>Nombre Firmante</th>
											</tr>
										</thead>							
										
							</table>
	  				
				
				<tr><td>&nbsp;</td></tr>
   				<fieldset> 
   					<legend>Motivo de Rechazo Autorizacion de Cuentas</legend>
	   				<table id="tblRechazo">
	   				
	   						<!-- tr><td>Folio: <div id="txtFolio"></div></td></tr -->
	   				 		<tr><td>Comentario:<div id="txtComentario"></div></td></tr>
	   				
	   				</table>
	   			</fieldset> 
   			</div>
 	</form>  
</body>
</html>
