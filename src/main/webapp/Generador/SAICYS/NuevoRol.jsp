<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuarioNR = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
    String login=usuarioNR.getLogin();
    if (usuarioNR == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
  
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Nuevo Rol</title>
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
			querySelectPost("RolesRead", "rol", {async : false});
			querySelectPost("RolesRead", "sltCopiarPermisos", {async : false});
			document.getElementById("lblDescripcion").style.readonly=true;
			var select=$('#rol option:selected').val();
			var opciones=select.split("|");			
			$("#idRol").val(opciones[0]);
			var aux=opciones[1];
			$("#lblDescripcion").val(aux);
			queryFormPost("UsuariosRead", {async : false});
			cargaOpciones();
			CargaRol();		    
			
			$("#btnEliminar").button().click(function(){
				if (confirm("¿Está seguro que desea eliminar el rol "+ $("#idRol").val() +" ? \n Esta acción no puede revertirse")) 
					{
						try{
							queryFormPost("mRolesDelete", { async:false });
						}catch(e){
							alert("No es posible eliminar");
						}
						window.location = "Roles.jsp?tab=0";							
					}
			});	
			EliminaRol();
			///// evento de tablas		
			$('#tblOpcionesDisp tr').live('click', function() {         
			if ( $(this).hasClass('row_selected') )             
				$(this).removeClass('row_selected');         
			else            
				$(this).addClass('row_selected');
				var aTrs = $('#tblOpcionesDisp').dataTable().fnGetNodes();  
				for ( var i=aTrs.length ; i>=0; i-- )     
				{         
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{             
						var nTr = $('#tblOpcionesDisp').dataTable().fnGetData(aTrs[i]);   							
						$('#tblOpcionesDisp').dataTable().fnDeleteRow( i );	
						var valor = nTr[0];
						var select=$('#rol option:selected').val();
						var opciones=select.split("|");			
						$("#idRol").val(opciones[0]);
						var valor2=$("#idRol").val();
						$("#r_nombre").val(valor2);
						$("#id_opcion").val(valor);
						queryFormPost("mRoleOpcionInsert", {async : false});
					}     
				}
				cargaOpciones();
				CargaRol();
			});
			
			$('#tblOpcionesSelec tr').live('click', function() {         
			if ( $(this).hasClass('row_selected') )             
				$(this).removeClass('row_selected');         
			else            
				$(this).addClass('row_selected');
				var aTrs = $('#tblOpcionesSelec').dataTable().fnGetNodes();  
				for ( var i=aTrs.length ; i>=0; i-- )     
				{         
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{             
						var nTr = $('#tblOpcionesSelec').dataTable().fnGetData(aTrs[i]);   							
						$('#tblOpcionesSelec').dataTable().fnDeleteRow( i );	
						var valor = nTr[0];
						var select=$('#rol option:selected').val();
						var opciones=select.split("|");			
						$("#idRol").val(opciones[0]); //nombre del rol
						var valor2=$("#idRol").val(); //nombre del rol
	
						$("#r_nombre").val(valor2); //nombre el rol
						$("#id_opcion").val(valor); // id opcion
						queryFormPost("mRoleOpcionDelete", {async : false});
					}     
				}
				cargaOpciones();
				CargaRol();
				
			});
			
			$("#btntblOpcionesSelecNeg").button().click(function(){				
						var aTrs = $('#tblOpcionesSelec').dataTable().fnGetNodes();								
            			for ( var i=aTrs.length-1 ; i>=0; i-- )     
						{         									
						    var nTr = $('#tblOpcionesSelec').dataTable().fnGetData(aTrs[i]);										
							var valor = nTr[0];
							var select=$('#rol option:selected').val();
							var opciones=select.split("|");			
							$("#idRol").val(opciones[0]); //nombre del rol
							var valor2=$("#idRol").val(); //nombre del rol
							$("#r_nombre").val(valor2); //nombre el rol
							$("#id_opcion").val(valor); // id opcion
							queryFormPost("mRoleOpcionDelete", {async : false});
						}	
            			
            			EliminaRol();
				});
			
			$("#btntblOpcionesDispPermitir").button().click(function(){							
						var aTrs = $('#tblOpcionesDisp').dataTable().fnGetNodes();	
						
            			for ( var i=aTrs.length-1; i>=0; i-- ){                
								var nTr = $('#tblOpcionesDisp').dataTable().fnGetData(aTrs[i]);	
								var valor = nTr[0];
								var select=$('#rol option:selected').val();
								var opciones=select.split("|");			
								$("#idRol").val(opciones[0]); 
								var valor2=$("#idRol").val(); 
								$("#r_nombre").val(valor2); 
								$("#id_opcion").val(valor); 
								queryFormPost("mRoleOpcionInsert", {async : false});     
						}
						cargaOpciones();
						CargaRol();
			});
		});
		
		function guardarRol () {
		
		
			$("#existe").val("");
			 if (document.getElementById("rolNombre").value!="" && document.getElementById("rolDescripcion").value!="") {
			 
			 
			 
				 	queryFormPost("mRoleExisteRead", {async: false});
				 	if ($("#existe").val()=='existe'){ //SI VA
				 		alert("El rol que desea agregar ya existe.");
				 		return;
				 	}else{
				 	
				 	
				 		var nombreNRol = $("#rolNombre").val();
				 		var descNRol = $("#rolDescripcion").val();
				 		var duenioNRol = $("#u_login").val();
				 		var url = "../../AgregarRolServlet?nombre=" + nombreNRol + "&desc="+ descNRol +"&duenio="+duenioNRol;
				 		
				 		
				 		$('input[name=chkCopiarPermisos]').each(function(){
				  			if (this.checked){  
				  				var opciones = $("#sltCopiarPermisos").val().split("|");
				  				//alert("opciones: "+opciones);
				  				$("#rolBuscar").val(opciones[0]);
				  				//alert("opciones[0]: "+opciones[0]);
				  				
				  				var buscarNRol = $("#rolBuscar").val();
				  				
				  				url += "&ConOpciones=true&rolBuscar="+buscarNRol;
				  				
				  				$.ajax({
										url: url, 
										type:'post', 
										async: false, 
										data:'operacion=2', 
										dataType: 'json', 
										//Si el ajax fue success
										success: function(json){
											alert("El nuevo rol se ha creado.");
										}
									});
				  				
				  			}
				 			else{
				 				if(confirm("Se creara el Rol con todas las propiedades existentes,¿Desea continuar?.")){
	
				 					$.ajax({
										url: url, 
										type:'post', 
										async: false, 
										data:'operacion=2', 
										dataType: 'json', 
										//Si el ajax fue success
										success: function(json){
											alert("El nuevo rol se ha creado.");
										}
									});
				

				 				}
				 				else
				 					return;
				 			}
				  		});
				  		
						var rol=document.getElementById("rolNombre").value;
						
						
				 	}
					
					window.location = "Roles.jsp?tab=0";
			 } else  //SI VA
			 	alert("Favor de revisar los campos antes de continuar.");
			 	
			 	
		};
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function cargaOpciones(){
			var select=$('#rol option:selected').val();
			var opciones=select.split("|");			
			$("#idRol").val(opciones[0]);
			var role=$("#idRol").val();
				$('#tblOpcionesDisp').dataTable(
		 		 {         
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mAccesoOpcionesDisponibles('"+role+"')",					                                                                                                                              
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "ID_OPCION"},
					{ sName: "O_DESCRIPCION"},
					{ sName: "menu"}
					
					]
				});
		}
		
		function EliminaRol(){
			var select=$('#rol option:selected').val();
			var opciones=select.split("|");			
			$("#idRol").val(opciones[0]);
			var aux=opciones[1];
			$("#lblDescripcion").val(aux);	
			// validar que es posible eliminar el rol
			queryFormPost("UsuariosRead", {async : false});				
			if ($("#totalUser").val()!=0){
				$("#btnEliminar").attr("disabled", true);
			}else{
				$("#btnEliminar").attr("disabled", false);
			}
			CargaRol();
			cargaOpciones();
		}
		function CargaRol(){		
			var where= " 1=1 "
			where+=" and R_NOMBRE='"+$("#idRol").val()+"'";
			var idrol=$("#idRol").val();
			$('#tblOpcionesSelec').dataTable({         
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
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mOpcionesSeleccionadas&qw="+ where,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "ID_OPCION"},
					{ sName: "O_DESCRIPCION"},
					{ sName: "menu"}
					]
				});		
		}
		function mostrarSltCopiarPermisos(){
			$('input[name=chkCopiarPermisos]').each(function(){
	  			if (this.checked)
					$("#copiarPermisosD").css("display","block");	
	 			else
	 				$("#copiarPermisosD").css("display","none");
	  		});
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form id="frmRole" name="frmRole" action="" method="POST"> 
  		<fieldset>
  			<legend>Roles</legend>
				<table border="0" width="100%">
	
					<tr>
						<td align="left">Role:</td>
						<td align="left"> <input type="text" name="rolNombre" id="rolNombre" /></td>
					</tr>
					<tr>
						<td align="left">Descripci&oacute;n:</td>
						<td align="left"><textarea name="rolDescripcion" rows="6" cols="72" id="rolDescripcion" onkeypress="textCounter(this,2000);"></textarea></td>
					</tr>
					<tr>
						<td align="left"><input type="checkbox" id="chkCopiarPermisos" name="chkCopiarPermisos" onClick="mostrarSltCopiarPermisos()">&nbsp;&nbsp;Copiar Permisos</td>
						<td align="left"><div id="copiarPermisosD" style="display:none;"><select id="sltCopiarPermisos" name="sltCopiarPermisos"></select></div></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><label class="validateTips ui-state-error"></label></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><button name="btnGuardar" id="btnGuardar" onclick="guardarRol();">Guardar</button></td>
					</tr>		                      				
				</table>
				 <input type="hidden" name="idRol" id="idRol"/>
				 <input type="hidden" name="rolBuscar" id="rolBuscar"/>
				 <input type="hidden" name="u_login" id="u_login" value="ADMIN_RECMAT" />  
  		</fieldset>		
  		<fieldset>
  			<legend>Roles</legend>
				<table border="0" width="100%">
					<tr>
						<td>Role:</td>
						<td align="left"> <select id="rol" name="rol" style="width: 40em;" onchange="EliminaRol();"></select>
						<input type="hidden" name="idRol" id="idRol"/></td>
					</tr>
					<tr>
				    	<td align="left" colspan="2"><input type="text" style="width: 700px" id="lblDescripcion" name="lblDescripcion" readonly style="border-width:0; background-color:transparent"/></td>
			    	</tr>
					<tr>
						<td colspan="2" align="center"><label class="validateTips ui-state-error"></label></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><button name="btnEliminar" id="btnEliminar">Eliminar</button>
														
						<input type="hidden" name="totalUser" id="totalUser"/> 
				 		<input type="hidden" name="totalOpcion" id="totalOpcion"/>
				 		<input type="hidden" name="r_nombre" id="r_nombre"/> 
				 		<input type="hidden" name="id_opcion" id="id_opcion"/>
				 		<input type="hidden" name="existe" id="existe"/>  
				 </td>
						
					</tr>						
				</table> 
			<fieldset>
				<legend>Opciones disponibles</legend>
				<table border="0" align="left" >
					<tr>
						<td colspan="3">
							<table id="tblOpcionesSelec" class="display">
					            <thead>
					                <tr>
					                	<th>Id</th>
					                    <th>Descripción</th>
					                    <th>Descripción</th>
					                </tr>
					            </thead>
					        </table>
						</td>						
					</tr>
					<tr>														
						<td colspan="2" align="left">					
							<button id="btntblOpcionesSelecNeg">Negar Todo</button> 
						</td>						
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Opciones Negadas</legend>
				<table border="0" align="left" >
					<tr>
						<td colspan="3">
							<table id="tblOpcionesDisp" class="display">
					            <thead>
					                <tr>
					                	<th>Id</th>
					                    <th>Descripción</th>
					                    <th>Descripción</th>
					                </tr>
					            </thead>
					        </table>
						</td>						
					</tr>
					<tr>														
						<td colspan="2" align="left">					
							<button id="btntblOpcionesDispPermitir">Permitir Todo</button> 
						</td>						
					</tr>
				</table>
			</fieldset>

  		</fieldset>
	</form>
  </body>
</html>
