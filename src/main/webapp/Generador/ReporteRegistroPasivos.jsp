<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reporte Registro Pasivos</title>

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
    		
    		querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
			$("#cIdUnidadAdministrativa").val( "<%=ur%>" );
			if ( $("#cIdUnidadAdministrativa").val() != "A02" ){
	   			$("#cIdUnidadAdministrativa").attr('disabled', true);
	   		}

			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$("#pbFiltrar")
				.button()
				.click( function() {

					$("#mTotalPasivo").val("0.00");	

					var szTemp = "";
					if ($("#cIdUnidadAdministrativa").val() != 'RHQ') szTemp += " cUnidadResponsable = '" + $("#cIdUnidadAdministrativa").val() + "'";
					
					if ($("#cEstatus").val() != "TODOS"){
						if(szTemp != "") szTemp += " AND ";
						szTemp += " ESTATUS = '" + $("#cEstatus").val() + "'";
					} 
						
					
					$("#szTemp").val(szTemp);
					
 					if (szTemp == '') 
 						szTemp = "&qw= 1=1";
 					else
 						szTemp = "&qw= " + szTemp;

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
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistroPasivos" + szTemp,
						aoColumns: [
							{ sName: "clavePasivo" },
							{ sName: "Referencia"   },
							{ sName: "RFC" },
							{ sName: "nombre" },
							{ sName: "cuentaBancaria" },
							{ sName: "cUnidadResponsable" },
							{ sName: "fechaCaptura" },
							{ sName: "fechaPago" },
							{ sName: "importePago", sClass: "alignRight" },
							{ sName: "estatus" },
							{ sName: "EP" },
							{ sName: "ImporteEP", sClass: "alignRight" }, 
							{ sName: "mImporteEP", bSearchable: false,	bSortable: false, bVisible: false }
						]
					});

    				setTimeout("fn_totalPasivo()",1000);
    				
				} );

			
			$("#pbLimpiar")
				.button()
				.click(function() {

					$("#cIdUnidadAdministrativa").val('RHQ');
					$("#mTotalPasivo").val("0.00");	
					
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
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistroPasivos&qw=clavepasivo = 'xx'",
						aoColumns: [
							{ sName: "clavePasivo" },
							{ sName: "Referencia"   },
							{ sName: "RFC" },
							{ sName: "nombre" },
							{ sName: "cuentaBancaria" },
							{ sName: "cUnidadResponsable" },
							{ sName: "fechaCaptura" },
							{ sName: "fechaPago" },
							{ sName: "importePago", sClass: "alignRight" },
							{ sName: "estatus" },
							{ sName: "EP" },
							{ sName: "ImporteEP", sClass: "alignRight" }, 
							{ sName: "mImporteEP", bSearchable: false,	bSortable: false, bVisible: false }
						]
					});
					
				});			
			
			$("#pbPDF")
				.button()
				.click(function() {
					
					window.open(
						"../admin/SeguridadCatalogos?"
							+ "catalogo=ANEXO"
							+ "&accion=run"
							+ "&rn=ReporteRegistroPasivos.jasper"
							+ "&cUnidadResponsable = '" + $("#cIdUnidadAdministrativa").val() + "'",
						"Anexo",
						"scrollbars=1, resizable=yes, width=1024, height=768"
					);
					

				});	

			$("#pbExcel")
				.button()
				.click(function() {
					document.ExportarForm.submit();

				});	
			
			$("#pbLimpiar").click();
		});
		
	function formSubmited() {
        alert("Beneficiario enviado!");
    }
		
	function fn_totalPasivo(){
		var oTableLocal = $("#tblMultilistador").dataTable();
		var nRows = $("#tblMultilistador tr").length -1 ;
		var aTrs = oTableLocal.fnGetNodes();
		var mTotalPasivo = 0;
		for ( var i=0 ; i<aTrs.length ; i++ ){
			var aData = oTableLocal.fnGetData( i );
			mTotalPasivo = mTotalPasivo + Number( aData[ 12 ] ) ;
		}
		$("#mTotalPasivo").val( mTotalPasivo ).formatCurrency();
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
	<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/generaLayoutRPasivosServlet" method="post"> 
		<div id="container" style="width:1000px; padding-left:100px" class="SyCData">	
			<input type="hidden" name="szTemp" id="szTemp">
			<h1>Reporte de Pasivos</h1>			
			<table id="clvcont" border=0>
				<tr align="right">
					<td valign="top" >Unidad Ejecutora: </td>
					<td valign="top"  colspan="3" align="left"> 
						<select name="cIdUnidadAdministrativa" id="cIdUnidadAdministrativa" style="width: 35em;" class="desahabilitado"><option value="RHQ"></option></select>
					</td>
					<td>Total Pasivo:
						<input type="text" name="mTotalPasivo" id="mTotalPasivo" value="0"  style="text-align:right;">
					</td>					
				</tr>
				<tr align="right">
					<td valign="top" >Estatus:</td>
					<td valign="top"  colspan="3" align="left"> 
						<select name="cEstatus" id="cEstatus" style="width: 15em;" class="desahabilitado">
							<option value="TODOS">TODOS</option>
							<option value="ACTIVO">ACTIVO</option>
							<option value="CANCELADO">CANCELADO</option>
						</select>
					</td>
					<td>
					</td>					
				</tr>
			</table>
			<br/>
			<center>
				<input type="button" id="pbFiltrar" value="Filtrar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbLimpiar" value="Limpiar" />&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbPdf" value="PDF">&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbExcel" value="Excel">
			</center>
			<br/>
			<table id="tblMultilistador" class="display"  >
	            <thead>
	                <tr>
	                	<th>Clave</th>
	                	<th>Referencia</th>
	                	<th>RFC</th>
	                	<th>Nombre o Razon Social</th>
	                    <th>Cuenta Bancaria</th>
	                	<th>U.R.</th>
	                    <th>Fecha Captura</th>
	                    <th>Fecha Pago</th>
	                    <th>Importe Pasivo</th>
	                    <th>Estatus</th>
	                    <th>Clave Presupuestal</th>
	                    <th>Importe EP</th>
	                    <th>&nbsp;</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>