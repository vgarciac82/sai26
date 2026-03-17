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
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Layout Solicitud de Recurso Fiscal</title>

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
		var modalCuentaBancaria;
		var modalCuentaBancariaUEjecutora;
		var modalFecha;
		var modalFechaUEjecutora;
		var modalLeyenda;
		var modalLeyendaUEjecutora;
		
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
			
			$("#tabs").tabs( {
				"show": function(event, ui) {
		    		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		    		if ( oTable.length > 0 ) {
		    			oTable.fnAdjustColumnSizing();
		    		}
				}
			} );
			
			modalCuentaBancaria = new bootstrap.Modal(document.getElementById('dialog-form'), 'data-bs-backdrop');
			modalCuentaBancariaUEjecutora = new bootstrap.Modal(document.getElementById('dialog-formUEjecutora'), 'data-bs-backdrop');
			modalFecha = new bootstrap.Modal(document.getElementById('dialog-formFecha'), 'data-bs-backdrop');
			modalFechaUEjecutora = new bootstrap.Modal(document.getElementById('dialog-formFechaUEjecutora'), 'data-bs-backdrop');
			modalLeyenda = new bootstrap.Modal(document.getElementById('dialog-formLeyenda'), 'data-bs-backdrop');
			modalLeyendaUEjecutora = new bootstrap.Modal(document.getElementById('dialog-formLeyendaUEjecutora'), 'data-bs-backdrop');
			
			$("#btnDevuelveConsolidada").button();
			$("#pbEnvia").button();
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

				$("#FechaInicial").val(moment().format('yyyy-01-01'));
				$("#FechaFinal").val(moment().format('yyyy-MM-DD'));
				$("#FechaProgramada").val(moment().format('yyyy-MM-DD'));
				$("#FechaInicial1").val( $("#FechaInicial").val()); 
				$("#FechaFinal1").val( $("#FechaFinal").val() );
				
				$("#FechaInicial2").val( $("#FechaInicial").val());
				$("#FechaFinal2").val( $("#FechaFinal").val() );
			
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'));			
			fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/'));			
		
			$("#tblCuentasBancarias tbody").click(function(event) {
					$(oTableCB.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('table-primary');
					});
					$(event.target.parentNode).addClass('table-primary');
					
				});
						
			$("#tblCuentasBancariasUEjecutora tbody").click(function(event) {
					$(oTableCBUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('table-primary');
					});
					$(event.target.parentNode).addClass('table-primary');
					
				});
			
			
			$( "#cIdUnidadEjecutora" )
			.change(function() 
			{
				
				if ($("#integraprosub").is(':checked'))
					$("#integraprosub").attr("checked", false);
				else if ($("#integranomina").is(':checked'))
					$("#integranomina").attr("checked", false);
				else if ($("#integraempleado").is(':checked'))
					$("#integraempleado").attr("checked", false);

				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'));
				
			});
			
			$("#cIdUnidadEjecutora1").change(function() {
				fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/'));
			});
			
			$("#checkAll").change(function(){
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				if ($('#checkAll').is(':checked')){
					$("input:checkbox").attr('checked', 'checked');
			    }else{
			    	$("input:checkbox").removeAttr('checked');
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
					//TODO: Falta modificar update en RegistroIngresosManager.JAVA
					Swal.fire({
						  title: '¿Desea continuar?',
						  text: "Esta seguro de regresar la Integracion: " +$('#sDataFolios').val(),
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
									url: '../gstnmngr/RegistroIngresoConLayoutServlet',
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
										Swal.fire({ icon: "warning",
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
				var bExiste = false;
				var szUEjecutora = '';
				var table = document.getElementById('dt_paraEnvio');
				var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
				for ( var i=0; i<aTrs.length; i++){
					szUEjecutora = $.trim(table.rows[i+1].cells[0].childNodes[0].nodeValue) ;
					if ($.trim(row.cells[1].innerHTML) == szUEjecutora){
						bExiste = true;
						break;
					}
				}			
				if (bExiste){
					var Monto = parseFloat($.trim(table.rows[i+1].cells[3].childNodes[0].nodeValue).replace(/,/g,""));
					Monto = Monto + parseFloat(row.cells[4].innerHTML.replace(/,/g,""));
					table.rows[i+1].cells[3].childNodes[0].nodeValue = formatCurrency(Monto);
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
 				var cCOLUMNALLAVE = 2;
 				var cCUENTABANCARIA = 5;
 				var cFECHAPROG = 7;
 				var cLeyenda = 8;
 				var cRFC = 3;
 				
 				document.getElementById('sDataH').value = " ";
				document.getElementById('sDataHCB').value = " ";
				document.getElementById('sDataHFecha').value = " ";
				document.getElementById('sDataHLeyenda').value = " ";		
				
 				for (var i=1; i<=aTrs.length; i++){   
 						var row= table.rows[i];
 						var chkbox = row.cells[0].childNodes[0];
 						
 						if(null != chkbox && true == chkbox.checked){
 							if (row.cells[cCUENTABANCARIA].innerHTML == "--" ){
 								Swal.fire({ icon: "warning",
 											text: "El RFC : " + row.cells[cRFC].innerHTML + " no tiene asignada Cuenta Bancaria"}); 								
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

							var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
							var caNoCuentaBancaria = row.cells[5].innerHTML; // el 11 corresponde al folio
							var caNoFecha = row.cells[7].innerHTML; // el 11 corresponde al folio
							var caNoLeyenda = row.cells[8].innerHTML;
							
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
		
		//TODO: Si no se utiliza eliminar este codigo
	/*	function fnGuardaUE(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			for (var i=0; i<aTrs.length; i++){         				
				var szValues = "'" + $.trim(table.rows[i+1].cells[1].childNodes[0].nodeValue) + "', "; 
				 szValues += " " + $.trim(table.rows[i+1].cells[2].childNodes[0].nodeValue) + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[3].childNodes[0].nodeValue) + "', ";
				 szValues += " " + $.trim(table.rows[i+1].cells[4].childNodes[0].nodeValue).replace(/,/g,"") + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[5].childNodes[0].nodeValue) + "', ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[7].childNodes[0].nodeValue) + "', ";
				 szValues += " " + $.trim(table.rows[i+1].cells[8].childNodes[0].nodeValue) + ", ";
				 szValues += "'" + $.trim(table.rows[i+1].cells[9].childNodes[0].nodeValue) + "', ";
				 szValues += "'', ";
				 szValues += "0";
				
				var szTabla = "M_RGINTEGRACION";                                                                                         
				$.getJSON("../catalogos/InsertJson.jsp",{Tabla: szTabla, Param: szValues, MaxReg: 20, ajax: 'false'}, function(j){});   					
			}			
		}*/

		function generar(){
 			try {
        		var table = document.getElementById('dt_generados');
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount = table.rows.length;
            	var rowCount2 = table2.rows.length;
            	
            	if (rowCount2 == 0){
            		Swal.fire({ icon: "warning",
								text: "Debe seleccionar al menos un pago"});
            		return;
            	}
                       	
				document.getElementById('archivo').value = "2";
            	document.location.href='../gstnmngr/generaLayoutRegistroIngreso';
            	document.envioSICOP.submit();
				
            	fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'));
            	
            	fnClickDellRows();
            	$("#pbEnvia").css("visibility","hidden");
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
		      fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'));
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
		      fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/'));
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
	    		if ((Anno % 4)==0 ){
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
		$( "#pbLeyenda" )
			.button()
			.click(function() {
				if (!fnValidaRowSel()){
					Swal.fire({ icon: "warning",
						  		text: "Debe seleccionar primero un renglón."});
					return;
				}
								
				modalLeyendaUEjecutora.show();
			});
			
		$("#pbFechaProgramada").button().click(function() {
				if (!fnValidaRowSel()){
					Swal.fire({ icon: "warning",
				  				text: "Debe seleccionar primero un renglón."});
					return;
				}
				modalFechaUEjecutora.show();
			});
		
		function fnValidaRowSel(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var bSel = false;
			
			for ( var i=0; i<aTrs.length;  i++ ){         
				var row= table.rows[i];
				if ( $(aTrs[i]).hasClass('table-primary') )         {
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
					
					modalCuentaBancariaUEjecutora.show();
			});
		});
		
		function aceptarCuentaBancaria(){
			var table = document.getElementById('tblCuentasBancarias');
				var aTrs = $('#tblCuentasBancarias').dataTable().fnGetNodes();
				var cCuentaBancaria = 4;
				var strCuentaBancaria = "";
				var bSel = false;

				for ( var i=0; i<aTrs.length;  i++ )     {         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         {
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
								text: "Advertencia...!\r\rNo ha seleccionado ninguna Cuenta Bancaria."});					
					return;
				}
				
			// escribir la cuenta bancaria
			var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var cCuentaBancaria = 5;
					
				for ( var i=0; i<aTrs.length; i++){         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('table-primary') )         {
							table.rows[i+1].cells[5].childNodes[0].nodeValue =strCuentaBancaria;
							break;
				    }  						
				}
				
				modalCuentaBancaria.hide();
				
		}
		
		function aceptarCuentaBancariaUEjecutora(){
			var table = document.getElementById('tblCuentasBancariasUEjecutora');
				var aTrs = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetNodes();
				var cCuentaBancaria = 4;
				var strCuentaBancaria = "";
				var bSel = false;

				for ( var i=0; i<aTrs.length; i++){         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         {
						var nTr = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetData(aTrs[i]);
						strCuentaBancaria = $("#strClabe"+i).val();
						bSel = true;
					break;
			    } 	 						
			}
				if (!bSel){					
					Swal.fire({ icon: "warning",
								text: "Advertencia...!\r\rNo ha seleccionado ninguna Cuenta Bancaria."});
					return;
				}
				
			// escribir la cuenta bancaria
			var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var UnidadEjec = "";
				
				for (var i=0; i<aTrs.length;i++){         
					table.rows[i+1].cells[5].childNodes[0].nodeValue =strCuentaBancaria;  						
			}
				
			modalCuentaBancariaUEjecutora.hide();
		}
		
		function aceptarFechaProgramada(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var cFechaProgramada = 7;
				
			for ( var i=0; i<aTrs.length;  i++ )     {         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         {
						table.rows[i+1].cells[cFechaProgramada].childNodes[0].nodeValue = $("#FechaProgramada").val();
						break;
			    }	 							 						
			}
			modalFecha.hide();
			
		}
		
		function aceptarFechaProgramadaUEjecutora(){
			var strFechaProg = $('#FechaProgramadaUEjecutora').val() ;
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var UnidadEjec = "";
			UnidadEjec=$("#cIdUnidadEjecutora").val();//URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO
			for ( var i=0; i<aTrs.length; i++){         
				table.rows[i+1].cells[7].childNodes[0].nodeValue = $("#FechaProgramadaUEjecutora").val(); //}  						
			}	
		
			modalFechaUEjecutora.hide();	
		}
		
		function aceptarLeyenda(){
			var bExiste = false;
			var table = document.getElementById('dt_generados');
 			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			for ( var i=0; i<aTrs.length;  i++ )     {         
				var row= table.rows[i];
				if ( $(aTrs[i]).hasClass('table-primary') )         {
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
				var cLeyenda = 8;
					
				for ( var i=0; i<aTrs.length;  i++ )     {         
						var row= table.rows[i];
						if ( $(aTrs[i]).hasClass('table-primary') )         {
							table.rows[i+1].cells[cLeyenda].childNodes[0].nodeValue = $("#cIdLeyenda").val();
							break;
				    } 	 						
				}
			modalLeyenda.hide();
		}
		
		function aceptarLeyendaUEjecutora(){
			var bExiste = false;
			var table = document.getElementById('dt_generados');
 			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			for ( var i=0; i<aTrs.length;  i++ )     {         
				var row= table.rows[i];
				if ( $(aTrs[i]).hasClass('table-primary') )         {
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
							table.rows[i+1].cells[8].childNodes[0].nodeValue = $("#cIdLeyendaUEjecutora").val();  						
			}

			modalLeyendaUEjecutora.hide();	
		}
		
	</script>
	</head>
	<br/>
  	<body id="dt_example" onLoad="inicio();" >  	
		<div  id="container" class="ms-5" class="container ms-5" style="width: 90%">			
			<div class="card-header"> <h3> Layout de Registro de Ingresos </h3> </div>
			<hr class="mt-3"/>
					
        	<div class="row d-flex justify-content-center">
	      							
	       		<ul class="nav nav-tabs" id="list-opciones">
	       			 <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-pendientes" type="button" role="tab" aria-controls="tabs-pendientes" aria-selected="true">Pendientes de generar Layout</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-proceso" type="button" role="tab" aria-controls="tabs-proceso" aria-selected="false">Ingresos en proceso SICOP</button>
		            </li>						
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-3" data-bs-toggle="tab" data-bs-target="#tabs-3-generados" type="button" role="tab" aria-controls="tabs-generados" aria-selected="false">Layouts generados</button>
		            </li>						
				</ul>        	     		           
        		
				<div class="tab-content mt-3" id="tabContent">		
					<div class="tab-pane fade show active" id="tabs-1-pendientes" role="tabpanel" aria-labelledby="tabs-pendientes">
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
										<input onchange="valFecha(this)" name="FechaInicial" type="date" id="FechaInicial" class="form-control form-control-sm" size="10"/>
									</div>						            
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="FechaFinal" class="form-label"> Fecha Final: </label>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha(this)" name="FechaFinal" type="date" id="FechaFinal" class="form-control form-control-sm" size="10"/>
									</div>						            
								</div>								
							</div>	
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
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
							
							
				          	<div class="row d-flex">								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				          			<input type="checkbox" id="checkAll" name="checkAll">Todos
				          		</div>
				          	</div>							
							
							<jsp:include page="listaRegistroIngreso.jsp"></jsp:include>
							
							
						</div>
					</form>
					
					<div class="tab-pane fade" id="tabs-2-proceso" role="tabpanel" aria-labelledby="tabs-proceso">					
					     <h5> Integraci&oacute;n del layout para ser enviado a SICOP </h5>
						<hr class="mt-3"/>
					    	
					    <jsp:include page="listaRegistroIngresoEnviados.jsp"></jsp:include>
						
					</div>
					
			        <div class="tab-pane fade" id="tabs-3-generados" role="tabpanel" aria-labelledby="tabs-generados">
			        	<input type="hidden" id="cUResp1" name="cUResp1" class="paso01" value="<%=cUR%>">
			        	
			        	<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="cIdUnidadEjecutora1" class="form-label"> U. Ejecutora: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<select id="cIdUnidadEjecutora1" name="cIdUnidadEjecutora1" class="form-select form-select-sm">
				            		<option value="<%=cUR%>"></option>
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="FechaInicial1" class="form-label"> Fecha Inicio: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha1(this)" name="FechaInicial1" type="date" id="FechaInicial1" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="FechaFinal1" class="form-label"> Fecha Final: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha1(this)" name="FechaFinal1" type="date" id="FechaFinal1" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>								
						</div>
						
						<div class="row d-flex">															
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="btnDevuelveConsolidada" name="btnDevuelveConsolidada" onClick="devuelveConsolidada();" value="Devolver Integraci&oacute;n" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
									 			
					    <jsp:include page="listaRIFConLayout.jsp"></jsp:include>
						
					</div>		        
				</div>			

			<div class="modal fade" id="dialog-form" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de Cuenta Bancaria</h5>
							<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
						  </div>
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblCuentasBancarias" class="table table-striped table-bordered">
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
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->
							<button type="button" id="aceptarCuenta" class="btn btn-primary btn-sm" onclick="aceptarCuentaBancaria();" >Aceptar</button>
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				  </div>		
			</div>
					
			<div class="modal fade" id="dialog-formUEjecutora" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog modal-dialog modal-lg"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de Cuenta Bancaria por Unidad Ejecutora</h5>
							<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
						  </div>
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblCuentasBancariasUEjecutora" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th>UE</th>
												<th>RFC</th>
												<th>Nombre Beneficiario</th>
												<th>No.Cuenta</th>
												<th>Tipo Cuenta</th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->
							<button type="button" id="aceptarCuentaUE" class="btn btn-primary btn-sm" onclick="aceptarCuentaBancariaUEjecutora();" >Aceptar</button>
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				  </div>		
			</div>		
			
			<div class="modal fade" id="dialog-formFecha" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
			    	<div class="modal-content"> <!-- Contenido de la caja -->
			      		<div class="modal-header"> <!-- Encabezado de la caja -->
			        		<h5 class="modal-title">Selección de Fecha</h5>
			        		<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
			      		</div>
			      	<div class="modal-body"> <!-- Cuerpo de la caja -->
				        <div class="row d-flex">								
				        	<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				        	</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<label for="FechaInicial" class="form-label"> Fecha Programada: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
										<input  name="FechaProgramada" type="date" id="FechaProgramada" class="form-control form-control-sm" />
								</div>						            
							</div>
						</div>
			      	</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarFecha" class="btn btn-primary btn-sm" onclick="aceptarFechaProgramada();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
				    </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="dialog-formFechaUEjecutora" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de Fecha por Unidad Ejecutora</h5>
							<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
						  </div>
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<label for="FechaProgramadaUEjecutora" class="form-label"> Fecha Programada: </label>
								</div>
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
											<input  name="FechaProgramadaUEjecutora" type="date" id="FechaProgramadaUEjecutora" class="form-control form-control-sm" />
									</div>						            
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->
							<button type="button" id="aceptarFechaUE" class="btn btn-primary btn-sm" onclick="aceptarFechaProgramadaUEjecutora();" >Aceptar</button>
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				</div>		
			</div>
			
			<div class="modal fade" id="dialog-formLeyenda" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
			    	<div class="modal-content"> <!-- Contenido de la caja -->
			      		<div class="modal-header"> <!-- Encabezado de la caja -->
			        		<h5 class="modal-title">Selección de Leyenda</h5>
			        		<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
			      		</div>
			      	<div class="modal-body"> <!-- Cuerpo de la caja -->
				        <div class="row d-flex">								
				        	<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					        	<select id="cIdLeyenda" name="cIdLeyenda" class="form-select form-select-sm">
				              		<option value="Z1:"></option>
						            <option value="Y1:"></option>
						            <option value="X1:"></option>
						            <option value="xx1" selected> -Leyenda- </option>
					            </select>		            
							</div>
						</div>
			      	</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarFecha" class="btn btn-primary btn-sm" onclick="aceptarLeyenda();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
				    </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="dialog-formLeyendaUEjecutora" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						<div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de Leyenda por Unidad Ejecutora</h5>
							<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
						</div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<select id="cIdLeyendaUEjecutora" name="cIdLeyendaUEjecutora" class="form-select form-select-sm">
									<option value="Z1:"></option>
									<option value="Y1:"></option>
									<option value="X1:"></option>
									<option value="xx1" selected> -Leyenda- </option>
								</select>		            
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarLeyendaUE" class="btn btn-primary btn-sm" onclick="aceptarLeyendaUEjecutora();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
					</div>
				</div>
			</div>
		</div>
	</body>
			
</html>
