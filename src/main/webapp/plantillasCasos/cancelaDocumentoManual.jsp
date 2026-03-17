<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%
	String mensaje=request.getParameter("mensaje")!= null ? request.getParameter("mensaje"):"";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cancela Documentos</title>
		
		<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/ui/jquery.ui.accordion.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		
	<script type="text/javascript" charset="utf-8">
	
	function limpiarSesion(){
		window.location.href="cancelaDocumentoManual.jsp?id=<%=request.getParameter("id")%>";
	}
	   
	function limpiaTabla(){
		$('#tablaCLC').dataTable().fnClearTable();
		$("#btnBuscar").show();
	}
	
	function limpiaDatos(){
		limpiaTabla();
		$('#folio').prop("readOnly", false);;
		$("#folio").val("");
	    $("#opcion").val("");
	    $("#opcion").attr('disabled',false);
	    $("#IdImporte").val("");
	    $("#IdLinea").val("");	    
	    $("#IdFecha").val("");
	    $("#IdLinea").val("");
	    $("#btnLimpiar").hide();
		$("#btnCancelar").hide();
		$("#lbLinea").css("visibility","hidden");
		$("#IdLinea").css("visibility","hidden");
	}
	
	function LineaVisible(){
	    if($("#opcion").val()== 2 || $("#opcion").val()== 3){
			$("#IdLinea").css("visibility","visible");
			$("#lbLinea").css("visibility","visible");
			//parent.document.getElementById("lbLinea").visibled= false;
		}
		else{			
			$("#IdLinea").css("visibility","hidden");
			$("#lbLinea").css("visibility","hidden");
		}
	}
	
     function cancelaDoc(){
    	 Swal.fire({
 			text: "Favor de confirmar que desea cancelar los movimientos de autorización y de apartado. Esta operación de cancelación no podrá ser revertida.",
 			icon: "warning",
 			showCancelButton: true,
 		  	confirmButtonColor: "#7066E0",
 		  	cancelButtonColor: "#e6e6e6",
 		  	confirmButtonText: "Aceptar",
 		  	cancelButtonText: "Cancelar"
 		}).then((result) => {
 			if(result.isConfirmed){
		//if (confirm("Favor de confirmar que desea cancelar los movimientos de autorización y de apartado. Esta operación de cancelación no podrá ser revertida.")){
		    	if ($("#opcion").val()== 1 ){
		    		$("#cancelaDocumento").val('true');
		    		$("#cancelaDocumentoMil").val('false');
		      		document.cancelaDocForm.action = '../servlet/RectificacionesServlet';
		      	}
		      	if ($("#opcion").val()== 2 ){
		      		$("#cancelaDocumento").val('true');
		    		$("#cancelaDocumentoMil").val('false');
		      		document.cancelaDocForm.action = '../servlet/ReintegrosServlet';
		      	}
		      	if ($("#opcion").val()== 3 ){
		      		$("#cancelaDocumento").val('false');
		    		$("#cancelaDocumentoMil").val('true');
		      		document.cancelaDocForm.action = '../servlet/RectificacionesServlet';
		      	}
		      	if ($("#opcion").val()== 4 ){
		      		$("#cancelaDocumento").val('false');
		    		$("#cancelaDocumentoMil").val('true');
		      		document.cancelaDocForm.action = '../servlet/ReintegrosServlet';
		      	}
		      	document.cancelaDocForm.submit();
			} else
				return false;		
 		})
     }
	
	function createInput(form, name, value){
		$('<input>').attr({
			type: 'hidden',
			name: name,
			value: value
		}).addClass('remove').appendTo('#' + form);
	}
	
	function borraElementos(){
		$('.remove').remove();	
	}

	$(document).ready(function (){
		$("#LimpiarTabla").button();
		$("#btnBuscar").button();
		$("#btnLimpiar").button();
		$("#btnCancelar").button();
		$("#btnLimpiar").hide();
		$("#btnCancelar").hide();
		
		$(function() {		
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 800,
      			height:500
    		});
    		
    	});
    	
		$("#btnBuscar").click(function(){ 
			buscarCxp(); 
		});
		
		var agregados = $('#tablaCLC').dataTable({      
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
				sSearch: "",
				oPaginate: {
					sFirst:    "Primero",			
					sPrevious: "Ant.",		
					sNext:     "Sigte.",			
					sLast:     "&Uacute;ltimo"
				}
			},
			bProcessing: true,
			bJQueryUI: true,
			bAutoWidth : false,
			bRetrive: true,
			bDestroy: true,
			bPaginate: false,
			iDisplayLength: 10,
			sScrollXInner: "100%", 
			sScrollY: "100%", 
			sScrollX: "80%",
			Height: "250px",
			Width: "80%"
		});
	});
	

function buscarCxp()
{
    //Declaracion de variables
    var datos = "sinDatos";
	var sTabla;
	var sCampos;
	var sWhere = " r.cDocumentoHaplicado = 'S'";	 //filtra porReintegro o rectificacion autorizada
	var sOrder ="";
	var cuenta = 0;
	//Buscar el Folio Para Mostrar Sus Detalles
	var sCampos = "";
	var folio = Number( $("#folio").val());
	var idDoc = Number($("#opcion").val());
	var nImporteMaybeComas = $("#IdImporte").val();
	var nImporte = nImporteMaybeComas.replace(",","");
	$("#IdFecha").val($("#fechaF").val().split('-').reverse().join('/'));
	var sFecha = $("#IdFecha").val();
	var sLinea = $("#IdLinea").val();
	
	limpiaTabla(); 

	$.blockUI({message: "Procesando espere ......"});		        
	             
	switch(idDoc){
		case 1: // rectificacion
			sTabla = "VRECTIFICACIONAUTDETALLE";
	        // sCampos = "r.nFolioRectificacionAut";
	        if (folio >0){
	        	sWhere += " and r.NFOLIORECTIFICACIONAUT = "+ folio;
	        	cuenta +=1; 
	        }
	        if (nImporte > 0 ){
	        	sWhere += " and r.importeLC = "+ nImporte;
	        	cuenta +=1; 
	        }
	        if (sFecha.length == 10 ){
	        	sWhere += " and  convert(varchar,r.fAplicacion,103) = '"+ sFecha + "'";
	        	cuenta +=1; 
	        }	 		
	        break;
		case 2:  // Reintegro			
			 	   sTabla = "VREINTEGROAUTDETALLE";
	     	  		//sCampos = " r.nFolioReintegroaut ";
	     	       cuenta = 0;
	     	       if (folio > 0){ 
	     	    	   sWhere += " and r.nFolioReintegroaut = "+ folio;
	     	    	   cuenta = 1;
	     	    	}
	     	       if (nImporte >0 )
	     	       {
	     	    	   sWhere += " and r.importeLC ="+ nImporte;
	     	    	   cuenta +=1;  	
	     	    	}
	     	       if (sFecha.length == 10 )
	     	       {													
	     	    	   sWhere += " and convert(varchar,r.fAplicacion,103) = '"+ sFecha + "'";
	     	    	   cuenta +=1;  	
	     	    	}
	     	       if (sLinea.length > 0 ){													
			             sWhere += " and r.lc like  '"+ sLinea + "%'";
						 cuenta +=1;  	
					}
			           
			 break;
		case 3:  // Reintegro			
			 	   sTabla = "VREINTEGROAUTDETALLEMIL";
	     	  		//sCampos = " r.nFolioReintegroaut ";
	     	       cuenta = 0;
	     	       if (folio > 0){ 
	     	    	   sWhere += " and r.nFolioReintegroMilaut = "+ folio;
	     	    	   cuenta = 1;
	     	    	}
	     	       if (nImporte >0 )
	     	       {
	     	    	   sWhere += " and r.importeLC ="+ nImporte;
	     	    	   cuenta +=1;  	
	     	    	}
	     	       if (sFecha.length == 10 )
	     	       {													
	     	    	   sWhere += " and convert(varchar,r.fAplicacion,103) = '"+ sFecha + "'";
	     	    	   cuenta +=1;  	
	     	    	}
	     	       if (sLinea.length > 0 ){													
			             sWhere += " and r.lc like  '"+ sLinea + "%'";
						 cuenta +=1;  	
					}
			           
			 break;
		case 4: // rectificacion
			sTabla = "VRECTIFICACIONAUTDETALLEMIL";
	        // sCampos = "r.nFolioRectificacionAut";
	        if (folio >0){
	        	sWhere += " and r.NFOLIORECTIFICACIONMILAUT = "+ folio;
	        	cuenta +=1; 
	        }
	        if (nImporte > 0 ){
	        	sWhere += " and r.importeLC = "+ nImporte;
	        	cuenta +=1; 
	        }
	        if (sFecha.length == 10 ){
	        	sWhere += " and  convert(varchar,r.fAplicacion,103) = '"+ sFecha + "'";
	        	cuenta +=1; 
	        }	 		
	        break;
		  }
	  	  
		if (sWhere.length > 0 ){ 
			//la consulta	
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: sTabla, Campos:sCampos, Param:sWhere, Order:sOrder, MaxReg: "10", ajax: 'true'}, function(j){
			var cuentaFolio = 0;
			var b_diferente = false;
			var v_idFolio;
			for (var i = 0; i < j.length; i++){
				v_idFolio = j[i].Col0;
				$("#folio").val( j[i].Col0);
				//	modificar aqui $("#IdImporte").val();
				//Verificamos si hay mas de un folio 
				if (cuentaFolio == 0 && b_diferente == false){
				  cuentaFolio = v_idFolio;
				}
				else{
				    if (cuentaFolio != v_idFolio){
				       b_diferente = true;
				    }
				}
				
				
				// obtengo los registros de base de datos
				$("#tablaCLC").dataTable().fnAddData([ j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col7]);
				$("#btnLimpiar").show();
				$("#folio").attr('readonly',true);
				$("#btnCancelar").show();
				$("#btnBuscar").hide();

				datos = "Haydatos";
				
				$("#opcion").attr('disabled',true);
			}		
					
			if(datos != "Haydatos"){
				Swal.fire({ icon: 'warning',
							text: "Información Inexistente." });				
				limpiaTabla();
			}else{
				if (b_diferente == true){
					Swal.fire({ icon: 'info',
								text: "La busqueda obtiene varios folios." });									
				}else{
					$("#btnCancelar").css("visibility","visible");
				}
			}
		});
	}
    else{ 
    	Swal.fire({ icon: 'warning',
					text: "Seleccione algun campo de busqueda." });    	 
    }
	$.unblockUI(); //reiniciar parametros
}

</script>
	</head>
<br/>
	<body id="dt_example">
		<div id="mensaje" class="container">			
			<div class="row d-flex">	
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<p class="mb-0" style="color:blue" align="center"><%=mensaje%></p>
				</div>
			</div>
		</div>			
		
	<form id="cancelaDocForm" name="cancelaDocForm" method="post" action="">
		<input type="hidden" name="cancelaDocumento" id="cancelaDocumento" >
		<input type="hidden" name="cancelaDocumentoMil" id="cancelaDocumentoMil" >
		<input type="hidden" name="IdFecha" id="IdFecha" >
		
		
		<div id="container" class="container">
			<div class="card-header"> <h3> Cancelación de Documentos </h3> </div>
			<hr class="mt-3">
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Documento:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select id = "opcion" name = "opcion" onchange = "LineaVisible();   " class = "form-select form-select-sm" >
						<option value="1" selected="selected"> RECTIFICACIÓN AUTORIZADA </option>
						<option value="2"> REINTEGRO AUTORIZADO </option>
						<option value="3"> REINTEGRO CAPÍTULO MIL AUTORIZADO </option>
						<option value="4"> RECTIFICACIÓN CAPÍTULO MIL AUTORIZADA </option>
					</select>	
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Folio:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-123"></i></span>	
						<input type="text" id="folio" name="folio" class="form-control form-control-sm" />
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Importe:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>						
						<input type="text" id="IdImporte" name="IdImporte" class="form-control form-control-sm" placeholder="0.00"/> 						
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Fecha de Aplicación:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<div class="input-group date" id="datepicker1">
	                   	<input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
	                   </div>			
				</div>
			</div>
			
			<div class="row d-flex" id="lbLinea" style='visibility:hidden'>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Linea de captura:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">		
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-123"></i></span>	
	                   	<input type="text" class="form-control form-control-sm" id="IdLinea" name="IdLinea"/>                                    
	                   </div>			
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" id="btnBuscar" name="btnBuscar" value="Buscar"/>													
					<input type="button" class="btn btn-secondary btn-sm" id="btnLimpiar" name="btnLimpiar" value="Limpiar" onclick="limpiaDatos();"/>
					<input type="button" class="btn btn-secondary btn-sm" id="btnCancelar" name="btnCancelar" value="Cancelar" style='visibility:hidden' onclick="javascript:cancelaDoc();"/>
				</div>						
			</div>	
				
			<br/>
	
			<table id="tablaCLC" class="table table-striped table-sm">
				<thead>
					<tr>
						<th>CLC</th>
						<th>EP</th>
						<th>MES</th>
						<th>IMPORTE</th>
						<th>CUENTASxPAGAR</th>
						<th>TIPO CLC</th>
					</tr>
				</thead>				
			</table>
			
		</div>		
	</form>
		
	</body>
</html>
