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
<%@page import="com.syc.sai.fonden.Fonden"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="java.util.Calendar"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<%!private Logger log = LoggerFactory.getLogger(getClass());%>

<%
	Calendar calendar = Calendar.getInstance();
	int year = calendar.get(Calendar.YEAR);
	FondenBusinessLogic fbl = new FondenBusinessLogic();
	List<Fonden> fondens = FondenManager.readFonden(fbl.getConnection(), year);
	Fonden fonden = null;
	boolean isNew = true;
	if(!fondens.isEmpty()){
		fonden = (Fonden) (fondens.get(0));
		isNew = false;
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	String currentYear = sdf.format(new Date(System.currentTimeMillis()));
	sdf = new SimpleDateFormat("yyyy-dd-MM");
	String today = sdf.format(new Date(System.currentTimeMillis()));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>FONDEN</title>

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

.modal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('http://i.stack.imgur.com/FhHRx.gif') 
                50% 50% 
                no-repeat;
}

/* When the body has the loading class, we turn
   the scrollbar off with overflow:hidden */
body.loading {
    overflow: hidden;   
}

/* Anytime the body has the loading class, our
   modal element will be visible */
body.loading .modal {
    display: block;
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
						loading();
						setValidations();					
					});
					
	function update() {
		queryFormPost({
			queryName : "updateFonden",
			async : false
		});
	};

	function save() {
		queryFormPost({
			queryName : "insertFonden",
			async : false
		});
	};
	
	
	function setValidations() {
		$('#formFonden')
				.validate(
						{ // initialize the plugin
							rules : {
								cidFonden : {
									required : true,
									minlength : 4,
									maxlength : 4,
									digits : true
								},

								cdescripcion : {
									required : true,
									maxlength : 150
								},

								nimporteAnual : {
									required : true
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

								dfechaCaptura : {
									required : "<li style=\"color:red\">El dfechaCaptura es un dato requerido.</li>",
									minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
									maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
									email : "<li style=\"color:red\">Email invalido.</li>",
									date : "<li style=\"color:red\">fecha invalida.</li>",
									number : "<li style=\"color:red\">Debe ser numérico.</li>",
									digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
								},

								cdescripcion : {
									required : "<li style=\"color:red\">La descripción es un dato requerido.</li>",
									minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
									maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
									email : "<li style=\"color:red\">Email invalido.</li>",
									date : "<li style=\"color:red\">fecha invalida.</li>",
									number : "<li style=\"color:red\">Debe ser numérico.</li>",
									digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
								},

								nimporteAnual : {
									required : "<li style=\"color:red\">El Importe anual es un dato requerido.</li>",
									minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
									maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
									email : "<li style=\"color:red\">Email invalido.</li>",
									date : "<li style=\"color:red\">fecha invalida.</li>",
									number : "<li style=\"color:red\">Debe ser numérico.</li>",
									digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
								}

							},
							submitHandler : function(form) {
								submitForm();
							}
						});
	}

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

	function format() {
		$("#nimporteAnual").toNumber().formatCurrency();
	}

	function submitForm() {
		$.post("../Fonden/saveFonden", $("#formFonden").serialize(), function(
				data) {
			alert(data[0].descripcion);
		}, "JSON");
	}
	
	$(function() {
		$( "#dfechaCaptura" ).datepicker({
			showOn: "button",
			dateFormat: "yy-mm-dd",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	
</script>
</head>
<body id="dt_example">
	<div id="container" class="container SyCData"
		style="width:800px; align:center">
		<fieldset>
			<legend>Captura Anual FONDEN</legend>
			<form id="formFonden">
				<table class="display">
					<tr>
						<td align="right">Año:</td>
						<td align="left"><input
							value="<%=fonden != null ? fonden.getCidFonden() : currentYear%>"
							name="cidFonden" readonly="readonly" type="text" id="cidFonden" size="25" /></td>
					</tr>
					<tr style="display:none">
						<td align="right">Usuario :</td>
						<td align="left"><input
							value="<%=fonden != null ? fonden.getCusuarioCreador() : ""%>"
							name="cusuarioCreador" readonly="readonly" type="text" id="cusuarioCreador" size="25" />
						</td>
					</tr>
					<tr>
						<td align="right">Fecha de captura:</td>
						<td align="left"><input
							value="<%=fonden != null ? fonden.getDfechaCaptura().toString() : today%>"
							name="dfechaCaptura" type="text" id="dfechaCaptura"							
							readonly="readonly" size="10" /></td>
					</tr>
					<tr>
						<td align="right">Descripción:</td>
						<td align="left"><input
							value="<%=fonden != null ? fonden.getCdescripcion() : ""%>"
							name="cdescripcion" type="text" id="cdescripcion" size="30" maxlength="50" /></td>
					</tr>
					<tr>
						<td align="right">Importe anual:</td>
						<td align="left"><input 
							value="<%=fonden != null ? fonden.getNimporteAnual() : ""%>"
							name="nimporteAnual" type="text" id="nimporteAnual" size="20"
							onchange="format()"/>
						</td>
					</tr>
					<tr>
						<td align="right"></td>
						<td align="left"><input value="Guardar" type="submit"							
							id="guardar" /></td>
					</tr>
				</table>
			</form>
		</fieldset>
	</div>
	<div id="loadingDiv" class="modal"><img src="imagenes/wait24trans.gif"></div>
</body>
</html>
