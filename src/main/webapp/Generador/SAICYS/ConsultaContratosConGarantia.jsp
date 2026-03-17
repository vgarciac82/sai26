<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
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
	
	//System.out.print(cIdContratoDefinitivo);
%>
<!DOCTYPE html>
<html>
<head>

<title>Consulta contratos con garantías</title>
	<script type="text/javascript" charset="utf-8">
		var roles;
		var oTableConsultaContratos;
		tabb=0;
		$(document).ready(function() {
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				
			%>
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			$("#tipoOperacion").val(1);
			
			$('#tblConsulta').on('dblclick', 'tr',function(){
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
			  	var anSelected=fnGetSelected(oTableConsultaContratos);
			  	var aData=oTableConsultaContratos.fnGetData(anSelected[0]);
			  	if(aData!= ""){
			  		if($("#esCapturaGarantia").val()==1){
			  			window.location = 'CapturaGarantias.jsp?tab=1&cIdContratoDefinitivo='+aData[1];
			  		}else{
			  			if(aData[7]==0){
			  				$(this).removeClass('row_selected');  
			  				swal("Para liberar la garantía primero hay que realizar la captura de la misma.",{icon:"warning",button: "Cerrar"});
			  				return;
			  			}else{
			  				window.location = 'Garantias.jsp?tab=1&cIdContratoDefinitivo='+aData[1];	
			  			}
			  				
			  		}
		  		}
			});
			pestanaConsulta();
		});//fin del document ready
		function pestanaConsulta(){
			$.blockUI({message: "Procesando espere ......"});
			var object={tipoProceso:5,
				tipoOperacion:1
			};
			$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
				,data:object
				,dataType: 'json', success: 
					function(j){
						llenaCombo(j[0].catalogoAreaResponsable,"cIdUnidadEjecutora");
						$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
						searchContracts();
						$.unblockUI();
				}, error: function() {
					$.unblockUI();
				}
			});
		}
		function searchContracts(){
			var ue=" '' ";
			if($("#cIdUnidadEjecutora").val()!="0"){
				 ue=" '"+$("#cIdUnidadEjecutora").val()+"' ";
			}
			var funcion=" fn_mContratosConGarantia ";
			var cadCont="'"+$("#contratoSAI").val()+"'";
			
			
			$('#tblConsulta').dataTable().fnClearTable();
			oTableConsultaContratos = $("#tblConsulta").dataTable({
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
				bProcessing: true,
				sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (ue+","+cadCont) +")" ) ,
				sPaginationType: "full_numbers",
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "cEjercicioFiscal" },
					{ sName: "cIdContratoDefinitivo" },
					{ sName: "cNumContratoCNET" },
					{ sName: "cNumProcedimientoCNET" },
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial" },
					{ sName: "cConcepto" },
					{ sName: "tieneGarantiaCap", bVisible: false }
					
				]
				,fnInitComplete: function() {
					if($('#tblContratosAprobados >tbody >tr').length>0){
						oTableContratosAprovados.fnAdjustColumnSizing();
					}
				}
			});
		}		
	</script>
</head>
<body>
	<form id="formConsultaGarantias">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Consulta Contratos con Garantías</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" >
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="contratoSAI">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CV-A04-1/2024" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1/2024" aria-describedby="basic-addon1"  
							name="contratoSAI" id="contratoSAI"  />
						</div>
					</div>
				</div>
			</div>
			
			
		    <div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" id="buscarContratoGarantia" name="buscarContratoGarantia" 	value="Buscar"	onclick="searchContracts();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th >Ejercicio <br/>Fiscal</th>
									<th >Contrato SAI</th>
									<th >Contrato CNET</th>
									<th >Procedimiento CNET</th>
									<th >RFC</th>
									<th >Raz&oacute;n social</th>
									<th >Descripci&oacute;n</th>
									<th style="display: none;">Tiene Garantía Capturada</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    	
    </form>
</body>
</html>