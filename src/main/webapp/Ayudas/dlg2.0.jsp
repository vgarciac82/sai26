<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">


<html>
	<head>
		<title>Ayudas</title>
		<meta http-equiv="Expires" content="0">
		<meta http-equiv="Last-Modified" content="0">
		<meta http-equiv="Cache-Control" content="no-cache, max-age=1">
		<meta http-equiv="Pragma" content="no-cache">
				
		<script type='text/javascript' src='../js/jquery-1.2.6.js'></script>
		<script type="text/javascript" src="../js/jquery.tablesorter.min.js"></script>
		<script type='text/javascript' src='js/ayudasDlg2.0.js'></script>
		<script type="text/javascript" src="js/autoCompleta.js"></script>
				
		<link rel="stylesheet" href="css/autocompleta.css" type="text/css"></link>
		
		<link rel="stylesheet" href="../css/datepickercontrol_bluegray.css" type="text/css" />
		<script src="../js/datepickercontrol_1_1_1.js" type="text/javascript"></script>
		
		<style type="text/css" media="screen">
			span.labelAyuda {
				font-family: Tahoma, Verdana, Arial, sans-serif;
				font-size: 10pt;
				color: #003366;
			}
			div.divGrid {
				border: gray 1px solid; 
				font-family: Tahoma, Verdana, Arial, sans-serif; 
				font-size: 8pt; 
				background: WhiteSmoke;
			}	
			
			/* estilos del sort table */
			table.tblGrid {
				border: gray 1px solid;
				/*table-layout: fixed;*/
				background-color: white;
			}
			th.ColConsulta {				
				padding-left: 20px;
				padding-top: 5px; 
				padding-bottom: 5px; 
				padding-right: 5px; 
				border-right: 1px solid #CBC7B8;
				border-bottom: 1px solid #CBC7B8;
				text-align: left;
				font-family: Verdana, Arial, Tahoma, sans-serif; 
				font-size: 8pt; 
				font-weight: normal;
			}
			td.tdDetalle {
				padding: 3px; 
				COLOR: gray; 
				cursor:default
				border-right: 1px solid #D4D0C8;
				border-bottom: 1px solid #D4D0C8;
				font-family: Verdana, Tahoma, Arial, sans-serif; 
				font-size: 8pt;
				font-weight: normal;
			}

			tr.even {
				BACKGROUND-COLOR: whitesmoke;
			}
			tr.odd {
				BACKGROUND-COLOR: white;
			}
			.odd TD {
				/*COLOR: #000;*/
			}
			.highlight {
				FONT-WEIGHT: bold; 
				BACKGROUND-COLOR: #3d3d3d
			}

			.header {
				PADDING-LEFT: 20px;   
				BACKGROUND-IMAGE: url(../imagenes/dash.jpg);   
				BACKGROUND-REPEAT: no-repeat; 
			}
			.headerSortUp {
				PADDING-LEFT: 20px; 
				BACKGROUND-IMAGE: url(../imagenes/header-asc.jpg); 
				BACKGROUND-REPEAT: no-repeat; 
			}
			.headerSortDown {
				PADDING-LEFT: 20px; 
				BACKGROUND-IMAGE: url(../imagenes/header-desc.jpg); 
				BACKGROUND-REPEAT: no-repeat;
			}
			
			nada {
				WIDTH: 1px;
			}


			thead.fixedHeader tr {
				position: relative;
				top: expression(document.getElementById("divTableContainer").scrollTop );
			}
					                                    
			head:first-child + body thead[class].fixedHeader tr {
				display: block;
			}

			tr.hover  {
				background: #E0E3EF;
			}
		</style>
		
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$("#divTXTyTables").find('input:text').each(function() {
					$("#" + this.id).bind('keypress', function(event) {
						var code = event.keyCode;
						if (code == 13) {
							subLlenaGridAyuda();
						}
					});
				});
			});
			
		</script>

	</head>
	<body id='dlgAyudas' bottommargin='0' topmargin='0' leftmargin='0' rightmargin='0'>
		<form id="Form1">
			<div style='overflow:hidden; WIDTH: 100%; HEIGHT: 18px; BACKGROUND-COLOR: #000000;'>
				<img align='right' onclick='window.close();' src='imagenes/close.png' title='Cerrar ayuda' style='CURSOR: hand;'/>
				<span id='spTitulo' style="POSITION: absolute; LEFT: 5px; FONT-SIZE: 12pt; FONT-FAMILY: Tahoma, Verdana, Arial; COLOR: white;">Ayudas</span>
			</div>
			<img src='imagenes/toolbar.png' width='100%' height='40px'/>
			<img id='btnGridFiltrar' name='btnGridFiltrar' class='btnGrid' src='imagenes/buscar.png'  onmouseout="this.style.border ='white 0px solid'" onmouseover="this.style.border = (this.src.indexOf('buscar.png')>0 ? 'white 1px solid':'white 0px solid')"  style='POSITION: absolute; TOP: 25px; LEFT:20px; CURSOR: hand; BORDER: white 0px solid;' title='Aplicar criterio de búsqueda'/>
			<img id='btnGridAgregar' name='btnGridAgregar' class='btnGrid' src='imagenes/agregar.png' onmouseout="this.style.border ='white 0px solid'" onmouseover="this.style.border = (this.src.indexOf('agregar.png')>0 ? 'white 1px solid':'white 0px solid')" style='POSITION: absolute; TOP: 25px; LEFT:120px; CURSOR: hand; BORDER: white 0px solid; VISIBILITY:visible;' title='Agregar registro'/>
			<img id='btnGridBorrar'  name='btnGridBorrar'  class='btnGrid' src='imagenes/borrar_d.png'  onmouseout="this.style.border ='white 0px solid'" onmouseover="this.style.border = (this.src.indexOf('borrar.png')>0 ? 'white 1px solid':'white 0px solid')"  style='POSITION: absolute; TOP: 25px; LEFT:220px; CURSOR: hand; BORDER: white 0px solid; VISIBILITY:visible;' title='Borrar registro'/>
			<img id='btnGridCambiar' name='btnGridCambiar' class='btnGrid' src='imagenes/Cambiar_d.png' onmouseout="this.style.border ='white 0px solid'" onmouseover="this.style.border = (this.src.indexOf('Cambiar.png')>0 ? 'white 1px solid':'white 0px solid')" style='POSITION: absolute; TOP: 25px; LEFT:320px; CURSOR: hand; BORDER: white 0px solid; VISIBILITY:visible;' title='Cambiar registro'/>
			<img id='btnGridLimpiar' name='btnGridLimpiar' class='btnGrid' src='imagenes/Limpiar.png' onmouseout="this.style.border ='white 0px solid'" onmouseover="this.style.border = (this.src.indexOf('Limpiar.png')>0 ? 'white 1px solid':'white 0px solid')" style='POSITION: absolute; TOP: 25px; LEFT:420px; CURSOR: hand; BORDER: white 0px solid;' title='Limpiar'/>
			<div id='divTXTyTables' style='OVERFLOW: auto; PADDING:6px; '>
			</div>
			<div id='divGrid' class='divGrid' style='overflow:auto; POSITION: relative; PADDING:1px; WIDTH: 0px; HEIGHT: 385px' >
				<div id="divTableContainer" class="divTableContainer" style='OVERFLOW: auto; HEIGHT: 100%; BACKGROUND-COLOR: #AAAAAA'></div>
			</div>
		</form>
	</body>
</html>




