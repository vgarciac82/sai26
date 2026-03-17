<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%
	//*****************************INSERT OPCIÓN MENÚ****************************************//
	//insert into CG_OPCION values(' - Avisos de Reintegro y Oficios de Rectificación','S','<a href="../plantillasCasos/ReporteReinRect.jsp" onclick="actionForm('CONTEXT_PATH','',this.id); toggleVerMain('none','smallCell', 'apMenu');" target="content-iframe" title="Reintegros y Rectificaciones" class="Menus" name="menu" id="mnu_rep_rein_rect">Avisos de Reintegro y Oficios de Rectificación</a>',NULL,10,200980,3)
	//insert into CG_ROLE_OPCION values('REINTEGROS',308,NULL)
	//****************************************************************************************
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String cUR = usuario.getU_UR();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Reporte Avisos de Reintegro y Oficios de Rectificación</title>

<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/ui/jquery.ui.accordion.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>

<script type="text/javascript" charset="utf-8">
	var muestraAplicar = true;
	var clickEnviar = false;

	function onPostSubmit(id_oper) {//validaciones del boton enviar
		return true;
	}

	function onPostDisplay(id_oper) {
	}

	function onSubmit(id_oper) {//validaciones del boton guardar
	}

	function onLoadPlantilla(id_oper) {

	}

	function transformDate(fecha) {
		var date = fecha.split("/");
		return date[2] + "-" + date[1] + "-" + date[0];
	}

	$(document).ready(function() {
		$("#remp").hide();
		$("#btnBuscar").button();
		$("#btnExportar").button();
		$("#cxpnomina").attr('disabled', 'disabled');
		$("#tipoconcepto").attr('disabled', 'disabled');
		$("#tipomovimiento").attr('disabled', 'disabled');
		$("#tramite").change(function() {
			replaceTable();
			if ($("#tramite").val() == 2 || $("#tramite").val() == 4) {
				$("#lCaptura").attr('disabled', 'disabled');
				$("#guia").attr('disabled', 'disabled');
				$("#ficha").attr('disabled', 'disabled');
				$("#tipoRein").attr('disabled', 'disabled');
			}
			if ($("#tramite").val() == 1 || $("#tramite").val() == 3) {
				$("#lCaptura").attr('disabled', false);
				$("#guia").attr('disabled', false);
				$("#ficha").attr('disabled', false);
				$("#tipoRein").attr('disabled', false);
			}
			if ($("#tramite").val() == 1 || $("#tramite").val() == 2) {
				$("#          ").attr('disabled', 'disabled');
				$("#tipoconcepto").attr('disabled', 'disabled');
				$("#tipomovimiento").attr('disabled', 'disabled');
			}
			if ($("#tramite").val() == 3 || $("#tramite").val() == 4) {
				$("#cxpnomina").attr('disabled', false);
				$("#tipoconcepto").attr('disabled', false);
				$("#tipomovimiento").attr('disabled', false);
			}
		});

	}); //fin del ready

	function cmdRegresar() {
		self.location = "../caso/principal.jsp";
	}

	function createInput(form, name, value) {
		$('<input>').attr({
			type : 'hidden',
			name : name,
			value : value
		}).addClass('remove').appendTo('#' + form);
	}

	function borraElementos() {
		$('.remove').remove();
	}

	function generaInputs() {
		borraElementos();
		var arrData = $("#tblinformacion").dataTable().fnGetData();

		if ($("#tramite").val() == 1) {
			for ( var i = 0; i < arrData.length; i++) {
				createInput('generaExcel', 'folio', arrData[i][0]);
				createInput('generaExcel', 'clc', arrData[i][1]);
				createInput('generaExcel', 'ep', arrData[i][2]);
				createInput('generaExcel', 'importe', arrData[i][3]);
				createInput('generaExcel', 'cxp', arrData[i][4]);
				createInput('generaExcel', 'mes', arrData[i][5]);
				createInput('generaExcel', 'lc', arrData[i][6]);
				createInput('generaExcel', 'clv', arrData[i][7]);
				createInput('generaExcel', 'ficha', arrData[i][8]);
			}
		}
		if ($("#tramite").val() == 2) {
			for ( var i = 0; i < arrData.length; i++) {
				createInput('generaExcel', 'folio', arrData[i][0]);
				createInput('generaExcel', 'clc', arrData[i][1]);
				createInput('generaExcel', 'ep', arrData[i][2]);
				createInput('generaExcel', 'importe', arrData[i][3]);
				createInput('generaExcel', 'cxp', arrData[i][4]);
				createInput('generaExcel', 'mes', arrData[i][5]);
			}
		}
		if ($("#tramite").val() == 3) {
			for ( var i = 0; i < arrData.length; i++) {
				createInput('generaExcel', 'folio', arrData[i][0]);
				createInput('generaExcel', 'clc', arrData[i][1]);
				createInput('generaExcel', 'ep', arrData[i][2]);
				createInput('generaExcel', 'importe', arrData[i][3]);
				createInput('generaExcel', 'cxp', arrData[i][4]);
				createInput('generaExcel', 'mes', arrData[i][5]);
				createInput('generaExcel', 'lc', arrData[i][6]);
				createInput('generaExcel', 'clv', arrData[i][7]);
				createInput('generaExcel', 'ficha', arrData[i][8]);
				createInput('generaExcel', 'cxpnomina', arrData[i][9]);
				createInput('generaExcel', 'tipoConcepto', arrData[i][10]);
				createInput('generaExcel', 'tipoMovimiento', arrData[i][11]);
			}
		}
		if ($("#tramite").val() == 4) {
			for ( var i = 0; i < arrData.length; i++) {
				createInput('generaExcel', 'folio', arrData[i][0]);
				createInput('generaExcel', 'clc', arrData[i][1]);
				createInput('generaExcel', 'ep', arrData[i][2]);
				createInput('generaExcel', 'importe', arrData[i][3]);
				createInput('generaExcel', 'cxp', arrData[i][4]);
				createInput('generaExcel', 'mes', arrData[i][5]);
				createInput('generaExcel', 'cxpnomina', arrData[i][6]);
				createInput('generaExcel', 'tipoConcepto', arrData[i][7]);
				createInput('generaExcel', 'tipoMovimiento', arrData[i][8]);
			}
		}
		$("#tipotramite").val($("#tramite").val());
		$("#generaExcel").submit();
		return true;
	}

	function informacion() {
		var where = datos();
		if (where == "") {
			Swal.fire({ icon: 'warning',
						text: "Favor de especificar al menos un filtro." });			
			return;
		}
		var vistar = vista();
		var columnas = autorizado();
		$("#remp").show();
		var consulta = window.location.protocol + "//" + window.location.host
				+ "/" + window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=" + vistar + "&qw=" + where;
		oTable = $('#tblinformacion')
				.dataTable(
						{
						  "bLengthChange" : true,
		                   "bFilter" : true,
		                   "bSort" : true,
		                   "bInfo" : true,
		                   "bPaginate" : false,
		                   "bAutoWidth" : false,
		                   "bScrollCollapse" : true,			                   
		           		   "sScrollX": "100%",
		                   "sPaginationType" : "full_numbers",
		                   "bJQueryUI" : true,
		                   "bRetrive" : true,
		                   "bDestroy" : true,
		                   "bServerSide": true,    
							sAjaxSource : consulta,
							aoColumns : columnas,
							"fnDrawCallback" : function(oSettings) {
								if (oSettings.aiDisplay.length == 0) {
									$("#remp").hide();
									Swal.fire({ icon: 'warning',
												text: "No hay información en el sistema con los criterios proporcionados." });									
								}
							},
							oLanguage : {
								sProcessing : "Procesando...",
								sLengthMenu : "Mostrar _MENU_ registros",
								sZeroRecords : "No hay registros a mostrar",
								sEmptyTable : "No hay datos en la tabla",
								sLoadingRecords : "Cargando...",
								sInfo : "Registros _START_ al _END_ de _TOTAL_",
								sInfoEmpty : "Registro 0 al 0 de 0",
								sInfoFiltered : "(filtered from _MAX_ total entries)",
								sInfoPostFix : "",
								sInfoThousands : ",",
								sSearch : "Filtro:",
								oPaginate : {
									sFirst : "Primero",
									sPrevious : "Ant.",
									sNext : "Sigte.",
									sLast : "&Uacute;ltimo"
								}
							}
						});
	}

	function datos() {
		$("#fAutorizacion").val($("#fechaA").val().split('-').reverse().join('/'));
		var where = "";

		if ($("#folio").val() != "") {
			if ($("#tramite").val() == 1) {
				where += "nFolioReintegro=" + $("#folio").val();
			} else if ($("#tramite").val() == 2) {
				where += "nFolioRectificacion=" + $("#folio").val();
			} else if ($("#tramite").val() == 3) {
				where += "nFolioReintegroMil=" + $("#folio").val();
			} else if ($("#tramite").val() == 4) {
				where += "nFolioRectificacionMil=" + $("#folio").val();
			}
		}

		if ($("#ep").val() != "" && where.length != 0)
			where += " AND ";
		if ($("#ep").val() != "")
			where += " ep='" + $("#ep").val() + "'";

		if (($("#autorizado:checked").length == 1 || $("#autorizado:checked").length == 0)
				&& where.length != 0)
			where += " AND ";
		if ($("#autorizado:checked").length == 1)
			where += " cDocumentoHaplicadoAut='S'";
		else if ($("#autorizado:checked").length == 0)
			where += " (cDocumentoHaplicadoAut='N' or cDocumentoHaplicadoAut is NULL)";
		
		if ($("#fAutorizacion").val() != "" && where.length != 0)
			where += " AND ";
		if ($("#fAutorizacion").val() != "")
			where += " fAplicacionAut='"
					+ transformDate($("#fAutorizacion").val()) + "'";
		
		return where;
	}

	function autorizado() {
		var columnas;
		if ($("#autorizado:checked").length == 1 && $("#tramite").val() == 1)
			columnas = [ {
				sName : "nFolioReintegroAut"
			}, {
				sName : "folioSIAFFAut"
			}, {
				sName : "EPAut"
			}, {
				sName : "mImporteAut"
			}, {
				sName : "cxpAut"
			}, {
				sName : "cMesAut"
			}, {
				sName : "lcAut"
			}, {
				sName : "clvRastreoAut"
			}, {
				sName : "fichaDepositoAut"
			} ];
		else if ($("#tramite").val() == 1)
			columnas = [ {
				sName : "nFolioReintegro"
			}, {
				sName : "folioSIAFF"
			}, {
				sName : "EP"
			}, {
				sName : "mImporte"
			}, {
				sName : "cxp"
			}, {
				sName : "cMes"
			}, {
				sName : "lc"
			}, {
				sName : "clvRastreo"
			}, {
				sName : "fichaDeposito"
			} ];

		if ($("#autorizado:checked").length == 1 && $("#tramite").val() == 2)
			columnas = [ {
				sName : "nFolioRectificacionAut"
			}, {
				sName : "folioSIAFFAut"
			}, {
				sName : "EPAut"
			}, {
				sName : "mImporteAut"
			}, {
				sName : "caNoContrarreciboAut"
			}, {
				sName : "cMesAut"
			} ];
		else if ($("#tramite").val() == 2)
			columnas = [ {
				sName : "nFolioRectificacion"
			}, {
				sName : "folioSIAFF"
			}, {
				sName : "EP"
			}, {
				sName : "mImporte"
			}, {
				sName : "caNoContrarrecibo"
			}, {
				sName : "cMes"
			} ];

		if ($("#autorizado:checked").length == 1 && $("#tramite").val() == 3)
			columnas = [ {
				sName : "nFolioReintegroMil"
			}, {
				sName : "folioSIAFFAut"
			}, {
				sName : "EPAut"
			}, {
				sName : "mImporteAut"
			}, {
				sName : "cxpAut"
			}, {
				sName : "cMesAut"
			}, {
				sName : "lcAut"
			}, {
				sName : "clvRastreoAut"
			}, {
				sName : "fichaDepositoAut"
			}, {
				sName : "cxpnominaAut"
			}, {
				sName : "tipoConceptoAut"
			}, {
				sName : "tipoMovimientoAut"
			} ];
		else if ($("#tramite").val() == 3)
			columnas = [ {
				sName : "nFolioReintegroMil"
			}, {
				sName : "folioSIAFF"
			}, {
				sName : "EP"
			}, {
				sName : "mImporte"
			}, {
				sName : "cxp"
			}, {
				sName : "cMes"
			}, {
				sName : "lc"
			}, {
				sName : "clvRastreo"
			}, {
				sName : "fichaDeposito"
			}, {
				sName : "cxpnomina"
			}, {
				sName : "tipoConcepto"
			}, {
				sName : "tipoMovimiento"
			} ];

		if ($("#autorizado:checked").length == 1 && $("#tramite").val() == 4)
			columnas = [ {
				sName : "nFolioRectificacionMilAut"
			}, {
				sName : "folioSIAFFAut"
			}, {
				sName : "EPAut"
			}, {
				sName : "mImporteAut"
			}, {
				sName : "caNoContrarreciboAut"
			}, {
				sName : "cMesAut"
			}, {
				sName : "caNoContrarreciboMilAut"
			}, {
				sName : "tipoConceptoAut"
			}, {
				sName : "tipoMovimientoAut"
			} ];
		else if ($("#tramite").val() == 4)
			columnas = [ {
				sName : "nFolioRectificacionMil"
			}, {
				sName : "folioSIAFF"
			}, {
				sName : "EP"
			}, {
				sName : "mImporte"
			}, {
				sName : "caNoContrarrecibo"
			}, {
				sName : "cMes"
			}, {
				sName : "caNoContrarreciboMil"
			}, {
				sName : "tipoConcepto"
			}, {
				sName : "tipoMovimiento"
			} ];

		return columnas;
	}

	function vista() {
		var vis = "";
		if ($("#tramite").val() == 1)
			vis = "v_Reintegros";
		if ($("#tramite").val() == 2)
			vis = "v_Rectificaciones";
		if ($("#tramite").val() == 3)
			vis = "v_ReintegrosMil";
		if ($("#tramite").val() == 4)
			vis = "v_RectificacionesMil";
		return vis;
	}

	function replaceTable() {
		if ($("#tramite").val() == 1)
			$("#remp")
					.html(
							'<table   class="display" id="tblinformacion" style="text-align: center;">'
									+ '<thead>'
									+ '<tr>'
									+ '<th>Folio</th>'
									+ '<th>noCLC</th>'
									+ '<th>EP</th>'
									+ '<th>mImporte</th>'
									+ '<th>Cuenta Por Pagar</th>'
									+ '<th>Mes</th>'
									+ '<th>Linea Captura</th>'
									+ '<th>Clave Rastreo</th>'
									+ '<th>Ficha</th>'
									+ '</tr>'
									+ '</thead>'
									+ '<tbody>'
									+ '<tr>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center; width:100px;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '</tr>' 
									+ '</tbody></table>');

		else if ($("#tramite").val() == 2)
			$("#remp")
					.html(
							'<table   class="display" id="tblinformacion" style="text-align: center;">'
									+ '<thead>'
									+ '<tr>'
									+ '<th>Folio</th>'
									+ '<th>noCLC</th>'
									+ '<th>EP</th>'
									+ '<th>mImporte</th>'
									+ '<th>Cuenta Por Pagar</th>'
									+ '<th>Mes</th>'
									+ '</tr>'
									+ '</thead>'
									+ '<tbody>'
									+ '<tr>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center; width:100px;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '</tr>' 
									+ '</tbody>' 
									+ '</table>');

		else if ($("#tramite").val() == 3) {
			$("#remp")
					.html(
							'<table   class="display" id="tblinformacion" style="text-align: center;">'
									+ '<thead>'
									+ '<tr>'
									+ '<th>Folio</th>'
									+ '<th>noCLC</th>'
									+ '<th>EP</th>'
									+ '<th>mImporte</th>'
									+ '<th>Cuenta Por Pagar</th>'
									+ '<th>Mes</th>'
									+ '<th>Linea Captura</th>'
									+ '<th>Clave Rastreo</th>'
									+ '<th>Ficha</th>'
									+ '<th>CXP Nomina</th>'
									+ '<th>Tipo Concepto</th>'
									+ '<th>Tipo Movimiento</th>'
									+ '</tr>'
									+ '</thead>'
									+ '<tbody>'
									+ '<tr>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center; width:100px;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '</tr>' 
									+ '</tbody></table>');
		}

		else if ($("#tramite").val() == 4)
			$("#remp")
					.html(
							'<table   class="display" id="tblinformacion" style="text-align: center;">'
									+ '<thead>'
									+ '<tr>'
									+ '<th>Folio</th>'
									+ '<th>noCLC</th>'
									+ '<th>EP</th>'
									+ '<th>mImporte</th>'
									+ '<th>Cuenta Por Pagar</th>'
									+ '<th>Mes</th>'
									+ '<th>CXP Nomina</th>'
									+ '<th>Tipo Concepto</th>'
									+ '<th>Tipo Movimiento</th>'
									+ '</tr>'
									+ '</thead>'
									+ '<tbody>'
									+ '<tr>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center; width:100px;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '<td style="text-align: center;"></td>'
									+ '</tr>' 
									+ '</tbody>' 
									+ '</table>');

		$("#remp").hide();
	}
</script>

</head>
<br/>
<body id="dt_example">
	<div id="container" class="container">
		
		<div class="card-header"> <h3> Reporte Reintegros y Rectificaciones </h3> </div>
		<hr class="mt-3">
		
		<form id="filtros" name="filtros" action="" method="POST">
			<input type="hidden" name="fCaptura" id="fCaptura" />
			<input type="hidden" name="fAutorizacion" id="fAutorizacion" />
			
			<div class="row d-flex">			
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Tipo de Trámite:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select name = "tramite" id = "tramite" class="form-select form-select-sm">	
						<option value="1">Reintegro</option>
						<option value="2">Rectificacion</option>
						<!-- Solo las oficinas centrales pueden ver capitulo mil de acuerdo al requerimiento -->
						<%
							if ("A02".equals(cUR) || "A03".equals(cUR)) {
						%>
						<option value="3">Reintegro Capitulo Mil</option>
						<option value="4">Rectificacion Capitulo Mil</option>
						<%
							}
						%>																				
					</select>	
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Folio:</label>											
				</div>					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-123"></i></span>
						<input type="text" id="folio" name="folio" class="form-control form-control-sm" size="5" maxlength="5" />
					</div>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>						
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">EP:</label>											
				</div>					
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-tag"></i></span>
						<input type="text" id="ep" name="ep" class="form-control form-control-sm" size="70" maxlength="70" />
					</div>
				</div>
			</div>
	
			<div class="row d-flex">	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Autorizado:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">					
					<input type="checkbox" name="autorizado" id="autorizado" class="form-check-input" value = "1" checked>					
				</div>			
			</div>
		
			<div class="row d-flex">	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Fecha Autorización:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<input type="date" id="fechaA" name="fechaA" class="form-control form-control-sm"/>								
				</div>				
			</div>
		
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>						
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" value="Buscar" id="btnBuscar" name="btnBuscar" onClick="informacion();" />											
				</div>							
			</div>
			
			<table>	
				<tr>
					<td></td>
				</tr>
			</table>
		</form>
		
		<br/>
		
		<div id="remp">
			<table class="table table-striped table-sm" id="tblinformacion" style="text-align: center;">
				<thead>
					<tr>
						<th>Folio</th>
						<th>noCLC</th>
						<th style="width: 80px">EP</th>
						<th>mImporte</th>
						<th>Cuenta Por Pagar</th>
						<th>Mes</th>
						<th>Linea Captura</th>
						<th>Clave Rastreo</th>
						<th>Ficha</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center; width:100px;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"></td>
					</tr>
				</tbody>
			</table>
		</div>

		<form id="generaExcel" name="generaExcel" action="../servlet/ReintegrosServlet" method="post">
			<input type="hidden" id="generaExcel" name="generaExcel" value="7" />
			<input type="hidden" id="tipotramite" name="tipotramite" /> 
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">															
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" id="btnExportar" name="btnExportar" value="Exportar Reporte" onclick=" return generaInputs();" />											
				</div>							
			</div>
			
		</form>
	</div>
</body>
</html>
