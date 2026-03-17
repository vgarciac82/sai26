<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base>
		<title>modificaConcepto</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
	</head>
	<link rel="stylesheet" type="text/css" href="../css/contable.css"></link>
	<link rel="stylesheet" type="text/css"
		href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css">

	<script type="text/javascript" src="../js/datepickercontrol.js">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery-1.6.2.min.js">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery.jeditable-1.6.2.js">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery.dataTables.js">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery.dataTables.editable-1.3.js">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery.ui.datepicker.js">
</script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.core">
</script>
	<script type="text/javascript"
		src="../Generador/js/jquery.ui.widget.js">
</script>
	<script type="text/javascript" src="../Generador/js/crud.js">
</script>
	<script type="text/javascript">

$(document).ready(function() {

});
function limpiar() {
	if ($("#nFolio").val() != "") {
		$("#nFolio").val("");
		$("#caNoContrarrecibo").val("");
		$("#cIdRFC").val("");
		$("#cnombre").val("");
		$("#fAplicacion").val("");
		$("#mImporteNeto").val("");
		$("#cConcepto").val("");
		noFolio = "";
		document.getElementById("actualizar").disabled = true;
	}
	return true;

}

function cargaConcep() {
	queryFormPost("selectConcepto", {
		async : false,
		callback : function() {
			if ($("#caNoContrarrecibo").val() != "") {
				document.getElementById("actualizar").disabled = false;
			} else
				alert("ContraRecibo no localizado!");
		}
	});
	
}

function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
			patron =/[A-Za-z.\d\s\\. ]/;
			te = String.fromCharCode(tecla);
			return patron.test(te);
	}


function modifConcep() {
	if ($("#caNoContrarrecibo").val() != "") {
		if ($("#docto").val() == 'RELG')
			queryFormPost("updateRelacionGastos", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}

			})
		else if ($("#docto").val() == 'PDIR')
			queryFormPost("updatePagoDirecto", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}
			})
		else if ($("#docto").val() == 'POBR')
			queryFormPost("updatePagoObra", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}
			})
		else if ($("#docto").val() == 'PDIV')
			queryFormPost("updatePagoDiverso", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}
			})
		else if ($("#docto").val() == 'NOMI')
			queryFormPost("updateNomina", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}
			})
		else if ($("#docto").val() == 'OPAJ')
			queryFormPost("updateOperAjenas", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}
			})
		else if ($("#docto").val() == 'PFED')
			queryFormPost("updatePagoFederalizado", {
				async : false,
				callback : function() {
					alert("Actualizado con exito!");
				}
			})
		document.getElementById("actualizar").disabled = true;
		document.getElementById("Buscar").disabled = false;
	}
}
</script>
	<body>
		<form id="modifConcepto" name="modifConcepto" method="post"
			action="modificaConcepto.jsp?modifConcepto=Si">
			<table id="cancela_Docto" width="50%">
				<tr>
					<td colspan="4" align="center">
						<h1>Modifica Concepto</h1>
					</td>
				</tr>
				<tr>
					<td colspan="4" align="center"></td>
				</tr>
				<tr>
					<td align="center">
						ContraRecibo:
						<input type="text" id="noFolio" name="noFolio" value=""
							style="text-transform: uppercase" />
					</td>
				</tr>
				<tr>
					<td align="center" valign="top" colspan="4">
						<input type="button" id="Buscar" name="Buscar" value="Buscar"
							onclick="javascript:cargaConcep();"></input>
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td>
						<table id="datos_doctos" border="0" align="center">
							<tr align="center">
								<td colspan="2" valign="top" nowrap>
									<input type="hidden" id="docto" name="docto" value="">
									<input type="hidden" id="caNoContrarrecibo"
										name="caNoContrarrecibo" value="">
									<input type="hidden" id="nEnviadoSicop" name="nEnviadoSicop"
										value="0">
									<input type="hidden" id="cUnidadResponsable"
										name="cUnidadResponsable" value="0">
									<input type="hidden" id="cDocumentoHaplicado"
										name="cDocumentoHaplicado">
									<input type="hidden" id="folioDevolucion"
										name="folioDevolucion">
									<input type="hidden" id="cCentroContable"
										name="cCentroContable" value="">

									Folio:
									<input name="nFolio" type="text" id="nFolio" size="10"
										maxlength="40" disabled="disabled" style="background: #f0f0f0">
								</td>
								<td align="center">
								</td>
							</tr>
							<tr align="center">
								<td valign="top">
									RFC:
									<input type="text" maxlength="15" size="15" name="cIdRFC"
										id="cIdRFC" disabled="disabled" style="background: #f0f0f0">
									<input type="text" maxlength="100" size="96" name="cnombre"
										id="cnombre" value="" disabled="disabled"
										style="background: #f0f0f0">
								</td>
								<td>
								</td>
							</tr>
							<tr align="center">
								<td colspan=2 valign="top">
									Fecha de Aplicaci&oacute;n:
									<input name="fAplicacion" disabled="disabled" class="paso01"
										type="text" id="fAplicacion" value="30/01/2012" maxlength="10"
										size="10" style="background: #f0f0f0">
									&nbsp;&nbsp;&nbsp;&nbsp;Importe Neto:
									<input name="mImporteNeto" disabled="disabled" type="text"
										style="background: #f0f0f0; text-align: right;"
										id="mImporteNeto" size="15">
								</td>
								<td></td>
							</tr>
							<tr align="center">
								<tr align="center">
									<td>
										Concepto
									</td>
								</tr>
								<tr align="center">
									<td>
										<textarea name="cConcepto" id="cConcepto" rows="6"
											style="width: 38%;" maxlength="300" onkeypress="return validar(event)"></textarea>
									</td>
								</tr>
								<tr align="center">
									<td></td>
								</tr>
								<tr align="center">
									<td>
										<input type="button" id="actualizar" name="actualizar"
											value="Actualizar" disabled="disabled"
											onclick="modifConcep();">
										<input type="button" id="Limpiar" name="Limpiar"
											value="Limpiar" onclick="limpiar();"></input>

									</td>
								</tr>
								<tr>
									<td></td>
								</tr>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
