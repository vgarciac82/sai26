<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Programa Anual Cap&iacute;tulo Mil</title>
	<meta http-equiv="pragma" content="no-cache" > 
		<meta http-equiv="cache-control" content="no-cache" > 
		<meta http-equiv="expires" content="0" > 
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" > 
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" > 
		<meta http-equiv="description" content="Cat?logo de Beneficiarios" > 
 
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" /> 
		 		
		<style type="text/css" title="currentStyle"> 
			@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../css/demo_table_jui.css"; 
			@import "../css/demo_page.css"; 
			@import "../css/demo_table.css"; 
			@import "../../css/interfaz.css";
		</style> 
 
		<!-- Estilos de dialogo -->
		<style> 
			estilos del dialogo 
						div#dialog-form fieldset { 
				padding: 0; 
				border: 0; 
				margin-top: 25px; 
			} 
 			div#users-contain { 
				width: 350px; 
				margin: 20px 0; 
			} 
			div#users-contain table { 
				margin: 1em 0; 
				border-collapse: collapse; 
				width: 100%; 
			} 
			div#users-contain table td,div#users-contain table th { 
				border: 1px solid #eee; 
				padding: .6em 10px; 
				text-align: left; 
			} 
			.ui-dialog .ui-state-error { 
				padding: .3em; 
			} 
			.validateTips { 
				border: 1px solid transparent; 
				padding: 0.3em; 
			} 
		</style> 
 		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
  		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script> 
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script> 
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script> 
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script> 
		<script type="text/javascript" src="../js/jquery.ui.core.js"></script> 
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script> 
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script> 
		<script type="text/javascript" src="../js/crud.js"></script> 
		<script type="text/javascript" src="../../js/catalogo/general.js"></script> 
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
		<script type="text/javascript" src="../../js/utils/syctools.js"></script>
		<script type="text/javascript" src="../js/funciones.js"></script>
		<script type="text/javascript" src="../js/funcionesPAASCapMil.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script> 
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
		<script type="text/javascript" src="../../js/utils/syctools.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		
	<script type="text/javascript" charset="utf-8">
  		$(document).ready(function() {
  			$(".tabs").tabs();
	  		
	  		$("#ConsultaPAAS").click(function() {
	  			$("#tbs").val(0);
				window.location = "ProgramaAnualCap1000.jsp?tab=" + 0;
			});
			if($("#tbs").val()!=1){
				$("#CalendarioPAAS" ).hide();
			}
			$(window).bind('resize', function (){
				resizeDt();
			});
  		
  		});
  		function checkShortcut()
		{			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
		function getUrlParameter(param) {
			param = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
			var r1 = "[\\?&]"+param+"=([^&#]*)";
			var r2 = new RegExp(r1);
			var r3 = r2.exec(window.location.href);
			if (r3 == null)
				return "";
			else
				return r3[1];
		}
  	</script>
  </head>
  <body bgcolor="red" id="dt_example" bottommargin="0" leftmargin="0" topmargin="0" onkeydown="return checkShortcut();" >  
  	<div id="container" class="container" style="width: 98%;" >
  		<br/>
		<br/>
		<h1>Programa Anual Cap&iacute;tulo Mil<label id="lbOperacion" style="font-size: 8pt"></label></h1>
		<br/>
		<div class="tabs" id="tabs">
			<ul>
				<li><a id="ConsultaPAAS" href="#tabs-0" >Consulta</a></li>
				<li><a id="CalendarioPAAS" href="#tabs-1" >Calendario</a></li>
			</ul>
			<div id="tabs-0" align="center">
				<% if (request.getParameter("tab").equals("0")) { %>
					<jsp:include page="ConsultaPAASCap1000.jsp" />
					<script type="text/javascript" charset="utf-8">
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 0);
					</script>
				<% } %>
			</div>
			<div id="tabs-1" align="center" >
				<% if (request.getParameter("tab").equals("1")) { %>
					<jsp:include page="CalendarioPAASCap1000.jsp" />
					<script type="text/javascript" charset="utf-8">
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 1);
					</script>
				<% } %>
			</div>
			
		</div>			
  	</div>			
   	<form action="">
   		<input type="hidden" id="tieneUe" name="tieneUe" size="10" value="-1" /> 
    	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" size="10" /> 
   		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" size="10" /> 
    	<input type="hidden" id="usuarioLogin" name="usuarioLogin" size="10" /> 
     	<input type="hidden" id="usuarioUE" name="usuarioUE" size="10" /> 
    	<input type="hidden" id="usuarioRole" name="usuarioRole" size="10" /> 
    	<input type="hidden" id="agregaCIdCABM" name="agregaCIdCABM" size="10" /> 
		<input type="hidden" id="agregaCIdSubPartida" name="agregaCIdSubPartida" size="10" /> 
		<input type="hidden" id="agreganPorcentajeIVA" name="agreganPorcentajeIVA" size="10" /> 
		<input type="hidden" id="agregacIdProcedencia" name="agregacIdProcedencia" size="10" value="N" /> 
		<input type="hidden" id="agregamPorcentajePyme" name="agregamPorcentajePyme" size="10" /> 
		<input type="hidden" id="agregamPorcentajeNoTratados" name="agregamPorcentajeNoTratados" size="10" /> 
		<input type="hidden" id="editaCIdCABM" name="editaCIdCABM" size="10" /> 
		<input type="hidden" id="editaCIdSubPartida" name="editaCIdSubPartida" size="10" />
		<input type="hidden" id="editaCIdSubPartidaCapMil" name="editaCIdSubPartidaCapMil" size="10" /> 
		<input type="hidden" id="editanIdPeriodo" name="editanIdPeriodo" size="10" /> 
		<input type="hidden" id="editanCantidad" name="editanCantidad" size="10" /> 
		<input type="hidden" id="editamPrecioUnitario" name="editamPrecioUnitario" size="10" /> 
		<input type="hidden" id="editacIdUsuarioModifica" name="editacIdUsuarioModifica" size="10" /> 
		<input type="hidden" id="borraCIdCABM" name="borraCIdCABM" size="10" /> 
		<input type="hidden" id="borraIdSubPartida" name="borraIdSubPartida" size="10" />
		<input type="hidden" id="borraIdSubPartidaCapMil" name="borraIdSubPartidaCapMil" size="10" /> 
		<input type="hidden" id="periodoSolicitudPA" name="periodoSolicitudPA" size="10" /> 
		<input type="hidden" id="cantidadLineasPA" name="cantidadLineasPA" size="10" /> 
		<input type="hidden" id="cBeneficiario" name="cBeneficiario" size="10" > 
		<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP" size="10" > 
		<input type="hidden" id="fBeneficiario" name="fBeneficiario" size="10" > 
		<input type="hidden" id="cUsuario" name="cUsuario" size="10" > 
		<input type="hidden" id="cUsuarioModifico" name="cUsuarioModifico" size="10" > 
		<input type="hidden" id="fCuentaModifico" name="fCuentaModifico" size="10" > 
		<input type="hidden" id="cBeneficiarioStatus" name="cBeneficiarioStatus" size="10" > 
		<input type="hidden" id="cUsuarioModifico" name="cUsuarioModifico" size="10" > 
		<input type="hidden" id="fCuentaModifico" name="fCuentaModifico" size="10" > 
		<input type="hidden" id="cEjercicio" name="cEjercicio" size="10" value="2021"> 
		<input type="hidden" id="incremento" name="incremento" size="10" > 
		<input type="hidden" id="flag" name="flag" size="10" > 
		<input type="hidden" id="loginPrecarga" name="loginPrecarga" size="10" > 
		<input type="hidden" id="unidadEjecutoraPrecarga" name="unidadEjecutoraPrecarga" size="10" > 
		<input type="hidden" id="centroContablePrecarga" name="centroContablePrecarga" size="10" >
		<input type="hidden" id="techoActivado" name="techoActivado" size="10" >
		<input type="hidden" id="cAccion" name="cAccion" size="10" >
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" size="10" >
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<!-- Para la plurianualidad -->
		<input type="hidden" id="agregaplurianualidad" name="agregaplurianualidad" size="10" >
		<input type="hidden" id="agregaplurianualidadv" name="agregaplurianualidadv" size="10" > 
		<input type="hidden" id="agregatipoAdjudicacion" name="agregatipoAdjudicacion" size="10" >
		<input type="hidden" id="valorplurianualidad" name="valorplurianualidad" size="10" value="">
		<input type="hidden" id="valorplurianualidadv" name="valorplurianualidadv" size="10" value="">
		<input type="hidden" id="valortipoAdjudicacion" name="valortipoAdjudicacion" size="10" value="">
		<input type="hidden" id="mImporteBruto" name="mImporteBruto" size="10" value="">
		<input type="hidden" id="cTolerancia" name="cTolerancia" />
		<!-- Inventario  -->
		<input type="hidden" id="cantidad_" name="cantidad_" size="10" value="">
		<input type="hidden" id="descrip_" name="descrip_" size="10" value="">
		<input type="hidden" id="CVE_UNI_" name="CVE_UNI_" size="10" value="">
		<input type="hidden" id="ajustePAAS" name="ajustePAAS" size="10" value="">
		<!-- Para las vistas -->
		<input type="hidden"  name="isAdmin" id="isAdmin" value="1"/>
		<input type="hidden"  name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>"/>
		<input type="hidden"  name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
		<input type="hidden"  name="modulo" id="modulo" value="MATERIALES"/> 				
		<input type="hidden" name="hayEP" id="hayEP" value="0"/>
		<input type="hidden" name="montoAnualTotalEPS" id="montoAnualTotalEPS" value="0" />
		<input type="hidden" name="cIdPartida" id="cIdPartida" />
		
		<!-- hiddens -->
		<input type="hidden" name="operacion" id="operacion" value="4"/>
		<input type="hidden" name="opcion" id="opcion" value="2"/>
		<input type="hidden" name="mesDisponible" id="mesDisponible" value="1"/>
    	<input type="hidden" name="tbs" id="tbs" value = "0">
    		
   	</form>
  </body>
</html>
