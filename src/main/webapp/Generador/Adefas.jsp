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
       
    <title>Adefas</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
		
	</style>
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	
	
<script type="text/javascript">
$(document).ready(function(){
			//$("#exportaDoc").click(function(){ exportaDoc(); });
						
			pParam = "<%=ur%>"; 
			var tablaCierre = $('#dataAdefas').dataTable({         
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
							bServerSide: true,
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vAdefasEncabezado&qw=" + " Estatus != 'Cancelado' AND caNoContrarrecibo != '0' ",
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
		        			sScrollY: "700px", 
		        			sScrollX: "1250px",
		        			Height: "450px", 
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"50px"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"100px"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"300px"},
								{ sName: "cUnidadResponsable",  bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter", sWidth:"60px"},
								{ sName: "aEjercicioFiscal",	bSearchable: true,	bSortable: true	, bVisible: false, sClass: "alignLeft"},
								{ sName: "fAplicacionF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "mImporteNetoF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "nFolioEnc",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"}, 
								{ sName: "docAplicado",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"150px"}
							]
		        		});	
			
			$( "#dialog-Motivo" ).dialog({
		
				autoOpen: false,
				height: 250,
				width: 650,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							var nFolioAdefaTabla = $("#nFolioAdefaTabla").val();
							$.ajax({
								url: './cierrePresupuestal.jsp',
								type: 'post',
								dataType: 'json',
								data: {tipo:'cancelaAdefa', nFolioAdefa:nFolioAdefaTabla},
								success: function(data){
										if(data.sinSesion == "sinSesion"){
											location.href = "../index.jsp";
										}else if(data.estatus == "guardado"){
											alert("Cancelado Correctamente");
										}else{
											alert("No Se Cancelo Correctamente");	
										}
										$("#esperar").attr("style","visibility=hidden");
										$("#btnProcesar").show();
										$("#btnExportar").show();
								}
							});
							$( this ).dialog( "close" );
						},
						
						"Cancelar": function() {
							$( "#" + $("#Name").val() ).attr('checked',true);
							$( this ).dialog( "close" );
							
							$("#esperar").attr("style","visibility=hidden");
							$("#btnProcesar").show();
							$("#btnExportar").show();
						}
					},
				close: function() {										
				}							
				});
});

function procesar(){
	
	$("#esperar").attr("style","visibility=visible");
	$("#btnProcesar").hide();
	$("#btnExportar").hide();
	
	var table = document.getElementById("dataAdefas");
	var data = $('#dataAdefas').dataTable().fnGetNodes();
	var docCuantos = 0;
	var total = 0;
	var tipoDocumento = "";
	var caNoContrarrecibo = "";
	var nFolioAdefa = "";
	
	
	var oTable = $('#dataAdefas').dataTable();
	var aData = oTable.fnGetData();
	
	
	for(var i=0; i < data.length; i++){
		
		//var row = table.rows[i];
		//var chkbox = row.cells[0].childNodes[0];
		//var nAplicado = row.cells[9].childNodes[0].data;
		
		var chkbox = $('input', data[i] )[0].checked;
		var nAplicado = aData[i][10];
		
		/* Valida Los Documentos Seleccionados */
		
		if( /*null != chkbox && true == chkbox.checked &&*/ chkbox == true && (nAplicado == 'Pendiente' || nAplicado == 'Adefa Cancelada') ){
			
			getNextSequenceVal({seqName: "ADEFA" , async: false, callback: setSequenceVal});
			//tipoDocumento += row.cells[1].childNodes[0].toString()+"/";
			//caNoContrarrecibo += row.cells[2].childNodes[0].toString()+"/";
			//total = Number(total) + Number(quitaFmt(row.cells[7].childNodes[0].toString()));
			
			tipoDocumento += aData[i][1] + "/";
			caNoContrarrecibo += aData[i][2] + "/";
			total = Number(total) + Number(quitaFmt(aData[i][8]));
			nFolioAdefa += $("#nFolioAdefa").val()+"/";
			var docSelec = 1;
			docCuantos = docCuantos + 1;
			
		}
		
	}if(docSelec == 0){
		alert("Seleccione un Documento");
		$("#btnProcesar").show();
		$("#btnExportar").show();
		$("#esperar").attr("style","visibility=hidden");
		return;
	}else{
		if(confirm("Esta Seguro De Pasar a Adefa \n " +
					" "+docCuantos+" documentos \n " +
					" con un total de: $ "+ total.toFixed(2))){
			
			$.ajax({
					url: './cierrePresupuestal.jsp',
					type: 'post',
					dataType: 'json',
					data: {tipo:'guardarAdefa', tipoDocumento:tipoDocumento,caNoContrarrecibo:caNoContrarrecibo,nFolioAdefa:nFolioAdefa},
					success: function(data){
							if(data.sinSesion == "sinSesion"){
								location.href = "../index.jsp";
							}else if(data.estatus == "guardado"){
								alert("Guardado Correctamente");
							}else if(data.estatus == "sinInformacion"){
								alert("Error Al Buscar Documento");
								
							}else{
							alert("No Guardado");	
							}
							location.reload();
					}
			});
		}else{
			$("#btnProcesar").show();
			$("#btnExportar").show();
			$("#esperar").attr("style","visibility=hidden");
		}
	}
}

function setSequenceVal(seqValue) 
{
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioAdefa").val( seqValue );
}
function exportaDoc(){
	
	$("#numFolios").val();
	
	var table = document.getElementById("dataAdefas");
	var aTrs = $('#dataAdefas').dataTable().fnGetNodes();
	var caNoContrarrecibo = "";
	
	if(aTrs.length > 0){
		for(var i=1; i<=aTrs.length; i++){
			var row = table.rows[i];
			caNoContrarrecibo += "'"+row.cells[2].childNodes[0].toString()+"'";
	 		caNoContrarrecibo += ",";
		}
		$("#numFolios").val(caNoContrarrecibo);
	    document.formAdefas.submit();
	}else{
			alert("No Se Puede Exportar");
			return;
	}
}
function quitarCancelacion(name,nFolioAdefaTabla)
{
	$("#esperar").attr("style","visibility=visible");
	$("#btnProcesar").hide();
	$("#btnExportar").hide();
	
	if ( $( "#"+name ).is(':checked') == false  ){
		
		var table = document.getElementById('dataAdefas');
		var rowCount = table.rows.length;
		
		for ( var i = 1; i < rowCount; i++) {
			
			var row = table.rows[i];
			var nFolio = row.cells[8].childNodes[0].data;
			var nAplicado = row.cells[9].childNodes[0].data;
			
			if( nFolio == name && nAplicado == 'Adefa Aplicada' ){
				$("#Name").val(name);
				$("#nFolioAdefaTabla").val(nFolioAdefaTabla);
				$( "#dialog-Motivo" ).dialog( "open" );
			}
		}
	}else{
		$("#esperar").attr("style","visibility=hidden");
		$("#btnProcesar").show();
		$("#btnExportar").show();
	}	
}
function quitaFmt( val ) 
{
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
}
function totalAdefa(){
	
	$.ajax({
		url: './cierrePresupuestal.jsp',
		type: 'post',
		dataType: 'json',
		data: {tipo:'totalAdefa'},
		success: function (data){
			
			var arr = data.respuesta.split("/");
			totalImporte = arr[0];
			totalDoc = arr[1];
			
			$("#totaladefa").text("Numero De Documento De Adefas Aplicados: "+ totalDoc + "  Importe Total:  $ "+ totalImporte);
			
		}
	});
}
</script>
  </head>
  <body id="dt_example">
		<form id="formAdefas" name="formAdefas" action="../gstnmngr/generaAdefa" method="post" >
  			<div id="container" >
  				<h1>Adefas<label style="font-size: 8pt"></label></h1>
  					<div id="tabsl">
  						<table id="tblCierreCXP" align="center" width="950px">
	  						<tr>
	  							<td><input type="hidden" id="nFolioAdefa" name="nFolioAdefa"></td>
	  							<td><input type="hidden" id="numFolios" name="numFolios" /></td>
	  							<td><input type="hidden" id="nFolioAdefaTabla" name="nFolioAdefaTabla" /></td>
	  							<td><input type="hidden" id="Name" name="Name" /></td>
	  						</tr>	
	  						<tr><td>&nbsp;</td></tr>	
  							<tr>
  								<td>	
  									<fieldset>
  										<div id="totaladefa" name="totaladefa" align="center"></div>
					   						<legend>Adefas</legend>
					   							<table align="right">
					   								<tr>
	   													<td><input type="button" id="btnProcesar" name="btnProcesar" value="Procesar" onclick="procesar();" ></td>
	   													<td><input type="button" id="btnTotalAdefa" name="btnTotalAdefa" value="Total Adefa" onclick="totalAdefa();" ></td>
	   													<td><input type="button" id="btnExportar" name="btnExportar" value="Exportar" onclick="exportaDoc();"></td>
		   											</tr>		
		   										</table>
		   										<label id="esperar" style="visibility: hidden">
													<div align="center">Espere por favor....
													  <img border="0" src="../imagenes/espera.gif" height="30">
													</div>
											</label>
							   					<table id="dataAdefas"  class="display" >
							   							
							   							<tbody>
							   								<thead>
							   								<tr>
							   									<th>-</th>
							   									<th>Documento</th>
																<th>Recibo</th>	
																<th>RFC</th>
																<th>Beneficiario</th>
																<th>UR</th>
																<th></th>
																<th>Fecha Aplicacion</th>
																<th>Importe Neto</th>	
																<th>Folio</th>
																<th>Estatus</th>	
															</tr>	
							   								</thead>
							   							</tbody>
					   							</table>
					   				</fieldset>
  								</td>
  							</tr>
  						</table>
  					</div>
  			</div>
  			<div id="dialog-Motivo" title="Motivo">
		 		<td>Esta Seguro Que Desea Cancelar El Documento Adefa</td>         
			</div>
		</form>
  </body>
</html>
