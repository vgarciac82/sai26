<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map rol =usuario.getRoles();
     String cEjercicio = "";
	String cIdDocumento = "";
	String nIdEstado = "";
    
    if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	if (session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio) != null) {
	    cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		
		}
	
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Consulta Pedido</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
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
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" charset="utf-8">
		var oTable;
		$(document).ready(function() {
		
		
			<%
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
				Map botones=nb.getBotones(roles,"Relacion Gastos","ConsultaRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
		
		
			//Agrega el click handler para el doble click de cada reglon de la tabla
			$('#tblConsultaRelaciones tr').live('dblclick', function() { 
				
				//if (tabla==0){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );				
					var aData = oTable.fnGetData(anSelected[0]);
					//La vista tiene en su primer elemento el link del pedido
					if(aData!= "")
						window.location = aData[0];
				//}
			});
			//Carga los valores para los comboBox
			querySelectPost("UnidadPedidosRead", "cIdUnidadEjecutora", {async: false });
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			querySelectPost("estadoRelacionGastosReadM", "nIdEstado", {async: false });
				
			//if (consulta==0){
				mostrar();
			//}
		});
		function mostrar() {
			
			//if (consulta==0){
				var qw = " cEjercicio = '"+ $("#cEjercicio").val()+"'"+ 				
				" AND cIdDocumento LIKE'%25" +$("#cIdDocumento").val()+
				"%25' AND cIdRFC LIKE'%25" +$("#cIdRFC").val()+
				"%25' AND cConcepto LIKE'%25" +$("#cConcepto").val()+"%25'";
			if ($("#nIdEstado").val() != "0")
				qw += " AND nIdEstado = " + $("#nIdEstado").val();
			if ($("#cIdUnidadEjecutora").val() != "0")
				qw += " AND cIdUnidadEjecutora = '"+ $("#cIdUnidadEjecutora").val()+"'";
			
			oTable = $("#tblConsultaRelaciones").dataTable({
				sScrollY: "300px",
				sScrollX: "100%",
				sScrollXInner: "200%",
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Relacion de Gastos",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_consultaRelacionGastos&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 3, "asc" ]] ,
				aoColumns: [
					{ sName: "cVinculo", bVisible: false },
					{ sName: "cEjercicio", bVisible: true },
					{ sName: "cIdUnidadEjecutora", bVisible: true },
					{ sName: "cIdDocumento" },
					{ sName: "cConcepto" },
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial" },
					{ sName: "cEstado" },
					{ sName: "importeNeto" },
					{ sName: "fRecepcion" }
				]
				
				
        	});
        	
			//}
			
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
	</script>
</head>
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
  		<fieldset >
  			<legend>Consulta Relación de Gastos</legend>
		  		<div id="container" class="container" >	
				    <table align="left" width="90%">
				    	<tr>
				    		<td>
				    			<table align="left" cellpadding="2" width="750px">
							   		<tr>
										<td>Unidad Ejecutora</td>
										<td>
											<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"style="width: 50em;">
											<option value="<%=usuario.getU_UR()%>" selected="selected">
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
										<td>No.Folio Relación</td>
										<td>
										   <input type="text" name="cIdDocumento" id="cIdDocumento" style="width: 50em;" />	
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
										   <input type="text" name="cConcepto" id="cConcepto" style="width: 50em;" />	
										</td>
									</tr>
									
							    	<tr>
							    		<td colspan="2" align="center"><input type="button"  name="btnBuscarConsultaRelaciones" id="btnBuscarConsultaRelaciones" value="Buscar"  onclick="mostrar();"/></td>
							    	</tr>
							    </table>
				    		</td>
				    	</tr>
				    	<tr  >
				    		<td style="width: 740px; height: 300px" >
								<table align="left" id="tblConsultaRelaciones" width="740px"
									class="display">
									<thead>
										<tr>
										<th></th>
										<th>EJERCICIO</th>
										<th>UNIDAD EJECUTORA</th>
											<th>
												NO.FOLIO RELACION GASTOS
											</th>
											<th>
												CONCEPTO RELACION GASTOS
											</th>
											<th>
												RFC 
											</th>
											<th>
												RAZON SOCIAL
											</th>
											<th>
												ESTADO
											</th>
											<th>
												MONTO NETO
											</th>
											<th>
												FECHA REGISTRO
											</th>
											
										</tr>
									</thead>
								</table>
							</td>
				    	</tr>
				    	<tr>
				    		<td>
							    <table align="left" width="80%">
						        	<tr>
						        		<td><input type="hidden" name="cEjercicio" id="cEjercicio"/></td>
						        	</tr>
						        	<tr>
						        		<td><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuario.getU_UR() %>"/></td>
						        	</tr>
						        </table>
				    		</td>
				    	</tr>
				    </table>
			    </div>
  		</fieldset>
	</form>
  </body>
</html>
