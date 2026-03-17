<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Calendar"%>


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
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());
	String cCentroContable = "";
	String UR = usuario.getU_UR();
	String ramo = usuario.getU_Ramo(); 

	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	cEjercicio = adecProy.obtenEjercicioFiscal();

	final int nFolioPurianual = new Integer(c.getFolio().substring(
			c.getFolio().lastIndexOf('-') + 1)).intValue();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>


<style>
.notEditable {
	background-color: #CCCCCC;
	text-align: right;
}

.monto {
	text-align: right;
	border: 1px solid #aaaaaa;
	color: #222222;
}
</style>

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
	text-align: left;
	border: 1px solid #aaaaaa;
	background-color: #CCCCCC;
}

.monto {
	text-align: right;
	border: 1px solid #aaaaaa;
	color: #222222;
}

.etiqueta {
	text-align: rigth;
	border: 1px solid #aaaaaa;
	background-color: #CCCCCC;
}
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">



	
	
</script>
<script type="text/javascript">
	$(document).ready(function() {
	     $("#tabs").tabs();
	    // $("#btnRechazo").hide();
	     //$("#btnAutoriza").hide();
	     //$("#btnRechazofinal").hide();
	     //$("#btnCancelacion").hide();
	     $("#divBotones").hide();
	     $("#h_PrendeBandera").val(0);
	      // document.formulario.btnRechazo.disabled = false;
	     $("#txtfPublicacion").datepicker
		({
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
	
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$('#dt_catalogo').dataTable({
			"bScrollCollapse" : true,
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : true,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sScrollY" : "400"
		});
		
		
	});
	
	
	
	
	function onLoadPlantilla(id_oper){
	 
	  $("#txtTramite").val("Captura");
	 
	  $("#chkRechaza").prop('disabled', 'disabled');
	  if (id_oper != 3)	  {
	     parent.document.getElementById("pb_cancel").disabled=true;
		 parent.document.getElementById("pb_leave").disabled=false;
		 parent.document.getElementById("pb_send").disabled=true;
	  }
	  
	  
      $("#h_id_oper").val(id_oper);
	  
	// document.getElementById('chkRechaza').style.visibility = 'hidden';
	  fn_ObtenEncabezado();
	 
	  $("#txtPrestador").val($("#txtRfc").val() + " : "+ $("#txtNombre").val());
	 
      
	  fn_ObtenCatalogo();
	  fn_FormatoMoneda();
	  $("#txtRechazo").prop('disabled', 'disabled');
	  //fn_LimpiaRegistro();
	  if (id_oper == 2){
	      $("#txtTramite").val("Autorizador");
		 //Estamos en autorizacion
		 $("#txtRechazo").prop('disabled', false);
		 $("#chkRechaza").prop('disabled', false);
		 //$("#btnRechazofinal").show();
		 //$("#btnRechazo").show();
		 //$("#btnAutoriza").show();
		// $("#btnCancelacion").show();
		 $("#divBotones").show();
		
 	 }
 	 if  (id_oper == 3){
 	  $("#txtTramite").val("Consulta");
 	     //Solo consulta, ocultar botones
 	     $("#divBotones").hide();
 	     fn_Desactivar();
 	     
 	 }
	 
	 return true;
	} 

	function fn_ObtenEncabezado()
	{
	    
	  
	    $("#txtfolioSai").val($("#hFolioSai").val()); 
	    queryFormPost("ConsultaComsocFolio" ,  {async : false });
	    $("#hFolioPago").val($("#txtFolio").val());
		queryFormPost("ConsultaComsocEncabezado", {async : false });
	    $("#h_TipoPago").val($("#txtTipoPago").val());
	     $("#h_txtRechazo").val($("#txtRechazo").val());
	     var strRechazo =$("#h_txtRechazo").val();
	    
	    if ($("#h_bAutoriza").val()==0){ 
	       document.getElementById("chkRechaza").checked=true; 
	       if (strRechazo.length > 0 &&  $("#h_id_oper").val() == 1 ){
           alert("Este tramite fue rechazado parcialmente, verifique el motivo del rechazo");
	     	} 
	     }
	     if ($("#h_bAutoriza").val()==-1 &&  $("#h_id_oper").val() == 1){ 
	       document.getElementById("chkRechaza").checked=true; 
	       if (strRechazo.length > 0 ){
           alert("Esta autorización fue rechazada Definitivamente ,verifique el motivo del rechazo, posteriormente solo precione Guardar y Envie para concluir el tramite");
	     } 
        }
        
        
       	var sOrder = ""; 
	    var param =""; 
        
       if ($("#txtTipoPago").val() == "PAGODIVERSO"){  
         // para relacion de gastos      
		var zTabla = "CONSULTA_EP_COMSOC_PAGO";
		var camposWhere = " WHERE nFolioPAGODIVERSO = " +$("#hFolioPago").val()  + "  and SUBSTRING(ep,32,5) in  (select cPartida from TpartidaComsoc) ";  
	
	   }	
		
		if ($("#txtTipoPago").val() == "RELACIONGASTOS"){  
		    // TRAER DATOS DE RELACION DE GASTOS
			zTabla = "CONSULTA_EP_COMSOC_RELACION_GASTOS";
		    camposWhere = " WHERE nFolioRELACIONGASTOS = " +$("#hFolioPago").val() + "  and SUBSTRING(ep,32,5) in  (select cPartida from TpartidaComsoc) ";  
		}
		if ($("#txtTipoPago").val() == "PAGODIRECTO"){
		    // TRAER DATOS DE RELACION DE GASTOS
			zTabla = "CONSULTA_EP_COMSOC_PAGODIRECTO";
		    camposWhere = " WHERE nFolioPagoDirecto = " +$("#hFolioPago").val() + "  and SUBSTRING(ep,32,5) in  (select cPartida from TpartidaComsoc) ";  
	
		
		
		}
		
		
		
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j){
		for (var i = 0; i < j.length; i++){
			$("#dt_catalogo").dataTable().fnAddData([j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4]);
			$("#hMes").val(j[i].Col1);
			$("#txtPartida").val(j[i].Col2); 
			//$("#h_importe_pago").val(j[i].Col3);
			
			}
			
		});
	  
		 
	}
	
	function fn_ObtenCatalogo()
	{
	  querySelectPost("ConsultatCatalogoMediosDifusion", "selectMedios",{async : false});
	 // queryFormPost("catalogoUnidadResponsableRead" ,  {async : false });
	  //$("#cUnidad").val($("#id").val());
	  $("#selectMedios").val($("#h_cveMedio").val());
	  fn_Select($("#h_txtTipoOrden").val());
	  //Obten tipo de orden el tipo de Orden 
	  if ($("#h_txtTipoOrden").val() != "")
	     document.getElementById($("#h_txtTipoOrden").val()).checked = true;
	 }
	
	
	function fn_LimpiaRegistro()
	{
		//$("#txtInsercion").val("INSER/  /");
	}
	
	function  fn_Desactivar(){
		$("#txtClave").attr("disabled",true);
		$("#txtClaveAutorizacion").attr("disabled",true);
		$("#txtClaveCampania").attr("disabled",true);   
		$("#txtCveContrato").attr("disabled",true);   
		$("#txtfPublicacion").attr("disabled",true);   
		$("#txtTarifaU").attr("disabled",true);   
		$("#txtCantidad").attr("disabled",true); 
		$("#selectMedios").attr("disabled",true); 
	    $("#txtIVA").attr("disabled",true);   
		$("#txtEspacio").attr("disabled",true);   
		$("#txtDescripcionMensaje").attr("disabled",true);   
		$("#txtDestinatarios").attr("disabled",true);   
	    $("#txtConcepto").attr("disabled",true);   
		$("#txtServidor").attr("disabled",true);   
		$("#txtDescripcionMensaje").attr("disabled",true);   
	}	
	
	
	
	function fn_Guardar(){
		var bCambios =false;
	      
		 $("#h_cveMedio").val($("#selectMedios").val());
	     $("#h_txtTipoOrden").val($('[name=RadioOrden]:checked').val());
	
	     if($("#h_id_oper").val() == 1)       {
	        if ($("#h_bAutoriza").val()==0) {
	     		$("#h_txtRechazo").val("");
	            $("#txtRechazo").val("");
	       }        
	     }   
	     if (fn_valida() ==true){  
		  	 var r=confirm("Guardar ?");
		     if (r==true){ 
		 	     queryFormPost({ queryName:"update_tComsocAutorizacion",async:false, callback:function(){bCambios = true;} });
			 }
			 if (bCambios==true)  {
			  alert("Cambio exitoso");
			  return bCambios;
			 }
		 }	  	 
	 }
	
	
	function fn_Autorizar()	{
	  //Se manda la bandera de la autorizacion en 1
	  $("#h_bAutoriza").val(1); //bandera que indica que el pago ya puede ser pagado y se concluye el comsoc
	  $("#txtRechazo").val("");
	  $("#h_txtRechazo").val($("#txtRechazo").val());
	  //   fn_Guardar(); 
	  //  alert("Se activo la bandera de Para que continue el pago, Seleccione Enviar");
	  $("#h_PrendeBandera").val(1); 
	  $("#btnAutoriza").prop('disabled', true);
	  $("#btnRechazo").prop('disabled', false);
	  $("#btnRechazofinal").prop('disabled', false);
	 
	}
	
	function fn_Rechazar() {
	   //Se Rechaza la atorizacion
	   if($("#chkRechaza").is(':checked') ){
   		 $("#h_bAutoriza").val(0);
	   	}
	   else {
	     alert('Seleccione la casilla de rechazo en la pestaña correspondiente y describa el motivo del rechazo'); 
	   return;   
	   }
	  $("#h_bAutoriza").val(0);
	  var strRechazo = $("#txtRechazo").val(); 
	  if (strRechazo.length == 0 || $.trim($("#txtRechazo").val())==""){  
	       alert('Describa el motivo de Rechazo'); 
	       return;
	  }
	  $("#h_txtRechazo").val($("#txtRechazo").val());
	  $("#h_PrendeBandera").val(1); 
	  $("#btnRechazo").prop('disabled', true);
	  $("#btnRechazofinal").prop('disabled', false);
	  $("#btnAutoriza").prop('disabled', false);
	
	}
 function fn_RechazoDefinitivo(){
	 if($("#chkRechaza").is(':checked') ){
   		 $("#h_bAutoriza").val(-1);
   		
	 }
	 else {
	     alert('Seleccione la casilla de rechazo en la pestaña correspondiente y describa el motivo del rechazo'); 
	   return;   
	 }
	 var strRechazo = $("#txtRechazo").val(); 
	 if (strRechazo.length == 0 || $.trim($("#txtRechazo").val())==""){  
	       alert('Describa el motivo de Rechazo Definitivo'); 
	       return;
	 }
	 $("#h_bAutoriza").val(-1);
	 $("#h_PrendeBandera").val(1);
	 //fn_Guardar(); 
	 $("#h_txtRechazo").val($("#txtRechazo").val());
	 parent.document.getElementById("pb_send").disabled = false;
	 $("#btnRechazofinal").prop('disabled', true);
	 $("#btnRechazo").prop('disabled', false);
	 $("#btnAutoriza").prop('disabled', false);
	
	 
}
	
	
	
	
 function fn_valida(){
	   var   TipoOrden = $('[name=RadioOrden]:checked').val();
	   var   ImporteNetoPago = fn_quitaFormato($("#txtImporteNeto").val());
	   var   ImportePagoComsoc =   fn_quitaFormato($("#txtTarifaTotal").val());
	   
	     ImporteNetoPago  = parseFloat((ImporteNetoPago),10);
		 ImportePagoComsoc = parseFloat((ImportePagoComsoc),10);	
	
	 	if (TipoOrden == undefined) {
          alert("Seleccione el tipo de orden");
          return false;
        }
		else if($.trim($("#txtClave").val())==""){
		
		     if (TipoOrden == 'Tras') {
		        alert("Indique la clave  de Tramsmisión"); 
		      }
		     else if  (TipoOrden == 'Ser') {
		        alert("Indique la clave  del Servicio");
		      }
		     else if  (TipoOrden == 'Ins') {
		        alert("Indique la clave  de Inserción");
		      }
		  $("#txtClave").focus();    
		  return false;
		}
		else if($.trim($("#txtClaveAutorizacion").val())==""){
			alert("Indique La Clave de Autorización");
			$("#txtClaveAutorizacion").focus();
			return false;
		}
	   else if($.trim($("#txtClaveCampania").val())==""){
			alert("Indique La Clave de Campaña");
			$("#txtClaveCampania").focus();
			return false;
		}
		else if($.trim($("#txtCveContrato").val())==""){
			alert("Indique la Clave de Contarto");
			$("#txtCveContrato").focus();
			return false;
		}
	   else if($.trim($("#txtfPublicacion").val())==""){
			alert("Indique la fecha del Contrato");
			$("#txtfPublicacion").focus();
			
			return false;
		}
		else if($.trim($("#txtTarifaU").val())==""){
			alert("Indique el Precio Unitario");
			$("#txtTarifaU").focus();
			return false;
		}
   		else if($.trim($("#txtCantidad").val())==""){
			alert("Indique la Cantidad");
			$("#txtCantidad").focus();
			return false;
		}
		
		else if($.trim($("#txtCantidad").val())==0){
			alert("La cantida no puede ser cero");
			$("#txtCantidad").focus();
			return false;
		}
		  
	   else if($.trim($("#txtIVA").val())==""){
			alert("Indique el IVA");
			$("#txtIVA").focus();
			return false;
		}
	   else if (ImportePagoComsoc >ImporteNetoPago){
		 	alert("LA Tarifa Total no debe ser mayor que el Importe Neto a Pagar");
			return false; 
		}
		
		 else if (ImportePagoComsoc == 0){
		 	alert("LA Tarifa Total no puede ser cero");
			return false; 
		}
		else if($.trim($("#selectMedios").val())==0){
			alert("Seleccione El Medio de Difusión"); 
			return false;
		}
		  else if($.trim($("#txtEspacio").val())==""){
			alert("Indique la Unidad de Medida");
			$("#txtEspacio").focus();
			return false;
		}
		else if($.trim($("#txtDescripcionMensaje").val())==""){
			alert("El campo de Mensaje no puede ir vacio");
			$("#txtDescripcionMensaje").focus();
			return false;
			
		}
	   else if($.trim($("#txtDestinatarios").val())==""){
			alert("Indique Los Destinatarios");
			$("#txtDestinatarios").focus();
			return false;
		}
	 	else if($.trim($("#txtConcepto").val())==""){
			alert("Indique el Concepto de la Publicación");
			$("#txtConcepto").focus();
			return false;
		} 
	  	   else if($.trim($("#txtServidor").val())==""){
			alert("Indique el Responsable de la Autorización"); 
			$("#txtServidor").focus();
			return false;
		}
		else if( $.trim($("#h_PrendeBandera").val()) ==0 && $("#h_id_oper").val() ==2){
			alert("Favor indicar si se rechaza o autoriza"); 
			return false;
		}
	return true;
	}
	
	
	function soloNumeros(evt)
    {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true; 
    }
    
    function LetrasNums(evt) 
    {
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
	
	
	
	function onSubmit(id_oper)
	{
	   if (fn_Guardar() != 1)
	    return;
	  
		//Realizar validaciones. Si regresa TRUE continua el proceso en caso contrario no guarda.
		var p = window.parent;
		p.gestion.setFolio('<%=c.getFolio()%>');
		p.gestion.setOperador('<%=usuario.getNombre()%>');
		p.gestion.setFechaDocumento('<%=today%>'); 
		p.gestion.setEjercicioFiscal('<%=cEjercicio%>');
		p.gestion.setMoneda("MXP");
		return true;
	}
	
	
	function ResponsableSiguiente(idOper){ 
//		Deben estar definidos en CG_GRUPO
       // alert("responsable siguiente");
 		if(idOper==1 ){
 			//return 'AUTORIZADOR_AUTORIZACOMSOC';
 			if($("#h_bAutoriza").val()==-1){
 		    //retorna a captura
 		    return 'CONSULTA_AUTORIZACOMSOC';
 		    } 
 		    else
 			return 'AUTORIZADOR_AUTORIZACOMSOC';
 		}			
 		if(idOper==2 ){
 		
 		    if($("#h_bAutoriza").val()==-1 || $("#h_bAutoriza").val()==0){
 		    //retorna a captura
 		    return 'CAPTURISTA_AUTORIZACOMSOC';
 		    } 
 		    else
 			return 'CONSULTA_AUTORIZACOMSOC';
 		}
 		   
 	}
	
 	function OperacionSiguiente(idOper){
//		Debe estar definido en cg_operacion
 		if(idOper==1 ){
 			if($("#h_bAutoriza").val()==-1)
 			return 'consulta_COMSOC';
 			else
 			return 'autoriza_COMSOC';
 			
 		}			
 		if(idOper==2 ){
 		    if($("#h_bAutoriza").val()==-1 || $("#h_bAutoriza").val()==0){
 		    //retorna acaptura
 		    return 'captura_COMSOC';
 		    } 
 		    else
 		    return 'consulta_COMSOC';
 		 	}
		}
 		
   
   
    
	function onPostDisplay(idOper) 
	{
		//alert("onPostDisplay");
		//if (idOper == 1) 
		parent.document.getElementById("pb_send").disabled = false;
		
		
		return true;

	}

	function onPostSubmit(idOper)
	 {
	    parent.document.getElementById("pb_send").disabled=true;
		parent.document.getElementById("pb_save").disabled=true;
		parent.document.getElementById("pb_cancel").disabled=true;
		parent.document.getElementById("pb_leave").disabled=true;
	 	$.blockUI( {
						message : "Procesando Envio espere ......"
					});
					
		return true;
		 
	}
	
function fn_CalculaTarifa(){
	
		var TarifaTotal = $("#txtTarifaU").val();
			TarifaTotal = parseFloat(fn_quitaFormato(TarifaTotal),10);
		if(TarifaTotal=="" ){
		     alert("indique el precio unitario");
		     $("#txtTarifaU").focus();
		     return false;
		}
		
		if(TarifaTotal==0 ){
		     alert("El precio unitario, no debe ser cero");
		     $("#txtTarifaU").focus();
		     return false;
		}
		if(TarifaTotal <=0 ){
		     alert("El precio Unitario debe ser mayor a cero");
		     $("#txtTarifaU").focus();
			 return false;
		}	
		if($.trim($("#txtCantidad").val())=="" || $.trim($("#txtCantidad").val()) == 0){
		     $("#txtCantidad").focus();
		     alert("La cantida no debe ser cero");
			return false;
		}
		if($.trim($("#txtIVA").val())=="" || $.trim($("#txtIVA").val())<0){
		        alert("Indique correctamente el iva");
		        $("#txtIVA").focus();
				return false;
		}
			Iva = $("#txtIVA").val()/ 100;
			TarifaTotal = TarifaTotal * parseFloat($("#txtCantidad").val());
			TarifaTotal = TarifaTotal + (TarifaTotal * Iva);
		    $("#txtTarifaTotal").val(TarifaTotal);
		    fn_FormatoMoneda();
 }
	
	function fn_FormatoMoneda(){
		$("#txtTarifaU").formatCurrency();
		$("#txtTarifaTotal").formatCurrency();
	}
	
	 function fn_Select(Valor_Selccionado){
	 
	   if (Valor_Selccionado =='Ins'){
	      $('#lblClave').text("No. de Inserción:");
	   }
	   else if (Valor_Selccionado =='Tras'){
	      $('#lblClave').text("No. de Transmisión:");
	   }
	   else if (Valor_Selccionado =='Ser'){
	      $('#lblClave').text("No. de Servicio:");
	   }
	 }
	
	
	function fn_quitaFormato(fld) 
{
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
 }
	
	
	function fn_Cancelar() {
	
	 $("#btnRechazo").prop('disabled', false);
	 $("#btnRechazofinal").prop('disabled', false);
	 $("#btnAutoriza").prop('disabled', false);
	
		
 }
 
 
  
    function maxLen(text, maxLen,NameText,numCampo) {
         if (text.value.length > maxLen) {
           alert ("La descripción del campo " + NameText + " debe ser máximo de " + maxLen + " caractéres");
           text.focus();
           return false;
     }
     return true;    
}
 


 
 
	
	
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato"
		action="../servlet/ComsocAutorizacionServlet" method="post">
		<input type="hidden" id="hFolioSai" name="hFolioSai"
			value="<%=c.getFolio()%>" /> <input type="hidden" id="hFolioPago"
			name="hFolioPago" /> <input type="hidden" id="hMes" name="hMes" /> 
			<input type="hidden" id="cUnidad" name="cUnidad" value="<%=UR%>" /> 
			<input type="hidden" id="id" name="id" value="<%=UR%>" /> 
			<input type="hidden" id="RamoEP" name="RamoEP" value="<%=ramo%>" /> 
			<input type="hidden" id="hFechaRechazo" name="hFechaRechazo" /> 
			<input type="hidden" Id="h_cUsuarioCaptura" name="h_cUsuarioCaptura" /> 
			<input type="hidden" Id="h_cveMedio" name="h_cveMedio" /> 
			<input type="hidden" Id="h_bAutoriza" name="h_bAutoriza" /> 
			<input type="hidden" Id="h_TipoPago" name="h_TipoPago" /> 
			<input type="hidden" Id="h_txtRechazo" name="h_txtRechazo"/>
			<input type="hidden" Id="h_txtTipoOrden" name="h_txtTipoOrden"/>
			<input type="hidden" Id="h_id_oper" name="h_id_oper"/>
			<input type="hidden" Id="h_PrendeBandera" name="h_PrendeBandera"/>
			<input type="hidden" Id="h_importe_pago" name="h_importe_pago"/> 			  
			


		<div id="container" class="container">
			<h1>Autorizaci&oacute;n de Pagos por Comunicación Social</h1>
			<fieldset>
				
				<table width="100%" border="0" class="tabla">
					<tr>
					
						<td align="right"><input type="text" id="txtTramite" readonly="readonly" size="7"
							class="notEditable"></td>
					</tr>
			</table>
			</fieldset>			
			<fieldset>
				<legend>Descripción del Pago</legend>
				<table width="100%" border="0" class="tabla">
					<tr>
						<td align="right" width="40%">Folio COMSOC:</td>
						<td><input type="text" id="txtfolioSai" readonly="readonly"
							class="notEditable"></td>
						<td align="right">Origen del Pago:</td>
						<td><input type="text" id="txtTipoPago" readonly="readonly"
							class="notEditable"></td>
					</tr>
					<tr>
						<td width="100" align="right">Folio:</td>
						<td width="100"><input type="text" id="txtFolio" name="txtFolio"
							class="notEditable" readonly="readonly" />
						</td>
						<td>No.Referencia:</td>
						<td width="100"><input type="text" class="notEditable"
							id="txtFolioLargo" readonly="readonly" />
						</td>
					</tr>
					<tr>
						<td width="100" align="right">RFC:</td>
						<td height="34"><input type="text" id="txtRfc"
							readonly="readonly" class="notEditable" />
						</td>
						<td>Nombre:</td>
						<td height="34"><input type="text" id="txtNombre"
							readonly="readonly" class="notEditable" size="50" />
						</td>
					</tr>
					
					<tr>
						<td align="right">Importe Retenido:</td>
						<td height="34"><input type="text" id="txtImporteRetencion"
							readonly="readonly" class="notEditable" />
						</td>
						<td width="100">Importe Neto:</td>
						<td height="34"><input type="text" id="txtImporteNeto"
							readonly="readonly" class="notEditable" />
						</td>
					</tr>
					<tr>
						<td>Fecha Aplicacion:</td>
						<td><input type="text" id="txtFechaAplicacion" size="8"
							readonly="readonly" class="notEditable" />
						</td>
						<td align="right">Fecha Pago:</td>
						<td height="34"><input type="text" id="txtfPago" 
							readonly="readonly" class="notEditable" />
						</td>
					</tr>
					<tr>
						<td align="right">Descripción Factura:</td>
						<td height="34" colspan="3"><textarea id="txtDescripcion"
								cols="85" rows="4" readonly="readonly" class="notEditable"></textarea>
						</td>

					</tr>


				</table>
			</fieldset>
			<fieldset>
				<legend>Detalle De la Clave Presupuestaria</legend>
				<table id="dt_catalogo" class="display" cellspacing="0"
					align="center">
					<thead>
						<tr>
							<th>Año</th>
							<th>Mes</th>
							<th>Partida</th>
							<th>Importe</th>
							<th>EP</th>

						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</fieldset>


			<br></br>

			<div id="divBotones" align="center">
				<input type="button" id="btnRechazo" name="btnRechazo" value="Rechazar a Captura"    onclick="fn_Rechazar()"> 
				<input type="button" id="btnRechazofinal" name="btnRechazofinal" value="Rechazo Definitivo del Pago" onclick="fn_RechazoDefinitivo()">
				<input type="button" id="btnAutoriza" name="btnAutoriza" value="Autorizar" onclick="fn_Autorizar()">
				<input type="button" id="btnCancelacion" name="btnCancelacion"  value="Cancelar" onclick="fn_Cancelar()">
			</div>
			<br></br>


			<div id="tabs">

				<ul>
					<li><a href="#tabs-2">Datos de Captura</a>
					</li>
					<li><a href="#tabs-3">Motivo del rechazo</a>
					</li>
				</ul>

				<div id="tabs-2">
					<fieldset>
						<legend>Datos de Captura</legend>

						<table border="0" width="25%">
							<tr>
							<tr><td width="25%" align="right">Partida Afectada:</td>
							    <td>  <input type="text" id="txtPartida" size="5"
									name="txtPartida" readonly="readonly" value=""
									class="notEditable">
								</td>
								<td width="25%" align="right">Unidad Ejecutora:</td>
								<td width="15%"><input type="text" size="45"
									id="descripcion" name="descripcion" readonly="readonly"
									class="notEditable">
								</td> 
							</tr>
							<tr>
							<td width="25%" align="right">Prestador del Servicio:</td>
							    <td colspan="3">  <input type="text" id="txtPrestador" size="80"
									name="txtPrestador" readonly="readonly" value=""
									class="notEditable">
								</td>
							</tr>
							<tr>
								
								<td align="right">Ejercicio:</td>
								<td><input type="text" id="txtEjercicio" size="5"
									name="txtEjercicio" readonly="readonly" value="<%=cEjercicio%>"
									class="notEditable">
									
							</tr>
							<tr>
								<td width="15%">Tipo de Orden:</td>
								<td><input type="radio" name="RadioOrden" id="Ins" value="Ins"  Onclick="fn_Select('Ins')">De Inserción<br>    
								    <input type="radio" name="RadioOrden" id="Tras" value="Tras" Onclick="fn_Select('Tras')">De Transmisión<br>
								    <input type="radio" name="RadioOrden" id="Ser" value="Ser" Onclick="fn_Select('Ser')">De Servicio<br>
								</td>
								<td align="right"><label id="lblClave" >Clave:</label></td>
								<td><input type="text" size="29"  maxlength="100" id="txtClave" name ="txtClave">							
							   </td>
							</tr>
							<tr>
								<td align="right">Clave de autorización:</td>
								<td><input type="text" id="txtClaveAutorizacion" maxlength="100"
									name="txtClaveAutorizacion" size="30">
								<td align="right">Clave de Campaña:</td>
								<td><input type="text" id="txtClaveCampania"
									name="txtClaveCampania" size="30">
							</tr>
									<tr>
								<td align="right">Clave Contrato:</td>
								<td><input type="text" id="txtCveContrato" name="txtCveContrato"  maxlength="100"
									size="20"></td>
								<td align="right">Fecha de Contrato:</td>
								<td><input align="right" type="text" id="txtfPublicacion"
									name="txtfPublicacion" readonly="readonly" value="" style="width: 100px"  maxlength="100"
									datepicker="true" maxlength="10" size="17" />
								</td>	
							</tr>
								<tr>
								<td align="right">Precio Unitario:</td>
								<td><input type="text" id="txtTarifaU" name="txtTarifaU" onblur="fn_CalculaTarifa()" 
									onKeyPress="return soloNumeros(event)"	>
								</td>	
								<td>Cantidad:</td>
								<td><input type="text"  id="txtCantidad" style="width: 30px" onblur="fn_CalculaTarifa()" 
										name="txtCantidad" onKeyPress="return soloNumeros(event)">
								IVA:
								<input type="text" id="txtIVA" name="txtIVA" style="width: 30px" 
									onKeyPress="return soloNumeros(event)"  onblur="fn_CalculaTarifa()" >
								</td>	
							</tr>
					  		<tr>   
								<td width="25%" align="right">Tarifa Total:</td>
								<td><input type="text" id="txtTarifaTotal"  class="notEditable" readonly="readonly"  
									name="txtTarifaTotal" >
								</td>
								<td></td>
								<td></td>
							</tr>
                           <tr>
								<td align="right">Medios de Difusión:</td>
								<td><select id="selectMedios" name="selectMedios"></select>
								</td>
								<td align="right">Unidad de Medida:</td>
								<td><input type="text" id="txtEspacio" name="txtEspacio"  maxlength="100"
									size="30"></td>
							</tr>
							
							<tr>
								<td>Concepto de la Publicación:</td>
								<td colspan="3"><textarea id="txtConcepto"   onBlur="maxLen(this,1000,'Concepto de la Publicación',1);"
										name="txtConcepto" rows="3" cols="70"></textarea> 
								</td>
							</tr>
							 <tr>
								<td align="right">Mensaje:</td>
								<td colspan="3"><textarea id="txtDescripcionMensaje" onBlur="maxLen(this,1000,'Mensaje',2);"
										name="txtDescripcionMensaje" cols="70" rows="3"
										onKeyPress="return LetrasNums(event)"></textarea></td>
							</tr>
					
								
							<tr>
								<td align="right">Destinatarios:</td>
								<td colspan="3"><textarea id="txtDestinatarios" onBlur="maxLen(this,1000,'Destinatarios',3);"
										name="txtDestinatarios" cols="70" rows="3"
										onKeyPress="return LetrasNums(event)"></textarea></td>
							</tr>
							<tr>
							<td align="Left" colspan="4">Nombre del Autorizador, RFC y Puesto :</td>
							<tr>
							<tr>
								
								<td colspan="4"><input type="text" id="txtServidor"  maxlength="200"
									name="txtServidor" size="100" />
								</td>
							</tr>
                      </table>
					</fieldSet>


				</div>
				<div id="tabs-3">

					<table border="0">

						<tr>
							<td align="left">Descripción del Rechazo:</td>
						</tr>
						<tr>
							<td>
							<input type="checkbox" id="chkRechaza" name="chkRechaza"
								value="0"></td>Rechazo
							<br>
						</tr>
						<tr>
							<td colspan="2"><textarea id="txtRechazo" name="txtRechazo"
									name="cRechazo" rows="10" cols="106"
									onKeyPress="return LetrasNums(event)"></textarea>
							</td>
						</tr>
					</table>
				</div>
				<!-- id="tabs-3" -->

			</div>
	</form>
</body>
</html>