<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>   
    <title>Catálogo de Clientes</title>
    
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
	


<script type="text/javascript" charset="utf-8">



	$(document).ready(
		function() 
		{
			$('#example').dataTable(
				{         
					"sScrollY": 200,         
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"oLanguage": {"sSearch": "Buscar:"} ,
					"sPaginationType": "full_numbers"    
				} );
			$('#tblTipoRFC').dataTable(
				{   
					"bJQueryUI": true,					
					"bPaginate": false,         
					"bLengthChange": false,         
					"bFilter": false,         
					"bSort": false,         
					"bInfo": false,
					"bAutoWidth": false  
				} );
		});

	function Hello()
	{

				//$('#tblSyCData0').dataTable().fnDraw();
				alert("helloo");
			} 
			
		</script>
		
	<script>
		$(function() {
			$( "#datepicker" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
		$(function() {
			$( "#tabs" ).tabs();
		});
		
	</script>

  </head>
  
  <body id="dt_example">
    This is my JSP page. <br>
    <div id="sdCliente2" class="SyCData2" >
    </div>
    <input type="button" onclick="Hello()" value="esto es una prueba"/>
    <br/>
    <br/>   
    
    <div class="demo">

		<p>Date: <input type="text" id="datepicker"></p>

	</div>
	
	<div id="container" class="container SyCData">
		<h1>Beneficiario</h1>
		
		<table border = 0>
			<tr>
				<td align="right">Tipo de Persona:</td> 
				<td>
					<SELECT id="cbTipoPersona" name="cbTipoPersona" style="WIDTH: 173px;">
						<OPTION value="Z:">A</OPTION>
						<OPTION value="Y:">B</OPTION>
						<OPTION value="X:">C</OPTION>
						<OPTION value="xx" selected>--</OPTION>
					</SELECT>
				</td>
				<td align="right">&nbsp;</td>
				<td colspan="3" align="right"><input type="checkbox" name="option1" value="Milk">RFC Válido &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Código SICOP:<input id="txtContrato2" name="txtContrato2" type="text" size="5" maxlength="5"></td> 
			</tr>
			<tr>
				<td align="right">RFC:</td> 
				<td><input id="txtRFC1" name="txtRFC1" type="text" size="3" maxlength="4">-<input id="txtRFC2" name="txtRFC2" type="text" size="4" maxlength="6">-<input id="txtRFC3" name="txtRFC3" type="text" size="2" maxlength="3"></td>
				<td align="right">CURP:</td> 
				<td colspan="3"><input id="txtContrato2" name="txtContrato2" type="text" size="23" maxlength="4"></td>
			</tr>
			<tr>
				<td align="right">A.Paterno:</td> 
				<td><input id="txtContrato2" name="txtContrato2" type="text" size="23" maxlength="30"></td>
				<td align="right">A.Materno:</td> 
				<td><input id="txtContrato2" name="txtContrato2" type="text" size="23" maxlength="30"></td>
				<td align="right">Nombre:</td> 
				<td><input id="txtContrato2" name="txtContrato2" type="text" size="23" maxlength="30"></td>
			</tr>
			<tr>
				<td align="right">Persona Moral:</td> 
				<td colspan="5"><input id="txtContrato2" name="txtContrato2" type="text" size="106" maxlength="50"></td>
			</tr>
			<tr>
				<td colspan="6" align="right"><input type="button" onclick="Hello()" value="Cuentas Bancarias"/></td>
			</tr>
		</table>
		<center>
		<h1>Tipo de RFC</h1>
		</center>
		<table border = "0" style="WIDTH: 100%; HEIGHT: 1px;">
			<tr >
				
				<td style="HEIGHT: 1px;">Tipo de RFC:<br>
					<SELECT id="cbTipoPersona" name="cbTipoPersona" style="WIDTH: 173px;">
						<OPTION value="Z:">A</OPTION>
						<OPTION value="Y:">B</OPTION>
						<OPTION value="X:">C</OPTION>
						<OPTION value="xx" selected>--</OPTION>
					</SELECT>
					<input type="button" onclick="Hello()" value="A"/>
					<input type="button" onclick="Hello()" value="B"/>
					<input type="button" onclick="Hello()" value="C"/>
					<br><br>
					<table   class="display" id="tblTipoRFC" >
						<thead>
							<tr>
								<th>Tipo RFC</th>
								<th>Descripción</th>
							</tr>
						</thead>
						<tbody>
							<tr class="odd gradeA">
								<td>A</td>
								<td>AA</td>
							</tr>
							<tr class="even gradeA">
								<td>B</td>
								<td>BB</td>
							</tr>
							<tr class="odd gradeA">
								<td>C</td>
								<td>CC</td>
							</tr>
						</tbody>
					</table>
				</td>
				<td valign="top" style="WIDTH: 300px;">
					<br><br><br>
					<FIELDSET>
						 <LEGEND>Características especiales</LEGEND>
						 <br>
						 <input type="checkbox" id="option1" value="Milk">Compensación de Adeudos(SICOM)<br>
						 <input type="checkbox" name="option1" value="Milk">Servicios Personales (Nómina)<br>
						 <input type="checkbox" name="option1" value="Milk">Servicios Personales (Terceros)<br>
						 <input type="checkbox" name="option1" value="Milk">Servicios Personales (ISSSTE)<br>
						 <input type="checkbox" name="option1" value="Milk">Operaciones Ajenas<br><br>
					</FIELDSET>
				</td>
				 
			</tr>
			
		</table>
		
		<center>
		<h1>Domicilio</h1>
		</center>
		
		<div id="tabs">
			<ul>
				<li><a href="#tabs-1">Dirección Fiscal</a></li>
				<li><a href="#tabs-2">Dirección Actual</a></li>
			</ul>
			<div id="tabs-1" >
				<table border = 0 style="WIDTH: 100%; HEIGHT: 1px;">
					<tr>
						<td align="right">Entidad Federativa:</td> 
						<td>
							<SELECT id="cbEntidadFederativaFiscal" name="cbEntidadFederativaFiscal" style="WIDTH: 173px;">
								<OPTION value="A:">A</OPTION>
								<OPTION value="B:">B</OPTION>
								<OPTION value="C:">C</OPTION>
								<OPTION value="xx" selected>--</OPTION>
							</SELECT>
						</td>
						<td align="right">&nbsp;</td>
						<td colspan="3" align="right"><input type="button" onclick="Hello()" value="?"/></td> 
					</tr>
					<tr>
						<td align="right">Calle:</td> 
						<td><input id="txtCalleFiscal" name="txtCalleFiscal" type="text" size="30" maxlength="4"></td>
						<td align="right">No.:</td> 
						<td><input id="txtCalleNoFiscal" name="txtCalleNoFiscal" type="text" size="6" maxlength="10"></td>
						<td align="right">No.Interior:</td> 
						<td><input id="txtCalleNoIntFiscal" name="txtCalleNoIntFiscal" type="text" size="6" maxlength="10"></td>
					</tr>
					<tr>
						<td align="right">Otras señas:</td> 
						<td colspan="5"><input id="txtOtrasSeñasFiscal" name="txtOtrasSeñasFiscal" type="text" size="80" maxlength="50"></td>
					</tr>
					<tr>
						<td align="right">Colonia o Manzana:</td> 
						<td colspan="5"><input id="txtColoniaFiscal" name="txtColoniaFiscal" type="text" size="80" maxlength="50"></td>
					</tr>
					<tr>
						<td align="right">Delegación:</td> 
						<td>
							<SELECT id="cbDelegacionFiscal" name="cbDelegacionFiscal" style="WIDTH: 173px;">
								<OPTION value="A:">A</OPTION>
								<OPTION value="B:">B</OPTION>
								<OPTION value="C:">C</OPTION>
								<OPTION value="xx" selected>--</OPTION>
							</SELECT>
						</td>
						<td colspan="2" align="right">&nbsp;</td>
						<td align="right">Código Postal:</td> 
						<td><input id="txtCPFiscal" name="txtCPFiscal" type="text" size="6" maxlength="5"></td> 
					</tr>
					<tr>
						<td align="right">Teléfono:</td> 
						<td><input id="txtTelFiscal" name="txtTelFiscal" type="text" size="15" maxlength="15"></td>
						<td align="right">Fax:</td> 
						<td colspan="3" ><input id="txtFaxFiscal" name="txtFaxFiscal" type="text" size="15" maxlength="15"></td>
					</tr>
					<tr>
						<td align="right">email:</td> 
						<td colspan="5"><input id="txteMailFiscal" name="txteMailFiscal" type="text" size="80" maxlength="50"></td>
					</tr>
				</table>
			</div>
			<div id="tabs-2">
				<table border = 0 style="WIDTH: 100%; HEIGHT: 1px;">
					<tr>
						<td align="right">Entidad Federativa:</td> 
						<td>
							<SELECT id="cbEntidadFederativaActual" name="cbEntidadFederativaActual" style="WIDTH: 173px;">
								<OPTION value="A:">A</OPTION>
								<OPTION value="B:">B</OPTION>
								<OPTION value="C:">C</OPTION>
								<OPTION value="xx" selected>--</OPTION>
							</SELECT>
						</td>
						<td align="right">&nbsp;</td>
						<td colspan="3" align="right"><input type="button" onclick="Hello()" value="?"/></td> 
					</tr>
					<tr>
						<td align="right">Calle:</td> 
						<td><input id="txtCalleActual" name="txtCalleActual" type="text" size="30" maxlength="4"></td>
						<td align="right">No.:</td> 
						<td><input id="txtCalleNoActual" name="txtCalleNoActual" type="text" size="6" maxlength="10"></td>
						<td align="right">No.Interior:</td> 
						<td><input id="txtCalleNoIntActual" name="txtCalleNoIntActual" type="text" size="6" maxlength="10"></td>
					</tr>
					<tr>
						<td align="right">Otras señas:</td> 
						<td colspan="5"><input id="txtOtrasSeñasActual" name="txtOtrasSeñasActual" type="text" size="80" maxlength="50"></td>
					</tr>
					<tr>
						<td align="right">Colonia o Manzana:</td> 
						<td colspan="5"><input id="txtColoniaActual" name="txtColoniaActual" type="text" size="80" maxlength="50"></td>
					</tr>
					<tr>
						<td align="right">Delegación:</td> 
						<td>
							<SELECT id="cbDelegacionActual" name="cbDelegacionActual" style="WIDTH: 173px;">
								<OPTION value="A:">A</OPTION>
								<OPTION value="B:">B</OPTION>
								<OPTION value="C:">C</OPTION>
								<OPTION value="xx" selected>--</OPTION>
							</SELECT>
						</td>
						<td colspan="2" align="right">&nbsp;</td>
						<td align="right">Código Postal:</td> 
						<td><input id="txtCPActual" name="txtCPActual" type="text" size="6" maxlength="5"></td> 
					</tr>
					<tr>
						<td align="right">Teléfono:</td> 
						<td><input id="txtTelActual" name="txtTelActual" type="text" size="15" maxlength="15"></td>
						<td align="right">Fax:</td> 
						<td colspan="3" ><input id="txtFaxActual" name="txtFaxActual" type="text" size="15" maxlength="15"></td>
					</tr>
					<tr>
						<td align="right">email:</td> 
						<td colspan="5"><input id="txteMailActual" name="txteMailActual" type="text" size="80" maxlength="50"></td>
					</tr>
				</table>
			</div>

		</div>
		
		<center>
		<h1>Apoderado</h1>
		</center>
		<table border = 1>
			<tr>
				<td align="right">A.Paterno:</td> 
				<td><input id="txtAPaternoApoderado" name="txtAPaternoApoderado" type="text" size="23" maxlength="30"></td>
				<td align="right">A.Materno:</td> 
				<td><input id="txtAMaternoApoderado" name="txtAMaternoApoderado" type="text" size="23" maxlength="30"></td>
				<td align="right">Nombre:</td> 
				<td><input id="txtNombreApoderado" name="txtNombreApoderado" type="text" size="23" maxlength="30"></td>
			</tr>
			<tr>
				<td align="right">Teléfono:</td> 
				<td><input id="txtTelActual" name="txtTelActual" type="text" size="15" maxlength="15"></td>
				<td align="right">Fax:</td> 
				<td colspan="3" ><input id="txtFaxActual" name="txtFaxActual" type="text" size="15" maxlength="15"></td>
			</tr>
			<tr>
				<td align="right">email:</td> 
				<td colspan="5"><input id="txteMailActual" name="txteMailActual" type="text" size="80" maxlength="50"></td>
			</tr>
			<tr>
				<td align="right">No. Oficio Poder Legal:</td> 
				<td colspan="5" ><input id="txtFaxActual" name="txtFaxActual" type="text" size="23" maxlength="15"></td>
			</tr>
		</table>
		
		<br/><br/>
		
		
		<div id="demo2">
			<table   class="display" id="example" >
			<thead>
				<tr>
					<th>Rendering engine</th>
					<th>Browser</th>
					<th>Platform(s)</th>
					<th>Engine version</th>
					<th>CSS grade</th>
				</tr>
			</thead>
	<tbody>
		<tr class="odd gradeX">
			<td>Trident</td>
			<td>Internet
				 Explorer 4.0</td>
			<td>Win 95+</td>
			<td class="center"> 4</td>
			<td class="center">X</td>
		</tr>
		<tr class="even gradeC">
			<td>Trident</td>
			<td>Internet
				 Explorer 5.0</td>
			<td>Win 95+</td>
			<td class="center">5</td>
			<td class="center">C</td>
		</tr>
		<tr class="odd gradeA">
			<td>Trident</td>
			<td>Internet
				 Explorer 5.5</td>
			<td>Win 95+</td>
			<td class="center">5.5</td>
			<td class="center">A</td>
		</tr>
		<tr class="even gradeA">
			<td>Trident</td>
			<td>Internet
				 Explorer 6</td>
			<td>Win 98+</td>
			<td class="center">6</td>
			<td class="center">A</td>
		</tr>
		<tr class="odd gradeA">
			<td>Trident</td>
			<td>Internet Explorer 7</td>
			<td>Win XP SP2+</td>
			<td class="center">7</td>
			<td class="center">A</td>
		</tr>
		<tr class="even gradeA">
			<td>Trident</td>
			<td>AOL browser (AOL desktop)</td>
			<td>Win XP</td>
			<td class="center">6</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Firefox 1.0</td>
			<td>Win 98+ / OSX.2+</td>
			<td class="center">1.7</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Firefox 1.5</td>
			<td>Win 98+ / OSX.2+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Firefox 2.0</td>
			<td>Win 98+ / OSX.2+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Firefox 3.0</td>
			<td>Win 2k+ / OSX.3+</td>
			<td class="center">1.9</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Camino 1.0</td>
			<td>OSX.2+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Camino 1.5</td>
			<td>OSX.3+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Netscape 7.2</td>
			<td>Win 95+ / Mac OS 8.6-9.2</td>
			<td class="center">1.7</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Netscape Browser 8</td>
			<td>Win 98SE+</td>
			<td class="center">1.7</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Netscape Navigator 9</td>
			<td>Win 98+ / OSX.2+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.0</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.1</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1.1</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.2</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1.2</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.3</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1.3</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.4</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1.4</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.5</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1.5</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.6</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">1.6</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.7</td>
			<td>Win 98+ / OSX.1+</td>
			<td class="center">1.7</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Mozilla 1.8</td>
			<td>Win 98+ / OSX.1+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Seamonkey 1.1</td>
			<td>Win 98+ / OSX.2+</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Gecko</td>
			<td>Epiphany 2.20</td>
			<td>Gnome</td>
			<td class="center">1.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>Safari 1.2</td>
			<td>OSX.3</td>
			<td class="center">125.5</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>Safari 1.3</td>
			<td>OSX.3</td>
			<td class="center">312.8</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>Safari 2.0</td>
			<td>OSX.4+</td>
			<td class="center">419.3</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>Safari 3.0</td>
			<td>OSX.4+</td>
			<td class="center">522.1</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>OmniWeb 5.5</td>
			<td>OSX.4+</td>
			<td class="center">420</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>iPod Touch / iPhone</td>
			<td>iPod</td>
			<td class="center">420.1</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Webkit</td>
			<td>S60</td>
			<td>S60</td>
			<td class="center">413</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 7.0</td>
			<td>Win 95+ / OSX.1+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 7.5</td>
			<td>Win 95+ / OSX.2+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 8.0</td>
			<td>Win 95+ / OSX.2+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 8.5</td>
			<td>Win 95+ / OSX.2+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 9.0</td>
			<td>Win 95+ / OSX.3+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 9.2</td>
			<td>Win 88+ / OSX.3+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera 9.5</td>
			<td>Win 88+ / OSX.3+</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Opera for Wii</td>
			<td>Wii</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Nokia N800</td>
			<td>N800</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>Presto</td>
			<td>Nintendo DS browser</td>
			<td>Nintendo DS</td>
			<td class="center">8.5</td>
			<td class="center">C/A<sup>1</sup></td>
		</tr>
		<tr class="gradeC">
			<td>KHTML</td>
			<td>Konqureror 3.1</td>
			<td>KDE 3.1</td>
			<td class="center">3.1</td>
			<td class="center">C</td>
		</tr>
		<tr class="gradeA">
			<td>KHTML</td>
			<td>Konqureror 3.3</td>
			<td>KDE 3.3</td>
			<td class="center">3.3</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeA">
			<td>KHTML</td>
			<td>Konqureror 3.5</td>
			<td>KDE 3.5</td>
			<td class="center">3.5</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeX">
			<td>Tasman</td>
			<td>Internet Explorer 4.5</td>
			<td>Mac OS 8-9</td>
			<td class="center">-</td>
			<td class="center">X</td>
		</tr>
		<tr class="gradeC">
			<td>Tasman</td>
			<td>Internet Explorer 5.1</td>
			<td>Mac OS 7.6-9</td>
			<td class="center">1</td>
			<td class="center">C</td>
		</tr>
		<tr class="gradeC">
			<td>Tasman</td>
			<td>Internet Explorer 5.2</td>
			<td>Mac OS 8-X</td>
			<td class="center">1</td>
			<td class="center">C</td>
		</tr>
		<tr class="gradeA">
			<td>Misc</td>
			<td>NetFront 3.1</td>
			<td>Embedded devices</td>
			<td class="center">-</td>
			<td class="center">C</td>
		</tr>
		<tr class="gradeA">
			<td>Misc</td>
			<td>NetFront 3.4</td>
			<td>Embedded devices</td>
			<td class="center">-</td>
			<td class="center">A</td>
		</tr>
		<tr class="gradeX">
			<td>Misc</td>
			<td>Dillo 0.8</td>
			<td>Embedded devices</td>
			<td class="center">-</td>
			<td class="center">X</td>
		</tr>
		<tr class="gradeX">
			<td>Misc</td>
			<td>Links</td>
			<td>Text only</td>
			<td class="center">-</td>
			<td class="center">X</td>
		</tr>
		<tr class="gradeX">
			<td>Misc</td>
			<td>Lynx</td>
			<td>Text only</td>
			<td class="center">-</td>
			<td class="center">X</td>
		</tr>
		<tr class="gradeC">
			<td>Misc</td>
			<td>IE Mobile</td>
			<td>Windows Mobile 6</td>
			<td class="center">-</td>
			<td class="center">C</td>
		</tr>
		<tr class="gradeC">
			<td>Misc</td>
			<td>PSP browser</td>
			<td>PSP</td>
			<td class="center">-</td>
			<td class="center">C</td>
		</tr>
		<tr class="gradeU">
			<td>Other browsers</td>
			<td>All others</td>
			<td>-</td>
			<td class="center">-</td>
			<td class="center">U</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<th>Rendering engine</th>
			<th>Browser</th>
			<th>Platform(s)</th>
			<th>Engine version</th>
			<th>CSS grade</th>
		</tr>
	</tfoot>
</table>
			</div>

	</body>
</html>
