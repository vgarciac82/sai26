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
		<title>Cat&aacute;logo de Firmantes</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
		$(document).ready(function() {
			querySelectPost("mCatalogoUnidadEjecutoraBusca", "UnidadResponsable", {async : false});
			mostrar();
			$( "#aTab1" ).attr("disabled", true);
			$("#tblFirmantes tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					var aTrs = $('#tblFirmantes').dataTable().fnGetNodes();           										
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         						
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{             
							var nTr = $('#tblFirmantes').dataTable().fnGetData(aTrs[i]);			
							window.location = "CatalogoFirmantes.jsp?tab=1&cIdUnidadEjecutora="+nTr[0]+"&nIdFirmante="+nTr[1];
						}     
					}										
					
				});
		
			$("#btnBuscar").button().click(function(){				
				mostrar();					 
			});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
		});
		
		function formSubmited() {
                alert("Firmante enviado!");
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
		function mostrar() {
		
				var qw = " 1 = 1";
				if ($("#UnidadResponsable").val() != "" && $("#UnidadResponsable").val() != "*" )
					qw += " AND cIdUnidadEjecutora = '" + $("#UnidadResponsable").val() + "'";
				if($("#nombreFirmanteText").val()!=''){
					qw+=" AND cNombre LIKE '%25" + $("#nombreFirmanteText").val() + "%25'";
				}
				
				oTable = $("#tblFirmantes").dataTable({
				sScrollX: "100%",
				sScrollXInner: "100%", 
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth : false,
				"iDisplayLength": 10, //Cuantos registros se despliegan
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catalogoFirmantesPedido&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdFirmante"   },
					{ sName: "cNombre" },
					{ sName: "cPuesto"	}
				]
        	});
				
		}
		
</script>
</head>
<body id="dt_example">
	<form> 
		<div id="container" class="container" style="width: 100%; height: 100%" >	
			<h1 align="left">Cat&aacute;logo Firmantes</h1>
			<table align="left">
				<tr id="trcUnidadResponsable" align="left">
					<td>Unidad Administrativa:</td>
					<td>
					<select id="UnidadResponsable" name="UnidadResponsable" style="width: 40em;"></select>
					</td>
				</tr>
				<tr id="nombreFirmante" align="left">
					<td>Nombre:</td>
					<td>
					<input type="text" id="nombreFirmanteText" name="nombreFirmanteText" style="width: 40em;"/>
					</td>
				</tr>
				<tr id="trBuscar" align="center">
					<td colspan="2"><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" /></td>					
				</tr>
			</table>																			
			<table id="tblFirmantes" class="display">
	            <thead>
	                <tr>
	                	<th width="5%">cIdUnidadEjecutora</th>
	                	<th width="5%">nIdFirmante</th>	                	
	                    <th width="40%">Nombre</th>
	                    <th width="50%">cPuesto</th>
	                </tr>
	            </thead>
	        </table>	       
			<br/>
		</div>
	</form>
	</body>
</html>