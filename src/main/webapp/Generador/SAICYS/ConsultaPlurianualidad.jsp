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
	String roles="";
	Map rol =usuarioTab1.getRoles();
	
	
%>

<!doctype html>
<html>
  <head>
    <title>Consulta Plurianualidad</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8"><!--
	
		var cEjercicio;
		var oTable;
		var roles="";
		$(document).ready(function() {
			tabb=1;
			showAndHideTabs();
			<%
				int presupuesto=0;
				int consulta=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map pestanas=ebl.getPestana(role,"PlurianualidadContratos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana=(String)e.getValue();
					if ("presupuestoPlurianualidad".equals(pestana)){
						 presupuesto=1;
					}
					if ("consultaPlurianualidad".equals(pestana)){
						 consulta=1;
					}
				}
				///botones
				Map botones=nb.getBotones(role,"PlurianualidadContratos","consultaPlurianualidad");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
			%> 
			// se deshabilitan las pestañas de presupuesto y precompromiso plurianual
			deshabilitaTabs();
			var consulta='<%=consulta%>';
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback : function() {
					$("#cIdUnidadEjecutora").val("<%=usuarioTab1.getU_UR()%>");
					mostrar();
				}
			});
			$('#tblconsultaPlurianualidades').on('dblclick', 'tr',function(){
				var presupuesto='<%=presupuesto%>';
				if(presupuesto==0){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
				  	var anSelected=fnGetSelected(oTable);
				  	var aData=oTable.fnGetData(anSelected[0]);
				  	
				  	$("#cIdContratoDefinitivoPlurianual").val(aData[0]);
				  	queryFormPost("datosContratoPlurianual", {async: false,
					  	callback : function() 
						{
							// se habilitan las pestañas de presupuesto y precompromiso plurianual al momento del doble click del renglon
						 	habilitaTabs();
						  	window.location = 'Plurianualidad.jsp?tab=2&cIdContratoDefinitivo='+aData[0]+'&cIdContrato='+aData[7]+'&cEjercicio='+aData[1]
					  			+'&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val()+'&cIdTipoCambio='+aData[6]+'&cIdTipoContrato='+  aData[7].substring(0, 2)+'&lContratoAbierto='+ $("#lContratoAbierto").val();
						}
				  	});
				}
			});
		});//termina el document ready
		
		function deshabilitaTabs(){
			$( "#presupuestoPlurianualidad" ).attr("disabled", true);
			$( "#precompromisoPlurianualidad" ).attr("disabled", true);
			$( "#caratulaPlurianualidad" ).attr("disabled", true);
			$( "#nuevasEps" ).attr("disabled", true);
// 			$( "#ampliacionContratoPluri" ).attr("disabled", true);
		
		}
		function habilitaTabs(){
			$( "#presupuestoPlurianualidad" ).attr("disabled", false);
			$( "#precompromisoPlurianualidad" ).attr("disabled", false);
			$( "#caratulaPlurianualidad" ).attr("disabled", false);
			$( "#nuevasEps" ).attr("disabled", false);
			//$( "#ampliacionContratoPluri" ).attr("disabled", false);
			
		}
		function mostrar() { 
			var consulta='<%=consulta%>';
			if (consulta==0){
				var qw = " 1=1 ";
				
				if($("#cIdDefinitivo").val()!=""){
					qw+=" and  cIdContratoDefinitivo LIKE '%25"+$("#cIdDefinitivo").val()+"%25'";
				}
				oTable=	$('#tblconsultaPlurianualidades').dataTable({
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
					bProcessing: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mConsultaContratosPlu('"+$("#cIdUnidadEjecutora").val()+"')&qw=" + qw,
					aaSorting: [[ 0, "asc" ],[ 1, "asc" ]] ,			
					aoColumns: [
						//{ sName: "cVinculo", bVisible: false },
						{ sName: "cIdContratoDefinitivo" },
						{ sName: "cEjercicio" },
						{ sName: "mMontoBruto" },
						{ sName: "nPorcentajeIVA", bVisible: false},
						{ sName: "mMontoIVA" },
						{ sName: "mMontoNeto" },
						{ sName: "nIdTipoCambio", bVisible: false },
						{ sName: "cIdContratoDefinitivoContrato" }
						
					]
				});
			}
		}
		
		//obtiene solo un registro del renglon seleccionado lo mete en un arreglo
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
		function guardarPlurianualidad(indice){
			if( parseInt($("#Estado_"+indice+"").val(),10)> 1){
				alert("No se puede guardar Plurianualidad, su Estado no lo Permite ")
				return;
			}
			if(parseFloat($("#porcentajeIva_"+indice+"").val())> 100 || parseFloat($("#porcentajeIva_"+indice+"").val()) < 0 ){
				alert("El Iva no puede ser Mayor a 100 o Menor a 0");
				return;
			}
			if(confirm("\xBFEstas seguro de actualizar los datos de contrato definitivo "+$("#cIdContratoDefinitivo_"+indice+"").val()+"?")){
				$("#contratoDefinitivo").val($("#cIdContratoDefinitivo_"+indice+"").val());
				$("#montoIva").val($("#porcentajeIva_"+indice+"").val());
				$("#montoTotal").val($("#montoNeto_"+indice+"").val());
				$("#mTipoCambio").val($("#nidtipoCambio_"+indice+"").val());
				queryFormPost("actualizaConsultaPlurianual",{async:false});
				window.location='Plurianualidad.jsp?tab=1'
				return;
			}
		}
		function borraPlurianualidad(indice){
			if( parseInt($("#Estado_"+indice+"").val(),10)> 1){
	           	alert("No se puede borrar la Plurianualidad, su Estado no lo Permite ")
	           	return
			}
	          if(confirm("\xBFEstas seguro de Borrar la Plurianualidad del contrato definitivo "+$("#cIdContratoDefinitivo_"+indice+"").val()+"?")){
			  	$("#contratoDefinitivo").val($("#cIdContratoDefinitivo_"+indice+"").val());
				queryFormPost("borraConsultaPlurianual",{async:false});
		       	window.location='Plurianualidad.jsp?tab=1'
			    return;
	         }
		}
		function cambiaCentrocontableUsuario(){
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
					}
				}
			});
		}
	</script>
</head>
  <body >
  	<form id="formConsulta">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Consulta Plurianualidades</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
								<option value="<%=usuarioTab1.getU_UR()%>" selected="selected">
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
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo PLU-CV-A04-1/2022" aria-label="Número de Contrato SAI, Ejemplo PLU-CV-A04-1/2022" aria-describedby="basic-addon1"  name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnConsultaPlurianualidad" name="btnConsultaPlurianualidad" 	value="Buscar"	onclick="mostrar();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblconsultaPlurianualidades" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th>Contrato Definitivo</th>
				        			<th>Ejercicio</th>
				        			<th> Monto Bruto</th>
				        			<th style="display: none;"> % IVA</th>
				        			<th> Monto IVA</th>			
				        			<th>Monto Neto</th>
				        			<th style="display: none;">Tipo de Cambio</th>
				        			<th>Contrato Original</th>
								</tr>										
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="contratoDefinitivo" name="contratoDefinitivo" size="10" value="-1" /> 
		<input type="hidden" id="montoIva" name="montoIva" size="10" /> 
		<input type="hidden" id="montoTotal" name="montoTotal" size="10" /> 
		<input type="hidden" id="mTipoCambio" name="mTipoCambio" size="10" /> 
		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
		<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab1.getLogin()%>"/>	
		<input type="hidden" name="nIdEstado" id="nIdEstado"/>
		<input type="hidden" name="lContratoAbierto" id="lContratoAbierto"/> 
		<input type="hidden" name="cIdContratoDefinitivoPlurianual" id="cIdContratoDefinitivoPlurianual" value=""  />
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="-1"/>
		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
	</form>
  </body>
</html>
