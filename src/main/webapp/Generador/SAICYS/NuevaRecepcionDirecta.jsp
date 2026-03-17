<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioNR = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioNR == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user = usuarioNR.getLogin();
	Map rol = usuarioNR.getRoles();
	
	Calendar c1 = Calendar.getInstance(); // today
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	System.out.println(usuarioNR.getPropiedad("CCENTROCONTABLE").getValor());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>L&iacute;neas de Captura</title>
    
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
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var roles="";
		$(document).ready(function() {
			<%String role = "";
			String roles = "";
			NegativaPestana NegPestana = new NegativaPestana();
			NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic(
					"jdbc/gestion");
			//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
			NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic(
					"jdbc/gestion");
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry) it1.next();
				role = (String) r.getKey();
				roles += r.getKey().toString() + ",";
			}
			Map botones = nb.getBotones(role, "Requisiciones",
					"NuevoRequisiciones");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry) btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%}%>
			roles="<%=roles%>";
			$("#fechaRequerida").datepicker({
				beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",
				currentText: "Now",
				showOn: 'button',
				altField: "#actualDate",
				buttonImageOnly: true,
				minDate: "+0D",
				//minDate: "01/01/2014",
    			//maxDate:"28/02/2014",
//     			minDate: "-4M",
//     			maxDate:"-4M",	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
			});
			var date = new Date();
			var months = new Array('01','02','03','04','05','06','07','08','09','10','11','12');
			var dia="01";
			if(date.getDate()<10){
				dia="0"+date.getDate();
			}else{
				dia=date.getDate();
			}
			$("#fechaRequerida").val( dia+"/"+months[date.getMonth()]+"/" + date.getFullYear());
			document.getElementById("fechaRequerida").disabled = "disabled";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
				querySelectPost("tCatalogoUnidadEjecutoraRead", "cIdUnidadEjecutoraSl", {async: false});
			} 				
			else{
				//query para las vistas de los usuarios
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutoraSl", {async: false});
			}
			querySelectPost("mCatalogoTipoSolicitudRD", "cIdTipoSolicitud", {async: false});
			querySelectPost("mCatalogoCapituloDirecto", "cIdCapituloSl", {async: false});
			querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
			querySelectPost("mCatalogoAlcanceRead", "nIdAlcance", {async: false});
			if ($("#CENTROCONTABLE").val()== "10"){
				$("#nIdAlcance").val(1);
			}else{
				$("#nIdAlcance").val(2);
			}
			
			$("#AgregarLineasAgrup").button();
			querySelectPost("tCatalogoAlmacenesReadVistas", "cIdAlmacenEntrega", {async: false});
			//querySelectPost("mCatalogoFacturarRead", "cIdAlmacen", {async: false});
			querySelectPost("mCatalogoCategoriaProcedimientoRead", "nIdCategoria", {async: false});
			querySelectPost("mCatalogoAgnosRead", "cIdAgnos", {async: false});
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			querySelectPost("mCatalogoTipoGarantia", "cTipoGarantia", {async: false});
			querySelectPost("mCatalogoTipoGarantiaPorcentaje", "mPorcentajeGarantia", {async: false});
			//querySelectPost("UnidadBusca", "cIdAlmacen", {async: false});
			$( "#cIdTipoSolicitud" )
				.change(function() {
					if($("#cIdTipoSolicitud").val()=='RC'){
						$("#capitulos").val('2,5');
					}
					if($("#cIdTipoSolicitud").val()=='RO'){
						$("#capitulos").val('6');
					}
					if($("#cIdTipoSolicitud").val()=='RM'){
						$("#capitulos").val('2,3,5');
					}
					if($("#cIdTipoSolicitud").val()=='RD'){
						$("#capitulos").val('2,3');
					}						
					if($("#cIdTipoSolicitud").val()=='RS' || $("#cIdTipoSolicitud").val()=='RA'){
						$("#capitulos").val('3');
					}
					querySelectPost("mCatalogoCapituloCMBRead", "cIdCapituloSl", {async: false});
					$( "#cIdCapitulo" ).val($( "#cIdCapituloSl" ).val());
					querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
				});
			$( "#cIdCapituloSl" )
				.change(function() {
					$( "#cIdCapitulo" ).val($( "#cIdCapituloSl" ).val());
					querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
				});
			$("#chkAnexo").change(function() {
				if ($("#lAnexos").val() == "0")
					$("#lAnexos").val("1");
				else
					$("#lAnexos").val("0");
			});	
			queryFormPost("mSolicitud_LabelRead", { async:false });
			queryFormPost("mSolicitud_cIdSubPartidaRead", { async:false });
			//queryFormPost("mSolicitudDescripcionUnidad", {async: false});
			
// 			querySelectPost("BuscarCABMsRead", "cIdCABM", { async:false });
			
			queryFormPost("cuentaConsolidadosRead", { async:false });
			queryFormPost("mSolicitudLineas_MontosRead", { async:false });
			
			var cadena = $("#montoNeto").val();
			cadena = '$' + cadena;
			var pos = $("#montoNeto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoNeto").val(cadena);
			
			cadena = $("#montoBruto").val();
			cadena = '$' + cadena;
			pos = $("#montoBruto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoBruto").val(cadena);
			queryFormPost("mUsuarioMismaUE", {async: false   });
			queryFormPost("mSolicitud_EstadoSolicitudRead", {async:false});
			if (!($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0
				|| $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val() 
				||parseInt($("#usuariosMismaUE").val(),10)==1)) {
				/*document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = true;*/
			}
			else if ($("#nIdEstadoReq").val() == "1") { //capturada
				/*document.getElementById("imgAprobarLineasReq").disabled = false;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = false;*/
			}
			else if ($("#nIdEstadoReq").val() == "2") { //solicitada
				/*document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = true;
				document.getElementById("AgregarLineasAgrup").disabled = true;*/
			}
			else if ($("#nIdEstadoReq").val() == "3") { //Aprobada
				/*document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = false;
				document.getElementById("imgAnularLineasReq").disabled = true;
				document.getElementById("AgregarLineasAgrup").disabled = true;*/
			} 
			else if ($("#nIdEstadoReq").val() == "4") { //anulada
				/*document.getElementById("imgAprobarLineasReq").disabled = true;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = true;
				document.getElementById("AgregarLineasAgrup").disabled = true;*/
			}
			else if ($("#nIdEstadoReq").val() == "5") { //habilitada por consolidado
				/*document.getElementById("imgAprobarLineasReq").disabled = false;
				document.getElementById("imgDevolverLineasReq").disabled = true;
				document.getElementById("imgAnularLineasReq").disabled = true;
				document.getElementById("AgregarLineasAgrup").disabled = true;*/
			}
			
			//una vez llenados todos los elementos, se desabilitan para evitar porblemas
			if (!(roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0||roles.indexOf("JEFES") >= 0 
				|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() || parseInt($("#usuariosMismaUE").val(),10)==1)
					|| $("#nIdEstadoReq").val() == "2"|| $("#nIdEstadoReq").val() == "3" || $("#nIdEstadoReq").val() == "4" ||  $("#nIdEstadoReq").val() == "5") {
				document.getElementById("btnGuardarLineasReq").disabled = true;//.setAttribute("disabled", "disabled");
				document.getElementById("btnEliminarLineasReq").disabled = true;//setAttribute("disabled", "disabled");
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
				document.getElementById("AgregarLineasAgrup").disabled = true;
			}
			/*
			if ($("#nIdEstadoReq").val() == "3" || $("#nIdEstadoReq").val() == "4") { //aprobada
				document.getElementById("btnGuardarLineasReq").disabled = true;//.setAttribute("disabled", "disabled");
				document.getElementById("btnEliminarLineasReq").disabled = true;//setAttribute("disabled", "disabled");
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
			}
			*/
			
			
			if ($("#cIdCABM").val() == null)
				document.getElementById("trCABM").style.display = "table-row";
			
			$("#cIdCABM").change(function() {
				//oTableCAMBs.fnClearTable(oTableCAMBs);
				oTableCAMBs2.fnClearTable(oTableCAMBs2);
				//mostrar();
				mostrar2();
			}); 
			$("#btnGuardarLineasReq").button().click(function(){
			 	guardar();
			});
			
			$("#btnEliminarLineasReq").button().click(function(){
			 	eliminarLineas();
			});
			
			//Para Fecha de vencimiento
			//cambiaColorFecha();
			//Agrega el firmante de gerente en automatico
			/*queryFormPost("mSolicitudFirmantesCuentaFirmantesRead", {async: false});
			if(parseInt($("#nFirmantes").val(),10)==0){
				//obtiene el nidfirmante de cada ue que sea gerente
				queryFormPost("mSolicitudFirmantes_siguienteConsecutivoRead", {async: false   });
				queryFormPost("obtieneNidFirmante", {async: false,callback : function() 
						{
							//Se agrega el firmante
							if($("#nIdFirmante").val()!=0)
								queryFormPost("mSolicitudFirmantesCreate", {async: false});
						}
				});
				
			}*/
		
			$('#tblCucop tr').live('dblclick', function() {
				//$(this).addClass('row_selected');
				
				//if($("#nIdEstadoReq").val()=="1"){
					if ( $(this).hasClass('row_selected') )             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					
					var anSelected = fnGetSelected( oTableCucop );
					if (anSelected != "") {
						var aData = oTableCucop.fnGetData(anSelected[0]);
						$("#cIdCABM").val(aData[0]);
						$("#cDescripcion").val(aData[1]);
						$(this).removeClass('row_selected'); 
						//mostrar();
						mostrar2();
					}
				/*}else{
					alert("El estatus de la requisición no permite esta operación");
				}*/
			});
			$('#tblRequisicionMeses td:nth-child(3)').live('dblclick', function() {   
				agregarLinea(this,1);
			});
			$('#tblRequisicionMeses td:nth-child(4)').live('dblclick', function() {   
				agregarLinea(this,2);
			});
			$('#tblRequisicionMeses td:nth-child(5)').live('dblclick', function() {   
				agregarLinea(this,3);
			});
			$('#tblRequisicionMeses td:nth-child(6)').live('dblclick', function() {   
				agregarLinea(this,4);
			});
			$('#tblRequisicionMeses td:nth-child(7)').live('dblclick', function() {   
				agregarLinea(this,5);
			});
			$('#tblRequisicionMeses td:nth-child(8)').live('dblclick', function() {   
				agregarLinea(this,6);
			});
			$('#tblRequisicionMeses td:nth-child(9)').live('dblclick', function() {   
				agregarLinea(this,7);
			});
			$('#tblRequisicionMeses td:nth-child(10)').live('dblclick', function() {   
				agregarLinea(this,8);
			});
			$('#tblRequisicionMeses td:nth-child(11)').live('dblclick', function() {   
				agregarLinea(this,9);
			});
			$('#tblRequisicionMeses td:nth-child(12)').live('dblclick', function() {   
				agregarLinea(this,10);
			});
			$('#tblRequisicionMeses td:nth-child(13)').live('dblclick', function() {   
				agregarLinea(this,11);
			});
			$('#tblRequisicionMeses td:nth-child(14)').live('dblclick', function() {   
				agregarLinea(this,12);
			});
			
			//selección de valores por default en los dropdownlist
			$("#cIdUnidadEjecutoraSl").val($("#cIdUnidadEjecutoraUsuario").val());
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
			if (roles.indexOf("ADMIN_RECMAT") >= 0)
				queryFormPost("usuario_UnidadEjecutoraRead", {async: false});
			 $("#btnGuardarNuevoReq").button().click(function(){
			 	 guardarRequisicion ();
			 });
			 mostrarCucop();
			//mostrar();			
			mostrarLineas();
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
		    $("#nIdPeriodo").val("<%=mesActual%>");
		    $("#CUCOPSDIV").hide();
	
			
		});
		
		function guardarRequisicion () {
			$("#mImportePoliza").val($("#mImportePoliza").val().replace(/,/g, ''));
			if (numberValidator(document.getElementById("nIdPlazo"),document.getElementById("trPlazo")) &&
				//floatPercentValidator(document.getElementById("mPorcentajeGarantia"),document.getElementById("trPorGarantia")) &&
			 	floatMoneyValidator(document.getElementById("mImportePoliza"),document.getElementById("trImpPoliza")) &&
			 	dateValidator(document.getElementById("fechaRequerida"), document.getElementById("trFechaRequerida"))
			 	) {
				 	$("#mPorcentajeGarantia").val($("#mPorcentajeGarantia").val().replace("%", ""));
					$("#mImportePoliza").val($("#mImportePoliza").val().replace("$", ""));
					$("#fRequerida").val($("#fechaRequerida").val());
					queryFormPost("mSolicitud_siguienteConsecutivoRead", {async: false}); 
					queryFormPost({
							queryName: "mSolicitudCreate",
							async: false,
							callback: function() {
								mostrarCucop();			
								mostrarLineas();
								$("#CUCOPSDIV").show();
								$("#btnaprobar").show();								
								$("#btnGuardarNuevoReq").hide();
								//Bitácora
								$("#cAccion").val("CREARECEPCIONDIRECTA");
								//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
								$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
								queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
								$("#cIdSolicitu").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
								$("#Foliotr").show();
								deshabilitaCombos(true);						
							}});
					
					
					
					
					//window.location = "Requisiciones.jsp?tab=3&cEjercicio=" + $("#cEjercicio").val() + "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val() + "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val() + "&nIdConsecutivo=" + $("#nIdConsecutivo").val();
			 }
			 else
			 	alert("Favor de revisar los campos antes de continuar.");
		};
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		function deshabilitaCombos(logic){
			//alert(logic);
			document.getElementById("cIdUnidadEjecutoraSl").disabled = logic;
			document.getElementById("cIdCapituloSl").disabled = logic;
			document.getElementById("nIdAlcance").disabled = logic;
			document.getElementById("cIdAlmacenEntrega").disabled = logic;	
			//document.getElementById("cIdTipoSolicitud").disabled = logic;
		}
		function dateValidator (field, printfield) {
			var pattern=/^[0123]\d[\/][012]\d[\/][12]\d\d\d$/
			if (field.value.replace(/^\s+|\s+$/g,"") == "") {
				printfield.style.display = "table-row";
				field.value = field.value.replace(/^\s+|\s+$/g,"");
			}
			else if (!pattern.test(field.value))
				printfield.style.display = "table-row";
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		function numberValidator( field, printfield ) {
			var pattern=/^\d+$/
			if (!pattern.test(field.value))
				printfield.style.display = "table-row";
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		function floatPercentValidator( field, printfield ) {
			var pattern=/^[1][0][0]([.][0][0]?)?[%]?$/
			if (field.value == "")
				printfield.style.display = "table-row";
			else if (!pattern.test(field.value)) {
				pattern=/^\d?\d([.]\d\d?)?[%]?$/
				if (!pattern.test(field.value)) {
					printfield.style.display = "table-row";
				}
				else {
					printfield.style.display = "none";
					return true;
				}
			}
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		function floatMoneyValidator( field, printfield ) {
			var pattern=/^[$]?\d+([.]\d\d?)?$/
			if (field.value == "")
				printfield.style.display = "table-row";
			else if (!pattern.test(field.value))
				printfield.style.display = "table-row";
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		function onlyPercentage(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed == 46 || keyPressed == 37)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		function Sinfrmt(fld){
			var valcol = $("#"+fld).val();
			valcol = valcol.replace("$", "");
			valcol = valcol.replace(",", "");
			$("#"+fld).val(valcol);
		}		
		function cambiafrmt(fld){
	   		$("#"+fld).formatCurrency();
		}
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(/,/g, '');
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true;
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function cambioTipoGarantia(){
			querySelectPost("mCatalogoTipoGarantiaPorcentaje", "mPorcentajeGarantia", {async: false});
			cambioTipoGarantiaId();
		}
		function cambioTipoGarantiaId(){
			$("#cIdPorcentaje").val($("#mPorcentajeGarantia option:selected").text());
			queryFormPost("mCatalogoTipoGarantiaId", {async: false});
			//alert("nId : "+$("#nId").val());
		}
		function cambiaCentrocontableUsuario(){
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraSl").val())
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						alert("No se hizo el cambio de centro contable y unidad ejecutora");
					}else{
						$("#cUnidadEjecutora").val(j[0].unidadEjecutora);
						$("#CENTROCONTABLE").val(j[0].centroContable);
					}
				}
			});
		}
		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ ){
				if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		function mostrarCucop() {
			var qw = "cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() 
					+ "' AND cIdSubPartida = '" + $("#cIdSubPartida").val() 
					+ "' AND cEjercicio = '" + $("#cEjercicio").val() + "'";
					//alert(qw);
			oTableCucop =$("#tblCucop").dataTable({
				//sScrollX: "50%",
				//sScrollXInner: "285%",
				bScrollCollapse: true,
				bDestroy: true,
				
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=v_mCucopsDisponiblesAgrupados&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cidCABM" },
					{ sName: "cCabm" },
					{ sName: "cantDisp" }
					
				],
				fnInitComplete: function(oSettings, json) {
						//mostrar();
						mostrar2();
					}
        	});
		}
		
		function mostrar() {
			//var qw = "(cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (cIdSubPartida = '" + $("#cIdSubPartida").val() + "') AND (cEjercicio = '" + $("#cEjercicio").val() + "') AND (CABM LIKE '" + $("#cIdCABM").val() + "')"; //" + $("#cIdCABM").val() + "
			oTableCAMBs = $("#tblRequisicionMeses").dataTable({
				sScrollX: "100",
				//sScrollXInner: "285%",
				bScrollCollapse: true,
				bDestroy: true,
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUnidadEjecutora").val() + "', '" + $("#cIdCABM").val() + "' , '" + 
					 $("#cIdSubPartida").val() + "')",//"&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ], [ 2, "asc" ], [ 3, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "enero" },
					{ sName: "febrero" },
					{ sName: "marzo" },
					{ sName: "abril" },
					{ sName: "mayo" },
					{ sName: "junio" },
					{ sName: "julio" },
					{ sName: "agosto" },
					{ sName: "septiembre" },
					{ sName: "octubre" },
					{ sName: "noviembre" },
					{ sName: "diciembre" },
					{ sName: "nPorcentajeIVA"}
				]
        	});
		}
		function mostrar2() {
			//var qw = "(cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (cIdSubPartida = '" + $("#cIdSubPartida").val() + "') AND (cEjercicio = '" + $("#cEjercicio").val() + "') AND (CABM LIKE '" + $("#cIdCABM").val() + "')"; //" + $("#cIdCABM").val() + "
			oTableCAMBs2 = $("#tblRequisicionMeses2").dataTable({
				//sScrollX: "100",
				//sScrollXInner: "285%",
				bScrollCollapse: true,
				bDestroy: true,
				"iDisplayLength": 12,
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario2('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUnidadEjecutora").val() + "', '" + $("#cIdCABM").val() + "' , '" + 
					 $("#cIdSubPartida").val() + "')",//"&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdPeriodo" ,bVisible: false},
					{ sName: "mes" },
					{ sName: "cIdCABM" },
					{ sName: "nCantidadDisponibilidad" },
					{ sName: "mMontoDisponibilidad" },
					{ sName: "nPorcentajeIVA" ,bVisible: false},
					{ sName: "cCABM",bVisible: false},
					{ sName: "CantAgregar" },
					{ sName: "mPrecioUnitario" ,bVisible: false}
					
				]
        	});
		}
		function mostrarLineas() {
			queryFormPost("mSolicitudLineas_MontosRead", { async:false });
			var roles="<%=roles%>";
			var cadena = $("#montoNeto").val();
			cadena = '$' + cadena;
			var pos = $("#montoNeto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoNeto").val(cadena);
			
			cadena = $("#montoBruto").val();
			cadena = '$' + cadena;
			pos = $("#montoBruto").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#montoBruto").val(cadena);
		var btnLnCopia=1;
			var btnLnElimina=1;
			//var qw = "(cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (cIdTipoSolicitud = '" + $("#cIdTipoSolicitud").val() + "') AND (cEjercicio = '" + $("#cEjercicio").val() + "') AND (nIdConsecutivo = " + $("#nIdConsecutivo").val() + ")";
			oTableLineas = $("#tblLineas").dataTable({
				//sScrollX: "100",
				//sScrollXInner: "200%",
				bScrollCollapse: true,
				bDestroy: true,
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
				bServerSide: true,
				bProcessing: true,
				bLengthChange: true,
				iDisplayLength: 10,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + 
					"/" + window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_mSolicitudLineasDirecta('" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() + "','" + $("#cIdTipoSolicitud").val() + "'," + $("#nIdConsecutivo").val() +")", //&qw=" + qw,
				
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "lineaCopia" ,bVisible: false},
					{ sName: "nIdLineas" },
					{ sName: "cIdCABM" },
					{ sName: "cMes" ,bVisible: false},
					{ sName: "cDescripcion" },
					{ sName: "cDescripcionAdicional" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" },
					{ sName: "mPrecioUnitarioPAAS" },
					{ sName: "nPorcentajeIVA" },
					{ sName: "mImporteNeto" },
					{ sName: "cIdUnidadMedida" },
					{ sName: "cDisponible",bVisible: false },
					{ sName: "cMontoDisponible",bVisible: false },
					{ sName: "lineaElimina" },
					{ sName: "nIdPeriodo"},
					{ sName: "nIdLineaSolicitud"}
				] ,
					fnInitComplete: function(oSettings, json) {
						queryFormPost("mSolicitudLineasCuentaRead", { async:false });
						$("#" + $("#paginacion").val()).click();
					}
					, fnDrawCallback: function(oObj) {
						if (!(roles.indexOf("ADMIN_RECMAT") >= 0||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 
							|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() ||parseInt($("#usuariosMismaUE").val(),10)==1)
							|| $("#nIdEstadoReq").val() == "2"|| $("#nIdEstadoReq").val() == "3" || $("#nIdEstadoReq").val() == "4" || $("#nIdEstadoReq").val() == "5") {
							for (var i = 1; i <= $("#nLineas").val(); i++) {
								if ($("#nIdLineas_" + i).val() != null) {
									$("#btnLnCopia_" + i).css("visibility", "hidden");
									$("#btnLnElimina_" + i).css("visibility", "hidden");
									$("#cDescripcion_" + i).css("border-width", "0");
									$("#cDescripcion_" + i).css("readonly", "readonly");
									$("#cDescripcion_" + i).css("background-color", "transparent");
									$("#cDescripcionAdicional_" + i).css("readonly", "readonly");
									$("#cDescripcionAdicional_" + i).css("background-color", "transparent");
									$("#nCantidad_" + i).css("border-width", "0");
									$("#nCantidad_" + i).css("readonly", "readonly");
									$("#nCantidad_" + i).css("background-color", "transparent");
									$("#mPrecioUnitario_" + i).css("border-width", "0");
									$("#mPrecioUnitario_" + i).css("readonly", "readonly");
									$("#mPrecioUnitario_" + i).css("background-color", "transparent");
								}
							}
						}
					}
        	});
		}
		//<button id="btnLnCopia_1" name="btnLnCopia_1" onclick="copiaLinea(1);">Copia</button>
		function agregarLinea(td,mes) {
			var roles="<%=roles%>";
			if ($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() != "5" &&
					(roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val())) {     
				/*
				var aPos = $('#tblRequisicionMeses').dataTable().fnGetPosition(td);
  				var aData = $('#tblRequisicionMeses').dataTable().fnGetData(aPos[0]);
  				$("#cIdCABMIns").val($("#cIdCABM_" + aPos[0]).val()); //(aPos[0] + 1)
				$("#cDescripcion").val($("#cCABM_" + aPos[0]).val());
				*/
  				$("#cIdCABMIns").val($("#cIdCABM").val()); //(aPos[0] + 1)
				$("#cDescripcion").val($("#cCABM").val());
				
  				//alert(aData[0]);
  				//alert($("#cIdCABMIns").val() + "-" + $("#cDescripcion").val());
  				
				$("#nIdPeriodo1").val(mes + "");
				$("#nIdPeriodo2").val(mes + "");
				queryFormPost("obtenMontoYCantidadRead", { async:false });
				
				$("#nPorcentajeIVA").val($("#porcentaje_iva").val()); //_" + aPos[0]
				if (parseInt($("#nCantidadDisponibilidad").val(),10) > 0) {
					if (parseFloat($("#mPrecioUnitario").val()) <= parseFloat($("#mMontoDisponibilidad").val())) {
						$("#cTipos").val(1);
						$("#nIdPeriodo").val(mes + "");
						queryFormPost("insertaSolicitudLinea_Tipos", {async:false});
						//oTableCAMBs.fnClearTable(oTableCAMBs);
						oTableCAMBs2.fnClearTable(oTableCAMBs2);
						oTableLineas.fnClearTable(oTableLineas);
						//mostrar();
						mostrarLineas();
					}
					else {
						alert("No se cuenta con presupuesto para este CUCOP, edite el programa anual.");
					}
				}
				else {
					alert("No hay disponiblidad para este CUCOP, edite el programa anual."); 
				}
			}else{
				alert("No tienes permisos o el estado de la requisición no permite hacer cambios");
			}
		}
		function agregarLineasAgrupadas(){
			var roles="<%=roles%>";
			var token="";
			var cadenaMesesCantidad="";
			var cadenaCantidad="";
			var cadenaMeses="";
			$("#cIdCABMIns").val($("#cIdCABM").val()); //(aPos[0] + 1)
			
			if ($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() != "5" &&
					(roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0 
					|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()||parseInt($("#usuariosMismaUE").val(),10)==1 ) ) { 
				var aTrs = oTableCAMBs2.dataTable().fnGetNodes();
				for ( var i=0 ; i<aTrs.length; i++ )     
				{       					
					aData = oTableCAMBs2.fnGetData(aTrs[i]);
					if(parseInt($("#cantAgregar"+(aData[0])).val(),10)!=0 && $("#cantAgregar"+(aData[0])).val()!= ""){
						cadenaMesesCantidad+=token+aData[0]+"-"+$("#cantAgregar"+(aData[0])).val()+"|"+aData[8];
						cadenaCantidad+=token+$("#cantAgregar"+(aData[0])).val();
						cadenaMeses+=token+aData[0];
						token=",";
						$("#mPrecioUnitario").val(aData[8]);
						$("#nPorcentajeIVA").val(aData[5]);
					}
				}
				$("#cadenaMesesCantidad").val(cadenaMesesCantidad);
				queryFormPost("insertaSolicitudLinea_Agrupadas", {async:false});
				//oTableCAMBs.fnClearTable(oTableCAMBs);
				oTableCAMBs2.fnClearTable(oTableCAMBs2);
				oTableLineas.fnClearTable(oTableLineas);
				mostrarCucop();
				mostrar2();
				mostrarLineas();
				//Bitácora
				$("#cAccion").val("AGREGAlINEA_REQUISICION");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}else{
				alert("No tienes permisos o el estado de la requisición no permite hacer cambios");
			}
		}
		function guardar() {
			queryFormPost("mSolicitudLineasCuentaRead", { async:false });
			for (var i = 1; i <= $("#nLineas").val(); i++) {
				if ($("#nIdLineas_" + i).val() != null) { //no se pueden encontrar todos los id's debido a la paginacion
					$("#nIdLineaSolicitud").val($("#nIdLineas_" + i).val());
					$("#cDescripcionAdicional").val($("#cDescripcionAdicional_" + i).val());
					$("#nCantidad").val($("#nCantidad_" + i).val());
					$("#mPrecioUnitario").val($("#mPrecioUnitario_" + i).val().replace("$", "").replace(",", ""));
					$("#mPrecioUnitarioPAAS").val($("#mPrecioUnitarioPAAS_" + i).val().replace("$", "").replace(",", ""));
					$("#nIdPeriodo").val($("#nIdPeriodo_" + i).val());
					$("#cIdCABMIns").val($("#cIdCABMLinea_" + i).val());
					$("#mMontoNetoLinea").val($("#mImporteNeto_" + i).val().replace("$", "").replace(",", ""));
					queryFormPost("updateSolicitudLinea", {async:false});
				}
				queryFormPost("deleteSolLineaApartado", {async:false});
			}
			//oTableCAMBs.fnClearTable(oTableCAMBs);
			oTableCAMBs2.fnClearTable(oTableCAMBs2);
			oTableLineas.fnClearTable(oTableLineas);
			//mostrar();
			mostrar2();
			mostrarLineas();
			alert("Datos Guardados.\nRevisa que tus montos netos sean los correctos en cada linea.\n Puedes modificar centavos en el importe neto.");
			//window.location="Requisiciones.jsp?tab=5";
		}
		
		function eliminarLineas() {
			if (confirm("¿Está seguro que desea eliminar las líneas? Una vez confirmado, no podrá deshacer los cambios.")) {
				var min = -1;
				var max = -1;
				var i = 0;
				while (min == -1) {
					if ($("#nIdLineas_" + i).val() != null) {//no se encuentran todos los id's debido a la paginación
						min = parseInt($("#nIdLineas_" + i).val(),10);
					}
					i++;
				}
				max = min + 10 - 1; //10 por la paginacion
				$("#max").val(max);
				$("#min").val(min);
				queryFormPost("eliminaLineasDesdeHasta", { async:false });
				queryFormPost("deleteSolLineaApartado", {async:false});
				//oTableCAMBs.fnClearTable(oTableCAMBs);
				oTableCAMBs2.fnClearTable(oTableCAMBs2);
				oTableLineas.fnClearTable(oTableLineas);
				mostrarCucop();
				mostrar2();
				mostrarLineas();
				//Bitácora
				$("#cAccion").val("ELIMINAlINEAS_REQUISICION");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}
		}
		
		function eliminaLinea(indiceTabla) {
			alert(indiceTabla);
			queryFormPost("mSolicitud_EstadoSolicitudRead", {async:false});
			if (($("#nIdEstadoReq").val() == "1")) {
				//alert("#nIdLineas_" + indiceTabla + "-           " + $("#nIdLineas_" + indiceTabla).val());
				$("#nIdLineaSolicitud").val(indiceTabla);
				queryFormPost("pa_mSolicitudLineasBorrarYRenumerar", {async:false});
				queryFormPost("deleteSolLineaApartado", {async:false});
				//oTableCAMBs.fnClearTable(oTableCAMBs);
				oTableCAMBs2.fnClearTable(oTableCAMBs2);
				oTableLineas.fnClearTable(oTableLineas);
				mostrarCucop();
				mostrar2();
				mostrarLineas();
				//Bitácora
				$("#cAccion").val("ELIMINAlINEA_RECEPCION");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}
		}
		
		function copiaLinea(indiceTabla) {
			var roles="<%=roles%>";
			if ((roles.indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() ||parseInt($("#usuariosMismaUE").val(),10)==1) &&
					($("#nIdEstadoReq").val() == "1")) {
				//actualizacion de los registros, previo al copiado de las líneas
				for (var i = 1; i <= $("#nLineas").val(); i++) {
					if ($("#nIdLineas_" + i).val() != null) { //no se pueden encontrar todos los id's debido a la paginacion
						$("#nIdLineaSolicitud").val($("#nIdLineas_" + i).val());
						$("#cDescripcion").val($("#cDescripcion_" + i).val());
						$("#nCantidad").val($("#nCantidad_" + i).val());
						$("#mPrecioUnitario").val($("#mPrecioUnitario_" + i).val().replace("$", ""));
						$("#nIdPeriodo").val($("#nIdPeriodo_" + i).val());
						$("#cIdCABMIns").val($("#cIdCABMLinea_" + i).val());
						queryFormPost("updateSolicitudLinea", {async:false});
					}
				}
				if ($("#cDisponible_" + indiceTabla).val() > 0) {
					$("#cTipos").val(1);
					//$("#mPrecioUnitario").val($("#mPrecioUnitario_" + indiceTabla).val().replace("$", ""));
					$("#nIdPeriodo").val($("#nIdPeriodo_" + indiceTabla).val());
					$("#cIdCABMIns").val($("#cIdCABMLinea_" + indiceTabla).val());
					queryFormPost("obtencCABMRead", { async : false });
					//$("#cDescripcion").val($("#cDescripcion_" + indiceTabla).val());
					//$("#nPorcentajeIVA").val($("#mPorcentajeIVA_" + indiceTabla).val().replace("%", ""));
					queryFormPost("obtencPrecioPARead", { async : false });
					queryFormPost("obtencIVAPARead", { async : false });
					queryFormPost("insertaSolicitudLinea_Tipos", {async:false});
					$("#nIdLineas_" + i).val();
					//oTableCAMBs.fnClearTable(oTableCAMBs);
					oTableCAMBs2.fnClearTable(oTableCAMBs2);
					oTableLineas.fnClearTable(oTableLineas);
					//mostrar();
					mostrar2();
					mostrarLineas();
				}
				else
					alert("Ya no hay disponibilidad en Programa Anual. Editarlo para agregar más líneas.");
			}else{
				alert("No tienes permisos o el estado de la requisición no permite hacer cambios");
			}
		}
		
		function aprobarRequisicion() {
			var imgAprobar=1;
			if (imgAprobar==0){
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
				queryFormPost("SolicitudImporteNetoRead", { async:false });
				queryFormPost("montoTotalSalarioMinimo", { async:false });
				alert ($("#mImporteNeto").val()+"-"+$("#montoSalarioMinimo").val());
			if(parseFloat($("#mImporteNeto").val())>parseFloat($("#montoSalarioMinimo").val())){
				alert("No se puede aprobar ya que el monto maximo de una Recepcion Directa es: "+$("#montoSalarioMinimo").val());
				return;
			}
			if ($("#nLineas").val() > 0) {
				//Regla de negocio
				var currentTime = new Date();
				   
				var fechaCompleta=currentTime.getFullYear() + '-' + currentTime.getDate() + '-' +(currentTime.getMonth()+1) + ' ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '.' + currentTime.getMilliseconds();
				$("#fAprobacion").val(fechaCompleta);
				//alert("Formato del currentTime "+currentTime);
				//alert($("#fAprobacion").val());
				$("#nIdEstado").val("3"); //aprobada
				$("#nIdEstadoPrecomprometido").val("3"); //para regularización
				queryFormPost("mSolicitudAprobarUpdate", { async:false });
				queryFormPost("sp_mSolicitudEstadoPrecomprometido", {async:false});
				//Bitácora
				$("#cAccion").val("APRUEBA_REQUISICION");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				
				alert("La requicisión ha sido aprobada.");
				window.location = "Requisiciones.jsp?tab=3";
			}
			else
				alert("Por lo menos debe haber una línea capturada para aprobar la requisición");
			}else{
				alert("No tiene permisos para acceder a esta opción");
			}
		}
	
		
		function devolverRequisicion () {
			var imgDevolver=1;
			var miPopup;
			if (imgDevolver==0){
				
				if ($("#cIdTipoSolicitud").val() == 'RM') {
					$("#cIdSolicitudMod").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());					
					$("#existeMod").val('');
					queryFormPost("mPedidoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						alert("No se puede devolver la requicision porque esta asociada a un pedido modificatorio");
						return;
					}
					
					$("#existeMod").val('');
					queryFormPost("mContratoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						alert("No se puede devolver la requicision porque esta asociada a un contrato modificatorio");
						return;
					}
				}
				
				if ($("#nConsolidados").val() == "0" || $("#nConsolidados").val() == "") {
				//Regla de negocio
				
				var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
				window.open("LineasSolicitudNotas.jsp?tipo=1&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
				
				
				
			}
			else
				alert("Esta requisición está asociada a un consolidado. Para devolverla, primero es necesario eliminarla del consolidado.");
			}else{
				alert("No tiene permisos para acceder a esta opción");
			}
			
		}
		function anularRequisicion () {	  
			
			var imgAnular=1;
			if (imgAnular==0){
				var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
				window.open("LineasSolicitudNotas.jsp?tipo=2&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
			}else{
				alert("No tiene permisos para acceder a esta opción");
			}
			
		}
// 		function textCounter( field, maxlimit ) {
// 			if ( field.value.length > maxlimit )
// 				field.value = field.value.substring( 0, maxlimit );
// 		}
		
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			return (keyPressed >= 48 && keyPressed <= 57);
		}

		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function openPDF(ext){
			
			var imgPdf=1;
			if (imgPdf==0){
			 if($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" ){
			  $("#cIdSolicitudRep").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
			   //se revisa si ya existe una linea en la tabla de msolicitudlineasapartado
			  	queryFormPost("numeroLineasApartadoSolicitud", { async : false});
				 	if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)> 0){
				  //checamos si el numero de lineas de msolicitud es igual a msolicitudlineasapartado
				   		queryFormPost("numeroLineasSolicitud", { async : false});
				   		if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)== parseInt($("#numeroLineasSolicitud").val(),10)){
				   		      queryFormPost("sumaLineasSolicitud", { async : false}); 
					   		   queryFormPost("sumaLineasApartadoSolicitudLineas", { async : false});
					   		   var lineasSolicitud=parseFloat($("#sumaLineasSolicitud").val());
					   		   var lineasApartadoSolicitud=parseFloat($("#sumaLineasApartadoSolicitudLineas").val());
					   		    //var total=lineasSolicitud-lineasApartadoSolicitud;
					   		  if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 1.0){
				   		    //nos puede ser mayor o menor a 1.0 
				   		    alert("La suma de las lineas presupuestadas no es igual a la suma de las lineas de solicitud favor de revisar.");
				   		    return;
				   		   }
				   		    else 				   		    
				   		      if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 0.0001){
				   		      //se ajusta la requisiscion
				   		       queryFormPost("pa_ajustePresupuestoRequisiscion", {async: false});
				   		      
				   		      }
				   		    
				   		    
				   		    }else{
				   		      alert("La requisicion no se puede imprimir porque no ha presupuestado el total de las lineas de la requisicion");
				   		      return;
				   		    
				   		    }
				   		   
				   		}
				   }		
						
				if(ext!='csv'){
					window.open(
					"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn=rptRequisiciones.jasper"
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else{
					window.open(
					"../../servlet/CatalogosCSV?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn=rptRequisiciones.jasper"
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
					
			}else{
				alert("No tiene permiso para acceder a esta opción");
			}
			
		}
		
		/*
		function openExcel(){
			window.open(
				"../../admin/SeguridadCatalogos?"
					+ "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=rptRequisiciones.jasper"
					+ "&cEjercicio=" + $("#cEjercicio").val()
					+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
					+ "&cIdTipoSolicitud=RN" + $("#cIdTipoSolicitud").val()
					+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
					+ "&xls=SI",
				"popacuse",
				"scrollbars=1, resizable=yes, width=1024, height=768");
		}
		*/
		
		function metodo(i) {
			if (band == false) {
				if (i == -1)
					i = $("#paginacion").val() - 1;
				if (i == -2)
					i = $("#paginacion").val() + 1;
				$("#paginacion").val(i);
			}
			band!=band;
		}
		var availableTags=[""];
  		function autoCoplete(name,CUCOP){
  			var qw = "'"+CUCOP+"'"; 
  				$("#cucop").val(CUCOP);
	    		
	    		$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: "AUTOCOMPLETECUCOP", Param:"",Campos:qw, MaxReg:"" , ajax: 'false'}, 
					function(j)
					{
 						//alert(j.length);
						for (var i = 0; i < j.length; i++) 
				    	{
				    		availableTags[i]=j[i].Col0;
				    		
				    	}
						
					});
				
	    		
  		}
  		
  		
  		function textCounter(name,field,maxlimit) {
  			 $( "#"+name ).autocomplete({
  			 	source: availableTags,
  			 	minLength: 0}).focus(function () {
    				$(this).autocomplete("search");
				});
  			
  			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function cambiafrmt(fld){
	   		$("#"+fld.id).formatCurrency();
		}
		
		function enviarPagoNvo(){
			queryFormPost("SolicitudImporteNetoRead", { async:false });
			queryFormPost("montoTotalSalarioMinimo", { async:false });
			//alert ($("#mImporteNeto").val()+"-"+$("#montoSalarioMinimo").val());
			
			if(parseFloat($("#mImporteNeto").val())>parseFloat($("#montoSalarioMinimo").val())){
				//$("#montoSalarioMinimo").formatCurrency();
				var salario =$("#montoSalarioMinimo").val();
				alert("No se puede aprobar ya que el monto maximo de una Recepcion Directa es: $"+salario);
				return;
			}else{
				enviarPago();
			}
		}
		
		function cambiaColorFecha(){
			var longitudReq=$("#lblRequisicion").val().length;
			$("#Req").val($("#lblRequisicion").val().substring(2,(parseInt(longitudReq,10)-2)));
			queryFormPost("mFechaVencimiento", { async:false });
			
			$("#fechaVence").val($("#vence").val());
			if(parseInt($("#difFecha").val(),10)<4 ) 
				$("#fechaVence").css("color","red");
			if(parseInt($("#difFecha").val(),10)>3 & parseInt($("#difFecha").val(),10)<11)
				$("#fechaVence").css("color","orange");
			if(parseInt($("#difFecha").val(),10)>10)	
				$("#fechaVence").css("color","green");
		}
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
	<form>
			<div id="container" class="container" style="width: 90%;">
		  			<fieldset >
				<legend>Nueva Recepcion Directa</legend>
				<table align="left" cellpadding="2" width="90%">
					<tr>
						<td><input type="hidden" name="U_LOGIN" id="U_LOGIN"
							value="<%=usuarioNR.getLogin()%>" /> <input type="hidden"
							name="R_NOMBRE" id="R_NOMBRE" /> <input type="hidden"
							name="cUnidadEjecutora" id="cUnidadEjecutora"
							value="<%=usuarioNR.getU_UR()%>" /></td>
							
					</tr>
					<tr id="Foliotr" style="display: none;">
						<td width="25%" align="left" id="Foliotr">Folio Recepcion:</td>
						<td align='left'><input type="text" id="cIdSolicitu"
							name="cIdSolicitu" readonly="readonly"/></td>
					</tr>
					<tr id="btnaprobar" style="display: none;">
						<td>
						</td>
						<td align="right">
						<span id="spanAprobar"><img id="imgAprobarLineasReq" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="enviarPagoNvo();"/> Aprobar</span>
						</td>
					</tr>
					
					<tr>
						<td width="25%" align="left">Unidad Ejecutora</td>
						<td align='left'><select name='cIdUnidadEjecutoraSl'
							id='cIdUnidadEjecutoraSl' style='width: 550px' onchange="cambiaCentrocontableUsuario();"></select></td>
							<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=usuarioNR.getU_UR()%>" />
					</tr>
					<tr >
						<td align="left">Tipo de Recepcion</td>
						<td align="left"><select name="cIdTipoSolicitud"
							id="cIdTipoSolicitud"></select>
						</td>
					</tr>
					<tr>
						<td align="left">Cap&iacute;tulo</td>
						<td align="left"><select name="cIdCapituloSl" id="cIdCapituloSl"
							style="width: 550px"></select>
							<input type="hidden" name="cIdCapitulo" id="cIdCapitulo" value="2" />
						</td>
					</tr>
					<tr>
						<td align="left">Partida</td>
						<td align="left"><select name="cIdSubPartida"
							id="cIdSubPartida" style="width: 550px" onChange="mostrarCucop();" ></select>
						</td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Mes</td>
						<td align="left"><select name="nIdPeriodo" id="nIdPeriodo">
								<option value="1">Enero</option>
								<option value="2">Febrero</option>
								<option value="3">Marzo</option>
								<option value="4">Abril</option>
								<option value="5">Mayo</option>
								<option value="6">Junio</option>
								<option value="7">Julio</option>
								<option value="8">Agosto</option>
								<option value="9">Septiembre</option>
								<option value="10">Octubre</option>
								<option value="11">Noviembre</option>
								<option value="12">Diciembre</option>

						</select></td>
					</tr>
					<tr>
						<td align="left">Alcance</td>
						<td align="left"><select name="nIdAlcance" id="nIdAlcance"></select>
						</td>
					</tr>
					<tr>
						<td align="left">Lugar de Entrega</td>
						<td align="left"><select name="cIdAlmacenEntrega"
							id="cIdAlmacenEntrega" style="width: 550px"></select>
						</td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Facturar A:</td>
						<td align="left"><input type="text" id="facturar"
							readonly="readonly" name="facturar"
							value="COMISIÓN NACIONAL FORESTAL" style="width: 550px; "
							onkeypress="textCounter(this,100);" /> <input type="hidden"
							id="cIdAlmacen" name="cIdAlmacen"
							value="<%=usuarioNR.getU_UR()%>" /> <!-- 				    		<select id="cIdAlmacen" name="cIdAlmacen" style="width: 550px;" > -->
							<!-- 				    		 <option value="<%=usuarioNR.getU_UR()%>" selected="selected">  -->
							<!-- 							</select></td> -->
					</tr>

					<tr style="display: none">
						<td align="left">Tipo de Procedimiento</td>
						<td align="left"><select name="nIdCategoria"
							id="nIdCategoria" style="width: 550px"></select>
						</td>
					</tr>
					<tr id="trFechaRequerida" style="display: none">
						<td align="left" colspan="2"><input type="text"
							style="width: 700px" id="rfvFechaRequerida"
							name="rfvFechaRequerida" readonly
							style="color: red; border-width:0; background-color:transparent"
							value="Necesita indicar una fecha válida. Selecciónela con el icono rojo de la derecha." />
						</td>
					</tr>
					<tr>
						<td align="left">Fecha De Emisión</td>
						<td align="left"><input type="text" id="fechaRequerida"
							name="fechaRequerida" />
						</td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Descripci&oacute;n</td>
						<!--  <td align="left"><textarea rows="6" cols="75"
								name="cDescripcion" id="cDescripcion"
								onkeypress="textCounter(this,255);"></textarea>
						</td>-->
					</tr >
					<tr id="trPlazo" style="display: none">
						<td colspan="2" align="left"><input type="text"
							style="width: 700px" id="rfvPlazo" name="rfvPlazo" readonly
							style="color: red; border-width:0; background-color:transparent"
							value="Necesita indicar un plazo de días naturales con un número entero." />
						</td>
					</tr>
					<tr style="display: none;">
						<td align="left">Plazo para formalización</td>
						<td align="left"><input type="text" id="nIdPlazo"
							name="nIdPlazo" value="30"
							onkeypress="return onlyIntegers(event);"
							onblur="numberValidatorOut(this);" />d&iacute;as naturales</td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Anexos</td>
						<td align="left"><input type="checkbox" id="chkAnexo"
							value="chkAnexo" /> Incluye anexos</td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Tipo de Garant&iacute;a</td>
						<td align="left"><select name="cTipoGarantia"
							id="cTipoGarantia" onchange="cambioTipoGarantia();"></select>
						</td>
					</tr>
					<tr id="trPorGarantia" style="display: none">
						<td align="left" colspan="2"><input type="text"
							style="width: 700px" id="rfvGarantia" name="rfvGarantia" readonly
							style="color: red; border-width:0; background-color:transparent"
							value="Necesita indicar un porcentaje para la garantía válido." />
						</td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Porcentaje de Garant&iacute;a</td>
						<td align="left"><select name="mPorcentajeGarantia"
							id="mPorcentajeGarantia" onchange="cambioTipoGarantiaId();"></select>

						</td>
					</tr  style="display: none;">
					<tr id="trImpPoliza" style="display: none">
						<td align="left" colspan="2"><input type="text"
							style="width: 700px" id="rfvImportePoliza"
							name="rfvImportePoliza" readonly
							style="color: red; border-width:0; background-color:transparent"
							value="Necesita indicar un porcentaje de póliza válido." />
						</td>
					</tr>
					<tr style="display: none;">
						<td align="left">Importe de la p&oacute;liza de la
							responsabilidad civil</td>
						<td align="left"><input type="text" id="mImportePoliza"
							name="mImportePoliza" value="$0.00"
							onblur="cambiafrmt(this.name);"
							onkeypress="return onlyIntegers(event);" />
					</tr>
					<tr  style="display: none;">
						<td align="left">Plurianualidad</td>
						<td align="left"><select name="cPlurianualidad"
							id="cPlurianualidad">
								<option value="No aplica">No aplica</option>
								<option value="Aplica">Aplica</option>
						</select></td>
					</tr>
					<tr  style="display: none;">
						<td align="left">Observaciones</td>
						<td align="left"><textarea rows="2" cols="50"
								name="cObservaciones" id="cObservaciones"
								onkeypress="textCounter(this,255);">Ninguna</textarea>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="button" name="btnGuardarNuevoReq" id="btnGuardarNuevoReq" value="Guardar" class="btnInterfaceBG"/>
						</td>
					</tr>
				</table>
	</fieldset>
			</div>
				<!-- Zona de hidden's -->
				<input type="hidden" name="cEjercicio" id="cEjercicio" /> <input
					type="hidden" name="nIdEstado" id="nIdEstado" value="1" /> <input
					type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" /> <input
					type="hidden" name="nIdEstadoPrecomprometido"
					id="nIdEstadoPrecomprometido" value="1" /> <input type="hidden"
					name="cIdEntidadContable" id="cIdEntidadContable"
					value="<%=usuarioNR.getU_Ramo()%>" /> <input type="hidden"
					name="lAnexos" id="lAnexos" value="0" />
					<!-- Lineas -->
					 <input type="hidden" name="cIdCABMIns" id="cIdCABMIns"/>
					 <input type="hidden" name="cadenaMesesCantidad" id="cadenaMesesCantidad" value=""/>
					 <input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA"/>
					 <input type="hidden" name="mPrecioUnitario" id="mPrecioUnitario"/>
					 <input type="hidden" name="mPrecioUnitarioPAAS" id="mPrecioUnitarioPAAS" value=""/>
					 <input type="hidden" name="mMontoNetoLinea" id="mMontoNetoLinea" value=""/>
					 <input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
					 <input type="hidden" name="cDescripcionAdicional" id="cDescripcionAdicional" value=""/>
					 <input type="hidden" name="cDescripcion" id="cDescripcion" value=""/>
					 <input type="hidden" name="min" id="min" />
					 <input type="hidden" name="nIdEstadoReq" id="nIdEstadoReq" value ="1"/>
					 <input type="hidden" name="nIdLineaSolicitud" id="nIdLineaSolicitud"/>
					 <input type="hidden" name="nCantidad" id="nCantidad"/>
					 <input type="hidden" name="nLineas" id="nLineas" />
					 <input type="hidden" name="mImporteNeto" id="mImporteNeto" />
					  <input type="hidden" name="montoSalarioMinimo" id="montoSalarioMinimo" />
					 
			<input type="hidden" name="max" id="max" />
			<input type="hidden" name="deshabilitado" id="deshabilitado"/>
				<!-- viene del checkbox -->
				<input type="hidden" name="fSolicitud" id="fSolicitud"
					value="1900-01-01 00:00:00.000" /> <input type="hidden"
					name="fCreacion" id="fCreacion" value="1900-01-01 00:00:00.000" />
				<input type="hidden" name="fAprobacion" id="fAprobacion"
					value="1900-01-01 00:00:00.000" /> <input type="hidden"
					name="cIdUsuarioCreacion" id="cIdUsuarioCreacion"
					value="<%=usuarioNR.getLogin()%>" /> <input type="hidden"
					name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="NULL" />
				<input type="hidden" name="fAnulacion" id="fAnulacion"
					value="1900-01-01 00:00:00.000" /> <input type="hidden"
					name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="NULL" />
				<input type="hidden" name="cIdFuenteFinanciamiento"
					id="cIdFuenteFinanciamiento" value="FF" /> <input type="hidden"
					name="fRequerida" id="fRequerida" value="1900-01-01 00:00:00.000" />
				<input type="hidden" name="cIdUnidadEjecutoraUsuario"
					id="cIdUnidadEjecutoraUsuario" value="<%=usuarioNR.getU_UR()%>" />
				<input type="hidden" name="isAdmin" id="isAdmin" value="1" /> <input
					type="hidden" name="cIdUsuario" id="cIdUsuario"
					value="<%=usuarioNR.getLogin()%>" /> <input type="hidden"
					name="modulo" id="modulo" value="MATERIALES" /> <input
					type="hidden" name="capitulos" id="capitulos" value="2,5" /> <input
					type="hidden" name="nId" id="nId" value="1" /> <input
					type="hidden" name="cIdPorcentaje" id="cIdPorcentaje" value="" />
				<input type="hidden" name="CENTROCONTABLE" id="CENTROCONTABLE"
					value="<%=usuarioNR.getPropiedad("CCENTROCONTABLE").getValor()%>" />
				<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
				<input type="hidden" name="cAccion" id="cAccion"/>
			<div  id="CUCOPSDIV"    >
				<fieldset >
  				<legend>CUCOPS Calendarizados en el PAAS</legend>
				<table align="left">
					<tr id="trCABM" style="display:none">
		    			<td align="left"><textarea style="width: 700px" id="rfvCAMB" name="rfvCAMB" readonly style="color: red; border-width:0; background-color:transparent; overflow: auto;" >No existe ningun CUCOP en el Programa Anual que cumpla los requisitos de la requisici&oacute;n o no cuenta con suficiencia en el periodo</textarea>
		    			</td>
		    		</tr>
		    		<tr align="left">
		    			<td align="left"  >
<!-- 		    			<select style="width:700px" id="cIdCABM" name="cIdCABM" ></select> -->
		    			<input name="cIdCABM" id="cIdCABM" type="hidden" value=""/>
		    			</td>
		    		</tr>
		    		<tr align="left" style="display: none;">
		    			<td align="left"><img id="imgRefresh" src="../../imagenes/icono_refresh.jpg" style="cursor: pointer" onclick="oTableCAMBs.fnClearTable(oTableCAMBs); oTableCucop.fnClearTable(oTableCucop); mostrarCucop();"/> Actualizar </td>
		    		</tr>
					
				</table>
				<table id="tblCucop" class="display" >
					<thead>
						<tr>
							<th align="center" style="width: 60px; ">CUCOP</th>
							<th align="center" >Descripción</th>
							<th align="center" style="width: 80px;">CantidadDisp_PAAS</th>
						</tr>
					</thead>
				</table>
				<br />
				<table id="tblRequisicionMeses" class="display" style="display: none;">
					<thead >
						<tr>
							<th align="center">&nbsp;&nbsp;CUCOP&nbsp;&nbsp;</th>
							<th align="center" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Descripci&oacute;n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							<th align="center" >ENERO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center">FEBRERO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >MARZO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >ABRIL<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >MAYO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >JUNIO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >JULIO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >AGOSTO<br />Mon. Disp. / Can. Disp.</th>
							<th align="center">SEPTIEMBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" >OCTUBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center">NOVIEMBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" style="width: 300px">DICIEMBRE<br />Mon. Disp. / Can. Disp.</th>
							<th align="center" style="display: none;"></th>
						</tr>															
					</thead>
					
				</table>
				<br />
				<table id="tblRequisicionMeses2" class="display">
					<thead >
						<tr>
							<th align="center" style="display: none;"></th>
							<th align="center" >Mes</th>
							<th align="center" >Cucop</th>
							<th align="center">Cantidad <br />Disponible </th>
							<th align="center" >Monto <br />Disponible </th>
							<th align="center"  style="display: none;">nPorcentaje IVA</th>
							<th align="center" style="display: none;">Descripci&oacute;n</th>
							<th align="center" >Cantidad</th>
							<th align="center" style="display: none;"></th>
						</tr>															
					</thead>
				</table>
					<input type="button" id="AgregarLineasAgrup" name="AgregarLineasAgrup" value="Agregar" title="Agrega CUCOP" onclick="agregarLineasAgrupadas()" class="btnInterfaceBG"/>
				</fieldset>
				
				
				<fieldset >
  				<legend>Agregar CUCOPS a la Recepcion Directa</legend>
				<table width="90%" align="left">
   						<tr>
    						<td align="left">Monto bruto de la Recepcion Directa:<input type="text" style="width: 700px" id="montoBruto" name="montoBruto" readonly style="border-width:0; background-color:transparent"/></td>
    					</tr>
    					<tr>
    						<td align="left">Monto neto de la Recepcion Directa:<input type="text" style="width: 700px" id="montoNeto" name="montoNeto" readonly style="border-width:0; background-color:transparent"/></td>
    					</tr>
    					
   					</table>
   					<table id="tblLineas" class="display" width="90%">
   							<thead>
				        		<tr>
				        			<th style="display: none;"></th>
				        			<th>#</th>
				        			<th>CUCOP</th>
				        			<th style="display: none;">MES</th>
				        			<th >Descripci&oacute;n</th>
				        			<th style="width: 300px">Descripci&oacute;n Adicional</th>
				        			<th>Cantidad</th>
				        			<th>Precio<br/>Unitario</th>
				        			<th>P.U<br/>Promedio PAAS</th>
				        			<th>IVA</th>
				        			<th>Importe<br />Neto</th>
				        			<th>Unidad de<br />Medida</th>
				        			<th style="display: none;">Cantidad<br/>Disponible</th>
				        			<th style="display: none;">Monto<br />Disponible<br/>PAAS</th>
				        		</tr>
				        	</thead>
   						</table>
   						<table>
	    					<tr>
	    						<td align="left">
		    						<input type="button" id="btnGuardarLineasReq" name="btnGuardarLineasReq" value="Guardar" class="btnInterfaceBG"/>
		    						<input type="button" id="btnEliminarLineasReq" name="btnEliminarLineasReq" value="Eliminar Lineas" class="btnInterfaceBG"/>
	    						</td>
	    					</tr>
   						</table>
   					</fieldset>
				</div>
	</form>
</body>
</html>
