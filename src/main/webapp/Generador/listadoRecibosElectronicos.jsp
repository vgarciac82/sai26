<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.CancelaDocumento"%>


<%
	String cCentroContable = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String resultadoCancelacion = "";
	String mensaje = "";
	Connection conn = null;

	int nFolioDocumento;
	String cTipoDocumento;

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	String cUE_Usuario = "";
	cUE_Usuario = usuario.getU_UR();

	String U_LOGIN = "";
	U_LOGIN = usuario.getLogin();

	boolean esAdmin = (usuario.getRole("JEFATURA_PAGOS") != null || usuario.getRole("ADMIN_RECMAT") != null || usuario.getRole("ADMIN") != null);
	
	String resultado = (String)session.getAttribute("MSG");
	resultado = StringUtils.trimToEmpty( resultado );
	
	boolean mostrarResultado = !StringUtils.isEmpty(resultado);
	session.removeAttribute("MSG");
	
%>
<!DOCTYPE HTML>
<html>
<head>
<title>Reporte de Recibos de pago Pendientes.</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
		
			var unidadEjecutora = "<%=cUE_Usuario%>";
			var mostrarResultado = <%=mostrarResultado%>;
			var oTableLocal;
			
			$(document).ready(function() {
				querySelectPost({
					queryName : 'unidadesREPPend<%=esAdmin ? "_Admin" : ""%>_Read',
					targetObjectId : 'cUnidadEjecutoraSel',
					async : false,
					callback : function() {
						$("#cUnidadEjecutoraSel").val(unidadEjecutora);
						cargaRFC();
					}
				});
		
				$("#tabs").tabs({
					"show" : function(event, ui) {
						var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
						if( oTable.length > 0 ) {
							oTable.fnAdjustColumnSizing();
						}
					}
				});
		
				
				$("#resultDialog").dialog({
					autoOpen : mostrarResultado,
					width : "800px",
					heigth : "450px",
					modal : true
				});
				
				if( mostrarResultado )
					$("#resultadoSpan").css("display","block");
				
			});
		
		
			function enviaNotificaciones(){
			
				if( $("#cNombreResponsable").val() == "" || $("#cCorreoNotificaciones").val() == "" ){
					Swal.fire({ icon: "warning",
								text: "Los datos minimos para las notificaciones son el nombre del responsable y su correo.\nCapture estos datos para continuar"});					
					return;
				}else{
					queryFormPost({
						queryName:"responsableCobranzaCreate",
						async:false,
						callback:function(){
							$.blockUI( "Procesando Espere ..." );
							$("#frmRecibosElectronicos").submit();
						}
					});
				}
				
			}
			function cargaRFC() {
				var qName = "RFC_REPPend_Read";
				if( "" == $("#cUnidadEjecutoraSel").val() )
					qName = "RFC_REP_Todos_Pend_Read";
					
				querySelectPost({
					queryName : qName,
					targetObjectId : 'cRFC',
					async : false,
					callback : function() {
						listadoPorComprobar();
					}
				});
			}
		
			function cambiaUnidad() {
				clearSelect("cRFC");
				limpiaRespCobranza();
				$("#CapturaNotificacionDIV").css("display","none");
				cargaRFC();
			}
			
			function cambiaBeneficiario(){
			
				if( $("#cRFC").val() == "" ){
					limpiaRespCobranza();
					$("#CapturaNotificacionDIV").css("display","none");
				}else{
					queryFormPost("ResponsableCobranza_Read", {async:false} );
					$("#CapturaNotificacionDIV").css("display","block");
				}
					
				listadoPorComprobar();
			}
			
			function limpiaRespCobranza(){
				$(".cobranzaBen").each(function(){
					$(this).val("");
				});
			}
			
			function clearSelect(idSel){
				$('#' + idSel).find('option').remove().end().append(
							'<option value="-1"></option>');
			}
			
			function listadoPorComprobar() {
				var condicionUE = $("#cUnidadEjecutoraSel").val() == "" ? "" : " AND cunidadejecutora = '" + $("#cUnidadEjecutoraSel").val() + "' ";
				var condicionRFC = $("#cRFC").val() == "" ? "" : " AND RFC = '" + $("#cRFC").val() + "' ";
				oTableLocal = $('#dt_REP_PorComprobar').dataTable({
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : true,
					"bAutoWidth" : true,
					"bScrollCollapse" : true,
					"sScrollXInner": "150%", 
					"sScrollX": "100%",
					"sPaginationType" : "full_numbers",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide": true,
					"iDisplayLength": 25,
					"fnInitComplete": function() {    
						oTableLocal.fnAdjustColumnSizing();
					},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtered from _MAX_ total entries)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:"
					},			
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_tReciboElectronico_PagoFactura&qw=nComprobado = 0 " + condicionUE + condicionRFC,
					bProcessing : true,			
					aoColumns : [
						{sName : "cTipoPago", 			bSearchable : false, bSortable : false, bVisible : true},
						{sName : "nFolioPago", 			bSearchable : true, bSortable :  false, bVisible : true},
						{sName : "FechaPagadoStr", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "caNoContrarrecibo", 	bSearchable : true, bSortable :  false, bVisible : true},
						{sName : "RFC", 				bSearchable : false, bSortable : false, bVisible : true},
						{sName : "RazonSocial", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "FacturaUUID", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "MontoFactura", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "mPendienteComprobar", bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cUnidadEjecutora", 	bSearchable : false, bSortable : false, bVisible : true}
					]
				});
			}
			
			function muestraLog(){
				$("#resultDialog").dialog("open");
			}
			
			function extraerListado() {
				
				$("#UR").val($("#cUnidadEjecutoraSel").val());
				$("#cIdRFC").val($("#cRFC").val());
				document.location.href='../gstnmngr/generaExtraccionListado?UR=' + $("#cUnidadEjecutoraSel").val() + '&cIdRFC=' + $("#cRFC").val();
				
			}
	</script>

</head>
<body id="dt_example">
<br/>
	<form id="frmRecibosElectronicos" method="post" action="../notificaREPFaltante" >
		<input type="hidden" id="cCC" name="cCC" value="<%= cCentroContable%>" />
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= cUE_Usuario%>" />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= U_LOGIN%>" />
		<input type="hidden" name="cUR" id="cUR" value="" />		
		<input type="hidden" name="cMovto" id="cMovto" value="" />
		
		<div id="container" class="container" style="width: 100%">
		   
		
			<div class="card-header"> <h3> Listado de Recibos Electronicos Pagados (REP) </h3> </div>
			<hr class="mt-3"/>
						
			<div id="resultadoSpan" style="display: none">
				<a href="#" onclick="muestraLog();return false">Mostrar Log</a>
			</div>
			
			<div class="row d-flex justify-content-center">	
				
	       		<ul class="nav nav-tabs" id="list-opciones">
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="" data-bs-toggle="tab" data-bs-target="#tabs-1-temp" type="button" role="tab" aria-controls="tabs-temp" aria-selected="true">Por Comprobar</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="" data-bs-toggle="tab" data-bs-target="#tabs-2-version" type="button" role="tab" aria-controls="tabs-version" aria-selected="false">Comprobados</button>
		            </li>			           				           
		    	</ul>
				
				<div class="tab-content mt-3" id="tabContent">		
					<div class="tab-pane fade show active" id="tabs-1-temp" role="tabpanel" aria-labelledby="tabs-temp">						
						<jsp:include page="listaREPPorComprobar.jsp"></jsp:include>
					</div>
					
					<div class="tab-pane fade" id="tabs-2-version" role="tabpanel" aria-labelledby="tabs-version">
						<h5> Recibos Electronicos Comprobados </h5>
						<hr class="mt-3"/>
						
						<jsp:include page="listaREPComprobados.jsp"></jsp:include>							
					</div>
					
					<div id="resultDialog">							
						<h5> Resultados </h5>
						<hr class="mt-3"/>
						
						<textarea rows="20" cols="100" class="form-control"><%=resultado%></textarea>							
					</div>
					
				</div> <!-- FIN class="tab-content mt-3" -->
			</div> <!-- FIN class="row" -->			
		</div> <!-- FIN class="container" -->
	</form>
	
	</body>
</html>
