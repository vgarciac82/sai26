<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	int cLayout=0;
	String cNameFile = "";
	String cMensaje="";
	String mensajeVAdec="";
	boolean bCargaDT=true;
	boolean brechazo = false;

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if(usuario==null){
		response.sendRedirect("../index.jsp");
	}

	String mensaje = "";
/*	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");*/
		if (session.getAttribute("msg") != null
			&& !"".equals(session.getAttribute("msg"))) {
		mensaje = (String)session.getAttribute("msg");
		session.removeAttribute("msg");
		//mensaje = mensaje.replace("[", "");
		//mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "\\n");
	}
	
	int actual=0;
	if(request.getParameter("pes")!=null){
			actual = Integer.parseInt(request.getParameter("pes"));
	}
	if (request.getParameter("cLayout")!= null){
		cLayout= Integer.valueOf(request.getParameter("cLayout"));
		System.out.println("cLayout="+cLayout);
	}
	
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Importación PAOP</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle"> 
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>

		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker-es.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

		<script type="text/javascript" charset="utf-8">

		var oTableUN;
		
		$(document).ready(function(){
		var muestraRes = false;
		if ('<%=mensaje%>'!= ''){
		 	$("#resProceso").val('<%=mensaje%>');
		 	muestraRes = true;
		}
		
				$('.currency').blur(function(){
					$('.currency').formatCurrency();
				});
	
				$("input.AyudaSyC").subIniciaDlg();			
			
				var tabs = $("#tabs").tabs({
					"show": function(event, ui) {
						var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
						if ( oTable.length > 0 ) {
							oTable.fnAdjustColumnSizing();
						}
					}
				});
					
				var $dialog;
			    $(function() {		
		    	    $('#dialogProceso').dialog({
		        	    autoOpen: muestraRes,
		            	width: 1000,
		            	heigth: 3000
    		    	});
    			});
		//	    carga();
			    
			    tabs.tabs('select', "<%=actual%>"); 
			    
			});  //fin del ready

//---------------------------------------------------------------------
	function onSubmit(id_oper){//validaciones del boton guardar
			var p = window.parent;
			var valida_campos = true;
			try{
				//Control de estado de botones
				guardaExp();
			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

 		function onPostSubmit(id_oper){//validaciones del boton enviar
	  		return true;
		}
		
		function onLoadPlantilla(){
				
		}		
		function onPostDisplay(){
		}
		
		function fnSubeArchivo(){	
			if (($("#archivoEUN").val()!="") ) {
				var cFileName=document.getElementById("archivoEUN").value;
				var output = [];
			    output=cFileName.split("\\");
	  			var cNameFile = output[output.length-1];//cFileName.substring(12,cFileName.length);
	  			document.subeArchivoUN.action="../gstnmngr/ImportaLayoutServlet?cLayout=<%=cLayout%>"+"&cNameFile="+cNameFile;
	  			//alert("../gstnmngr/ImportaLayoutServlet?cLayout=<%=cLayout%>"+"&cNameFile="+cNameFile);
	  			$.blockUI({message: "Procesando Alta espere ......"});
				document.subeArchivoUN.submit();
			}
			else
				alert("archivo requerido");
		}

	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
		<form id="ImportaExcel" name="ImportaExcel" action="creacionAnteProyecto.jsp?cImporXLS=Si" method="post" ></form>
		<form id="ExportaExcel" name="ExportaExcel" action="../gstnmngr/AnteProyectoLayoutServlet" method="post" ></form>
			<h1>Importación PAOP</h1>
			
				<input type="hidden" value="" id="rowsAffected" name="rowsAffected">
				<input type="hidden" id="cRamo" name="cRamo" value="16">
				<input type="hidden" id="cCentroContable" name="cCentroContable">
				<input type="hidden" id="id_oper" value="">
				<input name="cUnidadEjecutora" type="hidden" id="cUnidadEjecutora" value="A02">
				<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="2012"/>
				<input type="hidden" id="cLayout" name="cLayout" value="<%=cLayout%>"/>
				
				<div id="multitabs" style="width: 100%">
					<div id="tabs" style="width: 90%">
						<ul>
							<li> <a id="LNK01" href="#tabs-5" class="LNK01">.</a> </li>
						</ul>
						<div id="tabs-5">
							
							<table border="0" cellspacing="0" cellpadding="0" style="width: 550px">
								
								<tr>
									<td></td><td>
										<form id="subeArchivoUN" name="subeArchivoUN" method="post" action="../gstnmngr/ImportaLayoutServlet?cLayout=<%=cLayout%>" enctype="multipart/form-data">
											<input type="file" id="archivoEUN" name="archivoEUN" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="5"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo();"/>
										</form>
		
									</td>
								</tr>

							</table>

						</div>
					</div>
				</div>
		
		</div>
	<div id="dialogProceso" title="Detalle de Proceso">
		<textarea id="resProceso" name="resProceso" rows="13" cols="130"></textarea>
	</div>


	</body>
</html>
