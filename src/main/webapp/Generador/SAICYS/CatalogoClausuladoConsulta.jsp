<%@page import="com.syc.gestion.core.Role"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.admin.servlet.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	Map rol =usuario.getRoles();
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
		<title>Cat&aacute;logo de Cl&aacute;usulas</title>

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
			//querySelectPost("UnidadBusca2", "desUnidadResponsable2", {async: false });
			mostrar(0);
			
			$( "#aTab2" ).attr("disabled", true);
			$("#tblClausulas tbody").dblclick(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					var aTrs = $('#tblClausulas').dataTable().fnGetNodes();           										
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         						
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{             
							var nTr = $('#tblClausulas').dataTable().fnGetData(aTrs[i]);
									
							window.location = "CatalogoClausulado.jsp?tab=2&nIdClausula="+nTr[0];
						}     
					}										
					
				});
				
				
			$("#btnBuscar").button().click(function(){				
				mostrar(1);						 
			});
			
		});
	
	
	
function mostrar(id) {
				var qw = " 1 = 1 ";
				
			
				if(id==1){				
					qw += " AND  (CCLAUSULA LIKE '%25" + $("#cDesc").val() + "%25' OR CCLAUSULA LIKE '%25" + $("#cDesc").val().toUpper + "%25') ";
					
				}
				
			
				oTable = $("#tblClausulas").dataTable({
				sScrollX: "100%",
				sScrollXInner: "100%", 
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth : false,
				iDisplayLength: 5, //Cuantos registros se despliegan
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=[mClausulado]&qw="+qw,
				bProcessing: true,
				//Tablesorter:false,
				//bSortable: false
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "NIDCLAUSULA" },
					{ sName: "CCLAUSULA" },
					{ sName: "NCLAUSULA"  }
			
					]
        	});
				
				
		}
		
		
		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
</script>
		

</head>

<body id="dt_example">
	<form> 
		<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
		<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
		<div id="container" class="container" style="width: 100%; height: 100%" >	
			<h1 align="left"></h1>
			<table align="left">
			
			<tr id="trcUnidadResponsable" align="left">
					<td>Descripci&oacute;n de Cl&aacute;sula:</td>
					<td>
						<textarea  name="cDesc" id="cDesc" cols="50" rows="6" ></textarea>
					</td> 
				</tr>
			  
				<tr id="trBuscar" align="center">
					<td colspan="2"><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" onclick="" /></td>					
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>	
			</table>																			
			<table id="tblClausulas" class="display">
	            <thead>
	                <tr>
	    				<th width="5%" >Id</th>
	                   <th width="60%">Descripci&oacute;n</th>
	                   <th width="35%">N&umero de Cl&aacute;usula</th>
	                    
	                </tr>
	            </thead>
	        </table>	       
			<br/>
		</div>
	</form>
	</body>
</html>