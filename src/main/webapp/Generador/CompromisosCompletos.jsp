	<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%
	//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cCentroContable=usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String unidadEjec=usuario.getU_UR();
	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	Empleado e = new Empleado();
	String cEjercicio;
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);

	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	String UR = usuario.getU_UR();
	String cc = usuario.getPropiedad("CCENTROCONTABLE").getValor();

	String sDescripcion = ea.getDescripcion();
	String sNombre = e.getNombreCompleto();
	String sCargo = e.getCargo();

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	cEjercicio = adecProy.obtenEjercicioFiscal();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Listado de Compromisos</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

	<script type="text/javascript">
		
	var oTableBeneficiarios;
	
	$(document).ready(function(){
		$("#tabs").tabs("select", "#tabsC-1");
		compromisosSAI();	
		$("#tblSICOP").hide();
		$("#tblDobleSICOP").hide();
		$("#exportar").hide();
		$("#exportar").button();		
	}); //fin del ready
		
	function compromisosSAI() {		
		oTableBeneficiarios = $("#tblSAI").dataTable(
				{	"bLengthChange" : true,
		            "bFilter" : true,
		            "bSort" : true,
		            "bInfo" : true,
		            "bPaginate" : true,
		            "bAutoWidth" : true,//*
		            "bScrollCollapse" : true,
		            "sScrollY": "100%", 
		    		"sScrollX": "100%",
		            "sPaginationType" : "full_numbers",
		            "bJQueryUI" : true,
		            "bRetrive" : true,
		            "bDestroy" : true,
		            "bServerSide": true,		            		          
		            "iDisplayLength": 25,
					"fnInitComplete": function() {																							
						oTableBeneficiarios.fnAdjustColumnSizing();
					},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vCompromisosSai",							 
					aaSorting : [ [ 0, "asc" ] ],
					aoColumns : [ {
						sName : "cIdContrato" 		}, {
						sName : "fAplicacion"		}, {
						sName : "caNoCompromiso"	}, {
						sName : "Estatus"		}, {
						sName : "sicop"			}, {
						sName : "folioComp"		} ]
				});
		$("#cTipo").val() == 1;
	}
	function compromisosSICOP() {
		//var qw="";		
		$("#tblSICOP").show();
		$("#exportar").show();
		oTableBeneficiarios = $("#tblSICOP").dataTable(
				{
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
						oTableBeneficiarios.fnAdjustColumnSizing();
					},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vCompromisosSicop" ,					
					aaSorting : [ [ 0, "asc" ] ],
					aoColumns : [ {
						sName : "cIdProceso" 		}, {
						sName : "fExpedicion"		}, {
						sName : "cTipoMov"	}, {
						sName : "caNoCompromiso"		}, {
						sName : "cEstatus"			}, {
						sName : "cCompromisoSicop"			}, {
						sName : "cDescripcion"			}, {
						sName : "dRFC"		} ]
				});
		$("#cTipo").val(2);		
	}
	
	function compromisosDoblesSICOP() {
		//var qw="";
		$("#tblDobleSICOP").show();
		oTableBeneficiarios = $("#tblDobleSICOP").dataTable(
				{
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
						oTableBeneficiarios.fnAdjustColumnSizing();
					},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vCompromisosDuplicadosSICOP" ,					
					aaSorting : [ [ 0, "asc" ] ],
					aoColumns : [ {
						sName : "cIdProceso" 		}, {
						sName : "fExpedicion"		}, {
						sName : "cTipoMov"	}, {
						sName : "caNoCompromiso"		}, {
						sName : "cEstatus"			}, {
						sName : "cCompromisoSicop"			}, {
						sName : "cDescripcion"			}, {
						sName : "dRFC"		} ]
				});
		$("#cTipo").val(3);
	}		
	
	function exportaXLS(){
		
			$("#exportaCompromisos").submit();
			
		}
		</script>
	</head>
<body id="dt_example"  onkeydown="return(desactivaBackspace(event))" >
<br/>
	<div id="container" style="width: 100%" class="container">
		<form id="exportaCompromisos" name="exportaCompromisos" action="../reportes/exportaCompromisos" method = "get" target="_self" >
		<input type="hidden" id="cCC" name="cCC" value="<%= cCentroContable%>" />
		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cUR" id="cUR"  value=""/>
    	<input type="hidden" name="cTipo" id="cTipo"  value="1"/>
		
		<div class="card-header"> <h3> Consulta de Compromisos </h3> </div>
		<hr class="mt-3"/>
    	
    	<div class="row d-flex justify-content-center">
    		<ul class="nav nav-tabs" id="list-opciones">
	            <li class="nav-item" role="presentation">
	            	<button class="nav-link active" id="compSai" onClick="compromisosSAI();" data-bs-toggle="tab" data-bs-target="#tabsC-1" type="button" role="tab" aria-controls="tabs-temp" aria-selected="true">Compromisos SAI</button>
	            </li>
	            <li class="nav-item" role="presentation">
	            	<button class="nav-link" id="compSicop" onClick="compromisosSICOP();" data-bs-toggle="tab" data-bs-target="#tabsC-2" type="button" role="tab" aria-controls="tabs-version" aria-selected="false">Compromisos SICOP</button>
	            </li>
	            <li class="nav-item" role="presentation">
	            	<button class="nav-link" id="compDupSicop" onClick="compromisosDoblesSICOP();" data-bs-toggle="tab" data-bs-target="#tabsC-3" type="button" role="tab" aria-controls="tabs-notas" aria-selected="false">Compromisos Duplicados SICOP</button>
	            </li>	          
	    	</ul>	    	

			<div class="tab-content mt-3" id="tabContent">		
				<div class="tab-pane fade show active" id="tabsC-1" role="tabpanel" aria-labelledby="tabs-temp">					
					<table id="tblSAI" class="table table-striped">
							<thead>
								<tr>
									<th>Contrato </th>
									<th>Fecha<br> Aplicaci&oacute;n</th>
									<th>Compromiso <br>SAI</th>
									<th>Estatus</th>
									<th>Estatus <br>SICOP</th>
									<th>Folio <br> SICOP</th>
								</tr>										
							</thead>
						</table>
				</div>
				
				<div class="tab-pane fade show active" id="tabsC-2" role="tabpanel" aria-labelledby="tabs-temp">							
					<input type="button" class="btn btn-secondary btn-sm" id="exportar" name="exportar" value="Extraer" onclick="exportaXLS()" />
					<br/>
					<br/>													
					<table id="tblSICOP" class="table table-striped">
						<thead>
							<tr>
								<th>Proceso <br>SICOP</th>
								<th>Fecha<br> Expedici&oacute;n</th>
								<th>Tipo<br> Movto</th>
								<th>Compromiso <br>SAI</th>
								<th>Estatus</th>
								<th>Compromiso <br>SICOP</th>
								<th>Descripci&oacute;n del compromiso solicitado</th>
								<th>RFC</th>
							</tr>										
						</thead>
					</table>
				</div>
				
				<div class="tab-pane fade show active" id="tabsC-3" role="tabpanel" aria-labelledby="tabs-temp">						
					<table id="tblDobleSICOP" class="table table-striped">
						<thead>
							<tr>
								<th>Proceso <br>SICOP</th>
								<th>Fecha<br> Expedici&oacute;n</th>
								<th>Tipo<br> Movto</th>
								<th>Compromiso <br>SAI</th>
								<th>Estatus</th>
								<th>Compromiso <br>SICOP</th>
								<th>Descripci&oacute;n</th>
								<th>RFC</th>
							</tr>										
						</thead>
					</table>
				</div>												
			</div> <!-- FIN class="tab-content mt-3" -->						
		</div> <!-- FIN class="row" -->		
		</form>
	</div>
</body>
</html>