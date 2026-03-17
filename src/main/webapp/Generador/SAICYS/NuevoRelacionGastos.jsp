<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map rol =usuario.getRoles();
    String cCentroContable = "";
    
    if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else{
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Nuevo Relación Gastos</title>
	
	<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="CotizacionProcedimiento">
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
		
		
		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
		</style>

		<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			

			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>
	
	
	
	
	
	
	
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
			Map botones=nb.getBotones(roles,"RelacionGastos","NuevoRelacionGastos");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
			}
		%>
		
		
			initQueries();
			$("input.AyudaSyC").subIniciaDlg();
			$("#mImporteNeto").formatCurrency();
			
			$( "#btnGuardaRelacionGastos" ).button().click(function() {
			//primero se checa que el folio de la relacion no exista en la tabla
			queryFormPost("checaFolioRelacionExistente",{async:false});
			 if($("#folioRelacion").val() > "0"){
				alert("El numero de folio de la Relacion de Gastos ya existe,o fue ingresada directamente en la ventanilla de pagos, favor de ingresar un folio diferente");
				return;
				}
			
			    
				  //se checa que el importe neto sea mayor a 0
				  if(parseFloat(quitaFmt($('#mImporteNeto').val()))<= 0.00){
			       alert("El importe neto debe de ser mayor a 0.00");
			       return;
			       }
				    
						//se checa que todos los campos esten llenos
			
			            var cIdRelacion=$('#cIdRelacion');
						var tipo_operacion=$('#TIPO_OPERACION');
						var cIdRFC=$('#cIdRFC_RelacionGasto');
						var razon_social=$('#cnombre');
						var fRecepcion = $('#fRecepcion');
						var tipo_destino=$('#DESTINO_GASTO');
						var fuente_financiamiento = $('#TFONDO');
						var cConcepto = $('#cConcepto');
						var mImporteNeto = $('#mImporteNeto');
						//var altaAlmacen = $('#altaAlmacen');
						//var nFacturaEP = $('#nFacturaEP');
						
						
						
						if($("#DESTINO_GASTO").val() == "AL"){
						
						allFields = $( [] ).add(cIdRelacion) .add(tipo_operacion).add(cIdRFC).add(razon_social).add(fRecepcion).add(tipo_destino).add(fuente_financiamiento).add(cConcepto).add(mImporteNeto).add(cConcepto),
						tips = $( ".validateTips" );
						var bValid = true;
					    tips.text("");
					    allFields.removeClass( "ui-state-error" );
					    
					    bValid = bValid&& checkRequerido(cIdRelacion, "Numero de Folio de la Relacion Gastos");
						bValid = bValid&& checkRequerido(tipo_operacion, "Tipo de Relación de Gastos");
						bValid = bValid&& checkRequerido(cIdRFC, "RFC de la Relacion Gastos");
						bValid = bValid&& checkRequerido(razon_social, "Razon Social de Relación de Gastos");
						bValid = bValid&& checkRequerido(fRecepcion, "Fecha de Recepcion de la Relacion Gastos");
						bValid = bValid&& checkRequerido(tipo_destino, "Tipo Destino de la Relación de Gastos");
						bValid = bValid&& checkRequerido(fuente_financiamiento, "Fuente de Financiamiento de la Relación de Gastos");
						bValid = bValid&& checkRequerido(cConcepto, "Concepto de la Relacion Gastos");
						bValid = bValid&& checkRequerido(mImporteNeto, "Importe Neto de la Relación de Gastos");
					      //si existe un campo vacio ser regresa
				   			 if(!bValid)
				    			return;
					    				
						}else{
						
						allFields = $( [] ).add(cIdRelacion) .add(tipo_operacion).add(cIdRFC).add(razon_social).add(fRecepcion).add(tipo_destino).add(fuente_financiamiento).add(cConcepto).add(mImporteNeto).add(cConcepto),
						tips = $( ".validateTips" );
						var bValid = true;
					    tips.text("");
					    allFields.removeClass( "ui-state-error" );
					    
					    bValid = bValid&& checkRequerido(cIdRelacion, "Numero de Folio de la Relacion Gastos");
						bValid = bValid&& checkRequerido(tipo_operacion, "Tipo de Relación de Gastos");
						bValid = bValid&& checkRequerido(cIdRFC, "RFC de la Relacion Gastos");
						bValid = bValid&& checkRequerido(razon_social, "Razon Social de Relación de Gastos");
						bValid = bValid&& checkRequerido(fRecepcion, "Fecha de Recepcion de la Relacion Gastos");
						bValid = bValid&& checkRequerido(tipo_destino, "Tipo Destino de la Relación de Gastos");
						bValid = bValid&& checkRequerido(fuente_financiamiento, "Fuente de Financiamiento de la Relación de Gastos");
						bValid = bValid&& checkRequerido(cConcepto, "Concepto de la Relacion Gastos");
						bValid = bValid&& checkRequerido(mImporteNeto, "Importe Neto de la Relación de Gastos");
					     //si existe un campo vacio ser regresa
				   			 if(!bValid)
				    			return;
					    
						}
						
					if($("#nFolioRelacionGastos").val() == "")
					$.ajax({url: "../../servlet/RelacionGastosServlet" , type:'post' , async: false,data: 'operacion=1', dataType: 'json', success: guardaFolio});		
					//En caso de cualquier error al obtener el folio, sale de la función
					if($("#nFolioRelacionGastos").val() == 0 || $("#nFolioRelacionGastos").val() == ""){
					alert("No se pudo generar el Folio del Caso");
					return -1;
					}
				  // se agrega informacion a la tabla mRelacionGastos
					queryFormPost("sp_AgregaRelacionGastos",{async:false});
					alert("Se agrego correctamente la Relacion de Gastos");
					window.location = "RelacionGastos.jsp?tab=3&cIdDocumento="+$("#cIdRelacion").val()+"&cEjercicio="+$("#cEjercicio").val()+"&nIdEstado=1&DESTINO_GASTO="+$("#DESTINO_GASTO").val()+"&tConcepto="+$("#TIPO_CONCEPTO").val();
					
	
			});
			
			
		});
		
		   function initQueries(){
			$("#cCentroContable").val( "<%=cCentroContable%>" );
			$("#fRecepcion").val( "<%=today%>" );
			//querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO",{async: false });
			querySelectPost("CatalogoObraDGastoReadRELGMAT", "DESTINO_GASTO",{async: true });
			
			
			$("#ID_DESTINO_GASTO").val( $("#DESTINO_GASTO").val() )
			querySelectPost("CatalogoObraTConceptoRead", "tConcepto",{async: false });
			cambiaTipoDestino();
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			querySelectPost("CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			if($("#cEjercicio").val() != ""){
				$("#cAnioFactEP").append("<option value='"+$("#cEjercicio").val()+"'>"+$("#cEjercicio").val()+"</option>");
				$("#cAnioFactEP").append("<option value='"+($("#cEjercicio").val()-1)+"'>"+($("#cEjercicio").val()-1)+"</option>");
			}
			
			$("#altaAlmacenariaTitulo").css("display","none");
			
		}
		
		
		function cambiaTipoDestino(){
		
			$("input[id='ID_DESTINO_GASTO']").val($('#DESTINO_GASTO option:selected').val());
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
			
			if($("#DESTINO_GASTO").val() == "CD"){
				$("#altaAlmacenariaTitulo").css("display","none");
				$("#altaAlmacenariaCampos").css("display","none");	
			}
			else if($("#DESTINO_GASTO").val() == "AL"){
				$("#altaAlmacenariaTitulo").css("display","block");
				$("#altaAlmacenariaCampos").css("display","block");
			}
			
			concepto();
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
     	   $("#nFolioRelacionGastos").val(folioRel);
     	   $("#folioCasoRelacionGastos").val(folioCaso);
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
	
	
			function checkRequerido(o, n) {
			var sTemp = $.trim(o.val());
			o.val(sTemp);
			if (sTemp.length == 0) {
				o.addClass("ui-state-error");
				updateTipsDlg(n + " es un dato requerido.");
				o.focus();
				return false;
			} else {
				return true;
			}
		}
			
		function updateTipsDlg(t) {
			tips.text(t);
			alert(t);
		}
		
		
		function cambiafrmt(fld){
	   		$("#"+fld).formatCurrency();
		}
		
		function validarConcepto(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		patron = /[A-Za-z.\d\s\\-]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
		}	
		
		
		
	</script>
</head> 
<body >
<form>
	<table width="100%" align="left">
		<tr>
			<td>
				<fieldset>
					<legend>Datos Generales</legend>
					<table align="left" cellpadding="2" width="100%">
						<tr align="left">
							<td>
								&nbsp;
							</td>
						</tr>
						<tr align="left">
							<td>
								No. Relacion:&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="cIdRelacion" id="cIdRelacion" style="text-transform:uppercase; text-align: left;" onkeyup="$(this).val($(this).val().toUpperCase())" />
							</td>
						</tr>
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
											<textarea name="cConcepto" rows="8" style="width:250px;text-transform:uppercase; text-align: left;" ID="cConcepto" onkeypress="return validarConcepto(event)" onkeyup="$(this).val($(this).val().toUpperCase())" ></textarea>
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
								<select id="tConcepto" name="tConcepto" style="width: 10em;" onChange="concepto();"></select>
							</td>
							<td colspan="2">
								<select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" style="width: 20em;"></select>
							</td>
							<td>&nbsp;</td>
						</tr>
						
								<!-- 
						<tr id="altaAlmacenariaTitulo" >
						<td colspan="2"><span id="altaAlmacenariaCampos">Año Factura</span><select id="cAnioFactEP" name="cAnioFactEP"></select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id="altaAlmacenariaCampos">Almacen</span>
						<select id="ALM" name="ALM"></select></td>
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
						
						-->
					</table>
				</fieldset>
				
					<br/>
					
					<div id="altaAlmacenariaTitulo">
					<fieldset style="width:750px">
		    			<legend>Almacen</legend>
					 <table>
						<tr>
						<td colspan="4"><span id="altaAlmacenariaCampos">Año Factura</span><select id="cAnioFactEP" name="cAnioFactEP"></select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id="altaAlmacenariaCampos">Almacen</span>
						<select id="ALM" name="ALM"></select></td>
						</tr>
						</table>
						</fieldset>
					</div>	
					
						
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
	<input type="hidden" value="RELACIONGASTOS" id="cDocumento" name="cDocumento" />
	<input type="hidden" name="ID_DESTINO_GASTO" id="ID_DESTINO_GASTO" />
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" />
	<input type="hidden" id="cEjercicio" name="cEjercicio" />
	<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=usuario.getU_UR() %>" />
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="RELGASTO" />
	<input type="hidden" id="cDocumento" name="cDocumento" value="RELACIONGASTOS">
	<input type="hidden" id="folioRelacion" name="folioRelacion" value="">
	<input type="hidden" id="nIdEstado" name="nIdEstado" value="1">
	<input type="hidden" id="totalLineas" name="totalLineas" value="">
	<input type="hidden" id="nFolioRelacionGastos" name="nFolioRelacionGastos"/>
	<input type="hidden" id="folioCasoRelacionGastos" name="folioCasoRelacionGastos"/>
	
	
</form>   
</body>
</html>
