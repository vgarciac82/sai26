<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>

<%
String DATE_FORMAT = "dd/MM/yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today= sdf.format(c1.getTime());
String cCentroContable="";
String cUR = "";
String cRamo = "";
String usu="";
boolean bAplicadoCont=false;
Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
c.getTipoCaso().getGavetaAsociada();
if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
	if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
		bAplicadoCont = true;
}

String mensaje="";



/*if(request.getParameter("msg")!=null&&!"".equals(request.getParameter("msg"))){
	mensaje=request.getParameter("msg");
	mensaje=mensaje.replace("[","");
	mensaje=mensaje.replace("]","");
	mensaje=mensaje.replace(",","<br>");
}
*/
int id_oper = -1;
if(request.getParameter("id_oper")!=null)
	id_oper= new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper=c.getCasoOperacion(0).getIdOperacion();
	
Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

//documentos del caso
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
/*
String select=c.getTipoCaso().getGavetaAsociada()+"_G"+c.getIdGabinete();//se usa por separado abajo
String cAplicaDocto="No";
if ( request.getParameter("aplicaDocto")!= null && request.getParameter("aplicaDocto").equals("Si")){
	cAplicaDocto="Si";
}
*/
//Valida Centro de Costos

if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
            cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

if (cCentroContable.isEmpty() || cCentroContable.equals("")){
		mensaje="Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}

cUR = usuario.getU_UR();
//cUR ="B3";
cRamo = usuario.getU_Ramo();
usu = usuario.getLogin();

//usu="yon";
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>   
    <title>Factura Relación de Pagos - Recepción de documentos</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"> 
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
    <script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
 	<script type="text/javascript" charset="utf-8" >
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	var cxpPrefijo = "<%=cxpPrefijo%>";
		
		$(document).ready(function(){
				$('.currency').blur(function()
				{
					$('.currency').formatCurrency();
				});
		});
		
		 $(document).ready(
            function() {
            	
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 400,
				width: 800,
				modal: true,
				close: function() {

				}
			});
			
			var bCarga = false;
	    	var aaa =<%=request.getParameter("folio")%>;
		
			var Cont1=<%=request.getParameter("folio")%>;		
		        $("#id_caso").val(aaa);
				$("#cUnidadResponsable").val( "<%=cUR%>" );       
				$("#usur").val( "<%=usu%>" );
				$("#cUnidadResponsable2").val( "<%=cUR%>" );
				$("#cCentroContable").val( "<%=cCentroContable%>" );
				
				$("#Borrar").button();
				$("#Limpia").button();
				$("#Validar1").button();
				$("#cArchivo").button();
				$("#Doc").button();
				  
			$("#tabs").tabs( {"show": function(event, ui) {var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
					if ( oTable.length > 0 ) {
					oTable.fnAdjustColumnSizing();
					}
				}
			} );
			$(".fsproductsStcokistButton[href*='javascript']").hide();
				//llenaCombos ();
			$("input.AyudaSyC").subIniciaDlg();	
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			$("#fmFactDocDiversos").ajaxForm({dataType:  "json",success: formSubmited});
			$('#grdRetencion').dataTable({         
						"bPaginate": false,
		        			"bLengthChange": false,
		        			"bFilter": false,
		        			"bSort": false,
		        			"bInfo": false,
		        			"bAutoWidth": false, 
							"sScrollY": 100,         
							"bJQueryUI": true,    
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers"    
			});
			$('#grdRetClave').dataTable({         
						"bPaginate": false,
		        			"bLengthChange": false,
		        			"bFilter": false,
		        			"bSort": false,
		        			"bInfo": false,
		        			"bAutoWidth": false, 
							"sScrollY": 100,         
							"bJQueryUI": true,    
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers"    
		
			} );
			$('#grdMovimientos').dataTable({         
							sScrollY: "150px",
							sScrollX: "700px",
							sScrollXInner: "200%",
							"bPaginate": false,
		        			"bLengthChange": false,
		        			"bFilter": false,
		        			"bSort": false,
		        			"bInfo": false,
		        			"bAutoWidth": false, 
							"bJQueryUI": true,    
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers"    
			} );
			reiniciaCompromisos();
			$('#grdFacturas').dataTable({         
							sScrollY: "150px",
							sScrollX: "700px",
							sScrollXInner: "200%",
							"bPaginate": false,
		        			"bLengthChange": false,
		        			"bFilter": false,
		        			"bSort": false,
		        			"bInfo": false,
		        			"bAutoWidth": false, 
							"bJQueryUI": true,    
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers"    
			} );
			$('#grdEPS').dataTable({         
							sScrollY: "150px",
							sScrollX: "700px",
							sScrollXInner: "200%",
							"bPaginate": false,
		        			"bLengthChange": false,
		        			"bFilter": false,
		        			"bSort": false,
		        			"bInfo": false,
		        			"bAutoWidth": false, 
							"bJQueryUI": true,    
							"bRetrive" : true,
							"bDestroy" : true,
							"sPaginationType": "full_numbers"    
			} );
			$("#nfoliorelacionGastofactura").attr('disabled', true);
			var suma = 0;
			var res_iva=0;
			});
			
		function habilitaGuardar(elPar){
			if (elPar == 0){
				$("#operacio").val(elPar);
			}else{
				$("#operacio").val(elPar);
			}
		}
		
	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		
  		if ($("#docAplicado").val() == "S") {
			alert("Documento ya fue aplicado, se avanzará a modo de CONSULTA")
			return valida_campos;
		}		
		else {
			try{
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Aplicación Relacion de Gastos");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
				p.gestion.setAplicadoCont("false");
				
	
				var nretval = cmdGuardar();
				if (nretval == -1) {
					//alert('error al guardar información');
					return false;
				}
				
				if(id_oper==2){
				//alert("entro2");
					parent.document.getElementById("pb_send").disabled=true;
					//aplicacion contable
					//var ventAppCont = window.open("../gstnmngr/AppCont?elContra="+$('#caNoContrarrecibo').val(), "popappcont", "scrollbars=1, resizable=yes, width=1024, height=400");
					//ventAppCont.focus();			
					divAplica.innerHTML = "Procesando, por favor espere.";
					$( "#dialog-form" ).dialog( "open" );
					setTimeout('fnAplicaMotor()', 3000);
	

				}
				
				//Control de estado de botones
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}
  	}

 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>"
 	}

	
  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		//var valida_doctos_requeridos = true;
  		//validaciones de documentos requeridos
  		//return valida_doctos_requeridos;
  		//return confirm("Confirmar que quiere avanzar a la siguiente operación.");
  		return true;
  	}
  	
  	function onLoadPlantilla(){

  		//parent.document.getElementById("pb_send").value='Aplicar';
  		//parent.document.getElementById("pb_send").style.visibility='hidden';
		if (<%=id_oper%> != 3 ){
			parent.document.getElementById("pb_cancel").disabled=true;
		}
		//parent.document.getElementById("pb_cancel").style.visibility='hidden';
		//parent.document.getElementById("pb_save").disabled=true;
		enInicio();
		BuscaPoliza();  
		
		//	parent.document.getElementById("pb_save").disabled=true;
		//}
  	
  	}
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1){

  		 	return "AUTORIZA_" + $("#cDocumento").val();
			}
  		 if(id_oper==2){

  		 	return "CONSULTA_" + $("#cDocumento").val();
			}

  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "autoriza_factura";
  		if(id_oper==2)
  		 	return "consulta_factura";
  	}

	function onPostDisplay(){
		if ($("#docAplicado").val() == "S") {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}
	}
	
	function formSubmited() {

	}
	
	
	function cmdGuardar() {
		$("#nombre").val($("#cIdRelacion").val());
		$("#DCD_IMP_BRUTO2").val($("#mImporteNeto2").val());
		$("#mImporteMasIva").val();
		$("#nMes").val($("#txtFechaPago").val().split("/")[1]); 
		$("#cMes").val($("#nMes").val());
		if ($("#numPaso").val() == '1'){
			var hayError = '';
			$('.paso01').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			
		$('#cIdRFC_RelacionGasto').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			
			if (hayError==''){
				queryFormPost("tNominaEncabezadoCreate",{async: false });
				queryFormPost("tNOMINAEncabezadoUpdate",{async: false });
				alert("Caratula guardada!");
				$(".paso01").attr('disabled', true);
				$("#numPaso").val(2);		
				$(".pasoDos").show();
				$(".paso02").hide();
				$("#cIdRFC_RelacionGasto").attr('disabled', true);
				$("#L02").click();
			}else{
				alert('Debe ingresar los siguientes datos: '+ hayError);
			}
		}else  if ($("#numPaso").val() == '2'){
			$(".paso02").hide();
			$("#nombre").val($("#cIdRelacion").val());
			var otro=$("#txtFolioFact").val();
			$("#txtFolioFact").val(otro);						
			queryFormPost("tNominaFinanciamientoCreate",{async: false });
			parent.document.getElementById("pb_save").disabled=true;
			alert("Financiamiento guardado!");
			$(".paso02").attr('disabled', true);
			$("#numPaso").val('5');
			$("#cIdRFC_RelacionGasto").attr('disabled', true);
			$("#fRecepcion2").val($("#fRecepcion").val());
			$("#DCD_FECHA_FACTURA").val($("#fRecepcion2").val());
			$("#DCD_FACTURA").val($("#nombre").val());
			$("#DCD_IMP_BRUTO2").val($("#mImporteNeto").val());
			$("#DCD_NETO").val($("#DCD_IMP_BRUTO").val());
			$("#DCD_CONCEPTO").val($("#cConcepto").val());
			$("#L03").click();
		}else  if ($("#numPaso").val() == '5'){
			queryFormPost("CambioStatusNOMINAUpdate", {async: false });		
			parent.document.getElementById("pb_save").disabled=true;   
			parent.document.getElementById("pb_send").disabled=false;
			
			$(".paso03").attr('disabled', true);
			//$("#divImprime").show();
			//	cmdImprimir("PolizaPago");
			//	alert("Autorizacion guardada!");
		}
	}
	function enInicio(){
		if ($("#mensaje").val() !="" ){
			alert($("#mensaje").val());
		}
		queryFormPost("cEjercicioRead", {async: false });
		$("#txtFolioFact").val($("#id_caso").val());
		$("#mImporteNeto2").val($("#mImporteNeto").val());
		$("#DCD_IMP_BRUTO2").val($("#mImporteNeto2").val());
		$("#divAutorizar").hide();
		//$("#Borrar").hide();
		//$("#Limpia").hide();
		$("#divImprime").hide();
		$("#divImprimePoliza").hide();
		$("#divImprimeAnexo").hide();		 
		bCarga = true;
		querySelectPost("CAT_TIPO_IVARead", "DESCRIPCION20",{async: false });
		queryFormPost("fRecepcionRead",{async: false });           		  		   
		queryFormPost("TipoPolizaRead", {async: false });
		//querySelectPost("CAT_TIPO_OPERACIONRead", "TIPO_OPERACION",{async: false });
		//querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO",{async: false });
		//querySelectPost("CatalogoObraTConceptoRead", "TIPO_CONCEPTO",{async: false });
		//queryFormPost("cCentroContableRead", {async: false });
		cambio();
		cambio2();
		//concepto();
		//concepto2();
		//tfondos();
		if (<%=id_oper%> ==3 ){
			$("#divAutorizar").hide();
			$("#divImprime").show();
			$("#divImprimePoliza").show();		 
			$("#divImprimeAnexo").show();		 
			
			
			$("#Agregar").attr('disabled', true);
			$("#Limpia").attr('disabled', true);
			$("#Borrar").attr('disabled', true);
			$("#cArchivo").attr('disabled', true);
			$("#Doc").attr('disabled', true);
		}		   
		setTimeout("confolio()",100);
	}		   
	function concepto(){
		querySelectPost("CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
		concepto2();
	}	   
	function concepto2(){
		querySelectPost("CatalogoObraTMovimendo2Read",{async: false });
	}		   	   
	function confolio() {
		queryFormPost("tNOMINAEncabezadoRead","txtFolioFact",{async: false });			
		setTimeout("retraso()",100);
	}   
	function retraso(){   
		if ( $("#cIdRelacion").val()!=""){
			$(".pasoDos").show();
			$(".paso01").attr('disabled', true);
			$("#cIdRFC_RelacionGasto").attr('disabled', true);
			$("#numPaso").val('2');
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
			queryFormPost("tNOMINAFinanciamientoRead","id_caso",{async: false });	//aki		
			queryFormPost("tNOMINAClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });						  		   			
			setTimeout("retraso2()",100);	
			$(".pasoTres").hide();
		 
		}else{
			$(".pasoDos").hide();
			$(".pasoTres").hide();
			$(".pasoULTIMO").hide();
		}
	}
	function retraso2(){
		if ( $("#oficioElegibilidad").val()!=""){
			if ($("#oficioElegibilidad").val() == "FF"){
				$(".paso02").hide();   
				$("#TFONDO").attr('disabled', true);   
				$(".pasoTres").show();
			}else{
				$("#TFONDO").attr('disabled', true);   
				$(".pasoTres").show();
				$(".paso02").attr('disabled', true);   
			}
			queryFormPost("tNOMINADocumentRead","caNoContrarrecibo",{async: false });				
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
			queryFormPost("tNOMINAClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });						  		   
			DOCUMENTACION();
			claves();		
			setTimeout("retraso5()",1000);	
			setTimeout("retraso3()",400);			
		}else{
			tfondos();
		}
	}
	function retraso5(){
		if ( $("#cllave").val()  !=  0   ){
			$("#Validar1").attr('disabled', true);
			$(".pasoTres").show();	
		}else{
			$(".pasoTres").hide();	
		}
		if (<%=id_oper%> == 3 ){
				$(".pasoTres").show();
		}
	}
	function retraso3(){
		if ( $("#caNoContrarrecibo").val()!=0  && (<%=id_oper%> == 2 ) ){  
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
			queryFormPost("tNOMINAClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });						  		  
			$("#DCD_IMP_BRUTO2").val($("#mImporteNeto").val());
			$("#DCD_NETO").val($("#DCD_IMP_BRUTO2").val());
			$(".paso03").attr('disabled', true);   
			$("#divAutorizar").show();
			$("#numPaso").val('5');
		}else{
			$(".paso03").attr('disabled', false); 
		}
	}	
	function DOCUMENTACION(){    
		var szWhere = "";     
		szWhere = " caNoContrarrecibo ='"+  $("#caNoContrarrecibo").val()+"'";
		var elMonto = "";     
		var este=0;          
		var szTabla = "DOCUMENTACION_RELACION_GASTO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'true'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              
				$('#grdFacturas').dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12,j[i].Col13,j[i].Col14 ]);
			}
		})   
	}		  
</script>   
<script type="text/javascript" charset="utf-8">
var tot=0;			
	function fnClickAddRowRetClave(A, B, C, D) {
		$('#grdRetClave').dataTable().fnAddData( [
			A,
			B,
			C, D
			] );
	}
	function fnClickAddRowComp() {
		$('#grdFacturas').dataTable().fnAddData( [
			$("#DCD_FACTURA").val(),
			$("#DCD_FECHA_FACTURA").val(),
			$("#DCD_TBEN").val(),
			$("#DCD_CBEN").val(),
			$("#DCD_TIPO_OPE").val(),
			$("#DESCRIPCION20").val(),
			$("#DCD_IMP_BRUTO").val(),
			$("#DCD_IVADES").val(),
			$("#DCD_IVA").val(),
			$("#DCD_ISR").val(),
			$("#DCD_MIL5").val(),
			$("#DCD_MIL2").val(),
			$("#DCD_CONTRIBUCION").val(),
			$("#DCD_OTRAS_RET").val(),
			$("#DCD_PENALIZACION").val()
		] );
		$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
		$("#RFC").val($("#cIdRFC_RelacionGasto2").val());
		$("#fRecepcion2").val($("#fRecepcion").val());
		getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});

		//queryFormPost("SIG_FOLIO_CONTRARRECIBORead", {async: false });	
		setTimeout("contrareibo()",1000);
		parent.document.getElementById("pb_send").disabled=false;					
	}	
	
	function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#cEjercicio").val() + seqValue;
	$("#caNoContrarrecibo").val( seqValue );
}

	
	function no_foco(elemento) {
		elemento.style.border = "1px solid #CCCCCC";
	}
	function contrareibo(){
		$(".paso03").attr('disabled', false);
		queryFormPost("ActualizaContraNOMINAUpdate", {async: false });		
		queryFormPost("tDocumentacionComprobatoriaDetCreate",{async: false });
		$(".paso03").attr('disabled', true);
		$("#divImprime").show();
		//cmdImprimir('Contrarecibo');
		setTimeout("elRetardo()",1000);
	}			
	function fnClickAddRowB(A, B, C) {
		$('#grdRetencion').dataTable().fnAddData( [
			A,
			B,
			C
		]);
	}
	function fnClickAddRowC(A, B,C) {
		$('#grdCompromisos').dataTable().fnAddData( [
			A,
			B,
			C
		] );
	}
	function CargaArchi1(A,B,C,D) {
		$('#grdEPS').dataTable().fnAddData( [
			A,B,C,D
		] );
	}
	function fnClickAddRowZ(A, B, C,D,E,F,G,H,I,J) {					
		$('#grdMovimientos').dataTable().fnAddData( [
			A,
			B,
			C,
			D
		] );			 
	}
	var contador=0;			
	function cambioMovmientos(){   		
		var validM;			
		validM=quitaFmt( $("#mMovimiento2").val());						                           
		$("#txtFechaPago2").val($("#txtFechaPago").val());                                  
		var szWhere = "";     
		var elMonto = "";  
		var campos = "";  
		campos = $("#cEjercicio").val() + ", "+$("#txtFechaPago2").val().split("/")[1]+ ", '" + $("#EP").val()+"'";
		szWhere = " clavesiaff =substring('"+$("#EP").val()+"',1,55)  ";
		var szTabla = "SALDOS_DISPONIBLE_PAGO";                                                                                         
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){                     
			var acumulado = 0.0;
			for (var i = 0; i < j.length; i++) {              
				acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
				$("#TOTALSUBCUENTA").val(acumulado);
			}
			if (acumulado < validM){
				alert('La cuenta no tiene suficiente saldo comprometido');
				return;
			}
			var resto = parseFloat(validM);
			var aplicar = 0.0;
			for (var i = 0; i < j.length; i++) {
				if (resto-parseFloat(j[i].Col2) >0){
					aplicar = j[i].Col2;
				}else{
					aplicar = resto;
				}
				resto = resto-aplicar;
				if (parseFloat(aplicar) > 0.0) {
					fnClickAddRowC(j[i].Col0, j[i].Col1,aplicar);
				}
			$("#agrega2").attr('disabled', false);
			}
		})   
	}
	function Limpia1(){
		queryFormPost("tNominaDetalleDelete",{async: false });	
		queryFormPost("tNominaCargaADelete",{async: false });		
		queryFormPost("tNominaTMPDelete",{async: false });	
		$('#grdEPS').dataTable().fnClearTable(); 
		$('#grdMovimientos').dataTable().fnClearTable(); 
		$("#Validar1").attr('disabled', false);
		$("#Limpia").hide();
		$("#SUMACUENTAS").val("");
	}
	function cargaArchivo(){                                      
	document.getElementById("esperar").style.visibility="visible";
		$("#SUMACUENTAS").val(0);
		$("#Validar1").attr('disabled', true);
		var con = 0;
		var hayError = "";
		var ceros="";
		var contador=0;		
		var sum=0;
		var neto=quitaFmt($("#mImporteNeto").val());
		$("#txtFechaPago2").val($("#fAplicacion").val()); 
		$("#cUnidadResponsable2").val( "<%=cUR%>" );
		$("#cConcepto").val($("#DCD_CONCEPTO").val());
		var messs=parseInt($("#txtFechaPago2").val().split("/")[1], 10);          
		var aaaa=parseInt($("#txtFechaPago2").val().split("/")[2], 10);          
		var szWhere = "";     
		var campos = " '"+ $("#id_caso").val()+"','"+ aaaa+"','"+messs+"'";     
		szWhere = "";
		var elMonto = "";     
		var szTabla = "CARGA_ARCHIVO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, Campos: campos, ajax: 'false'}, function(j){                       
			var options = '';
						try{
			for (var i = 0; i < j.length; i++) {              
			contador=j.length;
			
						///alert("entro2 " + contador );
			
				if (j[i].Col1.toString().substring(0,6) == "ERROR."){
					hayError = "Se encontraron algunas Insuficiencias Presupuestales. No se procesó el archivo. <VERIFIQUE DETALLE>";
					$("#grdEPS").dataTable().fnAddData([j[i].Col0, j[i].Col3, j[i].Col2, j[i].Col1 ]);
					
					if (Number (i )== Number(contador- 1 ) ){
					alert(hayError);		
					document.getElementById("esperar").style.visibility="hidden";					
					}
					
				}else{
				
				//	$("#DCD_IMP_BRUTO").val(k[i].Col1);
				//	$("#EP").val(k[i].Col0);
				//	$("#mMovimiento2").val(k[i].Col1);
				//	$("#mMovimiento").val(k[i].Col1);
				//	$("#CONCEPTO").val(k[i].Col2);
				//	tam=k[i].Col3;
				//	for (var t1=tam.length ;t1 < 5 ;t1++ ){
				//		ceros="0"+ceros;
				//	}
				//	$("#MOVIMIENTO").val( ceros+ k[i].Col3);					   
				//	ceros="";
				//	sum=$("#mMovimiento2").val();
				sum = parseFloat (j[i].Col0) + parseFloat ($("#SUMACUENTAS").val());
					$("#SUMACUENTAS").val( sum );	    
				//	fnClickAddRowZ(k[i].Col6, k[i].Col7, k[i].Col5,k[i].Col1);
				//	$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());						
				//	$("#cMes").val(k[i].Col4);	
				//	$("#EP2").val($("#EP").val());
				//	$("#cEvento").val(k[i].Col8);
				//	queryFormPost("tNominaDetalleCreate",{async: false });	
				if ( Number (i )== Number(contador- 1 )  ){
				document.getElementById("esperar").style.visibility="hidden";
				con = Math.round( parseFloat ($("#SUMACUENTAS").val())*100)/100;
				 //queryFormPost("tFNcargaArchivoRead",{async: false });	
				
				$("#SUMACUENTAS").val(con);						
				if(parseFloat (con) == parseFloat ( $("#mImporteNeto").val() ) ){
					cargaArchivo2();		
					$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
					$("#DCD_IMP_BRUTO").val($("#mImporteNeto").val());
					$("#DCD_NETO").val($("#mImporteNeto").val());
					$("#Validar1").attr('disabled', true);
					$(".pasoTres").show();
					//queryFormPost("tNominaCargaADelete",{async: false });	
				}else{
					alert("El monto del archivo no corresponde a el Importe Neto");
					$("#Validar1").attr('disabled', true);
					$("#Borrar").show();	  
					$(".pasoTres").hide();
				}		   
				
				
				}
				
				}
				
			}
		}catch(e) {
		alert(e);
		}
			
			
		})   
		if (hayError ==""){
		
		}else { 
			alert(hayError);
			$("#Validar1").attr('disabled', true);
			$("#Limpia").show();
			document.getElementById("esperar").style.visibility="hidden";
		}
		
		
		
		
		
	}

	function cargaArchivo2(){
			// 11,'YON','a',2012,10,'miguel2',1
				document.getElementById("esperar2").style.visibility="visible";
	//		alert("entro");	
		var campos = " "+$("#id_caso").val()+",'"+$("#nombre").val()+"','"+$("#cIdCuentaContable").val()+"',"+ $("#cEjercicio").val() +","+$("#cCentroContable").val() +",'"+$("#cIdRFC_RelacionGasto2").val()+"',"+$("#ALM").val()+"";     
		szWhere = "";
		var elMonto = "";  
		var contador=0;		
		var szTabla = "CCARGA_ARCHIVO_DETALLE";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, Campos: campos, ajax: 'false'}, function(k){                       
			var options = '';
		try{
			for (var i = 0; i < k.length; i++) {              
			contador=k.length;
		//	alert(i);
				if ( i == ( contador - 1 ) ){
					document.getElementById("esperar2").style.visibility="hidden";
					alert("Validación exitosa");
					$("#L04").click();
			//queryFormPost("tNominaTMPDelete",{async: false });	
				}
		}	
		
		}catch(e) {
			alert(e);
		}
		
		}) 

	}
	
	
	function elRetardo(){
		elParametro = "'"+    $("#caNoContrarrecibo").val()   +"', 1, '0', '0', '"+$("#DCD_IMP_BRUTO").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_SANCION").val()+"', '"+$(				"#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','10'," + $("#cEjercicio").val() + ",'"+$("#nombre").val()+"', 'B','0','"+$("#cIdRFC_RelacionGasto2").val()+"'";
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIVERSOSCREATE", Param: elParametro, MaxReg: "", ajax: 'true'}, function(j){});
		
		
		}
	function FiltroRetenciones(){                                      
		var szWhere = "";     
		szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"'";
		var elMonto = "";     
		elMonto = $("#montoMovimiento").val();
		var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'true'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              
				fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
			}
		})   
	}
	function reiniciaCompromisos(){
		$('#grdCompromisos').dataTable({         
				"bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bInfo": false,
				"bAutoWidth": false, 
				"sScrollY": 100,         
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"    
				} );
	}
	function FiltroMovimientos(){ 
		$("#txtFechaPago2").val($("#fAplicacion").val()); 
		$("#cUnidadResponsable2").val( "<%=cUR%>" );
		$("#cConcepto").val($("#DCD_CONCEPTO").val());
		
		var checa=true;
		var validN;
		var validM;
		var valid; 
		var valEve='CD_';
		validM=quitaFmt( $("#mMovimiento2").val());						                           
		validN=quitaFmt( $("#mMovimiento2").val());
		var mes =["MontoEnero","MontoFebrero","MontoMarzo","MontoAbril","MontoMayo","MontoJunio","MontoJulio","MontoAgosto","MontoSeptiembre","MontoOctubre","MontoNoviembre","MontoDiciembre"];   
		var fecha=parseInt($("#txtFechaPago2").val().split("/")[1], 10);          
		var elMonto = " where " +  " nClaveCNA=" + $("#nClaveCNA1").val();
		var token ="";
		var campos="";
		for (var i =0; i < fecha; i++){
			campos += token + mes[i]
			token = " + " 
		}
		var acum=0.0;  
		var acum= parseFloat($("#Acumulado_Op").val());   
		var szWhere = "";  
		//elMonto = "2012, "+$("#fRecepcion").val().split("/")[1]+", '"+$("#EP").val()+"'";
		var szTabla = "VDISPONIBLEEP";      
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos,ajax: 'true'}, function(j){                       
				var acumulado = 0.0;
				for (var i = 0; i < j.length; i++) {	 
					acumulado = parseFloat(acumulado) + parseFloat(j[i].Col3);
					//$("#TOTALSUBCUENTA").val(acumulado);
				}
				
				if (acumulado < validM){
					alert('La cuenta no tiene suficiente disponible');
					return;
				}
				
				var aplicar = 0.0;
				var resto = parseFloat(validM);
				for (var i = 0; i < j.length; i++) {
					if (parseFloat(j[i].Col3)-parseFloat(resto) >0){
						aplicar = resto;
					}else{
						aplicar = parseFloat(j[i].Col3);
					}
					if (parseFloat(j[i].Col3)==0){
						aplicar = 0;
					}
					if (parseFloat(aplicar) > parseFloat(validN)){
						alert("Con este movimiento pasaría el Importe Neto");
						return;
					}
					resto = resto-aplicar;
					if (parseFloat(aplicar) > 0.0) {
						$("#cEvento").val( "" );
						
						queryFormPost("eventpEP2Read", {async: false });
						if ($("#cEvento").val() =="" ){
							alert('La cuenta no corresponde al concepto');
							return;
						}
					$("#cEvento").val($("#cEvento").val());
					fnClickAddRowZ(j[i].Col1, j[i].Col2, j[i].Col0,aplicar,"","","","","","");
					$("#nMes").val($("#txtFechaPago2").val().split("/")[1]); 
					$("#cMes").val($("#nMes").val());
					$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());						
					var table = document.getElementById('grdCompromisos');
					var rowCount = table.rows.length;            		
					for(var i=1; i<rowCount; i++) {
						var row = table.rows[i];                		
						var elMesGrd = ''
						var elMontoGrd = ''
						var elEPGrd = ''
						try{
							var elMesGrd = row.cells[0].childNodes[0];
							var elEPGrd= row.cells[1].childNodes[0];
							var elMontoGrd = row.cells[2].childNodes[0];						
							var mess;
						}catch(e) {
						null;
						}
						if(null != elMesGrd ) {
							if(elMesGrd.toString() != ""){
								mess='0'+ elMesGrd.toString();
								$("#cMes").val(mess);	
								$("#DCD_IMP_BRUTO").val(elMontoGrd.toString());
								$("#EP2").val($("#EP").val());
								queryFormPost("tNominaDetalleCreate",{async: false });	
								table.deleteRow(i);
								rowCount--;
								i--;
							}
						}							
					}
					$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
					$("#mImporteNeto").val(validN);
					$("#DCD_IMP_BRUTO").val($("#mImporteNeto").val());
					$("#acumuladoOperacion").val(parseFloat($("#acumuladoOperacion").val()) + parseFloat(validM));
					if(Number($("#acumuladoOperacion").val()) == Number($("#mImporteNeto").val())){ 
						$("#agrega2").attr('disabled', true);
						$(".extra").attr('disabled', true);
						$(".pasoTres").show();
					}
				}
				}
			})
	}
	$(function() {
		$( "#txtFechaPago" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	$(function() {
		$( "#fechaFactura" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	$(function() {
		$( "#DCD_FECHA_FACTURA" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});	
	$(function() {
		$( "#txtFechaOficio" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});

	$(function() {
		$( "#fElegibilidad" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	$(function() {
		$( "#fAplicacion" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	$(function() {
		$( "#fRecepcion" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	function cambioss(){
		cambioMovmientos();
	}
	function cambio(){
		cambio2();
	}
	function cambio2(){
		queryFormPost("PrestamoCategoriaInversion2Read",{async: false });
		$("#vcInvercion").val($('#nPorcentajeFinanciamiento').val());  
		if(Number($("#nPorcentajeFinanciamiento").val())==100){
			$("#contraparte").val(0);
		}else{
			$("#contraparte").val( 100-Number($("#nPorcentajeFinanciamiento").val()));
		}
	}
	function tfondos(){
		if ($('#TFONDO option:selected').val()=="FF"){
		//	 $(".paso02").attr('visible', true);
			$(".paso02").hide();
			$("#oficioElegibilidad").val("FF");
		}else{
			$("#oficioElegibilidad").val(" ");	
			$(".paso02").show();
			cambio();
		}
	}
	function validar3(e) { 
		tecla = (document.all) ? e.keyCode : e.which; 
		if (tecla==8) return true; 
		patron =/[A-Za-z.\d\s\\-]/; 
		te = String.fromCharCode(tecla); 
		return patron.test(te); 
	} 
	function validar(e) { 
		tecla = (document.all) ? e.keyCode : e.which; 
		if (tecla==8) return true; 
		patron =/[A-Z.\d\s\\-]/; 
		te = String.fromCharCode(tecla); 
		return patron.test(te); 
	} 
	function validar2(e) { 
		tecla = (document.all) ? e.keyCode : e.which; 
		if (tecla==8) return true; 
		patron =/[.\d]/; 
		te = String.fromCharCode(tecla); 
		return patron.test(te); 
	} 
	var tipo;
	function cmdImprimir(elFormato){
	 if (elFormato =='PolizaPago'){
	 elFormato=elFormato+'N';
	 tipo='CR.';
	 }else{
	 tipo='';
	 }
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=" + elFormato + ".jasper"
						+ "&whereFolio= "+  tipo +"caNoContrarrecibo = '" + $("#caNoContrarrecibo").val()
						+"'", 			//+ "&nombre="   + ""
						//+ "&cargo="    + ""
						//+ "&area="     + "",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
		
		//$("#anex").val(elFormato);		
		setTimeout('anexo("' + elFormato + '")', 5000);	
					
					
}
function anexo( pfmt ){

			if ( $("#cllave").val() == 1 && pfmt == "PolizaPago" ){

				
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=ANEXO"
						+ "&accion=run"
						+ "&rn=Anexo1.jasper"
						+ "&swhere= and caNoContrarrecibo ='"+ $("#caNoContrarrecibo").val()+"'" ,  
					"Anexo",
					"scrollbars=1, resizable=yes, width=1024, height=768");
	}
}

	
	
	
	function claves(){                                  
		document.getElementById("esperar").style.visibility="visible";
		var szWhere = "";     
		szWhere = " tr.nFolioNOMINA ='"+  $("#id_caso").val()+"'";
		var elMonto = "";     
		var este=0;          
		var szTabla = "CLAVES_NOMINA";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'true'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              
				fnClickAddRowZ(j[i].Col0, j[i].Col1, j[i].Col2,j[i].Col3,"","","","","","");
				//fnClickAddRowZ(k[i].Col6, k[i].Col7, k[i].Col5,k[i].Col1,"","","","","","");
				$("#cllave").val(1);
			}
			
		})   
		document.getElementById("esperar").style.visibility="hidden";
	}
	function Borrar1(){
		$("#elcontra").val($("#caNoContrarrecibo").val());
		//queryFormPost("tNominaEncabezadoDelete",{async: false });	
		//queryFormPost("tNominaFinanciamientoDelete",{async: false });	
		//queryFormPost("tNominaDetalleDelete",{async: false });	
		parent.document.getElementById("pb_send").disabled=true;
		parent.document.getElementById("pb_cancel").disabled=false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled=true;
	}
	function Sinfrmt( fld ){
	   var valcol = fld.value ;
	   valcol = valcol.replace("$", "");
	   valcol = valcol.replace(",", "");
	   $("#" + fld.id).val( valcol );
	}

	function cambiafrmt( fld )	{
	    $("#" + fld.id).formatCurrency();
	}
	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}
	
	function BuscaPoliza(){
		var szWhere = " nFolioNomina = " + $("#id_caso").val();
		var elMonto = "1";
		$("#laPoliza").val("");
		$("#docAplicado").val("");
		var szTabla = "TPAGONOMIDOCAPLICADOREAD";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			for (var i = 0; i < j.length; i++) {
				$("#laPoliza").val(j[i].Col0);
				$("#docAplicado").val(j[i].Col1);
			}
		})
	
	}	


	
	</script>

  </head>
  
  <body id="dt_example">

<div id="container" class="container SyCData">
<div id="container">

		<!--<form>-->
		<form method="post" action="../gstnmngr/cargaNomina" name="cargarCSVNomina" id="cargarCSVNomina" enctype="multipart/form-data">
	        <input name="nfoliocontratodiversoencabezado" type="hidden" id="nfoliocontratodiversoencabezado" value="" size="5">
			<input type="hidden" id="rowsAffected" name="rowsAffected">		
	        <input name="numPaso" type="hidden" id="numPaso" value="1" size="5" readonly>		
			<input name="CONTA" type="hidden" id="CONTA" value="0" size="5" readonly>		
		    <input type="hidden" id="cRamo" name="cRamo" value="16">
		    <input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable">
			<input type="hidden" id="cUnidadResponsable2" name="cUnidadResponsable2">
			<input type="hidden" id="cCentroContable" name="cCentroContable">
			<input type="hidden" id="mImporteMasIva" name="mImporteMasIva" value="0">
			<input type="hidden" id="docAplicado" value="">
			<input type="hidden" id="laPoliza" />
			
			
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">

			
			
			<input type="hidden" id="nMes" name="nMes">
			<input type="hidden" id="cMes" name="cMes">
			<input type="hidden" id="RFC" name="RFC">
			<input type="hidden" id="aEjercicioFiscal" value="2012" name="aEjercicioFiscal">
			<input type="hidden" id="usur" name="usur">
			<input type="hidden" id="operacio" name="operacio" value="0">

			<input type="hidden" id="ALM" name="ALM" value="1">


<input name="cEvento" type="hidden" id="cEvento" value="CD_AL01">
<input name="cimpo" type="hidden" id="cimpo" value="">
<input name="mTotalEPNomina" type="hidden" id="mTotalEPNomina" value="0">


<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>"/>
<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>"/>
<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>"/>
<input type="hidden" maxlength="15" size="15" name="cIdRFC_RelacionGasto2" id="cIdRFC_RelacionGasto2"  />  		  					
  <input name="txtFolioFact" type="hidden" id="txtFolioFact" readonly class="paso01" value="" size="5" >
	    <input type="hidden" value="2012" id="cEjercicio" name="cEjercicio">
		<input type="hidden" id="cIdTipoDocumento" name="cIdTipoDocumento" value="3">		
        <input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="3" />
		<input name="cIdCuentaContable" type="hidden" id="cIdCuentaContable" size="20">
		<input name="cllave" type="hidden" id="cllave" value="0" >
		
 <!--para nomina -->
 		<input name="TIPO_OPERACION" type="hidden" id="TIPO_OPERACION" value="NOMINA" >
 		<input name="DESTINO_GASTO" type="hidden" id="DESTINO_GASTO" value="NOMINA" >
		<input name="cUnidadEjecutora" type="hidden" id="cUnidadEjecutora" value="">
		<input name="CONCEPTO" type="hidden" id="CONCEPTO" value="" >
 		<input name="MOVIMIENTO" type="hidden" id="MOVIMIENTO" value="" >
	
        <h1>Documentos N&oacute;mina- Recepci&oacute;n de documentos</h1>
    <div class="dvGeneral">
		<div id="divAutorizar"> 
                <label id="lbAutorizar">Autorizar
                <input type="radio" id="grpAutorizar" name="grpAutorizar"  checked="checked" onclick="habilitaGuardar(1)" value="Si">
                Si</label> 
                <label> 
                <input type="radio" id="grpAutorizar" name="grpAutorizar" onclick="habilitaGuardar(2)" value="No">
                No</label>
              &nbsp;&nbsp;&nbsp;&nbsp; 
		</div>	
			  <div id="divImprimeAnexo"><img src="imagenes/Imprimir.png" width="25" height="21"			onClick="anexo('PolizaPago');"> Anexo
								</div>
              <div id="divImprimePoliza" ><img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaPago');"> Poliza   </div>

	   		
					<label id="esperar" style="visibility: hidden">
					<div align="center">Espere por favor....
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
					</label>
					<label id="esperar2" style="visibility: hidden">
					<div align="center">Espere por favor....................................................................
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
					</label>
		
		  
    <table  border="0">
	<tr>
	<td></td>
	</tr>
      <tr align="left"> 
        <td colspan="3" valign="top" nowrap>Relaci&oacute;n No.: 
              	<input id="cIdRelacion" name="cIdRelacion"  maxlength="40" size="40" type="text" onkeypress="return validar(event)" class="paso01"/>  
		  		<input id="nombre" name="nombre"  maxlength="40" size="40" type="hidden" value="0" />  
          &nbsp;&nbsp;&nbsp;No. contrarecibo: 
              	<input name="caNoContrarrecibo" id="caNoContrarrecibo" size="14" value="0" readonly/> 
	   <!--  <input type="checkbox" name="checkbox" value="checkbox">
          Con formato </td>-->      
		  <td>
		  	<input type="button" value="Borrar" onclick="Borrar1();" id="Borrar"  name="Borrar" class="btnInterfaceBG" />
		  </td>
		  </tr>
      
      <tr align="left"> 
        <td colspan="3"  valign="top">R F C:         
        	<input type="text" maxlength="15" size="15" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto"   class="AyudaSyC"/>  		  			
          	<input type="text" maxlength="100" size="70" name="cnombre" ID="cnombre" value="" readonly class="paso01" />  
		  <td>Folio:
			  <input name="id_caso" type="text" id="id_caso"  size="10" maxlength="40" readonly/>
		  </td>
      </tr>
    
      <tr >
        <td valign="top">Fecha de Aplicaci&oacute;n </td>
        <td valign="top">Fecha Programada de Pago</td>
        <td valign="top">Fecha de Recepci&oacute;n </td>
				  <td>&nbsp;</td>
      </tr>
      <tr > 
        <td valign="top"><input name="fAplicacion" class="paso01" type="text" id="fAplicacion" value="30/01/2012"  maxlength="10"  size="10"/></td>
        <td valign="top">&nbsp;
        	  <input name="txtFechaPago" class="paso01" type="text" id="txtFechaPago"   maxlength="10"  size="10"/>
		   	  <input name="txtFechaPago2" type="hidden" id="txtFechaPago2"   maxlength="10"  size="10"/>		  
		</td>
        <td valign="top">&nbsp;
          	<input name="fRecepcion" class="paso01" id="fRecepcion" size="10" value="05/12/2012" />
          	<input name="fRecepcion2"  id="fRecepcion2" type="hidden" size="10" value="05/12/2012" readonly/>          
         </td>
		  		  <td>&nbsp;</td>
      </tr>
    </table>
  </div>
		
		
  <div id="demo2" > </div>
	
  <div class="dvFuente"> </div>
 	<div id="multitabs" style="width:860px">
	<div id="tabs" style="width:100%">
		<ul>
			<li><a id="L01"  href="#tabs-1"  >Concepto</a></li>
			<li><a id="L02" href="#tabs-7" class="pasoDos" >Financiamiento</a></li>
            <li><a id="L03" href="#tabs-3" class="pasoDos" >Movimientos</a></li>
			<li><a id="L04" href="#tabs-4" class="pasoTres"  >Documentaci&oacute;n</a></li>
		</ul>
	<div id="tabs-1" >
      <table width="800" border="0">
        <tr> 
          <td width="13%">Importe neto: </td>
          <td><input name="mImporteNeto"  class="paso01" type="text" id="mImporteNeto"      onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);"
		      onkeypress="return validar2(event)" size="20"/>
		  	<input name="mImporteNeto2"   type="hidden" id="mImporteNeto2"   size="10"/>
		  </td>          
        </tr>        
        <tr>
          <td>Concepto:</td>
          <td></td>
        </tr>	
        <tr> 
          <td colspan="5">
          		<textarea name="cConcepto"  class="paso01" id="cConcepto" onkeypress="return validar3(event)" style="height: 91px; width: 448px"></textarea>
          </td>
          <td></td>
        </tr>        
      </table>
		</div>
		<div id="tabs-3" >
        <table height="66" width="98%" border="0">
		
			<tr>
					<td>
						<jsp:include page="/Generador/nominaCarga.jsp"></jsp:include>
					</td>
					<td>
					</td>
					<td align="right">
						    <input type="button" value="Validar" onclick="  cargaArchivo();" id="Validar1"  name="Validar1" class="btnInterfaceBG"/>
							<input type="button" value="Limpia" onclick="Limpia1();" id="Limpia"  name="Limpia" class="btnInterfaceBG"/>
							<input name="TIPO_CONCEPTO" type="hidden" id="TIPO_CONCEPTO" value="">			
					</td>
			</tr>
			<tr>
				<td>Monto del Archivo  
				    <input name="SUMACUENTAS" type="text" id="SUMACUENTAS" value="" readonly   onFocus="Sinfrmt(this)" onBlur="cambiafrmt(this); no_foco(this);"></td>
				<td>
					<input name="EP" type="hidden" id="EP" size="66" class="paso9" readonly/>
					<input name="EP2" type="hidden" id="EP2" size="66" class="paso9" readonly/>					
					<input name="nClaveCNA1" type="hidden" id="nClaveCNA1" size="10"  readonly/>
				</td>
				<td>
					<input name="mMovimiento" type="hidden"  class="paso9" onkeypress="return validar2(event)"    onChange="cambioss()"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this); no_foco(this);" id="mMovimiento" size="10"/>
					<input type="hidden" id="acumuladoOperacion" name="acumuladoOperacion" value="0"/>
                    <input name="mMovimiento2" type="hidden" id="mMovimiento2" size="10"/> 
                    <input name="cInterna" type="hidden" id="cInterna"size="10"/>
		            <input name="TOTALSUBCUENTA" type="hidden" id="TOTALSUBCUENTA"/>					  
				 	<input name="total1" type="hidden" id="total1" value="0" size="10"/>
				 </td>          		
			</tr>
            </table>
			<div>
			<table   class="display" id="grdMovimientos">
                <thead>
                  <tr> 
             	<th nowrap>Clave Presupuestaria SIAF</th>
										<th nowrap>Clave presupuestaria interna</th>
										<th nowrap>Cod. SIF</th>
										<th>Importe</th>
                  </tr>
                </thead>
                <tbody>
                </tbody>
                <tfoot>
                </tfoot>
              </table>
			  <table   class="display" id="grdEPS">
                  <thead>
                    <tr> 
                      <th>EP</th>
                      <th>Saldo</th>
					  <th>Concepto</th>
					  <th>Error</th>
                    </tr>
                  </thead>
                  <tbody>
                  </tbody>
                  <tfoot>
                  </tfoot>
              </table>
		    </div>
		</div>
		<div  id="tabs-4" >
	    	
			<table border="0" cellspacing="0" cellpadding="0" width="50%" >
          		<tr><td>
				   <table border="0" cellspacing="2" cellpadding="0" >
                  <tr> 
                    <td>No. factura</td>
                    <td>Fecha de factura</td>
                    <td>Tipo de beneficiario</td>
                    <td>Clave de beneficiario</td>
                    <td>Tipo de operaci&oacute;n</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td><input name="DCD_FACTURA" type="text" id="DCD_FACTURA" onkeypress="return validar(event)" class= "paso03" />                    </td>
                    <td> <input name="DCD_FECHA_FACTURA" type="text" id="DCD_FECHA_FACTURA" class= "paso03" maxlength="10"  size="10"/></td>
                    <td><input name="DCD_TBEN" type="text" class= "paso03" id="DCD_TBEN" readonly/></td>
                    <td><input name="DCD_CBEN" type="text" class= "paso03" id="DCD_CBEN" readonly/></td>
                    <td><select name="DCD_TIPO_OPE" id="DCD_TIPO_OPE" class= "paso03">
                        <option value="85" class= "paso03" >85 OTROS</option>
                      </select> </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td>Porcentaje IVA</td>
                    <td>Importe Total</td>
                    <td>IVA Desglose</td>
                    <td>IVA</td>
                    <td>ISR</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td> 
				<!--	<select id="DESCRIPCION20" class= "paso03" name="DESCRIPCION20" style="width: 10em;">
                      </select>-->
				<input name="DESCRIPCION20" type="text" class= "paso03" id="DESCRIPCION20" onKeyPress="return validar2(event)" value="0"></td>
                    <td>
                    	<input type="hidden" id="DCD_IMP_BRUTO"  name="DCD_IMP_BRUTO"/>          
						<input type="text" id="DCD_IMP_BRUTO2" class= "paso03" onkeypress="return validar2(event)" name="DCD_IMP_BRUTO2"/>          
					</td>
                    <td><input name="DCD_IVADES" type="text" id="DCD_IVADES" class= "paso03" value="0" onkeypress="return validar2(event)"/>                    
                    </td>
                    <td><input name="DCD_IVA" type="text" id="DCD_IVA" value="0" class= "paso03" onkeypress="return validar2(event)"/>                    
                    </td>
                    <td><input name="DCD_ISR" type="text" id="DCD_ISR" value="0"class= "paso03" onkeypress="return validar2(event)"/>                    
                    </td>
                    <td>&nbsp; </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td>Ret. 0.5%</td>
                    <td>Ret. 0.2%</td>
                    <td>Beneficio social</td>
                    <td>Impuesto cedular</td>
                    <td>Penalizaciones</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td><input name="DCD_MIL5" type="text" id="DCD_MIL5" class= "paso03" value="0" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_MIL2" type="text" id="DCD_MIL2" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_CONTRIBUCION" type="text" id="DCD_CONTRIBUCION" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_OTRAS_RET" type="text" id="DCD_OTRAS_RET" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_PENALIZACION" type="text" id="DCD_PENALIZACION" value="0" class= "paso03"onkeypress="return validar2(event)" /></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr>
                    <td>Sanci&oacute;n</td>
                    <td>Devoluci&oacute;n</td>
                    <td>Amort. Anticipo</td>
                    <td>Retenci&oacute;n</td>
                    <td>Neto</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td><input name="DCD_SANCION" type="text" id="DCD_SANCION" class= "paso03" value="0" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_DEVOL" type="text" id="DCD_DEVOL" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_AMORT" type="text" id="DCD_AMORT" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_RETENCION" type="text" id="DCD_RETENCION" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td><input name="DCD_NETO" type="text" id="DCD_NETO" value="0" class= "paso03" onkeypress="return validar2(event)"/></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td>Concepto:</td>
                    <td>
					  <input type="button" value="Agregar" name="Doc"  id="Doc" onclick="fnClickAddRowComp();" class="btnInterfaceBG"/>
					  <div id="divImprime" >   <img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('ContrareciboN');">Contrarecibo </div>
					  
					  </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td colspan="3"><textarea id="DCD_CONCEPTO" name="DCD_CONCEPTO" class= "paso03" style="height: 91px; width: 448px" onkeypress="return validar(event)"></textarea></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                </table>
				   </td></tr>
				<tr> 
            		<td colspan="3" align="left"> 
					</td>
          		</tr>
        </table>
		<table   class="display" id="grdFacturas">
                  <thead>
                    <tr> 
                      <th nowrap >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >Descuentos</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                    </tr>
                    <tr> 
                      <th nowrap >No. Factura</th>
                      <th align="left" >Fecha Factura</th>
                      <th align="left" >Tipo beneficiario</th>
                      <th align="left" >Cve Benef</th>
                      <th align="left" >Tipo oper</th>
                      <th align="left" >%IVA</th>
                      <th align="left" >Importe Total</th>
                      <th align="left" >IVA desglose</th>
                      <th align="left" >IVA</th>
                      <th align="left" >ISR</th>
                      <th align="left" >Ret. 0.5</th>
                      <th align="left" >Ret 0.2</th>
                      <th align="left" >Beneficio social</th>
                      <th align="left" >Impuesto cedular</th>
                      <th align="left" >Penalizaciones</th>
                    </tr>
                  </thead>
                  <tbody>
                  </tbody>
                  <tfoot>
                  </tfoot>
                </table>
		</div>

<div id="tabs-7">
	<table width="788" border="0">
		 <tr>
		    <td width="234"><table width="248" height="83" border="0">
			      <tr>
			        <td width="242"><strong>Fuentes de Financiamiento </strong></td>
			      </tr>
			      <tr>
			        <td>
						<select  id="TFONDO" name="TFONDO"  onChange="tfondos()"   class="extra">
						<option value="FF" selected="selected">Fondos Fiscales</option>
						<option value="CE" >Credito Externo </option>
						</select> 
					 </td>
			  	  </tr>
    		</table></td>
    <td width="236"><table width="232" height="78" border="0">
      <tr>
        <td colspan="2"><strong>Porcentaje de Financiamiento</strong> </td>
      </tr>
      <tr>
        <td width="103">Cr&eacute;dito Externo</td>
		  <td width="100">      
 <input type="text" id="vcInvercion"  name="vcInvercion"  value="" readonly class="paso02" size="5"/>
 % </td>
      </tr>
      <tr>
        <td>Contraparte</td>
			<td>
			  <input name="contraparte" type="text" class="paso02"  id="contraparte"  size="5"/>
% </td>
      </tr>
    </table></td>
    <td width="278"><table width="278" border="0">
      <tr>
        <td colspan="2"><strong>El contrato es considerado como: </strong></td>
      </tr>
      <tr>
        <td width="35">
		<select  id="TContrato" name="TContrato" class="paso02">
		<option value="AL">Aporte Local (AL)</option>
		<option value="FE">Financiamiento Externo</option>
		</select></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td colspan="3"><table width="778" border="0">
      <tr>
        <td colspan="2"><strong>Datos del Pr&eacute;stamo </strong></td>
      </tr>
      <tr>
        <td width="197">Prestamo</td>
		<td width="549"><select  id="Prestamo" name="Prestamo"  onChange="cambio();" class="paso02"></select>
                     <!-- <input type="hidden" id="vpres2" name="vpres"  /> -->
                      <input type="hidden" id="nPorcentajeFinanciamiento" name="nPorcentajeFinanciamiento"  /></td>
      </tr>
      <tr>
        <td>Categor&iacute;a Inversi&oacute;n </td>
		<td width="549"><select  id="CInversion"   name="CInversion"   onChange="cambio2();" class="paso02"></select>
		  <input type="hidden" id="pcInversion" name="pcInversion"  />		
		</td>
      </tr>
      <tr>
        <td>Oficio de Elegibilidad </td>
		<td width="549">
		  <input type="text" name="oficioElegibilidad" id="oficioElegibilidad" onkeypress="return validar(event)" value="FF"  class="paso02"/>
		</td>
      </tr>
      <tr>
        <td>Fecha de Oficio de Elegibilidad </td>
		<td>
			<input name="fElegibilidad"  id="fElegibilidad" value="" maxlength="10"  size="10"  class="paso02"/>
		</td>
      </tr>
    </table></td>    
  </tr>
</table>
  </div>
 	</div>
	
</div>

	<div id="dialog-form" title="Aplicación Presupuestal/Contable">	
		<div id="divAplica" >				
			<iframe id="ifAplica" src="about:blank"></iframe>
		</div>
	</div>
	
</form>
 </div>
</div>

		</body>
</html>
