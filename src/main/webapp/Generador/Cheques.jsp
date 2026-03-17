
<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.core.AdecuacionManager"%>
<%@page import="java.sql.Connection"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cUR2 = "";
	String cRamo = "";
	String u_login = "";
	boolean algo2 = false;
	boolean bAplicadoCont = false;
	String ejercicioFiscal="";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
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

	//documentos del caso
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
	u_login = usuario.getLogin();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}
	Connection con = cbl.getConnection(GestionInterface.ATT_CONEXION);
	try{
		ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal( con );	
	}
	catch(Exception x){
		x.printStackTrace();
		if(con != null){
			con.close();
			con = null;
		}
	}
	finally{
		if(con != null){
			con.close();
			con = null;
		}
	}
	
%>
<%
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Emisión de Cheques</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		
		<script type="text/javascript" src="../js/datepickercontrol.js"></script>
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/validaciones.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" charset="utf-8">
var bClicBtn = false;

$(document).ready(function(){
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	$("#destino").hide();	
	querySelectPost("tCtasBancariasDVacioRead", "cIdCuentasBancariasD", {async: false });
	
	$( "#dialog-form" ).dialog({
		autoOpen: false,
		height: 400,
		width: 800,
		modal: true,
		beforeClose: function( event, ui ) {
			return bClicBtn;			
		}
	});
				

 	$('.currency').blur(function()	{
		$('.currency').formatCurrency();
	});

	$("#cRamo").val( "<%=cRamo%>" );
	$("#cUnidadResponsable").val( "<%=cUR%>" );
	
	$("#OIRAUSU").val( "<%=u_login%>" );
	$("#miOper").val( "<%=id_oper%>" );
	
	$("#cCentroContable").val( "<%=cCentroContable%>" );

	$("#id_caso").val(<%=request.getParameter("folio")%>);

	
	$("#caNOcontrarrecibo").change(function () {
		if (this.value != ""){
			$("#cEstatusPagado").val( "X" );
			
			if ( (this.value).substring(0,2) != $("#cCentroContable").val() ){
				alert("El Prefijo de la Cuenta por Pagar debe ser igual al Centro Contable: " + $("#cCentroContable").val());
				this.value = "";
				return;
			}
			
			queryFormPost("BuscaPagadoRead",{async: false });
			
			if ( $("#cEstatusPagado").val() != "S" ){
				alert("La Cuenta por Pagar No Existe o No se Encuentra con Estatus PAGADO");
				this.value = "";
				return;
			}
		}
	});

	$("#chktrans").click(function () {
		if ( $("#chktrans").is(':checked') ){
			$("#destino").show();	
			$("#rfc").hide();
			$("#cIDRFC").val( "CNA890116SF2" );
			querySelectPost("tCuentasBancariasDURRead", "cIdCuentasBancariasD", {async: false });
		}else{
			$("#destino").hide();	
			$("#rfc").show();	
			$("#cIDRFC").val( "" );
			$("#cnombre").val( "" );
			$("#cIdCuentasBancariasD").val( "" );
			querySelectPost("tCtasBancariasDVacioRead", "cIdCuentasBancariasD", {async: false });
		}
	});

	$("#cIdCuentasBancarias").change(function () {
		if ( $("#chktrans").is(':checked') ){
			querySelectPost("tCuentasBancariasDURRead", "cIdCuentasBancariasD", {async: false });
		}
	});

});  //fin del ready


	$(function() {
		$( "#fElaboracion" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});

	$(function() {
		$( "#fEntrega" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});


 	function onLoadPlantilla(id_oper){
		querySelectPost("tCuentasBancariasURRead", "cIdCuentasBancarias", {async: false });
		querySelectPost("tEjercicioRead", "cEjercicio", {async: false });
		queryFormPost("TipoPolizaRead",{async: false });
		queryFormPost("tChequeEncabezadoRead",{async: false });
		$("#cIdCuentasBancarias").val( $("#cCuentaBancaria").val() );
		$("#cIdCuentasBancariasD").val( $("#cTransferencia").val() );
		$("#divImprimePoliza").hide();	

		if ( $("#cTransferencia").val() == "" ){
			querySelectPost("tCtasBancariasDVacioRead", "cIdCuentasBancariasD", {async: false });
			$("#destino").hide();	
			$("#rfc").show();	
		}else{
			//alert( $("#cIdCuentasBancariasD").val() );
			$("#chktrans").attr('checked', true);
			$("#destino").show();	
			$("#rfc").hide();	
			querySelectPost("tCuentasBancariasDURRead", "cIdCuentasBancariasD", {async: false });
		}
		
		if (id_oper == 2){
			$("input").attr("readonly", true); 
			$("input").css("background", "#f0f0f0");
			$("#cIdCuentasBancarias").attr("disabled","disabled");
			$("#cIdCuentasBancariasD").attr("disabled","disabled");
			$("#chktrans").attr("disabled","disabled");
			$("#cConcepto").attr("disabled","disabled");
		}
		if (id_oper == 3){
			$("#divImprimePoliza").show();	
			$("input").attr("readonly", true); 
			$("input").css("background", "#f0f0f0");
			$("#cIdCuentasBancarias").attr("disabled","disabled");
			$("#cIdCuentasBancariasD").attr("disabled","disabled");
			$("#chktrans").attr("disabled","disabled");
			$("#cConcepto").attr("disabled","disabled");
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
		}

		$("#mImporte").formatCurrency();		
		parent.document.getElementById("pb_send").style.visibility='hidden';
		parent.document.getElementById("pb_send").disabled = false;					
	}
  	
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1)
  		 	return "AUTORIZA_CHEQUE";
  		 if(id_oper==2)
  		 	return "CONSULTA_" + $("#cDocumento").val();

  	}

  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "autoriza_cheque";
  		if(id_oper==2){
  		 	return "consulta_cheque";}
  	}
	
  	
  	
	function onPostDisplay(id_oper){
		if (id_oper == 2 ) {
			parent.document.getElementById("pb_send").click();
		}

	}

	function cmdImprimir( elFormato ){
		var cCuenta = $("#cIdCuentasBancarias").val();
		cCuenta = cCuenta.substring(0, 3);
		
		elFormato = "PolizaCheque";
		if ( cCuenta == "072"){
			elFormato = "PolizaChequeBANORTE";	
		}
		if ( cCuenta == "012"){
			elFormato = "PolizaChequeBANCOMER";	
		}
		
		if ( cCuenta == "044"){
			elFormato = "PolizaChequeSCOTIABANK";	
		}
		//if ( $("#chktrans").is(':checked') ){
		//	elFormato = elFormato + "_Transf";
		//}
			window.open(
						"../admin/SeguridadCatalogos?"
							+ "catalogo=CONTRARECIBO"
							+ "&accion=run"
							+ "&rn=" + elFormato + ".jasper"
							+ "&whereFolio=" + $("#nFolioCheque").val(),
							//+ "&nombre="   + ""
							//+ "&cargo="    + ""
							//+ "&area="     + "",
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
							
	}
	
  	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		
  		if ($("#cDocumentoHaplicado").val() == "S") {
  			alert("Documento ya fue aplicado y se avanzará a modo de CONSULTA");
  		}else {
			try{
			//validaciones de la forma
			//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay

			//Guardado de los campos correspondientes a cada variable de caso
			p.gestion.setFolio( $("#FOLIO").val() );
			p.gestion.setOperador( $("#OPERADOR").val() );
			p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
			p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
			p.gestion.setConceptoMov("Aplicación de Cheques");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
			p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			p.gestion.setAplicadoCont("false");
			
			if(<%=c.getIdGabinete()%>!=-1){
				//alert("antes de fecha de aplicacion"+get("DPC_FECHA_APLICACION_CONTABLE"));
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			}


			if(id_oper==1){
				var bretval = cmdGuardar();
				if ( !bretval ) {
					//alert('error al guardar información');
					return false;
				}
				alert("Información Guardada Correctamente");
 		 		parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").style.visibility='visible';
			}


			if(id_oper==2){
 		 		parent.document.getElementById("pb_save").disabled=true;
				//parent.document.getElementById("pb_send").style.visibility='visible';
				parent.document.getElementById("pb_send").disabled = false;					

				//$( "#dialog-form" ).dialog( "open" );
				//fnAplicaMotor();
				//parent.document.getElementById("pb_send").click();
			}

			
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
  		}
			return valida_campos;
	}
  	
  	
  	function fnAplicaMotor(){
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNOcontrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>"
 	}

  	
	function cmdGuardar()
		{
			getNextSequenceVal({seqName: $("#cIdCuentasBancarias").val(), async: false, callback: setSequenceVal});

			if ( $("#nNumCheque").val() == "" ){
				alert("Se debe Capturar el Folio del Cheque");
				return false;
			}
			
			if ( $("#cIDRFC").val() == "" && $("#chktrans").is(':checked') ){
				alert("Se debe Capturar el RFC del Cheque");
				return false;
			}
			
			if ( $("#mImporte").val() == "$0.00" || $("#mImporte").val() == "" ){
				alert("Se debe Capturar el Importe del Cheque");
				return false;
			}
			
			if ( $("#cConcepto").val() == "" ){
				alert("Se debe Capturar el Concepto del Cheque");
				return false;
			}
			
			if ( $("#U_LOGIN_revisa").val() == "" ){
				alert("Se debe Capturar el Revisor del Cheque");
				return false;
			}

			if ( $("#U_LOGIN_autoriza").val() == "" ){
				alert("Se debe Capturar el Autorizador del Cheque");
				return false;
			}
			
			var vcons = "000000" + "<%=request.getParameter("folio")%>";
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;

			$("#fAplicacion").val( $("#FECHA_CARGA").val() ); 
			$("#cCentroContable").val( "<%=cCentroContable%>"); 
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val( "<%=cUR%>" );
			if ( $("#cIdTipoPersonaRFC").val() == '3' || $("#cIdTipoPersonaRFC").val() == '4'){
				$("#cEvento").val( "ChqDeudores" );	
			}else{
				$("#cEvento").val( "ChqAcreedores" );	
			}
			
			if ( $("#chktrans").is(':checked') ){
				$("#cEvento").val( "ChqTransferO" );
			}		
			
			$("#cDescripcionPoliza").val( $("#cConcepto").val() );
			$("#nFolioCheque").val( $("#id_caso").val() ) ;
			
			queryFormPost("tChequeEncabezadoCreate,tChequeDetalleCreate", {async: false });

			if ( $("#chktrans").is(':checked') ){
				$("#cIdCuentasBancarias").val( $("#cIdCuentasBancariasD").val() );
				$("#cEvento").val( "ChqTransferD" );
				$("#nDocRenglon").val( "2" );
				queryFormPost("tChequeDetalleCreate", {async: false });
			}		
			

			vrowAf = $("#rowsAffected").val();
			//alert("Compromiso guardado!");
			if (vrowAf == "0") {
				//return false;
			}
			return true;
        }


	function setSequenceVal(seqValue) {
		//seqValue = "000000" + seqValue;
		//seqValue = seqValue.substr(seqValue.length - 6);
		//seqValue = "<%=cCentroContable%>" + "CH" + $("#cEjercicio").val() + seqValue;
		$("#nNumCheque").val( seqValue );
	}

  	
  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		//var valida_doctos_requeridos = true;
  		//validaciones de documentos requeridos
  		//return valida_doctos_requeridos;
  		//return confirm("Confirmar que quiere avanzar a la siguiente operación.");
  		return true;
  	}

	function valida_concepto(e) {
		var nChars = $("#cConcepto").val();
		nChars = nChars.length;
		tecla = (document.all) ? e.keyCode : e.which;
		
		if (tecla==8) {
			$("#nChars").val( --nChars );
			if (nChars < 0)
				$("#nChars").val( 0 );
			return true;
		}
		
		if (nChars >= 400) {
			return false;
		}
		$("#nChars").val( ++nChars );
		patron =/[A-Za-z.\d\s\\. `,$-_%&]/;
		te = String.fromCharCode(tecla);
		return true; // patron.test(te);
	}

	function valida_importe(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
		patron =/[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	
	function valida_cheque(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
		patron =/[\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}

	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}	
	
	function Sinfrmt( fld )
	{
	   	var valcol = fld.value ;
	   	valcol = quitaFmt( valcol );
		$("#" + fld.id).val( valcol );
	   	fld.select();
	}


	
	function cambiafrmt( fld )
	{
	   	var vfld = $("#" + fld.id).val()
	   	if (vfld == "")
	   		vfld = '0';
		$("#" + fld.id).formatCurrency();
	}

</script>

	</head>

	<body id="dt_example">
		<form id="formPagos">
			<div id="container" class="container SyCData">
				<h1>Emisión de Cheques</h1>
				<input type="hidden" value="CHEQUE" id="TO_TIPO_DOCTO" name="TO_TIPO_DOCTO">
				<input type="hidden" value="" id="rowsAffected" name="rowsAffected">
				<input type="hidden" id="cRamo" name="cRamo" value="16">
				<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
				<input type="hidden" id="cCentroContable" name="cCentroContable">
				<input type="hidden" id="cEvento" name="cEvento" value="">
				<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value="A02">
				<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
				<input type="hidden" name="OPERADOR" id="OPERADOR"	value="<%=c.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" />
				<input type="hidden" value="<%=ejercicioFiscal%>" id="cEjercicio" name="cEjercicio">
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable">								
				<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza">
				<input type="hidden" id="fAplicacion" name="fAplicacion">
				<!-- input type="hidden" id="caNoContrarrecibo" name="caNoContrarrecibo"> -->
				<input type="hidden" id="U_LOGIN_imprime" name="U_LOGIN_imprime">
				<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado">
				<input type="hidden" id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC" value="">
				<input type="hidden" id="nFolioCheque" name="nFolioCheque" value="<%=c.getFolio()%>">
				<input type="hidden" id="cEstatusPagado" name="cEstatusPagado" value="">
				<input type="hidden" id="cCuentaBancaria" name="cCuentaBancaria" value="">
				<input type="hidden" id="cTransferencia" name="cTransferencia" value="">
				<input type="hidden" id="nDocRenglon" name="nDocRenglon" value="1">

				<input readonly type="hidden" id="id_caso" name="id_caso" size="8" >
				<div class="dvGeneral"> 
					<table height="66" width="800" border="0">
						<tr>
							<td colspan=3>Transferencia entre Cuentas:
								<input type="checkbox" id="chktrans" name="chktrans" value="0">	
							</td>
						</tr>
						<tr>
							<td colspan=3>Cuenta Bancaria:
								<select id="cIdCuentasBancarias" name="cIdCuentasBancarias">
									<option>BBVA BANCOMER 0123456789012345678</option>
								</select>	
							</td>
						</tr>
						<tr id="destino">
							<td colspan=3 >Transferir a:
								<select id="cIdCuentasBancariasD" name="cIdCuentasBancariasD">
									<option></option>
								</select>	
							</td>
						</tr>
						<tr align="left">
							<td>No. Cheque:
								<input type="text" readonly name="nNumCheque" id="nNumCheque" size="15" maxlength="15" onkeydown ="return valida_cheque(event)"/>
							</td>
							<td nowrap>Cuenta por Pagar
								<input id="caNOcontrarrecibo" name="caNOcontrarrecibo" type="text" value="" style="text-transform:uppercase" size="20" maxlength="40">
							</td>
							<td>
								<div id="divImprimePoliza">
									<img src="imagenes/Imprimir.png" width="25" height="21"	onClick="cmdImprimir('PolizaCheque');"> Póliza
								</div>
							</td>
						</tr>
						<tr id="rfc" align="left">
							<td>R.F.C.:
								<input readonly type="text" maxlength="15" size="15" name="cIDRFC" id="cIDRFC" class="AyudaSyC  obligatorio" />
							</td>
							<td colspan="2">
								<input disabled readonly type="text" maxlength="90"	size="90" name="cnombre" id ="cnombre" />
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe:
								<input name="mImporte" type="text" id="mImporte" value="0" maxlength="16" size="16" onkeypress="return valida_importe(event)" onfocus="Sinfrmt(this)"  onblur="cambiafrmt(this)" style="text-align:right;">
							</td>
							<td align="center">
								Fecha Emisión:
								<input name="fElaboracion" readonly type="text" id="fElaboracion" value="<%=today%>" size="10" maxlength="10" style="text-align:center;">
							</td>
							<td  align="center">
								Fecha Entrega:
								<input name="fEntrega" readonly type="text" id="fEntrega" value="" size="10" maxlength="10" style="text-align:center;">
							</td>
							<td>&nbsp;</td>
						</tr>
						<tr align="left">
							<td colspan=3>Concepto:
								<input name="nChars" readonly type="hidden" id="nChars" value="" size="5" maxlength="5" style="text-align:center;">
							</td>		
						</tr>
						<tr align="left">
							<td colspan=3>
								<textarea name="cConcepto" rows="512" id="cConcepto" style="height: 150px; width: 800px" onkeydown="return valida_concepto(event)"></textarea>
							</td>
						</tr>
						<tr align="left">
							<td>Elaboró
								<input name="U_LOGIN_captura" readonly type="text" id="U_LOGIN_captura" value="<%=u_login%>" maxlength="10" style="text-align:center;">
							</td>
							<td>Revisó
								<input name="U_LOGIN_revisa" readonly type="text" id="U_LOGIN_revisa" class="AyudaSyC  obligatorio" maxlength="10" style="text-align:center;">
							</td>
							<td>Autorizó
								<input name="U_LOGIN_autoriza" readonly type="text" id="U_LOGIN_autoriza" class="AyudaSyC  obligatorio" maxlength="10" style="text-align:center;">
							</td>							
						</tr>
						<tr align="left">
							<td>
								<input name="cNombre_captura" type="text" id="cNombre_captura" value="<%=c.getCasoOperacion(0).getResponsable()%>"  readonly size="35" style="text-align:left;">
							</td>
							<td>
								<input name="cNombre_revisa" type="text" id="cNombre_revisa" readonly size="40" style="text-align:left;">
							</td>
							<td>
								<input name="cNombre_autoriza" type="text" id="cNombre_autoriza" readonly size="40" style="text-align:left;">
							</td>							
						</tr>
					</table>

				</div>
				
				<div id="dialog-form" title="Aplicación Presupuestal/Contable">	
					<div id="divEspera" align="center">Espere por favor....
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
					<div id="divAplica" >				
						<iframe id="ifAplica" src="about:blank"></iframe>
					</div>
				</div>
											
			</div>
		</form>

	</body>
</html>
