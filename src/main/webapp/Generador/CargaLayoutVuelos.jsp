<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	} 
	
	String esPago = request.getParameter("pago");
	String cFolioVuelo = request.getParameter("folioPago");
	
	boolean esRespuesta = request.getParameter("RESPUESTA") != null ? true: false;	
	
	String nFolioPago = "";
	String cCentroContable = "";
	String mImportePago = "";
	String ulogin = "";
	String nFolioVuelos = "";
	
	
	
	if("N".equals(esPago)){	
		if (u.getPropiedades() != null && u.getPropiedades().containsKey("CCENTROCONTABLE")) {
			cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
		}
		
		if("*".equals(cFolioVuelo)){
			nFolioPago = "0";
		}else{				
			nFolioPago = request.getParameter("folioPago");
		}		
		mImportePago = "1.00";
		ulogin = u.getLogin();
	}else{		
		nFolioPago = request.getParameter("folioPago");
		cCentroContable = request.getParameter("centroContable");
		mImportePago = request.getParameter("importePago");
		ulogin = request.getParameter("ulogin");
		esPago = "S";
	}
	
	
	String msg = (String)session.getAttribute("RESULT");
	session.removeAttribute("RESULT");
	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Carga de Layout de Boletaje.</title>

	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">	

	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>

	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
	<script type="text/javascript">
			
		var nFolioPago = "<%=nFolioPago%>";
		var cCentroContable = "<%=cCentroContable%>";
		var mImportePago = "<%=mImportePago%>";
		var ulogin = "<%=ulogin%>";
		var esPago = "<%=esPago%>";
		var nFolioVuelos = "<%=nFolioVuelos%>";
		
		$(document).ready(function() {		
			$("#mensaje").hide();					
			$("#cEsPago").val(esPago);			 
			$("#nFolioPagoDiversoVuelos").val(nFolioPago);
			$("#cCentroContableVuelos").val(cCentroContable); 
			$("#uLoginVuelos").val(ulogin); 
			$("#mImporteTotalVuelos").val(mImportePago);
			$("#nFolioVuelos").val(nFolioVuelos);
					
			
			if( <%=esRespuesta%> ){
				$("#cargaDiv").hide();
				$("#mensaje").show();
			}		
			
		});
		
		var es_mx = {
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
		
		};		
		
		
		
		function cargarArchivoBoletaje(){
		
			var file = $("#flBoletaje").val();
			if(file == "" ){
				alert("Por favor seleccione el archivo de carga.");
				return;		
			}		   
			
			var archivo = (file.substring(file.lastIndexOf('\\')));
			var arr =  archivo.split('.');
			var nameFile = arr[0];
			var ext = arr[1];
			
			if( ext != "csv" ){
				alert("Solo se permiten archivos CSV. Por favor verifique.");
				return;
			}
			
			if(confirm("Esta Seguro De Cargar El Archivo de Pago de Boletaje Aereo.")){		
				
				$("#cNameFile").val(nameFile);
				$.blockUI({message: "Procesando espere ......"});
				$("#layoutForm").submit();
			}
		}
		
		
	</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="layoutForm" action="../layouts/cargaLayoutVuelos" method="POST" enctype="multipart/form-data">
		<input type="hidden" id="nFolioPagoDiversoVuelos" name="nFolioPagoDiversoVuelos" value="">
		<input type="hidden" id="cCentroContableVuelos" name="cCentroContableVuelos" value="">
		<input type="hidden" id="uLoginVuelos" name="uLoginVuelos" value="">
		<input type="hidden" id="mImporteTotalVuelos" name="mImporteTotalVuelos" value="">
		<input type="hidden" id="cNameFile" name="cNameFile" value="">
		<input type="hidden" id="cEsPago" name="cEsPago" value="">
		<input type="hidden" id="nFolioVuelos" name="nFolioVuelos" value="">
		<div id="container" class="ms-5" class="container" style="width: 80%"><!--Inicia div container-->		
			<div id="cargaDiv">
				<div class="row" >
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<input type="file" id="flBoletaje" name="flBoletaje" class="form-control" />
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1 d-flex justify-content-center">
						<input type="button" class="btn btn-primary" id="btnCargaBoletaje" name="btnCargaBoletaje" onclick="cargarArchivoBoletaje()" value="Cargar" />
					</div>
				</div>
			
			</div>

	    	<div id="mensaje">
		      	<div class="row">
					<div class="col-12">
						<p><%=msg%></p> 
					</div>
				</div>
	    	</div>
	      	
			</div>
		</div>		
	</form>
</body>
</html>
