<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab1.getLogin();
	String role="";
	String roles="";
	Map rol =usuarioTab1.getRoles();
	String cIdContratoDefinitivo = "";
	String nConsecutivoMod="0";
	String nIdContModCap4="0";
	//System.out.println("***********"+request.getParameter("cIdContratoDefinitivo") +"*****"+request.getParameter("nConsecutivoMod")+"****"+request.getParameter("nIdContModCap4"));
	if (request.getParameter("cIdContratoDefinitivo") != null && request.getParameter("nConsecutivoMod") != null) {
		cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
		nConsecutivoMod=request.getParameter("nConsecutivoMod") ;
		nIdContModCap4=request.getParameter("nIdContModCap4") ;
		session.setAttribute(GestionInterface.ATT_ContractConvCap4Definitivo,cIdContratoDefinitivo.toString(  ));
		session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo,request.getParameter("nConsecutivoMod").toString(  ) );
		session.setAttribute(GestionInterface.ATT_nIdContModCap4,request.getParameter("nIdContModCap4").toString(  ) );
	} else {
		cIdContratoDefinitivo = (String) session.getAttribute(GestionInterface.ATT_ContractConvCap4Definitivo);
		nConsecutivoMod=(String) session.getAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo);
		nIdContModCap4=(String) session.getAttribute(GestionInterface.ATT_nIdContModCap4);
	}
	//System.out.println("***********"+cIdContratoDefinitivo +"*****"+nConsecutivoMod+"****"+nIdContModCap4);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<script type="text/javascript" charset="utf-8">
		tabb=2;
		var oTableItems="";
		var myModal;
		$(document).ready(function() {
			showAndHideTabs();
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"ConvenioModificatorioCap4","caratulaConvCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				
			%>
			myModal = new bootstrap.Modal(document.getElementById('modalitemsConvCap4'), {
			  keyboard: false
			})
			$("#cIdContratoDefinitivo").val("<%= cIdContratoDefinitivo %>");
			$("#nConsecutivoMod").val("<%= nConsecutivoMod %>");
			$("#nIdContModCap4").val("<%= nIdContModCap4 %>");
			
			infoQuery();
			searchItemsContract();
			hideAndShowButtonDelete();
			//myModal.hide();
			$('#tblPartidasMods').on('dblclick', 'tr',function(){
				$(oTableItems.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				if($("#nEstatus").val()>1){
					swal("El estatus del convenio no permite la modificación, para modificar el monto, el convenio debe de estar en estatus de captura.",{icon:"info",button: "Cerrar"});
					return;
				}
				var anSelected = fnGetSelected( oTableItems );
				var aData = oTableItems.fnGetData(anSelected[0]);
				$("#nIdLineaConsolidado").val(aData[0]);
				$("#nCantidad").val(aData[5]);
				$("#nPorcentajeIVA").val(aData[7]);
				$("#mPrecioUnitario").val(aData[6].replace(",",""));//aData[7]
				$("#mMontoOriginal").val(aData[10]);
				$("#mMontoNeto").val(aData[8].replace(",",""));
				$("#nPorcentajeMod").val(aData[9]);
				enabledAndDisabledItems(aData[3]);
				myModal.show();
				
			});
			
		});
		
	</script>
</head>
<body>
	<form id="formCaratulaConveCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Car&aacute;tula Convenios Cap 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceCancelar ui-button ui-corner-all float-right" 	id="imgEliminar" name="imgEliminar" 	value="Eliminar"	onclick="deleteModificatorio();" />
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoModificado" id="lblTotalContratoModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalPorcentajeMod" id="lblTotalPorcentajeMod" readonly />
					</div>
				</div>
				
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trPartidasMod">
			<legend class="w-auto px-2"> PARTIDAS MODIFICADAS </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<h6>***Dar doble clic en la tabla para modificar el monto</h6>
						<div class="col">
							<table id="tblPartidasMods" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th >L&iacute;nea </th>
										<th >Partida</th>
										<th >CUCOP</th>
										<th >Unidad <br/>de Medida</th>
										<th >Descripci&oacute;n Adicional</th>
										<th >Cantidad</th>
										<th >Precio Unitario</th>
										<th >IVA</th>
										<th >Monto Neto</th>
										<th >% MOD</th>
										<th style="display: none;">Monto Original</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			
		</fieldset>
		<!-- Modal -->
		<div class="modal fade bd-example-modal-lg" id="modalitemsConvCap4" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="exampleModalLabel">Modificaci&oacute;n de Monto y/o cantidad</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-body">
		        	<div class="form-group row" >
	        			<div class="col-sm-1">
							<label for="nIdLineaConsolidado" class="form-label">L&iacute;nea</label>
							<input type="text" class="form-control" id="nIdLineaConsolidado" name="nIdLineaConsolidado" disabled>
	        			</div>
	        			<div class="col-sm-2">
							<label for="nCantidad" class="form-label">Cantidad</label>
							<input type="text" class="form-control" id="nCantidad" name="nCantidad" disabled onchange="calculaMontoNeto();" onkeypress="return onlyIntegers(event);">
	        			</div>
	        			
	        			<div class="col-sm-2">
							<label for="mPrecioUnitario" class="form-label">Precio Unitario</label>
							<input type="text" class="form-control" id="mPrecioUnitario" name="mPrecioUnitario" disabled>
	        			</div>
	        			<div class="col-sm-1">
							<label for="nPorcentajeIVA" class="form-label">IVA</label>
							<input type="text" class="form-control" id="nPorcentajeIVA" name="nPorcentajeIVA" disabled>
	        			</div>
	        			<div class="col-sm-2">
							<label for="nPorcentajeMod" class="form-label">Porcentaje</label>
							<input type="text" class="form-control" id="nPorcentajeMod" name="nPorcentajeMod" disabled>
	        			</div>
	        			<div class="col-sm-2" style="display: none;">
							<label for="mMontoOriginal" class="form-label">Monto Original</label>
							<input type="text" class="form-control" id="mMontoOriginal" name="mMontoOriginal" disabled>
	        			</div>
	        			<div class="col-sm-2">
							<label for="mMontoNeto" class="form-label">Monto Con IVA</label>
							<input type="text" class="form-control" id="mMontoNeto" name="mMontoNeto" placeholder="0.00" onkeypress="return onlyDoubles(event);" onchange="calulaprecioUnitario();" disabled>
	        			</div>
	        			<div class="col-sm-2" id="divBtnGuardar">
	        				<label for="btnSearchContract" class="form-label" style="color: white;">Guardar</label>
							<input type="button" id="btnSearchContract" value="Guardar" class="btn btn-primary" onclick="saveItem();"/>
	        			</div>
		        	</div>
		        	
		      </div>
		    </div>
		  </div>
		</div>
		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%= cIdContratoDefinitivo %>" />
  		<input type="hidden" name="nConsecutivoMod" id="nConsecutivoMod" value="<%= nConsecutivoMod %>" />
  		<input type="hidden" name="nIdContModCap4" id="nIdContModCap4" value="<%= nIdContModCap4 %>" />
  		<input type="hidden" name="tipoOperacion" id="tipoOperacion" value="4" />
  		<input type="hidden" name="tipoProceso" id="tipoProceso" value="3" />
  		<input type="hidden" name="nEstatus" id="nEstatus" value="-1" />
	</form>
</body>
</html>