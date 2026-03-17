<%@page import="com.syc.gestion.core.Role"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.admin.servlet.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    
    <title>CatalogoSalario</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

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
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
			var oTable;
			$(document).ready(function() {
				mostrar();
				$("#tblTipoGarantia tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					
					$(event.target.parentNode).addClass('gradeA');
					var aTrs = $('#tblTipoGarantia').dataTable().fnGetNodes();
					for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						//alert("0");
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{   
							var nTr = $('#tblTipoGarantia').dataTable().fnGetData(aTrs[i]);
							$("#nId").val(nTr[0]);
							$("#cDescripcion").val(nTr[1]);
							$("#cIdPorcentaje").val(nTr[2]);
							$("#porcentaje").val(nTr[3]);
							
						}     
					}
				});
			});
			function mostrar() {
				var qw = " 1 = 1";
// 				if ($("#cabmsop").val() != "" )
// 					qw += " AND nidCABMSOP like '%25" + $("#cabmsop").val() + "%25'";
// 				if ($("#descripcion").val() != "" )
// 					qw += " AND cDescripcion like '%25" + $("#descripcion").val() + "%25'";
				
				oTable = $("#tblTipoGarantia").dataTable({
				sScrollX: "100%",
				sScrollXInner: "100%", 
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth : false,
				//"iDisplayLength": 17, //Cuantos registros se despliegan
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=[v_mCatalogoTipoGarantia]&qw="+qw,
				bProcessing: true,
				//Tablesorter:false,
				//bSortable: false
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nId" },
					{ sName: "cDescripcion" },
					{ sName: "cIdPorcentaje" },
					{ sName: "porcentaje" }
					
						
					]
        	});
	
		}
		function Limpiar(){
			$("#nId").val('');
			$("#cDescripcion").val('');
			$("#cIdPorcentaje").val('');
			$("#porcentaje").val('');
		}
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true 
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var s=$("#salario").val();
			var strCheck = '0123456789.';
			
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true;
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function Cambiar(){
			if($("#cIdPorcentaje").val()!='' && $("#porcentaje").val()!=''){
				queryFormPost("mCatalogoTipoGarantiaUpdate",  {async : false});
				alert("Datos Guardados");
				mostrar();
				Limpiar();
			}else{
				alert("No pueden ir campos vacios");
			}
		}
		</script>
  </head>
  
  <body>
    <fieldset>
  		<legend>Catalogo Tipo Garantia</legend>
  		<form action="">
<!--   			<input title="Buscar" type="button" name="btnBuscar" id="btnBuscar" onclick="Buscar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/buscar_d.png')" > -->
<!-- 			<input title="Agregar" type="button" name="btnAgregar" id="btnAgregar" onclick="Agregar()" style="width: 85px;height:28px ;background-image: url('../../Ayudas/imagenes/agregar_d.png')" > -->
			<input title="Actualizar" type="button" name="btnCambiar" id="btnCambiar" onclick="Cambiar()" style="width: 86px;height:28px ;background-image: url('../../Ayudas/imagenes/Cambiar_d.png')">
			<input title="Limpiar" type="button" name="btnLimpiar" id="btnLimpiar" onclick="Limpiar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/Limpiar_d.png')">
			
			<table width="100%" border="0">
	   			<tr width="50%">
	   				<td width="10%">* #:</td>
	   				<td width="20%">* Descripción:</td>
	   				<td width="10%">* IdPorcentaje:</td>
	   				<td width="10%">* Porcentaje:</td>  				
	   			</tr>
	   			<tr width="50%">
	   				<td width="10%"><input type="text" name="nId" id="nId" maxlength="5" readonly="readonly"></td>
	   				<td width="20%"><input type="text" name="cDescripcion" id="cDescripcion" readonly="readonly" style="width: 90%;"></td>
	   				<td width="10%"><input type="text" name="cIdPorcentaje" id="cIdPorcentaje" ></td>
	   				<td width="10%"><input type="text" name="porcentaje" id="porcentaje" onkeypress="return(onlyNumbers2(event));"></td>	
	   			</tr>
	   		</table>
  		</form>
  		
	</fieldset>
	<table id="tblTipoGarantia" class="display">
        <thead>
            <tr>
				<th >#</th>
            	<th  >Decripción</th>
            	<th  >IdPorcentaje</th>              	
				<th  >Porcentaje</th>
            </tr>
        </thead>
	</table>	
  </body>
</html>
