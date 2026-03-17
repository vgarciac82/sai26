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
<style type="text/css">
	.table .thTitle {
	    background: aliceblue;
	    text-align: center;	
	    vertical-align: middle;
	}
</style>
<title>Captura de garantias</title>
	<script type="text/javascript" charset="utf-8">
		var roles;
		tabb=2;
		var tipo;
		var dataObject = new FormData();
		var dataObjectEndoso = new FormData();
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
				Map botones=nb.getBotones(roles,"CapturaGarantias","CapturaGarantiasContrato");
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
		
	</script>
</head>
<body>
	<form id="formCaptGarantia">
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
						<input type="text" class="form-control  transpInput" name="mTotalGarantias" id="mTotalGarantias" value="$ 0.00" readonly/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<div class="accordion accordion-flush" id="accordionFlushExample">
				<div class="accordion-item">
				    <h5 class="accordion-header" id="flush-headingOne">
				      	<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseOne" aria-expanded="false" aria-controls="flush-collapseOne">
				        	Captura de Garantías
				      	</button>
				    </h5>
				    <div id="flush-collapseOne" class="accordion-collapse collapse show" aria-labelledby="flush-headingOne" data-bs-parent="#accordionFlushExample">
				      	<div class="accordion-body">
							<div class="form-group">
								<div class="row" id="divDownloadFileGarantia">
									<div class="input-group">
										<div class="form-group col">
					 						<button id="downloadGarantia" name="downloadGarantia" class="btn btn-link float-end" title="Descarga los archivos comprobatorios de la garantía" onclick="downloadFile(2);">Documento Garantia</button>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<table class="table">
								  <thead>
								    <tr>
								      <th scope="col">Tipo Garantía</th>
								      <th scope="col">Tipo Documento</th>
								      <th scope="col">Aseguradora/Afianzadora</th>
								      <th scope="col">No. de Cheque/Fianza</th>
								      <th scope="col">Fecha de<br> Expedición</th>
								      <th scope="col">Monto de Garantía</th>
								    </tr>
								  </thead>
								  <tbody>
								    <tr>
								      <th scope="row" class="thTitle">Anticipo</th>
								      	<td>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineChequeGA" value="1" onclick="onclickChequeGarantiaA();">
												<label class="form-check-label" for="inlineChequeGA">CHEQUE</label>
											</div>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineFianzaGA" value="2" onclick="onclickFianzaGarantiaA();">
											  	<label class="form-check-label" for="inlineFianzaGA">FIANZA</label>
											</div>
										</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Nombre de Aseguradora/Afianzadora"  id="cAseguradoraGA" name="cAseguradoraGA" title="Captura Nombre de Aseguradora/Afianzadora"></td>
								      	<td><input type="text" class="form-control" placeholder="Captura Número de Cheque/Fianza"  id="nChequeFianzaGA" name="nChequeFianzaGA" title="Captura Número de Cheque/Fianza"></td>
								      	<td>
								      		<div class="input-group date" id="fExpedidoGA" data-target-input="nearest">
												<input type="text" class="form-control datetimepicker-input" data-target="#fExpedidoGA" placeholder="dd/mm/aaaa"  id="fFechaExpedicionGA" name="fFechaExpedicionGA" title="Captura la Fecha de Expedición Cheque/Fianza"/>
									          	<div class="input-group-append" data-target="#fExpedidoGA" data-toggle="datetimepicker">
									            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
									          	</div>
									        </div>
								      	</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Monto Con IVA de la Garantía de Anticipo"  id="mMontoGarantiaAnticipo" name="mMontoGarantiaAnticipo" title="Captura Monto Con IVA de la Garantía de Anticipo"
								      	 onkeypress="return onlyMoney(event)" onblur="moneyFormat(this)"></td>
								    </tr>
								    <tr>
								      	<th scope="row" class="thTitle">Cumplimiento</th>
								    	<td>
								      		<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineChequeGC" value="1" onclick="onclickChequeGarantiaC();">
												<label class="form-check-label" for="inlineChequeGC">CHEQUE</label>
											</div>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineFianzaGC" value="2" onclick="onclickFianzaGarantiaC();">
											  	<label class="form-check-label" for="inlineFianzaGC">FIANZA</label>
											</div>
										</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Nombre de Aseguradora/Afianzadora"  id="cAseguradoraGC" name="cAseguradoraGC" ></td>
								      	<td><input type="text" class="form-control" placeholder="Captura Número de Cheque/Fianza"  id="nChequeFianzaGC" name="nChequeFianzaGC" ></td>
								      	<td>
								      		<div class="input-group date" id="fExpedidoGC" data-target-input="nearest">
												<input type="text" class="form-control datetimepicker-input" data-target="#fExpedidoGC" placeholder="dd/mm/aaaa"  id="fFechaExpedicionGC" name="fFechaExpedicionGC" />
									          	<div class="input-group-append" data-target="#fExpedidoGC" data-toggle="datetimepicker">
									            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
									          	</div>
									        </div>
								      	</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Monto Sin IVA de Garantía de Cumplimiento"  id="mMontoGarantiaCumplimiento" name="mMontoGarantiaCumplimiento" 
								      	onkeypress="return onlyMoney(event)" onblur="moneyFormat(this)" title="Captura Monto Sin IVA de la Garantía de Cumplimiento"></td> 	
								    </tr>
								    <tr>
								      	<th scope="row" class="thTitle">Vicios Ocultos</th>
								      	<td>
								      		<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineChequeGV" value="1" onclick="onclickChequeGarantiaV();">
												<label class="form-check-label" for="inlineChequeGV">CHEQUE</label>
											</div>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineFianzaGV" value="2" onclick="onclickFianzaGarantiaV();">
											  	<label class="form-check-label" for="inlineFianzaGV">FIANZA</label>
											</div>
										</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Nombre de Aseguradora/Afianzadora"  id="cAseguradoraGV" name="cAseguradoraGV" ></td>
								      	<td><input type="text" class="form-control" placeholder="Captura Número de Cheque/Fianza"  id="nChequeFianzaGV" name="nChequeFianzaGV" ></td>
								      	<td>
								      		<div class="input-group date" id="fExpedidoGV" data-target-input="nearest">
												<input type="text" class="form-control datetimepicker-input" data-target="#fExpedidoGV" placeholder="dd/mm/aaaa"  id="fFechaExpedicionGV" name="fFechaExpedicionGV" />
									          	<div class="input-group-append" data-target="#fExpedidoGV" data-toggle="datetimepicker">
									            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
									          	</div>
									        </div>
								      	</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Monto Sin IVA de la Garantía de Vicios Ocultos"  id="mMontoGarantiaViciosO" name="mMontoGarantiaViciosO" 
								      	onkeypress="return onlyMoney(event)" onblur="moneyFormat(this)" title="Captura Monto Sin IVA de la Garantía de Vicios Ocultos"></td>
								    </tr>
								  </tbody>
								</table>
							</div>
							<div class="form-group">
								<div class="row">
									<div class="input-group">
										<div class="form-group col-md-12">
											<div class="custom-file" align="left">
										    	<input type="file" class="custom-file-input" id="nameArchivoGarantia" name="nameArchivoGarantia" aria-describedby="inputGroupFileAddon01" >
										    	<label class="custom-file-label" for="nameArchivoGarantia" >Favor de adjuntar los archivos comprobatorios de la garantía con extensión .zip</label>
										  	</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="input-group">
									  	<div class="form-group col-auto">
											<button type="button" class="btn btn-primary" id="btnGuardarGarantia" name="btnGuardarGarantia"  onclick="saveItemsGarantia()">Guardar</button>
										</div>
									</div>
								</div>
							</div>
						</div>
				    </div>
				</div>
			  	<div class="accordion-item">
				    <h5 class="accordion-header" id="flush-headingTwo">
						<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseTwo" aria-expanded="false" aria-controls="flush-collapseTwo">
				        	Captura Endoso
				      	</button>
				    </h5>
				    <div id="flush-collapseTwo" class="accordion-collapse collapse" aria-labelledby="flush-headingTwo" data-bs-parent="#accordionFlushExample">
				      <div class="accordion-body">
				      	<div class="form-group">
							<div class="row" id="divDownloadFileEndoso">
								<div class="input-group">
									<div class="form-group col">
				 						<button id="downloadEndoso" name="downloadEndoso" class="btn btn-link float-end" title="Descarga los archivos comprobatorios del endoso" onclick="downloadFile(3);">Documento Endoso</button>
									</div>
								</div>
							</div>
						</div>
						<div class="form-group">
	 						<label for="cAseguradora_endoso">Aseguradora/Afianzadora</label>
							<input type="text" class="form-control" placeholder="Captura el nombe de la aseguradora o afianzadora"  id="cAseguradora_endoso" name="cAseguradora_endoso" >
						</div> 
						<div class="form-group">
								<table class="table">
								  <thead>
								    <tr>
								      <th scope="col">No. Endoso</th>
								      <th scope="col">Tipo Documento</th>
								      <th scope="col">No. de Cheque/Fianza</th>
								      <th scope="col">Fecha de Expedición</th>
								      <th scope="col">Monto del Endoso</th>
								    </tr>
								  </thead>
								  <tbody>
								    <tr>
								      <th scope="row" class="thTitle">#1</th>
								      	<td>
								      		<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineChequeE" value="1" onclick="onclickChequeEndoso('');">
												<label class="form-check-label" for="inlineChequeE">CHEQUE</label>
											</div>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineFianzaE" value="2" onclick="onclickFianzaEndoso('');">
											  	<label class="form-check-label" for="inlineFianzaE">FIANZA</label>
											</div>
										</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Número de Cheque/Fianza"  id="nChequeFianza_endoso" name="nChequeFianza_endoso" title="Captura Número de Cheque/Fianza"></td>
								      	<td>
									        <div class="input-group date" id="fExpedido_endoso" data-target-input="nearest">
												<input type="text" class="form-control datetimepicker-input" data-target="#fExpedido_endoso" placeholder="dd/mm/aaaa"  id="fFechaExpedicion_endoso" name="fFechaExpedicion_endoso" />
									          	<div class="input-group-append" data-target="#fExpedido_endoso" data-toggle="datetimepicker">
									            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
									          	</div>
									        </div>
								      	</td>
								      	<td><input type="text" class="form-control" placeholder="$ 0.00"  id="mMontoEndosoCumplimiento" name="mMontoEndosoCumplimiento" title="Captura Monto Con IVA del Endoso #1"
								      	 onkeypress="return onlyMoney(event)" onblur="moneyFormat(this)"></td>
								    </tr>
								    <tr>
								      	<th scope="row" class="thTitle">#2</th>
								    	<td>
								      		<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineChequeE2" value="1" onclick="onclickChequeEndoso('2');">
												<label class="form-check-label" for="inlineChequeE2">CHEQUE</label>
											</div>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineFianzaE2" value="2" onclick="onclickFianzaEndoso('2');">
											  	<label class="form-check-label" for="inlineFianzaE2">FIANZA</label>
											</div>
										</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Número de Cheque/Fianza"  id="nChequeFianza_endoso2" name="nChequeFianza_endoso2" ></td>
								      	<td>
								      		<div class="input-group date" id="fExpedido_endoso2" data-target-input="nearest">
												<input type="text" class="form-control datetimepicker-input" data-target="#fExpedido_endoso2" placeholder="dd/mm/aaaa"  id="fFechaExpedicion_endoso2" name="fFechaExpedicion_endoso2" />
									          	<div class="input-group-append" data-target="#fExpedido_endoso2" data-toggle="datetimepicker">
									            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
									          	</div>
									        </div>
								      	</td>
								      	<td><input type="text" class="form-control" placeholder="$ 0.00"  id="mMontoEndosoCumplimiento2" name="mMontoEndosoCumplimiento2" title="Captura Monto Con IVA del Endoso #2"
								      	 onkeypress="return onlyMoney(event)" onblur="moneyFormat(this)"></td> 	
								    </tr>
								    <tr>
								      	<th scope="row" class="thTitle">#3</th>
								      	<td>
								      		<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineChequeE3" value="1" onclick="onclickChequeEndoso('3');">
												<label class="form-check-label" for="inlineChequeE3">CHEQUE</label>
											</div>
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox" id="inlineFianzaE3" value="2" onclick="onclickFianzaEndoso('3');">
											  	<label class="form-check-label" for="inlineFianzaE3">FIANZA</label>
											</div>
										</td>
								      	<td><input type="text" class="form-control" placeholder="Captura Número de Cheque/Fianza"  id="nChequeFianza_endoso3" name="nChequeFianza_endoso3" ></td>
								      	<td>
								      		<div class="input-group date" id="fExpedido_endoso3" data-target-input="nearest">
												<input type="text" class="form-control datetimepicker-input" data-target="#fExpedido_endoso3" placeholder="dd/mm/aaaa"  id="fFechaExpedicion_endoso3" name="fFechaExpedicion_endoso3" />
									          	<div class="input-group-append" data-target="#fExpedido_endoso3" data-toggle="datetimepicker">
									            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
									          	</div>
									        </div>
								      	</td>
								      	<td><input type="text" class="form-control" placeholder="$ 0.00"   id="mMontoEndosoCumplimiento3" name="mMontoEndosoCumplimiento3" title="Captura Monto Con IVA del Endoso #3"
								      	 onkeypress="return onlyMoney(event)" onblur="moneyFormat(this)"></td>
								    </tr>
								  </tbody>
								</table>
							</div>
						<div class="form-group">
							<div class="row">
								<div class="input-group">
									<div class="form-group col-md-12">
										<div class="custom-file" align="left">
									    	<input type="file" class="custom-file-input" id="nameArchivoEndoso" name="nameArchivoEndoso" aria-describedby="inputGroupFileAddon01" required="required">
									    	<label class="custom-file-label" for="nameArchivo" >Favor de adjuntar los archivos comprobatorios del endoso con extensión .zip</label>
									  	</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="input-group">
								  	<div class="form-group col-auto">
										<button type="button" class="btn btn-primary" id="btnGuardarEndoso" name="btnGuardarEndoso"  onclick="saveItemsEndoso()">Guardar</button>
									</div>
								</div>
							</div>
						</div>
				      </div>
				    </div>
			  	</div>
			</div>
		</fieldset>
		<input type="hidden" id="lFianzaGA" name="lFianzaGA" value="0" />
		<input type="hidden" id="lchequeGA" name="lchequeGA" value="0" />
		<input type="hidden" id="lFianzaGC" name="lFianzaGC" value="0" />
		<input type="hidden" id="lchequeGC" name="lchequeGC" value="0" />
		<input type="hidden" id="lFianzaGV" name="lFianzaGV" value="0" />
		<input type="hidden" id="lchequeGV" name="lchequeGV" value="0" />
		<input type="hidden" id="lFianzaE" name="lFianzaE" value="0" />
		<input type="hidden" id="lchequeE" name="lchequeE" value="0" />
		<input type="hidden" id="lFianzaE2" name="lFianzaE2" value="0" />
		<input type="hidden" id="lchequeE2" name="lchequeE2" value="0" />
		<input type="hidden" id="lFianzaE3" name="lFianzaE3" value="0" />
		<input type="hidden" id="lchequeE3" name="lchequeE3" value="0" />
		<input type="hidden" id="cFolio" name="cFolio" value="" />
		<input type="hidden" id="contratoCNET" name="contratoCNET" value="" />
		<input type="hidden" id="existeDoctoGarantia" name="existeDoctoGarantia" value="0" />
		<input type="hidden" id="existeDoctoEndoso" name="existeDoctoEndoso" value="0" />
	</form>
</body>
</html>