<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String cIdContratoDefinitivo="";
	if(request.getParameter("cIdContratoDefinitivo")!=null){
		cIdContratoDefinitivo=request.getParameter("cIdContratoDefinitivo");
		session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, cIdContratoDefinitivo);
	}else{
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_ContratCap4Definitivo);
	}
%>

<!DOCTYPE html>
<html>
  <head>
    
    
    <title>My JSP 'PreaupuestoContratoCap4.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	
	<script type="text/javascript">
		var oTableMP="";
		var roles='';
		$(document).ready(function() {
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
				Map botones=nb.getBotones(roles,"ContratoCap4","PresupuestoContratoCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarPresupuestoContCap4".equals(img)){  
						imgAprobar=1;
					}
					if ("imgDevolverPresupuestoContcap4".equals(img)){
						imgDevolver=1; 
					}	
				}

				%> 
			roles="<%=roles%>";
			initDataTable();
			initQuerys();
			loadEventClickHandlers();
			showButtons();
		});//Fin del document ready
		function initQuerys(){
			queryFormPost("obtieneDatosContCap4", {async : false,
				callback : function() {
					loadClavesPresupuestalesContrato();
					activacheck();
				}
			
			});
		}
		function loadClavesPresupuestalesContrato(){
			var idContrato = $('#cIdTipoContrato').val() + "-" + $('#cIdUnidadEjecutora').val() + "-" + $('#nIdConsecutivo').val();
			$('#cIdContratoMat').val(idContrato);
			if(roles.indexOf("ADMIN_RECMAT") < 0 &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0 && parseInt($("#usuariosMismaUE").val(),10)==0){
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
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP &qw="+qw,
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
		function buscaClavePresupuestal(){
			pp = window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
					+ '&cIdDocumento=' + $('#cIdContratoDefinitivo').val() + '&cIdRFC=' + $('#cIdRFC').val()
					 +'&cuentaDisponible=' + $('#cuentaDisponible').val()+'&isContratoCap4=1'
					 
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
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
			queryFormPost("mContratoCap4CTAUpdate",{async:false});
			loadClavesPresupuestalesContrato();
		}
		function activacheck(){
			$("#checkDispRadicado").attr("checked",false);
			if($("#cuentaDisponible").val()=='82109'){
				$("#checkDispRadicado").attr("checked",true);
			}
		}
		function fnClickAddRowC() {
			
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"warning",button: "Cerrar"});
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
			var idContrato = $('#cIdTipoContrato').val() + "-" + $('#cIdUnidadEjecutora').val() + "-" + $('#nIdConsecutivo').val();
			$('#cIdContratoMat').val(idContrato);
			queryFormPost("agregaEPContratoCreate", {async: false});
			loadClavesPresupuestalesContrato();
			guardaBitacora("AGREGA_EP",$('#cContratoDefinitivo').val());
			$('#ep').val("");
		}
		function loadEventClickHandlers(){
			$('#dt_clavepresup').on('dblclick','tr', function() {
				if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1) 
					&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
					swal("No tienes Permisos.",{icon:"warning",button: "Cerrar"});
					return;
				}
				if(parseInt($("#nIdEstado").val(),10)>1){
					swal("No se puede eliminar por que el estatus es diferente a captura.",{icon:"warning",button: "Cerrar"});
					return;
				}     
					$(oTableClaves.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});						
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableClaves );						
					var aData = oTableClaves.fnGetData(anSelected[0]);							
					$("#epAUX").val(aData[0]);
					var idContrato = $('#cIdTipoContrato').val() + "-" + $('#cIdUnidadEjecutora').val() + "-" + $('#nIdConsecutivo').val();
					$('#cIdContratoMat').val(idContrato);		
					if ($("#nIdEstado").val() == "1" ){
						 var index=($("#epAUX").val());
						  var tmp = index.lastIndexOf( "\." );
				          var uEje= index.substring( tmp - 3, tmp);
				            $("#cIdUnidadEjecutora").val(uEje);
							queryFormPost("quitaEPContratoDelete", {async: false});
							queryFormPost("quitaEPContratoDeleteBorra", {async: false});
							guardaBitacora("BORRA_EP",$('#cContratoDefinitivo').val());
							loadClavesPresupuestalesContrato();
				 	}
			});	
		}
		function guardaBitacora(accion,documento){
			//Bitácora
			$("#cAccion").val(accion);
			$("#cIdDocumento").val(documento);
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function fnGetSelected( oTableLocal ){
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
			if(parseInt($("#nIdEstado").val(),10)>1){
				swal("No se puede aprobar el contrato por su estatus.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if($("#cIdRFC").val()==''){
				swal("Favor de agregar un Proveedor.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if(parseFloat($("#subtotal").val())<=0.00){
				swal("El monto del contrato no puede ser menor o igual a 0.00",{icon:"warning",button: "Cerrar"});
				return;
			}
			//Validar si hay eps
			var aTrs = oTableClaves.dataTable().fnGetNodes();
			if(aTrs.length<=0){
				swal("Favor de agregar al menos una clave presupuestal.",{icon:"warning",button: "Cerrar"});
				return;
			}
		
			 $.ajax({url: '../../servlet/ContratoCap4Servlet'
						, type:'post' , async: false,data:'operacion=3&cEjercicio='+$("#cEjercicio").val()
						+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
						
						+'&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val()
						, dataType: 'json',
		        		mimeType: 'application/json', success: 
						function(j){
							var mensaje=j[0].MENSAJE;
							var resp=j[0].RESPUESTA;
							swal({
								title: "",
								text: mensaje,
								icon: "info",
								buttons: {
									confirm : "Cerrar"
								},
							}).then((continuar) => {
								if(resp){
									window.location = "ContratoCap4.jsp?tab=4";
								}
							});
						}
					});
			
		}
		function showButtons(){
			switch(parseInt($("#nIdEstado").val(),10)) {
				case 1:
			        $("#imgAprobarPresupuestoContCap4").show();					
					$("#AgregarEPContratoCap4").show();
					$("#nIdClaveEgresosXContratoCap4").show();
					$("#trDispRad").hide();
					$("#imgDevolverPresupuestoContcap4").hide();
					
			        break;
		     	case 2:
			        $("#imgAprobarPresupuestoContCap4").hide();
					$("#AgregarEPContratoCap4").hide();
					$("#nIdClaveEgresosXContratoCap4").hide();
					$("#trDispRad").hide();
					$("#imgDevolverPresupuestoContcap4").show();
			        break;
		     	default:
		     		$("#imgAprobarPresupuestoContCap4").hide();
					$("#AgregarEPContratoCap4").hide();
					$("#nIdClaveEgresosXContratoCap4").hide();
					$("#trDispRad").hide();
					$("#imgDevolverPresupuestoContcap4").hide();
		   			break;
			}
		}
		function devuelveContrato(){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 || roles.toString().indexOf("JEFES")>=0 ||roles.indexOf("ANALISTA")>=0||parseInt($("#usuariosMismaUE").val(),10)==1) 
				&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if(parseInt($("#nIdEstado").val(),10)!=2){
				swal("El contrato no se puede devolver por que su estado no lo permite.",{icon:"warning",button: "Cerrar"});
				return;
			}
			
			swal({
				title: "",
				text: "¿Está seguro que quiere devolver el contrato?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$.ajax({url: "../../servlet/ContratoCap4Servlet" , type:'post' , async: false
						,data:'operacion=4&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
						+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()
						, dataType: 'json', success: 
							function(j){
								var mensaje=j[0].MENSAJE;
								var resp=j[0].RESPUESTA;
								swal({
									title: "",
									text: mensaje,
									icon: "info",
									buttons: {
										confirm : "Cerrar"
									},
								}).then((continuar) => {
									if(resp){
										window.location = "ContratoCap4.jsp?tab=4";
									}
								});
							}
						});
				}
			});
			
			
		}
	</script>
  </head>
  
  <body>
  	<form id="formPresupContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarPresupuestoContCap4" name="imgAprobarPresupuestoContCap4" 	value="Aprobar"	onclick="guardaContrato();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverPresupuestoContcap4" name="imgDevolverPresupuestoContcap4" 	value="Devolver"	onclick="devuelveContrato();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoCap4.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTipoContrato" id="lblTipoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cEstado" id="cEstado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalIVA" id="lblTotalIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNeto" id="lblTotalNeto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNetoMax" id="lblTotalNetoMax"  readonly/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Claves</legend>
			<br>
			<div class="form-group form-check" id="trDispRad" style="display: none;">
				<div class="row" >
					<div class="form-check input-group">
						<div class="col-3">		
							<label class="form-check-label" for="checkDispRadicado">¿Es Disponible Radicado? </label>
						</div>
						<div class="form-check  col-auto">
			  				<input class="form-check-input" type="checkbox" id="checkDispRadicado" name="checkDispRadicado" onClick="muestraDispRadicado();">
			  			</div>
		  			</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" id="divAddEP">
					<div class="col-md-6" >
						<label for="ep">E.P.</label>
						<input type="text" class="form-control" placeholder="Seleccione la nueva estructura presupuestal que desea agregar."
						id="ep" name="ep" readonly>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="nIdClaveEgresosXContratoCap4" name="nIdClaveEgresosXContratoCap4" value="..." onclick="buscaClavePresupuestal()" 
						class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"  title="Dar clic para mostrar las estructuras presupuestales con recurso disponible."/>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="AgregarEPContratoCap4" name="AgregarEPContratoCap4" value="Agregar" onclick="fnClickAddRowC()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th>Estructura Presupuestal "EP"</th> 
									<th>Clave SHCP</th> 
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="cIdContratoDefinitivo" name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo %>" />
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo" value="<%=cIdContratoDefinitivo %>" />
		<input type="hidden" id="cuentaDisponible" name="cuentaDisponible" value="82106" />
		<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" value="" />
		<input type="hidden" id="cIdRFC" name="cIdRFC" value="" />
		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" id="cEjercicio" name="cEjercicio" value=""/>
		<input type="hidden" id="cAccion" name="cAccion" value=""/>
		<input type="hidden" id="usuariosMismaUE" name="usuariosMismaUE" value=""/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" id="cIdUnidadEjecutoraEP" name="cIdUnidadEjecutoraEP" value=""/>
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value=""/>
		<input type="hidden" id="ClaveInterna" name="ClaveInterna" value=""/>
		<input type="hidden" id="cIdTipoContrato" name="cIdTipoContrato" value=""/>
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuario.getLogin() %>"/>
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%= usuario.getLogin() %>"/>
		<input type="hidden" name="cIdContratoMat" id="cIdContratoMat" value=""/>
		<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value=""/>
		<input type="hidden" name="epAUX" id="epAUX" value=""/>
		<input type="hidden" name="nIdEstado" id="nIdEstado" value="1"/>
		<input type="hidden" name="subtotal" id="subtotal" value=""/>
		<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
  	</form>
  </body>
</html>
