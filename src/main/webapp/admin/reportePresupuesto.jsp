<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!
	private Logger log = LoggerFactory.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;
	
	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}
	
	public String getValor(String data){
		return (data == null? "": data);
	}
 %>
<%
	String rpt_body="";
	String Cuenta=  getValor(request.getParameter("Cuenta"));
	String ep	=  getValor(request.getParameter("ep"));
	String EjercicioFiscal=  getValor(request.getParameter("EjercicioFiscal")); 
	//String login=  getValor(request.getParameter("login")); 
	String UnidadEjecutora=  getValor(request.getParameter("UnidadEjecutora")); 
	String RamoEP=  getValor(request.getParameter("RamoEP")); 
	String UnidadResponsableEP=  getValor(request.getParameter("UnidadResponsableEP")); 
	String GrupoFuncional=  getValor(request.getParameter("GrupoFuncional")); 
	String Funcion=  getValor(request.getParameter("Funcion")); 
	String SubFuncion=  getValor(request.getParameter("SubFuncion")); 
	String ProgramaGeneral=  getValor(request.getParameter("ProgramaGeneral")); 
	String ProgramaPresupuestario=  getValor(request.getParameter("ProgramaPresupuestario")); 
	String ActividadInstitucional=  getValor(request.getParameter("ActividadInstitucional")); 
	String Partida=  getValor(request.getParameter("Partida")); 
	String TipoGasto=  getValor(request.getParameter("TipoGasto")); 
	String FuenteFinanciamiento=  getValor(request.getParameter("FuenteFinanciamiento")); 
	String EntidadFederativa=  getValor(request.getParameter("EntidadFederativa")); 
	String Cartera=  getValor(request.getParameter("Cartera")); 
	String UnidadNormativa=  getValor(request.getParameter("UnidadNorativa")); 
	String ClaveCNA=  getValor(request.getParameter("ClaveCNA")); 
	String cOrddeBy = getValor(request.getParameter("cOrddeBy"));
	
	String paramPDF = "&ejercicio_fiscal="+(EjercicioFiscal		!=""&&EjercicioFiscal		!=null?EjercicioFiscal:"");
	paramPDF+="&condiciones=";
	paramPDF+=(EjercicioFiscal		!=""&&EjercicioFiscal		!=null?" s.aEjercicioFiscal = '" + EjercicioFiscal +"'":"");
	paramPDF+=(Cuenta				!=""&&Cuenta				!=null?" AND s.nCuentaP = '"+Cuenta+"'":"");
	paramPDF+=(ep					!=""&&ep					!=null?" AND s.ClaveSIAFF = '"+ep+"'":"");
	paramPDF+=(RamoEP				!=""&&RamoEP				!=null?" AND s.cRamoEP='"+RamoEP+"'":"");
	paramPDF+=(GrupoFuncional		!=""&&GrupoFuncional		!=null?" AND s.cGrupoFuncional='"+GrupoFuncional+"'":"");
	paramPDF+=(Funcion				!=""&&Funcion				!=null?" AND s.cFuncion='"+Funcion+"'":"");
	paramPDF+=(SubFuncion			!=""&&SubFuncion			!=null?" AND s.cSubFuncion='"+SubFuncion+"'":"");
	paramPDF+=(ProgramaGeneral		!=""&&ProgramaGeneral		!=null?" AND s.cProgramaGeneral='"+ProgramaGeneral+"'":"");
	paramPDF+=(ProgramaPresupuestario!=""&&ProgramaPresupuestario!=null?" AND s.cProgramaPresupuestario='"+ProgramaPresupuestario+"'":"");
	paramPDF+=(ActividadInstitucional!=""&&ActividadInstitucional!=null?" AND s.cActividadInstitucional='"+ActividadInstitucional+"'":"");
	paramPDF+=(Partida				!=""&&Partida				!=null?" AND s.cPartida='"+Partida+"'":"");
	paramPDF+=(TipoGasto			!=""&&TipoGasto				!=null?" AND s.cTipoGasto='"+TipoGasto+"'":"");
	paramPDF+=(FuenteFinanciamiento	!=""&&FuenteFinanciamiento	!=null?" AND s.cFuenteFinanciamiento='"+FuenteFinanciamiento+"'":"");
	paramPDF+=(EntidadFederativa	!=""&&EntidadFederativa		!=null?" AND s.cEntidadFederativa='"+EntidadFederativa+"'":"");
	paramPDF+=(Cartera				!=""&&Cartera				!=null?" AND s.cCartera='"+Cartera+"'":"");
	paramPDF+=(UnidadNormativa		!=""&&UnidadNormativa		!=null?" AND s.cUnidadNorativa='"+UnidadNormativa+"'":"");
	paramPDF+=(UnidadEjecutora		!=""&&UnidadEjecutora		!=null?" AND s.cUnidadEjecutora='"+UnidadEjecutora+"'":"");
	paramPDF+=(ClaveCNA				!=""&&ClaveCNA				!=null?" AND s.nClaveCNA='"+ClaveCNA+"'":"");
	paramPDF+=(cOrddeBy 			!=""&&cOrddeBy				!=null?" Order by " + cOrddeBy +"" :"");

	String descFiltros ="&descFiltros=PRESUPUESTO: ";
	String descCuenta="";
	descCuenta=(Cuenta.equals("81101")?"ORIGINAL":descCuenta);
	descCuenta=(Cuenta.equals("81102")?"MODIFICADO":descCuenta);
	descCuenta=(Cuenta.equals("81103")?"AMPLIACION AUTORIZADA":descCuenta);
	descCuenta=(Cuenta.equals("81104")?"REDUCCION AUTORIZADA":descCuenta);
	descCuenta=(Cuenta.equals("81200")?"COMODIN ACREEDOR":descCuenta);
	descCuenta=(Cuenta.equals("82101")?"APARTADO":descCuenta);
	descCuenta=(Cuenta.equals("82102")?"PRECOMPROMETIDO":descCuenta);
	descCuenta=(Cuenta.equals("82103")?"COMPROMETIDO":descCuenta);
	descCuenta=(Cuenta.equals("82104")?"DEVENGADO":descCuenta);
	descCuenta=(Cuenta.equals("82105")?"EJERCIDO":descCuenta);
	descCuenta=(Cuenta.equals("82106")?"DISPONIBLE NETO":descCuenta);
	descCuenta=(Cuenta.equals("82107")?"DISPONIBLE BRUTO":descCuenta);
	descCuenta=(Cuenta.equals("82200")?"COMODIN DEUDOR":descCuenta);
	descFiltros+=descCuenta+";;";//para salto de linea adentro de jasper se remplazara por \n
	descFiltros+=(EjercicioFiscal		!=""&&EjercicioFiscal		!=null?"EJERCICIO: " + EjercicioFiscal+";;" :"");
	descFiltros+=(ep					!=""&&ep					!=null?"EP: "+ep+";;":"");
	descFiltros+=(ClaveCNA				!=""&&ClaveCNA				!=null?"CLAVE CORTA: "+ClaveCNA+";;":"");			
	descFiltros+=(RamoEP				!=""&&RamoEP				!=null?"RAMO: "+RamoEP+";;":"");
	descFiltros+=(GrupoFuncional		!=""&&GrupoFuncional		!=null?"FINALIDAD: "+GrupoFuncional+";;":"");
	descFiltros+=(Funcion				!=""&&Funcion				!=null?"FUNCION: "+Funcion+";;":"");
	descFiltros+=(SubFuncion			!=""&&SubFuncion			!=null?"SUBFUNCION: "+SubFuncion+";;":"");
	descFiltros+=(ProgramaGeneral		!=""&&ProgramaGeneral		!=null?"REASIGNACION: "+ProgramaGeneral+";;":"");
	descFiltros+=(ActividadInstitucional!=""&&ActividadInstitucional!=null?"ACTIVIDAD INSTITUCIONAL: "+ActividadInstitucional+";;":"");
	descFiltros+=(ProgramaPresupuestario!=""&&ProgramaPresupuestario!=null?"PROGRAMA PRESUPUESTARIO: "+ProgramaPresupuestario+";;":"");
	descFiltros+=(ActividadInstitucional!=""&&ActividadInstitucional!=null?"ACTIVIDAD INSTITUCIONAL: "+ActividadInstitucional+";;":"");
	descFiltros+=(Partida				!=""&&Partida				!=null?"PARTIDA: "+Partida+";;":"");
	descFiltros+=(TipoGasto				!=""&&TipoGasto				!=null?"TIPO GASTO: "+TipoGasto+";;":"");
	descFiltros+=(FuenteFinanciamiento	!=""&&FuenteFinanciamiento	!=null?"FUENTE FINANCIAMIENTO: "+FuenteFinanciamiento+";;":"");
	descFiltros+=(EntidadFederativa		!=""&&EntidadFederativa		!=null?"ENTIDAD FEDERATIVA: "+EntidadFederativa+";;":"");
	descFiltros+=(Cartera				!=""&&Cartera				!=null?"CARTERA: "+Cartera+";;":"");
	descFiltros+=(UnidadNormativa		!=""&&UnidadNormativa		!=null?"UNIDAD NORMATIVA: "+UnidadNormativa+";;":"");
	descFiltros+=(UnidadEjecutora		!=""&&UnidadEjecutora		!=null?"UNIDAD EJECUTORA: "+UnidadEjecutora+";;":"");

	paramPDF+=descFiltros;

	String rpt_header=("<tr align='center'><th>Clave corta</th><th>Clave SIAFF</th><th>Clave Interna</th><th>Enero</th><th>Febrero</th><th>Marzo</th><th>Abril</th><th>Mayo</th><th>Junio</th><th>Julio</th><th>Agosto</th><th>Septiembre</th><th>Octubre</th><th>Noviembre</th><th>Diciembre</th><th>Anual</th></tr>");
	session.setAttribute(GestionInterface.ATT_EXP_HEADER,rpt_header);
	ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
	if (ep=="" || EjercicioFiscal=="" || Cuenta=="" ||  UnidadEjecutora=="" || RamoEP=="" || UnidadResponsableEP=="" || GrupoFuncional=="" || Funcion=="" || SubFuncion=="" || ProgramaGeneral=="" || ProgramaPresupuestario=="" || ActividadInstitucional=="" || Partida=="" || TipoGasto=="" || FuenteFinanciamiento=="" || EntidadFederativa=="" || Cartera=="" || UnidadNormativa=="" || ClaveCNA=="")
		rpt_body = rbl.ReportePresupuesto(ep, EjercicioFiscal, Cuenta, UnidadEjecutora, RamoEP, UnidadResponsableEP, GrupoFuncional, Funcion, SubFuncion, ProgramaGeneral, ProgramaPresupuestario, ActividadInstitucional, Partida, TipoGasto, FuenteFinanciamiento, EntidadFederativa, Cartera, UnidadNormativa, ClaveCNA, cOrddeBy);
	session.setAttribute(GestionInterface.ATT_EXP_BODY,rpt_body);
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

    
    <title>'reportePresupuesto.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  	<script language="javascript">
		function openPDF(){
			var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=reportePresupuesto.jasper<%=paramPDF%>";
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
		function openExcel(){
			var param = "&rptExcel="+ document.getElementById("hidEXCEL").value;
			/*
			Esteban Badillo. Fecha: 11/Sep/2009. Descripcion: Se agrega el paso del parametro "orden" para realizar la impresion
			condicional de columnas en la exportación del reporte a Excel.
			*/
			param += "&orden=" + <%="\"" + cOrddeBy + "\""%>;
			//alert(param);
			
			var url = "../reportes/reporte_export.jsp?id=9&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			//var url = "../admin/SeguridadCatalogos?xls=SI&catalogo=REPORTE&accion=run&rn=reportePresupuesto.jasper<%=paramPDF%>";
			//alert(url);
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
  </script>
  
  <body>
  	<input type="hidden" id="hidPDF" name="hidPDF" />
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right">
		<input type="button" id="cmdPdf"   name="cmdPdf"   value="PDF"       		onclick="javascript:openPDF();"   />
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel"        	onclick="javascript:openExcel();" />
	</div>
		<table width="100%" border="1">
			<%=rpt_header %>
			<%=rpt_body %>
		</table>
  </body>
</html>
