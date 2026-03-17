<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	if( session == null){
		response.sendRedirect("../index.jsp");
		return;
	}	
	
	Usuario u = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	if( u.getPropiedad("CCENTROCONTABLE") == null || StringUtils.isEmpty(  u.getPropiedad("CCENTROCONTABLE").getValor() ) ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String urUsuario = u.getU_UR();
	String nombreUsuario = u.getLogin();
	String centroContableUsuario =  u.getPropiedad("CCENTROCONTABLE").getValor();
	Caso c = (Caso)session.getAttribute(GestionInterface.ATT_CASE);
	int nFolioPagoPenasConv =  new Integer(c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1) ).intValue();
	String hoy = Util.getTodayESMX();
	String folio = c.getFolio();
	String operador = u.getNombre();
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	String ejercicioFiscal = adbl.obtenEjercicioFiscal();
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Pago de penas convencionales</title>		
		<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/PagoPenas.js"></script>
		<script type="text/javascript">
		var nombreUsuario = '<%=nombreUsuario%>';
		var UR = '<%=urUsuario%>';	
		var CC = '<%=centroContableUsuario%>';
		var nFolioPagoPenasConv = <%=nFolioPagoPenasConv%>;		
		var fAplicacion = '<%=hoy%>';
		var folio = '<%=folio%>';
		var operador = '<%=operador%>';
		var ejercicioFiscal = <%=ejercicioFiscal%>;
		
		function cmdImprimir(elFormato){
			window.open(
						"../admin/SeguridadCatalogos?"
							+ "catalogo=CONTRARECIBO"
							+ "&accion=run"
							+ "&rn=" + elFormato + ".jasper"
							+ "&whereFolio= penas.caNoContrarrecibo = '" + $("#caNoContrarrecibo").val()
							+"'", 			//+ "&nombre="   + ""
							//+ "&cargo="    + ""
							//+ "&area="     + "",
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
	
		</script>
				
	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" style="width: 70%" class="container">
			<div class="card-header"> <h3> Pago de penas convencionales </h3> </div>
			<hr class="mt-3"/>
			
			<form id="mainFrm" name="mainFrm">
				<input type="hidden" id="cCentroContable" name="cCentroContable" value=""/> 
				<input type="hidden" id="cxpbusqueda" name="cxpbusqueda" value=""/>
				<input type="hidden" id="tipoPagado" name="tipoPagado" value=""/>
				<input type="hidden" id="FOLIO" name="FOLIO" value=""/>
				<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value=""/>
				<input type="hidden" id="cUnidadResponsableContable" name="cUnidadResponsableContable" value="RHQ"/>
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value=""/>
				<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value="S"/>
				<input type="hidden" id="cDocumento" name="cDocumento" value="PAGODIVERSO"/>
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="EG"/>
				<input type="hidden" id="cRamo" name="cRamo" value="16"/>
				<input type="hidden" id="U_LOGIN" name="U_LOGIN" value=""/>
				<input type="hidden" id="cNombreVo" name="cNombreVo"  />
				<input type="hidden" id="cPaternoVo" name="cPaternoVo"  />
				<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
				<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
				<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
				<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO"/>
				<input type="hidden" id="cNombreA" name="cNombreA"  />
				<input type="hidden" id="cPaternoA" name="cPaternoA"  />
				<input type="hidden" id="cMaternoA" name="cMaternoA" />
				<input type="hidden" id="cPuestoA" name="cPuestoA" />
				<input type="hidden" id="firmanteAut" name="firmanteAut" />
				<input type="hidden" id="CXPRow" name="CXPRow" value=""/>
				<input type="hidden" id="EPRow" name="EPRow" value=""/>
				<input type="hidden" id="cEventoEPRow" name="cEventoEPRow" value=""/>
				
				<div id="mainDiv">
					<div class="row">	
						<div class="col-2">
							Folio:
							<input type="text" id="nFolioPagoPenasConv" name="nFolioPagoPenasConv" class="form-control " size="5" maxlength="10" readonly="readonly"/>
						</div>
						<div class="col-3">
							Fecha de Aplicacion:
							<input type="text" id="fAplicacion" name="fAplicacion" class="form-control" size="14" maxlength="14" readonly="readonly"/>
						</div>
						<div class="col-3">
							Contrarecibo:
							<input type="text" size="16" maxlength="16" class="form-control" readonly="readonly" id="caNoContrarrecibo" name="caNoContrarrecibo"/> 
						</div>
						<div class="col-2">
							<div id="btnDiv" style="display: none">
								<br>
								<input type="button" id="generaLayout" value="GeneraLayout" class="btn btn-secondary"/>
							</div>
						</div>
						<div class="col-2">
							<div id="btnDiv2" >
								<br>
								<input type="button" id="Imprimir" value="  Imprimir   " onClick="cmdImprimir('PolizaPenas')" class="btn btn-secondary" />
							</div>
						</div>
					</div>
					<div class="row">	
						<div class="col-12">
							Concepto:
							<textarea rows="3" cols="80" id="cConcepto" name="cConcepto" class="form-control" readonly="readonly"></textarea>
						</div>
					</div>
					<div class="row mt-2">
						<h1>Datos del Pago:</h1>
						<div class="col-3">
							Folio Pago:
							<input type="text" name="nFolioPago" id="nFolioPago" size="7" value="" class="form-control numero" readonly="readonly"/>
						</div>
						<div class="col-3">
							Tipo Pago:
							<input type="text" name="cTipoPago" id="cTipoPago" size="15" value="" class="form-control" readonly="readonly"/>
						</div>
						<div class="col-3">
							CxP Pago:
							<input type="text" name="cxpPago" id="cxpPago" size="16" value="" class="form-control" readonly="readonly"/>
						</div>
						<div class="col-3">
							Fecha Pago:
							<input type="text" name="fAplicacionPago" id="fAplicacionPago" size="16" value="" class="form-control" readonly="readonly"/>
						</div>
						
					</div>
					<div class="row">
						<div class="col-3">
							Origen Pago:
							<input type="text" name="urPago" id="urPago" size="7" value="" class="form-control" readonly="readonly"/>
						</div>
						<div class="col-3">
							RFC:
							<input type="text" name="beneficiarioPago" id="beneficiarioPago" size="15" value="" class="form-control" readonly="readonly"/>
						</div>
						<div class="col-6">
							Nombre Proveedor:
							<input type="text" name="cNombreProveedor" id="cNombreProveedor" size="15" value="" class="form-control" readonly="readonly"/>
						</div>
					</div>
					<div class="row">
						
						<div class="col-3">
							Importe Bruto:
							<input type="text" name="importeBrutoPago" id="importeBrutoPago" size="16" value="" class="form-control moneda" readonly="readonly"/>
						</div>
						<div class="col-3">
							Importe Impuestos:
							<input type="text" name="importeImpuestosPago" id="importeImpuestosPago" size="15" value="" class="form-control moneda" readonly="readonly"/>
						</div>
						<div class="col-3">
							Importe Total:
							<input type="text" name="importeTotalPago" id="importeTotalPago" size="15" value="" class="form-control moneda" readonly="readonly"/>
						</div>
						<div class="col-3">
							Importe Penalizacion:
							<input type="text" name="mImportePenalizacionPago" id="mImportePenalizacionPago" size="16" value="" class="form-control moneda" readonly="readonly"/>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							Concepto del Pago:
							<textarea rows="5" cols="85" id="cConceptoPago" name="cConceptoPago" class="form-control form-control-sm" readonly="readonly"></textarea>
						</div>
					</div>
					<div class="row">
						<div id="dtDetalleDiv" class="mt-2">
							<h1>Detalle del pago de Penalizacion:	</h1>
							<table class="display" cellspacing="0" cellpadding="0" align="center" id="dtDetallePenas">
								<thead>
									<tr>
										<th>Mes</th>
										<th>EP</th>
										<th>Monto Penalizacion</th>
										<th>Remanente Penalizacion</th>										
										<th>CXP</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</div>
				</div>
				<div id="SeleccionDialog" title="Seleccion de pago">
					<fieldset>
						<h1>Seleccion de pago</h1>
						<span>
							<br/>
							<b>*</b>Seleccione la solicitud para generar el pago de penalizaciones y despues click en aceptar.
							<br/>
						</span>
						<div id="dtSeleccion">
							<table class="display" cellspacing="0" cellpadding="0" align="center" id="dtSelPagos">
								<thead>
									<tr>
										<th>CxP</th>
										<th>Tipo</th>
										<th>Folio</th>
										<th>Beneficiario</th>
										<th>Penalizacion</th>
										<th>Remanente</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</fieldset>
				</div>
				<div id="CambiaValorDialog" title="Captura de Monto por Pagar">
					<fieldset>
						<h1>Captura de Montos</h1>
						<table>
							<tr>
								<td align="right">
									Remanente Penalizacion:
								</td>
								<td	align="left">
									<input type="text" id="remanenteMes" value="" class="form-control moneda" size="16"/>
								</td>
							</tr>
							<tr>
								<td align="right">
									Monto a Pagar:
								</td>
								<td	align="left">
									<input type="text" id="importeMes" value="" class="form-control moneda" size="16"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form>
		</div>
			
			<div id="dialog-firmantes" title="Firmantes" >
				<h5> Datos Firmantes VºBº </h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cNombreVoBo" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cNombreVoBo" name="cNombreVoBo"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPaternoVoBo" class="form-label"> Ap. Paterno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPaternoVoBo" name="cPaternoVoBo"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cMaternoVoBo" class="form-label"> Ap. Materno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cMaternoVoBo" name="cMaternoVoBo"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPuestoVoBo" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPuestoVoBo" name="cPuestoVoBo"/>
					</div>
				</div>
				
				<h5> Datos Firmantes Autorización </h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cNombreAut" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cNombreAut" name="cNombreAut"/>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPaternoAut" class="form-label"> Ap Paterno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPaternoAut" name="cPaternoAut"/>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cMaternoAut" class="form-label"> Ap Materno: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cMaternoAut" name="cMaternoAut"/>
					</div>
				</div>			
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cPuestoAut" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
						<input class="form-control form-control-sm" id="cPuestoAut" name="cPuestoAut"/>
					</div>
				</div>		
			</div>
	</body>
</html>