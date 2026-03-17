<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	//Calendar c2 = Calendar.getInstance();
	//c2.add(Calendar.DATE, 20);
	//String vigencia = sdf.format(c2.getTime());
	String today = sdf.format(c1.getTime());
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	Map rol =usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoConsolidado= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String cCentroContable="";
	String cUR="";
	String cRamo="";
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		cIdTipoConsolidado = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
	}
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	/* cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo(); */
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
	
	String grupoMat;
	boolean bGrupo=false;
	
	Map grupo=usuario.getGrupos();
	Iterator it3 = grupo.entrySet().iterator();
	while(it3.hasNext()){
		Map.Entry r = (Map.Entry)it3.next();
		grupoMat=(String)r.getKey();
		System.out.print(grupoMat);
		if("RECURSOS_MATERIALES".equalsIgnoreCase(grupoMat)){
			bGrupo=true;
			break;
		}
	
	}
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
		var cIdConsolidado;
		var vcaNoCompromiso;
		$(document).ready(function() {
		<%
				
			
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			///// botones/////
			int imgPrecompromete=0;
			int imgDevuelve=0;
		
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				roles += r.getKey().toString()+",";
			}
			if(roles.length()>0){
				roles = roles.substring(0,roles.length()-1);
			}
			Map botones=nb.getBotones(roles,"Consolidado","PrecompromisoConsolidado");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
				String img=(String) b.getValue();
				if ("imgAprobarpreCompromiso".equals(img)){      
					imgPrecompromete=1; 
				}
				if ("imgDevolverpreCompromiso".equals(img)){
					imgDevuelve=1;
				}
				
				
			}
			NegativaPestana NegPestana=new NegativaPestana();
			NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			Iterator it2 = rol.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry r = (Map.Entry)it2.next();
				roles += r.getKey().toString()+",";
			}
			if(roles.length()>0){
				roles = roles.substring(0,roles.length()-1);
			}
			Map pestanas=ebl.getPestana(roles,"Consolidado");
			Iterator it = pestanas.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();%>
				$("#<%=e.getValue()%>").attr("disabled", true);
				<%
			}
		%>	
		
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cCentroContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuario").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleConsolidado").val('<%=roles%>');
	 	$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
	 	cIdConsolidado=$("#cIdConsolidado").val();
		initTables();
		cargaPreCompromisoVacio();
		muestraInformacion();
		habilitaPestanas();
		cargaSuficiencias();
		deshabilitaMeses($("#dt_preCompromiso").dataTable());
		deshabilitaMeses($("#dt_suficiencia").dataTable());
		
	});
	
	
	
	function muestraInformacion (){
		
			queryFormPost("mConsolidadoCaratulaRead",{async : false});				
			queryFormPost("mConsolidadoRead", {async : false});				
			queryFormPost("fn_mConsolidadoCalculaMontoRead", {async : false});
			queryFormPost("fn_mConsolidadoCalculaMontoBrutoRead", {async : false});
			queryFormPost("mConsolidado_LabelRead", {async : false});		
			
			queryFormPost("numPartidasConsolidadasRead",{async : false});
			queryFormPost("mValidaPrecompromiso",{async:false});
			queryFormPost("getcIdUsuarioCreacionConsolidado",{async:false});
			queryFormPost("vigenciaVentanillaPrecompromiso",{async:false});
			//// tipo de poliza
			queryFormPost("TipoPolizaRead", {async: false});
			
			queryFormPost("mSolicitudDescripcionUnidad", { async:false });
			
			document.getElementById("lblConsolidado").style.readonly=true;
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblDescripcion").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			
			//Lee la configuración de mes
			queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
			var offset=parseInt($("#mesDisponible").val(),10)+parseInt($("#GP_VALOR").val(),10);
		
			if(offset>12 || offset <0){
				alert("El desfase del calendario está mal configurado, favor de avisar al administrador");
				window.location ="Procedimiento-copia.jsp?tab=0";
			}else{
				$("#mesDisponible").val(offset);
			}
			$("#mImporteTotal").val($("#montoTotal").val().toString().replace("Total: ",""));
		
			if($("#documentoAplicado").val()==1){
				//obtener monto precomprometido
				queryFormPost("apartadoConsolidado", {async: false});
				cargaPrecompromiso($("#lblApartado").val());
			//	
			//	$("#mComprometido").val($("#lblApartado").val().toString().replace("$",""));
			}
			
		$("#mImporteTotal").formatCurrency();	
		$("#difPrecompromiso").formatCurrency();
		$("#montoTotal").formatCurrency();
		$("#mComprometido").formatCurrency();
			
	}

	
function validaBotones(boton){  
		var imgAprobar='<%=imgPrecompromete%>';
		var imgDevuelve='<%=imgDevuelve%>';
			switch (boton){  
			case "imgAprobarpreCompromiso":
				if (imgAprobar==0){   
					guardarPrecompromiso();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDevolverpreCompromiso":
				if (imgDevuelve==0){
					devuelvePrecompromiso();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			}/// FIN SWITCH
	} 
	function cargaSuficiencias(){
	var where='';
	if ($("#U_LOGIN").val()==$("#cIdUsuarioCreacion").val() ||( $('#usuarioRoleConsolidado').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
		where=" cIdConsolidado='" + $("#cIdConsolidado").val() + "'";
	}else{
		where=" cIdConsolidado='" + $("#cIdConsolidado").val() + "' and cIdEntidadContable = '" + $("#cCentroContable").val() + "'";
	}
	
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
			sScrollX: 100,
			sScrollY: 100,
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			//Query de Financiero, también se utiliza para Compromiso
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleConsolidadoEP&qw="+where,
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
			sScrollX: 100,
			sScrollY: 100,
			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,  
			//Carga el calendario con valores de 0
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoConsolidado('" + cIdConsolidado + "')",
		
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
				{ sName: "cIdConsolidado",	bSearchable: false,	bSortable: false, bVisible: false  }, 
           		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
           		fnInitComplete: function(oSettings, json) {
           			
           		//	if($("#documentoAplicado").val()==0){
           		
           			if(cubreMonto()){
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
		vcompr=parseFloat(vcompr);
		valor=parseFloat(valor);
		vimptot=parseFloat(vimptot);
		if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
	    	alert("El Compromiso Actual Excede al Saldo Compromiso"); 
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompT = vimptot-valor-vcompr; //importe total-valoren ep-lo de otras ep´s
		//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
		vcompT=vcompT.toFixed(2);
		$("#difPrecompromiso").val(vcompT);
		$("#difPrecompromiso").formatCurrency();
	}
	
	function guardarPrecompromiso (){
		var grupo=<%=bGrupo%>;
		if(grupo){
			var imgAprobar='<%=imgPrecompromete%>';
			if (imgAprobar==0){
				if(validaModificarConsolidado()){
					document.getElementById("imgAprobarpreCompromiso").disabled = true;
					/* if($("#mImporteTotal").val() != $("#mComprometido").val()){
						alert("El importe total es mayor al monto pre-comprometido");
						document.getElementById("imgAprobarpreCompromiso").disabled = false;
						return -1;
					} */
					var Aplicacion=preCompromiso();
					$("#cAccion").val("PRECOMPROMISO_CONSOLIDADO");
					$("#cIdDocumento").val($("#cIdConsolidado").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
				}	
			}
		}else{
			alert("No es posible realizar el precompromiso, ya que l mÃ³dulo de financiero se encuentra cerrado");
		}
		
		
	}
	
	function preCompromiso(){
		var mensajeAp="";
	    var Aplica="1";
	    
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val()=="0"){
			$.ajax({url: '../../servlet/ConsolidadoServlet' , type:'post' , async: false,data:'operacion=1', dataType: 'json', success: guardaFolio});
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
		$("#cIdContrato").val(cIdConsolidado);
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
		for ( var i=0 ; i<aTrs.length ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			if (jqInputs.length > 0) {
				for ( j=0 ; j <= nColums ; j++ ) {
					var vep = aData[ 0 ] + "." + $.trim(aData[ 1 ]) ;
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
					if (parseFloat(vimporteP) != 0 ) {
						var vimporteN = vimporteP * -1 ; 
						vMes = parseInt($("#mesDisponible").val(),10)+j;
								$('#dt_grabaprecompD').dataTable().fnAddData( [
								'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + vdocren + '"></td>',
								'<td><input type="text" id="EP" name="EP" value="' + vep + '"></td>',
								'<td><input type="text" id="cEvento" name="cEvento" value="DISP_PRECOMMAT"></td>',
								'<td><input type="text" id="mImporte" name="mImporte" value="' + vimporteP + '"></td>',
								'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
								'<td><input type="text" id="nFolioPreCompromisoD" name="nFolioPreCompromisoD" value="' + $("#nFolioPreCompromiso").val()+ '"></td>',
								'<td><input type="text" id="nMesD" name="nMesD" value="' + vMes + '"></td>',
								'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + "<%=cCentroContable%>" + '"></td>'
							]);	
						vdocren++ ;
						queryFormPost("tPreCompromisoDetalleCreate", {async: false });
						oTablD.fnClearTable();
					}
				}
			}
		}
		document.getElementById("imgAprobarpreCompromiso").disabled = false;
		//aplicacion contable
			$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&partidas=0", type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].Contable1;
					alert(mensajeAp);
					window.location = "Consolidado.jsp?tab=7";	
					
					}
			}); 

		//return mensajeAp;
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
	
	function setSequenceVal(seqValue) {
		seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "PR" + $("#cEjercicio").val() + seqValue;
		
	}
	
	function devuelvePrecompromiso(){
		
			var imgDevuelve='<%=imgDevuelve%>';
			if (imgDevuelve==0){
				if(validaModificarConsolidado()){
					queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
					if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
						$("#tieneProcedimiento").val('0');
					
					if ($("#tieneProcedimiento").val() == "0") {
						document.getElementById("imgDevolverpreCompromiso").disabled = true;
						//valida que existe documento aplicado
						if ($("#documentoAplicado").val() == "0") {
							$("#nIdEstado").val("1"); //capturada
							queryFormPost("mConsolidadoUpdate", { async:false });
							//BitÃ¡cora
							$("#cAccion").val("DEVUELVE_CONSOLIDADO");
							$("#cIdDocumento").val($("#cIdConsolidado").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							window.location = "Consolidado.jsp?tab=5";
						}else{
							queryFormPost("vigencias",{async:false});
							if($("#enEspera").val()==0){ // no existen vigencias en espera
								var grupo=<%=bGrupo%>;
								if(grupo){
									var res=window.confirm("Â¿EstÃ¡ seguro que desea devolver el Consolidado?, el preCompromiso sera devuelto al disponible, esta acciÃ³n no se puede deshacer");
									if(res){
										$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#consecutivoPrecompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&partidas=0" , type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
										function(j){
											mensajeAp=j[0].Contable1;
											alert(mensajeAp);
								//queryFormPost("mConsolidado_LabelRead", {async : false});
								//habilitaPestanas();	
											//Bitácora
											$("#cAccion").val("DEVUELVE_CONSOLIDADO");
											$("#cIdDocumento").val($("#cIdConsolidado").val());
											queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
											window.location = "Consolidado.jsp?tab=5";
										}
										});
									}
								}else{
									alert("No es posible devolver el precompromiso, ya que el mÃ³dulo de financiero se encuentra cerrado");
								}
							}else{
								alert("Existen solicitudes de ampliaciÃ³n de vigencia pendientes de su aprobaciÃ³n");
							}	
						}
						
					}else
						alert("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.");
					
				}
			}else{
				alert("No tiene permiso para realizar esta acciÃ³n");
			}
		
		
	}
	function validaModificarConsolidado(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleConsolidado').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
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
			if ($("#nIdEstado").val()==2 && $("#tieneProcedimiento").val()==0 ){	if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#presupuestoConsolidado").css("display", "block");
					$("#preCompromisoConsolidado").css("display", "block");		
				}else{ //aplicado contablemente
					// si el precompromiso aplicado cubre el monto del consolidado ya no se muestran las pestañas de presupuesto
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
					/* $("#ampliacionPrecompromiso").css("display", "block");
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#vigenciaRequisicionesConsolidadas").css("display", "none"); */	
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
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "CC", "CO", "CS", "CA" ];
		if ($.inArray($("#cIdTipoConsolidado").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	
	function anularConsolidado () {
		if(validaModificarConsolidado()){
			queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
			if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
				$("#tieneProcedimiento").val('0');
			
			if ($("#tieneProcedimiento").val() == "0") {
				if (confirm("¿Está seguro que desea anular el Consolidado "+ $("#cIdConsolidado").val() +" ? \n Esta acción no puede revertirse y todas sus lineas serán liberadas.")) {
					$("#nIdEstado").val("3"); //anulada
					queryFormPost("mConsolidadoUpdate", { async:false });
					queryFormPost("sp_mConsolidadoAnulaConsolidado", {async:false});
					//Bitácora
					$("#cAccion").val("ANULA_CONSOLIDADO");
					$("#cIdDocumento").val($("#cIdConsolidado").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					window.location = "Consolidado.jsp?tab=5";
				}
			}else
				alert("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.");
				
		}
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
	
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
		queryFormPost("apartadoConsolidado", {async: false});	
		alert($("#lblApartado").val());
		alert($("#MontoConIVA").val());
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
			return true;
		}else{
			return false;
		}
	}
	</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Informaci&oacute;n del Procedimiento</legend>
					<table align="left" width="35px">
				        <tr> 
							<td align="right" colspan="2"> 
								<img id="imgAprobarpreCompromiso" src="../imagenes/accept_green.png" style="cursor: pointer"	onclick="guardarPrecompromiso();" />	&nbsp;Pre-Comprometer
								<img id="imgDevolverpreCompromiso" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devuelvePrecompromiso();" />	&nbsp;Devolver
								<img id="imgAnularPartidas" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anularConsolidado();" /> Anular
								<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="window.location = 'Procedimiento-copia.jsp?tab=1';" /> &nbsp;Salir
							</td>
						</tr>
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
				    	<tr>
				    		<td>Monto del Consolidado</td><td align="left"><input type="text" style="width: 700px" name="montoTotal" id="montoTotal" readonly style="border-width:0; background-color:transparent"/></td>
				    	</tr>
						<tr>
						<td align="left" colspan="2">
							<table>
								<tr>
									<td align="left">
										Monto Consolidado:
										<input style="text-align: right;" readonly type="text" maxlength="20" size="12" name="mImporteTotal" id="mImporteTotal" value="0" disabled="disabled">
									</td>
									<td align="left">
										Precompromiso Actual:
										<input style="text-align: right;" readonly type="text" maxlength="12" size="12" name="mComprometido" id="mComprometido" value="0" disabled="disabled">
									</td>
									<td align="left">
										Saldo Precompromiso:
										<input style="text-align: right;" readonly type="text" maxlength="12" size="12" name="difPrecompromiso" id="difPrecompromiso" value="0" disabled="disabled">
									</td>
								</tr>
							</table>
						</td>
					</tr>
					</table>
				</fieldset>
				<fieldset>
				<table align="left" width="750px" >
				<tr>
					<td align="left" style="width: 750px">
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
					</td>
				</tr>
				<tr>
					<td><br /></td>
				</tr>
				<tr>
					<td align="left" style="width: 750px">
				
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
					</td>
				</tr>
				</table>
				<table id="dt_grabaprecompD" style="visibility: hidden"> <!--  -->
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
			
				<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
				<input id="cEjercicio2" name="cEjercicio2" type="hidden"  value="<%= cEjercicio %>">
				<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4" value="<%= cIdTipoConsolidado %>">
				<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
				<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
				<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>							
				<input id="usuarioRoleConsolidado" name="usuarioRoleConsolidado"  type="hidden" size="10">
				
				<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
				<input id="cIdConsolidado" name="cIdConsolidado" type="hidden" size="10">
				<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="10">									
				<input id="consecutivoPrecompromiso" name="consecutivoPrecompromiso" type="hidden" size="2">
				<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="2">
				<input id="tieneProcedimiento" name="tieneProcedimiento" type="hidden" size="2">
				
				
				<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
							
				
				<input type="hidden" name="mesDisponible" id="mesDisponible" value="1" />
 				<input id="usuarioLogin" name="usuarioLogin" value=""  type="hidden" size="10">
 				<input id="cCentroContable" name="cCentroContable" value=""  type="hidden" size="10">
 			
 				<input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMMATERIALES">
 				<input type="hidden" name="GP_NOMBRE" id="GP_NOMBRE" value="desfase_mes_activo_precompromiso" />
				<input type="hidden" name="GP_VALOR" id="GP_VALOR" />
				<input type="hidden" name="nFolioPreCompromiso" id="nFolioPreCompromiso" value="0"/>
				<input type="hidden" name="folioCasoPreCompromiso" id="folioCasoPreCompromiso" />
				<input type="hidden" name="cIdContrato" id="cIdContrato" />
				<input type="hidden" name="cTipoPoliza" id="cTipoPoliza" />
				<input type="hidden" name="fCarga" id="fCarga" />
				<input type="hidden" name="fAplicacion" id="fAplicacion" />
				<input type="hidden" name="cTipoContrato" id="cTipoContrato" />
				<input type="hidden" name="cRamo" id="cRamo" />
				<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" />
				<input type="hidden" name="caNoPreCompromiso" id="caNoPreCompromiso" />
				<input type="hidden" name="nEnviadoSICOP" id="nEnviadoSICOP" />
				<input type="hidden" name="nMes" id="nMes" />
				<input type="hidden" name="fVigencia" id="fVigencia" />
				<input type="hidden" name="vigenciaVentanillaPrecom" id="vigenciaVentanillaPrecom" />
				<input type="hidden" name="enEspera" id="enEspera" />  
				
				<!-- bitacora -->
				<input type="hidden" name="cAccion" id="cAccion" />  
				<input type="hidden" name="cIdDocumento" id="cIdDocumento" />  
				<input type="hidden" name="cIdUsuario" id="cIdUsuario"/>
				<input type="hidden" name="nIdEstado" id="nIdEstado" />
				<input type="hidden" name="lblApartado" id="lblApartado" />  
				<input type="hidden" name="MontoConIVA" id="MontoConIVA" />     				  
		</form>				
	</body>
</html>