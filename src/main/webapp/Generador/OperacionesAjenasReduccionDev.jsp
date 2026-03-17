<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G"
			+ c.getIdGabinete();//se usa por separado abajo
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Disminucion de Devengado</title>
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
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"> </script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"> </script>

<script type="text/javascript" charset="utf-8">
var breturnVal = false;
$(document).ready(function(){
		$("#cUsuario").val( "<%=algo%>" );
		$("#cCentroContable").val( "<%=cCentroContable%>");	
		$("#id_caso").val( <%=request.getParameter("folio")%>);
		$("input.AyudaSyC").subIniciaDlg();
		$("#Buscar").button();
		$("#buscarCXP").button();
		
		$( "#dialog-form" ).dialog({
			autoOpen: false,
			height: 400,
			width: 800,
			modal: true,
			beforeClose: function( event, ui ) {
				return bClicBtn;			
			}
		});
				
		$("#Detalle").dataTable({
			bPaginate : false,			//muestra las flechas y pagina dependiendo el rango
			bLengthChange : false,  //
			bInfo : false,			//es el que muestra los numeros de los registros		
			sScrollX: "200px",
			sScrollY: "200px",
			bJQueryUI: true,  //se coloca el dise?o que contiene en css
			bFilter : false,
			bSort : false, // para colocar los filtros en los campos
			bDestroy: true,
			bRetrieve:true,
			bleft:true,
			bAutoWidth : true,
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
				oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
			}
		});
		
		$("#Disminucion").dataTable({
			bPaginate : false,			//muestra las flechas y pagina dependiendo el rango
			bLengthChange : false,  //
			bInfo : false,			//es el que muestra los numeros de los registros		
			sScrollX: "200px",
			sScrollY: "200px",
			bJQueryUI: true,  //se coloca el dise?o que contiene en css
			bFilter : false,
			bSort : false, // para colocar los filtros en los campos
			bDestroy: true,
			bRetrieve:true,
			bleft:true,
			bAutoWidth : true,	   			
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
				oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
			}
		});	
		
		$("#dialog-Procesando").dialog({
			autoOpen: false,
			height: 400,
			width: 400,
			modal: true,
			async: false,
			open: function() {
				var tipo = "aplicarDisminucionDevengado";
				var nFolio = $("#id_caso").val();
				var campo = "nFolioDisminucionDev";
				var tablaEnc = "tDisminucionDevEncabezado";
				var campoCondicion = $("#campoCondicion").val(); 
				var tablaDet = "tDisminucionDevDetalle"; 
				var tipoDocumento = "DISMINUCIONDEV";
					
//				$("#divEsperaProcesando").attr("style","visibility=visible");
				
				$.ajax({
						url:'./cierrePresupuestal.jsp',
						type:'post',
						dataType: 'json',
						data:{tipo:tipo,tipoDocumento:tipoDocumento,campo:campo,nFolio:nFolio,tablaEnc:tablaEnc,tablaDet:tablaDet},
						async: false,
						success:function(data){
							if(data.sinSesion == 'sinSesion'){
								location.href = "../index.jsp";
							}
							if(data.estatus == "guardado"){
								if(<%=id_oper==1%>){
									alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val() ) ;
									cmdImprimir();
									parent.document.getElementById("pb_send").disabled = false;
									parent.execOperacion();
									parent.execResponsable();
									parent.document.getElementById("pb_send").click();
								}else{
									$("#docAplicado").val( "S" ) ;
									cmdImprimir();
									alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val());
									parent.document.getElementById("pb_send").disabled = false;
									parent.document.getElementById("pb_send").click();
								}								
								breturnVal = true;
							}else{
								breturnVal = false;
								alert( "El Documento No Se Aplico: "+$("#caNoContrarrecibo").val() + " - " + data.estatus ) ;		
							}
							$("#divEsperaProcesando").attr("style","visibility=hidden");
							$( "#dialog-Procesando" ).dialog( "close" );
						}
					});
				},
				close: function() {										
				}				
			});
			
		$( "#dialog-firmantes" ).dialog({
			autoOpen: false,
			height: 400,
			width: 480,
			modal: true,
			buttons: {
				"Aceptar": function(){
					if($("#cNombreVoBo").val() == ""){ alert("Falta Ingresar Nombre en Datos Vº Bº"); return; } 
					else if($("#cPaternoVoBo").val() == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return; }
					else if($("#cMaternoVoBo").val() == ""){ alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return; }
					else if($("#cPuestoVoBo").val() == ""){ alert("Falta Ingresar Puesto en Datos Vº Bº"); return; }
					
					if($("#cNombreAut").val() == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
					else if($("#cPaternoAut").val() == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
					else if($("#cMaternoAut").val() == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
					else if($("#cPuestoAut").val() == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					
					$("#cNombreVo").val($("#cNombreVoBo").val());
					$("#cPaternoVo").val($("#cPaternoVoBo").val());
					$("#cMaternoVo").val($("#cMaternoVoBo").val());
					$("#cPuestoVo").val($("#cPuestoVoBo").val());
					
					$("#cNombreA").val($("#cNombreAut").val());
					$("#cPaternoA").val($("#cPaternoAut").val());
					$("#cMaternoA").val($("#cMaternoAut").val());
					$("#cPuestoA").val($("#cPuestoAut").val());
					
					$("#firmanteVoBo").val($("#cNombreVoBo").val()+" "+$("#cPaternoVoBo").val()+" "+$("#cMaternoVoBo").val() );
					$("#firmanteAut").val($("#cNombreAut").val()+" "+$("#cPaternoAut").val()+" "+$("#cMaternoAut").val());
					
					try {
						parent.document.getElementById("pb_save").disabled=true;
						getNextSequenceVal({seqName: "DD-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
						queryFormPost({
							queryName:"tDisminucionDevEncabezadoCreate", async:false, callback:function(){
								guardatabladetalle();
								if (procesar()){
									parent.document.getElementById("pb_save").disabled = true;
									parent.document.getElementById("pb_save").style.visibility='hidden';											
								}
							}
						});
					} catch (e) {
						$("#divEsperaProcesando").attr("style","visibility=hidden");
						alert("No se pudo aplicar correctamente");
						$(this).dialog("close");
						return false;
					}
					
					var msn = "No Se Guardo Correctamente Informacion de Firmantes"; 
					/*	
					if($("#firmanteExiste").val() == "Existe"){
						// Actualiza informacion
						queryFormPost(
							{queryName:"firmanteModuloUpdate", async:false, callback:function(){
								msn = "Actualizado Correctamente Firmantes";}
							}
						);
					}else{
						queryFormPost(
							{queryName:"firmanteModuloCreate", async:false, callback:function(){
								msn = "Guardado Correctamente Firmantes";}
							});
					}*/
					$(this).dialog("close");
				},
				
				"Cancelar": function() {
					parent.document.getElementById("pb_save").disabled=false;
					$(this).dialog("close");
				}
			},
		close: function() {
				parent.document.getElementById("pb_save").disabled=false;
		}							
	});
	
	$("#chk_PagoPenas").change(function(){
			if ($("#chk_PagoPenas").prop("checked")){
				$("#esPagoPenas").val("1");
				$("#cBeneficiario").val(5);
			}else{
				$("#esPagoPenas").val("0");
			}
			
		});
	
	cssReadOnly();
});//FIN DEL READY

	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
 
		try{
		
			if (id_oper == 1){
				if (quitaFmt($('#mDisminucion').val()) == 0 ){
					alert( "Favor de capturar el importe de la Disminucion" );
					return false;
				}
				if (Number(quitaFmt($('#mDisminucion').val())) >  Number(quitaFmt($('#mImporteTotal').val()))){
					alert( "La Disminucion no puede ser mayor a " + $('#mImporteTotal').val());
					return false;
				}

				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#EF").val() );
				p.gestion.setConceptoMov("Aplicación Disminucion Devengado");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setAplicadoCont("false");
			}
			
			var nretval = cmdGuardar();
			if (nretval == -1) {
				return false;
			}

		}catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
  	}

 	function fnAplicaMotor(){
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}

  	function onPostSubmit(id_oper){
  		return true;
  	}

    function onLoadPlantilla(){
    	$("#divImprimePoliza").hide();
		$("#esperardet").hide();
		
		$("#cxpBuscar").val("");
		$("#mImporteTotal").val(0);
		$("#mDisminucion").val(0);

		if(<%=id_oper == 1%>){
			querySelectPost("tGrupoOpAjenasRead", "cBeneficiario", {async: false });
			querySelectPost("catalogoEjercicioFiscalRead", "EF", {async: false });
			
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_send").disabled=true;
		    parent.document.getElementById("pb_save").disabled=true;
		}
		if(<%=id_oper == 2%>){
			querySelectPost("tGrupoEncabezadoDisminucionRead", "cBeneficiario", {async: false });
			queryFormPost("leeContrarreciboImporteDisminucion", {async: false });
			
			$("#cDesccripcion").attr("readonly","readonly");
			$("#cxpBuscar").attr("readonly","readonly");
			$("#mDisminucion").attr("readonly","readonly");
			$("#Buscar").attr('disabled', true);
			$("#cBeneficiario").attr('disabled', true);
		    cssReadOnly();
		    detalleGuardado();
		    $("#mImporteTotal").formatCurrency();
			$("#mDisminucion").formatCurrency();
			
			$("#divImprimePoliza").show();
		}
		
		/*if ($("#cDocumentoHaplicado").val() == "S" && (<%=id_oper != 1%>)){
			parent.document.getElementById("pb_save").disabled=false;				
		}*/
  	}

    function ResponsableSiguiente(id_oper){
  		 if(id_oper==1)
  			 return "CONSULTA_DISMINUCIONDEV";
  	}
  	  	
  	function OperacionSiguiente(id_oper){
  		if(id_oper==1)
			return "consulta_disminuciondev";
  	}

	function onPostDisplay(id_oper){}
	function formSubmited(){}		
	
	function tipoFirmantes(){	
		$("#dialog-firmantes").dialog("open");
		queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async: false }); // voBo - autoriza
	}
		
	var  grupoBen=$("#cBeneficiario").val();
	
	function detalleGuardado(){
		var campos=$("#id_caso").val(); 
		var  elParametro2 ='';
		var szTabla = "CONSULTADISMINUCIONDEV";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: elParametro2,Campos:campos, MaxReg: "", ajax: 'false'}, function(j){
			for (var i = 0; i < j.length; i++) {			
				$('#Disminucion').dataTable().fnAddData([j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12,j[i].Col13,j[i].Col14,j[i].Col15,j[i].Col16,j[i].Col17,j[i].Col18,j[i].Col19,j[i].Col20,j[i].Col21,j[i].Col22,j[i].Col23]);
		    }
		});
	}
	
	function Busqueda(){
		$("#mImporteTotal").val(0);
		$("#mImporteTotal").formatCurrency();
		if ($('#cBeneficiario').val() == 0 ){
			alert( " Tiene que elegir un Grupo de Retenciones" );
			return;
		}
		if ($.trim($('#cxpBuscar').val()) == "" ){
			alert( " Favor de indicar la CXP" );
			return;
		}
		parent.document.getElementById("pb_save").disabled=true;
		$("#mDisminucion").attr("readonly","readonly");
		
		$("#esperardet").show();
		var grupo='';
		var condiciones='';
		var sumaRetenciones='';
		
		var nRows = $("#Detalle tr").length -1 ;
	
		if ( nRows > 0 ){
			var oDetalle = $("#Detalle").dataTable();
			oDetalle.fnClearTable();
		}
		
		var campos = "'" +  $("#cGrupo").val()+"','" +
							$("#cCentroContable").val()+"','" +
							$("#cCondiciones").val() + "','" +
							$("#cSumaRetenciones").val() + "'," +
							$("#cBeneficiario").val() + ", '" +
							$("#cxpBuscar").val() + "', " +
							$("#esPagoPenas").val() ;
		var elParametro2 ='';
		var szTabla = "BUSQUEDAOADISMINUCIONDEVENGADO";
		
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: elParametro2, Campos:campos, MaxReg: "", ajax: 'false'}, function(j){				
			var datosTabla = [];
			var cont = 0;
			var montoTotal = 0;
			for (var i = 0; i < j.length; i++) {
				if(j[i].Col5 > 0){
					montoTotal += Number(j[i].Col5);
					datosTabla[i] = [j[i].Col0, 
									 j[i].Col20,
									 j[i].Col1,
									 j[i].Col2,
									 j[i].Col3,
									 j[i].Col4,
									 j[i].Col5,
									 j[i].Col18,
									 j[i].Col19,
									 j[i].Col9,
									 j[i].Col10,
									 j[i].Col11,
									 j[i].Col13,
									 j[i].Col12,
									 j[i].Col14,
									 j[i].Col15,
									 j[i].Col16,
									 j[i].Col17,
									 j[i].Col21,
									 j[i].Col23,
									 j[i].Col24,									 
									 j[i].Col25,
									 j[i].Col26,
									 j[i].Col27];
					$('#Detalle').dataTable().fnAddData(datosTabla[i]);
					cont++;
				}
			}
			if (cont>0){
				$("#mImporteTotal").val(montoTotal);
				$("#mImporteTotal").formatCurrency();
				parent.document.getElementById("pb_save").disabled=false;
				document.getElementById("mDisminucion").removeAttribute("readonly",false);
			} else
				alert("No se encontro el "+$('#cxpBuscar').val()+ " en el Grupo "+$("#cBeneficiario option:selected").text());
			$("#esperardet").hide();
		});
	}
	
	function grupoRetencion(){
		queryFormPost("tGrupoOpAjenasOnclicRead", {async: false });
	}
	
	var retencion1;	
	
	function cmdGuardar(){
		if ( $("#caNoContrarrecibo").val() != "" ){
			if ($("#cDocumentoHaplicado").val() == "S" && (<%=id_oper == 1%>)){
				parent.document.getElementById("pb_save").disabled=true;				
				parent.document.getElementById("pb_send").style.visibility='visible';
				parent.document.getElementById("pb_send").disabled=false;
			}
			return 0;
		}

		var hayError='';			 
		if ($('#cBeneficiario').val() == 0 ){
			alert( " Tiene que elegir un Grupo de Retenciones" );
			return -1;
		}
		
		if(!buscarReferenciaCXP()){
			return -1;
		}
		
		if ($("#caNoContrarreciboRef").val() == "" ){
			alert( "Favor de capturar la Referencia CXP para continuar." );
			return -1;
		}			 

		$('.encabezado').each(function(){
			if ($(this).val()==''){
				hayError = hayError + this.name+', ';
			}			
		});
					
		if (hayError==''){
			tipoFirmantes();
			return 0;
		}else{
			alert('Debe ingresar los siguientes datos: '+ hayError);
			return -1;
		}
	}
	
	function setSequenceVal(seqValue) {
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "1" + seqValue.substr(seqValue.length - 5);
		seqValue = $("#cCentroContable").val() + "DD" + $("#EF").val() + seqValue;
		$("#caNoContrarrecibo").val( seqValue );
	}
	
	function GuardaContrarecibo(){
		elParametro = "'"+$("#caNoContrarrecibo").val()+"', '1', '0', '0', '"+quitaFmt($('#mDisminucion').val())+"', '0', '0', '0', '0','0', '0', '"+quitaFmt($('#mDisminucion').val())+"', '','" + $("#cCentroContable").val() + "',"+$("#EF").val()+",'DISMINUCION DE DEVENGADO/OPERACIONES AJENAS', 'D','0','"+$("#RFC").val()+"',0";
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIVERSOSCREATE", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
	}
	
	var tipo;
	function cmdImprimir(){
		window.open(
			"../admin/SeguridadCatalogos?"
				+ "catalogo=DISMINUCIONDEVENGADO"
				+ "&accion=run"
				+ "&rn=PolizaDisminucionDevengado.jasper"
				+ "&whereFolio= CR.canocontrarrecibo='" + $("#caNoContrarrecibo").val() + "'",
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");			
	}
	
	function anexo(){
		window.open(
			"../admin/SeguridadCatalogos?"
				+ "catalogo=ANEXO"
				+ "&accion=run"
				+ "&rn=OperAjenas.jasper"
				+ "&nFolioOperAjenas=" + $("#id_caso").val(),
			"Anexo",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}

    function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	
    function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}
	
	function validarNumerico(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla == 8)
			return true;
		patron = /[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	
	function Sinfrmt(fld){		
		var valcol = fld.value;
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$("#" + fld.id).val(valcol);
	}	
	
	function guardatabladetalle(){
		var tableDisminucion = document.getElementById('Disminucion');
		var rowCount = tableDisminucion.rows.length;
		for(var i=1; i<rowCount; i++){
			var rowDisminucion=  tableDisminucion.rows[i];
			
			var nFolioDoc 				= rowDisminucion.cells[2].innerHTML;
			var cTipoPago 				= rowDisminucion.cells[4].innerHTML;
			var EP 						= rowDisminucion.cells[5].innerHTML;
			var fAplicacionDoc 			= rowDisminucion.cells[3].innerHTML;
			var cMes 					= rowDisminucion.cells[18].innerHTML;
			var cEvento 				= rowDisminucion.cells[19].innerHTML;
			var aEjercicioFiscal 		= $("#EF").val();
			var cCentroContable 		= rowDisminucion.cells[20].innerHTML;
			var mTotal 					= rowDisminucion.cells[6].innerHTML;
			var mEntero 				= 0;
			var mSobrante 				= 0;
			var mImporteFlete23 		= 0;
			var mISRHonorarios 			= rowDisminucion.cells[9].innerHTML;	
			var mISRArrenda 			= rowDisminucion.cells[10].innerHTML;
			var mImporteFlete4 			= rowDisminucion.cells[11].innerHTML;
			var mCNIC 					= rowDisminucion.cells[13].innerHTML;
			var mIMDT 					= rowDisminucion.cells[12].innerHTML;
			var mObra5 					= rowDisminucion.cells[14].innerHTML;
			var mTesofe 				= rowDisminucion.cells[15].innerHTML;
			var mRetImpuestoCedular 	= rowDisminucion.cells[16].innerHTML;
			var caNocontrarrecibo 		= rowDisminucion.cells[0].innerHTML;
			var RFC 					= rowDisminucion.cells[1].innerHTML;
			var mImporteIvaArrenda 		= rowDisminucion.cells[8].innerHTML;
			var mImporteIvaHonorarios 	= rowDisminucion.cells[7].innerHTML;
			var mImporteISRLaudos 		= rowDisminucion.cells[17].innerHTML;
			
			var mISROtros 				= rowDisminucion.cells[21].innerHTML;
			var mImporteIva6 			= rowDisminucion.cells[22].innerHTML;		
			var mimporteISRResico		= rowDisminucion.cells[23].innerHTML;
			
          	$("#nFolioPago").val(nFolioDoc.toString());
          	$("#cTipoPago").val(cTipoPago.toString()); 
          	$("#Ep").val(EP.toString());
          	$("#fAplicacionDoc").val(fAplicacionDoc.toString());
          	$("#cMes").val(cMes.toString());
          	$("#cEventoPago").val(cEvento.toString());
          	$("#aEjercicioFiscal").val(aEjercicioFiscal.toString());
          	$("#cCentroContablePago").val(cCentroContable.toString());
          	$("#mTotal").val(-Number(mTotal.toString()));
          	$("#mEntero").val(-Number(mEntero,toString()));
          	$("#mSobrante").val(-Number(mSobrante.toString()));
	        $("#mImporteFlete23").val(-Number(mImporteFlete23.toString()));
          	$("#mISRHonorarios").val(-Number(mISRHonorarios.toString()));
          	$("#mISRArrenda").val(-Number(mISRArrenda.toString()));
          	$("#mImporteFlete4").val(-Number(mImporteFlete4.toString()));
          	$("#mCNIC").val(-Number(mCNIC.toString()));
          	$("#mIMDT").val(-Number(mIMDT.toString()));
          	$("#mObra5").val(-Number(mObra5.toString()));
          	$("#mTesofe").val(-Number(mTesofe.toString()));
          	$("#mRetImpuestoCedular").val(-Number(mRetImpuestoCedular.toString()));
          	$("#caNoContrarreciboPago").val(caNocontrarrecibo.toString());
          	$("#RFC").val(RFC.toString());
          	$("#mImporteIvaArrenda").val(-Number(mImporteIvaArrenda.toString()));
          	$("#mImporteIvaHonorarios").val(-Number(mImporteIvaHonorarios.toString()));
          	$("#mImporteISRLaudos").val(-Number(mImporteISRLaudos.toString()));
          	$("#mISROtros").val(-Number(mISROtros.toString()));
          	$("#mImporteIva6").val(-Number(mImporteIva6.toString()));
          	$("#mimporteISRResico").val(-Number(mimporteISRResico.toString()))
          	
          	if($("#esPagoPenas").val() == "1"){
          		$("#RFC").val("TESOFE");
          		$("#mPenalizacion").val(-Number(mTotal.toString()));
          	}
          	
          	queryFormPost("tDisminucionDevDetalleCreate",{async: false });
		}
		GuardaContrarecibo();
		actualizaReferenciaCXP();
	}
	function ajusteDisminucionDevengado(){
		var oDisminucion = $("#Disminucion").dataTable();
			oDisminucion.fnClearTable();
			
		if (Number($('#mDisminucion').val())>0){
			if (Number(quitaFmt($('#mDisminucion').val())) >  Number(quitaFmt($('#mImporteTotal').val()))){
				alert( "La Disminucion no puede ser mayor a " + $('#mImporteTotal').val());
				$('#mDisminucion').val("0");
				$('#mDisminucion').formatCurrency();
				return false;
			}
			var importeDisminucion = Number($('#mDisminucion').val());
			var tableDetalle = document.getElementById('Detalle');
			var rowCount = tableDetalle.rows.length;
			for(var i=1; i<rowCount; i++){
				var rowDetalle = tableDetalle.rows[i];
				
				var Contrarrecibo		= rowDetalle.cells[0].innerHTML;
				var RFC 				= rowDetalle.cells[1].innerHTML;
				var nFolioDoc 			= rowDetalle.cells[2].innerHTML;
				var fAplicacionDev		= rowDetalle.cells[3].innerHTML;
				var cTipoDoc 			= rowDetalle.cells[4].innerHTML;
				var Ep 					= rowDetalle.cells[5].innerHTML;
				var cMes 				= rowDetalle.cells[18].innerHTML;	
				var Evento 				= rowDetalle.cells[19].innerHTML;
				var CC 					= rowDetalle.cells[20].innerHTML;
				
				var mIVAHonorarios = rowDetalle.cells[7].innerHTML;
				if (Number(mIVAHonorarios.toString()) > importeDisminucion){
					mIVAHonorarios = importeDisminucion;
				}
				importeDisminucion -= Number(mIVAHonorarios.toString());
				
				var mIVAArrenda = rowDetalle.cells[8].innerHTML;
				if (Number(mIVAArrenda.toString()) > importeDisminucion){
					mIVAArrenda = importeDisminucion;
				}
				importeDisminucion -= Number(mIVAArrenda.toString());
				
				var mISRHonorarios = rowDetalle.cells[9].innerHTML;
				if (Number(mISRHonorarios.toString()) > importeDisminucion){
					mISRHonorarios = importeDisminucion;
				}
				importeDisminucion -= Number(mISRHonorarios.toString());
				
				var mISRArrenda = rowDetalle.cells[10].innerHTML;
				if (Number(mISRArrenda.toString()) > importeDisminucion){
					mISRArrenda = importeDisminucion;
				}
				importeDisminucion -= Number(mISRArrenda.toString());
				
				var mImporteFlete4 = rowDetalle.cells[11].innerHTML;
				if (Number(mImporteFlete4.toString()) > importeDisminucion){
					mImporteFlete4 = importeDisminucion;
				}
				importeDisminucion -= Number(mImporteFlete4.toString());
				
				var mIMDT = rowDetalle.cells[12].innerHTML;
				if (Number(mIMDT.toString()) > importeDisminucion){
					mIMDT = importeDisminucion;
				}
				importeDisminucion -= Number(mIMDT.toString());
				
				var mCNIC = rowDetalle.cells[13].innerHTML;
				if (Number(mCNIC.toString()) > importeDisminucion){
					mCNIC = importeDisminucion;
				}
				importeDisminucion -= Number(mCNIC.toString());
							
				var mObra5 = rowDetalle.cells[14].innerHTML;
				if (Number(mObra5.toString()) > importeDisminucion){
					mObra5 = importeDisminucion;
				}
				importeDisminucion -= Number(mObra5.toString());
							
				var mTesofe = rowDetalle.cells[15].innerHTML;	//penalizacion
				if (Number(mTesofe.toString()) > importeDisminucion){
					mTesofe = importeDisminucion;
				}
				importeDisminucion -= Number(mTesofe.toString());
							
				var mRetImpuestoCedular	= rowDetalle.cells[16].innerHTML;
				if (Number(mRetImpuestoCedular.toString()) > importeDisminucion){
					mRetImpuestoCedular = importeDisminucion;
				}
				importeDisminucion -= Number(mRetImpuestoCedular.toString());
					
				var mImporteISRLaudos = rowDetalle.cells[17].innerHTML;
				if (Number(mImporteISRLaudos.toString()) > importeDisminucion){
					mImporteISRLaudos = importeDisminucion;
				}
				importeDisminucion -= Number(mImporteISRLaudos.toString());
				
				var mISROtros = rowDetalle.cells[21].innerHTML;
				if (Number(mISROtros.toString()) > importeDisminucion){
					mISROtros = importeDisminucion;
				}
				importeDisminucion -= Number(mISROtros.toString());
				
				var mImporteIva6 = rowDetalle.cells[22].innerHTML;
				if (Number(mImporteIva6.toString()) > importeDisminucion){
					mImporteIva6 = importeDisminucion;
				}
				importeDisminucion -= Number(mImporteIva6.toString());
				
				var mimporteISRResico = rowDetalle.cells[23].innerHTML;
				if (Number(mimporteISRResico.toString()) > importeDisminucion){
					mimporteISRResico = importeDisminucion;
				}
				importeDisminucion -= Number(mimporteISRResico.toString());				
								
				var mTotal = Number(mIVAHonorarios.toString())+Number(mIVAArrenda.toString())+Number(mISRHonorarios.toString())+Number(mISRArrenda.toString())+Number(mImporteFlete4.toString())+Number(mIMDT.toString())+Number(mCNIC.toString())+Number(mObra5.toString())+Number(mTesofe.toString())+Number(mRetImpuestoCedular.toString())+Number(mImporteISRLaudos.toString()) +Number(mISROtros.toString()) +Number(mImporteIva6.toString()) +Number(mimporteISRResico.toString()) ;
			
				if($("#esPagoPenas").val() == "1"){
					mTotal = Number($("#mDisminucion").val());
				}
			
				$('#Disminucion').dataTable().fnAddData([Contrarrecibo.toString(),
														RFC.toString(),
														nFolioDoc.toString(),
														fAplicacionDev.toString(),
														cTipoDoc.toString(),
														Ep.toString(),
														parseFloat(mTotal.toString()).toFixed(2),
														parseFloat(mIVAHonorarios.toString()).toFixed(2),
														parseFloat(mIVAArrenda.toString()).toFixed(2),
														parseFloat(mISRHonorarios.toString()).toFixed(2),
														parseFloat(mISRArrenda.toString()).toFixed(2),
														parseFloat(mImporteFlete4.toString()).toFixed(2),
														parseFloat(mIMDT.toString()).toFixed(2),
														parseFloat(mCNIC.toString()).toFixed(2),
														parseFloat(mObra5.toString()).toFixed(2),
														parseFloat(mTesofe.toString()).toFixed(2),
														parseFloat(mRetImpuestoCedular.toString()).toFixed(2),
														parseFloat(mImporteISRLaudos.toString()).toFixed(2),
														cMes.toString(),
														Evento.toString(),
														CC.toString(),
														parseFloat(mISROtros.toString()).toFixed(2),
														parseFloat(mImporteIva6.toString()).toFixed(2),
														parseFloat(mimporteISRResico.toString()).toFixed(2)
													]);	
				if (importeDisminucion==0){
					i=rowCount;
				}
			}
		}
		$('#mDisminucion').formatCurrency();		
	}
	
	function procesar(){
		$("#dialog-Procesando").dialog( "open" );
		return breturnVal;
	}
	
	function buscarReferenciaCXP(){
		
		var bRegresa = true;
		
		if ($("#cBeneficiario").val() == 0 ){
			alert( " Tiene que elegir un Grupo de Retenciones" );
			return;
		}
		if ($.trim($("#cxpBuscar").val()) == "" ){
			alert( " Favor de indicar la CXP." );
			return;
		}
		
		if ($.trim($("#caNoContrarreciboRef").val()) == "" ){
			alert( " Favor de indicar Referencia de la CXP." );
			return;
		}
		
		queryFormPost("existeCXPPagadoRead",{async: false });
		
		if($("#existeCXPRef").val() != "1"){
			alert("La Referencia CXP capturada no existe. Verifique!!");
			$("#caNoContrarreciboRef").val("");
			$("#existeCXPRef").val("");
			 bRegresa = false;
		}else if($("#existeCXPRef").val() == "1"){
			//Buscar el tipo de pago y el folio para obtener el RFC
			$("#nFolioPagoRef").val("");
			$("#cTipoPagoRef").val("");
			queryFormPost("tipoFolioPagoCXPRef_Read",{async: false });
			
			if($("#cTipoPagoRef").val() == ""){
				alert("El contrarrecibo referencia capturado no es un tipo de Pago. Verifique!!");
				$("#caNoContrarreciboRef").val("");
				$("#existeCXPRef").val("");
				 bRegresa = false;
			}else if($("#cTipoPagoRef").val() == "PenasConv"){
				$("#RFCRef").val("TESOFE");
			}else{
				queryFormPost("obtenerRFCRefPago_Read",{async: false });
			}
		}
		
		return bRegresa;
		
	}
	
	function actualizaReferenciaCXP(){
		queryFormPost("tDisminucionDevEncRefCXPUpdate",{async: false });
		queryFormPost("tDisminucionDevEncRFCRefUpdate",{async: false });
	}
</script>

</head>
<br/>
<body id="dt_example">
	<form>		
		<input type="hidden" name="checkbox" id="checkbox" />
		<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
		<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
		<input type="hidden" id="cCentroContable" name="cCentroContable" />
		<input type="hidden" id="cRamo" name="cRamo" value="16 " />
		<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" />
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" />
		<input type="hidden" id="cUsuario" name="cUsuario" />
		<select id="EF" name="EF" style="visibility: hidden"></select>
		<input name="cDocumento" type="hidden" id="cDocumento" value="DISMINUCIONDEV"/>
		<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="EG" />
		<input type="hidden" id="cTipoDoc" name="cTipoDoc" />
		<input type="hidden" id="cEvento" name="cEvento" />
		<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" />
		<input type="hidden" id="cIdTipodocumento" name="cIdTipodocumento" />
		<input type="hidden" id="cGrupo" name="cGrupo" />
		<input type="hidden" id="cCondiciones" name="cCondiciones" />
		<input type="hidden" id="cSumaRetenciones" name="cSumaRetenciones" />
		<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value="" />
		<input type="hidden" id="firmanteExiste" name="firmanteExiste" size="14" />
		<input type="hidden" id="cNombreVo" name="cNombreVo" />
		<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
		<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
		<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
		<input type="hidden" id="firmanteVoBo" name="firmanteVoBo"/>
		<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO" />
		<input type="hidden" id="cNombreA" name="cNombreA" />
		<input type="hidden" id="cPaternoA" name="cPaternoA" />
		<input type="hidden" id="cMaternoA" name="cMaternoA" />
		<input type="hidden" id="cPuestoA" name="cPuestoA" />
		<input type="hidden" id="firmanteAut" name="firmanteAut" />
		<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" value="PAGO_AUT" />
		
		<!-- <input type="hidden" id="nFolioDisminucionDev" name="nFolioDisminucionDev" value=""/> -->
		<input type="hidden" id="nDocRenglon" name="nDocRenglon" value=""/>
		<input type="hidden" id="nFolioPago" name="nFolioPago" value=""/>
		<input type="hidden" id="cTipoPago" name="cTipoPago" value=""/>
		<input type="hidden" id="Ep" name="Ep" value=""/>
		<input type="hidden" id="fAplicacionDoc" name="fAplicacionDoc" value=""/>
		<input type="hidden" id="cMes" name="cMes" value=""/>
		<input type="hidden" id="cEventoPago" name="cEventoPago" value=""/>
		<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value=""/>
		<input type="hidden" id="cCentroContablePago" name="cCentroContablePago" value=""/>
		<input type="hidden" id="mTotal" name="mTotal" value=""/>
		<input type="hidden" id="mEntero" name="mEntero" value=""/>
		<input type="hidden" id="mSobrante" name="mSobrante" value=""/>
		<input type="hidden" id="mImporteFlete23" name="mImporteFlete23" value=""/>
		<input type="hidden" id="mISRHonorarios" name="mISRHonorarios" value=""/>
		<input type="hidden" id="mISRArrenda" name="mISRArrenda" value=""/>
		<input type="hidden" id="mImporteFlete4" name="mImporteFlete4" value=""/>
		<input type="hidden" id="mCNIC" name="mCNIC" value=""/>
		<input type="hidden" id="mIMDT" name="mIMDT" value=""/>
		<input type="hidden" id="mObra5" name="mObra5" value=""/>
		<input type="hidden" id="mTesofe" name="mTesofe" value=""/>
		<input type="hidden" id="mRetImpuestoCedular" name="mRetImpuestoCedular" value=""/>
		<input type="hidden" id="caNoContrarreciboPago" name="caNoContrarreciboPago" value=""/>
		<input type="hidden" id="RFC" name="RFC" value=""/>
		<input type="hidden" id="Periodo13" name="Periodo13" value=""/>
		<input type="hidden" id="ADEFAS" name="ADEFAS" value=""/>
		<input type="hidden" id="mImporteIvaArrenda" name="mImporteIvaArrenda" value=""/>
		<input type="hidden" id="mImporteIvaHonorarios" name="mImporteIvaHonorarios" value=""/>
		<input type="hidden" id="mImporteISRLaudos" name="mImporteISRLaudos" value=""/>
		<input type="hidden" id="mPenalizacion" name="mPenalizacion" value="0"/>
		<input type="hidden" id="mISROtros" name="mISROtros" value=""/>
		<input type="hidden" id="mImporteIva6" name="mImporteIva6" value="0"/>
		<input type="hidden" id="mimporteISRResico" name="mimporteISRResico" value="0"/>
		
		<!-- hidden para validar si existe la referencia CXP  -->
		<input type="hidden" id="existeCXPRef" name="existeCXPRef" value=""/>
		
		<input type="hidden" id="esPagoPenas" name="esPagoPenas" value="0"/>
		
		<input type="hidden" id="nFolioPagoRef" name="nFolioPagoRef" value=""/>
		<input type="hidden" id="cTipoPagoRef" name="cTipoPagoRef" value=""/>
				
		<div id="container" style="width: 100%" class="container" > 
			<div class="card-header"> <h3> Disminucion de Devengado </h3> </div>
			<hr class="mt-3"/>
			
			<div id="divImprimePoliza">
				<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir();"/>Poliza
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="cDesccripcion"> Descripcion: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">																			
					<input type="text" name="cDesccripcion" id="cDesccripcion" class="form-control form-control-sm" />		 							 						
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="caNoContrarrecibo"> ContraRecibo: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
					<input type="text" name="caNoContrarrecibo" id="caNoContrarrecibo" class="form-control form-control-sm" />		 							 						
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="cBeneficiario"> Grupo: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
					<select name="cBeneficiario" id="cBeneficiario" onchange="grupoRetencion()" class="form-select form-select-sm"></select>		 							 						
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="id_caso"> Folio: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																			
					<input type="text" name="id_caso" id="id_caso" class="form-control form-control-sm" readonly/>		 							 						
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="cxpBuscar"> CXP: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
					<input name="cxpBuscar" type="text" class="form-control form-control-sm" id="cxpBuscar" value="" size="15"/>		 							 						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<input name="Buscar" type="button" id="Buscar" value="Buscar" onclick="Busqueda()" class="btn btn-secondary btn-sm"/>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="mImporteTotal"> Importe Total: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																			
					<input name="mImporteTotal" type="text" id="mImporteTotal" class="form-control form-control-sm" value="$0.00" size="15" style="text-align: right;" readonly/>	 							 						
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="mDisminucion"> Disminucion: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<input name="mDisminucion" type="text" id="mDisminucion" class="form-control form-control-sm" value="$0.00" size="15" style="text-align: right;" onkeypress="return validarNumerico(event);" onblur="ajusteDisminucionDevengado();" onfocus="Sinfrmt(this)"/>																				 							 						
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="caNoContrarreciboRef"> Ref. CXP: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
					<input name="caNoContrarreciboRef" type="text" id="caNoContrarreciboRef" value="" size="15" class="form-control form-control-sm"/>&nbsp;						 							 					
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<input name="buscarCXP" type="button" id="buscarCXP" value="Buscar" onclick="buscarReferenciaCXP()" class="btn btn-secondary btn-sm"/>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
					<label for="mDisminucion"> Ref. RFC: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<input name="RFCRef" type="text" class="form-control form-control-sm" id="RFCRef" value="" size="15"/>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
					Pago Penas: &nbsp;<input type="checkbox" id="chk_PagoPenas" name="chk_PagoPenas" class="form-check-input" value="" />  												 							 						
				</div>
			</div>

			<br/>

			<label id="esperardet">
				<div align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif" height="30" />
				</div>
			</label>
			
			<h5> Devengado </h5>
			<hr class="mt-3"/>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<table id="Detalle" class="table table-striped table-bordered">				
						<thead>
							<tr align="center">
								<th>ContraRecibo</th>
								<th>RFC</th>
								<th>Folio Documento</th>
								<th>Fecha Aplicaci&oacute;n</th>
								<th>Tipo de Documento</th>
								<th>E.P.</th>
								<th>Total</th>
								<th>IVA HONORARIOS</th>
								<th>IVA ARRENDAMIENTO</th>
								<th>I.S.R. (HONORARIOS)</th>
								<th>I.S.R. (ARRENDAMIENTOS)</th>
								<th>FLETES (4.0%)</th>
								<th>APORTE I.M.D.T. (0.2%)</th>
								<th>APORTE C.N.I.C. (0.2%)</th>
								<th>INSPECCION DE OBRA (0.5%)</th>
								<th>PENALIZACIONES</th>
								<th>IMPUESTO CEDULAR</th>
								<th>ISR LAUDOS</th>
								<th>Mes</th>
								<th>Evento</th>
								<th>CC</th>
								<th>ISR OTROS</th>
								<th>IVA 6%</th>
								<th>ISR RESICO</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>

			<h5> Disminucion </h5>
			<hr class="mt-3"/>
			
				
			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<table id="Disminucion" class="table table-striped table-bordered">					
						<thead>
							<tr align="center">
								<th>ContraRecibo</th>
								<th>RFC</th>
								<th>Folio Documento</th>
								<th>Fecha Aplicaci&oacute;n</th>
								<th>Tipo de Documento</th>
								<th>EP</th>
								<th>Total</th>
								<th>IVA HONORARIOS</th>
								<th>IVA ARRENDAMIENTO</th>
								<th>ISR. (HONORARIOS)</th>
								<th>ISR (ARRENDAMIENTOS)</th>
								<th>FLETES (4.0%)</th>
								<th>APORTE IMDT (0.2%)</th>
								<th>APORTE CNIC (0.2%)</th>
								<th>INSPECCION DE OBRA (0.5%)</th>
								<th>PENALIZACIONES</th>
								<th>IMPUESTO CEDULAR</th>
								<th>ISR LAUDOS</th>
								<th>MES</th>
								<th>EVENTO</th>
								<th>CC</th>
								<th>ISR OTROS</th>
								<th>IVA 6%</th>
								<th>ISR RESICO</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			
			<div id="dialog-form" title="Aplicación Presupuestal/Contable">
				<div id="divEspera" align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif" height="30">
				</div>
				<div id="divAplica">
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
				<div id="dialog-Procesando" title="Procesando">
		  			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
				</div>
			
				<div id="dialog-firmantes" title="Firmantes">
					<h5> Datos Firmantes VºBº </h5>
					<hr class="mt-3">
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cNombreVoBo" class="form-label"> Nombre: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cNombreVoBo" name="cNombreVoBo"/>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPaternoVoBo" class="form-label"> Ap. Paterno: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cPaternoVoBo" name="cPaternoVoBo"/>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cMaternoVoBo" class="form-label"> Ap. Materno: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cMaternoVoBo" name="cMaternoVoBo"/>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPuestoVoBo" class="form-label"> Puesto: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cPuestoVoBo" name="cPuestoVoBo"/>
						</div>
					</div>
					
					<h5> Datos Firmantes Autorización </h5>
					<hr class="mt-3">
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cNombreAut" class="form-label"> Nombre: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cNombreAut" name="cNombreAut"/>
						</div>
					</div>		
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPaternoAut" class="form-label"> Ap Paterno: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cPaternoAut" name="cPaternoAut"/>
						</div>
					</div>		
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cMaternoAut" class="form-label"> Ap Materno: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cMaternoAut" name="cMaternoAut"/>
						</div>
					</div>			
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPuestoAut" class="form-label"> Puesto: </label>		
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">								
							<input class="form-control form-control-sm" id="cPuestoAut" name="cPuestoAut"/>
						</div>
					</div>	
				</div>
			</div>
		</div>
	</form>
</body>
</html>
