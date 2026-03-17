<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	
	String cidcontrato=request.getParameter("cIdContrato");
		
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Ventanilla PreCompromiso</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" charset="utf-8">
	var vcontrato, vfolio,EP, vAvanzo;
	var oTableCalendario,oTableEP,oTableRetencion;
	var cambio=true;
	var calendarioConsultado=false;
	$(document).ready(function() {
		//Todos los tabs están en un mismo JSP
		var $tabs=$(".tabs").tabs();
		$tabs.tabs('select', 0);
		//alert("<%=cidcontrato%>");
		var contrato=decodeURIComponent('<%=cidcontrato%>');
		$("#cIdContrato").val(contrato);
		queryFormPost("preCompromisoCaratulaVentanillaReadMateriales", {async: false});
		$("#rfcProv").val($("#dRFC").val());
		$("#razonSocial").val($("#cRazonSocial").val());
			
		$(".montos").formatCurrency();
		
	});
	
	
	function checkShortcut()
	{				
		//Deshabilita el ESC y BACKSPACE
		if(event.keyCode==27){
			return false;
		}
		if((event.srcElement.tagName.toUpperCase() != 'INPUT' &&
				event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
			&& (event.keyCode==8 || event.keyCode==13)){					
			return false;
		}
	}
	
  	
</script>
</head>
<body  id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
<form>
	<div id="container" class="container">
			<h1>Ventanilla PreCompromiso</h1>
			<div class="tabs" id="tabs">
				<input type="text" style="width: 600px" id="cIdContratoH" name="cIdContratoH" readonly style="border-width:0; background-color:transparent"/>
				
				<ul>
					<li><a id="aTab0" href="#tabs-0" >Informaci&oacute;n General</a></li>
					
				</ul>
				<div id="tabs-0" align="center">
						<table  align="left" width="90%">
						       <tr>
						    	<td align="left" >Tipo Documento:<input type="text" style="width: 200px" name="origen" id="origen" readonly style=" background-color:transparent"/></td>
						    	</tr>
							<tr><td colspan="2"><table width="100%" height="78" style="width: 709px;" align="left">
								
								
								<tr>
						    		<td align="left" >RFC: <input type="text" style="width: 200px" name="rfcProv" id="rfcProv" readonly style=" background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" >Razón Social: <input type="text" style="width: 400px" name="razonSocial" id="razonSocial" readonly style=" background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="4">Plazo:
						    		<input type="text" style="width: 75px" name="Plazo" id="Plazo" readonly style="background-color:transparent"/>
						    		Unidad Admin: 
						    		<input type="text" style="width: 450px" name="UnidadEjecutora" id="UnidadEjecutora" readonly style="background-color:transparent"/>
						    		 </td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="2">Tipo de Fondo: <input type="text" style="width: 200px" name="cTipoFondo" id="cTipoFondo" readonly style="background-color:transparent"/></td>
						    	</tr>
							</table ></td></tr>
							<tr>
								<td align="left">Concepto: <textarea id="cConceptoContrato"  name="cConceptoContrato" rows="5" readonly="readonly" cols="50"></textarea></td>
								<td align="center"><table >
									<tr id="trfsp" >
										<td align="left">Fecha Solicitud:
						    				<input type="text" name="fContratoSol" id="fContratoSol" readonly style="background-color:transparent"/>
						    			</td>
						    			<td align="left">Fecha Propuestas:
						    				<input type="text" name="fContratoPro" id="fContratoPro" readonly style=" background-color:transparent"/>
					    				</td>
				    				</tr>
									<tr>
										<td id="tdfd" align="left">Fecha documento:
						    				<input type="text" name="fAdjudicacion" id="fAdjudicacion" readonly style="background-color:transparent"/>
						    			</td>
						    			<td align="left">Fecha Formalización:
						    				<input type="text"  name="fFirmaContrato" id="fFirmaContrato" readonly style=" background-color:transparent"/>
					    				</td>
				    				</tr>
				    				<tr id="trfif" >
				    					<td align="left">Fecha Inicio:
						    				<input type="text" name="fContratoIni" id="fContratoIni" readonly style="background-color:transparent"/>
						    			</td>
						    			<td align="left">Fecha Fin:
						    				<input type="text"  name="fContratoFin" id="fContratoFin" readonly style=" background-color:transparent"/>
					    				</td>
				    				</tr>
									<tr>
										<td align="left" colspan="2">Tipo Adjudicación:
						    				<input type="text"  style="width: 200px"  name="cTipoAdjudicacion" id="cTipoAdjudicacion" readonly style=" background-color:transparent"/>
						    			</td>
						    		</tr>
									<tr>
										<td align="left">Tipo Moneda:
						    				<input type="text"   style="width: 180px" name="cTipoMoneda" id="cTipoMoneda" readonly style=" background-color:transparent" value="01 PESO MEXICANO"/>
					    				</td>
					    				<td align="left">%IVA:
						    				<input type="text" style="width: 150px"  name="IVA" id="IVA" readonly style="background-color:transparent"/>
					    				</td>
				    				</tr>
									</table></td>
							</tr>
							<tr><td colspan="2"><table>
									<tr>
										<td>Monto Pedido
											<input type="text"   style="width: 180px" class="montos" name="mImporteBruto" id="mImporteBruto" readonly style=" background-color:transparent" />
										</td>
										<td>Importe Bruto
											<input type="text"   style="width: 180px" class="montos" name="mImporteContrato" id="mImporteContrato" readonly style=" background-color:transparent" />
										</td>
										<td>Monto Contrato(Pesos)
											<input type="text"   style="width: 180px"  class="montos" name="mImporteTotal" id="mImporteTotal" readonly style=" background-color:transparent" />
										</td>
									</tr>
									<tr>
										<td></td>
										<td>Monto IVA
											<input type="text"   style="width: 180px"  class="montos"name="mImporteIVA" id="mImporteIVA" readonly style=" background-color:transparent" />
										</td>
										<td>Monto Contrato(ME)
											<input type="text"   style="width: 180px" class="montos" name="mContratoME" id="mContratoME" readonly style=" background-color:transparent" />
										</td>
									</tr>
									<tr>
										<td> Monto Total Original
										
										<input type="text"   style="width: 180px" class="montos" name="mContratoMNOriginal" id="mContratoMNOriginal" readonly style=" background-color:transparent" />
										
										
										</td>
										<td>Monto Total a Pagar
											<input type="text"   style="width: 180px" class="montos" name="mContratoMN" id="mContratoMN" readonly style=" background-color:yellow " />
										</td>
										<td>Responsable RM
											<input type="text"   style="width: 180px" name="U_NOMBRE" id="U_NOMBRE" readonly style=" background-color:transparent" />
										</td>
									</tr>
							</table></td></tr>
						</table>
					</div>
			  </div>
			
			
				<!-- Hidden Section -->
			   
				<input name="cIdContrato" type="hidden" id="cIdContrato" value="<%=cidcontrato%>"/>
				<input type="hidden" name="dRFC" id="dRFC" />
				<input type="hidden" name="cRazonSocial" id="cRazonSocial" />
				
		</div>
		
		
										
		
		
</form>

</body>
</html>
