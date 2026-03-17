<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%
	String cCentroContable = "";
	String algo = "";
 	
 	String cAnioFiscal = "";
	String DATE_FORMAT = "yyyy/MM/dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
 	
 	 	
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
<title>Cat&aacute;logo Cuentas SIIWEB</title>

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">

var oTable1;
						
$(document).ready(function() {
	$("#consulta").button();
	$("#agregar").button();
	setFechas();
	
		oTable1 = $("#dt_SIIWEB").dataTable(
					{
						bAutoWidth : false,
						oLanguage: {
									sZeroRecords: "No hay registros a mostrar",
									sEmptyTable: "No hay datos en la tabla",
									sLoadingRecords: "Cargando...",
									sSearch: "Buscar:",
									sInfo: "Número de Registros --> _TOTAL_ ",
									sInfoEmpty: "Sin Registros"
								   },
						bPaginate 	: false,
						bFilter 	: true,
						bSort 		: true,    
						sScrollX 	: "100%",
						sScrollY 	: "500",
						bRetrive 	: true,
						bDestroy 	: true,
						bJQueryUI 	: true,
						bServerSide : true,
						aaSorting: [[ 0, "asc" ]] ,
						sAjaxSource : window.location.protocol + "//"+ window.location.host + "/"+ window.location.pathname.split("/")[1]+"/crud?rt=t&ql=tSIIWEBCuentasVista",
						aoColumns: [{ sName: "nFolio" 	    , sClass: "center"	},
									{ sName: "cInstitucion"	, sClass: "center"	},
									{ sName: "cNombreInst"	},
									{ sName: "cCLABE"		},
									{ sName: "cTipoAdm"		, sClass: "center"  },
									{ sName: "cTipoAct"		, sClass: "center"  },
									{ sName: "cInstrumento"	, sClass: "center"	},
									{ sName: "cMoneda" 		, sClass: "center"	},
									{ sName: "cNaturaleza"	, sClass: "center"	},
									{ sName: "cResidencia"	, sClass: "center"	},
									{ sName: "nObjeto" 		, sClass: "center"	},
									{ sName: "nFormaAdq"	, sClass: "center"	},
									{ sName: "cTitular_1"	, sClass: "ancho"	},
									{ sName: "cTitular_2"	, sClass: "ancho"	},
									{ sName: "nClaveSII"	, sClass: "center"	},
									{ sName: "fApertura"	, sClass: "center"	},
									{ sName: "cTipoMOv"		, sClass: "center"	},
									{ sName: "fMovimiento"	, sClass: "center"	},
									{ sName: "cOficio"		, sClass: "center"	}]
						});		
				
				$("#dt_SIIWEB tbody").click(function(event) { 
		        $(oTable1.fnSettings().aoData).each(function (){ 
		            $(this.nTr).removeClass('row_selected');
		        	}); 
		        $(event.target.parentNode).addClass('row_selected'); 
		    	}); 
				
				setDblClck();
				
	});
	
	function setFechas(){
		$("#fMov").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});		
		
		$("#fApA").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});		
		
		$("#fMovA").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});		
	}
	
	$(function() {
	$( "#scDialog-form" ).dialog({
			autoOpen: false,
			height: 465,
			width: 800,
			modal: true,
			buttons: {
						"Actualizar": function() { 
								 $("#nFolio"		).val( $("#nfol"	).val() );
								 $("#cTitular1"		).val( $("#cT1"		).val() );
								 $("#cTitular2"		).val( $("#cT2"		).val() );
								 $("#cOficio"		).val( $("#cOf"		).val() );
								 $("#fMovimiento"	).val( $("#fMov"	).val() );
								 $("#cTipoMov"		).val( $("#cTM"		).val() );   
								 Swal.fire({
                                     title: '¿Desea continuar?',
                                     text: "Se modificará la inforamción.",
                                     icon: 'warning',
                                     showCancelButton: true,
                                     confirmButtonColor: '#288BA8',
                                     cancelButtonColor: '#e6e6e6',
                                     confirmButtonText: 'Aceptar',
                                     cancelButtonText: 'Cancelar'
                                   }).then((result) => {
                                     if (result.isConfirmed) {
                                    	 queryFormPost("actualizaCtaSIIWEB"
								 				  ,	{async:false, callback:function()
								 				  		{Swal.fire({ icon: "success",
								 									 text: "Proceso terminado " });}
								 				  	});
                                     } else if (result.dismiss === Swal.DismissReason.cancel) {
                                     	return;
                                     }
                                   })
						},
						"Cerrar"  : function() { $( this ).dialog( "close" ); }
					 },
			close: function() {
				oTable1.fnDraw();
			}
		});
	});
	
	
	$(function() {
	$( "#newDialog-form" ).dialog({
			autoOpen: false,
			height: 465,
			width: 800,
			modal: true,
			buttons: {
						"Aceptar": function() {
								 	$("#cInstitucionA"	).val( $("#cInstA"	).val() );
									$("#cNombreAlta"	).val( $("#cNombreA").val().toUpperCase() );
									$("#cCuentaAlta"	).val( $("#cCuentaA").val() );
									$("#cTipoAdm"		).val( $("#cAdmA"	).val() );
									$("#cTipoAct"		).val( $("#cActA"	).val().toUpperCase() );
									$("#cInstrumento"	).val( $("#cInstrA"	).val().toUpperCase() );
									$("#cMoneda"		).val( $("#cMonA"	).val().toUpperCase() );
									$("#cNaturaleza"	).val( $("#cNatA"	).val().toUpperCase() );
									$("#cResidencia"	).val( $("#cResidA"	).val().toUpperCase() );
									$("#nObjeto"		).val( $("#nObjA"	).val() );
									$("#cFormAdq"		).val( $("#nAdqA"	).val() );
									$("#cTitular1A"		).val( $("#cT1A"	).val().toUpperCase() );
									$("#cTitular2A"		).val( $("#cT2A"	).val().toUpperCase() );
									$("#nClaveSII"		).val( $("#cCSA"	).val() );
									$("#fApertura"		).val( $("#fApA"	).val() );
									$("#cTipoMovA"		).val( $("#cTMA"	).val().toUpperCase() );
									$("#fMovimientoA"	).val( $("#fMovA"	).val() );	
									$("#cOficioA"		).val( $("#cOfA"	).val().toUpperCase() );
									Swal.fire({
	                                     title: '¿Desea continuar?',
	                                     text: "Se modificará la inforamción.",
	                                     icon: 'warning',
	                                     showCancelButton: true,
	                                     confirmButtonColor: '#288BA8',
	                                     cancelButtonColor: '#e6e6e6',
	                                     confirmButtonText: 'Aceptar',
	                                     cancelButtonText: 'Cancelar'
	                                   }).then((result) => {
	                                     if (result.isConfirmed) {
	                                    	 queryFormPost("nuevaCtaSIIWEB"
									 				  ,	{async:false, callback:function()
									 				  		{Swal.fire({ icon: "success",
							 									 		 text: "Proceso terminado " });}
									 				  	});
	                                     } else if (result.dismiss === Swal.DismissReason.cancel) {
	                                     	return;
	                                     }
	                                   })
								 $( this ).dialog( "close" );
						},
						"Cerrar"  : function() { $( this ).dialog( "close" ); }
					 },
			close: function() {
				oTable1.fnDraw();
			}
		});
	});
	
	
	function exportaXLS(){
			 $( "#mostrarCuentas" ).submit();
		}
		
	function nuevaCuenta(){
				$("#cInstA"	 ).val("");
				$("#cNombreA").val("");
				$("#cCuentaA").val("");
				$("#cAdmA"	).val("");
				$("#cActA"	).val("");
				$("#cInstrA").val("");
				$("#cMonA"	).val("");
				$("#cNatA"	).val("");
				$("#cResidA").val("");
				$("#nObjA"	).val("");
				$("#nAdqA"	).val("");
				$("#cT1A"	).val("");
				$("#cT2A"	).val("");
				$("#cCSA"	).val("");
				$("#fApA"	).val("");
				$("#cTMA"	).val("");
				$("#fMovA"	).val("");	
				$("#cOfA"	).val("");
				
			 $( "#newDialog-form" ).dialog( "open" );
		}
	
	 function setDblClck() {
	 			 		
            $("#dt_SIIWEB tbody").dblclick(function(evt) {

                var aPos = oTable1.fnGetPosition(evt.target.parentNode);

                if (aPos instanceof Array)
                    currIndex = aPos[0];
                else
                    currIndex = aPos;

               var arr = oTable1.fnGetData()[currIndex];
                                
                $("#nfol"	).val( arr[0]);
                $("#cInst"	).val( arr[1]);
                $("#cNombre").val( arr[2]);
                $("#cCuenta").val( arr[3]);
                $("#cAdm"	).val( arr[4]);
                $("#cAct"	).val( arr[5]);
                $("#cInstr"	).val( arr[6]);
                $("#cMon"	).val( arr[7]);
                $("#cNat"	).val( arr[8]);
                $("#cResid"	).val( arr[9]);
                $("#nObj"	).val( arr[10]);
                $("#nAdq"	).val( arr[11]);
                $("#cT1"	).val( arr[12]);
                $("#cT2"	).val( arr[13]);
                $("#cCS"	).val( arr[14]);
                $("#fAp"	).val( arr[15]);
                $("#cTM"	).val( arr[16]);
                $("#fMov"	).val( arr[17]);
                $("#cOf"	).val( arr[18]);
                            	
            	$( "#scDialog-form" ).dialog( "open" );
            });
        }
	 
	 function upperCase(e) {		
			e.value = e.value.toUpperCase();
		}
	
</script>

</head>
<body id="dt_example">
<br/>
	 <form id="mostrarCuentas" name="mostrarCuentas" action="../reportes/mostrarCuentasSIIWEB" method = "get" target="_self" >
		<div id="principal" class="container" style="width: 90%">
				
			<div class="card-header"> <h3> Cuentas SIIWEB </h3> </div>
			<hr class="mt-3"/>
						
			<div id="consultar" style="visibility: visible; width: 90%" class="container" >		
				<div class="row d-flex justify-content-center">
					<div class="col-4">									
						<label for="listado" class="form-label">Exportar listado:</label>																						
						<input type="button" id="consulta" name="consulta" value="Consultar" class="btn btn-secondary btn-sm" onclick="exportaXLS()"/>				
					</div>
				
					<div class="col-4">									
						<label for="listado" class="form-label">Nueva Cuenta:</label>
						<input type="button" id="agregar" name="agregar" value="Agregar" class="btn btn-secondary btn-sm" onclick="nuevaCuenta()"/>				
					</div>
				</div>					
								
				<br/>
				
				<div id="SIIWEB" class="table-responsive">
					<table id="dt_SIIWEB" class="table table-striped"  >
						<thead>
							<tr>
								<th>Folio		</th>
								<th>Institucion	</th>
								<th>Nombre		</th>
								<th>Cuenta		</th>
								<th>TipoAdm		</th>
								<th>TipoAct		</th>
								<th>Instrumento	</th>
								<th>Moneda		</th>
								<th>Naturaleza	</th>
								<th>Residencia	</th>
								<th>Objeto		</th>
								<th>FormaAdq	</th>
								<th>Titular1	</th>
								<th>Titular2	</th>
								<th>ClaveSII	</th>
								<th>Apretura	</th>
								<th>TipoMov		</th>
								<th>Movimiento	</th>
								<th>Oficio		</th>
							</tr>
						</thead>
					</table>		
				</div>
			</div>
				
		</div>
	</form>
	
	<div id="scDialog-form" title="Edicion de Cuentas SIIWEB" class="container">
		<form id="actualizaCuentaSW" name="actualizaCuentaSW" action="" method = "get" target="_self" >			
			<div id="datos" style="visibility: visible;  width: 90%" class="container">
			
				<div class="card-header"> <h2> Datos NO Editables </h2> </div>
				<hr class="mt-3"/>
									
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">																
								<label for="nfol" class="form-label">Folio:&nbsp; </label>																												
							</div>																																	
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cCS" class="form-label">ClaveSSI:&nbsp; </label>									
							</div>							
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cMon" class="form-label">Moneda:&nbsp; </label>
							</div>							
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">																									
								<input type="text" name="nfol" id="nfol" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>
							</div>											
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>																	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cCS" id="cCS" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>										
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cMon" id="cMon" class="form-control form-control-sm" style="width: 5em !important;flex: none;" readonly/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cInst" class="form-label">Institucion:&nbsp; </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cAct" class="form-label">TipoAct:&nbsp; </label>																
							</div>							
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">							
								<label for="cNat" class="form-label">Naturaleza:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cInst" id="cInst" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>											
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="cAct" id="cAct" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<input type="text" name="cNat" id="cNat" class="form-control form-control-sm" style="width: 5em !important;flex: none;" readonly/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cNombre" class="form-label">Nombre:&nbsp; </label>
							</div>	
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cInstr" class="form-label">Instrumento:&nbsp; </label>																
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">							
								<label for="cResid" class="form-label">Residencia:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cNombre" id="cNombre" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>											
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="cInstr" id="cInstr" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<input type="text" name="cResid" id="cResid" class="form-control form-control-sm" style="width: 5em !important;flex: none;" readonly/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cCuenta" class="form-label">Cuenta:&nbsp; </label>
							</div>				
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="nAdq" class="form-label">FormaAdq:&nbsp; </label>																
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">							
								<label for="nObj" class="form-label">Objeto:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">										
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cCuenta" id="cCuenta" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>
							</div>					
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="nAdq" id="nAdq" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<input type="text" name="nObj" id="nObj" class="form-control form-control-sm" style="width: 5em !important;flex: none;" readonly/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cAdm" class="form-label">TipoAdm:&nbsp; </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="fAp" class="form-label">Apretura:&nbsp; </label>																
							</div>								
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cAdm" id="cAdm" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>
							</div>								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>															
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="fAp" id="fAp" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>								
							</div>								
						</div>
					</div>
				</div>
				
				<br/>
			
				<div class="card-header"> <h2> Datos Editables </h2> </div>
				<hr class="mt-3"/>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																
								<label for="cT1" class="form-label">Titular 1:&nbsp; </label>																												
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<label for="cOf" class="form-label">Oficio:&nbsp; </label>									
							</div>						
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">								
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																									
								<input type="text" name="cT1" id="cT1" class="form-control form-control-sm" style="width: 20em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cOf" id="cOf" class="form-control form-control-sm" style="width: 8em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>						
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																
								<label for="cT2" class="form-label">Titular 2:&nbsp; </label>																												
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<label for="fMov" class="form-label">Movimiento:&nbsp; </label>									
							</div>						
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">								
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																									
								<input type="text" name="cT2" id="cT2" class="form-control form-control-sm" style="width: 20em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="fMov" id="fMov" class="form-control form-control-sm" style="width: 8em !important;flex: none;"/>
							</div>						
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																
								<label for="cTM" class="form-label">TipoMov:&nbsp; </label>																												
							</div>	
						</div>																												
					</div>
				</div>		
				<div class="form-group">
					<div class="row">
						<div class="input-group">								
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																									
								<input type="text" name="cTM" id="cTM" class="form-control form-control-sm" style="width: 5em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>	
						</div>																												
					</div>
				</div>		
			</div>			

			<input	type="hidden"	id="nFolio"			name="nFolio"		value="" />
			<input	type="hidden"	id="cTitular1"		name="cTitular1"	value="" />
			<input	type="hidden"	id="cTitular2"		name="cTitular2"	value="" />
			<input	type="hidden"	id="cOficio"		name="cOficio"		value="" />
			<input	type="hidden"	id="fMovimiento"	name="fMovimiento"	value="" />
			<input	type="hidden"	id="cTipoMov"		name="cTipoMov"		value="" />
			
		</form>	
	</div>
	
	<div id="newDialog-form" title="Nueva Cuenta SIIWEB">
		<form id="insertaCuentaSW" name="insertsCuentaSW" action="" method = "get" target="_self" >			
			<div id="datos"  style=" visibility: visible;  width: 95% "  class="container" >
			
				<div class="card-header"> <h2> Datos NO Editables </h2> </div>
				<hr class="mt-3"/>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">																
								<label for="nfolA" class="form-label">Folio:&nbsp; </label>																												
							</div>	
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cCSA" class="form-label">ClaveSSI:&nbsp; </label>									
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cMonA" class="form-label">Moneda:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">																									
								<input type="text" name="nfolA" id="nfolA" class="form-control form-control-sm" style="width: 8em !important;flex: none;"/>
							</div>				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cCSA" id="cCSA" class="form-control form-control-sm" style="width: 8em !important;flex: none;"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>					
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cMonA" id="cMonA" class="form-control form-control-sm" style="width: 5em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cInstA" class="form-label">Institucion:&nbsp; </label>
							</div>			
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cActA" class="form-label">TipoAct:&nbsp; </label>																
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">							
								<label for="cNatA" class="form-label">Naturaleza:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cInstA" id="cInstA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" maxlength="6"/>
							</div>								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>															
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="cActA" id="cActA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" onkeyup="upperCase(this);" maxlength="10"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<input type="text" name="cNatA" id="cNatA" class="form-control form-control-sm" style="width: 5em !important;flex: none;" onkeyup="upperCase(this);" maxlength="1"/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cNombreA" class="form-label">Nombre:&nbsp; </label>
							</div>				
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cInstrA" class="form-label">Instrumento:&nbsp; </label>																
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">							
								<label for="cResidA" class="form-label">Residencia:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cNombreA" id="cNombreA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" onkeyup="upperCase(this);" maxlength="25"/>
							</div>								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>															
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="cInstrA" id="cInstrA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" onkeyup="upperCase(this);"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<input type="text" name="cResidA" id="cResidA" class="form-control form-control-sm" style="width: 5em !important;flex: none;" onkeyup="upperCase(this);" maxlength="1"/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cCuentaA" class="form-label">Cuenta:&nbsp; </label>
							</div>				
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="nAdqA" class="form-label">FormaAdq:&nbsp; </label>																
							</div>							
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">							
								<label for="nObjA" class="form-label">Objeto:&nbsp; </label>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cCuentaA" id="cCuentaA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" maxlength="25"/>
							</div>								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>			
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="nAdqA" id="nAdqA" class="form-control form-control-sm" style="width: 8em !important;flex: none;"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<input type="text" name="nObjA" id="nObjA" class="form-control form-control-sm" style="width: 5em !important;flex: none;"/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="cAdmA" class="form-label">TipoAdm:&nbsp; </label>
							</div>							
							<div class="col-12 col-lg-4 col-md-4 col-sm-12">
								<label for="fApA" class="form-label">Apretura:&nbsp; </label>																
							</div>								
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cAdmA" id="cAdmA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" onkeyup="upperCase(this);" maxlength="20"/>
							</div>								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>															
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" name="fApA" id="fApA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" readonly/>								
							</div>								
						</div>
					</div>
				</div>
				
				<br/>
			
				<div class="card-header"> <h2> Datos Editables </h2> </div>
				<hr class="mt-3"/>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																
								<label for="cT1A" class="form-label">Titular 1:&nbsp; </label>																												
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<label for="cOfA" class="form-label">Oficio:&nbsp; </label>									
							</div>												
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">							
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																									
								<input type="text" name="cT1A" id="cT1A" class="form-control form-control-sm" style="width: 20em !important;flex: none;" onkeyup="upperCase(this);" maxlength="250"/>
							</div>				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="cOfA" id="cOfA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" onkeyup="upperCase(this);" maxlength="20"/>
							</div>						
						</div>
					</div>
				</div>
									
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																
								<label for="cT2A" class="form-label">Titular 2:&nbsp; </label>																												
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<label for="fMovA" class="form-label">Movimiento:&nbsp; </label>									
							</div>						
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																									
								<input type="text" name="cT2A" id="cT2A" class="form-control form-control-sm" style="width: 20em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>																							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<input type="text" name="fMovA" id="fMovA" class="form-control form-control-sm" style="width: 8em !important;flex: none;" maxlength="1"/>
							</div>						
						</div>
					</div>
				</div>
				
				<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">																
								<label for="cTMA" class="form-label">TipoMov:&nbsp; </label>																												
							</div>	
						</div>																												
					</div>
				</div>
				<div class="form-group">
					<div class="row">
						<div class="input-group">							
							<div class="col-12 col-lg-6 col-md-6 col-sm-12">																									
								<input type="text" name="cTMA" id="cTMA" class="form-control form-control-sm" style="width: 5em !important;flex: none;" onkeyup="upperCase(this);"/>
							</div>	
						</div>																												
					</div>
				</div>
			</div>	
			
			<input	type="hidden"	id="cInstitucionA"	name="cInstitucionA" value="" />
			<input	type="hidden"	id="cNombreAlta"	name="cNombreAlta"	value="" />
			<input	type="hidden"	id="cCuentaAlta"	name="cCuentaAlta"	value="" />
			<input	type="hidden"	id="cTipoAdm"		name="cTipoAdm"		value="" />
			<input	type="hidden"	id="cTipoAct"		name="cTipoAct"		value="" />
			<input	type="hidden"	id="cInstrumento"	name="cInstrumento"	value="" />
			<input	type="hidden"	id="cMoneda"		name="cMoneda"		value="" />
			<input	type="hidden"	id="cNaturaleza"	name="cNaturaleza"	value="" />
			<input	type="hidden"	id="cResidencia"	name="cResidencia"	value="" />
			<input	type="hidden"	id="nObjeto"		name="nObjeto"		value="" />
			<input	type="hidden"	id="cFormAdq"		name="cFormAdq"		value="" />
			<input	type="hidden"	id="cTitular1A"		name="cTitular1A"	value="" />
			<input	type="hidden"	id="cTitular2A"		name="cTitular2A"	value="" />
			<input	type="hidden"	id="nClaveSII"		name="nClaveSII"	value="" />
			<input	type="hidden"	id="fApertura"		name="fApertura"	value="" />
			<input	type="hidden"	id="cTipoMovA"		name="cTipoMovA"	value="" />
			<input	type="hidden"	id="fMovimientoA"	name="fMovimientoA"	value="" />
			<input	type="hidden"	id="cOficioA"		name="cOficioA"		value="" />
			
		</form>	
	</div>
</body>
</html>