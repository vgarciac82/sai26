<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.CancelaDocumento"%>

<%
	String resultadoCancelacion="";
	String mensaje="";
	//Connection conn=null;
	String cTipoDocto="";
	String cFolio="";
	String cFecha="";
	int nFolioDocumento;
	String cTipoDocumento;
	String cUR = "";
	
	String fDevolucion = "";
	String FechaRegistro = "";
	String Documento = "";
	String noContrarrecibo = "";
	String Folio = "";
	String RFC = "";
	String nomRFC = "";
	String fAplicacion = "";
	String importeNeto = "";
	String MotivoDevolucion = ""; 
	String DescripcionDevolucion = "";
	String login = "";
		
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if(usuario==null){
		response.sendRedirect("../index.jsp");
	}

	cUR = usuario.getU_UR();
	login = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();

	//String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/");

	String path = request.getContextPath();
	if (request.getParameter("cancelarDoc") != null && "Si".equals(request.getParameter("cancelarDoc"))) {
		cTipoDocto=request.getParameter("ctipoDcto");
		cFolio=request.getParameter("noFolio");
		cFecha=request.getParameter("DPC_FECHA");
		
		//Guardar en Tabla tVolante_Devolucion
		fDevolucion = request.getParameter("folioDevolucion");
		Documento = request.getParameter("Documento");
		noContrarrecibo = request.getParameter("caNoContrarrecibo");
		Folio = request.getParameter("nfolio");
		RFC = request.getParameter("cIdRFC");
		nomRFC = request.getParameter("cnombre");
		fAplicacion = request.getParameter("fAplicacion");
		importeNeto = request.getParameter("mImporteNeto");
		MotivoDevolucion = request.getParameter("cIdMotivoCancelacion");
		DescripcionDevolucion = request.getParameter("motivo");
		
			try{
				String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
				CancelaDocumento canselDocto = new CancelaDocumento(GestionInterface.ATT_CONEXION);
				
				//canselDocto.guardaDocumentoCancelado(fDevolucion, cFolio, cFecha, Documento, noContrarrecibo, Folio, RFC, nomRFC, fAplicacion, importeNeto, MotivoDevolucion, DescripcionDevolucion);
				//Motor Original
				//mensaje = canselDocto.cancelaDocto(cFolio, cTipoDocto, cFecha);
				//Motor Nuevo
				Map m = new HashMap();
				mensaje = canselDocto.cancelaDoctoNuevo(cFolio,cTipoDocto,cFecha,m,prefixPath,usuario.getLogin(), false);
				//System.out.println("mensaje:"+mensaje);
			} catch (Exception ex) {
				mensaje = ex.getMessage();
			}
	}	

	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base>
    <title>cancelaDocumentos.jsp</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
 	<link rel="stylesheet" type="text/css" href="../css/contable.css"></link>
  	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />

	<style type="text/css" title="currentStyle">
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../Generador/css/demo_table_jui.css";
		@import "../Generador/css/demo_page.css";
		
	</style>
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	

	<script type="text/javascript" src="../js/datepickercontrol.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>

	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>


	<!-- script type="text/javascript" src="../js/datepickercontrol.js"></script>
	<script type="text/javascript" src="../js/jquery-1.2.6.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../js/jsquery-fetch.js"></script>
	<script type="text/javascript" src="../js/masks.js"></script>
	<script type="text/javascript" src="../js/utils/syctools.js"></script-->
 	<script type="text/javascript" src="../Generador/js/crud.js"></script>
 
  <script type="text/javascript">
  
  $(document).ready(function() {

		$( "#dialog-Motivo" ).dialog({
		
			autoOpen: false,
			height: 350,
			width: 500,
			modal: true,
			buttons: {
					"Aceptar": function() 
					{
						$("#divEspera").attr("style","visibility=visible");
						$("#nFolioAdefa").val( $("#nfolio").val() );
						$("#tipoDocumento").val( $("#TC_GAVETA_ASOCIADA").val() );
						var nFolioPagado = $("#nFolioAdefa").val();
						var cTipoDocto = $("#tipoDocumento").val();
						$.ajax({
							url: '../Generador/cierrePresupuestal.jsp',
							type: 'post',
							dataType: 'json',
							data: {tipo:'cancelaEjercidoPagado', nFolioAdefa:nFolioPagado, tipoDocumento:cTipoDocto},
							success: function(data){
									if(data.sinSesion == "sinSesion"){
										location.href = "../index.jsp";
									}else if(data.estatus == "guardado"){
										getNextSequenceVal({seqName: "DEV" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
						     			queryFormPost("tDevolucionCancelaCreate",{async: false });
										cmdImprimir();
										alert("Documento Cancelado");
     									
									}else{
										alert( "No se pudo Cancelar el Documento, " + data.estatus );
									}
									$("#divEspera").attr("style","visibility=hidden");
									$(  "#dialog-Motivo" ).dialog( "close" );
									location.reload();
							}
						});
						
					},
					
					"Cancelar": function() {
						$( this ).dialog( "close" );
					}
				},
			close: function() {										
			}							
		});

	
	$("#btnVolante").hide();
  	querySelectPost("tEjercicioRead", "aEjercicioFiscal", {async: false });
	$('#noFolio').change( function () {
      	var cdocto = this.value.substring(0, 4);
      	var cUnidR = this.value.substring(5, 8).toUpperCase();
      	var nfolio = this.value.substring(9);
     	$("#cDocumentoHaplicado").val("");		
      	$("#docto").val( cdocto );
      	$("#nfolio").val( nfolio );
      	
      	$("#Documento").val( "" );
      	$("#cIdRFC").val( "" );
      	$("#cnombre").val( "" );
      	$("#caNoContrarrecibo").val( "" );
      	$("#mImporteNeto").val( "" );
      	$("#fAplicacion").val( "" );
      	
      	queryFormPost("vDoctosCancelaRead",{async: false });
      	if( $("#TC_GAVETA_ASOCIADA").val() == 'NOMINA' ){
      		alert("No se Pueden Cancelar Documento de NOMINA desde esta Opción");
			$("#noFolio").val( "" );      		
      		return;
      	}
      	if( $("#cDocumentoHaplicado").val() == "S" && $("#cEstatus").val() != 'AUTORIZADO' ){
      		alert("No se Pueden Cancelar Documento por estar en estatus de: " + $("#cEstatus").val());
			$("#noFolio").val( "" );      		
    		$("#Documento").val( "" );
	      	$("#cIdRFC").val( "" );
	      	$("#cnombre").val( "" );
	      	$("#caNoContrarrecibo").val( "" );
	      	$("#mImporteNeto").val( "" );
	      	$("#fAplicacion").val( "" );
	      	$("#cUnidadResponsable").val("");
			return;
      	}
      	
    	if($("#cDocumentoHaplicado").val() == ""){
    		alert("Este Documento NO TIENE APLICACION CONTABLE Y/O PRESUPUESTAL");
    		$("#Documento").val( "" );
	      	$("#cIdRFC").val( "" );
	      	$("#cnombre").val( "" );
	      	$("#caNoContrarrecibo").val( "" );
	      	$("#mImporteNeto").val( "" );
	      	$("#fAplicacion").val( "" );
	      	$("#cUnidadResponsable").val("");
    	}
    	
    	if($("#cDocumentoHaplicado").val() == "C"){
    		alert("Este Documento Ya Esta Cancelado");
    		$("#cIdMotivoCancelacion").attr("disabled","disabled");
    		$("#motivo").attr("disabled","disabled");
    		$("#Cancelar").hide();
    		$("#btnVolante").show();
    	}else{
    		document.getElementById("cIdMotivoCancelacion").disabled = false;
    		document.getElementById("motivo").disabled =  false;
    		$("#Cancelar").show();
    		$("#btnVolante").hide();
    	}
    	
    	if ( $("#cIdRFC").val() == "" ){
    		alert("No se Encontró el Documento " + this.value.toUpperCase());
			//$("#noFolio").val( "" );     	
	      	$("#Documento").val( "" );
	      	$("#cIdRFC").val( "" );
	      	$("#cnombre").val( "" );
	      	$("#caNoContrarrecibo").val( "" );
	      	$("#mImporteNeto").val( "" );
	      	$("#fAplicacion").val( "" );
	      	$("#cUnidadResponsable").val("");
	      	return;
	   	}

    	if ( $("#cUnidadResponsable").val() <= "B14"){
    		if (cUnidR != "A02"){
				alert("La Unidad Responsable no corresponde al Documento " + this.value.toUpperCase());
		      	$("#Documento").val( "" );
		      	$("#cIdRFC").val( "" );
		      	$("#cnombre").val( "" );
		      	$("#caNoContrarrecibo").val( "" );
		      	$("#mImporteNeto").val( "" );
		      	$("#fAplicacion").val( "" );
		      	$("#cUnidadResponsable").val("");
				return;
			}
    	}else{
    		if ($("#cUnidadResponsable").val() != cUnidR){
				alert("La Unidad Responsable no corresponde al Documento " + this.value.toUpperCase());
		      	$("#Documento").val( "" );
		      	$("#cIdRFC").val( "" );
		      	$("#cnombre").val( "" );
		      	$("#caNoContrarrecibo").val( "" );
		      	$("#mImporteNeto").val( "" );
		      	$("#fAplicacion").val( "" );
		      	$("#cUnidadResponsable").val("");
				return;
			}
    	}

    	if ( $("#nEnviadoSicop").val() != "0" && $("#cDocumentoHaplicado").val() == "S" ){
    		alert("No es posible Cancelar Documento con LayOut Generado");
	      	$("#Documento").val( "" );
	      	$("#cIdRFC").val( "" );
	      	$("#cnombre").val( "" );
	      	$("#caNoContrarrecibo").val( "" );
	      	$("#mImporteNeto").val( "" );
	      	$("#fAplicacion").val( "" );
	      	$("#cUnidadResponsable").val("");
		}
	});
	querySelectPost("cat_motivo_cancelacion", "cIdMotivoCancelacion", {async: false });
	
  });
  
     function cancelarDoc(){
     		$("#Cancelar").attr('disabled', true);
     				
     		var cFolioDocto = document.getElementById("noFolio").value.toString();
     		var cFechaCancela = document.getElementById("DPC_FECHA").value.toString();
     		if (cFolioDocto==""){
     			alert("Indique el número de Folio de Documento.");
     		}else if (cFechaCancela==""){
     			alert("Indique la Fecha de Cancelación.");
     		}else if($("#cIdMotivoCancelacion").val() == ""){
					alert("Falta Agregar Motivo Devolución");
     				$("#Cancelar").attr('disabled', false);
					return;					
			}else if($("#motivo").val() == ""){
					alert("Falta Agregar Descripción Devolución");
					$("#Cancelar").attr('disabled', false);
					return;
			}else{
    			if ( $("#cIdRFC").val() == "" ){
		    		alert("No se ha Capturado Documento Válido para Cancelar");
		    		$("#Cancelar").attr('disabled', false);
					return;
    			}
				if ( $("#nEnviadoSicop").val() != "0" ){
		    		alert("No es Posible Cancelar Documento con LayOut Generado");
		    		$("#Cancelar").attr('disabled', false);
					return;
				}
				
				var cUnidResp = cFolioDocto.substring(5, 8).toUpperCase();
     			if ("<%=cUR%>" == "A02") {
     				$( "#dialog-Motivo" ).dialog( "open" );   				
     			}else {
	     			if (cUnidResp == "<%=cUR%>") {
	     				$( "#dialog-Motivo" ).dialog( "open" );
	     			}else {
	     				alert("La Unidad Responsable del Usuario No corresponde al documento a Cancelar");
	     			}
     			}
     			
     		}
     		$("#Cancelar").attr('disabled', false);
   }
  
function onLoadPlantilla(){
    	if ("<%=mensaje%>" != ""){
    		alert("<%=mensaje%>");
    	}
    	
}

function setSequenceVal(seqValue){
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "<%=cCentroContable%>" + "DEV" + $("#aEjercicioFiscal").val() + seqValue;
	$("#folioDevolucion").val( seqValue );
}
  	
  	
function cmdImprimir(){
//location.reload();
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=VOLANTE1.jasper"
						+ "&NumeroFolio=" + $("#noFolio").val(),
						//+ "&nombre="   + ""
						//+ "&cargo="    + ""
						//+ "&area="     + "",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
}
  </script>
</head>
  <body>
		<form id="cancelaDocto">
  			<div id="container">
				<table id="cancela_Docto" width="50%">
					<tr>
						<td colspan="4" align="center" >Cancelaci&oacute;n de Documentos</td>
					</tr>
					<tr>
						<td colspan="4" align="center" ><%=mensaje%></td>
					</tr>
					<tr>
						<td align="center">Numero de Folio:
						<input type="text" id="noFolio" name="noFolio" value="" style="text-transform:uppercase" />
						</td>
					</tr>
					<tr>
						<td align="center">Fecha de registro:
						<input type="text" name="DPC_FECHA" id="DPC_FECHA" readonly="readonly" value="" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="10" /></td>
					</tr>
					<tr>
						<td align="center" valign="top" colspan="4">
							<input type="button" id="Cancelar" name="Cancelar" value="Cancelar Documento" onclick="javascript:cancelarDoc();"></input>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<select style="visibility: hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"></select>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>
							<table id="datos_doctos" border="0" align="center">
								<tr align="center">
									<td colspan="2" valign="top" nowrap>
										<input type="hidden" id="docto" name="docto" value="">
										<input type="hidden" id="nEnviadoSicop" name="nEnviadoSicop" value="0">
										<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="0">
										<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado">
										<input type="hidden" id="folioDevolucion" name="folioDevolucion">
										<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>">
										<input type="hidden" id="nFolioAdefa" name="nFolioAdefa">
										<input type="hidden" id="tipoDocumento" name="tipoDocumento">
										<input type="hidden" id="TC_GAVETA_ASOCIADA" name="TC_GAVETA_ASOCIADA">
										<input type="hidden" id="cEstatus" name="cEstatus">
										<input type="hidden" id= "login" name="login" value="<%=login%>">
										
										Documento:
										<input id="Documento" name="Documento" maxlength="40" readonly style="background: #f0f0f0" 
											size="40" type="text">
										&nbsp;&nbsp;No. Contrarecibo:
										<input name="caNoContrarrecibo" id="caNoContrarrecibo"
											size="28" value="" readonly style="background: #f0f0f0">
										&nbsp;&nbsp;Folio:<input name="nfolio" type="text" id="nfolio" size="10" maxlength="40" readonly style="background: #f0f0f0">
									</td>
									<td align="center">
									</td>
								</tr>
								<tr align="center">
									<td valign="top">
										RFC:
										<input type="text" maxlength="15" size="15"
											name="cIdRFC" id="cIdRFC"  readonly  style="background: #f0f0f0">
										<input type="text" maxlength="100" size="96" name="cnombre"
											ID="cnombre" value="" readonly style="background: #f0f0f0">
									</td>
									<td>
									</td>
								</tr>
								<tr align="center">
									<td colspan=2 valign="top">Fecha de Aplicaci&oacute;n:
										<input name="fAplicacion" readonly class="paso01" type="text"
											id="fAplicacion" value="30/01/2012" maxlength="10" size="10" style="background: #f0f0f0">
										&nbsp;&nbsp;&nbsp;&nbsp;Importe Neto:
										<input name="mImporteNeto" readonly type="text" style="background: #f0f0f0;text-align:right;"
											id="mImporteNeto" size="15">
									</td>
									<td></td>
								</tr>
								<tr align="center">
									<tr align="center"><td>Motivo Devolucion<select name="cIdMotivoCancelacion" id="cIdMotivoCancelacion" disabled="disabled"></select></td></tr>
									<tr align="center"><td>Descripcion Devolucion</td></tr>
									<tr align="center"><td><textarea name="motivo" id="motivo" rows="6" style="width: 38%;" maxlength="300" disabled="disabled"></textarea></td></tr>
									<tr align="center"><td></td></tr>
									<tr align="center"><td><input type="button" id="btnVolante" name="btnVolante" value="Imprimir Volante" onclick="cmdImprimir()"></td></tr>
									<tr><td></td></tr>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
  			<div id="dialog-Motivo" title="Cancelar Documento">
	  			<h1>Esta Seguro Que Desea Cancelar El Documento</h1>
				<div id="divEspera" style="visibility: hidden" align="center">Espere por favor....
				  <img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
		</form>
  </body>
</html>
