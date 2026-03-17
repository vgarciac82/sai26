<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>

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
		session.setAttribute(GestionInterface.ATT_ContratArt25Definitivo, cIdContratoDefinitivo);
	}else{
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_ContratArt25Definitivo);
	}
	//System.out.print(cIdContratoDefinitivo);
%>
<!DOCTYPE html>
<html>
<head>
<title>Precompromiso</title>
	<script type="text/javascript" charset="utf-8">
		var roles;
		tabb=4;
		$(document).ready(function() {
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
			    int editar=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"ContratosArt25","PresupuestoContratoArt25");
				Iterator btn = botones.entrySet().iterator();
				String img=null;
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					img=(String) b.getValue();
					if ("trEditarpreCompromisoContratoArt25".equals(img)){
						editar=1; 
					}
				}
			%>
			roles="<%=roles%>";
			$("#tipoOperacion").val(2);
			$("#cIdContratoDefinitivo").val("<%=cIdContratoDefinitivo%>");
			consultaDatosMigrados();
			showHideButtons();
			initTables();
			cargaSuficiencias();
			cargaPrecompromiso();
			clickHandlers();
		});//fin del document ready
		function cargaPrecompromiso(){
			if(parseInt($("#nIdEstatus").val(),10)>2){
				cargaContratoPrecomprometido();
			}else{
				cargaPreCompromisoVacio();
			}
		}
		function showHideButtons(){
			$("#imgAprobarpreCompromisoContrato").css("display","none");
			$("#imgAprobarCompromisoContrato").css("display","none");
			$("#imgDevolverpreCompromisoContrato").css("display","none");
			if ((roles.toString().indexOf("ADMIN_RECMAT") >= 0)
					|| (roles.toString().indexOf("JEFES") >= 0)
			) {
				if(parseInt($("#nIdEstatus").val(),10)>2){
					$("#imgAprobarpreCompromisoContrato").css("display","none");
					$("#imgDevolverpreCompromisoContrato").css("display","");
					if(parseInt($("#nIdEstatus").val(),10)<4){
						$("#imgAprobarCompromisoContrato").css("display","");
					}else{
						$("#imgDevolverpreCompromisoContrato").css("display","none");
					}
				}else{
					if(parseInt($("#nIdEstatus").val(),10)<2){
						$("#imgAprobarpreCompromisoContrato").css("display","none");
					}else{
						$("#imgAprobarpreCompromisoContrato").css("display","");
					}
				}
			}else if((roles.toString().indexOf("ANALISTA") >= 0)){
				if(parseInt($("#nIdEstatus").val(),10)==3){
					$("#imgDevolverpreCompromisoContrato").css("display","");
				}
				if(parseInt($("#nIdEstatus").val(),10)==2){
					$("#imgAprobarpreCompromisoContrato").css("display","");
				}
			}
		}
		function initTables(){
			$('#dt_preCompromiso').dataTable(
				{
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
			$('#dt_suficiencia').dataTable(
				{
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleContratoEP&qw=cIdContrato='" + $("#cIdContratoDefinitivo").val() 
				+ "' and cuentaDisp='82106'" ,
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
					{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false } ],
				fnInitComplete: function(oSettings, json) {
					 //Se encuentra En SAI con un contrato diverso y esta vigente
					if($("#nIdEstatus").val() ==2 ){
						var editar='<%=editar%>';
						if (editar==0){
							document.getElementById("trEditarpreCompromisoContratoArt25").style.display="table-row";
						}
					}
				}	
	            });
		}
		function cargaPreCompromisoVacio(){
			var oTable=$('#dt_preCompromiso').dataTable( {
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
				//Carga el calendario con valores de 0
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + $("#cIdContratoDefinitivo").val() + "'",
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
		function cargaContratoPrecomprometido() {
			var qw="'"+$("#cIdContratoDefinitivo").val()+"'";
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
		function clickHandlers(){
			$('#edit').click( function () {
		       	var nRow = editPrecompromiso( $('#dt_preCompromiso').dataTable() ) ; 
		   	} );
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
			   		$("#dt_preCompromiso").children().children()[i].children[j].innerHTML = '<input style="width: 100%" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
			    	mes++;	
			    }
		    }
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
				swal("Ingrese solo valores num\u00e9ricos",{icon:"success",button: "Cerrar"});
				$("#" + fld.id).val( "0" );
				return false;
			}
			if(valor<0){
				swal("No se pueden ingresar valores negativos",{icon:"success",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
			}
			//obtiene la suficiencia de la EP y el valor del precompromiso ingresado para comparar
			var oTableLocal = $('#dt_suficiencia').dataTable();
			var vimptot = $("#mImporteTotal").val();
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
		    	swal("NO hay Suficicencia Mensual en la Clave Presupuestal",{icon:"success",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompr=parseFloat(vcompr);
			valor=parseFloat(valor);
			vimptot=parseFloat(vimptot);
			if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
		    	swal("El Compromiso Actual Excede al Saldo Compromiso",{icon:"success",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompT = vimptot-valor-vcompr;
			//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
			vcompT=vcompT.toFixed(2);
			$("#difPrecompromiso").val(parseFloat(vcompT));
			$("#difPrecompromiso").formatCurrency();
		}
		function precomprometer(){
			if(quitaFmt($("#mImporteTotal").val())!= quitaFmt($("#mComprometido").val())){
				swal("El monto del contrato es diferente al monto calendarizado en las EPS.\nNo puedes calendarizar mas, ni menos del valor del contrato.",{icon:"warning",button: "Cerrar"});
				return -1;
			}
			document.getElementById("imgAprobarpreCompromisoContrato").disabled = true;
			$("#tipoOperacion").val(6);
			var arregloDatos=arrayTablaPrecom("dt_preCompromiso");
			execAjaxPrecom(arregloDatos);
		}
		function devuelvePrecompromiso(){
			$("#tipoOperacion").val(7);
			document.getElementById("imgDevolverpreCompromisoContrato").disabled = true;
			swal({
				title: "",
				text: "¿Está seguro que quiere cancelar el pre-compromiso?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					document.getElementById("imgDevolverpreCompromisoContrato").disabled = false;
					return;
				}else{
					execAjaxPrecom("");		
				}
			});
			
		}
		function execAjaxPrecom(arregloDatos){
			$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
				,data:'tipoProceso='+$("#tipoProceso").val()+'&tipoOperacion='+$("#tipoOperacion").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()+'&tablaDatos='+arregloDatos
				,dataType: 'json', success: 
					function(j){
						swal(j[0].MENSAJE,{icon:"info",button: "Cerrar"});
						vaciarJsonAInputs(j[0].datosContratoArt25);
						cargaSuficiencias();
						cargaPrecompromiso();
						showHideButtons();
						
						$.unblockUI();
				}, error: function() {
					$.unblockUI();
				}
			});
		}
		function comprometer(){
			if (!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("JEFES") >= 0)) {
				swal("No tienes permiso para ejecutar está acción.",{icon:"warning",button: "Cerrar"});
				$("#imgAprobarCompromisoPluriContrato").css("display","none");
				return;
			}
			if(parseInt($("#nIdEstatus").val(),10)==3){
				swal({
					title: "",
					text: "¿Está seguro que quiere autorizar el contrato?",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						$("#tipoOperacion").val(8);
						$("#imgAprobarCompromisoContrato").css("display","none");
						execAjaxPrecom("");
					}
				});
			}
		}
	</script>
</head>
<body>
	<form id="formPrecompromisoContArt25">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarpreCompromisoContrato" name="imgAprobarpreCompromisoContrato" 	value="Pre-Comprometer"	onclick="precomprometer();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarCompromisoContrato" name="imgAprobarCompromisoContrato" 	value="Autoriza Compromiso"	onclick="comprometer();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverpreCompromisoContrato" name="imgDevolverpreCompromisoContrato" 	value="Devolver"	onclick="devuelvePrecompromiso();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'Plurianualidad.jsp?tab=1';" />
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
						<input type="text" class="form-control  transpInput" name="lblTipoContrato" id="lblTipoContrato"  readonly/>
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
				<div class="row" id="divTotalMax">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNetoMax" id="lblTotalNetoMax"  readonly/>
					</div>
				</div>
				
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Montos a precomprometer del contrato</legend>
			<div class="form-group" id="trinputsMontos">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<label for="mImporteTotal">Monto de Contrato:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="20"  aria-describedby="basic-addon1"  id="mImporteTotal" name="mImporteTotal" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer" id="lbPrecomprometer">PreCompromiso Actual:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="mComprometido" name="mComprometido" value="0">
						</div>
						<div class="col">
							<label for="difPrecompromiso">Monto Faltante:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="difPrecompromiso" name="difPrecompromiso" value="0">
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" id="trEditarpreCompromisoContratoArt25" style="display: none">
					<div class="col-4">
						<input class="btnInterfaceBG ui-button ui-corner-all" type="button" name="edit" id="edit" value="Editar">
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
				<div class="row">
					<div class="input-group">
						<div class="col">
							<br /><br />
							<h1 style="color: blue">Presupuesto disponible</h2>
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
		<input type="hidden" name="mesDisponible" id="mesDisponible" value="1"/>
  	</form>
</body>
</html>