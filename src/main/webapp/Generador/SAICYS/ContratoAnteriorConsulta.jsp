<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab1.getLogin();
	String role="";
	Map rol =usuarioTab1.getRoles();
 %>

<!DOCTYPE html>
<html>
  <head>
   	<title>Consulta Contrato</title>
    <meta charset="UTF-8">
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		var oTable="";
		$(document).ready(function() {
			tabb=1;
			showAndHideTabs();
			<%
				int tabla = 0;
				int consulta = 0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role+= r.getKey().toString()+",";
				}
				
				if(role.length()>0){
					role = role.substring(0,role.length()-1);
				}
				Map pestanas=ebl.getPestana(role,"ContratoAnterior");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana = (String)e.getValue();
					if ("caratulaContratoMod".equals(pestana)){
						 tabla=1;
					}
					if ("consultaContratoMod".equals(pestana)){
						 consulta=1;
					}
				}
				Map botones=nb.getBotones(role,"ContratoAnterior","consultaContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
			%>
			roles="<%=role%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			
			$('#tblConsultaContratosMod').on('dblclick', 'tr',function(){
				var tabla = '<%=tabla%>';
				if (tabla == 0){
					var aTrs = oTable.fnGetNodes();
					if(aTrs.length==0){
						return;
					}
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );				
					var aData = oTable.fnGetData(anSelected[0]);
					//Construir la ruta para la caratula del contrato modificado
					$("#cIdContrato").val(aData[0]);				
					$("#contratoDefinitivo").val(aData[0]);		  			
		  			$("#RFC").val(aData[1]);
		  			$("#cConcepto").val(aData[2]);
		  			$("#mTotalContratoAnterior").val(aData[3]);
		  			$("#mTotalPagado").val(aData[4]);
		  			$("#mTotalRemanente").val(aData[5]);
		  			$("#cIdUnidadEjecutora").val(aData[6]);
		  			
		  			//queryFormPost("estadoCotratoAnteriorConsulta", {async : false});
					var cDefinitivo = $("#contratoDefinitivo").val();
			  		var cContrato = $("#cIdContrato").val();
			  		var cEjer = $("#cEjercicio").val();

				  	window.location = 'ContratoAnterior.jsp?tab=2' + '&cDefinitivo=' + cDefinitivo + '&cEjercicio=' + cEjer + '&cIdUnidadEjecutora=' + $("#cIdUnidadEjecutora").val() + '&cContrato=' + cContrato;
				}
			});
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback : function() {
					$("#cIdUnidadEjecutora").val("<%=usuarioTab1.getU_UR()%>");
					mostrar();
				}	
			});
			
		});//Fin del document ready
		function mostrar() {
			var consulta=<%=consulta%>;
			if (consulta==0){
				var qw = " cEjercicio = '" + $("#cEjercicio").val() + "'" + 				
				" AND cIdUnidadEjecutora LIKE '%25" + $("#cIdUnidadEjecutora").val() + "%25'";
				
				oTable = $("#tblConsultaContratosMod").dataTable({
					bScrollCollapse: true,
					bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bRetrive : true,
					bDestroy : true,
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Contratos",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtrado de _MAX_ registros)",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mContratoRemanenteEjercicioAnterior&qw=" + qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "cIdContratoDefinitivo" },
						{ sName: "cIdRFC" },
						{ sName: "cConcepto" },
						{ sName: "mTotalContratoAnterior" },
						{ sName: "mTotalPagado" },
						{ sName: "mTotalRemanente" },
						{ sName: "cIdUnidadEjecutora" }
						
					]
	        	});
			}
			
		}
		
	</script>
  </head>
  
  <body>
  	<form id="formConsultaCont">
    	<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Contratos Remanentes</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdDefinitivo">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CV-A04-1/2024" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1/2024" aria-describedby="basic-addon1"  
							name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscarConsultaPed" name="btnBuscarConsultaPed" 	value="Buscar"	onclick="mostrar();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblConsultaContratosMod" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th width="5%">ID CONTRATO</th>
				        			<th width="5%" >PROVEEDOR</th>
				        			<th width="20%" >CONCEPTO</th>			
				        			<th width="5%">MONTO CONTRATO</th>
				        			<th width="5%">MONTO PAGADO</th>
				        			<th width="5%">MONTO REMANENTE</th>
				        			<th width="5%">UNIDAD EJECUTORA</th>
								</tr>										
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="cEjercicio" id="cEjercicio"/>
		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
	    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab1.getLogin()%>"/>
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
  		<input type="hidden" name="mTotalContratoAnterior" id="mTotalContratoAnterior" value="" />
  		<input type="hidden" name="mTotalPagado" id="mTotalPagado" value="" />
  		<input type="hidden" name="mTotalRemanente" id="mTotalRemanente" value="" />
  		<input type="hidden" name="cConcepto" id="cConcepto" value="" />
  		<input type="hidden" name="RFC" id="RFC" value="" />
  		<input type="hidden" name="cIdContrato" id="cIdContrato" />
  		<input type="hidden" name="nEstado" id="nEstado" />
  		<input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" />
  		
  		
    </form>
  </body>
</html>
