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
		Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		if (usuarioTab == null) {
			response.sendRedirect("../../index.jsp");
			return;
		}
		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Calendar c1 = Calendar.getInstance(); // today
		String today= sdf.format(c1.getTime());
		
		String cUR = "";
		String cRamo = "";
		boolean bAplicadoCont=false;
		String name_user=usuarioTab.getLogin();
		String idRol="0";		
		Map rol =usuarioTab.getRoles();
		String cCentroContable="";
		if (usuarioTab.getPropiedades() != null && usuarioTab.getPropiedades().containsKey("CCENTROCONTABLE")){
			cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE").getValor();
		}	
		String cEjercicio = "";
		String cIdTipoPedido= "";
		String cIdUnidadEjecutora = "";
		String nIdConsecutivo = "";		
		if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
			cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
			cIdTipoPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
			cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
			nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_PedidoConsecutivo);		
		}else 
			response.sendRedirect("Pedidos.jsp?tab=0");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
			$("#tbs").val(4);
			showAndHideTabs();
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
				Map botones=nb.getBotones(roles,"Pedidos","presupuestoPedido");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarPresupuestoPed".equals(img)){  
						imgAprobar=1;
					}
					if ("imgDevolverPresupuestoPed".equals(img)){
						imgDevolver=1; 
					}	
				}

				%> 
			roles="<%=roles%>";
			setReadOnly();
			headerQuery();
			initDataTable();
			validaCondicionesIniciales();
			loadClavesPresupuestalesPedido();
			loadEventClickHandlers();
			$("#mImporteTotal").formatCurrency();
			queryFormPost("fnMontoSubtotalRead", {async: false});
			queryFormPost("fnMontoIVARead", {async: false});
			queryFormPost("fnMontoImpuesto1Read", {async: false});
			queryFormPost("fnMontoImpuesto2Read", {async: false});
			queryFormPost("fnMontoImpuesto3Read", {async: false});

			if($("#lblImporteImpuesto1").val() == ""){
				$("#divImporteImpuesto1").css("display","none");
			}
			if($("#lblImporteImpuesto2").val() == ""){
				$("#divImporteImpuesto2").css("display","none");
			}
			if($("#lblImporteImpuesto3").val() == ""){
				$("#divImporteImpuesto3").css("display","none");
			}
			queryFormPost("fnMontoTotalPedido", {async: false});
			//Mostrar boton validaPrecomContMat
			if(parseInt($("#nIdEstadoPed").val(),10)>1){
				$("#validaPrecomContMat").css("display","");
			}
			//Esconde el letrero de precompromiso encontrado
			$("#precompromisoNotice").hide();
			//revisamos si es una copia del pedido
			queryFormPost("revisaCopiaPedido", {async: false});
			if(parseInt($("#estadoPartidaProcedimiento").val(),10) != 3 ){
				queryFormPost("readPrecompromisoTotalPedido", {async: false} );
				//Si el valor del precompromiso disponible es > 0
				if ($("#mPrecompromisoReal").val() > 0){
					$("#mPrecompromisoReal").formatCurrency();
					$("#precomReal").html($("#mPrecompromisoReal").val());
					$("#precompromisoNotice").show();
					if($("#nIdEstadoPed").val() == "1" ){
						//Agrega EP desde precompromiso del procedimiento o consolidado
						queryFormPost("pa_mPedidoPrecompromisoPrcpEP", {async: false});
						loadClavesPresupuestalesPedido();
					}
				}
			}
			
			
			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
			if(nIdEstado == 1){
				$("#preCompromisoPedido").css("display", "none");
				$("#anticiposRetencion").css("display", "none");
			}else{
				$("#preCompromisoPedido").css("display", "block");
				$("#anticiposRetencion").css("display", "block");
			}
			queryFormPost("mUsuarioMismaUE", {async: false   });
			desabilitaRadicado();
			//RADICADO
			queryFormPost("readBanderaRadicado", { async:false });
			if($("#BanderaRadicado").val()=="S"){
				document.getElementById('checkDispRadicado').disabled = false;
			}else{
				document.getElementById('checkDispRadicado').disabled = true;
			}
			desabilitaRadicado();	
		});	
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
			$("#imgDevolverPresupuestoPed").show();
			if (parseInt($("#nIdEstadoPed").val(),10)== 3 || parseInt($("#nIdEstadoPed").val(),10)==4 )
				$("#imgDevolverPresupuestoPed").hide();
			if (parseInt($("#nIdEstadoPed").val(),10)> 1 ){
				document.getElementById("nIdClaveEgresosX").disabled = true;	
				document.getElementById("Agregar").disabled = true;
				$("#imgAprobarPresupuestoPed").css("display", "none");
				$("#spanAprobarPresup").css("display", "none");
				document.getElementById("imgAprobarPresupuestoPed").disabled = true;
			}
		}
		function loadEventClickHandlers(){
			//Elimina la EP de la lista con el dblClick
			$('#dt_clavepresup tr').live('dblclick', function() {
					if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
					&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
						swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
						return;
					}
					$(oTableClaves.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});						
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableClaves );						
					var aData = oTableClaves.fnGetData(anSelected[0]);
					$("#epAUX").val(aData[0]);				
					if ($("#nIdEstadoPed").val() == "1" ){
						var index=($("#epAUX").val());
						var tmp = index.lastIndexOf( "\." );
						var uEje= index.substring( tmp - 3, tmp);
						$("#cIdUnidadEjecutora").val(uEje);
				    	queryFormPost("quitaEPPedidoDelete", {async: false});
				    	queryFormPost("quitaEPPedidoDeleteBorra", {async: false});
				    	guardaBitacora("BORRA_EP",$("#cIdPedido").val()+"/"+$("#cEjercicio").val());
				    	loadClavesPresupuestalesPedido();
				 	}
			});	
		}
		function loadClavesPresupuestalesPedido(){
			//lee las claves presupuestales relacionadas el pedido en la tabla temporal
			if(roles.indexOf("ADMIN_RECMAT") < 0 &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0 && parseInt($("#usuariosMismaUE").val(),10)==0){
				var qw = " cEjercicio = '" + $("#cEjercicio").val() +
				 "' AND cIdTipoPedido = '" + $("#cIdTipoPedido").val() + 
				 "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutoraEP").val() +
				 "' AND cIdPedido = '" + $("#cIdPedido").val()+"'";	
			}else{
				//Greyes: debe mostrar todas las EP que ya se agregaron sin importar quien sea el que lo ve
				var qw = " cEjercicio = '" + $("#cEjercicio").val() +
					"' AND cIdTipoPedido = '" + $("#cIdTipoPedido").val() + 
					"' AND cIdPedido = '" + $("#cIdPedido").val()+"'";
			
			}
			oTableClaves=$('#dt_clavepresup').dataTable( {
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tPedidoEP_TMP&qw="+qw,
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
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;
		}
		function headerQuery(){
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});		
			queryFormPost("cg_roleRead", {async: false});
		}
		function fnClickAddRowC() {
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
				return;
			}
			//agrega una nueva EP, no se pueden agregar EP que no correspondan a la unidad ejecutora
				rowCount = $('#dt_clavepresup tr').length;
			
			 	var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes(); 
			 	var vep = $('#ep').val();
				if (vep == '') 
					return ;
			   for(var i=0;i<aTrs.length;i++){
					// leer eps en tabla tPedidoEP_TMP y no inserta las que ya estan repetidas para evitar errores de primary key
				  queryFormPost("epsRead", {async: false});
				  if(vep == $("#epsConsulta").val()){
				 		swal("Ya existe esa clave ingrese otra..",{icon:"info",button: "Cerrar"});
				  		return;
			   		}
				}
				var tmp = vep.lastIndexOf( "\." );
				var uEje= vep.substring( tmp - 3, tmp);
				var cint = vep.substring( tmp -3 );
				$("#cIdUnidadEjecutoraEP").val(uEje);
				$("#ClaveInterna").val(cint);
				queryFormPost("agregaEPPedidoCreate", {async: false});
				loadClavesPresupuestalesPedido();
				guardaBitacora("AGREGA_EP",$('#cIdPedido').val()+"/"+$('#cEjercicio').val());
				$('#ep').val("");
		}

		function buscaClavePresupuestal()
		{  	
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
				return;
			}
			queryFormPost("getcIdUsuarioCreacion", {async: false});
			//Llama al Grid del Fiananciero para obtener otra EP
			pp = window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
					+ '&cIdDocumento=' + $('#cIdPedido').val()+ '&cuentaDisponible=' + $('#cuentaDisponible').val()
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
			return false;
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
		function guardaPedido(){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
				return;
			}
			var imgAprobar='<%=imgAprobar%>';
			if(imgAprobar==0){
				var proc=""+$("#cEjercicio").val()
							+","+$("#cIdPedido").val()
							+","+$("#U_LOGIN").val()
							+","+$("#nTipoPago").val()
							+","+$("#cIdProcedimiento").val()
							+","+$("#cIdRFC").val();

				
				//Si existe precompromiso, recarga las eps de apartado
				if (quitaFmt($("#mPrecompromisoReal").val()) > 0){
					queryFormPost("pa_mPedidoPrecompromisoPrcpEP", {async: false});
				}
				
				//Llama al PedidoServlet para llamar el stored procedure que aprueba el Pedido
				$.getJSON("../../servlet/PedidoServlet?operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
							for(var i = 0; i < j.length; i++)
								 var col=j[i].Col1;
							switch(col){
							case "0": 
							  	//Bitácora
								$("#cAccion").val("APRUEBA_PRESUPUESTO");
								$("#cIdDocumento").val($("#cPedidoDefinitivo").val());
								queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							
								swal("El Pedido se ha aprobado ,tiene que realizar el Precompromiso",{icon:"info",button: "Cerrar"});
								queryFormPost("mPedidoHeaderRead", {async: false,
									callback : function() 
									{
										$("#cPedidoContDefinitivo").val($("#cPedidoDefinitivo").val());
										queryFormPost("insertaretencionesEnAutomatico", {async: false});
									}
								});
								//deshabilita la imagen de aprobar una vez que se ha aprobado el pedido
								document.getElementById("nIdClaveEgresosX").disabled = true;	
								document.getElementById("Agregar").disabled = true;
								document.getElementById("imgAprobarPresupuestoPed").disabled = true;
								document.getElementById("imgDevolverPresupuestoPed").disabled = false;
								$("#imgDevolverPresupuestoPed").show();
								$("#nIdEstadoPed").val("2");
								
								window.location = "Pedidos.jsp?tab=5&nIdEstadoPedido="+$("#nIdEstadoPed").val();
							break;
							case "1":  
								swal("Este Pedido ya se encuentra aprobado",{icon:"info",button: "Cerrar"});
							break;
							case "2":  
								swal("Este proveedor no se encuentra registrado en el sistema Financiero",{icon:"info",button: "Cerrar"});
							break;
							case "3":
								swal("No se han registrado EPs para este pedido",{icon:"info",button: "Cerrar"});
							break;
							case "4":  
								swal("Ha ocurrido un error al registrar el pedido, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
							break;
							case "5":  
								swal("Ha ocurrido un error al registrar las EPs, por favor contacte a su administrador.",{icon:"info",button: "Cerrar"});
							break;
							case "6","7":  
								swal("Ha ocurrido un error al actualizar el pedido, por favor contacte a su administrador.",{icon:"info",button: "Cerrar"});
							break;
							case "-1":  
								swal("NO SE HA PODIDO APROBAR EL PRESUPUESTO INTENTELO NUEVAMENTE.",{icon:"info",button: "Cerrar"});
							break;
							}
					});
			}
		}
		//devuelve el pedido
		function devuelvePedido(){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos",{icon:"info",button: "Cerrar"});
				return;
			}
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver==0){
			if(parseInt($("#nIdEstadoPed").val(),10)<3 || parseInt($("#nIdEstadoPed").val(),10)==5 ){
						var proc=""+$("#cEjercicio").val()
						+","+$("#cPedidoDefinitivo").val()
						+","+$("#cIdPedido").val()
						+","+$("#U_LOGIN").val()+"";
					$.getJSON("../../servlet/PedidoServlet?operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1;
		                switch(col){
						case "0":  
						     //Bitácora
							$("#cAccion").val("DEVUELVE_PRESUPUESTO");
							$("#cIdDocumento").val($("#cPedidoDefinitivo").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							swal("Se ha devuelto el pedido",{icon:"info",button: "Cerrar"});
							queryFormPost("mPedidoHeaderRead", {async: false});
							document.getElementById("nIdClaveEgresosX").disabled = false;	
							document.getElementById("Agregar").disabled = false;
							document.getElementById("imgAprobarPresupuestoPed").disabled = false;
							$("#imgDevolverPresupuestoPed").hide();
							$("#nIdEstadoPed").val("1"); 
							window.location = "Pedidos.jsp?tab=4&nIdEstadoPedido="+$("#nIdEstadoPed").val();
						break;
						case "1":  
							swal("No se puede regresar el pedido porque su estado no lo permite",{icon:"info",button: "Cerrar"});
						break;
						case "2":  
							swal("Ha ocurrido un error al eliminar las EPs del pedido, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
						break;
						case "3":  
							swal("Ha ocurrido un error devolver el pedido, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
						break;
						case "4":  
							swal("Ha ocurrido un error al cambiar el estado del pedido, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
						break;
						case "5":  
							swal("Ha ocurrido un error al borrar las EP's de la tabla temporal.",{icon:"info",button: "Cerrar"});
						break;
						case "-1":  
							swal("NO SE HA PODIDO DEVOLVER EL PRESUPUESTO INTENTELO NUEVAMENTE",{icon:"info",button: "Cerrar"});
						break;
	                 }
				 	loadClavesPresupuestalesPedido();
				});
			}
		}
	}
	function POPUPpreVentanilla(){
	  var pedido=encodeURIComponent($("#cPedidoDefinitivo").val());
	  window.open("validaPrecomContratoMateriales.jsp?cIdContrato="+pedido, 'Notas', 'status=1, width=1000px, height=780px, left=150px');
	}
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
	function desabilitaRadicado(){
		if(quitaFmt($("#mPrecompromisoReal").val()) > 0 || parseInt($("#nIdEstadoPed").val(),10)>1){
			document.getElementById('checkDispRadicado').disabled = true;
		}else{
			document.getElementById('checkDispRadicado').disabled = false;
		}
		activacheck();
	}
	function muestraDispRadicado(){
		swal({
			title: "Si desea activar o desactivar el check se perderan las ep´s agregadas, ¿desea continuar?",
			text: "",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					activacheck();
					return;
				}else{
					queryFormPost("quitaEPSPedidoDelete",{async:false});
					$("#cuentaDisponible").val('82106');	
					if($('#checkDispRadicado').is(':checked')){
						$("#cuentaDisponible").val('82109');
					}
					queryFormPost("mPedidoCTAUpdate",{async:false});
					loadClavesPresupuestalesPedido();
				}
			});
	}
	function activacheck(){
		$("#checkDispRadicado").attr("checked",false);
		if($("#cuentaDisponible").val()=='82109'){
			$("#checkDispRadicado").attr("checked",true);
		}
	}	
	</script>
</head>  
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form action="#" name="formPedido">
  		<input type="hidden" name="nIdConsecutivoAdj" id="nIdConsecutivoAdj" value="-1"/>
		<div id="container" class="container" style="width: 95%">
			
			<fieldset>
				<legend>Partidas del Pedido</legend>
					<table align="left" cellpadding="2" width="100%">
				    	<tr>
				    		<td align="right" colspan="2">
								<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarPresupuestoPed" 	name="imgAprobarPresupuestoPed" 	value="Aprobar" onclick="guardaPedido();"	/>
								<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverPresupuestoPed" 	name="imgDevolverPresupuestoPed" 	value="Devolver" onclick="devuelvePedido();"/>
								<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Pedidos.jsp?tab=0';" />
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
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblPedido" id="lblPedido" readonly /></td>
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
				    	<tr id="precompromisoNotice">
							<td align="left" colspan="2"><span style="color:#33CC00;">Monto cubierto por el precompromiso: <span id="precomReal"></span></span></td>
						</tr>
						<tr style="display: none;">
							<td>
							  <input type="button" name="validaPrecomContMat" id="validaPrecomContMat" onclick="POPUPpreVentanilla();" value="As&iacute; lo ver&iacute;a Ventanilla" class="btnInterfaceBG ui-button ui-corner-all"  style="display:none; color: red; border-bottom: 0;background-color: white;">
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
							<input type="button" name="nIdClaveEgresosX" id="nIdClaveEgresosX" size="5" value="..." onclick="buscaClavePresupuestal()" class="btnInterfaceBG ui-button ui-corner-all">
						</td>
			            <td></td>
						<td><input type="button" value="Agregar" name="Add2" id="Agregar"	onclick="fnClickAddRowC();" class="btnInterfaceBG ui-button ui-corner-all"></td>
  					</tr>
				</table>	    			
   				<table  id="dt_clavepresup" class="display" style="width: 100%">
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
	        <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdSubPartida" id="cIdSubPartida"/>
		    <input type="hidden" name="cIdPedido" id="cIdPedido"/>
		    <input type="hidden" name="cIdRFC" id="cIdRFC"/>
		    <input type="hidden" id="nOrden" name="nOrden" value=""/>
		    <input type="hidden" id="Partida" name="Partida" value="2"/>
			<input type="hidden" id="Partida2" name="Partida2" value="3"/>
			<input type="hidden" id="Partida3" name="Partida3" value="5"/>
			<input type="hidden" id="Partida1" name="Partida1" value="1"/>
			<input type="hidden" id="ClaveInterna" name="ClaveInterna" />
			<input type="hidden" name="nTipoPago" id="nTipoPago" />
			
			<!-- Para unir apartado con precompromiso -->
		    <input type="hidden" name="nApartadosUsados" id="nApartadosUsados" />
		    <input type="hidden" name="mPrecompromisoReal" id="mPrecompromisoReal" />
			<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
			<input type="hidden" name="cIdConsolidado" id="cIdConsolidado" />
			<input type="hidden" name="mTotalPedido" id="mTotalPedido" />
			
		    <!--  Auxiliares para consulta -->		    
	       	<input type="hidden" name="epAUX" id="epAUX" />
	       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP"  value="<%= usuarioTab.getU_UR()%>"/>
	       	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	    
		    <!-- Resultado de consultas -->
		     <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />	   	     
		    <input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
		    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
		    <input type="hidden" name="nIdEstadoPed" id="nIdEstadoPed" />
		    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		    <input type="hidden" name="epsConsulta" id="epsConsulta" />
		    <!--<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />-->
		     <input name="cAccion" id="cAccion" type="hidden">
			<input name="cIdDocumento" id="cIdDocumento" type="hidden">
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
            <input type="hidden" name="estadoPartidaProcedimiento" id="estadoPartidaProcedimiento" value=""/>
	      	<input type="hidden" name="centralizado" id="centralizado" />
		    <input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable"  value="<%=cCentroContable%>"/>
		    <input type="hidden" name="cPedidoContDefinitivo" id="cPedidoContDefinitivo" />	
		    <input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
		    <input type="hidden" name="BanderaRadicado" id="BanderaRadicado" value="N" />
		    <input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />	
		    
	    </div>
	</form>
  </body>
</html>
