<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%!Logger log = Logger.getLogger("SolicitudViaticos.jsp"); %>
<%
  Usuario u = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
  
  if( u == null ){
  	log.warn("Sin usuario. Se solicita reingreso");
  	response.sendRedirect("../index.jsp");
  	return;
  }
  
  Caso c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE );
  if( c == null ){
    log.warn("Sin caso. Se solicita reingreso");
  	response.sendRedirect("../index.jsp");
  	return;
  }
  
  String uLogin = u.getLogin();
  String numeroEmpleado = u.getNumeroEmpleado();
  String cUnidadResponsable = u.getU_UR();
  String uEmail = u.getU_email();
  
  int nFolioSolicitudViaticos = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1 ) );
  String fechaCaptura = Util.getTodayESMX();
  
  EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
  String ejercicioFiscal = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal(); 
 %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Solicitud de Viaticos</title>
	
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<style type="text/css">
			input[readonly] {
			    background-color: #EEEEEE;
			}
		</style>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"> </script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
		<script type="text/javascript" src="../js/catalogo/general.js"> </script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"> </script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"> </script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		
		<script type="text/javascript" src="../Generador/js/DatosViaticante.js"> </script>
		<script type="text/javascript" src="../Generador/js/DatosComision.js"> </script>
		<script type="text/javascript" src="../Generador/js/Transporte.js"> </script>
		<script type="text/javascript" src="../Generador/js/FlujoSolViaticos.js"> </script>
		
		<script type="text/javascript">
		
			 var uLogin = '<%=uLogin%>';
  			 var numeroEmpleado = '<%=numeroEmpleado %>';
  			 var cUnidadResponsable = '<%=cUnidadResponsable %>';
  			 var nFolioSolicitudViaticos = '<%=nFolioSolicitudViaticos %>';
  			 var fechaCaptura = '<%=fechaCaptura %>';
  			 var empleadoEmail = '<%=uEmail%>';
  			 var ejercicioFiscal = "<%=ejercicioFiscal%>";
  			 
			$(document).ready(function() {
				$("#nFolioSolicitudViaticos").val(nFolioSolicitudViaticos);
				document.getElementById('resumenViaticosDIV').style.display = 'none';
				
				$.blockUI({
					message : "Cargando Informacion. Por favor espere ......"
				});
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();				
				
			});
			
			function mostrar(){
				document.getElementById('resumenViaticosDIV').style.display = 'block';
			}
		</script>
	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<form id="FormViaticos" name="FormViaticos">
		
		
			<input type="hidden" id="transporteCapturado" value="0">
			<input type="hidden" id="existeCaptura" value="0"> 
			<input type="hidden" id="agendaCapturada" value="0">
			<input type="hidden" id="nIDEstatus" value="0">
			
			<div id="container" class="container" style="width: 80%">
				<h1>Solicitud de Viaticos</h1>
				<div>
					<fieldset>
						<legend>General</legend>
						<div>
							<table width="100%">
								<tr>
									<td align="left">
										<table>
											<tr>
												<td align="right">Folio Solicitud:</td>
												<td align="left"><input type="text" readonly="readonly" size="18" id="nFolioSolicitudViaticos" name="nFolioSolicitudViaticos" ></td>
											</tr>
										</table>
									</td>
									<td align="right">
										<table>
											<tr>
												<td align="right">Fecha de Captura:</td>
												<td align="left"><input type="text" readonly="readonly" size="11" id="dFechaCaptura" name="dFechaCaptura"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>
					</fieldset>
				</div>
				<div id="datosViaticanteDIV">
					<fieldset>
						<legend>Datos Viaticante</legend>
						<jsp:include page="DatosViaticante.jsp"></jsp:include>
					</fieldset>
				</div>
				<div id="datosComisionDIV" style="display: none">
					<fieldset>
						<legend>Datos Comisi&oacute;n</legend>
						<jsp:include page="DatosComision.jsp"></jsp:include>
					</fieldset>
				</div>
				<div id="datosTransporteDIV"  style="display: none">
					<fieldset>
						<legend>Transporte</legend>
						<jsp:include page="Transporte.jsp"></jsp:include>
					</fieldset>
				</div>
				<div id="resumenViaticosDIV">
					<fieldset>
						<legend>Resumen Viaticos</legend>							
							<table>
								<tr>							
									<td align="right">D&iacute;as Acumulados</td>									
								</tr>
								<tr>
									<td align="right"><label id="lblNacional">Nacional:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="nDNacional" name="nDNacional" size="8" style="text-align: right" ></td>									
								</tr>
								<tr>
									<td align="right"><label id="lblInternacional">Internacional:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="nDInternacional" name="nDInternacional" size="8" style="text-align: right" ></td>									
								</tr>
								<tr>
									<td align="right"><label id="lblNacional">Este Formato:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="nDEsteFormato" name="nDEsteFormato" size="8" style="text-align: right" ></td>
									<td nowrap>&nbsp;</td>
									<td nowrap>&nbsp;</td>
									<td nowrap>&nbsp;</td>
									<td align="right"><label id="lblmAgenda">Importe Agenda:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="mImporteAgenda" name="mImporteAgenda" size="18" style="text-align: right" ></td>									
								</tr>
								<tr>
									<td align="right"><label id="lblNacional">Pendientes:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="nDPendientes" name="nDPendientes" size="8" style="text-align: right" ></td>
									<td nowrap>&nbsp;</td>
									<td nowrap>&nbsp;</td>
									<td nowrap>&nbsp;</td>
									<td align="right"><label id="lblTransporte">Transporte:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="mTransporte" name="mTransporte" size="18" style="text-align: right" ></td>
								</tr>
								<tr>
									<td align="right"><label id="lblNacional">TOTAL:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="nDTotal" name="nDTotal" size="8" style="text-align: right" ></td>
									<td nowrap>&nbsp;</td>
									<td nowrap>&nbsp;</td>
									<td nowrap>&nbsp;</td>
									<td align="right"><label id="lblTOTAL">TOTAL:</label></td>
									<td align="right"><input type="text" readonly="readonly" id="mTOTAL" name="mTOTAL" size="18" style="text-align: right" ></td>
								</tr>
							</table>
						
					</fieldset>
				</div>
				<div id="OperacionesDIV">
					<fieldset>
						<legend>Operaciones</legend>
						<table align="center">
							<tr>
								<td align="center">
									<input type="button" value="Guardar" id="GuardarBtn" onclick = "mostrar()" style="display: none">
								</td>
								<td align="center">
									<input type="button" value="Enviar" id="EnviarBtn"  style="display: none">
								</td>
								<td align="center">  
									<input type="button" value="Eliminar" id="EliminarBtn" >
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</div>	
		</form>
	</body>
</html>