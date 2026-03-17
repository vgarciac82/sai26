
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
    String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	c2.add(Calendar.DATE, 8);
	String vigencia=sdf.format(c2.getTime());
	String today= sdf.format(c1.getTime());
	int mesActual=c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont=false;
	String grupoMat;
	boolean bGrupo=true;
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	/*
	Map grupo=usuario.getGrupos();
	Iterator it2 = grupo.entrySet().iterator();
	while(it2.hasNext()){
	Map.Entry r = (Map.Entry)it2.next();
	grupoMat=(String)r.getKey();
	if("RECURSOS_MATERIALES".equalsIgnoreCase(grupoMat)){
	bGrupo=true;
	break;
		}
	
	}
	
	*/
	
	String name_user=usuario.getLogin();
	String idRol="0";		
	Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
			
	String cEjercicio = "";
	String cIdPedido= "";
	String cIdUnidadEjecutora = "";
	String cIdPedidoDefinitivo = "";
	String cIdTipoCambio = "";
	String cITipoPedido = "";
	//String cIdSubPartida="";
	
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicioPasivo) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicioPasivo);
		cIdPedido= (String)session.getAttribute(GestionInterface.ATT_PedidoPasivo);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjecPasivo);
		cIdPedidoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_PedidoPasivoDefinitivo);
		cIdTipoCambio = (String)session.getAttribute(GestionInterface. ATT_tipoCambioPasivoPedido);
		cITipoPedido = (String)session.getAttribute(GestionInterface. ATT_tipoContratoPasivoPedido);
		//cIdSubPartida = (String)session.getAttribute(GestionInterface. ATT_SubpartidaPasivo);
		
		
		
		System.out.println("tipo contrato " +cITipoPedido + cIdPedidoDefinitivo+ " "+cEjercicio+"  "+ cIdPedido+ " "+  cIdUnidadEjecutora+ " ");
						
	}else 
		response.sendRedirect("PasivosPedido.jsp?tab=0");
	
	
	
	
	
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	String cAplicaDocto="No";
	if ( request.getParameter("aplicaDocto")!= null && request.getParameter("aplicaDocto").equals("Si"))
		cAplicaDocto="Si";
	//Valida Centro de Costos
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
        cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Genera Pre-Compromiso</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
	<script type="text/javascript" src="../js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
<script type="text/javascript" charset="utf-8">
	var vcontrato;
	var vfolio;
	var vEstado;
	var vVigente;
	var vcaNoCompromiso;
	$(document).ready(function() {
		<%
			    String role="";
				//botones
				NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int editar=0;
				int imgAprobar=0;
				int imgDevolver=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map botones=nb.getBotones(role,"Contratos","preCompromisoContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarpreCompromisoCont".equals(img)){  
						imgAprobar=1;
					}
					if ("imgDevolverpreCompromisoCont".equals(img)){
						imgDevolver=1; 
					}
					if ("trEditarpreCompromisoCont".equals(img)){ 
						editar=1; 
					}	
				}		
			%>
	    var nEditing = null;
		initTables();
		clickHandlers();
		//para evitar el uso de BackSpace
		setReadOnly();
		//Carga los datos de inicio
		initQueries();
		//Guarda en variables globales el contrato, folio, estado y si es vigente
		vcontrato=$("#cContratoDefinitivo").val();
		vfolio=$("#nFolioPreCompromiso").val();
		vEstado=parseInt($("#nIdEstado").val(),10);
		
		//temporal para no validar los 8 dias
		//vVigente=$("#esPosiblePrecomprometer").val(); 
		
		vVigente=1;
		
		if(vEstado ==3 || vEstado ==4  ){
		//En caso que sea un contrato con un preCompromiso ya hecho
			cargaContratoPrecomprometido();
			$("#mComprometido").val($("#mImporteTotal").val());
		}
		else //Carga la tabla con valores 0
			cargaPreCompromisoVacio();
		setInitConditions();
		//Carga la tabla que tiene el monto de cada EP
		cargaSuficiencias();



		var offset=parseInt($("#mesDisponible").val(),10)+parseInt($("#GP_VALOR").val(),10);
		if(offset>12 || offset <0){
			alert("El desfase del calendario está mal configurado, favor de avisar al administrador");
			window.location ="Pasivos.jsp?tab=0";
		}else
			$("#mesDisponible").val(offset);
		//Solo se puede precomprometer para el mes siguiente al actual
		deshabilitaMeses($('#dt_preCompromiso').dataTable());
		deshabilitaMeses($('#dt_suficiencia').dataTable());
		$("#mComprometido").formatCurrency();
		$("#mImporteTotal").formatCurrency();
		$("#difPrecompromiso").formatCurrency();
	});
	function setInitConditions(){
		if(vEstado==3 ||vEstado==4 || vVigente==0){
			
			//Se deshabilita el boton de aprobar en caso que ya exista un precompromiso
			document.getElementById("imgAprobarpreCompromisoCont").disabled = true;
			$('#dt_preCompromiso').attr('disabled', true);
		}
		if(vEstado != 3){
			//En caso el estado del contrato sea aprobado, falta por confirmar que hacer en los otros casos
			document.getElementById("imgDevolverpreCompromisoCont").disabled = true;
		}
		
		if(vVigente==0){
			alert("No es posible crear más precompromisos porque todavía exiten precompromisos del área caducos");
		}
	}
	function clickHandlers(){
		$('#edit').click( function () {
	       	var nRow = editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
	   	} );
	}
	function initQueries(){
	
	   		queryFormPost("mContratoPasivosPedidoCaratulaRead", {async: false});
			queryFormPost("fnMontoNetoPedidoPasivoPresupuestoRead", {async: false});
			queryFormPost("fnMontosPedidoPresupuestoPasivoRead", {async: false});
			queryFormPost("fn_vigenciaValida", {async: false});
			queryFormPost("TipoPolizaRead", {async: false});
			//Lee la configuración de mes
			queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
			//verifica si hay motivo rechazo
			queryFormPost("motivoRechazoPasivo", { async:false });
			
			if($("#nIdEstado").val()!="5"){
			$("#trNotas").hide();
			}
			
	}
	function actualizaCaratula(){
		queryFormPost("mContratoPasivosPedidoCaratulaRead", {async: false});
	}
	function setReadOnly(){
		document.getElementById("lblUnidadEjecutora").style.readonly=true;
		document.getElementById("lblDefinitivo").style.readonly=true;
		document.getElementById("lblProveedor").style.readonly=true;
		document.getElementById("lblEstado").style.readonly=true;
		document.getElementById("lblTotal").style.readonly=true;
		document.getElementById("mComprometido").style.readonly=true;
		document.getElementById("mImporteTotal").style.readonly=true;
		document.getElementById("difPrecompromiso").style.readonly=true;
		document.getElementById("lblNotas").style.readonly=true;				
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
		$('#dt_grabaprecompD').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false } );
		$('#dt_grabaprecompD').attr('visible', false);
		
		loadURCC();
		$('#dt_urcc').attr('visible', false);
		
	}
	
	function loadURCC(){
		var campos = "   1=1  and cIdContratoDefinitivo= '"+ $("#cContratoDefinitivo").val()+"'" ;
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneCentroContable&qw="+campos,
				aoColumns: [
					{ sName: "centroContable"},
					{ sName: "ur" },
					{ sName: "cIdContratoDefinitivo" }
				]
		}) ;
	}
	
	
	
	
	
	function cargaSuficiencias(){
		var qw= " 1=1 and cIdContrato='"+vcontrato+"'" ;
		
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleContratoEP&qw=" + qw,
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
				 //Se encuentra En SAI con un contrato diverso y esta vigente
				if(vEstado ==2 || vEstado==5 && vVigente==1){
					var editar='<%=editar%>';
					if (editar==0){
						document.getElementById("trEditarpreCompromisoCont").style.display="table-row";
					}
				}
				}
            });
	}
	
	
			function cargaPreCompromisoVacio(){
			var qw= " 1=1 and cIdContrato='"+vcontrato+"'" ;
			
			var oTable=$('#dt_preCompromiso').dataTable( {
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw="+ qw ,
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
	function cargaContratoPrecomprometido() {
	 if ($("#nTipoPago").val() == '0') {
	 var qw= "'"+ vcontrato + "'"+"," + "'" + vfolio+"'" ;
	 
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
					}
				},
				bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 100,
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromiso(" +qw+")",
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
	 else{
	 var qw= "'"+ vcontrato + "'";
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
					}
				},
				bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 100,
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoDes(" +qw+")",
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
	}
	
	
	///////////////////////////////////////
	
	
	    
	function guardarPrecomprimisoGeneral(){
		var  imgAprobar='<%=imgAprobar%>';
		if (imgAprobar==0) {
		//Condiciones para guardar un precompromiso
			//Se tiene que precomprometer por el monto total
			if($("#mImporteTotal").val()!= $("#mComprometido").val()){
				alert("El importe total es mayor al monto pre-comprometido");
				return -1;
			}
			
			//Limpia lista de folios
			$("#foliosCasoPreCompromiso").val('');
						
			 //deshabilitamos el boton para que solo se le de click una vez.
			document.getElementById("imgAprobarpreCompromisoCont").disabled = true;
			
			
			if ($("#nTipoPago").val() == '1') {
				var aTrs = oTableUrcc.fnGetNodes();
				for (var i = 0; i < aTrs.length; i++) {
					line = oTableUrcc.fnGetData(aTrs[i])
					var cc = line[0];
					var ur = line[1];
					var mensaje=guardarPrecompromisoDes(cc, ur);
					if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE")<0){
					
					$("#nIdEstado").val(2);
					vEstado=2;
					queryFormPost("mContratoPasivoPedidoPrecompromisoUpdate", {async: false });
					queryFormPost('tPreCompromisoDDelete', {async: false });
					queryFormPost('tPreCompromisoEDelete', {async: false });
					
					 //habilitamos el boton ya que hubo un error
					document.getElementById("imgAprobarpreCompromisoCont").disabled = false;
					return;
					}
						
				}
				
				$("#nIdEstado").val(3);
				vEstado=3;
				//queryFormPost("mPedidoPrecompromisoDesUpdate", { async: false });
			}
			else{
			    var Aplicacion=guardarPrecompromiso();
			  	if(Aplicacion!="1")
				{
				//habilitamos el boton ya que hubo un error
				document.getElementById("imgAprobarpreCompromisoPasivoContrato").disabled = false;
				return;			
				}
				
			
			}
			
			$('#dt_grabaprecompD').attr('visible', false);
			$('#dt_preCompromiso').attr('disabled', true);
			document.getElementById("imgAprobarpreCompromisoCont").disabled = true;
			document.getElementById("imgDevolverpreCompromisoCont").disabled = false;
			document.getElementById("trEditarpreCompromisoCont").style.display = "none";
			actualizaCaratula();
			
			//Bitácora
			$("#cAccion").val("APRUEBA_PRECOMPROMISO_PASIVOCONTRATO");
			$("#cIdDocumento").val($("#cContratoDefinitivo").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			alert("Pasivo Pedido Precomprometido con folio(s) " + $("#foliosCasoPreCompromiso").val());
			
		} else {
			alert("No tiene permisos para realizar esta acción");
		}
	}
	

		function guardarPrecompromisoDes(cc, ur,nRows,oTable,oTablD,aTrs,vdocren)
		{	
				//Genera un nuevo folio en caso de que no lo tenga
				//if($("#nFolioPreCompromiso").val()==""){
				$.ajax({url: '../../servlet/PedidoPasivoServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
				//}
			
			
			//En caso de cualquier error al obtener el folio, sale de la función
			if($("#nFolioPreCompromiso").val()==0)
				return -1;
			//Guarda en una tabla auxiliar el encabezado del compromiso
			//var vcons = "000000" +$("#nFolioPreCompromiso").val();
			
			getNextSequenceVal({seqName: "CO-" +cc, async: false, callback: setSequenceVal});
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
			$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
			$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
			$("#fCarga").val( "<%=today%>" ); 
			$("#fAplicacion").val("<%=today%>" ); 
			$("#cIdContrato").val(vcontrato);
			$("#cTipoContrato").val( "DI" );
			$("#cCentroContable").val(cc); 
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val(ur);
			$("#caNoPreCompromiso").val( vcaNoCompromiso );
			$("#nEnviadoSICOP").val("0");
			$("#nMes").val( nMes ); 
			$("#fVigencia").val( "<%=vigencia%>" );
			//Genera Encabezado y detalle
			queryFormPost('tPreCompromisoECreate', {async: false });
			var j,nColums=12-parseInt($("#mesDisponible").val(),10);
			oTablD.fnClearTable();
			for ( var i=0 ; i<nRows ; i++ ) {
				var aData = oTable.fnGetData( i );
				//se obtiene unidad ejecutora del datatable
				var unidadEjec=$.trim(aData[1].substring(0,3))
				// Checa si la unidad ejecutora corresponde a la que se le envia para que se 
				if(unidadEjec==ur){
				
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
									'<td><input type="text" id="cEvento" name="cEvento" value="PRECOM"></td>',
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
			
			      } // if checa ur  si no es igual continua el for
			
		}
			//En caso que no haya ningun valor en el calendario
			if (vdocren == 1) {
				alert("No se ha Calendarizado ningun Compromiso");
				return -1;
			} 
			
						
			//Actualiza la tabla de Contrato con los folio y el nuevo estado
			queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
			queryFormPost("mDocumentoFolioPrecompromisoCreate", {async: false });
			
			
			//aplicacion contable
			$.ajax({url: '../../servlet/PedidoPasivoServlet' , type:'post' , async: false,data:'operacion=3', dataType: 'json', success: imprimeRes});
			
			
		if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
		
		
	}
	
	
		function guardarPrecompromiso()
		{	
			
			 var mensajeAp="";
		     var Aplica="0";
									
			//Genera un nuevo folio en caso de que no lo tenga
			
			//if($("#nFolioPreCompromiso").val()==""){
				$.ajax({url: '../../servlet/PedidoPasivoServlet' , type:'post' , async: false,data:'operacion=2', dataType: 'json', success: guardaFolio});
			//}
			//En caso de cualquier error al obtener el folio, sale de la función
			if($("#nFolioPreCompromiso").val()==0)
				return -1;
			//Genera en una tabla auxiliar el detalle del precompromiso
			var vdocren = 1 ;
			//obtiene el número de filas de la tabla de precompromiso
			var nRows = $("#dt_preCompromiso tr").length -1 ;
			var oTable = $('#dt_preCompromiso').dataTable();
			var oTablD = $('#dt_grabaprecompD').dataTable();
			var aTrs = oTable.fnGetNodes();
			
			
			getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
				
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
			$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
			$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
			$("#fCarga").val( "<%=today%>" ); 
			$("#fAplicacion").val("<%=today%>" ); 
			$("#cIdContrato").val(vcontrato);
			$("#cTipoContrato").val( "DI" );
			$("#cCentroContable").val( "<%=cCentroContable%>"); 
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val( "<%=cUR%>" );
			$("#caNoPreCompromiso").val( vcaNoCompromiso );
			$("#nEnviadoSICOP").val("0");
			$("#nMes").val( nMes ); 
			$("#fVigencia").val( "<%=vigencia%>" );
			//Genera Encabezado y detalle
			queryFormPost('tPreCompromisoECreate', {async: false });
			var j,nColums=12-parseInt($("#mesDisponible").val(),10);
			oTablD.fnClearTable();
			for ( var i=0 ; i<nRows ; i++ ) {
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
									'<td><input type="text" id="cEvento" name="cEvento" value="PRECOM"></td>',
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
		
		 	//En caso que no haya ningun valor en el calendario
			if (vdocren == 1) {
				alert("No se ha Calendarizado ningun Compromiso");
				return -1;
			} 
			
			
			//aplicacion contable
			$.ajax({url: '../../servlet/PedidoPasivoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#aEjercicioFiscal").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&cContratoDefinitivo="+$("#cContratoDefinitivo").val(), type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				Aplica=j[0].Aplica;
				alert(mensajeAp);
				}
		});
		
		
		//Agrega el folio a #foliosCasoPreCompromiso
		if ($("#foliosCasoPreCompromiso").val() != '') 
			$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());

       
		//return mensajeAp;
		return Aplica;
			
		
	}
	
	
	
	
	
		function devuelvePrecompromiso(){
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver==0){
				//Vuelve a leer el estado del contrato para evitar conflictos con ventanilla y saber si el pedido fue aprobado
				queryFormPost("mContratoPasivosContratoCaratulaRead", {async: false});
				if($("#nIdEstado").val()==("2")){
			alert("El Pedido Pasivo ha sido rechazado por el usuario de ventanilla");
			//se tiene que actualizar co_responsable en la tabla cg_caso_operacion para que ya no aparezca en el inbox
			queryFormPost("actualizaCoResponsableRead", {async: false});
			//actualizaCaratula();
			window.location = "PasivosPedido.jsp?tab=" + 3;
			return;
			}
			//primero checamos si el contrato ya esta siendo trabajado por el usuario de ventanilla
			queryFormPost("usuarioVentanillaReadContratoPasivoPedido", {async: false});
			if($("#operacion").val()==2 && $("#responsable").val()!="VENTANILLA_PRECOMPROMISO")
			{
				alert("No se puede eliminar el precompromiso, el pedido lo esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val());
				return;
			}		 
		if($("#nIdEstado").val()!=("4")){
				//if($("#nIdEstado").val()==("3"))
			var res=window.confirm("¿Está seguro que quiere eliminar el pre-compromiso, el pedido se encuentra en VENTANILLA DE PAGOS y esta pendiente su aprobación,esta acción no se puede deshacer?");
			//Elimina el Precompromiso
		if(res){
		 $.ajax({url: '../../servlet/PedidoPasivoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&cContratoDefinitivo="+$("#cContratoDefinitivo").val(), type:'post' , async: false,data:'operacion=4&tipoPago=' + $("#nTipoPago").val(), dataType: 'json', success: eliminaPrecompromiso});
	        		
		}else
			return;
			
		}else{
			actualizaCaratula();
			alert("El Pedido Pasivo no se puede devolver porque ya ha sido APROBADO y esta COMPROMETIDO");
		}


		}
		
	}
	
	
	function eliminaPrecompromiso(j){
		var mensaje=j[0].Contable1;
				var devuelve=j[0].Devuelve;
				alert(mensaje);
	            if(devuelve!="1"){
					document.getElementById("imgDevolverpreCompromisoCont").disabled = false;
					return;
					}
						
	      	if ($("#nTipoPago").val() == '1') 
			queryFormPost('mDocumentoFolioPrecompromisoDeletePasivo', {async: false });
			else 
			var editar='<%=editar%>';
			if (editar==0){
				document.getElementById("trEditarpreCompromisoCont").style.display="table-row";
			}
	     
	      
	       $('#dt_preCompromiso').attr('disabled', false);
			document.getElementById("imgAprobarpreCompromisoCont").disabled = false;
			document.getElementById("imgDevolverpreCompromisoCont").disabled = true;
			editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
			//Bitácora
			$("#cAccion").val("DEVUELVE_PRECOMPROMISO_PASIVOPEDIDO");
			$("#cIdDocumento").val($("#cContratoDefinitivo").val());
			 queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			 actualizaCaratula();
			 window.location = "PasivosPedido.jsp?tab=" + 3;
	     
	}
	
	
	function imprimeRes(j){
		if ($("#nTipoPago").val() == '0') {
			var res=j[0].Contable1;
			alert(res);
		}
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
		    nColumna=aData.length-mes-1;//dos columnas al final ocultas
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
		vcaNoCompromiso = "<%=cCentroContable%>" + "CO" + $("#cEjercicio").val() + seqValue;
		
	}
	
	
	
</script>
</head>
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
<form>
	<div id="container" class="container">
		<table align="left" width="750px">
				<tr><td>
						<fieldset style="width:750px" align="left">
						<legend>Pre-Compromiso de los Pedidos Pasivos</legend>
							<table align="left" cellpadding="2" width="100%">
						    	<tr>
						    		<td align="right" colspan="2">
						    			<img id="imgAprobarpreCompromisoCont" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="guardarPrecomprimisoGeneral();" />&nbsp;Pre-Comprometer
						    			<img id="imgDevolverpreCompromisoCont" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devuelvePrecompromiso();"/>&nbsp;Devolver
										<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="window.location = 'Contratos.jsp?tab=0';"/>&nbsp;Salir
						    		</td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
						    	</tr>
						    		<tr>
						    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblDefinitivo" id="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
						    	</tr>
						    	
						    	<tr id="trNotas">
									<td align="left" colspan="2" >
										<label>Motivo del rechazo:</label>
										<textarea rows="3" cols="1" style="color:red; width: 500px;" name="lblNotas" id="lblNotas" readonly
											style="border-width:0; background-color:transparent"> </textarea>
											
									</td>
								</tr>
						    								    	
						    </table>
						</fieldset>
					</td></tr>
					<tr><td align="left"><table>
							<tr>
								<td align="left">Monto Contrato: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mImporteTotal"  id="mImporteTotal" value="0" disabled="disabled"></td>
								<td align="left">Compromiso Actual: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mPreComprometer"  id="mComprometido" value="0" disabled="disabled"></td>
								<td align="left">Saldo Compromiso: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mPreComprometido"  id="difPrecompromiso" value="0" disabled="disabled"></td>
							</tr> 
					</table></td></tr>
					<tr id="trEditarpreCompromisoCont" style="display: none"><td align="left" ><a href="javascript:void(0)" id="edit">editar</a></td></tr>
					<tr><td align="left" style="width: 740px"><table id="dt_preCompromiso" class="display" style="width: 740px">
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
			       					 </table></td></tr>
					<tr><td><br/><br/></td> </tr>			       						
					<tr><td align="left" style="width: 740px"><table id="dt_suficiencia" class="display" style="width: 740px">
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
			 						</table></td></tr>		
						<tr><td><table id="dt_grabaprecompE" style="visibility: hidden">
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
													<td><input type="text" id="nFolioPreCompromisoE" name="nFolioPreCompromisoE"></td>
													<td><input type="text" id="aEjercicioFiscal" name="aEjercicioFiscal"></td>
													<td><input type="text" id="fCarga" name="fCarga"></td>
													<td><input type="text" id="fAplicacion" name="fAplicacion"></td>
													<td><input type="text" id="cIdContrato" name="cIdContrato"></td>
													<td><input type="text" id="cTipoContrato" name="cTipoContrato"></td>
													<td><input type="text" id="cCentroContable" name="cCentroContable"></td>
													<td><input type="text" id="cRamo" name="cRamo" value="16"></td>
													<td><input type="text" id="cUnidadResponsable" name="cUnidadResponsable"></td>
													<td><input type="text" id="caNoPreCompromiso" name="caNoPreCompromiso"></td>
													<td><input type="text" id="nEnviadoSICOP" name="nEnviadoSICOP"></td>
													<td><input type="text" id="nMes" name="nMes"></td>
													<td><input type="text" id="fVigencia" name="fVigencia"></td>
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
										
									<div style="width:0px; height: 0px;" >
									<table id="dt_urcc" style="visibility: hidden; width: 0px;" width="0px" >
										<thead style="width:0px" >
											<tr align="center" style="width:0px" >
												<th >CentroContable</th>
												<th >UnidadEjecutora</th>
											</tr>
										</thead>
									</table>
									</div>
							
										
							</td>
							
							
							
							
							</tr>
							
							
				
				</table>
				
			    
			      <!-- Hiddens de sesion -->
	        <input type="hidden" name="cIdContratoMat" id="cIdContratoMat" value="<%=cIdPedido%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	         <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	         <input type="hidden" name="cIdTipoCambio" id="cIdTipoCambio" value="<%=cIdTipoCambio%>"/>
		     <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%= cIdPedidoDefinitivo %>"/>
		     <input type="hidden" name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" value="<%= cIdPedidoDefinitivo %>"   />
		     <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cITipoPedido%>"/>
			 
			     <input type="hidden" name="mensajeFinanciero" id="mensajeFinanciero" />
			     <input type="hidden" name="mesDisponible" id="mesDisponible" value="<%=mesActual%>"/>
			     
				 <input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMPROMISO">
			    <input type="hidden" name="nIdEstado" id="nIdEstado" />
			    <!-- Valores de Precompromiso -->
			    <input type="hidden" name="esPosiblePrecomprometer" id="esPosiblePrecomprometer" />
			    <input type="hidden" name="folioCasoPreCompromiso" id="folioCasoPreCompromiso" />
			    <input type="hidden" id="foliosCasoPreCompromiso" name="foliosCasoPreCompromiso" value="">
			  	<input type="hidden" name="cIdUsuarioResponsable" id="cIdUsuarioResponsable" value="Usuario">
				<input type="hidden" id="rowsAffected" name="rowsAffected" value="0">
				<input type="hidden" id="numcomp" name="numcomp" value="0">
				<input type="hidden" id="nPorcAsignacion" name="nPorcAsignacion" value="0">
				<input type="hidden" id="mImporteAnticipo" name="mImporteAnticipo" value="0">
				<input type="hidden" id="mImporteAnticipoIVA" name="mImporteAnticipoIVA" value="0">
				<input type="hidden" id="mTotalAnticipo" name="mTotalAnticipo" value="0">	
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
				<input type="hidden" id="nFolioPreCompromiso" name="nFolioPreCompromiso" value="-1">
				
				
				<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" value="REGISTRO DEL PRECOMPROMISO FOLIO">
				<!-- Valores de Caso -->
				<input type="hidden" name="ID_CASO" id="ID_CASO" />
				<input type="hidden" name="ID_TC" id="ID_TC" />
				<input type="hidden" name="ID_CASO_OPER" id="ID_CASO_OPER" />
				<!--  Valores de Configuración -->
				<input type="hidden" name="GP_NOMBRE" id="GP_NOMBRE" value="desfase_mes_activo_precompromiso"/>
				<input type="hidden" name="GP_VALOR" id="GP_VALOR" />
				<input type="hidden" name="responsable" id="responsable" />
				<input type="hidden" name="operacion" id="operacion" />
				<input type="hidden" name="tipoCaso" id="tipoCaso" value="14" />
				<input type="hidden" name="nTipoPago" id="nTipoPago" value=""/>
				
				<input name="cAccion" id="cAccion" type="hidden">
				<input name="cIdDocumento" id="cIdDocumento" type="hidden">
				<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>	
				
				
				
				
				
				

		</div>
	</form>
</body>
</html>
