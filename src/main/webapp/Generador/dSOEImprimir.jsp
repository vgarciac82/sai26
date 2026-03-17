<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Generación SEO Documentado</title>

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
		
		<style>			// estilos del dialogo
			div#dialog-form-Componente fieldset { padding:0; border:0; margin-top:25px; }
			div#dialog-form-SubComponente fieldset { padding:0; border:0; margin-top:25px; }
			div#dialog-form-Categoria fieldset { padding:0; border:0; margin-top:25px; }

			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
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
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>		
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;		

		$(document).ready(function() {
         
			oTable = $("#tblSoeImprimir").dataTable({
				bAutoWidth : false,
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSOEImprimir",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 4, "desc" ]] ,
				aoColumns: [
					{ sName: "CheckBox",	bSortable: false, sClass: "alignCenter" },
					{ sName: "NumeroPrestamo" },
					{ sName: "numero_soe" },
					{ sName: "Estatus"	},
					{ sName: "FechaUltimoMovimiento", sClass: "alignCenter"	}
				]
        	});
        	
			$("#pbImprimir")
				.button()
				.click(function() {
				
				
				var aTrs = oTable.fnGetNodes();
				var rowsTbl = $("#tblSoeImprimir").dataTable().fnGetData();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					var szTemp =rowsTbl[i][2];
 					szTemp = (szTemp.replace(" ","")).replace(" ","");	
					szTemp = (szTemp.replace("/","")).replace("/","");
					if ( $("#chk" + szTemp).is(':checked')  )
					{
						var url = "../admin/SeguridadCatalogos?xls=SI&catalogo=REPORTE&accion=run&rn=RelacionGastosDocumentadosCorta.jasper&NO_SOE="+ rowsTbl[i][2] ;
						
						var ventimp = window.open(url, "popacuse"+i,"scrollbars=1, resizable=yes, width=1024, height=768");

					}					    
				} 		
			});
				
		});

		</script>
</head>

<body id="dt_example" >
	<form> 
		<input id="id_prestamo" name="id_prestamo" type="hidden" value="0" readonly style=" background:#f0f0f0; ">
		
		<div id="container" class="container"  style="width:1100px">	
			<h1>SOE Imprimir</h1>
				<input type="button" id="pbImprimir" value="Imprimir SOEs">&nbsp;&nbsp;&nbsp;
			<br/><br/>
			<table id="tblSoeImprimir" class="display"  >
	            <thead>
	                <tr>
	                	<th>Sel </th>
	                	<th>N&uacute;mero Pr&eacute;stamo</th>
	                	<th>N&uacute;mero SOE</th>
	                	<th align="center">Estatus</th>
	                	<th align="center">Fecha Último Movimiento</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>