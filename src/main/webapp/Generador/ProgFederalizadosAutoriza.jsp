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
		<title>Programas Federalizados Autorizacíon Layout</title>

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
		var vMotivoRechazo = "";
		var nFolioPago = 0;
		
		$(document).ready(function() {

			$( "#dialog-Rechazo" ).dialog({
				autoOpen: false,
				height: 300,
				width: 350,
				modal: true,
				buttons: {
					"Aceptar": function() {
					var table = document.getElementById('dt_generados');
					vMotivoRechazo = $("#motivo").val();
					
	 				if (vMotivoRechazo != ""){
	 					var cCOLUMNALLAVE = 2;
	 					var szTabla = "TFEDERALIZADOSLAYOUTAUT";
						
						var szCampos = " dMotivoRechazo = '" + vMotivoRechazo + "'";
						var szWhere = " nFolioPAGOFEDERALIZADO = " + nFolioPago;
						$.getJSON("../catalogos/UpdateJson.jsp", {Tabla: szTabla, Param: szWhere, SetParam: szCampos},function(j){});
						vMotivoRechazo = "";
						$("#motivo").val( "" );
						//alert(nCuantos + " Registro Rechazado.");	
	 				}
	
					location.reload();

					$( this ).dialog( "close" );
					},
					"Cancelar": function() {
						
						$( this ).dialog( "close" );
					}
				},
				close: function() {										
				}
			});

			
			var oTableEnviados = $('#dt_paraAutorizar').dataTable({
					bRetrive: true,
					bPaginate: false,
					bDestroy: true,
					//bLengthChange: false,
        			bFilter: false,
        			bSort: true,
        			bInfo: false,
        			bAutoWidth:true,

					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Buscar:"
					},

					bServerSide: true,
					
					
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vAutorizadorPAGOFEDERALIZADO&qw=U_LOGIN = '--' and dMotivoRechazo is null",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
					    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "nFolioPAGOFEDERALIZADO",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "mImporteNeto",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "dCuentaBancaria",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "cConcepto",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "fProgramada",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "IdLeyenda",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "caNoContrarrecibo",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
					]
					
        		});
    				
    				$("#dt_paraEnvio tbody").click(function(event) {
					$(oTableLocal.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					//$("#pbEnvia").css("visibility","visible");
					//$("#pbEnviaDocumentacion").css("visibility","visible");
										
				});
			
			$('#pbAutoriza')
				.button()
				.click( function() {

				var bSeleccionados = false;
				var nCuantos = 0;
				
				var table = document.getElementById('dt_paraAutorizar');
 				var aTrs = $('#dt_paraAutorizar').dataTable().fnGetNodes();

 				var cCOLUMNALLAVE = 2;
 				var szTabla = "TFEDERALIZADOSLAYOUTAUT";
					
 				for ( var i=1; i<=aTrs.length;  i++ )    
				{         
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					
					if(null != chkbox && true == chkbox.checked)
						{
 							var caNoFolio = row.cells[cCOLUMNALLAVE].childNodes[0].toString(); 
														
							var szCampos = " fAutorizacion = GETDATE(), U_LOGIN = '<%=usuario.getLogin()%>'";
							var szWhere = " nFolioPAGOFEDERALIZADO = " + caNoFolio
							$.getJSON("../catalogos/UpdateJson.jsp", {Tabla: szTabla, Param: szWhere, SetParam: szCampos},function(j){});
							bSeleccionados = true;
							nCuantos ++;
					    } 			
				}
 				if (!bSeleccionados)
				{
					alert("Debe marcar como seleccionado al menos un renglón.");
					return;
				}
 				alert(nCuantos + " registros autorizados.");

				location.reload();
				} );
			
			$('#pbRechazar')
				.button()
				.click( function() {
				var bSeleccionados = false;
				var nCuantos = 0;
				var caNoFolio = 0;
				var table = document.getElementById('dt_paraAutorizar');
 				var aTrs = $('#dt_paraAutorizar').dataTable().fnGetNodes();
				var cCOLUMNALLAVE = 2;
 				
 				for ( var i=1; i<=aTrs.length;  i++ )    
				{         
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					
					if(null != chkbox && true == chkbox.checked){
						nFolioPago = row.cells[cCOLUMNALLAVE].childNodes[0].toString(); 
					
						bSeleccionados = true;
						nCuantos ++;
				    } 			
				}
 				if (!bSeleccionados || nCuantos > 1)
				{
					alert("Debe marcar como seleccionado solo un renglón para Rechazo.");
					return;
				}
 				$( "#dialog-Rechazo" ).dialog( "open" );
				
			} );
			
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

		
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<center>
		<div id="container" align="left" style="width: 1000px">	
			<h1>Autorización para Generar Layout Programas Federalizados </h1>
				<input type="button" id="pbAutoriza" value="Autorizar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbRechazar" value="Rechazar"/>&nbsp;&nbsp;&nbsp;
			<br/><br/>
			<table id="dt_paraAutorizar" class="display"  >
	            <thead>
	            	<tr align="center">
						<th>Seleccionar</th>
           				<th>Unidad Ejecutora</th>
           				<th width="130px" align="center">Folio</th>
           				<th>RFC</th>
           	    		<th>Monto</th>
           	           	<th>Cuenta Bancaria</th>
           				<th>Concepto</th>
           	    		<th>Fecha Programada</th>
           	    		<th>Leyenda</th>
           	    		<th>Cuenta por Pagar</th>           	    		
          	    	</tr>					
	            </thead>
	        </table>
			<br/>
		</div>
		</center>
		<div id="dialog-Rechazo" title="Motivo de Rechazo">
			Motivo de Rechazo: 
           	<textarea name="motivo" id="motivo" rows="15" style="width: 200%;" ></textarea> 
		</div>		
	</form>
	</body>
</html>