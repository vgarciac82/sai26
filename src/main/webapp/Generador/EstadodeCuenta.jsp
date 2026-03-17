<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
String DATE_FORMAT = "yyyy-MM-dd";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today= sdf.format(c1.getTime());
String cCentroContable="";
String cUR = "";
String cRamo = "";
boolean bAplicadoCont=false;

//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
//if (c == null) {
//	response.sendRedirect("../index.jsp");
//	return;
//}


	
Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

//documentos del caso
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

//Valida Centro de Costos
if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
            cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
}


cUR = usuario.getU_UR();
cRamo = usuario.getU_Ramo();



%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Genera Compromiso</title>

	<link rel="stylesheet" type="text/css" href="../../SISECOP/css/style.css">
	<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
	
	<style type="text/css" title="currentStyle">
		
		@import "css/demo_table_jui.css";
		h4 {
			font-size: 1.3em;
			font-weight: normal;
			line-height: 1.6em;
			color: #4E6CA3;
			border-bottom: 1px solid #B0BED9;
			clear: both;
			margin-top: 0px;
		}
	</style>

	<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	

<script type="text/javascript" charset="utf-8">
	var bCarga = false;
var mx = {
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
	};

$(document).ready(function() {
	var oTable = $('#dt_compromiso').dataTable();
    var nEditing = null;
	
			
		    $("input.AyudaSyC").subIniciaDlg();
		    

			$('#dt_compromiso').dataTable(
				{
	   			    "bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false, 
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );

			$('#dt_pago').dataTable(
				{
					
	   			    "bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );


			querySelectPost("tEjercicioRead", "cEjercicio", {async: false });

			$("#cIdContratoCompromiso").change(function () {
				var vcontrato = $(this).val();
				
				
				$("#szTemp").val( "cIdContrato='" + vcontrato + "' and cIdEntidadContable = '" + "<%=cCentroContable%>" + "' and cDocumentoHaplicado = 'S'" );
				queryFormPost("vListaContratosRead", {async: false });
				
				
				$("#mImporteContrato").formatCurrency();
				$("#mImporteIVA").formatCurrency();

				creaTablaCompromisos(vcontrato);
				
		});

		$("#pbExcel")
			.button()
			.click(function() {
				if ( $("#szTemp").val() != "" ){
					document.ExportarForm.submit();	
				}

		});	

		$("#pbPdf")
			.button()
			.click(function() {

				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=ANEXO"
						+ "&accion=run"
						+ "&rn=EstadodeCuenta.jasper"
						+ '&whereFolio=' + $("#szTemp").val(),  
					"Anexo",
					"scrollbars=1, resizable=yes, width=1024, height=768");

		});	


});


	function fnTotales(){
		// tabla de compromisos
		var oTblComp = $('#dt_compromiso').dataTable();
		var aData = oTblComp.fnGetData();
		var nRows = $("#dt_compromiso tr").length -1;
		var j = 0;
		
		$("#mSaldoCompromiso").val(0);
		var sumaCompromiso = 0.0;
		
		for(var i=0; i < nRows; i++){
			var paso = aData[ i ];
			
			if (paso != undefined){
				 sumaCompromiso += Number( quitaFmt( paso[5] ) ) ;
				
			}
		}
		
		$("#mSaldoCompromiso").val( sumaCompromiso.toFixed(2)  );
	
		// tabla de facturas
		var oTblPago = $('#dt_pago').dataTable();
		var aData = oTblPago.fnGetData();
		var nRows = $("#dt_pago tr").length -1;
		var j = 0;
		$("#mSaldoFacturas").val(0);
		
		for(var i=0; i < nRows; i++){
			var paso = aData[ i ];
			if (paso != undefined){
				$("#mSaldoFacturas").val( Number($("#mSaldoFacturas").val()) + Number( quitaFmt( paso[3] ) ) - Number( quitaFmt( paso[4] ) ) + Number( quitaFmt( paso[5] ) )  - Number( quitaFmt( paso[6] ) ) + Number( quitaFmt( paso[9] ) ) );
			}
		}
		queryFormPost("leeImporteDisminucion", {async: false }); 
		$("#mSaldoContrato").val( ( Number( $("#mImporteTotal").val() ) - Number($("#mSaldoFacturas").val() ) - Number($("#mImporteDisminucion").val()) ).toFixed(2)  );
		$("#mSaldoContrato").formatCurrency();

		$("#mSaldoCompromiso").formatCurrency();
		$("#mSaldoFacturas").formatCurrency();
		$("#mImporteTotal").formatCurrency();
	}
	
	
	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}	
	
	function cmdGuardar(){
			return 0
        }



	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}	
	
	function Sinfrmt( fld )
	{
	   	var valcol = fld.value ;
	   	var vcompr = $("#mComprometer").val();
	   	vcompr = quitaFmt( vcompr );
	   	valcol = quitaFmt( valcol );
		$("#" + fld.id).val( valcol );
	   	fld.select();
		$("#mComprometer").val( parseFloat( vcompr ) - parseFloat( valcol ) );
		$("#mComprometer").formatCurrency();
}

	function cambiafrmt( fld )
	{
	   	var vcompr = $("#mComprometer").val();
	   	var vfld = $("#" + fld.id).val()
	   	if (vfld == "")
	   		vfld = '0';
		vcompr = quitaFmt( vcompr );
	   	$("#mComprometer").val( parseFloat(vcompr) + parseFloat( vfld ) );
		$("#" + fld.id).formatCurrency();
		$("#mComprometer").formatCurrency();
	}

	function creaTablaCompromisos(vcontrato) {
	
		$('#dt_compromiso').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					oLanguage: mx,
					bAutoWidth: false,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCntrtoComp&qw=cIdContrato='" + vcontrato + "' ", //and cCentroContable = '" + "<%=cCentroContable%>" + "'",
					aoColumns: [
						{ sName: "CanoCompromiso" },		
						{ sName: "nDocumento" },
						{ sName: "cFolioCompromisoSICOP" },
						{ sName: "cIdProceso" },
						{ sName: "EP" },
						{ sName: "mImporteCompromiso" },
						{ sName: "cEstatus" },
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }],
					"fnDrawCallback" : function( settings ){
							createTablaPagos(vcontrato);
					}
                } ) ;
	}
	
	function createTablaPagos(vcontrato){
		$('#dt_pago').dataTable( {
					bPaginate: false,
        			bFilter: false,
        			bInfo: false,
					oLanguage: mx,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vEdoCtaPagos&qw=cIdContrato='" + vcontrato + /*"' and cCentroContable = '" + "<%=cCentroContable%>" +*/ "' and cDocumentoHaplicado = 'S'",
					aoColumns: [
						{ sName: "CaNoContrarrecibo" },
						{ sName: "nFolioPagoObra" },
						{ sName: "cEstatus" },
						{ sName: "mImporteBruto" },
						{ sName: "mImporteSancion" },
						{ sName: "mImporteDevolucion" },
						{ sName: "mAmortizacionAnticipo" },
						{ sName: "mImportePenalizacion" },
						{ sName: "mImporteRetencion" },
						{ sName: "mImporteIVA" },
						{ sName: "mImporteNeto" },
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }],
						"fnDrawCallback" : function( settings ){
							fnTotales();
						}
                } ) ;
	}
</script>


</head>

	<body id="dt_example">
	<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/generaLayoutEdoCtaServlet" method="post">
	<input type="hidden" name="szTemp" id="szTemp" value="">
	<input type="hidden" name="mImporteDisminucion" id="mImporteDisminucion" value="0">
	<select style="visibility: hidden" name="cEjercicio" id="cEjercicio"><option value="0">cero</option></select>
	<input readonly type="hidden" size="15" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>"></td>
	
	<div  id="container" class="container">
		<h4>Estado de Cuenta</h4>	
		<div class="row mt-3">
			<div class="col-1">
				Clave de Contrato: 
			</div>
			<div class="col-2">
			 	<div class="input-group mb-3">
					<input type="text" class="AyudaSyC form-control" name="cIdContrato" id="cIdContratoCompromiso" readonly />
				</div>
			</div>
			<div class="col-2">
				Tipo Contrato :
			</div>
			<div class="col-2">
				<input readonly type="text" size="15" name="cTipoContrato" id="cTipoContrato" class="form-control"/>
			</div>
		</div>
		<div class="row">
			<div class="col-1">
				R.F.C. :
			</div>
			<div class="col-10">
				<div class="input-group mb-10">
					<div class="col-2">
			 		<input readonly type="text" maxlength="15" size="15" name="cIDRFC" id="cIDRFC" class="form-control"/>
			 		</div>
			 		<div class="col-8">
			 		<input readonly type="text" maxlength="80" size="80" name="cnombre" ID="cnombre" class="form-control"/>
					</div>
				</div>	
			</div>
			
		</div>
		
		<div class="row mt-2">
			<div class="col-1">
				Concepto:
			</div>
			<div class="col-9">
			 	<textarea name="cConceptoContrato"  readonly class="form-control" ID="cConceptoContrato" row="3"></textarea>
			</div>
			<div class="col-2">
				<div class="input-group mb-2">
				  <div class="input-group-prepend">
				    <span class="input-group-text" id="basic-addon1"><i class="bi bi-filetype-xls"></i></span>
				  </div>
				  <input type="button" id="pbExcel" value="Excel" class="btn btn-secondary"/>
				</div>
				
			</div>
			
		</div>
		<div class="row mt-2">
			<div class="col-1">
			</div>
			<div class="col-1">
				SubTotal:
			</div>
			<div class="col-2">
			 	<input style="text-align:right;" readonly type="text" maxlength="16" size="16" name="mImporteContrato"  id="mImporteContrato" value="0" class="form-control"/>
			</div>
			<div class="col-1">
				IVA:
			</div>
			<div class="col-2">
				<input style="text-align:right;" readonly type="text" maxlength="16" size="16" name="mImporteIVA"  id="mImporteIVA" class="form-control"/>
			</div>
			<div class="col-1">
				Total:
			</div>
			<div class="col-2">
				<input style="text-align:right;" readonly type="text" maxlength="16" size="16" name="mImporteTotal"  id="mImporteTotal" class="form-control"/>
			</div>
			<div class="col-2">
				<div class="input-group mb-3">
				  <div class="input-group-prepend">
				    <span class="input-group-text" id="basic-addon1"><i class="bi bi-file-pdf"></i></i></span>
				  </div>
				  <input type="button" id="pbPdf" value="PDF" class="btn btn-secondary"/>
				</div>
				
			</div>
		</div>
	
		<table id="dt_compromiso" class="display">
				<thead>
					<tr align="center">
						<th>Clave Compromiso</th>
						<th>#Compromiso SICOP</th>
						<th>#Folio SICOP</th>
						<th>#Proceso SICOP</th>
						<th>Clave Presupuestal</th>
						<th>Importe</th>
						<th>Estatus</th>
					</tr>
				</thead>
	        </table>
	
		<br>
		<div class="d-flex flex-row-reverse">
			<div class="col-2"> 
				<input style="text-align:right;" readonly type="text" name="mSaldoCompromiso"  id="mSaldoCompromiso" class="form-control"/>
			</div>
			<div class="col-2">
				<label for="mSaldoCompromiso"> Saldo Compromiso:</label>
			</div>
			 
		</div>
		
			<table id="dt_pago" class="display">
				<thead>
					<tr align="center">
						<th>Cuenta por Pagar</th>
						<th>Folio Pago</th>
						<th>Estatus</th>
						<th>Importe Bruto</th>
						<th>Importe Sanción</th>
						<th>Importe Devolución</th>
						<th>Importe Amortización</th>
						<th>Importe Penalización</th>
						<th>Importe Retención</th>
						<th>Importe IVA</th>
						<th>Importe Neto</th>
					</tr>
				</thead>
	        </table>
		
		<div class="d-flex flex-row-reverse">
			<div class="col-2"> 
				<input style="text-align:right;" readonly type="text"  name="mSaldoFacturas"  id="mSaldoFacturas" class="form-control"/>
			</div>
			<div class="col-2">
				<label for="mSaldoFacturas"> Saldo Facturas:</label>
			</div>
			
		</div>
		
		<div class="d-flex flex-row-reverse">
			<div class="col-2"> 
				<input style="text-align:right;" readonly type="text"  name="mSaldoContrato"  id="mSaldoContrato" class="form-control"/>
			</div>
			<div class="col-2">
				<label for="mSaldoContrato"> Saldo en Contrato:</label>
			</div>
			 
		</div>
	
	</div>
</form>
</body>
</html>
