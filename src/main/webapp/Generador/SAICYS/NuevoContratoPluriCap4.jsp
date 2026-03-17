<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
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
	
 %>
 <!DOCTYPE html>
<html>
  <head>
   	<meta charset="UTF-8">
    <title>Nuevo</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	
	<script type="text/javascript">
		var oTable;
		$(document).ready(function() {
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"ContratoPlurianualCap4","NuevoContratoPluriCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()){
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			tabb=0;	
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			initTable();
			showAndHideTabs();
			initQuerys();
			$("#btnBuscarContratoCap4").button().click(function(){
				searchContracts();
			});
			$('#tblSearchContract').on('dblclick','tr', function() { 
		        
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTable );
				if (anSelected != "") {
					var aData = oTable.fnGetData(anSelected[0]);
					$("#cIdContratoDefinitivo").val(aData[2]);
					swal({
						title: "",
						text: "¿Seguro que desea migrar el contrato?",
						icon: "info",
						buttons: {
							confirm : "Aceptar",
							cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
						}else{
							guardar();
						}
					});
					
				}
				
			});
		});//Fin del document ready
		function initQuerys(){
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback:function(){
					searchContracts();	
				}	
			});
			querySelectPost("tipoActividadEconomica", "actEconomContratoPluriCap4", {async: false});
		}
		function guardar(){
			$.ajax({url: "../../servlet/ContratoCap4Servlet" , type:'post' , async: false
			,data:'operacion=16&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
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
							window.location = "ContratoPlurianualCap4.jsp?tab=2";
						}
					});
				}
			});
		}
		function initTable(){
			oTable=$("#tblSearchContract").dataTable({
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					aoColumns: [
						{ sName: "url",bVisible: false },
						{ sName: "cIdUnidadEjecutora" },
						{ sName: "cIdContratoDefinitivo" },
						{ sName: "actEconomica" },
						{ sName: "cDescripcion" },
						{ sName: "cIdUsuarioCreacion" },
						{ sName: "cIdRFC" },
						{ sName: "cRazonSocial"}
						
					],
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}			
	    	});
		}
		function searchContracts() { 
			$.blockUI({message: "Procesando espere ......"});
			
		    var zTabla = "CONTRATOS_PLURIANUALES_CAP4";		
			var campos = "'"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdContratoDef").val()+"','"+$("#cnumCompranet").val()+"'";
		    oTable.fnClearTable();
		    $('#tblSearchContract').dataTable().fnClearTable();
		    oTable=$("#tblSearchContract").dataTable({
		    	sScrollX: "100%",
				bAutoWidth : true,
				bPaginate:false,
				bFilter : false,
				bDestroy:true,
				bRetrive:true,
				bLengthChange: false,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:"
					
				},
				bProcessing: true,
				bJQueryUI: true,
				aaSorting: [[ 1, "desc" ]] ,
				aoColumns: [
					{ sName: "url",bVisible: false },
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "cIdContratoDefinitivo" },
					{ sName: "actEconomica" },
					{ sName: "cDescripcion" },
					{ sName: "cIdUsuarioCreacion" },
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial"}
				]			
	    	});
		    var  elParametro2 ='';		    
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: zTabla, Param: elParametro2,Campos:campos, MaxReg: "10", ajax: 'false'}, function(j){		    							
				arrayCompleto=new Array();
				for (var i = 0; i < j.length; i++){
					arrayCompleto [i]=[ j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5 
						,j[i].Col6,j[i].Col7,j[i].Col8
						];
				}
				if(j.length>0){
					$('#tblSearchContract').dataTable().fnAddData(arrayCompleto);
					$("#tblSearchContract").dataTable().fnAdjustColumnSizing();
				}
				$.unblockUI();
			});	
			
		}
	</script>
  </head>
  
  <body>
  	<form id="formNuevoContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Migrar Contratos Plurianuales Cap&iacute;tulo 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
							<option value="<%=usuario.getU_UR()%>" selected="selected">
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cnumCompranet">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de contrato SAI, ejemplo PLU-CF-A04-1/2023" aria-label="Objeto del contrato" aria-describedby="basic-addon1"  name="cIdContratoDef" id="cIdContratoDef"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cnumCompranet">No. Contrato CNET: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de contrato CNET" aria-label="Objeto del contrato" aria-describedby="basic-addon1"  name="cnumCompranet" id="cnumCompranet"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscarContratoCap4" name="btnBuscarContratoCap4" 	value="Buscar"	 />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblSearchContract" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">url</th>
									<th >Unidad Ejecutora</th>
									<th >Contrato Definitivo</th>
									<th >Tipo Contrato</th>
									<th >Descripci&oacute;n</th>
									<th >Usuario Creador</th>
									<th >RFC</th>
									<th >Raz&oacute;n social</th>
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
    	<input type="hidden"  id="isPlurianual" name="isPlurianual" value="0"/>
    	<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
    	<input type="hidden" name="nIdEstado" id="nIdEstado" value="1"/>
    	<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value=""/>
    	
    </form>
  </body>
</html>
 