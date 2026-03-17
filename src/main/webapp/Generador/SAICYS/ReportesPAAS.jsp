<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	String role="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map<String, Role> rol =usuario.getRoles();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>'ReportesPAAS'</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
  		$(document).ready(function() {
  			<%
  				Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						role =(String)r.getKey();
						roles+= r.getKey().toString()+",";
					}
  			%>
  			var roles="<%=roles%>";
		    if( roles.indexOf("ADMIN_RECMAT") >=0){
		    	$('#isAdmin').val(0);
		    }
		    querySelectPost("UnidadBusca2", "desUnidadResponsable2", {async: false });
  		});//Fin del document ready
	  	function openCSV(){
			var where;
				if($("#desUnidadResponsable2").val()!='0')
					where=" and _Unidad_RM LIKE'%25"+$("#desUnidadResponsable2").val()+"%25'";
				else
					where='';
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn="+$('[name="REPORTE"]:checked').val()
				+ "&cIdUnidadEjecutora=" + where,
				 'Procesando', 'status=1, width=500px, height=100px, left=150px');
		}
		function openXLS(ext){
			var where;
			if($("#desUnidadResponsable2").val()!='0' )
				where=" and _Unidad_RM LIKE'%25"+$("#desUnidadResponsable2").val()+"%25'";
			else
				where='';
			var myWindow=window.open("../../servlet/ReportesGRM?"
				+"nTipoReporte=1"
				+"&nTipoIngreso=1"
				+"&operacion=2"
				+"&fechaInicio=''"
				+"&fechaFin=''"
				+"&cEjercicioActual=2020"
				+"&reporteNombre=PlantillaPASOP.xlsx"
				+"&formato="+ext
				+"&where=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function openARCH(ext){
			if($('[name="REPORTE"]:checked').val()=="rpt_CompraNET.jasper" && ext=="xls"){
				openXLS(ext);
			}else{
				$("#cAccion").val("IMPRIME_REPORTESPAAS");
				$("#cIdDocumento").val($("#ue_usuario").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
				var where;
				if($("#desUnidadResponsable2").val()!='0' )//&& ext!="rpt_CompraNET.jasper"
					where=" and _Unidad_RM ='"+$("#desUnidadResponsable2").val()+"'";
				else
					where='';
				$("#rn").val($('[name="REPORTE"]:checked').val());
				$("#formato").val(ext);
				$("#cIdUnidadEjecutora").val(where);
				var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
					+"catalogo=REPORTE"
					+"&accion=run"
					+"&rn="+$('[name="REPORTE"]:checked').val()
					+"&formato="+ext
					+"&cIdUnidadEjecutora=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');	
			}
			
		}
	</script>
  </head>
  
  <body>
    <form>
    	<div>
    		<fieldset>
    		<table  align="left">
				<tr> 
					<td align="left" colspan="4"> 
						<span>Unidad Ejecutora: </span>
						<select id="desUnidadResponsable2" name="desUnidadResponsable2" style="width: 30em;"> 
							<option value="<%=usuario.getU_UR()%>" selected="selected"> 
							</option>
						</select> 
					</td> 
				</tr> 
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_mProgramaAnualPorPartidayTrimestre.jasper">Programa Anual Resumido Por Partida y Trimestre</td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="fn_mProgramaAnualMontosPorCapituloReporte.jasper">Programa Anual de Adquisiciones Resumido Por Unidad y Capitulo </td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas.jasper">Programa Anual de Adquisiciones Resumido Por Unidad y Capitulo Restando Partidas </td></tr> 
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_CompraNET.jasper" CHECKED>Programa Anual Ordenado Por Partida Compranet </td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados.jasper">Programa Anual de Adquisiciones CUCOPS sin documentos asociados </td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_programaAnualResumidoPorCapitulosPartidasNacionalOarea.jasper">Programa Anual resumido Partida y Capitulos</td></tr>				
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="fn_mProgramaAnualConciliacionPresupuestal.jasper">Programa Anual Conciliacion Presupuestal</td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="mConsiliacion_RC_Apartado_Precompromiso.jasper">Conciliacion Presupuestal RC VS Precomprometido</td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="mConsiliacion_AP_PRE_Comp_Eje.jasper">Conciliacion Presupuestal APARTADO VS PAGADO</td></tr>
				<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="mCatalogoInventarioArticulosdeAlmacen.jasper">Inventario de Articulos en Almacenes</td></tr>
				<tr><td align="center">
					<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"		onclick="openARCH('pdf');">&nbsp;&nbsp;
					<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdxlsReporteProg" 		name="cmdxlsReporteProg" 	value="Excel"	onclick="openARCH('xls');">&nbsp;&nbsp;
					<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="CSV"		onclick="openCSV();">&nbsp;&nbsp;
					<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdwordReporteProg" 	name="cmdwordReporteProg" 	value="Word"	onclick="openARCH('doc');">&nbsp;&nbsp;
				</td></tr> 
			</table>
			</fieldset>
    	</div>
    	<input name="ue_usuario" id="ue_usuario" value="<%=usuario.getU_UR()%>" type="hidden"/>
    </form>
  </body>
</html>
