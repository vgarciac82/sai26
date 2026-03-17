<%@page import="com.syc.sai.fonden.model.FondenManager"%>
<%@page import="com.syc.sai.fonden.model.FondenBusinessLogic"%>
<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenManager"%>
<%@page import="com.syc.sai.fonden.*"%>
<%@page import="com.syc.sai.fonden.model.FondenMovimientoBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenMovimientoManager"%>
<%@page import="com.syc.sai.fonden.model.FondenCatalogsManager"%>
<%@page import="com.syc.sai.fonden.FondenMovimiento"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<%!private Logger log = LoggerFactory.getLogger(getClass());%>

<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String cCentroContable = "", mensaje = "";
	
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int id_oper = -1;
	if (request.getParameter("id_oper") != null){
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	}else{
		id_oper = c.getCasoOperacion(0).getIdOperacion();
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	String cidFonde = sdf.format(new Date(System.currentTimeMillis()));
	String DATE_FORMAT = "yyyy-MM-dd";
	sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	String cUR = usuario.getU_UR();
	String u_login = usuario.getLogin();

	
	FondenBusinessLogic fbl = new FondenBusinessLogic(); 
	FondenMovimiento fondenMovimiento = FondenMovimientoManager.readFondenMovimientoByFolio(fbl.getConnection(), c.getFolio());
		
	String currentYear = sdf.format(new Date(System.currentTimeMillis()));
	sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
	
	//Valida Centro de Costos
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
	            cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	List<FondenMoneda> fondenMonedas = FondenCatalogsManager.readCatalog(new FondenBusinessLogic().getConnection(), "tFondenMoneda");
	
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Movimiento FONDEN</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="-1">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<link rel="stylesheet" type="text/css"
	href="Ayudas/css/autocompleta.css"></link>
<style>
.ui-autocomplete {
	max-height: 100px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
}

/* IE 6 doesn't support max-height
				     * we use height instead, but this forces the menu to always be this tall
				     */
* html .ui-autocomplete {
	height: 100px;
}
</style>

<style type="text/css" title="currentStyle">
	@import "css/demo_page.css";
	@import "css/demo_table_jui.css";
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/ContabilidadCentroContable.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/Poliza.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/jquery.maskedinput.min.js"></script>
<script type="text/javascript" src="js/jquery.validate-1.9.0.js"></script>
<script type="text/javascript">

	var cIdFonden = '<%=fondenMovimiento != null ? fondenMovimiento.getCidFonden() : cidFonde%>';
	var nIdFondenMovimiento = '<%=fondenMovimiento != null ? fondenMovimiento.getNidFondenMovimiento() : c.getIdCaso()%>';
	var oTable;	
	
	$(document).ready(function() {												
						format();
						setMethodsValidations();
						setValidations();												
						refreshTable();	
						loading();		
						setSelectableTable();
						setSelectableTableStyle();
						var button = parent.document.getElementById("pb_cancel");																	
						button.onclick = function(e) {
						        $("#nIdFondenMovEstatus").val("3");
						        submitForm();						        
						        parent.doCancel();
						    };
						    
						var buttonLeave = parent.document.getElementById("pb_leave");																	
						buttonLeave.onclick = function(e) {
						        $("#nIdFondenMovEstatus").val("1");
						        submitForm();						        
						        parent.doLeave();
						    };	
						
						var buttonSend = parent.document.getElementById("pb_send");																	
						buttonSend.onclick = function(e) {
								if(<%=id_oper == 2%>){
						        	$("#nIdFondenMovEstatus").val("2");
						        }
						        submitForm();						        
						        parent.validaEnviar();;
						    };	
						    
		$("#cIdMoneda").change(function(){
			if ($(this).val() == "MXN"){
				$("#nTipoCambio").val( "1.00" );
				$("#nTipoCambio").attr("readonly", true);
				$("#nTipoCambio").hide();
				$("#lTCam").hide();
			}else{
				$("#nTipoCambio").val( "" );
				document.getElementById("nTipoCambio").removeAttribute("readonly",false);
				$("#nTipoCambio").show(); //visibility: hidden
				$("#lTCam").show();
			}
				
		});	
		
		$("#nTipoCambio").hide();
		$("#lTCam").hide();
		
		$("#nprecioUnitario").change(function(){
			var mprecioU = Number( quitaFmt( $(this).val() ) );
			$("#nimporte").val( mprecioU * Number( $("#ncantidad").val() ) );
			$("#nimporte").formatCurrency();
		});	
		
		$("#ncantidad").change(function(){
			var mprecioU = Number( quitaFmt( $("#nprecioUnitario").val() ) );
			$("#nimporte").val( Number( $(this).val() ) * mprecioU );
			$("#nimporte").formatCurrency();
		});	
		
		$("#nprecio").change(function(){
			var mprecioU = Number( quitaFmt( $(this).val() ) );
			$("#ntotal").val( mprecioU * Number( $("#nCantidadTotal").val() ) );
			$("#ntotal").formatCurrency();
		});	
		
		$("#nCantidadTotal").change(function(){
			var mprecioU = Number( quitaFmt( $("#nprecio").val() ) );
			$("#ntotal").val( Number( $(this).val() ) * mprecioU );
			$("#ntotal").formatCurrency();
		});	
						
	});

	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}
	
	function setMethodsValidations(){
	
		$.validator.addMethod(
	        "noGreaterThanImporteAnual", 
	        function(value, element) {
	            var importeAnual = validateImporteAnualServer();	
	            //alert(importeAnual);            
	            return importeAnual=='true';
	        }	       
	    );
	
	}
	
	function validateImporteAnualServer(){	
		var result;
		 $.ajax({
	                type: "POST",
	                async : false,
	                url: 	"../FondenMovimiento/validateNoGreaterThanImporteAnual?ntotal="+$("#ntotal").val()+"&cidFonden="+cIdFonden,	                
	                dataType:"json",
	                success: function(msg)
	                {	  
	               		result = msg[0].value;	                	
	                }
	             });
	     return result;
	}
	
	function format() {
		$("#nprecioUnitario").toNumber().formatCurrency();
		$("#nimporte").toNumber().formatCurrency();
		$("#nprecio").toNumber().formatCurrency();
		$("#ntotal").toNumber().formatCurrency();
		$("#nTechoDef").toNumber().formatCurrency();
		$("#nnetoPedido").toNumber().formatCurrency();
		
		

	};
	
	function setValidations(){
		$('#formFondenMovimiento')
								.validate(
										{ // initialize the plugin
											rules : {
												cidFonden : {
													required : true
												},

												nidFondenMovimiento : {
													required : true
												},

												cconcepto : {
													required : true
												},

												nprecioUnitario : {
													required : true
												},

												ncantidad : {
													required : true,
													number : true
												},

												nimporte : {
													required : true
												},

												crfc : {
													required : true
												},

												cnumPedido : {
													required : true
												},

												nnetoPedido : {
													required : true
												},

												nprecio : {
													required : true
												},

												ntotal : {
													required : true,
													noGreaterThanImporteAnual : true 
												},

												cproveedor : {
													required : true
												},

												activo : {
													required : true
												},

												idGabinete : {
													required : true
												},
												
												nNumCaso : {
													required : true,
													number : true
												},
												nCantidadTotal : {
													required : true,
													number : true
												},
												nTechoDef : {
													required : true													
												},

												nTipoCambio : {
													required : true,	
													number : true												
												}

											},
											messages : {
												cidFonden : {
													required : "<li style=\"color:red\">El año es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nidFondenMovimiento : {
													required : "<li style=\"color:red\">El nidFondenMovimiento es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												cconcepto : {
													required : "<li style=\"color:red\">El cconcepto es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 150 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nprecioUnitario : {
													required : "<li style=\"color:red\">El Precio unitario es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												ncantidad : {
													required : "<li style=\"color:red\">La cantidad es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nimporte : {
													required : "<li style=\"color:red\">El Importe es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												crfc : {
													required : "<li style=\"color:red\">El RFC es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												cnumPedido : {
													required : "<li style=\"color:red\">El Número del pedido es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nnetoPedido : {
													required : "<li style=\"color:red\">El Neto Pedido es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nprecio : {
													required : "<li style=\"color:red\">El Precio es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												ntotal : {
													required : "<li style=\"color:red\">El Total es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>",
													noGreaterThanImporteAnual : "<li style=\"color:red\">El monto total sobrepasa el saldo del importe anual.</li>"
												},

												cproveedor : {
													required : "<li style=\"color:red\">El Proveedor es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												activo : {
													required : "<li style=\"color:red\">El activo es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												idGabinete : {
													required : "<li style=\"color:red\">El idGabinete es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},
												
												nNumCaso : {
													required : "<li style=\"color:red\">El Número de caso es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},
												nCantidadTotal : {
													required : "<li style=\"color:red\">La cantidad total es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},
												nTechoDef : {
													required : "<li style=\"color:red\">El TECHO es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												},

												nTipoCambio : {
													required : "<li style=\"color:red\">El Tipo de cambio es un dato requerido.</li>",
													minlength : "<li style=\"color:red\">Debe contener al menos 0 caracteres.</li>",
													maxlength : "<li style=\"color:red\">Debe contener menos de 100 caracteres.</li>",
													email : "<li style=\"color:red\">Email invalido.</li>",
													date : "<li style=\"color:red\">fecha invalida.</li>",
													number : "<li style=\"color:red\">Debe ser numérico.</li>",
													digits : "<li style=\"color:red\">Unicamente dígitos.</li>"
												}

											},
											submitHandler: function(form) {submitForm();}
										});
	}
		
	function setSelectableTableStyle(){		
		$("#dataTableFacturacion tbody tr").live('click',function(evt1) {
					if ($(this).hasClass('row_selected')) {
						$(this).removeClass('row_selected');						
					} else {
						$('tr.row_selected').removeClass('row_selected');
						$(this).addClass('row_selected');											
					}
				});
	}
	
	function setSelectableTable(){		
		$("#dataTableFacturacion tbody tr").live('dblclick',function(evt1) {
					if ($(this).hasClass('row_selected')) {
						$(this).removeClass('row_selected');						
					} else {
						$('tr.row_selected').removeClass('row_selected');
						$(this).addClass('row_selected');
						var nTr = $(this).parents('tr')[0];
						var aData = oTable.fnGetData( nTr );
						seleccionado = oTable.fnGetPosition(evt1.target.parentNode);						
						//alert(aData[seleccionado][2]);
						agregarFactura(aData[seleccionado][2]);
					}
				});
	}


	function loading() {
		$('#loadingDiv').hide() // hide it initially
		.ajaxStart(function() {
			$("body").addClass("loading");
			$(this).show();
		}).ajaxStop(function() {
			$("body").removeClass("loading");
			$(this).hide();
		});
	}
	
	function refreshTable() {
		oTable = $('#dataTableFacturacion').dataTable(
				{
					bRetrive : false,
					bPaginate : true,
					bDestroy : true,
					bLengthChange : false,
					iDisplayLength : 100,
					bFilter : false,
					bSort : true,
					bInfo : false,
					bAutoWidth : false,
					oLanguage : {
						sProcessing : "Procesando... Espere Por Favor",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtered from _MAX_ total entries)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
					bServerSide : true,
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=tFondenFacturacion&qw= cidFonden="
							+ cIdFonden + " AND nIdFondenMovimiento="
							+ nIdFondenMovimiento,
					bProcessing : true,
					sPaginationType : "full_numbers",
					bJQueryUI : true,
					aaSorting : [ [ 2, "asc" ] ],
					aoColumns : [ {
						sName : "cIdFonden",
						bSearchable : true,
						bSortable : false,
						bVisible : false,
						sClass : "alignCenter",
						sWidth : "200px"
					}, {
						sName : "nIdFondenMovimiento",
						bSearchable : true,
						bSortable : false,
						bVisible : false,
						sClass : "alignCenter"
					}, {
						sName : "nIdFondenFacturacion",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter"
					}, {
						sName : "cNumero",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter"
					}, {
						sName : "nCantidad",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignLeft"
					}, {
						sName : "nImporteFactura",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter",
						fnRender: function ( o ) {
		                    		return "$"+parseFloat(o.aData[ o.iDataColumn ]).toFixed(2);
		               		 },
                		bUseRendered: false
					} ]
				});						
	};

	function submitForm(showMsg) {
		$.ajaxSetup({async: false});	
		if($("#formFondenMovimiento").valid()){		
			$.post("../FondenMovimiento/saveFondenMovimiento", $(
					"#formFondenMovimiento").serialize(), function(data) {
				//cIdFonden = $("#cidFonden").val();
				//nIdFondenMovimiento = $("nidFondenMovimiento").val();
				if(showMsg==true){
					alert(data[0].descripcion);
				}
				parent.document.getElementById("pb_send").disabled=false;
				document.getElementById("divFacturacion").style.display = "";				
			}, "JSON");	
			return true;					
		}else{
			return false;
		}		

	};

	function refreshFacturacion() {
		
		refreshTable();
	};
	
	function popup(url,ancho,alto) {				 
		var posicion_x=parseInt((screen.width/2)-(ancho/2));		
		var posicion_y=parseInt((screen.height/2)-(alto/2));
		var settings = "width="+ancho+",height="+alto+",menubar=0,toolbar=0,directories=0,scrollbars=no,resizable=no,left="+posicion_x+",top="+posicion_y+"";				
		window.open(url,"",settings);
	} 
	
	function onSubmit(){
		var p = window.parent;
		p.gestion.setFolio( $("#FOLIO").val() );
		p.gestion.setOperador( $("#OPERADOR").val() );
		p.gestion.setFechaDocumento( $("#fechaToday").val() );
		p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
		p.gestion.setConceptoMov("Apertura Cuentas Bancarias");
		p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
		p.gestion.setFechaApCont( $("#fechaToday").val() );
		return submitForm(true);
	}
	
	function onPostDisplay(){
	
	}
	
	function onLoadPlantilla(){
	
	}
	
	function ResponsableSiguiente(id_oper){			
		switch(id_oper){
			case 1 : return "AUTORIZACION_FONDEN";			
			default : return "" ;
		}		
  		 		
  	}
  	  	
  	function OperacionSiguiente(id_oper){  		
  		 switch(id_oper){
			case 1 : return "AUTORIZACION_FONDEN";			
			default : return "" ;
		}
  	}

	function onPostDisplay(){
	
	}
	
	function onPostSubmit(id_oper){//validaciones del boton enviar
  		//var valida_doctos_requeridos = true;
  		//validaciones de documentos requeridos
  		//return valida_doctos_requeridos;
  		//return confirm("Confirmar que quiere avanzar a la siguiente operación.");
  		return true;
  	}
  	
  	
	function agregarFactura(seleccionado) {
		var params = "?";
		params += "cidFonden=" + $("#cidFonden").val();
		params += "&nidFondenMovimiento=" + $("#nidFondenMovimiento").val();
		if(seleccionado!=null){
			params += "&nidFondenFacturacion=" + seleccionado;
		}				
		popup("FondenFacturacion.jsp" + params, 800, 400);
	}
</script>
</head>
<body id="dt_example">
	<div id="loadingDiv" class="modal"><img src="imagenes/wait24trans.gif"></div>
	<div id="container" class="container SyCData"
		style="width:800px; align:center">
		<fieldset class="display">		
			<legend>FONDEN Movimiento</legend>
			<form id="formFondenMovimiento">
				<table id="container_0" class="display">
					<tr>
						<td align="right">FOLIO :</td>
						<td align="left"><input style="text-align: center;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getcFolio() : c.getFolio()%>"
							name="cFolio" type="text" id="cFolio" size="10"
							readonly="readonly" /></td>
					</tr>	
					<tr>
						<td align="right">Año :</td>
						<td align="left"><input style="text-align: center;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getCidFonden() : cidFonde%>"
							name="cidFonden" type="text" id="cidFonden" size="4"
							readonly="readonly" /></td>
					</tr>						
					<tr>
						<td align="right">Num. Caso :</td>
						<td align="left"><input style="text-align: center;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getnNumCaso() : ""%>"
							name="nNumCaso" type="text" id="nNumCaso"							
							size="6" />
						</td>
					</tr>
					<tr>
						<td align="right">Concepto:</td>
						<td colspan="3" align="left"><textarea 	style="height: 50px; width: 300px"						
							name="cconcepto" id="cconcepto">
								<%=fondenMovimiento != null ? fondenMovimiento.getCconcepto() : ""%>
							</textarea>
						</td>
					</tr>
					<tr>
						<td align="right">Moneda : </td>
						<td align="left">
						<select name="cIdMoneda" id="cIdMoneda">
							<c:forEach items="<%= fondenMonedas %>" var="var">
								<% FondenMoneda moneda = (FondenMoneda)pageContext.getAttribute("var"); %>
								<option 
									value="<%=moneda.getCidMoneda()%>"
									<%	String selected = "";
										if(fondenMovimiento!=null && moneda.getCidMoneda() == fondenMovimiento.getcIdMoneda()){
											selected = "selected=\"selected\"";
										}else if(fondenMovimiento == null && moneda.getCidMoneda().equals("MXN")){
											selected = "selected=\"selected\"";
										}%>
									<%=selected%>>
										<%=moneda.getCdescripcion() %>
								</option>						
							</c:forEach>
						</select>						
						</td>
					</tr>
					
					<tr>
						<td align="right"><label id="lTCam" name="lTCam">Tipo de cambio:</label></td>
						<td align="left"><input  style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getnTipoCambio() : "1.00"%>"
							name="nTipoCambio" type="text" id="nTipoCambio" size="10" readonly />
						</td>
					</tr>
					<tr>
						<td align="right">TECHO :</td>
						<td align="left"><input  style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getnTechoDef() : ""%>"
							name="nTechoDef" onblur="format()" onchange="format()" type="text" id="nTechoDef" size="25" />
						</td>
					</tr>					
					<tr>
						<td align="right">Precio unitario:</td>
						<td align="left"><input  style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNprecioUnitario() : ""%>"
							name="nprecioUnitario" onblur="format()" onchange="format()" type="text" id="nprecioUnitario" size="25" />
						</td>
						<td align="right">Cantidad:</td>
						<td align="left"><input  style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNcantidad() : ""%>"
							name="ncantidad" type="text" id="ncantidad" size="10" /></td>
						<td align="right">Importe:</td>
						<td align="left"><input  style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNimporte(): ""%>"
							name="nimporte" onblur="format()" onchange="format()" type="text" id="nimporte" size="25" readonly /></td>
					</tr>
					<tr>
						<td align="right">RFC:</td>
						<td align="left"><input
							value="<%=fondenMovimiento != null ? fondenMovimiento.getCrfc(): ""%>"
							name="crfc" type="text" id="crfc" size="25" /></td>
						<td align="right">Proveedor:</td>
						<td align="left"><input
							value="<%=fondenMovimiento!=null?fondenMovimiento.getCproveedor():""%>"
							name="cproveedor" type="text" id="cproveedor"
							size="25" /></td>
					</tr>
					<tr>
						<td align="right">Num. Pedido:</td>
						<td align="left"><input  style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getCnumPedido() : ""%>"
							name="cnumPedido" type="text" id="cnumPedido" size="25" /></td>
						<td align="right">Neto Pedido:</td>
						<td align="left"><input style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNnetoPedido() : ""%>"
							name="nnetoPedido" type="text" id="nnetoPedido" size="10" /></td>
					</tr>
					<tr>
						<td align="right">Precio:</td>
						<td align="left"><input style="text-align: right;"
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNprecio(): ""%>"
							name="nprecio" onblur="format()" onchange="format()" type="text" id="nprecio" size="25" />
						</td>
						<td align="right">Cantidad total:</td>
						<td align="left"><input
							value="<%=fondenMovimiento != null ? fondenMovimiento.getnCantidadTotal(): ""%>"
							name="nCantidadTotal" type="text" id="nCantidadTotal" size="25" /></td>
						<td align="right">Total:</td>
						<td align="left"><input readonly
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNtotal(): ""%>"
							name="ntotal" onblur="format()" onchange="format()" type="text" id="ntotal"
							size="25" /></td>
					</tr>					
				</table>
				<table style="display:none"  class="display">
									
					<tr>
						<td align="right">activo:</td>
						<td align="left"><input
							value="<%=fondenMovimiento!=null?fondenMovimiento.getActivo():"S"%>"
							name="activo" type="text" id="activo" 
							size="25" /></td>
					</tr>
					<tr>
						<td align="right">idGabinete:</td>
						<td align="left"><input
							value="<%=fondenMovimiento!=null?fondenMovimiento.getIdGabinete():"0"%>"
							name="idGabinete" type="text" id="idGabinete" 
							size="25" /></td>
					</tr>
					<tr>
						<td>centroConta<input type="text" id="cCentroContable" name="cCentroContable" value="<%=fondenMovimiento!=null?fondenMovimiento.getcCentroContable():cCentroContable%>" /></td>
						<td>UR<input type="text" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=fondenMovimiento!=null?fondenMovimiento.getcUnidadResponsable():cUR%>" /></td>
							
					</tr>
					<tr>
						<td align="right">id_Caso :</td>
						<td align="left"><input
							value="<%=fondenMovimiento != null ? fondenMovimiento.getNidFondenMovimiento() : c.getIdCaso()%>"
							name="nidFondenMovimiento" type="text" id="nidFondenMovimiento"
							readonly="readonly"
							size="25" />
						</td>
					</tr>
					<tr>
						<td align="right">nIdFondenMovEstatus :</td>
						<td align="left"><input
							value="<%=fondenMovimiento != null ? fondenMovimiento.getnIdFondenMovEstatus() : "1"%>"
							name="nIdFondenMovEstatus" type="text" id="nIdFondenMovEstatus"
							readonly="readonly"
							size="25" />
						</td>
					</tr>					
				</table>
				<input type="hidden" name="cFolio" id="cFolio" value="<%=c.getFolio()%>" />
			</form>		
			</fieldset>
		<div id="divFacturacion" style="display:<%=fondenMovimiento!=null?"":"none"%>">
			<fieldset>
				<input type="button" onclick="agregarFactura(null)" value="Agregar factura"/>		
				<legend>Facturación</legend>			
				<table id="dataTableFacturacion" class="display" align="center"	width="900px">
					<thead>
						<tr>
							<th>cIdFonden</th>
							<th>nIdFondenMovimiento</th>
							<th>ID Facturacion</th>
							<th>Número</th>
							<th>Cantidad</th>
							<th>Importe</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>			
			</fieldset>
		</div>
		<form>
		<table style="display:none" id="tblGuardar">
	   				<tr>
			   				<td>id_oper<input type="text" name="id_oper" id="id_oper" value="<%=id_oper%>" /></td>
			   				<td>nFolio<input type="text" name="nFolio" id="nFolio" /></td>
			   				<td>renglon<input type="text" id="nDocRenglon" name="nDocRenglon" /></td>
			   				<td>nFolioCaso<input type="text" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /></td>
					</tr>
					<tr>		
							<td>operador<input type="text" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /></td>
							<td>fecha<input type="text" name="fechaToday" id="fechaToday" value="<%=today%>" /></td>
							<td>ejercicio<input type="text" name="cEjercicio" id="cEjercicio"  /></td>
							<td>cDocumento<input type="text" id="cDocumento" name="cDocumento" value="<%=c.getIdCaso()%>" /></td>
					</tr>	
					<tr>
							<td>caso<input type="text" id="caso" name="caso" value="<%=c.getCasoOperacion(0).getOperacion()%>" /></td>
							<!-- td><input type="text" value="DIRECTO" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" /></td -->
							<td>centroConta<input type="text" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /></td>
							<td>UR<input type="text" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /></td>
							<td>login<input type="text" id="usuarioLogin" name="usuarioLogin" value="<%=u_login%>" /></td>
			   		</tr>					   		
				</table>
				</form>
	</div>	
</body>
</html>
