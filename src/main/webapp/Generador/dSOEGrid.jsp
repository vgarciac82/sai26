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
		<title>Generación SEO Documentado</title>

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
		
		<style>			// estilos del dialogo
			div#dialog-form-Componente fieldset { padding:0; border:0; margin-top:25px; }
			div#dialog-form-SubComponente fieldset { padding:0; border:0; margin-top:25px; }
			div#dialog-form-Categoria fieldset { padding:0; border:0; margin-top:25px; }

			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
			.alignRight { text-align: right; }
			.alignCenter { text-align: center; }
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
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>		
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
		var sNoSoe="";
		var sNumPrestamo = "";
		var sIdPrestamo="";
		var sEstatusSoe="";
		var sFechaMod="";

		// Candado para consultar registros NO DOCUMENTADOS
		//var xWhere = " fechaNoObjecion IS NULL" ;		
		// sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vdoc_dSOE&qw="+xWhere,

		$(document).ready(function() {
		
			$('.currency').blur(function()	{
				$('.currency').formatCurrency();
				});
		
			$("#tblSoeDocumentado tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					var aTrs = $('#tblSoeDocumentado').dataTable().fnGetNodes();
					if (aTrs.length == 0)
						return;
						
					$(event.target.parentNode).addClass('row_selected');
					
					ObtenParamEnv();

					$("#pbModificar").css("visibility","visible"); 
					$("#pbDesplegar").css("visibility","visible"); 
					if (sEstatusSoe == "en Trámite") 
						$("#pbDesembolso").css("visibility","visible");
					else
						$("#pbDesembolso").css("visibility","hidden");
					$("#pbCancelarSOE").css("visibility","visible");					
				});

			oTable = $("#tblSoeDocumentado").dataTable({
				bAutoWidth : false,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSOETradicionalGrid",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 4, "desc" ]] ,
				aoColumns: [
					{ sName: "ID_Prestamo", bVisible: false },
					{ sName: "NumeroPrestamo" },
					{ sName: "Numero_SOE" },
					{ sName: "Estatus"	},
					{ sName: "FechaUltimoMovimiento", sClass: "alignCenter"	}
				]
        	});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$("#pbNuevo")
				.button()
				.click( function() {
 					location.href = 'dSOEForm.jsp?Op=A&EstatusParam=Generando Nuevo SOE' ;
				});

			$("#pbModificar")
				.button()
				.click( function() {

					ObtenParamEnv();
					location.href = 'dSOEForm.jsp?Op=M&SOEParam='  + sNoSoe + '&sPrestamoParam=' + sIdPrestamo + '&EstatusParam='+ sEstatusSoe + '&FechaParam='+ sFechaMod;
				});
				
			$("#pbDesplegar")
				.button()
				.click( function() {

					ObtenParamEnv();
					location.href = 'dSOEForm.jsp?Op=X&SOEParam='  + sNoSoe + '&sPrestamoParam=' + sIdPrestamo + '&EstatusParam='+ sEstatusSoe + '&FechaParam='+ sFechaMod;
				});				

			$("#pbCancelarSOE")
				.button()
				.click( function() {
					ObtenParamEnv();
					
					var r = confirm("¿Esta seguro de eliminar el Número SOE " + sNoSoe + " ?");
					if (r == true)
					{
	  					$("#numero_soe").val(sNoSoe);
						queryFormPost("dSOEDelete", {async: false});
						location.href = 'dSOEGrid.jsp';
  					}

				});

			
			function ObtenParamEnv()
			{
				var aTrs = oTable.fnGetNodes();    
				var rowsTbl = $("#tblSoeDocumentado").dataTable().fnGetData();      
				
				for ( var i=0 ; i<aTrs.length ; i++ )     
				{
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{             
						sIdPrestamo = rowsTbl[i][0];
						sNumPrestamo = rowsTbl[i][1];
						sNoSoe = rowsTbl[i][2];						
						sEstatusSoe = rowsTbl[i][3]; 
						sFechaMod = rowsTbl[i][4];
						break;
					} 							    
				}   		
			}

		});
		
			function formSubmited() {
                alert("Enviado!");
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
			
			$(function() {		
			
				$( "#dialog:ui-dialog" ).dialog( "destroy" );
	
				var dlgOficioNoObjecion = $("#dlgOficioNoObjecion"),
					dlgSolicitudDesembolso = $("#dlgSolicitudDesembolso"),
					dlgTipoCambioNoObjecion	= $("#dlgTipoCambioNoObjecion"),
					dlgMontoNoObjecion	= $("#dlgMontoNoObjecion"),
					dlgFechaNoObjecion	= $("#dlgFechaNoObjecion"),	
					
					allFields = $( [] ).add( dlgOficioNoObjecion ).add( dlgSolicitudDesembolso ).add( dlgTipoCambioNoObjecion ).add( dlgMontoNoObjecion ).add( dlgFechaNoObjecion ),
					tips = $( ".validateTips" );			
	
				function updateTipsDlg( t ) {
					tips
						.text( t );
						alert(t);
					//	.addClass( "ui-state-highlight" );
					// setTimeout(function() { tips.removeClass( "ui-state-highlight", 1500 );}, 500 );
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

				$( "#dialog-form-Desembolso" ).dialog({
					autoOpen: false,
					height: 350,
					width: 650,
					modal: true,
					buttons: {
						"Aceptar": function() {
						
							var bValid = true;
							tips.text("");
							allFields.removeClass( "ui-state-error" );
							
							$("input").each(function (){
								var szStyle = "" + $(this).attr('style');
								if (szStyle.indexOf("uppercase")>1)
								{$(this).val($(this).val().toUpperCase());  
								}
							});
							$("input").each(function (){
								var szStyle = "" + $(this).attr('style');
								if (szStyle.indexOf("lowercase")>1)
								{
									$(this).val($(this).val().toLowerCase());  
								}
							});				
			
							bValid = bValid && checkRequerido( dlgOficioNoObjecion, "Oficio Agente Financiero" );
							bValid = bValid && checkRequerido( dlgSolicitudDesembolso, "No.Solicitud de Desembolso" );
							bValid = bValid && checkRequerido( dlgTipoCambioNoObjecion, "Tipo de Cambio" );
							bValid = bValid && checkRequerido( dlgMontoNoObjecion, "Monto" );
							bValid = bValid && checkRequerido( dlgFechaNoObjecion, "Fecha" );
							
							if (!bValid) return;	
							
							var szTempDub = $("#dlgMontoNoObjecion").val();
							szTempDub = (szTempDub.replace("$","")).replace("$","");	
							szTempDub = (szTempDub.replace(",","")).replace(",","");
							if (Number(szTempDub) <= 0)				
							{
								alert("El Monto debe ser un valor mayor a 0.");
								$("#dlgMontoNoObjecion").focus();
								return;
							}			
						
							$("#OficioNoObjecion").val($("#dlgOficioNoObjecion").val());
							$("#SolicitudDesembolso").val($("#dlgSolicitudDesembolso").val());
							$("#TipoCambioNoObjecion").val($("#dlgTipoCambioNoObjecion").val());
							$("#MontoNoObjecion").val($("#dlgMontoNoObjecion").val());
							$("#FechaNoObjecion").val($("#dlgFechaNoObjecion").val());
							
							var szCampos = " Estatus = 'D', SolicitudDesembolso = '" + $("#SolicitudDesembolso").val() + "', OficioNoObjecion = '" + $("#OficioNoObjecion").val() + "', TipoCambioNoObjecion =" + $("#TipoCambioNoObjecion").val() + ", MontoNoObjecion =" + szTempDub + ", FechaNoObjecion = '" + $("#FechaNoObjecion").val() + "', FechaUltimoMovimiento=GETDATE()"; 
							var szWhere = " numero_soe = '" + sNoSoe + "' ";
							var szTabla = "DSOE";
							$.getJSON("../catalogos/UpdateJson.jsp", {Tabla: szTabla, Param: szWhere, SetParam: szCampos},
						   		function(j)
						   		{
		
					   			});
							alert("¡Operación Exitosa! \r\rSOE ha sido Desembolsado.");					
							
							$( this ).dialog( "close" );
							location.href = 'dSOEGrid.jsp';
						},
						"Cancelar": function() {
							tips.text("");
							allFields.removeClass( "ui-state-error" );
							$( this ).dialog( "close" );
						}
					},
	
					close: function() {
						allFields.removeClass( "ui-state-error" );
						tips.text( "" );
					}
				});		
			
			
				$("#pbDesembolso")
				.button()
				.click(function() {
					$("#dlgFechaNoObjecion").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
			
					$("#dlgNoPrestamo").val(sNumPrestamo);
					$("#dlgNoSOE").val(sNoSoe);
					
					$( "#dialog-form-Desembolso" ).dialog( "open" );

				});
			});	
			
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<input id="OficioNoObjecion" name="OficioNoObjecion"  type="hidden" size="20" readonly style="background:#f0f0f0; text-align:center">
		<input id="SolicitudDesembolso" name="SolicitudDesembolso"  type="hidden" size="20" readonly style="background:#f0f0f0; text-align:center">
		<input id="TipoCambioNoObjecion" name="TipoCambioNoObjecion" class="currency" type="hidden" size="4" readonly style="background:#f0f0f0; text-align:center">
		<input id="MontoNoObjecion" name="MontoNoObjecion" class="currency" type="hidden" size="13" readonly style="background:#f0f0f0; text-align:right">
		<input id="FechaNoObjecion" name="FechaNoObjecion"  type="hidden" size="10" readonly style="background:#f0f0f0; text-align:center">
		<input id="numero_soe" name="numero_soe" type="hidden" value="0" readonly style=" background:#f0f0f0; ">
		
		<div id="container" class="container"  style="width:1100px">	
			<h1>SOE Tradicional</h1>
				<input type="button" id="pbNuevo" value="Generar Nuevo SOE"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbModificar" style="visibility: hidden" value="Modificar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbDesplegar" style="visibility: hidden" value="Desplegar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbDesembolso" style="visibility: hidden" value="Desembolso"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbAgregarDatos" style="visibility: hidden" value="Agregar Datos"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelarSOE" style="visibility: hidden" value="Borrar SOE"/>
			<br/><br/>
			<table id="tblSoeDocumentado" class="display"  >
	            <thead>
	                <tr>
	                	<th >Pr&eacute;stamo</th>
	                	<th>N&uacute;mero Pr&eacute;stamo</th>
	                	<th>N&uacute;mero SOE</th>
	                	<th align="center">Estatus</th>
	                	<th align="center">Fecha Último Movimiento</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
		<div id="dialog-form-Desembolso" title="Datos Desembolso (Oficio de No Objeción)" style="overflow-x:hidden;">
			<br>
			<table border="0" >
				<tr>
					<td align="right" style="width:33%">N&uacute;mero Pr&eacute;stamo:</td>
					<td  style="width:150px">
						<input id="dlgNoPrestamo" type="text" size="40" maxlength="20" readonly disabled style="background:#f0f0f0; text-transform:uppercase;"> 
					</td>
				</tr>		
				<tr>
					<td align="right" style="width:33%">N&uacute;mero SOE:</td>
					<td  style="width:150px">
						<input id="dlgNoSOE" type="text" size="50" maxlength="20" readonly disabled style="background:#f0f0f0; text-transform:uppercase;"> 
					</td>
				</tr>				
				<tr>
					<td align="right" style="width:33%">Oficio Agente Financiero:</td>
					<td  style="width:150px">
						<input id="dlgOficioNoObjecion" type="text" size="20" maxlength="20" style="text-transform:uppercase;"> 
					</td>
				</tr>			
				<tr>
					<td align="right" style="width:33%">No.Solicitud de Desembolso:</td>
					<td  style="width:150px">
						<input id="dlgSolicitudDesembolso" type="text" size="20" maxlength="20" style="text-transform:uppercase;"> 
					</td>
				</tr>							
				<tr>
					<td align="right" >Tipo de Cambio:</td>	
					<td >
						<input id="dlgTipoCambioNoObjecion" type="text" size="7" maxlength="6" onkeypress="Validaciones(this,9)" class="currency" style=" text-align:center">
					</td>
				</tr>
				<tr>			
					<td align="right" >Monto Desembolsado USD:</td>	
					<td >
						<input id="dlgMontoNoObjecion" type="text" size="15" maxlength="15" value="$0.00" onkeypress="Validaciones(this,9)" class="currency" style=" text-align:right">
					</td>	
				</tr>
				<tr>	
					<td align="right">Fecha Valor:</td>
					<td >
						<input id="dlgFechaNoObjecion" type="text" size="12" readonly style="background:#f0f0f0; text-align:center">
					</td>
				</tr>
	        </table>
	        <label class="validateTips ui-state-error" ></label>
		</div>
	</form>
	</body>
</html>