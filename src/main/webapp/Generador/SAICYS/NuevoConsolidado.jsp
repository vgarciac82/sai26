<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map rol =usuario.getRoles();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consolidado</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
			var roles="";
			$(document).ready(function() {
				$("#tbs").val(1);
				<%
				NegativaPestana NegPestana=new NegativaPestana();
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
				Map botones=nb.getBotones(roles,"Consolidado","NuevoConsolidado");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
				roles="<%=roles%>";
				queryFormPost("ejercicioFiscalConsolidadoRead",{async:false});
				$("#btnGuardarConsolidado").button().click(function() {
					queryFormPost("ejercicioFiscalConsolidadoRead",{async:false});
					//Aqui poner el crud que traiga
					$("#unidadEjecutoraCA").val($("#cunidadRMC").val());
					queryFormPost("readCCentroContableUE",{async : false,
						callback : function() 
						{
							$("#cIdEntidadContable").val($("#cCentroContable").val());
						}
					
					});
					
					$("#cIdTipoConsolidado").val($('#ctipoConsolidadoC option:selected').val());
					$("#nIdAlcance").val($('#cAlcanceC option:selected').val());
					
					if($("#nIdAlcance").val() == 0){
						swal("Seleccionar un alcance.",{icon:"info",button: "Cerrar"});
						return;
					}
					//FABIAN
					if (!($("#cIdTipoConsolidado").val() == 'CS' || $("#cIdTipoConsolidado").val() == 'CN') && $("#nIdAlcance").val() == 4){
						swal("El alcance DELEGADA solo se aplica para consolidados de Servicio o de Servicio de Regularización",{icon:"info",button: "Cerrar"});
						return;
					}
					//FABIAN
					$("#cIdUnidadEjecutora").val($('#cunidadRMC').val());
					$("#cDescripcion").val($('#cDescripcionC').val());
					$("#cNotas").val($('#cNotasC').val());
					var des = $('#cDescripcionC');
					$("#nIdEstado").val('1');
					
								
						//Validación de los campos							
						var bValid = true;
						allFields = $( []).add(des),
						tips = $(".validateTips");
						tips.text("");
						allFields.removeClass("ui-state-error");
						
						bValid = bValid&& checkRequerido(des, "Descripcion");
						if (bValid) {
							queryFormPost("ConsecutivoConsolidadoRead",{async : false});	
							queryFormPost("ConsolidadoCreate",{async : false});
							window.location = "Consolidado.jsp?tab=3&cEjercicio=" + $("#cEjercicio").val() + "&cIdTipoConsolidado=" + $("#cIdTipoConsolidado").val() + "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val() + "&nIdConsecutivo=" + $("#nIdConsecutivo").val();
						}	
				});
				
				$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
				$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
				
				queryFormPost("ejercicioFiscalConsolidadoRead", {async : false});
				querySelectPost("TipoConsolidadoRead", "ctipoConsolidadoC", {async : false});
				eliminaOpcionTipoConsolidado();					
				if (roles.indexOf("ADMIN_RECMAT") >= 0){
					$("#isAdmin").val(0);
				}
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cunidadRMC", {async: false});
				//querySelectPost("UnidadBusca","cunidadRMC",{async : true});
				document.getElementById("unidadEjecutoraOC").style.display= 'block';
				document.getElementById("unidadEjecutoraLocal").style.display= 'none';
				//querySelectPost("AlcanceAdminRead", "cAlcanceC", {async : false});
				//UE'S Estatales
				
				if('<%=usuario.getU_UR().trim()%>'   == 'G01'  ||'<%=usuario.getU_UR().trim()%>' == 'G15'  ||'<%=usuario.getU_UR().trim()%>' == 'G28'
					||'<%=usuario.getU_UR().trim()%>' == 'G02' ||'<%=usuario.getU_UR().trim()%>' == 'G16'  ||'<%=usuario.getU_UR().trim()%>' == 'G29'
					||'<%=usuario.getU_UR().trim()%>' == 'G03' ||'<%=usuario.getU_UR().trim()%>' == 'G17'  ||'<%=usuario.getU_UR().trim()%>' == 'G30'				
					||'<%=usuario.getU_UR().trim()%>' == 'G04' ||'<%=usuario.getU_UR().trim()%>' == 'G18'  ||'<%=usuario.getU_UR().trim()%>' == 'G31'
					||'<%=usuario.getU_UR().trim()%>' == 'G05' ||'<%=usuario.getU_UR().trim()%>' == 'G19'  ||'<%=usuario.getU_UR().trim()%>' == 'G32'
					||'<%=usuario.getU_UR().trim()%>' == 'G06' ||'<%=usuario.getU_UR().trim()%>' == 'G20'
					||'<%=usuario.getU_UR().trim()%>' == 'G07' ||'<%=usuario.getU_UR().trim()%>' == 'G21'
					||'<%=usuario.getU_UR().trim()%>' == 'G08' ||'<%=usuario.getU_UR().trim()%>' == 'G22'
					||'<%=usuario.getU_UR().trim()%>' == 'G09' ||'<%=usuario.getU_UR().trim()%>' == 'G23'
					||'<%=usuario.getU_UR().trim()%>' == 'G10' ||'<%=usuario.getU_UR().trim()%>' == 'G24'
					||'<%=usuario.getU_UR().trim()%>' == 'G11' ||'<%=usuario.getU_UR().trim()%>' == 'G25'
					||'<%=usuario.getU_UR().trim()%>' == 'G12' ||'<%=usuario.getU_UR().trim()%>' == 'G26'
					||'<%=usuario.getU_UR().trim()%>' == 'G13' ||'<%=usuario.getU_UR().trim()%>' == 'G27'
					||'<%=usuario.getU_UR().trim()%>' == 'G14'){
						querySelectPost("mAlcanceRead", "cAlcanceC", {async : false});
					}
					else {//Centrales
						querySelectPost("mAlcanceAdminRead","cAlcanceC",{async : false});
					}

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

			function eliminaOpcionTipoConsolidado(){
				//elimina la opcion del combo tipo procedimiento FONDEN
				var objTipoProc = document.getElementById("ctipoConsolidadoC");
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
			function cambiaCentrocontableUsuario(){
				$.ajax({
					url: '../../servlet/CambiaPropiedadesUsuario',
					dataType: 'json',
					data: {"UnidadEjecutora" : $("#cunidadRMC").val()},
					async : false,
					success : function(j) {
						if(j[0].error){
							swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"warning",button: "Cerrar"});
						}else{
							$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
							$("#cIdEntidadContable").val(j[0].centroContable);
							if(j[0].unidadEjecutora.indexOf("G")>=0){
								querySelectPost("mAlcanceRead", "cAlcanceC", {async : false});	
							}else{
								querySelectPost("mAlcanceAdminRead","cAlcanceC",{async : false});
								$("#cAlcanceC").val(1);
							}
						}
						
					}
				});
			}
		</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Nuevo Consolidado</legend>
				<table border="0" width="100%">
					<tr>
						<td>
							
							<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
							<input id="cNotas" name="cNotas" type="hidden" size="255">							
							
							<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
							<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
							<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
							<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
						    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
						    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
						    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
						    <input type="hidden" name="unidadEjecutoraCA" id="unidadEjecutoraCA" value="" />
						    <input type="hidden" name="cCentroContable" id="cCentroContable" value="" />
						    
						    								
						</td>
					</tr>

					<tr>
						<td>Unidad Ejecutora:</td>
						<td align="left">
						<div id="unidadEjecutoraOC" style="display: block">
								<table width="100%">
									<tr>
										<td align="left">
											<select id="cunidadRMC" name="cunidadRMC" style="width: 45em;" onchange="cambiaCentrocontableUsuario();">
												<option value="<%=usuario.getU_UR()%>" selected="selected" ></option>
											</select>
										</td>
									</tr>
								</table>
							</div>
							<div id="unidadEjecutoraLocal" style="display: none">
								<table>
									<tr>
										<td align="left"><input name="unidadEjecutoraLocalNuevo" id="unidadEjecutoraLocalNuevo" style="border: 0px solid black;text-align: left" size="80"></td>
									</tr>
								</table>
							</div>
						
						
						</td>
					</tr>
					<tr>
						<td>Tipo Consolidado:</td>
						<td align="left"><select id="ctipoConsolidadoC" name="ctipoConsolidadoC" style="width: 45em;"></select></td>
					</tr>
					<tr>
						<td>Alcance:</td>
						<td align="left"><select id="cAlcanceC" name="cAlcanceC" style="width: 45em;"></select></td>
					</tr>
					<tr>
						<td>Descripci&oacute;n:</td>
						<td align="left"><textarea name="cDescripcionC" rows="6" cols="72" id="cDescripcionC" onkeypress="textCounter(this,2000);" ></textarea></td>
					</tr>
					<tr>
						<td>Notas:</td>
						<td align="left"><textarea name="cNotasC" rows="2" cols="72" id="cNotasC" onkeypress="textCounter(this,255);return validar(event)" ></textarea></td>
					</tr>
					<tr style="display: none;">
						<td colspan="2" align="center"><label class="validateTips ui-state-error"></label></td>
					</tr>
					<tr>
						<td colspan="2" align="center"> <input type="button" name="btnGuardarConsolidado" id="btnGuardarConsolidado" value="Guardar"  class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
					</tr>						
				</table>
			</fieldset>
		</form>				
	</body>
</html>
