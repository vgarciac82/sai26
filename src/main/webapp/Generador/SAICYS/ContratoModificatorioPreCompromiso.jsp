<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont=false;
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
	int mesActual = c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String name_user=usuario.getLogin();
	String idRol="0";		
	Map rol =usuario.getRoles();		
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";		
	String isConvEjercicioAnt="";
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		isConvEjercicioAnt = (String)session.getAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		cIdUnidadEjecutora = cIdContrato.split("-")[1];
		nIdConsecutivo = cIdContrato.split("-")[2];		
	}else 
		response.sendRedirect("ContratosModificatorio.jsp?tab=0");
	//Valida Centro de Costos
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
        cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
%>
<!DOCTYPE html>
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
	var vfolio=0;
	var vEstado;
	var roles="";
	var vVigente=1;  //temporal se deshabilita la vigencia de 8 dias
	var vcaNoCompromiso;
	var initPrecom=0;
	var oTableUrcc;
	var	initSuf=0
	$(document).ready(function() {
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
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"ContratoModificatorio","precompromisoContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarpreCompromisoContMod".equals(img)){  
						%>
						$("#<%=img%>").attr("disabled", false);<%
						imgAprobar=1;
					}
					if ("imgDevolverpreCompromisoContMod".equals(img)){
						%>
						$("#<%=img%>").attr("disabled", false);<%
						imgDevolver=1; 
					}
					if ("trEditar".equals(img)){
						editar=1; 
					}
				}
			%>
	    var nEditing = null;
	    tabb=4;
		showAndHideTabs();
	    roles="<%=roles%>";
	    $("#isConvEjercicioAnt").val("<%=isConvEjercicioAnt %>");
	    initTables();
		setReadOnly();
		//Esconde el letrero de apartado encontrado
		$("#apartadoNotice").hide();
		//Carga los datos de inicio
		var cIdContratoDef=$("#cContratoDefinitivo").val();
		if(1==$("#isConvEjercicioAnt").val()){
			setInitQueysContEjerAnt();
		}else{
			if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
				initQueriesPlu();
			}else{
				initQueries();
			}	
		}
		//Guarda en variables globales el contrato, folio, estado y si es vigente
		vcontrato = $("#cContratoDefinitivo").val();
		vfolio = $("#nFolioPreCompromiso").val();
		vEstado = parseInt($("#nIdEstado").val(),10);
		setInitConditions();
		if((vEstado==3 || vEstado==4)&& parseInt($("#tipoMod").val())!=2 ){
			//En caso que sea un contrato con un preCompromiso ya hecho
			cargaContratoPrecomprometido();
			$("#mComprometido").val($("#mImporteTotal").val());
			$("#mComprometido").formatCurrency();
		}
		else {
			//revisamos si es una copia del contrato
		  	cargaPreCompromisoVacio();
		  		
		}
		if(parseInt($("#tipoMod").val())!=1){
			$("#compromisoContratoMod").hide();
		}
		cargaSuficiencias();
		//Carga la tabla que tiene el monto de cada EP
		$("#mComprometido").formatCurrency();
		$("#mImporteTotal").formatCurrency();
		$("#difPrecompromiso").formatCurrency();
		var offset = parseInt($("#mesDisponible").val(),10) + parseInt($("#GP_VALOR").val(),10);
		if(offset > 12 || offset < 0){
			swal("El desfase del calendario está mal configurado, favor de avisar al administrador",{icon:"info",button: "Cerrar"});
			window.location ="ContratoModificatorio.jsp?tab=0";
		}else {
			$("#mesDisponible").val(offset);
		}
		loadURCC();
	});
	
	function setInitConditions(){
		if(vEstado == 2 ){
			$("#imgDevolverpreCompromisoContMod").hide();
		}
		if(vEstado==3 ||vEstado==4 || vVigente==0){
			$("#imgAprobarpreCompromisoContMod").hide();
			$('#dt_preCompromisoMod').attr('disabled', true);
		}
		
		if(vEstado != 3){
			//En caso el estado del contrato sea aprobado, falta por confirmar que hacer en los otros casos
			$("#imgAprobarCompromisoContMod").css("display", "none");
			$("#imgDevolverpreCompromisoContMod").css("display", "none");
		}
		if(vEstado==3 && roles.toString().indexOf("ANALISTA") >= 0  ){
	   		$("#textAutorizaContrto").show();
	   		$("#imgAprobarCompromisoContMod").hide();
	   		
	   	}
	   	if(vEstado==3 && roles.toString().indexOf("Estatales") >= 0  ){
	   		$("#textAutorizaContrto").show();
	   	}
		if(vVigente == 0){
			swal("No es posible crear más precompromisos porque todavía exiten precompromisos del área caducos.",{icon:"info",button: "Cerrar"});
		}
		if(parseInt($("#tipoMod").val())==2 || parseInt($("#tipoMod").val())==3 || parseInt($("#tipoMod").val())==4){
			$("#imgAprobarpreCompromisoContMod").hide();
			
			$("#tr_dt_preCompromiso").hide();
			$("#tr_dt_suficiencia").hide();
			$("#trinputsMontos").hide();
			if(parseInt($("#tipoMod").val())==2 || parseInt($("#tipoMod").val())==4){
				$("#imgAprobarPartidasContMod").css("display", "none");
				
			}else{
				$("#imgAprobarFechasContMod").css("display", "none");
			}
			if(vEstado==4){
				$("#imgAprobarFechasContMod").css("display", "none");
				
				$("#imgAprobarPartidasContMod").css("display", "none");
				
			}
		}else{
			$("#imgAprobarFechasContMod").css("display", "none");
			
			$("#imgAprobarPartidasContMod").css("display", "none");
			
		}
	}
	function initQueriesPlu(){
		queryFormPost("mContratoHeaderReadPLU", {async: false});
		queryFormPost("mContratoPluModificadoTotales", {async: false});
		queryFormPost("mContratoModificadoCtaDisp", {async: false});
		$("#mImporteTotal").val($("#totalMod").val());
		queryFormPost("cg_roleRead", {async: false});
		//Para ver si tiene apartados en nApartadosUsados
		queryFormPost("fn_mApartadoContratoPluModificadoCuenta", {async: false} );
		//Esconde el letrero de apartado encontrado
		$("#apartadoNotice").hide();
		if ($("#nApartadosUsados").val() > 0){
			queryFormPost("fn_mApartadoContratoPluModificadoTotal", {async: false} );
			
			//Si el valor del apartado disponible es > 0
			if ($("#mApartadoReal").val() > 0 && parseInt($("#nIdEstado").val(),10)<3){
				$("#mApartadoReal").formatCurrency();
				$("#aptdReal").html($("#mApartadoReal").val());
				$("#apartadoNotice").show();
				$("#mImporteTotal").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mApartadoReal").val()));
				if (parseInt($("#mImporteTotal").val(),10) < 0) {
					$("#mImporteTotal").val(0);
				}
			}
		}
		//Actualiza la diferencia entre el importe(montoMod - montoApartado) y el comprometido(siempre es 0)
		$("#difPrecompromiso").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mComprometido").val()));
		
		//Ejecuta una función en la base de datos para saber si la unidad ejecutora no tiene Precompromisos pendientes por entregar documentacion
		//queryFormPost("fn_vigenciaValida", {async: false});
		queryFormPost("TipoPolizaRead", {async: false});
		//Lee la configuración de mes
		queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
		queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
	}
	function initQueries(){
		//Lee los datos de la cabecera
		queryFormPost("mContratoHeaderRead", {async: false});
		queryFormPost("mContratoModificadoTotales", {async: false});
		queryFormPost("mContratoModificadoCtaDisp", {async: false});
		$("#mImporteTotal").val($("#totalMod").val());
		queryFormPost("cg_roleRead", {async: false});
		//Para ver si tiene apartados en nApartadosUsados
		queryFormPost("fn_mApartadoContratoModificadoCuenta", {async: false} );
		//Esconde el letrero de apartado encontrado
		$("#apartadoNotice").hide();
		//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
		if ($("#nApartadosUsados").val() > 0){
			queryFormPost("fn_mApartadoContratoModificadoTotal", {async: false} );
			//Si el valor del apartado disponible es > 0
			if ($("#mApartadoReal").val() > 0 && parseInt($("#nIdEstado").val(),10)<3){
				$("#mApartadoReal").formatCurrency();
				$("#aptdReal").html($("#mApartadoReal").val());
				$("#apartadoNotice").show();
				//Actualiza el 'Monto Contrato:'
				$("#mImporteTotal").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mApartadoReal").val()));
				if (parseInt($("#mImporteTotal").val(),10) < 0) {
					$("#mImporteTotal").val(0);
				}
			}
		}
		
		//Actualiza la diferencia entre el importe(montoMod - montoApartado) y el comprometido(siempre es 0)
		$("#difPrecompromiso").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mComprometido").val()));
		
		//Ejecuta una función en la base de datos para saber si la unidad ejecutora no tiene Precompromisos pendientes por entregar documentacion
		//queryFormPost("fn_vigenciaValida", {async: false});
		queryFormPost("TipoPolizaRead", {async: false});
		//Lee la configuración de mes
		queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
		queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
	}
	function setInitQueysContEjerAnt(){
		queryFormPost("mContratoHeaderReadConvAnt", {async: false});
		queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
		
		queryFormPost("mContratoModificadoCtaDisp", {async: false});
		$("#mImporteTotal").val($("#totalMod").val());
		queryFormPost("cg_roleRead", {async: false});
		//Para ver si tiene apartados en nApartadosUsados
		queryFormPost("fn_mApartadoContratoEjerAntModificadoCuenta", {async: false} );
		//Esconde el letrero de apartado encontrado
		$("#apartadoNotice").hide();
		//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
		if ($("#nApartadosUsados").val() > 0){
			queryFormPost("fn_mApartadoContratoEjerAntModificadoTotal", {async: false} );
			//Si el valor del apartado disponible es > 0
			if ($("#mApartadoReal").val() > 0 && parseInt($("#nIdEstado").val(),10)<3){
				$("#mApartadoReal").formatCurrency();
				$("#aptdReal").html($("#mApartadoReal").val());
				$("#apartadoNotice").show();
				//Actualiza el 'Monto Contrato:'
				$("#mImporteTotal").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mApartadoReal").val()));
				if (parseInt($("#mImporteTotal").val(),10) < 0) {
					$("#mImporteTotal").val(0);
				}
			}
		}
		
		//Actualiza la diferencia entre el importe(montoMod - montoApartado) y el comprometido(siempre es 0)
		$("#difPrecompromiso").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mComprometido").val()));
		
		//Ejecuta una función en la base de datos para saber si la unidad ejecutora no tiene Precompromisos pendientes por entregar documentacion
		//queryFormPost("fn_vigenciaValida", {async: false});
		queryFormPost("TipoPolizaRead", {async: false});
		//Lee la configuración de mes
		queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
		queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
		
	}
	function actualizaCaratula(){
		var cIdContratoDef=$("#cContratoDefinitivo").val();
		if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
			queryFormPost("mContratoHeaderReadPLU", {async: false});
			queryFormPost("mContratoPluModificadoTotales", {async: false});
		}else{
			queryFormPost("mContratoHeaderRead", {async: true});
			queryFormPost("mContratoModificadoTotales", {async: false});
		}
		
	}
	
	function setReadOnly(){
		document.getElementById("lblUnidadEjecutora").style.readonly = true;
		document.getElementById("lblProcedimiento").style.readonly = true;
		document.getElementById("lblDefinitivo").style.readonly = true;
		document.getElementById("lblContrato").style.readonly = true;
		document.getElementById("lblProveedor").style.readonly = true;
		document.getElementById("lblEstadoMod").style.readonly = true;
		document.getElementById("lblTotalAnterior").style.readonly = true;
		document.getElementById("lblTotalModificado").style.readonly = true;
		document.getElementById("lblTotal").style.readonly = true;		
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
		var campos = "'" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mUrccContratoModificacion(" + campos + ")",
				aoColumns: [
					{ sName: "centroContable"},
					{ sName: "ur" }
				]
		}) ;
	}
    
    function guardarPrecompromisoGeneral(){
		var  imgAprobar='<%=imgAprobar%>';

		if(imgAprobar==0){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("ANALISTA") >= 0) && !(roles.toString().indexOf("JEFE") >= 0) && !(roles.toString().indexOf("Estatales") >= 0) ){
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
					return;
				}
			}
			
			//Condiciones para guardar un precompromiso
			//Se tiene que precomprometer por el monto total
			if($("#mImporteTotal").val() != $("#mComprometido").val()){
				swal("El importe total es mayor al monto pre-comprometido.",{icon:"info",button: "Cerrar"});
				return -1;
			}
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
						queryFormPost("mUpdatePrecompromisoContratoModificado", {async: false });
						queryFormPost('tPreCompromisoDDelete', {async: false });
				        queryFormPost('tPreCompromisoEDelete', {async: false });
				        queryFormPost('mDocumentoFolioPrecompromisoDelete', {async: false });
						return;
					}
				}
				$("#nIdEstado").val(3);
				vEstado=3;
				queryFormPost("mContratoModificadoPrecompromisoUpdate", {async: false });
			}
			else{
			   	var mensaje=guardarPrecompromiso();
				if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE")<0){
					$("#nIdEstado").val(2);
					vEstado=2;
					queryFormPost("mUpdatePrecompromisoContratoModificado", {async: false });
					queryFormPost('tPreCompromisoDDelete', {async: false });
				    queryFormPost('tPreCompromisoEDelete', {async: false });
					return;
				}
				$("#nIdEstado").val(3);
				vEstado=3;
				queryFormPost("mContratoModificadoPrecompromisoUpdate", {async: false });
			}
			$("#imgAprobarpreCompromisoContMod").hide();
			$("#imgDevolverpreCompromisoContMod").show();
			//actualizaCaratula();
			swal("Contrato Modificatorio Precomprometido con folio(s) " + $("#foliosCasoPreCompromiso").val(),{icon:"info",button: "Cerrar"});
			$("#foliosCasoPreCompromiso").val('');
			location.reload();
		}else{
			swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
		}
	}
	
	function guardarPrecompromisoDes(entidadContable, ur){	
	   var mensajeAp="";	
		//Genera un nuevo folio en caso de que no lo tenga
		$.ajax({url: '../../servlet/ContratoModificadoServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val()==0)
			return -1;
		
		//Guarda en una tabla auxiliar el encabezado del compromiso
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 ) ;
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato + '#M' + $("#cConsecutivoMod").val());
		$("#cTipoContrato").val( "DI" );
		$("#cCentroContable").val(entidadContable); 
		$("#ccTem").val(entidadContable); 
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val(ur);
		$("#ueTem").val(ur);
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val(nMes); 
		$("#fVigencia").val("<%=vigencia%>");
		
		getNextSequenceVal({seqName: "CO-" + entidadContable, async: false, callback: setSequenceVal});
		$("#caNoPreCompromiso").val( vcaNoCompromiso );
		
		//Genera Encabezado y detalle
		queryFormPost('tPreCompromisoECreate', {async: false });
		
		//Si exste un apartado, se agregan a Detalle las operaciones pertinentes
		if ($("#nApartadosUsados").val() > 0 && quitaFmt($("#mApartadoReal").val()) > 0){
			if(1==$("#isConvEjercicioAnt").val()){
				queryFormPost("pa_mAptdPrccDetalleModEjerAnt", {async: false });
			}else{
				if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
					queryFormPost("pa_mAptdPrccDetalleModPlu", {async: false });
				}else{
					queryFormPost("pa_mAptdPrccDetalleMod", {async: false });
				}	
			}
				
			
		}
		
		//Actualiza la tabla de contrato con los folio y el nuevo estado
		queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
		queryFormPost("mDocumentoFolioPrecompromisoCreate", {async: false });
		
		
		$.ajax({url: '../../servlet/ContratoModificadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#aEjercicioFiscal").val()
			+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val(), type:'post' , async: false,data:'operacion=3&cIdContrato='+$("#cIdContrato").val(), dataType: 'json', success:
			function(j){
				mensajeAp=j[0].Contable1;
				swal(mensajeAp,{icon:"info",button: "Cerrar"});
				}
			
			});
		if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
		return mensajeAp;
    }
	
	function guardarPrecompromiso(){
		var cIdContratoDef=$("#cContratoDefinitivo").val();
		
		var mensajeAp="";		
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val()==""){
			$.ajax({url: '../../servlet/ContratoModificadoServlet' , type:'post' , async: false,data:'operacion=2', dataType: 'json', success: guardaFolio});
		}
		
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
		
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 ) ;
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato + '#M' + $("#cConsecutivoMod").val());
		$("#cTipoContrato").val( "DI" );
		$("#cCentroContable").val( "<%=cCentroContable%>"); 
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 
		$("#fVigencia").val( "<%=vigencia%>" );
		
		getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
		$("#caNoPreCompromiso").val( vcaNoCompromiso );
		
		//Genera Encabezado y detalle
		queryFormPost('tPreCompromisoECreate', {async: false });
		
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
		
		//Si exste un apartado, se agregan a Detalle las operaciones pertinentes
		
		if ($("#nApartadosUsados").val() > 0 && quitaFmt($("#mApartadoReal").val()) > 0){
			if(1==$("#isConvEjercicioAnt").val()){
				queryFormPost("pa_mAptdPrccDetalleModEjerAnt", {async: false });
			}else{
				if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
					queryFormPost("pa_mAptdPrccDetalleModPlu", {async: false });
				}else{
					queryFormPost("pa_mAptdPrccDetalleMod", {async: false });
				}	
			}
				
			
		}
		
		//Actualiza la tabla de contrato con los folio y el nuevo estado
		queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
		
		$.ajax({url: '../../servlet/ContratoModificadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#aEjercicioFiscal").val()
			+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val(), type:'post' , async: false,data:'operacion=3&cIdContrato='+$("#cIdContrato").val(), dataType: 'json', success:
		function(j){
			mensajeAp=j[0].Contable1;
			swal(mensajeAp,{icon:"info",button: "Cerrar"});
			}
		
		});
		
		if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
		return mensajeAp;
    }
	function devuelvePrecompromiso(){ //Modificar está función
		var imgDevolver='<%=imgDevolver%>';
		if(imgDevolver==0){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("ANALISTA") >= 0) && !(roles.toString().indexOf("JEFE") >= 0) && !(roles.toString().indexOf("Estatales") >= 0)){
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					swal("No tiene permisos para realizar esta accion",{icon:"info",button: "Cerrar"});
					return;
				}
			}
			
			////Vuelve a leer el estado del contrato para evitar conflictos con ventanilla y saber si el contrato fue aprobado
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			if(1==$("#isConvEjercicioAnt").val()){
				queryFormPost("pa_mAptdPrccDetalleModEjerAnt", {async: false });
			}else{
				if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
					queryFormPost("mContratoPluModificadoTotales", {async: false});
				}else{
					queryFormPost("mContratoModificadoTotales", {async: false});
				}
			}
				
			
			if($("#nIdEstado").val()==("2")){
				swal("El modificatorio ha sido rechazado por el usuario de ventanilla",{icon:"info",button: "Cerrar"});
				//se tiene que actualizar co_responsable en la tabla cg_caso_operacion para que ya no aparezca en el inbox
				queryFormPost("actualizaCoResponsableRead", {async: false});
				//actualizaCaratula();
				window.location = "ContratoModificatorio.jsp?tab=" + 4;
				return;
			}
			//primero checamos si el contrato ya esta siendo trabajado por el usuario de ventanilla
			queryFormPost("mContratoModificadoUsuarioVentanillaRead", {async: false});
			if($("#operacion").val() == 2 && $("#responsable").val() != "VENTANILLA_PRECOMPROMISO") {
				swal("No se puede eliminar el precompromiso, el contrato lo esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val(),{icon:"info",button: "Cerrar"});
				return;
			}		 
					
			if($("#nIdEstado").val() != ("4")){
				//Elimina el Precompromiso
				swal({
					title: "",
					text: "¿Está seguro que quiere eliminar el pre-compromiso, el modificatorio se encuentra en VENTANILLA DE PAGOS y esta pendiente su aprobación, esta acción no se puede deshacer?",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						$.ajax({url: '../../servlet/ContratoModificadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() 
							, type:'post' , async: false,data:'operacion=4&tipoPago=' + $("#nTipoPago").val(), dataType: 'json', success: eliminaPrecompromiso});
					}
				});
			}
			else{
				actualizaCaratula();
				swal("El modificatorio no se puede devolver porque ya ha sido APROBADO y esta COMPROMETIDO",{icon:"info",button: "Cerrar"});
			}
		}else{
			swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
		}
		
	}
	
	
	function eliminaPrecompromiso(j){
	
	var mensaje=j[0].Contable1;
			swal(mensaje,{icon:"info",button: "Cerrar"});
			if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE")<0){
					//NO CANCELO CONTABLEMENTE SE DEJA EN SAIPRESUPUESTADO
					$("#nIdEstado").val(3);vEstado=3;
					queryFormPost("mUpdatePrecompromisoContratoModificado", {async: false });
					return;
					}
						
			if ($("#nTipoPago").val() == '1') 
			queryFormPost('mDocumentoFolioPrecompromisoDelete', {async: false });
			else 
			//Regresa el estado a en SAI sin Precomprometer
			$("#nIdEstado").val(2);vEstado=2;
			queryFormPost("mContratoModificadoPrecompromisoDevuelveUpdate", {async: false });
			//Regresa el estado a en SAI sin Precomprometer
			$("#imgDevolverpreCompromisoContMod").hide();
			$("#imgAprobarpreCompromisoContMod").show();
			actualizaCaratula();
			window.location = "ContratoModificatorio.jsp?tab=" + 4;
			
	}
	
	function imprimeRes(j){
		if ($("#nTipoPago").val() == '0') {
			var res=j[0].Contable1;
			swal(res,{icon:"info",button: "Cerrar"});
		}
	}
	
	function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		swal("Ha ocurrido un error al crear el caso, contacte a su soporte",{icon:"info",button: "Cerrar"});
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
	   	return val;
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
	
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true;
	}
	
	function setSequenceVal(seqValue) {
		seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "CO" + $("#cEjercicio").val() + seqValue;
	}
	function autorizarFechas(){
		if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("ANALISTA") >= 0) && !(roles.toString().indexOf("JEFES") >= 0) && !(roles.toString().indexOf("Estatales") >= 0) && !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
			swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
			return;
		}
		
		$("#nIdEstado").val(4);
		queryFormPost("mContratoModificadoPrecompromisoDesUpdate", {async: false, 
			callback : function() 
			{
				//Bitácora
				$("#cAccion").val("APRUEBA_FECHAS_MODIFICATORIO");
				$("#cIdDocumento").val($("#cIdContratoH").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				swal("CONVENIO AUTORIZADO.",{icon:"info",button: "Cerrar"});
				//Refresca la pagina
				location.reload();	
			}
		});
	}
	function guardarcomprimisoGeneral(){
		if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("ANALISTA") >= 0) && !(roles.toString().indexOf("JEFES") >= 0) && !(roles.toString().indexOf("Estatales") >= 0) && !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
			swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
			return;
		}
		if($("#cNoConvenio").val()==null || $("#cNoConvenio").val()==''){
			swal("Falta agregar el No. de Convenio, favor de agregarlo",{icon:"info",button: "Cerrar"});
			return;
		}
		//Validar
		$("#cIdContratoH").val($("#cDocumentoDefinitivo").val());
		$("#rfcProv").val($("#cIdRFC").val());
		//return;
		var proc=""+$("#cEjercicio").val()
					+","+$("#cIdContratoH").val()
					+","+$("#rfcProv").val()+"";
					
		
		if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){			
		
		 $.getJSON("../../servlet/PedidoServlet?operacion=10",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
		                switch(j[0].Col1){
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
			
		}else{
	
		  $.getJSON("../../servlet/PedidoServlet?operacion=5",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
		                switch(j[0].Col1){
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
		
		}			
		//case 7 aplicaContablementeCompromiso
		//liberaSaldo
	}
	function onSubmit(){
	  	var mensaje="";
		//  se valida que existan anticipos y amortizaciones
		try{

			//llama a la aplicacion contable
			$("#tipoOperacion").val('ACTUALIZA');
			mensaje=fnTerminaAplicacionCon();
			if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE")<0)
			{
				//Regresa el Contrato
				$("#nIdEstado").val(3);vEstado=3;
				$("#tipoOperacion").val('RECHAZA');
				queryFormPost("pa_actualizaEstadoVentanillaPedidoContratoCreate", {async: false });
				queryFormPost('tCompromisoDDelete', {async: false });
				queryFormPost('tCompromisoEDelete', {async: false });
				return;			
			}else{
			  	//Valisa si existe algun precompromiso (sin aplicar) de pedido o contrato que libera saldo a disponible
			  if($("#origen").val() == "PEDIDO"){
			  		$.ajax({url: "../../servlet/PedidoServlet?cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:"operacion=13&pedidoDefinitivo="+$("#cIdContratoH").val(), dataType: 'json', success:
						function(j){}
					});
			  	}
			  	if($("#origen").val() == "CONTRATO"){
			  		$.ajax({url: "../../servlet/ContratoServlet?cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:"operacion=11&contratoDefinitivo="+$("#cIdContratoH").val(), dataType: 'json', success:
						function(j){}
					});
			  	}
			}

			//Bitácora
			$("#cAccion").val("APRUEBA_PRECOMPROMISO_MODIFICATORIO");
			$("#cIdDocumento").val($("#cIdContratoH").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			swal("Se ha creado el Compromiso con folio "+ $("#FOLIO").val(),{icon:"info",button: "Cerrar"});
			//Refresca la pagina
			location.reload();	
			}
			catch (e) {
				swal("onSubmit: Error: " + e.message,{icon:"error",button: "Cerrar"});
				//return false;
			}
			
  	}
  	function fnTerminaAplicacionCon(){
  	//aplicacion contable
  		var mensajeAp="";
  		var arrayCompromiso=new Array();
  		var oper=7;
  		if ($("#nTipoPago").val() == '1') {
  			oper=14;
  		}
  		$.ajax({url: '../../servlet/PedidoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()
  	  		+"&cEjercicio="+$("#cEjercicio").val()+"&nFolioPreCompromisoPRCP="+$("#folioCasoPreCompromiso").val(), type:'post' , async: false
  	  		,data:'operacion='+oper+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()
  	  		+'&tipoOperacion='+$("#tipoOperacion").val()+'&cuentaDispRadicado='+$("#cuentaDisponible").val()
  	  		, dataType: 'json', success:
  		//$.ajax({url: '../../servlet/PedidoServlet?nFolioCompromiso='+$("#nFolioCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=7', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				$("#FOLIO").val(j[0].FolioCompromiso);
				arrayCompromiso=j[0].FolioCompromiso.split('-');
				$("#nFolioCompromiso").val(arrayCompromiso[2]);
					 
				swal({
					title: "",
					text: mensajeAp,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						return mensajeAp;
				});
			}
		});
	}
	function cargaSuficiencias(){
		var oTable=$('#dt_suficiencia').dataTable( {
			bPaginate: false,
   			bLengthChange: false,
   			bFilter: false,
   			bInfo: false,
   			
   			bScrollCollapse: true,
			sScrollX: "100%",
			bRetrive : true,
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
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			//Query de Financiero, también se utiliza para Compromiso
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleContratoModEP&qw=cContratoDefinitivo='" +encodeURIComponent($("#cDocumentoDefinitivo").val()) + "' and cIdEntidadContable = '" + $("#cIdEntidadContable1").val() + "'",
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
					if (editar==0 && parseInt($("#tipoMod").val())!=2 && parseInt($("#tipoMod").val())!=3 && parseInt($("#tipoMod").val())!=4){
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
		var documento=$("#cDocumentoDefinitivo").val();
		var numMod=documento.split("#");
		var oTable=$("#dt_preCompromiso").dataTable({
				bPaginate: false,
      			bLengthChange: false,
      			bFilter: false,
      			bInfo: false,
      			
				sScrollX: "100%",
				bRetrive : true,
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
			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,   
			//Carga el calendario con valores de 0
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoContMod('"+vcontrato+"','"+numMod[1]+"')",
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
           			queryFormPost("existePrecomContMod", {async: false} );
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
       			
				sScrollX: "100%",
				bRetrive : true,
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
				
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoConMod( '" + encodeURIComponent($("#cDocumentoDefinitivo").val()) + "' , 0 )",
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
	    	swal("No hay Suficicencia Mensual en la Clave Presupuestal",{icon:"info",button: "Cerrar"});
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompr=parseFloat(vcompr);
		valor=parseFloat(valor);
		vimptot=parseFloat(vimptot);
		if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
	    	swal("El Compromiso Actual Excede al Saldo Compromiso",{icon:"info",button: "Cerrar"});
	    	$("#" + fld.id).val( "0" );
	    	return false;
	    }
		vcompT = vimptot-valor-vcompr;
		//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
		vcompT=vcompT.toFixed(2);
		$("#difPrecompromiso").val(parseFloat(vcompT));
		$("#difPrecompromiso").formatCurrency();
	}
	function deshabilitaMeses(oTableLocal){
		var i=$("#mesDisponible").val();
		i=parseInt(i,10);
		for(i; i>1;i--)
			oTableLocal.fnSetColumnVis(i,false);
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
	function precomprometer(){
		var  imgAprobar='<%=imgAprobar%>';
		if (imgAprobar==0) {
			document.getElementById("imgAprobarpreCompromisoContMod").disabled = true;
			
			//ajax Precomprometer en java
			$.ajax({url: '../../ContratacionFormalizadaServlet'
				, type:'post' , async: false,data:'tipoOperacion=1&tipoProceso=6&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
				+'&nTipoPago='+$("#nTipoPago").val()
				+'&nConsecutivoMod='+$("#cConsecutivoMod").val(), dataType: 'json',
        		mimeType: 'application/json', success: 
				function(j){
					var status=j[0].RESPUESTA;
					if(status=="true" || status==true){
						swal({
							title: "",
							text: j[0].MENSAJE,
							icon: "success",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								window.location = "ContratoModificatorio.jsp?tab=" + 4;
						});
						
					}else{
						swal({
							title: "",
							text: "Error: "+j[0].MENSAJE,
							icon: "error",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								window.location = "ContratoModificatorio.jsp?tab=" + 4;
						});
					}
				}
			});
		}else{
			swal("No tienes permisos.",{icon:"warning",button: "Cerrar"});
		}
	}
	function cancelarPrecompromiso(){
		var imgDevolver='<%=imgDevolver%>';
		if(imgDevolver==0){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("ANALISTA") >= 0) && !(roles.toString().indexOf("JEFE") >= 0) && !(roles.toString().indexOf("Estatales") >= 0)){
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					swal("No tiene permisos para realizar esta accion",{icon:"info",button: "Cerrar"});
					return;
				}
			}
			swal({
				title: "",
				text: "¿Está seguro que quiere eliminar el pre-compromiso, esta acción no se puede deshacer?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$.ajax({url: '../../ContratacionFormalizadaServlet'
						, type:'post' , async: false,data:'tipoOperacion=2&tipoProceso=6&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
						+'&nTipoPago='+$("#nTipoPago").val()
						+'&nConsecutivoMod='+$("#cConsecutivoMod").val(), dataType: 'json',
		        		mimeType: 'application/json', success: 
						function(j){
							var status=j[0].RESPUESTA;
							if(status=="true" || status==true){
								swal({
									title: "",
									text: j[0].MENSAJE,
									icon: "success",
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										window.location = "ContratoModificatorio.jsp?tab=" + 4;
								});
							}else{
								swal({
									title: "",
									text: "Error: "+j[0].MENSAJE,
									icon: "error",
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										window.location = "ContratoModificatorio.jsp?tab=" + 4;
								});
							}
						}
					});
				}
			});
		}else{
			swal("No tienes permisos.",{icon:"warning",button: "Cerrar"});
		}
	}
	function autorizaConvenio(){
		if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) &&  !(roles.toString().indexOf("JEFE") >= 0) ){
			swal("No tiene permisos para realizar esta accion",{icon:"info",button: "Cerrar"});
			return;
		}
		swal({
			title: "",
			text: "¿Está seguro que quiere autorizar el convenio, esta acción no se puede deshacer?",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
				},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				$.ajax({url: '../../ContratacionFormalizadaServlet'
					, type:'post' , async: false,data:'tipoOperacion=3&tipoProceso=6&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
					+'&nTipoPago='+$("#nTipoPago").val()
					+'&nConsecutivoMod='+$("#cConsecutivoMod").val(), dataType: 'json',
	        		mimeType: 'application/json', success: 
					function(j){
						var status=j[0].RESPUESTA;
						if(status=="true" || status==true){
							swal({
								title: "",
								text: j[0].MENSAJE,
								icon: "success",
								buttons: {
									confirm : "Cerrar"
									},
								}).then((continuar) => {
									window.location = "ContratoModificatorio.jsp?tab=" + 4;
							});
						}else{
							swal({
								title: "",
								text: "Error: "+j[0].MENSAJE,
								icon: "error",
								buttons: {
									confirm : "Cerrar"
									},
								}).then((continuar) => {
									window.location = "ContratoModificatorio.jsp?tab=" + 4;
							});
						}
					}
				});
			}
		});
		
	}
</script>
</head>
<body>
	<form id="formPrecompromiso">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Pre-Compromiso del Contrato Modificatorio</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarpreCompromisoContMod" name="imgAprobarpreCompromisoContMod" 	value="Pre-Comprometer"	onclick="precomprometer();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarCompromisoContMod" name="imgAprobarCompromisoContMod" 	value="Autorizar"	onclick="autorizaConvenio();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarFechasContMod" name="imgAprobarFechasContMod" 	value="Autorizar Fechas"	onclick="autorizarFechas();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarPartidasContMod" name="imgAprobarPartidasContMod" 	value="Autorizar Partidas"	onclick="autorizarFechas();" />
							
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverpreCompromisoContMod" name="imgDevolverpreCompromisoContMod" 	value="Devuelve"	onclick="cancelarPrecompromiso();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProcedimiento" id="lblProcedimiento" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						<input type="text" class="form-control transpInput" name="lblContrato" id="lblContrato" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalAnterior" id="lblTotalAnterior" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalModificado" id="lblTotalModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal" readonly />
					</div>
				</div>
				<div class="row" id="textAutorizaContrto" style="display: none;">
					<div class="col">
						<p style="color: red; ">Solicita a tú Jefe la autorización de modificación al contrato.</p>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Montos a precomprometer del convenio modificatorio</legend>
			<div class="form-group" id="trinputsMontos">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<label for="mImporteTotal">Monto Contrato:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="20"  aria-describedby="basic-addon1"  id="mImporteTotal" name="mImporteTotal" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Compromiso Actual:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="mComprometido" name="mPreComprometer" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Saldo Compromiso:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="difPrecompromiso" name="mPreComprometido" value="0">
						</div>
					</div>
				</div>
				<div class="row" id="apartadoNotice">
					<div class="col">
						<span style="color:#33CC00;">Monto cubierto por el apartado: <span id="aptdReal"></span>.</span>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" id="trEditarpreCompromisoCont" style="display: none">
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
				<div class="row" id="tr_dt_suficiencia">
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
				<div class="row" >
					<div class="input-group">
						<div class="col">
							<table id="dt_grabaprecompE" class="table table-striped table-bordered dt-responsive nowrap" style="visibility: hidden; width: 100%">
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
				<div class="row" >
					<div class="input-group">
						<div class="col">
							<table id="dt_grabaprecompD" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%;visibility: hidden">
								<thead >
									<tr>
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
				<div class="row" style="display: none;" >
					<div class="input-group">
						<div class="col">
							<table id="dt_urcc" class="table table-striped table-bordered dt-responsive nowrap" >
								<thead >
									<tr>
										<th>
											CentroContable
										</th>
										<th>
											UnidadEjecutira
										</th>
									</tr>										
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
	
		<!-- Hidden Section -->
		<!-- Valores de caratula -->
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
    	<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
    	<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
    	<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin() %>"/>
    	<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	    
    	<input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
    	<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
    	<input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
    	<input type="hidden" name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" value="<%=cIdContratoDefinitivo + "#M" + cIdConsecutivoMod%>" />
    	<input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
	    <input type="hidden" name="mesDisponible" id="mesDisponible" value="1"/>
	    <input type="hidden" name="tipoMod" id="tipoMod" />
        <input type="hidden" name="totalAnterior" id="totalAnterior" />
	    <input type="hidden" name="totalMod" id="totalMod" />
	    <input type="hidden" name="totalNuevo" id="totalNuevo" />
	    <input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMPROMISO">
	    <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="nTipoPago" id="nTipoPago" />
	    
	    <!-- Para unir apartado con precompromiso -->
	    <input type="hidden" name="nApartadosUsados" id="nApartadosUsados" />
	    <input type="hidden" name="mApartadoReal" id="mApartadoReal" />
	    
	    <!-- Valores de Precompromiso -->
	    <input type="hidden" name="folioCasoPreCompromiso" id="folioCasoPreCompromiso" />
	    <input type="hidden" name="foliosCasoPreCompromiso" id="foliosCasoPreCompromiso" />
		<input type="hidden" name="cIdUsuarioResponsable" id="cIdUsuarioResponsable" value="Usuario">
		
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
		
		<input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
		<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
		
		<input type="hidden" name="cIdContratoH" id="cIdContratoH"/>
		<input type="hidden" name="rfcProv" id="rfcProv"/>
		<input type="hidden" name="cIdRFC" id="cIdRFC"/>
		<input type="hidden" name="origen" id="origen" value="CONTRATO MODIFICADO"/>
		<input type="hidden" name="nFolioPreCompromisoPRCP" id="nFolioPreCompromisoPRCP"/>
		<input type="hidden" name="tipoOperacion" id="tipoOperacion"/>
		<input name="cAccion" id="cAccion" type="hidden">
		<input name="FOLIO" id="FOLIO" type="hidden">
		<input name="cIdDocumento" id="cIdDocumento" type="hidden">
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<input type="hidden" name="estadoPartidaProcedimiento" id="estadoPartidaProcedimiento"/>
		<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
		<input type="hidden" name="mPrecompromisoReal" id="mPrecompromisoReal" value="0"/>
		<input type="hidden" name="sumaImporteTotal" id="sumaImporteTotal" value="0"/>
		<input type="hidden" id="cIdEntidadContable1" name="cIdEntidadContable1" value="<%=cCentroContable%>">
		<input type="hidden" name="existePrecom" id="existePrecom" />
		<input type="hidden" name="cNoConvenio" id="cNoConvenio" value="" />
		<input type="hidden" name="nFolioCompromiso" id="nFolioCompromiso" value="" />
		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
		<input type="hidden" name="ccTem" id="ccTem" value="" />
		<input type="hidden" name="ueTem" id="ueTem" value="" />
		<input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0" />
	</form>
</body>
</html>
