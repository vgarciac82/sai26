<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!Logger log = Logger.getLogger( "CapturaConvenio.jsp" );%>

<%
	Usuario u = (Usuario)session.getAttribute( GestionInterface.ATT_USER );
	if( u == null ){
		response.sendRedirect( "../index.jsp" );
		return;
	}
	
	Caso c = (Caso)session.getAttribute( GestionInterface.ATT_CASE );
	if( c == null ){
		response.sendRedirect( "../index.jsp" );
		return;
	}

	EjercicioFiscalBusinessLogic ebl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	int nFolioConvenio = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1) );
	String fechaCaptura = Util.getTodayESMX(  );
	String ejercicioFiscal = ebl.getEjercicioFiscalActivo(  ).getaEjercicioFiscal(  );
	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Resumen Poliza Manual</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js" charset="UTF-8"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js" charset="UTF-8"></script>
<script type="text/javascript" src="../Generador/js/jquery.blockUI-2.70.0.js" charset="UTF-8"></script>


<script type="text/javascript">
	
	$(document).ready(function() {
		init();
	});
	
	function init(){
		
		querySelectPost({
			queryName:"mCatalogoTipoIVA",
			targetObjectId:"pcIVA",
			async:true,
			callback:function(){
				
			}
		}); 
		
		querySelectPost({
			queryName:"CatalogoTipoRetencionRead",
			targetObjectId:"idRetencion",
			async:true,
			callback:function(){
				
			}
		}); 
		
		convertMoney();
		estyleReadOnly();
		
		$("#cIdContrato").focus();
		convertDatePicker();
		creaDT();	
		$("#importeBruto").blur( function(){actualizaTotal();});
	}
	
	function actualizaTotal(){
		quitaFmtObj($("#importeBruto")[0]);
		var subtotal = $("#importeBruto").val();
		var pciva = parseFloat( $("#pcIVA").val() ); 
		subtotal = parseFloat(subtotal);
		
		var iva = subtotal * pciva / 100.00;
		var total = subtotal + iva;
		$("#importeIVA").val(iva.toFixed(2));
		$("#importeTotal").val(total.toFixed(2));
		moneyFrmt( "importeIVA" );
		moneyFrmt( "importeTotal" );
		moneyFrmt( "importeBruto" );
	}
	var dtRetenciones;
	var dtEP;
	function agregaEP(){
		$('#dt_ep').dataTable().fnAddData( [
			$("#epSel").val()
		 ] );
		$("#epSel").val("");
	}
	
	function creaDT(){
			
		dtRetenciones = $('#dt_retenciones').dataTable({
			sScrollY : "150px",
			sScrollX : "800px",
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true
		});
		
		$("#dt_retenciones tbody").click(function(event) {
            $(dtRetenciones.fnSettings().aoData).each(function() {
                $(this.nTr).removeClass('row_selected');
            });
            $(event.target.parentNode).addClass('row_selected');
        });
		
		$("#dt_retenciones tbody").dblclick(function(evt) {

            var aPos = dtRetenciones.fnGetPosition(evt.target.parentNode);

            if( aPos instanceof Array )
                currIndex = aPos[ 0 ];
            else
                currIndex = aPos;

            if( confirm("¿Esta seguro de eliminar la retencion?") )
            	$('#dt_retenciones').dataTable().fnDeleteRow(currIndex);

        });
 
	
		dtEP = $('#dt_ep').dataTable({
			sScrollY : "150px",
			sScrollX : "800px",
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true
		});
		
		$("#dt_ep tbody").click(function(event) {
            $(dtEP.fnSettings().aoData).each(function() {
                $(this.nTr).removeClass('row_selected');
            });
            $(event.target.parentNode).addClass('row_selected');
        });
		
		$("#dt_ep tbody").dblclick(function(evt) {

            var aPos = dtEP.fnGetPosition(evt.target.parentNode);

            if( aPos instanceof Array )
                currIndex = aPos[ 0 ];
            else
                currIndex = aPos;

            if( confirm("¿Esta seguro de eliminar la EP?") )
            	$('#dt_ep').dataTable().fnDeleteRow(currIndex);

        });
		
		
	}
	
	function onLoadPlantilla(){
		
	}
	
	function ayudaBeneficiarios(){
		window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=convenioForm&inputRFCTarget=rfc&inputDRFCTarget=cNombre', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
	}
	
	function agregaRetencion(){
		$('#dt_retenciones').dataTable().fnAddData( [
			$("#idRetencion").val(),
			$("#idRetencion option:selected").text()
		 ] );
	}
	
	function abreAyudaEP(){
		window.open('../plantillasCasos/ayudaEps.jsp?id=1&fnCallback=setEP', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	}
	
	function setEP(ep){
		$("#epSel").val(ep);
	}
	
	function onSubmit(idOper){
		var p = window.parent;
		if (idOper == 1 ) {
			if( capturaCompleta() ){
				p.gestion.setFolio($("#cFolio").val());
				p.gestion.setOperador($("#operador").val());
				p.gestion.setFechaDocumento($("#fechaCaptura").val());
				p.gestion.setEjercicioFiscal($("#ejercicioFiscal").val());
				return true;
			}
		}
	}
	
	
	function onPostDisplay(idOper){
		if( confirm("Esta seguro de registrar el Convenio " + $("#cIdContrato").val() ) ){
			parent.document.getElementById("pb_save").disabled = true;  
			$(".money").each(function() {
				quitaFmtObj($(this)[0]);
			});
			
			var epList = epsToList();
			var dataJSON = $("#convenioForm").serialize(); 
			dataJSON += epList;
			$.blockUI("Guardando Convenio. Espere por favor...");
			
			$.ajax({
				url : '../contratos/registraConvenio',
				dataType : 'json',
				data : dataJSON,
				async : true,
				type : "POST",
				success : function(json) {
					$.unblockUI();
					if( json.success === true){
						alert( json.message );
						parent.document.getElementById("pb_send").disabled = false;
						parent.document.getElementById("pb_send").click();
					}else{
						alert("No se guardo la informacion debido al error:\n" + json.message + "\nIntente nuevamente. Si el problema continua notifique al administrador.");
						parent.document.getElementById("pb_save").disabled = false;  
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					parent.document.getElementById("pb_save").disabled = false;
					alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
					$.unblockUI();
				}
			});
		} 
		
	}
	
	function epsToList(){
		var token = "&";
		var epList = $("#dt_ep").dataTable().fnGetData();
		var list = "";
		for( var i = 0; i < epList.length; i++ ){
			list =  list + token + "ep=" + epList[i];
		}
		
		return list;
	}
	
	function onPostSubmit(idOper){
		$.blockUI();
		return true; 
	}
	
	function capturaCompleta(){
		
		if( $("#cIdContrato").val() == "" ){
			Swal.fire("Capture","Debe capturar el Numero de Contrato.","info");
			return false;
		}else if( $("#rfc").val() == "" ){
			Swal.fire("Capture","Debe capturar el Beneficiario del Convenio.","info");
			return false;
		}else if( $("#fInicio").val() == "" ){
			Swal.fire("Capture","Debe capturar el Inicio de Convenio.","info");
			return false;
		}else if( $("#fFin").val() == "" ){
			Swal.fire("Capture","Debe capturar el Numero de Convenio.","info");
			return false;
		}else if( !ordenFechas("fInicio", "fFin") ){
			Swal.fire("Revise","La fecha final del convenio debe ser mayor a la fecha de inicio.","info");
			return false;
		}else if( !montoCapturado() ){
			Swal.fire( "Capture","Debe capturar el monto de Convenio." ,"info");
			return false;
		}else if( $("#pcIVA").val() == "" ){
			Swal.fire("Seleccione","Debe seleccionar el % de IVA del Convenio.","info");
			return false;
		}else if( $("#concepto").val() == "" ){
			Swal.fire("Capture","Debe capturar el concepto del Convenio.","info");
			return false;
		}else if( !epCapturada() ){
			Swal.fire("Capture","Debe capturar al menos una clave presupuestal.","info");
			return false;
		}else if( !retencionesCapturadas() ){
			/*Es la ultima validacion, si llega aqui todo es correcto solo falta validar que este seguro de que el contrato no tiene retenciones.*/
			return confirm( "No ha capturado retenciones para el convenio. ¿Esta seguro que desea continuar?" );
		}
		return true;
	}
	
	function retencionesCapturadas(){
		return $("#dt_retenciones").dataTable().fnGetData().length > 0 ;
	}
	
	function epCapturada(){
		return $("#dt_ep").dataTable().fnGetData().length > 0 ;
	}
	
	function ResponsableSiguiente(idOper){
		if(idOper == 1)
			return "CONSULTA_CONVCOLABORACION";
		
	}	
	
	function OperacionSiguiente(idOper){
		if(idOper == 1)
			return "consulta_convcolaboracion";
	}
	
	function montoCapturado(){
		var monto = parseFloat( Sinfrmt( $("#importeBruto").val() ) );
		if( monto > 0.00 )
			return true;
	}
	
</script>

</head>

<body id="dt_example" >
	<form action="" method="post" id="convenioForm">
		<input type="hidden" id="esPlurianual" name="esPlurianual" value="N"/>
		<input type="hidden" id="cFolio" name="cFolio" value="<%=c.getFolio(  )%>"/>
		<input type="hidden" id="nFolioConvenio" name="nFolioConvenio" value="<%=nFolioConvenio%>"/>
		<input type="hidden" id="operador" name="operador" value="<%=u.getNombre(  )%>"/>
		<input type="hidden" id="fechaCaptura" name="fechaCaptura" value="<%=fechaCaptura%>"/>
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=ejercicioFiscal%>"/>
		<input type="hidden" id="cTipoRfc" name="cTipoRfc" value="7"/>
		<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO" value=""/>
		
		<div id="container" class="container">
		
			
			<div class= "card">
				<div class="card-header">
					<h5>Captura de Convenio de Colaboracion</h5>
				</div>
				<div class="card-body">
				     <div id="ConvenioColaboracion" class="col-12">
				        <div class="row">
				        	<div class="col-3">
				        		<label for="cIdContrato">Numero de Convenio:</label>
				        		<input type="text" class="form-control"  id="cIdContrato" name="cIdContrato" value="" size="60" />
				        	</div>
				        </div>
				        <div class="row">
				        	<div class="col-3">
				        		<label for="rfc">Beneficiario:</label>
				        		<div class="input-group">
				        			<input type="text" class="form-control"  id="rfc" name="rfc" size="14" readonly/>
				        			<input type="button" class="btn btn-secondary" value="..." id= "ayuda" onclick="ayudaBeneficiarios()"/>
				        		</div>
				        	</div>
				        	<div class="col-6">
				        		<label for="cNombre">Razón Social:</label>
				        		<input type="text" class="form-control"  id="cNombre" name="cNombre" size="45" readonly/>
				        	</div>
				        </div>
				         <div class="row">
				        	<div class="col-3">
				        		<label for="fInicio">Inicio del Convenio</label>
				        		<input type="text" id="fInicio" name="fInicio" class="fecha form-control" readonly size="11"/>
				        	</div>
				        	<div class="col-3">
				        		<label for="fFin">Fin del Convenio</label>
				        		<input type="text" id="fFin" name="fFin" class="fecha form-control" readonly size="11"/>
				        	</div>
				        </div>
				         <div class="row">
				        	<div class="col-2">
				        		<label for="importeBruto">Importe Bruto</label>
				        		<input type="text" class="form-control money"  id="importeBruto" name="importeBruto" size="15" value="0.00" onkeypress="return onlyNumbers(event)"/>
				        	</div>
				        	<div class="col-2">
				        		<label for="importeBruto">%IVA</label>
				        		<select class="form-select" id="pcIVA" name="pcIVA" onchange="actualizaTotal()">
									<option value="">Seleccione</option>
								</select>
				        	</div>
				        	<div class="col-2">
				        		<label for="importeIVA">Importe IVA</label>
				        		<input type="text" class="form-control money" id="importeIVA" name="importeIVA" readonly size="15" value="0.00"/>
				        	</div>
				        	<div class="col-2">
				        		<label for="importeTotal">Importe Total</label>
				        		<input type="text" class="form-control money" id="importeTotal" name="importeTotal" readonly size="15" value="0.00"/>
				        	</div>
				        </div>
				         <div class="row">
				        	<div class="col-12">
				        		<label for="concepto">Concepto del Convenio</label>
				        		<textarea id="concepto" class="form-control"  name="concepto" cols="80" rows="5"></textarea>
				        	</div>
				        </div>
					</div>
				</div>
			</div>
			<div class= "mt-2 card">
				<div class="card-header">
					<h5>Retenciones</h5>
				</div>
				<div class="card-body">
					<div class="row">
				        	<div class="col-3">
				        		<label for="idRetencion">Retencion:</label>
				        		<select class="form-select" id="idRetencion">
									<option value="">Seleccionar Retencion</option>
								</select>
				        	</div>
				        	<div class="col-1">
				        		<label for="idRetencion">.</label>
				        		<input type="button" class="btn btn-secondary" id="btnAgregaRetencion" value="Agregar" onclick="agregaRetencion();"/>
				        	</div>
				        </div>
					
					<span style="display: block">*Doble clic para eliminar</span>
					<div id="divDTRetenciones">
						<table id="dt_retenciones" class="display" >
							<thead >
								<tr>
									<th align="center">ID</th>
									<th align="center">Retencion</th>
								</tr>										
							</thead>
						</table>
					</div>
				</div>	
			</div>
			
			<div class= "mt-2 card">
				<div class="card-header">
					<h5>Estructuras Presupuestales</h5>
				</div>
				<div class="card-body">
					<div class="row">
				        	<div class="col-6">
								<label for="btnEP">Seleccione EP:</label>
								<div class="input-group">
									<input type="text" class="form-control"  id="epSel" size="70" readonly/>
									<input type="button" class="btn btn-secondary"  value="..." id="btnEP" onclick="abreAyudaEP()"/>
								</div>
							</div>
							<div class="col-1">
								<label for="btnEP">.</label>
								<input type="button" class="btn btn-secondary"  value="Agregar EP" onclick="agregaEP()"/>
							</div>
					</div>
					
					<span style="display: block">*Doble clic para eliminar</span>
					<div id="divDTEP">
						<table id="dt_ep" class="display" >
							<thead >
								<tr>
									<th align="center">EP</th>
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			
		</div>
	</form>
</body>
</html>