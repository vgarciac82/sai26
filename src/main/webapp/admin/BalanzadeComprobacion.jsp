<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="org.slf4j.LoggerFactory"%>


<%  
    String ID=request.getParameter("id2");
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();
	Integer UR_Int = new Integer(UR.substring(1));
	
	String Meses[] = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO",
			"JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE",
			"NOVIEMBRE", "DICIEMBRE", "MES_13"};
	String CentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor() ;
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	
	
%>
<%!private Logger log = LoggerFactory.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \""
						+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
					+ jniName + "\"");
		}
	}

	public String getValor(String data) {
		return (data == null ? "" : data);
	}%>
	

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <meta http-equiv="Content-Type"	content="text/html; charset=ISO-8859-1"> -->
<title>Balanza y Analitico</title>

	<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>	
	
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
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
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>       		

<script type="text/javascript">
var Meses2 = new Array(
		"Enero",
		"Febrero",
		"Marzo",
		"Abril",
		"Mayo",
		"Junio",
		"Julio",
		"Agosto",
		"Septiembre",
		"Octubre",
		"Noviembre",
		"Diciembre", 
		"MES_13"
	);
var popup=null;
function limpiarSesion()
	{
		parent.frames['resultado'].location.href = "blank.htm";
		window.location.href="BalanzadeComprobacion.jsp?id=<%=request.getParameter("id")%>";
		
	}
	
function InicializaVar(){
var principal = "";
var asunto = "";
var cCentroContable = "";
var hcCentroContable = "";
var fAuxIni = "";
var fAuxFin = "";
var fAuxIniCRI = "";
var fAuxFinCRI = "";
var mesIni = "";
var mesFin = "";
var mIniMayor= 0;
var mFinMayor= 0;
var aEjercicioFiscal = "";
var FiltroReporte = "";
var TipoReporte ="";
var buscaCuentaIni ="";
var hbuscaCuentaIni ="";
var buscaCuentaFin = "";
var buscaSubCuenta = "";
var TipoSubCuentaC = "";
var FiltroSubcuenta ="";
var DepuraLineas = "";
var DepuraColumnas =  "";
var qry = "";
var param = "";
var url = "";
var ventimp = "";

};

function fnPrintTituloReporte(TipoSalida) {
	InicializaVar();
	principal = parent.parent.document;
	asunto = document.datawork; 
	cCentroContable = (document.getElementById("cCentroContable").value == 'undefined' ? '' : document.getElementById("cCentroContable").value);
	hcCentroContable = (document.getElementById("hcCentroContable").value == ""|| document.getElementById("hcCentroContable").value == "00"? "00 CONSOLIDADO" :  document.getElementById("hcCentroContable").value );
    fAuxIni = document.getElementById("fAuxIni").value;
    fAuxFin = document.getElementById("fAuxFin").value;
 		fAuxIni = (fAuxIni == "" ? "01/01/" + (new Date()).getFullYear() :fAuxIni);
		fAuxFin = (fAuxFin == "" ? (new Date()).getDate()+ "/"+ (((new Date()).getMonth() < 9 ? "0" : "") + ((new Date()).getMonth() + 1))+ "/" + (new Date()).getFullYear() :fAuxFin);
    mesIni = document.getElementById("InfoRegMesIni").value;
    mesFin = document.getElementById("InfoRegMesFin").value;
	mIniMayor = parseInt(fAuxIni.substr(3,2),10);
    mFinMayor = parseInt(fAuxFin.substr(3,2),10);	
	aEjercicioFiscal = document.getElementById("aEjercicioFiscal").value;
    FiltroReporte = (document.getElementById("TipoReporte").value  == "Balanza" ? "Balanza de Comprobacion" :
    					(document.getElementById("TipoReporte").value  == "Analitico" ? "Analitico de Cuenta" :
    					(document.getElementById("TipoReporte").value  == "AnaliticoEC" ? "Analitico de Cuenta por Entidad Contable" :
    					(document.getElementById("TipoReporte").value  == "Auxiliares" ? "Auxiliares" : "Detalle de Cuenta"))));
    FiltroReporte +=  	(document.getElementById("TipoReporte").value  == "Auxiliares" ?  (fAuxIni == "" ? "" : (fAuxFin == "" ? " del "	+ fAuxIni : " del "	+ fAuxIni+ " al "	+  fAuxFin )) :	" del mes "	+  Meses2[mesIni-1]+ " a "	+  Meses2[mesFin-1] + " del " + aEjercicioFiscal);
//	var TipoReporte = 	(document.getElementById("TipoReporte").value == "AnaliticoEC" ? "Analitico" : document.getElementById("TipoReporte").value) ;
	TipoReporte = 	 document.getElementById("TipoReporte").value ;
	buscaCuentaIni = 	(TipoReporte == "Balanza" ? "" : TipoReporte == "AuxiliarM"  ? document.getElementById("buscaCtaMayor").value : 
							 TipoReporte == "Auxiliares"  ? document.getElementById("buscaCuentaC").value :
							((TipoReporte == "Analitico"  || TipoReporte == "AnaliticoEC" ? (document.getElementById("buscaCuentaIni").value == "" ? document.getElementById("buscaCuentaC").value :
							document.getElementById("buscaCuentaIni").value) :
							(TipoReporte == "AnaliticoCC" ?  document.getElementById("buscaCuentaC").value :
							document.getElementById("buscaCuentaIni").value))));
	hbuscaCuentaIni = 	(TipoReporte == "Balanza" ? "" :
							(TipoReporte == "Auxiliares"  ? document.getElementById("hbuscaCuentaCAuxiliares").value :( TipoReporte == "Analitico"   || TipoReporte == "AnaliticoEC" ?
							document.getElementById("hbuscaCuentaIni").value :document.getElementById("hbuscaCuentaC").value )));
	buscaCuentaFin = (TipoReporte == "Balanza" ? "" : (TipoReporte == "Analitico"   || TipoReporte == "AnaliticoEC" || TipoReporte == "Auxiliares" ? document.getElementById("buscaCuentaFin").value : ""));
    buscaSubCuenta = document.getElementById("subCtaMayor").value;
    TipoSubCuentaC = document.getElementById("TipoSubCuentaC").value;
    FiltroSubcuenta = 	(TipoSubCuentaC == "RFC"  ? document.getElementById("buscaRFC" ).value :
    					(TipoSubCuentaC == "ALM"  ? document.getElementById("buscaALM" ).value : 
    					(TipoSubCuentaC == "CTAB" ? document.getElementById("buscaCTAB").value :
    					(TipoSubCuentaC == "EP"   ? document.getElementById("buscaEP"  ).value : ""))));
    DepuraLineas = document.getElementById("DepuraLineas").value;
    DepuraColumnas =  (mesIni == mesFin ? "S" :document.getElementById("DepuraColumnas").value);
    FiltroReporte +=    (TipoReporte == "Balanza" ? "" : " de la cuenta "+ (buscaCuentaFin == "" ? hbuscaCuentaIni : buscaCuentaIni + " a la " + buscaCuentaFin));
    FiltroReporte +=    (TipoReporte == "Auxiliares" && FiltroSubcuenta == "" ? " "+TipoSubCuentaC+": "+
    						(TipoSubCuentaC == "RFC" ? document.getElementById("hbuscaRFC").value :
    						(TipoSubCuentaC == "ALM" ? document.getElementById("hbuscaALM").value :
    						(TipoSubCuentaC == "CTAB" ? document.getElementById("hbuscaCTAB").value :
    						(TipoSubCuentaC == "EP" ? document.getElementById("hbuscaEP").value : "")))) : "");
    FiltroReporte =    FiltroReporte.replace(/-00000/g,"");
    qry = "EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '"+buscaCuentaIni+"', '"+buscaCuentaFin+"', '"+aEjercicioFiscal+"', '"+DepuraLineas+"', '"+DepuraColumnas+"', '"+cCentroContable+"', '"+TipoReporte+"', '"+mesIni+"', '"+mesFin+"', '"+FiltroSubcuenta+"'; ";
    //var qry = "EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT| '"+buscaCuentaIni+"'| '"+buscaCuentaFin+"'| '"+aEjercicioFiscal+"'| '"+DepuraLineas+"'| '"+DepuraColumnas+"'| '"+cCentroContable+"'| '"+TipoReporte+"'| '"+mesIni+"'| '"+mesFin+"'| '"+FiltroSubcuenta+"'; ";

	param = "";
	
	sbt = document.getElementById("subtotal").checked;
	subtotal = (sbt == false ?  "0" : "1");
	
	/****NUEVA VERSION DE LOS REPORTES EN EXCEL*/
	
	if (TipoReporte != "Balanza" && TipoReporte !=  "AnaliticoCC" && buscaCuentaIni == "" ){
		alert("Capturar algun dato");
		return false;
	} else {
		$("#GenerarReportePDF").attr("disabled", true);
		$("#GenerarReporteXLS").attr("disabled", true);
		$("#Limpiar").attr("disabled", true);
		$("#VistaPrevia").attr("disabled", true);
		
		
		}
		
	
	
	if( TipoSalida == "PDF" )
	{
	
	     if (TipoReporte == "Auxiliares") {
	
			param = "catalogo=REPORTE" //"catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=Auxiliares.jasper"
			+ "&hcCentroContable=" + hcCentroContable
			+ "&cCentroContable=" + cCentroContable
			+ "&buscaCuentaIni=" + buscaCuentaIni
			+ "&TipoReporte=" + TipoReporte
			+ "&FiltroReporte=" + FiltroReporte
			+ "&fAuxIni=" + fAuxIni
			+ "&fAuxFin=" + fAuxFin 
			+ "&swhere= AND mMovimiento != 0 "
			+ 	(fAuxIni == "" ? " AND  convert(date, fMovimiento, 103) BETWEEN convert(date, '"+ fAuxIni +"' , 103) AND convert(date,'"+ fAuxFin +"' , 103) " : 
				(fAuxFin == "" ? " AND  convert(date, fMovimiento, 103) = convert(date, '"+ fAuxIni +"' , 103) " : 
													" AND  convert(date, fMovimiento, 103) BETWEEN convert(date, '"+ fAuxIni +"' , 103) AND convert(date,'"+ fAuxFin +"' , 103) "))
			+ (TipoSubCuentaC == "" ? "" : 	(TipoSubCuentaC == "RFC"  ? " AND nSubCuenta like '" + document.getElementById("buscaRFC" ).value + "' ":
											(TipoSubCuentaC == "ALM"  ? " AND nSubCuenta like '" + document.getElementById("buscaALM" ).value + "' " + (TipoReporte == "Auxiliares"  ?(cCentroContable == "00"  ? "  AND cCentroContable IN (select b.cCentroContable from CAT_ALMACEN b  with (nolock) where b.nIdAlmacen='"+document.getElementById("buscaALM" ).value+"' AND CHARINDEX(b.cAlmacen,'"+FiltroSubcuenta+"')!=0 ) ": "") : ""):
											(TipoSubCuentaC == "CTAB" ? " AND nSubCuenta like '" + document.getElementById("buscaCTAB").value + "' ":
											(TipoSubCuentaC == "EP"   ? " AND nSubCuenta like '" + document.getElementById("buscaEP"  ).value + "' ":" AND nSubCuenta like '" +"%"+ "' ")))));
	    }//pdf auxi
	     else if (TipoReporte == "AuxiliarM"){
	     	param = "catalogo=REPORTE" //"catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=AuxiliarMayor2.jasper"
			+ "&nCuenta=" + buscaCuentaIni.replace("%","_") 
			+ "&nSubCuenta="+ buscaSubCuenta.replace("%","_")
			+ "&cCentroContable=" + cCentroContable
			+ "&mIni=" + mIniMayor.toString()
			+ "&mFin=" + mFinMayor.toString()
			+ "&subTot=" + subtotal
			
	    }
	     else {
	 		param = "catalogo=REPORTE"
	 		+ "&accion=run"
	 		+ "&cCentroContable=" 	+ cCentroContable
			+ "&aEjercicioFiscal=" 	+ aEjercicioFiscal
			+ "&buscaCuentaIni=" 	+ buscaCuentaIni
			+ "&buscaCuentaFin=" 	+ buscaCuentaFin
			+ "&DepuraLineas=" 		+ DepuraLineas
			+ "&DepuraColumnas=" 	+ DepuraColumnas
			+ "&TipoReporte=" 		+ TipoReporte
			+ "&mesIni=" 			+ mesIni
			+ "&mesFin=" 			+ mesFin
			+ "&FiltroSubcuenta=" 	+ FiltroSubcuenta
			+ "&TipoSubCuentaC=" 	+ TipoSubCuentaC
			+ "&hcCentroContable=" 	+ hcCentroContable
			+ "&FiltroReporte=" 	+ FiltroReporte
			+ "&qry=" 				+ qry
			+ "&rn=" 				+ (DepuraColumnas == "N" ? "BalanzaDeComprobacionConagua.jasper" : "BalanzaDeComprobacionCortaConagua.jasper");
	      }
	      
	      var url = "../admin/SeguridadCatalogos?"+param;
	  	  var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
	
	}
	
	else if( TipoSalida == "XLS" || TipoSalida == "PRV")
	{
	
	 	if (TipoReporte == "Auxiliares"){
	 	    document.getElementById("bTipoReporte").value= TipoReporte;
			document.getElementById("bFiltroReporte").value= FiltroReporte;
			document.getElementById("bfAuxIni").value= fAuxIni;
			document.getElementById("bfAuxFin").value= fAuxFin;
			document.getElementById("bswhere").value=" AND mMovimiento != 0 "
			+ 	(fAuxIni == "" ? " AND  convert(date, fMovimiento, 103) BETWEEN convert(date, '"+ fAuxIni +"' , 103) AND convert(date,'"+ fAuxFin +"' , 103) " : 
				(fAuxFin == "" ? " AND  convert(date, fMovimiento, 103) = convert(date, '"+ fAuxIni +"' , 103) " : 
													" AND  convert(date, fMovimiento, 103) BETWEEN convert(date, '"+ fAuxIni +"' , 103) AND convert(date,'"+ fAuxFin +"' , 103) "))
			+ (TipoSubCuentaC == "" ? "" : 	(TipoSubCuentaC == "RFC"  ? " AND nSubCuenta like '" + document.getElementById("buscaRFC" ).value + "%' ":
											(TipoSubCuentaC == "ALM"  ? " AND nSubCuenta like '" + document.getElementById("buscaALM" ).value + "%' " + (TipoReporte == "Auxiliares"  ?(cCentroContable == "00"  ? "  AND cCentroContable IN (select b.cCentroContable from CAT_ALMACEN b  with (nolock) where b.nIdAlmacen='"+document.getElementById("buscaALM" ).value+"' AND CHARINDEX(b.cAlmacen,'"+FiltroSubcuenta+"')!=0 ) ": "") : ""):
											(TipoSubCuentaC == "CTAB" ? " AND nSubCuenta like '" + document.getElementById("buscaCTAB").value + "%' ":
											(TipoSubCuentaC == "EP"   ? " AND nSubCuenta like '" + document.getElementById("hbuscaEP" ).value + "' ": " AND nSubCuenta like '" +"%"+ "' ")))));
	        document.getElementById("bbuscaCuentaIni").value=buscaCuentaIni;
	        document.getElementById("bcCentroContable").value=cCentroContable;
	        document.getElementById("bTipoReporte").value=TipoReporte;
	        document.getElementById("bFiltroSubcuenta").value=FiltroSubcuenta;
	        document.getElementById("bTipoSubCuentaC").value=TipoSubCuentaC;
	        document.getElementById("bhcCentroContable").value=hcCentroContable;
	        document.getElementById("bFiltroReporte").value=FiltroReporte;
	    	}
	 	else if (TipoReporte == "AuxiliarM"){
	 		document.getElementById("bhcCentroContable").value=hcCentroContable;
	 		document.getElementById("bTipoReporte").value= TipoReporte;
	 		document.getElementById("CuentaAuxMayor").value= buscaCuentaIni;
	 		document.getElementById("SubCuentaAuxMayor").value= buscaSubCuenta;
	 		document.getElementById("fAuxMayorIni").value = mIniMayor;
	 		document.getElementById("fAuxMayorFin").value = mFinMayor;
	 		document.getElementById("CentroContAux").value = cCentroContable;
	 		document.getElementById("cSubtotal").value = subtotal;
	 		document.getElementById("bFiltroReporte").value=FiltroReporte;
	 	}
	 	else{
	 	
	 	    document.getElementById("bDepuraColumnas").value=(mesIni == mesFin ? "S" :document.getElementById("DepuraColumnas").value);
			document.getElementById("bDepuraLineas").value=DepuraLineas;
			document.getElementById("bmesIni").value=mesIni;
			document.getElementById("bmesFin").value=mesFin;
			document.getElementById("bFiltroSubcuenta").value=FiltroSubcuenta;
			document.getElementById("bbuscaCuentaIni").value=buscaCuentaIni;
			document.getElementById("bbuscaCuentaFin").value=buscaCuentaFin;
			document.getElementById("baEjercicioFiscal").value=aEjercicioFiscal;
			document.getElementById("bTipoReporte").value=TipoReporte;
            document.getElementById("bcCentroContable").value=cCentroContable;
	 	    document.getElementById("bTipoSubCuentaC").value=TipoSubCuentaC;
	 	    document.getElementById("bhcCentroContable").value=hcCentroContable;
	 	    document.getElementById("bFiltroReporte").value=FiltroReporte;
	 	    }
	 	    
	 	    
	 	    if(TipoSalida=="PRV")
	         {	 	    
	 	     document.formulario.action="../reports/ReportetoHtml";
	 	     open_popup();
	 	     }
            else
            {
            document.formulario.action="../reports/ReportetoExcel";            
            document.formulario.submit();
            }
            
           document.getElementById("mensaje").style.filter = "alpha(opacity=0)";
           document.getElementById("mensaje").style.visibility='visible';
         
            muestraMensaje();
	 	    
	 	    
	 	    
             
	        
	
	}
	
	
	$("#GenerarReportePDF").attr("disabled", false);
	$("#GenerarReporteXLS").attr("disabled", false);
  	$("#Limpiar").attr("disabled", false);
  	$("#VistaPrevia").attr("disabled", false);
	   
		
		
		
	
}

function open_popup() {
    
    popup=window.open('', 'formpopup', 'width=1024,height=600,resizeable=1,scrollbars=1');
    document.formulario.target = 'formpopup';
    document.formulario.submit();
}






var timerM;
var timerO;
var count=0;
var times=95;

function muestraMensaje()
{
timerM=setInterval("mostrar()",25);
}
function ocultaMensaje()
{

  
  
timerO=setInterval("ocultar()",25);

}


function mostrar()
{
count+=1;

	if(count==times)
		{
		clearInterval(timerM);
		
		setTimeout("ocultaMensaje()",2000);
		
		}
	else
		{
		document.getElementById("mensaje").style.filter = "alpha(opacity="+count+")";
		}
}//mostrar

function ocultar()
{

         count-=1;

	    document.getElementById("mensaje").style.filter = "alpha(opacity="+count+")";
        
        
   if(count==0)
		{
		clearInterval(timerO);
		document.getElementById("mensaje").style.visibility='hidden';
		}
  
}







</script>
<script language="javascript" type="text/javascript">



$("#divAuxiliares").hide();

function controles(title)
{

$("#titulo0").hide();
$("#titulo1").hide();
$("#titulo2").hide();
$("#titulo3").hide();
$("#titulo4").hide();
$("#titulo5").hide();

$("#titulo"+title).show();

switch ($("#TipoReporte").val()){
        
		case "Balanza":
			$("#divAuxiliares").hide();
			$("#divAuxiliaresCRI").hide();
			$("#divButton").show();
			$("#divTodaslasCuentas").hide();
			$("#divRangoCuentas").hide();
			$("#divSubcuenta").hide();
			$("#divSubcuentaAuxiliares").hide();
			$("#divCtasMayor").hide();
			$("#divcCentroContable").hide();
			$("#divDepura").show();
			$("#trRangoCuentas").hide();			
			break;
		case "Analitico":
			$("#divAuxiliares").hide();
			$("#divAuxiliaresCRI").hide();
			$("#divButton").show();
			$("#divTodaslasCuentas").show();
			$("#divRangoCuentas").show();
			$("#divSubcuenta").hide();
			$("#divSubcuentaAuxiliares").hide();
			$("#divCtasMayor").hide();
			$("#divcCentroContable").hide();
			$("#divDepura").show();
			break;
		case "AnaliticoCC":  //Detalle de Cuenta
			$("#divAuxiliares").hide();
			$("#divAuxiliaresCRI").hide();
			$("#divButton").show();
			$("#divTodaslasCuentas").hide();divTodaslasCuentas
			$("#divRangoCuentas").hide();
			$("#divSubcuenta").show();
			$("#divSubcuentaAuxiliares").hide();
			$("#divCtasMayor").hide();
			$("#divcCentroContable").hide();
			$("#divDepura").show();
			$("#trRangoCuentas").hide();			
			break;
		case "AnaliticoEC":
			$("#divAuxiliares").hide();
			$("#divAuxiliaresCRI").hide();
			$("#divButton").show();
			$("#divTodaslasCuentas").hide();
			$("#divRangoCuentas").show();
			$("#divSubcuenta").hide();
			$("#divSubcuentaAuxiliares").hide();
			$("#divCtasMayor").hide();
			$("#divcCentroContable").hide();
			$("#divDepura").show();
			break;
		case "Auxiliares":
			$("#divAuxiliares").show();
			$("#divAuxiliaresCRI").hide();
			$("#divButton").show();
			$("#divTodaslasCuentas").hide();
			$("#divRangoCuentas").hide();
			$("#divSubcuenta").hide();
			$("#divSubcuentaAuxiliares").show();
			$("#divCtasMayor").hide();
			$("#divcCentroContable").hide();
			$("#divDepura").hide();
			$("#trRangoCuentas").hide();			
			fnMuestraFecha();
			break;
		case "AuxiliarM":
			$("#divAuxiliares").show();
			$("#divAuxiliaresCRI").hide();
			$("#divButton").show();
			$("#divTodaslasCuentas").hide();
			$("#divRangoCuentas").hide();
			$("#divSubcuenta").hide();
			$("#divSubcuentaAuxiliares").hide();
			$("#divCtasMayor").show();
			$("#divcCentroContable").hide();
			$("#divDepura").hide();
			$("#trRangoCuentas").hide();			
			fnMuestraFecha();
			break;
		case "AuxiliarCRI":
			$("#divAuxiliaresCRI").show();
			$("#divButton").hide();
			$("#divAuxiliares").hide();
			$("#divTodaslasCuentas").hide();
			$("#divRangoCuentas").hide();
			$("#divSubcuenta").hide();
			$("#divSubcuentaAuxiliares").hide();
			$("#divCtasMayor").hide();
			$("#divcCentroContable").hide();
			$("#divDepura").hide();
			$("#trRangoCuentas").hide();			
			fnMuestraFecha();
			break;
			}
		
}//controles



$(document).ready(
	function()
	{
		/*$("input.AyudaSyC").subIniciaDlg();
		$(this).ajaxForm({
			dataType: "json",
			success: formSubmited
		});*/
		
		$("#titulo1").hide();
        $("#titulo2").hide();
        $("#titulo3").hide();
        $("#titulo4").hide();
        $("#titulo5").hide();
		
		IDR='<%=ID%>';
		
		if(IDR!="null")
		{
		$("#TipoReporte").hide();
        $("#etiquetaTRep").hide();
		
		 if(IDR=="1")
        {
        $("#TipoReporte").val("Balanza");
        controles(IDR);
        }
         if(IDR=="2")
        {
        $("#TipoReporte").val("Analitico");
        controles(IDR);
        }
         if(IDR=="3")
        {
        $("#TipoReporte").val("Auxiliares");
        controles(IDR);
        }
         if(IDR=="4")
        {
        $("#TipoReporte").val("AnaliticoCC");
        controles(IDR);
        }
         if(IDR=="5")
        {
        $("#TipoReporte").val("AuxiliarM");
        controles(IDR);
        }
       	}//IDR
        
        $("#VistaPrevia").button();
      	$("#GenerarReportePDF").button();
      	$("#GenerarReporteXLS").button();
      	$("#Limpiar").button();

		$("#AlcanceCuentas")
		.change(function() {
		switch ($(this).val()){
			case "Todas":			
				$("#divRangoCuentas").hide();
				fnTodaslasCuentas();
				break;
			case "Rango":
				$("#divRangoCuentas").show();
				break;
			}
		});

		$("#hbuscaCuentaIni")
		.change(function() {
			CuentaFin();
		});

		$("#hbuscaCuentaC")
		.change(function() {
			tipoSubCuenta();
		});

		$("#hbuscaCuentaCAuxiliares")
		.change(function() {
			tipoSubCuenta();
		});
		
		getCentroContable();
		$("#cCentroContable").val( newCC);		
		querySelectPost("CatalogoCentroContReadBalanza", "hcCentroContable", {async: false });
		
		$("#GenerarReporte").attr("disabled", true);
		$("#Limpiar").attr("disabled", true);
		// Inicializaciones CRUD
		querySelectPost("catalogoEjercicioFiscalRead", "aEjercicioFiscal", {async: false });
		
		querySelectPost("MesContableAbierto2Read", "InfoRegMesIni", {async: false });
		querySelectPost("MesContableAbierto2Read", "InfoRegMesFin", {async: false });
		
		$("#GenerarReporte").attr("disabled", false);
		$("#Limpiar").attr("disabled", false);
	});

		$(function() {
			$( "#fAuxIni" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});
			inicio();
		});
		$(function() {
			$( "#fAuxFin" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});
			hoy();
		});
		$(function() {
			$( "#fAuxIniCRI" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});
			inicio();
		});
		$(function() {
			$( "#fAuxFinCRI" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});
			hoy();
		});

function formSubmited() {
alert("Beneficiario enviado!");
}
	function tipoSubCuenta(){
		switch ($("#TipoSubCuentaC").val()){
			case "RFC":
				$("#divALM").hide();
				$("#divCTAB").hide();
				$("#divRFC").show();
				$("#divEP").hide();
				$("#divOBGT").hide();
				break;
			case "CTAB":
				$("#divALM").hide();
				$("#divCTAB").show();
				$("#divRFC").hide();
				$("#divEP").hide();
				$("#divOBGT").hide();
				break;
			case "ALM":
				$("#divALM").show();
				$("#divCTAB").hide();
				$("#divRFC").hide();
				$("#divEP").hide();
				$("#divOBGT").hide();
				break;
			case "EP":
				$("#divALM").hide();
				$("#divCTAB").hide();
				$("#divRFC").hide();
				$("#divEP").show();
				$("#divOBGT").hide();
				break;
			case "OBGT":
				$("#divALM").hide();
				$("#divCTAB").hide();
				$("#divRFC").hide();
				$("#divEP").hide();
				$("#divOBGT").hide();
				break;
		}
	}

	function CuentaFin(){
			switch ($("#TipoReporte").val()){
			case "AnaliticoEC":
				$("#trRangoCuentas").hide();
				break;
			case "Analitico":
				$("#trRangoCuentas").show();
				break;
			case "Auxiliares":
				$("#trRangoCuentas").show();
				break;
			}
	}
	

	function MesFin() {
		var op_meses ='';
		for (i = 0; i < calendar.get(Calendar.MONTH) + 1; i++) {
			op_meses += '" <option value="' + (i + 1) + '"';
			if (calendar.get(Calendar.MONTH) == i) {
				op_meses += ' selected ';
			}
			op_meses += '>' + Meses2[i] + '</option>';
		}
	}
	
	function fnTodaslasCuentas(){
		$("#buscaCuentaIni").val( "10000");
		$("#buscaCuentaFin").val( "99999");
		$("#hbuscaCuentaIni").val( "");
		$("#hbuscaCuentaFin").val( "");
//			+ "&buscaCuentaIni=" 	+ buscaCuentaIni
//			+ "&buscaCuentaFin=" 	+ buscaCuentaFin
	}
	
	function fnMuestraFecha(){
		var mydate=new Date(); 
		var year=mydate.getYear(); 
		if (year < 1000) 
			year+=1900; 
		year=year.toString(); //.substring(4,2)
		var day=mydate.getDay(); 
		var month=mydate.getMonth()+1; 
		if (month<10) 
		month="0"+month; 
		var daym=mydate.getDate(); 
		if (daym<10) 
		daym="0"+daym; 
//		document.write("<small><font color='000000' face='Arial'><b>"+daym+"/"+month+"/"+year+"</b></font></small>")
		//$("#fAuxIni").val("01/01/"+year);
		//$("#fAuxFin").val(daym+"/"+month+"/"+year);
	}
	
	function hoy(){
		var dd = new Date().getDate();
		var mm = new Date().getMonth()+1;
		var aact = new Date().getFullYear();
		var aaaa = "<%=efa%>";
		var cad = "";
		if (dd<10)
			cad=cad+"0"+dd+"/";
		else
			cad=cad+dd+"/";
		if (mm<10)
			cad=cad+"0"+mm+"/";
		else
			cad=cad+mm+"/";
		if (aact != aaaa)
			cad = "01/01/" + aaaa;
		else 
			cad=cad+aaaa;
		$("#fAuxFin").val(cad);			
		$("#fAuxFinCRI").val(cad);
	}
	
	function inicio(){		
		var aact = new Date().getFullYear();
		var aaaa = "<%=efa%>";
		var cad = "01/01/";
					
		if (aact != aaaa)
			cad = cad + aaaa;
		else 
			cad = cad + aact;
		$("#fAuxIni").val(cad);				
		$("#fAuxIniCRI").val(cad);
	}
	
	function extraeAuxCRI(){								
		var conEP;
		conEP = document.getElementById("conEP").checked;
		conEP = (conEP == false ?  "0" : "1");
		$("#bfAuxIniCRI").val($("#fAuxIniCRI").val());
		$("#bfAuxFinCRI").val($("#fAuxFinCRI").val());
		$("#ctaMayorCRI").val($("#buscaCtaMayorCRI").val());
		$("#bsubCtaMayorCRI").val($("#subCtaMayorCRI").val());
		$("#bcCentroContable").val($("#cCentroContable").val());$
		$("#bconEP").val(conEP);		
		
		document.formCRI.submit();
	}

</script>
</head>
<br/>
<body style="width: 97%; ">
	<form style="height: 430px;" id="form1" name="form1" method="post" target="formulario" action="./BalanzadeComprobacion.jsp?select=u_login">		
		<input type="hidden" id="mesAbierto" name="mesAbierto" value="0" />
		<input type="hidden" id="nMes" name="nMes" />
		
		<div id="container" style="width: 80%" class="container" >
			<div class="card-header"> <h3> Auxiliar y Analítico </h3> </div>
			<hr class="mt-3"/>
						
			<input id="UnidadEjecutora" name="UnidadEjecutora" value="" type="hidden">
			<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" />
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="TipoReporte" class="form-label"> Tipo de Reporte: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select id="TipoReporte" name="TipoReporte" onChange="controles('0');" class="form-select form-select-sm">
						<option >--Seleccione--</option>							
						<%if ("00".equals(CentroContable)) {%> 
							<option value="AnaliticoEC">Anal&iacute;tico de Cuenta por Entidad Contable</option>
						<%	} %>
						<option value="AnaliticoCC">Detalle de Cuenta</option>							
						<option value="AuxiliarM">Auxiliar</option>
						<option value="AuxiliarCRI">Auxiliar CRI</option>
					</select>
				</div>
			</div>
							    	
			<div id="divDepura">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="DepuraLineas" class="form-label"> Cuentas en Ceros: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="DepuraLineas" name="DepuraLineas" class="form-select form-select-sm">
							<option value="N">Si</option>
							<option value="S" selected>No</option>
						</select>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="DepuraColumnas" class="form-label"> Tipo: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="DepuraColumnas" name="DepuraColumnas" class="form-select form-select-sm">
							<option value="N">Ampliada</option>
							<option value="S" selected>Corta</option>
						</select>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="InfoRegMesIni" class="form-label"> Del Mes: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select name="InfoRegMesIni" id="InfoRegMesIni" onchange="" value="" class="form-select form-select-sm">
							<%for (int i = 0; i <  1; i++) {%>
							<option value="<%=i + 1%>" 
								<%if (0 == i) {%> 
									selected <%}%>>
									<%=Meses[i]%>
							</option><%}%>
						</select> 
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
						<label for="InfoRegMesFin" class="form-label"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<select name="InfoRegMesFin" id="InfoRegMesFin" onchange="" value="" class="form-select form-select-sm">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected="selected">--</option>
						</select>
					</div>
				</div>			
			</div>
			
			<div id="divcCentroContable" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hcCentroContable" class="form-label"> Centro Contable: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="hcCentroContable" name="hcCentroContable" class="form-select form-select-sm">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select>
						<input id="cCentroContable" name="cCentroContable" value="" size="5" maxlength="5" class="" type="hidden">
						<%if (UR_Int >= 1 && UR_Int <= 14) {%>
							<input id="cUnidadResponsable" name="cUnidadResponsable" value="" size="5" maxlength="5" class="" type="hidden">
						<% 	} else { %>
							<input id="cUnidadResponsable" name="cUnidadResponsable" value="<%=UR%>" size="5" maxlength="5" class="" type="hidden">
						<%	} %>
					</div>
				</div>				
			</div>
			
			<div id="divTodaslasCuentas" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="AlcanceCuentas" class="form-label"> Alcance: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select id="AlcanceCuentas" name="AlcanceCuentas" class="form-select form-select-sm">
							<option value="Todas">Todas las Cuentas</option>
							<option value="Rango" selected>Rango de Cuentas</option>
						</select>
					</div>
				</div>
			</div>
			
			<div id="divRangoCuentas" style="display: none;">
				<h6> Rango de Cuentas </h6>
				<hr class="mt-3">
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaCuentaIni" class="form-label"> Cuenta Inicial: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<div class="input-group">
							<input id="hbuscaCuentaIni" name="hbuscaCuentaIni" value="" size="50" maxlength="50" class="form-control form-control-sm AyudaSyC autoCompletaSyC" type="text" onChange="CuentaFin()" onblur="CuentaFin()">
						</div>
						<input id="buscaCuentaIni" name="buscaCuentaIni" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="trRangoCuentas" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaCuentaFin" class="form-label"> Cuenta Final: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<div class="input-group">
							<input id="hbuscaCuentaFin" name="hbuscaCuentaFin" value="" size="50" maxlength="50" class="form-control form-control-sm AyudaSyC autoCompletaSyC" type="text">
						</div>
						<input id="buscaCuentaFin" name="buscaCuentaFin" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>				
			</div>
			
			<div id="divSubcuenta" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaCuentaC" class="form-label"> Cuenta: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<div class="input-group">
							<input type="text" id="hbuscaCuentaC" name="hbuscaCuentaC" value="" size="50" maxlength="50" class="AyudaSyC autoCompletaSyC form-control form-control-sm" onChange="tipoSubCuenta()" onblur="tipoSubCuenta()">
						</div>
						<input id="buscaCuentaC" name="buscaCuentaC" value="" size="5" maxlength="5" class="" type="hidden">
						<input id="TipoSubCuentaC" name="TipoSubCuentaC" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="divSubcuentaAuxiliares" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaCuentaCAuxiliares" class="form-label"> Cuenta: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="hbuscaCuentaCAuxiliares" name="hbuscaCuentaCAuxiliares" value="" size="50" maxlength="50" class="form-control form-contro-sm AyudaSyC autoCompletaSyC" type="text" onChange="tipoSubCuenta()" onblur="tipoSubCuenta()">
						<input id="buscaCuentaC" name="buscaCuentaC" value="" size="5" maxlength="5" class="" type="hidden">
						<input id="TipoSubCuentaC" name="TipoSubCuentaC" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>							
			</div>
			
			<div id="divRFC" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaRFC" class="form-label"> RFC: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="hbuscaRFC" name="hbuscaRFC" value="" size="50" maxlength="50" class="form-control form-control-sm AyudaSyC autoCompletaSyC" type="text">
						<input id="buscaRFC" name="buscaRFC" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="divCTAB" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaCTAB" class="form-label"> Cuenta Bancaria: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="hbuscaCTAB" name="hbuscaCTAB" value="" size="50" maxlength="50" class="form-control form-control-sm AyudaSyC autoCompletaSyC" type="text">
						<input id="buscaCTAB" name="buscaCTAB" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="divALM" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaALM" class="form-label"> Almacen: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="hbuscaALM" name="hbuscaALM" value="" size="50" maxlength="50" class="form-control form-control-sm AyudaSyC autoCompletaSyC" type="text">
						<input id="buscaALM" name="buscaALM" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="divEP" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaEP" class="form-label"> EP: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="hbuscaEP" name="hbuscaEP" value="" size="50" maxlength="63" class="form-control form-control-sm AyudaSyC autoCompletaSyC" type="text">
						<input id="buscaEP" name="buscaEP" value="" size="5" maxlength="63" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="divOBGT" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="hbuscaOBGT" class="form-label"> Objeto Gasto: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="hbuscaOBGT" name="hbuscaOBGT" value="" size="50" maxlength="50" class="" type="text" class="form-control form-control-sm">
						<input id="buscaOBGT" name="buscaOBGT" value="" size="5" maxlength="5" class="" type="hidden">
					</div>
				</div>
			</div>
			
			<div id="divAuxiliares" style="display: none;">
				<h6> Rango de Fechas </h6>
				<hr class="mt-3">
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="fAuxIni" class="form-label"> Inicio: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar2-week-fill"></i></span>
							<input id="fAuxIni" name="fAuxIni"  value="" readonly maxlength="10" size="10" class="form-control form-control-sm"  type="text" >
						</div>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="fAuxFin" class="form-label"> Término: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar2-week-fill"></i></span>
							<input id="fAuxFin" name="fAuxFin"  value="" readonly maxlength="10" size="10" class="form-control form-control-sm"  type="text" >
						</div>
					</div>
				</div>
			</div>	
			
			<div id="divCtasMayor" style="display: none;">
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="buscaCtaMayor" class="form-label"> Cuenta: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="buscaCtaMayor" name="buscaCtaMayor" value="" size="50" maxlength="50" class="form-control form-control-sm" type="text" onChange="" onblur="">
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="buscaCtaMayor" class="form-label"> Sub Cuenta: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="subCtaMayor" name="subCtaMayor" value="" size="50" maxlength="50" class="form-control form-control-sm" type="text" onChange="" onblur="">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="buscaCtaMayor" class="form-label"> Agregar Subtotales: </label>
						<input type="checkbox" name="subtotal" id="subtotal"  value="subtotal"/>
					</div>
				</div>				
			</div>
						
			<div id="divAuxiliaresCRI" style="display: none;">					
				<h6> Auxiliar Cuentas CRI </h6>
				<hr class="mt-3">
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="fAuxIniCRI" class="form-label"> Inicio: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar2-week-fill"></i></span>
							<input id="fAuxIniCRI" name="fAuxIniCRI"  value="" maxlength="10" size="10" class="form-control form-control-sm"  type="text" >
						</div>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="fAuxFinCRI" class="form-label"> Término: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar2-week-fill"></i></span>
							<input id="fAuxFinCRI" name="fAuxFinCRI"  value="" maxlength="10" size="10" class="form-control form-control-sm"  type="text" >
						</div>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="buscaCtaMayorCRI" class="form-label"> Cuenta: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="buscaCtaMayorCRI" name="buscaCtaMayorCRI" value="" size="50" maxlength="50" class="form-control form-control-sm" type="text" onChange="" onblur="">
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="subCtaMayorCRI" class="form-label"> Sub Cuenta: </label>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input id="subCtaMayorCRI" name="subCtaMayorCRI" value="" size="50" maxlength="50" class="form-control form-control-sm" type="text" onChange="" onblur="">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="conEP" class="form-label"> Agregar EP: </label>
					<input type="checkbox" name="conEP" id="conEP"  value="1"/>
				</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-file-excel-fill"></i></span>
							<input id="GenerarAuxiliarCRI"	name="GenerarAuxiliarCRI" value="Generar XLS" onclick="extraeAuxCRI();" type="button" class="btn-secondary btn-secondary-sm"/>
						</div>
					</div>
				</div>				
			</div>
						
			<br/>
			
			<div id="divButton" style="display: none;">			
				<div class="row d-flex">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-eye-fill"></i></span>
							<input id="VistaPrevia" name="VistaPrevia" value="Vista Previa" onclick="return fnPrintTituloReporte('PRV');" type="button" class="btn-secondary btn-secondary-sm"/>
						</div>
					</div>	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-file-earmark-pdf-fill"></i></span>
							<input id="GenerarReportePDF" name="GenerarReportePDF" value="Generar PDF" onclick="return fnPrintTituloReporte('PDF');" type="button" class="btn-secondary btn-secondary-sm"/>
						</div>
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-file-excel-fill"></i></span>
							<input id="GenerarReporteXLS"	name="GenerarReporteXLS" value="Generar XLS" onclick="return fnPrintTituloReporte('XLS');" type="button" class="btn-secondary btn-secondary-sm"/>
						</div>
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-trash3-fill"></i></span>
							<input id="Limpiar" name="Limpiar" value="Limpiar"	onClick="document.location.reload(true)" type="button" class="btn-secondary btn-secondary-sm"/>
						</div>
					</div>				
				</div>
			</div>

		</div>
	</form>
	
<form style="height: 430px;" id="formCRI" name="formCRI" action="../reportes/ReporteAuxiliarCRI" method="get">		
	<input type="hidden" id="bfAuxIniCRI" name="bfAuxIniCRI" />
	<input type="hidden" id="bfAuxFinCRI" name="bfAuxFinCRI" />
	<input type="hidden" id="ctaMayorCRI" name="ctaMayorCRI"/>
	<input type="hidden" id="bsubCtaMayorCRI" name="bsubCtaMayorCRI"/>
	<input type="hidden" id="bconEP" name="bconEP"/>
	<input type="hidden" id="bcCentroContable" name="bcCentroContable" />
</form>
	
<form action="../reports/ReportetoExcel" method="post" id="formulario" name="formulario"  >

<input type="hidden" id="bDepuraColumnas"name="bDepuraColumnas" />
<input type="hidden" id="bDepuraLineas"name="bDepuraLineas" />
<input type="hidden" id="bmesIni"name="bmesIni" />
<input type="hidden" id="bmesFin"name="bmesFin" />
<input type="hidden" id="bFiltroSubcuenta"name="bFiltroSubcuenta" />
<input type="hidden" id="bbuscaCuentaIni"name="bbuscaCuentaIni" />
<input type="hidden" id="bbuscaCuentaFin"name="bbuscaCuentaFin" />
<input type="hidden" id="baEjercicioFiscal"name="baEjercicioFiscal" />
<input type="hidden" id="bTipoReporte" name="bTipoReporte" />
<input type="hidden" id="bcCentroContable" name="bcCentroContable" />
<input type="hidden" id="bfAuxIni" name="bfAuxIni" />
<input type="hidden" id="bfAuxFin" name="bfAuxFin" />
<input type="hidden" id="bswhere" name="bswhere" />
<input type="hidden" id="bFiltroReporte" name="bFiltroReporte" />
<input type="hidden" id="bTipoSubCuentaC" name="bTipoSubCuentaC" />	
<input type="hidden" id="bhcCentroContable" name="bhcCentroContable" />	
<input type="hidden" id="fAuxMayorIni" name="fAuxMayorIni" />
<input type="hidden" id="fAuxMayorFin" name="fAuxMayorFin" />
<input type="hidden" id="CentroContAux" name="CentroContAux" />
<input type="hidden" id="CuentaAuxMayor" name="CuentaAuxMayor" />
<input type="hidden" id="SubCuentaAuxMayor" name="SubCuentaAuxMayor" />
<input type="hidden" id="cSubtotal" name="cSubtotal" />	
	</form>
	
<div id="mensaje" style="visibility: hidden">Favor de esperar, el reporte se esta generando... </div>
	
</body>
</html>
