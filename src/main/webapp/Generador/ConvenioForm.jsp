<%@page language="java" pageEncoding="UTF-8"  import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
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
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Convenio</title>

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
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
	
		var gsOperacion ='<%=request.getParameter("Op")%>';
		var szIdConvenio = '<%=request.getParameter("Re1")%>';
		var szIdContrato = '<%=request.getParameter("Re2")%>';
		var szIdPrestamo = '<%=request.getParameter("Re3")%>';


		function CambiaFormatoFecha(objParam)
		{
			var szFecha = objParam.val();
			if (szFecha.length < 10) return;
			szFecha = szFecha.substr(0,10);
			var res = szFecha.split("-");
			objParam.val(res[2] + "/" + res[1] + "/" + res[0] );
		}

		$(document).ready(function() {
			
			$( "#id_prestamo" )
				.change(function() {

					querySelectPost("vNumeroContratoCvRead", "id_Contrato", {async: false});
					
					$("#id_Contrato").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
					fnOrdenaCombo("#id_Contrato");
					
					$("#id_Contrato").change();
				});
				
			$( "#id_Contrato" )
                .change(function() {
                
                var dtTemp = $('#FechaFirma').val();
                
    			$('#FechaTerminacion').val("");
       			$('#FechaFirmaContrato').val("");
  				$('#ImporteOriginal').val("");
 				$('#ImporteTotal').val("");
 				$('#EjercicioFiscal').val(""); 							
  				queryFormPost("ContratosConveniosRead", {async: false});
				$('.currency').formatCurrency();
				
				CambiaFormatoFecha($("#FechaTerminacion"));
				CambiaFormatoFecha($("#FechaFirmaContrato"));
				
				$('#FechaFirma').val(dtTemp);
            });
                        
            $( "#chkUsaAutorizacion" )
                .change(function() {
                
                if($(this).is(':checked'))
                {										 // checked
                	$("#ImporteAumentoAutorizado").val($("#importe").val());
					$("#importe").val("");
	                $("#NumeroOficioOFI").css("background", "white");
	                $("#NumeroOficioOFI").removeAttr('disabled');
	                $("#ImporteAumentoAutorizado").css("background", "white");
	                $("#ImporteAumentoAutorizado").removeAttr('disabled');                
	                $("#importe").css("background", "#f0f0f0");
	                $("#importe").attr("disabled", true);
	                $("#NumeroOficioOFI").focus();
				}
				else
				{										// unchecked
					$("#NumeroOficioOFI").val("");
					$("#importe").val($("#ImporteAumentoAutorizado").val());
					$("#ImporteAumentoAutorizado").val("");
	                $("#NumeroOficioOFI").css("background", "#f0f0f0");
	                $("#NumeroOficioOFI").attr("disabled", true);
	                $("#ImporteAumentoAutorizado").css("background", "#f0f0f0");
	                $("#ImporteAumentoAutorizado").attr("disabled", true);                
	                $("#importe").css("background", "white");
	                $("#importe").removeAttr('disabled');
	                $("#importe").focus();
				}
				
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
 
 			$('.currency').blur(function()	{
				$('.currency').formatCurrency();
				});

			// Inicializaciones CRUD
			
			querySelectPost("PrestamoNumNomRead", "id_prestamo", {async: false});	
			
			$("#id_prestamo").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			fnOrdenaCombo("#id_prestamo");		
			
			$("#id_Contrato").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
									
			if (gsOperacion == 'A')
			{			
				$("#lbOperacion").text(" (Agregar)");
				
				$("#FechaFirma").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaTerminoConvenio").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				
				$("#NumeroConvenio").focus();
			
			}
									
			if (gsOperacion == 'D' || gsOperacion == 'B')
			{
				$("#lbOperacion").text(" (Desplegar)");
				$("#pbAceptar").css("visibility","hidden");
				$("#pbCancelar").val("Regresar");
				
				$("#id_convenioModifica").val(szIdConvenio);
				$("#id_prestamo").val(szIdPrestamo);
				$("#id_prestamo").change();
					
				$("#id_Contrato").val(szIdContrato);				
							
				queryFormPost("ConveniosRead", {async: false});
				
				queryFormPost("ContratosConveniosRead", {async: false});
				
				if ($("#UsaAutorizacion").val() == 0)
				{
					$("#chkUsaAutorizacion").attr('checked', false);
					$("#NumeroOficioOFI").css("background", "#f0f0f0");
	                $("#NumeroOficioOFI").attr("disabled", true);
	                $("#ImporteAumentoAutorizado").css("background", "#f0f0f0");
	                $("#ImporteAumentoAutorizado").attr("disabled", true); 
				}
				else
				{
					$("#ImporteAumentoAutorizado").val($("#importe").val());
					$("#importe").val("");
					$("#chkUsaAutorizacion").attr('checked', true);
					$("#NumeroOficioOFI").css("background", "white");
	                $("#NumeroOficioOFI").removeAttr('disabled');
	                $("#ImporteAumentoAutorizado").css("background", "white");
	                $("#ImporteAumentoAutorizado").removeAttr('disabled');      
				}	
				
				$('.currency').formatCurrency();

				$("input").attr("readonly", true);
				$("input").css("background", "#f0f0f0");

				$("input").attr("disabled", true);

				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
								
				CambiaFormatoFecha($("#FechaTerminacion"));
				CambiaFormatoFecha($("#FechaFirmaContrato"));
				CambiaFormatoFecha($("#FechaTerminoConvenio"));
				CambiaFormatoFecha($("#FechaFirma"));
			}
			
			if ( gsOperacion == 'B')
			{
				$("#lbOperacion").text(" (Borrar)");
				$("#pbAceptar").css("visibility","visible");
				$("#pbCancelar").val("Cancelar");
			}
			
			if ( gsOperacion == 'C')
			{
				$("#lbOperacion").text(" (Cambiar)");
		
				$("#id_prestamo").val(szIdPrestamo);
				$("#id_prestamo").change();   
				$("#id_Contrato").val(szIdContrato);
				$("#id_convenioModifica").val(szIdConvenio);
											
				queryFormPost("ConveniosRead", {async: false});
				queryFormPost("ContratosConveniosRead", {async: false});
				
				var szTempDub = $("#importe").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				szTempDub = fnMontoNegativo(szTempDub);
							
				var szTempTotal = $("#ImporteTotal").val();
				szTempTotal = (szTempTotal.replace("$","")).replace("$","");	
				szTempTotal = (szTempTotal.replace(",","")).replace(",","");
				szTempTotal = fnMontoNegativo(szTempTotal);
				$("#ImporteTotal").val(parseFloat(szTempTotal) - parseFloat(szTempDub));
				
				if ($("#UsaAutorizacion").val() == 0)
				{
					$("#chkUsaAutorizacion").attr('checked', false);
					$("#NumeroOficioOFI").css("background", "#f0f0f0");
	                $("#NumeroOficioOFI").attr("disabled", true);
	                $("#ImporteAumentoAutorizado").css("background", "#f0f0f0");
	                $("#ImporteAumentoAutorizado").attr("disabled", true); 
				}
				else
				{
					$("#ImporteAumentoAutorizado").val($("#importe").val());
					$("#importe").val("");
					$("#importe").css("background", "#f0f0f0");
	                $("#importe").attr("disabled", true);
					$("#chkUsaAutorizacion").attr('checked', true);
					$("#NumeroOficioOFI").css("background", "white");
	                $("#NumeroOficioOFI").removeAttr('disabled');
	                $("#ImporteAumentoAutorizado").css("background", "white");
	                $("#ImporteAumentoAutorizado").removeAttr('disabled');      
				}					
				
				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
					
				CambiaFormatoFecha($("#FechaFirma"));
				CambiaFormatoFecha($("#FechaTerminoConvenio"));
				CambiaFormatoFecha($("#FechaFirmaContrato"));
				CambiaFormatoFecha($("#FechaTerminacion"));	
				
				$("#FechaFirma").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaTerminoConvenio").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
			
				$("#NumeroConvenio").focus();			
			}
			
			$("#imgPlayStop").css("visibility", "hidden");
			
			$('.currency').formatCurrency();
		});
		
		function formSubmited() 
		{
        	alert("Convenio enviado!");
        }
            
        function CambiaFormatoFecha(objParam)
		{
			var szFecha = objParam.val();
			if (szFecha.length < 10) return;
			szFecha = szFecha.substr(0,10);
			var res = szFecha.split("-");
			objParam.val(res[2] + "/" + res[1] + "/" + res[0] );
		}    
		
		</script>

	<script type="text/javascript" charset="utf-8">
		
		$(function() {

			$( "#dialog:ui-dialog" ).dialog( "destroy" );

			var id_Contrato 	= $( "#id_Contrato" ),
				EjercicioFiscal = $( "#EjercicioFiscal" ),
				importe 		= $( "#importe" ),
				ImporteAumentoAutorizado = $( "#ImporteAumentoAutorizado" ),
				NumeroConvenio 	= $("#NumeroConvenio"),
				FechaFirma 		= $("#FechaFirma"),
				FechaTerminoConvenio = $("#FechaTerminoConvenio"),
			
				allFields 		= $( [] ).add( id_Contrato ).add( EjercicioFiscal ).add( importe )
										.add( NumeroConvenio ).add( FechaFirma ),
				tips 			= $( ".validateTips" );			



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

			function checkRegexp( o, regexp, n ) {
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg( n );
					return false;
				} else {
					return true;
				}
			}
			
			
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
					};
				});
				$("input").each(function (){
					var szStyle = "" + $(this).attr('style');
					if (szStyle.indexOf("lowercase")>1)
					{
						$(this).val($(this).val().toLowerCase());  
					}
				});
				
				var porcentaje = 0;					// valida importes

				var szTempDub = $("#importe").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");	
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				szTempDub = fnMontoNegativo(szTempDub);
				
				var szTempOri = $("#ImporteOriginal").val();
				szTempOri = (szTempOri.replace("$","")).replace("$","");	
				szTempOri = (szTempOri.replace(",","")).replace(",","");
				
				var szTempAumento = $("#ImporteAumentoAutorizado").val();
				szTempAumento = (szTempAumento.replace("$","")).replace("$","");	
				szTempAumento = (szTempAumento.replace(",","")).replace(",","");
				szTempAumento = fnMontoNegativo(szTempAumento);
				
				var szTempTotal = $("#ImporteTotal").val();
				szTempTotal = (szTempTotal.replace("$","")).replace("$","");	
				szTempTotal = (szTempTotal.replace(",","")).replace(",","");
				szTempTotal = fnMontoNegativo(szTempTotal);
				
				var nMonto = 100;

				bValid = bValid && checkRequerido( NumeroConvenio, "Número de Convenio ");
				//bValid = bValid && checkRequerido( EjercicioFiscal, "Ejercicio Fiscal");
				if (!bValid) return;
				if ($("#id_prestamo").val() == 0)
				{
					alert("El Prestamo es un dato requerido.");
					$("#id_prestamo").focus();
					return;				
				}
				
				if ($("#id_Contrato").val() == 0)
				{
					alert("El Número de Contrato es un dato requerido.");
					$("#id_Contrato").focus();
					return;				
				}				
				
				bValid = bValid && checkRequerido( FechaFirma, "Fecha de Firma del Convenio ");
				if (!bValid) return;
				
				if($("#chkUsaAutorizacion").is(':checked'))
				{
					if ($("#FechaTerminoConvenio").val()=="" && Number(szTempAumento) == 0)
					{
						alert("La fecha de Término del Convenio o el Importe Convenio Autorizado con IVA es un dato requerido.");
						$("#FechaTerminoConvenio").focus();
						return;
					}
					//	bValid = bValid && checkRequerido( ImporteAumentoAutorizado, "Importe Convenio Autorizado con IVA ");
				}
				else
				{					
					if ($("#FechaTerminoConvenio").val()=="" && Number(szTempDub) == 0)
					{
						alert("La fecha de Término del Convenio o el Importe Convenio con IVA es un dato requerido.");
						$("#FechaTerminoConvenio").focus();
						return;
					}
				}			
				
				
				var szFecha1 = $("#FechaFirma").val();
				var szFecha2 = $("#FechaFirmaContrato").val();
				
				szFecha1 = szFecha1.substr(6,4) + "/" + szFecha1.substr(3,2) + "/" + szFecha1.substr(0,2);  
				szFecha2 = szFecha2.substr(6,4) + "/" + szFecha2.substr(3,2) + "/" + szFecha2.substr(0,2); 
				
				var dtf = new Date(szFecha1);
				var dtfc = new Date(szFecha2);
	
				if (DateDiff(dtfc, dtf) >= 0)
				{
					alert("La Fecha de Firma del Convenio debe ser mayor a la Fecha de Firma del Contrato.");
					$("#FechaFirma").focus();
					return;	
				}
				
/*			Eliminado por que quieren la fecha de ejercicio fiscal sacada por la fecha de ejercicio fiscal  del contrato
				if (Number($("#EjercicioFiscal").val()) <= 2010)
				{
					alert("El Ejercicio Fiscal debe ser un año mayor a 2010.");
					$("#EjercicioFiscal").focus();
					return;
				}
*/				
				if ($("#FechaTerminoConvenio").val() == "" )
				{
					$("#FechaTerminoConvenio").val($("#FechaTerminacion").val()); 
				}
				
				nMonto = parseFloat(szTempTotal) - parseFloat(szTempOri);
				if (gsOperacion != 'B')
				{
					if($("#chkUsaAutorizacion").is(':checked'))
					{
						if ($.trim( $("#NumeroOficioOFI").val()) == "")
						{
							alert("El Número Oficio OFI es un dato requerido.");
							$("#NumeroOficioOFI").focus();
							return;					
						}		
						porcentaje = ((parseFloat(szTempAumento)+nMonto) * 100) / parseFloat(szTempOri);
						if ( porcentaje > 20)
						{
							alert("Error ....!\r\rEl Importe de Aumento Total de Convenios capturados representa el " + parseInt(porcentaje,10) + "% adicional al Importe Original del Contrato y no es posible aumentar ese valor.");
							$("#ImporteAumentoAutorizado").focus();
							return;
						}
						$("#importe").val(szTempAumento);	
						$("#UsaAutorizacion").val("1");		
					}
					else
					{	
						porcentaje = ((parseFloat(szTempDub)+nMonto) * 100) / parseFloat(szTempOri);
						
						if ( porcentaje > 20)
						{
							alert("Error ....!\r\rEl Importe de Aumento Total de Convenios capturados representa el " + parseInt(porcentaje,10) + "% adicional al Importe Original del Contrato y no es posible aumentar ese valor.");
							$("#importe").focus();
							return;					
						}
						if ( porcentaje > 15)
						{
							alert("Error ....!\r\rEl Importe de Aumento Total de Convenios capturados representa el " + parseInt(porcentaje,10) + "% adicional al Importe Original del Contrato, es necesario una autorización con su Número de Oficio OFI.");
							$("#importe").focus();
							return;
						}	
						$("#UsaAutorizacion").val("0");	
						
						$("#importe").val(szTempDub);
					}
				}
				
				if (gsOperacion == 'A')
				{
					szWhere = " NumeroConvenio = '" + $("#NumeroConvenio").val() + "' ";
					szTabla = "DNOCONVENIO";                                                                                     
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{
							if (j.length>0)
							{
								alert("Error. \rYa existe ese Número de Convenio.");
								$("#NumeroConvenio").focus();
								return;
							}
							else
							{
								$("#NumeroOficioOFI").removeAttr('disabled');
								$("#importe").removeAttr('disabled');
								
								queryFormPost("dConveniosCreate,dCategoriaInversion_ImporteTotalUpdate", {async: false});
								alert("¡Operación Exitosa! \r\rEl Convenio ha sido agregado.");
								location.href = 'ConvenioGrid.jsp';
							}
						});
				}

				if (gsOperacion == 'B')
				{
					var szWhere = " id_Contrato = " + $("#id_Contrato").val() + " ";
					var szTabla = "DNOFACTURA";
					$("#id_Contrato").removeAttr('disabled');                                      
					$("#id_convenioModifica").removeAttr('disabled');                                            
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{
							if (j.length>0)
							{
								alert("¡Advertencia! \r\rEste Convenio tiene Facturas asignadas,\rpor lo que NO PUEDE SER BORRADO.");
								return;
							}
							else
							{
								queryFormPost("dConvenio_ModificatorioDelete, dCategoriaInversion_ImporteTotalUpdate", {async: false});
								alert("¡Operación Exitosa! \r\rEl Convenio ha sido borrado.");
								$("#id_Contrato").removeAttr('disabled');
								location.href = 'ConvenioGrid.jsp';
							}
						});
				}
				
				if (gsOperacion == 'C')
				{
					$("#id_Contrato").removeAttr('disabled');
					$("#NumeroOficioOFI").removeAttr('disabled');
					$("#importe").removeAttr('disabled');
					queryFormPost("dConveniosUpdate,dCategoriaInversion_ImporteTotalUpdate", {async: false});
					alert("¡Operación Exitosa! \r\rEl Convenio ha sido cambiado.");				
					location.href = 'ConvenioGrid.jsp';
				}
				
			});

			$("#pbCancelar")
				.button()
				.click(function() {
					location.href = 'ConvenioGrid.jsp';
					
				});

		});
		
		function fnMontoNegativo(pMonto)
		{
			if (pMonto.indexOf("(")>=0)
			{
				pMonto = (pMonto.replace("(","")).replace("(","");
				pMonto = (pMonto.replace(")","")).replace(")","");
				pMonto = "-" + pMonto;
			}
			return pMonto;
		}

        function fnOrdenaCombo(objCombo)
		{
			$(objCombo).append($( objCombo + " option").remove().sort(function(a, b) {
				var at = $(a).text(), bt = $(b).text();
				return (at > bt)?1:((at < bt)?-1:0);
			}));		
		}
		
		
		
		function DateDiff(date1, date2) {
	    var datediff = date1.getTime() - date2.getTime(); //store the getTime diff - or +
	    return (datediff / (24*60*60*1000)); //Convert values to -/+ days and return value      
		};
		
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form>
		<input id="id_convenioModifica" name="id_convenioModifica" type="hidden" size="10"> 
		<input id="UsaAutorizacion" name="UsaAutorizacion" value="0" type="hidden" size="10">
		
		<div id="container" class="container">
			<h1><img id="imgPlayStop" src="imagenes/wait24trans.gif">Convenio Modificatorio <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<table border="0" align="center" width="100%">
				<tr>
					<td align="right">N&uacute;mero de Convenio:</td>
					<td>
						<input id="NumeroConvenio" name="NumeroConvenio" type="text" size="50" maxlength="50" style='text-transform:uppercase;' title="N&uacute;mero de Convenio"/>
					</td>
					<td colspan ="2" align="right">
						Ejercicio Fiscal:&nbsp;<input id="EjercicioFiscal" name="EjercicioFiscal" type="text" size="4" maxlength="4"  readonly onkeypress="Validaciones(this,2)" style='background:#f0f0f0; text-align:center; text-transform:uppercase;' title="Ejercicio Fiscal"/>
					</td>
				</tr>
				<tr>
					<td align="right">Pr&eacute;stamo:</td>
					<td colspan="3">
						<select id="id_prestamo" name="id_prestamo" style="width:100%" title="Pr&eacute;stamo">
						</select>
					</td>
				</tr>
			</table>
			 
			<table border="0" style="width:100%; background: #fafafa;" >
				<tr>
					<td colspan="4"><label style="FONT-WEIGHT:bold; color:gray">Datos del Contrato</label></td>
				</tr>
				<tr>
					<td align="right">N&uacute;mero de Contrato:</td>
					<td colspan="3">
						<select id="id_Contrato" name="id_Contrato" style="width:100%" title="N&uacute;mero de Contrato">	</select>
					</td>
				</tr>
				<tr>
					<td align="right">Término del Contrato:</td>
					<td colspan="3">
						<input type="text" size="10" id="FechaTerminacion" name="FechaTerminacion" style="background:#f0f0f0; text-align:center" readonly title="Fecha de Terminaci&oacute;n del Contrato">
					</td>					
				</tr>
				<tr>
					<td align="right">Firma del Contrato:</td>
					<td>
						<input type="text" size="10" id="FechaFirmaContrato" name="FechaFirmaContrato" style="background:#f0f0f0; text-align:center" readonly title="Fecha de Firma del Contrato">
					</td>
					<td colspan="2" align="right">Importe Original del Contrato con IVA:&nbsp;<input id="ImporteOriginal" name="ImporteOriginal" type="text" size="20" maxlength="20" readonly onkeypress="Validaciones(this,2)" class="currency" style="background:#f0f0f0;text-align:right" title="Importe Original del Contrato con IVA"/>
					</td>					
				</tr>
			</table>	
			<br>		 
			<table border="0" style="width:100%; " >
				<tr>
					<td align="right">Término del Convenio:</td>
					<td colspan="3">
						<input type="text" size="10" id="FechaTerminoConvenio" name="FechaTerminoConvenio" style="background:#f0f0f0; text-align:center" readonly title="Fecha de Terminaci&oacute;n del Convenio">
					</td>					
				</tr>
				<tr>
					<td align="right" style="width:173px">Fecha Firma Convenio:</td>
					<td><input type="text" size="10" id="FechaFirma" name="FechaFirma" readonly style='background:#f0f0f0; text-align:center' title="Fecha de Firma del Convenio"></td>
					
					<td colspan ="2" align="right">Importe Convenio con IVA:&nbsp;<input id="importe" name="importe" type="text" size="20" maxlength="20" value= "0.00" onkeypress="Validaciones(this,9)" class="currency" style="text-align:right"  title="Importe del Convenio con IVA"/></td>
				</tr>
			</table>
			<br>
			<table border="1" style="width:100%; border: 1px solid #e0e0e0;" >
				<tr>
					<td style="width:170px"><input id="chkUsaAutorizacion"  type="checkbox" title="Activar/Desactivar Oficio de Autorización OFI">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;N&uacute;mero de Oficio de Autorizaci&oacute;n OFI:</td>
					<td>
						<input id="NumeroOficioOFI" name="NumeroOficioOFI"  type="text" size="25"  maxlength="25" disabled style="background:#f0f0f0; text-transform:uppercase;" title="N&uacute;mero de Oficio OFI">
					</td>
					<td colspan="2" align="right">Importe Convenio Autorizado con IVA:&nbsp;<input id="ImporteAumentoAutorizado" name="ImporteAumentoAutorizado" type="text" size="20" maxlength="20" disabled onkeypress="Validaciones(this,9)" class="currency" style="background:#f0f0f0;text-align:right" title="Importe de Aumento Autorizado con IVA"/>
					</td>					
				</tr>
			</table>
			<br>
			<table border="0" style="width:100%; background: #fafafa;" >
				<tr>					
					<td colspan="4" align="right">Importe Total del Contrato con IVA:&nbsp;<input id="ImporteTotal" name="ImporteTotal" type="text" size="20" maxlength="20"  readonly onkeypress="Validaciones(this,2)" class="currency" style="background: #f0f0f0; text-align:right" title="Importe Total del Contrato con IVA"/></td>
				</tr>
			</table>
			<label class="validateTips ui-state-error" ></label>
			<br><br>
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



	</form>
	</body>
</html>