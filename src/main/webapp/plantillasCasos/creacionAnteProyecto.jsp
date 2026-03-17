<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.ArchivoExcel"%>
<%
	ArrayList<ArrayList<String>> arrmDatosAnteProyecto = new ArrayList<ArrayList<String>>();
	//ArrayList arrmEstatusRechazo = new ArrayList();
	ArrayList<ArrayList<String>> arrResultado = new ArrayList<ArrayList<String>>();
	ArrayList arrmTechoUN = new ArrayList();
	ArrayList arrmTechoUE = new ArrayList();
	ArrayList arrmTechoEF = new ArrayList();
	ArrayList arrmTechoPP = new ArrayList();
	ArrayList arrmTechoPA = new ArrayList();
	ArrayList arrmTechoPPUE = new ArrayList();
	ArrayList arrmTechoPAUE = new ArrayList();
	ArrayList arrmVerciones = new ArrayList();
	ArrayList arrmUN = new ArrayList();
	ArrayList arrmUE = new ArrayList();
	ArrayList arrValidaTechos = new ArrayList();
	String cImportarXLS="NO";
	String cMensaje="";
	String mensajeVAdec="";
	String cUnidadResponsable="";
	String text="";
	boolean bCargaDT=true;
	boolean brechazo = false;
	boolean bErrorTecho=false;
	boolean cGrupoUSR=false;
	boolean algo2 = false;
	boolean bAplicadoCont = false;
	boolean bCargaExcel = false;
	boolean bAutoCargaXLS=false;
	boolean bApagaBotones=false;
	String pathURL = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
	
	int iarrmUE=0;
	int i=0; 
	int iPP=0;
	String cMoey="";
	String[] cClaveUN = null; 
	String[] dClaveUN = null;
	String[] mClaveUN = null;
	String[] cClaveUNM = null;
	int iUN=0;
	int iarrmUN=0;
	String[] cClaveUE = null; 
	String[] dClaveUE = null;
	String[] mClaveUE = null;
	int iUE=0;
	String[] cClaveEF = null; 
	String[] dClaveEF = null;
	String[] mClaveEF = null;
	int iEF=0;
	String[] cClavePA = null; 
	String[] dClavePA = null;
	String[] cClaveTG = null; 
	String[] dClaveTG = null;
	String[] mClavePA = null;
	int iPA=0;

	String[] cClavePP = null; 
	String[] dClavePP = null;
	String[] mClavePP = null;

	String[] cClavePAUE = null; 
	String[] dClavePAUE = null;
	String[] cClaveTGUE = null; 
	String[] dClaveTGUE = null;
	String[] cClaveUEUE = null; 
	String[] dClaveUEUE = null;
	String[] mClavePAUE = null;
	int iPAUE=0;

	String[] cClavePPUE = null; 
	String[] dClavePPUE = null;
	String[] cClavePPUEC = null; 
	String[] dClavePPUED = null;
	String[] mClavePPUE = null;
	int iPPUE=0;
	// varibles de JAva para anteproyecto
	Integer[] iConsecutivo=null;
	String[] cEPDetalle=null;
	String[] mMontoCalculado=null;
	String[] mMontoOptimo=null;
	String[] mMontoIreductible=null;
	String[] cReduccion=null;
	String[] cIncremento=null;
	int iAPD=0;
	int iVerAnte=0;
	// fin de variables

	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String cUsrLog = "";
	String cAnteProyecto="";
	String nCuenta="";
	String cDescripcion="";
	String cCampo="";
	String cUnidadNormativa="";
	String cUnidadEjecutora="";
	int nFolioAnteProyecto=0;
	int nFolioAnteProyectoAnt=0;
	int nPorcentajeReduccion=0;
	int nPorcentajeAmpliacion=0;
	String aEjercicioFiscal="";
	String textError ="";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cAutoImport="NO";
	//String cAutoImport=(String) ;
	if (session.getAttribute("objcAutoImport") != null ){
		cAutoImport = (String) session.getAttribute("objcAutoImport");
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
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete();//se usa por separado abajo

	String cAplicaDocto = "No";
	if (request.getParameter("cAutoriza") != null && request.getParameter("cAutoriza").equals("A")) {
		cAplicaDocto = "SI";
	}
	if (request.getParameter("cXLSFile") != null && request.getParameter("cXLSFile").equals("1")) {
		bCargaExcel = true;
	}

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}
	/*if (c.getCasoDato("MONEDA").getValor() != "" || c.getCasoDato("MONEDA").getValor() != null){
			cUnidadEjecutora = c.getCasoDato("MONEDA").getValor();
	}*/
	if (c.getCasoDato("EJERCICIO_FISCAL").getValor() != "" || c.getCasoDato("EJERCICIO_FISCAL").getValor() != null){
			cUnidadNormativa = c.getCasoDato("EJERCICIO_FISCAL").getValor();
	}
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	cUsrLog = usuario.getLogin();
	AnteProyectoBusinessLogic anteProy = new AnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
    cGrupoUSR=(usuario.getGrupos() != null && usuario.getGrupos().containsKey("AUTORIZADOR_ANTEPROYECTO")) ? true : false;

	/* recupera datos de techos */
	FortimaxFile[] archivoExcel = null;
		
	try{
		aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
		if (c.getCasoOperacion(0).getIdOperacion() == 1){
			archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
		}else if (c.getCasoOperacion(0).getIdOperacion() == 2){
			archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, 1);
		}else if (c.getCasoOperacion(0).getIdOperacion() == 4){
			archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, 1);
		}else{
			archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
		}
		
		if ("".equals(cUnidadEjecutora) || cUnidadEjecutora == null){
			if (session.getAttribute("objcUniEjecutora") != null){
				session.removeAttribute("objcUniEjecutora");
			}
			if ( cGrupoUSR ==  true && request.getParameter("cUniEjecutora") != null && !request.getParameter("cUniEjecutora").isEmpty()) {
				cUnidadEjecutora = request.getParameter("cUniEjecutora");				
			}else{
				cUnidadEjecutora=cUR;
			}
		}
		session.setAttribute("objcUniEjecutora", cUnidadEjecutora);

		if (request.getParameter("cRevisa") != null && request.getParameter("cRevisa").equals("A") && id_oper ==2 ){
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			anteProy.cambiaEstatus(cUnidadEjecutora, nFolio, "A", "Revisor");
		}
		if ("SI".equals(cAplicaDocto) && id_oper ==4 ){
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			anteProy.cambiaEstatus(cUnidadEjecutora, nFolio, "A", "Autorizador");
		}

		if ((c.getCasoOperacion(0).getIdOperacion() == 1)){
			if (archivoExcel != null  && ("SI".equals(cAplicaDocto)) && ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()) || "SI".equals(c.getCasoDato("APLICADO_CONT").getValor())) ) {
				if ((archivoExcel.length > 0 ) ) {
				    try{
						arrResultado = anteProy.cargaArchivoXLS(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath(), c, usuario, cUnidadEjecutora,  aEjercicioFiscal);
				    }catch(Exception ex){
						mensajeVAdec+=ex.getMessage();
				    }
					if (arrResultado.size() > 0){
						mensajeVAdec="Importacion terminada con Exito.";
						bAutoCargaXLS=true;
						// VERIFICA SI EXISTEN ERRORES
						int u=0;
						String cEPError="";
						while (arrResultado.size()-1 > u) {
							ArrayList arrError = new ArrayList();
							arrError = (ArrayList) arrResultado.get(u);
							cEPError= (String)arrError.get(1);
							textError = (String)arrError.get(8);
							if (!"".equals(textError)) {
								text +=  textError + "\\r\\n";
								bErrorTecho=true;
							} 
							int h=0;
							u++;
						}
						if (arrResultado.size() == u){
							ArrayList arrErrorEP = new ArrayList();
							arrErrorEP = (ArrayList) arrResultado.get(u);
							int j=0;
							while (arrErrorEP.size() < j ){
								textError = (String) arrErrorEP.get(j);
								if (!"".equals(textError)) {
									text += "EP:"+ cEPError + " "+ textError + "\\r\\n";
									bErrorTecho=true;
								} 
								j++;
							}
						}
					}
				}else{
					if (archivoExcel.length == 0 &&  "true".equals(c.getCasoDato("APLICADO_CONT").getValor()) ){
						mensajeVAdec="Advertencia: Se requiere que modifique y suba el archivo que ha extraído, hasta que esta actividad no este completad no podrá continuar con el proceso.";
						bApagaBotones=true;
					}
				}
			}else{
				if (archivoExcel.length == 0 && "true".equals(c.getCasoDato("APLICADO_CONT").getValor())){
					mensajeVAdec="Advertencia: Se requiere que modifique y suba el archivo que ha extraído, hasta que esta actividad no este completad no podrá continuar con el proceso.";
					bApagaBotones=true;
				}
			}
		}
		//rechazo de revisor
		if ((c.getCasoOperacion(0).getIdOperacion() ==2) ){
			if (archivoExcel.length>0 && ("SI".equals(cAplicaDocto))){
				if ((archivoExcel.length > 0) ) {
					brechazo  = anteProy.cargaArchivoXLSR(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, 1)[0].getAbsolutePath(), c, usuario );
					session.removeAttribute("objcAutoImport");
					session.setAttribute("objcAutoImport", "NO");
					if (brechazo){
							mensajeVAdec="Importacion terminada con Exito. Se Marca para rechazo.";
					}else{
						//mensajeVAdec="Importacion terminada con Exito.";
					}
				}else{
					mensajeVAdec="Falta Agregar el archivo a importar.";
				}
			}
		}
		//Rechazo Normatividad
		if ((c.getCasoOperacion(0).getIdOperacion() == 4)){
			if (archivoExcel.length>0 && ("SI".equals(cAplicaDocto))){
				if ((archivoExcel.length > 0) ) {
					brechazo  = anteProy.cargaArchivoXLSR(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, 1)[0].getAbsolutePath(), c, usuario );
					session.removeAttribute("objcAutoImport");
					session.setAttribute("objcAutoImport", "NO");
					if (brechazo){
							mensajeVAdec="Importacion terminada con Exito. Se Marca para rechazo.";
					}else{
						//mensajeVAdec="Importacion terminada con Exito.";
					}
				}else{
					//mensajeVAdec="Falta Agregar el archivo a importar.";
				}
			}
		}
		
		arrmDatosAnteProyecto= anteProy.RecuperaTechos(cUR,cUsrLog);
		if (arrmDatosAnteProyecto != null && !arrmDatosAnteProyecto.isEmpty()){
			if (arrmDatosAnteProyecto.size() > 0){
				arrmTechoUN = (ArrayList) arrmDatosAnteProyecto.get(0);
				i = 0;	
				if (arrmTechoUN != null && !arrmTechoUN.isEmpty()){
					cClaveUN = new String[arrmTechoUN.size()];
					cClaveUNM = new String[arrmTechoUN.size()];
					dClaveUN = new String[arrmTechoUN.size()];
					mClaveUN = new String[arrmTechoUN.size()];
										
					while (i < arrmTechoUN.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoUN.get(i);
						cClaveUN[i] = (String) arrmPaso.get(0);
						dClaveUN[i] = (String) arrmPaso.get(1); 
						mClaveUN[i] = (String) arrmPaso.get(2);
						i++;
					}  
					iUN=i;
					i = 0;
				}
				arrmTechoEF = (ArrayList) arrmDatosAnteProyecto.get(1);
				if (arrmTechoEF != null  && !arrmTechoEF.isEmpty()){
					cClaveEF = new String[arrmTechoEF.size()];
					dClaveEF = new String[arrmTechoEF.size()];
					mClaveEF = new String[arrmTechoEF.size()];					
					while (i < arrmTechoEF.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoEF.get(i);
						cClaveEF[i] = (String) arrmPaso.get(0);
						dClaveEF[i] = (String) arrmPaso.get(1); 
						mClaveEF[i] = (String) arrmPaso.get(2); 
						i++;
						arrmPaso=null;
					}  
					iEF=i;
					i = 0;
				} 
				arrmTechoUE = (ArrayList) arrmDatosAnteProyecto.get(2);
				if (arrmTechoUE != null && !arrmTechoUE.isEmpty()){
					cClaveUE = new String[arrmTechoUE.size()];
					dClaveUE = new String[arrmTechoUE.size()];
					mClaveUE = new String[arrmTechoUE.size()];					
					while (i < arrmTechoUE.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoUE.get(i);
						cClaveUE[i] = (String) arrmPaso.get(0);
						dClaveUE[i] = (String) arrmPaso.get(1); 
						mClaveUE[i] = (String) arrmPaso.get(2); 
						i++;
						arrmPaso=null;
					}  
					iUE=i;
					i = 0;
				}
				arrmTechoPP = (ArrayList) arrmDatosAnteProyecto.get(3);
				if (arrmTechoPP != null && !arrmTechoPP.isEmpty()){
					cClavePP = new String[arrmTechoPP.size()];
					dClavePP = new String[arrmTechoPP.size()];
					mClavePP = new String[arrmTechoPP.size()];					
					while (i < arrmTechoPP.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoPP.get(i);
						cClavePP[i] = (String) arrmPaso.get(0);
						dClavePP[i] = (String) arrmPaso.get(1); 
						mClavePP[i] = (String) arrmPaso.get(2); 
						i++;
						arrmPaso=null;
					}  
					iPP=i;
					i = 0;
				}
				arrmTechoPA = (ArrayList) arrmDatosAnteProyecto.get(4);
				if (arrmTechoPA != null && !arrmTechoPA.isEmpty() ){
					cClavePA = new String[arrmTechoPA.size()];
					dClavePA = new String[arrmTechoPA.size()];
					cClaveTG = new String[arrmTechoPA.size()];					
					dClaveTG = new String[arrmTechoPA.size()];
					mClavePA = new String[arrmTechoPA.size()];
					while (i < arrmTechoPA.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoPA.get(i);
						cClavePA[i] = (String) arrmPaso.get(0);
						dClavePA[i] = (String) arrmPaso.get(1); 
						cClaveTG[i] = (String) arrmPaso.get(2);
						dClaveTG[i] = (String) arrmPaso.get(3); 
						mClavePA[i] = (String) arrmPaso.get(4); 
						i++;
						arrmPaso=null;
					}  
					iPA=i;
					i = 0;
				}
				arrmVerciones =  (ArrayList) arrmDatosAnteProyecto.get(5);
				if (arrmVerciones != null && !arrmVerciones.isEmpty() ){
					iVerAnte=arrmVerciones.size();
				}
				arrmUN =  (ArrayList) arrmDatosAnteProyecto.get(6);
				if (arrmUN != null && !arrmUN.isEmpty() ){
					iarrmUN=arrmUN.size();
				}
				arrmUE =  (ArrayList) arrmDatosAnteProyecto.get(7);
				if (arrmUE != null && !arrmUE.isEmpty() ){
					iarrmUE=arrmUE.size();
				}
				arrmTechoPPUE = (ArrayList) arrmDatosAnteProyecto.get(8);
				if (arrmTechoPPUE != null && !arrmTechoPPUE.isEmpty()){
					cClavePPUE = new String[arrmTechoPPUE.size()];
					dClavePPUE = new String[arrmTechoPPUE.size()];
					mClavePPUE = new String[arrmTechoPPUE.size()];					
					cClavePPUEC = new String[arrmTechoPPUE.size()];
					dClavePPUED = new String[arrmTechoPPUE.size()];
					//mClavePP = new Double[arrmTechoPP.size()];					
					while (i < arrmTechoPPUE.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoPPUE.get(i);
						cClavePPUE[i] = (String) arrmPaso.get(0);
						dClavePPUE[i] = (String) arrmPaso.get(1); 
						cClavePPUEC[i] =  (String) arrmPaso.get(2);
						dClavePPUED[i] =  (String) arrmPaso.get(3);
						mClavePPUE[i] = (String) arrmPaso.get(4); 
						i++;
						arrmPaso=null;
					}  
					iPPUE=i;
					i = 0;
				}
				arrmTechoPAUE = (ArrayList) arrmDatosAnteProyecto.get(9);
				if (arrmTechoPAUE != null && !arrmTechoPAUE.isEmpty() ){
					cClavePAUE = new String[arrmTechoPAUE.size()];
					dClavePAUE = new String[arrmTechoPAUE.size()];
					cClaveTGUE = new String[arrmTechoPAUE.size()];					
					dClaveTGUE = new String[arrmTechoPAUE.size()];
					mClavePAUE = new String[arrmTechoPAUE.size()];
					cClaveUEUE = new String[arrmTechoPAUE.size()];
					dClaveUEUE = new String[arrmTechoPAUE.size()];
					while (i < arrmTechoPAUE.size()) {
						ArrayList arrmPaso =  (ArrayList) arrmTechoPAUE.get(i);
						cClavePAUE[i] = (String) arrmPaso.get(0);
						dClavePAUE[i] = (String) arrmPaso.get(1); 
						cClaveTGUE[i] = (String) arrmPaso.get(2);
						dClaveTGUE[i] = (String) arrmPaso.get(3); 
						cClaveUEUE[i] = (String) arrmPaso.get(4);
						dClaveUEUE[i] = (String) arrmPaso.get(5);
						mClavePAUE[i] = (String) arrmPaso.get(6); 
						i++;
						arrmPaso=null;
					}  
					iPAUE=i;
					i = 0;
				}
			}
		}

		if (request.getParameter("cAnteProyecto") != null && request.getParameter("cAnteProyecto").equals("Si")) {
			if (request.getParameter("DESTINO_GASTO") != null && !request.getParameter("DESTINO_GASTO").isEmpty()) {
				nFolioAnteProyectoAnt = new Integer(request.getParameter("DESTINO_GASTO")).intValue();
			}
			if (request.getParameter("nPorcentajeReduccion") != null && !request.getParameter("nPorcentajeReduccion").isEmpty()) {
				nPorcentajeReduccion = new Integer(request.getParameter("nPorcentajeReduccion")).intValue();
			}
			if (request.getParameter("nPorcentajeAmpliacion") != null && !request.getParameter("nPorcentajeAmpliacion").isEmpty()) {
				nPorcentajeAmpliacion = new Integer(request.getParameter("nPorcentajeAmpliacion")).intValue();
			}
			if (request.getParameter("cDescripcion") != null && !request.getParameter("cDescripcion").isEmpty()) {
				cDescripcion = request.getParameter("cDescripcion");
			}
			if (request.getParameter("cUniNormativa") != null && !request.getParameter("cUniNormativa").isEmpty()) {
				cUnidadNormativa = request.getParameter("cUniNormativa");
			}
			if (bCargaExcel == false ){
				if (request.getParameter("cIdTipoOperacion") != null && !request.getParameter("cIdTipoOperacion").isEmpty()) {
				    nCuenta=request.getParameter("cIdTipoOperacion");
				}
				if ("".equals(nCuenta) && (request.getParameter("DESTINO_GASTO").toString()=="") ){
					mensajeVAdec="Se requiere indicar La cuenta Origen o la versión de la creacion del Presupuesto.";
				}
			}
			
		    if ("".equals(mensajeVAdec)){
		    	int iTotDetalle= anteProy.verificaUNUE(cUnidadNormativa,cUnidadEjecutora);
		    	if (iTotDetalle > 0){
		    		if (!bCargaExcel){
						anteProy.CreaAntePRoyecto(c, usuario, nCuenta, nFolioAnteProyectoAnt, nPorcentajeReduccion, nPorcentajeAmpliacion, cUnidadEjecutora, null, cDescripcion, cCampo, cUnidadNormativa);
		    		}else{
		    			archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
						if ((archivoExcel.length > 0) ) {
							try{
				    			arrResultado=anteProy.CreaAntePRoyectoXLS(c, usuario, nCuenta, nFolioAnteProyectoAnt, nPorcentajeReduccion, nPorcentajeAmpliacion, cUnidadEjecutora, null, cDescripcion, cCampo, cUnidadNormativa, cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath());

		    				// VERIFICA SI EXISTEN ERRORES
							int u=0;
							String cEPError="";
							while (arrResultado.size()-1 > u) {
								ArrayList<String> arrError = new ArrayList<>();
								arrError = (ArrayList) arrResultado.get(u);
								cEPError= (String)arrError.get(1);
								textError = (String)arrError.get(6);
								if (!"".equals(textError)) {
									text +=  textError + "\\r\\n";
									bErrorTecho=true;
								} 
								int h=0;
								u++;
							}
							if (arrResultado.size() == u){
								ArrayList<String> arrErrorEP = new ArrayList<String>();
								arrErrorEP = (ArrayList) arrResultado.get(u);
								int j=0;
								while (arrErrorEP.size() < j ){
									textError = (String) arrErrorEP.get(j);
									if (!"".equals(textError)) {
										text += "EP:"+ cEPError + " "+ textError + "\\r\\n";
										bErrorTecho=true;
									} 
									j++;
								}
							}
							}catch(Exception ex){
								mensajeVAdec+=ex.getMessage();
						    }
						}else{
							mensajeVAdec="Falta Agregar el archivo a importar.";
						}
		    		} //tot detalle
		    		
					arrResultado=anteProy.RecuperaAnteProyecto(cUnidadEjecutora,c);
		    		if ("".equals(mensajeVAdec) && !arrResultado.isEmpty() && arrResultado != null && arrResultado.size() > 0){
						mensajeVAdec="La Creacion de Partidas para el Presupuesto ha terminado."; 
		    		}else{
						mensajeVAdec+="La Creacion de Partidas para el Presupuesto ha Fallado reportelo con el Administrador."; 
		    		}
		    	}else{
		    		mensajeVAdec="Adevertencia: La Unidad:"+cUnidadEjecutora+" no es Normada por La Unidad:"+cUnidadNormativa+".";
		    	}
		    	
		    }
		}
		arrResultado=anteProy.RecuperaAnteProyecto(cUnidadEjecutora,c);
		if (arrResultado != null && !arrResultado.isEmpty()){
			i=0; 
			iConsecutivo = new Integer[arrResultado.size()];
			cEPDetalle = new String[arrResultado.size()];
			mMontoCalculado = new String[arrResultado.size()];					
			mMontoOptimo = new String[arrResultado.size()];
			mMontoIreductible = new String[arrResultado.size()];

			cReduccion = new String[arrResultado.size()];
			cIncremento = new String[arrResultado.size()];
			while (arrResultado.size() >=(i+1)){
				ArrayList<Object> arrmODatos = new ArrayList<>();
				arrmODatos = (ArrayList) arrResultado.get(i);
				iConsecutivo[i] 	= (Integer) arrmODatos.get(0);
				cEPDetalle[i]   	= (String) arrmODatos.get(2);
				mMontoCalculado[i]	= (String) arrmODatos.get(3);
				mMontoOptimo[i]		= (String) arrmODatos.get(4);
				mMontoIreductible[i]= (String) arrmODatos.get(5);
				cReduccion[i]		=  (String) arrmODatos.get(6);
				cIncremento[i]		= (String) arrmODatos.get(7);
				i++;
			}
			iAPD= i ;
		}
		
		if (!"".equals(cUnidadEjecutora)){
			if (arrResultado != null && !arrResultado.isEmpty()){
				if (iAPD > 0){
					arrValidaTechos=anteProy.ValidaTechos(c,usuario,cUnidadEjecutora);
					if (arrValidaTechos != null && !arrValidaTechos.isEmpty()){
						if (arrValidaTechos.size() > 0){
							bErrorTecho=true;
							int g=0;
							while (g < arrValidaTechos.size()){
							    ArrayList<String> arrmMensajeTechos = new ArrayList<>();
							    arrmMensajeTechos= (ArrayList) arrValidaTechos.get(g);
							    if (arrmMensajeTechos != null && !arrmMensajeTechos.isEmpty()){
							    int f=0;
								    while (f < arrmMensajeTechos.size()){
										text +=(String) arrmMensajeTechos.get(f) + "\\r\\n";
										System.out.println("Importando excel de ante proyecto rnglon:"+f+ " Numero de Folio:"+text );
										f++;
								    }
							    }
								g++;
							}
						}else if ("".equals(text)){
								bErrorTecho=false;
						}
					}
			 	}
			 }
		}
	}finally{
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Creación de Ante Proyecto</title>

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
				oTablePA = $('#grdRetencion').dataTable( );
				oTableUE = $('#grdUniEje').dataTable( );
				oTableUN = $('#grdUnidadNormativa').dataTable( );
				oTablePP = $('#grdProgPresup').dataTable( );
				oTableEF = $('#grdMovimientos').dataTable( );
				oTablePPUE = $('#grdProgPresup').dataTable( );
				oTablePAUE = $('#grdRetencion').dataTable( );
				oTablePTGUE = $('#grdPTGUE').dataTable( );
				oTableDANTEP = $('#grdAnteProyecto').dataTable( );
				oTableUEPP = $('#grdProgPresupUE').dataTable( );

				//------------------------------------------------------------Ejecutora
				$('#grdAnteProyecto').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": true,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdAnteProyecto tbody").click(function(event) {
					$(oTableDANTEP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableDANTEP.fnGetPosition( this.nTr );
					var aData = oTableDANTEP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdAnteProyecto tbody").dblclick(function(event) {
					$(oTableDANTEP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableDANTEP.fnGetPosition( this.nTr );
						var aData = oTableDANTEP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableDANTEP.fnGetPosition( event.target.parentNode );
					var aData = oTableDANTEP.fnGetData( aPos );
				});
				//$('#grdAnteProyecto thead tr th:eq(1)').click(); 
				
//--------
				$('#grdMovimientos').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );

				$('#grdPTGUE').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );

//------------------------------------------------------------Ejecutora
				 $('#grdUniEje').dataTable({
					"iDisplayLength": 20,
					"sScrollY": 100,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdUniEje tbody").click(function(event) {
					$(oTableUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUE.fnGetPosition( this.nTr );
					var aData = oTableUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdUniEje tbody").dblclick(function(event) {
					$(oTableUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUE.fnGetPosition( this.nTr );
						var aData = oTableUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableUE.fnGetPosition( event.target.parentNode );
					var aData = oTableUE.fnGetData( aPos );
				});
//---------------------------------------------------------Normativa				
				 $('#grdUnidadNormativa').dataTable({
					"iDisplayLength": 20,
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdUnidadNormativa tbody").click(function(event) {
					$(oTableUN.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUN.fnGetPosition( this.nTr );
					var aData = oTableUN.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdUnidadNormativa tbody").dblclick(function(event) {
					$(oTableUN.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUN.fnGetPosition( this.nTr );
					var aData = oTableUN.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableUN.fnGetPosition( event.target.parentNode );
					var aData = oTableUN.fnGetData( aPos );
				});
				//grdProgPresupUE
//---------------------------------------------------------ProgramaPresupuestario				
				$('#grdProgPresupUE').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdProgPresupUE tbody").click(function(event) {
					$(oTableUEPP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUEPP.fnGetPosition( this.nTr );
					var aData = oTableUEPP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresupUE tbody").dblclick(function(event) {
					$(oTableUEPP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTableUEPP.fnGetPosition( this.nTr );
					var aData = oTableUEPP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTableUEPP.fnGetPosition( event.target.parentNode );
					var aData = oTableUEPP.fnGetData( aPos );
				});
//---------------------------------------------------------ProgramaPresupuestario				
				$('#grdProgPresup').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
				$("#grdProgPresup tbody").click(function(event) {
					$(oTablePP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTablePP.fnGetPosition( this.nTr );
					var aData = oTablePP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresup tbody").dblclick(function(event) {
					$(oTablePP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos  = oTablePP.fnGetPosition( this.nTr );
					var aData = oTablePP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos  = oTablePP.fnGetPosition( event.target.parentNode );
					var aData = oTablePP.fnGetData( aPos );
				});
//---------------------------------------------------------Partida				
				$('#grdPartida').dataTable({
						"bPaginate": false,
						"bLengthChange": false,
						"bFilter": false,
						"bSort": false,
						"bInfo": false,
						"bAutoWidth": false,
						"sScrollY": 100,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers"
					} );
				
//---------------------------------------------
				$('#grdRetencion').dataTable(
					{
						"bPaginate": false,
						"bLengthChange": false,
						"bFilter": false,
						"bSort": false,
						"bInfo": false,
						"bAutoWidth": false,
						"sScrollY": 100,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers"
	
					} );
					$("#grdPTGUE tbody").click(function(event) {
						$(grdPTGUE.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
							var aPos = grdPTGUE.fnGetPosition( this.nTr );
							var aData = grdPTGUE.fnGetData( aPos[0] );
						});
						$(event.target.parentNode).addClass('row_selected');
					});
				
					$("#grdPTGUE tbody").dblclick(function(event) {
						$(grdPTGUE.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
							var aPos = grdPTGUE.fnGetPosition( this.nTr );
							var aData = grdPTGUE.fnGetData( aPos[0] );
						});
						$(event.target.parentNode).addClass('row_selected');
						var aPos = grdPTGUE.fnGetPosition( event.target.parentNode );
						var aData = grdPTGUE.fnGetData( aPos );
						quitaFormato();
					});
					$("#grdMovimientos tbody").click(function(event) {
						$(oTableMov.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
							var aPos = oTableMov.fnGetPosition( this.nTr );
							var aData = oTableMov.fnGetData( aPos[0] );
						});
						$(event.target.parentNode).addClass('row_selected');
					});
				
				$("#grdMovimientos tbody").dblclick(function(event) {
					$(oTableMov.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTableMov.fnGetPosition( this.nTr );
					var aData = oTableMov.fnGetData( aPos[0] );

					});
					$(event.target.parentNode).addClass('row_selected');
					var aPos = oTableMov.fnGetPosition( event.target.parentNode );
					var aData = oTableMov.fnGetData( aPos );
					quitaFormato();
				});

				oTableMov = $('#grdMovimientos').dataTable( );

				$("#grdProgPresup tbody").click(function(event) {
					$(oTablePP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePP.fnGetPosition( this.nTr );
					var aData = oTablePP.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdProgPresup tbody").dblclick(function(event) {
				});
//-----------------------------------
				/* Add a click handler to the rows - this could be used as a callback */
				$("#grdPartidaUE tbody").click(function(event) {
					$(oTablePAUE.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePAUE.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablePAUE.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdPartidaUE tbody").dblclick(function(event) {
				});

//-----------------------------------
				/* Add a click handler to the rows - this could be used as a callback */
				$("#grdRetencion tbody").click(function(event) {
					$(oTablePA.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
						var aPos = oTablePA.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablePA.fnGetData( aPos[0] );
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				$("#grdRetencion tbody").dblclick(function(event) {
				});

				$("input.AyudaSyC").subIniciaDlg();
			
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			
				var aaa = <%=request.getParameter("folio")%>;
			
				$("#cRamo").val( "<%=cRamo%>" );
				$("#cUnidadResponsable").val( "<%=cUR%>" );
			
				$("#OIRAUSU").val( "<%=cUsrLog%>" );
				$("#id_oper").val( "<%=id_oper%>" );
				$("#cCentroContable").val( "<%=cCentroContable%>" );
			
				$("#tabs").tabs( {
					"show": function(event, ui) {
						var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
					if ( oTable.length > 0 ) {
						oTable.fnAdjustColumnSizing();
					}
					}
				} );
			
				$('#grdFacturas').dataTable({
					"iDisplayLength": 20,
					sScrollY: "150px",
					sScrollX: "400px",
					sScrollXInner: "100%",
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
					});
					//onLoadPlantilla();
				var $dialog;
      			//PageInit();
			    $(function() {		
		    	    $('#dialog').dialog({
		        	    autoOpen: false,
		            	width: 1900,
		            	heigth: 2900
    		    	});
    			});
    			var $dlgError;
    			$(function(){
    				$('#dlgError').dialog({
    					modal:true,
    				    autoOpen: true,
		            	width: 1200,
		            	heigth: 900    				
    				});
    			});
			});  //fin del ready

		function carga(){
			
			$('#LNK01').bind('click', function(e) { 
				$.blockUI({message: "Cargando Techos Presupuestales Unidad Normativa espere ......"});
				oTableUN.fnClearTable();
				if (<%=iUN%> > 0){
				<% for (i = 0; i < iUN; i++ ){ %>
					fnClickAddRowUN("<%=cClaveUN[i]%>", "<%=dClaveUN[i]%>", "<%=mClaveUN[i]%>");
				<%}%>
				}
				$.unblockUI();
			});
			$('#LNK02').bind('click', function(e) { 
				$.blockUI({message: "Cargando Techos Presupuestales Unidad Ejecutora espere ......"});
				oTableUE.fnClearTable();
				if (<%=iUE%> > 0){
				<% for (i = 0; i < iUE; i++ ){ %>
					fnClickAddRowUE("<%=cClaveUE[i]%>", "<%=dClaveUE[i]%>", "<%=mClaveUE[i]%>");
				<%}%>
				}
				$.unblockUI();
			});
			$('#LNK03').bind('click', function(e) { 
				$.blockUI({message: "Cargando Techos Presupuestales Partida Unidad Ejecutora espere ......"});
				oTablePA.fnClearTable();
				oTablePAUE.fnClearTable();
				if (<%=iPA%> > 0){
				<% for (i = 0; i < iPA; i++ ){ %>
					fnClickAddRowB ("<%=cClavePA[i]%>", "<%=dClavePA[i]%>", "<%=cClaveTG[i]%>", "<%=dClaveTG[i]%>", "<%=mClavePA[i]%>" );
				<%}%>
				}
				$.unblockUI();
			});
			$('#LNK04').bind('click', function(e) { 
				$.blockUI({message: "Cargando Techos Presupuestales Entidad Federativa espere ......"});
				oTableEF.fnClearTable();
				if (<%=iEF%> > 0 ){
				<% for (i = 0; i < iEF; i++ ){ %>
					fnClickAddRowEF("<%=cClaveEF[i]%>", "<%=dClaveEF[i]%>", "<%=mClaveEF[i]%>");
				<%}%>
				}
				$.unblockUI();
			});
			$('#LNK05').bind('click', function(e) { 
				$.blockUI({message: "Cargando Techos Presupuestales Programa Presupuestal espere ......"});
				oTablePP.fnClearTable();
				if (<%=iPP%> > 0){
				<% for (i = 0; i < iPP; i++ ){ %>
					fnClickAddRowPP( "<%=cClavePP[i]%>", "<%=dClavePP[i]%>" , "<%=mClavePP[i]%>");
				<%}%>
				}
				$.unblockUI();
			});
			$('#LNK06').bind('click', function(e) {
				$.blockUI({message: "Cargando Techos Presupuestales Partida Unidad Ejecutora espere ......"});
				oTablePTGUE.fnClearTable();
				if (<%=iPAUE%> > 0){
				<% for (i = 0; i < iPAUE; i++ ){ %>                                                                                            
					fnClickAddRowBUE ("<%=cClavePAUE[i]%>", "<%=dClavePAUE[i]%>", "<%=cClaveTGUE[i]%>", "<%=dClaveTGUE[i]%>", "<%=cClaveUEUE[i]%>", "<%=dClaveUEUE[i]%>","<%=mClavePAUE[i]%>" );
				<%}%>                                                                                                                        
				}
				$.unblockUI();
			});
			$('#LNK07').bind('click', function(e) { 
				$.blockUI({message: "Cargando Techos Presupuestales Programa Presupuestal Unidad Ejecutora espere ......"});
				oTableUEPP.fnClearTable();
				if (<%=iPPUE%> > 0){
				<% for (i = 0; i < iPPUE; i++ ){ %>
					fnClickAddRowPPUE( "<%=cClavePPUE[i]%>", "<%=dClavePPUE[i]%>", "<%=cClavePPUEC[i]%>", "<%=dClavePPUED[i]%>", "<%=mClavePPUE[i]%>");
				<%}%> 
				}
				$.unblockUI();
			});
			
			var justi=document.getElementById("mensajeError");
			justi.value="<%=text%>";

		 	if (<%=bErrorTecho%> && <%=id_oper%> != 11 ){
		 		$("#pb_save").attr("disabled","disabled");
		 		//$("#pb_send").attr("disabled","disabled");
	 		}else if (<%=id_oper%> == 11 ){
	  			//parent.document.getElementById("pb_save").disabled=false;
	  			
	  			parent.document.getElementById("pb_cancel").disabled=true;
	 		}
		}

		function fnClickAddRowComprobatoria(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) {
			$('#grdFacturas').dataTable().fnAddData( [A,B,C ]);
		}
			//---------------------------------------------------------------------
			function fnClickAddRowB(A, B, C,D,E) {
						$('#grdRetencion').dataTable().fnAddData( [ A,B,C,D,E ] );
			}
			function fnClickAddRowBUE(A,B,C,D,E, F,G) {
					$('#grdPTGUE').dataTable().fnAddData( [ A,B,C,D,E, F,G ] );
			}
			function fnClickAddRowPP(A, B, C) {
						$('#grdProgPresup').dataTable().fnAddData([ A,B,C ]);
			}
			function fnClickAddRowPPUE(A,B,C,D,E) {
						$('#grdProgPresupUE').dataTable().fnAddData([ A,B,C,D,E ]);
			}
		
			function fnClickAddRowUE(A, B, C) {
						$('#grdUniEje').dataTable().fnAddData( [ A,B,C ] );
			}
		
			function fnClickAddRowUN(A, B, C) {
						$('#grdUnidadNormativa').dataTable().fnAddData( [ A,B,C ] );
			}
		
			function fnClickAddRowEF(A, B, C) {
						$('#grdMovimientos').dataTable().fnAddData( [ A,B,C ] );
			}

		function onSubmit(id_oper){
			var p = window.parent;
			var valida_campos = true;
			try{
		 		if (<%=bErrorTecho%> == true && <%=archivoExcel.length==1%>){
		 			$("#pb_save").attr("disabled","disabled");
		 			$("#pb_send").attr("disabled","disabled");
				}else if(<%=arrResultado.size()>0%> && <%=bErrorTecho%> == false && <%=archivoExcel.length==1%>){
					/*if (parent.document.getElementById("gstnSubject").value != ""){
						parent.document.getElementById("pb_save").disabled=false;
					}else{
						parent.document.getElementById("pb_save").disabled=true;
					}*/ //gstnSubject ni existe!!!
					//parent.document.getElementById("pb_send").disabled=false;
					parent.document.getElementById("pb_send").disabled=false;
					parent.document.getElementById("pb_save").disabled=true;
				}
				guardaExp();
				if ((<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))%> && <%=bAutoCargaXLS%> == false ) && id_oper == 1){
					fnImportarXLS();
				}
				if (id_oper == 2 || id_oper == 4){
					if (document.formAnteProy.chRechaza.checked && <%=archivoExcel.length %>  < 1){
						alert("Falta Agregar el Archivo con los Rechazos.");
					}else if (document.formAnteProy.chRechaza.checked){
						fnImportarXLS();	
					}else{
						parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_save").disabled=true;
					}
				}
			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

 		function onPostSubmit(id_oper){//validaciones del boton enviar
	 		if (<%=bErrorTecho%> == true){
	 			$("#pb_save").attr("disabled","disabled");
	 			$("#pb_send").attr("disabled","disabled");
	 		}else{
					if (parent.document.getElementById("gstnSubject").value != ""){
						parent.document.getElementById("pb_save").disabled=false;
					}else{
						parent.document.getElementById("pb_save").disabled=true;
					}
	 		}
	  		return true;
		}
  	
		function onLoadPlantilla(){
			carga();
			var nLongMsg="<%=mensajeVAdec.length()%>";
			if (nLongMsg > 0){
				alert("<%=mensajeVAdec%>");
			}
			if (<%=iAPD%> > 0) { 
	 			document.getElementById("btCrear").disabled=true;
				document.getElementById("btnMostrar").disabled=false;
				//document.getElementById("Exportar").disabled=false;
	 			$("#pb_save").attr("disabled","disabled");
	 			$("#pb_send").attr("disabled","disabled");
	 		}else{
				document.getElementById("btCrear").disabled=false;
	 			document.getElementById("btnMostrar").disabled=true;
				parent.document.getElementById("pb_save").disabled=false;
	 			//document.getElementById("Exportar").disabled=true;
	 		}
			if(<%=id_oper%>==3){
				document.getElementById("btCrear").disabled=false;
			}
			if (<%=id_oper%> > 1){
	 			parent.document.getElementById("pb_cancel").disabled=true;
	 			document.getElementById("DESTINO_GASTO").readonly=true;
	 			document.getElementById("cDescripcion").readonly=true;
	 			if (<%=cGrupoUSR%> ==  true ) 
	 				document.getElementById("cUniEjecutora").readonly=true;
	 			document.getElementById("cUniNormativa").readonly=true;
	 			document.getElementById("nPorcentajeAmpliacion").readonly=true;
	 			document.getElementById("nPorcentajeReduccion").readonly=true;
	 			document.getElementById("cIdTipoOperacion").readonly=true;
	 		}
	 		if  (<%=id_oper%> > 1){
					parent.document.getElementById("pb_cancel").disabled=true;
					//$("#Exportar").attr('disabled','disabled');
	 		}
	 		if (<%=bAutoCargaXLS%>){
	  			var p = window.parent;
				p.gestion.setAplicadoCont("false");
  				guardaExp();
  				<% bAutoCargaXLS=false;%>
	 		}
	 		if (<%=bErrorTecho%> == true){
	 		    if (<%=id_oper%> == 1 || <%=id_oper%> == 2 || <%=id_oper%> == 4){
		 			if (<%=archivoExcel.length %> < 1 ){
			 			$("#pb_save").attr("disabled","disabled");
			 			$("#pb_send").attr("disabled","disabled");
			  			parent.document.getElementById("pb_send").disabled=true;
						parent.document.getElementById("pb_save").disabled=true;
		 			}
				}else{
					/*if (parent.document.getElementById("gstnSubject").value != ""){
						parent.document.getElementById("pb_send").disabled=false;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}*/ //gstnSubject ya ni existe!!!
	 			}
	 		}else{
	 		    /*if (<!%=id_oper%> <= 4 &&<!%=id_oper%> >1){
					if (parent.document.getElementById("gstnSubject").value != ""){
						parent.document.getElementById("pb_send").disabled=false;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
	  			}*/
	 		}
	 		if (<%=bApagaBotones%>){
	  			parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_save").disabled=true;
	 		}
	 		/*if (<!%=brechazo %>){
	 			ResponsableSiguiente(<!%=id_oper%>);
	 			OperacionSiguiente(<!%=id_oper%>);
	 		}*/
	 		
	 		$("#cUniEjecutora").change(function(){
	 			$("#cUnidadEjecutora").val($("#cUniEjecutora").val());
	 		});
		}
		
		function ResponsableSiguiente(id_oper){
	 		/*if (<!%=bErrorTecho%> == true){
	 			$("#pb_save").attr("disabled","disabled");
	 			$("#pb_send").attr("disabled","disabled");
	  			parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_save").disabled=true;
	 		}*/
			if(id_oper==1)
				return "REVISOR_ANTEPROYECTO_"+$("#cUniNormativa").val();
			if(id_oper==2  && document.formAnteProy.chRechaza.checked )  {
				return "CAPTURISTA_RPROYECTO";
			}
			else if(id_oper==2 ){
				onActualizaEstatus("cRevisa=A");
				return "AUTORIZADORANTEPROYECTO";
			}
			if(id_oper==3)
				return "REVISOR_ANTEPROYECTO_"+$("#cUniNormativa").val();
			if(id_oper==4){
				  if(document.formAnteProy.chRechaza.checked)
					  return "RENORMA_ANTEPROYECTO_"+$("#cUniNormativa").val();
				  else{
					  onActualizaEstatus("cAutoriza=A");
						return "CONSULTA_ANTEPROYECTO";
				}
			}
				
			
			if(id_oper==5)
				return "CONSULTA_ANTEPROYECTO";
			if(id_oper==6)
				return "REVISOR_ANTEPROYECTO";
			if(id_oper==7)
				return "REVISOR_ANTEPROYECTO";
			if(id_oper==8)
				return "CAPTURISTA_ANTEPROYECTO";
			if(id_oper==9)
				return "AUTORIZADOR_ANTEPROYECTO";
			if(id_oper==10)
				return "REVISOR_ANTEPROYECTO";
			if(id_oper==11)
				return "CONSULTA_ANTEPROYECTO";
		}

		function OperacionSiguiente(id_oper){
			if(id_oper==1)
				return "revisa_anteproyecto";
			if(id_oper==2){
				if (document.formAnteProy.chRechaza.checked ){
					if (<%=archivoExcel.length %> < 1 ){
						//alert("Falta Agregar el Archivo a Importar.");
			  			parent.document.getElementById("pb_send").disabled=true;
						parent.document.getElementById("pb_save").disabled=true;
					}
					return "captura_rproyecto";
				}else if(!<%=brechazo%>){
					return "autanteproyecto";
			}
			}
			
			if(id_oper==3)
				return "revisa_anteproyecto";
			//if(id_oper==4 && <!%=brechazo%>){
			if(id_oper==4){
				if (document.formAnteProy.chRechaza.checked ){
					//alert("Se rechAZA");
					return "renorma_anteproyecto";
				}else{
					//alert("Se Autoriza");
					return "consultaanteproyecto";
				}
			}
			if(id_oper==6)
				return "revisa_anteproyecto";
			if(id_oper==7)
				return "captura_anteproyecto";
			if(id_oper==8)
				return "consultaanteproyecto";
			if(id_oper==9)
				return "consultaanteproyecto";
			if(id_oper==10)
				return "consultaanteproyecto";
			if(id_oper==11)
				return "revisa_anteproyecto";
			if(id_oper==12)
				return "consultaanteproyecto";
		}

		function onPostDisplay(){
		}
		
		function onActualizaEstatus(cTipoAutoriza){
	   		var strAction="creacionAnteProyecto.jsp?id_oper="+<%=id_oper%>+"&"+cTipoAutoriza;
	   		document.formAutoriza.action=strAction;
			document.formAutoriza.submit();		
		}
	 	function get(name) {
			return document.getElementById(name).value;
		}

		function fnCrear(){
			if (document.getElementById("cUniNormativa").value == ""){
				alert("Advertencia: La Unidad Normativa es requerida.");
				return;
			}
			else{
				document.getElementById("cUnidadResponsable").value=document.getElementById("cUniNormativa").value;
				
			}
			if (document.getElementById("cDescripcion").value == ""){
				alert("Advertencia: la Descripción de la versión es requerida.");
				return;
			}
			if (<%=cGrupoUSR%> ==  true ){
				//if (document.getElementById("cUniEjecutora").value != ""){ //SI ya lo estas validando antes por que haces esto?
				                                                             //si escogieron uno antes y lo cambian se va a quedar con el primero porque ya no va a entrar aqui
				                                                             //y va a guardar mal la información!!
					//document.getElementById("cUnidadEjecutora").value=document.getElementById("cUniEjecutora").value;
				//}
			}else{
				document.getElementById("cUnidadEjecutora").value="<%=cUR%>";
			}
	 		//guardaExp();
	 		document.getElementById("btCrear").disabled=true;
			document.getElementById("btnMostrar").disabled=false;
			//document.getElementById("Exportar").disabled=false;
			//document.getElementById("Importar").disabled=false;
			guardaExp();
			$.blockUI({message: "Procesando Creacion de Partidas para el Presupuesto espere ......"});
			if (document.formAnteProy.chExcel.checked){
				document.formAnteProyXLS.action="creacionAnteProyecto.jsp?cAnteProyecto=Si&cXLSFile=1&cUniEjecutora="+$("#cUnidadEjecutora").val();
				document.formAnteProyXLS.submit();
			}else{
				document.formAnteProy.submit();
			}
			//setTimeout($.unblockUI, 6000);
		}

		function fnExportaXLS(){
	  		var p = window.parent;
			p.gestion.setAplicadoCont("true");
  			guardaExp();
			$.blockUI({message: "Procesando Exportacion espere ......"});
			if (<%=id_oper%> == 1){
		  		parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_save").disabled=true;
			}
			document.ExportaExcel.submit();
			
 			if ( <%=id_oper%>==2  && (document.formAnteProy.chRechaza.checked )){
 				//alert("Falta agregar el archivo de los Rechazos.");
	  			parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_save").disabled=true;
 			}
			setTimeout($.unblockUI, 6000);
		}
		
		function fnImportarXLS(){
			$.blockUI({message: "Procesando Importacion espere ......"});
			if (<%=id_oper%>  == 4){
				document.formRNormativa.submit();
			}else{
				//document.ImportaExcel.submit();
				document.formAutoriza.submit();
			}
			setTimeout($.unblockUI, 6000); 
		}
		
	   	function guardaExp(){
	  		var p = window.parent;
			p.gestion.setFolio(get("FOLIO"));
			p.gestion.setOperador(get("OPERADOR"));
			p.gestion.setFechaDocumento(get("FECHA_SOLICITUD"));
			if (<%=cGrupoUSR%> ==  true )
				p.gestion.setMoneda(get("cUniEjecutora"));
			else
				p.gestion.setMoneda("<%=cUR%>");
			p.gestion.setEjercicioFiscal(get("cUniNormativa"));
			p.gestion.setConceptoMov("Creación de Presupuesto");
	  		//parent.document.getElementById("pb_send").disabled=false;
	  		parent.document.getElementById("pb_save").click();
	  	}
    
	   	function MostrarDialog() {
			var A="",B="",C="",D="",E="",F="",G="",H="";
			document.getElementById("btnMostrar").disabled=true;
			if (<%=bCargaDT%> ){
				if (<%=iAPD%> > 0 ){
					<% for (i = 0; i < iAPD; i++ ){ %>
					
						A="<%=iConsecutivo[i]%>";
						B="<%=cEPDetalle[i]%>";
						C="<%=mMontoCalculado[i]%>";
						D="<%=mMontoOptimo[i]%>";
						E="<%=mMontoIreductible[i]%>";
						F="<%=cReduccion[i]%>";
						G="<%=cIncremento[i]%>";
				    	$('#grdAnteProyecto').dataTable().fnAddData( [ A,B,C,D,E,F,G ] );
					<%}%>
				}
				<%bCargaDT=false;%>
			}
			
	      //$('#grdAnteProyecto thead tr th:eq(1)').click(); 
	      $('#dialog').dialog('option', 'modal', true).dialog('open');
	      //$('#grdAnteProyecto thead tr th:eq(1)').click(); 
			document.getElementById("btnMostrar").disabled=false;
	      return true;
	  }       
 
	function cambiaSeleccionCuenta(){
		if (document.getElementById("cIdTipoOperacion").value != ""){
			$("DESTINO_GASTO").attr('disabled', 'disabled');
			document.getElementById("DESTINO_GASTO").disabled=true;
		}else{
			document.getElementById("DESTINO_GASTO").disabled=false;
		}
	} 
	
	function cambiaSeleccionVercion(){
		if (document.getElementById("DESTINO_GASTO").value != ""){
			$("cIdTipoOperacion").attr('disabled', 'disabled');
			document.getElementById("cIdTipoOperacion").disabled=true;
		}else{
			document.getElementById("cIdTipoOperacion").disabled=false;
		}
	}

  	function fnImportarExcel(){
  		var msgAlert="";
  		if(	document.getElementById("importExcel").value == "" ){
  			msgAlert+="El archivo Excel es requerido";
  		}

  		if(msgAlert!=""){
  			alert(msgAlert);
  			return false;
  		}
  		else{
	  		var p = window.parent;
			p.gestion.setAplicadoCont("true");
  			guardaExp();
			<% session.setAttribute("objcAutoImport", "SI"); %>
  			$.blockUI({message: "Procesando espere ......"});
  			<% if (id_oper ==  1) { %>
	  			document.forms.upExcel.action="../caso/firmardoc?carpeta=2";
  			<% } %> 
  			<% if (id_oper ==  2) { %>
	  			document.forms.upExcel.action="../caso/firmardoc?carpeta=3";
  			<% } %> 
  			<% if (id_oper ==  4) { %>
	  			document.forms.upExcel.action="../caso/firmardoc?carpeta=4";
  			<% } %> 
  			document.upExcel.submit();
  			
  			return true;
  		}
  	}

	
	function validate(chk){
		document.getElementById("chExcel").disabled=true;
		guardaExp();
		if (chk.checked == 1){
				<%bCargaExcel=true;%>
		}else{
				<%bCargaExcel=false;%>
		}
	}
	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
		<form id="formAnteProyXLS" name ="formAnteProyXLS" action="creacionAnteProyecto.jsp?cAnteProyecto=Si&cXLSFile=1" method="post" ></form>
		<form id="ImportaExcel" name="ImportaExcel" action="creacionAnteProyecto.jsp?cImporXLS=SI" method="post" ></form>
		<form id="ExportaExcel" name="ExportaExcel" action="../gstnmngr/AnteProyectoLayoutServlet" method="post" ></form>
		<form id="formAutoriza" name="formAutoriza" action="creacionAnteProyecto.jsp?cAutoriza=A"  method="post" ></form>
		<form id="formRNormativa" name="formRNormativa" action="creacionAnteProyecto.jsp?cImpNormXLS=Si"  method="post" ></form>
		<h1>Creación de Ante Proyecto</h1>		
		<!-- 
		<form id="upExcel" name="upExcel" action="../caso/firmardoc?carpeta=2" enctype="multipart/form-data" method="post">
			<input type="file" id="importExcel" name="importExcel" size="32" value=""></input>
			<input type="button" id="ImportarExcel" name="ImportarExcel" value="Importar Excel" onclick="fnImportarExcel();"></input>
		</form> -->
		<form id="formAnteProy" name="formAnteProy" action="creacionAnteProyecto.jsp?cAnteProyecto=Si" method="post">
			<input type="hidden" value="" id="rowsAffected" name="rowsAffected">
			<input type="hidden" id="cRamo" name="cRamo" value="16">
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUnidadNormativa%>">
			<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
			<input type="hidden" id="cCentroContable" name="cCentroContable">
			<input type="hidden" id="id_oper" value="">
			<input name="cUnidadEjecutora" type="hidden" id="cUnidadEjecutora" value="<%=cUnidadEjecutora%>">
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="<%=aEjercicioFiscal %>"/>
			<div class="dvGeneral">
				<table height="66" width="100%" border="0">
					<tr align="left">
						<td>
							<input type="hidden" name="No_Folio" type="text" style="text-transform:uppercase" class="paso01" id="No_Folio" onkeypress="valFmt(this,11)" size="20" maxlength="40">
							Origen:<input type="text" size="30" value="DLQROO/DISTRITO DE RIEGO 102">
						</td>
						<td nowrap ></td>
						<td colspan="2">Folio SAI:<input type="text" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" readonly /><input type="hidden" readonly type="text" id="id_caso" name="id_caso">
							<input type="hidden" value="<%=aEjercicioFiscal %>" id="cEjercicio" name="cEjercicio"></td>
						<td></td>
					</tr>
					<tr align="left">
							<td nowrap>Cuenta Origen: </td>
							<td colspan="2" >
								<select id="cIdTipoOperacion" class="paso01" name="cIdTipoOperacion" style="width: 20em;" onchange="cambiaSeleccionCuenta();">
									<option value="" selected></option>
									<option value="81101">81101   Original Autorizado</option>
									<option value="81102">81102   Modificado</option>
									<option value="82104">82104   Devengado</option>
								</select>
								<%if(id_oper==1 || id_oper==3){%>
									<input type="checkbox" id="chExcel" name="chExcel" onclick="return validate(chExcel);">Desde Archivo de Excel
								<%} %>
							</td>
							<td><input type="button"  value="Crear Anteproyecto" name="btCrear" id="btCrear" onClick="fnCrear()"  /></td>
						</tr>
						<tr align="left">
							<td colspan="3">% de Incremento <input type="text" maxlength="15" size="5" name="nPorcentajeAmpliacion" id="nPorcentajeAmpliacion" class="" /> % de Decremento <input type="text" maxlength="15" size="5" name="nPorcentajeReduccion"  ID="nPorcentajeReduccion" />  </td>
							<td ><input id="btnMostrar" name="btnMostrar" type="button" value="Mostrar Detalle" onclick="MostrarDialog()"  /></td>
						</tr>
						<tr align="left">
							<td nowrap>Unidad Normativa</td>
							<td valign="top" nowrap colspan="2">
								<select id="cUniNormativa" class="paso01" name="cUniNormativa" style="width: 40em;">
									<option value=""></option>
									<% if (iarrmUN > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iarrmUN) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmUN.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td valign="top" nowrap><input type="button" id="Exportar" value="Exportar"  onClick="fnExportaXLS()" ></input></td>
						</tr>
						<%if (cGrupoUSR){ %>
							<tr>
							<td nowrap>Unidad Ejecutora</td>
							<td valign="top" nowrap colspan="2">
								<select id="cUniEjecutora" class="paso01" name="cUniEjecutora" style="width: 40em;">
									<option value=""></option>
									<% if (iarrmUE > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iarrmUE) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmUE.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td valign="top" nowrap>
								<input type="button" value="Integrar"/>
							</td>
							</tr>
						<%} %>
						<tr>
							<td>Descripción de la versión:</td>
							<td  colspan="2" ><input type="text" maxlength="10" size="9" name="cDescripcion" id="cDescripcion" class="" /></td>
							<td><input type="hidden" id="Importar" value="Importar"  onClick="fnImportarXLS();" ></input>
							<input type="button" value="Editar">
							 <% if (id_oper == 2 ||id_oper==4) {%> <input type="checkbox" id="chRechaza" name="chRechaza" >Rechazar </td> <% } %>
						</tr>
						<tr>
							<td>Versión de Ante Proyecto:</td>
							<td  colspan="2" >
								<select id="DESTINO_GASTO" class="paso01" name="DESTINO_GASTO" style="width: 40em;">
									<option value=""></option>
									<% if (iVerAnte > 0) { %>
									 	<% int j = 0;
									 	 while ( j < iVerAnte) {
									 	 	ArrayList arrmVercionPaso = new ArrayList();
									 	 	arrmVercionPaso = (ArrayList) arrmVerciones.get(j);
									 	 %>
											<option value="<%=(String) arrmVercionPaso.get(0)%>"><%=(String)arrmVercionPaso.get(1) %></option>
										<% 		j++;
										}
									} %>
								</select>
							</td>
							<td align="right"></td>
						</tr>
						<tr>
							<td colspan="2">Techos Presupuestales por:</td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</div>
					<div id="dialog" title="Detalle de AnteProyecto">
		<p>Consulta de Creacion del Presupuesto </p> <a rel=""></a>
		<table   class="display" id="grdAnteProyecto">
			<thead>
				<tr>
					<th>Consecutivo</th>
					<th>Clave EP</th>
					<th>$ Calculado</th>
					<th>$ Optimo</th>
					<th>$ Ireductible</th>
					<th>¿Reduccion?</th>
					<th>¿Incremento?</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
			<tfoot>
			</tfoot>
		</table>
	</div>
	<div id="multitabs" style="width: 110%">
					<div id="tabs" style="width: 100%">
						<ul>
							<li> <a id="LNK01" href="#tabs-5" class="">Unidad Normativa</a> </li>
							<li> <a id="LNK02" href="#tabs-3" class="">Unidad Ejecutora</a> </li>
							<li> <a id="LNK03" href="#tabs-1" class="">Partida</a> </li>
							<li> <a id="LNK04" href="#tabs-4" class="">Entidad Federativa</a></li>
							<li> <a id="LNK05" href="#tabs-2" class="">Programa Presupuestario</a></li>
							<li> <a id="LNK06" href="#tabs-6" class="">Partida Unidad Ejecutora</a></li>
							<li> <a id="LNK07" href="#tabs-7" class="">Programa Presupuestario Unidad Ejecutora</a></li>
						</ul>
						<div id="tabs-5">
							<table border="0" cellspacing="0" cellpadding="0" style="width: 550px">
								<tr><td  align="left"><input type="hidden" id="txtGridtUniNormC" name="txtGridtUniNormC" class="" maxlength="5" size="4" type="text" title=""></input> </td>
								<td align="left"><input type="hidden" id="txtGridtUniNormD" name="txtGridtUniNormD" class="" maxlength="80" size="70" type="text"></input> </td>
								<td align="left"><input type="hidden" id="mMontoUN" name="mMontoUN" class="" maxlength="20" size="25" type="text"></input> </td>
								<td align="left">
								</td>
								</tr>
								<tr>
									<td align="right"> </td>
									<td align="right"></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
							</table>
							<table   class="display" id="grdUnidadNormativa">
								<thead>
									<tr>
										<th>Clave</th>
										<th nowrap>Unidad Normativa</th>
										<th>Monto</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
						<div id="tabs-1" >
							<table border="0">
								<tr><td></td>
									<td><input type="hidden" name="txtGridOGTOC" 	class=""  id="txtGridOGTOC"  value="" size="5"></td>
									<td nowrap><input type="hidden" name="txtGridOGTOD" 	class="" id="txtGridOGTOD" 	value="" size="90"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td></td>
									<td><input type="hidden" name="txtGridCTG" 	class="" type="text" id="txtGridCTG" value="" size="1"></td>
										<td><input type="hidden" name="dTipoGasto" 	class="" type="text" id="txtGridDTG" value="" size="90"></td>
										<td></td>
										<td></td>
								</tr>
								<tr><td></td>
									<td><input type="hidden" name="mTechoPartida" 	class="" type="hidden" id="mTechoPartida" 	value="0" maxlength="25" size="25"  ></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td>
									</td>
									<td></td>
									<td></td>
								</tr>
							</table>
							<table   id="grdRetencion">
								<thead>
									<tr>
										<th>Clave</th>
										<th nowrap>Descripción Partida </th>
										<th>Clave</th>
										<th nowrap>Descripción Tipo Gasto</th>
										<th nowrap>$ Techo</th>
									</tr>
								</thead>
								<tbody style="width: 100%">
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
						<div id="tabs-2" >
							<table  border="0" cellspacing="0" cellpadding="0" style="width: 750px">
								<tr>
									<td><input type="hidden" class="" name="txtGridtProgPresupC" type="text" id="txtGridtProgPresupC" onkeypress="" size="6" maxlength="6" title="Clave del Programa Presupuestario"></td>
									<td><input type="hidden" class="" name="txtGridtProgPresupD" type="text" id="txtGridtProgPresupD" onkeypress="" size="70" maxlength="200"></td>
									<td><input type="hidden" class="" name="mMontoTopeProgPres" type="text" id="mMontoTopeProgPres" onkeypress="" maxlength="25" size="20" title="Monto de Tope para el Programa Presupuestario"></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td colspan="5">
									</td>
								</tr>
							</table>
							<table   class="display" id="grdProgPresup">
								<thead>
									<tr>
										<th>Clave </th>
										<th nowrap>Programa Presupuestario</th>
										<th>$ Techo</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
						<div id="tabs-3">
							<table border="0" cellspacing="0" cellpadding="0" style="width: 550px">
								<tr>
									<td><input type="hidden" id="txtGridtUniEjeC" name="txtGridtUniEjeC" class="" maxlength="3" size="3" type="text" title="Clave de la Unidad Ejecutora."></input></td>
									<td><input type="hidden" id="txtGridtUniEjeD" name="txtGridtUniEjeD" class="" maxlength="80" size="70" type="text"></input></td>
									<td rowspan="1" align="left"><input type="hidden" id="mMontoUE" name="mMontoUE" class="" maxlength="25" size="20" type="text" title="Monto del Techo para la Unidad Ejecutora"></input> </td>
									<td>
									</td>
								</tr>
							</table>
							<table   class="display" id="grdUniEje">
								<thead>
									<tr>
										<th>Clave </th>
										<th nowrap>Unidad Ejecutora</th>
										<th>$ Techo</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
						<div id="tabs-4">
							<table border="0" cellspacing="2" cellpadding="0">
								<tr>
									<td><input type="hidden" class="" name="txtGridEntFederalC" type="text" id="txtGridEntFederalC" onkeypress="" size="6" maxlength="6" title="Clave de la Entidad Federativa"></td>
									<td><input type="hidden" class="" name="txtGridEntFederalCabr" type="text" id="txtGridEntFederalCabr" onkeypress="" size="70" maxlength="100"></td>
									<td><input type="hidden" class="" name="mMontoTopeEntidad" type="text" id="mMontoTopeEntidad" onkeypress="" size="20" maxlength="25" Title="Monto del Techo Presupuestal para la Entidad Federativa"></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td colspan="2"  align="center">
									</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							<table   class="display" id="grdMovimientos">
								<thead>
									<tr>
										<th align="left">Clave</th>
										<th nowrap>Entidad Federativa</th>
										<th align="left">% Techo</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
						<div id="tabs-6">
							<table border="0" cellspacing="2" cellpadding="0">
								<tr><td></td>
									<td><input type="hidden" type="text" name="txtGridOGTOC" 	class=""  id="txtGridOGTOC"  value="" size="5"></td>
									<td nowrap><input type="hidden" type="text" name="txtGridOGTOD" 	class="" id="txtGridOGTOD" 	value="" size="90"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td></td>
									<td><input type="hidden" name="txtGridCTG" 	class="" type="text" id="txtGridCTG" value="" size="1"></td>
										<td><input type="hidden" name="dTipoGasto" 	class="" type="text" id="txtGridDTG" value="" size="90"></td>
										<td></td>
										<td></td>
								</tr>
								<tr><td></td>
									<td><input type="hidden" type="text" name="txtGridOGTOC" 	class=""  id="txtGridOGTOC"  value="" size="5"></td>
									<td nowrap><input type="hidden" type="text" name="txtGridOGTOD" 	class="" id="txtGridOGTOD" 	value="" size="90"></td>
									<td nowrap></td>
									<td></td>
								</tr>
								<tr><td></td>
									<td><input type="hidden" type="text" name="mTechoPartida" 	class="" type="hidden" id="mTechoPartida" 	value="0" maxlength="25" size="20"  ></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td colspan="2"  align="center">
									</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							<table   class="display" id="grdPTGUE">
								<thead>
									<tr>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Partida</th>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Tipo de Gasto</th>
										<th align="left">Clave</th>
										<th nowrap>Descripcion de Unidad</th>
										<th align="left">% Techo</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>

						<div id="tabs-7" >
							<table  border="0" cellspacing="0" cellpadding="0" style="width: 750px">
								<tr>
									<td><input type="hidden" class="" name="txtGridtProgPresupCU" type="text" id="txtGridtProgPresupCU" onkeypress="" size="6" maxlength="6" title="Clave del Programa Presupuestario"></td>
									<td><input type="hidden" class="" name="txtGridtProgPresupDU" type="text" id="txtGridtProgPresupDU" onkeypress="" size="70" maxlength="200"></td>
									<td><input type="hidden" class="" name="mMontoTopeProgPresU" type="text" id="mMontoTopeProgPresU" onkeypress="" maxlength="20" size="15" title="Monto de Tope para el Programa Presupuestario"></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td colspan="5">
									</td>
								</tr>
							</table>
							<table   class="display" id="grdProgPresupUE">
								<thead>
									<tr>
										<th>Clave </th>
										<th nowrap>Programa Presupuestario</th>
										<th>Clave </th>
										<th nowrap>Unidad Ejecutora</th>
										<th>$ Techo</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
					</div>
					</div>
			</form>
		</div>
					
	<div id="dlgError" title="Mensaje">
		<table align="center" width="100%">
	
	<tr>
		<td>
			<table align="center">
					<tr>
						<td align="center">&nbsp;</td>
						<td align="center">Dice:</td>
						<td align="center">Debe decir:</td>
					</tr>
					<tr>
						<td align="right">EP:</td>
						<td align="left"><input type="text" name="EPDice" size="65" maxlength="65" readOnly value="2013.16.B00.2.1.02.00.003.E006.21101.1.1.21.00000000000.B53.B08" style="background-color: gray;"/></td>
						<td align="left"><input type="text" name="EPDebeDecir" size="65" maxlength="65" value="2013.16.B00.2.1.02.00.003.E006.21101.1.1.21.00000000000.B53.B08" /></td>
					</tr>
					<tr>	
						<td align="right">Monto irreductible:</td>
						<td align="left"><input type="text" name="montoIrreductibleDice" size="15" maxlength="20" value="$2,250,000" readOnly style="background-color: gray;"/></td>
						<td align="left"><input type="text" name="montoIrreductibleDebeDecir" size="15" maxlength="20" value="$2,250,000"/></td>						
					</tr>	
					<tr>	
						<td align="right">Monto Medio:</td>
						<td align="left"><input type="text" name="montoMedioDice" size="15" maxlength="20" value="$3,000,000" readOnly style="background-color: gray;"/></td>
						<td align="left"><input type="text" name="montoMedioDebeDecir" size="15" maxlength="20" value="$3,000,000" /></td>
					</tr>	
					<tr>
						<td align="right">Monto Optimo:</td>
						<td align="left"><input type="text" name="montoOptimoDice" size="15" maxlength="20" value="$3,250,000" readOnly style="background-color: gray;"/></td>
						<td align="left"><input type="text" name="montoOptimoDebeDecir" size="15" maxlength="20" value="$3,250,000"/></td>
					</tr>	
			</table>
		</td>
	</tr>
		<tr>
		<td>
			<table align="center">
					<tr>
						<td align="center">
							<input type="button" name="aceptar_btn" value="Aceptar" />
							<input type="button" name="aceptar_btn" value="Cancelar" />
						</td>
					</tr>	
			</table>
		</td>
	</tr>
</table>
	</div>
	</body>
</html>
