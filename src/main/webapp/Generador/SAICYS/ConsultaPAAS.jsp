<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	String role="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
	Calendar c1 = Calendar.getInstance(); // today
	int mesActual=c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String mesAct="";
	if(mesActual<10){
		mesAct="0"+mesActual;
	}else{
		mesAct=""+mesActual;
	}
	String ueOrig=usuario.getU_UR_Orig() == null ? usuario.getU_UR(  ): usuario.getU_UR_Orig(); 
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>'ConsultaPAAS.jsp'</title>
	<script type="text/javascript" charset="utf-8">
  		$(document).ready(function() {
  			<%
  				int tabla=0;
  				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
  				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role =(String)r.getKey();
					roles+= r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
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
  			%>
  			var roles="<%=roles%>";
		    if( roles.indexOf("ADMIN_RECMAT") >=0){
		    	$('#isAdmin').val(0);
		    }
		    tips = $( ".validateTips" );
		    precargaPAAS = $("#precargaPAAS");
		    allFields = $( [] ).add(precargaPAAS);
		    querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "desUnidadResponsable1", {async: false });
		    queryFormPost("techoPresupuestalActivado",{async : false});
		    querySelectPost("mCatalogoTipoIVA", "iva_pa", {async : false});
		    querySelectPost("mPeriodoRead2", "mesesCat", {async : false});
		    $("#UE").val($("#desUnidadResponsable1").val());
		    intiTbls();
		    $("#mesesCat").val("<%=mesActual%>");
			if($("#desUnidadResponsable1").val() != 0 ){
				queryFormPost("existeUnidadEjecutora",{async:false});
			}else{
				$("#tieneUe").val(0);
			}
  			if($("#tieneUe").val()==-1){
				swal("La unidad ejecutora no tiene un PAAS asignado, favor de crearlo.",{icon:"info",button: "Cerrar"});
				$("#pbGuardaUE").css("visibility","visible");
				$("#trCargaManual").show();
				$("#pbPrecarga").css("visibility","visible");
				$("#chk_precarga").css("visibility","visible"); 
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
			//para darle formato a los montos	
			$("#mMontoC2").formatCurrency();
			$("#mMontoC3").formatCurrency();
			$("#mMontoC4").formatCurrency();
			$("#mMontoC5").formatCurrency();
			$("#mMontoC6").formatCurrency();
			$("#mMontoTotal").formatCurrency();
			$("#ejercicio").val($("#cEjercicio").val());
			$( "#chk_precarga" ).change(function() {
				if ($('#chk_precarga').is(':checked')) {
					$("#precargaPAAS").css("visibility","visible");
					$('#chk_precarga').val(1);
				}else{
					$("#precargaPAAS").css("visibility","hidden");
					$('#chk_precarga').val(0);
				}
			});
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
			    
			    oTable1=$('#tblSeleccionaCucop').dataTable(
				{
					bScrollCollapse: true,
					bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_agregacucops&qw="+qw1,
					aaSorting: [[0, "asc" ]] ,
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
			$("#autoAjustePAASUE").button().click(function(){
				$("#usuarioUE").val('<%=usuario.getU_UR_Orig()%>');
				if(($('#desUnidadResponsable1').val().trim()== $("#usuarioUE").val()) ||($("#desUnidadResponsable1").val().trim()=="E04" &&$("#usuarioUE").val()=="E02")|| ( roles.indexOf("ADMIN_RECMAT") >=0  || ( roles.indexOf("ANALISTA") >=0)) ){
					swal({
						title: "Est\u00e1 seguro que desea autoajustar montos y cantidades programadas con lo solicitado?",
						text: "Esta acci\u00f3n ajustara todo el PAAS de la unidad ejecutora y no podr\u00e1 revertirse!",
						icon: "info",
						buttons: {
							confirm : "Aceptar",
							cancel: "Cancelar"
							},
						}).then((continuar) => {
							if (!continuar) {
								return;
						}else{
							var res="";   
						    res="ajustaTodoUE";
						    $("#cucopEdita").val('');
						    $("#ajustePAAS").val(res);
							queryFormPost("pa_autoAjustePAAS", 
							{ 
								async : false, 
								callback : function() 
								{	
									if($("#desUnidadResponsable1").val() == 0){
										queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
									}else{
										queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
									}
									//se pone formato tipo moneda
									$("#mMontoC2").formatCurrency();
									$("#mMontoC3").formatCurrency();
									$("#mMontoC4").formatCurrency();
									$("#mMontoC5").formatCurrency();
									$("#mMontoC6").formatCurrency();
									$("#mMontoTotal").formatCurrency();
									swal("El Ajuste del PAAS de toda la Unidad Ejecutora se realizo correctamente.",{icon:"info",button: "Cerrar"});
									$("#pbBuscacucop").button().click();	
								}
							});		
							//Guarda en la Bitácora
							$("#cAccion").val("AUTO_AJUSTEPAAS_TOTAL");
							$("#cIdDocumento").val($("#desUnidadResponsable1").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});	
						}
					});
				}else{
					swal("Solo un administrador o un usuario de la Unidad Ejecutora pueden realizar el auto ajuste.",{icon:"warning",button: "Cerrar"});
					return;
				}
			});
			$("#pbivaTodos").button().click(function(){
		    	if(parseFloat( $("#iva_pa").val()) > 100 ){
	               	swal("El porcentaje de IVA no puede ser mayor a 100.",{icon:"info",button: "Cerrar"});
	               	return;
				}
		    	$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		    	//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
		     	if($("#iva_pa").val()!=""){
		     		swal({
		       			title: "Desea Asignar IVA a todos los CUCOPS del Programa Anual?",
		       			text: "Se procedera a hacer la actualizaci\u00f3n!",
		       			icon: "info",
		       			buttons: {
		       				confirm : "Aceptar",
		       				cancel: "Cancelar"
		       				},
		       			}).then((continuar) => {
		       				if (!continuar) {
		       					return;
		       			}else{
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
							$("#mMontoC4").formatCurrency();
							$("#mMontoC5").formatCurrency();
							$("#mMontoC6").formatCurrency();
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
		       			}
		       		});
		       		
				}else{
		        	return;
				}
			});
			$('#tblSeleccionaCucop tr').live('dblclick', function() {
				$("#usuarioUE").val('<%=usuario.getU_UR()%>');
				if ( $(this).hasClass('row_selected') ){           
					$(this).removeClass('row_selected');
				}else{
	               	$(this).addClass('row_selected'); 
                    /// se inserta cucop seleccionado
					var aTrs = $('#tblSeleccionaCucop').dataTable().fnGetNodes();
		     		var countSelect=0;
		     		
					for ( var i=aTrs.length ; i>=0; i-- ){       
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
									swal("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.",{icon:"warning",button: "Cerrar"});
									$(this).removeClass('row_selected');
									return;
								}else{
									queryFormPost("hayDisponibleEnEPS",{async:false});
									if($("#montoAnualTotalEPS").val()==0){
										swal("No hay monto disponible en tus EPS.\nSolicitar adecuación presupuestal.",{icon:"warning",button: "Cerrar"});
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
								swal("No se pudo agregar el cucop.",{icon:"warning",button: "Cerrar"});
							}
						}				
					}
				}
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
									swal("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.",{icon:"warning",button: "Cerrar"});
									return;
								}else{
									queryFormPost("hayDisponibleEnEPS",{async:false});
									if($("#montoAnualTotalEPS").val()==0){
										swal("No hay monto disponible en tus EPS.\nSolicitar adecuación presupuestal.",{icon:"warning",button: "Cerrar"});
									}
								}
								window.location = "ProgramaAnual.jsp?tab=1&editaCIdCABM="+$("#editaCIdCABM").val()+"&cEjercicio="+$("#cEjercicio").val()
								+"&desUnidadResponsable1="+$("#desUnidadResponsable1").val()+" &editaCIdSubPartida="+$("#editaCIdSubPartida").val()
								+"&CVE_UNI="+$("#cUnidadMedida_"+aData[3]+"").val()+"&descrip="+encodeURIComponent($("#cCABM_"+aData[2]+"").val());
							}					              
						}			
					}
				}
			});
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
						swal("El porcentaje de Inflacion no puede ser mayor a 100.",{icon:"warning",button: "Cerrar"});
						return;
					}
		            if(parseFloat( $("#precargaPAAS").val())< 0 ){
		            	swal("El porcentaje de Inflacion no puede ser menor a 0.",{icon:"warning",button: "Cerrar"});
		                return;
					}
					if(parseFloat( $("#precargaPAAS").val())== 0 ){
		            	swal("El porcentaje de Inflacion debe de ser mayor a 0.",{icon:"warning",button: "Cerrar"});
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
				$("#loginPrecarga").val('<%=usuario.getLogin()%>');
				$("#unidadEjecutoraPrecarga").val('<%=usuario.getU_UR()%>');
				$("#centroContablePrecarga").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
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
								swal("La precarga del programa anual se realiz\u00f3 de forma correcta.",{icon:"info",button: "Cerrar"});
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
								$("#mMontoC4").formatCurrency();
							    $("#mMontoC5").formatCurrency();
							    $("#mMontoC6").formatCurrency();
							    $("#mMontoTotal").formatCurrency();		
													            
							            //se pone visible formulario de consulta
							             $("#formBuscar").css("visibility","visible");
							             $("#tieneUe").val(1);
							             $("#pbGuardaUE").css("visibility","hidden");
							             $("#trCargaManual").hide();
							             $("#trEjercicioAnterior").hide();
							             $("#trLayout").hide();
								         $("#pbPrecarga").css("visibility","hidden");
								         $("#chk_precarga").css("visibility","hidden"); 
					                     $("#precargaPAAS").css("visibility","hidden");
					                     $("#porcentajeInflacion").css("visibility","hidden");
					                     
								         $("#cUnidadResponsable").val('<%=usuario.getU_UR()%>');
								        //se inserta en el campo de texto ue_usuario la unidad ejecutora que corresponde al uuario.
								         queryFormPost("UnidadRead", {async: false });
									
								break;
								case "1":  
									swal("Error al precargar el PAAS.",{icon:"error",button: "Cerrar"});
								break;
							}
						}
				});
			});
			$("#pbGuardaUE").button().click(function(){
				//se valida si es administrador o usuario de la unidad ejecutora seleccionada
				$("#usuarioUE").val('<%=usuario.getU_UR()%>');
				//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
				var elParametro=""+$("#cEjercicio").val()+","+$("#desUnidadResponsable1").val()+",<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>,<%=usuario.getLogin()%>";
		        $.getJSON("../../servlet/ProgramaAnualServlet?operacion=0",{Tabla: "", Param: elParametro, MaxReg: "", ajax: 'false'}, 
		        function(j){
		        	for(var i = 0; i < j.length; i++){
		            	var col=j[i].Col1;
					}
					if(j[0].Col1=="1"){
				    	swal("Ya existe un registro con los datos que desea ingresar, favor de validar.",{icon:"warning",button: "Cerrar"});
					}else{
						swal("El PAAS se agreg\u00f3 de forma correcta.",{icon:"info",button: "Cerrar"});
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
					    $("#mMontoC4").formatCurrency();
					    $("#mMontoC5").formatCurrency();
					    $("#mMontoC6").formatCurrency();
					    $("#mMontoTotal").formatCurrency();		
						//se pone visible formulario de consulta
				        $("#formBuscar").css("visibility","visible");
				        $("#tieneUe").val(1);
				        $("#pbGuardaUE").css("visibility","hidden"); 
				        $("#trCargaManual").hide();
				        $("#trEjercicioAnterior").hide();
				        $("#trLayout").hide();
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
  		});//Fin del document ready
  		function intiTbls(){
  			oTable1= $("#tblSeleccionaCucop").dataTable({
  				bAutoWidth : false,
			   	bRedraw:false,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollX: "100%",
				iDisplayLength: 10,
			    sPaginationType: "full_numbers",
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
				bJQueryUI: true 
			});				
		       	//tabla cucop agregado
			oTable2= $("#tblCucops").dataTable({
			   	bAutoWidth : false,
			   	bRedraw:false,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollX: "100%",
				iDisplayLength: 10,
			    sPaginationType: "full_numbers",
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
				bJQueryUI: true
				
			});
  		}
  		function actualizatablaCucops(query){
			//se borra la tabla antes de realizar la consulta
			$('#tblCucops').dataTable().fnClearTable();
			//tabla cucop agregado
			oTable2= $("#tblCucops").dataTable({
				"bLengthChange" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : true,
				"bPaginate" : true,
				"bAutoWidth" : true,
				"bScrollCollapse" : true,
				
				"sScrollX": "100%",
				"sPaginationType" : "full_numbers",
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide": true,
				"fnInitComplete": function() {
					oTable2.fnAdjustColumnSizing();
				},
				"iDisplayLength": 10,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCucopsGridBD&qw="+ query,
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
		          	oTable2.fnAdjustColumnSizing();
				}
			});
		}
		function cambiaComboPartida(){
			querySelectPost("cambiaCombomCatalogoSubPartidaRead", "mCatalogoSubPartida", {async : false});
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
		function eliminaCucop(indice){
			$("#usuarioUE").val('<%=usuario.getU_UR()%>');
			//if(($('#desUnidadResponsable1').val()== $("#usuarioUE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){
			$("#borraCIdCABM").val($("#cIdCABM_"+indice+"").val());
			$("#borraIdSubPartida").val($("#cIdSubPartida_"+indice+"").val());
			swal({
				title: "Est\u00e1 seguro de borrar el cucop "+$("#cIdCABM_"+indice+"").val()+" del programa anual?",
				text: "Se procedera a borrar la informaci\u00f3n",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					$.ajax({
						url: '../../servlet/ProgramaAnualServlet',
						dataType: 'json',
						data: {"cIdUnidadEjecutora" : $("#usuarioUE").val(),"cCucop":$("#borraCIdCABM").val(),"cPartida":$("#borraIdSubPartida").val(),"operacion":"3"},
						async : false,
						type: 'POST',
						success : function(j) {
							swal(j[0].MSG,{icon:"info",button: "Cerrar"});
							if(j[0].RESP){
								$("#pbBuscacucop").button().click();
								actualizaMontosCapitulo();
							}
							
						}
					});
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
			swal(t,{icon:"info",button: "Cerrar"});
		}
		function actualizaMontosCapitulo(){
			$("#usuarioUE").val('<%=usuario.getU_UR()%>');
			var roles="<%=role%>";
			if( roles.indexOf("ADMIN_RECMAT") >=0){
				$('#isAdmin').val(0);
			}
			$("#tieneUe").val(-1);
			if($("#desUnidadResponsable1").val() != 0 ){
				queryFormPost("existeUnidadEjecutora",{async:false});
				$("#UE").val($("#desUnidadResponsable1").val());
				cambiaCentrocontableUsuario($("#desUnidadResponsable1").val());
			}else
				$("#tieneUe").val(0);
					
			if($("#tieneUe").val() == -1){
				swal("La unidad ejecutora no tiene un PAAS asignado, favor de crearlo.",{icon:"info",button: "Cerrar"});
				$("#cucops-contain1").css("visibility","hidden");
				$("#cucops-contain").css("visibility","hidden");
				$("#pbGuardaUE").css("visibility","visible");
				//$("#trEjercicioAnterior").show();// se inhabilita la carga por ejercicio anterior
				//$("#trLayout").show();// se inhabilita la carga por layout
				$("#trCargaManual").show();
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
				$("#trEjercicioAnterior").hide();
				$("#trLayout").hide();
				$("#trCargaManual").hide();
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
				$("#mMontoC4").formatCurrency();
				$("#mMontoC5").formatCurrency();
				$("#mMontoC6").formatCurrency();
				$("#mMontoTotal").formatCurrency();	
				 count=true;
				$("#pbBuscacucop").click();
				actualizaMontoPartida();
			}
		}
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '-0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true; 
		}
		function cargaLayout(){
			$.ajax({
			    url: '../../servlet/ProgramaAnualServlet?operacion=2',
			    data: $("#uploadfileEncabezado").attr('file'),
			    cache: false,
			    contentType: 'multipart/form-data',
			    processData: false,
			    dataType: 'json', 
			    type: 'POST',
			    success: function(json){
			        swal(json[0].Respuesta,{icon:"info",button: "Cerrar"});
			    },
			    error: function(){
			    	swal("Error.",{icon:"error",button: "Cerrar"});
			    }
			});
		}
		function ejemploSubmit(){
			$("#upform").submit();
		}
		function cambiaCentrocontableUsuario(ue){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : ue},
				async : false,
				success : function(j) {
					if(j[0].error){
						swal("No se realiz\u00f3 el cambio de centro contable y unidad ejecutora",{icon:"info",button: "Cerrar"});
					}else{
						$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
					}
					
				}
			});
		}
  	</script>
  </head>
  
  <body>
    	<div id="container" class="container" style="width: 98%;">
    		
    			<fieldset>
				<table  border="0" align="left"> 
					<tr >
						 
						<td align="left">
							<form action="">
							Unidad Ejecutora: 
							<select id="desUnidadResponsable1" name="desUnidadResponsable1" style="width: 30em;" onchange="actualizaMontosCapitulo();"> 
							<option value="<%=usuario.getU_UR()%>" selected="selected"></option></select>
							</form> 
						</td> 
					</tr>
				
					<tr id="trCargaManual" style="display: none;">
						<td>
							<fieldset>
								<legend>Carga Manual</legend>
								<form action="">
									<input type="button" id="pbGuardaUE" name="pbGuardaUE" style="visibility: hidden;" value="Agregar UE" class="btnInterfaceBG ui-button ui-corner-all"/>
								</form>
							</fieldset>
							
						</td>
					</tr>
					<tr id="trEjercicioAnterior" style="display: none;">
						<td>
							<fieldset>
								<legend>Ejercicio anterior</legend>
								<form>
								<input type="text" id="porcentajeInflacion" name="porcentajeInflacion" value="Porcentaje de Inflacion:" style="border: 0px none ; visibility: hidden;"></input>
								<input type="checkbox" id="chk_precarga" name="chk_precarga" style="visibility:hidden;" /> 
								<input type="text" id="precargaPAAS" name="precargaPAAS" maxlength="3" style="width:5em; visibility: hidden;" onkeypress="return(onlyNumbers(event));" />
						
								<input type="button" id="pbPrecarga" style="visibility: hidden;" value="Precarga" class="btnInterfaceBG"/> 
								</form>
							</fieldset>
						</td>
					</tr>
					<tr id="trLayout" style="display: none;">
						<td>
							<fieldset>
								<legend>Layout</legend>
								<form name="upform" id = "upform" action="../../servlet/ProgramaAnualServlet?operacion=2"  enctype = "multipart/form-data" method = "post" >
									<input type="hidden" id="ejercicio" name="ejercicio" value="" />
									<input type="hidden" id="UE" name="UE" value="" />
									Encabezado: <input id="uploadfileEncabezado" name="uploadfileEncabezado" type="file" />
									Detallado: <input id="uploadfileDetallado" name="uploadfileDetallado" type="file" />
									<input type="button" style="display: none;" id="btnAjaxLayout"   name="btnAjaxLayout" value="Enviar" onclick="cargaLayout();" class="btnInterfaceBG"/>
									<input type="button" value="Envia" onclick="ejemploSubmit()" class="btnInterfaceBG"/>
								</form>
							</fieldset>
							
						</td>
					</tr>
				</table>
			</fieldset>
    		
			<br />
			<form id="formConsultaRecep">
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
									<input type="text" id="mMontoC2" name="mMontoC2" size="16" style="border: 0px none ;background:#FEFEFE " readonly="readonly" /> 
									&nbsp;Capitulo 3000:&nbsp; 
									<input type="text" id="mMontoC3" name="mMontoC3" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
									&nbsp;Capitulo 4000:&nbsp; 
									<input type="text" id="mMontoC4" name="mMontoC4" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
									
								</td> 
							</tr>
							<tr>
								<td align="left" colspan="7"> 
									&nbsp;Capitulo 5000: &nbsp; 
									<input type="text" id="mMontoC5" name="mMontoC5" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
									&nbsp;Capitulo 6000: &nbsp; 
									<input type="text" id="mMontoC6" name="mMontoC6" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
								</td> 
							</tr>
							<tr> 
								<td align="left" colspan="7"> 
									Total partida: 
									<input type="text" id="mMontoPartida" name="mMontoPartida" size="16" style="border: 0px none ;background:#FEFEFE;" readonly="readonly" /> 
									&nbsp;Presupuesto:&nbsp; 
									<input type="text" id="mTechoPresupuestal" name="mTechoPresupuestal" size="16" style="border: 0px none ;background:#FEFEFE;" readonly="readonly" />
									&nbsp;&nbsp; 
									<input type="text" id="mAvisoTecho" name="mAvisoTecho" style="color: red; border: 0px none;background:#FEFEFE; width: 250px; font-weight: bold;" readonly="readonly" />
								</td> 
							</tr>
							<tr align="left"> 
								<td colspan="2"> 
									Total Cap:&nbsp; 
									<input type="text" id="mMontoTotal" name="mMontoTotal" style="border: 0px none ;background:#FEFEFE" size="25" readonly="readonly" /> 
								</td> 
							</tr> 
							<tr> 
								<td width="33%"> 
									&nbsp; 
								</td> 
								<td width="33%" align="center"> 
									<input type="button" id="pbBuscacucop" name="pbBuscacucop" value="Buscar" class="btnInterfaceBG"/> 
									&nbsp;&nbsp;&nbsp; 
								</td> 
								<td width="33%" align="right"> 
									&nbsp; 
		
								</td> 
							</tr> 
							<tr> 
								<td align="left" colspan="2"> 
									IVA <select id="iva_pa" name="iva_pa" style="width: 8em;"> </select>
									<input type="button" id="pbivaTodos" name="iva_todos" value="Todos" class="btnInterfaceBG"/> 
								</td> 
								<td align="right" colspan="2"> 
								   Auto Ajuste PAAS
									 <select id="mesesCat" name="mesesCat" style="width: 8em;"> </select>
									 <input type="button" id="autoAjustePAASUE" name="autoAjustePAASUE" value="TOTAL" class="btnInterfaceBG" />
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
					<table id="tblSeleccionaCucop" class="display" style="width: 100%">
						<thead> 
							<tr> 
				            	<th >CUCOP</th> 
				                <th >Descripcion</th> 
				                <th >Partida</th> 
				                <th >Descripcion Partida</th> 
				                <th >Unidad  de Medida</th>						                         
								<th >Tipo Proceso</th> 
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
					<table  id="tblCucops" class="display" > 
						<thead> 
							<tr> 
								<th > 
									CUCOP 
								</th> 
								<th> 
									Partida 
								</th> 
								<th > 
									Descripcion 
								</th> 
								<th > 
									Unidad de Medida 
								</th> 
								<th > 
									Cantidad 
								</th> 
								<th > 
									Precio Unitario Promedio
								</th> 
								<th > 
									IVA 
								</th> 
								<th > 
									Importe Bruto 
								</th> 
								<th > 
									Importe Neto 
								</th> 
								<th>Eliminar</th>
								<th style="display: none;"></th>
								<th style="display: none;"></th>
							</tr> 
						</thead> 
					</table> 
				</fieldset> 
			</div>
			<input type="hidden" id="cucopEdita" name="cucopEdita" value=""/>
		</form>
		</div>
	</body>
</html>