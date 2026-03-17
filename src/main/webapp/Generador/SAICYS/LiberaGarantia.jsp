<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String cIdContratoDefinitivo="";
	if(request.getParameter("cIdContratoDefinitivo")!=null){
		cIdContratoDefinitivo=request.getParameter("cIdContratoDefinitivo");
		session.setAttribute(GestionInterface.ATT_GarantiasContDefinitivo, cIdContratoDefinitivo);
	}else{
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_GarantiasContDefinitivo);
	}
	//System.out.print(cIdContratoDefinitivo);
%>
<!DOCTYPE html>
<html>
<head>
<title>Liberación de Garantías</title>
	<script type="text/javascript" charset="utf-8">
		tabb=2;
		var dataObject = new FormData();
		$(document).ready(function() {
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"Garantias","LiberaGarantia");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			roles="<%=roles%>";
			$("#tipoOperacion").val(2);
			$("#cIdContratoDefinitivo").val("<%=cIdContratoDefinitivo%>");
			$(".custom-file-input").on("change", function() {
				var fileName = $(this).val().split("\\").pop();
	  		  	$(this).siblings(".custom-file-label").addClass("selected").html(fileName);
	  		});
			agregaDatePickerFechas();
			consultaDatos();
			
		});//fin del document ready
		function descargaArchivo(){
			swal("En construcción",{icon:"success",button: "Cerrar"});
		}
	</script>
</head>
<body>
	<form id="formLiberaGarantia">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblEjercicioFiscal" id="lblEjercicioFiscal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblContratoCNET" id="lblContratoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProcedimientoCNET" id="lblProcedimientoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNeto" id="lblTotalNeto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblGarantiaAnticipo" id="lblGarantiaAnticipo" value="$ 0.00" readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblGarantiaCumplimiento" id="lblGarantiaCumplimiento" value="$ 0.00" readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblGarantiaViciosOcultos" id="lblGarantiaViciosOcultos" value="$ 0.00" readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="mTotalGarantias" id="mTotalGarantias" value="$ 0.00" readonly/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Captura Liberación de Garantía</legend>
			<div class="form-group">
				<div class="row" id="divFile">
					<div class="input-group">
					  	<div class="form-group col">
							<button type="button" class="btn btn-link float-end" id="btnFile1" name="btnFile1"  onclick="downloadFileLiberaGarantia(4)">Documento Garantía Liberada</button>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-6">
							<h6>Garant&iacute;a de Cumplimiento</h6>
							<div class="form-check form-check-inline" >
								<input class="form-check-input" type="checkbox" id="inlineChequeG" value="1" disabled="disabled">
								<label class="form-check-label" for="inlineChequeG" >CHEQUE</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="inlineFianzaG" value="2" disabled="disabled">
							  	<label class="form-check-label" for="inlineFianzaG">FIANZA</label>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-6">
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="inlineChequeEntregado" value="1" onclick="onclickChequeEntregado();">
								<label class="form-check-label" for="inlineChequeEntregado">¿Cheque entregado a proveedor?</label>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-3 ">
	 						<label for="nChequeFianza">No. de Oficio de Solicitud</label>
							<input type="text" class="form-control" placeholder="Captura el número de oficio de solicitud"  id="nOficioSolicitud" name="nOficioSolicitud" >
							
						</div>
						<div class="form-group col-md-3">
	 						<label for="fOficioSolicitud">Fecha de Oficio de Solicitud</label>
	 						<div class="input-group date" id="fOficioSolicitud" data-target-input="nearest">
								<input type="text" class="form-control datetimepicker-input" data-target="#fOficioSolicitud" placeholder="dd/mm/aaaa"  id="fFechaOficioSol" name="fFechaOficioSol" />
					          	<div class="input-group-append" data-target="#fOficioSolicitud" data-toggle="datetimepicker">
					            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          	</div>
					        </div>
						</div>
						<div class="form-group col-md-3 ">
	 						<label for="nChequeFianza">No. de Oficio de Liberación</label>
							<input type="text" class="form-control" placeholder="Captura el número de oficio de liberación"  id="nOficioLiberacion" name="nOficioLiberacion" >
							
						</div>
						<div class="form-group col-md-3">
	 						<label for="fFechaOficioLiberacion">Fecha de Oficio de Liberación</label>
	 						<div class="input-group date" id="fFechaOficioLiberacion" data-target-input="nearest">
								<input type="text" class="form-control datetimepicker-input" data-target="#fFechaOficioLiberacion" placeholder="dd/mm/aaaa"  id="fFechaOficioLibera" name="fFechaOficioLibera" />
					          	<div class="input-group-append" data-target="#fFechaOficioLiberacion" data-toggle="datetimepicker">
					            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          	</div>
					        </div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-12 ">
	 						<label for="nChequeFianza">Motivo de Liberación</label>
							<input type="text" class="form-control" placeholder="Captura el motivo de liberación"  id="cMotivoLiberacion" name="cMotivoLiberacion" >
							
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-12">
							<div class="custom-file" align="left">
						    	<input type="file" class="custom-file-input" id="nameArchivoLiberaGarantia" name="nameArchivoLiberaGarantia" aria-describedby="inputGroupFileAddon01" >
						    	<label class="custom-file-label" for="nameArchivoLiberaGarantia" >Favor de adjuntar los archivos comprobatorios de la liberación de garantía con extensión .zip</label>
						  	</div>
						  	
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
					  	<div class="form-group col-auto">
							<button type="button" class="btn btn-primary" id="btnGuardarLiberaGarantia" name="btnGuardarLiberaGarantia"  onclick="saveItemsLiberaGarantia()">Guardar</button>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="cFolio" name="cFolio" value="" />
		<input type="hidden" id="contratoCNET" name="contratoCNET" value="" />
		<input type="hidden" id="lFianzaG" name="lFianzaG" value="0" />
		<input type="hidden" id="lchequeG" name="lchequeG" value="0" />
		<input type="hidden" id="lChequeEntregado" name="lChequeEntregado" value="0" />
		<input type="hidden" id="cFolio" name="cFolio" value="" />
		<input type="hidden" id="contratoCNET" name="contratoCNET" value="" />
		<input type="hidden" id="existeDoctoGarantiaLiberada" name="existeDoctoGarantiaLiberada" value="0" />
		
	</form>
</body>
</html>