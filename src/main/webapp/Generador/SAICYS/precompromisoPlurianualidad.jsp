<%@ page language="java" pageEncoding="UTF-8"%>
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
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
    String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	c2.add(Calendar.DATE, 8);
	String vigencia=sdf.format(c2.getTime());
	String today= sdf.format(c1.getTime());
	int mesActual=1;//c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont=false;
	boolean bGrupo=true;
	
	
	String name_user=usuario.getLogin();
	String idRol="0";		
	Map rol =usuario.getRoles();
	
			
	String cEjercicio = "";
	String cIdContrato= "";
	String cIdUnidadEjecutora = "";
	String cIdContratoDefinitivo = "";
	String cIdTipoCambio = "";
	String cITipoContrato = "";
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoPlurianual);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjecPlurianual);
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
		cIdTipoCambio = (String)session.getAttribute(GestionInterface. ATT_tipoCambioPlurianual);
		cITipoContrato = (String)session.getAttribute(GestionInterface. ATT_tipoContratoPlurianual);
							
	}else 
		response.sendRedirect("Plurianualidad.jsp?tab=0");
	
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
<!doctype html>
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
	var vVigente;
	var vcaNoCompromiso;
	var roles='';
	$(document).ready(function() {
		tabb=4;
		showAndHideTabs();
		<%
			    String roles="";
				//botones
				NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int editar=0;
				int imgAprobar=0;
				int imgDevolver=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					//role=(String)r.getKey();
					roles += r.getKey().toString() + ",";
				}
				if (roles.length() > 0) {
					roles = roles.substring(0, roles.length() - 1);
				}
				Map botones=nb.getBotones(roles,"PlurianualidadContratos","precompromisoPlurianualidad");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarpreCompromisoPluriContrato".equals(img)){  
						%>
						$("#<%=img%>").attr("disabled", false);<%
						imgAprobar=1;
					}
					if ("imgDevolverpreCompromisoPluriContrato".equals(img)){
						%>
						$("#<%=img%>").attr("disabled", false);<%
						imgDevolver=1; 
					}
					if ("trEditarpreCompromisoPlurContrato".equals(img)){
						editar=1; 
					}	
				}		
			%>
		roles="<%=roles%>";
	    var nEditing = null;
		initTables();
		clickHandlers();
		//Carga los datos de inicio
		initQueries();
		//Guarda en variables globales el contrato, folio, estado y si es vigente
		vcontrato=$("#cContratoDefinitivo").val();
		vfolio=$("#nFolioPreCompromiso").val();
		vEstado=parseInt($("#nIdEstado").val(),10);
		
		//temporal para no validar los 8 dias
		//vVigente=$("#esPosiblePrecomprometer").val(); 
		
		vVigente=1;
		
		if(vEstado ==3 || vEstado ==4){
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
			swal({
				title: "",
				text: "El desfase del calendario est\u00e1 mal configurado, favor de avisar al administrador",
				icon: "warm",
				buttons: {
					confirm : "Cerrar"
					},
				}).then((continuar) => {
					window.location ="Contratos.jsp?tab=0";
			});
		}else
			$("#mesDisponible").val(offset);
		//Solo se puede precomprometer para el mes siguiente al actual
		deshabilitaMeses($('#dt_preCompromiso').dataTable());
		deshabilitaMeses($('#dt_suficiencia').dataTable());
		$("#mComprometido").formatCurrency();
		$("#mImporteTotal").formatCurrency();
		$("#difPrecompromiso").formatCurrency();
		showHideButtons();
	});
	function setInitConditions(){
		if(vEstado==3 ||vEstado==4 || vVigente==0){
			
			//Se deshabilita el boton de aprobar en caso que ya exista un precompromiso
			document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = true;
			$('#dt_preCompromiso').attr('disabled', true);
		}
		if(vEstado != 3){
			//En caso el estado del contrato sea aprobado, falta por confirmar que hacer en los otros casos
			document.getElementById("imgDevolverpreCompromisoPluriContrato").disabled = true;
		}
		
		if(vVigente==0){
			swal("No es posible crear m\u00e1s precompromisos porque todav\u00eda exiten precompromisos caducados del \u00e1rea.",{icon:"warning",button: "Cerrar"});
		}
	}
	function clickHandlers(){
		$('#edit').click( function () {
	       	var nRow = editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
	   	} );
	}
	function initQueries(){
			queryFormPost("mContratoPlurianualidadCaratulaRead", {async: false});
	   		queryFormPost("datosContratoPlurianual", {async: false});
			
			
			//queryFormPost("cg_roleRead", {async: false});
			queryFormPost("fnMontoNetoContratoPlurianualPresupuestoRead", {async: false});
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
		queryFormPost("mContratoPlurianualidadCaratulaRead", {async: false});
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
		var campos = "   1=1  and cIdContratoDefinitivo= '"+ $("#cContratoDefinitivo").val()+"'" + " and cEjercicio= '"+ $("#cEjercicio").val()+"'";
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleContratoEP&qw=cIdContrato='" + vcontrato 
			+ "' and cuentaDisp='"+ + $("#cuentaDisponible").val()+"'" ,
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
						document.getElementById("trEditarpreCompromisoPlurContrato").style.display="table-row";
					}
				}
			}	
            });
	}
	
	
	function cargaPreCompromisoVacio(){


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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + vcontrato + "'",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromiso( " + qw +" )",
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

	
	
	
	function guardarPrecomprimisoGeneral(){
		var  imgAprobar='<%=imgAprobar%>';
		if (imgAprobar==0) {
		
			//Condiciones para guardar un precompromiso
			//Se tiene que precomprometer por el monto total
			if($("#mImporteTotal").val()!= $("#mComprometido").val()){
				swal("El importe total es mayor al monto pre-comprometido.",{icon:"warning",button: "Cerrar"});
				return -1;
			}
			
			//Limpia lista de folios
			$("#foliosCasoPreCompromiso").val('');
						
			 //deshabilitamos el boton para que solo se le de click una vez.
			document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = true;
						
			if ($("#nTipoPago").val() == '1') {
				var aTrs = oTableUrcc.fnGetNodes();
				for (var i = 0; i < aTrs.length; i++) {
					line = oTableUrcc.fnGetData(aTrs[i]);
					var cc = line[0];
					var ur = line[1];
					var mensaje=guardarPrecompromisoDes(cc, ur);
					if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE")<0){
					
					$("#nIdEstado").val(2);
					vEstado=2;
					queryFormPost("mContratoPlurianualPrecompromisoUpdate", {async: false });
					queryFormPost('tPreCompromisoDDelete', {async: false });
					queryFormPost('tPreCompromisoEDelete', {async: false });
					
					 //habilitamos el boton ya que hubo un error
					document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = false;
					return;
					}
						
				}
				
				$("#nIdEstado").val(3);
				vEstado=3;
				//queryFormPost("mPedidoPrecompromisoDesUpdate", { async: false });
			}
			else{
			    
			   	var Aplicacion=guardarPrecompromiso();
			  	
				//if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE")<0)
				if(Aplicacion!="1")
				{
				//habilitamos el boton ya que hubo un error
				document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = false;
				return;			
				}
				
			
			}
			
			$('#dt_grabaprecompD').attr('visible', false);
			$('#dt_preCompromiso').attr('disabled', true);
			document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = true;
			document.getElementById("imgDevolverpreCompromisoPluriContrato").disabled = false;
			document.getElementById("trEditarpreCompromisoPlurContrato").style.display = "none";
			actualizaCaratula();
			
			//Bitácora
			$("#cAccion").val("APRUEBA_PRECOMPROMISO_PLURIANUALIDADCONTRATO");
			$("#cIdDocumento").val($("#cContratoDefinitivo").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			swal("Plurianualidad Contrato Precomprometida con folio(s) " + $("#foliosCasoPreCompromiso").val(),{icon:"warning",button: "Cerrar"});
			
		} else {
			swal("No tiene permisos para realizar esta acci\u00f3n",{icon:"warning",button: "Cerrar"});
		}
	}
	 
	 
	 
	
	function guardarPrecompromisoDes(cc, ur,nRows,oTable,oTablD,aTrs,vdocren)
		{	
				//Genera un nuevo folio en caso de que no lo tenga
				//if($("#nFolioPreCompromiso").val()==""){
				$.ajax({url: '../../servlet/ContratoPlurianualidadServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
				//}
			
			
			//En caso de cualquier error al obtener el folio, sale de la función
			if($("#nFolioPreCompromiso").val()==0)
				return -1;
			//Guarda en una tabla auxiliar el encabezado del compromiso
			//var vcons = "000000" +$("#nFolioPreCompromiso").val();
			getNextSequenceVal({seqName: "CO-" +cc, async: false, callback: setSequenceVal});
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
			var oTablD = $('#dt_grabaprecompD').dataTable();
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
				var unidadEjec=$.trim(aData[1].substring(0,3));
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
									'<td><input type="text" id="cMes" name="nMesD" value="' + vMes + '"></td>'
									,'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + $("#cCentroContable").val()+ '"></td>'
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
				swal("No se ha Calendarizado ningun Compromiso.",{icon:"warning",button: "Cerrar"});
				return -1;
			} 
			
		//Actualiza la tabla de Contrato con los folio y el nuevo estado
			queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
			queryFormPost("mDocumentoFolioPrecompromisoCreate", {async: false });
			
			
			//aplicacion contable
			$.ajax({url: '../../servlet/ContratoPlurianualidadServlet' , type:'post' , async: false,data:'operacion=3', dataType: 'json', success: imprimeRes});
			
			
		if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
		
		
	}
	

		function guardarPrecompromiso(){	
	
		     var mensajeAp="";
		     var Aplica="0";
	    			
		     //Genera un nuevo folio en caso de que no lo tenga
			$.ajax({url: '../../servlet/ContratoPlurianualidadServlet' , type:'post' , async: false,data:'operacion=2', dataType: 'json', success: guardaFolio});
			
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
				swal("No se ha Calendarizado ningun Compromiso.",{icon:"warning",button: "Cerrar"});
				return -1;
			} 
		
		//aplicacion contable
			$.ajax({url: '../../servlet/ContratoPlurianualidadServlet?nFolioPrecompromiso='+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#aEjercicioFiscal").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&cContratoDefinitivo="+$("#cContratoDefinitivo").val(), type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				Aplica=j[0].Aplica;
				swal(mensajeAp,{icon:"success",button: "Cerrar"});
			}
		});
		
		
		//Agrega el folio a #foliosCasoPreCompromiso
		if ($("#foliosCasoPreCompromiso").val() != '') 
			$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());

       
		return Aplica;
			
		
		}
		
	
	function devuelvePrecompromiso(){
		
		var imgDevolver='<%=imgDevolver%>';
		if (imgDevolver==0){
			if(parseInt($("#nIdEstado").val(),10)==3){
				swal({
					title: "",
					text: "¿Está seguro que quiere eliminar el pre-compromiso?",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						document.getElementById("imgDevolverpreCompromisoPluriContrato").disabled = true;
					    $.ajax({url: '../../servlet/ContratoPlurianualidadServlet?cIdContratoDefinitivo='+$("#cIdContratoDefinitivoPlurianual").val(), type:'post' , async: false,data:'operacion=11' , dataType: 'json'
					    , success: 
					    	function(j){
								var status=j[0].PRECOM;
								if(status=="true"){
									swal({
										title: "",
										text: j[0].STATUS+"\n"+j[0].FOLIOS,
										icon: "success",
										buttons: {
											confirm : "Cerrar"
											},
										}).then((continuar) => {
											window.location = "Plurianualidad.jsp?tab=" + 4;
									});
								}else{
									swal({
										title: "",
										text: j[0].STATUS,
										icon: "success",
										buttons: {
											confirm : "Cerrar"
											},
										}).then((continuar) => {
											window.location = "Plurianualidad.jsp?tab=" + 4;
									});
								}
							}
						});
					}
				});
			}else{
				swal("El Contrato Plurianual no se puede devolver.",{icon:"warning",button: "Cerrar"});
			}
		}else{
				swal("No tiene permisos para realizar esta acci\u00f3n, contacte a su administrador",{icon:"warning",button: "Cerrar"});
		}
	}
	function eliminaPrecompromiso(j){
	     	var mensaje=j[0].Contable1;
			var devuelve=j[0].Devuelve;
			swal(mensaje,{icon:"success",button: "Cerrar"});
			if(devuelve!="1"){
					document.getElementById("imgDevolverpreCompromisoPluriContrato").disabled = false;
					return;
					}
						
			if ($("#nTipoPago").val() == '1') 
			queryFormPost('mDocumentoFolioPrecompromisoDeletePasivo', {async: false });
			else 
			var editar='<%=editar%>';
			if (editar==0){
				document.getElementById("trEditarpreCompromisoPlurContrato").style.display="table-row";
			}
			
			$('#dt_preCompromiso').attr('disabled', false);
			document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = false;
			document.getElementById("imgDevolverpreCompromisoPluriContrato").disabled = true;
			editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
			//Bitácora
			$("#cAccion").val("DEVUELVE_PRECOMPROMISO_PLURIANUALIDADCONTRATO");
			$("#cIdDocumento").val($("#cContratoDefinitivo").val());
			 queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			 actualizaCaratula();
			window.location = "Plurianualidad.jsp?tab=" + 3;
		
	}
	
	function imprimeRes(j){
		if ($("#nTipoPago").val() == '0') {
			var res=j[0].Contable1;
			swal(res,{icon:"success",button: "Cerrar"});
		}
	}

	function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		swal("Ha ocurrido un error al crear el caso, contacte a su soporte",{icon:"success",button: "Cerrar"});
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
	function cambiafrmt( fld ){
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
			swal("Ingrese solo valores num\u00e9ricos",{icon:"success",button: "Cerrar"});
			$("#" + fld.id).val( "0" );
			return false;
		}
		if(valor<0){
			swal("No se pueden ingresar valores negativos",{icon:"success",button: "Cerrar"});
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
	    	swal("NO hay Suficicencia Mensual en la Clave Presupuestal",{icon:"success",button: "Cerrar"});
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompr=parseFloat(vcompr);
		valor=parseFloat(valor);
		vimptot=parseFloat(vimptot);
		if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
	    	swal("El Compromiso Actual Excede al Saldo Compromiso",{icon:"success",button: "Cerrar"});
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
	function precomprometer(){
		var  imgAprobar='<%=imgAprobar%>';
		if (imgAprobar==0) {
			if($("#mImporteTotal").val()!= $("#mComprometido").val()){
				swal("El importe total es mayor al monto pre-comprometido",{icon:"success",button: "Cerrar"});
				return -1;
			}
			document.getElementById("imgAprobarpreCompromisoPluriContrato").disabled = true;
			
			//crea el array de la tabla de montos
			var arregloDatos=ArrayTabla();
			
			//ajax Precomprometer en java
			$.ajax({url: '../../servlet/ContratoPlurianualidadServlet'
				, type:'post' , async: false,data:'operacion=10&tablaDatos='+arregloDatos+'&cEjercicio='+$("#cEjercicio").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivoPlurianual").val()+'&ntipoPago='+$("#esDescentralizado").val(), dataType: 'json',
        		mimeType: 'application/json', success: 
				function(j){
					var status=j[0].PRECOM;
					if(status=="true"){
						swal({
							title: "",
							text: j[0].STATUS+"\n"+j[0].FOLIOS,
							icon: "success",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								window.location = "Plurianualidad.jsp?tab=" + 4;
						});
						
					}else{
						swal({
							title: "",
							text: "Error: "+j[0].STATUS,
							icon: "error",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								window.location = "Plurianualidad.jsp?tab=" + 4;
						});
					}
				}
			});
		}else{
			swal("No tienes permisos.",{icon:"warning",button: "Cerrar"});
		}	
	
	}
	function ArrayTabla(){
		var arregloTmp=new Array();
		var arrayFila=new Object();
		
		var aTrs = $('#dt_preCompromiso').dataTable().fnGetNodes();
		var vimporteP;
		var nTr;
		var jqInputs;
		for ( var i=0 ; i<aTrs.length; i++ )     
		{
			nTr =  $('#dt_preCompromiso').dataTable().fnGetData(aTrs[i]);
			jqInputs = $('input',aTrs[i] );
			for ( j=0 ; j < jqInputs.length ; j++ ) {
				var k=j;
				vimporteP = jqInputs[j].value ;
				vimporteP = quitaFmt(vimporteP);
				arrayFila=[nTr[0]+'.'+nTr[1],k+1,vimporteP,"|"];
				arregloTmp.push(arrayFila);
			}
		}
		
		return arregloTmp;
	}
	function showHideButtons(){
		$("#imgAprobarpreCompromisoPluriContrato").css("display","none");
		$("#imgAprobarCompromisoPluriContrato").css("display","none");
		$("#imgDevolverpreCompromisoPluriContrato").css("display","none");
		if ((roles.toString().indexOf("ADMIN_RECMAT") >= 0)
				|| (roles.toString().indexOf("JEFES") >= 0)
		) {
			if(parseInt($("#nIdEstado").val(),10)>2){
				$("#imgAprobarpreCompromisoPluriContrato").css("display","none");
				$("#imgDevolverpreCompromisoPluriContrato").css("display","");
				if(parseInt($("#nIdEstado").val(),10)<4){
					$("#imgAprobarCompromisoPluriContrato").css("display","");
				}else{
					$("#imgDevolverpreCompromisoPluriContrato").css("display","none");
				}
			}else{
				if(parseInt($("#nIdEstado").val(),10)<2){
					$("#imgAprobarpreCompromisoPluriContrato").css("display","none");
				}else{
					$("#imgAprobarpreCompromisoPluriContrato").css("display","");
				}
			}
		}else if((roles.toString().indexOf("ANALISTA") >= 0)){
			if(parseInt($("#nIdEstado").val(),10)==3){
				$("#imgDevolverpreCompromisoPluriContrato").css("display","");
			}
			if(parseInt($("#nIdEstado").val(),10)==2){
				$("#imgAprobarpreCompromisoPluriContrato").css("display","");
			}
		}
	}
	function comprometer(){
		if (!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("JEFES") >= 0)) {
			swal("No tienes permiso para ejecutar está acción.",{icon:"warning",button: "Cerrar"});
			$("#imgAprobarCompromisoPluriContrato").css("display","none");
			return;
		}
		if(parseInt($("#nIdEstado").val(),10)==3){
			queryFormPost("obtenCNET", { async : false}); 
			if($("#nCodContratoCNET").val()==0 || $("#nCodContratoCNET").val()==""){
				swal("Debe Capturar el Codigo de Contrato de Compranet para poder Autorizar el Contrato.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if($("#nCodExpedienteCNET").val()==0 || $("#nCodExpedienteCNET").val()==""){
				swal("Debe Capturar el Codigo de Expediente de Compranet para poder Autorizar el Contrato.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if($("#cAprobacionPLU").val()==""){
				swal("Debe Capturar el N\u00famero Aprobacion de la Plurianualidad para poder Autorizar el Contrato.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if($("#cNoProcedimientoCNET").val()==""){
				swal("Debe Capturar el No. de Procedimiento de Compranet para poder Autorizar el Contrato.",{icon:"warning",button: "Cerrar"});
				return;
			}
			swal({
				title: "",
				text: "¿Está seguro que quiere autorizar el contrato?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$("#imgAprobarCompromisoPluriContrato").css("display","none");
					$.ajax({url: '../../servlet/ContratoPlurianualidadServlet'
						, type:'post' , async: false,data:'operacion=12&cIdContratoDefinitivo='+$("#cIdContratoDefinitivoPlurianual").val()
						+'&cIdRFC='+$("#cIdRFC").val()+'&cEjercicio='+$("#cEjercicio").val(), dataType: 'json',
		        		mimeType: 'application/json', success: 
						function(j){
							var status=j[0].COMP;
							if(status==true){
								
								swal({
									title: "",
									text: j[0].STATUS+"\n"+j[0].FOLIOS,
									icon: "success",
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										//registrar Anticipos sin Amoritzar
										queryFormPost({
											queryName: "sp_RegistraCNET",//CRUD ACTUALIZA CNET PCONTRATODIVERSO
				    						async: false,
				    						callback: function() { }
				    					});
										queryFormPost({
											queryName: "anticipoPlurEjerAnt",
				    						async: false,
				    						callback: function() {
			    								queryFormPost({
			    									queryName: "AmortizacionPlurEjercAnt",
						    						async: false,
						    						callback: function() {
						    								var montoNetoAnticipoEjerAnt =parseFloat($("#montoNetoAnticipoEjerAnt").val());
						    								var mAmortizacionAnticipo =parseFloat($("#mAmortizacionAnticipo").val());
						    								var mMontoAnticipoConIVA = montoNetoAnticipoEjerAnt-mAmortizacionAnticipo.toFixed(2);
						    								var mMontoAnticipoSinIVA=(mMontoAnticipoConIVA/(1+(0.01*($("#nPorcentajeIVA").val())))).toFixed(2);
						    								var mMontoAnticipoIVA=(mMontoAnticipoConIVA-mMontoAnticipoSinIVA).toFixed(2);
						    								$("#montoBrutoAnticipoEjerAnt").val(mMontoAnticipoSinIVA);
						    								$("#montoAnticipoIVAEjerAnt").val(mMontoAnticipoIVA);
						    								$("#montoNetoAnticipoEjerAnt").val(mMontoAnticipoConIVA);
						    								queryFormPost("insertmRecepcionpMatAnticipoEjerciciosAnt", {async: false});
						    							}
						    						});
			    							}
			    						});
										window.location = "Plurianualidad.jsp?tab=" + 4;
									});
							}else{
								swal("Error: "+j[0].STATUS,{icon:"success",button: "Cerrar"});
							}
						}
					});
				}
			});
		}
	}
</script>
</head>
<body >
	<form id="frmPrecompromiso">
    	<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Pre-Compromiso  de la plurianualidad</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarpreCompromisoPluriContrato" name="imgAprobarpreCompromisoPluriContrato" 	value="Pre-Comprometer"	onclick="precomprometer();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarCompromisoPluriContrato" name="imgAprobarCompromisoPluriContrato" 	value="Autoriza Compromiso"	onclick="comprometer();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverpreCompromisoPluriContrato" name="imgDevolverpreCompromisoPluriContrato" 	value="Devolver"	onclick="devuelvePrecompromiso();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'Plurianualidad.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblcIdContratoDefinitivoPluri" id="lblcIdContratoDefinitivoPluri"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblcNoContratoCNET" id="lblcNoContratoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDescContrato" id="lblDescContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblOficioDG" id="lblOficioDG"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblFolioMASCP" id="lblFolioMASCP"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lbcContratoAbierto" id="lbcContratoAbierto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lbcContratoCentralizado" id="lbcContratoCentralizado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstatus" id="lblEstatus"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblMontoIVA" id="lblMontoIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col" id="trNotas" >
						<label for="lblNotas">Motivo del rechazo:</label>
						<textarea class="form-control" id="lblNotas" name="lblNotas" rows="3" readonly style="color:red;"></textarea>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Montos a precomprometer del contrato plurianual</legend>
			<div class="form-group" id="trinputsMontos">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<label for="mImporteTotal">Monto Contrato:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="20"  aria-describedby="basic-addon1"  id="mImporteTotal" name="mImporteTotal" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Compromiso Actual:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="mComprometido" name="mComprometido" value="0">
						</div>
						<div class="col">
							<label for="difPrecompromiso">Saldo Compromiso:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="difPrecompromiso" name="mPreComprometido" value="0">
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" id="trEditarpreCompromisoPlurContrato" style="display: none">
					<div class="col-4">
						<input class="btnInterfaceBG ui-button ui-corner-all" type="button" name="edit" id="edit" value="Editar">
					</div>
				</div>
				<div class="row" id="tr_dt_preCompromiso">
					<div class="input-group">
						<div class="col">
							<table id="dt_preCompromiso" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
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
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_suficiencia" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
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
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" style="visibility: hidden">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_grabaprecompE" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
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
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" style="visibility: hidden">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_grabaprecompD" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%;visibility: hidden" >
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
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" style="visibility: hidden">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_urcc" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%;visibility: hidden" >
								<thead style="width:0px" >
									<tr align="center" style="width:0px" >
										<th >CentroContable</th>
										<th >UnidadEjecutora</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<!-- Hiddens de sesion -->
		<input type="hidden" name="cIdContratoMat" id="cIdContratoMat" value="<%=cIdContrato%>"/>
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		<input type="hidden" name="cIdTipoCambio" id="cIdTipoCambio" value="<%=cIdTipoCambio%>"/>
		<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%= cIdContratoDefinitivo %>"/>
		<input type="hidden" name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" value="<%= cIdContratoDefinitivo %>"/>
		<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cITipoContrato%>"/>
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
		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cIdContrato %>"  />
  		<input type="hidden" name="cIdContratoDefinitivoPlurianual" id="cIdContratoDefinitivoPlurianual" value="<%=cIdContratoDefinitivo %>"  />
		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
		<input type="hidden" name="ccTem" id="ccTem" value="<%=cCentroContable %>" />
		<input type="hidden" name="ueTem" id="ueTem" value="<%=usuario.getU_UR() %>" />	
		<input type="hidden" name="esDescentralizado" id="esDescentralizado" value="0" />
		<input type="hidden" name="cIdRFC" id="cIdRFC" value="0" />
		<input type="hidden" name="mAmortizacionAnticipo" id="mAmortizacionAnticipo" value="0" />
		<input type="hidden" name="montoBrutoAnticipoEjerAnt" id="montoBrutoAnticipoEjerAnt" value="0" />
		<input type="hidden" name="montoAnticipoIVAEjerAnt" id="montoAnticipoIVAEjerAnt" value="0" />
		<input type="hidden" name="montoNetoAnticipoEjerAnt" id="montoNetoAnticipoEjerAnt" value="0" />
		<input type="hidden" name="nPorcentajeAnticipoEjerAnt" id="nPorcentajeAnticipoEjerAnt" value="0" />
		<input type="hidden" name="cidrecepmat" id="cidrecepmat" value="0" />
		<input type="hidden" name="nCodContratoCNET" id="nCodContratoCNET"  /> 
		<input type="hidden" name="nCodExpedienteCNET" id="nCodExpedienteCNET"  /> 
		<input type="hidden" name="cAprobacionPLU" id="cAprobacionPLU"  /> 
		<input type="hidden" name="cNoProcedimientoCNET" id="cNoProcedimientoCNET"  />
		<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" value="16" />
		
	</form>
</body>
</html>
