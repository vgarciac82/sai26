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
		<title>Factura</title>

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
		var szFactura = '<%=request.getParameter("Re1")%>';
		var szContrato = '<%=request.getParameter("Re2")%>';
		var szPrestamo = '<%=request.getParameter("Re3")%>';
		var dtNow = new Date();

		function CambiaFormatoFecha(objParam)
		{
			var szFecha = objParam.val();
			if (szFecha.length < 10) return;
			szFecha = szFecha.substr(0,10);
			var res = szFecha.split("-");
			objParam.val(res[2] + "/" + res[1] + "/" + res[0] );
		}

		$(document).ready(function() {
						
			$("#EjercicioFiscal").val(dtNow.getFullYear());
		
			$('.currency').blur(function()	{
				funSumaImportes();
				$('.currency').formatCurrency();
			});

			$( "#id_prestamo" )
                .change(function() {
                try
                {	
                	querySelectPost("vNumeroContratoCvRead", "id_Contrato", {async: false});	
 
 					$("#id_Contrato").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
					fnOrdenaCombo("#id_Contrato");
                	//$("#id_Contrato").removeAttr('selected');
					//$("#id_Contrato option:first").attr('selected','selected');
					$("#id_Contrato").change();
                }              	
                catch (ex)
                {
                }
            });
			$( "#id_Contrato" )
                .change(function() {
  				
  				$("#lbBeneficiario").val("");
				var szWhere = " id_Contrato = '" + $("#id_Contrato").val() + "' ";
				var szTabla = "DRFCCONTRATO";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                       
    					for (var i = 0; i < j.length; i++) 
    					{  
    						$("#lbBeneficiario").val(j[i].Col0);
						}
		         });
		         
				szWhere = " id_prestamo = " + $("#id_prestamo").val() + " AND id_Contrato = " + $("#id_Contrato").val();
				szTabla = "IMPORTETOTALCONTRATOSUMFACTURA";
				
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(k)
					{
				        $("#TotalContrato").val(k[0].Col0);
						$("#TotalFacturas").val(k[0].Col1);
						if ( gsOperacion == 'C')
							{
								var total = parseFloat(k[0].Col1)-parseFloat($("#ImporteTotal").val().replace(",","").replace("$",""));
								$("#TotalFacturas").val(total);
							}
						$('.currency').formatCurrency();
					});			
            });
			
			// Inicializaciones CRUD
			querySelectPost("PrestamoNumNomRead", "id_prestamo", {async: false});
			
			$("#id_prestamo").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			fnOrdenaCombo("#id_prestamo");
			
			$("#id_Contrato").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			
			//$("#id_prestamo").change();

			if (gsOperacion == 'A')
			{
				$("#lbOperacion").text(" (Agregar)");
				$("#FechaFactura").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaPoliza").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});

				$("#id_Contrato").focus();
			}

			if (gsOperacion == 'D' || gsOperacion == 'B')		
			{
				$("#lbOperacion").text(" (Desplegar)");
				
				$("#pbAceptar").css("visibility","hidden");
				$("#pbCancelar").val("Regresar");
						
				$("#id_prestamo").val(szPrestamo);
				$("#id_prestamo").change();
				$("#id_Contrato").val(szContrato);
				$("#id_Contrato").change();
				$("#id_Factura").val(szFactura);

				queryFormPost("dFacturaRead", {async: false});
				
				$("input").attr("readonly", true);
				$("input").css("background", "#f0f0f0");

				$("input").attr("disabled", true);

				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
				
				$("textarea").attr("disabled", true);
				$("textarea").css("background", "#f0f0f0");
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
				$("#id_prestamo").val(szPrestamo);
				$("#id_prestamo").change();
				$("#id_Contrato").val(szContrato);
				$("#id_Contrato").change();
				$("#id_Factura").val(szFactura);

				queryFormPost("dFacturaRead", {async: false});
							

				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
				$("#Concepto").removeAttr('disabled');
				$("#Concepto").css("background", "white");
				
				$("#FechaFactura").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaPoliza").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				
				$("#NumFactura").focus();				
			}
			
			CambiaFormatoFecha($("#FechaFactura"));			
			CambiaFormatoFecha($("#FechaPoliza"));	
			
			$('.currency').formatCurrency();
				
			$("#imgPlayStop").css("visibility", "hidden");
			
			funSumaImportes();
			$("#id_prestamo").focus();

		});
		
		function formSubmited() {
                alert("Factura enviada!");
            }
            
        function fnOrdenaCombo(objCombo)
		{
			$(objCombo).append($( objCombo + " option").remove().sort(function(a, b) {
				var at = $(a).text(), bt = $(b).text();
				return (at > bt)?1:((at < bt)?-1:0);
			}));		
		}
		
		</script>

	<script type="text/javascript" charset="utf-8">
		
		$(function() {

			$( "#dialog:ui-dialog" ).dialog( "destroy" );

			var id_Contrato = $( "#id_Contrato" ),
				NumFactura = $( "#NumFactura" ),
				Beneficiario = $( "#Beneficiario" ),
				RFC = $( "#RFC" ),
				EjercicioFiscal = $( "#EjercicioFiscal" ),
				FechaFactura = $( "#FechaFactura" ),
				FechaPoliza = $( "#FechaPoliza" ),
				ImporteFederal = $( "#ImporteFederal" ),
				ImporteEstatal = $( "#ImporteEstatal" ),
				ImporteMunicipal = $( "#ImporteMunicipal" ),
				ImporteOtros = $( "#ImporteOtros" ),
				Estimacion = $( "#Estimacion" ),
				Concepto = $( "#Concepto" ),
				
				allFields = $( [] ).add( id_Contrato ).add( NumFactura ).add( Beneficiario ).add( RFC ).add( EjercicioFiscal ).add( FechaFactura ).add( FechaPoliza )
									.add(ImporteFederal).add(ImporteEstatal).add(ImporteMunicipal).add(ImporteOtros).add(Estimacion).add(Concepto),
				tips = $( ".validateTips" );			

			function updateTipsDlg( t ) {
				tips
					.text( t );
					alert(t);
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
					}
				});
				$("input").each(function (){
					var szStyle = "" + $(this).attr('style');
					if (szStyle.indexOf("lowercase")>1)
					{
						$(this).val($(this).val().toLowerCase());  
					}
				});
				
				if ($("#id_prestamo").val() == 0 )
				{
					alert("El Préstamo es un dato requerido.");
					$("#id_prestamo").focus();
					return;
				}
				if ($("#id_Contrato").val() == 0 )
				{
					alert("El Contrato es un dato requerido.");
					$("#id_Contrato").focus();
					return;
				}
				
				bValid = bValid && checkRequerido( id_Contrato, "El Número de Contrato" );
				bValid = bValid && checkRequerido( NumFactura, "El Número de Factura" );
				bValid = bValid && checkRequerido( EjercicioFiscal, "El Ejercicio Fiscal" );
				bValid = bValid && checkRequerido( FechaFactura, "La Fecha de la Factura" );
				bValid = bValid && checkRequerido( FechaPoliza, "La Fecha Póliza de Pago" );
				bValid = bValid && checkRequerido( ImporteFederal, "El Importe Federal" );
				bValid = bValid && checkRequerido( ImporteEstatal, "El Importe Estatal" );
				bValid = bValid && checkRequerido( ImporteMunicipal, "El Importe Municipal" );
				bValid = bValid && checkRequerido( ImporteOtros, "El Importe de Otros" );
				bValid = bValid && checkRequerido( Estimacion, "El Número de Estimación" );
				bValid = bValid && checkRequerido( Concepto, "El Concepto de la Factura" );

				if (!bValid) return;






				var szFecha1 = $("#FechaFactura").val();
				var szFecha2 = $("#FechaPoliza").val();
				
				szFecha1 = szFecha1.substr(6,4) + "/" + szFecha1.substr(3,2) + "/" + szFecha1.substr(0,2);  
				szFecha2 = szFecha2.substr(6,4) + "/" + szFecha2.substr(3,2) + "/" + szFecha2.substr(0,2); 
				
				var dtf = new Date(szFecha1);
				var dtp = new Date(szFecha2);

				if (DateDiff(dtp, dtf) <= 0)
				{
					alert("La Fecha de Póliza de Pago debe ser mayor a la Fecha de Factura.");
					$("#FechaPoliza").focus();
					return;				
				}				








				var szTempDub = $("#ImporteFederal").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");	
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				if (Number(szTempDub) <= 0)				
				{
					alert("El Importe Federal de la Factura debe ser un valor mayor a $0.");
					$("#ImporteFederal").focus();
					return;
				}
								
				var szTempDub = $("#ImporteEstatal").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");	
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				if (Number(szTempDub) < 0)				
				{
					alert("El Importe Estatal de la Factura debe ser un valor mayor o igual a $0.");
					$("#ImporteEstatal").focus();
					return;
				}
				
				szTempDub = $("#ImporteMunicipal").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");	
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				if (Number(szTempDub) < 0)
				{
					alert("El Importe Municipal de la Factura debe ser un valor mayor o igual a $0.");
					$("#ImporteMunicipal").focus();
					return;
				}
				
				szTempDub = $("#ImporteOtros").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");	
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				if (Number(szTempDub) < 0)
				{
					alert("El Importe de Otros de la Factura debe ser un valor mayor o igual a $0.");
					$("#ImporteOtros").focus();
					return;
				}
				
				if (Number($("#EjercicioFiscal").val()) < dtNow.getFullYear()-1)
				{
					alert("El Ejercicio Fiscal debe ser un año mayor o igual a " + (dtNow.getFullYear() -1));
					$("#EjercicioFiscal").focus();
					return;
				}
				if (Number($("#EjercicioFiscal").val()) > dtNow.getFullYear()+2)
				{
					alert("El Ejercicio Fiscal debe ser un año menor o igual a " + (dtNow.getFullYear() +2));
					$("#EjercicioFiscal").focus();
					return;
				}
				
				if ($("#Concepto").val() == 0 )
				{
					alert("El Concepto es un dato requerido.");
					$("#Concepto").focus();
					return;
				}
				
				if (!bValid) 
				{
					return;
				}
				
				if (gsOperacion == 'A')
				{				
					szWhere = " NumFactura = '" + $("#NumFactura").val() + "' AND id_Contrato = " + $("#id_Contrato").val(); 
					szTabla = "DNOFACTURA";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{
							if (j.length>0)
							{
								alert("Error. \rYa existe ese Número de Factura.");
								$("#NumFactura").focus();
								return;
							}
							else
							{
								szWhere = " id_prestamo = " + $("#id_prestamo").val() + " AND id_Contrato = " + $("#id_Contrato").val();
								szTabla = "IMPORTETOTALCONTRATOSUMFACTURA";
								
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
								function(k)
								{
									var dTotal = funSumaImportes();   
									if (Number(k[0].Col0) >= (Number(k[0].Col1) + Number(dTotal)))
									{
										queryFormPost("dFacturasCreate", {async: false});
										alert("¡Operación Exitosa! \r\rLa factura ha sido agregada.");
										location.href = 'FacturaGrid.jsp';
									}
									else
									{
										alert("Error. \rEl Importe Total asignado más el total de facturas existentes es mayor que el Importe Total del Contrato.");
										return;
									}
								});
							}
						});									
				}

				if (gsOperacion == 'B')
				{
					var szWhere = " id_Factura = " + $("#id_Factura").val() + " ";
					var szTabla = "DSOE";                                          
					$("#id_Factura").removeAttr('disabled');
					                                              
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{               
							if (j.length>0)
							{
								alert("¡Advertencia! \r\rEsta Factura está asignada a un SOE,\rpor lo que NO PUEDE SER BORRADA.");
								return;
							}      
							else
							{
								queryFormPost("dFacturaDelete");
								alert("¡Operación Exitosa! \r\rLa Factura ha sido borrada.");
								location.href = 'FacturaGrid.jsp';
							}
						});
				}
				
				if (gsOperacion == 'C')
				{
				
					szWhere = "NumFactura = '" + $("#NumFactura").val() + "' AND id_Contrato = " + $("#id_Contrato").val() + " AND id_Factura != " + $("#id_Factura").val();
					szTabla = "DNOFACTURA";
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'},
						function(j)
						{
							if (j.length>0)
							{
								alert("Error. \rYa existe ese Número de Factura.");
								$("#NumFactura").focus();
								return;
							}
							else
							{
				
							szWhere = " id_prestamo = " + $("#id_prestamo").val() + " AND id_Contrato = " + $("#id_Contrato").val();
							szTabla = "IMPORTETOTALCONTRATOSUMFACTURA";
							
							$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
							function(k)
								{
									var dTotal = funSumaImportes();								
									var totalFactura = replaceAll( $("#TotalFacturas").val().replace("$","") , ",", "");
									
									if (Number(k[0].Col0) >= (Number(totalFactura) + Number(dTotal) ))
									{
										queryFormPost("dFacturasUpdate", {async: false});
										alert("¡Operación Exitosa! \r\rLa Factura ha sido cambiada.");				
										location.href = 'FacturaGrid.jsp';
									}
									else
									{
										alert("Error. \rEl Importe Total asignado más el total de facturas existentes es mayor que el Importe Total del Contrato.");
										return;
									}
								});
							
							};
						});
				}
				
			});
				
			$("#pbCancelar")
				.button()
				.click(function() {
					location.href = 'FacturaGrid.jsp';
				});	
		});		
		function funSumaImportes() {
			var importeTotal = 0;
			var szTempDub;
			
			szTempDub = $("#ImporteFederal").val();
			szTempDub = (szTempDub.replace("$","")).replace("$","");	
			szTempDub = (szTempDub.replace(",","")).replace(",",""); 
			importeTotal = importeTotal + new Number(szTempDub); 		
			szTempDub = $("#ImporteEstatal").val();
			szTempDub = (szTempDub.replace("$","")).replace("$","");	
			szTempDub = (szTempDub.replace(",","")).replace(",","");
			importeTotal = importeTotal + new Number(szTempDub); 	
			szTempDub = $("#ImporteMunicipal").val();
			szTempDub = (szTempDub.replace("$","")).replace("$","");	
			szTempDub = (szTempDub.replace(",","")).replace(",","");
			importeTotal = importeTotal + new Number(szTempDub);	
			szTempDub = $("#ImporteOtros").val();
			szTempDub = (szTempDub.replace("$","")).replace("$","");	
			szTempDub = (szTempDub.replace(",","")).replace(",","");
			importeTotal = importeTotal + new Number(szTempDub); 

			$("#ImporteTotal").val(importeTotal);
			$('.currency').formatCurrency();
			
		
			
			return importeTotal;
		};
		function DateDiff(date1, date2) {
	    var datediff = date1.getTime() - date2.getTime(); //store the getTime diff - or +
	    return (datediff / (24*60*60*1000)); //Convert values to -/+ days and return value      
		};
		
		
		function replaceAll( text, busca, reemplaza ){ 
				 while (text.toString().indexOf(busca) != -1)      
				 text = text.toString().replace(busca,reemplaza);  
				 return text;
		}
	</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form>
		<input type="hidden" id="id_Factura" name="id_Factura"  />
		
		<div id="container" class="container">
			<h1><img id="imgPlayStop" src="imagenes/wait24trans.gif">Factura <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<table border="0" align="center" width="100%" cellspacing="1" cellpadding="1">

				<tr>
					<td align="right">Pr&eacute;stamo</td>
					<td colspan = "3">
						<select id="id_prestamo" name="id_prestamo" style="width:100%" title="Pr&eacute;stamo"></select>
					</td>
				</tr>
				<tr>
					<td align="right">N&uacute;mero Contrato</td>
					<td width="310px">
						<select id="id_Contrato" name="id_Contrato" style="width:100%" title="N&uacute;mero Contrato"></select>
					</td>
					<td align="right">N&uacute;mero Factura:</td>
					<td><input id="NumFactura" name="NumFactura" type="text" size="20" maxlength="20" style='text-transform:uppercase;' title="N&uacute;mero Factura"/></td>
				</tr>
				<tr>
					<td align="right">Beneficiario:</td>
					<td colspan="3">
						<input id="lbBeneficiario"  type="text" readonly style="background: #f0f0f0" style=' width:100%; text-transform:uppercase;' title="Beneficiario"/>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td align="center">Ejercicio Fiscal:</td>
					<td align="center">Fecha de Factura:</td>
					<td align="center">Fecha Póliza de Pago:</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td align="center"><input id="EjercicioFiscal" name="EjercicioFiscal" type="text" size="4" maxlength="4" onkeypress="Validaciones(this,2)" style='text-align:center; text-transform:uppercase;' title="Ejercicio Fiscal"/></td>
					<td align="center"><input type="text" size="10" id="FechaFactura" name="FechaFactura" readonly style="background:#f0f0f0; text-align:center" title="Fecha de Factura"></td>
					<td align="center"><input  type="text" size="10" id="FechaPoliza" name="FechaPoliza" readonly style="background:#f0f0f0; text-align:center" title="Fecha Póliza de Pago"></td>
				</tr>
				<tr>
					<td colspan="3" align="right">Importe Federal con IVA:</td>
					<td><input id="ImporteFederal" name="ImporteFederal" type="text" size="20" maxlength="20" value= "0.00" onkeypress="Validaciones(this,9)" class="currency" style="text-align:right" title="Importe Federal con IVA"/></td>
				</tr>
				<tr>
					<td colspan="3" align="right">Importe Estatal con IVA:</td>
					<td><input id="ImporteEstatal" name="ImporteEstatal" type="text" size="20" maxlength="20" value= "0.00" onkeypress="Validaciones(this,9)" class="currency" style="text-align:right" title="Importe Estatal con IVA"/></td>
				</tr>	 
				<tr>
					<td align="right">Concepto:</td>
					<td>
						<select id="Concepto" name="Concepto" title="Concepto">
							<option value=0 selected>- Seleccionar -</option>
							<option value="Anticipo">Anticipo</option>
							<option value="Estimación">Estimación</option>
						</select>
					</td>
					<td align="right">Importe Municipal con IVA:</td>
					<td><input id="ImporteMunicipal" name="ImporteMunicipal" type="text" size="20" maxlength="20" value= "0.00" onkeypress="Validaciones(this,9)" class="currency" style="text-align:right" title="Importe Municipal con IVA"/></td>
				</tr>
				<tr>
					<td align="right">Número:</td>
					<td><input id="Estimacion" name="Estimacion" type="text" size="5" maxlength="2" onkeypress="Validaciones(this,2)" style='text-align:center; text-transform:uppercase;' title="Número Estimación"/></td>
					<td align="right">Importe Otros con IVA:</td>
					<td><input id="ImporteOtros" name="ImporteOtros" type="text" size="20" maxlength="20" value= "0.00" onkeypress="Validaciones(this,9)" class="currency" style="text-align:right" title="Importe Otros con IVA"/></td>
				</tr>
				<tr>
					<td colspan="3" align="right">Importe Total con IVA:</td>
					<td><input id="ImporteTotal" name="ImporteTotal" type="text" size="20" maxlength="20" readonly class="currency" style="background:#f0f0f0; text-align:right" title="Importe Total con IVA"/></td>
				</tr>
				<tr>				
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>				
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td align="center">Total Contrato:</td>
					<td align="center">Total Facturas:</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td><input id="TotalContrato" name="TotalContrato" type="text" size="20" maxlength="20" readonly class="currency" style="background:#f0f0f0; text-align:right" title="Monto Total del Contrato"/></td>
					<td><input id="TotalFacturas" name="TotalFacturas" type="text" size="20" maxlength="20" readonly class="currency" style="background:#f0f0f0; text-align:right" title="Suma de Facturas del Contrato"/></td>
				</tr>			
			</table>
			<br><br>
			<table width="100%" border="0" >
				<tr>
					<td align="center">
						<input type="button" id="pbAceptar" value="Aceptar"/>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbCancelar" value="Cancelar"/>
					</td>
				</tr>
			</table>
		</div>
	</form>
	</body>
</html>