<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
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
    
    <title>My JSP 'NuevasEpsContratoCap4.jsp' starting page</title>
    
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
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
			%>
			roles="<%=roles%>";
			initQuerys();
			loadClavesPresupuestalesContrato();
			
		});//Fin del document ready
		function initQuerys(){
			queryFormPost("obtieneDatosContCap4", {async : false,
				callback: function(){
					if($("#isAbierto").val()==0){
						$("#trTotalNetoMax").hide();
					}else{
						$("#trTotalNetoMax").show();
					}
				}
			});
		}
		function muestraDispRadicado(){
			$("#cuentaDisponible").val('82106');	
			if($('#checkDispRadicado').is(':checked')){
				$("#cuentaDisponible").val('82109');
			}
		}
		function loadClavesPresupuestalesContrato(){
			 var qw = " 1 = 1  AND cIdContratoDefinitivo = '"+ $("#cIdContratoDefinitivo").val()+"'";
			
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
					aoColumns: [
						{ sName: "nIdClaveEgresos"},
						{ sName: "ClaveInterna" }
						]
			}) ;
		}
		function buscaClavePresupuestal()
		{
			window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
					+ '&cIdDocumento=' + $('#cIdContratoDefinitivo').val() + '&cIdRFC=' + $('#cIdRFC').val()
					 +'&cuentaDisponible=' + $('#cuentaDisponible').val()+'&isContratoCap4=1'
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
		}
		function fnClickAddRowC() {
			
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
			$("#nIdClaveEgresos").val(vep);
			swal({
				title: "",
				text: "Seguro que desea agregar la siguiente EP: "+vep+"\nNo se podr\u00e1 revertir el cambio.\n¿Desea continuar?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$("#cIdContratoMat").val($("#cIdContratoDefinitivo").val());
					$("#cEjercicioTbl").val($("#cEjercicio").val());
					$("#cIdContratoTbl").val( $("#cIdContratoDefinitivo").val());
					queryFormPost("agregaEPContratoCreate,ContratoEPCreate", {async: false,
						callback: function(){
							$('#ep').val("");
							loadClavesPresupuestalesContrato();
						} 				
					});
				}
			});
			
		}
	</script>
  </head>
  
  <body>
  	<form id="formNuevasEPSContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
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
			<legend class="w-auto px-2">Estructuras presupuestales</legend>
			<div class="form-group">
				<div class="row" id="divRadicado" style="display: none;">
					<div class="col-auto">		
						<label class="form-check-label" for="checkDispRadicado">¿Es con recurso radicado? </label>
					</div>
					<div class="col-auto">
		  				<input class="form-check-input" type="checkbox" id="checkDispRadicado" name="checkDispRadicado" value="0" onclick="muestraDispRadicado()">
		  			</div>
				</div>
				<div class="row">
					<div class="col-md-6" >
						<label for="ep">E.P.</label>
						<input type="text" class="form-control" placeholder="Seleccione la nueva estructura presupuestal que desea agregar."
						id="ep" name="ep" readonly>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="nIdClaveEgresosXContrato" name="nIdClaveEgresosXContrato" value="..." onclick="buscaClavePresupuestal()" 
						class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"  title="Dar clic para mostrar las estructuras presupuestales con recurso disponible."/>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="AgregarEPContrato" name="Add2" value="Agregar" onclick="fnClickAddRowC()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>C&oacute;digo SAI</th> 
									<th>Clave SHCP</th> 
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="cIdContratoDefinitivo" name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo %>" />
		<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		<input type="hidden" id="cIdRFC" name="cIdRFC" value="" />
		<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuario.getU_UR()%>" />
    	<input type="hidden" id="ClaveInterna" name="ClaveInterna" value="1"/>
    	<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" value=""/>
    	<input type="hidden" name="cEjercicio" id="cEjercicio" value=""/>
    	<input type="hidden" name="cIdContratoMat" id="cIdContratoMat"/>
    	<input type="hidden" id="cEjercicioTbl" name="cEjercicioTbl" value=""/>
		<input type="hidden" id="cIdContratoTbl" name="cIdContratoTbl" value=""/>
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo"  value="<%=cIdContratoDefinitivo %>"/>
		<input type="hidden" id="cIdTipoContrato" name="cIdTipoContrato" value=""/>
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuario.getLogin() %>"/>
		<input type="hidden" id="cTContratoTbl" name="cTContratoTbl" value="DI"/>
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value="<%= usuario.getPropiedad("CCENTROCONTABLE").getValor()%>"/>
  	</form>
  </body>
</html>
