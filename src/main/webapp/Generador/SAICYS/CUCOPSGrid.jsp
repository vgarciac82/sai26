<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
/*Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}*/
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>CUCPOS</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

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
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			
			$("#tblCucops tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					$("#pbDesplegar").css("visibility","visible");
				});

			oTable = $("#tblCucops").dataTable({
				bAutoWidth : false,
				sScrollY: "90%",
				sScrollX: "90%",
				sScrollXInner: "90%",				
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCucopsGrid",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cIdSubPartida"   },					
					{ sName: "cCABM"	},
					{ sName: "cUnidadMedida" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" },
					{ sName: "nPorcentajeIVA" },
					{ sName: "mImporteBruto" },														
					{ sName: "mImporteNeto" }
				]
        	});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbAgregar')
				.button()
				.click( function() {

					location.href = 'CUCOPSForm.jsp?Op=A';
				} );
			
			$("#pbDesplegar")
				.button()
				.click(function() {

					var iPos = 0;
					var anSelected = fnGetSelected( oTable ); 	
					
					if (anSelected[0].innerText.indexOf('PERSONA FISICA')!=-1 || anSelected[0].innerText.indexOf('EMPLEADO C.N.A.')!=-1) 
						iPos = 15;
					else
						iPos = 14;
					
					var sRFC = anSelected[0].innerText.substr(6,iPos);
					
					location.href = 'BeneficiariosForm.jsp?Op=D&Re=' + sRFC;
				});
			 /* Add/remove class to a row when clicked on    
			  * 
			  * @memberOf {TypeName} 
			  */
			  $('#example tr').click( function() {    
				  $(this).toggleClass('row_selected');     } ); 
			  /* Init the table    
			   * 
			   */
			   var oTable = $('#example').dataTable( ); 					
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
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}
		function fnGetSelected( oTableLocal ) 
		{    
			return oTableLocal.$('tr.row_selected'); 
			} 
		

		
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<div id="container" class="container">				
			<br/><br/>
			<table id="example" class="display">
	            <thead>
	                <tr>
	                	<th>cIdCABM</th>
	                	<th>cIdSubPartida</th>	                    
	                    <th>cCABM</th>
	                    <th>cUnidadMedida</th>
	                    <th>nCantidad</th>
	                    <th>mPrecioUnitario</th>
	                    <th>nPorcentajeIVA</th>	                    	                                     	                    	                    
	                    <th>mImporteBruto</th>	                    
	                    <th>mImporteNeto</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>