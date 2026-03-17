
<%@page import="com.syc.ejercido.pagado.EgresosBusinessLogic"%>
<%@page import="com.syc.gestion.core.Operacion"%>
<%@page import="com.syc.fortimax.core.GetDatosNodo"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.fortimax.core.DocumentoManager"%>
<%@page import="com.syc.fortimax.core.Documento"%>
<%
	boolean isSearch = "true".equals(request.getParameter("search"));
	boolean isConsulta = "true".equals(request.getParameter("Consulta"));
	boolean esPago = false;
	boolean pagoConLayout = false;
	
    Caso objCaso = (Caso)session.getAttribute(GestionInterface.ATT_CASE);
    
    if( objCaso != null && (objCaso.getIdTC() == 4 || objCaso.getIdTC() == 5 || objCaso.getIdTC() == 6  || objCaso.getIdTC() == 11  || objCaso.getIdTC() == 21  || objCaso.getIdTC() == 43 ) ){
    	EgresosBusinessLogic ebl = new EgresosBusinessLogic( GestionInterface.ATT_CONEXION );
    	pagoConLayout = ebl.esPagoConLayout(objCaso);
    }
    
    Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
    int id_oper = objCaso.getCasoOperacion(0).getIdOperacion();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Contenedor de Caso en Ejecucion</title>
		<link href="../css/gestion.css" rel="stylesheet">
		<script src="../js/jquery.js" type="text/javascript"></script>
		<script src="../js/jquery_std.js" type="text/javascript"></script>
		<script src="../js/jq-tabs.js" type="text/javascript"></script>
		<script src="../js/jq-corner.js" type="text/javascript"></script>
		<script type="text/javascript">
		
		$(document).ready(function() {
			$('#actions').hide();
			$.tabs("container");
			$('.tabs a').each ( function(i) { $(this).corner("top"); } );
			$("#fichaCaso").click(function(){
				$('#actions').hide();
				document.frames['caso'].location.reload();
					
			});
			$("#fichaAdjunto").click(function(){
				$('#actions').show();
				document.frames['doctree'].location.reload(1);
				
			});
		});
		
		function acciones(type,nodo,duenio) {
		    
		    //Ethiel, se agrega parametro nodo para manipular botones de acuerdo a la carpeta vs paso del flujo
			
			var gaveta=nodo.substring(0,nodo.indexOf('_'));
			var gabinete=nodo.substring(nodo.indexOf('G')+1,nodo.indexOf('C'));
			var carpeta='';
			var isDoc=false;
			if(nodo.indexOf('D')!=-1){
				carpeta=nodo.substring(nodo.indexOf('C')+1,nodo.indexOf('D'));
				var documento=nodo.substring(nodo.indexOf('D')+1);
				isDoc=true;		
			}
			else
				carpeta=nodo.substring(nodo.indexOf('C')+1);
			
			if(gaveta=="GAVETA"){//restricciones para carpetas segun el paso del flujo y gaveta este es solo  un ejemnplo
				if( (carpeta==0||carpeta==1)||
					//<%=id_oper%>==1&&carpeta!=3 ||
					//<%=id_oper%>==2 ||
					<%=id_oper%>==1&&carpeta!=2&&carpeta!=3&&carpeta!=4&&carpeta!=5 ||
					<%=id_oper%>==5&&carpeta!=6 ||
					<%=id_oper%>==6&&carpeta!=7 ||
					<%=id_oper%>==7 ||
					<%=id_oper%>==8&&carpeta!=8 ||
					<%=id_oper%>==9&&carpeta!=8 ||
					<%=id_oper%>>=10&&<%=id_oper%><=16 ||
					<%=id_oper%>==17&&carpeta!=9&&carpeta!=10 ||
					<%=id_oper%>>=18
				){
					type='all';
				}
				if(carpeta==11&&isDoc&&duenio=='<%=usuario.getLogin()%>'){//solo permitira borra en la carpeta "Otros" cuando el usuario firmado sea dueño del documento
					type='doc';
				}
				if(carpeta==11&&!isDoc){
					type='fld';
				}
														
			}			
			<%
			if(isConsulta){
			%>
			type = 'all';
			<%
			}
			%>
			switch (type) {
				case "all":
					$("#newfldr").removeClass("on").addClass("off").disable();
					$("#newimg").removeClass("on").addClass("off").disable();
					
					//VGC Temporalmente comento esta linea para que siempre se puedan crear nuevos documentos
					//$("#newdoc").removeClass("on").addClass("off").disable();
					$("#newdoc").removeClass("off").addClass("on").enable();
					
					$("#deldoc").removeClass("on").addClass("off").disable().unclick();
					break;
				case "doc":
					$("#newfldr").removeClass("on").addClass("off").disable();
					$("#newimg").removeClass("on").addClass("off").disable();
					$("#newdoc").removeClass("on").addClass("off").disable();
					
					//VGC Temporalmente comento esta linea para que no puedan eliminar documentos, solo versionarlos.
					/*
					$("#deldoc").removeClass("off").addClass("on").enable().unclick().click(function(){
						return window.confirm("Desea eliminar el documento seleccionado?");
					});
					*/
					
					break;
					
				case "fld":
				
					$("#newfldr").removeClass("off").addClass("on").enable();
					$("#newimg").removeClass("off").addClass("on").enable();
					$("#newdoc").removeClass("off").addClass("on").enable();
					$("#deldoc").removeClass("on").addClass("off").disable().unclick();
					
					break;
			}
		}

		function alternar(Seccion,Objetivo){
			if (Objetivo == "caso"){
				if (document.getElementById(Seccion).style.display == ""){
					document.getElementById(Seccion).style.display="none";
				}	
			}else if (Objetivo == "documento"){
				if (document.getElementById(Seccion).style.display == "none"){
					document.getElementById(Seccion).style.display="";
				}
			}
		};
		</script>
		<style type="text/css">
		 html body, form {
		 	margin: 0px;
		 	padding: 0px;
		 	border: 0px;
			overflow: hidden;
		 }
		.tabs {
		    display: inline-block; /* @ IE 7 */
			list-style: none;
			margin: 0px;
			padding: 0px 0px 0px 0px;
			overflow: hidden;
		}
		.tabs li {
			float: left;
			margin: 0px 1px 0px 0px;
		}
		.tabs a {
		    float: left;
			display: block;
			width: 100px;
			position: relative;
			border-bottom: 0px;
			background: #696969; /*url(../images/greycurve.png) 0 -10px;*/
			z-index: 2;
			color: #99F;
			text-decoration: none;
			text-align: center;
		}
		.tabs .on a {
			padding-bottom: 0px;
			font-weight: bold;
		}
		.tabs a:focus,.tabs a:active {
			outline: none; /* @ Firefox 1.5, remove ugly dotted border */
		}
		.tabs a:hover, .tabs .on a,.tabs .on a:hover,.tabs a:active {
			background: #000000; /*url(../images/bluecurve.png) 0 -10px;*/
		}
		.tabs .on a:link,.tabs .on a:visited {
			/* @ Opera, use pseudo classes otherwise it confuses cursor... */
			cursor: text;
		}
		.tabs a:hover,.tabs a:focus,.tabs a:active {
			cursor: pointer;
		}
		.anchor div.pad {
			margin: 0px;
			padding: 0px;
			border: 1px solid navy;
		}
		.on {
			filter: normal;
		}
		.off {
			filter: gray;
		}
		
		a:link {
			font-family: Arial;
			color: #ffa500;
			text-decoration: none;
			font-size: 10pt;
			font-style: normal;
		}
		
		a:visited {
			font-family: Arial;
			color: #1d774a;
			text-decoration: none;
			font-size: 10pt;
			font-style: normal;
		}
		
		a:hover {
			font-family: Arial;
			color: #ff8000;
			text-decoration: none;
			font-size: 10pt;
			font-style: normal;
		}

		#actions {
			position: relative;
			z-index: 1;
			height:23px;
			font: 10pt Arial !important;
		}
		
        </style>
	</head>
	<body>
		
		<div id="actions">
			<form action="../caso/actions.jsp" method="post" target="main">
			<input type="hidden" name="reload" id="reload" value="no">
				<table align="right" style="background: none;">
					<tr>
						<td><img src="../images/divisor.gif" width="2" height="22"></td>
						<td><input type="image" name="newfldr" id="newfldr" src="../images/b_carpeta.gif" title="Crear carpeta" width="22" height="22"></td>
						<td><font style="font: 9pt Arial;">Crear carpeta</font></td>
						<td><img src="../images/divisor.gif" width="3" height="22"></td>
						<!-- <td><input type="image" name="newimg" id="newimg" src="../images/b_digitalizar.gif" title="Digitalizar documento" width="22" height="22"></td>
						<td><font style="font: 9pt Arial;">Digitalizar documento</font></td>
						<td><img src="../images/divisor.gif" width="3" height="22"></td> -->
						<td><input type="image" name="newdoc" id="newdoc" src="../images/b_respaldar_archivo.gif" title="Crear documento" width="22" height="22"></td>
						<td><font style="font: 9pt Arial;">Crear documento</font></td>
						<td><img src="../images/divisor.gif" width="3" height="22"></td>
						<!-- <td><input type="image" name="deldoc" id="deldoc" src="../images/b_eliminar.gif" title="Borrar documento" width="22" height="22"></td>
						<td><font style="font: 9pt Arial;">Borrar documento</font></td>
						<td><img src="../images/divisor.gif" width="3" height="22"></td> -->
						<!-- VGC Limpiar documento. -->
						<td><input type="image" name="cleandoc" id="cleandoc" src="../images/b_limpiar.png" title="Limpiar documento" width="22" height="22"></td>
						<td><font style="font: 9pt Arial;">Versiona documento</font></td>
						<td><img src="../images/divisor.gif" width="3" height="22"></td>
						
					</tr>
				</table>
			</form>
		</div>


		<div id="container">
			<ul class="tabs">
				<li><a href="#caso" onclick="window.frames['caso'].location.reload();$('#actions').hide();" id="fichaCaso">Documento</a></li>
				<li><a href="#documento" onclick="window.frames['doctree'].location.reload(1);$('#actions').show();">Adjuntos</a></li>
				
			</ul>
			<div id="caso" class="anchor">
				<iframe name="caso" id="caso" src="exec-caso.jsp?search=<%=isSearch%>" height="92%" width="100%" frameborder="0"></iframe>
			</div>
			<div id="documento" class="anchor">
				<table width="100%" height="97%" cellpadding="0" cellspacing="0" style="padding: 0px; margin: 0px; border: 0px;">
					<tr>
						<td width="80%">
							<iframe name="main" src="blank.jsp" height="100%" width="100%" frameborder="0"></iframe>
						</td>
						<td width="20%" style="border-left: 1px solid black">
							<iframe name="doctree" src="document-tree.jsp" height="100%" width="100%" frameborder="0"></iframe>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>
