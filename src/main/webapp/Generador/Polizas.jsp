<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>
<%!private Logger log = Logger.getLogger(getClass());%>
<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String nomUsuario = "";
	String nomLargoUsuario = "";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
					
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	
	//Documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G"
			+ c.getIdGabinete();//se usa por separado abajo
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	nomUsuario = usuario.getLogin();
	nomLargoUsuario = usuario.getNombre();
	try{
		Connection conn=cbl.getConnection();
		CasoDato cd = new CasoDato();
		cd.setIdCaso(c.getIdCaso());
		c.setCasoDato(CasoDatoManager.select(conn, cd));
		conn.close();
		conn=null;
	}
	catch(Exception expropcte){log.error("Error leyendo CasoDato: ",expropcte);}
	
	String nNumEmpleado = usuario.getNumeroEmpleado();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>P&oacute;lizas</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="-1">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		
		<link rel="stylesheet" type="text/css"
			href="../Ayudas/css/autocompleta.css"></link>
		<style>
			.ui-autocomplete {
				max-height: 100px;
				overflow-y: auto;
				/* prevent horizontal scrollbar */
				overflow-x: hidden;
			}
			
			/* IE 6 doesn't support max-height
				     * we use height instead, but this forces the menu to always be this tall
				     */
			* html .ui-autocomplete {
				height: 100px;
			}
		</style>

		<style type="text/css" title="currentStyle">
		
@import "css/demo_page.css";
@import "css/demo_table_jui.css";
@import "themes/smoothness/jquery-ui-1.8.4.custom.css";


</style>

		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
		<script type="text/javascript"
			src="js/jquery.dataTables.editable-1.3.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript"
			src="../js/ContabilidadCentroContable.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="js/Poliza.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var usuarioCapturaPoliza = <%=usuario.getGrupo("CAPTURA_POLIZA") != null%>;
		var usuarioRevisionPoliza = <%=usuario.getGrupo("REVISION_POLIZA") != null%>;
		var usuarioAutorizaPoliza = <%=usuario.getGrupo("AUTORIZA_POLIZA") != null%>;
		var usuarioCCentroContable = '<%=cCentroContable%>';
		var nNumEmpleado = "<%=nNumEmpleado%>";
		
		var asInitVals = new Array();
		var arrRet = null;
  	    var aSelected = [0];
		var rowCount = 0 ;

		var oTable;
		var oCurrentFocus;
		var oTableCuen;
		var ppMovimientoDetalle;
		
		var creado = false;
		var oClck = false;
		
		var ArrayDatos;
		var detalleC=false;
		
		
function cierraPantalla(newid_oper)
	{
		$("#Status").val(newid_oper);
		queryFormPost("ActualizaCOPERPolizaUpdate2", {async : false});		
	  	document.redireccionar.submit();
	
	}
	
function abrirDialog()
	{
	
		$("#dialog-procesar").dialog( "open" );
		
	}
											
function toRevision()
	{
	

				$.ajax({
					type: "GET",
					async: false,
					url: "../gstn/GestionPoliza",
					data: { "folioDocumento":$("#id_caso").val(),"id_oper":"2"},								
					success: function (msg) {
						
					    ArrayDatos=msg.split("//");	
					    
					   	                   
					    $("#dialog-procesar" ).dialog( "close" );
					    
						cierraPantalla(2);

					    if(ArrayDatos[0]=='true')
					     {		
					    	
					    	
					    	//$("#Status").val(2);
						    //$("#cDocumentoHaplicado").val('S');
						    folio();
						    //$("#nCambio").val(2);
					        //creado = true;	
					        
					        parent.document.getElementById("pb_send").disabled = false;    					
			                comprobacionPoliza();    					
			                parent.document.getElementById("pb_send").click(); 
			                
			                
		                 }
					    else
					    	{
					    	 alert(ArrayDatos[1]);
					    	 false;					    	
					    	}
					    
					    
					    
					    }
					});	
    
   
	}	

function toAutorizacion()
{

	
	$.ajax({
	type: "GET",
	async: false,
	url: "../gstn/GestionPoliza",
	data: { "folioDocumento":$("#id_caso").val(),"id_oper":"3"},								
	success: function (msg) {
	
	ArrayDatos=msg.split("//");	
	
	$("#dialog-procesar" ).dialog( "close" );
	
	cierraPantalla(3);
				
	if(ArrayDatos[0]=='true')
	{
		
		$("#cadFolio").val("POLI-C" + $("#cCentroContable").val() + "-"+ $("#id_caso").val());
		$("#resp").val("AUTORIZA_POLIZA");	
		
		if (usuarioAutorizaPoliza) 
		{	
		
			//$('#pCuentas').dataTable().fnClearTable();
			//$('#pCuentasRev').dataTable().fnClearTable();
			//$('#pCuentasAut').dataTable().fnClearTable();
			
			//autorizacion();	
			
			
			
			folio();
			
			
			
			$("#tba1").hide();
			$("#tba4").hide();
			$("#tba5").show();
			$("#tba5").click();
		
		} 
		else 
		{	  
		  document.redireccionar.submit();
		}
	
	
	
	}
	
	
	else
	{
		alert(ArrayDatos[1]);
		return;
	}
	
	
	
	}
	});	
		
	
}

function toConsulta()
	{
	
	$.ajax({
		type: "GET",
		async: false,
		url: "../gstn/GestionPoliza",
		data: { "folioDocumento":$("#id_caso").val(),"id_oper":"4"},								
		success: function (msg) {
			
			
		$("#dialog-procesar" ).dialog( "close" );
		
		ArrayDatos=msg.split("//");
		
		if(ArrayDatos[0]=='true') 
		{	
			alert("Autorización Guardada Correctamente");
			document.redireccionar.submit();		
		}
		
		else
			
		{
			alert(ArrayDatos[1]);
			return;
		}
	
	}});
			
	
	
	}

function backToCaptura()
{
  $.ajax({
	type: "GET",
	async: false,
	url: "../gstn/GestionPoliza",
	data: { "folioDocumento":$("#id_caso").val(),"id_oper":"1"},								
	success: function (msg) {
	
	$("#dialog-procesar" ).dialog( "close" );
	
	ArrayDatos=msg.split("//");
	
	cierraPantalla(1);

		if(ArrayDatos[0]=='true')
		{		
		
			if (usuarioCapturaPoliza)
			{		
				$('#pCuentas').dataTable().fnClearTable();
				$('#pCuentasRev').dataTable().fnClearTable();
				$('#pCuentasAut').dataTable().fnClearTable();
				
				folio(); 
				
				$("#tba1").click();
				$("#tba1").show();
				$("#tba2").hide();
				$("#tba3").hide();
				$("#tba4").hide();
				$("#tba5").hide();
				$("#cComentarios").val($("#cComentariosRev").val());
				$("#cancelar").attr("disabled", true);
				$("#cDocumentoHaplicado").val("");
			
			} 
			else 
			{		
				document.redireccionar.submit();		
			}
		
		
	
	
	}
	else
	{
	alert(ArrayDatos[1]);
	return;
	}
	
	
	
	}
	});

	
}

function backToRevision()
{

	$.ajax({
		type: "GET",
		async: false,
		url: "../gstn/GestionPoliza",
		data: { "folioDocumento":$("#id_caso").val(),"id_oper":"2"},								
		success: function (msg) {
		ArrayDatos=msg.split("//");
		
		$("#dialog-procesar" ).dialog( "close" );
		
		cierraPantalla(2);
		
		if(ArrayDatos[0]=='true')
		{
		
			if (usuarioRevisionPoliza) {
			
			alert("Autorización Guardada Correctamente");
			
			$('#pCuentas').dataTable().fnClearTable();
			$('#pCuentasRev').dataTable().fnClearTable();
			$('#pCuentasAut').dataTable().fnClearTable();
				
			folio();
			
			$("#cComentarios").show();
			$("#tba5").hide();
			$("#tba1").hide();
			$("#tba2").hide();
			$("#tba3").hide();
			$("#tba4").show();
			} 
			else {	
			
			document.redireccionar.submit();				
			
			}
		
		
		}
		else
		{
		alert(ArrayDatos[1]);
		return;
		
		}
	
	
	}});

	
}



		
$(document).ready(function(	)
		{

	$('#dtGuardaDetalle').dataTable({
		"iDisplayLength": 500,
      	"bPaginate": false,
      	"bFilter": false,
      	"bSort": false,
      	"bInfo": false } );

	
			
 $('#dialog-procesar').dialog({
		
	    autoOpen: false,
	    modal: true,
	    resizable: false,
	    width: 500,
	    heigth: 900,
	    title: 'Procesando',
	    show: "blind",
	    hide: "scale",
	    closeOnEscape: false,
		overlay: { backgroundColor: '#FFF',opacity: 6.5 }		
 });
		
		
			
			$("#nCuenta").bind( "keydown", function( event ) {
                return catchTab(event, "nCuenta");
            }).autocomplete({
				delay:100,
				minLength: 0,
				source: "../CuentaContable/AutoCompletaCuenta",
				select:function(event, ui){
					$("#nCuenta").val(ui.item.value);
					queryFormPost({
					queryName : "leeAttrCuenta",
					async : false,
					callback : function() {
						if ($("#dCuenta").val() == "") {
							alert("La cuenta " + $("#nCuenta").val()
									+ " no existe o no es una cuenta de aplicacion.");
							$("#nCuenta").val('');
							$("#nCuenta").focus();
							return false;
						}
						tipoSubCuenta();
						$("#divACnCuenta").hide();
						return true;
					}
				});
				}
			});
					
			
		
			$("input.AyudaSyC").subIniciaDlg();
    		$("input.autoCompletaSyC").subIniciaAutoCompleta();

			$('.currency').blur(function()
			{
				$('.currency').formatCurrency();
			});

			$("#usuario").val( "<%=nomUsuario%>" );
		    $("#tabs").tabs( {
		        "show": function(event, ui) {
		            var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		            if ( oTable.length > 0 ) {
		                oTable.fnAdjustColumnSizing();
		            }
		        }
		    } );
		    
		    $("#nNumEmpleadoElab").val(nNumEmpleado);

		});
		
		$(function() {
			$( "#fAplicacionC" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});


		$(function() {
				$( "#fCapturaC" ).datepicker({
					showOn: "button",
					dateFormat: "dd/mm/yy",
					buttonImage: "images/calendar.gif",
					buttonImageOnly: true
				});
		});

		$(function() {
			$( "#fAplicacion2C" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});


		$(function() {
				$( "#fCaptura2C" ).datepicker({
					showOn: "button",
					dateFormat: "dd/mm/yy",
					buttonImage: "images/calendar.gif",
					buttonImageOnly: true
				});
		});
		

	function onSubmit(id_oper){//validaciones del boton guardar
  		
		var p = window.parent;
  		var valida_campos = true;
		try{
			if (id_oper==1){
				
				<%if (c.getCasoDato("APLICADO_CONT").getValor() == null) {%>
				p.gestion.setFolio( $("#FOLIO").val() );
//				p.gestion.setOperador( $("#nomLargoUsuario").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
//				p.gestion.setOperador( nomLargoUsuario );
				p.gestion.setFechaDocumento('<%=today%>');
				p.gestion.setEjercicioFiscal( $("#EF").val() );
				p.gestion.setConceptoMov("Aplicación Poliza");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				<%}%>
			}
			if (id_oper==3){
				p.gestion.setFechaApCont('<%=today%>');//cambiar ala fecha del sistema
			}

		} catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
	}

	function onPostSubmit(id_oper) {
		if ($("#nCambio").val() == 2) {
			
			
			actualizaEstatusPoliza('E');
			return !usuarioRevisionPoliza;
		} else if ($("#nCambio").val() == 3 && !usuarioAutorizaPoliza)
			return true;
		else if (($("#nCambio").val() == 10 || $("#nCambio").val() == 4)
				&& !usuarioCapturaPoliza)
			return true;
		else if (Number($("#nCambio").val()) == 5 && $("#autorizar").val() == 1)
			return true;
		else
			return false;
	}
//*****************************************************************************************************************
	function onLoadPlantilla(id_oper) {
			$("#U_NOMBRERev").val($("#hU_NOMBRE" ).val());
			$("#U_NOMBREAut").val($("#hU_NOMBRE" ).val());
			
			getCentroContable();
			$("#cCentroContable").val( newCC);
			parent.document.getElementById("pb_cancel").style.display = 'none';
			Limpia();
			queryFormPost({
				queryName:"POLIZAMcreadaRead",
				//****
				async: false,
				//async: true,
				callback: function(){
					$("#TcargosLbl").text( $("#Tcargos").val() );
					$("#TabonosLbl").text( $("#Tabonos").val() );
					
					if ($("#nCambio").val() == 0 || $("#nCambio").val() == '') {
						getCentroContable();
						$("#cCentroContable").val(newCC);
						$("#fAplicacion").val(fechaAplicacion());
						$("#fCaptura").val(today());
						changeFAplicacion();
						$("#hPolCtroContable").val(newCC);
						updateCCont();
						$("#hPolCtroContableRev").val(newCC);
						$("#hPolCtroContableAut").val(newCC);
					} else {
						getCentroContable();
						$("#cCentroContable").val(newCC);
						changeFAplicacion();
						var dateArr = $("#fAplicacion").val().split("/");
						$("#nMes").val(parseInt(dateArr[1], 10));
						
						queryFormPost("dMes", {
							async : false,
							callback : function() {
								if($("#dMes").val()==13){
									$("#nMes").val("13");
									$("#mes13").val(1);
								}else if(  $("#nMes").val() != $("#dMes").val() ){
									alert("El mes "  + $("#nMes").val() + " no esta abierto. El mes actual abierto es " + $("#dMes").val() + 
									"\nNo podra realizar alguna operacion en esta poliza hasta que el mes " + $("#nMes").val() + " este abierto");
									$(":input").each(function(){
										if( $(this).attr("type")== 'text' || $(this).attr("type")== 'hidden' || $(this).attr("type")== 'textarea')
											$(this).attr('readonly', 'readonly');
										else
											$(this).attr('disabled', 'disabled');
									});
								}	
							}
						});
						
					}
					if( $("#nMes").val() != 13 ){
						$("#Periodo13Div").hide();
						$("#periodo13RevDiv").hide();
						$("#periodo13AutDiv").hide();
						
						$("#AjusteCapt").hide();
						$("#AjusteRev").hide();
						$("#AjusteAut").hide();	
						
						$("#mes13").val(0);
						
						$("#periodo13").val("N");
						$("#periodo13Rev").val("N");
						$("#periodo13Aut").val("N");
						
					}else{
						$("#Periodo13Div").show();
						$("#periodo13RevDiv").show();
						$("#periodo13AutDiv").show();
						$("#periodo13").val("S");
						$("#periodo13Rev").val("S");
						$("#periodo13Aut").val("S");
						$("#AjusteCapt").show();
						$("#AjusteRev").show();
						$("#AjusteAut").show();
						
						$("#mes13").val(1);
					}
					changeFCaptura();
				}
			});

			ocultaOperacionesFlujo();
			$('#cUnidadResponsable').val("<%=cUR%>");

			/* Tabla en la que se agregan los movimientos contables de la poliza, se agrega la funcionalidad de respuesta al dar doble click en un renglon */
			oTableMov = creaTblMovientos();
			dobleClickTblMovimiento();

			/* Tabla en la que se encuentran los datos de la cuenta, se agrega la fincionlidad de respuesta al dar doble click en un renglon */
			oTableCuen = creaTblPCuentas();
			dobleClickTblCuentas();
			clicTblCuentas();
			/* Tablas para cuentas */
 			creaTablaCuentasRev();
			creaTablaCuentasAut();

			ppMovimientoDetalle = creaTablaMovimientoDetalle();
				
		    moneyFrmt("Cargos", "0");
			moneyFrmt("Abonos", "0");
			carga();
			createButtons();
			cambioALM();

			$("#hPolCtroContable").attr("disabled", true);
			$("#hPolCtroContableRev").attr("disabled", true);
			$("#hPolCtroContableAut").attr("disabled", true);
			
			$("#cConcepto").focus();


	
	if (Number($("#FolioPolizaRev").val())>0  && id_oper == 1)
		$("#cancelar").attr("disabled", true);
		else {
		$("#cancelar").attr("disabled", false);
		}
	 $("#usuario").val("<% out.print(usuario.getLogin());%>"); 
	 $("#nomLargoUsuario").val("<% out.print(usuario.getNombre());%>");
	 $("#OPERADOR").val("<% out.print(usuario.getNombre());%>");       
	}
	
//***************************************************************************************************************	
	function ResponsableSiguiente(id_oper) {

		if (id_oper == 1)
			return "REVISION_POLIZA";
		else if (id_oper == 2 && $("#evelvar").val() == 1)
			return "AUTORIZA_POLIZA";
		else if (id_oper == 2 && $("#evelvar").val() != 1)
			return "CAPTURA_POLIZA";
		else if (id_oper == 3 && $("#autorizar").val() == 1)
			return "CONSULTA_POLIZA";
		else if (id_oper == 3 && $("#autorizar").val() != 1)
			return "REVISION_POLIZA";
	}

	function OperacionSiguiente(id_oper) {

		if (id_oper == 1){
			return "REVISION_POLIZA";
			}		
		else if (id_oper == 2 && $("#evelvar").val() == 1)
			return "AUTORIZA_POLIZA";
		else if (id_oper == 2 && $("#evelvar").val() != 1)
			return "CAPTURA_POLIZA";
		else if (id_oper == 3 && $("#autorizar").val() == 1)
			return "CONSULTA_POLIZA";
		else if (id_oper == 3 && $("#autorizar").val() != 1)
			return "REVISION_POLIZA";
	}

	function onPostDisplay(id_oper) {

	}

	function formSubmited() {
	}

	var renglon = 0;
	var posicion = 0;
	var cargotmp = 0;
	var abonotmp = 0;

	
	function cmdGuardar() 
	
	{   
		
		
		//alert($("#nCambio").val());
		
		
		var msg = '';
		
		if ($("#nCambio").val() == 2 && $("#evelvar").val() == 2 ){
				$("#usuario").val( "" );
		} else if ($("#nCambio").val() == 3 && $("#autorizar").val() == 2 ) {
				$("#usuario").val( "" );
		}
		else {
			$("#usuario").val( "<%=nomUsuario%>" );
		}
		$("#cReferenciaPolizaRev").val($("#cReferenciaPoliza").val());
		$("#cReferenciaPolizaAut").val($("#cReferenciaPoliza").val());
		$("#hPolCtroContable").attr("disabled", false);

		
		var hayError = '';
		$("#fAplicacion").attr('disabled', false);
		$("#hPolCtroContable").attr("disabled", false);
				

		if (($("#nCambio").val() == 10 || $("#nCambio").val() == null || $("#nCambio").val() == '0')) 
		
		{
			var arrCtasBloq = validaCuentasBloqueadas();
			if (arrCtasBloq.length > 0) {
				var ctas = '';
				for ( var i = 0; i < arrCtasBloq.length; i++)
					ctas = arrCtasBloq[i] + "\n";

				alert("No se puede avanzar el caso ya que las siguientes cuentas se encuentran bloqueadas:\n"
						+ ctas);
				return;
			}
			
			if (!validaPolizaCuadra()) {
				alert("La poliza no Cuadra. Por favor valide los cargos y abonos.");
				return;
			}
			

			var err = validaCaptura();
			if (err.length > 0) {
				var errMsg = 'Falto capturar los siguientes campos requeridos: ';
				for ( var i = 0; i < err.length; i++)
					errMsg += "\n" + err[i];

				alert(errMsg);
				return;
			}
			
			$("#cComentariosRev").val("");
			$("#cComentarios").val("");
			
			
			
			if ($("#nCambio").val() < 1) 
			{		    
			     //queryFormPost("tPolizaEncabezadoCreate", {async : false});
			     creacionDetalle("tPolizaEncabezadoCreate,");		     
		    }
		    
			else
			{							 	
			 /*	queryFormPost("tDocPolizaDetalleDelete,ActualizaPolizaEMUpdate", {async : false});			
				creacionDetalle("");			
				queryFormPost("actualizaTPOLConcepto", {async : false});*/
				creacionDetalle( "tDocPolizaDetalleDelete,ActualizaPolizaEMUpdate,actualizaTPOLConcepto,");	
			}

			//queryFormPost("tDocPolizaDetalleDelete,ActualizaPolizaEMUpdate", {async : false});			
					
			//queryFormPost("actualizaTPOLConcepto", {async : false});

			
			//avanzaRevision();
			
			

			if ($("#cDocumentoHaplicado").val() == ""|| $("#cDocumentoHaplicado").val() == " ") 
			{		
				$("#tOperacion").val("Avanzando a revision");			
			     queryFormPost("tDocPolizaBitacora", {	async : false});
			     
				abrirDialog();
				setTimeout("toRevision()", 500);				
			} 
			
		} else if ($("#nCambio").val() == 2) {
			
			
			
			$("#cComentariosAut").val($("#cComentariosRev").val());
			
			if (Number($("#evelvar").val()) == 1) 			
			{			
				$("#tOperacion").val("Avanzando a autorizacion");			
			    queryFormPost("tDocPolizaBitacora", {	async : false});
			    
                abrirDialog();
                setTimeout("toAutorizacion()", 500);			
				
			} else {	
				
				$("#usuario").val( "<%=nomUsuario%>" );
			    $("#tOperacion").val("Regresando de revision a Captura");			
			    queryFormPost("tDocPolizaBitacora", {	async : false});
						
				abrirDialog();				
				setTimeout("backToCaptura()", 500);					
								
			}
			
			
			
		} else if (Number($("#nCambio").val()) == 3) 
		
		{
			
			
			
			if ($("#autorizar").val() == 1)
			{			
				
				$("#tOperacion").val("Avanzando de autorizacion a consulta");			
			    queryFormPost("tDocPolizaBitacora", {	async : false});
			    
				abrirDialog();
				setTimeout("toConsulta()", 500);			

			} 			
			
			else { 
				
				$("#cComentarios").val($("#cComentariosAut").val());
				
				$("#usuario").val( "<%=nomUsuario%>" );
				$("#tOperacion").val("Regresando de autorizacion a revision");			
			    queryFormPost("tDocPolizaBitacora", {	async : false});
				
				queryFormPost("tdocPolizaComentarioAutUpdate", {async : false});
				
				abrirDialog();
				setTimeout("backToRevision()", 500);					

			}
			
		 	
			
		} 
		
		$("#hPolCtroContable").attr("disabled", true);
		
	}
</script>
		<script type="text/javascript">
	function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		patron = /[$A-Za-z._\d\s\\.\\-]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	function eleva(valor) {
		$("#evelvar").val(valor);
	}
	function autoriza(valor) {
		$("#autorizar").val(valor);
	}
	function cambioALM() {
		querySelectPost("CatalogoAlmacenPolizaRead", "ALM", {
			async : false
		});
		$("#nIdAlmacen").val($("#ALM").val());
		if ($("#nIdAlmacen").val() == '') {
			querySelectPost("CatalogoAlmacenVacioRead", "ALM", {
				async : false
			});
		}
 
	}

	function cmdBorrar() {

		//queryFormPost("tPolizaEncabDelete", {async : false});
		//queryFormPost("tPolizaDetalleDelete", {async : false});

		$("#tOperacion").val("Documento Descartado");			
	    queryFormPost("tDocPolizaBitacora", {async : false});

		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled = true;

	}

	function cmdGuardarParcial() {
		$("#cReferenciaPolizaRev").val($("#cReferenciaPoliza").val());
		$("#cReferenciaPolizaAut").val($("#cReferenciaPoliza").val());
		
		queryFormPost("ActulizaCasoPoliza", {
			async : false
		});

		$("#hPolCtroContable").attr("disabled", false);

		if (toUpdate >= 0) {
			alert("Debe terminar de editar el movimiento antes de continuar");
			return;
		}
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").click();
			parent.document.getElementById("pb_save").disabled = true;

		moneyFrmt("Cargos", 0);
		moneyFrmt("Abonos", 0);

		if ($("#nCambio").val() == 0 || $("#nCambio").val() == null || $("#nCambio").val() == '') 
		
		{
			$("#nCambio").val(10);
			
			queryFormPost("tPolizaEncabezadoCreate", {
				async : false
			});

			
			$("#Status").val(1);
			$("#cadFolio").val("POLI-C" + $("#cCentroContable").val() + "-"+ $("#id_caso").val());
			
			$("#resp").val("CAPTURA_POLIZA");

			
			$("#tOperacion").val("Guardado Parcial ncambio 0");			
			queryFormPost("tDocPolizaBitacora", {	async : false});
			
			//creacionDetalle("");
			creacionDetalle( "ActualizaCOPERPolizaUpdate,ActualizaOPERADORPolizaUpdate," );
			
			if(detalleC)
				{
				alert("Caratula Guardada Correctamente");
				cierraPantalla(1);
			    }
			else
				{
				alert("Error al guardar Caratula");
				}


			
			
			

			
		} else if ($("#nCambio").val() == 10 || $("#nCambio").val() == '4') 
		   {
			$("#cComentariosRev").val($("#cComentarios").val());
			$("#cComentariosAut").val($("#cComentariosRev").val());
			$("#nCambio").val(10);			
			
			$("#Status").val(1);

			$("#cadFolio").val(
					"POLI-C" + $("#cCentroContable").val() + "-"
							+ $("#id_caso").val());
			$("#resp").val("CAPTURA_POLIZA");


			//creacionDetalle( "ActualizaCOPERPolizaUpdate,ActualizaOPERADORPolizaUpdate,ActualizaPolizaMUpdate," );
			//queryFormPost("ActualizaOPERADORPolizaUpdate", {async : false});
			//alert("Caratula Guardada Correctamente");

			
			$("#tOperacion").val("Guardado Parcial ncambio 10");			
			queryFormPost("tDocPolizaBitacora", {	async : false});
			
			
			/*queryFormPost("", {async : false});			
			creacionDetalle("");			
			queryFormPost("", {async : false});*/
			
			creacionDetalle( "tDocPolizaDetalleDelete,ActualizaPolizaEMUpdate,actualizaTPOLConcepto," );
			
			
			if(detalleC)
				{
				alert("Caratula Guardada Correctamente");
				cierraPantalla(1);
			    }
			else
				{
				alert("Error al guardar Caratula");
				}
			
		}
		Limpia();
		$("#hPolCtroContable").attr("disabled", true);
	}
</script>
	</head>
	<body id="dt_example" onload="Limpia();">
	
		<form method="post">
		
			<input type="hidden" name="nNumEmpleadoElab" id="nNumEmpleadoElab" value="-1"/>
			<input type="hidden" name="ADEFAS" id="ADEFAS" value="N"/>
			<input type="hidden" name="hU_NOMBRE" id="hU_NOMBRE" value=""/>
			<input type="hidden" name="nSubCuenta" id="nSubCuenta" />
			<input type="hidden" name="mes13" id="mes13" value="0" />
			<input type="hidden" name="cComentarios" id="cComentarios"/>
			<input type="hidden" name="tOperacion" id="tOperacion" />
			
			<input type="hidden" id="dMes" name="dMes" value="" />
			<input type="hidden" id="tipoPolizaSel" name="tipoPolizaSel" value="" />
			<input type="hidden" id="cReferenciaPoliza" name="cReferenciaPoliza"
				value="" />
			<input type="hidden" id="nFolioPolizaDef" name="nFolioPolizaDef" value="-1" />
			<input type="hidden" id="cReferenciaPolizaRev"
				name="cReferenciaPolizaRev" value="" />
			<input type="hidden" id="cReferenciaPolizaAut"
				name="cReferenciaPolizaAut" value="" />
			<input type="hidden" id="referencia" name="referencia" />

			<input type="hidden" id="referenciaMvto" name="referenciaMvto" value"" />
			<input type="hidden" id="cDocumentoHaplicado"
				name="cDocumentoHaplicado" value"" />
			<input type="hidden" id="tmpInpt" name="tmpInpt" value"" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" />
			<input type="hidden" id="cRamo" name="cRamo" value="<%=cRamo%>" />
			<input type="hidden" id="cUnidadResponsable"   
				name="cUnidadResponsable" value="" />
			<input type="hidden" id="usuario" name="usuario" />
			<input type="hidden" id="nomLargoUsuario" name="nomLargoUsuario" value="<%=nomLargoUsuario%>" />		
			<input type="hidden" id="nCambio" name="nCambio" value="0" />
			<input type="hidden" id="evelvar" name="evelvar" value="1" />
			<input type="hidden" id="autorizar" name="autorizar" value="1" />
			<input type="hidden" id="nIdAlmacen" name="nIdAlmacen" value="" />
			<input type="hidden" id="nDato" name="nDato" value="0" />
			<input type="hidden" id="nRenglon" name="nRenglon" value="1" />
			<input type="hidden" id="mesAbierto" name="mesAbierto" value="0" />
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" />
			<input type="hidden" id="Status" name="Status" value="0" />
			<input type="hidden" id="cadFolio" name="cadFolio" value=" " />
			<input type="hidden" id="resp" name="resp" value=" " />
			<input type="hidden" id="nMes" name="nMes" />
			<input type="hidden" id="valSubCuenta" name="valSubCuenta" />
			<input type="hidden" id="valSubCuentaCompar" name="valSubCuentaCompar" />
			<input type="hidden" id="dBanco" name="dBanco" />
			<input type="hidden" id="dSucursal" name="dSucursal" />
			<input type="hidden" name="FOLIO" id="FOLIO"
				value="<%=c.getFolio()%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR"
			value="<%=nomLargoUsuario%>" />
			<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA"
				value="<%=today%>" />
			<input type="hidden" id="caso_id" name="caso_id"
				value="<%=c.getIdCaso()%>" />
			<input type="hidden" id="nFormatoPoliza" name="nFormatoPoliza"
				value="1" />
			<h1>P&oacute;lizas</h1>
			
			<div id="divConsulta" align="center">
				<label id="lbConsulta" style="font-size: large; color: #FF0000;">
					El Numero de Poliza es :
				</label>
		<input type="text" id="nFolioPoliza" name="nFolioPoliza" style="font-size: 18px; color: #FF0000; border: #FFFFFF;" readonly="readonly" />
			</div>
			<div id="container" class="container SyCData">
				<div id="tabs">
					<ul class="tabs">
						<li><a id="tba1" href="#tabs-1">Captura P&oacute;liza</a></li>
						<li><a id="tba4" href="#tabs-4">Revision P&oacute;liza</a></li>
						<li><a id="tba5" href="#tabs-5">Autorizaci&oacute;n P&oacute;liza</a></li>
						<li><a id="tba2" href="#tabs-2">Duplicar P&oacute;liza</a></li>
						<li><a id="tba3" href="#tabs-3">Datos a Duplicar</a></li>
					</ul>
					
					<div id="tabs-1">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table id="folioFechaTbl" width="100%">
										<tr>
											<td align="right">
												<label id="id_caso_lbl" style="font-weight: bold;">
													Folio Documento:
												</label>
											</td>
											<td align="left">
												<label id="id_caso_lbl" style="font-weight: bold;"><%=request.getParameter("folio")%></label>
												<input type="hidden" id="id_caso" name="id_caso" value="<%=request.getParameter("folio")%>">													
											</td>											
											<td align="right">Fecha de Captura:</td>
											<td align="left"><label id="fCatpuraLbl">&nbsp;</label>
											<input type="hidden" name="fCaptura" id="fCaptura" value=""	onchange="javascript:changeFAplicacion()">
											</td>
											<td align="right">
												Fecha Aplicaci&oacute;n:
											</td>
											<td align="left">
												<label id="fAplicacionLbl">&nbsp;</label>
												<input type="hidden" name="fAplicacion" id="fAplicacion" onchange="changeFAplicacion()">
												
												<input type="hidden" name="ndocrenglon" id="ndocrenglon" />
												<input type="hidden" name="Id_Caso_Oper" id="Id_Caso_Oper" />
												
											</td>
										</tr>
										<tr>
											<td align="right">Ejercicio Fiscal:	</td>
											<td align="left"><select id="EF" name="EF"></select></td>
											<td align="right">Tipo Poliza:</td>
											<td align="left"><select id="PolTipo" name="PolTipo" class="encabezado"></select></td>
											<td align="left">
												<div id="Periodo13Div">
													&nbsp;
													<input type="hidden" name="periodo13" id="periodo13" value="N">
													<input type="hidden" id="CtroContable" name="CtroContable" />
													<input type="checkbox" name="periodo13Cap" id="periodo13Cap"  checked="checked" disabled="disabled" value="1">
													<label for="periodo13Cap">Periodo 13</label>
												</div>
											</td>
											<td align="left">&nbsp;	</td>
										</tr>
										<tr>
											<td align="right">Centro Contable:</td>
											<td align="left" colspan="5"><select id="hPolCtroContable" name="hPolCtroContable" class="encabezado" onChange="cambioALM()"></select></td>
										</tr>
										<tr>
											<td colspan="6" align="left">
												<div id="AjusteCapt">
													<table>
														<tr>
															<td align="right">Ajuste:</td>
															<td align="left" colspan="5">
																<select id="nTipoAjuste" name="nTipoAjuste" onchange="updateAjuste()" >
																	<option value=""></option>
																	<option value="1">Ajuste Previo</option>
																	<option value="2">Ajuste Presupuestario</option>
																	<option value="3">Ajuste de Resultados</option>
																</select>
															</td>
														</tr>
													</table>
												</div>
											<td/>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="100%">
									<table width="100%">
										<tr>
											<td colspan="3" align="left">Concepto :</td>
											<td colspan="3" align="left"><label id="lbObservaciones" style="visibility: hidden;">Observaciones:	</label></td>
										</tr>
										<tr>
											<td colspan="3"><textarea class="encabezado" name="cConcepto" id="cConcepto" rows="5%" cols="50%"></textarea></td>
											<td colspan="3" align="left">
												<table width="100%" align="left">
													<tr><td align="right"><!-- Parcial: -->	</td></tr>
													<tr><!--
														<td align="right">
															<input type="checkbox" id="movParcialidad"
																name="movParcialidad" value="S"
																onclick="clickParcial(this.checked)">
														</td> -->
														<td align="right">
															Cuenta:
														</td>
														<td align="left">
															<input type="text" name="nCuenta" id="nCuenta" size="25"
																maxlength="30" onchange="tipoSubCuenta()"
																class="AyudaSyC" />
														</td>
													</tr>
													<tr>
														<td align="right">
															Descripcion:
														</td>
														<td align="left">
															<input name="dCuenta" type="text" id="dCuenta"
																onChange="tipoSubCuenta()" readonly="readonly" size="25" />
														</td>
													</tr>
													<tr>
														<td align="right">
															<label id="lblAuxiliar">
																Auxiliar:
															</label>
														</td>
														<td align="left">
															<div id="sinAuxiliar">
																Cuenta sin auxiliar.
															</div>
															<div id="divAML" style="display: none">
																<select id="ALM" name="ALM" onfocus="cierraAyuda()"
																	onkeypress="return toNext(event,this.id)"></select>
															</div>
															<div id="divRFC">
																<input type="text" name="cIDRFC" id="cIDRFC"
																	onfocus="cierraAyuda()" onblur=""
																	class="AyudaSyC autoCompletaSyC" />
															</div>
															<div id="divCTAB">
																&nbsp;&nbsp;&nbsp;ID:&nbsp;
																<input type="text" name="idCuenta" id="idCuenta"
																	value="" class="AyudaSyC" size="3" maxlength="3"
																	onblur="blurCtaBanc()"
																	onkeydown="return ctaKeyDwn(event, this.id)" />
																<br>
																Clabe:
																<input type="text" name="CTABAN" id="CTABAN" value=""
																	size="17" onkeydown="return ctaKeyDwn(event, this.id)"
																	onfocus="cierraAyuda()"
																	class="autoCompletaSyC AyudaSyC" />
															</div>

														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="100%">
									<table width="100%">
										<tr>
											<td colspan="6">
												<table   width="100%">
												<tr>
												<td align="left" colspan="2">
												<label>
												Repetir Cuenta y Texto:
												</label>
												<input type="checkbox" id="movParcialidad"
																name="movParcialidad" value="S"
																onclick="clickParcial(this.checked)">
												</tr>	
													<tr>
														<td align="left" colspan="2">
															<input type="text" name="Parciales" id="Parciales"
																value="" size="50" maxlength="120"
																onkeypress="return toNext(event,this.id)" />
														</td>
														<td align="right">
															        
														</td>
														
														<td align="right">
															Cargo:
														</td>
														<td align="left">
															<input type="text" name="Cargos" id="Cargos"
																onfocus="Sinfrmt(this);onFocusMoney(this);"
																onblur="onBlurMoney(this);cambiafrmt(this);"
																onkeypress="return toNext(event,this.id)"
																onkeydown="return catchTab(event,this.id)" size="10" />
														</td>
														<td align="right" >
															Abono:
														</td>
														<td align="left">
															<input type="text" name="Abonos" id="Abonos"
																onfocus="Sinfrmt(this);onFocusMoney(this);"
																onblur="onBlurMoney(this);cambiafrmt(this);"
																onkeypress="return toNext(event,this.id)" size="10" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align="right">
									<table>
										<tr>

											<td align="center">
												<input type="button" name="Agregar" id="Agregar"
													value="Agregar Movimiento" onClick="creaMovimiento()">
												<input type="button" name="Limpia1" id="Limpia1"
													value="Limpiar Campos" onClick="Limpia()"
													alt="Limpia los campos capturados al momento.">
												 
												<input type="button" value="Borrar" name="cancelar"
													id="cancelar" onClick="cmdBorrar()" />
												
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="50%" id="pCuentas" class="display">
							<thead>
								<tr align="center">
									<th>
										Renglon
									</th>
									<th>
										Cuenta
									</th>
									<th>
										Desc. Cuenta
									</th>
									<th>
										Auxiliar
									</th>
									<th>
										Descripcion Mov.
									</th>
									<th>
										Cargos
									</th>
									<th>
										Abonos
									</th>
									<th>
										Parcial									
									</th>
								</tr>
							</thead>
						</table>
						<table width="100%">
							<tr align="right">
								<td align="right">
									<table align="right">
										<tr align="right">
											<td nowrap="nowrap">
												<label style="font-style: italic;">
													Total Cargos:
												</label>
												<label style="font-weight: bolder;" id="TcargosLbl">
												</label>
												<input type="hidden" name="Tcargos" id="Tcargos" />
											</td>
											<td nowrap="nowrap">
												&nbsp;
												<label style="font-style: italic;">
													Total Abonos:
												</label>
												<label style="font-weight: bolder;" id="TabonosLbl">
												</label>
												<input type="hidden" name="Tabonos" id="Tabonos"
													onchange="alert('Hola')" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr align="center">
								<td>
									<table>
										<tr>
											<td colspan="3" align="center">
												<input type="button" value="Guardar Cambios" id="Guarda2"
													name="Guardar" onClick="cmdGuardarParcial()" />
												<input type="button" value="Guardar Enviar" id="Guarda"
													name="Guardar" onClick="cmdGuardar()" />
												<!-- 
												<input type="button" value="Cancelar Poliza" name="cancelar"
													id="cancelar" onClick="cmdBorrar()" />	
													 -->
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div id="tabs-4">
						<div id="divRevision">
							<label id="lbRevision" style="font-size: large">
								Revision
							</label>
							<input type="radio" id="grpRevision" name="grpRevision"
								onClick="eleva(1)" checked="checked" value="Si">
							Si
							<input type="radio" id="grpRevision" name="grpRevision"
								onClick="eleva(2)" value="No">
							No &nbsp;&nbsp;&nbsp;&nbsp;

							<input type="button" value="Guardar" id="GuardaEle"
								name="GuardaEle" onClick="cmdGuardar()" />
						</div>
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table id="folioFechaTbl" width="100%">
										<tr>
											<td align="left">
												<label id="id_caso_lbl" style="font-weight: bold;">
													Folio Documento:
												</label>
											</td>
											<td align="left">
												<label id="id_caso_txt" style="font-weight: bold;">
													<%=request.getParameter("folio")%>
												</label>
												<input type="hidden" id="id_casoRev" name="id_casoRev"
													value="<%=request.getParameter("folio")%>" />
											</td>
											<td align="right">
												<label id="folioPoliza_lbl" >
													Folio Poliza:
												</label>
											</td>
											<td align="left">
												<input type="text" id="FolioPolizaRev" name="FolioPolizaRev" readonly="readonly" />
											</td>
											<td align="right">
												Fecha de Captura:
											</td>
											<td align="left">
												<label id="fCatpuraRevLbl">
													&nbsp;
												</label>
												<input type="hidden" name="fCapturaRev" id="fCapturaRev"
													value="" onchange="cambiaFechaCaptura();">
											</td>
											<td align="right">
												Fecha Aplicaci&oacute;n:
											</td>
											<td align="left">
												<label id="fAplicacionRevLbl">
													&nbsp;
												</label>
												<input type="hidden" name="fAplicacionRev"
													id="fAplicacionRev">
											</td>

										</tr>
										<tr>
											<td align="right">
												Ejercicio Fiscal:
											</td>
											<td align="left">
												<input type="text" id="EFRev" name="EFRev" class="EleAut" />
											<td align="right">
												Tipo Poliza:
											</td>
											<td align="left">
												<select id="PolTipoRev" name="PolTipoRev" class="encabezado"
													disabled="disabled"></select>
											</td>
											<td align="left" colspan="2">
												<div id="periodo13RevDiv">
													&nbsp;
													<input type="hidden" name="periodo13Rev" id="periodo13Rev"
														value="N">
													<input type="hidden" id="CtroContable" name="CtroContable" />
													<input type="checkbox" name="periodo13RevChk" id="periodo13RevChk" checked="checked" disabled="disabled"
														value="1">
													<label for="periodo13RevChk">
													</label>
													Periodo 13
													<input type="hidden" id="CtroContable" name="CtroContable" />
												</div>
											</td>

										</tr>
										<tr>
											<td align="right">
												Centro Contable:
											</td>
											<td align="left" colspan="5">
												<select id="hPolCtroContableRev" name="hPolCtroContableRev"
													class="encabezado" onChange="cambioALM()"
													disabled="disabled">
												</select>
											</td>
										</tr>
										<tr>
											<!-- <td align="right">
												Usuario Captura:
											</td> -->
											<td align="left" colspan="6">
												<input type="hidden" id="U_NOMBRERev" name="U_NOMBRERev" value=""
												maxlength="60" readonly="readonly" size="60" class="EleAut" >
											</td>
										</tr>
										<tr>
											<td colspan="6" align="left">
												<div id="AjusteRev">
													<table>
														<tr>
															<td align="right">
																Ajuste:
															</td>
															<td align="left" colspan="5">
																<select id="nTipoAjusteRev" name="nTipoAjusteRev" disabled="disabled">
																	<option value="">
																	</option>
																	<option value="1">
																		Ajuste Previo
																	</option>
																	<option value="2">
																		Ajuste Presupuestario
																	</option>
																	<option value="3">
																		Ajuste de Resultados
																	</option>
																</select>
															</td>
														</tr>
													</table>
												</div>
											<td>
										</tr>
										<tr>
											<td colspan="3" align="left">
												Concepto :
											</td>
											<td colspan="3" align="left">
												<label id="lbObservaciones">
													Observaciones:
												</label>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="100%">
									<table width="100%">
										<tr>
											<td colspan="3">
												<textarea name="cConceptoRev" id="cConceptoRev" rows="5%"
													cols="50%" class="EleAut"></textarea>
											</td>
											<td colspan="3">
												<textarea id="cComentariosRev" name="cComentariosRev"
													rows="5" cols="50"></textarea>
												<!-- 
												<textarea name="cComentariosRev" id="cComentariosRev"
													rows="5%" cols="50%"></textarea>
												 -->
												<input type="hidden" name="nCuentaRev" id="nCuentaRev" />
												<input name="dCuentaRev" type="hidden" id="dCuentaRev" />
												<input type="hidden" name="nSubCuenta" id="nSubCuenta" />
												<input type="hidden" name="CargosRev" id="CargosRev" />
												<input type="hidden" name="AbonosRev" id="AbonosRev" />

											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align="right">
									<table>
										<tr>
											<td nowrap="nowrap" align="center">
												<label style="font-weight: bold;">
													Total Cargos:
												</label>
												<input type="text" name="TcargosRev" id="TcargosRev"
													maxlength="20" readonly="readonly" value="0" size="15"
													class="EleAut" style="font-weight: bold;"
													onkeydown="return disableKeys(event)" />

											</td>
											<td nowrap="nowrap" align="center">
												<label style="font-weight: bold;">
													Total Abonos:
												</label>
												<input type="text" name="TabonosRev" id="TabonosRev"
													maxlength="20" readonly="readonly" value="0" size="15"
													class="EleAut" style="font-weight: bold;"
													onkeydown="return disableKeys(event)" />
											</td>
											<td align="center">
												&nbsp;
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="50%" id="pCuentasRev" class="display">
							<thead>
								<tr align="center">
									<th>
										Renglon
									</th>
									<th>
										Cuenta
									</th>
									<th>
										Desc. Cuenta
									</th>
									<th>
										Auxiliar
									</th>
									<th>
										Parcialidades
									</th>
									<th>
										Cargos
									</th>
									<th>
										Abonos
									</th>

								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>

					<div id="tabs-5">
						<div id="divAutorizar">
							<label id="lbAutorizar" style="font-size: large">
								Autorizar
							</label>
							<input type="radio" id="grpAutorizar" name="grpAutorizar"
								onClick="autoriza(1)" checked="checked" value="Si">
							Si
							<input type="radio" id="grpAutorizar" name="grpAutorizar"
								onClick="autoriza(2)" value="No">
							No &nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" value="Guardar" id="GuardaAut"
								name="GuardaAut" onClick="cmdGuardar()" />
						</div>

						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table id="folioFechaTbl" width="100%">
										<tr>
											<td align="left">
												<label id="id_caso_lbl" style="font-weight: bold;">
													Folio Documento:
												</label>
											</td>
											<td>
												<label id="id_caso_lbl" style="font-weight: bold;">
													<%=request.getParameter("folio")%>
												</label>
												<input type="hidden" id="id_casoAut" name="id_casoAut" />
											</td>
											<td align="right">
												<label id="folioPoliza_lbl" >
													Folio Poliza:
												</label>
											</td>
											<td align="left">
												<input type="text" id="FolioPolizaAut" name="FolioPolizaAut" readonly="readonly" />
											</td>
											<td align="right">
												Fecha de Captura:
											</td>
											<td align="left">
												<label id="fCapturaAutLbl">
													&nbsp;
												</label>
												<input type="hidden" name="fCapturaAut" id="fCapturaAut"
													value="" />
											</td>
											<td align="right">
												Fecha Aplicaci&oacute;n:
											</td>
											<td align="left">
												<label id="fAplicacionAutLbl">
													&nbsp;
												</label>
												<input type="hidden" name="fAplicacionAut"
													id="fAplicacionAut" />
											</td>
										</tr>
										<tr>
											<td align="right">
												Ejercicio Fiscal:
											</td>
											<td align="left">
												<input type="text" id="EFAut" name="EFAut" class="EleAut" />
											</td>
											<td align="right">
												Tipo Poliza:
											</td>
											<td align="left">
												<select id="PolTipoAut" name="PolTipoAut" class="encabezado"
													disabled="disabled"></select>
											</td>
											<td align="left" colspan="2">
												<div id="periodo13AutDiv">
													&nbsp;
													<input type="hidden" name="periodo13Aut" id="periodo13Aut"
														value="N">
													<input type="hidden" id="CtroContable" name="CtroContable" />
													<input type="checkbox" name="periodo13AutChk" id="periodo13AutChk"  checked="checked" disabled="disabled"
														value="true">
													<label for="periodo13AutChk">
														Periodo 13
													</label>
												</div>
											</td>
										</tr>
										<tr>
											<td align="right">
												Centro Contable:
											</td>
											<td align="left" colspan="5">
												<select id="hPolCtroContableAut" name="hPolCtroContableAut"
													class="encabezado" onChange="cambioALM()"
													disabled="disabled">
												</select>
												<input type="hidden" id="CtroContableAut"
													name="CtroContableAut" />
											</td>
										</tr>
										<tr>
											<!--  <td align="right">
												Usuario Captura:
											</td>-->
											<td align="left" colspan="6">
												<input type="hidden" id="U_NOMBREAut" name="U_NOMBREAut" value=""
													maxlength="60" readonly="readonly" size="60" class="EleAut" >
											</td>
										</tr>
										<tr>
											<td colspan="6" align="left">
												<div id="AjusteAut">
													<table>
														<tr>
															<td align="right">
																Ajuste:
															</td>
															<td align="left" colspan="5">
																<select id="nTipoAjusteAut" name="nTipoAjusteAut" disabled="disabled">
																	<option value="">
																	</option>
																	<option value="1">
																		Ajuste Previo
																	</option>
																	<option value="2">
																		Ajuste Presupuestario
																	</option>
																	<option value="3">
																		Ajuste de Resultados
																	</option>
																</select>
															</td>
														</tr>
													</table>
												</div>
											<td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="100%">
									<table width="100%">
										<tr>
											<td colspan="3" align="left">
												Concepto :
											</td>
											<td colspan="3" align="left">
												<label id="lbObservaciones">
													Observaciones:
												</label>
											</td>
										</tr>
										<tr>
											<td colspan="3">
												<textarea name="cConceptoAut" id="cConceptoAut" rows="5%"
													cols="50%" class="EleAut"></textarea>
											</td>
											<td colspan="3">
												<textarea name="cComentariosAut" id="cComentariosAut"
													rows="6" cols="50"></textarea>
												<input type="hidden" name="nCuentaAut" id="nCuentaAut" />
												<input name="dCuentaAut" type="hidden" id="dCuentaAut" />
												<input type="hidden" name="nSubCuentaAut" id="nSubCuentaAut" />
												<input type="hidden" name="CargosAut" id="CargosAut" />
												<input type="hidden" name="AbonosAut" id="AbonosAut" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align="right">
									<table>
										<tr>
											<td nowrap="nowrap" align="center">
												<label style="font-weight: bold;">
													Total Cargos:
												</label>
												<input type="text" name="TcargosAut" id="TcargosAut"
													maxlength="20" readonly="readonly" value="0" size="15"
													class="EleAut" style="font-weight: bold;"
													onkeydown="return disableKeys(event)" />

											</td>
											<td nowrap="nowrap" align="center">
												<label style="font-weight: bold;">
													Total Abonos:
												</label>
												<input type="text" name="TabonosAut" id="TabonosAut"
													maxlength="20" readonly="readonly" value="0" size="15"
													class="EleAut" style="font-weight: bold;"
													onkeydown="return disableKeys(event)" />

											</td>
											<td align="center">
												&nbsp;
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="50%" id="pCuentasAut" class="display">
							<thead>
								<tr align="center">
									<th>
										Renglon
									</th>
									<th>
										Desc. Cuenta
									</th>
									<th>
										Parcial
									</th>
									<th>
										Auxiliar
									</th>
									<th>
										Parcialidades
									</th>
									<th>
										Cargos
									</th>
									<th>
										Abonos
									</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>

					<div id="tabs-2">
						<div align="center">
							<label id="esperar" style="visibility: hidden">

								Espere por favor....
								<img border="0" src="../imagenes/espera.gif" height="30">

							</label>
						</div>
						<table width="103%" border="0" cellspacing="0" cellpadding="0">
							<tr style="padding: 2px">
								<td colspan="3">
									Ejercicio Fiscal:&nbsp;&nbsp;&nbsp;&nbsp;
									<select id="EFC" name="EFC"></select>
									&nbsp;&nbsp;&nbsp; Centro Contable:
									<!-- <select id="hPolCtroContableC" name="hPolCtroContableC"></select> -->
									<input type="text" id="hPolCtroContableCLbl"
										name="hPolCtroContableCLbl" value="" size="50"
										readonly="readonly" maxlength="50"
										onkeydown="return disableKeys(event)" />
									<input type="hidden" id="hPolCtroContableC"
										name="hPolCtroContableC" value="" size="50"
										readonly="readonly" maxlength="50"
										onkeydown="return disableKeys(event)" />
								</td>
							</tr>
							<tr>
								<td colspan="3">
									Fecha Captura de:
									<input type="text" name="fCapturaC" id="fCapturaC"
										readonly="readonly" maxlength="10" size="10"
										onkeydown="return disableKeys(event)">
									al:
									<input type="text" name="fCaptura2C" id="fCaptura2C"
										readonly="readonly" maxlength="10" size="10"
										onkeydown="return disableKeys(event)">
									&nbsp; Fecha Aplicaci&oacute;n de:
									<input type="text" name="fAplicacionC" id="fAplicacionC"
										readonly="readonly" maxlength="10" size="10"
										onkeydown="return disableKeys(event)">
									al:
									<input type="text" name="fAplicacion2C" id="fAplicacion2C"
										readonly="readonly" maxlength="10" size="10"
										onkeydown="return disableKeys(event)">
								</td>
							</tr>
							<tr style="padding: 2px">
								<td colspan="3">
									No. de P&oacute;lizas&nbsp; de:
									<input type="text" name="polizaC" id="polizaC" maxlength="10"
										size="10">
									&nbsp; al:
									<input type="text" name="poliza2C" id="poliza2C" maxlength="10"
										size="10">
								</td>
							</tr>
							<tr>
								<td align="right">
									Tipo P&oacute;liza:
									<select id="PolTipoC" name="PolTipoC">
									</select>
								</td>
								<td>
									Origen :
									<select id="PolOrigen" name="PolOrigen"></select>
								</td>
								<td>
									Autom&aacute;tica:
									<select id="PolAutomatica" name="PolAutomatica">
										<option value="1">
											AUTOMÁTICA
										</option>
										<option value="0" selected="selected">
											CONTABILIDAD
										</option>
									</select>
								</td>
							</tr>
							<tr style="padding: 3px">
								<td colspan="2">
									Status:
									<select id="PolStatus" name="PolStatus">
									</select>
								</td>
								<td>
									<input id="Buscar" name="Buscar" value="Buscar"
										onClick="BuscarMovimiento();" type="button">
									<input id="LimpiaPantBusq" onclick="limpiaPantallaBusqueda()"
										type="button" value="Limpiar">
								</td>
							</tr>

						</table>
						<table id="pMovimiento" class="display" align="center">
							<thead>
								<tr align="center">
									<th>
										N&uacute;mero Poliza
									</th>
									<th>
										Concepto
									</th>
									<th>
										Fecha Captura
									</th>
									<th>
										Fecha Aplicacion
									</th>
									<th>
										Total
									</th>
									<th>
										Status
									</th>
									<th>
										TipoPoliza
									</th>
									<th>
										Origen
									</th>
									<th>
										Cent. Cont.
									</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>

					</div>
					<div id="tabs-3">
						<div align="center">
							<label id="esperardet" style="visibility: hidden">

								Espere por favor....
								<img border="0" src="../imagenes/espera.gif" height="30">

							</label>
						</div>
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									No. de P&oacute;lizas :
								</td>
								<td>
									<input type="text" name="polizaDet" id="polizaDet"
										readonly="readonly" maxlength="10" size="10" class="detalle">
								</td>
								<td align="right">
									Tipo P&oacute;liza:
								</td>
								<td>
									<input type="text" name="PolTipoDet" id="PolTipoDet"
										readonly="readonly" maxlength="10" size="10" class="detalle">
								</td>
							</tr>
							<tr>
								<td>
									Fecha Captura :
								</td>
								<td>
									<input type="text" name="fCapturaDet" id="fCapturaDet"
										readonly="readonly" maxlength="10" size="10" class="detalle">
								</td>
								<td align="right">
									Fecha Aplicaci&oacute;n :
								</td>
								<td>
									<input type="text" name="fAplicacionDet" id="fAplicacionDet"
										readonly="readonly" maxlength="10" size="10" class="detalle">
								</td>
							</tr>
							<tr>
								<td>
									Origen :
								</td>
								<td>
									<input type="text" name="PolOrigenDet" id="PolOrigenDet"
										readonly="readonly" maxlength="10" size="10" class="detalle">
								</td>
								<td>
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr>
								<td>
									Descripcion :
								</td>
								<td colspan="2">
									<input id="cConceptoDet" name="cConceptoDet" value="" size="50"
										readonly="readonly" maxlength="50" type="text" class="detalle" />
								</td>
								<td>
									Total Cargo :
									<input type="text" name="mTotalCargo" id="mTotalCargo"
										class="detalle">
								</td>
							</tr>
							<tr>
								<td align="right">
									Centro Contable :
								</td>
								<td colspan="2">
									<input type="text" name="hPolCtroContableDet"
										id="hPolCtroContableDet" readonly="readonly" maxlength="50"
										size="50" class="detalle">
									<input type="text" name="hPolCtroContableDet2"
										id="hPolCtroContableDet2" readonly="readonly" maxlength="5"
										size="5">
								</td>
								<td>
									Total Abono :
									<input type="text" name="mTotalAbono" id="mTotalAbono"
										class="detalle">
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
								<td colspan="2">
									&nbsp;
								</td>
								<td>
									<input type="button" name="Copiar" id="Copiar" value="Copiar"
										onClick="CopiaPoliza()">
								</td>
							</tr>
						</table>
						<br />
						<table id="ppMovimientoDetalle" class="display">
							<thead>
								<tr>
									<th>
										Cuenta
									</th>
									<th>
										Auxiliar
									</th>
									<th>
										Cargos
									</th>
									<th>
										Abonos
									</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				</div>

			</div>
			<div id="divGrabaDetalle" style="visibility: hidden">
				<table id="dtGuardaDetalle">
					<thead>
						<tr>
							<th>Cuenta</th>
							<th>Auxiliar</th>
							<th>Parciales</th>
							<th>Parcial</th>
							<th>mImporte</th>
							<th>cEvento</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
		</form>

<form id="redireccionar" name="redireccionar"  action="../gstnmngr/gestion?cmd=1" target="content-iframe"	method="post"></form>

<div id="dialog-procesar">
				<div id="esperar" align="center">Espere por favor....
					<div id="ProcMsg" align="center" style="font-size: 10pt">Aplicando cambios</div>
					<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
</div>

	</body>
</html>
