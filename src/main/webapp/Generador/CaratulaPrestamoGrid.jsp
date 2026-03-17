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
		<title>Car&aacute;tula de Pr&eacute;stamo</title>

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
			
		$(document).ready(function() {
			
			$("#tblCaratulaPrestamo tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					var aTrs = $('#tblCaratulaPrestamo').dataTable().fnGetNodes();
					if (aTrs.length == 0)
						return;
					$(event.target.parentNode).addClass('row_selected');
					$("#pbDesplegar").css("visibility","visible");
					$("#pbCambiar").css("visibility","visible");
					$("#pbBorrar").css("visibility","visible");
				});

			oTable = $("#tblCaratulaPrestamo").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCaratulaPrestamoGrid",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "id_prestamo",	 bVisible: false  },
					{ sName: "Numero" },
					{ sName: "Nombre"   },
					{ sName: "Estado" },
					{ sName: "Agente" },
					{ sName: "OFI" },
					{ sName: "UR"	}
				]
        	});
			
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$("#pbAgregar")
				.button()
				.click( function() {

					location.href = 'CaratulaPrestamoForm.jsp?Op=A';
				} );
			
			$("#pbCambiar")
				.button()
				.click(function() {

					var sPrestamo="";
					var aTrs = oTable.fnGetNodes();    
					var rowsTbl = $("#tblCaratulaPrestamo").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							sPrestamo = rowsTbl[i][0];
							break;
						} 							    
					}   			
					
					location.href = 'CaratulaPrestamoForm.jsp?Op=C&Re=' + sPrestamo;
				});	
				
			$("#pbDesplegar")
				.button()
				.click(function() {

					var sPrestamo="";
					var aTrs = oTable.fnGetNodes();    
					var rowsTbl = $("#tblCaratulaPrestamo").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							sPrestamo = rowsTbl[i][0];
							break;
						} 							    
					}   			
					
					location.href = 'CaratulaPrestamoForm.jsp?Op=D&Re=' + sPrestamo;
				});		
			
			$("#pbBorrar")
				.button()
				.click(function() {

					var sPrestamo="";
					var aTrs = oTable.fnGetNodes();    
					var rowsTbl = $("#tblCaratulaPrestamo").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							sPrestamo = rowsTbl[i][0];
							break;
						} 							    
					}   			
					
					location.href = 'CaratulaPrestamoForm.jsp?Op=B&Re=' + sPrestamo;
				});			
		});
		
		function formSubmited() {
                alert("Beneficiario enviado!");
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
		<div id="container" class="container" style="width:900px">	
			<h1>Car&aacute;tula de Pr&eacute;stamos</h1>
				<input type="button" id="pbAgregar" value="Agregar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbBorrar"    style="visibility: hidden" value="Borrar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCambiar"   style="visibility: hidden" value="Cambiar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbDesplegar" style="visibility: hidden" value="Desplegar"/>
			<br/><br/>
			<table id="tblCaratulaPrestamo" class="display"   >
	            <thead>
	                <tr>
	                	<th>ID</th>
	                	<th align="center">No Pr&eacute;stamo</th>
	                	<th >Nombre Pr&eacute;stamo</th>
	                    <th>Estatus</th>
	                    <th>Agente Financiero</th>
	                    <th>OFI</th>
	                    <th>UR</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>