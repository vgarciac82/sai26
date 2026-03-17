<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	String uLogin = usuario.getLogin();
	String folio = c.getFolio();
	String ue=folio.substring( 5, 8 );
	Calendar c1 = Calendar.getInstance();
	String today = sdf.format(c1.getTime());
	String msg;
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	int idCaso = c.getIdCaso();
	
%>
<!DOCTYPE html>
<html>
  <head>
    <title>Penas convencionales</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle"> 
		@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
 		@import "../../css/interfaz.css";
	</style>
	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>
	
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.min.js"></script>
	
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
	<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	<script type="text/javascript" src="../js/CapturaPenaConvencional.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>

	<script type="text/javascript">
		var oTablePenalty,oTableDeduction,oTableConsulta;
		var stringDeduction="Dedu";
		var stringPenalty="Pena";
		var devolucion;
		var myModal;
		var aData;
		$(document).ready(function() {
			$('#tblConsulta').on('dblclick', 'tr',function(){
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
			  	var anSelected=fnGetSelected(oTableConsulta);
			  	aData=oTableConsulta.fnGetData(anSelected[0]);
			  	if($("#pedidoContratoCompromiso").val()!="" && $("#pedidoContratoCompromiso").val()!=aData[0] ){
			  		swal({
			  			title: "¿Desea continuar?",
			  			text: "Ya se tenía un contrato seleccionado, se borraran los datos guardados con anterioridad!",
			  			icon: "info",
			  			buttons: {
			  				confirm : "Aceptar",
			  				cancel: "Cancelar"
			  			},
			  		}).then((continuar) => {
			  			if (!continuar) {
			  				return;
			  			}else{
			  				$("#pedidoContratoCompromiso").val(aData[0]);
			  				deletePenalityItems(idOper);
			  			}
			  		});
			  	}
			  	$("#pedidoContratoCompromiso").val(aData[0]);
  				$("#cNumCNET").val(aData[2]);
			  	$("#cProveedor").val("[ "+aData[1]+" ] "+aData[3]);
			  	$("#cIdTipoContrato").val(aData[5]);
			  	oTablePenalty="";
			  	oTableDeduction="";
			  	myModal.hide();
			});
		});//fin document ready
	</script>
	</head>
 
  	<body id="capPenaConv">
  		<form name="frmCapPenaConv" action="" method="POST" target="_blank">
			<div class="container-fluid fondoWhite margin">
				<div class="row">
					<h4 ><span style="color: #1A69A9;" id="spanEncabezado">Captura penas convencionales y/o deducciones al pago </span> </h4>
				</div>
				<div class="row">
					<fieldset class="form-group border p-3">
						<div class="form-group  row">
							<div class="form-group  col-4">
								<div class="input-group">
						      		<span class="input-group-text input-group-sm">FOLIO SAI:</span>
						      		<input type="text" class=" form-control input-group-sm font-weight-bold" disabled title="Número de Folio, se asigna automáticamente" value="<%=c.getFolio()%>"  id="cFolio" name="cFolio" />
					      		</div>
					    	</div>
					    	<div class="form-group col-6">
								<div class="input-group">
								  	<span class="input-group-text">CONTRATO SAI:</span>
									<input type="text" aria-label="Contrato SAI" class="form-control input-group-sm " placeholder="Favor de seleccionar el n&uacute;mero de contrato SAI"   onkeypress="return notWritte();" 
									onkeydown="no_backspaces(event);" id="pedidoContratoCompromiso" name="pedidoContratoCompromiso" >
									<input type="button" id="btnShowModal" value="Seleccionar Contrato" class="btn btn-primary" onclick="searchContract();"/>
								</div>
							</div>
						</div>
						<div class="form-group row">
							<div class="form-group  col-4">
								<div class="input-group">
						      		<span class="input-group-text input-group-sm">CONTRATO CNET:</span>
						      		<input type="text" class=" form-control input-group-sm font-weight-bold" disabled title="Número de Contrato CNET" value=""  id="cNumCNET" name="cNumCNET" />
					      		</div>
					    	</div>
					    	<div class="form-group  col-6">
								<div class="input-group">
						      		<span class="input-group-text input-group-sm">PROVEEDOR:</span>
						      		<input type="text" class=" form-control input-group-sm font-weight-bold" disabled title="Razón Social" value=""  id="cProveedor" name="cProveedor" />
					      		</div>
							</div>
						</div>
						<div class="form-group row">
	 						<div class="form-group col-4">
		 						<label for="cPeriodo">PER&Iacute;ODO EN EL QUE SE ORIGINAN LA PENAS O DEDUCCIONES</label>
								<select class="custom-select" id="cPeriodo" name="cPeriodo" >
								</select>
							</div> 
							<div class="form-group col-6">
		 						<label for="cPeriodo">N&Uacute;MERO DE OFICIO DE SOLICITUD DE VALIDACI&Oacute;N</label>
								<input type="text" class="form-control" placeholder="Favor de capturar el n&uacute;mero de oficio" aria-label="Favor de capturar el n&uacute;mero de oficio" 
								aria-describedby="basic-addon1"  name="cOficio" id="cOficio" maxlength="50" />
							</div>
	 					</div>
						<div class="form-group row">
							<div class="form-group col-10">
		 						<label for="cConcepto">CONCEPTO POR EL CUAL SE GENERAN LAS PENAS O DEDUCCIONES</label>
								 <textarea class="form-control" placeholder="Favor de capturar el concepto por el cual se generan las penas o deducciones" id="cConcepto" style="height: 100px" ></textarea>
							</div>
						</div>
						<div class="form-group row">
							<div  class="form-group col-10">
								<div class="form-floating" id="divObservaciones" style="display: none;">
								  <textarea class="form-control" placeholder="Comentarios" id="cObservations" name="cObservations" disabled="disabled"></textarea>
								  <label for="cObservations" id="floatingTextarea">  Observaciones del por qu&eacute; no es correcto el proceso de c&aacute;lculo.</label>
								</div>
							</div>
							
						</div>
						<div class="form-group">
							<div class="row">
								<div class="col-md-3">
									<div class="form-check form-check-inline">
									  	<input class="form-check-input" type="checkbox" id="checkPenaConv" value="0" onclick="catchOnclickPenalty();">
									  	<label class="form-check-label" for="checkPenaConv">Pena Convencional</label>
									</div>
								</div>
								<div class="col-md-3">
									<div class="form-check form-check-inline">
									  	<input class="form-check-input" type="checkbox" id="checkDeduccion" value="0" onclick="catchOnclickDeduction();">
									  	<label class="form-check-label" for="checkDeduccion">Deducci&oacute;n</label>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-3" id="divPenaltyAmount" style="display: none;">
			 						<label for="mAmountPenaltyTotal">Monto de Penalizaci&oacute;n Antes de IVA</label>
									<input type="text" class="form-control" placeholder="$ 0.00"  id="mAmountPenaltyTotal" name="mAmountPenaltyTotal" readonly="readonly">
								</div>
								<div class="form-group col-md-3" id="divDeductionAmount" style="display: none;">
			 						<label for="mAmountDeductionTotal">Monto de Deducc&oacute;n Antes de IVA</label>
									<input type="text" class="form-control" placeholder="$ 0.00"  id="mAmountDeductionTotal" name="mAmountDeductionTotal" readonly="readonly">
								</div>
							</div>
						</div>
						<div class="row" id="divAddRowItemPenalty">
							<div class="col-3">
								<label for="cPartidaPenalty">Partida de Contrato</label>
								<select class="custom-select" id="cPartidaPenalty" name="cPartidaPenalty" >
								</select>
							</div>
							<div class="col-2">
								<label for="btnAddRowPenalty" style="color: white;">"Agrega otro registro"</label>
								<input type="button" id="btnAddRowPenalty" name="btnAddRowPenalty" value="Agregar Registro" class="btn btn-primary" onclick="addRowOfTable(6);"
								title="Agrega un registro a la tabla de penalizaciones indicando la partida de contrato"/>
							</div>
						</div>
						<div class="form-group" id="divPenaltyService" style="display: none;">
							<div class="row">
								<div class="col">
									<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para la captura de penalizaciones</label>
									<table id="tblPenaltyService" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
										<thead >
											<tr>
												<th>Partida</th>
												<th>#</th>
							        			<th>Descripci&oacute;n</th>
							        			<th>Elementos</th>
							        			<th>(A)<br>Monto de los bienes<br> o servicios entregados o<br>prestados con atraso</th> 
							        			<th>(B)<br>Porcentaje de Penalidad <br> Diaria Establecida <br>en el contrato</th>
							        			<th>(C)=(A*B)<br>Penalidad diaria</th>
							        			<th>(D)<br>D&iacute;as con atraso</th>
							        			<th>(E)=(C*D)<br>Importe total <br>de la penalidad</th>
							        			<th>(F)<br>Porcentaje de la <br>Garantia de Cumplimiento</th>
							        			<th>(G)=(F/B)<br>D&iacute;as de penalizaci&oacute;n <br>de conformidad al principio<br> de proporcionalidad</th>
							        			<th>(H)=(G*C)<br>Importe de la <br>pena convencional</th>
											</tr>										
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="form-group dataTableGRM" id="divPenaltyProducts" style="display: none;">
							<div class="row">
								<div class="col">
									<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para la captura de penalizaciones</label>
									<table id="tblPenaltyProducts" class="table table-striped table-bordered dt-responsive nowrap dataTableGRM" style="width:100%">
										<thead class="dataTableGRM">
											<tr>
												<th>Partida</th>
												<th>#</th>
							        			<th>Descripci&oacute;n</th>
							        			<th>Fecha de entrega</th>
							        			<th>Piezas</th>
							        			<th>Monto del bien</th>
							        			<th>(A)<br>Monto de los bienes<br> o servicios entregados o<br>prestados con atraso</th> 
							        			<th>(B)<br>Porcentaje de Penalidad <br> Diaria Establecida <br>en el contrato</th>
							        			<th>(C)=(A*B)<br>Penalidad diaria</th>
							        			<th>(D)<br>D&iacute;as con atraso</th>
							        			<th>(E)=(C*D)<br>Importe total <br>de la penalidad</th>
							        			<th>(F)<br>Porcentaje de la <br>Garantia de Cumplimiento</th>
							        			<th>(G)=(F/B)<br>D&iacute;as de penalizaci&oacute;n <br>de conformidad al principio<br> de proporcionalidad</th>
							        			<th>(H)=(G*C)<br>Importe de la <br>pena convencional</th>
											</tr>										
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="row" id="divAddRowItemDeduction">
							<div class="col-2">
								<label for="cPartidaDeduction">Partida de Contrato</label>
								<select class="custom-select" id="cPartidaDeduction" name="cPartidaDeduction" >
								</select>
							</div>
							<div class="col-2">
								<label for="btnAddRowDeduction" style="color: white;">"Agrega otro registro"</label>
								<input type="button" id="btnAddRowDeduction" name="btnAddRowDeduction" value="Agregar Registro" class="btn btn-primary" onclick="addRowOfTable(7);" 
								title="Agrega un registro a la tabla de deducciones indicando la partida de contrato"/>
							</div>
						</div>
						<div class="form-group" id="divDeductionService" style="display: none;">
							<div class="row">
								<div class="col">
									<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para la captura de deducciones</label>
									<table id="tblDeductionService" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
										<thead >
											<tr>
												<th>Partida</th>
												<th>#</th>
							        			<th>Descripci&oacute;n</th>
							        			<th>Elementos</th>
							        			<th>(A)<br>Monto de los bienes<br> o servicios entregados o<br>prestados con atraso</th> 
							        			<th>(B)<br>Porcentaje de Penalidad <br> Diaria Establecida <br>en el contrato</th>
							        			<th>(C)=(A*B)<br>Deducci&oacute;n diaria</th>
							        			<th>(D)<br>D&iacute;as con deficiencia</th>
							        			<th>(E)=(C*D)<br>Importe total <br>de la deducci&oacute;n</th>
							        			<th>(F)<br>Porcentaje de la <br>Garantia de Cumplimiento</th>
							        			<th>(G)=(F/B)<br>Días de deducci&oacute;n <br>de conformidad al principio<br> de proporcionalidad</th>
							        			<th>(H)=(G*C)<br>Importe de la <br>deducci&oacute;n</th>
											</tr>										
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="form-group" id="divDeductionProducts" style="display: none;">
							<div class="row">
								<div class="col">
									<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para la captura de deducciones</label>
									<table id="tblDeductionProducts" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
										<thead >
											<tr>
												<th>Partida</th>
												<th>#</th>
							        			<th>Descripci&oacute;n</th>
							        			<th>Fecha de entrega</th>
							        			<th>Piezas</th>
							        			<th>Monto del bien</th>
							        			<th>(A)<br>Monto de los bienes<br> o servicios entregados o<br>prestados con atraso</th> 
							        			<th>(B)<br>Porcentaje de Penalidad <br> Diaria Establecida <br>en el contrato</th>
							        			<th>(C)=(A*B)<br>Deducci&oacute;n diaria</th>
							        			<th>(D)<br>D&iacute;as con deficiencia</th>
							        			<th>(E)=(C*D)<br>Importe total <br>de la deducci&oacute;n</th>
							        			<th>(F)<br>Porcentaje de la <br>Garantia de Cumplimiento</th>
							        			<th>(G)=(F/B)<br>Días de deducci&oacute;n <br>de conformidad al principio<br> de proporcionalidad</th>
							        			<th>(H)=(G*C)<br>Importe de la <br>deducci&oacute;n</th>
											</tr>										
										</thead>
									</table>
								</div>
							</div>
						</div>
						<!-- Modal -->
						<div class="modal fade bd-example-modal-lg" id="modalContracts" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
						  <div class="modal-dialog modal-lg">
						    <div class="modal-content">
						      <div class="modal-header">
						        <h5 class="modal-title" id="exampleModalLabel">Contratos Autorizados</h5>
						        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
						      </div>
						      <div class="modal-body">
						        	<div class="form-group row" >
					        			<div class="col-6 col-sm-4">
    										<label for="cIdDefinitivo" class="form-label">Contrato SAI</label>
											<input type="text" class="form-control" id="cIdDefinitivo" name="cIdDefinitivo" placeholder="Ejemplo CV-A04-1/2022">
					        			</div>
										<div class="col-3 col-sm-4">
    										<label for="cIdDefinitivo" class="form-label">Proveedor</label>
											<input type="text" class="form-control" id="cRazonSocial" name="cRazonSocial" placeholder="Raz&oacute;n Social">
					        			</div>
					        			<div class="col-3 col-sm-4">
    										<label for="cIdDefinitivo" class="form-label">Contrato CNET</label>
											<input type="text" class="form-control" id="cNumCNET" name="cNumCNET" placeholder="Ejemplo CNF-016RHQ001-E00">
					        			</div>
						        	</div>
						        	<div class="form-group row" >
						        		<div class="col-3 col-sm-4">
    										<input type="button" id="btnSearchContract" value="Buscar" class="btn btn-primary" onclick="queryTableContracts();"/>
					        			</div>
						        	</div>
						        	<div class="form-group">
										<div class="row">
											<div class="col">
												<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
													<thead >
														<tr>
															<th align="center">Contrato SAI</th>
															<th align="center">RFC</th>
															<th align="center">Contrato CNET</th>
															<th align="center">Proveedor </th>
															<th align="center">Concepto </th>
															<th align="center">Tipo </th>
														</tr>										
													</thead>
												</table>
											</div>
										</div>
									</div>
						      </div>
						    </div>
						  </div>
						</div>
					</fieldset>
				</div>
			</div>
			<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="CV" />
			<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=ue%>" />
			<input type="hidden" name="lEsPenaConvenvional" id="lEsPenaConvenvional" value="0" />
			<input type="hidden" name="lEsDeduccion" id="lEsDeduccion" value="0" />
			<input type="hidden" name="nIdEstate" id="nIdEstate" value="1" />
			<input type="hidden" name="nIdPenaltyDeduction" id="nIdPenaltyDeduction" value="-1" />
			<input type="hidden" name="cCaptureUsser" id="cCaptureUsser" value="" />
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO" value="<%=today%>" />
			<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=today.substring(6)%>" />
			<input type="hidden" name="cDocumentHAplicado" id="cDocumentHAplicado" value="I" />
		</form>
	</body>
</html>