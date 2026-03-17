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
		<title>Catálogo de Beneficiarios</title>

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
		var sIdRFC;
			
		$(document).ready(function() {
			
			oTable = $("#tblBeneficiarios").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vBeneficiariosGrid",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "CBEN" },
					{ sName: "dRFC"   },
					{ sName: "Nombre" },
					{ sName: "cTipoPersonaRFC"	}
				]
        	});
			
			$("#tblBeneficiarios tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('gradeA');
				});
				$(event.target.parentNode).addClass('gradeA');
				$("#pbDesplegar").css("visibility","visible");
			});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbAgregar')
				.button()
				.click( function() {

					location.href = 'BeneficiariosForm.jsp?Op=A';
				} );
			
			$("#pbDesplegar")
				.button()
				.click(function() {

					ObtenParamEnv();
					//alert (encodeURIComponent("&"));
					location.href = 'BeneficiariosForm.jsp?Op=D&Re=' + encodeURIComponent(sIdRFC);
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
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}
			
			function ObtenParamEnv()
			{
				var aTrs = oTable.fnGetNodes();    
				var rowsTbl = $("#tblBeneficiarios").dataTable().fnGetData();      
				
				for ( var i=0 ; i<aTrs.length ; i++ )     
				{
					if ( $(aTrs[i]).hasClass('gradeA') )         
					{             
						sIdRFC = rowsTbl[i][1];
						break;
					} 							    
				}   		
			}

		
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<div id="container" class="container" style="width: 65%" >	
			<h1>Beneficiarios</h1>
				<input type="button" id="pbAgregar" style="visibility: hidden" value="Agregar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbDesplegar" style="visibility: hidden" value="Desplegar"/>
			<br/><br/>
			<table id="tblBeneficiarios" class="display"  >
	            <thead>
	                <tr>
	                	<th width="60px">C&oacute;digo SICOP</th>
	                	<th width="130px" align="center">R.F.C.</th>
	                    <th>Nombre</th>
	                    <th>Tipo Persona</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>