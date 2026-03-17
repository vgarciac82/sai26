<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map rol =usuario.getRoles();
    int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	String today = sdf.format(c1.getTime());
    
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
				$("#tbs").val(0);
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
				Map botones=nb.getBotones(roles,"Consolidado","NuevoAutomaticoConsolidado");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>			

				roles="<%=roles%>";
				queryFormPost("ejercicioFiscalConsolidadoRead", {async : false});
				querySelectPost("TipoConsolidadoAutomaticoRead","tipoConsolidadoCA",{async : false});	
					
				//// tipo de poliza
				queryFormPost("TipoPolizaRead", {async: false});			
				eliminaOpcionTipoConsolidado();
				
				$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
				//Si es unidad responsable
				
					if (roles.indexOf("ADMIN_RECMAT") >= 0){
						$("#isAdmin").val(0);
					}
					querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "unidadEjecutoraCA", {async: false});
					//querySelectPost("consolidadoAutomaticoUnidadEjecutoraRead","unidadEjecutoraCA",{async : true});
					document.getElementById("unidadEjecutoraOC").style.display= 'block';
					document.getElementById("unidadEjecutoraLocal").style.display= 'none';	
					//querySelectPost("AlcanceconsolidadoAutomaticoAdminRead","AlcanceCA",{async : true});
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
							querySelectPost("mAlcanceRead", "AlcanceCA", {async : false});
						}
						else {//Centrales
							querySelectPost("mAlcanceAdminRead","AlcanceCA",{async : false});
						}
				
				
				$("#btnGuardarConsolidadoAutomatico").button().click(function(){
					if (!($("#tipoConsolidadoCA").val() == 'CS' || $("#tipoConsolidadoCA").val() == 'CN') && $("#AlcanceCA").val() == 4){
						swal("El alcance DELEGADA solo se aplica para consolidados de Servicio o de Servicio de Regularización.",{icon:"info",button: "Cerrar"});
						return;
					}
					//FABIAN
					queryFormPost("ejercicioFiscalConsolidadoRead", {async : false});
					queryFormPost("vigenciaPrecompromisoMateriales", {async : false});
					var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');									
					$("#cIdTipoSolicitudCA").val(cadena_campos[0]);
					$("#cIdUnidadEjecutoraSolicitudCA").val(cadena_campos[1]);
					$("#nIdConsecutivoSolicitudCA").val(cadena_campos[2]);
				    var des=$("#cDescripcionCA").val();
					des=des.toUpperCase();
					$("#cDescripcionCA").val(des);
					$("#cIdSolicitud").val($("#cIdTipoSolicitudCA").val()+'-'+$("#cIdUnidadEjecutoraSolicitudCA").val()+'-'+$("#nIdConsecutivoSolicitudCA").val());
					queryFormPost("consolidadoAutomatico",{async:false});
					
					if($("#documentoAplicado").val()=='S'){ // la solicitud trae apartado, se aplica el precompromiso
						//generar el consolidado encodeURIComponent(uri)
						$.ajax({url: '../../servlet/ConsolidadoServlet?cEjercicio='+$("#cEjercicio").val()+"&cIdTipoConsolidado="+$("#tipoConsolidadoCA").val()
							+"&cIdUnidadEjecutora="+$("#unidadEjecutoraCA").val()+"&cDescripcion="+encodeURIComponent($("#cDescripcionCA").val())+"&Alcance="+$("#AlcanceCA").val()
							+"&cIdTipoSolicitud="+$("#cIdTipoSolicitudCA").val()+"&cIdUnidadEjecutoraSolicitud="+$("#cIdUnidadEjecutoraSolicitudCA").val()
							+"&nIdConsecutivoSolicitud="+$("#nIdConsecutivoSolicitudCA").val()+"&cNotas="+$("#cNotasCA").val(),
				 			type:'post' , async: false,data:'operacion=5', dataType: 'json', success: 
								function(j){
									var respuesta=j[0].respuesta;
									if(respuesta=="0"){
										swal(j[0].mensaje,{icon:"info",button: "Cerrar"});
										return;
									}
									cIdNuevoConsolidado=j[0].Contable1;
									if(cIdNuevoConsolidado=="ERROR INESPERADO"){
										swal("ERROR INESPERADO",{icon:"error",button: "Cerrar"});
										return;
									}
									
									//Genera un nuevo folio en caso de que no lo tenga
										$("#cIdConsolidado").val(cIdNuevoConsolidado);
										if($("#nFolioPreCompromiso").val()==0){
											$.ajax({url: '../../servlet/ConsolidadoServlet' , type:'post' , async: false, data:'operacion=1', dataType: 'json', success: guardaFolio});
										}
										if($("#nFolioPreCompromiso").val()==0)
											return -1;
											
										
										//Guarda en una tabla auxiliar el encabezado del compromiso
										getNextSequenceVal({seqName: "PR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
										
										var nMes = "<%=today%>";
										var nMes = nMes.substring(5, 7 ) ;
									
										$("#fCarga").val( "<%=today%>" ); 
										$("#fAplicacion").val("<%=today%>" ); 
										$("#cIdContrato").val(cIdNuevoConsolidado); // id del consolidado
										$("#cTipoContrato").val( "DI" ); 
										$("#cRamo").val( "<%=cRamo%>" );
										$("#cUnidadResponsable").val($("#unidadEjecutoraCA").val()); //Cambiar centrocontable
										$("#caNoPreCompromiso").val( vcaNoCompromiso );
										$("#nEnviadoSICOP").val("0");
										$("#nMes").val( nMes ); 
										$("#fVigencia").val( $("#vigenciaPrecomMateriales").val());
										queryFormPost('tPreCompromisoEncabezadoCreate', {async: false });
										queryFormPost('tPreCompromisoMaterialesDetalle', {async: false });
										
										// aplicacion contable
										$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&partidas=1", type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
											function(j){
												mensajeAp=j[0].Contable1;
												swal(mensajeAp,{icon:"info",button: "Cerrar"});
												swal({
													title: "¿Se ha creado exitosamente el consolidado "+$("#cIdConsolidado").val()+" desea crear un procedimiento?",
													text: "",
													icon: "info",
													buttons: {
														confirm : "Aceptar",
														cancel: "Cancelar"
													},
													}).then((continuar) => {
														if (!continuar) {
															window.location="Consolidado.jsp?tab=2";
														}else{
															var pagina="Procedimiento-copia.jsp?tab=0";
															location.href=pagina;		
														}
												});
											}
										});
								}
						});	
					}else{ // no hay apartado
						queryFormPost("pa_mConsolidadoAutomaticoCreate",{async : false});
						swal({
							title: "¿Se ha creado exitosamente el consolidado desea crear un procedimiento?",
							text: "",
							icon: "info",
							buttons: {
								confirm : "Aceptar",
								cancel: "Cancelar"
								},
							}).then((continuar) => {
								if (!continuar) {
									window.location="Consolidado.jsp?tab=2";
								}else{
									var pagina="Procedimiento-copia.jsp?tab=0";
									location.href=pagina;
								}
						});
					}
    			});
				
				$("#tipoConsolidadoCA").change(function () {					
					querySelectPost("fn_mConsolidadoAutomaticoSolicitudesDisponiblesRead","cIdSolicitudCA",{async : false});
					var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');						
					$("#cDescripcionCA").val(cadena_campos[3]);
				});
				
				$("#AlcanceCA").change(function () {					
					querySelectPost("fn_mConsolidadoAutomaticoSolicitudesDisponiblesRead","cIdSolicitudCA",{async : false});
					var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');						
					$("#cDescripcionCA").val(cadena_campos[3]);
				});		
				
				
				$("#cIdSolicitudCA").change(function () {															  				
					var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');
					$("#cIdTipoSolicitudCA").val(cadena_campos[0]);
					$("#cIdUnidadEjecutoraSolicitudCA").val(cadena_campos[1]);
					$("#nIdConsecutivoSolicitudCA").val(cadena_campos[2]);
					//$("#cDescripcionCA").val(cadena_campos[3]);
				    //para que se presente toda la descripcion en el text area					
					if(parseInt(cadena_campos.length,10) > 4){
						var aux=cadena_campos[3]+' ';
						
						for(var i=4; i<cadena_campos.length;i++){
							aux+=cadena_campos[i]+' ';
							$("#cDescripcionCA").val(aux);
						}
						
					     }else
						$("#cDescripcionCA").val(cadena_campos[3]);
							
								
					
				});
			});
			
			
			function textCounter( field, maxlimit ) {
				if ( field.value.length > maxlimit )
					field.value = field.value.substring( 0, maxlimit );
			}
			
			   
		    function guardaFolio(j){
				var folioPre=-1;
				var folioCaso=-1;
		    	folioPre=j[0].Folio1;
		    	folioCaso=j[0].Folio2;
		        if(folioPre==-1){
		      		swal("Ha ocurrido un error al crear el caso, contacte a su soporte.",{icon:"info",button: "Cerrar"});
		      		return -1;
		        }else{
		     	   $("#nFolioPreCompromiso").val(folioPre);
		     	   $("#folioCasoPreCompromiso").val(folioCaso);
		     	   nFolioPreCompromiso= $("#nFolioPreCompromiso").val();
		     	   
		     	}
			}
			
			function setSequenceVal(seqValue) {
				seqValue = "000000" + seqValue;
				seqValue = seqValue.substr(seqValue.length - 6);
				vcaNoCompromiso = $("#cCentroContable").val() + "PR" + $("#cEjercicio").val() + seqValue;
				
			}

			function validar(e) {
				tecla = (document.all) ? e.keyCode : e.which;
				if (tecla==8) return true;
				patron =/[\w\d\s\\.\/\_\ñ\Ñ]/;					
				te = String.fromCharCode(tecla);																				
				var v = document.getElementById("cDescripcionCA").value.replace("ñ", "n").replace("Ñ", "N");					
				document.getElementById("cDescripcionCA").value = v;
				return patron.test(te);
			}
			
			function validarCampoDescp(a){
				var v = document.getElementById('cDescripcionCA').value.replace("ñ", "n").replace("Ñ", "N");					
					document.getElementById('cDescripcionCA').value = v;				
			}
			
			function eliminaOpcionTipoConsolidado(){
				//elimina la opcion del combo tipo procedimiento FONDEN
				var objTipoProc = document.getElementById("tipoConsolidadoCA");
				for(var l=0;l<objTipoProc.options.length;l++){
					if(objTipoProc.options[l].text.toString().toUpperCase() == "DE FONDEN"){
						objTipoProc.options[l]=null;
					}
				}
				//elimina la opcion del combo tipo procedimiento CAPITULO 1000
				for(var l=0;l<objTipoProc.options.length;l++){
					if(objTipoProc.options[l].text.toString().toUpperCase() == "DE CAPITULO1000"){
						objTipoProc.options[l]=null;
					}
				}
			}
			function cambiaCentrocontableUsuario(){
				$.ajax({
					url: '../../servlet/CambiaPropiedadesUsuario',
					dataType: 'json',
					data: {"UnidadEjecutora" : $("#unidadEjecutoraCA").val()},
					async : false,
					success : function(j) {
						if(j[0].error){
							swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"info",button: "Cerrar"});
						}else{
							$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
							$("#cCentroContable").val(j[0].centroContable);
							if(j[0].unidadEjecutora.indexOf("G")>=0){
								querySelectPost("mAlcanceRead", "AlcanceCA", {async : false});	
							}else{
								querySelectPost("mAlcanceAdminRead","AlcanceCA",{async : false});
								$("#AlcanceCA").val(1);
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
				<legend>Nuevo Consolidado Autom&aacute;tico</legend>
				<table border="0" width="100%">
					<tr>
						<td>
							<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4">
							<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3">
							
							<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
							<input id="cNotas" name="cNotas" type="hidden" size="255">
							<input id="nIdEstado" name="nIdEstado" type="hidden" size="2">
							<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
							<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
							<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
							<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4">							
							<input id="cIdTipoSolicitudCA" name="cIdTipoSolicitudCA" type="hidden" size="3">
							<input id="cIdUnidadEjecutoraSolicitudCA" name="cIdUnidadEjecutoraSolicitudCA" type="hidden" size="3">
							<input id="nIdConsecutivoSolicitudCA" name="nIdConsecutivoSolicitudCA" type="hidden">
							
							<!-- Precompromiso -->
							<input type="hidden" id="nFolioPreCompromiso" name="nFolioPreCompromiso" value="0">
							<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>">
							<input type="hidden" id="fCarga" name="fCarga">
							<input type="hidden" id="fAplicacion" name="fAplicacion">
							<input type="hidden" id="cIdContrato" name="cIdContrato">
							<input type="hidden" id="cTipoContrato" name="cTipoContrato">
							<input type="hidden" id="cRamo" name="cRamo" value="16">
							<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable">
							<input type="hidden" id="caNoPreCompromiso" name="caNoPreCompromiso">
							<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP">
							<input type="hidden" id="nMes" name="nMes">
							<input type="hidden" id="fVigencia" name="fVigencia">
							<input type="hidden" id="vigenciaPrecomMateriales" name="vigenciaPrecomMateriales">
							
							<input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMMATERIALES">
							<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
							<input type="hidden" id="folioCasoPreCompromiso" name="folioCasoPreCompromiso" value="">
							<input type="hidden" name="mesDisponible" id="mesDisponible" value="<%=mesActual%>" />
											
							<input type="hidden" id="cIdConsolidado" name="cIdConsolidado">
							<input type="hidden" id="cIdSolicitud" name="cIdSolicitud">
							<input type="hidden" id="documentoAplicado" name="documentoAplicado">
							
							 <input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
						    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
						    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
						    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
						
						</td>
					</tr>
					<tr>
						<td align="left" colspan="3">
							*El consolidado autom&aacute;tico crea un consolidado a partir de una sola requisici&oacute;n y 
							se aprueba autom&aacute;ticamente.<br>
							*Crea una partida por cada l&iacute;nea de requisici&oacute;n, 
							es &uacute;til en la elaboraci&oacute;n de consolidados de una sola l&iacute;nea 
							como en las requisiciones de servicios.
						</td>												
					</tr>
					<tr>
						<td>Unidad Ejecutora:</td>
						<td align="left">
							<div id="unidadEjecutoraOC" style="display: block">
								<table width="100%">
									<tr>
										<td align="left">
											<select id="unidadEjecutoraCA" name="unidadEjecutoraCA" style="width: 45em;" onchange="cambiaCentrocontableUsuario();">
												<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
											</select>
										</td>
									</tr>
								</table>
							</div>
							<div id="unidadEjecutoraLocal" style="display: none">
								<table>
									<tr>
										<td align="left"><input name="unidadEjecutoraCALocal" id="unidadEjecutoraCALocal" style="border: 0px solid black;text-align: left" size="80"></td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td>Tipo de Consolidado:</td>
						<td align="left"><select id="tipoConsolidadoCA" name="tipoConsolidadoCA" style="width: 45em;"></select></td>
					</tr>
					<tr>
						<td>Alcance:</td>
						<td align="left"><select id="AlcanceCA" name="AlcanceCA" style="width: 45em;"></select></td>
					</tr>
					<tr>
						<td>Requisici&oacute;n:</td>
						<td align="left"><select id="cIdSolicitudCA" name="cIdSolicitudCA" style="width: 45em;"></select></td>
					</tr>
					<tr>
						<td>Descripci&oacute;n:</td>
						<td align="left"><textarea name="cDescripcionCA" rows="6" cols="72" id="cDescripcionCA" style='text-transform: uppercase;' onkeypress="textCounter(this,2000);" ></textarea></td>
					</tr>
					<tr>
						<td>Notas:</td>
						<td align="left"><textarea name="cNotasCA" rows="2" cols="72" id="cNotasCA" style='text-transform: uppercase;' onkeypress="textCounter(this,255);" ></textarea></td>
					</tr>
					<tr>
						<td colspan="2" align="center"> <input type="button" name="btnGuardarConsolidadoAutomatico" id="btnGuardarConsolidadoAutomatico" value="Guardar"  class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
					</tr>
				</table>
			</fieldset>
		</form>				
	</body>
</html>
