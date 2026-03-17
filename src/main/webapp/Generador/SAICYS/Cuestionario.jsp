<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!Logger log = LoggerFactory.getLogger( "Cuestionario.jsp" ); %>
<%
	String empleadoFirmante = StringUtils.trimToEmpty(  request.getParameter( "empleadoFirmante" ) );
	String requestID = StringUtils.trimToEmpty(  request.getParameter( "requestID" ) );
	String nomCompletoFirmante=StringUtils.trimToEmpty(  request.getParameter( "nomCompletoFirmante" ) );
	boolean standAloneMode = ( StringUtils.isEmpty( empleadoFirmante ) && StringUtils.isEmpty( requestID )  );  
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cuestionario de Contratacion.</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css"	href="../../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../../Generador/js/crud.js"></script>
<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../../js/jsquery.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../../js/catalogo/general.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript">

	var index = 0;
	var empleadoFirmante = "<%=empleadoFirmante%>";
	var requestID = "<%=requestID%>";
	var standAloneMode = <%=standAloneMode%>;
	var nomCompletoFirmante="<%=nomCompletoFirmante%>";
	var  msgContratacion15D = ""
		+ "Deberas contar en tu expediente de seguimiento al contrato con la siguiente documentación complementaria :"
		+ "\n -- Registro a que se refiere el artículo 15 de la Ley federal del trabajo,  "
		+ "\n -- Copia de los comprobantes fiscales por concepto de pago de salarios de los trabajadores con los que le hayan proporcionado el servicio o ejecutado la obra correspondiente, "
		+ "\n -- Recibo de pago expedido por institución bancaria por la declaración de entero de las retenciones de impuestos efectuadas a dichos trabajadores, "
		+ "\n -- Pago de las cuotas obrero patronales al Instituto Mexicano del Seguro Social, así como del pago de las aportaciones al Instituto del Fondo Nacional de la Vivienda para los Trabajadores, "
		+ "\n -- Copia de la declaración del impuesto al valor agregado y del acuse de recibo del pago correspondiente al periodo en que el contratante efectuó el pago de la contraprestación y del impuesto al valor agregado que le fue trasladado.";

	$(document).ready(function() {
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$("#esperar").dialog({
			autoOpen : false,
			height : 110,
			width : 200,
			modal : true,
			open: function(event, ui){
				$(".ui-dialog-titlebar").hide();
			},
			close : function() {
			}
		});
		queryFormPost({
			queryName:"primerPreguntaRead",
			async:false,
			callback:function(){
				agregaRenglonCuestionario();
			}
		});
		
	});
	
	function enviaCuestionario(){
		Swal.fire({
	        title: "",
	        text: "¿Esta seguro de enviar el cuestionario para firma mediante FIEL a nombre de "+nomCompletoFirmante+"?",
	        icon: 'info',
	        showCancelButton: true,
	        confirmButtonText: "Aceptar",
	        cancelButtonText: "Cancelar",
	    })
	    .then(resultado => {
	        if (resultado.value) {
	        	$("#esperar").dialog("open");
				$(".answer").each(function(){
					$(this).remove();
				});
				
				$( "#tCuestionario input:checked" ).each(function(){
					$("#frmCuestionario").append('<input class="answer" type="hidden" name="answer" value="' + $(this).attr("name") + '_' + $(this).val() + '">');
				});
				
				$("#frmCuestionario").append('<input class="answer" type="hidden" name="requestID" value="' + requestID + '">');
				$("#frmCuestionario").append('<input class="answer" type="hidden" name="empleadoFirmante" value="' + empleadoFirmante + '">');
				
				sendInfo();
	        } else {
	            // Dijeron que no
	            return;
	        }
	    });
		
		
	}
	
	function sendInfo(){
		$.ajax({
			url : '../../contratos/RegistraCuestionario',
			dataType : 'json',
			type : "POST",
			data : $("#frmCuestionario").serialize(),
			async : false,
			success : function(json) {
				var success = json.success;
				if( success == true || success == "true" ){
					alert("Solicitud de firma registrada con exito.");
					if( parent && parent.successCreation ){
						parent.successCreation();
					}
				}else{
					alert("No se logro registrar sus respuestas debido al error:\n" + json.errorMsg + "\nIntente nuevamente o reporte al administrador." );
				}
				$("#esperar").dialog("close");
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
						+ textStatus + "\n" + errorThrown);
				r = true;
				$("#esperar").dialog("close");
			}
		});
	}
	
	function continuaContrato(){
		
		Swal.fire("Cuestionario terminado.","Presione Guardar Respuestas para firmar el cuestionario o el botón Corregir cuestionario para cambiar las respuestas.", "success");
		$(".R" + (index-1) ).each(function(){
			$(this).attr("disabled","true");
		});
		
		$("#delDiv" + (index-1) ).css("visibility","hidden");
		$("#aplicaArt15D").val("0");
		agregaRenglonOperaciones();
		
	}
	
	function agregaRenglonOperaciones(){
		
		$("#tCuestionario tbody").append(
				'<tr id="operaciones">' +
				'	<td align="center" colspan="4">' +
				'		<input type="button" id="aceptarBtn" value="' + (  standAloneMode? "Guardar Respuestas"  : "Firmar Cuestionario"  ) + '">' +
				'		<input type="button" id="corregirBtn" value="Corregir Cuestionario">' +
				'	</td>' +
				'</tr>'
		);
		
		$("#corregirBtn").button().click(function(){
			revisar();
		});
		
		
		$("#aceptarBtn").button().click(function(){
			if(standAloneMode){
				var answers = new Array();
				$( "#tCuestionario input:checked" ).each(function(){
					answers.push( $(this).attr("name") + '_' + $(this).val() );
				});
				parent.saveQuestionnaire(answers, $("#aplicaArt15D").val() );
			}else
				enviaCuestionario();
		});
		
		index++;
		
	}
	
	function revisar(){
		$("#operaciones").remove();
		$(".R" + (index-2) ).each(function(){
			$(this)[0].disabled = false;
		});
		
		$("#delDiv" + (index-2) ).css("visibility","visible");
		index--;
	}
	
	function eliminaRenglon(){
		if( (index-1) > 0 && $("#R" + (index-1) ).length > 0 ){
			$("#R" + (index-1) ).remove();
			$(".R" + (index-2) ).each(function(){
				$(this)[0].disabled = false;
			});
			
			$("#delDiv" + (index-2) ).css("visibility","visible");
			index--;
		}
	}
	
	function agregaRenglonCuestionario(eliminar){
		
		$("#tCuestionario tbody").append(
				'<tr id="R' + index + '">' +
				
				'	<td align="left">' +
				'		<p> ' + $('#cPregunta').val() + ' </p>' +
				'	</td>'+
				
				'	<td align="center">' +
				'		<input class="Question R' + index + '" type="radio" id="Y_Q' + $('#nIdPregunta').val() + '" name="' + $('#nIdPregunta').val() + '" onclick="'+ $('#cActionYes').val() + '" value="S">' +
				'		<label for="Y_Q' + $('#nIdPregunta').val() + '">Sí</label>' +
				'	</td>'+
				
				'	<td align="center">' +
				'		<input class="Question R' + index + '" type="radio" id="N_Q' + $('#nIdPregunta').val() + '" name="' + $('#nIdPregunta').val() + '" onclick="'+ $('#cActionNo').val() + '" value="N">' +
				'		<label for="N_Q' + $('#nIdPregunta').val() + '">No</label>' +
				'	</td>'+
				(
						eliminar?
						(
							'	<td align="center">' +
							'		<div id="delDiv' + index + '">' + 		
							'			<a border="0" href="#" onclick="eliminaRenglon(' + index +');return false;">' +
						    '				<img border="0" class="btnEliminar" src="../../imagenes/iconos/rechazar.png" title="Cambiar Respuesta"></img>' + 
						    '			</a>' +
						    '		</div>'+ 
							'	</td>' 
							
						):
						(	'	<td align="center">' +
							'		&nbsp;' +
							'	</td>'
						)
				) +
				
				'</tr>'
				
		);
		index++;
	}

	
	function siguientePregunta(idPregunta){
		
		$("#idPreguntaRead").val(idPregunta);
		
		queryFormPost({
			queryName:"CuestionarioPreguntaRead",
			async:false,
			callback:function(){
				$(".R" + (index-1) ).each(function(){
					$(this).attr("disabled","true");
				});
				
				$("#delDiv" + (index-1) ).css("visibility","hidden");
				
				agregaRenglonCuestionario(true);
			}
		});
	}
	
	function generaFormato(){
		Swal.fire("Cuestionario terminado.","Presione Guardar Respuestas para firmar el cuestionario o el botón Corregir cuestionario para cambiar las respuestas.", "success");
		
		alert(msgContratacion15D);
		
		$(".R" + (index-1) ).each(function(){
			$(this).attr("disabled","true");
		});
		
		$("#delDiv" + (index-1) ).css("visibility","hidden");
		$("#aplicaArt15D").val("1");
		agregaRenglonOperaciones();
	} 
	
	function rechazaProceso(){
		Swal.fire("Por favor descarte este tramite.", "LO SENTIMOS, de conformidad con el artículo 15-D del CFF no es posible continuar con tu contratación", "error");
		parent.rechazaProceso();
		
	} 
</script>

</head>

<body id="dt_example" bgColor="red">
	<form action="" method="post" id="frmCuestionario">
		
		<input type="hidden" id="cPregunta"/>
		<input type="hidden" id="cActionYes" />
		<input type="hidden" id="cActionNo" />
		<input type="hidden" id="nIdPregunta" />
		<input type="hidden" id="idPreguntaRead"  name="idPreguntaRead"/>
		<input type="hidden" id="aplicaArt15D" name="aplicaArt15D"/>
		
		<div id="container" class="container">
			<h1>
				Cuestionario Sub-Contrataci&oacute;n
			</h1>
			<div>
				<fieldset>
					<legend>Responda las siguientes preguntas:</legend>
					
					<table id="tCuestionario" width="100%">
						<thead>
							<tr>
								<th width="80%">
									Pregunta
								</th>
								<th colspan="2" width="15%">
									Respuesta
								</th>
								<th  width="5%">
									Cancelar
								</th>
							</tr>
						</thead>
						
						<tbody id="bodyCuestionario">
							
						</tbody>
						
					</table>
					
				</fieldset>
			</div>
			<div id="esperar" align="center" title="Espera">
				<fieldset>
					<table>
						<tr>
							<td>Espere por favor.... <img border="0"src="../../imagenes/espera.gif" height="30"></td>
						</tr>
					</table>
				</fieldset>
			</div>
		</div>
	</form>
</body>

</html>