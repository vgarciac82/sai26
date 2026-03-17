<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%
String DATE_FORMAT = "yyyy-MM-dd";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today= sdf.format(c1.getTime());
String cCentroContable="";
String cUR = "";
String cRamo = "";

Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

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

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>

	<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
    var bEditing = false;
	
	var oTable = $('#dt_regularizaPD').dataTable( {
				bPaginate: false,
       			bLengthChange: false,
       			bFilter: false,
       			bInfo: false,
				oLanguage: {
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
				},
				bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 450,
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,  
				bRetrieve: true,  
				bProcessing: true, 
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegularizaPD&qw=cUnidadResponsable='" + "<%=cUR%>" + "'",
				aoColumns: [
					{ sName: "nFolioPagoDirecto" },
					{ sName: "cIdDocumento" },
					{ sName: "EP" },
					{ sName: "RFC" },
					{ sName: "cMes" },
					{ sName: "mImporteMasIVA" },
					{ sName: "cCentroContable" },
					{ sName: "ALM" },
					{ sName: "altaAlmacen" },
				   	{ sName: "nDocRenglon",	bSearchable: false,	bSortable: false, bVisible: false }
				   ]
               } ) ;

	$('#dt_grabaPD').dataTable({
			"iDisplayLength": 10000,
      			"bPaginate": false,
      			"bFilter": false,
      			"bSort": false,
      			"bInfo": false } );


    $('#edit').click( function () { 
    	document.getElementById("esperar").style.visibility="visible"; 
		bEditing = true;
		var nRow = fnGetSelected( oTable );
		document.getElementById("esperar").style.visibility="hidden";
    } );

    $('#exit').click( function () {
		response.sendRedirect("../index.jsp");
	} );

	$('#save').click( function () {
    	document.getElementById("esperar").style.visibility="visible"; 
		if (bEditing) {
			var nRet = cmdGuardar();
			
			if (nRet == 0) {
	    		alert("Información Guardada Exitosamente!!!!");
	    	}else {
	    		alert("No Fue posible Guardar la Información");
	    	}
    	}else {
    		alert("No se ha Editado la Información");
    	}
    	document.getElementById("esperar").style.visibility="hidden"; 

    } );

    //$("#grbPD").hide();
});


	function cmdGuardar() {
		var vdocren = 1 ;
		var nRows = $("#dt_regularizaPD tr").length -1 ;
		var oTable = $('#dt_regularizaPD').dataTable();
		var oTablD = $('#dt_grabaPD').dataTable();
		var aTrs = oTable.fnGetNodes();
		var vrowAf = "";
		
		oTablD.fnClearTable();
		for ( var i=0 ; i<nRows ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			var nfolio 	= aData[ 0 ];
			var vep = aData[ 2 ];
			var cCCont = jqInputs[0].value;
			var cAlmac = jqInputs[1].value;
			var cNumAA = jqInputs[2].value;
			var ndocren = aData[ 9 ];
			if (cCCont != "") {
				$('#dt_grabaPD').dataTable().fnAddData( [
						'<td><input type="text" id="nFolioPagoDirecto" name="nFolioPagoDirecto" value="' + nfolio + '"></td>',
						'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + ndocren + '"></td>',
						'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + cCCont + '"></td>',
						'<td><input type="text" id="ALM" name="ALM" value="' + cAlmac + '"></td>',
						'<td><input type="text" id="altaAlmacen" name="altaAlmacen" value="' + cNumAA + '"></td>'
					] );
			}
		}
				
		queryFormPost("tReguralizaPDUPDATE", {async: false });
		vrowAf = $("#rowsAffected").val();
		if (vrowAf == "0") {
			//return -1;
		}
		return 0;
	}


	
	function editRow ( oTable, nRow ) {
	    var aData = oTable.fnGetData(nRow[0].rowIndex -1);
		$("#dt_regularizaPD").children().children()[nRow[0].rowIndex].children[6].innerHTML = '<input style="width: 100%" type="text" id="CC-' + nRow[0].rowIndex + '" name="cCContable" value="' + aData[6] + '">'; 
		$("#dt_regularizaPD").children().children()[nRow[0].rowIndex].children[7].innerHTML = '<input style="width: 100%" type="text" id="alm-' + nRow[0].rowIndex + '" name="ALMI" value="' + aData[7] + '">'; 
		$("#dt_regularizaPD").children().children()[nRow[0].rowIndex].children[8].innerHTML = '<input style="width: 100%" type="text" id="numalm-' + nRow[0].rowIndex + '" name="altaAlmacenI" value="' + aData[8] + '">'; 

	}



/* Get the rows which are currently selected */
function fnGetSelected( oTableLocal )
{
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	
	for ( var i=0 ; i<aTrs.length ; i++ ){
		aReturn.push( aTrs[i] );
		editRow ( oTableLocal, aReturn );
		aReturn.shift();
	}

	return aReturn;
}

</script>

</head>
<body id="dt_example">
	<form>
		<div  id="container" class="container SyCData">
			<h1>Regulariza Pago Directo</h1>
			<div align="center">
			<label id="esperar" style="visibility: hidden">Espere por favor....
			  <img border="0" src="../imagenes/espera.gif" height="30">
			</label>
			</div>
			<input type="hidden" id="rowsAffected" name="rowsAffected" value="0">
			<div id="container" class="container SyCData">
				<div id="demo1">
					<table class="display">
						<tr>
							<td><a href="javascript:void(0)" id="edit">Editar</a></td>
							<td>&nbsp;</td>
							<td><a href="javascript:void(0)" id="save">Guardar</a></td>
						</tr>
					</table>
					
					<table id="dt_regularizaPD" class="display">
						<thead>
							<tr align="center">
								<th>Folio</th>
								<th>Documento</th>
								<th>Estructura Programática</th>
								<th>R. F. C.</th>
								<th>Mes</th>
								<th>Importe Neto</th>
								<th>C.Cont</th>
								<th>Almacén</th>
								<th>#Almacenaria</th>
								<th>nDocRenglon</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
			        </table>
			    </div>
				<div id="grbPD" >
					<table id="dt_grabaPD" class="display">
						<thead>
							<tr align="center">
								<th>Folio</th>
								<th>nDocRenglon</th>
								<th>Centro Contable</th>
								<th>Almacén</th>
								<th>#Almacenaria</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
			        </table>
			    </div>
			</div>
		</div>
	</form>
</body>
</html>
