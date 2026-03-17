<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab1.getLogin();
	String role="";
	Map rol =usuarioTab1.getRoles();
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Consulta Plurianualidad</title>
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
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" charset="utf-8"><!--
	
		var cEjercicio;
		var oTable;
			$(document).ready(function() {
			deshabilitaTabs();
		<%
				int tabla=0;
				int consulta=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map pestanas=ebl.getPestana(role,"PasivoContrato");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana=(String)e.getValue();
					if ("presupuestoPasivoContrato".equals(pestana)){
						 tabla=1;
					}
					if ("consultaPasivoContrato".equals(pestana)){
						 consulta=1;
					}
				} 
				Map botones=nb.getBotones(role,"PasivoContrato","consultaPasivoContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}
				%> 
				
				var consulta='<%=consulta%>';
				var tabla='<%=tabla%>';
				if (consulta==0){
					oTable=	$('#tblconsultaPasivosContrato').dataTable({
						"bFilter" : false,
						"bDestroy" : true,
						"bJQueryUI": true,
						"bAutoWidth" : true,
						sScrollY: "300px",
				        sScrollX: "100%",
				        sScrollXInner: "200%",
				        bScrollCollapse: true,
						"iDisplayLength": 5, //Cuantos registros se despliegan
						"sPaginationType": "full_numbers",
						"oLanguage": {
								sProcessing: "Procesando...",
								sLengthMenu: "Mostrar _MENU_ registros <h5></h5>",
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
							bServerSide: true,
							aaSorting: [[ 0, "asc" ],[ 1, "asc" ],[2,"asc"]] ,			
							aoColumns: [
							{ sName: "cVinculo", bVisible: false },
							{ sName: "cIdContratoDefinitivo1" },
							//{ sName: "cIdSubpartida1" },
							{ sName: "cConceptoContrato" },
							{ sName: "cEstado" },
							{ sName: "cIdUnidadEjecutora1" },
							{ sName: "cIdRFC" },
							{ sName: "mMontoBruto" },
							{ sName: "nPorcentajeIVA1" },
							{ sName: "mMontoPorPagar1" },
							{ sName: "mMontoNeto1" },
							{ sName: "tipoMoneda" },
							{ sName: "boton1" },
							{ sName: "boton2" },
							{ sName: "nIdEstado1" }

								
							]
						});   	
				}
		
	        	
			
				querySelectPost("UnidadBusca2", "cIdUnidadEjecutora", {async: false });
			//	if (consulta==0){
					mostrar();
		//		}

				});	

		
		$('#tblconsultaPasivosContrato tr').live('dblclick',function() {
			var tabla='<%=tabla%>';
				if (tabla==0){
					if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');

				  var anSelected=fnGetSelected(oTable)
				  var aData=oTable.fnGetData(anSelected[0]);
				  habilitaTabs()
				  window.location = aData[0];
				}
			});


		        function deshabilitaTabs(){
		     			$( "#presupuestoPasivoContrato" ).attr("disabled", true);
						$( "#precompromisoPasivoContrato" ).attr("disabled", true);
						
				}
				
				function habilitaTabs(){
				  		$( "#presupuestoPasivoContrato" ).attr("disabled", false);
						$( "#precompromisoPasivoContrato" ).attr("disabled", false);
									
				}
 
		
			function mostrar() { 
				var consulta='<%=consulta%>';
				if (consulta==0){
					var qw = " 1=1 ";
				
				if($("#cIdUnidadEjecutora").val()!='0'){
					qw += " and cIdUnidadEjecutora='" + $("#cIdUnidadEjecutora").val()+"'";
				}
				  if($("#cIdDefinitivo").val()!=""){
				     qw+=" and  cIdContratoDefinitivo LIKE '%25"+$("#cIdDefinitivo").val()+"%25'";
				    }
				    /*
				   if($("#cIdSubPartida").val()!=""){
				     qw+=" and  cIdSubPartida LIKE '%25"+$("#cIdSubPartida").val()+"%25'";
				    } 
				    */
				    
	
				oTable=	$('#tblconsultaPasivosContrato').dataTable({
					"bFilter" : false,
					"bDestroy" : true,
					"bJQueryUI": true,
					"bAutoWidth" : true,
					sScrollY: "300px",
				     sScrollX: "100%",
				    sScrollXInner: "200%",
				    bScrollCollapse: true,
					"iDisplayLength": 5, //Cuantos registros se despliegan
					"sPaginationType": "full_numbers",
					"oLanguage": {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros <h5></h5>",
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
						bServerSide: true,
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_consultaPasivoContrato&qw=" + qw,
						aaSorting: [[ 0, "asc" ],[ 1, "asc" ],[2,"asc"]] ,			
						aoColumns: [
							{ sName: "cVinculo", bVisible: false },
							{ sName: "cIdContratoDefinitivo1" },
							//{ sName: "cIdSubpartida1" },
							{ sName: "cConceptoContrato" },
							{ sName: "cEstado" },
							{ sName: "cIdUnidadEjecutora1" },
							{ sName: "cIdRFC" },
							{ sName: "mMontoBruto" },
							{ sName: "nPorcentajeIVA1" },
							{ sName: "mMontoPorPagar1" },
							{ sName: "mMontoNeto1" },
							{ sName: "tipoMoneda" },
							{ sName: "boton1" },
							{ sName: "boton2" },
							{ sName: "nIdEstado1" }

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




	              function guardarPasivoContrato(indice){
	          
		           if( parseInt($("#Estado_"+indice+"").val(),10)> 1){
		           alert("No se puede guardar El pasivo, su Estado no lo Permite ")
		           return
		           }
	        
		          if(parseFloat($("#nporcIVA"+indice+"").val())> 100 || parseFloat($("#nporcIVA"+indice+"").val()) < 0 ){
		          alert("El Iva no puede ser Mayor a 100 o Menor a 0");
		          mostrar();
		          return;
		         }
		          
		          
		          
		          if(parseFloat($("#montoNeto_"+indice+"").val()) > parseFloat($("#mMontoPorPagar_"+indice+"").val())){
		          alert("El Monto Neto no puede ser mayor al Monto por Pagar");
		          mostrar();
		          return;
		            }
		          
		          
		          
		          if(confirm("\xBFEstas seguro de actualizar los datos de contrato definitivo "+$("#cIdContratoDefinitivo_"+indice+"").val()+ " "+  "de la Unidad Ejecutora"+" "+ $("#cIdUnidadEjecutoraConsultaPasivo_"+indice+"").val()+"?")){
				   $("#contratoDefinitivo").val($("#cIdContratoDefinitivo_"+indice+"").val());
				   $("#montoIva").val($("#nporcIVA"+indice+"").val());
				   $("#montoTotal").val($("#montoNeto_"+indice+"").val());
				   $("#mTipoCambio").val($("#nidtipoCambio_"+indice+"").val());
				//   $("#cIdSubPartida").val($("#cIdSubpartida_"+indice+"").val());
				   $("#cUnidadEjecutoraPasivo").val($("#cIdUnidadEjecutoraConsultaPasivo_"+indice+"").val());
				   queryFormPost("actualizaConsultaPasivoContrato",{async:false});
			       window.location='Pasivos.jsp?tab=1'
				   return;
				      
		         }
	        	
			}
			
			
			
			
			
	              function eliminaPasivoContrato(indice){
	          
		           if( parseInt($("#Estado_"+indice+"").val(),10)> 1){
		           alert("No se puede Eliminar El pasivo, su Estado no lo Permite ")
		           return
		           }
	        
		         
		          
		          if(confirm("\xBFEstas seguro de Eliminar los datos de contrato definitivo "+$("#cIdContratoDefinitivo_"+indice+"").val()+ " "+  "de la Unidad Ejecutora"+" "+ $("#cIdUnidadEjecutoraConsultaPasivo_"+indice+"").val()+"?")){
				   $("#contratoDefinitivo").val($("#cIdContratoDefinitivo_"+indice+"").val());
				   $("#cUnidadEjecutoraPasivo").val($("#cIdUnidadEjecutoraConsultaPasivo_"+indice+"").val());
				   queryFormPost("eliminaConsultaPasivoContrato",{async:false});
			       window.location='Pasivos.jsp?tab=1'
				   return;
				      
		         }
	        	
			}
			
			
			
			
			
			
	        
	</script>
</head>

  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
  	
		  	 <input type="hidden" id="contratoDefinitivo" name="contratoDefinitivo" size="10" value="-1" /> 
		     <input type="hidden" id="montoIva" name="montoIva" size="10" /> 
		     <input type="hidden" id="montoTotal" name="montoTotal" size="10" /> 
		     <input type="hidden" id="mTipoCambio" name="mTipoCambio" size="10" /> 
		     <input type="hidden" name="cIdSubPartida" id="cIdSubPartida" />
  		     <input type="hidden" name="cUnidadEjecutoraPasivo" id="cUnidadEjecutoraPasivo" />
		    
		   
  		<fieldset >
  			<legend>Consulta Pasivos</legend>
		  		<div id="container" class="container">	
				    <table align="left" width="90%">
				    	<tr>
				    		<td>
				    			<table align="left" cellpadding="2" width="750px">
									<tr>	
										<td>Unidad Ejecutora</td>
										<td>
											<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"style="width: 50em;">
												<option value="<%=usuarioTab1.getU_UR()%>" selected="selected">
											</select>	
										</td>
									</tr>	
									<tr >
										<td>Contrato Definitivo</td>
										<td>
											<input type="text" name="cIdDefinitivo" id="cIdDefinitivo" style="width: 50em;" />	
										</td>
									</tr>
									
									
									<tr>
							    		<td colspan="2" align="center"><input type="button"  name="btnBuscar" id="btnBuscar" value="Buscar"  onclick="mostrar();"/></td>
							    	</tr>
							    </table> 
				    		</td>
				    	</tr>
				    	
				    	<tr  >
				    		<td style="width: 740px; height: 120px" >
							     <table align="left" id="tblconsultaPasivosContrato" width="740px" class="display">
						        	<thead>
						        		<tr>
						        			<th></th>
						        			<th>Contrato Definitivo</th>
						        			<th>Descripcion</th>
						        			<th> Estado</th>
						        			<th> Unidad</th>			
						        			<th>R.F.C. Del Proveedor</th>
						        			<th>Monto Bruto</th>
						        			<th> % IVA</th>
						        			<th>Monto Por pagar</th>
						        			<th>Monto Neto</th>
						        			<th>Tipo de Cambio</th>
						        			<th></th>
						        			<th></th>	
						        			<th></th>				
					
						        		</tr>
						        	</thead> 
						        </table> 
				    		</td>
				    	</tr>
				    	
				    	<tr>
				    		<td>
							    <table align="left" width="80%">
						        	<tr>
						        		<td><input type="hidden" name="cEjercicio" id="cEjercicio" /></td>
						        		    	</tr>
						        	<tr>
						        		<td><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/></td>
						        	</tr>
						        </table>
				    		</td>
				    	</tr>
				    	
				    </table>
			    </div>
  		</fieldset>
	</form>
  </body>
  
</html>
