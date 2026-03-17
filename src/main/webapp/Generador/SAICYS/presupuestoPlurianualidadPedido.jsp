<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Calendar c1 = Calendar.getInstance(); // today
		String today= sdf.format(c1.getTime());
		String cCentroContable="";
		String cUR = "";
		String cRamo = "";
		boolean bAplicadoCont=false;
		Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		String name_user=usuarioTab.getLogin();		
		Map rol =usuarioTab.getRoles();
		String role="";
		if (usuarioTab == null) {
			response.sendRedirect("../../index.jsp");
			return;
		}		
		
	
	String cEjercicio = "";
	String cIdPedido= "";
	String cIdUnidadEjecutora = "";
	String cIdPedidoDefinitivo = "";
	String cIdTipoCambio = "";
	String cITipoPedido = "";
	
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicioPlurianual) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicioPlurianual);
		cIdPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoPlurianual);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjecPlurianual);
		cIdPedidoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_PedidoPlurianualDefinitivo);
		cIdTipoCambio = (String)session.getAttribute(GestionInterface. ATT_tipoCambioPlurianualPedido);
		cITipoPedido = (String)session.getAttribute(GestionInterface. ATT_tipoContratoPlurianualPedido);
		System.out.println("tipo contrato " +cITipoPedido + cIdPedidoDefinitivo+ " "+cEjercicio+"  "+ cIdPedido   );
						
	}else 
		response.sendRedirect("PlurianualidadesPedidos.jsp?tab=0");
	
	
	
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>    
    <title>Presupuesto</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>	
	<script type="text/javascript" src="../js/catalogo/general.js"></script>	
	<script type="text/javascript" charset="utf-8">
				var oTableSuficiencia;
				var oTableClaves;
				var oTableCompromiso;
				$(document).ready(function() {		
				<%
					int aprobar=0;
				    int devolver=0;
					NegativaPestana NegPestana=new NegativaPestana();
					NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
					//botones
					NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
					
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						role=(String)r.getKey();
					}
					Map botones=nb.getBotones(role,"PlurianualidadPedidos","presupuestoPlurianualidadPedido");
					Iterator btn = botones.entrySet().iterator();
					while (btn.hasNext()) {
						Map.Entry b = (Map.Entry)btn.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%
						String img=(String) b.getValue();
						if ("imgAprobarPlurianualidadPedido".equals(img)){  
							aprobar=1;
						}
						if ("imgDevolverPlurianualidadPedido".equals(img)){
							devolver=1; 
						} 
					}

				%>	
				setReadOnly();
				headerQuery();
				initDataTable();
				validaCondicionesIniciales();
				loadClavesPresupuestalesContrato();
				loadEventClickHandlers();
				$("#mImporteTotal").formatCurrency();
			});	
			function validaCondicionesIniciales(){
				//solo cuando esta capturado el pedido se puede agregar EPs
				if (parseInt($("#nIdEstado").val(),10)== 3 || parseInt($("#nIdEstado").val(),10)== 4 )
					document.getElementById("imgDevolverPlurianualidadPedido").disabled = true;
				if (parseInt($("#nIdEstado").val(),10)> 1 ){
					document.getElementById("nIdClaveEgresosXPlurianualidadPedido").disabled = true;	
					document.getElementById("AgregarEPPlurianualidadPedido").disabled = true;
					document.getElementById("imgAprobarPlurianualidadPedido").disabled = true;
				}
			}
			function loadEventClickHandlers(){
				$('#dt_clavepresup tr').live('dblclick', function() {        
						$(oTableClaves.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});						
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTableClaves );						
						var aData = oTableClaves.fnGetData(anSelected[0]);							
						$("#epAUX").val(aData[0]);				
						if ($("#nIdEstado").val() == "1" ){
							 var index=($("#epAUX").val())
							  var tmp = index.lastIndexOf( "\." );
					          var uEje= index.substring( tmp - 3, tmp);
					            $("#cIdUnidadEjecutora").val(uEje)
								queryFormPost("quitaEPContratoPasivoPlurianualDelete", {async: false});
								loadClavesPresupuestalesContrato();
					 	}
				});	
			}
				function loadClavesPresupuestalesContrato(){
						if($("#R_NOMBRE").val()!='ADMIN_RECMAT'){
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
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
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
			function setReadOnly(){
				document.getElementById("lblUnidadEjecutora").style.readonly=true;
				document.getElementById("lblDefinitivo").style.readonly=true;
				document.getElementById("lblProveedor").style.readonly=true;
				document.getElementById("lblEstado").style.readonly=true;
				document.getElementById("lblTotal").style.readonly=true;
				document.getElementById("lblNotas").style.readonly=true;
			}
		function headerQuery(){
			queryFormPost("mContratoPlurianualidadCaratulaReadPedido", {async: false});
			queryFormPost("fnMontosContratoPresupuestoPlurianualReadPedido", {async: false});
			queryFormPost("cg_roleRead", {async: false});
			//obtiene el usuario de creacion del pedido original
			queryFormPost("getcIdUsuarioCreacion", {async: false});
			
			//verifica si hay motivo rechazo
			queryFormPost("motivoRechazoPasivo", { async:false });
			
			if($("#nIdEstado").val()!="5"){
			$("#trNotas").hide();
			}
			
		
			
		}
		function fnClickAddRowC() {
		     rowCount = $('#dt_clavepresup tr').length;
			 var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes(); 
			 	var vep = $('#ep').val();
				if (vep == '') 
					return ;
			   for(var i=0;i<aTrs.length;i++){
					// leer eps en tabla tPedidoEP_TMP y no inserta las que ya estan repetidas para evitar errores de primary key
				  queryFormPost("epsReadPedidoContratoPasivoPlurianual", {async: false});
				  if(vep == $("#epsConsulta").val()){
				 		alert("Ya existe esa clave ingrese otra..");
				  		return;
			   		}
				}
		      
				var tmp = vep.lastIndexOf( "\." );
				var uEje= vep.substring( tmp - 3, tmp);
				var cint = vep.substring( tmp -3 );
				$("#cIdUnidadEjecutoraEP").val(uEje);
				$("#ClaveInterna").val(cint);
				queryFormPost("agregaEPContratoCreatePasivoPlurianual", {async: false});
				loadClavesPresupuestalesContrato();
				$('#ep').val("");
			
		}
		
		
	
				
			function buscaClavePresupuestal()
			{
				window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() + '&cIdDocumento=' + $('#cIdPedido').val(), 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
				if ($.trim(document.FormContrato.ep.value)!=""){
				}
				return false;
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
			function guardaContrato(){
			 var imgAprobar='<%=aprobar%>';
			 if (imgAprobar==0){
					var proc=""+$("#cEjercicio").val()
						+","+$("#cContratoDefinitivo").val()
						//+","+$("#cIdSubPartida").val()
						+","+$("#cIdUnidadEjecutora").val()
						+","+$("#U_LOGIN").val()
						+","+$("#nTipoPago").val()+"";
						
        			 $.getJSON("../../servlet/PedidoPlurianualidadServlet?operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
						case "0": 
							alert("El PedidoPlurianual se ha aprobado y está en el SAI");
							queryFormPost("mContratoPlurianualidadCaratulaReadPedido", {async: false});
							document.getElementById("nIdClaveEgresosXPlurianualidadPedido").disabled = true;	
							document.getElementById("AgregarEPPlurianualidadPedido").disabled = true;
							document.getElementById("imgAprobarPlurianualidadPedido").disabled = true;
							var imgDevolver='<%=devolver%>';
							if (imgDevolver==0){
								document.getElementById("imgDevolverPlurianualidadPedido").disabled = false;
							}
							$("#nIdEstadoPed").val("2"); 
						break;
						case "1":  
							alert("Este PedidoPlurianual ya se encuentra aprobado");
						break;
						case "2":  
							alert("Este proveedor no se encuentra registrado en el sistema Financiero");
						break;
						case "3":  
							alert("No se han registrado EPs para este PedidoPlurianual");
						break;
						case "4":  
							alert("Ha ocurrido un error al registrar el PedidoPlurianual, por favor contacte a su administrador");
						break;
						case "5":  
							alert("Ha ocurrido un error al registrar las EPs, por favor contacte a su administrador");
						break;
						case "6","7":  
							alert("Ha ocurrido un error al actualizar el PedidoPlurianual, por favor contacte a su administrador");
						break;
	                 	}
				});
			 }
			
		}
		function devuelveContrato(){
			var imgDevolver='<%=devolver%>';
			if (imgDevolver==0){
			if(parseInt($("#nIdEstado").val(),10)<3 || parseInt($("#nIdEstado").val(),10)==5 ){
			 var proc=""+$("#cEjercicio").val()
						+","+$("#cContratoDefinitivo").val()
						+","+$("#cIdUnidadEjecutora").val()
						+","+$("#cIdContratoMat").val()+"";
						
				$.getJSON("../../servlet/PedidoPlurianualidadServlet?operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
						case "0":  
							alert("Se ha devuelto el PedidoPlurianual");
							queryFormPost("mContratoPlurianualidadCaratulaReadPedido", {async: false});
							document.getElementById("nIdClaveEgresosXPlurianualidadPedido").disabled = false;	
							document.getElementById("AgregarEPPlurianualidadPedido").disabled = false;
							var imgAprobar='<%=aprobar%>';
							 if (imgAprobar==0){
								document.getElementById("imgAprobarPlurianualidadPedido").disabled = false; 
							 }

							document.getElementById("imgDevolverPlurianualidadPedido").disabled = true;
							$("#nIdEstadoPed").val("1"); 
						break;
						case "1":  
							alert("No se puede regresar el PedidoPlurianual porque su estado no lo permite");
						break;
						case "2":  
							alert("Ha ocurrido un error al eliminar las EPs del Contrato, por favor contacte a su administrador");
						break;
						case "3":  
							alert("Ha ocurrido un error devolver el PedidoPlurianual, por favor contacte a su administrador");
						break;
						case "4":  
							alert("Ha ocurrido un error al cambiar el estado del PedidoPlurianual, por favor contacte a su administrador");
						break;
	                 	}
				});
			}
		}
		window.location='PlurianualidadesPedidos.jsp?tab='+2
	}
						
		/*function openGrid(){
			window.open('../MultiReporteGridSacel.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
		}*/
	</script>
</head>  
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form action="#" name="formContrato"> 
		<div id="container" class="container">
			<table align="left" width="750px">
				<tr>
					<td>
						<fieldset style="width:750px" >
							<legend>Partidas de la plurianualidad</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2"> 
							    			<img id="imgAprobarPlurianualidadPedido" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="guardaContrato();" />&nbsp;Aprobar 
											<img id="imgDevolverPlurianualidadPedido" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devuelveContrato();"/>&nbsp;Devolver
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="window.location = 'Plurianualidad.jsp?tab=0';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblDefinitivo" id="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	   	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>	
							    	
							    	<tr id="trNotas">
									<td align="left" colspan="2" >
										<label>Motivo del rechazo:</label>
										<textarea rows="3" cols="1" style="color:red; width: 500px;" name="lblNotas" id="lblNotas" readonly
											style="border-width:0; background-color:transparent"> </textarea>
											
									</td>
								</tr>
							    	
							    							    	
							    </table>
						</fieldset>
					</td>
				</tr>				
		    	<tr>
		    		<td>
		    			<fieldset style="width:750px" ><legend>Claves</legend>		    			
		    				<table align="left"  width="100%">
			    				<tr>
			    					<td style="width: 740px;">
				    					<table height="66" width="98%" border="0">
							                <tr> 
							                	<td>E.P.</td>
												<td>&nbsp;</td>
							                </tr> 
							                <tr> 
							                  <td><input type="text" id="ep" name="ep" readonly size="65" value=""></td> 
							                  <td><input type="button" name="nIdClaveEgresosXPlurianualidadPedido" id="nIdClaveEgresosXPlurianualidadPedido" size="5" value="..." onclick="buscaClavePresupuestal()" onblur="rellenaCampos();"></td>
							                  <td><input type="button" value="Agregar" name="Add2" id="AgregarEPPlurianualidadPedido"	onclick="fnClickAddRowC();"></td>
							                </tr>
							              </table>
			    					</td>
			    				</tr>	
			    				<tr>
			    					<td  style="width: 740px;">
				    					<table  id="dt_clavepresup" class="display">
											<thead>
												<tr align="left">
													<th>C&oacute;digo SAI</th> 
													<th>Clave SHCP</th> 
											 	</tr> 
											</thead>
												<tbody>
												</tbody> 
										</table>
			    					</td>
		    					</tr>
		    				</table>		
	    				</fieldset>				    
			         </td>              	
		    	</tr>
	        </table>
	        <!-- Hiddens de sesion -->
	        <input type="hidden" name="cIdContratoMat" id="cIdContratoMat" value="<%=cIdPedido%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	         <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	         <input type="hidden" name="cIdTipoCambio" id="cIdTipoCambio" value="<%=cIdTipoCambio%>"/>
		     <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%= cIdPedidoDefinitivo %>"   />
		     <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cITipoPedido%>"/>
		       
		    <input type="hidden" name="cIdPedido" id="cIdPedido" value="<%=cIdPedido%>"/>
		    <input type="hidden" name="cIdSubPartida" id="cIdSubPartida"/>
		     <input type="hidden" id="nOrden" name="nOrden" value=""/>
		    <input type="hidden" id="Partida" name="Partida" value="2"/>
			<input type="hidden" id="Partida2" name="Partida2" value="3"/>
			<input type="hidden" id="Partida3" name="Partida3" value="5"/>
			<input type="hidden" id="Partida1" name="Partida1" value="1"/>
			<input type="hidden" id="ClaveInterna" name="ClaveInterna" />
		    <!--  Auxiliares para consulta -->		    
	       	<input type="hidden" name="epAUX" id="epAUX" />
	       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuarioTab.getU_UR()%>" />
	       	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	    
		    <!-- Resultado de consultas -->
		     <input type="hidden" name="nIdEstado" id="nIdEstado" />
		    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		     <input type="hidden" name="cCentroContable" id="cCentroContable" />	
		    <input type="hidden" name="nTipoPago" id="nTipoPago" />
		     <input type="hidden" name="epsConsulta" id="epsConsulta" />	
		   	    
			    
	    </div>
	</form>
  </body>
</html>
