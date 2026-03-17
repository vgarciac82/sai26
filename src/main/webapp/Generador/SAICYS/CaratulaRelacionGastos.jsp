<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	 String user_login=usuario.getLogin();
    String roles="";
    Map rol =usuario.getRoles();
    String cEjercicio = "";
	String cIdDocumento = "";
	String nIdEstado = "";
	String cCentroContable="";
		
	if (session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		
		}
	
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
	</style>
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker-es.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/validaciones.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		
		var vEstado;
	    var importeTotalDocumento;
	    		
		$(document).ready(function() {
		
		
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Relacion Gastos","CaratulaRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
		
		queryFormPost("obtieneDatosComboboxRelaciones", {async:false});
		initQueries();
		//llena caratula
	    queryFormPost("llenaCaratulaRelacionGastos", {async:false});
	    
	    activaDesactivaPestanas();
	    cambiaTipoDestino1();
		 $("input.AyudaSyC").subIniciaDlg();
		 $("#mImporteNeto").formatCurrency();
		//condiciones iniciales
		 vEstado=parseInt($("#nIdEstado").val(),10);
		 setInitConditions();
		 validaFacturasCargadas();
	
		
		$( "#btnGuardaRelacionGastos" ).button().click(function() {
		   if( vEstado==1){
		   //revisamos si cambio el tipo destino esto provocara que todas las facturas en el detalle sean borradas.
		    queryFormPost("verificaTipoDestinoRELG", {async:false});
		    if($("#tipoDestinoActual").val()!= $("#DESTINO_GASTO").val()){
		       if (!window.confirm("El tipo destino ha cambiado esto provocara que todos los detalles de las facturas sean borrados en caso de que existan, Desea Continuar?")){
		       location.reload();
		       return;
		       }
				
				else{
				//borramos todos los detalles de las facturas en caso de que existan
				queryFormPost("borraDetalleFacturasRELG", {async:false});
				}
				
				
		    }
		   
		     //revisamos que el monto total de la relacion de gastos sea igual o mayor al capturado en las facturas al momento de actualizar
		      importeTotalDocumento=parseFloat(quitaFmt($("#mImporteNeto").val()));
		      queryFormPost("verificaTotalFacturasRelacionGastosInicio", {async:false});
		       	 if(parseFloat(importeTotalDocumento) >= parseFloat(quitaFmt($("#totalFactCapturadoInicio").val()))){
			    // se agrega informacion a la tabla mRelacionGastos
			    
			        //guarda valores en campos ocultos
					 $("#ALMHidden").val($("#ALM").val());
					 $("#altaAlmacenHidden").val($("#altaAlmacen").val());
					 $("#cAnioFactEPHidden").val($("#cAnioFactEP").val());
					 $("#nFacturaEPHidden").val($("#nFacturaEP").val());
					 $("#DESTINO_GASTOHidden").val($("#DESTINO_GASTO").val());
					
				   	queryFormPost("sp_AgregaRelacionGastosActualiza",{async:false});
					alert("Se Actualizo la Relacion de Gastos Correctamente");
					queryFormPost("llenaCaratulaRelacionGastos", {async:false});
					$("#mImporteNeto").formatCurrency();
					//revisamos si es almacen 
				if($("#DESTINO_GASTO").val()=="AL"){
						//SE CHECA SI EXISTEN REGISTROS DADOS DE ALTA EN EL DETALLE
						queryFormPost("existeFacturasDetalleRELG", {async:false});
						if(parseInt($("#exixteDetalleFacturas").val(),10) > 0){
						//existen registros se tiene que actualizar el almacen si es que fuera cambiado
						queryFormPost("actualizaAlmacenFacturasDetalleRELG", {async:false});
						
						}
					
				}
				
					window.location = "RelacionGastos.jsp?tab=4&cIdDocumento="+$("#cIdRelacion").val()+"&cEjercicio="+$("#cEjercicio").val()+"&nIdEstado=1&DESTINO_GASTO="+$("#DESTINO_GASTOHidden").val()+"&tConcepto="+$("#TIPO_CONCEPTO").val();
				}
				else{
						alert("No se puede Actualizar la caratula ya que el monto que desea actualizar es menor al capturado en las facturas");
					 	queryFormPost("llenaCaratulaRelacionGastos", {async:false});
						$("#mImporteNeto").formatCurrency();
						return;
					}
						
			   }else{
				      alert("No se puede Actualizar la Caratula su estado no lo permite");
				      return;
			       	}
			    						
		});
	
				 				
		});
		
	
		
		function initQueries(){
		
			$("#cCentroContable").val( "<%=cCentroContable%>" );
			$("#fRecepcion").val( "<%=today%>" );
			querySelectPost("CatalogoObraDGastoReadRELG", "DESTINO_GASTO",{async: true });
			$("#ID_DESTINO_GASTO").val($("#DESTINO_GASTO_TMP").val())
			querySelectPost("CatalogoObraTConceptoRead", "tConcepto",{async: false });
			$("#TIPO_CONCEPTO").val($("#tConcepto_TMP").val());
			querySelectPost("CatalogoObraTMovimendoReadMateriales", "TIPO_MOVIMIENTO",{async: false });
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			queryFormPost("checaMontoTotalLineas",{async:false});
			//cambiaTipoDestino();
			if($("#cEjercicio").val() != ""){
				$("#cAnioFactEP").append("<option value='"+$("#cEjercicio").val()+"'>"+$("#cEjercicio").val()+"</option>");
				$("#cAnioFactEP").append("<option value='"+($("#cEjercicio").val()-1)+"'>"+($("#cEjercicio").val()-1)+"</option>");
			}
			 $("#mImporteNeto").formatCurrency();
			
			
		}
		
		
		
		 function setInitConditions(){
    
  		if(vEstado==3 || vEstado==4 || vEstado==6  ){
			document.getElementById("btnGuardaRelacionGastos").disabled = true;
	    }else if( vEstado==1 || vEstado==5 ){
		  document.getElementById("btnGuardaRelacionGastos").disabled = false;
		}
				
		}
		
		
			function cambiaTipoDestino(){
			$("input[id='ID_DESTINO_GASTO']").val($('#DESTINO_GASTO option:selected').val());
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
								
			if($("#DESTINO_GASTO").val() == "CD"){
				$("#altaAlmacenariaTitulo").css("display","none");
				$("#altaAlmacenariaCampos").css("display","none");	
			}
			else if($("#DESTINO_GASTO").val() == "AL"){
				$("#altaAlmacenariaTitulo").css("display","");
				$("#altaAlmacenariaCampos").css("display","");
			}
			
			concepto();
		}
		
		
		function cambiaTipoDestino1(){
		
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
								
			if($("#DESTINO_GASTO").val() == "CD"){
				$("#altaAlmacenariaTitulo").css("display","none");
				$("#altaAlmacenariaCampos").css("display","none");	
			}
			else if($("#DESTINO_GASTO").val() == "AL"){
				$("#altaAlmacenariaTitulo").css("display","");
				$("#altaAlmacenariaCampos").css("display","");
			}
			
						
			
			}
		
	
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		
    function concepto(){
		$("#TIPO_CONCEPTO").val($("#tConcepto").val());
		querySelectPost("CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
		if ($("#tConcepto").val() == "AL"){
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			$("#altaAlmacen").val("");
			$("#altaAlmacen").show();
			$("#ALM").show();
			$("#cAnioFactEP").val("");
			$("#nFacturaEP").val("");
			$("#cAnioFactEP").show(); 
			$("#nFacturaEP").show();
		}
		else{
			$("#altaAlmacen").val("");
			$("#altaAlmacen").hide();
			querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
			$("#ALM").hide();
			$("#cAnioFactEP").val("");
			$("#nFacturaEP").val("");
			$("#cAnioFactEP").hide(); 
			$("#nFacturaEP").hide();

		}
		
	}	
	
	function cambiafrmt(fld){
   		$("#"+fld).formatCurrency();
	}
	
	   
   function deshabilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",true);
	$("#partidasRelacionGastos").attr("disabled",true);
	$("#pagosRelacionGastos")	.attr("disabled", true);
	}
	
	 function habilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",false);
	$("#partidasRelacionGastos").attr("disabled",false);
	$("#pagosRelacionGastos").attr("disabled", false);
	}
	
	
	function activaDesactivaPestanas(){ 
	    var umbral;
	    importeTotalDocumento=parseFloat(quitaFmt($("#mImporteNeto").val()));
		 //verificar que el monto ingresado por linea no sobrepase el total que se ingreso en la caratula
		 queryFormPost("verificaTotalFacturasRelacionGastosInicio", {async:false});
		 queryFormPost("checaMontoCapturadoFacturaDetalleTotal", {async:false});
		 queryFormPost("verificaMontosTotalesPartidasRELG", {async:false});
		  deshabilitaTabs();
		  if(parseFloat(quitaFmt($("#totalFactCapturadoInicio").val())) == parseFloat(importeTotalDocumento)){
		  if(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) == parseFloat(importeTotalDocumento)){
		      $("#partidasRelacionGastos").attr("disabled",false);
		       umbral = Math.abs(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) - parseFloat($("#totalSumaPartidasRELG").val()));
	 	       if(umbral > .03){
				$("#apartadoRelacionGastos").attr("disabled",true);
	            $("#pagosRelacionGastos").attr("disabled",true);
	           	}else{
				habilitaTabs();
	            }
			    
	     }  
		 }
	    
	}
	
		function validaFacturasCargadas(){
		 queryFormPost("verificaExistenFacturasCapturadas", {async:false});
		 if(parseInt($("#existenFacturasCapturadas").val(),10) > 0){
		 //hay facturas capturadas en el detalle no se permite cambiar almacen y año de factura
		// $("#ALM").attr("disabled",true);
		 $("#altaAlmacen").attr("disabled",true);
		 //$("#cAnioFactEP").attr("disabled",true);
		 $("#nFacturaEP").attr("disabled",true);
		// $("#DESTINO_GASTO").attr("disabled",true);
		 }
		 
		//guarda valores en campos ocultos
		 $("#ALMHidden").val($("#ALM").val());
		 $("#altaAlmacenHidden").val($("#altaAlmacen").val());
		 $("#cAnioFactEPHidden").val($("#cAnioFactEP").val());
		 $("#nFacturaEPHidden").val($("#nFacturaEP").val());
		 $("#DESTINO_GASTOHidden").val($("#DESTINO_GASTO").val());
		
		}
		
		
		
		function copiarRelacionGastos(){
		//checa que sea el usuario creador el que puede realizar la reactivacion de la relacion de gastos
		queryFormPost("verificaUsuarioCreacionRELG", {async:false});
		if($("#usuarioCreacionRELG").val()== $("#cIdUsuario").val()){
		//se revisa que la relacion de gastos este aprobada y cancelada
		queryFormPost("verificaRelacionGastosCanceladaFinanciero", {async:false});
			if( $("#documentoAplicadoOld").val()=="C" && parseInt($("#estadoOld").val(),10)==4){
			
			 if (!window.confirm("Esta seguro de realizar la reactivacion de la Relacion de Gastos este proceso no podra ser cancelado , Desea Continuar?")){
		       location.reload();
		       return;
		       }
		
		//se tiene que crear un nuevo folio de gestion
		  	if($("#nFolioRelacionGastosNew").val() == "")
					$.ajax({url: "../../servlet/RelacionGastosServlet" , type:'post' , async: false,data: 'operacion=1', dataType: 'json', success: guardaFolio});		
					//En caso de cualquier error al obtener el folio, sale de la función
					if($("#nFolioRelacionGastosNew").val() == 0 || $("#nFolioRelacionGastosNew").val() == ""){
					alert("No se pudo generar el Folio del Caso");
					return -1;
					}
		        
		          //actualiza el folio en las tablas correspondientes
		  		$.ajax({url: '../../servlet/RelacionGastosServlet?cIdDocumento='+$("#cIdFolio").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nFolioRelacionGastosNew="+$("#nFolioRelacionGastosNew").val()+"&nFolioRelacionGastosOld="+$("#nFolioConsecutivoRelacionGastosCaratula").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoRelacionGastosNew="+$("#folioCasoRelacionGastosNew").val(), type:'post' , async: false,data:'operacion=4', dataType: 'json', success: 
				function(j){
				res=j[0].Contable1;
				if(res!="-1"){
				alert("Se Realizo con Exito la Copia de la Relación de Gastos");
				//se constuye link donde va a ir la copia
				//Bitácora
				$("#cAccion").val("REALIZA_COPIA_RELACION DE GASTOS");
				$("#cIdDocumento").val($("#cIdFolio").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				queryFormPost("fn_ConstruyeLinkRELGCopia",{async : false});
				link='RelacionGastos.jsp?tab=4&cEjercicio='+$("#cEjercicio").val()+'&cIdDocumento='+$("#cIdFolio").val()+'&nIdEstado='+$("#nIdEstadoLINK").val()+'&DESTINO_GASTO=' +$("#DESTINO_GASTOLINK").val()+'&tConcepto='+$("#tConceptoLINK").val();
				window.location=link;
				}else{
					alert("No se realizo con exito la copia de la  Relación de Gastos intentelo nuevamente");
					return;
					}
				}	
			});    
		
			
			}else{
			alert("Para que pueda Copiarse la Relacion de Gastos debe estar Cancelada por el Modulo Financiero y debe de estar Aprobada en el modulo de Materiales.");
			return;
			 
			}
		
		}else{
		alert("Solo el usuario creador de la Relacion de Gastos puede realizar la Reactivacion,el usuario creador es: "+$("#nombreUsuarioRELG").val()+"");
		return;
		}	    
		  
		
		
		}
		
		 function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioRel=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioRel==-1){
      		alert("Ha ocurrido un error al crear el caso, contacte a su soporte");
      		return -1;
        }else{
     	   $("#nFolioRelacionGastosNew").val(folioRel);
     	   $("#folioCasoRelacionGastosNew").val(folioCaso);
     	}
	}
		
		
	</script>
</head> 
<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Informaci&oacute;n de la Relaci&oacute;n de Gastos</legend>
				<table border="0" align="center" width="100%">
					
					<tr align="left">
						<td colspan="2">
							<input name="lblcIdDocumento" id="lblcIdDocumento" type="text" style="width: 600px"  style="border: 0px solid black;" readonly="readonly"/>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" style="width: 600px" readonly="readonly" style="border: 0px solid black;"></input>  
						</td>								
					</tr>
					
					<tr align="left">
						<td colspan="2">
							<input name="lblnIdEstado" id="lblnIdEstado" type="text" readonly="readonly" style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					 <tr>
							<td align="left">
								Importe Neto Relacion Gastos:
								<input type="text" style="width: 500px" name="lblImporteNeto"
									id="lblImporteNeto" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					<tr>
							<td align="left">
								<!-- Importe Neto Partidas: -->
								<input type="hidden" style="width: 500px" name="montoNetoRELG"
									id="montoNetoRELG" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					
					</table>
				</fieldset>
					
					<br/>
					<table align="center" cellpadding="2" width="100%">
				    	<tr>
				    		<td align="right" colspan="2">
				    		     <img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="copiarRelacionGastos();"/>&nbsp;Reacivar Relacion Gastos
								
				    		</td>
				    	</tr>
				    	</table>
								
					<br/>
					
					<table width="100%" align="left">
				<tr>
					<td>
						<fieldset>
					<legend>Datos Generales</legend>
					<table align="left" cellpadding="2" width="100%">
							<tr align="left">
							<td>
								Tipo de Relacion Gastos: &nbsp;&nbsp;&nbsp;&nbsp;										
										<select id="TIPO_OPERACION" name="TIPO_OPERACION">
												<option value="2">REEMBOLSOS O COMPROBACION
												</option>
											</select>
										
								
									
							</td>
						</tr>
						<tr align="left">
							<td>
								R.F.C.: &nbsp;&nbsp;&nbsp;&nbsp;<input readonly="readonly" type="text" maxlength="15" size="15" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto" class="AyudaSyC obligatorio" /> 
								&nbsp;&nbsp;<input readonly="readonly" type="text" maxlength="100"
									size="70" name="cnombre"   class="paso01" ID="cnombre" />
							</td>
						</tr>
						<tr align="left">
							<td>
								<table>
									<tr>
										<td style="width:80px;">
											Fecha Recepci&oacute;n:
										</td>
										<td>
											<input name="fRecepcion" type="text" id="fRecepcion" size="10" name="fRecepcion"  readonly="readonly">
										</td>
										<td>
											Tipo Destino:
										</td>
										<td style="width:180px;">
											<select class="paso01" id="DESTINO_GASTO" name="DESTINO_GASTO" onChange="cambiaTipoDestino();"></select>
										</td>
										<td>
											Fuentes de Financiamiento:
										</td>
										<td>
											<select id="TFONDO" name="TFONDO">
												<option value="FF">Fondos Fiscales
												</option>
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr align="left">
							<td>
								&nbsp;
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		
		<tr>
			<td>
				<fieldset>
					<legend>Concepto</legend>
					<br/>
					
					<table align="left">
						<tr>
							<td align="left">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="left">
											Concepto:
										</td>
									</tr>
									<tr>
										<td align="left">
											<textarea name="cConcepto" rows="8" style="width:250px;" ID="cConcepto" onkeypress="valFmt(this,15)"></textarea>
										</td>
									</tr>	
								</table>
							</td>
							<td>
								<table>
									<tr>
										
										<td align="left">
											Importe Neto:
										</td>
										<td  align="left">
											<input type="text" id="mImporteNeto" name="mImporteNeto" class="subtotall" onKeyPress="valFmt(this,9)" onblur="cambiafrmt(this.name);"  value="0" size="15" maxlength="15" style="text-align: right;"/>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					
				</fieldset>
			</td>
		</tr>
		
		
		
		<tr>
			<td align="left">
				<fieldset>
					<legend>Movimiento</legend>
					<table>
						<tr align="left">
							<td>Tipo de Concepto</td>
							<td colspan="2">Tipo De Movimiento</td>
							<td>&nbsp;</td>
						</tr>
						<tr align="left">
							<td>
								<select id="tConcepto" name="tConcepto" style="width: 10em;" onChange="concepto();">
								<option value="" selected="selected"></option>
								</select>
							
							</td>
							<td colspan="2">
								<select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" style="width: 20em;">
								<option value="" selected="selected"></option>
								
								</select>
							</td>
							<td>&nbsp;</td>
						</tr>
						<tr align="left" id="altaAlmacenariaTitulo">
							<td>Almacén</td>
							<td>Alta almacenaria</td>
							<td>Año</td>
							<td>Factura</td>
						</tr>
						<tr align="left" id="altaAlmacenariaCampos">
							<td style="width:250px;">
								<select id="ALM" name="ALM"></select>
							</td>
							<td>
								<input type="text" id="altaAlmacen" name="altaAlmacen"  style="text-align: right;" onKeyPress="valFmt(this,9)" value="0" size="12" maxlength="12">
							</td>
							<td style="width:80px;"><select id="cAnioFactEP" name="cAnioFactEP"></select></td>
							<td><input type="text" id="nFacturaEP" name="nFacturaEP" style="text-transform:uppercase" size="12" maxlength="12"></td>
						</tr>
					</table>
				</fieldset>
				<br/>
									
				<fieldset>
					<legend>Motivo Rechazo</legend>
					<br/>
					
					<table align="left">
						<tr>
							<td align="left">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="left">
											Rechazo:
										</td>
									</tr>
									<tr>
										<td align="left">
											<textarea name="cConceptoRechazo" rows="8" style="width:250px;" ID="cConceptoRechazo" readonly="readonly"></textarea>
										</td>
									</tr>	
								</table>
							</td>
							</tr>
					</table>
					
				</fieldset>
		
					<br/>
					<br/>
						
					<div>
					       <table align="center" >
								<tr>
									<td width="33%">&nbsp;</td>
									<td width="33%" align="center"><input type="button" id="btnGuardaRelacionGastos" value="GUARDAR"/></td>
									<td width="33%" align="right">&nbsp;</td>
								</tr>
							</table>
					
					</div>	
				
				<br/>
				<br/>
				
			</td>
		</tr>
	</table>
	   
		<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%=cEjercicio%>" />
		<input id="cIdDocumentoCaratula" name="cIdDocumentoCaratula" type="hidden" size="4" value="<%=cIdDocumento%>" />
		<input id="nIdEstadoCaratula" name="nIdEstadoCaratula" type="hidden" size="4" value="<%=nIdEstado%>" />
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=usuario.getU_UR() %>" />
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
		<input id="cIdRelacion" name="cIdRelacion" type="hidden" size="10" value="<%=cIdDocumento%>" />
		<input id="cIdFolio" name="cIdFolio" type="hidden" size="10" value="<%=cIdDocumento%>" />
		
		<input type="hidden" name="ID_DESTINO_GASTO" id="ID_DESTINO_GASTO" />
		<input type="hidden" id="cCentroContable" name="cCentroContable" />
		<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" />
		<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
		
		<input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="RELGASTO" />
		<input type="hidden" id="cDocumento" name="cDocumento" value="RELACIONGASTOS">
		<input type="hidden" id="folioRelacion" name="folioRelacion" value="">
		<input type="hidden" id="nIdEstado" name="nIdEstado" value="1">
		
		
		<input type="hidden" id="DESTINO_GASTO_TMP" name="DESTINO_GASTO_TMP" value="">
		<input type="hidden" id="tConcepto_TMP" name="tConcepto_TMP" value="">
		<input type="hidden" id="TIPO_MOVIMIENTO_TMP" name="TIPO_MOVIMIENTO_TMP" value="">
		<input type="hidden" id="montoNetoRELG" name="montoNetoRELG" />
		<input type="hidden" id="nFolioConsecutivoRelacionGastosCaratula" name="nFolioConsecutivoRelacionGastosCaratula" />
		<input type="hidden" id="mTotalFacturaDetalleTotal" name="mTotalFacturaDetalleTotal" />
		<input type="hidden" id="totalFactCapturadoInicio" name="totalFactCapturadoInicio" />
		<input type="hidden" id="existenFacturasCapturadas" name="existenFacturasCapturadas" />
		<input type="hidden" id="totalSumaPartidasRELG" name="totalSumaPartidasRELG" />
		<input type="hidden" id="nFolioRelacionGastos" name="nFolioRelacionGastos"/>
	    <input type="hidden" id="folioCasoRelacionGastos" name="folioCasoRelacionGastos"/>
	    <input type="hidden" id="nFolioRelacionGastosNew" name="nFolioRelacionGastosNew"/>
	    <input type="hidden" id="folioCasoRelacionGastosNew" name="folioCasoRelacionGastosNew"/>
	    		
		<!-- auxiliares para la actualizacion -->
		<input type="hidden" id="ALMHidden" name="ALMHidden" />
		<input type="hidden" id="altaAlmacenHidden" name="altaAlmacenHidden" />
		<input type="hidden" id="cAnioFactEPHidden" name="cAnioFactEPHidden" />
		<input type="hidden" id="nFacturaEPHidden" name="nFacturaEPHidden" />
		<input type="hidden" id="DESTINO_GASTOHidden" name="DESTINO_GASTOHidden" />
		<input type="hidden" id="tipoDestinoActual" name="tipoDestinoActual" />
		<input type="hidden" id="exixteDetalleFacturas" name="exixteDetalleFacturas" />
		
		
		
		<!-- bitacora -->
		<input type="hidden" id="cAccion" name="cAccion" />
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" />
		
		<!-- copia relacion gastos -->
		<input type="hidden" id="documentoAplicadoOld" name="documentoAplicadoOld" />
		<input type="hidden" id="estadoOld" name="estadoOld" />
		<input type="hidden" id="DESTINO_GASTOLINK" name="DESTINO_GASTOLINK" />
		<input type="hidden" id="nIdEstadoLINK" name="nIdEstadoLINK" />
		<input type="hidden" id="tConceptoLINK" name="tConceptoLINK" />
		<input type="hidden" id="usuarioCreacionRELG" name="usuarioCreacionRELG" />
		<input type="hidden" id="nombreUsuarioRELG" name="nombreUsuarioRELG" />
		
		
		
		
		</form>				
	</body>
</html>
