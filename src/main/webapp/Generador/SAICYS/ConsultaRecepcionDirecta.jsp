<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab1.getLogin();
	String idRol="0";	
	String Role ="";
	Map rol =usuarioTab1.getRoles();
	
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
    
    <title>Consulta Recepcion</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>
	
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>	
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>	

	
	<script type="text/javascript" charset="utf-8">
		var oTable;
		var roles="";
		$(document).ready(function() {
			<%
				int tabla=0;
				int consulta=0;
				String roles="";
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
				Map pestanas=ebl.getPestana(roles,"Requisiciones");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();
					String pestana=(String)e.getValue();
					if ("LineasRequisiciones".equals(pestana)){
						 tabla=1;
					}
					if ("ConsultaRequisiciones".equals(pestana)){
						 consulta=1;
					}
				}
				Map botones=nb.getBotones(roles,"Requisiciones","ConsultaRequisiciones");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
				%>			
				var tabla='<%=tabla%>';
				var consulta='<%=consulta%>';
				
			
			$('#tblSolicitudesRequisicion tr').live('dblclick', function() {
				if (tabla==0){
					$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );
					if (anSelected != "") {
						if($("#cIdUnidadEjecutora").val()=='0'){
							cambiaCentrocontableUsuario2(anSelected[2]);
						}
						var aData = oTable.fnGetData(anSelected[0]);
						window.location = aData[0];
					}
				}
			});
			//querySelectPost("tCatalogoUnidadEjecutoraRead", "cIdUnidadEjecutora", {async: false});
			roles="<%=roles%>";
			if(roles.indexOf("ADMIN_RECMAT") >=0){
				$("#isAdmin").val(0);
				
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			querySelectPost("mCatalogoTipoSolicitudRD", "cIdTipoSolicitud", {async: false});
			querySelectPost("mCatalogoEstadoSolicitudRead", "nIdEstado", {async: false});
			querySelectPost("mCatalogoPeriodoRead", "nIdPeriodo", {async: false});
			querySelectPost("mCatalogoCapituloConsultaRead", "cIdCapitulo", {async: false});
			querySelectPost("mCatalogoAlcanceCMBRead", "nIdAlcance", {async: false});
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			querySelectPost("mCatalogoSubPartidaReadSolicitud", "mCatalogoSubPartida", {async : false});
			
			if(roles.indexOf("ADMIN_RECMAT") >=0){
				$("#cIdUnidadEjecutora").prepend("<option value='%25'>*</option>");
			}
			
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
			//$("#cIdUnidadEjecutora").val(getUrlParameter("cIdUnidadEjecutora"));
			//$("#cIdTipoSolicitud").val(getUrlParameter("cIdTipoSolicitud"));
			agregaDatePickerFechas();
			if (consulta==0){
				mostrar();
			}
				
// 			$(this).ajaxForm({
// 				dataType:  "json",
// 				success: formSubmited
// 			});
		});
		
		function getUrlParameter(param) {
			param = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
			var r1 = "[\\?&]"+param+"=([^&#]*)";
			var r2 = new RegExp(r1);
			var r3 = r2.exec(window.location.href);
			if (r3 == null)
				return "";
			else
				return r3[1];
		}
		var where_="";
		function mostrar() {
			var rangoFechas=" and fCreacion BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
			var qw = " cEjercicio = '" + $("#cEjercicio").val() + 
				"' AND cIdTipoSolicitud LIKE '" + $("#cIdTipoSolicitud").val() + 
				"' AND cIdUnidadEjecutora LIKE '" + $("#cIdUnidadEjecutora").val()+"' " ;
				//"  AND cIdUnidadEjecutora NOT IN (SELECT cIdUnidadEjecutora from tCatUnidadEjecutora WHERE cIdUnidadEjecutora = 'B01')";
				//" AND cIdCapitulo = '" + $("#cIdCapitulo").val()+"'";
			if ($("#nIdEstado").val() != "0")
				qw += " AND nIdEstado = " + $("#nIdEstado").val();
			if ($("#cIdCapitulo").val() != "0")
				qw += " AND cIdCapitulo = " + $("#cIdCapitulo").val(); 	
			if ($("#cIdSolicitud").val().replace(/^\s+|\s+$/g,"") != "")
				qw += " AND cIdSolicitud LIKE '" + $("#cIdSolicitud").val() + "'";
			if ($("#nIdConsecutivo").val().replace(/^\s+|\s+$/g,"") != "")
				qw += " AND nIdConsecutivo = " + $("#nIdConsecutivo").val();
			qw=qw +"  "+rangoFechas+" ";
			where_=qw +" ";
			
			
			oTable = $("#tblSolicitudesRequisicion").dataTable(
			{
				sScrollX: 1400,
				"bPaginate": true,
				"bDestroy": true,
				bAutoWidth : false,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRecepcionDirecta&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aoColumns: [
					{ sName: "cVinculo", bVisible: false },
					{ sName: "cIdTipoSolicitud" },
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdConsecutivo"},
					{ sName: "cTipoSolicitud" },
					{ sName: "cEstado"},
					{ sName: "mBruto" },
					{ sName: "mNeto" },
					{ sName: "cIdSolicitud"}
				]
        	});
		}
		
		/* Get the rows which are currently selected */
		function fnGetSelected( oTableLocal )
		{
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
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function filtraPartida(){
		querySelectPost("mCatalogoSubPartidaByCapitulo", "mCatalogoSubPartida", {async : false});
		
		}
		function openARCH(ext){
			var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
			        + "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=ReporteRequisiciones.jasper"
					+"&formato="+ext
					+ "&where_=" + where_, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function openCSV(){
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn=ReporteRequisiciones"
				+ "&where_=" + where_,
				 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function cambiaCentrocontableUsuario(){
			if($("#cIdUnidadEjecutora").val()!='0'){
				$.ajax({
					url: '../../servlet/CambiaPropiedadesUsuario',
					dataType: 'json',
					data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
					async : false,
					success : function(j) {
						if(j[0].error){
							alert("No se hizo el cambio de centro contable y unidad ejecutora");
						}else{
							$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
						}
						
					}
				});
			}
		}
		function cambiaCentrocontableUsuario2(ue){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : ue},
				async : false,
				success : function(j) {
					if(j[0].error){
						alert("No se hizo el cambio de centro contable y unidad ejecutora");
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
// 			$('#fFin').datepicker('setDate', 'today');
			//$('#fInicio').datepicker('setDate', 'today-30');
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form> 
  		<fieldset >
  			<legend>Consulta Recepcion</legend>
		  		<div id="container" class="container" style="width: 90%;">	
				    <table align="left" width="90%">
				    	<tr>
				    		<td>
				    			<table align="left" cellpadding="2" width="90%">
							    	<tr>
							    		<td align="left">Unidad Ejecutora</td>
							    		<td align="left"><select name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"  style="width: 550px" onchange="cambiaCentrocontableUsuario();"></select></td>
							    	</tr>
							    	<tr>
							    		<td align="left">Tipo de Recepcion</td>
							    		<td align="left"><select name="cIdTipoSolicitud" id="cIdTipoSolicitud" style="width: 550px"></select></td>
							    	</tr>
							    	<tr>
							    		<td align="left">Estado</td>
							    		<td align="left"><select name="nIdEstado" id="nIdEstado" style="width: 550px"></select></td>
							    	</tr>
							    	<tr>
							    		<td align="left">Cap&iacute;tulo</td>
							    		<td align="left"><select name="cIdCapitulo" id="cIdCapitulo" style="width: 550px" onchange="filtraPartida();"></select></td>
							    	</tr>
							    	
							    	<tr> 
								<td align="left"> 
								</td> 
							</tr> 
							    	
							    	
							    	<tr>
							    		<td align="left">N&uacute;mero</td>
							    		<td align="left"><input type="text" name="nIdConsecutivo" id="nIdConsecutivo" style="width: 550px" onkeypress="return onlyIntegers(event);"></input></td>
							    	</tr>
							    	<tr>
							    		<td align="left">Recepcion</td>
							    		<td align="left"><input type="text" name="cIdSolicitud" id="cIdSolicitud" style="width: 550px" onblur="textCounter(this,10);" onkeypress="textCounter(this,10);"></input></td>
							    	</tr>
							    	<tr>
							    		<td colspan="2" align="left">
				    						Fecha Inicio:<input type="text" id="fInicio" name="fInicio" value="<%=todayAnt %>" readonly="readonly" class="desahabilitado"/>
				    					
				    						Fecha Fin:<input type="text" id="fFin" name="fFin" value="<%=today %>" readonly="readonly" class="desahabilitado"/>
				    					</td>
				    				</tr>
							    	<tr>
							    		<td colspan="2" align="center"><input type="button" onClick="mostrar();" name="btnBuscarConsultaReq" id="btnBuscarConsultaReq" value="Buscar" /></td>
							    	</tr>
							    	
							    </table>
							    
				    		</td>
				    		
				    	</tr>
				    	
				    	<tr> <td align="left">
								<img  id="cmdPdfReporteProg" name="cmdPdfReporteProg" style="cursor: pointer" src="../../imagenes/icono_PDF.jpg" onclick="javascript:openARCH('pdf');" />PDF&nbsp;
								<img  id="cmdxlsReporteProg" name="cmdxlsReporteProg" style="cursor: pointer" src="../../imagenes/icono_excel.jpg" onclick="javascript:openARCH('xls');" />xls&nbsp;
<!-- 							<img  id="cmdcsvReporteProg" name="cmdcsvReporteProg" style="cursor: pointer" src="../../imagenes/icono_excel.jpg" onclick="javascript:openCSV();" />csv&nbsp; -->
<!-- 							<img  id="cmdwordReporteProg" name="cmdwordReporteProg" style="cursor: pointer" src="../../imagenes/icono_word.png" onclick="javascript:openARCH('doc');" />word -->
							</td>
						</tr> 
						
<!-- 				    	<tr > -->
<!-- 				    		<td style="width: 740px; height: 300px;"> -->
<!-- 							    <table align="left" id="tblSolicitudesRequisicion" width="740px" class="display"> -->
<!-- 						        	<thead> -->
<!-- 						        		<tr> -->
<!-- 						        			<th></th> -->
<!-- 						        			<th>&nbsp;&nbsp;C&oacute;digo Requisición</th> -->
<!-- 						        			<th>&nbsp;&nbsp;Unidad Ejecutora</th> -->
<!-- 						        			<th>&nbsp;&nbsp;Consecutivo</th> -->
<!-- 						        			<th>Tipo de<br />Requisici&oacute;n</th> -->
<!-- 						        			<th>Alcance</th> -->
<!-- 						        			<th>Partida</th> -->
<!-- 						        			<th>Descripci&oacute;n</th> -->
<!-- 						        			<th>Estado<br />Requisici&oacute;n</th> -->
<!-- 						        			<th>Estado<br />Apartado</th> -->
<!-- 						        			<th>Periodo</th> -->
<!-- 						        			<th>Monto Bruto</th> -->
<!-- 						        			<th>Monto Neto</th> -->
<!-- 						        			<th>Requisi&oacute;n</th> -->
<!-- 						        		</tr> -->
<!-- 						        	</thead> -->
<!-- 						        </table> -->
<!-- 				    		</td> -->
<!-- 				    	</tr> -->
				    	<tr>
				    		<td>
							    <table align="left" width="80%">
						        	<tr>
						        		<td><input type="hidden" name="cEjercicio" id="cEjercicio" /></td>
						        	</tr>
						        	<tr>
						        		<td><input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>"/></td>
						        	</tr>
						        </table>
				    		</td>
				    	</tr>
				    </table>
				    
				    <table  id="tblSolicitudesRequisicion"  class="display">
			        	<thead>
			        		<tr>
			        			<th>&nbsp;</th>
			        			<th>&nbsp;</th>
			        			<th>&nbsp;</th>
			        			<th>&nbsp;</th>
			        			<th>Tipo </th>
			        			<th>Estado</th>
			        			<th>Monto Bruto</th>
			        			<th>Monto Neto</th>
			        			<th>Recepci&oacute;n</th>
			        		</tr>
			        	</thead>
			        </table>
					
			    </div>
  		</fieldset>
  		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab1.getLogin()%>"/>
	</form>
  </body>
</html>
