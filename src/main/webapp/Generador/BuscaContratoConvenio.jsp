<%@page import="com.syc.obrapublica.ObraPublicaContractBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(
			"jdbc/gestion");
	String accion = request.getParameter("accion");
	if ("buscar".equals(accion)) {

	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"content="text/html; charset=ISO-8859-1">
		<title>B&uacute;squeda de Contratos</title>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
		<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"> </script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"> </script>
		<script type="text/javascript" src="js/BuscaContratoConvenio.js"></script>
				
		<script type="text/javascript">
			$(document).ready(function() {
				init();
			});
		</script>

	</head>
	<br/>
	<body id="dt_example">
		<form action="../ConvenioModificatorio" method="post" id="mainForm">			
			<input type="hidden" value="" id="folioSAI" name="folioSAI">
			<input type="hidden" value="CONVENIO_MODIFICATORIO" id="accion" name="accion">
			<input type="hidden" value="" id="OperacionActual" name="OperacionActual">
															
			<div id="container" style="width: 80%" class="container" >
				<div class="card-header"> <h3> Convenio Modificatorio </h3> </div>
				<hr class="mt-3"/>
			
				<h5> Ingrese la siguiente informaci&oacute;n: </h5>
				<hr class="mt-3"/>
								
				<div class="row">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
						<label for="cveContrato"> N&uacute;mero de Contrato: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">																									
						<input type="text" id="cveContrato" size="30" class="form-control form-control-sm">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																									
						<input type="button" id="searachButton" value="Buscar" class="btn btn-secondary btn-sm">
					</div>
				</div>
	
				<br />
				
				<h6> Doble Click en el contrato para iniciar el convenio modificatorio. </h5>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<table id="resultTable" class="table table-striped table-bordered">				
							<thead>
								<tr>
									<th> No. Contrato </th>
									<th> Beneficiario </th>
									<th> Monto </th>
									<th> Folio SAI </th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>
				
				<div id="question" style="display: none; cursor: default">
					<h5> &#191;Desea continuar? </h5>
					
					<br/>
					<div class="row d-flex">								
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							<p> Se agregar&aacute; un convenio modficatorio al contrato seleccionado.</p>
							<hr class="mt-3"/>
						</div>
					</div>					
					
					<input type="button" id="yes" value="Continuar" class="btn btn-secondary btn-sm"/>
					<input type="button" id="no" value="Cancelar" class="btn btn-secondary btn-sm"/>
				</div>
			</div>		
		</form>
	</body>
</html>