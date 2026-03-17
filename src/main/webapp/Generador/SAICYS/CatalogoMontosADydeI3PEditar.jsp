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
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	Map<String, Role> rol =usuario.getRoles();
	
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cat&aacute;logo de Montos M&aacute;ximos </title>

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
			
		$(document).ready(function() {
			
			mostrar();
			$("#pbGuardaEdita").button().click(function(){
				//var t=0;
				var aTrs = $('#tblMontosADydeI3P').dataTable().fnGetNodes();
				//alert("T ="+t);
				var a;
				var i=0;
				var j=0;
				var alerta=0;
				var anterior=0;
				var siguiente=0;
				for(i=0;i<aTrs.length;i++){
					j=i;
					
					$("#editaId").val(parseInt((j+1),10));
					a=$("#MayorDe_"+i).val().replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace("$","");
					$("#editaMayorDe").val(parseFloat(a));
					
					a=$("#Hasta_"+i).val().replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace("$","");
					$("#editaHasta").val(parseFloat(a));
					// Dato Siguiente
					//alert($("#MayorDe_"+parseInt(i+1)).val());
					if(i<11){
						siguiente=$("#MayorDe_"+parseInt((i+1),10)).val().replace(",","");
						siguiente=siguiente.replace(",","");
						siguiente=siguiente.replace(",","");
						siguiente=siguiente.replace(",","");
						siguiente=siguiente.replace(",","");
						siguiente=siguiente.replace("$","");
					}
					else{
						siguiente=parseInt(a,10)+1;
						
					}
					
					a=$("#MontoMaxAD_"+i).val().replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace("$","");
					$("#editaMontoMaxAD").val(parseFloat(a));
					a=$("#MontoMaxI3P_"+i).val().replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace(",","");
					a=a.replace("$","");
					$("#editaMontoMaxI3P").val(parseFloat(a));
				
				
					if(parseInt($("#editaMayorDe").val(),10)< parseInt($("#editaHasta").val(),10 )){
						//Se compara con el siguiente
						
							if((parseInt($("#editaHasta").val(),10) <= parseInt(siguiente,10)) ){
								if(parseInt($("#editaMayorDe").val(),10)>= anterior)
									queryFormPost("mCatalogoMontosADupdate", {async: false });
								else{
									alerta=1;
									alert("El monto inicio  debe ser mayor o igual  al monto fin del registro anterior ");
								}
							}
						    	
							else{
								alerta=1;
								alert("El monto fin  debe ser menor o igual  al monto inicio del registro siguiente ");
							}
						
						//Se compara con el anterior
						/*else{
							if((parseInt($("#editaHasta").val()) >= parseInt(anterior)) )
						    	queryFormPost("mCatalogoMontosADupdate", {async: false });
							else{
								alerta=1;
								alert("El monto inicio  debe ser mayor o igual  al monto fin del registro anterior ");
							}
						}
						*/	
					}
					else{
						alert("El monto de inicio "+ $("#editaMayorDe").val()+" no puede ser mayor o igual que el Fin de monto "+$("#editaHasta").val());
						alerta=1;
					}
					anterior=$("#editaHasta").val();
				}
				if(alerta!=1)
					alert("Datos Guardados ");
				else
					alert("No todos los datos se Guardaron ");
				window.location = "CatalogoMontosADydeI3P.jsp?tab=" + 1;
			});
			
		});
function mostrar() {
							
				oTable = $("#tblMontosADydeI3P").dataTable({
				sScrollX: "100%",
				sScrollXInner: "100%", 
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth : false,
				"iDisplayLength": 25, //Cuantos registros se despliegan
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mMontosMaximosAdjudicaciones",
				bProcessing: true,
				bLengthChange: false,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "id" },
					{ sName: "MayorDe" ,"bSortable": false},
					{ sName: "Hasta"	,"bSortable": false},
					{ sName: "MontoMaxAD"	,"bSortable": false},
					{ sName: "MontoMaxI3P"	,"bSortable": false}
					//{ sName: "idd"	}
				
				]
        	});
		
		}
	function onlyNumbers2(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '0123456789';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
	
		return true 
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
	function onlyMoney(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 46 || keyPressed == 36)
			return true;
		return (keyPressed >= 48 && keyPressed <= 57);
	}
</script>
		

</head>

<body id="dt_example">
	<form> 
		<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
		<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
		<div id="container" class="container" style="width: 100%; height: 100%" >	
		
		<input type="hidden" name="editaId" id="editaId" value="">
		<input type="hidden" name="editaMayorDe" id="editaMayorDe" value="">
		<input type="hidden" name="editaHasta" id="editaHasta" value="">
		<input type="hidden" name="editaMontoMaxAD" id="editaMontoMaxAD" value="">
		<input type="hidden" name="editaMontoMaxI3P" id="editaMontoMaxI3P" value="">
			<!-- <table>
			   <tr>
            		<td align="left"> 
								
						<input type="button" value ="Guardar" id="pbGuardaEdita" class="btnInterfaceBG"/> 
								 
					</td> 
            	</tr>
			</table>
				 -->																	
			<table id="tblMontosADydeI3P" class="display">
				
	            <thead>
	            	<tr>
            		<td align="left"> 
								
						<input type="button" value ="Guardar" id="pbGuardaEdita" class="btnInterfaceBG"/> 
								 
					</td> 
            	</tr>
	            
	                <tr>
	    
	                	<th width="5%">#</th>                	
	                    <th width="32%">InicioMonto</th>
	                    <th width="33%">FinMonto</th>
	                    <th width="20%">MontoMaximo_AD</th>
	                   <th width="20%">MontoMaximo_I3P</th>
	                   
	                    
	                </tr>
	            </thead>
	          
	           
	           
	        </table>	       
			<br/>
		</div>
	</form>
	</body>
</html>