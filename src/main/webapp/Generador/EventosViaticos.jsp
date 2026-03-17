<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

int estado = 0;
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
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

	var oTable;
	$(document).ready(function() {
		
		$("#btnGuardar").button();
		$("#btnNuevo").button();
		$("#btnAgregar").button();
		$("#btnAgregarMpio").button();
		document.getElementById("btnNuevo").disabled = true;		
				 
		inittable();
		cargaPaises();
		cargaEstados();
		cargaMunicipios();
		creaDT();
		setFechas();
	});
	
	function inittable(){
		/*Inicializar tabla*/
		oTable= $("#comisionesDT").dataTable({
                   "bLengthChange" : true,
                   "bFilter" : true,
                   "bSort" : true,
                   "bInfo" : true,
                   "bPaginate" : true,
                   "bAutoWidth" : false,
                   "bScrollCollapse" : true,
                   //"sScrollXInner": "100%", 
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
                   }
            });    
		
		
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
	
	
	function generaEventoViaticos(){
		
		$("#pais").val(document.getElementById("cPais").value);
		$("#estado").val(document.getElementById("estadocombo").value);
		$("#municipio").val(document.getElementById("municipiocombo").value);
		
		if($("#fInicio").val() == ""){
			alert('Favor de capturar la fecha de inicio. Para poder generar la comisión.');
		}else if($("#fFin").val() == ""){
			alert('Favor de capturar la fecha de fin. Para poder generar la comisión.');
		}else if(!validarFechas()){
			alert("La fecha de Inicio no puede ser mayor a la fecha Fin. Verifique !!");
		}else if (document.getElementById("cPais").value == 0){
			alert('Favor de seleccionar el pais. Para poder generar la comisión.');
		}else if (document.getElementById("estadocombo").value == 0){
			alert('Favor de seleccionar un estado. Para poder generar la comisión.');
		}else if (document.getElementById("estadocombo").value == 9999){
			alert('Favor de presionar el boton "Agregar" para que agregue el nuevo Estado.');
		}else if (document.getElementById("municipiocombo").value == 0){
			alert('Favor de seleccionar un municipio. Para poder generar la comisión.');
		}else if (document.getElementById("municipiocombo").value == 9999){
			alert('Favor de presionar el boton "Agregar" para que agregue el nuevo Municipio/Ciudad.');
		}else if ($("#cLocalidad").val() == ""){
			alert('Favor de capturar la Localidad. Para poder generar la comisión.');			
		}else if ($("#cConcepto").val() == ""){
			alert('Favor de capturar el concepto. Para poder generar la comisión.');			
		}else{
			if(validaComisiones()){
				queryFormPost("tViaticosEventosInsert", {async:false});			
				queryFormPost("tViaticosComisionesIDRead", {async:false});
				alert("Comisión: " + $("#nIdComision").val() + " creada satisfactoriamente.");
				document.getElementById("btnGuardar").disabled = true;
				document.getElementById("btnNuevo").disabled = false;
				//location.reload();
			}
		}
	}
	
	function cargaPaises(){
		querySelectPost("tCatalogoPaisesRead", "cPais", {async:false});
		
		$("#cPais").val(146);
	}
	
	function cargaEstados(){
		$("#pais").val(document.getElementById("cPais").value);
		querySelectPost("cat_EstadosRead", "estadocombo", {async:false});
		
	}
	
	function cargaMunicipios(){
		$("#estado").val(document.getElementById("estadocombo").value);
	
		if ($("#estado").val() == 9999) {
			$("#dlgAgregar").css('display', 'block');
		}  else {
			$("#dlgAgregar").css('display', 'none');
		}
		filtrarTabla();
				
		querySelectPost("cat_MunicipiosRead", "municipiocombo", {async:false});
		
		$("#municipio").val(document.getElementById("municipiocombo").value);
	}
	
	function validaMunicipio(){
		$("#municipio").val(document.getElementById("municipiocombo").value);
	
		if ($("#municipio").val() == 9999) {
			$("#dlgAgregarMpio").css('display', 'block');
		}  else {
			$("#dlgAgregarMpio").css('display', 'none');
		}
		
	}
	
	function agregarEstado(){
	
		if ($('#nombre_estado').val() ==""){
			alert ("Debe capturar el nombre del estado para darlo de alta");
		} else {
			//Se agrega el nuevo estado
			queryFormPost("nuevoEstado", {async:false});
			
			//Se consulta el estado y se actualiza en el combo
			queryFormPost("cat_EstadosNuevoRead", {async:false});
			querySelectPost("cat_EstadosRead", "estadocombo", {async:false});
			$('#estadocombo').val($('#estado').val());
			
			//Se ocultan los controles de agregar
			$("#dlgAgregar").css('display', 'none');
		}
	}
	
	function agregarMunicipio(){
	
		if ($('#nombre_municipio').val() ==""){
			alert ("Debe capturar el nombre de la ciudad/municipio para darlo de alta");
		} else {
			//Se agrega el nuevo municipio / ciudad
			queryFormPost("nuevoMunicipio", {async:false});			
			
			// Se consulta el municipio agregado
			queryFormPost("cat_MunicipioNuevoRead", {async:false});
			querySelectPost("cat_MunicipiosRead", "municipiocombo", {async:false});
			$('#municipiocombo').val($('#municipio').val());
			
			//Se ocultan el dlg de agregar
			$("#dlgAgregarMpio").css('display', 'none');
		}
	}
	function creaDT( conds ){
		if( !conds )
			conds = "1<>1";
		
		oTable = $('#comisionesDT').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : true,
				"bAutoWidth" : true,
				"sScrollY" : 270,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType" : "full_numbers",
				"bScrollCollapse" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_ViaticosComisiones&qw=" + conds,
				aoColumns : [ {
					sName : "nIdComision"
				}, {
					sName : "cConcepto_Comision"
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				}, {
					sName : "Ubicacion"
				}, {
					sName : "cEstatusDesc"
				}],
				oLanguage : es_mx
			});			
			
			$("#comisionesDT tbody").dblclick( function( e ) {
				$(oTable.fnSettings().aoData).each(
					function (){
						$(this.nTr).removeClass('row_selected');
				});
					
				$(e.target.parentNode).addClass('row_selected');
				
				tblComisionDblClick(e);
			});
	}

	function filtrarTabla(){
		var cond = "";
		var token = "";
		
		if( $("#fInicio").val() != "" &&  $("#fFin").val()  != null ){
			cond = "(  CONVERT(DATE, finicio, 103) >= CONVERT(DATE, '" + $("#fInicio").val() + "', 103) AND CONVERT(DATE, fFin, 103) <= CONVERT(DATE, '" + $("#fFin").val() +"', 103)  )";
			token = " AND ";
		}else if( $("#fInicio").val() != "" ){
			cond = "(  CONVERT(DATE, finicio, 103) >= CONVERT(DATE, '" + $("#fInicio").val() + ", 103)  )";
			token = " AND ";
		}else if( $("#fFin").val() != "" ){
			cond = "(  CONVERT(DATE, fFin, 103) <= CONVERT(DATE, '" + $("#fFin").val() + ", 103)  )";
			token = " AND ";
		}
		
		if( $("#estadocombo").val() != "0" ){
			cond += token + " id_estado = " + $("#estadocombo").val();
			token = " AND ";
		}  
		creaDT(cond);
		//seEncontraronComisiones();		
	}
	
	function tblComisionDblClick(event) {	
		/* Obtener los valores del renglon para mostrarlos en pantalla */
		var aPos = oTable.fnGetPosition(event.target.parentNode);
		var aData = oTable.fnGetData(aPos);	
		var nComision = aData[0];
		
		$("#nIdComision").val(nComision);
		
		queryFormPost("tViaticosComisionesRead", {async:false});
		
		document.getElementById("btnGuardar").disabled = true;	
		document.getElementById("btnNuevo").disabled = false;
	}
	
	function validaComisiones(){
	
		var bRegresa = true;
		var oTableM = $("#comisionesDT").dataTable();
		var aData = oTableM.fnGetData();
		var nRows = aData.length;
		
		if(nRows > 0){
			if(confirm("Se encontraron comisiones relacionadas a los parametros capturados.\nDeseas generar una nueva comisión?")){
				bRegresa = true;
			}else{
				bRegresa = false;
			}			
		}
		
		return bRegresa;
	}
	
	function limpiarPantalla(){
		location.reload();
		document.getElementById("btnGuardar").disabled = false;
	}
	
	function validarFechas(){
		var bRegresa = true;
        var fInicio = document.getElementById('fInicio').value.split("/");
        var fFin  = document.getElementById('fFin').value.split("/");
		
		/*VGC201608051544 Se cambia la validacion ya que reportaron incidencia.*/
        
        
        fInicio = new Date( fInicio[2], fInicio[1]-1, fInicio[0], 0, 0, 0, 0 );
        fFin = new Date( fFin[2], fFin[1]-1, fFin[0], 0, 0, 0, 0);

        if(fInicio > fFin){
        	bRegresa = false;        	
        }
        
        return bRegresa;
  	}
	
	function setFechas(){
		$("#fInicio").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true			
		});	
		
		$("#fFin").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true			
		});	
	}
	
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="frmEventoViaticos" name="frmEventoViaticos">
		<div id="container" class="container" style="width: 90%">
			<input type="hidden" id="pais" name="pais">
			<input type="hidden" id="estado" name="estado">
			<input type="hidden" id="municipio" name="municipio">
			
			<div class="card-header"> <h3> Comisión </h3> </div>
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
					<label for="nIDComision" class="form-label">Folio</label>
					<div class="input-group">												
						<input type="text" id="nIdComision" name="nIdComision" class="form-control form-control-sm" style="width: 12em;" value="0" readonly/>													
					</div> 																	
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label for="fInicio" class="form-label"> Fecha Inicio </label>	
					<input type="date"	id="fInicio" name="fInicio" class="form-control form-control-sm" style="width: 10em;" onchange="filtrarTabla()">						
				</div>

				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label for="fFin" class="form-label"> Fecha Fin </label>						
					<input type="date"	id="fFin" name="fFin" class="form-control form-control-sm" style="width: 10em;" onchange="filtrarTabla()">							
				</div>	
			</div>						
			
			<br/>
			
			<h5> Ubicación </h5>
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label class="form-label">País</label>
					<select name="cPais" id="cPais" class="form-select form-select-sm" onchange="cargaEstados(), cargaMunicipios()">																							
					</select>							
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label class="form-label">Estado</label>
					<select name="estadocombo" id="estadocombo" class="form-select form-select-sm" onchange="cargaMunicipios()">																	
					</select>							
				</div>
			</div>
			
			<div id="dlgAgregar" style="display: none;">
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label class="form-label">Nombre</label>
						<input type="text" id="nombre_estado" name="nombre_estado" size="25" class="form-control form-control-sm"/>					
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-3">
						<input type="button" id="btnAgregar" name="btnAgregar" value="Agregar" onclick="agregarEstado()" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label class="form-label">Municipio</label>
					<select name="municipiocombo" id="municipiocombo" class="form-select form-select-sm" onchange="validaMunicipio()()">																							
					</select>							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label class="form-label">Localidad</label>
					<input type="text" name="cLocalidad" id="cLocalidad" class="form-control form-control-sm">																									
				</div>
			</div>
			
			<div id="dlgAgregarMpio" style="display: none;">
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label class="form-label">Ciudad</label>
						<input type="text" id="nombre_municipio" name="nombre_municipio" size="25" class="form-control form-control-sm"/>					
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-3">
						<input type="button" id="btnAgregarMpio" name="btnAgregarMpio" value="Agregar" onclick="agregarMunicipio()" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
			</div>
			
			<br/>
			
			<h5> Señalar denominación o Encargo de la Comisión (Máximo 30 caracteres) </h5>
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>				
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">					
					<input type="text" id="cConcepto" name="cConcepto" size="40" class="form-control form-control-sm"/>					
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>				
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">					
					<input type="button" id="btnGuardar" name="btnGuardar" value="Guardar" onclick="generaEventoViaticos()" class="btn btn-secondary btn-sm"/>					
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					<input type="button" id="btnNuevo" name="btnNuevo" value="Nuevo" onclick="limpiarPantalla()" class="btn btn-secondary btn-sm"/>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-6 col-md-6 col-sm-12">				
				<span> 
					<label style="font-weight:bold; font-size: 10px; text-align: right;">*Doble click en el renglon para desplegar datos de comisión. </label> 
				</span>
			</div>
			
			<div class="table-responsive">	    
				<table id="comisionesDT" class="table table-striped">			
					<thead>
						<tr>
							<th>ID</th>
							<th>Concepto</th>
							<th>Inicia</th>
							<th>Termina</th>
							<th>Ubicaci&oacute;n</th>
							<th>Estatus</th>							
						</tr>
					</thead>
				</table>
			</div>
		</div>
		
	</form>
</body>

</html>
