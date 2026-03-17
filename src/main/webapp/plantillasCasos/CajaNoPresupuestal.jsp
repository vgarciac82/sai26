<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.sai.contabilidad.utils.db.CloseObject"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.controller.DocPolizaEncabezadoBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.caja.*"%>
<%@page import="java.util.*" %>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null)
		response.sendRedirect("../index.jsp");

	String folioSAI = c.getFolio();
	int id_caso=c.getIdCaso();
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String cUR = "";
	cUR = usuario.getU_UR();	
	int id_oper = c.getCasoOperacion(0).getIdOperacion();
	System.out.println(id_oper);
	
	Connection conn = null;
	
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic( GestionInterface.ATT_CONEXION );
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	conn = ( new DocPolizaEncabezadoBusinessLogic() ).getConnection();
	int mesAbierto=DocPolizaEncabezadoManager.readnMesAbierto( conn, cCentroContable, cUR );
	CloseObject.closeObject( conn );
	
	String fAplicacion[]=CajaBusinessLogic.readfAplicacion(folioSAI.split("-")[2],cCentroContable,cUR);
	
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);
	
	String fCaptura=CajaBusinessLogic.readfCaptura(folioSAI.split("-")[2],cCentroContable);
	System.out.println(fCaptura);
	
	String fAppStr = fAplicacion[0].split("/")[2] + "-" + fAplicacion[0].split("/")[1] + "-" + fAplicacion[0].split("/")[0];
	
	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);
	String numeroEmpleadoElabora = usuario.getNumeroEmpleado();
	
	
	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<title>Modulo de caja</title>

<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../Generador/js/Firmantes.js"></script>
<script type="text/javascript" src="js/CajaNoPresupuestal.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
	var muestraDivImprimePoliza = false;
	var numeroEmpleadoElabora = "<%=numeroEmpleadoElabora%>";
	var nfoliocaja = <%=folioSAI.split("-")[2]%>;
	var id_oper=<%=id_oper%>;
	var pointer=-1;
	var totalCuentas=0;
	var eventoConf=new Array();
	var oTable;
	var oTabled;
	var regEvto=0; //num evto (pendiente actualizacion para editar dt)
	var success=false;
	var cont=false;
	var firmante=false;
	var evento = "";
	var tablaSolicitudes;
	var tablaSolicitudesL;
	var dt_Devolucion;
	var dt_Bonificacion;
	var dt_Reintegro;
	var dt_RendimientoFID;
	var dt_Comision;
	var dt_DevolucionL;
	var eventoDev=0;
	var subCuentaFondo = "";
	var limpia=" ";
	var reImprime="NO";
	var viaticosInsertados="NO";
	var update=false;
	var es_pago=false;
	var foliosComprobar=new Array();
	var foliosSA= new Array();
	var montosSA = new Array();
	var totalSA;
	var oTableSA;

	var cNombreElabora = "<%=cNombreElabora%>";
	var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
	var cPuestoElabora = "<%=cPuestoElabora%>";
	
var esConsulta = <%=esConsulta%>;
/* Variable que indica si el pago es con firma (FIEL) */
var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
var cCentroContable = "<%=cCentroContable%>";

$("#pb_cancel", parent.window.document).hide();
//$("#pb_leave", parent.window.document).hide();
$("#pb_save", parent.window.document).hide();
$("#pb_send", parent.window.document).hide();

function cat_CuentaBeneficiario(){
		try{
			var done=false;
			querySelectPost("readCuentaBeneficiario","ctabBeneficiario",{
				asyc:true,
				callback : function() {	
						queryFormPost("readNoCuentaBeneficiario","NoCuentasB",  {async:false});	
						done=true;
				}
			});
		} catch(ex){
			
			Swal.fire("Error 00CTAB_RFCjs.\r\r", ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.","error");
			}
		}//fin catalogo beneficiario	

function cat_beneficiario(){
		var formName  = "FormContrato";
		var inputName = "cTipoRfc";
		var inputRFCTarget = "hbuscabeneficiario";
		var inputDRFCTarget = "dRFC";
		var nombreArchivoPadre="CAJA";
		var nevto="";
		evento = $("#cEvento").val();
		nevto=$("#cEvento").val();
		evento = evento.substring(0,1);
		nevto = nevto.substring(4);
		
		if (evento=="8" && ((nevto=="12") || (nevto=="13")) )
			$("#cTipoRfc").val("1");
		else if (evento=="8" && ((nevto!="4") && (nevto!="5") && (nevto!="6") && (nevto!="9") )  )
			$("#cTipoRfc").val("3");
		else if (evento=="8" && ((nevto=="5") || (nevto=="6")) )
			$("#cTipoRfc").val("10");
		else if (evento=="8" && ((nevto=="9")) )
			$("#cTipoRfc").val("10,3");			
		else
			$("#cTipoRfc").val("0,1,2,3,4,5,7");
		window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=' + formName + '&inputName=' + inputName + '&inputRFCTarget=' + inputRFCTarget + '&inputDRFCTarget=' + inputDRFCTarget +'&nombreArchivoPadre='+nombreArchivoPadre, 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
	}	
	
function cat_FFM(){
		var formName  = "FormContrato";		
		var inputFFMTarget = "hFFM";
		var inputDFFMTarget = "dFFM,";
		var nombreArchivoPadre="CAJA";
		var nevto="";
		evento = $("#cEvento").val();
		nevto=$("#cEvento").val();
		evento = evento.substring(0,1);
		nevto = nevto.substring(4);

		window.open('../Generador/CatalogoFFM.jsp?formName=' + formName + '&inputFFMTarget=' + inputFFMTarget + '&inputDFFMTarget=' + inputDFFMTarget +'&nombreArchivoPadre='+nombreArchivoPadre, 'SubCuentas_Fondo', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
		eventoDev=4;
	}

function addToTableDetail() {
	if(eventoConf[pointer][4] == "35_1_11" || eventoConf[pointer][4] == "35_2_10")
		$("#CTABAN_SNP").val($("#CTABAN_FFM").val());
	
		oTabled.fnAddData([		   
			'<input type="text" id="ndocRenglon" name="ndocRenglon" value="' + regEvto + '" >',			
			'<input type="text" id="Evento" name="Evento" value="'+eventoConf[pointer][4]+'" >',	
			'<input type="text" id="mImporte" name="mImporte" value="'+withOutFormat("mMonto")+'">', 
			'<input type="text" id="mImporteNegativo" name="mImporteNegativo" value="'+Number(withOutFormat("mMonto"))*-1+'" >', 
			'<input type="text" id="ALMd" name="ALMd" value="'+$("#ALM").val()+'">', 
			'<input type="text" id="CTABd" name="CTABd" value="'+$("#CTABAN_SNP").val()+'" >',
			'<input type="text" id="OBGTd" name="OBGTd" value="'+$("#hPartida").val()+'" >',			
			'<input type="text" id="RFCd" name="RFCd" value="'+$("#hbuscabeneficiario").val()+'" >',
			'<input type="text" id="FFMd" name="FFMd" value="'+$("#hFFM").val()+'" >' 
	    ]);	
		
		 $("#ALM").val(" ");
		 $("#CTABAN_SNP").val(" ");
		 $("#CTABAN_FFM").val(" ");	
		 $("#hPartida").val(" "); 		
		 $("#hbuscabeneficiario").val(" ");
		 $("#hFFM").val(" ");
	}

function addToShowTable(){
	
		if( eventoDev==2){
			oTable.fnAddData([regEvto, $("#CTABAN_SNP").val(), $("#mMonto").val()	]);	
			eventoDev=3;
		
		} else if (eventoDev==3){
			oTable.fnAddData([regEvto, $("#hbuscabeneficiario").val(), $("#mMonto").val()	]);
			
		} else if(eventoDev==4) {
			oTable.fnAddData([regEvto,$("#hFFM").val(),$("#mMonto").val()]);
			subCuentaFondo = $("#hFFM").val();
			eventoDev=3;
		
		}else{	
				var cuentaB=$("#CTABAN_SNP").val();
				var rfc=$("#hbuscabeneficiario").val();
				
				if(rfc.length==1 && cuentaB.length>1){
					
				} else{
					if(limpia=="RFC")
						rfc=" ";
					if(limpia=="CTAB")
						cuentaB=" ";
				}
				
				oTable.fnAddData([regEvto,cuentaB+$("#hPartida").val()+$("#ALM").val()+rfc,$("#mMonto").val()]);	
				
				if(rfc.length==0 && cuentaB.length>1) {
					limpia="CTAB";
				}
				else if(rfc.length>1 && cuentaB.length==0){
					limpia="RFC";
				}
		}
	}

function loadTableDetail(ndocRenglon,Evento,mImporte,mImporteNegativo,ALMd,CTABd,OBGTd,RFCd,FFMd)
	{		
		oTabled.fnAddData([		   
			'<input type="text" id="ndocRenglon" name="ndocRenglon" value="' + ndocRenglon + '" >',			
			'<input type="text" id="Evento" name="Evento" value="'+Evento+'" >',	
			'<input type="text" id="mImporte" name="mImporte" value="'+mImporte+'">', 
			'<input type="text" id="mImporteNegativo" name="mImporteNegativo" value="'+mImporteNegativo+'" >', 
			'<input type="text" id="ALMd" name="ALMd" value="'+ALMd+'">', 
			'<input type="text" id="CTABd" name="CTABd" value="'+CTABd+'" >',
			'<input type="text" id="OBGTd" name="OBGTd" value="'+OBGTd+'" >',			
			'<input type="text" id="RFCd" name="RFCd" value="'+RFCd+'" >', 
			'<input type="text" id="FFMd" name="FFMd" value="'+FFMd+'" >'
	    ]);			
	}

function loadShowTable(ndocRenglon,mMonto,cSubcuenta)
	{
		oTable.fnAddData([	
			ndocRenglon,	
			cSubcuenta,
			mMonto			
		]);		
	}

function eventCompleted()
	{
		return (pointer+1)==totalCuentas;		
	}

function clearEvent()
	{
		eventoConf.length=0;
		eventoConf=new Array();	
	}

function loadEvent()
	{				
		hideButtonsDetail();
		
		if($.trim(eventoConf[pointer][8])=="" && pointer==1){						
				addToTableDetail();
				addToShowTable();				
				hideButtonsDetail();
				clearButtonsDetail();
				var acumulado=parseFloat(withOutFormat("mMonto")) + parseFloat(withOutFormat("nTotal"));
				$("#nTotal").val("$"+acumulado);	
				$("#mTotalLbl").text($("#nTotal").val());
				$("#mTotal").val($("#nTotal").val());
				$("#capturaDetalle").hide();
				$("#Datos_Cheque").hide();
				$("#mMonto").val("0.0");
				$("#cEvento").val("");
				$("#dEvento").val("");
				$("#btncEvento").show();
				$("#guardar").attr("disabled",false);
				$("#ue").attr("disabled", true);
				$("#enviar").attr("disabled",true);
				$("#mMonto").attr("disabled",false);
				$("#btnDevolucion").hide();
				$("#btnBonificacion").hide();	
				$("#btnPagoDeMas").hide();	
				$("#btnReintegros").hide();	
				$("#btnRendimientosFID").hide();
				$("#btnSaldoAntiguedad").hide();
				$("#btnRendimientos").hide();
				$("#btnComision").hide();
				$("#btnDevolucionLAUDO").hide();
				$(".datosComision").hide();
				$("#idMeta").hide();		
				pointer=-1;						
			}	
				
			$("#cEventoList").val(eventoConf[0][4]+"%");
			queryFormPost("tipo_evento", {async: false});
			queryFormPost("tipoSaldo", {async: false});
			queryFormPost("opcionesSaldoInicial", {async: false});		
			//queryFormPost("readUnidadesMetas", {async: false });
			
			$("#cEventoTipo2").val($("#cEventoTipo").val()); // 1 Anterior | 2 Ambos
			$("#cSaldoTipo2").val($("#cSaldoTipo").val()); // A | D | E
			$("#cCC2").val(<%=cCentroContable %>);
			$("#cBtn1").val($("#cBoton1").val());
			$("#cRg1").val($("#cRegistro1").val());
			$("#cBtn2").val($("#cBoton2").val());
			$("#cRg2").val($("#cRegistro2").val());	 
												
		switch (parseInt($("#cEventoTipo").val())){
			case 0: 
				break;
			case 1: 
				 $("#CheckSI").show();
				 $("#selEvento").attr("disabled",true);
				 $("#selEvento").attr("checked",true);
				 $("#ccSA").val($("#cCC2").val());
				 $("#cTipoSA").val($("#cSaldoTipo2").val());								
				 habilita();
				break;
			case 2:
				 $("#CheckSI").show(); 
				 $("#selEvento").attr("disabled",false);
				 $("#ccSA").val($("#cCC2").val());
				 $("#cTipoSA").val($("#cSaldoTipo2").val());
				break;
			case 3:
				//$("#btnComision").show();
				$("#registroComision").show();				
				if($("#existeUnidadMeta").val() == "1"){
					querySelectPost("readMetas", "meta", {async: false });
					$("#idMeta").show();
				}
				break;
			default : 
				break;
		}
		
		// Ingresamos la funcion para agregar saldo inicial segun evento		
		cargaSubCuenta(eventoConf[pointer][8]);
		$("#nidgrupoevento").val(eventoConf[pointer][1]);
		$("#cTipoPoliza").val(eventoConf[pointer][7]);
		
	}

function habilita(){
	if ( $("#selEvento").prop("checked")) {
			$("#esSaldoInicial").val(1);
			if (parseInt($("#cEventoTipo").val()) == 2){
				
				if ($("#cBoton1").val() != "NA")
				 	$("#"+ $("#cBoton1").val() +"").hide();
				 
				 $("#"+ $("#cRegistro1").val() +"").hide();
				 $("#"+ $("#cBoton2").val() +"").show();
				 $("#"+ $("#cRegistro2").val() +"").show(); 
			} 
			else{
				$("#btnSaldoAntiguedad").show();
				//$("#capturaDetalle").show();	
			}
	} else{
		$("#esSaldoInicial").val(0);
			if (parseInt($("#cEventoTipo").val()) == 2){
				
				 $("#"+ $("#cBoton2").val() +"").hide();
				 $("#"+ $("#cRegistro2").val() +"").hide(); 
				
				if ($("#cBoton1").val() != "NA")
				 	$("#"+ $("#cBoton1").val() +"").show();
				 
				 $("#"+ $("#cRegistro1").val() +"").show();
			} 
	}
}

function hideButtonsDetail()
	{
		$("#trCTABAN").hide();
		$("#trCTABANFFM").hide();
		$("#trhPartida").hide();
		$("#trALM").hide();
		$("#trhbuscabeneficiario").hide();	
		$("#trctabBene").hide();
		$("#trhFFM").hide();
			
	}

function clearButtonsDetail()
	{
		$("#CTABAN_SNP").val("");
		$("#CTABAN_FFM").val("");
		$("#hPartida").val("");
		$("#ALM").val("");
		$("#hbuscabeneficiario").val("");
		$("#ctabBeneficiario").val(" ");		
		$("#mMonto").blur();
		$("#hFFM").val("");	
	}

function moneyFrmt(id) 
	{
		try {			
			$("#" + id).formatCurrency();			
		}	catch(ex)	{
			Swal.fire("Error 0001js" , ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.", "error");
		}
	}

function quitaFmt(id)
	{
		try {			
			$("#" + id).val(withOutFormat(id));
		}	catch(ex) {
			Swal.fire("Error 0002js" , ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.", "error");
		}
	}
function withOutFormat(id)
	{
		var val = String($("#" + id).val());
			val = val.replace("$", "");
			val = val.replace(/,/g, "");
		
		if (val.indexOf("(") >= 0) 
		{
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
		}	
		return val;
	}

function teclasEspeciales(car)
 	{
 		var cars=[8,46,37,38,39,40,13];//borrados,flechas y enter
 		var permitido=true;
 		
 		for (var i=0;i<cars.length;i++)
 			if(cars[i]==car)
 				permitido=false;
 		
 		return permitido;
 	}

function cargaDevoluciones()
	{
		var sOrder = ""; 
	    var param =""; 	   	    	   
	    var zTabla = "R_CARGA_DEVOLUCIONES";	    
	    var camposWhere = " where VE.mMontoRemanente>0 and TE.cUnidadEjecutora='"+$("#ue").val()+"' AND TD.cEvento NOT IN ('8_2_1') order by TE.nFolioCaja ";
	    
	    var groupFilter="";  
	    tablaSolicitudes.fnClearTable();			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
		{			
			for (var i = 0; i < j.length; i++) {				
				tablaSolicitudes.fnAddData([ j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3 ]);
			}	
				
		});		
		eventoDev=2;
	}
	
function cargaBonificaciones()
	{
		var sOrder = ""; 
	    var param =""; 
	    var eventoComprobar="''";
	    var zTabla = "R_CARGA_BONIFICACION";

	    
	    if (eventoConf[0][4]=="35_2_7")//Pago Acreedores Por Pago de mas
	    	eventoComprobar = "'35_1_3'";
	    else if (eventoConf[0][4]=="35_1_2_A")//REGISTRO DE BONIFICACION DE COMISIONES BANCARIAS
	    	eventoComprobar = "'35_2_1_A'";
	    
	    var groupFilter="";  
		var camposWhere = " WHERE cunidadejecutora ='"+$("#ue").val()+"' and cevento like "+eventoComprobar+" order by nFolioCaja ";   
	    tablaSolicitudesB.fnClearTable();			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
		{			
			for (var i = 0; i < j.length; i++) {				
				tablaSolicitudesB.fnAddData( [ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5 ]);				
			}	
				
		});		
		eventoDev=2;
	}
	
function cargaReintegro()
	{
		var sOrder = ""; 
	    var param = ""; 
	    var esReintegro = "S";
	    var zTabla = "R_CARGA_REINTEGROS";

	    if (eventoConf[0][4].split("_")[0] == "5" && (eventoConf[0][4].split("_")[2]=="2" || eventoConf[0][4].split("_")[2]=="3"))// VERIFICA QUE SEA REINTEGRO O SOLO REGISTRO DE CAJA
	    	esReintegro = "N";
	    
	    var groupFilter="";  
		var camposWhere = " WHERE cEvento LIKE '"+eventoConf[0][4].split("_")[0]+"_"+eventoConf[0][4].split("_")[1]+"_1' AND cunidadejecutora = '"+$("#ue").val()+"'AND (nFolioComprobacion IS NULL OR nFolioComprobacion = 0) AND comprobado IS NULL order by enc.nFolioCaja ";  
	    tablaSolicitudesR.fnClearTable();			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
		{			
			for (var i = 0; i < j.length; i++) {				
				tablaSolicitudesR.fnAddData([ j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3 ]);				
			}	
				
		});		
		eventoDev=2;
	}
	
function cargaRendimientosFID()
	{
		var sOrder = ""; 
	    var param = ""; 
	    var zTabla = "R_CARGA_RENDIMIENTOS";	    
	      
		var camposWhere = " WHERE cEvento LIKE '"+eventoConf[0][4].split("_")[0]+"_1%' AND cunidadejecutora = '"+$("#ue").val()+"'AND (nFolioComprobacion IS NULL OR nFolioComprobacion = 0) AND comprobado IS NULL order by enc.nFolioCaja ";  
	    tablaSolicitudesMB.fnClearTable();			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
		{			
			for (var i = 0; i < j.length; i++) {				
				tablaSolicitudesMB.fnAddData([ j[i].Col0,j[i].Col1,j[i].Col2 ]);				
			}	
				
		});		
		eventoDev=2;
	}
	
function cargaDevolucionesL(){
	var sOrder = ""; 
    var param =""; 
    var zTabla = "R_CARGA_DEVOLUCIONES_L";

    var groupFilter="";  
	var camposWhere = " WHERE cUnidadEjecutora='"+$("#ue").val()+"' ";  
    tablaSolicitudesL.fnClearTable();			
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
	{			
		for (var i = 0; i < j.length; i++) {				
			tablaSolicitudesL.fnAddData([ j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3 ]);
		}	
			
	});		
	eventoDev=2;
}

function cargaSubCuenta(subcuenta)
	{
		if(subcuenta=="RFC" && es_pago) 
		{//mostrar cuando el tipo de póliza sea EG y las sub cuentas son RFC y CTAB
			if (eventoConf[0][4]=="8_2_5" || eventoConf[0][4]=="8_2_6" || eventoConf[0][4]=="8_2_11"){ 
				//Se pone por codigo duro los lnombres de los eventos por que es la unica forma de identificarlos ya que estos eventos llevan como subcuenta un RFC que es de una cuenta Bancaria()
				$("#trhbuscabeneficiario").show();
				$("#ctabBeneficiario").val(" ");
				$("#trctabBene").hide();
			} else{
				$("#trhbuscabeneficiario").show();
				$("#trctabBene").show();
			}	
		}else if(subcuenta=="RFC" && !es_pago ) {
				$("#trhbuscabeneficiario").show();
				$("#ctabBeneficiario").val(" ");
				$("#trctabBene").hide();				
		}else if(subcuenta=="CTAB"){
			if((eventoConf[0][4]=="35_1_11" || eventoConf[0][4]=="35_2_10")){
				$("#trCTABAN").hide();
				$("#trCTABANFFM").show();
			} else {
				$("#trCTABAN").show();
				$("#trCTABANFFM").hide();				
			} 
		}						
		else if(subcuenta=="OBGT")
			$("#trhPartida").show();
		else if(subcuenta=="ALM")
			$("#trALM").show();		
		else if(subcuenta=="FFM")
			$("#trhFFM").show();
	}

function cargaCuenta()
	{
		var sOrder = ""; 
	    var param =""; 
	    var zTabla = "R_CARGA_EVENTO";
	    var groupFilter="";  
		var camposWhere = " where EVT.Evento ='"+$("#cEvento").val()+"' and r.cUR='"+$("#ue").val()+"' order by nDocRenglon ";  
	    			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
		{
			totalCuentas=j.length;
			
			for (var i = 0; i < j.length; i++)
			{				
				eventoConf[i]=new Array(j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10);				
				
			}
			if(   ($.trim(j[0].Col8)=="RFC"    || $.trim(j[0].Col8)=="CTAB")  &&
				  !($.trim(j[0].Col6)=="ABONO"  && $.trim(j[1].Col6)=="ABONO") && 
				  ($.trim(j[1].Col8)=="RFC"    || $.trim(j[1].Col8)=="CTAB")  && 
				  ($.trim(j[0].Col8)!=$.trim(j[1].Col8)) && $.trim(j[0].Col7)=="EG") //VERIFICA EVENTOS DE PAGO; SUBCUENTAS = RFC Y CTAB  y TIPO DE POLIZA= EG	
				{
				es_pago=true;
				}	
			pointer=0;
			regEvto++;
			loadEvent();

			if($.trim(j[0].Col4)=="8_2_16" || $.trim(j[0].Col4)=="8_1_2" || $.trim(j[0].Col4)=="8_2_13" ) // VERIFICA EVENTOS DE DEVOLUCION
				{
					$("#btnDevolucion").show();
					$("#registroDevolucion").show();
				}		
			else if($.trim(j[0].Col4)=="8_2_8" || $.trim(j[0].Col4)=="8_2_15") // VERIFICA EVENTOS DE DEVOLUCION LAUDOS LIQUIDACION
			{
				$("#btnDevolucionLAUDO").show();
				$("#registroDevolucionL").show();
			}
			else if($.trim(j[0].Col4)=="35_1_2"){
					$("#btnBonificacion").show();
					$("#registroBonificacion").show();
					$("#capturaDetalle").hide();
					
			} else if($.trim(j[0].Col4)=="35_2_7"){//sam
					$("#btnPagoDeMas").show();
					$("#registroBonificacion").show();
					$("#capturaDetalle").hide();
			} else if($.trim(j[0].Col4).split("_")[0]=="5" && ($.trim(j[0].Col4).split("_")[2]=="2" || $.trim(j[0].Col4).split("_")[2]=="3"))//
				{					
					if ($("#selEvento").prop("checked")){
					}
					else {
						//Anita
						if ($.trim(j[0].Col4).split("_")[2]=="3"){
							eventoConf[3][4]=$.trim(j[0].Col4)+"_N";
							$("#btnReintegros").show();
							$("#registroReintegro").show();
						}
						else {
							$("#btnReintegros").show();
							$("#registroReintegro").show();
						}
					}
				} else if($.trim(j[0].Col4).split("_")[0]=="35" && $.trim(j[0].Col4).split("_")[1]=="2"){
					$("#btnRendimientosFID").show();
					$("#registroRendimientoFID").show();
				}	
						
			if($.trim(j[0].Col8)==$.trim(j[1].Col8)) //VERIFICA EVENTOS DE TRASPASO (SUBCUENTAS IGUALES)	
				{
				eventoConf[0][4]=$.trim(j[0].Col4)+"_A";
				eventoConf[1][4]=$.trim(j[0].Col4)+"_B";
				}
			if(i==4 && $.trim(j[0].Col4).split("_")[0]=="10" && $.trim(j[0].Col4).split("_")[1]=="5")  // es para eventos de nomina ya que son 4 registros contables y genera dos detalles PERO SOLO EL PRIMER RENLON DEL DETALLE OCUPA EL MOTOR CONTABLE POR ESO 
				eventoConf[3][4]=$.trim(j[0].Col4)+"_N";    //EN EL SEGUNDO DETALLE SE LE CONCATENA UNA "N" PARA QUE NO APLIQUE EL MOTOR	
		});	
	}
	

function muestraDetalle() 
	{  		
		hideButtonsDetail();

		if($.trim($("#cEvento").val())!="")
			{
				$("#capturaDetalle").show();
				cargaCuenta();
				queryFormPost("readUnidadesMetas", {async: false });
				
				if ($.trim($("#cEvento").val())=="8_2_1") {
					$("#beneficiarioViat").show();
					$("#btnBeneficiario").hide();
				} else {
					$("#btnBeneficiario").show();
					$("#beneficiarioViat").hide();
				}
					
			}
		else
			$("#capturaDetalle").hide();
	}


function procesar()
	{		
		$( "#dialog-Procesando" ).dialog( "open" );		
		return true;//breturnVal;
	}

function creaTablas()
	{
		$("#cMes").val($("#fechaAplicacion").val().toString().split("-")[1]);
		$("#cCentroContable").val(<%=cCentroContable %>);
		$("#ueje").val($("#ue").val());
		queryFormPost("tcajaDetalleDelete,tcajaEncabezadoDelete,tCajaEncabezadoCreate,tCajaDetalleCreate", 
		{
			async : false, 
			callback : function() 
			{	
				queryFormPost("montoSolicitudUpdate", {async:false});	
				success=true;
			}
		});
	}
	

function mostrarMotivoRechazo()
	{
		$("#CausaRegreso").dialog("open");
	}

function OpenDialogDevoluciones()
	{
		if($.trim($("#ue").val())=="0")
			{
				Swal.fire("Seleccione","Es necesario seleccionar la unidad ejecutora","warning");
				$("#ue").focus();
				return false;
			}
		cargaDevoluciones();		
		setTimeout('tablaSolicitudes.fnAdjustColumnSizing()',2000);	
		setTimeout('$("#DialogGastosPorComprobar").dialog("open")',1000);
	}

function OpenDialogBonificaciones()
	{
		var DataTable = $("#dt_Bonificacion").dataTable().fnGetData();
		dt_Bonificacion.fnClearTable();
		if($.trim($("#ue").val())=="0")
			{
				Swal.fire("Seleccione","Es necesario seleccionar la unidad ejecutora","warning");
				$("#ue").focus();
				return false;
			}
		cargaBonificaciones();		
		$("#tblSolicitudesB tbody").click(function(event) { 
			       compruebaRemanente();
		    	});
				
		setTimeout('tablaSolicitudesB.fnAdjustColumnSizing()',2000);	
		setTimeout('$("#DialogBonificacion").dialog("open")',1000);
		
	}
	
function OpenDialogReintegros()
	{
		if($.trim($("#ue").val())=="0")
			{
				Swal.fire("Seleccione","Es necesario seleccionar la unidad ejecutora","warning");
				$("#ue").focus();
				return false;
			}
		cargaReintegro();		
		setTimeout('tablaSolicitudesR.fnAdjustColumnSizing()',2000);	
		setTimeout('$("#DialogReintegros").dialog("open")',1000);
	}


function OpenDialogoRendimientosFID()
	{
		if($.trim($("#ue").val())=="0")
			{
				Swal.fire("Seleccione","Es necesario seleccionar la unidad ejecutora","warning");
				$("#ue").focus();
				return false;
			}
		cargaRendimientosFID();		
		setTimeout('tablaSolicitudesMB.fnAdjustColumnSizing()',2000);	
		setTimeout('$("#DialogRendimientosFID").dialog("open")',1000);
	}
	
function OpenDialogDevolucionesLAUDOS(){
	if($.trim($("#ue").val())=="0")
		{
			Swal.fire("Seleccione","Es necesario seleccionar la unidad ejecutora","warning");
			$("#ue").focus();
			return false;
		}
	cargaDevolucionesL();		
	setTimeout('tablaSolicitudesL.fnAdjustColumnSizing()',2000);	
	setTimeout('$("#DialogGastosPorComprobarL").dialog("open")',1000);
}
	
function OpenDialogSA()
	{
		oTableSA = $("#tblSaldoA").dataTable(
						{
							bAutoWidth : false,
							oLanguage: es_mx,
							bPaginate : true,
							bFilter : true,
							bSort : true,    
							iDisplayLength : 400,
							//sScrollYInner : "100%",
							//sScrollX: "100%",
    						bScrollCollapse: false,
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							bServerSide : true,
							aaSorting: [[ 1, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]	+ "/crud?rt=t&ql=fn_l_SaldosAntiguedad('"+$("#cSaldoTipo2").val()+"','"+$("#cCC2").val()+"')",
								aoColumns: [
											{ sName: "Folio" },
											{ sName: "nCuenta"   },
											{ sName: "cSubCuenta" },
											{ sName: "cCentroContable"	},
											{ sName: "fecha"	},
											{ sName: "Original"	},
											{ sName: "Saldo"},
											{ sName: "Disponible"	}],
							 fnDrawCallback: function( oSettings ) {
            						if (foliosSA.length > 0){
								      	cambiaDatosSA();
								      	sumaSeleccionados();
								      }
                			}
						}
					);
					
		 	$("#tblSaldoA tbody").click(function(event) { 
			       sumaSeleccionados();
		    	}); 	
		    	
		    if ( $("#cSaldoTipo2").val() == 'E')
		    	$("#eventoE").attr("style","visibility=visible");	
		    else
		    	$("#eventoE").attr("style","visibility=hidden");	
		    
		
			$("#DialogSaldosAntiguedad").dialog("open");
		
	}
	

	
function sumaSeleccionados()
	{
	   var total = 0;
	   var totalA = 0;
	   var totalD = 0;
	   var esEspecial = 0;
	   totalSA = 0;
	   var cnt = 0;
	   var tbl = $("#tblSaldoA").dataTable();
	   var nlines = $(tbl.fnGetNodes()).length;
	   foliosSA = new Array();
	   montosSA = new Array();
	  
	   // TODO: Validamos si es evento especial
	    if ( $("#cSaldoTipo2").val() == "E")
	    	esEspecial = 1;
	   	
    	for(var i=0; i<nlines; i++){
         var x = tbl.fnGetNodes()[i].cells[0].childNodes[0];
         
         if ($(x)[0].checked) {
	         var y = tbl.fnGetNodes()[i].cells[6];
	         var z = tbl.fnGetNodes()[i].cells[7].childNodes[0];
	         
	         if( esEspecial == 1){
	         	var esp = tbl.fnGetNodes()[i].cells[1];
	         	var e = $(esp)[0].innerText.substring(0,1);
	         	if ( parseFloat($(y)[0].innerText) >= parseFloat($(z).val()) ){
	         	
	         		if (e == "1")
	         			totalD +=Number(parseFloat( $(z).val()).toFixed(2));
	         		else if (e == "2")
	         			totalA +=Number(parseFloat( $(z).val()).toFixed(2));
	         			
			         foliosSA[cnt] = $(x).attr("id");
			         montosSA[cnt] = parseFloat($(z).val()).toFixed(2);
			         cnt++;
		         }
		         else{
		         	Swal.fire("Favor de rectificar!!!","El importe no debe de exeder al saldo pendiente", "info");
		         	$(z).val(parseFloat($(y)[0].innerText).toFixed(2));
		         }
	         }
	         else{
	         	 if ( parseFloat($(y)[0].innerText) >= parseFloat($(z).val()) ){
		         	 total += Number(parseFloat( $(z).val()).toFixed(2));
			         foliosSA[cnt] = $(x).attr("id");
			         montosSA[cnt] = parseFloat($(z).val()).toFixed(2);
			         cnt++;
		         }
		         else{
		         	Swal.fire("Favor de rectificar!!!","El importe no debe de exeder al saldo pendiente", "info");
		         	$(z).val(parseFloat($(y)[0].innerText).toFixed(2));
		         }
	         }
         }
        }
        
        if (esEspecial == 1){
        	if (totalA == totalD){
        		total = totalA;
        	}
        	else{
        		Swal.fire("Favor de rectificar!!!","Los importes Deudores y Acreedores tiene que ser iguales","info");
        	}
        }
    
    	$("#totalSaldoAnt").val(total.toFixed(2));
    	totalSA = Number(total.toFixed(2));
    }


function cambiaDatosSA()
	{
	   var tbl = $("#tblSaldoA").dataTable();
	   var nlines = $(tbl.fnGetNodes()).length;  
	   	
    	for(var i=0; i<nlines; i++){
         var x = tbl.fnGetNodes()[i].cells[0].childNodes[0];
         var y = tbl.fnGetNodes()[i].cells[7].childNodes[0];
         
         	for (var j=0; j< foliosSA.length; j++){
         		if ( $(x).attr("id") == foliosSA[j]){
         			$(x)[0].checked = true;
         			$(y).val(montosSA[j]);
         		}
         	}
        }  
    }


function creainputsSA(){
		
	var err = 0;
	$("#totalSaldoAntiguedad").val(totalSA);
	
	err = -1;
	queryFormPost("tSaldoAntiguedadEncabezadoInsert", {async: false , callback : function()	{err = 1;}});
	
	for ( var i = 0; i < foliosSA.length; i++ ){
		$("#cIdSaldoAnt").val(foliosSA[i]);
		$("#cMontoSaldoAnt").val(montosSA[i]);
		queryFormPost("tSaldoAntiguedadDetalleInsert", {async : false, callback: function(){err = 2;}});
	}
		
	queryFormPost("tCajaEncabezadoSIUpdate", {async : false , callback: function(){err = 3;}});
		
	if (err == 3)
		Swal.fire("Error","Se actualizo el saldo inicial","error");
	else if (err == 2)
		Swal.fire("Error","Error al actualizar tCajaEncabezado(esSaldoInicial)","error");
	else if (err == 1)
		Swal.fire("Error","Error en los saldos iniciales en el Detalle","error"); 
	else if (err == -1)
		Swal.fire("Error","Error en los saldos iniciales en el Encabezado","error");
	else
		Swal.fire("Error","Error en creainputsSA","error");
	
}

function OpenDialogRendimientos()
	{
		Swal.fire("Ok","Mostar los rendimientos registrados", "success");
	}	

function fill_Devolucion(folio,concepto,remanente,rfc)
	{
		dt_Devolucion.fnAddData([	
				folio,	
				concepto,
				remanente,
				rfc
			]);	
	}

function fill_Bonificacion(folio,concepto,importe)
	{
		dt_Bonificacion.fnAddData([	
				folio,	
				concepto,
				importe
			]);	
	}

function fill_Reintegro(folio,concepto,importe)
	{
		dt_Reintegro.fnAddData([	
				folio,	
				concepto,
				importe
			]);	
	}

function fill_RendimientoFID(folio,concepto,importe)
	{
		dt_RendimientoFID.fnAddData([	
				folio,	
				concepto,
				importe
			]);	
	}
	
function fill_DevolucionL(folio,concepto,remanente,rfc)
{
	dt_DevolucionL.fnAddData([	
			folio,	
			concepto,
			remanente,
			rfc
		]);	
}

function fnClickTblSolicitudes(event)
	{ 
		dt_Devolucion.fnClearTable();
		$(tablaSolicitudes.fnSettings().aoData).each(
			function (){
				$(this.nTr).removeClass('row_selected');
		});
				
		$(event.target.parentNode).addClass('row_selected');
		var aPost = tablaSolicitudes.fnGetPosition(event.target.parentNode);		
		var aData = tablaSolicitudes.fnGetData(aPost);
		
		$("#NoCajaDevolucion").val(aData[0]);
		fill_Devolucion(aData[0],aData[1],aData[2],aData[3]);
		$("#hbuscabeneficiario").val(aData[3]);
	
	}
	
function fnClickTblSolicitudesR(event)
	{ 
		dt_Reintegro.fnClearTable();
		$(tablaSolicitudesR.fnSettings().aoData).each(
			function (){
				$(this.nTr).removeClass('row_selected');
		});
				
		$(event.target.parentNode).addClass('row_selected');
		var aPost = tablaSolicitudesR.fnGetPosition(event.target.parentNode);		
		var aData = tablaSolicitudesR.fnGetData(aPost);
		
		$("#NoCajaReintegro").val(aData[0]);
		fill_Reintegro(aData[0],aData[1],aData[2],aData[3]);

	}

function fnClickTblSolicitudesMB(event)
	{ 
		dt_RendimientoFID.fnClearTable();
		$(tablaSolicitudesMB.fnSettings().aoData).each(
			function (){
				$(this.nTr).removeClass('row_selected');
		});
				
		$(event.target.parentNode).addClass('row_selected');
		var aPost = tablaSolicitudesMB.fnGetPosition(event.target.parentNode);		
		var aData = tablaSolicitudesMB.fnGetData(aPost);
		
		$("#NoCajaReintegro").val(aData[0]);
		fill_RendimientoFID(aData[0],aData[1],aData[2]);
	
	}

function fnClickTblSolicitudesL(event)
{ 
	dt_DevolucionL.fnClearTable();
	$(tablaSolicitudesL.fnSettings().aoData).each(
		function (){
			$(this.nTr).removeClass('row_selected');
	});
			
	$(event.target.parentNode).addClass('row_selected');
	var aPost = tablaSolicitudesL.fnGetPosition(event.target.parentNode);		
	var aData = tablaSolicitudesL.fnGetData(aPost);
	
	$("#NoCajaDevolucion").val(aData[0]);
	fill_DevolucionL(aData[0],aData[1],aData[2],aData[3]);
	$("#hbuscabeneficiario").val(aData[3]);
	
}
	
function comprobarRemanente()
{
	queryFormPost("readRemanente", {async : false});	
	if( Number( withOutFormat("mMonto") ) <=  Number( $("#mMontoRemanente").val() ))
		return true;
	else
		return false;		
}

function compruebaImporteReintegro()
{
	queryFormPost("readImporteReintegro", {async : false});	
	if( Number( withOutFormat("mMonto") ) <=  Number( $("#mMontoSolicitud").val() ))
		return true;
	else
		return false;		
}

$(document).ready(function() 
	{
		init();
		$("input.AyudaSyC").subIniciaDlg();
		querySelectPost("readUnidadResponsable", "ue", {async: false });
		hideButtonsDetail();
		
		$("#capturaDetalle").hide();		
		$("#Datos_Cheque").hide();
		$("#agregar").button();
		$("#Agrega_Datos_Cheque").button();
		$("#Edita_Datos_Cheque").button();
		$("#clearEvento").button();
		$("#imprimir").button();
		$("#guardar").button();
		$("#enviar").button();
		$("#descartar").button();
		$("#guardarRevision").button();	
		$("#ReImprimir").button();
		$("#Edita_Datos_Cheque").hide();
			
		$( ".datepicker" ).datepicker({
			showOn: "button",			
			buttonImage: "../images/calendar.gif",
			buttonImageOnly: true,
			minDate:new Date(<%=fAplicacion[1]%>),
			maxDate:new Date(<%=fAplicacion[2]%>)
			
		});
		
		$("#agregar").click(
			function(e)
			{				
				if($("#ue").val() == "0")
					{
						Swal.fire("Seleccione","Debe seleccionar una unidad ejecutora.","warning");
						$("#ue").focus();
						return false;
					}
				
				if($.trim($("#cConcepto").val()) == "")
					{
						Swal.fire("Capture","Es necesario escribir un Concepto.","warning");
						$("#cConcepto").focus();
						return false;
					}
				
				if ($.trim(eventoConf[pointer][4]) == "35_1_11" || $.trim(eventoConf[pointer][4]) == "35_2_10")				
					var subcuentas= 
					{
						"RFC":"hbuscabeneficiario",						
						"CTAB":"CTABAN_FFM",		
						"ALM":"ALM",
						"OBGT":"hPartida",
						"FFM":"hFFM"
					};
				else 
					var subcuentas= 
					{
						"RFC":"hbuscabeneficiario",
						"CTAB":"CTABAN_SNP",							
						"ALM":"ALM",
						"OBGT":"hPartida",
						"FFM":"hFFM"
					};
				
				if( $.trim(eventoConf[pointer][8])==null )
				eventoConf[pointer][8]="";
				
				if( $.trim(eventoConf[pointer][8])!="" )
					{						
				 		 if( $.trim($("#"+subcuentas[eventoConf[pointer][8]]).val())=="" )
				 			 {
				 			   var subC=subcuentas[eventoConf[pointer][8]];
				 			 	if(subC=="hbuscabeneficiario"){
					  			 	Swal.fire("Seleccione","Debe seleccionar un Beneficiario.","info");
					  			 	$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
				  			 	
				 			 	} else if(subC=="CTABAN") {
				  			 		Swal.fire("Seleccione","Debe seleccionar una Cuenta Bancaria.","info");
					  			 	$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
				 			 	} else if(subC=="CTABAN_FFM") {
				  			 		Swal.fire("Seleccione","Debe seleccionar una Cuenta Bancaria.","info");
					  			 	$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
						 		
				 			 	} else if(subC=="hPartida") {
				 			 		Swal.fire("Seleccione","Debe seleccionar una Partida.","info");
					  			 	$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
						 		
				 			 	} else if(subC=="ALM") {
				 			 		Swal.fire("Seleccione","Debe seleccionar un Almacen","info");
					  			 	$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
						 		
				 			 	} else if(subC=="hFFM") {
				 			 		Swal.fire("Seleccione","Debe seleccionar una subcuenta del Fondo.", "info");
					  			 	$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
						 		
				 			 	} else {
				 			 		Swal.fire("Verifique","SubCuenta inesperada Favor de notificarlo","error");
							 		$("#"+subcuentas[eventoConf[pointer][8]]).focus();
							 		return false;
						 		}
				 			 } 
				 			 var subC=subcuentas[eventoConf[pointer][8]];											
					} 				
				
				if(withOutFormat("mMonto")=="0.00")
					{
						Swal.fire("Monto cero!","Debe ingresar un monto.","error");
						$("#mMonto").focus();
						return false;
					}
					
								
				//****************comprueba remanente**************
					if( ($.trim(eventoConf[pointer][4])=="8_2_16" || $.trim(eventoConf[pointer][4])=="8_1_2") && viaticosInsertados=="NO"  )
					{						
						if(comprobarRemanente()){
							viaticosInsertados="SI";
						}else{
							Swal.fire("Revise","La devolución excede el remanente","info");
							$("#mMonto").focus();
							return false;						
						}
									
					}
				//*****************comprueba monto de reintegro*****************
					else if (eventoConf[0][4].split("_")[0] == "5" && (eventoConf[0][4].split("_")[2]=="2" || eventoConf[0][4].split("_")[2]=="3"))
					{
						if ($("#selEvento").prop("checked")){
						}
						else {
							if(compruebaImporteReintegro()){
								}
								else {
									return false;
									Swal.fire("Verifique!","El importe capurado es mayor al de la solicitud origen","error");
								}
							}
					}
						
				
					if(es_pago && $("#ctabBeneficiario").val()=="NA" ) {
						if(($.trim(eventoConf[pointer][4])=="8_2_5" || $.trim(eventoConf[pointer][4])=="8_2_6"))
						{
						$("#nctabBeneficiario").val($("#ctabBeneficiario").val());
						}
						else if($.trim(eventoConf[pointer][4]) != "8_2_11" && $.trim(eventoConf[pointer][4]) != "17_3_2"){
							Swal.fire("Capture","Seleccione una Cuenta Bancaria del Beneficiario","warning");	
							$("#ctabBeneficiario").focus();
							return false;
						}
					} else{
				         $("#nctabBeneficiario").val($("#ctabBeneficiario").val());
					}				
				
				if((eventoConf[pointer][6]=="ABONO" && eventoConf[pointer][9].toString().substring(Number($("#fechaAplicacion").val().split("-")[1])-1,Number($("#fechaAplicacion").val().split("-")[1]))=="1")
		    		||
		      		(eventoConf[pointer][6]=="CARGO" && eventoConf[pointer][10].toString().substring(Number($("#fechaAplicacion").val().split("-")[1])-1,Number($("#fechaAplicacion").val().split("-")[1]))=="1")
			    )
				{
						Swal.fire("Revise","La cuenta se encuentra bloqueada en la fecha de aplicación y no se puede realizar un "+eventoConf[pointer][6], "info");
						return false;
				}
				$("#btncEvento").hide();
				$("#clearEvento").hide();								
				addToShowTable();
				regEvto=pointer+1;
				
				if(totalCuentas==(pointer+1)) {							
						addToTableDetail();					
						hideButtonsDetail();
						clearButtonsDetail();
						var acumulado=parseFloat(withOutFormat("mMonto")) + parseFloat(withOutFormat("nTotal"));
						$("#nTotal").val("$"+acumulado);	
						$("#mTotalLbl").text($("#nTotal").val());
						$("#mTotal").val($("#nTotal").val());
						$("#mMonto").val("0.0");
						$("#cEvento").val("");
						$("#btncEvento").show();
						$("#ue").attr("disabled", true);
						$("#mMonto").attr("disabled",false);
						$("#btnDevolucion").hide();
						$("#btnBonificacion").hide();
						$("#btnPagoDeMas").hide();
						$("#btnReintegros").hide();
						$("#btnRendimientosFID").hide();
						$("#btnSaldoAntiguedad").hide();
						$("#btnRendimientos").hide();
						$("#capturaDetalle").hide();						
						$("#enviar").attr("disabled",true);
						$("#btnComision").hide();
						$("#btnDevolucionLAUDO").hide();
						//$(".datosComision").hide();
						$("#idMeta").hide();
						pointer=-1;
						
						if ($("#Evento").val()=="8_2_3") {  //VERIFICA QUE SEA UN EVENTO DE LAUDOS PARA QUE PUEDA CAPTURARSE EL NOMBRE DEL BENEFICIARIO DEL CHEQUE
							$("#Datos_Cheque").show();
							$("#guardar").attr("disabled",true);
						
						} else{
							$("#Datos_Cheque").hide();
							$("#guardar").attr("disabled",false);
							//*********Comprobacion**********
							//TODO: Validar Saldo Inicial
							if($("#Evento").val()=="35_1_2_A"||$("#Evento").val()=="35_2_7"){
								var DataTable = $("#dt_Bonificacion").dataTable().fnGetData();
								var nodes = $("#dt_Bonificacion").dataTable().fnGetNodes();
								var renglon,ndocrenglon=0;
								for (i = 0; i < nodes.length; i ++){
									renglon = dt_Bonificacion.fnGetData(nodes[i]);
									$("#nfoliocajaComprobado").val(renglon[0]);
									$("#mImporteComprobado").val(renglon[2]);
									queryFormPost("readNDocRenglonComprobacion", {async: false });
									ndocrenglon=parseInt($("#nDocRenglon").val())+1;									
									$("#nDocRenglon").val(ndocrenglon);
									queryFormPost("tBonificacion_ComisionCreate", {async: false });
									queryFormPost("tEstadoDeCuentaComprobacionesDetalleCreate", {async: false });
								}
							}
						}					
					
				} else {
					
						if(eventoConf[pointer][8]==eventoConf[pointer+1][8]) //VERIFICA EVENTOS DE TRASPASO (SUBCUENTAS IGUALES)	
						{
							addToTableDetail();
						}	
						if (eventoConf[pointer][8]=="FFM"){
							$("#hFFM").val(subCuentaFondo);
							$("#btnFFM").attr("disabled","disabled");
							$("#hFFM").attr("disabled","disabled");																						
						}
						$("#mMonto").attr("disabled","disabled");
						pointer++;
						loadEvent();						
					}

			}	
		);
		
		
		$("#guardar").click(
			function(e) {
					queryFormPost("consultaModuloViaticos", {async: false});
					if ($("#Evento").val()=="8_2_1" && $("#esModuloViaticos").val() == 0){
						if($("#nombreComision").val()==""){
							Swal.fire("Datos faltantes","El nombre de la comisión no debe estar vacio, complete para continuar...","warning");
							$("#nombreComision").focus();
							return;
						}
						
						if($("#existeUnidadMeta").val() == "1" && $("#meta").val() == "-1"){
							Swal.fire("Seleccione","Favor de Seleccionar la Meta que se asignara al Anticipo.","warning");
							$("#idMeta").show();
							return;
						}
						
						if($("#nIdComision").val() == "0"){
							Swal.fire("Seleccione","Favor de Seleccionar la comisión de Viaticos.","warning");
							$("#btnComision").show();
							$("#idMeta").show();							
							return; 
						} else {
							agregaComision();
						}
					}
															
					$.blockUI({message : "Guardando informacion. Por favor espere ..."});							
					$("#pb_save", parent.window.document).click();					
					$("#cCentroContable").val("<%=cCentroContable%>");
					$("#NombreBeneficiarioCheque").val( $("#Nombre_Cheque").val() +" "+$("#APaterno_Cheque").val()+" "+$("#AMaterno_Cheque").val()  );
					$("#btncEvento").hide();
					
					if(!firmante) {
						creaTablas();					
						
						if($("#nIdComision").val() != "0" && $("#esModuloViaticos").val() == 0){
							guardaRelacionComision();
						}
						
						if ($("#Evento").val() == "8_2_1" && $("#existeUnidadMeta").val() == "1"){
							queryFormPost("existetRelacionMetaRead",{async: false });
							$("#idMeta").show();
							
							var existeRelacionMeta = parseInt( $.trim( $("#existeRelacionMeta").val() ) );
							if (existeRelacionMeta == 0)
								queryFormPost("tRelacionMetaCreate", {async:false});
							
						}
						
						if ($("#Evento").val()=="8_2_3") {  //VERIFICA QUE SEA UN EVENTO DE LAUDOS PARA QUE PUEDA CAPTURARSE EL NOMBRE DEL BENEFICIARIO DEL CHEQUE
							queryFormPost("conceptotCajaEncabezadoUpdate", {async: false });
						}
						else	{
							queryFormPost("tCajaEncabezadoUpdate,ActualizaCajaComprobado", {async: false
																							, callback: function(){
																								if ($("#esSaldoInicial").val() == 1){
																										creainputsSA();
																									}																											
																							} }); 
						}
						
					}
				//***************************************************///
					if(success)// Valida si se actualizaron las tablas
					{
						if(!cont || reImprime=="SI") {
							tipoFirmantes();
						}
						
						
						if(firmante){
							$("#momentoGuarda").val(1);
							
														
							solicitudPolizaCaja();//manda a llamar para imprimir la SOLICITUD 
							$("#enviar").attr("disabled",false);
							firmante=false;
						}
					}
				
					else
						Swal.fire("Ocurrió un error al intentar guardar los datos","reintente en un momento.","error");
					
					succes=false;	
					$.unblockUI();		
				}		
		);
		
		$("#Agrega_Datos_Cheque").click(
			function(e) {
				if ($("#Nombre_Cheque").val()=="") {
					Swal.fire("Capture","Falta Capturar el NOMBRE del Beneficiario para el CHEQUE","info");
					return;
					
				}else if ( $("#APaterno_Cheque").val()=="") { 
					Swal.fire("Capture","Falta Capturar el APELLIDO PATERNO del Beneficiario para el CHEQUE","info");
				    return;
				    
				}else {
					  $("#Nombre_Cheque").prop('readonly',true);
					  $("#Nombre_Cheque").addClass('notEditable');
					  $("#APaterno_Cheque").prop('readonly',true);
					  $("#APaterno_Cheque").addClass('notEditable');
					  $("#AMaterno_Cheque").prop('readonly',true);
					  $("#AMaterno_Cheque").addClass('notEditable');
					  $("#guardar").attr("disabled",false);
					  $("#enviar").attr("disabled",true);	
					  $("#Edita_Datos_Cheque").show();
					  $("#Agrega_Datos_Cheque").hide();
					}  
			}	
		);
		
		
		$("#Edita_Datos_Cheque").click(
			function(e) {
			
				  $("#Nombre_Cheque").prop('readonly',false);
				  $("#Nombre_Cheque").removeClass('notEditable');
				  $("#APaterno_Cheque").prop('readonly',false);
				  $("#APaterno_Cheque").removeClass('notEditable');
				  $("#AMaterno_Cheque").prop('readonly',false);
				  $("#AMaterno_Cheque").removeClass('notEditable');
				  $("#Agrega_Datos_Cheque").show();
				  $("#Edita_Datos_Cheque").hide();
				  $("#guardar").attr("disabled",true);
				  $("#enviar").attr("disabled",true);
				 
			}	
		);
		
		
		$("#cConcepto").keydown(function(e)
		{ 
			var elemento=(e.target.id); 
			var car=e.which;
			var maxChars=299;
			
			if(car==219)
				e.preventDefault();			 		
			else if(teclasEspeciales(car) && $("#"+elemento).val().length > maxChars)
				{
					Swal.fire("Reduzca los caracteres","Ha llegado al número máximo de caracteres permitidos.","info");
					$("#"+elemento).val($("#"+elemento).val().substr(0, maxChars));
				}    				
		});	
		
		$("#cConcepto").bind('paste', function (e) 
	 	{   
			var elemento=(e.target.id);    		
			var charTemp=$("#"+elemento).val().replace("'","").replace("´","");			
				var maxChars=299;
			
			setTimeout(function () { 
				if($("#"+elemento).val().length > maxChars)
				{
					Swal.fire("Reduzca los caracteres","El texto excede el número máximo de caracteres permitidos.", "info");
					$("#"+elemento).text(charTemp);									
				}
				charTemp="";
			 });							
	  	});	
		
	  oTabled=$('#dt_catalogo_detaill').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bSort": false,
			      	"bInfo": false } );
	  
	  dt_Devolucion=$('#dt_Devolucion').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bJQueryUI" : true,
			      	"bSort": false,
			      	"bInfo": false } );
	 dt_Bonificacion=$('#dt_Bonificacion').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bJQueryUI" : true,
			      	"bSort": false,
			      	"bInfo": false } );	  
	 dt_Reintegro=$('#dt_Reintegro').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bJQueryUI" : true,
			      	"bSort": false,
			      	"bInfo": false } );
	dt_RendimientoFID=$('#dt_RendimientoFID').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bJQueryUI" : true,
			      	"bSort": false,
			      	"bInfo": false } );			      	
	dt_Comision=$('#dt_Comision').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bJQueryUI" : true,
			      	"bSort": false,
			      	"bInfo": false } );	 
	dt_DevolucionL=$('#dt_DevolucionL').dataTable({
					"iDisplayLength": 500,
			      	"bPaginate": false,
			      	"bFilter": false,
			      	"bJQueryUI" : true,
			      	"bSort": false,
			      	"bInfo": false } );
	  oTable=$('#dt_catalogo').dataTable({
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : false,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			aoColumns : [	
				{sName : "docRenglon",bVisible: false},
				{sName : "cSubcuenta"},
				{sName : "mImporte"}					
				],
			oLanguage : es_mx
		});
		
	  tablaSolicitudes =  $('#tblSolicitudes').dataTable({
				        "bPaginate": true,
	        			"bLengthChange": true,
	        			"bFilter": true,
	        			"bSort": true,
	        			"bInfo": true,
	        			//"bAutoWidth": true,
						//"sScrollY": 270,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",						
						//"bScrollCollapse": true,
						oLanguage: es_mx
			});
			
		tablaSolicitudesB =  $('#tblSolicitudesB').dataTable({
				        "bPaginate": true,
	        			"bLengthChange": true,
	        			"bFilter": true,
	        			"bSort": true,
	        			"bInfo": true,
	        			//"bAutoWidth": true,
						//"sScrollY": 270,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",						
						//"bScrollCollapse": true,
						oLanguage: es_mx
			});
		$("#btnAgregaSolicitudes").button().click(function() {
						alert("AGREGA SOL");
						
					});
		
		tablaSolicitudesR =  $('#tblSolicitudesR').dataTable({
				        "bPaginate": true,
	        			"bLengthChange": true,
	        			"bFilter": true,
	        			"bSort": true,
	        			"bInfo": true,
	        			//"bAutoWidth": true,
						//"sScrollY": 270,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",						
						//"bScrollCollapse": true,
						oLanguage: es_mx
			});
			
			tablaSolicitudesMB =  $('#tblSolicitudesMB').dataTable({
				        "bPaginate": true,
	        			"bLengthChange": true,
	        			"bFilter": true,
	        			"bSort": true,
	        			"bInfo": true,
	        			//"bAutoWidth": true,
						//"sScrollY": 270,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",						
						//"bScrollCollapse": true,
						oLanguage: es_mx
			});
			
			tablaSolicitudesL =  $('#tblSolicitudesL').dataTable({
				        "bPaginate": true,
		    			"bLengthChange": true,
		    			"bFilter": true,
		    			"bSort": true,
		    			"bInfo": true,
		    			//"bAutoWidth": true,
						//"sScrollY": 270,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",						
						//"bScrollCollapse": true,
						oLanguage: es_mx
			});
				
		
		//confirm("Esta seguro que desea autorizar la solicitud.\nPresione Aceptar para autorizar o Cancelar");
		$("#CausaRegreso").dialog({
			autoOpen : false,
			height : 300,
			width : 450,
			modal : true,
			buttons : 
			{
				"Aceptar" : function() {			
					if(id_oper==1)
						$("#motivoRechazo").val("");
					 else
						{
						    $("#motivoRechazo").val( $("#txtmotivoRechazo").val());
						    queryFormPost("updateMotivoRechazoCaja",{async : false,callback : function() {}});
							cont=true;
							$("#co_responsable").val("CONSULTA_CAJA");
							$("#id_oper").val("3");
							queryFormPost("UpdateCoperCaja", 
							{
							   async : false, 
					           callback : function() 
					            {update=true;}
				            });
        				   if(update)	
					         document.liberardocumento.submit();	
				           else
				           {
					         alert("Reintente en un momento.");
					         return false;
				           }
						}
					
					$(this).dialog("close");
					return false;
								
				},
				"Cancelar":function(){
					$(this).dialog("close");
				}
		}
		});
	
	$( "#dialog-Procesando" ).dialog({
				autoOpen: false,
				height: 200,
				width: 400,
				modal: true,
				open: function() 
				{
					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#nfoliocaja").val();
					var campo = "nfoliocaja";
					var tablaEnc ="tcajaencabezado";
					var campoCondicion ="nfoliocaja"; 
					var tablaDet = "tcajadetalle"; 
					var tipoAplicar = "CAJA";
					var fAplicar = $("#fechaAplicacion").val();
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					
					$.ajax({
							url:'../Generador/cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							//async: false,
							data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo,campo:campo,tablaEnc:tablaEnc,campoCondicion:campoCondicion,tablaDet:tablaDet,tipoAplicar:tipoAplicar,fAplicar:fAplicar},
							success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){																			
										success = true;
									}else{
										success = false;
										id_oper=2;
										alert( "El Documento No Se Aplicó:  " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesando" ).dialog( "close" );
							}
					});
					
				},
				close: function() {		
							if(success){
								queryFormPost("UpdateCoperCaja", 
									{
									async : false, 
									callback : function() 
									{		
										alert("Documento Aplicado Contablemente!");
										polizaCaja();
											
										if(es_pago)
										  	alert("Si requiere imprimir CHEQUE favor de ir a la opciòn Genera Ejercido/Pagado Manual que se encuentra en el menù Layouts ");	
									
										if ( $("#esAntiguedadSI").val() == 1){ 
											for ( var i = 0; i < foliosSA.length; i++ ){
													 $("#cIdSaldoAnt").val(foliosSA[i]);
													 $("#cMontoSaldoAnt").val(montosSA[i]);
													 queryFormPost("tSaldosAntiguedadUpdate",{async : false});
											}
											queryFormPost("saEstatusUpdate",{async : false});
										}
										
										document.liberardocumento.submit();
									}
								});
								
								
							}else{
								alert("Favor de Notificar al Administrador y Reintente en un momento");
								return false;
							}	
													
				}				
		}); //Fin del Dialog donde aplica contablemente
	
	$("#DialogGastosPorComprobar").dialog(
	{
		autoOpen : false,
		height : 600,
		width : 1100,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				
				if( $("#NoCajaDevolucion").val() == ""  ){ 
					Swal.fire("No ha seleccionado una solicitud a comprobar.", "Por favor seleccionela dando click en la tabla","info");
				
				} else {

					var concepto = $("#dt_Devolucion").find("td:eq(1)").text();
				  	$("#cConcepto").val(concepto);
				
					$(this).dialog("close");					
				}
				
				tablaSolicitudes.fnClearTable();
			},
			"Cancelar" : function() {
				$(this).dialog("close");
				dt_Devolucion.fnClearTable();
			}
		},
		open: function(){
			
			$("#tmpNoCaja").val("");
		}
	});
	
	$("#tblSolicitudes tbody tr").live("click", function(event){fnClickTblSolicitudes(event);} );
	
	$("#DialogBonificacion").dialog({
		autoOpen : false,
		height : 600,
		width : 1100,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				agregarSol();
				$(this).dialog("close");
			},
			"Cancelar" : function() {
				$(this).dialog("close");
				dt_Bonificacion.fnClearTable();
			}
		},
		open: function(){
			$("#tmpNoCaja").val("");
		}
	});
		
	
	$("#DialogReintegros").dialog({
		autoOpen : false,
		height : 600,
		width : 1100,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				if( $("#NoCajaReintegro").val() == ""  )
					Swal.fire("No ha seleccionado una solicitud.", "Por favor seleccionela dando click en la tabla","info");
				else
				{ 
					$(this).dialog("close");					
				}
				
				tablaSolicitudesR.fnClearTable();
			},
			"Cancelar" : function() {
				$(this).dialog("close");
				dt_Reintegro.fnClearTable();
			}
		},
		open: function(){
			
			$("#tmpNoCaja").val("");
	}
	});
	
	$("#DialogRendimientosFID").dialog({
		autoOpen : false,
		height : 600,
		width : 1100,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				
				if( $("#NoCajaReintegro").val() == ""  )
					Swal.fire("No ha seleccionado una solicitud.", "Por favor seleccionela dando click en la tabla","info");
				
				else{ 
					$(this).dialog("close");					
				}
				tablaSolicitudesMB.fnClearTable();
			},
			"Cancelar" : function() {
				$(this).dialog("close");
				dt_RendimientoFID.fnClearTable();
			}
		},
		open: function(){
			
			$("#tmpNoCaja").val("");
		}
	});
	
	$("#DialogGastosPorComprobarL").dialog(
	{
		autoOpen : false,
		height : 600,
		width : 1100,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				
				if( $("#NoCajaDevolucion").val() == ""  ){ 
					Swal.fire("No ha seleccionado una solicitud a comprobar.", "Por favor seleccionela dando click en la tabla","info");
				
				} else {

					var concepto = $("#dt_DevolucionL").find("td:eq(1)").text();
				  	$("#cConcepto").val(concepto);
				
					$(this).dialog("close");					
				}
				
				tablaSolicitudesL.fnClearTable();
			},
			"Cancelar" : function() {
				$(this).dialog("close");
				dt_DevolucionL.fnClearTable();
			}
		},
		open: function(){
			
			$("#tmpNoCaja").val("");
		}
	});
	
	$("#tblSolicitudesL tbody tr").live("click", function(event){fnClickTblSolicitudesL(event);} );
	
	$("#DialogSaldosAntiguedad").dialog({
		autoOpen : false,
		height : 800,
		width : 1100,
		modal : true,
		buttons : {
			"Aceptar" : function() {
					$("#mMonto").val(totalSA);
					$("#mMonto").attr("readonly","readonly");
					$("#mMonto").blur();
					$(this).dialog("close");
			},
			"Cancelar" : function() {
					$(this).dialog("close");
			}
		}
	});
	
	$("#tblSolicitudesR tbody tr").live("click", function(event){fnClickTblSolicitudesR(event);} );
	$("#tblSolicitudesMB tbody tr").live("click", function(event){fnClickTblSolicitudesMB(event);} );
	
	/*$("#autorizadoPorFielChk").attr("checked")
	$("#autorizadoPorFiel ").val("true");
	*/
	if( permitePagoSinFIEL ) {
		$("#AutorizaConFielTD").css("display","block");
	}else{
		$("#autorizadoPorFiel ").val("true");
	}
	
	});//Fin de on Ready

function agregarSol(){
				var DataTable = $("#tblSolicitudesB").dataTable().fnGetData();
				var renglon;
				var total=0.00;
				var i=0;
				foliosComprobar=new Array();
				$('#tblSolicitudesB input:checked').each(function(idx, elm) {
					
					var inputID = 'inp_' + $(this).attr("name");
					var importe= quitaFmt($("#" + inputID).val());
					
					renglon = DataTable[tablaSolicitudesB.fnGetPosition($(this).closest('tr')[0])];
					
 					fill_Bonificacion(renglon[1],renglon[2],numberFormat(importe));
 					total=(Math.round((total+ parseFloat(importe)) *100)/100);
					foliosComprobar[i]=renglon[1];
					i++;
				});
				if(total>0)
					$("#capturaDetalle").show();
				else
					$("#capturaDetalle").hide();
				$("#mMonto").val(total);
				$("#mMonto").prop('readonly',true);	
				$("#nfoliosComprobar").val(foliosComprobar);	
	}
	
 function numberFormat(numero){//SASV 12/05/2015 Formato con separadores de miles
        var resultado = "";
        numero=numero+"";
 
        if(numero.indexOf('-',0)==0)
        nuevoNumero=numero.replace(/\,/g,'').substring(1);
        else
            nuevoNumero=numero.replace(/\,/g,'');
 
        if(numero.indexOf(".")>=0)
            nuevoNumero=nuevoNumero.substring(0,nuevoNumero.indexOf("."));
 
        for (var j, i = nuevoNumero.length - 1, j = 0; i >= 0; i--, j++)
            resultado = nuevoNumero.charAt(i) + ((j > 0) && (j % 3 == 0)? ",": "") + resultado;

        if(numero.indexOf(".")>=0)
            resultado+=numero.substring(numero.indexOf("."));
 
        if(numero.indexOf('-',0)==0)
            return "-"+resultado;
          else
            return resultado;
        
    }
    
function quitaFmt( val ) {
		if(val=="")
			val="0";
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	
function compruebaRemanente(){
				var DataTable = $("#tblSolicitudesB").dataTable().fnGetData();
				var i=0;
				$('#tblSolicitudesB input:checked').each(function(idx, elm) {
					
					var inputID = 'inp_' + $(this).attr("name");
					var importe=parseFloat( quitaFmt($("#" + inputID).val())).toFixed(2);
										
 					renglon = DataTable[tablaSolicitudesB.fnGetPosition($(this).closest('tr')[0])];
					
 					var remanente=parseFloat(renglon[4]).toFixed(2);
 					
 					if(Number(importe)>Number(remanente)){
 						alert("El importe no puede ser mayor al remanente.");
 						$("#" + inputID).val(remanente);
 					}
 					
 					foliosComprobar[i]=renglon[1];
 					i++;
				});
}

function onLoadPlantilla(idOper) 
	{
		$("#folioSAI").val("<%=folioSAI%>");
		$("#nfoliocaja").val(nfoliocaja);	
		cargaCaja();
		
		$("#guardar").attr("disabled","disabled");
		$("#enviar").attr("disabled","disabled");
		$("#btnRechazo").hide();
		$("#btnDevolucion").hide();
		$("#btnBonificacion").hide();
		$("#btnPagoDeMas").hide();
		$("#btnReintegros").hide();
		$("#btnRendimientosFID").hide();
		$("#btnSaldoAntiguedad").hide();
		$("#btnRendimientos").hide();
		$("#registroDevolucion").hide();
		$("#registroBonificacion").hide();
		$("#registroReintegro").hide();
		$("#registroRendimientoFID").hide();
		$("#registroDevolucionL").hide();
		$("#dFolio").text("dciebckhe");
		$("#CheckSI").hide();
		$("#btnComision").hide();
		$("#btnDevolucionLAUDO").hide();
		$(".datosComision").hide();
		$("#registroComision").hide();
		$("#idMeta").hide();
		$("#beneficiarioViat").hide();
		$("#btnBeneficiario").hide();
	
		cargaDatosComision();
		
		if(id_oper==1) {
			$("#tdAutorizar").hide();
			$("#ReImprimir").hide();
			if ($("#Evento").val()=="8_2_3") {  //VERIFICA QUE SEA UN EVENTO DE LAUDOS PARA QUE PUEDA CAPTURARSE EL NOMBRE DEL BENEFICIARIO DEL CHEQUE
				$("#Datos_Cheque").show();
				$("#guardar").attr("disabled",true);
		    }
		}
	
		if($("#cTipoPoliza").val()=="EG" && $("#CTABd").val()!="" && $("#RFCd").val()!="" && $("#cCentroContable").val()=="10" )	
			es_pago=true;
		
		if(id_oper==2) //autorizacion
			{
				$("#cConcepto").attr("disabled","disabled");			
				$("#ue").attr("disabled","disabled");
				$("#meta").attr("disabled","disabled");
				$("#guardar").hide();
				$("#descartar").hide();
				$("#ReImprimir").hide();
				$("#Datos_Cheque").hide();
				
				var idComision = $("#nIdComision").val();
				oTableCom = $('#dt_Comision').dataTable(
						{
							"bProcessing": true,
							"bServerSide": true,
							"bDestroy": true,
							"bSort": true, 
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ViaticosComisiones&qw=nIdComision=" + idComision ,
							"bJQueryUI": true,
							"sScrollX": "100%",
							"sScrollY": "100%",
							"bPaginate": false,
							"bAutoWidth": false,
							"bInfo": false,
							aoColumns: [
										{ sName: "nIdComision" },
										{ sName: "cConcepto_Comision" },
										{ sName: "fInicio" },
										{ sName: "fFin" } 
										]	
						});
			}
			
			if(id_oper==3)//consulta
			{
				$(".datepicker").datepicker('destroy'); 
				$("#cConcepto").attr("disabled","disabled");			
				$("#ue").attr("disabled","disabled");
				$("#meta").attr("disabled","disabled");
				$("#tdEvento").hide();
				$("#guardar").hide();
				$("#descartar").hide();
				$("#enviar").attr("disabled","disabled");
				$("#guardar").hide();
				$("#descartar").hide();
				$("#OperacionesCaptura").hide();
				$("#tdAutorizar").hide();
				$("#guardar").hide();
				$("#clearEvento").hide();
			
				if( muestraDivImprimePoliza )
					$("#ReImprimir").show();
				else
					$("#ReImprimir").hide();
				
				$("#Datos_Cheque").hide();
				
				if( $("#cEsFirmaElectronica").val() != "S" )
					$("#EditaFirmas").css('visibility', 'visible');
					
			}
		
	}

function ResponsableSiguiente(id_oper){
	
	if(id_oper==1)
		return "AUTORIZA_CAJA";
	else if(id_oper==2)
		if($('input:radio[name=grpAutorizar]:checked').val()=="Si")
			return "CONSULTA_CAJA";
		else
			return "CONSULTA_CAJA";
}

function OperacionSiguiente(id_oper) {
	
	if(id_oper==1)
		return "autoriza_caja";
	else if(id_oper==2)
		if($('input:radio[name=grpAutorizar]:checked').val()=="Si")
			return "consulta_caja";
		else
			return "consulta_caja";
}

function Id_OperacionSiguiente(id_oper) {
	
	if(id_oper==1)
		return "2";
	else if(id_oper==2)
		if($('input:radio[name=grpAutorizar]:checked').val()=="Si")
			return "3";
		else
			return "3";

}
	
function onPostSubmit(id_oper){
	return true;
}
	
function onPostDisplay(id_oper){ 

}

function onSubmit(id_oper)				//validaciones del boton guardar
	{
		try {
	  		var p = window.parent;
	  		var valida_campos = true;
			
			try{											
				p.gestion.setOperador( $("#co_responsable").val() );
				if ( id_oper==1 ){					
					p.gestion.setFolio("<%=folioSAI%>");
					p.gestion.setFechaDocumento('<%=fCaptura%>');					
					p.gestion.setEjercicioFiscal($("#aejerciciofiscal").val());
					p.gestion.setConceptoMov("Caja no Presupestal");
					p.gestion.setMoneda("MXP");
					return true;					
				}
			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			
		} catch(ex) {
			alert("Error 0019js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
		}
	}				// fin de onSubmit()
	
	function cmdBorrar() 
			{	
				try
				{	
					queryFormPost("updateStatusCaja", {async: false});
					$("#pb_cancel", parent.window.document).click();					
				}
				catch(ex)
				{
					alert("Error 0023js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
				}
		}			
		
		function cmdReImprime() {	
			try{
				polizaCaja();
			}catch(ex){
				alert("Error 0023js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
			}
		}
						
function LimpiaEvento()
	{ 
		$("#cEvento").val(""); $("#nidgrupoevento").val("");
		$("#capturaDetalle").hide();
		$("#Datos_Cheque").hide();
		$("#dEvento").val("");
		$("#registroDevolucion").hide();
		$("#registroBonificacion").hide();
		$("#registroReintegro").hide();
		$("#registroRendimientoFID").hide();
		$("#registroComision").hide();
		$("#registroDevolucionL").hide();
		$("#btnDevolucion").hide();
		$("#btnBonificacion").hide();
		$("#btnPagoDeMas").hide();
		$("#btnReintegros").hide();
		$("#btnRendimientosFID").hide();
		$("#btnComision").hide();
		$("#btnDevolucionLAUDO").hide();
		$(".datosComision").hide();
		$("#idMeta").hide();
		$("#btnSaldoAntiguedad").hide();
		$("#btnRendimientos").hide();
		$("#ctabBeneficiario").val("");
		$("#nctabBeneficiario").val("");
		$("#hbuscabeneficiario").val("");
		$("#CTABAN_SNP").val("");
		$("#CTABAN_FFM").val("");
		$("#hFFM").val("");
		$("#mMonto").val("");
		es_pago=false;
		dt_Devolucion.fnClearTable();
		dt_Bonificacion.fnClearTable();
		dt_Reintegro.fnClearTable();
		dt_RendimientoFID.fnClearTable();
		dt_Comision.fnClearTable();
		dt_DevolucionL.fnClearTable();
		tablaSolicitudes.fnClearTable();
		tablaSolicitudesB.fnClearTable();
		tablaSolicitudesR.fnClearTable();
		tablaSolicitudesMB.fnClearTable();
		tablaComisiones.fnClearTable();
		$("#selEvento").attr("checked",false);
		$("#selEvento").attr("disabled",true);
		$("#CheckSI").hide();				
		
	 }

	function nombreMes(mes){
		switch (parseInt(mes)){
			case 1: return "Enero";
				break;
			case 2: return "Febrero";
				break;
			case 3: return "Marzo";
				break;
			case 4: return "Abril";
				break;
			case 5: return "Mayo";
				break;
			case 6: return "Junio";
				break;
			case 7: return "Julio";
				break;
			case 8: return "Agosto";
				break;
			case 9: return "Septiembre";
				break;
			case 10: return "Octubre";
				break;
			case 11: return "Noviembre";
				break;
			case 12: return "Diciembre";
				break;
			default : return "Error";
				break;
		}
		
	}
	
	function OpenDialogComisiones()
	{
        if($.trim($("#ue").val())=="0") {
            Swal.fire("Seleccione","Es necesario seleccionar la unidad ejecutora","warning");
            $("#ue").focus();
            return false;
        }
        
		queryFormPost("consultaNoEmpleado", {async: false});
		
		if($("#noEmpleadoRFC").val()=="")
			Swal.fire("No se puede continuar", "Ese beneficiarios no tiene numero de empleado, consulte al administrador", "error");
		
		var pop = window.open('../Generador/listaAgendas.jsp?idEmpleado=' + $("#noEmpleadoRFC").val() + '&formName=FormContrato', 'Comisiones', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
		pop.onunload = function () {
			llenarTabla();
		}
		 
	}
	
	function llenarTabla(){
		dt_Comision.fnClearTable();
		
		if ($("#idAgenda").val()!="") {
			$("#nIdComision").val($("#idAgenda").val());
			var arr = new Array();
			arr	= [$("#idAgenda").val(), $("#cConcepto").val(), $("#fechaIniAgenda").val(), $("#fechaFinAgenda").val()  ];
	      	dt_Comision.fnAddData(arr);	
		}
	}
	
	function agregaComision(){
		$.ajax({
			url : '../viaticos/guardarDatos',
			dataType : 'json',
			type :"GET",
			data : {
				"idAgenda": $("#idAgenda").val(),
				"nombreComision" : $("#nombreComision").val()
			},
			dataType:"json",
			async : false,
			success : function(agenda) {
				 if(agenda){
		              $("#nIdComision").val( agenda.idComision);
		              $("#nIdComisionReloj").val(agenda.idComisionReloj);
				}
			},
			error : function (err){
				Swal.fire("Ocurrio el siguiente error:", err.responseText, "error" );
					
				return false;}
		});

	}
	/*
	function cargaComisiones()
	{
		var zTabla = "R_CARGA_COMISIONES";		
		var arreglo = new Array();
		var arr = new Array();
	    tablaComisiones.fnClearTable();			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, MaxReg: "10", ajax:"true"}, function(j)
		{	
			var k = 0;			
			for (var i = 0; i < j.length; i++)
			{				
				//tablaComisiones.fnAddData([ j[i].Col0,j[i].Col3,j[i].Col1,j[i].Col2 ]);	
				arr	= [ j[i].Col0,j[i].Col3,j[i].Col1,j[i].Col2 ];
							
				arreglo.push(arr);
				k++;		
			}		
			
			if(k == 0){
				alert("No se encontraron comisiones; Favor de Solicitar la generacion de la Comisión.");
			}else{
				tablaComisiones.fnAddData(arreglo);
			}			
		});		
		
	}*/
	/*
	function fnClickTblComisiones(event)
	{ 
		dt_Comision.fnClearTable();
		$(tablaComisiones.fnSettings().aoData).each(
			function (){
				$(this.nTr).removeClass('row_selected');
		});
				
		$(event.target.parentNode).addClass('row_selected');
		var aPost = tablaComisiones.fnGetPosition(event.target.parentNode);		
		var aData = tablaComisiones.fnGetData(aPost);
		
		$("#nIdComision").val(aData[0]);
		fill_Comision(aData[0],aData[1],aData[2],aData[3]);
		
	}
	
	function fill_Comision(folio, concepto, fechaini, fechafin)
	{
		dt_Comision.fnAddData([	
				folio,	
				concepto,
				fechaini,
				fechafin
			]);	
	}
	*/
	function guardaRelacionComision(){
		var bRegresa = false;
		var existeRelacionComision = 0;
		
		queryFormPost("tRelacionComprobacionComisionesDelete, tRelacionComprobacionComisionesCreate", {async:false});
		
		queryFormPost("existetRelacionComprobacionComisionesRead",{async: false });
		
		existeRelacionComision = parseInt( $.trim( $("#existeRelacionComision").val() ) );
		
		if(existeRelacionComision == 1){
			bRegresa = true;	
		}
		
		return bRegresa;
	}
			
	function cargaDatosComision(){
		
		var existeRelacionComision = 0;
		var nIdComision = "0";
		queryFormPost("existetRelacionComprobacionComisionesRead",{async: false });
		
		existeRelacionComision = parseInt( $.trim( $("#existeRelacionComision").val() ) );
		
		if(existeRelacionComision == 1){

			queryFormPost("tRelacionComprobacionComisionesRead",{async: false });
			var zTabla = "R_CARGA_COMISION_CAJA";		
			var camposWhere = " where RelacionComprobacionComisiones.nFolioTramite = "+$("#nfoliocaja").val();
		    dt_Comision.fnClearTable();		    
					    			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, MaxReg: "10", ajax:"true"}, function(j)
			{				
				for (var i = 0; i < j.length; i++)
				{				
					dt_Comision.fnAddData([ j[i].Col0,j[i].Col3,j[i].Col1,j[i].Col2 ]);													
				}								
			});	
			
			$("#registroComision").show();			
			
		}
	}
	
	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}	
	
	function actualizaBenef(){
		
		$("#cIdRFC_RelacionGasto").val($("#hbuscabeneficiario").val());
		if ($("#cEventoTipo").val() == 3) {
			$("#btnComision").show();
			$(".datosComision").show();
		}
	}
	 
</script>

</head>
	<body id="dt_example" >
		<form id="FormContrato"  name="FormContrato">
			<input type="hidden" id="cTipoPago" name="cTipoPago"  value="CAJA"/>
			<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion"  value="0"/>
			<input type="hidden" id="numeroEmpleadoElabora" name="numeroEmpleadoElabora" value=""/>
			<input type="hidden" id="cEsFirmaElectronica" name="cEsFirmaElectronica" value="N"/>	
			<input type="hidden" id="paginas" name="paginas" value="0"/>
			<input type="hidden" id="dRFC" name="dRFC" value=""/>
			<input type="hidden" id="cTipoRfc" name="cTipoRfc" value=""/>
			<input type="hidden" id="mTotal" name="mTotal" value="0"/>			
			<input type="hidden" id="nfoliocaja" name="nfoliocaja" value=""/> 		
			<input type="hidden" id="nfoliocajaComprobado" name="nfoliocajaComprobado" value="0"/>
			<input type="hidden" id="nDocRenglon" name="nDocRenglon" value="0"/>
			<input type="hidden" id="mImporteComprobado" name="mImporteComprobado" value="0"/>
			<input type="hidden" id="nfoliosComprobar" name="nfoliosComprobar" value="0"/> 
			<input type="hidden" id="mesAbierto" name="mesAbierto" value="<%=mesAbierto%>" />
			<input type="hidden" id="NoCuentasB" name="NoCuentasB"  value="" />	
			<input type="hidden" id="cMes" name="cMes"  value="" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" />
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="" />			
			<input type="hidden" id="u_login" name="u_login" value="<%=usuario.getLogin()%>" /> 			
			<input type="hidden" id="cunidadresponsablecontable" name="cunidadresponsablecontable" value="RHQ" /> 		
			<input type="hidden" id="aejerciciofiscal" name="aejerciciofiscal" value="<%=fAplicacion[0].split("/")[2]%>"  /> 	
			<input type="hidden" id="cramo" name="cramo" value="<%=usuario.getU_Ramo()%>" /> 	
			<input type="hidden" id="id_caso" name="id_caso" value="<%=c.getIdCaso()%>" /> 		
			<input type="hidden" id="nidgrupoevento" name="nidgrupoevento" value="" />
			<input type="hidden" id="ueje" name="ueje" value="" />
			<input type="hidden" id="NombreBeneficiarioCheque" name="NombreBeneficiarioCheque" value=" " />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" />
			<input type="hidden" id="cNombreVo" name="cNombreVo" />
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo"/>
			<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO"/>
			<input type="hidden" id="cNombreA" name="cNombreA" />
			<input type="hidden" id="cPaternoA" name="cPaternoA" />
			<input type="hidden" id="cMaternoA" name="cMaternoA" />
			<input type="hidden" id="cPuestoA" name="cPuestoA" />
			<input type="hidden" id="firmanteAut" name="firmanteAut" />
			<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" value="PAGO_AUT">
			<input type="hidden" id="id_oper" name="id_oper" value="" />
			<input type="hidden" id="co_responsable" name="co_responsable" value="<%=usuario.getNombre()%>" />
			<input type="hidden" id="motivoRechazo" name="motivoRechazo" value="" />
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=usuario.getU_UR()%>" /> 	
			<input type="hidden" name="nTotal" id="nTotal" value="0" />	
			<input type="hidden" name="momentoGuarda" id="momentoGuarda" value="0" />
			<input type="hidden" name="NoCajaDevolucion" id="NoCajaDevolucion" value="0" />
			<input type="hidden" name="NoCajaBonificacion" id="NoCajaBonificacion" value="0" />	
			<input type="hidden" name="NoCajaReintegro" id="NoCajaReintegro" value="0" />	
			<input type="hidden" name="mMontoSolicitud" id="mMontoSolicitud" value="0" />	
			<input type="hidden" name="mMontoRemanente" id="mMontoRemanente" value="0" />			
			<input type="hidden" name="mMontoRemanenteComprobacion" id="mMontoRemanenteComprobacion" value="0" />			
			<input type="hidden" name="nctabBeneficiario" id="nctabBeneficiario" value=" " />	
		  	<input type="hidden" name="pago" id="pago" value=" " />
		  	<input type="hidden" name="tituloAplicacion" id="tituloAplicacion" value="<%=c.getTipoCaso().getGavetaAsociada()%>" />
		  	<input type="hidden" name="idGabinete" id="idGabinete" value="<%=c.getIdGabinete()%>" />	
		  	<input type="hidden" name="cEstatusPago" id="cEstatusPago" value=" " />
		  	<input type="hidden" name="cDocumento" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
		  	<input type="hidden" name="cEventoList" id="cEventoList" value="" />	
			<input type="hidden" name="cEventoTipo" id="cEventoTipo" value="0" />
			<input type="hidden" name="cSaldoTipo" id="cSaldoTipo" value="0" />
			<input type="hidden" name="totalSaldoAntiguedad" id="totalSaldoAntiguedad" value="0" />
			<input type="hidden" name="cIdSaldoAnt" id="cIdSaldoAnt" value="0" />
			<input type="hidden" name="cMontoSaldoAnt" id="cMontoSaldoAnt" value="0" />
			<input type="hidden" name="esAntiguedadSI" id="esAntiguedadSI" value="0" />
			<input type="hidden" name="cantidadSA" id="cantidadSA" value="0" />
			<input type="hidden" name="arrFoliosSA" id="arrFoliosSA" value="" />
			<input type="hidden" name="arrMontosSA" id="arrMontosSA" value="" />
			<input type="hidden" name="cBoton1" id="cBoton1" value="" />
			<input type="hidden" name="cRegistro1" id="cRegistro1" value="" />
			<input type="hidden" name="cBoton2" id="cBoton2" value="" />
			<input type="hidden" name="cRegistro2" id="cRegistro2" value="" />
			<input type="hidden" name="nIdComision" id="nIdComision" value="0" />
			<input type="hidden" name="cTipoTramite" id="cTipoTramite" value="<%=c.getTipoCaso().getGavetaAsociada()%>" />
			<input type="hidden" name="existeRelacionComision" id="existeRelacionComision" value="0" />
			<input type="hidden" name="idAgenda" id="idAgenda" value="0" />
			<input type="hidden" name="noEmpleadoRFC" id="noEmpleadoRFC" value="0" />
			<input type="hidden" name="motivoAgenda" id="motivoAgenda" value="0" />
			<input type="hidden" name="fechaIniAgenda" id="fechaIniAgenda" value="0" />
			<input type="hidden" name="fechaFinAgenda" id="fechaFinAgenda" value="0" />
			
			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
			<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=folioSAI.split("-")[2]%>"/>
			
			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>
						
			<input type="hidden" name="existeCajaChica" id="existeCajaChica" value=""/>
			<input type="hidden" id="autorizadoPorFiel" name="autorizadoPorFiel" value="false"/>
			<input type="hidden" id="existeUnidadMeta" name="existeUnidadMeta" value="0"/>
			<input type="hidden" id="existeRelacionMeta" name="existeRelacionMeta" value="0"/>			
			<input type="hidden" id="nIdComisionReloj" name="nIdComisionReloj" value="0"/>
			<input type="hidden" name="esModuloViaticos" id="esModuloViaticos" value="0"/>
			
			<select id="montosRemanente" name="montosRemanente" style="display: none;"></select>
			<div id="container" class="container" style="width: 80%">
			<h3 class="w-auto px-3" align="center">M&oacute;dulo de Solicitudes No Presupuestales</h3>
				<hr class="mt-3">
				<div class="row">
						<div class="col-md-2 mb-3">													
							<label for="cFolio" class="form-label"> Folio: </label>
							<input type="text" id="folioSAI" name="folioSAI" class="form-control form-control-sm" style="width: 12em;" value="<%=folioSAI%>" readonly/>							
							<h6 id="btnRechazo" ><a href="#" onclick="mostrarMotivoRechazo();" class="link-primary">Ver Motivo del rechazo</a></h6>
						</div>
						<div class="col-md-2 mb-3">
							<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>
							<input type="button" value="Imprimir Solicitud" id="ReImprimir" onClick="cmdReImprime()" class="btn btn-secondary"/>
						</div>
						<div class="col-md-4 mb-3">							
							<label for="fechaCaptura" class="form-label">Fecha de captura: </label>
							<input type="text"	id="fechaCaptura" name="fechaCaptura" class="form-control form-control-sm" style="width: 10em;" value="<%=fCaptura%>" readonly>						
						</div>
						
						<div class="col-md-4 mb-3">
							<label for="fechaAplicacion" class="form-label"> Fecha de aplicaci&oacute;n: </label>
							<input type="date"	id="fechaAplicacion" name="fechaAplicacion" class="form-control form-control-sm" style="width: 10em;" value="<%=fAppStr%>" >
						</div>
				</div>
				<div class="row">
					<div class="col-12">
						<label for="ue" class="form-label"> Unidad Ejecutora: </label>
							<select id="ue" name="ue" class="form-select form-select-sm">
									<option value="Z:">A</option>
							</select>
					</div>
				</div>
				<div class="row">
					<div class="col-4">	
						<div id="tdEvento">						
							<label for="EventoOrigen" class="form-label"> Evento: </label>								
							<div class="input-group">
								<input type="text" id="cEvento" class="form-control form-control-sm AyudaSyC" style="width: 10em !important;flex: none;" onchange="muestraDetalle()" readonly>													
								&nbsp;
								<input type="button" id="clearEvento" name="clearEvento" class="btn btn-secondary"onclick="LimpiaEvento()" value="Limpia Evento"/>
							</div>
						</div>
					</div>													
					<div class="col-8">
						<span id="btnDevolucion" ><a href="#" onclick="OpenDialogDevoluciones();" class="link-primary">SELECCIONAR DOCUMENTO PARA DEVOLUCION!</a></span>
						&nbsp;<span id="btnBonificacion" ><a href="#" onclick="OpenDialogBonificaciones();" class="link-primary">SELECCIONAR DOCUMENTO PARA BONIFICACION!</a></span>
					    &nbsp;<span id="btnPagoDeMas" ><a href="#" onclick="OpenDialogBonificaciones();" class="link-primary">SELECCIONAR DOCUMENTO PARA PAGO POR PAGO DE MAS</a></span>
						&nbsp;<span id="btnReintegros" ><a href="#" onclick="OpenDialogReintegros();" class="link-primary">SELECCIONAR DOCUMENTO DE INGRESO DE REINTEGRO!</a></span>
						&nbsp;<span id="btnSaldoAntiguedad" ><a href="#" onclick="OpenDialogSA();" class="link-primary">SELECCIONA EL SALDO INICIAL!</a></span>
						&nbsp;<span id="btnRendimientos" ><a href="#" onclick="OpenDialogRendimientos();" class="link-primary">SELECCIONA RENDIMIENTOS A PAGAR!</a></span>
						&nbsp;<span id="btnRendimientosFID" ><a href="#" onclick="OpenDialogoRendimientosFID();" class="link-primary">SELECCIONAR EL MOVIMIENTO BANCARIO!</a></span>
						&nbsp;<span id="btnDevolucionLAUDO" ><a href="#" onclick="OpenDialogDevolucionesLAUDOS();" class="link-primary">SELECCIONAR DOCUMENTO PARA DEVOLUCION DE LAUDO O LIQUIDACION!</a></span>  													
					</div>
				</div>
				<div class="row" id="beneficiarioViat">
					
					<div class="col-3">
						<label class="form-label">Beneficiario:</label>
						<div class="input-group">
							<input type="text" size="30" id="cIdRFC_RelacionGasto" name="cIdRFC_RelacionGasto" readonly   class="form-control form-control-sm" onchange="cat_CuentaBeneficiario();" />
							<input type="button" id="btnBenef" onclick="cat_beneficiario();" value="..." class="btn btn-secondary"/>
						</div>
					</div>
					<div class="col-4">
						<span id="btnComision" ><a href="#" onclick="OpenDialogComisiones();  return false;" >SELECCIONAR COMISION DE VIATICOS</a></span>
					</div>
					<div class="col-3">
						<label id="lblNombreComision" class="form-label datosComision">Nombre de la Comisión:</label>
						<input type="text" id="nombreComision" name="nombreComision" class="form-control form-control-sm datosComision" value="" maxlength="30" size="30"/>
					</div>
				</div>
				<div class="row">
					<div class="col-8">
						<div class="col-12" id="idMeta">
							<label for="meta" class="form-label"> METAS relacionadas con la Unidad Ejecutora </label>	
							<select id="meta" name="meta" class="form-select form-select-sm" style="width: 90%">
								<option value="Z:">A</option>
							</select>
						</div>
					</div>
					<div class="col-4">
						<span id="CheckSI"> <input type="checkbox" id="selEvento" value = "0"  disabled="disabled"  onclick="habilita()" class="form-check-input"> Es Saldo Incial</span>
					</div>
				</div>
				<div class="row">
					<div class="col-12" id="concepto">
						<label for="cConcepto" class="form-label">Concepto:</label>
						<textarea class="form-control form-control-sm" rows=3 id="cConcepto" name="cConcepto"></textarea>
					</div>
				</div>
				
				&nbsp;
			<div class="col-12">	
				<fieldset id="registroDevolucion" class="px-3">
					<legend> Documento para devoluci&oacute;n </legend>
					<table id="dt_Devolucion" class="table table-striped">
						<thead>
						<tr>
							<th>Folio</th>
							<th>Concepto</th>
							<th>Remanente</th>
							<th>Beneficiario</th>
						</tr>
						</thead>	
						<tbody></tbody>					
					</table>
				</fieldset>
				
				<fieldset id="registroBonificacion" class="px-3">
					<legend> Documento de Bonificacion </legend>
					<table id="dt_Bonificacion" class="table table-striped">
						<thead>
						<tr>
							<th>Folio</th>
							<th>Concepto</th>
							<th>Importe</th>						
						</tr>
						</thead>	
						<tbody></tbody>					
					</table>
				</fieldset>
				
				<fieldset id="registroReintegro" class="px-3">
					<legend> Documento de Reintegro </legend>
					<table id="dt_Reintegro" class="table table-striped">
						<thead>
						<tr>
							<th>Folio</th>
							<th>Concepto</th>
							<th>Importe</th>						
						</tr>
						</thead>	
						<tbody></tbody>					
					</table>
				</fieldset>
				
				<fieldset id="registroComision" class="px-3">
					<legend> Comisión de Viaticos </legend>
					<table id="dt_Comision" class="table table-striped">
						<thead>
						<tr>
							<th>Folio</th>
							<th>Concepto de la Agenda</th>
							<th>Fecha Inicio</th>
							<th>Fecha Fin</th>						
						</tr>
						</thead>	
						<tbody></tbody>					
					</table>
				</fieldset>
				
				<fieldset id="registroRendimientoFID" class="px-3">
					<legend> Documento de Movimiento Bancario  </legend>
					<table id="dt_RendimientoFID" class="table table-striped">
						<thead>
						<tr>
							<th>Folio</th>
							<th>Concepto</th>
							<th>Importe</th>						
						</tr>
						</thead>	
						<tbody></tbody>					
					</table>
				</fieldset>
				
				<fieldset id="registroDevolucionL" class="px-3">
					<legend> Documento para devoluci&oacute;n </legend>
					<table id="dt_DevolucionL" class="table table-striped">
						<thead>
						<tr>
							<th>Folio</th>
							<th>Concepto</th>
							<th>Remanente</th>
							<th>Beneficiario</th>
						</tr>
						</thead>	
						<tbody></tbody>					
					</table>
				</fieldset>
			</div>
			<div>	
				<fieldset id="capturaDetalle" class="form-group border px-3"> <br/>				
					<legend> Captura del Detalle  </legend>					
					<hr class="mt-3">
					
					<div class="row" id="trCTABAN">
						<div class="col-12 col-md-6 mb-3">							
							<label for="CTABAN_SNP" class="form-label">Cuenta Bancaria:</label>
							<div class="input-group">
								<input type="text" id="CTABAN_SNP" name="CTABAN_SNP" class="form-control AyudaSyC form-control-sm" style="width: 18em !important;flex: none;" readonly/>
							</div>
						</div>							
					</div>
					
					<div class="row" id="trCTABANFFM">
						<div class="col-12 col-md-6 mb-3">							
							<label for="CTABAN_FFM" class="form-label">Cuenta Bancaria FFM:</label>
							<div class="input-group">
								<input type="text" id="CTABAN_FFM" name="CTABAN_FFM" class="form-control AyudaSyC form-control-sm" style="width: 18em !important;flex: none;" readonly/>
							</div>
						</div>							
					</div>
					
					<div class="row" id="trhPartida">
						<div class="col-12 col-md-6 mb-3">							
							<label for="hPartida" class="form-label">Partida:</label>
							<div class="input-group">
								<input type="text" id="hPartida" name="hPartida" class="form-control AyudaSyC form-control-sm" style="width: 10em !important;flex: none;" readonly/>
							</div>
						</div>
					</div>
					
					<div class="row" id="trALM">
						<div class="col-12 col-md-6 mb-3">							
							<label for="ALM" class="form-label">Almacen:</label>
							<div class="input-group">
								<input type="text" id="ALM" name="ALM" class="form-control AyudaSyC form-control-sm" style="width: 10em !important;flex: none;" readonly/>
							</div>
						</div>
						
						<div class="col-12 col-md-6 mb-3">							
							<label for="altaAlm" class="form-label">Número de almacen:</label>
							<div class="input-group">
								<input type="text" id="altaAlm" name="altaAlm" class="form-control form-control-sm" style="width: 10em !important;flex: none;" readonly/>
							</div>
						</div>
					</div>	
					
					<div class="row" id="trhFFM">
						<div class="col-12 col-md-6 mb-3">							
							<label for="hFFM" class="form-label">FFM:</label>
							<div class="input-group">
								<input type="text" id="hFFM" name="hFFM" class="form-control form-control-sm" style="width: 18em !important;flex: none;" readonly/>
								<input type="button" id="btnFFM" onclick="cat_FFM()" value="..." class="btn btn-secondary"/>
							</div>
						</div>
					</div>
					
					<div class="row" id="trhbuscabeneficiario">
						<div class="col-4 col-md-4 mb-3">							
							<label for="hbuscabeneficiario" class="form-label">Beneficiario:</label>
							<div class="input-group">
								<input type="text" id="hbuscabeneficiario" name="hbuscabeneficiario" class="form-control form-control-sm" onchange="cat_CuentaBeneficiario(); actualizaBenef();" style="width: 18em !important;flex: none;" readonly/>
								<input type="button" id="btnBeneficiario" onclick="cat_beneficiario()" value="..." class="btn btn-secondary"/>
							</div>
						</div>
					</div>
					
					<div class="row" id="trctabBene">
						<div class="col-4 col-md-4 mb-3">							
							<label for="ctabBeneficiario" class="form-label">Cuenta Bancaria del Beneficiario:</label>
							<select name="ctabBeneficiario" id="ctabBeneficiario" class="form-select form-select-sm" style="width: 90%">
					  		   <option value="NA">-N/A-</option>
					  	    </select>
						</div>
					</div>
					
					<div class="row">
						<div class="col-4 col-md-4 mb-3">							
							<label for="mMonto" class="form-label">Monto:</label>
							<div class="input-group">
								 <div class="input-group-prepend">
								    <span class="input-group-text">$</span>
								  </div>
								<input  type="text" id="mMonto" name="mMonto" class="form-control form-control-sm" placeholder="0.00" onfocus="quitaFmt(this.id)" onkeypress="valFmt(this,9)" onblur="moneyFrmt(this.id)"/>
								&nbsp;<input type="button" value="Agregar Detalle" id="agregar" class="btn btn-secondary"/>
							</div>								
						</div>
					</div>		
				</fieldset>			
						
				<br/>
				
				<fieldset id="Datos_Cheque" class="form-group border px-3">					
					<legend> Datos del BENEFICIARIO para el CHEQUE </legend>					
					<hr class="mt-3">
					
					<div class="row">
						<div class="col-12 d-flex justify-content-center">							
							<label for="Nombre_Cheque" class="form-label">Nombre:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
							<input type="text" id="Nombre_Cheque" name="Nombre_Cheque" value="" class="form-control form-control-sm" style="width: 20em;" maxlength="200"/>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 d-flex justify-content-center">							
							<label for="APaterno_Cheque" class="form-label">Apellido Paterno:</label>
							<input type="text" id="APaterno_Cheque" name="APaterno_Cheque" value="" class="form-control form-control-sm" style="width: 20em;" maxlength="200"/>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 d-flex justify-content-center">							
							<label for="AMaterno_Cheque" class="form-label">Apellido Materno:</label>
							<input type="text" id="AMaterno_Cheque" name="AMaterno_Cheque" value="" class="form-control form-control-sm" style="width: 20em;" maxlength="200"/>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 d-flex justify-content-center">
							<input type="button" value="Aceptar" id="Agrega_Datos_Cheque" class="btn btn-secondary"/>
							<input type="button" value="Editar" id="Edita_Datos_Cheque" class="btn btn-secondary"/>
						</div>
					</div>
										
				</fieldset>
				
			</div>
			
			<div id="OperacionesCaptura" >				
						<div class="row" id="tdAutorizar">
							<div class="col-12 d-flex justify-content-center">								
								<label for="Autorizar" class="form-label">Autorizar:</label>
								<label for="autorizaSi" class="form-check-label">Si: </label> 
								<input type="radio" name="grpAutorizar" id="grpAutorizar" class="form-check-input" checked value="Si" />&nbsp;&nbsp;
									
								<label for="autorizaNo" class="form-check-label">No: </label>&nbsp;&nbsp;
								<input type="radio" name="grpAutorizar" id="grpAutorizar" class="form-check-input" value="No" />&nbsp;&nbsp;
							</div>
						</div>
						<br/>
						<div class="row">
							<div class="col-12 d-flex justify-content-center">
								<input type="button" value="Imprimir Solicitud" id="guardar" class="btn btn-secondary"/>
								&nbsp;<input type="button" value="Aplicar Tramite" id="enviar" class="btn btn-primary"/>			
								&nbsp;<input type="button" value="Descartar Tramite" id="descartar" onClick="cmdBorrar()" class="btn btn-dark"/>	
														
							</div>
						</div>
					
			</div>	
			<div class="mt-2 col-12">
				<table id="dt_catalogo" class="table table-striped" align="center">
					<thead>
						<tr>		
							<th>DocRenglon</th>				
							<th>Subcuenta</th>
							<th>Monto</th>							
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
				<table width="820">
						<tr>
							<td align="right" nowrap="nowrap">
								<label style="font-style: italic;">
									Monto Total de la Solicitud :
								</label>
								<label id="mTotalLbl" style="font-weight: bolder;">
								</label>								
							</td>
						</tr>
				</table>
				<div id="divGrabaDetalle" style="width:100px; visibility: hidden" class="table-responsive text-nowrap">
					<table id="dt_catalogo_detaill"  style="width:100px" class="table table-striped" >
						<thead>
							<tr>							
								<th>A</th><!-- ndocRenglon  -->							
								<th>B</th><!-- cEvento  -->							
								<th>C</th><!-- Monto  -->
								<th>D</th><!-- MontoNegativo  -->
								<th>E</th><!-- ALM  -->
								<th>F</th><!-- CTAB  -->
								<th>G</th><!-- OBGT  -->
								<th>H</th><!-- RFC  -->	
								<th>I</th><!-- FFM  -->								
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>			
			</div>

			<div id="CausaRegreso" title="Motivo de rechazo">
				<fieldset>
					<textarea class="form-control" rows="5" cols="45" id="txtmotivoRechazo" ></textarea>							
				</fieldset>
			</div>
			<!-- Dialogo Firmantes -->
				<jsp:include page="../Generador/Firmantes.jsp"></jsp:include>
			<!-- Fin Dialogo Firmantes -->
		</form>
		
		<div id="dialog-Procesando" title="Procesando">
			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
			<img border="0" src="../imagenes/espera.gif" height="30">
			</div>
		</div>
		<div id="DialogGastosPorComprobar">
			<fieldset>
				<legend> Solicitudes por comprobar </legend>
				<label style="font-size: 11px; font-weight: bold;"> Seleccione una solicitud dando clic en ella y despues clic en aceptar</label>
				<div id="dtSolDiv">
					<table id="tblSolicitudes" class="table table-striped" cellspacing="0" cellpadding="2" align="center"> 
						<thead>
							<tr>
								<th> #Solicitud
								</th>
								<th> Concepto
								</th>
								<th> Remanente
								</th>
								<th> RFC
								</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</fieldset>
		</div>
		
		<div id="DialogBonificacion">
			<fieldset>
				<legend> Solicitudes de Ingresos de Bonificaciones</legend>
				<label style="font-size: 11px; font-weight: bold;"> Marque las solicitudes deseadas despues clic en Aceptar</label>
				<div id="dtSolDivR">
					<table id="tblSolicitudesB" class="table table-striped" cellspacing="0" cellpadding="2" align="center"> 
						<thead>
							<tr>
								<th>&nbsp;</th>
								<th> #Solicitud
								</th>
								<th> Concepto
								</th>
								<th> MontoSolicitud
								</th>
								<th> Remanente
								</th>
								<th> Importe
								</th>							
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</fieldset>
		</div>
		
		<div id="DialogReintegros">
			<fieldset>
				<legend>
					Solicitudes de Ingresos de Reintegros
				</legend>
				<label style="font-size: 11px; font-weight: bold;"> Seleccione una solicitud dando clic en ella y despues clic en aceptar</label>
				<div id="dtSolDivR">
					<table id="tblSolicitudesR" class="table table-striped" cellspacing="0" cellpadding="2" align="center"> 
						<thead>
							<tr>
								<th> #Solicitud
								</th>
								<th> Concepto
								</th>
								<th> Importe
								</th>							
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</fieldset>
		</div>
		
		<div id="DialogRendimientosFID">
			<fieldset>
				<legend>
					Solicitudes de Movimientos Bancarios
				</legend>
				<label style="font-size: 11px; font-weight: bold;"> Seleccione una solicitud dando clic en ella y despues clic en aceptar</label>
				<div id="dtSolDivR">
					<table id="tblSolicitudesMB" class="table table-striped" cellspacing="0" cellpadding="2" align="center"> 
						<thead>
							<tr>
								<th> #Solicitud
								</th>
								<th> Concepto
								</th>
								<th> Importe
								</th>							
							</tr>
						</thead>
					</table>
				</div>
			</fieldset>
		</div>
		
		<div id="DialogGastosPorComprobarL">
			<fieldset>
				<legend> Solicitudes por comprobar </legend>
				<label style="font-size: 11px; font-weight: bold;"> Seleccione una solicitud dando clic en ella y despues clic en aceptar</label>
				<div id="dtSolDiv">
					<table id="tblSolicitudesL" class="table table-striped" cellspacing="0" cellpadding="2" align="center"> 
						<thead>
							<tr>
								<th> #Solicitud
								</th>
								<th> Concepto
								</th>
								<th> Remanente
								</th>
								<th> RFC
								</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</fieldset>
		</div>
		
		<div id="DialogSaldosAntiguedad">
			<fieldset>
				<legend> Saldos Iniciales Deudores o Acreedores </legend>
				<label style="font-size: 11px; font-weight: bold;"> Ingrese el monto a comprobar o pagar</label>
				<div id="dtSalIni">
					<table id="tblSaldoA" class="table table-striped" cellspacing="0" cellpadding="2" align="center"> 
						<thead>
							<tr>
								<th> Folio								
								</th>
								<th> Cuenta Contable
								</th>
								<th> Subcuenta
								</th>			
								<th> CC
								</th>				
								<th> Fecha
								</th>
								<th> Saldo Inicial
								</th>
								<th> Pendiente
								</th>
								<th> Importe
								</th>
							</tr>
						</thead>
					</table>
				</div>
				<div>
					<table id="totalSA" align="center">
						<tr>
							<td>Total a Registrar:</td>
							<td>
								<input type="text" size="19" name="totalSaldoAnt" id="totalSaldoAnt" style="text-align:right;" value = "0.00" readonly />
							</td>
							<td> 
								<div id="eventoE" style="visibility: hidden"> Debes de Seleccionar la Cuenta Deudora como Acreedora </div> 
							</td>
						</tr>
					</table>
				</div>
			</fieldset>
			
			<input type="hidden" name="cEventoTipo2" id="cEventoTipo2" value="0" />
			<input type="hidden" name="cSaldoTipo2" id="cSaldoTipo2" value="0" />
			<input type="hidden" name="cBtn1" id="cBtn1" value="0" />
			<input type="hidden" name="cRg1" id="cRg1" value="0" />
			<input type="hidden" name="cBtn2" id="cBtn2" value="0" />
			<input type="hidden" name="cRg2" id="cRg2" value="0" />
			<input type="hidden" name="cIdSa" id="cIdSa" value="0" />
			<input type="hidden" name="cMontoSa" id="cSaldoTipo2" value="0" />
			<input type="hidden" name="cCC2" id="cCC2" value="0" />	
			<input type="hidden" name="esSaldoInicial" id="esSaldoInicial" value="0" />		
		</div>
		
		<form id="liberardocumento" name="liberardocumento"
				action="../gstnmngr/gestion?cmd=1" target="content-iframe"
				method="post">
		</form>
	</body>
</html>
