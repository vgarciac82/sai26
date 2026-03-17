
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
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String usu = usuario.getLogin();
	System.out.println("usuario_" + usuario + "/" + usu);

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<head>
		<title>Acuerdos de Ministracion de Fondos</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
@import "themes/smoothness/jquery-ui-1.8.4.custom.css";

@import "css/demo_table_jui.css";

@import "css/demo_page.css";
</style>
		<style media="all" type="text/css">
.alignRight {
	text-align: right;
}

.alignCenter {
	text-align: center;
}
</style>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript"		src="js/jquery.dataTables.editable-1.3.js"></script>	
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
		
$(document).ready(function() {
	
	$("input.AyudaSyC").subIniciaDlg();
	$("#vMonto").click(function() {
		Validar();
	});
	$("#save").hide();
	$("#status").val("nuevo")
	$("#upd").hide();
	$("#vMonto").hide();
	$("#RST").show();

	$(function() {

		$("#fVigencia").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
		});
	});

	$('#tAcuerdosDetalle').dataTable( {

		ScrollY : "500px",
		sScrollX : "1400px",
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

	$("#ClaveAMF").change(function() {

		if ($("#status").val() == "nuevo") {
			mostrarDetalle()
			$("#save").hide();
			$("#vMonto").show();
			$("#update").val("up")
			$("#mMonto").val(Number($("#mMontoAMF").val()));
			$("#mMontoAMF").formatCurrency();

		}
	});

	$("#mMontoAMF").change(function() {

		$("#mMontoAMF").formatCurrency();

	});
	
		 

});

function mostrarDetalle() {

	$("#mMonto").val("");
	var nRows = $("#tAcuerdosDetalle tr").length - 1;

	if (nRows > 0) {
		var DtAcuerdosMF = $("#tAcuerdosDetalle").dataTable();
		DtAcuerdosMF.fnClearTable();
	}

	var campos = " '" + $("#cFolio").val() + "' , '" + $("#ClaveAMF").val()
			+ "'"
	var elParametro2 = '';
	var szTabla = "CATAMF";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla : szTabla,Param : elParametro2,Campos : campos,MaxReg : "",ajax : 'false'},function(j) {

						var Renglon2 = 0;
						var Saldo1 = 0;
						var Renglon3 = 0;
						var SUMATOTALS = 0;
						var SUMATOTALMonto = 0;
						var Renglon4 = 0;

						for ( var i = 0; i < j.length; i++) {

							RESTASM = Number(j[i].Col2) - Number(j[i].Col3);

							var campoM = "<input type = 'text' style='text-align: right' size = '30' id = 'monto_"
									+ Renglon2
									+ "' name = 'monto_"
									+ Renglon2
									+ "'  onfocus='Sinfrmt(this)' onchange = 'CambioUpdate("
									+ Renglon2
									+ ")' value = '"
									+ j[i].Col2
									+ "' onblur = 'cambiafrmt(this)' class = 'Deshabilitar' onKeyPress='return(onlyNumbers(event))'/>";
							var campoS = "<input type = 'text' style='text-align: right' size = '30' id = 'saldo_"
									+ Saldo1
									+ "' name = 'saldo_"
									+ Saldo1
									+ "'  value = '"
									+ j[i].Col3
									+ "'  onfocus='Sinfrmt(this)' onblur = 'cambiafrmt(this)' class = 'Deshabilitar2' onKeyPress='return(onlyNumbers(event))'/>";
							var campoE = "<input type = 'text' style='text-align: right' size = '30' id = 'Emonto_"
									+ Renglon4
									+ "' name = 'Emonto_"
									+ Renglon4
									+ "'  value = '"
									+ RESTASM
									+ "' onblur = 'cambiafrmt(this)'  class = 'Deshabilitar' />";
							var campoMS = "<input type = 'hidden' style='text-align: right' size = '15' id = 'Tmonto_"
									+ Renglon3
									+ "' name = 'Tmonto_"
									+ Renglon3
									+ "'  onchange = 'CambioUpdate("
									+ Renglon3
									+ ")' value = '"
									+ j[i].Col2
									+ "' onblur = 'cambiafrmt(this)'  class = 'Deshabilitar' />";

							$('#tAcuerdosDetalle').dataTable().fnAddData(
									[ j[i].Col0, j[i].Col1, campoM, campoS,
											campoE, campoMS ]);
							Renglon2++;
							Saldo1++;
							Renglon4++;
							Renglon3++;

							$('.Deshabilitar2').attr('disabled', true);

							SUMATOTALS = SUMATOTALS + Number(j[i].Col3);
							$("#mSaldo").val(SUMATOTALS);
							$("#mSaldo").formatCurrency();

							SUMATOTALMonto = SUMATOTALMonto + Number(j[i].Col2);
							$("#mMonto").val(SUMATOTALMonto);
							$("#mMonto").formatCurrency();

						}
					});
}

function NuevoAcuerdo() {
	
	Mtotal =0;
	$("#mMonto").val(0);
	$("#mSaldo").val(0);
	$("#update").val("no");
	$("#vMonto").show();
	$("#upd ").hide();
	$("#status").val("viejo");
	$("#ClaveAMF").val("");
	$("#cFolio").val("");
	$("#mMontoAMF").val("");
	$("#fVigencia").val("");
	$("#save").hide();
	$("#RST").show();

	var nRows = $("#tAcuerdosDetalle tr").length - 1;

	if (nRows > 0) {
		var DtAcuerdosMF = $("#tAcuerdosDetalle").dataTable();
		DtAcuerdosMF.fnClearTable();
	}

	var campos = "";
	var elParametro2 = '';
	var szTabla = "NUEVOCATAMF";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla : szTabla,Param : elParametro2,Campos : campos,MaxReg : "",ajax : 'false'},function(j) {

						var Renglon = 0;
						var Saldo1 = 0;
						var Renglon3 = 0;
						var Renglon4 = 0;
						var SUMATOTALS = 0;
						var SUMATOTALMONTO = 0;

						for ( var i = 0; i < j.length; i++) {

							RESTASM = 0;

							var campoM = "<input type = 'text' style='text-align: right' size = '30' id = 'monto_"
									+ Renglon
									+ "' name = 'monto_"
									+ Renglon
									+ "'  onchange = 'Cambio("
									+ Renglon
									+ ")' value = '"
									+ j[i].Col2
									+ "' onfocus='Sinfrmt(this)' onblur='cambiafrmt(this)'  class = 'Deshabilitar' onKeyPress='return(onlyNumbers(event))'/>";
							var campoS = "<input type = 'text' style='text-align: right' size = '30' id = 'saldo_"
									+ Saldo1
									+ "' name = 'saldo_"
									+ Saldo1
									+ "'  value = '"
									+ j[i].Col3
									+ "' onfocus='Sinfrmt(this)' onblur='cambiafrmt(this)'  class = 'Deshabilitar2' onKeyPress='return(onlyNumbers(event))' />";
							var campoE = "<input type = 'text' style='text-align: right' size = '30' id = 'Emonto_"
									+ Renglon4
									+ "' name = 'Emonto_"
									+ Renglon4
									+ "'  value = '"
									+ RESTASM
									+ "' onblur = 'cambiafrmt(this)'  class = 'Deshabilitar2' />";
							var campoMS = "<input type = 'hidden'  size = '15' id = 'Tmonto_"
									+ Renglon3
									+ "' name = 'Tmonto_"
									+ Renglon3
									+ "'  onchange = 'Cambio("
									+ Renglon3
									+ ")' value = '"
									+ j[i].Col2
									+ "' class = 'Deshabilitar' />";
							$('#tAcuerdosDetalle').dataTable().fnAddData(
									[ j[i].Col0, j[i].Col1, campoM, campoS,
											campoE, campoMS ]);
							Renglon++;
							Saldo1++;
							Renglon3++;
							Renglon4++;

							$("#saldo_" + Renglon).formatCurrency();

							SUMATOTALMONTO = SUMATOTALMONTO + Number(j[i].Col2);
							$("#mMonto").val(SUMATOTALMONTO);

							SUMATOTALS = SUMATOTALS + Number(j[i].Col3);
							$("#mSaldo").val(SUMATOTALS);

							$('.Deshabilitar2').attr('disabled', true);

						}

						//$( "#1" ).focus( function () {
						//	$( "#1" ).val($( "#1" ).val());

						//});	

					});

}

var Mtotal = 0;
var total = 0;
var resMM = 0;
function Cambio(Renglon) {

	if ($("#monto_" + Renglon).val() < $("#saldo_" + Renglon).val()) { //SI EL VALOR DE MONTO RENGLON ES MENOR AL SALDO RENGLON
		Mtotal = Mtotal - Number($("#saldo_" + Renglon).val()); // A LA VARIABLE ACUMULADA RESTALE EL VALOR DEL MONTO RENGLON
		$("#mMonto").val(Mtotal); //ASIGNA EL VALOR RESTADO EN EL CAMPO MONTO
	}

	$("#saldo_" + Renglon).val($("#monto_" + Renglon).val()); //ASIGNA EL VALOR DEL MONTO RENGLON TOTAL AL SALDO RENGLON

	Mtotal = Mtotal + Number($("#monto_" + Renglon).val()); //ACUMULA EL VALOR DE LOS INPUTS MONTO DE LA DATATABLE EN UNA VARIABLE
	$("#mMonto").val(Mtotal); //ASIGA EL VALOR ACUMULADO EN EL CAMPO MONTO UR'S
	$("#mSaldo").val($("#mMonto").val()); //ASIGNA EL VALOR DEL CAMPO MONTO AL CAMPO SALDO
	$("#mMonto").formatCurrency(); //AL CAMPO MONTO C LE DA FORMATO DE PESOS	
	$("#mSaldo").formatCurrency(); //AL CAMPO SALDO C LE DA FORMATO DE PESOS	

	var SinMonto = quitaFmt($("#mMonto").val()); //AL VALOR QUE TIENE EL CAMPO MONTO UR'S LE QUITA EL FORMATO DE PESOS Y C ASIGNA A UNA VARIABLE
	var sinMontoAMF = quitaFmt($("#mMontoAMF").val()); //AL VALOR QUE TIENE EL CAMPO MONTO AMF LE QUITA EL FORMATO DE PESOS Y C ASIGNA A UNA VARIABLE

	if (Number(SinMonto) > Number(sinMontoAMF)) { //SI LA VARIABLE SIN FORMATO DE PESOS MONTO UR'S ES MAYOR QUE EL CAMPO SIN FORMATO DE PESOS MONTO AMF
		var saldo = $("#saldo_" + Renglon).val();
		var Resta = SinMonto - saldo;
		Mtotal = Mtotal - saldo;
		$("#mMonto").val(Resta);
		$("#mMonto").formatCurrency();
		$("#monto_" + Renglon).val(0);
		$("#mSaldo").val($("#mMonto").val());
		$("#saldo_" + Renglon).val(0);
		alert("El Monto UR´s excede el Monto AMF ");
		return;
	}
}

function CambioUpdate(Renglon2) {

	//$( "#mSaldo" ).val( );

	var total = Number($("#monto_" + Renglon2).val())
			- Number($("#Tmonto_" + Renglon2).val());
	var CampoMonto = Number(quitaFmt($("#mMonto").val())) + total;
	$("#mMonto").val(CampoMonto);

	if (CampoMonto > Number(quitaFmt($("#mMontoAMF").val()))) {
		alert("El Monto UR´s excede el Monto AMF ");
		//$("#monto_"+Renglon2).val() + $("#Tmonto_"+Renglon2).val();
		$("#monto_" + Renglon2).val($("#Tmonto_" + Renglon2).val());
		$("#monto_" + Renglon2).val() - $("#Tmonto_" + Renglon2).val();
		var VmontoAMF = Number(quitaFmt($("#mMontoAMF").val()));
		$("#mMonto").val(VmontoAMF);
		$("#mMonto").formatCurrency();
		return;
	}

	$("#mMonto").val(CampoMonto)
	$("#mMonto").formatCurrency();
	var total2 = Number($("#saldo_" + Renglon2).val()) + total;

	if (total2 < 0) {
		alert(" La Reduccion del monto excede el saldo de la UR ");
		$("#monto_" + Renglon2).val($("#Tmonto_" + Renglon2).val());
		$("#mMonto").val(quitaFmt($("#mMontoAMF").val()));
		$("#mMonto").formatCurrency();
		return;
	}

	$("#saldo_" + Renglon2).val(total2);
	$("#Tmonto_" + Renglon2).val($("#monto_" + Renglon2).val())

}

function cmdGuardar() {

	var hayError = '';

	if ($("#ClaveAMF").val() == '') {
		alert("Debe Ingresar una Clave AMF");
		return;
	}

	$('.Validacion').each(function() {
		if ($(this).val() == '') {
			hayError = hayError + this.name + ',  ';
		}

	});

	if (hayError == '') {
		//getNextSequenceVal({seqName: "AMF-", async: false, callback: setSequenceVal});
		////getNextSequenceVal({seqName: "F", async: false, callback: setSequenceVal2});
		queryFormPost("tAcuerdosMFEncabezadoCreate", {
			async : false
		});
		guardatabladetalle();
		alert("Guardado Correctamente");
		$("#save").hide();
		window.location.reload();
	} else {
		alert('Debe ingresar los siguientes datos: ' + hayError);
		return;
	}
}

function Validar() {

	//$("#new").hide();
	//$("#status").val("nuevo");
	$("#RST").show();
	var SumaMonto = 0;
	var SumaSaldo = 0;
	var table = document.getElementById('tAcuerdosDetalle');
	var rowCount = table.rows.length;

	for ( var i = 1; i < rowCount; i++) {

		var row = table.rows[i];
		var ur = row.cells[0].childNodes[0];
		var nombre = row.cells[1].childNodes[0];
		var monto = row.cells[2].childNodes[0];
		var saldo = row.cells[3].childNodes[0];

		SumaMonto = SumaMonto + Number(quitaFmt(monto.value));
		SumaSaldo = SumaSaldo + Number(quitaFmt(saldo.value));
		monto = Number(quitaFmt(monto.value));
		saldo = Number(quitaFmt(saldo.value));
		//$( "#mMonto" ).val( SumaMonto );
		//$( "#mSaldo" ).val( SumaSaldo );

		if (saldo > monto) {
			alert("El Saldo no puede ser mayor al monto");
			return;
		}
	}

	if ($("#m1x1Saldo").val(saldo) < $("#m1x1Monto").val(SumaMonto)) {
		alert("El Saldo no puede ser mayor al monto");
		return;
	}

	if (quitaFmt($("#mMontoAMF").val()) < SumaMonto) {
		alert("has excedido el Monto Permitido");
		return;
	}

	if (quitaFmt($("#mMontoAMF").val()) > SumaMonto) {
		alert("El Monto Total de las UR es Menor al Monto AMF");
		return;
	}

	if (quitaFmt($("#mMontoAMF").val()) == 0 && SumaMonto == 0) {
		alert("El Monto no puede ser 0 ");
		return;
	}

	if ($("#update").val() == "up") {
		$("#upd").show();
		$("#save").hide();
		$(".Deshabilitar").attr('disabled', true);
		$(".Deshabilitar2").attr('disabled', true);
		alert("Validado Correctamente");
	}

	else {
		$("#status").val("viejo")
		alert("Validado Correctamente");
		$("#save").show();
		$(".Deshabilitar").attr('disabled', true);

	}
}

function guardatabladetalle() {

	$("#status").val("nuevo");
	var SumaMonto = 0;
	var SumaSaldo = 0;
	var table = document.getElementById('tAcuerdosDetalle');

	var rowCount = table.rows.length;
	for ( var i = 1; i < rowCount; i++) {
		var row = table.rows[i];

		try {

			var ur = row.cells[0].childNodes[0];
			var nombre = row.cells[1].childNodes[0];
			var monto = row.cells[2].childNodes[0];
			var saldo = row.cells[3].childNodes[0];
			monto = Number(quitaFmt(monto.value));
			saldo = Number(quitaFmt(saldo.value));
			$("#cUr").val(ur.toString());
			$("#cNombre").val(nombre.toString());
			$("#mMonto").val(monto);
			$("#mSaldo").val(saldo);

			queryFormPost("tAcuerdosMFDetalleCreate", {
				async : false
			});

		}

		catch (e) {
			null;
		}
	}

}

function ActualizarDetalle() {

	$("#new").show()
	$("#upd").hide();
	$("#status").val("nuevo");
	var SumaMonto = 0;
	var SumaSaldo = 0;
	var table = document.getElementById('tAcuerdosDetalle');
	var rowCount = table.rows.length;

	for ( var i = 1; i < rowCount; i++) {
		var row = table.rows[i];

		try {

			var ur = row.cells[0].childNodes[0];
			var nombre = row.cells[1].childNodes[0];
			var monto = row.cells[2].childNodes[0];
			var saldo = row.cells[3].childNodes[0];
			monto = Number(quitaFmt(monto.value));
			saldo = Number(quitaFmt(saldo.value));
			$("#cUr").val(ur.toString());
			$("#cNombre").val(nombre.toString());
			$("#mMonto").val(monto);
			$("#mSaldo").val(saldo);

			queryFormPost("AcuerdosMFUpdate", {
				async : false
			});
		}

		catch (e) {
			null;
		}
	}
	alert("Actualizado Correctamente");
	return;
}

function quitaFmt(val) {
	val = val.replace("$", "");
	val = val.replace(/,/g, "");

	if (val.indexOf("(") >= 0) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
	}
	return val
}

function cambiafrmt(fld) {
	$("#" + fld.id).formatCurrency();
}

function Sinfrmt(fld) {
	var valcol = fld.value;
	valcol = valcol.replace("$", "");
	valcol = valcol.replace(",", "");
	valcol = valcol.replace(/,/g, "");
	valcol = valcol.replace(".0000", "");
	valcol = valcol.replace(".00", "");
	$("#" + fld.id).val(valcol);
}

function LetrasNums(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241) {
		alert("Solo se permiten Letras y Numeros");
	}
	if (keyPressed == 61 || keyPressed == 63 || keyPressed == 62
			|| keyPressed == 59 || keyPressed == 58 || keyPressed == 60
			|| keyPressed == 91 || keyPressed == 92 || keyPressed == 93
			|| keyPressed == 94 || keyPressed == 95 || keyPressed == 96) {
		return false;
	}
	return !(keyPressed > 32 && (keyPressed < 48 || keyPressed > 122)
			&& keyPressed != 209 && keyPressed != 241);
}

function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 47) {
			return false;
		}
		return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
	}


//		function setSequenceVal(seqValue) 
//		{
//				seqValue = "000000" + seqValue;
//				seqValue = seqValue.substr(seqValue.length - 6);
//				seqValue = "AMF-"+ seqValue;
//				$("#ClaveAMF").val( seqValue );
//		}

//		function setSequenceVal2(seqValue) 
//		{
//			seqValue = "000000" + seqValue;
//			seqValue = seqValue.substr(seqValue.length - 6);
//			$("#cFolio").val( seqValue );
//		}


</script>

	</head>
	<body id="dt_example">
		<form>

			<input type="hidden" id="cUr" name="cUr" />
			<input type="hidden" id="cNombre" name="cNombre" />
			<%--<input type = "text" id = "mMonto"  name = "mMonto" />--%>
			<%--<input type = "text" id = "mSaldo"  name = "mSaldo" />--%>
			<input type="hidden" id="status" name="status" />
			<input type="hidden" id="update" name="update" />
			<input type="hidden" id="m1x1Monto" name="m1x1Monto" />
			<input type="hidden" id="m1x1Saldo" name="m1x1Saldo" />
			<input type="hidden" id="x" name="x" />


			<div id="container">
				<h1>
					Acuerdos de Ministracion de Fondos
					<label style="font-size: 16pt"></label>
				</h1>

				<table align="center" width="1000px">
					<tr>
						<td>
							<fieldset>
								<table align="center" width="1100px">
									<tr>
										<td style="text-align: right">
											Clave AMF:
										</td>
										<td>
											<input name="ClaveAMF" id="ClaveAMF" type="text"
												value="Introducir Clave" style="text-align: left"
												class="AyudaSyC">
										</td>
										<td style="text-align: right">
											Folio:
										</td>
										<td>
											<input name="cFolio" id="cFolio" type="text"
												style="text-align: left" class="Validacion">
										</td>
										<td>
											<input name="vMonto" type="button" id="vMonto"
												value="Validar" />
										</td>
										<td id="save">
											<input name="Guardar" id="Guardar" type="button"
												value="Guardar" onclick="cmdGuardar()" />
										</td>
										<td id="new">
											<input name="nAcuerdoMF" id="nAcuerdoMF" type="button"
												value="Nuevo Acuerdo" onclick="NuevoAcuerdo()" />
										</td>
										<td id="upd">
											<input name="Modificar" id="Modificar" type="button"
												value="Actualizar" onclick="ActualizarDetalle()" />
										</td>
									</tr>
									<tr>
										<td style="text-align: right">
											Monto AMF:
										</td>
										<td>
											<input name="mMontoAMF" id="mMontoAMF" type="text" size="30"
												class="Validacion" style="text-align: right;">
										</td>
										<td style="text-align: right">
											Vigencia:
										</td>
										<td>
											<input name="fVigencia" id="fVigencia" type="text" size="30"
												class="Validacion" style="text-align: left">
										</td>
									</tr>
									<tr id="RST">
										<td></td>
										<td></td>
										<td style="text-align: right">
											Monto UR's:
										</td>
										<td>
											<input name="mMonto" id="mMonto" type="text"
												readonly="readonly" size="30" style="text-align: right;">
										</td>
										<td style="text-align: right">
											Suma Total Saldo:
										</td>
										<td>
											<input name="mSaldo" id="mSaldo" type="text"
												readonly="readonly" size="30" style="text-align: right;">
										</td>
									</tr>
								</table>
							</fieldset>

							<table id="tAcuerdosDetalle">
								<thead>
									<tr>
										<th align="center">
											UR
										</th>
										<th align="center">
											NOMBRE UR
										</th>
										<th align="center">
											MONTO
										</th>
										<th align="center">
											SALDO
										</th>
										<th align="center">
											EJERCIDO
										</th>
										<th></th>
									</tr>
								</thead>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</form>
	</body>
</html>


