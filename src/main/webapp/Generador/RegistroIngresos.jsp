<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.AdecuacionManager"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%@page import="com.syc.registroingresos.RegistroIngresosBussinesLogic"%>
<%@page import="com.syc.registroingresos.RegistrosIngresosEncabezado"%>
<%@page import="com.syc.registroingresos.RegistrosIngresosDetalle"%>
<%@page import="com.syc.registroingresos.RegistroIngresoRazonSocial"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());	
	
	String cCentroContable = "";
	String aEjercicioFiscal = "";
	boolean bErrorAdec=false;
	boolean bAplicadoCont = false;
	String inserta = "1";
	String cUR = "";
	boolean esControlFonden = false;
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);	
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	esControlFonden = adecProy.controlFonden();
	
	if( Integer.parseInt( aEjercicioFiscal ) != c1.get(Calendar.YEAR) )
		today = "31/12/" + aEjercicioFiscal;
		
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int idTipoCaso = c.getIdTC();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}
	
	int idCaso = c.getIdCaso();
	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	
	RegistroIngresosBussinesLogic registroIngresos = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);
	RegistroIngresosBussinesLogic registroIngresosRS = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	String mensaje = "";
		
	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}	
	
	RegistrosIngresosEncabezado rie = registroIngresos.getRegistroEncabezadoNuevo(folio);
	RegistrosIngresosDetalle rid = registroIngresos.getRegistroDetalleNuevo(folio);
	RegistroIngresoRazonSocial rirs = registroIngresosRS.getRegistroIngresoRazonSocial(folio);
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
	
	cUR = usuario.getU_UR();
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Registro de Ingresos</title>

<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"> </script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"> </script>
	
<script type="text/javascript">
	var nTipoIngreso = "0";
	var bReturnInsert = true;
	//var esFONDEN = esControlFonden;
	
	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$("#agregar").button();
		$("#Agregar").button();
		$("#btnBorraEPs").button();
		$("#btnImprimir").button();
		$("#nIdClaveEgresos2").button();		
		$("#divRendimientosGreenMex").hide();
		cargaProgramas();
		armarTablaEPS();
		muestraProgramas();	
				
		$("#tipoIngreso").change(function(){ muestraProgramas(); });
		$("#programacbo").change(function(){ muestraInputRendimientosMixtos(); });
		
		$('#dialogInsertar').dialog({
	      	autoOpen: false,
  			width: 900,
 			heigth: 2900,
 			close: function(){
 				parent.document.getElementById("pb_leave").click();
 				$("#esperar").dialog("open");
 			}
	    });
		setFechas();
	    creaDialogoFirmantes();
	    creaDlgFirtmantesUpdate();	
  		$("#btnImprimir").hide();
  		$("#importaEPs").hide();
  		$("#btnBorraEPs").hide();
  			
	});

	function setFechas(){
		$("#fechaApl").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});		
	
		$("#fRecepcionRecurso").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});	
	}	
		
	/**
	 * 4
	 * Se ejecuta despues de que termina la funcion onSubmit 
	 */
	function onPostDisplay(id_oper) {
		if( bReturnInsert ) 
			capturaFirmantes();
			
		$("#btnImprimir").show();	
	}
	//Boton Enviar
	function onPostSubmit(id_oper) {
		var bRegresa = true;
		
		parent.document.getElementById("pb_send").disabled=true;
		
		if(id_oper == 1){			
			if(aplicarRegistroIngresos()){
				//alert("Se Envia la Solicitud");
			}else{
				parent.document.getElementById("pb_send").disabled=false;
				bRegresa = false;
			}			
		}
		
		return bRegresa;
	}
	
	// funcion para cargar la plantilla
	function onLoadPlantilla(id_oper) {
		if(<%=id_oper%> == 1){
			queryFormPost("existeRegistroIngresosRead", {async:false});
			
			if($("#existeFolio").val() == "1"){
				cargaEncabezado();
				$("#btnImprimir").show();
				$("#btnBorraEPs").hide();
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=false;
			}				
		}else if(<%=id_oper%> == 2){
			cargaEncabezado();				
		}
		
		if (<%=id_oper%> >= 2 ){
			$("#EditaFirmas").css('visibility', 'visible');
		}		
			
	}

	/**
	 * 1
	 * Funcion llamada al momento de guardar.
	 * Realiza validaciones,si todo es correcto, regresar true para que continue con el flujo
	 */
	function onSubmit(id_oper) {		
		var p = window.parent;
		
		var bRegresa = true;
		
		var grid = $('#dt_regIngreso').dataTable();
		var data = grid.fnGetData();
		var nRows = data.length; 
		
		try{
					
			//Guardado de los campos correspondientes a cada variable de caso
			p.gestion.setFolio( $("#FOLIO").val() );
			p.gestion.setOperador( $("#OPERADOR").val() );
			p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
			p.gestion.setEjercicioFiscal( "<%=aEjercicioFiscal%>" );
			p.gestion.setConceptoMov("Aplicación Registros Ingresos");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
			p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			p.gestion.setAplicadoCont("false");
			
			if (id_oper==1){	
			
				if($("#existeFolio").val() == "0"){
					if($("#tipoIngreso").val() == "S" ){
						Swal.fire({ icon: 'warning',
   									text: "Favor de seleccionar el tipo de Ingreso para continuar.." });			
						return;
					}else if($("#fechaApl").val() == ""){
						Swal.fire({ icon: 'warning',
									text: "Favor de capturar la fecha de aplicación para continuar.." });						
						return;				
					}else if($("#fechaCap").val() == ""){
						Swal.fire({ icon: 'warning',
									text: "Favor de capturar la fecha de Captura para continuar.." });
						return;				
					}else if($("#CTABAN").val() == ""){
						Swal.fire({ icon: 'warning',
									text: "Favor de capturar la Cuenta Bancaria para continuar.." });						
						return;
					}else if(nRows <= 0){
						if($("#tipoIngreso").val() == "FF" ){
							Swal.fire({ icon: 'warning',
										text: "Favor de capturar al menos una EP para continuar.." });						
							return;
						}
					}
					
					if ($("#programacbo").val() == '11' || $("#programacbo").val() == '6'){					
						if($("#cClave").val() == ""){
							Swal.fire({ icon: 'warning',
								text: "Favor de capturar la Razon Social del origen del recurso.." });						
							return;
						}else if($("#fRecepcionRecurso").val() == ""){
							Swal.fire({ icon: 'warning',
								text: "Favor de capturar la fecha de recepción del recurso.." });						
							return;
						}else if($("#cOrigenTransferencia").val() == ""){
							Swal.fire({ icon: 'warning',
								text: "Favor de capturar el nombre de quien hizo la transferencia o depósito.." });						
							return;
						}else if($("#cNombreGestion").val() == ""){
							Swal.fire({ icon: 'warning',
								text: "Favor de capturar el nombre de quien gestiono el origen del recurso.." });						
							return;
						}else if($("#nesExtranjero").val() == "-1"){
							Swal.fire({ icon: 'warning',
								text: "Favor de seleccionar si es del extranjero o no.." });						
							return;
						}else if($("#cesDonativo").val() == "-1"){
							Swal.fire({ icon: 'warning',
								text: "Favor de seleccionar si es donativo o no.." });						
							return;
						}else if($("#cComprobanteFiscal").val() == "-1"){
							Swal.fire({ icon: 'warning',
								text: "Favor de seleccionar si se requiere comprobante fiscal o no.." });						
							return;
						}	
					}
					
					if($("#tipoIngreso").val() == "IP" ){
						if(document.getElementById("programacbo").value == 0){
							Swal.fire({ icon: 'warning',
										text: "Favor de seleccionar un programa para continuar.." });							
							return;
						}
					}
					
					bRegresa = bReturnInsert;						
					
					return true;
				}else{
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
					bRegresa = true;
				}																
			}		
			
		}
		catch (e) {			
			Swal.fire({ icon: 'error',
						text: "onSubmit: Error: " + e.message });
			bRegresa = false;
		}
		
		return bRegresa;
	}

	/**
	 * 2
	 * Retorna el nombre del responsable siguiente.
	 */
	function ResponsableSiguiente(id_oper) {
		switch(parseInt(id_oper)){
		case 1:
			return "CONSULTA_REGISTROINGRESO";
			break;
		
		case 2 :
			return "CONSULTA_REGISTROINGRESO";
			break;
		}
	}

	/**
	 * 3
	 * Retorna el nombre de la operacion siguiente.
	 */
	function OperacionSiguiente(id_oper) {
		switch(parseInt(id_oper)){
		case 1:
			return "consulta_registroingreso";
			break;
		
		case 2 :
			return "consulta_registroingreso";
			break;
		}
	}	
	
	function cargaProgramas(){		
		querySelectPost("tCatProgramasProyectosRead", "programacbo", {async:false});
	}
	
	function armarTablaEPS(){
		$('#dt_regIngreso').dataTable(
			{         
				"iDisplayLength": 20,
				sScrollY: "150px",
				sScrollX: "700px",
				"bPaginate": false,
	      			"bLengthChange": false,
	      			"bFilter": false,
	      			"bSort": false,
	      			"bInfo": false,
	      			"bAutoWidth": true, 
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"	
			} );
	}
	
	function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8 || tecla == 45)
			return true;
		patron = /[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	
	function quitaFmt(val) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	
		if (val.indexOf("(") >= 0) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
		}
		return val;
	}
	
	
	function epDuplicada(){
		
		var oTableM = $('#dt_regIngreso').dataTable();
		var aData = oTableM.fnGetData();
		var nRows = aData.length + 1;
		
		if (nRows == 1)
			return false;
			
		
		for (var i = 0; i < aData.length; i++){
			if ( aData[i][1] == $("#EP").val() )
				return true;
		}	
		
		return false;
	}
	
	function validaCapturaEP(){
		
		if ( epDuplicada()){
			Swal.fire({ icon: 'warning',
						text: "La EP que intenta agregar ya existe en el Detalle, favor de rectificar!!!" });
			$("#btnBorraEPs").show();
			return;
		}
		
		$("#mImporte").val(quitaFmt($("#mImporte").val()));
		if ($("#EP").val()==""){
			Swal.fire({ icon: 'warning',
						text: "Favor de Seleccionar la EP." });			
			return;
		}
		if ($("#mImporte").val()=="" || quitaFmt($("#mImporte").val())==0.00){
			Swal.fire({ icon: 'warning',
						text: "Favor de Introducir el importe." });			
			return;
		}
		
		var monto = $("#mImporte").val();
		if (parseInt(monto.indexOf("."),10) != parseInt(monto.lastIndexOf("."),10)){ 
			Swal.fire({ icon: 'warning',
						text: "No se permiten dos puntos decimales, favor de introducir el importe correcto." });			
			$("#mImporte").val("0");
			return;
		}
		
		var redondeo;
		var validM;
		validM = Number(quitaFmt($("#mImporte").val())).toFixed(2);
		
		var mes = [ "MontoEnero", "MontoFebrero", "MontoMarzo", "MontoAbril",
					"MontoMayo", "MontoJunio", "MontoJulio", "MontoAgosto",
					"MontoSeptiembre", "MontoOctubre", "MontoNoviembre",
					"MontoDiciembre" 
				  ];		  
		
				  
		var fecha = parseInt($("#fechaApl").val().split("/")[1], 10);
		var sumaMonto = " WHERE " + " cSubCuenta = '" + $("#EP").val() + "'";
		var token = "";
		var campos = "";
		var camposSuma = "";
		
		/*if( $("#tipoIngreso").val() == "IP" || $("#tipoIngreso").val() == "IF"){
			fecha = 12;
		}*/
		
		for ( var i = 0; i < fecha; i++) {
			camposSuma += token + mes[i];
			token = " + ";
		}
		token = "";
		var acum = 0.0;
		var acum = parseFloat($("#acumuladoImporte").val());
		var szWhere = "";
		var szTabla = "";
		
		if( $("#tipoIngreso").val() == "IP" || $("#tipoIngreso").val() == "IF")
		{
			//fecha = fecha.getMonth() + 1;
			
			for ( var i = 0; i <= fecha-1; i++) {
				campos += token + mes[i];
				token = " , ";
			}			
			campos = camposSuma + ", " + campos;
			szTabla = "VDISPONIBLEEPREGINGRESOS";
			$.getJSON("../catalogos/SelectJson.jsp", {Tabla : szTabla,Param : szWhere,MaxReg : sumaMonto,Campos : campos,ajax : 'false'},
			function(j){
				var montoDisp = parseFloat(j[0].Col3);					
				
				/*if (montoDisp < validM ) {
					Swal.fire({ icon: 'warning',
								text: "No puedes registrar mas de lo disponible, favor de rectificar!!!" });
					$("#mImporte").val("");
					$("#EP").val("");					
					return;
				}*/			
			
				var resto = parseFloat(validM);
				var importeIP = parseFloat(validM);
				var mRegistro = 0;
				var mes = fecha; 
				var nRows = 1;
				var arreglo = new Array();			
				
				arreglo	= [ j[0].Col4,j[0].Col5,j[0].Col6,j[0].Col7, j[0].Col8,j[0].Col9,j[0].Col10,j[0].Col11, j[0].Col12,j[0].Col13,j[0].Col14,j[0].Col15 ];				
				
				//for ( var i = 0; i < fecha; i++) {					
					
					/*if ( parseFloat(arreglo[i]) > 0 )
					{
						if ( resto > parseFloat(arreglo[i]))
						{
							resto -=  parseFloat(arreglo[i]);
							mRegistro = parseFloat(arreglo[i]);
						}
						else
						{
							mRegistro = Math.round(resto * 100) / 100;
							resto = 0;
							i= fecha;
						}
						
						var oTableM = $('#dt_regIngreso').dataTable();
						var aData = oTableM.fnGetData();
						var nRows = aData.length + 1;
						
						agregarRenglon(nRows, $("#EP").val(), (mes).toString(), importeIP);
						redondeo = Number($("#acumuladoImporte").val()) + importeIP;
						$("#acumuladoImporte").val(Math.round(Number(redondeo) * 100) / 100);
						if($("#programacbo").val() != "14")
							$("#acumuladoBanco").val(Math.round(Number(redondeo) * 100) / 100);
					}*/	
					agregarRenglon(nRows, $("#EP").val(), (mes).toString(), importeIP);
					redondeo = Number($("#acumuladoImporte").val()) + importeIP;
					$("#acumuladoImporte").val(Math.round(Number(redondeo) * 100) / 100);
					if($("#programacbo").val() != "14")
						$("#acumuladoBanco").val(Math.round(Number(redondeo) * 100) / 100);
					
					//mes ++;
					//nRows ++;
				//}
			
				$("#mImporte").val("");
				$("#EP").val("");
			});
		} // IP
		else if( $("#tipoIngreso").val() == "FF" )
		{
			var token = "";
			var campos = "";
			
			for ( var i = fecha-1; i >= 0; i--) {
				campos += token + mes[i];
				token = " , ";
			}
			szTabla = "VDISPONIBLEEPFF";
			$.getJSON("../catalogos/SelectJson.jsp", {Tabla : szTabla,Param : szWhere,MaxReg : sumaMonto,Campos : campos,ajax : 'false'},
			function(j){
				var acumulado = 0.0;
				
				acumulado = Number($("#montoDisponible").val());
				
				if (acumulado < validM) {					
					Swal.fire({ icon: 'warning',
								text: "No puedes registrar mas de lo disponible, favor de rectificar!!!" });
					$("#mImporte").val($("#montoDisponible").val());
					return;
				}
				
				var resto = parseFloat(validM);
				var mRegistro = 0;
				var mes = fecha; 
									
				for ( var i = 0; i < j.length; i++) {
					
					if ( parseFloat(j[i].Col3) > 0 )
					{
						if ( resto > parseFloat(j[i].Col3))
						{
							resto -=  parseFloat(j[i].Col3);
							mRegistro = parseFloat(j[i].Col3);
						}
						else
						{
							mRegistro = Math.round(resto * 100) / 100;
							resto = 0;
							i= j.length;
						}
						
						var oTableM = $('#dt_regIngreso').dataTable();
						var aData = oTableM.fnGetData();
						var nRows = aData.length + 1;
						
						agregarRenglon(nRows, $("#EP").val(), (mes).toString(), mRegistro);
						redondeo = Number($("#acumuladoImporte").val()) + mRegistro;
						$("#acumuladoImporte").val(Math.round(Number(redondeo) * 100) / 100);
					}	
					mes --;
				}
				
					$("#mImporte").val("");
					$("#EP").val("");
			});	
		}
		document.getElementById("tipoIngreso").disabled=true;
		$("#btnBorraEPs").show();
		
	}
	
	function borrarRegistroIngreso(){
		 $("#dt_regIngreso").dataTable().fnClearTable();
		 $("#acumuladoImporte").val("0.00");
		 $("#btnBorraEPs").hide();
		 document.getElementById("tipoIngreso").disabled=false;
	}
	
	function agregarRenglon(renglon, ep, mes, importe) {
		$('#dt_regIngreso').dataTable().fnAddData( [ renglon, ep, mes, importe ]);
	}
	
	function muestraAyudaEPS(){
		if( $("#tipoIngreso").val() == "S" ){
			Swal.fire({ icon: 'warning',
						text: "Favor de seleccionar el tipo de Ingreso.." });
			return;
		}else{
			if( $("#tipoIngreso").val() == "IP" ){
				$("#fuenteFinanciamiento").val("4");
				window.open('ayudaEpsIngresos.jsp?fuenteFinancimiento=4', 'ayudaEpsIngresos', 'status=1, width=800px, height=500px, left=100px');
			}else if( $("#tipoIngreso").val() == "IF" ){
				$("#fuenteFinanciamiento").val("0");
				window.open('ayudaEpsIngresos.jsp?fuenteFinancimiento=0', 'ayudaEpsIngresos', 'status=1, width=800px, height=500px, left=100px');
			}else{
				var fecha = parseInt($("#fechaApl").val().split("/")[1], 10);
				$("#fuenteFinanciamiento").val("");
				$("#mesDisponible").val(fecha.toString());
				window.open('ayudaEpsIngresosFiscales.jsp', 'ayudaEpsIngresosFiscales', 'status=1, width=800px, height=500px, left=50px');
			}
		}
	}	
	
	function sumaRendimientosGM(){
		var importeBanco = 0;
		var rendimientoGM;
		var total;
		var importeEP;
		$("#acumuladoBanco").val(0);
		
		importeEP = Number($("#mImporteIP").val());
		importeBanco = Number($("#acumuladoBanco").val());
		rendimientoGM = Number($("#mImporteRendimientosGM").val());
		total = Number($("#mImporteIP").val()) + Number($("#acumuladoBanco").val()) + Number($("#mImporteRendimientosGM").val());
		
		$("#acumuladoBanco").val(Math.round(Number(total) * 100) / 100);			
	}
		
	function muestraProgramas(){
		$("#CTABAN").val("");
		if( $("#tipoIngreso").val() == "IP"){			
			$("#programacbo").show();
			$("#lblprogramacbo").show();
			$("#divIngresoPropio").show();
			$("#divIngresoFiscal").hide();
			$("#importaEPs").hide();	
		}
		else if( $("#tipoIngreso").val() == "IF"){			
			$("#programacbo").hide();
			$("#lblprogramacbo").hide();
			$("#divIngresoPropio").hide();
			$("#divIngresoFiscal").show();
			$("#importaEPs").hide();			
		}
		else if( $("#tipoIngreso").val() == "FF"){			
			$("#programacbo").hide();
			$("#lblprogramacbo").hide();
			$("#divIngresoPropio").hide();
			$("#divIngresoFiscal").show();
			$("#importaEPs").show();			
		}
		else{			
			$("#programacbo").hide();
			$("#lblprogramacbo").hide();
			$("#importaEPs").show();	
			$("#divRazonSocial").hide();
			$("#divIngresoPropio").hide();
			$("#divIngresoFiscal").hide();
		}
	}
	
	function muestraInputRendimientosMixtos(){
		if($("#programacbo").val() == '14')
			$("#divRendimientosGreenMex").show();
		else 
			$("#divRendimientosGreenMex").hide();
	}
	
	function cargaTipoIngreso(){
		if( $("#tipoIngreso").val() == "S" ){
			nTipoIngreso = "0";
		}else{
			if( $("#tipoIngreso").val() == "IP" ){
				nTipoIngreso = "2";
			}else if( $("#tipoIngreso").val() == "IF" ){
				nTipoIngreso = "3";
			}else if ( $("#tipoIngreso").val() == "FF" ){
				nTipoIngreso = "1";
			}
		}		
	}
	
	function guardaRegistroIngresos(){		
		var bValor = true;
		
		cargaTipoIngreso();
		//capturaFirmantes();		
		if(!InsertarRegistroIngreso()){
			bValor = false;
		}
				
		return bValor;
	}	
	
	function InsertarRegistroIngreso(){
	
		var bRegresa = true;
		
		$("#nTipoIngreso").val(nTipoIngreso);
		$("#cTipoIngreso").val($("#tipoIngreso").val());
		$("#cConcepto").val($("#concepto").val());
		
		var arrData = $("#dt_regIngreso").dataTable().fnGetData();
		var longitud = arrData.length;
		
		var renglon = new Array(longitud);
		var ep = new Array(longitud);
		var mes = new Array(longitud);
		var importe = new Array(longitud);
		var mImporteRendimientos = new Array(longitud);
		var mImporteRendimientosGM = new Array(longitud);
		
		for(var i = 0; i < arrData.length; i++  ){
			renglon[i] = arrData[i][0];
			ep[i] = arrData[i][1];
			mes[i] = arrData[i][2];
			importe[i] = arrData[i][3];				
		}	
		
		var folioIngreso = $("#nFolioRegistroIngreso").val();				
		var tipoIngreso = $("#nTipoIngreso").val();
		var cTipoIngreso = $("#cTipoIngreso").val();
		var fechaApl = $("#fechaApl").val();
		var fechaCap = $("#fechaCap").val();		
		var cPrograma = $("#programacbo").val();
		var cConcepto = $("#cConcepto").val();
		var cTab = $("#CTABAN").val();
		var apartado = "0";	
		var mImporteIP = $("#mImporteIP").val(); 
		
		if (tipoIngreso == "1"){
			if(<%=cCentroContable%> == "10"){
				 $("#cCentroContable").val(<%=cCentroContable%>);
				 $("#aEjercicioFiscal").val(<%=aEjercicioFiscal%>);
				getNextSequenceVal({seqName: "IF-" + $("#cCentroContable").val(), async: false, callback: setSequenceIF});
				getNextSequenceVal({seqName: "APARTADO", async: false, callback: setSequenceAptd});
			}
			else{
				$("#cCentroContable").val(<%=cCentroContable%>);
				$("#aEjercicioFiscal").val(<%=aEjercicioFiscal%>);
				getNextSequenceVal({seqName: "IF-" + $("#cCentroContable").val(), async: false, callback: setSequenceIF});
				getNextSequenceVal({seqName: "APARTADO", async: false, callback: setSequenceAptd});
//				alert ("No hay Secuencia para este Centro Contable, Favor de Notificar al Administrador!!!");
//				bRegresa = false;
//				return;
			}
			
			apartado = $("#apartado").val();
			
		}
			
		var caNoContrarrecibo = $("#caNoContrarrecibo").val();
		mImporteRendimientos = $("#acumuladoBanco").val();
		mImporteRendimientosGM = $("#mImporteRendimientosGM").val();
		
		var cClave = $("#cClave").val();
		var fRecepcionRecurso = $("#fRecepcionRecurso").val();
		var cOrigenTransferencia = $("#cOrigenTransferencia").val();
		var cNombreGestion = $("#cNombreGestion").val();
		var nesExtranjero = $("#nesExtranjero").val();
		var cesDonativo = $("#cesDonativo").val();
		var cComprobanteFiscal = $("#cComprobanteFiscal").val();
			
		try{
			
			var strAction="../servlet/RegistroIngresosServlet";
	   		$.ajax({
	   			datatype:"html",
				type: "POST",
				url: strAction,
				data:{inserta:1, folio:folioIngreso, nTipoIngreso:tipoIngreso, cTipoIngreso:cTipoIngreso, fechaApl:fechaApl, fechaCap:fechaCap, 
						cPrograma:cPrograma, cConcepto:cConcepto, renglon:renglon, ep:ep, mes:mes, importe:importe, CTAB:cTab, caNoContrarrecibo:caNoContrarrecibo, 
						apartado:apartado, mImporteRendimientos:mImporteRendimientos, mImporteRendimientosGM:mImporteRendimientosGM,
						cClave:cClave, fRecepcionRecurso:fRecepcionRecurso, cOrigenTransferencia:cOrigenTransferencia, cNombreGestion:cNombreGestion,
						nesExtranjero:nesExtranjero, cesDonativo:cesDonativo, cComprobanteFiscal:cComprobanteFiscal, mImporteIP:mImporteIP},
				success: function (data,textStatus){
					Swal.fire({ icon: 'success',
								text: "Se guardo la información correctamente." });
	   			},
				error: function (par) {
					Swal.fire({ icon: 'error',
								text: '<%=mensaje%>' });					
					}
			});
	   		
		}		
		catch (e) {
			Swal.fire({ icon: 'error',
						text: "onSubmit: Error: " + e.message });
			bRegresa = false;
		}
		 
		return bRegresa;
   	}	
	
	function setSequenceAptd(seqValue) {
			$("#apartado").val( seqValue );
		}
	
	function setSequenceIF(seqValue) {
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = $("#cCentroContable").val() + "IF" + $("#aEjercicioFiscal").val() + seqValue;
			$("#caNoContrarrecibo").val( seqValue );
		}
	
	function cargaEncabezado(){
				
		var sTipoIngreso = '<%=rie!=null?rie.getcTipoIngreso():"S"%>';
		$("#tipoIngreso").val(sTipoIngreso);
		
		var importe = '<%=rie!=null?rie.getmImporte():0%>';		
		var cmImporte = importe.toString();
		
		$("#acumuladoImporte").val(cmImporte);
		
		if(sTipoIngreso == "IP"){
			$("#programacbo").show();
			$("#lblprogramacbo").show();
			$("#divRazonSocial").show();
			$("#divIngresoPropio").show();
			
			var cClave = '<%=rirs!=null?rirs.getcClave() : ""%>';
			var fRecepcionRecurso = '<%=rirs!=null?rirs.getfRecepcionRecurso() : ""%>';
			var cOrigenTransferencia = '<%=rirs!=null?rirs.getcOrigenTransferencia() : ""%>';
			var cNombreGestion = '<%=rirs!=null?rirs.getcNombreGestion() : ""%>';
			var nesExtranjero = '<%=rirs!=null?rirs.getNesExtranjero() : ""%>';
			var cesDonativo = '<%=rirs!=null?rirs.getCesDonativo() : ""%>';
			var cComprobanteFiscal = '<%=rirs!=null?rirs.getcComprobanteFiscal() : ""%>';						
			
			$("#cClave").val(cClave);
			$("#fRecepcionRecurso").val(fRecepcionRecurso);
			$("#cOrigenTransferencia").val(cOrigenTransferencia);
			$("#cNombreGestion").val(cNombreGestion);
			$("#nesExtranjero").val(nesExtranjero);
			$("#cesDonativo").val(cesDonativo);
			$("#mImporteIP").val(cmImporte);
			$("#cComprobanteFiscal").val(cComprobanteFiscal);
			queryFormPost("tRazonSocialIP_Read", {async: false });
						
		} else {
			$("#divIngresoFiscal").show();	
		}			
		
		var Prog = '<%=rie!=null?rie.getcPrograma():"0"%>';
		var iProg = parseFloat(Prog);
		$("#programacbo").val(iProg);
		
		if(iProg == 14){			
			$("#divRendimientosGreenMex").show();
			var rendimientoB = '<%=rid!=null?rid.getmImporteRendimientos() : "0"%>';
			var rendimientoGM = '<%=rid!=null?rid.getmImporteRendimientosGM() : "0"%>';
			$("#acumuladoBanco").val(rendimientoB);
			$("#mImporteRendimientosGM").val(rendimientoGM);
			
		}
		
		$("#concepto").val('<%=rie!=null?rie.getcConcepto():""%>');	
		
		cargaDetalle();
	}
	
	function cargaDetalle(){
		$('#dt_regIngreso').dataTable(
			{   
				"bPaginate": true, 	"bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true, "bAutoWidth": false,
				"sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true, "bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers",
				"sScrollX": "100%", "sScrollXInner": "110%", "bScrollCollapse": true, "bServerSide": true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + 
				"/crud?rt=t&ql=tRegistroIngresoDetalle&qw=nFolioRegistroIngreso="+<%=folio%>,
				aoColumns: [
					{ sName: "nDocRenglon" },
					{ sName: "EP" },
					{ sName: "nMes"},
					{ sName: "mImporte"}
				],
				oLanguage: {
					sProcessing: "Procesando...", sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar", sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...", sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0", sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "", sInfoThousands: ",", sSearch: "Filtro:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
				}
			}	
		} );
				
		inhabilitaControles();
	}
	
	function inhabilitaControles(){
	
		document.getElementById("tipoIngreso").disabled=true;
		document.getElementById("programacbo").disabled=true;
		document.getElementById("lblprogramacbo").disabled=true;
		document.getElementById("fechaApl").disabled=true;
		document.getElementById("fechaCap").disabled=true;
		document.getElementById("EP").disabled=true;
		document.getElementById("mImporte").disabled=true;
		document.getElementById("acumuladoImporte").disabled=true;
		document.getElementById("agregar").disabled=true;
				
		if(<%=id_oper%> == 1){
			$("#btnImprimir").show();
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
		}else if(<%=id_oper%> == 2){
			$("#btnImprimir").show();
			//parent.document.getElementById("pb_save").disabled=true;
			parent.document.getElementById("pb_send").disabled=true;
		}
	}
	
	// crea dialogo pára los firmantes de autorizacion
	function creaDialogoFirmantes() { 
      	$("#dlgFirmantes").dialog({
	        title:"Datos de Firmantes",
	        autoOpen : false,
	        height : 420,
	        width : 500,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
            		if( $("#cNombreVoBo").val() == "" ) {
    					Swal.fire({ icon: 'warning',
    								text: "Falta Ingresar Nombre en Datos Vº Bº" });
    					return;
    				} else if($.trim($("#cPaternoVoBo").val()) == ""){     					
    					Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Paterno en Datos Vº Bº" });
    					return; 
    				} else if($.trim($("#cMaternoVoBo").val()) == ""){
    					Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Materno en Datos Vº Bº" });    					
    					return; 
					} else if( $("#cPuestoVoBo").val() == "" ) {					
    					Swal.fire({ icon: 'warning',
    								text: "Falta Ingresar Puesto en Datos Vº Bº" });
    					return;
    				}
                    
            		if( $("#cNombreAut").val() == "" ) {
    					Swal.fire({ icon: 'warning',
    								text: "Falta Ingresar Nombre en Datos Autorizar" });
    					return;
    				} else if($.trim($("#cPaternoAut").val()) == ""){ 
    					Swal.fire({ icon: 'warning',
    								text: "Falta Ingresar Apellido Paterno en Datos Autorizar" });
    					return; 
    				} else if($.trim($("#cMaternoAut").val()) == ""){ 
    					Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Materno en Datos Autorizar" }); 
    					return; 
    				} else if( $("#cPuestoAut").val() == "" ) {
    					Swal.fire({ icon: 'warning',
    								text: "Falta Ingresar Puesto en Datos Autorizar" });
    					return;
    				}            				
					
					$("#cNombreVo").val($("#cNombreVoBo").val());
					$("#cPaternoVo").val($("#cPaternoVoBo").val());
					$("#cMaternoVo").val($("#cMaternoVoBo").val());
					$("#cPuestoVo").val($("#cPuestoVoBo").val());
					$("#cNombreA").val($("#cNombreAut").val());
					$("#cPaternoA").val($("#cPaternoAut").val());
					$("#cMaternoA").val($("#cMaternoAut").val());
					$("#cPuestoA").val($("#cPuestoAut").val());
					
					$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
					try{
						if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")){										
							if(guardaRegistroIngresos()){
								bReturnInsert = true;  				  				  											
								parent.document.getElementById("pb_save").disabled=true;
								parent.document.getElementById("pb_send").disabled=false;
								queryFormPost("tRegistroIngresoEncabezadoFirmante_Update", {async: false });
								imprimirRegistroIngreso();
							}else{
								bReturnInsert = false;
							}				
							
							$("#cNombreVoBo").val("");
							$("#cPaternoVoBo").val("");
							$("#cMaternoVoBo").val("");
							$("#cPuestoVoBo").val("");
							$("#cNombreAut").val("");
							$("#cPaternoAut").val("");
							$("#cMaternoAut").val("");
							$("#cPuestoAut").val("");
							
							if(parent.document.getElementById("pb_send").disabled)
								parent.document.getElementById("pb_send").disabled=false;
						
						}else{
							if(parent.document.getElementById("pb_save").disabled)
								parent.document.getElementById("pb_save").disabled=false;
						}
					
						$(this).dialog("close");	
		
					}catch(e){
						if(parent.document.getElementById("pb_save").disabled)
							parent.document.getElementById("pb_save").disabled=false;
							
						Swal.fire({ icon: 'error',
									text: "No se pudo actualizar los firmantes, intente mas tarde." });
						$(this).dialog("close");
					}
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                	
                }
         	}
       });   
 
	}
	
	function capturaFirmantes(){
		$("#dlgFirmantes").dialog("open");
		queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async: false }); // voBo - autoriza					
	}
	
	function aplicarDocumento(){
		
		var bRegresa = true;
		queryFormPost("tRegistroIngresoEncAplicadoUpdate", {async: false });		
		queryFormPost("existeRegistroIngresosRead", {async: false });
		
		if($("#cDocumentoHaplicado").val() != "S"){
			bRegresa = false;
		}
		
		return bRegresa;
	}
	
	function aplicarRegistroIngresos(){
		
		var bRegresa = true;
		var folioIngreso = $("#nFolioRegistroIngreso").val();
		var msj = "";
		cargaTipoIngreso();
		
		
		if (nTipoIngreso == "1"){
			queryFormPost("getFolioApartado", {async: false });
			var apartado = $("#apartado").val();
			var fAplica = $("#fechaApl").val();
		}
		else
		{
			var apartado = "";
		}
		
		try{
		   	var strAction="../servlet/RegistroIngresosServlet";
		   	$.blockUI({message: "Procesando espere ......"});
		   	if (nTipoIngreso == "1"){
		   		/***********************************************************************************
				 ** SE CAMBIA TABLA REGISTROINGRESO POR APARTADO PARA MOVER A LAS CUENTAS COMODIN **
				 ***********************************************************************************/
				$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{aplica:1, folio:folioIngreso, tipoIngreso:nTipoIngreso, nApartado:apartado, fAplica:fAplica},
					success: function (data){						
						$("#mensajeInserta").val(data);
						$('#dialogInsertar').dialog('option', 'modal', true).dialog('open');					
		   				$.unblockUI();
		   			},
					error: function (par) {
						Swal.fire({ icon: 'error',
									text: '<%=mensaje%>' });
					}
				});
		   	}
		   	else{
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{aplica:1, folio:folioIngreso},
					success: function (data){						
						$("#mensajeInserta").val(data);
						$('#dialogInsertar').dialog('option', 'modal', true).dialog('open');					
		   				$.unblockUI();
		   			},
					error: function (par) {
						Swal.fire({ icon: 'error',
									text: '<%=mensaje%>' });
					}
				});
			}
				
			queryFormPost("existeRegistroIngresosRead", {async: false });
		
			if($("#cDocumentoHaplicado").val() != "S"){
				bRegresa = false;
			}		
			
		}		
		catch (e) {			
			Swal.fire({ icon: 'error',
						text: "onPostSubmit: Error: " + e.message });
			
			bRegresa = false;
		}
		 
		return bRegresa;
   	}
   	
   	function imprimirRegistroIngreso(){
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=PolizaRegistroIngresos.jasper"
			+ "&whereFolio= AND nFolioRegistroIngreso = " + <%=folio %>
			+ "&whereTipo= ''", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
				
	function importarEPs()
	{
	
		if ($("#importar").prop("checked")){
			//TODO: Habilitamos boton de importar archivo			
			Swal.fire({ icon: 'success',
						text: 'checked' });
		}else{
			//TODO: Deshabilitamos el boton de importar EP y el del monto individual			
			Swal.fire({ icon: 'success',
						text: 'Unchecked' });
		}
		
	}
	
	function updateFirmantes(){
		//queryFormPost("tFirmanteModuloActualizaRead", {async: false }); // voBo - autoriza	
		$("#dlgFirmantesUpdate").dialog("open");			
	}
	
	function creaDlgFirtmantesUpdate(){
		$("#dlgFirmantesUpdate").dialog({
			autoOpen: false,
			height: 420,
			width: 500,
			modal: true,
			buttons: {
				"Aceptar": function() {
					if($.trim($("#cNombreVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Vº Bº" });
						return; 
					} else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Paterno en Datos Vº Bº" });
						return; 
					} else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Materno en Datos Vº Bº" });
						return; 
					} else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Puesto en Datos Vº Bº" });
						return; 
					}					
					
					if($.trim($("#cNombreAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Autorizar" });
						return; 
					} else if($.trim($("#cPaternoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Paterno en Datos Autorizar" }); 
						return; 
					} else if($.trim($("#cMaternoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Materno en Datos Autorizar" }); 
						return; 
					} else if($.trim($("#cPuestoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Puesto en Datos Autorizar" });
						return; 
					}	
					
					$("#cNombreVo").val($("#cNombreVoBoUpdate").val());
					$("#cPaternoVo").val($("#cPaternoVoBoUpdate").val());
					$("#cMaternoVo").val($("#cMaternoVoBoUpdate").val());
					$("#cPuestoVo").val($("#cPuestoVoBoUpdate").val());
					$("#cNombreA").val($("#cNombreAutUpdate").val());
					$("#cPaternoA").val($("#cPaternoAutUpdate").val());
					$("#cMaternoA").val($("#cMaternoAutUpdate").val());
					$("#cPuestoA").val($("#cPuestoAutUpdate").val());
					
					$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
					try{
						if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")){
							queryFormPost("tRegistroIngresoEncabezadoFirmante_Update", {async: false });
							$("#cNombreVoBoUpdate").val("");
							$("#cPaternoVoBoUpdate").val("");
							$("#cMaternoVoBoUpdate").val("");
							$("#cPuestoVoBoUpdate").val("");
							$("#cNombreAutUpdate").val("");
							$("#cPaternoAutUpdate").val("");
							$("#cMaternoAutUpdate").val("");
							$("#cPuestoAutUpdate").val("");;
							imprimirRegistroIngreso();
							if(parent.document.getElementById("pb_send"))
								parent.document.getElementById("pb_send").disabled=false;
						}else{
							if(parent.document.getElementById("pb_save"))
								parent.document.getElementById("pb_save").disabled=false;
						}
					}catch(e){
						if(parent.document.getElementById("pb_save"))
							parent.document.getElementById("pb_save").disabled=false;
						Swal.fire({ icon: 'error',
									text: "No se pudo actualizar los firmantes, intente mas tarde." });
					}
					$(this).dialog("close");
				},
				"Cancelar": function() {
					if(parent.document.getElementById("pb_save"))
						parent.document.getElementById("pb_save").disabled=false;
					$(this).dialog("close");
				}
			},
			close: function(){}							
		});
	}
	
	function ayudaRazonSocial(){		
		window.open('../Generador/CatalogoRazonSocial_IP.jsp?formName=frmRegistroIngreso&inputRFCTarget=cClave&inputDRFCTarget=cNombre', 'Razon Social', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');		
	}
	
	function validaRazonSocial(){		
		if ($("#cComprobanteFiscal").val() == "SI"){
			queryFormPost("validaRazonSocialIP", {async: false });
			
			if($("#nFinalizado").val() == "0"){			
				Swal.fire({ icon: 'error',
							text: "No se puede continuar, el registro de la Razon Social para facturar de quien se originó el ingreso no esta completo, favor de completarlo" });
				parent.document.getElementById("pb_save").disabled=true;
			} else if($("#nFinalizado").val() == "1"){
				parent.document.getElementById("pb_save").disabled=false;
			}
		}
	}
	
	function capturaRazonSocial(){
		if ($("#programacbo").val() == "11" || $("#programacbo").val() == "6" || $("#programacbo").val() == "14" || $("#programacbo").val() == "15" || $("#programacbo").val() == "16"){
			$("#divRazonSocial").show();
		} else {
			$("#divRazonSocial").hide();
		}
	}
	
</script>

</head>
<br/>
<body id="dt_example">
	<form id="frmRegistroIngreso" name="frmRegistroIngreso" action="../servlet/RegistroIngresosServlet" method="post" >
		<input type="hidden" id="fuenteFinanciamiento" name="fuenteFinanciamiento" value=""/>
		
		<div id="container" style="width: 100%" class="container" > 		
		
			<input type="hidden" id="programa" name="programa"/>
			<input type="hidden" id="cEvento" name="cEvento"/>
			<input type="hidden" id="mesDisponible" name="mesDisponible" value=""/>
			<input type="hidden" id="montoDisponible" name="montoDisponible" value="0"/>
			<input type="hidden" id="nTipoIngreso" name="nTipoIngreso" value=""/>
			<input type="hidden" id="cTipoIngreso" name="cTipoIngreso" value=""/>
			<input type="hidden" id="cConcepto" name="cConcepto" value=""/>
			<input type="hidden" id="inserta" name="inserta" value="<%=inserta%>"/>
			<input type="hidden" id="mensaje" name="mensaje" value=""/>
			<input type="hidden" id="apartado" name="apartado" value=""/>
			<input type="hidden" id="cCentroContable" name="cCentroContable" value=""/>
			<input type="hidden" id="caNoContrarrecibo" name="caNoContrarrecibo" value=""/>								
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>">  
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /> 
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
			<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" />
			<input type="hidden" id="aEjercicioFiscal" value=""	name="aEjercicioFiscal" />
			<input type="hidden" id="existeFolio" name="existeFolio" value="0" />
			<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value="" />
			<input type="hidden" id="nFinalizado" name="nFinalizado" value="" />			
									
			<input type="hidden" id="cNombreVo" name="cNombreVo" />
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
			<input type="hidden" id="cNombreA" name="cNombreA"  />
			<input type="hidden" id="cPaternoA" name="cPaternoA"  />
			<input type="hidden" id="cMaternoA" name="cMaternoA" />
			<input type="hidden" id="cPuestoA" name="cPuestoA" />
			<input type="hidden" id="firmanteAut" name="firmanteAut" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" />
			
			<div class="card-header"> <h3> Registro Ingresos </h3> </div>
			<hr class="mt-3"/>
			 
			<div class="row">
				<div class="col-12 col-md-6 mb-3 d-flex">	
					<div class="form-check">
						<span id="EditaFirmas"><a href="#" onclick="updateFirmantes();">Firmas</a></span>																											
					</div>
				</div>
			</div> 
			 
			<h5> Datos Registro de Ingresos </h5>
			<hr class="mt-3"/>
				
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
					<label for="nFolioRegistroIngreso"> Folio: </label>
				</div>							
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
					<label for="fechaCap"> F. Captura: </label>
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="fechaApl"> F. Aplicación: </label>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">													
					<input name="nFolioRegistroIngreso" type="text" id="nFolioRegistroIngreso" readonly class="form-control form-control-sm" value="<%=folio%>"> 							 							
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">					
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>								
						<input type="text" id="fechaCap" name="fechaCap" class="form-control form-control-sm" value="<%=today%>"/>
					</div> 							 							
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>													
						<input type="text" id="fechaApl" name="fechaApl" class="form-control form-control-sm" value="<%=today%>"/>
					</div> 			
				</div>										
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="tipoIngreso"> Tipo Ingreso: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">														
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="lblprogramacbo" id="lblprogramacbo"> Programa: </label>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">													
					<select id="tipoIngreso" name="tipoIngreso" class="form-select form-select-sm">
						<option value="S" selected="selected"> --Seleccionar-- </option> 								
							<%if (!esControlFonden) {%>										
								<option value="FF" > Fondos Fiscales </option>
								<option value="IP">	Ingresos Propios </option>
							<%}else { %>
								<option value="IF">	Ingresos FONDEN </option>
							<%}%>
					</select>				
					<input type="hidden" id="tipoGasto" name="tipoGasto" value="1"/>
					<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value="<%=cUR%>" />			
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">													
					<select name="programacbo" id="programacbo" class="form-select form-select-sm" onchange="capturaRazonSocial()"></select> 							 							
				</div>					
			</div>
		
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="CTABAN"> Cuenta Bancaria: </label>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">													
					<input type="text" id="CTABAN" name="CTABAN" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" class="form-control form-control-sm AyudaSyC readonly"/> 						 							
				</div>								
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="concepto"> Concepto: </label>
				</div>								
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 d-flex p-1">													
					<textarea cols=90 rows=3 id="concepto" name="concepto" class="form-control form-control-sm"></textarea> 							 							
				</div>								
			</div>
			
			<br/>
			
			<div id="divRazonSocial">
				<h5> Datos Adicionales </h5>
				<hr class="mt-3"/>			
			
				<div class="row">
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<label for="cClave"> Denominación o razón social de quien realiza el ingreso, según contrato, convenio, acuerdo, oficio de instrucción, etc. </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
						<label for="fRecepcionRecurso"> F. Recepción del Recurso </label>
					</div>								
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																	
						<input type="text" id="cClave" name="cClave" class="form-control form-control-sm" readonly/>						
						<input type="button" class="btn btn-secondary btn-sm" id="btnRazonSocial" value="..." onclick="ayudaRazonSocial()">&nbsp;&nbsp;																																																			
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
						<input type="text" id="cNombre" name="cNombre" class="form-control form-control-sm" readonly/>												 						 					
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">					
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>								
							<input type="text" id="fRecepcionRecurso" name="fRecepcionRecurso" class="form-control form-control-sm" value="<%=today%>"/>
						</div> 							 							
					</div>		
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
						<label for="cOrigenTransferencia"> Nombre quien <b>realizó</b> la tranferencia o depósito </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
						<label for="cNombreGestion"> Nombre quien <b>gestionó</b> el origen del recurso </label>
					</div>					
				</div>
				
				<div class="row">							
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
						<input type="text" id="cOrigenTransferencia" name="cOrigenTransferencia" class="form-control form-control-sm"/>												 						 					
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
						<input type="text" id="cNombreGestion" name="cNombreGestion" class="form-control form-control-sm"/>												 						 					
					</div>										
				</div>
									
				<div class="row">
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
						<label for="nesExtranjero"> Nacionalidad quien <b>originó</b> </label>
					</div>					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
						<label for="cesDonativo"> Es Donativo </label>
					</div>					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
						<label for="cComprobanteFiscal"> Requiere CFDI </label>
					</div>									
				</div>
				
				
				<div class="row">							
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
						<select id="nesExtranjero" name="nesExtranjero" class="form-select form-select-sm">
							<option value="-1"> Selecciona uno... </option>
			            	<option value="1"> Extranjero </option>
			            	<option value="0"> Mexicano </option>
				        </select>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
						<select id="cesDonativo" name="cesDonativo" class="form-select form-select-sm">
			            	<option value="-1"> Selecciona uno... </option>
			            	<option value="SI"> Si </option>
			            	<option value="NO"> No </option>
				        </select>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					</div>					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">											
						<select id="cComprobanteFiscal" name="cComprobanteFiscal" class="form-select form-select-sm"> <!-- onchange="validaRazonSocial()"> Poner en cuando se haga el modulo para dar de alta la razon social-->
			            	<option value="-1"> Selecciona uno... </option>
			            	<option value="SI"> Si </option>
			            	<option value="NO"> No </option>
				        </select>
					</div>				
				</div>
							
				<br/>
				
				<div id="divIngresoPropio">
					<h5> Detalle Total IP</h5>
					<hr class="mt-3"/>
					
					<div class="row">								
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
							<label for="mImporteIP"> Importe </label>
						</div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
							<input name="mImporteIP" type="text" style="text-align: right" id="mImporteIP" onpaste="return false" onkeypress="return validar(event)" onkeyup="sumaRendimientosGM()" class="form-control form-control-sm"/>
						</div>							
					</div>		
					
					<div id="divRendimientosGreenMex">
						<div class="row">				
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
								<label for="mImporteRendimientosGM"> Rendimientos GreenMex </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
								<input name="mImporteRendimientosGM" type="text" style="text-align: right" id="mImporteRendimientosGM" onpaste="return false" onkeypress="return validar(event)" onkeyup="sumaRendimientosGM()" class="form-control form-control-sm"/>
							</div>															
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
							</div>				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
								<label for="acumuladoBanco"> Total Banco </label>
							</div>								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
								<input name="acumuladoBanco" type="text" style="text-align: right" id="acumuladoBanco" value="0" class="form-control form-control-sm" readonly/>
							</div>								
						</div>
					</div>	
				
					<div class="row">
						<div class="col-12 col-lg-10 col-md-10 col-sm-12 d-flex p-1">		
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
							<input type="button" name="btnImprimir" id="btnImprimir" value="Imprimir Solicitud" onclick="imprimirRegistroIngreso()" class="btn btn-secondary btn-sm"/>
						</div>											
					</div>			
									
				</div>							
				
			</div>
			
			<div id="divIngresoFiscal">
				<h5> Detalle presupuestal </h5>
				<hr class="mt-3"/>
	
				<div class="row">		
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
						<label for="EP"> Estructura Programática &nbsp;&nbsp; </label>		 							
					</div>											
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">			
						<span id="importaEPs"> 					
							<input type="checkbox" id="importar" value="0" onclick="importarEPs()"/> &nbsp;
							<label for="importar"> Importar EP's </label>
						</span>										 							 
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<label for="mImporte"> Importe EP </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<label for="acumuladoImporte"> Acumulado EP's </label>
					</div>								
				</div>		
				
				<div class="row">		
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
						<input type="hidden" id="cOBGT" name="cOBGT"/>
						<input type="hidden" name="nClaveCNA1" id="nClaveCNA1"/>
						<input type="text" id="EP" name="EP" size="68" readonly class="form-control form-control-sm"/>						 							
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<input type="button" id="nIdClaveEgresos2" size="5"  value="..." onclick="muestraAyudaEPS()" class="btn btn-secondary btn-sm"/> &nbsp;
						<input type="button" value="Agregar" onclick=" validaCapturaEP();" id="agregar" name="agregar" style="float:right;" class="btn btn-secondary btn-sm"/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<input name="mImporte" type="text" style="text-align: right" id="mImporte" onpaste="return false" onkeypress="return validar(event)" class="form-control form-control-sm"/>
					</div>								
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<input name="acumuladoImporte" type="text" style="text-align: right" id="acumuladoImporte" value="0" class="form-control form-control-sm" readonly/>
					</div>								
				</div>											
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<table id="dt_regIngreso" class="table table-striped table-bordered">								
							<thead>
								<tr>
									<th># Renglon</th>
									<th>Estructura Programatica</th>
									<th>Mes</th>
									<th>Importe</th>							
								</tr>
							</thead>
						</table>	
					</div>
				</div>							
										
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
					</div>							
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
						<input type="button" name="btnBorraEPs" id="btnBorraEPs" value="Borra Tabla" onclick="borrarRegistroIngreso()" class="btn btn-secondary btn-sm"/>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
						<input type="button" name="btnImprimir" id="btnImprimir" value="Imprimir Solicitud" onclick="imprimirRegistroIngreso()" class="btn btn-secondary btn-sm"/>
					</div>											
				</div>		
			</div>
				
			<div id="dialogInsertar" title="Mensajes del sistema. Registro de Ingresos.">
				<p>Mensajes del sistema </p>
				<a rel=""></a>
				<textarea id="mensajeInserta" name="mensajeInserta" rows="8" cols="120" class="form-control form-control-sm"></textarea>
			</div>
						
			<div id="dlgFirmantes" title="Firmantes Autorización"> 
				<h5> Datos Firmantes VºBº </h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cNombreVoBo" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cNombreVoBo" name="cNombreVoBo"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPaternoVoBo" class="form-label"> Ap. Paterno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPaternoVoBo" name="cPaternoVoBo"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cMaternoVoBo" class="form-label"> Ap. Materno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cMaternoVoBo" name="cMaternoVoBo"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPuestoVoBo" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPuestoVoBo" name="cPuestoVoBo"/>
					</div>
				</div>
				
				<h5> Datos Firmantes Autorización </h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cNombreAut" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cNombreAut" name="cNombreAut"/>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPaternoAut" class="form-label"> Ap Paterno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPaternoAut" name="cPaternoAut"/>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cMaternoAut" class="form-label"> Ap Materno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cMaternoAut" name="cMaternoAut"/>
					</div>
				</div>			
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPuestoAut" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPuestoAut" name="cPuestoAut"/>
					</div>
				</div>								
			</div>
		
			<div id="dlgFirmantesUpdate" title="Actualizacion de Firmantes">
				<h5> Actualiza Datos Firmantes VºBº </h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cNombreVoBoUpdate" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPaternoVoBoUpdate" class="form-label"> Ap. Paterno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cMaternoVoBoUpdate" class="form-label"> Ap. Materno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPuestoVoBoUpdate" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate"/>
					</div>
				</div>
				
				<h5> Actualiza Datos Firmantes Autorización </h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cNombreAutUpdate" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cNombreAutUpdate" name="cNombreAutUpdate"/>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPaternoAutUpdate" class="form-label"> Ap Paterno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPaternoAutUpdate" name="cPaternoAutUpdate"/>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cMaternoAutUpdate" class="form-label"> Ap Materno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cMaternoAutUpdate" name="cMaternoAutUpdate"/>
					</div>
				</div>			
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPuestoAutUpdate" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPuestoAutUpdate" name="cPuestoAutUpdate"/>
					</div>
				</div>
											
			</div>
		</div>
	</form>
</body>
</html>
