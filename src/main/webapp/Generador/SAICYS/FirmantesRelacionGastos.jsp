﻿<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	 String user_login=usuario.getLogin();
    String roles="";
    Map rol =usuario.getRoles();
    String cEjercicio = "";
	String cIdDocumento = "";
	String nIdEstado = "";
	String cCentroContable="";
		
	if (session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio) != null) {
	  	cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		}
			
	
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	/*
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	*/
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Firmantes de la Relacion de Gastos</title>
    
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
		 var oTableReq;
		 var importeTotalDocumento;
		
		$(document).ready(function() {
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"RelacionGastos","FirmantesRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					
				}

				%>			
			init();
			activaDesactivaPestanas();
						
			$('#tblCatalogoFirmantes tr').live('dblclick', function() {
				if ($("#nIdEstado").val() != "3" && $("#nIdEstado").val() != "4" && 
						($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
						//se checa numero de firmantes
					queryFormPost("mRelacionGastosFirmantesCuentaFirmantesRead", {async: false});
					if($("#nFirmantes").val() < 2) {        
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTable );
						var aData = oTable.fnGetData(anSelected[0]);
						$(oTable.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$("#nIdFirmante").val(aData[1]);
						queryFormPost("mRelacionGastosFirmantes_siguienteConsecutivoRead", {async: false});
						queryFormPost("mRelacionGastosFirmantesCreate", {async: false});
						oTableReq.fnClearTable(oTableReq);
						mostrarFirmantes();
					} 
				}
			});
			
			$('#tblFirmantesRelacionGastos tr').live('dblclick', function() {         
				if ($("#nIdEstado").val() != "3" && $("#nIdEstado").val() != "4" &&  
						($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableReq );
					var aData = oTableReq.fnGetData(anSelected[0]);
					$("#nNumeroFirmante").val(aData[5]);
					//alert(aData[5]);
					$(oTableReq.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					//queryFormPost("mSolicitudFirmanteDelete", { async:false });
					queryFormPost("pa_mRelacionGastosFirmantesBorrarYRenumerar", {async: false});
					//oTable.fnClearTable(oTable);
					oTableReq.fnClearTable(oTableReq);
					//mostrarCatalogo();
					mostrarFirmantes();
				}
			});
			
						
			mostrarCatalogo();
			mostrarFirmantes();
		
			
		});
		
		
		function init(){
		    queryFormPost("cg_roleRead", { async:false });
			/* Add a click handler to the rows - this could be used as a callback */
			queryFormPost("llenaCaratulaRelacionGastos", {async:false});
			queryFormPost("checaMontoTotalLineas",{async:false});
			queryFormPost("mRelacionGastosFirmantesCuentaFirmantesRead", {async: false});
			importeTotalDocumento=parseFloat(quitaFmt($("#mImporteNeto").val()));
		   		
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
		
		function mostrarCatalogo () {
			var qw = " cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'"; // = "(nIdFirmante NOT IN (SELECT nIdFirmante FROM dbo.mSolicitudFirmantes WHERE (cEjercicio = '" + $("#cEjercicio").val() + "') AND (cIdTipoSolicitud = '" + $("#cIdTipoSolicitud").val() +  "') AND (cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (nIdConsecutivo = " + $("#nIdConsecutivo").val() + "))) AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'";
			oTable = $("#tblCatalogoFirmantes").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCatalogoFirmantes&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" }
				]
        	});
		}
		
		function mostrarFirmantes () {
			var qw = " cEjercicio = '" + $("#cEjercicio").val() +  "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "' AND cIdDocumento= '" + $("#cIdFolio").val()+"'";
			oTableReq = $("#tblFirmantesRelacionGastos").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mSolicitudFirmantesRelacionGastos&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 5, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" },
					{ sName: "cTipoFirmante" },
					{ sName: "nNumeroFirmante", bVisible:false }
				]
        	});
		}
		
	
	   function quitaFmt( val ) {
			if ( val.indexOf( "$" ) >= 0 )
		   		val = val.replace("$", "");
		   	while(val.indexOf( "," ) > 0)
		   		val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		function eliminarFirmantes() {
			queryFormPost("mRelacionGastosFirmantesDelete", { async:false });
			oTableReq.fnClearTable(oTableReq);
			mostrarFirmantes();
		}
		
		
		function activaDesactivaPestanas(){
		var umbral;
	     //verificar que el monto ingresado por linea no sobrepase el total que se ingreso en la caratula
		 queryFormPost("verificaTotalFacturasRelacionGastosInicio", {async:false});
		 queryFormPost("checaMontoCapturadoFacturaDetalleTotal", {async:false});
		 queryFormPost("verificaMontosTotalesPartidasRELG", {async:false});
		  deshabilitaTabs();
		  if(parseFloat(quitaFmt($("#totalFactCapturadoInicio").val())) == parseFloat(importeTotalDocumento)){
		 //se deshabilita el boton de agregar facturas encabezado
		 // $("#pbAgregar").attr("disabled",true);
		  if(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) == parseFloat(importeTotalDocumento)){
		   $("#partidasRelacionGastos").attr("disabled",false);
		   umbral = Math.abs(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) - parseFloat($("#totalSumaPartidasRELG").val()));
		    if(umbral > .03){
				$("#apartadoRelacionGastos").attr("disabled",true);
	            $("#pagosRelacionGastos").attr("disabled",true);
				}else
			    habilitaTabs();
		   
	     }  
		 }
	    
	}
		
	function deshabilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",true);
	$("#partidasRelacionGastos").attr("disabled",true);
	$("#pagosRelacionGastos")	.attr("disabled", true);
	}
		
	function habilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",false);
	$("#partidasRelacionGastos").attr("disabled",false);
	$("#pagosRelacionGastos")	.attr("disabled",false);
	}	
		
		
		
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container">
			<table width="94%" align="left">
				<tr>
					<td>
						<fieldset>
				<legend>Informaci&oacute;n de la Relaci&oacute;n de Gastos</legend>
				<table border="0" align="center" width="100%">
					
					<tr align="left">
						<td colspan="2">
							<input name="lblcIdDocumento" id="lblcIdDocumento" type="text" style="width: 600px"  style="border: 0px solid black;" readonly="readonly"/>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" style="width: 600px" readonly="readonly" style="border: 0px solid black;"></input>  
						</td>								
					</tr>
					
					<tr align="left">
						<td colspan="2">
							<input name="lblnIdEstado" id="lblnIdEstado" type="text" readonly="readonly" style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					<tr>
							<td align="left">
								Importe Neto Relacion Gastos:
								<input type="text" style="width: 500px" name="lblImporteNeto"
									id="lblImporteNeto" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					<tr>
							<td align="left">
								<!--  Importe Neto Partidas:-->
								<input type="hidden" style="width: 500px" name="montoNetoRELG"
									id="montoNetoRELG" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					</table>
				</fieldset>
					<br/>
						<fieldset>
							<legend>Firmantes de la Relaci&oacute;n de Gastos</legend>
								<table width="97%" align="left">
									<tr>
										<td>
											<table align="center" id="tblFirmantesRelacionGastos" width="100%" class="display">
									        	<thead>
									        		<tr>
									        			<th>Unidad Ejecutora</th>
									        			<th>#</th>
									        			<th>Nombre</th>
									        			<th>Responsabilidades</th>
									        			<th>Tipo</th>
									        			<th></th>
									        		</tr>
									        	</thead>
									        </table>
										</td>
									</tr>
								</table>
						</fieldset>
						<fieldset>
							<legend>Cat&aacute;logo de Firmantes</legend>
								<table width="97%" align="left">
									<tr>
										<td>
											<table align="center" id="tblCatalogoFirmantes" width="100%" class="display">
									        	<thead>
									        		<tr>
									        			<th>Unidad Ejecutora</th>
									        			<th>#</th>
									        			<th>Nombre</th>
									        			<th>Responsabilidades</th>
									        		</tr>
									        	</thead>
									        </table>
										</td>
									</tr>
								</table>
						</fieldset>
					</td>
				</tr>
			</table>
		
			
			
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
		<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
		<input id="nIdEstado" name="nIdEstado" type="hidden"  />
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" />
		<input type="hidden" name="nFirmantes" id="nFirmantes" />
		<input id="cIdFolio" name="cIdFolio" type="hidden"  value="<%=cIdDocumento%>" />
		<input type="hidden" name="nNumeroFirmante" id="nNumeroFirmante" />
		<input type="hidden" name="nIdFirmante" id="nIdFirmante" />
		<input id="cEjercicio" name="cEjercicio" type="hidden"  value="<%=cEjercicio%>" />
		<input id="cIdDocumentoCaratula" name="cIdDocumentoCaratula" type="hidden"  value="<%=cIdDocumento%>" />
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
		<input type="hidden" id="montoNetoRELG" name="montoNetoRELG" />
		<input id="mImporteNeto" name="mImporteNeto" type="hidden"/>
		<input id="nFolioConsecutivoRelacionGastosCaratula" name="nFolioConsecutivoRelacionGastosCaratula" type="hidden"/>
		<input id="totalFactCapturadoInicio" name="totalFactCapturadoInicio" type="hidden"/>
		<input id="mTotalFacturaDetalleTotal" name="mTotalFacturaDetalleTotal" type="hidden"/>
		<input id="totalSumaPartidasRELG" name="totalSumaPartidasRELG" type="hidden"/>
		
		
		
		
		
		
		
		
		
			
	    </div>
	</form>
  </body>
</html>
