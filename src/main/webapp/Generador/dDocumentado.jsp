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
		var sNoSoe="";
		var sEstatusSoe="";

		// Candado para consultar registros NO DOCUMENTADOS
		var xWhere = " fechaNoObjecion IS NULL" ;

		$(document).ready(function() {
			
			$("#tblSoeDocumentado tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					var aTrs = $('#tblSoeDocumentado').dataTable().fnGetNodes();
					if (aTrs.length == 0)
						return;
						
					$(event.target.parentNode).addClass('row_selected');
					$("#pbDesplegar").css("visibility","visible");
					$("#pbModificar").css("visibility","visible");
					$("#pbDescartar").css("visibility","visible");
					$("#pbEnviar").css("visibility","visible");
					$("#pbCerrar").css("visibility","visible");					
				});

			oTable = $("#tblSoeDocumentado").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vdoc_dSOE&qw="+xWhere,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ID_Prestamo", bVisible: false },
					{ sName: "NumeroPrestamo" },
					{ sName: "Numero_SOE" },
					{ sName: "Estatus"	}
				]
        	});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$("#pbAgregar")
				.button()
				.click( function() {
                    // envia A para Cambiar informacion consultando el registro
                     
					location.href = 'dDocumentadoA.jsp?Op=A';
				});


			$("#pbModificar")
				.button()
				.click( function() {

					ObtenParamEnv();
					if ( sEstatusSoe == 'CAPTURA' ) {
							// envia D para crear un registro nuevo
					 		location.href = 'dDocumentadoD.jsp?Op=M&SOEp='  + sNoSoe + '&SEOEst='+ sEstatusSoe;
							//location.href = 'dDocumentadoD.jsp?Op=D' ;
					}
					else {
							alert("Acción no permitida a este SOE");
					}
				});

			$("#pbDescartar")
				.button()
				.click( function() {
					ObtenParamEnv();
					if (sEstatusSoe == 'CAPTURA' ) {
							// envia D para crear un registro nuevo
							 location.href = 'dDocumentadoD.jsp?Op=B&SOEp='  + sNoSoe + '&SEOEst='+ sEstatusSoe;
							//location.href = 'dDocumentadoD.jsp?Op=D' ;
					}
					else {
							alert("Acción no permitida a este SOE");
					}
				});

			$("#pbEnviar")
				.button()
				.click( function() {
					ObtenParamEnv();					
					if (sEstatusSoe == 'CAPTURA' ) {
							// envia D para crear un registro nuevo
					 		location.href = 'dDocumentadoD.jsp?Op=E&SOEp='  + sNoSoe + '&SEOEst='+ sEstatusSoe;
							//location.href = 'dDocumentadoD.jsp?Op=D' ;
					}
					else {
							alert("Acción no permitida a este SOE");
					}
				});

			$("#pbCerrar")
				.button()
				.click( function() {
					ObtenParamEnv();
					if ( sEstatusSoe == 'ENVIADO' ) {
							// envia D para crear un registro nuevo
					 		location.href = 'dDocumentadoD.jsp?Op=C&SOEp=' + sNoSoe + '&SEOEst='+ sEstatusSoe;
							//location.href = 'dDocumentadoD.jsp?Op=D' ;
					}
					else {
							alert("Acción no permitida a este SOE");
					}
				});

			$("#pbDesplegar")
				.button()
				.click(function() {
				
					ObtenParamEnv();
					// envia D para crear un registro nuevo
					 location.href = 'dDocumentadoD.jsp?Op=D&SOEp=' + sNoSoe + '&SEOEst='+ sEstatusSoe;
					//location.href = 'dDocumentadoD.jsp?Op=D' ;
				});				

			$("#pbNuevo")
				.click(function() {
					 location.href = 'dSOEGrid.jsp';
				});		
							
			function ObtenParamEnv()
			{
				var aTrs = oTable.fnGetNodes();    
				var rowsTbl = $("#tblSoeDocumentado").dataTable().fnGetData();      
				
				for ( var i=0 ; i<aTrs.length ; i++ )     
				{
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{             
						sNoSoe = rowsTbl[i][2];
						sEstatusSoe = rowsTbl[i][3];
						break;
					} 							    
				}   		
			}


		});
		
		function formSubmited() {
                alert("Enviado!");
            }
		

		function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('row_selected') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}		
			
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<input type="button" id="pbNuevo" value="otro"/>
		<div id="container" class="container">	
			<h1>SOE Documentado</h1>
				<input type="button" id="pbAgregar" value="Agregar"/>&nbsp;&nbsp;
				<input type="button" id="pbDesplegar" style="visibility: hidden" value="Desplegar"/>&nbsp;&nbsp;
				<input type="button" id="pbModificar" style="visibility: hidden" value="Modificar"/>&nbsp;&nbsp;
				<input type="button" id="pbDescartar" style="visibility: hidden" value="Descartar"/>&nbsp;&nbsp;
				<input type="button" id="pbEnviar" style="visibility: hidden" value="Enviar"/>&nbsp;&nbsp;
				<input type="button" id="pbCerrar" style="visibility: hidden" value="Cerrar"/>
			<br/><br/>
			<table id="tblSoeDocumentado" class="display"  >
	            <thead>
	                <tr>
	                	<th width="90px">Pr&eacute;stamo</th>
	                	<th width="180px">N&uacute;mero Pr&eacute;stamo</th>
	                	<th width="180px">N&uacute;mero SOE</th>
	                	<th width="90px" align="center">Estatus</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>