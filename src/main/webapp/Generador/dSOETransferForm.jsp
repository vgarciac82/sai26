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
		<title>Generación SOE Transferencia</title>

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
		
		var oTableSOE;
		var oTableSOEDatos;
		var gsOperacion ='<%=request.getParameter("Op")%>';
			
		var gsSOE ='<%=request.getParameter("SOEParam")%>';			
		var gsIdPrestamo ='<%=request.getParameter("sPrestamoParam")%>';	
		var gsEstatus  ='<%=request.getParameter("EstatusParam")%>'; 
		var gsFecha  ='<%=request.getParameter("FechaParam")%>';
		var gsDeDonde ="";
		var giSumaTotal = 0;
						
		$(document).ready(function() {
		
			$('.currency').blur(function()	{
				$('.currency').formatCurrency();
				});			
					
			$("#tblSOE tbody").click(function(event) {
					$(oTableSOE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					var aTrs = $("#tblSOE").dataTable().fnGetNodes();
					if (aTrs.length == 0)
						return;
						
					$(event.target.parentNode).addClass('row_selected');
				});
				
			$("#tblSOEDatos tbody").click(function(event) {
					$(oTableSOEDatos.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					var aTrs = $("#tblSOEDatos").dataTable().fnGetNodes();
					if (aTrs.length == 0)
						return;
						
					$(event.target.parentNode).addClass('row_selected');
				});
				
			$("#dlgIdPrestamo")
                .change(function() {
                try
                {	
                	if ($("#dlgIdPrestamo").val() != 0) $("#id_prestamo").val($("#dlgIdPrestamo").val());
					
					querySelectPost("vProgramaPresupuestarioXIdPrestamoRead", "dlgPPresupuestario", {async: false});
					querySelectPost("dcatalogoEntidadFederativaXIdPrestamoRead", "dlgEntidad", {async: false});
					querySelectPost("vDocumentoXIdPrestamoRead", "dlgDocumento", {async: false});
					
					$("#dlgPPresupuestario").append($('<option>', {
					    value: 0,
					    text: '- Seleccionar -'
					    , selected: true
					}));
					$("#dlgEntidad").append($('<option>', {
					    value: 0,
					    text: '- Seleccionar -'
					    , selected: true
					}));
					$("#dlgDocumento").append($('<option>', {
					    value: 0,
					    text: '- Seleccionar -'
					    , selected: true
					}));	
					
					fnOrdenaCombo("#dlgPPresupuestario");
					fnOrdenaCombo("#dlgEntidad");
					fnOrdenaCombo("#dlgDocumento");			
					
					//fnLlenaDatosAgregar();

                }              	
                catch (ex)
                {
                }
            });
            
//             $("#dlgPPresupuestario")
//                 .change(function() {
//                 try
//                 {
//                 	fnLlenaDatosAgregar();
//                 }              	
//                 catch (ex)
//                 {
//                 }
//             });
            
//             $("#dlgEntidad")
//                 .change(function() {
//                 try
//                 {
//                 	fnLlenaDatosAgregar();
//                 }              	
//                 catch (ex)
//                 {
//                 }
//             });
            
//             $("#dlgDocumento")
//                 .change(function() {
//                 try
//                 {
//                 	fnLlenaDatosAgregar();
//                 }              	
//                 catch (ex)
//                 {
//                 }
//            });
                        
            $("#chkAllAgregar")
                .change(function() {
                
                if($(this).is(':checked'))
                {										 // checked
				    $(".chkAg").each(function (){
						
						 $(this).attr('checked', true );
					});
				}
				else
				{										// unchecked
					$(".chkAg").each(function (){
						
						 $(this).attr('checked', false );
					});
				}
            });
            
            $(".chkAg")
                .change(function() {
               
                
                $("#chkAllAgregar").attr('checked', false );
                
            });
				
        	oTableSOE = $("#tblSOE").dataTable({
				bAutoWidth : false,
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
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bProcessing: true,
				
				            bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				"aLengthMenu": [
						            [25, 50, 100, 200, -1],
						            [25, 50, 100, 200, "Todo"]
						        ], 
				"iDisplayLength" : -1,
				aaSorting: [[ 4, "asc" ]] ,
				aoColumns: [
					{ sName: "IdPrestamo",	bSortable: false, bVisible: false },
					{ sName: "Chkbx",bSortable: false, sClass: "alignCenter" },
					{ sName: "NoContrato",bSortable: false },
					{ sName: "Num. Factura",bSortable: false},	
					{ sName: "Nombre Beneficiario" },
					{ sName: "ImporteFac", sClass: "alignRight"  }	,
					{ sName: "Programa"},
					{ sName: "Documento" },	
					{ sName: "Folio" },			
					{ sName: "CuentaXPagar" },
					{ sName: "RFC" },
					{ sName: "F. Aplicación" },
					{ sName: "Unidad Responsable" },
					{ sName: "Entidad" },
					{ sName: "Cartera" }
								
				]
        	});
        	
        	oTableSOEDatos = $("#tblSOEDatos").dataTable({
   	
        	
				bAutoWidth : false,
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
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				            bProcessing: true,
				            bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				"iDisplayLength" : 10,				
				aaSorting: [[ 4, "asc" ]] ,
				aoColumns: [
					{ sName: "IdPrestamo",	bSortable: false, bVisible: false },
					{ sName: "Chkbx",bSortable: false, sClass: "alignCenter" },
					{ sName: "NoContrato",bSortable: false },
					{ sName: "Num. Factura",bSortable: false},	
					{ sName: "Nombre Beneficiario" },
					{ sName: "ImporteFac", sClass: "alignRight"  }	,
					{ sName: "Programa"},
					{ sName: "Documento" },	
					{ sName: "Folio" },			
					{ sName: "CuentaXPagar" },
					{ sName: "RFC" },
					{ sName: "F. Aplicación" },
					{ sName: "Unidad Responsable" },
					{ sName: "Entidad" },
					{ sName: "Cartera" }
								
				]
        	});
        	
        	
							
										
        	if (gsOperacion == "M" || gsOperacion == "X")
        	{
        		$("#lbOperacion").text(" (Modificar)");
        		$("#numero_soe").val(gsSOE);
        		$("#Nombre").val(gsSOE);
        		$("#id_prestamo").val(gsIdPrestamo);
        		var szTemp;
        		
        		
        		if (gsOperacion == "X" )
        		 {
				 //$("#pbGenerar").attr("disabled", true);
				 $("#pbGuardar").attr("disabled", true);
				 $("#pbAgregarDatos").attr("disabled", true);
				 $("#pbBorrarDatos").attr("disabled", true);
                 }
        		
        		
        		giSumaTotal = 0;
				var szWhere = " Nombre ='" +gsSOE+"'"; 
				var szTabla = "DSOETransferEXISTE";  // Falta modificar el SELECT en el Jason de este parámetro (crear su vista y sus campos)                                                                                       
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"DSOETransferEXISTE", Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					
					function(j)
					{    
        		  		for (var i = 0; i < j.length; i++) 
	  					{  
        		  			szTemp =j[i].Col5;
        		  			szTemp=parseFloat(szTemp).toFixed(2);
        		  			
	  						var szCheck = '<input type="checkbox" id="chkSOE' + i + '" class="chkAgSOE" >'; 
	  						$("#tblSOE").dataTable().fnAddData([j[i].Col0,szCheck,j[i].Col2,j[i].Col3, j[i].Col4,'$'+szTemp, j[i].Col6, j[i].Col7, j[i].Col8, j[i].Col9, j[i].Col10,j[i].Col11,j[i].Col12, j[i].Col13,j[i].Col14]);
	  						
	  						szTemp = (szTemp.replace("$","")).replace("$","");	
							szTemp = (szTemp.replace(",","")).replace(",","");
							giSumaTotal = giSumaTotal + Number(szTemp);
						}
						$("#SumaTotal").val(giSumaTotal);
						$('.currency').formatCurrency();
				}); 

				querySelectPost("PrestamoNumNomRead", "dlgIdPrestamo", {async: false});
				$("#dlgIdPrestamo").append($('<option>', {
				    value: 0,
				    text: '- Seleccionar -'
				    , selected: true
				}));
				fnOrdenaCombo("#dlgIdPrestamo");
				$("#dlgIdPrestamo").change();
				$("#dlgIdPrestamo").val($("#id_prestamo").val());
				
				$("#lbPrestamo").val($("#dlgIdPrestamo :selected").text());
        	}
        	
			if (gsOperacion == "X")
        	{
        		$("#lbOperacion").text(" (Desplegar)");
        		
 				//$("#tblSOE").dataTable().fnSetColumnVis( 3, false );
 				$("#tblSOE").dataTable().fnSetColumnVis( 0, false );
 				$("#tblSOE").dataTable().fnSetColumnVis( 1, false );
 				
        	}      	
        	
        	if (gsEstatus == "Desembolso")
        	{
        		$("button").attr("disabled", true);
				$("#pbCancelar").removeAttr('disabled');
				
				var szWhere = " Nombre = '" + gsSOE + "' "; 
				var szTabla = "DSOET";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{    
        		  		for (var i = 0; i < j.length; i++) 
	  					{  	  					
	  						var szFechaNoObj = j[i].Col9;
	  						szFechaNoObj = szFechaNoObj.substring(0,10); 
	  						
	  						$("#OficioNoObjecion").val(j[i].Col10);
	  						$("#TipoCambioNoObjecion").val(j[i].Col11);
	  						$("#MontoNoObjecion").val(j[i].Col12);
	  						$("#FechaNoObjecion").val( szFechaNoObj );
	  						$("#SolicitudDesembolso").val( j[i].Col6 );
	  						
	  						$('.currency').formatCurrency();
	  						
	  						$("#dlgOficioNoObjecion").val($("#OficioNoObjecion").val());
	  						$("#dlgTipoCambioNoObjecion").val($("#TipoCambioNoObjecion").val());
	  						$("#dlgMontoNoObjecion").val($("#MontoNoObjecion").val());
	  						$("#dlgFechaNoObjecion").val($("#FechaNoObjecion").val());
	  						$("#dlgSolicitudDesembolso").val($("#SolicitudDesembolso").val());
						}
				}); 
				
        	}							
        				
        	$('.currency').formatCurrency();
        	
        	if ( gsEstatus == "en TrÃ¡mite") gsEstatus = "en Trámite";
        	
        	$("#lbStatus").text(gsEstatus);
        	
			if (gsOperacion != 'A') 
				$("#FechaUltimoMovimiento").val(gsFecha.substring(0,10));
        	else
        	{
        		$("#pbGenerar").attr("disabled", true);
        		$("#pbDesembolso").attr("disabled", true);
        	}
        	
        	
			$("#pbGuardar").focus();
			 				
		});

		
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
		
		function fnOrdenaCombo(objCombo)
		{
			$(objCombo).append($( objCombo + " option").remove().sort(function(a, b) {
				var at = $(a).text(), bt = $(b).text();
				return (at > bt)?1:((at < bt)?-1:0);
			}));		
		}
		
		            
		function fnLlenaDatosAgregar()
		{
			$("#tblSOEDatos").dataTable().fnClearTable();
			
			var szWhere = "";
			szWhere = szWhere + " ProgramaPresupuestario IN (SELECT cProgramaPresupuestario FROM dPrestamoProgramaPresupuestario WITH(NOLOCK) WHERE id_prestamo = " + $("#id_prestamo").val() + " )" ;
			if ($("#dlgPPresupuestario").val() != 0) szWhere = szWhere + " AND ProgramaPresupuestario = '" + $("#dlgPPresupuestario").val() + "'";
			if ($("#dlgEntidad").val() != 0) szWhere = szWhere + " AND Entidad =  '" + $("#dlgEntidad").val() + "'";
			if ($("#dlgDocumento").val() != 0) szWhere = szWhere + " AND Documento =  '" + $("#dlgDocumento").val() + "'";

			
			
			var szTabla = "DSOEDISPTRANSFER";    
			
			     $.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
				function(j)
				{
				
					var arregloDatos=new Array();
  					for (var i = 0; i < j.length; i++)
  					{
  						var szCheck = '<input type="checkbox" id="chkBusca' + i + '" class="chkAg" checked>';
  						 arregloDatos[i]=[$("#id_prestamo").val(),szCheck,j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, j[i].Col9, j[i].Col10, j[i].Col11, j[i].Col12 ];
  						//$("#tblSOEDatos").dataTable().fnAddData([ j[i].Col0, j[i].Col1, j[i].Col2, szCheck, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, j[i].Col9 ]);
  						
					} 
					$("#tblSOEDatos").dataTable().fnAddData(arregloDatos);
										
					if (gsOperacion != "M" && gsOperacion != "X"){						
						fnRemueveSeleccionados();
					    }
	         });
	         
	       
	           
		}
		
		function fnRemueveSeleccionados()
		{
			var aTrs = oTableSOE.fnGetNodes();    
			var rowsTblSOE = $("#tblSOE").dataTable().fnGetData();         
			var aTrsDatos = oTableSOEDatos.fnGetNodes();    
			var rowsTblDatos = $("#tblSOEDatos").dataTable().fnGetData(); 
						
			for (var i=aTrs.length-1 ; i>=0; i--)      
			{   
				for (var j=aTrsDatos.length-1 ; j>=0; j--) 
				{      
					if(rowsTblSOE[i][2] == rowsTblDatos[j][2])        
					{             
						$("#tblSOEDatos").dataTable().fnDeleteRow( j );								
					} 
				}    
			}  
		}
		
		

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		$(function() {
		
			$( "#dialog:ui-dialog" ).dialog( "destroy" );

			var dlgOficioNoObjecion = $("#dlgOficioNoObjecion"),
				dlgTipoCambioNoObjecion	= $("#dlgTipoCambioNoObjecion"),
				dlgMontoNoObjecion	= $("#dlgMontoNoObjecion"),
				dlgFechaNoObjecion	= $("#dlgFechaNoObjecion"),	
				dlgSolicitudDesembolso	= $("#SolicitudDesembolso"),
				
				allFields = $( [] ).add( dlgOficioNoObjecion ).add( dlgTipoCambioNoObjecion ).add( dlgMontoNoObjecion ).add( dlgFechaNoObjecion ).add( dlgSolicitudDesembolso ),
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
		
			$( "#dialog-form-Busqueda" ).dialog({
				autoOpen: false,
				height: 530,
				width: 800,
				modal: true,
				buttons: {
					"Aceptar": function() {
					
						var aTrs = oTableSOEDatos.fnGetNodes();    
						var rowsTbl = $("#tblSOEDatos").dataTable().fnGetData();      
						//giSumaTotal = 0;
						for ( var i=0 ; i<aTrs.length ; i++ )     
						{         
							var szControl = rowsTbl[i][1].substr(rowsTbl[i][1].indexOf('id=')+4);
							szControl = szControl.substring(0,szControl.indexOf(' ')-1); 
							if($("#"+szControl).is(':checked'))        
							{             
							
							//los datos de la bd empiezan desde la pos 2      0=prestamo   1=checked
								var szCheck = '<input type="checkbox" id="chkSOE' + i + '" class="chkAgSOE" checked>'; 
								$("#tblSOE").dataTable().fnAddData([ rowsTbl[i][0],szCheck, rowsTbl[i][2], rowsTbl[i][3], rowsTbl[i][4], rowsTbl[i][5], rowsTbl[i][6], rowsTbl[i][7], rowsTbl[i][8], rowsTbl[i][9], rowsTbl[i][10], rowsTbl[i][11], rowsTbl[i][12], rowsTbl[i][13], rowsTbl[i][14] ]); 
								var szTemp =rowsTbl[i][5]; 
		  						szTemp = (szTemp.replace("$","")).replace("$","");	
								szTemp = (szTemp.replace(",","")).replace(",","");
								giSumaTotal = giSumaTotal + Number(szTemp);
							}
						}   						
						$("#SumaTotal").val(giSumaTotal);  
						$('.currency').formatCurrency();   
												
						$("#lbPrestamo").val($("#dlgIdPrestamo :selected").text());
						$( this ).dialog( "close" );
					},
					"Cancelar": function() {
						$( this ).dialog( "close" );
					}
				},
				close: function() {
					allFields.removeClass( "ui-state-error" );
					tips.text( "" );
				}
			});
			
			$( "#dialog-form-Desembolso" ).dialog({
				autoOpen: false,
				height: 250,
				width: 400,
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
		
						bValid = bValid && checkRequerido( dlgOficioNoObjecion, "Oficio No." );
						bValid = bValid && checkRequerido( dlgTipoCambioNoObjecion, "Tipo de Cambio" );
						bValid = bValid && checkRequerido( dlgMontoNoObjecion, "Monto" );
						bValid = bValid && checkRequerido( dlgFechaNoObjecion, "Fecha" );
						bValid = bValid && checkRequerido( dlgSolicitudDesembolso, "Solicitud de Desembolso" );
						
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
						$("#TipoCambioNoObjecion").val($("#dlgTipoCambioNoObjecion").val());
						$("#MontoNoObjecion").val($("#dlgMontoNoObjecion").val());
						$("#FechaNoObjecion").val($("#dlgFechaNoObjecion").val());
						$("#SolicitudDesembolso").val($("#SolicitudDesembolso").val());

						
						var szCampos = " Estatus = 'D', OficioNoObjecion = '" + $("#OficioNoObjecion").val() + "', TipoCambioNoObjecion =" + $("#TipoCambioNoObjecion").val() + ", MontoNoObjecion =" + szTempDub + ", FechaNoObjecion = '" + $("#FechaNoObjecion").val() + "', FechaUltimoMovimiento=GETDATE(), SolicitudDesembolso ='" + $("#SolicitudDesembolso").val() + "'"; 
						var szWhere = " numero_soe = '" + $("#numero_soe").val() + "' ";
						var szTabla = "DSOET";
						$.getJSON("../catalogos/UpdateJson.jsp", {Tabla: szTabla, Param: szWhere, SetParam: szCampos},
					   		function(j)
					   		{
	
				   			});
						alert("¡Operación Exitosa! \r\rSOE ha sido Desembolsado.");					
						
						$( this ).dialog( "close" );
						location.href = 'dSOETransferGrid.jsp';
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
			
			
			$("#pbAgregarDatos")
				.button()
				.click(function() {
						
					if ($("#lbPrestamo").val() != "" )
					{
						querySelectPost("PrestamoNumNomRead", "dlgIdPrestamo", {async: false});
						$("#dlgIdPrestamo").append($('<option>', {
						    value: 0,
						    text: '- Seleccionar -'
						    , selected: true
						}));
						fnOrdenaCombo("#dlgIdPrestamo");
						$("#dlgIdPrestamo").val($("#id_prestamo").val());
						$("#dlgIdPrestamo").attr("disabled", true);
						$("#dlgIdPrestamo").css("background", "#f0f0f0");
						
						$("#dlgIdPrestamo").change();
						$("#dlgIdPrestamo").val($("#id_prestamo").val());

						fnRemueveSeleccionados(); 		
					}
					else
					{
						querySelectPost("PrestamoNumNomRead", "dlgIdPrestamo", {async: false});
						$("#dlgIdPrestamo").append($('<option>', {
						    value: 0,
						    text: '- Seleccionar -'
						    , selected: true
						}));
						fnOrdenaCombo("#dlgIdPrestamo");
						$("#tblSOEDatos").dataTable().fnClearTable();
					}
					
					$( "#dialog-form-Busqueda" ).dialog( "open" );  		
				});
				
		$("#pbBorrarDatos") .button() .click(function() { 
				
				var aTrs = oTableSOE.fnGetNodes(); 
				var rowsTbl = $("#tblSOE").dataTable().fnGetData(); 
				giSumaTotal = 0; 
				
				for (var i=aTrs.length-1 ; i>=0; i--) {
					
					var szControl = rowsTbl[i][1].substr(rowsTbl[i][1].indexOf('id=')+2); 
					szControl = szControl.substring(2,szControl.indexOf(' ')-1); 
					
					if($("#"+szControl).is(':checked')) {
						$("#tblSOE").dataTable().fnDeleteRow( i );
						} 
					else { 
						var szTemp = rowsTbl[i][3]; szTemp = (szTemp.replace("$","")).replace("$",""); 
					    
						szTemp = (szTemp.replace(",","")).replace(",",""); giSumaTotal = giSumaTotal + Number(szTemp); 
					} 
					
				}
				
				$("#SumaTotal").val(giSumaTotal); $('.currency').formatCurrency();
				
			});
				
			$("#pbGuardar")
				.button()
				.click(function() {
			
				
					var szNumeroSOE = "SOE-";
					var szWhere = " id_prestamo = " + $("#id_prestamo").val() + " ";
					var szTabla = "DNUMPRESTAMO";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{                       
		  					for (var i = 0; i < j.length; i++) 
		  					{  
		  						szNumeroSOE = szNumeroSOE + j[i].Col0 + "-"; 
		  						
		  						szWhere = " dSOETransfer.id_prestamo = " + $("#id_prestamo").val() + " ";
								szTabla = "DNUMSOET";                                                                                         
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
									function(j)
									{                    
					  					for (var i = 0; i < j.length; i++) 
					  					{  
					  						szNumeroSOE = szNumeroSOE + j[i].Col0; 
					  						if (gsOperacion == 'A')
					  						{
						  						$("#Nombre").val(szNumeroSOE); 
						  						$("#numero_soe").val(szNumeroSOE);
					  						}
											var aTrs = oTableSOE.fnGetNodes();    
											var rowsTbl = $("#tblSOE").dataTable().fnGetData();  
											var iSize = aTrs.length;   
											$('#tblSOE').dataTable().fnClearTable();
											$('#tblSOEDatos').dataTable().fnClearTable();	
											
											for ( var i=0 ; i<iSize ; i++ )     
											{         
												var szCheck = '<input type="checkbox" id="chkSOE' + i + '" class="chkAgSOE" >';
												$('#tblSOE').dataTable().fnAddData([ 
													//id_prestamo,NumeroContrato,NumeroFactura,numero_soe,CuentaXPagar,Nombre,ProgramaPresupuestario,ImporteFactura,Beneficiario,RFC
													'<input type="text" id="cid_prestamo" name="cid_prestamo" value="' + rowsTbl[i][0] +' "/>',
													'<input type="text" id="cnumero_soe" name="cnumero_soe" value="' + szNumeroSOE + '"/>',
													'<input type="text" id="cNumeroContrato" name="cNumeroContrato" value="' + rowsTbl[i][2] + '"/>',
													'<input type="text" id="cNumeroFactura" name="cNumeroFactura" value="' + rowsTbl[i][3] + '"/>',
													'<input type="text" id="cBeneficiario" name="cBeneficiario"  value="' + rowsTbl[i][4] + '"/>',
													'<input type="text" id="cImporteFactura" name="cImporteFactura"  value="' + rowsTbl[i][5] + '"/>',
													'<input type="text" id="cProgramaPresupuestario" name="cProgramaPresupuestario" value="' + rowsTbl[i][6] + '"/>',
													'<input type="text" id="cDocumento" name="cDocumento" value="' + rowsTbl[i][7] + '"/>',
													'<input type="text" id="cFolio" name="cFolio" value="' + rowsTbl[i][8] + '"/>',
													'<input type="text" id="cCuentaXPagar" name="cCuentaXPagar" value="' + rowsTbl[i][9] + '"/>',
													'<input type="text" id="cRFC" name="cRFC" value="' + rowsTbl[i][10] + '"/>',
													'<input type="text" id="cfAplicacion" name="cfAplicacion" value="' + rowsTbl[i][11] + '"/>',
													'<input type="text" id="cUnidadResponsable" name="cUnidadResponsable" value="' + rowsTbl[i][12] + '"/>',
													'<input type="text" id="cEntidad" name="cEntidad" value="' + rowsTbl[i][13] +'"/>',
													'<input type="text" id="cCartera" name="cCartera" value="' + rowsTbl[i][14] +'"/>'
													
													
													
													
													
												]);   
											}   		
											$('#tblSOE').dataTable().fnSetColumnVis( 0, true );
											$('#tblSOE').dataTable().fnSetColumnVis( 1, true );
											
											queryFormPost("dSOETransferDelete", {async: false});
												
											if (iSize>0) {  queryFormPost("dSOETransferCreate", {async: false}); }
											
											
											
											//$('#tblSOE').dataTable().fnClearTable();	
											if (gsDeDonde == '') 
											{
												alert("¡Operación Exitosa! \r\rSOE ha sido Guardado.");		
												location.href = 'dSOETransferGrid.jsp';		  		
											}	
											if (gsDeDonde == "De Generar")	
											{
												var szCampos = " Estatus = 'T'";
												var szWhere = " numero_soe = '" + $("#numero_soe").val() + "', FechaUltimoMovimiento=GETDATE() ";
												var szTabla = "DSOET";
												$.getJSON("../catalogos/UpdateJson.jsp", {Tabla: szTabla, Param: szWhere, SetParam: szCampos},
											   		function(j)
											   		{
							
										   			});
												alert("¡Operación Exitosa! \r\rSOE ha sido Generado.");
											   	
											   	var url = "../admin/SeguridadCatalogos?xls=SI&catalogo=REPORTE&accion=run&rn=RelacionGastosDocumentadosCorta.jasper&NO_SOE="+ $("#numero_soe").val()+ "";
											   	
												var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=1024, height=768");
											   	gsDeDonde = "";				
											}									
										}
						         });   
							}
			         });  					
				});
				
			$("#pbGenerar")
				.button()
				.click(function() 
				{
					gsDeDonde = "De Generar";
					//$("#pbGuardar").click();

					var szCampos = " Estatus = 'T', FechaUltimoMovimiento=GETDATE() ";
					var szWhere = " numero_soe = '" + $("#numero_soe").val() + "' ";
					var szTabla = "DSOET";
					$.getJSON("../catalogos/UpdateJson.jsp", {Tabla: szTabla, Param: szWhere, SetParam: szCampos},
				   		function(j)
				   		{

			   			});
					
					//alert("¡Operación Exitosa! \r\rSOE ha sido Generado.");
					
					tiporep = 1;
				   	var url = "../gstnmngr/ReporteSOExTransferencia?tipo_rep="+tiporep+"&ParamRep="+$("#Nombre").val(); // es excel con plantilla;	
				
					var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=1024, height=768");
				   	gsDeDonde = "";
				   	location.href = 'dSOETransferGrid.jsp';

				});
				
				
		function openExcel(tiporep) {
		var url = "../gstnmngr/ReporteSOExTransferencia?tipo_rep="+tiporep+
				"&ParamRep="+$("#Nombre").val(); // es excel con plantilla;	
		var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=no, width=624, height=468");
		}
				
			$("#pbDesembolso")
				.button()
				.click(function() {
					gsDeDonde = "De Desembolso";
					$("#dlgFechaNoObjecion").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "images/calendar.gif", buttonImageOnly : true	});
					
					//$("#pbGuardar").click();
					$( "#dialog-form-Desembolso" ).dialog( "open" );
					gsDeDonde = "";
				});
				
			$("#pbCancelar")
				.button()
				.click(function() {
					location.href = 'dSOETransferGrid.jsp';
				});
		});
	</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0" >
	<form> 
		<input id="id_prestamo" name="id_prestamo" type="hidden" value="0" readonly style=" background:#f0f0f0; ">
		<input id="numero_soe" name="numero_soe" type="hidden" value="0" readonly style=" background:#f0f0f0; ">
		
		<div id="container" class="container">					
			<h1>SOE por Transferencia <label id="lbOperacion" style="font-size: 8pt">(Agregar)</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label id="lbStatus" style="font-size: 11pt"></label></h1>
			<table border="0" align="center" width="100%">
				<tr>
					<td align="right" style="width:100px">N&uacute;mero SOE:</td>
					<td>
			 			<input id="Nombre" name="Nombre" type="text" size="40" readonly style="background:#f0f0f0; text-align:center"> 
			 		</td>
			 		<td align="right" style="width:200px">Fecha Última Modificación:</td>
					<td align="right" style="width:100px">
			 			<input id="FechaUltimoMovimiento" name="FechaUltimoMovimiento" type="text" size="10" readonly style="background:#f0f0f0; text-align:center"> 
			 		</td>
			    </tr>
              	<tr>
					<td align="right">Pr&eacute;stamo:</td>
					<td  colspan="3">
						<input id="lbPrestamo" type="text" value="" readonly  style="width:100%; background:#f0f0f0; ">
					</td>
        		</tr>			
			</table>
			<label style="FONT-WEIGHT:bold;">Datos Desembolso (Oficio de No Objeción)</label> 
			<table border="0" style="width:100%; border: 1px solid #e0e0e0;" >
				<tr >
					<td colspan = '2' align="right" style="width:50%">
						Oficio Agente Financiero No.:&nbsp;<input id="OficioNoObjecion" name="OficioNoObjecion"  type="text" size="20" readonly style="background:#f0f0f0; text-align:center"> 
					</td>
					<td align="right" style="width:50%">
						Solicitud de Desembolso:&nbsp;<input id="SolicitudDesembolso" name="SolicitudDesembolso"  type="text" size="20" readonly style="background:#f0f0f0; text-align:center">
					</td>
				</tr>
				<tr>
					<td align="right" style="width:25%">
						Fecha Valor:&nbsp;<input id="FechaNoObjecion" name="FechaNoObjecion"  type="text" size="10" readonly style="background:#f0f0f0; text-align:center">
					</td>
					<td align="right" >
						Tipo Cambio:&nbsp;<input id="TipoCambioNoObjecion" name="TipoCambioNoObjecion" class="currency" type="text" size="4" readonly style="background:#f0f0f0; text-align:center">
					</td>				
					<td align="right" style="width:30%">
						Monto Desembolsado USD:&nbsp;<input id="MontoNoObjecion" name="MontoNoObjecion" class="currency" type="text" size="13" readonly style="background:#f0f0f0; text-align:right">
					</td>
				</tr>
	        </table>
			<br/>
			<label style="FONT-WEIGHT:bold;">Datos SOE</label>   
			<fieldset style="padding:15px;">
				<input type="button" id="pbAgregarDatos" value="Agregar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbBorrarDatos" value="Borrar"/>&nbsp;&nbsp;
				<br/>	
				<br/>		
				<table id="tblSOE" class="display" style="width:100%;" >
		            <thead>
		                <tr>		                	
		                <th>Prestamo</th>
	                	<th>Sel</th>
	                	<th>Contrato</th>
	                	<th>Factura</th>
	                	<th>Beneficiario</th>
	                	<th>Importe Factura</th>
	                	<th>Programa Presupuestario</th>
	                	<th>Documento</th>
	                	<th>Folio</th>
	                	<th>CuentaXPagar</th>
	                	<th>RFC</th>
	                	<th>F. Aplicación</th>
	                	<th>Unidad Responsable</th>
	                	<th>Entidad</th>
	                	<th>Cartera</th>
		                </tr>
		            </thead>
		        </table>
		        <label style="position:relative; left:440px;">Importe Total de Facturas:</label> <input id="SumaTotal" class="currency" type="text" size="17" value="$0.00" readonly style="position:relative; left:445px;  background:#f0f0f0; text-align:right">
				<br/>
			</fieldset>
			<label class="validateTips ui-state-error" ></label>
			<br>
			<table width="100%" border="0" >
				<tr>
					<td width="33%">&nbsp;</td>
					<td width="33%" align="center">
						<input type="button" id="pbGuardar" value="Guardar"/>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbGenerar" value="Generar SOE"/>&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbDesembolso" style="visibility: hidden" value="Desembolso"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" id="pbCancelar" value="Regresar"/>
					</td>
					<td width="33%" align="right">
						&nbsp;
					</td>
				</tr>
			</table>
			<br>
		</div>
 
		<div id="dialog-form-Busqueda" title="Agregando datos SOE" style="overflow-x:hidden;">
			<table align="center" cellpadding="2" cellspacing="0" border="0" width="97%">
              	<tr>
					<td align="right">Pr&eacute;stamo:</td>
					<td >
						<select id="dlgIdPrestamo" name="dlgIdPrestamo" style="width: 100%;"><option value="0" selected>- Seleccionar -</option></select>						
					</td>
        		</tr>
              	<tr>
					<td align="right">Programa Presupuestario:</td>
					<td>
						<select id="dlgPPresupuestario" style="width: 100%;"><option value="0" selected>- Seleccionar -</option></select>
					</td>
        		</tr>
              	<tr>					
					<td align="right" style="width:150px">Entidad Federativa:</td>
					<td >
						<select id="dlgEntidad" style="width: 100%;"><option value="0" selected>- Seleccionar -</option></select>
					</td>
				</tr>
								
              	<tr>
					<td align="right">Tipo de Documento:</td>
					<td>
						<select id=dlgDocumento style="width: 100%;"><option value="0" selected>- Seleccionar -</option></select>
					</td>
        		</tr>
        		
        		<tr>					
				<td align="right" style="width:150px"></td>
				<td >
					<input type="button" name="bBuscar" id="bBuscar" value="Buscar" onclick="fnLlenaDatosAgregar();"/>
				</td>
				</tr>
								
			</table>
			<br>
			<div  style="overflow-x:hidden; height:100px; width:97%; backgroundcolor:red">
			<table id="tblSOEDatos" class="display" style="width:100%;" >
	            <thead>
	                <tr>
	                	<th>Prestamo</th>
	                	<th>Sel <input id="chkAllAgregar" type="checkbox" checked ></th>
	                	<th>Contrato</th>
	                	<th>Factura</th>
	                	<th>Beneficiario</th>
	                	<th>Importe Factura</th>
	                	<th>Programa Presupuestario</th>
	                	<th>Documento</th>
	                	<th>Folio</th>
	                	<th>CuentaXPagar</th>
	                	<th>RFC</th>
	                	<th>F. Aplicación</th>
	                	<th>Unidad Responsable</th>
	                	<th>Entidad</th>
	                	<th>Cartera</th>
	                	
	                </tr>
	            </thead>
	        </table>
	        </div>
		</div>
		
		<div id="dialog-form-Desembolso" title="Datos Desembolso (Oficio de No Objeción)" style="overflow-x:hidden;">
			<br>
			<table border="0" >
				<tr>
					<td align="right" style="width:33%">Oficio Agente Financiero No.:</td>
					<td  style="width:150px">
						<input id="dlgOficioNoObjecion" type="text" size="20" maxlength="20" style="text-transform:uppercase;"/> 
					</td>
				</tr>
				<tr>
					<td align="right" style="width:33%">Solicitud de Desembolso:</td>
					<td  style="width:150px">
						<input id="dlgSolicitudDesembolso" type="text" size="20" maxlength="20" style="text-transform:uppercase;"/> 
					</td>			
				</tr>
				<tr>
					<td align="right" >Tipo de Cambio:</td>	
					<td >
						<input id="dlgTipoCambioNoObjecion" type="text" size="5" maxlength="6" onkeypress="Validaciones(this,9)" style=" text-align:center"/>
					</td>
				</tr>
				<tr>			
					<td align="right" >Monto Desembolsado USD:</td>	
					<td >
						<input id="dlgMontoNoObjecion" type="text" size="15" maxlength="15" value="$0.00" onkeypress="Validaciones(this,9)" class="currency" style=" text-align:right"/>
					</td>	
				</tr>
				<tr>	
					<td align="right">Fecha Valor:</td>
					<td >
						<input id="dlgFechaNoObjecion" type="text" size="12" readonly style="background:#f0f0f0; text-align:center"/>
					</td>
				</tr>
	        </table>
		</div>

	</form>
	
	
	
	</body>
</html>