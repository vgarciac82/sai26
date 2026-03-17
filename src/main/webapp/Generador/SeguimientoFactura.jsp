<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Seguimiento - Factura</title>

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
			.alignRight { text-align: right; }
			.alignCenter { text-align: center; }
			.alignLeft { text-align: left; }
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

		<script type="text/javascript" charset="utf-8">

			var oTable;
			var oTablePres;
			var oTableFact;
			var szWhere=" id_prestamo=" + '<%=request.getParameter("pa")%>';
			var szWhereFact=" id_prestamo=" + '<%=request.getParameter("pa")%>' + " AND id_Contrato=" + '<%=request.getParameter("co")%>';
	
			$(document).ready(function() {
			
				oTablePres = $("#tblPrestamo").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSeguimientoPrestamo&qw="+szWhere,
					bProcessing: true,
					bJQueryUI: true,
					 "sDom": 'frt' ,
					bFilter:false,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "id_prestamo", bVisible: false },
						{ sName: "NumeroPrestamo"   },
						{ sName: "NombrePrestamo" },
						{ sName: "montoAcumulado",	sClass: "alignRight" 	},
						{ sName: "TipoCambio",	sClass: "alignCenter" 	},
						{ sName: "mAcumuladoMXN",	sClass: "alignRight" 	},
						{ sName: "ImporteComprobado",	sClass: "alignRight"  },
						{ sName: "porComprobar",	sClass: "alignRight"  },
						{ sName: "avanceComprobado",	sClass: "alignCenter"  }
						
					]
	        	});				
				
				oTable = $("#tblContrato").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSeguimientoContrato&qw="+szWhereFact,
					bProcessing: true,
					bJQueryUI: true,
					"sDom": 'frt' ,
					bFilter:false,
					bSearch:false,
					aaSorting: [[ 0, "asc" ]] ,
					aoColumns: [
						{ sName: "NoContrato"   },
						{ sName: "ImporteTotal", sClass: "alignRight" 	},
						{ sName: "ImporteComprobado",	sClass: "alignRight" 	},
						{ sName: "PorComprobar",	sClass: "alignRight"  },
						{ sName: "AvanceComprobacion",	sClass: "alignCenter"  }
						
					]
	        	});				
				
	oTableFact = $("#tblFactura").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSeguimientoFactura&qw="+szWhereFact,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "id_Factura", bVisible: false },
						{ sName: "NumFactura"   },
						{ sName: "ImporteComprobado",	sClass: "alignRight" 	},
						{ sName: "TipoCambio",	sClass: "alignRight" 	},
						{ sName: "importe_US",	sClass: "alignRight" 	},
						{ sName: "SolicitudDesembolso",	sClass: "alignCenter"   },
						{ sName: "OficioSoe",	sClass: "alignCenter"  }
					]
	        	});					
				
			});
			
			
			


			
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<div id="container" style="padding:10px">	
			<h1>Seguimiento - Facturas</h1>
			<table id="tblPrestamo" class="display"  >
	            <thead>
	                <tr>
	                	<th>idPrestamo</th>
	                	<th>N&uacute;mero Pr&eacute;stamo</th>
	                	<th>Nombre Pr&eacute;stamo</th>
	                    <th>Monto USD</th>
	                    <th>Tipo de Cambio</th>
	                    <th>Monto MXN</th>
	                    <th>Desembolsado MXN</th>
	                    <th>Saldo del Pr&eacute;stamo MXN</th>
	                    <th>Avance Comprobaci&oacute;n</th>
	                    
	                </tr>
	            </thead>
	        </table>
			<br/>
			<table id="tblContrato" class="display"  >
	            <thead>
	                <tr>
	                	<th>N&uacute;mero Contrato</th>
	                	<th>Monto MXN</th>
	                    <th>Comprobado MXN</th>
	                    <th>Saldo del Contrato MXN</th>
	                    <th>Avance Comprobaci&oacute;n</th>
	                    
	                </tr>
	            </thead>
	        </table>
			<br/>
			<table id="tblFactura" class="display"  >
	            <thead>
	                <tr>
	                	<th>idFactura</th>
	                	<th>N&uacute;mero Factura</th>
	                	<th>Importe MXN</th>
	                    <th>Tipo de Cambio</th>
	                    <th>Importe USD</th>
	                    <th>Solicitud de Desembolso</th>
	                    <th>Autorizaci&oacute;n OFI</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>