<%@page language="java" pageEncoding="UTF-8"  import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	String cUR = "";
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
	
	cUR = usuario.getU_UR();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Contrato</title>

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
		var szTemp = '<%=request.getParameter("Re")%>';
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
			
 			$("#id_prestamo")
                .change(function() {
				querySelectPost("ComponenteNumNomRead", "id_componentetecnicobis", {async: false});
				
				$("#id_componentetecnicobis").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));	
				fnOrdenaCombo("#id_componentetecnicobis");
				
				//$("#id_componentetecnicobis").removeAttr('selected');
				//$("#id_componentetecnicobis option:first").attr('selected','selected');
				//$("#id_componentetecnico").val($("#id_componentetecnicobis").val()); 
				$("#id_componentetecnicobis").change();
             });	
             
            $("#id_componentetecnicobis")
                .change(function() {
                $("#id_componentetecnico").val($("#id_componentetecnicobis").val());
                querySelectPost("vCategoriaInversionRead", "id_categoriaInversionPrestamobis", {async: false});  
              
              	$("#id_categoriaInversionPrestamobis").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));	
				fnOrdenaCombo("#id_categoriaInversionPrestamobis");
              	
                //$("#id_categoriaInversionPrestamobis").removeAttr('selected');
				//$("#id_categoriaInversionPrestamobis option:first").attr('selected','selected');

             });
             
             $('.currency').blur(function()	{
				$('.currency').formatCurrency();
				});
				
			// Inicializaciones CRUD
			
			querySelectPost("PrestamoNumNomRead", "id_prestamo", {async: false});
			
			$("#id_prestamo").change();
			
			$("#cUnidadResponsable").val("<%=cUR%>");
			
			if("<%=cUR%>" == "A02" ) $("#cUnidadResponsable").val('%');
			
			querySelectPost("dcatalogoEntidadFederativaRead", "cEntidadFed", {async: false});
			querySelectPost("vEjecutorRead", "id_EntidadEjecResp", {async: false});
			querySelectPost("dCatalogoTipoAdjudicacionRead", "TipoAdjudicacion", {async: false});
			
			$("#id_prestamo").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			$("#cEntidadFed").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			$("#id_EntidadEjecResp").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			$("#TipoAdjudicacion").append($('<option>', { value: 0, text: '- Seleccionar -', selected: true }));
			
			fnOrdenaCombo("#id_prestamo");
			fnOrdenaCombo("#cEntidadFed");
			fnOrdenaCombo("#id_EntidadEjecResp");
			fnOrdenaCombo("#TipoAdjudicacion");

			//$("#cEntidadFed").val("09");		// por default el DF

			if (gsOperacion == 'A')
			{
				$("#lbOperacion").text(" (Agregar)");
				$("#FechaFirma").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaTerminacion").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaInicio").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				
				$("#NoContrato").focus();
			};

			if (gsOperacion == 'D' || gsOperacion == 'B' )
			{
				$("#lbOperacion").text(" (Desplegar)");
				$("#pbAceptar").css("visibility","hidden");
				$("#pbCancelar").val("Regresar");
				
				$("#id_Contrato").val(szTemp);

				queryFormPost("ContratosRead", {async: false});
				querySelectPost("ComponenteNumNomRead", "id_componentetecnicobis", {async: false});			   
				var szGuardaCompo = $("#id_componentetecnico").val();	
				var szGuardaCate = $("#id_categoriaInversionPrestamo").val();	
				$( "#id_prestamo" ).change();
 				$( "#id_componentetecnicobis" ).val(szGuardaCompo);   
				$( "#id_componentetecnicobis" ).change();
 				$( "#id_categoriaInversionPrestamobis" ).val(szGuardaCate);
 
				$("input").attr("readonly", true);
				$("input").css("background", "#f0f0f0");

				$("input").attr("disabled", true);

				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
				
				$("#ObjetoDeObra").attr("disabled", true);
				$("#ObjetoDeObra").css("background", "#f0f0f0"); 
				
				CambiaFormatoFecha($("#FechaFirma"));
				CambiaFormatoFecha($("#FechaInicio"));
				CambiaFormatoFecha($("#FechaTerminacion"));
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
				$("#id_Contrato").val(szTemp);

				queryFormPost("ContratosRead", {async: false});
				querySelectPost("ComponenteNumNomRead", "id_componentetecnicobis", {async: false});			   
				var szGuardaCompo = $("#id_componentetecnico").val();	
				var szGuardaCate = $("#id_categoriaInversionPrestamo").val();	
				
				$( "#id_prestamo" ).change();
 				$( "#id_componentetecnicobis" ).val(szGuardaCompo);   
				$( "#id_componentetecnicobis" ).change();
 				$( "#id_categoriaInversionPrestamobis" ).val(szGuardaCate);
 				
				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
				$("#cEntidadFed").removeAttr('disabled');
				$("#id_EntidadEjecResp").removeAttr('disabled');
				$("#TipoAdjudicacion").removeAttr('disabled');
				$("#cEntidadFed").css("background", "white");
				$("#id_EntidadEjecResp").css("background", "white");
				$("#TipoAdjudicacion").css("background", "white");
				
				CambiaFormatoFecha($("#FechaFirma"));
				CambiaFormatoFecha($("#FechaInicio"));
				CambiaFormatoFecha($("#FechaTerminacion"));
				
				$("#FechaFirma").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaTerminacion").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaInicio").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				
				$("#NoContrato").focus();
				
			}
			
			$("#imgPlayStop").css("visibility", "hidden");
			$('.currency').formatCurrency();
		});
		function formSubmited() {
                alert("Contrato enviado!");
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

			var cEntidadFed 	= $( "#cEntidadFed" ),
				id_prestamo 	= $( "#id_prestamo"),
				id_componentetecnicobis = $( "#id_componentetecnicobis"),
				id_categoriaInversionPrestamobis = $( "#id_categoriaInversionPrestamobis"),
				EjercicioFiscal = $( "#EjercicioFiscal" ),
				NoContrato 		= $( "#NoContrato" ),
				ImporteOriginal = $( "#ImporteOriginal"),
				//ImporteTotal  	= $( "#ImporteTotal"),
				NombreBeneficiario = $( "#NombreBeneficiario"),
				RFCBeneficiario = $( "#RFCBeneficiario"),
				FechaFirma 		= $( "#FechaFirma"),
				FechaInicio 	= $( "#FechaInicio"),
				FechaTerminacion = $( "#FechaTerminacion"),
				TipoAdjudicacion = $( "#TipoAdjudicacion"),
				ObjetoDeObra 	= $( "#ObjetoDeObra"),
				id_EntidadEjecResp = $( "#id_EntidadEjecResp"),

				
				allFields 		= $( [] )	.add( cEntidadFed ).add( id_prestamo ).add( id_componentetecnicobis ).add( id_categoriaInversionPrestamobis ).add( EjercicioFiscal )
									.add( NoContrato ).add( ImporteOriginal ).add( NombreBeneficiario )
									.add( RFCBeneficiario ).add( FechaFirma ).add( FechaInicio ).add( FechaTerminacion )
									.add( TipoAdjudicacion ).add( ObjetoDeObra ).add( id_EntidadEjecResp ),
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
				};
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
				
			
				if ($("#id_prestamo").val() == 0 )
				{
					alert("El Préstamo es un dato requerido.");
					$("#id_prestamo").focus();
					return;
				}
				if ($("#id_componentetecnicobis").val() == 0 )
				{
					alert("Componente es un dato requerido.");
					$("#id_componentetecnicobis").focus();
					return;
				}
				if ($("#id_categoriaInversionPrestamobis").val() == 0 )
				{
					alert("La Categoría de Inversión es un dato requerido.");
					$("#id_categoriaInversionPrestamobis").focus();
					return;
				}
				if ($("#cEntidadFed").val() == 0 )
				{
					alert("La Entidad Federativa es un dato requerido.");
					$("#cEntidadFed").focus();
					return;
				}
				if ($("#id_EntidadEjecResp").val() == 0 )
				{
					alert("El Área Ejecutora Externa es un dato requerido.");
					$("#id_EntidadEjecResp").focus();
					return;
				}
				if ($("#TipoAdjudicacion").val() == 0 )
				{
					alert("El Tipo de Adjudicación es un dato requerido.");
					$("#TipoAdjudicacion").focus();
					return;
				}
				
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
					};
				});
				
				bValid = bValid && checkRequerido( NoContrato, "Número de Contrato" );
				bValid = bValid && checkRequerido( EjercicioFiscal, "Ejercicio Fiscal" );				
				bValid = bValid && checkRequerido( id_prestamo, "Préstamo" );
				bValid = bValid && checkRequerido( id_componentetecnicobis, "Componente" );
				bValid = bValid && checkRequerido( id_categoriaInversionPrestamobis, "Categoría de Inversión" );				
				bValid = bValid && checkRequerido( cEntidadFed, "Entidad Federativa" );
				bValid = bValid && checkRequerido( NombreBeneficiario, "Nombre del Beneficiario" );
				bValid = bValid && checkRequerido( RFCBeneficiario, "RFC del Beneficiario");
				bValid = bValid && checkRequerido( TipoAdjudicacion, "Tipo de Adjudicacion");
				bValid = bValid && checkRequerido( ImporteOriginal, "Importe Original" );
				//bValid = bValid && checkRequerido( ImporteTotal, "Importe Total" );
				bValid = bValid && checkRequerido( FechaFirma, "Fecha de Firma del Contrato");
				bValid = bValid && checkRequerido( FechaInicio, "Fecha de Inicio del Contrato");
				bValid = bValid && checkRequerido( FechaTerminacion, "Fecha de Terminación del Contrato");
				bValid = bValid && checkRequerido( ObjetoDeObra, "Objeto de la Obra");
				
				if (!bValid) return;
				
				if (Number($("#EjercicioFiscal").val()) <= 2010)
				{
					alert("El Ejercicio Fiscal debe ser un año mayor a 2010.");
					$("#EjercicioFiscal").focus();
					return;
				}
				
				var szRFC = $("#RFCBeneficiario").val();
				var iLongRFC = szRFC.length;

				if (iLongRFC<12 || iLongRFC >14)
				{
					alert("El RFC del Beneficiario debe tener una longitud entre 12 y 14 caracteres.");
					$("#RFCBeneficiario").focus();
					return;
				}
	
				var szTempDub = $("#ImporteOriginal").val();
				szTempDub = (szTempDub.replace("$","")).replace("$","");	
				szTempDub = (szTempDub.replace(",","")).replace(",","");
				if (Number(szTempDub) <= 0)
				{
					alert("El Importe Original del Contrato debe ser un valor mayor a 0.");
					$("#ImporteOriginal").focus();
					return;
				}

				var szFecha1 = $("#FechaFirma").val();
				var szFecha2 = $("#FechaInicio").val();
				var szFecha3 = $("#FechaTerminacion").val();
				
				szFecha1 = szFecha1.substr(6,4) + "/" + szFecha1.substr(3,2) + "/" + szFecha1.substr(0,2);  
				szFecha2 = szFecha2.substr(6,4) + "/" + szFecha2.substr(3,2) + "/" + szFecha2.substr(0,2); 
				szFecha3 = szFecha3.substr(6,4) + "/" + szFecha3.substr(3,2) + "/" + szFecha3.substr(0,2);
				
				var dtf = new Date(szFecha1);
				var dti = new Date(szFecha2);
				var dtt = new Date(szFecha3);

				if (DateDiff(dti, dtf) < 0)
				{
					alert("La Fecha de Inicio del Contrato debe ser mayor o igual a la Fecha de Firma del Contrato.");
					$("#FechaInicio").focus();
					return;				
				}			
				if (DateDiff(dtt, dti) <= 0)
				{
					alert("La Fecha de Termino del Contrato debe ser mayor a la Fecha de Inicio del Contrato.");
					$("#FechaTerminacion").focus();
					return;				
				}					
							
				$( "#id_componentetecnico" ).val($("#id_componentetecnicobis").val()); 
				$( "#id_categoriaInversionPrestamo" ).val($( "#id_categoriaInversionPrestamobis" ).val());

			
				if (gsOperacion == 'A')
				{
					$("#Estatus").val("A");
					$("#ImporteTotal").val($("#ImporteOriginal").val());
					
					szWhere = " NoContrato = '" + $("#NoContrato").val() + "' "; 
					szTabla = "DNOCONTRATO";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'},
						function(j)
						{             
							if (j.length>0)
							{
								alert("Error. \rYa existe ese Número de Contrato.");
								$("#NoContrato").focus();
								return;
							}  
							else
							{
								queryFormPost("dContratosCreate", {async: false});
								alert("¡Operación Exitosa! \r\rEl Contrato ha sido agregado.");
								location.href = 'ContratoGrid.jsp';
							}
						});
				}

				if (gsOperacion == 'B')
				{
					var szWhere = " id_Contrato = " + $("#id_Contrato").val() + " ";
					var szTabla = "DNOCONVENIO";                                          
					$("#id_Contrato").removeAttr('disabled');
					                                               
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{
							if (j.length>0)
							{
								alert("¡Advertencia! \r\rEste Contrato tiene Convenios asignados,\rpor lo que NO PUEDE SER BORRADO.");
								return;
							}
							else
							{
								szWhere = " id_Contrato = " + $("#id_Contrato").val() + " ";
								szTabla = "DNOFACTURA";
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'},
									function(j)
									{
										if (j.length>0)
										{
											alert("¡Advertencia! \r\rEste Contrato tiene Facturas asignadas,\rpor lo que NO PUEDE SER BORRADO.");
											return;
										}
										else
										{
											queryFormPost("dContratoDelete");
											alert("¡Operación Exitosa! \r\rEl Contrato ha sido borrado.");
											location.href = 'ContratoGrid.jsp';
										}
									});
							}
						});
				}
				
				if (gsOperacion == 'C')
				{
					queryFormPost("dContratoUpdate,dCategoriaInversion_ImporteTotalUpdate", {async: false});
					alert("¡Operación Exitosa! \r\rEl Contrato ha sido cambiado.");				
					location.href = 'ContratoGrid.jsp';
				}
			});

			$("#pbCancelar")
				.button()
				.click(function() {
					location.href = 'ContratoGrid.jsp';
					
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
			};
			
		}
		
		function DateDiff(date1, date2) {
		    var datediff = date1.getTime() - date2.getTime(); //store the getTime diff - or +
		    return (datediff / (24*60*60*1000)); //Convert values to -/+ days and return value      
		}

	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form>		
		<input type="hidden" id="Estatus" name="Estatus"   />
		<input type="hidden" id="id_Contrato" name="id_Contrato"  />
		<input type="hidden" id="id_componentetecnico" name="id_componentetecnico" />
		<input type="hidden" id="id_categoriaInversionPrestamo" name="id_categoriaInversionPrestamo" />
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" />
	
		<div id="container" class="container">
			<h1><img id="imgPlayStop" src="imagenes/wait24trans.gif">Contrato <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<table border="0" align="center" width="100%" cellspacing="1" cellpadding="1">
				<tr>
					<td align="right">Número de Contrato:</td>
					<td>
						<input id="NoContrato" name="NoContrato" type="text" maxlength="50" style='width:300px; text-transform:uppercase;' title="N&uacute;mero del Contrato"/>
					</td>
					<td colspan="2" align="right">Ejercicio Fiscal:<input id="EjercicioFiscal" name="EjercicioFiscal" type="text" size="4" maxlength="4" onkeypress="Validaciones(this,2)" style='text-align:center; text-transform:uppercase;' title="Ejercicio Fiscal"/></td>
				</tr>
				<tr>
					<td align="right">Pr&eacute;stamo:</td>
					<td colspan = "3">
						<select id="id_prestamo" name="id_prestamo" style = "width:100%" title="Pr&eacute;stamo"></select>
					</td>
				</tr>
				<tr>
					<td align="right">Componente:</td>
					<td colspan = "3">
						<select id="id_componentetecnicobis" name="id_componentetecnicobis" style = "width:100%" title="Componente"></select>
					</td>
				</tr>
				<tr>
					<td align="right">Categor&iacute;a de Inversi&oacute;n:</td>
					<td colspan="3">
						<select id="id_categoriaInversionPrestamobis" name="id_categoriaInversionPrestamobis" style="width:100%" title="Categor&iacute;a de Inversi&oacute;n"></select>
					</td>
				</tr>
				<tr>
					<td align="right">Entidad Federativa:</td>
					<td colspan="3">
						<select id="cEntidadFed" name="cEntidadFed" title="Entidad Federativa"></select>
					</td>
				</tr>
				<tr>
					<td align="right" style="width:170px">Área Ejecutora Externa:</td>
					<td colspan="3">
						<select id="id_EntidadEjecResp" name="id_EntidadEjecResp" style = "width:100%" title="Área Ejecutora Externa"></select>
					</td>
				</tr>
				<tr>
					<td align="right">Nombre Beneficiario:</td>
					<td><input id="NombreBeneficiario" name="NombreBeneficiario" type="text"  maxlength="300" style='width:300px; text-transform:uppercase;' title="Nombre Beneficiario"/></td>
					<td align="right">RFC Beneficiario:</td>
					<td><input id="RFCBeneficiario" name="RFCBeneficiario" type="text" maxlength="14" style='width:95%; text-transform:uppercase;'title="RFC Beneficiario"/></td>
				</tr>
				<tr>
					<td align="right">Tipo de Adjudicaci&oacute;n:</td>
					<td colspan="3">
						<select id="TipoAdjudicacion" name="TipoAdjudicacion" title="Tipo de Adjudicaci&oacute;n"></select>
					</td>
				</tr>
				<tr>
					<td colspan="3" align="right">Importe Original:</td>
					<td><input id="ImporteOriginal" name="ImporteOriginal" type="text" size="20" maxlength="20" value= "0.00" onkeypress="Validaciones(this,2)" class="currency" style="text-align:right" title="Importe Original"/></td>
				</tr>
				<tr>					
					<td colspan="3" align="right">Importe Total:</td>
					<td><input id="ImporteTotal" name="ImporteTotal" type="text" size="20" maxlength="20" value= "0.00" readonly onkeypress="Validaciones(this,2)" class="currency" style="background: #f0f0f0; text-align:right" title="Importe Total"/></td>
				</tr>

			</table>
			<br/>
			<fieldset>
			<table border="0" align="center" width="100%">
				<tr>
					<td  align="center">Fecha Firma:</td>
					<td  align="center">Fecha Inicio:</td>
					<td  align="center">Fecha Terminaci&oacute;n:</td>
				</tr>
				<tr>
					<td align="center"><input type="text" size="10" id="FechaFirma" name="FechaFirma" style="background:#f0f0f0; text-align:center" readonly title="Fecha Firma"></td>
					<td align="center"><input type="text" size="10" id="FechaInicio" name="FechaInicio" style="background:#f0f0f0; text-align:center" readonly title="Fecha Inicio"></td>
					<td align="center"><input type="text" size="10" id="FechaTerminacion" name="FechaTerminacion" style="background:#f0f0f0; text-align:center" readonly title="Fecha Terminaci&oacute;n"></td>
				</tr>
			</table>
			</fieldset>
			<br/>
			<table style="width:100%;" >
				<tr>
					<td align ="right" style="width:160px;">Objeto de la Obra:</td>
					<td >
						<textarea id="ObjetoDeObra" name="ObjetoDeObra" rows="5"   style='width:97%; FONT-FAMILY: Arial, Tahoma, Verdana;' title="Objeto de la Obra"></textarea> 
					</td>
				</tr>
			</table>


			<label class="validateTips ui-state-error" ></label>
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