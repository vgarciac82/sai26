<%@page language="java" pageEncoding="UTF-8" import="java.util.*"%>
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
		<title>Car&aacute;tula de Pr&eacute;stamo</title>

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
	
		var gsOperacion ='<%=request.getParameter("Op")%>';
		
		var arrSubComponentes = new Array();
		var arrCategorias = new Array();
		
			// arreglos para Cambio
		var arrComponentesCambio = new Array();
		var arrSubComponentesCambio = new Array();
		var arrCategoriasCambio = new Array();
		
		var oTablePP;
		
		function CambiaFormatoFecha(objParam)
		{
			var szFecha = objParam.val();
			if (szFecha.length < 10) return;
			szFecha = szFecha.substr(0,10);
			var res = szFecha.split("-");
			objParam.val(res[2] + "/" + res[1] + "/" + res[0] );
		}

		$(document).ready(function() {
			
			oTableComponente = $("#tblComponente").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : true,
				sScrollY: "74",
				sScrollX: "97%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "id_componentetecnico",	bSortable: false, bVisible: true  },
					{ sName: "componentetecnico",	bSortable: false }
				]
        	});
        	
        	oTableSubComponente = $("#tblSubComponente").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				sScrollY: "74",
				sScrollX: "92%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "id_subcomponentetecnico",	bSortable: false },
					{ sName: "subcomponentetecnico",	bSortable: false },
					{ sName: "id_componentetecnicoS",	bSortable: false, bVisible: false  }					
				]
        	});
        	
        	oTableCategoria = $("#tblCategoria").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				sScrollY: "74",
				sScrollX: "92%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "NumeroCategoria",	bSortable: false },
					{ sName: "NombreCategoria",	bSortable: false },
					{ sName: "Monto",	bSortable: false, sClass: "alignRight" },
					{ sName: "PorcentajeFinanciamiento",	bSortable: false, sClass: "alignCenter" },
					{ sName: "id_subcomponeteD",	bSortable: false, sClass: "alignCenter" },
					{ sName: "id_componentetecnicoD",	bSortable: false, bVisible: false  },
					{ sName: "id_categoriaInversionPrestamoD",	bSortable: false, bVisible: false  }
				]
        	});
        	
        	oTablePP = $("#tblProgramasPresupuestarios").dataTable({
					bAutoWidth : false,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vProgramasPresupuestarios",
					bProcessing: true,
					bJQueryUI: true,
					 "sDom": 'frt' ,
					bFilter:false,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "CheckBox",	sClass: "alignCenter" },
						{ sName: "Clave" },
						{ sName: "Descripcion" 	}
					]
	        	});		
			

			$("#tblComponente tbody").click(function(event) {
			
					$("#aTab1").click();
					var aTrs = $('#tblComponente').dataTable().fnGetNodes();
					
					if (aTrs.length == 0)
						return;
					
					$(oTableComponente.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					
					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();    
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#idComponente").val(rowsTbl[i][0]);
							$("#txtNumeroComponente").val(rowsTbl[i][0]);
							$("#txtNombreComponente").val(rowsTbl[i][1]);  
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					$('#tblSubComponente').dataTable().fnClearTable();
					$('#tblCategoria').dataTable().fnClearTable();
					
					for (i=0;i<arrSubComponentes.length;i++)
					{
						var res = arrSubComponentes[i].split("||");   
						if ($("#idComponente").val() == res[0])
						{
								$('#tblSubComponente').dataTable().fnAddData([ 
								res[1],
								res[2],
								"0"
								]); 
						}
					}
					for (i=0;i<arrCategorias.length;i++)
					{
						var res = arrCategorias[i].split("||");   
						if ($("#idComponente").val() == res[0])
						{
								$('#tblCategoria').dataTable().fnAddData([ 
								res[1],
								res[2],
								res[3],
								res[4],
								res[5],
								res[6],
								"0"
								]); 
						}
					}
					
				});

			$("#tblSubComponente tbody").click(function(event) {
			
					var aTrs = $('#tblSubComponente').dataTable().fnGetNodes();
					
					if (aTrs.length == 0)
						return;
					
					$(oTableSubComponente.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});

			$("#tblCategoria tbody").click(function(event) {
			
					var aTrs = $('#tblCategoria').dataTable().fnGetNodes();
					
					if (aTrs.length == 0)
						return;
					
					$(oTableCategoria.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});

			$(".tabs").tabs();
						
			$('.currency').blur(function()	{
				$('.currency').formatCurrency();
				});

			// Inicializaciones CRUD
			
			querySelectPost("catalogoOFIRead", "id_ofi", {async: false});
			querySelectPost("catalogoAgenteFinancieroRead", "id_agentefinanciero", {async: false});
			querySelectPost("UnidadresponsableRead", "cUnidadResponsable", {async: false});
			
			$("#id_ofi").append($('<option>', {
			    value: 0,
			    text: '- Seleccionar -'
			    , selected: true
			}));
			$("#id_agentefinanciero").append($('<option>', {
			    value: 0,
			    text: '- Seleccionar -'
			    , selected: true
			}));
			$("#cUnidadResponsable").append($('<option>', {
			    value: 0,
			    text: '- Seleccionar -'
			    , selected: true
			}));
			
			fnOrdenaCombo("#id_ofi");
			fnOrdenaCombo("#id_agentefinanciero");
			fnOrdenaCombo("#cUnidadResponsable");				

			if (gsOperacion == 'A')
			{
				$("#lbOperacion").text(" (Agregar)");
				
				$("#FechaFirma").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaEfectividad").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});			
				$("#FechaTerminacion").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaLimiteDesembolsar").datepicker({ showOn : "button",	dateFormat:"dd/mm/yy",showAnim:"slideDown", buttonImage : "images/calendar.gif", buttonImageOnly : true	});
			
				$("#FechaFirma").val( "<%=today%>");
				$("#FechaEfectividad").val( "<%=today%>");

				$("#NumeroPrestamo").focus();
			}

			if (gsOperacion == 'D' || gsOperacion == 'C' || gsOperacion == 'B')
			{
				$("#lbOperacion").text(" (Desplegar)");
				
				$("#fieldCambio").css("visibility","visible");
		        $("#fieldCambio").css("height","205px");
		        
		        $("#pbAceptar").css("visibility","hidden");
				$("#pbCancelar").val("Regresar");
				
				var szTemp = '<%=request.getParameter("Re")%>';

				$("#id_prestamo").val(szTemp);

				queryFormPost("dPrestamoRead", {async: false });
				
				var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " ";
				var szTabla = "DPRESTAMOPROGRAMAPRESUPUESTARIO";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                       
						for (var i = 0; i < j.length; i++) 
    					{  
    						$("#chk"+j[i].Col1).attr('checked', true );
						}				
						
						if (gsOperacion != 'C')
						{
							var aTrs = oTablePP.fnGetNodes();    
							var rowsTbl = $("#tblProgramasPresupuestarios").dataTable().fnGetData();      
							
							for ( var i=0 ; i<aTrs.length ; i++ )     
							{
								$("#chk"+rowsTbl[i][1]).css("background", "#f0f0f0");
								$("#chk"+rowsTbl[i][1]).attr("disabled", true);						    
							}  
						}
		         	});  
				

				var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " ";
				var szTabla = "DCOMPONENTES";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                       
    					for (var i = 0; i < j.length; i++) 
    					{  
    						$('#tblComponente').dataTable().fnAddData([  j[i].Col0 , j[i].Col1 ]);
						}
		         });   
		         
		        var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " ";
				var szTabla = "DSUBCOMPONENTES";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                      
    					for (var i = 0; i < j.length; i++) 
    					{   
    						arrSubComponentes[arrSubComponentes.length] = j[i].Col0 + "||" + j[i].Col1 + "||" + j[i].Col2;
						}
		         }); 
		         
		        var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " ";
				var szTabla = "DCATEGORIAS";  
				                                                                                       
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{                       
    					for (var i = 0; i < j.length; i++) 
    					{  
    						$('#MontoMientras').val(j[i].Col3); 
    						$('.currency').formatCurrency(); 
    						arrCategorias[arrCategorias.length] = j[i].Col0 + "||" + j[i].Col1 + "||" + j[i].Col2 + "||" + $('#MontoMientras').val() + "||" + j[i].Col4 + "||" + j[i].Col5 + "||" + j[i].Col6;
						}
						CalculaAcumulado();

						$('.currency').formatCurrency();
		         	}); 
				
				//$("input").attr("readonly", true);
				$("input").css("background", "#f0f0f0");
				
				$("input").attr("disabled", true);
				
				$("select").attr("disabled", true);
				$("select").css("background", "#f0f0f0");
				
				$("textarea").attr("disabled", true);
				$("textarea").css("background", "#f0f0f0");
				
				$("button").attr("disabled", true);
				$("#pbCancelar").removeAttr('disabled');

				CambiaFormatoFecha($("#FechaFirma"));
				CambiaFormatoFecha($("#FechaEfectividad"));
				CambiaFormatoFecha($("#FechaTerminacion"));
				CambiaFormatoFecha($("#FechaLimiteDesembolsar"));
				
				CambiaFormatoFecha($("#EntradaEnVigorModificacion"));
				CambiaFormatoFecha($("#FechaCancelacionParcial"));
				CambiaFormatoFecha($("#FechaTerminacionActualizada"));
				CambiaFormatoFecha($("#FechaLimiteDesembolsoActualizada"));
				
			}
			
			if (gsOperacion == 'C' )
			{
				$("#lbOperacion").text(" (Cambiar)");
							
				$("#EntradaEnVigorModificacion").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaCancelacionParcial").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaTerminacionActualizada").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
				$("#FechaLimiteDesembolsoActualizada").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});

				$("#EntradaEnVigorModificacion").removeAttr('disabled');
				$("#FechaCancelacionParcial").removeAttr('disabled');
				$("#FechaTerminacionActualizada").removeAttr('disabled');
				$("#FechaLimiteDesembolsoActualizada").removeAttr('disabled');

				$("#NoDoctoModificacion").removeAttr('disabled');
				$("#MontoCancelado").removeAttr('disabled');
				$("#MontoIncremento").removeAttr('disabled');
				$("#cbSubComponente").removeAttr('disabled');
				
				$("#NoDoctoModificacion").css("background", "white");
				$("#MontoCancelado").css("background", "white");
				$("#MontoIncremento").css("background", "white");	
				$("#cbSubComponente").css("background", "white");		

				$("button").removeAttr('disabled');

				$("#pbAceptar").css("visibility","visible");
				$("#pbCancelar").val("Cancelar");
								
				$("#NoDoctoModificacion").focus();
			}
			
			if (gsOperacion == 'B' )
			{
				$("#lbOperacion").text(" (Borrar)");
				
				$("#pbAceptar").removeAttr('disabled');
				$("#pbAceptar").css("visibility","visible");
				$("#pbCancelar").val("Cancelar");
			}
			
			$("#imgPlayStop").css("visibility", "hidden");
		});
		function formSubmited() {
                alert("Prestamo enviado!");
            }

		function fnOrdenaCombo(objCombo)
		{
			$(objCombo).append($( objCombo + " option").remove().sort(function(a, b) {
				var at = $(a).text(), bt = $(b).text();
				return (at > bt)?1:((at < bt)?-1:0);
			}));		
		}
		
		
/*******************************************************************************************************************************************************/
		
		$(function() {

			$( "#dialog:ui-dialog" ).dialog( "destroy" );

			var NumeroPrestamo = $( "#NumeroPrestamo" ),
				NombrePrestamo = $( "#NombrePrestamo" ),
				Objetivo = $( "#Objetivo" ),
				FechaFirma = $( "#FechaFirma" ),
				FechaEfectividad = $( "#FechaEfectividad" ),
				FechaTerminacion = $( "#FechaTerminacion" ),
				FechaLimiteDesembolsar = $( "#FechaLimiteDesembolsar" ),
				MontoOriginal = $( "#MontoOriginal" ),		
				
				NoDoctoModificacion = $( "#NoDoctoModificacion" ),	
				EntradaEnVigorModificacion = $( "#EntradaEnVigorModificacion" ),	
				FechaCancelacionParcial = $( "#FechaCancelacionParcial" ),	
				FechaTerminacionActualizada = $( "#FechaTerminacionActualizada" ),	
				FechaLimiteDesembolsoActualizada = $( "#FechaLimiteDesembolsoActualizada" ),	
				MontoIncremento = $( "#MontoIncremento" ),	
				MontoCancelado = $( "#MontoCancelado" ),			
				
				allFields = $( [] ).add( NumeroPrestamo ).add( NombrePrestamo ).add( Objetivo ).add( FechaFirma )
								   .add( FechaEfectividad ).add( FechaTerminacion ).add(FechaLimiteDesembolsar).add(MontoOriginal)
								   .add(NoDoctoModificacion).add(EntradaEnVigorModificacion).add(FechaCancelacionParcial).add(FechaTerminacionActualizada)
								   .add(FechaLimiteDesembolsoActualizada).add(MontoIncremento).add(MontoCancelado),
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

			$( "#dialog-form-Componente" ).dialog({
				autoOpen: false,
				height: 200,
				width: 800,
				modal: true,
				buttons: {
					"Aceptar": function() {
					
						if ($( "#lbAcciónComponente").text()=="Agregando un nuevo Componente")
						{
							// quita espacios izq-der y convierte a mayusculas
							$("#txtNumeroComponente").val($.trim($("#txtNumeroComponente").val()).toUpperCase());
							$("#txtNombreComponente").val($.trim($("#txtNombreComponente").val()).toUpperCase());
										
							if ($("#txtNumeroComponente").val()=="")				
							{
								alert( "El Número de Componente es un dato requerido.");							
								$("#txtNumeroComponente").focus();
								return;	
							}
							if ($("#txtNombreComponente").val()=="")				
							{
								alert( "El Nombre del Componente es un dato requerido.");
								$("#txtNombreComponente").focus();
								return;	
							}
							
							// Valida antes de insertar que no se repita en el grid
							var aTrs = oTableComponente.fnGetNodes();    
							var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
							
							for ( var i=0 ; i<aTrs.length ; i++ )     
							{
								if ($("#txtNumeroComponente").val() == rowsTbl[i][0])   
								{
									$("#txtNumeroComponente").focus();
									alert("Error. \rNúmero de Componente existente.");
									return;
								}      
								if ($("#txtNombreComponente").val() == rowsTbl[i][1])   
								{
									$("#txtNombreComponente").focus();
									alert("Error. \rNombre de Componente existente.");
									return;
								}  							    
							}   		
							
							// inserta en grid
							$('#tblComponente').dataTable().fnAddData([ 
								$("#txtNumeroComponente").val(), 
								$("#txtNombreComponente").val()
								]);
								
							if (gsOperacion == 'C' )
							{
								arrComponentesCambio[arrComponentesCambio.length] = $("#txtNumeroComponente").val() + "||" + $("#txtNombreComponente").val() + "||nuevo";
							}
						}
						else		// borrado de componente
						{
							var aTrs = $('#tblComponente').dataTable().fnGetNodes();           
							for ( var i=aTrs.length ; i>=0; i-- )     
							{         
								if ( $(aTrs[i]).hasClass('row_selected') )         
								{             
									$('#tblComponente').dataTable().fnDeleteRow( i ); 
									
									if (gsOperacion == 'C' )
									{
										arrComponentesCambio[arrComponentesCambio.length] = $("#txtNumeroComponente").val() + "||" + $("#txtNombreComponente").val() + "||borra";
									}
								}     
							} 
							
							for (i=0;i<arrSubComponentes.length;i++)
							{
								var res = arrSubComponentes[i].split("||");   
								if ($("#txtNumeroComponente").val() == res[0])
								{
									arrSubComponentes[i] = "";
									if (gsOperacion == 'C' )
									{
										arrSubComponentesCambio[arrSubComponentesCambio.length] = res[0] + "||" + res[1] + "||" + res[2] + "||borra";
									}
								}
							}
							for (i=0;i<arrCategorias.length;i++)
							{
								var res = arrCategorias[i].split("||");   
								if ($("#txtNumeroComponente").val() == res[0])
								{
									arrCategorias[i] = "";
									if (gsOperacion == 'C' )
									{
										arrCategoriasCambio[arrCategoriasCambio.length] = res[0] + "||" + res[1] + "||" + res[2]  + "||" + res[3] + "||" + res[4] + "||" + res[5] + "||" + res[6] +"||borra";
									}
								}
							}
							
							$('#tblSubComponente').dataTable().fnClearTable();
							$('#tblCategoria').dataTable().fnClearTable();
							
							$("#idComponente").val("");
						}
						
						$("#txtNumeroComponente").val(""); 
						$("#txtNombreComponente").val("");
							
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

			$( "#pbAgregarComponente" )
				.click(function() {

					$( "#lbAcciónComponente").text("Agregando un nuevo Componente");
					
					$("#txtNumeroComponente").removeAttr('disabled');
					$("#txtNumeroComponente").css("background","white");
					$("#txtNombreComponente").removeAttr('disabled');
					$("#txtNombreComponente").css("background","white");
					
					$("#txtNumeroComponente").val("");
					$("#txtNombreComponente").val("");  
					
					$( "#dialog-form-Componente" ).dialog( "open" );	
					$("#txtNumeroComponente").focus();			
				});
				
			$( "#pbBorrarComponente" )
				.click(function() {

					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();    
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNumeroComponente").val(rowsTbl[i][0]);
							$("#txtNombreComponente").val(rowsTbl[i][1]);  
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						$("#idComponente").val("");
						alert("Debe seleccionar un Componente antes para poder borrarlo.");
						return;
					}
				
					if ($("#id_prestamo").val() != "")
					{
						var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " AND id_componentetecnico = '" + $("#txtNumeroComponente").val() + "'";
						var szTabla = "DNOCONTRATO";                                                                                         	
						$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
							function(j)
							{         
		    					if (j.length>0)
								{
									alert("¡Advertencia! \r\rEste Componente tiene Contratos asignados,\rpor lo que NO PUEDE SER BORRADO.");
									return;
								}      
								else
								{
									$("#idComponente").val("");
						
									$( "#lbAcciónComponente").text("Esta acción eliminará las dependencias de SubComponentes y Categorías. ¿Esta seguro de Borrar el Componente?");
									
									$("#txtNumeroComponente").attr("disabled", "disabled");
									$("#txtNumeroComponente").css("background","#f0f0f0");
									$("#txtNombreComponente").attr("disabled", "disabled");
									$("#txtNombreComponente").css("background","#f0f0f0");
				
									$("#dialog-form-Componente").dialog( "open" );	
									$("#txtNumeroComponente").focus();		
								}
				         }); 
					}
					else
					{
						$("#idComponente").val("");
			
						$( "#lbAcciónComponente").text("Esta acción eliminará las dependencias de SubComponentes y Categorías. ¿Esta seguro de Borrar el Componente?");
						
						$("#txtNumeroComponente").attr("disabled", "disabled");
						$("#txtNumeroComponente").css("background","#f0f0f0");
						$("#txtNombreComponente").attr("disabled", "disabled");
						$("#txtNombreComponente").css("background","#f0f0f0");
	
						$("#dialog-form-Componente").dialog( "open" );	
						$("#txtNumeroComponente").focus();
					}
				});

			$( "#dialog-form-SubComponente" ).dialog({
				autoOpen: false,
				height: 220,
				width: 800,
				modal: true,
				buttons: {
					"Aceptar": function() {
				
						if ($( "#lbAcciónSubComponente").text()=="Agregando un nuevo SubComponente")
						{
							// quita espacios izq-der y convierte a mayusculas
							$("#txtNumeroSubComponente").val($.trim($("#txtNumeroSubComponente").val()).toUpperCase());
							$("#txtNombreSubComponente").val($.trim($("#txtNombreSubComponente").val()).toUpperCase());
										
							if ($("#txtNumeroSubComponente").val()=="")				
							{
								alert( "El Número de SubComponente es un dato requerido.");							
								$("#txtNumeroSubComponente").focus();
								return;	
							}
							if ($("#txtNombreSubComponente").val()=="")				
							{
								alert( "El Nombre del SubComponente es un dato requerido.");
								$("#txtNombreSubComponente").focus();
								return;	
							}
							
							// Valida antes de insertar que no se repita en el grid
							var aTrs = oTableSubComponente.fnGetNodes();    
							var rowsTbl = $("#tblSubComponente").dataTable().fnGetData();      
							
							for ( var i=0 ; i<aTrs.length ; i++ )     
							{
								if ($("#txtNumeroSubComponente").val() == rowsTbl[i][0])   
								{
									$("#txtNumeroSubComponente").focus();
									alert("Error. \rNúmero de SubComponente existente.");
									return;
								}      
								if ($("#txtNombreSubComponente").val() == rowsTbl[i][1])   
								{
									$("#txtNombreSubComponente").focus();
									alert("Error. \rNombre de SubComponente existente.");
									return;
								}  							    
							}   		
							
							// inserta en grid
							$('#tblSubComponente').dataTable().fnAddData([ 
								$("#txtNumeroSubComponente").val(), 
								$("#txtNombreSubComponente").val(),
								"0"
								]);
								
							arrSubComponentes[arrSubComponentes.length] = $("#idComponente").val() + "||" + $("#txtNumeroSubComponente").val() + "||" + $("#txtNombreSubComponente").val();
							
							if (gsOperacion == 'C' )
							{
								arrSubComponentesCambio[arrSubComponentesCambio.length] = $("#idComponente").val() + "||" + $("#txtNumeroSubComponente").val() + "||" + $("#txtNombreSubComponente").val() + "||nuevo";
							}
						}
						else	// Borrar SubComponente
						{						
							var aTrs = $("#tblSubComponente").dataTable().fnGetNodes();           
							for ( var i=aTrs.length ; i>=0; i-- )     
							{         
								if ( $(aTrs[i]).hasClass('row_selected') )         
								{             
									$('#tblSubComponente').dataTable().fnDeleteRow( i ); 
								}     
							} 
							for (i=0;i<arrSubComponentes.length;i++)
							{
								if (arrSubComponentes[i] == $("#idComponente").val() + "||" + $("#txtNumeroSubComponente").val() + "||" + $("#txtNombreSubComponente").val())
								{
									if (gsOperacion == 'C' )
									{
										arrSubComponentesCambio[arrSubComponentesCambio.length] = $("#idComponente").val() + "||" + $("#txtNumeroSubComponente").val() + "||" + $("#txtNombreSubComponente").val() + "||borra";
									}
									arrSubComponentes[i]="";
								}
							}
							
							for (i=0;i<arrCategorias.length;i++)
							{
								if ( arrCategorias[i] != "" )
								{
									//var szComillas;
									var res = arrCategorias[i].split("||");   
									
									if ($("#txtNumeroSubComponente").val() == res[5])
									{
										arrCategorias[i] = res[0] + "||" + res[1] + "||" + res[2] + "||" + res[3] + "||" + res[4] + "||" + "" + "||" + res[6];
										alert("El SubComponete " + res[5] + " fue eliminado de la Categoría " + res[1]);
									}
								}
							}
						}
						
						$("#txtNumeroSubComponente").val(""); 
						$("#txtNombreSubComponente").val("");
						
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

			$( "#pbAgregarSubComponente" )
				.click(function() {
				
					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();     
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#idComponente").val(rowsTbl[i][0]);
							$("#txtNombreComponenteSubComponente").val(rowsTbl[i][0] + " - " + rowsTbl[i][1]);
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						$("#idComponente").val("");
						alert("Debe seleccionar un Componente para realizar esta acción.");
						return;
					}
					
					$( "#lbAcciónSubComponente").text("Agregando un nuevo SubComponente");
					
					$("#txtNumeroSubComponente").removeAttr('disabled');
					$("#txtNumeroSubComponente").css("background","white");
					$("#txtNombreSubComponente").removeAttr('disabled');
					$("#txtNombreSubComponente").css("background","white");
					
					$("#txtNumeroSubComponente").val("");
					$("#txtNombreSubComponente").val("");  
					
					$( "#dialog-form-SubComponente" ).dialog( "open" );	
					$("#txtNumeroSubComponente").focus();
								
				});
				
			$( "#pbBorrarSubComponente" )
				.click(function() {

					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();     
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#idComponente").val(rowsTbl[i][0]);
							$("#txtNombreComponenteSubComponente").val(rowsTbl[i][0] + " - " + rowsTbl[i][1]);
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{	
						$("#idComponente").val("");
						alert("Debe seleccionar un Componente para realizar esta acción.");
						return;
					}

					bSeleccionado = false;
					aTrs = oTableSubComponente.fnGetNodes();    
					rowsTbl = $("#tblSubComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNumeroSubComponente").val(rowsTbl[i][0]);
							$("#txtNombreSubComponente").val(rowsTbl[i][1]);  
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						alert("Debe seleccionar un SubComponente antes para poder borrarlo.");
						return;
					}
					
					$( "#lbAcciónSubComponente").text("¿Esta seguro de Borrar el SubComponente?");
					
					$("#txtNumeroSubComponente").attr("disabled", "disabled");
					$("#txtNumeroSubComponente").css("background","#f0f0f0");
					$("#txtNombreSubComponente").attr("disabled", "disabled");
					$("#txtNombreSubComponente").css("background","#f0f0f0");

					$("#dialog-form-SubComponente").dialog( "open" );	
					$("#txtNumeroSubComponente").focus();			
				});
			
			
			$( "#dialog-form-Categoria" ).dialog({
				autoOpen: false,
				height: 270,
				width: 800,
				modal: true,
				buttons: {
					"Aceptar": function() {
				
						if ($( "#lbAcciónCategoria").text()=="Agregando una nueva Categoría")
						{
							// quita espacios izq-der y convierte a mayusculas
							$("#txtNumeroCategoria").val($.trim($("#txtNumeroCategoria").val()).toUpperCase());
							$("#txtNombreCategoria").val($.trim($("#txtNombreCategoria").val()).toUpperCase());
							$("#MontoCategoriaOriginal").val($.trim($("#MontoCategoriaOriginal").val()).toUpperCase());
							$("#txtPorcentajeCategoriaOriginal").val($.trim($("#txtPorcentajeCategoriaOriginal").val()).toUpperCase());
										
							if ($("#txtNumeroCategoria").val()=="")				
							{
								alert( "El Número de Categoría es un dato requerido.");							
								$("#txtNumeroCategoria").focus();
								return;	
							}
							if ($("#txtNombreCategoria").val()=="")				
							{
								alert( "El Nombre de la Categoría es un dato requerido.");
								$("#txtNombreCategoria").focus();
								return;	
							}
							if ($("#MontoCategoriaOriginal").val()=="")				
							{
								alert( "El Monto Categoría Original es un dato requerido.");
								$("#MontoCategoriaOriginal").focus();
								return;	
							}
							if ($("#txtPorcentajeCategoriaOriginal").val()=="")				
							{
								alert( "El Porcentaje de Financiamiento Original es un dato requerido.");
								$("#txtPorcentajeCategoriaOriginal").focus();
								return;	
							}
							
							// Valida antes de insertar que no se repita en el grid
							var aTrs = oTableCategoria.fnGetNodes();    
							var rowsTbl = $("#tblCategoria").dataTable().fnGetData();      
							
							for ( var i=0 ; i<aTrs.length ; i++ )     
							{
								if ($("#txtNumeroCategoria").val() == rowsTbl[i][0])   
								{
									$("#txtNumeroCategoria").focus();
									alert("Error. \rNúmero Categoría existente.");
									return;
								}      
								if ($("#txtNombreCategoria").val() == rowsTbl[i][1])   
								{
									$("#txtNombreCategoria").focus();
									alert("Error. \rNombre de Categoría existente.");
									return;
								}  							    
							}   
							
							
							var szTempSubCompo = "";	
							
							if ($("#cbSubComponente").val() != "-Seleccionar-") szTempSubCompo = $("#cbSubComponente").val();
							
							// inserta en grid
							$('#tblCategoria').dataTable().fnAddData([ 
								$("#txtNumeroCategoria").val(), 
								$("#txtNombreCategoria").val(),
								$("#MontoCategoriaOriginal").val(),
								$("#txtPorcentajeCategoriaOriginal").val(),
								szTempSubCompo,
								"0","0"
								]);
								
							arrCategorias[arrCategorias.length] = $("#idComponente").val() + "||" + $("#txtNumeroCategoria").val() + "||" + $("#txtNombreCategoria").val() + "||" + $("#MontoCategoriaOriginal").val() + "||" + $("#txtPorcentajeCategoriaOriginal").val() + "||" + szTempSubCompo + "||0";
							
							if (gsOperacion == 'C' )
							{
								arrCategoriasCambio[arrCategoriasCambio.length] = $("#idComponente").val() + "||" + $("#txtNumeroCategoria").val() + "||" + $("#txtNombreCategoria").val() + "||" + $("#MontoCategoriaOriginal").val() + "||" + $("#txtPorcentajeCategoriaOriginal").val() + "||" + szTempSubCompo + "||0||nuevo";
							}
							
						}
						
						if ($( "#lbAcciónCategoria").text()=="¿Esta seguro de Borrar la Categoria?")   // Borra Categoría
						{
							var aTrs = $('#tblCategoria').dataTable().fnGetNodes();           
							for ( var i=aTrs.length ; i>=0; i-- )     
							{         
								if ( $(aTrs[i]).hasClass('row_selected') )         
								{             
									$('#tblCategoria').dataTable().fnDeleteRow( i ); 
								}     
							} 
							
							for (i=0;i<arrCategorias.length;i++)
							{
								var szTempSubCompo = "";
								if ($("#cbSubComponente").val() != "-Seleccionar-") szTempSubCompo = $("#cbSubComponente").val();
								if (arrCategorias[i] == $("#idComponente").val() + "||" + $("#txtNumeroCategoria").val() + "||" + $("#txtNombreCategoria").val() + "||" + $("#MontoCategoriaOriginal").val() + "||" + $("#txtPorcentajeCategoriaOriginal").val() + "||" + szTempSubCompo + "||" + $("#txtid_categoriaInversionPrestamo").val())  
								{
									arrCategorias[i]="";
									if (gsOperacion == 'C' )
									{
										arrCategoriasCambio[arrCategoriasCambio.length] = $("#idComponente").val() + "||" + $("#txtNumeroCategoria").val() + "||" + $("#txtNombreCategoria").val() + "||" + $("#MontoCategoriaOriginal").val() + "||" + $("#txtPorcentajeCategoriaOriginal").val() + "||" + szTempSubCompo  + "||" + $("#txtid_categoriaInversionPrestamo").val() + "||borra";
									}
								}
							}
						}
						
						if ($( "#lbAcciónCategoria").text()=="Modificando la Categoria")		// modificando la categoria
						{
							var aTrs = $('#tblCategoria').dataTable().fnGetNodes();           
							for ( var i=aTrs.length ; i>=0; i-- )     
							{         
								if ( $(aTrs[i]).hasClass('row_selected') )         
								{             
									$('#tblCategoria').dataTable().fnDeleteRow( i ); 
									var szTempSubCompo = "";								
									if ($("#cbSubComponente").val() != "-Seleccionar-") szTempSubCompo = $("#cbSubComponente").val();
									$('#tblCategoria').dataTable().fnAddData([ 
										$("#txtNumeroCategoria").val(), 
										$("#txtNombreCategoria").val(),
										$("#MontoCategoriaOriginal").val(),
										$("#txtPorcentajeCategoriaOriginal").val(),
										szTempSubCompo,
										"0","0"
										]);
										
									for (j=0;j<arrCategorias.length;j++)
									{
										var res = arrCategorias[j].split("||");
										
										if (res[1] ==  $("#txtNumeroCategoria").val())
										{
											arrCategorias[j] = $("#idComponente").val() + "||" + $("#txtNumeroCategoria").val() + "||" + $("#txtNombreCategoria").val() + "||" + $("#MontoCategoriaOriginal").val() + "||" + $("#txtPorcentajeCategoriaOriginal").val() + "||" + szTempSubCompo  + "||" + $("#txtid_categoriaInversionPrestamo").val();
											if (gsOperacion == 'C' )
											{
												arrCategoriasCambio[arrCategoriasCambio.length] = $("#idComponente").val() + "||" + $("#txtNumeroCategoria").val() + "||" + $("#txtNombreCategoria").val() + "||" + $("#MontoCategoriaOriginal").val() + "||" + $("#txtPorcentajeCategoriaOriginal").val() + "||" + szTempSubCompo  + "||" + $("#txtid_categoriaInversionPrestamo").val() + "||cambio";
											}
										}
									}
								}     
							} 
						}
						fnVerificaMontos();
						
						$("#txtNumeroCategoria").val(""); 
						$("#txtNombreCategoria").val("");
						
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

			$("#pbAgregarCategoria")
				.click(function() {
					
					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();     
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNombreComponenteCategoria").val(rowsTbl[i][0] + " - " + rowsTbl[i][1]);
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						$("#idComponente").val("");
						alert("Debe seleccionar un Componente para realizar esta acción.");
						return;
					}
					
					$('#cbSubComponente').empty();
					$('#cbSubComponente').append($('<option>', { value: null, text: "-Seleccionar-" }));
					for (i=0;i<arrSubComponentes.length;i++)
					{
						var res = arrSubComponentes[i].split("||");   
						if ($("#idComponente").val() == res[0])
						{							
							$('#cbSubComponente').append($('<option>', { value: res[1], text: res[1] + " - " + res[2] }));							
						}
					}
					
					$("#lbAcciónCategoria").text("Agregando una nueva Categoría");
					
					$("#txtNumeroCategoria").removeAttr('disabled');
					$("#txtNumeroCategoria").css("background","white");
					$("#txtNombreCategoria").removeAttr('disabled');
					$("#txtNombreCategoria").css("background","white");
					$("#MontoCategoriaOriginal").removeAttr('disabled');
					$("#MontoCategoriaOriginal").css("background","white");
					$("#txtPorcentajeCategoriaOriginal").removeAttr('disabled');
					$("#txtPorcentajeCategoriaOriginal").css("background","white");
					$("#cbSubComponente").removeAttr('disabled');
					$("#cbSubComponente").css("background","white");
					
					$("#txtNumeroCategoria").val("");
					$("#txtNombreCategoria").val("");  
					$("#MontoCategoriaOriginal").val("0.00"); 
					$("#txtPorcentajeCategoriaOriginal").val("0"); 
					
					$( "#dialog-form-Categoria" ).dialog( "open" );	
					$("#txtNumeroCategoria").focus();
								
				});
				
			$( "#pbBorrarCategoria" )
				.click(function() {

					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();     
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNombreComponenteCategoria").val(rowsTbl[i][0] + " - " + rowsTbl[i][1]);
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						$("#idComponente").val("");
						alert("Debe seleccionar un Componente para realizar esta acción.");
						return;
					}
					
					$('#cbSubComponente').empty();
					$('#cbSubComponente').append($('<option>', { value: "", text: "-Seleccionar-" }));
					for (i=0;i<arrSubComponentes.length;i++)
					{
						var res = arrSubComponentes[i].split("||");   
						if ($("#idComponente").val() == res[0])
						{							
								$('#cbSubComponente').append($('<option>', { value: res[1], text: res[1] + " - " + res[2] }));							
						}
					}

					bSeleccionado = false;
					aTrs = oTableCategoria.fnGetNodes();    
					rowsTbl = $("#tblCategoria").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNumeroCategoria").val(rowsTbl[i][0]);
							$("#txtNombreCategoria").val(rowsTbl[i][1]);  
							$("#MontoCategoriaOriginal").val(rowsTbl[i][2]);
							$("#txtPorcentajeCategoriaOriginal").val(rowsTbl[i][3]);
							$("#cbSubComponente").val(rowsTbl[i][4]);
							$("#txtid_categoriaInversionPrestamo").val(rowsTbl[i][5]);
							bSeleccionado = true; 		
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						alert("Debe seleccionar una Categoría antes para poder borrarla.");
						return;
					}
					
					$( "#lbAcciónCategoria").text("¿Esta seguro de Borrar la Categoria?");
					
					$("#txtNumeroCategoria").attr("disabled", "disabled");
					$("#txtNumeroCategoria").css("background","#f0f0f0");
					$("#txtNombreCategoria").attr("disabled", "disabled");
					$("#txtNombreCategoria").css("background","#f0f0f0");
					$("#MontoCategoriaOriginal").attr("disabled", "disabled");
					$("#MontoCategoriaOriginal").css("background","#f0f0f0");
					$("#txtPorcentajeCategoriaOriginal").attr("disabled", "disabled");
					$("#txtPorcentajeCategoriaOriginal").css("background","#f0f0f0");
					$("#cbSubComponente").attr("disabled", "disabled");
					$("#cbSubComponente").css("background","#f0f0f0");
					
					$("#dialog-form-Categoria").dialog( "open" );	
					$("#txtNumeroCategoria").focus();			
				});
				
			$( "#pbCambiarCategoria" )
				.click(function() {

					var bSeleccionado = false;
					var aTrs = oTableComponente.fnGetNodes();     
					var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNombreComponenteCategoria").val(rowsTbl[i][0] + " - " + rowsTbl[i][1]);
							bSeleccionado = true; 
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						$("#idComponente").val("");
						alert("Debe seleccionar un Componente para realizar esta acción.");
						return;
					}
					
					$('#cbSubComponente').empty();
					$('#cbSubComponente').append($('<option>', { value: "", text: "-Seleccionar-" }));
					for (i=0;i<arrSubComponentes.length;i++)
					{
						var res = arrSubComponentes[i].split("||");   
						if ($("#idComponente").val() == res[0])
						{							
								$('#cbSubComponente').append($('<option>', { value: res[1], text: res[1] + " - " + res[2] }));							
						}
					}

					bSeleccionado = false;
					aTrs = oTableCategoria.fnGetNodes();    
					rowsTbl = $("#tblCategoria").dataTable().fnGetData();      
					
					for ( var i=0 ; i<aTrs.length ; i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							$("#txtNumeroCategoria").val(rowsTbl[i][0]);
							$("#txtNombreCategoria").val(rowsTbl[i][1]);  
							$("#MontoCategoriaOriginal").val(rowsTbl[i][2]);
							$("#txtPorcentajeCategoriaOriginal").val(rowsTbl[i][3]);
							$("#cbSubComponente").val(rowsTbl[i][4]);
							$("#txtid_categoriaInversionPrestamo").val(rowsTbl[i][5]);
							bSeleccionado = true; 		
							break;     
						}     
					}   		
					
					if (!bSeleccionado)	
					{
						alert("Debe seleccionar una Categoría antes para poder borrarla.");
						return;
					}
					
					$( "#lbAcciónCategoria").text("Modificando la Categoria");
					
					$("#txtNumeroCategoria").attr("disabled", "disabled");
					$("#txtNumeroCategoria").css("background","#f0f0f0");
					$("#txtNombreCategoria").attr("disabled", "disabled");
					$("#txtNombreCategoria").css("background","#f0f0f0");
					$("#MontoCategoriaOriginal").removeAttr('disabled');
					$("#MontoCategoriaOriginal").css("background","white");
					$("#txtPorcentajeCategoriaOriginal").attr("disabled", "disabled");
					$("#txtPorcentajeCategoriaOriginal").css("background","#f0f0f0");
					$("#cbSubComponente").attr("disabled", "disabled");
					$("#cbSubComponente").css("background","#f0f0f0");

					$("#dialog-form-Categoria").dialog( "open" );	
					$("#txtNumeroCategoria").focus();			
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

				bValid = bValid && checkRequerido( NumeroPrestamo, "Número Préstamo" );
				bValid = bValid && checkRequerido( NombrePrestamo, "Nombre Préstamo" );
				bValid = bValid && checkRequerido( Objetivo, "Objetivo" );
				bValid = bValid && checkRequerido( FechaFirma, "Fecha Firma" );
				bValid = bValid && checkRequerido( FechaEfectividad, "Fecha Efectividad" );
				bValid = bValid && checkRequerido( FechaTerminacion, "Fecha Terminación" );
				bValid = bValid && checkRequerido( FechaLimiteDesembolsar, "Fecha Límite para Desembolsar" );
				bValid = bValid && checkRequerido( MontoOriginal, "Monto Original del Prestamo" );
								
				if (gsOperacion == 'C')
				{
					bValid = bValid && checkRequerido( NoDoctoModificacion, "Número de Documento de Modificación" );
					bValid = bValid && checkRequerido( EntradaEnVigorModificacion, "Fecha de Entrada en Vigor de la Modificación" );
					bValid = bValid && checkRequerido( MontoIncremento, "Monto Incremental" );
					bValid = bValid && checkRequerido( MontoCancelado, "Monto Cancelado" );
				}
								
				if (!bValid) return;
				
				
				if ($("#id_ofi").val() == 0)
				{
					alert("Organismo Financiero Internacional es un dato requerido.");
					$("#id_ofi").focus();
					return;
				}
				if ($("#id_agentefinanciero").val() == 0)
				{
					alert("Agente Financiero es un dato requerido.");
					$("#id_agentefinanciero").focus();
					return;
				}
				
								// valida que exista por lo menos un programa presupuestario
				var aTrsPP = oTablePP.fnGetNodes();     
				var rowsTblPP = $("#tblProgramasPresupuestarios").dataTable().fnGetData();    
				bValid = false;  
				
				for ( var i=0 ; i<aTrsPP.length ; i++ )     
				{        
					if($("#chk"+rowsTblPP[i][1]).is(':checked'))
					{
						bValid = true;
					} 
				}   	
				if (!bValid)
				{
					alert("Programa Presupuestario es un dato requerido.");
					$("#tblProgramasPresupuestarios").focus();
					return;
				}		
				
				if ($("#cUnidadResponsable").val() == 0)
				{
					alert("Unidad Responsable es un dato requerido.");
					$("#cUnidadResponsable").focus();
					return;
				}
				
				if (Number($("#MontoOriginal").val()) <= 0)
				{
					alert("El Monto Original del Prestamo debe ser un valor mayor de 0.");
					$("#MontoOriginal").focus();
					return;
				}
				
				
				var szFecha1 = $("#FechaFirma").val();
				var szFecha2 = $("#FechaEfectividad").val();
				var szFecha3 = $("#FechaTerminacion").val();
				var szFecha4 = $("#FechaLimiteDesembolsar").val();
				
				szFecha1 = szFecha1.substr(6,4) + "/" + szFecha1.substr(3,2) + "/" + szFecha1.substr(0,2);  
				szFecha2 = szFecha2.substr(6,4) + "/" + szFecha2.substr(3,2) + "/" + szFecha2.substr(0,2); 
				szFecha3 = szFecha3.substr(6,4) + "/" + szFecha3.substr(3,2) + "/" + szFecha3.substr(0,2);
				szFecha4 = szFecha4.substr(6,4) + "/" + szFecha4.substr(3,2) + "/" + szFecha4.substr(0,2);
				
				var dtf = new Date(szFecha1);
				var dte = new Date(szFecha2);
				var dtt = new Date(szFecha3);
				var dtl = new Date(szFecha4);

				if (DateDiff(dte, dtf) < 0)
				{
					alert("La Fecha de Efectividad del Préstamo debe ser mayor o igual a la Fecha de Firma del Préstamo.");
					$("#FechaEfectividad").focus();
					return;				
				}				
				if (DateDiff(dtt, dte) <= 0)
				{
					alert("La Fecha de Terminación del Préstamo debe ser mayor a la Fecha de Efectividad del Préstamo.");
					$("#FechaTerminacion").focus();
					return;				
				}
				if (DateDiff(dtl, dtt) <= 0)
				{
					alert("La Fecha Límite para Desembolsar del Préstamo debe ser mayor a la Fecha de Terminación del Préstamo.");
					$("#FechaLimiteDesembolsar").focus();
					return;				
				}		
				
				
				
				// valida que exista por lo menos un componente
				var aTrs = oTableComponente.fnGetNodes();     
				var rowsTbl = $("#tblComponente").dataTable().fnGetData();      
				
				if  ( aTrs.length <= 0)  
				{
					alert("Un Préstamo debe tener por lo menos un Componente.");
					$("#pbAgregarComponente").focus();
					return;
				}
				
				var bExisteCategoria = false;
				// valida que exista por lo menos una categoria por componente          
				for ( var i=0; i<aTrs.length ; i++ )     
				{         
					bExisteCategoria = false;
					for (j=0;j<arrCategorias.length;j++)
					{	
						if (arrCategorias[j]!="")
						{
							var res = arrCategorias[j].split("||");   
							if (rowsTbl[i][0] == res[0])
							{
									bExisteCategoria = true;
									break;
							}
						}
					}  
					if  (!bExisteCategoria)  
					{
						$(oTableComponente.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$(aTrs[i]).addClass('row_selected');
						$("#aTab1").click();
						$("#tblCategoria").dataTable().fnClearTable();
						alert("Es requisito registrar por lo menos una Categoría por Componente.");
						$("#pbAgregarCategoria").focus();
						return;
					}
				} 
				
				if (!fnVerificaMontos()) 
				{
					alert($("#lbMontosOk").text());
					$("#pbAgregarCategoria").focus();
					return;
				}
				
				$("#id_prestamo").removeAttr('disabled');
	
				if (gsOperacion == 'A')
				{
					var szWhere = " NumeroPrestamo = '" + $("#NumeroPrestamo").val() + "' ";
					var szTabla = "DNUMEROPRESTAMO";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{               
							if (j.length>0)
							{
								alert("Error. \rYa existe ese Número de Préstamo.");
								$("#NumeroPrestamo").focus();
								return;
							}      
							else
							{
						        szWhere = " NombrePrestamo = '" + $("#NombrePrestamo").val() + "' ";
								szTabla = "DNOMBREPRESTAMO";                                                                                         
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
									function(j)
									{               
										if (j.length>0)
										{
											alert("Error. \rYa existe ese Nombre de Préstamo.");
											$("#NombrePrestamo").focus();
											return;
										}   
										else
										{
											$("#Estatus").val("A");
						
											queryFormPost("dPrestamoCreate", {async: false});
											queryFormPost("dPrestamosRead", {async: false});
											
											// aqui inserta los valores de programas presupuestarios
											for ( var i=0 ; i<aTrsPP.length ; i++ )     
											{        
												if($("#chk"+rowsTblPP[i][1]).is(':checked'))
												{
													$("#tblSubComponente").dataTable().fnAddData([ 
														rowsTblPP[i][0],
														"<input type='text' id='cProgramaPresupuestario' name='cProgramaPresupuestario' value='" + rowsTblPP[i][1] + "'/>",
														rowsTblPP[i][2]
															]);   
												} 
											}   	
											queryFormPost("dPrestamoProgramaPresupuestarioDelete,dPrestamoProgramaPresupuestarioCreate", {async: false});

											// aqui inserta los valores de componente sub y categoria											
											var bSeleccionado = false;
											var aTrs = oTableComponente.fnGetNodes();    
											var rowsTbl = $("#tblComponente").dataTable().fnGetData();  
											var iSize = aTrs.length;   
											
											for ( var i=0 ; i<iSize ; i++ )     
											{         
												$('#tblComponente').dataTable().fnAddData([ 
													"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + rowsTbl[i][0] + "'/>",
													"<input type='text' id='componentetecnico' name='componentetecnico' value='" + rowsTbl[i][1] + "'/>"
												]);   
											}   		
											queryFormPost("dComponente_TecnicoCreate", {async: false});		
										
											$('#tblComponente').dataTable().fnClearTable();
											$('#tblSubComponente').dataTable().fnClearTable();
											$('#tblCategoria').dataTable().fnClearTable();
											
											for (i=0;i<arrSubComponentes.length;i++)
											{
												if ( arrSubComponentes[i] != "" )
												{
													var res = arrSubComponentes[i].split("||");   
													$('#tblSubComponente').dataTable().fnAddData([ 
														"<input type='text' id='id_subcomponentetecnico' name='id_subcomponentetecnico' value='" + res[1] + "'/>",
														"<input type='text' id='subcomponentetecnico' name='subcomponentetecnico' value='" + res[2] + "'/>",
														"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>"
													]); 
												}
											}
											
											$('#tblSubComponente').dataTable().fnSetColumnVis( 2, true ); 
											if (arrSubComponentes.length>0) 
												queryFormPost("dSubcomponente_TecnicoCreate", {async: false});	
						
											for (i=0;i<arrCategorias.length;i++)
											{
												if ( arrCategorias[i] != "" )
												{
													var szComillas;
													var res = arrCategorias[i].split("||");   
													var szTemp = res[3];
													szTemp = (szTemp.replace("$","")).replace("$","");	
													szTemp = (szTemp.replace(",","")).replace(",","");
													if (res[5]==null) 
														szComillas = "<input type='text' id='id_SubComponenteTecnico' name='id_SubComponenteTecnico' value=null/>";
													else
														szComillas = "<input type='text' id='id_SubComponenteTecnico' name='id_SubComponenteTecnico' value='" + res[5] + "'/>";
													$('#tblCategoria').dataTable().fnAddData([ 
														"<input type='text' id='NumeroCategoria' name='NumeroCategoria' value='" + res[1] + "'/>",
														"<input type='text' id='NombreCategoria' name='NombreCategoria' value='" + res[2] + "'/>",
														"<input type='text' id='Monto' name='Monto' value='" + szTemp + "'/>",
														"<input type='text' id='PorcentajeFinanciamiento' name='PorcentajeFinanciamiento' value='" + res[4] + "'/>",
														"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>",
														"<input type='text' id='id_SubComponenteTecnico' name='id_SubComponenteTecnico' value='" + res[5] + "'/>","0"
													]); 
												}
											}

											$('#tblSubComponente').dataTable().fnClearTable();
											$('#tblCategoria').dataTable().fnSetColumnVis( 5, true );
											queryFormPost("dCategoriaInversion_prestamoCreate", {async: false}); 
											alert("¡Operación Exitosa! \r\rEl Préstamo ha sido agregado.");
											location.href = 'CaratulaPrestamoGrid.jsp';
										}     
						         }); 							
							}  
			         });   
				}
		
				if (gsOperacion == 'C')
				{
					///////////////////////// Inserta categorias - subcomponentes y componentes ////////////////////////////////////////
					var iCuantos = 0;
					$('#tblComponente').dataTable().fnClearTable();				// inserta componentes nuevos por cambio
					for ( var i=0 ; i<arrComponentesCambio.length ; i++ )     
					{         
						var res = arrComponentesCambio[i].split("||");
						if (res[2] == "nuevo")
						{
							$('#tblComponente').dataTable().fnAddData([ 
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>",
								"<input type='text' id='componentetecnico' name='componentetecnico' value='" + res[1] + "'/>"
							]);   
							iCuantos = iCuantos + 1;
						}
					}   		
					if (iCuantos > 0) queryFormPost("dComponente_TecnicoCreate", {async: false});	
				
					iCuantos = 0;
					$('#tblComponente').dataTable().fnClearTable();
					$('#tblSubComponente').dataTable().fnClearTable();			// inserta subcomponentes nuevos por cambio
					for ( var i=0 ; i<arrSubComponentesCambio.length ; i++ )     
					{         
						var res = arrSubComponentesCambio[i].split("||");
						if (res[3] == "nuevo")
						{ 
							$('#tblSubComponente').dataTable().fnAddData([ 
								"<input type='text' id='id_subcomponentetecnico' name='id_subcomponentetecnico' value='" + res[1] + "'/>",
								"<input type='text' id='subcomponentetecnico' name='subcomponentetecnico' value='" + res[2] + "'/>",
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>"
							]); 
							iCuantos = iCuantos + 1;
						}
					}   		
					$('#tblSubComponente').dataTable().fnSetColumnVis( 2, true ); 
					if (iCuantos > 0) queryFormPost("dSubcomponente_TecnicoCreate", {async: false});		
			
					iCuantos = 0;
					$('#tblComponente').dataTable().fnClearTable();
					$('#tblSubComponente').dataTable().fnClearTable();			// inserta categorias nuevos por cambio
					$('#tblCategoria').dataTable().fnClearTable();
					for ( var i=0 ; i<arrCategoriasCambio.length ; i++ )     
					{         
						var res = arrCategoriasCambio[i].split("||");  
						if (res[7] == "nuevo")
						{ 
							var szTemp = res[3];
							szTemp = (szTemp.replace("$","")).replace("$","");	
							szTemp = (szTemp.replace(",","")).replace(",","");
							$('#tblCategoria').dataTable().fnAddData([ 
								"<input type='text' id='NumeroCategoria' name='NumeroCategoria' value='" + res[1] + "'/>",
								"<input type='text' id='NombreCategoria' name='NombreCategoria' value='" + res[2] + "'/>",
								"<input type='text' id='Monto' name='Monto' value='" + szTemp + "'/>",
								"<input type='text' id='PorcentajeFinanciamiento' name='PorcentajeFinanciamiento' value='" + res[4] + "'/>",
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>",
								"<input type='text' id='id_SubComponenteTecnico' name='id_SubComponenteTecnico' value='" + res[5] + "'/>","0"
							]); 
							iCuantos = iCuantos + 1;
						}
					}   		
					$('#tblCategoria').dataTable().fnSetColumnVis( 5, true ); 
					$('#tblCategoria').dataTable().fnSetColumnVis( 6, true ); 
					if (iCuantos > 0) queryFormPost("dCategoriaInversion_prestamoCreate", {async: false});
					
		///////////////////////////////////////////////////////////  Modifica categorias  /////////////////////////////////////////////////////////////////		

					iCuantos = 0;
					$('#tblComponente').dataTable().fnClearTable();
					$('#tblSubComponente').dataTable().fnClearTable();			// inserta categorias nuevos por cambio
					$('#tblCategoria').dataTable().fnClearTable();
					for ( var i=0 ; i<arrCategoriasCambio.length ; i++ )     
					{         
						var res = arrCategoriasCambio[i].split("||");
						if (res[7] == "cambio")
						{ 
							var szTemp = res[3];
							szTemp = (szTemp.replace("$","")).replace("$","");	
							szTemp = (szTemp.replace(",","")).replace(",","");
							$('#tblCategoria').dataTable().fnAddData([ 
								"<input type='text' id='NumeroCategoria' name='NumeroCategoria' value='" + res[1] + "'/>",
								"<input type='text' id='NombreCategoria' name='NombreCategoria' value='" + res[2] + "'/>",
								"<input type='text' id='Monto' name='Monto' value='" + szTemp + "'/>",
								"<input type='text' id='PorcentajeFinanciamiento' name='PorcentajeFinanciamiento' value='" + res[4] + "'/>",
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>",
								"<input type='text' id='id_SubComponenteTecnico' name='id_SubComponenteTecnico' value='" + res[5] + "'/>","0"
							]); 
							iCuantos = iCuantos + 1;
						}
					}   		
					$('#tblCategoria').dataTable().fnSetColumnVis( 5, true ); 
					$('#tblCategoria').dataTable().fnSetColumnVis( 6, true ); 
					if (iCuantos > 0) queryFormPost("dCategoriaInversion_prestamoUpdate", {async: false});		

				
		////////////////////////////////////////////////// Borra categorias - subcomponentes y componentes ////////////////////////////////////////////////
					iCuantos = 0;
					$('#tblCategoria').dataTable().fnClearTable();			// borra categorias nuevos por cambio
					for ( var i=0 ; i<arrCategoriasCambio.length ; i++ )     
					{         
						var res = arrCategoriasCambio[i].split("||");			
						if (res[7] == "borra")
						{ 
							var szTemp = res[3];
							szTemp = (szTemp.replace("$","")).replace("$","");	
							szTemp = (szTemp.replace(",","")).replace(",","");
							$('#tblCategoria').dataTable().fnAddData([ 
								"<input type='text' id='NumeroCategoria' name='NumeroCategoria' value='" + res[1] + "'/>",
								"<input type='text' id='NombreCategoria' name='NombreCategoria' value='" + res[2] + "'/>",
								"<input type='text' id='Monto' name='Monto' value='" + szTemp + "'/>",
								"<input type='text' id='PorcentajeFinanciamiento' name='PorcentajeFinanciamiento' value='" + res[4] + "'/>",
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>",
								"<input type='text' id='id_SubComponenteTecnico' name='id_SubComponenteTecnico' value='" + res[5] + "'/>",
								"<input type='text' id='id_categoriaInversionPrestamo' name='id_categoriaInversionPrestamo' value='" + res[6] + "'/>"
							]); 
							iCuantos = iCuantos + 1;			
						}
					}   		
					$('#tblCategoria').dataTable().fnSetColumnVis( 5, true ); 
					$('#tblCategoria').dataTable().fnSetColumnVis( 6, true ); 
				
					if (iCuantos > 0) queryFormPost("dCategoriasXPrestamoXComponenteXCategoriaDelete", {async: false});	
					
					iCuantos = 0;
					$('#tblSubComponente').dataTable().fnClearTable();			// borra subcomponentes nuevos por cambio
					for ( var i=0 ; i<arrSubComponentesCambio.length ; i++ )     
					{         
						var res = arrSubComponentesCambio[i].split("||");				
						if (res[3] == "borra")
						{ 
							$('#tblSubComponente').dataTable().fnAddData([ 
								"<input type='text' id='id_subcomponentetecnico' name='id_subcomponentetecnico' value='" + res[1] + "'/>",
								"<input type='text' id='subcomponentetecnico' name='subcomponentetecnico' value='" + res[2] + "'/>",
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>"
							]); 
							iCuantos = iCuantos + 1;
						}
					}   		
					$('#tblSubComponente').dataTable().fnSetColumnVis( 2, true ); 
					if (iCuantos > 0) queryFormPost("dSubcomponenteTecnicoXComponenteXSubComponenteDelete", {async: false});	
				
					iCuantos = 0;
					$('#tblComponente').dataTable().fnClearTable();			// borra componentes tecnicos por cambio
					for ( var i=0 ; i<arrComponentesCambio.length ; i++ )     
					{         
						var res = arrComponentesCambio[i].split("||");
						if (res[2] == "borra")
						{
							$('#tblComponente').dataTable().fnAddData([ 
								"<input type='text' id='id_componentetecnico' name='id_componentetecnico' value='" + res[0] + "'/>",
								"<input type='text' id='componentetecnico' name='componentetecnico' value='" + res[1] + "'/>"
							]);
							iCuantos = iCuantos + 1;   
						}
					}   		
					if (iCuantos > 0) queryFormPost("dComponenteTecnicoXComponenteDelete", {async: false});		
					$('#tblComponente').dataTable().fnClearTable();
					
										// aqui inserta los valores de programas presupuestarios
					for ( var i=0 ; i<aTrsPP.length ; i++ )     
					{        
						if($("#chk"+rowsTblPP[i][1]).is(':checked'))
						{
							$("#tblSubComponente").dataTable().fnAddData([ 
							rowsTblPP[i][0],
							"<input type='text' id='cProgramaPresupuestario' name='cProgramaPresupuestario' value='" + rowsTblPP[i][1] + "'/>",
							rowsTblPP[i][2]
								]);   
						} 
					}   	
					queryFormPost("dPrestamoProgramaPresupuestarioDelete,dPrestamoProgramaPresupuestarioCreate", {async: false});
		
					queryFormPost("dPrestamoUpdate", {async: false});
					alert("¡Operación Exitosa! \r\rEl Préstamo ha sido cambiado.");
					location.href = 'CaratulaPrestamoGrid.jsp';
				}
				
				if (gsOperacion == 'B')
				{
					var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " ";
					var szTabla = "DNOCONTRATO";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{                       
	    					if (j.length>0)
							{
								alert("¡Advertencia! \r\rEste Préstamo tiene Contratos asignados,\rpor lo que NO PUEDE SER BORRADO.");
								return;
							}      
							else
							{
								queryFormPost("dPrestamoProgramaPresupuestarioDelete,dCategoriaInversionDelete,dSubcomponenteTecnicoDelete,dComponenteTecnicoDelete,dPrestamoDelete");
								alert("¡Operación Exitosa! \r\rEl Préstamo ha sido borrado.");
								location.href = 'CaratulaPrestamoGrid.jsp';
							}
			         }); 
				}

				});

			$("#pbCancelar")
				.button()
				.click(function() {
					location.href = 'CaratulaPrestamoGrid.jsp';
					
				});

		});


		function CalculaAcumulado()	
		{
			var szTemp = "";
			var MontoOriginal = 0;
			var MontoCancelado = 0;
			var MontoIncremento = 0;
			
			szTemp = $("#MontoOriginal").val();
			if (szTemp.length == 0) $("#MontoOriginal").val("$0.00");
			szTemp = $("#MontoOriginal").val();
			szTemp = (szTemp.replace("$","")).replace("$","");	
			szTemp = (szTemp.replace(",","")).replace(",","");
			MontoOriginal = Number(szTemp);
			
			szTemp = $("#MontoCancelado").val();
			if (szTemp.length == 0) $("#MontoCancelado").val("$0.00");
			szTemp = $("#MontoCancelado").val();
			szTemp = (szTemp.replace("$","")).replace("$","");	
			szTemp = (szTemp.replace(",","")).replace(",","");
			MontoCancelado = Number(szTemp);
			
			szTemp = $("#MontoIncremento").val();
			if (szTemp.length == 0) $("#MontoIncremento").val("$0.00");
			szTemp = $("#MontoIncremento").val();
			szTemp = (szTemp.replace("$","")).replace("$","");	
			szTemp = (szTemp.replace(",","")).replace(",","");
			MontoIncremento = Number(szTemp);
					
			$("#MontoActualizado").val(MontoOriginal + MontoIncremento - MontoCancelado );
			
			fnVerificaMontos();
			
		}
		
		function DateDiff(date1, date2) {
		    var datediff = date1.getTime() - date2.getTime(); //store the getTime diff - or +
		    return (datediff / (24*60*60*1000)); //Convert values to -/+ days and return value      
		}
		
		function fnVerificaMontos()
		{
			var nMontoCategoria = 0;
			var nMontoOriginal = 0;
			var nMontoActualizado = 0;
			var szTemp = "";
			
			for (i=0; i < arrCategorias.length; i++)
			{
				var res = arrCategorias[i].split("||");
				szTemp = res[3]; 
				if (res == "") szTemp = "0";
				szTemp = (szTemp.replace("$","")).replace("$","");	
				szTemp = (szTemp.replace(",","")).replace(",","");
			
				nMontoCategoria = nMontoCategoria + Number(szTemp);
	
			}
			$("#MontoMientras").val(nMontoCategoria); $('.currency').formatCurrency();
			
			if (gsOperacion == 'A')
			{
				szTemp = $("#MontoOriginal").val();
				if (szTemp.length == 0) $("#MontoOriginal").val("$0.00");
				szTemp = $("#MontoOriginal").val();
				szTemp = (szTemp.replace("$","")).replace("$","");	
				szTemp = (szTemp.replace(",","")).replace(",","");
				nMontoOriginal = Number(szTemp);
				if (nMontoOriginal != nMontoCategoria)
				{
					$("#lbMontosOk").text("La suma de los Montos de todas las Categorías (" + $("#MontoMientras").val() + "), debe ser igual que el Monto Original del Prestamo.");
					$("#lbMontosOk").css("color","red");
					return false;
				}
				$("#lbMontosOk").text("La suma de los Montos de todas las Categorías es igual que el Monto Original del Prestamo.");
				$("#lbMontosOk").css("color","#0066FF");
				return true;
			}
			
			if (gsOperacion == 'C')
			{
				szTemp = $("#MontoActualizado").val();
				if (szTemp.length == 0) $("#MontoActualizado").val("$0.00");
				szTemp = $("#MontoActualizado").val();
				szTemp = (szTemp.replace("$","")).replace("$","");	
				szTemp = (szTemp.replace(",","")).replace(",","");
				nMontoActualizado = Number(szTemp);						
				if (nMontoActualizado != nMontoCategoria)
				{
					$("#lbMontosOk").text("La suma de los Montos de todas las Categorías (" + $("#MontoMientras").val() + "), debe ser igual que el Monto Actualizado.");
					$("#lbMontosOk").css("color","red");
					return false;
				}
				$("#lbMontosOk").text("La suma de los Montos de todas las Categorías es igual que el Monto Actualizado.");
				$("#lbMontosOk").css("color","#0066FF");
				return true;
			}
			return true;
		}

		
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form>
		<input id="idComponente" type="hidden" size="10">
		<input id="id_prestamo" name="id_prestamo" type="hidden" size="10">
		<input id="Estatus" name="Estatus" type="hidden" size="10">
		<input id="MontoMientras" type="hidden" class="currency" />	

		
		<div id="container" class="container">
			<h1><img id="imgPlayStop" src="imagenes/wait24trans.gif">Pr&eacute;stamos <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<table border="0" align="center" width="100%">
				<tr>
					<td width="180px" align="right">N&uacute;mero Pr&eacute;stamo:</td>
					<td colspan="3"><input id="NumeroPrestamo" name="NumeroPrestamo" type="text" size="30" maxlength="25" style='text-transform:uppercase;' title="N&uacute;mero Pr&eacute;stamo"/></td>
				</tr>
				<tr>
					<td align="right">Nombre Pr&eacute;stamo:</td>
					<td  colspan="3"><input id="NombrePrestamo" name="NombrePrestamo" type="text" size="100" maxlength="200" style='text-transform:uppercase;' title="Nombre Pr&eacute;stamo"/></td>
				</tr>
				<tr>
					<td align="right" valign="top">Objetivo:</td>
					<td colspan="3" >
						<textarea id="Objetivo" name="Objetivo" rows="4" cols="102" maxlength="500" title="Objetivo del Pr&eacute;stamo" style='FONT-FAMILY: Arial, Tahoma, Verdana; '></textarea> 
					</td>
				</tr>
				<tr>
					<td align="right">Organismo Financiero Internacional:</td>
					<td colspan="3">
						<select id="id_ofi" name="id_ofi" title="Organismo Financiero Internacional (OFI)">
							<option value="2">A</option>
							<option value="3">B</option>
							<option value="4">C</option>
							<option value="1" selected>--</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">Agente Financiero:</td>
					<td colspan="3">
						<select id="id_agentefinanciero" name="id_agentefinanciero" title="Agente Financiero">
							<option value="2">A</option>
							<option value="3">B</option>
							<option value="4">C</option>
							<option value="1" selected>--</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<br>Programas Presupuestarios:
						<table id="tblProgramasPresupuestarios" class="display" style=" border: 1px solid #e0e0e0;" >
				            <thead>
				                <tr>
				                	<th>Sel</th>
				                	<th>Clave</th>
				                	<th>Descripción</th>
				                </tr>
				            </thead>
				        </table>
					</td>
				</tr>		
			</table>

			<br/>
			<fieldset>
				<table border="0" align="center" width="100%">
					<tr>
						<td  align="center">Fecha Firma:</td>
						<td  align="center">Fecha Efectividad:</td>
						<td  align="center">Fecha Terminaci&oacute;n:</td>
						<td  align="center">Fecha Límite para Desembolsar:</td>
					</tr>
					<tr>
						<td align="center"><input type="text" size="10" id="FechaFirma" name="FechaFirma" style="background:#f0f0f0; text-align:center" readonly title="Fecha Firma"></td>
						<td align="center"><input type="text" size="10" id="FechaEfectividad" name="FechaEfectividad" style="background:#f0f0f0; text-align:center" readonly title="Fecha Efectividad"></td>
						<td align="center"><input type="text" size="10" id="FechaTerminacion" name="FechaTerminacion" style="background:#f0f0f0;text-align:center" readonly title="Fecha Terminaci&oacute;n"></td>
						<td align="center"><input type="text" size="10" id="FechaLimiteDesembolsar" name="FechaLimiteDesembolsar" style="background:#f0f0f0; text-align:center" readonly title="Fecha Límite para Desembolsar"></td>
					</tr>
				</table>
			</fieldset>
			<br/>
			
			<table border="0" align="center" width="100%">
				<tr>
					<td align="right">Unidad Responsable:</td>
					<td colspan="3">
						<select style="width:330px" id="cUnidadResponsable" name="cUnidadResponsable" title="Unidad Responsable">
							<option value="2">A</option>
							<option value="3">B</option>
							<option value="4">C</option>
							<option value="1" selected>--</option>
						</select>
					</td>
					<td width="180px" align="right">Monto Original (USD):</td>
					<td><input id="MontoOriginal" name="MontoOriginal" type="text" size="15" maxlength="15" value= "0.00" onkeypress="Validaciones(this,2)" class="currency" onblur="fnVerificaMontos();" style="text-align:right" title="Monto Original (USD)"/></td>
				</tr>

			</table>
			<br/>
			
			<fieldset id="fieldCambio" style="height:0px; visibility:hidden">
				<table border="0" align="center" width="100%">
						<tr>
							<td colspan="4" >N&uacute;mero de Documento de Modificaci&oacute;n:
							<input id="NoDoctoModificacion" name="NoDoctoModificacion" type="text" size="20" maxlength="15" style='text-transform:uppercase;' title="N&uacute;mero de Documento de Modificaci&oacute;n"/></td>
						</tr>
					<tr>
						<td width="150px" align="center">Fecha de Entrada <br>en Vigor de la Modificación:</td>
						<td  align="center">Fecha de Cancelación Parcial:</td>
						<td  align="center">Fecha Terminaci&oacute;n Actualizada:</td>
						<td  align="center">Fecha Límite para Desembolsar Actualizada:</td>
					</tr>
					<tr>
						<td align="center"><input type="text" size="10" id="EntradaEnVigorModificacion" name="EntradaEnVigorModificacion" style="background:#f0f0f0; text-align:center" readonly title="Fecha de Entrada en Vigor de la Modificación"/></td>
						<td align="center"><input type="text" size="10" id="FechaCancelacionParcial" name="FechaCancelacionParcial" style="background:#f0f0f0; text-align:center" readonly title="Fecha de Cancelación Parcial"/></td>
						<td align="center"><input type="text" size="10" id="FechaTerminacionActualizada" name="FechaTerminacionActualizada" style="background:#f0f0f0; text-align:center" readonly title="Fecha Terminaci&oacute;n Actualizada"/></td>
						<td align="center"><input type="text" size="10" id="FechaLimiteDesembolsoActualizada" name="FechaLimiteDesembolsoActualizada" style="background:#f0f0f0; text-align:center" readonly title="Fecha Límite para Desembolsar Actualizada"/></td>
					</tr>
					<tr>
						<td align="right" colspan="4">Monto Incremental (USD):
							<input id="MontoIncremento" name="MontoIncremento" type="text" onblur="CalculaAcumulado();" class="currency" size="15" maxlength="15" value= "0.00" onkeypress="Validaciones(this,2)" style="text-align:right" title="Monto Incremental (USD)"/></td>
					</tr>
					<tr>
						<td align="right" colspan="4">Monto Cancelado (USD):
							<input id="MontoCancelado" name="MontoCancelado" type="text" onblur="CalculaAcumulado();" class="currency" size="15" maxlength="15" value= "0.00" onkeypress="Validaciones(this,2)" style="text-align:right" title="Monto Cancelado (USD)"/></td>
					</tr>
					<tr>
						<td align="right" colspan="4">Monto Actualizado (USD):
						<input id="MontoActualizado" type="text" size="15" maxlength="15" class="currency" value= "0.00" readonly style="background:#f0f0f0; text-align:right" title="Monto Actualizado (USD)"/></td>
					</tr>
				</table>
			</fieldset>
			<label id="lbMontosOk" style="color:#0066FF; width:100%; text-align: right;"></label>  <br/>
			<label style="FONT-WEIGHT:bold;">Componentes</label>   
			<fieldset style="padding:15px;">
				<table id="tblComponente" class="display"  >
		            <thead>
		                <tr>
		                	<th width="40px">No.</th>
		                    <th>Nombre Componente</th>
		                </tr>
		            </thead>
		        </table>	
		        <br><center><input type="button" id="pbAgregarComponente" value="Agregar"/>&nbsp; &nbsp; <input type="button" id="pbBorrarComponente" value="Borrar"/>&nbsp;&nbsp;</center>
			</fieldset>
			<br>
		
			<div class="tabs">
				<ul>
					<li><a id="aTab0" href="#tabs-0">SubComponentes</a></li>
					<li><a id="aTab1" href="#tabs-1">Categor&iacute;as</a></li>
				</ul>
				<div id="tabs-0" >
					<table id="tblSubComponente" class="display"  >
			            <thead>
			                <tr>
			                	<th width="40px">No.</th>
			                    <th>Nombre SubComponente</th>
			                </tr>
			            </thead>
			        </table>	
			        <br><center>
			        			<input type="button" id="pbAgregarSubComponente" value="Agregar"/>&nbsp; &nbsp; 
			        			<input type="button" id="pbBorrarSubComponente" value="Borrar"/>&nbsp;&nbsp;
			        	</center>
				</div>
				<div id="tabs-1" >
					<table id="tblCategoria" class="display"  >
			            <thead>
			                <tr>
			                	<th width="40px">No.</th>
			                    <th>Nombre Categoría</th>
			                    <th>Monto Original</th>
			                    <th>% Financiamento Ori</th>
			                    <th>SubComponente</th>
			                    <th>CategoriaInversion</th>
			                </tr>
			            </thead>
			        </table>	
			        <br><center>
				        	<input type="button"  id="pbAgregarCategoria" value="Agregar"/>&nbsp; &nbsp; 
				        	<input type="button"  id="pbBorrarCategoria" value="Borrar"/>&nbsp;&nbsp;&nbsp; &nbsp; 
				        	<input type="button"  id="pbCambiarCategoria" value="Cambiar"/>
			        	</center>
				</div>
			</div>
			
			<label class="validateTips ui-state-error" ></label><br>
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


		<div id="dialog-form-Componente" title="Componente">
			<center><label id="lbAcciónComponente" style="font-size:11px; font-style:italic; color:#0066FF"> Agregando un nuevo Componente</label></center>
			<table cellpadding="2" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="right">N&uacute;mero:</td>
					<td >
						<input type="text" id="txtNumeroComponente" size="4" maxlength="4" style="text-transform:uppercase; text-align:center" title="N&uacute;mero del Componente"/>
					</td>						
				</tr>
				<tr>
					<td width="100px" align="right">Nombre:</td>
					<td >
						<input type="text" id="txtNombreComponente" size="89" maxlength="200" style="text-transform:uppercase;" title="Nombre del Componente"/>
					</td>						
				</tr>
			</table>
		</div>

		<div id="dialog-form-SubComponente" title="SubComponente">
			<center><label id="lbAcciónSubComponente" style="font-size:11px; font-style:italic; color:#0066FF"> Agregando un nuevo SubComponente</label></center>
			<table cellpadding="2" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="right">Componente:</td>
					<td >
						<input type="text" id="txtNombreComponenteSubComponente" size="89" maxlength="200" readonly disabled style="background: #f0f0f0" title="Componente" />
					</td>						
				</tr>
				<tr>
					<td align="right">N&uacute;mero:</td>
					<td >
						<input type="text" id="txtNumeroSubComponente" size="4" maxlength="4" style="text-transform:uppercase; text-align:center" title="N&uacute;mero Subcomponente"/>
					</td>						
				</tr>
				<tr>
					<td width="100px" align="right">SubComponente:</td>
					<td >
						<input type="text" id="txtNombreSubComponente" size="89" maxlength="200" style="text-transform:uppercase;" title="Nombre del SubComponente" />
					</td>						
				</tr>
			</table>
		</div>
		
		<div id="dialog-form-Categoria" title="Categoria">
			<center><label id="lbAcciónCategoria" style="font-size:11px; font-style:italic; color:#0066FF"> Agregando una nueva Categoría</label></center>
			<table cellpadding="2" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="right">Componente:</td>
					<td >
						<input type="text" id="txtNombreComponenteCategoria" size="80" maxlength="200" readonly disabled style="background: #f0f0f0" title="Componente" /><input type="hidden" id="txtid_categoriaInversionPrestamo" readonly  />
					</td>						
				</tr>
				<tr>
					<td align="right">SubComponente:</td>
					<td >
						<select id="cbSubComponente" name="cbSubComponente" title="SubComponente">
							<option value="2">A</option>
							<option value="3">B</option>
							<option value="4">C</option>
							<option value="1" selected>--</option>
						</select>
					</td>						
				</tr>
				<tr>
					<td align="right">N&uacute;mero Categoría:</td>
					<td >
						<input type="text" id="txtNumeroCategoria" size="4" maxlength="4" style="text-transform:uppercase; text-align:center" title="N&uacute;mero Categoría"/>
					</td>						
				</tr>
				<tr>
					<td align="right">Nombre Categoría:</td>
					<td >
						<input type="text" id="txtNombreCategoria" size="80" maxlength="200" style="text-transform:uppercase;" title="Nombre Categoría" />
					</td>	
				</tr>
				<tr>
					<td width="400px" align="right">Monto Categoría:</td>	
					<td >
						<input id="MontoCategoriaOriginal" name="MontoCategoriaOriginal" type="text" class="currency" size="15" maxlength="25" value= "0.00" onkeypress="Validaciones(this,2)" style="text-align:right" title="Monto Categoría"/>
						&nbsp;&nbsp;&nbsp; Porcentaje de Financiamiento: &nbsp; <input id="txtPorcentajeCategoriaOriginal" type="text" size="3" maxlength="3" value= "0" onkeypress="Validaciones(this,2)" style="text-align:center" title="Porcentaje de Financiamiento"/>
					</td>					
				</tr>
			</table>
		</div>
		
	</form>
	</body>
</html>