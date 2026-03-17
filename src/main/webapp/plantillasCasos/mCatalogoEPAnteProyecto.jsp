<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	ArrayList arrmEstatusRechazo = new ArrayList();
	String cMensaje="";
	String mensajeVAdec="";
	String cUnidadResponsable="";
	String cEjercicoFiscal="";
	boolean bErrorTecho=false;
	boolean cGrupoUSR=false;
	int i=0;
	int iarrmTecho=0; 
	String cMoey="";
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	AnteProyectoBusinessLogic anteProy = new AnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
	AdecuacionBusinessLogic   adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
    cGrupoUSR=(usuario.getGrupos() != null && usuario.getGrupos().containsKey("AUTORIZADOR_ANTEPROYECTO")) ? true : false;

	/* recupera datos de techos */
	
	try{
		cEjercicoFiscal=adecProy.obtenEjercicioFiscal();
		
	}finally{
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Creación de Ante Proyecto</title>

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

		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker-es.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

		<script type="text/javascript" charset="utf-8">

		$(document).ready(
			function(){
				$('.currency').blur(function(){
					$('.currency').formatCurrency();
				});
				/* Init the table */
				oTableTechos = $('#grdProgPresupUE').dataTable( );

//------------------------------------------------------------Ejecutora
				 $('#grdAnteProyecto').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": true,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdAnteProyecto tbody").click(function(event) {
					$(oTableDANTEP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableDANTEP.fnGetPosition( this.nTr );
					var aData = oTableDANTEP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdAnteProyecto tbody").dblclick(function(event) {
					$(oTableDANTEP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableDANTEP.fnGetPosition( this.nTr );
						var aData = oTableDANTEP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableDANTEP.fnGetPosition( event.target.parentNode );
					var aData = oTableDANTEP.fnGetData( aPos );
				});
				//$('#grdAnteProyecto thead tr th:eq(1)').click(); 
				
//--------
				$('#grdMovimientos').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );

				$('#grdPTGUE').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );

//------------------------------------------------------------Ejecutora
				 $('#grdUniEje').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdUniEje tbody").click(function(event) {
					$(oTableUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUE.fnGetPosition( this.nTr );
					var aData = oTableUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdUniEje tbody").dblclick(function(event) {
					$(oTableUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUE.fnGetPosition( this.nTr );
						var aData = oTableUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableUE.fnGetPosition( event.target.parentNode );
					var aData = oTableUE.fnGetData( aPos );
				});
//---------------------------------------------------------Normativa				
				 $('#grdUnidadNormativa').dataTable({
					"iDisplayLength": 20,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdUnidadNormativa tbody").click(function(event) {
					$(oTableUN.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUN.fnGetPosition( this.nTr );
					var aData = oTableUN.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdUnidadNormativa tbody").dblclick(function(event) {
					$(oTableUN.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUN.fnGetPosition( this.nTr );
					var aData = oTableUN.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableUN.fnGetPosition( event.target.parentNode );
					var aData = oTableUN.fnGetData( aPos );
				});
				//grdProgPresupUE
//---------------------------------------------------------ProgramaPresupuestario				
				$('#grdProgPresupUE').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": "150px",
					"sScrollX": "400%",
					"sScrollXInner": "300%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdProgPresupUE tbody").click(function(event) {
					$(oTableUEPP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUEPP.fnGetPosition( this.nTr );
					var aData = oTableUEPP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresupUE tbody").dblclick(function(event) {
					$(oTableUEPP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUEPP.fnGetPosition( this.nTr );
					var aData = oTableUEPP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableUEPP.fnGetPosition( event.target.parentNode );
					var aData = oTableUEPP.fnGetData( aPos );
				});
//---------------------------------------------------------ProgramaPresupuestario				
				$('#grdProgPresup').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdProgPresup tbody").click(function(event) {
					$(oTablePP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTablePP.fnGetPosition( this.nTr );
					var aData = oTablePP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresup tbody").dblclick(function(event) {
					$(oTablePP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTablePP.fnGetPosition( this.nTr );
					var aData = oTablePP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTablePP.fnGetPosition( event.target.parentNode );
					var aData = oTablePP.fnGetData( aPos );
				});
//---------------------------------------------------------Partida				
			$('#grdPartida').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				
//---------------------------------------------
			$('#grdRetencion').dataTable(
				{
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
				$("#grdPTGUE tbody").click(function(event) {
					$(grdPTGUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = grdPTGUE.fnGetPosition( this.nTr );
						var aData = grdPTGUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				
				$("#grdPTGUE tbody").dblclick(function(event) {
					$(grdPTGUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = grdPTGUE.fnGetPosition( this.nTr );
						var aData = grdPTGUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos = grdPTGUE.fnGetPosition( event.target.parentNode );
					var aData = grdPTGUE.fnGetData( aPos );
					quitaFormato();
				});
				$("#grdMovimientos tbody").click(function(event) {
					$(oTableMov.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTableMov.fnGetPosition( this.nTr );
						var aData = oTableMov.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				
				$("#grdMovimientos tbody").dblclick(function(event) {
					$(oTableMov.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTableMov.fnGetPosition( this.nTr );
					var aData = oTableMov.fnGetData( aPos[0] );

					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos = oTableMov.fnGetPosition( event.target.parentNode );
					var aData = oTableMov.fnGetData( aPos );
					quitaFormato();
				});

				oTableMov = $('#grdMovimientos').dataTable( );

				$("#grdProgPresup tbody").click(function(event) {
					$(oTablePP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePP.fnGetPosition( this.nTr );
					var aData = oTablePP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresup tbody").dblclick(function(event) {
				});
//-----------------------------------
				/* Add a click handler to the rows - this could be used as a callback */
				$("#grdPartidaUE tbody").click(function(event) {
					$(oTablePAUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePAUE.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablePAUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdPartidaUE tbody").dblclick(function(event) {
				});

//-----------------------------------
				/* Add a click handler to the rows - this could be used as a callback */
				$("#grdRetencion tbody").click(function(event) {
					$(oTablePA.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePA.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablePA.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdRetencion tbody").dblclick(function(event) {
				});

				$("input.AyudaSyC").subIniciaDlg();
			
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			
				$('#grdProgPresupUE').dataTable({
					"iDisplayLength": 20,
					sScrollY: "150px",
					sScrollX: "400px",
					sScrollXInner: "100%",
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
					});
			});

		function carga(){
	/*		if (< %=iPP%> > 0){
				< % for (i = 0; i < iPP; i++ ){ %>
					fnClickAddRowPP( "< %=cClavePP[i]%>", "< %=dClavePP[i]%>" , "< %=mClavePP[i]%>");
				< %}%> 
			}*/
		}

		function fnClickAddRowComprobatoria(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) {
			$('#grdProgPresupUE').dataTable().fnAddData( [A,B,C ]);
		}

		function fnAgregarRet() {
			var table = document.getElementById('grdProgPresupUE');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
		    try{
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
		        	var chkbox = '';
					try {
					  var chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
					if(null != chkbox ) {
						if(chkbox.toString() == $("#txtGridOGTOC").val()){
			        		yaExiste=1;
						}
	        		}	
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó una retención con esa clave");
				return;
			}
			queryFormPost("tTechosPartidaCreate", {async: false });
			fnClickAddRowB($("#txtGridOGTOC").val(), $("#txtGridOGTOD").val(), $("#txtGridCTG").val(), $("#txtGridDTG").val(), $("#mTechoPartida").val() );
		}

		function fnAgregarPAUE() {
			var table = document.getElementById('grdPartidaUE');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
		    try{
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
		        	var chkbox = '';
					try {
					  var chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó una retención con esa clave");
				return;
			}
			queryFormPost("tTechosPartidaUniEjeCreate", {async: false });
			fnClickAddRowB($("#txtGridOGTOCUE").val(), $("#txtGridOGTODUE").val(), $("#txtGridCTGUE").val(), $("#txtGridDTGUE").val(), $("#txtGridCUEUE").val(),$("#txtGridDUEUE").val(),$("#tTechosPartidaUniEjec").val() );
		}

		function onSubmit(id_oper){
		//alert("onSumbit");
			var p = window.parent;
			var valida_campos = true;
			try{
		 		if (<%=bErrorTecho%> == true){
		 			$("#pb_save").attr("disabled","disabled");
		 			$("#pb_send").attr("disabled","disabled");
		 		}
				guardaExp();
			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

 		function onPostSubmit(id_oper){//validaciones del boton enviar
	 		if (<%=bErrorTecho%> == true){
	 			$("#pb_save").attr("disabled","disabled");
	 			$("#pb_send").attr("disabled","disabled");
	 		}
	  		return true;
		}
  	
		function onLoadPlantilla(){
			carga();
			var nLongMsg="<%=mensajeVAdec.length()%>";
			if (nLongMsg > 0){
				alert("<%=mensajeVAdec%>");
			}
		}
		
		function ResponsableSiguiente(id_oper){
		}

		function OperacionSiguiente(id_oper){
		}

		function onPostDisplay(){
		}
		function onActualizaEstatus(cTipoAutoriza){
	   		var mas_params="&"+cTipoAutoriza;
			document.formAutoriza.submit();
		}

	 	function get(name) {
			return document.getElementById(name).value;
		}
		
		function validEP(){
		
		}
		
 
	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>Techos Presupuestales de Unidades Ejecutoras</h1>
			<form id="formAnteProy" name="formAnteProy" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
				<input type="hidden" value="" id="rowsAffected" name="rowsAffected">
				<input type="hidden" id="cRamo" name="cRamo" value="16">
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="">
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="" />
				<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="<%=cEjercicoFiscal%>"/>
				<div class="dvGeneral">
					<table height="66" width="100%" border="0">
						<tr align="left">
							<td>Estructura Programatica:</td>
							<td><input type="text" id="cEP" name="cEP"  size="64" maxlength="66" onchange="validEP();"/> </td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr align="left">
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr align="left">
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr align="left">
							<td></td>
							<td valign="top" nowrap colspan="2">
								<select id="cUniNormativa" class="paso01" name="cUniNormativa" style="width: 40em;">
									<option value=""></option>
									<% if (iarrmTecho > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iarrmTecho) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmEstatusRechazo.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td valign="top" nowrap colspan="2">
								<select id="cUniNormativa" class="paso01" name="cUniNormativa" style="width: 40em;">
									<option value=""></option>
									<% if (iarrmTecho > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iarrmTecho) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmEstatusRechazo.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td valign="top" nowrap colspan="2">
								<select id="cUniNormativa" class="paso01" name="cUniNormativa" style="width: 40em;">
									<option value=""></option>
									<% if (iarrmTecho > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iarrmTecho) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmEstatusRechazo.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td valign="top" nowrap colspan="2">
								<select id="cUniNormativa" class="paso01" name="cUniNormativa" style="width: 40em;">
									<option value=""></option>
									<% if (iarrmTecho > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iarrmTecho) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmEstatusRechazo.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</div>
				<div id="tabs-5">
					<table   class="display" id="grdProgPresupUE">
						<thead>
							<tr>
								<th>Consecutivo</th>
								<th>Clave</th>
								<th>Grupo Funcional</th>
								<th>Clave</th>
								<th>Funcion</th>
								<th>Clave</th>
								<th>Sub-Funcion</th>
								<th>Clave</th>
								<th>Programa Prioritario</th>
								<th>Clave</th>
								<th>Actividad Institucional</th>
								<th>Clave</th>
								<th>Programa Presupuestario</th>
								<th>Clave</th>
								<th>Partida</th>
								<th>Clave</th>
								<th>Tipo Gasto</th>
								<th>Clave</th>
								<th>Fuente Financiamiento</th>
								<th>Clave</th>
								<th>Entidad Federativa</th>
								<th>Clave</th>
								<th>Cartera</th>
								<th>$ Techo</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
						<tfoot>
						</tfoot>
					</table>
				</div>
			</form>
		</div>
	</body>
</html>
