<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String cIdContratoDefPluri=(String)session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
	String cidContratoOriginal=(String)session.getAttribute(GestionInterface.ATT_ContratoPlurianual);
		
	Map<String, Role> rol =usuario.getRoles();
	String roles="";
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.DAY_OF_MONTH, +20);
	String fechaLimite= sdf.format(c1.getTime());
%>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="ISO-8859-1">
	<title>Terminaci&oacute; Anticipada para contratos Plurianuales</title>
		<script type="text/javascript">
			var roles='';
			var fechaLimiteDefault = "<%=fechaLimite%>";
			var data = new FormData();
			$(document).ready(function() {
				tabb=7;
				showAndHideTabs();
				<%
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						roles += r.getKey().toString()+",";
					}
					if(roles.length()>0){
						roles = roles.substring(0,roles.length()-1);
					}
					//botones
					NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
					Map botones=nb.getBotones(roles,"PlurianualidadContratos","CaratulaPlurianualidad");
						Iterator btn = botones.entrySet().iterator();
						while (btn.hasNext()) {
							Map.Entry b = (Map.Entry)btn.next();%>
							$("#<%=b.getValue()%>").attr("disabled", true);<%
							String img=(String) b.getValue();
							
						}
				%>
				roles="<%=roles%>";
				$(".custom-file-input").on("change", function() {
		  		  var fileName = $(this).val().split("\\").pop();
		  		  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
		  		});
				headerQuery();
				
			});
			function headerQuery(){
				queryFormPost("datosContratoPlurianual", {async: false,
					callback : function() {
						muestra();
					}
				});
				queryFormPost("mUsuarioMismaUE", {async: false   });
			}
			function muestra(){
				if($("#terminacionAnti").val()==1){
					$("#divCapturaDatos").hide();
					$("#divConsultaDatos").show();
					cargaTabla();
				}else{
					agregaDatePickerFechas();
					$("#divConsultaDatos").hide();
					$("#divCapturaDatos").show();
				}
				
			}
			function muestraUltimoPago(show){
				if( show ){
					$("#pagoPendienteTr").show();
					$("#limitePagoPendienteTr").show();
					$("#fNotificacionUAF_DIV").show();
					$("#fNotificacionUAF").val("");
				}else{
					$("#pagoPendienteTr").hide();
					$("#fLimitePagoPendiente").val(fechaLimiteDefault);
					$("#llevaPagoPendiente").attr("checked",true);
					$("#limitePagoPendienteTr").show();
					
					$("#fNotificacionUAF_DIV").hide();
					$("#fNotificacionUAF").val("");
				}
				
			}
			function agregaDatePickerFechas(){
				$("#fTerminacion").datetimepicker({
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
				});
				$("#fNotificacion").datetimepicker({
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
				});
				$("#fLimitePago").datetimepicker({
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
				});
			}
			function  cargaTabla(){
				var qw="cIdContratoDefinitivo='"+$("#cContratoDefinitivo").val()+"'";
				oTableDocumentos = $("#tblConsultaDatos").dataTable({
					bScrollCollapse: true,
	        		bInfo: false,
	        		//sScrollY : "100%",
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
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
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratosTerminacionAnticipada&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ]] ,
					aoColumns: [
						{sName: "tipoTerminacion"},
						{sName: "cCausa"},
						{sName: "fFechaTermino"},
						{sName: "fFechaLimitePagoPendiente"},
						{sName: "fFechaCaptura"},
						{sName: "cLogin"}
						
					]
				});
			}
			function llevaPagoPendienteAction(){
				if ($('#llevaPagoPendiente').is(':checked')) {
					$("#fLimitePagoPendiente").val(fechaLimiteDefault);
					$("#limitePagoPendienteTr").show();
				}else{
					$("#fLimitePagoPendiente").val("");
					$("#limitePagoPendienteTr").hide();
				}
			}
			function validaFechaUltimoPago(){
				
				if( !$("#llevaPagoPendiente").is(":checked") || ( $("#llevaPagoPendiente").is(":checked") && $("#fLimitePagoPendiente").val() !== "" ) )
					return true;
				else 
					return false;
			}
			function validaFechaNotificacion(){
				if( $("#resicionContrato").is(":checked") )
					return  $("#fNotificacionUAF").val() !== "" 
				else 
					return true;
			}
			function Guardar(){
				$.blockUI({message: "Procesando espere ......"});
				if($("#causaTerminoContrato").val()==""){
					swal("Favor de capturar la causa de la terminación del contrato.",{icon:"warning",button: "Cerrar"});
					$.unblockUI();
					return;
				}
				if(!validateAtachment()){
					swal("Favor de seleccionar un archivo.",{icon:"warning",button: "Cerrar"});
					$.unblockUI();
					return;
				}
				if( !validaFechaNotificacion() ){
					swal("Favor de ingresar la fecha de notificacion a la UAF.",{icon:"warning",button: "Cerrar"});
					$.unblockUI();
					return;
				}
				if( !validaFechaUltimoPago() ){
					swal("Favor de ingresar la fecha limite para tramitar el pago.",{icon:"warning",button: "Cerrar"});
					$.unblockUI();
					return;
				}
				ejecutaAjax();
			}
			function validateAtachment(){
				var hayDocumento=false;
				jQuery.each(jQuery('#nameArchivo')[0].files, function(i, file) {
				    data.append('file-'+i, file);
				    hayDocumento=true;
				});
				return hayDocumento;
			}
			function ejecutaAjax(){
				data.append('operacion', $("#operacion").val());
				data.append('descripcionCausa', encodeURIComponent($("#causaTerminoContrato").val()));
				data.append('fechaTermino', $("#fTermino").val());
				data.append('fechaLimitePagoPendiente', $("#fLimitePagoPendiente").val());
				
				if(document.getElementById("terminacionaAnt").checked) {
					$("#nTipoTerminacionCont").val( $("#terminacionaAnt").val());
	            }else if(document.getElementById("minimosAgotados").checked) {
	            	$("#nTipoTerminacionCont").val( $("#minimosAgotados").val());
	            }else{
	            	$("#nTipoTerminacionCont").val( $("#resicionContrato").val());
	            }
				data.append('nTipoTerminacionCont', $("#nTipoTerminacionCont").val());
				data.append('cFolio', $("#C_FOLIO").val());
				data.append('cContratoDefinitivo', $("#cContratoDefinitivo").val());
				data.append('fechaNotificacionUAF', $("#fNotificacionUAF").val());
				
				jQuery.ajax({
				    url: '../../servlet/LeeArchivos',
				    data: data,
				    cache: false,
				    contentType: false,
				    dataType: "json",
				    processData: false,
				    method: 'POST',
				    type: 'POST', // For jQuery < 1.9
				    success: function(j){
				    	headerQuery();
				    	if(j[0].ISCORRECT=="true" || j[0].ISCORRECT){
				    		swal(j[0].MSG,{icon:"success",button: "Cerrar"});
				    	}else{
				    		swal(j[0].MSG,{icon:"error",button: "Cerrar"});
				    	}
				    	$.unblockUI();
				    },error: function(){
				    	swal("Error",{icon:"error",button: "Cerrar"});
				    	$.unblockUI();
				    }
				});
			}
		</script>
	</head>
	<body>
		<form action="formTerminacionAntPlu">
			<fieldset class="form-group border p-3">
				<legend class="w-auto px-2"> Datos de contrato</legend>
				<div class="form-group">
					<div class="row">
						<div class="col">
							<input type="text" class="form-control transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblcIdContratoDefinitivoPluri" id="lblcIdContratoDefinitivoPluri"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblcNoContratoCNET" id="lblcNoContratoCNET"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblDescContrato" id="lblDescContrato"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput font-weight-bold" name="lblEstatus" id="lblEstatus"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblMontoIVA" id="lblMontoIVA"  readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<input type="text" class="form-control  transpInput" name="lblTotal" id="lblTotal"  readonly/>
						</div>
					</div>
				</div>
			</fieldset>
			<fieldset class="form-group border p-3">
				<legend class="w-auto px-2"> Terminaci&oacute;n Ancipada</legend>
				<div class="form-group" id="divCapturaDatos" style="display: none">
					<div class="row-group">
						<div class="col ">
							<div class="row" id="divCheck" >
								<div class="col-auto">
									<div class="form-check form-check-inline">
						  				<input class="form-check-input" type="radio" id="terminacionaAnt" name="radioTerminacionCont" value="1" checked="checked" onclick="muestraUltimoPago(false)" >
						  				<label class="form-check-label" for="terminacionaAnt">Terminaci&oacute;n anticipada </label>
					  				</div>
					  			</div>
							</div>
						</div>
					</div>
					<div class="row-group">
						<div class="col ">
							<div class="row" id="divCheck2" >
								<div class="col-auto">
									<div class="form-check form-check-inline">
						  				<input class="form-check-input" type="radio" id="minimosAgotados" name="radioTerminacionCont" value="2" onclick="muestraUltimoPago(false)">
						  				<label class="form-check-label" for="minimosAgotados">Por haber agotado los m&iacute;nimos </label>
					  				</div>
					  			</div>
							</div>
						</div>
	  				</div>
					<div class="row-group" style="display: none;">
						<div class="col ">
							<div class="row" id="divCheck3" >
								<div class="col-auto">
									<div class="form-check form-check-inline">
						  				<input class="form-check-input" type="radio" id="resicionContrato" name="radioTerminacionCont" value="3" onclick="muestraUltimoPago(true)">
						  				<label class="form-check-label" for="resicionContrato">Por recisi&oacute;n de Contrato.</label>
					  				</div>
					  			</div>
							</div>
						</div>
					</div>
	  				<div class="form-group row-group">
					  	<div class="col">
					  		<textarea class="form-control" placeholder="Favor de capturar la causa de la terminación del contrato" id="causaTerminoContrato" name="causaTerminoContrato" rows="3" title="Capture la causa de la terminación del contrato"></textarea>
					  	</div>
					</div>
	  				<div class="form-group row-group">
						<div class=" col">
							<div class="row">
								<div class="col-md-5">
									<div class="custom-file" align="left">
								    	<input type="file" class="custom-file-input" id="nameArchivo" aria-describedby="inputGroupFileAddon01" >
								    	<label class="custom-file-label" for="nameArchivo" >Favor de adjuntar archivos .zip</label>
								  	</div>
							  	</div>
							</div>
						</div>
					</div>
		  			<div class="row-group">
						<div class="col">
							<div class="row" align="left">
								<div class="col-md-3">
									<label for="fTerminacion">Fecha de terminaci&oacute;n</label>
								</div>
							</div>
							<div class="row" >
								<div class="col-md-3">
									<div class="input-group date" id="fTerminacion" data-target-input="nearest">
							          <input type="text" class="form-control datetimepicker-input" data-target="#fTerminacion" placeholder="dd/mm/aaaa" title="Fecha de Termino" id="fTermino" name="fTermino" value="<%=today %>"/>
							          <div class="input-group-append" data-target="#fTerminacion" data-toggle="datetimepicker" title="Fecha de Termino">
							            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							          </div>
							        </div>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group row-group" id="fNotificacionUAF_DIV" style="display: none">
						<div class="col">
							<div class="row" align="left">
								<div class="col-md-3">
									<label for="fNotificacion">Fecha de noticiaci&oacute;n a la UAF</label>
								</div>
							</div>
							<div class="row" >
								<div class="col-md-3">
									<div class="input-group date" id="fNotificacion" data-target-input="nearest">
							          <input type="text" class="form-control datetimepicker-input" data-target="#fNotificacion" placeholder="dd/mm/aaaa" title="Fecha de notificación a la UAF" id="fNotificacionUAF" name="fNotificacionUAF" value=""/>
							          <div class="input-group-append" data-target="#fNotificacion" data-toggle="datetimepicker" title="Fecha de notificación a la UAF">
							            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							          </div>
							        </div>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group row-group" id="pagoPendienteTr" style="display: none">
						<div class="col ">
							<div class="row" id="divCheck3" >
								<div class="col-auto">
									<div class="form-check form-check-inline">
						  				<input class="form-check-input" type="checkbox" id="llevaPagoPendiente" name="llevaPagoPendiente" checked="checked" onclick="llevaPagoPendienteAction()">
						  				<label class="form-check-label" for="resicionContrato">Existe pago pendiente</label>
					  				</div>
					  			</div>
							</div>
						</div>
	  				</div>
					<div class="form-group row-group" id="limitePagoPendienteTr">
						<div class="col">
							<div class="row" align="left">
								<div class="col-md-3">
									<label for="fLimitePago">Fecha limite para tramitar pago pendiente</label>
								</div>
							</div>
							<div class="row" >
								<div class="col-md-3">
									<div class="input-group date" id="fLimitePago" data-target-input="nearest">
							          <input type="text" class="form-control datetimepicker-input" data-target="#fLimitePago" placeholder="dd/mm/aaaa" title="Fecha dlimite para tramitar pago pendiente"
							           		id="fLimitePagoPendiente" name="fLimitePagoPendiente" value="<%=fechaLimite%>"/>
							          <div class="input-group-append" data-target="#fLimitePago" data-toggle="datetimepicker" title="Fecha dlimite para tramitar pago pendiente">
							            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							          </div>
							        </div>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group row-group">
						<div class=" col" align="left">
							<button type="button" class="btn btn-primary" id="btnGuardar" name="btnGuardar" onclick="Guardar()">Guardar</button>
						</div>
					</div>
				</div>
				<br>
				<br>
				<div class="form-group" id="divConsultaDatos" style="display: none">
					<div class="form-group row">
						<div class="col">
							<table id="tblConsultaDatos" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th align="center">Tipo de Termino</th>
										<th align="center">Causa</th>
										<th align="center">Fecha de Termino</th>
										<th align="center">Fecha limite para<br>tramitar pago pendiente</th>
										<th align="center">Fecha de captura</th>
										<th align="center">Usuario</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
				
			</fieldset>
	 		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cidContratoOriginal %>"  />
	    	<input type="hidden" name="cIdContratoDefinitivoPlurianual" id="cIdContratoDefinitivoPlurianual" value="<%=cIdContratoDefPluri %>"  />
	    	<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefPluri %>"  />
	    	<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
	    	<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion"/>
    		<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE"/>
    		<input type="hidden" id="operacion" name="operacion" value="3"/>
    		<input type="hidden" id="C_FOLIO" name="C_FOLIO" value=""/>
    		<input type="hidden" id="terminacionAnti" name="terminacionAnti" value="-1"/>
    		<input type="hidden" id="nTipoTerminacionCont" name="nTipoTerminacionCont" value="1"/>
		</form>
	</body>
</html>