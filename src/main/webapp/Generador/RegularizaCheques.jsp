
<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
String DATE_FORMAT = "dd/MM/yyyy";
String cCentroContable = "";
String cUR = "";
String cUR2 = "";
String cRamo = "";
String u_login = "";
String mensaje = "";

Usuario usuario = (Usuario) session
		.getAttribute(GestionInterface.ATT_USER);

//Valida Centro de Costos
if (usuario.getPropiedades() != null
		&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
			.getValor();
}

if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
	mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}

cUR = usuario.getU_UR();
cRamo = usuario.getU_Ramo();
u_login = usuario.getLogin();


%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Regularización de Cheques</title>

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
		<style media="all" type="text/css">     
			.alignRight { text-align: right; } 
			.alignCenter { text-align: center; }
		</style> 
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			
			$("input.AyudaSyC").subIniciaDlg(); 
    		$("input.autoCompletaSyC").subIniciaAutoCompleta();

			$("#cUnidadResponsable").val( "<%=cUR%>" );

			querySelectPost("tCuentasBancariasURRead", "cIdCuentasBancarias", {async: false });
			//querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
			
			
			
			$("#tblMultilistador tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});

			oTable = $("#tblMultilistador").dataTable({
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
				sPaginationType: "full_numbers",
				bJQueryUI: true
        	});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbFiltrar')
				.button()
				.click( function() {
					
					var szTemp = "cCuentaBancaria = '" + $("#cIdCuentasBancarias").val() + "'";
					//if ($("#cIdUnidadAdministrativa").val() != 'B00') szTemp += " cUnidadResponsable = '" + $("#cIdUnidadAdministrativa").val() + "'";
										
					$("#szTemp").val(szTemp);
					
 					if (szTemp != '') szTemp = "&qw=" + szTemp;
    		
    				$("#tblMultilistador").dataTable({
    					bPaginate: false,
    					bAutoWidth : true,
    					iDisplayLength: 20000,
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
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegularizaCheques" + szTemp ,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "nNumCheque" },
							{ sName: "fElaboracion", sClass: "alignCenter" },
							{ sName: "fEntrega", sClass: "alignCenter" },
							{ sName: "caNoContrarrecibo" },
							{ sName: "cnombre" },
							{ sName: "mImporteCheque", bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight" },
							{ sName: "nFolioCheque",   bSearchable: false,	bSortable: false, bVisible: true }
						]
					});
    				
				} );

			
			$("#pbLimpiar")
				.button()
				.click(function() {
					
					$("#tblMultilistador").dataTable({
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
						sPaginationType: "full_numbers",
						bJQueryUI: true,
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegularizaCheques&qw=cUnidadResponsable = 'XX'"  ,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "nNumCheque" },
							{ sName: "fElaboracion"   },
							{ sName: "fEntrega" },
							{ sName: "caNoContrarrecibo" },
							{ sName: "cnombre" },
							{ sName: "mImporteCheque" },
							{ sName: "nFolioCheque" }
						]
					});
					
				});			
			
			$("#pbGuardar")
				.button()
				.click(function() {
					queryFormPost("tChequeEncabezadoUpdate", {async: false });
					alert("Actualización de Cheques se realizó Correctamente");
				});	
			

			$("#cIdCuentasBancarias").change(function () {
				$("#pbLimpiar").click();
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
		
		</script>
		

</head>

<body id="dt_example" >
	<form id="ExportarForm" name="ExportarForm"> 
		<div id="container" style="width:1000px; padding-left:100px" class="SyCData">	
			<input type="hidden" name="szTemp" id="szTemp">
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable">
			
			<h1>Regularización Cheques</h1>			
			<table id="clvcont" border=0>
				<tr align="right" >
					<td valign="top" >Cuenta Bancaria: </td>
					<td valign="top" width="400" align="left"> 
						<select id="cIdCuentasBancarias" name="cIdCuentasBancarias"></select>
					</td>			
				</tr>
			</table>
			<br/>
			<center>
				<input type="button" id="pbFiltrar" value="Filtrar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbLimpiar" value="Limpiar" />&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbGuardar" value="Actualizar"/>
			</center>
			<br/>
			<table id="tblMultilistador" class="display"  >
	            <thead>
	                <tr>
	                	<th width="80px">Cheque</th>
	                	<th width="100px">Fecha Elaboración</th>
	                	<th width="100px">Fecha Entrega</th>
	                    <th width="130px">Cuenta por Pagar</th>
	                	<th width="250px">Nombre o Razon Social</th>
	                    <th width="100px" align="center">Importe</th>
	                    <th width="30px">Folio</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>