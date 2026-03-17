<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CUENTAS_BANCARIAS_BENEFICIARIOS")){
		mntoCuentas = "SI".equals(usuario.getPropiedad("CUENTAS_BANCARIAS_BENEFICIARIOS").getValor());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Beneficiario</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Catálogo de Beneficiarios">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>

		<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			

			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
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
	
		var giTimer=0;
		var gsOperacion ='<%=request.getParameter("Op")%>';

		function fnTimerFiscaltoActual()
		{
			$("#cMunicipioActual").val($("#cMunicipioFiscal").val());
			window.clearInterval(giTimer);
		}

		function fnTimerActualtoFiscal()
		{
			$("#cMunicipioFiscal").val($("#cMunicipioActual").val());
			window.clearInterval(giTimer);
		}

		$(document).ready(function() {
			
			oTableDisp = $("#tblTipoRFCDisp").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				sScrollY: "114",
				sScrollX: "100%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : false,
				//bSearch : false,
				//sScrollXInner: "100%",
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
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdTipoRFCDisp",	bSearchable: false,	bSortable: false, bVisible: false },
					{ sName: "cTipoRFCDisp",	bSortable: false }
				]
        	});
			
			oTable = $("#tblTipoRFC").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				sScrollY: "114",
				sScrollX: "100%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : false,
				//bSearch : false,
				//sScrollXInner: "100%",
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
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdTipoRFC",	bSearchable: false,	bSortable: false, bVisible: false },
					{ sName: "cTipoRFC",	bSortable: false }
				]
				
        	});
			
			$("#tblCuentasBancarias").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				sScrollY: "125",
				sScrollX: "100",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : true,
				//bSearch : false,
				//sScrollXInner: "100%",
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
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cBanco",	bSearchable: false,	bSortable: false, bVisible: false },
					{ sName: "dBanco" },
					{ sName: "cPlaza" },
					{ sName: "dCuentaBancaria" },
					{ sName: "dDigitoVerificador" },
					{ sName: "dSucursal" },
					{ sName: "cStatusCuenta",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "dStatusCuenta" },
					{ sName: "nBCBEnviadoSICOP",	bSearchable: false,	bSortable: false, bVisible: false  }
				]
				
        	});
			
			
			$(".tabs").tabs();
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

			function fnGetSelected( oTableLocal ) {     
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

			$('#tblTipoRFCDisp tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');     
				} );
			
			$('#tblTipoRFC tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');     
				} );

			$( "#cIdTipoPersonaRFC" )
				.change(function() {

					if ($(this).val()=='2' || $(this).val()=='3' )	// si cambia el combo de Tipo RFC has visible o no controles
					{
						$("#lbCURP").css("visibility","visible");
						$("#dCURP").css("visibility","visible");
						$("#trPersonaFisica").css("visibility","visible");
						$("#trPersonaMoral").css("visibility","hidden");

						$("#trPersonaMoral").val("");
						$("#cIdRFC1").attr("maxlength","4");
						if (gsOperacion != 'D')
						{
							$("#cIdRFC1").val("");
							$("#cIdRFC2").val("");
							$("#cIdRFC3").val("");
						}
					}
					else
					{
						$("#lbCURP").css("visibility","hidden");
						$("#dCURP").css("visibility","hidden");
						$("#trPersonaFisica").css("visibility","hidden");
						$("#trPersonaMoral").css("visibility","visible");

						$("#lbCURP").val("");
						$("#dCURP").val("");
						$("#trPersonaFisica").val("");
						
						$("#cIdRFC1").attr("maxlength","3");

						if (gsOperacion != 'D')
						{
							$("#cIdRFC1").val("");
							$("#cIdRFC2").val("");
							$("#cIdRFC3").val("");
						}					
					}
					
					$('#tblTipoRFCDisp').dataTable().fnClearTable(); 
					$('#tblTipoRFC').dataTable().fnClearTable(); 
									
					switch($(this).val())
					{
					case '0':
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=6 />", "ENTIDAD GOBIERNO FED. O EST." ]); 
						break;
					case '1':
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=1 />", "CONTRATISTA" ]); 
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=2 />", "PROVEEDOR DE BIENES" ]); 
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=3 />", "PROVEEDOR DE SERVICIOS" ]);
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=6 />", "ENTIDAD GOBIERNO FED. O EST." ]);
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=7 />", "ORGANISMO OPERADOR" ]);
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=8 />", "ASOCIACION DE USUARIOS" ]);
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=9 />", "ORGANISMO FINANCIERO EXTERNO" ]);
						break;
					case '2':
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=1 />", "CONTRATISTA" ]); 
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=2 />", "PROVEEDOR DE BIENES" ]); 
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=3 />", "PROVEEDOR DE SERVICIOS" ]);
						break;
					case '3':
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=4 />", "EMPLEADO DE C.N.A." ]); 
						break;
					case '4':
						$('#tblTipoRFCDisp').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=5 />", "AREA DE C.N.A." ]); 
						break;
					}
				});

			$( "#cEstadoFiscal" )
				.change(function() {
					querySelectPost("MunicipiosFiscalRead", "cMunicipioFiscal");
				});

			$( "#cEstadoActual" )
				.change(function() {
					querySelectPost("MunicipiosActualRead", "cMunicipioActual");
				});

			$( "#chkRFCValido" )
				.change(function() {
					if ($('#chkRFCValido').is(':checked')) {
				        $('#cRFCValido').val(1);
				    } else {
				        $('#cRFCValido').val(0);
				    }
				});

			$( "#chkExtranjero" )
				.change(function() {
					if ($('#chkExtranjero').is(':checked')) {
				        $('#cExtranjero').val(1);
				    } else {
				        $('#cExtranjero').val(0);
				    }
				});
			
			$( "#cbBanco" )
				.change(function() {
					var szTemp = CalculaCLABE()					
					$("#txtCLABE").val(szTemp);
					$("#txtDigitoVerificador").val(szTemp.substr(17,1));
				});
			
			$( "#txtPlaza" )
				.change(function() {
					var szTemp = CalculaCLABE()					
					$("#txtCLABE").val(szTemp);
					$("#txtDigitoVerificador").val(szTemp.substr(17,1));
				});
			
			$( "#cIdRFC1" )
				.change(function() {
					if ($("#cIdRFC1").val() != "" && $("#cIdRFC2").val() != "" && ($("#cIdTipoPersonaRFC").val() == 2 || $("#cIdTipoPersonaRFC").val() == 3))					
						$("#dCURP").val($("#cIdRFC1").val() + $("#cIdRFC2").val());
				});
			$( "#cIdRFC2" )
				.change(function() {
					if ($("#cIdRFC1").val() != "" && $("#cIdRFC2").val() != "" && ($("#cIdTipoPersonaRFC").val() == 2 || $("#cIdTipoPersonaRFC").val() == 3))					
						$("#dCURP").val($("#cIdRFC1").val() + $("#cIdRFC2").val());
				});
			
			$( "#txtCuentaBancaria" )
				.change(function() {
					var szTemp = CalculaCLABE()					
					$("#txtCLABE").val(szTemp);
					$("#txtDigitoVerificador").val(szTemp.substr(17,1));
				});
				
			$( "#txtSucursal" )
				.change(function() {
					var szTemp = CalculaCLABE();
				});
				
			$("#pbCopiaDirActual" )
				.button()
				.click(function() {

					$("#cEstadoActual").val($("#cEstadoFiscal").val());
					querySelectPost("MunicipiosActualRead", "cMunicipioActual");
					$("#dCalleActual").val($("#dCalleFiscal").val());
					$("#dNoDomicilioActual").val($("#dNoDomicilioFiscal").val());
					$("#dNoInteriorDomicilioActual").val($("#dNoInteriorDomicilioFiscal").val());
					$("#dOtrosDatosActual").val($("#dOtrosDatosFiscal").val());
					$("#dColoniaActual").val($("#dColoniaFiscal").val());
					giTimer=window.setInterval("fnTimerFiscaltoActual()",2000);
					$("#dCodigoPostalActual").val($("#dCodigoPostalFiscal").val());
					$("#dTelefonoActual").val($("#dTelefonoFiscal").val());
					$("#dFaxActual").val($("#dFaxFiscal").val());
					$("#dEMailActual").val($("#dEMailFiscal").val());

					alert("Se realizo la copia de la Dirección Fiscal a la Dirección Actual.");
				});

			$( "#pbCopiaDirFiscal" )
				.button()
				.click(function() {

					$("#cEstadoFiscal").val($("#cEstadoActual").val());
					querySelectPost("MunicipiosFiscalRead", "cMunicipioFiscal");
					$("#dCalleFiscal").val($("#dCalleActual").val());
					$("#dNoDomicilioFiscal").val($("#dNoDomicilioActual").val());
					$("#dNoInteriorDomicilioFiscal").val($("#dNoInteriorDomicilioActual").val());
					$("#dOtrosDatosFiscal").val($("#dOtrosDatosActual").val());
					$("#dColoniaFiscal").val($("#dColoniaActual").val());
					giTimer=window.setInterval("fnTimerActualtoFiscal()",2000);
					$("#dCodigoPostalFiscal").val($("#dCodigoPostalActual").val());
					$("#dTelefonoFiscal").val($("#dTelefonoActual").val());
					$("#dFaxFiscal").val($("#dFaxActual").val());
					$("#dEMailFiscal").val($("#dEMailActual").val());

					alert("Se realizo la copia de la Dirección Actual a la Dirección Fiscal.");
				});
			
			$("#pbPalla" )
				.button()
				.click(function() {

					var aTrs = $('#tblTipoRFCDisp').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblTipoRFCDisp').dataTable().fnGetData(aTrs[i]);   
//							nTr[0] = nTr[0].replace("nombre","name");
							$('#tblTipoRFC').dataTable().fnAddData( nTr );
							$('#tblTipoRFCDisp').dataTable().fnDeleteRow( i ); 
						}     
					} 
				});
			
			$("#pbPaca" )
				.button()
				.click(function() {

					var aTrs = $('#tblTipoRFC').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblTipoRFC').dataTable().fnGetData(aTrs[i]);  
							nTr[0] = nTr[0].replace("name","nombre");
							$('#tblTipoRFCDisp').dataTable().fnAddData( nTr );
							$('#tblTipoRFC').dataTable().fnDeleteRow( i ); 
						}     
					} 
				});


			// Inicializaciones CRUD
			querySelectPost("catalogoTipoPersonaRFCRead", "cIdTipoPersonaRFC", {async: false});
			querySelectPost("EstadosRead", "cEstadoFiscal", {async: false});
			querySelectPost("EstadosRead", "cEstadoActual", {async: false});
			querySelectPost("BancosRead", "cbBanco", {async: false});

			if (gsOperacion == 'A')
			{
				$("#lbOperacion").text(" (Agregar)");
				$( "#cIdTipoPersonaRFC" ).change();
				querySelectPost("MunicipiosFiscalRead", "cMunicipioFiscal", {async: false});
				querySelectPost("MunicipiosActualRead", "cMunicipioActual", {async: false});
				$( "#pbCuentasBancarias" ).css("visibility","hidden");
				$("#cIdTipoPersonaRFC").focus();
			}

			if (gsOperacion == 'D')
			{
				$("#lbOperacion").text(" (Desplegar)");
				$("#pbAceptar").css("visibility","hidden");
				$("#pbCancelar").val("Regresar");
				$("#pbPalla").attr("disabled", true);
				$("#pbPaca").attr("disabled", true);
				$("#tdCBEN").css("visibility","visible");
				//$( "#pbCuentasBancarias" ).css("visibility","visible");
				
				var szTemp = '<%=new String(  ( request.getParameter("Re") == null? "" : request.getParameter("Re") ).getBytes("ISO-8859-1"), "UTF-8"  )%>';
				szTemp = (szTemp.replace("-","")).replace("-","");	

				$("#dRFC").val(szTemp);

				queryFormPost("BeneficiariosRead", {async: false });
				querySelectPost("MunicipioActualByBeneficiario", "cMunicipioActual", {async: false});
				querySelectPost("MunicipioFiscalByBeneficiario", "cMunicipioFiscal", {async: false});
				$( "#cIdTipoPersonaRFC" ).change();
				
				if ($("#cIdTipoPersonaRFC").val() == 2 || $("#cIdTipoPersonaRFC").val() == 3)
				{
					$("#dNombrePF").val($("#dNombre").val());
					$("#cIdRFC1").val($("#dRFC").val().substr(0,4));
					$("#cIdRFC2").val($("#dRFC").val().substr(4,6));
					$("#cIdRFC3").val($("#dRFC").val().substr(10));
				}
				else
				{					
					$("#cIdRFC1").val($("#dRFC").val().substr(0,3));
					$("#cIdRFC2").val($("#dRFC").val().substr(3,6));
					$("#cIdRFC3").val($("#dRFC").val().substr(9));
					$("#dNombrePM").val($("#dNombre").val());
				}

				if ($("#cRFCValido").val() == '1') $("#chkRFCValido").attr("checked",true);
				if ($("#cExtranjero").val() == '1') $("#chkExtranjero").attr("checked",true);

				//$("input").attr("readonly", true);
				$("input").css("background", "#f0f0f0");

				$("input").attr("disabled", true);

				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
				
				$("#cBeneficiario").attr("disabled", false);

				var szWhere = " cBeneficiario = " + $("#cBeneficiario").val() + " ";
				var szTabla = "TIPORFCBENEFICIARIO";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                       
    					for (var i = 0; i < j.length; i++) 
    					{  
    						$('#tblTipoRFC').dataTable().fnAddData([ "<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=" + j[i].Col1 + " />", j[i].Col0 ]);
    						var aTrsDisp = $('#tblTipoRFCDisp').dataTable().fnGetNodes(); 
    						for ( var k=aTrsDisp.length ; k>=0; k-- )     
							{
								var nTrDisp = $('#tblTipoRFCDisp').dataTable().fnGetData(aTrsDisp[k]);
								if ("<input type=text id='cIdTipoRFC' name='cIdTipoRFC' value=" + j[i].Col1 + " />" == nTrDisp[0])
								{
									$('#tblTipoRFCDisp').dataTable().fnDeleteRow( k );
								}
							}    
						}
		         });   
				
				szWhere = " dRFC = '" + $("#dRFC").val() + "' "; 
				szTabla = "BENEFICIARIOCUENTASBANCARIAS2";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{     
						for (var i = 0; i < j.length; i++) 
    					{
	    					$('#tblCuentasBancarias').dataTable().fnAddData([ 
								"<input type='text' id='cBanco' name='cBanco' value='" + j[i].Col1 + "'  style='width:50px; border-width:0; background-color:transparent'/>", 
								"<input type='text' id='dBanco' name='dBanco' value='" + j[i].Col6 + "' readonly style='width:120px; border-width:0; background-color:transparent'/>", 
								"<input type='text' id='cPlaza' name='cPlaza' value='" + j[i].Col2 + "' readonly style='width:38px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dCuentaBancaria' name='dCuentaBancaria' value='" + j[i].Col3 + "' readonly style='width:100px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dDigitoVerificador' name='dDigitoVerificador' value='" + j[i].Col4 + "' readonly style='width:35px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dSucursal' name='dSucursal' value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='cStatusCuenta' name='cStatusCuenta' value='" + j[i].Col5 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dStatusCuenta' name='dStatusCuenta' value='" + ((j[i].Col10==1)? "Activo":"Inactivo") + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								
								"<input type='text' id='nBCBEnviadoSICOP' name='nBCBEnviadoSICOP' value='" + j[i].Col10 + "' readonly style='width:10px; border-width:0; background-color:transparent'/>"
								]); 
    					}
		         });   

			}
			$("#imgPlayStop").css("visibility", "hidden");
		});
		function formSubmited() {
                alert("Beneficiario enviado!");
            }
		function Hello() {
			alert("helloo");
		}
		
		</script>

	<script type="text/javascript" charset="utf-8">		// funciones del dialogo de Cuentas Bancarias
		
		$(function() {

			$( "#dialog:ui-dialog" ).dialog( "destroy" );

			var name = $( "#name" ),
				email = $( "#email" ),
				password = $( "#password" ),

				IdRFC1 = $("#cIdRFC1"),
				IdRFC2 = $("#cIdRFC2"),
				IdRFC3 = $("#cIdRFC3"),
				CURP = $("#dCURP"),
				NombrePF = $("#dNombrePF"),
				ApellidoPaterno = $("#dApellidoPaterno"),
				ApellidoMaterno = $("#dApellidoMaterno"),
				NombrePM = $("#dNombrePM"),

				CalleFiscal = $( "#dCalleFiscal" ),
				CalleActual = $( "#dCalleActual" ),
				NoDomicilioFiscal = $("#dNoDomicilioFiscal"),
				NoDomicilioActual = $("#dNoDomicilioActual"),
				NoInteriorDomicilioFiscal = $("#dNoInteriorDomicilioFiscal"),
				NoInteriorDomicilioActual = $("#dNoInteriorDomicilioActual"),
				OtrosDatosFiscal = $("#dOtrosDatosFiscal"),
				OtrosDatosActual = $("#dOtrosDatosActual"),
				ColoniaFiscal = $("#dColoniaFiscal"),
				ColoniaActual = $("#dColoniaActual"),
				CodigoPostalFiscal = $("#dCodigoPostalFiscal"),
				CodigoPostalActual = $("#dCodigoPostalActual"),
				TelefonoFiscal = $("#dTelefonoFiscal"),
				TelefonoActual = $("#dTelefonoActual"),
				FaxFiscal = $("#dFaxFiscal"),
				FaxActual = $("#dFaxActual"),
				EMailFiscal = $("#dEMailFiscal"),
				EMailActual = $("#dEMailActual"),
				APaternoApoderado = $("#dAPaternoApoderado"),
				AMaternoApoderado = $("#dAMaternoApoderado"),
				NombreApoderado = $("#dNombreApoderado"),
				TelefonoApoderado = $("#dTelefonoApoderado"),
				FaxApoderado = $("#dFaxApoderado"),
				EMailApoderado = $("#dEMailApoderado"),
				
				Plaza =  $("#txtPlaza"),
				CuentaBancaria =  $("#txtCuentaBancaria"),
				Sucursal =  $("#txtSucursal"),
				EntidadSiaff =  $("#txtEntidadSiaff"),					
				
				allFields = $( [] ).add( name ).add( email ).add( password ).add( Plaza ).add( CuentaBancaria ).add( Sucursal ).add( EntidadSiaff ).add(IdRFC1).add(IdRFC2).add(IdRFC3).add(CURP).add( NombrePF ).add( ApellidoPaterno ).add( ApellidoMaterno ).add( NombrePM ).add( CalleFiscal ).add( CalleActual ).add(NoDomicilioFiscal).add(NoDomicilioActual).add(NoInteriorDomicilioFiscal).add(NoInteriorDomicilioActual).add(OtrosDatosFiscal).add(OtrosDatosActual).add(ColoniaFiscal).add(ColoniaActual).add(CodigoPostalFiscal).add(CodigoPostalActual).add(TelefonoFiscal).add(TelefonoActual).add(FaxFiscal).add(FaxActual).add(EMailFiscal).add(EMailActual).add( APaternoApoderado ).add( AMaternoApoderado ).add( NombreApoderado ).add( TelefonoApoderado ).add( FaxApoderado ).add( EMailApoderado ),
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
				autoOpen: false,
				height: 200,
				width: 800,
				modal: true,
				buttons: {
					"Aceptar": function() {
				
						var bValid = true;
						tips.text("");
						allFields.removeClass( "ui-state-error" );
	
						bValid = bValid && checkRequerido( Plaza, "Plaza" );
						bValid = bValid && checkLength( Plaza, "Plaza", 3, 3 );
						bValid = bValid && checkRegexp( Plaza, /^([0-9])+$/, "Plaza solo permite números : 0-9" );
						
						bValid = bValid && checkRequerido( CuentaBancaria, "No.Cuenta" );
						bValid = bValid && checkLength( CuentaBancaria, "No.Cuenta", 11, 11 );
						bValid = bValid && checkRegexp( CuentaBancaria, /^([0-9])+$/, "No.Cuenta solo permite números : 0-9" );
						
						bValid = bValid && checkRequerido( Sucursal, "Sucursal" );
						bValid = bValid && checkLength( Sucursal, "Sucursal", 1, 4 );
						bValid = bValid && checkRegexp( Sucursal, /^([0-9])+$/, "Sucursal solo permite números : 0-9" );
						
						
						if (!bValid) return;
						
						var szTemp = CalculaCLABE()					
						$("#txtCLABE").val(szTemp);
						$("#txtDigitoVerificador").val(szTemp.substr(17,1));
						
						$('#tblCuentasBancarias').dataTable().fnAddData([ 
							"<input type='text' id='cBanco' name='cBanco' value='" + $('#cbBanco').val() + "'  style='width:55px; border-width:0; background-color:transparent'/>", 
							"<input type='text' id='dBanco' name='dBanco' value='" + $('#cbBanco option:selected').text() + "' readonly style='width:130px; border-width:0; background-color:transparent'/>", 
							"<input type='text' id='cPlaza' name='cPlaza' value='" + $('#txtPlaza').val() + "' readonly style='width:35px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='dCuentaBancaria' name='dCuentaBancaria' value='" + $('#txtCuentaBancaria').val() + "' readonly style='width:100px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='dDigitoVerificador' name='dDigitoVerificador' value='" + $('#txtDigitoVerificador').val() + "' readonly style='width:35px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='dSucursal' name='dSucursal' value='" + $('#txtSucursal').val() + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='cStatusCuenta' name='cStatusCuenta' value='1' readonly style='width:60px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='dStatusCuenta' name='dStatusCuenta' value='Activo' readonly style='width:60px; border-width:0; background-color:transparent'/>",
							
							"<input type='text' id='nBCBEnviadoSICOP' name='nBCBEnviadoSICOP' value='0' readonly style='width:10px; border-width:0'/>"
							]); 
						
						$("#dRFC").removeAttr('disabled');
						$('#cUsuarioModifico').removeAttr('disabled');
						$('#fCuentaModifico').removeAttr('disabled');
						$('#tblCuentasBancarias').dataTable().fnSetColumnVis( 0, true ); 
						$('#tblCuentasBancarias').dataTable().fnSetColumnVis( 6, true ); 
						$('#tblCuentasBancarias').dataTable().fnSetColumnVis( 8, true ); 
						
						$('#cUsuarioModifico').val($.trim("<%=usuario.getLogin()%>"));
						var dHoy=new Date();
						$("#fCuentaModifico").val(dHoy.getFullYear() + "" + cerosIzq("" +(dHoy.getMonth() + 1),2) + "" + cerosIzq("" +dHoy.getDate(),2) + " " + cerosIzq("" +dHoy.getHours(),2) + ":" + cerosIzq("" +dHoy.getMinutes(),2));
						
						// Dubois,Se estan perdiendo las cuentas por lo que ahora ya no vamos a borrar nada
						//queryFormPost("tBeneficiarioCuentasBancariasDelete,tBeneficiarioCuentasBancariasCreate");
						
						queryFormPost("tBeneficiarioCuentasBancariasCreate");

						$('#tblCuentasBancarias').dataTable().fnSetColumnVis( 0, false ); 
						$('#tblCuentasBancarias').dataTable().fnSetColumnVis( 6, false ); 
						$('#tblCuentasBancarias').dataTable().fnSetColumnVis( 8, false ); 
						$( this ).dialog( "close" );
					},
					Cancelar: function() {
						$( this ).dialog( "close" );
					}
				},
				close: function() {
					allFields.removeClass( "ui-state-error" );
					tips.text( "" );
				}
			});

			$( "#pbCuentasBancarias" )
				.button()
				.click(function() {
					$("#txtNombreProveerdor").val( (IdRFC1.val() + "-"+ IdRFC2.val() + "-" + IdRFC3.val()).toUpperCase() + " / "+($.trim($("#dApellidoPaterno").val() + " " + $("#dApellidoMaterno").val() + " " + $("#dNombrePF").val() + " " + $("#dNombrePM").val())).toUpperCase());
					
					$("#cbBanco").removeAttr('disabled');
					$("#cbBanco").css("background","white");
					$("#txtPlaza").removeAttr("disabled");
					$("#txtPlaza").css("background", "white");					
					$("#txtCuentaBancaria").removeAttr("disabled");
					$("#txtCuentaBancaria").css("background", "white");
					
					$("#txtSucursal").removeAttr("disabled");
					$("#txtSucursal").css("background", "white");					
					$("#chkEntidad").removeAttr("disabled");
					$("#chkEntidad").css("background","white");
					
					$( "#dialog-form" ).dialog( "open" );				
				});

			$("#pbAceptar")
				.button()
				.click(function() {

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
				

				var iMaxLength = parseInt($("#cIdRFC1").attr("maxlength"),10);

				bValid = bValid && checkRequerido( IdRFC1, "RFC" );
				bValid = bValid && checkLength( IdRFC1, "este segmento del RFC ", iMaxLength, iMaxLength );
				bValid = bValid && checkRequerido( IdRFC2, "RFC" );
				bValid = bValid && checkLength( IdRFC2, "este segmento del RFC ", 6, 6 );
				bValid = bValid && checkRequerido( IdRFC3, "RFC" );
				bValid = bValid && checkLength( IdRFC3, "este segmento del RFC ", 3, 3 );
				if ( $("#lbCURP").css("visibility") == "visible" )
				{
					NombrePM.val("");
					bValid = bValid && checkRequerido( CURP, "CURP" );
					bValid = bValid && checkLength( CURP, "CURP", 18, 18 );
					bValid = bValid && checkRequerido( ApellidoPaterno, "Apellido Paterno del Beneficiario" );
					bValid = bValid && checkRequerido( ApellidoMaterno, "Apellido Materno del Beneficiario" );
					bValid = bValid && checkRequerido( NombrePF, "Nombre del Beneficiario" );
				}
				else
				{
					$("#dCURP").val("");
					$("#dApellidoPaterno").val("");
					$("#dApellidoMaterno").val("");
					NombrePF.val("");
					bValid = bValid && checkRequerido( NombrePM, "Persona Moral" );
				}
				if (!bValid) return;
				$("#dRFC").val(IdRFC1.val() + IdRFC2.val() + IdRFC3.val());
				$("#dNombre").val(NombrePF.val() + NombrePM.val());


				$("#aTab1").click();

				bValid = bValid && checkRequerido( CalleFiscal, "Calle Fiscal" );
				bValid = bValid && checkRequerido( NoDomicilioFiscal, "No de Calle Fiscal" );
				//bValid = bValid && checkRequerido( NoInteriorDomicilioFiscal, "No.Inteior de Calle Fiscal" );
				//bValid = bValid && checkRequerido( OtrosDatosFiscal, "Otras señas Fiscal" );
				bValid = bValid && checkRequerido( ColoniaFiscal, "Colonia Fiscal" );
				bValid = bValid && checkRequerido( CodigoPostalFiscal, "Código Postal Fiscal" );
				bValid = bValid && checkLength( CodigoPostalFiscal, "Código Postal Fiscal", 5, 5 );
				bValid = bValid && checkRequerido( TelefonoFiscal, "Teléfono Fiscal" );
				//bValid = bValid && checkRequerido( FaxFiscal, "Fax Fiscal" );
				bValid = bValid && checkRequerido( EMailFiscal, "E-Mail Fiscal" );
				bValid = bValid && checkRegexp( EMailFiscal, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "Formato del E-Mail Fiscal inválido. Ejemplo: ui@gmail.com" );

				if (!bValid) return;

				$("#aTab2").click();
				bValid = bValid && checkRequerido( CalleActual, "Calle Actual" );
				bValid = bValid && checkRequerido( NoDomicilioActual, "No de Calle Actual" );
				//bValid = bValid && checkRequerido( NoInteriorDomicilioActual, "No.Inteior de Calle Actual" );
				//bValid = bValid && checkRequerido( OtrosDatosActual, "Otras señas Actual" );
				bValid = bValid && checkRequerido( ColoniaActual, "Colonia Actual" );
				bValid = bValid && checkRequerido( CodigoPostalActual, "Código Postal Actual" );
				bValid = bValid && checkLength( CodigoPostalActual, "Código Postal Actual", 5, 5 );
				bValid = bValid && checkRequerido( TelefonoActual, "Teléfono Actual" );
				//bValid = bValid && checkRequerido( FaxActual, "Fax Actual" );
				bValid = bValid && checkRequerido( EMailActual, "E-Mail Actual" );
				bValid = bValid && checkRegexp( EMailActual, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "Formato del E-Mail Actual inválido. Ejemplo: ui@gmail.com" );

				if (!bValid) return;

				$("#aTab3").click();
				bValid = bValid && checkRequerido( APaternoApoderado, "A.Paterno del Apoderado" );
				bValid = bValid && checkRequerido( AMaternoApoderado, "A.Materno del Apoderado" );
				bValid = bValid && checkRequerido( NombreApoderado, "Nombre del Apoderado" );
				bValid = bValid && checkRequerido( TelefonoApoderado, "Teléfono del Apoderado" );
				//bValid = bValid && checkRequerido( FaxApoderado, "Fax del Apoderado" );
				bValid = bValid && checkRequerido( EMailApoderado, "E-Mail del Apoderado" );
				bValid = bValid && checkRegexp( EMailApoderado, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "Formato del E-Mail Apoderado inválido. Ejemplo: ui@gmail.com" );
				

				if (!bValid) return;
				
				if (gsOperacion == 'A')
				{
					$("#nEnviadoSICOP").val(0);
					$("#cBeneficiarioStatus").val(1);
					$('#cRFCValido').val(1);

					var dHoy=new Date();
					$("#fBeneficiario").val(dHoy.getFullYear() + "" + cerosIzq("" +(dHoy.getMonth() + 1),2) + "" + cerosIzq("" +dHoy.getDate(),2) + " " + cerosIzq("" +dHoy.getHours(),2) + ":" + cerosIzq("" +dHoy.getMinutes(),2));

					queryFormPost("tBeneficiarioCreate", {async: false});
					queryFormPost("BeneficiariosRead", {async: false});
					szTemp = 'C' + cerosIzq($("#cBeneficiario").val(),5 );
					$("#CBEN").val(szTemp);
					queryFormPost("tBeneficiariosCBENUpdate", {async: false}); 

					$("#cBeneficiario").attr("disabled", false);
					
					$('#tblTipoRFC').dataTable().fnSetColumnVis( 0, true ); 
					queryFormPost("tBeneficiarioTipoRFCDelete,tBeneficiarioTipoRFCCreate");

					location.href = 'BeneficiariosGrid.jsp';
				}


				});

			$("#pbCancelar")
				.button()
				.click(function() {
					location.href = 'BeneficiariosGrid.jsp';
					
				});

		});


		function fnEsperaIns()
		{

			var oSettings = oTable.fnSettings();
			var aTrs = oTable.fnGetNodes();
			oSettings.sAjaxSource = null;
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				oTable.fnUpdate( $("#cBeneficiario").val(), i, 4); 
			}
			
		}
		
		function CalculaCLABE()
		{
			$("#cBancoH").attr("disabled", false);
			$("#cPlazaH").attr("disabled", false);
			$("#dCuentaBancariaH").attr("disabled", false);
			$("#dDigitoVerificadorH").attr("disabled", false);
			$("#cStatusCuentaH").attr("disabled", false);
			$("#dBancoH").attr("disabled", false);
			$("#dSucursalH").attr("disabled", false);
			$("#nBCBEnviadoSICOPH").attr("disabled", false);
					
			$('#cBancoH').val($('#cbBanco').val());
			$('#cPlazaH').val($('#txtPlaza').val());
			$('#dCuentaBancariaH').val($('#txtCuentaBancaria').val());
			$('#dDigitoVerificadorH').val($('#txtDigitoVerificador').val());
			$('#dBancoH').val($('#cbBanco option:selected').text());
			$('#dSucursalH').val($('#txtSucursal').val());
						
			var  n=0;
			var iRes=0;
			var arrFactor=new Array(3,7,1,3,7,1,3,7,1,3,7,1,3,7,1,3,7); 
			var arrResult=new Array(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)
			var szCLABE = $("#cbBanco").val();
			
			$("#txtPlaza").val(cerosIzq($("#txtPlaza").val(),3 ));
			$("#txtCuentaBancaria").val(cerosIzq($("#txtCuentaBancaria").val(),11 ));
			szCLABE += $("#txtPlaza").val() + $("#txtCuentaBancaria").val();
			
			for (n=0 ;n<arrFactor.length ; n++)
			{
				arrResult[n] = (arrFactor[n] * parseInt(szCLABE.charAt(n),10)) % 10;
				iRes += arrResult[n];
			}
			iRes = iRes % 10;
			iRes = (10 - iRes) % 10;
			szCLABE = szCLABE + iRes;
			return szCLABE
		}
		
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form>
		<input id="cBeneficiario" name="cBeneficiario" type="hidden" size="10">
		<input id="nEnviadoSICOP" name="nEnviadoSICOP" type="hidden" size="10">
		<input id="fBeneficiario" name="fBeneficiario" type="hidden" size="10">
		<input id="cUsuario" name="cUsuario" type="hidden" size="10">
		<input id="cUsuarioModifico" name="cUsuarioModifico" type="hidden" size="10">
		<input id="fCuentaModifico" name="fCuentaModifico" type="hidden" size="10">
		<input id="cBeneficiarioStatus" name="cBeneficiarioStatus" type="hidden" size="10">
		<input id="cUsuarioModifico" name="cUsuarioModifico" type="hidden" size="10">
		<input id="fCuentaModifico" name="fCuentaModifico" type="hidden" size="10">
		
		<input type='hidden' id='cBancoH' name='cBancoH' value=''  >
		<input type='hidden' id='cPlazaH' name='cPlazaH' value=''  >
		<input type='hidden' id='dCuentaBancariaH' name='dCuentaBancariaH' value=''  >
		<input type='hidden' id='dDigitoVerificadorH' name='dDigitoVerificadorH' value=''  >
		<input type='hidden' id='cStatusCuentaH' name='cStatusCuentaH' value='1'  >
		<input type='hidden' id='dBancoH' name='dBancoH' value=''  > 
		<input type='hidden' id='dSucursalH' name='dSucursalH' value=''  >
		<input type='hidden' id='nBCBEnviadoSICOPH' name='nBCBEnviadoSICOPH' value='0'  >
		
		<div id="container" class="container">
			<h1><img id="imgPlayStop" src="imagenes/wait24trans.gif">Beneficiario <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<table border="0" align="center" width="100%">
				<tr>
					<td align="right">Tipo Persona:</td>
					<td width=250px>
						<select id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC">
							<option value="2">A</option>
							<option value="3">B</option>
							<option value="4">C</option>
							<option value="1" selected>--</option>
						</select>

					</td>
					<td id="tdCBEN" colspan="4" align="right" style="visibility:hidden">
						C&oacute;digo SICOP: <input id="CBEN" name="CBEN" type="text" size="10" maxlength="5" readonly style="background: #f0f0f0">
					</td>
				</tr>
				<tr>
					<td align="right">RFC:</td>
					<td>
						<input id="dRFC" name="dRFC" type="hidden" size="13" maxlength="13" style='text-transform:uppercase;'>
						<input id="cIdRFC1" name="cIdRFC1" type="text" size="4" maxlength="3" onkeypress="Validaciones(this,0);" style='text-transform:uppercase;'>
						-
						<input id="cIdRFC2" name="cIdRFC2" type="text" size="5" maxlength="6" onkeypress="Validaciones(this,2)">
						-
						<input id="cIdRFC3" name="cIdRFC3" type="text" size="3" maxlength="3" onkeypress="Validaciones(this,13)" style='text-transform:uppercase;'>
					</td>
					<td colspan="2" >
						<input type="checkbox" id="chkRFCValido" name="chkRFCValido" style="visibility: hidden">
						<select id="cRFCValido" name="cRFCValido" style="visibility: hidden">
							<option value="0" selected>No</option>
							<option value="1">Si</option>
						</select>
					</td>
					<td align="right" ><label id="lbCURP" style="visibility: hidden">CURP:</label> </td>
					<td>
						<input id="dCURP" name="dCURP" type="text" size="23" maxlength="18" style="visibility: hidden" onkeypress="Validaciones(this,13)" style='text-transform:uppercase;'>
					</td>
				</tr>
				<tr id="trPersonaFisica" style="visibility: hidden">
					<td align="right">A.Paterno:</td>
					<td>
						<input id="dApellidoPaterno" name="dApellidoPaterno" type="text" size="24" maxlength="60" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
					</td>
					<td align="right">A.Materno:</td>
					<td>
						<input id="dApellidoMaterno" name="dApellidoMaterno" type="text" size="13" maxlength="60" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
					</td>
					<td align="right">Nombre:</td>
					<td>
						<input id="dNombrePF" name="dNombrePF" type="text" size="23" maxlength="400" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
					</td>
				</tr>
				<tr id="trPersonaMoral" style="visibility: visible">
					<td align="right">Persona Moral:</td>
					<td colspan="5">
						<input id="dNombre" name="dNombre" type="hidden" size="106" maxlength="400" style='text-transform:uppercase;'/>
						<input id="dNombrePM" name="dNombrePM" type="text" size="106" maxlength="400" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
					</td>
				</tr>
				<tr>
					<td align="right" colspan="2">
						<select id="cExtranjero" name="cExtranjero" style="visibility: hidden">
							<option value="0" selected>No</option>
							<option value="1">Si</option>
						</select>
					</td>
					<td colspan="4" >
						<input type="checkbox" id="chkExtranjero" name="chkExtranjero" />Es beneficiario extranjero
					</td>
				</tr>
			</table>

			<br/>

			<div class="tabs">
				<ul>
					<li><a id="aTab0" href="#tabs-0">Tipo de RFC</a></li>
					<li><a id="aTab1" href="#tabs-1">Direcci&oacute;n Fiscal</a></li>
					<li><a id="aTab2" href="#tabs-2">Direcci&oacute;n Actual</a></li>
					<li><a id="aTab3" href="#tabs-3">Contacto/Apoderado</a></li>
					<li><a id="aTab4" href="#accounts-contain">Cuentas Bancarias</a></li>
				</ul>
				<div id="tabs-0" >
					<table >
						<tr>
							<td width="330px">
								<table id="tblTipoRFCDisp" class="display"  >
						            <thead>
						                <tr>
						                	<th>ID</th>
						                    <th>Tipo RFC</th>
						                </tr>
						            </thead>
						        </table>
							</td>
							<td>
								&nbsp;<input type="button" id="pbPalla" value=">>"/>&nbsp;<br><br>
								&nbsp;<input type="button" id="pbPaca" value="<<"/>&nbsp;
							</td>
							<td  width="330px">
								<table id="tblTipoRFC" class="display"  >
						            <thead>
						                <tr>
						                	<th>ID</th>
						                    <th>Tipo RFC</th>
						                </tr>
						            </thead>
						        </table>
							</td>
						</tr>
					</table>
					<label style="POSITION: relative; TOP:-195px; LEFT:20px">Disponibles</label>
					<label style="POSITION: relative; TOP:-195px; LEFT:365px">Seleccionados</label>
					
				</div>
				<div id="tabs-1">
					<table border="0">
						<tr>
							<td align="right">Entidad Federativa:</td>
							<td>
								<select id="cEstadoFiscal" name="cEstadoFiscal" style="width: 20em;">
									<option value="1">A</option>
									<option value="2">B</option>
									<option value="3">C</option>
									<option value="1" selected>--</option>
								</select>
							</td>
							<td align="right">&nbsp;</td>
							<td colspan="3" align="right">
								<input id="pbCopiaDirActual" name="pbCopiaDirActual" type="button" value="Copiar datos a Dir. Actual" />
							</td>
						</tr>
						<tr>
							<td align="right">Calle:</td>
							<td align="left">
								<input id="dCalleFiscal" name="dCalleFiscal" type="text" size="30" maxlength="60" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
							<td align="right">No.:</td>
							<td>
								<input id="dNoDomicilioFiscal" name="dNoDomicilioFiscal" type="text" size="6" maxlength="10"  onkeypress="Validaciones(this,3)" style='text-transform:uppercase;'>
							</td>
							<td align="right">No.Interior:</td>
							<td>
								<input id="dNoInteriorDomicilioFiscal" name="dNoInteriorDomicilioFiscal" type="text" size="6" maxlength="10"  onkeypress="Validaciones(this,3)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Otras se&ntilde;as:</td>
							<td colspan="5">
								<input id="dOtrosDatosFiscal" name="dOtrosDatosFiscal" type="text" size="80" maxlength="200" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Colonia o Manzana:</td>
							<td colspan="5">
								<input id="dColoniaFiscal" name="dColoniaFiscal" type="text" size="80" maxlength="50" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Delegaci&oacute;n o Municipio:</td>
							<td>
								<select id="cMunicipioFiscal" name="cMunicipioFiscal" style="width: 20em;">
								</select>

							</td>
							<td colspan="2" align="right">
								&nbsp;
							</td>
							<td align="right">C&oacute;digo Postal:</td>
							<td>
								<input id="dCodigoPostalFiscal" name="dCodigoPostalFiscal" type="text" size="6" maxlength="5" onkeypress="Validaciones(this,2)">
							</td>
						</tr>
						<tr>
							<td align="right">Tel&eacute;fono:</td>
							<td align="left">
								<input id="dTelefonoFiscal" name="dTelefonoFiscal" type="text" size="15" maxlength="20" onkeypress="Validaciones(this,8)" style='text-transform:uppercase;'>
							</td>
							<td align="right">Fax:</td>
							<td align="left" colspan="3">
								<input id="dFaxFiscal" name="dFaxFiscal" type="text" size="15" maxlength="20" onkeypress="Validaciones(this,8)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">E-Mail:</td>
							<td colspan="5">
								<input id="dEMailFiscal" name="dEMailFiscal" type="text" size="80" maxlength="60" onkeypress="Validaciones(this,15)" style='text-transform:lowercase;'>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-2">
					<table border="0">
						<tr>
							<td align="right">Entidad Federativa:</td>
							<td align="left">
								<select id="cEstadoActual" name="cEstadoActual" style="width: 20em;">
									<option value="1">A</option>
									<option value="2">B</option>
									<option value="3">C</option>
									<option value="1" selected>--</option>
								</select>
							</td>
							<td align="right">&nbsp;</td>
							<td colspan="3" align="right">
								<input id="pbCopiaDirFiscal" name="pbCopiaDirFiscal" type="button" value="Copiar datos a Dir. Fiscal" />
							</td>
						</tr>
						<tr>
							<td align="right">Calle:</td>
							<td align="left">
								<input id="dCalleActual" name="dCalleActual" type="text" size="30" maxlength="60" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
							<td align="right">No.:</td>
							<td>
								<input id="dNoDomicilioActual" name="dNoDomicilioActual" type="text" size="6" maxlength="10"  onkeypress="Validaciones(this,3)" style='text-transform:uppercase;'>
							</td>
							<td align="right">No.Interior:</td>
							<td>
								<input id="dNoInteriorDomicilioActual" name="dNoInteriorDomicilioActual" type="text" size="6" maxlength="10"  onkeypress="Validaciones(this,3)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Otras se&ntilde;as:</td>
							<td colspan="5">
								<input id="dOtrosDatosActual" name="dOtrosDatosActual" type="text" size="80" maxlength="200" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Colonia o Manzana:</td>
							<td colspan="5">
								<input id="dColoniaActual" name="dColoniaActual" type="text" size="80" maxlength="60" onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Delegaci&oacute;n o Municipio:</td>
							<td align="left">
								<select id="cMunicipioActual" name="cMunicipioActual" style="width: 20em;">
								</select>
							</td>
							<td colspan="2" align="right">&nbsp;</td>
							<td align="right">C&oacute;digo Postal:</td>
							<td>
								<input id="dCodigoPostalActual" name="dCodigoPostalActual" type="text" size="6" maxlength="5" onkeypress="Validaciones(this,2)">
							</td>
						</tr>
						<tr>
							<td align="right">Tel&eacute;fono:</td>
							<td align="left">
								<input id="dTelefonoActual" name="dTelefonoActual" type="text" size="15" maxlength="20" onkeypress="Validaciones(this,8)" style='text-transform:uppercase;'>
							</td>
							<td align="right">Fax:</td>
							<td align="left" colspan="3">
								<input id="dFaxActual" name="dFaxActual" type="text" size="15" maxlength="20" onkeypress="Validaciones(this,8)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">E-Mail:</td>
							<td colspan="5">
								<input id="dEMailActual" name="dEMailActual" type="text" size="80" maxlength="60" onkeypress="Validaciones(this,15)" style='text-transform:lowercase;'>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-3" style="height: 207px">
					<table border="0" align="center">
						<tr>
							<td align="right">A.Paterno:</td>
							<td>
								<input id="dAPaternoApoderado" name="dAPaternoApoderado" type="text" size="20" maxlength="60"  onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
							<td align="right">A.Materno:</td>
							<td>
								<input id="dAMaternoApoderado" name="dAMaternoApoderado" type="text" size="13" maxlength="60"  onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
							<td align="right">Nombre:</td>
							<td>
								<input id="dNombreApoderado" name="dNombreApoderado" type="text" size="20" maxlength="60"  onkeypress="Validaciones(this,16)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">Tel&eacute;fono:</td>
							<td align="left">
								<input id="dTelefonoApoderado" name="dTelefonoApoderado" type="text" size="15" maxlength="20" onkeypress="Validaciones(this,8)" style='text-transform:uppercase;'>
							</td>
							<td align="right">Fax:</td>
							<td align="left" colspan="3">
								<input id="dFaxApoderado" name="dFaxApoderado" type="text" size="15" maxlength="15" onkeypress="Validaciones(this,8)" style='text-transform:uppercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">E-Mail:</td>
							<td align="left" colspan="5">
								<input id="dEMailApoderado" name="dEMailApoderado" type="text" size="80" maxlength="60" onkeypress="Validaciones(this,15)" style='text-transform:lowercase;'>
							</td>
						</tr>
						<tr>
							<td align="right">No. Oficio Poder Legal:</td>
							<td align="left" colspan="5">
								<input id="dNoOficioPoderLegal" name="dNoOficioPoderLegal" type="text" size="40" maxlength="40" onkeypress="Validaciones(this,4)">
							</td>
						</tr>
					</table>
				</div>
				<div id="accounts-contain" >
					<table id="tblCuentasBancarias"  width="750px">
						<thead>
							<tr>
								<th width="50px">cBanco</th>
								<th width="120px">Banco</th>
								<th width="40px">Plaza</th>
								<th width="115px">No.Cuenta</th>
								<th width="35px">Dígito</th>
								<th width="60px">Sucursal</th>
								<th width="60px">cStatus</th>
								<th width="70px">Status</th>
								<th width="30px">Enviado</th>
							</tr>
						</thead>
					</table>
					
					<input type="button" id="pbCuentasBancarias"  style="visibility: hidden" value="Agregar Cuenta Bancaria"/>
				</div>
			</div>
			<label class="validateTips ui-state-error" ></label></br>
			<table width="100%" border="0" >
				<tr>
					<td width="33%">&nbsp;</td>
					<td width="33%" align="center">
						<input type="button" id="pbAceptar" value="Aceptar"/>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbCancelar" value="Cancelar"/>
					</td>
					<td width="33%" align="right">
						&nbsp;
					</td>
				</tr>
			</table>
		</div>


		<div id="dialog-form" title="Captura de Cuentas Bancarias">
			
			<fieldset>
				<table cellpadding="2" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="right">Beneficiario:</td>
						<td colspan=3 >
							<input type="text" name="txtNombreProveerdor" id="txtNombreProveerdor" readonly style=" width:100%; background: #f0f0f0" />
						</td>						
					</tr>
					<tr>
						<td align="right">Banco:</td>
						<td colspan="3" > <select id="cbBanco" name="cbBanco" ></select> 
							&nbsp;&nbsp;Plaza:<input type="text" name="txtPlaza" id="txtPlaza" size="3" maxlength="3" onkeypress="Validaciones(this,2)" />
							&nbsp;&nbsp;No.Cuenta:<input type="text" name="txtCuentaBancaria" id="txtCuentaBancaria" size="12" maxlength="11"  onkeypress="Validaciones(this,2)"/>
							&nbsp;&nbsp;Dígito de Control:<input type="text" name="txtDigitoVerificador" id="txtDigitoVerificador" align="middle" size="1" readonly style="background: #f0f0f0"/>
						</td>
					</tr>
					<tr >
						<td  align="right">Sucursal:</td>
						<td  width="200px"><input type="text" name="txtSucursal" id="txtSucursal" align="middle" size="4" maxlength="4" onkeypress="Validaciones(this,2)"/></td>
						<td  align="right"  width="130px">CLABE:</td>
						<td  width="250px"><input type="text" name="txtCLABE" id="txtCLABE" readonly style="WIDTH:100%; background: #f0f0f0"/></td>
					</tr>

				</table>


			</fieldset>
		</div>


	</form>
	</body>
</html>