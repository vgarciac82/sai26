<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
	
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@page import="com.syc.contable.core.AdecuacionManager"%>



<%	
    Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
    String cCentroContable=(u.getPropiedad("CCENTROCONTABLE") != null? (u.getPropiedad("CCENTROCONTABLE").getValor() != null? u.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );
    Calendar c = Calendar.getInstance();
%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"	content="text/html; charset=ISO-8859-1">
		<title>DIM</title>
		
		<link href="../admin/js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css" rel="stylesheet">
		<link href="../Generador/css/demo_page.css" rel="stylesheet">
		<link href="../Generador/css/demo_table_jui.css" rel="stylesheet">
		<link href="../css/menuContabilidad.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>
		
		<script type="text/javascript" src="../admin/js/jq9/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../admin/js/jq9/jquery-ui-1.9.0.custom.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
		
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
		
		<script type="text/javascript">
			var dTable;
			var reportType="DIM";
			var reportTitle="layoutDim";
			var ejercicioFiscal;
			var currentYear="<%=c.get(Calendar.YEAR)%>";
			var meses =new Array("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
		
            
            $(document).ready( function(){	
				$("#generar").button();
				querySelectPost("catalogoEjercicioFiscalRead","aEjercicioFiscal", {async: false });
				ejercicioFiscal=document.getElementById("aEjercicioFiscal").value;
			
				var select1 = document.getElementById("mesi");
				var select2 = document.getElementById("mesf");
				var limite=0;
				
				
				if(ejercicioFiscal<currentYear) {
					limite=11;
				} else	{
					limite=<%=c.get(Calendar.MONTH)%>
				}
				
	            for(var i=0 ;i<=limite;i++) {
		            select1.options[select1.options.length] = new Option(meses[i], i);
		            select2.options[select2.options.length] = new Option(meses[i], i);
	            }
				select2.value=limite;
			
			});
	
			
	
	function generarReporte() {
	    var url;
		url = "../reports/CuentaPublica?isSP=true" + "&reportType=" + reportType + "&condicion=" + $("#mesi").val()+"&condicion="+$("#mesf").val()+ "&condicion="+ $("#cCentroContable").val();
		var ventimp = window.open(url, "popacuse","resizable=no,scrollbars=yes,toolbar=yes,directories=no,status=no,menubar=no,copyhistory=no, width=500,height=350");
	}
	
	function verificaMeses() {
			var inicial=parseInt($("#mesi").val());
			var fin=parseInt($("#mesf").val());
			
			if(inicial>fin)			{
				$("#mesi").val("0");			
				alert("El mes inicial no puede ser mayor al mes final");
			}
	
	}		
			
		</script>
	</head>
	<body id="dt_example">
	<br/>
	   	<form id="mainForm">		  
			<div id="container" class="container" style="width: 60%">
				<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
				
				<div class="card-header"> <h3> Generaci&oacute;n de Layout DIM </h3> </div>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="mesi" class="form-label"> Mes Inicial: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="mesi" name="mesi" class="form-select form-select-sm" onchange="verificaMeses();"></select>							
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="mesf" class="form-label"> Mes Final: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="mesf" name="mesf" class="form-select form-select-sm" onchange="verificaMeses();"></select>							
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="aEjercicioFiscal" name="aEjercicioFiscal" class="form-select form-select-sm" style="visibility: hidden">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected="selected">--</option>
						</select>							
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12" >
						<input type="button" id="generar" name="generar" value="Generar Layout" class="btn btn-secondary btn-sm" onclick="generarReporte()"/>
					</div>
				</div>

		  </div>
		</form>
		
		
	</body>
</html>