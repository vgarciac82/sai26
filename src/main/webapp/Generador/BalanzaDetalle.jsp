<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page	import="java.util.Calendar"%>
<%@page	import="com.syc.gestion.core.Usuario"%>
<%@page	import="com.syc.gestion.servlet.GestionInterface"%>

<%	
    Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
   	if (u == null) {
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}
   	String cCentroContable=(u.getPropiedad("CCENTROCONTABLE") != null? (u.getPropiedad("CCENTROCONTABLE").getValor() != null ? u.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );
	
	//Valida Centro de Costos
	if (u.getPropiedades() != null && u.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
	}

	 Calendar c = Calendar.getInstance();
%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>BALANZA DETALLE</title>
				
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
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		
		<script type="text/javascript">
			var dTable;
			var ejercicioFiscal;
			var currentYear="<%=c.get(Calendar.YEAR)%>";
			var meses =new Array("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");

            $(document).ready(
            
			        function(){	
			        	
			        	$("#cmdImprimir").button();
			        
					        $("#generar").button();
					        $("#consultar").button();
					        $("#generarCsv").button();
					        $("#generarDet").button();
							$("#generarAcu").button();
					        
							querySelectPost("catalogoEjercicioFiscalRead","aEjercicioFiscal", {async: false });
							ejercicioFiscal=document.getElementById("aEjercicioFiscal").value;
							
							var select = document.getElementById("mesi");
							var limite=0;
					
						if(ejercicioFiscal<currentYear) {
							limite=11;
						} else	{
						limite=<%=c.get(Calendar.MONTH)%>
						}
			            
			            for(var i=0 ;i<=limite;i++) {
			            	select.options[select.options.length] = new Option(meses[i], i);
			            }
						
						if(ejercicioFiscal == "2015" ){
							document.getElementById("gdetalle").style.display = "none";
							document.getElementById("acumulado").style.display = "none";
						}
					
						if ( $("#cCentroContable").val()!="10" && $("#cCentroContable").val()!="00" )
							document.getElementById("gdetalle").style.display = "none";
						
					}
			); //FIN DEL READY
				

	
	var reportType="";
	
	function imprimirReporte() {
	
			var inicial=parseInt($("#mesi").val());
			inicial=inicial+1;
			var Ini="";
		    
				switch (inicial)
				{
					case 1:
					 	Ini="ENERO";
					 	break;
					case 2:
					 	Ini="FEBRERO";
					 	break;
					case 3:
						Ini="MARZO";
						break;
					case 4:
						Ini="ABRIL";
						break;
					case 5:
						Ini="MAYO";
						break;
					case 6:
						Ini="JUNIO";
						break;
					case 7:
					 	Ini="JULIO";
					 	break;
					case 8:
					 	Ini="AGOSTO";
					 	break;
					case 9:
						Ini="SEPTIEMBRE";
						break;
					case 10:
						Ini="OCTUBRE";
						break;
					case 11:
						Ini="NOVIEMBRE";
						break;
					case 12:
						Ini="DICIEMBRE";
						break;
				}
			
			var nombreCC;			
			if ( $("#cCentroContable").val()=="00")	
				nombreCC = " a nivel CONSOLIDADO";
			else
				nombreCC = " del Centro Contable : "+$("#cCentroContable").val();
				
				Swal.fire({
					  title: '¿Desea continuar?',
					  text: "El reporte se genera con Informacion del Mes de  "+ Ini + nombreCC,
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  if($("#tipoBalanza").val()=="Contable" &&  $("#MENSUAL").prop("checked"))
								reportType="1";
							else if ($("#tipoBalanza").val()=="Detallado" &&  $("#MENSUAL").prop("checked"))
								reportType="2";
							else if ($("#tipoBalanza").val()=="Contable" &&  $("#ACC").prop("checked"))
								reportType="3";
							else if ($("#tipoBalanza").val()=="Detallado" &&  $("#ACC").prop("checked"))
								reportType="4";
							
							$("#tipoReporte").val(reportType);
							$("#mesInicio").val(inicial);
							document.ExportarForm.submit();	
							
					  } else if (result.dismiss === Swal.DismissReason.cancel) {
						  return false;
					  }
					})
	}
	
	function saldos(){
		$.blockUI({message: "Procesando espere ......"});
		$.ajax({
				url : '../reportes/ReporteEstadosFinancieros',
				dataType : 'json',
				type :"GET",
				data : {
					"mes": "12"
				},
				async : false,
				success : function(json) {
					if( json.valueOf()=="success"){
						Swal.fire("OK","Saldos verificados correctamente","success");
						$.unblockUI();
					}
				},
				error : function (){
					Swal.fire(" No se corrieron los saldos.", "Por favor intente mas tarde.","info");
					$.unblockUI();	
					return false;}
			});
		
	}
				
		</script>
	</head>
	<body id="dt_example">		
	   	<form id="ExportarForm" name="ExportarForm" action="../reportes/Balanza" method="get" target="_blank">		  
			<div id="container" class="container" style="width: 60%">
				<input type="hidden" id="mesInicio"  name="mesInicio" value=" " />
				<input type="hidden" id="tipoReporte"  name="tipoReporte" value=" " />								
				<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
				<select id="aEjercicioFiscal" style="visibility: hidden" name="aEjercicioFiscal" class="form-select form-select-sm">
						<option value="Z:">A</option>
						<option value="Y:">B</option>
						<option value="X:">C</option>
						<option value="xx" selected="selected">--</option>
				</select>
										
				<div class="card-header"> <h3> Balanza de Comprobaci&oacute;n </h3> </div>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12" >
						<input type="button" class="btn btn-primary btn-sm" id="btn_saldo" name="btn_saldo" value="  Verificar Saldos  " onclick="saldos()">
					</div>
				</div>
				
				<br/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="mesi" class="form-label"> Mes de Consulta: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="mesi" name="mesi" class="form-select form-select-sm"></select>							
					</div>						
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="tipoBalanza" class="form-label"> Tipo de Balanza: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						<select id="tipoBalanza" name="tipoBalanza" class="form-select form-select-sm">
								<option value="Contable" selected="selected">Contable</option>
								<option value="Detallado">Detallado</option>
						</select>							
					</div>						
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="radio" id="MENSUAL" name="Detalle" value="false" class="form-check-input" checked /> Mensual
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="radio" id="ACC" name="Detalle" value="true" class="form-check-input" /> Acumulado
					</div>						
				</div>
				
				<br/> 
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12" >
						<input type="button" id="cmdImprimir" name="cmdImprimir" value="   Generar  " class="btn btn-secondary btn-sm" onclick="imprimirReporte()"/>
					</div>
				</div>
				
				<br/>
												
		  	</div>		  			  	
		</form>			
	</body>
</html>