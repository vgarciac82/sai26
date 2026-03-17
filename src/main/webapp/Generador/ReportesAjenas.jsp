<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Operaciones Ajenas</title>
		<meta http-equiv="Content-Type"
			content="text/html; charset=iso-8859-1" />
		<meta http-equiv="pragma" content="no-cache"/>
		<meta http-equiv="cache-control" content="no-cache"/>
		<meta http-equiv="expires" content="0"/>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
		<meta http-equiv="description" content="This is my page"/>
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico"/>
		<link rel="stylesheet" type="text/css" href="css/tcal.css"/>
		<style type="text/css" title="currentStyle"/>
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>
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
			<script type="text/javascript">
			
			var oTableP;
			var oTableNP;
			var dTSel;
			
function dataTableSel(num)
{
dTSel=num;
}			
			
$(document).ready(function() {
	$("#esperardet").hide();
	$('#divPagadas').hide();
	$('#divFaltantes').hide();
	querySelectPost("tGrupoOpAjenasRead", "cBeneficiario", {async: false });
	querySelectPost("PagosAMFAjenasRead", "PagosAMF", {async: false });
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	

	oTableP=$('#ReporteAjenas').dataTable( {

		//ScrollY : "100",
		sScrollX : "100%",
		sScrollXInner:"50%",
		bScrollCollapse:true,
		bPaginate : false,
		bLengthChange : false,
		bFilter : false,
		bSort : false,
		bInfo : false,
		bAutoWidth : true,
		bJQueryUI : true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType : "full_numbers",
		oLanguage : {
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
			sSearch : "Buscar:"
		}
	
	});
  
	oTableNP=$('#SinAplicar').dataTable( {

		//ScrollY : "100",
		sScrollX : "100%",
		sScrollXInner:"50%",
		bScrollCollapse:true,
		bPaginate : false,
		bLengthChange : false,
		bFilter : false,
		bSort : false,
		bInfo : false,
		bAutoWidth : true,
		bJQueryUI : true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType : "full_numbers",
		oLanguage : {
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
			sSearch : "Buscar:"
		}
	});
	
	
	$(function() {
			$( "#fBusquedaDe" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});	
		$(function() {
			$( "#fBusquedaHasta").datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});

		});
		
		

		$("#pbExcel")
			.button()
			.click(function() {
				if ( $("#szTemp").val() != "" ){
					//vdocument.ExportarForm.submit();	
					
				
var oTable;
var dTable="";
			
	

var nRow;
			
if(dTSel==1)
{
oTable=oTableP;
nRow =  $('#ReporteAjenas thead tr')[0];
}
else
{
oTable=oTableNP;
nRow =  $('#SinAplicar thead tr')[0];
}

		
var numColumns = oTable.fnGetData(0).length;  
var temp="";


for(var r=0;r<oTable.fnGetData().length;r++)
    {
        for(var c=0;c < numColumns;c++)
        {
          temp+=oTable.fnGetData(r,c)+"|";   
        }
        
    }
    

document.getElementById("tabla0").value = temp;

temp="";

if(dTSel==1)
{
temp="ContraRecibo|Folio SICOP|Folio SIAFF|Fecha CXP|Fecha OA Pagada SIAFF|R. F. C.|ContraRecibo Operación Ajena|EP|Total|Enteros|Sobrante|2/3 DEL IMPORTE DE I.V.A|2/3 DEL IMPORTE PAGADO|2/3 DEL IMPORTE DIFERENCIA|I.S.R. (HONORARIOS)|I.S.R. (HONORARIOS)PAGADO|I.S.R. (HONORARIOS)DIFERENCIA|I.S.R. (ARRENDAMIENTOS)|I.S.R. (ARRENDAMIENTOS) PAGADO|I.S.R. (ARRENDAMIENTOS) DIFERENCIA|FLETES (4.0%)|FLETES (4.0%)PAGADO|FLETES (4.0%)DIFERENCIA|APORTE I.M.D.T. ( 0.2%)|APORTE I.M.D.T. PAGADO|APORTE I.M.D.T. DIFERENCIA|APORTE C.N.I.C. ( 0.2%)|APORTE C.N.I.C. PAGADO|APORTE C.N.I.C. DIFERENCIA|INSPECCION DE OBRA (0.5%)|INSPECCION DE OBRA PAGADO|INSPECCION DE OBRA DIFERENCIA|PENALIZACIONES|PENALIZACIONES PAGADO|PENALIZACIONES DIFERENACIA|IMPUESTO CEDULAR|IMPUESTO CEDULAR PAGADO|IMPUESTO CEDULAR DIFERENCIA";
}
else
{
temp="ContraRecibo|Folio Documento|Fecha Aplicación|Tipo de Documento|E.P.|Total|Enteros|Sobrante|2/3 DEL IMPORTE DE I.V.A|I.S.R. (HONORARIOS)|I.S.R. (ARRENDAMIENTOS)|FLETES (4.0%)|APORTE I.M.D.T. ( 0.2%)|APORTE C.N.I.C. ( 0.2%)|INSPECCION DE OBRA (0.5%)|PENALIZACIONES|IMPUESTO CEDULAR";
}



document.getElementById("colsxTabla").value=oTable.fnGetData(0).length;
document.getElementById("encabezadosTabla0").value = temp;
document.getElementById("titulosxTabla").value = "OperAjenas";

temp="";

for(var i=0;i<oTable.fnGetData(0).length;i++)
{
if(dTSel==1)
	{
	
	if((i>8))
	{
	temp+="money"+"|";
	}
	else
	{
	temp+="String"+"|";
	}
	
	
	}
else
	{
	if(i>4)
	{
	temp+="money"+"|";
	}
	else
	{
	temp+="String"+"|";
	}
	}
}
document.getElementById("formatosTabla0").value = temp;
document.getElementById("tTablas").value = "1";


document.formulario.submit();
					
					
					
}

 });	

		$("#pbPdf")
			.button()
			.click(function() {
				var repPdf = ( $("#szTabla").val() == "REPAJENAS" ) ? "RepOperAjenasProc.jasper" : "RepOperAjenasPend.jasper";
				var cCriterios = '';
				cCriterios = "&TRetencion = '" + $("#cGrupo").val() + "' &fAplicacionDe = '" + $("#fBusquedaDe").val() + "' &fAplicacionHasta = '" + $("#fBusquedaHasta").val();
				cCriterios = cCriterios + "' &cCentroContable = '" + $("#cCentroContable").val() + "' &cCondiciones = '" + $("#cCondiciones").val();
				cCriterios = cCriterios + "' &cRetenciones = '" + $("#cSumaRetenciones").val() + "' &cNumPagoAMF = '" + $("#PagosAMF").val() + "'";
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=ANEXO"
						+ "&accion=run"
						+ "&rn=" + repPdf
						+ cCriterios,  
					"Anexo",
					"scrollbars=1, resizable=yes, width=1024, height=768");

		});	

});

function Busqueda(){
	$("#esperardet").show();
	$('#divFaltantes').hide();
	$('#divPagadas').show();
	
	dataTableSel(1);
	
	var grupo='';	
	var condiciones='';	
	var sumaRetenciones='';
	var nRows = $("#ReporteAjenas tr").length -1 ;
	
	if ( nRows > 0 ){
		var oBusqueda = $("#ReporteAjenas").dataTable();
		oBusqueda.fnClearTable();
	}
			

	var campos = "'" + $("#cGrupo").val() + "','"+ $("#fBusquedaDe").val() +"','"+ $("#fBusquedaHasta").val() +"','"+ $("#cCentroContable").val()+"','" + $("#cCondiciones").val() + "','" + $("#cSumaRetenciones").val() + "','" + $("#PagosAMF").val() + "'"
	$("#szTemp").val( campos );
	$("#szTabla").val( "REPAJENAS" );
	var szTabla = "REPAJENAS";
	var  elParametro2 ='';
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: elParametro2,Campos:campos, MaxReg: "", ajax: 'false'}, function(j){			
		var Nombre=0;
		valjson = j;
		var arrlist = new Array();
		for (var i = 0; i < j.length; i++) {
			arrlist[ i ] = [j[i].Col0, 
							j[i].Col27,
							j[i].Col38,
							j[i].Col2,
							j[i].Col39,
							j[i].Col26,
							j[i].Col37,
							j[i].Col4,
							j[i].Col5,
							j[i].Col6,
							j[i].Col7,
							j[i].Col8,  j[i].Col17, j[i].Col28,
							j[i].Col9,  j[i].Col18, j[i].Col29,
							j[i].Col10, j[i].Col19, j[i].Col30,
							j[i].Col11, j[i].Col20, j[i].Col31,
							j[i].Col13, j[i].Col22, j[i].Col32,
							j[i].Col12, j[i].Col21, j[i].Col33,
							j[i].Col14, j[i].Col23, j[i].Col34,
							j[i].Col15, j[i].Col24, j[i].Col35,
							j[i].Col16, j[i].Col25, j[i].Col36];
			
		}	
		$('#ReporteAjenas').dataTable().fnAddData ( arrlist );
		$("#esperardet").hide();
	});
		
}	
	
function Busquedafaltante(){
	$("#esperardet").show();
	$('#divFaltantes').show();
	$('#divPagadas').hide();
	
	dataTableSel(2);
	
	var grupo='';	
	var condiciones='';	
	var sumaRetenciones='';
	var nRows = $("#SinAplicar tr").length -1 ;
		
		if ( nRows > 0 ){
			var oBusqueda = $("#SinAplicar").dataTable();
			oBusqueda.fnClearTable();
		}
			
	var campos = "'" + $("#cGrupo").val() + "','"+ $("#fBusquedaDe").val() +"','"+ $("#fBusquedaHasta").val() +"','"+ $("#cCentroContable").val()+"','" + $("#cCondiciones").val() + "','" + $("#cSumaRetenciones").val() + "','" + $("#PagosAMF").val() + "'"
	$("#szTemp").val( campos );
	$("#szTabla").val( "REPAJENASFALTANTES" );
	var szTabla = "REPAJENASFALTANTES";
	var  elParametro2 ='';
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: elParametro2,Campos:campos, MaxReg: "", ajax: 'false'}, function(j){
				
		var Nombre=0;
		valjson = j;
		var arrlist = new Array();
	
		for (var i = 0; i < j.length; i++) {
				arrlist[ i ] = [j[i].Col0, 
								j[i].Col1,
								j[i].Col2,
								j[i].Col3,
								j[i].Col4,
								j[i].Col5,
								j[i].Col6,
								j[i].Col7,
								j[i].Col8,
								j[i].Col9,
								j[i].Col10,
								j[i].Col11,
								j[i].Col13,
								j[i].Col12,
								j[i].Col14,
								j[i].Col15,
								j[i].Col16];
		}	
		$('#SinAplicar').dataTable().fnAddData( arrlist );
		$("#esperardet").hide();
		
	});
		
}	

function grupoRetencion(){
	queryFormPost("tGrupoOpAjenasOnclicRead", {async: false });
}


	</script>
  </head>
<body id="dt_example">

<div id="buscando" style="visibility:hidden ">
	<div id="bloque">
	<img src="../imagenes/espera.gif"/><br>Por favor espere mientras se busca informaci&oacute;n en el sistema....
	</div>
	</div>

  <form id="ExportarForm" name="ExportarForm" action="../gstnmngr/generaLayoutOAServlet" method="post">
  	<input type="hidden" id="szTemp" name="szTemp" value="" />
	<input type="hidden" id="szTabla" name="szTabla" value="" />
	<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" />
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="cIdTipodocumento" name="cIdTipodocumento" />
	<input type="hidden" id="csubtmImportFlete1" name="csubtmImportFlete1" />
	<input type="hidden" id="csubHonor1" name="csubHonor1" />
	<input type="hidden" id="csubARR1" name="csubARR1" />
	<input type="hidden" id="csubFlete1" name="csubFlete1" />
	<input type="hidden" id="cIMDT1" name="cIMDT1" />
	<input type="hidden" id="cCNIC1" name="cCNIC1" />
	<input type="hidden" id="cmillar" name="cmillar" />
	<input type="hidden" id="csubPenal1" name="csubPenal1" />
	<input type="hidden" id="csubCedular1" name="csubCedular1" />
	<input type="hidden" id="cGrupo" name="cGrupo" />
	<input type="hidden" id="cCondiciones" name="cCondiciones" />
	<input type="hidden" id="cSumaRetenciones" name="cSumaRetenciones" />
	
	
	<div id="container" style="width: 100%">
		<h1>Reporte Operaciones Ajenas	<label style="font-size: 11pt"></label>	</h1>
		<select name="cBeneficiario" id="cBeneficiario"	onchange="grupoRetencion()" > </select>
		Desde:<input type="text" name="fBusquedaDe" id="fBusquedaDe" maxlength="12" size="12" />
		Hasta:<input type="text" name="fBusquedaHasta" id="fBusquedaHasta" maxlength="12" size="12" />                         	
		<input name="Buscar" type="button" id="Buscar" value="Buscar Pagadas" onclick="Busqueda()" />
		<input name="Buscar" type="button" id="Buscar" value="Buscar Sin Pagar" onclick="Busquedafaltante()" />
		<select  name="PagosAMF" id="PagosAMF" ></select> 
		<label id="esperardet">
			<div style="font-size: 13pt" align="center">
				<BR>Espere por favor....</BR>
				<img border="0" src="../imagenes/espera.gif" height="30"/>
			</div>
	    </label>             	
		</div>	
		<input type="button" id="pbExcel" value="Excel"/>    
		<input type="button" id="pbPdf" value="PDF"/>
			
        <div id="divPagadas" style="display: none;">
			<table id="ReporteAjenas" align="center"   width="100%">
				<thead>
					<tr align="center">	
						<th>ContraRecibo</th>
						<th>Folio SICOP</th>
						<th>Folio SIAFF</th>
						<th>Fecha CXP</th>
						<th>Fecha OA Pagada SIAFF</th>
						<th>R. F. C.</th>
						<th>ContraRecibo Operación Ajena</th>
						<th>EP</th>
						<th>Total</th>
						<th>Enteros</th>
						<th>Sobrante</th>
						<th>2/3 DEL IMPORTE DE I.V.A</th>
						<th>2/3 DEL IMPORTE PAGADO</th>
						<th>2/3 DEL IMPORTE DIFERENCIA</th>
						<th>I.S.R. (HONORARIOS)</th>
						<th>I.S.R. (HONORARIOS)PAGADO</th>
						<th>I.S.R. (HONORARIOS)DIFERENCIA</th>
						<th>I.S.R. (ARRENDAMIENTOS)</th>
						<th>I.S.R. (ARRENDAMIENTOS) PAGADO</th>
						<th>I.S.R. (ARRENDAMIENTOS) DIFERENCIA</th>
						<th>FLETES (4.0%)</th>
						<th>FLETES (4.0%)PAGADO</th>
						<th>FLETES (4.0%)DIFERENCIA</th>
						<th>APORTE I.M.D.T. ( 0.2%)</th>
						<th>APORTE I.M.D.T. PAGADO</th>
						<th>APORTE I.M.D.T. DIFERENCIA</th>
						<th>APORTE C.N.I.C. ( 0.2%)</th>
						<th>APORTE C.N.I.C. PAGADO</th>
						<th>APORTE C.N.I.C. DIFERENCIA</th>
						<th>INSPECCION DE OBRA (0.5%)</th>
						<th>INSPECCION DE OBRA PAGADO</th>
						<th>INSPECCION DE OBRA DIFERENCIA</th>
						<th>PENALIZACIONES</th>
						<th>PENALIZACIONES PAGADO</th>
						<th>PENALIZACIONES DIFERENACIA</th>
						<th>IMPUESTO CEDULAR</th>
						<th>IMPUESTO CEDULAR PAGADO</th>
						<th>IMPUESTO CEDULAR DIFERENCIA</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="divFaltantes" style="display: none;" >
			<table id="SinAplicar"  align="center"   width="100%">
				<thead>
					<tr align="center">
						<th>ContraRecibo</th>
						<th>Folio Documento</th>
						<th>Fecha Aplicaci&oacute;n</th>
						<th>Tipo de Documento</th>
						<th>E.P.</th>
						<th>Total</th>
						<th>Enteros</th>
						<th>Sobrante</th>
						<th>2/3 DEL IMPORTE DE I.V.A</th>
						<th>I.S.R. (HONORARIOS)</th>
						<th>I.S.R. (ARRENDAMIENTOS)</th>
						<th>FLETES (4.0%)</th>
						<th>APORTE I.M.D.T. ( 0.2%)</th>
						<th>APORTE C.N.I.C. ( 0.2%)</th>
						<th>INSPECCION DE OBRA (0.5%)</th>
						<th>PENALIZACIONES</th>
						<th>IMPUESTO CEDULAR</th>
					</tr>
				</thead>
			</table>
		</div>
	</form>
	

<form action="../reports/GeneraExcelTable" method="post" id="formulario" name="formulario">
    <input type="hidden" name="tabla0" id="tabla0" />
	<input type="hidden" name="colsxTabla" id="colsxTabla" />
	<input type="hidden" name="titulosxTabla" id="titulosxTabla" />
	<input type="hidden" name="encabezadosTabla0" id="encabezadosTabla0" />
	<input type="hidden" name="formatosTabla0" id="formatosTabla0" />
	<input type="hidden" name="tTablas" id="tTablas" />
	</form>	
	
	
</body>	
</html>


