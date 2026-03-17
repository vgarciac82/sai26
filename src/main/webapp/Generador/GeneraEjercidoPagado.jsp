<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}
String usu = usuario.getLogin();
String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Ejercido Pagado</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
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

	
<script type="text/javascript">
$(document).ready(function () {
	
	//$('#demo9').click(function() {  $.blockUI();  $('.blockOverlay').attr('title','Click to unblock').click($.unblockUI);  }); 
	$("#btnValidar").click(function (){ validarDatos(); });
	$("#btnAplicar").click(function () { aplicar(); });
	$("#btnAplicar").attr("disabled","disabled");
	$("#btnNuevo").click(function () {location.reload();});
	$("#tblText").hide();
	$("#tdRelacionGastosIntegracion").hide();
	$("#tdRelacionGastosDiferentes").hide();
	$("#selectRelacion").hide();
	if($("#usuario").val() == "admin" ){
		$("#tblAplicacion").show();
	}else{
		$("#tblAplicacion").hide();	
	}
	
	// dataTable CXP Enc	
	var TableMovCXP = $('#movimientoCXP').dataTable({         
			//iDisplayLength: 20,
			bSortClasses: false,
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: false,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: true,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			bScrollCollapse: true,
			//sScrollXInner: "100%",
			aaSorting: [[ 1, "asc" ]] ,
			bRetrive: true,
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
				aoColumnDefs:[{"bVisible":false, "aTargets":[1]},
								{"aTargets":[6], "sClass":"right"},
								{"sClass": "dt-center", "aTargets": ['dt-center']}
				
					//"width":"20px"
					//"sWidth":"20px"
				]
		});
		//Dar click en Linea ejecute mostrar sus detalles
		$("#movimientoCXP tbody").click(function(event) {
			
			var aPos = TableMovCXP.fnGetPosition( event.target.parentNode );
     		var aData = TableMovCXP.fnGetData( aPos );
			if($("#tipoDoc").val() == "r_gastos" && $("#selectRelacion").val() == "integracion"){
				$("#flcxp").val($("#relacionGastosIntegracion").val());
				$("#selectRelacion").attr('disabled','disabled');
			}else if($("#tipoDoc").val() == "r_gastos" && $("#selectRelacion").val() == "diferente"){
				if(confirm("Agregar Folio "+$("#relacionGastosDiferentes").val()+" Para Comparar")){
					var folios = $("#flcxp").val()+"/"+aData[0];
					$("#flcxp").val(folios);
				}
				
				$("#selectRelacion").attr('disabled','disabled');
			}else{
				$("#flcxp").val(aData[0]);
			}
     		
			muestraDatosCxpDetalles(aData[1]);
		});
		//dataTable Detalles CXP
		$('#movimientoCXPDetalles').dataTable({         
			iDisplayLength: 20,
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: true,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: false,
        	bInfo: false,
        	bAutoWidth: false,
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
			aoColumnDefs:[{"bVisible":false, "aTargets":[4]}]	
		});
		// dataTable SICOP		
		var TableMovSICOP = $('#movimientoSicop').dataTable({         
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: true,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: false,
        	bInfo: false,
        	bAutoWidth: true,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			aaSorting: [[ 1, "asc" ]] ,
			sPaginationType: "full_numbers",
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
				}
			
		});
		//Dar click en Linea ejecute mostrar sus detalles
		$("#movimientoSicop tbody").click(function(event) {
			var aPos = TableMovSICOP.fnGetPosition( event.target.parentNode );
     		var aData = TableMovSICOP.fnGetData( aPos );
			$("#flsicop").val(aData[0]);
			muestraDatosSicopDetalles(aData[0]);
		});
		//dataTable Detalles SICOP
		$('#movimientoSICOPDetalles').dataTable({         
			iDisplayLength: 20,
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: true,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: false,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			aaSorting: [[ 1, "asc" ]] ,
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
			aoColumnDefs:[{"bVisible":false, "aTargets":[3]}]
		});
});		
												
	function muestraDatosCxp(){
			
			$("#tblText").val("");
			$("#tblText").hide();
			$('#movimientoCXPDetalles').dataTable().fnClearTable();
			var tipoDoc = $("#tipoDoc").val();
			var idCXP =  $("#bDatosCXP").val();
			var campos = "";
			var elParametro = "";	
			var szTabla = "";
			if((tipoDoc == "seleccione" || idCXP == "") && tipoDoc != "r_gastos"){
				$("#flcxp").val("");
				$("#flsicop").val("");
				$('#movimientoCXP').dataTable().fnClearTable();
				//$('#movimientoCXPDetalles').dataTable().fnClearTable();
				alert("Error. Obligatorio El Tipo de Documento y Folio Cuenta Por Pagar.");
				return;
			}else if(tipoDoc == "r_gastos" && $("#selectRelacion").val() != "seleccione" && ($("#tdRelacionGastosIntegracion").val() != "" || $("#tdRelacionGastosDiferentes").val() || $("#tdCXP").val() )){
				alert("Error. Falta Informacion Para Hacer Busqueda Relacion de Gastos");
				return;
			}else{
				//getNextSequenceVal({seqName: "EJERCIDO" , async: false, callback: setSequenceValE});
				//getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
				$("#flcxp").val("");
				//$('#movimientoCXP').dataTable().fnClearTable();
				var datos = "sinDatos";
				if(tipoDoc == "p_directo"){
					$('#movimientoCXP').dataTable().fnClearTable();
					campos = "caNoContrarrecibo,nFolioPagoDirecto,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1),CONVERT(varchar,CONVERT(money,mImporteIVA),1),isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.040') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1),nFolioPolizaCancelacion";
					szTabla = "tPagoDirectoEncabezado";
					elParametro = "caNoContrarrecibo = '"+idCXP+"'";
				}
				if(tipoDoc == "r_gastos"){
										
						if($("#relacionGastosIntegracion").val() != ""){
							$('#movimientoCXP').dataTable().fnClearTable();
							var idIntegracion = $("#relacionGastosIntegracion").val();
							campos = " l.sNoContrarrecibo, l.nFolio,l.sUnidadResponsable,e.caNoContrarrecibo,l.fAplicacion,l.sRamo ";
							elParametro = "sAuxiliarComodin = '"+$("#relacionGastosIntegracion").val()+"'";
							szTabla = "tLayoutsCreadosRelacionGastosHeader as l INNER JOIN tEjercidoEncabezado as e ON l.sNoContrarrecibo = e.caNoContrarrecibo";
							
						}if($("#relacionGastosDiferentes").val() != ""){
							campos = " caNoContrarrecibo,nFolioRELACIONGASTOS,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteMasIva),1) AS importeIVA,'0.00' as Retenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
							szTabla = "tRELACIONGASTOSEncabezado";
							elParametro = "caNoContrarrecibo = '"+$("#relacionGastosDiferentes").val()+"'";
							idCXP = $("#relacionGastosDiferentes").val();
						}if($("#bDatosCXP").val() != ""){
							$('#movimientoCXP').dataTable().fnClearTable();
							campos = " caNoContrarrecibo,nFolioRELACIONGASTOS,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteMasIva),1) AS importeIVA,'0.00' as Retenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
							szTabla = "tRELACIONGASTOSEncabezado";
							elParametro = "caNoContrarrecibo = '"+idCXP+"'";
						}
					
				}if(tipoDoc == "p_diverso"){
					$('#movimientoCXP').dataTable().fnClearTable();
					campos = "caNoContrarrecibo,nFolioPAGODIVERSO,(SELECT dNombre FROM tBeneficiario WHERE dRFC = RFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' as fRecepcion,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIVA),1) AS importeIVA,isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.00') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tPAGODIVERSOEncabezado";
					elParametro = "caNoContrarrecibo = '"+idCXP+"'";
				}if(tipoDoc == "p_obra"){
					$('#movimientoCXP').dataTable().fnClearTable();
					campos = "caNoContrarrecibo,nFolioPAGOOBRA,(SELECT dNombre FROM tBeneficiario WHERE dRFC = RFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' AS fRecepcion, CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIVA),1) AS importeIVA,isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.00') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tPAGOOBRAEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
				}if(tipoDoc == "nomina"){
					$('#movimientoCXP').dataTable().fnClearTable();
					campos = "caNoContrarrecibo,nFolioNOMINA,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1),'-','-',CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion";
					szTabla = "tNominaEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
				}if(tipoDoc == "federalizado"){
					$('#movimientoCXP').dataTable().fnClearTable();
					campos = "caNoContrarrecibo,nFolioPAGOFEDERALIZADO,(SELECT dNombre FROM tBeneficiario WHERE dRFC = RFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' AS fRecepcion, CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIVA),1) AS importeIVA,isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.00') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion";
					szTabla = "tPAGOFEDERALIZADOEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
				}if(tipoDoc == "o_ajenas"){
					$('#movimientoCXP').dataTable().fnClearTable();
					campos = "caNoContrarrecibo,nFolioOperAjenas,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIDRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' AS fRecepcion, '-' AS importeBruto, '-' AS importeIVA, '-' AS totalRetenciones,CONVERT(varchar,CONVERT(money,mImportes),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tOperAjenasEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
				}
			}	
			if(tipoDoc == "r_gastos" && $("#relacionGastosIntegracion").val() != ""){
					//Verifica Si Existe Integracion
					$.ajax({
							url:'./ejercidoPagadoValidar.jsp',
							type:'post',
							data:{tipo:'existeIntegracion',nomCampoId:'sAuxiliarComodin',idCab:idIntegracion ,tabla:szTabla},
							success:function(data){
								if(data.sinSesion == 'sinSesion'){
									location.href = "../index.jsp";
								}else if(data == true){
									alert("La Integracion "+idIntegracion+". Ya Fue Aplicada");
									return;
								}else{
									szTabla = "tLayoutsCreadosRelacionGastosHeader2";
									campos = "l.sNoContrarrecibo, l.nFolio,(SELECT dNombre FROM tBeneficiario WHERE dRFC = sIdRFC) AS Beneficiaro,CONVERT(varchar,r.fAplicacion,103),CONVERT(varchar,r.fRecepcion,103),CONVERT(varchar,CONVERT(money,r.mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,r.mImporteMasIva),1) AS importeIVA,'0.00' as Retenciones,CONVERT(varchar,CONVERT(money,r.mImporteNeto),1) AS importeNeto";
									
									$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "5", ajax: 'true'}, function(j){
											var abono=0;
											var cargo=0;
											for (var i = 0; i < j.length; i++) {
												
													$("#nFolio").val(j[i].Col1);
													$("#fAplicacion").val(j[i].Col3);
													//$("#cEjercicio").val( j[i].Col9);
													datos = "datos";
													aoColumns: [
														   { sName: j[i].Col0,	bSearchable: true,	bSortable: false, bVisible: false},
														   { sName: j[i].Col1,	bSearchable: false,	bSortable: false, bVisible: true},
														   { sName: j[i].Col2,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col3,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col4,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col5,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col6,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col7,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col8,	bSearchable: false,	bSortable: false, bVisible: false},
													]
													$("#cuantosRecibos").val(i);	
													$("#movimientoCXP").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8 ]);
												}
												if(datos == "sinDatos"){
													alert("El Documento Con el Folio "+idCXP+" No Existe.");
													$('#movimientoCXP').dataTable().fnClearTable();
													$('#movimientoCXPDetalles').dataTable().fnClearTable();
													$("#flcxp").val("");
													$("#btnValidar").removeAttr("disabled");
													$("#btnAplicar").attr("disabled","disable");
												}else{
													$("#btnValidar").removeAttr("disabled");
													$("#btnAplicar").attr("disabled","disable");
												}
											});
								
								}
							}
					});
				
			//}else if(tipoDoc == "r_gastos" && $("#relacionGastosDiferentes").val() != ""){
			
			}else{
				//Verifica Si Existe CXP y Para Relacion de Gastos Diferentes
					$.ajax({
							url: './ejercidoPagadoValidar.jsp',
							type: 'post',
							data:{tipo:'existe',nomCampoId:'caNoContrarrecibo',idCab:idCXP,tabla:'tEjercidoEncabezado'},
							success:function(data){
								
								if(data.sinSesion == 'sinSesion'){
									location.href = "../index.jsp";
								}else if(data == true ){
									alert("El Documento Con el Folio "+idCXP+" Ya Fue Aplicado.");
									return;
					            }else{
									$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "5", ajax: 'true'}, function(j){
											var abono=0;
											var cargo=0;
											for (var i = 0; i < j.length; i++) {
													$("#nFolio").val(j[i].Col1);
													$("#fAplicacion").val(j[i].Col3);
													//$("#cEjercicio").val( j[i].Col9);
													datos = "datos";
													aoColumns: [
														   { sName: j[i].Col0,	bSearchable: true,	bSortable: false, bVisible: false},
														   { sName: j[i].Col1,	bSearchable: false,	bSortable: false, bVisible: true},
														   { sName: j[i].Col2,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col3,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col4,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col5,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col6,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col7,	bSearchable: false,	bSortable: false, bVisible: false},
														   { sName: j[i].Col8,	bSearchable: false,	bSortable: false, bVisible: false},
													]
														
													$("#movimientoCXP").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8 ]);
													if(j[i].Col9 > 0){
														alert("Documento Cancelado. Intente con otra Cuenta Por Pagar");
														$('#movimientoCXP').dataTable().fnClearTable();
													}
												}
												if(datos == "sinDatos"){
													alert("El Documento Con el Folio "+idCXP+" No Existe.");
													$('#movimientoCXP').dataTable().fnClearTable();
													$('#movimientoCXPDetalles').dataTable().fnClearTable();
													$("#flcxp").val("");
													$("#btnValidar").removeAttr("disabled");
													$("#btnAplicar").attr("disabled","disable");
												}else{
													$("#btnValidar").removeAttr("disabled");
													$("#btnAplicar").attr("disabled","disable");
												}
											});
								}
							}
					});
			}		
	}
	//dataTable Detalles CXP
	function muestraDatosCxpDetalles(idFolio){
			$('#movimientoCXPDetalles').dataTable().fnClearTable();
			var tipoDoc = $("#tipoDoc").val();
			var order = ""; //" ORDER BY nDocRenglon ASC ";
			//alert(idFolio);
			if(tipoDoc == "p_directo"){
					//campos = " EP,CONVERT(varchar,SUM(CONVERT(money,mImporteBruto)),1) AS importeBruto,CONVERT(varchar,SUM(CONVERT(money,mImporteIva)),1) AS importeIVA,CONVERT(varchar,SUM(CONVERT(money,mRetencion)),1) AS retencion,CONVERT(varchar,SUM(CONVERT(money,mImporteNeto)),1) AS importeNeto";
					szTabla = "tPagoDirectoDetalle";
					campos = " nFolioPagoDirecto = "+idFolio+" GROUP BY EP";
					elParametro = ""; 
			}
			if(tipoDoc == "r_gastos"){
					//campos = "nDocRenglon,EP,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIva),1) AS importeIVA,CONVERT(varchar,CONVERT(money,mRetencion),1) AS retencion,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto";
					szTabla = "tRELACIONGASTOSDetalle";
					campos = "nFolioRELACIONGASTOS = "+idFolio+" GROUP BY EP";
					elParametro = ""; 
					
			}if(tipoDoc == "p_diverso"){
					//campos = "nDocRenglon,EP,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIva),1) AS importeIVA,CONVERT(varchar,CONVERT(money,mRetencion),1) AS retencion,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto";
					szTabla = "tPAGODIVERSODetalle";
					campos = "nFolioPAGODIVERSO ="+idFolio+" GROUP BY EP ";
					elParametro = "";
			}if(tipoDoc == "p_obra"){
					//campos = "nDocRenglon,EP,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mIva),1) AS importeIVA,CONVERT(varchar,CONVERT(money,mRetencion),1) AS retencion,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto";
					szTabla = "tPAGOOBRADetalle";
					campos = "nFolioPAGOOBRA ="+idFolio+" GROUP BY EP ";
					elParametro = "";
			}if(tipoDoc == "nomina"){
					//campos = "nDocRenglon,EP,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIva),1) AS importeIVA,CONVERT(varchar,CONVERT(money,mRetencion),1) AS retencion,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto";
					szTabla = "tNOMINADetalle";
					campos = "nFolioNOMINA ="+idFolio+ " GROUP BY EP ";
					elParametro = "";
			}if(tipoDoc == "federalizado"){
					//campos = "nDocRenglon,EP,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mIva),1) AS importeIVA,CONVERT(varchar,CONVERT(money,mRetencion),1) AS retencion,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto";
					szTabla = "tPAGOFEDERALIZADODetalle";
					campos = "nFolioPAGOFEDERALIZADO ="+idFolio+" GROUP BY EP ";
					elParametro = "";
			}if(tipoDoc == "o_ajenas"){
					szTabla = "tOperAjenasDetalle";
					campos = "nFolioOperAjenas ="+idFolio+" GROUP BY EP ";
					elParametro = "";
			}
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "10", Order:order, ajax: 'true'}, function(j){
				var abono=0;
				var cargo=0;
				var noRenglon = 0;
				for (var i = 0; i < j.length; i++) {
						noRenglon = noRenglon + 1;
						$("#numCXP").val(noRenglon);
						$("#movimientoCXPDetalles").dataTable().fnAddData( [ noRenglon, j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3 ,j[i].Col4 ]);
					}		
				});
	}
	function muestraDatosSicop(){
			var idclc = $("#bDatos").val();
			var datos = "sinDatos";
			var tipoDoc = $("#tipoDoc").val();
			var szTabla = "";
			var campos = "";
			
			if(tipoDoc == "nomina"){
				szTabla = "datossicop_nomina";
				campos = " AND NCLC_43 = '"+idclc+"' GROUP BY SICOP.NCLC_43,SIAFF.BENEFICIARIO,FECHA_APL,SICOP.FOLIO_SIAFF_112,FECHA_APLICACION,FECHA_PAGO,SICOP.TOTAL_DIVISA_104, SIAFF.ESTATUS_CLC, SIAFF.APLICACION_CONTABLE ";
			}else{
				szTabla = "datossicop";
				campos = " NCLC_43 = '"+idclc+"' GROUP BY SICOP.NCLC_43,SIAFF.BENEFICIARIO,FECHA_APL,SICOP.FOLIO_SIAFF_112,FECHA_APLICACION,FECHA_PAGO,SICOP.TOTAL_DIVISA_104,RETENCION.IMP_RETE_49, SIAFF.ESTATUS_CLC, SIAFF.APLICACION_CONTABLE";
			}
			var elParametro = "";
			$("#flsicop").val("");
			$('#movimientoSicop').dataTable().fnClearTable();
			$('#movimientoSICOPDetalles').dataTable().fnClearTable();
			
			//Verifica Si Existe
			$.ajax({
					url: './ejercidoPagadoValidar.jsp',
					type: 'post',
					data:{tipo:'existe',nomCampoId:'nFolioSICOP',idCab:idclc,tabla:'tEjercidoEncabezado'},
					success:function(data){
						//if(data == true || data == 'true'){
						if(data == true && tipoDoc != "r_gastos"){
							
							alert("El Documento Con el CLC "+idclc+" Ya Fue Aplicado.");
							return;
							
						}else{
								if(data == true && tipoDoc == "r_gastos"){
							
									alert("Ya Existe CLC SICOP " +idclc+" Con Otros Documentos \n Puede Agregar Las Faltantes Si Este Es Su Caso");
								}
								
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "5",ajax: 'true'}, function(j){
									for (var i = 0; i < j.length; i++) {
											/*if (j[i].Col2=='C'){
												cargo=j[i].Col3;
												abono=0;
											}else{
												abono=j[i].Col3;
												cargo=0;
											}*/
											datos = "datos";
											$("#fPago").val(j[i].Col5);
											$("#estatus_clc").val(j[i].Col9);
											$("#aplicacion_contable").val(j[i].Col10);
											aoColumns: [
												   { sName: j[i].Col0,	bSearchable: true,	bSortable: false, bVisible: false},
												   { sName: j[i].Col1,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col2,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col3,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col4,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col5,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col6,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col7,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col8,	bSearchable: false,	bSortable: false, bVisible: false}
												   //{ sName: j[i].Col9,	bSearchable: false,	bSortable: false, bVisible: false}
											]
																
											$("#movimientoSicop").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8]);
										}
										if(datos == "sinDatos"){
											alert("El Documento Con el Folio "+idclc+" No Existe.");
											$('#movimientoSicop').dataTable().fnClearTable();
											$('#movimientoSICOPDetalles').dataTable().fnClearTable();
											$("#flsicop").val("");
											$("#btnValidar").removeAttr("disabled");
											$("#btnAplicar").attr("disabled","disable");
										}else{
											$("#btnValidar").removeAttr("disabled");
											$("#btnAplicar").attr("disabled","disable");
										}		
									});
						}
					}	
				});	
	}
	function muestraDatosSicopDetalles(idFolio){
			$('#movimientoSICOPDetalles').dataTable().fnClearTable();
			var szTabla = "DATOSSICOPDETALLES";
			var campos = "NCLC_43 = '"+idFolio+"' GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166,CCAU_162,CCOP_163";
			var elParametro	= "";
			var order = "";//" ORDER BY CONVERT(INT,ID_RENGLON) ASC";
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
				var noRenglon=0;
				var cargo=0;
				for (var i = 0; i < j.length; i++) {
						noRenglon = noRenglon + 1;
						//j[i].Col0 = noRenglon
						$("#numSICOP").val(noRenglon);
						//$("#movimientoSICOPDetalles").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4]);
						$("#movimientoSICOPDetalles").dataTable().fnAddData([ noRenglon, j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3 ]);
					}		
				});
	}function cambiaDoc(tipo){
		$("#flcxp").val();
		$('#movimientoCXP').dataTable().fnClearTable();
		$('#movimientoCXPDetalles').dataTable().fnClearTable();
		
		//Muestra tipo relacion de gastos.
		if(tipo == "r_gastos"){
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").hide();
			$("#selectRelacion").show();
		}else{
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").show();
			$("#selectRelacion").hide();
		}
	}
	function validarDatos(){
		
		$("#tblText").hide();
		$("#tblText").val("");
		var tipoDoc = $("#tipoDoc").val();
		var id_CXP = $("#flcxp").val();
		var id_CLC = $("#flsicop").val();
		var nomTabla = "", nomTablaDet = "";
		var nomCampos = "", nomCamposDet = "";
		var nomIdEnc = "", nomIdDet;
		var numCXP = $("#numCXP").val();
		var numSICOP = $("#numSICOP").val();
		var tipoCxp = "";
		if($("#aplicacion_contable").val() == 0  ){
			alert("Este Documento No Puede Aplicar Como Ejercido y/o Pagado Porque Esta Pendiente De Aplicacion En SIAFF");
			return
			
		}
		if(numCXP != numSICOP && $("#relacionGastosDiferentes").val() == "" && $("#relacionGastosIntegracion").val() == "" ){
			alert("El Numero De Detalles Es Diferente.");
			return;
		}
		//Se quita ID_TIPO_MOVIMIENTO,cEvento,ID_TIPO_CONCEPTO  para validar
		if(tipoDoc == "p_directo"){
			nomTabla = "tPagoDirectoEncabezado";
			nomTablaDet = "tPagoDirectoDetalle";
			nomCampos = "nFolioPagoDirecto AS idFolio, mImporteNeto as impNeto,aEjercicioFiscal"; //Campos Encabezado para Comparar con SICOP
			nomCamposDet = "EP,SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto, cCentroContable,RFC,cEjercicio,cIdDocumento AS cIdRelacion,nCapitulo"; //Campos Detalle para Comparar SICOP
			nomIdEnc = "caNoContrarrecibo";
			nomIdDet = "nFolioPagoDirecto";
		}else if(tipoDoc == "r_gastos"){
			if($("#relacionGastosIntegracion").val() != ""){
					var idIntegracion = $("#relacionGastosIntegracion").val();
					nomTabla = "tLayoutsCreadosRelacionGastosHeader";
					tipoCxp = "integracion"
			}if($("#selectRelacion").val() == "diferente"){
						elParametro = " diferente = '"+$("#relacionGastosDiferentes").val()+"'";
						tipoCxp = "diferente";
			}if($("#bDatosCXP").val() != ""){
					nomTabla = "tRELACIONGASTOSEncabezado";
					nomTablaDet = "tRELACIONGASTOSDetalle";
					nomCampos = "nFolioRELACIONGASTOS AS idFolio, mImporteNeto as impNeto,aEjercicioFiscal";
					nomCamposDet = "EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo"; //Campos Detalle para Comparar SICOP;
					nomIdEnc = "caNoContrarrecibo";
					nomIdDet = "nFolioRELACIONGASTOS";
			}
					
		}else if(tipoDoc == "p_diverso"){
			nomTabla = "tPAGODIVERSOEncabezado";
			nomTablaDet = "tPAGODIVERSODetalle";
			nomCampos = "nFolioPAGODIVERSO AS idFolio, mImporteNeto as impNeto,aEjercicioFiscal";
			nomCamposDet = "EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo"; //Campos Detalle para Comparar SICOP;
			nomIdEnc = "caNoContrarrecibo";
			nomIdDet = "nFolioPAGODIVERSO";
		}else if(tipoDoc == "p_obra"){
			nomTabla = "tPAGOOBRAEncabezado";
			nomTablaDet = "tPAGOOBRADetalle";
			nomCampos = "nFolioPAGOOBRA as idFolio, mImporteNeto as impNeto,aEjercicioFiscal";
			nomCamposDet = "EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo"; //Campos Detalle para Comparar SICOP;
			nomIdEnc = "caNoContrarrecibo";
			nomIdDet = "nFolioPAGOOBRA";
		}else if(tipoDoc == "nomina"){
			nomTabla = "tNOMINAEncabezado";
			nomTablaDet = "tNOMINADetalle";
			nomCampos = "nFolioNOMINA as idFolio, mImporteNeto as impNeto,aEjercicioFiscal";
			nomCamposDet = "EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo"; //Campos Detalle para Comparar SICOP;
			nomIdEnc = "caNoContrarrecibo";
			nomIdDet = "nFolioNOMINA";
		}else if(tipoDoc == "federalizado"){
			nomTabla = "tPAGOFEDERALIZADOEncabezado";
			nomTablaDet = "tPAGOFEDERALIZADODetalle";
			nomCampos = "nFolioPAGOFEDERALIZADO as idFolio, mImporteNeto as impNeto, aEjercicioFiscal";
			nomCamposDet = "EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo"; //Campos Detalle para Comparar SICOP;
			nomIdEnc = "caNoContrarrecibo";
			nomIdDet = "nFolioPAGOFEDERALIZADO";
		}else if(tipoDoc == "o_ajenas"){
			nomTabla = "tOperAjenasEncabezado";
			nomTablaDet = "tOperAjenasDetalle";
			nomCampos = "nFolioOperAjenas as idFolio, mImportes AS impNeto, aEjercicioFiscal";
			nomCamposDet = "Ep AS EP, SUM(CONVERT(money,mTotal)) AS mImporteNeto,cCentroContable,aEjercicioFiscal AS cEjercicio";
			nomIdEnc = "caNoContrarrecibo";
			nomIdDet = "nFolioOperAjenas";
		}
		
		//if(id_CXP != "" && id_CLC != "" && tipoDoc != ""){
		if(id_CXP != "" && id_CLC != "" && tipoDoc != ""){
			if(tipoDoc == "r_gastos" && ($("#relacionGastosIntegracion").val() != "" || $("#selectRelacion").val() == "diferente")){
				$.ajax({
					url: './ejercidoPagadoValidar.jsp',
					type: 'post',
					dataType: 'json',
					data:{
						tipo:'validarIntegracion',
						idCXP:id_CXP,
						idCLC:id_CLC,
						tabla:nomTabla,
						tipoCxp:tipoCxp
					},
					success: function(data){
						if(data.encaFalso == "encaFalso"){
							alert("Encabezados Incorrectos.");
						}else if(data.validacionCorrecta == "validacionCorrecta"){
							alert("La Validacion Es Correcta.");
							$("#btnAplicar").removeAttr("disabled");
							$("#btnValidar").attr("disabled","disabled");
							$("#tipoDoc").attr("disabled","disabled");
						}else if(data.diferenciaCentavo == "diferenciaCentavo"){
							alert("Diferencia De Un Centavo, Puede Aplicar");
							$("#btnAplicar").removeAttr("disabled");
							$("#btnValidar").attr("disabled","disabled");
							$("#tipoDoc").attr("disabled","disabled");
						}else if(data.error == "error"){
							alert("Error: no se hizo la comparacion, intente de nuevo");
						}else{
							alert("Detalles Incorrectos.");
							$("#txtDif").val(data.errorDetalles);
							$("#tblText").show();
						}
					}
					
				});
			}else if(tipoDoc == "r_gastos" && $("#relacionGastosDiferentes").val() != ""){
				
				
			}else{
				
				//Para las cuentas por pagar diferentes de relacion gastos integracion o diferentes.
				$.ajax({
						url: './ejercidoPagadoValidar.jsp',
						type: 'post',
						dataType: 'json',
						data: {
							tipo:'validar',
							idCXP:id_CXP, 
							idCLC:id_CLC, 
							tabla:nomTabla,
							tablaDet:nomTablaDet,
							campos:nomCampos,
							camposDet:nomCamposDet,
							idEnc:nomIdEnc,
							idDet:nomIdDet
						},
						success: function(data){
														
								if(data.encaFalso == "encaFalso"){
									alert("Encabezados Incorrectos.");
								}else if(data.validacionCorrecta == "validacionCorrecta"){
									alert("La Validacion Es Correcta.");
									$("#btnAplicar").removeAttr("disabled");
									$("#btnValidar").attr("disabled","disabled");
									$("#tipoDoc").attr("disabled","disabled");
								}else if(data.diferenciaCentavo == "diferenciaCentavo"){
									alert("Diferencia De Un Centavo, Puede Aplicar");
									$("#btnAplicar").removeAttr("disabled");
									$("#btnValidar").attr("disabled","disabled");
									$("#tipoDoc").attr("disabled","disabled");
								}else if(data.error == "error"){
									alert("Error: no se hizo la comparacion, intente de nuevo");
								}else{
									alert("Detalles Incorrectos.");
									//alert(data.errorDetalles);
									$("#txtDif").val(data.errorDetalles);
									$("#tblText").show();
								}
								/*if(data.datos == "encaFalso"){
									alert("Encabezados Incorrectos.");
								}else if(data.datos == "detFalso"){
									alert("Detalles Incorrectos.");
								}else if(data.datos == "iguales"){
									alert("La Validacion Es Correcta.");
								}else{
									alert("else");
								}*/
							 	/*if(data == false){
									alert("La Validacion No Es Correcta, Intente Con Otros Folio.");
								}else{
									alert("La Validacion Es Correcta.");
									$("#btnAplicar").removeAttr("disabled");
									$("#btnValidar").attr("disabled","disabled");
									$("#tipoDoc").attr("disabled","disabled");
								}*/
					 	}
			  	 	});
			}
		}else{
			alert("Error: Falta Folio CXP o Folio Sicop para Validar");
		}
	}
	function aplicar(){	
		
		
		document.getElementById("btnAplicar").disabled = false;
		//document.getElementById("esperar").style.visibility = 'visible';
		$("#esperar").attr("style","visibility=visible");
		$("#tipoDoc").removeAttr("disabled");
		var tipoDoc = $("#tipoDoc").val();
		var id_CXP = $("#flcxp").val();
		var id_CLC = $("#flsicop").val();
		var fPago = $("#fPago").val(); //Variable para ingresar en el campo fAplicado y lo aplique en Pagado.
		var usuario = $("#usuario").val();
		var statusSiaff = $("#estatus_clc").val();
		
		getNextSequenceVal({seqName: "EJERCIDO" , async: false, callback: setSequenceValE});
		getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
		
		var idfolioEjercido = $("#nFolioEjercido").val();
		var idfolioPagado = $("#nFolioPagado").val();
		
		/*if(fPago == " " || fPago == ""){
			alert("No Se Puede Aplicar Porque No Tiene Fecha de Pago SIAFF");
			return;
		}*/
		if(tipoDoc == "p_directo"){
			nomTabla = "tPagoDirectoEncabezado";
			nomTablaDet = "tPagoDirectoDetalle";
			nomCampos = "nFolioPagoDirecto AS nFolio, mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
			nomCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable,cIdDocumento AS cIdRelacion";
			nomCamposDetValidar = "cEvento, EP, cCentroContable,SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,RFC,cEjercicio,cIdDocumento AS cIdRelacion,nCapitulo,cMes";
			nomCXP = "caNoContrarrecibo";
			nomCXPDetFolio = "nFolioPagoDirecto";
			cTipoPago = "PAGODIRECTO";
		}else if(tipoDoc == "r_gastos"){
			
			if($("#relacionGastosIntegracion").val() != ""){
					var idIntegracion = $("#relacionGastosIntegracion").val();
					nomTabla = "tLayoutsCreadosRelacionGastosHeader";
					nomCamposDetValidar="";
					cuantos = $("#cuantosRecibos").val();
					for(var i = 0; i <= cuantos; i++){
						getNextSequenceVal({seqName: "EJERCIDO" , async: false, callback: setSequenceValE});
						getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
						
						idfolioEjercido += "/"+$("#nFolioEjercido").val();
						idfolioPagado += "/"+$("#nFolioPagado").val();
					}
	
			}if($("#selectRelacion").val() == "diferente"){
					var nF = id_CXP.split("/");
					var idEjercido = "";
					var idPagado = "";
					for(var i = 1; i < nF.length - 1; i++){
						getNextSequenceVal({seqName: "EJERCIDO" , async: false, callback: setSequenceValE});
						getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
						
						idfolioEjercido += "/"+$("#nFolioEjercido").val();
						idfolioPagado += "/"+$("#nFolioPagado").val();
						
					}
					nomTabla = " tRELACIONGASTOSEncabezado ";
			}if($("#bDatosCXP").val() != ""){
				nomTabla = "tRELACIONGASTOSEncabezado";
				nomTablaDet = "tRELACIONGASTOSDetalle";
				nomCampos = "nFolioRELACIONGASTOS AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
				nomCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
				nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo,cMes";
				nomCXP = "caNoContrarrecibo";
				nomCXPDetFolio = "nFolioRELACIONGASTOS";
				cTipoPago = "RELACIONGASTOS";
			}
		}else if(tipoDoc == "p_diverso"){
			nomTabla = "tPAGODIVERSOEncabezado";
			nomTablaDet = "tPAGODIVERSODetalle";
			nomCampos = "nFolioPAGODIVERSO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
			nomCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
			nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo,cMes";
			nomCXP = "caNoContrarrecibo";
			nomCXPDetFolio = "nFolioPAGODIVERSO";
			cTipoPago = "PAGODIVERSO";
		}else if(tipoDoc == "p_obra"){
			nomTabla = "tPAGOOBRAEncabezado";
			nomTablaDet = "tPAGOOBRADetalle";
			nomCampos = "nFolioPAGOOBRA AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
			nomCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
			nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo,cMes";
			nomCXP = "caNoContrarrecibo";
			nomCXPDetFolio = "nFolioPAGOOBRA";
			cTipoPago = "PAGOOBRA";
		}else if(tipoDoc == "nomina"){
			nomTabla = "tNOMINAEncabezado";
			nomTablaDet = "tNOMINADetalle";
			nomCampos = "nFolioNOMINA AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,cIdUsuarioCaptura AS U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
			nomCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,cCentroContable AS cIdEntidadContable, cIdRelacion";
			nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo,cMes";
			nomCXP = "caNoContrarrecibo";
			nomCXPDetFolio = "nFolioNOMINA";
			cTipoPago = "NOMINA";
		}else if(tipoDoc == "federalizado"){
			nomTabla = "tPAGOFEDERALIZADOEncabezado";
			nomTablaDet = "tPAGOFEDERALIZADODetalle";
			nomCampos = "nFolioPAGOFEDERALIZADO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
			nomCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
			nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo,cMes";
			nomCXP = "caNoContrarrecibo";
			nomCXPDetFolio = "nFolioPAGOFEDERALIZADO";
			cTipoPago = "FEDERALIZADO";
		}else if(tipoDoc == "o_ajenas"){
			nomTabla = "tOperAjenasEncabezado";
			nomTablaDet = "tOperAjenasDetalle";
			nomCampos = "nFolioOperAjenas AS nFolio,mImportes as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
			nomCamposDet = "nDocRenglon,cMes,aEjercicioFiscal AS cEjercicio,Ep AS EP,cEvento,cCentroContable,RFC,mTotal AS mImporteNeto,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteFlete23,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,cCentroContable AS cIdEntidadContable";
			nomCamposDetValidar = "cEvento, Ep AS EP, SUM(CONVERT(money,mTotal)) AS mImporteNeto,cCentroContable,RFC,aEjercicioFiscal AS cEjercicio,cMes";
			nomCXP = "caNoContrarrecibo";
			nomCXPDetFolio = "nFolioOperAjenas";
			cTipoPago = "AJENAS";
		}
		
		if(tipoDoc == "r_gastos" && $("#relacionGastosIntegracion").val() != ""){
				$.ajax({
					url:'./ejercidoPagadoValidar.jsp',type:'post',data:{tipo:'aplicarIntegracion',idCXP:id_CXP,idCLC:id_CLC,tabla:nomTabla,fPago:fPago,usuario:usuario,idfolioEjercido:idfolioEjercido,idfolioPagado:idfolioPagado,nomCamposDetValidar:nomCamposDetValidar,statusSiaff:statusSiaff},
					success: function(data){
						if(data.estatus == "correcto"){	
							alert("Aplicado Correctamente");
							location.reload();
						}else{
							document.getElementById("esperar").style.visibility = 'hidden';
							document.getElementById("btnAplicar").disabled = false;
							alert("El Documento No Fue Aplicado, Por Favor Intente Nuevamente.");
							
						}
					}	
				});
		
		}else if(tipoDoc == "r_gastos" && $("#selectRelacion").val() == "diferente"){
				$.ajax({
					url:'./ejercidoPagadoValidar.jsp',type:'post',data:{tipo:'aplicarDiferentes',idCXP:id_CXP,idCLC:id_CLC,tabla:nomTabla,fPago:fPago,usuario:usuario,idfolioEjercido:idfolioEjercido,idfolioPagado:idfolioPagado,statusSiaff:statusSiaff},
					success: function(data){
						if(data.estatus == "correcto"){	
							alert("Aplicado Correctamente");
							location.reload();
						}else{
							document.getElementById("esperar").style.visibility = 'hidden';
							document.getElementById("btnAplicar").disabled = false;
							alert("El Documento No Fue Aplicado, Por Favor Intente Nuevamente.");
							
						}
					}	
				});
				
		}else{
				$.ajax({
					url:'./ejercidoPagadoValidar.jsp',type: 'post',dataType: 'json',data:{tipo:'aplicar',idCXP:id_CXP,idCLC:id_CLC,tabla:nomTabla,tablaDet:nomTablaDet,campos:nomCampos,camposDet:nomCamposDet,nomCXP:nomCXP,tipoPago:cTipoPago,nomDetFolio:nomCXPDetFolio,fPago:fPago,usuario:usuario,idfolioEjercido:idfolioEjercido,idfolioPagado:idfolioPagado,nomCamposDetValidar:nomCamposDetValidar,statusSiaff:statusSiaff},
					success: function(data){
						if(data.estatus == "correcto"){
							alert("Aplicado Correctamente");
							location.reload();
						}else{
							document.getElementById("esperar").style.visibility = 'hidden';
							document.getElementById("btnAplicar").disabled = false;
							alert("El Documento No Fue Aplicado, Por Favor Intente Nuevamente.");
						
						}
					}
				});	
		}
		document.getElementById("btnAplicar").disabled = true;
		//document.getElementById("esperar").style.visibility = 'hidden';
		
	}
	/*function opcionRelacionGastos(){
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").hide();
			$("#selectRelacion").show();
		if(tipo == "r_gastos"){
			$("#tdRelacionGastosIntegracion").show();
			$("#tdRelacionGastosDiferentes").show();
			
		}else{
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
		}	
	}*/
	function tipoRelacion(tipo){
		if(tipo == "integracion"){
			$("#tdRelacionGastosIntegracion").show();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").hide();
			$("#relacionGastosIntegracion").val("");
			$("#relacionGastosDiferentes").val("");
			$("#bDatosCXP").val("");
		}else if(tipo == "diferente"){
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").show();
			$("#tdCXP").hide();
			$("#relacionGastosIntegracion").val("");
			$("#relacionGastosDiferentes").val("");
			$("#bDatosCXP").val("");
		}else if(tipo == "cxp" ){
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").show();
			$("#relacionGastosIntegracion").val("");
			$("#relacionGastosDiferentes").val("");
			$("#bDatosCXP").val("");
		}else{
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").hide();
			$("#relacionGastosIntegracion").val("");
			$("#relacionGastosDiferentes").val("");
			$("#bDatosCXP").val("");
		}
	}
	function aplicarContable(){
		if(confirm("Esta seguro que desea aplicar contablemente?")){
				$.ajax({
					url: './ejercidoPagadoValidar.jsp',
					type: 'post',
					data: {tipo:'aplicarContable',tipoAplicacion:$("#selectApliContable").val() , nFolio:$("#nFolioContable").val() ,parametro:$("#condicion").val()},
					success: function(data){
						if(data.estatus == "correcto"){
							alert("Aplicado Correctamente");
							location.reload();
						}else{
							alert("No Se Aplico Correctamente o Ya Esta Aplicado");
						}
					}
				});
		}		
	}
	function setSequenceValE(seqValue) 
{
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioEjercido").val( seqValue );
}
	function setSequenceValP(seqValue) 
{
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioPagado").val( seqValue );
}
	
	
</script>	
  </head>
  <body id="dt_example">
		<div  id="container">
			<h1>Ejercido Pagado<label style="font-size: 8pt"></label></h1>			
        	<div id="tabsl">    		
        		<table id="tblEjecidoPagado" align="center" width="1000px">	
        		<tr>
					<td>
					<label id="esperar" style="visibility: hidden">
													<div align="center">Espere por favor....
													  <img border="0" src="../imagenes/espera.gif" height="30">
													</div>
											</label>
						<fieldset>
							<table id="tblBoton" align="center" width="1100px">
								<tr>
									<td align="center">TIPO DE PAGO</td><td><select id="tipoDoc" name="tipoDoc" onchange="cambiaDoc(this.value)">
																<option value="seleccione">Seleccione</option>
																<option value="p_directo">Pago Directo</option>
																<option value="r_gastos">Relacion Gastos</option>
																<option value="p_diverso">Pago Diverso</option>
																<option value="p_obra">Pago de Obra</option>
																<option value="nomina">Nomina</option>
																<option value="federalizado">Federalizado</option>
																<option value="o_ajenas">Operaciones Ajenas</option>
															 </select>
									</td>
									<td>FOLIO CUENTAS POR PAGAR</td><td><input type="text" id="flcxp" name="flcxp" size="30" readonly="readonly" ></td>
									<td>FOLIO SICOP</td><td><input type="text" id="flsicop" name="flsicop" size="15" readonly="readonly" ></td>
									<td><input type="button" id="btnValidar" name="btnValidar" value="Validar"></td>
									<td><input type="button" id="btnAplicar" name="btnAplicar" value="Aplicar"></td>
									<td><input type="button" id="btnNuevo" name="btnNuevo" value="Nuevo"></td>
									
									<td><input type="hidden" id="fPago" name="fPago"></td>
									<td><input type="hidden" id="numCXP" name="numCXP"></td>
									<td><input type="hidden" id="numSICOP" name="numSICOP"></td>
									<td><input type="hidden" id="usuario" name="usuario" value="<%=usuario.getLogin()%>"></td>
									<td><input type="hidden" id="nFolioEjercido" name="nFolioEjercido"></td>
									<td><input type="hidden" id="nFolioPagado" name="nFolioPagado"></td>
									<td><input type="hidden" id="cuantosRecibos" name="cuantosRecibos"/></td>
									<td><input type="hidden" id="estatus_clc" name="estatus_clc" /></td>
									<td><input type="hidden" id="aplicacion_contable" name="aplicacion_contable" /></td>
									<!--td><input type="text" id="nFolio" name="nFolio"></td>
									
									<td><input type="text" id="fAplicacion" name="fAplicacion"></td>
									<td><input type="text" id="cEjercicio" name="cEjercicio"></td-->
									
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td>
						<fieldset>
							<legend>Cuenta Por Pagar, Detalle No Encontrado En Detalle SICOP</legend>
							<table id="tblText" name="tblText" align="center">
								<td><textarea rows="14" cols="115" id="txtDif" name="txtDif" style="font-size: 9pt; font-family: Arial" readonly="readonly"></textarea></td>
							
							</table>
						</fieldset>
					</td>	
				</tr>
        		<tr>	
					<td>
						<fieldset>
							<legend>Datos Cuentas Por Pagar</legend>
								<table align="right">
									<td><select id="selectRelacion" name="selectRelacion" onchange="tipoRelacion(this.value);">
										<option value="seleccione">Seleccione</option>
										<option value="integracion">Buscar Por Integracion</option>
										<option value="diferente">Buscar Por CXP Diferentes</option>
										<option value="cxp">Buscar Por Cuenta Por Pagar</option>
									</select></td>
									<td id="tdRelacionGastosIntegracion">BUSCAR POR INTEGRACION <input type="text" name="relacionGastosIntegracion" id="relacionGastosIntegracion" /></td>
									<td id="tdRelacionGastosDiferentes">BUSCAR DIFERENTES <input type="text" name="relacionGastosDiferentes" id="relacionGastosDiferentes" /></td>
									<td id="tdCXP">BUSCAR POR CUENTA POR PAGAR<input type="text" name="bDatosCXP" id="bDatosCXP"></td><td><input type="button" id="btonBuscar" value="Buscar" onClick="muestraDatosCxp()"></td>
								</table>
								<table id="movimientoCXP" class="display" align="center" width="900px">
									<tbody>
										<thead>
											<tr align="center">
												<th>No CXP</th>
												<th>Folio</th>	
												<th>Beneficiario</th>
												<th>Fecha Aplicacion</th>
												<th>Fecha Registro</th>	
												<th>Importe Bruto</th>
												<th>Importe Iva</th>
												<th>Importe Retenciones</th>
												<th>Importe Neto</th>
												<!--th>Total Ejercido</th>
												<th></th>Claves Presupuestales</th-->
												<!--th>Importes</th-->
											</tr>	
										</thead>							
									</tbody>	
								</table>
						  </fieldset>	
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>	
					<td>
						<fieldset>
							<legend id="lgndDetalles">Datos Cuentas Por Pagar Detalles</legend>
							<table id="movimientoCXPDetalles" class="display" align="center" width="900px">
								<thead>
									<tr align="center">
										<th>No</th>
										<th>Estructura Programatica</th>
										<th class="center">Importe Bruto</th>
										<th>Importe Iva</th>
										<th>Importe Retencion</th>
										<th>Importe Neto</th>
									</tr>
								</thead>
							</table>	
						</fieldset>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
				<tr>	
					<td>
						<fieldset>
							<legend>Datos SICOP</legend>
								<table align="right"> 
									<td>BUSCAR POR CLC SICOP</td><td><input type="text" name="bDatos" id="bDatos"><input type="button" id="btonBuscar" value="Buscar" onClick="muestraDatosSicop()"></td>
								</table>
								<table id="movimientoSicop" class="display" align="center">
											<thead>
												<tr align="center">
												    <th>No CLC SICOP</th>
													<th>Beneficiario</th>
													<th>Fecha Aplic. SICOP</th>	
													<th>No CLC SIAFF</th>	
													<th>Fecha Aplic. SIAFF</th>
													<th>Fecha Pago SIAFF</th>															
													<th>Importe Neto</th>
													<th>Retenciones</th>
													<th>Total Ejercido</th>
												</tr>	
											</thead>							
								  </table>
						  </fieldset>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>	
					<td>
						<fieldset>
							<legend id="lgndDetalles">Datos SICOP Detalles</legend>
							<table id="movimientoSICOPDetalles" class="display" align="center" width="900px">
								<thead>
									<tr align="center">
										<th>No</th>
										<th>Estructura Programatica</th>
										<th>Importe Neto</th>
										<th>Importe Retencion</th>
										<th>Importe Ejercido</th>
									</tr>
								</thead>
							</table>	
						</fieldset>
					</td>
				</tr>
				
				<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<fieldset>
							<legend id="lgndDetalles">Datos Para Aplicacion Contable</legend>
								<table id="tblAplicacion" align="center" width="700px">
									<td>Tipo De Aplicacion Contable</td><td><select id="selectApliContable" name="selectApliContable"><option value="EJERCIDO">EJERCIDO</option><option value="PAGADO">PAGADO</option></select></td>
									<td>Numero Folio Cuentas Por Pagar</td><td><input type="text" name="nFolioContable" id="nFolioContable"></td>
									
									<td><input type="button" id="btnContable" name="btnContable" value="APLICAR" onclick="aplicarContable();" ></td>
								</table>
						</fieldset>		
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
			</table>				
		</div>
	</div>	
</html>
