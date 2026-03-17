<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%
Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuarioTab.getRoles();
	String cIdContratoDefPluri=(String)session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
	String cidContratoOriginal=(String)session.getAttribute(GestionInterface.ATT_ContratoPlurianual);
%>

<!doctype html>
<html>
  <head>
   
    
    <title>My JSP 'NuevasEPSContratoPluri.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var roles='';
		$(document).ready(function() {
			tabb=5;
			showAndHideTabs();
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
			headerQuery();
			initDataTable();
			loadClavesPresupuestalesContrato();
		
		});//Fin del docuemnt ready
		function headerQuery(){
			queryFormPost("datosContratoPlurianual", {async: false});
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
			        "bScrollCollapse": true,
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );
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
					$("#cIdContratoTbl").val( $("#cContratoDefinitivo").val());
					queryFormPost("agregaEPContratoCreate,ContratoEPCreate", {async: false,
						callback: function(){
							loadClavesPresupuestalesContrato();
							$('#ep').val("");
						} 				
					});
				}
			});
		}
		function muestraDispRadicado(){
			$("#cuentaDisponible").val('82106');	
			if($('#checkDispRadicado').is(':checked')){
				$("#cuentaDisponible").val('82109');
			}
		}
		function activacheck(){
			$("#checkDispRadicado").attr("checked",false);
			if($("#cuentaDisponible").val()=='82109'){
				$("#checkDispRadicado").attr("checked",true);
			}
		}
		function loadClavesPresupuestalesContrato(){
			 var qw = " cEjercicio = '" + $("#cEjercicio").val() +
				 "' AND cIdContratoDefinitivo = '" + $("#cIdContratoDefinitivoPlurianual").val()+"'";
			if(roles.toString().indexOf("ADMIN_RECMAT") < 0 && roles.toString().indexOf("ANALISTA") < 0 && roles.toString().indexOf("JEFE") < 0
				 && parseInt($("#esDescentralizado").val(),10)==1   ){
				qw +=  " AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutoraEP").val()+"'";
				 
			}
			
			oTableClaves=$('#dt_clavepresup').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
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
				+ '&cIdDocumento=' + $('#cIdContratoDefinitivoPlurianual').val()+ '&isPlurianual=1 &cuentaDisponible='+$("#cuentaDisponible").val()
				, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px, top=10px');
			
		}
	</script>
  </head>
  
  <body>
  	<form id="frmNuevasEPS">
    	<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato Plurianual</legend>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblcIdContratoDefinitivoPluri" id="lblcIdContratoDefinitivoPluri"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblcNoContratoCNET" id="lblcNoContratoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDescContrato" id="lblDescContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblOficioDG" id="lblOficioDG"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblFolioMASCP" id="lblFolioMASCP"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lbcContratoAbierto" id="lbcContratoAbierto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lbcContratoCentralizado" id="lbcContratoCentralizado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstatus" id="lblEstatus"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblMontoIVA" id="lblMontoIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal"  readonly/>
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
    	<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cidContratoOriginal %>"  />
    	<input type="hidden" name="cIdContratoDefinitivoPlurianual" id="cIdContratoDefinitivoPlurianual" value="<%=cIdContratoDefPluri %>" />
    	<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
    	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuarioTab.getU_UR()%>" />
    	<input type="hidden" id="ClaveInterna" name="ClaveInterna" value="1"/>
    	<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" value=""/>
    	<input type="hidden" name="cIdRFC" id="cIdRFC"/>
    	<input type="hidden" name="cIdContratoMat" id="cIdContratoMat"/>
    	<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
    	<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value=""/>
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo"  value="<%=cIdContratoDefPluri %>"/>
		<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value=""/>
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
		<input type="hidden" id="cEjercicioTbl" name="cEjercicioTbl" value=""/>
		<input type="hidden" id="cIdContratoTbl" name="cIdContratoTbl" value=""/>
		<input type="hidden" id="esDescentralizado" name="esDescentralizado" value="0"/>
		<input type="hidden" id="cTContratoTbl" name="cTContratoTbl" value="DI"/>
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value="<%= usuarioTab.getPropiedad("CCENTROCONTABLE").getValor()%>"/>
    </form>
  </body>
</html>
