<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cLogin = "";
	String cUR = "";
	
	cLogin = usuario.getLogin();
	cUR = usuario.getU_UR();
	
	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean esSAIAlterno = "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") ) || "true".equals( cabl.getSystemSetting("SAI_FONDEN") );
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Autorizar Generaci&oacute;n de Layout's</title>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>

<script type="text/javascript" charset="utf-8">
	var esSAIAlterno = <%=esSAIAlterno%>;
	
	$(document).ready(function() {
		
		var oTable =$("#dt_AutorizarLayouts").dataTable();
		$("#u_Login").val("<%=cLogin%>");		
	
		if ($("#cUR").val()!="A02" || esSAIAlterno){
			querySelectPost("cUnidadEjecutoraVistasTesoreria", "uEjecutora", {async : false});
			$( "#uEjecutora" ).val('<%=cUR%>');
		}else{
			querySelectPost("cUnidadEjecutoraAENSSRead", "uEjecutora", {async : false});
			$( "#uEjecutora" ).val('*');
		}
		
		$("#btn_Autoriza").button();
		cargaGrid();
	});//FIN DEL READY
	
	function cargaGrid(){
	
		$("#cWhere").val(generaCondicion());
		
		oTable = $("#dt_AutorizarLayouts").dataTable({
	        "bPaginate": true,
	        "iDisplayLength":"20",
   			"bLengthChange": true,
   			"bFilter": true,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"sScrollY": "100%",
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true,	
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaPagosAutorizaEnvioSICOP&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns: [
				{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "Folio",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignLeft"},
				{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignLeft"},
				//{ sName: "cIdRFC",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cnombre",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "mImporteNeto",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "CTAB",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cConcepto",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "fAplicacion",			bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignLeft"}
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
   		
		$("#dt_AutorizarLayouts tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');					
		});	
	}
	
	function generaCondicion(){
		var where = "";
		if ('<%=cUR%>'!="A02" || esSAIAlterno)
			where = where + " cUnidadResponsable = '" + $('#uEjecutora option:selected').val() + "' AND tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
		else{
			if( '*' == $('#uEjecutora').val() ){
				where = where + " tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
			}else{
				where = where + " cUnidadResponsable = '" + $('#uEjecutora').val() + "' AND tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
			} 	
		}
		
		return where;
		
	}

	function AutorizaLayouts(){
		var vacio = true;
		var tListado = $("#dt_AutorizarLayouts").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_AutorizarLayouts');
		$("#nFolios").val("");
		var folios = "";
		var token = "";
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				$("#nFolios").val(tListado[i][2]);
				queryFormPost({queryName:"UpdateAutorizacionLayoutSicop",async:false});
				
				folios += token + tListado[i][2];
				token = ", ";
				vacio = false;
			}			  
		}
		if (!vacio){
			Swal.fire({ icon: "success",
						text: "Fueron Autorizados los folios "+ folios + " para generar su Layout."});				
			cargaGrid();
		}else{
			Swal.fire({ icon: "warning",
						text: "Seleccione al menos un Pago para autorizar."});			
			return;
		}
	}

</script>
</head>
<body id="dt_example">
<br/>
	<div id="container" class="container" style="width: 80%">
		<div class="card-header"> <h3> Autoriza Envio SICOP-SIAFF </h3> </div>

		<form id="autorizaLayouts" name="autorizaLayouts">
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
			<input id="cUR" name="cUR" type="hidden" value="<%=cUR%>">
			<input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'">
			<input id="nFolios" name="nFolios" type="hidden" value="">
			
			<div class="mt-4 row d-flex justify-content">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="uEjecutora" class="form-label"> Unidad Ejecutora: </label>
					</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<select id="uEjecutora" name="uEjecutora" class="form-select form-select-sm" onchange="cargaGrid();"></select>
				</div>				
			</div>
			
			<br/>
			
		 	<h5>Tipo de Pago</h5>
			<hr class="mt-3">		
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="DI" onclick="cargaGrid();" checked/>
						<label for="reporte1" class="form-check-label">Pago Directo</label>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="DV" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Pago Diverso</label>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="FE" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Pago Federalizado</label>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="RG" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Relación Gastos</label>
					</div>
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="PO" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Pago de Obra</label>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="IF" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Ingreso Fiscal</label>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<div class="form-check form-check-inline">			
						<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="PC" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Penas Convencionales</label>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
				</div>
			</div>
									
			<br/>
						
			<div id="pagos" class="table-responsive">
				<table id="dt_AutorizarLayouts" class="table table-striped">
					<thead>
						<tr>
							<th><font size="2"></font></th>
							<th><font size="2">UE</font></th>
							<th><font size="2">Folio</font></th>
							<th><font size="2">CXP</font></th>
							<!-- <th><font size="2">RFC</font></th>  -->
							<th><font size="2">Nombre</font></th>
							<th><font size="2">Importe</font></th>
							<th><font size="2">Cuenta Bancaria</font></th>
							<th><font size="2">Concepto</font></th>
							<th><font size="2">Fecha Captura</font></th>
						</tr>
					</thead>
				</table>
			</div>
			
			<div class="row d-flex justify-content-right">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btn_Autoriza" name="btn_Autoriza" value="Autorizar" onclick="AutorizaLayouts()" class="btn btn-secondary btn-sm"/>
				</div>
			</div>			
				
		</form>
	</div>	
</body>
</html>