<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	String uLogin = usuario.getLogin();
	String folio = c.getFolio();
	Calendar c1 = Calendar.getInstance();
	String today = sdf.format(c1.getTime());
	String msg;
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}

	int idCaso = c.getIdCaso();
	Map rol = usuario.getRoles();
	Iterator it1 = rol.entrySet().iterator();
	String roles = "";
	while (it1.hasNext()) {
		Map.Entry r = (Map.Entry) it1.next();
		roles += r.getKey().toString() + ",";
	}
	if (roles.length() > 0) {
		roles = roles.substring(0, roles.length() - 1);
	}
%>

<!DOCTYPE html>
<html>
  <head>
  
    
    <title>Alta Cta Bancaria Proveedores</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css"	href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css"	href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
	<style type="text/css">
		.validateTips {
			border: 1px solid transparent;
			padding: 0.3em;
		}
		
	</style>
	
 	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	
	<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap-dataTables/datatables.css"/>

	<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../Bootstrap/Bootstrap-dataTables/datatables.js"></script>

	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
  	<link href="../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../Generador/js/AltaProveedores.js"></script>
	<script type="text/javascript" src="../Generador/js/funciones.js"></script>
	<script type="text/javascript">
	//--------INICIO Variables Globales---------------------------------------------------------------------------------------------------
	var idOper;
	var operacionActual = <%=c.getCasoOperacion(0).getOperacion().getIdOperacion()%>;
	var roles="<%=roles%>";
	var actualizaCuentasBancarias=false;
	var sincambios=true;
	var rechazoAutorizacion=false;
	var myModal;
	var tips = $( ".validateTips" );
	var myModalMSG;
//-------FIN Variables Globales-------------------------------------------------------------------------------------------------------------
		//--------Inicia READY----------------------------------------------------------------------------------------------------------------------
		$(document).ready(function() {
			$(".custom-file-input").on("change", function() {
	  		  var fileName = $(this).val().split("\\").pop();
	  		  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
	  		});
			myModalMSG = new bootstrap.Modal(document.getElementById('modalConfirm'), {
				  keyboard: false
				})
			tablaCtas();
			//Funciones para Actualizar Clabe
		    $("#cbBanco").change(function() {
			    var szTemp = CalculaCLABE();
			    $("#txtCLABE").val(szTemp);
			    $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
			});
			$("#txtPlaza")
		        .change(function() {
		            var szTemp = CalculaCLABE();
		            $("#txtCLABE").val(szTemp);
		            $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
		        });
		    $("#txtCuentaBancaria")
		        .change(function() {
		            var szTemp = CalculaCLABE();
		            $("#txtCLABE").val(szTemp);
		            $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
		            queryFormPost("cuentaBancariaExiste", {async: false});
		            var existe = $("#cuentaExistente").val();
		            if (existe > 0)
		            	swal("Esa cuenta ya esta dada de alta para otro Beneficiario, verifique!!",{icon:"error",closeOnClickOutside: false,button: "Cerrar"});
		        });
	
		    $("#txtSucursal")
		        .change(function() {
		            var szTemp = CalculaCLABE();
		        });
		    $(function() {
				var IdRFC1 = $("#cIdRFC1"),
					IdRFC2 = $("#cIdRFC2"),
					IdRFC3 = $("#cIdRFC3"),
					ApellidoPaterno = $("#cApellidoPaterno"),
					ApellidoMaterno = $("#cApellidoMaterno"),
					Plaza =  $("#txtPlaza"),
					CuentaBancaria =  $("#txtCuentaBancaria"),
					Sucursal =  $("#txtSucursal"),
					EntidadSiaff =  $("#txtEntidadSiaff");
					
				
				$( "#pbCuentasBancarias" )
					.button()
					.click(function() {
						$("#txtNombreProveerdor").val( (IdRFC1.val() + "-"+ IdRFC2.val() + "-" + IdRFC3.val()).toUpperCase() + " / "+($.trim($("#cApellidoPaterno").val() + " " + $("#cApellidoMaterno").val() + " " + $("#cNombre").val())).toUpperCase());
						enableSelectBanck();
						inicializaDatosBancarios();				
					});
				
				$("#btnAddBankAcount" )
					.button()
					.click(function() {
						
						var bValid = true;
						bValid = bValid && esRequerido( Plaza, "La Plaza" );
						bValid = bValid && checkLength( Plaza, "La Plaza", 3, 3 );
						bValid = bValid && checkRegexp( Plaza, /^([0-9])+$/, "La plaza solo permite números : 0-9" );
						
						bValid = bValid && esRequerido( CuentaBancaria, "El No.Cuenta" );
						bValid = bValid && checkLength( CuentaBancaria, "El No.Cuenta", 11, 11 );
						bValid = bValid && checkRegexp( CuentaBancaria, /^([0-9])+$/, "El No.Cuenta solo permite números : 0-9" );
						
						bValid = bValid && esRequerido( Sucursal, "La Sucursal" );
						bValid = bValid && checkLength( Sucursal, "La Sucursal", 1, 4 );
						bValid = bValid && checkRegexp( Sucursal, /^([0-9])+$/, "La sucursal solo permite números : 0-9" );
						
				        var existe = $("#cuentaExistente").val();
				            if (existe > 0) {
				            	swal("Esa cuenta ya esta dada de alta para otro Beneficiario, No se puede Guardar!!",{icon:"error",closeOnClickOutside: false,closeOnClickOutside: false,button: "Cerrar"});
								return;
				            }
				            	
				            	
						if (!bValid) return;
						var arch = $.trim($("#archivoZip").val());
						if(arch.length == 0){
							swal("Debe cargar el Comprobante Bancario.",{icon:"info",closeOnClickOutside: false,closeOnClickOutside: false,button: "Cerrar"});
							return;
						}
						var szTemp = CalculaCLABE();		
						$("#txtCLABE").val(szTemp);
						$("#txtDigitoVerificador").val(szTemp.substr(17,1));
			    		var docsiguiente = parseInt($('#docSiguiente').val(),10);
			    		$('#docSiguiente').val(docsiguiente);
			    		$('#cNombreCuenta').val($('#cbBanco option:selected').text() + '-'+$('#txtCuentaBancaria').val());
			    		$('#cUsuarioModifico').val($.trim("<%=usuario.getLogin()%>"));
			    		var dHoy = new Date();
			    		$('#fCuentaModifico').val(dHoy.getFullYear() + "" + cerosIzq("" + (dHoy.getMonth() + 1), 2) + "" + cerosIzq("" + dHoy.getDate(), 2) + " " + cerosIzq("" + dHoy.getHours(), 2) + ":" + cerosIzq("" + dHoy.getMinutes(), 2));
			    		$('#cBancoH2').val($('#cBancoH').val());
			    		$('#cIdRFCB2').val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());
			    		$('#dBancoH2').val($('#dBancoH').val());
			    		$('#cFolio2').val($('#cFolio').val());
			    		ejecutaAjax();
					});
				
			});
		   
		   
			
		});//-------------Fin del READY----------------------------------------------------------------------------------------------------------------
		function ejecutaAjax(){
			var hayDocumento=false;
			var data = new FormData();
			jQuery.each(jQuery('#archivoZip')[0].files, function(i, file) {
			    data.append('file-'+i, file);
			    hayDocumento=true;
			});
			if(!hayDocumento){
				swal("Favor de seleccionar un archivo.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				return;
			}
			data.append('cIdRFCB2', $("#cIdRFCB2").val());
			data.append('cBancoH2', $("#cBancoH2").val());
			data.append('txtPlaza', $("#txtPlaza").val());
			data.append('txtCuentaBancaria', $("#txtCuentaBancaria").val());
			data.append('txtDigitoVerificador', $("#txtDigitoVerificador").val());
			data.append('cStatusCuentaH2', $("#cStatusCuentaH2").val());
			data.append('dBancoH2', $("#dBancoH2").val());
			data.append('txtSucursal', $("#txtSucursal").val());
			data.append('nBCBEnviadoSICOPH2', $("#nBCBEnviadoSICOPH2").val());
			data.append('cFolio2', $("#cFolio2").val());
			$.blockUI({message: "Procesando espere ......"});
			jQuery.ajax({
			    url: '../UploadDocBancario',
			    data: data,
			    cache: false,
			    contentType: false,
			    dataType: "json",
			    processData: false,
			    method: 'POST',
			    type: 'POST', // For jQuery < 1.9
			    success: function(j){
			    	swal({
			    		title: "",
			    		text: j[0].MSG,
			    		icon: "info",
			    		closeOnClickOutside: false,
			    		buttons: {
			    			confirm : "Cerrar"
			    			},
			    		}).then((continuar) => {
			    			$('#tblCuentasBancarias').dataTable().fnClearTable();
			    			parent.window.document.getElementById("pb_send").style.visibility = "visible";
			    			parent.document.getElementById("pb_send").disabled = true;
			    			sincambios=false;
			    			cargaDataTable();
			    			$('#btnCloseModal').click();
					    	$.unblockUI();
			    	});
			    },
			    error: function(j){
			    	$('#btnCloseModal').click();
			    	$.unblockUI();
			    }
			});
		}
		function  inicializaDatosBancarios(){
			$("#cbBanco").val('133');
			$("#txtPlaza").val('');
			$("#txtCuentaBancaria").val('');
			$("#txtSucursal").val('');
			var szTemp = CalculaCLABE();
		    $("#txtCLABE").val(szTemp);
		    $("#txtDigitoVerificador").val(szTemp.substr(17, 1));
		}
		function enableSelectBanck(){
			if($("#bEsActCta").val() == 1){
				$('#cbBanco').prop('disabled', false);
			}
		}
		function eliminaRegistro(cuenta,banco,plaza,digito,sucursal,status,cBanco){
			$("#cDescriptionDeleteBanckAcount").val("");
			clearDataDeleteBanckAcount();
			myModal = new bootstrap.Modal(document.getElementById('modalMotivoEliminaCta'), {
				  keyboard: false
				})
			myModal.show();
			addDataDeleteBanckAcount(cuenta,banco,plaza,digito,sucursal,status,cBanco);
		}
		function addDataDeleteBanckAcount(cuenta,banco,plaza,digito,sucursal,status,cBanco){
			$("#cIdRFCB").val($("#cIdRFC1").val() +$("#cIdRFC2").val()+$("#cIdRFC3").val());  
			$("#dCuentaBancariaH").val(cuenta);
			$("#cBancoH").val(banco);
			$("#cPlazaH").val(plaza);
			$("#dDigitoVerificadorH").val(digito);
			$("#dSucursalH").val(sucursal);
			$("#cStatus").val(status);
			$("#dBancoH2").val(cBanco);
			$("#cMotivoEliminaCta").val($("#cDescriptionDeleteBanckAcount").val());
		}
		function clearDataDeleteBanckAcount(){
			$("#cIdRFCB").val('');  
			$("#dCuentaBancariaH").val('');
			$("#cBancoH").val('');
			$("#cPlazaH").val('');
			$("#dDigitoVerificadorH").val('');
			$("#dSucursalH").val('');
			$("#cStatus").val('');
			$("#dBancoH2").val('');
			$("#cMotivoEliminaCta").val('');
		}
		function deleteBanckAcount(){
			if($("#cDescriptionDeleteBanckAcount").val() != ""){
				myModal.hide();
				swal({
					title: "Proceso para eliminar cuenta bancaria",
					text: "Esta seguro de Eliminar el siguiente registro:\n Cuenta: "+ $("#dCuentaBancariaH").val()
					+"\n Banco: "+$("#cBancoH").val()+"\n Plaza: "+$("#cPlazaH").val()
					+"\n Digito: "+$("#dDigitoVerificadorH").val()+"\n Sucursal: "+$("#dSucursalH").val(),
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
					}else{
						$.blockUI({message: "Procesando espere ......"});
						var object=llenaObjectDat(12,idOper);
						$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
							,data:object
							,dataType: 'json', success: 
								function(j){
								$('#tblCuentasBancarias').dataTable().fnClearTable();
								cargaDataTable();
								parent.window.document.getElementById("pb_send").style.visibility = "hidden";
								swal({
									title: "",
									text: j[0].MENSAJE,
									icon: "info",
									closeOnClickOutside: false,
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										$.unblockUI();
								});
							}, error: function( jqXHR, textStatus, errorThrown ) {
								$.unblockUI();
							}
						});
					}
				});
			}else{
				swal("Favor de capturar el motivo por el cual se requiere eliminar la cuenta bancaria.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
			}
		}
		function tablaCtas(){
			
			daTable= $("#tblCuentasBancarias").dataTable({
		        bPaginate: false,
		        bLengthChange: false,
		        bInfo: false,
		        bAutoWidth: false,
		        bJQueryUI: true,
		        bFilter: false,
		        bSort: false,
		        bInfo: false,
		        bAutoWidth: true,
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
		            oPaginate: {
		                sFirst: "Primero",
		                sPrevious: "Ant.",
		                sNext: "Sigte.",
		                sLast: "&Uacute;ltimo"
		            }
		        },
		        aaSorting: [
		            [1, "asc"]
		        ],
		        aoColumns: [
		        	{sName: "cBanco",bSearchable: false,bSortable: false,bVisible: false},
		            {sName: "dBanco"},
		            {sName: "cPlaza"}, 
		            {sName: "dCuentaBancaria"}, 
		            {sName: "dDigitoVerificador"}, 
		            {sName: "dSucursal"}, 
		            {sName: "cStatusCuenta",bSearchable: false,bSortable: false,bVisible: false}, 
		            {sName: "dStatusCuenta"}, 
		            {sName: "nBCBEnviadoSICOP"},
		            {sName: "Autorizada"},
		            {sName: "Operaciones",bVisible: ((operacionActual==2||operacionActual==4)?true:false)}
		        ]
	
		    });
		}
		function CalculaCLABE(){
			$("#cBancoH").attr("disabled", false);
			$("#cPlazaH").attr("disabled", false);
			$("#dCuentaBancariaH").attr("disabled", false);
			$("#dDigitoVerificadorH").attr("disabled", false);
			$("#cStatusCuentaH").attr("disabled", false);
			$("#dBancoH").attr("disabled", false);
			$("#dSucursalH").attr("disabled", false);
			$("#nBCBEnviadoSICOPH").attr("disabled", false);
					
			$('#cBancoH').val($('#cbBanco').val());
			$('#cPlazaH').val($('#txtPlaza').val());
			$('#dCuentaBancariaH').val($('#txtCuentaBancaria').val());
			$('#dDigitoVerificadorH').val($('#txtDigitoVerificador').val());
			$('#dBancoH').val($('#cbBanco option:selected').text());
			$('#dSucursalH').val($('#txtSucursal').val());
						
			var  n=0;
			var iRes=0;
			var arrFactor=new Array(3,7,1,3,7,1,3,7,1,3,7,1,3,7,1,3,7); 
			var arrResult=new Array(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1);
			var szCLABE = $("#cbBanco").val();
			
			szCLABE += (cerosIzq($("#txtPlaza").val(),3 )) + (cerosIzq($("#txtCuentaBancaria").val(),11 ));
			
			for (n=0 ;n<arrFactor.length ; n++)
			{
				arrResult[n] = (arrFactor[n] * parseInt(szCLABE.charAt(n),10)) % 10;
				iRes += arrResult[n];
			}
			iRes = iRes % 10;
			iRes = (10 - iRes) % 10;
			szCLABE = szCLABE + iRes;
			return szCLABE;
		}
		
	</script>

  </head>
  
  <body id="dt_example" bgColor="red" onkeydown="return(desactivaBackspace(event))">
	<form id="ExportarForm" name="ExportarForm" action="../reportes/FormatoAltaProveedor" method="POST" target="_blank">
		<div class="container-fluid">
			<h1><span id="spanEncabezado">Alta Cuenta Bancaria Proveedor</span></h1>
			<div class="row">
				<div class="col-lg ">
					<fieldset class="form-group border p-3">
						<legend class="w-auto px-2">Captura Datos Generales</legend>
						<div class="form-group">
							<div class="row">
						    	<div class="col-auto">
						      		<label for="cFolio">Folio SAI:</label>
						      		<input type="text" class="form-control form-control-sm" disabled title="Número de Folio, se asigna automáticamente" 
						      		value="<%=c.getFolio()%>" placeholder="Folio SAI" aria-label="cFolio" aria-describedby="basic-addon1"  id="cFolio" name="cFolio" />
						    	</div>
						    	<div class="col-auto">
						    		<label for="botonLibera"></label>
						      		<input id="botonLibera" name="botonLibera" type="button" value="Liberar" class="form-control btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all" 
						      		onclick="liberaCaso()" style="display: none;" />
						      	</div>
						  	</div>
					  	</div>
					  	<div class="form-group" >
							<div class="row">
						    	<div class="col-auto">
						      		<span id="btnObservaciones">
										<a href="#" onclick="OpenDialogObservaciones();">MOSTRAR OBSERVACIONES</a> 
									</span>
						      	</div>
						      	<div class="col-auto" id="tdObservacion" style="display: none;">
									<textarea class="form-control" id="cObservaciones" name="cObservaciones" rows="5" cols="60"></textarea>
								</div>
						  	</div>
							<div class="row" id="trEsEFO">
								<div class="col-auto">
							  		<input class="form-control" id="msgEFOS" name="msgEFOS" value="" readonly="readonly" style="color: red; border-width:0; background-color:transparent;"/> 
							  	</div>
							</div>
							<div class="row" id="trdescProv">
								<div class="col-auto">
							  		<font color="blue">Proveedor: Prestador de
											servicios, Persona Moral o Física para pago por capitulo 2000,
											3000, 5000 y 6000</font> 
							  	</div>
							</div>
							<div class="row" id="trdescBen" style="display: none;">
								<div class="col-auto">
							  		<font color="blue">Beneficiario: Empleado CNF,
											Persona Moral o Física para pago de subsidios capitulo 4000</font> 
							  	</div>
							</div>
						</div>
						<div class="form-group" id="trCBEN" style="display: none;">
							<div class="row" >
								<div class="col-4">
							  		<label for="NEmp" >CBEN:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
								  		<input type="text" class="form-control" placeholder="CBEN" aria-label="CBEN" aria-describedby="basic-addon1" name="CBEN" id="CBEN" readonly="readonly"/>
								  		<label id="labelCBEN"><font color="white">*</font></label>
							  		</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row">
								<div class="col-4">
							  		<label for="cIdTipoPersonaRFC">Tipo de Persona:</label>
							  	</div>
							  	<div class="col-auto">
							  		<div class="row">
								  		<div class="col-auto">
											<div class="row">
												<div class="col-auto col-auto-inline ">
													<select id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC"  class="custom-select" disabled="disabled"></select>
												</div>
												<div class="col-auto col-auto-inline">
													<select id="tipoPB" name="tipoPB"  class="custom-select" disabled="disabled">
														<option value="PROVEEDOR" selected>PROVEEDOR</option>
														<option value="BENEFICIARIO">BENEFICIARIO</option>
													</select> 
													<select id="cIdTipoDoc" name="cIdTipoDoc" style="display: none;" class="custom-select" ></select>
													<select id="cDocBanca" name="cDocBanca" style="display: none;" class="custom-select" ></select>
													<select id="ctaBanca" name="ctaBanca" style="display: none;" class="custom-select" ></select>
												</div>
												<div class="col-auto col-auto-inline">
													<div class="row" id="divCheckExtramjero" >
														<div class="col-auto">		
														<label class="form-check-label" for="chk_extranjero">¿Es extranjero? </label>
														</div>
														<div class="col-auto">
											  				<input class="form-check-input" type="checkbox" id="chk_extranjero" name="chk_extranjero" value="0" disabled="disabled">
											  			</div>
													</div>
												</div>
											</div>
										</div>
									</div>
							  	</div>
							</div>
						</div>
						
						<div class="form-group">
							<div class="row">
								<div class="col-4">
							  		<label for="idRegimenFiscal">Regimen Fiscal:</label>
							  	</div>
							  	<div class="col-6">
									<div class="input-group ">
										<select id="idRegimenFiscal" name="idRegimenFiscal"  class="custom-select" ></select>
									</div> 
							  	</div>
							</div>
						</div>
						
						<div class="form-group">
							<div class="row">
								<div class="col-4">
							  		<label for="cNumeroREPSE" id="cNumeroREPSELabel">N&uacute;mero REPSE:</label>
							  	</div>
							  	<div class="col-6">
									<div class="input-group ">
										<input type="text" class="form-control" placeholder="REPSE" aria-label="REPSE" aria-describedby="basic-addon1"  name="cNumeroREPSE" id="cNumeroREPSE"  />
									</div> 
							  	</div>
							</div>
						</div>
					
						<div class="form-group">
							<div class="row" id="trNoEmpleado" style="display: none;">
								<div class="col-4">
							  		<label for="NEmp" >No. Empleado:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
								  		<input type="text" class="form-control" placeholder="No. Empleado" aria-label="No. Empleado" aria-describedby="basic-addon1" maxlength="5" name="NEmp" id="NEmp" onKeyPress="return(onlyNumbersAndLetters(event,this));" />
								  		<label id="labelNumEmp"><font color="red">*</font></label>
							  		</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row"  >
								<div class="col-4">
							  		<label for="NEmp">Registro Federal de Contribuyentes [RFC]:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
										<input type="text" class="form-control input-sm" placeholder="RFC1" aria-label="RFC1" aria-describedby="basic-addon1" name="cIdRFC1" id="cIdRFC1" readonly="readonly"/>
										<span class="input-group-text">-</span>
										<input type="text" class="form-control" placeholder="RFC2" aria-label="RFC2" name="cIdRFC2" id="cIdRFC2" readonly="readonly"/>
										<span class="input-group-text">-</span>
										<input type="text" class="form-control" placeholder="RFC3" aria-label="RFC3" aria-describedby="basic-addon1" name="cIdRFC3" id="cIdRFC3" readonly="readonly"/>
										<label id="labelRFC"><font color="red">*</font></label>
										<input type="hidden" name="cIdRFC" id="cIdRFC" style="width: 4em;" />
									</div>
									
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="trCurp" style="display: none;">
								<div class="col-4">
							  		<label for="cCURP">CURP:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="row">
							  			<div class="col">
							  				<div class="input-group has-validation">
								  				<input type="text" class="form-control" placeholder="CURP" aria-label="CURP" aria-describedby="basic-addon1"  name="cCURP" id="cCURP" maxlength="18" onblur="ChangeCase(this);"
												onKeyPress="return(onlyNumbersAndLetters(event,this));"  />
												<label id="labelCURP" ><font color="red">*</font></label>
												<div class="invalid-feedback">
											        Por favor capture la CURP.
											    </div>
											</div>
							  			</div>
							  		</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="pMoralRazon">
								<div class="col-4">
							  		<label for="cRazonSocial"> Raz&oacute;n Social:</label>
							  	</div>
							  	<div class="col-6" >
							  		<div class="input-group">
							  		<input type="text" name="cRazonSocial" id="cRazonSocial" class="form-control" placeholder="Razoón Social" aria-label="Razoón Social" maxlength="300" onblur="ChangeCase(this);" />
									<label id="labelRazon"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="pMoralRepresentante">
								<div class="col-4">
							  		<label for=""> Nombre del Representante:</label>
							  	</div>
							  	
							</div>
							<div class="row" id="aPat">
								<div class="col-4">
							  		<label for="cApellidoPaterno"> Apellido Paterno:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<input type="text" class="form-control" placeholder="Apellido Paterno" aria-label="Apellido Paterno" name="cApellidoPaterno" id="cApellidoPaterno" 
									maxlength="300" onblur="ChangeCase(this);" /> 
									<label id="labelPaterno"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="aMat">
								<div class="col-4">
							  		<label for="cApellidoMaterno">Apellido Materno :</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<input type="text" class="form-control" placeholder="Apellido Materno" aria-label="Apellido Materno" name="cApellidoMaterno" id="cApellidoMaterno" 
									maxlength="300" onblur="ChangeCase(this);" /> 
									<label id="labelMaterno"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="aNom">
								<div class="col-4">
							  		<label for="cNombre"> Nombre:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<input type="text" class="form-control" placeholder="Nombre" aria-label="Nombre" name="cNombre" id="cNombre"
									maxlength="300" onblur="ChangeCase(this);" />
									<label id="labelNombre"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="trGiro">
								<div class="col-4">
							  		<label for="cGiro"> Actividad Econ&oacute;mica:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<input type="text" class="form-control" placeholder="Giro" aria-label="Giro" name="cGiro" id="cGiro"
									maxlength="499" onblur="ChangeCase(this);" />
									<label id="labelGiro"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="trPyme">
								<div class="col-4">
							  		<label for="nIdPyme"> Pyme:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  			<select id="nIdPyme" name="nIdPyme"  class="custom-select" onchange="validaOrganismoPublico()">	</select>
							  			<label id="labelPyme"><font color="white">*</font></label>
							  		</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" id="trLegenOrgPub" style="display: none;">
								<div class="col-6">
							  		<span style="color: red">Los
									organismos p&uacute;blicos solo se pueden dar de alta en el &acute;rea de adquisiciones de oficinas centrales. </span>
							  	</div>
							</div>
						</div>
					</fieldset>
				</div>
			</div>
			<div class="row">
			  	<div class="col-lg ">
			  		<fieldset class="form-group border p-3">
			  			<legend class="w-auto px-2">Domicilio Fiscal</legend>
			  			<div class="form-group">
				  			<div class="row" id="Pais" style="display: none;">
								<div class="col-4">
							  		<label for="cPais"> Pais:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  			<input type="text" class="form-control" placeholder="Pais" aria-label="Pais" name="cPais" id="cPais"
										maxlength="100" onblur="ChangeCase(this);" />
										<font color="red">*</font>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cEstadoFiscal"> Entidad Federativa:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<select id="cEstadoFiscal" name="cEstadoFiscal" class="custom-select" onChange="actualizaMunicipio();" ></select>
							  		<label id="labelEstadoFiscal"><font color="white">*</font></label>
							  		</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cMunicipioFiscal"> Municipio:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<select id="cMunicipioFiscal" name="cMunicipioFiscal" class="custom-select"></select>
							  		<label id="labelMunicipioFiscal"><font color="white">*</font></label>
							  		</div>
							  	</div>
							</div>	
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cLocalidadFiscal"> Localidad:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<select id="cLocalidadFiscal" name="cLocalidadFiscal" class="custom-select"></select>
							  		<label id="labelLocalidadFiscal"><font color="white">*</font></label>
							  		</div>
							  	</div>
							</div>	
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cCalle"> Calle:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  			<input type="text" class="form-control" placeholder="Calle" aria-label="Calle" name="cCalle" id="cCalle"
										maxlength="100" onKeyPress="return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" /> 
										<label id="labelCalle"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cNumeroExterno"> N&uacute;mero Externo:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
										<input type="text" class="form-control" placeholder="N&uacute;mero Externo" aria-label="N&uacute;mero Externo" name="cNumeroExterno"
										id="cNumeroExterno" maxlength="20" onKeyPress="return(onlyNumbersAndLetters(event,this));" /> 
										<label id="labelNumero"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cNumeroInterno"> N&uacute;mero Interno:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
										<input type="text" class="form-control" placeholder="N&uacute;mero Interno" aria-label="N&uacute;mero Interno" name="cNumeroInterno" id="cNumeroInterno"  maxlength="20"
										onKeyPress="return(onlyNumbersAndLetters(event,this));" />
										<label id="cNumeroInterno"><font color="white">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cColonia"> Colonia:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
										<input type="text" class="form-control" placeholder="Colonia" aria-label="Colonia" name="cColonia" id="cColonia"
										 maxlength="100" onblur="ChangeCase(this);"
										onKeyPress="return(onlyNumbersAndLetters(event,this));" /> 
										<label id="labelColonia"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cCodigoPostal"> C&oacute;digo Postal:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
										<input type="text" class="form-control" placeholder="C&oacute;digo Postal" aria-label="C&oacute;digo Postal" name="cCodigoPostal" id="cCodigoPostal" 
										onKeyPress="return onlyNumbers(event);" maxlength="5" /> 
										<label id="labelCP"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
			  		</fieldset>
			  	</div>
		  	</div>
			<div class="row">
			  	<div class="col-lg ">
			  		<fieldset class="form-group border p-3">
			  			<legend class="w-auto px-2">Contacto</legend>
			  			<div class="form-group">
				  			<div class="row" >
								<div class="col-4">
							  		<label for="cUrl"> P&aacute;gina Web:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  			<input type="text" class="form-control" placeholder="P&aacute;gina Web" aria-label="P&aacute;gina Web" name="cUrl" id="cUrl"  maxlength="75" />
							  			<label id="labelPaginaWeb"><font color="White">*</font></label>
							  		</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cEmail"> Correo Electr&oacute;nico:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
							  		<input type="text" class="form-control" placeholder="Correo Electr&oacute;nico" aria-label="Correo Electr&oacute;nico" name="cEmail" id="cEmail"  maxlength="75" /> 
									<label id="labelCorreo"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-4">
							  		<label for="cTelefono"> Tel&eacute;fono:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
								  		<select id="cTipoTelefono" name="cTipoTelefono" class="custom-select"></select> 
										<input type="text" class="form-control" placeholder="Tel&eacute;fono" aria-label="Tel&eacute;fono" name="cTelefono" id="cTelefono"
										maxlength="10" onKeyPress="return onlyNumbers(event);" /> 
										<label id="labelTel"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
						
			  		</fieldset>
			  	</div>
		  	</div>
		  	<div class="row" id="cuentasBancarias">
			  	<div class="col-lg ">
			  		<fieldset class="form-group border p-3">
			  			<legend class="w-auto px-2">Cuentas Bancarias</legend>
			  				<div class="form-group">
								<div class="row" >
									<div class="col-4">
								  		
								  	</div>
								  	<div class="col-6">
								  		<div class="input-group">
									  		<input type="button" id="pbCuentasBancarias" value="Agregar Cuenta Bancaria" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#dialog-form"/>
										</div>
								  	</div>
								</div>
							</div>
			  				<div class="form-group">
								<div class="form-group row" >
									<div class="col">
										<table id="tblCuentasBancarias" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
											<thead>
												<tr>
													<th width="50px">cBanco</th>
													<th width="120px">Banco</th>
													<th width="40px">Plaza</th>
													<th width="115px">No.Cuenta</th>
													<th width="35px">Dígito</th>
													<th width="60px">Sucursal</th>
													<th width="60px">cStatus</th>
													<th width="70px">Estatus</th>
													<th width="30px">Enviado</th>
													<th width="30px">Autorizada</th>
													<th width="30px">&nbsp;</th>
												</tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
			  		</fieldset>
			  	</div>
			</div>
		</div>
		<input type="hidden" name="idCaso" id="idCaso" value="<%=idCaso%>" />
		<input type="hidden" name="cIdUsuarioCaptura" id="cIdUsuarioCaptura" value="<%=usuario.getLogin()%>" /> 
		<input type="hidden" name="cIdUsuarioValida" id="cIdUsuarioValida" value="<%=usuario.getLogin()%>" /> 
		<input type="hidden" name="cIdUsuaUltModif" id="cIdUsuaUltModif" value="<%=usuario.getLogin()%>" />
		<input type="hidden" name="cIdUsuarioLogeado" id="cIdUsuarioLogeado" value="<%=usuario.getLogin()%>" />
		<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
		<input type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO" value="<%=today%>" /> 
		<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"> 
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"	value="<%=usuario.getU_UR()%>" />
		<input type="hidden" name="cDocumentoHaplicado" id="cDocumentoHaplicado" value="" />
		<input type="hidden" name="cActualizacion" id="cActualizacion" value="" />
		<input type='hidden' id='cExtranjero' name='cExtranjero'>
		<input type="hidden" name="existeFolio" id="existeFolio" value="" />
		<input type="hidden" name="regreso" id="regreso" value=" " />
		<input type="hidden" name="regresoBenef" id="regresoBenef" value=" " />
		<input type="hidden" name="cIdRFCB" id="cIdRFCB" value="" /> 
		<input type="hidden" name="cIdRFCsinH" id="cIdRFCsinH" value="" />
		<input type="hidden" name="flujo" id="flujo" value="PROVEEDORES" /> 
		<input type="hidden" name="regresoSinH" id="regresoSinH" value="" />
		
		<input type="hidden" name="deleteCuenta" id="deleteCuenta" value="" />
		<input type="hidden" name="deleteBanco" id="deleteBanco" value="" />
		<input type="hidden" name="deletePlaza" id="deletePlaza" value="" />
		<input type="hidden" name="deleteDigito" id="deleteDigito" value="" />
		<input type="hidden" name="deleteSucursal" id="deleteSucursal" value="" />
		<input type="hidden" name="deleteStatus" id="deleteStatus" value="" />
		<input type="hidden" name="deleteBanco" id="deleteBanco" value="" />
		
		<!-- Variables Para Cuentas Bancarias -->
		<input id="cUsuarioModifico" name="cUsuarioModifico" type="hidden" size="10">
		<input type='hidden' id='cBancoH' name='cBancoH' value=''>
		<input type='hidden' id='cPlazaH' name='cPlazaH' value=''> 
		<input type='hidden' id='dCuentaBancariaH' name='dCuentaBancariaH' value=''>
		<input type='hidden' id='dDigitoVerificadorH' name='dDigitoVerificadorH' value=''> 
		<input type='hidden' id='cStatusCuentaH' name='cStatusCuentaH' value='1'>
		<input type='hidden' id='cStatus' name='cStatus' value=''>
		<input type='hidden' id='dBancoH' name='dBancoH' value=''> 
		<input type='hidden' id='dSucursalH' name='dSucursalH' value=''> 
		<input type='hidden' id='nBCBEnviadoSICOPH' name='nBCBEnviadoSICOPH' value='0'>
		<input type="hidden" name="bLiberaCaso" id="bLiberaCaso" value="0" />
		<input type="hidden" name="bEsActCta" id="bEsActCta" value="0" />
		<input type="hidden" name="nIdOperAnt" id="nIdOperAnt" value="0" />
		<input type="hidden" name="cuentaExistente" id="cuentaExistente" />
	</form>
	
	
	<!-- Modal -->
	<div class="modal fade bd-example-modal-lg" id="dialog-form" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<form id="FormProveedor" name="FormProveedor" action="../UploadDocBancario" method="post"	enctype="multipart/form-data">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title" id="staticBackdropLabel">Captura de Cuentas Bancarias</h5>
			        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      </div>
			      <div class="modal-body">
			      	<fieldset class="form-group border p-3">
			      		<div class="form-group">
							<div class="row" >
								<div class="col-12">
							  		<label for="txtNombreProveerdor" >Beneficiario:</label>
							  		<input type="text" class="form-control" placeholder="Beneficiario" aria-label="No. Empleado" aria-describedby="basic-addon1" name="txtNombreProveerdor" id="txtNombreProveerdor" readonly/>
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-auto">
							  		<label for="txtNombreProveerdor" >Banco:</label>
							  		<select id="cbBanco" name="cbBanco" class="custom-select"></select>
							  	</div>
							  	<div class="col-auto">
							  		<label for="txtPlaza" >Plaza:</label>
							  		<input type="text" class="form-control" placeholder="000" aria-label="000" aria-describedby="basic-addon1" name="txtPlaza" 
							  		id="txtPlaza" size="3" maxlength="3" onkeypress="Validaciones(this,2)" required="required"/>
							  	</div>
							  	<div class="col-auto">
							  		<label for="txtCuentaBancaria" >No.Cuenta:</label>
							  		<input type="text" name="txtCuentaBancaria" id="txtCuentaBancaria" class="form-control"  placeholder="00000000000" aria-label="00000000000" 
							  		aria-describedby="basic-addon1"
									size="11" maxlength="11" onkeypress="Validaciones(this,2)" />
							  	</div>
							  	<div class="col-auto">
							  		<label for="txtSucursal" >Sucursal:</label>
							  		<input type="text" name="txtSucursal" id="txtSucursal" size="4" maxlength="4" placeholder="0000" class="form-control" aria-label="0000" aria-describedby="basic-addon1"
										onkeypress="Validaciones(this,2)" />
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
								<div class="col-auto">
							  		<label for="txtDigitoVerificador" >D&iacute;gito de Control:</label>
							  		<input type="text" name="txtDigitoVerificador" id="txtDigitoVerificador" size="2" class="form-control" placeholder="0" aria-label="0" aria-describedby="basic-addon1" readonly />
							  	</div>
							  	<div class="col-auto">
							  		<label for="txtCLABE" >CLABE:</label>
							  		<input type="text" name="txtCLABE" id="txtCLABE" readonly class="form-control" placeholder="00000000000000000000" aria-label="Clabe" aria-describedby="basic-addon1" />
							  	</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row" >
							  	<div class="col-12">
							  		<div class="custom-file">
									    <input type="file" class="custom-file-input" id="archivoZip" name="archivoZip" aria-describedby="inputGroupFileAddon01" required="required">
									    <label class="custom-file-label" for="archivoZip">Comprobante Bancario "Adjunta estado de cuenta"</label>
									</div>
							  	</div>
							</div>
						</div>
						
			      	</fieldset>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-primary" id="btnAddBankAcount" name="btnAddBankAcount">Aceptar</button>
			        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCloseModal" name="btnCloseModal">Cancelar</button>
			        <input type='hidden' id='cBancoH2' name='cBancoH2' value=''>
					<input type='hidden' id='cIdRFCB2' name='cIdRFCB2' value=''>
					<input type='hidden' id='cStatusCuentaH2' name='cStatusCuentaH2' value='0'> 
					<input type='hidden' id='dBancoH2' name='dBancoH2' value=''> 
					<input type='hidden' id='nBCBEnviadoSICOPH2' name='nBCBEnviadoSICOPH2' value='0'>
					<input type='hidden' id='cFolio2' name='cFolio2'>
			      </div>
			    </div>
			</form>
		</div>
	</div>
	<!-- Modal -->
	<div class="modal fade" id="modalMotivoEliminaCta" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLabel">¿Cuál es el motivo para eliminar la cuenta?</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
	        	<input type="text" id="cDescriptionDeleteBanckAcount" name="cDescriptionDeleteBanckAcount" value="" class="form-control" placeholder="Motivo por el cual se requiere eliminar la cta. bancaria" aria-label="Motivo por el cual se requiere eliminar la cta. bancaria" aria-describedby="basic-addon1"/>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" id="btnAceptDeleteBanckAcount" name="btnAceptDeleteBanckAcount" onclick="deleteBanckAcount();">Aceptar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelDeleteBanckAcount" name="btnCancelDeleteBanckAcount">Cancelar</button>
	      </div>
	    </div>
	  </div>
	</div>
	<!-- Modal -->
		<div class="modal fade" id="modalConfirm" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="modalLabelConfirm">Writte your mesage..</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" id="btnCancelModalConfirm" name="btnModalConfirm">Aceptar</button>
		      </div>
		    </div>
		  </div>
		</div>
  </body>
</html>

