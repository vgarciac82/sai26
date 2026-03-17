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
		<title>Cierre Presupuestal Devengado</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

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

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			
			queryFormPost("tAcumuladoCancelaDevengadoRead", {async: false});
			queryFormPost("CancelaDevengadoREAD", {async: false});
			
			$("#lblLeyenda").text("El total Cuentas a Cancelar de Devengado es de : " + $("#nCuantas").val() + " cuentas, con un monto de acumulado de : " + $("#mMonto").val());
						
			$('#pbCerrar').button().click( function() {
	
				if ( $("#mMonto").val() != "$0.00" ){
					if(confirm("¿Esta seguro que desea correr el proceso \rCierre Presupuestal Devengado?")){	
						$("#pbCerrar").css("visibility","hidden");
						$("#pbRestaurar").css("visibility","hidden");
						$("#lblProcesa").css("visibility","visible");
						var szWhere = "TODO";
						var szTabla = "CANCELADEVENGADOANUAL";                                                                                         
						$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{   
							if (j[0].Col0 != "0"){
								aplicarContablemente( j[0].Col1 )
								$("#nFolioDevengado").val( j[0].Col2 );
							}
							else
								alert("Cierre Presupuestal de Devengado ya Procesado");
							
	    					$("#pbCerrar").css("visibility","visible");
	    					$("#pbRestaurar").css("visibility","visible");
							$("#lblProcesa").css("visibility","hidden");
						});   
					}	
				}else{
					alert("Cierre Presupuestal Devengado ya fue Procesado Previamente");
				}	
			});
			
			$('#pbRestaurar')
				.button()
				.click( function() {
					
					if ( $("#mMonto").val() == "$0.00" ){
						if(confirm("¿Esta seguro que desea Restaurar los Devengado?")){	
							$("#pbCerrar").css("visibility","hidden");
							$("#pbRestaurar").css("visibility","hidden");
							$("#lblProcesa").css("visibility","visible");
							$("#nFolioAdefa").val( $("#nFolioDevengado").val() );
							$("#tipoDocumento").val( 'CANCELADEVENGADO' );
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
											queryFormPost("CancelaDevengadoREAD", {async: false});
											$("#nFolioDevengado").val( "0" );
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
		

	function aplicarContablemente( caNoContrarrecibo ){
			
			var campo = "nFolioCancelaDevengado" ; 
			var tablaEnc = "tCancelaDevengadoEncabezado";
			var campoCondicion = "caNoContrarrecibo" ; 
			var tablaDet = "tCancelaDevengadoDetalle";
			var tipoAplicar = "CANCELADEVENGADO";

			var tipo = "aplicarMotor";
			$("#lblProcesa").css("visibility","visible");
			
			$.ajax({
				url:'./cierrePresupuestal.jsp',
				type:'post',
				dataType: 'json',
				async: false, 
				data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo,campo:campo,tablaEnc:tablaEnc,campoCondicion:campoCondicion,tablaDet:tablaDet,tipoAplicar:tipoAplicar},
				success:function(data){
					if(data.sinSesion == 'sinSesion'){
						location.href = "../index.jsp";
					}
					if(data.estatus == "guardado"){
						queryFormPost("tAcumuladoCancelaDevengadoRead", {async: false});
						$("#lblLeyenda").text("El total cuentas a cancelar de Compromiso es de : " + $("#nCuantas").val() + " cuentas, con un monto de acumulado de : " + $("#mMonto").val());
						queryFormPost("CancelaDevengadoREAD", {async: false});
						
    					alert("Cierre Presupuestal de Devengado Aplicado Correctamente " + caNoContrarrecibo);
					}else{
						alert("Documento NO pudo ser Aplicado " + data.estatus);
						//queryFormPost("tCancelaCompromisoDetDelete, tCancelaCompromisoEncDelete", {async: false});
					}
				}
			});
				
		$("#lblProcesa").css("visibility","hidden");
	}
		


</script>
		

</head>

<body id="dt_example" >
	<form> 
		<input id="nCuantas" name="nCuantas" type="hidden" >
		<input id="mMonto" name="mMonto" type="hidden" >
		<input id="nFolioAdefa" name="nFolioAdefa" type="hidden" >
		<input id="tipoDocumento" name="tipoDocumento" type="hidden" >
		<input id="nFolioDevengado" name="nFolioDevengado" value="0" type="hidden" >
		<input id="cDocumentoHaplicado" name="cDocumentoHaplicado"  value="" type="hidden" >
		<div id="container" class="container">	
			<h1>Cancelación de Devengado Anualizado</h1>
				<span id="lblLeyenda" >cargando ...</span><br/><br/>
				<input type="button" id="pbCerrar" value="Cancelar Devengado" />&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbRestaurar" value="Restaurar Devengado" />&nbsp;&nbsp;&nbsp;
			<br/><br/>
			<span id="lblProcesa" style="visibility:hidden">Procesando, por favor espere....</span>
			<br/>
		</div>
	</form>
	</body>
</html>