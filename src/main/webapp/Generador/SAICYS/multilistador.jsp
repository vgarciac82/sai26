<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CUENTAS_BANCARIAS_BENEFICIARIOS")){
		mntoCuentas = "SI".equals(usuario.getPropiedad("CUENTAS_BANCARIAS_BENEFICIARIOS").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    <title>Multilistador</title>
    
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
		<script type='text/javascript' src='../js/formexp.js'></script>
		<script type='text/javascript' src='../js/jquery.multiselect.js'></script>
		<link rel="stylesheet" type="text/css" href="../css/jquery.multiselect.css"/>
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {

				//Para saber de que ejercicio es y el año actual  readEjercicioFiscalActivo
				queryFormPost("readEjercicioFiscalActivo", {async : false});
				$("#anoActual").val($("#cEjercicio").val());
				//Carga las plantillas existentes
				querySelectPost("readReportePredeterminadoExistentes","sltPlantillasExistentes",{async : false});
				queryFormPost("ejercicioFiscalPA",{async:false});
				// volver a cargar los registros
				$("#auxIdProceso").val("0");
				$("#btnEditarPlantilla").css("display","none");
				
				var arreglo=arreglosModulo("todos",1);
				columnasDisponibles("Descripcion",arreglo,1);
				//////////////Cambios para Greyes de ocultar el checkbox de todos/////////////////////////////
				oTable.dataTable().fnClearTable();
				xDisplay("procesos", 'block');
				$("#todos").css("display", "none");
				$("#1").css("display","none");
				$("#2").css("display","none");
				$("#3").css("display","none");
				$("#4").css("display","none");
				$("#5").css("display","none");
				$("#6").css("display","none");
				$("#7").css("display","none");
				$("#8").css("display","none");
				//////////////////////////////////////////////////////////////////////////////////////////////
				reloadColumnasSelec("Descripcion");
			$("#btnGuardarPlantilla").button().click(function(){
				if($("#txtNombrePlantilla").val() == ""){
					alert("Debe introducir el nombre de la plantilla.");
					return;
				}
				else if ($("#tblColumnasSelec").dataTable().fnGetNodes().length == 0){
					alert("No existe ningun campo seleccionado.");
					return;
				}
				else{
					$("#hdnConsecutivoReporte").val("0");
					queryFormPost("readReportePredeterminado", {async : false});
					
					if(parseInt($("#hdnConsecutivoReporte").val(),10) > 0){
						alert("Ya existe un reporte con el mismo nombre.");
						return;
					}
					else{
						if(confirm("\xBFEst\xE1s seguro de guardar los datos?")){
							var aTrs = $("#tblColumnasSelec").dataTable().fnGetNodes();
							queryFormPost("readConsecutivoPlantilla", {async : false});
							queryFormPost("createPlantillaEncabezado",{async : false});
							
							for ( var i=0;i<=aTrs.length-1;i++ ){
								var nTr = $("#tblColumnasSelec").dataTable().fnGetData(aTrs[i]);
								$("#hdnOrden").val(i);
								$("#hdnCampo").val(nTr[0]);
								$("#hdnModulo").val(nTr[1]);
								$("#hdnDescripcion").val(nTr[2]);
								queryFormPost("createPlantillaDetalle",{async : false});
							}
							
							alert("Los datos se guardaron correctamente.");
							querySelectPost("readReportePredeterminadoExistentes","sltPlantillasExistentes",{async : false});
							$("#txtNombrePlantilla").val("");
							$("#sltPlantillasExistentes").val($("#hdnConsecutivoPlantilla").val());
							$("#btnEditarPlantilla").css("display","");
						}
					}
				}
			});
			
			$("#btnCargarPlantilla").button().click(function(){
				var arreglo = new Array();
				var arr = new Array();
				
				if($("#sltPlantillasExistentes").val() == 0 || $("#sltPlantillasExistentes").val() == ""){
					alert("Debe seleccionar una plantilla.");
					return;
				}
				else{
					$.getJSON("../../catalogos/SelectJson.jsp?"+new Date(),{Tabla: "PLANTILLAREPORTE", Param: " nIdConsecutivo = "+$("#sltPlantillasExistentes").val() , MaxReg:"" , ajax: false, async : false}, 
						function(aData)
						{	
							$("#tblColumnas").dataTable().fnClearTable();
							$("#tblColumnasSelec").dataTable().fnClearTable();
							reloadColumnasSelec("Descripcion");
							var id="";
							for(var i=0; i < aData.length; i++){
								arr = [aData[i].Col1, aData[i].Col2, aData[i].Col3];
								arreglo.push(arr);
								if(aData[i].Col2 == 1){
									$("#chPanual").attr("checked",true);
									id="chPanual";
								}
								if(aData[i].Col2 == 2){
									$("#chsolicitu").attr("checked",true);
									id="chsolicitu";
								}
									
								if(aData[i].Col2 == 3){
									$("#chConsolidado").attr("checked",true);
									id="chConsolidado";
								}
								if(aData[i].Col2 == 4){
									$("#chProcedimiento").attr("checked",true);
									id="chProcedimiento";
								}
									
								if(aData[i].Col2 == 5){
									$("#chPC").attr("checked",true);
									id="chPC";
								}
								if(aData[i].Col2 == 6){
									$("#chsaldos").attr("checked",true);
									id="chsaldos";
								}
									
								if(arreglo[i][1] == 7){
									$("#chpagos").attr("checked",true);
									id="chpagos";
								}
									
								if(aData[i].Col2 == 8){
									$("#chProveedores").attr("checked",true);
									id="chProveedores";
								}
							}
							$("#tblColumnasSelec").dataTable().fnAddData(arreglo);
							consulta();
						}
					);
				}
			});
			
			$("#btnEditarPlantilla").button().click(function(){
				if($("#tblColumnasSelec").dataTable().fnGetNodes().length > 0){
					if(confirm("\xBFEst\xE1 seguro que desea sobreescribir la informaci\xF3n de la plantilla? Una vez confirmado, no podr\xE1 deshacer cambios.")){
						$("#hdnConsecutivoPlantilla").val($("#sltPlantillasExistentes").val());
						queryFormPost("deletePlantillaDetalle",{async : false});
						var aTrs = $("#tblColumnasSelec").dataTable().fnGetNodes();
						
						for ( var i=0; i <= aTrs.length-1; i++ ){
							var nTr = $("#tblColumnasSelec").dataTable().fnGetData(aTrs[i]);
							$("#hdnOrden").val(i);
							$("#hdnCampo").val(nTr[0]);
							$("#hdnModulo").val(nTr[1]);
							$("#hdnDescripcion").val(nTr[2]);
							queryFormPost("createPlantillaDetalle",{async : false});
						}
						
						alert("Los datos se guardaron correctamente.");
					}
				}
				else
					alert("No existe ningun campo seleccionado.");
			});
				
				/// Borrar todos
			$("#btntblBorraColumnasSelec").button().click(function(){
				$('#tblColumnasSelec').dataTable().fnClearTable();
				if (document.multilistador.todos.checked){
					var arreglo=arreglosModulo("todos",1);
					 oTable.dataTable().fnClearTable();
					columnasDisponibles("Descripcion",arreglo,1);
				}else{
					cargaColumnas();
				}
			});
			
			//Seleccionar todos
			$("#btntblColumnas").button().click(function(){		
				var aTrs = $('#tblColumnas').dataTable().fnGetNodes();
				arrayCompleto=new Array();
				for ( var i=0; i<aTrs.length; i++ ){                     
						var nTr = $('#tblColumnas').dataTable().fnGetData(aTrs[i]);  
						arrayCompleto [i]=[nTr[0],nTr[1],nTr[2]];
				}
				$('#tblColumnasSelec').dataTable().fnAddData(arrayCompleto);
				$('#tblColumnas').dataTable().fnClearTable();
		    });
		    
		    
				/// generar query
				$("#btnGenerarReporte").button().click(function(){
					// recuperar valores de opciones seleccionadas
					var query= "select distinct ";
					var encabezado="";
					var aTrs = $('#tblColumnasSelec').dataTable().fnGetNodes();
					if (aTrs.length==0){
						alert("No hay elementos seleccionados");
						return;
					}else{
						for ( var i=0;i<=aTrs.length-1;i++ ){
							var nTr = $('#tblColumnasSelec').dataTable().fnGetData(aTrs[i]);
							var dato=nTr[0];
							query=query+dato+',';
							var titulo="_"+ nTr[2];
							encabezado=encabezado+titulo+',';
							
						}
						//Poner la fecha de corte cuando se consulta el historico
						if($("#mes1").val()!='00'){
							query=query+"fechaCorte,";
							encabezado=encabezado+"_Fecha de Corte,"
						}
						query=query.substring(0,query.length-1);
						encabezado=encabezado.substring(0,encabezado.length-1);
					}
					
					//Para saber a que dataStore se va hacer la consulta
					
					if($("#HistoricoEjercicio").val()!= $("#anoActual").val()) {
						query=query+" from mDataStoreHistorial with(nolock) ";
					}
					//consultando ejercicio actual	
					else{
						var andSaldos=" left join v_mSaldosAgrupados as mds on md.UnidadEjecutora=mds.cUnidadEjecutora and md.cEjercicio=mds.aEjercicioFiscal and md.cIdPartidaPA=mds.cPartida ";		
						var andPagos=" left  join mDataStorePagos as mdp with(nolock) on md.cIdUnidadEjecutoraPedidoCnt=mdp.cUnidadEjecutora_P and cIdPedidoContratoDefinitivo=cIdDocumento ";//inner
						var andProveedores = "( "+
											"select "+ 
											"cp.cIdRFC as cIdRFCProveedor, replace(cp.cRazonSocial,',','') as cRazonSocial, cef.cIdEntidadFederativa, cef.cEntidadFederativa as cEntidadFederativaProveedor, cpy.nIdPyme, "+
											"replace(cpy.cPyme,',','') as cPyme, case when cp.lHabilitado=1 then 'SI' else 'NO' end as lHabilitado, cp.lHabilitado as nHabilitado, "+
											"replace(cp.cGiro,',','') as cGiro,replace(cp.cNumeroRegistro,',','') as cNumeroRegistro, replace(cp.cRepresentanteLegal,',','') as cRepresentanteLegal,"+ 
											"replace(cp.cCalle,',','') as cCalle, replace(cp.cNumeroExterno,',','') as cNumeroExterno, replace(cp.cNumeroInterno,',','') as cNumeroInterno, "+
											"replace(cp.cColonia,',','') as cColonia, replace(cp.cMunicipio,',','') as cMunicipio, cp.cCodigoPostal, dbo.fn_mTelefonosProveedor(cp.cIdRFC) as cTelefono, "+
											"replace(cp.cEmail,',','') as cEmail, replace(cp.cUrl,',','') as cUrl, case when len(replace(replace(cp.cIdRFC,'-',''),' ',''))=13 then 1 else 2 end as nTipoPersona, "+
											"case when len(replace(replace(cp.cIdRFC,'-',''),' ',''))=13 then 'Fisica' else 'Moral' end as tipoPersona, "+
											"tb.dCURP as dCURP,tb.CBEN from "+
											"mCatalogoProveedor cp with(nolock) "+ 
											"inner join mCatalogoPyme cpy with(nolock) on cp.nIdPyme=cpy.nIdPyme "+ 
											"inner join mCatalogoEntidadFederativa cef with(nolock) on cp.cIdEntidadFederativa=cef.cIdEntidadFederativa "+ 
											"inner join tBeneficiario tb with(nolock) on REPLACE(REPLACE(cp.cIdRFC,'-',''),' ','')=tb.dRFC"+ 
											") as tabProveedor";
						
						if($('#todos').is(':checked')){							
							if($("#mes1").val()!='00')
								query+=" from mdataStoreHistorialActual as md with(nolock) "+andSaldos+" "+andPagos;
							else
								query+=" from mdataStore as md with(nolock) "+andSaldos+" "+andPagos+" left join "+andProveedores+" on md.cIdRfcPedidoContrato=tabProveedor.cIdRFCProveedor";
						}
						else{
							/*Valida si se puede generar el reporte de acuerdo a los campos de los modulos
							* seleccionados, NO se puede generar el reporte cuando este seleccionado Proveedores y Programa Anual,
							* Requisiciones, Consolidado, Saldos o Pagos 
							* y ni pedido ni contrato se encuentran seleccionados.
							*/
							if((validaCamposSeleccionados('1') == 1 || validaCamposSeleccionados('2') == 1 || 
							validaCamposSeleccionados('3') == 1 || validaCamposSeleccionados('6') == 1 ||
							validaCamposSeleccionados('7') == 1) && (validaCamposSeleccionados('4') == 0 && 
							validaCamposSeleccionados('5') == 0) && (validaCamposSeleccionados('8') == 1)){
								alert("No es posible generar el reporte ya que los proveedores no se puede ligar con los demas "+
									"modulos seleccionados.");
								return;
							}
							else{
								if($("#mes1").val()!='00')
									query+=" from mdataStoreHistorialActual as md";
								else if(validaCamposSeleccionados('1') == 1 || validaCamposSeleccionados('2') == 1 ||
								validaCamposSeleccionados('3') == 1 || validaCamposSeleccionados('4') == 1 ||
								validaCamposSeleccionados('5') == 1 || validaCamposSeleccionados('6') == 1 ||
								validaCamposSeleccionados('7') == 1){
									query+=" from mdataStore as md";
									if(validaCamposSeleccionados('6') == 1)
										query+=" "+andSaldos;
									if(validaCamposSeleccionados('7') == 1)
										query+=" "+andPagos;
									if(validaCamposSeleccionados('8') == 1)
										query+=" left join "+andProveedores+" on md.cIdRfcPedidoContrato=tabProveedor.cIdRFCProveedor";
								}
								else
									query+=" from "+andProveedores;
							}
						}
					}
						
					
					var where=" where 1=1 ";
					var mes="";
					var unidadResponsable="";
					var cambs=new Array();
					var partida=new Array();
					/// creacion de where para programa anual
					$('input[name=progAnual]').each(function(){
	  					if (this.checked){
							  mes=$("#periodo").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 unidadResponsable=$("#URM").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 cambs=$('#CABMS').val();
							  partida=$('#partida').val();
							
							if (cambs!=""){
								 cambs=$('#CABMS').val().split(',');
							 }
							 if(partida!=""){
								 partida=$('#partida').val().split(','); 
							 }
	 					}
					});	
					if (mes!=""){
						where=where+ " and nIdPeriodo in("+mes+")";
					}
					if (cambs!=""){
						var qw=" and cIdCABM in(";
						var valores="";
						
						for (var i=0;i<cambs.length;i++){
							valores=valores+"'"+cambs[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					if (partida!=""){
						var qw=" and cIdpartidaPA in(";
						var valores="";
						for (var i=0;i<partida.length;i++){
							valores=valores+"'"+partida[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					if (unidadResponsable!=""){
						where=where+" and cIdUnidadEjecutoraPA in("+unidadResponsable+")";
					}

					// requisiciones
					var URMsol1="";
					var TipoSol1="";
					var EdoSol1="";
					var alcance1="";
					var Capitulo1="";
					var partidaRequisicion=new Array();
					var descripcionRequisicion="";
					var solicitudesRequisicion=new Array();
					var codigoRequisicion=new Array();
					$('input[name=requisicion]').each(function(){
	  					if (this.checked){
							  URMsol1=$("#URMsol").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 TipoSol1=$("#TipoSol").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 EdoSol1=$("#EdoSol").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 alcance1=$("#alcance").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 Capitulo1=$("#Capitulo").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();	 
							 
							  partidaRequisicion=$('#partidaRequisicion').val();
							  descripcionRequisicion=$('#descripcionRequisicion').val();
							  solicitudesRequisicion=$('#solicitudesRequisicion').val();
							  codigoRequisicion=$('#codigoRequisicion').val();
							if (partidaRequisicion!=""){
								 partidaRequisicion=partidaRequisicion.split(',');
							 }
							if (descripcionRequisicion!=""){
								 descripcionRequisicion=descripcionRequisicion.split(',');
							 }
							 if(solicitudesRequisicion!=""){
								 solicitudesRequisicion=solicitudesRequisicion.split(','); 
							 }
							 if(codigoRequisicion!=""){
								codigoRequisicion=codigoRequisicion.split(','); 
							 }
							}
	  					
	 					});	
	 					
	 				
					
					if (TipoSol1!=""){
						where=where+" and cIdTipoSolicitud in("+TipoSol1+")";
					}
					if (EdoSol1!=""){
						where=where+" and nIdEstadoSolicitud in("+EdoSol1+")";
					}
					if (alcance1!=""){
						where=where+" and cIdAlcance in("+alcance1+")";
					}
					if (Capitulo1!=""){
						where=where+" and cIdCapitulo in("+Capitulo1+")";
					}
					if (URMsol1!=""){
						where=where+" and UnidadEjecutora in("+URMsol1+")";
					}
					if (partidaRequisicion!=""){
						var qw=" and cIdSubPartida in(";
						var valores="";
						for (var i=0;i<partidaRequisicion.length;i++){
							valores=valores+"'"+partidaRequisicion[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					 }
					if (descripcionRequisicion!=""){
						var qw=" and cDescripcionSolicitud in(";
						var valores="";
						for (var i=0;i<descripcionRequisicion.length;i++){
							valores=valores+"'"+descripcionRequisicion[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					 }
					 if(solicitudesRequisicion!=""){
						var qw=" and cIdSolicitud in(";
						var valores="";
						for (var i=0;i<solicitudesRequisicion.length;i++){
							valores=valores+"'"+solicitudesRequisicion[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					 }
					 if(codigoRequisicion!=""){
						var qw=" and nIdClaveEgresos in(";
						var valores="";
						for (var i=0;i<codigoRequisicion.length;i++){
							valores=valores+"'"+codigoRequisicion[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					 }
					 
					// consolidado
					var URMConsolidado="";
					var TipoConsolidado="";
					var alcanceConsolidado="";
					var consolidadoC="";
					$('input[name=consolidado]').each(function(){
	  					if (this.checked){
							  URMConsolidado=$("#URMConsolidado").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 TipoConsolidado=$("#TipoConsolidado").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 alcanceConsolidado=$("#alcanceConsolidado").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 consolidadoC=$('#consolidadoC').val(); 
							
							if (consolidadoC!=""){
								 consolidadoC=consolidadoC.split(',');
							 }
						}
					});	
					
					if (consolidadoC!=""){
						var qw=" and cIdConsolidado in(";
						var valores="";
						
						for (var i=0;i<consolidadoC.length;i++){
							valores=valores+"'"+consolidadoC[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}  
					if (URMConsolidado!=""){
						where=where+" and cIdUnidadEjecutoraConsolidado in("+URMConsolidado+")";
					}
					if (TipoConsolidado!=""){
						where=where+" and cIdTipoConsolidado in("+TipoConsolidado+")";	
					}
					if (alcanceConsolidado!=""){
						where=where+" and nIdAlcanceConsolidado in("+alcanceConsolidado+")";
					}
					
					
					// procedimiento	    
					var URMProcedimiento="";
					var TipoProcedimiento="";
					var EdoProcedimiento="";
					var categoriaProcedimiento="";
					var tipoMonedaProc="";
					var descripcionProcedimiento="";
					var rfcProcedimiento="";
					var procedimientoP="";
					var razonSocialProcedimiento="";
					$('input[name=procedimiento]').each(function(){
	  					if (this.checked){ 
							 URMProcedimiento=$("#URMProcedimiento").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 TipoProcedimiento=$("#TipoProcedimiento").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 EdoProcedimiento=$("#EdoProcedimiento").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 categoriaProcedimiento=$("#categoriaProcedimiento").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 tipoMonedaProc=$("#tipoMonedaProc").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 descripcionProcedimiento=$('#descripcionProcedimiento').val(); 
							 rfcProcedimiento=$('#rfcProcedimiento').val();
							 procedimientoP=$('#procedimientoP').val();	
							 razonSocialProcedimiento=$('#razonSocialProcedimiento').val();	
							// razonSocialProc razonSocialProcedimiento
							 if ( descripcionProcedimiento!=""){
								  descripcionProcedimiento= descripcionProcedimiento.split(',');
							 }
							 if (rfcProcedimiento!=""){
								 rfcProcedimiento=rfcProcedimiento.split(',');
							 }
							 if (procedimientoP!=""){
								 procedimientoP=procedimientoP.split(',');
							 }
						}
					});	
					if ( descripcionProcedimiento!=""){
						var qw=" and cDescripcionProcedimiento in(";
						var valores="";
						
						for (var i=0;i<descripcionProcedimiento.length;i++){
							valores=valores+"'"+descripcionProcedimiento[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					 if (rfcProcedimiento!=""){
						var qw=" and cIdRFC LIKE '%25";
						var valores="";

						for (var i=0;i<rfcProcedimiento.length;i++){
							valores=valores+""+rfcProcedimiento[i]+",";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+"%25'";
						where=where+qw;
					 }
					 ///razon social
					  if (razonSocialProcedimiento!=""){
						var qw=" and cRFC LIKE '%25"+razonSocialProcedimiento+"%25'";
						where=where+qw;
					 }
					 if (procedimientoP!=""){
						var qw=" and cIdProcedimiento in(";
						var valores="";
						
						for (var i=0;i<procedimientoP.length;i++){
							valores=valores+"'"+procedimientoP[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					 }
					      
					if (URMProcedimiento!=""){
						where=where+" and cIdUnidadEjecutoraProc in("+URMProcedimiento+")";
					}
					if (TipoProcedimiento!=""){
						where=where+" and cIdTipoProcedimiento in("+TipoProcedimiento+")";
					}
					if (EdoProcedimiento!=""){
						where=where+" and nIdEstadoProcedimiento in("+EdoProcedimiento+")";
					}
					if (categoriaProcedimiento!=""){
						where=where+" and cIdCategoria in("+categoriaProcedimiento+")";
					}
					if (tipoMonedaProc!=""){
						where=where+" and cTipoMoneda in("+tipoMonedaProc+")";
					}
					
					////////////////////////
					///   PEDIDOS       ////
					////////////////////////
					var URMPedido="";
					var TipoPedido=new Array();
					var EdoPedido="";
					var descripcionPedido="";
					var numConsecutivoPedido="";
					var definitivoPedido="";
					var razonSocialPedido="";
					var rfcPedido="";
					
					$('input[name=pedido]').each(function(){
	  					if (this.checked){
	  						
							 URMPedido=$("#URMPedido").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 TipoPedido=$("#TipoPedido").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 EdoPedido=$("#EdoPedido").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							 
							 descripcionPedido=$('#descripcionPedido').val(); 
							 numConsecutivoPedido=$('#numConsecutivoPedido').val();
							 definitivoPedido=$('#definitivoPedido').val();

							 razonSocialPedido=$('#razonSocialPedido').val();
							 rfcPedido=$('#rfcPedido').val();
							 if (descripcionPedido!=""){
								  descripcionPedido=descripcionPedido.split(',');
							 }
							 if (numConsecutivoPedido!=""){
								 numConsecutivoPedido=numConsecutivoPedido.split(',');
							 }
							 
							 if (definitivoPedido!=""){
								 definitivoPedido=definitivoPedido.split(',');
							 }
							 
						}
					});	
					if (descripcionPedido!=""){
						var qw=" and cDescripcionPedidoContrato in(";
						var valores="";
						for (var i=0;i<descripcionPedido.length;i++){
							valores=valores+"'"+descripcionPedido[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					if (numConsecutivoPedido!=""){
						var qw=" and nIdConsecutivoPedidoCnt in(";
						var valores="";
						for (var i=0;i<numConsecutivoPedido.length;i++){
							valores=valores+"'"+numConsecutivoPedido[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					if (definitivoPedido!=""){
						var qw=" and cIdPedidoContratoDefinitivo in(";
						var valores="";
						for (var i=0;i<definitivoPedido.length;i++){
							valores=valores+"'"+definitivoPedido[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}  
					
					if (TipoPedido!=""){
						var qw=" and cIdTipoPedidoCnt in(";
						var valores="";
						if (TipoPedido.length>1){
							TipoPedido[2]="CB";
						}
						for (var i=0;i<TipoPedido.length;i++){
							valores=valores+"'"+TipoPedido[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					} 
					if (URMPedido!=""){
						where=where+" and cIdUnidadEjecutoraPedidoCnt in("+URMPedido+")";
					}
					
					if (EdoPedido!=""){
						where=where+" and cIdEstadoPedidoContrato in("+EdoPedido+")";
					}
					
					if (rfcPedido!=""){
						var qw=" and cIdRfcPedidoContrato LIKE '%25"+rfcPedido+"%25'";
						where=where+qw;
					 }
					 ///razon social
					  if (razonSocialPedido!=""){
						var qw=" and cRFC LIKE '%25"+razonSocialPedido+"%25'";
						where=where+qw;
					 }
					 ////////////////////////
					///   SAldos       ////
					////////////////////////
					var URMSaldo="";
					
					var partidaSaldo=new Array();
					$('input[name=saldos]').each(function(){
	  					if (this.checked){ 
	  						
							 URMSaldo=$("#URMSaldo").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							partidaSaldo=$('#partidaSaldo').val();
							 if(partidaSaldo!=""){
						 		partidaSaldo=$('#partidaSaldo').val().split(',');
							 }
							  /*partidaSaldo=$("#partidaSaldo").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();*/
						}
	  				});
					
					//aqui me quede
					if (URMSaldo!=""){
						where=where+" and cUnidadEjecutora in("+URMSaldo+")";
					}
					if (partidaSaldo!=""){
						alert("Partida Saldos");
						var qw=" and cPartida in(";
						var valores="";
						for (var i=0;i<partidaSaldo.length;i++){
							valores=valores+"'"+partidaSaldo[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					 ////////////////////////
					///   Pagos         ////
					////////////////////////
					var URMPagos="";
					
					var partidaPagos=new Array();
					var documentoPagos=new Array();
					$('input[name=Pagos]').each(function(){
	  					if (this.checked){ 
	  						
							 URMPagos=$("#URMPagos").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							partidaPagos=$('#partidaPagos').val();
							documentoPagos=$('#documentoPagos').val();
							 if(partidaPagos!=""){
						 		partidaPagos=$('#partidaPagos').val().split(',');
							 }
							
							 if(documentoPagos!=""){
						 		documentoPagos=$('#documentoPagos').val().split(',');
							 }
							 
						}
	  				});
					
					
					if (URMPagos!=""){
						where=where+" and cUnidadEjecutora_P in("+URMPagos+")";
					}
					if (partidaPagos!=""){
						var qw=" and cPartida_P in(";
						var valores="";
						for (var i=0;i<partidaPagos.length;i++){
							valores=valores+"'"+partidaPagos[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					if (documentoPagos!=""){
						var qw=" and cIdDocumento in(";
						var valores="";
						for (var i=0;i<documentoPagos.length;i++){
							valores=valores+"'"+documentoPagos[i]+"',";
						}
						valores=valores.substring(0,valores.length-1);
						qw=qw+valores+")";
						where=where+qw;
					}
					
					////////////////////////
					///   PROVEEDORES   ////
					////////////////////////
					var filtroRFC=new Array();
					var filtroRazonSocial=new Array();
					var filtroEntidadFederativa="";
					var filtroPyme="";
					var filtroTipoContribuyente="";
					var filtroHabilitado="";
					var filtroGiro=new Array();					
					
					$('input[name=proveedores]').each(function(){
						if(this.checked){
							filtroRFC = $("#rfcText").val();
							filtroRazonSocial = $("#razonSocialText").val();
							filtroEntidadFederativa = $("#entidadFederativaSelect").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							filtroPyme = $("#pymeSelect").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							filtroTipoContribuyente = $("#tipoContribuyenteSelect").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get(); 
							filtroHabilitado = $("#habilitadoSelect").multiselect("getChecked").map(function(){
								  return this.value;
							  }).get();
							filtroGiro = $("#giroText").val();
							
							if(filtroRFC != ""){
								filtroRFC = replaceAll(filtroRFC," ","");
								while (filtroRFC.toString().indexOf(",") != -1)
						      		filtroRFC = filtroRFC.toString().replace(",","' '");
						      	while (filtroRFC.toString().indexOf("' '") != -1)
						      		filtroRFC = filtroRFC.toString().replace("' '","','");
								filtroRFC = "'"+filtroRFC+"'";
							}
						}
					});
					
					if(filtroRFC != "")
						where+=" and tabProveedor.cIdRFCProveedor in ("+filtroRFC+") ";
					if(filtroRazonSocial != "")
						where+=" and tabProveedor.cRazonSocial like '%"+filtroRazonSocial+"%' ";
					if(filtroEntidadFederativa != "")
						where+=" and tabProveedor.cIdEntidadFederativa in ("+filtroEntidadFederativa+") ";
					if(filtroPyme != "")
						where+=" and tabProveedor.nIdPyme in ("+filtroPyme+") ";
					if(filtroTipoContribuyente != "")
						where+=" and tabProveedor.nTipoPersona in ("+filtroTipoContribuyente+")";
					if(filtroHabilitado != "")
						where+=" and nHabilitado in ("+filtroHabilitado+")";
					if(filtroGiro != "")
						where+=" and cGiro like '%"+filtroGiro+"%'";
					
					//Para la ganancia de lo programado y lo pagado(Prog anual-Procedimiento)
					if($('#ch_Ganancia').is(':checked')){
						where=where+" and Dif_Prog_proc > 0";
					}
					
					if($('#ch_Perdida').is(':checked')){
						if($('#ch_Ganancia').is(':checked'))
							where=where+" or Dif_Prog_proc < 0";
						else
							where=where+" and Dif_Prog_proc < 0";
					}
					
					if($('#ch_igual').is(':checked')){
						if($('#ch_Ganancia').is(':checked') )
							where=where+" or Dif_Prog_proc = 0";
						
						else{
							if( $('#ch_Perdida').is(':checked') )
								where=where+" or Dif_Prog_proc = 0";
							else
								where=where+" and Dif_Prog_proc = 0";
						}
							
					}
					//aqui concatenarle al where la fecha si es al dataStore Historico
					if(parseInt($("#HistoricoEjercicio").val(),10)!= parseInt($("#anoActual").val(),10)){
						
							//alert("Consultando el historico");
							var fechaInicio=$("#HistoricoEjercicio").val()+"-"+$("#mes1").val()+"-01 ";
							if($("#mes1").val()!='00')
								where=where+" and fechaCorte in('"+ fechaInicio +"') ";
							
					}
					else{
						if($("#mes1").val()!='00'){
							var FechaActual=new Date();
							var MesActual=FechaActual.getMonth()+1;//(f.getMonth() +1
							if(parseInt($("#mes1").val(),10) <= parseInt(MesActual,10)){
								var fechaInicio=$("#HistoricoEjercicio").val()+"-"+$("#mes1").val()+"-01 ";
								where=where+" and fechaCorte in('"+ fechaInicio +"') ";
							}
							else{
								alert("No hay historico despues de la fecha actual");
								return;
							}
								
							
						}
							
					}
					query=query+where;
				
					
					openCSV(query, encabezado);
					
				});
											
				//Historico del año
				$("#HistoricoEjercicio").change(function () {	
					
						
					muestrames();
					
				
				});
				
			});
			
			$('#tblColumnas tr').live('click', function() {    
				var cadena="";
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var aTrs = $('#tblColumnas').dataTable().fnGetNodes();       
				for ( var i=0; i<=aTrs.length; i++ ){
					if ($(aTrs[i]).hasClass('row_selected') ){
						var nTr = $('#tblColumnas').dataTable().fnGetData(aTrs[i]); 
						$('#tblColumnasSelec').dataTable().fnAddData(nTr);
						$('#tblColumnas').dataTable().fnDeleteRow( i );	
					}
				}
			});
			
			$('#tblColumnasSelec tr').live('dblclick', function() {   			
				arregloTmp=new Array();
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var aTrs = $('#tblColumnasSelec').dataTable().fnGetNodes();   
				for ( var i=0; i<=aTrs.length; i++ ){
					if ( $(aTrs[i]).hasClass('row_selected') ){          
						var nTr = $('#tblColumnasSelec').dataTable().fnGetData(aTrs[i]);
						$('#tblColumnas').dataTable().fnAddData(nTr);
						$('#tblColumnasSelec').dataTable().fnDeleteRow( i );
					}
				}
			});
			
			function consulta2(val,id){
				var division1=val;
				var cad='"#'+id+'"';
				if($('#'+id).is(':checked')){
					xDisplay($('#'+id).val(), 'block');
					if((parseInt(division1,10))==1){
						//vadquisiciones.push($('#'+id).val());
						querySelectPost("mPeriodoRead","periodo", {async:false});
						querySelectPost("mURMRead","URM", {async:false});
						
						$("#URM").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#periodo").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
					}
					if((parseInt(division1,10))==2){
						//vadquisiciones.push($('#'+id).val());
						querySelectPost("mURMRead","URMsol", {async:false}); 
						querySelectPost("mTipoSolicitudRead","TipoSol", {async:false});
						querySelectPost("mEstadoSolicitudRead","EdoSol", {async:false});
						querySelectPost("mAlcanceSolicitudRead","alcance", {async:false});
						querySelectPost("mcapituloSolicitudRead","Capitulo", {async:false});
						
						 $("#URMsol").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#TipoSol").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#EdoSol").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#alcance").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#Capitulo").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
					}
					if((parseInt(division1,10))==3){
						querySelectPost("mURMRead","URMConsolidado", {async:false}); 
						querySelectPost("mTipoConsolidadoReadMult","TipoConsolidado", {async:false});
						querySelectPost("mAlcanceSolicitudRead","alcanceConsolidado", {async:false});
						
						$("#URMConsolidado").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#TipoConsolidado").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
					}
					if((parseInt(division1,10))==4){
						querySelectPost("mURMRead","URMProcedimiento", {async:false}); 
						querySelectPost("mTipoProcedimientoRead","TipoProcedimiento", {async:false});
						querySelectPost("mEdoProcedimientoRead","EdoProcedimiento", {async:false}); 
						querySelectPost("mcategoriaProcedimientoRead","categoriaProcedimiento", {async:false});
						querySelectPost("mtipoMonedaProcRead","tipoMonedaProc", {async:false});
						
						$("#URMProcedimiento").multiselect({ 
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#TipoProcedimiento").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#EdoProcedimiento").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#categoriaProcedimiento").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#tipoMonedaProc").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
					}
					if((parseInt(division1,10))==5){
						querySelectPost("mEdoPedidoRead","EdoPedido", {async:false});
						querySelectPost("mURMRead","URMPedido", {async:false});
						
						$("#URMPedido").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
						$("#TipoPedido").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
						$("#EdoPedido").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
						});
					}
					if((parseInt(division1,10))==6){
						querySelectPost("mURMRead","URMSaldo", {async:false});
						$("#SaldosActivo").val('1');
						
						$("#URMSaldo").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
					}
					if((parseInt(division1,10))==7){
						querySelectPost("mURMRead","URMPagos", {async:false});
						$("#PagosActivo").val('1');
						   
						$("#URMPagos").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
					}
					if((parseInt(division1,10))==8){
						querySelectPost("mCatalogoEntidadFederativaEditarRead","entidadFederativaSelect", {async:false});
						querySelectPost("mPymeRead","pymeSelect", {async:false});
						
						$("#entidadFederativaSelect").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
						$("#pymeSelect").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
						$("#tipoContribuyenteSelect").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
						$("#habilitadoSelect").multiselect({
							noneSelectedText:"Elija una opción",
							header:"Elija una opción",
							selectedText: "# de # seleccionados"
							
						});
					}
				}else{
					xDisplay($('#'+id).val(), 'none');
					
					if((parseInt(division1,10))==1){
						$("#CABMS").val(""); $("#URM").multiselect("uncheckAll"); $("#periodo").multiselect("uncheckAll");$("#partida").val("");
					}
					if((parseInt(division1,10))==2){
						$("#URMsol").multiselect("uncheckAll"); $("#TipoSol").multiselect("uncheckAll"); $("#EdoSol").multiselect("uncheckAll"); 
						$("#alcance").multiselect("uncheckAll"); $("#Capitulo").multiselect("uncheckAll"); $("#partidaRequisicion").val("");
						$("#descripcionRequisicion").val(""); $("#solicitudesRequisicion").val(""); $("#codigoRequisicion").val(""); 

					}
					if((parseInt(division1,10))==3){
						$("#URMConsolidado").multiselect("uncheckAll"); $("#TipoConsolidado").multiselect("uncheckAll"); $("#alcanceConsolidado").multiselect("uncheckAll");$("#consolidadoC").val("");
					}
					if((parseInt(division1,10))==4){
						$("#URMProcedimiento").multiselect("uncheckAll"); $("#TipoProcedimiento").multiselect("uncheckAll"); $("#EdoProcedimiento").multiselect("uncheckAll");
						$("#categoriaProcedimiento").multiselect("uncheckAll"); $("#tipoMonedaProc").multiselect("uncheckAll"); $("#descripcionProcedimiento").val("");
						$("#rfcProcedimiento").val(""); $("#procedimientoP").val("");
					}
					if((parseInt(division1,10))==5){
						$("#URMPedido").multiselect("uncheckAll"); $("#TipoPedido").multiselect("uncheckAll"); $("#EdoPedido").multiselect("uncheckAll");
						$("#descripcionPedido").val(""); $("#numConsecutivoPedido").val(""); $("#definitivoPedido").val("");
					}
					if((parseInt(division1,10))==6){
						$("#SaldosActivo").val('0');
						$("#URMSaldo").multiselect("uncheckAll");$("#partidaSaldo").val("");
			
					}
					if((parseInt(division1,10))==7){
						$("#PagosActivo").val('0');
						$("#URMPagos").multiselect("uncheckAll");$("#partidaPagos").val("");$("#documentoPagos").val("");
					}
					if((parseInt(division1,10))==8){
						$("#rfcText").val("");
						$("#razonSocialText").val("");
						$("#entidadFederativaSelect").multiselect("uncheckAll");
						$("#pymeSelect").multiselect("uncheckAll");
						$("#tipoContribuyenteSelect").multiselect("uncheckAll");
						$("#habilitadoSelect").multiselect("uncheckAll");
						$("#giroText").val("");
					}
 					
				}
				
			}
			function cargaColumnas(){
				var vadquisiciones=new Array();
				$('input[name=adquisiciones]').each(function(){
	  					if (this.checked){
							vadquisiciones.push($(this).val());
						}
				});
				var proceso = "";
				arreglo=new Array();
				for (var i=vadquisiciones.length-1; i>=0;i--){
				 	arreglo.push(arreglosModulo(vadquisiciones[i],1));
				}
			    oTable.dataTable().fnClearTable();
			    columnasDisponibles("Descripcion",arreglo,0);	
			}
			function consulta(){
				var division1="";
				var vadquisiciones=new Array();
				
				$('input[name=adquisiciones]').each(function(){
	  					if (this.checked){
							vadquisiciones.push($(this).val());
							division1 =$(this).val();
							xDisplay(division1, 'block');
							if((parseInt(division1,10))==1){
								querySelectPost("mPeriodoRead","periodo", {async:false});
								querySelectPost("mURMRead","URM", {async:false});
								
								$("#URM").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#periodo").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
							}
							if((parseInt(division1,10))==2){
								querySelectPost("mURMRead","URMsol", {async:false}); 
								querySelectPost("mTipoSolicitudRead","TipoSol", {async:false});
								querySelectPost("mEstadoSolicitudRead","EdoSol", {async:false});
								querySelectPost("mAlcanceSolicitudRead","alcance", {async:false});
								querySelectPost("mcapituloSolicitudRead","Capitulo", {async:false});
								
								 $("#URMsol").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#TipoSol").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#EdoSol").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#alcance").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#Capitulo").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
							}
							if((parseInt(division1,10))==3){
								querySelectPost("mURMRead","URMConsolidado", {async:false}); 
								querySelectPost("mTipoConsolidadoReadMult","TipoConsolidado", {async:false});
								querySelectPost("mAlcanceSolicitudRead","alcanceConsolidado", {async:false});
								
								$("#URMConsolidado").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#TipoConsolidado").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
							}
							if((parseInt(division1,10))==4){
								querySelectPost("mURMRead","URMProcedimiento", {async:false}); 
								querySelectPost("mTipoProcedimientoRead","TipoProcedimiento", {async:false});
								querySelectPost("mEdoProcedimientoRead","EdoProcedimiento", {async:false}); 
								querySelectPost("mcategoriaProcedimientoRead","categoriaProcedimiento", {async:false});
								querySelectPost("mtipoMonedaProcRead","tipoMonedaProc", {async:false});
								
								$("#URMProcedimiento").multiselect({ 
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#TipoProcedimiento").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#EdoProcedimiento").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#categoriaProcedimiento").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#tipoMonedaProc").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
							}
							if((parseInt(division1,10))==5){
								querySelectPost("mEdoPedidoRead","EdoPedido", {async:false});
								querySelectPost("mURMRead","URMPedido", {async:false});
								
								$("#URMPedido").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
								$("#TipoPedido").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
								$("#EdoPedido").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
								});
							}
							if((parseInt(division1,10))==6){
								querySelectPost("mURMRead","URMSaldo", {async:false});
								$("#SaldosActivo").val('1');
								
								$("#URMSaldo").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
							}
							if((parseInt(division1,10))==7){
								querySelectPost("mURMRead","URMPagos", {async:false});
								$("#PagosActivo").val('1');
								   
								$("#URMPagos").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
							}
							if((parseInt(division1,10))==8){
								querySelectPost("mCatalogoEntidadFederativaEditarRead","entidadFederativaSelect", {async:false});
								querySelectPost("mPymeRead","pymeSelect", {async:false});
								
								$("#entidadFederativaSelect").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
								$("#pymeSelect").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
								$("#tipoContribuyenteSelect").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
								$("#habilitadoSelect").multiselect({
									noneSelectedText:"Elija una opción",
									header:"Elija una opción",
									selectedText: "# de # seleccionados"
									
								});
							}
	 					}else{
	 						division1 =$(this).val();
	 						xDisplay(division1, 'none');
	 						
	 						if((parseInt(division1,10))==1){
								$("#CABMS").val(""); $("#URM").multiselect("uncheckAll"); $("#periodo").multiselect("uncheckAll");$("#partida").val("");
							}
	 						if((parseInt(division1,10))==2){
								$("#URMsol").multiselect("uncheckAll"); $("#TipoSol").multiselect("uncheckAll"); $("#EdoSol").multiselect("uncheckAll"); 
								$("#alcance").multiselect("uncheckAll"); $("#Capitulo").multiselect("uncheckAll"); $("#partidaRequisicion").val("");
								$("#descripcionRequisicion").val(""); $("#solicitudesRequisicion").val(""); $("#codigoRequisicion").val(""); 

							}
	 						if((parseInt(division1,10))==3){
								$("#URMConsolidado").multiselect("uncheckAll"); $("#TipoConsolidado").multiselect("uncheckAll"); $("#alcanceConsolidado").multiselect("uncheckAll");$("#consolidadoC").val("");
							}
	 						if((parseInt(division1,10))==4){
								$("#URMProcedimiento").multiselect("uncheckAll"); $("#TipoProcedimiento").multiselect("uncheckAll"); $("#EdoProcedimiento").multiselect("uncheckAll");
								$("#categoriaProcedimiento").multiselect("uncheckAll"); $("#tipoMonedaProc").multiselect("uncheckAll"); $("#descripcionProcedimiento").val("");
								$("#rfcProcedimiento").val(""); $("#procedimientoP").val("");
							}
	 						if((parseInt(division1,10))==5){
								$("#URMPedido").multiselect("uncheckAll"); $("#TipoPedido").multiselect("uncheckAll"); $("#EdoPedido").multiselect("uncheckAll");
								$("#descripcionPedido").val(""); $("#numConsecutivoPedido").val(""); $("#definitivoPedido").val("");
							}
	 						if((parseInt(division1,10))==6){
								$("#SaldosActivo").val('0');
								$("#URMSaldo").multiselect("uncheckAll");$("#partidaSaldo").val("");
					
							}
							if((parseInt(division1,10))==7){
								$("#PagosActivo").val('0');
								$("#URMPagos").multiselect("uncheckAll");$("#partidaPagos").val("");$("#documentoPagos").val("");
							}
							if((parseInt(division1,10))==8){
								$("#rfcText").val("");
								$("#razonSocialText").val("");
								$("#entidadFederativaSelect").multiselect("uncheckAll");
								$("#pymeSelect").multiselect("uncheckAll");
								$("#tipoContribuyenteSelect").multiselect("uncheckAll");
								$("#habilitadoSelect").multiselect("uncheckAll");
								$("#giroText").val("");
							}
	 					}
				});
					
				var proceso = "";
				arreglo=new Array();
				for (var i=vadquisiciones.length-1; i>=0;i--){
				 arreglo.push(arreglosModulo(vadquisiciones[i],1));
				}
			    oTable.dataTable().fnClearTable();
			    columnasDisponibles("Descripcion",arreglo,0);			
			}
			function muestraSaldosPagos(){
				$("#chpagos").css("display", "");
				$("#chsaldos").css("display", "");
				$("#chProveedores").css("display", "");
				$("#inpSaldos").css("display", "");
				$("#inpPagos").css("display", "");
				$("#inpProveedor").css("display", "");
			}
			function ocultarSaldosPagos(){
				$("#chpagos").css("display", "none");
				$("#chsaldos").css("display", "none");
				$("#chProveedores").css("display", "none");
				$("#inpSaldos").css("display", "none");
				$("#inpPagos").css("display", "none");
				$("#inpProveedor").css("display", "none");
			}
			//Cambiar la condicion
			function muestrames(){
				var a =document.getElementById("meses");
				oTable.dataTable().fnClearTable();
				
				document.getElementById("chPanual").checked=false;
				document.getElementById("chsolicitu").checked=false;
				document.getElementById("chConsolidado").checked=false;
				document.getElementById("chProcedimiento").checked=false;
				document.getElementById("chPC").checked=false;
				document.getElementById("chsaldos").checked=false;
				document.getElementById("chpagos").checked=false;
				
				if ($("#HistoricoEjercicio").val()!= $("#anoActual").val()){
					xDisplay('procesos', 'block');
					ocultarSaldosPagos();
					$("#SaldosActivo").val('0');
					$("#PagosActivo").val('0');
					
				}
				else{
					muestraSaldosPagos();
				muestra();		
			}
					
			}
			function limpiaFiltros(){
				$('input[name=adquisiciones]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});	
					$('input[name=progAnual]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$('input[name=requisicion]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$('input[name=consolidado]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$('input[name=procedimiento]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$('input[name=pedido]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$('input[name=saldos]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$('input[name=pagos]').each(function(){
	  					if (this.checked){
							$(this).removeAttr("checked");	
	 					}
	  				});
					$("#CABMS").val(""); $("#URM").multiselect("uncheckAll"); $("#periodo").multiselect("uncheckAll");$("#partida").val("");
					
					$("#URMsol").multiselect("uncheckAll"); $("#TipoSol").multiselect("uncheckAll"); $("#EdoSol").multiselect("uncheckAll"); 
					$("#alcance").multiselect("uncheckAll"); $("#Capitulo").multiselect("uncheckAll"); $("#partidaRequisicion").val("");
					$("#descripcionRequisicion").val(""); $("#solicitudesRequisicion").val(""); $("#codigoRequisicion").val(""); 
					
					$("#URMConsolidado").multiselect("uncheckAll"); $("#TipoConsolidado").multiselect("uncheckAll"); $("#alcanceConsolidado").multiselect("uncheckAll");$("#consolidadoC").val("");
					
					$("#URMProcedimiento").multiselect("uncheckAll"); $("#TipoProcedimiento").multiselect("uncheckAll"); $("#EdoProcedimiento").multiselect("uncheckAll");
					$("#categoriaProcedimiento").multiselect("uncheckAll"); $("#tipoMonedaProc").multiselect("uncheckAll"); $("#descripcionProcedimiento").val("");
					$("#rfcProcedimiento").val(""); $("#procedimientoP").val("");
					
					$("#URMPedido").multiselect("uncheckAll"); $("#TipoPedido").multiselect("uncheckAll"); $("#EdoPedido").multiselect("uncheckAll");
					$("#descripcionPedido").val(""); $("#numConsecutivoPedido").val(""); $("#definitivoPedido").val("");
					
					$("#URMSaldo").multiselect("uncheckAll");$("#partidaSaldo").val("");
					
					$("#URMPagos").multiselect("uncheckAll");$("#partidaPagos").val("");$("#documentoPagos").val("");
					$("#rfcText").val("");
					$("#razonSocialText").val("");
					$("#entidadFederativaSelect").multiselect("uncheckAll");
					$("#pymeSelect").multiselect("uncheckAll");
					$("#tipoContribuyenteSelect").multiselect("uncheckAll");
					$("#habilitadoSelect").multiselect("uncheckAll");
					$("#giroText").val("");
					
					muestraURM(); 
					muestraSolicitudes(); 
					muestraConsolidado(); 
					muestraProcedimientos(); 
					muestraPedidos();
					muestraSaldos();
					muestraPagos();
					muestraProveedores();
			}
			function muestra(){			
				if (document.multilistador.todos.checked){
					xDisplay('procesos', 'none'); //ocultar
					xDisplay('1', 'block'); //mostrar
					xDisplay('2', 'block'); //mostrar
					xDisplay('3', 'block'); //mostrar
					xDisplay('4', 'block'); //mostrar
					xDisplay('5', 'block'); //mostrar
					xDisplay('6', 'block'); //mostrar
					xDisplay('7', 'block'); //mostrar
					xDisplay('8', 'block'); //mostrar
					
					if(parseInt($("#HistoricoEjercicio").val(),10)!= parseInt($("#anoActual").val(),10))
						var arreglo=arreglosModulo("todosHistorico",1);						
					else
						var arreglo=arreglosModulo("todos",1);	
					
					columnasDisponibles("Descripcion",arreglo,1);
					//// inhabilitar los check de adquisiciones
					limpiaFiltros();
				}else{
					xDisplay('procesos', 'block');
					xDisplay('1', 'none'); //ocultar
					xDisplay('2', 'none'); //ocultar
					xDisplay('3', 'none'); //ocultar
					xDisplay('4', 'none'); //ocultar
					xDisplay('5', 'none'); //ocultar	
					xDisplay('6', 'none'); //ocultar
					xDisplay('7', 'none'); //ocultar
					xDisplay('8', 'none'); //ocultar
					
					var arreglo=arreglosModulo("todos",1);
					columnasDisponibles("Descripcion",arreglo,1);
					oTable.dataTable().fnClearTable();				
				}
			}
			
			function columnasDisponibles(columna,modulo,tipo){
				oTable=$('#tblColumnas').dataTable({         
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
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 bAutoWidth: false,
					aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: columna, bVisible: false},
					{ sName: "idProceso", bVisible: false},
					{ sName: "Etiqueta"}
					
					]
				});
				
				aux=new Array();
				aux1=new Array();
				arrayCompleto=new Array();
				var l=0;
				if (tipo==1){
					l=0;
					for(var j=0; j<=modulo.length-3;j=j+3){
						arrayCompleto [l]=[modulo[j+1],modulo[j],modulo[j+2]];
						l++;
					}
				}
				else{
					l=0;
					for (var i=0;i<=modulo.length-1;i++){
						var cad=modulo[i];
						for(var j=0; j<=cad.length-3;j=j+3){
							arrayCompleto [l]=[cad[j+1],cad[j],cad[j+2]];
							l++;
						}
					}
				}
				$('#tblColumnas').dataTable().fnAddData(arrayCompleto);
			}

			/// programa anual
			function muestraURM(){
				var division="";
				$('input[name=progAnual]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');		
	 					}else{						
	 						division =$(this).val();
	 						xDisplay(division, 'none');
	 						if(division=="URMPA"){
								$("#URM").multiselect("uncheckAll"); 
							}
							if(division=="CABMSPA"){
								$("#CABMS").val("");
							}
							if(division=="PeriodoPA"){
								$("#periodo").multiselect("uncheckAll"); 
							}
							if(division=="partidaPA"){
								$("#partida").val(""); 
							}
	 					}
				});		
			}
			//// requisiciones
			function muestraSolicitudes(){
				var division="";
				$('input[name=requisicion]').each(function(){
	  					if (this.checked){
	  					    division =$(this).val();
							xDisplay(division, 'block');
	 					}else{
	 					   	division =$(this).val();
	 						xDisplay(division, 'none');
	 						if(division=="URMReq"){
	 							
								$("#URMsol").multiselect("uncheckAll");  
							}
							if(division=="TipoSolReq"){
								$("#TipoSol").multiselect("uncheckAll");
								
							}
							if(division=="EdoSolReq"){
								$("#EdoSol").multiselect("uncheckAll"); 
							}
							if(division=="alcanceReq"){
								$("#alcance").multiselect("uncheckAll"); 
							}
							if(division=="CapituloReq"){
								$("#Capitulo").multiselect("uncheckAll"); 
							}     
							if(division=="partidaReq"){
								$("#partidaRequisicion").val(""); 
							}
							 if(division=="descripcionReq"){
								$("#descripcionRequisicion").val(""); 
							}
							 if(division=="solicitudesReq"){
								$("#solicitudesRequisicion").val(""); 
							}
							 if(division=="codigoReq"){
								$("#codigoRequisicion").val(""); 
							}
	 					}
				});		
			}
			
			//// limpiar campos de consolidado
			function muestraConsolidado(){ 
				var division="";
				$('input[name=consolidado]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');
	 					}else{	
	 						division =$(this).val();
	 						xDisplay(division, 'none'); 
	 						if(division=="URMConsol"){
								$("#URMConsolidado").multiselect("uncheckAll");
							}
							if(division=="TipoConsol"){
								$("#TipoConsolidado").multiselect("uncheckAll"); 
							}
							if(division=="alcanceConsol"){
								$("#alcanceConsolidado").multiselect("uncheckAll"); 
							}
							 if(division=="consolidadoConsol"){
								$("#consolidadoC").val(""); 
							}
						}
				
				});
			}
			function muestraProcedimientos(){ 
				var division="";
				$('input[name=procedimiento]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');
	 					}else{	
	 						division =$(this).val();
	 						xDisplay(division, 'none'); 
	 						if(division=="URMProc"){
								$("#URMProcedimiento").multiselect("uncheckAll");
							}
							if(division=="TipoProc"){
								$("#TipoProcedimiento").multiselect("uncheckAll"); 
							}
							if(division=="EdoProc"){
								$("#EdoProcedimiento").multiselect("uncheckAll"); 
							}
							if(division=="acategoriaProc"){
								$("#categoriaProcedimiento").multiselect("uncheckAll"); 
							}
							if(division=="tipoMoneda"){
								$("#tipoMonedaProc").multiselect("uncheckAll"); 
							}     
							if(division=="descripcionProc"){
								$("#descripcionProcedimiento").val(""); 
							}
							 if(division=="rfcProc"){
								$("#rfcProcedimiento").val(""); 
							}
							 if(division=="procedimientoProc"){
								$("#procedimientoP").val(""); 
							}
							 if(division=="razonSocialProc"){
								$("#razonSocialProcedimiento").val(""); 
							}
							 
	 					}
	  			});
			}
			
			//pedidos o contratos
			function muestraPedidos(){ 
				var division="";
				$('input[name=pedido]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');
	 					}else{	
	 						division =$(this).val();
	 						xDisplay(division, 'none'); 
	 						if(division=="URMPed"){
								$("#URMPedido").multiselect("uncheckAll"); 
							}
							if(division=="TipoPed"){
								$("#TipoPedido").multiselect("uncheckAll"); 
							}
							if(division=="EdoPed"){
								$("#EdoPedido").multiselect("uncheckAll"); 
							}
							     
							if(division=="descripcionPed"){
								$("#descripcionPedido").val(""); 
							}
							 if(division=="numConsecutivoPed"){
								$("#numConsecutivoPedido").val(""); 
							}
							 if(division=="definitivoPed"){
								$("#definitivoPedido").val(""); 
							}

							 if(division=="rfcPed"){
								$("#rfcPedido").val(""); 
							}
							 if(division=="razonSocialPed"){
								$("#razonSocialPedido").val(""); 
							}
	 					} 
	  			});
			}
			//Saldos
			function muestraSaldos(){ 
				var division="";
				$('input[name=saldos]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');
	 					}else{	
	 						division =$(this).val();
	 						xDisplay(division, 'none'); 
	 						if(division=="URMSal"){
								$("#URMSaldo").multiselect("uncheckAll"); 
							}
							if(division=="partidaSal"){
								$("#partidaSaldo").val(""); 
							}
							
	 					} 
	  			});
			}
			//Pagos
			function muestraPagos(){ 
				var division="";
				$('input[name=pagos]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');
	 					}else{	
	 						division =$(this).val();
	 						xDisplay(division, 'none'); 
	 						if(division=="URMPag"){
								$("#URMPagos").multiselect("uncheckAll"); 
							}
							if(division=="partidaPag"){
								$("#partidaPagos").val(""); 
							}
							if(division=="documentoPag"){
								$("#DocumentoPagos").val(""); 
							}
							
	 					} 
	  					
	  			});
			}
			//Proveedores
			function muestraProveedores(){ 
				var division="";
				$('input[name=proveedores]').each(function(){
	  					if (this.checked){
							division =$(this).val();
							xDisplay(division, 'block');
	 					}else{	
	 						division =$(this).val();
	 						xDisplay(division, 'none');
							if(division == "rfc"){
								$("#rfcText").val(""); 
							}
							if(division == "razonSocial"){
								$("#razonSocialText").val(""); 
							}
							if(division == "entidadFederativa"){
								$("#entidadFederativaSelect").multiselect("uncheckAll");
							}
							if(division == "pyme"){
								$("#pymeSelect").multiselect("uncheckAll");
							}
							if(division == "tipoContribuyente"){
								$("#tipoContribuyenteSelect").multiselect("uncheckAll");
							}
							if(division == "habilitado"){
								$("#habilitadoSelect").multiselect("uncheckAll");
							}
							if(division == "giro"){
								$("#giroText").val(""); 
							}
							
	 					} 
	  					
	  			});
			}
	function openCSV(query, encabezado){
		window.open("","procesa","width=1024,height=608,top=10,resizable=yes,scrollbars=1","popacuse");
		$("#encabezado").val(encabezado);
		$("#query").val(query);
		document.enviaDatos.submit();
		
	}
	
	function arreglosModulo(nummodulo, quitarCamposSeleccionados){
		switch(nummodulo){
		case 'todos':
				var todos=new Array();
				var arregloMod=new Array();
				 	todos=[
				 			//Programa Anual
							"1",	"cEjercicio",	"Ejercicio",
							"1",	"cIdUnidadEjecutoraPA",	"Unidad Ejecutora Programa Anual",
							"1",	"Descripcion_UEPA",	"Descripcion de Unidad Ejecutora Programa Anual",
							"1",	"cIdCABM",	"CUCOP",
							"1",	"cIdPartidaPA",	"Partida Programa Anual",
							"1",	"cSubPartidaPA",	"SubPartida Programa Anual",
							"1",	"mImporteBrutoPA",	"Importe Bruto Programa Anual",
							"1",	"mImporteNetoPA",	"Importe Neto Programa Anual",
							"1",	"mDisponibilidadPA",	"Disponibilidad Programa  Anual",
							"1",	"mMontoNetoEnSolicitudesPA",	"Monto Neto en Requisiciones de Programa Anual",
							"1",	"mPrecioUnitarioPA",	"Precio Unitario Programa Anual",
							"1",	"nCantidadPA",	"Cantidad De Programa Anual",
							"1",	"nCantidadDisponibilidadPA",	"Cantidad Disponibles de Programa Anual",
							"1",	"nCantidadEnSolicitudesPA",	"Cantidad en Requisiciones de Programa Anual",
							"1",	"nIdPeriodo",	"Periodo",

							//Requisicion
							"2",	"cIdSolicitud",	"Requisicion",
							"2",	"cTipoSolicitud",	"Tipo de Requisicion",
							"2",	"UnidadEjecutora",	"Unidad Ejecutora Requisicion",
							"2",	"descripcionUESolicitud",	"Descripcion de Unidad Ejecutora Requisicion",
							"2",	"cDescripcionSolicitud",	"Descripcion de Requisicion",
							"2",	"cAlcance",	"Alcance Requisicion",
							"2",	"cEstadoSolicitud",	"Estado de Requisicion",
							"2",	"cIdSubPartida",	"Id SubPartida Requisicion",
							"2",	"cSubPartida",	"SubPartida Requisicion",
							"2",	"cIdCapitulo",	"Capitulo Requisicion",
							"2",	"cIdEntidadContableSolicitud",	"Entidad Contable Requisicion",
							"2",	"mMontoBrutoSolicitud",	"Monto Bruto Requisicion",
							"2",	"mMontoNetoSolicitud",	"Monto Neto Requisicion",
							"2",	"nIdLineaSolicitud",	"Linea Requisicion",
							"2",	"cDescripcionLineaSolicitud",	"Descripcion Linea Requisicion",
							"2",	"nCantidadLineaSolicitud",	"Cantidad de Linea Requisicion",
							"2",	"mPrecioUnitarioLineaSolicitud",	"Precio Unitario Linea Requisicion",
							"2",	"nPorcentajeIVALineaSolicitud",	"Porcentaje IVA Linea Requisicion",
							"2",	"cOLI",	"OLI Requisicion",
							"2",	"nIdClaveEgresos",	"Clave Egresos",
							"2",	"mClaveSolicitud",	"Clave de Requisicion",
							"2",	"mNotas",	"Notas",
								
							//Consolidado
							"3",	"cIdConsolidado",	"Consolidado",
							"3",	"cIdTipoConsolidado",	"Tipo de Consolidado",
							"3",	"cIdUnidadEjecutoraConsolidado",	"Unidad Ejecutora Consolidado",
							"3",	"DescripcionUnidadEjecutoraConsolidado",	"Descripcion de Unidad Ejecutora Consolidado",
							"3",	"desAlcanceConsolidado",	"Alcance Consolidado",
							"3",	"mConsolidadoBruto",	"Monto Bruto Consolidado",
							"3",	"mConsolidadoNeto",	"Monto Neto Consolidado",
							"3",	"nIdLineaConsolidado",	"Linea Consolidado",
							"3",	"cDescripcionLineaConsolidado",	"Descripcion Linea Consolidado",
							"3",	"cIdUnidadMedida",	"Unidad de Medida",
							"3",	"nCantidadLineaConsolidado",	"Cantidad Linea Consolidado",
							"3",	"mMontoLineaConsolidadoBruto",	"Monto Bruto Linea Consolidado",
							"3",	"mMontoLineaConsolidadoNeto",	"Monto Neto Linea Consolidado",
								
							//Procedimiento
							"4",	"cIdProcedimiento",	"Procedimiento",
							"4",	"TipoProceso",  "Tipo de Proceso",
							"4",	"cIdTipoProcedimiento",	"Tipo de Procedimiento",
							"4",	"cCategoriaProcedimiento",	"Categoria Procedimiento",
							"4",	"cDescripcionProcedimiento",	"Descripcion Procedimiento",
							"4",	"cEstadoProcedimiento",	"Estado Procedimiento",
							"4",	"cOficio",	"Oficio Procedimiento",
							"4",	"cIdRFC",	"RFC (Procedimiento)",
							"4",	"cRFC",	"Razón Social (Procedimiento)",
							"4",	"cDescripcionPartidaProcedimiento",	"Descripcion Partida Procedimiento",
							"4",	"mMontoPrecioUnitarioProcedimiento",	"Monto Precio Unitario Procedimiento",
							"4",	"nPorcentajeIVAProcedimiento",	"Porcentaje IVA Procedimiento",
							"4",	"cTipoCambioProcedimiento",	"Tipo de cambio Procedimiento",
							"4",	"cTipoMoneda",	"Tipo Moneda",
							"4",	"mMontoBrutoPartidaProcedimiento",	"Monto Bruto Partida Procedimiento",
							"4",	"mMontoNetoPartidaProcedimiento",	"Monto Neto Partida Procedimiento",
							"4",	"cIdUnidadEjecutoraProc",	"Unidad Ejecutora de Procedimiento",
							"4",	"direccionProveedor",	"Direccion",
							"4",	"telefonoProveedor",	"Telefono",
							"4",	"nFechaConvocatoria",	"Fecha de convocatoria",
							"4",	"nFechaApertura",	"Fecha de apertura de ofertas",
							"4",	"nFechaJunta",	"Fecha de junta de aclaraciones",
							"4",	"nFechaEvaluacionT",	"Fecha de evaluación técnica",
							"4",	"nFechaEvaluacionE",	"Fecha de evaluación económica",
							"4",	"nFechaFallo",	"Fecha de Fallo",
							"4",	"nFechaFormalizacion",	"Fecha de formalización",
							"4",	"nFechaEntrega",	"Fecha de entrega de bienes, o inicio de los servicios",
							"4",	"nFechaConclusion",	"Conclusión de los servicios",
							"4",	"Dif_Prog_proc",	"Diferencia de Programado y Pagado",

							//Pedidos y Contratos
							"5",	"cIdPedidoContrato",	"Pedido o Contrato",
							"5",	"cIdEntidadContablePedido",	"Entidad Contable Pedido",
							"5",	"mTipoCambioPedidoContrato",	"Tipo de Cambio de Pedido o Contrato",
							"5",	"cEstadoPedidoContrato",	"Estado Pedido o Contrato",
							"5",	"cIdPedidoContratoDefinitivo",	"Pedido o Contrato Definitivo",
							"5",	"cDescripcionPedidoContrato",	"Descripcion de Pedido o Contrato",
							"5",	"mMontoPedidoContratoBruto",	"Monto Bruto de Pedido o Contrato",
							"5",	"mPedidoContratoNeto",	"Monto  Neto de Pedido o Contrato",
							"5",	"fFallo",	"Fecha de fallo de Pedido o Contrato",
							"5",	"fFormalizacion",	"Fecha de Formalizacion de Pedido o Contrato",
							"5",	"fEntrega",	"Fecha de Entrega de Pedido o Contrato",
							"5",	"fInicio",	"Fecha de Inicio de Pedido o Contrato",
							"5",	"fFin",	"Fecha de Fin de Pedido o Contrato",
							"5",	"cIdRfcPedidoContrato",	"RFC (Pedido)",
							"5",	"cRFC",	"Razón Social (Pedido)",
							"5",	"cIdTipoPedidoCnt",	"Tipo Pedido o Contrato",
							"5",	"cIdUnidadEjecutoraPedidoCnt",	"Unidad Ejecutora de Pedido o Contrato",
							"5",	"nIdConsecutivoPedidoCnt",	"Consecutivo de Pedido o Contrato",
							"5",	"MontoMinimoPedido",	"Monto minimo Pedido/Contrato",
							"5",	"MontoMaximoPedido",	"Monto maximo Pedido/Contrato",
							"5",	"mTotalModificacionPedidoContrato",	"Monto Total Modificado Pedido/contrato",
							"5",	"cIdPedidoContratoModificado",	"cId Pedido/Contrato Modificado",
							"5",	"cCotizador",	"Cotizador",
							//Saldos
							"6",	"cUnidadEjecutora",	"Unidad Ejecutora de Saldos",
							"6",	"cEntidadFederativa",	"Entidad Federativa Saldos",
							"6",	"cPartida",	"Partida Saldos",
							"6",	"aEjercicioFiscal",	"Ejercicio Saldos",
							"6",	"cSubCuenta",	"Subcuenta Saldos",
							"6",	"Original",	"Monto Original Saldos",
							"6",	"Modificado",	"Monto Modificado Saldos",
							"6",	"Comprometido",	"Monto Comprometido Saldos",
							"6",	"Saldo",	"Saldo Saldos",
							"6",	"Devengado",	"Devengado Saldos",
							"6",	"EjercidoPagado",	"Ejercido Pagado Saldos",
							"6",	"EjercidoNoPagado",	"Ejercido No Pagado Saldos",
							"6",	"totalPagado",	"Total Pagado Saldos",

							//Pagos
							"7",	"folioTipoDocto",	"Folio documento de Pagos",
							"7",	"tipoDocto",	"Tipo documento Pagos",
							"7",	"cIdDocumento",	"Documento Pagos",
							"7",	"caNoContrarrecibo",	"Contrarrecibo Pagos",
							"7",	"cIdRFC_P",	"RFC Pagos",
							"7",	"Nombre",	"Nombre Pagos",
							"7",	"cConcepto",	"Concepto Pagos",
							"7",	"[mImporteBruto(Total Pedido/Contrato)]",	"Importe total Bruto pedido/contrato Pagos",
							"7",	"[mImporteNeto(Total Pedido/Contrato)]",	"Importe total Neto pedido/contrato Pagos",
							"7",	"EP",	"EP Pagos",
							"7",	"NetoPorEp",	"Monto neto x EP Pagos",
							"7",	"cMes",	"Mes Pagos",
							"7",	"cPartida_P",	"Partida Pagos",
							"7",	"cUnidadEjecutora_P",	"Unidad Ejecutora Pagos",
							"7",	"foliotipo",	"Tipo de folio Pagos",
							"7",	"fAplicacion",	"Fecha de Ampliacion Pagos",
							"7",	"fProgramadaPago",	"Fecha de pago programada Pagos",
							"7",	"totalSinRetenciones",	"Total Sin retenciones Pagos",
							"7",	"mImportePenalizacion",	"Importe Penalizado",
							//Proveedores
							"8",	"tipoPersona",	"Tipo de Contribuyente",
							"8",	"cIdRFCProveedor",	"RFC Proveedor",
							"8",	"cRazonSocial",	"Razon Social Proveedor",
							"8",	"cEntidadFederativaProveedor",	"Entidad Federativa Proveedor",
							"8",	"cPyme",	"Pyme Proveedor",
							"8",	"lHabilitado",	"Habilitado Proveedor",
							"8",	"cGiro",	"Giro Proveedor",
							"8",	"cNumeroRegistro",	"Numero de Registro Proveedor",
							"8",	"cRepresentanteLegal",	"Representante Legal Proveedor",
							"8",	"cCalle",	"Calle Proveedor",
							"8",	"cNumeroExterno",	"Numero Externo Proveedor",
							"8",	"cNumeroInterno",	"Numero Interno Proveedor",
							"8",	"cColonia",	"Colonia Proveedor",
							"8",	"cMunicipio",	"Delegacion o Municipio Proveedor",
							"8",	"cCodigoPostal",	"Codigo Postal Proveedor",
							"8",	"cTelefono",	"Telefono(s) Proveedor",
							"8",	"cEmail",	"Mail Proveedor",
							"8",	"cUrl",	"Pagina Web Proveedor",
							"8",	"dCURP",	"CURP Proveedor",
							"8",	"CBEN",	"Clave Proveedor"	
						];

				if(quitarCamposSeleccionados == 1)
				 	arregloMod = columnasSeleccionadas(todos);
				else
					arregloMod = todos;
				return arregloMod;
		break;
		case '1':
			/////////////////////programa anual//////////////
			var progAnualArray=new Array();
			var arregloMod=new Array();
				 progAnualArray=["1",	"cEjercicio",	"Ejercicio",
							"1",	"cIdUnidadEjecutoraPA",	"Unidad Ejecutora Programa Anual",
							"1",	"Descripcion_UEPA",	"Descripcion de Unidad Ejecutora Programa Anual",
							"1",	"cIdCABM",	"CUCOP",
							"1",	"cIdPartidaPA",	"Partida Programa Anual",
							"1",	"cSubPartidaPA",	"SubPartida Programa Anual",
							"1",	"mImporteBrutoPA",	"Importe Bruto Programa Anual",
							"1",	"mImporteNetoPA",	"Importe Neto Programa Anual",
							"1",	"mDisponibilidadPA",	"Disponibilidad Programa  Anual",
							"1",	"mMontoNetoEnSolicitudesPA",	"Monto Neto en Requisiciones de Programa Anual",
							"1",	"mPrecioUnitarioPA",	"Precio Unitario Programa Anual",
							"1",	"nCantidadPA",	"Cantidad De Programa Anual",
							"1",	"nCantidadDisponibilidadPA",	"Cantidad Disponibles de Programa Anual",
							"1",	"nCantidadEnSolicitudesPA",	"Cantidad en Requisiciones de Programa Anual",
							"1",	"nIdPeriodo",	"Periodo"];
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(progAnualArray);
				else
					arregloMod = progAnualArray;
				
				return arregloMod;
		break;
		case '2':
				 //////////////////Requisiciones/////////////////
				 var requisicionArray=new Array();
				 var arregloMod=new Array();
				 requisicionArray=["2",	"cIdSolicitud",	"Requisicion",
								//"1",	"cIdTipoSolicitud",	"Tipo de Requisicion",
								"2",	"cTipoSolicitud",	"Tipo de Requisicion",
								"2",	"UnidadEjecutora",	"Unidad Ejecutora Requisicion",
								"2",	"descripcionUESolicitud",	"Descripcion de Unidad Ejecutora Requisicion",
								"2",	"cDescripcionSolicitud",	"Descripcion de Requisicion",
								"2",	"cAlcance",	"Alcance Requisicion",
								"2",	"cEstadoSolicitud",	"Estado de Requisicion",
								"2",	"cIdSubPartida",	"Id SubPartida Requisicion",
								"2",	"cSubPartida",	"SubPartida Requisicion",
								"2",	"cIdCapitulo",	"Capitulo Requisicion",
								"2",	"cIdEntidadContableSolicitud",	"Entidad Contable Requisicion",
								"2",	"mMontoBrutoSolicitud",	"Monto Bruto Requisicion",
								"2",	"mMontoNetoSolicitud",	"Monto Neto Requisicion",
								"2",	"nIdLineaSolicitud",	"Linea Requisicion",
								"2",	"cDescripcionLineaSolicitud",	"Descripcion Linea Requisicion",
								"2",	"nCantidadLineaSolicitud",	"Cantidad de Linea Requisicion",
								"2",	"mPrecioUnitarioLineaSolicitud",	"Precio Unitario Linea Requisicion",
								"2",	"nPorcentajeIVALineaSolicitud",	"Porcentaje IVA Linea Requisicion",
								"2",	"cOLI",	"OLI Requisicion",
								"2",	"nIdClaveEgresos",	"Clave Egresos",
								"2",	"mClaveSolicitud",	"Clave de Requisicion",
								"2",	"mNotas",	"Notas"];
								
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(requisicionArray);
				else
					arregloMod = requisicionArray;
					
				return arregloMod;
		break;
		case '3':
				///////////////////////consolidado//////////////////	 
				 var consolidadoArray=new Array();
				 consolidadoArray=["3",	"cIdConsolidado",	"Consolidado",
									"3",	"cIdTipoConsolidado",	"Tipo de Consolidado",
									"3",	"cIdUnidadEjecutoraConsolidado",	"Unidad Ejecutora Consolidado",
									"3",	"DescripcionUnidadEjecutoraConsolidado",	"Descripcion de Unidad Ejecutora Consolidado",
									"3",	"desAlcanceConsolidado",	"Alcance Consolidado",
									"3",	"mConsolidadoBruto",	"Monto Bruto Consolidado",
									"3",	"mConsolidadoNeto",	"Monto Neto Consolidado",
									"3",	"nIdLineaConsolidado",	"Linea Consolidado",
									"3",	"cDescripcionLineaConsolidado",	"Descripcion Linea Consolidado",
									"3",	"cIdUnidadMedida",	"Unidad de Medida",
									"3",	"nCantidadLineaConsolidado",	"Cantidad Linea Consolidado",
									"3",	"mMontoLineaConsolidadoBruto",	"Monto Bruto Linea Consolidado",
									"3",	"mMontoLineaConsolidadoNeto",	"Monto Neto Linea Consolidado"];
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(consolidadoArray);
				else
					arregloMod = consolidadoArray;
					
				 return arregloMod;
				 
		break;
		case '4':
			var procedimientoArray=new Array();
				procedimientoArray=["4",	"cIdProcedimiento",	"Procedimiento",
									"4",	"TipoProceso",  "Tipo de Proceso",
									"4",	"cIdTipoProcedimiento",	"Tipo de Procedimiento",
									"4",	"cCategoriaProcedimiento",	"Categoria Procedimiento",
									"4",	"cDescripcionProcedimiento",	"Descripcion Procedimiento",
									"4",	"cEstadoProcedimiento",	"Estado Procedimiento",
									"4",	"cOficio",	"Oficio Procedimiento",
									"4",	"cIdRFC",	"RFC (Procedimiento)",
									"4",	"cRFC",	"Razón Social (Procedimiento)",
									"4",	"cDescripcionPartidaProcedimiento",	"Descripcion Partida Procedimiento",
									"4",	"mMontoPrecioUnitarioProcedimiento",	"Monto Precio Unitario Procedimiento",
									"4",	"nPorcentajeIVAProcedimiento",	"Porcentaje IVA Procedimiento",
									"4",	"cTipoCambioProcedimiento",	"Tipo de cambio Procedimiento",
									//	"4",	"nIdTipoCambioProcedimiento",	"Tipo de cambio Procedimiento",
									"4",	"cTipoMoneda",	"Tipo Moneda",
									"4",	"mMontoBrutoPartidaProcedimiento",	"Monto Bruto Partida Procedimiento",
									"4",	"mMontoNetoPartidaProcedimiento",	"Monto Neto Partida Procedimiento",
									"4",	"cIdUnidadEjecutoraProc",	"Unidad Ejecutora de Procedimiento",
									"4",	"direccionProveedor",	"Direccion",
									"4",	"telefonoProveedor",	"Telefono",
									"4",	"nFechaConvocatoria",	"Fecha de convocatoria",
									"4",	"nFechaApertura",	"Fecha de apertura de ofertas",
									"4",	"nFechaJunta",	"Fecha de junta de aclaraciones",
									"4",	"nFechaEvaluacionT",	"Fecha de evaluación técnica",
									"4",	"nFechaEvaluacionE",	"Fecha de evaluación económica",
									"4",	"nFechaFallo",	"Fecha de Fallo",
									"4",	"nFechaFormalizacion",	"Fecha de formalización",
									"4",	"nFechaEntrega",	"Fecha de entrega de bienes, o inicio de los servicios",
									"4",	"nFechaConclusion",	"Conclusión de los servicios",
									"4",	"Dif_Prog_proc",	"Diferencia de Programado y Pagado"];
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(procedimientoArray);
				else
					arregloMod = procedimientoArray;
					
				 return arregloMod;
		break;
		case '5':
			////////////////////////// Pedidos o contratros/////////////
		
				var pedidoArray=new Array();
				pedidoArray=["5",	"cIdPedidoContrato",	"Pedido o Contrato",
							"5",	"cIdEntidadContablePedido",	"Entidad Contable Pedido",
							"5",	"mTipoCambioPedidoContrato",	"Tipo de Cambio de Pedido o Contrato",
							"5",	"cEstadoPedidoContrato",	"Estado Pedido o Contrato",
							"5",	"cIdPedidoContratoDefinitivo",	"Pedido o Contrato Definitivo",
							"5",	"cDescripcionPedidoContrato",	"Descripcion de Pedido o Contrato",
							"5",	"mMontoPedidoContratoBruto",	"Monto Bruto de Pedido o Contrato",
							"5",	"mPedidoContratoNeto",	"Monto  Neto de Pedido o Contrato",
							"5",	"fFallo",	"Fecha de fallo de Pedido o Contrato",
							"5",	"fFormalizacion",	"Fecha de Formalizacion de Pedido o Contrato",
							"5",	"fEntrega",	"Fecha de Entrega de Pedido o Contrato",
							"5",	"fInicio",	"Fecha de Inicio de Pedido o Contrato",
							"5",	"fFin",	"Fecha de Fin de Pedido o Contrato",
							"5",	"cIdRfcPedidoContrato",	"RFC (Pedido)",
							"5",	"cRFC",	"Razón Social (Pedido)",
							"5",	"cIdTipoPedidoCnt",	"Tipo Pedido o Contrato",
							"5",	"cIdUnidadEjecutoraPedidoCnt",	"Unidad Ejecutora de Pedido o Contrato",
							"5",	"nIdConsecutivoPedidoCnt",	"Consecutivo de Pedido o Contrato",
							"5",	"MontoMinimoPedido",	"Monto minimo Pedido/Contrato",
							"5",	"MontoMaximoPedido",	"Monto maximo Pedido/Contrato",
							"5",	"mTotalModificacionPedidoContrato",	"Monto Total Modificado Pedido/contrato",
							"5",	"cIdPedidoContratoModificado",	"cId Pedido/Contrato Modificado",
							"5",	"cCotizador",	"Cotizador"];	
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(pedidoArray);
				else
					arregloMod = pedidoArray;
				
				 return arregloMod;
		break;
		case '6':
				///////////////////////Saldos//////////////////	 
				 var SaldosArray=new Array();
				 SaldosArray=["6",	"cUnidadEjecutora",	"Unidad Ejecutora de Saldos",
							  "6",	"cEntidadFederativa",	"Entidad Federativa Saldos",
							  "6",	"cPartida",	"Partida Saldos",
							  "6",	"aEjercicioFiscal",	"Ejercicio Saldos",
							  "6",	"cSubCuenta",	"Subcuenta Saldos",
							  "6",	"Original",	"Monto Original Saldos",
							  "6",	"Modificado",	"Monto Modificado Saldos",
							  "6",	"Comprometido",	"Monto Comprometido Saldos",
							  "6",	"Saldo",	"Saldo Saldos",
							  "6",	"Devengado",	"Devengado Saldos",
							  "6",	"EjercidoPagado",	"Ejercido Pagado Saldos",
							  "6",	"EjercidoNoPagado",	"Ejercido No Pagado Saldos",
							  "6",	"totalPagado",	"Total Pagado Saldos"];
					
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(SaldosArray);
				else
					arregloMod = SaldosArray;
				 
				 return arregloMod;
				 
		break;
		case '7':
				///////////////////////Pagos//////////////////	 
				 var PagosArray=new Array();
				 PagosArray=["7",	"folioTipoDocto",	"Folio documento de Pagos",
							 "7",	"tipoDocto",	"Tipo documento Pagos",
							 "7",	"cIdDocumento",	"Documento Pagos",
							 "7",	"caNoContrarrecibo",	"Contrarrecibo Pagos",
							 "7",	"cIdRFC_P",	"RFC Pagos",
							 "7",	"Nombre",	"Nombre Pagos",
							 "7",	"cConcepto",	"Concepto Pagos",
							 "7",	"[mImporteBruto(Total Pedido/Contrato)]",	"Importe total Bruto pedido/contrato Pagos",
							 "7",	"[mImporteNeto(Total Pedido/Contrato)]",	"Importe total Neto pedido/contrato Pagos",
							 "7",	"EP",	"EP Pagos",
							 "7",	"NetoPorEp",	"Monto neto x EP Pagos",
							 "7",	"cMes",	"Mes Pagos",
							 "7",	"cPartida_P",	"Partida Pagos",
							 "7",	"cUnidadEjecutora_P",	"Unidad Ejecutora Pagos",
							 "7",	"foliotipo",	"Tipo de folio Pagos",
							 "7",	"fAplicacion",	"Fecha de Ampliacion Pagos",
							 "7",	"fProgramadaPago",	"Fecha de pago programada Pagos",
							 "7",	"totalSinRetenciones",	"Total Sin retenciones Pagos",
							 "7",	"mImportePenalizacion",	"Importe Penalizado"];
								
				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(PagosArray);
				else
					arregloMod = PagosArray;
				 
				 return arregloMod;
		break;
		case '8':
				///////////////////////Proveedores//////////////////	 
				 var datosProveedores=new Array();
				 datosProveedores=[	"8",	"tipoPersona",	"Tipo de Contribuyente",
									"8",	"cIdRFCProveedor",	"RFC Proveedor",
									"8",	"cRazonSocial",	"Razon Social Proveedor",
									"8",	"cEntidadFederativaProveedor",	"Entidad Federativa Proveedor",
									"8",	"cPyme",	"Pyme Proveedor",
									"8",	"lHabilitado",	"Habilitado Proveedor",
									"8",	"cGiro",	"Giro Proveedor",
									"8",	"cNumeroRegistro",	"Numero de Registro Proveedor",
									"8",	"cRepresentanteLegal",	"Representante Legal Proveedor",
									"8",	"cCalle",	"Calle Proveedor",
									"8",	"cNumeroExterno",	"Numero Externo Proveedor",
									"8",	"cNumeroInterno",	"Numero Interno Proveedor",
									"8",	"cColonia",	"Colonia Proveedor",
									"8",	"cMunicipio",	"Delegacion o Municipio Proveedor",
									"8",	"cCodigoPostal",	"Codigo Postal Proveedor",
									"8",	"cTelefono",	"Telefono(s) Proveedor",
									"8",	"cEmail",	"Mail Proveedor",
									"8",	"cUrl",	"Pagina Web Proveedor",
									"8",	"dCURP",	"CURP Proveedor",
									"8",	"CBEN",	"Clave Proveedor"
									];

				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(datosProveedores);
				else
					arregloMod = datosProveedores;
				 
				 return arregloMod;
				 
		break;
		case 'todosHistorico':
				var todosHistorico=new Array();
				var arregloMod=new Array();
				 todosHistorico=[
				 			//Programa Anual
							"1",	"cEjercicio",	"Ejercicio",
							"1",	"cIdUnidadEjecutoraPA",	"Unidad Ejecutora Programa Anual",
							"1",	"Descripcion_UEPA",	"Descripcion de Unidad Ejecutora Programa Anual",
							"1",	"cIdCABM",	"CUCOP",
							"1",	"cIdPartidaPA",	"Partida Programa Anual",
							"1",	"cSubPartidaPA",	"SubPartida Programa Anual",
							"1",	"mImporteBrutoPA",	"Importe Bruto Programa Anual",
							"1",	"mImporteNetoPA",	"Importe Neto Programa Anual",
							"1",	"mDisponibilidadPA",	"Disponibilidad Programa  Anual",
							"1",	"mMontoNetoEnSolicitudesPA",	"Monto Neto en Requisiciones de Programa Anual",
							"1",	"mPrecioUnitarioPA",	"Precio Unitario Programa Anual",
							"1",	"nCantidadPA",	"Cantidad De Programa Anual",
							"1",	"nCantidadDisponibilidadPA",	"Cantidad Disponibles de Programa Anual",
							"1",	"nCantidadEnSolicitudesPA",	"Cantidad en Requisiciones de Programa Anual",
							"1",	"nIdPeriodo",	"Periodo",

							//Requisicion
							"2",	"cIdSolicitud",	"Requisicion",
							"2",	"cTipoSolicitud",	"Tipo de Requisicion",
							"2",	"UnidadEjecutora",	"Unidad Ejecutora Requisicion",
							"2",	"descripcionUESolicitud",	"Descripcion de Unidad Ejecutora Requisicion",
							"2",	"cDescripcionSolicitud",	"Descripcion de Requisicion",
							"2",	"cAlcance",	"Alcance Requisicion",
							"2",	"cEstadoSolicitud",	"Estado de Requisicion",
							"2",	"cIdSubPartida",	"Id SubPartida Requisicion",
							"2",	"cSubPartida",	"SubPartida Requisicion",
							"2",	"cIdCapitulo",	"Capitulo Requisicion",
							"2",	"cIdEntidadContableSolicitud",	"Entidad Contable Requisicion",
							"2",	"mMontoBrutoSolicitud",	"Monto Bruto Requisicion",
							"2",	"mMontoNetoSolicitud",	"Monto Neto Requisicion",
							"2",	"nIdLineaSolicitud",	"Linea Requisicion",
							"2",	"cDescripcionLineaSolicitud",	"Descripcion Linea Requisicion",
							"2",	"nCantidadLineaSolicitud",	"Cantidad de Linea Requisicion",
							"2",	"mPrecioUnitarioLineaSolicitud",	"Precio Unitario Linea Requisicion",
							"2",	"nPorcentajeIVALineaSolicitud",	"Porcentaje IVA Linea Requisicion",
							"2",	"cOLI",	"OLI Requisicion",
							"2",	"nIdClaveEgresos",	"Clave Egresos",
							"2",	"mClaveSolicitud",	"Clave de Requisicion",
							"2",	"mNotas",	"Notas",
								
							//Consolidado
							"3",	"cIdConsolidado",	"Consolidado",
							"3",	"cIdTipoConsolidado",	"Tipo de Consolidado",
							"3",	"cIdUnidadEjecutoraConsolidado",	"Unidad Ejecutora Consolidado",
							"3",	"DescripcionUnidadEjecutoraConsolidado",	"Descripcion de Unidad Ejecutora Consolidado",
							"3",	"desAlcanceConsolidado",	"Alcance Consolidado",
							"3",	"mConsolidadoBruto",	"Monto Bruto Consolidado",
							"3",	"mConsolidadoNeto",	"Monto Neto Consolidado",
							"3",	"nIdLineaConsolidado",	"Linea Consolidado",
							"3",	"cDescripcionLineaConsolidado",	"Descripcion Linea Consolidado",
							"3",	"cIdUnidadMedida",	"Unidad de Medida",
							"3",	"nCantidadLineaConsolidado",	"Cantidad Linea Consolidado",
							"3",	"mMontoLineaConsolidadoBruto",	"Monto Bruto Linea Consolidado",
							"3",	"mMontoLineaConsolidadoNeto",	"Monto Neto Linea Consolidado",
								
							//Procedimiento
							"4",	"cIdProcedimiento",	"Procedimiento",
							"4",	"TipoProceso",  "Tipo de Proceso",
							"4",	"cIdTipoProcedimiento",	"Tipo de Procedimiento",
							"4",	"cCategoriaProcedimiento",	"Categoria Procedimiento",
							"4",	"cDescripcionProcedimiento",	"Descripcion Procedimiento",
							"4",	"cEstadoProcedimiento",	"Estado Procedimiento",
							"4",	"cOficio",	"Oficio Procedimiento",
							"4",	"cIdRFC",	"RFC (Procedimiento)",
							"4",	"cRFC",	"Razón Social (Procedimiento)",
							"4",	"cDescripcionPartidaProcedimiento",	"Descripcion Partida Procedimiento",
							"4",	"mMontoPrecioUnitarioProcedimiento",	"Monto Precio Unitario Procedimiento",
							"4",	"nPorcentajeIVAProcedimiento",	"Porcentaje IVA Procedimiento",
							"4",	"cTipoCambioProcedimiento",	"Tipo de cambio Procedimiento",
							"4",	"cTipoMoneda",	"Tipo Moneda",
							"4",	"mMontoBrutoPartidaProcedimiento",	"Monto Bruto Partida Procedimiento",
							"4",	"mMontoNetoPartidaProcedimiento",	"Monto Neto Partida Procedimiento",
							"4",	"cIdUnidadEjecutoraProc",	"Unidad Ejecutora de Procedimiento",
							"4",	"direccionProveedor",	"Direccion",
							"4",	"telefonoProveedor",	"Telefono",
							"4",	"nFechaConvocatoria",	"Fecha de convocatoria",
							"4",	"nFechaApertura",	"Fecha de apertura de ofertas",
							"4",	"nFechaJunta",	"Fecha de junta de aclaraciones",
							"4",	"nFechaEvaluacionT",	"Fecha de evaluación técnica",
							"4",	"nFechaEvaluacionE",	"Fecha de evaluación económica",
							"4",	"nFechaFallo",	"Fecha de Fallo",
							"4",	"nFechaFormalizacion",	"Fecha de formalización",
							"4",	"nFechaEntrega",	"Fecha de entrega de bienes, o inicio de los servicios",
							"4",	"nFechaConclusion",	"Conclusión de los servicios",
							"4",	"Dif_Prog_proc",	"Diferencia de Programado y Pagado",

							//Pedidos y Contratos
							"5",	"cIdPedidoContrato",	"Pedido o Contrato",
							"5",	"cIdEntidadContablePedido",	"Entidad Contable Pedido",
							"5",	"mTipoCambioPedidoContrato",	"Tipo de Cambio de Pedido o Contrato",
							"5",	"cEstadoPedidoContrato",	"Estado Pedido o Contrato",
							"5",	"cIdPedidoContratoDefinitivo",	"Pedido o Contrato Definitivo",
							"5",	"cDescripcionPedidoContrato",	"Descripcion de Pedido o Contrato",
							"5",	"mMontoPedidoContratoBruto",	"Monto Bruto de Pedido o Contrato",
							"5",	"mPedidoContratoNeto",	"Monto  Neto de Pedido o Contrato",
							"5",	"fFallo",	"Fecha de fallo de Pedido o Contrato",
							"5",	"fFormalizacion",	"Fecha de Formalizacion de Pedido o Contrato",
							"5",	"fEntrega",	"Fecha de Entrega de Pedido o Contrato",
							"5",	"fInicio",	"Fecha de Inicio de Pedido o Contrato",
							"5",	"fFin",	"Fecha de Fin de Pedido o Contrato",
							"5",	"cIdRfcPedidoContrato",	"RFC (Pedido)",
							"5",	"cRFC",	"Razón Social (Pedido)",
							"5",	"cIdTipoPedidoCnt",	"Tipo Pedido o Contrato",
							"5",	"cIdUnidadEjecutoraPedidoCnt",	"Unidad Ejecutora de Pedido o Contrato",
							"5",	"nIdConsecutivoPedidoCnt",	"Consecutivo de Pedido o Contrato",
							"5",	"MontoMinimoPedido",	"Monto minimo Pedido/Contrato",
							"5",	"MontoMaximoPedido",	"Monto maximo Pedido/Contrato",
							"5",	"mTotalModificacionPedidoContrato",	"Monto Total Modificado Pedido/contrato",
							"5",	"cIdPedidoContratoModificado",	"cId Pedido/Contrato Modificado"						
						];

				if(quitarCamposSeleccionados == 1)
				 	arregloMod=columnasSeleccionadas(todosHistorico);
				else
					arregloMod = todosHistorico;
				return arregloMod;
		break;
		}
	}

	
	function columnasSeleccionadas(arreglo){
		var compara="";
		var num="";
		var id="";
		var igual=0; //0=diferente , 1=iguales
		var arreglonuevo=new Array();
		var aTrs = $('#tblColumnasSelec').dataTable().fnGetNodes();
		if (aTrs==0){
			return arreglo;
		}else{
			//for ( var i=0;i<=aTrs.length-1;i++ ){   
			for (var j=0;j<=arreglo.length-1;j=j+3){
			igual=0;
			num=arreglo[j];
			id=arreglo[j+1];
			compara=arreglo[j+2];
		/*	var nTr = $('#tblColumnasSelec').dataTable().fnGetData(aTrs[i]);
			var dato=nTr[0];*/
		//	for (var j=0;j<=arreglo.length-1;j=j+3){
			for ( var i=0;i<=aTrs.length-1;i++ ){ 
				var nTr = $('#tblColumnasSelec').dataTable().fnGetData(aTrs[i]);
				var dato=nTr[0];
				//alert("dato "+dato);
				//alert("id "+ id);
			  /*  num=arreglo[j];
				id=arreglo[j+1];
				compara=arreglo[j+2];*/
				if (id==dato){
					igual=1;
					break;
				}	
			//	alert(igual);
			}
			if (igual==0){
					arreglonuevo.push(num);
					arreglonuevo.push(id);
					arreglonuevo.push(compara);
			}
		}
		return arreglonuevo;
		}
		
	}
	
	/*
	* Regresa 1 si existen campos seleccionados del modulo indicado 
	* y regresa 0 en caso contrario.
	*/
	function validaCamposSeleccionados(nummodulo){
		var arregloMod=new Array();
		var tem;
		var aTrs = $('#tblColumnasSelec').dataTable().fnGetNodes();
		var nTr;
		arregloMod = arreglosModulo(nummodulo,0);
		
		for ( var i=0; i<=aTrs.length-1; i++ ){
			tem = 1;
			nTr = $('#tblColumnasSelec').dataTable().fnGetData(aTrs[i]);
			for(var l=1;  l<= arregloMod.length/3; l++){
				if(nTr[0] == arregloMod[tem])
					return 1;
				tem+=3;
			}
		}
		return 0;		
	}
	/*
	* Reemplaza los caracteres indicados en toda la cadena
	*/
	function replaceAll( text, busca, reemplaza ){
  		while (text.toString().indexOf(busca) != -1){
      		text = text.toString().replace(busca,reemplaza);
      	}
  		return text;
	}
	/*
	* Valida usuario creador de la plantida del reporte
	*/
	function validaUsuarioCreador(){
		$("#cIdUsuarioCreacion").val("");
		queryFormPost("readUserCreadorPlantilla", {async : false});
		
		if($("#cIdUsuarioCreacion").val() == $("#hdnUsuario").val())
			$("#btnEditarPlantilla").css("display","");
		else
			$("#btnEditarPlantilla").css("display","none");
	}
	
	function reloadColumnasSelec(titulo){
		// tabla de opciones seleccionadas
		$("#tblColumnasSelec").dataTable({
			 sScrollX: "100%",
			 sScrollXInner: "97%",
			 bScrollCollapse: true,
			 bDestroy: true,
			 oLanguage: {
			 //  sProcessing: "Procesando...",
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
			aaSorting: [] ,
			 aoColumns: [
			{ sName: titulo, bVisible: false},
			{ sName: "idProceso", bVisible: false},
			{ sName: "Etiqueta"}
			]
		});
	}
</script>
  </head>
  
  <body>
  	<br/>
  	<form name="enviaDatos" id="enviaDatos" method="post" action="../../servlet/CatalogosCSV"  TARGET="procesa">
  		<input type="hidden" name="rn" id="rn" value="rpt_mMultiReporte.jasper" />
  		<input type="hidden" name="encabezado" id="encabezado" value=""/>
  		<input type="hidden" name="query" id="query" value=""/>
  		<input type="hidden" name="hdnConsecutivoReporte" id="hdnConsecutivoReporte"/>
  		<input type="hidden" name="cEjercicio" id="cEjercicio"/>
  		<input type="hidden" name="hdnUE" id="hdnUE" value="<%=usuario.getU_UR() %>"/>
  		<input type="hidden" name="hdnConsecutivoPlantilla" id="hdnConsecutivoPlantilla"/>
  		<input type="hidden" name="hdnUsuario" id="hdnUsuario" value="<%= usuario.getLogin()%>"/>
  		<input type="hidden" name="hdnCampo" id="hdnCampo"/>
  		<input type="hidden" name="hdnOrden" id="hdnOrden"/>
  		<input type="hidden" name="hdnModulo" id="hdnModulo"/>
  		<input type="hidden" name="hdnDescripcion" id="hdnDescripcion"/>
  		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion"/>
  	<fieldset>
  		<legend>Nueva Plantilla</legend>
  		<br/>
  		<table>
  			<tr>
  				<td>
  					Nombre:
  				</td>
  				<td>
  					<input type="text" name="txtNombrePlantilla" id="txtNombrePlantilla" size="60"/>
  				</td>
  				<td>
  					<button name="btnGuardarPlantilla" id="btnGuardarPlantilla">Guardar</button>
  				</td>
  			</tr>
  		</table>
  		<br/>
  	</fieldset>
  	<br/>
  	<table align="center" border="0" width="100%">
  		<tr>
  			<td>
  				<table align="left">
  					<tr>
  						<td>
			   				Ejercicio
			   				<select id="HistoricoEjercicio" name="HistoricoEjercicio"  style="width: 5em;" >
			   				    <option value="2015"  selected="selected">2015</option>
			  					<option value="2014">2014</option>

			  				</select>	
			  				
			  				
			  				
  						<td>
		  					<div id="meses" style="display: block;"> 
			  					De:
			  					<select id="mes1" name="mes1"  style="width: 8em;" >
			  						<option value="00">*</option>
			  						<option value="01">Enero</option>
			  						<option value="02">Febrero</option>
			  						<option value="03">Marzo</option>
			  						<option value="04">Abril</option>
			  						<option value="05">Mayo</option>
			  						<option value="06">Junio</option>
			  						<option value="07">Julio</option>
			  						<option value="08">Agosto</option>
			  						<option value="09">Septiembre</option>
			  						<option value="10">Octubre</option>
			  						<option value="11">Noviembre</option>
			  						<option value="12">Diciembre</option>
			  					</select>
			   				</div>
		   				</td>
		   			</tr>
		   		</table>
   			</td>
   		</tr>
   		<tr>
   			<td>
   				<table>
  					<tr>
  						<td>
  							Plantillas Existentes:
  						</td>
  						<td>
  							<select name="sltPlantillasExistentes" id="sltPlantillasExistentes" onChange="validaUsuarioCreador();">
  							</select>
  							&nbsp;&nbsp;&nbsp;
  							<button id="btnCargarPlantilla">Cargar</button>
  							<!--&nbsp;-->
  							<button id="btnEditarPlantilla">Editar</button>
  						</td>
  					</tr>
  				</table>
   			</td>
   		</tr>
   	</table>
   	<br/>
  </form>
  	<form name="multilistador" id="multilistador">
		<fieldset>
  
  
  <table border="1">		
   	<tr>
	   <td  colspan="2">
	   		<input type="checkbox" name='todos' id="todos" value='0' onclick="muestra();"  />
	   		<div id="procesos" style="display: none"> 
	   			<input type="checkbox" name='adquisiciones' id="chPanual" value='1' onclick="consulta2(1,'chPanual');cargaColumnas();"/>Programa Anual
		   		<input type="checkbox" name='adquisiciones' id="chsolicitu" value='2' onclick="consulta2(2,'chsolicitu');cargaColumnas();"/>Requisiciones
		   		<input type="checkbox" name='adquisiciones'  id="chConsolidado" value='3'onclick="consulta2(3,'chConsolidado');cargaColumnas();"/>Consolidado
		   		<input type="checkbox" name='adquisiciones'  id="chProcedimiento" value='4' onclick="consulta2(4,'chProcedimiento');cargaColumnas();"/>Procedimiento
		   		<input type="checkbox" name='adquisiciones'  id="chPC" value='5' onclick="consulta2(5,'chPC');cargaColumnas();"/>Pedidos y Contrato
		   		<input type="checkbox" name='adquisiciones' id="chsaldos" value='6' onclick="consulta2(6,'chsaldos');cargaColumnas();"/><input name="inpSaldos" id="inpSaldos"  value="Saldos" style="width: 45px; border-width:0; background-color:transparent;" />
		   		<input type="checkbox" name='adquisiciones' id="chpagos" value='7' onclick="consulta2(7,'chpagos');cargaColumnas();"/><input name="inpPagos" id="inpPagos"  value="Pagos" style="width: 45px; border-width:0; background-color:transparent;" />
		   		<input type="checkbox" name='adquisiciones'  id="chProveedores" value='8' onclick="consulta2(8,'chProveedores');cargaColumnas();"/><input name="inpProveedor" id="inpProveedor"  value="Proveedores" style="width: 80px; border-width:0; background-color:transparent;" />
		   		
		   		<input type="hidden" name="datoelimina" id="datoelimina" value=""/>
		   		<input type="hidden" name="numIdProceso" id="numIdProceso" value=""/>
		   		<input type="hidden" name="numIdProcesoInsert" id="numIdProcesoInsert" value=""/>
		   		<input type="hidden" name="descripcion" id="descripcion" value=""/>
		   		
		   		<input type="hidden" name="auxIdProceso" id="auxIdProceso" value=""/>
		   		<input type="hidden" name="etiqueta" id="etiqueta" value=""/>
		   		
				<input type="hidden" name="anoActual" id="anoActual" value=""/>	
			   	<input type="hidden" name="SaldosActivo" id="SaldosActivo" value="0"/>
			   	<input type="hidden" name="PagosActivo" id="PagosActivo" value="0"/>	
	   		</div>
	   		
	   </td>
   </tr>
	<tr>
		<td width="330px">
			<table id="tblColumnas" class="display" border="2" >
	            <thead>
	            <tr>
	                <th></th><th></th><th>Posibles Variables</th>
	            </tr>
	            </thead>
	            
	            
	        </table>
	        
	        <table>
	        	<tr>
	            	<td><button id="btntblColumnas">Seleccionar Todas</button></td>
	            </tr>
	        </table>
		</td>
		<td  width="330px">
			<table id="tblColumnasSelec" class="display" border="2"  >
	            <thead><tr>
	                	<th></th><th></th><th>Variables Seleccionadas</th> 
	            </tr> </thead>
	            
	        </table>
	        <table>
	       		<tr>
	            	<td><button id="btntblBorraColumnasSelec">Borrar Todas</button></td>
	            </tr>
	        </table>
		</td>
	</tr>
</table>



<br/> 
 <button id="btnGenerarReporte">Generar</button>
<table>

<tr>
	<td>
	<div id='1'><fieldset><legend>Filtros de Programa Anual</legend>
		<table>
		<tr>
			<td><input type="checkbox" name='progAnual' value='URMPA' onclick="muestraURM();"/>Unidad de Recursos Materiales
				<div id ="URMPA" style="display: none" > <select multiple="multiple" id="URM" name="URM" style="width: 60em;"></select></div>
			 </td>
		</tr>
		<tr>
			<td>
				<input type="checkbox" name='progAnual' value='CABMSPA' onclick="muestraURM();"/>CUCOP
				<div id ="CABMSPA"  style="display: none"><input type="text" name="CABMS" id="CABMS" value="" style="width: 60em;"/></div>
			</td>
		</tr>
		<tr>
			<td>
				<input type="checkbox" name='progAnual' value='PeriodoPA' onclick="muestraURM();"/>Periodo
				<div id ="PeriodoPA"  style="display: none"><select multiple="multiple" id="periodo" name="periodo" style="width: 40em;"></select></div>
			</td>
		</tr>
		<tr>
			<td><input type="checkbox" name='progAnual' value='partidaPA' onclick="muestraURM();"/>Partida especifica<br/>
				<input type="text" value='Puede incluir varias Partidas separadas por coma, Ej 21101,29601' style="color: red; border: 0px none; width: 400px; font-weight: bold;" readonly="readonly" />
				<div id ="partidaPA"  style="display: none"><input type="text" name="partida" id="partida" value="" style="width: 60em;"/></div>
			</td>
		</tr>
	</table>
	</fieldset>
	</div>
	</td>
	</tr>
	
	<tr>
		<td><div id='2'>
			<fieldset>	
				<legend>Filtros de las Requisiciones</legend>
				<table>
				<tr>
					<td><input type="checkbox" name='requisicion' value='URMReq' onclick="muestraSolicitudes();"/>Unidad de Recursos Materiales
						<div id ="URMReq" style="display: none" ><select multiple="multiple" id="URMsol" name="URMsol" style="width: 60em;"></select></div>
			 		</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='requisicion' value='TipoSolReq' onclick="muestraSolicitudes();"/>Tipo de Requisicion 
						<div id ="TipoSolReq"  style="display: none"><select multiple="multiple" id="TipoSol" name="TipoSol" style="width: 60em;"></select></div>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='requisicion' value='EdoSolReq' onclick="muestraSolicitudes();"/>Estado de la Requisicion
						<div id ="EdoSolReq"  style="display: none">
							<select multiple="multiple" id="EdoSol" name="EdoSol" style="width: 40em;"></select> 
					    </div>
					</td>
				</tr>
				<tr>
						<td><input type="checkbox" name='requisicion' value='alcanceReq' onclick="muestraSolicitudes();"/>Alcance de la Requisicion
							<div id ="alcanceReq"  style="display: none"><select multiple="multiple" id="alcance" name="alcance" style="width: 40em;"></select></div>
						</td>
				</tr>
			   	<tr>
						<td><input type="checkbox" name='requisicion' value='CapituloReq' onclick="muestraSolicitudes();"/>Capítulo
							<div id ="CapituloReq"  style="display: none"><select multiple="multiple" id="Capitulo" name="Capitulo" style="width: 40em;"></select></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='requisicion' value='partidaReq' onclick="muestraSolicitudes();"/>Partida especifica<br/>
							<input type="text" value='Puede incluir varias Partidas separadas por coma, Ej 21101,29601' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
							<div id ="partidaReq"  style="display: none"><input type="text" name="partidaRequisicion" id="partidaRequisicion" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='requisicion' value='descripcionReq' onclick="muestraSolicitudes();"/>Descripción de la Requisicion
							<div id ="descripcionReq"  style="display: none"><input type="text" name="descripcionRequisicion" id="descripcionRequisicion" value="" style="width: 60em;"/></div>
						</td>
				 </tr> 
				 <tr>
						<td><input type="checkbox" name='requisicion' value='solicitudesReq' onclick="muestraSolicitudes();"/>Requisiciones<br/>
						<input type="text" value='Puede incluir varias Solicitudes separadas por coma, Ej RC-B02-1,RC-B02-5,RC-B02-7' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
							<div id ="solicitudesReq"  style="display: none"><input type="text" name="solicitudesRequisicion" id="solicitudesRequisicion" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='requisicion' value='codigoReq' onclick="muestraSolicitudes();"/>Código de egresos
							<div id ="codigoReq"  style="display: none"><input type="text" name="codigoRequisicion" id="codigoRequisicion" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				
			  </table>
			</fieldset>
		</div></td>
	</tr>
	<tr>
		<td><div id='3'>
			<fieldset>
				<legend>Filtros de Consolidado</legend>
				<table>
				<tr>
					<td><input type="checkbox" name='consolidado' value='URMConsol' onclick="muestraConsolidado();"/>Unidad de Recursos Materiales
						<div id ="URMConsol" style="display: none" ><select multiple="multiple" id="URMConsolidado" name="URMConsolidado" style="width: 60em;"></select></div>
			 		</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='consolidado' value='TipoConsol' onclick="muestraConsolidado();"/>Tipo de Consolidado 
						<div id ="TipoConsol"  style="display: none"><select multiple="multiple" id="TipoConsolidado" name="TipoConsolidado" style="width: 60em;"></select></div>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='consolidado' value='alcanceConsol' onclick="muestraConsolidado();"/>Alcance
						<div id ="alcanceConsol"  style="display: none">
							<select multiple="multiple" id="alcanceConsolidado" name="alcanceConsolidado" style="width: 40em;"></select> 
					    </div>
					</td>
				</tr>
				<tr>
						<td><input type="checkbox" name='consolidado' value='consolidadoConsol' onclick="muestraConsolidado();"/>Consolidados<br/>
						<input type="text" value='Puede incluir varios Consolidados separadas por coma, Ej CC-A04-1,CC-A04-2,CC-A04-3' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
						<div id ="consolidadoConsol"  style="display: none"><input type="text" name="consolidadoC" id="consolidadoC" value="" style="width: 60em;"/></div>
						</td>
				</tr>
			  </table>
			</fieldset>
		</div></td>
	</tr>
	<tr>
		<td><div id='4'>
			<fieldset>
				<legend>Filtros de Procedimientos</legend>
				<table>
				<tr>
					<td><input type="checkbox" name='procedimiento' value='URMProc' onclick="muestraProcedimientos();"/>Unidad de Recursos Materiales
						<div id ="URMProc" style="display: none" ><select multiple="multiple" id="URMProcedimiento" name="URMProcedimiento" style="width: 60em;"></select></div>
			 		</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='procedimiento' value='TipoProc' onclick="muestraProcedimientos();"/>Tipo de Procedimiento 
						<div id ="TipoProc"  style="display: none"><select multiple="multiple" id="TipoProcedimiento" name="TipoProcedimiento" style="width: 60em;"></select></div>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='procedimiento' value='EdoProc' onclick="muestraProcedimientos();"/>Estado de Procedimiento
						<div id ="EdoProc"  style="display: none"><select multiple="multiple" id="EdoProcedimiento" name="EdoProcedimiento" style="width: 40em;"></select></div>
					</td>
				</tr>
				<tr>
						<td><input type="checkbox" name='procedimiento' value='categoriaProc' onclick="muestraProcedimientos();"/>Categoria de Procedimiento
							<div id ="categoriaProc"  style="display: none"><select multiple="multiple" id="categoriaProcedimiento" name="categoriaProcedimiento" style="width: 40em;"></select></div>
						</td>
				</tr>
			   	<tr>
						<td><input type="checkbox" name='procedimiento' value='tipoMoneda' onclick="muestraProcedimientos();"/>Tipo de moneda
							<div id ="tipoMoneda"  style="display: none"><select multiple="multiple" id="tipoMonedaProc" name="tipoMonedaProc" style="width: 40em;"></select></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='procedimiento' value='descripcionProc' onclick="muestraProcedimientos();"/>Descripción del Procedimiento
							<div id ="descripcionProc"  style="display: none"><input type="text" name="descripcionProcedimiento" id="descripcionProcedimiento" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='procedimiento' value='rfcProc' onclick="muestraProcedimientos();"/>R.F.C del proveedor<br/>
						<input type="text" value='Puede incluir varios RFC separados por coma, Ej MM-020812-R22, TI-101005-1Q2' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
							<div id ="rfcProc"  style="display: none"><input type="text" name="rfcProcedimiento" id="rfcProcedimiento" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				  <tr>
						<td><input type="checkbox" name='procedimiento' value='razonSocialProc' onclick="muestraProcedimientos();"/>Razón Social 
							<div id ="razonSocialProc"  style="display: none"><input type="text" name="razonSocialProcedimiento" id="razonSocialProcedimiento" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='procedimiento' value='procedimientoProc' onclick="muestraProcedimientos();"/>Procedimientos<br/>
						<input type="text" value='Puede incluir varios Procedimientos separados por coma, Ej PN-B02-2,PN-B02-3' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
							<div id ="procedimientoProc"  style="display: none"><input type="text" name="procedimientoP" id="procedimientoP" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 </table>
			</fieldset>
			<fieldset><legend>Filtro Programado VS. Ejercido</legend>
		
				<table>
				<tr><td>Solicitudes VS Procedimiento</td></tr>
				<tr>
					
					<td><input type="checkbox" name='ch_Ganancia' id="ch_Ganancia"/>Ahorros
						
					<input type="checkbox" name='ch_Ganancia' id="ch_Perdida" />Sobregasto
						
					<input type="checkbox" name='ch_Ganancia' id="ch_igual" />Sin cambio
						
					 </td>
				</tr>
			
			</table>
			</fieldset>
		</div></td>
	</tr>
	<tr>
		<td><div id='5'>
			<fieldset>
				<legend>Filtros de Pedidos y Contratos</legend>
				<table>
				<tr>
					<td><input type="checkbox" name='pedido' value='URMPed' onclick="muestraPedidos();"/>Unidad de Recursos Materiales
						<div id ="URMPed" style="display: none" ><select multiple="multiple" id="URMPedido" name="URMPedido" style="width: 60em;"></select></div>
			 		</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='pedido' value='TipoPed' onclick="muestraPedidos();"/>Pedido o Contrato 
						<div id ="TipoPed"  style="display: none">
						<select multiple="multiple" id="TipoPedido" name="TipoPedido" style="width: 60em;">
							<option value='PE'>Pedido</option>
	  						<option value='CV'>Contrato</option>
						</select>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name='pedido' value='EdoPed' onclick="muestraPedidos();"/>Estado del Pedido o Contrato
						<div id ="EdoPed"  style="display: none"><select multiple="multiple" id="EdoPedido" name="EdoPedido" style="width: 40em;"></select></div>
					</td>
				</tr>
				<tr>
						<td><input type="checkbox" name='pedido' value='descripcionPed' onclick="muestraPedidos();"/>Descripcion del Pedido o Contrato
							<div id ="descripcionPed"  style="display: none"><input type="text" name="descripcionPedido" id="descripcionPedido" value="" style="width: 60em;"/></div>
						</td>
				</tr>
			   	<tr>
						<td><input type="checkbox" name='pedido' value='numConsecutivoPed' onclick="muestraPedidos();"/>Numero Consecutivo del Pedido o Contrato
							<div id ="numConsecutivoPed"  style="display: none"><input type="text" name="numConsecutivoPedido" id="numConsecutivoPedido" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name='pedido' value='definitivoPed' onclick="muestraPedidos();"/>Definitivo del Pedido o Contrato<br/>
						<input type="text" value='Puede incluir varios Pedidos separados por coma, Ej PE-A04-1/2012,PE-A04-2/2012' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
						<div id ="definitivoPed"  style="display: none"><input type="text" name="definitivoPedido" id="definitivoPedido" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				  <tr>
						<td><input type="checkbox" name='pedido' value='rfcPed' onclick="muestraPedidos();"/>R.F.C del proveedor<br/>
						<input type="text" value='Puede incluir varios RFC separados por coma, Ej MM-020812-R22, TI-101005-1Q2' style="color: red; border: 0px none; width: 520px; font-weight: bold;" readonly="readonly" />
						<div id ="rfcPed"  style="display: none"><input type="text" name="rfcPedido" id="rfcPedido" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				  <tr>
						<td><input type="checkbox" name='pedido' value='razonSocialPed' onclick="muestraPedidos();"/>Razón Social  
							<div id ="razonSocialPed"  style="display: none"><input type="text" name="razonSocialPedido" id="razonSocialPedido" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				
				 </table>
			</fieldset>
		</div></td>
	</tr>
	<tr>
		<td><div id='6'>
			<fieldset>
				<legend>Filtros de Saldos</legend>
				<table>
				<tr>
					<td><input type="checkbox" name='saldos' value='URMSal' onclick="muestraSaldos();"/>Unidad de Recursos Materiales
						<div id ="URMSal" style="display: none" ><select multiple="multiple" id="URMSaldo" name=URMSaldo style="width: 60em;"></select></div>
			 		</td>
				</tr>
				<tr>
					<td><input type="checkbox" name='saldos' value='partidaSal' onclick="muestraSaldos();"/>Partida especifica
						<div id ="partidaSal"  style="display: none"><input type="text" name="partidaSaldo" id="partidaSaldo" value="" style="width: 60em;"/></div>
					</td>
				 </tr>
				 </table>
			</fieldset>
		</div></td>
	</tr>
	<tr>
		<td><div id='7'>
			<fieldset>
				<legend>Filtros de Pagos</legend>
				<table>
				<tr>
					<td><input type="checkbox" name='pagos' value='URMPag' onclick="muestraPagos();"/>Unidad de Recursos Materiales
						<div id ="URMPag" style="display: none" ><select multiple="multiple" id="URMPagos" name=URMPagos style="width: 60em;"></select></div>
			 		</td>
				</tr>
				<tr>
						<td><input type="checkbox" name='pagos' value='partidaPag' onclick="muestraPagos();"/>Partida especifica
							<div id ="partidaPag"  style="display: none"><input type="text" name="partidaPagos" id="partidaPagos" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				<tr>
						<td><input type="checkbox" name='pagos' value='documentoPag' onclick="muestraPagos();"/>Ducumento especifico
							<div id ="documentoPag"  style="display: none"><input type="text" name="documentoPagos" id="documentoPag" value="" style="width: 60em;"/></div>
						</td>
				 </tr>
				 </table>
			</fieldset>
		</div></td>
		</tr>
	<tr>
		<td><div id='8'>
			<fieldset>
				<legend>Filtro de Proveedores</legend>
				<table>
				<tr>
					<td><input type="checkbox" name="proveedores" value="rfc" onclick="muestraProveedores();"/>RFC<br/>
						<input type="text" value='Puede incluir varios RFC separados por coma, Ej AAAA-831201-XX1,AAA-831201-XX2,AAA-831201-XX3' style="color: red; border: 0px none; width: 620px; font-weight: bold;" readonly="readonly" />
						<div id ="rfc" style="display: none" ><input type="text" id="rfcText" name="rfcText" style="width: 60em;"/></div>
						
			 		</td>
				</tr>
				<tr>
						<td><input type="checkbox" name="proveedores" value="razonSocial" onclick="muestraProveedores();"/>Raz&oacute;n Social
							<div id ="razonSocial" style="display: none"><input type="text" id="razonSocialText" name="razonSocialText" style="width: 60em;"/></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name="proveedores" value="entidadFederativa" onclick="muestraProveedores();"/>Entidad Federativa
							<div id ="entidadFederativa" style="display: none"><select multiple="multiple" id="entidadFederativaSelect" name="entidadFederativaSelect" style="width: 60em;"></select></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name="proveedores" value="pyme" onclick="muestraProveedores();"/>Pyme
							<div id ="pyme" style="display: none"><select multiple="multiple" id="pymeSelect" name="pymeSelect" style="width: 60em;"></select></div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name="proveedores" value="tipoContribuyente" onclick="muestraProveedores();"/>Tipo de Contribuyente
							<div id ="tipoContribuyente" style="display: none">
								<select multiple="multiple" id="tipoContribuyenteSelect" name="tipoContribuyenteSelect" style="width: 60em;">
									<option value="1">Persona F&iacute;sica</option>
									<option value="2">Persona Moral</option>
								</select>
							</div>
						</td>
				 </tr>
				 <tr>
						<td><input type="checkbox" name="proveedores" value="habilitado" onclick="muestraProveedores();"/>Habilitado
							<div id ="habilitado" style="display: none">
								<select multiple="multiple" id="habilitadoSelect" name="habilitadoSelect" style="width: 60em;">
									<option value="1">SI</option>
									<option value="0">NO</option>
								</select>
							</div>
						</td>
				 </tr>
				 <tr>
					<td><input type="checkbox" name="proveedores" value="giro" onclick="muestraProveedores();"/>Giro
						<div id ="giro" style="display: none" ><input type="text" id="giroText" name="giroText" style="width: 60em;"/></div>
			 		</td>
				</tr>
				 </table>
			</fieldset>
		</div></td>
	</tr>			
	<!-- <tr>
	<td>
		
		<div id='0'><fieldset><legend>Filtro Programado VS. Ejercido</legend>
			
			<table>
			<tr><td>Solicitudes VS Procedimiento</td></tr>
			<tr>
				
				<td><input type="checkbox" name='ch_Ganancia' id="ch_Ganancia"/>Ahorros
					
				<input type="checkbox" name='ch_Ganancia' id="ch_Perdida" />Sobregasto
					
				<input type="checkbox" name='ch_Ganancia' id="ch_igual" />Sin cambio
					
				 </td>
			</tr>
		
		</table>
		</fieldset>
		</div>
	</td>
	</tr>   -->
	</table>
	</fieldset>
</form>
</body>
</html>
