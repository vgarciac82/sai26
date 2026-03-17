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
<script type="text/javascript" charset="utf-8">
		var oTable;
		$(document).ready(function() {
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
				Map pestanas=ebl.getPestana(role,"ContratoModificatorio");
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
				Map botones=nb.getBotones(role,"ContratoModificatorio","consultaContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
			%>
			tabb=1;
			showAndHideTabs();
			 $('#tblConsultaContratosMod').on('dblclick', 'tr',function(){
				 var tabla = '<%=tabla%>';
					if (tabla == 0){
						if ($(this).hasClass('row_selected'))             
							$(this).removeClass('row_selected');         
						else            
							$(this).addClass('row_selected');
						var anSelected = fnGetSelected( oTable );				
						var aData = oTable.fnGetData(anSelected[0]);
						
						//Construir la ruta para la caratula del contrato modificado
						var cEjer = aData[0];
						var cMod = aData[1];
						var cDefinitivo = aData[2];
						var cContrato = aData[6];
						var pUrl = 'ContratoModificatorio.jsp?tab=2&mod=' + cMod + '&cDefinitivo=' + cDefinitivo + '&cEjercicio=' + cEjer 
						+ '&cContrato=' + cContrato+ '&isConvEjercicioAnt=' + aData[7];
						window.location = pUrl;
					}
			});  
			
			
			roles="<%=role%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback : function() {
					$("#cIdUnidadEjecutora").val("<%=usuarioTab1.getU_UR()%>");
				}
			});
			queryFormPost("mSistema_cEjercicioRead", {async: false,
				callback : function() {
					mostrar();		
				}
			});
			
	
		});
		
		function mostrar() {
			var consulta=<%=consulta%>;
			if (consulta==0){
				var qw = " cEjercicio = '" + $("#cEjercicio").val() + "'" ;				
				
				if ($("#cIdDefinitivo").val() != ''){
					qw = qw + " AND cIdContratoDefinitivo LIKE '%25" + $("#cIdDefinitivo").val() + "%25'";
				}
				if($("#cIdUnidadEjecutora").val()=="A04"){
					qw = qw +" AND (cIdContrato LIKE '%25" + $("#cIdUnidadEjecutora").val() + "%25' or cIdContrato LIKE '%25A10%25')";
				}else{
					qw = qw +" AND cIdContrato LIKE '%25" + $("#cIdUnidadEjecutora").val() + "%25'";
				}
				oTable = $("#tblConsultaContratosMod").dataTable({
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay convenios modificatorios",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mContratoModificado&qw=" + qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "cEjercicio" },
						{ sName: "nConsecutivoModificacion" },
						{ sName: "cIdContratoDefinitivo" },
						{ sName: "mTotalAnterior" },
						{ sName: "mTotalModificacion" },
						{ sName: "mTotalNuevo" },
						{ sName: "cIdContrato", bVisible: false },
						{ sName: "isConvEjercicioAnt", bVisible: false }
					]
	        	});
			}
			
		}
		function fnGetSelected( oTableLocal )
		{
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
						$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
						$("#cIdUnidadResponsableUsuario").val(j[0].unidadEjecutora);
						
					}
				}
			});
		}
	</script>
</head>
<body>
  	<form id="formConsulta">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Consulta Contratos Modificados</legend>
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
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CV-A04-1/2022" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1/2022" aria-describedby="basic-addon1"  name="cIdDefinitivo" id="cIdDefinitivo"  />
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
									<th>EJERCICIO</th>
				        			<th>MODIFICACI&Oacute;N</th>
				        			<th>CONTRATO DEFINITIVO</th>
				        			<th>MONTO ANTERIOR</th> 
				        			<th>MONTO MODIFICACION</th>
				        			<th>MONTO NUEVO</th>
				        			<th style="display: none;"></th>
				        			<th style="display: none;"></th>
								</tr>										
							</thead>
						</table>
					</div>
					
				</div>
			</div>
			
		</fieldset>
	 		
	 	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
	 	<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
	    <input type="hidden" name="isAdmin" id="isAdmin" value="1" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab1.getLogin()%>"/>
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
	    <input type="hidden" name="cEjercicio" id="cEjercicio"/>
	 </form>
</body>
</html>

