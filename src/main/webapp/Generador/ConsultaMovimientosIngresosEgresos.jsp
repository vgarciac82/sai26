<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	String cUR = "";
	String cRamo = "";
	String cCentroContable="";

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
		cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();

	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consulta de Movimientos Ingresos / Egresos</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function(){
			
			$("input.AyudaSyC").subIniciaDlg(); 
    		$("input.autoCompletaSyC").subIniciaAutoCompleta();
	
    		$("#cUnidadResponsable").val( "<%=cUR%>" );

    		querySelectPost("tRdbCat_Concepto","cConcepto", {async: false });
    		
			$("#tBancosRDB").change(function(){ 
				//$("#cBanco").val( $( this ).val() );
				querySelectPost("cuentasBancariasRBD", "cuentaBancaria", {async: false });  
				//($("#tBancosRDB").val() == "BBVA BANCOMER") ? $("#tdBancomer").show() : $("#tdBancomer").hide();
			});
			
			oTable = $("#tblIngresosEgresosMov").dataTable({
					       
				//iDisplayLength: 20,
				bSortClasses: false,
				
				bPaginate: false,
	        	bLengthChange: false,
	        	bFilter: false,
	        	bSort: true,
	        	bInfo: false,
	        	bAutoWidth: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				//bScrollCollapse: true,
				iDisplayLength: 10,
		        sScrollY: "950px", 
		        sScrollX: "1300px",
		        Height: "550px", 
				//sScrollXInner: "100%",
				aaSorting: [[ 1, "asc" ]] ,
				bRetrive: true,
				oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No hay datos en la tabla",
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
			
			$('#pbFiltrar').button().click( function() {
					
					var szTemp = "";
					$("#szTemp").val("");
					mostrarDetalle()
			});
			
			$("#pbLimpiar").button().click(function() {

					$("#tBancosRDB").val(""); 
					$("#fFechaInicio").val(""); 
					$("#fFechaFin").val(""); 
					$("#cIdMediodePago").val("0"); 
					$("#cConcepto").val("0"); 
					$("#cMovimiento").val("SELECCIONE");
					$("#cuentaBancaria").val("0");
					$("#szTemp").val( "" );
					
					var szTemp = " nFolio = '0'";
					
					$("#tblIngresosEgresosMov").dataTable({
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "500",
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
						bRetrive: true,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						bJQueryUI: true,
						bPaginate: false,
						iDisplayLength: 10,
		        			sScrollY: "950px", 
		        			sScrollX: "1300px",
		        			Height: "550px", 
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_RDBCARGAARCHIVORDB_Terminados" + szTemp ,
						aaSorting: [[ 10, "asc" ]] ,
						aoColumns: [
							{ sName: "folioSai", sClass: "alignCenter" },
							{ sName: "nFolio", sClass: "alignCenter" },
							{ sName: "banco", sClass: "alignCenter"   },
							{ sName: "tipoBancomer" },
							{ sName: "cuentaBancaria" },
							{ sName: "sReferencia", sClass: "alignCenter" },
							{ sName: "sMovimiento", sClass: "alignCenter" },
							{ sName: "cConcepto", sClass: "alignCenter" },
							{ sName: "sBeneficiario", sClass: "alignCenter" },
							{ sName: "mImporte", sClass: "alignRight"	},
							{ sName: "tipoMovimiento", sClass: "alignRight"	}
						]
				});
					
			});			
			
			$("#pbExcel").button().click(function() {
				
					//if( $("#szTemp").val() != "" ){
						document.ExportarForm.submit();
						$("#ExportarForm").submit();
						
						
					//}
					
			});	
	
			$("#pbPdf").button().click(function() {
				
					if ( $("#szTemp").val() != "" ){
						window.open(
							"../admin/SeguridadCatalogos?"
								+ "catalogo=ANEXO"
								+ "&accion=run"
								+ "&rn=RepConsultaIngresosEgresos.jasper"
								+ "&whereFolio=" + $("#szTemp").val(),  
							"Anexo",
							"scrollbars=1, resizable=yes, width=1024, height=768");
					}

			});	
			
			
			$( "#fFechaInicio" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		
	
		
		
			$( "#fFechaFin" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		
	
});


		
		
		function formSubmited() {
                alert("Beneficiario enviado!");
            }
		
		
		function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}

			function foco(elemento) {
			 elemento.style.border = "1px solid #FF0000";
			 }
			
			 function no_foco(elemento) {
			 elemento.style.border = "1px solid #CCCCCC";
			 }


function mostrarDetalle(){

		var szTemp = " cEvento != '800_50_27A' ";
		
		$("#tblIngresosEgresosMov").dataTable().fnClearTable();
		
		if ($("#tBancosRDB").val() != "") {
			
					szTemp += " AND banco = '" + $("#tBancosRDB").val() + "'";
		}
		
		if ($("#cuentaBancaria").val() != "" && $("#cuentaBancaria").val() != null){
			
					if(szTemp != "") szTemp = szTemp + " AND ";
					szTemp += " cuentaBancaria = '" + $("#cuentaBancaria").val() + "'";
		}

		if ($("#fFechaInicio").val() != "" && $("#fFechaFin").val() != ""){
			
					if (szTemp != "") szTemp = szTemp + " AND ";
					szTemp += " fAplicacion between '" + $("#fFechaInicio").val() + "' and '" + $("#fFechaFin").val() + "'";
		}
					
		if ($("#cConcepto").val() != '0'){
			
					if (szTemp != '') szTemp = szTemp + " AND ";
					szTemp += " sConcepto = '" + $("#cConcepto").val() + "'";
		}
		if ($("#cMovimiento").val() != 'SELECCIONE'){
			
					if (szTemp != '') szTemp = szTemp + " AND ";
					szTemp += " tipoMovimiento = '" + $("#cMovimiento").val() + "'";
		}
			
		//if (szTemp != ""){ szTemp = szTemp; }
    	
		//var szTemp =  szTemp+ "  ";
		$("#szTemp").val(szTemp);
		
		if (szTemp != '') szTemp = "&qw=" + szTemp;
		
		$("#tblIngresosEgresosMov").dataTable({
    					//bAutoWidth : false,
						//sScrollX: "100%",
						//sScrollY: "900",
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
		        		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_RDBCARGAARCHIVORDB_Terminados" + szTemp ,
						bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
		        			sScrollY: "550px", 
		        			sScrollX: "1300px",
		        			Height: "550px", 
		        		aaSorting: [[ 10, "asc" ]] ,
						aoColumns: [
							{ sName: "folioSai", sClass: "alignCenter", sWidth:"45px" },
							{ sName: "nFolio",  sWidth:"150px" },
							{ sName: "banco", sClass: "alignCenter"   },
							{ sName: "tipoBancomer" },
							{ sName: "cuentaBancaria" },
							{ sName: "sReferencia", sClass: "alignCenter" },
							{ sName: "sMovimiento", sClass: "alignCenter" },
							{ sName: "cConcepto", sClass: "alignCenter" },
							{ sName: "sBeneficiario", sClass: "alignCenter" },
							{ sName: "mImporte", sClass: "alignRight"	},
							{ sName: "tipoMovimiento", sClass: "alignRight"	}
						]
				});
		
		var campos = $("#szTemp").val();
		
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_RDBCARGAARCHIVORDB_TOTALES", Campos:campos, Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
										
					var totalEgreso = j[i].Col0;
					var totalIngreso = j[i].Col1;
										
					$("#egreso").html(totalEgreso);
					$("#ingreso").html(totalIngreso);
				}
				
		});
		
		
		
		
		/*
		 * 
		 * var campos =  szTemp+ " ORDER BY tipoMovimiento DESC ";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_RDBCARGAARCHIVORDB_DET", Campos:campos, Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
										
					fRegistro = j[i].Col0;
					fSai = j[i].Col1;
					banco = j[i].Col2;
					tipoBancomer = j[i].Col3;
					cuenta = j[i].Col4;
					referencia = j[i].Col5;
					movimiento = j[i].Col6;
					concepto = j[i].Col7;
					beneficiario = j[i].Col8;
					importe = j[i].Col9;
					tipoMovimiento = j[i].Col10;
						
					
					$("#tblIngresosEgresosMov").dataTable().fnAddData( [ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, j[i].Col9, j[i].Col10 ]);
					//$("#tblIngresosEgresosMov").dataTable().fnAddData([ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, j[i].Col9, j[i].Col10 ]);
					//alert(j[i].Col0);
			}
				
		});	*/
}		
</script>
</head>
<body id="dt_example" >
	<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/LayoutGeneral" method="post"> 
		<div id="container"  class="container SyCData" style="width:1300px; align:center">	
			<input type="hidden" name="szTemp" id="szTemp">
			<input type="hidden" name="cDocumento" id="cDocumento" value="TODOS">
			<input type="hidden" name="cBanco" id="cBanco" value="">
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="">
			<input type="hidden" name="tipoConsulta" id="tipoConsulta" value="ConsultaIngresosEgreso">
															
			<h1>Consulta de Movimientos Ingresos / Egresos</h1>	
					
				<table id="clvcont" border="1" align="center">
				
					<tr>
						<td align="right">Banco: </td><td><input type="text" class="AyudaSyC desahabilitado" maxlength="20" size="20" name="tBancosRDB" id="tBancosRDB" readonly ></td>
						<td align="right">Cuenta:</td><td><select name="cuentaBancaria" id="cuentaBancaria" style="width: 15em;" class="desahabilitado"></select></td>
						<td valign="top" >Concepto:</td>
						<td valign="top" align="left"> 
							<select name="cConcepto" id="cConcepto" style="width: 20em;" class="desahabilitado"></option></select>
						</td>					
						<td valign="top" >Movimiento:</td>
						<td valign="top" align="left"> 
							<select name="cMovimiento" id="cMovimiento" style="width: 10em;" class="desahabilitado">
								<option value="SELECCIONE">SELECCIONE</option>
								<option value="EGRESO">EGRESO</option>
								<option value="INGRESO">INGRESO</option>
							</select>
						</td>		
					</tr>
					<tr>
						<td></td><td></td>	
						<td align="right" >Fecha Inicio: </td><td align="center"><input type="text" maxlength="10" size="10" name="fFechaInicio" id="fFechaInicio" readonly ></td>
						<td align="right">Fecha Fin: </td><td align="center"><input type="text" maxlength="10" size="10" name="fFechaFin" id="fFechaFin" readonly></td>
					</tr>
				
				</table>
				
				<table><tr><td>&nbsp;</td></tr></table>
				
				<table align="center">
				
					<tr>
						<td><input type="button" id="pbFiltrar" value="Buscar"/></td>
						
						<td><input type="button" id="pbLimpiar" value="Nuevo"/></td>
						
						<td><input type="button" id="pbExcel" value="Excel"/></td>
						
						<td><input type="button" id="pbPdf" value="PDF"/></td>
					</tr>
					
				</table>
				
				<fieldset>
						<legend>Movimientos</legend>
							<table width="450px" align="right" height="50px" >
								<td style="font-size: 11pt">Total Ingreso <div id="ingreso" style="font-size: 12pt"/>0.00</div></td>
								<td style="font-size: 11pt">Total Egreso <div id="egreso" style="font-size: 12pt"/>0.00</div></td>
							</table>
					<table id="tblIngresosEgresosMov" >
								<tbody>
								<thead>
									<tr align="center">
										<th>Registro</th>
				                		<th>Folio_Sai</th>
				                		<th>Banco</th>
				                		<th>-</th>
				                   	 	<th>Número_Cuenta</th>
										<th>Referencia</th>
										<th>Movimiento</th>
										<th>Concepto</th>
										<th>Beneficiario</th>
										<th>Importe</th> 
										<th>Movimiento</th>
									</tr>	
								</thead>							
								</tbody>	
					</table>
				</fieldset>
			<br/>
		</div>
	</form>
	</body>
</html>