<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>

<%
	String cEjercicio = "";
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Calendar c1 = Calendar.getInstance(); // today
	//Obtiene la fecha del sistema
	Calendar c2 = Calendar.getInstance(); 
	c2.add(Calendar.DATE,5); //le suma 5 días
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	if(usuario==null){
		response.sendRedirect("../../index.jsp");
		return;
	}
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		System.out.println("cEjercicio de mantenimiento lineas consolidado : "+cEjercicio );
	}
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	String today = sdf.format(c1.getTime());
	String today_mascinco = sdf.format(c2.getTime());
	
%>

<!doctype html>
<html lang="en">
  <head>
    <title>Mantenimiento Requisiciones</title>
	    <meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Procedimiento">
		<style type="text/css" title="currentStyle"> 
			@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../../css/interfaz.css";
		</style>
		<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
		
		<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
		<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>
	
		<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
		<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
	
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
		
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/funciones.js"></script>
		<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" charset="utf-8">
		var nFolioPreCompromiso;
		var cIdConsolidado;
		var vcaNoCompromiso;
			$(document).ready(function() {
				//Llena los combos
				querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutoraConsultar", {async: false });
				querySelectPost("mTipoConsolidadoRead", "cboTipoConsolidadoConsultar", {async: false,
					callback : function() {
						$("#cboTipoConsolidadoConsultar").val(0);
					}	
				});
// 				querySelectPost("mCatalogoPeriodoRead", "cboPeriodoConsultar", {async: false });
				querySelectPost("mCatalogoAlcanceCMBRead", "cboAlcanceConsultar", {async: false });
				querySelectPost("mCatalogoEstadoSolicitudRead", "cboEstadoConsultar", {async: false });
				queryFormPost("cg_roleRead", {async: false});
				//Se obtiene el ejercicio fiscal
				queryFormPost("EjercicioFiscalActvRead", { async : false});
				// se inicializa con la pestaña de consulta habilitada 
				
				$('#tblConsolidados').dataTable({
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
			        bScrollCollapse: true,
					bDestroy: true,
				  	bJQueryUI: true,
					bRetrive : true,
					sPaginationType: "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay partidas",
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
					aaSorting: [[ 0, "asc" ]] ,
						aaSorting: [[ 0, "asc" ],[ 1, "asc" ],[2,"asc"]] ,			
						aoColumns: [
							{ sName: "cIdTipoConsolidadoCons" },
							{ sName: "cIdUnidadEjecutoraCons" },
							{ sName: "nIdConsecutivo" },
							{ sName: "cAlcanceCons" },
							{ sName: "cEstadoCons" },
							{ sName: "cDescripcionCons" },
							{ sName: "cIdConsolidadoCons",bVisible: false },
							{ sName: "cProcedimientoCons",bVisible: false }
						]
				});
				$("#tblLineas").dataTable({
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
			        bScrollCollapse: true,
					bDestroy: true,
				  	bJQueryUI: true,
					bRetrive : true,
					sPaginationType: "full_numbers",
					oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay partidas",
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
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdConsolidado" },
					{ sName: "nIdLineaConsolidado"   },
					{ sName: "cDescripcion"	},
					{ sName: "cIdProcedimiento" },
					{ sName: "pedCont"	},
					{ sName: "anular"	},
					{ sName: "liberar"	},
					{ sName: "cancelarPresupuesto"	},
					{ sName: "est_ped_cont"	},
					{ sName: "est_ped_cont_num",bVisible: false	},
					{ sName: "est_procedimiento_num",bVisible: false},
					{ sName: "nFolioPreCompromiso",bVisible: false}
					
					
				]			
        	});			
			
			$("#btnBuscarConsultaConsolidado" ).button().click(function() {
				mostrarTablaConsolidado();
			});			
			
			//Al darle doble click a la tabla de consolidados
			$('#tblConsolidados').on('dblclick', 'tr',function(){
				var aTrs = $('#tblConsolidados').dataTable().fnGetNodes();
				$(this).addClass('row_selected');
				var nTr = $('#tblConsolidados').dataTable().fnGetData(this);
				cIdConsolidado=nTr[6];
				$("#cIdConsolidado").val(nTr[6]);
				$("#cIdProcedimiento").val(nTr[7]);
 				mostrarTablaLineas();

			});
			
		});//Fin del cocument ready
		
			

			function mostrarTablaConsolidado(){
				var qw = " cIdUnidadEjecutoraCons = '"+$("#cboUnidadEjecutoraConsultar").val()+"'";
				if($("#cboTipoConsolidadoConsultar").val()!='0'){
					qw += " AND cIdTipoConsolidadoCons LIKE '"+($("#cboTipoConsolidadoConsultar").val())+"'";
				}
				
				if($("#cboAlcanceConsultar").val() != 0){
					qw += " AND nIdAlcanceCons = "+$("#cboAlcanceConsultar").val();	
				}
				if($("#cboEstadoConsultar").val() != 0){
					qw += " AND cEstadoCons = "+$("#cboEstadoConsultar").val();	
				}
				if($("#numeroConsultar").val() != ""){
					qw += " AND nIdConsecutivo = "+$("#numeroConsultar").val();
				}
				if($("#descripcionConsultar").val() != ""){
					qw +=" AND cDescripcionCons LIKE '%25" + $.trim($("#descripcionConsultar").val())+"%25'";
				}
        		$('#tblConsolidados').dataTable({
					"bFilter" : false,
					"bDestroy" : true,
					"bJQueryUI": true,
					"bAutoWidth" : true,
					"iDisplayLength": 5, //Cuantos registros se despliegan
					"sPaginationType": "full_numbers",
					"oLanguage": {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros <h5></h5>",
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
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vconsultaConsolidado&qw="+qw,
						aaSorting: [[ 0, "asc" ],[ 1, "asc" ],[2,"asc"]] ,			
						aoColumns: [
							{ sName: "cIdTipoConsolidadoCons" },
							{ sName: "cIdUnidadEjecutoraCons" },
							{ sName: "nIdConsecutivo" },
							{ sName: "cAlcanceCons" },
							{ sName: "cEstadoCons" },
							{ sName: "cDescripcionCons" },
							{ sName: "cIdConsolidadoCons",bVisible: false },
							{ sName: "cProcedimientoCons",bVisible: false }
							
							
						]
					});
				}
	function lineasSoli_De_LineaCons(){
		var qw = " cIdConsolidado = '"+$("#cIdConsolidado").val()+"' and nIdLineaConsolidado="+$("#nIdLineaConsolidado").val()+"";
			
			$("#lineasSolPorLineaCons").dataTable({         					 				
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
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.[v_lineasSolPorLineaCons&qw="+qw,
				aaSorting: [[ 1, "asc" ]] ,
				bAutoWidth: false,
				aoColumns: [
					{ sName: "cIdConsolidado" },
					{ sName: "nIdLineaConsolidado"},
					{ sName: "cIdSolicitud"},
					{ sName: "nIdLineaSolicitud"}
					
					],	
					fnInitComplete: function(oSettings, json) {
						liberaProgramaAnual();
					}
			});	
			$("#lineasSolPorLineaCons").attr('visible', false);
	}
	//Tabla para cuando el flujo no es normal (Recorte presupuestal)
	function conRecortePresupuestal(){
		
		$("#epLineaConsolidadoTabla").attr('visible', true);
		var qw = " cIdDocumento='"+$("#cIdConsolidado").val()+"'";
			if($("#accion").val()=="Cancelar"){
			qw = " cIdDocumento='"+$("#cIdProcedimiento").val()+"'";
			}
			$("#epLineaConsolidado").dataTable({         					 				
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
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.v_mSaldoPrecompromisoMateriales &qw="+qw,
				aaSorting: [[ 2, "asc" ]] ,
				bAutoWidth: false,
				aoColumns: [
					{ sName: "cIdDocumento",bVisible: false },
					{ sName: "EP"},
					{ sName: "cMes"},
					{ sName: "mImporte"},
					{ sName: "mSaldoAgotado"},  
					{ sName: "quitarMonto"}
					],
					fnInitComplete: function(oSettings, json) {
						$("#MontoLinea").val($("#MontoNeto").val());
						//movConRecortePresupuestal();
					}
			});	
			
	}					
	function llenaPreCompromisoDetalle(){
		var campos=" '"+$("#cIdConsolidado").val()+"','"+$("#nFolioPreCompromiso").val()+"','"+$("#cCentroContable").val()+"',"+$("#nIdLineaConsolidado").val();		
			$("#apartadoConsolidado").dataTable({         					 				
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
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.[fn_mObtieneDatosParatPrecomMaterialesDetalle]("+campos+")",
				aaSorting: [[ 1, "asc" ]] ,
				bAutoWidth: false,
				aoColumns: [
					{ sName: "nFolio" },
					{ sName: "nDocRenglon"},
					{ sName: "ep"},
					{ sName: "cEvento"},
					{ sName: "mImporte"},
					{ sName: "mImporteNegativo"},
					{ sName: "mes"},
					{ sName: "centroContable"}
					],	
					fnInitComplete: function(oSettings, json) {
						aplicaContable();
						
					}
			});	
			$("#apartadoConsolidado").attr('visible', false);
	}		
	function mostrarTablaLineas(){

		$('#tblLineas').dataTable().fnClearTable(); 
		var qw = "'"+cIdConsolidado+"'";  
		   
		//Checar si existe un pedido aprobado cIdConsolidado,estadoPedido
		$("#cIdConsolidado").val(cIdConsolidado);
		$("#estadoPedido").val('4');//4 pedido aprobado
		queryFormPost("ExistePedidoAprobado", {async: false}); 
		
	   if($("#existe").val()=='EXISTE'){                                                        
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: "CONSULTALINEASCONSOLIDADO", Param:"",Campos:qw, MaxReg:"" , ajax: 'false'}, 
				function(j)
				{     
					var a=0;
					arrayCompleto=new Array();
					for (var i = 0; i < j.length; i++) 
   					{
   						
   						//Lineas que no se asignaron a ningun pedido
   						if(j[i].Col4==""){
	    					arrayCompleto [a]=[
	    						"<input type='text' name='cIdConsolidado"+a+"' id='cIdConsolidado"+a+"' value='" + j[i].Col0 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						"<input type='text' name='nIdLineaConsolidado"+a+"' id='nIdLineaConsolidado"+a+"' value='" + j[i].Col1 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col2,
	    						"<input type='text' name='cIdProcedimiento"+a+"' id='cIdProcedimiento"+a+"' value='" + j[i].Col3 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col4,
	    						"<input type='button' class='btnInterfaceBG ui-button ui-corner-all' title='Devuelve el precompromiso al disponible y libera el PAAAS' value='Anular' name='anular"+a+"' id='anular"+a+"'   onclick='bloquearCheck(\"liberar"+a+"\",\"anular"+a+"\",\"Anular\",\""+j[i].Col1+"\")'/>",
	    						"<input type='button' class='btnInterfaceBG ui-button ui-corner-all' title='Devuelve el precompromiso al apartado y libera el PAAAS' value='Liberar' name='liberar"+a+"' id='liberar"+a+"'  onclick='bloquearCheck(\"anular"+a+"\",\"liberar"+a+"\",\"Liberar\",\""+j[i].Col1+"\")'/>",
	    						j[i].Col7,
	    						j[i].Col8,
	    						"<input type='text' name='est_ped_cont_num"+a+"' id='est_ped_cont_num"+a+"' value='" + j[i].Col9 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						"<input type='text' name='est_procedimiento_num"+a+"' id='est_procedimiento_num"+a+"' value='" + j[i].Col10 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col11
	    					
	                            
							]; 
						}
						//Lineas que hay en pedidos que no estan aprobados
						else if(j[i].Col9!=4 & j[i].Col9!=6 & j[i].Col9!=7 & j[i].Col9!=3){//3,4,6,7 -->EN SAI PRESUPUESTADO,aprobado,anulado y clonado
						arrayCompleto [a]=[
	    						"<input type='text' name='cIdConsolidado"+a+"' id='cIdConsolidado"+a+"' value='" + j[i].Col0 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						"<input type='text' name='nIdLineaConsolidado"+a+"' id='nIdLineaConsolidado"+a+"' value='" + j[i].Col1 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col2,
	    						"<input type='text' name='cIdProcedimiento"+a+"' id='cIdProcedimiento"+a+"' value='" + j[i].Col3 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col4,
	    						j[i].Col5,
	    						j[i].Col6,
	    						"<input type='button' class='btnInterfaceBG ui-button ui-corner-all' title='Devuelve el precompromiso al apartado' value='CancelarPre' name='cancelaPresupuesto"+a+"' id='cancelaPresupuesto"+a+"' disabled='disabled' onclick='bloquearCheck(\"anular"+a+"\",\"liberar"+a+"\",\"Cancelar\",\""+j[i].Col1+"\")'/>",
	    						
	    						j[i].Col8,
	    						"<input type='text' name='est_ped_cont_num"+a+"' id='est_ped_cont_num"+a+"' value='" + j[i].Col9 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						"<input type='text' name='est_procedimiento_num"+a+"' id='est_procedimiento_num"+a+"' value='" + j[i].Col10 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col11
	                            
							];
						}
						else{
							arrayCompleto [a]=[ 
	    						"<input type='text' name='cIdConsolidado"+a+"' id='cIdConsolidado"+a+"' value='" + j[i].Col0 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						"<input type='text' name='nIdLineaConsolidado"+a+"' id='nIdLineaConsolidado"+a+"' value='" + j[i].Col1 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col2,
	    						"<input type='text' name='cIdProcedimiento"+a+"' id='cIdProcedimiento"+a+"' value='" + j[i].Col3 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col4,
	    						j[i].Col5,
	    						j[i].Col6,
	    						j[i].Col7,
	    						
	    						j[i].Col8,
	    						"<input type='text' name='est_ped_cont_num"+a+"' id='est_ped_cont_num"+a+"' value='" + j[i].Col9 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						"<input type='text' name='est_procedimiento_num"+a+"' id='est_procedimiento_num"+a+"' value='" + j[i].Col10 + "' readonly style='border-width:0; background-color:transparent'/>",
	    						j[i].Col11
	                            
							]; 
						}
						a=a+1;

   					 }
   					 $('#tblLineas').dataTable().fnAddData(arrayCompleto);
   					$('#tblLineas').dataTable().fnAdjustColumnSizing();
 				});  
 				$("#existe").val(''); 
		}//del if
		else{
			swal("No hay pedidos o contratos aprobados para el consolidado "+cIdConsolidado,{icon:"info",button: "Cerrar"});
		}
	}//de la funcion
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true; 
	}
			
	function bloquearCheck(idBloquear,idChecked,accion,nIdLineaCons){
		//Checar si el flujo es normal o ya hubo recorte presupuestal
		queryFormPost("fueFlujoNormal", {async: false}); 
		//Se obtiene el ejercicio fiscal
		queryFormPost("EjercicioFiscalActvRead", { async : false});
		$("#nIdLineaConsolidado").val(nIdLineaCons);
		
		$("#accion").val(accion);
			//Anular
		if(accion=="Anular"){
			swal({
				title: "",
				text: "Está seguro de Anular el presupuesto?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					document.getElementById(idBloquear).disabled = true;
					document.getElementById(idChecked).disabled = true;
					$("#accion").val(2);
					flujoNormal();
				}
			});
			
		}
			//Liberar
		else if(accion=="Liberar"){
			swal({
				title: "",
				text: "Está seguro de Liberar el presupuesto?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					document.getElementById(idBloquear).disabled = true; 
					document.getElementById(idChecked).disabled = true;
					$("#accion").val(1);
					flujoNormal();
				}
			});
		}
			//CAncelar presupuesto de lineas que se quedaron en pedidos que no fueron aprobados
		else if(accion=="Cancelar"){
			swal({
				title: "",
				text: "Está seguro de cancelar el presupuesto?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					guardarPrecompromiso();
					$("#estadoLineaConcolidado").val('3');
					queryFormPost("ActualizaEstadoLineaConsolidado", {async: false});
				}
			});
		}
		//Limpia hiddens
		$("#nFolioPreCompromiso").val('0');
		$("#cIdConsolidado").val('');
		$("#nIdLineaConsolidado").val('');	
		$("#accion").val('');
		$("#cEstadoLineaSolicitud").val('');
		$("#cIdSolicitud").val('');
		$("#cIdLineaSolicitud").val('');
		$("#fecha").val('');
		$("#vence").val('');
		$("#existeFlujoNormal").val('-1');
		$("#cIdProcedimiento").val('');
		$("#resultado").val('0');
		
	}
	function flujoNormal(){
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val()==0){
			$.ajax({url: '../../servlet/ConsolidadoServlet?cEjercicio='+$("#cEjercicio").val() , type:'post' , async: false, data:'operacion=1', dataType: 'json', success: guardaFolio});
		}
		if($("#nFolioPreCompromiso").val()==0)
			return -1;
		//aplicacion contable
		$.ajax({url: '../../servlet/MantenimientoLineasConsolidado?nFolioPrecom='+$("#nFolioPreCompromiso").val()+"&idCons="+$("#cIdConsolidado").val()
				+"&nIdLineaCons="+$("#nIdLineaConsolidado").val()+"&ramo="+$("#cRamo").val()+"&unidadE="+$("#cUnidadResponsable").val()
				+"&operacion="+$("#accion").val()+"&cEjercicio="+$("#cEjercicio").val(), 
			type:'post' , async: false,data:'operacion=5', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				swal(mensajeAp,{icon:"info",button: "Cerrar"});
				mostrarTablaLineas();
			}
		});
	}
	function generaEncabezado(){
		//Genera un nuevo folio en caso de que no lo tenga
			if($("#nFolioPreCompromiso").val()==0){
				$.ajax({url: '../../servlet/ConsolidadoServlet?cEjercicio='+$("#cEjercicio").val() , type:'post' , async: false, data:'operacion=1', dataType: 'json', success: guardaFolio});
			}
							
			if($("#nFolioPreCompromiso").val()==0)
				return -1;
					
			$("#cCentroContable").val( "<%=cCentroContable%>");
			//Guarda en una tabla auxiliar el encabezado del compromiso
			getNextSequenceVal({seqName: "PR-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
							
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
		
			$("#fCarga").val( "<%=today%>" ); 
			$("#fAplicacion").val("<%=today%>" ); 
			$("#cIdContrato").val($("#cIdConsolidado").val()); // id del consolidado
			$("#cTipoContrato").val( "DI" ); 
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val( "<%=cUR%>" );
			$("#caNoPreCompromiso").val( vcaNoCompromiso );
			$("#nEnviadoSICOP").val("0");
			$("#nMes").val( nMes ); 
			$("#fVigencia").val( "<%=today%>" );// vigenciaPrecomMateriales
			$("#cTipoPoliza").val( "PR" );

			 //Genera Encabezado
			queryFormPost('tPreCompromisoEncabezadoCreate', {async: false });
	}
	function guardarPrecompromiso(){
		if($("#existeFlujoNormal").val()==1){//0 es recorte presupuestal en el precom del consol, 1 es flujo normal, 2 es recorte presupuestal en el procedimiento y 3 flujo desconocido 
			
			generaEncabezado();
			
			llenaPreCompromisoDetalle();
			
		}else if($("#existeFlujoNormal").val()==0){
			//Obtener el monto de la linea de consolidado
			queryFormPost('obtieneMontoNetoLineaConsolidado', {async: false });
			//Mostrar una tabla para de eps con las que se pago el precompromiso
			conRecortePresupuestal();

		}else{
			swal("Flujo desconocido con valor : "+$("#existeFlujoNormal").val(),{icon:"info",button: "Cerrar"});
			return;
		}
   }
    function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		swal("Ha ocurrido un error al crear el caso, contacte a su soporte",{icon:"info",button: "Cerrar"});
      		return -1;
        }else{
     	   $("#nFolioPreCompromiso").val(folioPre);
     	   $("#folioCasoPreCompromiso").val(folioCaso);
     	   nFolioPreCompromiso= $("#nFolioPreCompromiso").val();
     	   
     	}
	}
	function setSequenceVal(seqValue) {
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "PR" + $("#cEjercicio").val() + seqValue;
		
	}
	function generaPreCompromisoDetalle(){
		$("#nFolioPreCompromiso").val(nFolioPreCompromiso);
		var aTrs = $('#apartadoConsolidado').dataTable().fnGetNodes();	
        for ( var i=aTrs.length-1 ; i>=0; i-- ){
			var aData = $('#apartadoConsolidado').dataTable().fnGetData( aTrs[i]);
			$("#nDocRenglon").val(aData[1]);
			$("#EP").val(aData[2]);
			$("#cEvento").val(aData[3]);//PRECOM_MAT
			//El evento cambia cuando se va a liberar una linea de consolidado
			if($("#accion").val()=="Liberar"){
				$("#cEvento").val('PRECOMMAT_APD');
			}
			
			$("#mImporte").val(aData[4]);
			$("#mImporteNegativo").val(aData[5]);
			$("#nMesD").val(aData[6]);
			
			queryFormPost("tPreCompromisoDetalleCreate", {async: false });
			
		}
	}
	function aplicaContable(){
		//var mensajeAp="";
		$("#cIdConsolidado").val(cIdConsolidado);
		$("#cAccion").val("GENERA PRECOMPROMISO");
		$("#cIdDocumento").val($("#cIdconsolidado").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		
		generaPreCompromisoDetalle();	
		
		
		//aplicacion contable
		$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()
				+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&partidas=1", type:'post' , async: false,data:'operacion=5', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				swal(mensajeAp,{icon:"info",button: "Cerrar"});
			}
		});
		//return mensajeAp;
	}
	function liberaProgramaAnual(){
		
		$("#cEstadoLineaSolicitud").val('C'); //C es Cancelada o Anulada
		//El estatus cambia cuando se va a liberar una linea de consolidado
		if($("#accion").val()=="Liberar"){
			$("#cEstadoLineaSolicitud").val('L');
		}
		var aTrs = $('#lineasSolPorLineaCons').dataTable().fnGetNodes();	
        for ( var i=aTrs.length-1 ; i>=0; i-- ){
			var aData = $('#lineasSolPorLineaCons').dataTable().fnGetData( aTrs[i]);
			$("#cIdSolicitud").val(aData[2]);
			$("#cIdLineaSolicitud").val(aData[3]);
			queryFormPost("actualizaEstadoRequisicionContratoModificado", { async : false});
		}
		
	}
	function ActualizavigenciaApartadoLineaSolicitud(){
		
		//Se obtiene el folio del apartado
		queryFormPost("mObtieneFolioApartadoPorLineaDeConsolidado", { async : false});
		//Actualizar el folio en el tipo=old
		queryFormPost("mUpdateFolioApartadoPorLineaDeConsolidado", { async : false});
		//insertar un nuevo registro a la tabla mApartadoVigencia con el mismo folio y con tipo =new
		$("#fecha").val("<%=today%>");
		$("#vence").val("<%=today_mascinco%>");
		queryFormPost("mCreateFolioApartadoPorLineaDeConsolidado", { async : false});
	}
	function movConRecortePresupuestal(){
		$("#cEvento").val('PRECOM_MAT');
		//El evento cambia cuando se va a liberar una linea de consolidado
		if($("#accion").val()=="Liberar"){
			$("#cEvento").val('PRECOMMAT_APD');
		}
		if($("#accion").val()=="Anular"){
			$("#cEvento").val(''); //poner el evento que pasa del apartado materiales a disponible
		}
		
		var aTrs = $('#epLineaConsolidado').dataTable().fnGetNodes();	
        
       	//generar encabezado y detalle
       	generaEncabezado();
       	var a=1;
		 for ( var i=aTrs.length-1 ; i>=0; i-- ){
			var aData = $('#epLineaConsolidado').dataTable().fnGetData( aTrs[i]);
			if(aData[5]!=""){
				$("#nDocRenglon").val(a);
				$("#EP").val(aData[1]);
				
				$("#mImporte").val(aData[3]);
				$("#mImporteNegativo").val(aData[3]*-1);
				$("#nMesD").val(aData[2]);
				
				queryFormPost("tPreCompromisoDetalleCreate", {async: false });
			}
			a=a+1;
		}
		//aplicacion contable
		$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&partidas=1", type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				swal(mensajeAp,{icon:"info",button: "Cerrar"});
			}
		});
		return mensajeAp;
	}
	function checaMonto(){
		var aTrs = $('#epLineaConsolidado').dataTable().fnGetNodes();
		var total=0;
		for ( var i=aTrs.length-1 ; i>=0; i-- ){
			var aData = $('#epLineaConsolidado').dataTable().fnGetData( aTrs[i]);
			
			if(aData[5]!=""){
				var t=parseFloat(aData[4])+parseFloat(aData[5]);
				if(parseFloat(aData[3])<t){
					swal("No puedes agotar mas de lo que hay en la ep",{icon:"info",button: "Cerrar"});
					return;
				}else{
					total=total+t;
				}
				
			}
		}
		if(parseFloat(total)==parseFloat($("#MontoNeto2").val())){
			movConRecortePresupuestal();
		}else{
			swal("Se debe de agotar el monto de la línea",{icon:"info",button: "Cerrar"});
		}
	}
	function cancelaPrecompromiso(){
		
		$("#accion").val(3);
		if($("#nFolioPreCompromiso").val()==0){
			$.ajax({url: '../../servlet/ConsolidadoServlet?cEjercicio='+$("#cEjercicio").val() , type:'post' , async: false, data:'operacion=1', dataType: 'json', success: guardaFolio});
		}
		if($("#nFolioPreCompromiso").val()==0)
			return -1;
		
		$.ajax({url: '../../servlet/MantenimientoLineasConsolidado?nFolioPrecom='+$("#nFolioPreCompromiso").val()+"&idCons="+$("#cIdConsolidado").val()
				+"&nIdLineaCons="+0+"&ramo="+$("#cRamo").val()+"&unidadE="+$("#cUnidadResponsable").val()+"&operacion="+$("#accion").val()+"&cEjercicio="+$("#cEjercicio").val(), 
			type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				swal(mensajeAp,{icon:"info",button: "Cerrar"});
				mostrarTablaLineas();
			}
		});
	}
</script>
 </head>
  
  <body>
	<form id="formMantConsolidado">
		<div class="container-fluid">
			<h1>Mantenimiento de Consolidado<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Filtros</legend>
	 				<div class="form-group">
	 					<div class="row">
	 						<div class="col-md-4">
		 						<label for="cboUnidadEjecutoraConsultar">Unidad Ejecutora</label>
								<select class="custom-select" id="cboUnidadEjecutoraConsultar" name="cboUnidadEjecutoraConsultar" > 
									<option value="<%=usuario.getU_UR()%>" selected="selected"> </option>
								</select>
							</div>
							<div class="col-md-4">
		 						<label for="cboTipoConsolidadoConsultar">Tipo de consolidado</label>
								<select class="custom-select" id="cboTipoConsolidadoConsultar" name="cboTipoConsolidadoConsultar" > 
									<option value="<%=usuario.getU_UR()%>" selected="selected"> </option>
								</select>
							</div> 
	 					</div>
	 					<div class="row" style="display: none">
	 						<div class="col-md-4">
		 						<label for="cboPeriodoConsultar">Periodo</label>
								<select class="custom-select" id="cboPeriodoConsultar" name="cboPeriodoConsultar" > 
								</select>
							</div>
	 					</div>
	 					<div class="row">
	 						<div class="col-md-4">
		 						<label for="cboAlcanceConsultar">Alcance</label>
								<select class="custom-select" id="cboAlcanceConsultar" name="cboAlcanceConsultar" > 
								</select>
							</div>
							<div class="col-md-4">
		 						<label for="numeroConsultar">N&uacute;mero</label>
								<input type="text" class="form-control" placeholder="Número" aria-label="Número" aria-describedby="basic-addon1"  id="numeroConsultar" name="numeroConsultar" onKeyPress="return(onlyNumbers(event))">
							</div>
	 					</div>
	 					<div class="form-group row" style="display: none">
	 						<div class="form-group col-md-4">
		 						<label for="cboEstadoConsultar">Estado</label>
								<select class="custom-select" id="cboEstadoConsultar" name="cboEstadoConsultar" > 
								</select>
							</div> 
	 					</div>
		 				<div class="row">
	 						<div class="col-md-4">
		 						<label for="descripcionConsultar">Descripci&oacute;n</label>
								<input type="text" class="form-control" placeholder="Descripción" aria-label="Descripción" aria-describedby="basic-addon1"  id="descripcionConsultar" name="descripcionConsultar" >
							</div> 
	 					</div>
		 				<div class="row">
		 					<div class="col-md-4">
		 						<input type="button" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start" 	id="btnBuscarConsultaConsolidado" name="btnBuscarConsultaConsolidado" 	value="BUSCAR"/>
							</div>
						</div>
						<div class="form-group row" id= "div_tblConsolidados">
							<div class="col">
								<table id="tblConsolidados" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr>
											<th >&nbsp;</th>
						                	<th >&nbsp;</th>
						                    <th >&nbsp;</th>
						                    <th >Alcance</th>
						                    <th >Estado Consolidado</th>
						                    <th >Descripci&oacute;n</th>
						                   	<th style="display: none;"></th>
						                   	<th style="display: none;"></th>
										</tr>										
									</thead>
								</table>
								
							</div>
						</div>
						<div class="form-group row">
		 					<div class="form-group col-md-4">
		 						<input type="button" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-center" 	id="cancelaTodo" name="cancelaTodo" 	value="Cancela Lineas Consolidado" title="Cancela todas las lineas del consolidado que no se adjudicaron" onclick="cancelaPrecompromiso()" />
							</div>
						</div>
						<div class="form-group row" id= "div_tblLineas">
							<div class="col">
								<table id="tblLineas" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr>
											<th>Consolidado</th>
						                    <th >Part Cons</th>
						                    <th >Descripci&oacute;n</th>
						                    <th >Procedimiento</th>
						                    <th >Pedido/Contrato</th>
						                    <th >Anular</th>
						                    <th >Liberar</th>
						                    <th >CancelarPres</th>
						                    <th >Estado Pedido/Contrato</th>
						                    <th  style="display: none;"></th>
						                    <th  style="display: none;"></th>
						                    <th  style="display: none;"></th>
										</tr>										
									</thead>
								</table>
								
							</div>
							<div class="form-group row">
			 					<div class="form-group col-md-4">
			 						<input type="button" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-center" 	id="btnGuerdarLineasConsolidado" name="btnGuerdarLineasConsolidado" 	
			 						value="Guardar"  onclick="guardarLineasConsolidado()" style="display: none;"/>
								</div>
							</div>
						</div>
						
	 				</div>
	 				
	 				
	 			</fieldset>
	 		</div>
	 	</div>
    	<div id="apartadoConsolidadoTabla" style="display: none">
			<table id="apartadoConsolidado"  >
				<thead>
					<tr>
						<th>folio</th>
						<th>renglon</th>
						<th>ep</th>
						<th>cEvento</th>
						<th>importe</th>
						<th>importeNegativo</th>
						<th>Mes</th>
						<th>centro contable</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="lineasSolPorLineaConsTabla" style="display: none">
			<table id="lineasSolPorLineaCons"  >
				<thead>
					<tr>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						
					</tr>
				</thead>
			</table>
		</div>
		<div id="epLineaConsolidadoTabla" style="display: none">
			Monto Linea : <input type="text" name="MontoLinea" id="MontoLinea" value="" style="width:80px; border-width:0; background-color:transparent">
			<table id="epLineaConsolidado"  >
				<thead>
					<tr>
						<th style="display: none"></th>
						<th>EP</th>
						<th>MES</th>
						<th>IMPORTE</th>
						<th>SALDO AGOTADO</th>
						<th>Quitar Monto</th>
					</tr>
				</thead>
			</table>
			<input type="button" name="GuardaMonto" id="GuardaMonto" value="Guardar" onclick="checaMonto()">
		</div>
		
  		<input id="nFolioPreCompromiso" name="nFolioPreCompromiso" type="hidden" value="0">
  		<input id="folioCasoPreCompromiso" name="folioCasoPreCompromiso" type="hidden" value="0">
  		<input id="fCarga" name="fCarga" type="hidden">
  		<input id="fAplicacion" name="fAplicacion" type="hidden">
  		<input id="cIdContrato" name="cIdContrato" type="hidden">
  		<input id="cTipoContrato" name="cTipoContrato" type="hidden">
  		<input id="cRamo" name="cRamo" type="hidden" value="<%=cRamo%>">
  		<input id="cUnidadResponsable" name="cUnidadResponsable" type="hidden" value="<%=cUR%>">
  		<input id="caNoPreCompromiso" name="caNoPreCompromiso" type="hidden">
  		<input id="nEnviadoSICOP" name="nEnviadoSICOP" type="hidden">
  		<input id="nMes" name="nMes" type="hidden">
  		<input id="fVigencia" name="fVigencia" type="hidden">
  		<input id="cCentroContable" name="cCentroContable" type="hidden" value=""> 
  		<input id="cTipoPoliza" name="cTipoPoliza" type="hidden" value="">
  		
  		<input id="cEjercicio" name="cEjercicio" type="hidden" value="">
  		<input id="cIdConsolidado" name="cIdConsolidado" type="hidden" value="">
  		<input id="cIdProcedimiento" name="cIdProcedimiento" type="hidden" value="">
  		<input id="nIdLineaConsolidado" name="nIdLineaConsolidado" type="hidden" value="">
  		<input id="estadoPedido" name="estadoPedido" type="hidden" value="">
  		<input id="existe" name="existe" type="hidden" value="">
  		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuario.getLogin() %>"/>
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
	    <input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin() %>" >
	    
	    <input id="nDocRenglon" name="nDocRenglon" type="hidden" value="">	
	    <input id="EP" name="EP" type="hidden" value="">	
	    <input id="cEvento" name="cEvento" type="hidden" value="">	
	    <input id="mImporte" name="mImporte" type="hidden" value="">	
	    <input id="mImporteNegativo" name="mImporteNegativo" type="hidden" value="">	
	    <input id="nMesD" name="nMesD" type="hidden" value="">	
	    <input id="estadoLineaConcolidado" name="estadoLineaConcolidado" type="hidden" value="">	
	    
	    <input id="cEstadoLineaSolicitud" name="cEstadoLineaSolicitud" type="hidden" value="">
	    <input id="cIdSolicitud" name="cIdSolicitud" type="hidden" value="">		
	    <input id="cIdLineaSolicitud" name="cIdLineaSolicitud" type="hidden" value="">
	    <input id="accion" name="accion" type="hidden" value="">
	    
	    <input id="C_FOLIO_APA" name="C_FOLIO_APA" type="hidden" value=""> 
	    <input id="fecha" name="fecha" type="hidden" value=""> 
	    <input id="vence" name="vence" type="hidden" value=""> 
	    <input id="existeFlujoNormal" name="existeFlujoNormal" type="hidden" value="-1"> 
	    <input id="resultado" name="resultado" type="hidden" value="0"> 
	    <input id="ue" name="ue" type="hidden" value="">
	    <input id="monto" name="monto" type="hidden" value="">
	    
	    <input id="MontoNeto" name="MontoNeto" type="hidden" value="">
	    <input id="MontoNeto2" name="MontoNeto2" type="hidden" value="">
	    <input id="cIdDocumento" name="cIdDocumento" type="hidden" value="">
	    <input id="cAccion" name="cAccion" type="hidden" value="">
  	</form>
  </body>
</html>