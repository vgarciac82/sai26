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
		<title>Generación SEO No Documentado</title>

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

		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />


		<script type="text/javascript" charset="utf-8">
		var oTable;
		var gsOperacion ='<%=request.getParameter("Op")%>';
		
		var gsPrestamoPar ='<%=request.getParameter("Prestamo")%>';
		var gsCategoPar  ='<%=request.getParameter("Categoria")%>';
		var gsEntidadPar ='<%=request.getParameter("Entidad")%>';
		var gsSOEPar ='<%=request.getParameter("SOEp")%>';
		var gsSOEEstatus ='<%=request.getParameter("SEOEst")%>';
		
	    var xWhere = "";		    
		var xWhereResul = "";

		$(document).ready(function() {
			
			$("input.AyudaSyC").subIniciaDlg();
			
			$("#tblSoeDocumentado tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
				});

		    //COLOCAR AQUI CONDICION PARA QUE A PARTIR DE UN BOTON PARA CONSULTAR INFORMACION ENVIANDO WHERE A LA dataTable
		  	$("#FechaNoObjecion").datepicker({
			showOn: "button",
			dateFormat:"dd/mm/yy",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
			});
		  
			$("#Numero_SOEResp").val(gsSOEPar);
			$("#Numero_SOE").val(gsSOEPar);
			
			$("#Estatus").val(gsSOEEstatus);
			
			querySelectPost("readPrestamoSOEDespliege","id_prestamo", {async: false });
			
			$("#ID_CategoriaInversionPrestamo").val(gsCategoPar);
			querySelectPost("readCategoriaSOEDespliegue", "ID_CategoriaInversionPrestamo", {async: false});
			
			$("#ID_EntidadEjecResp").val(gsEntidadPar);
			querySelectPost("readEntidadFederativaSOEDespliege", "ID_EntidadEjecResp", {async: false});
			
			$("#ID_Prestamo").val($("#id_prestamo").val());
			
			$("#Numero_SOE").attr("disabled", true);
			$("#id_prestamo").attr("disabled", true);

			xWhere += " id_prestamo = "+$("#id_prestamo").val() ;
			xWhere += " and id_categoriaInversion = " + $("#ID_CategoriaInversionPrestamo").val() ;
			xWhere += " and id_entidadEjecResp = " + $("#ID_EntidadEjecResp").val() ;

	  		$("#Numero_SOE").attr("disabled", true);
			$("#id_prestamo").attr("disabled", true);

			if (gsOperacion != 'M' ){
				$("#ID_CategoriaInversionPrestamo").attr("disabled", true);
				$("#ID_EntidadEjecResp").attr("disabled", true);
				
				//ocultar campo de check del datatable
 			    VarEstatus = false;
				
				if (gsOperacion == 'D'){
						//SolDesembolso
						$("#etqFechaNoObjecion").hide;
						$("#FechaNoObjecion").attr("disabled", true);
						$("#CausaDeBorrar").attr("disabled", true);
						$("#CausaDeBorrar").attr("disabled", true);
						$("#pbBorrar").hide();
						$("#SolDesembolso").hide();
						$("#etqSolDesembolso").hide();
						$("#lbOperacion").hide();
						$("#titulo").html("SOE No Documentado (Desplegar)");
				}
				
				 if (gsOperacion == 'B'){
				 	if( gsSOEEstatus == 'CAPTURA') {
							$("#pbBorrar").css("visibility","visible");
							$("#SolDesembolso").hide();
							$("#etqSolDesembolso").hide();
						}
					else {
						$("#CausaDeBorrar").hide();
						$("#etqCausaDeBorrar").hide();
						$("#pbBorrar").hide();
						$("#SolDesembolso").hide();
						$("#etqSolDesembolso").hide();
						alert("Esta acción no es permitida en este SOE ");
						location.href = 'dNoDocumentado.jsp';
					}
					$("#titulo").html("SOE No Documentado (Descartar)");
				}

				if (gsOperacion == 'E'){
					if( gsSOEEstatus == 'CAPTURA') {
						$("#CausaDeBorrar").hide();
						$("#etqCausaDeBorrar").hide();
						$("#pbEnviar").css("visibility","visible");
						$("#SolDesembolso").hide();
						$("#etqSolDesembolso").hide();
						$("#etqFechaNoObjecion").css("visibility","visible");
						$("#FechaNoObjecion").css("visibility","visible");
					}
					else {
						$("#CausaDeBorrar").hide();
						$("#etqCausaDeBorrar").hide();
						$("#pbBorrar").hide();
						$("#SolDesembolso").hide();
						$("#etqSolDesembolso").hide();
						alert("Esta acción no es permitida en este SOE ");
						location.href = 'dNoDocumentado.jsp';
					}
					$("#titulo").html("SOE No Documentado (Enviar)");						
				}

				if (gsOperacion == 'C'){
					// la operacion cerado es el univo que verifica que el estatus sea ENVIADO y no CAPTURA como los otros
					if( gsSOEEstatus == 'ENVIADO') {
						$("#CausaDeBorrar").hide();
						$("#etqCausaDeBorrar").hide();
						$("#pbCerrar").css("visibility","visible");
					}
					else {
						$("#CausaDeBorrar").hide();
						$("#etqCausaDeBorrar").hide();
						$("#pbBorrar").hide();
						$("#SolDesembolso").hide();
						$("#etqSolDesembolso").hide();
						alert("Esta acción no es permitida en este SOE ");
						location.href = 'dNoDocumentado.jsp';
					}
					$("#titulo").html("SOE No Documentado (Cerrar)");
				}
				
				$("#resconsulta").hide();
				
				$("#pbAceptar").hide();
				
				$("#pbBuscar").css("visibility","hidden");
				
				// ocultar la datatable
				$("#tblSoeDocumentado").hide();
				
       		} // termina if 
			else {

			//Muestra campo del datatable
 			 VarEstatus = true;
			
			$("#titulo").html("SOE No Documentado (Modificar)");
			if( gsSOEEstatus == 'CAPTURA') {
					$("#CausaDeBorrar").hide();
					$("#etqCausaDeBorrar").hide();
					$("#pbBorrar").hide();
					$("#SolDesembolso").hide();
					$("#etqSolDesembolso").hide();
					
					$("#ID_CategoriaInversionPrestamo").attr("disabled", false);
					$("#ID_EntidadEjecResp").attr("disabled", false);
								
					$("#pbEnviar").attr("disabled", false);
				
			// bFilter: false,     quita el filtro, por default coloca el campo para filtrar
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=Vdfactura&qw="+xWhere,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				bFilter: false,
				iDisplayLength: 5,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ChecaBox" },
					{ sName: "Contrato" },
					{ sName: "FechaContrato" },
					{ sName: "ImporteContrato" },
					{ sName: "NumConvenio" },
					{ sName: "ImpoteConvenio"	},
					{ sName: "Beneficiario"	},
					{ sName: "NumFactura"	},
					{ sName: "ImporteTotFactura"	},
					{ sName: "ID_Prestamo",bVisible: false },
					{ sName: "ID_Contrato",bVisible: false },
					{ sName: "ID_factura",bVisible: false }
				]
        	});
        	}
			else {
				$("#CausaDeBorrar").hide();
				$("#etqCausaDeBorrar").hide();
				$("#pbBorrar").hide();
				$("#SolDesembolso").hide();
				$("#etqSolDesembolso").hide();
				$("#pbAceptar").hide();
				$("#pbBuscar").hide();
				
				alert("Esta acción no es permitida en este SOE ");
				location.href = 'dNoDocumentado.jsp';
			}						
        	
           } //termina el else

			$("#pbAceptar")
				.button()
				.click(function() {
					cmdGuardarNuevoSOE();
					cmdEliminarDeSOE();
					location.href = 'dNoDocumentadoA.jsp?Op=A&Prestamo='+$("#id_prestamo").val()+'&Categoria='+$("#ID_CategoriaInversionPrestamo").val()+'&Entidad='+$("#ID_EntidadEjecResp").val() +'&SOEp='+$("#Numero_SOE").val() ;
				});

		    $("#pbBuscar")
		    .button()
				.click(function() {
				location.href = 'dNoDocumentadoA.jsp?Op=A&Prestamo='+$("#id_prestamo").val()+'&Categoria='+$("#ID_CategoriaInversionPrestamo").val()+'&Entidad='+$("#ID_EntidadEjecResp").val() +'&SOEp='+$("#Numero_SOE").val() ;
			});

            xWhereResul = "  numerosoe = '"+ $("#Numero_SOE").val() +"'";
            
			oTable = $("#tblSoeDocumentadoResult").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=Vdsoe&qw="+xWhereResul,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				bFilter: false,
				iDisplayLength: 5,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ChecaBox" },
					{ sName: "Contrato" },
					{ sName: "FechaContrato" },
					{ sName: "ImporteContrato" },
					{ sName: "NumConvenio" },
					{ sName: "ImpoteConvenio"	},
					{ sName: "Beneficiario"	},
					{ sName: "NumFactura"	},
					{ sName: "ImporteTotFactura"	},
					{ sName: "ID_Prestamo",bVisible: false },
					{ sName: "ID_Contrato",bVisible: false },
					{ sName: "ID_factura",bVisible: false }
				]
        	});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			
			$("#pbPalla" )
				.button()
				.click(function() {

					var aTrs = $('#tblSoeDocumentadoOrigen').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							var nTr = $('#tblSoeDocumentadoOrigen').dataTable().fnGetData(aTrs[i]);   
							// nTr[0] = nTr[0].replace("nombre","name");
							$('#tblSoeDocumentadoDestino').dataTable().fnAddData( nTr );
							$('#tblSoeDocumentadoOrigen').dataTable().fnDeleteRow( i ); 
						}     
					} 
				});
		
		    $("#pbEnviar")
		    .button()
				.click(function() {
				//dsoe.Estatus=ENVIADO		Enviado a desembolso
				$("#Estatus").val("ENVIADO");
				//Envias los parametros:  Estatus,CausaDeBorrar,Numero_SOEResp
				queryFormPost("updateSOEDocumentado",{async: false });
				alert("Enviado!");
				location.href = 'dNoDocumentado.jsp';
			});
		

		    $("#pbBorrar")
		    .button()
				.click(function() {
				//dsoe.Estatus=DESCARTADO		Se podra borrar un soe solo cuando este tenga status de captura.
				$("#Estatus").val("DESCARTADO");
				//Envias los parametros:  Estatus,CausaDeBorrar,Numero_SOEResp
				if ($("#CausaDeBorrar").val()== ""){
					alert("Registre la Causa");
				}
				else {
				    alert("SOE Descartado");
					queryFormPost("updateSOEDocumentado",{async: false });
				}
				location.href = 'dNoDocumentado.jsp';
			});

		    $("#pbCerrar")
		    .button()
				.click(function() {
				//dsoe.Estatus=CERRADO		Ya regreso de enviado, no se debera eliminar, se registro el oficio del agente financiero
				$("#Estatus").val("CERRADO");
				//Envias los parametros:  Estatus,CausaDeBorrar,Numero_SOEResp
			
				if ($("#SolDesembolso").val()== ""){
					alert("Registre el Número solicitud de SOE");
				}
				else {
				    alert("SOE Cerrado");
					queryFormPost("updateSOEDocumentadoCerrar",{async: false });
				}
				location.href = 'dNoDocumentado.jsp';
			});

			$("#pbAceptar")
				.button()
				.click(function() {
					cmdGuardarNuevoSOE();
					cmdEliminarDeSOE();
					location.href = 'dNoDocumentadoA.jsp?Op=A&Prestamo='+$("#id_prestamo").val()+'&Categoria='+$("#ID_CategoriaInversionPrestamo").val()+'&Entidad='+$("#ID_EntidadEjecResp").val() +'&SOEp='+$("#Numero_SOE").val() ;
				});

			$("#pbCancelar")
				.button()
				.click(function() {
				//var bValid = true;
				//tips.text("");
				//allFields.removeClass( "ui-state-error" );
				location.href = 'dNoDocumentado.jsp';
				});
				
							
		});
		// TERMINA AREA DE (document).ready
		
		function formSubmited() {
                alert("Enviado!");
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

      		// guardar los datos seleccionados en el grid
			function cmdGuardarNuevoSOE()
			{
					var nRows = $("#tblSoeDocumentado tr").length -1;
					var oTable = $('#tblSoeDocumentado').dataTable();
					var table = document.getElementById("tblSoeDocumentado");
					var data = $('#tblSoeDocumentado').dataTable().fnGetNodes();
					for( var i=0 ; i < nRows ; i++ ) {
							var aData = oTable.fnGetData( i );
							if ( $('input', data[i] )[0].checked ){
								 $("#ID_Prestamo").val(aData[9]);
								 $("#ID_Contrato").val(aData[10]);
								 $("#ID_Factura").val(aData[11]);
								if ($("#Numero_SOE").val() == "" ) {
									// readGeneraNoSOE es el que genera llave de SOE, debes enviar el ID_Prestamo
					      			queryFormPost("readGeneraNoSOE","ID_Prestamo,ID_Prestamo",{async: false });
					      			$("#Numero_SOE").attr("disabled", true);
					      			$("#id_prestamo").attr("disabled", true);
					      			
					      			alert("No. de SOE generado");

					      			$("#Numero_SOEResp").val($("#Numero_SOE").val());
					 			}
								if ($("#Numero_SOE").val() != "" || $("#Numero_SOE").val() != "null" ) {					 			
									// aqui hace el insert en el crud
									$("#Numero_SOEResp").val($("#Numero_SOE").val());
							    	queryFormPost("InsertSOEDocumentado","Numero_SOEResp",{async: false });
							    }
							}
					}
			}

			function cmdEliminarDeSOE()
			{

					var nRows = $("#tblSoeDocumentadoResult tr").length -1;
					var oTable = $('#tblSoeDocumentadoResult').dataTable();
					var table = document.getElementById("tblSoeDocumentadoResult");
					var data = $('#tblSoeDocumentadoResult').dataTable().fnGetNodes();
					for( var i=0 ; i < nRows ; i++ ) {
							var aData = oTable.fnGetData( i );
							if ( $('input', data[i] )[0].checked ){
								 $("#ID_Prestamo").val(aData[9]);
								 $("#ID_Contrato").val(aData[10]);
								 $("#ID_Factura").val(aData[11]);
								 
								if ($("#Numero_SOE").val() == "" ) {
									// readGeneraNoSOE es el que genera llave de SOE, debes enviar el ID_Prestamo
					      			queryFormPost("readGeneraNoSOE","ID_Prestamo,ID_Prestamo",{async: false });
					      			$("#Numero_SOE").attr("disabled", true);
					      			$("#id_prestamo").attr("disabled", true);
					      			
					      			alert("No. de SOE generado");

					      			$("#Numero_SOEResp").val($("#Numero_SOE").val());

					 			}
								if ($("#Numero_SOE").val() != "" || $("#Numero_SOE").val() != "null" ) {					 			
									// aqui hace el insert en el crud
									$("#Numero_SOEResp").val($("#Numero_SOE").val());
							    	queryFormPost("EliminarSOEDocumentado","Numero_SOEResp",{async: false });
							    }
							}
					}
			}

      		// Eliminar rgistros del SOE
			function cmdEliminarDeSOE()
			{

					var nRows = $("#tblSoeDocumentadoResult tr").length -1;
					var oTable = $('#tblSoeDocumentadoResult').dataTable();
					var table = document.getElementById("tblSoeDocumentadoResult");
					var data = $('#tblSoeDocumentadoResult').dataTable().fnGetNodes();
					for( var i=0 ; i < nRows ; i++ ) {
							var aData = oTable.fnGetData( i );
							if ( $('input', data[i] )[0].checked ){
								 $("#ID_Prestamo").val(aData[9]);
								 $("#ID_Contrato").val(aData[10]);
								 $("#ID_Factura").val(aData[11]);
								 
								if ($("#Numero_SOE").val() == "" ) {
									// readGeneraNoSOE es el que genera llave de SOE, debes enviar el ID_Prestamo
					      			queryFormPost("readGeneraNoSOE","ID_Prestamo,ID_Prestamo",{async: false });
					      			$("#Numero_SOE").attr("disabled", true);
					      			$("#id_prestamo").attr("disabled", true);
					      			
					      			alert("No. de SOE generado");

					      			$("#Numero_SOEResp").val($("#Numero_SOE").val());

					 			}
								if ($("#Numero_SOE").val() != "" || $("#Numero_SOE").val() != "null" ) {					 			
									// aqui hace el insert en el crud
									$("#Numero_SOEResp").val($("#Numero_SOE").val());
							    	queryFormPost("EliminarSOEDocumentado","Numero_SOEResp",{async: false });
							    }
							}
					}
			}

		</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0" >
	<form> 
					<div id="tabs-0" class="container">
						<h1><div id="titulo"></div><label style="font-size: 9pt"></label></h1>
					<table>
					<tr>
					<td><input type="hidden" id="ID_Prestamo" name="ID_Prestamo" type="text" size="5" maxlength="15"> </td>
					<td><input type="hidden" id="ID_Contrato" name="ID_Contrato" type="text" size="5" maxlength="15"> </td>
					<td><input type="hidden" id="ID_Factura" name="ID_Factura" type="text" size="5" maxlength="15"> </td>
					<td><input type="hidden" id="Estatus" name="Estatus" type="text" size="20" maxlength="20"> </td>
					<td><input type="hidden" id="Numero_SOEResp" name="Numero_SOEResp" type="text" size="10" maxlength="20"> </td>
					</tr>
					<tr>
						<td>N&uacute;mero:</td>
							<td width=50px align="right">
					     		<input id="Numero_SOE" name="Numero_SOE" type="text" size="12" maxlength="20" > 
					 	  </td>
					<td align="right">Prestamo:</td>
					<td colspan="3">
						<select id="id_prestamo" name="id_prestamo"  style="width: 20em;">
						</select>
					</td>
                    </tr>
                    <tr>
					<td>Categoria:</td>
					<td width=50px align="right">
						<select id="ID_CategoriaInversionPrestamo" name="ID_CategoriaInversionPrestamo">
						</select>
					</td>
					<td align="right">Entidad Federativa:</td>
					<td colspan="3">
						<select id="ID_EntidadEjecResp" name="ID_EntidadEjecResp"  style="width: 20em;">
						</select>
					</td>
					</tr>
					<tr> <td></td>
					     <td></td>
					     <td align="right" id="etqCausaDeBorrar" >Causa para Descartar:</td>
					    <td colspan="3"><input id="CausaDeBorrar" name="CausaDeBorrar" type="text" size="60" maxlength="500" style='text-transform:uppercase;'> </td>
					</tr>
					<tr>
						<td></td>
					     <td></td>
						<td align="right" id="etqSolDesembolso" >Solicitud de Desembolso:</td>
					    <td colspan="3"><input id="SolDesembolso" name="SolDesembolso" type="text" size="60" maxlength="500" style='text-transform:uppercase;'> </td>
					</tr>
					<tr>
						<td></td>
					     <td></td>
						<td align="right" id="etqFechaNoObjecion" >Fecha de No Objeción</td>
					    <td colspan="3"><input type="text" name="FechaNoObjecion" id="FechaNoObjecion" maxlength="10" size="12" readonly/></td>
					</tr>
					<tr> <td colspan="3" id="resconsulta"> <h1>Resultado de la consulta<label id="lbOperacion" style="font-size: 5pt"></label></h1> </td> </tr>
					</table>
					</div>

 
		<div id="container" class="container">	
			<table id="tblSoeDocumentado" class="display"  >
	            <thead>
	                <tr>
	                	<th width="60px">Sel</th>
	                	<th width="80px">Contrato</th>
	                	<th width="80px">Fecha</th>
	                	<th width="80px">Importe</th>
	                	<th width="80px">Convenio</th>
	                	<th width="80px">Importe</th>
	                	<th width="80px">Beneficiario</th>
	                	<th width="80px">Factura</th>
	                	<th width="80px">Importe</th>
	                	<th width="40px">P</th>
	                	<th width="40px">C</th>
	                	<th width="40px">F</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>

		<div id="container" class="container">
		<table>	
			<tr> <td colspan="3"> <h1>Datos en SOE <label id="lbOperacion" style="font-size: 5pt"></label></h1> </td> </tr>
			<table id="tblSoeDocumentadoResult" class="display"  >
	            <thead>
	                <tr>
	                	<th width="60px">Sel</th>
	                	<th width="80px">Contrato</th>
	                	<th width="80px">Fecha</th>
	                	<th width="80px">Importe</th>
	                	<th width="80px">Convenio</th>
	                	<th width="80px">Importe</th>
	                	<th width="80px">Beneficiario</th>
	                	<th width="80px">Factura</th>
	                	<th width="80px">Importe</th>
	                	<th width="40px">P</th>
	                	<th width="40px">C</th>
	                	<th width="40px">F</th>
	                </tr>
	            </thead>
	        </table>
	       </table>
			<br/>
		</div>

			<label class="validateTips ui-state-error" ></label>
			<table width="100%" border="0" >
				<tr>
					<td width="10%">&nbsp;</td>
					<td width="80%" align="center">
						<input type="button" id="pbAceptar" value="Aceptar"/>&nbsp;&nbsp;
						<input type="button" id="pbCancelar" value="Cancelar"/>&nbsp;&nbsp;
						<input type="button" id="pbBorrar" style="visibility: hidden" value="Descartar"/>&nbsp;&nbsp;
						<input type="button" id="pbEnviar" style="visibility: hidden" value="Enviar"/>&nbsp;&nbsp;
						<input type="button" id="pbCerrar" style="visibility: hidden" value="Cerrar"/>&nbsp;&nbsp;
						<input type="button" id="pbBuscar" value="B&uacute;squeda"/>
					</td>
					<td width="10%" align="right">
					</td>
				</tr>
			</table>


	</form>
	</body>
</html>