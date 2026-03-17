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
	Map<String, Role> rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cIdcapitulo=request.getParameter("cidCapitulo")==null || "".equals(request.getParameter("cidCapitulo"))? "0":request.getParameter("cidCapitulo");
	String nameForm=request.getParameter("nameForm")==null || "".equals(request.getParameter("nameForm"))? "":request.getParameter("nameForm");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       <title>Cat&aacute;logo de Partidas Presupuestales</title>
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
			@import "../../css/interfaz.css";
		</style>
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" charset="utf-8">
		var oTable;
		var formName ="<%=nameForm%>";
		var cIdCapitulo="<%=cIdcapitulo%>";
		$(document).ready(function(){
			mostrar();
			$("#btnBuscar").button();
			$("#btnLimpiar").button();
			$("#tblpartida tbody").dblclick(function(event) {
				var inputTrgt;
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('gradeA');
				});
				$(event.target.parentNode).addClass('gradeA');
				var aTrs = $('#tblpartida').dataTable().fnGetNodes();
				for ( var i=aTrs.length ; i>=0; i-- ){  
					if ( $(aTrs[i]).hasClass('gradeA')){   
						var nTr = $('#tblpartida').dataTable().fnGetData(aTrs[i]);
						$("#partida").val(nTr[0]);
						$("#descripcion").val(nTr[1]);
						$("#descripcionC").val(nTr[2]);
						eval( 'window.opener.document.' + formName +'.cIdSubPartida.value=window.B.partida.value');
						eval( 'window.opener.document.' + formName +'.partida.value=window.B.descripcion.value');
						eval( 'inputTrgt = window.opener.document.' + formName  + '.partida'  );
						if( inputTrgt.onchange ){
							inputTrgt.onchange();
						}
						window.close();
					}     
				}
			});
		});//fin del document
		function mostrar(){
			var qw = " cIdCapitulo ="+cIdCapitulo;
			if ($("#partida").val() != "" )
				qw += " AND cIdSubPartida like '%25" + $("#partida").val() + "%25'";
			if ($("#descripcion").val() != "" )
				qw += " AND cSubPartida like '%25" + $("#descripcion").val() + "%25'";
			if ($("#descripcionC").val() != "" )
				qw += " AND cSubPartidaCorto like '%25" + $("#descripcionC").val() + "%25'";
			oTable = $("#tblpartida").dataTable({
				bAutoWidth : false,
			   	bRedraw:false,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				SSCROLLX: "100%",
				iDisplayLength: 10,
			    sPaginationType: "full_numbers",
			    bLengthChange: false,
				//sScrollXInner: "200%",				
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCatalogoPartidas&qw="+qw,
				bProcessing: true,
			    sPaginationType: "full_numbers",
				bJQueryUI: true	,
				aaSorting: [[0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdSubPartida" },
					{ sName: "cSubPartida" },
					{ sName: "cSubPartidaCorto"	}
				]
        	});
		}
		function Limpiar(){
			$("#partida").val('');
			$("#descripcion").val('');
			$("#capitulo").val('');
			$("#descripcionC").val('');
			$("#RP").val('');
		}
		function Buscar(){
			mostrar();
		}
	</script>
  	</head>
  	<body>
  		<fieldset>
			<legend>Partidas Presupuestales</legend>
			<form action="" name="B" id="B">
				<table width="100%" border="0">
	   				<tr style="width: 50%">
	   					<td >*Partida:</td>
	   				</tr>
		   			<tr style="width: 50%">
		   				<td ><input type="text" name="partida" id="partida" maxlength="5" style="width: 20%;"></td>
		   			</tr>
		   			<tr style="width: 50%">
		   				<td >*Descripción:</td>
		   			</tr>
		   			<tr style="width: 50%">
		   				<td ><input type="text" name="descripcion" id="descripcion" maxlength="2000" style="width: 100%;"></td>
		   			</tr>
		   			<tr style="width: 50%">
		   				<td >*Descripcion Corta:</td>
		   			</tr>
		   			<tr style="width: 50%">
		   				<td ><input type="text" name="descripcionC" id="descripcionC" maxlength="40" style="width: 40%;"></td>
		   			</tr>
				</table>
  			</form>
  			<form action="" name="A" id="A">
		    	<input title="Buscar" type="button" name="btnBuscar" id="btnBuscar" onclick="Buscar()" value="Buscar" class="btnInterfaceBG"/>
				<input title="Limpiar" type="button" name="btnLimpiar" id="btnLimpiar" onclick="Limpiar()" value="Limpiar" class="btnInterfaceBG"/>
				<input type="hidden" name="esAdminRecMat" id="esAdminRecMat" value="">
				<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
				<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
				<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
			</form>
  		</fieldset>
  		<table>
  			<tr>
  				<td>
  					<b>Seleccione en la tabla la partida que se requiere con doble clic.</b>
  				</td>
  			</tr>
  		</table>
    	<table id="tblpartida" class="display">
        	<thead>
            	<tr>
	   				<th width="5%" >Partida Presupuestal</th>
	               	<th width="20%" >Descripci&oacute;n</th>
	                <th width="15%">Descripci&oacute;n Corta</th>
				</tr>
			</thead>
		</table>	
	</body>
</html>
