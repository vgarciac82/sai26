<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIniciaEstatus = "NO";
	String cDevuelveConsolidada = "NO";
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	cUR = usuario.getU_UR();

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("INICIAESTATUS")){
		cIniciaEstatus = usuario.getPropiedad("INICIAESTATUS").getValor();
	}
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("DEVUELVECONSOLIDADA")){
		cDevuelveConsolidada = usuario.getPropiedad("DEVUELVECONSOLIDADA").getValor();
	}
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Layout de RG con OC</title>

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
	
		function inicio()
		{
			//if ("<%=cIniciaEstatus%>" == "NO") {
				$("#pbStatusInicial").attr('disabled', true);
				$("#pbStatusInicial").hide();
			//}
			if ("<%=cDevuelveConsolidada%>" == "NO") {
				$("#btnDevuelveConsolidada").attr('disabled', true);
				$("#btnDevuelveConsolidada").hide();
			}
			
			document.getElementById('archivo').value = "";
			document.getElementById('sDataH').value = "";
			document.getElementById('sDataHCB').value = "";
			document.getElementById('sDataHFecha').value = "";
			document.getElementById('sDataFolios').value = "";
		}
		
		function formatCurrency(num) 
		{
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num)) num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10) cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
			num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));
			//return (((sign) ? '' : '-') + '$' + num + '.' + cents);
			return (((sign) ? '' : '-') + num + '.' + cents);
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
				bInfo : false,
				bAutoWidth : false,
				sScrollX: "10px",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : true,
				bRetrive: true,
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
				]
				
        	});
			
			oTableCBUE = $("#tblCuentasBancariasUEjecutora").dataTable({
				bPaginate : false,
				//bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,
				//sScrollX: "10px",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bRetrive: true,
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
				aaSorting: [[ 2, "asc" ]] ,
				aoColumns: [
					{ sName: "strUnidadEjecutora",	bSearchable: false,	bSortable: false, bVisible: true },
					{ sName: "strRFC" },
					{ sName: "strNombreBeneficiario" },
					{ sName: "strClabe" },
					{ sName: "strTipoCuenta" }
				]
				
        	});
			
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora1", {async: false });

			querySelectPost("rCatalogoLeyendaRead", "cIdLeyenda", {async: false });
			querySelectPost("rCatalogoLeyendaRead", "cIdLeyendaUEjecutora", {async: false });
			queryFormPost("FechaInicialRead", {async: false });
			queryFormPost("FechaFinalRead", {async: false });

			$( "#cIdUnidadEjecutora" ).val('<%=cUR%>');
			$( "#cIdUnidadEjecutora" ).attr("disabled", true);

			
			$("#FechaProgramada").val($("#FechaInicial").val());
			
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
			$("#FechaInicial1").val( $("#FechaInicial").val()); 
			$("#FechaFinal1").val( $("#FechaFinal").val() );
			fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());			
		
			$("#tblCuentasBancarias tbody").click(function(event) {
					$(oTableCB.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					
				});
						
			$("#tblCuentasBancariasUEjecutora tbody").click(function(event) {
					$(oTableCBUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					
				});
			
			
			$( "#cIdUnidadEjecutora" )
			.change(function() 
			{
				//alert($("#cIdUnidadEjecutora").val());
				$("#checkAll").prop("disabled", true);				
				//Desactiva el RadioButton seleccionado
				if ($("#integraprosub").is(':checked'))
					$("#integraprosub").attr("checked", false);
				else if ($("#integranomina").is(':checked'))
					$("#integranomina").attr("checked", false);
				else if ($("#integraempleado").is(':checked'))
					$("#integraempleado").attr("checked", false);

				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());	
				
			});
			
			$( "#cIdUnidadEjecutora1" )
			.change(function() {				
				fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
			});
			
			$("#chkIntegra").change(function(){
				var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
 				
				if ($('#chkIntegra').is(':checked')){
			        alert("¡ AVISO !\r\rLa Integración suma los montos y aplica la transferencia \ra las cuentas de la Unidad Ejecutora.");
			        
			        $('#dt_paraEnvio').dataTable().fnSetColumnVis( 1, false );
			    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 2, false );
			    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 5, false );
			    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 8, false );
			    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 9, false );
			    	for ( var i=0; i<aTrs.length;  i++ )     
					{         
 						var row= table.rows[i];
 						table.rows[i+1].cells[5].childNodes[0].nodeValue ="--";  
 						table.rows[i+1].cells[7].childNodes[0].nodeValue ="--";
 						table.rows[i+1].cells[8].childNodes[0].nodeValue ="--";
					}
			    }
			});	
			
			$("#checkAll").change(function(){
				var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				if ($('#checkAll').is(':checked')){
					$("input:checkbox").attr('checked', 'checked');
			    }else{
			    	$("input:checkbox").removeAttr('checked');
			    	$("#chkIntegra").attr('checked', 'checked');
			    }
			});
		});
			 
		function Inicializa()
		{
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
 				
    		if(!vacio){
				document.RGConLayout.submit();	
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
 		
 		function devuelveConsolidada(){
			var cont = 0;
 			try {
        		var table = document.getElementById('dt_CuentaConLayout');
 				var aTrs = $('#dt_CuentaConLayout').dataTable().fnGetNodes();
 				var columna= 11; //folio integracion

 				$('#sDataFolios').val("");
 				for ( var i=1; i<=aTrs.length; i++){
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					if(null != chkbox && true == chkbox.checked){
						$('#sDataFolios').val(row.cells[columna].childNodes[0].toString());
						//$('#sDataFolios').val(caNoFolio);
						cont++;
				    }
				}
				if (cont!=1){
					if (cont==0)
						alert("Debe seleccionar un registro.");
					else
						alert("Para esta opción debe seleccionar un solo registro.");
    		    	return;
				}else{
					if (confirm("Esta seguro de regresar la Integracion: " +$('#sDataFolios').val())){
						document.PagoDiversoRGConLayout.submit();
						alert("Se ha devuelto la Integracion "+$('#sDataFolios').val());
						fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
						//location.reload(true);
					}
				}
        	}catch(e) {
         		alert(e);
    		}
 		}
		
		function fnClickAddRow(row) 
		{
			try
			{
				if ($('#chkIntegra').is(':checked')){
					var bExiste = false;
					var szUEjecutora = '';
					var table = document.getElementById('dt_paraEnvio');
					var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
					for ( var i=0; i<aTrs.length;  i++ )     
					{         				
						szUEjecutora = $.trim(table.rows[i+1].cells[0].childNodes[0].nodeValue) ;
						if ($.trim(row.cells[1].childNodes[0].toString()) == szUEjecutora){
							bExiste = true;
							break;
						}
					}			
					if (bExiste){
						var Monto = parseFloat($.trim(table.rows[i+1].cells[1].childNodes[0].nodeValue).replace(",",""));
						Monto = Monto + parseFloat(row.cells[4].childNodes[0].toString().replace(",",""));
						
						table.rows[i+1].cells[1].childNodes[0].nodeValue = formatCurrency(Monto);
					}else{
						$('#dt_paraEnvio').dataTable().fnAddData( [	
							row.cells[1].childNodes[0].toString(),
		      				row.cells[2].childNodes[0].toString(),
		      				row.cells[3].childNodes[0].toString(),
		      				row.cells[4].childNodes[0].toString(),
		      				row.cells[5].childNodes[0].toString(),
		      				row.cells[6].childNodes[0].toString(),
		      				row.cells[7].childNodes[0].toString(),
		      				row.cells[8].childNodes[0].toString(),
		      				0,
		      	            row.cells[9].childNodes[0].toString()
		      	            ] );		
					}
				}else{
					var table2 = $('#dt_paraEnvio').dataTable().fnAddData( [	
							row.cells[1].childNodes[0].toString(),
		      				row.cells[2].childNodes[0].toString(),
		      				row.cells[3].childNodes[0].toString(),
		      				row.cells[4].childNodes[0].toString(),
		      				row.cells[5].childNodes[0].toString(),
		      				row.cells[6].childNodes[0].toString(),
		      				row.cells[7].childNodes[0].toString(),
		      				row.cells[8].childNodes[0].toString(),
		      				0,
		      	            row.cells[9].childNodes[0].toString()
		      	            ] );
				}
			}
			catch (ex)
			{alert(ex.message);}
		}
		
		function fnClickDellRows()
		{
			var table2 = $('#dt_paraEnvio').dataTable();
			table2.fnClearTable();						
		}
		
		function enviar(){
			var vacio = true;
 			try {
 				fnClickDellRows();
        		var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
 				var cCOLUMNALLAVE = 2;
 				var cCUENTABANCARIA = 5;
 				var cFECHAPROG = 7;
 				var cLeyenda = 8;
 				var cRFC = 3;
 				
 				document.getElementById('sDataH').value = " ";
				document.getElementById('sDataHCB').value = " ";
				document.getElementById('sDataHFecha').value = " ";
				document.getElementById('sDataHLeyenda').value = " ";		
				
 				for ( var i=1; i<=aTrs.length;  i++ )     
					{   
 						var row= table.rows[i];
 						var chkbox = row.cells[0].childNodes[0];
 						
 						if(null != chkbox && true == chkbox.checked){
 							if (row.cells[cCUENTABANCARIA].childNodes[0].toString() == "--" ){
 								alert("El RFC : " + row.cells[cRFC].childNodes[0].toString() + " no tiene asignada Cuenta Bancaria");
 								//location.reload(true);
 								return;
 							}
 							if (row.cells[cFECHAPROG].childNodes[0].toString() == "--"){
 								alert("La Unidad : " + row.cells[1].childNodes[0].toString() + " no tiene asignada Fecha Programada");
 								return;
 							}
 							if (row.cells[cLeyenda].childNodes[0].toString() == "--"){
 								alert("La Unidad : " + row.cells[1].childNodes[0].toString() + " no tiene asignada Leyenda");
 								return;
 							}
 							$("#pbEnvia").css("visibility","visible");
 							$("#pbEnviaDocumentacion").css("visibility","visible");
 							$("#pbEnviaDoc").css("visibility","visible");
 							

							var caNoFolio = row.cells[cCOLUMNALLAVE].childNodes[0].toString(); // el 11 corresponde al folio
							var caNoCuentaBancaria = row.cells[5].childNodes[0].toString(); // el 11 corresponde al folio
							var caNoFecha = row.cells[7].childNodes[0].toString(); // el 11 corresponde al folio
							var caNoLeyenda = row.cells[8].childNodes[0].toString();
							
							document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
							document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
							document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
							document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
							
							vacio = false;
				      		fnClickAddRow(row);
					    } 
					}
        	}catch(e) {
         		alert(e);
    		}
    		if(vacio){
    		    alert("Debe marcar al menos una fila");
    		}
 		}
		
		function fnGuardaUE()
		{
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			for ( var i=0; i<aTrs.length;  i++ )     
			{         				
				var szValues = "'" + $.trim(table.rows[i+1].cells[1].childNodes[0].nodeValue) + "', "; 
				 szValues += " " + $.trim(table.rows[i+1].cells[2].childNodes[0].nodeValue) + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[3].childNodes[0].nodeValue) + "', ";
				 szValues += " " + $.trim(table.rows[i+1].cells[4].childNodes[0].nodeValue).replace(",","") + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[5].childNodes[0].nodeValue) + "', ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[7].childNodes[0].nodeValue) + "', ";
				 szValues += " " + $.trim(table.rows[i+1].cells[8].childNodes[0].nodeValue) + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[9].childNodes[0].nodeValue) + "', ";
				 szValues += "'', ";		// strFolioInterno
				 szValues += "0";
				
				var szTabla = "M_RGINTEGRACION";                                                                                        
				$.getJSON("../catalogos/InsertJson.jsp",{Tabla: szTabla, Param: szValues, MaxReg: 20, ajax: 'false'}, 
					function(j)
					{     

		         });   					
			}			
		}

		function generar(){
 			try {
        		var table = document.getElementById('dt_generados');
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount = table.rows.length;
            	var rowCount2 = table2.rows.length;
            	
            	if (rowCount2 == 0){
            		alert("Debe seleccionar al menos un pago");
            		return;
            	}
            	            	
            	if ($('#chkIntegra').is(':checked')){
            		fnGuardaUE();
           			document.getElementById('archivo').value = "2";
           		}else{
           			document.getElementById('archivo').value = "1";
           		}
            	
            	document.location.href='../gstnmngr/generaLayoutPagoDiversoRelGastos';
            	document.envioSICOP.submit();
				
            	fnClickDellRows();
            	fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
            	
            	$("#pbEnvia").css("visibility","hidden");
         	}
 			catch(e) {
         		alert(e);
         	}
         	//location.reload(true);
         	fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val());
 		}
		
  		function generarDocumentacion(){
 			try {
        		var table = document.getElementById('dt_generados');
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount = table.rows.length;
            	var rowCount2 = table2.rows.length;
            	
            	if (rowCount2 == 0){
            		alert("Debe seleccionar al menos un pago");
            		return;
            	}
            	
            	for(var i=0; i<rowCount; i++) {
            		var row = table.rows[i];
                	var chkbox = row.cells[0].childNodes[0];
                	
                	if(null != chkbox && true == chkbox.checked) {
						//quitando de la lista de compromisos los registros enviados a SICOP...
						table.deleteRow(i);
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
            	document.location.href='../gstnmngr/generaLayoutPagoDiversoRelGastos?archivo=2';
            	document.envioSICOP.submit();
            	
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
			 var date = new Date();
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
		    */ dd=0;
		       mm=1;
		       aaaa=2;
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
				    	if (Dia > 29 ) 
				    		return false;				
				    }else{
					 	if (Dia > 28) 
					 		return false;
					}
	            }
	    
	        return true;
	
        }
		
		$(function() {
			$( "#FechaInicial" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
	    });
	
		$(function() {
			$( "#FechaFinal" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
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
			
			$( "#FechaProgramadaUEjecutora" ).datepicker({
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
			
			$( "#dialog-formUEjecutora" ).dialog({
				autoOpen: false,
				height: 500,
				width: 900,
				modal: true,
				buttons: {"Aceptar": function() {
					var table = document.getElementById('tblCuentasBancariasUEjecutora');
	 				var aTrs = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetNodes();
	 				var cCuentaBancaria = 4;
	 				var strCuentaBancaria = "";
	 				var bSel = false;

	 				for ( var i=0; i<aTrs.length;  i++ )     
					{         
 						var row= table.rows[i];
 						
 						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
 							var nTr = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetData(aTrs[i]);
 							
 							strCuentaBancaria = $("#strClabe"+i).val();
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
	 				var UnidadEjec = "";
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						//if(table.rows[i+1].cells[1].childNodes[0].nodeValue == UnidadEjec){  //URVP.12082014. SE COMENTARIZA YA QUE NO NECESITA VALIDAR RENGLON POR UR, POR QUE TODA LA INTEGRACION TENDRA LA MISMA CUENTA BANCARIA
 	 						table.rows[i+1].cells[5].childNodes[0].nodeValue =strCuentaBancaria;//}  						
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
			
			$( "#dialog-formFechaUEjecutora" ).dialog({
				autoOpen: false,
				height: 200,
				width: 250,
				modal: true,
				buttons: {
					"Aceptar": function() {

	 				var strFechaProg = $('#FechaProgramadaUEjecutora').val() ;

	 				var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var UnidadEjec = "";
	 				
	 				/* for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						if ( $(aTrs[i]).hasClass('row_selected') )         
							{
	 							UnidadEjec = table.rows[i+1].cells[1].childNodes[0].nodeValue;
	 							break;
						    }  						
						} */
					//URVP.12082014. SE COMENTARIZA YA QUE SE NECESITA LA UR DE LA UR QUE ESTA HACIENDO LA INTEGRACION Y NO LA DE LA RG SELECCIONADA
	 				UnidadEjec=$("#cIdUnidadEjecutora").val();//URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						//if(table.rows[i+1].cells[1].childNodes[0].nodeValue == UnidadEjec){ //URVP.12082014.SE COMENTARIZA YA QUE SE NECESITA TENER LA MISMA FECHA PROGRAMADA EN TODAS LAS RG INTEGRADAS
	 							table.rows[i+1].cells[7].childNodes[0].nodeValue = $("#FechaProgramadaUEjecutora").val(); //}  						
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
					var bExiste = false;
					var table = document.getElementById('dt_generados');
		 			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
					for ( var i=0; i<aTrs.length;  i++ )     
					{         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							bExiste = true;
							break;
					    }  						
					}
					if (! bExiste)
					{
						alert("Debe primero seleccionar un renglon.");
						return;
					}
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
			$( "#dialog-formLeyendaUEjecutora" ).dialog({
				autoOpen: false,
				height: 150,
				width: 650,
				modal: true,
				buttons: {
					"Aceptar": function() {
					var bExiste = false;
					var table = document.getElementById('dt_generados');
		 			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
					for ( var i=0; i<aTrs.length;  i++ )     
					{         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							bExiste = true;
							break;
					    }  						
					}
					if (! bExiste)
					{
						alert("Debe primero seleccionar un renglon.");
						return;
					}
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var UnidadEjec = "";
	 				
	 				/* for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						if ( $(aTrs[i]).hasClass('row_selected') )         
							{
	 							UnidadEjec = table.rows[i+1].cells[1].childNodes[0].nodeValue;
	 							break;
						    }  						
						} */
					//URVP.12082014. SE COMENTARIZA YA QUE SE NECESITA LA UR QUE ESTA HACIENDO LA INTEGRACION Y NO LA DE LA RG SELECCIONADA
 					UnidadEjec=$("#cIdUnidadEjecutora").val();//URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO PARA MANDARLA A LA CONSULTA DE LAS CUENTAS BANCARIAS
 					
	 				for ( var i=0; i<aTrs.length;  i++ )     
						{         
	 						//if(table.rows[i+1].cells[1].childNodes[0].nodeValue == UnidadEjec){ //URVP.12082014.SE COMENTARIZA YA QUE SE NECESITA TENER LA MISMA LEYENDA EN TODAS LAS RG QUE SERAN INTEGRADAS
	 							table.rows[i+1].cells[8].childNodes[0].nodeValue = $("#cIdLeyendaUEjecutora").val();// }  						
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
				if (!fnValidaRowSel()){
					alert("Debe seleccionar primero un renglón.");
					return;
				}
								
				if ($('#chkIntegra').is(':checked')){
					$( "#dialog-formLeyendaUEjecutora" ).dialog( "open" );
           		}else{
					$( "#dialog-formLeyenda" ).dialog( "open" );
				}
			});
			
		$( "#pbFechaProgramada" )
			.button()
			.click(function() {
				
				if (!fnValidaRowSel()){
					alert("Debe seleccionar primero un renglón.");
					return;
				}
				
				if ($('#chkIntegra').is(':checked'))
           		{
					$( "#dialog-formFechaUEjecutora" ).dialog( "open" );
           		}
				else
				{
					$( "#dialog-formFecha" ).dialog( "open" );
				}
				
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
				
				if ($('#chkIntegra').is(':checked'))
           		{			
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var strUE = "";
						
	 			/* 	for ( var i=0; i<aTrs.length;  i++ )     
					{         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							var nTr = $('#dt_generados').dataTable().fnGetData(aTrs[i]); 
							strUE = nTr[1]; 
							break;
					    }  						
					} */
					//URVP.12082014. SE COMENTARIZA EL BLOQUE ANTERIOR YA QUE SE NECESITA LA UR QUE ESTA HACIENDO LA INTEGRACION Y NO LA DE LA RG SELECCIONADA
	 				strUE=$("#cIdUnidadEjecutora").val(); //URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO PARA MANDARLA A LA CONSULTA DE LAS CUENTAS BANCARIAS AL SER INTEGRACION
	 				
	 				$('#tblCuentasBancariasUEjecutora').dataTable().fnClearTable();
	 				
	 				if (strUE == "")
	 				{
	 					alert("Debe seleccionar primero un renglón.");
	 					return;
					}	
					var szWhere = " strUnidadEjecutora = '" + strUE + "' "; 
					var szTabla = "UECUENTASBANCARIAS";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{     
							for (var i = 0; i < j.length; i++) 
	    					{
		    					$('#tblCuentasBancariasUEjecutora').dataTable().fnAddData([ 
									j[i].Col0 , 
									j[i].Col1 , 
									j[i].Col2 ,
									"<input type='text' id='strClabe" + i + "' name='strClabe' value='" + j[i].Col3 + "' readonly style='border-width:0; background-color:transparent'/>",
									j[i].Col4 
									]); 
	    					}
			         });   				
					
					$( "#dialog-formUEjecutora").dialog( "open" );	
				}
				else
				{
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
							strRFC = $.trim(nTr[3]); // el 11 corresponde al folio
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
									]); 
	    					}
			         });   					
					
					$( "#dialog-form" ).dialog( "open" );	
				}
			});
		});
		
	</script>
	</head>

  	<body id="dt_example" onLoad="inicio();" >  	
		<div  id="container" >
			<h1>Layout Pago Diverso con RG<label id="lbOperacion" style="font-size: 8pt"></label></h1>		
        	<div id="tabs">        	     		           
        		<ul>
					<li><a href="#tabs-1">Pendientes de generar Layout</a></li>
					<li><a href="#tabs-2" onClick="enviar();">Pagos en proceso SICOP</a></li>
					<li><a href="#tabs-3">Layouts generados</a></li>
				</ul>                              
	            
				<div id="tabs-1">
			  		<form>
		        	<table width="100%" border="0"">
			        	<tr>	        	
				        	<td width="130px" align="right">Unidad Ejecutora:</td>				        	
				        	<td> 
								<input type="hidden" id="cUResp" name="cUResp" class="paso01" value="<%=cUR%>">
				            	<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" class="paso01" style="width: 20em;">
				            		<option value="<%=cUR%>"></option>
					            </select>
					        </td>				           
				            <td align="right"> Fecha Inicio: </td>				        	
				        	<td>
		            			<input onchange="valFecha(this)" name="FechaInicial" type="text" id="FechaInicial" class="paso01" size="10" readonly> 
					        </td>
					        <td  align="right"> Fecha Final: </td>				        	
				        	<td>
					            <input onchange="valFecha(this)"  name="FechaFinal" type="text" id="FechaFinal" class="paso01" size="10" readonly> 
					        </td>
				        </tr>
				        
				        <tr>
				        	<td>&nbsp;</td>
				        	<td style="font-weight: bold">
				        		<input type="checkbox" id="chkIntegra" name="chkIntegra" checked disabled>&nbsp;Aplicar Integración 
				        	</td>
					        <td colspan="4" align="right">
					        	<input type="button" id="pbCuentasBancarias" style="visibility: hidden" value="Cuenta Bancaria"> &nbsp; &nbsp; &nbsp;
					        	<input type="button" id="pbFechaProgramada" style="visibility: hidden" value="Fecha Programada"> &nbsp; &nbsp; &nbsp;
					        	<input type="button" id="pbLeyenda" style="visibility: hidden" value="Leyenda">  &nbsp; &nbsp; &nbsp;     	
					        </td>
				        </tr>
			        </table>
		          
					<fieldset>
						<legend> Pagos sin layout	</legend>
						<table><tr>
		          		<td> <input type="checkbox" id="checkAll" name="checkAll">Todos</td>								
						</tr>
						<tr><td>
						<jsp:include page="ListaPagosDiversosRelGastos.jsp"></jsp:include>
						</td></tr>
						</table>
					</fieldset>
					</form>	
				</div>
				<div id="tabs-2">				
				    <fieldset>
						<legend> Integraci&oacute;n del layout para ser enviado a SICOP  </legend>
				    	<jsp:include page="listaPagosDiversosRelGastosEnviados.jsp"></jsp:include>
					</fieldset>
				</div>
		        <div id="tabs-3">
		        	<table width="100%" border="0">
			        	<tr>	        	
				        	<td width="130px" align="right">Unidad Ejecutora:</td>				        	
				        	<td> 
								<input type="hidden" id="cUResp1" name="cUResp1" class="paso01" value="<%=cUR%>"/>
				            	<select id="cIdUnidadEjecutora1" name="cIdUnidadEjecutora1" class="paso01" style="width: 20em;">
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
		        	<input type="button" id="pbStatusInicial" onClick="Inicializa();" value="Devolver"/>
		        	<input type="button" id="btnDevuelveConsolidada" onClick="devuelveConsolidada();" value="Devolver Integraci&oacute;n"/>		        	
				    <fieldset>
						<legend> Pagos enviados a SICOP </legend>
				    	<jsp:include page="listaPagoDiversoRelGastosConLayout.jsp"></jsp:include>
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
					
			<div id="dialog-formUEjecutora" title="Selección de Cuenta Bancaria por Unidad Ejecutora">
				<table id="tblCuentasBancariasUEjecutora" class="display"  >
					<thead>
						<tr>
							<th>UE</th>
							<th width="130px">RFC</th>
							<th>Nombre Beneficiario</th>
							<th width="120px">No.Cuenta</th>
							<th>Tipo Cuenta</th>
						</tr>
					</thead>
				</table>
			</div>
			
			<div id="dialog-formFecha" title="Selección de Fecha">
				Fecha Inicio: 
            	<input  name="FechaProgramada" type="text" id="FechaProgramada" class="paso01" size="10" readonly/> 
			</div>
			
			<div id="dialog-formFechaUEjecutora" title="Selección de Fecha por Unidad Ejecutora">
				Fecha Inicio: 
            	<input  name="FechaProgramadaUEjecutora" type="text" id="FechaProgramadaUEjecutora" class="paso01" size="10" readonly/> 
			</div>
			
			<div id="dialog-formLeyenda" title="Selección de Leyenda">
		 		<select id="cIdLeyenda" class="paso01" name="cIdLeyenda" style="width: 600px;">
	              <option value="Z1:"></option>
	              <option value="Y1:"></option>
	              <option value="X1:"></option>
	              <option value="xx1" selected> -Leyenda- </option>
	            </select>
			</div>
			
			<div id="dialog-formLeyendaUEjecutora" title="Selección de Leyenda por Unidad Ejecutora">
		 		<select id="cIdLeyendaUEjecutora" class="paso01" name="cIdLeyendaUEjecutora" style="width: 600px;">
	              <option value="Z1:"></option>
	              <option value="Y1:"></option>
	              <option value="X1:"></option>
	              <option value="xx1" selected> -Leyenda- </option>
	            </select>
			</div>
		</div>
	</body>
			
</html>
