<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	//Calendar c2= Calendar.getInstance();
	//c2.add(Calendar.DATE, 20);
	//String vigencia=sdf.format(c2.getTime());
	String today = sdf.format(c1.getTime());
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont = false;
	String name_user = usuario.getLogin();
	String idRol = "0";
	Map rol = usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoContrato = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String) session
				.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoContrato = (String) session
				.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String) session
				.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String) session
				.getAttribute(GestionInterface.ATT_ContratoConsecutivo);
	} else
		response.sendRedirect("Contratos.jsp?tab=0");
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si"))
		cAplicaDocto = "Si";
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Genera Pre-Compromiso</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<script type="text/javascript" charset="utf-8">
	var vcontrato;
	var vfolio;
	var vEstado;
	var vVigente=1;
	var vcaNoCompromiso;
	var initSuf=0;
	var initPrecom=0;
	var roles='';
	$(document).ready(function() {
		$("#tbs").val(4);
		showAndHideTabs();
		<%String roles = "";
			//botones
			NegativaBoton NegBoton = new NegativaBoton();
			NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic(
					"jdbc/gestion");
			int editar = 0;
			int imgAprobar = 0;
			int imgDevolver = 0;
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry) it1.next();
				roles += r.getKey().toString() + ",";
			}
			if (roles.length() > 0) {
				roles = roles.substring(0, roles.length() - 1);
			}
			Map botones = nb.getBotones(roles, "Contratos",
					"preCompromisoContrato");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry) btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%String img = (String) b.getValue();
				if ("imgAprobarpreCompromisoCont".equals(img)) {
					imgAprobar = 1;
				}
				if ("imgDevolverpreCompromisoCont".equals(img)) {
					imgDevolver = 1;
				}
				if ("trEditarpreCompromisoCont".equals(img)) {
					editar = 1;
				}
				if ("imgAprobarCompromisoCont".equals(img)) {%>
						$("#imgAprobarCompromisoCont").css("display", "none");
				<%}
			}%>
		roles="<%=roles%>";
	    var nEditing = null;
		initTables();
		//clickHandlers();
		//para evitar el uso de BackSpace
		setReadOnly();
		//Carga los datos de inicio
		initQueries();
		
		//Copia el Pedido definitivo a documentodefinitivo
		$("#cDocumentoDefinitivo").val($("#cContratoDefinitivo").val())
		//Carga ur cc
		$("#dt_urcc").attr("visible", false);
		loadURCC();
		
		//Guarda en variables globales el contrato, folio, estado y si es vigente
		vcontrato=$("#cContratoDefinitivo").val();
		vfolio=$("#nFolioPreCompromiso").val();
		vEstado=parseInt($("#nIdEstado").val(),10);
		//Carga la tabla que tiene el monto de cada EP
		
		//vVigente=$("#esPosiblePrecomprometer").val();
		
		if(vEstado==3 || vEstado==4 ){
			//En caso que sea un contrato con un preCompromiso ya hecho
			$("#docContrato").css("display", "block");
			cargaContratoPrecomprometido();
			$("#mComprometido").val($("#mImporteTotal").val());
			$("#mComprometido").formatCurrency();
			if(vEstado==4){
				queryFormPost("mValidaCompAutSicop", {async: false});
			}
		}
		else {
			//revisamos si es una copia del contrato
			queryFormPost("revisaCopiaPedido", {async: false});
			if(parseInt($("#estadoPartidaProcedimiento").val(),10) != 3 )
		  		cargaPreCompromisoVacioContrato();
		  	else
		  		cargaPreCompromisoVacio();
		}
		
		setInitConditions();
		cargaSuficiencias();
		
		var offset=parseInt($("#mesDisponible").val(),10)+parseInt($("#GP_VALOR").val(),10);
		if(offset>12 || offset <0){
			swal("El desfase del calendario está mal configurado, favor de avisar al administrador.",{icon:"info",button: "Cerrar"});
		}else
			$("#mesDisponible").val(offset);
		//Solo se puede precomprometer para el mes siguiente al actual
		deshabilitaMeses($('#dt_preCompromiso').dataTable());
		deshabilitaMeses($('#dt_suficiencia').dataTable());
		$("#mComprometido").formatCurrency();
		$("#mImporteTotal").formatCurrency();
		$("#difPrecompromiso").formatCurrency();
		
		$("#edit").button().click(function(){
	       	var nRow = editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
	   	});
	   	
	});
	
	function setInitConditions(){
		//if(vEstado>2 || vVigente==0){
		if(vEstado==3 ||vEstado==4 || vVigente==0){
		
			//Se deshabilita el boton de aprobar en caso que ya exista un precompromiso
			document.getElementById("imgAprobarpreCompromisoCont").disabled = true;
			$("#imgAprobarpreCompromisoCont").hide();
			$("#inpPre-ComprometerCont").hide();
			$('#dt_preCompromiso').attr('disabled', true);
		}
		if(vEstado != 3){
			//En caso el estado del contrato sea aprobado, falta por confirmar que hacer en los otros casos
			document.getElementById("imgDevolverpreCompromisoCont").disabled = true;
			$("#imgDevolverpreCompromisoCont").hide();
			$("#imgAprobarCompromisoCont").css("display", "none");
		}
		if(vEstado==3 && roles.toString().indexOf("ANALISTA") >= 0  ){
	   		$("#textAutorizaContrto").show();
	   		$("#imgAprobarCompromisoCont").hide();
	   	}
	   	if(vEstado==6){
	   		$("#imgAprobarCompromisoCont").hide();
	   		$("#imgAprobarpreCompromisoCont").hide();
			$("#inpPre-ComprometerCont").hide();
			document.getElementById("imgDevolverpreCompromisoCont").disabled = true;
	   	}
	}
	
	function clickHandlers(){
		$('#edit').click( function () {
	       	var nRow = editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
	   	} );
	}
	function initQueries(){
		//leemos la vigencia  que tiene la ventanilla para aprobar el precompromiso y se le suma lo que tiene parametrizado en msistema
	   	queryFormPost("vigenciaVentanillaPrecompromiso", {async: false});
	  	//Lee los montos del contrato
		//queryFormPost("fnMontoNetoContratoPresupuestoRead", {async: false});
		//Lee los datos del cabecero
		queryFormPost("mContratoCaratulaRead", {async: false,
			callback:function(){
				muestraObserv();
			}
		});
		queryFormPost("mContratoHeaderRead", {async: false});
		//Ejecuta una función en la base de datos para saber si la unidad ejecutora no tiene Precompromisos pendientes por entregar documentacion
		queryFormPost("fn_vigenciaValida", {async: false});
		queryFormPost("TipoPolizaRead", {async: false});
		//Lee la configuración de mes
		queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});

		//queryFormPost("fnMontoSubtotalReadContrato", {async: false});
		//queryFormPost("fnMontoIVAReadContrato", {async: false});
		queryFormPost("fnMontoImpuesto1ReadContrato", {async: false});
		queryFormPost("fnMontoImpuesto2ReadContrato", {async: false});
		queryFormPost("fnMontoImpuesto3ReadContrato", {async: false});
		
		if($("#lblImporteImpuesto1").val() == "")
			$("#divImporteImpuesto1").css("display","none");
		if($("#lblImporteImpuesto2").val() == "")
			$("#divImporteImpuesto2").css("display","none");
		if($("#lblImporteImpuesto3").val() == "")
			$("#divImporteImpuesto3").css("display","none");
		if($("#lblfCarga").val() == "")
			$("#divFCarga").css("display","none");
		if($("#lblfVigencia").val() == "")
			$("#divFVigencia").css("display","none");
		
		//queryFormPost("fnMontoTotalContrato", {async: false});
		//$("#mImporteTotal").val($("#lblTotal").val().toString().replace("Total: ",""));
		
		//Esconde el letrero de precompromiso encontrado
		$("#precompromisoNotice").hide();
        //revisamos si es una copia del contrato
		queryFormPost("revisaCopiaPedido", {async: false});
		if(parseInt($("#estadoPartidaProcedimiento").val(),10) != 3 ){
			queryFormPost("readPrecompromisoTotal", {async: false} );
			//Si el valor del precompromiso disponible es > 0
			if (quitaFmt($("#mPrecompromisoReal").val()) > 0){
				$("#mPrecompromisoReal").formatCurrency();
				$("#precomReal").html($("#mPrecompromisoReal").val());
				$("#precompromisoNotice").show();
				$("#mImporteTotal").val(quitaFmt($("#mImporteTotal").val()));
			}
	  	}	
		//Muestra fechas de vigencia y carga
		queryFormPost("fCargaVigenciaContrato", {async: false});
		
		//querys para Comprometer
		queryFormPost("obtienenFolioCont", {async: false});
		queryFormPost("preCompromisoCaratulaVentanillaRead", {async: false});
	}
	function formateaMoneda(importe){
		var importeSeparado = importe.toString().split("\.");
		var importeParte1 = importeSeparado[0];
		var cont=0;
		var tem="";
		
		for(var i=importeParte1.length; i>0; i--){
			if(cont == 3){
				tem = ","+tem;
				cont=0;
			}
			tem = importeParte1.substring(i-1,i)+tem;
			cont++;
		}
		if(importe.toString().indexOf("\.")>0){
			for(var i=importeSeparado[1].length; i<2; i++){
				importeSeparado[1]+="0";
			}
			return tem+"."+importeSeparado[1];
		}
		else{
			return tem+".00";
		}
	}
	function actualizaCaratula(){
		queryFormPost("mContratoCaratulaRead", {async: true});
		queryFormPost("mContratoHeaderRead", {async: true});
	}
	function setReadOnly(){
		document.getElementById("lblUnidadEjecutora").style.readonly=true;
		document.getElementById("lblProcedimiento").style.readonly=true;
		document.getElementById("lblDefinitivo").style.readonly=true;
		document.getElementById("lblContrato").style.readonly=true;
		document.getElementById("lblProveedor").style.readonly=true;
		document.getElementById("lblEstado").style.readonly=true;
		document.getElementById("lblTotal").style.readonly=true;
		document.getElementById("mComprometido").style.readonly=true;
		document.getElementById("mImporteTotal").style.readonly=true;
		document.getElementById("difPrecompromiso").style.readonly=true;		
	}
	function initTables(){
		$('#dt_preCompromiso').dataTable(
				{
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
        			"bInfo": false } );
		$('#dt_grabaprecompD').attr('visible', false);
		
			$('#dt_suficiencia').dataTable(
			{
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
		
		
		
	}
	
	function loadURCC(){
		var campos = "'" + $("#cIdTipoContrato").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val() + "'";
		
		oTableUrcc=$('#dt_urcc').dataTable( {
				bPaginate: false,
       			bLengthChange: false,
       			bFilter: false,
       			bInfo: false,
				bAutoWidth: false,
				sScrollY: 100,
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mUrccContrato(" + campos + ")",
				aoColumns: [
					{ sName: "cIdEntidadContable"},
					{ sName: "ur" }
				]
		}) ;
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
			sScrollX: "100%",
			sScrollY: "100%",
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			//Query de Financiero, también se utiliza para Compromiso
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_disponibleContratoEP('" + vcontrato 
			+ "','"+$("#cuentaDisponible").val()+"','"+$("#cIdEntidadContable1").val()+"')",
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
				{ sName: "MontoAnual",bSortable: false },
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false } ],
			fnInitComplete: function(oSettings, json) {
				if(vEstado ==2 || vEstado==5 && vVigente==1) //Se encuentra En SAI con un contrato diverso y esta vigente
					var editar='<%=editar%>';
					if (editar==0){
						document.getElementById("trEditarpreCompromisoCont").style.display="table-row";
					}
					
				 	initSuf=1;
				 	if(initPrecom==1)	{	
						//Se encuentra En SAI con un contrato diverso y esta vigente
						var aTrsSuf = $('#dt_suficiencia').dataTable().fnGetNodes();
						var aTrsPrecom= $('#dt_preCompromiso').dataTable().fnGetNodes();
						autoAjuste(aTrsSuf,aTrsPrecom);
						initSuf=0;
					}
				}
            });
	}
	function cargaPreCompromisoVacio(){
		oTable=$("#dt_preCompromiso").dataTable({
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
			sScrollX: "100%",
			sScrollY: "150%",
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoDevuelto('" + vcontrato + "')",
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
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
           		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
           		
           		fnInitComplete: function(oSettings, json) {
           			queryFormPost("existePrecom", {async: false} );
           			
           			if($("#existePrecom").val() == 0){
           				editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ;
						//summamos el monto comprometido si es que hay
						$("#mComprometido").val('0');
						$("#mComprometido").formatCurrency();
           			}
           			else{
           				editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ;
						//summamos el monto comprometido si es que hay
						queryFormPost("sumaMontoPreCompromiso", {async: false });
						$("#mComprometido").val(Math.abs(quitaFmt($("#sumaImporteTotal").val())- quitaFmt($("#mPrecompromisoReal").val())));
						$("#mComprometido").formatCurrency();
           			}
           		           			
           		 	initPrecom=1;
					
					if(initSuf==1)	{	
						//Se encuentra En SAI con un contrato diverso y esta vigente
						var aTrsSuf = $('#dt_suficiencia').dataTable().fnGetNodes();
						var aTrsPrecom= $('#dt_preCompromiso').dataTable().fnGetNodes();
						autoAjuste(aTrsSuf,aTrsPrecom);
				 		initPrecom=0;
					}
				}
			});
	}
	
	
	function cargaContratoPrecomprometido() {
		var oTable=$("input").attr("readonly", true); 
 		$('#dt_preCompromiso').dataTable( {
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
			sScrollX: "100%",
			sScrollY: "150%",
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromiso( '" + vcontrato + "' , " + vfolio+" )",
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
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
				{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }]
			} ) ;
	}
	function guardarPrecomprimisoGeneral(){
		if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
			&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
			swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
			return;
		}
		var  imgAprobar='<%=imgAprobar%>';
		if (imgAprobar==0) {
			 //deshabilitamos el boton para que solo se le de click una vez.
			document.getElementById("imgAprobarpreCompromisoCont").disabled = true;
			//Verifica si ya existe algun precompromiso del contrato
			queryFormPost("existePrecom", {async: false} );
			
			if($("#existePrecom").val() == 0){
				//Condiciones para guardar un precompromiso
				//Se tiene que precomprometer por el monto total			
				if($("#mImporteTotal").val()!= $("#mComprometido").val()){
					swal("El importe total es mayor al monto pre-comprometido.",{icon:"info",button: "Cerrar"});
					document.getElementById("imgAprobarpreCompromisoCont").disabled = false;
					return -1;
				}
					
				//Limpia lista de folios
				$("#foliosCasoPreCompromiso").val("");
				queryFormPost("documentoFolioContratoDelete",{async:false});
				if ($("#nTipoPago").val() == '1') {
					//Contrato DESCENTRALIZADO
					//Valida que no existan faltantes o sobrantes por Unidad Ejecutora
					precompromisoDescentralizado();
				}
				else{
					//Contrato CENTRALIZADO
				   	$("#cEventoTmp").val("COMP_MAT");
				  
					var Aplicacion=guardarPrecompromiso();
					
					if(Aplicacion == "0"){
						//habilitamos el boton ya que hubo un error
						document.getElementById("imgAprobarpreCompromisoCont").disabled = false;
						return;
					}
					else if(Aplicacion == "1"){
						//Guarda realcion de precompromiso materiales con precompromiso financiero
						queryFormPost("precomMatPrecomFinanCreate",{async:false});
						$("#cEventoTmp").val("PRECOMMAT_DISP");
						if($("#cuentaDisponible").val()=="82109"){
							$("#cEventoTmp").val("R_PRECOMMAT_DISP");
						}
						queryFormPost("liberaDisponiblePrecompromisoRead",{async:false});
						if($("#liberaDisponible").val() > 0){
							$("#nFolioPreCompromiso").val("");
							guardarPrecompromisoDisponible();
							//Guarda relacion de precompromiso materiales con precompromiso financiero
							queryFormPost("precomMatPrecomFinanCreate",{async:false});
							queryFormPost("mDocumentoFolioPrecompromisoDelete",{async:false});
							$("#ccTem").val("<%=cCentroContable%>");
							$("#ueTem").val("<%=cUR%>");
							queryFormPost("mDocumentoFolioPrecompromisoCreate",{async:false});
						}
						
						document.getElementById("imgAprobarpreCompromisoCont").disabled = true;
						document.getElementById("imgDevolverpreCompromisoCont").disabled = false;
						//document.getElementById("trEditarpreCompromisoCont").style.display = "none";
						document.getElementById("edit").style.display = "none";
						//Bitácora
						$("#cAccion").val("APRUEBA_PRECOMPROMISO_MATERIALES");
						$("#cIdDocumento").val($("#cContratoDefinitivo").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						swal({
							title: "",
							text: "Contrato Precomprometido .",
							icon: "info",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								location.reload();
						});
						
					}
				}
			}
			else{
				$("#nIdEstado").val(3);
				vEstado=3;
				queryFormPost("mContratoPrecompromisoDesUpdate", { async: false });
				//Avanza el caso del precompromiso para que pueda ser visualizado en ventanilla
				queryFormPost("avanzaCasoVentanillaPrecompromisoContrato", {async: false });
				//Actualiza el folio definitivo en el(los) precompromiso(s)
				queryFormPost("updatePrecompromisoFolioDefinitivoContrato", {async: false });
				//Actualiza el folio definitivo en la tabla mDocumentoFolio
				queryFormPost("updateDocumentoFolioContrato", {async: false });
				
				swal({
					title: "",
					text: "DOCUMENTO ENVIADO A VENTANILLA.",
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						location.reload();
				});
			}
		} else {
			swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
		}
		
	}
	
	/*
    * Funcion que genera precompromiso para liberar dinero al disponible 
    * cuando es contrato centralizado
    */
    function guardarPrecompromisoDisponible(){
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val() == "")
			$.ajax({url: "../../servlet/ContratoServlet" , type:'post' , async: false,data: 'operacion=2', dataType: 'json', success: guardaFolio});		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val() == 0 || $("#nFolioPreCompromiso").val() == "")
			return -1;
		
		$("#cCentroContable").val( "<%=cCentroContable%>");
		//Guarda en una tabla auxiliar el encabezado del compromiso
		getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
			
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 );
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato);
		$("#cTipoContrato").val( "DI" );
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$("#caNoPreCompromiso").val( vcaNoCompromiso );
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 
		$("#fVigencia").val( $("#vigenciaVentanillaPrecom").val());
		
		queryFormPost("tPreCompromisoECreate", {async: false });
		
		queryFormPost("fn_mAptdPrcpDetalle", {async: false });
		queryFormPost("avanzaCasoLiberaPrecompromiso", {async: false });
    }
	
	function guardarPrecompromisoDes(cc, ur, numeroPrecompromisos, precomGenerar)
	{	
		var mensajeAp="";
		//Genera un nuevo folio siempre
		$.ajax({url: '../../servlet/ContratoServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val()==0)
			return -1;
		
		//Genera en una tabla auxiliar el detalle del precompromiso
		var vdocren = 1 ;
		//obtiene el número de filas de la tabla de precompromiso
		var nRows = $("#dt_preCompromiso tr").length -1 ;
		var oTable = $("#dt_preCompromiso").dataTable();
		var oTablD = $("#dt_grabaprecompD").dataTable();
		var aTrs = oTable.fnGetNodes();

		$("#cCentroContable").val(cc);
		//Guarda en una tabla auxiliar el encabezado del compromiso, aprovecha el #cCentroContable en el callback
		getNextSequenceVal({seqName: "CO-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
			
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7) ;
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato);
		$("#cTipoContrato").val( "DI" );
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val(ur);
		$("#caNoPreCompromiso").val( vcaNoCompromiso );
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 
		$("#fVigencia").val( $("#vigenciaVentanillaPrecom").val());
		
		//Genera y actualiza Encabezado
		queryFormPost("tPreCompromisoECreate,tPreCompromisoECreateTmpMateriales", {async: false });
		//Genera Encabezado y detalle
		var j,nColums=12-parseInt($("#mesDisponible").val(),10);
		oTablD.fnClearTable();
		var evento='PRECOM';
		if($("#cuentaDisponible").val()=="82109"){
			evento="R_PRECOM";
		}
		for ( var i=0 ; i<aTrs.length ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			if (jqInputs.length > 0  && ur.indexOf(aData[1].substring(0,3)) >= 0) {
				for ( j=0 ; j <= nColums ; j++ ) {
					var vep = aData[ 0 ] + "." + $.trim(aData[ 1 ]) ;
					var clv=$.trim(aData[0]);
					var ff=clv.substring(39, 40); 
					if(ff=='4' || $("#cuentaDisponible").val()=="82106"){
						evento="PRECOM";
					}else{
						evento="R_PRECOM";
					}
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
								
					if (parseFloat(vimporteP) != 0 ) {
						var vimporteN = vimporteP * -1 ; 
						vMes = parseInt($("#mesDisponible").val(),10)+j;
								$('#dt_grabaprecompD').dataTable().fnAddData( [
								'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + vdocren + '"></td>',
								'<td><input type="text" id="EP" name="EP" value="' + vep + '"></td>',
								'<td><input type="text" id="cEvento" name="cEvento" value="'+evento+'"></td>',
								'<td><input type="text" id="mImporte" name="mImporte" value="' + vimporteP + '"></td>',
								'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
								'<td><input type="text" id="nFolioPreCompromisoD" name="nFolioPreCompromisoD" value="' + $("#nFolioPreCompromiso").val()+ '"></td>',
								'<td><input type="text" id="cMes" name="nMesD" value="' + vMes + '"></td>',
								'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + cc + '"></td>'
							] );
							
						vdocren++ ;
						queryFormPost("tPreCompromisoDCreate", {async: false });
						oTablD.fnClearTable();
					}
				}
			}
		}
		
		//revisamos si es una copia del contrato
		queryFormPost("revisaCopiaPedido", {async: false});
		if(parseInt($("#estadoPartidaProcedimiento").val(),10) != 3 ){
			//Si exste un apartado, se agregan a Detalle las operaciones pertinentes
			if (quitaFmt($("#mPrecompromisoReal").val()) > 0){
				queryFormPost("fn_mAptdPrcpDetalleDes", {async: false });
			}
		}
		
		$("#ccTem").val(cc);
		$("#ueTem").val(ur);
		queryFormPost("deleteDocumentoFolioPrecompromiso", {async: false });
		queryFormPost("mDocumentoFolioPrecompromisoCreate", {async: false });
		queryFormPost("tPreCompromisoDCreateTmpMateriales", {async: false });
		
		//aplicacion contable
		$.ajax({url: "../../servlet/ContratoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#aEjercicioFiscal").val()+"&folioCaso="+$("#folioCaso").val()
		+"&numeroPrecompromisos="+numeroPrecompromisos+"&precomGenerar="+precomGenerar+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()
		+"&cIdContratoDefinitivo="+$("#cContratoDefinitivo").val() 	, type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				swal({
					title: "",
					text: mensajeAp,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						
				});
			}
		});
		
		//Agrega el folio a #foliosCasoPreCompromiso
		if ($("#foliosCasoPreCompromiso").val() != '') 
			$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val( $("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
		
		return mensajeAp;
	}
	
	function guardarPrecompromiso()
	{
		var mensajeAp="";
		var Aplica="0";
		
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val()=="")
			$.ajax({url: "../../servlet/ContratoServlet" , type:'post' , async: false,data:'operacion=2', dataType: 'json', success: guardaFolio});
		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val()==0 || $("#nFolioPreCompromiso").val()=="")
			return -1;
		
		//Genera en una tabla auxiliar el detalle del precompromiso
		var vdocren = 1 ;
		//obtiene el número de filas de la tabla de precompromiso
		var nRows = $("#dt_preCompromiso tr").length -1 ;
		var oTable = $("#dt_preCompromiso").dataTable();
		var oTablD = $("#dt_grabaprecompD").dataTable();
		var aTrs = oTable.fnGetNodes();

		$("#cCentroContable").val( "<%=cCentroContable%>"); 
		getNextSequenceVal({seqName: "CO-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
			
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 ) ;
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato);
		$("#cTipoContrato").val( "DI" );
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$("#caNoPreCompromiso").val( vcaNoCompromiso );
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 
		$("#fVigencia").val( $("#vigenciaVentanillaPrecom").val());
		
		//borra tablas temporales de materiales
		queryFormPost("tPreCompromisoDDeletetmpMateriales", {async: false });
		queryFormPost("tPreCompromisoEDeletetmpMateriales", {async: false });
		
		//Genera y actualiza Encabezado
		queryFormPost("tPreCompromisoECreate", {async: false });
		
		//inserta encabezado tabla temporal materiales
		queryFormPost("tPreCompromisoECreateTmpMateriales", {async: false });
		
		var j,nColums=12-parseInt($("#mesDisponible").val(),10);
		oTablD.fnClearTable();
		var evento='PRECOM';
		if($("#cuentaDisponible").val()=="82109"){
			evento="R_PRECOM";
		}
		for ( var i=0 ; i<aTrs.length ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			if (jqInputs.length > 0) {
				for ( j=0 ; j <= nColums ; j++ ) {
					var vep = aData[ 0 ] + "." + $.trim(aData[ 1 ]) ;
					var clv=$.trim(aData[0]);
					var ff=clv.substring(39, 40); 
					if(ff=='4' || $("#cuentaDisponible").val()=="82106"){
						evento="PRECOM";
					}else{
						evento="R_PRECOM";
					}
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
					if (parseFloat(vimporteP) != 0 ) {
						var vimporteN = vimporteP * -1 ; 
						vMes = parseInt($("#mesDisponible").val(),10)+j;
								$("#dt_grabaprecompD").dataTable().fnAddData( [
								'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + vdocren + '"></td>',
								'<td><input type="text" id="EP" name="EP" value="' + vep + '"></td>',
								'<td><input type="text" id="cEvento" name="cEvento" value="'+evento+'"></td>',
								'<td><input type="text" id="mImporte" name="mImporte" value="' + vimporteP + '"></td>',
								'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
								'<td><input type="text" id="nFolioPreCompromisoD" name="nFolioPreCompromisoD" value="' + $("#nFolioPreCompromiso").val()+ '"></td>',
								'<td><input type="text" id="cMes" name="nMesD" value="' + vMes + '"></td>',
								'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + "<%=cCentroContable%>" + '"></td>'
							] );
						vdocren++ ;
						queryFormPost("tPreCompromisoDCreate", {async: false });
						oTablD.fnClearTable();
					}
				}
			}
		}
			
		//revisamos si es una copia del contrato
		queryFormPost("revisaCopiaPedido", {async: false});
		if(parseInt($("#estadoPartidaProcedimiento").val(),10) != 3 ){
			//Si exste un precompromiso, se agregan a Detalle las operaciones pertinentes
			if (quitaFmt($("#mPrecompromisoReal").val()) > 0)
				queryFormPost("fn_mAptdPrcpDetalle", {async: false });
		}
			
		queryFormPost("tPreCompromisoDCreateTmpMateriales", {async: false });	
		
		$.ajax({url: "../../servlet/ContratoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#aEjercicioFiscal").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&folioCaso="+$("#folioCaso").val()
		+"&numeroPrecompromisos=1&precomGenerar=1" +"&cIdContratoDefinitivo="+$("#cContratoDefinitivo").val(), type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				Aplica=j[0].Aplica;
				swal({
					title: "",
					text: mensajeAp,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						
				});
			}
		});
			
		//Agrega el folio a #foliosCasoPreCompromiso
		if ($("#foliosCasoPreCompromiso").val() != '') 
			$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val( $("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
		
		return Aplica;
    }
	
	function devuelvePrecompromiso(){
		if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 || roles.toString().indexOf("JEFES")>=0 ||roles.indexOf("ANALISTA")>=0||parseInt($("#usuariosMismaUE").val(),10)==1) 
			&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
			swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
			return;
		}
		var imgDevolver='<%=imgDevolver%>';
		if (imgDevolver==0){
			//Vuelve a leer el estado del contrato para evitar conflictos con ventanilla y saber si el contrato fue aprobado
			queryFormPost("mContratoHeaderRead", {async: false});
			
			if($("#nIdEstado").val() != ("3")){
				swal("El contrato no se puede devolver por que su estado no lo permite.",{icon:"info",button: "Cerrar"});
				return;
			}
			
			//Valida cuantos precompromisos esta revisando en ventanilla.
			queryFormPost("precompromisosRevisionVentanilla", {async: false});
			
			if($("#precomRevisionUser").val() > 0){
				swal("No se puede devolver el precompromiso, el contrato lo esta revisando el usuario de ventanilla.",{icon:"info",button: "Cerrar"});
				return;
			}
			
			//Valida si existen compromisos generados.
			queryFormPost("compromisosGeneradosRead", {async: false});
			if($("#compromisosGenerados").val() > 0){
				swal("El contrato no se puede devolver por que ya tiene Compromisos generados.",{icon:"info",button: "Cerrar"});
				return;
			}
			
			var res=window.confirm("¿Está seguro que quiere devolver el pre-compromiso, el contrato se encuentra en VENTANILLA DE PAGOS y esta pendiente su aprobación?");
			
			if(res){
				//Actualiza el estatus del contrato
				$("#nIdEstado").val(2);
				queryFormPost("mContratoPrecompromisoDesUpdate", { async: false });
				//Retrocede el caso del contrato
				queryFormPost("retrocedeCasoVentanillaPrecompromisoContrato", {async: false });
				//Se cambia el documento definitivo por el folio del contrato en mDocumentoFolio
				queryFormPost("updateDocumentoFolioDefinitivoContrato", {async: false });
				//Actualiza el folio del contrato en el precompromiso
				queryFormPost("updatePrecompromisoFolioContrato", { async: false });
				guardaBitacora("DEVUELVE_PRE-COMPROMISO", $("#cContratoDefinitivo").val());
				
				location.reload();
			}else
				return;
		}
		else {
			swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
		}
	}
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
	function imprimeRes(j){
		var res=j[0].Contable1;
		swal(res,{icon:"info",button: "Cerrar"});
	}
	function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		swal({
	        	title: "",
	        	text: "Ha ocurrido un error al crear el caso, contacte a su soporte",
	        	icon: "info",
	        	buttons: {
	        		confirm : "Cerrar"
	        		},
	        	}).then((continuar) => {
	        		return -1;
	        });
        }else{
     	   $("#nFolioPreCompromiso").val(folioPre);
     	   $("#folioCasoPreCompromiso").val(folioCaso);

     	}
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
	   	return val
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
	function cambiafrmt( fld )	{
	   	var vcompr = $("#mComprometido").val();
	   	var vfld = $("#" + fld.id).val()
	   	if (vfld == "")
	   		vfld = '0';
		vcompr = quitaFmt( vcompr );
		vfld=quitaFmt(vfld);
	   	$("#mComprometido").val( parseFloat(vcompr) + parseFloat( vfld ) );
		$("#" + fld.id).formatCurrency();
		$("#mComprometido").formatCurrency();
	}
	function valSufic( fld ) { 
		var valor = $("#" + fld.id).val();

		valor=quitaFmt(valor);
		//validacion para que solo se permitan números
		if(isNaN(valor)){
			swal("Ingrese solo valores numéricos",{icon:"info",button: "Cerrar"});
			$("#" + fld.id).val( "0" );
			return false;
		}
		if(valor<0){
			swal("No se pueden ingresar valores negativos",{icon:"info",button: "Cerrar"});
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
	    	swal("No hay Suficicencia Mensual en la Clave Presupuestal.",{icon:"info",button: "Cerrar"});
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompr=parseFloat(vcompr);
		valor=parseFloat(valor);
		vimptot=parseFloat(vimptot);
		if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
	    	swal("El Compromiso Actual Excede al Saldo Compromiso.",{icon:"info",button: "Cerrar"});
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompT = vimptot-valor-vcompr;
		//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
		vcompT=vcompT.toFixed(2);
		$("#difPrecompromiso").val(parseFloat(vcompT));
		$("#difPrecompromiso").formatCurrency();
	}
	function editPrecompromiso( oTableLocal )
	{
	//funcion para agregar los inputs a la tabla
		var i,j,descMes, nomMes,nColumna;
		var aData;
		var aTrs = oTableLocal.fnGetNodes();
		for (i=1 ; i<=aTrs.length ; i++ ){
			mes=parseInt($("#mesDisponible").val(),10);
			if(mes==0)
				mes=mes+1;
		    aData = oTableLocal.fnGetData(i-1);
		    nColumna=aData.length-mes-1;//dos columnas al final ocultas}
		       for(j=2; j<nColumna; j++){
		    	nomMes='mes'+mes;
		    	descMes='mes'+mes+'-'+i;
		    	$("#dt_preCompromiso").children().children()[i].children[j].innerHTML = '<input style="width: 100%" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
		    	mes++;	
		    }
	    }
	}
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true 
	}
	function deshabilitaMeses(oTableLocal){
		var i=$("#mesDisponible").val();
		i=parseInt(i,10);
		for(i; i>1;i--)
			oTableLocal.fnSetColumnVis(i,false);
	}
	
	function setSequenceVal(seqValue) {
		seqValue = 100000 + parseInt(seqValue,10);
		//seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "CO" + $("#cEjercicio").val() + seqValue;
		
	}
	
	
	function cargaPreCompromisoVacioContrato(){
		var oTable=$("#dt_preCompromiso").dataTable({
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoDevueltoPrecom('" + vcontrato + "')",
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
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
           		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
           		
           		fnInitComplete: function(oSettings, json) {
           			queryFormPost("existePrecom", {async: false} );
           			
           			if($("#existePrecom").val() == 0){
           			 	editPrecompromiso($("#dt_preCompromiso").dataTable());
           			 	queryFormPost("sumaMontoPreCom", {async: false });
           			 	if(quitaFmt($("#sumaImporteTotal").val()) > 0){
	           			 	
	           			 	if($("#mPrecompromisoReal").val() != "" && quitaFmt($("#mPrecompromisoReal").val()) > 0){
	           			 		$("#mComprometido").val(quitaFmt($("#sumaImporteTotal").val()));
	           			 		$("#mComprometido").formatCurrency();
	           			 		$("#difPrecompromiso").val(parseFloat( parseFloat(quitaFmt($("#mImporteTotal").val())) - quitaFmt($("#mComprometido").val())));
								$("#difPrecompromiso").formatCurrency();
	           			 	}
	           			 	else{
	           			 		
	           			 		$("#mComprometido").val(quitaFmt($("#sumaImporteTotal").val()));
	           			 		$("#difPrecompromiso").val(parseFloat(quitaFmt($("#mImporteTotal").val())) - quitaFmt($("#sumaImporteTotal").val()) );
	           			 		$("#mComprometido").formatCurrency();
	           			 		$("#difPrecompromiso").formatCurrency();
	           			 	}
	           			 }
	           			 else{
           			 		if($("#mPrecompromisoReal").val() != "" && quitaFmt($("#mPrecompromisoReal").val()) > 0){
           			 			$("#mComprometido").val($("#mPrecompromisoReal").val());
           			 			$("#difPrecompromiso").val(parseFloat(quitaFmt($("#mImporteTotal").val())) - parseFloat(quitaFmt($("#mPrecompromisoReal").val())));
								$("#difPrecompromiso").formatCurrency();
	           			 		$("#mComprometido").formatCurrency();
           			 		}
           			 	}
           			}
           			else{
           				//$("#trEditarpreCompromisoCont").css("display","none");
           				$("#edit").css("display","none");
						//summamos el monto comprometido si es que hay
						queryFormPost("sumaMontoPreCompromiso", {async: false });
						var total=0;
					   	var j,nColums=12-parseInt($("#mesDisponible").val(),10);
					   	var aTrsPrecom= $("#dt_preCompromiso").dataTable().fnGetNodes();
						
						for ( var i=0 ; i<aTrsPrecom.length ; i++ ) {
							var aDataPrecomSuma = $("#dt_preCompromiso").dataTable().fnGetData(aTrsPrecom[i]);
							
							for ( j=0 ; j <= nColums ; j++ ) {
								var vimporteP = aDataPrecomSuma[j+2] ;
								vimporteP = quitaFmt(vimporteP);
								
								if (parseFloat(vimporteP) != 0 ) 
									total=parseFloat(total)+parseFloat(vimporteP);
							}
						}
						
						if($("#mPrecompromisoReal").val() != "" && quitaFmt($("#mPrecompromisoReal").val()) > 0)
							total = parseFloat(total) + parseFloat(quitaFmt($("#mPrecompromisoReal").val()));
						
						$("#mComprometido").val(total);
				  		$("#mComprometido").formatCurrency();
						$("#difPrecompromiso").val(quitaFmt($("#sumaImporteTotal").val()) - quitaFmt($("#mComprometido").val()));
						$("#difPrecompromiso").formatCurrency();
           			}
        		}
		});
	}
	
	function autoAjuste(aTrsSuf,aTrsPrecom){ 
 		if(parseInt(aTrsPrecom.length,10) == parseInt(aTrsSuf.length,10)){
	   		var mesDisp=parseInt($("#mesDisponible").val(),10)+1;
	   		var mesPrecom=14-parseInt($("#mesDisponible").val(),10);
	  		
	  		for(var j=0; j < aTrsSuf.length; j++ ){
		 		for(var i=mesDisp; i < 14 ; i++ ){
		   			var aDataPrecom = $('#dt_preCompromiso').dataTable().fnGetData(aTrsPrecom[j]);
		   			var aDataSuf = $('#dt_suficiencia').dataTable().fnGetData(aTrsSuf[j]);
		    		var precom=aDataPrecom[i];
		    		var suf=aDataSuf[mesDisp];
		   			
		     		if( parseFloat(quitaFmt(precom)) > parseFloat(quitaFmt(suf)) ){
		        		$("#mes"+(i-1)+"-"+(j+1)+"").val('0.00');		       
		   	  		}
		    		mesDisp ++;
	  		 	}
	   		}
	   
		   	var total=0;
		   	var j,nColums=12-parseInt($("#mesDisponible").val(),10);
		   	
			for ( var i=0 ; i<aTrsPrecom.length ; i++ ) {
				var aDataPrecomSuma = $('#dt_preCompromiso').dataTable().fnGetData(aTrsPrecom[i]);
				var jqInputs = $('input', aTrsPrecom[i] );
				
				for ( j=0 ; j <= nColums ; j++ ) {
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
					if (parseFloat(vimporteP) != 0 ) {
						 total=parseFloat(total)+parseFloat(vimporteP);
					}
				}
			}
	   	
	   		$("#mComprometido").val(quitaFmt($("#mComprometido").val())+total);
			$("#mComprometido").formatCurrency();
	   	}	  
	}
	
	//Valida la Unidad Ejecutora con que se creara el precompromiso para el centro contable 10
	function validaUR_CC10(){
		var aTrs = oTableUrcc.fnGetNodes();
		var line;
		var lineDelete = new Array();
		var totalURCC10 = 0;
		var data = new Array();
		
		for (var i = 0; i < aTrs.length; i++) {
			line = oTableUrcc.fnGetData(aTrs[i]);
			data.push(line);
		}
		return data;
	}
	
	function precompromisoDescentralizado(){
		var oTable = $("#dt_preCompromiso").dataTable();
		var aTrs = oTable.fnGetNodes();
		var data = validaUR_CC10();
		var precomGenerar = 0;
		var cc;
		var ur;
					
		//borra tablas temporales de materiales
		queryFormPost("tPreCompromisoDDeletetmpMateriales,tPreCompromisoEDeletetmpMateriales", {async: false });
		//queryFormPost("tPreCompromisoEDeletetmpMateriales", {async: false });
					
		for (var i = 0; i < data.length; i++){
			line = data[i];
			cc = line[0];
			ur = line[1];
			precomGenerar++;
			$("#cEventoTmp").val("COMP_MAT");
			var mensaje = guardarPrecompromisoDes(cc, ur, data.length, precomGenerar);
			
			if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE") < 0){
				$("#nIdEstado").val(2);
				vEstado = 2;
				queryFormPost("mUpdatePrecompromisoContrato", {async: false });
				queryFormPost("tPreCompromisoDDelete", {async: false });
				queryFormPost("tPreCompromisoEDelete", {async: false });
					
				//borra tablas temporales de materiales
				queryFormPost("tPreCompromisoDDeletetmpMateriales", {async: false });
				queryFormPost("tPreCompromisoEDeletetmpMateriales", {async: false });
							
				//habilitamos el boton ya que hubo un error
				document.getElementById("imgAprobarpreCompromisoCont").disabled = false;
				return;
			}
			else{
				//Guarda relacion de precompromiso materiales con precompromiso financiero
				queryFormPost("precomMatPrecomFinanCreate",{async:false});
				$("#nFolioPreCompromiso").val("");
				$("#cCentroContable").val(cc);
				$("#nFolioPreCompromisoE").val(0);
			}
		}
		
		$("#nFolioPreCompromiso").val("");
		$("#cEventoTmp").val("PRECOMMAT_DISP");
		if($("#cuentaDisponible").val()=="82109"){
			$("#cEventoTmp").val("R_PRECOMMAT_DISP");
		}
		queryFormPost("liberaDisponiblePrecompromisoRead",{async:false});
						
		if($("#liberaDisponible").val() > 0){
			guardarPrecompromisoDesDisponible(cc, ur);
			//Guarda relacion de precompromiso materiales con precompromiso financiero
			queryFormPost("precomMatPrecomFinanCreate",{async:false});
		}
		swal("Contrato Precomprometido con folio(s) " + $("#foliosCasoPreCompromiso").val(),{icon:"info",button: "Cerrar"});
		location.reload();
	}
	
	/*
    * Funcion que genera precompromiso para liberar dinero al disponible 
    * cuando es contrato descentralizado
    */
    function guardarPrecompromisoDesDisponible(cc, ur){
		var mensajeAp="";
		//Genera un nuevo folio siempre
		$.ajax({url: '../../servlet/ContratoServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val() == 0)
			return -1;

		//Guarda en una tabla auxiliar el encabezado del compromiso, aprovecha el #cCentroContable en el callback
		getNextSequenceVal({seqName: "CO-" + cc, async: false, callback: setSequenceVal});
			
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 );
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato);
		$("#cTipoContrato").val( "DI" );
		$("#cRamo").val( "<%=cRamo%>");
		$("#cUnidadResponsable").val(ur); //ur recibida
		$("#caNoPreCompromiso").val(vcaNoCompromiso);
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val(nMes);
		$("#fVigencia").val($("#vigenciaVentanillaPrecom").val());

		//Genera y actualiza Encabezado
		queryFormPost("tPreCompromisoECreate", {
			async : false
		});
		queryFormPost("fn_mAptdPrcpDetalleDes", {
			async : false
		});
		queryFormPost("mDocumentoFolioPrecompromisoCreate", {
			async : false
		});
		queryFormPost("avanzaCasoLiberaPrecompromiso", {
			async : false
		});
	}

	//Obtiene las Unidades Ejecutoras del Centro Contable indicado de la tabla dt_urcc
	function obtieneURCCTable(cc) {
		var aTrs = oTableUrcc.fnGetNodes();
		var aData;
		var urs = "";

		for ( var i = 0; i < aTrs.length; i++) {
			aData = oTableUrcc.fnGetData(aTrs[i]);

			if (aData[0] == cc)
				urs += aData[1] + ",";
		}

		if (urs.length > 0)
			urs = urs.substring(0, urs.length - 1);

		return urs;
	}
	function guardarcomprimisoGeneral() {
		document.getElementById("imgAprobarCompromisoCont").disabled = true;
		if (!(roles.toString().indexOf("ADMIN_RECMAT") >= 0)
				&& !(roles.toString().indexOf("JEFES") >= 0)
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())) {
			swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
			document.getElementById("imgAprobarCompromisoCont").disabled = false;
			return;
		}
		if ($("#cnumCompranet").val() == null
				|| $("#cnumCompranet").val() == '') {
			swal("Falta agregar el No. de Contrato Compranet, favor de agregarlo.",{icon:"info",button: "Cerrar"});
			document.getElementById("imgAprobarCompromisoCont").disabled = false;
			return;
		}
		if (($("#nCodExpedienteCNET").val() == null
				|| $("#nCodExpedienteCNET").val() == ''|| $("#nCodExpedienteCNET").val() == 0)&& parseInt($("#nIdCategoria").val(),10)!=11) {
			swal("Falta agregar el Codigo de Expediente Compranet, favor de agregarlo.",{icon:"info",button: "Cerrar"});
			document.getElementById("imgAprobarCompromisoCont").disabled = false;
			return;
		}
		if (($("#nCodContratoCNET").val() == null
				|| $("#nCodContratoCNET").val() == ''|| $("#nCodContratoCNET").val() == 0)&& parseInt($("#nIdCategoria").val(),10)!=11) {
			swal("Falta agregar el Codigo de Contrato Compranet, favor de agregarlo.",{icon:"info",button: "Cerrar"});
			document.getElementById("imgAprobarCompromisoCont").disabled = false;
			return;
		}
		//Validación de contratos plurianuales y campos obligatorios 
		if ($("#isPlurianual").val() == 1
				&& ($("#oficioDG").val() == "" || $("#folioMASCP").val() == "")) {
			swal("Falta agregar el Oficio DG ó el Folio MASCP, favor de agregarlo.",{icon:"info",button: "Cerrar"});
			return;
		}
		if($("#lEsPSP").val()==0 && $("#datoPrestacionServicio").val()==0){
			swal("Falta agregar el lugar de prestación del servicio o entrega de los bienes.",{icon:"warning",button: "Cerrar"});
			return;
		}
		$("#cIdContratoH").val($("#cContratoDefinitivo").val());
		$("#rfcProv").val($("#cIdRFC").val());
		//return;
		var proc = "" + $("#cEjercicio").val() + "," + $("#cIdContratoH").val()
				+ "," + $("#rfcProv").val() + "";

		if ($("#origen").val() == 'PEDIDO MODIFICADO'
				|| $("#origen").val() == 'CONTRATO MODIFICADO'
				|| $("#origen").val() == 'CONTRATO AMPLIACION'
				|| $("#origen").val() == 'PEDIDO AMPLIACION') {

			$
					.getJSON(
							"../../servlet/PedidoServlet?operacion=10",
							{
								Tabla : "",
								Param : proc,
								MaxReg : "",
								ajax : 'false'
							},
							function(j) {
								switch (j[0].Col1) {
								case "0":
									onSubmit();
									break;
								case "1":
									swal("Este proveedor no está dado de alta en los beneficiarios",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("Este proveedor no está dado de alta en los beneficiarios");
									break;
								case "2":
									swal("Las fechas no respetan el orden",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("Las fechas no respetan el orden fechaDocumento<fechaFormalización<fechaInicio<=fechaFin");
									break;
								case "3":
									swal("Los montos no coinciden con la suma total",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("Los montos no coinciden con la suma total (montoBruto+motoIVA=montoTotal)");
									break;
								case "-1":
									swal("No se pudo realizar la validacion intentelo nuevamente por favor",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("No se pudo realizar la validacion intentelo nuevamente por favor");
									break;
								}
							});

		} else {
			$
					.getJSON(
							"../../servlet/PedidoServlet?operacion=5",
							{
								Tabla : "",
								Param : proc,
								MaxReg : "",
								ajax : 'false'
							},
							function(j) {
								switch (j[0].Col1) {
								case "0":
									onSubmit();
									break;
								case "1":
									swal("Este proveedor no se está dado de alta en los beneficiarios",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("Este proveedor no se está dado de alta en los beneficiarios");
									break;
								case "2":
									swal("Las fechas no respetan el orden",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("Las fechas no respetan el orden fechaDocumento<fechaFormalización<fechaInicio<=fechaFin");
									break;
								case "3":
									swal("Los montos no coinciden con la suma total",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("Los montos no coinciden con la suma total (montoBruto+motoIVA=montoTotal)");
									break;
								case "-1":
									swal("No se pudo realizar la validacion intentelo nuevamente por favor",{icon:"info",button: "Cerrar"});
									$("#cMotivoDevolucion").val("No se pudo realizar la validacion intentelo nuevamente por favor");
									break;

								}

							});
			document.getElementById("imgAprobarCompromisoCont").disabled = false;
		}
		//case 7 aplicaContablementeCompromiso
		//liberaSaldo
		queryFormPost("actualizacionFechasPcontratoDiverso", {
			async : false
		});
		
	}
	function onSubmit() {

		var mensaje = "";
		//  se valida que existan anticipos y amortizaciones

		queryFormPost("AnticipoPreCompromisosRead", {
			async : false
		});
		queryFormPost("retencionesDiverso", {
			async : false
		});

		if ($("#cantRetenciones").val() == 0
				|| $("#nPorcAsignacion").val() == '0'
				|| $("#nPorcAmortizacion").val() == '0') {
			if (!confirm("Favor de revisar tus retenciones, anticipos o Amortizaciones.\nSelecciona cancelar para revisar en la pestaña anticipos y retenciones.\nDesea generar el Compromiso?")) {
				$("#imgAprobarCompromisoCont").css("display", "block");
				return;
			}
		}

		try {

			//llama a la aplicacion contable
			$("#tipoOperacion").val('ACTUALIZA');
			mensaje = fnTerminaAplicacionCon();
			if (mensaje.toString().toUpperCase().indexOf(
					"DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE") < 0) {
				//Regresa el Contrato
				$("#nIdEstado").val(3);
				vEstado = 3;
				$("#tipoOperacion").val('RECHAZA');
				queryFormPost(
						"pa_actualizaEstadoVentanillaPedidoContratoCreate", {
							async : false
						});
				queryFormPost('tCompromisoDDelete', {
					async : false
				});
				queryFormPost('tCompromisoEDelete', {
					async : false
				});
				return;
			} else {
				//Valisa si existe algun precompromiso (sin aplicar) de pedido o contrato que libera saldo a disponible
				if ($("#origen").val() == "PEDIDO") {
					$.ajax({
						url : "../../servlet/PedidoServlet?cEjercicio="
								+ $("#cEjercicio").val(),
						type : 'post',
						async : false,
						data : "operacion=13&pedidoDefinitivo="
								+ $("#cIdContratoH").val(),
						dataType : 'json',
						success : function(j) {
						}
					});
				}
				if ($("#origen").val() == "CONTRATO") {
					$.ajax({
						url : "../../servlet/ContratoServlet?cEjercicio="
								+ $("#cEjercicio").val(),
						type : 'post',
						async : false,
						data : "operacion=11&contratoDefinitivo="
								+ $("#cIdContratoH").val(),
						dataType : 'json',
						success : function(j) {
						}
					});
				}
			}

			//Bitácora
			$("#cAccion").val("APRUEBA_PRECOMPROMISO_FINANCIERO");
			$("#cIdDocumento").val($("#cIdContratoH").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", {
				async : false
			});
			queryFormPost({
				queryName: "sp_RegistraCNET",//CRUD ACTUALIZA CNET PCONTRATODIVERSO
							async: false,
							callback: function() {
									swal("Se ha creado el Compromiso con folio " + $("#FOLIO").val(),{icon:"info",button: "Cerrar"});
									location.reload();	
	   						}
	   		});
			

			//Refresca la pagina
		} catch (e) {
			swal("onSubmit: Error: " + e.message,{icon:"error",button: "Cerrar"});
			//return false;
		}

	}
	function fnTerminaAplicacionCon() {
		//aplicacion contable
		var mensajeAp = "";
		var arrayCompromiso = new Array();
		var oper = 7;
		if ($("#nTipoPago").val() == '1') {
			oper = 14;
		}
		$.ajax({
			url : '../../servlet/PedidoServlet?nFolioPreCompromiso='
					+ $("#nFolioPreCompromiso").val() + "&cEjercicio="
					+ $("#cEjercicio").val() + "&nFolioPreCompromisoPRCP="
					+ $("#nFolioPreCompromisoPRCP").val(),
			type : 'post',
			async : false,
			data : 'operacion=' + oper + '&contratoDefinitivo='
					+ $("#cIdContratoH").val() + '&origen='
					+ $("#origen").val() + '&tipoOperacion='
					+ $("#tipoOperacion").val() + '&cuentaDispRadicado='
					+ $("#cuentaDisponible").val(),
			dataType : 'json',
			success :
			//$.ajax({url: '../../servlet/PedidoServlet?nFolioCompromiso='+$("#nFolioCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=7', dataType: 'json', success: 
			function(j) {
				mensajeAp = j[0].Contable1;
				$("#FOLIO").val(j[0].FolioCompromiso);
				arrayCompromiso = j[0].FolioCompromiso.split('-');
				$("#nFolioCompromiso").val(arrayCompromiso[2]);
				swal({
					title: "",
					text: mensajeAp,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
				}).then((continuar) => {
					
				});
			}
		});
		return mensajeAp;
	}
</script>
</head>
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0"
	topmargin="0">
	<form>
		<div id="container" class="container" style="width: 95%">
			<fieldset >
				<legend>Pre-Compromiso del Contrato</legend>
				<table align="left" cellpadding="2" width="100%">
					<tr>
						<td align="right" colspan="2">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" title="Genera pre-compromiso"	id="imgAprobarpreCompromisoCont" 	name="imgAprobarpreCompromisoCont" 	value="Pre-Comprometer" onclick="guardarPrecomprimisoGeneral();"	/>
							<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" title="Autorizar pre-compromiso"	id="imgAprobarCompromisoCont" 	name="imgAprobarCompromisoCont" 	value="Autorizar" onclick="guardarcomprimisoGeneral();"	/>
							<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverpreCompromisoCont" 	name="imgDevolverpreCompromisoCont" 	value="Devolver" onclick="devuelvePrecompromiso();"/>
							<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Contratos.jsp?tab=0';" />
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent; color:red" id="lblAutorizaSICOP"
							name="lblAutorizaSICOP" readonly
							/>
						</td>
					</tr>
					<tr id="trDuplicidad" align="left">
							<td align="left" colspan="2">
								<textarea id="cadenaDuplicidad" name="cadenaDuplicidad" rows="5" cols="100" style="border: 0px solid black;color: red" readonly="readonly"></textarea>
							</td>
						</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora"
							name="lblUnidadEjecutora" readonly
							/>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento"
							id="lblProcedimiento" readonly
							/>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo"
							readonly />
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblContrato" id="lblContrato"
							readonly  />
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor"
							readonly  />
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly
							/>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblSubtotal" id="lblSubtotal"
							readonly  />
						</td>
					</tr>
					<tr id="divImporteIVA">
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA"
							readonly  />
						</td>
					</tr>
					<tr id="divImporteImpuesto1">
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto1"
							id="lblImporteImpuesto1" readonly
							/>
						</td>
					</tr>
					<tr id="divImporteImpuesto2">
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto2"
							id="lblImporteImpuesto2" readonly
							/>
						</td>
					</tr>
					<tr id="divImporteImpuesto3">
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto3"
							id="lblImporteImpuesto3" readonly
							 />
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly
							 />
						</td>
					</tr>
					<tr id="divFCarga">
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblfCarga" id="lblfCarga" readonly
							 /></td>
					</tr>
					<tr id="divFVigencia">
						<td align="left" colspan="2"><input type="text"
							style="width: 600px;border-width:0; background-color:transparent" name="lblfVigencia" id="lblfVigencia"
							readonly />
						</td>
					</tr>
					<tr align="left" id="textAutorizaContrto" style="display: none;">
						<td>
							<p style="color: red; ">Solicita a tú Jefe la autorización
								del contrato.</p></td>
					</tr>
				</table>
				<table style="width: 100%">
					<tr>
						<td align="left">Monto Contrato: <input
							style="text-align:right;" readonly type="text" maxlength="12"
							size="12" name="mImporteTotal" id="mImporteTotal" value="0"
							disabled="disabled">
						</td>
						<td align="left">Compromiso Actual: <input
							style="text-align:right;" readonly type="text" maxlength="12"
							size="12" name="mPreComprometer" id="mComprometido" value="0"
							disabled="disabled">
						</td>
						<td align="left">Saldo Compromiso: <input
							style="text-align:right;" readonly type="text" maxlength="12"
							size="12" name="mPreComprometido" id="difPrecompromiso"
							value="0" disabled="disabled">
						</td>
					</tr>
					<tr id="precompromisoNotice">
						<td colspan="3"><span style="color:#33CC00;">Monto
								cubierto por el precompromiso: <span id="precomReal"></span>.</span>
						</td>
					</tr>
					<tr id="trEditarpreCompromisoCont" style="display: none">
						<td align="left">
							<!--<a href="javascript:void(0)" id="edit">editar</a>--> <input
							type="button" name="edit" id="edit" size="5" value="Editar" /></td>
					</tr>
				</table>
				<table id="dt_preCompromiso" class="display" style="width: 100%">
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
				<table id="dt_suficiencia" class="display" style="width: 100%">
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
							<th>Contrato</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<table id="dt_grabaprecompE" style="visibility: hidden">
					<thead>
						<tr align="center">
							<th>nFolioPreCompromisoE</th>
							<th>aEjercicioFiscal</th>
							<th>fCarga</th>
							<th>fAplicacion</th>
							<th>cIdContrato</th>
							<th>cTipoContrato</th>
							<th>cCentroContable</th>
							<th>cRamo</th>
							<th>cUnidadResponsable</th>
							<th>caNoPreCompromiso</th>
							<th>nEnviadoSICOP</th>
							<th>nMes</th>
							<th>fVigencia</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="text" id="nFolioPreCompromisoE"
								name="nFolioPreCompromisoE">
							</td>
							<td><input type="text" id="aEjercicioFiscal"
								name="aEjercicioFiscal">
							</td>
							<td><input type="text" id="fCarga" name="fCarga">
							</td>
							<td><input type="text" id="fAplicacion" name="fAplicacion">
							</td>
							<td><input type="text" id="cIdContrato" name="cIdContrato">
							</td>
							<td><input type="text" id="cTipoContrato"
								name="cTipoContrato">
							</td>
							<td><input type="text" id="cCentroContable"
								name="cCentroContable">
							</td>
							<td><input type="text" id="cRamo" name="cRamo" value="16">
							</td>
							<td><input type="text" id="cUnidadResponsable"
								name="cUnidadResponsable">
							</td>
							<td><input type="text" id="caNoPreCompromiso"
								name="caNoPreCompromiso">
							</td>
							<td><input type="text" id="nEnviadoSICOP"
								name="nEnviadoSICOP">
							</td>
							<td><input type="text" id="nMes" name="nMes">
							</td>
							<td><input type="text" id="fVigencia" name="fVigencia">
							</td>
						</tr>
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
				<div style="width: 0px; height: 0px;display: none;">
					<table id="dt_urcc" style=" width: 0px;" width="0px">
						<thead style="width: 0px">
							<tr align="center" style="width: 0px">
								<th>CentroContable</th>
								<th>UnidadEjecutira</th>
							</tr>
						</thead>
					</table>
				</div>
			</fieldset>
			<!-- Hidden Section -->
			<!-- Valores de caratula -->
			<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato"
				value="<%=cIdTipoContrato%>" /> <input type="hidden"
				name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"
				value="<%=cIdUnidadEjecutora%>" /> <input type="hidden"
				name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario"
				value="<%=usuario.getU_UR()%>" /> <input type="hidden"
				name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" /> <input
				type="hidden" name="nIdConsecutivo" id="nIdConsecutivo"
				value="<%=nIdConsecutivo%>" /> <input type="hidden"
				name="mesDisponible" id="mesDisponible" value="1" /> <input
				type="hidden" id="cDocumento" name="cDocumento"
				value="PRECOMPROMISO"> <input type="hidden" name="nIdEstado"
				id="nIdEstado" /> <input type="hidden" name="cIdRFC" id="cIdRFC" />
			<input type="hidden" name="nTipoPago" id="nTipoPago" /> <input
				type="hidden" name="centralizado" id="centralizado" /> <input
				type="hidden" name="mTotalContrato" id="mTotalContrato" /> <input
				type="hidden" name="cIdConsolidado" id="cIdConsolidado" />

			<!-- Valores de Precompromiso -->
			<input type="hidden" name="esPosiblePrecomprometer"
				id="esPosiblePrecomprometer" /> <input type="hidden"
				name="folioCasoPreCompromiso" id="folioCasoPreCompromiso" /> <input
				type="hidden" name="foliosCasoPreCompromiso"
				id="foliosCasoPreCompromiso" /> <input type="hidden"
				name="cContratoDefinitivo" id="cContratoDefinitivo" /> <input
				type="hidden" name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" />
			<input type="hidden" name="cIdUsuarioResponsable"
				id="cIdUsuarioResponsable" value="Usuario"> <input
				type="hidden" id="rowsAffected" name="rowsAffected" value="0">
			<input type="hidden" id="numcomp" name="numcomp" value="0"> <input
				type="hidden" id="nPorcAsignacion" name="nPorcAsignacion" value="0">
			<input type="hidden" id="mImporteAnticipo" name="mImporteAnticipo"
				value="0"> <input type="hidden" id="mImporteAnticipoIVA"
				name="mImporteAnticipoIVA" value="0"> <input type="hidden"
				id="mTotalAnticipo" name="mTotalAnticipo" value="0"> <input
				type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
			<input type="hidden" id="nFolioPreCompromiso"
				name="nFolioPreCompromiso" value="-1"> <input type="hidden"
				id="cDescripcionPoliza" name="cDescripcionPoliza"
				value="REGISTRO DEL PRECOMPROMISO FOLIO"> <input
				type="hidden" id="cIdEntidadContable1" name="cIdEntidadContable1"
				value="<%=cCentroContable%>">
			<!-- Valores de Caso -->
			<input type="hidden" name="ID_CASO" id="ID_CASO" /> <input
				type="hidden" name="ID_TC" id="ID_TC" /> <input type="hidden"
				name="ID_CASO_OPER" id="ID_CASO_OPER" /> <input type="hidden"
				name="folioCaso" id="folioCaso" />
			<!--  Valores de Configuración -->
			<input type="hidden" name="GP_NOMBRE" id="GP_NOMBRE"
				value="desfase_mes_activo_precompromiso" /> <input type="hidden"
				name="GP_VALOR" id="GP_VALOR" /> <input type="hidden"
				name="responsable" id="responsable" /> <input type="hidden"
				name="operacion" id="operacion" /> <input type="hidden"
				name="tipoCaso" id="tipoCaso" value="14" />

			<!-- Para unir apartado con precompromiso -->
			<input type="hidden" name="mPrecompromisoReal"
				id="mPrecompromisoReal" /> <input type="hidden"
				name="cIdProcedimiento" id="cIdProcedimiento" /> <input
				type="hidden" name="sumaImporteTotal" id="sumaImporteTotal" /> <input
				type="hidden" name="existePrecom" id="existePrecom" /> <input
				type="hidden" name="vigenciaVentanillaPrecom"
				id="vigenciaVentanillaPrecom" /> <input name="cAccion" id="cAccion"
				type="hidden"> <input name="cIdDocumento" id="cIdDocumento"
				type="hidden"> <input type="hidden" name="cIdUsuario"
				id="cIdUsuario" value="<%=usuario.getLogin()%>" /> <input
				name="sumaApartadoActual" id="sumaApartadoActual" type="hidden">
			<input name="formatCurrencyTem" id="formatCurrencyTem" type="hidden">
			<input type="hidden" name="importeCellTem" id="importeCellTem" /> <input
				name="ccTem" id="ccTem" type="hidden" /> <input name="ueTem"
				id="ueTem" type="hidden" /> <input name="precomRevisionUser"
				id="precomRevisionUser" type="hidden" /> <input
				name="compromisosGenerados" id="compromisosGenerados" type="hidden" />
			<input type="hidden" name="cEventoTmp" id="cEventoTmp" /> <input
				type="hidden" name="liberaDisponible" id="liberaDisponible" /> <input
				type="hidden" name="estadoPartidaProcedimiento"
				id="estadoPartidaProcedimiento" /> <input type="hidden"
				name="cIdContratoH" id="cIdContratoH" /> <input type="hidden"
				name="rfcProv" id="rfcProv" /> <input type="hidden" name="origen"
				id="origen" value="CONTRATO"/>
			<!-- 				<input type="hidden" name="nFolioPreCompromiso" id="nFolioPreCompromiso"/> -->
			<input type="hidden" name="nFolioPreCompromisoPRCP"
				id="nFolioPreCompromisoPRCP" /> <input type="hidden"
				name="nPorcAmortizacion" id="nPorcAmortizacion" /> <input
				type="hidden" name="nPorcAsignacion" id="nPorcAsignacion" /> <input
				type="hidden" name="cantRetenciones" id="cantRetenciones" value="0" />
			<input type="hidden" name="tipoOperacion" id="tipoOperacion" /> <input
				type="hidden" name="FOLIO" id="FOLIO" /> <input name="FECHA_CARGA"
				type="hidden" id="FECHA_CARGA" value="<%=today%>" /> <input
				name="nFolioCompromiso" type="hidden" id="nFolioCompromiso" value="" />
			<input type="hidden" name="U_LOGIN" id="U_LOGIN"
				value="<%=usuario.getLogin()%>" /> <input type="hidden"
				name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" /> <input
				type="hidden" name="cnumCompranet" id="cnumCompranet" /> 
				<input
				type="hidden" name="nCodExpedienteCNET" id="nCodExpedienteCNET" />
				<input
				type="hidden" name="nCodContratoCNET" id="nCodContratoCNET" />
				<input
				type="hidden" name="cuentaDisponible" id="cuentaDisponible"
				value="82106" /> <input name="isPlurianual" id="isPlurianual"
				type="hidden" /> <input name="oficioDG" id="oficioDG" type="hidden" />
			<input name="folioMASCP" id="folioMASCP" type="hidden" />
			<input name="nIdCategoria" id="nIdCategoria" type="hidden" value="0"/>
			<input name="nIdConsecutivoAdj" id="nIdConsecutivoAdj" type="hidden" value="0"/>
			<input name="datoPrestacionServicio" id="datoPrestacionServicio" type="hidden" value="0"/>
			<input name="lEsPSP" id="lEsPSP" type="hidden" value="0"/>
			
		</div>
	</form>
</body>
</html>
