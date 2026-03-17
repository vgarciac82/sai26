<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@page import="java.text.DecimalFormat"%>

<%
	String cCentroContable = "";
	String algo = "";
 	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	boolean error = "SI".equals( request.getParameter("error") );
	
	String msg = "";
	
	if(error){
		msg = (String) session.getAttribute(GestionInterface.ATT_MSG);	
	}
	
	String mensaje = "";
	
	if (request.getParameter("msg") != null	&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}
	
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	algo = usuario.getLogin();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Cedulas SII-WEB</title>

<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">

var oTable1;

$(document).ready(function() {	
	if( <%=error%> ){		
		Swal.fire({ icon: "error",
					text: "Ocurrio el siguiente error:" <%=msg%>});
	}
	$("#cUsuario").val( "<%=algo%>");
	$("#generaCedula").button();
	creaTablaCedula();	
	});
	
	
	function nuevaCedula(){		
		var N = "";
		var M = "";
		var Existe = 0;
		var ExisteAnt = 0;
		var dif = 0;
		if ($("#mesMostrado").val()!= 0){			
			queryFormPost("existeMes", {async: false});
			queryFormPost("existeMesAnterior", {async: false});			
			queryFormPost("numConciliaciones", {async: false});
			queryFormPost("numCuentas", {async: false});
			
			Existe = $("#existe").val();
			ExisteAnt = $("#existeAnt").val();
			
			if(Existe != 0){
				Swal.fire({ icon: "error",
							text: "Ya existe conciliacion para este mes"});
			}
			else if ( ExisteAnt > 0 ){
				N = $("#numConc").val();
				M = $("#numCtas").val();
				
				dif = parseInt(M,10) - parseInt(N,10);
				if (N != M){
					Swal.fire({ icon: "error",
								text: "Informacion incompleta, Estados de cuenta faltantes: "+ dif.toString()});   					
				}
				else{
					Swal.fire({
						 title: '¿Desea continuar?',
						 text: "Se tienen " + N + " Conciliaciones de " + M + " Requeridas.",
						 icon: 'warning',
						 showCancelButton: true,
						 confirmButtonColor: '#288BA8',
						 cancelButtonColor: '#e6e6e6',
						 confirmButtonText: 'Aceptar',
						 cancelButtonText: 'Cancelar'
					   }).then((result) => {
						 if (result.isConfirmed) {
							 queryFormPost("generaCedulaE06"
									  ,	{async:false, callback:function()
											{Swal.fire({ icon: "success",
														 text: "Proceso terminado " });}
										});
						 } else if (result.dismiss === Swal.DismissReason.cancel) {
							return;
						 }
					   })
				}		
			}
			else{
				Swal.fire({ icon: "warning",
							text: "Se requiere que exista la Cedula del mes Anteriro..."});	
			}			
		}
		else{
			Swal.fire({ icon: "warning",
						text: "Mes no válido, favor de rectificar..."});			
		}		
		creaTablaCedula();	
		}
	
	function nuevaCedulaE02(nFolioECeroSeis, nMes){
		var ExisteAnt = 0;
		Swal.fire({ icon: "info",
					text: "Generamos Cedula E02 para el Mes: " + nMes});
		//alert("Generamos Cedula E02 para el Mes: " + nMes );
		$("#nFolioeCeroSeis").val(nFolioECeroSeis);
		queryFormPost("existeMesAnteriorE02", {async: false});
		ExisteAnt = $("#existeAntE02").val();
		if (ExisteAnt > 0){	
			queryFormPost("generaCedulaE02",{async:false, callback:function() { Swal.fire({ icon: "success",
																						text: "Proceso terminado."});																	
																				} });
		}
		else{
			Swal.fire({ icon: "warning",
						text: "Se requiere que exista la Cedula E02 del mes Anteriro..."});				
		}
		creaTablaCedula();	
	}

	function generaE06(nFolioECeroSeis,nMes){
		Swal.fire({ icon: "info",
					text: "Generamos excel Cedula E06",
					timer: 2000});
		$("#nCedula").val(6);
		$("#nMes").val(nMes);
		$("#nFolioeCeroSeis").val(nFolioECeroSeis);
		$("#ExportarForm").submit();
	}


	function generaE02(nFolioECeroSeis, nMes){
		Swal.fire({ icon: "info",
					text: "Generamos excel Cedula E02",
					timer: 2000});
		$("#nCedula").val(2);
		$("#nMes").val(nMes);
		$("#nFolioeCeroSeis").val(nFolioECeroSeis);
		$("#ExportarForm").submit();
	}	
	
	
	function cambia_Mes(){
			creaTablaCedula();	
	}
	

	function creaTablaCedula() {
		oTable1 = $("#dt_Cedulas").dataTable(
						{
							bAutoWidth : false,
							oLanguage: {
										sProcessing: "Procesando...",
										sZeroRecords: "No hay registros a mostrar",
										sEmptyTable: "No hay datos en la tabla",
										sLoadingRecords: "Cargando...",
										sSearch: "Buscar:",
										sInfo: "Número de Registros --> _TOTAL_ ",
										sInfoEmpty: "Sin Registros"
									   },
						    bPaginate 	: false,
							bFilter 	: true,
							bSort 		: true,    
							sScrollX 	: "100%",							
							bRetrive 	: true,
							bDestroy 	: true,
							bJQueryUI 	: true,
							bServerSide : true,
							aaSorting: [[ 1, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]	+ "/crud?rt=t&ql=fn_l_Cedulas("+$("#mesMostrado").val()+")",
								aoColumns: [
											{ sName: "nEjercicio"   },
											{ sName: "nMes" 		},
											{ sName: "mSaldoBanco"	},
											{ sName: "mCheques"		},
											{ sName: "mChequesAnt"	},
											{ sName: "mCirculante"	},
											{ sName: "nVersion"		},
											{ sName: "abreE06"	},
											{ sName: "abreE02"	} ]
						}
					);
		}
</script>
</head>
<body id="dt_example">
<br/>			
	<form id="ExportarForm" name="ExportarForm" action="../servlet/GeneraCedulasServlet" method = "get" >
		<div id="cedula"  style=" visibility: visible;  width: 80% "  class="container" >
				
			<div class="card-header"> <h3> Cedula SII-WEB E06 </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-4">										
					<label for="mesMostrado" class="form-label"> Selecciona el Mes:&nbsp; </label>											
					<select id="mesMostrado" name="mesMostrado" class="form-select form-select-sm" style="width: 12em;" onchange="cambia_Mes()">
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
						<option value=13>Anual CP</option>
					</select>																													
				</div>	
				
				<div class="col-4">															
					<input type="button" id="generaCedula" name="generaCedula" value="Nueva Cedula E06" class="btn btn-secondary btn-sm" onclick="nuevaCedula()"/>				
				</div>																																									
			</div>				
			
			<br/>
			
			<div id="cedulas" class="table-responsive">	
				<table id="dt_Cedulas" class="table table-striped" >
					<thead>
						<tr>
							<th>Anio</th>
							<th>Mes</th>
							<th>Bancos</th>
							<th>Cheques</th>
							<th>Cheques Ant</th>
							<th>Circulante</th>
							<th>Version</th>
							<th>Cedula E06</th>
							<th>Cedula E02</th>
						</tr>
					</thead>
				</table>
			</div>			
		</div>
		
		<div style="display: none" >
			<input  type="hidden" id="nFolioeCeroSeis" name="nFolioeCeroSeis" value="" />
			<input  type="hidden" id="nCedula"  name="nCedula"  value="" />
			<input  type="hidden" id="nMes"   	name="nMes"   	 value="" />
			<input  type="hidden" id="cUsuario" name="cUsuario" value="" />
			<input  type="hidden" id="numConc"  name="numConc"  value="" />
			<input  type="hidden" id="numCtas"  name="numCtas"  value="" />
			<input  type="hidden" id="existeAnt"  name="existeAnt"  value="" />
			<input  type="hidden" id="existe"   name="existe"  value="" />
			<input  type="hidden" id="existeAntE02"  name="existeAntE02"  value="" />
		</div>
	</form>
</body>
</html>


