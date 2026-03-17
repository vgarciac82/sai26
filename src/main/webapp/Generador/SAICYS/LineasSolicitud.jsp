<%@ page language="java" pageEncoding="UTF-8"%>
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
	Map rol = usuarioTab.getRoles();
	
	String cEjercicio = "";
	String cIdTipoSolicitud = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String cIdSolicitu="";
	
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ReqEjercicio);
		cIdTipoSolicitud = (String)session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ReqConsecutivo);
		cIdSolicitu=cIdTipoSolicitud+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo;
	}
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuarioTab.getU_UR();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>L&iacute;neas de Captura</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	
	<script type="text/javascript" charset="utf-8">
		var oTableLineas;
		var oTableCAMBs;
		var oTableCAMBs2;
		var nAgregaRegistros = 0;
		var nEditaRegistros = 0;
		var band;
		var oTableCucop;	
		$(document).ready(function() {
			$("#tbs").val(3);
			showHideTabs();
			var apartables = [ "RC", "RM", "RO", "RS","RA","RT" ];
			if ($.inArray($("#cIdTipoSolicitud").val(), apartables) != -1){
				$("#imgAprobarLineasReq").hide();
				$("#imgDevolverLineasReq").hide();
			}
			<%
			    String roles="";
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
				int btnLnCopia=0;
				int btnLnElimina=0;
				Map botones=nb.getBotones(roles,"Requisiciones","LineasRequisiciones");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAnularLineasReq".equals(img)){  
						imgAnular=1; 
					}
					if ("imgAprobarLineasReq".equals(img)){
						imgAprobar=1;
					}
					if ("imgDevolverLineasReq".equals(img)){
						imgDevolver=1;
					}
					if ("cmdPdfLineasReq".equals(img)){
						imgPdf=1;
					}
					if ("btnLnCopiaLineas".equals(img)){
						btnLnCopia=1;
					}
					if ("btnLnElimina".equals(img)){
						btnLnElimina=1;
					}
				}

            /*
			   Solicitud de requerimiento, se debe homologar el evento Doble Click en todas las pantallas que utilicen el "Click"
			*/

				%> 
			band = false;
			var roles="<%=roles%>";
			$("#paginacion").val(1);
			queryFormPost("cg_roleRead", { async:false });
			
			$('#tblRequisicionMeses td:nth-child(3)').live('dblclick', function() {   
				agregarLinea(this,1);
			});
			$('#tblRequisicionMeses td:nth-child(4)').live('dblclick', function() {   
				agregarLinea(this,2);
			});
			$('#tblRequisicionMeses td:nth-child(5)').live('dblclick', function() {   
				agregarLinea(this,3);
			});
			$('#tblRequisicionMeses td:nth-child(6)').live('dblclick', function() {   
				agregarLinea(this,4);
			});
			$('#tblRequisicionMeses td:nth-child(7)').live('dblclick', function() {   
				agregarLinea(this,5);
			});
			$('#tblRequisicionMeses td:nth-child(8)').live('dblclick', function() {   
				agregarLinea(this,6);
			});
			$('#tblRequisicionMeses td:nth-child(9)').live('dblclick', function() {   
				agregarLinea(this,7);
			});
			$('#tblRequisicionMeses td:nth-child(10)').live('dblclick', function() {   
				agregarLinea(this,8);
			});
			$('#tblRequisicionMeses td:nth-child(11)').live('dblclick', function() {   
				agregarLinea(this,9);
			});
			$('#tblRequisicionMeses td:nth-child(12)').live('dblclick', function() {   
				agregarLinea(this,10);
			});
			$('#tblRequisicionMeses td:nth-child(13)').live('dblclick', function() {   
				agregarLinea(this,11);
			});
			$('#tblRequisicionMeses td:nth-child(14)').live('dblclick', function() {   
				agregarLinea(this,12);
			});
			
			document.getElementById("lblRequisicion").style.readonly=true;
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblDescripcion").style.readonly=true;
			document.getElementById("lblPartida").style.readonly=true;
			document.getElementById("lblMesRequisicion").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("rfvCAMB").style.readonly=true;
			
			queryFormPost("mSolicitud_LabelRead", { async:false });
			queryFormPost("mSolicitud_cIdSubPartidaRead", { async:false });
			queryFormPost("mSolicitudDescripcionUnidad", {async: false});
			
// 			querySelectPost("BuscarCABMsRead", "cIdCABM", { async:false });
			
			queryFormPost("cuentaConsolidadosRead", { async:false });
			queryFormPost("mSolicitudLineas_MontosRead", { async:false });
			
			var cadena = $("#montoNeto").val();
			cadena = '$' + cadena;
			var pos = $("#montoNeto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoNeto").val(cadena);
			
			cadena = $("#montoBruto").val();
			cadena = '$' + cadena;
			pos = $("#montoBruto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoBruto").val(cadena);
			queryFormPost("mUsuarioMismaUE", {async: false   });
			queryFormPost("mSolicitud_EstadoSolicitudRead", {async:false});
			if (!($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0
				|| $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val() 
				||parseInt($("#usuariosMismaUE").val(),10)==1)) {
				document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = true;
			}
			else if ($("#nIdEstadoReq").val() == "1") { //capturada
				document.getElementById("imgAprobarLineasReq").disabled = false;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = false;
			}
			else if ($("#nIdEstadoReq").val() == "2") { //solicitada
				document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("AgregarLineasAgrup").disabled = true;
				$("#imgAnularLineasReq").hide();
			}
			else if ($("#nIdEstadoReq").val() == "3") { //Aprobada
				document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = false;
				$("#imgAnularLineasReq").hide();
				document.getElementById("AgregarLineasAgrup").disabled = true;
			} 
			else if ($("#nIdEstadoReq").val() == "4") { //anulada
				document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				$("#imgAnularLineasReq").hide();
				document.getElementById("AgregarLineasAgrup").disabled = true;
			}
			else if ($("#nIdEstadoReq").val() == "5") { //habilitada por consolidado
				document.getElementById("imgAprobarLineasReq").disabled = false;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = true;
				document.getElementById("AgregarLineasAgrup").disabled = true;
			}
			else if($("#nIdEstadoReq").val() == "6"  || $("#nIdEstadoReq").val() == "7"){
				$("#imgAnularLineasReq").hide();
				$("#AgregarLineasAgrup").hide();
				$("#imgDevolverLineasReq").hide();
				$("#imgAprobarLineasReq").hide();
			}
			
			//una vez llenados todos los elementos, se desabilitan para evitar porblemas
			if (!(roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0||roles.indexOf("JEFES") >= 0 
				|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() || parseInt($("#usuariosMismaUE").val(),10)==1)
					|| $("#nIdEstadoReq").val() == "2"|| $("#nIdEstadoReq").val() == "3" || $("#nIdEstadoReq").val() == "4" ||  $("#nIdEstadoReq").val() == "5"||  $("#nIdEstadoReq").val() == "6" || $("#nIdEstadoReq").val() == "7") {
				document.getElementById("btnGuardarLineasReq").disabled = true;//.setAttribute("disabled", "disabled");
				document.getElementById("btnEliminarLineasReq").disabled = true;//setAttribute("disabled", "disabled");
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
				document.getElementById("AgregarLineasAgrup").disabled = true;
			}
			
			mostrarCucop();
			//mostrar();			
			mostrarLineas();
			
			if ($("#cIdCABM").val() == null)
				document.getElementById("trCABM").style.display = "table-row";
			
			$("#cIdCABM").change(function() {
				//oTableCAMBs.fnClearTable(oTableCAMBs);
				oTableCAMBs2.fnClearTable(oTableCAMBs2);
				//mostrar();
				mostrar2();
			}); 
			$("#btnGuardarLineasReq").button().click(function(){
			 	guardar();
			});
			
			$("#btnEliminarLineasReq").button().click(function(){
			 	eliminarLineas();
			});
			
			//Para Fecha de vencimiento
			cambiaColorFecha();
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
		
			$('#tblCucop tr').live('dblclick', function() {
				//$(this).addClass('row_selected');
				
				if($("#nIdEstadoReq").val()=="1"){
					if ( $(this).hasClass('row_selected') )             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					
					var anSelected = fnGetSelected( oTableCucop );
					if (anSelected != "") {
						var aData = oTableCucop.fnGetData(anSelected[0]);
						$("#cIdCABM").val(aData[0]);
						$("#cDescripcion").val(aData[1]);
						$(this).removeClass('row_selected'); 
						//mostrar();
						mostrar2();
					}
				}else{
					swal("El estatus de la requisici\u00f3n no permite esta operaci\u00f3n",{icon:"info",button: "Cerrar"});
				}
			});
		});
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
		function mostrarCucop() {
			var qw = "cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() 
					+ "' AND cIdSubPartida = '" + $("#cIdSubPartida").val() 
					+ "' AND cEjercicio = '" + $("#cEjercicio").val() + "'";
			oTableCucop =$("#tblCucop").dataTable({
				sScrollX: "100%",
				//sScrollXInner: "285%",
				bScrollCollapse: true,
				bDestroy: true,
				
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
				//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCucopsDisponiblesAgrupados&qw=" + qw,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mCucopsDisponiblesAgrupados('"+$("#cEjercicio").val()
						+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdSubPartida").val()+"','"+$("#cIdTipoSolicitud").val()+"')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cidCABM" },
					{ sName: "cCabm" },
					{ sName: "cantDisp" }
					
				],
				fnInitComplete: function(oSettings, json) {
						//mostrar();
						mostrar2();
					}
        	});
		}
		function mostrar() {
			//var qw = "(cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (cIdSubPartida = '" + $("#cIdSubPartida").val() + "') AND (cEjercicio = '" + $("#cEjercicio").val() + "') AND (CABM LIKE '" + $("#cIdCABM").val() + "')"; //" + $("#cIdCABM").val() + "
			oTableCAMBs = $("#tblRequisicionMeses").dataTable({
				sScrollX: "100",
				//sScrollXInner: "285%",
				bScrollCollapse: true,
				bDestroy: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUnidadEjecutora").val() + "', '" + $("#cIdCABM").val() + "' , '" + 
					 $("#cIdSubPartida").val() + "')",//"&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ], [ 2, "asc" ], [ 3, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "enero" },
					{ sName: "febrero" },
					{ sName: "marzo" },
					{ sName: "abril" },
					{ sName: "mayo" },
					{ sName: "junio" },
					{ sName: "julio" },
					{ sName: "agosto" },
					{ sName: "septiembre" },
					{ sName: "octubre" },
					{ sName: "noviembre" },
					{ sName: "diciembre" },
					{ sName: "nPorcentajeIVA"}
				]
        	});
		}
		function mostrar2() {
			//var qw = "(cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (cIdSubPartida = '" + $("#cIdSubPartida").val() + "') AND (cEjercicio = '" + $("#cEjercicio").val() + "') AND (CABM LIKE '" + $("#cIdCABM").val() + "')"; //" + $("#cIdCABM").val() + "
			oTableCAMBs2 = $("#tblRequisicionMeses2").dataTable({
				sScrollX: "100%",
				//sScrollXInner: "285%",
				bScrollCollapse: true,
				bDestroy: true,
				"iDisplayLength": 12,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario2('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUnidadEjecutora").val() + "', '" + $("#cIdCABM").val() + "' , '" + 
					 $("#cIdSubPartida").val() + "','" + 
					 $("#cIdTipoSolicitud").val() + "')",//"&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdPeriodo" ,bVisible: false},
					{ sName: "mes" },
					{ sName: "cIdCABM" },
					{ sName: "nCantidadDisponibilidad" },
					{ sName: "mMontoDisponibilidad" },
					{ sName: "nPorcentajeIVA" ,bVisible: false},
					{ sName: "cCABM",bVisible: false},
					{ sName: "CantAgregar" },
					{ sName: "mPrecioUnitario" ,bVisible: false}
					
				]
        	});
		}
		function mostrarLineas() {
			$('#tblLineas').dataTable().fnClearTable();
			queryFormPost("mSolicitudLineas_MontosRead", { async:false });
			var roles="<%=roles%>";
			var cadena = $("#montoNeto").val();
			cadena = '$' + cadena;
			var pos = $("#montoNeto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoNeto").val(cadena);
			
			cadena = $("#montoBruto").val();
			cadena = '$' + cadena;
			pos = $("#montoBruto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoBruto").val(cadena);
			var btnLnCopia=<%=btnLnCopia%>;
			var btnLnElimina=<%=btnLnElimina%>;
			oTableLineas = $("#tblLineas").dataTable({
				sScrollX: "100%",
				bScrollCollapse: true,
				bDestroy: true,
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
				bProcessing: true,
				bLengthChange: true,
				iDisplayLength: 10,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + 
					"/" + window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_mSolicitudLineasIndices('" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() + "','" + $("#cIdTipoSolicitud").val() + "'," + $("#nIdConsecutivo").val() + ",'" + $("#cIdSubPartida").val()
						+ "')", //&qw=" + qw,
				
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
// 					{ sName: "lineaCopia" ,bVisible: false},
					{ sName: "nIdLineas" },
					{ sName: "cIdCABM" },
					{ sName: "cMes" ,bVisible: false},
					{ sName: "cDescripcion" },
					{ sName: "cDescripcionAdicional" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" },
					{ sName: "mPrecioUnitarioPAAS" },
					{ sName: "nPorcentajeIVA" },
					{ sName: "mImporteNeto" },
					{ sName: "cIdUnidadMedida" },
					{ sName: "cDisponible",bVisible: false },
					{ sName: "cMontoDisponible",bVisible: false },
					{ sName: "lineaElimina" },
					{ sName: "nIdPeriodo"},
					{ sName: "nIdLineaSolicitud"}
				] ,
					fnInitComplete: function(oSettings, json) {
						queryFormPost("mSolicitudLineasCuentaRead", { async:false });
						$("#" + $("#paginacion").val()).click();
					}
					, fnDrawCallback: function(oObj) {
						if (!(roles.indexOf("ADMIN_RECMAT") >= 0||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 
							|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() ||parseInt($("#usuariosMismaUE").val(),10)==1)
							|| $("#nIdEstadoReq").val() == "2"|| $("#nIdEstadoReq").val() == "3" || $("#nIdEstadoReq").val() == "4" || $("#nIdEstadoReq").val() == "5") {
							
							//swal("Líneas: "+$("#nLineas").val(),{icon:"warning",button: "Cerrar"});
							for (var i = 1; i <= $("#nLineas").val(); i++) {
								if ($("#nIdLineas_" + i).val() != null) {
									//$("#btnLnCopia_" + i).css("visibility", "hidden");
									$("#btnLnElimina_" + i).css("visibility", "hidden");
									$("#cDescripcion_" + i).css("border-width", "0");
									$("#cDescripcion_" + i).css("readonly", "readonly");
									$("#cDescripcion_" + i).css("background-color", "transparent");
									$("#cDescripcionAdicional_" + i).css("readonly", "readonly");
									$("#cDescripcionAdicional_" + i).css("background-color", "transparent");
									$("#nCantidad_" + i).css("border-width", "0");
									$("#nCantidad_" + i).css("readonly", "readonly");
									$("#nCantidad_" + i).css("background-color", "transparent");
									$("#mPrecioUnitario_" + i).css("border-width", "0");
									$("#mPrecioUnitario_" + i).css("readonly", "readonly");
									$("#mPrecioUnitario_" + i).css("background-color", "transparent");
								}
							}
						}
					}
        	});
		}
		function agregarLinea(td,mes) {
			
			var roles="<%=roles%>";
			if ($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() != "5" &&
					(roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val())) {     
  				$("#cIdCABMIns").val($("#cIdCABM").val()); //(aPos[0] + 1)
				$("#cDescripcion").val($("#cCABM").val());
				
				$("#nIdPeriodo1").val(mes + "");
				$("#nIdPeriodo2").val(mes + "");
				queryFormPost("obtenMontoYCantidadRead", { async:false });
				
				$("#nPorcentajeIVA").val($("#porcentaje_iva").val()); //_" + aPos[0]
				if (parseInt($("#nCantidadDisponibilidad").val(),10) > 0) {
					if (parseFloat($("#mPrecioUnitario").val()) <= parseFloat($("#mMontoDisponibilidad").val())) {
						$("#cTipos").val(1);
						$("#nIdPeriodo").val(mes + "");
						queryFormPost("insertaSolicitudLinea_Tipos", {async:false});
						//oTableCAMBs.fnClearTable(oTableCAMBs);
						oTableCAMBs2.fnClearTable(oTableCAMBs2);
						oTableLineas.fnClearTable(oTableLineas);
						//mostrar();
						mostrarLineas();
					}
					else {
						swal("No se cuenta con presupuesto para este CUCOP, edite el programa anual.",{icon:"info",button: "Cerrar"});
					}
				}
				else {
					swal("No hay disponiblidad para este CUCOP, edite el programa anual.",{icon:"info",button: "Cerrar"});
				}
			}else{
				swal("No tienes permisos o el estado de la requisici\u00f3n no permite hacer cambios",{icon:"info",button: "Cerrar"});
			}
		}
		function agregarLineasAgrupadas(){
			var roles="<%=roles%>";
			var token="";
			var cadenaMesesCantidad="";
			var cadenaCantidad="";
			var cadenaMeses="";
			$("#cIdCABMIns").val($("#cIdCABM").val()); 
			if ($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() != "5" &&
					(roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 
					|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()||parseInt($("#usuariosMismaUE").val(),10)==1 ) ) { 
				var aTrs = oTableCAMBs2.dataTable().fnGetNodes();
				if(aTrs.length==0){
					swal("Favor de hacer doble clic en un registro de la primera tabla.",{icon:"info",button: "Cerrar"});
					return;
				}
				for ( var i=0 ; i<aTrs.length; i++ )     
				{       					
					aData = oTableCAMBs2.fnGetData(aTrs[i]);
					if(parseInt($("#cantAgregar"+(aData[0])).val(),10)!=0 && $("#cantAgregar"+(aData[0])).val()!= ""){
						cadenaMesesCantidad+=token+aData[0]+"-"+$("#cantAgregar"+(aData[0])).val()+"|"+aData[8];
						cadenaCantidad+=token+$("#cantAgregar"+(aData[0])).val();
						cadenaMeses+=token+aData[0];
						token=",";
						$("#mPrecioUnitario").val(aData[8]);
						$("#nPorcentajeIVA").val(aData[5]);
					}
				}
				$("#cadenaMesesCantidad").val(cadenaMesesCantidad);
				if($("#cadenaMesesCantidad").val()==""){
					swal("Favor de capturar la cantidad de bienes a registrar.",{icon:"info",button: "Cerrar"});
				}else{
					queryFormPost("insertaSolicitudLinea_Agrupadas", {async:false});
					//oTableCAMBs.fnClearTable(oTableCAMBs);
					oTableCAMBs2.fnClearTable(oTableCAMBs2);
					oTableLineas.fnClearTable(oTableLineas);
					mostrarCucop();
					mostrar2();
					mostrarLineas();
					//Bitácora
					$("#cAccion").val("AGREGAlINEA_REQUISICION");
					//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				}
			}else{
				swal("No tienes permisos o el estado de la requisici\u00f3n no permite hacer cambios",{icon:"info",button: "Cerrar"});
			}
		}
		function guardar() {
			queryFormPost("mSolicitudLineasCuentaRead", { async:false });
			for (var i = 1; i <= $("#nLineas").val(); i++) {
				if ($("#nIdLineas_" + i).val() != null) { //no se pueden encontrar todos los id's debido a la paginacion
					$("#nIdLineaSolicitud").val($("#nIdLineas_" + i).val());
					$("#cDescripcionAdicional").val($("#cDescripcionAdicional_" + i).val());
					$("#nCantidad").val($("#nCantidad_" + i).val());
					$("#mPrecioUnitario").val($("#mPrecioUnitario_" + i).val().replace("$", "").replace(",", ""));
					$("#mPrecioUnitarioPAAS").val($("#mPrecioUnitarioPAAS_" + i).val().replace("$", "").replace(",", ""));
					$("#nIdPeriodo").val($("#nIdPeriodo_" + i).val());
					$("#cIdCABMIns").val($("#cIdCABMLinea_" + i).val());
					$("#mMontoNetoLinea").val($("#mImporteNeto_" + i).val().replace("$", "").replace(",", ""));
					queryFormPost("updateSolicitudLinea", {async:false});
				}
				queryFormPost("deleteSolLineaApartado", {async:false});
			}
			//oTableCAMBs.fnClearTable(oTableCAMBs);
			oTableCAMBs2.fnClearTable(oTableCAMBs2);
			oTableLineas.fnClearTable(oTableLineas);
			//mostrar();
			mostrar2();
			mostrarLineas();
			swal("Datos Guardados.\nRevisa que tus montos netos sean los correctos en cada l\u00ednea.\n Puedes modificar centavos en el importe neto de cada línea.",{icon:"info",button: "Cerrar"});

		}
		
		function eliminarLineas() {
			swal({
				title: "¿Est\u00e1 seguro que desea eliminar las l\u00edneas? ",
				text: "Una vez confirmado, no podrá deshacer los cambios!",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					var min = -1;
					var max = -1;
					var i = 0;
					while (min == -1) {
						if ($("#nIdLineas_" + i).val() != null) {//no se encuentran todos los id's debido a la paginación
							min = parseInt($("#nIdLineas_" + i).val(),10);
						}
						i++;
					}
					max = min + 10 - 1; //10 por la paginacion
					$("#max").val(max);
					$("#min").val(min);
					queryFormPost("eliminaLineasDesdeHasta", { async:false });
					queryFormPost("deleteSolLineaApartado", {async:false});
					//oTableCAMBs.fnClearTable(oTableCAMBs);
					oTableCAMBs2.fnClearTable(oTableCAMBs2);
					oTableLineas.fnClearTable(oTableLineas);
					mostrarCucop();
					mostrar2();
					mostrarLineas();
					//Bitácora
					$("#cAccion").val("ELIMINAlINEAS_REQUISICION");
					//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				}
			});
		}
		
		function eliminaLinea(indiceTabla) {
			var roles="<%=roles%>";
			if ((roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() ||parseInt($("#usuariosMismaUE").val(),10)==1) &&
					($("#nIdEstadoReq").val() == "1")) {
				$("#nIdLineaSolicitud").val($("#nIdLineas_" + indiceTabla).val());
				queryFormPost("pa_mSolicitudLineasBorrarYRenumerar", {async:false});
				queryFormPost("deleteSolLineaApartado", {async:false});
				//oTableCAMBs.fnClearTable(oTableCAMBs);
				oTableCAMBs2.fnClearTable(oTableCAMBs2);
				oTableLineas.fnClearTable(oTableLineas);
				mostrarCucop();
				mostrar2();
				mostrarLineas();
				//Bitácora
				$("#cAccion").val("ELIMINAlINEA_REQUISICION");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}
		}
		
		function copiaLinea(indiceTabla) {
			var roles="<%=roles%>";
			if ((roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() ||parseInt($("#usuariosMismaUE").val(),10)==1) &&
					($("#nIdEstadoReq").val() == "1")) {
				//actualizacion de los registros, previo al copiado de las líneas
				for (var i = 1; i <= $("#nLineas").val(); i++) {
					if ($("#nIdLineas_" + i).val() != null) { //no se pueden encontrar todos los id's debido a la paginacion
						$("#nIdLineaSolicitud").val($("#nIdLineas_" + i).val());
						$("#cDescripcion").val($("#cDescripcion_" + i).val());
						$("#nCantidad").val($("#nCantidad_" + i).val());
						$("#mPrecioUnitario").val($("#mPrecioUnitario_" + i).val().replace("$", ""));
						$("#nIdPeriodo").val($("#nIdPeriodo_" + i).val());
						$("#cIdCABMIns").val($("#cIdCABMLinea_" + i).val());
						queryFormPost("updateSolicitudLinea", {async:false});
					}
				}
				if ($("#cDisponible_" + indiceTabla).val() > 0) {
					$("#cTipos").val(1);
					//$("#mPrecioUnitario").val($("#mPrecioUnitario_" + indiceTabla).val().replace("$", ""));
					$("#nIdPeriodo").val($("#nIdPeriodo_" + indiceTabla).val());
					$("#cIdCABMIns").val($("#cIdCABMLinea_" + indiceTabla).val());
					queryFormPost("obtencCABMRead", { async : false });
					//$("#cDescripcion").val($("#cDescripcion_" + indiceTabla).val());
					//$("#nPorcentajeIVA").val($("#mPorcentajeIVA_" + indiceTabla).val().replace("%", ""));
					queryFormPost("obtencPrecioPARead", { async : false });
					queryFormPost("obtencIVAPARead", { async : false });
					queryFormPost("insertaSolicitudLinea_Tipos", {async:false});
					$("#nIdLineas_" + i).val();
					//oTableCAMBs.fnClearTable(oTableCAMBs);
					oTableCAMBs2.fnClearTable(oTableCAMBs2);
					oTableLineas.fnClearTable(oTableLineas);
					//mostrar();
					mostrar2();
					mostrarLineas();
				}
				else
					swal("Ya no hay disponibilidad en Programa Anual. Editarlo para agregar m\u00e1s l\u00edneas.",{icon:"info",button: "Cerrar"});
			}else{
				swal("No tienes permisos o el estado de la requisici\u00f3n no permite hacer cambios",{icon:"info",button: "Cerrar"});
			}
		}
		
		function aprobarRequisicion() {
			var imgAprobar='<%=imgAprobar%>';
			if (imgAprobar==0){
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
			if ($("#nLineas").val() > 0) {
				//Regla de negocio
				var currentTime = new Date();
				   
				var fechaCompleta=currentTime.getFullYear() + '-' + currentTime.getDate() + '-' +(currentTime.getMonth()+1) + ' ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '.' + currentTime.getMilliseconds();
				$("#fAprobacion").val(fechaCompleta);
				$("#nIdEstado").val("3"); //aprobada
				$("#nIdEstadoPrecomprometido").val("3"); //para regularización
				queryFormPost("mSolicitudAprobarUpdate", { async:false });
				queryFormPost("sp_mSolicitudEstadoPrecomprometido", {async:false});
				//Bitácora
				$("#cAccion").val("APRUEBA_REQUISICION");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				
				swal("La requicisi\u00f3n ha sido aprobada.",{icon:"info",button: "Cerrar"});
				window.location = "Requisiciones.jsp?tab=3";
			}
			else
				swal("Por lo menos debe haber una l\u00ednea capturada para aprobar la requisici\u00f3n",{icon:"info",button: "Cerrar"});
			}else{
				swal("No tiene permisos para acceder a esta opci\u00f3n",{icon:"info",button: "Cerrar"});
			}
			
		}
	
		
		function devolverRequisicion () {
			var imgDevolver='<%=imgDevolver%>';
			var miPopup;
			if (imgDevolver==0){
				
				if ($("#cIdTipoSolicitud").val() == 'RM') {
					$("#cIdSolicitudMod").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());					
					$("#existeMod").val('');
					queryFormPost("mPedidoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						swal("No se puede devolver la requicisi\u00f3n porque esta asociada a un pedido modificatorio",{icon:"info",button: "Cerrar"});
						return;
					}
					
					$("#existeMod").val('');
					queryFormPost("mContratoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						swal("No se puede devolver la requicisi\u00f3n porque esta asociada a un contrato modificatorio",{icon:"info",button: "Cerrar"});
						return;
					}
				}
				
				if ($("#nConsolidados").val() == "0" || $("#nConsolidados").val() == "") {
				//Regla de negocio
				
				var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
				window.open("LineasSolicitudNotas.jsp?tipo=1&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
			}
			else
				swal("Esta requisici\u00f3n est\u00e1 asociada a un consolidado. Para devolverla, primero es necesario eliminarla del consolidado.",{icon:"info",button: "Cerrar"});
			}else{
				swal("No tiene permisos para acceder a esta opci\u00f3n",{icon:"info",button: "Cerrar"});
			}
			
		}
		function anularRequisicion () {	  
			
			var imgAnular='<%=imgAnular%>';
			if (imgAnular==0){
				var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
				window.open("LineasSolicitudNotas.jsp?tipo=2&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
			}else{
				swal("No tiene permisos para acceder a esta opci\u00f3n",{icon:"info",button: "Cerrar"});
			}
			
		}

		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			return (keyPressed >= 48 && keyPressed <= 57);
		}

		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function openPDF(ext){
			
			var imgPdf='<%=imgPdf%>';
			if (imgPdf==0){
			 if($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" ){
			  $("#cIdSolicitudRep").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
			   //se revisa si ya existe una linea en la tabla de msolicitudlineasapartado
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
				   		 	swal("La suma de las l\u00edneas presupuestadas no es igual a la suma de las l\u00edneas de solicitud, favor de revisar.",{icon:"info",button: "Cerrar"});
				   		    return;
				   		   }
				   		    else 				   		    
				   		      if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 0.0001){
				   		      	//se ajusta la requisiscion
								queryFormPost("pa_ajustePresupuestoRequisiscion", {async: false});
				   		      }
				   		    }else{
				   		    	swal("La requisici\u00f3n no se puede imprimir porque no ha presupuestado el total de las l\u00edneas de la requisici\u00f3n.",{icon:"info",button: "Cerrar"});
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
				swal("No tiene permiso para acceder a esta opci\u00f3n",{icon:"info",button: "Cerrar"});
			}
			
		}
		
		function metodo(i) {
			if (band == false) {
				if (i == -1)
					i = $("#paginacion").val() - 1;
				if (i == -2)
					i = $("#paginacion").val() + 1;
				$("#paginacion").val(i);
			}
			band!=band;
		}
		var availableTags=[""];
  		function autoCoplete(name,CUCOP){
  			var qw = "'"+CUCOP+"'"; 
			$("#cucop").val(CUCOP);
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: "AUTOCOMPLETECUCOP", Param:"",Campos:qw, MaxReg:"" , ajax: 'false'}, 
			function(j){
				for (var i = 0; i < j.length; i++) {
		   			availableTags[i]=j[i].Col0;
		   		}
			});
  		}
  		
  		
  		function textCounter(name,field,maxlimit) {
  			 $( "#"+name ).autocomplete({
  			 	source: availableTags,
  			 	minLength: 0}).focus(function () {
    				$(this).autocomplete("search");
				});
  			
  			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function cambiafrmt(fld){
	   		$("#"+fld.id).formatCurrency();
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
  
  <body id="dt_example">
  	<form action="">
  		<div id="container" class="container" style="width: 98%;">
  			<fieldset >
				<legend>Informaci&oacute;n de la Requisiciones</legend>
				<table align="left" width="100%">
					<tr>
			    		<td align="right" colspan="2">
                            <input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdPdfLineasReq" 		name="cmdPdfLineasReq" 	value="PDF"		onclick="openPDF('pdf');">&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdxlsLineasReq" 		name="cmdxlsLineasReq" 	value="Excel"	onclick="openPDF('xls');">&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdcsvLineasReq" 		name="cmdcsvLineasReq" 	value="CSV"		onclick="openPDF('csv');">&nbsp;&nbsp;
							<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdwordLineasReq" 		name="cmdwordLineasReq" value="Word"	onclick="openPDF('doc');">&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarLineasReq" 	name="imgAprobarLineasReq" 	value="Aprobar"		onclick="aprobarRequisicion();" style="display: none;">&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverLineasReq" 	name="imgDevolverLineasReq" value="Devolver"	onclick="devolverRequisicion();" style="display: none;">&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAnularLineasReq" 	name="imgAnularLineasReq" 	value="Anular"	onclick="anularRequisicion();">&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 	id="imgSalir" 				name="imgSalir" 			value="Salir"	onclick="window.location = 'Requisiciones.jsp?tab=1&ses=0';">&nbsp;&nbsp;
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
							<input type="text" style="width: 90px;border: 0px none ;background:#FEFEFE"name="fechaVence" id="fechaVence" value="" readonly  />
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset >
  				<legend>CUCOPS Calendarizados en el PAAS</legend>
  				<table align="left">
					<tr id="trCABM" style="display:none">
		    			<td align="left"><textarea style="width: 700px" id="rfvCAMB" name="rfvCAMB" readonly style="color: red; border-width:0; background-color:transparent; overflow: auto;" >No existe ningun CUCOP en el Programa Anual que cumpla los requisitos de la requisici&oacute;n o no cuenta con suficiencia en el periodo</textarea>
		    			</td>
		    		</tr>
		    		<tr align="left">
		    			<td align="left"  >
		    			<input name="cIdCABM" id="cIdCABM" type="hidden" value=""/>
		    			</td>
		    		</tr>
		    		<tr align="left" style="display: none;">
		    			<td align="left"><img id="imgRefresh" src="../../imagenes/icono_refresh.jpg" style="cursor: pointer" onclick="oTableCAMBs.fnClearTable(oTableCAMBs); oTableCucop.fnClearTable(oTableCucop); mostrarCucop();"/> Actualizar </td>
		    		</tr>
				</table>
				<br />
				<table id="tblCucop" class="display" >
					<thead>
						<tr>
							<th align="center" >CUCOP</th>
							<th align="center" >Descripción</th>
							<th align="center" >CantidadDisp_PAAS</th>
						</tr>
					</thead>
				</table>
				<br/>
				<table id="tblRequisicionMeses" class="display" style="display: none;">
					<thead >
						<tr>
							<th align="center">&nbsp;&nbsp;CUCOP&nbsp;&nbsp;</th>
							<th align="center" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Descripci&oacute;n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							<th align="center" >ENERO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center">FEBRERO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >MARZO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >ABRIL<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >MAYO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >JUNIO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >JULIO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >AGOSTO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center">SEPTIEMBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >OCTUBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center">NOVIEMBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" style="width: 300px">DICIEMBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" style="display: none;"></th>
						</tr>															
					</thead>
				</table>
				<br />
				<table id="tblRequisicionMeses2" class="display">
					<thead >
						<tr>
							<th align="center" style="display: none;"></th>
							<th align="center" >Mes</th>
							<th align="center" >Cucop</th>
							<th align="center">Cantidad <br />Disponible </th>
							<th align="center" >Monto <br />Disponible </th>
							<th align="center"  style="display: none;">nPorcentaje IVA</th>
							<th align="center" style="display: none;">Descripci&oacute;n</th>
							<th align="center" >Cantidad</th>
							<th align="center" style="display: none;"></th>
						</tr>															
					</thead>
				</table>
				<input type="button" id="AgregarLineasAgrup" name="AgregarLineasAgrup" value="Agregar" title="Agrega CUCOP" onclick="agregarLineasAgrupadas()" class="btnInterfaceBG ui-button ui-corner-all" />
  			</fieldset>
  			<fieldset >
  				<legend>Agregar CUCOPS a la Requisici&oacute;n</legend>
  				<table width="90%" align="left">
					<tr>
						<td align="left">Monto bruto de la requisici&oacute;n:<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" id="montoBruto" name="montoBruto" readonly /></td>
					</tr>
  					<tr>
  						<td align="left">Monto neto de la requisici&oacute;n:<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" id="montoNeto" name="montoNeto" readonly /></td>
  					</tr>
				</table>
				<table id="tblLineas" class="display">
					<thead>
				  		<tr>
<!-- 					  		<th style="display: none;"></th> -->
							<th>#</th>
							<th>CUCOP</th>
							<th style="display: none;">MES</th>
							<th >Descripci&oacute;n</th>
							<th style="width: 300px">Descripci&oacute;n Adicional</th>
							<th>Cantidad</th>
							<th>Precio<br/>Unitario</th>
							<th>P.U<br/>Promedio PAAS</th>
							<th>IVA</th>
							<th>Importe<br />Neto</th>
							<th>Unidad de<br />Medida</th>
							<th style="display: none;">Cantidad<br/>Disponible</th>
							<th style="display: none;">Monto<br />Disponible<br/>PAAS</th>
				   			<th></th>
				   			<th></th>
				   			<th></th>
				   		</tr>
				   	</thead>
				</table>
				<br/>
				<table>
					<tr>
						<td align="left">
						<input type="button" id="btnGuardarLineasReq" name="btnGuardarLineasReq" value="Guardar" class="btnInterfaceBG" />
						<input type="button" id="btnEliminarLineasReq" name="btnEliminarLineasReq" value="Eliminar L&iacute;neas" class="btnInterfaceBG" />
						</td>
					</tr>
				</table>
  			</fieldset>
  		</div>
  		<input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud" value="<%=cIdTipoSolicitud%>"/>
        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdSubPartida" id="cIdSubPartida"/>
	    <input type="hidden" name="cIdSolicitu" id="cIdSolicitu" value="<%=cIdSolicitu%>" />
	    <input type="hidden" name="cIdCABMIns" id="cIdCABMIns"/>
	    <input type="hidden" name="cDescripcion" id="cDescripcion"/>
	    <input type="hidden" name="cTipos" id="cTipos"/>
	    <input type="hidden" name="mPrecioUnitario" id="mPrecioUnitario"/>
	    <input type="hidden" name="mPorcentajeIVA" id="mPorcentajeIVA"/>
	    <input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA"/>
	    <input type="hidden" name="nIdPeriodo" id="nIdPeriodo"/>
	    <input type="hidden" name="nIdLineaSolicitud" id="nIdLineaSolicitud"/>
	    <input type="hidden" name="nCantidad" id="nCantidad"/>
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
		<input type="hidden" name="nCantidadDisponibilidad" id="nCantidadDisponibilidad" />
		<input type="hidden" name="mMontoDisponibilidad" id="mMontoDisponibilidad" />
		<input type="hidden" name="nIdPeriodo1" id="nIdPeriodo1" />
		<input type="hidden" name="nIdPeriodo2" id="nIdPeriodo2" />
		<input type="hidden" name="min" id="min" />
		<input type="hidden" name="max" id="max" />
		<input type="hidden" name="deshabilitado" id="deshabilitado"/>
		<input type="hidden" name="paginacion" id="paginacion" value="1"/>
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
		<input type="hidden" name="nFirmantes" id="nFirmantes" />
		<input type="hidden" name="nIdFirmante" id="nIdFirmante" />
		<input type="hidden" name="nNumeroFirmante" id="nNumeroFirmante" />
		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" value=""/>
		<input type="hidden" name="cDescripcionAdicional" id="cDescripcionAdicional" value=""/>
		<input type="hidden" name="cadenaMesesCantidad" id="cadenaMesesCantidad" value=""/>
		<input type="hidden" name="mPrecioUnitarioPAAS" id="mPrecioUnitarioPAAS" value=""/>
		<input type="hidden" name="mMontoNetoLinea" id="mMontoNetoLinea" value=""/>
		<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
  	</form>
  </body>
</html>
