<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.syc.gestion.core.*"%>
<%@ page import="com.syc.gestion.servlet.*"%>
<%@ page import="com.syc.gestion.util.*"%>
<%@ page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@ page import="com.syc.gestion.CasoBusinessLogic"%>
<%@ page import="com.syc.contable.RetencionBusinessLogic"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="java.io.File"%>
<%@ page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page import="com.syc.contable.core.RetencionEncabezado"%>
<%@ page import="com.syc.contable.core.RetencionDetalle"%>
<%@ page import="com.syc.contable.core.Retencion"%>
<%!private static Logger log = Logger
			.getLogger("com.syc.plantillas.casos.retencion.jsp");%>
<%
	
	Caso caso = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (caso == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String cUsrLog = "";
	String mensaje = "";
	String aEjercicioFiscal = EjercicioFiscalBusinessLogic.getEjercicioFiscal();
	String today = Util.getTodayESMX();
		
	Empleado empleado = new Empleado();
	EmpleadoBusinessLogic empleadobl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	RetencionBusinessLogic retencion = new RetencionBusinessLogic(GestionInterface.ATT_CONEXION);
	EmpleadoArea empleadoarea = new EmpleadoArea();
	CasoBusinessLogic casobl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	
	boolean esConsulta = ( caso == null? false : ( caso.getCasoOperacion(0) == null? false : (  caso.getCasoOperacion(0).getOperacion() == null ? false : ( "CONSULTA_RETENCION".equalsIgnoreCase( caso.getCasoOperacion(0).getOperacion().getNombre() ) )  ) )  );
	String operacionActual = ( caso != null? (  caso.getCasoOperacion(0) != null? (  caso.getCasoOperacion(0).getOperacion() != null? caso.getCasoOperacion(0).getOperacion().getNombre() : ""   ) : "" ) : "" ) ;
	
	String cDocumento = (caso.getTipoCaso().getGavetaAsociada());	
	int id_oper = -1;
	boolean reload = true;
	
	empleado.setClaveUsuario(usuario.getLogin());
	empleado = empleadobl.getEmpleado(empleado);
	empleadoarea.setId(empleado.getClaveArea());
	empleadoarea = empleadobl.getEmpleadoArea(empleadoarea);
	Retencion  retencionCapturada = null;
	
	if( session.getAttribute("exito") != null ){
		if( (Boolean) session.getAttribute("exito") ){
			retencionCapturada = (Retencion) session.getAttribute( "RETENCION" );
			
			session.removeAttribute("RETENCION");
		}else{
			String err = (String) session.getAttribute( "ERR_MSG" );
			if( StringUtils.isBlank(  err ) ){
				mensaje = "No se encontro informacion de la CxP";
			}else{
				mensaje = err;
				session.removeAttribute("ERR_MSG");
			}
		}
	}
	
    int folio = new Integer(caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)).intValue();

	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = caso.getCasoOperacion(0).getIdOperacion();

 	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	
	RetencionEncabezado rec = retencion.getRetencionEncabezado(folio);
	List<RetencionDetalle> rds = retencion.getRetencionDetalle(folio);
    boolean existeDetalle = false; //checamos si ya existe un detalle cargado: si hay lo agregamos al grid en paso 1; sino es paso 1, usamos la vista; si no hay nada, agregamos.

	if(!rds.isEmpty())
	    existeDetalle=true;
    
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Retenciones</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<style type="text/css" title="currentStyle">
@import "../Generador/css/demo_page.css";
@import "../Generador/css/demo_table_jui.css";
@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
<style>
.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}

.money {
	text-align: right;
}

.centro{
	text-align: center;
}

.alignRight {
	text-align: right;
}

.alignCenter {
	text-align: center;
}

div#dialog-form fieldset {
	padding: 0;
	border: 0;
	margin-top: 0px;
}

div#dialog-form label {
	display: block;
}

div#dialog-form input {
	display: block;
}


div#accounts-contain table td,div#accounts-contain table th {
	border: 1px solid #eee;
	padding: .6em 10px;
	text-align: left;
}

div#users-contain table td,div#users-contain table th {
	border: 1px solid #eee;
	padding: .6em 10px;
	text-align: left;
}

.ui-dialog .ui-state-error {
	padding: .3em;
}

</style>
<script type="text/javascript" src="../Generador/js/jquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/catalogo/general.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/retencion.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" charset="utf-8">
		var oTable;
		var esConsulta = <%=esConsulta%>;
		var operacionActual = "<%=operacionActual%>";
		var id_operacion = "<%=id_oper%>";
		var arrCXP = new Array();

	function onLoadPlantilla(id_oper){
		$("#contrarrecibo").val('<%=(retencionCapturada != null && retencionCapturada.getEncabezado() != null)?retencionCapturada.getEncabezado().getCaNoContrarrecibo():""%>');
		$("#cEjercicio_C").val('<%=aEjercicioFiscal%>');
		$("#CXP").val('<%=(retencionCapturada != null && retencionCapturada.getEncabezado() != null)?retencionCapturada.getEncabezado().getCaNoContrarrecibo():""%>');
			
		queryFormPost("fExpRead", {async: false });
		queryFormPost("fAplRead", {async: false });
		
		
		
		if(id_oper==2){
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_cancel").disabled=false;
		}
		if(id_oper==3 ){
			if (parent.document.getElementById("pb_send"))
				parent.document.getElementById("pb_send").disabled=true;
			if (parent.document.getElementById("pb_cancel"))
				parent.document.getElementById("pb_cancel").disabled=false;
		}
	
	
		if(id_oper == 1){
			
			queryFormPost("tEsIPReadC",{async: false });
	
			if($("#PagoEsIP").val() == "4"){
				$("#EsIP").attr("checked", true);
				$("#cEsIP").val("S");
			}else 
				$("#cEsIP").val("N");
		}					
	
		var rw = $('#tblPagadoFiltrado').dataTable().fnGetData();

	}		
	
	function inicio(recargar){
		
		if(recargar){
			var msg = $('#mensaje').val();
			if( msg != "" && msg != null ){
				Swal.fire("Revise",msg, "info");
				parent.document.getElementById("pb_send").disabled=true;
			}else{
				$('#mensaje').val("");
			}
			$("#cIdRFC").val("");
			$('#CatMovimientoRetencion').val("");
			$('#cConcepto').val("");
			$('#nombre').val("");
			$('#compromisoSICOP').val("");
			$('#nOrigenPPTO').val("");
			
			$("#tblPagadoFiltrado tbody tr td:eq(0)").click();
			
		} else{
			var msg = $('#mensaje').val();
			if( msg != "" && msg != null ){
				parent.document.getElementById("pb_send").disabled=true;
				Swal.fire("Revise",msg, "info");
				//parent.document.getElementById("pb_save").click();
				$('#tblPagadoFiltrado thead tr td:eq(0)').click();
				
			}else{
				$('#mensaje').val("");
			}
		}
	}
	
	function permite(elEvento, permitidos) {
		var numeros = "0123456789";
		var caracteres = " abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ.";
		var numeros_caracteres = numeros + caracteres;
		var decimal = numeros + ".";
		var teclas_especiales = [8];
		switch(permitidos) {
			case 'num':
				permitidos = numeros;
				break;
			case 'car':
				permitidos = caracteres;
				break;
			case 'num_car':
				permitidos = numeros_caracteres;
				break;
			case 'dec':
				permitidos = decimal;
				break;
		}
		var evento = elEvento || window.event;
		var codigoCaracter = evento.charCode || evento.keyCode;
		var caracter = String.fromCharCode(codigoCaracter);
		var tecla_especial = false;
		for(var i in teclas_especiales) {
			if(codigoCaracter == teclas_especiales[i]) {
				tecla_especial = true;
				break;
			}
		}
		return permitidos.indexOf(caracter) != -1 || tecla_especial;
	}
	
	function buscarCLC(){
		
		if ($("#EsIP").attr("checked"))
			$("#cEsIP").val("S");
		else 
			$("#cEsIP").val("N");
		
		
		$('#filtroRetencion').submit();				
								
	}

	function cambiaEsIP (){
		if ($('#EsIP').attr('checked') )
			$("#cEsIP").val("S");
		else
			$("#cEsIP").val("N");
		
	}

	function ResponsableSiguiente(id_oper){
		if(id_oper==1)
			return "autoriza_retencion";

		if(id_oper==2){
			return "consulta_retencion";
		}
	}

	function OperacionSiguiente(id_oper){
	
		if(id_oper==1)
			return "autoriza_retencion";

		if(id_oper==2){
				return "consulta_retencion";
		}	
		
	}
	
	function onPostDisplay(){
		guardaExp();
	}

	
	$(document).ready(function(){
			var p = window.parent;

			$("input.AyudaSyC").subIniciaDlg();
			
		if(<%=rec != null%>){
			$("#cIdRFC").val('<%=rec!=null &&rec.getcIdRFC()!=null?rec.getcIdRFC():""%>');
			$("#nombre").val('<%=rec!=null &&rec.getNOMBRE()!=null?rec.getNOMBRE():""%>');
			$("#compromisoSICOP").val('<%=rec!=null &&rec.getnCompromisoSICOP()!=null?rec.getnCompromisoSICOP():""%>');
			$("#nOrigenPPTO").val('<%=rec!=null && rec.getnOrigenPPTO()!=0? rec.getnOrigenPPTO():""%>');
			$("#tipoPago").val('<%=rec!=null && rec.getTipoPago() != null? rec.getTipoPago():""%>');
			$("#cConcepto").val('<%=rec!= null && rec.getcConcepto() != null?rec.getcConcepto():""%>');
			$("#cPasivo").val('<%=rec!= null && rec.getcPasivo_C() != null?rec.getcPasivo_C():""%>');
			$("#nFolioPago").val('<%=rec!= null && rec.getnFolioPago() != 0?rec.getnFolioPago():""%>');	
			$("#nFolioSICOP").val('<%=rec!= null && rec.getnFolioSICOP() != 0?rec.getnFolioSICOP():""%>');
			$("#CXP").val('<%=rec!= null && rec.getCaNoContrarrecibo()!= null?rec.getCaNoContrarrecibo():""%>');	
			$("#filtroRec").hide();
		}else {
			$("#cIdRFC").val('<%=retencionCapturada!=null &&retencionCapturada.getEncabezado()!=null?retencionCapturada.getEncabezado().getcIdRFC():""%>');
			$("#nombre").val('<%=retencionCapturada!=null &&retencionCapturada.getEncabezado()!=null?retencionCapturada.getEncabezado().getNOMBRE():""%>');
			$("#compromisoSICOP").val('<%=retencionCapturada!=null && retencionCapturada.getEncabezado()!=null ? retencionCapturada.getEncabezado().getnCompromisoSICOP():""%>');
			$("#nOrigenPPTO").val('<%=retencionCapturada!=null && retencionCapturada.getEncabezado()!=null ? retencionCapturada.getEncabezado().getnOrigenPPTO():""%>');
			$("#tipoPago").val('<%=retencionCapturada != null && retencionCapturada.getEncabezado() != null? retencionCapturada.getEncabezado().getTipoPago():""%>');
			$("#cConcepto").val('<%=retencionCapturada != null && retencionCapturada.getEncabezado() != null?retencionCapturada.getEncabezado().getcConcepto():""%>');
			$("#cPasivo").val('<%=retencionCapturada != null && retencionCapturada.getEncabezado() != null?retencionCapturada.getEncabezado().getcPasivo_C():""%>');
			$("#nFolioPago").val('<%=retencionCapturada != null && retencionCapturada.getEncabezado() != null?retencionCapturada.getEncabezado().getnFolioPago():""%>');
			$("#CXP").val('<%=retencionCapturada!=null && retencionCapturada.getEncabezado()!=null ? retencionCapturada.getEncabezado().getCaNoContrarrecibo():""%>');
		}
		
			queryFormPost("fExpRead", {async: false });
			queryFormPost("fAplRead", {async: false });
		
			
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 900,
      			heigth: 2900
    		});
	
      		$('#dialogMensaje').dialog({
      			autoOpen: false,
      			width: 900,
      			heigth: 2900
    		});
			
			$('#dialogMotor').dialog({
		      	autoOpen: false,
	  			width: 900,
	 			heigth: 2900
	    	}); 
	    	
			$('#dialogSolicitud').dialog({
		      	autoOpen: false,
	  			width: 900,
	 			heigth: 2900
	    	});
			
			$("#fExp").datepicker({
				showOn:"button",
				dateFormat:"dd/mm/yy",
				buttonImage:"../Generador/images/calendar.gif",
				buttonImageOnly:true
			});
	
			$("#fApl").datepicker({
				showOn:"button",
				dateFormat:"dd/mm/yy",
				buttonImage:"../Generador/images/calendar.gif",
				buttonImageOnly:true
			});

			querySelectPost("TipoPresupuestoReteRead","nOrigenPPTO");
	
			$('#CatMovimientoRetencion').change(function() {
				querySelectPost("TipoPresupuestoReteRead","nOrigenPPTO");
			});
			
			
				    oTable = $("#tblPagadoFiltrado").dataTable({
								"bPaginate": false,
								"bLengthChange": true,
								"bFilter": true,
								"bSort": true,
								"bInfo": true,
								"bAutoWidth": true,
								"sScrollY": "300px",
								//"sScrollYInner": "100%",
								"bJQueryUI": true,
								"bRetrive" : true,
								"bDestroy" : true,
								"sPaginationType": "full_numbers",
								"sScrollX": "100%",
								"bScrollCollapse": true,	
								"bServerSide": false,
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
									sSearch: "Filtro:",
									oPaginate: {
										sFirst:    "Primero",
										sPrevious: "Ant.",
										sNext:     "Sigte.",
										sLast:     "&Uacute;ltimo"
									}
								}
								
						});
					
				$("#tblPagadoFiltrado tbody").click(function(event) {
					
					$(oTable.fnSettings().aoData).each(
						function (){
							$(this.nTr).removeClass('row_selected');
						});
						
						$(event.target.parentNode).addClass('row_selected');
					});
				
				//Funcion con doble click
				$("#tblPagadoFiltrado tbody").dblclick( function( e ) {
					$(oTable.fnSettings().aoData).each(
						function (){
							$(this.nTr).removeClass('row_selected');
					});
						
					$(e.target.parentNode).addClass('row_selected');

					if(id_operacion ==1)
						tblPagoDblClick(e);
										
						
				});
				
		
			$('input').each(function() {
						    var readonly = $(this).attr("readonly");
						    if(readonly && readonly.toLowerCase()!=='false') { 
						        $(this).addClass("notEditable");
						    }
			});
			
			if($("#mensaje").val() !=""){
				alert($("#mensaje").val());
			}
		creaDialogoEditar();
		creaDialogoCaptura();
		
	});
	

	function onActualizaEstatus(cTipoAutoriza){
		var strAction="retencion.jsp?id_oper="+<%=id_oper%>;
	}
	

	
	function onSubmit(id_oper){
		var p = window.parent;
		var valida_campos = false;
		try{
			if (id_oper==1){
				
				var esIP = $("#cEsIP").val();
				p.gestion.setFolio($("#folio").val());
				p.gestion.setFechaDocumento($("#fApl").val());
				p.gestion.setEjercicioFiscal($("#cEjercicio").val());
				p.gestion.setOperador($("#operador").val());
				p.gestion.setConceptoMov("Captura de retencion con FOLIO " + $("#folio").val() + ".");
				p.gestion.setMoneda("MXP");
				
				$("#idrfc").val($("#cIdRFC").val());
				$("#cxp_c").val($("#CXP").val());
				$("#nombre_c").val($("#nombre").val());
				$("#cPasivo_C").val($("#cPasivo").val());
				
				//Validar que el importe no sea cero
						$("#info").val(obtenValoresGrid());
							$.ajax({
								url: '../gstnmngr/CapturaRetencion',
								type: 'post',
								async:false,
								dataType: 'json',
								data : $("#guardarRete").serialize(),
								error: function(data) {
									alert("Ocurrio un error en el Insert. ["+ data.data_1.result +"]");
								},
								success: function(data){
									var exito = data.success;	
									if (exito == "true") {
										Swal.fire("Ok","Se guardo correctamente","success");
										valida_campos = true;
									} else {
										alert("Ocurrio un error al guardar. ["+ data.data_1.result +"]");
										//alert("La cuenta por pagar no es de Laudos ni Liquidaciones o hubo un problema de comunicacion");
									}
									
								}
							});
			}
				
			if(id_oper==2){
				
				parent.document.getElementById("pb_send").disabled=false;
				parent.document.getElementById("pb_save").disabled=true;
  				parent.document.getElementById("pb_cancel").disabled=false;
  				aplicaCont();
  			}
  			
  			if(id_oper==3){
  				parent.document.getElementById("pb_send").disabled=false;
  				parent.document.getElementById("pb_cancel").disabled=true;
  			}
			
		} catch (e) {
			window.alert("onSubmit: Error: " + e.message + " " + '<%=mensaje%>');
			return false;
		}
		return valida_campos;
	}
	

	
	function onPostSubmit(id_oper){
		if (id_oper == 2) {
			parent.execResponsable(id_oper);
			parent.execOperacion(id_oper);
		}
		return true;
		
	}
	
	function obtenValoresGrid(){
		var info="";
		$('#tblPagadoFiltrado tbody tr').each(function(idx, elm){
			info += $(this).find('td:eq(0)').html();
			info += "!" + $(this).find('td:eq(1)').html();
			info += "!" + $(this).find('td:eq(2)').html();	
			info += "!" + $(this).find('td:eq(3)').html();
			info += "!" + $(this).find('td:eq(4)').html();
			info += "!" + $(this).find('td:eq(5)').html();
			info += "!" + $(this).find('td:eq(6)').html();
			info += "!" + $(this).find('td:eq(7)').html();
			info += "!" + $(this).find('td:eq(8)').html();
			info += "!" + $(this).find('td:eq(9)').html();
			info += "!" + $(this).find('td:eq(10)').html();
			info += "!" + $(this).find('td:eq(11)').html();
			info += "!" + $(this).find('td:eq(12)').html();
			info += "!" + $(this).find('td:eq(13)').html();
			info += "!" + $(this).find('td:eq(14)').html();
			info += "!" + $(this).find('td:eq(15)').html();
			info += "!" + $(this).find('td:eq(16)').html() + "!" ;
		});
		return info;
	}
	
	function cmdImprimir(elFormato) {
		var folRet =  $("#folio").val();
		var datos = folRet.split("-")
		var swhere = "&whereFolio= and ce.nFolioRetencion= '" + datos[2] + "'";
		//Imprimir Solicitud
		window.open("../admin/SeguridadCatalogos?" 
			    + "catalogo=RETENCIONES"
				+ "&accion=run" 
				+ "&rn=" + elFormato + ".jasper" 
				+ swhere,
				"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");

	}
	
	function aplicaCont(){
		$("#dialog").dialog("close");				
		if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {
			return false;
		}
	   	var strAction="../gstnmngr/RetencionServlet";
   		$.blockUI({message: "Procesando espere ......"});
   		$.ajax({
   			datatype:"html",
			type: "POST",
			url: strAction,
			data:{accion:1, fecha:$("#fApl").val()},
			success: function (data,textStatus){
				alert("Se aplicó la solicitud correctamente");
   				$.unblockUI();
			},
			error: function(data) {
				alert("Ocurrio un error al aplicar la solicitud. " + '<%=mensaje%>');
				$.unblockUI();
			}
		});
		}
	
	function cancelarDoc(){
		if(!confirm("Enviara el Documento a Cancelar.  \n \n  ¿desea continuar?")) {
			return false;
		}
		var strAction="../gstnmngr/RetencionServlet";
	   	$.blockUI({message: "Procesando espere ......"});
	   	$.ajax({
   			datatype:"html",
			type: "POST",
			url: strAction,
			data:{accion:2},
			success: function (data,textStatus){
				//$("#mensajeMotor").val(data);
				//$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
				alert("Se cancelo la solicitud correctamente");
   				$.unblockUI();
			},
			error: function (par) {alert('<%=mensaje%>');}
		});
	}
	 
	
	function fnExportaSicop() {
		
		$.blockUI( {
			message : "Procesando espere ......"
		});
		document.ExportaSicop.submit();
		$.unblockUI();
		//$("#Exportar").attr('disabled','disabled');
		return true;
	}

	function guardaExp(){
  		parent.document.getElementById("pb_save").disabled=true;
  		parent.document.getElementById("pb_send").disabled=false;
  		parent.document.getElementById("pb_send").click();
	}
	


</script>
</head>
<body id="dt_example" class="ex_highlight" >
	<div id="container" class="container">
		<h1>Captura de retenciones</h1>
		<div id="dialogMensaje" title="Mensajes">

			<%
				if (mensaje != null && !"".equals(mensaje)) {
			%>
			<div style="border-style: double;">
				<%=mensaje%>
			</div>

			<%
				}
			%>
		</div>
		<%
			if (id_oper > 1) {
		%>
		<div class="row d-flex justify-content-center mt-2">					
			<div class="col-auto">
				<div id="divImprime" style="font-size: 18px">
					<img src="imagenes/Imprimir.png" width="33" height="28"	onClick="cmdImprimir('polizaRetencion');" class="btn btn-secondary"/>
					Imprimir
				</div>
			</div>
		</div>
		<%
			}
		%>
		<div id="filtroRec" title="Filtro por criterios de la CLC">
			<form id="filtroRetencion" name="filtroRetencion" method="POST" action="../gstnmngr/RetencionFiltrar">
				<input type="hidden" id="cDocumento" name="cDocumento" value="<%=cDocumento%>">
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>">
				<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=aEjercicioFiscal%>"> 
				<input type="hidden" id="cRamo" name="cRamo" value="16"> 
				<input type="hidden" id="cUnidad" name="cUnidad" value="RHQ"> 
				<input type="hidden" id="operador" name="operador" value="<%=caso.getCasoOperacion(0) == null ? "caso operacion nulo" : caso.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" id="fDocumento" name="fDocumento" value="<%=today%>" />
				<input type="hidden" id="contrarrecibo" name="contrarrecibo" value="">
				<input type="hidden" id="cEsIP" name="cEsIP" value="N">
				<input type="hidden" id="mtotal" name="mtotal" value =0 />
				<%
					if (id_oper == 1) {
				%>
					<div class="row">
						<div class="col-4">
						    Cuenta por pagar 
						    <div class="input-group mb-3">
							<input type="text" class="form-control" id="CXP" name="CXP" size="14" maxlength="14" />
							<input type="button" id="validar" name="buscar" value="Buscar" onClick="buscarCLC();" class="btn btn-secondary"/>
							</div>
						</div>
						
					</div>
				<%
					}
				%>
			</form>
			
		</div>
		
		
		<!-- ----------------------------------------------- -->

		<div id="container2">
			<form id="guardarRete" name="guardarRete" method="POST" action="../gstnmngr/CapturaRetencion">
				<input type="hidden" id="mensaje" name="mensaje" value="<%=mensaje%>" />
				<input type="hidden" id="cEjercicio_C" name="cEjercicio_C" value="<%=aEjercicioFiscal%>" /> 
				<input type="hidden" id="cRamo_C" name="cRamo_C" value="<%=usuario.getU_Ramo() == null ? "" : usuario.getU_Ramo()%>" />
				<input type="hidden" id="cUnidad_C" name="cUnidad_C" value="<%=usuario.getU_UR() == null ? "" : usuario.getU_UR()%>" />
				<input type="hidden" id="operador_C" name="operador_C"  value="<%=caso.getCasoOperacion(0) == null ? "caso operacion nulo" : caso.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" id="u_login_C" name="u_login_C" value="<%=usuario.getLogin() == null ? "usuario nulo" : usuario .getLogin()%>" />
				<input type="hidden" id="cCentroContable_C" name="cCentroContable_C" value="<%=cCentroContable == null ? "centro contable nulo" : cCentroContable%>" />
				<input type="hidden" id="ID_Caso_C" name="ID_Caso_C" value="<%=caso.getIdCaso()%>" /> 
				<input type="hidden" id="cMes_C" name="cMes_C" value="10" />
				<input type="hidden" id="cPasivo_C" name="cPasivo_C"  />  
				<input type="hidden" id="cDescripcionPolizaH" name="cDescripcionPolizaH" value="<%=(retencionCapturada != null && retencionCapturada.getEncabezado() != null) ? retencionCapturada.getEncabezado().getcConcepto() : ""%>" />				 
				<input type="hidden" id="epGrid" name="epGrid" /> 
				<input type="hidden" name="info" id="info" /> 
				<input type="hidden" id="mesTemporal" name="mesTemporal" />
				<input type="hidden" name="CTAB" id="CTAB" value="" />	
				<input type="hidden" name="cxp_c" id="cxp_c" value="<%=(retencionCapturada != null && retencionCapturada.getEncabezado() != null) ? retencionCapturada.getEncabezado().getCaNoContrarrecibo() : ""%>" />
				<input type="hidden" name="idrfc" id="idrfc" value="<%=(retencionCapturada != null && retencionCapturada.getEncabezado() != null) ? retencionCapturada.getEncabezado().getcIdRFC() : ""%>" />
				<input type="hidden" name="nombre_c" id="nombre_c" value="<%=(retencionCapturada != null && retencionCapturada.getEncabezado() != null) ? retencionCapturada.getEncabezado().getNOMBRE() : ""%>" />		
				<input type="hidden" name="cEsIP" id="cEsIP" value="N" />				
				<input type="hidden" id="folioRete" name="folioRete" size=40 value="<%=folio%>"/>
			
				<input type="hidden" id="mImporteRetencion" name="mImporteRetencion" value="0">
				
				<div class="row">
					
					<div class="col-4">
						Folio
						<input type="text" class="form-control" name="folio" id="folio" size="10" readOnly="readOnly" onKeyDown="return false;" value="<%=caso.getFolio()%>" />
					</div>
					<div class="col-4"></div>
					<div class="col-4">
						Es Ingresos Propios
						<input type="checkbox" class="form-check-input" id="EsIP" name="EsIP" onclick="javscript:cambiaEsIP()"/>
					</div>
				</div>
				
				<div class="row">
					
					<div class="col-4">
						Fecha Expedici&oacute;n
						<input type="text" class="form-control" id="fExp" name="fExp" readOnly="readonly" size="10" maxlength="10" onKeyDown="return false" />
					</div>
					<div class="col-4">
						*Fecha Aplicaci&oacute;n
						<input type="text" class="form-control" id="fApl" name="fApl" />
					</div>
					<div class="col-4">
						Tipo Pago
						<input type="text" class="form-control" id="tipoPago" name="tipoPago" size = "15" readOnly="readonly" />
					</div>
				</div>
				<div class="row">
					
					<div class="col-md-4">
						RFC
						<input type="text" class="form-control" id="cIdRFC" name="cIdRFC" readOnly="readonly" size="10" maxlength="10" onKeyDown="return false" />
					</div>
					<div class="col-md-8">
						Nombre
						<input type="text" class="form-control" id="nombre" name="nombre" size= "73" readOnly onKeyDown="return false"/>
					</div>
				</div>
				<div class="row">
					<div class="col-md-1">
						Movto.
						<input type="text" class="form-control" id="CatMovimientoRetencion" name="CatMovimientoRetencion"  readOnly size="5" value = "N"/>
					</div>
					<div class="col-2">
						Origen Ppto
						<input type="text" class="form-control" id="nOrigenPPTO" name="nOrigenPPTO" size= "10" readOnly onKeyDown="return false"/>
					</div>
					<div class="col-3">
						No. Compromiso
						<input type="text" class="form-control" id="compromisoSICOP" name="compromisoSICOP"  size = "15" />
					</div>
					<div class="col-2">
						Folio del Pago
						<input type="text" class="form-control" id="nFolioPago" name="nFolioPago" size= "10" readOnly />
					</div>
					<div class="col-2">
						id Pasivo
						<input type="text" class="form-control" id="cPasivo" name="cPasivo"  size = "10" readOnly/>
					</div>
					<div class="col-2">
						Folio SICOP
						<input class="form-control" id="nFolioSICOP" name="nFolioSICOP"  size = "10" readOnly/>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						Concepto
						<textarea class="form-control" id="cConcepto" name="cConcepto" rows="3" cols="70" onkeypress="return event.keyCode!=13"></textarea>
					</div>
					
				</div>
				<div>
					<fieldset>
						 <% if(id_oper ==1) { %>
						 <span>
						 <label	style="font-weight:bold; font-size: 10px; text-align: right;  width: 100%">*Doble
								click en el renglon para editar </label>
							</span>
						<% } %>
						<div id="demo_ui">
							<table id="tblPagadoFiltrado" class="display">
							<thead>
									<tr>
										<th>Renglon</th>
										<th>Mes</th>
										<th>EP</th>
										<th>Importe</th>
										<th>2Millar</th>
										<th>5Millar</th>
										<th>Fletes</th>
										<th>ISR Honor</th>
										<th>ISR Arr</th>
										<th>IVA Honor</th>
										<th>IVA Arr</th>
										<th>Cedular</th>
										<th>ISR Laudos</th>
										<th>Otros</th>
										<th>IVA 6</th>
										<th>rfc</th>
									<% if(id_oper ==2) {%>	
										<th style="visibility:hidden;">Descartar</th>
									<% } else {%>
										<th>Descartar</th>
									<%  } %>
									</tr>
								</thead>
								<tbody>	
						<%
							if(  existeDetalle == false ){
								if(retencionCapturada != null){
									int indice = 0;
									List<RetencionDetalle> detalleRetencion = retencionCapturada.getDetalle();
									for (Iterator<RetencionDetalle> itDet = detalleRetencion.iterator(); itDet.hasNext(); ) {
										RetencionDetalle registro = itDet.next();
						%>			
								
									<tr >	
										<td style="text-align: center;"><%=registro.getnDocRenglon()%></td>
										<td style="text-align: center;"><%=registro.getcMes()%></td>
										<td ><%=registro.getEP()%></td>
										<td ><%=registro.getmImporte()%></td>
										<td ><%=registro.getM2Millar()%></td>
										<td ><%=registro.getmObra5()%></td>
										<td ><%=registro.getmImporteFlete4()%></td>
										<td ><%=registro.getmISRHonorarios()%></td>
										<td ><%=registro.getmISRArrenda()%></td>
										<td ><%=registro.getmImporteIvaHonorarios()%></td>
										<td ><%=registro.getmImporteIvaArrenda()%></td>
										<td ><%=registro.getmRetImpuestoCedular()%></td>
										<td ><%=registro.getmImporteISRLaudos()%></td>
										<td ><%=registro.getmISROtros()%></td>
										<td ><%=registro.getmImporteIva6()%></td>
										<td ><%=registro.getRfc()%></td>
										<td><img id= '<%="R" + indice%>'
											src="../imagenes/cancelar.gif" width="25" height="21"
											alt="Eliminar Renglon"
											onClick="descartar( '<%=registro.getnDocRenglon()%>');"/>
										</td>
									</tr>
								<% indice ++; } %> 
							<% } else 	{%>							
									
						<% } } else {
								for (Iterator<RetencionDetalle> itDet = rds.iterator(); itDet.hasNext(); ) {
									RetencionDetalle registro = itDet.next(); %>
								<tr>
										<td style="text-align: center;"><%=registro.getnDocRenglon()%></td>
										<td style="text-align: center;"><%=registro.getcMes()%></td>
										<td ><%=registro.getEP()%></td>
										<td ><%=registro.getmImporte()%></td>
										<td ><%=registro.getM2Millar()%></td>
										<td ><%=registro.getmObra5()%>
										<td ><%=registro.getmImporteFlete4()%>
										<td ><%=registro.getmISRHonorarios()%></td>
										<td ><%=registro.getmISRArrenda()%>
										<td ><%=registro.getmImporteIvaHonorarios()%></td>
										<td ><%=registro.getmImporteIvaArrenda()%>
										<td ><%=registro.getmRetImpuestoCedular()%></td>
										<td ><%=registro.getmImporteISRLaudos()%>
										<td ><%=registro.getmISROtros()%></td>
										<td ><%=registro.getmImporteIva6()%></td>
										<td ><%=registro.getRfc()%></td>
										<td style="visibility:hidden;"></td>
									</tr>
						<%  } } %>
								</tbody>
							</table>
						</div>
					</fieldset>
					
				</div>
			</form>
		</div>
		<%
			if (id_oper == 2|| id_oper == 3) {
		%>
		<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/RetencionSicop" method="POST">
			<input type="hidden" name="info" id="info" /> 
			<input type="button" id="Exportar" value="Generar Layout de SICOP" onclick="fnExportaSicop()" class="btn btn-secondary"></input>
			<input type="hidden" id="folioSICOP" name="folioSICOP"/>
			<input type="hidden" id="folioRete" name="folioRete" value="<%=folio%>"/>
			<% if (id_oper == 3) { %>
				<input type="button" id="cancelar" value="Cancelar solicitud" onclick="cancelarDoc()" class="btn btn-secondary"></input>
				<input type="button" id="captura" value="Captura Folio Rete SICOP" onclick="capturaFolioSicop()" class="btn btn-secondary"></input>
			<% } %>
		</form>
		<%
			}
		%>
	</div>
	<div id="dialogMotor"
		title="Mensajes del sistema de aplicacion de Retenciones">
		<p>Mensajes del sistema</p>
		<a rel=""></a>
		<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="120"></textarea>
	</div>
	<div id="dialogSolicitud"
		title="Captura folio de la Solicitud de Retencion ">
		<p>Folio de la solicitud de Rete</p>
		<input type="text" id="cRETE" name="cRETE" size="15">
	</div>
	<div id="EdtaRenglon">
		<form id="editaEP">
			<fieldset>
				<legend> Captura datos </legend>
				<table id="tablaEditar" align="center">				
					<tr>
						<td colspan = "4">
						EP
						<input type="text" class="form-control" id="EPR" name="EPR" size="65" readonly="readonly">
						</td>
					</tr>
					<tr>
						<td align="left">Mes:</td>
						<td align="left"><input type="text" class="form-control" id="nMesR" name="nMesR" size="3">
						<td align="left">RFC:</td>
						<td align="right"><input type="text" class="form-control" id="RFC" name="RFC" size="15" readonly="readonly">
						<td align="left" style="visibility:hidden;"><input type="text" id="nDocRenglonR" name="nDocRenglonR" size="3" readonly="readonly">
					</tr>
					<tr>
						<td> 2 al millar:</td>
						<td align="right"><input type="text" class="form-control" id="M2Millar" name="M2Millar"	size="15"  class="money" value = 0>
						</td>
						<td>5 al millar</td>
						<td align="right"><input type="text" class="form-control" id="mObra5" name="mObra5" size="15"  class="money" value = 0>
						</td>
					</tr>
					<tr>
						<td>ISR Honorarios:</td>
						<td align="right"><input type="text" class="form-control" id="mISRHonorarios" name="mISRHonorarios"	size="15"  class="money" value = 0>
						</td>
						<td>ISR Arrendamientos:</td>
						<td align="right"><input type="text" class="form-control" id="mISRArrenda" name="mISRArrenda"	size="15"  class="money" value = 0>
						</td>
					</tr>
					<tr>
						<td>IVA Honorarios:</td>
						<td align="right"><input type="text" class="form-control" id="mImporteIvaHonorarios" name="mImporteIvaHonorarios"	size="15"  class="money" value = 0>
						</td>
						<td>IVA Arrendamientos:</td>
						<td align="right"><input type="text" class="form-control" id="mImporteIvaArrenda" name="mImporteIvaArrenda"	size="15"  class="money" value = 0>
						</td>
					</tr>
					<tr>
						<td>Impuesto Cedular:</td>
						<td align="right"><input type="text" class="form-control" id="mRetImpuestoCedular" name="mRetImpuestoCedular" size="15"  class="money" value = 0>
						</td>
						<td>Autotransportes:</td>
						<td align="right"><input type="text" class="form-control" id="mImporteFlete4" name="mImporteFlete4"	size="15"  class="money" value = 0>
						</td>
					</tr>
					<tr>
						<td>ISR Laudos:</td>
						<td align="right"><input type="text" class="form-control" id="mImporteISRLaudos" name="mImporteISRLaudos"	size="15"  class="money" value = 0>
						</td>
						<td>ISR Otros:</td>
						<td align="right"><input type="text" class="form-control" id="mISROtros" name="mISROtros"	size="15"  class="money" value = 0>
						</td>
					</tr>
					<tr>
						<td>IVA 6%:</td>
						<td align="right"><input type="text" class="form-control" id="mImporteIva6" name="mImporteIva6" size="15"  class="money" value = 0>
						</td>
						
					</tr>
					<tr>
						<td>Saldo Disponible:</td>
						<td align="right">
							<input type="text" class="form-control"id="mDisponible" name="mDisponible" size="15"  class="money" value = 0>
						</td>	
					</tr>
				</table>
			</fieldset>
			
		</form>
	</div>
	
</body>
</html>