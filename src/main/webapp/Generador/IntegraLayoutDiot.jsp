<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
	
%>

<%	
    Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
    String cCentroContable=(u.getPropiedad("CCENTROCONTABLE") != null? (u.getPropiedad("CCENTROCONTABLE").getValor() != null? u.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );
    Calendar c = Calendar.getInstance();
%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>DIOT</title>
		
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
			
			var reportType="DIOT";
			var reportTitle="layoutDiot";
			var ejercicioFiscal;
			var currentYear="<%=c.get(Calendar.YEAR)%>";
			var meses =new Array("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
		            
       $(document).ready(function(){  	       	   
			querySelectPost("catalogoEjercicioFiscalRead","aEjercicioFiscal", {async: false });
			ejercicioFiscal=document.getElementById("aEjercicioFiscal").value;
			
			var select = document.getElementById("mes");
			var limite=0;
			
			if(ejercicioFiscal<currentYear) {
				limite=11;
			} else	{
				limite=<%=c.get(Calendar.MONTH)%>
			}
			
            for(var i=0 ;i<=limite;i++) {
           		 select.options[select.options.length] = new Option(meses[i], i);
            }
			
			$("#btnGenerar").button();
			$("#btnBuscar").button();
			
			$("#btnBuscar").button();    	
				
		});
	
	function generarReporte() {
	    if (parseInt(ejercicioFiscal,10) >= 2020 )
	    	reportType = "DIOT_v2";
		else if( parseInt(ejercicioFiscal,10) == 2019 && parseInt($("#mes").val(),10) >= 2 )
			reportType = "DIOT_v2";
		else 
			reportType = "DIOT";
		
		var url;	
		url = "../reports/CuentaPublica?isSP=true" + "&reportType=" + reportType + "&condicion=" + $("#mes").val()+ "&condicion=00";//;+ $("#cCentroContable").val();
		
			
		var ventimp = window.open(url, "popacuse","resizable=no,scrollbars=yes,toolbar=yes,directories=no,status=no,menubar=no,copyhistory=no, width=500,height=350");		
	}		
	
	function generarReporteDetalle(){
		var mes = $("#mes").val();
		var mesConsulta = Number(mes) + 1;
						
		Swal.fire({
			  title: '¿Desea continuar?',
			  text: "Estas seguro de cosultar el RFC en el mes " + mesConsulta,
			  icon: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
				  	if($("#RFC").val() == ""){
						Swal.fire({ icon: 'warning',
									text: "Favor de escribir al menos un RFC.." });
						return;
				  	} else {
					  	var rfc =  $("#RFC").val().split(',');
						var filtro = "IN ('"; 
						
						for(var i=0; i<rfc.length; i++){
							if (i == rfc.length - 1)
								filtro += rfc[i] + "')";
							else
								filtro += rfc[i] + "','";
						}		
						
						$("#filtro").val(filtro);
						document.mainForm.submit();
				  	}
			  } 
			})
				
	}
			
		</script>
	</head>
	<body id="dt_example">
	<br/>
		<form id="mainForm" name="mainForm" action="../reportes/ReporteDetalleDIOT" method="get">		  
			<div id="container" class="container" style="width: 60%">
				<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
				<input type="hidden" id="filtro" name="filtro" value=""/>
				
				<div class="card-header"> <h3> Generaci&oacute;n de Layout DIOT </h3> </div>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="mesi" class="form-label"> Mes: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="mes" name="mes" class="form-select form-select-sm"></select>							
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
						<input type="button" id="btnGenerar" name="btnGenerar" value="Generar Layout" class="btn btn-secondary btn-sm" onclick="generarReporte()"/>
					</div>
				</div>
				
				<br/>
				<br/>
			
				<h5> Consultar pagos por RFC</h5>
				<hr class="mt-3"/>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">		
								<label for="RFC" class="form-label"> Captura el RFC de la DIOT: </label>	
								<br/>
								<label for="RFC" class="form-label"> Cada RFC debe estar separado por una coma(,) </label>	
								<br/>
								<label for="RFC" class="form-label"> <p class="text-danger">Selecciona el mes de consulta de la DIOT a buscar su detalle</p> </label>								
								<textarea id="RFC" name="RFC" rows="1" class="form-control form-control-sm" placeholder="RFC1,RFC2"></textarea>																						
							</div>			
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" class="btn btn-secondary btn-sm" onclick="generarReporteDetalle()"/>									
							</div>																					
						</div>
					</div>
				</div>
									
		  </div>
		</form>		
		
	</body>
</html>