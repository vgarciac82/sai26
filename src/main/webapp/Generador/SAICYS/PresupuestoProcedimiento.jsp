<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if(usuario==null){
		response.sendRedirect("../index.jsp");
		return;
	}
	String roles="";
	Map rol =usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoProcedimiento = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdTipoProcedimiento = (String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
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
		var epDT;
		var Pp, Pp2, Pp3;
		var oCurrentFocus;
		var timeouts = [];
		var clavesDT;
		$(document).ready(function() {
			$("#tbs").val(10);
			showAndHideTabs();
		<%
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
		
		%>
			$("#U_LOGIN").val('<%=usuario.getLogin()%>');	
			$("#usuarioLogin").val('<%=usuario.getLogin()%>');
			$("#cIdUsuario").val('<%=usuario.getLogin()%>');
			$("#usuarioRoleProcedimiento").val('<%=roles%>');
			var roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}	
			querySelectPost("CategoriaRead", "cboCategoria", {async : false});
			$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');	
			muestraInformacion();
			habilitarPestanas();	
			//Carga valores de Pps
			Pp = $('#Partida').val();
			
			Pp2 = $('#Partida2').val();
			
			Pp3 = $('#Partida3').val();
		
			for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) {
					//Si ya hay un timeout, se cancela
					if (timeouts[i])
						clearInterval(timeouts[i]);
					//Nuevo timeout
					timeouts[i] = setTimeout(function(){
						oCurrentFocus = this; 
						fnFilterColumn(evt);
						$("#pbAceptar").css("visibility","hidden");
						$('#tblSaldos').dataTable().fnClearTable();
						//document.getElementById("col"+i+"_filter").focus();
					}, 500);
				});
			epDTclick();
			queryFormPost("cg_roleRead", { async:false });
			queryFormPost("mValidaPrecompromisoProcedimiento",{async:false});
			if($("#documentoAplicadoProcedimiento").val()==0){ // precompromiso cancelado
				$(".botones").attr("disabled", false);
			}else{
				$(".botones").attr("disabled", true);
			}
				epDT = initDTEPs("");
			initDTSaldos();
			clavesDt=initDTClaves();
			$("#btnAgregar").button().click(function(){
				//valida que no exista la ep
				if($("#nIdClaveEgresos").val()==''){
					alert("Seleccione una EP.");
					return;
				}
				queryFormPost("existeEp", {async:false});
				
				if($("#existeEp").val()==1){
					alert("La clave presupuestal que intenta agregar ya se encuentra registrada");
					$("#existeEp").val("");
					return;
				}else{
					queryFormPost("insertaProcedimientoEp",{async:false});
					clavesDt=initDTClaves();
				}
			});
		activacheck();
		//RADICADO
			queryFormPost("readBanderaRadicado", { async:false });
			if($("#BanderaRadicado").val()=="S"){
				document.getElementById('checkDispRadicado').disabled = false;
			}else{
				document.getElementById('checkDispRadicado').disabled = true;
			}
	});
	
	function initDTSaldos(){	
		$("#tblSaldos").dataTable({
			bAutoWidth : true,
			bPaginate : false,
			bLengthChange : false,
			bInfo : false,
			sScrollX: "350%",
			//sScrollXInner: "250%",
			bScrollCollapse: true,
			bJQueryUI: true,
			bFilter : false,
			bSort : false,
			bInfo : false,
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
				oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }},
			aoColumns: [
				{ sName: "MontoEnero",		bSortable: false },
				{ sName: "MontoFebrero",	bSortable: false },
				{ sName: "MontoMarzo",		bSortable: false },
				{ sName: "MontoAbril",		bSortable: false },
				{ sName: "MontoMayo",		bSortable: false },
				{ sName: "MontoJunio",		bSortable: false },
				{ sName: "MontoJulio",		bSortable: false },
				{ sName: "MontoAgosto",		bSortable: false },
				{ sName: "MontoSeptiembre",		bSortable: false },
				{ sName: "MontoOctubre",		bSortable: false },
				{ sName: "MontoNoviembre",		bSortable: false },
				{ sName: "MontoDiciembre",		bSortable: false },
				{ sName: "MontoAnual",	bSortable: false }
				]
     			});
     		}
       		
       		

	function initDTEPs(w){

		var where="((substring(Partida,1,1)='" + Pp + 
			"') OR (substring(Partida,1,1)='" + Pp2 +
			"') OR (substring(Partida,1,1)='" + Pp3 + "'))" + w;
		if ($("#U_LOGIN").val()==$("#cIdUsuarioCreacion").val() || $('#R_NOMBRE').val()=='ADMIN_RECMAT'){
	    		esAdmin=1;
		}else{
			esAdmin=2;
		}
		
		var funcion="fn_EpsConMontoAnualModProced";
		return oTableEP= $("#tblEP").dataTable({
			bDestroy: true,
			fnDrawCallback: function() {
				$(oCurrentFocus).focus(function() {
					if (this.createTextRange) {
						var r = this.createTextRange();
						r.collapse(false);
						r.select();
					}
					this.focus();
				});
				$(oCurrentFocus).focus();
			},
			bAutoWidth : true,
			sScrollX: "100px",
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
			//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPGridPrecom&qw="+where,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+funcion+"('"
					+$('#isAdmin').val()+"','"+$('#cIdUnidadEjecutoraUsuario').val()
 					+"','"+$('#U_LOGIN').val()+"','"+$('#modulo').val()+"','"+$('#cIdConsolidado').val()+"','"+$('#cuentaDisponible').val()
					+"')&qw="+where,
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aoColumns: [
				{ sName: "EjercicioFiscal"   },
				{ sName: "Ramo" },
				{ sName: "UnidadResponsable"	},
				{ sName: "GrupoFuncional" },
				{ sName: "Funcion"   },
				{ sName: "SubFuncion" },
				{ sName: "ProgramaGeneral"	},
				{ sName: "ActividadInstitucional" },
				{ sName: "ProgramaPresupuestario"   },
				{ sName: "Partida" },
				{ sName: "TipoGasto"	},
				{ sName: "FuenteFinanciamiento" },
				{ sName: "EntidadFederativa"   },
				{ sName: "Cartera" },
				{ sName: "UnidadEjecutora"	},
				{ sName: "UnidadNorativa" }
			]
       	});
     }		
     
	function epDTclick(){
		$('#tblEP tr').live('dblclick', function() { 
			$(this).addClass('row_selected');
			var anSelected = fnGetSelected( oTableEP );
			$('#tblSaldos').dataTable().fnClearTable();
			if (anSelected != "") {
				var aData = oTableEP.fnGetData(anSelected[0]);
				//Construye la cadena EP para consultar los disponibles
				var anSelected = fnGetSelected( epDT );
				var sEP = 	aData[0]+"." +
							aData[1]+"." +
							aData[2]+"." +
							aData[3]+"." +
							aData[4]+"." +
							aData[5]+"." +
							aData[6]+"." +
							aData[7]+"." +
							aData[8]+"." +
							aData[9]+"." +
							aData[10]+"." +
							aData[11]+"." +
							aData[12]+"." +
							aData[13];
							
				var sCI = 	aData[14]+"." +aData[15];	
			
				//Pone los valores que identifican a la EP en hiddens
				$("#nIdClaveEgresos").val(sEP);
				$("#ClaveInterna").val(sCI);
				sEP += "." + sCI;
								
				var szWhere = " EP = '" + sEP + "' ";	
				var tbla="VSALDOSANUALESFORMAT";
				if($("#cuentaDisponible").val()=="82109"){
					tbla="VSALDOSANUALESFORMATCTADISP";
				}
				//Consulta los saldos y agrega una fila con dicha info en tblSaldos                                                                                      
				$.getJSON(
					"../../catalogos/SelectJson.jsp",
					{Tabla: tbla, 
						Param: szWhere,
						MaxReg: 10, 
						ajax: 'false'}, 
					function(j, status){
	   					for (var i = 0; i < j.length; i++) 
	   						$('#tblSaldos').dataTable().fnAddData(
	   						[j[i].Col4,
	   						 j[i].Col5,
	   						 j[i].Col6,
	   						 j[i].Col7,
	   						 j[i].Col8,
	   						 j[i].Col9,
	   						 j[i].Col10,
	   						 j[i].Col11,
	   						 j[i].Col12,
	   						 (j[i].Col13),
	   						 (j[i].Col14),
	   						 (j[i].Col15),
	   						 (j[i].Col16) ]);
						
						//Puntero a tr con saldos de tabla saldos
						dtSaldosRow = $("#tblSaldos_wrapper").find("div.dataTables_scrollBody").find("tbody>tr");
	         		}
	         	);
			}
			$(this).removeClass('row_selected');
		});
	}
	
	 function initDTClaves() {	
		return oTableLineas = $("#dt_clavepresup").dataTable({
			sScrollX: "100%",
			sScrollXInner: "100%",
			bScrollCollapse: true,
			bScrollAutoCss: true,
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + 
				"/" + window.location.pathname.split("/")[1] + 
   				  "/crud?rt=t&ql=fn_mProcedimientoEp('" + $("#cIdProcedimiento").val()+ "',"+esAdmin+",'"+$("#unidadUsuarioLogeado").val()+"',"+$("#documentoAplicadoProcedimiento").val()
					+ ")",  
			bProcessing: true,
			//iDisplayLength: 10,
			//sPaginationType: "full_numbers",
			bJQueryUI: true,
			bPaginate: true,
			bLengthChange: false,
			bFilter: false,
			//bSort: false, 
			bInfo: false,
	        //bAutoWidth: false,					
			sPaginationType: "full_numbers",
			bAutoWidth: false,
					
			aaSorting: [[ 0, "asc" ]],
			aoColumns: [
				{ sName: "nIdClaveEgresos" },
				{ sName: "ClaveInterna" },
				{sName: "eliminaClave",  bVisible: true}
				]
			
	  });
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
	function deshabilitarCampos() {
		document.getElementById("desConsol").setAttribute("disabled", "disabled");
		document.getElementById("notasConsol").setAttribute("disabled", "disabled");
		document.getElementById("btnGuardarConsolCaratula").setAttribute("disabled", "disabled");
	}
					
	function textCounter( field, maxlimit ) {
		if ( field.value.length > maxlimit )
			field.value = field.value.substring( 0, maxlimit );
	}
			
	function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
			patron =/[\w\d\s\\.\/\_\ñ\Ñ]/;					
			te = String.fromCharCode(tecla);																				
			var v = document.getElementById('desConsol').value.replace("ñ", "n").replace("Ñ", "N");					
			document.getElementById('desConsol').value = v;
			return patron.test(te);
	}
	
	function muestraInformacion(){
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
		   queryFormPost("mValidaPrecompromiso",{async:false});
			if($("#nIdEstado").val()==1){
				 queryFormPost("apartadoConsolidado",{async:false});
			}else{
				  if ($("#documentoAplicado").val()==0){
				 	 queryFormPost("sumPrecomProcedimiento",{async:false});
				 }else{
				 	 queryFormPost("apartadoConsolidado",{async:false});
				 }
			}
			
		   queryFormPost("fn_mConsolidadoCalculaMontoConIVARead",{async:false});
		   queryFormPost("montoProcedimiento",{async:false});
		   
		   $("#mImporteTotal").formatCurrency();	
		   if($("#cIdTipoProcedimiento").val()=='PN' || $("#cIdTipoProcedimiento").val()=='PS'){
				if($("#cProcedimientoCumple").val()==1){ // cumple requisitos
					if($("#tipoProceso").val()==1){ // proceso corto
						$("#CotizacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").attr("disabled", false);
						$("#EvaluacionProcedimiento").css("display", "none");
					} 
					if($("#tipoProceso").val()==2){ // proceso largo
						$("#EvaluacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").attr("disabled", false);
					}
				}else{
					if($("#tipoProceso").val()==1){
						$("#CotizacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").attr("disabled", true);
						$("#EvaluacionProcedimiento").css("display", "none");
					}
					if($("#tipoProceso").val()==2){
						$("#EvaluacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").attr("disabled", true);
					}
				}		
			}
			
			//si existe un precompromiso en consolidado,se agregan las eps a la tabla de procedimientoEp
			if($("#documentoAplicado").val()==1 && $("#nIdEstado").val()==1 ){				
				queryFormPost("insertEpConsolidado", {async:false});
			}
			
			queryFormPost("mSolicitudDescripcionUnidad", { async:false });
	}

	function habilitarPestanas(){
		if($("#tipoProceso").val()==1){ //proceso corto
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#CotizacionProcedimiento" ).css("display", "block");
			if(esRegularizacion()){// no trae precompromiso
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");	
			}else{ // trae precompromiso
				if ($("#nIdEstado").val()==1){ 
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado
						$("#presupuestoProcedimiento" ).css("display", "block");
						$("#precompromisoProcedimiento" ).css("display", "block");	
					}else{
						//ya trae precompromiso, si no cubre el monto del consolidado mostrar las pestañas de precom y presupuesto			
						if(cubreMonto()){
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block");
						}else{
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
							
						}
					}
				}else{
					/* $("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none"); */
					
					if($("#nIdEstado").val()==2){
						queryFormPost("validaFlujoProcedimiento", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT"){
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block");
							/* document.getElementById('trBotones').style.display = 'none';
							document.getElementById('trSalir').style.display = 'block'; */
						}else{
							/* document.getElementById('trBotones').style.display = 'block';
							document.getElementById('trSalir').style.display = 'none'; */
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
						}
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}
			}			
		}else{// proceso completo
			$("#EvaluacionProcedimiento" ).css("display", "block");
			$("#PreguntasProcedimiento" ).css("display", "block");
			$("#ArchivosProcedimiento" ).css("display", "block");
			
			$("#CotizacionProcedimiento" ).css("display", "none");
			
			if(esRegularizacion()){// no trae precompromiso
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");	
			}else{
				if ($("#nIdEstado").val()==1){
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado 
						$("#presupuestoProcedimiento" ).css("display", "block");
						$("#precompromisoProcedimiento" ).css("display", "block");	
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}else{
					/* $("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none"); */
						if($("#nIdEstado").val()==2){
						queryFormPost("validaFlujoProcedimiento", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"|| $("#cEventoFlujo").val()=="PRECOM_MAT"){
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block");
							document.getElementById('trBotones').style.display = 'none';
							document.getElementById('trSalir').style.display = 'block';
						}else{
							document.getElementById('trBotones').style.display = 'block';
							document.getElementById('trSalir').style.display = 'none';
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
						}
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}
			}
		}
		if($("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PS" ){
			$("#RequisitosProcedimiento" ).css("display", "block");
		}else{
			$("#RequisitosProcedimiento" ).css("display", "none");
		}
		//valida que exista un precompromiso en procedimiento,muestra la pestaña de ampliacion de vigencias
		if($("#nIdEstado").val()==2){
			queryFormPost("existePrecompromiso",{async:false});
			if($("#existePrecompromiso").val()==1){
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
			}else{
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			}
		}else{
			$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
		}
		
	}
	
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<quitaFmt($("#MontoConIVA").val())){			
			return true;
		}else{
			return false;
		}
		
	}
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "PC", "PO", "PS", "PA", "PT" ];
		if ($.inArray($("#cIdTipoProcedimiento").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	
	function validaModificarProcedimiento(){ 
		if(($('#cIdUsuarioCreacion').val()== $("#U_LOGIN").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	
	function eliminaClave(cIdProcedimiento, nIdClaveEgresos){
		$("#nIdClaveEgresos").val(nIdClaveEgresos);
		queryFormPost("eliminaClaveEp", {async:false});
		initDTClaves();
	}

	 function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" ||$("#nIdEstadoProcedimiento").val()=="DESIERTO  " )
			return;	
			
			if (confirm("¿Desea declarar desierto el Procedimiento " + $("#cIdProcedimiento").val())) {
				$("#EstadoCaptura").val('3'); 
				queryFormPost("sp_ProcedimientoDesierto",{async:false});
				actualizaDatosProcedimientoDesierto();
			}else{
				return;
			}
		}
	 }
	 
	 function actualizaDatosProcedimientoDesierto(){
		$("#nIdEstadoProcedimiento").val("DESIERTO");

		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    
    }
  
  	function quitaFmt( val ) {
	  	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	
	function fnFilterColumn (evt){				
		var value, myWhere, token;
		var column_name = [
			"EjercicioFiscal",
			"Ramo",
			"UnidadResponsable",
			"GrupoFuncional",
			"Funcion",
			"SubFuncion",
			"ProgramaGeneral",
			"ActividadInstitucional",
			"ProgramaPresupuestario",
			"Partida",
			"TipoGasto",
			"FuenteFinanciamiento",
			"EntidadFederativa",
			"Cartera",
			"UnidadEjecutora",
			"UnidadNorativa"
		];
		myWhere = "";
		token = "&qw=";
		var charCode = evt.which ? evt.which : window.event.keyCode;
		if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
			if (charCode <= 13) return true;
				// Es teclado numerico?
			if (charCode < 96 || charCode > 106) { 
				var keyChar = String.fromCharCode(charCode);
				var re = /[a-zA-Z0-9.]/;
				if (!re.test(keyChar)) 
					return true;
			}
		}
		for (var i = 1; i < 17; i++) {
			value = $("#col"+i+"_filter" ).val();
			if (value !== "") {
				myWhere += token + column_name[i - 1] + " LIKE '%25" + value + "%25'";
				token = " AND ";
			}
		}
		//oTable = createDataTable(myWhere);
		epDT = initDTEPs(myWhere);
		return true;
	}
	function muestraDispRadicado(){
		if (!confirm('Si desea activar o desactivar el check se perderan las ep´s agregadas, ¿desea continuar?')) {
			activacheck();
			return;
		}
		queryFormPost("eliminaClaveEps",{async:false});
		$("#cuentaDisponible").val('82106');	
		if($('#checkDispRadicado').is(':checked')){
			$("#cuentaDisponible").val('82109');
		}
		queryFormPost("mUpdateProcedimientoCuentaDisp",{async:false});
		epDT = initDTEPs("");
		$('#tblSaldos').dataTable().fnClearTable();
		clavesDt=initDTClaves();
		$("#nIdClaveEgresos").val('');
		$("#ClaveInterna").val('');
	}
	function activacheck(){
		$("#checkDispRadicado").attr("checked",false);
		if($("#cuentaDisponible").val()=='82109'){
			$("#checkDispRadicado").attr("checked",true);
		}
	}
	</script>
	</head>
	<body id="dt_example" >
		<form>
		<fieldset>
			<legend>Informaci&oacute;n del Procedimiento</legend>
				<table border="0" align="center" width="100%">
					<tr>							
						<td align="right" colspan="2"  >
						<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
						<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="window.location = 'Procedimiento-copia.jsp?tab=1&ses=1';"/>Salir
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2">
				    		<input type="text" style="width: 25px;border-width:0; background-color:transparent;" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly value="<%=unidadUsuarioLogeado%>"/>
				    		<input type="text" style="width: 690px;border-width:0; background-color:transparent" id="lblDescUsuario" name="lblDescUsuario" readonly />
				    	</td>
			    	</tr>
					<tr align="left">
						<td colspan="2">
							[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="8" style="border-width:0; background-color:transparent;" readonly="readonly"/>]]&nbsp;
							<input name="desProcedimiento" id="desProcedimiento" type="text" maxlength="150" style="border-width:0; background-color:transparent; width: 40em;" readonly="readonly"></input>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border-width:0; background-color:transparent; width: 30em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
							<input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text" readonly="readonly" style="border-width:0; background-color:transparent; width: 45em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Consolidado: <input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input> 
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Tipo de Proceso: <input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border-width:0; background-color:transparent" readonly="readonly"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
								Monto Precomprometido: <input name="lblApartado" id="lblApartado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input>
						</td>								
					</tr>
					<tr>
						<td align="left" style="color: black " colspan="2">																						  
							Total Consolidado con IVA: <input name="mImporteTotal" id="mImporteTotal"  readonly style="border-width:0; background-color:transparent;">										
						</td>
					</tr>
					<tr>
						<td colspan="2" align="left">
					 		<div id="tablaFechasProcedimientoCaratula" style="width:100%;">
								<!-- Carga las fechas -->
							</div>
						</td>
					</tr>
					<tr>
   						<td align="left"  colspan="2">
   						<label>Disponible Radicado:</label> <input type="checkbox" name="checkDispRadicado" id="checkDispRadicado" onclick="muestraDispRadicado()" />
   						</td>
   					</tr>								
					<tr>
						<td>
					 	<br/>
						</td>
					</tr>
					   
 			</table>
		</fieldset>
			<table border="0" width="100%">
				<tr>
					<td>
						
						<input id="U_LOGIN" name="U_LOGIN"  type="hidden" size="10">
						<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">					
						<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" type="hidden" size="10">
						<input type="hidden" name="BanderaRadicado" id="BanderaRadicado" value="N" />
						
						<input id="Categoria" name="Categoria" type="hidden" size="4" >
						<input id="Activo" name="Activo" type="hidden" size="4" >
						<input id="esServicio" name="esServicio" type="hidden" size="4" >
						
						<input id="EstadoCaptura" name="EstadoCaptura" type="hidden" size="10">
						<input id="cProcedimientoCumple" name="cProcedimientoCumple" value=""  type="hidden" size="10">
						<input id="tieneProveedor" name="tieneProveedor" type="hidden" size="10">
						<input id="imagenEstadoProcedimiento" name="imagenEstadoProcedimiento" value=""  type="hidden" size="10">
						
						<input id="TipoConsolidado" name="TipoConsolidado" type="hidden" size="10">
						<input id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" type="hidden" size="10">
						<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
						
						
						<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="10">
						
						<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="10">
						<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
						<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">  
						<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
						
						<input type="hidden" id="Partida" name="Partida" value="2" />
						<input type="hidden" id="Partida2" name="Partida2" value="3" />
						<input type="hidden" id="Partida3" name="Partida3" value="5" />
						<input type="hidden" id="usuarioUEReport" name="usuarioUEReport"/>
						
						<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" />
						<input type="hidden" id="ClaveInterna" name="ClaveInterna" />
						<input type="hidden" id="existeEp" name="existeEp" value="0" />
						<input id="cboCategoria" name="cboCategoria" type="hidden" size="10">
						<input id="descripcionCaratula" name="descripcionCaratula" type="hidden" size="10">
						<input id="numero_externoCaratula" name="numero_externoCaratula" type="hidden" size="10">
						<input id="Activo" name="Activo" type="hidden" size="10">
						<input id="ivaProcedimiento" name="ivaProcedimiento" value=""  type="hidden" size="10">
						
						<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
						<input type="hidden" id="unidadUsuarioLogeado" name="unidadUsuarioLogeado" value="<%=unidadUsuarioLogeado%>" />
						<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
						<input id="documentoAplicadoProcedimiento" name="documentoAplicadoProcedimiento" type="hidden" size="10">
						
						 <!-- BITACORA -->
						<input id="cAccion" name="cAccion" type="hidden" size="10">
						<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="10">
						<input id="cIdUsuario" name="cIdUsuario" type="hidden" size="10">
						<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
						<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
						<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
						<input type="hidden" name="isPlurianual" id="isPlurianual" value="0"/>
						<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
						<input id="nIdconsecutivoAdj" name="nIdconsecutivoAdj" type="hidden" value="-1">
					</td>
				</tr>
				<tr>
					<td>
						<fieldset style="width: 750px" align="left">
						<h1 align="left">Estructura Programática</h1>
						<table id="tblEP" class="display"    class="display" width="740px" align="left">
				            <thead>
				                <tr>
							      <th>EF</th>
							      <th>Ra</th>
							      <th>UR</th>
							      <th>GF</th>
							      <th>Fu</th>
							      <th>SF</th>
							      <th>PG</th>
							      <th>AI</th>
							      <th>PP</th>
							      <th>Pa</th>
							      <th>TG</th>
							      <th>FF</th>
							      <th>EF</th>
							      <th>Ca</th>
							      <th>UE</th>
							      <th>UN</th>
				                </tr>
				            </thead>
			            	<tfoot>
								<tr>
									<th><input type="text" name="col1_filter" id="col1_filter"  /></th>
									<th><input type="text" name="col2_filter" id="col2_filter"   /></th>
									<th><input type="text" name="col3_filter" id="col3_filter"   /></th>
									<th><input type="text" name="col4_filter" id="col4_filter"   /></th>
									<th><input type="text" name="col5_filter" id="col5_filter"   /></th>
									<th><input type="text" name="col6_filter" id="col6_filter"   /></th>
									<th><input type="text" name="col7_filter" id="col7_filter"   /></th>
									<th><input type="text" name="col8_filter" id="col8_filter"    /></th>
									<th><input type="text" name="col9_filter" id="col9_filter"   /></th>
									<th><input type="text" name="col10_filter" id="col10_filter"   /></th>
									<th><input type="text" name="col11_filter" id="col11_filter"   /></th>
									<th><input type="text" name="col12_filter" id="col12_filter"   /></th>
									<th><input type="text" name="col13_filter" id="col13_filter"   /></th>
									<th><input type="text" name="col14_filter" id="col14_filter"   /></th>
									<th><input type="text" name="col15_filter" id="col15_filter" /></th>
									<th><input type="text" name="col16_filter" id="col16_filter"  /></th>
								</tr>
							</tfoot>
			        	</table>
			        	<br />
			        	<table id="tblSaldos" class="display" width="740px" align="left">
				            <thead>
				                <tr>
				                	<th>Enero</th>
				                    <th>Febrero</th>
				                    <th>Marzo</th>
				                    <th>Abril</th>
				                    <th>Mayo</th>
				                    <th>Junio</th>
				                    <th>Julio</th>
				                    <th>Agosto</th>
				                    <th>Septiembre</th>
				                    <th>Octubre</th>
				                    <th>Noviembre</th>
				                    <th>Diciembre</th>
				                    <th>Anual</th>
				                </tr>
				            </thead>
				        </table>
					    <br />
					    <input type="button" id="btnAgregar" name="btnAgregar" value="Agregar"  class="btnInterfaceBG ui-button ui-corner-all" />
			        	<h1 align="left">Claves seleccionadas</h1>
			        	<table  id="dt_clavepresup" class="display">
							<thead>
								<tr align="left">
									<th>C&oacute;digo SAI</th> 
									<th>Clave SHCP</th>
									<th>Elimina</th>  
							 	</tr> 
							</thead>
								<tbody>
								</tbody> 
						</table>
				        <br />
					    
					</fieldset>
					</td>
				</tr>
			</table>	
		</form>				
	</body>
</html>
