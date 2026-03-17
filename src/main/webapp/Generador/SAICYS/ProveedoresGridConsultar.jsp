<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="java.util.*"%>
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
	String roles = "";
	Map rol = usuario.getRoles();
	Iterator it1 = rol.entrySet().iterator();
	while (it1.hasNext()) {
		Map.Entry r = (Map.Entry) it1.next();
		roles += r.getKey().toString() + ",";
	}
	if (roles.length() > 0) {
		roles = roles.substring(0, roles.length() - 1);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Catálogo de Proveedores</title>

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
		var roles="<%=roles%>";	
		$(document).ready(function() {			
			querySelectPost("mCatalogoEntidadFederativaConsultaRead", "cIdEntidadFederativa", {async : false});
			
										
			mostrar();
			$( "#aTab1" ).attr("disabled", true);
			$("#tblProveedores tbody").click(function(event) {
				
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					
					$(event.target.parentNode).addClass('gradeA');
					
					var aTrs = $('#tblProveedores').dataTable().fnGetNodes();           										
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         						
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{             
							var nTr = $('#tblProveedores').dataTable().fnGetData(aTrs[i]);  							
							window.location = "Proveedores.jsp?tab=1&rfc="+nTr[0].replace('&', '_=_');
						}     
					}										
					
				});
						
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			$("#btnBuscar").button().click(function(){				
				var aTrs = $('#tblProveedores').dataTable().fnGetNodes();								
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
		function mostrar() {
				var qw = " 1 = 1";	
				
				if ($("#cIdRFC").val() != "")
					qw += " AND cIdRFC LIKE '%25" + $("#cIdRFC").val() + "%25'";
				if ($("#cRazonSocial").val() != "")
					qw += " AND cRazonSocial LIKE '%25" + $("#cRazonSocial").val() + "%25'";
				if ($("#cGiro").val() != "")
					qw += " AND cGiro LIKE '%25" + $("#cGiro").val() + "%25'";
				if ($("#Situacion").val() != "0"){
				if($("#Situacion").val() == "2")
				qw += " AND lHabilitado = " + "0";	
				else	
				qw += " AND lHabilitado = " + $("#Situacion").val();	
				}
						
				if ($("#cIdEntidadFederativa").val() != "00")
					qw += " AND cIdEntidadFederativa = " + $("#cIdEntidadFederativa").val();
				if ($("#cNumeroInterno").val() != "")
					qw += " AND registro LIKE '%25" + $("#cNumeroInterno").val() + "%25'";
				if ($("#nIdPyme").val() != "0")
					qw += " AND nIdPyme = " + $("#nIdPyme").val();
				
				var mostrarElim=false;
				$("#elim").css("display", "none");
				if(roles.indexOf("ADMIN_RECMAT") >= 0){
					$("#elim").css("display", "block");
					mostrarElim=true;
				}
					oTable = $("#tblProveedores").dataTable({
						sScrollX: "100%",
						sScrollXInner: "100%", 
						bScrollCollapse: true,
						bDestroy: true,
						bAutoWidth : false,
						"iDisplayLength": 10, //Cuantos registros se despliegan
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
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vProveedoresGrid&qw=" + qw,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						aaSorting: [[ 0, "asc" ]] ,
						aoColumns: [
							{ sName: "cIdRFC" },
							{ sName: "cRazonSocial"   },
							{ sName: "cGiro" },
							{ sName: "Situacion"	},
							{ sName: "EntidadFederativa"	},
							{ sName: "registro"	},
							{ sName: "cPyme"	},
							{ sName: "eliminar", bVisible: mostrarElim }
						]
		        	});
			
				
				
		}
		function deleteProveedor(rfc){
			if (!window.confirm("Seguro que desea Eliminar el RFC: "+rfc)){
				return;
			}else{
				$("#rfc").val(rfc);
				queryFormPost("mDeleteRFC",{async:false});
				alert("RFC Eliminado ");
				mostrar();
			}
		}
</script>
		

</head>

<body id="dt_example">
	<form> 
		<div id="container" class="container" style="width: 100%; height: 100%" >	
			<h1 align="left">Proveedores</h1>
			<table align="left">
				<tr id="trRfc" align="left">
					<td>RFC:</td>
					<td><input type="text" name="cIdRFC" id="cIdRFC" style="width: 40em;" /></td>
				</tr>
				<tr id="trcRazonSocial" align="left">
					<td>Razon Social:</td>
					<td><input type="text" name="cRazonSocial" id="cRazonSocial" style="width: 40em;" /></td>
				</tr>
				<tr id="trGiro" align="left">
					<td>Giro:</td>
					<td><input type="text" name="cGiro" id="cGiro" style="width: 40em;" /></td>
				</tr>
				<tr id="trSituacion" align="left">
					<td>Situación:</td>
					<td><select id="Situacion" name="Situacion" style="width: 40em;">
						<option id="0" value="0">Todos</option>
						<option id="1" value="1">Habilitado</option>
						<option id="2" value="2">Inhabilitado</option>
						</select></td>
				</tr>
				<tr id="trEntidadFederativa" align="left">
					<td>Entidad Federativa:</td>
					<td><select id="cIdEntidadFederativa" name="cIdEntidadFederativa" style="width: 40em;"></select></td>
				</tr>
				<tr id="trNumInt" align="left">
					<td>Número Interno:</td>
					<td><input type="text" name="cNumeroInterno" id="cNumeroInterno" style="width: 40em;" /></td>
				</tr>
				<tr id="trPyme" align="left">
					<td>Pyme:</td>
					<td><select id="nIdPyme" name=nIdPyme style="width: 40em;">
						<option id="0" value="0">No aplica</option>
						<option id="1" value="1">Micro Empresa</option>
						<option id="2" value="2">Pequeña Empresa</option>
						<option id="3" value="3">Mediana Empresa</option>
						</select></td>
				</tr>						
				<tr id="trPyme" align="center">
					<td colspan="2"><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" onclick="mostrar();" /></td>					
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>	
			</table>																			
			<table id="tblProveedores" class="display">
	            <thead>
	                <tr>
	                	<th width="20%">R.F.C.</th>
	                	<th width="20%">Raz&oacute;n Social</th>	                	
	                    <th width="20%">Giro</th>
	                    <th width="20%">Situaci&oacute;n</th>
	                    <th width="20%">Entidad Federativa</th>
	                    <th width="20%">Registro Interno</th>
	                    <th width="20%">Pyme</th>
	                    <th width="20%" id="elim" ></th>
	                </tr>
	            </thead>
	        </table>	       
			<br/>
		</div>
		<input type="hidden" name="rfc" id="rfc" />
	</form>
	</body>
</html>