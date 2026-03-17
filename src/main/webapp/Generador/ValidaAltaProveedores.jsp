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
%>

<!DOCTYPE html>
<html>
  <head>
   
    
    <title>Valida Alta Proveedores</title>
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
	<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript">
	//--------INICIO Variables Globales---------------------------------------------------------------------------------------------------
	var idOper;
	var operacionActual = <%=c.getCasoOperacion(0).getOperacion().getIdOperacion()%>;
	var actualizaCuentasBancarias=false;
	var sincambios=true;
	var rechazoAutorizacion=false;
	var devolucion=false;
//-------FIN Variables Globales-------------------------------------------------------------------------------------------------------------
		//--------Inicia READY----------------------------------------------------------------------------------------------------------------------
		$(document).ready(function() {
			tablaCtas();
		});//-------------Fin del READY----------------------------------------------------------------------------------------------------------------
		function tablaCtas(){
			daTable= $("#tblCuentasBancarias").dataTable({
		        bPaginate: false,
		        bLengthChange: false,
		        bInfo: false,
		        bAutoWidth: false,
		        sScrollY: "125",
		        sScrollX: "100",
		        bJQueryUI: true,
		        bFilter: false,
		        bSort: false,
		        bInfo: false,
		        bAutoWidth: true,
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
		function habilitaObservaciones(opc){// en caso de no Autorizar Habilita para escribir observaciones
			if(opc==1){
				$("#tdObservacion").hide();
				$("#lObservacion").hide();
				document.getElementById("autorizaNo").checked = false;
			}else if(opc==2){
				$("#tdObservacion").show();
				$("#lObservacion").show();
				document.getElementById("autorizaSi").checked = false;
			}	
		}
		function eliminaRegistro(cuenta,banco,plaza,digito,sucursal,status){
			swal("No se puede eliminar la cuenta, para eliminarla tiene que devolver el tramite.",{icon:"info",button: "Cerrar"});
		}
		</script>
  </head>
  
  <body id="dt_example" bgColor="red"  onkeydown="return(desactivaBackspace(event))">
    <form id="ExportarForm" name="ExportarForm" action="../reportes/FormatoAltaProveedor" method="POST" target="_blank">
		<div class="container-fluid">
			<h1><span id="spanEncabezado">Valida Proveedor</span> </h1>
			<div class="row">
			  		<div class="col-lg ">
					<fieldset class="form-group border p-3">
						<legend class="w-auto px-2"> Datos Generales</legend>
						<div class="form-group">
							<div class="row">
						    	<div class="col-auto">
						      		<label for="cFolio">Folio SAI:</label>
						      		<input type="text" class="form-control form-control-sm" disabled title="Número de Folio, se asigna automáticamente" 
						      		value="<%=c.getFolio()%>" placeholder="Folio SAI" aria-label="cFolio" aria-describedby="basic-addon1"  id="cFolio" name="cFolio" />
						    	</div>
						    	<div class="col-auto">
						    		<label for="botonExtrae"></label>
						      		<input id="botonExtrae" name="botonExtrae" type="button" value="Extraer" class="form-control btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all"
										onclick="enviaConsulta()" style="display: none;" />
						      	</div>
						  	</div>
						  	<div class="row">
						  		<div class="col-lg">
						  			<fieldset id="fieldAutoriza" class="form-group border p-3"  style="display: none;" >
										<legend id="legendAutoriza" class="w-auto px-2">Autorizar</legend>
											<div class="row">
						  						<div class="col-auto">
						  							<div class="form-check  form-check-inline">
													 	<input class="form-check-input" type="radio" name="autorizaSi" id="autorizaSi" onClick="habilitaObservaciones(1)" value="Si">
													  	<label class="form-check-label" for="autorizaSi">Si</label>
													</div>
													<div class="form-check  form-check-inline">
													 	<input class="form-check-input" type="radio" name="autorizaNo" id="autorizaNo" onClick="habilitaObservaciones(2)" value="No">
													  	<label class="form-check-label" for="autorizaNo">No</label>
													  	
													</div>
													<div class="form-check  form-check-inline" id="tdObservacion" style="display: none;">
												  		<label for="botonLibera" id="lObservacion" style="display: none;">Observaciones:</label>
												  		<textarea class="form-control" id="cObservaciones" placeholder="Observaciones por el cual no se autoriza el tramite" 
												  		aria-label="Observaciones por el cual no se autoriza el tramite" aria-describedby="basic-addon1" name="cObservaciones" rows="5" cols="60"></textarea>
												  	</div>
						  						</div>
						  					</div>
									</fieldset>
						  		</div>
						  	</div>
					  	</div>
					  	<div class="form-group">
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
							  		<label for="CBEN" >CBEN:</label>
							  	</div>
							  	<div class="col-6">
							  		<div class="input-group">
								  		<input type="text" class="form-control" placeholder="CBEN" aria-label="CBEN" aria-describedby="basic-addon1" name="CBEN" id="CBEN" readonly="readonly" />
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
									<div class=" input-group ">
										<select id="idRegimenFiscal" name="idRegimenFiscal"  class="custom-select" disabled="disabled"></select>
									</div> 
							  	</div>
							</div>
						</div>
						
						<div class="form-group">
							<div class="row">
								<div class="col-4">
							  		<label for="cNumeroREPSE" id="cNumeroREPSELabel" >N&uacute;mero REPSE:</label>
							  	</div>
							  	<div class="col-6">
									<div class="input-group">
										<input type="text" class="form-control" placeholder="REPSE" aria-label="REPSE" aria-describedby="basic-addon1"  name="cNumeroREPSE" id="cNumeroREPSE" readonly="readonly" />
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
								  		<input type="text" class="form-control" placeholder="No. Empleado" aria-label="No. Empleado" aria-describedby="basic-addon1" 
								  		maxlength="5" name="NEmp" id="NEmp" readonly="readonly" />
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
							  				<div class="input-group">
								  				<input type="text" class="form-control" placeholder="CURP" aria-label="CURP" aria-describedby="basic-addon1"  name="cCURP" id="cCURP" 
								  				maxlength="18" readonly="readonly"  />
												<label id="labelCURP" ><font color="red">*</font></label>
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
							  		<input type="text" name="cRazonSocial" id="cRazonSocial" class="form-control" placeholder="Razoón Social" aria-label="Razoón Social" maxlength="300" 
							  		readonly="readonly" />
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
									maxlength="300" readonly="readonly" /> 
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
									maxlength="300" readonly="readonly" /> 
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
									maxlength="300" readonly="readonly" />
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
									maxlength="499" readonly="readonly" />
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
							  			<select id="nIdPyme" name="nIdPyme"  class="custom-select" disabled="disabled">	</select>
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
										maxlength="100" readonly="readonly" />
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
							  		<select id="cEstadoFiscal" name="cEstadoFiscal" class="custom-select" disabled="disabled"></select>
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
							  		<select id="cMunicipioFiscal" name="cMunicipioFiscal" class="custom-select" disabled="disabled"></select>
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
							  		<select id="cLocalidadFiscal" name="cLocalidadFiscal" class="custom-select" disabled="disabled"></select>
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
										maxlength="100" readonly="readonly" /> 
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
										id="cNumeroExterno" maxlength="20" readonly="readonly" /> 
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
										<input type="text" class="form-control" placeholder="N&uacute;mero Interno" aria-label="N&uacute;mero Interno" name="cNumeroInterno" 
										id="cNumeroInterno"  maxlength="20" readonly="readonly"/>
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
										 maxlength="100" readonly="readonly" /> 
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
										maxlength="5" readonly="readonly" /> 
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
							  			<input type="text" class="form-control" placeholder="P&aacute;gina Web" aria-label="P&aacute;gina Web" name="cUrl" id="cUrl"  maxlength="75" readonly="readonly" />
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
							  		<input type="text" class="form-control" placeholder="Correo Electr&oacute;nico" aria-label="Correo Electr&oacute;nico" name="cEmail" id="cEmail"  maxlength="75" readonly="readonly" /> 
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
								  		<select id="cTipoTelefono" name="cTipoTelefono" class="custom-select" disabled="disabled"></select> 
										<input type="text" class="form-control" placeholder="Tel&eacute;fono" aria-label="Tel&eacute;fono" name="cTelefono" id="cTelefono"
										maxlength="10" readonly="readonly" /> 
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
		
	</form>
  </body>
</html>
		