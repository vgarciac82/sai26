
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang.StringUtils"%>

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
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Validación XML en Pagos</title>

<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" href="css/bootstrap.min.css"></link>
<script src="js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
 
<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "css/demo_table_jui.css";
	@import "css/demo_page.css";		
</style>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
		
<script type="text/javascript" charset="utf-8">
						
	var oTable;
	
	$(document).ready(function() {
		$("#btnBuscar").button();
		
		$("#btnBuscar").button().click(function(){    	
			reloadtable();
	    });	
		
		$("#btnCosultar").button().click(
				function(){
					if( $("#archivo").val() == "" )
						Swal.fire({ icon: 'warning',							  
							  		text: 'Debe seleccionar un archivo.',							  
							})
					else{
						if(getFileExtension($("#archivo").val()) == "xls"){
							$("#FormXML").submit();
						} else {
							Swal.fire({ icon: 'warning',							  
						  		text: 'Solo es posible cargar archivos Excel en formato de compatibilidad 97-2023 *.xls',							  
						})
						}
					}
				}
			);
		
		$( '#tbl_pagos' ).dataTable( {
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
				sInfoFiltered: "(filtered from _MAX_ total entries)",
				sInfoPostFix: "",
				sInfoThousands: ",",
				sSearch: "Buscar:"
			},
			
		} );
		
	});		
				
	function reloadtable(){	
		
		var elmnts =  $("#UUID").val().split(',');
		var condicion = "IN ('"; 
		
		for(var i=0; i<elmnts.length; i++){
			if (i == elmnts.length - 1)
				condicion += elmnts[i] + "')";
			else
				condicion += elmnts[i] + "','";
		}
				
		oTable = $("#tbl_pagos").dataTable({
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bScrollCollapse" : true,
			"sScrollX": "1000px",			
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide": true,
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
    			oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
            },           
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_busca_UUID&qw=" + encodeURIComponent( "UUID "+ condicion),			
			aaSorting: [[ 0, "asc" ]] ,
			aoColumns: [				
				{ sName: "nFolioPago" },
				{ sName: "cTipoPago" },
				{ sName: "RFC" },
				{ sName: "mImporteBruto" },
				{ sName: "mimporteiva" },
				{ sName: "mimporteconiva" },
				{ sName: "UUID" },
				{ sName: "cNombreBD" },
				{ sName: "estatus" }
			]
       	});	
	}	
	
	function getFileExtension(filename) {
	  return filename.split('.').pop();
	}
		
</script>

</head>
<body id="dt_example">
	<br/>	
		<form id="FormXML" name="FormXML" method="POST" action="../contabilidad/validaXML" enctype="multipart/form-data" target="_blank">		
			<input type="hidden" id="ConsultaXML" name="ConsultaXML"/>
			
			<div id="pagos" style="width: 80%" class="container" >				  		      
					
				<div class="card-header"> <h4> Buscar XML en Pagos </h4> </div>
				<hr class="mt-3"/>
				
				<h5> Consulta masiva </h5>
				<hr class="mt-3">
						
				<div class="row d-flex justify-content-center">				
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">		
						<label for="archivo" class="form-label"> Adjunta el archivo formato (.xls) con los UUID a consultar: </label>	
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-filetype-xls"></i></span>													
							<input type="file" name="archivo" id="archivo" class="form-control form-control-sm" />
						</div>																										
					</div>
				</div>				
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
						<input type="button" id="btnCosultar" name="btnCosultar" value="Consultar" class="btn btn-secondary btn-sm"/>									
					</div>																									
				</div>
							
				<br/>
				<br/>
				
				<h5> Consulta indivudual </h5>
				<hr class="mt-3">
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">		
								<label for="UUID" class="form-label"> Captura el UUID de la factura: </label>	
								<br/>
								<label for="UUID" class="form-label"> Cada UUID debe estar separado por una coma(,) </label>
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-filetype-xml"></i></span>
									<textarea id="UUID" name="UUID" rows="3" class="form-control" placeholder="UUID-00001-FACTURA,UUID-00002-FACTURA"></textarea>													
									<!-- <input type="text" name="UUID" id="UUID" class="form-control form-control-sm" placeholder="ABCDEFGH-0000-0000-0000-ABCDEFGHIJKL" onblur = "LimitAttach(this, - 1);"/> -->
								</div>																										
							</div>			
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" class="btn btn-secondary btn-sm"/>									
							</div>																					
						</div>
					</div>
				</div>
				
				<br/>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<div id="integrada" class="table-responsive">
							<table id="tbl_pagos" class="table table-striped table-bordered">
					            <thead>
					                <tr>
					                	<th>Folio Pago</th>					                	
					                	<th>Tipo Pago</th>
										<th>RFC</th>
										<th>Bruto</th>
										<th>IVA</th>
										<th>Bruto + IVA</th>
										<th>UUID</th>
										<th>Base Datos</th>
										<th>Status</th>
					                </tr>
					        	</thead>
					        </table>
				        </div>
					</div>
				</div>
		        
		        <br/>
			        								
			</div>					
		</form>	
	</body>
</html>
