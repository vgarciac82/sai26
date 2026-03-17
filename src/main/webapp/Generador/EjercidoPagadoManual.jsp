<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.gestion.core.Role"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.sai.contabilidad.caja.*"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	  
	  
	String cLogin = "";
	String cUR = "";
	String cCentroContable = "";	
	cLogin = usuario.getLogin();
	cUR = usuario.getU_UR();
	String AlcanceVistas=CajaBusinessLogic.LeerVistas(cLogin);
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	String fAplicacion[]=CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	Role role = usuario.getRole("CHEQUES");
	boolean imprimecheque = role!=null && "CHEQUES".equals(role.getNombre());
	
	role = usuario.getRole("ISRLAUDOS");
	boolean pagaISRLaudos = role!=null && "ISRLAUDOS".equals(role.getNombre());
	
	System.out.println(cUR);	
	
	ConfiguraAplicativoBusinessLogic configSys = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean activaPagoParcial = "S".equals( configSys.getSystemSetting("ACTIVA_PAGO_PARCIAL") );
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean esSAIAlterno = "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") ) || "true".equals( cabl.getSystemSetting("SAI_FONDEN") );
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Autorizar Generaci&oacute;n de Layout's</title>

	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<script type="text/javascript" charset="utf-8">
	var BeneficiarioNuevo=" ";
	var id_Caja = " ";
	var login=" ";
	var evento="";
	var nombre=" ";
	var ready=false;
	var vistas="<%=AlcanceVistas%>";
	var esSAIAlterno = <%=esSAIAlterno%>;
	var modalCambioBeneficiario;
	var modalBeneficiario;
	var oTablePD;
	
	$(document).ready(
		function() {
				modalCambioBeneficiario = new bootstrap.Modal(document.getElementById('dialog-CambioBeneficiario'), 'data-bs-backdrop');
				modalBeneficiario = new bootstrap.Modal(document.getElementById('dialog-Beneficiario'), 'data-bs-backdrop');
				
				ready=true;
				var condicionUR=generaCondicionUR();
				var activaPagoParcia = <%=activaPagoParcial%>;
				querySelectPost("CatCuentasBancarias2", "ctaBancariasLAUDOS", {async : false});
				$("#divCtab").hide();
				if( activaPagoParcia ){
					$("#pagoParcialSpan").show();
					$("#pagoParcialSpanRG").show();
					$("#pagoParcialSpanRet").show();
				}else{
					$("#pagoParcialSpan").hide();
					$("#pagoParcialSpanRG").hide();
					$("#pagoParcialSpanRet").hide();
				}			
				
				$("#btn_imprimeCheque").button();
				$("#btn_Aplicar").button();
				$("#btn_busca").button();
				$("#btn_limpia").button();
				$("#btn_Reimprimir").button();
				$("#btn_Reemplazar").button();
				$("#btn_layoutBancarioRG").button();
				$("#btn_AplicarISR").button();
				$("#btn_AplicarCXP").button();
				$("#btn_AplicarPDRG").button();
				$("#btn_layoutBancarioPDRG").button();
				$("#btn_layoutBancarioAnticipoCaja").button();
				
					$("#tabs").tabs( {
				       "show": function(event, ui) {
				           var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
				           if ( oTable.length > 0 ) {
				               oTable.fnAdjustColumnSizing();
				           }
				      	}
				  	} );
				  	var condicion_UR=" ";
										
					if(login=="admin")		
						condicion_UR =" != 'A16' ";
					else
						condicion_UR =" IN ("+vistas+")";
				  	$( ".datepicker" ).datepicker({
									showOn: "button",			
									buttonImage: "../images/calendar.gif",
									buttonImageOnly: true,
									minDate:new Date(<%=fAplicacion[1]%>),
									maxDate:new Date(<%=fAplicacion[2]%>),
									onSelect: function(dateText, inst) {
								 		validarFechaAplicacion(dateText);
								 	}
										});
				  	
				  	$("#dt_RelacionGastosProveedor tbody").click(function(event) {
						$(oTable.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$(event.target.parentNode).addClass('row_selected');					
					});
					
					$("#dt_PagosDiversosRG tbody").click(function(event) {
						$(oTablePD.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$(event.target.parentNode).addClass('row_selected');					
					});	
					
					$("#dt_ISRLaudos tbody").click(function(event) {
						$(oTable.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$(event.target.parentNode).addClass('row_selected');					
					});
					
				  	$("#dt_ChequesElaborados").dataTable({
				  		"bLengthChange" : true,
	   		            "bFilter" : true,
	   		            "bSort" : true,
	   		            "bInfo" : true,
	   		            "bPaginate" : true,
	   		            "bAutoWidth" : false,
	   		            "bScrollCollapse" : true,   		            	    	
	   		            "sPaginationType" : "full_numbers",
	   		            "bJQueryUI" : true,
	   		            "bRetrive" : true,
	   		            "bDestroy" : true,
	   		            "bServerSide": true,                   
	   					"iDisplayLength": 25,
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaChequesGenerados&qw=" +"cUnidadResponsable"+condicion_UR,
						aoColumns: [
							{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "cUnidadResponsable",	bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "nNumCheque",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cIdRFC",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cConcepto",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "mImporteCheque",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cCuentaBancaria",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "banco",				bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
							{ sName: "fAplicacion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"}
						],
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtered from _MAX_ total entries)",
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
			   		
			   		$("#dt_ChequesCancelados").dataTable({
			   			"bLengthChange" : true,
	   		            "bFilter" : true,
	   		            "bSort" : true,
	   		            "bInfo" : true,
	   		            "bPaginate" : true,
	   		            "bAutoWidth" : false,
	   		            "bScrollCollapse" : true,   		            	    	
	   		            "sPaginationType" : "full_numbers",
	   		            "bJQueryUI" : true,
	   		            "bRetrive" : true,
	   		            "bDestroy" : true,
	   		            "bServerSide": true,                   
	   					"iDisplayLength": 25,	       
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaChequesCancelados&qw=" +"cUnidadResponsable"+condicion_UR,
						aoColumns: [
							//{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "cUnidadResponsable",	bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "nFolioCheque",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cIdRFC",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cConcepto",			bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cDescripcionPoliza",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "mImporteCheque",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cCuentaBancaria",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignLeft"},
							{ sName: "nNumCheque",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "banco",				bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
							{ sName: "fCancelacion",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignLeft"}
						],
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtered from _MAX_ total entries)",
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
			   		
			   		$("#dt_ChequesCaja").dataTable({
			   			"bLengthChange" : true,
	   		            "bFilter" : true,
	   		            "bSort" : true,
	   		            "bInfo" : true,
	   		            "bPaginate" : true,
	   		            "bAutoWidth" : false,
	   		            "bScrollCollapse" : true,   		            	    	
	   		            "sPaginationType" : "full_numbers",
	   		            "bJQueryUI" : true,
	   		            "bRetrive" : true,
	   		            "bDestroy" : true,
	   		            "bServerSide": true,                   
	   					"iDisplayLength": 25,
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistrosParaChequeCaja&qw=" +"cUnidadResponsable"+condicion_UR,
						aoColumns: [
							{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "nFoliocaja",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "RFC",				    bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cdescripcionpoliza",	Searchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "mMontoSolicitud",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "CTAB",		        bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "Banco",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignRight"},
							{ sName: "fAplicacion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "cevento",			bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"},
							{ sName: "cnombre",			bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"}
						],
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtered from _MAX_ total entries)",
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
			   		
			   		$("#dt_AnticipoCaja").dataTable({
			   			"bLengthChange" : true,
	   		            "bFilter" : true,
	   		            "bSort" : true,
	   		            "bInfo" : true,
	   		            "bPaginate" : true,
	   		            "bAutoWidth" : false,
	   		            "bScrollCollapse" : true,   		            	    	
	   		            "sPaginationType" : "full_numbers",
	   		            "bJQueryUI" : true,
	   		            "bRetrive" : true,
	   		            "bDestroy" : true,
	   		            "bServerSide": true,                   
	   					"iDisplayLength": 25,	       
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistrosDeAnticiposCaja&qw=" +"cUnidadResponsable"+condicion_UR,
						aoColumns: [
							{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "nFoliocaja",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "RFC",				    bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "cnombre",			    bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"},
							{ sName: "cdescripcionpoliza",	Searchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "mMontoSolicitud",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
							{ sName: "CTAB",		        bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "Banco",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignRight"},
							{ sName: "fAplicacion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "nCuentaBeneficiario",	bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
							{ sName: "cevento",			    bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"}				
						],
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtered from _MAX_ total entries)",
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
			   		   		
					//SE AGREGA CONDICION PARA VERIFICAR DE ACUERDO A LOS ROLES QUIEN PUEDE VER LA OPCION DE IMPRESION Y CONSULTA DE CHEQUES
					if (<%=imprimecheque%> != true) {
						$("#chk_Cheques").attr('disabled', true);
						$("#chk_Cheques").hide();
						$("#lblCheque").hide();
						$("#LNK02").hide();
						$("#LNK03").hide();
						$("#LNK04").hide();
					}
					if (<%=pagaISRLaudos%> != true) {
						$("#LNK05").hide();
						$("#LNK06").hide();
					}
					if(<%=imprimecheque%> == true && "<%=cUR%>" == "A03" ) {
						//SE DESACTIVA LA PESTAÑA DE CHEQUES DE CAJA PARA EL A03
						$("#LNK02").hide();
						$("#LNK08").hide();
					}
					
					var oTable = $("#dt_RelacionGastosProveedor").dataTable();

					querySelectPost("CatCuentasBancarias2", "ctaBancarias", {async : false});
					querySelectPost("CatCuentasBancarias2", "ctaBancariasPDRG", {async : false});
					querySelectPost("CatCuentasBancarias2", "ctaBancariasAnticipoCaja", {async : false});
					querySelectPost("CatCuentasBancariasLaudos", "ctasBancariaLaudos", {async : false});
					cargaGrid();
					cargaGridPDRG();
					cargaISRLaudos();
					cargaGridAnticipoCaja();
					inicio = false;
				
					$("#checkAll").change(function(){
						if ($('#checkAll').is(':checked'))
							$("input:checkbox").attr('checked', 'checked');
						else
							$("input:checkbox").removeAttr('checked');
					});
				
				login=$("#u_Login").val();
				cssReadOnly();
	});//FIN DEL READY
	
	var inicio = true;
	var nomTabla = "";
	var nomTablaDet = "";
	var nomCampos = "";
	var nomCamposDet = "";
	
	function cargaGrid(){
		
		$("#cWhere").val(generaCondicion());
		
		oTable = $("#dt_RelacionGastosProveedor").dataTable({
		    "bLengthChange": true,
		    "bFilter": true,
		    "bSort": true,
		    "bInfo": true,
		    "bPaginate": false,               
		    "bAutoWidth": false,
		    "bScrollCollapse": true,
		    "sPaginationType": "full_numbers", 
		    "bJQueryUI": true,
		    "bDestroy": true,
		    "bServerSide": true,
		    "sAjaxSource": window.location.protocol + "//" + window.location.host + "/" +
		                   window.location.pathname.split("/")[1] +
		                   "/crud?rt=t&ql=vRelacionGastosProveedorEjerPag&qw=" +
		                   " " + encodeURI($("#cWhere").val()),

		    "aoColumns": [
		      { 
		        sName: "id", 
		        bSearchable: true,
		        bSortable: false, 
		        bVisible: true, 
		        sClass: "alignLeft",
		        fnRender: function(o){
		          var folio = o.aData[2];
		          return '<input type="checkbox" class="chkRG" data-folio="'+ folio +'" id="' + folio + '">';
		        }
		      },
		      { sName: "cUnidadResponsable",   bSearchable:false, bSortable:true,  bVisible:true, sClass:"alignLeft" },
		      { sName: "nFolioRELACIONGASTOS", bSearchable:false, bSortable:true,  bVisible:true, sClass:"alignLeft" },
		      { sName: "caNoContrarrecibo",    bSearchable:true,  bSortable:true,  bVisible:true, sClass:"alignLeft" },
		      { sName: "Integracion",          bSearchable:true,  bSortable:true,  bVisible:true, sClass:"alignLeft" },
		      { sName: "cIdRFC",               bSearchable:false, bSortable:true,  bVisible:true, sClass:"alignLeft" },
		      { sName: "cnombre",              bSearchable:false, bSortable:true,  bVisible:true, sClass:"alignLeft" },
		      { sName: "cConcepto",            bSearchable:false, bSortable:false, bVisible:true, sClass:"alignLeft" },
		      { sName: "mImporteMasIva",       bSearchable:false, bSortable:true,  bVisible:true, sClass:"alignRight" },
		      { sName: "CtaIntegrada",         bSearchable:false, bSortable:false, bVisible:true, sClass:"alignLeft" },
		      { sName: "ID_DESTINO_GASTO",     bSearchable:true,  bSortable:true,  bVisible:true, sClass:"alignRight" }
		    ],
		    "oLanguage": {
		      sProcessing:"Procesando...", sLengthMenu:"Mostrar _MENU_ registros",
		      sZeroRecords:"No hay registros a mostrar",
		      sEmptyTable:"No se encontraron resultados con esos filtros",
		      sLoadingRecords:"Cargando...",
		      sInfo:"Registros _START_ al _END_ de _TOTAL_",
		      sInfoEmpty:"Registro 0 al 0 de 0",
		      sInfoFiltered:"(filtered from _MAX_ total entries)",
		      sSearch:"Buscar:",
		      oPaginate:{ sFirst:"Primero", sPrevious:"Ant.", sNext:"Sigte.", sLast:"&Uacute;ltimo" }
		    }
		  });
   		
	}
	
	function tipoPagoRad(){
		$("#cTipoPagoRad").val(document.getElementById("tipoPagRadicado").value);
		}
	
	function cargaGridPDRG(){
		// TODO: RADICADO
		
		var tabla = "";
		tipoPagoRad();
		
		$("#cTipoPagoRad").val(document.getElementById("tipoPagRadicado").value);
		
		if ($("#cTipoPagoRad").val() == "1"){
			tabla = "vPagosDiversosRGEjerPag";
		}else if ($("#cTipoPagoRad").val() == "2"){
			tabla = "vPagosObraEjerPag";
		}else if ($("#cTipoPagoRad").val() == "3"){
			tabla = "vPagosRGEjerPag";
		}else if ($("#cTipoPagoRad").val() == "4"){
			tabla = "vPagosPenasIPEjerPag";
		}
		
		$("#cWhere").val(generaCondicionPDRG());
   		oTablePD = $("#dt_PagosDiversosRG").dataTable({
   			"bLengthChange" : true,
            "bFilter" : true,
            "bSort" : true,
            "bInfo" : true,
            "bPaginate" : false,
            "bAutoWidth" : false,
            "bScrollCollapse" : true,   		            	    	
            "sPaginationType" : "full_numbers",
            "bJQueryUI" : true,
            "bRetrive" : true,
            "bDestroy" : true,
            "bServerSide": true, 	       
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + tabla +"&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns: [
				{ sName: "id",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "nFolio",					bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "caNoContrarrecibo",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "Integracion",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "RFC",						bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "NOMBRE",					bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cConcepto",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "mImporteMasIva",			bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
				{ sName: "CtaIntegrada",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "ID_DESTINO_GASTO",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignRight"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
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
	}
	
	function cargaISRLaudos(){
		$("#dt_ISRLaudos").dataTable({
			"bLengthChange" : true,
            "bFilter" : true,
            "bSort" : true,
            "bInfo" : true,
            "bPaginate" : true,
            "bAutoWidth" : false,
            "bScrollCollapse" : true,   		            	    	
            "sPaginationType" : "full_numbers",
            "bJQueryUI" : true,
            "bRetrive" : true,
            "bDestroy" : true,
            "bServerSide": true,                   
			"iDisplayLength": 25,	       
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vOperacionAjenaLaudos&qw=1=1",
			aoColumns: [
				{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "nFolioOperAjenas",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cIDRFC",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "Descripcion",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "mImporteTotal",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignRight"},
				{ sName: "fCaptura",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
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
	}
	
	function cargaDataTableCaja(){ 
		$("#cWhereCaja").val(generaCondicionCaja());
	//	alert($("#cWhereCaja").val());
		//var condicionUR=generaCondicionUR();
		oTable = $("#dt_ChequesCaja").dataTable({
			"bLengthChange" : true,
            "bFilter" : true,
            "bSort" : true,
            "bInfo" : true,
            "bPaginate" : true,
            "bAutoWidth" : false,
            "bScrollCollapse" : true,   		            	    	
            "sPaginationType" : "full_numbers",
            "bJQueryUI" : true,
            "bRetrive" : true,
            "bDestroy" : true,
            "bServerSide": true,                   
			"iDisplayLength": 25,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistrosParaChequeCaja&qw=" + " " + encodeURI($("#cWhereCaja").val()),
			aoColumns: [
				{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "nFoliocaja",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "RFC",				    bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cdescripcionpoliza",	bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "mMontoSolicitud",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "CTAB",		        bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "Banco",				bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
				{ sName: "fAplicacion",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cevento",			    bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"},
				{ sName: "cnombre",			    bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
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
   		
		$("#dt_ChequesCaja tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');					
		});	
	
	}// fin carga solicitudes de caja
	
	
	function cargaChequesGenerados(){ 
	//	$("#cWhereChequeGenerado").val(generaCondicionCaja());
		
			$("#dt_ChequesElaborados").dataTable({
			"bLengthChange" : true,
            "bFilter" : true,
            "bSort" : true,
            "bInfo" : true,
            "bPaginate" : true,
            "bAutoWidth" : false,
            "bScrollCollapse" : true,   		            	    	
            "sPaginationType" : "full_numbers",
            "bJQueryUI" : true,
            "bRetrive" : true,
            "bDestroy" : true,
            "bServerSide": true,                   
			"iDisplayLength": 25,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaChequesGenerados&qw=" + "cUnidadResponsable"+condicion_UR /*+ encodeURI($("#cWrhere").val())*/,
			aoColumns: [
				{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "nNumCheque",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cIdRFC",				bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cConcepto",			bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "mImporteCheque",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cCuentaBancaria",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "banco",				bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
				{ sName: "fAplicacion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignLeft"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
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
   		
		$("#dt_ChequesElaborados tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');					
		});	
	
	}// fin carga cheques Generados
	
	function cargaGridAnticipoCaja(){
		
		$("#cWhereAnticipo").val(generaCondicionAnticipoCaja());
   		oTable = $("#dt_AnticipoCaja").dataTable({
   			"bLengthChange" : true,
            "bFilter" : true,
            "bSort" : true,
            "bInfo" : true,
            "bPaginate" : true,
            "bAutoWidth" : false,
            "bScrollCollapse" : true,   		            	    	
            "sPaginationType" : "full_numbers",
            "bJQueryUI" : true,
            "bRetrive" : true,
            "bDestroy" : true,
            "bServerSide": true,                   
			"iDisplayLength": 25,	       
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistrosDeAnticiposCaja&qw=" + " " + encodeURI($("#cWhereAnticipo").val()),
			aoColumns: [
				{ sName: "id",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "nFoliocaja",			bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "RFC",				    bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "cnombre",			    bSearchable: false,	bSortable: true,  bVisible: false, sClass: "alignLeft"},
				{ sName: "cdescripcionpoliza",	bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "mMontoSolicitud",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "CTAB",		        bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "Banco",				bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
				{ sName: "fAplicacion",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "nCuentaBeneficiario",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cevento",			    bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignLeft"}				
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
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
	}
	
	function generaCondicion(){
		var where = "";
		if(  $("#cUnidadResponsable").val() == "A02"  ) 
			where = where + " CtaIntegrada = '" + $('#ctaBancarias option:selected').val() + "' AND cUnidadResponsable = cUnidadResponsable";
		else
			where = where + " CtaIntegrada = '" + $('#ctaBancarias option:selected').val() + "' AND cUnidadResponsable = '" + $("#cUnidadResponsable").val() + "'";
		return where;
	}
	
	function generaCondicionPDRG(){
		var where = "";
		
		if(  $("#cUnidadResponsable").val() == "A02"  ) 
			where = where + " CtaIntegrada = '" + $('#ctaBancariasPDRG option:selected').val() + "' AND cUnidadResponsable = cUnidadResponsable";
		else
			where = where + " CtaIntegrada = '" + $('#ctaBancariasPDRG option:selected').val() + "' AND cUnidadResponsable = '" + $("#cUnidadResponsable").val() + "'";
			
		return where;
	}
	
	function generaCondicionAnticipoCaja(){
		var folioCaja = $("#folioCaja").val();
		var token="%";
		var where = "1=1";
		
		if($('#ctaBancariasAnticipoCaja option:selected').val() != ""){
			where = where + " AND CTAB = '" + $('#ctaBancariasAnticipoCaja option:selected').val() + "'";
		}
		
		return where;
	}
	
	function generaCondicionCaja(){
		var folioCaja = $("#folioCaja").val();
		var token="%";
		var where = "";


		if (ready==true)
			return where=" nFolioCaja like  '"+folioCaja+token+"'";
		else{
			
			ready=true;
			if ("<%=cUR%>" != "A02" ) 
				return "  and cUnidadResponsable ='"+"<%=cUR%>"+"'";
				
			else	
			return "1=1";
			}
	}
	
	function generaCondicionUR(){
		 var whereUR = "";
		if ("<%=cUR%>" != "A02" )
			return whereUR="   cUnidadResponsable ='"+"<%=cUR%>"+"'";
		else
		return "1=1 ";	
		}
		

		
	
	
		function limpiarDatos(){
		$("#folioCaja").val("");
		
		document.getElementById("folioCaja").focus();
		
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		
		cargaDataTableCaja();
	}
	
	function aplicarISRLaudos(){
		var ctab = "";
		if ($.trim($("#ctasBancariaLaudos option:selected").val())==""){			
			Swal.fire({ icon: "warning",
						text: "Favor de Seleccionar una cuenta Bancaria."});
			return;
		}else{ ctab = $("#ctasBancariaLaudos option:selected").val(); }
		var vacio = true;
		var tListado = $("#dt_ISRLaudos").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_ISRLaudos');
		var folios = "";
		var token = "";
		var cont = 0;
		var fPago =  null;
		var usuario = $("#u_Login").val();
		$("#nFolios").val("");
		fPago = $("#fechaAplicacionISR").val();
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				folios += token + tListado[i][2];
				token = ", ";
				vacio = false;
				cont++;
			}
		}
		if (!vacio){
			document.getElementById("esperar").style.visibility = 'visible';
			$("#btn_AplicarISR").attr("disabled","disabled");
			$("#nFolios").val(folios);
			
			$.ajax({
				url:'./ejercidoPagadoValidar.jsp',
				type:'post',
				data:{tipo:'aplicarISRLaudos', cfolios:folios, fPago:fPago, usuario:usuario, ctab:ctab },
				success: function(data){
					if(data.estatus == "correcto"){
						Swal.fire({ icon: "info",
									text: "Se aplicaron los siguientes folios: "+$("#nFolios").val()});
					}else{
						Swal.fire({ icon: "warning",
									text: "No se pudo aplicar el Ejercido/Pagado de los folios: " + $("#nFolios").val() + " [" + data.estatus + "]"});						
					}
					cargaISRLaudos();
					document.getElementById("btn_AplicarISR").disabled = false;
					document.getElementById("esperar").style.visibility = 'hidden';
				}
			});			
		}else{
			Swal.fire({ icon: "warning",
						text: "Seleccione al menos un Registro."});			
			return;
		}
	}
	
	function AplicarPagosDiversosRG(){ /*ARLA 29/02/2024 Se agrega la funcion generaLayoutBancario() para aplicar el pago al mismo tiempo que se genera el lay put bancario.*/
		/*VGC Cambio para pago parcial*/
		var esPagoParcial = ( $("#pagoParcialRG").attr("checked")?"S" : "N" );
		
		var ctab = "";
		if ($.trim($("#ctaBancariasPDRG option:selected").val())==""){
			Swal.fire({ icon: "warning",
						text: "Favor de Seleccionar una cuenta Bancaria."});			
			return;
		}else{ 
			ctab = $("#ctaBancariasPDRG option:selected").val(); 
		}
		
		var vacio = true;
		var tListado = $("#dt_PagosDiversosRG").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_PagosDiversosRG');
		var folios = "";
		var token = "";
		var fPagado =  null;
		var usuario = $("#u_Login").val();
		var tipoPago = $("#cTipoPagoRad").val();
		$("#nFolios").val("");
		
		//if (!validaFechas($("#fechaEjercidoPD").val(),$("#fechaPagadoPD").val(),"Pagado")) return;
		if(tipoPago == 1){
			var pago = 'PAGODIV';
		}
		fPagado = $("#fechaPagadoPD").val();
		
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				folios += token + tListado[i][2];
				token = ", ";
				vacio = false;
			}
		}
		
		if (!vacio){
			$("#btn_AplicarPDRG").attr("disabled","disabled");
			$("#sTipoLayout").val("PDIVRG");
			$("#nFoliosLayout").val(folios);
			$("#sCuentaLayout").val(ctab);
			$.ajax({
				url:'./ejercidoPagadoValidar.jsp',
				type:'post',
				data:{tipo:'aplicarPagosDiversosRG', cfolios:folios, fPagado:fPagado, usuario:usuario, ctab:ctab,parcial:esPagoParcial,cTipoPagoRad:tipoPago },
				success: function(data){
					if(data.estatus == "correcto"){							
						Swal.fire({ icon: "info",
									text: "Se aplicaron los siguientes folios: "+folios+"."});
					}else{
						Swal.fire({ icon: "warning",
									text: "No se termino de aplicar el Ejercido/Pagado. [" + data.estatus + "]"});						
					}
					generaLayoutBancario(pago);	
					
					cargaGrid();
				},
				error: function(xhr, textStatus, errorThrown) {					
					Swal.fire({ icon: "error",
								text: errorThrown});
					cargaGridPDRG();					
				}
			});	
		}else{
			Swal.fire({ icon: "warning",
						text: "Seleccione al menos un Registro."});
			return;
		}
	}
	
	function AplicarEjercidoPagadoAnterior(){ //FUNCION ANTERIOR
		
		var vacio = true;
		var tListado = $("#dt_RelacionGastosProveedor").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_RelacionGastosProveedor');
		var folios = "";
		var id_CXP = "";
		var token = "";
		var cont = 0;
		var fPago =  null;
		var usuario = $("#u_Login").val();
		
		$("#btn_Aplicar").attr("disabled","disabled");
		$("#nFolios").val("");
		
		fPago = $("#fechaAplicacion").val();
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				$("#nFolios").val(tListado[i][2]);
				folios += token + tListado[i][2];
				token = ", ";
				vacio = false;
				cont++;
			}			  
		}
		if (!vacio){
			if ($("#chk_Cheques").prop("checked") && cont>1){				
				Swal.fire({ icon: "info",
							text: "En el caso de Cheque unicamente debes seleccionar un registro."});
				document.getElementById("btn_Aplicar").disabled = false;
				return;
			}
			if (!$("#chk_Cheques").prop("checked")){
				Swal.fire({ icon: "info",
							text: "Se aplicara contablemente los siguientes folios "+ folios + "."});			
			}
			
			id_CXP = folios.replace(", ", "/");
			
			nomTabla = " tRELACIONGASTOSEncabezado ";
			nomTablaDet = " tRELACIONGASTOSDetalle ";
			token = "";
			for (i=0; i<tListado.length;i++){
				var row = tListado2.rows[i+1];
				var chkbox = row.cells[0].childNodes[0];
				
				if(null != chkbox && true == chkbox.checked){
				
					$("#foliocxp").val(tListado[i][3]);
					$("#ExisteReg").val("0");	
					queryFormPost("existeEjerPagManual",{async: false });
					
					if ($("#ExisteReg").val()=="0"){
						getNextSequenceVal({seqName: "EJERCIDO" , async: false, callback: setSequenceValE});
						getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
						$("#accion").val("1");//insertar y apligar ejercido y pagado
					}else if ($("#nFolioPagado").val()=="-1"){
						getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
						$("#accion").val("2");//insertar y aplicar pagado
					}else{
						if ($("#statusE").val()=="" && $("#statusP").val()==""){
							$("#accion").val("3");//aplicar ejercido y pagado
						}else if ($("#statusP").val()==""){
							$("#accion").val("4");//aplicar pagado
						}
					}
					
					var idfolioEjercido = $("#nFolioEjercido").val();
					var idfolioPagado = $("#nFolioPagado").val();
					var idFolio = 0;
					var cuentaBanc = "";
					var accion = $("#accion").val();
					
					idFolio = tListado[i][2];
					id_CXP = tListado[i][3];
					cuentaBanc = tListado[i][9];
				
					$.ajax({
						url:'./ejercidoPagadoValidar.jsp',
						type:'post',
						data:{tipo:'aplicacionManual', idFolio:idFolio, idCXP:id_CXP, tabla:nomTabla, tablaDet:nomTablaDet, fPago:fPago, usuario:usuario, idfolioEjercido:idfolioEjercido, idfolioPagado:idfolioPagado, ctab:cuentaBanc, accion:accion},
						success: function(data){
							if(data.estatus == "correcto"){
								if ($("#chk_Cheques").prop("checked")){
									Swal.fire({ icon: "info",
												text: "Se imprimira el Cheque de la RG "+folios+" ("+id_CXP+")."});
									window.open(
										"../reportes/ImpresionCheque?"
										+"&folio="+id_CXP
										+"&usuario="+login
										+"&tipo="+1
										+"&rn=ChequeManual.jasper",
										 'Procesando', 'status=1');
								}
							}else{
								Swal.fire({ icon: "info",
											text: data.estatus});
							}
						}
					});
					cargaGrid();
					document.getElementById("btn_Aplicar").disabled = false;					
				}
			}
		}else{
			document.getElementById("btn_Aplicar").disabled = false;			
			Swal.fire({ icon: "info",
						text: "Seleccione al menos un Registro."});
			return;
		}
	}
	
	function AplicarEjercidoPagado(){ //FUNCION NUEVA /*ARLA 22/03/2024 Se agrega la funcion generaLayoutBancario() para aplicar el pago al mismo tiempo que se genera el lay put bancario.*/		
		//if(confirm("Ya genero el layout Bancario?\nEn caso afirmativo de click en Aceptar.\nCaso contrario de click en Cancelar.")){
		var tipoPago = $("#cTipoPagoRad").val();
		
		if(tipoPago == 1){
			var pago = 'RELGASTOS';
		}

		var vacio = true;
		var cont  = $('#dt_RelacionGastosProveedor input:checked').length;
		var fPago = $("#fechaAplicacion").val();
		var folios = "";
		var id_CXP = "";
		var token = "";
		var usuario = $("#u_Login").val();
		var tListado = $("#dt_RelacionGastosProveedor").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_RelacionGastosProveedor');
		var cuentaBanc = $("#ctaBancarias").val();
		
		$("#btn_Aplicar").attr("disabled","disabled");
		$("#nFolios").val("");
		
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				folios += token + tListado[i][2];
				token = ", ";
				vacio = false;
			}
		}
		
		if (!vacio){
		
			/*$('#dt_RelacionGastosProveedor input:checked').each(function(idx, elm) {
				folios += token + $(this).attr("id");
				token = ", ";
			});*/
			
			$("#nFolios").val(folios);
			
			$("#sTipoLayout").val("RG");
			$("#nFoliosLayout").val(folios);
			$("#sCuentaLayout").val(cuentaBanc);
			
			if ($("#chk_Cheques").prop("checked") && cont > 1){
				Swal.fire({ icon: "warning",
							text: "En el caso de Cheque unicamente debes seleccionar un registro."});
				document.getElementById("btn_Aplicar").disabled = false;
				return;
			}
			
			/*if (!$("#chk_Cheques").prop("checked")){
				Swal.fire({ icon: "info",
							text: "Se aplicara contablemente los siguientes folios "+ folios + "."});					
			}*/
			
			var esPagoParcial = ( $("#pagoParcial").attr("checked")?"S" : "N" );
			
			$.ajax({
				url:'./ejercidoPagadoValidar.jsp',
				type:'post',
				data:{tipo:'aplicacionManualNuevo', idFolio:folios, tabla:nomTabla, tablaDet:nomTablaDet, fPago:fPago, usuario:usuario, ctab:cuentaBanc,parcial:esPagoParcial },				
				success: function(data){
					if(data.estatus == "correcto"){
						if ($("#chk_Cheques").prop("checked")){
							queryFormPost("leeCXPCheque",{async: false });
							Swal.fire({ icon: "info",
										text: "Se imprimira el Cheque de la RG "+folios});
							window.open(
								"../reportes/ImpresionCheque?"
								+"&folio="+$("#cxpCheque").val()
								+"&usuario="+login
								+"&tipo="+1
								+"&rn=ChequeManual.jasper",
								 'Procesando', 'status=1');
						}
						
					Swal.fire({ icon: "info",
						text: "Se aplicaron los siguientes folios: "+folios+"."});
																							
					}else{
						Swal.fire({ icon: "warning",
							text: "No se termino de aplicar el Ejercido/Pagado. [" + data.estatus + "]"});						
					}
					generaLayoutBancario(pago);
					
					cargaGrid();
				},
				error : function(xhr, textStatus, errorThrown) {
					Swal.fire({ icon: "error",
								text: "Ocurrio un error, favor de intentar nuevamente"});
					cargaGrid();
				}
			});		
		}else{
			document.getElementById("btn_Aplicar").disabled = false;				
			Swal.fire({ icon: "warning",
						text: "Seleccione al menos un Registro."});
			return;
		}
			
	}
	
	function setSequenceValE(seqValue) {
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioEjercido").val( seqValue);
	}
	function setSequenceValP(seqValue) {
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioPagado").val(seqValue);
	}
	
	function reimprimir(){
		var vacio = true;
		var tListado = $("#dt_ChequesElaborados").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_ChequesElaborados');
		var id_CXP = "";
		var cont = 0;
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				id_CXP=tListado[i][2];
				vacio = false;
				cont++;
			}			  
		}
		if (!vacio){
			if (cont>1){
				Swal.fire({ icon: "warning",
							text: "Favor de seleccionar un solo Cheque."});
				return;
			}
			window.open(
				"../reportes/ImpresionCheque?"
				+"&folio="+id_CXP
				+"&usuario="+login
				+"&tipo="+2
				+"&rn=ChequeManual.jasper",
				 'Procesando', 'status=1');
		}
	}
	
	function remplazar(){
	//alert(login);
		var vacio = true;
		var tListado = $("#dt_ChequesElaborados").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_ChequesElaborados');
		var id_CXP = "";
		var cont = 0;
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				$("#cancelarCheque").val(tListado[i][3]);//CXP
				id_CXP=tListado[i][2];
				vacio = false;
				cont++;
			}			  
		}
		if (!vacio){
			if (cont>1){
				Swal.fire({ icon: "warning",
							text: "Favor de seleccionar un solo Cheque."});
				return;
			}

			else {
				//if(confirm("¿Esta seguro que desea REEMPLAZAR el cheque?"))	{
				Swal.fire({
					  text: "¿Esta seguro que desea REEMPLAZAR el cheque?",					  
					  icon: 'info',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {						
						$.ajax({
							url : '../reportes/ImpresionCheque',
							dataType : 'json',
							type : "GET",
							data : {
								 "folio" : id_CXP
								,"tipo" : "3"
							},
							async : true,
							success : function(json) {
								var exito = json.success;
								if (exito == "true") {
									r = json.data_1.result;						
									Swal.fire({ icon: "info",
												text: "Se aplico contablemente. Nuevo numero de cheque " + r});
									cargaChequesGenerados();
								
									window.open(
											"../reportes/ImpresionCheque?"
											+"&folio="+r
											+"&tipo="+2
											+"&usuario="+login
											+"&rn=ChequeManual.jasper",
											 'Procesando', 'status=1'
									);
								
								} else {
									Swal.fire({ icon: "info",
												text: json.data_1.result});
								}
							},
							error : function(xhr, textStatus, errorThrown) {
								Swal.fire({ icon: "warning",
											text: "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown});					
								r = true;
							}
						});		
					}
				})						
			}
		}
	}
	
	
	function imprimeChequeCaja(){
		var vacio = true;
		var tListado = $("#dt_ChequesCaja").dataTable().fnGetData();
		var tListado2 = document.getElementById('dt_ChequesCaja');
		var beneficiario="";
		var cont = 0;
		var tipo=0;
	        
		for (i=0; i<tListado.length;i++){
			var row = tListado2.rows[i+1];
			var chkbox = row.cells[0].childNodes[0];
			if(null != chkbox && true == chkbox.checked){
				id_Caja=tListado[i][2];
				beneficiario=tListado[i][3];
				evento=tListado[i][9];
				nombre=tListado[i][10];
				vacio = false;
				cont++;
			}			  
		}
		$("#nombre").val(nombre);
		if (!vacio){
			if (cont>1){
				Swal.fire({ icon: "warning",
							text: "Favor de Seleccionar un solo Cheque."});
				return;
			}
			
			if(beneficiario==''){
			//alert (" IMPRIMIR UN CHEQUE SIN UN RFC REGISTRADO");
				modalBeneficiario.show();			
				return;
			}
			else if(evento=="8_2_1"){
				//$( "#dialog-CambioBeneficiario" ).dialog( "open" );
				modalCambioBeneficiario.show();
			}
			else{
					 if(evento=="8_2_3")// evento de LAUDOS para que el cheque se emite a otro nombre
						tipo=6;
					else
						tipo=4;
							
					Swal.fire({ icon: "info",
								text: "Se imprimirà el Cheque de la SOLICITUD NO PRESUPUESTAL con folio: "+id_Caja+"."});
					window.open(
						"../reportes/ImpresionCheque?"
						+"&folio="+id_Caja
						+"&usuario="+login
						+"&tipo="+tipo
						+"&rn=ChequeManual.jasper",
						 'Procesando', 'status=1');
				cargaDataTableCaja();
		}
		
	}//	
	}
	
	function cargaDetalleCXPFuera(condicion){
		$("#dt_CXPfuera").dataTable({
	        "bPaginate": false,
	        //"iDisplayLength":"10",
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"sScrollY": "100%",
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true,	
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_AplicarEjercidoPagadoDetalle&qw=" + condicion,
			aoColumns: [
				{ sName: "EP",				bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignLeft"},
				{ sName: "mImporteMasIva",	bSearchable: false,	bSortable: true,  bVisible: true, sClass: "alignRight"},
				{ sName: "cMes",			bSearchable: false,	bSortable: true,  bVisible: true, sClass: "aligCenter"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
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
	}
	
	function buscarporFuera(){
		$("#nFolio").val("");
		$("#tipoPagoCXP").val("");
		$("#urCXP").val("");
		$("#mImporteCXP").val("");
		$("#rfcCXP").val("");
		$("#nombreCXP").val("");
		$("#fAplicacionDevCXP").val("");
		queryFormPost("buscaCXPporFuera",{async: false });		
		if ($("#fAplicacionDevCXP").val()!=""){
			var cFecha = $("#fAplicacionDevCXP").val().split("-")[2] + "/" + $("#fAplicacionDevCXP").val().split("-")[1] + "/" + $("#fAplicacionDevCXP").val().split("-")[0];
			$("#fAplicacionDevCXP").val(cFecha);
		}
		$("#mImporteCXP").formatCurrency();
		var condicion = "cTipoPago = '"+$("#tipoPagoCXP").val()+"' AND nFolio = "+$("#nFolio").val();
		//cargaDetalleCXPFuera(condicion);
		if($("#nFolio").val() != "" ){
			$("#divCtab").show();	
		} else {
			$("#divCtab").hide();
		}
	}
	function validaFechas(primera, segunda, tipo){
		var fechaValida = null;
		var dDay=Number(primera.substring(0, 2));
		var dMonth=Number(primera.substring(3, 5));  
		var dYear=Number(primera.substring(6,10));  
		var aDay=Number(segunda.substring(0, 2));  
		var aMonth=Number(segunda.substring(3, 5));  
		var aYear=Number(segunda.substring(6,10));
		if (dYear> aYear)
            fechaValida=false;
        else
         	if (dYear == aYear)
         		if (dMonth > aMonth)
         			fechaValida=false;
            	else
              		if (dMonth == aMonth)
                		if (dDay > aDay)
                  			fechaValida=false;
                		else
                  			fechaValida=true;
              		else
                		fechaValida=true;
          	else
            	fechaValida=true;
        if (!fechaValida){        	
        	Swal.fire({ icon: "warning",
						text: "La fecha de " + tipo + " no puede ser menor a " + primera});
        	return false;
        }else
        	return true;
    }
	
	function aplicarCXP(){
		if ($.trim($("#cxpPorFuera").val())==""){
			Swal.fire({ icon: "warning",
						text: "Favor de indicar la CXP que se va aplicar."});
			return;
		}else if($.trim($("#tipoPagoCXP").val())==""){
			Swal.fire({ icon: "warning",
						text: "No existe la CXP: "+$("#cxpPorFuera").val()});
			return;
		}else if (($.trim($("#folioSicop").val())=="" || $.trim($("#folioSicop").val())=="0" 
			|| $.trim($("#solPago").val())=="" || $.trim($("#solPago").val())=="0"
			|| $.trim($("#procesoSicop").val())=="" || $.trim($("#procesoSicop").val())=="0"
			|| $.trim($("#folioSiaff").val())=="" || $.trim($("#folioSiaff").val())=="0") 
			&& ($("#tipoPagoCXP").val()!="INTEGRACION")){
			Swal.fire({ icon: "warning",
						text: "Favor de verificar que los folios sean validos."});
			return;
		}
		
		//Validacion de fechas de aplicacion
		if ($("#tipoPagoCXP").val()!="INTEGRACION"){
			if (!validaFechas($("#fAplicacionDevCXP").val(),$("#ejercidoCXP").val(),"Ejercido"))
				return;
			if (!validaFechas($("#ejercidoCXP").val(),$("#pagadoCXP").val(),"Pagado"))
				return;				
		}else
			if (!validaFechas($("#fAplicacionDevCXP").val(),$("#pagadoCXP").val(),"Integracion"))
				return;
		
		//if (confirm("¿Desea aplicar el Ejercido/Pagado de "+$("#cxpPorFuera").val()+"?")){
		Swal.fire({
			  html: "¿Desea aplicar el Ejercido/Pagado de " + $("#cxpPorFuera").val() + "?",					  
			  icon: 'info',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {				
				document.getElementById("esperar").style.visibility = 'visible';
				$("#cxpPorFuera").attr('disabled', true);
				$("#btn_AplicarCXP").attr("disabled","disabled");
				$.ajax({
					url:'./ejercidoPagadoValidar.jsp',
					type:'post',
					data:{
						tipo		: 'AplicarPagosFuera', 
						nFolio		: $("#nFolio").val(),
						idCXP		: $("#cxpPorFuera").val(),
						tipoPago	: $("#tipoPagoCXP").val(),
						fPago		: $("#pagadoCXP").val(),
						fEjer		: $("#ejercidoCXP").val(),
						usuario		: $("#u_Login").val(),
						folioSICOP	: $.trim($("#folioSicop").val()),
						solPago		: $.trim($("#solPago").val()),
						numProceso	: $.trim($("#procesoSicop").val()),
						folioSIAFF	: $.trim($("#folioSiaff").val()),
						CTAB		: $.trim($("#ctaBancariasLAUDOS").val())
					},
					success: function(data){
						if(data.estatus == "correcto"){
							Swal.fire({ icon: "success",
										text: "Se aplico correctamente el Ejercido/Pagado"});
							$("#cxpPorFuera").val("");
							$("#nFolio").val("");
							$("#tipoPagoCXP").val("");
							$("#urCXP").val("");
							$("#mImporteCXP").val("");
							$("#rfcCXP").val("");
							$("#nombreCXP").val("");
							$("#folioSicop").val("");
							$("#solPago").val("");
							$("#procesoSicop").val("");
							$("#folioSiaff").val("");
							$("#fAplicacionDevCXP").val("");
						}else{
							Swal.fire({ icon: "error",
										text: "No se pudo aplicar el Ejercido/Pagado. [" + data.estatus + "]"});						
						}
						document.getElementById("esperar").style.visibility = 'hidden';
						document.getElementById("btn_AplicarCXP").disabled = false;
						$("#cxpPorFuera").attr('disabled', false);
					}
				});
			}
		})
	}
	function borraEspacios(){
		$("#cxpPorFuera").val($.trim($("#cxpPorFuera").val()));
		buscarporFuera();
		document.getElementById("folioSicop").focus();
	}
	
	function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}
	
	function validarNumerico(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		patron = /[\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	
	function generaLayoutBancario(tipoPago){
		var folios = "";
		var token = "";
		var vacio = true;
		
		if (tipoPago=="RELGASTOS"){
			vacio = $('#dt_RelacionGastosProveedor input:checked').length == 0;
			if (!vacio){
				$('#dt_RelacionGastosProveedor input:checked').each(function(idx, elm){
					folios += token + $(this).attr("id");
					token = ", ";
				});
				
				$("#nFoliosLayout").val(folios);
				$("#sTipoLayout").val("RG");
				$("#sCuentaLayout").val($("#ctaBancarias").val());
				$("#layoutBanco").submit();
			}else{
				Swal.fire({ icon: "warning",
							text: "Seleccione al menos un Registro."});
				return;
			}
		}else if (tipoPago=="CAJA"){
			vacio = $('#dt_AnticipoCaja input:checked').length == 0;
			if (!vacio){
				$('#dt_AnticipoCaja input:checked').each(function(idx, elm){
					folios += token + $(this).attr("id");
					token = ", ";
				});
				
				$("#nFoliosLayout").val(folios);
				$("#sTipoLayout").val("CAJA");
				$("#sCuentaLayout").val($("#ctaBancariasAnticipoCaja").val());
				$("#layoutBanco").submit();
								
				setTimeout("cargaGridAnticipoCaja()",5000) 
			}else{
				Swal.fire({ icon: "warning",
							text: "Seleccione al menos un Registro."});
				return;
			}					
			
		}else if (tipoPago="RADICADO"){
			vacio = $('#dt_PagosDiversosRG input:checked').length == 0;
			if (!vacio){
				$('#dt_PagosDiversosRG input:checked').each(function(idx, elm){
					folios += token + $(this).attr("id");
					token = ", ";
				});
				
				$("#nFoliosLayout").val(folios);
				
				$("#cTipoPagoRad").val(document.getElementById("tipoPagRadicado").value);
		
				if ($("#cTipoPagoRad").val()== 1){
					$("#sTipoLayout").val("PDIVRG");
				}else if ($("#cTipoPagoRad").val() ==2){
					$("#sTipoLayout").val("PO");
				}else if ($("#cTipoPagoRad").val() == 3){
					$("#sTipoLayout").val("RG");
				}
				cargaGridPDRG();
				$("#sCuentaLayout").val($("#ctaBancariasPDRG").val());
				$("#layoutBanco").submit();
			}else{				
				Swal.fire({ icon: "warning",
							text: "Seleccione al menos un Registro."});
				return;
			}
		}					
				
	}
	
	function validarFechaAplicacion(fCapturada){
			
		var fActual = "<%=today%>";  
			
		var x = fActual.split("/");
	    var z = fCapturada.split("/");
	    
	    fActual = x[2]+x[1]+x[0];
	    fCapturada = z[2]+z[1]+z[0];
	
	    //Comparamos las fechas
	    if (fCapturada > fActual){	    	
	    	Swal.fire({ icon: "warning",
						text: "La fecha seleccionada no puede ser Mayor a la fecha Actual. Verifique!!"});
	        $("#fechaAplicacion").val("<%=today%>");
	        $("#fechaAplicacionISR").val("<%=today%>");
	        $("#ejercidoCXP").val("<%=today%>");
	        $("#pagadoCXP").val("<%=today%>");
	        $("#fechaPagadoPD").val("<%=today%>");
	    }		
	}
	
	function aceptarBeneficiario(){
		window.open(
			"../reportes/ImpresionCheque?"
			+"&folio="+id_Caja
			+"&usuario="+login
			+"&tipo="+4
			+"&rn=ChequeManual.jasper",
			 'Procesando', 'status=1');
			cargaDataTableCaja();
			modalCambioBeneficiario.hide();
	}
	
	function cambiaBeneficiario(){
		modalCambioBeneficiario.hide();	
		modalBeneficiario.show();
	}
	
	function chequeNvoBeneficiario(){	
		if($("#cNombreBeneficiario").val() == ""){ 
			Swal.fire({ icon: "warning",
						text: "Falta Ingresar el nombre del Beneficiario"});
			return; 
		} 
		else if($("#cPaternoBeneficiario").val() == ""){ 
			Swal.fire({ icon: "warning",
						text: "Falta Ingresar el  Apellido Paterno del Beneficiario"});
			return; 
		}							
		BeneficiarioNuevo=$("#cNombreBeneficiario").val()+' '+$("#cPaternoBeneficiario").val()+' '+$("#cMaternoBeneficiario").val();	
		
		window.open(
			"../reportes/ImpresionCheque?"
			+"&folio="+id_Caja
			+"&usuario="+login
			+"&BeneficiarioNuevo="+BeneficiarioNuevo
			+"&tipo="+5
			+"&rn=ChequeManual.jasper",
			 'Procesando', 'status=1');
		cargaDataTableCaja();
		modalBeneficiario.hide();
	}
	
	function upperCase(e) {		
		e.value = e.value.toUpperCase();
	}
	
</script>
</head>
<br/>
<body id="dt_example" >
	<div id="container" class="ms-5" class="container" style="width: 90%">				
		<div class="card-header"> <h3> Ejercido Pagado Manual </h3> </div>
		<hr class="mt-3"/>
			
		<form method="post" id="layoutBanco" name="layoutBanco" action="../gstnmngr/generaLayoutBancoRG">
			<input id="nFoliosLayout" 	name="nFoliosLayout" 	type="hidden" value="">
			<input id="sTipoLayout" 	name="sTipoLayout" 		type="hidden" value="">
			<input id="sCuentaLayout" 	name="sCuentaLayout" 	type="hidden" value="">
		</form>
				
		<form id="ejercidoPagadoCheque" name="ejercidoPagadoCheque">
		
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
			<input id="cUnidadResponsable" name="cUnidadResponsable" type="hidden" value="<%=cUR%>">
			<input id="cCentroContable" name="cCentroContable" type="hidden" value="<%=cCentroContable%>">
			<input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'">			
			<input id="nFolios" name="nFolios" type="hidden" value="">
			
			<input id="nFolioEjercido" name="nFolioEjercido" type="hidden" value="">
			<input id="nFolioPagado" name="nFolioPagado" type="hidden" value="">
			<input id="foliocxp" name="foliocxp" type="hidden" value="">
			<input id="ExisteReg" name="ExisteReg" type="hidden" value="0">
			<input id="accion" name="accion" type="hidden" value="">
			<input id="statusE" name="statusE" type="hidden" value="">
			<input id="statusP" name="statusP" type="hidden" value="">
			<input id="nFolio" name="nFolio" type="hidden" value="">
			<input id="cxpCheque" name="cxpCheque" type="hidden" value="">
			
			<!-- Tipo Pago Radicado -->
			<input id="cTipoPagoRad" name ="cTipoPagoRad" type="hidden" value=""> 
						
			<input id="cancelarCheque" name="cancelarCheque" type="hidden" value="">
			<input id="aplicarCheque" name="aplicarCheque" type="hidden" value="">
			
			<!-- Tipo de pago Anticipo de Caja -->
			<input id="cWhereAnticipo" name="cWhereAnticipo" type="hidden" value="">			
			
			
				<div class="row d-flex justify-content-center">
	      							
	       			<ul class="nav nav-tabs" id="list-opciones">
	       				<li class="nav-item" role="presentation">
		            		<button class="nav-link active" id="LNK01" data-bs-toggle="tab" data-bs-target="#tabs-1" type="button" role="tab" aria-controls="tabs-LNK01" aria-selected="true">Aplicar</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK02" data-bs-toggle="tab" data-bs-target="#tabs-2" type="button" role="tab" aria-controls="tabs-LNK02" aria-selected="false">Generar Cheque de CAJA</button>
			            </li>								     
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK03" data-bs-toggle="tab" data-bs-target="#tabs-3" type="button" role="tab" aria-controls="tabs-LNK03" aria-selected="false">Cheques Generados</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK04" data-bs-toggle="tab" data-bs-target="#tabs-4" type="button" role="tab" aria-controls="tabs-LNK04" aria-selected="false">Cheques Cancelados</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK05" onclick="cargaISRLaudos();" data-bs-toggle="tab" data-bs-target="#tabs-5" type="button" role="tab" aria-controls="tabs-LNK05" aria-selected="false">ISR Laudos</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK06" data-bs-toggle="tab" data-bs-target="#tabs-6" type="button" role="tab" aria-controls="tabs-LNK06" aria-selected="false">Por Fuera</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK07" data-bs-toggle="tab" data-bs-target="#tabs-7" type="button" role="tab" aria-controls="tabs-LNK07" aria-selected="false">Pagos Diversos RG</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="LNK08" data-bs-toggle="tab" data-bs-target="#tabs-8" type="button" role="tab" aria-controls="tabs-LNK08" aria-selected="false">Anticipos de Caja</button>
			            </li>
					</ul>
					
					<div align="center">
							<label id="esperar" style="visibility: hidden">	Espere por favor...
								<img border="0" src="../imagenes/espera.gif" height="30">
							</label>
					</div>
					
				<div class="tab-content mt-3" id="tabContent">			
					<div class="tab-pane fade show active" id="tabs-1" role="tabpanel" aria-labelledby="tabs-LNK01">
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="ctaBancarias" class="form-label"> Cuenta Bancaria: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<select id="ctaBancarias" name="ctaBancarias" class="form-select form-select-sm" onchange="cargaGrid();">				            		
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fechaAplicacion" class="form-label"> Fecha de aplicaci&oacute;n: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input name="fechaAplicacion" type="date" id="fechaAplicacion" class="form-control form-control-sm" size="10" />
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<span id="pagoParcialSpan" style="display: none;"> <input type="checkbox" id="pagoParcial" name="pagoParcial" class="form-check-input" value="S" >Pago parcial </span>
							</div>
						</div>
						
						<div class="row d-flex">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="chk_Cheques" class="form-label"> Cheque: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="checkbox" id="chk_Cheques" name="chk_Cheques" class="form-check-input" value="1" >						            
							</div>
						</div>

						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_RelacionGastosProveedor" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th><font size="2"></font></th>
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">CXP</font></th>
												<th><font size="2">Folio Integracion</font></th>
												<th><font size="2">RFC</font></th>
												<th><font size="2">Nombre</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Cuenta Bancaria</font></th>
												<th><font size="2">Tipo Destino</font></th>						
											</tr>
										</thead>
									</table>	
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<!-- <input type="button" id="btn_Aplicar" name = "btn_Aplicar" onClick="AplicarEjercidoPagado();" value="Aplicar" class="btn btn-secondary btn-sm"/>							
								<input type="button" id="btn_layoutBancarioRG" name = "btn_layoutBancarioRG" onclick="generaLayoutBancario('RELGASTOS');" value="Layout Bancario" class="btn btn-secondary btn-sm"/> -->
								<input type="button" id="btn_layoutBancarioRG" name = "btn_layoutBancarioRG" onClick="AplicarEjercidoPagado();" value="Layout Bancario y Pagado" class="btn btn-secondary btn-sm"/>
							</div>
						</div>

					</div>
					
					<div class="tab-pane fade show" id="tabs-2" role="tabpanel" aria-labelledby="tabs-LNK02">
			  			<input name="cWhereCaja" type="hidden" id="cWhereCaja">
			  				
		  				<h5> Datos de la  B&uacute;squeda </h5>
						<hr class="mt-3"/>
								
		  				<div class="row d-flex">								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="folioCaja" class="form-label"> Folio de la Solicitud NO Presupuestal: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<input type="text" name="folioCaja" id="folioCaja" class="form-control form-control-sm" onkeyup="cargaDataTableCaja()"/>
							</div>
							<div class="col-12 col-lg-9 col-md-9 col-sm-12 p-1">							
								<input type="button" id="btn_busca" name = "btn_busca" onClick="cargaGrid();" value=Buscar class="btn btn-secondary btn-sm"/>							
								<input type="button" id="btn_limpia" name = "btn_limpia" onclick="limpiarDatos();" value="Limpiar" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
					
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_ChequesCaja" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th><font size="2"></font></th>
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">Beneficiario</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Cuenta Bancaria</font></th>
												<th><font size="2">Banco</font></th>
												<th><font size="2">Fecha</font></th>			
												<th><font size="2">Evento</font></th>
												<th><font size="2">Nombre</font></th>			
											</tr>
										</thead>
									</table> 
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<input type="button" id="btn_imprimeCheque" name = "btn_imprimeCheque" onClick="imprimeChequeCaja();" value="Imprimir Cheque" class="btn btn-secondary btn-sm"/>															
							</div>
						</div>
						
					</div>
					
					<div class="tab-pane fade show" id="tabs-3" role="tabpanel" aria-labelledby="tabs-LNK03">
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_ChequesElaborados" class="table table-striped table-bordered">									
										<thead>
											<tr>
												<th><font size="2"></font></th>
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">CXP</font></th>
												<th><font size="2">Beneficiario</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Cuenta Bancaria</font></th>
												<th><font size="2">Banco</font></th>
												<th><font size="2">Fecha</font></th>						
											</tr>
										</thead>
									</table> 
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<input type="button" id="btn_Reimprimir" name = "btn_Reimprimir" onClick="reimprimir();" value="Reimprimir" class="btn btn-secondary btn-sm"/>															
								<input type="button" id="btn_Reemplazar" name = "btn_Reemplazar" onClick="remplazar();" value="Reemplazar" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
					</div>
					
					<div class="tab-pane fade show" id="tabs-4" role="tabpanel" aria-labelledby="tabs-4-LNK04">
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_ChequesCancelados" class="table table-striped table-bordered">							
										<thead>
											<tr>												
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">CXP</font></th>
												<th><font size="2">Beneficiario</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Motivo</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Cuenta Bancaria</font></th>
												<th><font size="2">No.</font></th>
												<th><font size="2">Banco</font></th>
												<th><font size="2">Fecha</font></th>						
											</tr>
										</thead>
									</table> 
								</div>
							</div>						
						</div>
					</div>
					
					<div class="tab-pane fade show" id="tabs-5" role="tabpanel" aria-labelledby="tabs-4-LNK05">
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="ctasBancariaLaudos" class="form-label"> Cuenta Bancaria: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<select id="ctasBancariaLaudos" name="ctasBancariaLaudos" class="form-select form-select-sm" onchange="cambiaCuentaISR();">				            		
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fechaAplicacionISR" class="form-label"> Fecha de aplicaci&oacute;n: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input name="fechaAplicacionISR" type="date" id="fechaAplicacionISR" class="form-control form-control-sm" size="10" value="<%=fAplicacion[0]%>" />
								</div>						            
							</div>
						</div>
						
						<div class="row d-flex">							
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="checkAll" class="form-label"> Todos: </label>
								<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input" >
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_ISRLaudos" class="table table-striped table-bordered">								
										<thead>
											<tr>
												<th><font size="2"></font></th>
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">CXP</font></th>
												<th><font size="2">RFC</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Fecha Captura</font></th>						
											</tr>
										</thead>
									</table> 
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<input type="button" id="btn_AplicarISR" name = "btn_AplicarISR" onClick="aplicarISRLaudos();" value="Aplicar" class="btn btn-secondary btn-sm"/>																							
							</div>
						</div>
					</div>
					
					<div class="tab-pane fade show" id="tabs-6" role="tabpanel" aria-labelledby="tabs-6-LNK06">
						<h5> Pagos por Fuera </h5>
						<hr class="mt-3"/>
						
						<div id="divCtab">
							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="ctaBancariasLAUDOS" class="form-label"> Cuenta Bancaria: </label>
								</div>
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
									<select id="ctaBancariasLAUDOS" name="ctaBancariasLAUDOS" class="form-select form-select-sm" onchange="cargaGrid();">				            		
						            </select>
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="cxpPorFuera" class="form-label"> CXP: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="cxpPorFuera" name="cxpPorFuera" class="form-control form-control-sm" onchange="borraEspacios();">				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="ejercidoCXP" class="form-label"> Ejercido: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input name="ejercidoCXP" type="date" id="ejercidoCXP" class="form-control form-control-sm" size="10" value="<%=fAplicacion[0]%>" />
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="pagadoCXP" class="form-label"> Pagado: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input name="pagadoCXP" type="date" id="pagadoCXP" class="form-control form-control-sm" size="10" value="<%=fAplicacion[0]%>" />
								</div>						            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="tipoPagoCXP" class="form-label"> Tipo: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="tipoPagoCXP" name="tipoPagoCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="urCXP" class="form-label"> UR: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="urCXP" name="urCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fAplicacionDevCXP" class="form-label"> Fecha: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="fAplicacionDevCXP" name="fAplicacionDevCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="mImporteCXP" class="form-label"> Importe: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="mImporteCXP" name="mImporteCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="rfcCXP" class="form-label"> RFC: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="rfcCXP" name="rfcCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="nombreCXP" class="form-label"> Nombre: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<input type="text" id="nombreCXP" name="nombreCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="folioSicop" class="form-label"> SICOP: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="folioSicop" name="folioSicop" class="form-control form-control-sm" onkeypress="return validarNumerico(event);">				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="solPago" class="form-label"> SOL PAGO: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="solPago" name="solPago" class="form-control form-control-sm" onkeypress="return validarNumerico(event);">				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="procesoSicop" class="form-label"> PROCESO: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="procesoSicop" name="procesoSicop" class="form-control form-control-sm" onkeypress="return validarNumerico(event);">				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="procesoSicop" class="form-label"> SIAFF: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="folioSiaff" name="folioSiaff" class="form-control form-control-sm" onkeypress="return validarNumerico(event);">				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">			
								<input type="button" id="btn_AplicarCXP" name = "btn_AplicarCXP" onClick="aplicarCXP();" value="Aplicar" class="btn btn-secondary btn-sm"/>										            							           
							</div>
						</div>											
					</div>
					
					<div class="tab-pane fade show" id="tabs-7" role="tabpanel" aria-labelledby="tabs-4-LNK07">
						<div class="row d-flex">															
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="ctaBancariasPDRG" class="form-label"> Cuenta Bancaria: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<select id="ctaBancariasPDRG" name="ctaBancariasPDRG" class="form-select form-select-sm" onchange="cargaGridPDRG();">				            		
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="tipoPagRadicado" class="form-label"> Tipo Pago: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<select id="tipoPagRadicado" name="tipoPagRadicado" class="form-select form-select-sm" onchange="cargaGridPDRG();">		
									 <option value = "1" >Pago Diverso</option>
									 <option value = "2" >Pago Obra</option>
									 <option value = "3" >Relacion Gastos</option>
									 <option value = "4" >Pago Penas</option>		            		
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fechaPagadoPD" class="form-label"> Pagado: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input name="fechaPagadoPD" type="date" id="fechaPagadoPD" class="form-control form-control-sm" size="10" value="<%=fAplicacion[0]%>"/>
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<span id="pagoParcialSpanRG" style="display: none;"> <input type="checkbox" id="pagoParcialRG" name="pagoParcialRG" class="form-check-input" value="S" >Pago parcial </span>
							</div>
						</div>
						
						<div class="row d-flex">							
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="lblCheque" class="form-label"> Cheque: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="checkbox" id="chk_Cheques" name="chk_Cheques" class="form-check-input" value="1" >						            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_PagosDiversosRG" class="table table-striped table-bordered" style="width: 100%">									
										<thead>
											<tr>
												<th><font size="2"></font></th>
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">CXP</font></th>
												<th><font size="2">Folio Integracion</font></th>
												<th><font size="2">RFC</font></th>
												<th><font size="2">Nombre</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Cuenta Bancaria</font></th>
												<th><font size="2">Tipo Destino</font></th>						
											</tr>
										</thead>
									</table>	
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<!-- <input type="button" id="btn_AplicarPDRG" name = "btn_AplicarPDRG" onClick="AplicarPagosDiversosRG();" value="Aplicar" class="btn btn-secondary btn-sm"/>															
								<input type="button" id="btn_layoutBancarioPDRG" name = "btn_layoutBancarioPDRG" onClick="generaLayoutBancario('PAGODIV');" value="Layout Bancario" class="btn btn-secondary btn-sm"/> -->
								<input type="button" id="btn_layoutBancarioPDRG" name = "btn_layoutBancarioPDRG" onClick="AplicarPagosDiversosRG();" value="Layout Bancario y Pagado" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
					</div>
					
					<div class="tab-pane fade show" id="tabs-8" role="tabpanel" aria-labelledby="tabs-8-LNK08">
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="ctaBancariasAnticipoCaja" class="form-label"> Cuenta Bancaria: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<select id="ctaBancariasAnticipoCaja" name="ctaBancariasAnticipoCaja" class="form-select form-select-sm" onchange="cargaGridAnticipoCaja();">				            		
					            </select>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<div class="table-responsive">	    
									<table id="dt_AnticipoCaja" class="table table-striped table-bordered">										
										<thead>
											<tr>
												<th><font size="2"></font></th>
												<th><font size="2">UE</font></th>
												<th><font size="2">Folio</font></th>
												<th><font size="2">RFC</font></th>
												<th><font size="2">Nombre</font></th>
												<th><font size="2">Concepto</font></th>
												<th><font size="2">Importe</font></th>
												<th><font size="2">Cuenta Bancaria</font></th>
												<th><font size="2">Banco</font></th>
												<th><font size="2">Fecha</font></th>
												<th><font size="2">Cuenta Empleado</font></th>				
												<th><font size="2">Evento</font></th>												
											</tr>
										</thead>
									</table> 
								</div>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<input type="button" id="btn_layoutBancarioAnticipoCaja" name = "btn_layoutBancarioAnticipoCaja" onClick="generaLayoutBancario('CAJA');" value="Layout Bancario" class="btn btn-secondary btn-sm"/>																							
							</div>
						</div>						
					</div>
				</div>
	       </div>			
		</form>
	</div>

	<div class="modal fade" id="dialog-Beneficiario" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
		<div class="modal-dialog"> <!-- Caja de dialogo -->
			<div class="modal-content"> <!-- Contenido de la caja -->
				<div class="modal-header"> <!-- Encabezado de la caja -->
					<h5 class="modal-title">Datos del Beneficiario</h5>
					<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
				</div>
			<div class="modal-body"> <!-- Cuerpo de la caja -->
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="cNombreBeneficiario" class="form-label"> Nombre: </label>            
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="cNombreBeneficiario" name="cMaternoBeneficiario" size=40 onkeyup="upperCase(this);" >			            
					</div>
				</div>
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="cPaternoBeneficiario" class="form-label"> Apellido Paterno: </label>			               
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="cPaternoBeneficiario" name="cPaternoBeneficiario" size=40 onkeyup="upperCase(this);" >			               
					</div>
				</div>
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="cMaternoBeneficiario" class="form-label"> Apellido Materno: </label>			              
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="cMaternoBeneficiario" name="cMaternoBeneficiario" size=40 onkeyup="upperCase(this);" >			              
					</div>
				</div>			
			</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="imprimeChequNvoBeneficiario" class="btn btn-primary btn-sm" onclick="chequeNvoBeneficiario();" >Aceptar</button>
				<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal" >Cancelar</button>										    
			</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="dialog-CambioBeneficiario" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
		<div class="modal-dialog"> <!-- Caja de dialogo -->
			<div class="modal-content"> <!-- Contenido de la caja -->
				<div class="modal-header"> <!-- Encabezado de la caja -->
					<h5 class="modal-title">Datos del Cheque</h5>
					<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
				</div>
			<div class="modal-body"> <!-- Cuerpo de la caja -->
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<label for="cheque" class="form-label"> El Cheque sera impreso a Nombre de: </label>			            
					</div>
				</div>
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="nombre" name="nombre" size=50  value="" readonly>			            
					</div>
				</div>
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" align="right">
						<label for="cambioDatos" class="form-label"> ¿Desea cambiar los datos? </label>			            
					</div>
				</div>
			</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="cambiaBeneficiario" class="btn btn-primary btn-sm" onclick="cambiaBeneficiario();" >Si</button>
				<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal" onclick="aceptarBeneficiario();">No</button>										    
			</div>
			</div>
		</div>
	</div>
		
</body>
</html>