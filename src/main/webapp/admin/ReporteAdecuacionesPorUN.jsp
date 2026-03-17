<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bitacora</title>
<script src="../js/datepickercontrol.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/jquery-1.2.6.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />

<script type="text/javascript">

	function limpiarSesion()
	{
		window.location.href="wrkflw-reporteAuditoria.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
	}
	
	function Grid(){
		window.open('../admin/MultiReporteGridAdecPorUN.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=500px');
	}
	
	function fnGeneraReporte() {

		var principal = parent.parent.document;
		var asunto    = document.datawork;

		var Ep 					 = $.trim(document.getElementById('ep').value);
		var UnidadResponsableEP	 = document.getElementById('UnidadResponsableEP').value;
		var mFolio 				 = document.getElementById('mFolio').value;
		var mEstatus 			 = document.getElementById('mEstatus').value;
		var fechaIni 		 	 = document.getElementById('DPC_fechaInicial').value;
		var fechaFin 			 = document.getElementById('DPC_fechaFinal').value;
		var mMonto				 = document.getElementById('mMonto').value;
		var mFolioSICOP			 = document.getElementById('mFolioSICOP').value;
		var mFolioMAP			 = document.getElementById('mFolioMAP').value;
		var login				 = document.getElementById('res_u_login_3').value;
		//var nombre				 = document.getElementById('res_u_nombre_3').value;
		var reporte				 = "reporte";
		var param = "&opcion=" + reporte
					 + "&EP=" + Ep
					 + "&UnidadResponsableEP=" + UnidadResponsableEP
					 + "&mFolio=" + mFolio
					 + "&mEstatus=" + mEstatus
					 + "&fechaIni=" + fechaIni
					 + "&fechaFin=" + fechaFin
					 + "&mMonto=" + mMonto
					 + "&mFolioSICOP=" + mFolioSICOP
					 + "&mFolioMAP=" + mFolioMAP
					 + "&login=" + login;
					   
			//alert("param-->"+param);	   

			if(fechaIni=="" & fechaFin=="")
			{
				alert("Debes capturar algun dato");
				return false;	
			} else {
				$("#GenerarReporte").attr("disabled", true);
				$("#Limpiar").attr("disabled", true);
				window.open('../admin/ReporteAdecuacionesPorUN2.jsp?'+ param, 'MultiReporteResultado2','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
				$("#GenerarReporte").attr("disabled", false);
				$("#Limpiar").attr("disabled", false);
				return true;	
			}
		}
</script>
<script type="text/javascript" >

		$(document).ready(
			function() 
			{
				$("input.AyudaSyC").subIniciaDlg();	
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				
			}
		);
</script>		
</head>
<body background="../imagenes/steel_BG.gif" >
	<form id="frmReporteAdecuacionesUN" name="frmReporteAdecuacionesUN" target="formulario">
	<div class="Contenido" style="height: 10%">
		<table class="TituloRutaCA">
			<tr>
				<td>
					<img src="../imagenes/iconos/reportes.png" alt="" width="16"
						height="16">
					<font color="#FFFFFF">
					<strong>
						Reporte del status de las adecuaciones por Unidad Normativa.
					</strong>
					</font>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
		</table>
	</div>
	<br>		
	<table border="0" cellspacing="0" cellpadding="2">
		<tr>
			<td align="left">Estructura Programática:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input id="ep" name="ep" type="text" value="" size="70" maxlength="64" class=""/>
				<input type="button" value="..." onclick="Grid()"/>
			</td>
		</tr>
		<tr>
			<td align="left">Unidad Responsable:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input id="hUnidadResponsableEP" name="hUnidadResponsableEP" type="text" value="" size="40" maxlength="50" class="AyudaSyC  "/>
				<input id="UnidadResponsableEP" name="UnidadResponsableEP" type="hidden" value="" size="5" maxlength="5" class=""/>
			</td>
		</tr>
		<tr>
			<td>Folio:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input type="text" name="mFolio" id="mFolio" value="0"  style="text-align:right;">
			</td>
		</tr>
		<tr>
			<td>Estatus:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<select id="mEstatus" name="mEstatus">
					<option value="" selected>Seleccionar</option>
					<option value="CAPTURA">CAPTURA</option>
					<option value="REVISION">REVISION</option>
					<option value="SOLICITUD">SOLICITUD</option>
					<option value="TRÁMITE DE AUTORIZACIÓN DE HACIENDA">TRÁMITE DE AUTORIZACIÓN DE HACIENDA</option>
					<option value="CANCELADO">CANCELADO</option>
					<option value="AUTORIZADO">AUTORIZADO</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<input type="hidden" id="lbl_fecha" name="lbl_fecha" value="FECHA"/>Fecha
			</td>
			<td align="right">De:</td>
			<td width="25%">
			&nbsp;&nbsp;<input type="text" name="DPC_fechaInicial" id="DPC_fechaInicial" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20" tabindex="11"/>
			</td>
			<td align="right">A:</td>
			<td>
				<input type="text" name="DPC_fechaFinal"   id="DPC_fechaFinal"   datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20" tabindex="11"/>
			</td>
		</tr>
		<tr>
			<td>Monto:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input type="text" name="mMonto" id="mMonto" value="0"  style="text-align:right;">
			</td>
		</tr>
		<tr>
			<td>Folio SICOP:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input type="text" name="mFolioSICOP" id="mFolioSICOP" value="0"  style="text-align:right;">
			</td>
		</tr>
		<tr>
			<td>Folio MAP:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input type="text" name="mFolioMAP" id="mFolioMAP" value="0"  style="text-align:right;">
			</td>
		</tr>
		<tr>
			<td>Usuario:</td>
			<td>&nbsp;</td>
			<td colspan="3">
				<input id="res_u_login_3"  name="res_u_login_3"  type="hidden" size="15" class="protegido" disabled/>
				<input id="res_u_nombre_3" name="res_u_nombre_3" type="text"   size="53" class="AyudaSyC autoCompletaSyC"/>
			</td>
		</tr>
	</table>
	<br>
 	<table align="center">

		<tr>
			<td>
				<input type="button" value="Generar Reporte" onClick="return fnGeneraReporte();">
				<!--  input type="submit" name="button2" id="button2" value="Buscar" onclick="return fnValida();"/ -->
				<input type="button" value="Limpiar" onClick="limpiarSesion();"/>
			</td>
		</tr>
	</table>
	<p>&nbsp;</p>
	</form>
</body>
</html>