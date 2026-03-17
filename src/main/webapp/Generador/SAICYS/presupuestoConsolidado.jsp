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
	String cIdTipoConsolidado = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		cIdTipoConsolidado = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
	}
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
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
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" charset="utf-8">
		var epDT;
		var Pp, Pp2, Pp3;
		var oCurrentFocus;
		var timeouts = [];
		var clavesDT;
		var esAdmin;
		$(document).ready(function() {
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
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
		$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
		queryFormPost("mConsolidadoCaratulaRead",{async : false});	
		queryFormPost("mConsolidadoRead", {async : false});	
		queryFormPost("fn_mConsolidadoCalculaMontoRead", {async : false});
		queryFormPost("fn_mConsolidadoCalculaMontoBrutoRead", {async : false});	
		queryFormPost("fn_mConsolidadoTieneProcedimientoRead", {async : false});
		queryFormPost("numPartidasConsolidadasRead",{async : false});
		queryFormPost("mValidaPrecompromiso",{async:false});
		queryFormPost("getcIdUsuarioCreacionConsolidado",{async:false});
		
		habilitaPestanas();
				
		document.getElementById("lblConsolidado").style.readonly=true;
		document.getElementById("lblUnidadEjecutora").style.readonly=true;
		document.getElementById("lblDescripcion").style.readonly=true;
		document.getElementById("lblEstado").style.readonly=true;
		
		if($("#tieneProcedimiento").val() == '' ||$("#tieneProcedimiento").val() == 0)
			$("#tieneProcedimiento").val('N/A');
		if($("#tienePedidos").val() == '')
			$("#tienePedidos").val('0');							
		$("#montoTotal").formatCurrency();
		$("#MontoBruto").formatCurrency();		
		$("#cIdConsolidadof").val($("#cIdConsolidado").val());
		$("#cEstadof").val($("#cEstado").val());
		
		querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
		
		queryFormPost("mConsolidado_LabelRead", { async:false });					
		queryFormPost("mConsolidado_EstadoConsolidadoRead", {async:false});
		queryFormPost("cg_roleRead", { async:false });
		queryFormPost("mSolicitudDescripcionUnidad", { async:false });
		
		/* queryFormPost("mValidaPrecompromiso",{async:false});
		if($("#documentoAplicado").val()==0){ // precompromiso cancelado
			$(".botones").attr("disabled", false);
		}else{
			$(".botones").attr("disabled", true);
		} */
		epDT = initDTEPs("");
		initDTSaldos();
		clavesDt=initDTClaves();
		$("#btnAgregar").button().click(function(){
		//valida que no exista la ep
			queryFormPost("existeEpConsolidado", {async:false});
			if($("#existeEp").val()==1){
				alert("La clave presupuestal que intenta agregar ya se encuentra registrada");
				$("#existeEp").val("");
				return;
			}else{
				queryFormPost("insertaConsolidadoEp",{async:false});
				clavesDt=initDTClaves();
			}	
			
		});
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
				if (!re.test(keyChar)) return true;
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
	function initDTEPs(w){
	    //para filtrar dependiendo de la unidad ejecutora			

		var where="((substring(Partida,1,1)='" + Pp + 
			"') OR (substring(Partida,1,1)='" + Pp2 +
			"') OR (substring(Partida,1,1)='" + Pp3 + "'))" + w;
    	
    	
	 /*    if( $('#R_NOMBRE').val()=='ADMIN_RECMAT'){ // si es admin_recmat podra ver las claves de todas las ue y de todas las partidas
	       	where+="";
	       	esAdmin=1;
	    }else{  */
	    	if ($("#U_LOGIN").val()==$("#cIdUsuarioCreacion").val() || $('#R_NOMBRE').val()=='ADMIN_RECMAT'){
	    	/* 	where+=" AND UnidadEjecutora in (select distinct cIdUnidadEjecutoraSolicitud from mConsolidadoSolicitud where cIdConsolidado='"+$("#cIdConsolidado").val()+"')";
	    		where+=" AND Partida in (SELECT b.cIdSubPartida FROM fn_mConsolidadoPartidas('"+$("#cIdConsolidado").val()+"') a inner join mCatalogoCABM b on a.cIdCABM=b.cIdCABM)"; */
	    		where+= "and unidadPartida  in  (SELECT distinct a.unidadPartida FROM fn_mConsolidadoPartidasPrecom('"+$("#cIdConsolidado").val()+"') a )";
	    		esAdmin=1;
	    	}else{
	    		where+=" AND UnidadEjecutora='"+$("#unidadUsuarioLogeado").val()+"'";
	    		//where+=" AND Partida in (SELECT b.cIdSubPartida FROM fn_mConsolidadoPartidas('"+$("#cIdConsolidado").val()+"') a inner join mCatalogoCABM b on a.cIdCABM=b.cIdCABM)";
	    		where +="AND Partida in ( SELECT distinct b.cIdSubPartida FROM fn_mConsolidadoPartidas('"+$("#cIdConsolidado").val()+"') a "+
 					    " inner join mCatalogoCABM b with(nolock) on a.cIdCABM=b.cIdCABM "+
 					    " inner join mConsolidadoSolicitud d with(nolock) on a.nIdLineaConsolidado=d.nIdLineaConsolidado "+
 					    " where   d.cIdUnidadEjecutoraSolicitud='A04' and d.cIdConsolidado='"+$("#cIdConsolidado").val()+"')";
	    		esAdmin=2;
	    	}
			
			//where+=" AND Partida in (SELECT b.cIdSubPartida FROM fn_mConsolidadoPartidas('"+$("#cIdConsolidado").val()+"') a inner join mCatalogoCABM b on a.cIdCABM=b.cIdCABM)";
	//	}
		return $("#tblEP").dataTable({
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
			sScrollX: "100",
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPGridPrecom&qw="+where,
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aoColumns: [
				{ sName: "EjercicioFiscal"},
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
		$("#tblEP tbody").click(function(event) {
			
			//Deselecciona todos los seleccionados
			$(epDT.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			
			//Vuelve a selecionar el ultimo seleccionado
			$(event.target.parentNode).addClass('row_selected');
			
			
			
			//Limpia la tabla de saldos
			$('#tblSaldos').dataTable().fnClearTable();
			
			//Construye la cadena EP para consultar los disponibles
			var anSelected = fnGetSelected( epDT );
			var sEP = 	anSelected[0].innerText.substr(0,4)+"." +
						anSelected[0].innerText.substr(4,2)+"." +
						anSelected[0].innerText.substr(6,3)+"." +
						anSelected[0].innerText.substr(9,1)+"." +
						anSelected[0].innerText.substr(10,1)+"." +
						anSelected[0].innerText.substr(11,2)+"." +
						anSelected[0].innerText.substr(13,2)+"." +
						anSelected[0].innerText.substr(15,3)+"." +
						anSelected[0].innerText.substr(18,4)+"." +
						anSelected[0].innerText.substr(22,5)+"." +
						anSelected[0].innerText.substr(27,1)+"." +
						anSelected[0].innerText.substr(28,1)+"." +
						anSelected[0].innerText.substr(29,2)+"." +
						anSelected[0].innerText.substr(31,11);
						
			var sCI = anSelected[0].innerText.substr(42,3)+"." +
						anSelected[0].innerText.substr(45,3);
						
			//Pone los valores que identifican a la EP en hiddens
			$("#nIdClaveEgresos").val(sEP);
			$("#ClaveInterna").val(sCI);
			sEP += "." + sCI;
			var exWhere = " EP = '" + sEP + "' ";

			//Consulta los saldos y agrega una fila con dicha info en tblSaldos                                                                                      
			$.getJSON(
				"../../catalogos/SelectJson.jsp",
				{Tabla: "VSALDOSANUALESFORMAT", 
					Param: exWhere,
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
				"/crud?rt=t&ql=fn_mConsolidadoEp('" + $("#cIdConsolidado").val()+ "',"+esAdmin+",'"+$("#unidadUsuarioLogeado").val()+"',"+$("#documentoAplicado").val()+")",  
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
				{sName: "eliminaClave", bVisible:false}
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

	function habilitaPestanas(){
		queryFormPost("mValidaPrecompromiso",{async:false});
		if(esRegularizacion()){
			$("#presupuestoConsolidado").css("display", "none");
			$("#preCompromisoConsolidado").css("display", "none");	
			$("#ampliacionPrecompromiso").css("display", "none");
			$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
		}else{
			queryFormPost("fn_mConsolidadoTieneProcedimientoRead", {async : false});
			if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
				$("#tieneProcedimiento").val('0');
			if ($("#nIdEstado").val()==2 && $("#tieneProcedimiento").val()==0 ){ // validar que el consolidado este aprobado pero que no tenga un procedimiento
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#presupuestoConsolidado").css("display", "block");
					$("#preCompromisoConsolidado").css("display", "block");		
				}else{ //aplicado contablemente
					// si el precompromiso aplicado cubre el monto del consolidado ya no se muestran las pestañas de presupuesto
					alert(cubreMonto());
					if (cubreMonto()){ // no lo cubre, se muestran las pestañas
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
						$("#presupuestoConsolidado").css("display", "block");
						$("#preCompromisoConsolidado").css("display", "block");	
					}else{ // se cubre el monto del consolidado con el del precompromiso
						// si el precompromiso se aprobo del disponible, mostrar las pestañas a manera de consulta
						$("#cEventoFlujo").val("");
						queryFormPost("validaFlujo", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
							$("#ampliacionPrecompromiso").css("display", "block");
							$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
							$("#presupuestoConsolidado").css("display", "block");
							$("#preCompromisoConsolidado").css("display", "block");
						}else{
							$("#ampliacionPrecompromiso").css("display", "block");
							$("#presupuestoConsolidado").css("display", "none");
							$("#preCompromisoConsolidado").css("display", "none");
							$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
						}	
						
					}					
				}
			}else{ // esta aprobado y tiene procedimiento 
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");					
				}else{ //aplicado contablemente
					queryFormPost("validaFlujo", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "block");
						$("#preCompromisoConsolidado").css("display", "block");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					}else{
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "none");
						$("#preCompromisoConsolidado").css("display", "none");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					}
				}
			}
			if($("#nIdEstado").val()==1 ){
				$("#presupuestoConsolidado").css("display", "none");
				$("#preCompromisoConsolidado").css("display", "none");	
				$("#ampliacionPrecompromiso").css("display", "none");
				queryFormPost("numPartidasConsolidadasRead",{async : false});
				if($("#numLineasConsolidadas").val()!=0){
					$("#vigenciaRequisicionesConsolidadas").css("display", "block");	
				}else{
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
				}
			}
		}
	}
	
	function eliminaClave(cIdConsolidado, nIdClaveEgresos){
		$("#nIdClaveEgresos").val(nIdClaveEgresos);
		queryFormPost("eliminaClaveEpConsolidado", {async:false});
		initDTClaves();
	}
	
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "CC", "CO", "CS", "CA" ];
		if ($.inArray($("#cIdTipoConsolidado").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else{
			regularizacion=false;
		}
			
		return regularizacion;
	}
	
	function cubreMonto(){
			queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
			queryFormPost("apartadoConsolidado", {async: false});
			if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
				return true;
			}else{
				return false;
			}
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

		</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Información del Consolidado</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 25px" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly style="border-width:0; background-color:transparent" value="<%=unidadUsuarioLogeado%>"/><input type="text" style="width: 690px" id="lblDescUsuario" name="lblDescUsuario" readonly style="border-width:0; background-color:transparent"/>
				    	</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 80x" id="lblConsolidado" name="lblConsolidado" readonly style="border-width:0; background-color:transparent"/><input type="text" style="width: 600px" name="lblDescripcion" id="lblDescripcion" readonly style="border-width:0; background-color:transparent"/></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/>
			    		</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/>
			    		</td>
			    	</tr>
			    </table>
			</fieldset>
				<table border="0" width="100%">
					<tr>
						<td>
							<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
							<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4" value="<%= cIdTipoConsolidado %>">
							<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
							<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
							<input id="cEstado" name="cEstado" type="hidden" />
							<input type="hidden" name="BanderaRadicado" id="BanderaRadicado" value="N" />
							<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
							<input id="nIdEstado" name="nIdEstado" type="hidden" size="2">
							<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
							
							<input type="hidden" id="imgEstado" name="imgEstado"/>
							
							<%-- <input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
							<input type="hidden" name="cIdUnidadEjecutoraConsolidado" id="cIdUnidadEjecutoraConsolidado" value="<%=cIdUnidadEjecutora%>" /> --%>
							<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
							
							<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
							<input name="cIdConsolidado" id="cIdConsolidado" type="hidden"/>
							<input id="nIdEstadoCon" name="nIdEstadoCon" type="hidden" size="2"/>
							<input name="numLinTot" id="numLinTot" type="hidden">
							<input name="cAccion" id="cAccion" type="hidden">
							<input name="cIdDocumento" id="cIdDocumento" type="hidden">
							<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
							<input name="nFolioPrecompromiso" id="nFolioPrecompromiso" type="hidden">
							<input name="consecutivoPrecompromiso" id="consecutivoPrecompromiso" type="hidden"/>
							
							<input type="hidden" id="Partida" name="Partida" value="2" />
							<input type="hidden" id="Partida2" name="Partida2" value="3" />
							<input type="hidden" id="Partida3" name="Partida3" value="5" />
							
							<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" />
							<input type="hidden" id="ClaveInterna" name="ClaveInterna" />
							<input type="hidden" id="cIdEntidadContable" name="cIdEntidadContable" />
							<input type="hidden" id="unidadUsuarioLogeado" name="unidadUsuarioLogeado" value="<%=unidadUsuarioLogeado%>" />
							<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" />
							<input type="hidden" id="existeEp" name="existeEp" />
							<!-- habilita pestanas -->
							<input name="documentoAplicado" id="documentoAplicado" type="hidden"/>
							<input type="hidden" id="cEventoFlujo" name="cEventoFlujo">
							<input type="hidden" id="tieneProcedimiento" name="tieneProcedimiento">
							
							<input type="hidden" id="cIMontoConIVAdEntidadContableCreate" name="cIdEntidadContableCreate">
							<input type="hidden" name="lblApartado" id="lblApartado" />
							<input type="hidden" name="MontoConIVA" id="MontoConIVA" />       	
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
						    <br /><button id="btnAgregar" class="botones">Agregar</button>
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
