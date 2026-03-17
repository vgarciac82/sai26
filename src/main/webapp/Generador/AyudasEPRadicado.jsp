<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	
	String today = Util.getToday();
	EjercicioFiscalBusinessLogic ebl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String aEjercicioFiscal = ebl.getEjercicioFiscalActivo().getaEjercicioFiscal();
		
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String UE=usuario.getU_UR();
	String login=usuario.getLogin();
		boolean mntoCuentas=false;
	 
	if( usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE") ){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Catálogo de Estructura Programática</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/css/demo_page.css";
		</style>

		<script type="text/javascript" src="../jq/js/jquery.js"></script>
		<script type="text/javascript" src="../jq/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" charset="UTF-8">
		
		var oTable, oCurrentFocus;
		var UE = "<%= usuario.getU_UR()%>";
		var Bandera="";
		
		$(document).ready(
			function() {
				
				oTableSaldos = $("#tblSaldos").dataTable({
								bPaginate : false,
								bLengthChange : false,
								bInfo : false,
								bAutoWidth : false,				
								sScrollX: "100%",
								bJQueryUI: true,
								bFilter : false,
								bSort : false,
								bInfo : false,
								bAutoWidth : false,
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
									oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
								},
								aoColumns: [
									{ sName: "Folio",		bSortable: false },
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
			
				oTableDetalle = $("#tblDetalle").dataTable({
								bPaginate : false,
								bLengthChange : false,
								bInfo : false,
								bAutoWidth : false,				
								sScrollY: "100%",
								bJQueryUI: true,
								bFilter : false,
								bSort : false,
								bInfo : false,
								bAutoWidth : false,
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
									oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
								},
								aoColumns: [
									{ sName: "Folio",	bSortable: false },
									{ sName: "EP",		bSortable: false },
									{ sName: "Codigo",	bSortable: false },
									{ sName: "Mes",		bSortable: false },
									{ sName: "Importe",	bSortable: false }
								]
				        	});
				
				oTable = createDataTable();
			
				$("#mAcumulado").val(quitaFmt(window.opener.frmcontratoDeObra.saldoCompM.value));
				$("#mSaldo").val( parseFloat(quitaFmt( $("#mPorEjercer").val())) - parseFloat(quitaFmt($("#mAcumulado").val())));
				
				$("#tblEP tbody").click(
					function(event) {
						$(oTable.fnSettings().aoData).each(
							function (){
								$(this.nTr).removeClass('row_selected');
							});
							
						$(event.target.parentNode).addClass('row_selected');
						$('#tblSaldos').dataTable().fnClearTable(); 
						
						var anSelected = fnGetSelected( oTable );
						
						var sEP =   anSelected[0].innerText.substr(0,4)+"."+
									anSelected[0].innerText.substr(4,2)+"."+
									anSelected[0].innerText.substr(6,3)+"."+
									anSelected[0].innerText.substr(9,1)+"."+
									anSelected[0].innerText.substr(10,1)+"."+
									anSelected[0].innerText.substr(11,2)+"."+
									anSelected[0].innerText.substr(13,2)+"."+
									anSelected[0].innerText.substr(15,3)+"."+
									anSelected[0].innerText.substr(18,4)+"."+
									anSelected[0].innerText.substr(22,5)+"."+
									anSelected[0].innerText.substr(27,1)+"."+
									anSelected[0].innerText.substr(28,1)+"."+
									anSelected[0].innerText.substr(29,2)+"."+
									anSelected[0].innerText.substr(31,11)+"."+
									anSelected[0].innerText.substr(42,3)+"."+
									anSelected[0].innerText.substr(45,3);
						
						$("#ep").val(sEP);
						
						/*VGC29092016 Se valida que no exista la EP*/
						
						if( window.opener.validaEPCapturada(sEP) ){
							alert("Ya existe la EP: " + sEP + " capturada en el pago. Si necesita editarla, eliminela primero.");
							return;
						}
						
						$("#nFolioIngreso").val("");
						$("#rifEP").val("");
						var ctipodocto = $("#TO_TIPO_DOCTO").val();
						ctipodocto = ctipodocto.substring(0,2);						
						var szWhere = " EP = '" + sEP + "' ";
						//var szTabla = "VSALDOSANUALESINGRESOFISCAL";
						var szTabla = "VSALDOSRADICADO";
													                                                                                      
						$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 10, ajax: 'false'}, 
							function(j) {                      
		    					for (var i = 0; i < j.length; i++) 
		    					{  
			    					$('#tblSaldos').dataTable().fnAddData(
				    					[j[i].Col0 
				    					, formatCurrency(j[i].Col2)
				    					, formatCurrency(j[i].Col3)
				    					, formatCurrency(j[i].Col4)
				    					, formatCurrency(j[i].Col5)
				    					, formatCurrency(j[i].Col6)
				    					, formatCurrency(j[i].Col7)
				    					, formatCurrency(j[i].Col8)
				    					, formatCurrency(j[i].Col9)
				    					, formatCurrency(j[i].Col10)
				    					, formatCurrency(j[i].Col11)
				    					, formatCurrency(j[i].Col12)
				    					, formatCurrency(j[i].Col13)
				    					, formatCurrency(j[i].Col14)]);
								}
				         	}); //function(j)  		
					}); //function(event)
			
			
			$("#tblSaldos tbody").click(
					function(event) {
						$(oTableSaldos.fnSettings().aoData).each(
							function (){
								$(this.nTr).removeClass('row_selected');
							});
							
						$(event.target.parentNode).addClass('row_selected');
						
						var rifSelected = fnGetSelected( oTableSaldos );
						
						$("#idRIF").val(rifSelected[0].childNodes[0].innerText);
						
						$("#nFolioIngreso").val($("#idRIF").val());
						$("#rifEP").val($("#ep").val());
						
					}); //function(event)
		
			$(this).ajaxForm({ dataType: "json", success: formSubmited });
			
			$("#pbAceptar").button().click(
				function() {			
					
					var aEPsDet = oTableDetalle.fnGetData();
					var nuevo = 0;
					
					$("#nFolioPago").val(window.opener.frmcontratoDeObra.nFolioPago.value);
					$("#cTipoPago").val(window.opener.frmcontratoDeObra.cTipoPago.value);
					$("#cCentroContable").val(window.opener.frmcontratoDeObra.cCentroContable.value);
										
					queryFormPost("buscaIngresoPago", {async:false});
		
					if ($("#nExisteIngresoPago").val() == "0") 
						nuevo = 1;
						
					$("#ep").val(aEPsDet[0][1]);
					if ( nuevo == 0 ) 
						queryFormPost("buscaEpIngresoPago", {async:false});
										
					if ($("#EpUsada").val()== "Verdadero"){
						alert(" EP ya incluida en el Pago, Favor de Rectificar!!!");
					}
					else{
						for ( var i=0 ; i<aEPsDet.length ; i++ )
						{
							$("#ep").val(  aEPsDet[i][1] );
							$("#nclavecna").val( aEPsDet[i][2] );
							$("#mImporteEP").val( Number(parseFloat( quitaFmt($("#mImporteEP").val()) ).toFixed(2)) + Number(parseFloat(quitaFmt(aEPsDet[i][3].toFixed(2)))) );
							
							window.opener.insertaObjeto( aEPsDet[i][0], aEPsDet[i][1], Number(quitaFmt(aEPsDet[i][3])).toFixed(2), aEPsDet[i][4] );						
						}	
			 					 			
						window.opener.frmcontratoDeObra.ep.value=window.EPxRegistro.ep.value;
						window.opener.frmcontratoDeObra.codSIAFF.value=window.EPxRegistro.nclavecna.value;
						window.opener.frmcontratoDeObra.montoDev.value=Number(quitaFmt(window.EPxRegistro.mImporteEP.value)).toFixed(2);
											
						if( $("#TO_TIPO_DOCTO").val() == "DIVERSO"  ){
							opener.CAPITULO();
						}
						
						opener.cambioss();
						window.close();
					}
				});	//function()	
			
		$("#pbCancelar").button().click(function() { window.close();	});	
		
		$("#pbBorrar").button().click(function() { 
			$("#tblDetalle").dataTable().fnClearTable();
			$("#pbAceptar").css("visibility","hidden");
			$("#pbBorrar").css("visibility","hidden");
			});	
			
		}); // Ready

		function formSubmited() {
			
                alert("EP enviada");
                window.close();
            }


		function fnGetSelected( oTableLocal )
			{
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

		function createDataTable() {
			
			if( window.opener.frmcontratoDeObra.cIDContratoObra )
				window.EPxRegistro.nFolioCompromiso.value=window.opener.frmcontratoDeObra.cIDContratoObra.value; 
			else
				window.EPxRegistro.nFolioCompromiso.value=window.opener.frmcontratoDeObra.nFolioCompromiso.value;
			
			window.EPxRegistro.TO_TIPO_DOCTO.value=window.opener.frmcontratoDeObra.TO_TIPO_DOCTO.value;
			window.EPxRegistro.cIdContrato.value=window.opener.frmcontratoDeObra.cIdContrato.value;
			window.EPxRegistro.cCentroContable.value=window.opener.frmcontratoDeObra.cCentroContable.value;
			window.EPxRegistro.mPorEjercer.value=window.opener.frmcontratoDeObra.mImpEjercer.value;
	
			var nFolioCompromiso= $('#nFolioCompromiso').val(); 
			var cunidadej = $('#cUnidadEjecutora').val();
			
			if (cunidadej == 'A02'){
				cunidadej = "";
			}
			else {
				cunidadej = " and UnidadEjecutora IN (SELECT ur FROM tVistasUR WHERE modulo='TESORERIA' AND usuario = '<%=login%>') ";
			}
	
			var urlAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vConsultaCompromisoEP_Radicado&qw=cIdContrato='" + nFolioCompromiso + "'" + cunidadej + " AND fuentefinanciamiento <> 4 "; 
				
			return $("#tblEP").dataTable({
					"bDestroy": true,
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
					bAutoWidth : false,
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
					sAjaxSource: urlAjaxSource,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
						{ sName: "UnidadResponsable"	},
						{ sName: "EjercicioFiscal"   },
						{ sName: "Ramo" },
						{ sName: "GrupoFuncional" },
						{ sName: "Funcion"   },
						{ sName: "SubFuncion" },
						{ sName: "ProgramaGeneral"	},
						{ sName: "ActividadInstitucional" },
						{ sName: "ProgramaPresupuestario"   },
						{ sName: "Partida" },
						{ sName: "TipoGasto"	},
						{ sName: "FuenteFinanciamiento" },
						{ sName: "EntidadFederativa"   },
						{ sName: "Cartera" },
						{ sName: "UnidadEjecutora"	},
						{ sName: "UnidadNorativa" }
					]
	        	});
		}
		
		
		function agregarDetalle(){

		var existe = existeEPenDetalle();
		
		if (!existe){
			 if ($("#mImporte").val() != "" ) {
			 	if (parseFloat($("#mImporte").val()) <= parseFloat($("#mSaldo").val())){		
					
					queryFormPost("codigoCNA", {async:false});
					queryFormPost("mesesRadicado", {async:false});
			 		var inicio = parseInt($("#nMesIni").val());
	                var fin = parseInt($("#nMesFin").val());
	                var valor = 0;
	                var anio = 0;
	                var mes = 0;
	                
	                anio = parseInt("<%=today%>".split("/")[2], 10);
	                mes = parseInt("<%=today%>".split("/")[1], 10);
	                
	                //Ejercicio actual
	                if ( anio != parseInt(<%=aEjercicioFiscal%>)  ){
	                	mes = 12;
	                }
	                
	                if (mes > fin){
	                	mes = fin;
	                }
	                
	  				//while ( (inicio <= fin) && ( parseFloat($("#mImporte").val()) > 0 )  )
	  				while ( (mes >= inicio) && ( parseFloat($("#mImporte").val()) > 0 )  )
		  				{  
		  					$("#tmpMes").val(mes);
		  					queryFormPost("importeMesRadicado", {async:false});
		  					
		  					if (parseFloat($("#mImporte").val())<= parseFloat($("#mImporteMes").val()) ){
		  						
		  						valor =  Number(parseFloat($("#mImporte").val()).toFixed(2));
		  						
		  						$("#mImporteMes").val( parseFloat($("#mImporteMes").val()) - valor);
		  						$("#mAcumulado").val(parseFloat($("#mAcumulado").val()) + valor);
		  						$("#mSaldo").val( $("#mPorEjercer").val() - parseFloat($("#mAcumulado").val()) );
		  						$("#mImporte").val("0");
									
		  					}else{
		  					
		  						valor = Number(parseFloat($("#mImporteMes").val()).toFixed(2));
		  						$("#mImporte").val( Number(parseFloat($("#mImporte").val()).toFixed(2)) - valor);
		  						$("#mAcumulado").val(parseFloat($("#mAcumulado").val()) + valor);
		  						$("#mSaldo").val( $("#mPorEjercer").val() - parseFloat($("#mAcumulado").val()) );
		  						$("#mImporteMes").val("0");
		  						
		  					}
		  					
		  					$("#tblDetalle").dataTable().fnAddData(
		    					[ $("#nFolioIngreso").val() //nFolioIngreso
		    					, $("#rifEP").val() //EP
		    					, $("#nclavecna").val() // ClaveCNA
		    					, valor // Importe Por Mes
		    					, mes //Mes
		    					]);
		    					
		    				mes --;
						}
			         	
			 			$("#pbAceptar").css("visibility","visible");
			 			$("#pbBorrar").css("visibility","visible");
			 		
			 	}
			 	else{
			 		alert("Ha superado el importe a Devengar, Favor de Rectificar!!!");
			 	} 	
			 }
			 else{
			 	alert("Ingresa el monto a Devengar para la EP Seleccionada");
			 }
		}
		else{
			if ($("#nFolioIngreso").val() != ""){
				alert ("Solicitud de Recurso x Pagar con EP ya utilizado, favor de rectificar!!!");
				$("#nFolioIngreso").val("");
				$("#rifEP").val("");
				$("#nclavecna").val("");
				$("#mImporte").val("");
			}
		} 	
	}
	
	
	function existeEPenDetalle(){
		
		var aEPs = oTableDetalle.fnGetNodes();

		for ( var i=0 ; i<aEPs.length ; i++ )
		{
			if ( $(aEPs[i])[0].childNodes[1].innerText == $("#ep").val() )
			{
				if ($(aEPs[i])[0].childNodes[0].innerText == $("#nFolioIngreso").val()){
					return true;
				} 
			}
			else {
				if ($(aEPs[i])[0].childNodes[1].innerText != "" ){
					alert ("Solo se pueden agregar diferentes Folios del ingreso pero con la misma EP, favor de Rectificar!!!");
					$("#nFolioIngreso").val("");
					$("#rifEP").val("");
					$("#nclavecna").val("");
					$("#mImporte").val("");
					return true;
				}
			}
			
		}
		return false;
	}
	
	function quitaFmt(val) {
	    val =  ""+ val;
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	
		if (val.indexOf("(") >= 0) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
		}
		return val;
	}
	
</script>


</head>

<body id="dt_example" >
	<form id="EPxRegistro" name="EPxRegistro" >
		<div id="container" class="container">
			<h1>Relacion entre el Pago y la Solicitud de Recurso x Pagar</h1>
				<input id="ep" name="ep" type="hidden" style="display: none;"  value="" size="4" maxlength="64" class=""/>
				<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" value="6"/>
				<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value="<%=UE%>"/>
				<input type="hidden" id="nFolioCompromiso" name="nFolioCompromiso" value=""/>				
				<input type="hidden" id="nclavecna" name="nclavecna" value=""/>				
				<input type="hidden" id="TO_TIPO_DOCTO" name="TO_TIPO_DOCTO" value=""/>								
				<input type="hidden" id="cCentroContable" name="cCentroContable" value=""/>								
				<input type="hidden" id="cIdContrato" name="cIdContrato" value=""/>
				<input type="hidden" id="idRIF" name="idRIF" value=""/>
				<input type="hidden" id="tmpEP" name="tmpEP" value=""/>
				<input type="hidden" id="tmpMes" name="tmpMes" value=""/>
				<input type="hidden" id="nMesIni" name="nMesIni" value=""/>
				<input type="hidden" id="nMesFin" name="nMesFin" value=""/>
				<input type="hidden" id="mImporteMes" name="mImporteMes" value="0"/>
				<input type="hidden" id="mImporteEP" name="mImporteEP" value="0"/>
				
				<!-- Variables para Insertar en tIngresoEncabezado y tIngresoPagoDetalle -->
				<input type="hidden" id="nIdIngresoPago" name="nIdIngresoPago" value="0"/>
				<input type="hidden" id="nDocRenglonRIF" name="nDocRenglonRIF" value="0"/>
				<input type="hidden" id="nFolioPago" name="nFolioPago" value=""/>
				<input type="hidden" id="cTipoPago" name="cTipoPago" value=""/>
				<input type="hidden" id="cCentroContable" name="cCentroContable" value=""/>
				<input type="hidden" id="nFolioSICOP" name="nFolioSICOP" value="0"/>
				<input type="hidden" id="nFolioSIAFF" name="nFolioSIAFF" value="0"/>
				<input type="hidden" id="EpUsada" name="EpUsada" value="Falso"/>
				<input type="hidden" id="nExisteIngresoPago" name="nExisteIngresoPago" value="0"/>
				<input type="hidden" id="cEsRadicado" name= "cEsRadicado" value="S"/>
			
			<table align="center" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="center"> <label> Total por Devengar &nbsp;</label></td>
					<td align="center"> <label> Acumulado &nbsp;</label> </td>
					<td align="center"> <label> Saldo Pendiente </label> </td>
				</tr>
				<tr>
					<td align="center"><input type="text" size="10" id="mPorEjercer" name="mPorEjercer" maxlength="16" style="text-align:right" readonly > &nbsp;</td>
					<td align="center"><input type="text" size="10" id="mAcumulado" name="mAcumulado" maxlength="16" style="text-align:right" readonly> &nbsp;</td>
					<td align="center"><input type="text" size="10" id="mSaldo" name="mSaldo" maxlength="16" style="text-align:right" readonly></td>
				</tr>
			</table>
			<br>
			<label style="POSITION:static;">EPs Disponibles para el PAGO</label>
			<table id="tblEP" class="display"    class="display" >
	            <thead>
	                <tr>
					      <th>EF</th>
					      <th>Ra</th>
					      <th>UR</th>
					      <th>GF</th>
					      <th>Fu</th>
					      <th>SF</th>
					      <th>PG</th>
					      <th>AI</th>
					      <th>PP</th>
					      <th>Pa</th>
					      <th>TG</th>
					      <th>FF</th>
					      <th>EF</th>
					      <th>Ca</th>
					      <th>UE</th>
					      <th>UN</th>
	                </tr>
	            </thead>
	        </table>
			<br>
			<label style="POSITION:static;">Selecciona la Solicitud de Recurso x Pagar</label>	
			<table id="tblSaldos" class="display" >
	            <thead>
	                <tr>
	                	<th>Folio</th>
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
	                </tr>
	            </thead>
	        </table>
	        <br>
			<fieldset>
				<legend> Ingresa el Monto a Devengar </legend>
				<table align="center" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="center"> <label> Folio &nbsp;</label></td>
						<td align="center"> <label> EP &nbsp;</label> </td>
						<td align="center"> <label> Monto a Devengar &nbsp; </label> </td>
						<td align="center"> </td>
					</tr>
					<tr>
						<td align="center"><input type="text" size="10" id="nFolioIngreso" name="nFolioIngreso" maxlength="16" style="text-align:center" readonly > &nbsp;</td>
						<td align="center"><input type="text" size="64" id="rifEP" name="rifEP" maxlength="16" style="text-align:left" readonly> &nbsp;</td>
						<td align="center"><input type="text" size="10" id="mImporte" name="mImporte" maxlength="16" style="text-align:right"> &nbsp;</td>
						<td align="center"><input type="button" value="Agregar"	onClick="agregarDetalle()" id="agrega" name="agrega" /></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<label style="POSITION:static;">Detalle EP a Devengar</label>
			
			<table id="tblDetalle" class="display" >
	            <thead>
	                <tr>
	                	<th>Folio</th>
	                	<th>EP</th>
	                    <th>Codigo</th>
	                    <th>Importe</th>
	                    <th>Mes</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
			<center>
				<input type="button" id="pbAceptar" style="visibility: hidden" value ="Aceptar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" style="visibility: hidden" value ="Cancelar"/> &nbsp;&nbsp;&nbsp;
				<input type="button" id="pbBorrar" style="visibility: hidden" value="Borrar Tabla"/>	
			</center>
		</div>
	</form>
</body>
</html>