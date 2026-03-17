<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
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
	
	String msg = "";
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cuentas por Pagar de Programas Federalizados</title>

		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
			
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

	var UMA = "";
	var msg = "<%=msg%>";
		function inicio(){
		
			if ("<%=cIniciaEstatus%>" == "NO") {
				$("#pbStatusInicial").attr('disabled', true);
				$("#pbStatusInicial").hide();			   	
			}
			
			document.getElementById('archivo').value = "";
			document.getElementById('sDataH').value = "";
			document.getElementById('sDataHCB').value = "";
			document.getElementById('sDataHFecha').value = "";
			document.getElementById('sDataFolios').value = "";
			
		}
		
		$(document).ready(function(){

			querySelectPost("CAT_UNIDAD_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
			querySelectPost("rCatalogoLeyendaRead", "cIdLeyenda", {async: false });
			queryFormPost("FechaInicialRead", {async: false });
			queryFormPost("FechaFinalRead", {async: false });
			
			$("#FechaInicial").val(moment().format('yyyy-01-01'));
			$("#FechaFinal").val(moment().format('yyyy-MM-DD'));
			$("#FechaProgramada").val(moment().format('yyyy-MM-DD'));
			
			modalFecha = new bootstrap.Modal(document.getElementById('dialog-formFecha'), 'data-bs-backdrop');
			modalLeyenda = new bootstrap.Modal(document.getElementById('dialog-formLeyenda'), 'data-bs-backdrop');
			
			if ($("#chk_UMA").prop("checked")) {
				UMA = "1";
			}else{
				UMA = "0";
			}
			
			$( "#pbEnvia" ).button();
			$( "#pbStatusInicial" ).button();
			$( "#cIdUnidadEjecutora" ).val('<%=cUR%>');
			if ('<%=cUR%>' != 'A02'){
				$( "#cIdUnidadEjecutora" ).attr("disabled", true);
			}
			$("#FechaProgramada").val($("#FechaInicial").val());
			fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);
			fnGridConLayout($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);	
			
			$("#tblCuentasBancarias tbody").click(function(event) {
				$(oTableCB.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');

			});
			
			if( msg != ""){
				Swal.fire ('Error', msg, 'error' );
			}
			
			$("#chk_UMA").change(function() {
				if ($("#chk_UMA").prop("checked")) {
					UMA = "1";
				}else{
					UMA = "0";
				}
				
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);
				fnGridConLayout($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);	

			});
			
			$( "#cIdUnidadEjecutora" )
			.change(function() 
			{
				//alert($("#cIdUnidadEjecutora").val());
				
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);
				fnGridConLayout($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);
			});
			
			$("#chkTodos").change(function() {
				if ($("#chkTodos").prop("checked")) {
					$("input:checkbox").attr('checked', 'checked');
				}else{
					$("input:checkbox").removeAttr('checked');
				}	
			});
		});

		
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
					
					if(null != chkbox && true == chkbox.checked)
					{ 							
						var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
						
						$('#sDataFolios').val($('#sDataFolios').val() + " " + caNoFolio + ","); //en el servlet se elimina la coma al final
													
						vacio = false;
				    } 
				}
 				if (!vacio) {
	 				$.ajax({
						url: '../gstnmngr/PagoProgFederalizadosConLayout',
						type: 'post',
						dataType: 'json',
						data: { sDataFolios : $("#sDataFolios").val() },
						success: function(data) {
							queryFormPost("updateLayoutDevuelto", {async: false });							
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
    		    Swal.fire("Atención","Debe marcar al menos una fila","warning");
    		    location.reload(true);
    		}
 		}		
		
 		function enviar()
		{
			var table2 = $('#dt_paraEnvio').dataTable();
			table2.fnClearTable();
			//obj.fnClearTable(0);
    		table2.fnDraw(false);
    		
    		document.getElementById('sValorUMA').value = UMA;
    		var ur = "";
    		
    		if ($('#cIdUnidadEjecutora').val('A02'))
    			ur = "%";
    		
    		creaTablaEnviados(ur, UMA);
		}
 		
		function generar()
 		{
 			try 
 			{
				var bSeleccionados = false;
				
				var table = document.getElementById('dt_paraEnvio');
 				var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();

 				var cCOLUMNALLAVE = 2;
 				var cCUENTABANCARIA = 5;
 				var cRFC = 3;
 				
				document.getElementById('sDataH').value = "" ;
				document.getElementById('sDataHCB').value = "" ;
				document.getElementById('sDataHFecha').value = "" ;
				document.getElementById('sDataHLeyenda').value = "" ;
							
 				for ( var i=1; i<=aTrs.length;  i++ )    
				{         
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					
					if(null != chkbox && true == chkbox.checked)
						{
							var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; 
							var caNoCuentaBancaria = row.cells[5].innerHTML; 
							var caNoFecha = row.cells[7].innerHTML; 
							var caNoLeyenda = row.cells[8].innerHTML;
							var caAutoriza = row.cells[10].innerHTML;
							if (caAutoriza != "--")
							{
								document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
								document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
								document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
								document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
								
								bSeleccionados = true;
							}
					    } 			
				}
 				if (!bSeleccionados)
				{
					Swal.fire("Seleccione","Debe marcar como seleccionado al menos \run renglón que este autorizado.","warning");
					return;
				}
 				
            	
            	
            	document.getElementById('archivo').value = "1";
            	document.location.href = '../gstnmngr/generaLayoutPagosProgFederalizados?archivo=1';
				document.envioSICOP.submit();

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
		  						text: "Debe seleccionar al menos un pago"});         		
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
				document.location.href = '../gstnmngr/generaLayoutPagosProgFederalizados?archivo=2';
				document.envioSICOP.submit();
            	
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
	 						  text: "La fecha incial no puede ser mayor a la fecha final"});
		         document.getElementById("FechaFinal").value = document.getElementById("FechaInicial").value;
		      }
		      fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);
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
		
		$( "#pbXAutorizar" )
			.button()
			.click(function() {
				var bSeleccionados = false;
				
				var table = document.getElementById('dt_generados');
 				var aTrs = $('#dt_generados').dataTable().fnGetNodes();

 				var cCOLUMNALLAVE = 3;
 				var cCUENTABANCARIA = 6;
 				var cRFC = 4;
 				var szTabla = "FEDERALIZADOSLAYOUTAUT";
					
 				for ( var i=1; i<=aTrs.length;  i++ ){         
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					
					if(null != chkbox && true == chkbox.checked){
 						//Validamos la cuenta bancaria
 						if (row.cells[cCUENTABANCARIA].innerHTML == ""){
 							Swal.fire("Operación cancelada.","El RFC: " + row.cells[cRFC].innerHTML + " no tiene asignada Cuenta Bancaria.","error");
 							return;
 						}
						var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; 
						var caNoCuentaBancaria = row.cells[6].innerHTML; 
						var caNoFecha = row.cells[8].innerHTML; 
						var caNoLeyenda = row.cells[9].innerHTML;
						
						if (caNoCuentaBancaria!=""){
							document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
							document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
							document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
							document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
						
							var szParam = caNoFolio + ", '" + caNoCuentaBancaria + "','" + caNoLeyenda + "', '" + caNoFecha + "'";
							
 				   			$("#folio").val(caNoFolio);
 				   			$("#cuenta").val(caNoCuentaBancaria);
 				   			$("#fecha").val(caNoFecha);
 				   			$("#leyenda").val(caNoLeyenda);
 				   			$("#usuario").val('<%=usuario.getLogin()%>'); 
 				   			
 				   			queryFormPost({
								queryName:"insertFederalizadoAutoriza",
								async:false,
								callback:function(){
									queryFormPost("updateFederalizadoAutoriza", {async: false });
								} 
							});
 				   			
							bSeleccionados = true;
						}
				    } 			
				}
 				if (!bSeleccionados){
					Swal.fire("Seleccione","Debe marcar como seleccionado al menos un renglón.","warning");
					return;
				}
 								
				$("#pbXAutorizar").css("visibility","hidden");				
				$("#pbFechaProgramada").css("visibility","hidden");
				$("#pbLeyenda").css("visibility","hidden");	
				
				var table2 = $('#dt_generados').dataTable();
				table2.fnClearTable();
	    		table2.fnDraw(false);
	    		
	    		fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(), UMA);
	    		
			});
		
		});
		
		function aceptarFechaProgramada(){
			// escribir la fecha programada
			//Validamos que la fecha sea mayor al dia de hoy mas tres dias
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var cFechaProgramada = 8;
				
			for ( var i=0; i<aTrs.length;  i++ )     
			{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('row_selected') )         
				{
						table.rows[i+1].cells[cFechaProgramada].childNodes[0].nodeValue = $("#FechaProgramada").val().split('-').reverse().join('/');
						break;
			    }
						 						
			}
			modalFecha.hide();
		}
		
		function aceptarLeyenda(){
			var table = document.getElementById('dt_generados');
			var aTrs = $('#dt_generados').dataTable().fnGetNodes();
			var cLeyenda = 9;
				
			for ( var i=0; i<aTrs.length;  i++ )     
			{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('row_selected') )         
				{
						table.rows[i+1].cells[cLeyenda].childNodes[0].nodeValue = $("#cIdLeyenda").val();
						break;
			    } 	 						
			}
			modalLeyenda.hide();
		}
	</script>
	</head>
	<br/>
	<body id="dt_example" onLoad="inicio();">
		<form id="formAutorizaFed">
			<input name="folio" id="folio" type="hidden" value="">
			<input name="cuenta" id="cuenta" type="hidden" value="">
			<input name="fecha" id="fecha" type="hidden" value="">
			<input name="leyenda" id="leyenda" type="hidden" value="">
			<input name="usuario" id="usuario" type="hidden" value="">
			
		</form>
		
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<div class="card-header"> <h3> Layout Programas Federalizados </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex justify-content-center">
	      							
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
				
				<div class="tab-content mt-3" id="tabContent">	
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout">
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
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<input type="checkbox" id="chk_UMA" name="chk_UMA" value="" class="form-check-input"> Pedido (Menor 300 UMAS) 
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
								<input type="button" id="pbXAutorizar" style="visibility: hidden" value="Envíar a Autorizar" class="btn btn-primary"/>&nbsp; &nbsp; &nbsp;
							</div>														
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="pbFechaProgramada" style="visibility: hidden" value="Fecha Programada" class="btn btn-secondary" data-toggle="modal" data-target="#dialog-formFecha"/> &nbsp; &nbsp; &nbsp;
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="pbLeyenda" style="visibility: hidden" value="Leyenda" class="btn btn-dark" data-toggle="modal" data-target="dialog-formLeyenda"/>  &nbsp; &nbsp; &nbsp;
							</div>
						</div>
						
						
						<jsp:include page="listaPagoProgFederalizados.jsp"></jsp:include>
						
					</div>
					
					<div class="tab-pane fade show" id="tabs-2-enviar" role="tabpanel" aria-labelledby="tabs-enviar">
						
			        	<br>
					   	<h5> Integraci&oacute;n del layout para ser enviado a SICOP (Nota: Al generar el archivo solo tomará los pagos seleccionados y autorizados) </h5>
						<hr class="mt-3"/>
						
						<jsp:include page="listaPagoProgFederalizadosEnviados.jsp"></jsp:include>
						
					</div>
					
			        <div class="tab-pane fade show" id="tabs-3-generados" role="tabpanel" aria-labelledby="tabs-generados">
						<h5> Pagos enviados a SICOP </h5>
						<hr class="mt-3"/>
					    <jsp:include page="listaPagoProgFederalizadosConLayout.jsp"></jsp:include>
						<div class="mt-2">								
			        			<input type="button" id="pbStatusInicial" name="pbStatusInicial" onClick="Inicializa();" value="Devolver" class="btn btn-secondary">
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
</html>
