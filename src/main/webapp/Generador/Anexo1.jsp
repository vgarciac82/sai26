<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.core.Role"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="java.util.Calendar"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page	import="com.syc.gestion.core.*"%>
<%
	/*VGC20160105 Se calcula la fecha de aplicacion en base al EF activo.*/
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa = Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	
	Calendar c1 = Calendar.getInstance();
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	
	/*VGC20160105 Se calcula la fecha de aplicacion en base al EF activo.*/
	if( c1.get(Calendar.YEAR ) != efa )
		today = "31/12/" + efa;
	
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	String uLogin = usuario.getLogin();

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad(
				"CCENTROCONTABLE").getValor());
	}

	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}
	
	Role role = usuario.getRole("ADMIN_ANEXO1");
	boolean adminAnexo1 = role!=null && "ADMIN_ANEXO1".equals(role.getNombre());
	
	int nFolioAnexo1 = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1) );
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] solFirmada = null; 
	
	int nIdDocumento = 0;
	
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, "Solicitud Firmada");
	solFirmada = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, nIdDocumento);
	
	int iSolFirmada = solFirmada.length;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>ANEXO 1 - Solicitud de Recursos</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="-1">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<style type="text/css" title="currentStyle">
@import "themes/smoothness/jquery-ui-1.8.4.custom.css";

@import "css/demo_table_jui.css";

@import "css/demo_page.css";
</style>

<style>
.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}
</style>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/Anexo1.js"></script>
<script type="text/javascript" src="js/ValidaMesContable.js"></script>

<script type="text/javascript" charset="utf-8">

var operacion = <%=id_oper%>;
var folioTramite = '<%=c.getFolio()%>';
var usuarioTramite = '<%=usuario.getLogin()%>';
var oTable2;
var breturnVal = false;
var solFirmada = <%=iSolFirmada%>;
</script>
</head>
<body id="dt_example">
	<form method="post" id="anexo1" name="anexo1" action="../Anexo1/crear">
		
		<!-- hidden para la captura de oficio delegatorio -->
		<input type="hidden" id="firmanteExiste" name="firmanteExiste" size="14">
		<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
		<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
		<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
		<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
		<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
		<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
		<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
		<!--FIN hidden para la captura de oficio delegatorio -->
		
		<!-- hidden para la captura de oficio delegatorio VoBo-->
		<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
		<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
		<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
		<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
		<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
		<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
		<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
		<!-- FIN hidden para la captura de oficio delegatorio VoBo-->
		
		<input type="hidden" id="uLogin" name="uLogin" value="<%=uLogin%>" />
		<input type="hidden" id="epEditar" name="epEditar" value="" /> 
		<input type="hidden" id="nPosicion" name="nPosicion" value="" /> 
		<input type="hidden" id="nFolioAnexo" name="nFolioAnexo" value="<%=nFolioAnexo1%>" /> 
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=usuario.getU_UR()%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"  value="<%=cCentroContable%>" /> 
		<input type="hidden" name="cEjercicio" id="cEjercicio" />
		<input type="hidden" name="cTipoAnexo" id="cTipoAnexo" value="DISPONIBLE"/>
		<input type="hidden" name="caNoContrarrecibo" id="caNoContrarrecibo" value="<%=c.getFolio()%>"/>
		<!-- <input type="hidden" name="cIdContrato" id="cIdContrato" value=""/>  -->
		
		<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
		<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
		<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
		<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40 value="">
		
		<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40 value="">
		<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
		<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
		<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
		<input type="hidden" id="cPuestoA" name="cPuestoA" size=40 value="">
		<input type="hidden" id="firmanteAut" name="firmanteAut" size=40 value="">
		<input type="hidden" id="cEvento" name="cEvento" size=40 value="DIS_ANEXO1">

		<input type="hidden" name="nIdEstatus" id="nIdEstatus" value="0"/>
		<input type="hidden" name="cMotivoRechazoSnd" id="cMotivoRechazoSnd" value=""/>
		
		<input type="hidden" name="epUpdate" id="epUpdate" value=""/>
		<input type="hidden" name="cMesUpdate" id="cMesUpdate" value=""/>
		<input type="hidden" name="mImporteUpdate" id="mImporteUpdate" value=""/>
		<input type="hidden" name="accion" id="accion" value=""/>
		<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=nFolioAnexo1%>">
		<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
		<input type="hidden" id="solGeneradasUR" name="solGeneradasUR" value="0">
		<input type="hidden" name="cDocumentoHAplicado" id="cDocumentoHAplicado" value=""/>
		<input type="hidden" name="nFolioPagoApartado" id="nFolioPagoApartado" value=""/>
		<input type="hidden" name="existeDetalle" id="existeDetalle" value=""/>
		<input type="hidden" name="existeIntegrado" id="existeIntegrado" value=""/>	
		<input type="hidden" name="nFolioApartado" id="nFolioApartado" value=""/>
		<input type="hidden" name="anexoCancelado" id="anexoCancelado" value=""/>
		<input type="hidden" name="rolAdminAnexo" id="rolAdminAnexo" value="<%=adminAnexo1%>"/>
		
		<!-- hidden para la captura de los datos de quien elaboro -->
		<input type="hidden" id="cNombreE" name="cNombreE" size=40>
		<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
		<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
		<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
		<input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
		
		<input type="hidden" id="existeFirmante" name="existeFirmante" size=40 value="NOEXISTE">
		
		<div id="container" class="container">
			<h1 align="center">Solicitud de Recursos x Pagar</h1>
			<div id="autorizacionDiv" style="display: none;">
				<fieldset>
					<legend>Autorizar</legend>
					<table width="100%">
						<tr>
							<td colspan="3">
								Seleccione una opcion:
							</td>
						</tr>
						<tr>
							<td align="left">
								<input type="radio" name="autorizarAnexo1" id="autoriza" value="1" checked="checked"><b>Autorizar</b> <br/>Termina el tramite autorizando la solicitud de recursos.
							</td>
							<td align="left">
								<input type="radio" name="autorizarAnexo1" id="corrige" value="0" onclick="muestraCapturaRechazo()"> <b>Corregir</b> <br/>Envia el tramite a correcci&oacute;n.
							</td>
							<td align="left">
								<input type="radio" name="autorizarAnexo1" id="rechaza" value="-1" onclick="muestraCapturaRechazo()" > <b>Rechazar</b> <br/>Termina el tramite rechazando la solicitud de recursos. 
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<div id="mostrarMotivoDiv">
				<table>
					<tr>
						<td align="left">
							<a href="#" onclick="muestraCapturaRechazo();return false;">Mostrar Motivo de Rechazo</a>
						</td>
					</tr>
				</table>	
			</div>
			<fieldset>
				<legend>Datos Captura</legend>
				<table style="width:740px">
					<tr>
						<td align="left">
							Folio 
							<input type="text" id="id_caso" name="id_caso" size="10" value="<%=nFolioAnexo1%>" style="text-align: right;" readonly="readonly">
						</td>
						<td align="left">
							Fecha 
							<input type="text" id="fAplicacion" name="fAplicacion" size="10" style="text-align: center;" readonly="readonly"  value="<%=today%>">
						</td>
						<td align="left">
							<b>Monto Solicitud</b> 
							<input type="text" id="importeTotal" name="importeTotal" size="20" style="text-align: right;" readonly="readonly" value=$0.00>
						</td>
						<td>
							<div id="divImprimePoliza">
								<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaRecxPagar');">Póliza
								<span id="EditaFirmas"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="3">
								<textarea name="cConcepto" id="cConcepto"
								onkeypress="return validaCaracter(event)"
								style="height: 100px; width: 500px" onblur="blurConcepto()">Escriba un concepto</textarea>
						</td>
					</tr>
				</table>
			</fieldset>
			<table width="100%">
				<tr>
					<td align="right">
						<input type="button" id="btnCancela" name="btnCancela" value="Cancelar" alt="Cancelar el Apartado." onclick="cancelaSolicitud();">
					</td>
				</tr>	
			</table>
			<table width="100%" id="tblSelPresupuesto">
				<tr>
					<td align="left">
						<input type="button" id="btnPresupuesto" name="btnPresupuesto" value="Ver Presupuesto" alt="Ver y seleccionar el presupesto disponible.">
					</td>
						
					<td align="left" style="display: none">Clave de Contrato: <input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="50" name="cIdContrato" id="cIdContratoCompromiso" readonly ></td>
					<td align="right">
						<b>
							<label>*Doble clic para editar importes.</label>
						</b>
					</td>
				</tr>
			</table> 
			<div>
				<table id="dt_epAgregadas" class="display" cellspacing="0"
					cellpadding="2" align="center">
					<thead>
						<tr>
							<th>MARCAR</th>
							<th>EP</th>
							<th>TOTAL</th>
							<th>ENERO</th>
							<th>FEBRERO</th>
							<th>MARZO</th>
							<th>ABRIL</th>
							<th>MAYO</th>
							<th>JUNIO</th>
							<th>JULIO</th>
							<th>AGOSTO</th>
							<th>SEPTIEMBRE</th>
							<th>OCTUBRE</th>
							<th>NOVIEMBRE</th>
							<th>DICIEMBRE</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
			<table width="100%">
				<tr>
					<td align="left">
						<input type="button" id="btnBorraEp" name="btnBorraEp" value="Eliminar" alt="Elimina la EP" onclick="eliminaEP();">
					</td>
					<td align="right">
						<b>
							<label>*Eliminar Renglon. Marcar renglon y dar click en boton Eliminar. </label>
						</b>
					</td>
				</tr>
			</table> 
		</div>
		<div id="dialog-EPS" title="Saldo Disponible">
			<input type="checkbox" id="checkAll" name="checkAll">Seleccionar Todo
			<table id="tblEPS" class="display"  >
				<thead>
					<tr>
						<th>&nbsp;</th>
						<th>EP</th>
						<th>TOTAL</th>
						<th>ENERO</th>
						<th>FEBRERO</th>
						<th>MARZO</th>
						<th>ABRIL</th>
						<th>MAYO</th>
						<th>JUNIO</th>
						<th>JULIO</th>
						<th>AGOSTO</th>
						<th>SEPTIEMBRE</th>
						<th>OCTUBRE</th>
						<th>NOVIEMBRE</th>
						<th>DICIEMBRE</th>
					</tr>
				</thead>
			</table>
			<table width="100%">
				<tr>
					<td align="right">
						<input type="button" id="btnAgregaClaves" name="btnAgregaClaves" value="Agregar" />
					</td>
				</tr>
			</table> 
		</div>
		<div id="dialogEditaImportes" title="Editar Importes">
			<fieldset>
				<table>
					<tr>
						<td><label for="epDisp">EP:</label></td>
						<td><input type="text" name="epDisp" id="epDisp" value=""
							size="65" readonly="readonly" /></td>
					</tr>
				</table>
				<table id="capturaMontosTbl" align="center">
					<thead>
						<tr>
							<th>Mes</th>
							<th>Disponible</th>
							<th>Monto</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><label for="eneroDisp"> Enero</label></td>
							<td><input type="text" id="montoEnero" name="montoEnero"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="EneroEditar" name="EneroEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="febreroDisp">Febrero</label></td>
							<td><input type="text" id="montoFebrero" name="montoFebrero"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="FebreroEditar"
								name="FebreroEditar" value="0.00" size="15"
								style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="marzoDisp">Marzo</label></td>
							<td><input type="text" id="montoMarzo" name="montoMarzo"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="MarzoEditar" name="MarzoEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="abrilDisp">Abril</label></td>
							<td><input type="text" id="montoAbril" name="montoAbril"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="AbrilEditar" name="AbrilEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="mayoDisp">Mayo</label></td>
							<td><input type="text" id="montoMayo" name="montoMayo"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="MayoEditar" name="MayoEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="junioDisp">Junio</label></td>
							<td><input type="text" id="montoJunio" name="montoJunio"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="JunioEditar" name="JunioEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="julioDisp">Julio</label></td>
							<td><input type="text" id="montoJulio" name="montoJulio"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="JulioEditar" name="JulioEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="agostoDisp">Agosto</label></td>
							<td><input type="text" id="montoAgosto" name="montoAgosto"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="AgostoEditar" name="AgostoEditar"
								value="0.00" size="15" style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="septiembreDisp">Septiembre</label></td>
							<td><input type="text" id="montoSeptiembre"
								name="montoSeptiembre" value="0.00" size="15"
								readonly="readonly" style="text-align: right;" /></td>
							<td><input type="text" id="SeptiembreEditar"
								name="SeptiembreEditar" value="0.00" size="15"
								style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td><label for="octubreDisp">Octubre</label></td>
							<td><input type="text" id="montoOctubre" name="montoOctubre"
								value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="OctubreEditar"
								name="OctubreEditar" value="0.00" size="15"
								style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td>
								<label for="noviembreDisp">Noviembre</label>
							</td>
							<td>
								<input type="text" id="montoNoviembre"
								name="montoNoviembre" value="0.00" size="15" readonly="readonly"
								style="text-align: right;" /></td>
							<td><input type="text" id="NoviembreEditar"
								name="NoviembreEditar" value="0.00" size="15"
								style="text-align: right;"
								onkeypress="return validaKeyPress(event)"
								onblur="validaImporte(this)" /></td>
						</tr>
						<tr>
							<td>
								<label for="diciembreDisp">Diciembre</label></td>
							<td>
								<input type="text" id="montoDiciembre" name="montoDiciembre" value="0.00" size="15" readonly="readonly" style="text-align: right;" />
							</td>
							<td>
								<input type="text" id="DiciembreEditar" name="DiciembreEditar" value="0.00" size="15" style="text-align: right;" onkeypress="return validaKeyPress(event)" onblur="validaImporte(this)" />
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td align="right">
								<label>Total Calendarizado:</label>
							</td>
							<td>
								<input type="text" id="totalEditado" readonly="readonly" value="0.00" size="15" style="text-align: right;" />
							</td>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</div>
		<div id="esperar" align="center">
			Espere por favor... <img border="0" src="../imagenes/espera.gif"
				height="30">
		</div>
		<div id="dlgCaptuarMotivo">
			<fieldset>
				<legend>Captura de Motivo.</legend>
				<table>
					<tr>
						<td align="left">
							Por favor ingrese el motivo:
						</td>
					</tr>
					<tr>
						<td align="left">
							<textarea rows="10" cols="50" id="cMotivoRechazo" name="cMotivoRechazo"></textarea>
						</td>
					</tr>
				</table>
			</fieldset>
		</div>
		<div id="dialog-firmantes" title="Firmantes">
			<fieldset>
				
				<table>
					<tr>
						<td align="left">
							<input type="checkbox" id="oficioDelegatorioUpdate" name="oficioDelegatorioUpdate" onclick="showDivOficio();">Oficio Delegatorio Autoriza
						</td>
						<td align="left">
							<input type="checkbox" id="oficioDeleVoBoUpdate" name="oficioDeleVoBoUpdate" onclick="showDivOficioVoBo();">Oficio Delegatorio VoBo
						</td>	
					</tr>
				</table>
				
				<legend>Datos VºBº</legend>
				<table>
					<tr>
						<td>Nombre</td>
						<td><input type="text" id="cNombreVoBo" name="cNombreVoBo" size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Apellido Paterno</td>
						<td><input type="text" id="cPaternoVoBo" name="cPaternoVoBo" size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Apellido Materno</td>
						<td><input type="text" id="cMaternoVoBo" name="cMaternoVoBo" size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Puesto</td>
						<td><input type="text" id="cPuestoVoBo" name="cPuestoVoBo" size=40 maxlength="70" />
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Datos Autoriza</legend>
				<table>
					<tr>
						<td>Nombre</td>
						<td><input type="text" id="cNombreAut" name="cNombreAut" size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Apellido Paterno</td>
						<td><input type="text" id="cPaternoAut" name="cPaternoAut" size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Apellido Materno</td>
						<td><input type="text" id="cMaternoAut" name="cMaternoAut" size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Puesto</td>
						<td><input type="text" id="cPuestoAut" name="cPuestoAut" size=40 maxlength="70" />
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Datos Elabora</legend>
				<table>
					<tr>
						<td>Nombre</td>
						<td><input type="text" id="cNombreEla" name="cNombreEla"
							size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Apellido Paterno</td>
						<td><input type="text" id="cPaternoEla" name="cPaternoEla"
							size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Apellido Materno</td>
						<td><input type="text" id="cMaternoEla" name="cMaternoEla"
							size=40 maxlength="70" />
						</td>
					</tr>
					<tr>
						<td>Puesto</td>
						<td><input type="text" id="cPuestoEla" name="cPuestoEla"
							size=40 maxlength="70" />
						</td>
					</tr>
				</table>
			</fieldset>
			<div id="oficioDelegatorioCapturaUpdate" style="display: none">
					<fieldset>
						<legend>Datos del Suplente Autoriza</legend>
						<table>
							<tr>
								<td>No. de Oficio</td>
								<td><input type="text" id="cFolioOficioUpdate"
									name="cFolioOficioUpdate" size=30 maxlength="70" />
								</td>
							</tr>
							<tr>
								<td>Fecha de Oficio</td>
								<td><input type="text" id="dFechaOficioUpdate"
									name="dFechaOficioUpdate" size=10 maxlength="10" />
								</td>
							</tr>
							
							<tr>
								<td>
									Tipo de Suplencia:
								</td>
								<td>
									<select id="tipoSuplencia" name="tipoSuplencia"></select>
								</td>
							</tr>
							
							<tr>
								<td>Nombre Suplente:</td>
								<td><input type="text" id="cNombreTitularUpdate"
									name="cNombreTitularUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Paterno</td>
								<td><input type="text" id="cApellidoPaternoTitularUpdate"
									name="cApellidoPaternoTitularUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Materno</td>
								<td><input type="text" id="cApellidoMaternoTitularUpdate"
									name="cApellidoMaternoTitularUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Puesto</td>
								<td><input type="text" id="cPuestoTitularUpdate"
									name="cPuestoTitularUpdate" size=40 maxlength="70" /></td>
						</table>
					</fieldset>
				</div>
				
				<div id="oficioDelegatorioVoBoUpdate" style="display: none">
					<fieldset>
						<legend>Datos del Suplente VoBo</legend>
						<table>
							<tr>
								<td>No. de Oficio</td>
								<td><input type="text" id="cFolioOficioVoBoUpdate"
									name="cFolioOficioVoBoUpdate" size=30 maxlength="70" />
								</td>
							</tr>
							<tr>
								<td>Fecha de Oficio</td>
								<td><input type="text" id="dFechaOficioVoBoUpdate"
									name="dFechaOficioVoBoUpdate" size=10 maxlength="10" />
								</td>

							</tr>
							
							<tr>
								<td>
									Tipo de Suplencia:
								</td>
								<td>
									<select id="tipoSuplenciaVoBo" name="tipoSuplenciaVoBo"></select>
								</td>
							</tr>
							
							<tr>
								<td>Nombre Suplente:</td>
								<td><input type="text" id="cNombreTitularVoBoUpdate"
									name="cNombreTitularVoBoUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Paterno</td>
								<td><input type="text" id="cApellidoPaternoTitularVoBoUpdate"
									name="cApellidoPaternoTitularVoBoUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Materno</td>
								<td><input type="text" id="cApellidoMaternoTitularVoBoUpdate"
									name="cApellidoMaternoTitularVoBoUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Puesto</td>
								<td><input type="text" id="cPuestoTitularVoBoUpdate"
									name="cPuestoTitularVoBoUpdate" size=40 maxlength="70" /></td>
						</table>
					</fieldset>
		</div>
		<div id="dialog-Procesando" title="Procesando">
  			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
			  <img border="0" src="../imagenes/espera.gif" height="30">
			</div>
		</div>
		<div id="dialog-Cancela" title="Cancela Documento">
  			<h2>Esta Seguro Que Desea Cancelar el Documento?</h2>
			<div id="divEsperaCancelando" style="visibility: hidden" align="center">Espere por favor...
				<img border="0" src="../imagenes/espera.gif" height="30">
			</div>
		</div>
		<div id="procesar" align="center" title="Espera">
			<fieldset>
				<table>
					<tr>
						<td>Espere por favor.... <img border="0" src="../imagenes/espera.gif" height="30"> </td>
					</tr>
				</table>
			</fieldset>
		</div>		
	</form>
</body>
</html>