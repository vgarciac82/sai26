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
		<title>Cat&aacute;logo de CCAOP</title>

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
<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
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
			$(".tabs").tabs();
			$( "#aTab1" ).attr("disabled", true);
			mostrar();
			$( "#aTab0" ).click (function(){
					$( "#aTab1" ).attr("disabled", true);
					$( "#aTab2" ).attr("disabled", false);
					 borrarCampos();
			});
			$("#btnBuscar").button().click(function(){				
				var aTrs = $('#tblCCAOP').dataTable().fnGetNodes();								
	        		 for ( var i=aTrs.length-1 ; i>=0; i-- )     
					{
						$(aTrs[i]).addClass('row_selected');									    
					}            					 
			});	
			$("#btnLimpiar").button().click(function(){});
			
			$("#btnEliminar").button().click(function(){
				if($("#cIdCCAOP_edt").val()!=""){
					var resultado = confirm("Estimado Usuario:\n Confirma Eliminar el CCAOP?");
					if(resultado){
						queryFormPost("mCatalogoCcaopDelete",  {async : false});
						$( "#aTab0" ).click();
						borrarCampos();
					}
				}else{
					alert("Estimado Usiario:\nVerifique el valor de idCCAOP es obligatorio,Gracias.");
				}
				
			});
			
					
			$("#tblCCAOP tbody").click(function(event) {
				
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					var aTrs = $('#tblCCAOP').dataTable().fnGetNodes();           										
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         						
						if ( $(aTrs[i]).hasClass('gradeA') )         
						{             
							var nTr = $('#tblCCAOP').dataTable().fnGetData(aTrs[i]);
							$( "#cIdCCAOP" ).val(nTr[0]);
							queryFormPost("CCAOPRead", {async : false});
							$( "#aTab1" ).click();
							$( "#aTab2" ).attr("disabled", true);
							$( "#aTab1" ).attr("disabled", false);
						}     
					}										
			});
		});
		
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
				if ($("#cIdCCAOP").val() != "")
					qw += " AND cIdCCAOP LIKE '%25" + $("#cIdCCAOP").val() + "%25'";
				if ($("#cCCAOP").val() != "")
					qw += " AND cCCAOP LIKE '%25" + $("#cCCAOP").val() + "%25'";
				if ($("#cDescripcion").val() != "")
					qw += " AND cDescripcion LIKE '%25" + $("#cDescripcion").val() + "%25'";				
				oTable = $("#tblCCAOP").dataTable({
				sScrollX: "100%",
				sScrollXInner: "100%", 
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth : false,
				"iDisplayLength": 5, //Cuantos registros se despliegan
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCcaopGrid&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCCAOP" },
					{ sName: "cCCAOP"   },
					{ sName: "cDescripcion" }
				]
        	});
				
		}
		function borrarCampos(){
				$("#cIdCCAOP").val('');
				$("#cCCAOP").val('');
				$("#cDescripcion").val('');
				$("#cIdCCAOP_edt").val('');
				$("#cCCAOP_edt").val('');
				$("#cDescripcion_edt").val('');
				$("#cIdCCAOP_alt").val('');
				$("#cCCAOP_alt").val('');
				$("#cDescripcion_alt").val('');
				mostrar();
		}
		
		
		</script>
		
		<script type="text/javascript" charset="utf-8">		// funciones del dialogo de Catalogo CCAOP
		
		$(function() {
			$( "#dialog:ui-dialog" ).dialog( "destroy" );
			var cIdCCAOP_alt = $( "#cIdCCAOP_alt" ),
				cCCAOP_alt = $( "#cCCAOP_alt" ),
				cDescripcion_alt = $( "#cDescripcion_alt" ),
				cIdCCAOP_edt = $( "#cIdCCAOP_edt" ),
				cCCAOP_edt = $( "#cCCAOP_edt" ),
				cDescripcion_edt = $( "#cDescripcion_edt" ),
				allFields = $( [] ).add( cIdCCAOP_alt ).add(cCCAOP_alt).add(cDescripcion_alt).add(cCCAOP_edt).add(cDescripcion_edt),
				tips = $( ".validateTips" );			

			function updateTipsDlg( t ) {
				tips
					.text( t );
					alert(t);
				//	.addClass( "ui-state-highlight" );
				// setTimeout(function() { tips.removeClass( "ui-state-highlight", 1500 );}, 500 );
			}

			function checkLength( o, n, min, max ) {
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass( "ui-state-error" );
					if (min == max)
						updateTipsDlg( "La longitud de " + n + " debe ser de " + min + " caracteres." );
					else
						updateTipsDlg( "La longitud de " + n + " debe estar entre " + min + " y " + max + "." );
					o.focus();
					return false;
				} else {
					return true;
				}
			}
			function checkRequerido( o, n) {
				var sTemp = $.trim(o.val());
				o.val(sTemp);
				if ( sTemp.length == 0  ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg(  n + " es un dato requerido." );
					o.focus();
					return false;
				} else {
					return true;
				}
			}

			function checkRegexp( o, regexp, n ) {
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg( n );
					return false;
				} else {
					return true;
				}
			}

			$( "#dialog-form" ).dialog({
			});

			$("#btnAlta").button().click(function(){
			var bValid = true;
				tips.text("");
				allFields.removeClass( "ui-state-error" );

				bValid = bValid && checkRequerido( cIdCCAOP_alt, "cIdCCAOP_alt" );
				bValid = bValid && checkLength( cIdCCAOP_alt, "cIdCCAOP_alt", 2, 16 );
				bValid = bValid && checkRegexp( cIdCCAOP_alt, /^([0-9])+$/, "cIdCCAOP solo permite números : 0-9" );
				
				bValid = bValid && checkRequerido( cCCAOP_alt, "cCCAOP_alt" );
				bValid = bValid && checkLength( cCCAOP_alt, "cCCAOP_alt", 2,200 );
						
				bValid = bValid && checkRequerido( cDescripcion_alt, "cDescripcion_alt" );
				bValid = bValid && checkLength( cDescripcion_alt, "cDescripcion_alt", 2, 4000 );
						
				if(bValid){
					queryFormPost("CCAOPRead", {async : false});
					var str_idCCAOP =$("#cIdCCAOP").val();
					if($("#cIdCCAOP_alt").val()!= str_idCCAOP){
					queryFormPost("mCatalogoCcaopCreate",  {async : false});
					$( "#aTab0" ).click();
					borrarCampos();
					}else{
						alert("Estimado Usuario:\nYa Existe un idCCAOP dado de alta verifique su valor,Gracias.");
					}
				}
			});
			
			$("#btnCambiar").button().click(function(){
				var bValid = true;
				tips.text("");
				allFields.removeClass( "ui-state-error" );
				
				bValid = bValid && checkRequerido( cCCAOP_edt, "cCCAOP_edt" );
				bValid = bValid && checkLength( cCCAOP_edt, "cCCAOP_edt", 2,200 );
						
				bValid = bValid && checkRequerido( cDescripcion_edt, "cDescripcion_edt" );
				bValid = bValid && checkLength( cDescripcion_edt, "cDescripcion_edt", 2, 4000 );

				if(bValid){
					queryFormPost("mCatalogoCcaopUpdate",  {async : false});
					$( "#aTab0" ).click();	
					mostrar();
				}
			});
});


		
		
	</script>


	</head>

	<body bgcolor="red" id="dt_example" bottommargin="0" leftmargin="0" topmargin="0">
		<form>
			<div id="container" class="container">
				<h1 align="left">
					Cat&aacute;logo CCAOP
				</h1>
				<div class="tabs" id="tabs" name="tabs" >
					<ul>
					<li><a id="aTab0" href="#tabs-0">Consulta</a></li>
					<li><a id="aTab1" href="#tabs-1">Editar</a></li>
					<li><a id="aTab2" href="#tabs-2">Alta</a></li>
					
				</ul>
				<div align="center" id="tabs-0">
					<div>
						<fieldset>
							<table width="800" border="0" align="center">
								<tr>
									<td align="left" colspan="4">cIdCCAOP</td>
									<td align="left" colspan="4"><input type="text" name="cIdCCAOP" id="cIdCCAOP" style="width: 40em;" /></td>
									
								</tr>
								<tr>
									<td align="left" colspan="4">Clave cCCAOP</td>
									<td align="left" colspan="4"><input type="text" name="cCCAOP" id="cCCAOP" style="width: 40em;" /></td>
								</tr>
								<tr>
									<td align="left" colspan="4">Descripci&oacute;n gen&eacute;rica</td>
									<td align="left" colspan="4"><textarea rows="3" cols="70" id="cDescripcion" name="cDescripcion"></textarea></td>
								</tr>
								<tr align="center">
									<td align="center" colspan="8"><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" onclick="mostrar();" />
									<input type="button" name="btnLimpiar" id="btnLimpiar" value="Limpiar" onclick="borrarCampos();" /></td>
								</tr>
							</table>
							<table id="tblCCAOP" class="display">
	            <thead>
	                <tr>
	                	<th width="10%">cIdCCAOP</th>
	                	<th width="20%">cCCAOP</th>	                	
	                    <th width="60%">cDescripcion</th>
	                </tr>
	            </thead>
	        </table>	       
			<br/>
						</fieldset>
					</div>
					<br />
				</div>


				<div align="center" id="tabs-1">
					<table width="800" border="0" align="center">
								<tr>
									<td align="left" colspan="4">cIdCCAOP</td>
									<td align="left" colspan="4"><input type="text" name="cIdCCAOP_edt" id="cIdCCAOP_edt" style="width: 40em;" readonly="readonly"/></td>
									
								</tr>
								<tr>
									<td align="left" colspan="4">Clave cCCAOP<font color="red">*</font></td>
									<td align="left" colspan="4"><input type="text" name="cCCAOP_edt" id="cCCAOP_edt" style="width: 40em;" /></td>
								</tr>
								<tr>
									<td align="left" colspan="4">Descripci&oacute;n gen&eacute;rica<font color="red">*</font></td>
									<td align="left" colspan="4"><textarea rows="8" cols="70" id="cDescripcion_edt" name="cDescripcion_edt"></textarea></td>
								</tr>
								<tr>
									<td align="left" colspan="8"><label class="validateTips ui-state-error" ></label></br></td>
								</tr>
								
								<tr align="center">
									<td align="center" colspan="8">
									<input type="button" name="btnCambiar" id="btnCambiar" value="Cambiar" />
									<input type="button" name="btnEliminar" id="btnEliminar" value="Eliminar" />
									</td>
								</tr>
							</table>
				</div>
				<div align="center" id="tabs-2">
					<table width="800" border="0" align="center">
								<tr>
									<td align="left" colspan="4">cIdCCAOP<font color="red">*</font></td>
									<td align="left" colspan="4"><input type="text" name="cIdCCAOP_alt" id="cIdCCAOP_alt" style="width: 40em;" /></td>
									
								</tr>
								<tr>
									<td align="left" colspan="4">Clave cCCAOP<font color="red">*</font></td>
									<td align="left" colspan="4"><input type="text" name="cCCAOP_alt" id="cCCAOP_alt" style="width: 40em;" /></td>
								</tr>
								<tr>
									<td align="left" colspan="4">Descripci&oacute;n gen&eacute;rica<font color="red">*</font></td>
									<td align="left" colspan="4"><textarea rows="8" cols="70" id="cDescripcion_alt" name="cDescripcion_alt"></textarea></td>
								</tr>
								<tr>
									<td align="left" colspan="8"><label class="validateTips ui-state-error" ></label></br></td>
								</tr>
								<tr align="center">
									<td align="center" colspan="8"><input type="button" name="btnAlta" id="btnAlta" value="Alta" onclick="" /></td>
								</tr>
							</table>
				</div>		
				</div>
			</div>

			<br />
		</form>
	</body>
</html>