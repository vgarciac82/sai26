<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@page import="java.text.DecimalFormat"%>

<%
	String cCentroContable = "";
	String algo = "";
 	 	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	String mensaje = "";
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	} 

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado, Consulte a su administrador.";
	}

	algo = usuario.getLogin();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Consulta Saldo</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "css/demo_table_jui.css";
	@import "css/demo_page.css";
</style>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>


<script type="text/javascript" charset="utf-8">

var oTable1;
var oTable2;
var oTable3;
						
$(document).ready(function() {	
		
		$("#Exportar").button();
		$("#Cerrar").button();
		
		oTable1 = $("#dt_Consulta").dataTable(
					{
						bAutoWidth : false,
						oLanguage: {
									sProcessing: "Procesando... espere un momento!!!",
									sZeroRecords: "No hay registros a mostrar",
									sEmptyTable: "No hay datos en la tabla",
									sLoadingRecords: "Cargando...",
									sSearch: "Buscar:",
									sInfo: "Número de Registros --> _TOTAL_ ",
									sInfoEmpty: "Sin Registros"
								   },
						bPaginate : false,
						bFilter : true,
						bSort : true,    
						sScrollY : 450,
						sScrollYInner : "100%",
   						bScrollCollapse: true,
						bRetrive : true,
						bDestroy : true,
						bJQueryUI : true,
						bServerSide : false,
						bProcessing : true,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
									{ sName: "nCuenta" 	    },
									{ sName: "cSubCuenta"	},
									{ sName: "cCC"		 	},
									{ sName: "mSaldoIni"  , sClass: "alignRight" 	},
									{ sName: "mCargo"	  , sClass: "alignRight"	},
									{ sName: "mAbono"	  , sClass: "alignRight"	},
									{ sName: "mSaldoFin"  , sClass: "alignRight"	}
								   ]
					}
				);
		
		oTable2 = $("#dt_movimientos").dataTable(
				{
					bAutoWidth : false,
					oLanguage: {
								sProcessing: "Procesando... espere un momento!!!",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
								sLoadingRecords: "Cargando...",
								sSearch: "Buscar:",
								sInfo: "Número de Registros --> _TOTAL_ ",
								sInfoEmpty: "Sin Registros"
							   },
					bPaginate : false,
					bFilter : true,
					bSort : true,    
					sScrollY : 450,
					sScrollYInner : "100%",
						bScrollCollapse: true,
					bRetrive : true,
					bDestroy : true,
					bJQueryUI : true,
					bServerSide : false,
					bProcessing : true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
								{ sName: "cTipoPoliza" 	    },
								{ sName: "nFolioPoliza"	},
								{ sName: "cTipoDocumento"		 	},
								{ sName: "cFolioDocumentoMovimiento" 	},
								{ sName: "MesAplicacion"	 	},
								{ sName: "Cargos"	, sClass: "alignRight" 	},
								{ sName: "Abonos" 	, sClass: "alignRight"  },
								{ sName: "Descripcion" 	},
								{ sName: "Estatus" 	}
							   ]
				}
			);
				
				setDblClck();
				
				setDblClkPoliza();
				
			$("#sIni").val(0.00);
			$("#ca").val(0.00);
			$("#ab").val(0.00);
			$("#sFin").val(0.00);
							
	});
	
	$(function() {
	$( "#scDialog-form" ).dialog({
			autoOpen: false,
			height: 750,
			width: 1250,
			modal: true,
			buttons: {
						"Exportar": function() { 
									$("#movMini").val( $("#mesInicio").val() );
									$("#movMfin").val( $("#mesFin").val() );
									exportaXLSMoviemitnos();  
									},
						"Cerrar"  : function() { 	
										oTable2.fnDestroy();
										$("#dt_movimientos tbody").empty();
										$( this ).dialog( "close" ); 
										}
					 },
			close: function() {
				oTable2.fnDestroy();
				$("#dt_movimientos tbody").empty(); 
			}
		});
	});
	
	$(function() {
	$( "#poliza-form" ).dialog({
			autoOpen: false,
			height: 850,
			width: 1000,
			modal: true,
			buttons: {
						"Exportar": function() { exportaXLSPoliza();  },
						"Cerrar"  : function() {
										oTable3.fnDestroy();
										$("#dt_poliza tbody").empty();
										$( this ).dialog( "close" ); }
					 },
			close: function() {
				oTable3.fnDestroy();
				$("#dt_poliza tbody").empty();
			}
		});
	});
	
	
		function exportaXLS(){
		
			$("#cCC").val(<%=cCentroContable%>);
			$("#exportaConsulta").submit();
		}
	
		function exportaXLSMoviemitnos(){
			$("#exportaMovimientos").submit();	
		}
	
		function exportaXLSPoliza(){		
			$("#exportaPoliza").submit();
		}
			
	function creaTablaConsulta() {
						
			if ($("#cuenta").val() == "" || $("#cuenta").val() == "%" ){
				alert("La cuenta no puede ir vacia, favor de rectificar!!!");
			}
			else{
			
				actualizaDatos();
				
				$("#dt_Consulta tbody").click(function(event) { 
			        $(oTable1.fnSettings().aoData).each(function (){ 
			            $(this.nTr).removeClass('row_selected');
			        	}); 
			        $(event.target.parentNode).addClass('row_selected'); 
		    	}); 
		    
			}
			
			//var suma = $("#dt_Consulta").dataTable().fnGetData();
			var renglon;
			var sSI = 0;
			var sCA = 0;
			var sAB = 0;
			var sSF = 0;
			
			$(oTable1.fnSettings().aoData).each(function() {
				renglon = $(this._aData);
				sSI += Math.round( parseFloat( renglon[3].replace(/[^\d.-]/g, '') * 1 ).toFixed(2) * 100 ) / 100;
				sCA += Math.round( parseFloat( renglon[4].replace(/[^\d.-]/g, '') * 1 ).toFixed(2) * 100 ) / 100;
				sAB += Math.round( parseFloat( renglon[5].replace(/[^\d.-]/g, '') * 1 ).toFixed(2) * 100 ) / 100;
				sSF += Math.round( parseFloat( renglon[6].replace(/[^\d.-]/g, '') * 1 ).toFixed(2) * 100 ) / 100;	
				}
			);
						
			$("#sIni").val( sSI);
			$("#ca"  ).val( sCA);
			$("#ab"  ).val( sAB);
			$("#sFin").val( sSF.toFixed(2) );
			
			
			$("#sIni").formatCurrency();
			$("#ca"  ).formatCurrency();
			$("#ab"  ).formatCurrency();
			$("#sFin").formatCurrency();
			
			document.getElementById("exportar").disabled = false;

		}
	
	
	function setDblClkPoliza() {
	 			 		
            $("#dt_movimientos tbody").dblclick(function(evt) {

                var aPos = oTable2.fnGetPosition(evt.target.parentNode);

                if (aPos instanceof Array)
                    currIndex = aPos[0];
                else
                    currIndex = aPos;

               var arr = oTable2.fnGetData()[currIndex];
                
                $("#cTipo"			).val( arr[0]);
                $("#nFolio"			).val( arr[1]);
                $("#cTipoDocumento"	).val( arr[2]);
                $("#nFolioDocumento").val( arr[3]);
               
               if ( arr[8] == "Poliza de Cancelacion"){
               		 $("#nEstatus").val( 0 );
               } else{
               		 $("#nEstatus").val( 1 );
               }
                                         

			oTable3 = $("#dt_poliza").dataTable(
					{
						bAutoWidth : false,
						oLanguage: {
									sProcessing: "Procesando... espere un momento!!!",
									sZeroRecords: "No hay registros a mostrar",
									sEmptyTable: "No hay datos en la tabla",
									sLoadingRecords: "Cargando...",
									sSearch: "Buscar:",
									sInfo: "Número de Registros --> _TOTAL_ ",
									sInfoEmpty: "Sin Registros"
								   },
						bPaginate : false,
						bFilter : true,
						bSort : true,    
						sScrollY : 200,
						sScrollYInner : "100%",
   						bScrollCollapse: true,
						bRetrive : true,
						bDestroy : true,
						bJQueryUI : true,
						bServerSide : true,
						bProcessing : true,
						aaSorting: [[ 1, "asc" ]] ,
						sAjaxSource : window.location.protocol + "//"+ window.location.host + "/"+ window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=fn_l_DetallePoliza("+$("#nFolio").val()+",'"+$("#cTipo").val()+"',"+$("#nFolioDocumento").val()+",'"+$("#cTipoDocumento").val()+"')",
						aoColumns: [
									{ sName: "nCuenta" 	    },
									{ sName: "SubCuenta"	},
									{ sName: "Cargo"	, sClass: "alignRight" 	},
									{ sName: "Abono"	, sClass: "alignRight" 	}
								   ]
					}
				); 
				
               	$("#dt_poliza tbody").click(function(event) { 
			        $(oTable3.fnSettings().aoData).each(function (){ 
			            $(this.nTr).removeClass('row_selected');
			        	}); 
			        $(event.target.parentNode).addClass('row_selected'); 
		    	}); 
                
                queryFormPost("encabezado_poliza", {async: false});	
                
                 $("#pCargos").formatCurrency();
                 $("#pAbonos").formatCurrency();
                 
                queryFormPost("datosReferencia", {async: false});
                
                switch($("#cTipoDocumento").val()) {
				     case "CHEQUE":
				       queryFormPost("tieneNumCheque", {async: false});
				        break;
				     case "CAJA":
				       queryFormPost("tieneNumCheque", {async: false});
				        break;
				     case "RELACIONGASTOS":
				       queryFormPost("tieneNumCheque", {async: false});
				        break;
				     default:
				         $("#rCheque").val(0);
				 } 
                
               $( "#poliza-form" ).dialog( "open" );

            });
        }
	
	 function setDblClck() {
	 			 		
            $("#dt_Consulta tbody").dblclick(function(evt) {

                var aPos = oTable1.fnGetPosition(evt.target.parentNode);

                if (aPos instanceof Array)
                    currIndex = aPos[0];
                else
                    currIndex = aPos;

               var arr = oTable1.fnGetData()[currIndex];
                
                $("#scCuenta"	).val( arr[0]);
                $("#scSubcuenta").val( arr[1]);
                $("#scCc"		).val( arr[2]);
                $("#scSi"		).val( arr[3]);
                $("#scCargos"	).val( arr[4]);
                $("#scAbonos"	).val( arr[5]);
                $("#scSf"		).val( arr[6]);
                
                $("#scSi").formatCurrency();
                $("#scCargos").formatCurrency();
                $("#scAbonos").formatCurrency();
                $("#scSf").formatCurrency();
				
			oTable2 = $("#dt_movimientos").dataTable(
					{
						bAutoWidth : false,
						oLanguage: {
									sProcessing: "Procesando... espere un momento!!!",
									sZeroRecords: "No hay registros a mostrar",
									sEmptyTable: "No hay datos en la tabla",
									sLoadingRecords: "Cargando...",
									sSearch: "Buscar:",
									sInfo: "Número de Registros --> _TOTAL_ ",
									sInfoEmpty: "Sin Registros"
								   },
						bPaginate : false,
						bFilter : true,
						bSort : true,    
						sScrollY : 350,
						sScrollYInner : "100%",
   						bScrollCollapse: true,
						bRetrive : true,
						bDestroy : true,
						bJQueryUI : true,
						bServerSide : true,
						bProcessing : true,
						aaSorting: [[ 1, "asc" ]] ,
						sAjaxSource : window.location.protocol + "//"+ window.location.host + "/"+ window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=fn_l_ConsultaMovimientos("+$("#mesInicio").val()+","+$("#mesFin").val()+",'"+$("#scCuenta").val()+"','"+$("#scSubcuenta").val()+"','"+$("#scCc").val()+"')",
						aoColumns: [
									{ sName: "cTipoPoliza" 	    },
									{ sName: "nFolioPoliza"	},
									{ sName: "cTipoDocumento"		 	},
									{ sName: "cFolioDocumentoMovimiento" 	},
									{ sName: "MesAplicacion"	 	},
									{ sName: "Cargos"	, sClass: "alignRight" 	},
									{ sName: "Abonos" 	, sClass: "alignRight"  },
									{ sName: "Descripcion" 	},
									{ sName: "Estatus" 	}
								   ]
					}
				); 
				
               	$("#dt_movimientos tbody").click(function(event) { 
			        $(oTable2.fnSettings().aoData).each(function (){ 
			            $(this.nTr).removeClass('row_selected');
			        	}); 
			        $(event.target.parentNode).addClass('row_selected'); 
		    	}); 
                
               $( "#scDialog-form" ).dialog( "open" );

            });
        }
	

	function actualizaDatos(){
			
			$('#dt_Consulta').dataTable().fnClearTable(); 
					
			$.ajax({
					async: false,
					cache: false,
					type: 'GET',
					dataType: 'json',
					url: "../reportes/ConsultaSaldo",
					data: "Param="+$("#mesInicio").val()+","+$("#mesFin").val()+"," + encodeURIComponent( $("#cuenta").val() ) + ","+ <%=cCentroContable%> + "," + encodeURIComponent( $("#subcuenta").val() ),
					success: function(resp){
						if ( !resp.ERROR ){
							var arrData = resp.DATA;
							$('#dt_Consulta').dataTable().fnAddData(arrData);
						}
						else{
							alert(resp.ERROR);
						}
					},
					 error: function(errorThrown) 
				      {
				         alert("error: " + errorThrown.ERROR);
				      }
					
				});
		}
		
	
</script>

</head>
<body id="dt_example">
<br/>
	 <form id="exportaConsulta" name="exportaConsulta" action="../reportes/exportaConsulta" method = "get" target="_self" >
		<div id="principal" class="container" style="width: 90%">
			<div id="consulta"  style=" visibility: visible;  width: 90% "  class="container" >
			
				<div class="card-header"> <h3> Consulta Saldos </h3> </div>
				<hr class="mt-3"/>
			
				<div class="row">						
		        	<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="selecciona" class="form-label"> Selecciona el periodo </label>												
					</div>				
					<div class="col-12 col-lg-3 col-md-4 col-sm-12 d-flex">
						<label for="selecciona" class="form-label"> Cuenta </label>
					</div>
					<div class="col-12 col-lg-3 col-md-4 col-sm-12 d-flex">
						<label for="selecciona" class="form-label"> Sub Cuenta </label>
					</div>
				</div>
				
				<div class="row">						
		        	<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
		        		<div class="row">
		        			<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
								<select name = "mesInicio" id = "mesInicio" class="form-select form-select-sm" onchange="cambia_Mes()">	
									<option value="1">Enero</option>
									<option value="2">Febrero</option>
									<option value="3">Marzo</option>
									<option value="4">Abril</option>
									<option value="5">Mayo</option>
									<option value="6">Junio</option>
									<option value="7">Julio</option>
									<option value="8">Agosto</option>
									<option value="9">Septiembre</option>
									<option value="10">Octubre</option>
									<option value="11">Noviembre</option>
									<option value="12">Diciembre</option>						
								</select>		
							</div>

		        			<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
								<select name = "mesFin" id = "mesFin" class="form-select form-select-sm" onchange="">	
									<option value="1">Enero</option>
									<option value="2">Febrero</option>
									<option value="3">Marzo</option>
									<option value="4">Abril</option>
									<option value="5">Mayo</option>
									<option value="6">Junio</option>
									<option value="7">Julio</option>
									<option value="8">Agosto</option>
									<option value="9">Septiembre</option>
									<option value="10">Octubre</option>
									<option value="11">Noviembre</option>
									<option value="12">Diciembre</option>						
								</select>
							</div>
						</div>
					</div>						
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<input type="text" name="cuenta" id="cuenta" class="form-control form-control-sm"/>
					</div>						
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<input type="text" name="subcuenta" id="subcuenta" class="form-control form-control-sm"/>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<input type="button" id="consulta" name="consulta" value="Consultar" class="btn btn-secondary btn-sm" onclick="creaTablaConsulta()"/>						
						<input type="button" id="exportar" name="exportar" value="Extraer" class="btn btn-secondary btn-sm" onclick="exportaXLS()" disabled/>
					</div>											
				</div>
				
				<br/>
				
				<div id="consulta" class="table-responsive">	      				
					<table id="dt_Consulta" class="table table-striped" >
						<thead>
							<tr>
								<th>Cuenta</th>
								<th>SubCuenta</th>
								<th>CC</th>
								<th>Saldo Inicial</th>
								<th>Cargos</th>
								<th>Abonos</th>
								<th>Saldo Final</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			
			<br/>
			
			<div id="saldos"  style=" visibility: visible;  width: 90% "  class="container">
				
				<h6> Suma Saldos - Cargos y Abonos </h6>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">						
		        	<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<label for="selecciona" class="form-label"> Inicial&nbsp; </label>																	
					</div>				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<label for="selecciona" class="form-label"> Cargos&nbsp; </label>													
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<label for="selecciona" class="form-label"> Abonos&nbsp; </label>													
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<label for="selecciona" class="form-label"> Final&nbsp; </label>													
					</div>
				</div>
				
				<div class="row d-flex justify-content-center">						
		        	<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
		        		<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							<input type="text" id="sIni" name="sIni" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
						</div>					
					</div>				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">			
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
							<input type="text" id="ca" name="ca" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
						</div>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>							
							<input type="text" id="ab" name="ab" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
						</div>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>							
							<input type="text" id="sFin" name="sFin" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
						</div>
					</div>
				</div>
						
			</div>
			
			<div style="display: none" >
				<input  type="hidden" id="cCC" name="cCC" value="" />
			</div>
					
		</div>
	</form>
	
	<div id="scDialog-form" title="Movimientos" style="width: 90%" class="container">
		<form id="exportaMovimientos" name="exportaMovimientos" action="../reportes/exportaMovimientos" method = "get" target="_self" >			
			<div id="subCon"  style=" visibility: visible;  width: 90% "  class="container" >
			
				<div class="card-header"> <h5> Consulta de Movimientos </h5> </div>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">						
		        	<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="scCuenta" class="form-label"> Cuenta </label>												
					</div>				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="scSubcuenta" class="form-label"> Sub Cuenta </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<label for="scCc" class="form-label"> CC </label>
					</div>
				</div>
				
				<div class="row d-flex justify-content-center">						
		        	<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">							
						<input type="text" id="scCuenta" name="scCuenta" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>					
					</div>				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">							
						<input type="text" id="scSubcuenta" name="scSubcuenta" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">							
						<input type="text" id="scCc" name="scCc" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
				</div>
				
				<div class="row d-flex justify-content-center">						
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="scSi" class="form-label"> Saldo Inicial </label>					
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="scCargos" class="form-label"> Cargos </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="scAbonos" class="form-label"> Abonos </label>														
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="scSf" class="form-label"> Saldo Final </label>												
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>				
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="scSi" name="scSi" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>					
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="scCargos" name="scCargos" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="scAbonos" name="scAbonos" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="scSf" name="scSf" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>

				<br/>
				
				<div id="consulta" class="table-responsive">	      	
					<table id="dt_movimientos" class="table table-striped" >
						<thead>
							<tr>
								<th>Tipo</th>
								<th>Folio</th>
								<th>Documento</th>
								<th>Numero</th>
								<th>Mes Aplicacion</th>
								<th>Cargos</th>
								<th>Abonos</th>
								<th>Descripcion</th>
								<th>Estatus</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>	
			
			<div style="display: none" >
				<input  type="hidden" id="movMini" name="movMini" value="" />
				<input  type="hidden" id="movMfin" name="movMfin" value="" />  
			</div>	
		</form>	
	</div>
	
	<div id="poliza-form" title="Poliza" class="container">
		<form id="exportaPoliza" name="exportaPoliza" action="../reportes/exportaPoliza" method = "get" target="_self">			
			<div id="poliza"  style=" visibility: visible;  width: 90% "  class="container" >			
				<div class="card-header"> <h5> Consulta de Poliza </h5> </div>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">						
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="pTipo" class="form-label"> Tipo </label>					
					</div>				
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<label for="pFolio" class="form-label"> Folio </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<label for="pCC" class="form-label"> CC </label>														
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="pCreacion" class="form-label"> Fecha Creacion </label>												
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="scSf" class="form-label"> Fecha Aplicacion </label>												
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>				
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="pTipo" name="pTipo" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>					
					</div>				
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">							
						<input type="text" id="pFolio" name="pFolio" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">							
						<input type="text" id="pCC" name="pCC" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="pCreacion" name="pCreacion" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="pAplicacion" name="pAplicacion" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">						
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="pDocumento" class="form-label"> Documento </label>					
					</div>				
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<label for="pNumero" class="form-label"> Numero </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<label for="pMes" class="form-label"> Mes </label>														
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="pCargos" class="form-label"> Cargos </label>												
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="pAbonos" class="form-label"> Abonos </label>												
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>				
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="pDocumento" name="pDocumento" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>					
					</div>				
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">							
						<input type="text" id="pNumero" name="pNumero" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">							
						<input type="text" id="pMes" name="pMes" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="pCargos" name="pCargos" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="pAbonos" name="pAbonos" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<br/>
				
				<div id="consulta" class="table-responsive">	      						
					<table id="dt_poliza" class="table table-striped" >
						<thead>
							<tr>
								<th>Cuenta</th>
								<th>Subcuenta</th>
								<th>Cargos</th>
								<th>Abonos</th>
							</tr>
						</thead>
					</table>
				</div>
					
				<br/>
				
				<div class="row d-flex justify-content-center">												
		        	<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">
						<label for="pDescripcion" class="form-label"> Descripcion Poliza </label>					
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">												
		        	<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">
						<textarea id="pDescripcion" name="pDescripcion" rows="3" cols="70" class="form-control form-control-sm" readonly></textarea>					
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">						
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rTD" class="form-label"> Documento </label>					
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rND" class="form-label"> Numero </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rTP" class="form-label"> T. Poliza </label>														
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<label for="rFP" class="form-label"> No. Pol. </label>												
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rRef" class="form-label"> CxP </label>												
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>				
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rTD" name="rTD" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>					
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rND" name="rND" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rTP" name="rTP" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">							
						<input type="text" id="rFP" name="rFP" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rRef" name="rRef" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">						
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rEst" class="form-label"> Estatus Documento </label>					
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rPC" class="form-label"> Poliza Cancelacion </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rFC" class="form-label"> F. Cancelacion </label>														
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">																	
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="rCheque" class="form-label"> No. Cheque </label>												
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
				
				<div class="row d-flex justify-content-center">			
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>				
		        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rEst" name="rEst" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>					
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rPC" name="rPC" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rFC" name="rFC" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">														
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
						<input type="text" id="rCheque" name="rCheque" style="text-align:right;" value="" class="form-control form-control-sm" readonly/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">											
					</div>	
				</div>
								
			</div>
			<div style="display: none" >
				<input  type="hidden" id="nFolio" name="nFolio" value="" />
				<input  type="hidden" id="cTipo" name="cTipo" value="" />  
				<input  type="hidden" id="cTipoDocumento" name="cTipoDocumento" value="" />
				<input  type="hidden" id="nFolioDocumento" name="nFolioDocumento" value="" />
				<input  type="hidden" id="nEstatus" name="nEstatus" value="" />   
			</div>	
		</form>	
	</div>
</body>
</html>



