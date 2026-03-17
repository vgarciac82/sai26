
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>

<%
	boolean bAplicadoCont = false;
	String msg = "";
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";
	String cSubcuenta = "";

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
		
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
		
	
	if( session.getAttribute("MSG_RESP") != null ){
		msg = (String)session.getAttribute("MSG_RESP");
		session.removeAttribute("MSG_RESP");
	}
		
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable,cUR);	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Oficios SIPLAN (Adecuaciones)</title>

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

<script type="text/javascript" charset="utf-8">
						
var oTable;
var oTable2;
var msg = "<%=msg%>";

$(document).ready(function() {
	
	$("#fecha_aplicacion").val(moment().format('yyyy-MM-DD'));
	
	$("#oficio").button();
	$("#modificado").button();
		
	querySelectPost("catCoordinacion", "cCoordinacion", {async: false });
		
	$("#oficio").button().click(
			function(){
				
				if ($("#nOficio").val() == "" ){
					Swal.fire({ icon: "warning",
				  				text: "Debes capturar el numero de oficio."});		   					
					$("#nOficio").focus();
					return false;
				}
				if ($("#fecha_aplicacion").val() == "" ){
					Swal.fire({ icon: "warning",
		  						text: "Debes capturar la fecha de emisión del oficio."});						
					$("#fecha_aplicacion").focus();
					return false;
				}
				if ($("#mes").val() == "0" ){
					Swal.fire({ icon: "warning",
  								text: "Debes seleccionar el mes de consulta para el modificado."});					
					$("#mes").focus();
					return false;
				}
				if ($("#cCoordinacion").val() == "-1" ){
					Swal.fire({ icon: "warning",
								text: "Debes seleccionar una Coordinacion."});					
					$("#cCoordinacion").focus();
					return false;
				}
				
				var vacio = true;
				var tListado = $("#tblUnidades").dataTable().fnGetData();
				var tListado2 = document.getElementById('tblUnidades');
				var cont = 0;
			    var unidad = 0;
			    var monto = 0;
			   
				$("#cUnidadEjecutora").val(unidad);								
				
				if( $(":checked").length > 0 ){					
						$.blockUI( {
							message : "Procesando espere ......"
						});
						
						$("input[name='chk_UnidadEjecutora']:checked").each( 
							function(){												
								$("#cUnidadEjecutora").val( $(this).val() );
																				
							} 
						);
						
						$("#FormOficios").submit();					
				}else{
					Swal.fire({ icon: "warning",
								text: "Debe seleccionar al menos una Unidad Ejecutora."});					
				}
			}
		);
		
	if( msg != ""){
		$( "#tab1" ).click();
		Swal.fire({ icon: "success",
					text: msg});		
	}
		
	//inittable();
	seleccionaCoordinacion();
	creaTablaOficiosGuardados();
	
});		

	function seleccionaCoordinacion(){
		var qw="cCoordinacion ='"+$("#cCoordinacion").val()+"'";
		oTable = $("#tblUnidades").dataTable({
			"bLengthChange" : true,
            "bFilter" : true,
            "bSort" : true,
            "bInfo" : true,
            "bPaginate" : true,
            "bAutoWidth" : false,
            "bScrollCollapse" : true,   		            
            "sPaginationType" : "full_numbers",
            "bJQueryUI" : true,
            "bRetrive" : true,
            "bDestroy" : true,
            "bServerSide": true,                   
			"iDisplayLength": 25,	    
			oLanguage: {
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
			bServerSide: true,
			"fnServerData": function ( sSource, aoData, fnCallback ) {
	                                    $.ajax( {
	                                        "dataType": 'json', 
	                                        "type": "POST", 
	                                        "url": sSource, 
	                                        "data": aoData, 
	                                        "success": fnCallback
	                                    } );
	                                },
	        aaSorting: [[1, "asc" ]] ,
	        sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_coordinacionUnidad&qw="+qw,            
			aoColumns : [
				{sName : "chk"				,bSearchable : false,bSortable : false},
				{sName : "cUnidadEjecutora"	,bSearchable : false,bSortable : false},
				{sName : "Descripcion"		,bSearchable : false,bSortable : false}
			]
		});	
		
		queryFormPost("buscaTitular", {async: false })
			
	}
	
	
	
	function seleccionaTodo() {
		if ($("#Movimiento_0").prop('checked')) {
			$("#todas").val("1");
			$(".chk_oficio").each(function(){
				$("#"+this.id).prop('checked', true);
				$("#"+this.id).prop('disabled', true);
			});
		} else {
			$("#todas").val("0");
			$(".chk_oficio").each(function(){
				$("#"+this.id).prop('checked', false);
				$("#"+this.id).prop('disabled', false);
			});
		}

	}
	
	function creaTablaOficiosGuardados() {

		oTable2 = $("#dt_OficiosGuardados").dataTable({
							"bLengthChange" : true,
				            "bFilter" : true,
				            "bSort" : true,
				            "bInfo" : true,
				            "bPaginate" : true,
				            "bAutoWidth" : false,
				            "bScrollCollapse" : true,   		            
				            "sPaginationType" : "full_numbers",
				            "bJQueryUI" : true,
				            "bRetrive" : true,
				            "bDestroy" : true,
				            "bServerSide": true,                   
							"iDisplayLength": 25,	 
			       			"fnInitComplete": function() {   
			       				oTable.fnAdjustColumnSizing();
			    			},                   
							oLanguage: {
										sProcessing: "Procesando...",
										sZeroRecords: "No hay registros a mostrar",
										sEmptyTable: "No hay datos en la tabla",
										sLoadingRecords: "Cargando...",
										sSearch: "Buscar:",
										sInfo: "Número de Registros --> _TOTAL_ ",
										sInfoEmpty: "Sin Registros"
									   },
							
							aaSorting: [[ 0, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]	+ "/crud?rt=t&ql=fn_OficiosAdecuaciones()",
								aoColumns: [
											{ sName: "nOficioC" },
											{ sName: "nMesModificado" },		
											{ sName: "cCoordinacion" },
											{ sName: "abreOficio" },
								   			{ sName: "eliminaOficio" } ]
						}
					);
		}
	
	function abreOficio(oficio){
		$("#nOficioC").val(oficio);
		$("#consulta").val("S");
		document.FormOficios.action="../presupuesto/OficiosSIPLAN";
		document.FormOficios.method="GET";
		document.FormOficios.target="_blank";
		document.FormOficios.submit();						
	}
	
	function openARCH(){
		$("#ext").val("xls");		
		document.FormOficios.action="../presupuesto/OficiosSIPLAN";
		document.FormOficios.method="GET";
		document.FormOficios.submit();						
	}
	
	function eliminaOficio(oficio){
		$("#nOficioC").val(oficio);
		queryFormPost("eliminaOficioAdecuacion",{async: false });
		Swal.fire({ icon: "success",
					text: "Oficio No " + oficio + " eliminado correctamente."});			
		creaTablaOficiosGuardados();
	}
		
</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormOficios" name="FormOficios" method="post" action="../presupuesto/OficiosSIPLAN">		
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />
		<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value=""/>
		<input type="hidden" id="todas" name="todas" value="0"/>
		<input type="hidden" id="nOficioC" name="nOficioC" value="0"/>
		<input type="hidden" id="consulta" name="consulta" value="N"/>	
		<input type="hidden" id="ext" name="ext" value=""/>
		
		<div id="container" style="width: 90%" class="container" >
			<div id="tabs" style="width: 90%" class="container" >    	
				<div class="card-header"> <h3> Generacion e Impresion de Oficios (Adecuaciones) </h3> </div>
				<hr class="mt-3"/>
				
		      	<div class="row d-flex justify-content-center">
		      							
		       		<ul class="nav nav-tabs" id="list-opciones">
		       			 <li class="nav-item" role="presentation">
			            	<button class="nav-link active" id="tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-generar" type="button" role="tab" aria-controls="tabs-generar" aria-selected="true">Generar Oficio</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="tabs-2" data-bs-toggle="tab" data-bs-target="#tabs-2-existe" type="button" role="tab" aria-controls="tabs-existe" aria-selected="false">Oficios Existentes</button>
			            </li>						
					</ul>
							
					<br/>
					
					<div class="tab-content mt-3" id="tabContent">
						<div class="tab-pane fade show active" id="tabs-1-generar" role="tabpanel" aria-labelledby="tabs-generar">
							<div id="container" style="width: 90%" class="container">
									
								<div class="row d-flex">
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="nOficio" class="form-label"> No. Oficio: </label>
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
										<input type="text" id="nOficio" name="nOficio" class="form-control form-control-sm" />
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="fecha_aplicacion" class="form-label"> Fecha Oficio: </label>
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
										<div class="input-group">
											<span class="input-group date"><i class="datepicker1"></i></span>
											<input type="date" id="fecha_aplicacion" name="fecha_aplicacion" class="form-control form-control-sm"/>
										</div>
									</div>
								</div>		
								
								<div class="row d-flex">
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="mes" class="form-label"> Mes del Modificado: </label>
									</div>
									<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
										<select name="mes" id="mes" class="form-select form-select-sm">
											<option value="0">Selecciona el Modificado...</option>												
											<option value="1">Enero</option>												
											<option value="2">Febrero</option>
											<option value="3">Marzo</option>
											<option value="4">Abril</option>												
											<option value="5">Mayo</option>												
											<option value="6">Junio</option>
											<option value="7">Julio</option>
											<option value="8">Agosto</option>
											<option value="9">Septiembre</option>
											<option value="10">Octubre</option>
											<option value="11">Noviembre</option>
											<option value="12">Diciembre</option>
										</select>
									</div>
								</div>		
								
								<div class="row d-flex">
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="cCoordinacion" class="form-label"> Coordinación: </label>
									</div>
									<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
										<select name="cCoordinacion" id="cCoordinacion" class="form-select form-select-sm" onchange="seleccionaCoordinacion()">											
										</select>
									</div>
								</div>		
								
								<div class="row d-flex">
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="titular" class="form-label"> Titular Coordinación: </label>
									</div>
									<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
										<input type="text" name="titular" id="titular" class="form-control form-control-sm" readonly/>
									</div>
								</div>		
								
								<div class="row d-flex">
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="titular" class="form-label"> Unidades Ejecutoras: </label>
									</div>
									<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
										<div class="table-responsive">	    
											<table id="tblUnidades" class="table table-striped table-bordered">
												<thead>
													<tr>
														<td width="20px"> <input type="checkbox" id="Movimiento_0" onclick="seleccionaTodo()"/> </td>																											
														<td>Unidades</td>
														<td>Descripcion</td>
													</tr>
												</thead>
											</table>
										</div>
									</div>
								</div>	
								
								<br/>
								
								<div class="row d-flex">
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1 d-flex justify-content-center">
										<input id="oficio" name="oficio" value="Guarda Oficio" type="button" class="btn btn-secondary btn-sm"/> 
									</div>
								</div>
								
								<div class="row d-flex">
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1 d-flex justify-content-center">
										<input id="modificado" name="modificado" value="Extrae Modificado" type="button" class="btn btn-secondary btn-sm" onclick="openARCH()"/>
									</div>
								</div>

							</div>								
						</div>
					 					
						<div class="tab-pane fade" id="tabs-2-existe" role="tabpanel" aria-labelledby="tabs-existe">												
							<div id="consulta" style="width: 90%" class="container">
							
								<div class="table-responsive">	    
									<table id="dt_OficiosGuardados" class="table table-striped" >
										<thead>
											<tr>
												<td>No. Oficio</td>
												<td>Mes Modificado</td>												
												<td>Coordinacion</td>
												<td>Imprimir Oficio</td>
												<td>Eliminar Oficio</td>
											</tr>
										</thead>
									</table>
								</div>
								
							</div>											
						</div>
					</div>
				 
				</div>
			</div>				
		</div>
			
		<div id="firmas" style="width: 70%" class="container">
			<h5> Firmas </h5>
			<hr class="mt-3"/>				
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">								
					<label for="nombre1"> Nombre:&nbsp; </label> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">															 
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" value="Tania A. Limón Magaña" />
					</div> 								
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">								
					<label for="cargo1"> Cargo:&nbsp; </label> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">															 
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cargo1" name="cargo1" class="form-control form-control-sm" value="Gerente de Programación y Presupuesto" />
					</div> 								
				</div>
			</div>
							
		</div>	
		
	</form>

</body>
</html>
