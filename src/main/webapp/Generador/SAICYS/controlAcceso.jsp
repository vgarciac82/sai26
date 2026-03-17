<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
	   return;
	}
	String cEjercicio = "";
	String cIdTipoConsolidado = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute( GestionInterface.ATT_ReqEjercicio) != null ) {
	      session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
	session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
	session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
	session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
	}

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Control de Accesos</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
				@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
				@import "../css/demo_table_jui.css";
				@import "../css/demo_page.css";
		</style>
		<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />
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
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
		
		<script type="text/javascript" charset="utf-8">		
		$(document).ready(function() {	
			// catalogos
			querySelectPost("RolesRead", "rol", {async : false});
			cargarOpciones();
			
			querySelectPost("ModulosRead", "modulo", {async : false});	
			querySelectPost("ModulosRead", "bmodulo", {async : false});	
			//querySelectPost("ModulosFRead", "fmodulo", {async : false});
			$("#idModulo").val($('#modulo option:selected').val());
			$("#idFModulo").val($('#fmodulo option:selected').val());
			$("#idModuloB").val($('#bmodulo option:selected').val());
			querySelectPost("PestanaRead", "pestana", {async : false});
			//querySelectPost("FundamentoRead", "fundamento", {async : false});
			querySelectPost("BPestanaRead", "bPestana", {async : false});
			$("#idPestana").val($('#pestana option:selected').val());
			$("#idPestana").val($('#pestana option:selected').val());
			$("#idFundamento").val($('#fundamento option:selected').val());
			querySelectPost("BotonRead", "boton", {async : false});
			$("#idBoton").val($('#boton option:selected').val());
			
			
			
			$("#btnAgregarP").button().click(function(){
				queryFormPost("mRoleNegPestanaInsert",{async : false});
				limpiar();
				cargarOpciones();
			});
			$("#btnAgregarF").button().click(function(){
				queryFormPost("mRoleNegCategoriaProcedimientoInsert",{async : false});
				limpiar();
				cargarOpciones();
			});
			$("#btnAgregarB").button().click(function(){
				queryFormPost("mRoleNegBotonInsert",{async : false});
				limpiar();
				cargarOpciones();
			});
			
			$('#tblPestana tr').live('click', function(){        
				if ( $(this).hasClass('row_selected') )             
					 $(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				
			}); 
			$('#tblFundamento tr').live('click', function(){        
				if ( $(this).hasClass('row_selected') )             
					 $(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				
			}); 
			
			$('#tblBotones tr').live('click', function(){        
				if ( $(this).hasClass('row_selected') )             
					 $(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				
			}); 
			
			$("#btnEliminaP").button().click(function(){
				var aTrs = $('#tblPestana').dataTable().fnGetNodes(); 
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblPestana').dataTable().fnGetData(aTrs[i]);
							var mod=nTr[0];
							var pestana=nTr[2];
							$("#pestanaDelete").val(pestana);
							$("#moduloDelete").val(mod);
							queryFormPost("mRoleNegPestanaDelete", {async : false});												
						}     
					}
					limpiar();
					cargarOpciones();
					
			});
			$("#btnEliminaF").button().click(function(){
				var aTrs = $('#tblFundamento').dataTable().fnGetNodes(); 
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblFundamento').dataTable().fnGetData(aTrs[i]);
							var mod=nTr[0];
							var fundamento=nTr[3];
							//alert (nTr);
							$("#fundamentoDelete").val(fundamento);
							$("#moduloFDelete").val(mod);
							queryFormPost("mRoleNegCategoriaProcedimientoDelete", {async : false});												
						}     
					}
					limpiar();
					cargarOpciones();
					
			});
			
			$("#btnEliminaB").button().click(function(){
				var aTrs = $('#tblBotones').dataTable().fnGetNodes(); 
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblBotones').dataTable().fnGetData(aTrs[i]);
							
							var mod=nTr[0];
							var pestana=nTr[1];
							var boton=nTr[3];
							$("#pestanaDelete").val(pestana);
							$("#moduloDelete").val(mod);
							$("#botonDelete").val(boton);
							
							queryFormPost("mRoleNegBotonDelete", {async : false});	
							
						}     
					}
					limpiar();
					cargarOpciones();
			});
			
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
			alert(t);
		}
		
			
		function validarCampoDescp(a){
			var v = document.getElementById(a).value.replace("ñ", "n").replace("Ñ", "N");					
				document.getElementById(a).value = v;				
		}
		
		
		function cargarOpciones(){
			var select=$('#rol option:selected').val();
			var opciones=select.split("|");			
			$("#idRol").val(opciones[0]);
			querySelectPost("PestanaRead", "pestana", {async : false});
			$("#idPestana").val($('#pestana option:selected').val());
			var where =" 1=1 ";
			where+=" AND R_NOMBRE='"+$("#idRol").val()+"'";
			$('#tblPestana').dataTable({         
					 sScrollX: "100%",
					 sScrollXInner: "97%",
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
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 bAutoWidth: false,
					 bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mNegativaPestana&qw="+ where,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "MODULO"},
					{ sName: "PESTANA"},
					{ sName: "IDPESTANA", bVisible: false}
					]
				});
				
				$('#tblFundamento').dataTable({         
					 sScrollX: "100%",
					 sScrollXInner: "97%",
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
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 bAutoWidth: false,
					 bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mNegativaCategoria&qw="+ where,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "MODULO"},
					{ sName: "cCategoria"},
					{ sName: "R_NOMBRE", bVisible: false},
					{ sName: "nidCategoria", bVisible: false}
					]
				});
			
			var select=$('#rol option:selected').val();
			var opciones=select.split("|");			
			$("#idRol").val(opciones[0]);
			var where =" 1=1 ";
			where+=" AND R_NOMBRE='"+$("#idRol").val()+"'";
			$('#tblBotones').dataTable({         
					 sScrollX: "100%",
					 sScrollXInner: "97%",
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
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 bAutoWidth: false,
					 bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mNegativaBoton&qw="+ where,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "MODULO"},
					{ sName: "idPestana", bVisible: false},
					{ sName: "PESTANA"},
					{ sName: "idBoton", bVisible: false},
					{ sName: "BOTON"}
					]
				});
			
		}
		
		function limpiar(){
			
			$("#Modulo").val("");
			$("#Pestana").val("");
			$("#moduloDelete").val("");
			$("#pestanaDelete").val("");
			$("#botonDelete").val("");
			$("#bModulo").val("");
			$("#bPestana").val("");
			$("#fmodulo").val("");
			$("#fundamento").val("");
			
			$("#boton").val("");
			querySelectPost("ModulosRead", "modulo", {async : false});	
			querySelectPost("ModulosRead", "bmodulo", {async : false});	
			querySelectPost("ModulosFRead", "fmodulo", {async : false});
			$("#idModulo").val($('#modulo option:selected').val());
			$("#idModuloB").val($('#bmodulo option:selected').val());
			$("#idFModulo").val($('#fmodulo option:selected').val());
			querySelectPost("PestanaRead", "pestana", {async : false});
			querySelectPost("BPestanaRead", "bPestana", {async : false});
			querySelectPost("FundamentoRead", "fundamento", {async : false});
			$("#idFundamento").val($('#fundamento option:selected').val());
			$("#idPestana").val($('#pestana option:selected').val());
			$("#idPestanaB").val($('#bPestana option:selected').val());
			querySelectPost("BotonRead", "boton", {async : false});
			$("#idBoton").val($('#boton option:selected').val());
		//	$("#idModulo").val("");  
		}
		
		function cargarPestana(){
			$("#idModulo").val($("#modulo option:selected").val());
			querySelectPost("PestanaRead", "pestana", {async : false});
			$("#idPestana").val($('#pestana option:selected').val());
		}
		function cargarFundamento(){
			$("#idFModulo").val($("#fmodulo option:selected").val());
			querySelectPost("FundamentoRead", "fundamento", {async : false});
			$("#idFundamento").val($('#fundamento option:selected').val());
		}
		
		function getIdPestana(){
			$("#idPestana").val($('#pestana option:selected').val());
		}
		function getIdFundamento(){
			$("#idFundamento").val($('#fundamento option:selected').val());
		}
		function cargarPestanaB(){
			$("#idModuloB").val($("#bmodulo option:selected").val());
			querySelectPost("BPestanaRead", "bPestana", {async : false});
			$("#idPestanaB").val($('#bPestana option:selected').val());
			cargarBotones();
		}
		function cargarBotones(){
		
			$("#idPestanaB").val($('#bPestana option:selected').val());	
			querySelectPost("BotonRead", "boton", {async : false});
			$("#idBoton").val($('#boton option:selected').val());
			// contar los botones disponibles
		//	queryFormPost("CountBotonRead",{async:false});
		/*	var total=("#numBoton").val();
			if (total==0){
				querySelectPost("BPestanaRead", "bPestana", {async : false});
			}*/
			
			
		}
		function getIdBoton(){
			$("#idBoton").val($('#boton option:selected').val());
		}

		</script>
		</head> <body id="ctrl_acceso" ><form name="access">			
			<fieldset><legend>Control de Acceso</legend>	
				<br/><br/>
				<table align="center">
				<tr>
					<td>Rol</td>
					<td align="left">
					  <select id="rol" name="rol" style="width: 40em;" onchange="cargarOpciones();"></select><input type="hidden" name="idRol" id="idRol"/>
					</td>
				</tr>
				</table>
				<br/>		<br/>		<br/>
				<fieldset>
					<legend>Pestañas</legend>
					<table align="center">
						<tr>
							<td colspan="3">
								<table id="tblPestana" class="display">
						            <thead>
						                <tr>
						                	<th>Modulo</th>
						                    <th>Pestaña</th>
						                </tr>
						            </thead>
						        </table>
							</td>			
						</tr>
						<tr>
						<td>Modulo</td>
						<td><select id="modulo" name="modulo" style="width: 40em;" onchange="cargarPestana();"></select></td>
					</tr>
					<tr>
						<td>Pestaña</td>
						<td><select id="pestana" name="pestana" style="width: 40em;" onchange="getIdPestana();"> 
						</select></td>
					</tr>
					<tr>
						<td align="center"><button name="btnAgregarP" id="btnAgregarP">Agregar</button></td>
						<td align="center"><button name="btnEliminaP" id="btnEliminaP">Eliminar</button> 
						<input type="hidden" id="moduloDelete" name="moduloDelete" style="width: 30em;" /> 
						<input type="hidden" id="pestanaDelete" name="pestanaDelete" style="width: 30em;" />
						<input type="hidden" id="botonDelete" name="botonDelete" style="width: 30em;" />
						<input type="hidden" id="idModulo" name="idModulo" style="width: 30em;" />
						<input type="hidden" id="idPestana" name="idPestana" style="width: 30em;" />
						<input type="hidden" id="idBoton" name="idBoton" style="width: 30em;" />
						<input type="hidden" id="idModuloB" name="idModuloB" style="width: 30em;" />
						<input type="hidden" id="idPestanaB" name="idPestanaB" style="width: 30em;" />
						<input type="hidden" id="numBoton" name="numBoton" style="width: 30em;" /></td>
						
					</tr>
					</table>
				</fieldset>
				<br/>
				<br/>
				<fieldset>
					<legend>Botones</legend>
					<table align="center">
					<tr>
						<td colspan="3">
							<table id="tblBotones" class="display">
					            <thead>
					                <tr>
					                	<th>Modulo</th>
					                	<th>idPestaña</th>
					                    <th>Pestaña</th>
					                    <th>idBoton</th>
					                    <th>Boton</th>
					                </tr>
					            </thead>
					        </table>
						</td>			
					</tr>
					<tr>
						<td>Modulo</td>
						<td><select id="bmodulo" name="bmodulo" style="width: 40em;" onchange="cargarPestanaB();"></select></td>
					</tr>
					<tr>
						<td>Pestaña</td>
						<td><select id="bPestana" name="bPestana" style="width: 40em;" onchange="cargarBotones();"></select>
					</tr>
					<tr>
						<td>Boton</td>
						<td><select id="boton" name="boton" style="width: 40em;" onchange="getIdBoton();"></select>
					</tr>
					<tr>
						<td align="center"><button name="btnAgregarB" id="btnAgregarB">Agregar</button></td>
						<td align="center"><button name="btnEliminaB" id="btnEliminaB">Eliminar</button> </td>
					</tr>
					</table>
					
				</fieldset>
				<br/> <br/> <br/>
				<fieldset>
					<legend>Fundamentos</legend>
					<table align="center">
						<tr>
							<td colspan="3">
								<table id="tblFundamento" class="display">
						            <thead>
						                <tr>
						                	<th>Modulo</th>
						                    <th>Fundamento</th>
						                </tr>
						            </thead>
						        </table>
							</td>			
						</tr>
						<tr>
						<td>Modulo</td>
						<td><select id="fmodulo" name="fmodulo" style="width: 40em;" onchange="cargarFundamento();"></select></td>
					</tr>
					<tr>
						<td>Fundamento</td>
						<td><select id="fundamento" name="fundamento" style="width: 40em;" onchange="getIdFundamento();"> 
						</select></td>
					</tr>
					<tr>
						
						
						<td align="center"><button name="btnAgregarF" id="btnAgregarF">Agregar</button></td>
						<td align="center"><button name="btnEliminaF" id="btnEliminaF">Eliminar</button> 
						<input type="hidden" id="idFModulo" name="idFModulo" style="width: 30em;" />
						<input type="hidden" id="idFundamento" name="idFundamento" style="width: 30em;" />
						
						<input type="hidden" id="moduloFDelete" name="moduloFDelete" style="width: 30em;" /> 
						<input type="hidden" id="fundamentoDelete" name="fundamentoDelete" style="width: 30em;" />
						
						
					</tr>
					</table>
				</fieldset>
				<br/>
				
			<fieldset>
				<table align="center" border="0">
					<tr>
						<td>
						
						<input type="hidden" name="idRolBoton" id="idRolBoton"/>
						<input type="hidden" name="boton1" id="boton1"/>
						<input type="hidden" name="jsp" id="jsp" value=""/>
						<input type="hidden" name="nombrePestanaH" id="nombrePestanaH" value=""/>
						<input type="hidden" name="nombreModulo" id="nombreModulo"/>
						<input type="hidden" name="modulo" id="modulo" value=""/>
						<input type="hidden" name="bntAnt" id="bntAnt" value=""/>
						</td>
					</tr>
				</table>
 			</fieldset>
		</form>				
	</body>
</html>