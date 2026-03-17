<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*"%>
<%
	Usuario usuarioNR = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioNR == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String name_user = usuarioNR.getLogin();
	Map rol = usuarioNR.getRoles();
	
	Calendar c1 = Calendar.getInstance(); // today
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	System.out.println(usuarioNR.getPropiedad("CCENTROCONTABLE").getValor());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>Nueva Requisici&oacute;n</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">


<script type="text/javascript" charset="utf-8">
		var roles="";
		$(document).ready(function() {
			$("#tbs").val(1);
			showHideTabs();
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
				querySelectPost("tCatalogoUnidadEjecutoraRead", "cIdUnidadEjecutora", {async: false});
			} 				
			else{
				//query para las vistas de los usuarios
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			}
			querySelectPost("mCatalogoTipoSolicitudCMBRead", "cIdTipoSolicitud", {async: false});
			querySelectPost("mCatalogoCapituloCMBRead", "cIdCapitulo", {async: false});
			querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
			querySelectPost("mCatalogoAlcanceRead", "nIdAlcance", {async: false});
			if ($("#CENTROCONTABLE").val()== "10"){
				$("#nIdAlcance").val(1);
			}else{
				$("#nIdAlcance").val(2);
			}
			//querySelectPost("mCatalogoAlmacenReadSimca", "cIdAlmacenEntrega", {async: false});
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
					if($("#cIdTipoSolicitud").val()=='RC' || $("#cIdTipoSolicitud").val()=='RR'){
						$("#capitulos").val('1,2,4,5');
					}
					else if($("#cIdTipoSolicitud").val()=='RO'){
						$("#capitulos").val('6');
					}
					else if($("#cIdTipoSolicitud").val()=='RM' ){
						$("#capitulos").val('1,2,3,4,5');
					}
					else if($("#cIdTipoSolicitud").val()=='RT'){
						$("#capitulos").val('2');
					}					
					else if($("#cIdTipoSolicitud").val()=='RS' || $("#cIdTipoSolicitud").val()=='RN'){
						$("#capitulos").val('1,3,4');
					}
					querySelectPost("mCatalogoCapituloCMBRead", "cIdCapitulo", {async: false});
					querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
				});
			$( "#cIdCapitulo" )
				.change(function() {
					querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
				});
			$("#chkAnexo").change(function() {
				if ($("#lAnexos").val() == "0")
					$("#lAnexos").val("1");
				else
					$("#lAnexos").val("0");
			});	
			//selección de valores por default en los dropdownlist
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
			if (roles.indexOf("ADMIN_RECMAT") >= 0)
				queryFormPost("usuario_UnidadEjecutoraRead", {async: false});
			 $("#btnGuardarNuevoReq").button().click(function(){
			 	 guardarRequisicion ();
			 });
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
					queryFormPost("mSolicitudCreate", {async: false});
					//Bitácora
					$("#cAccion").val("CREA_REQUISICIÓN");
					//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					window.location = "Requisiciones.jsp?tab=3&cEjercicio=" + $("#cEjercicio").val() + "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val() + "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val() + "&nIdConsecutivo=" + $("#nIdConsecutivo").val();
			 }
			 else{
				 swal("Favor de revisar los campos antes de continuar.",{icon:"info",button: "Cerrar"});
			 }
		};
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
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
		}
		function cambiaCentrocontableUsuario(){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"info",button: "Cerrar"});
					}else{
						$("#cUnidadEjecutora").val(j[0].unidadEjecutora);
						$("#CENTROCONTABLE").val(j[0].centroContable);
					}
				}
			});
		}
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0"
	topmargin="0">
	<form>
		<fieldset>
			<legend>Nueva Requisici&oacute;n</legend>
			<div id="container" class="container" style="width: 90%;">
				<table align="left" cellpadding="2" width="90%">
					<tr>
						<td><input type="hidden" name="U_LOGIN" id="U_LOGIN"
							value="<%=usuarioNR.getLogin()%>" /> <input type="hidden"
							name="R_NOMBRE" id="R_NOMBRE" /> <input type="hidden"
							name="cUnidadEjecutora" id="cUnidadEjecutora"
							value="<%=usuarioNR.getU_UR()%>" /></td>
					</tr>
					<tr>
						<td width="25%" align="left">Unidad Ejecutora</td>
						<td align='left'><select name='cIdUnidadEjecutora'
							id='cIdUnidadEjecutora' style='width: 550px' onchange="cambiaCentrocontableUsuario();"></select></td>
					</tr>
					<tr>
						<td align="left">Tipo de requisici&oacute;n</td>
						<td align="left"><select name="cIdTipoSolicitud"
							id="cIdTipoSolicitud"></select>
						</td>
					</tr>
					<tr>
						<td align="left">Cap&iacute;tulo</td>
						<td align="left"><select name="cIdCapitulo" id="cIdCapitulo"
							style="width: 550px"></select>
						</td>
					</tr>
					<tr>
						<td align="left">Partida</td>
						<td align="left"><select name="cIdSubPartida"
							id="cIdSubPartida" style="width: 550px"></select>
						</td>
					</tr>
					<tr>
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
					<tr>
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
					<tr>
						<td align="left">Descripci&oacute;n</td>
						<td align="left"><textarea rows="6" cols="75"
								name="cDescripcion" id="cDescripcion"
								onkeypress="textCounter(this,255);"></textarea>
						</td>
					</tr>
					<tr id="trPlazo" style="display: none">
						<td colspan="2" align="left"><input type="text"
							style="width: 700px" id="rfvPlazo" name="rfvPlazo" readonly
							style="color: red; border-width:0; background-color:transparent"
							value="Necesita indicar un plazo de días naturales con un número entero." />
						</td>
					</tr>
					<tr>
						<td align="left">Plazo para formalización</td>
						<td align="left"><input type="text" id="nIdPlazo"
							name="nIdPlazo" value="30"
							onkeypress="return onlyIntegers(event);"
							onblur="numberValidatorOut(this);" />d&iacute;as naturales</td>
					</tr>
					<tr>
						<td align="left">Anexos</td>
						<td align="left"><input type="checkbox" id="chkAnexo"
							value="chkAnexo" /> Incluye anexos</td>
					</tr>
					<tr>
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
					<tr>
						<td align="left">Porcentaje de Garant&iacute;a</td>
						<td align="left"><select name="mPorcentajeGarantia"
							id="mPorcentajeGarantia" onchange="cambioTipoGarantiaId();"></select>

						</td>
					</tr>
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
					<tr>
						<td align="left">Plurianualidad</td>
						<td align="left"><select name="cPlurianualidad"
							id="cPlurianualidad">
								<option value="No aplica">No aplica</option>
								<option value="Aplica">Aplica</option>
						</select></td>
					</tr>
					<tr>
						<td align="left">Observaciones</td>
						<td align="left"><textarea rows="2" cols="50"
								name="cObservaciones" id="cObservaciones"
								onkeypress="textCounter(this,255);">Ninguna</textarea>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="button" id="btnGuardarNuevoReq" name="btnGuardarNuevoReq" value="Guardar" class="btnInterfaceBG"/>
						</td>
					</tr>
				</table>

				<!-- Zona de hidden's -->
				<input type="hidden" name="cEjercicio" id="cEjercicio" /> <input
					type="hidden" name="nIdEstado" id="nIdEstado" value="1" /> <input
					type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" /> <input
					type="hidden" name="nIdEstadoPrecomprometido"
					id="nIdEstadoPrecomprometido" value="1" /> <input type="hidden"
					name="cIdEntidadContable" id="cIdEntidadContable"
					value="<%=usuarioNR.getU_Ramo()%>" /> <input type="hidden"
					name="lAnexos" id="lAnexos" value="0" />
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
					type="hidden" name="capitulos" id="capitulos" value="1,2,4,5" /> <input
					type="hidden" name="nId" id="nId" value="1" /> <input
					type="hidden" name="cIdPorcentaje" id="cIdPorcentaje" value="" />
				<input type="hidden" name="CENTROCONTABLE" id="CENTROCONTABLE"
					value="<%=usuarioNR.getPropiedad("CCENTROCONTABLE").getValor()%>" />
				<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
				<input type="hidden" name="cAccion" id="cAccion"/>
			</div>
		</fieldset>
	</form>
</body>
</html>
