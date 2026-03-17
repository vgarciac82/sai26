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
<!DOCTYPE HTML>
<html>
	<head>
		<title>Catálogo de Entidades Federativas</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
		</style>
		<link rel="stylesheet" type="text/css"	href="../css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../css/bootstrap.min.css"></link>
		
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		
		var oTable;
			
		$(document).ready(function() {
			
			querySelectPost("MCATALOGOENTIDADfEDERATIVA", "UNIDAD", {async : false});
			
			mostrar();
			cerrardiv('formulario');
			
			$('#tblConsultaConsolidados tr').live('click', function() {         
					$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );
					$(event.target.parentNode).addClass('gradeA');
					if (anSelected != "") {
						var aData = oTable.fnGetData(anSelected[2]);
					}
					alert(anSelected[2]);
				});	
						
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			$("#btnBuscar").button();
			$("#btnBuscar").button().click(function(){				
				var aTrs = $('#tblCatalogoEF').dataTable().fnGetNodes();								
	        		for ( var i=aTrs.length-1 ; i>=0; i-- )     
					{
						$(aTrs[i]).addClass('row_selected');									    
					}            					 
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
		function guardar(){
			//var validaInputText=validarInputTest();
			//if(validaInputText==1){
				document.getElementById('AREAS');
				document.getElementById('SIGLAS');
				document.getElementById('UNIDAD_EJECUTORA');
				//alert(document.getElementById('AREAS').val());
				queryFormPost("actualizaCatalogoentidadesFederativas", {async: false});	
				cerrardiv('formulario');
				mostrardiv('tabla');
				mostrar();
			//}
			//else
				//alert('Datos Requeridos');	
			
				
		}
		
		
		function mostrar() {
				var qw = " 1 = 1";	
			
				if ($("#UNIDAD").val() != "0")
					qw += " AND cIdEntidadFederativa= '" + $("#UNIDAD").val()+"'";
					
			
				oTable = $("#tblCatalogoEF").dataTable({
				sScrollX: "100%",
				sScrollXInner: "100%",
				bScrollCollapse: true,
				bScrollCollapse: true,
				bDestroy: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CatalogoEntidadesFederativasRPT&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ENTIDAD_FEDERATIVA" },
					{ sName: "UNIDAD_EJECUTORA"   },
					{ sName: "AREAS" },
					{ sName: "SIGLAS"	}
					
					
					
				]
        	});
				
		}
		function cerrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display='none';
		}
		function mostrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display ='block';
		}
		function bloqueaInputText() { 
			document.modificarDatos.ENTIDAD_FEDERATIVA.disabled = true;
			alert(document.modificarDatos.ENTIDAD_FEDERATIVA.val);
			//document.textInput.disabled = true; 
		} 
		
		
		</script>
		

</head>

<body id="dt_example" >
	<!--  id="container" class="container" -->
		<div  id="tabla">	
			<p align="left">Catálogos de Entidades Federativas</p>
			<div class="row">
				<div class="col-2">
					Entidad Federativa:
				</div>
				<div class="col-8">
					<select id="UNIDAD" name="UNIDAD" class="form-select"></select>
				</div>
				<div class="col-2">
					<input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" onclick="mostrar();" class="btn btn-secondary"/>
				</div>
			</div>
			<br>
			<table id="tblCatalogoEF" class="display"  width="80%">
	            <thead>
	                <tr>
	                	<th  align="center" width="20%">Entidad Federativa</th>
	                	<th  width="10%" >Unidad Ejecutora</th>	                	
	                    <th width="50%">Area</th>
	                    <th width="20%">Siglas</th>
	                   
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
		
		
	<div id="formulario" >
		<form id="modificarDatos" name="modificarDatos" >
			<fieldset>
				<legend>Entidades Federativas</legend>
				<h1>Datos a Modificar</h1>
				
				<label>ENTIDAD_FEDERATIVA:</label><br /><input id="ENTIDAD_FEDERATIVA" name="ENTIDAD_FEDERATIVA" disabled="disabled" type="text" style="width: 20em;" ><br>
				<input id="UNIDAD_EJECUTORA" name="UNIDAD_EJECUTORA" type="hidden" style="width: 20em; " >
				<label>Area:</label><br><input id="AREAS" name="AREAS" type="text" maxlength="25" style="width: 20em;" ><br />
				<label>Siglas:</label><br><input id="SIGLAS" name="SIGLAS" type="text" maxlength="15" style="width: 20em;" > <br />
				<br /><tr id="trPyme" align="left">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" onclick="guardar();" /></td>					
				</tr>
			</fieldset>
		</form>
		
	</div>
	</body>
</html>