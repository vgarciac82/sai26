<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String user_login=usuario.getLogin();
    String roles="";
    Map rol =usuario.getRoles();
    String cadena="";
    if (session.getAttribute(GestionInterface.ATT_pDefinitivo) != null) {
    	cadena = (String)session.getAttribute(GestionInterface.ATT_pDefinitivo);
    }

	String cEjercicio = "";
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";	
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
		cIdTipoPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_PedidoConsecutivo);		
	}else 
		response.sendRedirect("Pedidos.jsp?tab=0");   
    
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Clausulas</title>
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
		<script>
			$(document).ready(function() {
				
				var cadena='<%=cadena%>';
				var lbl=cadena.split("_");
				var idPedido=lbl[0];
				/// obtener el rfc para mostrar acta constitutiva o acta de nacimiento
				$("#cPedidoDefinitivo").val(idPedido);
				queryFormPost("obtieneRFC",{async:false});
				
				$("#rfcPedido").val($("#rfcPedido").val().substring(3,4));
				
				if($("#rfcPedido").val()=="-"){
					//muestra persona moral 0=persona moral, 1= persona fisica
					$("#esFisica").val('0');
					
					document.getElementById("personaMoral").style.display="block";
				}else{
					//muestra persona Fisica
					
					$("#esFisica").val('1');
					document.getElementById("personaFisica").style.display="block";
				}
				
				/////////////////////////////////////////////////////////////
				
				
				var modo =lbl[1];
				if (modo=="edit"){
						if($("#esFisica").val()=='0'){
            				//persona moral
            				queryFormPost("mClausulaEditRead",{async:false});
            			}else{
            				//persona fisica
            				queryFormPost("mClausulaEditReadFisica",{async:false});
            			}
					
					
				}
				
				if (modo=="nuevo"){
					if($("#esFisica").val()=='0'){
            				//persona moral
            				queryFormPost("mClausulaInfRead",{async:false});
            			}else{
            				//persona fisica
            				queryFormPost("mClausulaInfReadFisica",{async:false});
            			}
				}
				
				
				document.getElementById("fFormalizacion").style.readonly=true;
				$("#fFormalizacion").val($("#fFormalizacionH").val());
				$("#fFormalizacionFisica").val($("#fFormalizacionH").val());
				
				if (modo=="edit"){
					 query="fn_mclausulasPedidoReporteSeleccionadas('"+idPedido+"')";
				}
				if (modo=="nuevo"){
					 query="v_clausulasPedidoReporte";
				}
				
				
				//tabla de clausulas
				$('#tblClausulas').dataTable({         
					bAutoWidth : true,
					bDestroy: true,		
					iDisplayLength: 25,
					oLanguage: {
						
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						//aLengthMenu: [[]],
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
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "checkbox" },
						{ sName: "nIdClausula", bVisible: false },
						{ sName: "nClausula" },
						{ sName: "cClausula" }
					]
				});
				
				
				// tabla fundamentos
				if (modo=="edit"){
					 query="fn_mfundamentosPedidoReporteSeleccionadas('"+idPedido+"')";
				}
				if (modo=="nuevo"){
					 query="v_fundamentosPedido";
				}
				$('#tblFundamentos').dataTable({         
				bAutoWidth : true,
				bDestroy: true,
			//	sScrollX: "100%",
				iDisplayLength: 15,
				oLanguage: {
					sProcessing: "Procesando...",
				//	sLengthMenu: "Mostrar _MENU_ registros",
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
					 bProcessing: true,
				  	 sPaginationType: "full_numbers",
				 	 bJQueryUI: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
						{ sName: "checkbox" },
						{ sName: "idFundamento", bVisible: false },
						{ sName: "fundamento" }
					]
				});
				
				$("#cFechaAutorizacion").datepicker({
					beforeShowDay: nonWorkingDates,
					dateFormat: "dd/mm/yy",
					 altField: "#actualDate",
					 currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
				    buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});
				$("#cFechaAutorizacionFisica").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					 altField: "#actualDate",
					 currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
				    buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});
				$("#fFormalizacion").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					 altField: "#actualDate",
					 currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
				    buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
					});
				$("#fFormalizacionFisica").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					 altField: "#actualDate",
					 currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
				    buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
					});
				
				
				$("#btnGuardarNuevoClausula").button().click(function(){
					if (modo=="edit"){
						queryFormPost("deleteClausulas",{async:false});
						queryFormPost("deleteRepresentante",{async:false});
						queryFormPost("deleteFundamentos",{async:false});
						
					}
					var vclausulas=new Array();
					$('input[name=clausulas]').each(function(){
	  					if (this.checked){
							vclausulas.push($(this).val());
	 					 }
					});
					var pos=vclausulas.length;
					var vector
					var posicion=["PRIMERA","SEGUNDA","TERCERA","CUARTA","QUINTA","SEXTA","SEPTIMA","OCTAVA","NOVENA","DECIMA","DECIMA PRIMERA","DECIMA SEGUNDA","DECIMA TERCERA","DECIMA CUARTA","DECIMA QUINTA","DECIMA SEXTA","DECIMA SEPTIMA","DECIMA OCTAVA","DECIMA NOVENA", "VIGESIMA", "VIGESIMA PRIMERA", "VIGESIMA SEGUNDA"];
						var aTrs = $('#tblClausulas').dataTable().fnGetNodes();	
            			for ( var i=aTrs.length-1 ; i>=0; i--){ 	 	
            				 var nTr = $('#tblClausulas').dataTable().fnGetData(i);	
            				// var id=nTr[0];
            				var id=nTr[1];
            				 for (var j=0;j<=vclausulas.length;j++){ 
            					if (id==vclausulas[j]){
            						vector=posicion[pos-1];
            						$("#clausula").val($("#cClausula_"+id).val());
            						$("#cIdClausula").val(id);        						
            						$("#cNo").val(vector);
            						queryFormPost("mPedidoClausulaInsert", {async : false});
            						pos=pos-1;
            					 }
            				 } 
					   }
            			// fundamentos
            			var vfundamentos=new Array();
						$('input[name=fundamentos]').each(function(){
	  						if (this.checked){
							    vfundamentos.push($(this).val());
	 					 	}
						});
						var aTrs = $('#tblFundamentos').dataTable().fnGetNodes();	
            			for ( var i=aTrs.length-1 ; i>=0; i--){ 	
            				 var nTr = $('#tblFundamentos').dataTable().fnGetData(i);	
            				 var id=nTr[1];
            				 for (j=0;j<=vfundamentos.length;j++){
            					if (id==vfundamentos[j]){
            						$("#fundamento").val($("#fundamentos_"+id).val());
            						$("#IdFundamento").val(id);
									queryFormPost("mPedidofundamentosInsert", {async : false}); 
            					 }			       
            				 } 
					   }
            			
            			// guardar datos de representante
            			//muestra persona moral 0=persona moral, 1= persona fisica
						queryFormPost("mClavePedido",{async:false});
					
            			if($("#esFisica").val()=='0'){
            				if($("#ep").val()==''){
            					$("#ep").val($("#EpGeneral").val());
            				}
            				//persona moral
            				queryFormPost("mPedidoRepresentanteInsert", {async : false}); 
            			}else{
            				if($("#epFisica").val()==''){
            					$("#epFisica").val($("#EpGeneral").val());
            				}
            				//persona fisica
            				queryFormPost("mPedidoRepresentanteInsertFisica", {async : false}); 
            			}
            			
            			// actualizar la fecha de formalizacion si esta fue modificada
            			if ($("#fFormalizacionH").val()!=$("#fFormalizacion").val()){
            				queryFormPost("mPedidoUpdateFecha", {async : false});
            				queryFormPost("mPedidoUpdateFechaProcedimiento", {async : false});
            			}
            			
            			if ($("#fFormalizacionH").val()!=$("#fFormalizacionFisica").val()){
            				$("#fFormalizacion").val($("#fFormalizacionFisica").val());
            				queryFormPost("mPedidoUpdateFecha", {async : false});
            				queryFormPost("mPedidoUpdateFechaProcedimiento", {async : false});
            			}
            			window.location = 'Pedidos.jsp?tab=7&imprimir=true';
				});
				
				queryFormPost("mPedidoCaratulaRead",{async:false});
				if ($("#cIdTipoProcedimiento").val() == 'PF') {
					$( "#presupuestoPedido" ).attr("disabled", true);
					$( "#preCompromisoPedido" ).attr("disabled", true);
					$( "#pagosPedido" ).attr("disabled", true);
				}

				var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
				if(nIdEstado == 1)
					$("#preCompromisoPedido").css("display", "none");
				else
					$("#preCompromisoPedido").css("display", "block");
			});
			function textCounter( field, maxlimit ) {
				if ( field.value.length > maxlimit )
					field.value = field.value.substring( 0, maxlimit );
			}
			function onlyNumbers(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode
				var strCheck = '0123456789';
				var key = String.fromCharCode( keyPressed );
				if (strCheck.indexOf( key ) == -1)
					return false; // Valida que sea numero y punto decimal
				return true 
				//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
			}
			///Desabilita sabados y domingos del datepicker
			 function nonWorkingDates(date){
		        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
		        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
		        var closedDays = [[Sunday], [Saturday]];
		        for (var i = 0; i < closedDays.length; i++) {
		            if (day == closedDays[i][0]) {
		                return [false];
		            }
		
		        }
		
	// 	        for (i = 0; i < closedDates.length; i++) {
	// 	            if (date.getMonth() == closedDates[i][0] - 1 &&
	// 	            date.getDate() == closedDates[i][1] &&
	// 	            date.getFullYear() == closedDates[i][2]) {
	// 	                return [false];
	// 	            }
	// 	        }
		
		        return [true];
		    }
			function checkShortcut(){			
				if(event.keyCode==8){
					return true;
				}
			}
			function textCounter( field, maxlimit ) {
				//	field=/[0-9]/;
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );	    
			}
		</script>
	</head>
	<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
		<form>
			<fieldset>
				<legend>Clausulas</legend>
				<table border="0" >
			    	<tr>
			    		<td  align="center">
			    			<div id="demo">
							    <table id="tblClausulas" class="display" >
						        	<thead>
						        		<tr align="center"> 
						        			<th></th>
						        			<th>id</th>
						        			<th>Clausula</th>
						        			<th>Descripción</th>						        			
						        		</tr>
						        	</thead>
						        </table>
							</div>			        
			    		</td>
			    	</tr>
			   	</table>
			  </fieldset>
			  <fieldset>
			  <legend>Representante</legend>
			  <div id="personaMoral" style="display:none" >
				<table border="0" >
			    	<tr>
			    		<td align="left" >
							    <table border="0"> 
								    <tr>
								    	<td>Nombre del representante</td>
								    	<td colspan="2"><input type="text" name="cRepresentanteLegal" id="cRepresentanteLegal" value="" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>Cargo</td>
								    	<td colspan="2"><input type="text" name="cCargoRepresentante" id="cCargoRepresentante" value="" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>No. escritura acta constitutiva</td>
								    	<td colspan="2"><input type="text" name="cActaConstitutiva" id="cActaConstitutiva" value="" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>No. escritura poder notarial</td>
								    	<td colspan="2"><input type="text" name="cNoEscritura" id="cNoEscritura" value="" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>Telefono</td>
								    	<td colspan="2"><input type="text" name="cTelefono" id="cTelefono" value="" onkeypress="textCounter(this,20);"/></td>
								    </tr>
								    <tr>
								    	<td>Fecha</td>
								    	<td colspan="2"><input type="text" id="fFormalizacion" name="fFormalizacion" ></td>
								    </tr>
								   <!--  <tr>
								    	<td>Partida presupuestal (No. y nombre)</td>
								    	<td colspan="2"><input type="text" style="width: 600px" id="lblPartidaPresupuestal" name="lblPartidaPresupuestal" readonly style="border-width:0; background-color:transparent"/></td>
								    </tr> -->
								    <tr>
							    		<td  align="left">Partida Presupuestal</td>
							    		<td colspan="2">
							    		<textarea name="ep" id="ep" rows="2" cols="90" onkeypress="textCounter(this,200);"></textarea>
							    		</td>
							    	</tr>
								    <tr>
								    	<td rowspan="2">Autorizacion para inversión</td>
								    	<td>No. de oficio</td>
								    	<td>Fecha</td>
								    </tr>						 
								    <tr>
								    	<td><input type="text" name="cAutorizacion" id="cAutorizacion" value=""/></td>
								    	<td><input type="text" name="cFechaAutorizacion" id="cFechaAutorizacion" value=""/></td>
								    </tr>  	
						        </table>
			    		</td>
			    	</tr>
			    	</table>
			    	</div>
			    	<div id="personaFisica" style="display:none">
				<table border="0" >
			    	<tr>
			    		<td align="left" >
							    <table border="0"> 
								    <tr>
								    	<td>Nombre del representante</td>
								    	<td colspan="2"><input type="text" name="cRepresentanteLegalFisica" id="cRepresentanteLegalFisica" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>Cargo</td>
								    	<td colspan="2"><input type="text" name="cCargoRepresentanteFisica" id="cCargoRepresentanteFisica" value="" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>Acta de nacimiento</td>
								    	<td colspan="2"><input type="text" name="cActaConstitutivaFisica" id="cActaConstitutivaFisica" value="" style="width: 200px"/></td>
								    </tr>
								    <tr>
								    	<td>Telefono</td>
								    	<td colspan="2"><input type="text" name="cTelefonoFisica" id="cTelefonoFisica" value="" onkeypress="textCounter(this,20);" /></td>
								    </tr>
								    <tr>
								    	<td>Fecha</td>
								    	<td colspan="2"><input type="text" id="fFormalizacionFisica" name="fFormalizacionFisica" ></td>
								    </tr>
								    <!-- <tr>
								    	<td>Partida presupuestal (No. y nombre)</td>
								    	<td colspan="2"><input type="text" style="width: 600px" id="lblPartidaPresupuestalFisica" name="lblPartidaPresupuestalFisica" readonly style="border-width:0; background-color:transparent"/></td>
								    </tr> -->
								    <tr>
							    		<td  align="left">Partida Presupuestal</td>
							    		<td colspan="2">
							    		<textarea name="epFisica" rows="2" cols="90" id="epFisica" onkeypress="textCounter(this,200);"></textarea>
							    		</td>
							    		
							    	</tr>
							    	<tr>
							    		<td align="left"><input type="checkbox" name="propuestaConjuntaChk" id="propuestaConjuntaChk" onclick="muestraPropuestaConjunta();" style="display:none"/></td>
							    	</tr>
								    <tr>
								    <tr>
								    	<td rowspan="2">Autorizacion para inversión</td>
								    	<td>No. de oficio</td>
								    	<td>Fecha</td>
								    </tr>						 
								    <tr>
								    	<td><input type="text" name="cAutorizacionFisica" id="cAutorizacionFisica" value=""/></td>
								    	<td><input type="text" name="cFechaAutorizacionFisica" id="cFechaAutorizacionFisica" value=""/></td>
								    </tr>  	
						        </table>
			    		</td>
			    	</tr>
			    	</table>
			    	</div>
			    	</fieldset>
			    	<fieldset>
			    	<legend>Fundamentos</legend>
			    	<table>
			    	<tr>
			    		<td  align="center">
			    			<div id="demo">
							    <table id="tblFundamentos" class="display" >
						        	<thead>
						        		<tr align="center"> 
						        			<th></th>
						        			<th></th>
						        			<th>Descripción</th>						        			
						        		</tr>
						        	</thead>
						        </table>
							</div>			        
			    		</td>
			    	</tr>
			    </table>
			    <table>
			    	<tr>
			    		<td>
						    <table align="left">
					        	<tr>
					        		<td align="center"><button name="btnGuardarNuevoClausula" id="btnGuardarNuevoClausula">Guardar</button></td>
					        	<!-- <td align="left"><img  id="cmdPdfClausula" name="cmdPdfClausula" style="cursor: pointer" src="../../imagenes/icono_PDF.jpg" onclick="javascript:openPDF('clausulado.jasper');" />PDF</td> -->	
					        		
					        	</tr>	
					        </table>
			    		</td>
			    	</tr>
			    </table>
			    <input type="hidden" name="clausula" id="clausula" value=""/>
		    	<input type="hidden" name="fundamento" id="fundamento" value=""/>
		    	<input type="hidden" name="IdFundamento" id="IdFundamento" value=""/>
		    	<input type="hidden" name="cIdClausula" id="cIdClausula" value=""/>
		    	<input type="hidden" name="cNo" id="cNo" value=""/>
		    	<input type="hidden" name="fFormalizacionH" id="fFormalizacionH" value=""/>
		    	<input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo"/>
		    	<input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
				<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
				<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
				<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>" />
				<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento"/>
				<input type="hidden" name="rfcPedido" id="rfcPedido"/>
				<input type="hidden" name="esFisica" id="esFisica"/> <!--0=persona moral, 1= persona fisica -->
				<input type="hidden" name="EpGeneral" id="EpGeneral"/>
				
			</fieldset>
		</form>				
	</body>
</html>