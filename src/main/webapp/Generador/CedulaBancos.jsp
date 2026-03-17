<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@page import="java.text.DecimalFormat"%>
<%
	String cCentroContable = "";
	String algo = "";
 	 	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	String mensaje = "";
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	} 

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado, Consulte a su administrador.";
	}

	algo = usuario.getLogin();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Consulta Saldo</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"/>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">		
	$(document).ready(function() {	
	 	$("#pbExcel").button();
	});
	function enviaConsulta() {
		 queryFormPost("obtenerEjercicioActual", {async: false});
		
		 $("#nMes").val(  $("#mes").val());
		
		 document.ExportaCedula.submit();
	}
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportaCedula" name="ExportaCedula" action="../reportes/CedulaBancos" method="get">
		<div id="container" style="width: 80%" class="container">
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" />
			<input  type="hidden" id="nMes" name="nMes" value="" />
			<input  type="hidden" id="nAnio" name="nAnio" value="" />
						
			<div class="card-header"> <h3> Cedula Conciliacion Bancaria </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex justify-content-center">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex justify-content-center">
					<label class="form-label">Mes:&nbsp;&nbsp; </label>
					<select name = "mes" id = "mes" class="form-select form-select-sm">
						<option value=1>Enero</option>
						<option value=2>Febrero</option>
						<option value=3>Marzo</option>
						<option value=4>Abril</option>
						<option value=5>Mayo</option>
						<option value=6>Junio</option>
						<option value=7>Julio</option>
						<option value=8>Agosto</option>
						<option value=9>Septiembre</option>
						<option value=10>Octubre</option>
						<option value=11>Noviembre</option>
						<option value=12>Diciembre</option>
					</select>
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 d-flex justify-content-center">
					<input id="pbExcel" name="pbExcel" type="button" class="btn btn-secondary btn-sm" value="Extraer" onclick="enviaConsulta()"/>
				</div>
			</div>
			
					
		</div>
	</form>

</body>
</html>


