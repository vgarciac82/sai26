<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       
    <title>Catalogo de Consecutivos en Cheques</title>
    
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
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

$(document).ready(function(){

	cssReadOnly();
	querySelectPost("catalogoCuentasCheques", "catalogoCuentas",{async: false });
	cargaGrid();
	$("#btnAgregar").button();
	$("#btnGuardar").button();	
	   	
});

function cssReadOnly(){
	$( "[readOnly]" ).each(function(){	
		$(this).addClass("notEditable");	
	});
}

function cargaGrid(){
	$('#dt_chequesCuentas').dataTable({
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
			sSearch: "Buscar:",
			oPaginate: {
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},		
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vConsecutivoCheques&qw=1=1",
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
     	Height: "450px", 
		aaSorting: [[2,"asc"]],
		aoColumns: [
			{ sName: "seq_name",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
			{ sName: "strClabe",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
			{ sName: "strTipoCuenta",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
			{ sName: "seq_value",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
		]
	});
}

function cargaInfoCuenta(){
	$("#existeConsecutivo").val("0");
	if($("#catalogoCuentas").val()!=""){
		var cuenta = $("#catalogoCuentas").val();
		var cadena = cuenta.substring(0,30);
		$("#catCuentas").val(cadena);
		queryFormPost("cargaDatoscuenta",{async: false });
		queryFormPost("existeChequeCuenta",{async: false });
		
		if($("#existeConsecutivo").val()=="1"){
			$("#btnAgregar").attr("disabled",true);
			document.getElementById("btnGuardar").removeAttribute("disabled",true);
		}else{
			$("#folioConsecutivo").val("");
			document.getElementById("btnAgregar").removeAttribute("disabled",true);
			$("#btnGuardar").attr("disabled",true);
		}
	}else{
		$("#btnAgregar").attr("disabled",true);
		$("#btnGuardar").attr("disabled",true);
		$("#strNombreBeneficiario").val("");
		$("#strClabe").val("");
		$("#strTipoCuenta").val("");
		$("#folioConsecutivo").val("");
	}
}
function registrarConsecutivo(tipo){
	if ($("#folioConsecutivo").val()==""){
		Swal.fire({ icon: "warning",
					text: "Favor de capturar el folio del consecutivo"});		
		return;
	}
	
	var cuenta = $("#strNombreBeneficiario").val();
	$("#strNombreBeneficiario").val(cuenta.substring(0,30));
	
	if (tipo=="1"){		
		queryFormPost({
			queryName:"insertSequenceCheque", 
			async:false, 
			callback:function(){ 
				Swal.fire({ icon: "success",
							text: "Se insertó correctamente el registro."});				
			}
		});
		cargaInfoCuenta();
	}else if (tipo=="2"){
		queryFormPost({
			queryName:"updateSequenceCheque", 
			async:false, 
			callback:function(){
				Swal.fire({ icon: "success",
							text: "Se actualizó correctamente el registro."});				
			}
		});
	}
	cargaGrid();
}

function ValidNum() {
    if (event.keyCode < 48 || event.keyCode > 57) {
        event.returnValue = false;
    }
}

</script>
</head>
</br>
	<body id="dt_example">
		<div id="container" class="container" style="width: 90%">						
			
			<div class="card-header"> <h3> Consecutivo de Cheques </h3> </div>
			<hr class="mt-3"/>
			
			<form id="consecutivoCheques" name="consecutivoCheques"> 
				<input id="existeConsecutivo" name="existeConsecutivo" type="hidden" value="0">
				<input type="hidden" name="catCuentas" id="catCuentas" value="" />
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>					
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="catalogoCuentas" class="form-label">Cuentas:</label>
					</div>					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						<select  name="catalogoCuentas" id="catalogoCuentas" class="form-select form-select-sm" onChange="cargaInfoCuenta();">							
						</select>						
					</div>
				</div>
				
				<div class="row">			
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="strNombreBeneficiario" class="form-label">Nombre:</label>
					</div>	
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						<input id="strNombreBeneficiario" name="strNombreBeneficiario" type="text" class="form-control form-control-sm" readonly value="" maxlength="30"/>						
					</div>
				</div>
				
				<div class="row">			
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="strClabe" class="form-label">Clabe:</label>
					</div>	
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						<input id="strClabe" name="strClabe" type="text" class="form-control form-control-sm" readonly value=""/>						
					</div>
				</div>
				
				<div class="row">			
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="strTipoCuenta" class="form-label">Tipo:</label>
					</div>	
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						<input id="strTipoCuenta" name="strTipoCuenta" type="text" class="form-control form-control-sm" readonly value=""/>						
					</div>
				</div>
				
				<div class="row">			
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="folioConsecutivo" class="form-label">Folio:</label>
					</div>	
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						<input id="folioConsecutivo" name="folioConsecutivo" type="text" class="form-control form-control-sm" onkeypress="return ValidNum(event);" value=""/>						
					</div>
				</div>
			
				<br/>
				
				<div class="row">					
					<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">																						
						<input type="button" id="btnAgregar" name="btnAgregar" class="btn btn-secondary btn-sm"  onclick="registrarConsecutivo('1');" value="Agregar"/>																	 																																										
						<input type="button" id="btnGuardar" name="btnGuardar" class="btn btn-secondary btn-sm" onclick="registrarConsecutivo('2');"  value="Editar"/>					
					</div>
				</div>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<div class="table-responsive">	    
							<table id="dt_chequesCuentas" class="table table-striped table-bordered" >					
								<thead>
									<tr>
										<th><font size="2">CUENTA</font></th>
										<th><font size="2">CLABE</font></th>
										<th><font size="2">TIPO</font></th>
										<th><font size="2">CONSECUTIVO</font></th>
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

