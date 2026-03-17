<%@ page 
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String login=usuario.getLogin();
	
	String cUR = ""; 
	
	cUR = usuario.getU_UR();
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	int nFolio = new Integer(c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1) ).intValue();
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Relaci&oacute;n de Gastos Integradas</title>

<!-- Estilos estandar para los controles JQuery -->
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
	
<script type="text/javascript">
	var tableIntegrados;
	var oTableIntD;
	var oTableIntD2;
	
	//READY
	$(document).ready(function(){
		queryFormPost("leeImporteIntegracion", {async: false });
		cssReadOnly();
		dtLoad();
		
		$("#dt_Integrados tbody").click(function(event) {
			$(oTableIntD.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		});
		$("#dt_Detalle tbody").click(function(event) {
			$(oTableIntD.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		});
			$("#dt_RelacionGastos tbody").click(function(event) {
			$(oTableIntD2.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		});
				
	});
	
	function dtLoad(){
		
		//ENCABEZADO
		tableIntegrados = $('#dt_Integrados').dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tCONSOLIDACIONRGENCABEZADO&qw= nFolioCONSOLIDACIONRG=" + <%=nFolio%>,
				aoColumns: [
					{ sName: "nFolioCONSOLIDACIONRG", 	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "nIdIntegracion",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "fAplicacion",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "nFolioPoliza",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "cTipoPoliza",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "U_LOGIN",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "cDescripcionPoliza",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "cDocumentoHaplicado",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
				],
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}
	   		});
   		
   		//INDIVIDUALES
		oTableIntD = $('#dt_Detalle').dataTable({
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bPaginate" : false,
			"bAutoWidth" : false,
			"bScrollCollapse" : true,   		            
			"sPaginationType" : "full_numbers",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListadoRelacionGastosIntegracion&qw= Integracion = (SELECT nIdIntegracion FROM tconsolidacionrelaciongastosEncabezado WITH (NOLOCK) WHERE nFolioConsolidacion="+ <%=nFolio%>+")",
			aoColumns: [
				{ sName: "nFolioRELACIONGASTOS",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "cIdRelacion",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "caNoContrarrecibo",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "fAplicacion",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "cIdRFC",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "cnombre",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "ID_DESTINO_GASTO",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "mImporteMasIva",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
				{ sName: "cIdUsuarioCaptura",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
				sInfoPostFix: "",
				sInfoThousands: ",",
				sSearch: "Buscar:",
				oPaginate: {
					sFirst:    "Primero",
					sPrevious: "Ant.",
					sNext:     "Sigte.",
					sLast:     "&Uacute;ltimo"
				}
			}
   		});
   		//DETALLE
		oTableIntD2 = $('#dt_RelacionGastos').dataTable({
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tCONSOLIDACIONRGDetalle&qw= nFolioCONSOLIDACIONRG=" + <%=nFolio%>,
			aoColumns: [
				{ sName: "nFolioCONSOLIDACIONRG",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "ep",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "cevento",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "cmes",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "mimportemasiva",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
				{ sName: "ctab",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
				sInfoPostFix: "",
				sInfoThousands: ",",
				sSearch: "Buscar:",
				oPaginate: {
					sFirst:    "Primero",
					sPrevious: "Ant.",
					sNext:     "Sigte.",
					sLast:     "&Uacute;ltimo"
				}
			}
   		});
	}
	
	function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}
	
	function generaLayoutBancarioRG(){
		var folios = "";
		var oTableCTA = $("#dt_RelacionGastos").dataTable();
		var aDataCTA = oTableCTA.fnGetData();
		var ctaBancaria = ""
		ctaBancaria = aDataCTA[ 0 ][ 5 ];
		
		var table = document.getElementById("dt_Detalle");
		var data = $('#dt_Detalle').dataTable().fnGetNodes();
	
		var oTable = $('#dt_Detalle').dataTable();
		var aData = oTable.fnGetData();
		var nRows = $("#dt_Detalle tr").length -1 ;
		var arrlist = new Array();
		var j = 1;
			
		for(var i=0; i < nRows; i++){			
			var caNoFolio = aData[ i ][ 0 ];
			$("#folioRelGasto").val(caNoFolio);
			queryFormPost("esRelGastosLayoutBancarioRead", {async:false});
			
			if(j==1){
				folios += caNoFolio ;
			}else{
				folios += ", " + caNoFolio ;
			}
			j++;
			$("#esPagoLayoutBan").val("");

		}
		
		if(folios == ""){
			Swal.fire({ icon: "info",
						text: "Los folios seleccionados no aplican para generar Layout."});
		}else{
			$("#esIntCompromiso").val("NO");
			$("#nFoliosLayout").val(folios);
			$("#sTipoLayout").val("RG");
			$("#sCuentaLayout").val(ctaBancaria);
			$("#layoutBanco").submit();
		}
	}
	
</script>
	
</head>
<br/>
	<body id="dt_example">  	
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<div class="card-header"> <h3> Consulta de Integraci&oacute;n de Relaci&oacute;n de Gastos </h3> </div>
			<hr class="mt-3"/>
			
		  		<form method="post" id="layoutBanco" name="layoutBanco" action="../gstnmngr/generaLayoutBancoRG">
		  			<input type="hidden" value="<%=cUR%>" id="UR" name="UR">
		  			<input type="hidden" value="<%=nFolio%>" id="nFolioConsolidacion" name="nFolioConsolidacion">
		  			<input type="hidden" value="" id="folioRelGasto" name="folioRelGasto">
		  			<input type="hidden" value="" id="esPagoLayoutBan" name="esPagoLayoutBan">
		  			<input type="hidden" value="" id="esIntCompromiso" name="esIntCompromiso">
		  			<input type="hidden" value="" id="nFoliosLayout" name="nFoliosLayout">
		  			<input type="hidden" value="" id="sTipoLayout" name="sTipoLayout">
		  			<input type="hidden" value="" id="sCuentaLayout" name="sCuentaLayout">
		  			<input type="hidden" value="" id="layoutBanco" name="layoutBanco">
		  			
		  			
					<h5> Integraci&oacute;n </h5>
					<hr class="mt-3"/>
											
					<div id="dv_integra">
						 <div class="row d-flex">								
				        	<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
				        	</div>
				        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				        		<input type="button" id="btnLayOutBancario" name="btnLayOutBancario" onClick="generaLayoutBancarioRG();" value="Regenera LayOut Bancario" class="btn btn-secondary btn-sm"/>
				        	</div>
				        	<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				        	<label for="importeIntegra" class="form-label"> Monto Total: </label>
				        	</div>
				        	<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				        		<input type="text" value="0" id="importeIntegra" name="importeIntegra" class="form-control form-control-sm" readonly style="text-align: right;">
				        	</div>
				        </div>
				        
				        <div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    				           
									<table id="dt_Integrados" class="table table-striped table-bordered" >							
										<thead>
											<tr>
												<th><font size="2">Folio</font></th>
												<th><font size="2">Integraci&oacute;n</font></th>
												<th><font size="2">Fecha Ingreso</font></th>
												<th><font size="2">No. Poliza</font></th>
												<th><font size="2">Tipo Poliza</font></th>
												<th><font size="2">Usuario</font></th>
												<th><font size="2">Descripcion</font></th>
												<th><font size="2">Aplicada</font></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
					</div>
				
					<br/>
					
					<h5> Detalle de la Integraci&oacute;n </h5>
					<hr class="mt-3"/>
						
					<div class="row d-flex">								
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							<div class="table-responsive">	    				           
								<table id="dt_RelacionGastos" class="table table-striped table-bordered" >									
									<thead>
										<tr>
											<th><font size="2">Folio</font></th>
											<th><font size="2">EP</font></th>
											<th><font size="2">Evento</font></th>
											<th><font size="2">Mes</font></th>
											<th><font size="2">Importe</font></th>
											<th><font size="2">Cuenta Bancaria</font></th>
										</tr>
									</thead>
								</table>							
							</div>
						</div>
					</div>
						
					<br/>
					
					<h5> Relaciones de Gastos Integradas </h5>
					<hr class="mt-3"/>
						
					<div class="row d-flex">								
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							<div class="table-responsive">	    				           
								<table id="dt_Detalle" class="table table-striped table-bordered" >											
									<thead>
										<tr>
											<th><font size="2">Folio</font></th>
											<th><font size="2">UR</font></th>										
											<th><font size="2">Folio Relacion</font></th>
											<th><font size="2">CXP</font></th>
											<th><font size="2">F. Devengado</font></th>
											<th><font size="2">RFC</font></th>
											<th><font size="2">Beneficiario</font></th>
											<th><font size="2">Tipo Destino</font></th>
											<th><font size="2">Importe</font></th>
											<th><font size="2">Usuario</font></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</form>	
			</div>
		</body>	
</html>