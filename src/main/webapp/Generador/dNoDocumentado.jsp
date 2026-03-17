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
		<title>Generación SEO No Documentado</title>

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
		
		// Candado para consultar registros NO DOCUMENTADOS
		var xWhere = " fechaNoObjecion IS NOT NULL" ;
		
		$(document).ready(function() {
			
			$("#tblSoeDocumentado tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
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
					{ sName: "ID_Prestamo" },
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
					location.href = 'dNoDocumentadoA.jsp?Op=A';
				});


			$("#pbModificar")
				.button()
				.click( function() {

					ObtenParamEnv();
					
					// envia D para crear un registro nuevo
					 location.href = 'dNoDocumentadoD.jsp?Op=M&SOEp=' + $("#ParSoe").val() + '&SEOEst='+ $("#Estatus").val();
					//location.href = 'dDocumentadoD.jsp?Op=D' ;
				});

			$("#pbDescartar")
				.button()
				.click( function() {
					ObtenParamEnv();
					// envia D para crear un registro nuevo
					 location.href = 'dNoDocumentadoD.jsp?Op=B&SOEp=' + $("#ParSoe").val() + '&SEOEst='+ $("#Estatus").val();
					//location.href = 'dDocumentadoD.jsp?Op=D' ;
				});

			$("#pbEnviar")
				.button()
				.click( function() {
					ObtenParamEnv();					
					// envia D para crear un registro nuevo
					 location.href = 'dNoDocumentadoD.jsp?Op=E&SOEp=' + $("#ParSoe").val() + '&SEOEst='+ $("#Estatus").val();
					//location.href = 'dDocumentadoD.jsp?Op=D' ;
				});

			$("#pbCerrar")
				.button()
				.click( function() {
					ObtenParamEnv();
					// envia D para crear un registro nuevo
					 location.href = 'dNoDocumentadoD.jsp?Op=C&SOEp=' + $("#ParSoe").val() + '&SEOEst='+ $("#Estatus").val();
					//location.href = 'dDocumentadoD.jsp?Op=D' ;

				});

			$("#pbDesplegar")
				.button()
				.click(function() {
					
					ObtenParamEnv();

					// envia D para crear un registro nuevo
					 location.href = 'dNoDocumentadoD.jsp?Op=D&SOEp=' + $("#ParSoe").val() + '&SEOEst='+ $("#Estatus").val();
					//location.href = 'dDocumentadoD.jsp?Op=D' ;
				});				

			
			function ObtenParamEnv(){
					var anSelected = fnGetSelected( oTable ); 	
					var iPos = 0;
					var iPosFin = 0;
					var SoeParam = anSelected[0].innerText.substr(3,13);
					if ( anSelected[0].innerText.substr(1,3) =='SOE' ) {
						iPos=1;
						iPosFin=10;
					}
					if ( anSelected[0].innerText.substr(2,3) =='SOE' ) {
						iPos=2;
						iPosFin=11;
					}
					if ( anSelected[0].innerText.substr(3,3) =='SOE' ) {
						iPos=3;
						iPosFin=12;
					}
					if ( anSelected[0].innerText.substr(4,3) =='SOE' ) {
						iPos=4;
						iPosFin=13;
					}
					SoeParam = anSelected[0].innerText.substr(iPos,iPosFin);
					$("#ParSoe").val(SoeParam);
					
					SoeParam = anSelected[0].innerText.substr(iPosFin+2,10);
					$("#Estatus").val(SoeParam);
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
					if ( $(aTrs[i]).hasClass('gradeA') )
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
		<div id="container" class="container">	
			<h1>SOE No Documentado</h1>
				<input type="button" id="pbAgregar" value="Agregar"/>&nbsp;&nbsp;
				<input type="button" id="pbDesplegar" style="visibility: hidden" value="Desplegar">&nbsp;&nbsp;
				<input type="button" id="pbModificar" style="visibility: hidden" value="Modificar">&nbsp;&nbsp;
				<input type="button" id="pbDescartar" style="visibility: hidden" value="Descartar">&nbsp;&nbsp;
				<input type="button" id="pbEnviar" style="visibility: hidden" value="Enviar">&nbsp;&nbsp;
				<input type="button" id="pbCerrar" style="visibility: hidden" value="Cerrar">
			<table id="tblSoeDocumentado" class="display"  >
	            <thead>
	            	<tr>
			   			<td><input type="hidden" id="ParSoe" name="ParSoe" type="text" size="20" maxlength="20"> </td>
			   			<td><input type="hidden" id="Estatus" name="Estatus" type="text" size="20" maxlength="20"> </td>
					</tr>
	                <tr>
	                	<th width="60px">Pr&eacute;stamo</th>
	                	<th width="120px">N&uacute;mero</th>
	                	<th width="60px" align="center">Estatus</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>