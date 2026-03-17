<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   
    <title>AMF Cierre Presupuestal</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
	</style>
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
<script type="text/javascript">
$(document).ready(function(){

	$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
	$("#btnProcesar").click(function (){ enviar(); });
	$("#btnImprimir").click(function () { cmdImprimir(); });
	$("#btnExportar").click(function () { exportarDoc(); });
	$("#chckImprimir").click(function () { imprimeVarias() ;})
	//$("#btnImprimir").attr("disabled","disabled");
	$("#btnNuevo").hide();
	$("#btnNuevo").click(function () { location.reload(); } );
	
	querySelectPost("tEjercicioRead", "aEjercicioFiscal", {async: false });
	querySelectPost("UnidadresponsableRead","cUnidadResponsable", {async: false });
	
	$("#fechaCaptura").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",	buttonImage : "images/calendar.gif", buttonImageOnly : true	});
	$("#fechaPago").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
	
	$("#de").hide();
	$("#hasta").hide();
	$("#deI").hide();
	$("#hastaI").hide();
	var importe = 0;
	pParam = "<%=ur%>"; 
	var tablaCierre = $('#dataCierrePresupuestales').dataTable({         
							bRetrive: false,
							bPaginate: true,
							bDestroy: true,
							bLengthChange: true,
							iDisplayLength: 100,
		        			bFilter: true,
		        			bSort: true,
		        			bInfo: true,
		        			bAutoWidth: false,
		        			oLanguage: {
								sProcessing: "Procesando...",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
								sLoadingRecords: "Cargando...",
								sInfo: "Registros _START_ al _END_ de _TOTAL_",
								sInfoEmpty: "Registro 0 al 0 de 0",
								sInfoFiltered: "(filtered from _MAX_ total entries)",
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vMultilistadorPagosAMFCierre&qw=" + (importe == 0 ? " " : " mImporteNetoNum <= "+importe+ " AND " ) + " Estatus != 'Cancelado' AND caNoContrarrecibo != '0' " +(pParam == 'RHQ' ? " " : " AND cUnidadResponsable = '"+pParam+"'"),
							bProcessing: true,
							sPaginationType: "full_numbers",
							bJQueryUI: true,
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "fAplicacion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "mImporteNeto",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "mImporteTotal",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"}
								
							]
		        		});	
	
});
function tablaCierre(importe,pParam)
{
	if(importe == "" || importe < 0){ importe = 0; }
			   $('#dataCierrePresupuestales').dataTable({         
							
							bRetrive: false,
							bPaginate: true,
							bDestroy: true,
							bLengthChange: false,
							iDisplayLength: 100,
		        			bFilter: true,
		        			bSort: true,
		        			bInfo: false,
		        			bAutoWidth: false,
		        			oLanguage: {
								sProcessing: "Procesando...",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
								sLoadingRecords: "Cargando...",
								sInfo: "Registros _START_ al _END_ de _TOTAL_",
								sInfoEmpty: "Registro 0 al 0 de 0",
								sInfoFiltered: "(filtered from _MAX_ total entries)",
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vMultilistadorPagosAMFCierre&qw=" + (importe == 0 ? " " : " mImporteNetoNum <= "+importe+ " AND " ) + " Estatus != 'Cancelado' AND caNoContrarrecibo != '0' " +(pParam == 'RHQ' ? " " : " AND cUnidadResponsable = '"+pParam+"'"),
							bProcessing: true,
							sPaginationType: "full_numbers",
							bJQueryUI: true,
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "fAplicacion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "mImporteNeto",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "mImporteTotal",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"}
								
							]
		        		});	
}
function enviar()
{
			var vacio = true;
 			//try{
 				var table = document.getElementById('dataCierrePresupuestales');
 				var aTrs = $('#dataCierrePresupuestales').dataTable().fnGetNodes();
 				
 				if($("#FacturaAcuerdosAdministracion").val() == ""){
					alert("Agregar Clave AMF");
					return;
				}else if($("#rfcAMF").val() == ""){
					alert("Agregar RFC AMF");
					return;
				}else if($("#fechaCaptura").val() == ""){
					alert("Seleccionar Fecha de Captura");
					return;
				}else if($("#fechaPago").val() == ""){
					alert("Seleccionar Fecha de Pago");
					return;
				}else{
					var fC = $("#fechaCaptura").val().split("/");
					var fCaptura = fC[2]+"-"+fC[1]+"-"+fC[0];
					
					var fP = $("#fechaPago").val().split("/");
					var fPago = fP[2]+"-"+fP[1]+"-"+fP[0];
					
					var numFolioAMF = $("#numFolioAMF").val();
					var nClaveAMF = $("#FacturaAcuerdosAdministracion").val();
 					var aEjercicioFiscal = $("#aEjercicioFiscal").val();
 					var cCentroContable = $("#cCentroContable").val();
 					var u_login = $("#u_login").val();
 					var estatus = $("#estatus").val();
					var importeNeto = 0;
 					
					var numPagoAMF = "";
 					var caNoContrarrecibo = "";
 					var rfcAMF = "";
 					var tipoDocumento = "";
 					var importeNetoCxp = "";
 					var entra = 0;
					var cUnidadResponsable = $("#cUnidadResponsable").val();
 					
					for(var i=1; i<=aTrs.length;  i++)     
					{   
	 					var row = table.rows[i];
	 					var chkbox = row.cells[0].childNodes[0];
	 					if(null != chkbox && true == chkbox.checked)
						{
	 						getNextSequenceVal({seqName: "AM" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
	 						
	 						importeNetoIndiv = row.cells[6].childNodes[0].toString();
	 						importeNeto = Number(importeNeto) + Number(quitaFmt(importeNetoIndiv)); 
							
	 						numPagoAMF += $("#numPagoAMF").val()+"/";
	 						tipoDocumento += row.cells[1].childNodes[0].toString()+"/";
	 						caNoContrarrecibo += row.cells[2].childNodes[0].toString()+"/";
	 						rfcAMF += row.cells[3].childNodes[0].toString()+"/";
	 						//alert(numPagoAMF);
	 						$("#numPagoAMFImprimir").val(numPagoAMF);
	 						entra = 1;
	 					} 
					}
					if(entra == 0){
						alert("Seleccione un Documento");
						return;
					}
					var importeSaldo = quitaFmt($("#mSaldo").val());
					
					if(importeNeto > importeSaldo){
						alert("El Importe Es Mayor al Saldo");
						$("#numPagoAMF").val("");
						$("#numPagoAMFImprimir").val("");
					}else{
						if(confirm("Esta Seguro De Realizar El Pago")){
							
							$.ajax({
									url: './cierrePresupuestal.jsp',
									type: 'post',
									dataType: 'json',
									data: {tipo:'guardarPagoAMF',tipoDocumento:tipoDocumento,numPagoAMF:numPagoAMF,numFolioAMF:numFolioAMF,nClaveAMF:nClaveAMF,rfcAMF:rfcAMF,aEjercicioFiscal:aEjercicioFiscal,fCaptura:fCaptura,fPago:fPago,caNoContrarrecibo:caNoContrarrecibo,cUnidadResponsable:cUnidadResponsable},
									success:function(data){
										
										if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
										}else if(data.estatus == "guardado"){
											alert("Se Realizo El Pago Correctamente: "+numPagoAMF );
											//$("#btnImprimir").removeAttr("disabled");
											$("#btnProcesar").hide();
											$("#btnNuevo").show();
										}else if(data.estatus == "error"){
											alert("No Se Realizo el Pago");
										}else{
											alert("");
										}
										//location.reload();
									}
							});
							
						}else{
							alert("No Se Realizo el Pago");
						}					
					}
        		}
 			/*}catch(e) {
         		alert("Error de comunicacion:" +e);
    		}*/
}
function quitaFmt( val ) 
{
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
}
function setSequenceVal(seqValue) 
{
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "<%=cCentroContable%>" + "AM" + $("#aEjercicioFiscal").val() + seqValue;
		$("#numPagoAMF").val( seqValue );
}
function cmdImprimir(){
		
		var parametro = "";
		var campos = "";
		var datos = "";
		var order = ""; 
		var cTabla = "TPAGOAMFCIERRE";
		var numPagoAMFImprimir = "";
		var total = 0;
		
		if($("#chckImprimir").is(":checked")){
			var arrChck = new Array();
						
			var deI = $("#deT").val().substring(0,8);        //10AM2012
			var hastaI = $("#hastaT").val().substring(0,8);  //10AM2012
			
			var deF = $("#deT").val().substring(8,14);        //000001
			var hastaF = $("#hastaT").val().substring(8,14);  //000005
			
			if(deI == hastaI){
				if(deF <= hastaF){
					var count = Number(hastaF) - Number(deF);
					deFnuevo = deF;
					
					for(var i = 0; i <= count; i++){
							
							numPagoAMFImprimir = " numPagoAMF = '"+deI+deFnuevo+"'";
							//alert(numPagoAMFImprimir);
							$.getJSON("../catalogos/SelectJson.jsp",{Tabla: cTabla, Campos:numPagoAMFImprimir, Param: parametro, MaxReg: "10", Order:order, ajax: 'true'}, function(j){
								
								for (var ii = 0; ii < j.length; ii++) {
									
											window.open(	"../admin/SeguridadCatalogos?"
														+ "catalogo=CONTRARECIBO"
														+ "&accion=run"
														+ "&rn=PolizaAcuerdosAMFCierre.jasper"
														+ "&NumPagoAMF=" + j[ii].Col0,
														"popacuse",
														"scrollbars=1, resizable=yes, width=1024, height=768"
												   );
								}		
							});
						
						//deFnuevow = parseFloat('.'+deF) + parseFloat(0.000001);
						total = Number(deF) + Number(1);
						deF = total.toString();
						
						if(deF.length == 1){
	   						deFnuevo = "00000"+deF;
	   						
						}else if(deF.length == 2){
							deFnuevo = "0000"+deF;
							
						}else if(deF.length == 3){
							deFnuevo = "000"+deF;
							
						}else if(deF.length == 4){
							deFnuevo = "00"+deF;
							
						}else if(deF.length == 5){
							deFnuevo = "0"+deF;
							
						}
					}
				}else{
					alert("No Se Pueden Imprimir Porque La Informacion Es Incorrecta");
					return;
				}
				
			}else{
				alert("No Se Puede Imprimir Porque son de Diferentes Centros Contables ");
			}
		}else{
				var arr = new Array() 
				var amf = $("#numPagoAMFImprimir").val();
				var arr = amf.split("/");
				var n = arr.length - 1;
				
							
				for(var a = 0; a < n; a++){
					datos = "sinDatos";
					campos = " numPagoAMF = '"+arr[a]+"'";
					
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla:cTabla, Campos:campos, Param:parametro, MaxReg: "10", Order:order, ajax:'true'}, function(j){
							
								for (var i = 0; i < j.length; i++) {
									 	//datos = "datos";
										window.open(
										"../admin/SeguridadCatalogos?"
												+ "catalogo=CONTRARECIBO"
												+ "&accion=run"
												+ "&rn=PolizaAcuerdosAMFCierre.jasper"
												+ "&NumPagoAMF=" + j[i].Col0,
												//+ "&nombre="   + ""
												//+ "&cargo="    + ""
												//+ "&area="     + "",
												"popacuse",
												"scrollbars=1, resizable=yes, width=1024, height=768"
										);
								}
							
							});
				}
		}	

			
}
function exportarDoc()
{
		$("#numRecibos").val("");
		
		var table = document.getElementById('dataCierrePresupuestales');
 		var aTrs = $('#dataCierrePresupuestales').dataTable().fnGetNodes();
		var caNoContrarrecibo = "";
		
		if(aTrs.length > 0){
	 		for(var i=1; i<=aTrs.length;  i++){
	 			var row = table.rows[i];
	 			caNoContrarrecibo += "'"+row.cells[2].childNodes[0].toString()+"'";
	 			caNoContrarrecibo += ",";
	 		}
 			$("#numRecibos").val(caNoContrarrecibo);
	    	document.formCierre.submit();
		}else{
			alert("No Existe Ninguna Consulta");
			return;
		}
	 	//  $.ajax({ url: './cierrePresupuestal.jsp',type: 'post',data: {tipo:'exportarDoc',caNoContrarrecibo:caNoContrarrecibo} });
}
function imprimeVarias()
{
	var checkImprime = $("#chckImprimir").is(":checked");
	if(checkImprime){
		$(".ocultar").show();
	}else{
		$(".ocultar").hide();
	}	
}
function onlyNumbers(evt) 
{
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 47) { return false; }
		return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}
</script>
</head>
   <body id="dt_example">
   		<form id="formCierre" name="formCierre" action="../gstnmngr/generaCierrePresupuestalAMF" method="post" >
		   	<div id="container" >
		   		<h1>Cierre Presupuestal AMF<label style="font-size: 8pt"></label></h1>		
		   			<div id="tabsl">
		   				<table id="tblCierrePresupuestal" align="center" width="1000px">	
		   					<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=ur%>" />
		   					<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>">
		   					<input type="hidden" id="u_login" name="u_login" value="<%=usuLogin%>" />
		   					<input type="hidden" id="numPagoAMF" name="numPagoAMF" />
		   					<input type="hidden" id="numPagoAMFImprimir" name="numPagoAMFImprimir" />
		   					<select style="visibility: hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"></select>
		   					<input type="hidden" id="numRecibos" name="numRecibos" />
		   					<tr>
		   						<td>
		   							<fieldset>
		   								<table id="tblAMF" align="center" width="1250px" height="110px">
		   									<tr>
		   										<tr><td>Clave AMF</td><td><input type="text" id="FacturaAcuerdosAdministracion" name="FacturaAcuerdosAdministracion" class="AyudaSyC" readonly="readonly" onchange="tablaCierre($('#importeBuscar').val(),$('#cUnidadResponsable').val())" /></td><td>Saldo</td><td><input type="text" id="mSaldo" readonly='readonly'></td><td><input type="button" id="btnProcesar" name="btnProcesar" value="Procesar" ></td></tr>
											  	<tr><td>Numero Folio AMF</td><td><input type="text" id="numFolioAMF" name="numFolioAMF" readonly='readonly'></td><td>Fecha Vigencia</td><td><input type="text" id="fVigencia" name="fVegencia" readonly='readonly'></td><td><input type="button" value="Exportar" name="btnExportar" id="btnExportar" /></td></tr>
												<tr><td>Fecha Captura</td><td><input type="text" id="fechaCaptura" name="fechaCaptura" ></td><td>Fecha Pago</td><td><input type="text" id="fechaPago" name="fechaPago"></td><td><input type="button" id="btnImprimir" name="btnImprimir" value="Imprimir" ></td><td>Imprimir Varias <input type="checkbox" name="chckImprimir" id="chckImprimir"></td></tr>
												<tr><td></td><td></td><td></td><td></td><td id="de" class="ocultar">De</td><td id="deI" class="ocultar"><input type="text" name="deT" id="deT" /></td></tr>
												<tr><td></td><td></td><td></td><td></td><td id="hasta" class="ocultar">Hasta</td><td id="hastaI" class="ocultar"><input type="text" name="hastaT" id="hastaT" /></td></tr>
												<tr><td></td><td></td><td></td><td></td><td><input type="button" name="btnNuevo" id="btnNuevo" value="Nuevo" /></td>
											</tr>
		   								</table>
		   							</fieldset>
		   						</td>
		   					</tr>
		   					<tr>
								<td>
		   							<!--  fieldset>
										<table id="tblFiltro" align="center" width="1000px">
											<tr>
												<td align="center">TIPO DE PAGO</td><td><select id="tipoDoc" name="tipoDoc" onchange="cambiaDoc(this.value)">
														<option value="seleccione">Seleccione</option>
														<option value="Directo">Pago Directo</option>
														<option value="Federalizado">Federalizado</option>
														<option value="Obra">Pago de Obra</option>
													</select>
												</td>
												<td>Cuenta Por Pagar</td><td><input type="text" name="contrarrecibo" id="contrarrecibo" /></td><td></td>
												<td>RFC</td><td><input type="text" name="rfc" id="rfc"></td>
												<td></td>
											</tr>	
										</table>
									</fieldset -->	
		   						</td>
		   					</tr>
		   					<tr><td>&nbsp;</td></tr>	
		   					<tr>
		   						<td>
		   							<fieldset>
		   								<legend>Cuenta Por Pagar</legend>
		   									<table align="right">
		   										<td>Buscar Por Importe Neto Menor o Igual a</td><td><input type="text" name="importeBuscar" id="importeBuscar" onKeyPress="return(onlyNumbers(event))" /></td><td><input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" onclick="tablaCierre($('#importeBuscar').val(),$('#cUnidadResponsable').val())"></td>
		   									</table>
				   							<table id="dataCierrePresupuestales" name="dataCierrePresupuestales" class="display" align="center" width="1250px" >
				   								<tbody>
				   									<thead>
				   										<th>Seleccionar</th>
				   										<th>Tipo Documento</th>
														<th>caNoContrarrecibo</th>	
														<th>RFC</th>
														<th>Beneficiario</th>
														<th>Fecha Aplicacion</th>
														<th>Importe Neto</th>	
														<th>Importe Ejercer</th>
				   									</thead>
				   								</tbody>
		   									</table>
		   							</fieldset>
		   						</td>
		   					</tr>
		   					<tr><td>&nbsp;</td></tr>
		   					<tr><td>&nbsp;</td></tr>	
		   				</table>
		   				
		   			</div>
		   	</div>
		</form>
  </body>
</html>
