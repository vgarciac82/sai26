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
		String today= sdf.format(c1.getTime());
		String cCentroContable="";
		String cUR = "";
		String cRamo = "";
		boolean bAplicadoCont=false;
		Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		
		if (usuarioTab == null) {
			response.sendRedirect("../../index.jsp");
			return;
		}
		if (usuarioTab.getPropiedades() != null && usuarioTab.getPropiedades().containsKey("CCENTROCONTABLE")){
			cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE").getValor();
		}
		String name_user=usuarioTab.getLogin();		
		Map rol =usuarioTab.getRoles();		
		String cEjercicio = "";
		String cIdTipoContrato= "";
		String cIdUnidadEjecutora = "";
		String nIdConsecutivo = "";		
		if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
			cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
			cIdTipoContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
			cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
			nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);		
		}else 
			response.sendRedirect("Contratos.jsp?tab=0");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>    
    <title>Presupuesto</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	
	<script type="text/javascript" charset="utf-8">
		var oTableSuficiencia;
		var oTableClaves;
		var oTableCompromiso;
		var roles='';
		$(document).ready(function() {	
			$("#tbs").val(3);
			showAndHideTabs();
			<%
			    String roles="";
				//botones
				NegativaBoton NegBoton= new NegativaBoton();
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
				Map botones=nb.getBotones(roles,"Contratos","presupuestoContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarPresupuestoCont".equals(img)){  
						imgAprobar=1;
					}
					if ("imgDevolverPresupuestoCont".equals(img)){
						imgDevolver=1; 
					}	
				}

				%> 
			roles="<%=roles%>";
			setReadOnly();
			headerQuery();
			initDataTable();
			validaCondicionesIniciales();
			loadClavesPresupuestalesContrato();
			loadEventClickHandlers();
			$("#mImporteTotal").formatCurrency();
			
			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			if(nIdEstado == 1){
				$("#preCompromisoContrato").css("display", "none");
				$("#anticiposRetencion").css("display", "none");
			}else{
				$("#preCompromisoContrato").css("display", "block");
				//Mostrar boton validaPrecomContMat
				$("#validaPrecomContMat").css("display","");
				$("#anticiposRetencion").css("display", "block");
			}
			queryFormPost("mUsuarioMismaUE", {async: false   });
			//RADICADO
			queryFormPost("readBanderaRadicado", { async:false });
			if($("#BanderaRadicado").val()=="S"){
				document.getElementById('checkDispRadicado').disabled = false;
			}else{
				document.getElementById('checkDispRadicado').disabled = true;
			}
			desabilitaRadicado();
			
			
		});	
		
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
		
		function validaCondicionesIniciales(){
			//solo cuando esta capturado el pedido se puede agregar EPs
			if (parseInt($("#nIdEstado").val(),10)== 1 || parseInt($("#nIdEstado").val(),10)== 3 || parseInt($("#nIdEstado").val(),10)== 4  ){
				$("#imgDevolverPresupuestoCont").hide();	
			}
			if (parseInt($("#nIdEstado").val(),10)> 1 ){
				document.getElementById("nIdClaveEgresosXContrato").disabled = true;	
				document.getElementById("AgregarEPContrato").disabled = true;
				document.getElementById("imgAprobarPresupuestoCont").disabled = true;
				$("#imgAprobarPresupuestoCont").hide();
			}
			if(parseInt($("#validacionMontoTipoAdj").val(),10)== 0 &&  $("#lJustificaTipoProced").val()==0){
				$("#imgAprobarPresupuestoCont").hide();
				$("#legenTipoProcedimiento").show();
			}
		}
		function loadEventClickHandlers(){
			$('#dt_clavepresup tr').live('dblclick', function() {
				if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
					&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
					swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
					return;
				}       
					$(oTableClaves.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});						
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableClaves );						
					var aData = oTableClaves.fnGetData(anSelected[0]);							
					$("#epAUX").val(aData[0]);				
					if ($("#nIdEstado").val() == "1" ){
						 var index=($("#epAUX").val());
						  var tmp = index.lastIndexOf( "\." );
				          var uEje= index.substring( tmp - 3, tmp);
				            $("#cIdUnidadEjecutora").val(uEje);
							queryFormPost("quitaEPContratoDelete", {async: false});
							queryFormPost("quitaEPContratoDeleteBorra", {async: false});
							guardaBitacora("BORRA_EP",$('#cIdContratoMat').val());
							loadClavesPresupuestalesContrato();
				 	}
			});	
		}
		function loadClavesPresupuestalesContrato(){
				if($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") < 0 &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0 && parseInt($("#usuariosMismaUE").val(),10)==0){
					var qw = " cEjercicio = '" + $("#cEjercicio").val() +
					 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
					 "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutoraEP").val() +
					 "' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";	
					 }else{
						 
					 var qw = " cEjercicio = '" + $("#cEjercicio").val() +
					 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
					 "' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";
						 }
			 		
			oTableClaves=$('#dt_clavepresup').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					sScrollX: "100%",
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
					aoColumns: [
						{ sName: "nIdClaveEgresos"},
						{ sName: "ClaveInterna" }
						]
			}) ;
		}
		function initDataTable(){
			$('#dt_clavepresup').dataTable(
				{         
	   			    "bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false, 
					"sScrollY": 100,         
			        "sScrollX": "100%",
			        "sScrollXInner": "100%",
			        "bScrollCollapse": true,
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );
		}		
		function setReadOnly(){
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblContrato").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;
		}
		function headerQuery(){
			queryFormPost("mContratoCaratulaRead", {async: false});
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("fnMontoImpuesto1ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto2ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto3ReadContrato", {async: false});
			
			if($("#lblImporteImpuesto1").val() == ""){
				$("#divImporteImpuesto1").css("display","none");
			}
			if($("#lblImporteImpuesto2").val() == ""){
				$("#divImporteImpuesto2").css("display","none");
			}
			if($("#lblImporteImpuesto3").val() == ""){
				$("#divImporteImpuesto3").css("display","none");
			}

			queryFormPost("cg_roleRead", {async: false});
			
			//Esconde el letrero de precompromiso encontrado
			$("#precompromisoNotice").hide();
			
			//revisamos si es una copia del contrato
			queryFormPost("revisaCopiaPedido", {async: false});
			if(parseInt($("#estadoPartidaProcedimiento").val(),10) != 3 ){
				queryFormPost("readPrecompromisoTotal", {async: false} );

				//Si el valor del apartado disponible es > 0
				if ($("#mPrecompromisoReal").val() > 0){
			  		$("#mPrecompromisoReal").formatCurrency();
					$("#precomReal").html($("#mPrecompromisoReal").val());
					$("#precompromisoNotice").show();
					
					if ($("#nIdEstado").val() == "1" ){
						//agrega EP desde apartado
						queryFormPost("pa_mContratoPrecompromisoPrcpEP", {async: false});
						loadClavesPresupuestalesContrato();
					}
				}
			}		
		}
		function formateaMoneda(importe){
			var importeSeparado = importe.toString().split("\.");
			var importeParte1 = importeSeparado[0];
			var cont=0;
			var tem = "";
			
			for(var i=importeParte1.length; i > 0; i--){
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
		function fnClickAddRowC() {
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
				return;
			}
				rowCount = $('#dt_clavepresup tr').length;
				var vep = $('#ep').val();
				if (vep == '') {
					return ;
				}
				var tmp = vep.lastIndexOf( "\." );
				var uEje= vep.substring( tmp - 3, tmp);
				var cint = vep.substring( tmp -3 );
				$("#cIdUnidadEjecutoraEP").val(uEje);
				$("#ClaveInterna").val(cint);
				queryFormPost("agregaEPContratoCreate", {async: false});
				loadClavesPresupuestalesContrato();
				guardaBitacora("AGREGA_EP",$('#cContratoDefinitivo').val());
				$('#ep').val("");
		}
		function buscaClavePresupuestal()
		{
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
				return;
			}
			var idContrato = $('#cIdTipoContrato').val() + "-" + $('#cIdUnidadEjecutora').val() + "-" + $('#nIdConsecutivo').val();
			$('#cIdContratoMat').val(idContrato);
			queryFormPost("getcIdUsuarioCreacionContrato", {async: false});
			pp = window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
					+ '&cIdDocumento=' + $('#cIdContratoMat').val() + '&cIdRFC=' + $('#cIdRFC').val()
					 + '&cIdProcedimiento=' + $('#cIdProcedimiento').val()+ '&cuentaDisponible=' + $('#cuentaDisponible').val()
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
		}
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
		function guardaContrato(){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
				return;
			}			 
			 var imgAprobar='<%=imgAprobar%>';
			 if (imgAprobar==0){
				var proc=""+$("#cEjercicio").val()
						+","+$("#cIdContratoMat").val()
						+","+$("#U_LOGIN").val()
						+","+$("#nTipoPago").val()
						+","+$("#cIdProcedimiento").val()
						+","+$("#cIdRFC").val();
				
				
			//Si existe precompromiso, recarga las eps de apartado
			if (quitaFmt($("#mPrecompromisoReal").val()) > 0){
				queryFormPost("pa_mContratoPrecompromisoPrcpEP", {async: false});
			}
			
			 $.getJSON("../../servlet/ContratoServlet?operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1;
		                switch(col){
						case "0": 
						   	//Bitácora
							$("#cAccion").val("APRUEBA_PRESUPUESTO");
							$("#cIdDocumento").val($("#cContratoDefinitivo").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							alert("El Contrato se ha aprobado ,tiene que realizar el Precompromiso para que pase a SAI Presupuestado");
							queryFormPost("mContratoHeaderRead", {async: false});
							$("#cPedidoContDefinitivo").val($("#cContratoDefinitivo").val());
							queryFormPost("insertaretencionesEnAutomatico", {async: false});
							document.getElementById("nIdClaveEgresosXContrato").disabled = true;	
							document.getElementById("AgregarEPContrato").disabled = true;
							document.getElementById("imgAprobarPresupuestoCont").disabled = true;
							var imgDevolver='<%=imgDevolver%>';
							if (imgDevolver==0){
								document.getElementById("imgDevolverPresupuestoCont").disabled = false;
							}
							$("#nIdEstadoPed").val("2");
							window.location = "Contratos.jsp?tab=4&nIdEstadoContrato="+$("#nIdEstado").val();
						break;
						case "1":  
							alert("Este Contrato ya se encuentra aprobado");
						break;
						case "2":  
							alert("Este proveedor no se encuentra registrado en el sistema Financiero");
						break;
						case "3":  
							alert("No se han registrado EPs para este Contrato");
						break;
						case "4":  
							alert("Ha ocurrido un error al registrar el Contrato, por favor contacte a su administrador");
						break;
						case "5":  
							alert("Ha ocurrido un error al registrar las EPs, por favor contacte a su administrador");
						break;
						case "6","7":  
							alert("Ha ocurrido un error al actualizar el Contrato, por favor contacte a su administrador");
						break;
						
						case "-1":  
							alert("NO SE HA PODIDO APROBAR EL PRESUPUESTO INTENTELO NUEVAMENTE");
						break;
						
	                 	}
				});
			 }
			
		}
		function devuelveContrato(){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
				return;
			}
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver==0){
			if(parseInt($("#nIdEstado").val(),10)<3 || parseInt($("#nIdEstado").val(),10)==5 ){
			var proc=""+$("#cEjercicio").val()
						+","+$("#cContratoDefinitivo").val()
						+","+$("#cIdContratoMat").val()
						+","+$("#U_LOGIN").val()+"";
						
					$.getJSON("../../servlet/ContratoServlet?operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
						case "0":  
						    //Bitácora
							$("#cAccion").val("DEVUELVE_PRESUPUESTO");
							$("#cIdDocumento").val($("#cContratoDefinitivo").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						  	alert("Se ha devuelto el Contrato");
							queryFormPost("mContratoHeaderRead", {async: false});
							document.getElementById("nIdClaveEgresosXContrato").disabled = false;	
							document.getElementById("AgregarEPContrato").disabled = false;
							var imgAprobar='<%=imgAprobar%>';
							 if (imgAprobar==0){
								document.getElementById("imgAprobarPresupuestoCont").disabled = false; 
							 }

						document.getElementById("imgDevolverPresupuestoCont").disabled = true;
						$("#nIdEstadoPed").val("1");
						window.location = "Contratos.jsp?tab=3&nIdEstadoContrato="+$("#nIdEstado").val();
						break;
						case "1":  
							alert("No se puede regresar el Contrato porque su estado no lo permite");
						break;
						case "2":  
							alert("Ha ocurrido un error al eliminar las EPs del Contrato, por favor contacte a su administrador");
						break;
						case "3":  
							alert("Ha ocurrido un error devolver el Contrato, por favor contacte a su administrador");
						break;
						case "4":  
							alert("Ha ocurrido un error al cambiar el estado del Contrato, por favor contacte a su administrador");
						break;
						case "5":  
							alert("Ha ocurrido un error al borrar las EP's de la tabla temporal.");
						break;
						case "-1":  
							alert("NO SE PUEDE DEVOLVER EL PRESUPUESTO INTENTELO NUEVAMENETE");
						break;
	                 	}
				 		loadClavesPresupuestalesContrato();
				});
			}
		}
	}
						
		/*function openGrid(){
			window.open('../MultiReporteGridSacel.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
		}*/
	function POPUPpreVentanilla(){
		var contrato=encodeURIComponent($("#cContratoDefinitivo").val())
		window.open("validaPrecomContratoMateriales.jsp?cIdContrato="+contrato, 'Notas', 'status=1, width=1000px, height=780px, left=150px');
	}
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
	function desabilitaRadicado(){
		if(quitaFmt($("#mPrecompromisoReal").val()) > 0 || parseInt($("#nIdEstado").val(),10)>1){
			document.getElementById('checkDispRadicado').disabled = true;
		}else{
			document.getElementById('checkDispRadicado').disabled = false;
		}
		activacheck();
	}
	function muestraDispRadicado(){
		if (!confirm('Si desea activar o desactivar el check se perderan las ep´s agregadas, ¿desea continuar?')) {
			activacheck();
			return;
		}
		queryFormPost("quitaEPSContratoDelete",{async:false});
		$("#cuentaDisponible").val('82106');	
		if($('#checkDispRadicado').is(':checked')){
			$("#cuentaDisponible").val('82109');
		}
		queryFormPost("mContratoCTAUpdate",{async:false});
		loadClavesPresupuestalesContrato();
	}
	function activacheck(){
		$("#checkDispRadicado").attr("checked",false);
		if($("#cuentaDisponible").val()=='82109'){
			$("#checkDispRadicado").attr("checked",true);
		}
	}
	</script>
</head>  
<body id="dt_example">
  	<form action="#" name="formContrato"> 
		<div id="container" class="container" style="width:95%">
			<fieldset  >
				<legend>Presupuesto del Contrato</legend>
					<table align="left" cellpadding="2" width="100%">
				    	<tr>
				    		<td align="right" colspan="2">
								<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarPresupuestoCont" 	name="imgAprobarPresupuestoCont" 	value="Aprobar" onclick="guardaContrato();"	/>
								<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverPresupuestoCont" 	name="imgDevolverPresupuestoCont" 	value="Devolver" onclick="devuelveContrato();"/>
								<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Contratos.jsp?tab=0';" />
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblContrato" id="lblContrato" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblSubtotal" id="lblSubtotal" readonly /></td>
				    	</tr>
				    	<tr id="divImporteIVA">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto1">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto2">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto2" id="lblImporteImpuesto2" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto3">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto3" id="lblImporteImpuesto3" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent; color: blue;" name="lblTotalMax" id="lblTotalMax" readonly /></td>
				    	</tr>
				    	<tr id="precompromisoNotice">
							<td align="left" colspan="2"><span style="color:#33CC00;">Monto cubierto por el precompomiso: <span id="precomReal"></span>.</span></td>
						</tr>
						<tr id="legenTipoProcedimiento" style="display: none;">
							<td align="left" colspan="2"><span style="color:red;">Est&eacute; contrato no se podra aprobar. El tipo de procedimiento seleccionado no corresponde con el rango de montos m&iacute;nimos y m&aacute;ximos. Tendras que devolver el procedimiento a captura y modificarlo en la car&aacute;tula.</span></td>
						</tr>
						<tr style="display: none;">
							<td>
								  <input type="button" name="validaPrecomContMat" id="validaPrecomContMat" onclick="POPUPpreVentanilla();" value="As&iacute; lo ver&iacute;a Ventanilla" style="display:none; color: red; border-bottom: 0;background-color: white;">
							</td>	
						</tr>				    	
				    </table>
			</fieldset>
					
   			<fieldset  ><legend>Claves</legend>		    			
   				<table height="66" width="98%" border="0">
	                <tr>
   						<td align="left"  colspan="2">
   						<label>Disponible Radicado:</label> <input type="checkbox" name="checkDispRadicado" id="checkDispRadicado" onclick="muestraDispRadicado()" />
   						</td>
   					</tr>
	                <tr> 
	                	<td>E.P.</td>
						<td>&nbsp;</td>
	                </tr> 
	                <tr> 
	                  <td><input type="text" id="ep" name="ep" readonly size="65" value="">
	                  <input type="button" name="nIdClaveEgresosXContrato" id="nIdClaveEgresosXContrato" size="5" value="..." onclick="buscaClavePresupuestal()" class="btnInterfaceBG ui-button ui-corner-all"></td>
	                  <td><input type="button" value="Agregar" name="Add2" id="AgregarEPContrato"	onclick="fnClickAddRowC();" class="btnInterfaceBG ui-button ui-corner-all"></td>
	                </tr>
	              </table>
   				<table  id="dt_clavepresup" class="display">
					<thead>
						<tr align="left">
							<th>C&oacute;digo SAI</th> 
							<th>Clave SHCP</th> 
					 	</tr> 
					</thead>
						<tbody>
						</tbody> 
				</table>		
  				</fieldset>
	        <!-- Hiddens de sesion -->
	        <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdSubPartida" id="cIdSubPartida"/>
		    <input type="hidden" name="cIdContratoMat" id="cIdContratoMat"/>
		    <input type="hidden" name="cIdRFC" id="cIdRFC"/>
		    <input type="hidden" id="nOrden" name="nOrden" value=""/>
		    <input type="hidden" id="Partida" name="Partida" value="2"/>
			<input type="hidden" id="Partida2" name="Partida2" value="3"/>
			<input type="hidden" id="Partida3" name="Partida3" value="5"/>
			<input type="hidden" id="Partida1" name="Partida1" value="1"/>
			<input type="hidden" id="ClaveInterna" name="ClaveInterna" />
			<input type="hidden" name="nTipoPago" id="nTipoPago" />
			
			<!-- Para unir apartado con precompromiso -->
		    <input type="hidden" name="mPrecompromisoReal" id="mPrecompromisoReal" />
			<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
			<input type="hidden" name="cIdConsolidado" id="cIdConsolidado" />
			
		    <!--  Auxiliares para consulta -->		    
	       	<input type="hidden" name="epAUX" id="epAUX" />
	       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuarioTab.getU_UR()%>" />
	       	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	    
		    <!-- Resultado de consultas -->
		     <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" />	   	     
		    <input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
		    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />
		    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		    <input name="cAccion" id="cAccion" type="hidden">
			<input name="estadoPartidaProcedimiento" id="estadoPartidaProcedimiento" type="hidden">
			<input name="cIdDocumento" id="cIdDocumento" type="hidden">
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
			<input type="hidden" name="cPedidoContDefinitivo" id="cPedidoContDefinitivo" />	    
			<input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable"  value="<%=cCentroContable%>"/>
			<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
			<input type="hidden" name="BanderaRadicado" id="BanderaRadicado" value="N" />
			<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
			<input type="hidden" name="validacionMontoTipoAdj" id="validacionMontoTipoAdj" value="0" />	
			<input type="hidden" id="lJustificaTipoProced" name="lJustificaTipoProced" value="0" />
			<input type="hidden" name="nIdConsecutivoAdj" id="nIdConsecutivoAdj" value=""/>
	    </div>
	</form>
  </body>
</html>
