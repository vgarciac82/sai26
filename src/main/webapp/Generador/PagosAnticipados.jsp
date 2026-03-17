<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>


<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">
	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$('#dt_catalogo').dataTable({
			"bScrollCollapse" : true,
			"bPaginate" : true,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : true,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sScrollY" : "400",
			"sScrollX" : "1020"
		});
         //Inicio de Carga de Catalogos
         
         	querySelectPost("Catalogo_PluriAnuales_tipoGasto", "subfuncion");
         
         
          
		//Genera Tabs (Pestañas)
		//$(".tabs").tabs();
		 $( "#tabs" ).tabs();
		
		
        $("#IdFechaInicio").datepicker
		({
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
		
		$("#IdFechaFin").datepicker
		({
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
		
	});
	
	
	function onPostDisplay(idOper){
		alert("onPostDisplay");
		parent.document.getElementById("pb_send").disabled=false;
		return true;
		
	}	
	function onPostSubmit(idOper){
		alert("onPostSubmit");
		return true;
	}	
	function onLoadPlantilla(idOper){
		alert("onLoadPlantilla");
	}
	function onSubmit(idOper){
		alert("onSubmit");
		//Realizar validaciones. Si regresa TRUE continua el proceso en caso contrario no guarda.
		var p = window.parent;
		p.gestion.setFolio("CPLU-A04-1");
		p.gestion.setOperador("Usuario Prueba");
		p.gestion.setFechaDocumento("10/10/2013"); 
		p.gestion.setEjercicioFiscal("2013");
		p.gestion.setMoneda("MXP");
		return true;
	}
	
	function ResponsableSiguiente(idOper){
		alert("ResponsableSiguiente");
		// Deben estar definidos en CG_GRUPO
		if(idOper==1 ){
			return 'REVISOR_CNT_PLURIANUAL';
		}			
		if(idOper==2 ){
			return 'AUTORIZADOR_CNT_PLURIANUAL';
		}
		if(idOper==4){
			return 'CONSULTA_CNT_PLURIANUAL'; 
		}
		
		
	}	
	
	function OperacionSiguiente(idOper){
		// Debe estar definido en cg_operacion
		alert("OperacionSiguiente");
		if(idOper==1 ){
			return 'REVISA_CNT_PA';
		}			
		if(idOper==2 ){
			return 'AUTORIZA_CNT_PA';
		}
		if(idOper==4){
			return 'CONSULTA_CNT_PA'; 
		}
	}
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<input type="hidden" name="FOLIO">
		<input type="hidden" name="OPERADOR">
		<input type="hidden" name="FECHA_DOCUMENTO">
		<input type="hidden" name="EJERCICIO_FISCAL">
		<input type="hidden" name="MONEDA">
		
		<div id="container" class="container">
			<h1>Contratos Plurianuales y Especiales</h1>
			<fieldset  >
				<legend>Captura</legend>
				<table width="100%" height="190" border="0">
				           <tr>
				           <td></td>
				           <td></td>
				           <td align="right"></td>
				           <td align="right">Año:<input type="text" maxlength="6" size="4"></td>
				          
				           </tr>
				          <tr>
				          <td  align="right">Contrato : </td>
				          </td>
				          <td  bgcolor="#CCCCCC" >
								 <input type="radio" name="rd_si" value="1">Abierto&nbsp;&nbsp;&nbsp;&nbsp;
			                      <input type="radio" name="td_no" value="2">Cerrado
								
						    </td>
						    <td align="right">Tipo de Gasto:</td>
						    
								  
						     
						   
						          <td    height="10px">
						          
						          <select id= "TipoGasto" name="TipoGasto"   >
								</select>
                                     </td>
						                
				          
				          
				          </tr>
						  <tr>
						      <td  align="right"> Solicitud:   </td>
						   
						     <td><select id= "opcionMoneda" name="opcionMoneda"   >
													<option value="1" selected="selected">PluriaAnual</option>
													<option value="2">Especial</option>
													
											</select></td>
						   
						      <td  align="right">Tipo de Contrato: </td>
				               <td align="left"><select id= "opcionTipo" name="opcionTipo"   >
													<option value="1" selected="selected">Obra</option>
													<option value="2">Adquisiciones</option>
													<option value="3">Arrendamiento</option>
													<option value="4">Servicio</option>
											</select></td>
				            
						   
						  </tr>
						 
						  <tr>
						    <td  align="right">* Fecha Inicio:</td>
						    <td><input align="right" type="text" id="IdFechaInicio" name="IdFechaInicio"  readonly="readonly" value="" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="17" /></td>
						    <td  align="right">* Fecha Fin:</td>
						    <td><input align="right" type="text" id="IdFechaFin" name="IdFechaFin"  readonly="readonly" value="" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="17" /></td>
						    
						  </tr>
						  <tr>
						    <td align="right">Tipo de Moneda:</td>
						    <td><select id= "opcionMoneda" name="opcionMoneda"   >
													<option value="1" selected="selected">Nacional</option>
													<option value="2">Extranjera</option>
													
											</select></td>
						    <td align="right">* Tipo de Cambio:</td>
						    <td><input type="text"></td>
						    
						  </tr>
						  <tr><td  align="right">Monto en Moneda:</td>
						  		<td colspan="5">
																  		<table width="369" border="1">
									  <tr>
									    <th bgcolor="#336666" width="59" scope="col">&nbsp;</th>
									    <th bgcolor="#336666"  width="69" scope="col"><span class="style4 style5">2013</span></th>
									    <th bgcolor="#336666"  width="69" scope="col"><span class="style4 style5">2014</span></th>
									    <th bgcolor="#336666"  width="72" scope="col"><span class="style5">2015</span></th>
									    <th  bgcolor="#336666" width="66" scope="col"><div align="center" class="style5"><span class="style4">..</span></div></th>
									  </tr>
									  <tr>
									    <th bgcolor="#336666" scope="row"><div align="right" class="style5">Nacional</div></th>
									    <td><div align="right">$3,900</div></td>
									    <td><div align="right">$5,200</div></td>
									    <td><div align="right">0</div></td>
									    <td><div align="center">..</div></td>
									  </tr>
									 
								</table>

								</td>
								
								
					       </tr>
						
			</table>
			</fieldset>
			<fieldset>
				<legend> Claves Presupuestarias</legend>
				<table align="center">
					<tr>
						<td align="right"></td>
						<td>
						<textarea rows="5" cols="80">
								2013.16.RHQ.1.3.04.00.001.O001.11301.1.1.09.00000000000.B13.A04
								2013.16.RHQ.1.3.04.00.001.O001.11301.1.1.09.00000000000.B20.A04
								2013.16.RHQ.1.3.04.00.001.O001.11301.1.1.14.00000000000.B26.A04
						</textarea>
						
						</td>
						<td><input type="submit" value="...." name="Aceptar" id="Aceptar"/></td>
					</tr>
				</table>
			</fieldset>
  <div id="dv">
				<table id="dt_catalogo" class="display" cellspacing="0"
					cellpadding="2" align="center">
					<thead>
						<tr>
							<th>Estrucutura  Programática</th>
							<th>Enero</th>
							<th>Febrero</th>
							<th>Marzo</th>
							<th>Abril</th>
							<th>Mayo</th>
							<th>Junio</th>
							<th>Julio</th>
							<th>Agosto</th>
							<th>Septiembre</th>
							<th>Octubre</th>
							<th>Nobiembre</th>
							<th>Diciembre</th>
						</tr>
					</thead>
					<tbody>
					<tr>
					<td>2013</td>
					<td>3000</td>
					<td>8000</td>
					<td>00</td>
					<td>44</td>
					<td>4343</td>
					<td>1</td>
					<td>2</td>
					<td>3</td>
					<td>4</td>
					<td>5</td>
					<td>6</td>
					<td>7</td>
					</tr>
					<tr>
					<td>2014</td>
					<td>2231</td>
					<td>00</td>
					<td>000</td>
					<td>44</td>
					<td>4343</td>
					<td>1</td>
					<td>2</td>
					<td>3</td>
					<td>4</td>
					<td>5</td>
					<td>6</td>
					<td>7</td>
					</tr>
					</tbody>
				</table>
			</div>
			
	<div id="dv">
   <div id="tabs">

  <ul>

    <li><a href="#tabs-1">Descripción del Contrato</a></li>

    <li><a href="#tabs-2">Justificación del compromiso</a></li>

    <li><a href="#tabs-3">Justificación del plazo</a></li>
    
    <li><a href="#tabs-4">Documentos Anexos</a></li>
    

  </ul>

  <div id="tabs-1">

    <p>
<textarea rows="10" cols="118">Descripcion textual varchar 4000</textarea> </p>

  </div>

  <div id="tabs-2">

 

  </div>

  <div id="tabs-3">

   

  </div>

</div>
 <div id="Botones">
     <table>
     <tr><td><label>
                    <div align="center">
                      <input type="submit" value="Autorizar" name="Autorizar" id="Autorizar"/>
					  <input type="button" id="Cancelar" name ="Cancelar" value="Cancelar" onClick="limpiarSesion();">
                    </div>
    </label></td>
    </tr>
     </table>
  </div>>

		</div>
	 </div>
	</form>
</body>
</html>