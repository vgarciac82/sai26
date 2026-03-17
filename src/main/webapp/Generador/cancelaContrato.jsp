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
		//response.sendRedirect("../index.jsp");
		//return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       
    <title>Cancela Contratos</title>
   
	<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<script src="js/bootstrap.bundle.min.js"></script>
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
	</style>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
<script type="text/javascript">
var bClicBtn = false;
var table;

$(document).ready(function(){	  
	Consultar();
	
	$('#btnProcesar')
		.button()
		.click( function() {
    		procesar();
	} );	
	
	$('#dialog-procesar').dialog({
	    autoOpen: false,
	    modal: true,
	    resizable: false,
	    width: 350,
	    heigth: 200,
	    title: 'Procesando la Cancelación del Contrato',
	    show: "blind",
	    hide: "scale",
	    closeOnEscape: false,
		beforeClose: function( event, ui ) {
			return bClicBtn;			
		},
	    overlay: { backgroundColor: '#FFF',
				   opacity: 6.5   
		}
	});
});


function seleccionar() {
	 let isChecked = $('#select-all').prop('checked');
     $('.marcar').prop('checked', isChecked);
}

function Consultar(){
			table = $('#dataCompromiso').dataTable({         
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ContratoFederalizado",
							"bLengthChange" : true,
							"bFilter" : true,
							"bSort" : true,
							"bInfo" : true,
							"bPaginate" : true,
							"bAutoWidth" : true,
							"bScrollCollapse" : true,
							"sScrollXInner": "100%", 
							"sScrollX": "100%",
							"sPaginationType" : "full_numbers",
							"bJQueryUI" : true,
							"bRetrive" : true,
							"bDestroy" : true,
							"bServerSide": true,
							"fnInitComplete": function() {    
								    table.fnAdjustColumnSizing();
								},
							"iDisplayLength": 25,   
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "center"},
							    { sName: "nFolioPrecomFinanciero",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "center"},
								{ sName: "docto",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "cIdContrato",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "cIdRFC",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "dNombre",		bSearchable: true,	bSortable: true	, bVisible: true, sClass: "left"},
								{ sName: "fInicio",  	bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
								{ sName: "fTermino",	bSearchable: true,	bSortable: true	, bVisible: false, sClass: "left"},
								{ sName: "cObjetoContrato",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "center"},
								{ sName: "mImporte",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "right"},
								{ sName: "cIdUnidadAdministrativa",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"}
							]
		        		});	
}

function procesar(){

	var table = document.getElementById("dataCompromiso");
	var data = $('#dataCompromiso').dataTable().fnGetNodes();
	var docCuantos = 0;
	var total = 0;
	var tipoDocumento = "";
	var nFolio = "";
	var contrato = "";
		
	var oTable = $('#dataCompromiso').dataTable();
	var aData = oTable.fnGetData();
	
	
	for(var i=0; i < data.length; i++){
		var chkbox = $('input', data[i] )[0].checked;
		
		if( chkbox == true ){
				
			tipoDocumento += aData[i][1] + "/";
			nFolio += aData[i][2] + "/";
			contrato += aData[i][3] + "/";
						
			var docSelec = 1;
			docCuantos = docCuantos + 1;
		}
	}
	
	if(docCuantos == 0){
		Swal.fire("Atención!","Seleccione un Documento","info");
		$("#btnProcesar").show();
		return;
	}else{
		Swal.fire({
			  title: '¿Esta seguro de continuar?',
			  text: "Se cancelará \n " + " " + docCuantos + " contrato",
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#dialog-procesar" ).dialog( "open" );
	
				$.ajax({
						url: './cierrePresupuestal.jsp',
						type: 'post',
						dataType: 'json',
						data: { tipo:'cancelaCompromiso', tipoDocumento:tipoDocumento, nFolio:nFolio, contrato: contrato },
						success: function(data){
						
								if(data.sinSesion == "sinSesion"){		
									location.href = "../index.jsp";
									
								}else if(data.estatus == "guardado"){
									alert("Documentos Cancelados Correctamente");
									
								}else if(data.estatus == "sinInformacion"){
									alert("Error Al Buscar Documento");
									
								}else{
									alert("No Guardado");	
								}
								
								bClicBtn = true;
								$("#dialog-procesar" ).dialog( "close" );
								location.reload();
						}
				});
			  } else if (result.dismiss === Swal.DismissReason.cancel) {
				  	$("#btnProcesar").show();
					$("#btnExportar").show();
					$("#dialog-procesar" ).dialog( "close" );
			  }
			})
	}
}



</script>
</head>
<body id="dt_example">
<br/>  
		<form>
			<input type="hidden" id="numFolios" name="numFolios" />
			<input type="hidden" id="nFolioComp" name="nFolioComp" />
  			<div id="container" style="width:90%" class="container">
  						
					<div class="card-header"> <h4> Cancela Contratos Federalizados </h4> </div>
					<hr class="mt-3"/>
					
					<div class="row">
						<div class="col-lg-2 col-md-2 col-sm-12">
							<br>
							<input class="form-check-input" type="checkbox" value="" id="select-all" onChange="seleccionar()"/> 
							<label class="form-check-label" for="select-all"> Seleccionar Todos </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12">							
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
							<input type="button" class="btn btn-secondary" id="btnProcesar" value="Procesar"/>
						</div>
					</div>
					
					<br/>
  				
					<table id="dataCompromiso" class="display" style="font-size: 9pt">
						<tbody>
							<thead>
								<tr>
									<th>-</th>
									<th>Folio</th>
									<th>Documento</th>
									<th>Contrato</th>	
									<th>RFC</th>
									<th>Nombre</th>
									<th>fecha Ini</th>
									<th>fecha Fin</th>
									<th>Objeto del Contrato</th>
									<th>Importe</th>	
									<th>UR</th>
								</tr>	
							</thead>
						</tbody>
					</table>
						
					<br/>   				
	  								
  				
  			</div>
  			
			<div id="dialog-procesar">
				<div id="esperar" align="center">Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
		</form>
  </body>
</html>

