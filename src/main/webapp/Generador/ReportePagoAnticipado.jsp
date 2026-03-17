<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%
//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
} 

//final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();


String mensaje = "";
if (request.getParameter("msg") != null
		&& !"".equals(request.getParameter("msg"))) {
	mensaje = request.getParameter("msg");
	mensaje = mensaje.replace("[", "");
	mensaje = mensaje.replace("]", "");
	mensaje = mensaje.replace(",", "<br>");
}



/*int id_oper = -1;
if (request.getParameter("id_oper") != null)
	id_oper = new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper = c.getCasoOperacion(0).getIdOperacion();*/

Empleado e = new Empleado();
String cEjercicio;
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);


EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);
String UR = usuario.getU_UR();

String sDescripcion =ea.getDescripcion()  ;
String sNombre=e.getNombreCompleto()  ;
String sCargo= e.getCargo() ;


AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
cEjercicio = adecProy.obtenEjercicioFiscal();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title></title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<link type="text/css" rel="stylesheet" href="../css/themes/base/jquery.ui.all.css" />
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />

		<style type="text/css" title="currentStyle">
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		
		
		<style type="text/css">
			.calendar_1 {					
			background-color: #F5F5f5;
			padding-left: 6px;
			}
			.calendar_2 {				
				text-align: right;
				background-color: #E2E4FF;
				padding-left: 6px;
				padding-right: 6px;
			}
			
			.monto {
					text-align: right;
					border: 1px solid #aaaaaa;
					color: #222222;
					}
		</style>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/ui/jquery.ui.accordion.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

	<script type="text/javascript" charset="utf-8">
		
	$(document).ready(function() {
		Unormativa();
		Cargas();
     });
	



	function Unormativa(){
			$("#Unormativa").val('<%=UR%>');
			$("#ParametroNormativa").val('<%=UR%>');
			
			querySelectPost("catAreaContableReporteComsoc", "Select_UEjecutora", {async : false});
			
			 $("#Select_UEjecutora").val('<%=UR%>');
			 $("#Select_UEjecutora").attr("disabled",true);
		    
	}		

	
	function Cargas(){ 
	
	              $("#nClave").val(1);
	              // Cargamos encabezados
	              querySelectPost("readReportePagoAnticipadoEncabezado","Select_DirigidoA", {async : false});
	              $("#Select_DirigidoA").val($("#nClave").val());
	 			  queryFormPost("readReportePagoAnticipadoDetalle", {async : false});
	 			   
	 			  
	}
	
	
	function Valida(){
	
	  if ($.trim($("#ParametroNormativa").val()) == 0){
	   alert("Seleccione la Unidad  normativa");
	    return false;
	   }
      if ($.trim($("#txtNoFolio").val()) == '')   {
	   alert("Indique el número de folio del Pago");
	   $("#txtNoFolio").focus();
	    return false;
	   }
	   
	  if ($("#txtNoFolio").val() < 0)    {
	   alert("El folio debe ser mayor a cero");
	    $("#txtNoFolio").focus();
	    return false;
	   }
	   
	  	   
	  
	  if ($.trim($("#txtOficio").val() ) == ''){
	   alert("Indique el número de Oficio del Reporte");
	   ("#txtOficio").focus();
	   return false;
	   }
	  
	  if ($.trim($("#cRemitente").val()) == ''){
	   alert("Indique el Nombre del remitente");
	   $("#cRemitente").focus();
	    return false;
	   }
	   
	    if ($.trim($("#cDestinatario").val()) == ''){
	    alert("Indique el Nombre del Destinatario");
	    $("#cDestinatario").focus();
	    return false;
	   }
	   
	    if ($.trim($("#cPuestoRemitente").val()) == ''){
	   alert("Indique el Puesto del Remitente");
	   $("#cPuestoRemitente").focus();
	   return false;
	   }
	    
	     if ($.trim($("#cPuestoDestinatario").val()) == ''){ 
	   alert("Indique el Puesto del Destinatario");
	   $("#cPuestoDestinatario").focus();
	   return;
	   } 
	  
	   $("#nFolioPagoAnticipado").val($("#txtNoFolio").val()); 
	 // alert($("#nFolioPagoAnticipado").val());
	
	//Validemos que exista el pago
	   queryFormPost({
					queryName : "readExistePagoAnticipado",
					async : false,
					callback : function() {
											if ($("#existe").val() != "1"){	
	    										alert("No se encontró el Folio del Pago Anticipado");
	  											 return;
											}
	  
	    								   }
	    			});
	    			
	 //Guardamos los datos de remitente y destintario   			 
	  queryFormPost("upagoAnticipadoReporte", {async : false});   			
	    
	  
	return true;  
	}   
	   
	   
	   
	   
	   
	   
	
 
    function Imprimir(){
         if ( Valida()){
         
             		
         		
         			window.open("../admin/SeguridadCatalogos?"
														 + "catalogo=REPORTE"
														 + "&accion=run"
													 	 + "&rn=\ReportePagoAnticipado.jasper"
													     + "&nFolio="    	+ $("#nFolioPagoAnticipado").val()+ 
													       "&NoOficio="  	+ $("#txtOficio").val()+
													       "&Unormativa="	+ $("#ParametroNormativa").val()+
													       "&noEncabezado="	+ $("#Select_DirigidoA").val(),
													       "popacuse",
													       "scrollbars=1, resizable=yes, width=200, height=200"
								    		              );
         
         
         }
	} 
	
	function fn_CambiarDestnatario(){
		$("#nClave").val($("#Select_DirigidoA").val());
	   	queryFormPost("readReportePagoAnticipadoDetalle", {async : false});
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
   	 	 
	
	</script>

	</head>
	
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form action="" method="post" name="frm" id="frm">
	
	    <input type="hidden" Id="nPartida" name="nPartida">
	    <input type="hidden" Id="Unormativa" name="Unormativa">
	    <input type="hidden" Id="nTotaldeRegistros" name="nTotaldeRegistros">
	    <input type="hidden" Id="hUR" name="hUR">
	    <input type="hidden" Id="hPartida" name="hPartida">
	    <input type="hidden" Id="hMes" name="hMes">
	    <input type="hidden" Id="hMes_Ant" name="hMes_Ant">
	    <input type="hidden" Id="hAnio" name="hAnio">
        <input type="hidden" Id="hMedio" name="hMedio">
	    <input type="hidden" Id="ParametroNormativa" name="ParametroNormativa">
	    <input type="hidden" name="nFolioPagoAnticipado" id="nFolioPagoAnticipado">
	    <input type="hidden" value="" id="existe" name="existe">
	    <input type="hidden" value="" id="nClave" name="nClave">
	    
	            
		<div id="container" class="container SyCData">
			
			<h1 >
				Reportes de Pago Anticipado
			</h1>
			 <fieldset>
				<legend>Seleccione </legend>
		
				<table id="Docto" width="30%" align ="center" border="0" >
				    <tr>
						<td align="left">Unidad Ejecutora: </td> 
					</tr>	
				    <tr> 	
						<td align="left"><select  style="width: 300px" id="Select_UEjecutora"
							name="Select_UEjecutora">
						   	</select>
						</td>
					</tr>
					<tr> 
						<td align="left">Folio: </td>
						<td align="left">no. de Oficio del Reporte: </td>
					</tr>
					<tr>
				    	<td align="left"><input type="text" id="txtNoFolio" name="txtNoFolio"  onKeyPress="return soloNumeros(event)" >
						</td>
						<td align="left"><input type="text" id="txtOficio" name="txtOficio">
						</td>
					</tr>
					<br>
					<tr> 
						<td align="left">dirigido A: </td>
						<td align="left"><select  style="width: 300px" id="Select_DirigidoA" name="Select_DirigidoA" onChange="fn_CambiarDestnatario()">
						   	</select>
						</td>
										
					<tr>	 
					<tr> 
						<td align="left">Nombre de quien Remite: </td><td align="left">Nombre del Destinatario: </td>  
				     	
					</tr>
					<tr>
						<td align="left"><textarea id="cRemitente" name="cRemitente" rows="2"  cols="40"></textarea></td>
					    <td align="left"><textarea id="cDestinatario" name="cDestinatario" rows="2"  cols="40"></textarea></td>
				
					
					</tr>
					<tr> 
						<td align="left">Puesto Remitente: </td><td align="left">Puesto Destinatario: </td>  
				     	
					</tr>
					<tr>
				    		<td align="left"><textarea id="cPuestoRemitente" name="cPuestoRemitente" rows="2"  cols="40"></textarea></td>
							<td align="left"><textarea id="cPuestoDestinatario" name="cPuestoDestinatario" rows="2"  cols="40"></textarea></td>
						
							
					</tr>
					<tr>
						
						<td align="right" >
					    <input type="button" id="btnImprimir" name ="btnImprimir" value=" Imprimir " onclick="javascript:Imprimir();" >
					    </td> 
				
				   </tr>
				  
				   
				
				</table>
		</fieldset>
		</div>
	
		</form>
	</body>
</html>
