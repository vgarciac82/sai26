<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero

	String roles = "";
	Map rol = null;
	if (usuario != null) {
		rol = usuario.getRoles();
	}

	String cEjercicio = "";
	String cIdTipoProcedimiento = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio = (String) session
				.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora = (String) session
				.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento = (String) session
				.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo = (String) session
				.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	String unidadUsuarioLogeado = "";
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
		var cIdProcedimiento;
		var vcaNoCompromiso;
		$(document).ready(function() {
			$("#tbs").val(11);
			showAndHideTabs();
		<%NegativaPestana NegPestana = new NegativaPestana();
			//NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			//botones

			NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic(
					"jdbc/gestion");
			///// botones/////
			int imgAdjudicarP = 0;
			int imgDesiertoP = 0;
			int imgDevolverP = 0;
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry) it1.next();
				roles += r.getKey().toString() + ",";
			}
			if (roles.length() > 0) {
				roles = roles.substring(0, roles.length() - 1);
			}
			Map botones = nb.getBotones(roles, "Procedimiento",
					"PrecompromisoProcedimiento");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry) btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%String img = (String) b.getValue();
				if ("imgAdjudicarProveedoresProc".equals(img)) {
					imgAdjudicarP = 1;
				}
				if ("imgDesiertoProveedoresProc".equals(img)) {
					imgDesiertoP = 1;
				}
				if ("imgDevolverProveedoresProc".equals(img)) {
					imgDevolverP = 1;
				}

			}%>	
		
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cCentroContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuario").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		
		cIdProcedimiento= $("#cIdTipoProcedimiento").val()+'-'+$("#cIdUnidadEjecutora").val()+'-'+$("#nIdConsecutivo").val();
		muestraInformacion();
		initTables();
		cargaPreCompromisoVacio();

		habilitarPestanas();
		cargaSuficiencias();
		deshabilitaMeses($("#dt_preCompromiso").dataTable());
		deshabilitaMeses($("#dt_suficiencia").dataTable());
		$("#mImporteTotal").formatCurrency();	
		$("#difPrecompromiso").formatCurrency();
		queryFormPost("mValidaPrecompromisoProcedimiento",{async:false});
		if ($("#documentoAplicadoProcedimiento").val()!=0){
			document.getElementById("imgAprobarpreCompromiso").disabled = true;
			document.getElementById("imgDesiertoProcedimiento").disabled = true;	
		}
	});
	
	function muestraInformacion (){
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   queryFormPost("esActivoProcedimiento",{async:false});
			//// tipo de poliza
		   queryFormPost("TipoPolizaRead", {async: false});
		   //montos totales del procedimiento
		   queryFormPost("montoProcedimiento",{async:false});
			queryFormPost("mValidaPrecompromiso",{async:false});
		   queryInnerDivPost("llenaFechasProcedimiento",{async:false});
		   queryFormPost("fn_mConsolidadoCalculaMontoConIVARead",{async:false});
		   queryFormPost("vigenciaVentanillaPrecompromiso",{async:false});
		   var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]);
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
		//	queryFormPost("apartadoConsolidado", {async: false});
			if($("#nIdEstado").val()==1){
				 queryFormPost("apartadoConsolidado",{async:false});
			}else{
				 queryFormPost("sumPrecomProcedimiento",{async:false});
			}
			//Lee la configuración de mes
			queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
			var offset=parseInt($("#mesDisponible").val(),10)+parseInt($("#GP_VALOR").val(),10);
		
			if(offset>12 || offset <0){
				alert("El desfase del calendario está mal configurado, favor de avisar al administrador");
				window.location ="Procedimiento-copia.jsp?tab=0";
			}else{
				$("#mesDisponible").val(offset);
			}
			
			// si el consolidado tiene un precompromiso
			if($("#documentoAplicado").val()==1){
				cargaPrecompromiso($("#lblApartado").val());
			}
			
			
	}

	
		
	function validaBotones(boton){
		var imgAdjudicarP='<%=imgAdjudicarP%>';
		var imgDesiertoP='<%=imgDesiertoP%>';
		var imgDevolverP='<%=imgDevolverP%>';
			switch (boton){
			case "imgAdjudicarProveedoresProc":
				if (imgAdjudicarP==0){
					adjudicarProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDesiertoProveedoresProc":
				if (imgDesiertoP==0){
					desiertoProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDevolverProveedoresProc": 
				if (imgDevolverP==0){
					devolverProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;			
			}/// FIN SWITCH
	} 
		
	function validaModificarProcedimiento(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	
	function cargaSuficiencias(){
		var oTable=$('#dt_suficiencia').dataTable( {
			bPaginate: false,
   			bLengthChange: false,
   			bFilter: false,
   			bInfo: false,
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
			}},
			bAutoWidth: true,
			sScrollX: 200,
			sScrollY: 210,
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			//Query de Financiero, también se utiliza para Compromiso
// 			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleProcedimientoEP&qw=cIdProcedimiento='" + $("#cIdProcedimiento").val() 
// 				+ "' and cIdEntidadContable = '" + $("#cCentroContable").val() + "' and nCuentaP = '" + $("#cuentaDisponible").val() + "'",
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] 
				+ "/crud?rt=t&ql=fn_mDisponibleProcedimientoEP('" + $("#cIdProcedimiento").val() + "','"+$("#cCentroContable").val()+"','"+$("#cuentaDisponible").val()+"')",
			aoColumns: [
				{ sName: "ClaveSIAFF" ,bSortable: false},
				{ sName: "ClaveInterna",bSortable: false },
				{ sName: "MontoEnero" ,bSortable: false},
				{ sName: "MontoFebrero" ,bSortable: false},
				{ sName: "MontoMarzo" ,bSortable: false},
				{ sName: "MontoAbril",bSortable: false },
				{ sName: "MontoMayo" ,bSortable: false},
				{ sName: "MontoJunio",bSortable: false },
				{ sName: "MontoJulio" ,bSortable: false},
				{ sName: "MontoAgosto" ,bSortable: false},
				{ sName: "MontoSeptiembre" ,bSortable: false},
				{ sName: "MontoOctubre",bSortable: false },
				{ sName: "MontoNoviembre" ,bSortable: false},
				{ sName: "MontoDiciembre" ,bSortable: false},
				{ sName: "MontoAnual",bSortable: false }]
    	});
	}
	function cargaPreCompromisoVacio(){
		var oTable=$("#dt_preCompromiso").dataTable( {
				bPaginate: false,
      			bLengthChange: false,
      			bFilter: false,
      			bInfo: false,
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
			bAutoWidth: true,
			sScrollX: 200,
			sScrollY: 200,
			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,  
			//Carga el calendario con valores de 0
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoMateriales('" + cIdProcedimiento + "')",
			//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + vcontrato + "'",
			aoColumns: [
				{ sName: "ClaveSIAFF",bSortable: false },
				{ sName: "ClaveInterna",bSortable: false },
				{ sName: "compromiso01",bSortable: false },
				{ sName: "compromiso02",bSortable: false },
				{ sName: "compromiso03",bSortable: false },
				{ sName: "compromiso04",bSortable: false },
				{ sName: "compromiso05",bSortable: false },
				{ sName: "compromiso06",bSortable: false },
				{ sName: "compromiso07",bSortable: false },
				{ sName: "compromiso08",bSortable: false },
				{ sName: "compromiso09",bSortable: false },
				{ sName: "compromiso10",bSortable: false },
				{ sName: "compromiso11",bSortable: false },
				{ sName: "compromiso12",bSortable: false },
				{ sName: "cIdProcedimiento",	bSearchable: false,	bSortable: false, bVisible: false  }, 
           		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
           		fnInitComplete: function(oSettings, json) {
           			queryFormPost("mValidaPrecompromisoProcedimiento", {async:false});
           			
           			if($("#documentoAplicadoProcedimiento").val()==0){
           				editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ;
           			}
           			
				}
        }) ;
	}
	function editPrecompromiso( oTableLocal ){
		//funcion para agregar los inputs a la tabla
		var i,j,descMes, nomMes,nColumna;
		var aData;
		var aTrs = oTableLocal.fnGetNodes();
		for (i=1 ; i<=aTrs.length ; i++ ){
			mes=parseInt($("#mesDisponible").val(),10);
			if(mes==0)
				mes=mes+1;
		    aData = oTableLocal.fnGetData(i-1);
		    nColumna=aData.length-mes-1;//dos columnas al final ocultas
		    for(j=2; j<nColumna; j++){
		    	nomMes='mes'+mes;
		    	descMes='mes'+mes+'-'+i;
		   		$("#dt_preCompromiso").children().children()[i].children[j].innerHTML = '<input style="width: 100%" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
		    	mes++;	
		    }
	    }
	}
	
	function cambiafrmt( fld )	{
	
	   	var vcompr = $("#mComprometido").val();
	   	var vfld = $("#" + fld.id).val();
	   	if (vfld == "")
	   		vfld = '0';
		vcompr = quitaFmt( vcompr );
		vfld=quitaFmt(vfld);
	   	$("#mComprometido").val( parseFloat(vcompr) + parseFloat( vfld ) );
		$("#" + fld.id).formatCurrency();
		$("#mComprometido").formatCurrency();
	}
	function Sinfrmt( fld )	{
		
	   	var valcol = fld.value ;
	   	var vcompr = $("#mComprometido").val();
	   	vcompr = quitaFmt( vcompr );
	   	valcol = quitaFmt( valcol );
		$("#" + fld.id).val( valcol );
	   	fld.select();
		$("#mComprometido").val( parseFloat( vcompr ) - parseFloat( valcol ) );
		$("#mComprometido").formatCurrency();
	}
	
	function deshabilitaMeses(oTableLocal){
		var i=$("#mesDisponible").val();
		i=parseInt(i,10);
		for(i; i>1;i--)
			oTableLocal.fnSetColumnVis(i,false);
	}
	
	function initTables(){
		$('#dt_preCompromiso').dataTable({
		    "bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": false, 
			"sScrollX": 100,         
			"sScrollY": 100,         
			"bJQueryUI": true,    
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"    
		} );
		$('#dt_suficiencia').dataTable({
		    "bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": false, 
			"sScrollX": 100,         
			"sScrollY": 100,         
			"bJQueryUI": true,    
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"    
		} );
		$('#dt_grabaprecompD').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false });
		$('#dt_grabaprecompD').attr('visible', false);
	}
	
	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true;
	}
	
	function valSufic( fld ) { 
		var valor = $("#" + fld.id).val();
		//validacion para que solo se permitan números
		valor=quitaFmt(valor);
		if(isNaN(valor)){
			alert("Ingrese solo valores numéricos");
			$("#" + fld.id).val( "0" );
			return false;
		}
		if(valor<0){
			alert("No se pueden ingresar valores negativos");
	    	$("#" + fld.id).val( "0" );
	    	return false;
		}
		//obtiene la suficiencia de la EP y el valor del precompromiso ingresado para comparar
		var oTableLocal = $('#dt_suficiencia').dataTable();
		var vimptot = $("#mImporteTotal").val();
		var vcompr  = $("#mComprometido").val();
		vimptot = quitaFmt( vimptot );
		vcompr = quitaFmt( vcompr );
		vcompT = parseFloat(vimptot)-parseFloat(vcompr);
		var nren = parseInt(fld.id.substring(fld.id.lastIndexOf("-") + 1),10) -1;
		var ncol = parseInt( fld.id.substring(5, 3),10 ) + 1 ;
		var aData = oTableLocal.fnGetData( nren );
		if (valor == "") {
			valor = '0';
			$("#" + fld.id).val( "0" );
		}
		var valsuf = parseFloat( aData[ ncol ] );
		if (parseFloat(valor) > valsuf) {
	    	alert("NO hay Suficicencia Mensual en la Clave Presupuestal");
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
	    vcompr=vcompr.toFixed(2);
		vcompr=parseFloat(vcompr); //0
		valor=valor.toFixed(2);
		valor=parseFloat(valor);// 200
		vimptot=parseFloat(vimptot);//116
		
		if ((parseFloat(vcompr+valor))>  parseFloat(vimptot)) {
	    	alert("El monto que desea precomprometer excede el monto total del procedimiento.\nMonto Capturado="+(parseFloat(vcompr+valor))+"\nMonto Procedimiento="+parseFloat(vimptot)); 
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompT = vimptot-valor-vcompr; //importe total-valoren ep-lo de otras ep´s
		//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
		vcompT=vcompT.toFixed(2);
		$("#difPrecompromiso").val(vcompT);
		$("#difPrecompromiso").formatCurrency();
	}
	
	function cargaPrecompromiso(val){
		var vimptot = $("#mImporteTotal").val();
		var vcompr  = $("#mComprometido").val();
		var totCompr=quitaFmt(vcompr)+quitaFmt(val);
		var difCompr=quitaFmt(vimptot)-quitaFmt(totCompr);
		//totCompr=totCompr.toFixed(2);
		$("#mComprometido").val(quitaFmt(totCompr));
		$("#mComprometido").formatCurrency();
		
		difCompr=difCompr.toFixed(2);
		$("#difPrecompromiso").val(quitaFmt(difCompr));
		$("#difPrecompromiso").formatCurrency();
	}
	function guardarPrecompromiso(){
	//leer Procedimiento Compranet 
		queryFormPost("readProcedimientoCNET", {async:false});
		if($("#cOficio").val()==""){
			alert ("Para Autorizar el Procedimiento, se debe capturar el No. de Procedimiento CNET de la caratula.");
			return;
		}else{
			if(validaModificarProcedimiento()){
				var res=window.confirm("Una vez realizado el precompromiso, el procedimiento sera adjudicado, ¿Desea continuar?");
				if(res){
					validaMontos();
				}else{
					return;
				}
				
			}
		}
	}
	
	function validaMontos(){
		var valido=true;
		var oTable = $("#dt_preCompromiso").dataTable();
		var aTrs = oTable.fnGetNodes();
		var cadena="";
		var modificacion=0;
		$.getJSON("../../catalogos/SelectJson.jsp?"+new Date().getTime() ,{Tabla: "MONTOS_U_PRECOM", Param: $("#cIdConsolidado").val(), SelectFunc: "", MaxReg:"" , ajax: 'false', async: false}, 
		function(j)
		{
				for(var i = 0; i < j.length; i++){
					var nColums = 12-parseInt($("#mesDisponible").val(),10);
					for ( var l=0 ; l < aTrs.length ; l++ ) { //numero de claves
						var aData = oTable.fnGetData( l );
						var jqInputs = $("input", aTrs[l] ); //input de la clave
						
						//Compara la UE que regrea la consulta con
						//if (aData[1].substring(0, 3) == j[i].Col1){
							//compara la partida que regresa la consulta con la elegida en las eps
							if(aData[0].substring(31, 36) == j[i].Col0){
								for (var m = 0 ; m <= nColums ; m++ ){
									if (quitaFmt(jqInputs[ m ].value) > 0 ){
										$("#formatCurrencyTem").val(parseInt(quitaFmt(jqInputs[ m ].value),10)+parseInt($("#formatCurrencyTem").val(),10));
										modificacion++;
									}
								}
							}
						//}
					}
					if($("#formatCurrencyTem").val()>0){
						if (parseInt($("#formatCurrencyTem").val(),10)>j[i].Col2){
							cadena=cadena+j[i].Col1+','+j[i].Col0+',';
							//alert("Se ha superado el límite de precompromiso en la unidad "+aData[1].substring(0, 3)+"");
							$("#formatCurrencyTem").val("0");
							valido=false;
						}else{
							$("#formatCurrencyTem").val("0");
						}
					}

				}
				
				if(cadena!=""){
					cadena=cadena.substring(0,cadena.length-1);
					var arreglo=cadena.split(',');
					var mensaje="Se ha superado el límite de precompromiso en la ";
					for(var y=0;y<arreglo.length; y=y+2){
						mensaje=mensaje+"Unidad: "+arreglo[y]+", Partida: "+arreglo[y+1];
					}
					alert(mensaje);
					valido=false;
				}
				if(modificacion<=0){
					valido=false;
					alert("No se han registrado los montos de precompromiso");
				}
					
					
				if (valido){
					document.getElementById("imgAprobarpreCompromiso").disabled = true;
					queryFormPost("mSaldosDelete", {async:false});
					/* if($("#mImporteTotal").val() != $("#mComprometido").val()){
						alert("El importe total es mayor al monto pre-comprometido");
						document.getElementById("imgAprobarpreCompromiso").disabled = false;
						return -1;
					} */
					//si existe un precompromiso en consolidado, se cancela y se genera un nuevo con los nuevos montos
					if($("#documentoAplicado").val()==1){
						//cancelamos el precompromiso generado en consolidado, se genera un nuevo en procedimiento
						preCompromiso('1');
					}else{
					 //genera precompromiso en procedimiento
					 preCompromiso('0');
					}
					
					$("#cAccion").val("PRECOMPROMETE_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				}
				
		});

	}
	function preCompromiso(val){
		var mensajeAp="";
	    var Aplica="0";
	    
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val()==""){
			$.ajax({url: '../../servlet/ProcedimientoServlet' , type:'post' , async: false,data:'operacion=1', dataType: 'json', success: guardaFolio});
		}
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val()==0){
		return -1;
		}	

		//Genera en una tabla auxiliar el detalle del precompromiso
		var vdocren = 1 ;
		//obtiene el número de filas de la tabla de precompromiso
		var nRows = $("#dt_preCompromiso tr").length -1 ;
		var oTable = $('#dt_preCompromiso').dataTable();
		var oTablD = $('#dt_grabaprecompD').dataTable();
		var aTrs = oTable.fnGetNodes();

		$("#cCentroContable").val( "<%=cCentroContable%>");
		//Guarda en una tabla auxiliar el encabezado del compromiso
		getNextSequenceVal({seqName: "PR-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
		
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 ) ;

		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(cIdProcedimiento);
		$("#cTipoContrato").val( "DI" ); 
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$("#caNoPreCompromiso").val( vcaNoCompromiso );
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 		
		$("#fVigencia").val( $("#vigenciaVentanillaPrecom").val());
				
		//Genera y actualiza Encabezado
		queryFormPost('tPreCompromisoEncabezadoCreate', {async: false });
		
		var j,nColums=12-parseInt($("#mesDisponible").val(),10);
		oTablD.fnClearTable();
		var cEvento="DISP_PRECOMMAT";
		if($("#cuentaDisponible").val()=="82109"){
			cEvento="R_DISP_PRECOMMAT";
		}
		var totA_Precom=0;
// 		alert("cuentaDisponible="+ $("#cuentaDisponible").val());
		for ( var i=0 ; i<aTrs.length ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			if (jqInputs.length > 0) {
				for ( j=0 ; j <= nColums ; j++ ) {
					var vep = aData[ 0 ] + "." + $.trim(aData[ 1 ]) ;
					var clv=$.trim(aData[0]);
					var ff=clv.substring(39, 40); 
					if(ff=='4' || $("#cuentaDisponible").val()=="82106"){
						cEvento="DISP_PRECOMMAT";
					}else{
						cEvento="R_DISP_PRECOMMAT";
					}
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
					totA_Precom=totA_Precom+parseFloat(vimporteP);
					if (parseFloat(vimporteP) != 0 ) {
						var vimporteN = vimporteP * -1 ; 
						vMes = parseInt($("#mesDisponible").val(),10)+j;
								$('#dt_grabaprecompD').dataTable().fnAddData( [
								'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + vdocren + '"></td>',
								'<td><input type="text" id="EP" name="EP" value="' + vep + '"></td>',
								'<td><input type="text" id="cEvento" name="cEvento" value="'+cEvento+'"></td>',
								'<td><input type="text" id="mImporte" name="mImporte" value="' + vimporteP + '"></td>',
								'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
								'<td><input type="text" id="nFolioPreCompromisoD" name="nFolioPreCompromisoD" value="' + $("#nFolioPreCompromiso").val()+ '"></td>',
								'<td><input type="text" id="nMesD" name="nMesD" value="' + vMes + '"></td>',
								'<td><input type="text" id="cCentroContableD" name="cCentroContableD" value="' + "<%=cCentroContable%>" + '"></td>'
							] );	
						vdocren++ ;
						queryFormPost("tPreCompromisoDetalleProcCreate", {async: false });
						oTablD.fnClearTable();
					}
				}
			}
		}
		// SI HAY PRECOMPROMISO DEL CONSOLIDADO, SE AGREGA EL DETALLE
		if($("#documentoAplicado").val()=='1'){
			queryFormPost("InsertDetallePrecom", {async:false});
			//queryFormPost("InsertDetallePrecomTmp", {async:false});//tabla temporal
		}else{
			var vimptot = $("#mImporteTotal").val();
			vimptot = quitaFmt( vimptot );
			totA_Precom=totA_Precom.toFixed(2);
			totA_Precom=parseFloat(totA_Precom);
			if(totA_Precom!=vimptot){//mImporteTotal
				alert("El monto a precomprometer es diferente al total del procedimiento.\nimporteTotalProced="+vimptot+"\n totalCap="+totA_Precom);
				document.getElementById("imgAprobarpreCompromiso").disabled = false;
				return;
			}
		}
		
		document.getElementById("imgAprobarpreCompromiso").disabled = false;
		//aplicacion contable
			 $.ajax({url: '../../servlet/ProcedimientoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val()+"&cIdConsolidado="+$("#cIdConsolidado").val()+"&val="+val, type:'post' , async: false,data:'operacion=4', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].Contable1;
					alert(mensajeAp);
					window.location = "Procedimiento-copia.jsp?tab=11";	
				}
			});
		return Aplica;
    }
	
	function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		alert("Ha ocurrido un error al crear el caso, contacte a su soporte");
      		return -1;
        }else{ 
     	   $("#nFolioPreCompromiso").val(folioPre);
     	   $("#folioCasoPreCompromiso").val(folioCaso);
     	}
	}
	
	function guardaFolioConsolidado(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		alert("Ha ocurrido un error al crear el caso, contacte a su soporte");
      		return -1;
        }else{ 
     	   $("#nFolioPreCompromisoConsolidado").val(folioPre);
     	   $("#folioCasoPreCompromisoConsolidado").val(folioCaso);
     	}
	}
	
	function setSequenceVal(seqValue) {
		seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "PR" + $("#cEjercicio").val() + seqValue;
		
	}
	
	function devuelvePrecompromiso(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="CAPTURADO "){
				alert("No se puede devolver el procedimiento, porque su estado no lo permite");
				return;
			}
			document.getElementById("imgDevolverpreCompromiso").disabled = true;
			var res=window.confirm("¿Está seguro que desea devolver el Procedimiento?, el preCompromiso sera devuelto al disponible, esta acción no se puede deshacer");
				if(res){
					////////////////////////////////////////////////////////
					//si existia un precompromiso en consolidado se debera volver a aplicar
					var val=0;
					queryFormPost("existePrecomConsolidado", {async:false});
					
					if($("#existePrecom").val()=='1'){
						//Genera un nuevo folio para el consolidado
						if($("#nFolioPreCompromisoConsolidado").val()=="0"){
							$.ajax({url: '../../servlet/ProcedimientoServlet' , type:'post' , async: false,data:'operacion=7', dataType: 'json', success: guardaFolioConsolidado});
						}
						if($("#nFolioPreCompromisoConsolidado").val()==0){
							return -1;
						}	
						////////Encabezado del consolidado/////////
						var nMes = "<%=today%>";
						var nMes = nMes.substring(5, 7 ) ;
						$("#fCarga").val( "<%=today%>" ); 
						$("#fAplicacion").val("<%=today%>" ); 
						$("#cIdContrato").val($("#cIdConsolidado").val());
						$("#cTipoContrato").val( "DI" ); 
						$("#cRamo").val( "<%=cRamo%>" );
						$("#cUnidadResponsable").val( "<%=cUR%>" );
						$("#caNoPreCompromiso").val( vcaNoCompromiso );
						$("#nEnviadoSICOP").val("0");
						$("#nMes").val( nMes ); 
						$("#fVigencia").val( $("#vigenciaVentanillaPrecom").val());
						queryFormPost('tPreCompromisoEncabezadoCreateConsolidado', {async: false });
						
						///////////Generar detalle//////////////////
						queryFormPost('tPrecomDetalleCreate', {async: false });
						val=$("#nFolioPreCompromisoConsolidado").val();
					}
					
					////////////////////////////////////////////////////////
					$.ajax({url: '../../servlet/ProcedimientoServlet?nFolioPrecompromiso='+$("#consecutivoPrecompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val()+"&val="+val+"&cIdConsolidado="+$("#cIdConsolidado").val(), type:'post' , async: false,data:'operacion=5', dataType: 'json', success: 
					function(j){
						mensajeAp=j[0].Contable1;
						alert(mensajeAp);
						$("#cAccion").val("DEVUELVEPRECOM_PROCEDIMIENTO");
						$("#cIdDocumento").val($("#cIdProcedimiento").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						/* // si existia un precompromiso en consolidado se debera volver a aplicar
						if(mensajeAp.toString().toUpperCase().indexOf("SE DEVUELVE CORRECTAMENTE EL PROCEDIMIENTO") < 0){
							//Genera un nuevo folio para el consolidado
							if($("#nFolioPreCompromisoConsolidado").val()=="0"){
								$.ajax({url: '../../servlet/ConsolidadoServlet' , type:'post' , async: false,data:'operacion=1', dataType: 'json', success: guardaFolioConsolidado});
							}
						} */
						
						window.location = "Procedimiento-copia.jsp?tab=11";	
						habilitaPestanas();	
					}
					});
				}
		}
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
						//ya trae precompromiso, si no cubre el monto del consolidado mostrar las pestañas de precom y presupuesto, para que sea solvetad						
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
					queryFormPost("validaFlujoProcedimiento", {async:false});
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
	
	function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" ||$("#nIdEstadoProcedimiento").val()=="DESIERTO  " ){
				alert("El estado del procedimiento no permite actualizar su estado");
				return;
			}
			
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
		$("#btnGuardarCaratulaProcedimiento").attr("visibility", "hidden");	
		document.getElementById("imgAprobarpreCompromiso").disabled = true;
		document.getElementById("imgDevolverpreCompromiso").disabled = true;
		document.getElementById("imgDesiertoProcedimiento").disabled = true;		
		$("#cAccion").val("DESIERTO_PROCEDIMIENTO");
	    $("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
    }
    
	</script>
</head>
<body id="dt_example">
	<form>
		<fieldset>
			<legend>Informaci&oacute;n del Procedimiento</legend>
			<table align="left" width="750px">
				<tr>
					<td align="right">
						<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarpreCompromiso" 	name="imgAprobarpreCompromiso" 	value="Pre-Comprometer"	onclick="guardarPrecompromiso();"/>&nbsp;&nbsp;  
						<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverpreCompromiso" 	name="imgDevolverpreCompromiso" 	value="Devolver" onclick="devuelvePrecompromiso();"	/>&nbsp;&nbsp;
						<input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgDesiertoProcedimiento" 	name="imgDesiertoProcedimiento" 	value="Desierto"	onclick="desiertoProcedimiento();"/>&nbsp;&nbsp;
                        <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	 onclick="window.location = 'Procedimiento-copia.jsp?tab=1';" />&nbsp;&nbsp;
					</td>
						
				</tr>
				<tr>
					<td align="left" colspan="2">
						<input type="text"style="width: 25px;border-width:0; background-color:transparent" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly value="<%=unidadUsuarioLogeado%>" />
						<input type="text" style="width: 690px;border-width:0; background-color:transparent" id="lblDescUsuario" name="lblDescUsuario" readonly /></td>
				</tr>
				<tr align="left">
					<td colspan="2">[[<input name="cIdProcedimiento"id="cIdProcedimiento" type="text" size="10" style="border-width:0; background-color:transparent;" readonly="readonly" />]]&nbsp;
						<input name="desProcedimiento" id="desProcedimiento" type="text" maxlength="150" style="border-width:0; background-color:transparent; width: 40em;"readonly="readonly"></input>
					</td>
				</tr>
				<tr align="left">
					<td colspan="2"><input name="lblcIdUnidadEjecutora"
						id="lblcIdUnidadEjecutora" type="text" readonly="readonly"
						style="border-width:0; background-color:transparent; width: 30em;"></input></td>
				</tr>
				<tr align="left">
					<td colspan="2"><input name="nIdEstadoProcedimiento"
						id="nIdEstadoProcedimiento" type="text"
						style="border-width:0; background-color:transparent; width: 45em;"></input></td>
				</tr>
				<tr align="left">
					<td colspan="2">Consolidado: <input name="cIdConsolidado"
						id="cIdConsolidado" type="text" size="10"
						style="border-width:0; background-color:transparent;" readonly="readonly"></input></td>
				</tr>
				<tr align="left">
					<td colspan="2">Tipo de Proceso: <input name="lbltipoProceso"
						id="lbltipoProceso" type="text" size="18"
						style="border-width:0; background-color:transparent;" readonly="readonly"></input></td>
				</tr>
				<tr align="left">
					<td colspan="2">Monto Precomprometido: <input
						name="lblApartado" id="lblApartado" type="text" size="10"
						style="border-width:0; background-color:transparent;" readonly="readonly"></input></td>
				</tr>
				<tr>
					<td align="left">
						<table>
							<tr>
								<td align="left">Monto Procedimiento: <input
									style="text-align: right;" readonly type="text" maxlength="20"
									size="12" name="mImporteTotal" id="mImporteTotal" value="0"
									disabled="disabled">
								</td>
								<td align="left">Precompromiso Actual: <input
									style="text-align: right;" readonly type="text" maxlength="12"
									size="12" name="mComprometido" id="mComprometido" value="0"
									disabled="disabled">
								</td>
								<td align="left">Saldo Precompromiso: <input
									style="text-align: right;" readonly type="text" maxlength="12"
									size="12" name="difPrecompromiso" id="difPrecompromiso"
									value="0" disabled="disabled">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<table id="dt_preCompromiso" class="display" style="width: 750px">
				<thead>
					<tr align="center">
						<th>Estructura Program&aacute;tica</th>
						<th>Clave Interna</th>
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
						<th>Contrato</th>
						<th>EP</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<table id="dt_suficiencia" class="display" style="width: 750px">
				<thead>
					<tr align="center">
						<th>Estructura Program&aacute;tica</th>
						<th>Clave Interna</th>
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
				<tbody>
				</tbody>
			</table>
			<table id="dt_grabaprecompD" style="visibility: hidden">
				<thead>
					<tr align="center">
						<th>nDocRenglon</th>
						<th>EP</th>
						<th>cEvento</th>
						<th>mImporte</th>
						<th>mImporteNegativo</th>
						<th>nFolioPreCompromisoD</th>
						<th>cMes</th>
						<th>cCentroContable</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</fieldset>

		<input id="cEjercicio" name="cEjercicio" type="hidden" size="4"
			value="<%=cEjercicio%>"> <input id="cIdTipoProcedimiento"
			name="cIdTipoProcedimiento" type="hidden" size="4"
			value="<%=cIdTipoProcedimiento%>"> <input
			id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden"
			size="3" value="<%=cIdUnidadEjecutora%>"> <input
			id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4"
			value="<%=nIdConsecutivo%>"> <input type="hidden"
			name="mesDisponible" id="mesDisponible" value="1" /> <input
			id="TipoConsolidado" name="TipoConsolidado" value="" type="hidden"
			size="10"> <input id="ConsecutivoConsolidado"
			name="ConsecutivoConsolidado" value="" type="hidden" size="10">
		<input id="Activo" name="Activo" type="hidden" size="10"> <input
			id="tipoProceso" name="tipoProceso" type="hidden" size="10">
		<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden"
			size="10"> <input id="usuarioLogin" name="usuarioLogin"
			value="" type="hidden" size="10"> <input id="cCentroContable"
			name="cCentroContable" value="" type="hidden" size="10"> <input
			id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento"
			value="" type="hidden" size="10"> <input type="hidden"
			id="cDocumento" name="cDocumento" value="PRECOMMATERIALES"> <input
			type="hidden" name="GP_NOMBRE" id="GP_NOMBRE"
			value="desfase_mes_activo_precompromiso" /> <input type="hidden"
			name="GP_VALOR" id="GP_VALOR" /> <input type="hidden"
			name="nFolioPreCompromiso" id="nFolioPreCompromiso" /> <input
			type="hidden" name="folioCasoPreCompromiso"
			id="folioCasoPreCompromiso" /> <input type="hidden"
			name="cIdContrato" id="cIdContrato" /> <input type="hidden"
			name="cTipoPoliza" id="cTipoPoliza" /> <input type="hidden"
			name="fCarga" id="fCarga" /> <input type="hidden" name="fAplicacion"
			id="fAplicacion" /> <input type="hidden" name="cTipoContrato"
			id="cTipoContrato" /> <input type="hidden" name="cRamo" id="cRamo" />
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" />
		<input type="hidden" name="caNoPreCompromiso" id="caNoPreCompromiso" />
		<input type="hidden" name="nEnviadoSICOP" id="nEnviadoSICOP" /> <input
			type="hidden" name="nMes" id="nMes" /> <input type="hidden"
			name="fVigencia" id="fVigencia" /> <input type="hidden"
			name="documentoAplicadoProcedimiento"
			id="documentoAplicadoProcedimiento" /> <input type="hidden"
			name="consecutivoPrecompromiso" id="consecutivoPrecompromiso" /> <input
			type="hidden" name="vigenciaVentanillaPrecom"
			id="vigenciaVentanillaPrecom" />

		<!-- habilitar pestanas -->
		<input type="hidden" name="documentoAplicado" id="documentoAplicado" />
		<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
		<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">
		<input id="existePrecompromiso" name="existePrecompromiso"
			type="hidden" size="10"> <input id="MontoConIVA"
			name="MontoConIVA" type="hidden" size="10">
			
		<input id="cOficio" name="cOficio" type="hidden"> 
		<!-- BITACORA -->
		<input id="cAccion" name="cAccion" type="hidden" size="10"> <input
			id="cIdDocumento" name="cIdDocumento" type="hidden" size="10">
		<input id="cIdUsuario" name="cIdUsuario" type="hidden" size="10">
		<input id="formatCurrencyTem" name="formatCurrencyTem" type="hidden"
			size="10" value="0">

		<!-- precom de cosolidado -->
		<input id="nFolioPreCompromisoConsolidado"
			name="nFolioPreCompromisoConsolidado" type="hidden" size="10"
			value="0"> <input id="folioCasoPreCompromisoConsolidado"
			name="folioCasoPreCompromisoConsolidado" type="hidden" size="10">
		<input id="existePrecom" name="existePrecom" type="hidden" size="10"
			value="0"> <input id="cboCategoria" name="cboCategoria"
			type="hidden" size="10"> <input id="EstadoCaptura"
			name="EstadoCaptura" type="hidden" size="10"> <input
			id="cIdEntidadContable" name="cIdEntidadContable" type="hidden"
			size="10"> <input id="numero_externoCaratula"
			name="numero_externoCaratula" type="hidden" size="10"> <input
			id="ivaProcedimiento" name="ivaProcedimiento" value="" type="hidden"
			size="10"> <input id="descripcionCaratula"
			name="descripcionCaratula" type="hidden" size="10" /> <input
			type="hidden" name="isPlurianual" id="isPlurianual" /> <input
			type="hidden" name="cuentaDisponible" id="cuentaDisponible"
			value="82106" />
	</form>
</body>
</html>