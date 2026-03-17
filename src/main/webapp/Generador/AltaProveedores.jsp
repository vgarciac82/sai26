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
    <title>Alta de Proveedores</title>
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
//-------FIN Variables Globales-------------------------------------------------------------------------------------------------------------
		//--------Inicia READY----------------------------------------------------------------------------------------------------------------------
		var myModal;
		var repuestaValidaDAtos=false;
		
		$(document).ready(function() {
			
			$("input.AyudaSyC").subIniciaDlg();
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			
			$("#chk_extranjero").change(function(){
				esExtranjero();			
			});
			
			myModal = new bootstrap.Modal(document.getElementById('modalConfirm'), {
			  keyboard: false
			});
			
			$("#cIdTipoPersonaRFC").change(function(){
				cargaRegimenFiscal($("#cIdTipoPersonaRFC").val());
			})
			
			
		});//-------------Fin del READY----------------------------------------------------------------------------------------------------------------
		function aceptModal(){
			return validarDatos(id_oper,10);
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0" onkeydown="return(desactivaBackspace(event))">
  	<form name="ExportarForm" action="../reportes/FormatoAltaProveedor" method="POST" target="_blank">
		<div class="container-fluid">
			<h1><span id="spanEncabezado">Captura Alta Proveedor</span> </h1>
		    
			<div class="row" id="fieldHabilita" style="display: none;">
			  	<div class="col-lg ">
			  		<fieldset class="form-group border p-3">
			  			<legend class="w-auto px-2">Proveedor Habilitado</legend>
			  			<div class="row" >
			  				<div class="col-auto">
				  				<div class="form-check  form-check-inline">
								  <input class="form-check-input" type="radio" name="flexRadioDefault" id="habilitaSi" onClick="inhabilitaProveedor(1)" value="Si">
								  <label class="form-check-label" for="habilitaSi">
								    Si
								  </label>
								  
								</div>
								<div class="form-check  form-check-inline">
								  <input class="form-check-input" type="radio" name="flexRadioDefault" id="habilitaNo" onClick="inhabilitaProveedor(2)" value="No">
								  <label class="form-check-label" for="habilitaNo">
								    No
								  </label>
								</div>
							</div>
							<div class="col-auto" id="tdObservacion" style="display: none;">
								<textarea class="form-control" id="cObservaciones" name="cObservaciones" rows="5" cols="60"></textarea>
							</div>
						</div>
			  		</fieldset>
			  	</div>
			</div>
		  	<div class="row">
		  		<div class="col-lg ">
				<fieldset class="form-group border p-3">
					<legend class="w-auto px-2"> Captura Datos Generales</legend>
					<div class="row">
				    	<div class="col-auto">
				      		<label for="cFolio">Folio SAI:</label>
				      		<input type="text" class="form-control form-control-sm" disabled title="Número de Folio, se asigna automáticamente" value="<%=c.getFolio()%>" placeholder="Folio SAI" aria-label="cFolio" aria-describedby="basic-addon1"  id="cFolio" name="cFolio" />
				    	</div>
				    	<div class="col-auto">
				    		<label for="botonLibera"></label>
				      		<input id="botonLibera" name="botonLibera" type="button" value="Liberar" class="form-control btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all" 
				      		onclick="liberaCaso()" style="display: none;" />
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
												<select id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC" onChange="hideAndShowXtipePerson2();" class="custom-select" ></select>
											</div>
											<div class="col-auto col-auto-inline">
												<select id="tipoPB" name="tipoPB" onChange="tipoPBChange()"	 class="custom-select" >
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
										  				<input class="form-check-input" type="checkbox" id="chk_extranjero" name="chk_extranjero" value="0">
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
									<input type="text" class="form-control input-sm" placeholder="RFC1" aria-label="RFC1" aria-describedby="basic-addon1" name="cIdRFC1" id="cIdRFC1" maxLength="3" 
									onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" />
									<span class="input-group-text">-</span>
									<input type="text" class="form-control" placeholder="RFC2" aria-label="RFC2" name="cIdRFC2" id="cIdRFC2" maxLength="6" 
									onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);"  />
									<span class="input-group-text">-</span>
									<input type="text" class="form-control" placeholder="RFC3" aria-label="RFC3" aria-describedby="basic-addon1" name="cIdRFC3" id="cIdRFC3" maxLength="3" 
									onKeyPress="return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);existeProveedor();"  />
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
							  		<select id="cMunicipioFiscal" name="cMunicipioFiscal" class="custom-select" onchange="actualizaLocalidad();"></select>
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
										<input type="text" class="form-control" placeholder="Tel&eacute;fono" aria-label="Tel&eacute;fono" name="cTelefono" id="cTelefono" style="width: 8em;"
										maxlength="10" onKeyPress="return onlyNumbers(event);" /> 
										<label id="labelTel"><font color="red">*</font></label>
									</div>
							  	</div>
							</div>
						</div>
			  		</fieldset>
			  	</div>
		  	</div>
		  	<div class="row" id="Motivos" style="display: none;">
			  	<div class="col-lg ">
			  		<fieldset class="form-group border p-3">
			  			<legend class="w-auto px-2">Capture los motivos de la modificaci&oacute;n</legend>
			  			<div class="row" >
							<div class="col-6">
						  		<label for="motivoModifica"> Motivos:</label>
						  	</div>
						  	<div class="col-4">
						  		<textarea class="form-control" placeholder="Motivos" id="motivoModifica" name="motivoModifica" rows="6" title="Capture los motivos de la modificaci&oacute;n"></textarea>
						  	</div>
						</div>
			  		</fieldset>
			  	</div>
			</div>
			
		</div>
  		<!-- Modal -->
		<div class="modal fade" id="modalConfirm" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="modalLabelConfirm">Motivo de Cancelación</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" id="btnAcepModalConfirm" name="btnAcepModalConfirm" onclick="aceptModal()">Aceptar</button>
		        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelModalConfirm" name="btnModalConfirm">Cancelar</button>
		      </div>
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
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=today.substring(6)%>" /> 
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
		<input type="hidden" name="nIdOperAnt" id="nIdOperAnt" value="0" />
		<input type="hidden" name="bLiberaCaso" id="bLiberaCaso" value="0" />
  	</form>
  
  </body>
</html>
