<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String cIdContratoDefinitivo="";
	if(request.getParameter("cIdContratoDefinitivo")!=null){
		cIdContratoDefinitivo=request.getParameter("cIdContratoDefinitivo");
		session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, cIdContratoDefinitivo);
	}else{
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_ContratCap4Definitivo);
	}
	int editar=0;
	int imgAprobar=0;
	int imgDevolver=0;
%>

<!DOCTYPE html>
<html>
  <head>
    
    <title>My JSP 'PrecompromisoContratoCap4.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		var oTableMP="";
		var vcontrato;
		var vfolio;
		var roles;
		$(document).ready(function() {
			<%
			    String roles="";
				//botones
				NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"ContratoCap4","PrecompromisoContratoCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarPresupuestoContCap4".equals(img)){  
						imgAprobar=1;
					}
					if ("imgDevolverPresupuestoContcap4".equals(img)){
						imgDevolver=1; 
					}	
				}

				%>
			roles="<%=roles%>";
			initQuerys();
			vcontrato=$("#cIdContratoDefinitivo").val();
			initTables();
		  
			if(parseInt($("#nIdEstado").val(),10) ==3 || parseInt($("#nIdEstado").val(),10) ==4){
				//En caso que sea un contrato con un preCompromiso ya hecho
				cargaContratoPrecomprometido();
				$("#mComprometido").val($("#totalNeto").val());
				if($("#isAbierto").val()==1 && $("#nComprometeMax").val()==1 ){
					$("#mComprometido").val($("#totalNetoMax").val());
					$('#flexCheckMax').prop('checked', true);
					$("#totalNeto").val($("#totalNetoMax").val());
				}
			}
			else{ //Carga la tabla con valores 0
				cargaPreCompromisoVacio();
			}	
				
			cargaSuficiencias();
			
		});//Fin del document ready
		function editCeldasDetalle(){
	       		var nRow = editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
	   	}
		function initQuerys(){
			queryFormPost("obtieneDatosContCap4", {async : false,
				callback: function(){
					showButtons();
				}
			});
		}
		function showButtons(){
			switch(parseInt($("#nIdEstado").val(),10)) {
				case 2:
			        $("#imgAprobarpreCompromisoContCap4").show();
					$("#imgAprobarCompromisoContCap4").hide();
					$("#imgDevolverpreCompromisoContCap4").hide();
					$("#spanDevolver").hide();
			        break;
		     	case 3:
			       	$("#imgAprobarpreCompromisoContCap4").hide();
					if(roles.indexOf("ADMIN_RECMAT") >= 0 || roles.indexOf("JEFES") >= 0){
						$("#imgAprobarCompromisoContCap4").show();
					}else{
						$("#imgAprobarCompromisoContCap4").hide();
					}
					$("#imgDevolverpreCompromisoContCap4").show();
					$("#spanDevolver").show();
					$("#edit").hide();
			        break;
		     	default:
		     		$("#imgAprobarpreCompromisoContCap4").hide();
					$("#imgAprobarCompromisoContCap4").hide();
					$("#imgDevolverpreCompromisoContCap4").hide();
					$("#spanDevolver").hide();
					$("#edit").hide();
		   			break;
			}
			if($("#isAbierto").val()==0){
				$("#trTotalNetoMax").hide();
			}else{
				$("#trTotalNetoMax").show();
			}
		}
		function initTables(){
			

			$('#dt_suficiencia').dataTable({
  			    "bPaginate": false,
      			"bLengthChange": false,
      			"bFilter": false,
      			"bSort": false,
      			"bInfo": false,
      			"bAutoWidth": false, 
				"sScrollX": 100,         
				"sScrollY": 100,         
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"    
			} );
		}
		function cargaPreCompromisoVacio(){
			var oTable=$('#dt_preCompromiso').dataTable( {
				bAutoWidth : true,
				bPaginate:false,
				bFilter : false,
				bPaginate: false,
				sScrollX: "100%",
      			bLengthChange: false,
      			bFilter: false,
      			bInfo: false,
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
			

			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,   
			//Carga el calendario con valores de 0
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + vcontrato + "'",
			aoColumns: [
				{ sName: "ClaveSIAFF",bSortable: false },
				{ sName: "ClaveInterna",bSortable: false },
				{ sName: "compromiso01",bSortable: false },
				{ sName: "compromiso02",bSortable: false },
				{ sName: "compromiso03",bSortable: false },
				{ sName: "compromiso04",bSortable: false },
				{ sName: "compromiso05",bSortable: false },
				{ sName: "compromiso06",bSortable: false },
				{ sName: "compromiso07",bSortable: false },
				{ sName: "compromiso08",bSortable: false },
				{ sName: "compromiso09",bSortable: false },
				{ sName: "compromiso10",bSortable: false },
				{ sName: "compromiso11",bSortable: false },
				{ sName: "compromiso12",bSortable: false },
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
           		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }]
              } ) ;		
			
		}
		function editPrecompromiso( oTableLocal ){
			//funcion para agregar los inputs a la tabla
			var i,j,descMes, nomMes,nColumna;
			var aData;
			var aTrs = oTableLocal.fnGetNodes();
			for (i=1 ; i<=aTrs.length ; i++ ){
				mes=parseInt($("#mesDisponible").val(),10);
				if(mes==0)
					mes=mes+1;
			    aData = oTableLocal.fnGetData(i-1);
			    nColumna=aData.length-mes-1;//dos columnas al final ocultas
			    for(j=2; j<nColumna; j++){
			    	nomMes='mes'+mes;
			    	descMes='mes'+mes+'-'+i;
			    	$("#dt_preCompromiso").children().children()[i].children[j].innerHTML = '<input class="form-control" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
			   		//$("#dt_preCompromiso").children().children()[i].children[j].innerHTML = '<input style="width: 100%" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
			    	mes++;	
			    }
		    }
		}
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '-0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true 
		}
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val
		}	
		function Sinfrmt( fld )	{
		   	var valcol = fld.value ;
		   	var vcompr = $("#mComprometido").val();
		   	vcompr = quitaFmt( vcompr );
		   	valcol = quitaFmt( valcol );
			$("#" + fld.id).val( valcol );
		   	fld.select();
			$("#mComprometido").val( parseFloat( vcompr ) - parseFloat( valcol ) );
			$("#mComprometido").formatCurrency();
		}
		function cambiafrmt( fld ){
		   	var vcompr = $("#mComprometido").val();
		   	var vfld = $("#" + fld.id).val()
		   	if (vfld == "")
		   		vfld = '0';
			vcompr = quitaFmt( vcompr );
			vfld=quitaFmt(vfld);
		   	$("#mComprometido").val( parseFloat(vcompr) + parseFloat( vfld ) );
			$("#" + fld.id).formatCurrency();
			$("#mComprometido").formatCurrency();
		}
		function valSufic( fld ) { 
			var valor = $("#" + fld.id).val();
	
			valor=quitaFmt(valor);
			//validacion para que solo se permitan números
			if(isNaN(valor)){
				swal("Ingrese solo valores numéricos.",{icon:"warning",button: "Cerrar"});
				$("#" + fld.id).val( "0" );
				return false;
			}
			if(valor<0){
				swal("No se pueden ingresar valores negativos.",{icon:"warning",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
			}
			//obtiene la suficiencia de la EP y el valor del precompromiso ingresado para comparar
			var oTableLocal = $('#dt_suficiencia').dataTable();
			var vimptot = $("#totalNeto").val();
			var vcompr  = $("#mComprometido").val();
			vimptot = quitaFmt( vimptot );
			vcompr = quitaFmt( vcompr );
			vcompT = parseFloat(vimptot)-parseFloat(vcompr);
			var nren = parseInt(fld.id.substring(fld.id.lastIndexOf("-") + 1),10) -1;
			var ncol = parseInt( fld.id.substring(5, 3),10 ) + 1 ;
			var aData = oTableLocal.fnGetData( nren );
			if (valor == "") {
				valor = '0';
				$("#" + fld.id).val( "0" );
			}
			var valsuf = parseFloat( aData[ ncol ] );
			if (parseFloat(valor) > valsuf) {
		    	swal("NO hay Suficicencia Mensual en la Clave Presupuestal.",{icon:"warning",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompr=parseFloat(vcompr);
			valor=parseFloat(valor);
			vimptot=parseFloat(vimptot);
			if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
		    	swal("El Compromiso Actual Excede al Saldo Compromiso.",{icon:"warning",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompT = vimptot-valor-vcompr;
			//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
			vcompT=vcompT.toFixed(2);
			$("#difPrecompromiso").val(parseFloat(vcompT));
			$("#difPrecompromiso").formatCurrency();
		}
		function cargaSuficiencias(){
			var oTable=$('#dt_suficiencia').dataTable( {
			bPaginate: false,
   			bLengthChange: false,
   			bFilter: false,
   			bInfo: false,
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
			}},
   			bAutoWidth: true,
			sScrollX: 100,
			sScrollY: 100,
	        bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,
			//Query de Financiero, también se utiliza para Compromiso
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleContratoEP&qw=cIdContrato='" + vcontrato 
			+ "' and cuentaDisp='"+ + $("#cuentaDisponible").val()+"'" ,
			aoColumns: [
				{ sName: "ClaveSIAFF" ,bSortable: false},
				{ sName: "ClaveInterna",bSortable: false },
				{ sName: "MontoEnero" ,bSortable: false},
				{ sName: "MontoFebrero" ,bSortable: false},
				{ sName: "MontoMarzo" ,bSortable: false},
				{ sName: "MontoAbril",bSortable: false },
				{ sName: "MontoMayo" ,bSortable: false},
				{ sName: "MontoJunio",bSortable: false },
				{ sName: "MontoJulio" ,bSortable: false},
				{ sName: "MontoAgosto" ,bSortable: false},
				{ sName: "MontoSeptiembre" ,bSortable: false},
				{ sName: "MontoOctubre",bSortable: false },
				{ sName: "MontoNoviembre" ,bSortable: false},
				{ sName: "MontoDiciembre" ,bSortable: false},
				{ sName: "MontoAnual",bSortable: false },
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false } ]
			
            });
		}
		function precomprometer(){
			var  imgAprobar='<%=imgAprobar%>';
			if (imgAprobar==0) {
				var precomprometer=quitaFmt($("#mComprometido").val());
				
				if(parseFloat($("#totalNeto").val())!= parseFloat(precomprometer)){
					swal("El importe total del contrato es diferente al monto calendarizado.",{icon:"warning",button: "Cerrar"});
					return -1;
				}
				$("#imgAprobarpreCompromisoContCap4").hide();
				//crea el array de la tabla de montos
				var arregloDatos=ArrayTabla();
				
				//ajax Precomprometer en java
				$.ajax({url: '../../servlet/ContratoCap4Servlet'
					, type:'post' , async: false,data:'operacion=5&tablaDatos='+arregloDatos+'&cEjercicio='+$("#cEjercicio").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
					+'&ntipoPago='+$("#esDescentralizado").val()+'&descripPoliza=PRECOMPROMISO DE CONTRATOS CAPITULO 4000'
					+'&nComprometeMax='+$("#nComprometeMax").val()
					, dataType: 'json',
	        		mimeType: 'application/json', success: 
					function(j){
						var mensaje=j[0].MENSAJE;
						var resp=j[0].RESPUESTA;
						swal({
							title: "",
							text: mensaje,
							icon: "info",
							buttons: {
								confirm : "Cerrar"
							},
						}).then((continuar) => {
							if(resp){
								window.location = "ContratoCap4.jsp?tab=5";
							}
						});
					}
				});
			}else{
				swal("No tienes permisos.",{icon:"warning",button: "Cerrar"});
			}
		}
		function ArrayTabla(){
			var arregloTmp=new Array();
			var arrayFila=new Object();
			var aTrs = $('#dt_preCompromiso').dataTable().fnGetNodes();
			var vimporteP;
			var nTr;
			var jqInputs;
			for ( var i=0 ; i<aTrs.length; i++ )     
			{
				nTr =  $('#dt_preCompromiso').dataTable().fnGetData(aTrs[i]);
				jqInputs = $('input',aTrs[i] );
				for ( j=0 ; j < jqInputs.length ; j++ ) {
					var k=j;
					vimporteP = jqInputs[j].value ;
					vimporteP = quitaFmt(vimporteP);
					arrayFila=[nTr[0]+'.'+nTr[1],k+1,vimporteP,"|"];
					arregloTmp.push(arrayFila);
				}
			}
			
			return arregloTmp;
		}
		function cargaContratoPrecomprometido() {
		 	if ($("#esDescentralizado").val() == '0') {
		 		var qw= "'"+ vcontrato + "'"+"," + "''" ;
		 
				var oTable=$("input").attr("readonly", true); 
	 				$('#dt_preCompromiso').dataTable( {
					bPaginate: false,
	       			bLengthChange: false,
	       			bFilter: false,
	       			bInfo: false,
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
					bAutoWidth: true,
					sScrollX: 100,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromiso( " + qw +" )",
					aoColumns: [
						{ sName: "ClaveSIAFF",bSortable: false },
						{ sName: "ClaveInterna",bSortable: false },
						{ sName: "compromiso01",bSortable: false },
						{ sName: "compromiso02",bSortable: false },
						{ sName: "compromiso03",bSortable: false },
						{ sName: "compromiso04",bSortable: false },
						{ sName: "compromiso05",bSortable: false },
						{ sName: "compromiso06",bSortable: false },
						{ sName: "compromiso07",bSortable: false },
						{ sName: "compromiso08",bSortable: false },
						{ sName: "compromiso09",bSortable: false },
						{ sName: "compromiso10",bSortable: false },
						{ sName: "compromiso11",bSortable: false },
						{ sName: "compromiso12",bSortable: false },
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
						{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }]
				} ) ;
	
		 }
		 else{
			 var qw= "'"+ vcontrato + "'";
			 var oTable=$("input").attr("readonly", true); 
	 				$('#dt_preCompromiso').dataTable( {
					bPaginate: false,
	       			bLengthChange: false,
	       			bFilter: false,
	       			bInfo: false,
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
					bAutoWidth: true,
					sScrollX: 100,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoDes(" +qw+")",
					aoColumns: [
						{ sName: "ClaveSIAFF",bSortable: false },
						{ sName: "ClaveInterna",bSortable: false },
						{ sName: "compromiso01",bSortable: false },
						{ sName: "compromiso02",bSortable: false },
						{ sName: "compromiso03",bSortable: false },
						{ sName: "compromiso04",bSortable: false },
						{ sName: "compromiso05",bSortable: false },
						{ sName: "compromiso06",bSortable: false },
						{ sName: "compromiso07",bSortable: false },
						{ sName: "compromiso08",bSortable: false },
						{ sName: "compromiso09",bSortable: false },
						{ sName: "compromiso10",bSortable: false },
						{ sName: "compromiso11",bSortable: false },
						{ sName: "compromiso12",bSortable: false },
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
						{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }]
				} ) ;
			}
	
		}
		function devuelvePrecompromiso(){
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver==0){
				if(parseInt($("#nIdEstado").val(),10)==3){
					swal({
						title: "",
						text: "¿Está seguro que quiere eliminar el pre-compromiso?",
						icon: "info",
						buttons: {
							confirm : "Aceptar",
							cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
						}else{
							$("#imgDevolverpreCompromisoContCap4").hide();
							$("#spanDevolver").hide();
							$.ajax({url: '../../servlet/ContratoCap4Servlet'
								, type:'post' , async: false,data:'operacion=6&cEjercicio='+$("#cEjercicio").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
								+'&ntipoPago='+$("#esDescentralizado").val()
								, dataType: 'json',
				        		mimeType: 'application/json', success: 
								function(j){
									var mensaje=j[0].MENSAJE;
									var resp=j[0].RESPUESTA;
									swal({
										title: "",
										text: mensaje,
										icon: "info",
										buttons: {
											confirm : "Cerrar"
										},
									}).then((continuar) => {
										if(resp){
											window.location = "ContratoCap4.jsp?tab=5";
										}
									});
									
								}
							});
						}
					});
				}else{
					swal("El Contrato no se puede devolver.",{icon:"warning",button: "Cerrar"});
				}
			
			}else{
				swal("No tiene permisos.",{icon:"warning",button: "Cerrar"});
			}
		}
		function comprometer(){
			var  imgAprobar='<%=imgAprobar%>';
			if (imgAprobar==0   && (roles.indexOf("JEFES") >= 0)|| roles.indexOf("ADMIN_RECMAT") >= 0 ) {
				
				$("#imgAprobarCompromisoContCap4").hide();
				
				//ajax comprometer en java
				$.ajax({url: '../../servlet/ContratoCap4Servlet'
					, type:'post' , async: false,data:'operacion=7&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
					+'&ntipoPago='+$("#esDescentralizado").val()+'&cCuentaDisponible='+$("#cuentaDisponible").val()
					, dataType: 'json',
	        		mimeType: 'application/json', success: 
					function(j){
						var mensaje=j[0].MENSAJE;
						var resp=j[0].RESPUESTA;
						swal({
							title: "",
							text: mensaje,
							icon: "info",
							buttons: {
								confirm : "Cerrar"
							},
						}).then((continuar) => {
							if(resp){
								window.location = "ContratoCap4.jsp?tab=5";
							}
						});
					}
				});
			}else{
				swal("No tienes permisos.",{icon:"warning",button: "Cerrar"});
			}
		}
		function modificaMontoContrato(){
			$("#totalNeto").val($("#mMontoActualContrato").val());
			if($("#isAbierto").val()==1 && $("#flexCheckMax").is(':checked') ){
				$("#totalNeto").val($("#totalNetoMax").val());
				$("#nComprometeMax").val(1);
			}
		}
	</script>
  </head>
  <body>
  	<form id="formPresupContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarpreCompromisoContCap4" name="imgAprobarpreCompromisoContCap4" 	value="Pre-Comprometer"	onclick="precomprometer();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarCompromisoContCap4" name="imgAprobarCompromisoContCap4" 	value="Autorizar"	onclick="comprometer();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverpreCompromisoContCap4" name="imgDevolverpreCompromisoContCap4" 	value="Devolver"	onclick="devuelvePrecompromiso();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoCap4.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cEstado" id="cEstado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalIVA" id="lblTotalIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNeto" id="lblTotalNeto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNetoMax" id="lblTotalNetoMax"  readonly/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Capturar Pre-Compromiso</legend>
			<div class="form-group" id="trinputsMontos">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<div class="form-check">
							  <input class="form-check-input" type="checkbox" value="" id="flexCheckMax" onclick="modificaMontoContrato()">
							  <label class="form-check-label" for="flexCheckMax">
							    Se Requiere Comprometer el M&aacute;ximo
							  </label>
							</div>
						</div>
					</div>
					<div class="input-group">
						<div class="col">
							<label for="mImporteTotal">Monto del Contrato:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="20"  aria-describedby="basic-addon1"  id="totalNeto" name="totalNeto" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Compromiso Actual:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="mComprometido" name="mPreComprometer" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Saldo Compromiso:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="difPrecompromiso" name="mPreComprometido" value="0">
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row"  >
					<div class="col-4">
						<h1 style="color: blue">Tabla del Precompromiso</h1>
					</div>
				</div>
				<div class="row" id="trEditarpreCompromisoPlurContrato" >
					<div class="col-4">
						<input class="btnInterfaceBG ui-button ui-corner-all" type="button" name="edit" id="edit" value="Editar" onclick="editCeldasDetalle()">
					</div>
				</div>
				<div class="row" id="tr_dt_preCompromiso">
					<div class="input-group">
						<div class="col">
							<table id="dt_preCompromiso" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th>Estructura Program&aacute;tica</th>
										<th>Clave Interna</th>
										<th>Enero</th>
										<th>Febrero</th>
										<th>Marzo</th>
										<th>Abril</th>
										<th>Mayo</th>
										<th>Junio</th>
										<th>Julio</th>
										<th>Agosto</th>
										<th>Septiembre</th>
										<th>Octubre</th>
										<th>Noviembre</th>
										<th>Diciembre</th>
										<th>Contrato</th>
										<th>EP</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row"  >
					<div class="col-4">
						<h1 style="color: blue">Tabla del Disponible</h1>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_suficiencia" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th>Estructura Program&aacute;tica</th>
										<th>Clave Interna</th>
										<th>Enero</th>
										<th>Febrero</th>
										<th>Marzo</th>
										<th>Abril</th>
										<th>Mayo</th>
										<th>Junio</th>
										<th>Julio</th>
										<th>Agosto</th>
										<th>Septiembre</th>
										<th>Octubre</th>
										<th>Noviembre</th>
										<th>Diciembre</th>
										<th>Anual</th>
										<th>Contrato</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="cIdContratoDefinitivo" name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		<input type="hidden" id="subtotal" name="subtotal" value="" />
		<input type="hidden" id="totalIVA" name="totalIVA" value="" />
		<input type="hidden" id="totalNetoMax" name="totalNetoMax" value="" />
		<input type="hidden" name="nIdEstado" id="nIdEstado" value=""/>
		<input type="hidden" name="mesDisponible" id="mesDisponible" value="1"/>
		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106"/>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value=""/>
		<input type="hidden" id="esDescentralizado" name="esDescentralizado" value="" />
		<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
		<input type="hidden" id="mMontoActualContrato" name="mMontoActualContrato" value="0" />
		<input type="hidden" id="nComprometeMax" name="nComprometeMax" value="0" />
		
  	</form>
  </body>
</html>
