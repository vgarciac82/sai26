<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
String action = request.getParameter("a");

int nFolioPago = Integer.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));

String cTipoPago = request.getParameter("d");

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

String cLogin = usuario.getLogin();
String cUR = usuario.getU_UR();
String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
String RFCUsuario = usuario.getuRFC();

String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));

String fielMsg = "";
boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null
		&& ((Boolean) session.getAttribute("EXPIRING_SOON"));
if (fielExpiringSoon) {
	fielMsg = (String) session.getAttribute("EXPIRING_MSG");
	session.removeAttribute("EXPIRING_SOON");
	session.removeAttribute("EXPIRING_MSG");
}

String tipoAutorizacion = ("VoBoPago".equals(action)
		? "VOBO"
		: ("AutPago".equals(action)
		? "AUT"
		: ("RVoBoPago".equals(action) ? "R_VOBO" : "RAutPago".equals(action) ? "R_AUT" : "")));
String numeroEmpleado = "";
numeroEmpleado = usuario.getNumeroEmpleado();
String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));

boolean mostrarResultado = !StringUtils.isBlank(result);
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
Caso c = cbl.findByFolioLike(cTipoPago, String.valueOf(nFolioPago));
ITree tree = cbl.getArbolCaso(c);
session.setAttribute(GestionInterface.ATT_TREE, tree);
session.setAttribute(GestionInterface.ATT_CASE, c);
%>

<!DOCTYPE html>
<html lang="es">
<head>


<title>Resumen de Pago</title>

<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">

<link href="css/ResumenFIEL.css" rel="stylesheet" type="text/css" />
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/2.3.4/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <style>
        h1 {
			font-size: 1.3em;
			font-weight: normal;
			line-height: 1.6em;
			color: #4E6CA3;
			border-bottom: 1px solid #B0BED9;
			clear: both;
			margin-top: 0px;
		}
    </style>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://cdn.datatables.net/2.3.4/js/dataTables.min.js"></script>
	<script src="https://cdn.datatables.net/2.3.4/js/dataTables.bootstrap5.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="js/funciones.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../FIEL/js/ResumenFIEL.js"></script>
	<script type="text/javascript" charset="utf-8">
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFC = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";
		var mostrarResultado = <%=mostrarResultado%>;
		var mensaje = "<%=msg%>";
		var tipoAutorizacion = "<%=tipoAutorizacion%>";
		var fielExpiringSoon = <%=fielExpiringSoon%>;
		var modalFiel;
		 
		$(document).ready(function() {
		
			$("#cTipoPago").val("<%=cTipoPago%>");
			
			$('#mnLogTbl tbody').html("<%=result%>");
			$('#msgWarning').html("<%=fielMsg%>");
			
			$("#loginFirma").val( cLogin );
			$("#ueFirma").val( cUR );
			$("#rfcFirma").val( RFC );
			$("#tipoAutorizacion").val( tipoAutorizacion );
			modalFiel = new bootstrap.Modal(document.getElementById('dlg-FIEL'), 'data-bs-backdrop');
			
			
			$.ajax({
				url : '../fortimax/documents',
				dataType : 'json',
				type : "GET",
				data : {
					"accion" : "send_tree"
				},
				async : true,
				success : function(objResp) {
					var nodos = objResp.nodos;
					for( var i in nodos){
						nodo = nodos[i];
						$("#bodyDoctos")
						.append("<tr>"
						      + "<td>" + nodo.nombreDocumento + "</td>"
						      + "<td><a style='color:black;' href=\"#\" onclick=\"muestraDocumento('" + nodo.fortimax + "')\">" + nodo.path + "</a></td>"
						      + "</tr>" );
					}
					creaTablaArchivos();
				},
				error : function(xhr, textStatus, errorThrown) {
					try{
						var obj = eval(xhr.responseText);
						var msg = obj.errCause;
						alert("No fue posible consultar los documentos debido al error: " + msg);
					}catch (e) {
						alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
					}
					
					$.unblockUI();
				}
			});
			
			init();
			initFiel();
			consultaTablas();
			
		});
		
		function muestraDocumento(fortimaxNode){
			openCenteredWindow("../filestore?select=" + fortimaxNode, "_blank", 800, 1024);
		}
		
		function openCenteredWindow(url, name, height, width, parms) {
			var left = Math.floor((screen.width - width) / 2);
			var top = Math.floor((screen.height - height) / 2);
			var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes,resizable=1";
			if (parms) {
				winParms += "," + parms;
			}
			var win = window.open(url, name, winParms);
			if (parseInt(navigator.appVersion) >= 4) {
				win.window.focus();
			}
			return win;
		}

		function regresar(){
			location.href = "../Generador/AutorizaGeneracionLayouts.jsp?TYPE=" + tipoAutorizacion ;		
		}
		 
		function reloadTabla(){
		
			oTableEPS = $("#tblEPS").DataTable({
				bPaginate: true,
       			//bLengthChange: true,
       			bFilter: true,
       			bSort: false,
       			bInfo: false,
				sScrollX: "100%",
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				aaSorting: [[ 0, "asc" ]]
			});
			
			
			oTableFacturas = $("#tblFacturas").DataTable({
				bPaginate: true,
       			bLengthChange: true,
       			bFilter: true,
       			bSort: false,
       			bInfo: false,
				sScrollX: "100%",
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				aaSorting: [[ 0, "asc" ]]
			});
			
		}
		
		function init(){
			$("#cTipoPago").val("<%=cTipoPago%>");
			 queryFormPost("datosResumenPago", {async: false});
		}
		
		function consultaTablas() {

		    oTableEPS = $("#tblEPS").DataTable({
		        scrollCollapse: true,
		        info: false,
		        scrollX: true,
		        autoWidth: false,
		        retrieve: true,        
		        destroy: true,
		        pagingType: "full_numbers",

		        language: {           
		            processing: "Procesando...",
		            lengthMenu: "Mostrar _MENU_ registros",
		            zeroRecords: "No hay registros a mostrar",
		            emptyTable: "No hay datos en la tabla",
		            loadingRecords: "Cargando...",
		            info: "Registros _START_ al _END_ de _TOTAL_",
		            infoEmpty: "Registro 0 al 0 de 0",
		            infoFiltered: "(filtrado de _MAX_ registros)",
		            thousands: ",",
		            search: "Buscar:",
		            paginate: {
		                first:    "Primero",
		                previous: "Ant.",
		                next:     "Sigte.",
		                last:     "Último"
		            }
		        },

		        serverSide: true,      

		        ajax: {               
		            url: window.location.protocol
		                + "//" + window.location.host
		                + "/" + window.location.pathname.split("/")[1]
		                + "/crud?rt=nt&ql=fnResumenPagosEP('"
		                + $("#cTipoPago").val() + "',"
		                + $("#nFolioPago").val() + ")",
		            type: "POST"       
		        },

		        order: [[ 0, "asc" ]],   

		        columns: [               
		            { data: "row_number",    name: "row_number" },
		            { data: "EP",         name: "EP" },
		            { data: "totalEP",    name: "totalEP" },

		            { data: "Enero",      name: "Enero" },
		            { data: "Febrero",    name: "Febrero" },
		            { data: "Marzo",      name: "Marzo" },
		            { data: "Abril",      name: "Abril" },
		            { data: "Mayo",       name: "Mayo" },
		            { data: "Junio",      name: "Junio" },
		            { data: "Julio",      name: "Julio" },
		            { data: "Agosto",     name: "Agosto" },
		            { data: "Septiembre", name: "Septiembre" },
		            { data: "Octubre",    name: "Octubre" },
		            { data: "Noviembre",  name: "Noviembre" },
		            { data: "Diciembre",  name: "Diciembre" }
		        ]
		    });
		    
		    
		    oTableFacturas = $("#tblFacturas").DataTable({
		        scrollCollapse: false,
		        info: false,
		        scrollX: true,
		        autoWidth: true,
		        retrieve: true,
		        destroy: true,
		        pagingType: "full_numbers",

		        language: {
		            processing: "Procesando...",
		            lengthMenu: "Mostrar _MENU_ registros",
		            zeroRecords: "No hay registros a mostrar",
		            emptyTable: "No hay datos en la tabla",
		            loadingRecords: "Cargando...",
		            info: "Registros _START_ al _END_ de _TOTAL_",
		            infoEmpty: "Registro 0 al 0 de 0",
		            infoFiltered: "(filtrado de _MAX_ registros)",
		            thousands: ",",
		            search: "Buscar:",
		            paginate: {
		                first:    "Primero",
		                previous: "Ant.",
		                next:     "Sigte.",
		                last:     "Último"
		            }
		        },

		        serverSide: true,

		        ajax: {
		            url: window.location.protocol
		                + "//" + window.location.host
		                + "/" + window.location.pathname.split("/")[1]
		                + "/crud?rt=nt&ql=fnResumenPagosFacturas('"
		                + $("#cTipoPago").val() + "',"
		                + $("#nFolioPago").val() + ")",
		            type: "POST"    
		        },

		        order: [[0, "asc"]],

		        columns: [
		            { data: "row_number",        name: "row_number" },
		            { data: "cRFCFactura",    name: "cRFCFactura" },
		            { data: "cfactura",       name: "cfactura" },
		            { data: "mimporteconiva", name: "mimporteconiva" },
		            { data: "mImporteBruto",  name: "mImporteBruto" },
		            { data: "mimporteiva",    name: "mimporteiva" }
		        ]
		    });

		}

		
		function creaTablaArchivos(){
			$("#tblFiles").DataTable({
				bPaginate: false,
				sScrollX: "100%",
       			bFilter: false,
       			bSort: false,
       			bInfo: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}
			});
		}

		function rechazaPago(){
		
			var msg = "Est\u00e1 a punto de cancelar el pago con folio: " + $("#nFolioPago").val();
			
			if( confirm(msg) ) {
			
				getNextSequenceVal({seqName: "DEV" + "<%=cCentroContable%>",
				async : false,
				callback : setSequenceVal
			});
			$
					.ajax({
						url : './cierrePresupuestal.jsp',
						type : 'post',
						dataType : 'json',
						data : {
							tipo : 'cancelaDevengado',
							tipoDocumento : $("#cTipoPago").val() + "/",
							nFolio : $("#nFolioPago").val() + "/",
							nFolioDev : $("#nFolioDev").val() + "/",
							autorizadoPorFiel : "true"
						},
						success : function(data) {
							if (data.sinSesion == "sinSesion") {
								location.href = "../index.jsp";
							} else if (data.estatus == "guardado") {
								alert("Documentos Cancelado Correctamente");
								location.href = "../Generador/AutorizaGeneracionLayouts.jsp?TYPE="
										+ tipoAutorizacion;
							} else if (data.estatus == "sinInformacion") {
								alert("Error Al Buscar Documento");
							} else {
								alert("No Guardado");
							}
						}
					});
		}
	}

	function setSequenceVal(seqValue) {
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioDev").val(seqValue);
	}

	function aceptarDlg() {
	    var msgValidaciones = validaCamposCompletos();
	    modalFielDlg.hide();
	    if (msgValidaciones && msgValidaciones !== "") {
	        // Mensaje de validación con SweetAlert2
	        Swal.fire({
	            icon: 'warning',
	            title: 'Validación',
	            text: msgValidaciones,
	            confirmButtonText: 'Aceptar'
	        });
	        return;
	    }

	    $("#nFolios").val($("#nFolioPago").val());
	    var msg = "Está a punto de aceptar el proceso de los pagos con folio: " + $("#nFolios").val();

	    Swal.fire({
	        icon: 'question',
	        title: 'Confirmar operación',
	        html: msg, // uso html por si luego quieres resaltar el folio
	        showCancelButton: true,
	        confirmButtonText: 'Aceptar',
	        cancelButtonText: 'Cancelar',
	        reverseButtons: true
	    }).then(function (result) {
	        if (!result.isConfirmed) {
	            return;
	        }

	        $("#tipoPagoSeleccionado").val($("#cTipoPago").val());

	        // Cierra el modal de FIEL
	        if (typeof modalFielDlg !== "undefined" && modalFielDlg) {
	        	modalFielDlg.hide();
	        }

	        if ("OPERAJENAS" === $("#cTipoPago").val()) {
	            $("#folder").val("Solicitud Firmada");
	        }
	       

	        $.blockUI({
	            message: "Espere ..."
	        });
	        $("#nFolios").val($("#nFolioPago").val());
	        $("#autorizaLayouts").submit();
	    });
	}

 
</script>
</head>

<body  onkeydown="return(desactivaBackspace(event))">
    <form action="" name="formCampos" id="formCampos">
        <div id="container" class="container-fluid py-3">

            <input type="hidden" id="nenviadosicop" name="nenviadosicop" value="" />
            <input type="hidden" id="cEstatus" name="cEstatus" value="" />

            <!-- Link regresar -->
            <div class="row mb-3">
                <div class="col text-end">
                    <a href="#" onclick="regresar();return false;">Ver listado de pendientes</a>
                </div>
            </div>

            <!-- Resumen del Pago -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Resumen del Pago</h5>
                </div>
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="tipoPago" class="form-label">Tipo de Pago</label>
                            <input id="tipoPago" name="tipoPago" value="" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="nFolioPago" class="form-label">Folio de Pago</label>
                            <input id="nFolioPago" name="nFolioPago" value="<%=nFolioPago%>" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="cNoContrato" class="form-label">No Contrato</label>
                            <input id="cNoContrato" name="cNoContrato" value="" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-6">
                            <label for="caNoContrarrecibo" class="form-label">Contra Recibo</label>
                            <input id="caNoContrarrecibo" name="caNoContrarrecibo" value="" class="form-control" />
                        </div>
                    </div>

                    <div class="row g-3 mt-1">
                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mImporteFacturas" class="form-label">Importe en Facturas</label>
                            <input id="mImporteFacturas" name="mImporteFacturas" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mImporteBruto" class="form-label">Importe Bruto</label>
                            <input id="mImporteBruto" name="mImporteBruto" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mImporteOtrosImpuestos" class="form-label">Otros Impuestos</label>
                            <input id="mImporteOtrosImpuestos" name="mImporteOtrosImpuestos" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoSanciones" class="form-label">Monto Sanciones</label>
                            <input id="mMontoSanciones" name="mMontoSanciones" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoDevoluciones" class="form-label">Monto Devoluciones</label>
                            <input id="mMontoDevoluciones" name="mMontoDevoluciones" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoSubtotal" class="form-label">Importe Subtotal</label>
                            <input id="mMontoSubtotal" name="mMontoSubtotal" value="$ 0.00" class="form-control" />
                        </div>
                    </div>

                    <div class="row g-3 mt-1">
                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoAmortizaAnticipo" class="form-label">Amortización anticipo</label>
                            <input id="mMontoAmortizaAnticipo" name="mMontoAmortizaAnticipo" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoIVA" class="form-label">Monto IVA</label>
                            <input id="mMontoIVA" name="mMontoIVA" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoRetencion" class="form-label">Monto de Retenciones</label>
                            <input id="mMontoRetencion" name="mMontoRetencion" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mMontoPenalizacion" class="form-label">Monto de Penalización</label>
                            <input id="mMontoPenalizacion" name="mMontoPenalizacion" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mImporteNeto" class="form-label">Importe Neto</label>
                            <input id="mImporteNeto" name="mImporteNeto" value="$ 0.00" class="form-control" />
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-4">
                            <label for="mImporteEjercer" class="form-label">Importe a Ejercer</label>
                            <input id="mImporteEjercer" name="mImporteEjercer" value="$ 0.00" class="form-control" />
                        </div>
                    </div>

                    <div class="row g-3 mt-2">
                        <div class="col-12">
                            <label for="cConceptoPago" class="form-label">Concepto</label>
                            <textarea
                                id="cConceptoPago"
                                name="cConceptoPago"
                                rows="4"
                                class="form-control"
                                style="resize: vertical;"></textarea>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Documentos del pago -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Documentos del pago</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table id="tblFiles" class="table table-striped table-bordered table-hover table-sm align-middle nowrap">
                            <thead>
                                <tr class="text-center">
                                    <th>Documento</th>
                                    <th>Consultar</th>
                                </tr>
                            </thead>
                            <tbody id="bodyDoctos">
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Facturas del pago</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table id="tblFacturas"
                               class="table table-striped table-bordered table-hover table-sm align-middle nowrap"
                               style="width:100%;">
                            <thead>
                                <tr class="text-center">
                                    <th style="width:5%;">#</th>
                                    <th style="width:20%;">RFC</th>
                                    <th style="width:45%;">Factura</th>
                                    <th style="width:10%;">Importe con IVA</th>
                                    <th style="width:10%;">Importe sin IVA</th>
                                    <th style="width:10%;">Importe IVA</th>
                                </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Estructuras presupuestales del pago -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Estructuras presupuestales del pago</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table id="tblEPS"
                               class="table table-striped table-bordered table-hover table-sm align-middle nowrap"
                               style="width:100%;">
                            <thead>
                                <tr class="text-center">
                                    <th>#</th>
                                    <th>Estructura Programática</th>
                                    <th>Total EP</th>
                                    <th>Enero</th>
                                    <th>Febrero</th>
                                    <th>Marzo</th>
                                    <th>Abril</th>
                                    <th>Mayo</th>
                                    <th>Junio</th>
                                    <th>Julio</th>
                                    <th>Agosto</th>
                                    <th>Sept</th>
                                    <th>Octubre</th>
                                    <th>Nov</th>
                                    <th>Dic</th>
                                </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>

            <input type="hidden" name="cTipoPago" id="cTipoPago" value="" />
            <input type="hidden" name="nFolioDev" id="nFolioDev" value="" />

        </div>
    </form>

    <jsp:include page="../FIEL/OperacionesFIEL.jsp"></jsp:include>
</body>
</html>

</html>
