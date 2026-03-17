<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.gestion.core.Role"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.sai.contabilidad.caja.*"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	  
	  
	String cLogin = "";
	String cUR = "";
	String cCentroContable = "";	
	cLogin = usuario.getLogin();
	cUR = usuario.getU_UR();
	String AlcanceVistas=CajaBusinessLogic.LeerVistas(cLogin);
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	String fAplicacion[]=CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	Role role = usuario.getRole("CHEQUES");
	boolean imprimecheque = role!=null && "CHEQUES".equals(role.getNombre());
	
	role = usuario.getRole("ISRLAUDOS");
	boolean pagaISRLaudos = role!=null && "ISRLAUDOS".equals(role.getNombre());
	
	System.out.println(cUR);	
	
	ConfiguraAplicativoBusinessLogic configSys = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean activaPagoParcial = "S".equals( configSys.getSystemSetting("ACTIVA_PAGO_PARCIAL") );
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean esSAIAlterno = "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") ) || "true".equals( cabl.getSystemSetting("SAI_FONDEN") );
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Autorizar Generaci&oacute;n de Layout's</title>

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
	
	$(document).ready(
		function() {
		querySelectPost("CatCuentasBancarias2", "ctaBancarias", {async : false});
	});//FIN DEL READY
	
	function borraEspacios(){
		$("#cxpPorFuera").val($.trim($("#cxpPorFuera").val()));
		buscarporFuera();
		document.getElementById("fpagadoCXP").focus();
	}
	
	function buscarporFuera(){
		$("#nFolio").val("");
		$("#tipoPagoCXP").val("");
		$("#urCXP").val("");
		$("#mImporteCXP").val("");
		$("#rfcCXP").val("");
		$("#nombreCXP").val("");
		$("#fAplicacionDevCXP").val("");
		queryFormPost("buscaCXPporFueraISN",{async: false });
		if ($("#fAplicacionDevCXP").val()!=""){
			var cFecha = $("#fAplicacionDevCXP").val().split("-")[2] + "/" + $("#fAplicacionDevCXP").val().split("-")[1] + "/" + $("#fAplicacionDevCXP").val().split("-")[0];
			$("#fAplicacionDevCXP").val(cFecha);
		}
		$("#mImporteCXP").formatCurrency();
		var condicion = "cTipoPago = '"+$("#tipoPagoCXP").val()+"' AND nFolio = "+$("#nFolio").val();	
		if($("#nFolio").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Aun no se hace el traspaso de bancos para aplicar el Pagado o la CxP no corresponde al pago de ISN de Queretato, Validar con el area de Egresos la información."});
			return;
		}
	}
	
	function aplicarCXP(){
		if ($.trim($("#cxpPorFuera").val())==""){
			Swal.fire({ icon: "warning",
						text: "Favor de indicar la CXP que se va aplicar."});
			return;
		}else if($.trim($("#tipoPagoCXP").val())==""){
			Swal.fire({ icon: "warning",
						text: "No existe la CXP: "+$("#cxpPorFuera").val()});
			return;
		}else if (($.trim($("#folioSicop").val())=="" || $.trim($("#folioSicop").val())=="0" 
			|| $.trim($("#solPago").val())=="" || $.trim($("#solPago").val())=="0"
			|| $.trim($("#procesoSicop").val())=="" || $.trim($("#procesoSicop").val())=="0"
			|| $.trim($("#folioSiaff").val())=="" || $.trim($("#folioSiaff").val())=="0") 
			&& ($("#tipoPagoCXP").val()!="INTEGRACION")){
			Swal.fire({ icon: "warning",
						text: "Favor de verificar que los folios sean validos."});
			return;
		}
		
		$("#pagadoCXP").val($("#fpagadoCXP").val().split('-').reverse().join('/'));				
		$("#ejercidoCXP").val($("#pagadoCXP").val());
		
		//Validacion de fechas de aplicacion
		if ($("#tipoPagoCXP").val()!="INTEGRACION"){
			if (!validaFechas($("#fAplicacionDevCXP").val(),$("#pagadoCXP").val(),"Pagado"))
				return;
		}else
			if (!validaFechas($("#fAplicacionDevCXP").val(),$("#pagadoCXP").val(),"Integracion"))
				return;		
		
		Swal.fire({
			  html: "¿Desea aplicar el Ejercido/Pagado de " + $("#cxpPorFuera").val() + "?",					  
			  icon: 'info',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {				
				document.getElementById("esperar").style.visibility = 'visible';
				$("#cxpPorFuera").attr('disabled', true);
				$("#btn_AplicarCXP").attr("disabled","disabled");
				$.ajax({
					url:'./ejercidoPagadoValidar.jsp',
					type:'post',
					data:{
						tipo		: 'AplicarPagosFueraNomina', 
						nFolio		: $("#nFolio").val(),
						idCXP		: $("#cxpPorFuera").val(),
						tipoPago	: $("#tipoPagoCXP").val(),
						fPago		: $("#pagadoCXP").val(),			
						fEjer		: $("#ejercidoCXP").val(),
						usuario		: $("#u_Login").val(),
						ctaBancaria	: $("#ctaBancarias").val(),
						folioSICOP	: $.trim($("#folioSicop").val()),
						solPago		: $.trim($("#solPago").val()),
						numProceso	: $.trim($("#procesoSicop").val()),
						folioSIAFF	: $.trim($("#folioSiaff").val())
					},
					success: function(data){
						if(data.estatus == "correcto"){
							Swal.fire({ icon: "success",
										text: "Se aplico correctamente el Ejercido/Pagado"});
							$("#cxpPorFuera").val("");
							$("#nFolio").val("");
							$("#tipoPagoCXP").val("");
							$("#urCXP").val("");
							$("#mImporteCXP").val("");
							$("#rfcCXP").val("");
							$("#nombreCXP").val("");
							$("#folioSicop").val("");
							$("#solPago").val("");
							$("#procesoSicop").val("");
							$("#folioSiaff").val("");
							$("#fAplicacionDevCXP").val("");
						}else{
							Swal.fire({ icon: "error",
										text: "No se pudo aplicar el Ejercido/Pagado. [" + data.estatus + "]"});						
						}
						document.getElementById("esperar").style.visibility = 'hidden';
						document.getElementById("btn_AplicarCXP").disabled = false;
						$("#cxpPorFuera").attr('disabled', false);
					}
				});
			}
		})
	}
		
	function validaFechas(primera, segunda, tipo){
		var fechaValida = null;
		var dDay=Number(primera.substring(0, 2));
		var dMonth=Number(primera.substring(3, 5));  
		var dYear=Number(primera.substring(6,10));  
		var aDay=Number(segunda.substring(0, 2));  
		var aMonth=Number(segunda.substring(3, 5));  
		var aYear=Number(segunda.substring(6,10));
		if (dYear> aYear)
            fechaValida=false;
        else
         	if (dYear == aYear)
         		if (dMonth > aMonth)
         			fechaValida=false;
            	else
              		if (dMonth == aMonth)
                		if (dDay > aDay)
                  			fechaValida=false;
                		else
                  			fechaValida=true;
              		else
                		fechaValida=true;
          	else
            	fechaValida=true;
        if (!fechaValida){        	
        	Swal.fire({ icon: "warning",
						text: "La fecha de " + tipo + " no puede ser menor a " + primera});
        	return false;
        }else
        	return true;
    }

</script>
</head>
<br/>
<body id="dt_example" >
	<div id="container" class="ms-5" class="container" style="width: 90%">				
		<div class="card-header"> <h3> Ejercido Pagado Manual </h3> </div>
		<hr class="mt-3"/>
				
		<form id="ejercidoPagadoCheque" name="ejercidoPagadoCheque">
		
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
			<input id="cUnidadResponsable" name="cUnidadResponsable" type="hidden" value="<%=cUR%>">
			<input id="cCentroContable" name="cCentroContable" type="hidden" value="<%=cCentroContable%>">
			<input id="nFolio" name="nFolio" type="hidden" value="">
					
			<div class="row d-flex justify-content-center">
      							
       			<ul class="nav nav-tabs" id="list-opciones">	       				
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="LNK06" data-bs-toggle="tab" data-bs-target="#tabs-6" type="button" role="tab" aria-controls="tabs-LNK06" aria-selected="false"> ISN </button>
		            </li>
				</ul>
				
				<div align="center">
						<label id="esperar" style="visibility: hidden">	Espere por favor...
							<img border="0" src="../imagenes/espera.gif" height="30">
						</label>
				</div>
					
				<div class="tab-content mt-3" id="tabContent">													
					<div class="tab-pane fade show active" id="tabs-6" role="tabpanel" aria-labelledby="tabs-6-LNK06">
						<h5> Pagado Manual </h5>
						<hr class="mt-3"/>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="cxpPorFuera" class="form-label"> CXP: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="cxpPorFuera" name="cxpPorFuera" class="form-control form-control-sm" onchange="borraEspacios();">				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="pagadoCXP" class="form-label"> Pagado: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input name="fpagadoCXP" type="date" id="fpagadoCXP" class="form-control form-control-sm" size="10" value="<%=fAplicacion[0]%>" />
									<input name="ejercidoCXP" type="hidden" id="ejercidoCXP" value="" />
									<input name="pagadoCXP" type="hidden" id="pagadoCXP" value="" />									
								</div>						            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="tipoPagoCXP" class="form-label"> Tipo: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="tipoPagoCXP" name="tipoPagoCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="ctaBancarias" class="form-label"> Cuenta Bancaria: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<select id="ctaBancarias" name="ctaBancarias" class="form-select form-select-sm" onchange="cargaGrid();">				            		
					            </select>
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="urCXP" class="form-label"> UR: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="urCXP" name="urCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="fAplicacionDevCXP" class="form-label"> Fecha: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="fAplicacionDevCXP" name="fAplicacionDevCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="mImporteCXP" class="form-label"> Importe: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="mImporteCXP" name="mImporteCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="rfcCXP" class="form-label"> RFC: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<input type="text" id="rfcCXP" name="rfcCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="nombreCXP" class="form-label"> Nombre: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<input type="text" id="nombreCXP" name="nombreCXP" class="form-control form-control-sm" readonly>				            							            
							</div>
						</div>
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="folioSicop" class="form-label"> SICOP: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="folioSicop" name="folioSicop" class="form-control form-control-sm" onkeypress="return validarNumerico(event);" readonly>				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="solPago" class="form-label"> SOL PAGO: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="solPago" name="solPago" class="form-control form-control-sm" onkeypress="return validarNumerico(event);" readonly>				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="procesoSicop" class="form-label"> PROCESO: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="procesoSicop" name="procesoSicop" class="form-control form-control-sm" onkeypress="return validarNumerico(event);" readonly>				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="folioSiaff" class="form-label"> SIAFF: </label>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<input type="text" id="folioSiaff" name="folioSiaff" class="form-control form-control-sm" onkeypress="return validarNumerico(event);" readonly>				            							            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">			
								<input type="button" id="btn_AplicarCXP" name = "btn_AplicarCXP" onClick="aplicarCXP();" value="Aplicar" class="btn btn-secondary btn-sm"/>										            							           
							</div>
						</div>											
					</div>
					
				</div>
	       </div>			
		</form>
	</div>
		
</body>
</html>