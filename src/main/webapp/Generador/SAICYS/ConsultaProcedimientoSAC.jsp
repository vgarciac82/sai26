<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'ConsultaProcedimientoSAC.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script>
	var oTableConsulta;
		$(document).ready(function() {
			$("#tbs").val(0);
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"SAC","ConsultaProcedimientoSAC");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			initTabla();
			agregaDatePickerFechas();
			$('#tblConsulta tr').live('dblclick', function() { 
				        
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableConsulta );
				if (anSelected != "") {
					var aData = oTableConsulta.fnGetData(anSelected[0]);
					window.location = aData[0];
				}
				
			});
		});
		function  initTabla(){
		oTableConsulta= $("#tblConsulta").dataTable({
				bPaginate: true,
      			bLengthChange: true,
      			bFilter: true,
      			bSort: false,
      			bInfo: false,
				sScrollX: "100%",
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				aaSorting: [[ 0, "asc" ]]
			});
		}
		function agregaDatePickerFechas(){
			$("#fInicio").datepicker({
				dateFormat: "dd/mm/yy",
				altField: "#actualDate",
			 	currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			   	buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
			});
			$("#fFin").datepicker({
				dateFormat: "dd/mm/yy",
				altField: "#actualDate",
			 	currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			   	buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				
			});
		}
		function consultaTabla(){
			var qw=" 1=1 ";
			
	        $("#tblConsulta").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "80%",
				sScrollX: "100%",
				bAutoWith: true,
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
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mConsultaProcedSAC('"+$("#fInicio").val()+"','"+$("#fFin").val()+"')&qw=" + qw,
				bJQueryUI: true,
				aaSorting: [[ 2, "asc" ],[3, "asc"]] ,
				aoColumns: [
						{sName: "cVinculo",bVisible: false},
						{sName: "nIdProcedimientoSAC",bVisible: false},
						{ sName: "numero" },
						{ sName: "fSolicitud" },
						{ sName: "cOficioSolicitud" },
						{ sName: "areaReq" },
						{ sName: "cAreaTecnica" },
						{ sName: "cAreaResponsable" },
						{ sName: "cProcedimientoTurnado" },
						{ sName: "tipoProcedimiento" },
						{ sName: "materiaProced" },
						{ sName: "cDenominacionProced" },
						{ sName: "cIdProcedimientoSAC" },
						{ sName: "cProyectoConvocatoria" },
						{ sName: "fAutConvocatoria" },
						{ sName: "fConvocatoria" },
						{ sName: "fJuntaAclaraciones" },
						{ sName: "fAperturaProposiciones" },
						{ sName: "fEvaluacionTecnica" },
						{ sName: "fFallo_ActaAdjucdicacion" },
						{ sName: "fGeneracionContratoCNET" },
						{ sName: "paricipantes" },
						{ sName: "fExpedienteTurnadoContrato" },
						{ sName: "proveedorDadoAlta" }
				]
			});
		}
	</script>
  </head>
  
  <body onkeydown="return checkShortcut()">
	<form id="formConsulta">
    	<div id="container" class="container" style="width: 98%;" align="left">
    		<fieldset style="width: 95%" >
    			<legend>Consulta SAC</legend>
    			<table style="width: 100%" align="left" >
    				<tr style="display: none;">
    					<td style="width: 10%">
    						&Aacute;rea Requirente:
    					</td>
    					<td style="width: 90%">
    						<select name='cAreaRequirente' id='cAreaRequirente' style='width: 40%' onchange="obtieneAreasResponsables()"></select>
    					</td>
    				</tr>
    				<tr>
			    		<td colspan="2" align="left">
    						Fecha Solicitud Inicio:<input type="text" id="fInicio" name="fInicio" value="<%=todayAnt %>" readonly="readonly" class="desahabilitado"/>
    					
    						Fecha Solicitud Fin:<input type="text" id="fFin" name="fFin" value="<%=today %>" readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
			    		<td style="width: 30%" align="center">
			    			<input type="button" id="buscarRecepMat" name="buscarRecepMat" value="Buscar" onclick="consultaTabla()" align="middle" class="btnInterfaceBG ui-button ui-corner-all"/>
			    		</td>
			    		<td style="width: 60%" id="tdXlsx" align="right" >
			    		<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdXlsxReporteProg" 		name="cmdXlsxReporteProg" 	value="Semáforo SAC"	onclick="openXLSX();" />&nbsp;&nbsp;
			    		</td>
    				</tr>
    			</table>
    			<br />
    			<br />
    			<br />
    			<table id="tblConsulta" class="display" style="width: 100%" >
					<thead>
						<tr>
							<th style="display: none;"></th>
							<th style="display: none;"></th>
							<th align="center">No.</th>
							<th align="center">Fecha<br />Solicitud</th>
							<th align="center">Oficio<br />Solicitud</th>
							<th align="center">Área <br />Requirente</th>
							<th align="center">Área <br />Técnica</th>
							<th align="center">Área <br />Responsable</th>
							<th align="center">Procedimiento <br />Contratación<br/>Turnado</th>
							<th align="center">Tipo <br />Procedimiento</th>
							<th align="center">Materia <br />Procedimiento</th>
							<th align="center">Denominación<br />Procedimiento</th>
							<th align="center">Número <br />Procedimiento</th>
							<th align="center">Proyecto<br />Convocatoria</th>
							<th align="center">Fecha<br />Autorización<br/>Convocatoria</th>
							<th align="center">Fecha<br />Publicación<br/>Convocatoria</th>
							<th align="center">Fecha<br />Junta<br/>Aclaraciones</th>
							<th align="center">Fecha<br />Apertura<br/>Proposiciones</th>
							<th align="center">Fecha<br />Evaluación<br/>Tecnica</th>
							<th align="center">Fecha<br />Fallo/Acta<br/>Adjudicación</th>
							<th align="center">Fecha<br />Generación de<br/>Contrato</th>
							<th align="center">Participantes en<br />el Proceso</th>
							<th align="center">Fecha<br />Expediente<br/>Turnado</th>
							<th align="center">Proveedor<br />Dado de<br/>Alta (SAI/CNET)</th>
							
						</tr>										
					</thead>
				</table>
    		</fieldset>
    	</div>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="nTipoReporte" id="nTipoReporte" value="1" />
    	
    </form>
  </body>
</html>
