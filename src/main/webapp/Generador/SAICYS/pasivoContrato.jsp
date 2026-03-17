<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab1.getLogin();
	String roles="";
	Map rol =usuarioTab1.getRoles();
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
		
	}
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);		
	}else 
		response.sendRedirect("Contratos.jsp?tab=0");
		
		
		int cEjercicioPasivo=0;
		cEjercicioPasivo = Calendar.getInstance().get(Calendar.YEAR);//(String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Plurianualidad Contrato</title>
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
	<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" charset="utf-8"><!--

	var cEjercicio;
	//var cIdTipoContrato;
	var oTable;
		$(document).ready(function() {
			<%
				int tabla=0;
				int consulta=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				int imgGuardarPasivoContrato=0;
				Map botones=nb.getBotones(roles,"Contratos","pasivoContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%	
					String img=(String) b.getValue();
					if ("imgGuardarPasivoContrato".equals(img)){// actualiza
						%>$("#<%=img%>").attr("disabled", false);<%
						imgGuardarPasivoContrato=1; 
					}
				
				} 
				%> 
			
				//inicializacion de las tablas
		
		            oTable= $("#tblConsultaPasivo").dataTable({
				    bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			sScrollY: "300px",
					sScrollX: "100%",
					sScrollXInner: "200%",
			        bScrollCollapse: true,
					bDestroy: true,
					//bAutoWidth: true,
				  	bJQueryUI: true,
					bRetrive : true,
					sPaginationType: "full_numbers",
					oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "Todas Las Partidas Estan Pagadas",
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
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
						{ sName: "cEjercicio" },
						{ sName: "cIdUnidadEjecutora"},
						{ sName: "cIdSubPartida" },
						{ sName: "cSubPartidaCorto" },
						{ sName: "totalpagado" },
						{ sName: "montobruto_ue" },
						{ sName: "nIVA" },
						{ sName: "montoneto_ue" },
						{ sName: "porpagar" }
				        ]			
        	});			
		
			setFieldsInit();
			var consulta='<%=consulta%>';
			var tabla='<%=tabla%>';
			 
				    queryFormPost("mContratoHeaderRead", {async: false});
				    //Lee los montos del contrato
					queryFormPost("fnMontosContratoRead", {async: false});
					queryFormPost("fnMontoNetoContratoPresupuestoRead", {async: false});
				    queryFormPost("contratoDefinitivoPasivo", {async: false});
				    queryFormPost("contratoDefinitivoPasivo1", {async: false});
				    querySelectPost("UnidadPedidosRead", "cIdUnidadEjecutora", {async: false });
					querySelectPost("estadoContratoRead", "nIdEstado", {async: false });
					queryFormPost("mSistema_cEjercicioRead", {async: false});
					querySelectPost("llenaTipoCambioCotizaciones","cboCambioCotizacion",{async:false});
					//queryFormPost("mContratoPagadoRead", {async: false});
					
					
					
					
			queryFormPost("mContratoPagadoRead", {async: false});
			//obtenemos cIdProcedimiento y RFC
			queryFormPost("obtieneRFCProcedimiento", {async: false});
			
			//se checa si es contrato abierto
			queryFormPost("esContratoPedidoAbierto", {async: false});
			if($("#esAbierto").val() == "1"){
			queryFormPost("mContratoTotalMaximoAmpliacion", {async: false});
			queryFormPost("mContratoPorPagarReadMaximoAmpliacion", {async: false});
			
			}else
			queryFormPost("mContratoPorPagarRead", {async: false});
			
					
					//queryFormPost("mContratoPorPagarRead", {async: false});
		
					if (consulta==0){
						mostrar();
					}
				
			    	mostrarPasivoEditable();
			    	var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
					if(nIdEstado == 1)
						$("#preCompromisoContrato").css("display", "none");
					else
						$("#preCompromisoContrato").css("display", "block");
									
		});
		
		      $('#tblConsultaPasivo tr').live('dblclick', function() { 
			   
					//if (tabla==0){
				 
				  var porPagar=quitaFmt($("#lblTotalPorPagar").val());
				  if(parseFloat(porPagar) == 0){
				  alert("No se puede crear un Pasivo el Contrato ya ha sido Pagado en su Totalidad");
				  return;
				  }
					if ($(this).hasClass('row_selected')) {
					$(this).removeClass('row_selected'); 
				
					}            
						      
					else {
					$(this).addClass('row_selected');
					}        
				
				
				      var aTrs = oTable.dataTable().fnGetNodes();
			          for ( var i=0 ; i<aTrs.length; i++ ){
		              $("#cIdSubPartida").val($("#cIdSubPartidaConsulta_"+i+"").val());
		              $("#cCABM").val($("#cSubPartidaConsulta_"+i+"").val());
		              $("#cIdUnidadEjecutoraEdita").val($("#cIdUnidadEjecutora_"+i+"").val());
		              $("#mMontoPorPagar").val($("#porpagar_"+i+"").val());
		              $("#mMontoNetoUE").val($("#montonetoue_"+i+"").val());
		              $("#porcentajeIva").val(quitaFmt($("#niva_"+i+"").val()));
		              $("#montoNeto").val(quitaFmt($("#montonetoue_"+i+"").val()));
					  agregarNuevoPasivo();
		              break;
		            }
					
				
				mostrar();
				
			});
			
 
			// Agrega una nueva plurianualidad
			function agregarNuevoPasivo(){
			
		
				if ($("#tblConsultaPasivo").dataTable().fnGetNodes().length != 0){
					$("#cejercicioPlurianual").val(parseInt($("#cEjercicioPasivo").val(),10));
					$("#cIdDefinitivoPasivo").val();
					queryFormPost("sp_PasivoInserta", {async: false});
					mostrarPasivoEditable();		
				}
			}
		
		
		
		
		
		//  Actualiza el valor de %IVA y MontoNeto de un pasivo
			function actualizaPasivo(){
			var imgGuardarPasivoContrato='<%=imgGuardarPasivoContrato%>';
			if (imgGuardarPasivoContrato==0){
				var totalEdita=0;	
				  
				  
				  if(parseFloat($("#lblTotalPorPagar").val().replace(',','')) == 0){
				  alert("No se puede crear un Pasivo el Contrato ya ha sido Pagado en su Totalidad");
				  return;
				  }
				  
			         var aTrs=$("#tblCapturaPasivo").dataTable().fnGetNodes();
					 if (aTrs.length != 0){
							 
					 for(var i=0; i <aTrs.length; i++){
					  aData=oTable1.fnGetData(aTrs[i]);
					  if(parseFloat($("#nporcIVA"+aData[11]+"").val())> 100 || parseFloat($("#nporcIVA"+aData[11]+"").val()) < 0 ){
			          alert("El Iva no puede ser Mayor a 100 o Menor a 0");
			          mostrarPasivoEditable();
			          return;
		            }
		               
			         
				        var neto=quitaFmt($("#mMontoNeto_"+aData[11]+"").val());
				        var pagar=quitaFmt($("#mMontoPorPagar_"+aData[11]+"").val());
				       			          
					  if(parseFloat(neto)> parseFloat(pagar)) {
					  alert("El importe Neto Asignado es mayor al importe por Pagar de La Unidad Ejecutora"+" "+ $("#cUnidadEjecutora_"+aData[11]+"").val()+" " +"Partida"+ " "+$("#cIdSubPartida_"+aData[11]+"").val());
					  mostrarPasivoEditable();
					  return;
					 } 
						if(parseFloat(neto)==0){
						alert("El valor capturado es 0 en la Unidad Ejecutora"+" "+ $("#cUnidadEjecutora_"+aData[11]+"").val()+" " +"Partida"+ " "+$("#cIdSubPartida_"+aData[11]+"").val()+" "+"ingrese valores en los campos de texto");
						mostrarPasivoEditable();
						return;
						
						}
					
					
					}
				
				    for(var i=0; i <aTrs.length; i++){
				    aData=oTable1.fnGetData(aTrs[i]);
				   	$("#cejercicioPlurianual").val(aData[0]);
					$("#montoNeto").val(quitaFmt($("#mMontoNeto_"+aData[11]+"").val()));		
					$("#porcentajeIva").val(parseInt($("#nporcIVA"+aData[11]+"").val(),10));
					$("#cIdSubPartida").val($("#cIdSubPartida_"+aData[11]+"").val());
					$("#cCABM").val(aData[3]);
					$("#cIdUnidadEjecutoraEdita").val ($("#cUnidadEjecutora_"+aData[11]+"").val());
		            $("#mMontoPorPagar").val (quitaFmt($("#mMontoPorPagar_"+aData[11]+"").val())); 
		            $("#mMontoNetoUE").val (quitaFmt($("#mMontoNetoUE_"+aData[11]+"").val())); 
		            queryFormPost("sp_PasivoInserta", {async: false});
					}
		        alert("Se ha Creado el Pasivo Correctamente");
				mostrarPasivoEditable();
		}
			}else{
				alert("Usted no tiene permiso para realizar esta accion, contacte a su administrador");
			}
				  
		}
//-------------------------------------------------------------------------------------------------------------------------------		
		
			function setFieldsInit(){
				//set readonly para deshabilitar backspace			
				document.getElementById("lblUnidadEjecutora").style.readonly=true;
				document.getElementById("lblProcedimiento").style.readonly=true;
				document.getElementById("lblDefinitivo").style.readonly=true;
				document.getElementById("lblContrato").style.readonly=true;
				document.getElementById("lblProveedor").style.readonly=true;
				document.getElementById("lblEstado").style.readonly=true;
				
				
			}
		
			function mostrar() {
			  oTable.dataTable().fnClearTable(); 
			   var qw = $("#cIdDefinitivo").val();
			   szTabla = "CONSULTAPASIVO";                                                                                         
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param:"",Campos:qw, MaxReg:"" , ajax: 'false'}, 
					function(j)
					{    
						arrayCompleto=new Array();
						for (var i = 0; i < j.length; i++) 
    					{
    				    			
	    					arrayCompleto [i]=[
							"<input type='text' id='cEjercicio' name='cEjercicio' value='" + j[i].Col0 + "' readonly style='width:50px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='cIdUnidadEjecutora_"+i+"' name='cIdUnidadEjecutora_"+i+"' value='" + j[i].Col1 + "' readonly style='width:50px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='cIdSubPartidaConsulta_"+i+"' name='cIdSubPartidaConsulta_"+i+"' value='" + j[i].Col2 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='cSubPartidaConsulta_"+i+"' name='cSubPartidaConsulta_"+i+"' value='" + j[i].Col3 + "' readonly style='width:350px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='totalpagado_"+i+"' name='totalpagado_"+i+"' value='" + j[i].Col4 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
                            "<input type='text' id='montobrutoue_"+i+"' name='montobrutoue_"+i+"' value='" + j[i].Col5 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
                            "<input type='text' id='niva_"+i+"' name='niva_"+i+"' value='" + j[i].Col6 + "' readonly style='width:50px; border-width:0; background-color:transparent'/>",
                            "<input type='text' id='montonetoue_"+i+"' name='montonetoue_"+i+"' value='" + j[i].Col7 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
                            "<input type='text' id='porpagar_"+i+"' name='porpagar_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>"														

							]; 
							
						}	
    				  	$('#tblConsultaPasivo').dataTable().fnAddData(arrayCompleto);
    					 
		         });   
		     
			}
			
			function mostrarPasivoEditable() {
				 var qw = "cIdContratoDefinitivo LIKE'%25" +$("#cIdDefinitivo").val()+"%25'";
				
				oTable1 = $("#tblCapturaPasivo").dataTable({
					sScrollY: "300px",
					sScrollX: "100%",
					sScrollXInner: "200%",
					bScrollCollapse: true,
					bDestroy: true,
					bAutoWidth: true,
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Pasivos",
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
					
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_PasivoEdita&qw=" + qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ]] ,
					aoColumns: [
				
					    { sName: "cEjercicio" },
					    { sName: "cIdUnidadEjecutora1" },
					    { sName: "cIdSubPartida1" },
					    { sName: "cCABM" },
					   	{ sName: "mMontoBruto1" },
						{ sName: "nPorcentajeIVA1" },
						{ sName: "mMontoIVA" },
						{ sName: "mMontoNetoUE1" },
						{ sName: "mMontoPorPagar1" },
						{ sName: "mMontoNeto1" },
						{ sName: "boton1"},
						{ sName: "identificador" ,bVisible:false}
						
						]
	        	});
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


		        function borraPasivo(indice){
		        $("#cIdSubPartida").val(parseInt($("#cIdSubPartida_"+indice+"").val(),10));
		        queryFormPost("borraPartidaPasivo",{async: false});
		        mostrar();
		        mostrarPasivoEditable();
		        }


       		function modificaIVA(){
       
	       if ($('#chk_pasivo').is(':checked')) {
	      $("#ivaEditaNuevo").css("visibility","visible");
	         
          }else{
          $("#ivaEditaNuevo").css("visibility","hidden");
          }
        
     	  }
      
      

			function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '-0123456789.';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true; 
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
      
   
   
   
      function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}	
      

	--></script>
</head>

<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
	<form >
	<div id="container" class="container" align="left">
		<table align="left" width="750px">
			<tr>
				<td>
	 
		<fieldset style="width:750px;">
			<legend>Pasivo</legend>
			<table align="left" cellpadding="2" width="100%">
			<tr>
  				<td align="right" colspan="2">
					<img id="imgGuardarPasivoContrato" src="../../imagenes/iconos/guardar.png" style="cursor: pointer"  onclick="actualizaPasivo();"/>&nbsp;Guardar
					
				</td>
			 </tr>
			 <tr>
					<td align="left" colspan="2">
						<input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora"  readonly style="border-width:0; background-color:transparent"/>
					</td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProcedimiento" id="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblDefinitivo" id="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 700px" name="lblContrato" id="lblContrato"  readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor"   readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 100px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 
			 <tr>
				<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
			</tr>	
			 	     
			 	<tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalMaximo" id="lblTotalMaximo" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>	
			 	     
			 	     
			 <tr>
				<td align="left" colspan="2">Total Pagado:$<input type="text" style="width:600px" name="lblTotalPagado" id="lblTotalPagado" readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>		     
			 	     
			 	   
			 <tr>  
			<td align="left" colspan="2">Total Por Pagar:$<input type="text" style="width:500px" name="lblTotalPorPagar" id="lblTotalPorPagar" readonly style="border-width:0; background-color:transparent"/></td> 	   
			 </tr>	 
			 
			
			<!--<tr> 
			   <td align="left"> 
				Iva:<input type="text" id="ivaEdita" name="ivaEdita" style="width: 4em;" readonly="readonly"  onkeypress=return(onlyNumbers(event));" maxlength="3" />% 
				Modifica Iva: <input type="checkbox" id="chk_pasivo" name="chk_pasivo"  onclick="modificaIVA();" /><input type="text" id="ivaEditaNuevo" name="ivaEditaNuevo" value="" style="width: 4em;visibility:hidden"  onkeypress=return(onlyNumbers(event));" maxlength="3" />
				</td> 
				
			</tr> 
			      
  			--></table>
  		</fieldset>
  	</td>
</tr>	

			<tr>
				<td style="width: 740px; height: 300px" >
					<table  id="tblConsultaPasivo" width="740px" class="display">
						<thead>
							<tr>
							    <th width="50px" >EJERCICIO</th>
							    <th width="50px">U.E</th>
							    <th width="80px">PARTIDA</th>			
							    <th width="350px">DESCRIPCION</th>
							    <th width="150px">TOTAL PAGADO POR UE</th>
							    <th width="150px">MONTO BRUTO POR UE</th>
							    <th width="50px">% IVA</th>
							    <th width="150px">MONTO NETO POR UE</th>
							    <th width="150px">POR PAGAR</th>
							          			
							 </tr>
						</thead> 
					</table> 
				</td>
			</tr>
			
			
			
			<tr>
				<td style="width: 740px; height: 300px" >
					<table align="left" id="tblCapturaPasivo" width="740px" class="display">
						<thead>
							<tr>
							    <th>EJERCICIO</th>
							    <th>UE</th>
							    <th>PARTIDA</th>
							    <th>DESCRIPCION</th>
							    <th>MONTO BRUTO</th>
							    <th>% DE IVA</th>			
							    <th>MONTO IVA</th>
							    <th>MONTO NETOUE</th>
							    <th>MONTO POR PAGAR</th>
							    <th>MONTO NETO</th>
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
						<td><input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" /></td>
						<td><input type="hidden" name="cEjercicioPasivo" id="cEjercicioPasivo" value="<%=cEjercicioPasivo%>" /></td>
						<td>
						<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
						<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>" />
						<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
						<input type="hidden" name="montoBruto" id="montoBruto" />
						<input type="hidden" name="montoNeto" id="montoNeto" />
						<input type="hidden" name="porcentajeIva" id="porcentajeIva" />
						<input type="hidden" name="cejercicioPlurianual" id="cejercicioPlurianual" />
						<input type="hidden" name="maximoPlurianual" id="maximoPlurianual" />
						<input type="hidden" name="cIdDefinitivo" id="cIdDefinitivo" />
						<input type="hidden" name="cIdDefinitivoPasivo" id="cIdDefinitivoPasivo" />
						<input type="hidden" name="mImporteTotal" id="mImporteTotal" />
						<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" />
						<input type="hidden" name="cCABM" id="cCABM" />
						<input type="hidden" name="cIdCABM" id="cIdCABM" />
						<input type="hidden" name="totalEditable" id="totalEditable" />
						<input type="hidden" name="cIdUnidadEjecutoraEdita" id="cIdUnidadEjecutoraEdita" />
						<input type="hidden" name="mMontoPorPagar" id="mMontoPorPagar" />
						<input type="hidden" name="mMontoNetoUE" id="mMontoNetoUE" />
						<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
					    <input type="hidden" name="RFC" id="RFC" />
					    <input type="hidden" name="esAbierto" id="esAbierto" />
						</td></tr>
						<tr>
							<td><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/></td>
						</tr>
					</table>
				</td>
			</tr>
			
		</table>
	</div>
</form>
</body>
</html>

