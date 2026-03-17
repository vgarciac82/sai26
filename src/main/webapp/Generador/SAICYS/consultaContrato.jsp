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
	String roles="";
	session.setAttribute(GestionInterface.ATT_ContratoAbierto,null);
	session.setAttribute(GestionInterface.ATT_EstadoContrato,null);
	Map <String, Role>rol  =usuarioTab1.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
	session.setAttribute(GestionInterface.ATT_PedidoEjercicio,null);
	session.setAttribute(GestionInterface.ATT_PedidoTipoPedido, null);
	session.setAttribute(GestionInterface.ATT_PedidoUnidadEjec, null);
	session.setAttribute(GestionInterface.ATT_PedidoConsecutivo, null);
	
	session.setAttribute(GestionInterface.ATT_ContratoEjercicio,null);
	session.setAttribute(GestionInterface.ATT_ContratoTipoContrato, null);
	session.setAttribute(GestionInterface.ATT_ContratoUnidadEjec, null);
	session.setAttribute(GestionInterface.ATT_ContratoConsecutivo, null);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Consulta Contrato</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var oTable;
		var roles='';
		$(document).ready(function() {
			$("#tbs").val(0);
			showAndHideTabs();
			<%
				int tabla=0;
				int consulta=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map pestanas=ebl.getPestana(roles,"Contratos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana=(String)e.getValue();
					if ("caratulaContrato".equals(pestana)){
						 tabla=1;
					}
					if ("consultaContrato".equals(pestana)){
						 consulta=1;
					}
				}
				Map botones=nb.getBotones(roles,"Contratos","consultaContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
				%> 
			roles="<%=roles%>";
			var consulta='<%=consulta%>';
			var tabla='<%=tabla%>';
			agregaDatePickerFechas();
			$('#tblConsultaPedidos tr').live('dblclick', function() { 
				if (tabla==0){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );
					var aData = oTable.fnGetData(anSelected[0]);
					
					if(aData!= "")
						window.location = aData[0];
				}
			});
			
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			
			querySelectPost("estadoContratoRead", "nIdEstado", {async: false });
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			if (consulta==0){
				mostrar();
			}
		});
		var where_='';
		function mostrar() {
			var qw = " cEjercicio = '" + $("#cEjercicio").val() + 				
				//"' AND cIdUnidadEjecutora LIKE '" + $("#cIdUnidadEjecutora").val() +
				"' AND cIdContrato LIKE'%25" +$("#cIdContrato").val()+
				"%25' AND cIdContratoDefinitivo LIKE'%25" +$("#cIdDefinitivo").val()+
				"%25' AND cIdProcedimiento LIKE'%25" +$("#cIdProcedimiento").val()+
				"%25' AND cIdRFC LIKE'%25" +$("#cIdRFC").val()+
				"%25' AND cConceptoContrato LIKE'%25" +$("#cDescripcion").val()+"%25'";
			if ($("#nIdEstado").val() != "0")
				qw += " AND nIdEstado = " + $("#nIdEstado").val();
				
			var rangoFechas=" and fCreacion BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";	
			qw=qw+rangoFechas;
			where_=qw+' ';
			if($("#cIdUnidadEjecutora").val() == "0"){
					oTable = $("#tblConsultaPedidos").dataTable({
					sScrollX: "100%",
					bScrollCollapse: true,
					bDestroy: true,
					bAutoWidth: true,
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Pedidos",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_consultaContratos&qw=" + qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 3, "asc" ]] ,
					aoColumns: [
						{ sName: "cVinculo", bVisible: false },
						{ sName: "cIdtipoContrato" },
						{ sName: "cIdUnidadEjecutora" },
						{ sName: "nIdConsecutivo" },
						{ sName: "cIdContratoDefinitivo" },
						{ sName: "cConceptoContrato" },
						{ sName: "cIdRFC" },
						{ sName: "cRazonSocial" },
						{ sName: "cEstado" },
						{ sName: "cIdProcedimiento" },
						{ sName: "cIdContrato" },
						{ sName: "mContratoMontoBruto" },
						{ sName: "mContratoMontoNeto" },
						{ sName: "lblfCarga" },
						{ sName: "lblfVigencia" }
					]
	        	});
			
			}else{
				oTable = $("#tblConsultaPedidos").dataTable({
					sScrollX: "100%",
					bScrollCollapse: true,
					bDestroy: true,
					bAutoWidth: true,
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Pedidos",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mConsultaContratos('"+$("#cIdUnidadEjecutora").val()+"')&qw=" + qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 3, "asc" ]] ,
					aoColumns: [
						{ sName: "cVinculo", bVisible: false },
						{ sName: "cIdtipoContrato" },
						{ sName: "cIdUnidadEjecutora" },
						{ sName: "nIdConsecutivo" },
						{ sName: "cIdContratoDefinitivo" },
						{ sName: "cConceptoContrato" },
						{ sName: "cIdRFC" },
						{ sName: "cRazonSocial" },
						{ sName: "cEstado" },
						{ sName: "cIdProcedimiento" },
						{ sName: "cIdContrato" },
						{ sName: "mContratoMontoBruto" },
						{ sName: "mContratoMontoNeto" },
						{ sName: "lblfCarga" },
						{ sName: "lblfVigencia" }
					]
	        	});
			}	
			
		}
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
		function openARCH(ext){
			if ($("#cIdUnidadEjecutora").val() != "0")
				where_ += " AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val()+"'";
			var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
			        + "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=ReporteConsultaContratos.jasper"
					+"&formato="+ext
					+ "&where_=" + where_, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function openCSV(){
			if ($("#cIdUnidadEjecutora").val() != "0")
				where_ += " AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val()+"'";
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn=ReporteConsultaContratos"
				+ "&where_=" + where_,
				 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function cambiaCentrocontableUsuario(){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						 swal({
					        	title: "",
					        	text: "No se hizo el cambio de centro contable y unidad ejecutora",
					        	icon: "info",
					        	buttons: {
					        		confirm : "Cerrar"
					        		},
					        	}).then((continuar) => {
					        });
					}else{
						$("#cIdUnidadResponsableUsuario").val(j[0].unidadEjecutora);
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
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form>
  	<div id="container" class="container" style="width: 95%">
  		<fieldset >
  			<legend>Consulta Contratos</legend>
		  		<table align="left" cellpadding="2" width="100%">
			   		<tr>
						<td>Unidad Ejecutora</td>
						<td>
							<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"style="width: 50em;" onchange="cambiaCentrocontableUsuario();">
							<option value="<%=usuarioTab1.getU_UR()%>" selected="selected">
							</select>	
						</td>
					</tr>
					<tr >
						<td>Estado</td>
						<td>
							<select id="nIdEstado" name="nIdEstado"style="width: 50em;"></select>	
						</td>
					</tr>
					<tr >
						<td>Contrato</td>
						<td>
						   <input type="text" name="cIdContrato" id="cIdContrato" style="width: 50em;" />	
						</td>
					</tr>
					<tr >
						<td>Definitivo</td>
						<td>
						   <input type="text" name="cIdDefinitivo" id="cIdDefinitivo" style="width: 50em;" />	
						</td>
					</tr>
					<tr >
						<td>R.F.C.</td>
						<td>
						   <input type="text" name="cIdRFC" id="cIdRFC" style="width: 50em;" />	
						</td>
					</tr>	
					<tr >
						<td>Descripci&oacute;n</td>
						<td>
						   <input type="text" name="cDescripcion" id="cDescripcion" style="width: 50em;" />	
						</td>
					</tr>
					<tr >
						<td>Procedimiento</td>
						<td>
						   <input type="text" name="cIdProcedimiento" id="cIdProcedimiento" style="width: 50em;" />	
						</td>
					</tr>
					<tr>
			    		<td colspan="2" align="left">
    						Fecha Inicio:<input type="text" id="fInicio" name="fInicio" value="<%=todayAnt %>" readonly="readonly" class="desahabilitado"/>
    					
    						Fecha Fin:<input type="text" id="fFin" name="fFin" value="<%=today %>" readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
			    	<tr>
			    		<td colspan="2" align="center"><input type="button"  name="btnBuscarConsultaContrato" id="btnBuscarConsultaContrato" value="Buscar"  onclick="mostrar();" class="btnInterfaceBG ui-button ui-corner-all"/></td>
			    	</tr>
			    </table>
				<table align="left" style="width: 100%">
					<tr> <td align="left">
						<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"		onclick="openARCH('pdf');" />&nbsp;&nbsp;
	    				<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdxlsReporteProg" 		name="cmdxlsReporteProg" 	value="Excel"	onclick="openARCH('xls');" />&nbsp;&nbsp;
						</td>
					</tr> 
				</table>
				<table align="left" id="tblConsultaPedidos"  class="display" style="width: 100%">
		        	<thead>
		        		<tr>
		        			<th></th>
		        			<th>&nbsp;&nbsp;<!-- C&oacute;digo Contrato --></th>
		        			<th>&nbsp;&nbsp;<!-- Unidad Ejecutora --></th>
		        			<th>&nbsp;&nbsp;<!-- Consecutivo --></th>
		        			<th>CONTRATO DEFINITIVO</th>
		        			<th>CONCEPTO DEL CONTRATO</th>
		        			<th>RFC DEL PROVEEDOR</th>
		        			<th>RAZON SOCIAL</th> 
		        			<th>ESTADO</th>
		        			<th>PROCEDIMIENTO</th>
		        			<th>CONTRATO</th>
		        			<th>MONTO BRUTO</th>
		        			<th>MONTO NETO</th>	
		        			<th>Fecha Carga</th>	
		        			<th>Fecha Vigencia</th>						        								        			
		        		</tr>
		        	</thead> 
		        </table>
  		</fieldset>
  		</div>
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="cEjercicio" id="cEjercicio"/>
  		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
	    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab1.getLogin()%>"/>
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
	</form>
  </body>
</html>
