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
       <title>CCAOP</title>
    
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
			$("#tblCCAOP tbody").click(function(event) {
				
				$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					
				$(event.target.parentNode).addClass('gradeA');
		 		
					
				var aTrs = $('#tblCCAOP').dataTable().fnGetNodes();
				//alert("Yaaaa");
				for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						//alert("0");
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{   
							var nTr = $('#tblCCAOP').dataTable().fnGetData(aTrs[i]);
							$("#ccaop").val(nTr[0]);
							$("#idccaop").val(nTr[1]);
							
							
							
							
							//window.location = "../SAICYS/CatalogoCUCOP?partida="+nTr[1];
							//opener.document.getElementById("partida").valueOf()=nTr[1];
							window.opener.document.B.ccaop.value=nTr[0];
							window.opener.document.A.cIdCCAOP.value=nTr[1];
							
							window.close();
							
						}     
					}
			});
		});
		function mostrar() {
				var qw = " 1 = 1";
				if ($("#ccaop").val() != "" )
					qw += " AND ccaop like '%25" + $("#ccaop").val() + "%25'";
				if ($("#idccaop").val() != "" )
					qw += " AND cidccaop like '%25" + $("#idccaop").val() + "%25'";
				
				
				oTable = $("#tblCCAOP").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=[v_mCatalogoCCAOP]&qw="+qw,
				bProcessing: true,
				//Tablesorter:false,
				//bSortable: false
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "ccaop" },
					{ sName: "cidccaop" }
					
					
						
					]
        	});
	
		}
		function Limpiar(){
			$("#ccaop").val('');
			$("#idccaop").val('');
			
			mostrar();
		}
		function Buscar(){
			mostrar();
		}
</script>

  </head>
  
  <body>
  <fieldset>
  
	<legend>CCAOP</legend>
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
	   				<td >CCAOP:
	   				
	   				</td>
	   					   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td ><input type="text" name="ccaop" id="ccaop" maxlength="50" style="width: 20%;"></td>
	   					   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td >CCAOP:</td>
	   					   				   				
	   			</tr>
	   			<tr width="50%">
	   				<td ><input type="text" name="idccaop" id="idccaop" maxlength="50" style="width: 100%;"></td>
	   					   				   				
	   			</tr>
	   			
	   				
	   	</table>
  	</form>
  	</fieldset>
    <table id="tblCCAOP" class="display">
           <thead>
               <tr>
   				<th width="35%" >ccaop</th>
               	<th width="15%" >cidccaop</th>
               	                	
                  
                
                 
                  
               </tr>
           </thead>
	</table>	
  </body>
</html>
