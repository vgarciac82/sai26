<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page	import=" org.apache.commons.fileupload.FileItem"%>
<%
String menssage,accion;
String registro;
	//HttpSession sesion= request.getSession();
	Usuario usuarioNR = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioNR == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	//if(menssage==null)
	 	menssage=request.getParameter("msg");
		accion=request.getParameter("accion");
		registro=request.getParameter("registro");
	//System.out.println("menssage*********"+menssage);
	
	String choice=request.getParameter("BienesServicios");
	System.out.println("accion : "+accion);
	//session.setAttribute("url", choice);
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
	
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>

	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>

	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			//cerrardiv("form");
			$("#sub").button();
			$("#guardar").button();
			<% if(registro!=null) {%>
				alert("No se encuentra el registro asociado. Debe crear la licitación.");
			<%}%>
			<% if(accion!=null) {%>
				alert("Archivo Cargado ");
			<%}%>
			<%if(menssage!=null) {%>
				//alert("***********");
				if (!window.confirm("El Archivo ya Existe. ¿Desea Remplazarlo?")){
					<% menssage=null;%>
					return;
				
				}
				//Aqui entra cuando le das aceptar
				else{
					
					$.ajax({url: '../../servlet/CatalogoLPCE' , type:'post' , async: false,data:'operacion=4', dataType: 'json'});
					alert("Archivo Cargado ");
					
				}
		
			<%}%>
			<% if(choice!=null) {%>
				if(document.getElementById("Servicios").value=='<%= choice %>'){
					document.getElementById("Servicios").checked=true
				}
				else
					document.getElementById("Bienes").checked=true
						
						
			
			<%}%>
			
		});
		function choiceChecked(){
			alert("Va seleccionar un choice")
		} 
		function enviar(){
				document.datos.submit();
		}
		
		function cerrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display='none';
		}
		
		function mostrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display ='block';
		}
		function MostrarForm(){
			cerrardiv("tabla");
			mostrardiv("form");
		}
		function btnGuardar(){
			//Obtiene el maximo
			if($("#cDescripcion").val()!=""){
				
				//alert(var);
				queryFormPost("obtieneMaximoLPCE",{async:false});
				if ($("#maximo").val() != '') { 
					$("#nIdLicitacion").val(parseInt($("#maximo").val(),10) + 1);
				}
				else {
					$("#nIdLicitacion").val('1');
				}
				
				//obtiene la ruta para Servicios
				$("#cParametro").val('Servicios');
				$("#cConvocaServicios").val("<a href=../../docs/lpce/servicios/"+$("#nIdLicitacion").val()+".doc "+"style=\"color: grey\" target=\"_blank\"/>Modelo de Licitación</a>");
				
				// obtiene la ruta Para bienes
				$("#cParametro").val('Bienes');
				$("#cConvocaBienes").val("<a href=../../docs/lpce/bienes/"+$("#nIdLicitacion").val()+".doc "+"style=\"color: grey\" target=\"_blank\"/>Modelo de Licitación</a>");
				
				queryFormPost("mCatalogoLPCECreate",{async:false});
				
				alert("Datos Guardados");
				$("#cDescripcion").val('');
				window.location = "mCatalogoLicitacionPublicaCreditoExterno.jsp?tab=" + 0;
				
			}
			else{
				alert("Debe especificar la descripción de la licitación a guardar.");
			}
		}
		
		function btnCargar(){
			if($("#uploadfile").val()!="" ){  
				if(<%= choice!=null %>) {
					//alert("Yaaaaaaa");
					document.upform.submit();
				}
				else
					alert("Debe seleccionar el tipo de Licitación: Bienes o Servicios.");
			}
			else
				alert("Debe seleccionar el archivo para cargar.");
			
		}
	</script>
	

  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<div  id="tabla" align="left">	
		<h1 align="left">Cargar Archivo al Servidor </h1>
		 <form name="datos" id="datos" method="post" action="mCatalogoLicitacionPublicaCreditoExterno.jsp?tab=1">
						
			<input type="hidden" id="nIdParametro" name="nIdParametro" size="10" value=""><!-- Id para la busqueda en mi query -->
			<input type="hidden" id="cValor" name="cValor" size="10" value="">
			<input type="radio" name="BienesServicios" id="Servicios" value="Servicios" onClick="enviar();"> Servicios 
			<input type="radio" name="BienesServicios" id="Bienes" value="Bienes" onClick="enviar();" >Bienes	
		</form>
		
		<form name="upform" id = "upform" action="../../servlet/CatalogoLPCE?choice=<%=choice %>"  enctype = "multipart/form-data" method = "post" >
		  		<table borde="">
		  			
		  			<tr>
		  				<td>
							Archivo:
							<input type="file" name="uploadfile" id = "uploadfile"  value="" >
						</td>
					</tr>
					<tr>
						<td><input type="hidden" id="nIdParametro" name="nIdParametro" size="10" value=""></td><!-- Id para la busqueda en mi query -->
						
						
					</tr>
					
					<tr>
						<td><input type="button"  value="Cargar Archivo" name="sub" id="sub" onclick="btnCargar();" class="btnInterfaceBG"/></td>
					</tr>
				</table>
		</form>
		<br />
	</div>
	<div id="form" align="left">
		<h1 align="left">Crear nuevo Modelo de Licitación Pública con Crédito Externo </h1>
		<form  name="sitema" id="sistema">
			Descripción:<input type="text" name="cDescripcion" id="cDescripcion" value="" size="45" maxlength="150" />
			
			<input type="hidden" name="cConvocaBienes" id="cConvocaBienes" value=""  />
			<input type="hidden" name="cConvocaServicios" id="cConvocaServicios" value=""  />
			<input type="hidden" name="maximo" id="maximo" value=""  />
			<input type="hidden" name="nIdLicitacion" id="nIdLicitacion" value=""  />
			<input type="hidden" name="cParametro" id="cParametro" value=""  />
			<input type="hidden" id="cValor" name="cValor" size="10" value=""/>
			<input type="button"  value="Guardar" name="guardar" id="guardar"  onclick="btnGuardar();" class="btnInterfaceBG"/>
		</form>
	</div>
  </body>
</html>
