<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.EstructuraProgramaticaBusinessLogic"%>
<%@page import="com.syc.contable.core.CatEPAnteP"%>
<%
	ArrayList arrmEstatusRechazo = new ArrayList();
	ArrayList arrmEPpos1 = new ArrayList();
	String cCapo1="";
	ArrayList arrmEPpos2 = new ArrayList();
	String cCapo2="";
	ArrayList arrmEPpos3 = new ArrayList();
	String cCapo3="";
	ArrayList arrmEPpos4 = new ArrayList();
	String cCapo4="";
	ArrayList arrmEPpos5 = new ArrayList();
	String cCapo5="";
	ArrayList arrmEPpos6 = new ArrayList();
	String cCapo6="";
	ArrayList arrmEPpos7 = new ArrayList();
	String cCapo7="";
	ArrayList arrmEPpos8 = new ArrayList();
	String cCapo8="";
	ArrayList arrmEPpos9 = new ArrayList();
	String cCapo9="";
	ArrayList arrmEPpos10 = new ArrayList();
	String cCapo10="";
	ArrayList arrmEPpos11 = new ArrayList();
	String cCapo11="";
	ArrayList arrmEPpos12 = new ArrayList();
	String cCapo12="";
	ArrayList arrmEPpos13 = new ArrayList();
	String cCapo13="";
	ArrayList arrmEPpos14 = new ArrayList();
	String cCapo14="";
	ArrayList arrmEPpos15 = new ArrayList();
	String cCapo15="";
	ArrayList arrmEPpos16 = new ArrayList();
	String cCapo16="";
	ArrayList arrmEPpos17 = new ArrayList();
	String cCapo17="";
	ArrayList arrmEPpos18 = new ArrayList();
	String cCapo18="";
	ArrayList arrmEPpos19 = new ArrayList();
	String cCapo19="";
	ArrayList arrmEPpos20 = new ArrayList();
	String cCapo20="";
	ArrayList arrmDropDaun1 = new ArrayList();
	ArrayList arrmDropDaun2 = new ArrayList();
	ArrayList arrmDropDaun3 = new ArrayList();
	ArrayList arrmDropDaun4 = new ArrayList();
	ArrayList arrmDropDaun5 = new ArrayList();
	ArrayList arrmDropDaun6 = new ArrayList();
	ArrayList arrmDropDaun7 = new ArrayList();
	ArrayList arrmDropDaun8 = new ArrayList();
	ArrayList arrmDropDaun9 = new ArrayList();
	ArrayList arrmDropDaun10 = new ArrayList();
	ArrayList arrmDropDaun11 = new ArrayList();
	ArrayList arrmDropDaun12 = new ArrayList();
	ArrayList arrmDropDaun13 = new ArrayList();
	ArrayList arrmDropDaun14 = new ArrayList();
	ArrayList arrmDropDaun15 = new ArrayList();
	ArrayList arrmDropDaun16 = new ArrayList();
	ArrayList arrmDropDaun17 = new ArrayList();
	ArrayList arrmDropDaun18 = new ArrayList();
	ArrayList arrmDropDaun19 = new ArrayList();
	ArrayList arrmDropDaun20 = new ArrayList();
	ArrayList arrmMSG = new ArrayList();
	Map<String, String> mapValues = new HashMap<String, String>();
	String cEP = "";
	String mensajeVAdec="";
	String cUnidadResponsable="";
	String cEjercicoFiscal="";
	boolean bErrorTecho=false;
	boolean cGrupoUSR=false;
	boolean bActivaInsert=false;
	boolean bValidarEP=false;
	int i=0;
	int iarrmTecho=0; 
	String cMoey="";
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); 
	String today = sdf.format(c1.getTime());
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	int j=0;
	String textError="";

	if (request.getParameter("msg") != null && !"".equals(request.getParameter("msg"))) {
		mensajeVAdec = request.getParameter("msg");
		mensajeVAdec = mensajeVAdec.replace("[", "");
		mensajeVAdec = mensajeVAdec.replace("]", "");
		mensajeVAdec = mensajeVAdec.replace(",", "<br>");
	}

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);

	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	AnteProyectoBusinessLogic anteProy = new AnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
	AdecuacionBusinessLogic   adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	EstructuraProgramaticaBusinessLogic estrProgaBinss = new EstructuraProgramaticaBusinessLogic(GestionInterface.ATT_CONEXION);

	ArrayList<CatEPAnteP> cateps = new ArrayList<CatEPAnteP>();
	try{
		cEjercicoFiscal=adecProy.obtenEjercicioFiscal();
		int nEjercisioFiscal = new Integer(cEjercicoFiscal).intValue();
		nEjercisioFiscal++;
		if (request.getParameter("buscar") != null && "si".equals(request.getParameter("buscar").toLowerCase())){
			String queryBuscar = "SELECT * FROM tCatalogoEPAnteProy ";
			queryBuscar += "WHERE 1=1 ";
			if (request.getParameter("cEP") != "" ){
				cEP=request.getParameter("cEP");
				//String cComponentesEP[] =cEP.split("\\.");
				//int nNumComponentes = cComponentesEP.length;
				//int nConfSubCuenta = estrProgaBinss.iNumeroConfSubcuenta(cEjercicoFiscal,"EPA");
				//if (nNumComponentes==nConfSubCuenta){
					queryBuscar += " AND EP like '%"+cEP+"%'";
					//mapValues.put("EP",cEP);
				//}else{
				//	mensajeVAdec="Error: no se pudo recuperar la ep";			
				//}
			}else{
				String ejercicioF = request.getParameter("cCapo1");
				if(ejercicioF!=null && !"".equals(ejercicioF)){
					queryBuscar += " AND aEjercicioFiscal = '"+ejercicioF+"'"; 
					mapValues.put("aEjercicioFiscal",ejercicioF);
				}
				String cRamo = request.getParameter("cCapo2");
				if(cRamo!=null && !"".equals(cRamo)){
					queryBuscar += " AND cRamo = '"+cRamo+"'";
					mapValues.put("cRamo",cRamo);
				}
				String cUnidadRespEP = request.getParameter("cCapo3");
				if(cUnidadRespEP!=null && !"".equals(cUnidadRespEP)){
					queryBuscar += " AND cUnidadResponsableEP = '"+cUnidadRespEP+"'";
					mapValues.put("cUnidadResponsable",cUnidadRespEP);
				}
				String cGrupoFuncional = request.getParameter("cCapo4");
				if(cGrupoFuncional!=null && !"".equals(cGrupoFuncional)){
					queryBuscar +=  " AND cGrupoFuncional = '"+cGrupoFuncional+"'";
					mapValues.put("cGrupoFuncional",cUnidadRespEP);
				}
				String cFuncion = request.getParameter("cCapo5");
				if(cFuncion!=null && !"".equals(cFuncion)){
					queryBuscar += " AND cFuncion = '"+cFuncion+"'";
					mapValues.put("cFuncion",cFuncion);
				}
				String cSubFuncion = request.getParameter("cCapo6");
				if(cSubFuncion!=null && !"".equals(cSubFuncion)){
					queryBuscar += " AND cSubFuncion = '"+cSubFuncion+"'";
					mapValues.put("cSubFuncion",cSubFuncion);
				}
				String cProgramaGeneral = request.getParameter("cCapo7");
				if(cProgramaGeneral!=null && !"".equals(cProgramaGeneral)){
					queryBuscar += " AND cProgramaGeneral = '"+cProgramaGeneral+"'";
					mapValues.put("cProgramaGeneral",cProgramaGeneral);
				}
				
				String cActividadInstitucional = request.getParameter("cCapo8");
				if(cActividadInstitucional!=null && !"".equals(cActividadInstitucional)){
					queryBuscar += " AND cActividadInstitucional = '"+cActividadInstitucional+"'";
					mapValues.put("cActividadInstitucional",cActividadInstitucional);
				}
				String cProgramaPresupuestario = request.getParameter("cCapo9");
				if(cProgramaPresupuestario!=null && !"".equals(cProgramaPresupuestario)){
					queryBuscar += " AND cProgramaPresupuestario = '"+cProgramaPresupuestario+"'";
					mapValues.put("cProgramaPresupuestario",cProgramaPresupuestario);
				}
				String cPartida = request.getParameter("cCapo10");
				if(cPartida!=null && !"".equals(cPartida)){
					queryBuscar += " AND cPartida = '"+cPartida+"'";
					mapValues.put("cPartida",cPartida);
				}
				String cTipoGasto = request.getParameter("cCapo11");
				if(cTipoGasto!=null && !"".equals(cTipoGasto)){
					queryBuscar += " AND cTipoGasto = '"+cTipoGasto+"'";
					mapValues.put("cTipoGasto",cTipoGasto);
				}
				String cFuenteFinanciamiento = request.getParameter("cCapo12");
				if(cFuenteFinanciamiento!=null && !"".equals(cFuenteFinanciamiento)){
					queryBuscar += " AND cFuenteFinanciamiento = '"+cFuenteFinanciamiento+"'";
					mapValues.put("cFuenteFinanciamiento",cFuenteFinanciamiento);
				}
				String cEntidadFederativa = request.getParameter("cCapo13");
				if(cEntidadFederativa!=null && !"".equals(cEntidadFederativa)){
					queryBuscar += " AND cEntidadFederativa = '"+cEntidadFederativa+"'";
					mapValues.put("cEntidadFederativa",cEntidadFederativa);
				}
				String tipoGasto = request.getParameter("cCapo14");
				if(tipoGasto!=null && !"".equals(tipoGasto)){
					queryBuscar += " AND cCartera = '"+tipoGasto+"'";
					mapValues.put("cEntidadFederativa",cEntidadFederativa);
				}
				String cUnidadEjecutora = request.getParameter("cCapo15");
				if(cUnidadEjecutora!=null && !"".equals(cUnidadEjecutora)){
					queryBuscar += " AND cUnidadEjecutora = '"+cUnidadEjecutora+"'";
					mapValues.put("cUnidadEjecutora",cUnidadEjecutora);
				}
				String cUnidadNorativa = request.getParameter("cCapo16");
				if(cUnidadNorativa!=null && !"".equals(cUnidadNorativa)){
					queryBuscar += " AND cUnidadNorativa = '"+cUnidadNorativa+"'";
					mapValues.put("cUnidadNorativa",cUnidadNorativa);
				}
			}
			session.setAttribute("objcQuery",queryBuscar);
			System.out.println(queryBuscar);
			cateps = anteProy.getCatEPAnteProy(queryBuscar);
		}
		if (request.getParameter("epError") != null){
			if (session.getAttribute("objcArrayEP") != null)
				arrmMSG=(ArrayList) session.getAttribute("objcArrayEP");
			if (request.getParameter("cErrorFile") != null ){
				mensajeVAdec=request.getParameter("cErrorFile").toString();
				bErrorTecho=true;
			}
		}
		if (session.getAttribute("objcArrayEP") != null ){
			session.removeAttribute("objcArrayEP");
			while (arrmMSG.size() > j ){
				arrmEPpos1 = (ArrayList) arrmMSG.get(j);
				int k=0;
				while (arrmEPpos1.size() > k ){
					textError = (String) arrmEPpos1.get(k); 
					if (!"".equals(textError)) {
						mensajeVAdec += " "+ textError + "\\r\\n";
						bErrorTecho=true;
					}else{
						bActivaInsert=true;
					}
					k++;
				}
				j++;
			}
		}
		
		if (request.getParameter("epError") != null){
			textError=request.getParameter("epError");
		}
		if (request.getParameter("cEP") != null){
			cEP = request.getParameter("cEP");
		}
		if ("Si".equals(request.getParameter("BuscaEP"))){
			if ("".equals(cEP)){
				mensajeVAdec="Error: no se pudo recuperar la ep";
			}
		}
		if ((!"".equals(cEP)) && (request.getParameter("buscar") == null )){
			System.out.println("EP:"+ cEP);
			try{
				if (!"".equals(cEP)){
					arrmMSG=estrProgaBinss.validaClaveEP(cEP,  1,"EPA");
					if (mensajeVAdec.length() > 0){
						bErrorTecho=true;
					}else{
						bValidarEP=true;
					}
					j=0;
					textError="";
					while (arrmMSG.size() > j ){
						arrmEPpos1 = (ArrayList) arrmMSG.get(j);
						textError = (String) arrmEPpos1.get(0); 
						if (!"".equals(textError)) {
							mensajeVAdec += " "+ textError + "\\r\\n";
							bErrorTecho=true;
						}else{
							bActivaInsert=true;
						}
							
						j++;
					}
					System.out.println("EP ERROR:"+ mensajeVAdec);
				}
			}catch (Exception exi){
					System.out.println("ERROR"+ exi);
				   mensajeVAdec="ERROR"+ exi;
			}
		}
		arrmEstatusRechazo=anteProy.getConfSubcuenta("EPA",nEjercisioFiscal, cEP, mapValues);
		if (arrmEstatusRechazo.size()-1  >= 0 && arrmEstatusRechazo != null){
			if (arrmEstatusRechazo.size() >= 0 ){
				arrmEPpos1 = (ArrayList) arrmEstatusRechazo.get(0);
				if (arrmEPpos1.size() > 0 && arrmEPpos1 != null){
					cCapo1=(String) arrmEPpos1.get(0);
					arrmDropDaun1 =  (ArrayList) arrmEPpos1.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 1 ){
				arrmEPpos2 = (ArrayList) arrmEstatusRechazo.get(1);
				if (arrmEPpos2.size() > 0 && arrmEPpos2 != null){
					cCapo2=(String) arrmEPpos2.get(0);
					arrmDropDaun2 =  (ArrayList) arrmEPpos2.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 2 ){
				arrmEPpos3 = (ArrayList) arrmEstatusRechazo.get(2);
				if (arrmEPpos3.size() > 0 && arrmEPpos3 != null){
					cCapo3=(String) arrmEPpos3.get(0);
					arrmDropDaun3 =  (ArrayList) arrmEPpos3.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 3 ){
				arrmEPpos4 = (ArrayList) arrmEstatusRechazo.get(3);
				if (arrmEPpos4.size() > 0 && arrmEPpos4 != null){
					cCapo4=(String) arrmEPpos4.get(0);
					arrmDropDaun4 =  (ArrayList) arrmEPpos4.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 4 ){
				arrmEPpos5 = (ArrayList) arrmEstatusRechazo.get(4);
				if (arrmEPpos5.size() > 0 && arrmEPpos5 != null){
					cCapo5=(String) arrmEPpos5.get(0);
					arrmDropDaun5 =  (ArrayList) arrmEPpos5.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 5 ){
				arrmEPpos6 = (ArrayList) arrmEstatusRechazo.get(5);
				if (arrmEPpos6.size() > 0 && arrmEPpos6 != null){
					cCapo6=(String) arrmEPpos6.get(0);
					arrmDropDaun6 =  (ArrayList) arrmEPpos6.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 6 ){
				arrmEPpos7 = (ArrayList) arrmEstatusRechazo.get(6);
				if (arrmEPpos7.size() > 0 && arrmEPpos7 != null){
					cCapo7=(String) arrmEPpos7.get(0);
					arrmDropDaun7 =  (ArrayList) arrmEPpos7.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 7 ){
				arrmEPpos8 = (ArrayList) arrmEstatusRechazo.get(7);
				if (arrmEPpos8.size() > 0 && arrmEPpos8 != null){
					cCapo8=(String) arrmEPpos8.get(0);
					arrmDropDaun8 =  (ArrayList) arrmEPpos8.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 8 ){
				arrmEPpos9 = (ArrayList) arrmEstatusRechazo.get(8);
				if (arrmEPpos9.size() > 0 && arrmEPpos9 != null){
					cCapo9=(String) arrmEPpos9.get(0);
					arrmDropDaun9 =  (ArrayList) arrmEPpos9.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 9 ){
				arrmEPpos10 = (ArrayList) arrmEstatusRechazo.get(9);
				if (arrmEPpos10.size() > 0 && arrmEPpos10 != null){
					cCapo10=(String) arrmEPpos10.get(0);
					arrmDropDaun10 =  (ArrayList) arrmEPpos10.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 10 ){
				arrmEPpos11 = (ArrayList) arrmEstatusRechazo.get(10);
				if (arrmEPpos11.size() > 0 && arrmEPpos11 != null){
					cCapo11=(String) arrmEPpos11.get(0);
					arrmDropDaun11 =  (ArrayList) arrmEPpos11.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 11 ){
				arrmEPpos12 = (ArrayList) arrmEstatusRechazo.get(11);
				if (arrmEPpos12.size() > 0 && arrmEPpos12 != null){
					cCapo12=(String) arrmEPpos12.get(0);
					arrmDropDaun12 =  (ArrayList) arrmEPpos12.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 12 ){
				arrmEPpos13 = (ArrayList) arrmEstatusRechazo.get(12);
				if (arrmEPpos13.size() > 0 && arrmEPpos1 != null){
					cCapo13=(String) arrmEPpos13.get(0);
					arrmDropDaun13 =  (ArrayList) arrmEPpos13.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 13 ){
				arrmEPpos14 = (ArrayList) arrmEstatusRechazo.get(13);
				if (arrmEPpos14.size() > 0 && arrmEPpos14 != null){
					cCapo14=(String) arrmEPpos14.get(0);
					arrmDropDaun14 =  (ArrayList) arrmEPpos14.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 14 ){
				arrmEPpos15 = (ArrayList) arrmEstatusRechazo.get(14);
				if (arrmEPpos15.size() > 0 && arrmEPpos15 != null){
					cCapo15=(String) arrmEPpos15.get(0);
					arrmDropDaun15 =  (ArrayList) arrmEPpos15.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 15 ){
				arrmEPpos16 = (ArrayList) arrmEstatusRechazo.get(15);
				if (arrmEPpos16.size() > 0 && arrmEPpos16 != null){
					cCapo16=(String) arrmEPpos16.get(0);
					arrmDropDaun16 =  (ArrayList) arrmEPpos16.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1 >= 16 ){
				arrmEPpos17 = (ArrayList) arrmEstatusRechazo.get(17);
				if (arrmEPpos17.size() > 0 && arrmEPpos17 != null){
					cCapo17=(String) arrmEPpos17.get(0);
					arrmDropDaun17 =  (ArrayList) arrmEPpos17.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 17 ){
				arrmEPpos18 = (ArrayList) arrmEstatusRechazo.get(18);
				if (arrmEPpos18.size() > 0 && arrmEPpos18 != null){
					cCapo18=(String) arrmEPpos18.get(0);
					arrmDropDaun18 =  (ArrayList) arrmEPpos18.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 18 ){
				arrmEPpos19 = (ArrayList) arrmEstatusRechazo.get(19);
				if (arrmEPpos19.size() > 0 && arrmEPpos19 != null){
					cCapo19=(String) arrmEPpos19.get(0);
					arrmDropDaun19 =  (ArrayList) arrmEPpos19.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 19 ){
				arrmEPpos20 = (ArrayList) arrmEstatusRechazo.get(20);
				if (arrmEPpos20.size() > 0 && arrmEPpos20 != null){
					cCapo20=(String) arrmEPpos20.get(0);
					arrmDropDaun20 =  (ArrayList) arrmEPpos20.get(1);
				}
			}
			if (arrmEstatusRechazo.size()-1  >= 20 ){
				System.out.println("ERROR: Solo s eProgramaron un maximo de 20 componentes reportar a desarrollo.");
				mensajeVAdec="ERROR: Solo s eProgramaron un maximo de 20 componentes reportar a desarrollo.";
			}
		}
	}catch (Exception exi){
		System.out.println("ERROR"+ exi);
		mensajeVAdec="ERROR"+ exi;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Catálogo de Estructuras Programáticas Ante Proyecto</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle"> 
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker-es.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" charset="utf-8">

		$(document).ready(
			function(){
				$('.currency').blur(function(){
					$('.currency').formatCurrency();
				});
				/* Init the table */
				oTableTechos = $('#grdProgPresupUE').dataTable( );

				$("input.AyudaSyC").subIniciaDlg();
			
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			
				$('#grdProgPresupUE').dataTable({
					"bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"sScrollXInner": "240%",
					"bScrollCollapse": true,	
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
					
				});
				var $dlgDetEPs;
				$(function(){
	    			/*$('#dlgDetEPs').dialog({
    			    	autoOpen: false,
			       		width: 1200,
			       		heigth: 900
	    			});*/
				})
	    		var $dlgError;
	    		$(function(){
	    			$('#dlgError').dialog({
    			    	autoOpen: <%if (bErrorTecho){%>true<%}else{%>false<%}%>,
			       		width: 1200,
			       		heigth: 900
	    			});
	    		});
				onLoadPlantilla();
			});

		function carga(){
	/*		if (< %=iPP%> > 0){
				< % for (i = 0; i < iPP; i++ ){ %>
					fnClickAddRowPP( "< %=cClavePP[i]%>", "< %=dClavePP[i]%>" , "< %=mClavePP[i]%>");
				< %}%> 
			}*/
			var justi=document.getElementById("mensajeError");
			justi.value="<%=mensajeVAdec%>";
		}

		function fnClickAddRowComprobatoria(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) {
			//$('#grdProgPresupUE').dataTable().fnAddData( [A,B,C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S ]);
			$('#grdProgPresupUE').dataTable().fnAddData( [A,B,C, D, E, F, G, H, I, J, K,L ]);
		}

		function fnAgregarRet() {
			var table = document.getElementById('grdProgPresupUE');
		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
		    try{
				for(var i=0; i<rowCount; i++) {
		        	var row = table.rows[i];
		        	var chkbox = '';
					try {
					  var chkbox = row.cells[0].childNodes[0];
					} catch(e) {
		        		null;
		        	}
	    		}
		    } catch(e) {
		       alert(e);
	    	}
			if (yaExiste == 1){
				alert("Ya se ingresó una retención con esa clave");
				return;
			}
			queryFormPost("tTechosPartidaCreate", {async: false });
			fnClickAddRowB($("#txtGridOGTOC").val(), $("#txtGridOGTOD").val(), $("#txtGridCTG").val(), $("#txtGridDTG").val(), $("#mTechoPartida").val() );
		}

		function onSubmit(){
		//alert("onSubmit");
			var p = window.parent;
			var valida_campos = true;
			try{
				guardaExp();
			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

 		function onPostSubmit(){//validaciones del boton enviar
		//alert("onPostSubmit");
	  		return true;
		}
  	
		function onLoadPlantilla(){
		//alert("onLoadPlantilla");
			carga();
			var nLongMsg="<%=mensajeVAdec.length()%>";

			if (nLongMsg == 0 && document.getElementById("cEP").value != "" && <%=bValidarEP%>){
				alert("Estructura Programática Valida. dar click al botón Guardar EP");
			}
			
			if(<%=cateps.size()>0%>){
				$.blockUI({message:"Procesando..."});
					<% for( j=0;j<cateps.size();j++){ 
					    	CatEPAnteP catep = cateps.get(j);
					%>
							//fnClickAddRowComprobatoria('< %=catep.getaEjercicioFiscal()%>','< %=catep.getEP()%>','< %=catep.getClaveSiaff()%>','< %=catep.getClaveInterna()%>','< %=catep.getcRamoEP()%>','< %=catep.getcUnidadResponsableEP()%>','< %=catep.getcGrupoFuncional()%>','< %=catep.getcFuncion()%>','< %=catep.getcSubFuncion()%>','< %=catep.getcProgramaGeneral()%>','< %=catep.getcActividadInstitucional()%>','< %=catep.getcProgramaPresupuestario()%>','< %=catep.getcPartida()%>','< %=catep.getcTipoGasto()%>','< %=catep.getcFuenteFinanciamiento()%>','< %=catep.getcEntidadFederativa()%>','< %=catep.getcCartera()%>','< %=catep.getcUnidadNormativa()%>','< %=catep.getcUnidadEjecutora()%>');
							//fnClickAddRowComprobatoria('< %=catep.getaEjercicioFiscal()%>,"b","c",< %=catep.getClaveInterna().trim()%>,"e","f","g","h","i","j","k","l","m","n","o","p","q","r","s");
							fnClickAddRowComprobatoria('<%=catep.getaEjercicioFiscal()%>','<%=catep.getEP()%>','<%=catep.getcGrupoFuncional()%>','<%=catep.getcFuncion()%>','<%=catep.getcSubFuncion()%>','<%=catep.getcProgramaGeneral()%>','<%=catep.getcActividadInstitucional()%>','<%=catep.getcProgramaPresupuestario()%>','<%=catep.getcPartida()%>','<%=catep.getcTipoGasto()%>','<%=catep.getcFuenteFinanciamiento()%>','<%=catep.getcEntidadFederativa()%>');
					<%
					    }
					%>
					$.unblockUI();
					$('#efcl').click();
			}
				
		}
		
		function onPostDisplay(){
		//alert("onPostDisplay");
		}
		function onActualizaEstatus(cTipoAutoriza){
	   		var mas_params="&"+cTipoAutoriza;
			document.formAutoriza.submit();
		}

	 	function get(name) {
			return document.getElementById(name).value;
		}

		function validaEP(){
			document.formAnteProy.submit();
		}
		
		function BuscaEP(){
			var msgAlert="";
	  		var vBuscarEP="buscar=si";	  		  	
	  		if($.trim($("#cCapo1").val())!="" || $.trim($("#cCapo2").val())!="" || $.trim($("#cCapo3").val())!="" || $.trim($("#cCapo4").val())!="" || $.trim($("#cCapo5").val())!="" || $.trim($("#cCapo6").val())!="" ||
	  				$.trim($("#cCapo7").val())!="" || $.trim($("#cCapo8").val())!="" || $.trim($("#cCapo9").val())!="" || $.trim($("#cCapo10").val())!="" || $.trim($("#cCapo11").val())!="" || $.trim($("#cCapo12").val())!="" ||
	  				$.trim($("#cCapo13").val())!="" || $.trim($("#cCapo14").val())!="" || $.trim($("#cCapo15").val())!="" || $.trim($("#cCapo16").val())!="" || $.trim($("#cCapo17").val())!="" || $.trim($("#cCapo18").val())!="" ||
	  				$.trim($("#cCapo19").val())!="" || $.trim($("#cCapo20").val())!="" || ($.trim($("#cEP").val()))!="" ) {
	  			$.blockUI({message: "Procesando espere ......"});
		  		document.forms.formAnteProy.action="catalogoEstructuraProgramatica.jsp?"+vBuscarEP;
		  		document.formAnteProy.submit();
	  		}else{
	  			alert("Seleccione al menos un campo para el filtro, este proceso puede tardar mucho tiempo");
	  		}
		}
		
	  	function fnImportarExcel(){
	  		var msgAlert="";
	  		var vSuperAdecua="cSuperReduccion=NO";
	  		if(	document.getElementById("importExcel").value == "" ){
	  			msgAlert+="El archivo Excel es requerido";
	  		}
	
	  		if(msgAlert!=""){
	  			alert(msgAlert);
	  			return false;
	  		}
	  		else{
	  			guardaExp();
	  			$.blockUI({message: "Procesando espere ......"});
	  			document.forms.upExcel.action="../caso/firmardoc?carpeta=2&"+vSuperAdecua;
	  			document.upExcel.submit();
	  			return true;
	  		}
	  	}
		 function fnSubeArchivo(tipo){	
			if(($("#archivoEUN").val()!="") || ($("#archivoEUE").val()!="") || ($("#archivoEPP").val()!="") || ($("#archivoEEF").val()!="") || ($("#archivoERET").val()!="") || ($("#archivoEPUE").val()!="") || ($("#archivoEProgPr").val()!="")){
				$.blockUI({message: "Procesando Alta espere ......"});
					document.subeArchivoUN.submit();
			}
				
			else
				alert("Archivo Requerido");
		}

		function armaEPa(cValue){
			var cEP = document.getElementById("cEP").value;
			document.getElementById("cEP").value=cValue.value;
		}
		function armaEPb(cValue, nComponente){
			var cEP = "";
			if(document.getElementById("cCapo1").selectedIndex!=0&&
				document.getElementById("cCapo2").selectedIndex!=0&&
				document.getElementById("cCapo3").selectedIndex!=0&&
				document.getElementById("cCapo4").selectedIndex!=0&&
				document.getElementById("cCapo5").selectedIndex!=0&&
				document.getElementById("cCapo6").selectedIndex!=0&&
				document.getElementById("cCapo7").selectedIndex!=0&&
				document.getElementById("cCapo8").selectedIndex!=0&&
				document.getElementById("cCapo9").selectedIndex!=0&&
				document.getElementById("cCapo10").selectedIndex!=0&&
				document.getElementById("cCapo11").selectedIndex!=0&&
				document.getElementById("cCapo12").selectedIndex!=0&&
				document.getElementById("cCapo13").selectedIndex!=0&&
				document.getElementById("cCapo14").selectedIndex!=0&&
				document.getElementById("cCapo15").selectedIndex!=0&&
				document.getElementById("cCapo16").selectedIndex!=0//&&
				//document.getElementById("cCapo17").selectedIndex!=0&&
				//document.getElementById("cCapo18").selectedIndex!=0&&
				//document.getElementById("cCapo19").selectedIndex!=0&&
				//document.getElementById("cCapo20").selectedIndex!=0
			){
				cEP=document.getElementById("cCapo1")[document.getElementById("cCapo1").selectedIndex].value+"."+
					document.getElementById("cCapo2")[document.getElementById("cCapo2").selectedIndex].value+"."+
					document.getElementById("cCapo3")[document.getElementById("cCapo3").selectedIndex].value+"."+
					document.getElementById("cCapo4")[document.getElementById("cCapo4").selectedIndex].value+"."+
					document.getElementById("cCapo5")[document.getElementById("cCapo5").selectedIndex].value+"."+
					document.getElementById("cCapo6")[document.getElementById("cCapo6").selectedIndex].value+"."+
					document.getElementById("cCapo7")[document.getElementById("cCapo7").selectedIndex].value+"."+
					document.getElementById("cCapo8")[document.getElementById("cCapo8").selectedIndex].value+"."+
					document.getElementById("cCapo9")[document.getElementById("cCapo9").selectedIndex].value+"."+
					document.getElementById("cCapo10")[document.getElementById("cCapo10").selectedIndex].value+"."+
					document.getElementById("cCapo11")[document.getElementById("cCapo11").selectedIndex].value+"."+
					document.getElementById("cCapo12")[document.getElementById("cCapo12").selectedIndex].value+"."+
					document.getElementById("cCapo13")[document.getElementById("cCapo13").selectedIndex].value+"."+
					document.getElementById("cCapo14")[document.getElementById("cCapo14").selectedIndex].value+"."+
					document.getElementById("cCapo15")[document.getElementById("cCapo15").selectedIndex].value+"."+
					document.getElementById("cCapo16")[document.getElementById("cCapo16").selectedIndex].value//+"."
					//document.getElementById("cCapo17")[document.getElementById("cCapo17").selectedIndex].value+"."
					//document.getElementById("cCapo18")[document.getElementById("cCapo18").selectedIndex].value+"."
					//document.getElementById("cCapo19")[document.getElementById("cCapo19").selectedIndex].value+"."
					//document.getElementById("cCapo20")[document.getElementById("cCapo20").selectedIndex].value
														
				document.getElementById("cEP").value = cEP;
			}
			
		}

		function ExportaEP(){
	  		var msgAlert="";
	  			$.blockUI({message: "Procesando espere ......"});
	  			//document.forms.formExpoEP.action="../caso/firmardoc?carpeta=2&"+vSuperAdecua;
	  			document.formExpoEP.submit();
	  			return true;
			
		}
		 
		</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>Catálogo de Estructuras Programáticas Ante Proyecto</h1>
			<form id="subeArchivoUN" name="subeArchivoUN" method="post" action="../gstnmngr/CatalogoEPExcel?Importa=SI" enctype="multipart/form-data">
				<input type="file" id="archivoEUN" name="archivoEUN" size="17" value=""/>
				<input type="hidden" id="pestana" name="pestana" value="5"/>
				<input type="hidden" id="accion" name="accion" value="CargaEXCEL"/>
				<input type="button" value="Subir Archivo" onclick="fnSubeArchivo('un');"/>
			</form>
			<form id="formValidaEP" name="formValidaEP" action="catalogoEstructuraProgramatica.jsp?BuscaEP=Si" ></form>
			<form id="formExpoEP" name="formExpoEP" action="../gstnmngr/CatalogoEPExcel?CreaXLS=SI" ></form>
			<form id="formAnteProy" name="formAnteProy" action="catalogoEstructuraProgramatica.jsp?BuscaEP=Si" method="post">
				<input type="hidden" value="" id="rowsAffected" name="rowsAffected">
				<input type="hidden" id="cRamo" name="cRamo" value="16">
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="">
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="" />
				<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="<%=cEjercicoFiscal%>"/>
				<input type="hidden" id="mensajeVAdec" name="mensajeVAdec" value="<%=mensajeVAdec%>" />
				<div class="dvGeneral">
					<table height="66" width="70%" border="0">
						<tr align="left">
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr align="left">
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr align="left">
							<td><%=cCapo1.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun1.size() >= 0) { %>
								<select id="cCapo1" name="cCapo1" style="width: 20em;" onChange="armaEPb(this,0)">
									<option value=""></option>
									<% if (arrmDropDaun1.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun1.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							<td><%=cCapo2.toString() %></td>
							<td>
							<% if (arrmDropDaun2.size() > 0) { %>
								<select id="cCapo2" name="cCapo2" style="width: 20em;" onChange="armaEPb(this, 1)">
									<option value=""></option>
									<% if (arrmDropDaun2.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun2.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo3.toString() %></td>
							<td valign="top" nowrap>
							<% if (arrmDropDaun3.size() > 0) { %>
								<select id="cCapo3" name="cCapo3" style="width: 20em;" onChange="armaEPb(this, 2)">
									<option value=""></option>
									<% if (arrmDropDaun3.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun3.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo4.toString() %></td>
							<td>
							<% if (arrmDropDaun4.size() > 0) { %>
								<select id="cCapo4" name="cCapo4" style="width: 20em;" onChange="armaEPb(this, 3)">
									<option value=""></option>
									<% if (arrmDropDaun4.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun4.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo5.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun5.size() > 0) { %>
								<select id="cCapo5" name="cCapo5" style="width: 20em;" onChange="armaEPb(this, 4)">
									<option value=""></option>
									<% if (arrmDropDaun5.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun5.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo6.toString() %></td>
							<td>
							<% if (arrmDropDaun6.size() > 0) { %>
								<select id="cCapo6" name="cCapo6" style="width: 20em;" onChange="armaEPb(this, 5)">
									<option value=""></option>
									<% if (arrmDropDaun6.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun6.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo7.toString() %></td>
							<td valign="top" nowrap>
							<% if (arrmDropDaun7.size() > 0) { %>
								<select id="cCapo7" name="cCapo7" style="width: 20em;" onChange="armaEPb(this, 6)">
									<option value=""></option>
									<% if (arrmDropDaun7.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun7.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo8.toString() %></td>
							<td>
							<% if (arrmDropDaun8.size() > 0) { %>
								<select id="cCapo8" name="cCapo8" style="width: 20em;" onChange="armaEPb(this, 7)">
									<option value=""></option>
									<% if (arrmDropDaun8.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun8.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo9.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun9.size() > 0) { %>
								<select id="cCapo9" name="cCapo9" style="width: 20em;" onChange="armaEPb(this, 8)">
									<option value=""></option>
									<% if (arrmDropDaun9.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun9.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo10.toString() %></td>
							<td>
							<% if (arrmDropDaun10.size() > 0) { %>
									<select id="cCapo10" name="cCapo10" style="width: 20em;" onChange="armaEPb(this, 9)">
										<option value=""></option>
										<% if (arrmDropDaun10.size() > 0) { %>
										 	<% j = 0;
										 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun10.get(0);
										 	 while ( j < arrhtmlData.size()) {
										 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
										 	 %>
												<%=cDAtaHTML%>
											<% 		j++;
											}
										} %>
									</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo11.toString() %></td>
							<td valign="top" nowrap >
								<% if (arrmDropDaun11.size() > 0) { %>
								<select id="cCapo11" name="cCapo11" style="width: 20em;"  onChange="armaEPb(this, 10)">
									<option value=""></option>
									<% if (arrmDropDaun11.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun11.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo12.toString() %></td>
							<td>
							<% if (arrmDropDaun12.size() > 0) { %>
								<select id="cCapo12" name="cCapo12" style="width: 20em;" onChange="armaEPb(this, 11)">
									<option value=""></option>
									<% if (arrmDropDaun12.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun12.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo13.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun13.size() > 0) { %>
								<select id="cCapo13" name="cCapo13" style="width: 20em;" onChange="armaEPb(this, 12)">
									<option value=""></option>
									<% if (arrmDropDaun13.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun13.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo14.toString() %></td>
							<td>
							<% if (arrmDropDaun14.size() > 0) { %>
								<select id="cCapo14" name="cCapo14" style="width: 20em;"  onChange="armaEPb(this, 13)">
									<option value=""></option>
									<% if (arrmDropDaun14.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun14.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo15.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun15.size() > 0) { %>
								<select id="cCapo15" name="cCapo15" style="width: 20em;"  onChange="armaEPb(this, 14)">
									<option value=""></option>
									<% if (arrmDropDaun15.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun15.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo16.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun16.size() > 0) { %>
								<select id="cCapo16" name="cCapo16" style="width: 20em;"  onChange="armaEPb(this, 15)">
									<option value=""></option>
									<% if (arrmDropDaun16.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun16.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo17.toString() %></td>
							<td>
							<% if (arrmDropDaun17.size() > 0) { %>
								<select id="cCapo17" name="cCapo17" style="width: 20em;"  onChange="armaEPb(this, 16)">
									<option value=""></option>
									<% if (arrmDropDaun17.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun17.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo18.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun18.size() > 0) { %>
								<select id="cCapo18" name="cCapo18" style="width: 20em;"  onChange="armaEPb(this, 17)">
									<option value=""></option>
									<% if (arrmDropDaun18.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun18.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr>
							<td><%=cCapo19.toString() %></td>
							<td>
							<% if (arrmDropDaun19.size() > 0) { %>
								<select id="cCapo19" name="cCapo19" style="width: 20em;"  onChange="armaEPb(this, 18)">
									<option value=""></option>
									<% if (arrmDropDaun19.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun19.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
							
							<td><%=cCapo20.toString() %></td>
							<td valign="top" nowrap >
							<% if (arrmDropDaun20.size() > 0) { %>
								<select id="cCapo20" name="cCapo20" style="width: 20em;"  onChange="armaEPb(this, 19)">
									<option value=""></option>
									<% if (arrmDropDaun20.size() > 0) { %>
									 	<% j = 0;
									 	ArrayList arrhtmlData = (ArrayList) arrmDropDaun20.get(0);
									 	 while ( j < arrhtmlData.size()) {
									 	 	String cDAtaHTML = (String) arrhtmlData.get(j);
									 	 %>
											<%=cDAtaHTML%>
										<% 		j++;
										}
									} %>
								</select>
							<%} %>
							</td>
						</tr>
						<tr align="left">
							<td>Estructura Programatica:</td>
							<td><input type="text" name="cEP" id="cEP" value="<%=cEP.toString() %>" size="70" /></td>
							<td><input type="button" id="vEP" value="Valida EP" onclick="validaEP();"/> <input type="button" id="guardaEP" value="Guardar EP" onclick="Agrega();" <%=(!bActivaInsert?"disabled=\"disabled\"":"") %>></td>
							<td><input type="button" id="buscaEP" value="Buscar EP" onclick="BuscaEP();"/>
							<!--  input type="button" id="exportaEP" value="Exporta Excel" onclick="ExportaEP();"/></td-->
						</tr>
					</table>
				</div>
				<div id="dlgDetEPs"  title="Estructuras Programaticas actuales">
					<table   id="grdProgPresupUE" style="text-align:center;" >
						<thead>
							<tr>
								<th id="efcl">Ejecicio Fiscal</th>
								<th>EP</th>
								<!--<th>Clave SIAFF</th>-->
								<!--<th>Clave Interna</th>-->
								<!-- <th>Ramo EP</th>
								<th>Unidad Responsable EP</th>-->
								<th>Grupo Funcional</th>
								<th>Función</th>
								<th>Sub Función</th>
								<th>Programa General</th>
								<th>Actividad Institucional</th>
								<th>Programa Presupuestario</th>
								<th>Partida</th>
								<th>Tipo Gasto</th>
								<th>Fuente Financiamiento</th>
								<th>Entidad Federativa</th>
								<!--<th>Unidad Normativa</th>
								<th>Unidad Ejecutora</th>
								<th>Ramo</th>-->
							</tr>
						</thead>
						<tbody>
						<tr>
						<td></td>
						<td></td>
						<!--<td></td>-->
						<!--<td></td>-->
						<!--<td></td>
						<td></td>-->
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<!--  <td></td>
						<td></td>
						<td></td>-->
						</tr>
						</tbody>
						<tfoot>
						</tfoot>
					</table>
				</div>
				<div id="dlgError" title="Detalle de Errores de Estructuras PRogramaticas para el AnteProyecto">
					<p>Detalle de Errores </p> <a rel=""></a>
					<table   class="display" id="grdAnteProyecto">
						<tr> <td>
								<textarea id="mensajeError" name="mensajeError" rows="3" cols="150"></textarea>
						</td></tr>
					</table>
				</div>
			</form>
		</div>
	</body>
</html>
