<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
	String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
	
	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);	

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	String mensaje = null;
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
		return;
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();
	
	if (session.getAttribute("RESULT") != null) {
		mensaje = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);	

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Cedula y Poliza Pasivo Contingente Patrimonial</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
var oTable;

$(document).ready(function() {	
	inittable();
	
	var msg = "<%=StringUtils.isEmpty( mensaje )?"":mensaje.replaceAll("\n", "").replaceAll("\r", "")%>";
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#usuario").val("<%=algo%>");
	
	$("#generaPoliza").button();
	$("#cedula").button();	
		
	$("#generaPoliza").button().click(function() {
		$("#fecha_aplicacion").val($("#fecha").val().split('-').reverse().join('/'));
		if( $("#fecha_aplicacion").val() != "" && validaMesCaptura() ){		
			document.TempForm.action="../reportes/PasivosContingentes";
			document.TempForm.method="POST";
			document.TempForm.submit();
		}
	});
		
	document.getElementById("dpoliza").style.display = "none";	
	if( msg != "" )
		Swal.fire({ icon: "success",
					text: msg });	
	
});

	function inittable(){
		/*Inicializar tabla*/
		oTable= $("#tblcargas").dataTable({
					"bLengthChange" : true,
				       "bFilter" : true,
				       "bSort" : true,
				       "bInfo" : true,
				       "bPaginate" : true,
				       "bAutoWidth" : false,
				       "bScrollCollapse" : true,
				       "sScrollXInner": "100%", 
					"sScrollX": "100%",
				       "sPaginationType" : "full_numbers",
				       "bJQueryUI" : true,
				       "bRetrive" : true,
				       "bDestroy" : true,
				       "bServerSide": true,		            
					"iDisplayLength": 25,
	       			"fnInitComplete": function() {   
	       				oTable.fnAdjustColumnSizing();
	    			},
	               oLanguage : {
	               	    sProcessing: "Procesando...",
	          			sLengthMenu: "Mostrar _MENU_ registros",
	          			sZeroRecords: "No hay registros a mostrar",
	          			sEmptyTable: "No hay datos en la tabla",
	          			sLoadingRecords: "Cargando...",
	          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
	          			sInfoEmpty: "Registro 0 al 0 de 0",
	          			sInfoFiltered: "(filtado de _MAX_ registros)",
	          			sInfoPostFix: "",
	          			sInfoThousands: ",",
	          			sSearch: "Buscar:",
	          			oPaginate: {
	          				sFirst:    "Primero",
	          				sPrevious: "Ant.",
	          				sNext:     "Sigte.",
	          				sLast:     "&Uacute;ltimo"
	                      }
	               },
	               sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_cargaTemporalPCPatrimonial",			
		  			aaSorting: [[ 0, "asc" ]] ,
		  			aoColumns: [				
		  				{ sName: "cMes"},		  				
		  				{ sName: "nFolioDocPoliza" },
		  				{ sName: "nFolioPoliza" },
		  				{ sName: "estatus" },
		  				{ sName: "cDescripcionPoliza" },
		  				{ sName: "boton"}
		  			]
	        }); 
				
	}
		
	function validaMesCaptura(){
		var continuar = true;
		$("#fecha_aplicacion").val($("#fecha").val().split('-').reverse().join('/'));
		var valFecha = $("#fecha_aplicacion").val();
		
		$("#pasivoCapturado").val("");
		
		if( valFecha != "" ){
			var mes = valFecha.split("/")[1];
			$("#nMes").val(mes);
			queryFormPost( {
				queryName:"existePasivoPatrimonial",
				async:false,
				callback:function(){
					if( $("#pasivoCapturado").val() != "0" ){
						Swal.fire({ icon: "warning",
									text: "El mes ya contiene una poliza registrada de pasivos contingentes. Debe cancelar la anterior." });						
						continuar = false;
					}
				}
			} );
		}
		return continuar;	
	}
	function selecciona(elemento){		
		if(elemento.value == "1"){
			document.getElementById("dcedula").style.display = "block";
			document.getElementById("dpoliza").style.display = "none";
		}
		else {
			document.getElementById("dcedula").style.display = "none";
			document.getElementById("dpoliza").style.display = "block";
		}
		
	}
	function extraeCedula(){
		document.TempForm.action="../pasivoscontingentes/PasivosContingentes";
		document.TempForm.method="GET";
		document.TempForm.target="_blank";
		document.TempForm.submit();
	}			

</script>

</head>
<body id="dt_example">
<br/>
	<form id="TempForm" name="TempForm" >		

	<div id=container style="width: 50%" class="container">		
		<input type="hidden" id="nMes" name="nMes" />
		<input type="hidden" id="pasivoCapturado" name="pasivoCapturado" value="0"/>
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />	
		<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="<%=efa%>"/>	
		<input type="hidden" id="fecha_aplicacion" name="fecha_aplicacion"/>
		<input type="hidden" name="reportPath" id="reportPath" value="<%=reportPath%>" />

		<div class="card-header"> <h3> Cédula y Póliza Pasivo Contingente Patrimonial </h3> </div>
		<hr class="mt-3">			
					
		<div class="row">
			<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
			</div>
			<div class="col-12 col-lg-5 col-md-5 col-sm-12">			
				<div class="form-check">
					<input type="radio" name="TIPO_REPORTE" class="form-check-input" value = "1" onclick="selecciona(this)" checked>								
					<label for="patrimonial" class="form-check-label">Cédula</label>
				</div>
			</div>
			<div class="col-12 col-md-6 mb-3">
				<div class="form-check">								
					<input type="radio" name="TIPO_REPORTE" class="form-check-input" value = "2" onclick="selecciona(this)"/>
					<label for="laboral" class="form-check-label">Póliza</label>
				</div>
			</div>		
		</div>		
								
		<div id="dcedula">															
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
				</div>
				<div class="col-12 col-md-5 mb-5 d-flex justify-content-center">										
					<label for="mes" class="form-label"> Mes de Consulta: </label>											
					<select id="mes" name="mes" class="form-select form-select-sm" style="width: 12em;">							
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
					&nbsp;<input type="button" id="cedula" name="cedula" value="Cédula" class="btn btn-secondary btn-sm" onclick="extraeCedula()" />
				</div>
			</div>
			
			<br/>
				
			<h5> Polizas mensuales </h5>
			<hr class="mt-3"/>
		
			<div id="cargas" class="table-responsive">	    
				<table id="tblcargas" class="table table-striped">
		            <thead>
		                <tr>
		                	<th>Mes</th>
		                	<th>Folio Documento</th>
		                	<th>Póliza</th>
		                	<th>Status</th>
		                	<th>Descripción Póliza</th>
		                	<th>Acción</th>						                    
		                </tr>
		            </thead>
		        </table>
			</div>	
																
		</div>										

		<div id="dpoliza">														
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
				</div>
				<div class="col-12 col-md-6 mb-3 d-flex justify-content-center" >						
					<div class="form-group">
						<label for="fecha">Fecha Aplicación:</label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fecha" name="fecha" onchange="validaMesCaptura()"/>                                    
	                    </div>
	                </div>										
					&nbsp;<input type="button" id="generaPoliza" name="generaPoliza" value="Genera Póliza" class="btn btn-secondary btn-sm"/>												
				</div>														
			</div>													
		</div>
						
	</div>																					
</form>

</body>
</html>


