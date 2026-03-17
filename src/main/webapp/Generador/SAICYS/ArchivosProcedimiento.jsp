<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	String mensaje = request.getParameter("cMensaje");
	String tipoArchivo = request.getParameter("cTipoArchivo");
	String cIdProcedimiento[] = request.getParameter("cIdProcedimientoArchivo").toString().split("-");
	String cIdConsolidado[] = request.getParameter("cIdConsolidadoArchivo").toString().toString().split("-");
	//CONSOLIDADO
	session.setAttribute(GestionInterface.ATT_ConTipoConsolidado, cIdConsolidado[0]);
	session.setAttribute(GestionInterface.ATT_ConUnidadEjec, cIdConsolidado[1]);
	session.setAttribute(GestionInterface.ATT_ConConsecutivo, cIdConsolidado[2]);
	//EJERCICIO
	session.setAttribute(GestionInterface.ATT_ProEjercicio, request.getParameter("cEjercicioArchivo"));
	//PROCEDIMIENTO
	session.setAttribute(GestionInterface.ATT_ProTipoProcedimiento, cIdProcedimiento[0]);
	session.setAttribute(GestionInterface.ATT_ProUnidadEjecutora, cIdProcedimiento[1]);
    session.setAttribute(GestionInterface.ATT_ProConsecutivo, cIdProcedimiento[2]);
    //TIPO ARCHIVO
    session.setAttribute(GestionInterface.ATT_ProcTipoArchivo,tipoArchivo);
    
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTablesSAICYSPA.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		function validaCargarArchivo(){
			var nomTem = document.getElementById("cargaArchivo").value;
			if(nomTem == ""){
				alert("No se cargo ningun archivo.");
			}
			else{
				if(nomTem.substring(nomTem.length-4,nomTem.length-1).replace(" ","").toUpperCase() == "DOC" || nomTem.substring(nomTem.length-5,nomTem.length-1).replace(" ","").toUpperCase() == "DOCX"){
					document.form_cargarArchivo.submit();
				}
				else{
					alert("El archivo debe ser un documento de Word.")
				}
			}
		}
		function validaGenerarArchivo(){
			var tipoArchivo = "<%=tipoArchivo%>";
			if(tipoArchivo.toString() == "falloProcedimiento"){
				exportFallo();
			}
			if(tipoArchivo.toString() == "aperturaProcedimiento"){
				exportAPTE();
			}
		}
		function exportAPTE(){
			var cIdProc = "<%=cIdProcedimiento[0]+'-'+cIdProcedimiento[1]+'-'+cIdProcedimiento[2]%>";
			var cIdCons = "<%=cIdConsolidado[0]+'-'+cIdConsolidado[1]+'-'+cIdConsolidado[2]%>";
			var servletPath = "../../servlet/SeguridadCatalogosMateriales?" + "rn=rptActaAPTE.jasper" +"&formato=dsdoc" + "&cIdProcedimiento=" + cIdProc + "&cIdConsolidado=" + cIdCons;
			window.open(servletPath, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		function exportFallo(){
			var cIdProc = "<%=cIdProcedimiento[0]+"-"+cIdProcedimiento[1]+"-"+cIdProcedimiento[2]%>";
			var cIdCons = "<%=cIdConsolidado[0]+'-'+cIdConsolidado[1]+'-'+cIdConsolidado[2]%>";
			var servletPath = "../../servlet/SeguridadCatalogosMateriales?" + "rn=rptActaFallo.jasper" +"&formato=dsdoc" + "&cIdProcedimiento=" + cIdProc + "&cIdConsolidado=" + cIdCons;
			window.open(servletPath, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
	</script>
  </head>
<body id="dt_example" bottomMargin="0" leftmargin="0" topmargin="0" >
  	<div  id="tabla" align="left">
		<form name="form_cargarArchivo" id = "form_cargarArchivo" action="../../servlet/CargaArchivosProcedimiento"  enctype="multipart/form-data" method="post" >
			<table >
		  		<tr>
		  			<td style="width:100px;">
						&nbsp;
					</td>
					<td>
						<input type="file" name="cargaArchivo" id="cargaArchivo"  value="" >
					</td>
				</tr>
			</table>
			<div id="divMensaje" style="font-family: 'Arial', serif; font-size: 10pt; color:#FF0000;">
			</div>
			<script>
				document.getElementById("divMensaje").innerHTML="<%=mensaje%>".replace("'","");
			</script>
		</form>
		<form name="form_descargarArchivo" id="form_descargarArchivo" action="../../servlet/DescargarArchivosProcedimiento" method="post" >
		</form>
		<br>
		<table>
			<tr>
				<td>
					<button id="btnCargarArchivoProcedimiento" onClick="validaCargarArchivo();">CARGAR</button>
				</td>
				<td>
					<button id="btnGenerarArchivoProcedimiento" onClick="validaGenerarArchivo();">GENERAR</button>
				</td>
				<td>
					<button id="btnDescargarArchivoProcedimiento" onClick="document.form_descargarArchivo.submit();">DESCARGAR</button>
				</td>
			</tr>
		</table>				
		<br/>
	</div>
  </body>
</html>
