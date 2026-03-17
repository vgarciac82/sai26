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
	
	var d = new Date();
    var month = d.getMonth()+1;
    $("#Select_Mes").val(String(month));
    
	

		
    
     });
	
		
	function Cargas(){
	
	querySelectPost("PartidasComsocReporte", "Select_Partida",{async : false});
	querySelectPost("ConsultatCatalogoMediosDifusion", "selectMedios",{async : false});	
	$("#selectMedios").val('100');
		
	}
	
	
	function Unormativa(){
	$("#Unormativa").val('<%=UR%>');
	$("#ParametroNormativa").val('<%=UR%>');
	
	querySelectPost("catAreaContableReporteComsoc", "Select_UEjecutora", {async : false});
	
	if ('<%=UR%>' != 'A02'){ 
	 $("#Select_UEjecutora").val('<%=UR%>');
	 $("#Select_UEjecutora").attr("disabled",true);
	}
	
	
}



 function fn_Select(Valor_Selccionado){
	    $("#NomReporte").val(Valor_Selccionado);
	  
 }
	 
	 
  function Imprimir () {
  
      $("#nTotaldeRegistros").val(0); 
     if($("#RadioTipoReporte1").is(':checked') == false && $("#RadioTipoReporte2").is(':checked') == false && $("#RadioTipoReporte3").is(':checked') == false ){
       alert("Seleccione el Tipo de reporte");
       return;
     
     }
     
  
     var month=$("#Select_Mes").val();
	 var nMes_ant = month -1 ;
     $("#nPartida").val($("#Select_Partida").val());
     
     
      //variables de busqueda
      $("#hPartida").val($("#nPartida").val());
      $("#hUR").val($("#Select_UEjecutora").val());
      $("#hMes").val(month);
      $("#hMes_Ant").val(nMes_ant);
      $("#hAnio").val(<%=cEjercicio%>);
      $("#hMedio").val($("#selectMedios").val());
      
      
    
   		if ($("#NomReporte").val() == 'Analitico'){
             //Verificamos que existan registros
  			 queryFormPost("ExistenDatosReporteAnalitico", {async : false	});
   	         //la salida se registra nTotaldeRegistros
    		if ($("#nTotaldeRegistros").val() >0){
      
								            	window.open("../admin/SeguridadCatalogos?xls=SI"
														 + "&catalogo=REPORTE"
														 + "&accion=run"
													 	 + "&rn=\ReporteComsoc.jasper"
													     + "&nPartida="+  $("#nPartida").val()+ "&Unormativa="+ $("#Select_UEjecutora").val()+"&mes="+ month ,
													       "popacuse",
													       "scrollbars=1, resizable=yes, width=200, height=200"
								    		              );
    	    }
	    	else
	    	{
	    	  alert("No Existen Registros que cumplan Con el criterio de Busqueda");
	          return ;
	       }
   }
   else if ($("#NomReporte").val() == 'Consolidado')  {
       
         queryFormPost("ExistenDatosReporteConsolidado", {async : false	});
   	          //la salida se registra nTotaldeRegistros
    		if ($("#nTotaldeRegistros").val() >0){
   										window.open("../admin/SeguridadCatalogos?xls=SI"
													+ "&catalogo=REPORTE" 
													+ "&accion=run"
													+ "&rn=\ReporteConsolidadoComsoc.jasper"
												    + "&UNormtiva="+ $("#Select_UEjecutora").val()+"&nPartida="+  $("#nPartida").val()+"&anio=" + $("#hAnio").val()+"&mes="+ month + "&mes_ant="+ nMes_ant ,
													"popacuse",  
													"scrollbars=1, resizable=yes, width=200, height=200"  
	  			 									);
	       }
	  else
	       {
	    	  alert("No Existen Registros que cumplan Con el criterio de Busqueda");
	          return ;
	       }
	       
   }
   else if ($("#NomReporte").val() == 'ConsolidadoxMedioDifusion')  {
       
         queryFormPost("ExistenDatosReporteConsolidadoxMedios", {async : false	});
   	          //la salida se registra nTotaldeRegistros
    		if ($("#nTotaldeRegistros").val() >0){
   										window.open("../admin/SeguridadCatalogos?xls=SI"
													+ "&catalogo=REPORTE" 
													+ "&accion=run"
													+ "&rn=\ReporteConsolidadoComsocxMediosDifusion.jasper"
												    + "&UNormtiva="+ $("#Select_UEjecutora").val()+"&nPartida="+  $("#nPartida").val()+"&anio=" + $("#hAnio").val()+"&mes="+ month + "&mes_ant="+ nMes_ant +"&nMedio="+ $("#selectMedios").val(),
													"popacuse",  
													"scrollbars=1, resizable=yes, width=200, height=200"  
	  			 									);
	       }
	  else
	       {
	    	  alert("No Existen Registros que cumplan Con el criterio de Busqueda");
	          return ;
	       }
	       
   }
   
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
	       <input type="hidden" Id="NomReporte" name="NomReporte">        
		<div id="container" class="container SyCData">
			<h1 >
				Reportes de Comunicación Social
			</h1>
			<div id="busqueda" style="display: block;">
				<table id="reAplica_Docto" width="30%" align ="center" border="0" >
				    <tr>
						<td align="right">Unidad Ejecutora: </td> 
				     	
						<td align="left"><select  style="width: 300px" id="Select_UEjecutora"
							name="Select_UEjecutora">
						   	</select>
						</td>
					</tr>
					<tr> 
						<td align="right">Partida: </td> 
				     	
						<td align="left"><select  style="width:100px" id="Select_Partida"
							name="Select_Partida">
						   	</select>
						</td>
					</tr>
					
					<tr>
						<td align="right">Mes: </td> 
				     	
						<td align="left"><select  style="width:100px" id="Select_Mes" 
							name="Select_Mes">
							<option value="1">Enero</option>
  							<option value="2">Febrero</option> 
 							<option value="3">Marzo</option>
  							<option value="4">Abril</option>
  							<option value="5">Mayo</option>
  							<option value="6">Junio</option>
  							<option value="7">Julio</option>
  							<option value="8">Agosto</option>
  							<option value="9">Septiembre</option>
  							<option value="10">Octubre</option>
  							<option value="11">Noviembre</option>
  							<option value="12">Diciembre</option>
  					   	</select>
						</td>
					</tr>
					<tr>
								<td width="15%">Seleccione:</td>
								<td><input type="radio" name="RadioTipoReporte1" id="RadioTipoReporte1" value="1"  Onclick="fn_Select('Analitico')">Reporte Analítico</td>
				    </tr>
					<tr>    	<td></td><td><input type="radio" name="RadioTipoReporte2" id="RadioTipoReporte2" value="2"  Onclick="fn_Select('Consolidado')">Reporte Consolidado</td>
					</tr>
					<tr>		<td></td><td><input type="radio" name="RadioTipoReporte3" id="RadioTipoReporte3" value="3"  Onclick="fn_Select('ConsolidadoxMedioDifusion')">Reporte Consolidado Agrupado por Medios de Difusion</td>
					
						<td> <select style="width: 300px"  id="selectMedios" name="selectMedios"></select></td>
					</tr>
					<tr>
						<td></td>	
						<td align="center" >
					    <input type="button" id="btnImprimir" name ="btnImprimir" value=" Imprimir " onclick="javascript:Imprimir();" ></td> 
					 	<!-- <input type="button" id="Limpiar" name ="Limpiar" value="Limpiar" onclick="limpiarSesion();"></input>-->
						</td>
				   </tr>
				
				</table>
			</div>
			
			
		</div>
	</body>
</html>
