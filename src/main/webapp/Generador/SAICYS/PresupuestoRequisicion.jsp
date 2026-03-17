<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*"%>
<%
	Usuario usuarioTab = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user = usuarioTab.getLogin();
	String idRol = "0";
	Map rol = usuarioTab.getRoles();
	String cEjercicio = "";
	String cIdTipoSolicitud = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String cIdSolicitud="";
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) {
		cEjercicio = 			(String) session.getAttribute(GestionInterface.ATT_ReqEjercicio);
		cIdTipoSolicitud = 		(String) session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
		cIdUnidadEjecutora = 	(String) session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
		nIdConsecutivo = 		(String) session.getAttribute(GestionInterface.ATT_ReqConsecutivo);
	} else
		response.sendRedirect("Requisiciones.jsp?tab=0");
	
	//Para que llene el hidden con mesActual y pueda desaparecer columnas de meses anteriores
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	int mesActual=c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuarioTab.getU_UR();
	cIdSolicitud=cIdTipoSolicitud+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo;
	//int imgAprobar = 0;
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Presupuesto</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		
		<script type="text/javascript" charset="utf-8">
			<%
				String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Requisiciones","PresupuestoRequisicion");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					
				}

			%>
			$("#tbs").val(5);
			//oTable: OBJ datatable de EP
			var epDT;
			
			//oTableLineas: OBJ datatable de LineA
			var lineasDT; 
			
			
			//oCurrentFocus
			var oCurrentFocus;
			
			
			//Valores con los iniciales de las partidas utilizadas
			var Pp, Pp2, Pp3;
			
			
			//Numero de columnas antes de meses en DTlineas
			var columnasPrinc = 5;
			
			
			//Desfase meses antes del actual que se muestran
			var desfase;
			
			
			//Mes actual
			var mesDisp;
			
			
			//Agarraderas del header de tabla lineas y al Row con los saldos
			var dtLineasHeader, dtSaldosRow;
			
			
			//Array para mapear el nombre del mes con su indice 0 - 11
			var meses = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'];
			
			
			//Variable que contiene el valor del ultimo input que lanzó el evento focus
			var lastValue;
			
			
			var timeouts = [];
			var roles="<%=roles%>";
			$(document).ready(function() {
				$("#tbs").val(5);
				showHideTabs();
				$(".tabs").tabs({
					select: function(event, ui){
						var changed = false;
						var old, nw;
					}
				});
				//Prepara campos de encabezado y los carga, tambien carga hiddens
				setReadOnlyConfigLbls();
				
				initQueries();
				
				
				//Guarda la conf de desfase y mesActual en una var global
				desfase = parseInt($("#GP_VALOR").val(),10);
				
				mesDisp = parseInt($("#mesDisponible").val(),10);
				
				
				//Carga valores de Pps
				Pp = $('#Partida').val();
				
				Pp2 = $('#Partida2').val();
				
				Pp3 = $('#Partida3').val();
				
				
				//Inicializa DataTables
				initDTSaldos();
				
				epDT = initDTEPs("");
				lineasDT = initDTLineas();
				
				//Puntero al header de tabla lineas
				//dtLineasHeader = $("#tblLineas_wrapper").find("tr");
				
       			//Desaparece las columnas de los meses que ya pasaron en oTableLineas
				deshabilitaMeses(oTableLineas);
       			
				
				//Listener para campos de filtro en tabla EP
				for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) {
					//Si ya hay un timeout, se cancela
					if (timeouts[i])
						clearInterval(timeouts[i]);
					//Nuevo timeout
					timeouts[i] = setTimeout(function(){
						oCurrentFocus = this; 
						fnFilterColumn(evt);
						$("#pbAceptar").css("visibility","hidden");
						$('#tblSaldos').dataTable().fnClearTable();
						//document.getElementById("col"+i+"_filter").focus();
					}, 500);
					
				});
				epDTclick();
				//lineasclick();
				//Desactiva de entrada el btnGuardar y le pone su listener
				document.getElementById('btnGuardar').disabled = true;
				
				$("#btnGuardar").click( btnGuardarClickHanlder );

				
				$("input.input_monto").live("focusout", inputMontoLiveFocusoutHandler);
				
				$("input.input_monto").live("keyup", inputMontoLiveKeyupHandler);
				
				$("input.input_monto").live("focus", inputMontoLiveFocusHandler);
				
				//Oculta el select que se usa para cachar datos de una consulta de saldos en ...
				$("#tmpGastos").css("visibility", "hidden");
				//Para Fecha de vencimiento
				cambiaColorFecha();
				
				/*
				 * Crea el area de captura de EP.
				 */
				$("#dialog-form-ep")
					.dialog(
							{
						autoOpen : false,
						height : 530,
						width : 650,
						modal : true,
						buttons : {
							"Agregar" : function() {
								agregaLinea();
							},
							Cancel : function() {
								$(this).dialog("close");
								
							}
						},
						close : function() {
							initDTLineas();
						}
					});	
		//RADICADO
			queryFormPost("readBanderaRadicado", { async:false });
			if($("#BanderaRadicado").val()=="S"){
				document.getElementById('checkDispRadicado').disabled = false;
			}else{
				document.getElementById('checkDispRadicado').disabled = true;
			}
			desabilitaCargaLAyout();
		});//Termina el docuement
		
		
		function agregaLinea(){
				
				$("#ExisteLineaAp").val("0");
				
				queryFormPost({
					queryName:"readExisteLineaAp",
					async:false,
					callback:function(){
						$("#m01").val(parseFloat(unFormatCurrency($("#eneroApart").val())));
						$("#m02").val(parseFloat(unFormatCurrency($("#febreroApart").val())));
						$("#m03").val(parseFloat(unFormatCurrency($("#marzoApart").val())));
						$("#m04").val(parseFloat(unFormatCurrency($("#abrilApart").val())));
						$("#m05").val(parseFloat(unFormatCurrency($("#mayoApart").val())));
						$("#m06").val(parseFloat(unFormatCurrency($("#junioApart").val())));
						$("#m07").val(parseFloat(unFormatCurrency($("#julioApart").val())));
						$("#m08").val(parseFloat(unFormatCurrency($("#agostoApart").val())));
						$("#m09").val(parseFloat(unFormatCurrency($("#septiembreApart").val())));
						$("#m10").val(parseFloat(unFormatCurrency($("#octubreApart").val())));
						$("#m11").val(parseFloat(unFormatCurrency($("#noviembreApart").val())));
						$("#m12").val(parseFloat(unFormatCurrency($("#diciembreApart").val())));					
						var qName = "";
						if($("#ExisteLineaAp").val()!="0" ) 
							qName = "deleteSolLineaAp,createSolLineasApartado";
						else
							qName = "createSolLineasApartado";
						
						queryFormPost({
							queryName : qName,
							async: false,
							callback:function(){
								swal("Línea insertada exitosamente.",{icon:"info",button: "Cerrar"});
								//Bitácora
								$("#cAccion").val("CALENDARIZA_PRESUPUESTO");
								//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
								$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
								queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
								queryFormPost("mSolicitudUpdateCuaentaDisp", { async : false});
								//document.getElementById('checkDispRadicado').disabled = true;
								$("#dialog-form-ep").dialog("close");
							}
						});
					}
				});
		}
			function setReadOnlyConfigLbls(){
				document.getElementById("lblUnidadEjecutora").style.readonly=true;
				document.getElementById("lblRequisicion").style.readonly=true;
				document.getElementById("lblEstado").style.readonly=true;
				document.getElementById("lblDescripcion").style.readonly=true;
				document.getElementById("lblPartida").style.readonly=true;
				document.getElementById("lblMesRequisicion").style.readonly=true;
			}
			
			function initQueries(){	
				queryFormPost("cg_roleRead", {async: false});
				//Encabezado y algunos hiddens
				queryFormPost("mSolicitud_LabelRead", { async:false });
				//Montos en encabezado
				queryFormPost("mSolicitudLineas_MontosRead", { async:false });
				//Lee la configuración de mes (desfase)
				queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
				queryFormPost("mSolicitudDescripcionUnidad", {async: false});
				
			    if( (roles.indexOf("ADMIN_RECMAT") >=0) ||($("#cIdCapitulo").val()=="1" ) ){
			       	$("#isAdmin").val(0);
			     }
				queryFormPost("tCatalogoUnidadEjecutoraReadVistasCadena", { async:false });
				desabilitaRadicado();
			}
			function activacheck(){
				$("#checkDispRadicado").attr("checked",false);
				if($("#cuentaDisponible").val()=='82109'){
					$("#checkDispRadicado").attr("checked",true);
				}
			}
			function initDTSaldos(){	
				oTableSaldos=$("#tblSaldos").dataTable({
					bAutoWidth : true,
					bPaginate : false,
					bLengthChange : false,
					bInfo : false,
					sScrollX: "100%",
					bScrollCollapse: true,
					bJQueryUI: true,
					bFilter : false,
					bSort : false,
					bInfo : false,
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
						oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }},
					aoColumns: [
						{ sName: "MontoEnero",		bSortable: false },
						{ sName: "MontoFebrero",	bSortable: false },
						{ sName: "MontoMarzo",		bSortable: false },
						{ sName: "MontoAbril",		bSortable: false },
						{ sName: "MontoMayo",		bSortable: false },
						{ sName: "MontoJunio",		bSortable: false },
						{ sName: "MontoJulio",		bSortable: false },
						{ sName: "MontoAgosto",		bSortable: false },
						{ sName: "MontoSeptiembre",		bSortable: false },
						{ sName: "MontoOctubre",		bSortable: false },
						{ sName: "MontoNoviembre",		bSortable: false },
						{ sName: "MontoDiciembre",		bSortable: false },
						{ sName: "MontoAnual",	bSortable: false }
						]
       			});
       		}
       		function initDTEPs(w){
			    //para filtrar dependiendo de la unidad ejecutora	
				$('#usuarioUEReport').val('<%=usuarioTab.getU_UR()%>');
				var where=" 1=1 "+ w;	
				return oTableEP= $("#tblEP").dataTable({
					bDestroy: true,
					fnDrawCallback: function() {
						$(oCurrentFocus).focus(function() {
							if (this.createTextRange) {
								var r = this.createTextRange();
								r.collapse(false);
								r.select();
								
							}
							this.focus();
						});
						$(oCurrentFocus).focus();
					},
					bAutoWidth : true,
					sScrollX: "100%",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_EpsConMontoAnualMod('"+$('#cIdSubPartida').val()
					+"','"+$('#isAdmin').val()+"','"+$('#cIdUnidadEjecutoraUsuario').val()
 					+"','"+$('#U_LOGIN').val()+"','"+$('#modulo').val()+"','"+$('#cuentaDisponible').val()
					+"')&qw="+where,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
						{ sName: "EjercicioFiscal"},
						{ sName: "Ramo"},
						{ sName: "UnidadResponsable"},
						{ sName: "GrupoFuncional"},
						{ sName: "Funcion"},
						{ sName: "SubFuncion"},
						{ sName: "ProgramaGeneral"},
						{ sName: "ActividadInstitucional",},
						{ sName: "ProgramaPresupuestario"},
						{ sName: "Partida"},
						{ sName: "TipoGasto"},
						{ sName: "FuenteFinanciamiento"},
						{ sName: "EntidadFederativa"},
						{ sName: "Cartera"},
						{ sName: "UnidadEjecutora"},
						{ sName: "UnidadNorativa"}
					]
	        	});
       		}
       		function initDTLineas() {
				return oTableLineas = $("#tblLineas").dataTable({
					sScrollX: "100%",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + 
						"/" + window.location.pathname.split("/")[1] + 
						"/crud?rt=t&ql=fn_mLineasApartadoSolicitud('" + $("#cIdSolicitud").val() + "','" + $("#cEjercicio").val()+ "','" + $("#claveEP").val()
							+ "')",    
					bProcessing: true,
					bPaginate: true,
					bLengthChange: false,
					bFilter: false,
					bInfo: false,
					sPaginationType: "full_numbers",
					bAutoWidth: false,
					bServerSide: true,	
					bProcessing: true,
					bJQueryUI: true,	
					aaSorting: [[ 0, "asc" ]],
					aoColumns: [
						{ sName: "nIdLineaSolicitud" ,bSortable: false,"sWidth": "2%"},
						{ sName: "cIdCABM" ,bSortable: false,"sWidth": "5%"},
						{ sName: "cDescripcion" ,bSortable: false,"sWidth": "10%"},
						{ sName: "importeNeto" ,bSortable: false,"sWidth": "5%"},
						{ sName: "otrasEP" ,bSortable: false,"sWidth": "18%"},
						{ sName: "enero",bSortable: false ,"sWidth": "5%"},
						{ sName: "febrero",bSortable: false ,"sWidth": "5%"},
						{ sName: "marzo",bSortable: false ,"sWidth": "5%"},
						{ sName: "abril" ,bSortable: false,"sWidth": "5%"},
						{ sName: "mayo" ,bSortable: false,"sWidth": "5%"},
						{ sName: "junio" ,bSortable: false,"sWidth": "5%"},
						{ sName: "julio" ,bSortable: false,"sWidth": "5%"},
						{ sName: "agosto" ,bSortable: false,"sWidth": "5%"},
						{ sName: "septiembre" ,bSortable: false,"sWidth": "5%"},
						{ sName: "octubre" ,bSortable: false,"sWidth": "5%"},
						{ sName: "noviembre" ,bSortable: false,"sWidth": "5%"},
						{ sName: "diciembre" ,bSortable: false,"sWidth": "5%"},
						{ sName: "montoneto" ,bVisible: false}]
						
        		});
			}
			
			function deshabilitaMeses(oTableLocal){
				//desde {mesDiponible -1 (0-indexed)} 
				//hasta llegar a columnaLimite, ocultar.
				var n= columnasPrinc + mesDisp + desfase - 1;//(0-indexed)
				
				for(i = columnasPrinc; i < n; i++)
					oTableLocal.fnSetColumnVis(i, false);
				//oTableLocal.fnAdjustColumnSizing();
			}
			function epDTclick(){
				$('#tblEP tr').live('dblclick', function() { 
					$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTableEP );
					$('#tblSaldos').dataTable().fnClearTable();
					if (anSelected != "") {
						var aData = oTableEP.fnGetData(anSelected[0]);
						var sEP = 	aData[0]+"." +
									aData[1]+"." +
									aData[2]+"." +
									aData[3]+"." +
									aData[4]+"." +
									aData[5]+"." +
									aData[6]+"." +
									aData[7]+"." +
									aData[8]+"." +
									aData[9]+"." +
									aData[10]+"." +
									aData[11]+"." +
									aData[12]+"." +
									aData[13];
						var sCI = 	aData[14]+"." +aData[15];
						$("#nIdClaveEgresos").val(sEP);
						$("#ClaveInterna").val(sCI);
						sEP += "." + sCI;
						$("#claveEP").val(sEP);		
						//var szWhere = " EP = '" + sEP + "' ";
						var inWhere = " cEjercicio = '" + $("#cEjercicio").val() + "' " + 
							"AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "' " +
							"AND cIdSolicitud = '" + $("#cIdSolicitud").val() + "' " +
							"AND nIdClaveEgresos = '" + $("#nIdClaveEgresos").val() + "' " +
							"AND ClaveInterna = '" + $("#ClaveInterna").val() + "' ";
							
						var exWhere = " S.EP = '" + sEP + "' ";
						var nameTabla="VSALDOSANUALESSPANRADICADO";
						if($('#cuentaDisponible').val()=='82106'){
							nameTabla="VSALDOSANUALESSPAN";	
						}
								
						//Consulta los saldos y agrega una fila con dicha info en tblSaldos                                                                                      
						$.getJSON("../../catalogos/SelectJson.jsp",
							{Tabla: nameTabla, 
								Param: exWhere,
								SelectFunc: inWhere,
								MaxReg: 10, 
								ajax: 'false'
							}, 
							function(j, status){
								if(j.length==0){
									swal("No hay saldos para esta EP, solicita a financieros una adecuación liquida.",{icon:"info",button: "Cerrar"});
									return;
						   		}
								for (var i = 0; i < j.length; i++) {
									oTableSaldos=$('#tblSaldos').dataTable().fnAddData(
										[j[i].Col4,
										 j[i].Col5,
										 j[i].Col6,
										 j[i].Col7,
										 j[i].Col8,
										 j[i].Col9,
										 j[i].Col10,
										 j[i].Col11,
										 j[i].Col12,
										 (j[i].Col13),
										 (j[i].Col14),
										 (j[i].Col15),
										 (j[i].Col16) ]
									);
								}
						   					
								
							}
						);
						initDTLineas();
					}
					$(this).removeClass('row_selected');
				});
			}
			function editarMontos(){
				if($("#nIdEstadoSolicitud").val()==1){
					
				}
			}
			function btnGuardarClickHanlder(event){
				//Deshabilitar el botón
				document.getElementById('btnGuardar').disabled = true;
				
				//Se llenan los campos para la consulta, de los campos necesarios para el query sp_calendarizacionRequisicion,
				//Cargan junto con el encabezado: cEjercicio, cIdUnidadEjecutora, cIdSolicitud		      	
				//Cambian cada que se selecciona la EP: nIdClaveEgresos, ClaveInterna
				
				var mes = mesDisp + desfase;
				var sMes, mesReal, colMes;
				//Por cada renglon (tr) en oTableLineas
				
				
				$( oTableLineas.fnGetNodes() ).each(function(index){
					$("#nIdLineaSolicitud").val($(this).children().eq(0).html());
					$("#cIdCABM").val($(this).children().eq(1).html());
					$("#cDescripcion").val($(this).children().eq(2).html());
					
					//i = 10; 
					for (var i = mes; i <= 12; i ++){
						//mesxy para id de hidden. En xy la x puede ser un 0 si y es menor a 10: 01, 02 ... 09, 10, .. 12
						sMes = "mes" + (i < 10 ? "0": "") + i;
						//numero de columna en tabla DTLineas
						colMes = i - mes + columnasPrinc;
						
						$("#" + sMes).val($(this).children().eq(colMes).find("input:not(:hidden)").val());
						
					}
					queryFormPost('sp_calendarizacionRequisicion', {async: false });
				});
				
				//Finalmente actualiza los hiddens que guardan el valor almacenado en BD
				$('span.input_monto_editando').each(function(index) {
					$(this).find("input:hidden").val($(this).find("input:not(:hidden)").val());
				});
				swal("Datos guardados.",{icon:"info",button: "Cerrar"});
			}
			function inputMontoLiveFocusoutHandler(event){
				$(this).formatCurrency();
			}
			
			function inputMontoLiveFocusHandler(event){
				//TODO: Guardar valor actual
				lastValue = unFormatCurrency(this.value);
			}
			function inputMontoLiveKeyupHandler(){
				var old = $(this).siblings(":hidden").val();
				var tr = $(this).closest("tr");
				var index = $(this).closest("td").index();
				
				var thMes, sMes, trSaldo, i, gastado, neto, sumaGastado, exceso, asignado, otras, disponible, last;
				this.value = this.value.replace(/[^0-9\.]/g,'');
				//Si queda vacío, se le pone 0
				if($.trim(this.value).length == 0){
					$("#tmpMonto").val(0);
					$("#tmpMonto").formatCurrency();
					this.value = $("#tmpMonto").val();
				}
				if($.trim(this.value).length > 10){
					this.value=this.value.substring(0,10);
				}
				last = parseFloat(unFormatCurrency($.trim(lastValue)));


				//Si está cambiando el valor en el input
				if(old != this.value){
					//Se vuelve a habilitar el botón guardar
					document.getElementById('btnGuardar').disabled = false;
					//Cantidad en float que se está gastando en this input
					gastado = unFormatCurrency(this.value);
					//Obtiene suma de mes1 + mes2 ... = todo el $ gastado en esa linea, incluso this.value
					sumaGastado = 0.00;
					$(tr).find("span.input_monto_editando").each(function(index){
						sumaGastado =parseFloat(sumaGastado)+ parseFloat( ($(this).find("input.input_monto").val()).replace("$","").replace(/\,/g,'') );
					});
					otras  = unFormatCurrency( $(tr).children().eq(4).find("span.otrasEP").text() );					
					//Obtiene el header de la columna correspondiente, trim y a minusculas (e.g. 'octubre')
					thMes = $(dtLineasHeader).children().eq(index).find("div");
					sMes = $.trim($(thMes).contents().eq(0).text()).toLowerCase();
					//Busca el mes en array meses, obtiene el indice
					for(i = 0; i < meses.length; i++){
						if(meses[i] == sMes)
							break;
					}
					//Obtener saldo asignado(disponible) (e.g. $45.00 / $18.00. Obtiene $45) de tblMontos
					//Para ver si el saldo gastado en un mes no sobrepasa el presupuesto asignado de ese mes en la EP
					asignado = parseFloat( $(dtSaldosRow).children().eq(i).children("span.asignado").text() );//round(2)
					//asignado=asignado.replace("$","").replace(/\,/g,'');
					//Monto disponible en la ep
					disponible =  unFormatCurrency( $(dtSaldosRow).children().eq(i).children("span.gastado").text());
					
					//Obtiene monto neto de la linea
					neto = unFormatCurrency( $(tr).children().eq(3).find("span").text());
					//Si se está gastando mas dinero del disponible en el mes en la EP,
					//Avisa al usuario y corrige el monto inteligentemente
					var alertado = false;
					//gastado es el monto escrito
					//validación para cuando no tenemos saldo suficiente
					if((disponible + last) < gastado){
						alertado=true;
						sumaGastado =parseFloat(sumaGastado)-parseFloat(gastado);//0
						if((parseFloat(sumaGastado)+parseFloat(disponible)+parseFloat(last))>parseFloat(neto)){
							gastado=parseFloat(neto)-(parseFloat(sumaGastado)+parseFloat(otras));
						}else{
							gastado=(parseFloat(disponible)+parseFloat(last))-parseFloat(otras);
						}
						if(gastado<0 || (parseFloat(sumaGastado)+parseFloat(gastado)>parseFloat(neto)))
							alertado=false;
					}
					
					//Si se está gastando mas dinero del necesario por el monto neto de la linea,
					//Avisa al usuario y corrige el monto inteligentemente
					if((parseFloat(sumaGastado) + parseFloat(otras)) > parseFloat(neto) && !alertado){
						alertado=true;
						sumaGastado=parseFloat(sumaGastado)-parseFloat(gastado);
						if((parseFloat(sumaGastado)+parseFloat(gastado)+ parseFloat(otras))>parseFloat(neto)){
							gastado=parseFloat(neto)-(parseFloat(otras)+parseFloat(sumaGastado));
						}
						if(gastado<0)
							gastado=0.00;
					}
					//ninguna de las anteriores condiciones
					if(!alertado){
						sumaGastado =parseFloat(sumaGastado)-parseFloat(gastado);//0
						if((parseFloat(sumaGastado)+parseFloat(gastado)+ parseFloat(otras))>parseFloat(neto)){
							gastado=parseFloat(neto)-(parseFloat(sumaGastado)+parseFloat(otras));
						}
						if(gastado<0)
							gastado=0.00;
						
					}
					disponible=(parseFloat(disponible)+parseFloat(last))-parseFloat(gastado);
					sumaGastado =parseFloat(sumaGastado)+parseFloat(gastado);//0
					
					$("#tmpMonto").val(gastado);
					$("#tmpMonto").formatCurrency();
					this.value = $("#tmpMonto").val();
					
					$("#tmpMonto").val(sumaGastado);
					$("#tmpMonto").formatCurrency();					
					$(tr).children().eq(4).find("span.estaEP").text($("#tmpMonto").val());
					
					$("#tmpMonto").val(sumaGastado+otras);
					$("#tmpMonto").formatCurrency();
					$(tr).children().eq(4).find("span.totalEP").text($("#tmpMonto").val());
					
					$("#tmpMonto").val(disponible);
					$("#tmpMonto").formatCurrency();
					trSaldo = $(dtSaldosRow).children().eq(i).children("span.gastado").text($("#tmpMonto").val());
					//Se actualiza lastValue, por si se hace otro cambio sin salir de foco
					lastValue = unFormatCurrency(this.value);
					semaforo(neto, sumaGastado + otras, $(tr).children().eq(4));
				}
			}
			//Aux
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
							
			function fnFilterColumn (evt)
			{				
				var value, myWhere, token;
				var column_name = [
					"EjercicioFiscal",
					"Ramo",
					"UnidadResponsable",
					"GrupoFuncional",
					"Funcion",
					"SubFuncion",
					"ProgramaGeneral",
					"ActividadInstitucional",
					"ProgramaPresupuestario",
					"Partida",
					"TipoGasto",
					"FuenteFinanciamiento",
					"EntidadFederativa",
					"Cartera",
					"UnidadEjecutora",
					"UnidadNorativa"
				];
				myWhere = "";
				token = "&qw=";
				var charCode = evt.which ? evt.which : window.event.keyCode;
				if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
					if (charCode <= 13) return true;
	
					if (charCode < 96 || charCode > 106) { // Es teclado numerico?
						var keyChar = String.fromCharCode(charCode);
						var re = /[a-zA-Z0-9.]/;
						if (!re.test(keyChar)) return true;
					}
				}
				for (var i = 1; i < 17; i++) {
					value = $("#col"+i+"_filter" ).val();
					if (value !== "") {
						myWhere += token + column_name[i - 1] + " LIKE '%25" + value + "%25'";
						token = " AND ";
					}
				}
				//oTable = createDataTable(myWhere);
				epDT = initDTEPs(myWhere);
				return true;
			}
			
			function unFormatCurrency(str){
				str = str.replace("$","");
				str = str.replace(/\,/g,'');
				return parseFloat(str);
			}
			function unFormatCurrency2(fld){
				var str=$("#"+fld.id).val();
				str = str.replace("$","");
				str = str.replace(/\,/g,'');
				$("#"+fld.id).val(str);
				
			}
			function semaforo(meta, actual, span){
				meta = Math.round(meta*100)/100;
				actual = Math.round(actual*100)/100;
				
				if(actual == 0 || actual < meta)
					$(span).css("color", "#E00000");
				if(meta < actual )
					$(span).css("color", "#0000FF");
				if(meta == actual )
					$(span).css("color", "#33CC00");
			}
			//Funcion que recibe los  montos de la suma, les da formato, 
			//los coloca en el span y  los colorea.
			function editaSpanSuma(otras, esta, neto, span){
				var actual = otras + esta;
			
				if(actual == 0 || actual < meta)
					$(span).css("color", "#FF9900");
				else
					$(span).css("color", "#33CC00");
			}
			
			
			function openPDF(ext){
				if($("#nIdEstadoSolicitud").val() != "3" && $("#nIdEstadoSolicitud").val() != "4" ){		
			   $("#cIdSolicitudRep").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
			    //se revisa si ya existe una linea en la tabla de msolicitudlineasapartado
			  	queryFormPost("numeroLineasApartadoSolicitud", { async : false});
				 	if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)> 0){
				  //checamos si el numero de lineas de msolicitud es igual a msolicitudlineasapartado
				   		queryFormPost("numeroLineasSolicitud", { async : false});
				   		if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)== parseInt($("#numeroLineasSolicitud").val(),10)){
				   		      queryFormPost("sumaLineasSolicitud", { async : false}); 
					   		   queryFormPost("sumaLineasApartadoSolicitudLineas", { async : false});
					   		   var lineasSolicitud=parseFloat($("#sumaLineasSolicitud").val());
					   		   var lineasApartadoSolicitud=parseFloat($("#sumaLineasApartadoSolicitudLineas").val());
					   		    //var total=lineasSolicitud-lineasApartadoSolicitud;
					   		  if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 1.0){
				   		    //nos puede ser mayor o menor a 1.0 
				   		 	swal("La suma de las lineas presupuestadas no es igual a la suma de las lineas de solicitud favor de revisar.",{icon:"warning",button: "Cerrar"});
				   		    return;
				   		   }
				   		    else 				   		    
				   		      if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 0.0001){
				   		      //se ajusta la requisiscion
				   		       queryFormPost("pa_ajustePresupuestoRequisiscion", {async: false});
				   		      
				   		      }
				   		    
				   		    
				   		    }else{
				   		    	swal("La requisición no se puede imprimir porque no ha presupuestado el total de las líneas de la requisición",{icon:"warning",button: "Cerrar"});
				   		      return;
				   		    
				   		    }
				   		   
				   		}
				   	}	
				   	
				   	

				//Bitácora
				$("#cAccion").val("IMPRIME_REQUISICION");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				   	
			
				if(ext!='csv'){
					window.open(
						"../../servlet/SeguridadCatalogosMateriales?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn=rptRequisiciones.jasper"
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
							+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
							+ "&formato=" +ext
							+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else{
					window.open(
						"../../servlet/CatalogosCSV?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn=rptRequisiciones.jasper"
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
							+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
							+ "&formato=" +ext
							+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
				}
			
		}
		
		function cambiaColorFecha(){
			var longitudReq=$("#lblRequisicion").val().length;
			$("#Req").val($("#lblRequisicion").val().substring(2,(parseInt(longitudReq,10)-2)));
			queryFormPost("mFechaVencimiento", { async:false });
			
			$("#fechaVence").val($("#vence").val());
			if(parseInt($("#difFecha").val(),10)<4 ) 
				$("#fechaVence").css("color","red");
			if(parseInt($("#difFecha").val(),10)>3 & parseInt($("#difFecha").val(),10)<11)
				$("#fechaVence").css("color","orange");
			if(parseInt($("#difFecha").val(),10)>10)	
				$("#fechaVence").css("color","green");
		}
		function obtieneMontoNetoTblLineas(){
			queryFormPost("mMontoTotalTbl",  {async : false, 
			callback : function() 
				{
					var arrayMontoNetoTbl=$("#montoNetotabla").val().split('.');
					var arrayMontoNeto=$("#montoNeto").val().split('.');
					
					if(parseInt(arrayMontoNetoTbl[1],10)>parseInt(arrayMontoNeto[1],10)){
						$("#trlegend").show();
						$("#txtCentavos").val(parseInt(arrayMontoNetoTbl[1],10)-parseInt(arrayMontoNeto[1],10));
					}else{
						$("#trlegend").hide();
						$("#txtCentavos").val(0);
					}
				}
			});
			
		}
		function ajustaCentavos(){
			var arrayMontoNetoTbl=$("#montoNetotabla").val().split('.');
			var arrayMontoNeto=$("#montoNeto").val().split('.');
			var res=parseInt(arrayMontoNetoTbl[1],10)-parseInt(arrayMontoNeto[1],10);
			if(res<parseInt($("#txtCentavos").val(),10)){
				swal("No puedes reducir mas de "+res+" centavos",{icon:"info",button: "Cerrar"});
				return;
			}
			queryFormPost("actualizaCentavosRequi",  {async : false, 
			callback : function() 
				{
					initDTLineas();
					swal("Datos actualizados",{icon:"info",button: "Cerrar"});
				}
			});
		}
		function desactivaBackspace(event){
			if(window.event && window.event.keyCode == 8)
				{
			     	window.event.keyCode = 505;
		    	}
			    if(window.event && window.event.keyCode == 505)
				{
		    	 	return false;
		    	}
		    	return true;
		}
		
			$('#tblLineas tr').live('dblclick', function() {
				//$(this).addClass('row_selected');
				if(parseInt($("#nIdEstadoSolicitud").val(),10)>1){					
					swal("No puedes cambiar el calendario del presupuesto a menos que tu requisici\u00f3n est\u00e9 en estatus de captura.",{icon:"info",button: "Cerrar"});
					return;
				}
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				
				var anSelected = fnGetSelected( oTableLineas );
				if (anSelected != "") {
					var aData = oTableLineas.fnGetData(anSelected[0]);
					$("#numeroLineasSolicitud").val(aData[0]);
					$("#cIdCABM").val(aData[1]);
					$("#cDescripcion").val(aData[2]);
					$("#totalLinea").val(aData[17]);
					$("#totalLinea").formatCurrency();
					$(this).removeClass('row_selected');
					$("#epDisp").val($("#claveEP").val());
					if($("#claveEP").val()==''){
						swal("Selecionar una EP.",{icon:"info",button: "Cerrar"});
					}else{
						queryFormPost("obtieneDatosLineaSol", {async: false,
							callback : function() 
							{
								queryFormPost("obtieneDisponiblePorEP", {async: false,
									callback : function() 
									{
										$("#dialog-form-ep").dialog("open");
									}
								});
							}
						});
					}
				}
					
			});
		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function alertaMontos(cadena){
			
			var idA=cadena+"Apart";
			var idD=cadena+"Disp";
			var idG=cadena+"Guard";
			if($("#"+idA).val()==''){
				$("#"+idA).val(0.00);
				return;
			}
			var apart=unFormatCurrency($("#"+idA).val() );
			var disponible=unFormatCurrency($("#"+idD).val() );
			var guard=unFormatCurrency($("#"+idG).val() );
			
			
			var faltante=quitaFmt($("#faltanteLinea").val() );
			var faltTotalGuardado=parseFloat(faltante)+ parseFloat($("#totalGuardado").val());
			faltTotalGuardado=faltTotalGuardado.toFixed(2);
			sumaTotal();
			if((parseFloat(apart))>(parseFloat(disponible)) 
				|| (parseFloat($("#totalCalendarizado").val()))>( parseFloat(faltTotalGuardado) )
			){
				$("#"+idA).val(0.00);
				sumaTotal();
			}

			
		
		}
		function sumaTotal(){
			var a=parseFloat(unFormatCurrency($("#eneroApart").val()));
			var	b=parseFloat(unFormatCurrency($("#febreroApart").val()));
			var c=parseFloat(unFormatCurrency($("#marzoApart").val()));
			var d=parseFloat(unFormatCurrency($("#abrilApart").val()));
			var e=parseFloat(unFormatCurrency($("#mayoApart").val()));
			var f=parseFloat(unFormatCurrency($("#junioApart").val()));
			var g=parseFloat(unFormatCurrency($("#julioApart").val()));
			var h=parseFloat(unFormatCurrency($("#agostoApart").val()));
			var i=parseFloat(unFormatCurrency($("#septiembreApart").val()));
			var j=parseFloat(unFormatCurrency($("#octubreApart").val()));
			var k=parseFloat(unFormatCurrency($("#noviembreApart").val()));
			var l=parseFloat(unFormatCurrency($("#diciembreApart").val()));
			
			var result=a+b+c+d+e+f+g+h+i+j+k+l;
			$("#totalCalendarizado").val(result.toFixed(2));
			
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
		function desabilitaRadicado(){
			
			if( parseInt($("#nIdEstadoSolicitud").val(),10)>1 ){
				document.getElementById('checkDispRadicado').disabled = true;
			}else{
				document.getElementById('checkDispRadicado').disabled = false;
			}
			document.getElementById('checkDispRadicado').disabled = true;
			activacheck();
		}
		function desabilitaCargaLAyout(){
			if( parseInt($("#nIdEstadoSolicitud").val(),10)>1 ){
				document.getElementById('uploadLayoutFile').disabled = true;
				$("#btnUploadLayout").css("visibility","hidden");
			}else{
				$("#btnUploadLayout").css("visibility","");
				document.getElementById('uploadLayoutFile').disabled = false;
			}
		}
		function muestraDispRadicado(){
			if (!confirm('Si desea activar o desactivar el check se perderan los montos calendarizados, ¿desea continuar?')) {
				activacheck();
				return;
			}
			$("#cuentaDisponible").val('82106');
			if($('#checkDispRadicado').is(':checked')){
				$("#cuentaDisponible").val('82109');
			}
			queryFormPost("mSolicitudUpdateCuaentaDisp", {async: false});
			queryFormPost("deleteSolLineaApartado", {async: false});
			//Inicializa DataTables
			epDT = initDTEPs("");
			lineasDT = initDTLineas();
			$('#tblSaldos').dataTable().fnClearTable();
			$("#claveEP").val('');	
		}
		</script>
</head>
	<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
		<form action="#" name="formPedido">
			<div id="container" class="container" style="width: 90%;">
				<fieldset>
					<legend>
					Información de la Requisición
					</legend>
					<table align="left" width="100%">
						<tr>
							<td align="right" colspan="2">
								<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdxlsLayoutPresupReq" 	name="cmdxlsLayoutPresupReq" 	value="Pre-Layout"		onclick="openLayout();">&nbsp;&nbsp;
								<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdPdfPresupReq" 	name="cmdPdfPresupReq" 	value="PDF"		onclick="openPDF('pdf');">&nbsp;&nbsp;
				    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdxlsPresupReq" 	name="cmdxlsPresupReq" 	value="Excel"	onclick="openPDF('xls');">&nbsp;&nbsp;
				    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdcsvPresupReq" 	name="cmdcsvPresupReq" 	value="CSV"		onclick="openPDF('csv');">&nbsp;&nbsp;
								<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdwordPresupReq" 	name="cmdwordPresupReq" value="Word"	onclick="openPDF('doc');">&nbsp;&nbsp;
	                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 	id="imgSalir" 			name="imgSalir" 		value="Salir"	onclick="window.location = 'Requisiciones.jsp?tab=1&ses=0';">&nbsp;&nbsp;
							</td>
						</tr>
						<tr>
					    	<td align="left" colspan="2">
					    		<input type="text" style="width: 30px;border: 0px none ;background:#FEFEFE" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly  value="<%=unidadUsuarioLogeado%>"/>
					    		<input type="text" style="width: 690px;border: 0px none ;background:#FEFEFE" id="lblDescUsuario" name="lblDescUsuario" readonly />
					    	</td>
				    	</tr>
				    	<tr>
					    	<td align="left" colspan="2">
					    		<input type="text" style="width: 95px;border: 0px none ;background:#FEFEFE" id="lblRequisicion" name="lblRequisicion" readonly />
					    		<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" id="lblDescripcion" name="lblDescripcion" readonly />
					    	</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2">
				    			<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly />
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblEstado" id="lblEstado" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblPartida" id="lblPartida" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblMesRequisicion" id="lblMesRequisicion" readonly/></td>
				    	</tr>
				    	<tr>
							<td align="left" colspan="2">Fecha de Apartado: 
								<input type="text" style="width: 90px;border: 0px none ;background:#FEFEFE"
								name="fechaVence" id="fechaVence" value="" readonly  />
							</td>
						</tr>
						<tr>
    						<td align="left"  colspan="2">
    						<label>Monto bruto de la requisici&oacute;n:</label> <input type="text" style="width: 500px;border: 0px none ;background:#FEFEFE" id="montoBruto" name="montoBruto" readonly />
    						</td>
    					</tr>
    					<tr>
    						<td align="left"  colspan="2">
    						<label>Monto neto de la requisici&oacute;n:</label> <input type="text" style="width: 500px;border: 0px none ;background:#FEFEFE" id="montoNeto" name="montoNeto" readonly />
    						</td>
    					</tr>
    					<tr style="display: none;">
    						<td align="left"  colspan="2">
    						<label>Disponible Radicado:</label> <input type="checkbox" name="checkDispRadicado" id="checkDispRadicado" onclick="muestraDispRadicado()" checked="checked" disabled="disabled"/>
    						</td>
    					</tr>
    					<tr style="display: none;">
    						<td align="left"  colspan="2">
    						<label>Monto neto de la tabla:</label> <input type="text" style="width: 500px" id="montoNetotabla" name="montoNetotabla" readonly style="border-width:0; background-color:transparent"/>
    						</td>
    					</tr>
    					<tr id="trlegend" style="display: none;">
    						<td align="left"  colspan="2">
    						Linea <input type="text" id="lineaReq" name="lineaReq" value="1" style="width: 25px;"/>&nbsp;Centavos<input style="width: 25px;" type="text" id="txtCentavos" name="txtCentavos" value="1" /> <input type="button" id="" name="" value="Ajustar Centavos" onclick="ajustaCentavos();"/>
    						</td>
    					</tr>
					</table>
				</fieldset>	
				<fieldset>
					<legend>Calendarizar</legend>
					<h1 align="left" title="Descarga el Pre-Layout llenalo y cargalo en esta secci&oacute;n">Carga de presupuesto por medio de layout</h1>
					<div id="divUploadLayout" align="left">
						<form name="frmLayout" id="frmLayout" enctype="multipart/form-data" method="post" >
							<table>
								<tr>
									<td><label for="myfile">Seleccionar  archivo excel:</label><input id="uploadLayoutFile" name="uploadLayoutFile" type="file" /></td>
								</tr>
								<tr>
									<td>
    									<textarea class="form-control" id="observaciones" name="observaciones" rows="5" cols="85" readonly></textarea>
									</td>
								</tr>
								<tr>
									<td><input class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all" type="button" id="btnUploadLayout" name="btnUploadLayout" value="Cargar"  onclick="ejecutaAjax()"/> </td>
								</tr>
							</table>
						</form>
					</div>
					
					<br />        	
					<h1 align="left">Estructura Programática "Carga manual"</h1>
					<table id="tblEP" class="display" >
			            <thead>
			                <tr>
						      <th style="width: 5%;">EF</th>
						      <th style="width: 5%;">Ra</th>
						      <th style="width: 5%;">UR</th>
						      <th style="width: 5%;">GF</th>
						      <th style="width: 5%;">Fu</th>
						      <th style="width: 5%;">SF</th>
						      <th style="width: 5%;">PG</th>
						      <th style="width: 5%;">AI</th>
						      <th style="width: 10%;">PP</th>
						      <th style="width: 10%;">Pa</th>
						      <th style="width: 5%;">TG</th>
						      <th style="width: 5%;">FF</th>
						      <th style="width: 5%;">EF</th>
						      <th style="width: 15%;">Ca</th>
						      <th style="width: 5%;">UE</th>
						      <th style="width: 5%;">UN</th>
			                </tr>
			            </thead>
		            	<tfoot>
							<tr>
								<th><input type="text" name="col1_filter" id="col1_filter"  onMouseOver="windowStatus('Ejercicio Fiscal');" /></th>
								<th><input type="text" name="col2_filter" id="col2_filter"  onMouseOver="windowStatus('Ramo');" /></th>
								<th><input type="text" name="col3_filter" id="col3_filter"  onMouseOver="windowStatus('Unidad Responsable');" /></th>
								<th><input type="text" name="col4_filter" id="col4_filter"  onMouseOver="windowStatus('Grupo Funcional');" /></th>
								<th><input type="text" name="col5_filter" id="col5_filter"  onMouseOver="windowStatus('Funcion');" /></th>
								<th><input type="text" name="col6_filter" id="col6_filter"  onMouseOver="windowStatus('SubFuncion');" /></th>
								<th><input type="text" name="col7_filter" id="col7_filter"  onMouseOver="windowStatus('Programa General');" /></th>
								<th><input type="text" name="col8_filter" id="col8_filter"   onMouseOver="windowStatus('Actividad Institucional');" /></th>
								<th><input type="text" name="col9_filter" id="col9_filter"  onMouseOver="windowStatus('Programa Presupuestario');" /></th>
								<th><input type="text" name="col10_filter" id="col10_filter"   onMouseOver="windowStatus('Partida');" /></th>
								<th><input type="text" name="col11_filter" id="col11_filter"   onMouseOver="windowStatus('Tipo de Gasto');" /></th>
								<th><input type="text" name="col12_filter" id="col12_filter"   onMouseOver="windowStatus('Fuente Financiamiento');" /></th>
								<th><input type="text" name="col13_filter" id="col13_filter"   onMouseOver="windowStatus('Entidad Federativa');" /></th>
								<th><input type="text" name="col14_filter" id="col14_filter"   onMouseOver="windowStatus('Cartera');" /></th>
								<th><input type="text" name="col15_filter" id="col15_filter"  onMouseOver="windowStatus('Unidad Ejecutora');"/></th>
								<th><input type="text" name="col16_filter" id="col16_filter"  onMouseOver="windowStatus('Unidad Norativa');"/></th>
								
							</tr>
						</tfoot>
		        	</table>
					<br />
		        	<table id="tblSaldos" class="display"  >
			            <thead>
			                <tr >
			                	<th >Enero</th>
			                    <th >Febrero</th>
			                    <th >Marzo</th>
			                    <th >Abril</th>
			                    <th >Mayo</th>
			                    <th >Junio</th>
			                    <th >Julio</th>
			                    <th >Agosto</th>
			                    <th >Septiembre</th>
			                    <th >Octubre</th>
			                    <th>Noviembre</th>
			                    <th >Diciembre</th>
			                    <th >Anual</th>
			                </tr>
			            </thead>
			        </table>
		        	<br />
		        	<h1 align="left">L&iacute;neas de Requisici&oacute;n</h1>
		        	<table  id="tblLineas" class='display' >
			        	<thead>
			        		<tr>
			        			<th style="width: 2%;">#</th>
			        			<th style="width: 5%;">CUCOP</th>
			        			<th style="width: 10%;">Descripci&oacute;n</th>
								<th style="width: 5%;">Importe Neto</th>
								<th style="width: 18%;">Esta EP + Otras EP = Total</th>
								<th style="width: 5%;">Enero</th>
			        			<th style="width: 5%;">Febrero</th>
			                    <th style="width: 5%;">Marzo</th>
			                    <th style="width: 5%;">Abril</th>
			                    <th style="width: 5%;">Mayo</th>
			                    <th style="width: 5%;">Junio</th>
			                    <th style="width: 5%;">Julio</th>
			                    <th style="width: 5%;">Agosto</th>
			                    <th style="width: 5%;">Septiembre</th>
			                    <th style="width: 5%;">Octubre</th>
			                    <th style="width: 5%;">Noviembre</th>
			                    <th style="width: 5%;">Diciembre</th>
								<th style="display: none;"></th>
			        		</tr>
			        	</thead>
			        </table>
			        <br />
					<button id="btnGuardar" style="display: none;">Guardar</button>
				</fieldset>	
				<!-- Dialogo para captura de montos en las eps Humberto -->
				<div id="dialog-form-ep"
					title="Agregar Estructura Program&aacute;tica">
					<fieldset>
						<table>
							<tr>
								<td>
									<label for="epDisp">
										EP:
									</label>
								</td>
								<td>
									<input type="text" name="epDisp" readonly="readonly" id="epDisp" value="" onkeydown="return(desactivaBackspace(event));"
										class="monto" size="63" />
								</td>
								
							</tr>
							<tr>
								<td>
									<label for="epDisp">
										Total Neto:
									</label>
								</td>
								<td>
									<input type="text" name="totalLinea" id="totalLinea" value="$ 0.00" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
										class="monto" size="23" />
								</td>
							</tr>
							<tr>
								<td>
									<label for="epDisp">
										Faltante:
									</label>
								</td>
								<td>
									<input type="text" name="faltanteLinea" id="faltanteLinea" value="$ 0.00" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
										class="monto" size="23" />
								</td>
							</tr>
						</table>
						<table id="capturaMontosTbl" align="center">
							<thead>
								<tr>
									<th>
										Mes
									</th>
									<th>
										Disponible
									</th>
									<th style="display: none;">
										Guardado
									</th>
									<th>
										Monto
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										<label class="NombreMes" for="eneroDisp">
											Enero
										</label>
									</td>
									<td >
										<input type="text" id="eneroDisp" value="$ 0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="eneroGuard" value="$ 0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="eneroApart" value="0.00" onkeypress="return onlyMoney(this);" onfocus="unFormatCurrency2(this)" onblur="alertaMontos('enero');"
											class="montoCaptura" size="12" maxlength="12" name="eneroApart" />
										<input type="hidden" id="eneroHide" value="" size="12" />
									</td>
								</tr>

								<tr>
									<td>
										<label class="NombreMes" for="febreroDisp">
											Febrero
										</label>
									</td>
									<td>
										<input type="text" id="febreroDisp" value="$ 0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="febreroGuard" value="$ 0.00" class="monto" style="display: none;"
											size="12" />
									</td>
									<td>
										<input type="text" id="febreroApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('febrero');" onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="febreroHide" value="" size="12" />
									</td>
								</tr>

								<tr>
									<td>
										<label class="NombreMes" for="marzoDisp">
											Marzo
										</label>
									</td>
									<td>
										<input type="text" id="marzoDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="marzoGuard" value="0.00" class="monto" readonly="readonly" 
											size="12" />
									</td>
									<td>
										<input type="text" id="marzoApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('marzo');" onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12"  />
										<input type="hidden" id="marzoHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="abrilDisp">
											Abril
										</label>
									</td>
									<td>
										<input type="text" id="abrilDisp"value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="abrilGuard" value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="abrilApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('abril');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="abrilHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="mayoDisp">
											Mayo
										</label>
									</td>
									<td>
										<input type="text" id="mayoDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="mayoGuard"value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="mayoApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('mayo');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12"  />
										<input type="hidden" id="mayoHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="junioDisp">
											Junio
										</label>
									</td>
									<td>
										<input type="text" id="junioDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="junioGuard" value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="junioApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('junio');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="junioHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="julioDisp">
											Julio
										</label>
									</td>
									<td>
										<input type="text" id="julioDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="julioGuard" value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="julioApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('julio');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="julioHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="agostoDisp">
											Agosto
										</label>
									</td>
									<td>
										<input type="text" id="agostoDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="agostoGuard" value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="agostoApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('agosto');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="agostoHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="septiembreDisp">
											Septiembre
										</label>
									</td>
									<td>
										<input type="text" id="septiembreDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="septiembreGuard" value="0.00" class="monto" readonly="readonly" 
											size="12" />
									</td>
									<td>
										<input type="text" id="septiembreApart" value="0.00" onkeypress="return onlyMoney(this);" onblur="alertaMontos('septiembre');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="septiembreHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="octubreDisp">
											Octubre
										</label>
									</td>
									<td>
										<input type="text" id="octubreDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="octubreGuard"value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="octubreApart" value="0.00" onkeypress="return onlyMoney(this);"onblur="alertaMontos('octubre');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="octubreHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="noviembreDisp">
											Noviembre
										</label>
									</td>
									<td>
										<input type="text" id="noviembreDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="noviembreGuard" value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									
									<td>
										<input type="text" id="noviembreApart" value="0.00" onkeypress="return onlyMoney(this);"onblur="alertaMontos('noviembre');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12"/>
										<input type="hidden" id="noviembreHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="diciembreDisp">
											Diciembre
										</label>
									</td>
									<td>
										<input type="text" id="diciembreDisp" value="0.00" class="monto" readonly="readonly" onkeydown="return(desactivaBackspace(event));"
											size="12" />
									</td>
									<td style="display: none;">
										<input type="text" id="diciembreGuard" value="0.00" class="monto" readonly="readonly"
											size="12" />
									</td>
									<td>
										<input type="text" id="diciembreApart" value="0.00"  onkeypress="return onlyMoney(this);"onblur="alertaMontos('diciembre');"onfocus="unFormatCurrency2(this)"
											class="montoCaptura" size="12" maxlength="12" />
										<input type="hidden" id="diciembreHide" value="" size="12" />
									</td>
								</tr>
								<tr>
									<td>
										&nbsp;
									</td>
									<td style="display: none;">
										&nbsp;
									</td>
									<td align="right">
										<label class="NombreMes">
											Total Calendarizado:
										</label>
									</td>
									<td>
										<input type="text" id="totalCalendarizado" readonly="readonly"
											value="$ 0.00" class="numerico notEditable" size="12" onkeydown="return(desactivaBackspace(event));"/>
										<input type="hidden" id="totalGuardado" readonly="readonly"
											value="$ 0.00" class="numerico notEditable" size="12" />
									</td>
								</tr>
							</tbody>
						</table>
					</fieldset>
				</div>

				
				
				<!-- Combo con montos por linea -->
				<select name="tmpGastos" id="tmpGastos" style=""></select>
				
				<!-- Usados para poder consultar la info de la Solicitud -->
				<input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud"	value="<%=cIdTipoSolicitud%>" />
				<input type="hidden" name="cIdUnidadEjecutora"	id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
				<input type="hidden" name="cEjercicio" id="cEjercicio"	value="<%=cEjercicio%>" />
				<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo"	value="<%=nIdConsecutivo%>" />
				<input type="hidden" name="nIdEstadoSolicitud" id="nIdEstadoSolicitud" />
							
				<!-- Login y rol -->
				<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin()%>"/>
				<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
		    	<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
		    	
		    	<!-- Utilizados para poder cargar el popup de EPs -->
				<input type="hidden" id="Partida" name="Partida" value="2" />
				<input type="hidden" id="Partida2" name="Partida2" value="3" />
				<input type="hidden" id="Partida3" name="Partida3" value="5" />
				
				<!-- Utilizado para llenar automaticamente el filtro en el popup de eps -->
				<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" />
				<!--  Valores de Configuración -->
				<input type="hidden" name="GP_NOMBRE" id="GP_NOMBRE" value="desfase_mes_activo_precompromiso"/>
				<input type="hidden" name="GP_VALOR" id="GP_VALOR" />
				
				<!-- Para ocultar columnas de meses pasados -1 -->
				<input type="hidden" name="mesDisponible" id="mesDisponible" value="1"/>
				
				<!-- Para guardar una calendarizacion -->
				<input type="hidden" name="cIdSolicitud" id="cIdSolicitud"/>
				<input type="hidden" name="cIdSolicitu" id="cIdSolicitu" value="<%=cIdSolicitud%>"/>
				<input type="hidden" name="nIdClaveEgresos" id="nIdClaveEgresos"/>
				<input type="hidden" name="ClaveInterna" id="ClaveInterna" />
				
				<input type="hidden" name="nIdLineaSolicitud" id="nIdLineaSolicitud" />
				<input type="hidden" name="cIdCABM" id="cIdCABM" />
				<input type="hidden" name="cDescripcion" id="cDescripcion" />
				<input type="hidden" name="lineas_0" id="lineas_0" />
				<input type="hidden" name="lineas_1" id="lineas_1" />
				<input type="hidden" name="lineas_2" id="lineas_2" />
				<input type="hidden" name="lineas_3" id="lineas_3" />
				<input type="hidden" name="lineas_4" id="lineas_4" />
				<input type="hidden" name="lineas_5" id="lineas_5" />
				<input type="hidden" name="lineas_6" id="lineas_6" />
				<input type="hidden" name="lineas_7" id="lineas_7" />
				<input type="hidden" name="lineas_8" id="lineas_8" />
				<input type="hidden" name="lineas_9" id="lineas_9" />
				
				
				
				<input type="hidden" name="mes01" id="mes01" />
				<input type="hidden" name="mes02" id="mes02" />
				<input type="hidden" name="mes03" id="mes03" />
				<input type="hidden" name="mes04" id="mes04" />
				<input type="hidden" name="mes05" id="mes05" />
				<input type="hidden" name="mes06" id="mes06" />
				<input type="hidden" name="mes07" id="mes07" />
				<input type="hidden" name="mes08" id="mes08" />
				<input type="hidden" name="mes09" id="mes09" />
				<input type="hidden" name="mes10" id="mes10" />
				<input type="hidden" name="mes11" id="mes11" />
				<input type="hidden" name="mes12" id="mes12" />
				
				<input type="hidden" name="BanderaRadicado" id="BanderaRadicado" value="N" />
				
				<!-- Aux para formatCurrency de monto asignado en tblSaldos -->
				<input type="hidden" name="tmpMonto" id="tmpMonto"/>
				
			<input type="hidden" name="existeLineaApartadoSolicitud" id="existeLineaApartadoSolicitud"/>
			<input type="hidden" name="sumaLineasSolicitud" id="sumaLineasSolicitud"/>
			<input type="hidden" name="sumaLineasApartadoSolicitudLineas" id="sumaLineasApartadoSolicitudLineas"/>
			<input type="hidden" name="numeroLineasApartadoSolicitud" id="numeroLineasApartadoSolicitud"/>
			<input type="hidden" name="numeroLineasSolicitud" id="numeroLineasSolicitud"/>
			<input type="hidden" name="cIdSolicitudRep" id="cIdSolicitudRep"/>
			<input type="hidden" name="cAccion" id="cAccion"/>
			<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
			
			<input type="hidden" name="vence" id="vence"/>
			<input type="hidden" name="Req" id="Req"/>
			<input type="hidden" name="difFecha" id="difFecha"/>
			<input type="hidden" name="UES_USUARIO" id="UES_USUARIO" value="" />
			<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
			<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
			<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab.getU_UR() %>" />
			<input type="hidden" name="claveEP" id="claveEP" value=""/>
			<input type="hidden" name="ExisteLineaAp" id="ExisteLineaAp"  />
			
			<input type="hidden" name="m01" id="m01"  />
			<input type="hidden" name="m02" id="m02"  />
			<input type="hidden" name="m03" id="m03"  />
			<input type="hidden" name="m04" id="m04"  />
			<input type="hidden" name="m05" id="m05"  />
			<input type="hidden" name="m06" id="m06"  />
			<input type="hidden" name="m07" id="m07"  />
			<input type="hidden" name="m08" id="m08"  />
			<input type="hidden" name="m09" id="m09"  />
			<input type="hidden" name="m10" id="m10"  />
			<input type="hidden" name="m11" id="m11"  />
			<input type="hidden" name="m12" id="m12"  />
			<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82109" />
			<input type="hidden" name="reporteNombre" id="reporteNombre" value="Pre_Layout_Precupuesto_Requi.xlsx" />
			<input type="hidden" name="nTipoReporte" id="nTipoReporte" value="1" />
			<input type="hidden" name="operacion" id="operacion" value="6" />
			<input type="hidden" name="cIdCapitulo" id="cIdCapitulo" value="0" />
			
			
			</div>
		</form>
	</body>
</html>