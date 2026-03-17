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
	String cIdTipoConsolidado = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	session.setAttribute(GestionInterface.ATT_CASE, null);
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		cIdTipoConsolidado = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
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
		<title>Consolidado</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
		
		<script type="text/javascript" charset="utf-8">
		var roles="";
		$(document).ready(function() {
			$("#tbs").val(2);
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
			Map pestanas=ebl.getPestana(roles,"Consolidado");
			Iterator it = pestanas.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				String pestana=(String)e.getValue();
				if ("CaratulaConsolidado".equals(pestana)){
					 tabla=1;
				}
				if ("ConsultaConsolidado".equals(pestana)){
					 consulta=1;
				}
			}
			Map botones=nb.getBotones(roles,"Consolidado","ConsultaConsolidado");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%			
			}

		%>			
			var tabla='<%=tabla%>';
			var consulta='<%=consulta%>';
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			agregaDatePickerFechas();
			//querySelectPost("UnidadEjecutoraConsolidadoRead", "cIdUnidadEjecutoraCon", {async: false });
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutoraCon", {async: false});
			querySelectPost("mTipoConsolidadoRead", "cIdTipoConsolidadoCon", {async : false});		
			queryFormPost("mSistema_cEjercicioRead", {async: false});	
			//UE'S Estatales
			if('<%=usuario.getU_UR().trim()%>'   == 'G01'  ||'<%=usuario.getU_UR().trim()%>' == 'G15'  ||'<%=usuario.getU_UR().trim()%>' == 'G28'
				||'<%=usuario.getU_UR().trim()%>' == 'G02' ||'<%=usuario.getU_UR().trim()%>' == 'G16'  ||'<%=usuario.getU_UR().trim()%>' == 'G29'
				||'<%=usuario.getU_UR().trim()%>' == 'G04' ||'<%=usuario.getU_UR().trim()%>' == 'G17'  ||'<%=usuario.getU_UR().trim()%>' == 'G30'				
				||'<%=usuario.getU_UR().trim()%>' == 'G04' ||'<%=usuario.getU_UR().trim()%>' == 'G18'  ||'<%=usuario.getU_UR().trim()%>' == 'G31'
				||'<%=usuario.getU_UR().trim()%>' == 'G05' ||'<%=usuario.getU_UR().trim()%>' == 'G19'  ||'<%=usuario.getU_UR().trim()%>' == 'G32'
				||'<%=usuario.getU_UR().trim()%>' == 'G06' ||'<%=usuario.getU_UR().trim()%>' == 'G20'
				||'<%=usuario.getU_UR().trim()%>' == 'G07' ||'<%=usuario.getU_UR().trim()%>' == 'G21'
				||'<%=usuario.getU_UR().trim()%>' == 'G08' ||'<%=usuario.getU_UR().trim()%>' == 'G22'
				||'<%=usuario.getU_UR().trim()%>' == 'G09' ||'<%=usuario.getU_UR().trim()%>' == 'G23'
				||'<%=usuario.getU_UR().trim()%>' == 'G10' ||'<%=usuario.getU_UR().trim()%>' == 'G24'
				||'<%=usuario.getU_UR().trim()%>' == 'G11' ||'<%=usuario.getU_UR().trim()%>' == 'G25'
				||'<%=usuario.getU_UR().trim()%>' == 'G12' ||'<%=usuario.getU_UR().trim()%>' == 'G26'
				||'<%=usuario.getU_UR().trim()%>' == 'G13' ||'<%=usuario.getU_UR().trim()%>' == 'G27'
				||'<%=usuario.getU_UR().trim()%>' == 'G14'){
					querySelectPost("mAlcanceRead", "nIdAlcanceCon", {async : false});	
				}
			else {//Centrales
				querySelectPost("mAlcanceAdminRead","nIdAlcanceCon",{async : false});
			}
			querySelectPost("mEstadoRead", "nIdEstadoCon", {async: false });				
			
			if (consulta==0){
				mostrar();
			}
			
			$('#tblConsultaConsolidados tr').live('dblclick', function() {         
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
		var where_="";
		function mostrar() {
			queryFormPost("mSistema_cEjercicioRead", {async: false});	
				var consulta='<%=consulta%>';
				if (consulta==0){
					var qw = " 1 = 1";		
					var qw = " cEjercicioCons = '" + $("#cEjercicio").val() +"' "
					//"' AND cIdUnidadEjecutoraCons = '" + $("#cIdUnidadEjecutoraCon").val() + 
					if ($("#cIdTipoConsolidadoCon").val() != "0")
						qw +=" AND cIdTipoConsolidadoCons LIKE '" + $("#cIdTipoConsolidadoCon").val()+"'";		
					if ($("#nIdEstadoCon").val() != "0")
					qw += " AND nIdEstadoCons = " + $("#nIdEstadoCon").val();			
					if ($("#nIdAlcanceCon").val() != "0")
					qw += " AND nIdAlcanceCons = " + $("#nIdAlcanceCon").val();
				
					if ($("#cIdUnidadEjecutoraCon").val() != "0")
					qw += " AND cIdUnidadEjecutoraCons = '" + $("#cIdUnidadEjecutoraCon").val()+"'";
				
					
					qw += " AND cDescripcionCons LIKE '%25" + $("#cDescripcionCon").val() + "%25'";
					qw+= " AND cIdConsolidadoCons LIKE '%25"+ $("#cIdConsolidadoCon").val()+"%25'";
					qw+= " AND cIdTipoConsolidadoCons NOT IN (SELECT cIdTipoConsolidado from mCatalogoTipoConsolidado WHERE cIdTipoCOnsolidado = 'CI' OR cIdTipoConsolidado = 'CF') ";
					var rangoFechas=" and fCreacion BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
					qw +=rangoFechas;
					where_=qw+" ";			
					oTable = $("#tblConsultaConsolidados").dataTable({
						bScrollCollapse: true,
		        		bInfo: false,
		        		//sScrollY : "100%",
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
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vconsultaConsolidado&qw=" + qw,
						bJQueryUI: true,
						bProcessing: true,
						aaSorting: [[ 1, "asc" ], [ 2, "asc" ],[ 3, "asc" ]] ,
						aoColumns: [		
							{ sName: "cVinculo", bVisible: false },
							{ sName: "cIdTipoConsolidadoCons" },
							{ sName: "cIdUnidadEjecutoraCons" },
							{ sName: "nIdConsecutivo" },
							{ sName: "cAlcanceCons" },
							{ sName: "cEstadoCons" },
							{ sName: "mMontoConsolidadoCons" },
							{ sName: "mMontoConsolidadoIVACons" },
							{ sName: "cDescripcionCons" },
							{ sName: "nCantidadLineasCons" },
							{ sName: "cProcedimientoCons" },
							{ sName: "nTotalPedidosCons" }
						]
	        	});
			}
				
		}
			
		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		function openARCH(ext){
			var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
			        + "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=ReporteConsultaCons.jasper"
					+"&formato="+ext
					+ "&where_=" + where_, 'Procesando', 'status=1, width=400px, height=200px, left=150px');

		}
		function openCSV(){
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn=ReporteConsolidados"
				+ "&where_=" + where_,
				 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function cambiaCentrocontableUsuario(){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutoraCon").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"warning",button: "Cerrar"});
					}else{
						$("#cIdUnidadResponsableUsuario").val(j[0].unidadEjecutora);
						$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
						if(j[0].unidadEjecutora.indexOf("G")>=0){
							querySelectPost("mAlcanceRead", "nIdAlcanceCon", {async : false});	
						}else{
							querySelectPost("mAlcanceAdminRead","nIdAlcanceCon",{async : false});
							$("#nIdAlcanceCon").val(1);
						}
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
				<legend>Consulta Consolidados</legend>
				<table align="left">
			   		<tr id="trArea" align="left">
						<td>Unidad Ejecutora:</td>
						<td><select id="cIdUnidadEjecutoraCon" name="cIdUnidadEjecutoraCon"style="width: 40em;" onchange="cambiaCentrocontableUsuario();"><option value="<%=usuario.getU_UR()%>" selected="selected"></select></td>
					</tr>
					<tr id="trTipoCons" align="left">
						<td>Tipo de Consolidado:</td>
						<td><select id="cIdTipoConsolidadoCon" name="cIdTipoConsolidadoCon" style="width: 40em;"></select></td>
					</tr>
					<tr id="trAlcance" align="left">
						<td>Alcance:</td>
						<td><select id="nIdAlcanceCon" name="nIdAlcanceCon"style="width: 40em;"></select></td>
					</tr>
					<tr id="trNumero" align="left"> 
						<td>N&uacute;mero:</td>
						<td><input type="text" name="cIdConsolidadoCon"  onkeypress="return onlyIntegers(event);"   id="cIdConsolidadoCon" style="width: 40em;"/>	</td>
					</tr>
					<tr id="trEstado" align="left">
						<td>Estado:</td>
						<td><select id="nIdEstadoCon" name="nIdEstadoCon" style="width: 40em;"></select></td>
					</tr>	
					<tr id="trDescripcionCon" align="left">
						<td>Descripci&oacute;n:</td>
						<td><input type="text" name="cDescripcionCon" id="cDescripcionCon" style="width: 40em;" /></td>
					</tr>
					<tr>
						<td align="left">
	   						Fecha Inicio:</td>
	   					<td align="left">   
	   						<input type="text" id="fInicio" name="fInicio" value="<%=todayAnt %>" readonly="readonly" class="desahabilitado"/>
	   					
	   						Fecha Fin:<input type="text" id="fFin" name="fFin" value="<%=today %>" readonly="readonly" class="desahabilitado"/>
	   					</td>
					</tr>
			    	<tr>
			    		<td colspan="2" align="center"><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" onclick="mostrar();" class="btnInterfaceBG ui-button ui-corner-all"/></td>
			    	</tr>
			    	<tr >
		    			<td >
							<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"		onclick="openARCH('pdf');">&nbsp;&nbsp;
							<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdxlsReporteProg" 		name="cmdxlsReporteProg" 	value="Excel"	onclick="openARCH('xls');">&nbsp;&nbsp;
						</td>
		    		</tr>
			    </table>
			    <br />
			    <table id="tblConsultaConsolidados" class="display">
		        	<thead>
		        		<tr align="center">
		        			<th></th>
		        			<th></th>
		        			<th></th>
		        			<th></th>
		        			<th>ALCANCE</th>
		        			<th>ESTADO</th>
		        			<th>MONTO BRUTO</th>
		        			<th>MONTO C/IVA</th>
		        			<th>DESCRIPCION</th>
		        			<th>PARTIDAS</th>
		        			<th>PROC.</th>
		        			<th>P/C</th>						        			
		        		</tr>
		        	</thead>
		        </table>
				
			</fieldset>
			<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuario.getU_UR() %>"/>
			<input id="cEjercicio" name="cEjercicio" type="hidden" size="4">
			<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
		    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
		    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
		</form>				
	</body>
</html>