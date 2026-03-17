<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    if (usuario == null) {
		response.sendRedirect("../index.jsp");
		System.out.println("Se termino la session...............");
		return;
	}
	Map <String, Role>rol =usuario.getRoles();
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
			var roles='';
			$(document).ready(function() {
				$("#tbs").val(0);
				showAndHideTabs();
				<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Procedimiento","NuevoProcedimiento");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
			roles="<%=roles%>";	
			queryInnerDivPost("minimoMaximoTipoProcedimiento", {async : false}); 
			queryFormPost("ejercicioProcedimiento",{async:false});
			$("#usuarioUE").val('<%=usuario.getU_UR()%>');
			queryFormPost("unidadEjecutoraHederProcedimiento",{async:false});
			$("#usuarioLogin").val('<%=usuario.getLogin()%>');
			queryFormPost("unidadEjecutoraHederProcedimiento",{async:false});
			$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
			$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
			$("#usuarioRoleProcedimiento").val('<%=roles%>');
			
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora", {async: false});
			//querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutora", {async: false });
			querySelectPost("TipoProcedimientoRead", "cboTipoProcedimiento", {async: false });
			querySelectPost("tipoProcesoProcedimiento","tipoProcesoProcedimiento", {async: false });
			
			querySelectPost("CategoriaRead", "cboCategoria", {async : false});
			
			
			querySelectPost("mCatalogoTipoIVA", "nPorcentajeIVA", {async : false});
			
			//arrendamiento
			querySelectPost("TipoDenominacionInmuebleRead", "cboDenominacionInmueble", {async: false });
			querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutoraInmueble", {async: false });
			
			$("#EstadoCaptura").val('1');
			eliminaOpcionTipoProcedimiento();

			if($("#cboTipoProcedimiento").val().toString()!="PA" && $("#cboTipoProcedimiento").val().toString()!="PL"){
				$(".arrendamientos_div").attr("disabled", true);
				document.getElementById("fieldsetArrendamiento").style.display = "none";
			}
			
			if($("#cboTipoProcedimiento").val().toString()=="PS" || $("#cboTipoProcedimiento").val().toString()=="PA" || $("#cboTipoProcedimiento").val().toString()=="PL" || $("#cboTipoProcedimiento").val().toString()=="PN"){
				$("#esServicio").val(1);
			}else{
				$("#esServicio").val(0);
			}
			muestraFechas();	
			muestraConsolidado();
			$("#btnGuardarProcedimiento" ).button().click(function() {
				$("#cIdTipoProcedimiento").val($("#cboTipoProcedimiento").val());
				var cIdCons="";
				if($("#cboConsolidado").val()==null){
					swal("No hay consolidado.",{icon:"info",button: "Cerrar"});
					return;
				}
				cIdCons=$("#cboConsolidado").val();
				queryFormPost("ejercicioProcedimiento",{async:false});
				var inputTablaFechasProcedimiento = $('input','#tablaFechasProcedimiento');
				if (inputTablaFechasProcedimiento.length < $("#fechasProcedimiento").val()){
					swal("Error al cargar las fechas necesarias. Contacte a su soporte.",{icon:"info",button: "Cerrar"});
					return;
				}
				var banFechasProcedimiento = true;
				var fechaTem="";
				var numFecha="";
				var numFechaAnt="0";
				var cadenaFechas='';
				var cantidadFechas=inputTablaFechasProcedimiento.length;
				//validación para fechas bacías 
				for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
							numFecha=parseInt(inputTablaFechasProcedimiento[i].id.replace("fechaProcedimiento",""),10);
							if(i>0){
								cadenaFechas=cadenaFechas+",";
								if(inputTablaFechasProcedimiento[i-1].value != "" && inputTablaFechasProcedimiento[i].value != ""){
									if(numFechaAnt!=12){
										fechaTem=inputTablaFechasProcedimiento[i-1].value;
									}else{
										fechaTem=inputTablaFechasProcedimiento[i-2].value;
									}
									if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
										swal("Las fechas se deben introducir en orden cronologico.",{icon:"info",button: "Cerrar"});
										banFechasProcedimiento = false;
										return;
									}
									
								}else{
									swal("Falta insertar algunas fechas.",{icon:"info",button: "Cerrar"});
									banFechasProcedimiento = false;
									return;
								}
							}
							cadenaFechas+=numFecha+"-"+inputTablaFechasProcedimiento[i].value;
							numFechaAnt=numFecha;
						}
					}
				}
				if(banFechasProcedimiento){
					 $("#tipoProceso").val($("#tipoProcesoProcedimiento").val());
				 	 if( $("#cboCategoria").val()=="13"){
						var cboUnidadEjecutora=$('#cboUnidadEjecutora');
						var cboTipoProcedimiento=$('#cboTipoProcedimiento');
						//var cboConsolidado=$('#cboConsolidado')
						var des = $('#cboConsolidado');
						var nPorcentajeIVA=$('#nPorcentajeIVA');
						//var numero_externo=$('#numero_externo')
						var cboCategoria=$('#cboCategoria');
						var descripcion=$('#descripcion');
						var tipoProcesoProcedimiento=$('#tipoProcesoProcedimiento');
						var cboDenominacionInmueble=$('#cboDenominacionInmueble');
						var area_construida=$('#area_construida');
						var area_rentable=$('#area_rentable');
						var numero_empleados=$('#numero_empleados');
						var direccion_inmueble=$('#direccion_inmueble');
						var cboUnidadEjecutoraInmueble=$('#cboUnidadEjecutoraInmueble');
						allFields = $( [] ).add(cboUnidadEjecutora).add(cboTipoProcedimiento).add(des).add(nPorcentajeIVA).add(cboCategoria).add(descripcion).add(tipoProcesoProcedimiento).add(cboDenominacionInmueble).add(area_construida).add(area_rentable).add(numero_empleados).add(direccion_inmueble).add(cboUnidadEjecutoraInmueble),
						tips = $( ".validateTips" );
						var bValid = true;
					    tips.text("");
					    allFields.removeClass( "ui-state-error" );
						bValid = bValid&& checkRequerido(cboUnidadEjecutora, "Unidad Ejecutora");
						bValid = bValid&& checkRequerido(cboTipoProcedimiento, "Tipo de Procedimiento");
						bValid = bValid&& checkRequerido(des, "Consolidado");
						bValid = bValid&& checkRequerido(nPorcentajeIVA, "IVA");
						//bValid = bValid&& checkRequerido(numero_externo, "Numero Externo");
						bValid = bValid&& checkRequerido(cboCategoria, "Categoria del Procedimiento");
						bValid = bValid&& checkRequerido(descripcion, "Descripcion del Procedimiento");
						bValid = bValid&& checkRequerido(tipoProcesoProcedimiento, "Tipo Proceso del Procedimiento");
						bValid = bValid&& checkRequerido(cboDenominacionInmueble, "Uso y/o Denominacion del Inmueble");
						bValid = bValid&& checkRequerido(area_construida, "Area Construida");
						bValid = bValid&& checkRequerido(area_rentable, "Area Rentable");
						bValid = bValid&& checkRequerido(numero_empleados, "Numero de Empleados");
						bValid = bValid&& checkRequerido(direccion_inmueble, "Direccion del Inmueble");
						bValid = bValid&& checkRequerido(cboUnidadEjecutoraInmueble, "Unidad Administrativa Inmueble");
							
                    }else{
				  		var des = $('#cboConsolidado');
						allFields = $( []).add(des),
						tips = $(".validateTips");
						var bValid = true;
				        tips.text("");
				        allFields.removeClass( "ui-state-error" );
						bValid = bValid&& checkRequerido(des, "Consolidado");
					}
					if(!bValid){return;}
					try{
						//Obtener el siguiente consecutivo del procedimiento y asignarlo al hidden ConsecutivoProcedimiento
						$("#cIdUnidadEjecutora").val($("#cboUnidadEjecutora").val());
						$("#arrayFechas").val(cadenaFechas);
						var cadena_campos = cIdCons.split('|');
						$("#cIdConsolidado").val(cadena_campos[0] + '-' + $("#cIdUnidadEjecutora").val() + '-' + cadena_campos[1]);
						$.ajax({
							url: '../../servlet/ProcedimientoServlet?operacion=8',
							dataType: 'json', 					
							async: false, 
			          		type:'post',
							data:{"cEjercicio":$("#cEjercicio").val(),"cIdTipoProcedimiento":$("#cIdTipoProcedimiento").val(),
									"cIdUnidadEjecutora":$("#cIdUnidadEjecutora").val(),"tipoProceso":$("#tipoProceso").val(),
									"cIdconsolidado":$("#cIdConsolidado").val(),"categoria":$("#cboCategoria").val(),
									"descripcion":$("#descripcion").val(),"numero_externo":$("#numero_externo").val(),"tipoProceso":$("#tipoProceso").val(),
									"Activo":$("#Activo").val(),"nPorcentajeIVA":$("#nPorcentajeIVA").val(),"isPlurianual":$("#isPlurianual").val(),
									"cboUnidadEjecutoraInmueble":$("#cboUnidadEjecutoraInmueble").val(),"cboDenominacionInmueble":$("#cboDenominacionInmueble").val(),
									"descripcionOtros":$("#descripcionOtros").val(),"area_construida":$("#area_construida").val(),
									"area_rentable":$("#area_rentable").val(),"numero_empleados":$("#numero_empleados").val(),
									"direccion_inmueble":$("#direccion_inmueble").val(),"arrayFechas":$("#arrayFechas").val(),"cantidadFechas":cantidadFechas  
									},
							//Si el ajax fue success
							success: function(json){
								respuesta=json[0].respuesta;
								mensajeAp=json[0].mensaje;
								swal(mensajeAp,{icon:"info",button: "Cerrar"});
								if(respuesta=="1"){
									inputTablaFechasProcedimiento='';
		                			$("#estadoProcedimiento").val('CAPTURADO ');	
		                			$("#cEjercicio").val(json[0].cEjercicio);
		                			$("#cIdTipoProcedimiento").val(json[0].cIdTipoProcedimiento);
		                			$("#cIdUnidadEjecutora").val(json[0].cIdUnidadEjecutora);
		                			$("#nIdConsecutivo").val(json[0].nIdConsecutivo);
		                			
		                			window.location = "Procedimiento-copia.jsp?tab=3&cEjercicio=" + $("#cEjercicio").val() + "&cIdTipoProcedimiento=" + $("#cIdTipoProcedimiento").val() + "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val() + "&nIdConsecutivo=" + $("#nIdConsecutivo").val();
								}else{
									swal("Reintente",{icon:"warning",button: "Cerrar"});
									window.location = "Procedimiento-copia.jsp?tab=0";
								}
								
							},
							error:function(json){
								swal("Error, Reintente",{icon:"error",button: "Cerrar"});
							}
						});
					}catch(e){
						swal("No se pudo insertar el registro...",{icon:"error",button: "Cerrar"});
					}
				}	
				
		});//Termina el guardar
				
				$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
				$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
				
				
	});
						
			function checkRequerido(o, n) {
				var sTemp = $.trim(o.val());
				o.val(sTemp);
				if (sTemp.length == 0) {
					o.addClass("ui-state-error");
					updateTipsDlg(n + " es un dato requerido.");
					o.focus();
					return false;
				} else {
					return true;
				}
			}
			
			function updateTipsDlg(t) {
				tips.text(t);
				swal(t,{icon:"info",button: "Cerrar"});
			}
			
			function textCounter( field, maxlimit ) {
				if ( field.value.length > maxlimit )
					field.value = field.value.substring( 0, maxlimit );
			}
			
			function validar(e) {
				tecla = (document.all) ? e.keyCode : e.which;
				if (tecla==8) return true;
					patron =/[\w\d\s\\.\/\_\ñ\Ñ]/;					
					te = String.fromCharCode(tecla);																				
					var v = document.getElementById('cDescripcionC').value.replace("ñ", "n").replace("Ñ", "N");					
					document.getElementById('cDescripcionC').value = v;
					return patron.test(te);
			}
			
			function validarCampoDescp(a){
				var v = document.getElementById('cDescripcionC').value.replace("ñ", "n").replace("Ñ", "N");					
					document.getElementById('cDescripcionC').value = v;				
			}
			
			function eliminaOpcionTipoProcedimiento(){
				//elimina la opcion del combo tipo procedimiento FONDEN
				var objTipoProc = document.getElementById("cboTipoProcedimiento");
				for(var l=0;l<objTipoProc.options.length;l++){
					if(objTipoProc.options[l].text.toString().toUpperCase() == "DE FONDEN")
						objTipoProc.options[l]=null;
				}
				//elimina la opcion del combo tipo procedimiento CAPITULO 1000
				for(var l=0;l<objTipoProc.options.length;l++){
					if(objTipoProc.options[l].text.toString().toUpperCase() == "DE CAPITULO1000")
						objTipoProc.options[l]=null;			
				}
			}
			
	function validaLicitacionNuevo(){
		if($("#cboCategoria").val()=="13"){
			$(".arrendamientos_div").attr("disabled", false);
			document.getElementById("fieldsetArrendamiento").style.display = "block";
		}
		else{
			$(".arrendamientos_div").val("");
			$(".arrendamientos_div").attr("disabled", true);
			document.getElementById("fieldsetArrendamiento").style.display = "none";
         }
		if(parseInt($("#cboCategoria").val(),10) <= 4){
			//es licitación
			$("#doctosComite11").hide();
			$("#doctosComite22").hide();
			$("#etiquetaChkActivoNuevo").hide();
			$("#Activo").val(0);
			$("#row10Dias").show();
			
		}
		else{
			//validación del valor del checkbox en BD para la Categoría				
		 	$("#doctosComite11").show();
		 	$("#doctosComite22").show();
			$("#doctosComite11").attr("checked", false);
			$("#doctosComite22").attr("checked", true);
			$("#Activo").val(0);	
			$("#etiquetaChkActivoNuevo").show();
			$("#row10Dias").hide();
			validaCaratulaNuevo();
		}			
		muestraFechas();			
	}
	function muestraFechas(){
		queryFormPost("fechasProcedimientoCuentaRead",{async:false}); //Cuantas fechas hay en la bd
		var intentos = 0;
		do{
			queryInnerDivPost("fn_mFechasProcedimientoRead", {async : false}); //Trae el html con los inputs
			var inputDateCreados = $('input','#tablaFechasProcedimiento').size(); //Cuenta los inputs creados
			intentos++;
		}
		while(inputDateCreados < $("#fechasProcedimiento").val() && intentos < 3);
		
		if (inputDateCreados < $("#fechasProcedimiento").val()){
			swal("Error al cargar las fechas necesarias. Contacte a su soporte.",{icon:"info",button: "Cerrar"});
		}
		$("#tblProveedoresDisponibles").css("display", "none"); 
	}
	///Desabilita sabados y domingos del datepicker
	function nonWorkingDates(date){
        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
        var closedDays = [[Sunday], [Saturday]];
        /*for (var i = 0; i < closedDays.length; i++) {
            if (day == closedDays[i][0]) {
                return [false];
            }

        }*/

        return [true];
    }
	function validaCaratulaNuevo(){
		if($("#doctosComite11").is(':checked')){
			$("#Activo").val(1);
		}
		else{
			$("#Activo").val(0);				
		}				
	}
	
	function muestraConsolidado(){
		queryFormPost("ejercicioProcedimiento",{async:false});
		
	    $("#cIdUnidadEjecutora").val($("#cboUnidadEjecutora").val());
	    $("#cIdTipoProcedimiento").val($("#cboTipoProcedimiento").val());
	    
	    
	   querySelectPost("ConsolidadoRead", "cboConsolidado", {async : false,
	   		callback:function(){
	   			if(""!=$("#cboConsolidado").val() && $("#cboConsolidado").val()!=null){
	   				var cadena_campos=$("#cboConsolidado").val().split('|');
	   				$("#cIdConsolidado").val(cadena_campos[0] + '-' + $("#cIdUnidadEjecutora").val() + '-' + cadena_campos[1]);
	   				queryFormPost("ConsolidadoMontoNetoRead",{async:false});
	   			}
	   		}
	   });	
	    
		if($("#cboTipoProcedimiento").val().toString()!="PA" && $("#cboTipoProcedimiento").val().toString()!="PL"){
			$(".arrendamientos_div").attr("disabled", true);
			document.getElementById("fieldsetArrendamiento").style.display = "none";
		}else{
			$(".arrendamientos_div").attr("disabled", false);
			document.getElementById("fieldsetArrendamiento").style.display = "block";
		}
		//Colocar valor en los hiiden
		if($("#cboConsolidado").val()!=null){
			var cadena_campos=$("#cboConsolidado").val().split('|');
			$("#TipoConsolidado").val(cadena_campos[0]); 
			$("#ConsecutivoConsolidado").val(cadena_campos[1]);
			$("#descripcion").val(cadena_campos[2]);
			
			
			if(parseInt($("#cboCategoria").val(),10) <= 4)
				$("#etiquetaChkActivoNuevo").hide();
			else
				$("#etiquetaChkActivoNuevo").show();
	     }
	     else{
	    	 $("#descripcion").val(" ");
	     	return;
	     }
	
	}
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "PC", "PO", "PS", "PA","PT" ];
		if ($.inArray($("#cIdTipoProcedimiento").val(), apartables) == -1){ // no traen precompromiso
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	function validaFechaTerminoServicio(select){
		if(select.value.toString()=="PN" || select.value.toString()=="PS" || $("#cboTipoProcedimiento").val().toString()=="PO" || $("#cboTipoProcedimiento").val().toString()=="PQ"){
			$("#trfechaProcedimiento10").css("display","");
		}
		else{
			$("#trfechaProcedimiento10").css("display","none");
		}
		
		if(select.value.toString()=="PS" || select.value.toString()=="PA" || select.value.toString()=="PL" || select.value.toString()=="PN" || select.value.toString()=="PO"){
			$("#esServicio").val(1);
		}else{
			$("#esServicio").val(0);
		}
		
		muestraFechas();
	}
	
	function compare_dates(fecha, fecha2){
		var xFecha = fecha.split("/");
		var yFecha = fecha2.split("/");
		var xMonth;
		var xDay;
		var xYear;
		var yMonth;
		var yDay;
		var yYear;
	
		xMonth = xFecha[1];
		yMonth = yFecha[1];
		//verifica en que posision viene en anio en fecha1
		if(xFecha[0].toString>2){
			xDay = xFecha[2];
			xYear = xFecha[0];
		}
		else{
			xDay = xFecha[0];
			xYear = xFecha[2];
		}
		
		//verifica en que posision viene en anio en fecha2
		if(yFecha[0].toString>2){
			yDay = yFecha[2];
			yYear = yFecha[0];
		}
		else{
			yDay = yFecha[0];
			yYear = yFecha[2];
		}
		
	  	if (xYear> yYear){
	      return(true);
	  	}
	  	else{
	    	if (xYear == yYear){ 
	      		if (xMonth> yMonth){
	          		return(true);
	     		}
	      		else{ 
	        		if (xMonth == yMonth){
	          			if (xDay >= yDay)
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
	
	function muestraDenominacionOtros(select){
		if(select.value.toString()=="8"){
			$("#descripcionOtros").css("visibility","visible");
		}else
			$("#descripcionOtros").css("visibility","hidden");
	}
	function onlyNumbers2(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789.';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
	
		return true;
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
	
	function onlyIntegers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		return (keyPressed >= 48 && keyPressed <= 57);
	}
	function PlurianualChecked(){
		if($("#isPlurianualCheck").is(':checked')){
			$("#isPlurianual").val(1);
		}else{
			$("#isPlurianual").val(0);
		}
	}
	function cambiaCentrocontableUsuario(){
		$.ajax({
			url: '../../servlet/CambiaPropiedadesUsuario',
			dataType: 'json',
			data: {"UnidadEjecutora" : $("#cboUnidadEjecutora").val()},
			async : false,
			success : function(j) {
				if(j[0].error){
					swal("No se hizo el cambio de centro contable y unidad ejecutora",{icon:"info",button: "Cerrar"});
				}else{
					$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
					$("#usuarioUE").val(j[0].unidadEjecutora);
					$("#cIdEntidadContable").val(j[0].centroContable);
				}
			}
		});
	}
	</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Nuevo Procedimiento</legend>
				
				<input type="hidden" name="esServicio" id="esServicio" />
				<input id="cEjercicio" name="cEjercicio" type="hidden" size="4">
				<input id="usuarioUE" name="usuarioUE" type="hidden" size="4">
				<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="4">
				<input id="TipoConsolidado" name="TipoConsolidado" type="hidden" size="4">
				<input id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" type="hidden" size="4">
				<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4">
				<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="4">
				<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
				<input id="EstadoCaptura" name="EstadoCaptura" type="hidden" size="4">
				<input id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" type="hidden" size="4">
				<input id="tipoProceso" name="tipoProceso" type="hidden" size="4">
				<input id="cIdProcedimiento" name="cIdProcedimiento" type="hidden" size="4">  
				<input id="cProcedimientoCumple" name="cProcedimientoCumple" type="hidden" size="4">  
				<input id="cIdConsolidado" name="cIdConsolidado" type="hidden" size="4">    
				<input id="Activo" name="Activo" value="0" type="hidden" size="10">
				
				<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
			    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
			    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
			    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
			    <input type="hidden" name="arrayFechas" id="arrayFechas" value="" />
				<div id="tabs-0" align="center">
					<table border="0" align="center" width="800px" height="20">

						<tr>
							<td align="right">Unidad Ejecutora:</td>
							<td><select id="cboUnidadEjecutora" name="cboUnidadEjecutora" style="width: 30em;" onchange="cambiaCentrocontableUsuario(); muestraConsolidado();">
									<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">Tipo de Procedimiento:</td>
							<td><select id="cboTipoProcedimiento" name="cboTipoProcedimiento"style="width: 30em;" onchange="muestraConsolidado();validaFechaTerminoServicio(this);">
									<option value="" selected="selected"></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">Consolidado:</td>
							<td><select id="cboConsolidado" name="cboConsolidado" style="width: 30em;" onchange="muestraConsolidado();">
									<option value="" selected="selected"></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">Monto Neto del Consolidado:</td>
							<td><input type="text" id="montoNetoConsolidado" name="montoNetoConsolidado" value="$ 0.00" style="width: 30em; background-color:#D3D3D3 " readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td align="right">IVA del procedimiento(%):</td>
							<td>
<!-- 							<input type="text" id="nPorcentajeIVA" name="nPorcentajeIVA" style="width: 30em;" onkeypress="return onlyIntegers(event);"/> -->
								<select id="nPorcentajeIVA" name="nPorcentajeIVA" style="width: 30em;"> 
								</select> 
							</td>
						</tr>
						<tr>
						
							<td align="right">N&uacute;mero de Procedimiento (COMPRANET):</td>
							<td><input type="text" id="numero_externo" name="numero_externo" style="width: 30em;" /></td>
						</tr>
						<tr>
						
							<td align="right">Monto M&iacute;nimo y M&aacute;ximo por Tipo de Procedimiento</td>
							<td>
								<div id="tblMinimoMaximo" style="width:100%;"></div>
								
							</td>
						</tr>
						<tr>
							<td align="right">Tipo de Procedimiento:</td>
							<td><select id="cboCategoria" name="cboCategoria" onchange="validaLicitacionNuevo(); muestraConsolidado();" style="width: 30em;">
								</select>
							</td>
							<td id="etiquetaChkActivoNuevo" style="width:12em; display: none;" align="center" >
							
							¿Cuenta con documentos <br>o autorización <br>del comité?
							<BR>
								Si<input type="radio" id="doctosComite11" name="doctosComite" value="1" onclick="document.getElementById('Activo').value=this.value;validaCaratulaNuevo();"/>
						  &nbsp;No<input type="radio" id="doctosComite22" name="doctosComite" value="0" onclick="document.getElementById('Activo').value=this.value;validaCaratulaNuevo();"/>
							</td>		
						</tr>
						
						<tr>
								<td align="right">Descripci&oacute;n:</td>
                   				<td><textarea id="descripcion" name="descripcion"  style="height: 91px; width: 400px" ></textarea></td>
							</tr>	
							<tr>
								<td align="right">Tipo de Proceso:</td>
								<td><select id="tipoProcesoProcedimiento" name="tipoProcesoProcedimiento"	style="width: 30em;">
									<option value="" selected="selected"></option>
								</select>
							</td>
							</tr>
							<tr id="rowPlurianual">
								<td align="right">Es Plurianual:</td>
								<td align="left">
									<input type="checkbox"  id="isPlurianualCheck" name="isPlurianualCheck" onclick="PlurianualChecked();" /> 
									<input type="hidden"  id="isPlurianual" name="isPlurianual" value="0"/>
								</td>
							</tr>
							<tr id="row10Dias">
								<td align="right">Reducción de Plazos:</td>
								<td align="left"><input type="checkbox" checked="checked" id="10dias" name="10dias" /> </td>
							</tr>		
							<tr>
								<td colspan="2">
									<input type="hidden" name="nIdProcedimientoNuevo" id="nIdProcedimientoNuevo" />
									<input type="hidden" name="nIdFechaProcedimiento" id="nIdFechaProcedimiento" />
									<input type="hidden" name="nFechaProcedimiento" id="nFechaProcedimiento" />
									<div id="tablaFechasProcedimiento" style="width:100%;"></div>
								</td>
							</tr>
							</table>
					
							</div>	
					</fieldset>	
						
						<fieldset id="fieldsetArrendamiento">
					<legend>Arrendamiento de Inmuebles</legend>	
				
					  <div id="div_arrendamientos" align="center"  >
					    <table border="0"  width="700px" height="20" align="center">
					
							<tr >
							<td align="right">Uso y/o Denominacion Inmueble:</td>
							<td align="left"  ><select id="cboDenominacionInmueble" name="cboDenominacionInmueble" style="width: 25em;" onchange="muestraDenominacionOtros(this);" class="arrendamientos_div">
							<option value="" selected="selected"></option>
								</select><font color="red">*</font>
							</td>
							<td align="left">
							<textarea  id="descripcionOtros" name="descripcionOtros"  style="height: 91px; width: 200px" style="visibility: hidden;" class="arrendamientos_div" ></textarea>
							</td>
							</tr>
							
							</table>
							
							 <table border="0"  width="750px" height="20" align="center">
							<tr>
							<td align="right">Area Construida:</td>
							<td align="left"><input type="text" id="area_construida" name="area_construida" style="width: 30em;" onkeypress="return onlyNumbers2(event);"  class="arrendamientos_div" /></td><td align="left">m2 <font color="red">*</font></td>
						   </tr>
						
						   <tr>
							<td align="right">Area Rentable:</td>
							<td align="right"><input type="text" id="area_rentable" name="area_rentable" style="width: 30em;" onkeypress="return onlyNumbers2(event);"  class="arrendamientos_div"/></td><td align="left">m2 <font color="red">*</font></td>
						   </tr>
						
						    <tr>
							<td align="right">No.Empleados que laboran en el Inmueble:</td>
							<td align="left"><input type="text" id="numero_empleados" name="numero_empleados" style="width: 30em;" onkeypress="return onlyIntegers(event);" class="arrendamientos_div" /></td><td align="left">Empleados <font color="red">*</font></td>
						   </tr>
							
							<tr>
							<td align="right">Direccion Inmueble:</td>
							<td align="left"><textarea  id="direccion_inmueble" name="direccion_inmueble"  style="height: 91px; width: 250px" class="arrendamientos_div" ></textarea><font color="red">*</font></td>
						    </tr>
						   
							<tr>
							<td align="right">Unidad Administrativa que Ocupa el Inmueble:</td>
							<td align="left"><select id="cboUnidadEjecutoraInmueble" name="cboUnidadEjecutoraInmueble" style="width: 30em;" class="arrendamientos_div">
									<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
								</select><font color="red">*</font>
							</td>
							</tr>
				   
				     </table>	
				 
				    </div>
						</fieldset>	
					   <div>
					       <table align="center" >
								<tr>
									<td width="33%">&nbsp;</td>
									<td width="33%" align="center">
										<input type="button" name="btnGuardarProcedimiento" id="btnGuardarProcedimiento" value="GUARDAR"  class="btnInterfaceBG ui-button ui-corner-all"/>
									</td>
									
									<td width="33%" align="right">&nbsp;</td>
								</tr>
							</table>
					
					</div>	
		</form>				
	</body>
</html>
