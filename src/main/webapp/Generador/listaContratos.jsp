<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>.::Lista Contratos::.</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<link rel="shortcut icon" type="image/ico" href="http://www.datatables.net/media/images/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" charset="utf-8">
	
	$(document).ready(function() 
		{
		    $("#tabs").tabs( {
		        "show": function(event, ui) {
		            var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		            if ( oTable.length > 0 ) {
		                oTable.fnAdjustColumnSizing();
		            }
		        }
		    } );
		     
		    $('table.display').dataTable( {
		        "sScrollY": "200px",
		        "bScrollCollapse": true,
		        "bPaginate": false,
		        "bJQueryUI": true,
		        "aoColumnDefs": [
		            { "sWidth": "10%", "aTargets": [ -1 ] }
		        ]
		    } );

		    $('#tablaSeleccionados').dataTable();
	} );
	</script>
  </head>
  
  <body>
    <form id="listaContrato" action="" method="">
    
    <div id="tabs">
    	<ul>
    		<li><a href="#tabs-1">Pendientes de Generar Layout</a></li>

			<li><a href="#tabs-2">Compromisos en proceso SICOP</a></li>

			<li><a href="#tabs-3">Third</a></li>

			</ul>

			<div id="tabs-1">Pagina 1</div>

			<div id="tabs-2">
			  <table   class="display" id="tablaSeleccionados">
        		<thead>
        			<tr>
          				<th width="20" align="center" bgcolor="#70FFFF"><input type="checkbox" name="todos" id="todos"></th>
            	  		<th width="50" align="center" bgcolor="#70FFFF"><font size="2">E.C.</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">Compromiso</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">C&eacute;dula</font></th>
            	        <th width="70" align="center" bgcolor="#70FFFF"><font size="2">Movimiento</font></th>
            	        <th width="70" align="center" bgcolor="#70FFFF"><font size="2">Documento</font></th>
            	        <th width="70" align="center" bgcolor="#70FFFF"><font size="2">Tipo Documento</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">Subtipo Documento</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">Estado</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">Fecha</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">Ramo</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">Unidad</font></th>
            	        <th width="50" align="center" bgcolor="#70FFFF"><font size="2">RFC</font></th>
          			</tr>
          		</thead>
          		<%
          			int noRegistros = 10; 
          			for(int i=0; i<noRegistros; i++){
          		%>
          		<tbody>
          		    <%
          		    if(noRegistros % 2 == 1 ){ 
          		    %>
          			<tr class="gradeX" align="left" valing="middle" heigth="20" bgcolor="#FFFFFF" style="font-weight:bold">
          				<td width="20" align="center" bgcolor="#FFFFCC"><input type="checkbox" name="check' + i '" id="todos"></td>
          				<td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
          			</tr>
          			<% 
          			}
          			else{	
          			%>
          			<tr class="gradeC" align="left" valing="middle" heigth="20" bgcolor="#FFFFFF" style="font-weight:bold">
          				<td width="20" align="center" bgcolor="#FFFFCC"><input type="checkbox" name="check' + i '" id="todos"></td>
          				<td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
            	        <td width="100" align="center" bgcolor="#FFFFCC" ><font size="2"><br/></font></td>
            	        <td width="50" align="center" bgcolor="#FFFFCC"><font size="2"><br/></font></td>
          			</tr>
          			<%
          			}
          			%>
          			
          		</tbody>
          		<%} %>
			</table>
		</div>
		<div id="tabs-3">Pagina 3</div>

	</div>
    
       
    
    </form>
  </body>
</html>
