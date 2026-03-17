<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import=" java.sql.CallableStatement" %>
<%
	Usuario usuario= (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
	String idRol="0";	
	String cCentroContable = "";	
	Map<String, Role> rol =usuario.getRoles();
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	System.out.println("cCentroContable : "+cCentroContable);
	String cEjercicio = "";
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";	
	String idPedidoDef="";
	boolean isPedido=false;
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
		cIdTipoPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_PedidoConsecutivo);
		idPedidoDef=cIdTipoPedido+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo+"/"+cEjercicio;
		isPedido=true;
	}else if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoPedido = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);
		idPedidoDef=((session.getAttribute(GestionInterface.ATT_ContratoDefinitivo)==null || "".equals((String)session.getAttribute(GestionInterface.ATT_ContratoDefinitivo)))?cIdTipoPedido+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo+"/"+cEjercicio: (String)session.getAttribute(GestionInterface.ATT_ContratoDefinitivo));//cIdTipoPedido+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo+"/"+cEjercicio;
		
	}
	System.out.println("isPedido: "+isPedido);
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Anticipos y Retenciones</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	
	<script type="text/javascript" charset="utf-8">
	var vcontrato, vfolio,EP, vAvanzo;
	var oTableCalendario,oTableEP;
	var cambio=true;
	var nIdEstado=0
	var calendarioConsultado=false;
	var tipoDocto="contrato";
	$(document).ready(function() {
		if(<%=isPedido%>){
			$("#tbs").val(17);
			tipoDocto="pedido";
		}else{
			$("#tbs").val(12);
		}
		showAndHideTabs();
		initQueries();
		
		//variables globales del contrato
		$("#cIdContrato").val($("#cIdContratoH").val());
		vcontrato=$("#cIdContrato").val();
		vfolio=$("#nFolioPreCompromiso").val();
		initAnticipos();
		loadRetenciones();
		
		$(".montos").formatCurrency();
		hideAndShowButtons();
		
		
	});
	
		//Click para quitar una retencion de la tabla de retenciones
		$('#dt_retencion tr').live('dblclick', function() {
		
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var obligatoria=0;
				var aTrs = $('#dt_retencion').dataTable().fnGetNodes();
				if(parseInt(nIdEstado)>=4 && aTrs.length>0){
					swal("El estatus del "+tipoDocto+" no permite el borrado de retenciones.",{icon:"info",button: "Cerrar"});
					$(this).removeClass('row_selected');
					return;
		    	}
				for ( var i=0; i<=aTrs.length; i++ ){
					if ( $(aTrs[i]).hasClass('row_selected') ){          
						var nTr = $('#dt_retencion').dataTable().fnGetData(aTrs[i]);
						$("#cIdTipoRetencionVentanilla").val(nTr[0]);
						$("#cTipoRetencionVentanilla").val(nTr[1]);
						obligatoria=nTr[4];
						$('#dt_retencion').dataTable().fnDeleteRow( i );
					}
				}
				
			
				if(parseInt(obligatoria,10)==0){
					$("#cAccion").val("Elina retención "+$("#cTipoRetencionVentanilla").val());
					if(parseInt($("#cIdTipoRetencionVentanilla").val(),10)==16){//Si es la retención del IVA 6%
						//Guardar en bitacora la eliminación de la retención IVA 6%
						swal({
							title: "Queda bajo su responsabilidad la omisión al cumplimiento del artículo 1-A fracción IV  de la Ley del Impuesto al Valor Agregado.\n¿Está seguro que quiere eliminar la retención?",
							text: "",
							icon: "info",
							buttons: {
								confirm : "Aceptar",
								cancel: "Cancelar"
								},
						}).then((continuar) => {
							if (!continuar) {
								return;
							}else{
								$("#lEliminaRetencion6IVA").val(1);
								queryFormPost("tContratoDiversoRetencionVentanillaDelete,movimientoRetencionIVA6,sp_mBitacoraMovimientosCreate", {async: false});
							}
						});
					}else{
						queryFormPost("tContratoDiversoRetencionVentanillaDelete,sp_mBitacoraMovimientosCreate", {async: false});
					}
					loadRetenciones();
				}else{
					swal("La retención no se puede quitar por que está como oblicatoria.",{icon:"info",button: "Cerrar"});
				}
				
				
			});
	

	function initQueries(){
		
		//Query heredado de SAI para obtener los tipos de retenciones
		querySelectPost("CatalogoTipoRetencionRead", "cIdTipoRetencionCMB", {async: false });
		queryFormPost("AnticipoPreCompromisosRead", {async: false});
		queryFormPost("preCompromisoCaratulaVentanillaRead", {async: false});
		$("#nPorcAmortizacion").val(parseInt( $("#nPorcAmortizacion").val(),10 ) );
		$("#nPorcAsignacion").val(parseInt($("#nPorcAsignacion").val(),10));
	
		if(	parseInt($("#nPorcAsignacion").val(),10)>0 || $("#nPorcAmortizacion").val()>0){
			document.getElementById("BorrarAntAmort").disabled=false;
		}
		
		
	}
	
	function checkShortcut()
	{				
		//Deshabilita el ESC y BACKSPACE
		if(event.keyCode==27){
			return false;
		}
		if((event.srcElement.tagName.toUpperCase() != 'INPUT' &&
				event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
			&& (event.keyCode==8 || event.keyCode==13)){					
			return false;
		}
	}
	function ResponsableSiguiente(id_oper){
   	 if(parseInt(id_oper,10)==2)
  		 	return "CONSULTA_PRECOMPROMISO";
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){
   		if(parseInt(id_oper,10)==2)
  		 	//return "consulta_compromiso";
  		 	return "consulta_precomp";
  	}
  	function initAnticipos(){
		oTableAnticipo=$('#dt_anticipos').dataTable({         
			"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
  			"bInfo": false,
   			"bAutoWidth": true, 
			sScrollX: "100%",
			"bJQueryUI": true,    
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"  
		} );
		
		oTableRetencion=$("#dt_retencion").dataTable({         
	    	"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
  			"bInfo": false,
   			"bAutoWidth": true, 
			"sScrollY": 100,
			"sScrollX": 200,
			"bJQueryUI": true,    
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"    
		} );
	}
	function fnAgregarRet() {
		if ( $("#cIdTipoRetencionCMB").val() == "2")
			if ($("#CamInst").val() == "") {
				swal("Selecccione Camara o Instituto para continuar",{icon:"info",button: "Cerrar"});
				return false;
			}
		if ($("#nPorcAsignacion").val()=='0' || $("#nPorcAsignacion").val()=='' || $("#nPorcAsignacion").val()=='0.0' || $("#nPorcAsignacion").val()=='0.00')
			$("#LHaySaldoAnticipo").val(0);
		else
			$("#LHaySaldoAnticipo").val(1);
		var oTableLocal = $('#dt_retencion').dataTable();
		var rowCount = oTableLocal.fnGetNodes().length;
		var idRet=$("#cIdTipoRetencionCMB").val();
		//i: inicia en 2 por que hay dos filas en la tabla al iniciar
		var i=0, noExiste=true;
		var aData;
		//Revisa que no se repita la clave de retención
		while(i<rowCount && noExiste){
			aData = oTableLocal.fnGetData( i );
			if(idRet== aData[ 0 ])	
				noExiste=false;
			else
				i++;
		}
		// En caso de que ya se haya agregado la clave sale de la función
		if(i!=rowCount){
			swal("Esa clave ya se agregó previamente",{icon:"info",button: "Cerrar"});
			return false;
		}			
		$("#cIdTipoRetencionVentanilla").val(idRet);
		$("#lEliminaRetencion6IVA").val(0);
		$("#cAccion").val("Agrega retención "+$("#cIdTipoRetencionCMB option:selected").text());
		if(parseInt($("#cIdTipoRetencionCMB").val(),10)==16){//Si es la retención del IVA 6%
			queryFormPost("tContratoDiversoRetencionVentanillaCreate,movimientoRetencionIVA6,sp_mBitacoraMovimientosCreate", {async: false });
		}else{
			queryFormPost("tContratoDiversoRetencionVentanillaCreate,sp_mBitacoraMovimientosCreate", {async: false });
		}
		
		loadRetenciones();
	}
	function fnAgregarAntAmort(){
		var anticipo=parseInt($("#nPorcAsignacion").val(),10);
    	var amort=parseInt($("#nPorcAmortizacion").val(),10);
		if(parseInt(anticipo,10)>0 || parseInt(amort,10)>0){
		
			if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){
				queryFormPost("obtieneContratoOriginal", {async: false});
					
			}else{
				$("#cIdContratoOriginal").val($("#cIdContratoH").val())
			}
				
			queryFormPost("pa_agregaAnticipoAmortizacion", {async: false });
			swal("Amortización creada",{icon:"info",button: "Cerrar"});
			document.getElementById("BorrarAntAmort").disabled=false;
		}else{
			swal("Tienes que agegar un anticipo o amortización.",{icon:"info",button: "Cerrar"});
		}
	}
	function fnBorrarAntAmort(){
		if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){
			queryFormPost("obtieneContratoOriginal", {async: false});
					
		}else{
			$("#cIdContratoOriginal").val($("#cIdContratoH").val())
		}
		
		
		if(	parseInt($("#nPorcAsignacion").val(),10)>0 || $("#nPorcAmortizacion").val()>0){	
			queryFormPost("borrarAnticipoAmortizacion", {async: false });
			$("#nPorcAsignacion").val(0);
		   	$("#nPorcAmortizacion").val(0);
			swal("Amortización borrada",{icon:"info",button: "Cerrar"});
			document.getElementById("BorrarAntAmort").disabled=true;
		}
		
	}
	function loadRetenciones(){
	 	oTableRetencion=$('#dt_retencion').dataTable( {
			bPaginate: false,
  			bFilter: false,
  			bInfo: false,
			sScrollX: "100%",
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			"fnServerData": function ( sSource, aoData, fnCallback ) {
				$.ajax( {
					"dataType": 'json', 
					"type": "POST", 
					"url": sSource, 
					"data": aoData, 
					"success": fnCallback
				} );
			},   
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRetencionesPedContrato&qw=" + encodeURI("cIdContrato='" + $("#cIdContratoH").val() + "' AND cIdTipoRetencion<>1"),
			aoColumns: [
				{ sName: "cIdTipoRetencion", bVisible: false},
				{ sName: "cTipoRetencion"},
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  },
				{ sName: "obligatoria",	bSearchable: false,	bSortable: false, bVisible: false  },
				{ sName: "idObligatoria",	bSearchable: false,	bSortable: false, bVisible: false  }
				]
              } ) ;
		}
	function onlyNumbers(evt){
        var keyPressed = (evt.which) ? evt.which : event.keyCode
        if (keyPressed == 47)
			return false;
        return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
     }
     function fn_actret(pcontrol, pctrlret) {				
		var valor = $('#' + pcontrol).val()
		$('#' + pctrlret).val( valor.substring(2) * 100 + "%" ) ;
		if ( $("#cIdTipoRetencionCMB").val() == 2 ) 
			$("#divMilla2").show();
		else 
			$("#divMilla2").hide();
	}
     function hideAndShowButtons(){
    	 $("#AgregarAntAmort").hide();
		 $("#BorrarAntAmort").hide();
		 $("#Agregar").hide();
    	 if(parseInt(nIdEstado)==2 || parseInt(nIdEstado)==3){
    		 $("#AgregarAntAmort").show();
    		 $("#BorrarAntAmort").show();
    		 $("#Agregar").show();
    	 }
     }
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  		<fieldset>
		<legend>Anticipos y Retenciones</legend>
		<table align="left">
			<tr>
				<td>
					<input type="text" style="width: 600px;border-width:0; background-color:transparent" id="cIdContratoH" name="cIdContratoH" value="<%=idPedidoDef%>" readonly  />			
				</td>
			</tr>
		</table>
		
		<table  id="dt_anticipos" class="display" style="width: 100%">
			<thead>
				<tr align="center">
				<th>% Anticipo</th>
				<th>% Amortizaci&oacute;n</th> 
				</tr> 
			</thead>
			<tbody>
				<tr align="left">
					<td align="center"><input type="text" maxlength="2" size="5" name="nPorcAsignacion" id="nPorcAsignacion" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"></td>
					<td align="center"><input type="text" maxlength="2" size="5" name="nPorcAmortizacion" id="nPorcAmortizacion" value="0" onKeyPress="return onlyNumbers(event)" style="text-align:right;"></td> 
				</tr> 
			</tbody>
		</table>
		<br/>
		
		<input align="left" type="button" value="Agregar Anticipo y Amortización"  id="AgregarAntAmort" name="AgregarAntAmort"	onclick="fnAgregarAntAmort();" class="btnInterfaceBG ui-button ui-corner-all" />
		<input align="right" type="button" value="Borrar Anticipo y Amortización"  id="BorrarAntAmort" name="BorrarAntAmort"	onclick="fnBorrarAntAmort();" disabled="disabled" class="btnInterfaceBG ui-button ui-corner-all">
		
		<br/><br/><br/>
		<h2>Retenciones</h2>
		
		<select	onchange="fn_actret('cIdTipoRetencionCMB', 'nPorcRetencion')" name="cIdTipoRetencionCMB" id="cIdTipoRetencionCMB" style="width: 250px;">
			<option selected value="1">SIN RETENCION</option>
		</select>

		<input type="button" value="Agregar Retención" name="Agregar" id="Agregar"	onclick="fnAgregarRet();" class="btnInterfaceBG ui-button ui-corner-all" />

		
		
		
		<div id="divMilla2" style="display: none;">
			<label>
				Camara :
				<input name="grpMilla2" type="radio" id="grpMilla2"
					onclick="habilitaMillar2(0)" value="Camara">
			</label>
			<label>
				Instituto :
				<input type="radio" id="grpMilla2" name="grpMilla2"
					onclick="habilitaMillar2(1)" value="Instituto">
			</label>
		</div>			
		<table id="dt_retencion" class="display" style="width: 100%">
			<thead>
				<tr>
					<th style="display: none;"></th>
					<th >Clave de retenci&oacute;n</th>
					<th style="display: none;"></th>
					<th style="display: none;"></th>
					<th style="display: none;"></th> 
				</tr> 
			</thead>
		</table>
		<!-- Hidden Section -->
			    <!-- Valores de Precompromiso -->
			
			    <input type="hidden" name="cIdContrato" id="cIdContrato" />
			    
			    <input type="hidden" name="origen" id="origen" />
			    <input type="hidden" name="cIdContratoOriginal" id="cIdContratoOriginal" />
			    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
			    <input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable"  value="<%=cCentroContable%>"/>
				<input type="hidden" name="cIdTipoRetencionVentanilla" id="cIdTipoRetencionVentanilla"/>
				<input type="hidden" name="cTipoRetencionVentanilla" id="cTipoRetencionVentanilla"/>
				<input type="hidden" name="nExisteAntAmort" id="nExisteAntAmort"/>
				<input type="hidden" name="nFolioPreCompromiso" id="nFolioPreCompromiso"/>
				<input type="hidden" name="cAccion" id="cAccion"/>
				<input type="hidden" name="cIdDocumento" id="cIdDocumento" value="<%=idPedidoDef%>"/>
				<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
				<input type="hidden" name="lEliminaRetencion6IVA" id="lEliminaRetencion6IVA" value="0"/>
				<input type="hidden" name="nIdEstadoPed" id="nIdEstadoPed" value="0"/>
								
			</fieldset>
  </body>
</html>
