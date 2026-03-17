<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Lista Compromisos</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

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
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" charset="utf-8">
		function inicio(){
			document.getElementById('sDataH').value = "";
		
		}
		$(document).ready(function(){
			$("#tabs").tabs( {
				"show": function(event, ui) {
		    		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		    		if ( oTable.length > 0 ) {
		    			oTable.fnAdjustColumnSizing();
		    		}
				}
			} );
		});

		function fnClickAddRow(row) {
			var table2 = $('#dt_paraEnvio').dataTable().fnAddData( [	row.cells[1].childNodes[0].toString(),
			      				row.cells[2].childNodes[0].toString(),
			      				row.cells[3].childNodes[0].toString(),
			      				row.cells[4].childNodes[0].toString(),
			      				row.cells[5].childNodes[0].toString(),
			      				row.cells[6].childNodes[0].toString(),
			      				row.cells[7].childNodes[0].toString(),
			      				row.cells[8].childNodes[0].toString(),
			      				row.cells[9].childNodes[0].toString()
			      	                     ] );

		}
		
		function fnClickDellRows(){
			var table2 = $('#dt_paraEnvio').dataTable();
			table2.fnClearTable();				
		}

		function enviar(){
 			try {
 			    fnClickDellRows();
        		var table = document.getElementById('dt_generados');        		
            	var rowCount = table.rows.length;
                var vacio =true;
            	for(var i=0; i<rowCount; i++) {
            		var row= table.rows[i];
                	var chkbox = row.cells[0].childNodes[0];
                	if(null != chkbox && true == chkbox.checked) {
						//agregando registros a la tabla 'dt_paraEnvio' ...
						vacio=false;
						var caNoCompromiso = row.cells[2].childNodes[0].toString();
						document.getElementById('sDataH').value += "'" + caNoCompromiso + "'," ;
			      		fnClickAddRow(row);
                	}
            	}
        	}catch(e) {
         		alert(e);
    		}
    		if(vacio){
    		    alert("Debe marcar al menos una fila");
    		}
 		}

 		function generar(){
 			try {
        		var table = document.getElementById('dt_generados');
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount = table.rows.length;
            	for(var i=0; i<rowCount; i++) {
            		var row = table.rows[i];
                	var chkbox = row.cells[0].childNodes[0];
                	if(null != chkbox && true == chkbox.checked) {
						//quitando de la lista de compromisos los registros enviados a SICOP...
                    	table.deleteRow(i);
                    	rowCount--;
                    	alert(rowCount);
                	}
            	}
            	document.envioSICOP.submit();
         	}catch(e) {
         		alert(e);
         	}
 		}
	
		function toggleReactivar(status) {
			$("input:checkbox").each( 
				function() {
					$(this).attr("checked",status.checked);
				}
			);
		}

		function toggle(status) {
			$("input:checkbox").each( 
				function() {
					$(this).attr("checked",status.checked);
				}
			);
		}
	</script>
	</head>

  	<body id="dt_example" onLoad="inicio();" >
		<div  id="container" >
        	<div id="tabs">
        		<ul>
					<li><a href="#tabs-1">Pendientes de generar Layout</a></li>
					<li><a href="#tabs-2" onClick="enviar();">Compromisos en proceso SICOP</a></li>
					<!--
					<li><a href="#tabs-3">Compromisos autorizados SICOP</a></li>
					
					<li><a href="#tabs-4">Compromisos rechazados SICOP</a></li>
					 -->
				</ul>
               
				<div id="tabs-1">
					<fieldset>
						<legend> Compromisos sin layout	</legend>
						<jsp:include page="lista.jsp"></jsp:include>
					</fieldset>	
				</div>
				<div id="tabs-2">
				    <fieldset>
						<legend> Integraci&oacute;n del layout para ser enviado a SICOP </legend>
				    	<jsp:include page="listaEnviados.jsp"></jsp:include>
					</fieldset>
				</div>
		        <!--
		        <div id="tabs-3">
				</div>
		        
				<div id="tabs-4">
					 <fieldset>
						<legend> Compromisos rechazados por SICOP</legend>
				    	<jsp:include page="listaRechazados.jsp"></jsp:include>
					</fieldset>
				</div>
				-->
			</div>
		</div>
	</body>
</html>
