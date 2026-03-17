
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();
	String roles="";
	Map<String, Role> rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Reporte Pedido</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>
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
	<script type="text/javascript" charset="utf-8">	
		$(document).ready(function() {
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
		    deshabilitaTabs();
			setFieldsInit();
			validaCondicionesIniciales();
			

			/* if($("#cDescripcionImpuestoAdicional1").val()!="" || $("#cDescripcionImpuestoAdicional2").val()!="" || $("#cDescripcionImpuestoAdicional3").val()!=""){
				//$("#divImpuestosAdicionales").css("visibility","visible");
				document.getElementById("divImpuestosAdicionales").style.display="block";
				document.getElementById("chkImpuestosAdicionales").checked=true;
			}
			
			if($("#notaPedido").val()!=""){
				//if(document.getElementById("chkNotas").checked){
				document.getElementById("chkNotas").checked=true;
				document.getElementById("divNotas").style.display="block";
			}
				 */
		
				
				
				 //llena el combo de Categoria
				querySelectPost("CategoriaReadPedido", "cboCategoriaCaratula", {async : false});
				//Se obtiene el valor que tiene el pedido seleccionado
			    queryFormPost("valorCaratulaPedido", {async : false});//"ValObtenido",
			  
				
				
				if ($("#cIdTipoProcedimiento").val() == 'PF') {
					$( "#presupuestoPedido" ).attr("disabled", true);
					$( "#preCompromisoPedido" ).attr("disabled", true);
					$( "#pagosPedido" ).attr("disabled", true);
				}
				habilitaCamposEdicion();
				
				var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
				if(nIdEstado == 1)
					$("#preCompromisoPedido").css("display", "none");
				else
					$("#preCompromisoPedido").css("display", "block");
		  });		
		function setFieldsInit(){
			
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			
			
			//asigna el valor de true a readonly para los campos de fechas
			
		
					
			//Carga de controles y caratula
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalRead", {async: false});
			queryFormPost("fnMontoIVARead", {async: false});
			queryFormPost("fnMontoImpuesto1Read", {async: false});
			queryFormPost("fnMontoImpuesto2Read", {async: false});
			queryFormPost("fnMontoImpuesto3Read", {async: false});
			queryFormPost("mPedidoFechaFallo", {async: false});
			queryFormPost("mPedidoFechaFormalizacion", {async: false});
			queryFormPost("mPedidoFechaEntrega", {async: false});
			queryFormPost("mPedidoFechaEntregaConvenio", {async: false}); //cPedidoDefinitivo
			
			queryFormPost("cg_roleRead", {async: false});
			//Inicializa controles apartir de Hiddens, como auxiliares para el CRUD
					  
			$("#cPedidoDefinitivoV").val($("#cPedidoDefinitivo").val());

			
			queryFormPost("fnMontoTotalPedido", {async: false});
			//condiciones del pedido
			//verifica existencia en mPedidoCondiciones, si no existe se inserta
			queryFormPost("existePedidoCondicion", {async: false});
			if ($("#existeCondicion").val()==1){
				queryFormPost("pedidoCondicion", {async: false});
			}else{      
				$("#condicionTransporte").val("Por parte del proveedor");
				$("#condicionEntrega").val("DDP. nuestro almacén");
				$("#condicionPago").val("20 dias naturales");
			}
			//cuando la unidad se A04 el campo de facturar a sera "COMISION NACIONAL DEL AGUA"
			if($("#cIdUnidadEjecutora").val()=="A04"){
				$("#facturar").val("COMISION NACIONAL DEL AGUA");
				$("#direccion").val("AV. INSURGENTES SUR 2416, COL. COPILCO EL BAJO, DELEGACION COYOACAN, C.P. 04340 MEXICO, D.F. RFC:CNA-890116-SF2");
			}
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
				
					
			if($("#nIdEstadoPed").val()!="1"){
				//if($("#nIdEstadoPed").val()!="5"){
					if($("#nIdEstadoPed").val()!="6"){
				    	if($("#nIdEstadoPed").val()=="2" || $("#nIdEstadoPed").val()=="5"){//en sif sin presupuesto
							//si es dos deshabilita el textbox de pedido definitivo y tipo cambio
							document.getElementById("cPedidoDefinitivoV").disabled = true;			  	
					 	    
							habilitaTabs();
						}
						else{ 
							if($("#nIdEstadoPed").val()=="3"){// en sif presupuestado
				 	    		
				 	    		
								document.getElementById("cPedidoDefinitivoV").disabled=true;
				 	    		habilitaTabs();
						   	}	
							else{ //es 4 aprobado
								if($("#U_LOGIN").val() != $("#cIdUsuarioCreacion").val() && roles.toString().indexOf("ADMIN_RECMAT") < 0){
									document.getElementById("btnGuardarCaratulaPed").disabled = true;	
								}
								
								document.getElementById("cPedidoDefinitivoV").disabled=true;
							
								
								//CONDICIONES   
								document.getElementById("condicionPago").disabled=true;
								document.getElementById("condicionEntrega").disabled=true;
								document.getElementById("condicionTransporte").disabled=true;
								
								
								
								
								
								habilitaTabs();
					        }
					   }
				  	}
				//}
			}

			
			
			
				
			// convertimos a mayasculas usuario de creacion y u_login
			var login=$("#U_LOGIN").val();
			var u_creacion=$("#cIdUsuarioCreacion").val();
			login=login.toUpperCase();
			u_creacion=u_creacion.toUpperCase();
			$("#U_LOGIN").val(login);
			$("#cIdUsuarioCreacion").val(u_creacion);
			 
			 if($("#U_LOGIN").val()!=$("#cIdUsuarioCreacion").val()){
					if(roles.toString().indexOf("ADMIN_RECMAT") < 0){
					  	document.getElementById("cPedidoDefinitivoV").disabled = true;	
					  	document.getElementById("mTipoCambioV").disabled = true;
						document.getElementById("btnGuardarCaratulaPed").disabled = true;
						dateChangeAllowed=false;
				  }
			}
			//dateChangeAllowed solo se enciende (true) para ADMIN_RECMAT y cIdUsuarioCreacion
			if(dateChangeAllowed){
				
				
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
		
	// 	        for (i = 0; i < closedDates.length; i++) {
	// 	            if (date.getMonth() == closedDates[i][0] - 1 &&
	// 	            date.getDate() == closedDates[i][1] &&
	// 	            date.getFullYear() == closedDates[i][2]) {
	// 	                return [false];
	// 	            }
	// 	        }
		
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
		
		//Funcion para actualizar el estado del Pedido en la base de datos
		function guardar() {
			var roles="<%=roles%>";
		
		
			
			
	
			manejaRadioBtn();
						
			//Actualiza el tipo de categoria si eres ADMIN_RECMAT
			if(roles.toString().indexOf("ADMIN_RECMAT") >= 0){
				$("#nIdCategoria").val($("#cboCategoriaCaratula").val())
				queryFormPost("actualizaCaratulaCategoriaPedido", {async: false});
			}
				
			
		 	if(validaCampos()) {				
				//obtiene datos de controles a hiddens				
								
				$("#cPedidoDefinitivo").val($("#cPedidoDefinitivoV").val());
				
				
				$("#fechaEntrega").val($("#fechaEntregaV").val());
				$("#mTipoCambio").val($("#mTipoCambioV").val());	

				//Guarda impuestos adicionales
				queryFormPost("impuestosAdicionalesPedidoDelete",{async: false});
				if(document.getElementById("chkImpuestosAdicionales").checked){
					validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional1","cPorcentajeImpuestoAdicional1","1");
					validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional2","cPorcentajeImpuestoAdicional2","2");
					validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional3","cPorcentajeImpuestoAdicional3","3");
				}

				//actualiza fechas si esta en estado 2 o tres
				if ($("#nIdEstadoPed").val() == "1" || $("#nIdEstadoPed").val() == "2" || $("#nIdEstadoPed").val() == "3" ) {
				
				
				 //checamos que el pedido definitivo que se pretende actualizar no tenga un contrato diverso 
					queryFormPost("revisaPedidosComprometidos", {async: false});
					queryFormPost("revisaPedidosConContratoDiverso", {async: false});
										
					if((parseInt($("#numeroContratos").val(),10) || parseInt($("#numDiversos").val(),10)) > 0  ){
						alert("El numero de Pedido que desea actualizar ya tiene un Contrato Diverso  o un Pedido de Materiales Asignado ");
						location.reload();
						return;
					}
					queryFormPost("usuarioVentanillaReadPedido", {async: false});
					if($("#responsable").val()!="VENTANILLA_PRECOMPROMISO" && $("#responsable").val()!="")
					{
						alert("No se pueden actualizar los datos, el pedido lo esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val());
					}
					else{
						//Actualiza
						queryFormPost("actualizaCaratulaPedidoUpdate", {async: false});
						//verifica existencia en mPedidoCondiciones, si no existe se inserta
						queryFormPost("existePedidoCondicion", {async: false});
						if ($("#existeCondicion").val()==1){
							//actualiza registro
							queryFormPost("actualizaPedidoCondicion", {async: false});	
						}else{
							//inserta registro
							queryFormPost("insertaPedidoCondicion", {async: false});
						}
		
						queryFormPost("mDeleteFechasPedido", {async: false});
						//insertar en tabla fechas procedimiento
						queryFormPost("mInsertFechaFormalizacionPedido", {async: false});
						queryFormPost("mInsertFechaEntregaPedido", {async: false});
						//actualiza las fechas del contrato diverso siempre y cuando no lo tengael usuario de ventanilla de pagos
						queryFormPost("actualizaPedidoFinancieroUpdateContratoDiverso", {async: false});
						$("#lblMsj").val('Los cambios se han realizado exitosamente');
						$("#lblMsj").css('color','green');	
						document.getElementById("trErrorFechas").style.visibility='visible';
					}
				}else if ($("#nIdEstadoPed").val() == "4"){
					if($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val() || roles.toString().indexOf("ADMIN_RECMAT") >= 0){
						if(confirm("Esta modificación afectará al sistema financiero ¿Desea continuar?")){
							//agrega contrato diverso convenio con fecha nueva
							queryFormPost("mInsertFechaPedidoDiversoConvenio", {async: false});
							$("#lblMsj").val('Los cambios se han realizado exitosamente');
							$("#lblMsj").css('color','green');	
							document.getElementById("trErrorFechas").style.visibility='visible';
						}
					}
				}
				
				habilitaTabs();
			}	
		}

		/* function validaDatosImpuestosAdicionales(descripcion, porcentaje, impuesto){
			if($("#"+descripcion).val() != ""){
				if($("#"+porcentaje).val() != ""){
					if(parseFloat($("#"+porcentaje).val()) > 0){
						$("#cDescripcionImpuestoAdicional").val($("#"+descripcion).val());
						$("#cPorcentajeImpuestoAdicional").val(parseFloat($("#"+porcentaje).val()));
						$("#cImpuesto").val(impuesto);
						queryFormPost("insertImpuestoAdicionalPedido",{async: false});
					}
					else{
						alert("El valor del porcentaje del "+descripcion.replace("cDescripcion","").replace("Adicional"," ")+" debe ser mayor a 0");
					}
				}
				else{
					alert("Debe insertar en valor del procentaje en el "+descripcion.replace("cDescripcion","").replace("Adicional"," "));
				}
			}
			else{
				if($("#"+porcentaje).val() != ""){
					alert("Debe insertar la descripci\xF3n del "+descripcion.replace("cDescripcion","").replace("Adicional"," "));
				}
			}
		} */
		
		function Sinespacios(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed > 91 && keyPressed != 209 && keyPressed != 95){
			alert ("Solo se Permiten Mayusculas y Numeros");
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
		/* function muestrachk(){
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
		} */
/* 		function muestraImpuestosAdicionales(){
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
		} */
		
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
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		/* function validaFecha(){
			
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
		 */
		function borrar(){
			if (($("#cIdTipoProcedimiento").val() == 'PI' || $("#cIdTipoProcedimiento").val() == 'PF') && $.trim($("#lblEstado").val()) == 'CAPTURADO') {
				if (confirm("¿Esta seguro de Anular el Pedido? Se eliminara y no será posible regresar los cambios")) {
					queryFormPost("mPedidoDelete",{async: false});
					window.location = 'Pedidos.jsp?tab=0';
				}
			}
			else {
				alert("No es posible anular el pedido");
			}
		}
				
		/* function manejaRadioBtn(){
			if($("#centralizado").is(':checked')){
				//hidden 'esDescentralizado' = 0
				$("#esDescentralizado").val(0);
			}
			else{
				//hidden 'esDescentralizado' = 1
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
		} */
		
		function validaEdicionDatos(){
			var roles="<=roles>";
			queryFormPost("mValidaPagosPedidoRead",{async: false});
			queryFormPost("mTotalPagosPedidoRead",{async: false});
			
			if($("#lblEstado").val().toString() == "APROBADO"){
				if(parseInt($("#cValidaPagosPedido").val(),10) == 1){
					if(roles.indexOf("ADMIN_RECMAT") >=0 ){
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
				
				
				document.getElementById("cPedidoDefinitivoV").disabled=true;
				document.getElementById("cnumCotizacion").disabled=true;
				//document.getElementById("fechaFalloV").disabled=false;
				document.getElementById("fechaEntregaV").disabled=false;
			
				
				
				
			}
		}
		
	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
		<div id="container" class="container">
			<table width="94%" align="left">
				<tr>
					<td>
						<fieldset>
							<legend>Reporte del Pedido</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
							    			<img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="borrar();"/>&nbsp;Anular
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="window.location = 'Pedidos.jsp?tab=0';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProcedimiento" id="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblDefinitivo" id="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblPedido" id="lblPedido" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 500px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	
							    	
							    	
							    </table>
						</fieldset>
					</td>
				</tr>
				<tr>	
					<td>
					      <fieldset>
					    	<legend>Reporte del Pedido</legend>
					    	<table align="left" cellpadding="2" width="100%">
					    		<!-- facturar a  -->
							    	<tr> 
							    		<td align="left">Facturar a:</td>
							    		<td align="left"><input type="text" id="facturar" name="facturar" style="width: 600px"/></td>
							    	</tr>
							    	<!-- Direccion del area que genera el pedido -->
							    	<tr> 
							    		<td align="left">Dirección:</td>
							    		<td align="left"><input type="text" id="direccion" name="direccion" style="width: 600px"/></td>
							    	</tr>
							    	<!-- condiciones transporte --> 
							    	<tr> 
							    		<td align="left">Transporte</td>
							    		<td align="left"><input type="text" id="condicionTransporte" name="condicionTransporte" style="width: 600px"  /></td>
							    	</tr>
							    	<!-- condiciones entrega -->
							    	<tr>
							    		<td align="left">Condiciones de entrega</td>
							    		<td align="left"><input type="text" id="condicionEntrega" name="condicionEntrega" style="width: 600px" /></td>
							    	</tr>
							    	<!-- condiciones pago -->
							    	<tr>
							    		<td align="left">Condiciones de pago</td>
							    		<td align="left"><input type="text" id="condicionPago" name="condicionPago" style="width: 600px" /></td>
							    	</tr>
							    	<tr>
							    		<td align="left"><input type="checkbox" name="fMaxima" id="fMaxima" onclick="muestraFechaEntrega();"/>Fecha maxima de entrega</td>
							    		<td align="left">
								    		<div id="fechaMaximaEntrega" style="display:none" >
									    		<table>
									    			<tr>
									    			<td><textarea name="fechaMaxima" rows="6" cols="72" id="fechaMaxima" onkeypress="textCounter(this,2000);"></textarea></td>
									    			</tr>
									    			
											    </table>
									    	</div>
							    		</td>
							    	</tr>
							    	<tr>
							    		<td>
							    			&nbsp;
							    		</td>
							    	</tr>
							    	<tr>
							    		 <td colspan="2" align="center"><input type="button"  name="btnGuardarCaratulaPed" id="btnGuardarCaratulaPed" value="Guardar" onclick="guardar();" /></td>
							    	</tr>
					    	</table>
					    </fieldset>
					</td>
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
	     <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />
	     <input type="hidden" name="cPedidoDefinitivoV" id="cPedidoDefinitivoV" />		
	         
	     
	   <!--  <input type="hidden" name="mTipoCambio" id="mTipoCambio" />
	    <input type="hidden" name="cConcepto" id="cConcepto" />
	    <input type="hidden" name="nIdTipoCambio" id="nIdTipoCambio" />
	   
	    <input type="hidden" name="fechaFallo" id="fechaFallo" />
	    <input type="hidden" name="fechaFormalizacion" id="fechaFormalizacion" />
	    <input type="hidden" name="fechaEntrega" id="fechaEntrega" />	
	    <input type="hidden" name="fechaEntregaTMP" id="fechaEntregaTMP" />
	    <input type="hidden" name="ValObtenido" id="ValObtenido" value=""/>
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
	    <input type="hidden" name="numDiversos" id="numDiversos" />	 -->
	    
	    <!--  fechas parciales  -->
	    <input type="hidden" name="fechaEntregaP1O" id="fechaEntregaP1O" />
	    <input type="hidden" name="fechaEntregaP2O" id="fechaEntregaP2O" />	
	    <input type="hidden" name="fechaEntregaP3O" id="fechaEntregaP3O" />	
	    <input type="hidden" name="fechaEntregaP4O" id="fechaEntregaP4O" />		
	    <input type="hidden" name="activo" id=activo />	
	    <!-- Pedido condiciones -->
	    <input type="hidden" name="existeCondicion" id="existeCondicion" />
	    
	    </div>
	</form>
  </body>
</html>
