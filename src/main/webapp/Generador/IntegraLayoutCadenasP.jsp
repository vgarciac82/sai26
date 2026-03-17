<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIniciaEstatus = "NO";
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	cUR = usuario.getU_UR();

	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("INICIAESTATUS")) {
		cIniciaEstatus = usuario.getPropiedad("INICIAESTATUS")
				.getValor();
	}
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Layout Cadenas Productivas</title>

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
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" charset="utf-8">
	
	
		function inicio(){
			
			if ("<%=cIniciaEstatus%>" == "NO") {
				$("#pbStatusInicial").attr('disabled', true);
				$("#pbStatusInicial").hide();
			   	//pbStatusInicial.style.visibility="hidden";
			}

			document.getElementById('archivo').value = "";
			document.getElementById('sDataH').value = "";
			document.getElementById('sDataHCB').value = "";
			document.getElementById('sDataHFecha').value = "";
			//document.getElementById('sDataFolios').value = "";
			
		}
		
		$(document).ready(function(){
			
			
			$("#tabs").tabs( {
				"show": function(event, ui) {
		    		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		    		if ( oTable.length > 0 ) {
		    			oTable.fnAdjustColumnSizing();
		    		}
				}
			} );
			
			
			oTableCB = $("#tblCuentasBancarias").dataTable({
				bPaginate : false,
				//bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				sScrollX: "10px",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : true,
				bRetrive: true,
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
					{ sName: "cBanco",	bSearchable: false,	bSortable: false, bVisible: true },
					{ sName: "dBanco" },
					{ sName: "cPlaza" },
					{ sName: "dCuentaBancaria" },
					{ sName: "dDigitoVerificador" },
					{ sName: "dSucursal" },
					{ sName: "cStatusCuenta",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "dStatusCuenta" }
/*,					{ sName: "cIdTipoEntidadSiaff",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "dIdTipoEntidadSiaff",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "nEntidadSiaff",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "cIdTipoPagoEntidadSiaff",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "dIdTipoPagoEntidadSiaff",	bSearchable: false,	bSortable: false, bVisible: false  },
					{ sName: "nBCBEnviadoSICOP",	bSearchable: false,	bSortable: false, bVisible: false  }*/
				]
				
        	});
			
			//querySelectPost("CAT_UNIDAD_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora1", {async: false });

			querySelectPost("rCatalogoLeyendaOARead", "cIdLeyenda", {async: false });
			//queryFormPost("FechaInicialRead", {async: false });
			//queryFormPost("FechaFinalRead", {async: false });
			
			$("#FechaProgramada").val($("#FechaInicial").val());
			
			$("#FechaInicial1").val( $("#FechaInicial").val() );
			$("#FechaFinal1").val( $("#FechaFinal").val() );
			
			  fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
			 fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial").val(), $("#FechaFinal").val());			
		    $("#cIdUnidadEjecutora").val();
		    $("#FechaInicial").val();
		    $("#FechaFinal").val();
		    
			$("#tblCuentasBancarias tbody").click(function(event) {
			
					$(oTableCB.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					
				});
			
			
			$( "#cIdUnidadEjecutora" )
			.change(function() 
			{	
				//alert($("#cIdUnidadEjecutora").val());
				
				 fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
			$("#cIdUnidadEjecutora").val();
			$("#FechaInicial").val();
			$("#FechaFinal").val();
			});
			
			$( "#cIdUnidadEjecutora1" )
			.change(function() 
			{
				//alert($("#cIdUnidadEjecutora").val());
				
				 fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
				
			});
		
		});
			 
							
		function fnClickAddRow(row) {
			
			try
			{
			
			var table2 = $('#dt_paraEnvio').dataTable().fnAddData( [	
								row.cells[1].childNodes[0].toString(),
			      				row.cells[2].childNodes[0].toString(),
			      				row.cells[3].childNodes[0].toString(),
			      				row.cells[4].childNodes[0].toString(),
			      				row.cells[5].childNodes[0].toString(),
			      				row.cells[6].childNodes[0].toString(),
			      				row.cells[7].childNodes[0].toString(),
			      				row.cells[8].childNodes[0].toString()
			      	            //row.cells[9].childNodes[0].toString()
			      	            ] );
			dialogo();
			}
			
			catch (ex)
			{alert(ex.message);}
			
		}
		
		function fnClickDellRows(){
			var table2 = $('#dt_paraEnvio').dataTable();
			table2.fnClearTable();				
		}

		function Inicializa(){
			var vacio = true;
 			try {
 								
        		var table = document.getElementById('dt_CuentaConLayout');
 				var aTrs = $('#dt_CuentaConLayout').dataTable().fnGetNodes();
 				var cCOLUMNALLAVE = 2;
 				
 				//$('#chkRFCValido').is(':checked')
 				$('#sDataFolios').val("");
 				for ( var i=1; i<=aTrs.length;  i++ )     
				{   
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					//if ( $(aTrs[i]).hasClass('row_selected') )
					//alert(aTrs.length);
					if(null != chkbox && true == chkbox.checked)
					{ 								
						//var nTr = $('#dt_generados').dataTable().fnGetData(aTrs[i]);
						//alert("dentro del if");
						var caNoFolio = row.cells[cCOLUMNALLAVE].childNodes[0].toString(); // el 11 corresponde al folio
						
						$('#sDataFolios').val($('#sDataFolios').val() + " " + caNoFolio + " ,");
													
						vacio = false;
				    } 
				}
 				if (!vacio) {
 					alert($('#sDataFolios').val());
 					document.ConLayout.submit();	
 					location.reload(true);
 				}
            	
        	}catch(e) {
         		alert(e);
    		}
    		if(vacio){
    		    alert("Debe marcar al menos una fila");
    		    location.reload(true);
    		}
 		}
		
		function enviar(){
			var vacio = true;
			
 			try {
 				fnClickDellRows();
        		var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
 				var cCOLUMNALLAVE = 2;
 				var cCUENTABANCARIA = 5;
 				var cRFC = 3;
 				var ImporteMin = 9999999999999999999999;
 				var ImporteMax = -999999999999999
 				var sumaEnv = 0;
				document.getElementById('sDataH').value = " ";
				document.getElementById('sDataHCB').value = " ";
				document.getElementById('sDataHFecha').value = " ";
				//document.getElementById('sDataHLeyenda').value = " ";		
				document.getElementById('sDataHcontrarecibo').value = "";
 				for ( var i=0; i<=aTrs.length;  i++ )     
					{   
 						var row= table.rows[i];
 						var chkbox = row.cells[0].childNodes[0];
 						//if ( $(aTrs[i]).hasClass('row_selected') )
 						//alert(aTrs.length);
 						
 						if(null != chkbox && true == chkbox.checked)
						{
 							
 							$("#pbEnvia").css("visibility","visible");
 							$("#pbEnviaDocumentacion").css("visibility","visible");
 							$("#pbEnviaDoc").css("visibility","visible");
 							
							
							var caNoFolio = row.cells[cCOLUMNALLAVE].childNodes[0].toString(); // el 11 corresponde al folio
							var caNoCuentaBancaria = row.cells[5].childNodes[0].toString(); // el 11 corresponde al folio
							var caNoFecha = row.cells[7].childNodes[0].toString(); // el 11 corresponde al folio					
							var caNoContrarecibo = row.cells[8].childNodes[0].toString();
						  	var unidadEject = row.cells[1].childNodes[0].data;
						  	var RFC = row.cells[3].childNodes[0].data;
							var ImporteMin1 = row.cells[4].childNodes[0].data;
							var CXP = row.cells[8].childNodes[0].data;
							var sumaEnviados =  row.cells[4].childNodes[0].data;
							
							sumaEnv = Number(sumaEnv) + Number( quitaFmt(sumaEnviados) );
							
							document.getElementById('sDataH').value += " "+ caNoFolio +"," ;
							document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
							document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
							//document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
							document.getElementById('sDataHcontrarecibo').value += " '" + caNoContrarecibo + "'," ;
							
							if ( ImporteMin  > Number( quitaFmt(ImporteMin1) ) ){
			
								ImporteMin = Number( quitaFmt(ImporteMin1) );
								$("#ImpMin").val(ImporteMin).formatCurrency();
								$("#Unidad").val(unidadEject);
								$("#CxP").val(CXP);
								
							}
							if ( ImporteMax  < Number( quitaFmt(ImporteMin1) ) ){
			
								ImporteMax = Number( quitaFmt(ImporteMin1) );
								$("#ImpMax").val(ImporteMax).formatCurrency();
								$("#Unidad2").val(unidadEject);
								$("#CxP2").val(CXP);
								
							}		
							$("#SumaDoc").val(sumaEnv).formatCurrency();
							vacio = false;							
				      		fnClickAddRow(row);	
					    }		
					}		
            	
        	}catch(e) {
         	//	alert(e);
    		}
    		if(vacio){
    		   // alert("Debe marcar al menos una fila");
    		}
 		}

 		function generar(){
 		guardartbLayout ();
 			
 			try 
 			{	
 				
 				$( "#dialog-Reporte" ).dialog( "open" );
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount2 = table2.rows.length;
            	
            	if (rowCount2 == 0)
            	{
            		alert("Debe seleccionar al menos un pago");
            		return;
            	}
            	
            	document.getElementById('archivo').value = "1";
            	document.location.href='../gstnmngr/generaLayoutCadenasP?archivo=1';
            	document.envioSICOP.submit();
				
            	 fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
            	
            	fnClickDellRows();
            	
            	$("#pbEnvia").css("visibility","hidden");
         	}
 			catch(e) {
         		alert(e);
         	}
 		}
	
 		function generarDocumentacion(){
 			try {
        		var table = document.getElementById('dt_generados');
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount = table.rows.length;
            	var rowCount2 = table2.rows.length;
            	
            	$("#SumDoc").val();
            	if (rowCount2 == 0)
            	{
            		alert("Debe seleccionar al menos un pago");
            		return;
            	}
            	
            	for(var i=0; i<rowCount; i++) {
            		var row = table.rows[i];
                	var chkbox = row.cells[0].childNodes[0];
                	
                	if(null != chkbox && true == chkbox.checked) {
						//quitando de la lista de compromisos los registros enviados a SICOP...
						//table.deleteRow(i);
						// Dubois no puede borrar indices iguales de tablas diferentes, por eso lo comente
						//table2.deleteRow(i);
                    	rowCount--;
                    	i--;
                    	
                    	//table.deleteRow(i);
                    	//rowCount--;
                    	//alert(rowCount + "psc");
                	}
            	}
            	document.getElementById('archivo').value = "2";
            	document.location.href='../gstnmngr/generaLayoutCadenasP?archivo=2';
            	document.envioSICOP.submit();
            	
            	//document.envioSICOP.submit();
            	
         	}catch(e) {
         		alert(e);
         	}
 		}
 		
		function toggleReactivar(status) {
			$("input:checkbox").each( 
				function() {
					$(this).attr("checked",status.checked);
				}
			);
		}

		function convertirAFecha(string) 
		{
			 var date = new Date()
			 mes = parseInt(string.substring(3, 5), 10);
			 date.setMonth(mes - 1); //en javascript los meses van de 0 a 11
			 date.setDate(string.substring(0, 2));
			 date.setYear(string.substring(6, 10));
			 return date;
		}
		
		function valFecha(object1) 
		{
			var FechaIni = document.getElementById("FechaInicial").value;
		    var FechaFin = document.getElementById("FechaFinal").value;
			
		    if (object1.value != "") 
			{
		      //if (Verifica_Fecha(object1.value) == false) 
		      //{
		      //   object1.value = "";
		      //   alert("Formato de fecha incorrecto. Debe ser DD/MM/YYYY" + " " + object1.value);
		      //}
		      
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) 
		      {
		    	  alert("La fecha incial no puede ser mayor a la fecha final");
		         document.getElementById("FechaFinal").value = document.getElementById("FechaInicial").value;
		      }
		      fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
		    
		   }
		}
		
		function valFecha1(object1) 
		{
			var FechaIni = document.getElementById("FechaInicial1").value;
		    var FechaFin = document.getElementById("FechaFinal1").value;
			
		    if (object1.value != "") 
			{
		      //if (Verifica_Fecha(object1.value) == false) 
		      //{
		      //   object1.value = "";
		      //   alert("Formato de fecha incorrecto. Debe ser DD/MM/YYYY" + " " + object1.value);
		      //}
		      
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) 
		      {
		    	  alert("La fecha incial no puede ser mayor a la fecha final");
		         document.getElementById("FechaFinal1").value = document.getElementById("FechaInicial1").value;
		      }
		      fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
		   }
		}
		
		function Verifica_Fecha(pstrFecha)
	    {
	        // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	        // :: Proposito : Verifica si pstrFecha, contiene un formato y fecha ::
	        // ::             correcta, del tipo dd/mm/aaaa                      ::
	        // :: Entradas  : pstrFecha, string a validar                        ::
	        // ::                                                                ::
	        // :: :
	        // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	        //alert('va');
	        var strCharCorrectos = "0123456789/";
	        var Fecha = pstrFecha;
		    if (Fecha == "")
			    return false;
		
		    for (i=0; i < Fecha.length;i++)
	    		{   var car = Fecha.substr(i,1);
				    if (strCharCorrectos.indexOf(car)==-1)
				        {
					      return false;
					    }
			    }
		    
		    /* ::::::::::::::::::::::::::::::::::::::::::::::
		       Creamos un arreglo con los datos de la fecha 
		       separados por la diagonal
		       ::::::::::::::::::::::::::::::::::::::::::::::
		    */ dd=0
		       mm=1
		       aaaa=2
		    ArrayFecha = Fecha.split("/");
		    if(ArrayFecha.length!=3)
	    	    return false;
	    
		    var Dia  = Number(ArrayFecha[0]);
		    var Mes  = Number(ArrayFecha[1]);
		    var Anno = Number(ArrayFecha[2]);
		    var TemAno =String(ArrayFecha[2]);
		    
	    	
	    	if (TemAno.length != 4 )  return false;
	    	if (Dia > 31 || Dia < 1 ) return false;
	    	if (Mes >12 || Mes < 1 )  return false;
	    	if (Mes == 4 || Mes == 6 || Mes == 9 || Mes == 11)
	    	    {
		        if (Dia > 30 ) return false;
		        }	
		    if ( Mes == 2 )
		        {
	    	    if ((Anno % 4)==0 )  /* Se verifica si el Anno es biciesto **/
				    { 
				    if (Dia > 29 ) return false;				
				    }
			    else{
					 if (Dia > 28) return false;
					}
	            }
	    
	        return true;
	
        }
		
		$(function() {
			$( "#FechaInicial" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				showAnim:"slideDown",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
	    });
	
		$(function() {
			$( "#FechaFinal" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				showAnim:"slideDown",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
	
		$(function() {
			$( "#FechaInicial1" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
	    });
	
		$(function() {
			$( "#FechaFinal1" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
	
		$(function() {
			$( "#FechaProgramada" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
	    });
		function toggle(status) {
			$("input:checkbox").each( 
				function() {
					$(this).attr("checked",status.checked);
				}
			);
		}
		
		$(function() {
			
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 350,
				width: 650,
				modal: true,
				buttons: {"Aceptar": function() {
					var table = document.getElementById('tblCuentasBancarias');
	 				var aTrs = $('#tblCuentasBancarias').dataTable().fnGetNodes();
	 				var cCuentaBancaria = 4;
	 				var strCuentaBancaria = "";
	 				var bSel = false;

	 				for ( var i=0; i<aTrs.length;  i++ )     
					{         
 						var row= table.rows[i];
 						
 						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
 							var nTr = $('#tblCuentasBancarias').dataTable().fnGetData(aTrs[i]);
 							
 							strCuentaBancaria = $("#cBanco"+i).val();
 							strCuentaBancaria += $("#cPlaza"+i).val();
							strCuentaBancaria += $("#dCuentaBancaria"+i).val();
							strCuentaBancaria += $("#dDigitoVerificador"+i).val();
							bSel = true;
							break;
					    } 	 						
					}
	 				if (!bSel)
 					{
 						alert("Advertencia...!\r\rNo ha seleccionado ninguna Cuenta Bancaria.");
 						return;
 					}
	 				
					// escribir la cuenta bancaria
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cCuentaBancaria = 5;
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						var row= table.rows[i];
	 						
	 						if ( $(aTrs[i]).hasClass('row_selected') )         
							{
	 							table.rows[i+1].cells[5].childNodes[0].nodeValue =strCuentaBancaria;
	 							break;
						    }  						
						}
						$( this ).dialog( "close" );
					},
					"Cancelar": function() {
						
						$( this ).dialog( "close" );
					}
				},
				close: function() {
										
				}
			});
		
					
			$( "#dialog-formFecha" ).dialog({
				autoOpen: false,
				height: 200,
				width: 250,
				modal: true,
				buttons: {
					"Aceptar": function() {
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cFechaProgramada = 7;
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						var row= table.rows[i];
	 						
	 						if ( $(aTrs[i]).hasClass('row_selected') )         
							{
	 							table.rows[i+1].cells[cFechaProgramada].childNodes[0].nodeValue = $("#FechaProgramada").val();
	 							break;
						    }	 							 						
						}
						$( this ).dialog( "close" );
					},
					"Cancelar": function() {
						
						$( this ).dialog( "close" );
					}
				},
				close: function() {										
				}
			});
		
		$( "#dialog-formLeyenda" ).dialog({
				autoOpen: false,
				height: 150,
				width: 650,
				modal: true,
				buttons: {
					"Aceptar": function() {

					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cLeyenda = 8;
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						var row= table.rows[i];
	 						
	 						if ( $(aTrs[i]).hasClass('row_selected') )         
							{
	 							table.rows[i+1].cells[cLeyenda].childNodes[0].nodeValue = $("#cIdLeyenda").val();
	 							break;
						    } 	 						
						}
					$( this ).dialog( "close" );
					},
					"Cancelar": function() {						
						$( this ).dialog( "close" );
					}
				},
				close: function() {										
				}
			});
			
		$( "#pbLeyenda" )
			.button()
			.click(function() {
				if (!fnValidaRowSel())
				{
					alert("Debe seleccionar primero un renglón.");
					return;
				}
				$( "#dialog-formLeyenda" ).dialog( "open" );
			});
			
		$( "#pbFechaProgramada" )
			.button()
			.click(function() {
				if (!fnValidaRowSel())
				{
					alert("Debe seleccionar primero un renglón.");
					return;
				}
				$( "#dialog-formFecha" ).dialog( "open" );
			});
			
		function fnValidaRowSel()
		{
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var bSel = false;
			
			for ( var i=0; i<aTrs.length;  i++ )     
			{         
				var row= table.rows[i];
				
				if ( $(aTrs[i]).hasClass('row_selected') )         
				{
					bSel = true; 
					break;
			    }  						
			}
			return bSel;	
		}
				
		$( "#pbCuentasBancarias" )
		
			.button()
			.click(function() {
				
				if (!fnValidaRowSel())
				{
					alert("Debe seleccionar primero un renglón.");
					return;
				}
				
				var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
 				var cRFC = 3;
 				var strRFC = "";
					
 				for ( var i=0; i<aTrs.length;  i++ )     
				{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{
						var nTr = $('#dt_generados').dataTable().fnGetData(aTrs[i]); 
						strRFC = nTr[3]; // el 11 corresponde al folio
						break;
				    }  						
				}
 				$('#tblCuentasBancarias').dataTable().fnClearTable();
					
				var szWhere = " dRFC = '" + strRFC + "' "; 
				var szTabla = "BENEFICIARIOCUENTASBANCARIAS";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{     
						for (var i = 0; i < j.length; i++) 
    					{
	    					$('#tblCuentasBancarias').dataTable().fnAddData([ 
								"<input type='text' id='cBanco" + i + "' name='cBanco' value='" + j[i].Col1 + "'  style='width:55px; border-width:0; background-color:transparent'/>", 
								"<input type='text' id='dBanco' name='dBanco' value='" + j[i].Col6 + "' readonly style='width:130px; border-width:0; background-color:transparent'/>", 
								"<input type='text' id='cPlaza" + i + "' name='cPlaza' value='" + j[i].Col2 + "' readonly style='width:38px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dCuentaBancaria" + i + "' name='dCuentaBancaria' value='" + j[i].Col3 + "' readonly style='width:100px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dDigitoVerificador" + i + "' name='dDigitoVerificador' value='" + j[i].Col4 + "' readonly style='width:35px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dSucursal' name='dSucursal' value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='cStatusCuenta' name='cStatusCuenta' value='" + j[i].Col5 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dStatusCuenta' name='dStatusCuenta' value='" + ((j[i].Col5==1)? "Activo":"Inactivo") + "' readonly style='width:60px; border-width:0; background-color:transparent'/>"
/*	,								
								"<input type='text' id='cIdTipoEntidadSiaff' name='cIdTipoEntidadSiaff' value='" + j[i].Col8 + "' readonly style='width:50px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dIdTipoEntidadSiaff' name='dIdTipoEntidadSiaff' value='" + j[i].Col14 + "'readonly style='width:220px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='nEntidadSiaff' name='nEntidadSiaff' value='" + j[i].Col9 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='cIdTipoPagoEntidadSiaff' name='cIdTipoPagoEntidadSiaff' value='" + j[i].Col10 + "' readonly style='width:30px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='dIdTipoPagoEntidadSiaff' name='dIdTipoPagoEntidadSiaff' value='" + j[i].Col15 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='nBCBEnviadoSICOP' name='nBCBEnviadoSICOP' value='" + j[i].Col13 + "' readonly style='width:10px; border-width:0; background-color:transparent'/>"*/
								]); 
    					}
		         });   
				
				
				$( "#dialog-form" ).dialog( "open" );				
			});
		});

   
	</script>
	</head>

  	<body id="dt_example" onLoad="inicio();" >
		<div  id="container" >
			<h1>Layout Cadenas Productivas <label id="lbOperacion" style="font-size: 8pt"></label></h1>			
        	<div id="tabs">    		
        		<ul>
					<li><a href="#tabs-1">Pendientes de generar Layout</a></li>
					<li><a href="#tabs-2" onClick="enviar();">Pagos en proceso Cadenas Productivas</a></li>
					<li><a href="#tabs-3" onClick ="fnGridConLayout()">Layouts generados</a></li>
				</ul>
			
				<div id="tabs-1">	
			  		<form>
		        	<table width="100%" border="0">
			        	<tr>	        	
				        	<td width="130px" align="right">Unidad Ejecutora:</td>	
				        				        	
				        	<td> 
				            	<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"  class="paso01" style="width: 20em;">
				            		<option value="<%=cUR%>"></option>
					            </select>
					              <input name="Desmar" type="button" id="Desmar" value="Desmarcar"	onclick="desmarkar()" />
					        </td>				           
				            <td align="right"> Fecha Inicio: </td>				        	
				        	<td>
		            			<input onchange="valFecha(this)" name="FechaInicial" type="text" id="FechaInicial" class="paso01" size="10" readonly/> 
					        </td>
					        <td  align="right"> Fecha Final: </td>				        	
				        	<td>
					            <input onchange="valFecha(this)"  name="FechaFinal" type="text" id="FechaFinal" class="paso01" size="10" readonly/> 
					        </td>
				        </tr>
				        <tr>
					        <td colspan="6" align="right">
<%--					        	<input type="button" id="pbCuentasBancarias" style="visibility: hidden" value="Cuenta Bancaria"> &nbsp; &nbsp; &nbsp;--%>
<%--					        	<input type="button" id="pbFechaProgramada" style="visibility: hidden" value="Fecha Programada"> &nbsp; &nbsp; &nbsp;--%>
<%--					        	<input type="button" id="pbLeyenda" style="visibility: hidden" value="Leyenda">  &nbsp; &nbsp; &nbsp;     	--%>
					        </td>
				        </tr>
			        </table>
		          
					<fieldset>
						<legend> Pagos sin layout	</legend>
						<jsp:include page="listaCadenasP.jsp"></jsp:include>
					</fieldset>
					</form>	
				</div>
				<div id="tabs-2">				
				    <fieldset>
						<legend> Integraci&oacute;n del layout para ser enviado a SICOP  </legend>
				    	<jsp:include page="listaCadenasPEnviados.jsp"></jsp:include>
					</fieldset>
				</div>
		        <div id="tabs-3">
		        	<table width="100%" border="0">
			        	<tr>	        	
				        	<td width="130px" align="right">Unidad Ejecutora:</td>				        	
				        	<td> 
				            	<select id="cIdUnidadEjecutora1" name="cIdUnidadEjecutora1"  class="paso01" style="width: 20em;">
				            		<option value="<%=cUR%>"></option>
					            </select>
					        </td>				           
				            <td align="right"> Fecha Inicio: </td>				        	
				        	<td>
		            			<input onchange="valFecha1(this)" name="FechaInicial1" type="text" id="FechaInicial1" class="paso01" size="10" readonly/> 
					        </td>
					        <td  align="right"> Fecha Final: </td>				        	
				        	<td>
					            <input onchange="valFecha1(this)"  name="FechaFinal1" type="text" id="FechaFinal1" class="paso01" size="10" readonly/> 
					        </td>
				        </tr>
			        </table>
		        	<input type="button" id="pbStatusInicial" name="pbStatusInicial" onClick="Inicializa();" value="Inicia Estatus">	        	
				    <fieldset>
						<legend> Pagos enviados a SICOP </legend>
				    	<jsp:include page="listaCadenasPConLayout.jsp"></jsp:include>
					</fieldset>
				</div>		        
				</div>
		
			<div id="dialog-form" title="Selección de Cuenta Bancaria">
				<table id="tblCuentasBancarias" class="display" width="10px" >
					<thead>
						<tr>
							<th>Clave</th>
							<th>Banco</th>
							<th>Plaza</th>
							<th>No.Cuenta</th>
							<th>Dígito</th>
							<th>Sucursal</th>
							<th>cStatus</th>
							<th>Status</th>
						</tr>
					</thead>
				</table>
			</div>
			
			<div id="dialog-formFecha" title="Selección de Fecha">
				Fecha Inicio: 
            	<input  name="FechaProgramada" type="text" id="FechaProgramada" class="paso01" size="10" readonly> 
			</div>
			
			<div id="dialog-formLeyenda" title="Selección de Leyenda">
		 		<select id="cIdLeyenda" class="paso01" name="cIdLeyenda" style="width: 600px;">
	              <option value="Z1:"></option>
	              <option value="Y1:"></option>
	              <option value="X1:"></option>
	              <option value="3" selected> -Leyenda- </option>
	            </select>
			</div>
		</div>
	</body>	
</html>
