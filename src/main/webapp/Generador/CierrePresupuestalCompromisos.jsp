<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cierre Presupuestal Compromisos</title>
				
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			
			queryFormPost("tAcumuladoCancelaCompromisoRead", {async: false});
			queryFormPost("CancelaCompromisoREAD", {async: false});
			
			$("#lblLeyenda").text("El total cuentas a cancelar de Compromiso es de : " + $("#nCuantas").val() + " cuentas, con un monto de acumulado de : " + $("#mMonto").val());
						
			$('#pbCerrar').button().click( function() {
	
				if ( $("#mMonto").val() != "$0.00" ){
					if(confirm("¿Esta seguro que desea correr el proceso \rCierre Presupuestal Compromisos?")){	
						$("#pbCerrar").css("visibility","hidden");
						$("#pbRestaurar").css("visibility","hidden");
						$("#lblProcesa").css("visibility","visible");
						var szWhere = "TODO";
						var szTabla = "CANCELACOMPROMISOANUAL";                                                                                         
						$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{   
							if (j[0].Col0 != "0"){
								aplicarContablemente( j[0].Col1 )
								$("#nFolioCompromiso").val( j[0].Col1 );
							}
							else
								alert("Cierre Presupuestal de Compromisos ya Procesado");
							
	    					$("#pbCerrar").css("visibility","visible");
	    					$("#pbRestaurar").css("visibility","visible");
							$("#lblProcesa").css("visibility","hidden");
						});   
					}	
				}else{
					alert("Cierre Presupuestal ya Procesado Previamente");
				}	
			});
			
			$('#pbRestaurar')
				.button()
				.click( function() {
					
					if ( $("#mMonto").val() == "$0.00" ){
						if(confirm("¿Esta seguro que desea Restaurar los Compromisos?")){	
							$("#pbCerrar").css("visibility","hidden");
							$("#pbRestaurar").css("visibility","hidden");
							$("#lblProcesa").css("visibility","visible");
							$("#nFolioAdefa").val( $("#nFolioCompromiso").val() );
							$("#tipoDocumento").val( 'CANCELACOMPROMISO' );
							$("#lblProcesa").css("visibility","visible");
	
							var nFolioPagado = $("#nFolioAdefa").val();
							var cTipoDocto = $("#tipoDocumento").val();
							$.ajax({
								url: '../Generador/cierrePresupuestal.jsp',
								type: 'post',
								dataType: 'json',
								async: false, 
								data: {tipo:'cancelaEjercidoPagado', nFolioAdefa:nFolioPagado, tipoDocumento:cTipoDocto},
								success: function(data){
										if(data.sinSesion == "sinSesion"){
											location.href = "../index.jsp";
										}else if(data.estatus == "guardado"){
											//queryFormPost("tCancelaCompromisoDetDelete, tCancelaCompromisoEncDelete", {async: false});
											queryFormPost("CancelaCompromisoREAD", {async: false});
											$("#nFolioCompromiso").val( "0" );
											$("#lblLeyenda").text("El total cuentas a cancelar de Compromiso es de : " + $("#nCuantas").val() + " cuentas, con un monto de acumulado de : " + $("#mMonto").val());
											alert("Compromisos Restaurados");     									
										}else{
											alert( "No se pudo Restaurar el Documento, " + data.estatus  );
										}
										$("#lblProcesa").css("visibility","hidden");
										
								}
							});
						    $("#pbCerrar").css("visibility","visible");
		   					$("#pbRestaurar").css("visibility","visible");
							$("#lblProcesa").css("visibility","hidden");
						}
					}else{
						alert( "No se ha Realizado el Proceso de Cierre Presupuestal Compromisos" );
					}
				} );
		});
		

	function aplicarContablemente(documento){
			
			var caNoContrarrecibo = "";
			var tipo = "";
			
			caNoContrarrecibo = documento;
			tipo = "aplicarCierreCompromiso";
			$("#lblProcesa").css("visibility","visible");
			
			$.ajax({
				url:'./cierrePresupuestal.jsp',
				type:'post',
				dataType: 'json',
				async: false, 
				data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo},
				success:function(data){
					if(data.sinSesion == 'sinSesion'){
						location.href = "../index.jsp";
					}
					if(data.estatus == "guardado"){
						queryFormPost("tAcumuladoCancelaCompromisoRead", {async: false});
						$("#lblLeyenda").text("El total cuentas a cancelar de Compromiso es de : " + $("#nCuantas").val() + " cuentas, con un monto de acumulado de : " + $("#mMonto").val());
						queryFormPost("CancelaCompromisoREAD", {async: false});
						
    					alert("Cierre Presupuestal de Compromisos Aplicado Correctamente " + caNoContrarrecibo);
					}else{
						alert("Saldo Insuficiente");
						//queryFormPost("tCancelaCompromisoDetDelete, tCancelaCompromisoEncDelete", {async: false});
					}
				}
			});
				
		$("#lblProcesa").css("visibility","hidden");
	}
		


</script>
		

</head>

<body id="dt_example" >
<br/>
	<form> 
		<input id="nCuantas" name="nCuantas" type="hidden" >
		<input id="mMonto" name="mMonto" type="hidden" >
		<input id="nFolioAdefa" name="nFolioAdefa" type="hidden" >
		<input id="tipoDocumento" name="tipoDocumento" type="hidden" >
		<input id="nFolioCompromiso" name="nFolioCompromiso" value="0" type="hidden" >
		<input id="cDocumentoHaplicado" name="cDocumentoHaplicado"  value="" type="hidden" >
		
		<div id="container" class="container">
				
			<div class="card-header"> <h3> Cancelación de Compromisos Anualizado </h3> </div>
			<hr class="mt-3"/>
			
			<span id="lblLeyenda" >cargando ...</span><br/><br/>
			
			<div class="form-group">
				<div class="row">
					<div class="input-group">		
						<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
						</div>					
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">																																	
							<input type="button" id="pbCerrar" name="pbCerrar" value="Cancelar Compromisos" class="btn btn-secondary btn-sm"/>
						</div>		
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">
							<input type="button" id="pbRestaurar" name="pbRestaurar" value="Restaurar Compromisos" class="btn btn-secondary btn-sm"/>									
						</div>																					
					</div>
				</div>
			</div>
			
			<br/>
			
			<span id="lblProcesa" style="visibility:hidden">Procesando, por favor espere....</span>
			
		</div>
	</form>
	</body>
</html>