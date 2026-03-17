<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	   String user_login=usuario.getLogin();
    String roles="";
    Map rol =usuario.getRoles();
    String cEjercicio = "";
	String cIdTipoProcedimiento = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento=(String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Procedimiento</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
			var roles="";
			$(document).ready(function() {
				$("#tbs").val(1);
				showAndHideTabs();
				<%
				int tabla=0;
				int consulta=0;
			
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
				Map pestanas=ebl.getPestana(roles,"Procedimiento");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();
					String pestana=(String)e.getValue();
					if ("CaratulaProcedimiento".equals(pestana)){
						 tabla=1;
					}
					if ("ConsultaProcedimiento".equals(pestana)){
						 consulta=1;
					}
				}
				Map botones=nb.getBotones(roles,"Procedimiento","ConsultaProcedimiento");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}

				%>			
				var tabla='<%=tabla%>';
				var consulta='<%=consulta%>';
				roles="<%=roles%>";
				queryFormPost("mSistema_cEjercicioRead", {async: false});
				if (roles.indexOf("ADMIN_RECMAT") >= 0){
					$("#isAdmin").val(0);
				}
				agregaDatePickerFechas();
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora", {async: false});
				//querySelectPost("UnidadEjecutoraReadFiltro", "cboUnidadEjecutora", {async: false });
				querySelectPost("TipoProcedimientoConsultarRead", "cboTipoProcedimiento", {async: false });
				querySelectPost("EstadoProcedimientoRead", "cboEstadoConsultar", {async: false });
				querySelectPost("tipoProcesoProcedimientoAll","tipoProcesoProcedimientoBuscar",{async:false});
				if (consulta==0){
					mostrarTablaProcedimientos();
				}
				
				$("#btnBuscarConsultaProcedimiento" ).button().click(function() {
						mostrarTablaProcedimientos();
							//window.location.reload();
				});	
				
						////EVENTO DOBLECLICK EN LA TABLA DE PROCEDIMIENTOS DISPONIBLES
		
			//Al darle doble click a la tabla de porcedimientos
			$('#tblProcedimientos tr').live('dblclick', function() {
				//Actualizar las variables necesarias
				if (tabla==0){
						$(this).addClass('row_selected');
						var anSelected = fnGetSelected( oTable );
						if (anSelected != "") {
							var aData = oTable.fnGetData(anSelected[0]);
							window.location = aData[0];
						}
					}
				
			});		
		});
		var where_='';	
		function mostrarTablaProcedimientos(){
		queryFormPost("mSistema_cEjercicioRead", {async: false});
        	//Mostramos la tabla de procedimientos
			$("#tblProcedimientos").css("display", ""); 
			$("#tblProcedimientos tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					//$("#pbDesplegar").css("visibility","visible");
				});
			
			//Parametros de busqueda opcionales
			var consulta=" cEjercicio='"+$("#cEjercicio").val()+"'" ;
				if (($.trim($("#cboTipoProcedimiento").val()))!='0')
				{
					consulta += "AND cIdTipoProcedimiento LIKE '%25" + $.trim($("#cboTipoProcedimiento").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				
				if (($.trim($("#cboEstadoConsultar").val()))!=0)
				{
					consulta += "AND nIdEstado LIKE '%25" + $.trim($("#cboEstadoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}

				if (($.trim($("#procedimientoConsultar").val()).length)>0)
				{
					consulta += "AND cIdProcedimiento LIKE '%25" + $.trim($("#procedimientoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if(($.trim($("#numeroConsultar").val()).length)>0){
					consulta += " AND nIdConsecutivo LIKE '%25" + $.trim($("#numeroConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if(($.trim($("#descripcionConsultar").val()).length)>0){
					consulta += " AND cDescripcion LIKE '%25" + $.trim($("#descripcionConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if(($.trim($("#consolidadoConsultar").val()).length)>0){
					consulta += " AND cIdConsolidado LIKE '%25" + $.trim($("#consolidadoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if($("#tipoProcesoProcedimientoBuscar").val() != 0){
					consulta += " AND IdProceso = "+$("#tipoProcesoProcedimientoBuscar").val();
				}
							
				if($("#cboUnidadEjecutora").val() != 0){
					consulta += " AND cIdUnidadEjecutora= '"+$("#cboUnidadEjecutora").val()+"'";
				}
				
			var rangoFechas=" and fProcedimiento BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";	
			consulta += " AND cIdTipoProcedimiento NOT IN (SELECT cIdTipoProcedimiento from mCatalogoTipoProcedimiento WHERE cIdTipoProcedimiento = 'PI' OR cIdTipoProcedimiento = 'PF') ";
			consulta +=rangoFechas;
			where_=consulta;
			oTable = $('#tblProcedimientos').dataTable({
				"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
				"bFilter" : false,
				"bDestroy" : true,
				"bJQueryUI": true,
				"bAutoWidth" : false,
				sScrollX: "100%",
				"iDisplayLength": 5, //Cuantos registros se despliegan
				"sPaginationType": "full_numbers",
				"oLanguage": {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros <h5>Doble click para seleccionar procedimiento</h5>",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneProcedimientos&qw="+consulta,
					aaSorting: [[ 1, "asc" ],[ 2, "asc" ],[3,"asc"]] ,			
					aoColumns: [
						{ sName: "cVinculo", bVisible: false },
						{ sName: "cIdTipoProcedimiento" },
						{ sName: "cIdUnidadEjecutora"   },
						{ sName: "nIdConsecutivo" },
						{ sName: "cDescripcion"	},
						{ sName: "cEstado"	},
						{ sName: "cCategoria"	},
						{ sName: "cIdConsolidado"	},
						{ sName: "cIdProcedimiento"	},
						{ sName: "tipoProceso"	},
						//Campos ocultos
						{ sName: "nIdCategoria",bVisible: false},
						{ sName: "cIdUsuarioCreacion",bVisible:false},
						{ sName: "nIdEstado",bVisible:false},
						{ sName: "cOficio",bVisible:false}
					]
				
			});
		}

			
		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ ){
				if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		function openARCH(ext){
			var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
			        + "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=ReporteConsultaProcedimientos.jasper"
					+"&formato="+ext
					+ "&where_=" + where_, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function openCSV(){
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn=ReporteConsultaProcedimientos"
				+ "&where_=" + where_,
				 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function cambiaCentrocontableUsuario(){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cboUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"info",button: "Cerrar"});
					}else{
						$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
						
						
					}
				}
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
		</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Consulta Procedimiento</legend>
				<input type="hidden" id="cEjercicio" name="cEjercicio" >
				<div align="center">
					<table border="0" align="center" width="700px">
							   <tr>
								 <td align="right">Unidad Ejecutora:</td>
								 <td align="left">
									<select id="cboUnidadEjecutora" name="cboUnidadEjecutora" style="width: 30em;" onchange="cambiaCentrocontableUsuario();">
										<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
									</select>
								</td>
							 </tr>
							 <tr>
								<td align="right">Tipo de Procedimiento:</td>
								<td align="left">
									<select id="cboTipoProcedimiento" name="cboTipoProcedimiento"style="width: 30em;" onchange="">
										<option value="" selected="selected"></option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">Estado:</td>
								<td align="left">
									<select id="cboEstadoConsultar" name="cboEstadoConsultar" style="width: 30em;" onchange="">
										<option value="" selected="selected"></option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">N&uacute;mero:</td>
								<td align="left"><input type="text" id="numeroConsultar" name="numeroConsultar"style="width: 30em;" /></td>
							</tr>
							<tr>
								<td align="right">Procedimiento:</td>
								<td align="left">
									<input type="text" id="procedimientoConsultar" name="procedimientoConsultar"style="width: 30em;" />
								</td>
							</tr>
							
							<tr>
								<td align="right">Descripci&oacute;n:</td >
								<td align="left"><input type="text" id="descripcionConsultar" name="descripcionConsultar"style="width: 30em;" /></td>
							</tr>
							<tr>
								<td align="right">Consolidado:</td>
								<td align="left"><input type="text" id="consolidadoConsultar" name="consolidadoConsultar"style="width: 30em;" /></td>
							</tr>
							<tr>
								<td align="right">Tipo de Proceso:</td>
								<td align="left">
								   <select id="tipoProcesoProcedimientoBuscar" name="tipoProcesoProcedimientoBuscar"	style="width: 30em;">
									     <option value="" selected="selected"></option>
								   </select>
							    </td>
							</tr>
							<tr>
									<td align="right">
			    						Fecha Inicio:</td>
			    					<td align="left">   
			    						<input type="text" id="fInicio" name="fInicio" value="<%=todayAnt %>" readonly="readonly" class="desahabilitado"/>
			    					
			    						Fecha Fin:<input type="text" id="fFin" name="fFin" value="<%=today %>" readonly="readonly" class="desahabilitado"/>
			    					</td>
								</tr>
						    	<tr>
							<tr>
								<td colspan="2" align="center">
									<input type="button" name="btnBuscarConsultaProcedimiento" id="btnBuscarConsultaProcedimiento" value="Buscar"  class="btnInterfaceBG ui-button ui-corner-all"/>
								</td>								
							</tr>
							<tr> <td align="left">
								<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"		onclick="openARCH('pdf');" />&nbsp;&nbsp;
			    				<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdxlsReporteProg" 		name="cmdxlsReporteProg" 	value="Excel"	onclick="openARCH('xls');" />&nbsp;&nbsp;
								</td>
							</tr>						
					
			</table>
			<div id= "div_tblProcedimientos" align="center">
							<table id="tblProcedimientos" class="display" >
							   <thead>
							      <tr >
							      	<th ></th>
				                	<th >&nbsp;</th>
				                	<th >&nbsp;</th>
				                    <th >&nbsp;</th>
				                    <th >Descripci&oacute;n</th>
				                    <th >Estado</th>
				                    <th >Categor&iacute;a</th>
				                    <th >Consolidado</th>
				                    <th >Procedimiento</th>
				                    <th >Tipo Proceso</th>
				                    <th >IdCategoria</th>
				                    <th >IdUsuarioCreacion</th>
				                    <th >IdEstado</th>
				                    <th >cOficio</th>
				                 </tr>
				             </thead>
				           </table>
				      </div>
	     </div>
	   </fieldset>
	   	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
	    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
	 </form>				
	</body>
</html>