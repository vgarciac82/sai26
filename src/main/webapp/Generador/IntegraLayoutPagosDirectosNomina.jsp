<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIniciaEstatus = "NO";
	String msg = "";
	
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
	
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Layout Pago Directo</title>

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
		var msg = "<%=msg%>";
		var tipoSolicitud = "";
		var modalFecha;
		var modalLeyenda;
		var modalFolioSicop;
		
		function inicio(){
			
			if ("<%=cIniciaEstatus%>" == "NO") {
				$("#pbStatusInicial").attr('disabled', true);
				$("#pbStatusInicial").hide();
			   	
			}

			document.getElementById('archivo').value = "";
			document.getElementById('sDataH').value = "";
			document.getElementById('sDataHCB').value = "";
			document.getElementById('sDataHFecha').value = "";
			document.getElementById('sDataHCxP').value = "";
			document.getElementById('sDataFolios').value = "";			
			
		}
		
		$(document).ready(function(){
					
			$("#btnDevuelveConsolidada").button();
			$("#pbStatusInicial").button();
			$("#pbEnvia").button();	
			$("#btnLayoutBan").button();	
			$("#btnLayoutCmp").button();		
			$("#generaLayoutIntBtn").button().click(
				function(){
					generaLayoutIntCompromiso();
					if( msg != ""){
						Swal.fire({ icon: "error",
									text: msg});
					}
				}
			);
			
			$("#generaLayoutCompromiso").button().click(
				function(){
					reimprimeLayoutCompromiso();
					if( msg != ""){
						Swal.fire({ icon: "error",
									text: msg});						
					}	
				}
			);
			
			$("#generaLayoutBancoCompromiso").button().click(
				function(){
					generaLayoutBancarioRGCompromiso();
					if( msg != ""){
						Swal.fire({ icon: "error",
									text: msg});						
					}
				}
			);

			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora1", {async: false });
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora2", {async: false });
			querySelectPost("rCatalogoLeyendaRead", "cIdLeyenda", {async: false });
			queryFormPost("FechaInicialRead", {async: false });
			queryFormPost("FechaFinalRead", {async: false });
			
			$("#FechaInicial").val(moment().format('yyyy-01-01'));
			$("#FechaFinal").val(moment().format('yyyy-MM-DD'));
			$("#FechaProgramada").val(moment().format('yyyy-MM-DD'));
			
			modalFecha = new bootstrap.Modal(document.getElementById('dialog-formFecha'), 'data-bs-backdrop');
			modalLeyenda = new bootstrap.Modal(document.getElementById('dialog-formLeyenda'), 'data-bs-backdrop');
			modalFolioSicop = new bootstrap.Modal(document.getElementById('dialog-CapturaFolio'), 'data-bs-backdrop');

			$( "#cIdUnidadEjecutora" ).val('<%=cUR%>');
			if ('<%=cUR%>' != 'A02'){
				$( "#cIdUnidadEjecutora" ).attr("disabled", true);
			}

			$("#FechaProgramada").val($("#FechaInicial").val());
			
			$("#FechaInicial1").val( $("#FechaInicial").val() );
			$("#FechaFinal1").val( $("#FechaFinal").val() );
			$("#FechaInicial2").val( $("#FechaInicial").val() );
			$("#FechaFinal2").val( $("#FechaFinal").val() );
			
			$("#pbEnvia").button().click(
					function(){
						generar();
					});
			
			if( msg != ""){
				Swal.fire({ icon: "error",
							text: msg});				
			}
			
			$("#pbStatusInicial").button();
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'), tipoSolicitud);
			fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/'));			
			fnGridCompromisoLayout($("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/'));		
			
			$( "#cIdUnidadEjecutora" ).change(function(){
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), tipoSolicitud);				
			});
			
			$( "#cIdUnidadEjecutora1" ).change(function(){
				fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());				
			});
			
			$( "#cIdUnidadEjecutora2" ).change(function(){
				fnGridCompromisoLayout($("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val(), $("#FechaFinal2").val());
			});
		
		});			
		
		
		
		function tipoRecurso(){
			
			tipoSolicitud = $("input[name='rTipo']:checked").val();
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), tipoSolicitud);
		}
								
		function fnClickAddRow(row) {
			try
			{
				if ($('#chkIntegra').is(':checked'))
				{
					var bExiste = false;
					var szUEjecutora = '';
					var table = document.getElementById('dt_paraEnvio');
					var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
					for ( var i=0; i<aTrs.length;  i++ )     
					{         				
						szUEjecutora = $.trim(table.rows[i+1].cells[0].innerHTML) ;
						if ($.trim(row.cells[1].innerHTML) == szUEjecutora)
						{
							bExiste = true;
							break;
						}
					}			
					if (bExiste)
					{
						var Monto = parseFloat($.trim(table.rows[i+1].cells[1].innerHTML).replace(/,/g,""));
						Monto = Monto + parseFloat(row.cells[5].innerHTML.replace(/,/g,""));
						
						
						table.rows[i+1].cells[1].innerHTML = formatCurrency(Monto);
					}
					else
					{
						$('#dt_paraEnvio').dataTable().fnAddData( [	
							row.cells[2].innerHTML,
		      				row.cells[3].innerHTML,
		      				row.cells[4].innerHTML,
		      				row.cells[5].innerHTML,
		      				row.cells[6].innerHTML,
		      				row.cells[7].innerHTML,
		      				row.cells[8].innerHTML,
		      				row.cells[9].innerHTML,
		      				0,
		      	            row.cells[10].innerHTML
		      	            ] );		
					}
				} else {
					var table2 = $('#dt_paraEnvio').dataTable().fnAddData( [	
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
			      	            ] );
				}
			}
			catch (ex)
			{Swal.fire({ icon: "error",
						 text: ex.message});				
				}

		}
		
		function fnClickAddRowCom(row) {
			try
			{
				if ($('#chkIntegra').is(':checked'))
				{
					var bExiste = false;
					var szUEjecutora = '';
					var table = document.getElementById('dt_paraEnvio');
					var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
					for ( var i=0; i<aTrs.length;  i++ )     
					{         				
						szUEjecutora = $.trim(table.rows[i+1].cells[0].innerHTML) ;
						if ($.trim(row.cells[1].innerHTML) == szUEjecutora)
						{
							bExiste = true;
							break;
						}
					}			
					if (bExiste)
					{
						var Monto = parseFloat($.trim(table.rows[i+1].cells[1].innerHTML).replace(/,/g,""));
						Monto = Monto + parseFloat(row.cells[5].innerHTML.replace(/,/g,""));
						
						
						table.rows[i+1].cells[1].innerHTML = formatCurrency(Monto);
					} 	else
					{
						$('#dt_paraEnvio').dataTable().fnAddData( [	
							row.cells[2].innerHTML,
		      				row.cells[3].innerHTML,
		      				row.cells[4].innerHTML,
		      				row.cells[5].innerHTML,
		      				row.cells[6].innerHTML,
		      				row.cells[7].innerHTML,
		      				row.cells[8].innerHTML,
		      				row.cells[9].innerHTML,
		      				0,
		      	            row.cells[10].innerHTML
		      	            ] );		
					}
				}else {
		
					var table2 = $('#dt_CompPNNominaLayout').dataTable().fnAddData( [	
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
			      	            ] );
					}
			}
			catch (ex)
			{Swal.fire({ icon: "error",
				 		 text: ex.message});
				}

		}
		
		function fnClickDellRows(){
			var table2 = $('#dt_paraEnvio').dataTable();
			table2.fnClearTable();				
		}
		
		function fnClickDellRowsCom(){
			var table2 = $('#dt_CompPNNominaLayout').dataTable();
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
						var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
						
						$('#sDataFolios').val($('#sDataFolios').val() + " " + caNoFolio + " ,");
													
						vacio = false;
				    } 
				}
 				if (!vacio) {
 					$.ajax({
						url: '../gstnmngr/PagosDirectosConLayout',
						type: 'post',
						dataType: 'json',
						data: { sDataFolios : $("#sDataFolios").val() },
						success: function(data) {
							Swal.fire({ icon: "success",
								 		text: "Se regresaron exitosamente."}).then (function() { location.reload();});														
						}
					});
 				}
            	
        	}catch(e) {
        		Swal.fire({ icon: "error",
			  				text: e});         		
    		}
    		if(vacio){
    			Swal.fire({ icon: "warning",
			 				text: "Debe marcar al menos una fila."});    		    
    		    location.reload(true);
    		}
 		}
		
		function enviar(){
			var vacio = true;
			if(tipoSolicitud == "SI"){	
				Swal.fire({ icon: "info",
	 						text: "Los pagos mayores a 300 UMAS se tramitan en la pestaña Layouts Compromiso."});				
				return;
			} else {
	 			try { 							
	 				fnClickDellRows();
	        		var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cCOLUMNALLAVE = 2;
	 				var cCUENTABANCARIA = 5;
	 				var cRFC = 3;
	 				var CxP = 9;
	 				//if (aTrs.length > 0){
					document.getElementById('sDataH').value = " ";
					document.getElementById('sDataHCB').value = " ";
					document.getElementById('sDataHFecha').value = " ";
					document.getElementById('sDataHLeyenda').value = " ";
					document.getElementById('sDataHCxP').value = " ";
					
	 				for ( var i=1; i<=aTrs.length;  i++ )     
						{   
	 						var row= table.rows[i];
	 						var chkbox = row.cells[0].childNodes[0];
	 						
	 						if(null != chkbox && true == chkbox.checked)
							{
	 							//Validamos la cuenta bancaria
	 							if (row.cells[cCUENTABANCARIA].innerHTML == "--")
	 							{
	 								Swal.fire({ icon: "warning",
	 			 								text: "El RFC : " + row.cells[cRFC].innerHTML + " no tiene asignada Cuenta Bancaria."});	 								
	 								//location.reload(true);
	 								return;
	 							}
	 							$("#pbEnvia").css("visibility","visible");
	 							$("#pbEnviaDocumentacion").css("visibility","visible");
	 							$("#pbEnviaDoc").css("visibility","visible");
	 							
								var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
								var caNoCuentaBancaria = row.cells[cCUENTABANCARIA].innerHTML; // el 11 corresponde al folio
								var caNoFecha = row.cells[7].innerHTML; // el 11 corresponde al folio
								var caNoLeyenda = row.cells[8].innerHTML;
								var caNoCxP = row.cells[CxP].innerHTML;
								
								document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
								document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
								document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
								document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
								document.getElementById('sDataHCxP').value += " " + caNoCxP + " ," ;
								
								vacio = false;
					      		fnClickAddRow(row);
						    }
	 						//alert("Debe marcar al menos una fila");
						}					
	        	}catch(e) {
	        		Swal.fire({ icon: "error",
								text: e});	         		
	    		}  
 				
				if(vacio){
					Swal.fire({ icon: "warning",
								text: "Debe marcar al menos una fila."});																			
	    		}	 			
			}				
 		}
		
		function enviarCompromiso(){
			var vacio = true;			
			if(tipoSolicitud == "NO"){
				Swal.fire({ icon: "info",
							text: "Los pagos menores a 300 UMAS se tramitan en la pestaña Pagos en proceso SICOP."});							
				return;
			} else {			
	 			try {	 		
	 				fnClickDellRowsCom();
	        		var table = document.getElementById('dt_generados');
	 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
	 				var cCOLUMNALLAVE = 2;
	 				var cCUENTABANCARIA = 5;
	 				var cRFC = 3;
	 				
					document.getElementById('sDataH').value = " ";
					document.getElementById('sDataHCB').value = " ";
					document.getElementById('sDataHFecha').value = " ";
					document.getElementById('sDataHLeyenda').value = " ";
					document.getElementById('sDataHCxP').value = " ";
					
	 				for ( var i=1; i<=aTrs.length;  i++ ){   
 						var row= table.rows[i];
 						var chkbox = row.cells[0].childNodes[0];
 						
 						if(null != chkbox && true == chkbox.checked){
 							//Validamos la cuenta bancaria
 							if (row.cells[cCUENTABANCARIA].innerHTML == "--"){
 								Swal.fire({ icon: "warning",
 											text: "El RFC : " + row.cells[cRFC].innerHTML + " no tiene asignada Cuenta Bancaria."}); 								
 								//location.reload(true);
 								return;
 							}
 							$("#pbEnvia").css("visibility","visible");
 							$("#pbEnviaDocumentacion").css("visibility","visible");
 							$("#pbEnviaDoc").css("visibility","visible");
 							
							var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
							var caNoCuentaBancaria = row.cells[5].innerHTML; // el 11 corresponde al folio
							var caNoFecha = row.cells[7].innerHTML; // el 11 corresponde al folio
							var caNoLeyenda = row.cells[8].innerHTML;
							var caNoCxP = row.cells[9].innerHTML;
							
							document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
							document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
							document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
							document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
							document.getElementById('sDataHCxP').value += " " + caNoCxP + " ," ;
							
							vacio = false;
							fnClickAddRowCom(row);
					    } 
					}	 			
	 				
					
	        	}catch(e) {
	        		Swal.fire({ icon: "error",
								text: e});	        		         		
	    		}
        	
	        	if(vacio){
	        		Swal.fire({ icon: "warning",
								text: "Debe marcar al menos una fila."});			
	    		} else {
	    			$("#tipoLayout").val();
	 				document.envioSICOP.action='../gstnmngr/generaLayoutPDNominaCompromiso';
					document.envioSICOP.method='POST';
					document.envioSICOP.submit();					
	    		}
			}        	        
 		}

 		function generar()
 		{
 			try 
 			{
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount2 = table2.rows.length;
            	
            	if (rowCount2 == 0)
            	{
            		Swal.fire({ icon: "warning",
								text: "Debe seleccionar al menos un pago"});            	            		
            		return;
            	}
            	
            	document.getElementById('archivo').value = "1";
            	document.location.href='../gstnmngr/generaLayoutPagosDirectos?archivo=1';
            	document.envioSICOP.submit();
				
            	fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), tipoSolicitud);
            	
            	fnClickDellRows();
            	
            	$("#pbEnvia").css("visibility","hidden");
         	}
 			catch(e) {
 				Swal.fire({ icon: "error",
							text: e}); 			         		
         	}
 		}
	
 		function generarDocumentacion(){
 			try {
        		var table = document.getElementById('dt_generados');
        		var table2 = document.getElementById('dt_paraEnvio');
            	var rowCount = table.rows.length;
            	var rowCount2 = table2.rows.length;
            	
            	if (rowCount2 == 0)
            	{	
            		Swal.fire({ icon: "warning",
								text: "Debe seleccionar al menos un pago."});            		
            		return;
            	}
            	
            	for(var i=0; i<rowCount; i++) {
            		var row = table.rows[i];
                	var chkbox = row.cells[0].childNodes[0];
                	
                	if(null != chkbox && true == chkbox.checked) {
						//quitando de la lista de compromisos los registros enviados a SICOP...
						table.deleteRow(i);
						rowCount--;
                    	i--;
                    	
                	}
            	}
            	document.getElementById('archivo').value = "2";
            	document.location.href='../gstnmngr/generaLayoutPagosDirectos?archivo=2';
            	document.envioSICOP.submit();
            	
            	//document.envioSICOP.submit();
            	
         	}catch(e) {
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
		     
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) 
		      {
	    		Swal.fire({ icon: "warning",
							text: "La fecha incial no puede ser mayor a la fecha final."});		    	  
		         document.getElementById("FechaFinal").value = document.getElementById("FechaInicial").value;
		      }
		      fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), tipoSolicitud);
		   }
		}
		
		function refreshGrid(){
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), tipoSolicitud);
		}
		
		function valFecha1(object1) 
		{
			var FechaIni = document.getElementById("FechaInicial1").value;
		    var FechaFin = document.getElementById("FechaFinal1").value;
			
		    if (object1.value != "") 
			{
		      
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) 
		      {
		    	  Swal.fire({ icon: "warning",
							  text: "La fecha incial no puede ser mayor a la fecha final."});		      		    	  
		         document.getElementById("FechaFinal1").value = document.getElementById("FechaInicial1").value;
		      }
		      fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val(), $("#FechaFinal1").val());
		   }
		}
		
		function valFecha2(object1) 
		{
			var FechaIni = document.getElementById("FechaInicial2").value;
		    var FechaFin = document.getElementById("FechaFinal2").value;
			
		    if (object1.value != "") {
		    	if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) {
		    		Swal.fire({ icon: "warning",
					 			text: "La fecha incial no puede ser mayor a la fecha final."});		      		      		      	
		      		document.getElementById("FechaFinal2").value = document.getElementById("FechaInicial2").value;
		      	}
		      
		      fnGridCompromisoLayout( $("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val(), $("#FechaFinal2").val() );
		      
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
				if (!fnValidaRowSel())
				{
					Swal.fire({ icon: "warning",
						  		text: "Debe seleccionar primero un renglón."});		      				
					return;
				}
				modalLeyenda.show();
			});
			
		$( "#pbFechaProgramada" )
			.button()
			.click(function() {
				if (!fnValidaRowSel())
				{
					Swal.fire({ icon: "warning",
				  				text: "Debe seleccionar primero un renglón."});									
					return;
				}
				modalFecha.show();
			});
			
		function fnValidaRowSel()
		{
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var bSel = false;
			
			for ( var i=0; i<aTrs.length;  i++ )     
			{         
				var row= table.rows[i];
				
				if ( $(aTrs[i]).hasClass('table-primary') )         
				{
					bSel = true; 
					break;
			    }  						
			}
			return bSel;	
		}
	
		});
		
		function aceptarFechaProgramada(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var cFechaProgramada = 7;
				
			for ( var i=0; i<aTrs.length;  i++ )     
			{         
					var row= table.rows[i];
					
					//if ( $(aTrs[i]).hasClass('row_selected') ){
						table.rows[i+1].cells[cFechaProgramada].childNodes[0].nodeValue = $("#FechaProgramada").val().split('-').reverse().join('/');
						//break;
			    //}	 							 						
			}
			modalFecha.hide();
		}
		
		function aceptarLeyenda(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var cLeyenda = 8;
				
			for ( var i=0; i<aTrs.length;  i++ )     
			{         
					var row= table.rows[i];
					
					//if ( $(aTrs[i]).hasClass('row_selected') ){
						table.rows[i+1].cells[cLeyenda].childNodes[0].nodeValue = $("#cIdLeyenda").val();
						//break;
			    //} 	 						
			}
			modalLeyenda.hide();
		}
	</script>
	</head>
	<br/>
  	<body id="dt_example" onLoad="inicio();" >
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<input type="hidden" id="tipoLayout"name="tipoLayout" value="3">
			
			<div class="card-header"> <h3> Layout Pagos Directo Nomina</h3> </div>
			<div class="mt-4 row d-flex justify-content-center">
	      							
	       		<ul class="nav nav-tabs" id="list-opciones">
	       			 <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="refreshGrid();" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Pendientes de generar Layout</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-enviar" type="button" role="tab" aria-controls="tabs-enviar" aria-selected="false">Pagos en proceso SICOP</button>
		            </li>								     
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-3" onCLick="enviarCompromiso();" data-bs-toggle="tab" data-bs-target="#tabs-3-generados" type="button" role="tab" aria-controls="tabs-generados" aria-selected="false">Layouts generados</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-4" data-bs-toggle="tab" data-bs-target="#tabs-4-compromiso" type="button" role="tab" aria-controls="tabs-compromiso" aria-selected="false">Layouts compromiso</button>
		            </li>								            
				</ul>
				
				<div class="tab-content mt-3" id="tabContent">	
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout">
				  		<form>
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
								<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="button" id="pbFechaProgramada" style="visibility: hidden" value="Fecha Programada" class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formFecha"/> &nbsp; &nbsp; &nbsp;
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="button" id="pbLeyenda" style="visibility: hidden" value="Leyenda" class="btn btn-secondary btn-sm " data-toggle="modal" data-target="#dialog-formLeyenda"/>  &nbsp; &nbsp; &nbsp;
								</div>
							</div>		
					        <h5> Tipo de Pago </h5>
							<hr class="mt-2"/>
					        
					        <div class="row d-flex">								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="sinCompromiso" name="rTipo" class="form-check-input" value="NO" onclick="tipoRecurso()"> Sin Compromiso
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="conCompromiso" name=rTipo class="form-check-input" value="SI" onclick="tipoRecurso()"> Con Compromiso
								</div>
							</div>
				          
			          		<br/>
			          		
			          		<h5> Pagos sin layout </h5>
							<hr class="mt-2"/>
			          
							<jsp:include page="listaPagosDirectosNomina.jsp"></jsp:include>
						
						</form>	
					</div>
					
					<div class="tab-pane fade" id="tabs-2-enviar" role="tabpanel" aria-labelledby="tabs-enviar">				
					    <br/>
					    
					    <h5> Integraci&oacute;n del layout para ser enviado a SICOP </h5>
						<hr class="mt-3"/>
					    
					    <jsp:include page="listaPagosDirectosNominaEnviados.jsp"></jsp:include>
						
					</div>
					
			        <div class="tab-pane fade" id="tabs-3-generados" role="tabpanel" aria-labelledby="tabs-generados">
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
								<input type="button" id="pbStatusInicial" name="pbStatusInicial" onClick="Inicializa();" value="Devolver" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
											    
						<h5> Pagos enviados a SICOP </h5>
						<hr class="mt-3"/>
						
					    <jsp:include page="listaPagosDirectosNominaConLayout.jsp"></jsp:include>
						
					</div>		 
					
					<div class="tab-pane fade" id="tabs-4-compromiso" role="tabpanel" aria-labelledby="tabs-compromiso">
			        	<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="cIdUnidadEjecutora2" class="form-label"> U. Ejecutora: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<input type="hidden" id="cUResp2" name="cUResp2" class="paso04" value="<%=cUR%>"/>
								<select id="cIdUnidadEjecutora2" name="cIdUnidadEjecutora2" class="form-select form-select-sm">
				            		<option value="<%=cUR%>"></option>
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="FechaInicial2" class="form-label"> Fecha Inicio: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha2(this)" name="FechaInicial2" type="date" id="FechaInicial2" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="FechaFinal2" class="form-label"> Fecha Final: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha2(this)" name="FechaFinal2" type="date" id="FechaFinal2" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>								
						</div>	
						
						<br/>
						
						<h5> Layouts de Compromiso Generados </h5>
						<hr class="mt-3"/>
						
					    <div class="row d-flex">															
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<input type="button" id="generaLayoutIntBtn" name="generaLayoutIntBtn" style="visibility: hidden" value="Generar Layout" class="btn btn-secondary btn-sm"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="generaLayoutCompromiso" name="generaLayoutCompromiso" value="Regenerar Layout Compromiso" class="btn btn-secondary btn-sm"/>
							</div>
						</div>

					    <jsp:include page="listaCompromisoPDNominaConLayout.jsp"></jsp:include>
						
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
			
		</div>
	</body>	
</html>
