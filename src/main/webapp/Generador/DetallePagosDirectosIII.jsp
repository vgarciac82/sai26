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

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
//if (c == null) {
	//response.sendRedirect("../index.jsp");
	//return;
//}

//if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
	//if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
		//bAplicadoCont = true;
//}

String mensaje="";
if(request.getParameter("msg")!=null&&!"".equals(request.getParameter("msg"))){
	mensaje=request.getParameter("msg");
	mensaje=mensaje.replace("[","");
	mensaje=mensaje.replace("]","");
	mensaje=mensaje.replace(",","<br>");
}

//int id_oper = -1;
//if(request.getParameter("id_oper")!=null)
	//id_oper= new Integer(request.getParameter("id_oper")).intValue();
//else
	//id_oper=c.getCasoOperacion(0).getIdOperacion();
	
Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

//documentos del caso
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

//String select=c.getTipoCaso().getGavetaAsociada()+"_G"+c.getIdGabinete();//se usa por separado abajo
//String cAplicaDocto="No";
//if ( request.getParameter("aplicaDocto")!= null && request.getParameter("aplicaDocto").equals("Si")){
//	cAplicaDocto="Si";
//}
//Valida Centro de Costos
if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
            cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

if (cCentroContable.isEmpty() || cCentroContable.equals("")){
		mensaje="Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
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
	var bCarga = false;

$(document).ready(function() {
	/* Add a click handler to the rows - this could be used as a callback */
	$("#dt_compromiso tbody").click(function(event) {
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
		//editRow(oTable, 1);
	});
	
	$("#pbCancelar")
	.button()
	.click(function() {
		location.href = 'IntegraLayoutPagosDirecto.jsp';
		
	});
	
	/* Init the table */
	oTable = $('#dt_compromiso').dataTable( );
	//PSC
	$("#nFolioPagoDirecto").val('<%=request.getParameter("folio")%>');
	//querySelectPost("tPagosDirectosRead", {async: false });
	queryFormPost("PagosDirectosRead", {async: false });
	} );

    $('#edit').click( function () {
       // e.preventDefault();
         
        /* Get the row as a parent of the link that was clicked on */
        var nRow = fnGetSelected( oTable ) ; 
        	//$(this).parents('tr')[0];
         
    } );
  	
  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		//var valida_doctos_requeridos = true;
  		//validaciones de documentos requeridos
  		//return valida_doctos_requeridos;
  		//return confirm("Confirmar que quiere avanzar a la siguiente operación.");
  		return true;
  	}
  	
  	function onLoadPlantilla(){

  		//parent.document.getElementById("pb_send").value='Aplicar';
  		parent.document.getElementById("pb_send").style.visibility='hidden';
		parent.document.getElementById("pb_cancel").disabled=true;
		//parent.document.getElementById("pb_save").disabled=true;
		
		if(<%=bAplicadoCont%>==true){
		//	parent.document.getElementById("pb_save").disabled=true;
		}
  	
  	}
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1)
  		 	return "VENTANILLA_COMPROMISO";
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "consulta_compromiso";
  	}

	function onPostDisplay(){
	
	}
		
/* Get the rows which are currently selected */
function fnGetSelected( oTableLocal )
{
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	
	for ( var i=0 ; i<aTrs.length ; i++ )
	{
		//if ( $(aTrs[i]).hasClass('row_selected') )
		//{
			aReturn.push( aTrs[i] );
			editRow ( oTable, aReturn );
			aReturn.shift();
		//}
	}
	return aReturn;
}


function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '-0123456789.';

	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal

	return true 
	//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
}



</script>


</head>

	<body id="dt_example">
<form>

<div  id="container" class="container SyCData">
	<br>
	<br>
	<br>
	<h1>Pagos Directos</h1>
	<table id="clvcont">
	
	    <tr><td>Folio Pago Directo: <input readonly type="text" maxlength="10" size="10" name="nFolioPagoDirecto" id="nFolioPagoDirecto"></td>
	    <td>Fecha Expedición :<input readonly type="text" size="10" name="fCarga" id="fCarga"></td>
	    <td>Fecha Aplicación :<input readonly type="text" size="10" name="fAplicacion" id="fAplicacion"></td>
	    
	</table>
	<table>
		<tr><td>Ramo :<input readonly type="text" size="10" name="cRamo" id="cRamo"></td>
		<td>Unidad Responsable :<input readonly type="text" size="50" name="cUnidadResponsable" id="cUnidadResponsable"></td>
	</table>
	<table>
		<tr><td>Ejercicio Fiscal :<input readonly type="text" size="10" name="aEjercicioFiscal" id="aEjercicioFiscal"></td>
		<td>Entidad Contable :<input readonly type="text" size="10" name="cIdEntidadContable" id="cIdEntidadContable"></td>	
		<td>Documento :<input readonly type="text" size="10" name="cIdDocumento" id="cIdDocumento"></td>
		<td>Tipo Documento :<input readonly type="text" size="10" name="cIdTipoDocumento" id="cIdTipoDocumento"></td>
	</table>
	<table>
		<tr><td>Tipo Monto Desembolso :<input readonly type="text" size="10" name="cIdTipoMontoDesembolso" id="cIdTipoMontoDesembolso"></td>
		<td>RFC :<input readonly type="text" size="15" name="cIdRFC" id="cIdRFC"></td>
	</table>
	
	<h1>Datos</h1>
	<table>
		<tr><td>Fecha Recepción :<input readonly type="text" size="10" name="fRecepcion" id="fRecepcion"></td>
		<td>Fecha Revisión :<input readonly type="text" size="10" name="fRevision" id="fRevision"></td>
		<td>Fecha Programada de Pago :<input readonly type="text" size="10" name="fProgramadaPago" id="fProgramadaPago"></td>
	</table>
	<table>
		<tr><td>Concepto :<input readonly type="text" size="10" name="cConcepto" id="cConcepto"></td>
		<td>Vigencia IVA :<input readonly type="text" size="10" name="fVigenciaIVA" id="fVigenciaIVA"></td>
	</table>
	<table>
		<tr><td>% IVA :<input readonly type="text" size="10" name="nPorcIVA" id="nPorcIVA"></td>
		<td>Importe Bruto :<input readonly type="text" size="10" name="mImporteBruto" id="mImporteBruto"></td>
		<td>Importe IVA :<input readonly type="text" size="10" name="mImporteIVA" id="mImporteIVA"></td>
		<td>Importe Retención :<input readonly type="text" size="10" name="mImporteRetencion" id="mImporteRetencion"></td>
	</table>
	<table>
		<tr><td>Importe Neto :<input readonly type="text" size="10" name="mImporteNeto" id="mImporteNeto"></td>
	</table>
	<table>
		<input type="button" id="pbCancelar" value="Cancelar"/>	
	</table>
</div>
</form>
</body>
</html>
