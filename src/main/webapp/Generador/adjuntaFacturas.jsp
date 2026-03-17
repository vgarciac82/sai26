<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.CancelaDocumento"%>


<%
	String cCentroContable = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String resultadoCancelacion = "";
	String mensaje = "";
	Connection conn = null;

	int nFolioDocumento;
	String cTipoDocumento;

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	String cUE_Usuario = "";
	cUE_Usuario = usuario.getU_UR();

	String U_LOGIN = "";
	U_LOGIN = usuario.getLogin();

	boolean esAdmin = (usuario.getRole("JEFATURA_PAGOS") != null  || usuario.getRole("ADMIN") != null);
	
	String resultado = (String)session.getAttribute("MSG");
	resultado = StringUtils.trimToEmpty( resultado );
	
	boolean mostrarResultado = !StringUtils.isEmpty(resultado);
	session.removeAttribute("MSG");
	
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Carga de Facturas de Boletos de avión</title>
		
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>	
		<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>	
		<link href="../css/sai.css" rel="stylesheet">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		
		<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="js/jquery-ui.min.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		
	
		<script type="text/javascript">
		 $(document).ready(function() {
		 	$("#sendBtn").button().click(
		 		function(){
		 			sendFile();
		 		}
		 	);
		 	
		 	listadoPorComprobar();
		 	listadoComprobados();
		 	
			
			
		 	$("#tabs").tabs({
				"show" : function(event, ui) {
					var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
					if( oTable.length > 0 ) {
						oTable.fnAdjustColumnSizing();
					}
				}
			});
		 	
		$('#list-opciones a').on('click', function (e) {
				  e.preventDefault()
				  $(this).tab('show')
		})
		
		$('#list-opciones a[href="#tabs-2"]').on('click', function (e) {
				  e.preventDefault()
				  $(this).tab('show')
				  listadoPorComprobar();
				 
			})	
		
		$('#list-opciones a[href="#tabs-3"]').on('click', function (e) {
				  e.preventDefault()
				  $(this).tab('show')
				  listadoComprobados();
				 
			})	
			
		 } );
		 
		 function sendFile(){
		 	
			 if( $("#fileCLC").val() == "" ){
		 		Swal.fire("Cargar archivo","Debe eligir el archivo de carga.", "info");
		 		return;
		 	
		 	}else {
		 		Swal.fire({
					  title: 'Se cargará la información al sistema.',
					  text: "¿Desea continuar?",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  $("#frmCLC").submit();	
					  }
					})
		 	}
		 }
		 function muestraLog(){
				$("#resultDialog").dialog("open");
			}
			

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

		 function listadoPorComprobar() {
			 	$('#tipoExtrae').val(1);
				oTableLocal = $('#dt_PorComprobar').dataTable({
					"bPaginate" : false,
					"bLengthChange" : false,
					"bFilter" : false,
					"bSort" : true,
					"bInfo" : false,
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide" : true,
					//"fnInitComplete": function() {    
						//oTableLocal.fnAdjustColumnSizing();
					//},
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_solicitudes_pendientes_boleto",
					aoColumns : [
						{sName : "nFolioComision" 		},
						{sName : "RFC"	 				},
						{sName : "nombreCompleto" 		},
						{sName : "cInformeComision" 	},
						{sName : "faplicacion"	 		},
						{sName : "Documento" 			},
						{sName : "cBoleto" 				},
						{sName : "cRuta" 				},
						{sName : "mImporteBoleto"		},
						{sName : "cEsFirmaElectronica" }
					],
					oLanguage : es_mx,		
				});
			}
			
		 	
			function listadoComprobados() {
				$('#tipoExtrae').val(2);
				oTableLocal = $('#dt_Comprobados').dataTable({
					"bPaginate" : false,
					"bLengthChange" : false,
					"bFilter" : false,
					"bSort" : true,
					"bInfo" : false,
					"bAutoWidth" : false,
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide" : true,
					//"fnInitComplete": function() {    
						//oTableLocal.fnAdjustColumnSizing();
					//},
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_solicitudes_con_boleto" ,			
					aoColumns : [
						{sName : "nFolioRelacionGastos" },
						{sName : "RFC" 				},
						{sName : "cnombre" 			},
						{sName : "cConcepto" 		},
						{sName : "faplicacion" 		},
						{sName : "Documento" 		},
						{sName : "cUnidadResponsable" },
						{sName : "cEsFirmaElectronica" }
					],
					oLanguage : es_mx,	
				});
			}
			
			function extraerListado() {		
				
				document.location.href='../gstnmngr/generaExtraccionBoletos?tipo=' + $("#tipoExtrae").val();
				
			}
		</script>
	</head>
	<body id="dt_example">
 		<form id="frmCLC" name="frmCLC" method="post" enctype="multipart/form-data" action="../AdjuntaFacturaServlet">
 			<input type="hidden" name="u_Login" id="u_Login" value="" />
 			<input type="hidden" name="cUR" id="cUR" value="<%=cUE_Usuario%>" />		
			<input type="hidden" name="cMovto" id="cMovto" value="" />
			<input type="hidden" name="tipoExtrae" id="tipoExtrae" value="0" />
			<div id="container" class="container">
				<div class="card-header"> <h3> Carga Facturas de boletos de avión</h3> </div>
				<br/>
				
	       		<ul class="nav nav-tabs" id="list-opciones">
		            <li class="nav-item" role="presentation">
		            	<a class="nav-link active"  href="#tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-temp" role="tab" aria-controls="tabs-temp" aria-selected="true">Cargar facturas</a>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<a class="nav-link" href="#tabs-2" data-bs-toggle="tab" data-bs-target="#tabs-2" role="tab" aria-controls="tabs-2" aria-selected="false">Pendientes de adjuntar</a>
		            </li>	
		             <li class="nav-item" role="presentation">
		            	<a class="nav-link" href="#tabs-3" data-bs-toggle="tab" data-bs-target="#tabs-3" role="tab" aria-controls="tabs-3" aria-selected="false">Facturas adjuntas</a>
		            </li>		           				           
		    	</ul>
				<div class="card-body">
				<div class="tab-content mt-3" id="tabContent">		
					<div class="tab-pane fade show active" id="tabs-1-temp" role="tabpanel" aria-labelledby="tabs-temp">
						<div class="row d-flex">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12">	
								<label for="fileCLC"></label> Cargar Facturas archivo zip 
					  			<input class="form-control form-control-sm mt-2" type="file" id="fileCLC" name="fileCLC" style="width: 35em;" />																							
							</div>																															
						</div>
						<div class="row d-flex">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12">
								<p>* Solo se procesar&aacute;n archivos .zip Cualquier otro tipo sera ignorado</p>
							</div>
						</div>
						<div class="row d-flex mt-2">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">
								<input type="button" class="btn btn-secondary" id="sendBtn" value="Cargar">
							</div>											
						</div>
					</div>
					<div class="tab-pane fade" id="tabs-2" role="tabpanel" aria-labelledby="tabs-2">
						<div class="row d-flex justify-content">
							<div class="col-11"></div>
							<div class="col-1 col-lg-1 col-md-1 col-sm-1 p-1">
									<input type="button" id="btnExtraer" name="btnExtraer" value="Extraer" onclick="extraerListado()" class="btn btn-secondary"/>
								</div>	
						</div>
						<div class="table-responsive" style="width: 100%">
							<table id="dt_PorComprobar" class="table table-striped"  style="width: 100%">
								<thead>
									<tr>
										<th>Folio</th>
										<th>RFC</th>
										<th>Nombre_completo</th>
										<th>Concepto_de_la_solicitud_(Informe_comisión)</th>
										<th>Fecha_aplic</th>
										<th>Documento</th>
										<th>Boleto</th>
										<th>Ruta</th>
										<th>Importe boleto</th>
										<th>Firma Electrónica</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</div>
					<div class="tab-pane fade" id="tabs-3" role="tabpanel" aria-labelledby="tabs-3">
						<div class="row d-flex justify-content">
							<div class="col-11"></div>
							<div class="col-1 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="button" id="btnExtraer" name="btnExtraer" value="Extraer" onclick="extraerListado()" class="btn btn-secondary"/>
							</div>			
						</div>
						<div class="table-responsive" style="width: 100%">
							<table id="dt_Comprobados" class="table table-striped table-bordered">
								<thead>
									<tr>
										<th scope="col">Folio</th>
										<th scope="col">RFC</th>
										<th scope="col">Nombre_completo</th>
										<th scope="col">Concepto_de_la_solicitud_realizada_(Informe_de_Comisión_capturado).</th>
										<th scope="col">Fecha_aplicación</th>
										<th scope="col">Documento</th>
										<th scope="col">UR</th>
										<th scope="col">Firma Electrónica</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
													
					</div>
					<!-- 
						<div id="resultDialog">							
						<h5> Resultados </h5>
						<hr class="mt-3"/>
						<textarea rows="20" cols="100" class="form-control"><%=resultado%></textarea>							
					</div>
					 -->
				</div> 
			</div>
						
			</div>
 		</form>
	</body>
</html>