<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();
	String roles="";
	Map rol =usuarioTab.getRoles();
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
    <title>Car&aacute;tula Pedido</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">	
		$(document).ready(function() {
			$("#tbs").val(1);
			showAndHideTabs();
			<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Pedidos","caratulaPedido");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

			%>
			var roles="<%=roles%>";
			//primero se tienen que validar que los datos de las fechas asi como la informacion de la caratula sea la correcta para poder habilitar las pestañas
		    queryFormPost("consultaPedidoImpuestoAdicional1", {async : false});
			queryFormPost("consultaPedidoImpuestoAdicional2", {async : false});
			queryFormPost("consultaPedidoImpuestoAdicional3", {async : false});
			
			queryFormPost("consultaPedidoOtroImpuesto1", {async : false});
			queryFormPost("consultaPedidoOtroImpuesto2", {async : false});
			queryFormPost("consultaPedidoOtroImpuesto3", {async : false});
		    deshabilitaTabs();
			setFieldsInit();
			validaCondicionesIniciales();
			if($("#cDescripcionImpuestoAdicional1").val()!="" || $("#cDescripcionImpuestoAdicional2").val()!="" || $("#cDescripcionImpuestoAdicional3").val()!=""){
				//$("#divImpuestosAdicionales").css("visibility","visible");
				document.getElementById("divImpuestosAdicionales").style.display="block";
				document.getElementById("chkImpuestosAdicionales").checked=true;
			}
			if($("#cDescripcionOtroImpuesto1").val()!="" || $("#cDescripcionOtroImpuesto2").val()!="" || $("#cDescripcionOtroImpuesto3").val()!=""){
				document.getElementById("divOtrosImpuestos").style.display="block";
				document.getElementById("chkOtrosImpuestos").checked=true;
				
				if($("#nIdEstadoPed").val()!=1){
					
					document.getElementById("MESSAGESPAN").style.display="block";
					$("#cDescripcionOtroImpuesto1").prop("readonly",true);
					$("#cDescripcionOtroImpuesto2").prop("readonly",true);					
					$("#cDescripcionOtroImpuesto3").prop("readonly",true);					
					$("#cMontoOtroImpuesto1").prop("readonly",true);					
					$("#cMontoOtroImpuesto2").prop("readonly",true);					
					$("#cMontoOtroImpuesto3").prop("readonly",true);
				}
			}
			if($("#notaPedido").val()!=""){
				//if(document.getElementById("chkNotas").checked){
				document.getElementById("chkNotas").checked=true;
				document.getElementById("divNotas").style.display="block";
			}
			validaFecha();
			if(!validaCampos()){
				       swal("Existe un problema con las Fechas,favor de revisarlas.",{icon:"info",button: "Cerrar"});

			 }else
				 habilitaTabs();
				 //Desabilitar combo box categoria si el usuario es diferente del administrador
				if(roles.toString().indexOf("ADMIN_RECMAT") < 0 &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0 && parseInt($("#usuariosMismaUE").val(),10)==0){//document.getElementById("trErrorFechas").style.visibility='visible';
					$("#cboCategoriaCaratula").css("visibility","hidden");
					$("#Categoria").css("visibility","hidden");					
				}
				else{
					$("#cboCategoriaCaratula").css("visibility","visible");
					$("#Categoria").css("visibility","visible");
				}

				if ($("#cIdTipoProcedimiento").val() == 'PF') {
					$( "#presupuestoPedido" ).attr("disabled", true);
					$( "#preCompromisoPedido" ).attr("disabled", true);
					$( "#pagosPedido" ).attr("disabled", true);
				}
				habilitaCamposEdicion();
				iniciaRadioBtn();
				var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
				if(nIdEstado == 1){
					$("#preCompromisoPedido").css("display", "none");
					$("#anticiposRetencion").css("display", "none");
					queryFormPost("mUpdatePedidocNoCot", {async: false});
				}
				else{
					$("#preCompromisoPedido").css("display", "block");
					$("#anticiposRetencion").css("display", "block");
				}
				//Agrega el firmante de gerente en automatico
				queryFormPost("hayFirmanteAgregado", {async: false});
				if(parseInt($("#hayFirmante").val(),10)==0){
					//obtiene el nidfirmante de cada ue que sea gerente
					queryFormPost("obtieneNidFirmante", {async: false});
					//Se agrega el firmante
					if($("#nIdFirmante").val()!=0)
						queryFormPost("pa_mAgregaFirmantePedidoCreate", {async: false});
				}
		  });		
		function setFieldsInit(){
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;			
			document.getElementById("lblSubtotal").style.readonly=true;
			//asigna el valor de true a readonly para los campos de fechas
			document.getElementById("fechaFalloV").style.readonly=true;
			document.getElementById("fechaFormalizacionV").style.readonly=true;
			document.getElementById("fechaEntregaV").style.readonly=true;			 		
			//Carga de controles y caratula
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalRead", {async: false});
			queryFormPost("fnMontoIVARead", {async: false});
			queryFormPost("fnMontoImpuesto1Read", {async: false});
			queryFormPost("fnMontoImpuesto2Read", {async: false});
			queryFormPost("fnMontoImpuesto3Read", {async: false});
			
			//llena el combo de Categoria
			querySelectPost("CategoriaReadPedido", "cboCategoriaCaratula", {async : false});
			queryFormPost("valorCaratulaPedido", {async : false});//"ValObtenido",
		    $("#cboCategoriaCaratula").val($('#ValObtenido').val());
			querySelectPost("FundamentoLegReadFiltrado", "cboFundamentoLeg", {async : false});
			$("#cboFundamentoLeg").val($('#ValObtenidoFL').val());
			queryFormPost("mUpdateProcedimientoFundamentoLeg", {async: false});
			
			if($("#cboCategoriaCaratula").val()>7){
				$("#nIdFechaFallo").val('2');
			}
			queryFormPost("mPedidoFechaFallo", {async: false});
			if($("#cboCategoriaCaratula").val()==13){
				$("#nIdFechaInicio").val(15);
				$("#nIdFechaFin").val(16);
			}else{
				$("#nIdFechaInicio").val(17);
				$("#nIdFechaFin").val(18);
			}
			queryFormPost("mPedidoFechaFormalizacion", {async: false});
			if($("#cIdTipoProcedimiento").val()=="PS" || $("#cIdTipoProcedimiento").val()=="PA" || $("#cIdTipoProcedimiento").val()=="PL" 
			|| $("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PO"){
				queryFormPost("mContratoFechaInicio", {async: false});
				queryFormPost("mContratoFechaFin", {async: false});
				document.getElementById("trfinicio").style.display="block";
				document.getElementById("trffin").style.display="block";
				document.getElementById("trfent").style.display="none";
				document.getElementById("trfechasParciales").style.display="none";
			}else{
				queryFormPost("mPedidoFechaEntrega", {async: false});
				document.getElementById("trfinicio").style.display="none";
				document.getElementById("trffin").style.display="none";
				document.getElementById("trfent").style.display="block";
				//document.getElementById("trfechasParciales").style.display="block";
				document.getElementById("trfechasParciales").style.display="none";
			}
			$("#fechaInicioV").val($("#fechaInicio").val());
			$("#fechaFinV").val($("#fechaFin").val());
			queryFormPost("mPedidoFechaEntregaConvenio", {async: false}); //cPedidoDefinitivo
			if($("#fechaEntregaTMP").val().length > 0){
				$("#fechaEntrega").val($("#fechaEntregaTMP").val());
			}
			queryFormPost("cg_roleRead", {async: false});
			//Inicializa controles apartir de Hiddens, como auxiliares para el CRUD
			$("#cConceptoV").val($("#cConcepto").val());			  
			$("#cPedidoDefinitivoV").val($("#cPedidoDefinitivo").val());
			//pedido definitivo usado para clausulas
			$("#pedidoDefinitivoAnt").val($("#cPedidoDefinitivoV").val());
			 
			$("#fechaFalloV").val($("#fechaFallo").val());
			$("#fechaFormalizacionV").val($("#fechaFormalizacion").val());

			$("#fechaEntregaV").val($("#fechaEntrega").val());
			$("#mTipoCambioV").val($("#mTipoCambio").val());
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
			//condiciones del pedido
			//verifica existencia en mPedidoCondiciones, si no existe se inserta
			queryFormPost("existePedidoCondicion", {async: false});
			queryFormPost("existeFechaMaxima", {async:false});
			if ($("#existeCondicion").val()==1){
				queryFormPost("pedidoCondicion", {async: false});
				
			}else{      
				$("#condicionTransporte").val("Por parte del proveedor");
				$("#condicionEntrega").val("DDP. nuestro almacén");
				$("#condicionPago").val("20 dias naturales");
				//cuando la unidad se A04 el campo de facturar a sera "COMISION NACIONAL Forestal"
				//if($("#cIdUnidadEjecutora").val()=="A04"){
					$("#facturar").val("COMISION NACIONAL FORESTAL");
					$("#direccion").val("PERIFÉRICO PONIENTE #5360 COL. SAN JUAN DE OCOTÁN, ZAPOPAN, JALISCO, C.P. 45019  RFC:CNF-010405-EG1");
				//}
								
			}
			if($("#gerencia").val()==''){
				$("#gerencia").val($("#descUnidadEjecutora").val());
			}
			if($("#facturar").val()==''){
				$("#facturar").val("COMISION NACIONAL FORESTAL");
			}
			if($("#direccion").val()==''){
				$("#direccion").val("PERIFÉRICO PONIENTE #5360 COL. SAN JUAN DE OCOTÁN, ZAPOPAN, JALISCO, C.P. 45019  RFC:CNF-010405-EG1");
			}
			if ($("#existeFechaMaxima").val()==1){
				
			}else{
				if($("#existeFechaMaxima").val()==''){

				}else{
					document.getElementById("fMaxima").checked=true;
					muestraFechaEntrega();
				}
			}
			$( "#cboCategoriaCaratula" ).attr("disabled", true);
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

		function validaCondicionesIniciales(){

			var roles="<%=roles%>";
			var dateChangeAllowed=true;			
			//Condiciones para habilitar campos
				if($("#nIdTipoCambio").val()=="01"){
				document.getElementById("trTipoCambio").style.display="none";
			}	
			queryFormPost("mUsuarioMismaUE", {async: false   });		

			if($("#nIdEstadoPed").val()!="1"){
				//if($("#nIdEstadoPed").val()!="5"){
					if($("#nIdEstadoPed").val()!="6"){
				    	if($("#nIdEstadoPed").val()=="2" || $("#nIdEstadoPed").val()=="5"){//en sif sin presupuesto
							//si es dos deshabilita el textbox de pedido definitivo y tipo cambio
							document.getElementById("cPedidoDefinitivoV").disabled = true;			  	
					 	    document.getElementById("mTipoCambioV").disabled = true;
							habilitaTabs();
						}
						else{ 

							if($("#nIdEstadoPed").val()=="3"){// en sif presupuestado
				 	    		document.getElementById("mTipoCambioV").disabled = true;
				 	    		document.getElementById("cConceptoV").disabled=true
								document.getElementById("cPedidoDefinitivoV").disabled=true;
				 	    		habilitaTabs();
						   	}	

							else{ //es 4 aprobado
								if($("#U_LOGIN").val() != $("#cIdUsuarioCreacion").val() && roles.toString().indexOf("ADMIN_RECMAT") < 0
									&& roles.toString().indexOf("ANALISTA") < 0 && roles.toString().indexOf("JEFE") < 0 && parseInt($("#usuariosMismaUE").val(),10)==0){
									document.getElementById("btnGuardarCaratulaPed").disabled = true;	
								}
								document.getElementById("cConceptoV").disabled=true;
								document.getElementById("cboCategoriaCaratula").disabled=true
								document.getElementById("cPedidoDefinitivoV").disabled=true;
								document.getElementById("mTipoCambioV").disabled = true;
								document.getElementById("btnGuardarCaratulaPed").disabled = true;
								document.getElementById("fMaxima").disabled = true;
								document.getElementById("condicionPago").disabled = true;
								document.getElementById("condicionEntrega").disabled = true;
								document.getElementById("condicionTransporte").disabled = true;
								document.getElementById("direccion").disabled = true;
								document.getElementById("facturar").disabled = true;
								document.getElementById("lugarEntrega").disabled = true;
								document.getElementById("subgerencia").disabled = true;
								document.getElementById("gerencia").disabled = true;
								document.getElementById("chkOtrosImpuestos").disabled = true;
								document.getElementById("chkImpuestosAdicionales").disabled = true;
								document.getElementById("chkNotas").disabled = true;
								document.getElementById("centralizado").disabled = true;
								document.getElementById("descentralizado").disabled = true;
								document.getElementById("fParcial").disabled = true;
								document.getElementById("fechaEntregaV").style.readonly=true;
								habilitaTabs();
					        }
					   }
				  	}
				//}
			} 
			  //si esta en capturado se checa si es una copia de pedido
			  else{
			  queryFormPost("revisaCopiaPedido", {async: false});
			  if(parseInt($("#estadoPartidaProcedimiento").val(),10)== 3 )
			  document.getElementById("cPedidoDefinitivoV").disabled=true;
			  
			  }
			document.getElementById("trffal").disabled=true;

			document.getElementById("trffor").disabled=true;

			document.getElementById("trfent").disabled=true;		

			document.getElementById("fechaFalloV").disabled=true;
			document.getElementById("fechaEntregaV").disabled=true;

			document.getElementById("fechaFormalizacionV").disabled=true;
			// convertimos a mayasculas usuario de creacion y u_login
			var login=$("#U_LOGIN").val();
			var u_creacion=$("#cIdUsuarioCreacion").val();
			login=login.toUpperCase();
			u_creacion=u_creacion.toUpperCase();
			$("#U_LOGIN").val(login);
			$("#cIdUsuarioCreacion").val(u_creacion);
			 if($("#U_LOGIN").val()!=$("#cIdUsuarioCreacion").val()){
					if(roles.toString().indexOf("ADMIN_RECMAT") < 0 && roles.toString().indexOf("ANALISTA") < 0 
						&& roles.toString().indexOf("JEFE") < 0 && parseInt($("#usuariosMismaUE").val(),10)==0){
					  	document.getElementById("cPedidoDefinitivoV").disabled = true;	
					  	document.getElementById("mTipoCambioV").disabled = true;
						document.getElementById("btnGuardarCaratulaPed").disabled = true;
						dateChangeAllowed=false;
				  }

			}
			//dateChangeAllowed solo se enciende (true) para ADMIN_RECMAT y cIdUsuarioCreacion
			if(dateChangeAllowed){
				//Inicializacion de datePickers
				if($("#nIdEstadoPed").val() == "1" || $("#nIdEstadoPed").val() == "2" || $("#nIdEstadoPed").val() == "3" || $("#nIdEstadoPed").val() == "5"){
					//document.getElementById("trffal").disabled=false;
					document.getElementById("trffor").disabled=false;
					document.getElementById("trfent").disabled=false;		
					//document.getElementById("fechaFalloV").disabled=false;
					document.getElementById("fechaEntregaV").disabled=false;
					document.getElementById("fechaFormalizacionV").disabled=false;


					agregaDatePickerFechas();

				}else if($("#nIdEstadoPed").val() == "4"){
					//document.getElementById("trffor").disabled=false;
					document.getElementById("trfent").disabled=false;		
					document.getElementById("fechaEntregaV").disabled=true;
					//document.getElementById("fechaFormalizacionV").disabled=false;
					agregaDatePickerFechas();
				}else{
					document.getElementById("btnGuardarCaratulaPed").disabled = true;
				}
				
				$("#fechaEntregaP1").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					altField: "#actualDate",
					currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});

				$("#fechaEntregaP2").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					altField: "#actualDate",
					currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});

				$("#fechaEntregaP3").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					altField: "#actualDate",
					currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});

				$("#fechaEntregaP4").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					altField: "#actualDate",
					currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});
			 }
		}
		 ///Desabilita sabados y domingos del datepicker
			 function nonWorkingDates(date){
		        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
		        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
		        var closedDays = [[Sunday], [Saturday]];
		        for (var i = 0; i < closedDays.length; i++) {
		            if (day == closedDays[i][0]) {
		                return [false];
		            }
		
		        }
		        return [true];
		    }
		//funciones para hacer foco en los botones
		function foco(elemento) {
			elemento.style.border = "1px solid #FF0000";
		}		
		function no_foco(elemento) {
			elemento.style.border = "1px solid #CCCCCC";
		}
		//Funcion para comparar fechas
		function compare_dates(fecha, fecha2)  
		{  
			var xMonth=fecha.substring(3, 5);  
			var xDay=fecha.substring(0, 2);  
			var xYear=fecha.substring(6,10);  
			var yMonth=fecha2.substring(3, 5);  
			var yDay=fecha2.substring(0, 2);  
			var yYear=fecha2.substring(6,10);  
			if (xYear> yYear)  
			{  
				return(true);  
			}  
			else  
			{  
				if (xYear == yYear)  
				{   
					if (xMonth> yMonth)  
					{  
						return(true); 
					}  
					else  
					{   
						if (xMonth == yMonth)  
						{  
							if (xDay> yDay)  
								return(true);  
							else  
								return(false);  
						 }  
						else  
							return(false);  
					}  
				}  
				else  
					return(false);  
			}  
		}		
		function validaCampos(){			
			var validado=true;
			var patternNumero=/^\d+([.]\d\d?)?$/
			var tipoCambioVar;
			$("#lblMsj").css('color','red');	
			//Valida Tipo de Cambio
			tipoCambioVar=  $("#mTipoCambioV").val();			
			if(!patternNumero.test(tipoCambioVar)){
				$("#lblMsj").val('El tipo de cambio debe ser un dato numérico');
				foco(document.getElementById('mTipoCambioV'));
				document.getElementById("trErrorFechas").style.visibility='visible';
				return false;
			}	
			if(tipoCambioVar <0){
				$("#lblMsj").val('El tipo de cambio debe ser mayor a 0');
				foco(document.getElementById('mTipoCambioV'));
				document.getElementById("trErrorFechas").style.visibility='visible';
				return false;
			}
						
			if(document.getElementById("fParcial").checked){
				var fechaEntrega1 = ($("#fechaEntregaP1").val()).split("/");
				var fechaEntrega1_ = new Date(fechaEntrega1[2], fechaEntrega1[1]-1, fechaEntrega1[0]);
				var fechaFallo=($("#fechaFalloV").val()).split("/");
				var fechaFallo_ = new Date(fechaFallo[2], fechaFallo[1]-1, fechaFallo[0]);
				if( $("#cIdTipoProcedimiento").val()=="PC"&& !compare_dates($("#fechaEntregaP2").val().toString(),$("#fechaEntregaP1").val().toString())){
					$("#lblMsj").val('La primera fecha de entrega tiene que ser menor que la segunda fecha de entrega');
					foco(document.getElementById('fechaEntregaP1'));
					document.getElementById("trErrorFechas").style.visibility='visible';
					return false;
				}
				if ($("#cIdTipoProcedimiento").val()=="PC" && $("#fechaEntregaP3").val()!="" && $("#fechaEntregaP3").val()!="01/01/1900" ){
					if(!compare_dates($("#fechaEntregaP3").val().toString(),$("#fechaEntregaP2").val().toString())){
						$("#lblMsj").val('La segunda fecha de entrega tiene que ser menor que la tercera fecha de entrega');
						foco(document.getElementById('fechaEntregaP2'));
						document.getElementById("trErrorFechas").style.visibility='visible';
						return false;
					}
				}else{
					$("#fechaEntregaP3").val("");
				}
				if ($("#cIdTipoProcedimiento").val()=="PC" && $("#fechaEntregaP4").val()!=""  && $("#fechaEntregaP4").val()!="01/01/1900"){
					if(!compare_dates($("#fechaEntregaP4").val().toString(),$("#fechaEntregaP3").val().toString())){
						$("#lblMsj").val('La tercera fecha de entrega tiene que ser menor que la cuarta fecha de entrega');
						foco(document.getElementById('fechaEntregaP3'));
						document.getElementById("trErrorFechas").style.visibility='visible';
						return false;
					}
				}else{
					$("#fechaEntregaP4").val("");
				}
				
			}else{
				var fechaEntrega1 = ($("#fechaEntregaV").val()).split("/");
				var fechaEntrega1_ = new Date(fechaEntrega1[2], fechaEntrega1[1]-1, fechaEntrega1[0]);

				
				var fechaFallo=($("#fechaFalloV").val()).split("/");
				var fechaFallo_ = new Date(fechaFallo[2], fechaFallo[1]-1, fechaFallo[0]);
				var fechaFormalizacion = ($("#fechaFormalizacionV").val()).split("/");
				var fechaFormalizacion_ = new Date(fechaFormalizacion[2], fechaFormalizacion[1]-1, fechaFormalizacion[0]);
			}			
			return true;
		}
		//Funcion para actualizar el estado del Pedido en la base de datos
		function guardar() {

			var roles="<%=roles%>";
			no_foco(document.getElementById('fechaEntregaV'));

			no_foco(document.getElementById('fechaFormalizacionV'));
			no_foco(document.getElementById('mTipoCambioV'));
			no_foco(document.getElementById('fechaFalloV'));
			no_foco(document.getElementById('fechaEntregaP4'));
			no_foco(document.getElementById('fechaEntregaP3'));
			no_foco(document.getElementById('fechaEntregaP2'));
			no_foco(document.getElementById('fechaEntregaP1'));
			document.getElementById("trErrorFechas").style.visibility='hidden';
			manejaRadioBtn();
			queryFormPost("mUsuarioMismaUE", {async: false   });			

			//Actualiza el tipo de categoria si eres ADMIN_RECMAT
			if(roles.toString().indexOf("ADMIN_RECMAT") >= 0 || roles.toString().indexOf("ANALISTA") >= 0 || roles.toString().indexOf("JEFE") >= 0
				|| parseInt($("#usuariosMismaUE").val(),10)==1){

				$("#nIdCategoria").val($("#cboCategoriaCaratula").val())
				queryFormPost("actualizaCaratulaCategoriaPedido", {async: false});
			}
			if($("#gerencia").val()==''){
				$("#gerencia").val($("#descUnidadEjecutora").val());
			}	
			
			if(validaCampos()){
				//obtiene datos de controles a hiddens				
				$("#cConcepto").val($("#cConceptoV").val());				
				$("#cPedidoDefinitivo").val($("#cPedidoDefinitivoV").val());
				$("#fechaFallo").val($("#fechaFalloV").val());
				$("#fechaFormalizacion").val($("#fechaFormalizacionV").val());
				$("#fechaEntrega").val($("#fechaEntregaV").val());
				$("#mTipoCambio").val($("#mTipoCambioV").val());
				$("#fechaInicio").val($("#fechaInicioV").val());
				$("#fechaFin").val($("#fechaFinV").val());
				//Validar la captura del folio STPS
				queryFormPost("existePrecomPedido",{async:false});
				if($("#existePrecom").val() == "" || $("#existePrecom").val() == "0"){
					//Actualiza Centralizado
					queryFormPost("descentralizadoPedidoUpdate",{async:false});
					//Guarda impuestos adicionales
					queryFormPost("impuestosAdicionalesPedidoDelete",{async: false});
					if(document.getElementById("chkImpuestosAdicionales").checked){
						validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional1","cPorcentajeImpuestoAdicional1","1");
						validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional2","cPorcentajeImpuestoAdicional2","2");
						validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional3","cPorcentajeImpuestoAdicional3","3");
					}
					

					$("#cMontoOtroImpuesto1").val($("#cMontoOtroImpuesto1").val().replace(/,/g, ''));
					$("#cMontoOtroImpuesto1").val($("#cMontoOtroImpuesto1").val().replace("$", ""));

					$("#cMontoOtroImpuesto2").val($("#cMontoOtroImpuesto2").val().replace(/,/g, ''));
					$("#cMontoOtroImpuesto2").val($("#cMontoOtroImpuesto2").val().replace("$", ""));

					$("#cMontoOtroImpuesto3").val($("#cMontoOtroImpuesto3").val().replace(/,/g, ''));
					$("#cMontoOtroImpuesto3").val($("#cMontoOtroImpuesto3").val().replace("$", ""));
				
					//Guarda otros impuestos
					queryFormPost("OtrosimpuestosPedidoDelete",{async: false});
					if(document.getElementById("chkOtrosImpuestos").checked){
						validaDatosOtrosImpuestos("cDescripcionOtroImpuesto1","cMontoOtroImpuesto1","1");
						validaDatosOtrosImpuestos("cDescripcionOtroImpuesto2","cMontoOtroImpuesto2","2");
						validaDatosOtrosImpuestos("cDescripcionOtroImpuesto3","cMontoOtroImpuesto3","3");
					}
				}
				
				//actualiza fechas si esta en estado 2 o tres
				if ($("#nIdEstadoPed").val() == "1" || $("#nIdEstadoPed").val() == "2" || $("#nIdEstadoPed").val() == "3" ) {
				
				 //revisamos que el contrato no tenga un pasivo a plurianualiada asigando
				     queryFormPost("revisaContratosPasivosPlurianualesPedido", {async: false});
				    if(parseInt($("#tienePasivoPlurianual").val(),10) > 0 ){
						swal("El numero de Pedido que desea actualizar ya tiene un Pedido Pasivo  o un Pedido Plurianual Asignado.",{icon:"info",button: "Cerrar"});
						location.reload();
						return;
					}
				
				 //checamos que el pedido definitivo que se pretende actualizar no tenga un contrato diverso 
					queryFormPost("revisaPedidosComprometidos", {async: false});
					queryFormPost("revisaPedidosConContratoDiverso", {async: false});

										
					if((parseInt($("#numeroContratos").val(),10) || parseInt($("#numDiversos").val(),10)) > 0  ){
						swal("El numero de Pedido que desea actualizar ya tiene un Contrato Diverso  o un Pedido de Materiales Asignado.",{icon:"info",button: "Cerrar"});
						location.reload();
						return;
					}
				

					queryFormPost("usuarioVentanillaReadPedido", {async: false});
					if(($("#responsable").val() == "VENTANILLA_PRECOMPROMISO" || $("#responsable").val() == "") && ($("#nIdEstadoPed").val() == "4")){
						swal("No se pueden actualizar los datos por el tipo de estatus que tiene, hay que devolver el precompromiso.",{icon:"info",button: "Cerrar"});
						location.reload();
						return;
					}
					queryFormPost("existePedidoCondicion", {async: false});
					if(pedidoCondicion()){
						//Actualiza
						$("#cnumCotizacion").val($("#cPedidoDefinitivo,").val());
						queryFormPost("actualizaCaratulaPedidoUpdate", {async: false});////Humberto
						if($("#cIdTipoProcedimiento").val()=="PC"){
							//queryFormPost("mInsertFechaEntregaPedido", {async: false});
							queryFormPost("mUpdateFechaEntregaPedido", {async: false});
						}else{
							queryFormPost("mUpdateFechaInicioContrato", {async: false});
							queryFormPost("mUpdateFechaFinContrato", {async: false});



						}
						queryFormPost("actualizaPedidoDefinitivoClausula", {async: false});

						queryFormPost("actualizaPedidoDefinitivoFundamentos", {async: false});
						queryFormPost("actualizaPedidoDefinitivoRepresentantes", {async: false});
						queryFormPost("actualizaPedidoFinancieroUpdateContratoDiverso", {async: false});
						swal("Los cambios se han realizado exitosamente.",{icon:"info",button: "Cerrar"});
						document.getElementById("trErrorFechas").style.visibility='visible';
						location.reload();
					}

				}else if ($("#nIdEstadoPed").val() == "4"){
					if(pedidoCondicion()){
						if($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val() || roles.toString().indexOf("ADMIN_RECMAT") >= 0
							||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
							swal({
								title: "Esta modificación afectará al sistema financiero ¿Desea continuar?",
								text: "",
								icon: "info",
								buttons: {
									confirm : "Aceptar",
									cancel: "Cancelar"
									},
								}).then((continuar) => {
									if (!continuar) {
										return;
								}else{
									//agrega contrato diverso convenio con fecha nueva
									queryFormPost("mInsertFechaPedidoDiversoConvenio", {async: false});
									swal("Los cambios se han realizado exitosamente.",{icon:"info",button: "Cerrar"});
									document.getElementById("trErrorFechas").style.visibility='visible';
									location.reload();
								}
							});
						}
					}else{
						//break;
					}
				
				}
				queryFormPost("mUpdateProcedimientoFundamentoLeg,mUpdateProcedimientoAdjFundamentoLeg", {async: false});
				guardaBitacora("GUARDA_CARATULA", $("#cPedidoDefinitivo").val());
				habilitaTabs();
			}	
		}

		function validaDatosImpuestosAdicionales(descripcion, porcentaje, impuesto){
			if($("#"+descripcion).val() != ""){
				if($("#"+porcentaje).val() != ""){
					if(parseFloat($("#"+porcentaje).val()) > 0){
						$("#cDescripcionImpuestoAdicional").val($("#"+descripcion).val());
						$("#cPorcentajeImpuestoAdicional").val(parseFloat($("#"+porcentaje).val()));
						$("#cImpuesto").val(impuesto);
						queryFormPost("insertImpuestoAdicionalPedido",{async: false});
					}
					else{
						swal("El valor del porcentaje del "+descripcion.replace("cDescripcion","").replace("Adicional"," ")+" debe ser mayor a 0",{icon:"info",button: "Cerrar"});
					}
				}
				else{
					swal("Debe insertar en valor del procentaje en el "+descripcion.replace("cDescripcion","").replace("Adicional"," "),{icon:"info",button: "Cerrar"});
				}
			}
			else{
				if($("#"+porcentaje).val() != ""){
					swal("Debe insertar la descripci\xF3n del "+descripcion.replace("cDescripcion","").replace("Adicional"," "),{icon:"info",button: "Cerrar"});
				}
			}
		}
		function validaDatosOtrosImpuestos(descripcion, monto, impuesto){
			if($("#"+descripcion).val() != ""){
				if($("#"+monto).val() != ""){
					if(parseFloat($("#"+monto).val()) > 0){
						$("#cDescripcionOtroImpuesto").val($("#"+descripcion).val());
						$("#cMontoOtroImpuesto").val(parseFloat($("#"+monto).val()));
						$("#cImpuestoOtros").val(impuesto);
						queryFormPost("insertOtrosImpuestosPedido",{async: false});
					}
					else{
						swal("El valor del monto del "+descripcion.replace("cDescripcionOtro","")+" debe ser mayor a 0",{icon:"info",button: "Cerrar"});
					}
				}
				else{
					swal("Debe insertar en valor del Monto en el "+descripcion.replace("cDescripcionOtro",""),{icon:"info",button: "Cerrar"});
				}
			}
			else{
				if($("#"+monto).val() != ""){
					swal("Debe insertar la descripci\xF3n del "+descripcion.replace("cDescripcionOtro",""),{icon:"info",button: "Cerrar"});	
				}
			}
		}
		function Sinespacios(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed > 91 && keyPressed != 209 && keyPressed != 95){
			swal("Solo se Permiten Mayusculas y Números",{icon:"info",button: "Cerrar"});
			 }
			 if (keyPressed == 61 || keyPressed == 63 ||
			 	 keyPressed == 46 || keyPressed == 59 ||
			 	 keyPressed == 58 || keyPressed == 60 ||
			 	 keyPressed == 62)
			{
			return false;
			 }
			return !(keyPressed > 31 && (keyPressed < 45 || keyPressed > 90) && keyPressed != 209 && keyPressed != 95);
		}
		function deshabilitaTabs(){
				$( "#aTab2" ).attr("disabled", true);
				$( "#aTab3" ).attr("disabled", true);
				$( "#aTab4" ).attr("disabled", true);
				$( "#aTab5" ).attr("disabled", true);
				$( "#aTab6" ).attr("disabled", true);
				$( "#aTab7" ).attr("disabled", true);
		}
		
		function habilitaTabs(){
				$( "#aTab2" ).attr("disabled", false);
				$( "#aTab3" ).attr("disabled", false);
				$( "#aTab4" ).attr("disabled", false);
				$( "#aTab5" ).attr("disabled", false);
				$( "#aTab6" ).attr("disabled", false);
				$( "#aTab7" ).attr("disabled", false);
			
		}
		function muestrachk(){
			if(document.getElementById("fParcial").checked){
				document.getElementById("fechasParciales").style.display="block";
				document.getElementById("fechaEntregaV").disabled=true;
				$("#fechaEntregaV").val("");
			}else{
				$("#activo").val(0);
				document.getElementById("fechaEntregaV").disabled=false;
				if($("#fechaEntrega").val()=="01/01/1900"){
					$("#fechaEntregaV").val("01/01/2012");	
				}else{
					$("#fechaEntregaV").val($("#fechaEntrega").val());
				}
				document.getElementById("fechasParciales").style.display="none";
		     	$("#fechaEntregaP1").val("");
				$("#fechaEntregaP2").val("");
				$("#fechaEntregaP3").val("");
				$("#fechaEntregaP4").val("");
			}
		}
		function muestraNotas(){
			if(document.getElementById("chkNotas").checked){
				document.getElementById("divNotas").style.display="block";
			}else{
				$("#notaPedido").val("");
				document.getElementById("divNotas").style.display="none";
				
			}
		}
		function muestraImpuestosAdicionales(){
			if(document.getElementById("chkImpuestosAdicionales").checked){
				document.getElementById("divImpuestosAdicionales").style.display="block";
			}else{
				$("#cDescripcionImpuestoAdicional1").val("");
				$("#cDescripcionImpuestoAdicional2").val("");
				$("#cDescripcionImpuestoAdicional3").val("");
				$("#cPorcentajeImpuestoAdicional1").val("");
				$("#cPorcentajeImpuestoAdicional2").val("");
				$("#cPorcentajeImpuestoAdicional3").val("");
				document.getElementById("divImpuestosAdicionales").style.display="none";
			}
		}
		
		function muestraOtrosImpuestos(){
			if(document.getElementById("chkOtrosImpuestos").checked){
				document.getElementById("divOtrosImpuestos").style.display="block";
				if($("#nIdEstadoPed").val()!=1){
					document.getElementById("MESSAGESPAN").style.display="block";
					$("#cDescripcionOtroImpuesto1").prop("readonly",true);
					$("#cDescripcionOtroImpuesto2").prop("readonly",true);					
					$("#cDescripcionOtroImpuesto3").prop("readonly",true);					
					$("#cMontoOtroImpuesto1").prop("readonly",true);					
					$("#cMontoOtroImpuesto2").prop("readonly",true);					
					$("#cMontoOtroImpuesto3").prop("readonly",true);
				}
			}else{
				$("#cDescripcionOtroImpuesto1").val("");
				$("#cDescripcionOtroImpuesto2").val("");
				$("#cDescripcionOtroImpuesto3").val("");
				$("#cMontoOtroImpuesto1").val("");
				$("#cMontoOtroImpuesto2").val("");
				$("#cMontoOtroImpuesto3").val("");
				document.getElementById("divOtrosImpuestos").style.display="none";
			}
		}
				
		function muestraFechaEntrega(){
			if(document.getElementById("fMaxima").checked){
				if(document.getElementById("fParcial").checked){
					$("#activo").val(1);
					document.getElementById("fParcial").disabled=true;
					document.getElementById("fechasParciales").style.display="none";
					/// fechas parciales ocultas
					$("#fechaEntregaP1O").val($("#fechaEntregaP1").val());
					$("#fechaEntregaP2O").val($("#fechaEntregaP2").val());
					$("#fechaEntregaP3O").val($("#fechaEntregaP3").val());
					$("#fechaEntregaP4O").val($("#fechaEntregaP4").val());
				}
				
				$("#fechaEntregaP1").val("");
				$("#fechaEntregaP2").val("");
				$("#fechaEntregaP3").val("");
				$("#fechaEntregaP4").val("");
				
				document.getElementById("fechaMaximaEntrega").style.display="block";
			}else{
				if($("#activo").val()=='1'){
					document.getElementById("fParcial").disabled=false;
					document.getElementById("fechasParciales").style.display="block";
					$("#fechaEntregaP1").val($("#fechaEntregaP1O").val());
					$("#fechaEntregaP2").val($("#fechaEntregaP2O").val());
					$("#fechaEntregaP3").val($("#fechaEntregaP3O").val());
					$("#fechaEntregaP4").val($("#fechaEntregaP4O").val());
				}
				
				$("#fechaMaxima").val("");
				document.getElementById("fechaMaximaEntrega").style.display="none";
			}
		}
		function muestraPropuestaConjunta(){
	
			if(document.getElementById("propuestaConjuntaChk").checked){
				document.getElementById("propuestaConjunta").style.display="block";
			}else{
				document.getElementById("propuestaConjunta").style.display="none";
			}
			
		}
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		function validaFecha(){
			
			//if ($("#fechaEntregaP1").val()!="01/01/1900"|| $("#fechaEntregaP2").val()!="01/01/1900"||$("#fechaEntregaP3").val()!="01/01/1900"||$("#fechaEntregaP4").val()!="01/01/1900"){
			if(compare_dates($("#fechaEntregaP1").val().toString(),$("#fechaEntregaV").val().toString())){
					document.getElementById("fParcial").checked=true;
					document.getElementById("fechasParciales").style.display="block";
					document.getElementById("fechaEntregaV").disabled=true;
					//document.getElementById("fechaEntregaV").style.display="none";
					$("#fechaEntregaV").val("");
			}else{
				$("#fechaEntregaP1").val("");
				$("#fechaEntregaP2").val("");
				$("#fechaEntregaP3").val("");
				$("#fechaEntregaP4").val("");

			}
		}
		

		function borrar(){
			if (($("#cIdTipoProcedimiento").val() == 'PI' || $("#cIdTipoProcedimiento").val() == 'PF') && $.trim($("#lblEstado").val()) == 'CAPTURADO') {
				swal({
					title: "¿Esta seguro de Anular el Pedido?",
					text: "Se eliminara y no será posible regresar los cambios!",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
					}else{
						queryFormPost("mPedidoDelete",{async: false});
						window.location = 'Pedidos.jsp?tab=0';
					}
				});
			}
			else {
				swal("No es posible anular el pedido",{icon:"info",button: "Cerrar"});
			}
		}
				

		function manejaRadioBtn(){
			if($("#centralizado").is(':checked')){
				$("#esDescentralizado").val(0);
			}
			else{
				$("#esDescentralizado").val(1);
			}
		}

		// muestra en pantalla si el registro de la bd es centralizado o descentralizado
		function iniciaRadioBtn(){
			queryFormPost("verSiPedidoDescentralizado",{async: false});
			if($("#esDescentralizado").val()==1){
				$("#descentralizado").attr("checked",true);
			}
			else{
				$("#centralizado").attr("checked",true);
			}
		}
		
		function validaEdicionDatos(){
			var roles="<%=roles%>";
			queryFormPost("mValidaPagosPedidoRead",{async: false});
			queryFormPost("mTotalPagosPedidoRead",{async: false});
			
			if($("#lblEstado").val().toString() == "APROBADO"){
				if(parseInt($("#cValidaPagosPedido").val(),10) == 1){
					if(roles.indexOf("ADMIN_RECMAT") >=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
						if(parseInt($("#cTotalPagosPedido").val(),10) == 0)
							return true;
						else
							return false;
					}
					else
						return false;
				}
				else
					return false;
			}
			return;
		}
		function habilitaCamposEdicion(){
			if(validaEdicionDatos() == true){
				document.getElementById("cboCategoriaCaratula").disabled=true;
				document.getElementById("cConceptoV").disabled=false;
				document.getElementById("cPedidoDefinitivoV").disabled=true;
				document.getElementById("cnumCotizacion").disabled=true;
				//document.getElementById("fechaFalloV").disabled=false;
				document.getElementById("fechaFormalizacionV").disabled=false;
				document.getElementById("fechaEntregaV").disabled=false;
				document.getElementById("fParcial").disabled=true;
				document.getElementById("fechaEntregaP1").disabled=true;
				document.getElementById("fechaEntregaP2").disabled=true;
				document.getElementById("fechaEntregaP3").disabled=true;
				document.getElementById("fechaEntregaP4").disabled=true;
				document.getElementById("chkNotas").disabled=true;
				document.getElementById("centralizado").disabled=true;
				document.getElementById("descentralizado").disabled=true;
				document.getElementById("notaPedido").disabled=true;
				document.getElementById("chkImpuestosAdicionales").disabled=true;
				document.getElementById("cDescripcionImpuestoAdicional1").disabled=true;
				document.getElementById("cPorcentajeImpuestoAdicional1").disabled=true;
				document.getElementById("cDescripcionImpuestoAdicional2").disabled=true;
				document.getElementById("cPorcentajeImpuestoAdicional2").disabled=true;
				document.getElementById("cDescripcionImpuestoAdicional3").disabled=true;
				document.getElementById("cPorcentajeImpuestoAdicional3").disabled=true;
				document.getElementById("btnGuardarCaratulaPed").disabled=false;
			}
		}

		function agregaDatePickerFechas(){
			/*$('#fechaFalloV').datepicker({						
				dateFormat: 'dd/mm/yy',
				currentText: 'Now',
				showOn: 'button',
				altField: '#actualDate',
				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				});	
			*/	
			if($("#nIdEstadoPed").val()!="4"){
				$("#fechaEntregaV").datepicker({


			 	beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",

				currentText: "Now",
				showOn: 'button',
				altField: "#actualDate",
				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				});
							  
			 
						 
			  $("#fechaFormalizacionV").datepicker({
			  	beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",
				 altField: "#actualDate",
				 currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				});
				
				$("#fechaInicioV").datepicker({
			  	beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",
				 altField: "#actualDate",
				 currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				});
				

				$("#fechaFinV").datepicker({
			  	beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",
				 altField: "#actualDate",
				 currentText: "Now",
				showOn: 'button',

				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				});
				
			}	

		}
	
		function copiarPedido(){
			var roles="<%=roles%>";
			if(parseInt($("#nIdEstadoPed").val(),10) == 4 && (roles.toString().indexOf("ADMIN_RECMAT") || $("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val() 
				||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1)){
			    if (!window.confirm("Esta seguro de  Realizar una copia del Pedido,tiene que estar seguro que el Pedido sufrio una Reduccion Presupuestal del Compromiso?"))
					return;
				//revisamos si existe una reduccion
				queryFormPost("checaReduccionPedido",{async : false});
				
				if(parseFloat($("#mImporteCompromiso").val())!=0){
					swal("No se puede Realizar la copia no han hecho la Reduccion Presupuestal del Compromiso",{icon:"info",button: "Cerrar"});
					return;
			   }
				//Obtener el siguiente consecutivo del procedimiento y asignarlo al hidden ConsecutivoProcedimiento
				queryFormPost("fn_ConsecutivoProcedimientoPedidoCopia",{async : false});
			
				var res="";
				var link="";
				//aplicacion contable
				$.ajax({url: '../../servlet/PedidoServlet?cIdPedidoDefinitivo='+$("#cPedidoDefinitivo").val()+"&ConsecutivoProcedimientoCopia="+$("#ConsecutivoProcedimientoCopia").val(), type:'post' , async: false,data:'operacion=12', dataType: 'json', success: 
					function(j){
						res=j[0].Contable1;
						if(res!="-1"){
						swal("Se Realizo con Exito la Copia de Pedido",{icon:"info",button: "Cerrar"});
						//se constuye link donde va a ir la copia
						//Bitácora
						$("#cAccion").val("REALIZA_COPIA_PEDIDO");
						$("#cIdDocumento").val($("#cPedidoDefinitivo").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			
						queryFormPost("fn_ConstruyeLinkPedidoCopia",{async : false});
						link='Pedidos.jsp?tab=1&cEjercicio='+$("#cEjercicio").val()+'&cIdTipoPedido='+$("#cIdTipoPedido").val()+ '&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val()+'&nIdConsecutivo=' +$("#nIdConsecutivoPedidoClon").val()+'&lPedidoAbierto='+$("#lContratoAbiertoClon").val()+'&nIdEstadoPedido='+$("#nIdEstadoPedidoClon").val()
						window.location=link;
						
						}else{
							swal("No se realizo con exito la copia del pedido intentelo nuevamente",{icon:"info",button: "Cerrar"});
							return;
						}
					}	
				});
			
			}else{
			   swal("Para poder realizar una Copia, el Pedido debe estar aprobado,la Copia solo la podra realizar un Administrador o el Usuario Creador del Pedido, ademas tiene que haber sufrido una Reduccion Presupuestal del Compromiso.",{icon:"info",button: "Cerrar"});
			}
	
		}

		function pedidoCondicion(){
			if($("#direccion").val()==''){
				swal({
					title: "Falta agregar el campo Domicilio Fiscal del área creadora del pedido, este se mostrara en el pie del reporte del pedido, si lo deja vacio se mostrara en blanco ¿Desea continuar?",
					text: "",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return false;
					}else{
						if ($("#existeCondicion").val()==1){
							//actualiza registro
							queryFormPost("actualizaPedidoCondicion", {async: false});	
						}else{
							//inserta registro
							queryFormPost("insertaPedidoCondicion", {async: false});
						}
						return true;
					}
				});
			}else{
				if ($("#existeCondicion").val()==1){
						//actualiza registro
						queryFormPost("actualizaPedidoCondicion", {async: false});	
					}else{
						//inserta registro
						queryFormPost("insertaPedidoCondicion", {async: false});
					}
				return true;
			}
			// valida si existe propuesta conjunta
			if(document.getElementById("propuestaConjuntaChk").checked){
				if ($("#existePropuestaConjunta").val()==1){
						//actualiza registro
						queryFormPost("actualizaPropuestaConjuntan", {async: false});	
					}else{
						//inserta registro
						queryFormPost("insertaPropuestaConjunta", {async: false});
					}
			}
		}
		
	function cambiafrmt(fld){
	   	$("#"+fld).formatCurrency();
	}		
		
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '0123456789.';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal

	
		return true 
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}


	
		function days_between(date1, date2) {
		    // The number of milliseconds in one day
		    var ONE_DAY = 1000 * 60 * 60 * 24;
		
		    // Convert both dates to milliseconds
		    var date1_ms = date1.getTime();
		    var date2_ms = date2.getTime();
		
		    // Calculate the difference in milliseconds
		    var difference_ms = date2_ms - date1_ms;
		    
		    // Convert back to days and return
		    return Math.round(difference_ms/ONE_DAY);
		}

		function guardaBitacora(accion,documento){
			//Bitácora
			$("#cAccion").val(accion);
			$("#cIdDocumento").val(documento);
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		
	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
		<div id="container" class="container" style="width: 95%">
			<fieldset>
				<legend>Car&aacute;tula del Pedido</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
			    		<td align="right" colspan="2">
			    		    <img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer;display: none;"  onclick="copiarPedido();"/>&nbsp;<span id="spamCopiaPedido" style="display: none;">Realizar Copia</span> 
			    			<img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer;display: none;"  onclick="borrar();"/>&nbsp;<span id="spamBorrarPedido" style="display: none;">Anular</span> 
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
			    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblPedido" id="lblPedido" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 500px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
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


			    </table>
			</fieldset>
			<fieldset>
				<legend>Edici&oacute;n del Pedido</legend>
		    		<table align="left" cellpadding="2" width="100%">	    	
				    	<tr>
							<td align="left">
								<input name="Categoria" id="Categoria"  value="Tipo de Procedimiento:"size="20" style="border-width:0; background-color:transparent">
								
							</td>
							<td align="left">
								<select id="cboCategoriaCaratula" name="cboCategoriaCaratula"
									style="width: 30em;">
									<option value="" selected="selected">
									</option>
								</select>
	
							</td>
	
						</tr>
						<tr>
							<td align="left">Fundamento Legal:</td>
							<td align="left"><select id="cboFundamentoLeg" name="cboFundamentoLeg" style="width: 30em;">
									<option value="" selected="selected"></option>
								</select></td>
						</tr>
				    	<tr>
				    		<td align="left">Concepto</td>
				    		<td align="left"><input type="text" id="cConceptoV" name="cConceptoV" style="width: 600px" maxlength="1000"/></td>
				    	</tr>
				    	<tr id="trTipoCambio" >								    	
				    		<td align="left">Tipo de Cambio</td>
				    		<td align="left"><input type="text" id="mTipoCambioV" name="mTipoCambioV" style="width: 600px"/></td>
					    </tr>
				    	<tr>
				    		<td align="left">No. Pedido SAI</td>
				    		<td align="left"><input type="text" disabled="disabled" id="cPedidoDefinitivoV"  maxlength="25"  name="cPedidoDefinitivoV" style="width: 600px" onKeyPress="return Sinespacios(event)" /></td>
				    	</tr>
				    	<tr style="display: none;">
				    		<td align="left">No. de Cotizaci&oacute;n o Referencia de Fecha</td>


				    		<td align="left"><input type="text" id="cnumCotizacion" name="cnumCotizacion" style="width: 600px" /></td>
				    	</tr>
				    	<tr style="display: none;">
				    		<td align="left">No. de Contrato Compranet</td>
				    		<td align="left"><input type="text" id="cnumCompranet" name="cnumCompranet" style="width: 600px" maxlength="100"/></td>
				    	</tr>
				    	<tr id="trffal" style="display: none;">
				    		<td align="left">Fecha de Fallo</td>
				    		<td align="left"><input type="text" id="fechaFalloV" name="fechaFalloV" ></td>
				    	</tr>							    	


				    	<tr id="trffor" style="display: none;">
				    		<td align="left">Fecha de Formalizaci&oacute;n</td>
				    		<td align="left"><input type="text" id="fechaFormalizacionV" name="fechaFormalizacionV" ></td>
				    	</tr>
				    	<tr id="trfent">
				    		<td align="left">Fecha de Entrega</td>
				    		<td align="left"><input type="text" id="fechaEntregaV" name="fechaEntregaV"></td>
				    	</tr>
				    	<tr id="trfinicio" style="display: none;">
				    		<td align="left">Fecha Inio</td>
				    		<td align="left"><input type="text" id="fechaInicioV" name="fechaInicioV"></td>
				    	</tr>
				    	<tr id="trffin" style="display: none;">
				    		<td align="left">Fecha Fin</td>
				    		<td align="left"><input type="text" id="fechaFinV" name="fechaFinV"></td>
				    	</tr>
				    	<tr id="trErrorFechas" style="visibility: hidden;" >		

					    	<td colspan="2"  align="left">
					    		<textarea rows="1" cols="20" style="width: 700px;overflow: auto; border-width:0; background-color:transparent" id="lblMsj" name="lblMsj" readonly  ></textarea>  
					    	</td>	    									    		

				    	</tr>
				    	<tr>
					    	<td><input type="radio" name='grupoTipoPago' id='centralizado' value='0' onclick=""/>Centralizado</td>
					    	<td><input type="radio" name='grupoTipoPago' id='descentralizado' value='1' onclick="" checked />Descentralizado</td>
					    	


				    	</tr>
				    	<tr id="trfechasParciales">
				    		<td align="left"><input type="checkbox" name="fParcial" id="fParcial" onclick="muestrachk();"/>Entregas Parciales</td>


				    		<td align="left">
					    		<div id="fechasParciales" style="display: none">
						    		<table>
						    			<tr><td>1ra Fecha de Entrega Parcial</td><td><input type="text" id="fechaEntregaP1" name="fechaEntregaP1"></td></tr>
						    			<tr><td>2da Fecha de Entrega Parcial</td><td><input type="text" id="fechaEntregaP2" name="fechaEntregaP2"></td></tr>
						    			<tr><td>3ra Fecha de Entrega Parcial</td><td><input type="text" id="fechaEntregaP3" name="fechaEntregaP3"></td></tr>
						    			<tr><td>4ta Fecha de Entrega Parcial</td><td><input type="text" id="fechaEntregaP4" name="fechaEntregaP4"></td></tr>
								    </table>
						    	</div>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left"><input type="checkbox" name='chkNotas' id='chkNotas' value='0' onclick="muestraNotas();"/>Incluir Notas</td>
				    		<td>
				    		<div id="divNotas" style="display: none">
				    		<textarea name="notaPedido" rows="6" cols="72" id="notaPedido" onkeypress="textCounter(this,2000);"></textarea>
				    		</div>
				    		</td>


				    	</tr>
				    	<tr style="display: none;">
				    		<td align="left"><input type="checkbox" name='chkImpuestosAdicionales' id='chkImpuestosAdicionales' value='0' onclick="muestraImpuestosAdicionales();" />Impuestos Adicionales</td>
				    		<td align="left">
					    		<div id="divImpuestosAdicionales" style="display: none">
					    			<table>
					    				<tr>
					    					<td>&nbsp;</td>
					    					<td>Descripci&oacute;n</td>
					    					<td>Porcentaje</td>
					    				</tr>
					    				<tr>
					    					<td>Impuesto 1:</td>
					    					<td><input type="text" name="cDescripcionImpuestoAdicional1" id="cDescripcionImpuestoAdicional1" size="15" maxlength="10"/></td>
					    					<td><input type="text" name="cPorcentajeImpuestoAdicional1" id="cPorcentajeImpuestoAdicional1" size="5" maxlength="5"  onkeypress="return(onlyNumbers(event));"/></td>
					    				</tr>
					    				<tr>
					    					<td>Impuesto 2:</td>
					    					<td><input type="text" name="cDescripcionImpuestoAdicional2" id="cDescripcionImpuestoAdicional2" size="15" maxlength="10"/></td>
					    					<td><input type="text" name="cPorcentajeImpuestoAdicional2" id="cPorcentajeImpuestoAdicional2" size="5" maxlength="5"  onkeypress="return(onlyNumbers(event));"/></td>
					    				</tr>
					    				<tr>
					    					<td>Impuesto 3:</td>
					    					<td><input type="text" name="cDescripcionImpuestoAdicional3" id="cDescripcionImpuestoAdicional3" size="15" maxlength="10"/></td>
					    					<td><input type="text" name="cPorcentajeImpuestoAdicional3" id="cPorcentajeImpuestoAdicional3" size="5" maxlength="5"  onkeypress="return(onlyNumbers(event));"/></td>
					    				</tr>
					    			</table>
					    		</div>
				    		</td>
				    	</tr>		
				    		
				    		<tr >

				    		<td align="left"><input type="checkbox" name='chkOtrosImpuestos' id='chkOtrosImpuestos' value='0' onclick="muestraOtrosImpuestos();"/>Otros Impuestos</td>
				    		<td align="left">
					    		<div id="divOtrosImpuestos" style="display: none">
					    			<span id="MESSAGESPAN" style="display: none; color: red;" >PARA MODIFICAR LOS IMPUESTOS EL ESTATUS DEL PEDIDO DEBE DE ESTAR EN CAPTURADO</span>
					    			<table>
					    				<tr>
					    					<td>&nbsp;</td>
					    					<td>Descripci&oacute;n</td>
					    					<td>Monto</td>
					    				</tr>
					    				<tr>
					    					<td>Impuesto 1:</td>
					    					<td><input type="text" name="cDescripcionOtroImpuesto1" id="cDescripcionOtroImpuesto1" size="15" maxlength="10"/></td>
					    					<td><input type="text" name="cMontoOtroImpuesto1" id="cMontoOtroImpuesto1"   onblur="cambiafrmt(this.name);"  size="10" maxlength="15" onkeypress="return(onlyNumbers(event));" /></td>

					    				</tr>
					    				<tr>
					    					<td>Impuesto 2:</td>
					    					<td><input type="text" name="cDescripcionOtroImpuesto2" id="cDescripcionOtroImpuesto2" size="15" maxlength="10"/></td>
					    					<td><input type="text" name="cMontoOtroImpuesto2" id="cMontoOtroImpuesto2" size="10" maxlength="15" onblur="cambiafrmt(this.name);"  onkeypress="return(onlyNumbers(event));"/></td>
					    				</tr>
					    				<tr>
					    					<td>Impuesto 3:</td>
					    					<td><input type="text" name="cDescripcionOtroImpuesto3" id="cDescripcionOtroImpuesto3" size="15" maxlength="10"/></td>
					    					<td><input type="text" name="cMontoOtroImpuesto3" id="cMontoOtroImpuesto3" size="10"  maxlength="15"  onblur="cambiafrmt(this.name);"  onkeypress="return(onlyNumbers(event));"/></td>
					    				</tr>
					    			</table>
					    		</div>
				    		</td>
				    	</tr>	
				    </table>
				
						<table align="left" cellpadding="2" width="100%">
			    			<tr> 
					    		<td align="left">Gerencia:</td>
					    		<td align="left"><input type="text" id="gerencia" name="gerencia" style="width: 600px" onkeypress="textCounter(this,100);" /></td>
					    	</tr>
					    	<tr> 
					    		<td align="left">Subgerencia:</td>
					    		<td align="left"><input type="text" id="subgerencia" name="subgerencia" style="width: 600px"  onkeypress="textCounter(this,100);"/></td>
					    	</tr>
			    		<!-- facturar a  -->
			    			<tr id="trFacturar"  >		<!-- style="visibility: hidden;" -->
						    	<td colspan="2"  align="left">
						    		<textarea rows="1" cols="20" style="width: 700px;overflow: auto; border-width:0; background-color:transparent" id="lblMsj" name="lblMsjFacturar" readonly>Nombre del área a facturar</textarea>  
						    	</td>	    									    		
					    	</tr>
					    	<tr> 
					    		<td align="left">Entregar en:</td>
					    		<td align="left"><input type="text" id="lugarEntrega" name="lugarEntrega" style="width: 600px" onkeypress="textCounter(this,158);"/></td>
					    	</tr>
					    	<tr> 
					    		<td align="left">Facturar a:</td>
					    		<td align="left"><input type="text" id="facturar" name="facturar" style="width: 600px" onkeypress="textCounter(this,100);"/></td>
					    	</tr>
					    	<!-- Direccion del area que genera el pedido -->
					    	<tr> 
					    		<td align="left">Domicilio Fiscal:</td>
					    		<td align="left"><input type="text" id="direccion" name="direccion" style="width: 600px" onkeypress="textCounter(this,200);"/></td>
					    	</tr>
					    	<!-- condiciones transporte --> 
					    	<tr> 
					    		<td align="left">Transporte</td>
					    		<td align="left"><input type="text" id="condicionTransporte" name="condicionTransporte" style="width: 600px" onkeypress="textCounter(this,50);"  /></td>
					    	</tr>
					    	<!-- condiciones entrega -->
					    	<tr>
					    		<td align="left">Condiciones de entrega</td>
					    		<td align="left"><input type="text" id="condicionEntrega" name="condicionEntrega" style="width: 600px" onkeypress="textCounter(this,50);" /></td>
					    	</tr>
					    	<!-- condiciones pago -->
					    	<tr>
					    		<td align="left">Condiciones de pago</td>
					    		<td align="left"><input type="text" id="condicionPago" name="condicionPago" style="width: 600px" onkeypress="textCounter(this,50);" /></td>
					    	</tr>
					    	<tr>
					    		<td align="left"><input type="checkbox" name="fMaxima" id="fMaxima" onclick="muestraFechaEntrega();"/>Fecha maxima de entrega</td>
					    		<td align="left">
						    		<div id="fechaMaximaEntrega" style="display:none" >
							    		<table>
							    			<tr>
							    			<td><textarea name="fechaMaxima" rows="6" cols="72" id="fechaMaxima" onkeypress="textCounter(this,100);"></textarea></td>
							    			</tr>
									    </table>
							    	</div>
					    		</td>
					    	</tr>
					    	<tr>
					    		<td align="left"><input type="checkbox" name="propuestaConjuntaChk" id="propuestaConjuntaChk" onclick="muestraPropuestaConjunta();" style="display:none"/></td>

					    	</tr>
					    	<tr>
					    		<td>
					    		</td>

					    	</tr>
			    	</table>
			    </fieldset>
		    	<!-- pedido de propuesta conjunta -->
		    	<div id="propuestaConjunta" style="display:none">
			    	<fieldset>
			    		<legend>Pedido de Propuesta Conjunta</legend>
			    		<table>
			    			<tr>
					    		<td align="left">RFC</td>
					    		<td align="left"><input type="text" id="rfc1" name="rfc1" style="width: 600px" /></td>
					    	</tr>
					    	<tr>
					    		<td align="left">Razón Social</td>
					    		<td align="left"><input type="text" id="razonSocial1" name="razonSocial1" style="width: 600px" /></td>
					    	</tr>
					    	<tr>
					    		<td align="left">RFC</td>
					    		<td align="left"><input type="text" id="rfc2" name="rfc2" style="width: 600px" /></td>
					    	</tr>
					    	<tr>
					    		<td align="left">Razón Social</td>
					    		<td align="left"><input type="text" id="razonSocial2" name="razonSocial2" style="width: 600px" /></td>
					    	</tr>
			    		</table>
			    	</fieldset>
		    	</div>
		    	<table>
		    		<tr>	<td>&nbsp;</td></tr>
				    <tr>
				    	 <td colspan="2" align="center"><input type="button"  name="btnGuardarCaratulaPed" id="btnGuardarCaratulaPed" value="Guardar" onclick="guardar();" class="btnInterfaceBG ui-button ui-corner-all" /></td>
				    </tr>
		    	</table>
					   
	    <!-- Hidden's -->
	    <!-- Sesion  -->
	    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
	    <input type="hidden" name="cIdTipoDocumento" id="cIdTipoDocumento" value="<%=cIdTipoPedido%>" />
	    <input type="hidden" name="cIdPedido" id="cIdPedido" value="<%=cIdTipoPedido%>-<%=cIdUnidadEjecutora%>-<%=nIdConsecutivo%>" />

	     <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />	    
	    <!-- Resultado de consultas -->	   	     


	    <input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
	    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
	    <input type="hidden" name="nIdEstadoPed" id="nIdEstadoPed" />
	    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
	    <input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
	    <input type="hidden" name="existePrecom" id="existePrecom" />

	    <!--  Hidden valores auxiliares -->
	     <input type="hidden" name="mTipoCambio" id="mTipoCambio" />
	    <input type="hidden" name="cConcepto" id="cConcepto" />
	    <input type="hidden" name="nIdTipoCambio" id="nIdTipoCambio" />
	    <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />
	    <input type="hidden" name="fechaFallo" id="fechaFallo" />
	    <input type="hidden" name="fechaFormalizacion" id="fechaFormalizacion" />


	    <input type="hidden" name="fechaEntrega" id="fechaEntrega" />	
	    <input type="hidden" name="fechaEntregaTMP" id="fechaEntregaTMP" />
	    <input type="hidden" name="ValObtenido" id="ValObtenido" value=""/>
	    <input type="hidden" name="ValObtenidoFL" id="ValObtenidoFL" value=""/>

	    
	    <input type="hidden" name="nIdCategoria" id="nIdCategoria" value=""/>

	    <input type="hidden" name="cDescripcionImpuestoAdicional" id="cDescripcionImpuestoAdicional" value=""/>
	    <input type="hidden" name="cPorcentajeImpuestoAdicional" id="cPorcentajeImpuestoAdicional" value=""/>
	    <input type="hidden" name="cImpuesto" id="cImpuesto" value=""/>    
	    <input type="hidden" name="esDescentralizado" id="esDescentralizado"/>
	    <input type="hidden" name="cTotalPagosPedido" id="cTotalPagosPedido"/>

	    <input type="hidden" name="responsable" id="responsable" />


	    <input type="hidden" name="cValidaPagosPedido" id="cValidaPagosPedido"/>

	    <input type="hidden" name="numeroContratos" id="numeroContratos" />	
	    <input type="hidden" name="cContableRevisa" id="cContableRevisa" />	
	    <input type="hidden" name="numDiversos" id="numDiversos" />
	    <input type="hidden" name="tienePasivoPlurianual" id="tienePasivoPlurianual" />
	     	
		
		<input type="hidden" name="cDescripcionOtroImpuesto" id="cDescripcionOtroImpuesto" value=""/>
	    <input type="hidden" name="cMontoOtroImpuesto" id="cMontoOtroImpuesto" value=""/>
	    <input type="hidden" name="cImpuestoOtros" id="cImpuestoOtros" value=""/> 
	     <input type="hidden" name="ConsecutivoProcedimientoCopia" id="ConsecutivoProcedimientoCopia" value=""/>
	     <input type="hidden" name="nIdConsecutivoPedidoClon" id="nIdConsecutivoPedidoClon" value=""/>
	     <input type="hidden" name="lContratoAbiertoClon" id="lContratoAbiertoClon" value=""/>
	     <input type="hidden" name="nIdEstadoPedidoClon" id="nIdEstadoPedidoClon" value=""/>
	     <input type="hidden" name="estadoPartidaProcedimiento" id="estadoPartidaProcedimiento" value=""/>
	     

	      <input name="cAccion" id="cAccion" type="hidden">
		  <input name="cIdDocumento" id="cIdDocumento" type="hidden">
		  <input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
		  <input name="mImporteCompromiso" id="mImporteCompromiso" type="hidden">
		 
	    <!--  fechas parciales  -->
	    <input type="hidden" name="fechaEntregaP1O" id="fechaEntregaP1O" />
	    <input type="hidden" name="fechaEntregaP2O" id="fechaEntregaP2O" />	

	    <input type="hidden" name="fechaEntregaP3O" id="fechaEntregaP3O" />	
	    <input type="hidden" name="fechaEntregaP4O" id="fechaEntregaP4O" />		
	    <input type="hidden" name="activo" id=activo />	
	    <!-- Pedido condiciones -->
	    <input type="hidden" name="existeCondicion" id="existeCondicion" />
	    <input type="hidden" name="existeFechaMaxima" id="existeFechaMaxima" />
	    <!-- pedido definitivo en clausulas -->
	    <input type="hidden" name="pedidoDefinitivoAnt" id="pedidoDefinitivoAnt" />
	    <input type="hidden" name="hayFirmante" id="hayFirmante" />
	    <input type="hidden" name="nIdFirmante" id="nIdFirmante" />
	    <input type="hidden" name="nIdFechaFallo" id="nIdFechaFallo" value='11'/>
	    <input type="hidden" name="fechaInicio" id="fechaInicio" />
	    <input type="hidden" name="fechaFin" id="fechaFin" />
	    <input type="hidden" name="nIdFechaInicio" id="nIdFechaInicio" />


	    <input type="hidden" name="nIdFechaFin" id="nIdFechaFin" />
	    <input type="hidden" name="descUnidadEjecutora" id="descUnidadEjecutora" />
	    <input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
	    <input type="hidden" name="cIdRFC" id="cIdRFC" value=""/>
		<input type="hidden" name="cAplica15D" id="cAplica15D" value="N" />
	    <input type="hidden" name="cIdConsolidado" id="cIdConsolidado" value="" />
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="" />
	    
	    </div>
	</form>
  </body>
</html>
