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
       <title>CABMSOP</title>	
    
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
			//Obtengo el rol del usuario
			queryFormPost("checaRolUsuario",{async:false});
			queryFormPost("esAdminRecMat",{async:false});
			if( parseInt( $('#esAdminRecMat').val(), 10 ) == 0 ){
				document.A.btnAgregar.style.display ='none';
				document.A.btnBorrar.style.display ='none';
				document.A.btnCambiar.style.display ='none';
			}
			mostrar();
			$("#tblcabmsop tbody").click(function(event) {
				
				$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					
				$(event.target.parentNode).addClass('gradeA');
		 		
					
				var aTrs = $('#tblcabmsop').dataTable().fnGetNodes();
				//alert("Yaaaa");
				for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						//alert("0");
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{   
							var nTr = $('#tblcabmsop').dataTable().fnGetData(aTrs[i]);
							$("#nidCABMSOP").val(nTr[0]);
							$("#descripcion").val(nTr[1]);
							
							window.opener.document.B.cabmsop.value=nTr[0];
							window.opener.document.B.descipcioncabmsop.value=nTr[1];
							window.close();
							
						}     
					}
			});
		});
		function mostrar() {
				var qw = " 1 = 1";
				if ($("#cabmsop").val() != "" )
					qw += " AND nidCABMSOP like '%25" + $("#cabmsop").val() + "%25'";
				if ($("#descripcion").val() != "" )
					qw += " AND cDescripcion like '%25" + $("#descripcion").val() + "%25'";
				
				oTable = $("#tblcabmsop").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=[mCatalogoCABMSOP]&qw="+qw,
				bProcessing: true,
				//Tablesorter:false,
				//bSortable: false
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "nidCABMSOP" },
					{ sName: "cDescripcion" }
					
					
						
					]
        	});
	
		}
		function Limpiar(){
			$("#cabmsop").val('');
			$("#descripcion").val('');
			
			mostrar();
		}
		function Buscar(){
			mostrar();
		}
</script>

  </head>
  
  <body>
  <fieldset>
  
	<legend>CABMSOP</legend>
	  <form action="" name="A" id="A">
		    	<input title="Buscar" type="button" name="btnBuscar" id="btnBuscar" onclick="Buscar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/buscar_d.png')" >
				<input title="Limpiar" type="button" name="btnLimpiar" id="btnLimpiar" onclick="Limpiar()" style="width: 80px;height:28px ;background-image: url('../../Ayudas/imagenes/Limpiar_d.png')">
				
				<input type="hidden" name="esAdminRecMat" id="esAdminRecMat" value="">
				<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
				<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
				<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
		  </form>
  	<form action="" name="B" id="B">
  		<table width="100%" border="0">
	   			<tr width="50%">
	   				<td >CABMSOP:
	   				
	   				</td>
	   					   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td ><input type="text" name="cabmsop" id="cabmsop" maxlength="50" style="width: 20%;"></td>
	   					   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td >DESCRIPCION:</td>
	   					   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td ><input type="text" name="descripcion" id="descripcion" maxlength="50" style="width: 80%;"></td>
	   					   				   				
	   			</tr>
	   			
	   				
	   	</table>
  	</form>
  	</fieldset>
    <table id="tblcabmsop" class="display">
           <thead>
               <tr>
   				<th >ccaop</th>
               	<th  >cidccaop</th>
               	                	
                  
                
                 
                  
               </tr>
           </thead>
	</table>	
  </body>
</html>
