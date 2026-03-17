<%@page import="com.syc.gestion.core.Role"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.admin.servlet.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	String role="";
	String roles="";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
	   return;
	}
	String name_user=usuario.getLogin();	
	Map rol =usuario.getRoles();


	Calendar c1 = Calendar.getInstance(); // today
	int mesActual=c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String mesAct="";
	if(mesActual<10){
		mesAct="0"+mesActual;
	}else{
		mesAct=""+mesActual;
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html> 
	<head> 
		<title>Programa Anual</title> 
 
		<meta http-equiv="pragma" content="no-cache" > 
		<meta http-equiv="cache-control" content="no-cache" > 
		<meta http-equiv="expires" content="0" > 
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" > 
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" > 
		<meta http-equiv="description" content="Cat?logo de Beneficiarios" > 
 
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" /> 
 		<!--<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />-->
		<style type="text/css" title="currentStyle"> 
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css"; 
	 		@import "../css/demo_table_jui.css"; 
			@import "../css/demo_page.css"; 
			@import "../css/demo_table.css"; 
		</style> 
 
 
		<!-- Estilos de dialogo -->
		<style> 
			estilos del dialogo 
						div#dialog-form fieldset { 
				padding: 0; 
				border: 0; 
				margin-top: 25px; 
			} 
 			div#users-contain { 
				width: 350px; 
				margin: 20px 0; 
			} 
			div#users-contain table { 
				margin: 1em 0; 
				border-collapse: collapse; 
				width: 100%; 
			} 
			div#users-contain table td,div#users-contain table th { 
				border: 1px solid #eee; 
				padding: .6em 10px; 
				text-align: left; 
			} 
			.ui-dialog .ui-state-error { 
				padding: .3em; 
			} 
			.validateTips { 
				border: 1px solid transparent; 
				padding: 0.3em; 
			} 
		</style> 
 		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
  		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
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
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
		<script type="text/javascript" src="../../js/utils/syctools.js"></script>
 
		<script type="text/javascript" charset="utf-8">
	        var totalPrevio = null;
			var largo;
			$(document).ready(function() {
				<%
					int tabla=0;
					NegativaPestana NegPestana=new NegativaPestana();
					NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
					//botones
					//NegativaBoton NegBoton= new NegativaBoton();
					NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						role =(String)r.getKey();
						roles+= r.getKey().toString()+",";
					}
					Map pestanas=ebl.getPestana(role,"Programa anual");
					Iterator it = pestanas.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry e = (Map.Entry)it.next();%>
						$( "#<%=e.getValue()%>" ).attr("disabled", true);<%
						
						String pestana=(String)e.getValue();
						if ("calendarioPrograma".equals(pestana)){
							 tabla=1;
						}	
					}
					Map botonescalendario=nb.getBotones(role,"Programa anual","calendarioPrograma");
					Iterator btncalendario = botonescalendario.entrySet().iterator();
					while (btncalendario.hasNext()) {
						Map.Entry b = (Map.Entry)btncalendario.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%			
					}
				
					Map botonesconsulta=nb.getBotones(role,"Programa anual","consultaPrograma");
					Iterator btnconsulta = botonesconsulta.entrySet().iterator();
					while (btnconsulta.hasNext()) {
						Map.Entry bconsulta = (Map.Entry)btnconsulta.next();%>
						$("#<%=bconsulta.getValue()%>").attr("disabled", true);<%			
					}
				
					Map botonesreporte=nb.getBotones(role,"Programa anual","reportePrograma");
					Iterator btnreporte = botonesreporte.entrySet().iterator();
					while (btnreporte.hasNext()) {
						Map.Entry breporte = (Map.Entry)btnreporte.next();%>
						$("#<%=breporte.getValue()%>").attr("disabled", true);<%			
					}		
				%>
				var count=true;
	            iva = $("#ivaEdita"),
				estimado_compras = $("#porcentajeEdita"),
				pyme = $("#pymeEdita"),
				plurianualidad = $("#plurianualidad"),
				plurianualidadv = $("#plurianualidadv"),
				precargaPAAS = $("#precargaPAAS"),
				allFields = $( [] ).add( iva ).add( estimado_compras ).add( pyme ).add( plurianualidad ).add( precargaPAAS )
				tips = $( ".validateTips" );
				queryFormPost("toleranciaPAPartidaRead",{async : false});
				queryFormPost("techoPresupuestalActivado",{async : false});
				querySelectPost("mCatalogoTipoIVA", "iva_pa", {async : false});
				querySelectPost("mPeriodoRead2", "mesesCat", {async : false});
				//Mes actual
				$("#mesesCat").val("<%=mesActual%>");
				//inicializacion de las pesta?as
				$("#tabsId").tabs();
				$("#calendarioPrograma").attr("disabled",true)
				//$("#aTab2").attr("disabled",true)
				//inicializacion de las tablas
				oTable1= $("#tblSeleccionaCucop").dataTable({
				    bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "100%",
					//sScrollXInner: "200%",
				  	bJQueryUI: true,
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
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}			
				});			
		       	//tabla cucop agregado
				oTable2= $("#tblCucops").dataTable({
				   	bAutoWidth : true,
				   	bRedraw:false,
					bPaginate:true,
					bDestroy:true,
					bRetrive : true,
					bServerSide:false,
					sScrollY: "200px",
					sScrollX: "200px",
					iDisplayLength: 10,
				    sPaginationType: "full_numbers",
				    bLengthChange: false,
					//sScrollXInner: "200%",				
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
					//bServerSide: true,
					//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCucopsGrid&qw="+ qw,
					bProcessing: true,
				    sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 11, "asc" ]] ,
					aoColumns: [
						{ sName: "cIdCABM1" ,sType: "string" },
						{ sName: "cIdSubPartida1",sType: "string"  },					
						{ sName: "cCABM1",sType: "string" 	},
						{ sName: "cUnidadMedida1" },
						{ sName: "nCantidad1" },
						{ sName: "mPrecioUnitario1"},
						{ sName: "nPorcentajeIVA1"},
						{ sName: "mImporteBruto1" },														
						{ sName: "mImporteNeto1" },
						{ sName: "boton1" },
						{ sName: "identificador", bVisible: false },
						{ sName: "cIdCABM", bVisible: false }
					]
				});
        	
				// tabla de cucop que genera el calendario.
				oTable3=  $("#tblcucopPeriodo").dataTable({
					bAutoWidth : true,
					bPaginate:false,
					bFilter : false,
					sScrollY: "100%",
					sScrollX: "104%",
					bDestroy:true,
					bRetrive:true,
					bLengthChange: false,
					//sScrollXInner: "120%",				
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
						{ sName: "cperiodo" },
						{ sName: "cantDisp"   },
						{ sName: "ncantidad"   },					
						{ sName: "mpreciounitario"	},
						{ sName: "mimportebruto" },
						{ sName: "mimporteneto" },
						{ sName: "ncantidadensolicitudes" },
						{ sName: "ncantidaddisponibilidad" },
						{ sName: "mMontoNetoEnSolicitudes" },
						{ sName: "mmontodisponibilidad" },													
						{ sName: "mMantener" }	
					]
	        	});
				//lo primero es checar el rol del usurio
		    	$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		     	queryFormPost("checaRolUsuario",{async:false});
		    	// checar si ya existe un programa anual cargado a la unidad ejecutora.
		    	//querySelectPost("UnidadBusca", "desUnidadResponsable1", {async: false });
		    	var roles="<%=roles%>";
			    if( roles.indexOf("ADMIN_RECMAT") >=0){
			    	$('#isAdmin').val(0);
			    }
			    querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "desUnidadResponsable1", {async: false });
			    querySelectPost("UnidadBusca2", "desUnidadResponsable2", {async: false });
				if($("#desUnidadResponsable1").val() != 0 ){
					queryFormPost("existeUnidadEjecutora",{async:false});
				}else{
					$("#tieneUe").val(0);
				}
				if($("#tieneUe").val()==-1){
					alert("La unidad ejecutora no tiene un PAAS asignado favor de crearlo..");
					$("#pbGuardaUE").css("visibility","visible"); 
					$("#pbPrecarga").css("visibility","visible");
					$("#chk_precarga").css("visibility","visible"); 
		           // $("#precargaPAAS").css("visibility","visible");
		            $("#porcentajeInflacion").css("visibility","visible");
				}else{
					//actualiza programa anual ya que pudo haber ingresado otro usuario.
					$("#usuarioLogin").val('<%=usuario.getLogin()%>');
					queryFormPost("actualizaAltadePA", {async: false });
					$("#formBuscar").css("visibility","visible");
					//agregar unidad ejecutora al campo de texto
					$("#cUnidadResponsable").val('<%=usuario.getU_UR().trim()%>');
					//se inserta en el campo de texto ue_usuario la unidad ejecutora que corresponde al uuario.
					queryFormPost("UnidadRead", {async: false });
				}
		      	//es necesario leer la tabla de sistema para jalar ejercicio.
		      	queryFormPost("ejercicioFiscalPA",{async:false});
				//se inicializan los cruds.
			    querySelectPost("mCatalogoCapituloRead1", "mCatalogoCapitulo", {async : false});
				querySelectPost("mCatalogoSubPartidaRead", "mCatalogoSubPartida", {async : false});
				querySelectPost("cambiaCombomCatalogoSubPartidaRead", "mCatalogoSubPartida", {async : false});
				querySelectPost("BuscaProceso", "procesoPa", {async : false});
				if($("#desUnidadResponsable1").val() == 0){
			     	queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
			    }else{
					queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
				}
				//inicializa combox de alta de cucops.
				querySelectPost("mCapituloCucop", "mCapituloCucop", {async : false});
				querySelectPost("BuscaProcesoCucop", "procesoCucop", {async : false});
				querySelectPost("mCatalogoSubPartidaCucop", "mSubPartidaCucop", {async : false});
				querySelectPost("mCatalogoProcedenciaEdita", "tipoProcedimientoEdita", {async : false});
				querySelectPost("mCatalogoTipoAdj", "tipoAdjudicacion", {async : false});
				querySelectPost("mCatalogoTipoIVA", "ivaEdita", {async : false});
				//para darle formato a los montos	
				$("#mMontoC2").formatCurrency();
				$("#mMontoC3").formatCurrency();
				$("#mMontoC5").formatCurrency();
				$("#mMontoTotal").formatCurrency();		
				queryFormPost("fn_mProgramaAnualMontoPorPartidaRead",{async : false});
				queryFormPost("fn_mDataStoreTechoPresupuestalPartidaRead",{async : false});
				if ($("#mTechoPresupuestal").val() == null || $("#mTechoPresupuestal").val() == '') {
					$("#mTechoPresupuestal").val(0);
				}
				if ($("#mMontoPartida").val() >= $("#mTechoPresupuestal").val()){
					$("#mMontoPartida").attr("style", "color: red; border: 0px;");
					$("#mTechoPresupuestal").attr("style", "color: red; border: 0px;");
					$("#mAvisoTecho").val("Se ha alcanzado el presupuesto.");
				}
				$("#mMontoPartida").formatCurrency();
				$("#mTechoPresupuestal").formatCurrency();		
				//se ocultan botones de calendario y borrar cucop
			    $("#pbBorrarCucop").css("visibility","hidden");
			    $("#pbEditarCucop").css("visibility","hidden");
				//eventos de botones
				$("#pbBuscacucop").button().click(function(){
		        var qw;
		        var tmp;
		        //if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
		        qw=" cEjercicio='"+$("#cEjercicio").val()+"'"; 
		        if($("#desUnidadResponsable1").val() != 0){
		        	qw += " and cIdUnidadEjecutora ='"+$("#desUnidadResponsable1").val()+"'";
				}
		        if($("#mCatalogoCapitulo").val() != 0){
		        	qw += " and cIdCapitulo='"+$("#mCatalogoCapitulo").val()+"'";
		        }
		        if($("#mCatalogoSubPartida").val() != 0){
		         	qw+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
		        }
		        if($("#procesoPa").val() != 0){
		        	qw+=" and nidtipoproceso='"+$("#procesoPa").val()+"'";
		       	}
		        tmp=qw;
		        if($("#desCucopPa").val()!=""){
		        	qw+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
		       	}
				if($("#cucopPa").val()!=""){
		        	qw += " and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
				}
		        //se actualiza la tabla de cucops
		        actualizatablaCucops(qw);
				//tambien se tiene que cargar la tabla de cucopseleccionado y mandar parametros
			    qw1=" 1=1 "; 
				if($("#mCatalogoCapitulo").val() != 0){
					qw1+=" and cIdCapitulo='"+$("#mCatalogoCapitulo").val()+"'"; 
				}
				if($("#procesoPa").val() != 0){
					qw1+=" and nidtipoproceso='"+$("#procesoPa").val()+"'";
				}
				if($("#desCucopPa").val()!=""){
					qw1+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
					tmp+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
				}
				if($("#cucopPa").val()!=""){
			    	qw1 += " and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
			        tmp+=" and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
				}
				if($("#mCatalogoSubPartida").val() != 0){
					qw1+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
			        tmp+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
				}
			    //para asegurar que no serepitan los cabms que ya estan en el programa anual
			    qw1+= "and cidcabm not in(select  cidcabm  from vCucopsGrid where "+tmp+")";
			    //tabla de cucop seleccionado
			    // $('#tblSeleccionaCucop').dataTable().fnClearTable(); 
				$('#tblSeleccionaCucop').dataTable(
				{
					bAutoWidth : true,
					bDestroy:true,
					bRetrive:true,
					sScrollY: "200%",
				    sScrollX: "200%",
				    bLengthChange: false,
					//bScrollCollapse: true,         
				   	//sScrollXInner: "100%",
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
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_agregacucops&qw="+qw1,
					bProcessing: true,
					aaSorting: [[ 0, "asc" ]] ,
					aoColumns: [
						{ sName: "cIdCABM" },
						{ sName: "cCABM"},
						{ sName: "cIdSubPartida" },	
						{ sName: "csubpartida" },	
						{ sName: "cUnidadMedida" },
						{ sName: "ctipoproceso" }
					]
				});
			});
			$("#pbBorrarCucop").button().click(function(){
				$("#usuarioUE").val('<%=usuario.getU_UR()%>');
				var aTrs = oTable2.dataTable().fnGetNodes();
				for ( var i=aTrs.length ; i>0; i-- )     
				{       
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{  
						aData = oTable2.fnGetData(aTrs[i]);
						$("#borraCIdCABM").val($("#cIdCABM_"+aData[10]+"").val());
						$("#borraIdSubPartida").val($("#cIdSubPartida_"+aData[10]+"").val());
					    if (!window.confirm("Esta seguro de Borrar el cucop '"+$("#borraCIdCABM").val()+"' del Programa Anual?")){
					    	return;
					    }
						else{
							for ( var k=1 ; k<=12; k++ ) {
								$("#periodoSolicitudPA").val(k);
								//checa si existen lineas de solicitud
								queryFormPost("checaLineasSolicitudProgramaAnual",{async:false});
								if($("#cantidadLineasPA").val()>=1){
									alert("No se puede eliminar el CUCOP porque esta asociado a una o mas solicitudes");
									return;
						      	}
							}
							//query para borrar cucops detalle
							queryFormPost("borraCucopsPAdetalle",{async:false});
							//borra detalle periodo
						    queryFormPost("borraCucopsPAdetallePeriodo",{async:false});
						    //alert("Se borro correctamente el registro");
							//se recargan valores de montos por capitulos
				            if($("#desUnidadResponsable1").val() == 0){
				            	queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
				            }else{
				            	queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
				            }
				            //se pone formato tipo moneda
				            $("#mMontoC2").formatCurrency();
						    $("#mMontoC3").formatCurrency();
						     $("#mMontoC5").formatCurrency();
						    $("#mMontoTotal").formatCurrency();	
						    $("#pbBuscacucop").click();
							return;
						}
					}        
				}
				alert("No ha seleccionado un cucop de la lista");
			});
		   	$("#pbivaTodos").button().click(function(){
		    	if(parseFloat( $("#iva_pa").val()) > 100 ){
                	alert("El porcentaje de IVA no puede ser mayor a 100");
                	return;
                }
		    	$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		    	//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
		     	if($("#iva_pa").val()!=""){
		     		if (!window.confirm("Desea Asignar el IVA a todos los CUCOPS del Programa Anual?"))
						return;
		 			//se actualiza iva a todos los cucops del programa anual.
		     		queryFormPost("actualizaIvaCucopsProgramaAnual",{async:false});
					// actualizar tabla de cucops
					if($("#desUnidadResponsable1").val() == 0){
						queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
					}else{
						queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
					}
					//se pone formato tipo moneda
					$("#mMontoC2").formatCurrency();
					$("#mMontoC3").formatCurrency();
					$("#mMontoC5").formatCurrency();
					$("#mMontoTotal").formatCurrency();	
					$("#pbBuscacucop").click();
		     		//actualiza montos y cantidades.
			        var qw;
			        qw=" cEjercicio='"+$("#cEjercicio").val()+"' and cIdUnidadEjecutora ='"+ $("#desUnidadResponsable1").val() +"' and cIdCapitulo='"+$("#mCatalogoCapitulo").val()+"'"; 
			        qw+=" and nidtipoproceso='"+$("#procesoPa").val()+"'";
			        if($("#desCucopPa").val()!=""){
			        	qw+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
			       	}
		          	if($("#cucopPa").val()!=""){
		          		qw += " and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
		       		}
			        if($("#mCatalogoSubPartida").val()!=0){
						qw+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
			        }
		       		actualizatablaCucops(qw);
				}else{
		        	return;
				}
			});
		   	//esto siempre lo hace para los renglones de las tablas
			$('#tblSeleccionaCucop tr').live('dblclick', function() {
				$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		    	//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
				if ( $(this).hasClass('row_selected') ){           
					$(this).removeClass('row_selected');
				}else{
	               	$(this).addClass('row_selected'); 
                    /// se inserta cucop seleccionado
					var aTrs = $('#tblSeleccionaCucop').dataTable().fnGetNodes();
		     		var countSelect=0;
					for ( var i=aTrs.length ; i>=0; i-- )     
					{       
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{  
							aData = oTable1.fnGetData(aTrs[i]);
							countSelect=1;
							try{
							    $("#agregaCIdCABM").val(aData[0]);
							    $("#agregaCIdSubPartida").val(aData[2]);
							    $("#agreganPorcentajeIVA").val('16');
							    $("#agregacIdProcedencia").val('');
							    $("#agregamPorcentajePyme").val('0');
							    $("#agregamPorcentajeNoTratados").val('0');
							    $("#agregaplurianualidad").val('0');
							    $("#agregaplurianualidadv").val('0');
							    $("#agregatipoAdjudicacion").val('LP');
							    $("#cIdPartida").val(aData[2]);
							    queryFormPost("hayEPS",{async:false});
								if($("#hayEP").val()==0){
									alert("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.");
								}else{
									queryFormPost("hayDisponibleEnEPS",{async:false});
									if($("#montoAnualTotalEPS").val()==0){
										alert("No hay monto disponible en tus EPS.\nSolicitar adecuación presupuestal.");
									}
								}
							    //procedimiento almacenado para agregar cucops al programa anual
							    queryFormPost("spAgregaCucop", {async: false });
							   	$('#tblSeleccionaCucop').dataTable().fnDeleteRow( i );
								$("#pbBuscacucop").click();
								//se habilitan botones porque ya existe porlo menos un registro en la tabla
							    $("#pbBorrarCucop").css("visibility","visible");
				                $("#pbEditarCucop").css("visibility","visible");
				      					
							}catch(e){
								alert("No se pudo agregar cucop...");
							}
						}				
					} 
				}      
			});
			/////////CHECK BOX PARA LA PLURIANUALIDAD////////////////
      		$( "#chk_plurianualidad" ).change(function() {
				if ($('#chk_plurianualidad').is(':checked')) {
					$("#plurianualidad").css("visibility","visible");
					$("#plurianualidadv").css("visibility","visible");
					$("#EP").css("visibility","visible");
					$("#ME").css("visibility","visible");
					$("#anos").css("visibility","visible");
					///$("#legendplurianualidad").css("visibility","visible");
					//obtiene los valores de plurianualidad 
					queryFormPost("obtienePluri", {async: false });
	   	 			//alert($("#valorplurianualidad").val());
	      			$("#plurianualidad").val($("#valorplurianualidad").val());
					$("#plurianualidadv").val($("#valorplurianualidadv").val());
					$('#chk_plurianualidad').val(1);
			    } else {
			        $("#plurianualidad").css("visibility","hidden");
			        $("#plurianualidadv").css("visibility","hidden");
					$("#EP").css("visibility","hidden");
			        $("#ME").css("visibility","hidden");
					$("#anos").css("visibility","hidden");
			        $('#chk_plurianualidad').val(0);
			    }
			});
		 	$("#autoAjustePAAS").button().click(function(){
				$("#usuarioUE").val('<%=usuario.getU_UR()%>');
			 	if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
			 		if (!window.confirm("Esta seguro que desea autoajustar los montos y cantidades programadas con lo solicitado. ? Esta accion no podra revertirse"))
						return;
					var res="";
					res=new Array();
					//revisar los checkbox que estan habilitados				 			
					var aTrs = $('#tblcucopPeriodo').dataTable().fnGetNodes();
					var token = "";
					for ( var i=0 ; i<aTrs.length; i++ ) {
						if ($("#mantener_"+i+"").is(':checked')){
							res += token + (i+1) ;
							token=",";
						}
					}  
					
			  		if(res== ""){
			   			res="ajustaTodoCucop";
			  		}
			  		
					
					$("#ajustePAAS").val(res);
					queryFormPost("pa_autoAjustePAAS", { async : false});		
					alert("Se realizo el Ajuste Correctamente ");
					actualizaPeriodoSuficiencia();
				 
				 	//Guarda en la Bitácora
					$("#cAccion").val("AUTO_AJUSTEPAAS_PARCIAL");
					$("#cIdDocumento").val($("#cucopEdita").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
					
					
				}else{
					alert("Solo un administrador o un usuario de la Unidad Ejecutora pueden realizar el Auto Ajuste");
					return;
			   	}
		});
		$("#autoAjustePAASUE").button().click(function(){
			$("#usuarioUE").val('<%=usuario.getU_UR()%>');
			if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
				if (!window.confirm("Esta seguro que desea autoajustar los montos y cantidades programadas con lo solicitado. ? Esta accion ajustara todo el PAAS de la Unidad Ejecutora y no podra revertirse"))
					return;
			   	var res="";   
			    res="ajustaTodoUE";
			    $("#cucopEdita").val('');
			    $("#ajustePAAS").val(res);
				queryFormPost("pa_autoAjustePAAS", 
				{ 
					async : false, 
					callback : function() 
					{	
						alert("El Ajuste del PAAS de toda la Unidad Ejecutora se realizo Correctamente ");
						$("#pbBuscacucop").button().click();	
					} 
				});		
				//Guarda en la Bitácora
				$("#cAccion").val("AUTO_AJUSTEPAAS_TOTAL");
				$("#cIdDocumento").val($("#desUnidadResponsable1").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
			}else{
				alert("Solo un administrador o un usuario de la Unidad Ejecutora pueden realizar el Auto Ajuste");
				return;
			}
		});
              

		$("#pbGuardaEdita").button().click(function(){
     		var totalVirtual = 0;
     		var aTrsV = $('#tblcucopPeriodo').dataTable().fnGetNodes();
           	var puPromedio="0.00";
           	var color="";
			for(var i=0; i < aTrsV.length; i++){
          		var nTr = $('#tblcucopPeriodo').dataTable().fnGetData(i);
          		if($("#ncantidad_" + i + "").val()==''){
          			$("#ncantidad_" + i + "").val(0);
          		}
				var cantidad = parseInt($("#ncantidad_" + i + "").val(),10);
				//var precio = $("#mpreciounitario_" + i + "").asNumber({ parseType: 'Float'});
				var precio = parseFloat(quitaFmt($("#mpreciounitario_" + i + "").val()));
				var resultadoPrecio = Math.round(precio*Math.pow(10,2))/Math.pow(10,2);
				
				if(parseInt(cantidad,10)>0 && parseFloat(resultadoPrecio)<=parseFloat(0)){
					alert("El precio no puede ser 0.00 en el mes "+(i+1));
					return;
				}
				var totalLinea =parseFloat(cantidad * resultadoPrecio);
				totalVirtual = totalVirtual + totalLinea;
           	}
		   	$.ajax({
				async: false,
				cache: false,
				type: 'GET',
				url: "../../servlet/ObtienePrecioUnitarioPAA",
				data: "Param="+$("#editaCIdCABM").val()+","+$("#desUnidadResponsable1").val()+","+$("#cEjercicio").val(),
				success: function(resp){
					if(resp.indexOf("Error:") >= 0)
  	  					puPromedio = "0.00";
  	  				else
  	  					puPromedio = resp;
				}
			});
			totalVirtual = parseFloat(totalVirtual * (1 + ($("#ivaEdita").val() / 100)));
			// var difVirtual = totalVirtual - $("#totalEdita").asNumber({ parseType: 'Float'});
			var difVirtual = totalVirtual -  parseFloat(quitaFmt($("#totalEdita").val()));
			if (difVirtual < 0.01 && difVirtual >= 0) {
            	difVirtual = 0;
			}
			//queryFormPost("fn_mProgramaAnualMontoPorPartidaRead",{async : false});
			queryFormPost("fn_mDataStoreTechoPresupuestalPartidaRead",{async : false});
			//queryFormPost("toleranciaPAPartidaRead",{async : false});
			if ($("#mTechoPresupuestal").val() == null || $("#mTechoPresupuestal").val() == '') {
				$("#mTechoPresupuestal").val(0);
			}
			var totalPartidaV = parseFloat(totalVirtual);
			var techoConTolerancia = (parseFloat($("#mTechoPresupuestal").val()) * parseFloat((1 + ($("#cTolerancia").val() / 100))));
			// VALIDA SI ESTA ACTIVADO EL TECHO PRESUPUESTAL
			//queryFormPost("techoPresupuestalActivado",{async : false});
			if($("#techoActivado").val()=='TRUE'){
				if (totalPartidaV > techoConTolerancia){
                	alert("No es posible realizar las modificaciones porque sobrepasan el presupuesto de la partida");
                	return;
                }
			}
	        var bValid = true;
			tips.text("");
			//para obtener el importe bruto de un cucop seleccionado
	      	queryFormPost("obtienMontoBrutoCucop", {async: false });
			allFields.removeClass( "ui-state-error" );
			bValid = bValid && checkRequerido( iva, "Iva" );
			bValid = bValid && checkRequerido( estimado_compras, "Pyme" );
			bValid = bValid && checkRequerido( pyme, "Estimado de compras" );
			if ($('#chk_plurianualidad').is(':checked')) {
				bValid = bValid && checkRequerido( plurianualidad, "Plurianualidad" );
				bValid = bValid && checkRequerido( plurianualidadv, "Plurianualidadv" );
			} 
			if(!bValid){
				return;
			}
		     //se valida si es administrador o usuario de la unidad ejecutora seleccionada
			$("#usuarioUE").val('<%=usuario.getU_UR()%>');
			if(parseInt( $("#pymeEdita").val(),10) > ( parseInt($("#mImporteBruto").val(),10)) ){
            	alert("El pyme debe ser menor igual que el monto bruto ");
                return;
			}
            if(parseInt( $("#porcentajeEdita").val(),10) > ( parseInt($("#mImporteBruto").val(),10)) ){
            	alert("El valor estimado debe ser menor o igual al monto bruto");
                return;
			}
            if(parseFloat( $("#ivaEdita").val()) > 100 ){
            	alert("El porcentaje de IVA no puede ser mayor a 100");
				return;
			}
     		var aTrs = $('#tblcucopPeriodo').dataTable().fnGetNodes();
            for(var i=0;i<aTrs.length;i++){
				if(parseInt($("#ncantidad_"+i+"").val(),10) < parseInt($("#ncantidadensolicitudes_"+i+"").val(),10)){
			    	alert("La cantidad no puede ser menor a " + $("#ncantidadensolicitudes_"+i+"").val() + ", en el periodo "+ (i+1));
		            //actualizaPeriodoSuficiencia();
		            return;
				}
				if(parseInt($("#ncantidadensolicitudes_"+i+"").val(),10)!=0){
		        	var precio=$("#mMontoNetoEnSolicitudes_"+i+"").asNumber({ parseType: 'Float' })/(1 + (parseFloat($("#ivaEdita").val())* 0.01))/$("#ncantidadensolicitudes_"+i+"").asNumber({ parseType: 'int' });
					precio=precio.toFixed(2);
		            var unitario= $("#mpreciounitario_"+i+"").asNumber({ parseType: 'Float'});
		            unitario=unitario.toFixed(2);
					if(parseFloat(unitario) <  parseFloat(precio)) {
			        	alert("El precio unitario no puede ser menor a " + precio + ", en el periodo "+ (i+1));
			            //actualizaPeriodoSuficiencia();
		                return;
					}
				}
			}
			if(parseFloat( $("#ivaEdita").val()) > 100 ){
            	alert("El porcentaje de IVA no puede ser mayor a 100");
                return;
			}
			for(var i=0;i<aTrs.length;i++){
				if(parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) != parseFloat(puPromedio) 
			  		&& parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) > 0  
			  		&& parseInt($("#ncantidad_"+i+"").val(),10) == 0 ){
			  		alert("Debe introducir una cantidad en el mes "+(i+1)+".");
			  		return;
				}
			  	if(parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) == 0  
			  		&& parseInt($("#ncantidad_"+i+"").val(),10) > 0 ){
			  		alert("Debe introducir el precio unitario en el mes "+(i+1)+".");
			  		return;
				}
			}
			for(var i=0;i<aTrs.length;i++){
				$("#editanCantidad").val(parseInt($("#ncantidad_"+i+"").val(),10));
			    $("#editanIdPeriodo").val(i+1);
			    $("#editacIdUsuarioModifica").val('<%=usuario.getLogin()%>');
				if((parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) == parseFloat(puPromedio) 
			    	|| parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) == 0)  
			    	&& parseInt($("#ncantidad_"+i+"").val(),10) == 0){
			    		$("#mpreciounitario_"+i+"").val("0.00");
				}
			    $("#editamPrecioUnitario").val($("#mpreciounitario_"+i+"").val());			      	
			    queryFormPost("spGuardaCucopEditado", {async: false });
			}
		    //Se checa si se habilito el checkbox de plurianualidad
		    if ($('#chk_plurianualidad').is(':checked')) {
		    	if($("#plurianualidad").val()>0 ){
					if( parseInt($("#plurianualidadv").val(),10)<(parseInt($("#mImporteBruto").val(),10)) ){
				    	$("#valorplurianualidad").val($("#plurianualidad").val());
					    $("#valorplurianualidadv").val($("#plurianualidadv").val());
					    $("#valortipoAdjudicacion").val($("#tipoAdjudicacion").val());
					    //PARA MODIFICAR IVA Y PORCENTAJE DE PYMES
				      	queryFormPost("spAgregaCucopEdita", {async: false });
					    alert("Datos Guardados");
					}else{
		      				alert("El campo Monto a Ejercer debe ser  menor  que su monto total bruto ");
					}
				}else{
					alert("El campo Ejercicio de plurianualidad debe ser mayor a cero ");
				}
			}else{
		     	$("#valorplurianualidadv").val('0');
				$("#valorplurianualidad").val('0');
		      	$("#valortipoAdjudicacion").val($("#tipoAdjudicacion").val());
				queryFormPost("spAgregaCucopEdita", {async: false });
				alert("Datos Guardados");
			}
		    var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#desUnidadResponsable1").val() + "', '" + $("#editaCIdCABM").val() + "'";
          	$('#tblcucopPeriodo').dataTable().fnClearTable(); 
			szTabla = "CUCOPSPERIODOGRID";
			$.getJSON("../../catalogos/SelectJson.jsp?"+new Date().getTime(),{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
			function(j){   
				arrayCompleto=new Array();  
				for (var i = 0; i < j.length; i++) 
    			{
    				if(parseFloat(j[i].Col2) > 0){
			    		color="#000000";
			    	}else{
			    		j[i].Col2 = puPromedio;
			    		color="#B4B4B4";
					}
					arrayCompleto [i]=[
						"<input type='text' id='cperiodo' name='cperiodo' value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
						"<input type='text' id='cantDisp' name='cantDisp' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='ncantidad_"+i+"' name='ncantidad' value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
						"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");' onKeyPress='return(onlyNumbers2(event));' />",
						"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
	                    "<input type='text' id='mmontodisponibilidad' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
	                    "<input type='checkbox' id='mantener_"+i+"' class='editacucop' name='mantener_"+i+"'  value='" + j[i].Col10 + "'/>"
					];
				}	
    			$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);
				$('.editacucop').formatCurrency();	
			});   
			queryFormPost("llenaDetalleCucopEdita",{async:false});
			queryFormPost("totalCucopEdita",{async:false});
			$("#totalEdita").formatCurrency();  
			if($("#desUnidadResponsable1").val() == 0){
		    	queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
		    }else{
		    	queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
			}
			//se pone formato tipo moneda
			$("#mMontoC2").formatCurrency();
			$("#mMontoC3").formatCurrency();
			$("#mMontoC5").formatCurrency();
			$("#mMontoTotal").formatCurrency();	
			//GUARDA EN LA BITACORA ACCION
			//Guarda en la Bitácora
			$("#cAccion").val("GUARDA_CALENDARIOPAAS");
			$("#cIdDocumento").val($("#cucopEdita").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
			$("#pbBuscacucop").click(); 
		});
		$("#pbCopiarEdita").button().click(function(){
			var aTrs = $('#tblcucopPeriodo').dataTable().fnGetNodes();
			var can= $("#ncantidad_0").val();
			var pu=  $("#mpreciounitario_0").val();
			for ( var i=1 ; i<aTrs.length; i++ ) {
				$("#ncantidad_"+i+"").val(can);
				$("#mpreciounitario_"+i+"").val(pu);
			}    
			$('.editacucop').formatCurrency();
			//GUARDA EN LA BITACORA ACCION
			//Guarda en la Bitácora
			$("#cAccion").val("COPIA_CALENDARIOPAAS");
			$("#cIdDocumento").val($("#cucopEdita").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
		});
		$("#tblCucops tr").live("dblclick", function() {
			var tabla='<%=tabla%>';
			var meses = 0;
			var puPromedio="0.00";
			if (tabla==0){
				//obtiene los valores de plurianualidad
				if ($(this).hasClass('row_selected')){
					$(this).removeClass('row_selected'); 
				}else{
               		$(this).addClass('row_selected'); 
					var aTrs =oTable2.dataTable().fnGetNodes();
		  			for ( var i=aTrs.length ; i>=0; i-- )  
					{       
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							//Obtiene el CABM seleccionado
						  	$(this).removeClass('row_selected'); 
						  	aData = oTable2.fnGetData(aTrs[i]);
                          	$("#editaCIdCABM").val($("#cIdCABM_"+aData[10]+"").val());
						  	$("#editaCIdSubPartida").val($("#cIdSubPartida_"+aData[10]+"").val());
					   	  	queryFormPost("llenaDetalleCucopEdita",{async:false});
					   	  	queryFormPost("totalCucopEdita",{async:false});
					   	  	$("#totalEdita").formatCurrency();
					   	  	queryFormPost("existenciasAlmacen",{async:false});
					   	  	$("#cIdPartida").val($("#cIdSubPartida_"+aData[10]+"").val());
					   	  	queryFormPost("hayEPS",{async:false});
							if($("#hayEP").val()==0){
								alert("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.");
							}else{
								queryFormPost("hayDisponibleEnEPS",{async:false});
								if($("#montoAnualTotalEPS").val()==0){
									alert("No hay monto disponible en tus EPS.\nSolicitar adecuación presupuestal.");
								}
							}
					   	  	$.ajax({
					   	  		async: false,
					   	  		cache: false,
					   	  		type: 'GET',
					   	  		url: "../../servlet/ObtienePrecioUnitarioPAA",
					   	  		data: "Param="+$("#editaCIdCABM").val()+","+$("#desUnidadResponsable1").val()+","+$("#cEjercicio").val(),
					   	  		success: function(resp){
					   	  			if(resp.indexOf("Error:") >= 0)
					   	  				puPromedio = "0.00";
					   	  			else
					   	  				puPromedio = resp;
								}
							});
				  			if($("#cantidad_").val()==''){
								$("#cantidad").val('0');
				  				$("#descrip").val($("#cCABM_"+aData[2]+"").val());
				  				$("#CVE_UNI").val($("#cUnidadMedida_"+aData[3]+"").val() );//
				  				//alert("Elese"+$("#cantidad").val());
				  			}
				  			else{
				  				$("#cantidad").val($("#cantidad_").val());
				  				$("#descrip").val($("#descrip_").val());
				  				$("#CVE_UNI").val($("#CVE_UNI_").val() );//
				  				//alert("Elese"+$("#cantidad").val());
							}
							var oTable3 ;
					 	 	//obtiene los valores de plurianualidad 
			  				queryFormPost("obtienePluri", {async: false });
		   	 				//alert($("#valorplurianualidad").val());
		   	 				//Para mostrar los datos que ya tiene guardados
		      				$("#plurianualidad").val($("#valorplurianualidad").val());
							$("#plurianualidadv").val($("#valorplurianualidadv").val());
							$("#tipoAdjudicacion").val($("#valortipoAdjudicacion").val());
							//alert($("#plurianualidad").val())
			 	    		if(parseInt($("#plurianualidad").val(),10)>0){
					 	    	document.getElementById("chk_plurianualidad").checked=true
						 	    $("#plurianualidad").css("visibility","visible");
								$("#plurianualidadv").css("visibility","visible");
						 	   	$("#EP").css("visibility","visible");
								$("#ME").css("visibility","visible");
								$("#anos").css("visibility","visible");
					 	    }
					        //var query="  cEjercicio='"+$("#cEjercicio").val()+"' AND cIdUnidadEjecutora ='"+ $("#desUnidadResponsable1").val()+"' AND cIdCABM  ='"+ $("#editaCIdCABM").val()+"' AND cIdSubPartida  ='"+$("#editaCIdSubPartida").val()+"' ORDER BY nIdPeriodo";
					        var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#desUnidadResponsable1").val() + "', '" + $("#editaCIdCABM").val() + "'";
					        var color="";
							$('#tblcucopPeriodo').dataTable().fnClearTable(); 
							szTabla = "CUCOPSPERIODOGRID";                                                                                         
							$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
							function(j)
							{
								arrayCompleto=new Array();
								for (var i = 0; i < j.length; i++) 
			    				{
			    					if(parseFloat(j[i].Col2) > 0){
			    						color="#000000";
									}else{
			    						j[i].Col2 = puPromedio;
			    						color="#B4B4B4";
									}
				    				arrayCompleto [i]=[
										"<input type='text' id='cperiodo' name='cperiodo' value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
										"<input type='text' id='cantDisp' name='cantDisp' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='ncantidad_"+i+"' name='ncantidad' value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
										"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");' onKeyPress='return(onlyNumbers2(event));' />",
										"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
					                    "<input type='text' id='mmontodisponibilidad' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
					                    "<input type='checkbox' id='mantener_"+i+"' class='editacucop' name='mantener_"+i+"'  value='" + j[i].Col10 + "'/>"
									];
								}
								$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);		    					   
								$('.editacucop').formatCurrency();
							});   
							$("#calendarioPrograma").click();
						}					              
					}			
				}
			}
		});
		$("#pbAgregaCucops").button().click(function(){
			var aTrs =$('#tblCucopSeleccionado').dataTable().fnGetNodes();
			var countSelect=0;
		 	if(aTrs.length <= 0 ) {
		  		alert("Debe de seleccionar algun cucop.") ;
		   		return;	
		 	}
		    for ( var i=aTrs.length ; i>=0; i-- )     
			{       
				if ( $(aTrs[i]).hasClass('row_selected') )         
				{  
					aData = oTablec.fnGetData(aTrs[i]);
					countSelect=1;
					try{
						$("#agregaCIdCABM").val(aData[0]);
					    alert("volr " + aData[0]);
					    $("#agregaCIdSubPartida").val(aData[2]);
					   	$("#agreganPorcentajeIVA").val('16');
					    $("#agregacIdProcedencia").val('');
					    $("#agregamPorcentajePyme").val('0');
					    $("#agregamPorcentajeNoTratados").val('0');
					    $("#agregaplurianualidad").val('0');
					    $("#agregaplurianualidadv").val('0');
					    $("#agregatipoAdjudicacion").val('LP');
					 	$('#tblCucopSeleccionado').dataTable().fnDeleteRow( i );
					 	queryFormPost("spAgregaCucop", {async: false });
					 	alert("Registro insertado correctamente")						
					}catch(e){
						alert("No se pudo agregar cucop...");
					}					    
				} 	
			}	
		});
		//Se llama a servlet para guardar unidad ejecutora nuevo.
	$("#pbGuardaUE").button().click(function(){
		//se valida si es administrador o usuario de la unidad ejecutora seleccionada
		$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
		var elParametro=""+$("#cEjercicio").val()+","+$("#desUnidadResponsable1").val()+",<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>,<%=usuario.getLogin()%>";
        $.getJSON("../../servlet/ProgramaAnualServlet?operacion=0",{Tabla: "", Param: elParametro, MaxReg: "", ajax: 'false'}, 
        function(j){
        	for(var i = 0; i < j.length; i++){
            	var col=j[i].Col1
			}
			if(j[0].Col1=="1"){
		    	alert("Ya existe un registro con los datos que desea ingresar revisar porfavor.");
			}else{
				alert("Se agrego correctamente el PAAS");
		        $("#cucops-contain1").css("visibility","visible");
				$("#cucops-contain").css("visibility","visible");
                //se recargan valores de montos por capitulos
				if($("#desUnidadResponsable1").val() == 0)
					queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
				else
					queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
	            //se pone formato tipo moneda
	            $("#mMontoC2").formatCurrency();
			    $("#mMontoC3").formatCurrency();
			    $("#mMontoC5").formatCurrency();
			    $("#mMontoTotal").formatCurrency();		
				//se pone visible formulario de consulta
		        $("#formBuscar").css("visibility","visible");
		        $("#tieneUe").val(1)
		        $("#pbGuardaUE").css("visibility","hidden"); 
			    $("#pbPrecarga").css("visibility","hidden");
			    $("#chk_precarga").css("visibility","hidden"); 
			    $("#precargaPAAS").css("visibility","hidden");
			    $("#porcentajeInflacion").css("visibility","hidden");
			    $("#cUnidadResponsable").val('<%=usuario.getU_UR()%>');
			    //se inserta en el campo de texto ue_usuario la unidad ejecutora que corresponde al uuario.
			    queryFormPost("UnidadRead", {async: false });
			}
		});
	});
	//se inicializa el boton de precarga del programa anual
	$("#pbPrecarga").button().click(function(){
		$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
    	var bValid = true;
		tips.text("");
		allFields.removeClass( "ui-state-error" );
		if ($('#chk_precarga').is(':checked')) {
			bValid = bValid && checkRequerido( precargaPAAS, "Porcentaje de Inflacion" );
			if(!bValid){
				return;
			}
			if(parseFloat( $("#precargaPAAS").val()) > 100 ){
				alert("El porcentaje de Inflacion no puede ser mayor a 100");
				return;
			}
            if(parseFloat( $("#precargaPAAS").val())< 0 ){
            	alert("El porcentaje de Inflacion no puede ser menor a 0");
                return;
			}
			if(parseFloat( $("#precargaPAAS").val())== 0 ){
            	alert("El porcentaje de Inflacion debe de ser mayor a 0");
				return;
			}
			//si pasa todas las validaciones se agrega porcentaje de inflacion y bandera true
            $("#incremento").val($("#precargaPAAS").val());
			$("#flag").val('true');
		}else{
			//se pone bandera en false para que no aplique ningun porcentaje de iva en procedimiento almacenado
			$("#incremento").val('0');
			$("#flag").val('false');
		}
		//se agregan datos a parametros que requiere el procedimiento almacenado
		$("#loginPrecarga").val('<%=usuario.getLogin()%>')
		$("#unidadEjecutoraPrecarga").val('<%=usuario.getU_UR()%>')
		$("#centroContablePrecarga").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>')
		//parametros que se envian al servlet
		var param=" "+$("#cEjercicio").val()+","+$("#unidadEjecutoraPrecarga").val()+","+$("#centroContablePrecarga").val()+","+$("#loginPrecarga").val()+","+$("#incremento").val()
		+","+$("#flag").val()+"";
		//Llamamos al servlet
		$.getJSON("../../servlet/ProgramaAnualServlet?operacion=1",{Tabla: "", Param: param, MaxReg: "", ajax: 'false'}, 
		function(j){
			for(var i = 0; i < j.length; i++){
		    	var col=j[i].Col1;
		        	switch(col){
					case "0": 
						alert("La precarga del Programa anual se realizo exitosamente");
						//deshabilita checkbox y textbox de precarga
						$("#cucops-contain1").css("visibility","visible");
			            $("#cucops-contain").css("visibility","visible");
					    //se recargan valores de montos por capitulos
					    if($("#desUnidadResponsable1").val() == 0)
							queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
						else
							queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
						//se pone formato tipo moneda
					    $("#mMontoC2").formatCurrency();
						$("#mMontoC3").formatCurrency();
							    $("#mMontoC5").formatCurrency();
							    $("#mMontoTotal").formatCurrency();		
											            
					            //se pone visible formulario de consulta
					             $("#formBuscar").css("visibility","visible");
					             $("#tieneUe").val(1)
					             $("#pbGuardaUE").css("visibility","hidden"); 
						         $("#pbPrecarga").css("visibility","hidden");
						         $("#chk_precarga").css("visibility","hidden"); 
			                     $("#precargaPAAS").css("visibility","hidden");
			                     $("#porcentajeInflacion").css("visibility","hidden");
			                     
						         $("#cUnidadResponsable").val('<%=usuario.getU_UR()%>');
						        //se inserta en el campo de texto ue_usuario la unidad ejecutora que corresponde al uuario.
						         queryFormPost("UnidadRead", {async: false });
							
						break;
						case "1":  
							alert("Error al precargar el PAAS");
						break;
					}
				}
		});
	});
	$( "#chk_precarga" ).change(function() {
		if ($('#chk_precarga').is(':checked')) {
			$("#precargaPAAS").css("visibility","visible");
			$('#chk_precarga').val(1);
		}else{
			$("#precargaPAAS").css("visibility","hidden");
			$('#chk_precarga').val(0);
		}
	});
});///FIN DOCUMENT READY

	// funcion que cambia dinamicamente el valor del combo de p?rtida en buscar cam
	function cambiaComboPartida(){
		querySelectPost("cambiaCombomCatalogoSubPartidaRead", "mCatalogoSubPartida", {async : false});
	}
	function cambiaComboPartidaCucop(){
		querySelectPost("cambiaCombomCatalogoSubPartidaReadCucop", "mSubPartidaCucop", {async : false});
	}
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true; 
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
	function onlyNumbers2(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true 
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
	function actualizaMontoPartida(){
		queryFormPost("fn_mProgramaAnualMontoPorPartidaRead",{async : false});
		queryFormPost("fn_mDataStoreTechoPresupuestalPartidaRead",{async : false});
		if ($("#mTechoPresupuestal").val() == null || $("#mTechoPresupuestal").val() == '') {
			$("#mTechoPresupuestal").val(0);
		} 
		if (parseFloat($("#mMontoPartida").val()) >= parseFloat($("#mTechoPresupuestal").val()) && parseFloat($("#mTechoPresupuestal").val()) !=0){
			$("#mMontoPartida").attr("style", "color: red; border: 0px;");
			$("#mTechoPresupuestal").attr("style", "color: red; border: 0px;");
			$("#mAvisoTecho").val("Se ha alcanzado el presupuesto.");
		}
		else {
			$("#mMontoPartida").attr("style", "border: 0px;");
			$("#mTechoPresupuestal").attr("style", "border: 0px;");
			$("#mAvisoTecho").val("");
		}
		$("#mMontoPartida").formatCurrency();
		$("#mTechoPresupuestal").formatCurrency();	
	}
	function actualizaMontosCapitulo(){
		$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		var roles="<%=role%>";
		if( roles.indexOf("ADMIN_RECMAT") >=0){
			$('#isAdmin').val(0);
		}
		$("#tieneUe").val(-1);
		if($("#desUnidadResponsable1").val() != 0 )
			queryFormPost("existeUnidadEjecutora",{async:false});
		else
			$("#tieneUe").val(0);	
		if($("#tieneUe").val() == -1){
			alert("La unidad ejecutora no tiene un PAAS asignado favor de crearlo..");
			$("#cucops-contain1").css("visibility","hidden");
			$("#cucops-contain").css("visibility","hidden");
			$("#pbGuardaUE").css("visibility","visible"); 
			$("#pbPrecarga").css("visibility","visible");
			$("#chk_precarga").css("visibility","visible"); 
			$("#porcentajeInflacion").css("visibility","visible");
			$("#formBuscar").css("visibility","hidden");
			$("#pbBorrarCucop").css("visibility","hidden");
			$("#pbEditarCucop").css("visibility","hidden");
			return;			
		}else{
			$("#pbPrecarga").css("visibility","hidden");
			$("#pbGuardaUE").css("visibility","hidden"); 
			$("#chk_precarga").css("visibility","hidden"); 
			$("#precargaPAAS").css("visibility","hidden");
			$("#porcentajeInflacion").css("visibility","hidden");
			$("#formBuscar").css("visibility","visible");
			$("#cucops-contain1").css("visibility","visible");
			$("#cucops-contain").css("visibility","visible");
			$("#pbBorrarCucop").css("visibility","hidden");
			$("#pbEditarCucop").css("visibility","hidden");
			if($("#desUnidadResponsable1").val() == 0)
		     	queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
		     else
		     	queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
			$("#mMontoC2").formatCurrency();
			$("#mMontoC3").formatCurrency();
			$("#mMontoC5").formatCurrency();
			$("#mMontoTotal").formatCurrency();	
			 count=true;
			$("#pbBuscacucop").click();
			actualizaMontoPartida();
		}
	}
	function actualizaMontos(){
		$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		$("#tieneUe").val(-1);
		if($("#desUnidadResponsable1").val() != 0 )
			queryFormPost("existeUnidadEjecutora",{async:false});
		else
			$("#tieneUe").val(0);
		if($("#tieneUe").val()==-1){
			alert("La unidad ejecutora no tiene un PAAS asignado favor de crearlo..");
			$("#cucops-contain1").css("visibility","hidden");
			$("#cucops-contain").css("visibility","hidden");
			$("#pbGuardaUE").css("visibility","visible"); 
			$("#pbPrecarga").css("visibility","visible");
			$("#chk_precarga").css("visibility","visible"); 
			$("#porcentajeInflacion").css("visibility","visible");
			$("#formBuscar").css("visibility","hidden");
			$("#pbBorrarCucop").css("visibility","hidden");
			$("#pbEditarCucop").css("visibility","hidden");
			return;
		}
		else{
			$("#pbPrecarga").css("visibility","hidden");
			$("#pbGuardaUE").css("visibility","hidden"); 
			$("#chk_precarga").css("visibility","hidden"); 
			$("#precargaPAAS").css("visibility","hidden");
			$("#porcentajeInflacion").css("visibility","hidden");
			$("#formBuscar").css("visibility","visible");
			$("#cucops-contain1").css("visibility","visible");
			$("#cucops-contain").css("visibility","visible");
			$("#pbBorrarCucop").css("visibility","hidden");
			$("#pbEditarCucop").css("visibility","hidden");
			if($("#desUnidadResponsable1").val() == 0)
		     	queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
		     else
		     	queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
			$("#mMontoC2").formatCurrency();
			$("#mMontoC3").formatCurrency();
			$("#mMontoC5").formatCurrency();
			$("#mMontoTotal").formatCurrency();	
 			count=true;
			actualizaMontoPartida();
		}       
	}	                          
	function actualizatablaCucops(query){
		//se borra la tabla antes de realizar la consulta
		$('#tblCucops').dataTable().fnClearTable();
		//tabla cucop agregado
		oTable2= $("#tblCucops").dataTable({
			bAutoWidth : true,
			bRedraw:false,
			bPaginate:true,
			bDestroy:true,
			bRetrive : true,
			sScrollY: "200px",
			sScrollX: "200px",
			iDisplayLength: 10,
			sPaginationType: "full_numbers",
			bLengthChange: false,
			//sScrollXInner: "200%",				
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
			bServerSide: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCucopsGridBD&qw="+ query,
			bProcessing: true,
		    sPaginationType: "full_numbers",
			bJQueryUI: true,
			aaSorting: [[11, "asc" ]] ,
			aoColumns: [
				{ sName: "cIdCAMB1" ,sType: "string" },
				{ sName: "cIdSubPartida1",sType: "string"  },					
				{ sName: "cCABM1",sType: "string" 	},
				{ sName: "cUnidadMedida1" },
				{ sName: "nCantidad1" },
				{ sName: "mPrecioUnitario1"},
				{ sName: "nPorcentajeIVA1"},
				{ sName: "mImporteBruto1" },														
				{ sName: "mImporteNeto1" },
			    { sName: "boton1"},
				{ sName: "identificador", bVisible: false },
				{ sName: "cIdCABM", bVisible: false }
			],
			fnInitComplete: function(oSettings, json) {
				$(".hola1").formatCurrency();
	  			$(".hola2").formatCurrency();
	            $(".hola3").formatCurrency();
	            var aTrs = $('#tblCucops').dataTable().fnGetNodes(); 
	          	if(aTrs.length >0 ){
			  		$("#pbEditarCucop").css("visibility","visible");
			  		$("#pbBorrarCucop").css("visibility","visible");
				}
			} 
		});
	}	
	function checkRequerido( o, n) {
		var sTemp = $.trim(o.val());
		o.val(sTemp);
		if ( sTemp.length == 0  ) {
			o.addClass( "ui-state-error" );
			updateTipsDlg(  n + " es un dato requerido." );
			o.focus();
			return false;
		}else {
			return true;
		}
	}
	function updateTipsDlg( t ) {
		tips.text( t );
		alert(t);	
	}
	function actualizaPeriodoSuficiencia(){
		var puPromedio="0.00";
		var color="";
		queryFormPost("llenaDetalleCucopEdita",{async:false});
		queryFormPost("totalCucopEdita",{async:false});
		$("#totalEdita").formatCurrency();
		var oTable3 ;
		var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#desUnidadResponsable1").val() + "', '" + $("#editaCIdCABM").val() + "'";
		$.ajax({
			async: false,
			cache: false,
			type: 'GET',
			url: "../../servlet/ObtienePrecioUnitarioPAA",
			data: "Param="+$("#editaCIdCABM").val()+","+$("#desUnidadResponsable1").val()+","+$("#cEjercicio").val(),
			success: function(resp){
				if(resp.indexOf("Error:") >= 0)
					puPromedio = "0.00";
				else
				puPromedio = resp;
			}
		});
		$('#tblcucopPeriodo').dataTable().fnClearTable(); 
		szTabla = "CUCOPSPERIODOGRID";                                                                                         
		$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
		function(j){   
			arrayCompleto=new Array();  
			for (var i = 0; i < j.length; i++) 
			{
				if(parseFloat(j[i].Col2) > 0){
					color="#000000";
				}
				else{
					j[i].Col2 = puPromedio;
					color="#B4B4B4";
				}
				arrayCompleto [i]=[ 
					"<input type='text' id='cperiodo' name='cperiodo' value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
					"<input type='text' id='cantDisp' name='cantDisp' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
					"<input type='text' id='ncantidad_"+i+"' name='ncantidad' value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
					"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");' onKeyPress='return(onlyNumbers2(event));' />",
					"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
					"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
					"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
					"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
					"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
                    "<input type='text' id='mmontodisponibilidad' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
                    "<input type='checkbox' id='mantener_"+i+"' class='editacucop' name='mantener_"+i+"'  value='" + j[i].Col10 + "'/>"
				]; 
			}	
			$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);
			$('.editacucop').formatCurrency();
		});   
	}
	function checkShortcut(){
		if(event.keyCode==27){
			return false;
		}
		if((event.srcElement.tagName.toUpperCase() != 'INPUT'|| document.getElementById(event.srcElement.id).style.readonly ) 
			&& (event.keyCode==8 || event.keyCode==13)){ 
			return false;
		}
	}
	function borraDatos(indice){
		var br=$("#mpreciounitario_"+indice+"").val();
		br=br.replace("$","");
		br=br.replace(",","");
		$("#mpreciounitario_"+indice+"").val(br);
	}

	function eliminaCucop(indice){
		$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
		var aTrs = oTable2.dataTable().fnGetNodes();
		for(var i=0;i<aTrs.length;i++){
			aData = oTable2.fnGetData(aTrs[i]);
			$("#borraCIdCABM").val($("#cIdCABM_"+indice+"").val());
			$("#borraIdSubPartida").val($("#cIdSubPartida_"+indice+"").val());
			if (!window.confirm("Esta seguro de Borrar el cucop '"+$("#borraCIdCABM").val()+"' del Programa Anual?"))
				return;
			for ( var k=1 ; k<=12; k++ ) {
				$("#periodoSolicitudPA").val(k);
     			//checa si existen lineas de solicitud
     			queryFormPost("checaLineasSolicitudProgramaAnual",{async:false});
     			if($("#cantidadLineasPA").val()>=1){
     				alert("No se puede eliminar el CUCOP porque esta asociado a una o mas solicitudes");
     				return;
     			}
     		}
			//query para borrar cucops detalle
			queryFormPost("borraCucopsPAdetalle",{async:false});
			//borra detalle periodo
			queryFormPost("borraCucopsPAdetallePeriodo",{async:false});
			//alert("Se borro correctamente el registro");
		    //se recargan valores de montos por capitulos
		    if($("#desUnidadResponsable1").val() == 0)
				queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
			else
				queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
			//se pone formato tipo moneda
		    $("#mMontoC2").formatCurrency();
			$("#mMontoC3").formatCurrency();
			$("#mMontoC5").formatCurrency();
			$("#mMontoTotal").formatCurrency();	
			$("#pbBuscacucop").click();
			return;
		}		
	}
	function openCSV(){
		var where;
			if($("#desUnidadResponsable2").val()!='0')
				where="and _Unidad_RM ='"+$("#desUnidadResponsable2").val()+"'";
			else
				where='';
		window.open(
			"../../servlet/CatalogosCSV?"
			+ "rn="+$('[name="REPORTE"]:checked').val()
			+ "&cIdUnidadEjecutora=" + where,
			 'Procesando', 'status=1, width=500px, height=100px, left=150px');
	}
	function openARCH(ext){
		//Guarda en la Bitácora
		$("#cAccion").val("IMPRIME_REPORTESPAAS");
		$("#cIdDocumento").val($("#cucopEdita").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
		var where;
		if($("#desUnidadResponsable2").val()!='0' && ext!="rpt_CompraNET.jasper")
			where=" and _Unidad_RM ='"+$("#desUnidadResponsable2").val()+"'";
		else
			where='';
		$("#rn").val($('[name="REPORTE"]:checked').val());
		$("#formato").val(ext);
		$("#cIdUnidadEjecutora").val(where);
		//Guarda en la Bitácora
		$("#cAccion").val("IMPRIME_REPORTESPAAS");
		$("#cIdDocumento").val($("#cucopEdita").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
		var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
			+"catalogo=REPORTE"
			+"&accion=run"
			+"&rn="+$('[name="REPORTE"]:checked').val()
			+"&formato="+ext
			+"&cIdUnidadEjecutora=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
	}		
	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}	
	function exitpop(){
		alert("A terminado");
	}
</script>	      
</head>
<body bgcolor="red" id="dt_example" bottommargin="0" leftmargin="0" topmargin="0" onkeydown="return checkShortcut();" > 
	<form> 
		<input type="hidden" id="tieneUe" name="tieneUe" size="10" value="-1" /> 
    	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" size="10" /> 
   		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" size="10" /> 
    	<input type="hidden" id="usuarioLogin" name="usuarioLogin" size="10" /> 
     	<input type="hidden" id="usuarioUE" name="usuarioUE" size="10" /> 
    	<input type="hidden" id="usuarioRole" name="usuarioRole" size="10" /> 
    	<input type="hidden" id="agregaCIdCABM" name="agregaCIdCABM" size="10" /> 
		<input type="hidden" id="agregaCIdSubPartida" name="agregaCIdSubPartida" size="10" /> 
		<input type="hidden" id="agreganPorcentajeIVA" name="agreganPorcentajeIVA" size="10" /> 
		<input type="hidden" id="agregacIdProcedencia" name="agregacIdProcedencia" size="10" /> 
		<input type="hidden" id="agregamPorcentajePyme" name="agregamPorcentajePyme" size="10" /> 
		<input type="hidden" id="agregamPorcentajeNoTratados" name="agregamPorcentajeNoTratados" size="10" /> 
		<input type="hidden" id="editaCIdCABM" name="editaCIdCABM" size="10" /> 
		<input type="hidden" id="editaCIdSubPartida" name="editaCIdSubPartida" size="10" /> 
		<input type="hidden" id="editanIdPeriodo" name="editanIdPeriodo" size="10" /> 
		<input type="hidden" id="editanCantidad" name="editanCantidad" size="10" /> 
		<input type="hidden" id="editamPrecioUnitario" name="editamPrecioUnitario" size="10" /> 
		<input type="hidden" id="editacIdUsuarioModifica" name="editacIdUsuarioModifica" size="10" /> 
		<input type="hidden" id="borraCIdCABM" name="borraCIdCABM" size="10" /> 
		<input type="hidden" id="borraIdSubPartida" name="borraIdSubPartida" size="10" /> 
		<input type="hidden" id="periodoSolicitudPA" name="periodoSolicitudPA" size="10" /> 
		<input type="hidden" id="cantidadLineasPA" name="cantidadLineasPA" size="10" /> 
		<input type="hidden" id="cBeneficiario" name="cBeneficiario" size="10" > 
		<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP" size="10" > 
		<input type="hidden" id="fBeneficiario" name="fBeneficiario" size="10" > 
		<input type="hidden" id="cUsuario" name="cUsuario" size="10" > 
		<input type="hidden" id="cUsuarioModifico" name="cUsuarioModifico" size="10" > 
		<input type="hidden" id="fCuentaModifico" name="fCuentaModifico" size="10" > 
		<input type="hidden" id="cBeneficiarioStatus" name="cBeneficiarioStatus" size="10" > 
		<input type="hidden" id="cUsuarioModifico" name="cUsuarioModifico" size="10" > 
		<input type="hidden" id="fCuentaModifico" name="fCuentaModifico" size="10" > 
		<input type="hidden" id="cEjercicio" name="cEjercicio" size="10" > 
		<input type="hidden" id="incremento" name="incremento" size="10" > 
		<input type="hidden" id="flag" name="flag" size="10" > 
		<input type="hidden" id="loginPrecarga" name="loginPrecarga" size="10" > 
		<input type="hidden" id="unidadEjecutoraPrecarga" name="unidadEjecutoraPrecarga" size="10" > 
		<input type="hidden" id="centroContablePrecarga" name="centroContablePrecarga" size="10" >
		<input type="hidden" id="techoActivado" name="techoActivado" size="10" >
		<input type="hidden" id="cAccion" name="cAccion" size="10" >
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" size="10" >
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<!-- Para la plurianualidad -->
		<input type="hidden" id="agregaplurianualidad" name="agregaplurianualidad" size="10" >
		<input type="hidden" id="agregaplurianualidadv" name="agregaplurianualidadv" size="10" > 
		<input type="hidden" id="agregatipoAdjudicacion" name="agregatipoAdjudicacion" size="10" >
		<input type="hidden" id="valorplurianualidad" name="valorplurianualidad" size="10" value="">
		<input type="hidden" id="valorplurianualidadv" name="valorplurianualidadv" size="10" value="">
		<input type="hidden" id="valortipoAdjudicacion" name="valortipoAdjudicacion" size="10" value="">
		<input type="hidden" id="mImporteBruto" name="mImporteBruto" size="10" value="">
		<input type="hidden" id="cTolerancia" name="cTolerancia" />
		<!-- Inventario  -->
		<input type="hidden" id="cantidad_" name="cantidad_" size="10" value="">
		<input type="hidden" id="descrip_" name="descrip_" size="10" value="">
		<input type="hidden" id="CVE_UNI_" name="CVE_UNI_" size="10" value="">
		<input type="hidden" id="ajustePAAS" name="ajustePAAS" size="10" value="">
		<!-- Para las vistas -->
		<input type="hidden"  name="isAdmin" id="isAdmin" value="1"/>
		<input type="hidden"  name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>"/>
		<input type="hidden"  name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
		<input type="hidden"  name="modulo" id="modulo" value="MATERIALES"/> 				
		<input type="hidden" name="hayEP" id="hayEP" value="0"/>
		<input type="hidden" name="montoAnualTotalEPS" id="montoAnualTotalEPS" value="0" />
		<input type="hidden" name="cIdPartida" id="cIdPartida" />
		<div id="container" class="container" > 
		<h1> 
			Programa Anual de Adquisiciones,Arrendamientos y Servicios 
			<label id="lbOperacion" style="font-size: 8pt;"></label> 
		</h1>
		<br /> 
		<div class="tabs" id="tabsId"> 
			<ul> 
				<li> 
					<a id="consultaPrograma" onclick="javascript:actualizaMontos()" href="#tabs-0">Consulta</a>
				</li> 
				<li> 
					<a id="calendarioPrograma" href="#tabs-1">Calendario</a> 
				</li> 
				<li>
					<a id="reportePrograma" href="#tabs-2">Reportes</a> 
				</li>
				</ul> 
			<br/>
			<table>
				<tr>
					<td align="right">
						Unidad Ejecutora:<input type="text" id="ue_usuario" name="ue_usuario" style="border: 0px none ; width: 30em; color: red;" maxlength="120" readonly="readonly"/>
					</td>
					<td align="left" >
						<input type="text" id="porcentajeInflacion" name="porcentajeInflacion" value="Porcentaje de Inflacion:" style="border: 0px none ; visibility: hidden;"></input>
						<input type="checkbox" id="chk_precarga" name="chk_precarga" style="visibility:hidden;" /> 
						<input type="text" id="precargaPAAS" name="precargaPAAS" maxlength="3" style="width:5em; visibility: hidden;" onkeypress="return(onlyNumbers(event));" /> 
					</td>		 
				</tr>
			</table>
			<div align="center" id="tabs-0"> 
		   	<div>
				<fieldset> 
					<table width="800" border="0" align="center"> 
						<tr> 
							<td align="left" colspan="4"> 
								Unidad Ejecutora: 
								<select id="desUnidadResponsable1" name="desUnidadResponsable1" style="width: 30em;" onchange="actualizaMontosCapitulo();"> 
								<option value="<%=usuario.getU_UR()%>" selected="selected"></option></select> 
								&nbsp; 
								<button id="pbGuardaUE" style="visibility: hidden;">
									Agregar UE 
								</button> 
								&nbsp; 
								<button id="pbPrecarga" style="visibility: hidden;"> 
									Precarga 
								</button> 
							</td> 
						</tr>
					</table> 
				</fieldset> 
			</div> 
                  <br /> 
			<div id="formBuscar" style="visibility: hidden;"> 
				<fieldset> 
					<legend> 
						Filtro de Consulta 
					</legend> 
                          <table id="tblbuscaCucop">    
						<tr> 
							<td align="right"> 
								Capitulo: 
							</td> 
							<td> 
								<select id="mCatalogoCapitulo" name="mCatalogoCapitulo" style="width: 30em;" onchange="cambiaComboPartida(); actualizaMontoPartida();"> 
								</select> 
							</td> 
							</tr> 
							<tr> 
							<td align="right"> 
								Partida: 
							</td> 
							<td> 
								<select id="mCatalogoSubPartida" name="mCatalogoSubPartida" style="width: 30em;" onchange="actualizaMontoPartida();"> 
									<option value="" selected="selected"></option> 
								</select>
							</td>
						</tr>
						<tr> 
							<td align="right"> 
								Cucop: 
							</td> 
							<td> 
								<input type="text" id="cucopPa" name="cucop_pa" style="width: 30em;" /> 
							</td> 
						</tr> 
						<tr> 
							<td align="right"> 
								Descripcion: 
							</td> 
							<td> 
								<input type="text" id="desCucopPa" name="des_pa" style="width: 30em;" /> 
							</td> 
						</tr> 
						<tr style="display: none;"> 
							<td align="right"> 
								Tipo de proceso: 
							</td> 
							<td> 
								<select id="procesoPa" name="procesoPa" style="width: 30em;"></select> 
							</td>
						</tr>
						<tr>
							<td align="left" colspan="7"> 
								Capitulo 2000: 
								<input type="text" id="mMontoC2" name="mMontoC2" size="16" style="border: 0px none ;" readonly="readonly" /> 
								&nbsp;Capitulo 3000:&nbsp; 
								<input type="text" id="mMontoC3" name="mMontoC3" size="16" style="border: 0px none ;" readonly="readonly" /> 
								&nbsp;Capitulo 5000: &nbsp; 
								<input type="text" id="mMontoC5" name="mMontoC" size="16" style="border: 0px none ;" readonly="readonly" /> 
							</td> 
						</tr>
						<tr> 
							<td align="left" colspan="7"> 
								Total partida: 
								<input type="text" id="mMontoPartida" name="mMontoPartida" size="16" style="border: 0px none ;" readonly="readonly" /> 
								&nbsp;Presupuesto:&nbsp; 
								<input type="text" id="mTechoPresupuestal" name="mTechoPresupuestal" size="16" style="border: 0px none ;" readonly="readonly" />
								&nbsp;&nbsp; 
								<input type="text" id="mAvisoTecho" name="mAvisoTecho" style="color: red; border: 0px none; width: 250px; font-weight: bold;" readonly="readonly" />
							</td> 
						</tr>
						<tr align="left"> 
							<td colspan="2"> 
								Total:&nbsp; 
								<input type="text" id="mMontoTotal" name="mMontoTotal" style="border: 0px none ;" size="25" readonly="readonly" /> 
							</td> 
						</tr> 
					<tr> 
						<td width="33%"> 
							&nbsp; 
						</td> 
						<td width="33%" align="center"> 
							<button id="pbBuscacucop"> 
								Buscar 
							</button> 
							&nbsp;&nbsp;&nbsp; 
						</td> 
						<td width="33%" align="right"> 
							&nbsp; 

						</td> 
					</tr> 
					<tr> 
						<td align="left" colspan="2"> 
							IVA <select id="iva_pa" name="iva_pa" style="width: 8em;"> 
							<input type="button" id="pbivaTodos" name="iva_todos" value="Todos" /> 
						</td> 
						<td align="right" colspan="2"> 
						   Auto Ajuste PAAS
							 <select id="mesesCat" name="mesesCat" style="width: 8em;"> 
							 <input type="button" id="autoAjustePAASUE" name="autoAjustePAASUE" value="TOTAL" />
							 <input type="hidden" name="mesDisponible" id="mesDisponible" value="1"/>
						</td> 
					</tr> 
				</table>
			</fieldset> 
		</div> 
		<br /> 
		<div id="cucops-contain1"> 
			<fieldset> 
				<legend>Cucops Disponibles</legend> 
				<br /> 
				<table height="55" width="800" border="2" id="tblSeleccionaCucop" class="display"> 
					<thead> 
						<tr> 
			            	<th width="50">CUCOP</th> 
			                <th width="325">Descripcion</th> 
			                <th width="35">Partida</th> 
			                <th width="325">Descripcion Partida</th> 
			                <th width="15">Unidad  de Medida</th>						                         
							<th width="50">Tipo Proceso</th> 
						</tr> 
					</thead> 
				</table> 
			</fieldset> 
			</div> 
		    <br />
			<div id="cucops-contain"> 
				<fieldset> 
					<legend> 
						Cucops Seleccionados 
					</legend> 
					<br /> 
					<table width="950" border="2" id="tblCucops" class="display"> 
						<thead> 
							<tr> 
								<th width="70"> 
									CUCOP 
								</th> 
								<th width="60"> 
									Partida 
								</th> 
								<th width="200"> 
									Descripcion 
								</th> 
								<th width="35"> 
									Unidad de Medida 
								</th> 
								<th width="10"> 
									Cantidad 
								</th> 
								<th width="120"> 
									Precio Unitario Promedio
								</th> 
								<th width="30"> 
									IVA 
								</th> 
								<th width="120"> 
									Importe Bruto 
								</th> 
								<th width="120"> 
									Importe Neto 
								</th> 
								<th></th>
								<th></th>
								<th></th>
							</tr> 
						</thead> 
					</table> 
				</fieldset> 
			</div> 
		</div> 
		<div align="center" id="tabs-1"> 
			<table align="center"> 
				<tr> 
					<td align="right"> 
						UE Consultada: 
					</td> 
					<td align="left"> 
						<input type="text" id="ue_usuarioEdita" name="ue_usuarioEdita" style="border: 0px none ; width: 30em;" maxlength="120" readonly="readonly" /> 
					</td> 
				</tr> 
				<tr> 
					<td align="right"> 
						Cucop: 
					</td> 
					<td align="left" colspan="2"> 
						<input type="text" id="cucopEdita" name="cucopEdita" readonly="readonly" style="border: 0px none ; width: 5em; color: red;" /> 
								-- 
						<input type="text" id="cCABMEdita" name="cCABMEdita" readonly="readonly" style="border: 0px none ; width: 25em; color: red;" /> 
					</td> 
				</tr> 
				<tr> 
					<td align="right"> 
						Partida: 
					</td> 
							<td align="left"> 
							<input type="text" id="partidaEdita" name="partidaEdita" style="border: 0px none ; width: 30em;" readonly="readonly" /> 
						</td> 
					 
					</tr> 
					<tr> 
						<td align="right"> 
							Total: 
						</td> 
						<td align="left"> 
							<input type="text" id="totalEdita" name="totalEdita" readonly="readonly" style="border: 0px none ; width: 30em;" /> 
						</td> 
					</tr> 
					<tr> 
					   <td align="right"> 
<!-- 									<input type="text" id="ivaEdita" name="ivaEdita" style="width: 4em;" onkeypress="return(onlyNumbers(event));" maxlength="3" />%  -->
							<select id="ivaEdita" name="ivaEdita" style="width: 8em;"> 
							</select> 
						</td> 
						<td align="left" colspan=""> 
							Iva 
						</td> 
						<td> 
							 
							</td> 
							 
						</tr> 
						 
						 
						<tr> 
						   <td align="right"> 
								$<input type="text" id="pymeEdita" name="pymeEdita" maxlength="15"  style="width: 7em;" onkeypress="return(onlyNumbers2(event));" maxlength="4" /> 
							</td> 
							<td align="left"> 
								Cantidad destinado a MIPyME  
							</td> 
							 
						</tr> 
						 
						 
						<tr> 
						   <td align="right"> 
								$<input type="text" id="porcentajeEdita" name="porcentajeEdita" maxlength="15"  style="width: 7em;" onkeypress="return(onlyNumbers2(event));" maxlength="4" /> 
							</td> 
							<td align="left" colspan="2"> 
								estimado de compras no cubiertas por tratados 
							</td> 
						</tr> 
						<tr> 
						<td align="right"> 
								<select id="tipoProcedimientoEdita" name="tipoProcedimientoEdita" style="width: 15em;"> 
								</select> 
							</td> 
							<td align="left"> 
								Procedencia 
							</td> 
						</tr> 
						<tr> 
						<td align="right"> 
							Plurianual: <input type="checkbox" id="chk_plurianualidad" name="chk_plurianualidad" /> 
						</td> 
							<td align="left" colspan="6"> 

							<input name="EP" id="EP"  value="Ejercicio de Plurianualidad"size="23" style="border-width:0; background-color:transparent;visibility: hidden;" onkeypress="return(onlyNumbers(event));"/>
							<input type="text" id="plurianualidad" name="plurianualidad" maxlength="2" style="width: 2em; visibility: hidden;" onkeypress="return(onlyNumbers2(event));" /> 
							<input name="anos" id="anos"  value="A&ntilde;os"size="8" style="border-width:0; background-color:transparent;visibility: hidden;" onkeypress="return(onlyNumbers(event));"/>
							<br><input name="ME" id="ME"  value="Monto a Ejercer $" size="15" style="border-width:0; background-color:transparent; visibility: hidden;" onkeypress="return(onlyNumbers(event));" />
							<input type="text" id="plurianualidadv" name="plurianualidadv" maxlength="15" style="width: 7em; visibility: hidden;" onkeypress="return(onlyNumbers2(event));" />
							</td> 
						</tr> 
						<tr> 
							<td align="right">Tipo de Procedimiento</td> 
							<td align="left" colspan="6"> 
								<select id="tipoAdjudicacion" name="tipoAdjudicacion" style="width: 22em;"> 
								</select> 
							</td>
						</tr> 
						 	<tr> 
							<td align="right">Existencias: </td> 
							<td align="left">
								<input type="text" id="descrip" name="descrip" readonly="readonly" style="border: 0px none ; width: 15em;" />  
								<input type="text" id="cantidad" name="cantidad" readonly="readonly" style="border: 0px none ; width: 1em;" />
								<input type="text" id="CVE_UNI" name="CVE_UNI" readonly="readonly" style="border: 0px none ; width: 5em;" />
							</td> 
						</tr> 
						<tr> 
							<td width="33%"> 
								&nbsp; 
							</td> 
							<td align="left"> 
								<button id="pbCopiarEdita"> 
									Copiar 
								</button> 
								&nbsp;&nbsp;&nbsp; 
								<button id="pbGuardaEdita"> 
									Guardar 
								</button> 
							</td> 
						</tr> 
							 <tr>
							 <td align="right" colspan="5" >
							  Auto Ajuste PAAS 
						        </td>   
							 </tr>
						<tr>	
						  <td align="right" colspan="8">
								<button id="autoAjustePAAS"> 
									PARCIAL
								</button> 
						  </td>
						 </tr>
                    </table> 
			<br /> 
			<table id="tblcucopPeriodo" class="display"> 
            <thead> 
                <tr> 
                	<th>Trimestre</th>
                	<th>Cantidad Disponible</th>
                	<th>Cantidad</th>	                     
                    <th>Precio Unitario</th> 
                    <th>Importe Bruto</th> 
                    <th>Importe Neto</th> 
                    <th>Cantidad En solicitudes</th> 
                    <th>Cantidad Disponible</th> 
                    <th>Monto en solicitudes</th>	 
                    <th>Monto Disponible</th>
                    <th>Mantener</th>		                     
              </tr> 
            </thead> 
        </table> 
        <br /> 
         <br /> 
        
         
        </div> 
	<div align="center" id="tabs-2">
		<table width="97%" align="left">
			<tr> 
				<td align="left" colspan="4"> 
						Unidad Ejecutora: 
				 
					<select id="desUnidadResponsable2" name="desUnidadResponsable2" style="width: 30em;"> 
					<option value="<%=usuario.getU_UR()%>" selected="selected"> 
					</option>
					</select> 
				</td> 
			</tr> 
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_mProgramaAnualOrdenadoPorPartida_xls.jasper" CHECKED>Programa Anual Ordenado Por Partida (Detallado)</td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_mProgramaAnualOrdenadoPorPartidaAcumulado.jasper">Programa Anual Ordenado Por Partida (Acumulado)</td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_mProgramaAnualPorPartidayTrimestre.jasper">Programa Anual Resumido Por Partida y Trimestre</td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="fn_mProgramaAnualMontosPorCapituloReporte.jasper">Programa Anual de Adquisiciones Resumido Por Unidad y Capitulo </td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas.jasper">Programa Anual de Adquisiciones Resumido Por Unidad y Capitulo Restando Partidas </td></tr>  -->
			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_CompraNET.jasper">Programa Anual Ordenado Por Partida Compranet </td></tr>
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados.jasper">Programa Anual de Adquisiciones CUCOPS sin documentos asociados </td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rpt_programaAnualResumidoPorCapitulosPartidasNacionalOarea.jasper">Programa Anual resumido Partida y Capitulos</td></tr>				 -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="fn_mProgramaAnualConciliacionPresupuestal.jasper">Programa Anual Conciliacion Presupuestal</td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="mConsiliacion_RC_Apartado_Precompromiso.jasper">Conciliacion Presupuestal RC VS Precomprometido</td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="mConsiliacion_AP_PRE_Comp_Eje.jasper">Conciliacion Presupuestal APARTADO VS PAGADO</td></tr> -->
<!-- 			<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="mCatalogoInventarioArticulosdeAlmacen.jasper">Inventario de Articulos en Almacenes</td></tr> -->
			<tr><td align="center">
				<img  id="cmdPdfReporteProg" name="cmdPdfReporteProg" style="cursor: pointer" src="../../imagenes/icono_PDF.jpg" onclick="javascript:openARCH('pdf');" />PDF&nbsp;
				<img  id="cmdxlsReporteProg" name="cmdxlsReporteProg" style="cursor: pointer" src="../../imagenes/icono_excel.jpg" onclick="javascript:openARCH('xls');" />xls&nbsp;
				<img  id="cmdcsvReporteProg" name="cmdcsvReporteProg" style="cursor: pointer" src="../../imagenes/icono_excel.jpg" onclick="javascript:openCSV();" />csv&nbsp;
				<img  id="cmdwordReporteProg" name="cmdwordReporteProg" style="cursor: pointer" src="../../imagenes/icono_word.png" onclick="javascript:openARCH('doc');" />word
			</td></tr> 
		</table>
		
		<table>
			<tr>
				<td>
					<iframe name="generaArchivo"  id="generaArchivo" width="100px" height="100px" style="display: none"></iframe>
				</td>
			</tr>
		</table>
	</div> 		 
	</div> 
		</div> 
	</form>
	<form name="formReportes" id="formReportes" action="../SAICYS/Procesando.jsp" method="GET" target="_self"><!-- target="_self" -->
		<input type="hidden" name="catalogo" id="catalogo" value="REPORTE">
		<input type="hidden" name="accion" id="accion" value="run">
		<input type="hidden" name="rn" id="rn">
		<input type="hidden" name="formato" id="formato">
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora">
		
		
		
	</form>
	</body> 
</html>
