<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>

<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map rol =usuario.getRoles();
    String cIdDocumento = "";
    String cCentroContable = "";
    String cEjercicio = "";
    String cIdUnidadEjecutora = "";
    String nIdEstado = "";
    
    if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		
		if (session.getAttribute(GestionInterface.ATT_RelacionGastosFolio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		
		}else 
			response.sendRedirect("RelacionGastos.jsp?tab=1");
			System.out.println("centro contable "+cCentroContable );
			System.out.println("cEjercicio "+cEjercicio );
			System.out.println("cIdDocumento "+cIdDocumento );
			System.out.println("cIdDocumento "+nIdEstado );
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title></title>
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
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker-es.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/validaciones.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
		  	var oTableCucops;
			var oTableDetalle;
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Relacion Gastos","PartidasRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			var mes = 0;
			
			$("#tblCucopPAA td:nth-child(3)").live("click", function() {
				mes = 1;
			});
			$("#tblCucopPAA td:nth-child(4)").live("click", function() {
				mes = 2;
			});
			$("#tblCucopPAA td:nth-child(5)").live("click", function() {
				mes = 3;
			});
			$("#tblCucopPAA td:nth-child(6)").live("click", function() {
				mes = 4;
			});
			$("#tblCucopPAA td:nth-child(7)").live("click", function() {
				mes = 5;				
			});
			$("#tblCucopPAA td:nth-child(8)").live("click", function() {
				mes = 6;
			});
			$("#tblCucopPAA td:nth-child(9)").live("click", function() {
				mes = 7;
			});
			$("#tblCucopPAA td:nth-child(10)").live("click", function() {
				mes = 8;
			});
			$("#tblCucopPAA td:nth-child(11)").live("click", function() {
				mes = 9;
			});
			$("#tblCucopPAA td:nth-child(12)").live("click", function() {
				mes = 10;
			});
			$("#tblCucopPAA td:nth-child(13)").live("click", function() {
				mes = 11;
			});
			$("#tblCucopPAA td:nth-child(14)").live("click", function() {
				mes = 12;
			});
			$("#tblCucopPAA tr").live("click", function(){
				fnClearSelected( $("#tblCucopPAA").dataTable() );
				
				if(mes >= 1){
					$(this).addClass("row_selected");
					validaAgregaDetalle(mes);
				}
				mes = 0;
			});
			$("#btnGuardar").button().click(function(){
				validaActualizaDetalle();
			});
		});
		
		
		
		
	function init(){
	       var umbral;
	      	queryFormPost("EjercicioFiscalActvRead",{async:false});
			$("#cIdFolio").val("<%=cIdDocumento%>");	
			$("#cIdDocumentoCaratula").val("<%=cIdDocumento%>");	
			queryFormPost("llenaCaratulaRelacionGastos", {async:false});
			queryFormPost("obtieneFolioRELAPA", {async:false});
			querySelectPost("mCatalogoCapituloCMBRead","sltCapitulo",{async:false});
			$("#cIdCapitulo").val($("#sltCapitulo").val());
			querySelectPost("mCatalogoSubPartidaCMBReadRELGASTOS","sltSubpartida",{async:false});
			queryFormPost("obtieneUsuarioCreacion", {async:false});
			queryFormPost("checaMontoTotalLineas",{async:false});
			 umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#montoNetoRELG").val())));
			if(umbral > .03){
			$("#apartadoRelacionGastos").css("display", "none");
			}else
			 $("#apartadoRelacionGastos").css("display", "block");
					
			
			if($("#lblMotivoRechazo").val() != ""){
				$("#trMovitoRechazo").css("display","");
				$("#divMotivoRechazo").html($("#lblMotivoRechazo").val());
			}
			else
			$("#trMovitoRechazo").css("display","none");
			
						
			//$("#lblImporteNeto").val($("#mImporteNeto").val());
			
			//$("#lblImporteNeto").formatCurrency();
			validaHabilitaCampos();
			initTable_CUCOPS();
			initTable_DetalleRELG();
		}
		
		
		function initTable_CUCOPS(){
			var qw = " cIdCABM like '%25FONDO%25' or cIdCABM like '%25PROCE%25' or cIdCABM like '%25PGDIR%25'";			
			oTableCucops = $("#tblCucopPAA").dataTable({
				sScrollX: "440%",
				//sScrollXInner: "440%",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUnidadEjecutora").val() + "', 'NULL' , '" + 
					 $("#sltSubpartida").val() + "')&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "enero" },
					{ sName: "febrero" },
					{ sName: "marzo" },
					{ sName: "abril" },
					{ sName: "mayo" },
					{ sName: "junio" },
					{ sName: "julio" },
					{ sName: "agosto" },
					{ sName: "septiembre" },
					{ sName: "octubre" },
					{ sName: "noviembre" },
					{ sName: "diciembre" },
					{ sName: "CABM", bVisible : false }
					
				]
        	});
		}
		
		
		function inicializaMeses(){
		initTable_CUCOPS();
		}
		
		
		function initTable_DetalleRELG(){
			oTableDetalle = $("#tblDetalle").dataTable({
				sScrollX: "120%",
				//sScrollXInner: "440%",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_DetalleRelacionGastos('"+$("#cEjercicio").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdFolio").val()+"')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				iDisplayLength:50,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cDescripcion" },
					{ sName: "cMes" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" },
					{ sName: "nPorcentajeIVA" },
					{ sName: "mImporteNeto" },
					{ sName: "cEliminar"},
					{ sName: "nIdMes", bVisible: false},
					{ sName: "cIdSubPartida", bVisible: false}
				]
        	});
		}
		
		function quitaFmt( val ) {
			if ( val.indexOf( "$" ) >= 0 )
		   		val = val.replace("$", "");
		   	while(val.indexOf( "," ) > 0)
		   		val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		function cambiaCapitulo(){
			$("#cIdCapitulo").val($("#sltCapitulo").val());
			
			querySelectPost("mCatalogoSubPartidaCMBReadRELGASTOS","sltSubpartida",{async:false});
		
			
		}
		
		function cambiaSubpartida(){
			initTable_CUCOPS();
		}
		
		function agregarDetalle(td, mes){
			alert(mes);
		}
		
		function fnGetSelected( oTableLocal ){
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
		
		function validaAgregaDetalle(mes){
			var anSelected = fnGetSelected( $("#tblCucopPAA").dataTable() );				
			var aData = $("#tblCucopPAA").dataTable().fnGetData(anSelected[0]);
			var umbral;
			$("#cIdCABMIns").val(aData[14]);
			$("#nIdPeriodo1").val(mes);
			$("#nIdPeriodo2").val(mes);
			$("#cIdSubPartida").val($("#sltSubpartida").val());
			queryFormPost("obtenMontoYCantidadRead",{async:false});
			
			if (parseInt($("#nCantidadDisponibilidad").val(),10) > 0) {
				if (parseFloat($("#mPrecioUnitario").val()) <= parseFloat($("#mMontoDisponibilidad").val())) {
					queryFormPost("existeDetalleRelacionGastos",{async:false});
					if(parseInt($("#existeDetalleRelacionGastos").val(),10) == 0){						
						//if(validaTopeImporte($("#cIdSubPartida").val())){
							queryFormPost("detalleRelacionGastosCreate",{async:false});
							initTable_DetalleRELG();
							initTable_CUCOPS();	
							
					//se revisa que el monto agregado a las lineas corresponda con el monto total de la relacion
				    queryFormPost("checaMontoTotalLineas",{async:false});
			        umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#montoNetoRELG").val())));
					if(umbral > .03){
					$("#apartadoRelacionGastos").css("display", "none");
					alert("El monto neto de las facturas debe ser el mismo que al monto neto de las partidas");
					}else
					$("#apartadoRelacionGastos").css("display", "block");
					
					
					}
					else
						alert("Ya existe un detalle con este mes, favor de editarlo.");
				}
				else
					alert("No se cuenta con presupuesto para este CUCOP, edite el programa anual.");
			}
			else
				alert("No hay disponiblidad para este CUCOP, edite el Programa Anual.");
		}
		
		function validaActualizaDetalle(){
	        var umbral;
	    	var aTrs = $("#tblDetalle").dataTable().fnGetNodes();
		    var aData;
			var montoBruto=0.00;
			var ivaTem=0;
			var montoTotal=0.00;
			var cantidad=0;
			var montoUnitario=0.00;
			var where=" where nFolioRelacionGastos="+$("#nFolioConsecutivoRelacionGastosCaratula").val()+" ";
			var arreglo= new Array();
			var arr = new Array();
			var totalPartida=0.00;
			var totalLinea=0.00;
			var partida="";
			var cantidad=0;
			var precioUnitario=0.00;
			var totalLineaTMP=0.00;
		    var totalLineaTMP1=0.00;
		    var totalLineaTMP2=0.00;
			//Valida que todas las partidas tengan el mismo IVA.
			 for ( var i=0 ; i < aTrs.length ; i++ ){
				aData =  $("#tblDetalle").dataTable().fnGetData(aTrs[i]);				
				ivaTem = $("#nPorcentajeIVA-"+aData[8]+"-"+aData[0]).val();
				
				if(i==0){
							partida=aData[9];
							cantidad=parseInt($("#nCantidad-"+aData[8]+"-"+aData[0]).val(),10);
							precioUnitario=parseFloat(quitaFmt( $("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
							totalLinea=precioUnitario*cantidad;
							totalLinea=totalLinea*((ivaTem/100)+1);
						    totalLineaTMP=parseFloat(totalLinea.toFixed(2)); 
							totalPartida=parseFloat(totalPartida+totalLineaTMP);
							if(i+1 == aTrs.length){
								arr = [partida,totalPartida];
								arreglo.push(arr);
								}
						  								
						}else{
						
						if(partida==aData[9]){
						    cantidad=parseInt($("#nCantidad-"+aData[8]+"-"+aData[0]).val(),10);
							precioUnitario=parseFloat(quitaFmt( $("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
							totalLinea=precioUnitario*cantidad;
							totalLinea=totalLinea*((ivaTem/100)+1);
						    totalLineaTMP1=parseFloat(totalLinea.toFixed(2));
						    totalPartida=parseFloat(totalPartida + totalLineaTMP1);
						  	if(i+1 == aTrs.length){
								arr = [partida,totalPartida];
								arreglo.push(arr);
							}
						
						
					}else{
					     	arr = [partida,totalPartida];
							arreglo.push(arr);
							totalPartida=0;
							totalLinea=0;
							partida=aData[9];
							cantidad=parseInt($("#nCantidad-"+aData[8]+"-"+aData[0]).val(),10);
							precioUnitario=parseFloat(quitaFmt( $("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
							totalLinea=precioUnitario*cantidad;
							totalLinea=totalLinea*((ivaTem/100)+1);
						    totalLineaTMP2=parseFloat(totalLinea.toFixed(2)); 
						    totalPartida=parseFloat(totalPartida+totalLineaTMP2);
							if(i+1 == aTrs.length){
								arr = [partida,totalPartida];
								arreglo.push(arr);
							}
					}
				}	
					
			}
			
			  	////////////////////////////////////////////////
			var partidas= new Array();
			var szWhere = "";
			var szTabla = "MONTOVALIDAPAARELG";
			var diferentes=false;
			var arregloFacturas= new Array();
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
          		var disponible = 0.0;
           		
				for (var i = 0; i < j.length; i++) {
					arr = [j[i].Col0,j[i].Col1];
					arregloFacturas.push(arr);
				}
								
				for(var x=0;x<arregloFacturas.length;x++){
					for(var y=0;y<arreglo.length;y++){						
						if(arregloFacturas[x][0]==arreglo[y][0]){
							if(arregloFacturas[x][1]>arreglo[y][1]){
								continue;
							
							}else{
								var aux=Math.abs(arregloFacturas[x][1]-arreglo[y][1]);
								if(aux <.03){
									continue;
								}else{
									diferentes=true;
									partidas.push(arreglo[y][0]);
								}
								
							}
							
						}
					}
				}
			  
			    if(!diferentes){
			    for ( var i=0 ; i < aTrs.length ; i++ ){
				aData =  $("#tblDetalle").dataTable().fnGetData(aTrs[i]);
				$("#cIdCABMIns").val(aData[0]);
				$("#nIdPeriodo1").val(aData[8]);
				$("#nIdPeriodo2").val(aData[8]);
				$("#cIdSubPartida").val(aData[9]);
				$("#nCantidad").val($("#nCantidad-"+aData[8]+"-"+aData[0]).val());
				$("#nPorcentajeIVA").val($("#nPorcentajeIVA-"+aData[8]+"-"+aData[0]).val());
				
				if(parseInt($("#nCantidad").val(),10) > 0){
				$("#mPrecioUnitario").val(quitaFmt($("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
				queryFormPost("updateDetalleRelacionGastos", {async:false});
										
				}
				else{
					alert("No puede guardar en cantidad 0.");
				}
			}
			initTable_DetalleRELG();
			initTable_CUCOPS();
			
			
			//se revisa que el monto agregado a las lineas corresponda con el monto total de la relacion
			    queryFormPost("checaMontoTotalLineas",{async:false});
		        umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#montoNetoRELG").val())));
				if(umbral > .03){
				$("#apartadoRelacionGastos").css("display", "none");
				alert("El monto neto de las facturas debe ser el mismo que al monto neto de las partidas");
				}else
				$("#apartadoRelacionGastos").css("display", "block");
						    
		    }else{
			alert("Los montos de las partidas: "+partidas+" no corresponden a los montos capturados en la pestaña de facturas");
			location.reload();
			}
			
		   });		
			
		
		}
		
		function validaHabilitaCampos(){
		
			if(parseInt($("#nIdEstado").val(),10) == 1 || parseInt($("#nIdEstado").val(),10)==5 ){
				var roles = "<%=roles%>";
								
				if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val()){
					$("#sltCapitulo").attr("disabled","true");
					$("#sltSubpartida").attr("disabled","true");
					$("#tblCucopPAA").attr("disabled","true");
					$("#tblDetalle").attr("disabled","true");
					$("#btnGuardar").attr("disabled","true");
				}
			}
			else{
				$("#sltCapitulo").attr("disabled","true");
				$("#sltSubpartida").attr("disabled","true");
				$("#tblCucopPAA").attr("disabled","true");
				$("#tblDetalle").attr("disabled","true");
				$("#btnGuardar").attr("disabled","true");
			}
		}
		
		function Sinfrmt( fld )	{
			var valcol = fld.value ;
		   	var vcompr = $("#mImporteTmp").val();
		   	vcompr = quitaFmt( vcompr );
		   	valcol = quitaFmt( valcol );
			$("#" + fld.id).val( valcol );
		   	fld.select();
			$("#mImporteTmp").val( parseFloat( vcompr ) - parseFloat( valcol ) );
			$("#mImporteTmp").formatCurrency();
		}
		
		function cambiafrmt( fld )	{
		   	var vcompr = $("#mImporteTmp").val();
		   	var vfld = $("#" + fld.id).val();
		   	if (vfld == "")
		   		vfld = '0';
			vcompr = quitaFmt( vcompr );
			vfld=quitaFmt(vfld);
		   	$("#mImporteTmp").val( parseFloat(vcompr) + parseFloat( vfld ) );
			$("#" + fld.id).formatCurrency();
			$("#mImporteTmp").formatCurrency();
		}
		
		function eliminaDetalle(cabm, mes){
			$("#cIdCABMIns").val(cabm);
			$("#nIdPeriodo1").val(mes);
			
			if(confirm("\xBFEst\xE1s seguro de eliminar el detalle?.")){
				queryFormPost("detalleRelacionGastosDelete",{async:false});
				initTable_CUCOPS();
				initTable_DetalleRELG();
				location.reload();
			}
			
		}
		
	
		function fnClearSelected( oTable ){
			var aTrs = oTable.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
				$(aTrs[i]).removeClass("row_selected");
		}	
		
	</script>
</head> 
<body>
<form>
	<table width="100%" align="left">
	
		<tr>
			<td>
				<fieldset>
				<legend>Informaci&oacute;n de la Relaci&oacute;n de Gastos</legend>
				<table border="0" align="center" width="100%">
					
					<tr align="left">
						<td colspan="2">
							<input name="lblcIdDocumento" id="lblcIdDocumento" type="text" style="width: 600px"  style="border: 0px solid black;" readonly="readonly"/>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" style="width: 600px" readonly="readonly" style="border: 0px solid black;"></input>  
						</td>								
					</tr>
					
					<tr align="left">
						<td colspan="2">
							<input name="lblnIdEstado" id="lblnIdEstado" type="text" readonly="readonly" style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					
					 <tr>
							<td align="left">
								Importe Neto Relacion Gastos:
								<input type="text" style="width: 500px" name="lblImporteNeto"
									id="lblImporteNeto" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					  <tr>
							<td align="left">
								<!--Importe Neto Partidas:-->
								<input type="hidden" style="width: 500px" name="montoNetoRELG" 
									id="montoNetoRELG" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					
					
					</table>
				</fieldset>
			</td>
		</tr>
		
		
		
		<tr>
			<td>
				<fieldset>
					<legend>Partidas</legend>
					<table align="left" style="width: 740px">
						<tr>
							<td style="width: 740px" align="left">
								<table>
									<tr>
										<td>
											Capitulo:
										</td>
										<td>
											<select id="sltCapitulo" onChange="cambiaCapitulo();" style="width: 650px">
											</select>
										</td>
									</tr>
									<tr>
										<td>
											Partida:
										</td>
										<td>
											<select id="sltSubpartida" onChange="cambiaSubpartida();" style="width: 650px">
											</select>
										</td>
									</tr>
									
									<tr>
						    			<td align="left"><img id="imgRefresh" src="../../imagenes/icono_refresh.jpg" style="cursor: pointer" onclick="inicializaMeses();"/> Actualizar </td>
						    		</tr>
									
									
								</table>
							</td>
						</tr>
						<tr>
							<td style="width: 740px">
						    	<table id="tblCucopPAA" class="display">
									<thead >
										<tr>
											<th align="center">&nbsp;&nbsp;CUCOP&nbsp;&nbsp;</th>
											<th align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Descripci&oacute;n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
											<th align="center">ENERO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">FEBRERO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">MARZO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">ABRIL<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">MAYO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">JUNIO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">JULIO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">AGOSTO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">SEPTIEMBRE<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">OCTUBRE<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">NOVIEMBRE<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">DICIEMBRE<br />Mon. Disp. / Can. Disp.</th>
											<th></th>
											</tr>										
									</thead>
								</table>
						    </td>
						</tr>
						<tr>
							<td align="left" style="width: 240px">
								<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" />
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td style="width: 740px">
			    				<table id="tblDetalle" class='display' width="740px" >
									<thead>
								    	<tr>
									    	<th>CUCOP</th>
									    	<th style="width: 300px">Descripci&oacute;n</th>
									    	<th>Mes</th>
									    	<th>Cantidad</th>
									    	<th>Precio<br/>Unitario</th>
									    	<th>IVA</th>
									    	<th>Importe<br/>Neto</th>
									    	<th>&nbsp;</th>
									    	<th>&nbsp;</th>
									    	<th>&nbsp;</th>
									    </tr>
									</thead>
								</table>
			    			</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=cEjercicio %>"/>
	<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion"/>
	<input type="hidden" id="cIdFolio" name="cIdFolio" />
	<input type="hidden" id="cIdDocumentoCaratula" name="cIdDocumentoCaratula" />
	<input type="hidden" id="cIDRFC" name="cIDRFC" />
	<input type="hidden" id="nIdEstado" name="nIdEstado" />
	<input type="hidden" id="cIdCapitulo" name="cIdCapitulo" />
	<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
	<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
	<input type="hidden" id="Imp_Neto" name="Imp_Neto" />
	<input type="hidden" id="nCantidadDisponibilidad" name="nCantidadDisponibilidad" />
	<input type="hidden" id="mMontoDisponibilidad" name="mMontoDisponibilidad" />
	<input type="hidden" id="mPrecioUnitario" name="mPrecioUnitario" />
	<input type="hidden" id="cIdCABMIns" name="cIdCABMIns" />
	<input type="hidden" id="nIdPeriodo1" name="nIdPeriodo1" />
	<input type="hidden" id="nIdPeriodo2" name="nIdPeriodo2" />
	<input type="hidden" id="cDescripcion" name="cDescripcion" />
	<input type="hidden" id="cIdSubPartida" name="cIdSubPartida" />
	<input type="hidden" id="mImporteTmp" name="mImporteTmp" />
	<input type="hidden" id="existeDetalleRelacionGastos" name="existeDetalleRelacionGastos" />
	<input type="hidden" id="montoUEPartida" name="montoUEPartida" />
	<input type="hidden" id="montoUEProveedor" name="montoUEProveedor" />
	<input type="hidden" id="nCantidad" name="nCantidad" />
	<input type="hidden" id="nPorcentajeIVA" name="nPorcentajeIVA" />
	<input type="hidden" id="montoNetoRELG" name="montoNetoRELG" />
	<input type="hidden" id="mCatalogoCapitulo" name="mCatalogoCapitulo" />
	<input id="nFolioConsecutivoRelacionGastosCaratula" name="nFolioConsecutivoRelacionGastosCaratula" type="hidden"/>
	
	
</form>    
</body>
</html>
