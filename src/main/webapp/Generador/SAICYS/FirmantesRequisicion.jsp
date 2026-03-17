﻿﻿<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();
	Map rol =usuarioTab.getRoles();
	String cEjercicio = "";
	String cIdTipoSolicitud = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ReqEjercicio);
		cIdTipoSolicitud = (String)session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ReqConsecutivo);
	}
	
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuarioTab.getU_UR();
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Firmantes de las Requisiciones</title>
    
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
	<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var oTable;
		var oTableReq;
		var apartables;
		$(document).ready(function() {
			$("#tbs").val(4);
			showHideTabs();
			apartables = [ "RC", "RM", "RS","RT" ];
			if ($.inArray($("#cIdTipoSolicitud").val(), apartables) != -1){
				$("#imgDevolverFirmantesReq").hide();
				$("#imgAprobarFirmantesReq").hide();
			}
			<%
				String roles="";
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
				Map botones=nb.getBotones(roles,"Requisiciones","FirmantesRequisiciones");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAnularFirmantesReq".equals(img)){   
						imgAnular=1; 
					}
					if ("imgAprobarFirmantesReq".equals(img)){
						imgAprobar=1;
					}
					if ("imgDevolverFirmantesReq".equals(img)){
						imgDevolver=1;
					}
					if ("cmdPdfFirmantesReq".equals(img)){ 
						imgPdf=1;
					}
				}

				%>			
			
			queryFormPost("cg_roleRead", { async:false });
			/* Add a click handler to the rows - this could be used as a callback */
			
			$('#tblCatalogoFirmantes tr').live('dblclick', function() {
				var roles="<%=roles%>";
				queryFormPost("mUsuarioMismaUE", {async: false   });
				if ($("#nIdEstadoReq").val() != "2" && $("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() != "5" &&  $("#nIdEstadoReq").val() != "6" &&  $("#nIdEstadoReq").val() != "7" &&
						(roles.indexOf("ADMIN_RECMAT") >=0||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() 
						|| $("#cIdUsuario").val()==1 ||parseInt($("#usuariosMismaUE").val(),10)==1 )) {
						//se checa numero de firmantes
					queryFormPost("mSolicitudFirmantesCuentaFirmantesRead", {async: false});
					if($("#nFirmantes").val() < 1) {        
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTable );
						var aData = oTable.fnGetData(anSelected[0]);
						$(oTable.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$("#nIdFirmante").val(aData[1]);
						queryFormPost("mSolicitudFirmantes_siguienteConsecutivoRead", {async: false});
						queryFormPost("mSolicitudFirmantesCreate", {async: false});
						oTableReq.fnClearTable(oTableReq);
						mostrarFirmantes();
						//Bitácora
						$("#cAccion").val("AGREGA_FIRMANTE");
						//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
						$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					} 
				}else{
					swal("No tienes permisos o el estado de la requisición no permite hacer cambios.",{icon:"warning",button: "Cerrar"});
				}
			});
			
			$('#tblFirmantesRequisicion tr').live('dblclick', function() {
				var roles="<%=roles%>";
				queryFormPost("mUsuarioMismaUE", {async: false   });
				if ($("#nIdEstadoReq").val() != "2" && $("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() != "5" &&  $("#nIdEstadoReq").val() != "6"&&  $("#nIdEstadoReq").val() != "7" &&
						(roles.indexOf("ADMIN_RECMAT") >=0||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() ||parseInt($("#usuariosMismaUE").val(),10)==1 )) {
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableReq );
					var aData = oTableReq.fnGetData(anSelected[0]);
					$("#nNumeroFirmante").val(aData[5]);
					$(oTableReq.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					//queryFormPost("mSolicitudFirmanteDelete", { async:false });
					queryFormPost("pa_mSolicitudFirmantesBorrarYRenumerar", {async: false});
					//oTable.fnClearTable(oTable);
					oTableReq.fnClearTable(oTableReq);
					//mostrarCatalogo();
					mostrarFirmantes();
					//Bitácora
					$("#cAccion").val("BORRA_FIRMANTE");
					//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				}else{
					swal("No tienes permisos o el estado de la requisición no permite hacer cambios.",{icon:"info",button: "Cerrar"});
				}
				
			});
			
			document.getElementById("lblRequisicion").style.readonly=true;
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblDescripcion").style.readonly=true;
			document.getElementById("lblPartida").style.readonly=true;
			document.getElementById("lblMesRequisicion").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			
			
			//Agrega el firmante de gerente en automatico
			queryFormPost("mSolicitudFirmantesCuentaFirmantesRead", {async: false});
			if(parseInt($("#nFirmantes").val(),10)==0){
				//obtiene el nidfirmante de cada ue que sea gerente
				queryFormPost("mSolicitudFirmantes_siguienteConsecutivoRead", {async: false   });
				queryFormPost("obtieneNidFirmante", {async: false,callback : function() 
						{
							//Se agrega el firmante
							if($("#nIdFirmante").val()!=0)
								queryFormPost("mSolicitudFirmantesCreate", {async: false});
						}
				});
				
			}
			mostrarCatalogo();
			mostrarFirmantes();
			
			//título de la pantalla
			queryFormPost("mSolicitud_LabelRead", { async:false });
			queryFormPost("mSolicitudFirmantesCuentaFirmantesRead", { async:false });
			queryFormPost("mSolicitudDescripcionUnidad", {async: false});	
			queryFormPost("cuentaConsolidadosRead", { async:false});
			
			queryFormPost("mSolicitud_EstadoSolicitudRead", {async:false});
			if (!($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
				document.getElementById("imgAprobarFirmantesReq").disabled = true;
				document.getElementById("imgDevolverFirmantesReq").disabled = true;
				document.getElementById("imgAnularFirmantesReq").disabled = true;
			}
			else if ($("#nIdEstadoReq").val() == "1" && $.inArray($("#cIdTipoSolicitud").val(), apartables) == -1) { //capturada
				$("#imgAprobarFirmantesReq" ).show();
				$("#imgDevolverFirmantesReq" ).hide();
				document.getElementById("imgAnularFirmantesReq").disabled = false;
				
			}
			else if ($("#nIdEstadoReq").val() == "2" ) { //solicitada
				document.getElementById("imgAprobarFirmantesReq").disabled = true;
				document.getElementById("imgDevolverFirmantesReq").disabled = true;
				document.getElementById("imgAnularFirmantesReq").disabled = true;
			}
			else if ($("#nIdEstadoReq").val() == "3" && $.inArray($("#cIdTipoSolicitud").val(), apartables) == -1) { //Aprobada
				$("#imgDevolverFirmantesReq" ).show();
				$("#imgAprobarFirmantesReq" ).hide();
				$("#imgAnularFirmantesReq" ).hide();
			} 
			else if ($("#nIdEstadoReq").val() == "4" && $.inArray($("#cIdTipoSolicitud").val(), apartables) == -1) { //anulada
				$("#imgAprobarFirmantesReq" ).hide();
				$("#imgDevolverFirmantesReq" ).hide();
				$("#imgAnularFirmantesReq" ).hide();
			}
			
			else if ($("#nIdEstadoReq").val() == "5") { //habilitada por consolidado
				$("#imgDevolverFirmantesReq" ).hide();
				$("#imgAprobarFirmantesReq" ).hide();
				$("#imgAnularFirmantesReq" ).hide();
			}
			else if($("#nIdEstadoReq").val() == "6"  || $("#nIdEstadoReq").val() == "7"){
				$("#imgAnularFirmantesReq").hide();
				$("#imgDevolverFirmantesReq").hide();
				$("#imgAprobarFirmantesReq").hide();
			}
			
			//Para Fecha de vencimiento
			cambiaColorFecha();
			
// 			$(this).ajaxForm({
// 				dataType:  "json",
// 				success: formSubmited
// 			});
			
			
		});
		
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
			var roles="<%=roles%>";
			var qw="";
			if(roles.indexOf("ADMIN_RECMAT") >=0){
				$("#isAdmin").val(0);
				qw="1=1";
			}else{
				queryFormPost("tCatalogoUnidadEjecutoraReadVistasCadena", { async:false });
				qw = " cIdUnidadEjecutora in("+$("#UES_USUARIO").val()+")";
			}
			
			//var qw = " cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'"; // = "(nIdFirmante NOT IN (SELECT nIdFirmante FROM dbo.mSolicitudFirmantes WHERE (cEjercicio = '" + $("#cEjercicio").val() + "') AND (cIdTipoSolicitud = '" + $("#cIdTipoSolicitud").val() +  "') AND (cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (nIdConsecutivo = " + $("#nIdConsecutivo").val() + "))) AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'";
			
			
			oTable = $("#tblCatalogoFirmantes").dataTable({
				bAutoWidth : false,
				bDestroy: true,
				//sScrollX: "100%",
				"bPaginate": true,
				"bDestroy": true,
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
			var qw = " cEjercicio = '" + $("#cEjercicio").val() + "' AND cIdTipoSolicitud = '" + $("#cIdTipoSolicitud").val() + "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "' AND nIdConsecutivo = " + $("#nIdConsecutivo").val();
			oTableReq = $("#tblFirmantesRequisicion").dataTable({
				bAutoWidth : false,
				bDestroy: true,
				//sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mSolicitudFirmantes&qw=" + qw,
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
		
		function aprobarRequisicion() {
			
			var imgAprobar='<%=imgAprobar%>';
			if (imgAprobar==0){
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
				if ($("#nLineas").val() > 0) {
					//Regla de negocio
					var currentTime = new Date();
					$("#fAprobacion").val(currentTime.getFullYear() + '-' +currentTime.getDate() + '-' +  (currentTime.getMonth()+1)+ ' ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '.' + currentTime.getMilliseconds());
					$("#nIdEstado").val("3"); //aprobada
					$("#nIdEstadoPrecomprometido").val("3"); //para regularización
					queryFormPost("mSolicitudAprobarUpdate", { async:false });
					queryFormPost("sp_mSolicitudEstadoPrecomprometido", {async:false});
					//Bitácora
					$("#cAccion").val("APRUEBA_REQUISICION");
					//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					
					swal({
						title: "",
						text: "La requicisión ha sido aprobada.",
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							window.location = "Requisiciones.jsp?tab=4";
					});
							
				}else
					swal("Por lo menos debe haber una línea capturada para aprobar la requisición.",{icon:"info",button: "Cerrar"});
			}else{
				swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
			}	
		}
		
		function devolverRequisicion () {
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver==0){
				
				if ($("#cIdTipoSolicitud").val() == 'RM') {
				
					$("#cIdSolicitudMod").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());					
					$("#existeMod").val('');
					queryFormPost("mPedidoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						swal("No se puede devolver la requicisión porque está asociada a un pedido modificatorio.",{icon:"info",button: "Cerrar"});
						return;
					}
					
					$("#existeMod").val('');
					queryFormPost("mContratoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						swal("No se puede devolver la requicision porque esta asociada a un contrato modificatorio.",{icon:"info",button: "Cerrar"});
						return;
					}
				}
				
				if ($("#nConsolidados").val() == "0" || $("#nConsolidados").val() == "") {
				//Regla de negocio
				
					var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
					window.open("LineasSolicitudNotas.jsp?tipo=1&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
				
				
				
				}
			else
				swal("Esta requisición está asociada a un consolidado. Para devolverla, primero es necesario eliminarla del consolidado.",{icon:"info",button: "Cerrar"});
			}
			else{
				swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
			}
			
		}
		
		function anularRequisicion(){
			var imgAnular='<%=imgAnular%>';
			if (imgAnular==0){
				var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
				window.open("LineasSolicitudNotas.jsp?tipo=2&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
			}else{
				swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
			}
		}
		
		function eliminarFirmantes() {
			queryFormPost("mSolicitudFirmantesDelete", { async:false });
			oTableReq.fnClearTable(oTableReq);
			mostrarFirmantes();
		}
		
		function openPDF(ext){
			
			var imgPdf='<%=imgPdf%>';
			if (imgPdf==0){
			 if($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" && $("#nIdEstadoReq").val() != "5"){
			   //se revisa si ya existe una linea en la tabla de msolicitudlineasapartado
			    $("#cIdSolicitudRep").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
				queryFormPost("numeroLineasApartadoSolicitud", { async : false});
				if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)> 0){
				  //checamos si el numero de lineas de msolicitud es igual a msolicitudlineasapartado
				   		queryFormPost("numeroLineasSolicitud", { async : false});
				   		if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)== parseInt($("#numeroLineasSolicitud").val(),10)){
				   		    queryFormPost("sumaLineasSolicitud", { async : false}); 
					   		   queryFormPost("sumaLineasApartadoSolicitudLineas", { async : false});
					   		   var lineasSolicitud=parseFloat($("#sumaLineasSolicitud").val());
					   		   var lineasApartadoSolicitud=parseFloat($("#sumaLineasApartadoSolicitudLineas").val());
					   		    //var total=lineasSolicitud-lineasApartadoSolicitud;
					   			   		   
				   		  if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 1.0){
				   		    //nos puede ser mayor o menor a 1.0 
				   		    swal("La suma de las líneas presupuestadas no es igual a la suma de las líneas de solicitud, favor de revisar.",{icon:"info",button: "Cerrar"});
				   		    return;
				   		   }
				   		    else 				   		    
				   		      if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 0.0001){
				   		      //se ajusta la requisiscion
				   		       queryFormPost("pa_ajustePresupuestoRequisiscion", {async: false});
				   		      
				   		      }
				   		    
				   		    
				   		    }else{
				   		    	swal("La requisición no se puede imprimir porque no ha presupuestado el total de las líneas de la requisición.",{icon:"info",button: "Cerrar"});
				   		      	return;
				   		    }
				   		   
				   		}
				   		
				   	}	
			
				if(ext!='csv'){
					window.open(
					"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn=rptRequisiciones.jasper"
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else{
					window.open(
					"../../servlet/CatalogosCSV?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn=rptRequisiciones.jasper"
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
			}else{
				swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
			}
			
		}
		
		function cambiaColorFecha(){
			var longitudReq=$("#lblRequisicion").val().length;
			$("#Req").val($("#lblRequisicion").val().substring(2,(parseInt(longitudReq,10)-2)));
			queryFormPost("mFechaVencimiento", { async:false });
			
			$("#fechaVence").val($("#vence").val());
			if(parseInt($("#difFecha").val(),10)<4 ) 
				$("#fechaVence").css("color","red");
			if(parseInt($("#difFecha").val(),10)>3 & parseInt($("#difFecha").val(),10)<11)
				$("#fechaVence").css("color","orange");
			if(parseInt($("#difFecha").val(),10)>10)	
				$("#fechaVence").css("color","green");
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container" style="width: 90%;">
<!-- 			<table width="94%" align="left"> -->
<!-- 				<tr> -->
<!-- 					<td> -->
						<fieldset>
							<legend>Informaci&oacute;n de la Requisici&oacute;n</legend>
								<table  align="left" width="100%">
									<tr>
							    		<td align="right" colspan="2">
	                                        <input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfFirmantesReq" 		name="cmdPdfFirmantesReq" 	value="PDF"		onclick="openPDF('pdf');">&nbsp;&nbsp;
							    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdxlsFirmantesReq" 		name="cmdxlsFirmantesReq" 	value="Excel"	onclick="openPDF('xls');">&nbsp;&nbsp;
							    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvFirmantesReq" 		name="cmdcsvFirmantesReq" 	value="CSV"		onclick="openPDF('csv');">&nbsp;&nbsp;
											<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdwordFirmantesReq" 	name="cmdwordFirmantesReq" 	value="Word"	onclick="openPDF('doc');">&nbsp;&nbsp;
				                        	<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarFirmantesReq" 	name="imgAprobarFirmantesReq" 	value="Aprobar"	onclick="aprobarRequisicion();" style="display: none;">&nbsp;&nbsp;
				                        	<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverFirmantesReq" 	name="imgDevolverFirmantesReq" value="Devolver"	onclick="devolverRequisicion();" style="display: none;">&nbsp;&nbsp;
                                       		
				                            <input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 		id="imgAnularFirmantesReq" 	name="imgAnularFirmantesReq" 	value="Anular"	onclick="anularRequisicion();">&nbsp;&nbsp;
				                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	onclick="window.location = 'Requisiciones.jsp?tab=1&ses=0';">&nbsp;&nbsp;
	                                	</td>
							    	</tr>
							    	<tr>
								    	<td align="left" colspan="2">
								    		<input type="text" style="width: 30px;border: 0px none ;background:#FEFEFE" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly  value="<%=unidadUsuarioLogeado%>"/>
								    		<input type="text" style="width: 690px;border: 0px none ;background:#FEFEFE" id="lblDescUsuario" name="lblDescUsuario" readonly />
								    	</td>
							    	</tr>
							    	<tr>
								    	<td align="left" colspan="2">
								    		<input type="text" style="width: 95px;border: 0px none ;background:#FEFEFE" id="lblRequisicion" name="lblRequisicion" readonly />
								    		<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" id="lblDescripcion" name="lblDescripcion" readonly />
								    	</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2">
							    			<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly />
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblEstado" id="lblEstado" readonly /></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblPartida" id="lblPartida" readonly /></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblMesRequisicion" id="lblMesRequisicion" readonly/></td>
							    	</tr>
							    	<tr>
										<td align="left" colspan="2">Fecha de Apartado: 
											<input type="text" style="width: 90px;border: 0px none ;background:#FEFEFE"
											name="fechaVence" id="fechaVence" value="" readonly  />
										</td>
									</tr>
							    </table>
						</fieldset>	
						<fieldset>
							<legend>Firmantes de la Requisici&oacute;n</legend>
								
								<table id="tblFirmantesRequisicion"  class="display">
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
										
						</fieldset>
						<fieldset>
							<legend>Cat&aacute;logo de Firmantes</legend>
								
								<table  id="tblCatalogoFirmantes"  class="display">
						        	<thead>
						        		<tr>
						        			<th >Unidad Ejecutora</th>
						        			<th >#</th>
						        			<th >Nombre</th>
						        			<th >Responsabilidades</th>
						        		</tr>
						        	</thead>
						        </table>
										
						</fieldset>

			<input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud" value="<%=cIdTipoSolicitud%>" />
			<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
			<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
			<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>" />
			<input type="hidden" name="nNumeroFirmante" id="nNumeroFirmante" />
			<input type="hidden" name="nIdFirmante" id="nIdFirmante" />
			<input type="hidden" name="nFirmantes" id="nFirmantes" />
			
			<input type="hidden" name="nIdEstadoReq" id="nIdEstadoReq" />
		    <input type="hidden" name="nLineas" id="nLineas" />
		    <input type="hidden" name="fAprobacion" id="fAprobacion" />
		    <input type="hidden" name="fAnulacion" id="fAnulacion" />
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />
		    <input type="hidden" name="nIdEstadoPrecomprometido" id="nIdEstadoPrecomprometido" /> 
		    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="nConsolidados" id="nConsolidados" />
		    
		    <input type="hidden" name="cIdEstadoLinea" id="cIdEstadoLinea" />
		    
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab.getLogin()%>"/>
			<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
			<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuarioTab.getU_UR()%>" />

			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
			<input type="hidden" name="cIdDocumento" id="cIdDocumento"/>
			<input type="hidden" name="cAccion" id="cAccion"/>
			
			<input type="hidden" name="existeMod" id="existeMod"/>
			<input type="hidden" name="cIdSolicitudMod" id="cIdSolicitudMod"/>
			<input type="hidden" name="existeLineaApartadoSolicitud" id="existeLineaApartadoSolicitud"/>
			<input type="hidden" name="sumaLineasSolicitud" id="sumaLineasSolicitud"/>
			<input type="hidden" name="sumaLineasApartadoSolicitudLineas" id="sumaLineasApartadoSolicitudLineas"/>
			<input type="hidden" name="numeroLineasApartadoSolicitud" id="numeroLineasApartadoSolicitud"/>
			<input type="hidden" name="numeroLineasSolicitud" id="numeroLineasSolicitud"/>
			<input type="hidden" name="cIdSolicitudRep" id="cIdSolicitudRep"/>
			
			
			<input type="hidden" name="vence" id="vence"/>
			<input type="hidden" name="Req" id="Req"/>
			<input type="hidden" name="difFecha" id="difFecha"/>
			<input type="hidden" name="UES_USUARIO" id="UES_USUARIO" value=""/>
			<input type="hidden" name="isAdmin" id="isAdmin" value="1"/>
			<input type="hidden" name="modulo" id="modulo" value="MATERIALES"/>
			<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" value=""/>
			<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
	    </div>
	</form>
  </body>
</html>
