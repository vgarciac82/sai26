<%@page import="com.syc.sai.fonden.model.FondenCatalogsManager"%>
<%@page import="com.syc.sai.fonden.FondenTipoPago"%>
<%@page import="com.syc.sai.fonden.model.FondenManager"%>
<%@page import="com.syc.sai.fonden.model.FondenBusinessLogic"%>
<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenManager"%>
<%@page import="com.syc.sai.fonden.*"%>
<%@page import="com.syc.sai.fonden.model.FondenCatalogsManager"%>
<%@page import="com.syc.sai.fonden.model.FondenMovimientoBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenMovimientoManager"%>
<%@page import="com.syc.sai.fonden.FondenMovimiento"%>
<%@page import="com.syc.sai.fonden.model.FondenFacturacionBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenFacturacionManager"%>
<%@page import="com.syc.sai.fonden.FondenFacturacion"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<%!private Logger log = Logger.getLogger(getClass());%>

<%
	FondenFacturacion fondenFacturacion = null;
	Integer cidFonden = null;
	Integer nidFondenMovimiento = null;
	Integer nidFondenFacturacion = null;
	if(request.getParameter("nidFondenFacturacion")!=null && !request.getParameter("nidFondenFacturacion").equals("")){
		cidFonden = new Integer(request.getParameter("cidFonden"));
		nidFondenMovimiento = new Integer(request.getParameter("nidFondenMovimiento"));
		nidFondenFacturacion = new Integer(request.getParameter("nidFondenFacturacion"));		
		fondenFacturacion = FondenFacturacionManager.readFondenFacturacion(new FondenFacturacionBusinessLogic().getConnection(), cidFonden, nidFondenMovimiento, nidFondenFacturacion);
	}else{
		cidFonden = new Integer(request.getParameter("cidFonden"));
		nidFondenMovimiento = new Integer(request.getParameter("nidFondenMovimiento"));
		nidFondenFacturacion = FondenFacturacionManager.newIdFondenFacturacion(new FondenFacturacionBusinessLogic().getConnection(), cidFonden, nidFondenMovimiento);
	}
		
	List<FondenTipoPago> fondenTipoPagos = FondenCatalogsManager.readCatalog(new FondenBusinessLogic().getConnection(), "tFondenTipoPago");
	List<FondenTipoFactura> fondenTipoFacturas = FondenCatalogsManager.readCatalog(new FondenBusinessLogic().getConnection(), "tFondenTipoFactura"); 
	FondenTipoPago fondenTipoPago = null;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	String currentYear = sdf.format(new Date(System.currentTimeMillis()));
	sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
	String today = sdf.format(new Date(System.currentTimeMillis()));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Facturación FONDEN</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="-1">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<link rel="stylesheet" type="text/css"
	href="Ayudas/css/autocompleta.css"></link>
<style>
.ui-autocomplete {
	max-height: 100px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
}

/* IE 6 doesn't support max-height
				     * we use height instead, but this forces the menu to always be this tall
				     */
* html .ui-autocomplete {
	height: 100px;
}
</style>

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript"
	src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/ContabilidadCentroContable.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/Poliza.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/jquery.maskedinput.min.js"></script>
<script type="text/javascript" src="js/jquery.validate-1.9.0.js"></script>
<script type="text/javascript">
	
	$(document).ready(function() {
						format();
						setValidations();
						loading();								
					});
	
	function setValidations(){
		$('#formFondenFacturacion')
								.validate(
										{ // initialize the plugin
											rules : {
												cidFonden : {
													required : true
												},

												nidFondenMovimiento : {
													required : true
												},

												nidFondenFacturacion : {
													required : true
												},

												cnumero : {
													required : true
												},

												ncantidad : {
													required : true,
													number : true
												},

												nimporteFactura : {
													required : true													
												},

												nTipoCambio : {
													required : true													
												},

												nIdTipoPago : {
													required : true													
												},

												nIdTipoFactura : {
													required : true													
												},

												cDescripcionFactura : {
													required : false													
												}																									

											},
											messages : {
												cidFonden : {
													required : "<li style=\"color:red\">El Año es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nidFondenMovimiento : {
													required : "<li style=\"color:red\">El nidFondenMovimiento es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nidFondenFacturacion : {
													required : "<li style=\"color:red\">El nidFondenFacturacion es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												cnumero : {
													required : "<li style=\"color:red\">El Número es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												ncantidad : {
													required : "<li style=\"color:red\">La cantidad es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nimporteFactura : {
													required : "<li style=\"color:red\">El Importe de la factura es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nTipoCambio : {
													required : "<li style=\"color:red\">El Tipo de cambio es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nIdTipoPago : {
													required : "<li style=\"color:red\">El Tipo de pago es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nIdTipoFactura : {
													required : "<li style=\"color:red\">El Tipo de facturación es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												cDescripcionFactura : {
													required : "<li style=\"color:red\">La decripción es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												}												
											},submitHandler: function(form) {submitForm();}
										});
	}
	
	function format() {
		$("#nimporteFactura").toNumber().formatCurrency();		
	};
	
		

		
	function loading() {
		$('#loadingDiv').hide() // hide it initially
		.ajaxStart(function() {
			$("body").addClass("loading");
			$(this).show();
		}).ajaxStop(function() {
			$("body").removeClass("loading");
			$(this).hide();
		});
	}
	
	function submitForm() {
		$.ajaxSetup({async: false});
		$.post("../FondenFacturacion/saveFondenFacturacion", $(
				"#formFondenFacturacion").serialize(), function(data) {							
			alert(data[0].descripcion);					
		}, "JSON");		
		 try {
        		window.opener.refreshFacturacion();
        		window.opener.refreshFacturacion();
        		window.close();
	    } catch (err) {
	        alert(err.description || err); //or console.log or however you debug
	    }
			
	};
	
</script>
</head>
<body id="dt_example">
	<div id="loadingDiv" class="modal"><img src="imagenes/wait24trans.gif"></div>
	<div id="container" class="container SyCData"
		style="width:800px; align:center">
		<fieldset>
			<legend>FONDEN Movimiento - Facturación</legend>
			<form id="formFondenFacturacion">
				<table class="display">

					<tr>
						<td align="right">Año : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getCidFonden() : cidFonden%>"
							name="cidFonden" type="text" id="cidFonden" size="25" 
							readonly="readonly"/></td>
					</tr>
					<tr style="display:none">
						<td align="right">Caso : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getNidFondenMovimiento() : nidFondenMovimiento%>"
							name="nidFondenMovimiento" type="text" id="nidFondenMovimiento"
							readonly="readonly"
							size="25" /></td>
					</tr>
					<tr>
						<td align="right">ID : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getNidFondenFacturacion() : nidFondenFacturacion%>"
							name="nidFondenFacturacion" type="text" id="nidFondenFacturacion"
							readonly="readonly"
							size="25" /></td>
					</tr>
					<tr>
						<td align="right">Número : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getCnumero() : ""%>"
							name="cnumero" type="text" id="cnumero" size="25" /></td>
					</tr>
					<tr>
						<td align="right">Cantidad : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getNcantidad() : ""%>"
							name="ncantidad" type="text" id="ncantidad" size="25" /></td>
					</tr>
					<tr>
						<td align="right">Importe de la factura : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getNimporteFactura() : ""%>"
							name="nimporteFactura" type="text" id="nimporteFactura" size="25"
							onclick="format()" onblur="format()" />
						</td>
					</tr>
					<tr style="display:none">
						<td align="right">Tipo de cambio : </td>
						<td align="left"><input
							value="0.0"
							name="nTipoCambio" type="text" id="nTipoCambio" size="25"
							/>
						</td>
					</tr>
					<tr>
						<td align="right">Tipo de pago : </td>
						<td align="left">
						<select name="nIdTipoPago" id="nIdTipoPago">
							<c:forEach items="<%= fondenTipoPagos %>" var="var">
								<% FondenTipoPago tipoPago = (FondenTipoPago)pageContext.getAttribute("var"); %>
								<option 
									value="<%=tipoPago.getNidTipoPago()%>"
									<%=fondenFacturacion!=null && tipoPago.getNidTipoPago() == fondenFacturacion.getnIdTipoPago()?"selected=\"selected\"":"" %>>
										<%=tipoPago.getCdescripcion() %>
								</option>						
							</c:forEach>
						</select>						
						</td>
					</tr>
					<tr>
						<td align="right">Tipo de factura : </td>
						<td align="left">
						<select name="nIdTipoFactura" id="nIdTipoFactura">
							<c:forEach items="<%= fondenTipoFacturas %>" var="var">
								<% FondenTipoFactura tipoFactura = (FondenTipoFactura)pageContext.getAttribute("var"); %>
								<option 
									value="<%=tipoFactura.getNidTipoFactura()%>"
									<%=fondenFacturacion!=null && tipoFactura.getNidTipoFactura() == fondenFacturacion.getnIdTipoFactura()?"selected=\"selected\"":"" %>>
										<%=tipoFactura.getCdescripcion() %>
								</option>						
							</c:forEach>
						</select>
						</td>
					</tr>
					<tr>
						<td align="right">Descripción : </td>
						<td align="left"><input
							value="<%=fondenFacturacion != null ? fondenFacturacion.getcDescripcionFactura() : ""%>"
							name="cDescripcionFactura" type="text" id="cDescripcionFactura" size="25"
							/>
						</td>
					</tr>				
					<tr>
						<td></td>
						<td><input type="submit" value="Guardar" />
						</td>
					</tr>
				</table>
			</form>
		</fieldset>
	</div>	
</body>
</html>
