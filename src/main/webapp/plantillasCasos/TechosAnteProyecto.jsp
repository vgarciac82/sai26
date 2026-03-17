<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	String cMensaje="";
	String mensajeVAdec="";
	boolean bCargaDT=true;
	boolean brechazo = false;
	int id_oper = -1;
	int iarrmUE=0;
	int i=0; 
	int iPP=0;
	String[] cClaveUN = null; 
	String[] dClaveUN = null;
	Double[] mClaveUN = null;
	int iUN=0;
	int iarrmUN=0;
	String[] cClaveUE = null; 
	String[] dClaveUE = null;
	Double[] mClaveUE = null;
	int iUE=0;
	String[] cClaveEF = null; 
	String[] dClaveEF = null;
	Double[] mClaveEF = null;
	int iEF=0;
	String[] cClavePA = null; 
	String[] dClavePA = null;
	String[] cClaveTG = null; 
	String[] dClaveTG = null;
	Double[] mClavePA = null;
	String[] cClavePP = null; 
	String[] dClavePP = null;
	Double[] mClavePP = null;
	int iPA=0;
	// varibles de JAva para anteproyecto
	Integer[] iConsecutivo=null;
	String[] cEPDetalle=null;
	Double[] mMontoCalculado=null;
	Double[] mMontoOptimo=null;
	Double[] mMontoIreductible=null;
	String[] cReduccion=null;
	String[] cIncremento=null;
	int iAPD=0;
	int iVerAnte=0;
	// fin de variables
	boolean cGrupoUSR=false;
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String cUsrLog = "";
	String cAnteProyecto="";
	String nCuenta="";
	String cDescripcion="";
	String cCampo="";
	String cUnidadNormativa="";
	String cUnidadEjecutora="";
	int nFolioAnteProyecto=0;
	int nFolioAnteProyectoAnt=0;
	int nPorcentajeReduccion=0;
	int nPorcentajeAmpliacion=0;

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if(usuario==null){
		response.sendRedirect("../index.jsp");
	}

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}
	
	
	/* recupera datos de techos */
	AnteProyectoBusinessLogic apBL = new AnteProyectoBusinessLogic("");
	HashMap tTechoUNormativa = apBL.getTechos("tTechoUNormativa");
	HashMap tTechoUEjecutora = apBL.getTechos("tTechoUEjecutora");
	HashMap tTechosPartidaUniEjec = apBL.getTechos("tTechosPartidaUniEjec");
	HashMap tTechosPartidaUniNorm = apBL.getTechos("tTechosPartidaUniNorm");
	HashMap tTechosPartida = apBL.getTechos("tTechosPartida");
	HashMap tTechoProgramaPresupuestario = apBL.getTechos("tTechoProgramaPresupuestario");
	HashMap tTechoProgPresupUniEjec = apBL.getTechos("tTechoProgPresupUniEjec");
	HashMap tTechoEntidadFederativa = apBL.getTechos("tTechoEntidadFederativa");
	
	int actual=0;
	if(request.getParameter("pes")!=null){
			actual = Integer.parseInt(request.getParameter("pes"));
	}
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Techos Presupuestales</title>

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

		var oTableUN;
		
		$(document).ready(function(){
				$('.currency').blur(function(){
					$('.currency').formatCurrency();
				});
				
				oTablePA = $('#grdRetencion').dataTable( );
				oTableUE = $('#grdUniEje').dataTable( );
				oTableUN = $('#grdUnidadNormativa').dataTable( );
				oTablePP = $('#grdProgPresup').dataTable( );
				oTableMov = $('#grdMovimientos').dataTable( );
				oTablePPUE = $('#grdProgPresup').dataTable( );
				oTablePAUE = $('#grdPartidaUE').dataTable( );
				oTablePTGUE = $('#grdPTGUE').dataTable( );
				oTablePTGUN = $('#grdPTGUN').dataTable( );
				oTableDANTEP = $('#grdAnteProyecto').dataTable( );
				oTableProgPU = $('#grdProgPresupU').dataTable( );

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
					"sScrollX" : "1250",
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
					var rowIndex = oTable.fnGetPosition($(this)[0]);
					editRow(oTable,rowIndex);		
				});
				
				//grdMovimientos = Entidad Federativa
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
					"sScrollX" : "1250",
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
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
					var rowIndex = oTableMov.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTableMov,rowIndex,"#grdMovimientos","tTechoEntidadFederativaUpdate","#cEntidadFederativa","#mTechoMontoEF",null,null);
				});

				//grdPTGUE = PARTIDA UNIDAD EJECUTORA****
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
					"sScrollX" : "1250",
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				});
				$("#grdPTGUE tbody").click(function(event) {
					$(oTablePTGUE.fnSettings().aoData).each(function(){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePTGUE.fnGetPosition( this.nTr );
						var aData = oTablePTGUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdPTGUE tbody").dblclick(function(event) {
					var rowIndex = oTablePTGUE.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTablePTGUE,rowIndex,"#grdPTGUE","tTechoPartidaUnidadEjecutoraUpdate","#cPartidaPUE","#mTechoMontoPUE","#cTGPUE","#cUPUE");
				});
				

//------------------------------------------------------------Ejecutora
				//grdUniEje = UNIDAD EJECUTORA
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
					"sScrollX" : "1250",
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				});
				$("#grdUniEje tbody").click(function(event) {
					$(oTableUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUE.fnGetPosition( this.nTr );
					var aData = oTableUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdUniEje tbody").dblclick(function(event) {
					var rowIndex = oTableUE.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTableUE,rowIndex,"#grdUniEje","tTechoUEjecutoraUpdate","#cUnidadResponsable","#mTechoMontoUE",null,null);
				});
//---------------------------------------------------------Normativa
				//grdUnidadNormativa = UNIDAD NORMATIVA
				oTableUN = $('#grdUnidadNormativa').dataTable({
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
					"sScrollX" : "1250",
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
					var rowIndex = oTableUN.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTableUN,rowIndex,"#grdUnidadNormativa","tTechoUNormativaUpdate","#cUnidadNormativa","#mTechoMonto",null,null);
				});

//---------------------------------------------------------ProgramaPresupuestario
				//grdProgPresup = PROGRAMA PRESUPUESTARIO
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
					"sScrollX" : "1250",
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
					var rowIndex = oTablePP.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTablePP,rowIndex,"#grdProgPresup","tTechoProgramaPresupuestarioUpdate","#cProgramaPresupuestario","#mTechoMontoPP",null,null);
				});
				

				//grdPTGUN = PARTIDA UNIDAD NORMATIVA**** <!-- brenda prueba -->
				$('#grdPTGUN').dataTable({
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
					"sScrollX" : "1250",
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				});
				$("#grdPTGUN tbody").click(function(event) {
					$(oTablePTGUN.fnSettings().aoData).each(function(){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePTGUN.fnGetPosition( this.nTr );
						var aData = oTablePTGUN.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdPTGUN tbody").dblclick(function(event) {
					var rowIndex = oTablePTGUN.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTablePTGUN,rowIndex,"#grdPTGUN","tTechoPartidaUnidadNormativaUpdate","#cPartidaPUN","#mTechoPUN","#cTGPUN","#cUPUN");
				});


//---------------------------------------------
			//grdRetencion = PARTIDA
			$('#grdRetencion').dataTable(
				{
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": true,
					"bJQueryUI": true,
					"bRetrive" : true,
					"sScrollX" : "1250",
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				});
				$("#grdRetencion tbody").click(function(event) {
					$(oTablePA.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePA.fnGetPosition( this.nTr );
						var aData = oTablePA.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdRetencion tbody").dblclick(function(event) {
					var rowIndex = oTablePA.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTablePA,rowIndex,"#grdRetencion","tTechosPartidaUpdate","#cPartida","#mTechoPartidaUpdate","#cTipoGasto",null);
				});

			oTableProgPU = $('#grdProgPresupU').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"sScrollX" : "1250",
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				});
				$("#grdProgPresupU tbody").click(function(event) {
					$(oTableProgPU.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTableProgPU.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTableProgPU.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresupU tbody").dblclick(function(event) {
					var rowIndex = oTableProgPU.fnGetPosition($(event.target.parentNode)[0]);
					editRow(oTableProgPU,rowIndex,"#grdProgPresupU","tTechosProgPresuUpdate","#cProgramaPresuPPUE","#mTechoMontoPPUE","#cUnidadResponsalePPUE",null);
				});
//-----------------------------------
				$("#grdPartidaUE tbody").click(function(event) {
					$(oTablePAUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePAUE.fnGetPosition( this.nTr );
						var aData = oTablePAUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdPartidaUE tbody").dblclick(function(event) {
				});
				
				$("input.AyudaSyC").subIniciaDlg();			
			
				var tabs = $("#tabs").tabs({
					"show": function(event, ui) {
						var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
						if ( oTable.length > 0 ) {
							oTable.fnAdjustColumnSizing();
						}
					}
				});
					
				var $dialog;
			    $(function() {		
		    	    $('#dialog').dialog({
		        	    autoOpen: false,
		            	width: 900,
		            	heigth: 2900
    		    	});
    			});
			    carga();
			    
			    tabs.tabs('select', "<%=actual%>"); 
			    
			});  //fin del ready

		
		function carga(){ //en el for se divide entre el total de campos que trae para poder distinguir las filas
			if (<%=tTechoUNormativa.size()%> > 0){
				<% for (i = 0; i < (tTechoUNormativa.size()/3); i++ ){ %>
					fnClickAddRowUN( "<%=tTechoUNormativa.get("c"+i)%>", "<%=tTechoUNormativa.get("d"+i)%>" , "<%=tTechoUNormativa.get("techo"+i)%>","<div id=\"delete<%=tTechoUNormativa.get("c"+i)%>\"><a href=\"#\">Eliminar</a></div>");
					var uno = "<%=tTechoUNormativa.get("c"+i)%>";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTableUN.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deleteUN','tTechoUNormativa','<%=tTechoUNormativa.get("c"+i)%>',null,null);
						oTableUN.fnDeleteRow( aPos);
					});
				<%}%>
			}
			if (<%=tTechosPartida.size()%> > 0){
				<% for (i = 0; i < (tTechosPartida.size()/5); i++ ){ %>
					fnClickAddRowB ("<%=tTechosPartida.get("cp"+i)%>", "<%=tTechosPartida.get("d"+i)%>", "<%=tTechosPartida.get("cu"+i)%>", "<%=tTechosPartida.get("d2"+i)%>", "<%=tTechosPartida.get("techo"+i)%>","<div id=\"delete<%=tTechosPartida.get("cp"+i)%><%=tTechosPartida.get("cu"+i)%>\"><a href=\"#\">Eliminar</a></div>" );
					var uno = "<%=tTechosPartida.get("cp"+i)%><%=tTechosPartida.get("cu"+i)%>";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTablePA.fnGetPosition(event.target.parentNode.parentNode.parentNode);
						fnDeleteAjax('deleteRet','tTechosPartida','<%=tTechosPartida.get("cu"+i)%>','<%=tTechosPartida.get("cp"+i)%>',null);
						oTablePA.fnDeleteRow(aPos);
					});
				<%}%>
			}
			if (<%=tTechoUEjecutora.size()%> > 0 ){
				<% for (i = 0; i <(tTechoUEjecutora.size()/3); i++ ){ %>
					fnClickAddRowUE("<%=tTechoUEjecutora.get("c"+i)%>", "<%=tTechoUEjecutora.get("d"+i)%>", "<%=tTechoUEjecutora.get("techo"+i)%>","<div id=\"delete<%=tTechoUEjecutora.get("c"+i)%>UE\"><a href=\"#\">Eliminar</a></div>");
					var uno = "<%=tTechoUEjecutora.get("c"+i)%>UE";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTableUE.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deleteUE','tTechoUEjecutora','<%=tTechoUEjecutora.get("c"+i)%>',null,null);
						oTableUE.fnDeleteRow( aPos);
					});
				<%}%>
			}
			
			if (<%=tTechoProgramaPresupuestario.size()%> > 0){
				<% for (i = 0; i < (tTechoProgramaPresupuestario.size()/3); i++ ){ %>
					 fnClickAddRowPP ("<%=tTechoProgramaPresupuestario.get("c"+i)%>", "<%=tTechoProgramaPresupuestario.get("d"+i)%>", "<%=tTechoProgramaPresupuestario.get("techo"+i)%>","<div id=\"delete<%=tTechoProgramaPresupuestario.get("c"+i)%>\"><a href=\"#\">Eliminar</a></div>");
					 var uno = "<%=tTechoProgramaPresupuestario.get("c"+i)%>";
					 $("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTablePP.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deletePP','tTechoProgramaPresupuestario','<%=tTechoProgramaPresupuestario.get("c"+i)%>',null,null);
						oTablePP.fnDeleteRow( aPos);
					});
				<%}%>
			}
			if (<%=tTechoEntidadFederativa.size()%> > 0 ){
				<% for (i = 0; i < (tTechoEntidadFederativa.size()/3); i++ ){ %>
					fnClickAddRowEF("<%=tTechoEntidadFederativa.get("c"+i)%>", "<%=tTechoEntidadFederativa.get("d"+i)%>", "<%=tTechoEntidadFederativa.get("techo"+i)%>","<div id=\"delete<%=tTechoEntidadFederativa.get("c"+i)%>\"><a href=\"#\">Eliminar</a></div>");
					var uno = "<%=tTechoEntidadFederativa.get("c"+i)%>";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTableMov.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deleteEn','tTechoEntidadFederativa','<%=tTechoEntidadFederativa.get("c"+i)%>',null,null);
						oTableMov.fnDeleteRow( aPos);
					});
				<%}%>
			}
			if (<%=tTechosPartidaUniEjec.size()%> > 0 ){
				<% for (i = 0; i < (tTechosPartidaUniEjec.size()/7); i++ ){ %>
					fnClickAddRowPUE("<%=tTechosPartidaUniEjec.get("cu"+i)%>", "<%=tTechosPartidaUniEjec.get("d3"+i)%>","<%=tTechosPartidaUniEjec.get("ct"+i)%>","<%=tTechosPartidaUniEjec.get("d2"+i)%>", "<%=tTechosPartidaUniEjec.get("cp"+i)%>","<%=tTechosPartidaUniEjec.get("d"+i)%>", "<%=tTechosPartidaUniEjec.get("techo"+i)%>","<div id=\"delete<%=tTechosPartidaUniEjec.get("cp"+i)%><%=tTechosPartidaUniEjec.get("ct"+i)%><%=tTechosPartidaUniEjec.get("cu"+i)%>\"><a href=\"#\">Eliminar</a></div>");
					var uno = "<%=tTechosPartidaUniEjec.get("cp"+i)%><%=tTechosPartidaUniEjec.get("ct"+i)%><%=tTechosPartidaUniEjec.get("cu"+i)%>";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTablePTGUE.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deletePUE','tTechosPartidaUniEjec','<%=tTechosPartidaUniEjec.get("cp"+i)%>','<%=tTechosPartidaUniEjec.get("cp"+i)%>','<%=tTechosPartidaUniEjec.get("cu"+i)%>');
						oTablePTGUE.fnDeleteRow(aPos);
					});
				<%}%>
			}
			if (<%=tTechosPartidaUniNorm.size()%> > 0 ){
				<% for (i = 0; i < (tTechosPartidaUniNorm.size()/7); i++ ){ %>
					fnClickAddRowPUN("<%=tTechosPartidaUniNorm.get("cu"+i)%>", "<%=tTechosPartidaUniNorm.get("d3"+i)%>","<%=tTechosPartidaUniNorm.get("ct"+i)%>","<%=tTechosPartidaUniNorm.get("d2"+i)%>", "<%=tTechosPartidaUniNorm.get("cp"+i)%>","<%=tTechosPartidaUniNorm.get("d"+i)%>", "<%=tTechosPartidaUniNorm.get("techo"+i)%>","<div id=\"delete<%=tTechosPartidaUniNorm.get("cp"+i)%><%=tTechosPartidaUniNorm.get("ct"+i)%><%=tTechosPartidaUniNorm.get("cu"+i)%>\"><a href=\"#\">Eliminar</a></div>");
					var uno = "<%=tTechosPartidaUniNorm.get("cp"+i)%><%=tTechosPartidaUniNorm.get("ct"+i)%><%=tTechosPartidaUniNorm.get("cu"+i)%>";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTablePTGUN.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deletePUN','tTechosPartidaUniNorm','<%=tTechosPartidaUniNorm.get("cp"+i)%>','<%=tTechosPartidaUniNorm.get("ct"+i)%>','<%=tTechosPartidaUniNorm.get("cu"+i)%>');
						oTablePTGUN.fnDeleteRow(aPos);
					});
				<%}%>
			}
			if (<%=tTechoProgPresupUniEjec.size()%> > 0 ){
				<% for (i = 0; i < (tTechoProgPresupUniEjec.size()/5); i++ ){ %> //BRENDA
					fnClickAddRowPPUE("<%=tTechoProgPresupUniEjec.get("cu"+i)%>", "<%=tTechoProgPresupUniEjec.get("d2"+i)%>", "<%=tTechoProgPresupUniEjec.get("cp"+i)%>","<%=tTechoProgPresupUniEjec.get("d"+i)%>", "<%=tTechoProgPresupUniEjec.get("techo"+i)%>","<div id=\"delete<%=tTechoProgPresupUniEjec.get("cp"+i)%><%=tTechoProgPresupUniEjec.get("cu"+i)%>UNI\"><a href=\"#\">Eliminar</a></div>");
					var uno = "<%=tTechoProgPresupUniEjec.get("cp"+i)%><%=tTechoProgPresupUniEjec.get("cu"+i)%>UNI";
					$("#delete"+uno).bind('click', function(event) {  
						var aPos  = oTableProgPU.fnGetPosition( event.target.parentNode.parentNode.parentNode );
						fnDeleteAjax('deletePPUE','tTechoProgPresupUniEjec','<%=tTechoProgPresupUniEjec.get("cu"+i)%>','<%=tTechoProgPresupUniEjec.get("cp"+i)%>',null);
						oTableProgPU.fnDeleteRow(aPos);
					});
				<%}%>
			}
		}
			
		function fnAgregarRet() {
			var table = document.getElementById('grdRetencion');
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
			fnClickAddRowB($("#txtGridOGTOC").val(), $("#txtGridOGTOD").val(), $("#txtGridCTG").val(), $("#txtGridDTG").val(), $("#mTechoPartida").val(),"<div id='delete"+$("#txtGridOGTOC").val()+$("#txtGridCTG").val()+"'><a href='#'>Eliminar</a></div>" );
			var uno = $("#txtGridOGTOC").val()+""+$("#txtGridCTG").val();
			$("#delete"+uno).bind('click', function(event) {  
				var aPos  = oTablePAUE.fnGetPosition(event.target.parentNode.parentNode.parentNode);
				fnDeleteAjax('deleteRet','tTechosPartida',$('#grdRetencion tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdRetencion tbody tr:eq('+aPos+') td:eq(2)').html(),null);
				oTablePAUE.fnDeleteRow(aPos);
			});
		}
		//---------------------------------------------------------------------
		function fnAgregarPAUE() {
			//quitaFormato();
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
//					if(null != chkbox ) {
//						if(chkbox.toString() == $("#txtGridOGTOC").val() && ){
//			        		yaExiste=1;
//						}
//	        		}	
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
			//ponFormato();
		}
//--------------------------------------
		function fnAgregarPP() {
			//quitaFormato();
			var table = document.getElementById('grdProgPresup');
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
						if(chkbox.toString() == $("#txtGridtProgPresupC").val()){
			        		yaExiste=1;
						}
	        		}	
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó un Programa Presupuestario con esa clave");
				return;
			}
			queryFormPost("tTechoProgramaPresupuestarioCreate", {async: false });
			fnClickAddRowPP($("#txtGridtProgPresupC").val(), $("#txtGridtProgPresupD").val(), $("#mMontoTopeProgPres").val(),"<div id='delete"+$("#txtGridtProgPresupC").val()+"'><a href='#'>Eliminar</a></div>" );
			//ponFormato();
			var uno = $("#txtGridtProgPresupC").val();
			$("#delete"+uno).bind('click', function(event) {  
				var aPos  = oTablePP.fnGetPosition( event.target.parentNode.parentNode.parentNode );
				oTablePP.fnDeleteRow( aPos);
			});
		}

//---------------------------------------------------------------------

	function fnAgregarUN() {
		//quitaFormato();
		if($.trim($("#txtGridtUniNormC").val())!="" || $.trim($("#txtGridtUniNormD").val())!="" || $.trim($("#mMontoUN").val())!=""){
			var table = document.getElementById('grdUnidadNormativa');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
		    try{
	        	var chkbox = '';
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
					try {
					  chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
					if(null != chkbox ) {
						if(chkbox.toString() == $("#txtGridtUniNormC").val()){
			        		yaExiste=1;
						}
	        		}	
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó un Programa Presupuestario con esa clave");
				return;
			}
			queryFormPost("tTechoUNormativaCreate", {async: false });
			fnClickAddRowUN($("#txtGridtUniNormC").val(), $("#txtGridtUniNormD").val(), $("#mMontoUN").val(),"<div id='delete"+$("#txtGridtUniNormC").val()+"'><a href='#'>Eliminar</a></div>" );
			var uno = $("#txtGridtUniNormC").val();
			document.getElementById("txtGridtUniNormC").value="";
			document.getElementById("txtGridtUniNormD").value="";
			document.getElementById("mMontoUN").value="";
			//ponFormato();
			$("#delete"+uno).bind('click', function(event) {  
				var aPos  = oTableUN.fnGetPosition( event.target.parentNode.parentNode.parentNode );
				oTableUN.fnDeleteRow( aPos);
			});
		}else{
			alert("Favor de llenar los tres campos");
		}
	}

		
		
//---------------------------------------------------------------------
	function fnAgregarUniEje() {
		//quitaFormato();
		var table = document.getElementById('grdUniEje');
	    var rowCount = table.rows.length;
	    var yaExiste = 0;
		var nu2=0;
	    try{
        	var chkbox = '';
			for(var i=0; i<rowCount; i++) {
	        	var row = table.rows[i];
				try {
				  chkbox = row.cells[0].childNodes[0];
				} catch(e) {
	        		null;
	        	}
				if(null != chkbox ) {
					if(chkbox.toString() == $("#txtGridtUniEjeC").val()){
		        		yaExiste=1;
					}
        		}	
    		}
	    } catch(e) {
	       alert(e);
    	}
		if (yaExiste == 1){
			alert("Ya se ingresó un Programa Presupuestario con esa clave");
			return;
		}
		queryFormPost("tTechoUEjecutoraCreate", {async: false });
		fnClickAddRowUE($("#txtGridtUniEjeC").val(), $("#txtGridtUniEjeD").val(), $("#mMontoUE").val(),"<div id='delete"+$("#txtGridtUniEjeC").val()+"'><a href='#'>Eliminar</a></div>" );
		var uno = $("#txtGridtUniEjeC").val();
		document.getElementById("txtGridtUniEjeC").value="";
		document.getElementById("txtGridtUniEjeD").value="";
		document.getElementById("mMontoUE").value="";
		$("#delete"+uno).bind('click', function(event) {  
			var aPos  = oTableUE.fnGetPosition( event.target.parentNode.parentNode.parentNode );
			oTableUE.fnDeleteRow( aPos);
		});
		//ponFormato();
	}

	function fnAgregarEF() {
		var table = document.getElementById('grdMovimientos');
	    var rowCount = table.rows.length;
	    var yaExiste = 0;
		var nu2=0;
       	var chkbox = '';
	    try{
			for(var i=0; i<rowCount; i++) {
	        	var row = table.rows[i];
				try {
				  chkbox = row.cells[0].childNodes[0];
				} catch(e) {
	        		null;
	        	}
				if(null != chkbox ) {
					if(chkbox.toString() == $("#txtGridOGTOCPUE").val()){
		        		//yaExiste=1;
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
		queryFormPost("tTechoUEjecutoraCreate", {async: false });
		fnClickAddRowUE($("#txtGridtUniEjeC").val(), $("#txtGridtUniEjeD").val(), $("#mMontoUE").val(),"<div id='delete"+$("#txtGridtUniEjeC").val()+"'><a href='#'>Eliminar</a></div>" );
		var uno = $("#txtGridtUniEjeC").val();
		document.getElementById("txtGridtUniEjeC").value="";
		document.getElementById("txtGridtUniEjeD").value="";
		document.getElementById("mMontoUE").value="";
		$("#delete"+uno).bind('click', function(event) {  
			var aPos  = oTableMov.fnGetPosition( event.target.parentNode.parentNode.parentNode );
			fnDeleteAjax('deleteEn','tTechoEntidadFederativa',$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(0)').html(),null,null);
			oTableMov.fnDeleteRow( aPos);
		});
	}
		
	function fnAgregarPUE() { 
			var table = document.getElementById('grdPTGUE');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
	       	var chkbox = '';
		    try{
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
					try {
					  chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
					if(null != chkbox ) {
						if(chkbox.toString() == $("#txtGridEntFederalC").val()){
			        		yaExiste=1;
						}
	        		}	
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó una partida de unidad ejecutora con esa clave");
				return;
			}
		
		var uno = $("#txtGridOGTOCPUE").val()+$("#txtGridCTGPUE").val()+$("#txtGridtUniEjeCPUE").val();
		queryFormPost("tTechoPartidaUnidadEjecutoraCreate", {async: false });
		fnClickAddRowPUE($("#txtGridOGTOCPUE").val(), $("#txtGridOGTODPUE").val(), $("#txtGridCTGPUE").val(),$("#txtGridDTGPUE").val(),$("#txtGridtUniEjeCPUE").val(),$("#txtGridtUniEjeDPUE").val(),$("#mTechoPUE").val(),"<div id='delete"+$("#txtGridOGTOCPUE").val()+$("#txtGridCTGPUE").val()+$("#txtGridtUniEjeCPUE").val()+"'><a href='#'>Eliminar</a></div>" );
		$("#delete"+uno).bind('click', function(event) {  
			var aPos  = oTablePTGUE.fnGetPosition( event.target.parentNode.parentNode.parentNode );
			fnDeleteAjax('deletePUE','tTechosPartidaUniEjec',$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(2)').html(),$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(4)').html());
			oTablePTGUE.fnDeleteRow(aPos);
		});
	}
	
	function fnAgregarPUN() { //BRENDA 
			var table = document.getElementById('grdPTGUN');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;	
			var nu2=0;
	       	var chkbox = '';
		    try{
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
					try {
					  chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
					if(null != chkbox ) {
						if(chkbox.toString() == $("#txtGridEntFederalC").val()){
			        		yaExiste=1;
						}
	        		}	
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó una partida de unidad ejecutora con esa clave");
				return;
			}
		
		var uno = $("#txtGridOGTOCPUE2").val()+$("#txtGridCTGPUE2").val()+$("#txtGridtUniNormC2").val();
		queryFormPost("tTechoPartidaUnidadNormativaCreate", {async: false });//BRENDA
		fnClickAddRowPUN($("#txtGridOGTOCPUE2").val(), $("#txtGridOGTODPUE2").val(), $("#txtGridCTGPUE2").val(),$("#txtGridDTGPUE2").val(),$("#txtGridtUniNormC2").val(),$("#txtGridtUniNormD2").val(),$("#mTechoPUN").val(),"<div id='delete"+$("#txtGridOGTOCPUE2").val()+$("#txtGridOGTOCPUE2").val()+$("#txtGridtUniNormC2").val()+"'><a href='#'>Eliminar</a></div>" );
		$("#delete"+uno).bind('click', function(event) {  
			var aPos  = oTablePTGUN.fnGetPosition( event.target.parentNode.parentNode.parentNode );
			fnDeleteAjax('deletePUE','tTechosPartidaUniNorm',$('#grdPTGUN tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdPTGUN tbody tr:eq('+aPos+') td:eq(2)').html(),$('#grdPTGUN tbody tr:eq('+aPos+') td:eq(4)').html());
			oTablePTGUN.fnDeleteRow(aPos);
		});
	}


	function fnAgregarProgPresupUE(){
			var table = document.getElementById('grdProgPresupU');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
	       	var chkbox = '';
		    try{
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
					try {
					  chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
					if(null != chkbox ) {
						if(chkbox.toString() == $("#txtGridtProgPresupCU").val()){
			        		yaExiste=1;
						}
	        		}	
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó una partida de unidad ejecutora con esa clave");
				return;
			}
		
		var uno = $("#txtGridtProgPresupCU").val()+$("#txtGridtProgPresupCUE").val();
		queryFormPost("tTechoProgPresupUniEjecCreate", {async: false });
		fnClickAddRowPPUE($("#txtGridtProgPresupCU").val(), $("#txtGridtProgPresupDU").val(), $("#txtGridtProgPresupCUE").val(),$("#txtGridtProgPresupDUE").val(),$("#mMontoTopeProgPresU").val(),"<div id='delete"+$("#txtGridtProgPresupCU").val()+$("#txtGridtProgPresupCUE").val()+"'><a href='#'>Eliminar</a></div>" );
		$("#delete"+uno).bind('click', function(event) {  
			var aPos  = oTableProgPU.fnGetPosition( event.target.parentNode.parentNode.parentNode );
			fnDeleteAjax('deletePPUE','tTechoProgPresupUniEjec',$('#grdProgPresupU tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdProgPresupU tbody tr:eq('+aPos+') td:eq(2)').html(),$('#grdProgPresupU tbody tr:eq('+aPos+') td:eq(4)').html());
			oTableProgPU.fnDeleteRow(aPos);
		});
	}

	//------------------------------------------------------------------
	//-------------ADD ROWS--------------------------
	//---------------------------------------------------------------------
	function fnClickAddRowB(A, B, C,D,E,F) {
		$('#grdRetencion').dataTable().fnAddData( [ A,B,C,D,E,F ] );
	}
	function fnClickAddRowPP(A,B,C,D) {
		$('#grdProgPresup').dataTable().fnAddData([ A,B,C,D ]);
	}

	function fnClickAddRowUE(A, B, C,D) {
		$('#grdUniEje').dataTable().fnAddData( [ A,B,C,D ] );
	}

	function fnClickAddRowUN(A, B, C,D) {
		$('#grdUnidadNormativa').dataTable().fnAddData( [ A,B,C,D ] );
	}

	function fnClickAddRowEF(A, B, C, D) {
		$('#grdMovimientos').dataTable().fnAddData( [ A,B,C,D ] );
	}
	
	function fnClickAddRowComprobatoria(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) {
		$('#grdFacturas').dataTable().fnAddData( [A,B,C ]);
	}
	function fnClickAddRowPUE(A,B,C,D,E,F,G,H) {
		$('#grdPTGUE').dataTable().fnAddData( [ A,B,C,D,E,F,G,H ] );
	}
	function fnClickAddRowPPUE(A,B,C,D,E,F) {
		$('#grdProgPresupU').dataTable().fnAddData( [ A,B,C,D,E,F ] );
	}
	function fnClickAddRowPUN(A,B,C,D,E,F,G,H) {
		$('#grdPTGUN').dataTable().fnAddData( [ A,B,C,D,E,F,G,H ] );
	}

	//---------------------------------------------------------------------
	//---------------------------------------------------------------------
	//------------------------------------------------------------------
		
	
	function onSubmit(id_oper){//validaciones del boton guardar
			var p = window.parent;
			var valida_campos = true;
			try{
				//Control de estado de botones
				guardaExp();
			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

 		function onPostSubmit(id_oper){//validaciones del boton enviar
	  		return true;
		}
		
		function onLoadPlantilla(){}		
		function onPostDisplay(){}
		
		var importa=true;
		function fnImportaExcel(tipo){
			if(importa){
			$.blockUI({message: "Procesando Importacion espere ......"});
				$.ajax({
	  				type: "POST",
	  				url: "../gstnmngr/TechosExcel",
	  				data: {accion:'1',tipo:tipo}
				}).success(function( msg ) {
					var obj = eval ("(" + msg + ")"); 
					if(obj.techos[0].mensaje == undefined){
						if(tipo=="un")oTableUN.fnClearTable();
						if(tipo=="ue")oTableUE.fnClearTable();
						if(tipo=="entidad")oTableMov.fnClearTable();
						if(tipo=="programap")oTablePP.fnClearTable();
						if(tipo=="ret")oTablePAUE.fnClearTable();
						if(tipo=="partidaue")oTablePTGUE.fnClearTable();
						if(tipo=="progprue")oTableProgPU.fnClearTable();
						if(tipo=="partidaun")oTablePTGUN.fnClearTable();						
						for (i in obj.techos){
							if(tipo=="un"){
		  						fnClickAddRowUN(obj.techos[i].clave,obj.techos[i].descripcion,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave).bind('click', function(event) {  
									var aPos  = oTableUN.fnGetPosition( event.target.parentNode.parentNode.parentNode );
									fnDeleteAjax('deleteUN','tTechoUNormativa',$('#grdUnidadNormativa tbody tr:eq('+aPos+') td:eq(0)').html(),null,null);
									oTableUN.fnDeleteRow( aPos);
								});
		  					}else if(tipo=="ue"){
		  						fnClickAddRowUE(obj.techos[i].clave,obj.techos[i].descripcion,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave).bind('click', function(event) {  
									var aPos  = oTableUE.fnGetPosition( event.target.parentNode.parentNode.parentNode );
									fnDeleteAjax('deleteUE','tTechoUEjecutora',$('#grdUniEje tbody tr:eq('+aPos+') td:eq(0)').html(),null,null);
									oTableUE.fnDeleteRow( aPos);
								});
		  					}else if(tipo=="entidad"){
		  						fnClickAddRowEF(obj.techos[i].clave,obj.techos[i].descripcion,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave).bind('click', function(event) {  
									var aPos  = oTableMov.fnGetPosition( event.target.parentNode.parentNode.parentNode );
									fnDeleteAjax('deleteEn','tTechoEntidadFederativa',$('#grdMovimientos tbody tr:eq('+aPos+') td:eq(0)').html(),null,null);
									oTableMov.fnDeleteRow( aPos);
								});
		  					}else if(tipo=="programap"){
		  						fnClickAddRowPP(obj.techos[i].clave,obj.techos[i].descripcion,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave).bind('click', function(event) {  
									var aPos  = oTablePP.fnGetPosition( event.target.parentNode.parentNode.parentNode );
									fnDeleteAjax('deletePP','tTechoProgramaPresupuestario',$('#grdProgPresup tbody tr:eq('+aPos+') td:eq(0)').html(),null,null);
									oTablePP.fnDeleteRow( aPos);
								});
		  					}else if(tipo=="ret"){
		  						fnClickAddRowB(obj.techos[i].clave1,obj.techos[i].descripcion1,obj.techos[i].clave2,obj.techos[i].descripcion2,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave1+obj.techos[i].clave2+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave1+obj.techos[i].clave2).bind('click', function(event) {  
									var aPos  = oTablePAUE.fnGetPosition(event.target.parentNode.parentNode.parentNode);
									fnDeleteAjax('deleteRet','tTechosPartida',$('#grdRetencion tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdRetencion tbody tr:eq('+aPos+') td:eq(2)').html(),null);
									oTablePAUE.fnDeleteRow(aPos);
								});
		  					}else if(tipo=="partidaue"){
		  						fnClickAddRowPUE(obj.techos[i].clave1,obj.techos[i].descripcion1,obj.techos[i].clave2,obj.techos[i].descripcion2,obj.techos[i].clave3,obj.techos[i].descripcion3,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave1+obj.techos[i].clave2+obj.techos[i].clave3+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave1+obj.techos[i].clave2+obj.techos[i].clave3).bind('click', function(event) {  
									var aPos  = oTablePTGUE.fnGetPosition(event.target.parentNode.parentNode.parentNode);
									fnDeleteAjax('deletePUE','tTechosPartidaUniEjec',$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(2)').html(),$('#grdPTGUE tbody tr:eq('+aPos+') td:eq(4)').html());
									oTablePTGUE.fnDeleteRow(aPos);
								});
		  					}else if(tipo=="progprue"){
		  						fnClickAddRowPPUE(obj.techos[i].clave1,obj.techos[i].descripcion1,obj.techos[i].clave2,obj.techos[i].descripcion2,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave1+obj.techos[i].clave2+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave1+obj.techos[i].clave2).bind('click', function(event) {  
									var aPos  = oTableProgPU.fnGetPosition(event.target.parentNode.parentNode.parentNode);
									fnDeleteAjax('deletePPUE','tTechoProgPresupUniEjec',$('#grdProgPresupU tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdProgPresupU tbody tr:eq('+aPos+') td:eq(2)').html(),null);
									oTableProgPU.fnDeleteRow(aPos);
								});
		  					}else if(tipo=="partidaun"){
		  						fnClickAddRowPUN(obj.techos[i].clave1,obj.techos[i].descripcion1,obj.techos[i].clave2,obj.techos[i].descripcion2,obj.techos[i].clave3,obj.techos[i].descripcion3,obj.techos[i].techo,"<div id='delete"+obj.techos[i].clave1+obj.techos[i].clave2+obj.techos[i].clave3+"'><a href='#'>Eliminar</a></div>");	  						
		  						$("#delete"+obj.techos[i].clave1+obj.techos[i].clave2+obj.techos[i].clave3).bind('click', function(event) {  
									var aPos  = oTablePTGUN.fnGetPosition(event.target.parentNode.parentNode.parentNode);
									fnDeleteAjax('deletePUN','tTechosPartidaUniNorm',$('#grdPTGUN tbody tr:eq('+aPos+') td:eq(0)').html(),$('#grdPTGUN tbody tr:eq('+aPos+') td:eq(2)').html(),$('#grdPTGUN tbody tr:eq('+aPos+') td:eq(4)').html());
									oTablePTGUN.fnDeleteRow(aPos);
								});
							}
		  				}
					}else{
						alert(obj.techos[0].mensaje);
					}
					$.unblockUI();
				}).error(function(msg){
					$.unblockUI();
				});
				importa = false;
			}
		}
		
	function fnDeleteAjax(accion,tabla,filtro,filtro2,filtro3){ //sirve para borrar un registro de techos dependiendo del que se esté trabajando, a través de ajax
		$.ajax({
	  		type: "POST",
	  		url: "../gstnmngr/TechosExcel",
	  		data: {accion:accion,tabla:tabla,filtro:filtro,filtro2:filtro2,filtro3:filtro3}
		}).success(function( msg ) {
			//$.unblockUI();
		}).error(function(msg){
			alert("hubo un error borrando el registro");
		});
	}
	
	var editable = true;
	function editRow ( oTable, nRow, grid, update, campo1, campo2, campo3, campo4) {
		if(editable){
			var posicion=2;
			if(campo3!=null){
				posicion=4;
			}
			if(campo4!=null)
				posicion=6;
			var importe = 	$(grid+ ' tbody tr:eq('+nRow+') td:eq('+posicion+')').html();
			editable=false;
			$(grid + ' tbody tr:eq('+nRow+') td:eq('+posicion+')').html('<td><input type="text" id="edit'+nRow+'"  value="'+importe+'" /></td>');
		}
		$(grid + ' input').bind('keypress', function(e) {         
			if(e.keyCode==13){
				var valor = $('#edit'+nRow).val();
				$(campo1).val($(grid+' tbody tr:eq('+nRow+') td:eq(0)').html());
				$(campo2).val(valor);
				if(campo3!=null){
					$(campo3).val($(grid+' tbody tr:eq('+nRow+') td:eq(2)').html());
					if(campo4!=null){
						$(campo4).val($(grid+' tbody tr:eq('+nRow+') td:eq(4)').html());
						$(grid + ' tbody tr:eq('+nRow+') td:eq(6)').html(valor);
					}else{
						$(grid + ' tbody tr:eq('+nRow+') td:eq(4)').html(valor);
					}
				}else{
					$(grid + ' tbody tr:eq('+nRow+') td:eq(2)').html(valor);
				}
				queryFormPost(update, {async: false });
				editable = true;
				oTable.fnDraw();
			} 
		});
	} 
	
		function fnSubeArchivo(tipo){	
			if(($("#archivoEUN").val()!="") || ($("#archivoEUE").val()!="") || ($("#archivoEPP").val()!="") || ($("#archivoEEF").val()!="") || ($("#archivoERET").val()!="") || ($("#archivoEPUE").val()!="") || ($("#archivoEProgPr").val()!="") || ($("#archivoEPUN").val()!="") ){
				$.blockUI({message: "Procesando Alta espere ......"});
				if(tipo=='un')
					document.subeArchivoUN.submit();
				else if(tipo=='ue')
					document.subeArchivoUE.submit();
				else if(tipo=='pp')
					document.subeArchivoPP.submit();
				else if(tipo=='ef')
					document.subeArchivoEF.submit();
				else if(tipo=='ret')
					document.subeArchivoRet.submit();
				else if(tipo=='partidaue')
					document.subeArchivoPUE.submit();
				else if(tipo=='progprue')
					document.subeArchivoProgPrUE.submit();
				else if(tipo=='partidaun')
					document.subeArchivoPUN.submit();

			}
				
			else
				alert("archivo requerido");
		}
		
		

	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
		<form id="ImportaExcel" name="ImportaExcel" action="creacionAnteProyecto.jsp?cImporXLS=Si" method="post" ></form>
		<form id="ExportaExcel" name="ExportaExcel" action="../gstnmngr/AnteProyectoLayoutServlet" method="post" ></form>
			<h1>Techos Presupuestales</h1>
			
				<input type="hidden" value="" id="rowsAffected" name="rowsAffected">
				<input type="hidden" id="cRamo" name="cRamo" value="16">
				<input type="hidden" id="cCentroContable" name="cCentroContable">
				<input type="hidden" id="id_oper" value="">
				<input name="cUnidadEjecutora" type="hidden" id="cUnidadEjecutora" value="A02">
				<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
				<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="2012"/>
				
				<div id="multitabs" style="width: 110%">
					<div id="tabs" style="width: 100%">
						<ul>
							<li> <a id="LNK01" href="#tabs-5" class="LNK01">Unidad Normativa</a> </li>
							<li> <a id="LNK02" href="#tabs-3" class="LNK02">Unidad Ejecutora</a> </li>
							<li> <a id="LNK03" href="#tabs-1" class="LNK03">Partida</a> </li>
							<li> <a id="LNK04" href="#tabs-4" class="LNK04">Entidad Federativa</a></li>
							<li> <a id="LNK05" href="#tabs-2" class="LNK05">Programa Presupuestario</a></li>
							<li> <a id="LNK06" href="#tabs-6" class="LNK06">Partida Unidad Ejecutora</a></li>
							<li> <a id="LNK07" href="#tabs-7" class="LNK07">Programa Presupuestario Unidad Ejecutora</a></li>
							<li> <a id="LNK08" href="#tabs-8" class="LNK08">Partida Unidad Normativa</a></li>
						</ul>
						<div id="tabs-5"> 
							
							<table border="0" cellspacing="0" cellpadding="0" style="width: 550px">
								<tr><td  align="left"><form id="formAnteProy1" name="formAnteProy1" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input id="txtGridtUniNormC" name="txtGridtUniNormC" class="AyudaSyC" maxlength="5" size="4" type="text" title=""></input></form> </td>
								<td align="left"><form id="formAnteProy2" name="formAnteProy2" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input id="txtGridtUniNormD" name="txtGridtUniNormD" class="" maxlength="80" size="70" type="text"></input></form> </td>
								<td align="left"><form id="formAnteProy3" name="formAnteProy3" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input id="mMontoUN" name="mMontoUN" class="" maxlength="20" size="25" type="text"></input></form> </td>
								<td align="left">
									<input id="bTab5" name="bTab5" class="" maxlength="10" value="Agregar" size="15" type="button" onClick="fnAgregarUN();" ></input> 
									<input id="bImportUN" name="bImportUN" class="" maxlength="10" value="Importa UN" size="15" type="button" onClick="fnImportaExcel('un');" ></input>
								</td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoUN" name="subeArchivoUN" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEUN" name="archivoEUN" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="5"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('un');"/>
										</form>
		
									</td>
								</tr>
								<tr>
									<td align="right"> </td>
									<td align="right"></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
							</table>
							<form id="formAnteProy" name="formAnteProy" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							<table   class="display" id="grdUnidadNormativa">
								<thead>
									<tr>
			
										<th>Clave <input type="hidden" id="cUnidadNormativa" name="cUnidadNormativa"/></th>
										<th nowrap>Unidad Normativa</th>
										<th>Monto <input type="hidden" id="mTechoMonto" name="mTechoMonto"/></th>
										<th></th>
										
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
							</form>
						</div>
						<div id="tabs-1" >
							<table border="0">
								<tr><td>Partida:</td>
									<td><form id="formAnteProyP1" name="formAnteProyP1" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input type="text" name="txtGridOGTOC" 	class="AyudaSyC"  id="txtGridOGTOC"  value="" size="5"></form></td>
									<td nowrap><form id="formAnteProyP2" name="formAnteProyP2" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input type="text" name="txtGridOGTOD" 	class="" id="txtGridOGTOD" 	value="" size="60"></form></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td>Tipo Gasto:</td>
									<td><form id="formAnteProyP3" name="formAnteProyP3" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input name="txtGridCTG" 	class="AyudaSyC" type="text" id="txtGridCTG" value="" size="1"></form></td>
										<td><form id="formAnteProyP4" name="formAnteProyP4" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input name="dTipoGasto" 	class="" type="text" id="txtGridDTG" value="" size="60"></form></td>
										<td></td>
										<td></td>
								</tr>
								<tr><td>Techo$:</td>
									<td><form id="formAnteProyP5" name="formAnteProyP5" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input type="text" name="mTechoPartida" 	class="" type="hidden" id="mTechoPartida" 	value="0" maxlength="25" size="25"  ></form></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td>
										<input type="button"  id="bTab3" class="" name="bTab3" onClick="fnAgregarRet();" value="Agregar">
										<input id="bImportPA" name="bImportPA" class="" maxlength="10" value="Importa Partida" size="15" type="button" onClick="fnImportaExcel('ret');" ></input>
									</td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoRet" name="subeArchivoRet" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoERET" name="archivoERET" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="1"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('ret');"/>
										</form>
									</td>
								</tr>
							</table>
							<form id="formAnteProyP" name="formAnteProyP" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							<table   id="grdRetencion">
								<thead>
									<tr>
										<th>Clave <input type="hidden" id="cPartida" name="cPartida"/></th>
										<th>Descripción Partida </th>
										<th>Clave <input type="hidden" id="cTipoGasto" name="cTipoGasto"/></th>
										<th>Descripción Tipo Gasto</th>
										<th>$ Techo <input type="hidden" id="mTechoPartidaUpdate" name="mTechoPartidaUpdate"/></th>
										<th></th>
									</tr>
								</thead>
								<tbody style="width: 100%">
								</tbody>
								<tfoot>
								</tfoot>
							</table>
							</form>
						</div>
						<div id="tabs-2" >
						
							<table  border="0" cellspacing="0" cellpadding="0" style="width: 750px">
								<tr>
									<td><form id="formAnteProyPP1" name="formAnteProyPP1" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input class="AyudaSyC" name="txtGridtProgPresupC" type="text" id="txtGridtProgPresupC" onkeypress="" size="6" maxlength="6" title="Clave del Programa Presupuestario"></form></td>
									<td><form id="formAnteProyPP2" name="formAnteProyPP2" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input class="" name="txtGridtProgPresupD" type="text" id="txtGridtProgPresupD" onkeypress="" size="70" maxlength="200"></form></td>
									<td><form id="formAnteProyPP3" name="formAnteProyPP3" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input class="" name="mMontoTopeProgPres" type="text" id="mMontoTopeProgPres" onkeypress="" maxlength="25" size="20" title="Monto de Tope para el Programa Presupuestario"></form></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td colspan="5">
										<input class="" name="btab2"              type="button"  id="btab2" onClick="fnAgregarPP();" value="Agregar">
										<input id="bImportPP" name="bImportPP" class="" maxlength="10" value="Importa Programa Presupuestario" size="15" type="button" onClick="fnImportaExcel('programap');" ></input>
									</td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoPP" name="subeArchivoPP" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEPP" name="archivoEPP" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="2"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('pp');"/>
										</form>
									</td>
								</tr>
							</table>
							<form id="formAnteProyPP" name="formAnteProyPP" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							<table   class="display" id="grdProgPresup">
								<thead>
									<tr>
										<th>Clave <input type="hidden" id="cProgramaPresupuestario" name="cProgramaPresupuestario"/></th>
										<th nowrap>Programa Presupuestario</th>
										<th>$ Techo <input type="hidden" id="mTechoMontoPP" name="mTechoMontoPP"/></th>
										<th></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
							</form>
						</div>
						<div id="tabs-3">
							<table border="0" cellspacing="0" cellpadding="0" style="width: 550px">
								<tr>
									<td><form id="formAnteProy4" name="formAnteProy4" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input id="txtGridtUniEjeC" name="txtGridtUniEjeC" class="AyudaSyC" maxlength="3" size="3" type="text" title="Clave de la Unidad Ejecutora."></input></form></td>
									<td><form id="formAnteProy5" name="formAnteProy5" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input id="txtGridtUniEjeD" name="txtGridtUniEjeD" class="" maxlength="80" size="70" type="text"></input></form></td>
									<td rowspan="1" align="left"><form id="formAnteProy6" name="formAnteProy6" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input id="mMontoUE" name="mMontoUE" class="" maxlength="25" size="20" type="text" title="Monto del Techo para la Unidad Ejecutora"></input> </form></td>
									<td>
									<input type="button"  id="btab3" class="" name="btab3" onClick="fnAgregarUniEje();" value="Agregar">
									<input id="bImportPA" name="bImportPA" class="" maxlength="10" value="Importa UE" size="15" type="button" onClick="fnImportaExcel('ue');" ></input></td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoUE" name="subeArchivoUE" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEUE" name="archivoEUE" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="3"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('ue');"/>
										</form>
		
									</td>
								</tr>
							</table>
							<form id="formAnteProy6" name="formAnteProy6" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							<table   class="display" id="grdUniEje">
								<thead>
									<tr>
										<th>Clave <input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable"/></th>
										<th nowrap>Unidad Ejecutora</th>
										<th>$ Techo <input type="hidden" id="mTechoMontoUE" name="mTechoMontoUE"/></th>
										<th></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
							</form>
						</div>
						<div id="tabs-4">
						<form id="formAnteProy7" name="formAnteProy7" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							<table border="0" cellspacing="2" cellpadding="0">
								<tr>
									<td><input class="AyudaSyC" name="txtGridEntFederalC" type="text" id="txtGridEntFederalC" onkeypress="" size="6" maxlength="6" title="Clave de la Entidad Federativa"></td>
									<td><input class="" name="txtGridEntFederalCabr" type="text" id="txtGridEntFederalCabr" onkeypress="" size="70" maxlength="100"></td>
									<td><input class="" name="mMontoTopeEntidad" type="text" id="mMontoTopeEntidad" onkeypress="" size="20" maxlength="25" Title="Monto del Techo Presupuestal para la Entidad Federativa"></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td colspan="2"  align="center">
										<input type="button"  id="btab4" class="" name="btab4" onClick="fnAgregarEF();" value="Agregar">
										<input id="bImportPA" name="bImportPA" class="" maxlength="10" value="Importa Entidad" size="15" type="button" onClick="fnImportaExcel('entidad');" ></input>
									</td>
									<td>&nbsp;</td>
								</tr>
						</form>
								<tr>
									<td></td><td>
										<form id="subeArchivoEF" name="subeArchivoEF" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEEF" name="archivoEEF" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="4"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('ef');"/>
										</form>
		
									</td>
								</tr>
							</table>
							<form id="formAntePro8" name="formAnteProy8" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							<table   class="display" id="grdMovimientos">
								<thead>
									<tr>
										<th align="left">Clave <input type="hidden" id="cEntidadFederativa" name="cEntidadFederativa"/></th>
										<th nowrap>Entidad Federativa</th>
										<th align="left">% Techo <input type="hidden" id="mTechoMontoEF" name="mTechoMontoEF"/></th>
										<th></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
							</form>
						</div>
						
						<!-- PARTIDA UNIDAD EJECUTORA -->
						
						<div id="tabs-6"> <!-- brenda -->
						<form id="formAntePro11" name="formAnteProy11" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							
							<table border="0" cellspacing="2" cellpadding="0">
								<tr><td>Partida: <input type="hidden" id="cPartidaPUE" name="cPartidaPUE"/></td>
									<td><input type="text" name="txtGridOGTOCPUE" 	class="AyudaSyC"  id="txtGridOGTOCPUE"  value="" size="5"></td>
									<td nowrap><input type="text" name="txtGridOGTODPUE" 	class="" id="txtGridOGTODPUE" 	value="" size="60"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td>Tipo Gasto: <input type="hidden" id="cTGPUE" name="cTGPUE"/></td>
									<td><input name="txtGridCTGPUE" 	class="AyudaSyC" type="text" id="txtGridCTGPUE" value="" size="1"></td>
										<td><input name="dTipoGastoPUE" 	class="" type="text" id="txtGridDTGPUE" value="" size="60"></td>
										<td></td>
										<td></td>
								</tr>
								<tr><td>Unidad: <input type="hidden" id="cUPUE" name="cUPUE"/></td>
									<td><input type="text" name="txtGridtUniEjeCPUE" 	class="AyudaSyC"  id="txtGridtUniEjeCPUE"  value="" size="5"></td>
									<td nowrap><input type="text" name="txtGridtUniEjeDPUE" 	class="" id="txtGridtUniEjeDPUE" 	value="" size="60"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td>Techo$: <input type="hidden" id="mTechoMontoPUE" name="mTechoMontoPUE"/></td>
									<td><input type="text" name="mTechoPUE" class=""  id="mTechoPUE" value="0" maxlength="25" size="20" /></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								</form>
								<tr>
									<td></td>
									<td colspan="2"  align="center">
										<input type="button"  id="btab4" class="" name="btab4" onClick="fnAgregarPUE();" value="Agregar">
										<input id="bImportPA" name="bImportPA" class="" maxlength="10" value="Importa Partida UE" size="15" type="button" onClick="fnImportaExcel('partidaue');" ></input>
									</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoPUE" name="subeArchivoPUE" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEPUE" name="archivoEPUE" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="6"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('partidaue');"/>
										</form>
									</td>
								</tr>
							</table>
							<table   class="display" id="grdPTGUE">
								<thead>
									<tr>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Partida</th>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Tipo de Gasto</th>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Unidad</th>
										<th align="left">% Techo</th>
										<th></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>

						<div id="tabs-7">
						
							<table  border="0" cellspacing="0" cellpadding="0" style="width: 750px">
								<tr>
									<td><form id="formAnteProy21" name="formAnteProy21" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">Programa Presupuestario:<input type="hidden" id="cProgramaPresuPPUE" name="cProgramaPresuPPUE"/><input class="AyudaSyC" name="txtGridtProgPresupCU" type="text" id="txtGridtProgPresupCU" onkeypress="" size="6" maxlength="6" title="Clave del Programa Presupuestario"></form></td>
									<td><form id="formAnteProy22" name="formAnteProy22" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input class="" name="txtGridtProgPresupDU" type="text" id="txtGridtProgPresupDU" onkeypress="" size="70" maxlength="200"></form></td>
									</tr>
								<tr>
									<td><form id="formAnteProy23" name="formAnteProy23" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">Unidad Ejecutora:<input type="hidden" id="cUnidadResponsalePPUE" name="cUnidadResponsalePPUE"/><input class="AyudaSyC" name="txtGridtProgPresupCUE" type="text" id="txtGridtProgPresupCUE" onkeypress="" size="6" maxlength="6" title="Clave de la Unidad Ejecutora"></form></td>
									<td><form id="formAnteProy24" name="formAnteProy24" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post"><input class="" name="txtGridtProgPresupDUE" type="text" id="txtGridtProgPresupDUE" onkeypress="" size="70" maxlength="200"></form></td>
								</tr>
								<tr>
								<td><form id="formAnteProy25" name="formAnteProy25" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">$ Techo:<input type="hidden" id="mTechoMontoPPUE" name="mTechoMontoPPUE"/><input class="" name="mMontoTopeProgPresU" type="text" id="mMontoTopeProgPresU" onkeypress="" maxlength="20" size="15" title="Monto de Tope para el Programa Presupuestario"></form></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td colspan="5">
										<input class="" name="btab7"              type="button"  id="btab2" onClick="fnAgregarProgPresupUE();" value="Agregar">
										<input id="bImportPPUE" name="bImportPPUE" class="" maxlength="10" value="Importa Programa Presupuestario Unidad Ejecutora" size="15" type="button" onClick="fnImportaExcel('progprue');" ></input>
									</td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoProgPrUE" name="subeArchivoProgPrUE" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEProgPr" name="archivoEProgPr" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="7"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('progprue');"/>
										</form>
									</td>
								</tr>
							</table>
							<table   class="display" id="grdProgPresupU">
								<thead>
									<tr>
										<th>Clave </th>
										<th nowrap>Programa Presupuestario</th>
										<th>Clave </th>
										<th nowrap>Unidad Ejecutora</th>
										<th>$ Techo </th>
										<th></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>

						<!-- PARTIDA UNIDAD NORMATIVA --> <!-- brenda -->
						
						<div id="tabs-8">
						<form id="formAnteProUN" name="formAnteProyUN" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
							
							<table border="0" cellspacing="2" cellpadding="0">
								<tr><td>Partida: <input type="hidden" id="cPartidaPUN" name="cPartidaPUN"/></td>
									<td><input type="text" name="txtGridOGTOCPUE2" 	class="AyudaSyC"  id="txtGridOGTOCPUE2"  value="" size="5"></td>
									<td nowrap><input type="text" name="txtGridOGTODPUE2" 	class="" id="txtGridOGTODPUE2" 	value="" size="60"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td>Tipo Gasto: <input type="hidden" id="cTGPUN" name="cTGPUN"/></td>
									<td><input name="txtGridCTGPUE2" 	class="AyudaSyC" type="text" id="txtGridCTGPUE2" value="" size="1"></td>
										<td><input name="dTipoGastoPUE2" 	class="" type="text" id="txtGridDTGPUE2" value="" size="60"></td>
										<td></td>
										<td></td>
								</tr>
								<tr><td>Unidad: <input type="hidden" id="cUPUN" name="cUPUN"/></td>
									<td><input type="text" name="txtGridtUniNormC2" 	class="AyudaSyC"  id="txtGridtUniNormC2"  value="" size="5"></td>
									<td nowrap><input type="text" name="txtGridtUniNormD2" 	class="" id="txtGridtUniNormD2" 	value="" size="60"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td>Techo$: <input type="hidden" id="mTechoMontoPUN" name="mTechoMontoPUN"/></td>
									<td><input type="text" name="mTechoPUN" class=""  id="mTechoPUN" value="0" maxlength="25" size="20" /></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								</form>
								<tr>
									<td></td>
									<td colspan="2"  align="center">
										<input type="button"  id="btab4" class="" name="btab4" onClick="fnAgregarPUN();" value="Agregar">
										<input id="bImportPA" name="bImportPA" class="" maxlength="10" value="Importa Partida UN" size="15" type="button" onClick="fnImportaExcel('partidaun');" ></input>
									</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td></td><td>
										<form id="subeArchivoPUN" name="subeArchivoPUN" method="post" action="../gstnmngr/TechosExcel" enctype="multipart/form-data">
											<input type="file" id="archivoEPUN" name="archivoEPUN" size="17" value=""/>
											<input type="hidden" id="pestana" name="pestana" value="8"/>
											<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('partidaun');"/>
										</form>
									</td>
								</tr>
							</table>
							<table   class="display" id="grdPTGUN">
								<thead>
									<tr>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Partida</th>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Tipo de Gasto</th>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Unidad</th>
										<th align="left">% Techo</th>
										<th></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>

					
					</div>
				</div>
		
		</div>
	<div id="dialog" title="Detalle de AnteProyecto">
		<p>Consulta de Creacion del Presupuesto </p> <a rel=""></a>
		<table   class="display" id="grdAnteProyecto">
			<thead>
				<tr>
					<th>Consecutivo</th>
					<th>Clave EP</th>
					<th>$ Calculado</th>
					<th>$ Optimo</th>
					<th>$ Ireductible</th>
					<th>¿Reduccion?</th>
					<th>¿Incremento?</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
			<tfoot>
			</tfoot>
		</table>
	</div>

	</body>
</html>
