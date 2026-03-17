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
		<title>Convenio</title>

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
			.alignRight { text-align: right; }
			.alignCenter { text-align: center; }
			.alignLeft { text-align: left; }
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
			
			$("#tblConvenio tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					
					var aTrs = $('#tblConvenio').dataTable().fnGetNodes();
					if (aTrs.length == 0)
						return;
					$(event.target.parentNode).addClass('row_selected');
					$("#pbDesplegar").css("visibility","visible");
					$("#pbBorrar").css("visibility","visible");
					$("#pbCambiar").css("visibility","visible");
					
				});
				
			
			oTable = $("#tblConvenio").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vConveniosGrid",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 10, "desc" ]] ,
				aoColumns: [
					{ sName: "IdCv", bVisible: false  },
					{ sName: "IdCt", bVisible: false   },
					{ sName: "IdPr", bVisible: false   },
					{ sName: "Numero",	sClass: "alignCenter" },
					{ sName: "Contrato",	sClass: "alignCenter" },
					{ sName: "NumeroPrestamo", 	sClass: "alignLeft"   },
					{ sName: "Estado",	sClass: "alignCenter" },
					{ sName: "RFC",	sClass: "alignCenter" },
					{ sName: "Importe",	sClass: "alignRight" },
					{ sName: "Ano",	sClass: "alignCenter"	},
					{ sName: "FechaUltimoMovimiento",	sClass: "alignCenter"	},
					{ sName: "TipoCarga",	sClass: "alignCenter"	}					
				]
        	});
        	
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbAgregar')
				.button()
				.click( function() {

					location.href = 'ConvenioForm.jsp?Op=A';
				} );
			
			$("#pbDesplegar")
				.button()
				.click(function() {

					var sConvenio1="";
					var sConvenio2="";
					var sConvenio3="";
					var aTrs1 = oTable.fnGetNodes();    
					var rowsTbl1 = $("#tblConvenio").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs1.length ; i++ )     
					{
						if ( $(aTrs1[i]).hasClass('row_selected') )         
						{             
							sConvenio1 = rowsTbl1[i][0];
							sConvenio2 = rowsTbl1[i][1];
							sConvenio3 = rowsTbl1[i][2];
							break;
						} 							    
					}   			
					location.href = 'ConvenioForm.jsp?Op=D&Re1=' + sConvenio1 + '&Re2=' + sConvenio2 + '&Re3=' + sConvenio3;
				});
				
			$("#pbBorrar")
				.button()
				.click(function() {

					var sConvenio1="";
					var sConvenio2="";
					var sConvenio3="";
					var aTrs1 = oTable.fnGetNodes();    
					var rowsTbl1 = $("#tblConvenio").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs1.length ; i++ )     
					{
						if ( $(aTrs1[i]).hasClass('row_selected') )         
						{             
							sConvenio1 = rowsTbl1[i][0];
							sConvenio2 = rowsTbl1[i][1];
							sConvenio3 = rowsTbl1[i][2];
							break;
						} 							    
					}   			
					location.href = 'ConvenioForm.jsp?Op=B&Re1=' + sConvenio1 + '&Re2=' + sConvenio2 + '&Re3=' + sConvenio3;
				});
				
			$("#pbCambiar")
				.button()
				.click(function() {

					var sConvenio1="";
					var sConvenio2="";
					var sConvenio3="";
					var aTrs1 = oTable.fnGetNodes();    
					var rowsTbl1 = $("#tblConvenio").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs1.length ; i++ )     
					{
						if ( $(aTrs1[i]).hasClass('row_selected') )         
						{             
							sConvenio1 = rowsTbl1[i][0];
							sConvenio2 = rowsTbl1[i][1];
							sConvenio3 = rowsTbl1[i][2];
							break;
						} 							    
					}   			
					location.href = 'ConvenioForm.jsp?Op=C&Re1=' + sConvenio1 + '&Re2=' + sConvenio2 + '&Re3=' + sConvenio3;
				});
			$("#pbCargar")
				.button()
				.click(function() {

					location.href = 'ConvenioCarga.jsp';
				});
						
		});
		
		
		
				
			function formSubmited() {
                alert("Convenio enviado!");
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
		<div id="container" class="container" style="width:1100px">	
			<h1>Convenios Modificatorios</h1>

			<table border="0" style="width:100%">
				<tr>
					<td>
						<input type="button" id="pbAgregar" value="Agregar"/>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbBorrar" style="visibility: hidden" value="Borrar"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbCambiar" style="visibility: hidden" value="Cambiar"/>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbDesplegar" style="visibility: hidden" value="Desplegar"/>
					</td>
					<td  align="right">
						<input type="button" id="pbCargar" value="Cargar"/>&nbsp;&nbsp;&nbsp;&nbsp;
					</td>
				</tr>
			</table>	
			<br/>
			<table id="tblConvenio" class="display"  >
	            <thead>
	                <tr>
	                	<th>IdCv</th>
	                	<th>IdCt</th>
	                	<th>IdPr</th>
	                	<th>No. Convenio</th>
	                	<th>No. Contrato</th>
	                	<th>N&uacute;mero Pr&eacute;stamo</th>
	                    <th>Entidad Fed</th>
	                    <th>RFC Beneficiario</th>
	                    <th  width="110px">Importe</th>
	                    <th>Ej Fiscal</th>
	                   	<th>Fecha Último Mov</th>
	                    <th>Tipo Carga</th>	     
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	</body>
</html>