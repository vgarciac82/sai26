<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<%
%>
  <head>
    <title>Auxiliares</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	

	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
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

	var rowCount = 0 ;

$(document).ready(function()
{
	$('.currency').blur(function()
	{
		$('.currency').formatCurrency();
	});
});

$(document).ready(function()
		{



		    $("#tabs").tabs( {
		        "show": function(event, ui) {
		            var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		            if ( oTable.length > 0 ) {
		                oTable.fnAdjustColumnSizing();
		            }
		        }
		    } );

		    $('table.display').dataTable( {
		        "sScrollY": "200px",
		        "sScrollX": "100%",
			    "sScrollXInner": "100%",
		        "bScrollCollapse": true,
		        "bPaginate": false,
		        "bJQueryUI": true,
		        "aoColumnDefs": [
		            { "sWidth": "10%", "aTargets": [ -1 ] }
		        ]
		    } );


			$('#datosfec').dataTable(
				{
	   			    "bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 30,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );

			$('#dt_ReportePre').dataTable(
				{
	   			     "bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": true,
					"sScrollY": 100,
			        "sScrollX": "100%",
			        "sScrollXInner": "100%",
			        "bScrollCollapse": true,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
			});

		$(function() {
			$( "#fAuxIni" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "../Generador/images/calendar.gif",
				buttonImageOnly: true
			});
		});
		$(function() {
			$( "#fAuxFin" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "../Generador/images/calendar.gif",
				buttonImageOnly: true
			});
		});

	//Consultar Auxiliar y Subcuenta
	function mostrarOcultar(obj) {
  	document.getElementById('cSubcuenta').style.visibility = (obj.checked) ? 'visible' : 'hidden';
	document.getElementById('LabelSubCuenta').style.visibility = (obj.checked) ? 'visible' : 'hidden';
		if((document.FormAuxiliares.Auxiliar.checked) == true) {
		    $("#Rango").attr("disabled", true);
		    }
			if((document.FormAuxiliares.Auxiliar.checked) == false) {
		    $("#Rango").attr("disabled", false);
		    	}
		}
	function mostrarOcultarSBC(obj) {
  	document.getElementById('nCuenta2').style.visibility = (obj.checked) ? 'visible' : 'hidden';
  	document.getElementById('nCuenta2A2').style.visibility = (obj.checked) ? 'visible' : 'hidden';
  	document.getElementById('nCuenta2A3').style.visibility = (obj.checked) ? 'visible' : 'hidden';
  	document.getElementById('nCuenta2A4').style.visibility = (obj.checked) ? 'visible' : 'hidden';
	document.getElementById('LabelnCuenta2').style.visibility = (obj.checked) ? 'visible' : 'hidden';
	if((document.FormAuxiliares.Rango.checked) == true) {
	    $("#Auxiliar").attr("disabled", true);
	    }
		if((document.FormAuxiliares.Rango.checked) == false) {
	    $("#Auxiliar").attr("disabled", false);
	    	}
	}
	////////////////////////////////

	function autotab(current,to){
    if (current.getAttribute &&
      current.value.length==current.getAttribute("maxlength")) {
        to.focus()
        }
	}

	function ceros(obj) {
	  numCeros = '00000'; // pon el nº de ceros que necesites
	  valor = obj.value;
	  valor = numCeros.substring(0,numCeros.length-valor.length)+valor;
	  obj.value = valor;
	}


	function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	if (keyPressed == 47)
	{
	return false;
	 }
	return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

	function Valida (){
	if ($("#cCentroContable").val()==''){
			alert("Por Favor Ingrese el Centro Contable");
			return false;
			}
	if ((document.FormAuxiliares.Rango.checked) == false && (document.FormAuxiliares.Auxiliar.checked) == false){
	Generar();
		}
	if ((document.FormAuxiliares.Rango.checked) == true){
	GenerarRango();
		}
	if ((document.FormAuxiliares.Auxiliar.checked) == true){
	GenerarAuxiliar();
		}
	}


	function Generar(){
	var CuentaVal = $('#nCuenta').val();
		CuentaVal = CuentaVal.substring(1,0);
	if (CuentaVal == '') {
	    alert ("La Cuenta es un campo requerido");
		return ;
		}
	if (CuentaVal == '8') {
	    alert ("La Cuenta ingresada no es una cuenta valida");
		return ;
		}

				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=Auxiliares.jasper"
						+ "&swhere=nCuenta LIKE '%25"+ $("#nCuenta").val()+"%25' AND fMovimiento BETWEEN '"+ $("#fAuxIni").val()+"' AND '"+ $("#fAuxFin").val()+"' AND mMovimiento != '0.00' AND cCentroContable = '"+ $("#cCentroContable").val()+"'"
						+ "&fAuxIni=" + $("#fAuxIni").val() + ""
						+ "&fAuxFin=" + $("#fAuxFin").val() + "" ,
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
			}
	function GenerarRango(){
	var CuentaVal = $('#nCuenta').val();
		CuentaVal = CuentaVal.substring(1,0);
	if (CuentaVal == '') {
	    alert ("La Cuenta es un campo requerido");
		return ;
		}
	if (CuentaVal == '8') {
	    alert ("La Cuenta ingresada no es una cuenta valida");
		return ;
		}
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=Auxiliares.jasper"
						+ "&swhere=nCuenta BETWEEN '"+ $("#nCuenta").val()+"' AND '"+ $("#nCuenta2").val()+"' AND  fMovimiento BETWEEN '"+ $("#fAuxIni").val()+"' AND '"+ $("#fAuxFin").val()+"' AND mMovimiento != '0.00' AND cCentroContable = '"+ $("#cCentroContable").val()+"'"
						+ "&fAuxIni=" + $("#fAuxIni").val() + ""
						+ "&fAuxFin=" + $("#fAuxFin").val() + "" ,
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
			}

</script>

<script type="text/javascript" >
  $(document).ready(
   function() {
    $("input.AyudaSyC").subIniciaDlg();
    $("input.autoCompletaSyC").subIniciaAutoCompleta();
   }
  )

 </script>


  </head>

	<body id="dt_example">
<form name="FormAuxiliares" action="#">
<div  id="container" class="container SyCData">
	<h1>Auxiliares</h1>
</div>

<div  id="container" class="container SyCData">
	<div id="tabs">
		<ul class="tabs">
			<li><a href="#tabs-1">Filtros de B&uacute;squeda</a></li>
		</ul>

<div id="tabs-4">
<br/>
		<table  id="datosfec" class="display">
			<thead>
				<tr>
					<th>Fecha Inicio</th>
					<th>Fecha T&eacute;rmino</th>
				</tr>
			</thead>
			<tbody>
				<tr align="left">
					<td valign="top" align="center"><input type="text" readonly class="tcal" maxlength="10" size="10" id="fAuxIni" name="fAuxIni" style="text-align:center;"></td>
					<td valign="top" align="center"><input type="text" readonly class="tcal" maxlength="10" size="10" id="fAuxFin" name="fAuxFin" style="text-align:center;"></td>
				</tr>
			</tbody>
		</table>

	<div id="container" class="container SyCData">

        <h1>Rango de Cuentas Contables</h1>

        <table>
			<tr>
				<td>Desde:</td><td style="height: 10px; text-align: left; width: 737px;"
							align="left"><input id="hbuscaCuentaC"
							name="hbuscaCuentaC" value="" size="50" maxlength="50"
							class="AyudaSyC autoCompletaSyC" type="text">
							<input
							id="buscaCuentaC" name="buscaCuentaC" value="" size="5"
							maxlength="5" class="" type="hidden"><input
							id="TipoSubCuentaC" name="TipoSubCuentaC" value="" size="5"
							maxlength="5" class="" type="hidden">
							Rango:<input type="checkbox" name="Rango" id="Rango" onClick="mostrarOcultarSBC(this)">
							Ejercicio:<select name="cEjercicioA" id="cEjercicioA"><option value="0">2012</option></select></td>
			</tr>
		<tr>
			<td style="visibility:hidden;" id="LabelnCuenta2">Hasta:</td><td style="height: 10px; text-align: left; width: 737px;"
							align="left"><input id="hbuscaCuentaC"
							name="hbuscaCuentaC" value="" size="50" maxlength="50"
							class="AyudaSyC autoCompletaSyC" type="text"><input
							id="buscaCuentaC" name="buscaCuentaC" value="" size="5"
							maxlength="5" class="" type="hidden"><input
							id="TipoSubCuentaC" name="TipoSubCuentaC" value="" size="5"
							maxlength="5" class="" type="hidden"></td>

		</tr>
		</table>
		<h1>&nbsp;</h1>
		<table>
			<tr>
				<td style="visibility:hidden;" id="LabelSubCuenta">Auxiliar:</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
				<td><input name="cSubcuenta" id="cSubcuenta" value="" size="25" style="text-align:right;" style="visibility:hidden;"></td>
				<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
				<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
				<td valign="top">Auxiliar:<input type="checkbox" name="Auxiliar" id="Auxiliar" onClick="mostrarOcultar(this)"></td>
			</tr>
		</table>
		<table>
			<tr>
			<td>Descripci&oacute;n:</td>

			<td><input name="Descripcion" id="Descripcion" value="" size="40"style="background-color:#CCCCCC" style="text-align:left;"></td>
			</tr>
			<tr>
			<td>Centro Contable:</td>
			<td style="height: 10px; text-align: left; width: 737px;" align="left"><input
						id="hcCentroContable" name="hcCentroContable" value="" size="50"
						maxlength="50" class="AyudaSyC autoCompletaSyC" type="text"><input
						id="cCentroContable" name="cCentroContable" value="" size="5"
						maxlength="5" class="" type="hidden">
						<input id="cUnidadResponsable" name="cUnidadResponsable" value="" size="5" maxlength="5" class="" type="hidden">
						<input id="cUnidadResponsable" name="cUnidadResponsable" value="" size="5" maxlength="5" class="" type="hidden">
						</td>
			<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
<!--			<td><input name="cCentroContable" id="cCentroContable" value="" size="5" style="text-align:left;"></td>-->
			</tr>
		</table>
	</div>
<!--  			<div align="right">-->
<!--			<input type="button" name="GenerarAux" value="Generar" >-->
<!--			</div>-->
<!--			<table id="dt_ReportePre" class="display" >-->
<!--			-->
<!--			<thead>-->
<!--				<tr align="center">-->
<!--					<th>TIPO</th>-->
<!--					<th>N&Uacute;M</th>-->
<!--					<th>A.P.</th>-->
<!--					<th>FECHA</th>-->
<!--					<th>CONCEPTO</th>-->
<!--					<th>CARGOS</th>-->
<!--					<th>ABONOS</th>-->
<!--				</tr>-->
<!--			</thead>-->
<!--			-->
<!--				<tbody>-->
<!--								-->
<!--				</tbody>-->
<!--			</table>-->
	   </div>
    </div>
</div>

</form>
	</body>
</html>
