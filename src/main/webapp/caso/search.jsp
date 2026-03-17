<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="java.util.Map,java.util.Iterator,com.syc.gestion.CasoBusinessLogic,com.syc.gestion.core.TipoCaso,com.syc.gestion.core.Usuario,com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Grupo"%>

<%
    Usuario u = (Usuario) session
					.getAttribute(GestionInterface.ATT_USER);
			if (u == null) {
				response.sendRedirect("../index.jsp");
				return;
			}
			CasoBusinessLogic ct = new CasoBusinessLogic("jdbc/gestion");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Búsqueda de Casos</title>

<!-- Estilos estandar para los controles JQuery -->

<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	$(document).ready(function() {

		$("#Consultar").button();
		$("#estatusAdec").hide();		
		$("#tdRFC").hide();
		
		if ($("#gavetaAsociada").val() == 'ADECUACION') {
			$("#estatus").hide();
			$("#estatusAdec").show();
		}

		$("#gavetaAsociada").change(function() {
			if ($("#gavetaAsociada").val() == 'ADECUACION') {
				$("#estatus").hide();
				$("#estatusAdec").show();
			} else if ($("#gavetaAsociada").val() == 'PROVEEDORES') {
				$("#tdRFC").show();
				$("#tdDocumento").hide();
				$("#trImporte").hide();
				$("#trFolioSICOP").hide();
				$("#trFolioMAP").hide();
				$("#trOperador").hide();
			} else {
				$("#estatus").show();
				$("#estatusAdec").hide();
				$("#tdRFC").hide();
				$("#tdDocumento").show();
				$("#trImporte").show();
				$("#trFolioSICOP").show();
				$("#trFolioMAP").show();
				$("#trOperador").show();
			}
		});
	});

	function enviaConsulta() {
		$("#fDesde").val($("#fechaD").val().split('-').reverse().join('/'));
		$("#fHasta").val($("#fechaA").val().split('-').reverse().join('/'));
		
		$("#estatus").val($("#estatusAdec").val());
		if ($("#noFolio").val() != "" || $("#documento").val() != ""
				|| $("#operador").val() != "" || $("#estatus").val() != ""
				|| $("#importe").val() != "" || $("#folioSICOP").val() != ""
				|| $("#folioMAP").val() != ""
				|| ($("#fDesde").val() != "" && $("#fHasta").val() != "")) {
			document.consulta.submit();
		} else {
			Swal.fire({ icon: "warning",
						text: "Llenar por lo menos un criterio de búsqueda ademas del trámite, en caso de utilizar fechas seleccionar ambas."});			
		}
	}

	function validaFechas() {
		$("#fDesde").val($("#fechaD").val().split('-').reverse().join('/'));
		$("#fHasta").val($("#fechaA").val().split('-').reverse().join('/'));
		
		if (comparaFechas("fDesde", "fHasta") <= 0) {
			Swal.fire({ icon: "warning",
						text: "La Fecha final debe ser mayor a la inicial."});			
			$("#fHasta").val("");
			document.getElementById("fHasta").focus();

			return false;
		}
		return true;
	}

	function comparaFechas(fecha1, fecha2) {
		var str1 = document.getElementById(fecha1).value;
		var str2 = document.getElementById(fecha2).value;

		var dt1 = parseInt(str1.substring(0, 2), 10);
		var mon1 = parseInt(str1.substring(3, 5), 10) - 1;
		var yr1 = parseInt(str1.substring(6, 10), 10);

		var dt2 = parseInt(str2.substring(0, 2), 10);
		var mon2 = parseInt(str2.substring(3, 5), 10) - 1;
		var yr2 = parseInt(str2.substring(6, 10), 10);

		var date1 = new Date(yr1, mon1, dt1);
		var date2 = new Date(yr2, mon2, dt2);

		if (date2 >= date1)
			return 1;
		else if (date2 < date1)
			return -1;
		else
			// if (date2 == date1)
			return 0;
	}
</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="consulta" name="consulta" action="inboxC.jsp">
		<input type="hidden" id="fDesde" name="fDesde"/>
		<input type="hidden" id="fHasta" name="fHasta"/>
	
		<div id="container" class="container">
			<div class="card-header"> <h3> Consulta de Documentos </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Tramite:</label>											
				</div>					
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<SELECT id="gavetaAsociada" name="gavetaAsociada" class="form-select form-select-sm">
							<%
							    String tipo = "";
							    //Map m = ct.getAllTipoCaso(u.getLogin());
							    Map m = u.getGrupos();
							    for (Iterator iter = m.keySet().iterator(); iter.hasNext();) {
									String name = (String) iter.next();
									Grupo grupo = (Grupo) m.get(name);

									if (grupo.getNombre().startsWith("CONSULTA_")) {
							%>
							<OPTION VALUE="<%=grupo.getNombre().substring(grupo.getNombre().lastIndexOf("_") + 1)%>"><%=grupo.getDescripcion()%></OPTION>
							<%
							    	}
							    }
							%>
					</SELECT>					
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Folio:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-123"></i></span>
						<input type="text" id="noFolio" name="noFolio" value="" class="form-control form-control-sm" style="text-transform: uppercase" />
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Fecha: </label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">
						de &nbsp; <input type="date" id="fechaD" name="fechaD" value="" class="form-control form-control-sm" />
					</div>					
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">
						a &nbsp; <input type="date" id="fechaA" name="fechaA" value="" class="form-control form-control-sm" />
					</div>					
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" id="tdDocumento">
					<label class="form-label">Documento:</label>												
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" id="tdRFC">					
					<label class="form-label">RFC:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-tag"></i></span>
						<input type="text" id="documento" name="documento" value="" class="form-control form-control-sm" style="text-transform: uppercase" placeholder="10CP2022110001" />
					</div>	
				</div>
			</div>
			
			<div class="row d-flex" id="trOperador">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Operador:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="operador" name="operador" value="" class="form-control form-control-sm" style="text-transform: uppercase"  />
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Estatus:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" id="estatus" name="estatus" value="" style="text-transform: uppercase" class="form-control form-control-sm"/>
					<select id="estatusAdec" name="estatusAdec" class="form-select form-select-sm">
						<option value=""></option>
						<option value="CAPTURA">Captura</option>
						<option value="REVIS">Revisión</option>
						<option value="SOLICITUD">Solicitud</option>
						<option value="HACIENDA">Trámite de Autorización a SHCP</option>
						<option value="CANCELADO">Cancelado</option>
						<option value="AUTORIZADO">Autorizado</option>
					</select>	
				</div>
			</div>
			
			<div class="row d-flex" id="trImporte"> 	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Importe:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
						<input type="text" id="importe" name="importe" value="" class="form-control form-control-sm" style="text-transform: uppercase" placeholder="0.00"/>
					</div>	
				</div>
			</div>
			
			<div class="row d-flex" id="trFolioSICOP">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Folio SICOP:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-123"></i></span>
						<input type="text" id="folioSICOP" name="folioSICOP" value="" class="form-control form-control-sm" style="text-transform: uppercase"/>
					</div>	
				</div>
			</div>
			
			<div class="row d-flex" id="trFolioMAP">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Folio MAP:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-123"></i></span>
						<input type="text" id="folioMAP" name="folioMAP" value="" class="form-control form-control-sm" style="text-transform: uppercase"/>
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">															
				</div>						
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" value="Consultar" id="Consultar" name="Consultar" onClick="enviaConsulta();" />											
				</div>							
			</div>
		
		</div>
	</form>
</body>
</html>