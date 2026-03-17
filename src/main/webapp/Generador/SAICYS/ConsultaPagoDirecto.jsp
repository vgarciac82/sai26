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
    Map<String, Role> rol =usuario.getRoles();
    String cCentroContable = "";
    session.setAttribute(GestionInterface.ATT_PagoDirectoEjercicio, null);
	session.setAttribute(GestionInterface.ATT_PagoDirectoFolio, null);
	session.setAttribute(GestionInterface.ATT_PagoDirectoEstado, null);
	session.setAttribute(GestionInterface.ATT_PagoDirectoUE, null);
    
    if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 <head>
 <title></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
	</style>
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker-es.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/validaciones.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		var oTable;
		
		$(document).ready(function() {
			<%
				int tabla=0;
				int consulta=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map pestanas=ebl.getPestana(roles,"PagosDirectos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana=(String)e.getValue();
					if ("caratulaPagoDirecto".equals(pestana)){
						 tabla=1;
					}
					if ("consultaPagoDirecto".equals(pestana)){
						 consulta=1;
					}
				}
				Map botones=nb.getBotones(roles,"PagosDirectos","consultaPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
			%>
			
			init();
						
			$("#btnBuscar").button().click(function(){
				cargaTable();
			});
			
			$("#tblPagosDirectos tr").live("dblclick", function() { 
				var tabla='<%=tabla%>';
				if (tabla==0){
					if ($(this).hasClass("row_selected"))             
						$(this).removeClass("row_selected");         
					else            
						$(this).addClass("row_selected");
					var anSelected = fnGetSelected( oTable );				
					var aData = oTable.fnGetData(anSelected[0]);
					
					if(aData!= "")
						window.location = aData[0];
				}
			});
			
		});
		
		function init(){
			var roles="<%=roles%>";
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			$("#cCentroContable").val( "<%=cCentroContable%>" );
			querySelectPost("UnidadPedidosRead", "cIdUnidadEjecutora", {async: false });
			querySelectPost("catalogoEstadoPagoDirectoRead","nIdEstado",{async:false});
			querySelectPost("catalogoTipoPagoDirectoReadALL", "cIdTipoOperacion", {async: false });
			querySelectPost("CatalogoObraDGastoReadALL", "DESTINO_GASTO", {async: true });
			cargaTable();
			
			if(roles.indexOf("ADMIN_RECMAT") < 0)
				eliminaOpcionesComboUE();
		}
		
		function cargaTable(){
			oTable = $("#tblPagosDirectos").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "200%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_consultaPagosDirectos&qw=" + getFiltros(),
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "page", bVisible: false },
					{ sName: "cIdDocumento" },
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "cConcepto" },
					{ sName: "cEstado" },
					{ sName: "TIPO_OPERACION" },
					{ sName: "cIdRFC" },
					{ sName: "fRecepcion" },
					{ sName: "DESTINO_GASTO" },
					{ sName: "mImporteBruto" },
					{ sName: "nPorcentajeIVA", bVisible: false },
					{ sName: "mImporteNeto" }
				]
        	});
		}
		
		function getFiltros(){
			var filtros = " cEjercicio = "+$("#cEjercicio").val();
			
			if($("#cIdUnidadEjecutora").val() != 0)
				filtros += " and cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"'";
			if($("#cIdTipoOperacion").val() != 0)
				filtros += " and ID_TIPO_OPER = "+$("#cIdTipoOperacion").val()+" and TO_TIPO_DOCTO = '"+$("#TO_TIPO_DOCTO").val()+"'";
			if($("#DESTINO_GASTO").val() != 0)
				filtros += " and ID_DESTINO_GASTO = '"+$("#DESTINO_GASTO").val()+"'";
			if($("#nIdEstado").val() != 0)
				filtros += " and nIdEstado = "+$("#nIdEstado").val();
			if($("#cIDRFC").val() != "")
				filtros += " and cIdRFC LIKE '%25"+$("#cIDRFC").val()+"%25'";
			if($("#cIdFolio").val() != "")
				filtros += " and cIdDocumento LIKE '%25"+$("#cIdFolio").val()+"%25'";
				
			return filtros;
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
		
		function eliminaOpcionesComboUE(){
			$("#cIdUnidadEjecutora option").each(function(){
				if($(this).attr('value') != $("#cIdUnidadEjecutoraUsuario").val())
					$("#cIdUnidadEjecutora").find("option[value='"+$(this).attr('value')+"']").remove();
			});
		}
	</script>
</head>  
<body>
	<form>
    	<fieldset >
  			<legend>Consulta Pago Directo</legend>
		  		<div id="container" class="container" >	
				    <table align="left" width="100%">
				    	<tr>
				    		<td>
				    			<table align="left" cellpadding="2" width="750px">
							   		<tr>
										<td>
											Unidad Ejecutora:
										</td>
										<td>
											<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" style="width: 580px;">
												<option value="<%=usuario.getU_UR()%>" selected="selected">
											</select>	
										</td>
									</tr>
									<tr>
										<td>
											Tipo de Pago Directo:
										</td>
										<td>
											<select id="cIdTipoOperacion" name="cIdTipoOperacion" style="width: 580px;">
											</select>
										</td>
									</tr>
									<tr>
										<td>
											Tipo Destino:
										</td>
										<td>
											<select id="DESTINO_GASTO" name="DESTINO_GASTO" style="width: 580px;">
											</select>
										</td>
									</tr>
									<tr>
										<td>
											Estado:
										</td>
										<td>
											<select id="nIdEstado" name="nIdEstado" style="width: 580px;">
											</select>	
										</td>
									</tr>
									<tr>
										<td>
											Folio:
										</td>
										<td>
										   <input type="text" name="cIdFolio" id="cIdFolio" style="width: 580px;"/>	
										</td>
									</tr>
									<tr>
										<td>
											RFC:
										</td>
										<td>
										   <input type="text" name="cIDRFC" id="cIDRFC" style="width: 580px;"/>	
										</td>
									</tr>
									<tr>
										<td align="center" colspan="2">
											<input type="button"  name="btnBuscar" id="btnBuscar" value="Buscar"/>
										</td>
									</tr>		
							    </table>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td style="width: 740px;" >
				    			<br/>
								<table align="left" id="tblPagosDirectos" width="740px"
									class="display">
									<thead>
										<tr>
											<th>
												&nbsp;
											</th>
											<th>
												Folio
											</th>
											<th>
												Unidad<br/>Ejecutora
											</th>
											<th>
												Concepto
											</th>
											<th>
												Estado
											</th>
											<th>
												Tipo Pago Directo
											</th>
											<th>
												RFC
											</th>
											<th>
												Fecha Recepcion
											</th>
											<th>
												Tipo Destino
											</th>
											<th>
												Importe Bruto
											</th>
											<th>
												IVA(%)
											</th>
											<th>
												Monto Neto
											</th>
											
										</tr>
									</thead>
								</table>
							</td>
				    	</tr>
				    </table>
			    </div>
  		</fieldset>
  		<input type="hidden" id="cIdUnidadEjecutoraUsuario" name="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR() %>" />
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
		<input type="hidden" id="cCentroContable" name="cCentroContable" />
  		<input type="hidden" id="cEjercicio" name="cEjercicio" />
  		<input type="hidden" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" value="DIRECTO"/>
		<input type="hidden" id="cDocumento" name="cDocumento" value="PAGODIRECTO"/>
  	</form>
</body>
</html>
