<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cDevuelveConsolidada = "NO";
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	cUR = usuario.getU_UR();
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("DEVUELVECONSOLIDADA")){
		cDevuelveConsolidada = usuario.getPropiedad("DEVUELVECONSOLIDADA").getValor();
	}
	String user = "";
	user = usuario.getLogin();
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean esSAIAlterno = ( "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") ) || "true".equals( cabl.getSystemSetting("SAI_FONDEN") ) );
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Layout Pagos Diversos RG</title>

	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
	<script type="text/javascript" charset="utf-8">
	
	var radicado = "N";
	var esSAIAlterno = <%=esSAIAlterno%>;
	
		function inicio(){
			if ("<%=cDevuelveConsolidada%>" == "NO") {
				$("#btnDevuelveConsolidada").attr('disabled', true);
				$("#btnDevuelveConsolidada").hide();
			}
			document.getElementById('archivo').value = "";
			document.getElementById('sDataH').value = "";
			document.getElementById('sDataHCB').value = "";
			document.getElementById('sDataHFecha').value = "";
			document.getElementById('sDataFolios').value = "";
			document.getElementById('u_login').value = "";			
		}
		
		function formatCurrency(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num)) 
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10)
				cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));

			return (((sign) ? '' : '-') + num + '.' + cents);
		}
		
		$(document).ready(function(){
			
			$("#divTipoPresupuesto").hide();

			oTableCB = $("#tblCuentasBancarias").dataTable({
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : true,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,
	            "sScrollXInner": "100%",      	    		
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,     
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
				"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : true,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,
	            "sScrollXInner": "100%",      	    		
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   				
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
			
			if( esSAIAlterno ){
				querySelectPost("U_EJECUTORAReadAlternativo", "cIdUnidadEjecutora", {async: false });
				querySelectPost("U_EJECUTORAReadAlternativo", "cIdUnidadEjecutora1", {async: false });
			}else{
				querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
				querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora1", {async: false });
			}
			

			querySelectPost("rCatalogoLeyendaRead", "cIdLeyenda", {async: false });
			querySelectPost("rCatalogoLeyendaRead", "cIdLeyendaUEjecutora", {async: false });
			queryFormPost("FechaInicialRead", {async: false });
			queryFormPost("FechaFinalRead", {async: false });

			$( "#cIdUnidadEjecutora" ).val('<%=cUR%>');
			/*
			if ( esSAIAlterno && '<%=cUR%>' != 'A02') 
				$( "#cIdUnidadEjecutora" ).attr("disabled", true);
			*/
			
			if(esSAIAlterno && '<%=cUR%>' == 'F02' )
				$( "#cIdUnidadEjecutora" ).attr("disabled", false);
			else
				$( "#cIdUnidadEjecutora" ).attr("disabled", true);
				
			$("#FechaProgramada").val($("#FechaInicial").val());
			
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), radicado);
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
				
				$("#chkIntegra").prop("disabled", true);
				$("#checkAll").prop("disabled", true);				
				//Desactiva el RadioButton seleccionado
				
				//?????????
				if ($("#integraprosub").is(':checked'))
					$("#integraprosub").attr("checked", false);
				else if ($("#integranomina").is(':checked'))
					$("#integranomina").attr("checked", false);
				else if ($("#integraempleado").is(':checked'))
					$("#integraempleado").attr("checked", false);

				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), radicado);
				
			});
			
			$("#cIdUnidadEjecutora1").change(function() {
				fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
			});
			
			$("#chkIntegra").change(function(){
			
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				
					if ($('#chkIntegra').is(':checked')) {
				        Swal.fire({ icon: "info",text: "¡ AVISO ! <br/>La Integración suma los montos y aplica la transferencia a las cuentas de la Unidad Ejecutora."});

				        $('#dt_paraEnvio').dataTable().fnSetColumnVis( 2, false );
				    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 3, false );
				    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 6, false );
				    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 9, false );
				    	$('#dt_paraEnvio').dataTable().fnSetColumnVis( 10, false );
				    	for (var i=0; i<aTrs.length; i++){         
	 						var row= table.rows[i];
	 						
	 						table.rows[i+1].cells[6].childNodes[0].nodeValue ="--";  
	 						table.rows[i+1].cells[8].childNodes[0].nodeValue ="--";
	 						table.rows[i+1].cells[9].childNodes[0].nodeValue ="--";
						}
				    }else{
				    	location.reload(true);
				 
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
						$('#sDataFolios').val(row.cells[columna].innerHTML);
						cont++;
				    }
				}
				if (cont!=1){
					if (cont==0)
						Swal.fire({ icon: "warning",
			 						text: "Debe seleccionar un registro."});						
					else				
						Swal.fire({ icon: "warning",
 									text: "Para esta opción debe seleccionar un solo registro."});
    		    		return;					
				}else{					
					Swal.fire({
						  title: '¿Desea continuar?',
						  text: "Se regresara la Integracion: " + $('#sDataFolios').val(),
						  icon: 'warning',
						  showCancelButton: true,
						  confirmButtonColor: '#288BA8',
						  cancelButtonColor: '#e6e6e6',
						  confirmButtonText: 'Aceptar',
						  cancelButtonText: 'Cancelar'
						}).then((result) => {
						  if (result.isConfirmed) {
								$("#u_login").val("<%=user%>");
								$.ajax({
									url: '../gstnmngr/PagosDiversosRGConLayout',
									dataType: 'json',
									type: "POST",
									data: {"sDataFolios":$('#sDataFolios').val(), "u_login":$("#u_login").val()},
									async: true,
									success: function(j){								
										Swal.fire({ icon: "success",
		 											text: "Se ha devuelto la Integracion "+$('#sDataFolios').val()});
										location.reload(true);								
									},
									error: function(j){								
										Swal.fire({ icon: "error",
													text: "No se pudo regresar la consolidada, Intente mas tarde o avise al Administrador."});								
										location.reload(true);
									}
								});			
							}
						})
				}
        	}catch(e){
         		Swal.fire({ icon: "error",
							text: e});
    		}
 		}
		
		function fnClickAddRow(row) {
			try{
				if ($('#chkIntegra').is(':checked')){
					var bExiste = false;
					var szUEjecutora = '';
					var table = document.getElementById('dt_paraEnvio');
					var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
					for ( var i=0; i<aTrs.length; i++){
						szUEjecutora = $.trim(table.rows[i+1].cells[0].childNodes[0].nodeValue) ;
						if ($.trim(row.cells[1].childNodes[0].nodeValue()) == szUEjecutora){
							bExiste = true;
							break;
						}
					}			
					if (bExiste){
						var Monto = parseFloat($.trim(table.rows[i+1].cells[3].childNodes[0].nodeValue).replace(",",""));
						Monto = Monto + parseFloat(row.cells[4].childNodes[0].nodeValue().replace(",",""));
						table.rows[i+1].cells[3].childNodes[0].nodeValue = formatCurrency(Monto);
					}else{
						$('#dt_paraEnvio').dataTable().fnAddData([
							row.cells[1].innerHTML,
		      				row.cells[2].innerHTML,
		      				row.cells[3].innerHTML,
		      				row.cells[4].innerHTML,
		      				row.cells[5].innerHTML,
		      				row.cells[6].innerHTML,
		      				row.cells[7].innerHTML,
		      				row.cells[8].innerHTML,
		      				0,
		      	            row.cells[9].innerHTML
		      			]);
					}
				}else{
					var table2 = $('#dt_paraEnvio').dataTable().fnAddData([
						row.cells[1].innerHTML,
	      				row.cells[2].innerHTML,
	      				row.cells[3].innerHTML,
	      				row.cells[4].innerHTML,
	      				row.cells[5].innerHTML,
	      				row.cells[6].innerHTML,
	      				row.cells[7].innerHTML,
	      				row.cells[8].innerHTML,
	      				0,
	      	            row.cells[9].innerHTML
		      		]);
				}
			}catch (ex){
				Swal.fire({ icon: "error",
							text: ex.message});
			}
		}
		
		function fnClickDellRows(){
			var table2 = $('#dt_paraEnvio').dataTable();
			table2.fnClearTable();						
		}
		
		function enviar(){
			var vacio = true;
 			try {
 				fnClickDellRows();
        		var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
 				var cCOLUMNALLAVE = 3;
 				var cCUENTABANCARIA = 6;
 				var cFECHAPROG = 8;
 				var cLeyenda = 9;
 				var cRFC = 4;
 				
 				document.getElementById('sDataH').value = " ";
				document.getElementById('sDataHCB').value = " ";
				document.getElementById('sDataHFecha').value = " ";
				document.getElementById('sDataHLeyenda').value = " ";		
				
 				for (var i=1; i<=aTrs.length; i++){   
 						var row= table.rows[i];
 						var chkbox = row.cells[0].childNodes[0];
 						
 						if(null != chkbox && true == chkbox.checked){
 							if (row.cells[cCUENTABANCARIA].innerHTML == "--" ){
 								Swal.fire({ icon: "error",
 											text: "El RFC : " + row.cells[cRFC].innerHTML + " no tiene asignada Cuenta Bancaria"}); 								
 								//location.reload(true);
 								return;
 							}
 							if (row.cells[cFECHAPROG].innerHTML == "--"){ 								
 								Swal.fire({ icon: "warning",
											text: "La Unidad : " + row.cells[1].innerHTML + " no tiene asignada Fecha Programada"});
 								return;
 							}
 							if (row.cells[cLeyenda].innerHTML == "--"){
 								Swal.fire({ icon: "warning",
											text: "La Unidad : " + row.cells[1].innerHTML + " no tiene asignada Leyenda"}); 								
 								return;
 							}
 							$("#pbEnvia").css("visibility","visible");
							$("#btnLayoutBan").css("visibility","visible");
							
							var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
							var caNoCuentaBancaria = row.cells[6].innerHTML; // el 11 corresponde al folio
							var caNoFecha = row.cells[8].innerHTML; // el 11 corresponde al folio
							var caNoLeyenda = row.cells[9].innerHTML;
							
							document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
							document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
							document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
							document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
							
							vacio = false;
				      		fnClickAddRow(row);
					    } 
					}
        	}catch(e) {
         		Swal.fire({ icon: "error",
							text: e});
    		}
    		if(vacio){
    			Swal.fire({ icon: "warning",
							text: "Debe marcar al menos una fila"});    		    
    		}
 		}
		
		function fnGuardaUE(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			for (var i=0; i<aTrs.length; i++){         				
				var szValues = "'" + $.trim(table.rows[i+1].cells[2].childNodes[0].nodeValue) + "', "; 
				 szValues += " " + $.trim(table.rows[i+1].cells[3].childNodes[0].nodeValue) + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[4].childNodes[0].nodeValue) + "', ";
				 szValues += " " + $.trim(table.rows[i+1].cells[5].childNodes[0].nodeValue).replace(",","") + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[6].childNodes[0].nodeValue) + "', ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[7].childNodes[0].nodeValue) + "', ";
				 szValues += " " + $.trim(table.rows[i+1].cells[8].childNodes[0].nodeValue) + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[9].childNodes[0].nodeValue) + "', ";
				 szValues += "'', ";		// strFolioInterno
				 szValues += "0";
				
				var szTabla = "M_RGINTEGRACION";                                                                                         
				$.getJSON("../catalogos/InsertJson.jsp",{Tabla: szTabla, Param: szValues, MaxReg: 20, ajax: 'false'}, function(j){});   					
			}			
		}

		function generar(){
 			try {
 				//if(confirm("Ya genero el layout Bancario?\nEn caso afirmativo de click en Aceptar.\nCaso contrario de click en Cancelar.")){
				Swal.fire({
					  title: '¿Ya genero el layout Bancario?',
					  text: "En caso afirmativo de click en Aceptar.<br/>Caso contrario de click en Cancelar.",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {	
		 					var table = document.getElementById('dt_generados');
			        		var table2 = document.getElementById('dt_paraEnvio');
			            	var rowCount = table.rows.length;
			            	var rowCount2 = table2.rows.length;
			            	
			            	if (rowCount2 == 0){
			            		Swal.fire({ icon: "warning",
											text: "Debe marcar al menos un pago"});
			            		return;
			            	}
			            	            	
			            	if ($('#chkIntegra').is(':checked')){
			            		//fnGuardaUE();
			           			document.getElementById('archivo').value = "2";
			           		}else{
			           			document.getElementById('archivo').value = "1";
			           		}
			            	
		// 	            	document.location.href='../gstnmngr/generaLayoutPagosDiversosRG';
			            	document.envioSICOP.submit();
							
			            	fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), radicado);
			            	
			            	fnClickDellRows();
			            	$("#pbEnvia").css("visibility","hidden");
			            	$("#btnLayoutBan").css("visibility","hidden");
            			}
					})
         	}
 			catch(e) {
         		Swal.fire({ icon: "error",
							text: e});         		
         	}
 		}
 		
		function toggleReactivar(status) {
			$("input:checkbox").each( 
				function() {
					$(this).attr("checked",status.checked);
				}
			);
		}
 		function convertirAFecha(string) {
			 var date = new Date();
			 mes = parseInt(string.substring(3, 5), 10);
			 date.setMonth(mes - 1); //en javascript los meses van de 0 a 11
			 date.setDate(string.substring(0, 2));
			 date.setYear(string.substring(6, 10));
			 return date;
		}
		
		function valFecha(object1) {
			var FechaIni = document.getElementById("FechaInicial").value;
		    var FechaFin = document.getElementById("FechaFinal").value;
			
		    if (object1.value != ""){
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) {		    		
		    		Swal.fire({ icon: "warning",
								text: "La fecha incial no puede ser mayor a la fecha final"});
		         	document.getElementById("FechaFinal").value = document.getElementById("FechaInicial").value;
		      }
		      fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), radicado);
		   	}
		}
		
		function valFecha1(object1) {
			var FechaIni = document.getElementById("FechaInicial1").value;
		    var FechaFin = document.getElementById("FechaFinal1").value;
			
		    if (object1.value != "") {
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) {
		    	  Swal.fire({ icon: "warning",
							  text: "La fecha incial no puede ser mayor a la fecha final"});
		         document.getElementById("FechaFinal1").value = document.getElementById("FechaInicial1").value;
		      }
		      fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
		   }
		}
		
		function Verifica_Fecha(pstrFecha){
	        var strCharCorrectos = "0123456789/";
	        var Fecha = pstrFecha;
		    if (Fecha == "")
			    return false;
		
		    for (i=0; i < Fecha.length;i++){
	    		var car = Fecha.substr(i,1);
			    if (strCharCorrectos.indexOf(car)==-1){
				      return false;
				}
			}
		    
		       dd=0;
		       mm=1;
		       aaaa=2;
		    ArrayFecha = Fecha.split("/");
		    if(ArrayFecha.length!=3)
	    	    return false;
	    
		    var Dia  = Number(ArrayFecha[0]);
		    var Mes  = Number(ArrayFecha[1]);
		    var Anno = Number(ArrayFecha[2]);
		    var TemAno =String(ArrayFecha[2]);
		    
	    	
	    	if (TemAno.length != 4 ) 
	    		return false;
	    	if (Dia > 31 || Dia < 1 ) 
	    		return false;
	    	if (Mes >12 || Mes < 1 )  
	    		return false;
	    	if (Mes == 4 || Mes == 6 || Mes == 9 || Mes == 11){
		        if (Dia > 30 ) 
		        	return false;
			}	
		    if ( Mes == 2 ){
	    		if ((Anno % 4)==0 ){  /* Se verifica si el Anno es biciesto **/
					if (Dia > 29 ) return false;				
				}else{
					 if (Dia > 28) return false;
					}
	            }
	    
	        return true;
        }
					
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
				buttons: {
					"Aceptar": function() {
						var table = document.getElementById('tblCuentasBancariasUEjecutora');
		 				var aTrs = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetNodes();
		 				var cCuentaBancaria = 4;
		 				var strCuentaBancaria = "";
		 				var bSel = false;
	
		 				for ( var i=0; i<aTrs.length; i++){         
	 						var row= table.rows[i];
	 						
	 						if ( $(aTrs[i]).hasClass('row_selected') )         {
	 							var nTr = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetData(aTrs[i]);
	 							strCuentaBancaria = $("#strClabe"+i).val();
	 							bSel = true;
								break;
						    } 	 						
						}
		 				if (!bSel){	 						
	 						Swal.fire({ icon: "warning",
								  		text: "Advertencia...! <br/>No ha seleccionado ninguna Cuenta Bancaria."});
	 						return;
	 					}
		 				
						// escribir la cuenta bancaria
						var table = document.getElementById('dt_generados');
		 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
		 				var UnidadEjec = "";
		 				
		 				for (var i=0; i<aTrs.length;i++){         
	 	 					table.rows[i+1].cells[6].childNodes[0].nodeValue =strCuentaBancaria;  						
						}
						$( this ).dialog( "close" );
					},
					"Cancelar":function() { $(this).dialog("close"); }
				},
				close:function(){}
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

	 				for ( var i=0; i<aTrs.length;  i++ )     {         
 						var row= table.rows[i];
 						
 						if ( $(aTrs[i]).hasClass('row_selected') )         {
 							var nTr = $('#tblCuentasBancarias').dataTable().fnGetData(aTrs[i]);
 							
 							strCuentaBancaria = $("#cBanco"+i).val();
 							strCuentaBancaria += $("#cPlaza"+i).val();
							strCuentaBancaria += $("#dCuentaBancaria"+i).val();
							strCuentaBancaria += $("#dDigitoVerificador"+i).val();
							bSel = true;
							break;
					    } 	 						
					}
	 				if (!bSel){ 						
 						Swal.fire({ icon: "warning",
					  				text: "Advertencia...! <br/>No ha seleccionado ninguna Cuenta Bancaria."});
 						return;
 					}
	 				
					// escribir la cuenta bancaria
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cCuentaBancaria = 6;
	 					
	 				for ( var i=0; i<aTrs.length; i++){         
	 						var row= table.rows[i];
	 						
	 						if ( $(aTrs[i]).hasClass('row_selected') )         {
	 							table.rows[i+1].cells[6].childNodes[0].nodeValue =strCuentaBancaria;
	 							break;
						    }  						
						}
						$( this ).dialog( "close" );
					},
					"Cancelar": function() { $(this).dialog("close"); }
				},
				close: function(){}
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
	 				var cFechaProgramada = 8;
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     {         
	 						var row= table.rows[i];
	 						
	 						if ( $(aTrs[i]).hasClass('row_selected') )         {
	 							table.rows[i+1].cells[cFechaProgramada].childNodes[0].nodeValue = $("#FechaProgramada").val();
	 							break;
						    }	 							 						
						}
						$( this ).dialog( "close" );
					},
					"Cancelar": function() { $(this).dialog("close"); }
				},
				close: function() {}
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
		 				UnidadEjec=$("#cIdUnidadEjecutora").val();//URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO
		 				for ( var i=0; i<aTrs.length; i++){         
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
					for ( var i=0; i<aTrs.length;  i++ )     {         
						var row= table.rows[i];
						if ( $(aTrs[i]).hasClass('row_selected') )         {
							bExiste = true;
							break;
					    }  						
					}
					if (! bExiste){
						Swal.fire({ icon: "warning",
			  						text: "Debe primero seleccionar un renglon."});
						return;
					}
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cLeyenda = 9;
	 					
	 				for ( var i=0; i<aTrs.length;  i++ )     {         
	 						var row= table.rows[i];
	 						if ( $(aTrs[i]).hasClass('row_selected') )         {
	 							table.rows[i+1].cells[cLeyenda].childNodes[0].nodeValue = $("#cIdLeyenda").val();
	 							break;
						    } 	 						
						}
					$( this ).dialog( "close" );
					},
					"Cancelar": function() { $(this).dialog("close"); }
				},
				close: function() {}
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
						for ( var i=0; i<aTrs.length;  i++ )     {         
							var row= table.rows[i];
							if ( $(aTrs[i]).hasClass('row_selected') )         {
								bExiste = true;
								break;
						    }  						
						}
						if (! bExiste){
							Swal.fire({ icon: "warning",
		  								text: "Debe primero seleccionar un renglon."});
							return;
						}
						var table = document.getElementById('dt_generados');
		 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
		 				var UnidadEjec = "";
		 				
	 					UnidadEjec=$("#cIdUnidadEjecutora").val();
	 					
		 				for ( var i=0; i<aTrs.length;  i++ ){         
		 							table.rows[i+1].cells[9].childNodes[0].nodeValue = $("#cIdLeyendaUEjecutora").val();  						
						}
						$( this ).dialog( "close" );
					},
					"Cancelar": function() { $(this).dialog("close"); }
				},
				close: function() {}
			});
			
		$( "#pbLeyenda" )
			.button()
			.click(function() {
				if (!fnValidaRowSel()){
					Swal.fire({ icon: "warning",
								text: "Debe seleccionar primero un renglón."});
					return;
				}
								
				if ($('#chkIntegra').is(':checked')){
					$( "#dialog-formLeyendaUEjecutora" ).dialog( "open" );
           		}else{
					$( "#dialog-formLeyenda" ).dialog( "open" );
				}
			});
			
		$("#pbFechaProgramada").button().click(function() {
				if (!fnValidaRowSel()){					
					Swal.fire({ icon: "warning",
								text: "Debe seleccionar primero un renglón."});
					return;
				}
				if ($('#chkIntegra').is(':checked')){
					$( "#dialog-formFechaUEjecutora" ).dialog( "open" );
           		}else{
					$( "#dialog-formFecha" ).dialog( "open" );
				}
			});
		
		function fnValidaRowSel(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var bSel = false;
			
			for ( var i=0; i<aTrs.length;  i++ ){         
				var row= table.rows[i];
				if ( $(aTrs[i]).hasClass('row_selected') )         {
					bSel = true; 
					break;
			    }  						
			}
			return bSel;	
		}
			
		$( "#pbCuentasBancarias" ).button().click(function() {
				
				if (!fnValidaRowSel()){					
					Swal.fire({ icon: "warning",
								text: "Debe seleccionar primero un renglón."});
					return;
				}
				
				if ($('#chkIntegra').is(':checked')){			
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var strUE = "";
						
	 				strUE=$("#cIdUnidadEjecutora").val();
	 				
	 				$('#tblCuentasBancariasUEjecutora').dataTable().fnClearTable();
	 				
	 				if (strUE == ""){	 					
	 					Swal.fire({ icon: "warning",
									text: "Debe seleccionar primero un renglón."});
	 					return;
					}
					var szWhere = " strUnidadEjecutora = '" + strUE + "' "; 
					var szTabla = "UECUENTASBANCARIAS";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j){     
							for (var i = 0; i < j.length; i++) {
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
				}else{
					var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cRFC = 4;
	 				var strRFC = "";
						
	 				for ( var i=0; i<aTrs.length; i++){         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('row_selected') )         {
							var nTr = $('#dt_generados').dataTable().fnGetData(aTrs[i]); 
							strRFC = $.trim(nTr[4]); // el 11 corresponde al folio
							break;
					    }  						
					}
	 				$('#tblCuentasBancarias').dataTable().fnClearTable();
						
					var szWhere = " dRFC = '" + strRFC + "' "; 
					var szTabla = "BENEFICIARIOCUENTASBANCARIAS";                                                                                         
					$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j){    
							for (var i = 0; i < j.length; i++) {
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
		
		function tipopresupuesto(){
			radicado = $("input[name='rTipoPresupuesto']:checked").val();
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), radicado);
		}
		
		function generaLayoutBancario(){
			var folios = "";
			var ctaBancaria = "";
			var table = document.getElementById('dt_generados');			
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var cCOLUMNALLAVE = 3;
			var cCUENTABANCARIA = 6; 
			var j = 1;				
				
			for ( var i=1; i<=aTrs.length;  i++ ){   
				var row= table.rows[i];
				var chkbox = row.cells[0].childNodes[0];									
				
				if(null != chkbox && true == chkbox.checked){
					var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; 
					var caNoCuentaBancaria = row.cells[6].innerHTML;					
					
					$("#folioPago").val(caNoFolio);
					queryFormPost("esRelGastosOCLayoutBancarioRead", {async:false});
					
					if($("#esPagoLayoutBan").val() == "SI"){										
						if(j==1){
							folios += caNoFolio ;
						}else{
							folios += ", " + caNoFolio ;
						}
						ctaBancaria = caNoCuentaBancaria;
						
						j++;
						$("#esPagoLayoutBan").val("");
					}									
				}			     
			}
			
			if(folios == ""){
				Swal.fire({ icon: "warning",
							text: "Los folios seleccionados no aplican para generar Layout."});
			}else{
				$("#nFoliosLayout").val(folios);
				$("#sTipoLayout").val("PD");
				$("#sCuentaLayout").val(ctaBancaria);
				$("#layoutBanco").submit();
			}					
		}
	</script>
	</head>
	<br/>
  	<body id="dt_example" onLoad="inicio();" >  	
		<div  id="container" class="ms-5" class="container" style="width: 90%">
			<form method="post" id="layoutBanco" name="layoutBanco" action="../gstnmngr/generaLayoutBancoRG" target="_blank">
				<input id="nFoliosLayout" name="nFoliosLayout" type="hidden" value="">
				<input id="sTipoLayout" name="sTipoLayout" type="hidden" value="">
				<input id="sCuentaLayout" name="sCuentaLayout" type="hidden" value="">
				<input type="hidden" id="esPagoLayoutBan" name="esPagoLayoutBan" />
				<input type="hidden" id="folioPago" name="folioPago" />
			</form>
			
			<div class="card-header"> <h3> Layout Pagos Diversos RG </h3> </div>
			<hr class="mt-3"/>
						
        	<div class="row d-flex justify-content-center"><!--Inicio Div row para iniciar los tabs-->
        						        	     		          
        		<ul class="nav nav-tabs" id="list-opciones">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Pendientes de generar Layout</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-enviar" type="button" role="tab" aria-controls="tabs-enviar" aria-selected="false">Pagos en proceso SICOP</button>
					</li>								     
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-3" data-bs-toggle="tab" data-bs-target="#tabs-3-generados" type="button" role="tab" aria-controls="tabs-generados" aria-selected="false">Layouts generados</button>
					</li>								     											           
				</ul>                            
		          
		    	<div class="tab-content mt-3" id="tabContent"><!--Inicio div contenido tabs-->	
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout"><!--Inicio tab-1-->
				  		<form>
				  			<input type="hidden" id="cUResp" name="cUResp" class="paso01" value="<%=cUR%>">
				  			
				  			<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="cIdUnidadEjecutora" class="form-label"> U. Ejecutora: </label>
								</div>
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
									<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" class="form-select form-select-sm">
					            		<option value="<%=cUR%>"></option>
						            </select>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="FechaInicial" class="form-label"> Fecha Inicio: </label>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha(this)" name="FechaInicial" type="date" id="FechaInicial" class="form-control form-control-sm" size="10" />
									</div>						            
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="FechaFinal" class="form-label"> Fecha Final: </label>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha(this)" name="FechaFinal" type="date" id="FechaFinal" class="form-control form-control-sm" size="10" />
									</div>						            
								</div>								
							</div>
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="checkbox" id="chkIntegra" name="chkIntegra" class="form-check-input" disabled checked/>&nbsp;<b>Aplicar Integración </b>
								</div>			
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">									
								</div>				
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="button" id="pbCuentasBancarias" style="visibility: hidden" value="Cuenta Bancaria" class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-form"/> &nbsp; &nbsp; &nbsp;
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="button" id="pbFechaProgramada" style="visibility: hidden" value="Fecha Programada" class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formFecha"/> &nbsp; &nbsp; &nbsp;
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="button" id="pbLeyenda" style="visibility: hidden" value="Leyenda" class="btn btn-secondary btn-sm " data-toggle="modal" data-target="#dialog-formLeyenda"/>  &nbsp; &nbsp; &nbsp;
								</div>
							</div>	
							
							<div id="divTipoPresupuesto">
								<br/>
	
								<h5> Tipo de Presupuesto </h5>
								<hr class="mt-3"/>
								
								<div class="row d-flex">						
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						        	</div>		
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<input type="radio" id="presupuestoDisp" name="rTipoPresupuesto" class="form-check-input" value="N" onclick="tipopresupuesto()" checked>Compromiso Normal
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<input type="radio" id="presupuestoRad" name=rTipoPresupuesto class="form-check-input" value="S" onclick="tipopresupuesto()"> Compromiso Radicado
									</div>
								</div>
							</div>	
			          		<br/>
						
							<h5> Pagos sin layout</h5>
							<hr class="mt-3"/>
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input" disabled> Todos
								</div>
							</div>
					
							<jsp:include page="listaPagosDiversosRG.jsp"></jsp:include></td>
												
						</form>	
					</div>
					
					<div class="tab-pane fade show" id="tabs-2-enviar" role="tabpanel" aria-labelledby="tabs-enviar"><!--Inicio tab-2-->				
					   <h5> Integraci&oacute;n del layout para ser enviado a SICOP </h5>
						<hr class="mt-3"/>
					    
					    <jsp:include page="listaPagosDiversosRGEnviados.jsp"></jsp:include>
						
					</div>
			        
			        <div class="tab-pane fade show" id="tabs-3-generados" role="tabpanel" aria-labelledby="tabs-generados"><!--Inicio tab-3-->
			        	<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
								<input type="hidden" id="cUResp1" name="cUResp1" class="paso01" value="<%=cUR%>"/>
								<label for="cIdUnidadEjecutora1" class="form-label"> U. Ejecutora: </label>
								<select id="cIdUnidadEjecutora1" name="cIdUnidadEjecutora1" class="form-select form-select-sm">
				            		<option value="<%=cUR%>"></option>
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<label for="FechaInicial1" class="form-label"> Fecha Inicio: </label>
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha1(this)" name="FechaInicial1" type="date" id="FechaInicial1" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<label for="FechaFinal1" class="form-label"> Fecha Final: </label>
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha1(this)" name="FechaFinal1" type="date" id="FechaFinal1" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>								
						</div>
						
						<br/>
						
						<div class="row d-flex">																					
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="btnDevuelveConsolidada" name="btnDevuelveConsolidada" onClick="devuelveConsolidada();" value="Devolver Integraci&oacute;n" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
									        			        
					    <h5> Pagos enviados a SICOP </h5>
						<hr class="mt-3"/>
						
					    <jsp:include page="listaPagosDiversosRGConLayout.jsp"></jsp:include>
						
					</div>		        
				</div><!--Fin div contenido tabs-->
			</div><!--Fin Div row para iniciar los tabs-->

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
            	<input  name="FechaProgramada" type="text" id="FechaProgramada" class="paso01" size="10" readonly> 
			</div>
			
			<div id="dialog-formFechaUEjecutora" title="Selección de Fecha por Unidad Ejecutora">
				Fecha Inicio: 
            	<input  name="FechaProgramadaUEjecutora" type="text" id="FechaProgramadaUEjecutora" class="paso01" size="10" readonly> 
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
