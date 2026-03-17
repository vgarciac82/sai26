<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%String Control[] = {"EjercicioFiscal","RamoEP",
			"UnidadResponsableEP",
			"GrupoFuncional","Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
			"ProgramaPresupuestario","Partida",
			"TipoGasto","FuenteFinanciamiento","EntidadFederativa","Cartera","UnidadNormativa",
			"cUnidadEjecutora"
			};%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Pago Acuerdo Ministracion De Fondos</title>
    
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
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
 
 <script type="text/javascript">
 $(document).ready(function (){
	
		$("#btnGuardar").button();
		$("#btnNuevo").button();
		$("#btnCancelar").button();
		$("#btnCXP").button();
		$("#AgregarEP").button();
				
	$("#cUnidadResponsable").val("<%=ur%>");
 	$("#fechaCaptura").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
	$("#fechaPago").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
	$("#btnGuardar").click(function(){ guardarEncDet(); $("#btnGuardar").attr("disabled","disabled"); /*guardar();*/   });
	$("#btnGuardar").attr("disabled","disabled");
	$("#btnCXP").attr("disabled","disabled");
	$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
	$("#rfcAMF").change(function(){ querySelectPost("cuentasBancarias", "cIdCuentasBancarias", {async: false }); });
	querySelectPost("tEjercicioRead", "aEjercicioFiscal", {async: false });
	querySelectPost("UnidadresponsableRead","cUnidadResponsable", {async: false });
	$("#btnCancelar").hide();
	$("#cIdCuentasBancarias1").hide();
	$("#btnNuevo").click(function(){ window.location.reload(); });
	$("#btnCancelar").click(function(){ cancelarPago(); });
	if("<%=ur%>" == "A02"){
		$("#cUnidadResponsable").show();
	}else{
		$("#cUnidadResponsable").hide();
	}
	$("#cUnidadResponsable").val("<%=ur%>");
	
	$("#AgregarEP").click(function() 
	{ 
		var ep = '<input type="text" name="ep_" id="ep_" value="'+$("#ep").val()+'" size=85 style="background-color:#E0E0F8;border:1px solid #E0E0F8" readonly="readonly">'; 
		var importe = '<input type="text" name="importe_" id="importe_" value="0.00" size=30 onKeyPress="return(onlyNumbers(event))" onchange="validarEpDetalleTable();" class="deshabilita">'; 
		agregarEPDetalleTable(ep,importe); 
		$("#btnGuardar").attr("disabled","disabled");
	});
	
	$("#clavePasivo").change(function() { 
			$(".deshabilita").attr('disabled',true);
			//$("#clavePasivo").attr("disabled","disabled");
			$("#FacturaAcuerdosAdministracion").attr("disabled","disabled");
			$("#mSaldo").attr("disabled","disabled");
			$("#numFolioAMF").attr("disabled","disabled");
			$("#fVigencia").attr("disabled","disabled");
			$("#referenciaAMF").attr("disabled","disabled");
			$("#rfcAMF").attr("disabled","disabled");
			$("#fechaCaptura").attr("disabled","disabled");
			$("#fechaPago").attr("disabled","disabled");
			$("#importePago").attr("disabled","disabled");
		    $("#btnCancelar").show();
		    $("#btnGuardar").hide();
		    $("#epDetalle1").hide();
		    $("#cIdCuentasBancarias1").show();
		    $("#cIdCuentasBancarias").hide();
		    $("#cIdCuentasBancarias1").attr("disabled","disabled");
		    $("#btnCXP").removeAttr("disabled","");
		    
		    buscarEPAMFDetalle("TREGISTRODETALLE","","clavePasivo = '"+$("#clavePasivo").val()+"'");
		    if($("#estatus").val() == "Cancelado"){ 
		    	alert("Pago Cancelado");
		    	$(".deshabilita").attr('disabled','disabled');
		    	$("#btnCancelar").attr("disabled","disabled");
		    }else{
		    	alert("Pago Activo");
		    	$(".deshabilita").attr('disabled','disabled');
		    	$("#btnCancelar").removeAttr("disabled","disabled");
		    }
		    
		 	});
	
		var oTable = $('#epPagoAMF').dataTable({         
			//iDisplayLength: 20,
			bSortClasses: false,
			ScrollY: "500px",
			sScrollX: "702px",
			bPaginate: false,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: true,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			bScrollCollapse: true,
			//sScrollXInner: "100%",
			aaSorting: [[ 1, "asc" ]] ,
			bRetrive: true,
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
				}
		});
	
		$("#epPagoAMF tbody").dblclick(function(event) {
				if(confirm("¿ Desea Eliminar Estructura Programatica ?")){
					var aPos = oTable.fnGetPosition( event.target.parentNode );
			     	var aData = oTable.fnGetData( aPos );
			     	oTable.fnDeleteRow( aPos);
			     }	
		});
	
});

function guardarEncDet()
{
	
	//Sinfrmt("importePago");
	//Sinfrmt("mSaldo");
	
	var importe =$("#importePago").val();
	var saldo = $("#mSaldo").val();
	
	if($("#FacturaAcuerdosAdministracion").val() == ""){
		alert("Agregar Clave AMF");
		return;
	}else if($("#referenciaAMF").val() == ""){
		alert("Agregar Referencia AMF");
		return;
	}else if($("#rfcAMF").val() == ""){
		alert("Agregar RFC AMF");
		return;
	}else if($("#cIdCuentasBancarias").val() == ""){
		alert("Seleccionar Cuenta Bancaria");
		return;
	}else if($("#fechaCaptura").val() == ""){
		alert("Seleccionar Fecha de Captura");
		return;
	}else if($("#fechaPago").val() == ""){
		alert("Seleccionar Fecha de Pago");
		return;
	}else if(importe == ""){
		alert("Agregar Importe");
		return;
	}else if( Number(importe) > Number(saldo) ){
		alert("El Importe De Pago Es Mayor Al Saldo");
		return;
	}else{ 
		// mSaldoResto = Number(quitaFmt(saldo)) - Number(quitaFmt(importe));
		
		// $("#mSaldoResto").val(mSaldoResto);
		$("#importePago").val(quitaFmt(importe));
		//$("#mSaldo").val(quitaFmt(saldo));
		
		fC = $("#fechaCaptura").val().split("/");
		fC2 = fC[2]+"-"+fC[1]+"-"+fC[0];
		
		fP = $("#fechaPago").val().split("/");
		fP2 = fP[2]+"-"+fP[1]+"-"+fP[0];
		
		$("#fechaCaptura").val(fC2);
		$("#fechaPago").val(fP2);
		
		getNextSequenceVal({seqName: "RP" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
		$("#cUnidadResponsable").val("<%=ur%>");
		queryFormPost("tRegistroPasivoEncabezadoCreate", {async: false });
		//queryFormPost("tRegistroPasivoDetalleUpdate", {async: false});
		
		//Guardar Detalles
		var table = document.getElementById("epPagoAMF");
		var count = table.rows.length;
		var epTable = "";
		var ep = "";
		var importeDet = 0;
		
		for(var i = 1; i < count; i++){
			row = table.rows[i];
			epTable = row.cells[0].childNodes[0];
			importeDet = row.cells[1].childNodes[0];
			
			$("#epDet").val(epTable.value);
			$("#importeDet").val(importeDet.value);
			$("#numPagoAMFDet").val($("#clavePasivo").val());
			
			queryFormPost("tRegistroPasivoDetalleCreate", {async: false});
			$("#epDet").val("");
			$("#importeDet").val("");
		}	
		
		alert("Guardado Correctamente Con Numero de Pago AMF: "+$("#clavePasivo").val());
		$("#btnCXP").removeAttr("disabled","disabled");
		$("#btnNuevo").removeAttr("disabled","disabled");
		location.reload();
	}	
}

function setSequenceVal(seqValue) 
{
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "<%=cCentroContable%>" + "RP" + $("#aEjercicioFiscal").val() + seqValue;
		$("#clavePasivo").val( seqValue );
}
function onlyNumbers(evt) 
{
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 47) { return false; }
		return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}
function Sinfrmt(fld){
		var valcol = $("#"+fld).val();
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$("#"+fld).val(valcol);
}		
function cambiafrmt(fld){
	   	$("#"+fld).formatCurrency();
}

function borrarEpPagoAMF(renglon){
	$("#epDelete").val( $("#ep_"+renglon).val() );
	$("#numPagoAMFDelete").val( $("#clavePasivo").val() );
	queryFormPost("TREGISTRODETALLE",{async:false});	
	buscarEPAMFDetalle("TREGISTRODETALLE","","clavePasivo = '"+$("#clavePasivo").val()+"'")
}

function buscarEPAMFDetalle(szTabla,elParam,campos){
						
				$('#epPagoAMF').dataTable().fnClearTable();
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParam, MaxReg: "5",ajax: 'true'}, 
										
							function(j){					
								var renglon = 0;									
								for(var i = 0; i < j.length; i++){
									renglon = renglon + 1;	
									clavePasivo = j[i].Col0;
									ep = j[i].Col1; 
									importe = j[i].Col2;
									$("#epPagoAMF").dataTable().fnAddData([
											'<input type="text" name="ep_'+renglon+'" id="ep_'+renglon+'" value="'+ep+'" size=80 style="background-color:#E0E0F8;border:1px solid #E0E0F8" readonly />',
											'<input type="text" name="importe_'+renglon+'" id="importe_'+renglon+'" value="'+importe+'" class="deshabilita" onchange="validarEpDetalle('+renglon+')">' , 
											'<input type="button" name="borrar" id="borrar" value="Borrar EP"  class="deshabilita" onClick="borrarEpPagoAMF('+renglon+')">'								
									]);
								}	
						});
}	

function Grid()
{
	window.open('AcuerdoAMFAyuda.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=680px, left=100px');
}

function validarEpDetalle(renglon){
	var table = document.getElementById("epPagoAMF");
	var count = table.rows.length;
	var monto = 0;
	var row = 0;
	var montoTotal = 0;
	
	for(var i = 1; i < count; i++){
		
		row = table.rows[i];
		monto = row.cells[1].childNodes[0];
		
		montoTotal = Number(montoTotal) + Number(monto.value) ;
	}
	var importePago = quitaFmt($("#importePago").val());
	if(importePago < montoTotal){
		alert("Error: Importe Pago Menor a Monto de Estructura Programatica");
		return;
	}else if(importePago > montoTotal){
		alert("Error: Importe Pago Mayor a Monto de Estructura Programatica");
		return;
	}else{
		$("#importeUpdate").val($("#importe_"+renglon).val());
		$("#numPagoAMFUpdate").val($("#clavePasivo").val());
		$("#EPUpdate").val($("#ep_"+renglon).val());
		queryFormPost("tRegistroPasivoDetalleUpdate",{async:false});
		
		var campo = "numPagoAMF = '"+$("#clavePasivo").val()+"'";	
		buscarEPAMFDetalle("TREGISTRODETALLE","",campo)
		$("#importeUpdate").val("");
		$("#numPagoAMFUpdate").val("");
		$("#EPUpdate").val("");
	}
} 

function validarEpDetalleTable(){
	var table = document.getElementById("epPagoAMF");
	var count = table.rows.length;
	var monto = 0;
	var row = 0;
	var montoTotal = 0;
	
	for(var i = 1; i < count; i++){
		
		row = table.rows[i];
		monto = row.cells[1].childNodes[0];
		
		montoTotal = Number(montoTotal) + Number(monto.value) ;
	}
	montoTotal = montoTotal.toFixed(2);
	var importePago = Number( quitaFmt( $("#importePago").val() ) );
	/*if(importePago < montoTotal){
		alert("Error: Importe Pago Menor a Monto de Estructura Programatica");
		$("#btnGuardar").attr("disabled","disabled");
		return;
	}else*/ 
	if(montoTotal > importePago){
		alert("Error: Importe Pago Mayor a Monto de Estructura Programatica");
		$("#btnGuardar").attr("disabled","disabled");
		return;
	}
	if(importePago == montoTotal){
		alert(" Validacion Correcta ");
		$("#btnGuardar").removeAttr("disabled","");
		//$("#btnCXP").removeAttr("disabled","");
	}
}

function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
}

function cancelarPago(){
	var clavePasivo =  $("#clavePasivo").val();
	if(confirm("¿ Esta Seguro Que Desea Cancelar El Pago " + clavePasivo +  " ?")){
		queryFormPost("tRegistroPasivoEncabezadoUpdateEstatus",{async:false}); 
	}else{
		alert("Documento No Cancelado");
	}
}

function agregarEPDetalleTable(A, B) {
	var table = document.getElementById("epPagoAMF");
	var count = table.rows.length;
	var epTable = "0";
	var ep = $("#ep").val();
	
	if($("#ep").val() == ""){
		alert("Agregar EP");
	}else{
		for(var i = 1; i < count; i++){
			row = table.rows[i];
			epTable = row.cells[0].childNodes[0];
			if(epTable.value == ep){
				alert("EP Ya Esta Agregada");
				return;
			}
		}
		 $('#epPagoAMF').dataTable().fnAddData( [ A,B ] );
		 $("#ep").val("");
	}
}

function cmdImprimir(){
location.reload();
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=PolizaRegistroPasivos.jasper"
						+ "&NumPagoAMF=" + $("#clavePasivo").val(),
						//+ "&nombre="   + ""
						//+ "&cargo="    + ""
						//+ "&area="     + "",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
					
}


</script>
</head>
<body id="dt_example">
	  <form id="formPagos" name="formPagos">
					  		<div id="container" class="container SyCData">
								<div id="container">
								
								  	<h1>Registro de Pasivos<label style="font-size: 8pt"></label></h1>
								  	
								 	<input type="hidden" id="Partida" name="Partida" value="6"/>
									<input type="hidden" id="Partida2" name="Partida2" value=""/>
									<input type="hidden" id="Partida3" name="Partida3" value=""/>					
									<input type="hidden" id="Partida4" name="Partida4" value=""/>	
								  	<input type="hidden" id="estatus" name="estatus" value="Activo">
								  	<select style="visibility: hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"></select>
								  	<input type="hidden" id="U_LOGIN" name="U_LOGIN" value="<%=usuLogin%>">
								  	<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>">
								  	
								  	<input type="hidden" id="mSaldoResto" name="mSaldoResto" value="0">
								  	<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP" value="">
								  	<!-- Borrar EP Detalle -->
								  	<input type="hidden" id="numPagoAMFDelete" name="numPagoAMFDelete" >
								  	<input type="hidden" id="epDelete" name="epDelete" >
								  	<!-- Actualizar EP Importe -->
								  	<input type="hidden" id="importeUpdate" name="importeUpdate">
								  	<input type="hidden" id="numPagoAMFUpdate" name="numPagoAMFUpdate">
								  	<input type="hidden" id="EPUpdate" name="EPUpdate">
								  	<!-- Ingresar EP Detalle -->
								  	<input type="hidden" id="numPagoAMFDet" name="numPagoAMFDet">
								  	<input type="hidden" id="epDet" name="epDet">
								  	<input type="hidden" id="importeDet" name="importeDet">
									  	<div id="tabsl" style="width:850px; align:center"> 
									  	<fieldset> 
									  		<table id="tbl" width="850px" height ="250px" >
									  											  											  		
									  			<tr>
									  				<td id="uResponsable">Unidad Responsable</td>
									  				<td colspan="3"><SELECT  id="cUnidadResponsable" name="cUnidadResponsable"></SELECT></td>
									  			</tr>
									  			<tr>
									  				<td>Clave Pasivos</td>
									  				<td colspan="3"><input type="text" id="clavePasivo" name="clavePasivo" class="AyudaSyC" readonly="readonly"></td>
									  				
									  			</tr>
									  			<tr>
									  				<td>Referencia</td>
									  				<td colspan="3"><input type="text" id="referenciaAMF" name="referenciaAMF" maxlength="60" size="100"></td>
									  				
									  			</tr>
									  			<tr>
									  				<td>RFC</td>
									  				<td><input type="text" id="rfcAMF" name="rfcAMF" class="AyudaSyC" readonly='readonly'></td>
									  				<td>Cuenta Bancaria</td>
									  				<td><select id="cIdCuentasBancarias" name="cIdCuentasBancarias"></select><input type="text" id="cIdCuentasBancarias1" name="cIdCuentasBancarias1"></td>
									  			</tr>
									  			<tr>
									  				<td>Fecha Captura</td>
									  				<td><input type="text" id="fechaCaptura" name="fechaCaptura" ></td>
									  				<td>Fecha Pago</td>
									  				<td><input type="text" id="fechaPago" name="fechaPago"></td>
									  			</tr>
									  			<tr>
									  				<td>Importe Pago</td>
									  				<td><input type="text" id="importePago" name="importePago" value="$0.00" onfocus="Sinfrmt(this.name)" onblur="cambiafrmt(this.name)" onKeyPress="return(onlyNumbers(event))"></td>
									  				<td>&nbsp;</td>
									  				<td>&nbsp;</td>
									  			</tr>		  		
									  			
									  		</table>
									  	</fieldset>	
									  	</div>
									  	<div>
									  	
									  		<table id="epDetalle1" name="epDetalle1">
									  			<tr><td colspan="1">Estructura Programatica:</td></tr>
												<tr>
													<td><input type="text" id="ep" name="ep" size="80" readonly class="" ></td><td><input type="button" id="nIdClaveEgresos2" name="nIdClaveEgresos2" size="5"  value="..." onclick="Grid();"></td>													
													<td><input type="button" value="Agregar" name="AgregarEP" id="AgregarEP"></td>
												</tr>
												<tr><td>&nbsp;</td></tr>
											</table>
											<table id="epDetalle" name="epDetalle">	
												<tr>	
														<td>
															<table id="epPagoAMF" class="display" align="center" width="600px">
																<tbody>
																	<thead>
																		<tr align="center">
																			<th>Estructura Programatica</th>	
																			<th>Importe</th>
																			<!--th>Accion</th -->
																		</tr>	
																	</thead>							
																</tbody>	
															</table>						
														</td>
												</tr>
										   </table>
										   <table align ="center">
									  			<tr>
									  				<td>&nbsp;</td><td><input type="button" id="btnGuardar" name="btnGuardar" value="Guardar" ></td>
									  				<td>&nbsp;</td><td><input type="button" id="btnNuevo" name="btnNuevo" value="Nuevo Pago"></td>
									  				<td>&nbsp;</td><td><input type="button" id="btnCancelar" name="btnCancelar" value="Cancelar Pago"></td>
									  				<td>&nbsp;</td><td><input type="button" id="btnCXP" name="Cuenta Por Pagar" value="Imprimir Cuenta Por Pagar" onclick="cmdImprimir()" /></td></tr>
									  			<tr>
									  		</table>
									  	</div>
					  			</div> 
					  		</div>
	  </form>
</body>
</html>
