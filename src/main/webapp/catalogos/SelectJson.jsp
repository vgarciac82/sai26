<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@page language="java" contentType="application/json"%>
<%@page import="com.syc.gestion.documental.CatalogosBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>

<%
// version 

	boolean makeCommit = false;
	boolean blnManto = false;
	//String baseDatos = null;
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String strUsuario = u.getLogin();
	final String PRD_GESTION  = "3";
	final String TAB_CAT_AREAS = "13";
	String[][] strParamQ = null;
	String strFrom = "";
	String strWhere = "";
	String cCentroContable = "";

	String uUR = u.getU_UR();


	if(u.getPropiedades()!=null&&u.getPropiedades().containsKey("CCENTROCONTABLE")){
		cCentroContable= u.getPropiedad("CCENTROCONTABLE").getValor();
	}		
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean esSAIAlterno = "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") ) || "true".equals( cabl.getSystemSetting("SAI_FONDEN") );

	String strQuery  = "";
	String strError  = "";
	String strTabla  = request.getParameter("Tabla").toUpperCase();
	String strParam  = "";
	try{ 
		strParam = request.getParameter("Param") == null ? "": new String( request.getParameter("Param").getBytes("ISO-8859-1"), "ISO-8859-1"); 
		//System.out.println("strParam: "+strParam);
	}catch(Exception e){
		strParam = request.getParameter("Param");
	}	
	String strMaxReg = request.getParameter("MaxReg");
	String campos = request.getParameter("Campos") == null ? "" : new String( request.getParameter("Campos").getBytes("ISO-8859-1"), "UTF-8");
	
	String func = request.getParameter("Function");
	String strOrder = request.getParameter("Order");
	String strSelectFunc = request.getParameter("SelectFunc");
	
	String jndiName = "jdbc/gestion";
	if(strTabla.indexOf("_NNOMINA")>0){
		jndiName = "jdbc/nomina";
	}

	String jsonStringOrig = "";
	String jsonString = "";
	response.setContentType("text/x-json; charset=ISO-8859-1");
	  
		try
	{
		CatalogosBusinessLogic ObjC = new CatalogosBusinessLogic(jndiName);

		//Catalogo M_CAT_ESTATUS
		if (strTabla.equals("M_CAT_ESTATUS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " c_estatus, ";
			strQuery += " d_estatus ";
			strQuery += " FROM ";
			strQuery += " cg_cat_estatus WITH(NOLOCK) ";
		}


		if (strTabla.equals("M_TUECUENTASBANCARIAS")){
			
			strQuery =  " SELECT TOP " + strMaxReg;
				strQuery += " strUnidadEjecutora,strCentroContable,strRFC,strNombreBeneficiario,strClabe,strTipoCuenta ";
				strQuery += " FROM ";
				strQuery += " tUECuentasBancarias WITH(NOLOCK) ";
		}

		if (strTabla.equals("M_CAT_ALMACEN")){
			
			strQuery =  " SELECT TOP " + strMaxReg;
				strQuery += " cCentroContable,	nIdAlmacen,	cAlmacen ";
				strQuery += " FROM ";
				strQuery += " CAT_ALMACEN WITH(NOLOCK) ";
		}
		
		
		
		if (strTabla.equals("M_CAT_EJERCICIOFISCAL"))
		{
		
			strQuery =  " SELECT";
			strQuery += " '*' as aEjercicioFiscal, ";
			strQuery += " '-' as cActivo ";
			strQuery += " UNION ";
		
			strQuery +=  " SELECT ";
			strQuery +=  " CASE WHEN DATALENGTH(RTRIM(LTRIM(aEjercicioFiscal))) > 3 ";
			strQuery +=  " then aEjercicioFiscal ";
			strQuery +=  " else '2' + LTRIM(RTRIM(aEjercicioFiscal)) + '-PRUEBAS' ";
			strQuery +=  " end as aEjercicioFiscal,  ";
			strQuery +=  " cActivo as cActivo ";
			strQuery +=  " FROM  tejerciciofiscal WITH(NOLOCK) ";
			
			System.out.println("strQuery : "+strQuery);

		}

		//Catalogo Función 
		if (strTabla.equals("M_CAT_FUNCION"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cFuncion, ";
			strQuery += " dFuncion, ";
			strQuery += " cGrupoFuncional ";
			strQuery += " FROM ";
			strQuery += " tCatalogoFuncion WITH(NOLOCK) ";

		} 
		
		//Catalogo Sub-Función
		if (strTabla.equals("M_CAT_SUBFUNCION"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cSubFuncion, ";
			strQuery += " dSubFuncion, ";
			strQuery += " cFuncion, ";
			strQuery += " cGrupoFuncional ";			
			strQuery += " FROM ";
			strQuery += " tCatalogoSubFuncion WITH(NOLOCK) ";

		}
		
		//Catalogo Unidad Normativa
		if (strTabla.equals("M_CAT_UNORMATIVA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cUnidadResponsable as cUnidadNorativa , ";
			strQuery += " D_DESCRIPCION ";
			strQuery += " FROM ";
			strQuery += " tCatUnidadResponsable WITH(NOLOCK) ";
			strQuery += " WHERE nAlcance = 1";
			
			strParam = strParam.toUpperCase().replaceAll("CUNIDADNORATIVA LIKE '","cUnidadResponsable LIKE '%");
		}

		//Catalogo Unidad Ejecutora
		if (strTabla.equals("M_CAT_UEJECUTORA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cUnidadResponsable as cUnidadEjecutora, ";
			strQuery += " D_DESCRIPCION ";
			strQuery += " FROM ";
			strQuery += " tCatUnidadResponsable WITH(NOLOCK) ";

			strParam = strParam.toUpperCase().replaceAll("CUNIDADEJECUTORA LIKE '","cUnidadResponsable LIKE '%");
			
		}
		// TERMINA MODIFICACION PARA CATALOGO EP's
		
		if (strTabla.equals("M_CAT_ROLE"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " R_NOMBRE, R_DESCRIPCION";
			strQuery += " FROM ";
			strQuery += " cg_role WITH(NOLOCK) ";

		}


		if( strTabla.equals("M_TCATALOGOCENTROCONTABLE")){
			strQuery = "";
			strQuery += "SELECT	cCentroContable, ";
			strQuery += "		cDescripcion, ";
			strQuery += "		cCentroContableAbierto ";
			strQuery += "FROM	tCatalogoCentroContable WITH(NOLOCK) ";
		}
		//Catalogo CAT_DET_INSTRUCCION
		if (strTabla.equals("CAT_DET_INSTRUCCION"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_det_instruccion, ";
			strQuery += " di_descripcion ";
			strQuery += " FROM ";
			strQuery += " cat_det_instruccion WITH(NOLOCK) ";
		}
		//Verifica si ya se aplicï¿½ contablemente el documento de pago directo
		if (strTabla.equals("TPAGODIRECTODOCAPLICADOREAD"))
		{
			strQuery =  " SELECT case when isnull(nFolioPoliza,'') ='0' then '' else isnull(nFolioPoliza,'') end laPoliza, isnull(cDocumentoHaplicado,'') docAplicado ";
			strQuery += " FROM ";
			strQuery += " tpagodirectoencabezado WITH(NOLOCK) ";
		}

		if (strTabla.equals("TPAGOOBRADOCAPLICADOREAD"))
		{
			strQuery =  " SELECT case when isnull(nFolioPoliza,'') ='0' then '' else isnull(nFolioPoliza,'') end laPoliza, isnull(cDocumentoHaplicado,'') docAplicado ";
			strQuery += " FROM ";
			strQuery += " tpagoObraEncabezado WITH(NOLOCK) ";
		}

				
		if (strTabla.equals("COMPROMISONOMINA"))
		{
			strQuery =  " SELECT e.caNOcompromiso, e.cIdContrato, convert(varchar(10), e.faplicacion, 103) as faplicacion,"; 
			strQuery += "(select '$' + convert(varchar, isnull(sum(d.mImporte), 0), 1) from tCompromisoNominaDetalle d WITH(NOLOCK) where d.nFolioCompromisoNomina = e.nFolioCompromisoNomina) as mImporte, ";
			strQuery += "isnull(e.cDocumentoHaplicado, '') as docAplicado, e.nFolioCompromisoNomina, e.nEnviadoSICOP, e.nFolioSICOP ";
			strQuery += " FROM tCompromisoNominaEncabezado e WITH(NOLOCK) WHERE caNoCompromiso = CASE WHEN caNoCompromisoAmpliado IS NULL THEN caNoCompromiso WHEN caNoCompromisoAmpliado = '' THEN caNoCompromiso END ";
			strQuery += " AND ISNULL(cDocumentoHaplicado,'') NOT IN ('C','N')";
		}
		if (strTabla.equals("COMPROMISONOMINAAMPLIACION"))
		{
			strQuery =  " SELECT e.caNOcompromiso, e.cIdContrato, e.nFolioSICOP ";
			strQuery += " FROM tCompromisoNominaEncabezado e WITH(NOLOCK) WHERE caNoCompromiso=CASE WHEN caNoCompromisoAmpliado IS NULL THEN caNoCompromiso WHEN caNoCompromisoAmpliado = '' THEN caNoCompromiso END AND cDocumentoHaplicado='S' ";
		}
		if (strTabla.equals("COMPROMISONOMINAAMPLIACIONAPLICAR"))
		{
			strQuery =  " SELECT e.caNOcompromiso, e.caNoCompromisoAmpliado, e.cIdContrato, ISNULL(e.cDocumentoHaplicado, '') as docAplicado, e.nEnviadoSICOP, e.nFolioSICOP  ";
			strQuery += " FROM tCompromisoNominaEncabezado e WITH(NOLOCK) WHERE caNoCompromiso<>CASE WHEN caNoCompromisoAmpliado IS NULL THEN caNoCompromiso WHEN caNoCompromisoAmpliado = '' THEN caNoCompromiso ELSE caNoCompromisoAmpliado END AND ISNULL(cDocumentoHaplicado,'') NOT IN ('C','N')";
		}

				
		if (strTabla.equals("TCHEQUERESPONSABLE"))
		{
			strQuery =  " SELECT r.U_LOGIN, U_NOMBRE ";
			strQuery += " FROM tChequeResponsable r WITH(NOLOCK), CG_USUARIO u WITH(NOLOCK)  ";
			strQuery += " WHERE r.U_LOGIN = u.U_LOGIN and r.cCentroContable = '" + cCentroContable + "'";
		}

		if (strTabla.equals("TPAGOFEDDOCAPLICADOREAD"))
		{
			strQuery =  " SELECT case when isnull(nFolioPoliza,'') ='0' then '' else isnull(nFolioPoliza,'') end laPoliza, isnull(cDocumentoHaplicado,'') docAplicado ";
			strQuery += " FROM ";
			strQuery += " tpagoFederalizadoEncabezado WITH(NOLOCK) ";
		}

		if (strTabla.equals("TPAGODIVERSODOCAPLICADOREAD"))
		{
			strQuery =  " SELECT case when isnull(nFolioPoliza,'') ='0' then '' else isnull(nFolioPoliza,'') end laPoliza, isnull(cDocumentoHaplicado,'') docAplicado ";
			strQuery += " FROM ";
			strQuery += " tpagoDIVERSOEncabezado WITH(NOLOCK) ";
		}

		if (strTabla.equals("TPAGORELGDOCAPLICADOREAD"))
		{
			strQuery =  " SELECT case when isnull(nFolioPoliza,'') ='0' then '' else isnull(nFolioPoliza,'') end laPoliza, isnull(cDocumentoHaplicado,'') docAplicado ";
			strQuery += " FROM ";
			strQuery += " tRELACIONGASTOSencabezado WITH(NOLOCK) ";

		}

		if (strTabla.equals("TPAGONOMIDOCAPLICADOREAD"))
		{
			strQuery =  " SELECT case when isnull(nFolioPoliza,'') ='0' then '' else isnull(nFolioPoliza,'') end laPoliza, isnull(cDocumentoHaplicado,'') docAplicado ";
			strQuery += " FROM ";
			strQuery += " tNOMINAencabezado WITH(NOLOCK) ";
		}

		if (strTabla.equals("BUSQUEDAOPERACIONESAJENAS")){
	 		strQuery = " exec.dbo.OperacionesAjenas "+ campos;
	 		System.out.println("strQuery"+strQuery);

	 	}
	 	if (strTabla.equals("BUSQUEDAOPERACIONESAJENASIP")){
	 		strQuery = " exec.dbo.OperacionesAjenasIP "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	 	if (strTabla.equals("BUSQUEDAOPERACIONESAJENASRADICADO")){
	 		strQuery = " exec.dbo.OperacionesAjenasRadicado "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	 	if (strTabla.equals("BUSQUEDAOPERACIONESAJENASRELGASTO")){
	 		strQuery = " exec.dbo.OperacionesAjenasRELGASOC "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	 	if (strTabla.equals("REMANENTEEJERCICIOANTERIOR")){
	 		strQuery = " exec.dbo.sp_RemanenteEjercicioAnterior "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	 	if (strTabla.equals("REMANENTEEJERCICIOANTERIORPARTIDAS")){
	 		strQuery = " exec.dbo.sp_RemanenteEjercicioAnteriorPartidas "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	 	if (strTabla.equals("CONTRATOS_PLURIANUALES_CAP4")){
	 		strQuery = " exec.dbo.sp_mContratosPlurianualesCap4 "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	 	if (strTabla.equals("BUSQUEDAOADISMINUCIONDEVENGADO")){
	 		strQuery = " exec.dbo.OperacionesAjenasReduccionDev "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
		if (strTabla.equals("REPAJENAS")){
	 		strQuery = " exec.dbo.OperacionesAjenas_2 "+ campos;
	 		System.out.println("strQuery"+strQuery);

	 	}
		
		if (strTabla.equals("REPAJENASFALTANTES")){
	 		strQuery = " exec.dbo.OperacionesAjenas_SinAplic "+ campos;
	 		System.out.println("strQuery"+strQuery);

	 	}

		if (strTabla.equals("CANCELACOMPROMISOANUAL")){
	 		strQuery = " exec.dbo.sp_cancela_compromiso ";
	 		System.out.println("strQuery"+strQuery);

	 	}
		
		if (strTabla.equals("CANCELADEVENGADOANUAL")){
	 		strQuery = " exec.dbo.sp_cancela_devengado ";
	 		System.out.println("strQuery"+strQuery);

	 	}
		
		if (strTabla.equals("CONSULTAOPERAJENAS")){
	 		strQuery = " SELECT nFolioDoc,fAplicacion,cTipoDoc,Ep,mTotal,mEntero,mSobrante,caNoContrarrecibo,RFC, cmes FROM tOperAjenasDetalle WITH(NOLOCK) WHERE nFolioOperAjenas= "+ campos;
	 		System.out.println("strQuery"+strQuery);

	 	}
	 	
	 	if (strTabla.equals("CONSULTADISMINUCIONDEV")){
	 		strQuery = " SELECT caNoContrarrecibo, RFC, nFolioDoc, fAplicacion, cTipoDoc, Ep, CONVERT(varchar,mTotal,1), CONVERT(varchar,mImporteIvaHonorarios,1), CONVERT(varchar,mImporteIvaArrenda,1), CONVERT(varchar,mISRHonorarios,1),"
	 					+ " CONVERT(varchar,mISRArrenda,1), CONVERT(varchar,mImporteFlete4,1), CONVERT(varchar,mIMDT,1), CONVERT(varchar,mCNIC,1), CONVERT(varchar,mObra5,1), CONVERT(varchar,mPenalizacion,1), CONVERT(varchar,mRetImpuestoCedular,1),"
	 					+ " CONVERT(varchar,mImporteISRLaudos,1), cMes, cEvento, cCentroContable, CONVERT(varchar,mISROtros,1), CONVERT(varchar,mImporteIva6,1), CONVERT(varchar,mimporteISRResico,1) " 
	 				 	+ " FROM tDisminucionDevDetalle WITH (NOLOCK) WHERE nFolioDisminucionDev = "+ campos;
	 		System.out.println("strQuery"+strQuery);
	 	}
	
	if (strTabla.equals("CATAMF")){
 		strQuery = " exec.dbo.AcuerdosMF "+ campos;
 		System.out.println("strQuery"+strQuery);

 	}
	
	if (strTabla.equals("NUEVOCATAMF")){
 		strQuery = "select cUnidadResponsable, cNombreUr, mMontoD, mSaldo from tAcuerdosMFDetalle WITH(NOLOCK) WHERE cFolio = '0' and mMontoD = '0' and mSaldo = '0'";
 		System.out.println("strQuery"+strQuery);

 	}
	
	if (strTabla.equals("AMFCATALOGOUR")){
				
		strQuery =  " SELECT ";
		strQuery += " nClaveAMF, ";
		strQuery += " cFolio ";
		strQuery += " ,mMonto, fVigencia FROM ";
		strQuery += " tAcuerdosMFEncabezado WITH(NOLOCK) ";
		strQuery += " WHERE cFolio != '0' ";
 	}
    
	if (strTabla.equals("CADENASP")){
 		strQuery = " exec.dbo.sp_CadenasProductivas "+ campos;
 		System.out.println("strQuery"+strQuery);

 	}
	if (strTabla.equals("REPORTECADENASP")){
 		strQuery = " exec.dbo.sp_ReporteCadenasProductivas "+ campos;
 		System.out.println("strQuery"+strQuery);

 	}
	if (strTabla.equals("REPORTECIERRECXPPAGADAS")){
 		strQuery = " exec.dbo.sp_ReporteCierreCxPPagadas "+ campos;
 		System.out.println("strQuery"+strQuery);

 	}
		//Catalogo VCONTRATOCOMPROMISO
		if (strTabla.equals("VCONTRATOCOMPROMISO"))
		{
			
			boolean esAdmin = ( u != null && ( u.getRole("ADMIN_RECMAT") != null  || u.getRole("ADMIN") != null )  ); 
			
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cIDContrato, ";
			strQuery += " cIDRFC,  ";
			strQuery += " cNombre, ";
			strQuery += " mImporteTotal, ";
			strQuery += " cTCont, d_descripcion, ";
			strQuery += " cTipoContratoDiverso, ";
			strQuery += " cTipoAdjudicacion, ";
			strQuery += " cIdEntidadContable,nPorcIVAAplicable ";
			//VGC20150416 Se agrega campo que indica si es descentralizado
			strQuery += " ,nesdescentralizado, iEsAbierto";
			strQuery += " FROM VCONTRATOCOMPROMISO WITH(NOLOCK) ";
			
			//VGC20150615 Si no es admin ve los contratos de su CC y los descentralizados.
			if( !esAdmin )
				strWhere += " WHERE (  cIdEntidadContable ='" + cCentroContable + "' OR ( nEsDescentralizado = 1 )  )";
				
			strQuery += strWhere;

		}
		if (strTabla.equals("VCONTRATOCOMPROMISO_UE"))
		{
			boolean esAdmin = ( u != null && ( u.getRole("ADMIN_RECMAT") != null  || u.getRole("ADMIN") != null )  );
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cIDContrato, ";
			strQuery += " cIDRFC,  ";
			strQuery += " cNombre, ";
			strQuery += " mImporteTotal, ";
			strQuery += " d_descripcion, ";
			strQuery += " cTipoContratoDiverso, ";
			strQuery += " cTipoAdjudicacion, ";
			strQuery += " cTCont, cIdEntidadContable,cUnidadResponsable,isConvEjercicioAnt,FolioSicop, nFolioAutSICOP";
			strQuery += " FROM fn_mvContratoCompromisoRecepMat('"+strParam.substring(strParam.length()-5, strParam.length()-2)+"') ";
			if( !esAdmin ){
				strWhere += " WHERE cIdEntidadContable ='" + cCentroContable + "'";
			}
			strQuery += strWhere;
			System.out.println(strParam);
			System.out.println(strQuery);
			
		}
		if (strTabla.equals("VPARTIDASPRESUPCONT"))
		{
			
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " catSub.cIdSubPartida, ";
			strQuery += " catSub.cSubPartida,  ";
			strQuery += " catSub.cIdCapitulo, ";
			strQuery += " catSub.cIdSubPartida+' - '+catSub.cSubPartida cIdSubPartidaDesc, ";
			strQuery += " v.cIdContratoDefinitivo ";
			
			strQuery += " FROM mCatalogoSubPartida as catSub With(Nolock) "; 
			strQuery +=" inner join v_mPartidasPresupContratos as v  With(Nolock) on v.cIdCapitulo=catSub.cIdCapitulo ";
			strQuery +=" and catSub.cIdSubPartida<>v.cIdSubPartida";
			
			strQuery += strWhere;
			System.out.println(strParam);
			System.out.println(strQuery);
			
		}
		//Catalogo vSaldosAnualesVCONTRATOCOMPROMISO
		if (strTabla.equals("VSALDOSANUALES"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP = '82106') /*AND (aEjercicioFiscal = '2012')*/ ";
		}
		//Catalogo vSaldosAnualesVCONTRATOCOMPROMISO
				if (strTabla.equals("VSALDOSANUALESCOM"))
					{
					strQuery =  " SELECT TOP " + strMaxReg;
					strQuery += " nClaveCNA,  ";
					strQuery += " ClaveSIAFF, ";
					strQuery += " cUnidadNorativa, ";
					strQuery += " cUnidadEjecutora, ";
					strQuery += " MontoEnero, ";
					strQuery += " MontoFebrero, ";
					strQuery += " MontoMarzo, ";
					strQuery += " MontoAbril, ";
					strQuery += " MontoMayo, ";
					strQuery += " MontoJunio, ";
					strQuery += " MontoJulio, ";
					strQuery += " MontoAgosto, ";
					strQuery += " MontoSeptiembre, ";
					strQuery += " MontoOctubre, ";
					strQuery += " MontoNoviembre, ";
					strQuery += " MontoDiciembre, ";
					strQuery += " MontoAnual, ";
					strQuery += " cSubCuenta ";
					strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
					strQuery += " WHERE (nCuentaP = '82106') ";
				}
		//Catalogo vSaldosAnuales Cuenta 85206: Disponible Neto Ingreso
		if (strTabla.equals("VSALDOSANUALESINGRESO"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP = '82106')  ";
		}
		//Catalogo vSaldosAnualesVCONTRATOCOMPROMISO
		if (strTabla.equals("VSALDOSANUALES_DISP_RAD"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP in(case when cFuenteFinanciamiento='4' then '82106' else '82109' end))  /*AND (aEjercicioFiscal = '2012')*/ ";
		}
		//Catalogo vSaldosAnuales Cuenta 82106: FONDEN
		if (strTabla.equals("VSALDOSANUALESINGRESOFONDEN"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP = '82104')  ";
		}
		if (strTabla.equals("VSALDOSANUALESSPAN"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " S.nClaveCNA,  ";
			strQuery += " S.ClaveSIAFF, ";
			strQuery += " S.cUnidadNorativa, ";
			strQuery += " S.cUnidadEjecutora, ";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoEnero,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoEnero - isnull(A.mes01, 0),1) + '</span>' as MontoEnero,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoFebrero,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoFebrero - isnull(A.mes02, 0),1) + '</span>' as MontoFebrero,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoMarzo,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoMarzo - isnull(A.mes03, 0),1) + '</span>' as MontoMarzo,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoAbril,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoAbril - isnull(A.mes04, 0),1) + '</span>' as MontoAbril,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoMayo,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoMayo - isnull(A.mes05, 0),1) + '</span>' as MontoMayo,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoJunio,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoJunio - isnull(A.mes06, 0),1) + '</span>' as MontoJunio,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoJulio,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoJulio - isnull(A.mes07, 0),1) + '</span>' as MontoJulio,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoAgosto,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoAgosto - isnull(A.mes08, 0),1) + '</span>' as MontoAgosto,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoSeptiembre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoSeptiembre - isnull(A.mes09, 0),1) + '</span>' as MontoSeptiembre,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoOctubre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoOctubre - isnull(A.mes10, 0),1) + '</span>' as MontoOctubre,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoNoviembre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoNoviembre - isnull(A.mes11, 0),1) + '</span>' as MontoNoviembre,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoDiciembre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoDiciembre - isnull(A.mes12, 0),1) + '</span>' as MontoDiciembre,";
			strQuery += " '$' + CONVERT(varchar,MontoAnual,1)";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales S with (nolock)";
			strQuery += " LEFT OUTER JOIN (";
			strQuery += " 	SELECT (nIdClaveEgresos + '.' + ClaveInterna) ep, cEjercicio, cIdUnidadEjecutora, cIdSolicitud, sum(mes01) mes01, ";
			strQuery += " 	sum(mes02) mes02, sum(mes03)mes03, sum(mes04) mes04, sum(mes05) mes05, sum(mes06) mes06, sum(mes07) mes07, ";
			strQuery += " 	sum(mes08) mes08, sum(mes09) mes09, sum(mes10) mes10, sum(mes11) mes11, sum(mes12) mes12 ";
			strQuery += " 	FROM mSolicitudLineasApartado WITH(NOLOCK) ";
			strQuery += " 	WHERE " + strSelectFunc;
			strQuery += " 	GROUP BY (nIdClaveEgresos + '.' + ClaveInterna), nIdClaveEgresos, cEjercicio, cIdUnidadEjecutora, cIdSolicitud ";
			strQuery += " ) as A ON S.EP = A.ep";
			strQuery += " WHERE (nCuentaP = '82106') /*AND (aEjercicioFiscal = '2012')*/ ";
		}
		if (strTabla.equals("VSALDOSANUALESSPANRADICADO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " S.nClaveCNA,  ";
			strQuery += " S.ClaveSIAFF, ";
			strQuery += " S.cUnidadNorativa, ";
			strQuery += " S.cUnidadEjecutora, ";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoEnero,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoEnero - isnull(A.mes01, 0),1) + '</span>' as MontoEnero,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoFebrero,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoFebrero - isnull(A.mes02, 0),1) + '</span>' as MontoFebrero,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoMarzo,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoMarzo - isnull(A.mes03, 0),1) + '</span>' as MontoMarzo,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoAbril,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoAbril - isnull(A.mes04, 0),1) + '</span>' as MontoAbril,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoMayo,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoMayo - isnull(A.mes05, 0),1) + '</span>' as MontoMayo,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoJunio,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoJunio - isnull(A.mes06, 0),1) + '</span>' as MontoJunio,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoJulio,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoJulio - isnull(A.mes07, 0),1) + '</span>' as MontoJulio,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoAgosto,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoAgosto - isnull(A.mes08, 0),1) + '</span>' as MontoAgosto,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoSeptiembre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoSeptiembre - isnull(A.mes09, 0),1) + '</span>' as MontoSeptiembre,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoOctubre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoOctubre - isnull(A.mes10, 0),1) + '</span>' as MontoOctubre,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoNoviembre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoNoviembre - isnull(A.mes11, 0),1) + '</span>' as MontoNoviembre,";
			strQuery += " '$<span class=\"asignado\">' + CONVERT(varchar, S.MontoDiciembre,1) + '</span><span> / </span><span class=\"gastado\">$' + CONVERT(varchar, S.MontoDiciembre - isnull(A.mes12, 0),1) + '</span>' as MontoDiciembre,";
			strQuery += " '$' + CONVERT(varchar,MontoAnual,1)";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales S with (nolock)";
			strQuery += " LEFT OUTER JOIN (";
			strQuery += " 	SELECT (nIdClaveEgresos + '.' + ClaveInterna) ep, cEjercicio, cIdUnidadEjecutora, cIdSolicitud, sum(mes01) mes01, ";
			strQuery += " 	sum(mes02) mes02, sum(mes03)mes03, sum(mes04) mes04, sum(mes05) mes05, sum(mes06) mes06, sum(mes07) mes07, ";
			strQuery += " 	sum(mes08) mes08, sum(mes09) mes09, sum(mes10) mes10, sum(mes11) mes11, sum(mes12) mes12 ";
			strQuery += " 	FROM mSolicitudLineasApartado WITH(NOLOCK) ";
			strQuery += " 	WHERE " + strSelectFunc;
			strQuery += " 	GROUP BY (nIdClaveEgresos + '.' + ClaveInterna), nIdClaveEgresos, cEjercicio, cIdUnidadEjecutora, cIdSolicitud ";
			strQuery += " ) as A ON S.EP = A.ep";
			strQuery += " WHERE (nCuentaP in(case when s.cFuenteFinanciamiento='4' then '82106' else '82109' end)) /*AND (aEjercicioFiscal = '2012')*/ ";
		}
		if (strTabla.equals("VSALDOSAPARTADOSEPREQ"))
		{
			strQuery =  " SELECT ";//TOP " + strMaxReg;
			strQuery += " mSolicitudLineasApartado.nIdLineaSolicitud,";
			strQuery += " CONVERT(char, mes01) AS mes01,";
			strQuery += " CONVERT(char, mes02) AS mes02,";
			strQuery += " CONVERT(char, mes03) AS mes03,";
			strQuery += " CONVERT(char, mes04) AS mes04,";
			strQuery += " CONVERT(char, mes05) AS mes05,";
			strQuery += " CONVERT(char, mes06) AS mes06,";
			strQuery += " CONVERT(char, mes07) AS mes07,";
			strQuery += " CONVERT(char, mes08) AS mes08,";
			strQuery += " CONVERT(char, mes09) AS mes09,";
			strQuery += " CONVERT(char, mes10) AS mes10,";
			strQuery += " CONVERT(char, mes11) AS mes11,";
			strQuery += " CONVERT(char, mes12) AS mes12,";
			strQuery += " CONVERT(char, isnull(O.suma, 0)) AS otras,";
			strQuery += " CONVERT(char, isnull((mes01 + mes02 + mes03 + mes04 + mes05+ mes06 + mes07 + mes08 + mes09 + mes10 + mes11 + mes12), 0)) AS esta";
			strQuery += " FROM mSolicitudLineasApartado WITH(NOLOCK) ";
			strQuery += " LEFT OUTER JOIN dbo.fn_mSolicitudApartadoOtras(" + strSelectFunc + ")";
			strQuery += " as O ON mSolicitudLineasApartado.nIdLineaSolicitud = O.nIdLineaSolicitud";
		}
		if (strTabla.equals("VSALDOSANUALESFORMAT"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoEnero,0) AS MONEY),1) MontoEnero, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoFebrero,0) AS MONEY),1)MontoFebrero, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoMarzo,0) AS MONEY),1) MontoMarzo, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoAbril,0) AS MONEY),1)MontoAbril, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoMayo,0) AS MONEY),1) MontoMayo, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoJunio,0) AS MONEY),1) MontoJunio, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoJulio,0) AS MONEY),1) MontoJulio, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoAgosto,0) AS MONEY),1) MontoAgosto, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoSeptiembre,0) AS MONEY),1) MontoSeptiembre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoOctubre,0) AS MONEY),1) MontoOctubre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoNoviembre,0) AS MONEY),1) MontoNoviembre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoDiciembre,0) AS MONEY),1) MontoDiciembre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoAnual,0) AS MONEY),1) MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP = '82106') ";
		}
		if (strTabla.equals("VSALDOSANUALESFORMATCTADISP"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoEnero,0) AS MONEY),1) MontoEnero, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoFebrero,0) AS MONEY),1)MontoFebrero, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoMarzo,0) AS MONEY),1) MontoMarzo, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoAbril,0) AS MONEY),1)MontoAbril, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoMayo,0) AS MONEY),1) MontoMayo, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoJunio,0) AS MONEY),1) MontoJunio, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoJulio,0) AS MONEY),1) MontoJulio, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoAgosto,0) AS MONEY),1) MontoAgosto, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoSeptiembre,0) AS MONEY),1) MontoSeptiembre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoOctubre,0) AS MONEY),1) MontoOctubre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoNoviembre,0) AS MONEY),1) MontoNoviembre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoDiciembre,0) AS MONEY),1) MontoDiciembre, ";
			strQuery += " '$'+ CONVERT(VARCHAR,CAST(ISNULL(MontoAnual,0) AS MONEY),1) MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales as s WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP in(case when s.cFuenteFinanciamiento='4' then '82106' else '82109' end)) ";
		}
		if ( strTabla.equals("PIVOTAPARTADO") ){
		
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " ClaveSIAFF + '.' + ClaveInterna EP,  ";
			strQuery += " '$ ' + convert(varchar, Monto) mImporte, ";
			strQuery += " convert(int, right(Meses,2)) cMes ";
			strQuery += " FROM (SELECT cIdSolicitud, ClaveSIAFF, ClaveInterna, ";
			strQuery += " MontoEnero as mes01, MontoFebrero as mes02, MontoMarzo as mes03, ";
			strQuery += " MontoAbril as mes04, MontoMayo as mes05, MontoJunio as mes06, ";
			strQuery += " MontoJulio as mes07, MontoAgosto as mes08, MontoSeptiembre as mes09, ";
			strQuery += " MontoOctubre as mes10, MontoNoviembre as mes11, MontoDiciembre as mes12 ";
			strQuery += " FROM vPresupuestoSolicitudEP WITH(NOLOCK) WHERE " + strSelectFunc;
			strQuery += " ) Main UNPIVOT (Monto FOR Meses IN (mes01, mes02, mes03, mes04, mes05, mes06, ";
			strQuery += " mes07, mes08, mes09, mes10, mes11, mes12) ) Mes";
			strQuery += " WHERE Monto > 0 ";
			
			System.out.println("testtt: " + strQuery);
		}
		
		if (strTabla.equals("VSALDOSANUALESCUENTA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta, ";
			strQuery += " nCuentaP ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP = '82106')";
		}
		
		if (strTabla.equals("VSALDOSANUALESDIVERSOS"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnuales WITH(NOLOCK) ";
			strQuery += " WHERE (nCuentaP = '82106') /*AND (aEjercicioFiscal = '2012')*/ ";
			strQuery += " AND   substring(cSubCuenta, 32,5) LIKE  '2%' ";
			strQuery += " OR   substring(cSubCuenta, 32,5) LIKE  '3%' ";
			strQuery += " OR   substring(cSubCuenta, 32,5) LIKE  '5%' ";
		}

		if (strTabla.equals("VSALDOSANUALESOBRA"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " nClaveCNA,  ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " cUnidadNorativa, ";
			strQuery += " cUnidadEjecutora, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " MontoAnual, ";
			strQuery += " cSubCuenta ";
			strQuery += " FROM vSaldosAnualesObra WITH(NOLOCK) ";

		}

		
		if (strTabla.equals("VSALDOSRADICADO"))
			{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " Folio, ";
			strQuery += " ClaveSIAFF, ";
			strQuery += " MontoEnero, ";
			strQuery += " MontoFebrero, ";
			strQuery += " MontoMarzo, ";
			strQuery += " MontoAbril, ";
			strQuery += " MontoMayo, ";
			strQuery += " MontoJunio, ";
			strQuery += " MontoJulio, ";
			strQuery += " MontoAgosto, ";
			strQuery += " MontoSeptiembre, ";
			strQuery += " MontoOctubre, ";
			strQuery += " MontoNoviembre, ";
			strQuery += " MontoDiciembre, ";
			strQuery += " Acumulado ";
			strQuery += " FROM vSaldosRadicado WITH(NOLOCK) ";
		}	
		
		//Catalogo TBENEFICIARIO OBRA Y DIVERSOS
		if (strTabla.equals("PCATALOGORFC"))
		{
			
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cIDRFC, ";
			strQuery += " cnombre ";
			strQuery += " ,DCD_TBEN, DCD_CBEN FROM ";
			strQuery += " v_PCATALOGORFC WITH(NOLOCK) ";
			
		}

		//Catalogo CAT_TIPO_DOCUMENTO
		if (strTabla.equals("CAT_TIPO_DOCUMENTO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " td_id, ";
			strQuery += " td_descripcion ";
			strQuery += " FROM ";
			strQuery += " cat_tipo_documento WITH(NOLOCK) ";
		}

		//Catalogo TBENEFICIARIO
		if (strTabla.equals("TBENEFICIARIO"))
		{
		
			strQuery =  " SELECT TOP " + strMaxReg + "  * FROM ( "; 
			strQuery +=  " SELECT ";
			strQuery += " cIDRFC, ";
			strQuery += " cnombre, DCD_TBEN, DCD_CBEN, cIdTipoPersonaRFC ";
			strQuery += "  FROM ";
			strQuery += " v_PCATALOGORFC WITH(NOLOCK) ";
			if( u.getPropiedad("ROL_CONTABLE") != null && "ADMIN_CONTABILIDAD".equals(  u.getPropiedad("ROL_CONTABLE").getValor() )  )
				strQuery += " UNION SELECT * FROM (SELECT scodigoentidad  AS cidRFC, sbeneficiario,  2 dcd_tben, ssicop, 2 cIdTipoPersonaRFC from tBeneficiarioCapituloMil with(nolock)  ) as capMil ";
			strQuery += ") AS tbl ";
		}
		
		if (strTabla.equals("TBENEFICIARIO_ACTIVO"))
		{
		
			strQuery =  " SELECT TOP " + strMaxReg + "  * FROM ( "; 
			strQuery += " SELECT ";
			strQuery += " cIDRFC cIDRFC_contrato, ";
			strQuery += " cnombre, DCD_TBEN, DCD_CBEN, cIdTipoPersonaRFC ";
			strQuery += "  FROM v_PCATALOGORFC_ACTIVO WITH(NOLOCK) ";
			strQuery += ") AS tbl WHERE cIdTipoPersonaRFC IN (1, 2, 7)";
		}

		//Catalogo TBENEFICIARIO1000
		if (strTabla.equals("TBENEFICIARIO1000"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " sCodigoEntidad, sbeneficiario, nCuenta, sSicop ";
			strQuery += "  FROM tBeneficiarioCapituloMil WITH(NOLOCK) ";
		}

		// Catalogo Personalizada/Copia para/Turnado
		if (strTabla.equals("PERSONALIZADA")	||
			strTabla.equals("COPIAPARA")		||
			strTabla.equals("TURNADO"))
		{
			// Consulta al catalogo considerando Cobertura
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u.u_login ";
			strQuery += (strTabla.equals("PERSONALIZADA")?" ,e.salutacion ":"");
			strQuery += (strTabla.equals("COPIAPARA")?" ,e.salutacion ":"");
			strQuery += " ,e.nombrecompl ";
			strQuery += " ,p.pto_nombre ";
			strQuery += " ,a.d_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_usuario U WITH(NOLOCK), ";
			strQuery += " vimx_empleado E WITH(NOLOCK), ";
			strQuery += " cat_puestos P WITH(NOLOCK), ";
			strQuery += " cg_cat_areas A WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = e.ce_os_responsable ";
			strQuery += " AND e.id_area = a.id_area ";
			strQuery += " AND e.id_puesto = p.id_puesto ";

			strParam = strParam.replace("E.NOMBRECOMPL LIKE '", "E.NOMBRECOMPL LIKE '%");
		}

		// Catalogo Remitente Interno
		if (strTabla.equals("REMINTERNO"))
		{
			// Consulta al catalogo considerando Cobertura
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u.u_login ";
			strQuery += (strTabla.equals("PERSONALIZADA")?" ,e.salutacion ":"");
			strQuery += (strTabla.equals("COPIAPARA")?" ,e.salutacion ":"");
			strQuery += " ,e.nombrecompl ";
			strQuery += " ,p.pto_nombre ";
			strQuery += " ,a.d_descripcion ";
			strQuery += " ,a.id_area ";
			strQuery += " FROM ";
			strQuery += " cg_usuario U WITH(NOLOCK), ";
			strQuery += " vimx_empleado E WITH(NOLOCK), ";
			strQuery += " cat_puestos P WITH(NOLOCK), ";
			strQuery += " cg_cat_areas A WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = e.ce_os_responsable ";
			strQuery += " AND e.id_area = a.id_area ";
			strQuery += " AND e.id_puesto = p.id_puesto ";

			strParam = strParam.replace("E.NOMBRECOMPL LIKE '", "E.NOMBRECOMPL LIKE '%");
		}

			// Catalogo Remitente Interno para Reportes
		if (strTabla.equals("REMINTERNO3"))
		{
			strQuery =  " select distinct u.U_LOGIN, u.U_NOMBRE ";
			strQuery += " from CG_USUARIO_GRUPO ug ";
			strQuery += " inner join cg_usuario u on ug.U_LOGIN = u.U_LOGIN ";
			strQuery += " where ug.G_NOMBRE IN (select o_responsable from CG_OPERACION where ID_TC = 3 and O_RESPONSABLE <> 'CONSULTA_ADECUACIONES') ";
			strQuery += " order by U_LOGIN ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = " " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}
		}

			// Catalogo Remitente Interno para Reportes
		if (strTabla.equals("RPT_REMINTERNO"))
		{
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = " " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			System.out.println("Fin de busqueda de cobertura, strFrom="+strFrom+", strWhere="+strWhere);

			// Consulta al catalogo considerando Cobertura
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u.u_login ";
			strQuery += (strTabla.equals("PERSONALIZADA")?" ,e.salutacion ":"");
			strQuery += (strTabla.equals("COPIAPARA")?" ,e.salutacion ":"");
			strQuery += " ,e.nombrecompl ";
			strQuery += " ,p.pto_nombre ";
			strQuery += " ,a.d_descripcion ";
			strQuery += " ,a.id_area ";
			strQuery += " FROM ";
			strQuery += " cg_usuario U WITH(NOLOCK), ";
			strQuery += " vimx_empleado E WITH(NOLOCK), ";
			strQuery += " cat_puestos P WITH(NOLOCK), ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " u.u_login = e.ce_os_responsable ";
			strQuery += " AND e.id_area = a.id_area ";
			strQuery += " AND e.id_puesto = p.id_puesto ";
			strQuery += strWhere;

			strParam = strParam.replace("E.NOMBRECOMPL LIKE '", "E.NOMBRECOMPL LIKE '%");
		}

		//Catalogo Remitentes Externos, para el registro
		if (strTabla.equals("M_CAT_REM_EXTERNO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += "       r.re_nombre           ";
			strQuery += ",      r.re_cargo            ";
			strQuery += ",      p.pro_descripcion     ";
			strQuery += ",      tp.tp_descripcion     ";
			strQuery += ",      e.edo_nombre          ";
			strQuery += ",      m.mpo_nombre          ";
			strQuery += ",      r.re_localidad        ";
			strQuery += ",      r.re_direccion        ";
			strQuery += ",      r.id_rem_externo      ";
			strQuery += ",      p.id_procedencia      ";
			strQuery += ",      p.id_tipo_procedencia ";
			strQuery += ",      e.id_estado           ";
			strQuery += ",      m.id_municipio        ";
			strQuery += " FROM                         ";
			strQuery += "       cat_rem_externo R WITH(NOLOCK)     ";
			strQuery += " INNER  JOIN cat_procedencia P WITH(NOLOCK) ";
			strQuery += " ON     r.id_procedencia = p.id_procedencia ";
			strQuery += " INNER  JOIN cg_tipo_procedencia TP WITH(NOLOCK) ";
			strQuery += " ON     tp.id_tipo_procedencia = p.id_tipo_procedencia ";
			strQuery += " LEFT   OUTER JOIN cat_estados E WITH(NOLOCK) ";
			strQuery += " ON     r.id_estado      = e.id_estado ";
			strQuery += " LEFT   OUTER JOIN cat_municipio M WITH(NOLOCK) ";
			strQuery += " ON     r.id_municipio   = m.id_municipio ";

			strParam = strParam.replaceAll("R.RE_NOMBRE LIKE '","R.RE_NOMBRE LIKE '%");
			strParam = strParam.replaceAll("R.RE_CARGO LIKE '","R.RE_CARGO LIKE '%");
			strParam = strParam.replaceAll("R.RE_DIRECCION LIKE '","R.RE_DIRECCION LIKE '%");
		}


		//Catalogo Remitentes Externos, para la consulta por Asunto.
		if (strTabla.equals("CAT_REM_EXTERNO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += "       r.re_nombre           ";
			strQuery += ",      r.re_cargo            ";
			strQuery += ",      p.pro_descripcion     ";
			strQuery += ",      tp.tp_descripcion     ";
			strQuery += ",      e.edo_nombre          ";
			strQuery += ",      m.mpo_nombre          ";
			strQuery += ",      r.re_localidad        ";
			strQuery += ",      r.re_direccion        ";
			strQuery += ",      r.id_rem_externo      ";
			strQuery += ",      p.id_procedencia      ";
			strQuery += ",      p.id_tipo_procedencia ";
			strQuery += ",      e.id_estado           ";
			strQuery += ",      m.id_municipio        ";
			strQuery += " FROM                         ";
			strQuery += "       cat_rem_externo R WITH(NOLOCK)     ";
			strQuery += " INNER  JOIN cat_procedencia P WITH(NOLOCK) ";
			strQuery += " ON     r.id_procedencia = p.id_procedencia ";
			strQuery += " INNER  JOIN cg_tipo_procedencia TP WITH(NOLOCK) ";
			strQuery += " ON     tp.id_tipo_procedencia = p.id_tipo_procedencia ";
			strQuery += " LEFT   OUTER JOIN cat_estados E WITH(NOLOCK) ";
			strQuery += " ON     r.id_estado      = e.id_estado ";
			strQuery += " LEFT   OUTER JOIN cat_municipio M WITH(NOLOCK) ";
			strQuery += " ON     r.id_municipio   = m.id_municipio ";

			strParam = strParam.replaceAll("R.RE_NOMBRE LIKE '","R.RE_NOMBRE LIKE '%");
			strParam = strParam.replaceAll("R.RE_CARGO LIKE '","R.RE_CARGO LIKE '%");
			strParam = strParam.replaceAll("R.RE_DIRECCION LIKE '","R.RE_DIRECCION LIKE '%");
		}

		//SubCatalogo CAT_PROCEDENCIA
		if (strTabla.equals("CAT_PROCEDENCIA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += "        p.pro_descripcion     ";
			strQuery += " ,      p.pro_abreviatura     ";
			strQuery += " ,      tp.tp_descripcion     ";
			strQuery += " ,      p.id_procedencia       ";
			strQuery += " ,      p.id_tipo_procedencia ";
			strQuery += " FROM                         ";
			strQuery += "       cat_procedencia P WITH(NOLOCK)      ";
			strQuery += " INNER  JOIN cg_tipo_procedencia TP WITH(NOLOCK) ";
			strQuery += " ON     tp.id_tipo_procedencia = p.id_tipo_procedencia ";
		}

		//Catalogo CG_TIPO_PROCEDENCIA
		if (strTabla.equals("CG_TIPO_PROCEDENCIA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += "       id_tipo_procedencia ";
			strQuery += ",      tp_descripcion      ";
			strQuery += " FROM                       ";
			strQuery += "       cg_tipo_procedencia WITH(NOLOCK) ";
		}

		//SubCatalogo CAT_ESTADOS
		if (strTabla.equals("CAT_ESTADOS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			//strQuery += " id_estado, ";
			strQuery += " edo_nombre, ";
			strQuery += " edo_abreviatura, ";
			strQuery += " id_estado ";
			strQuery += " FROM ";
			strQuery += " cat_estados WITH(NOLOCK) ";
		}

		//SubCatalogo para Desembolsos, del catalogo de TipodeCambio
		if (strTabla.equals("D_HELPCATAGENTEFINANCIERO"))
		{
			strQuery =  " SELECT TOP 1000 ";
			strQuery += " ID_AgenteFinanciero, ";
			strQuery += " AgenteFinancieroCorto ";
			strQuery += " FROM ";
			strQuery += " dCat_Agente_Financiero WITH(NOLOCK) ";
		}

		//SubCatalogo CAT_MUNICIPIO
		if (strTabla.equals("CAT_MUNICIPIO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			//strQuery += " e.id_estado, ";
			strQuery += " e.edo_nombre, ";
			strQuery += " m.mpo_nombre, ";
			strQuery += " e.id_estado, ";
			strQuery += " m.id_municipio ";
			strQuery += " FROM ";
			strQuery += " cat_estados E WITH(NOLOCK), ";
			strQuery += " cat_municipio M WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " e.id_estado = m.id_estado ";
			//strQuery += " ORDER BY e.id_estado, m.id_estado";
		}

		//SubCatalogo IMXEXPEDIENTES
		if (strTabla.equals("IMXEXPEDIENTES"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " FOLIO, ";
			strQuery += " REFERENCIA, ";
			strQuery += " ALCANCE, ";
			strQuery += " ANTECEDENTE, ";
			strQuery += " ASUNTO, ";
			strQuery += " convert(varchar,DPC_F_REGISTRO,103) as DPC_F_REGISTRO, ";
			strQuery += " REMINUNOMBRE, ";
			strQuery += " REMINPTONOM, ";
			strQuery += " REMINDDESC, ";
			strQuery += " RENOMBRE, ";
			strQuery += " RECARGO, ";
			strQuery += " REPROCEDENCIA ";

			strQuery += " FROM ";
			strQuery += " IMXEXPEDIENTES WITH(NOLOCK) ";
		}

		//SubCatalogo VIMX_REPORTES
		if (strTabla.equals("VIMX_REPORTES"))
		{
			strQuery =  " SELECT DISTINCT TOP " + strMaxReg;
			strQuery += " vimx.FOLIO, ";
			strQuery += " vimx.REFERENCIA, ";
			strQuery += " vimx.N_CONTROL, ";
			strQuery += " CASE WHEN vimx.CERRADO= 'N' THEN 'PENDIENTE' ELSE 'CONCLUIDO' END AS ESTATUS, ";
			strQuery += " convert(varchar,DPC_F_REGISTRO,103) as DPC_F_REGISTRO, ";
			strQuery += " vimx.ASUNTO, ";
			strQuery += " REMINUNOMBRE, ";
			strQuery += " REMINPTONOM, ";
			strQuery += " REMINDDESC, ";
			strQuery += " RENOMBRE, ";
			strQuery += " RECARGO, ";
			strQuery += " REPROCEDENCIA ";
			strQuery += " FROM ";
			strQuery += " IMXEXPEDIENTES imx WITH(NOLOCK) INNER JOIN VIMX_REPORTES vimx WITH(NOLOCK) ";
			strQuery += " ON imx.FOLIO = vimx.FOLIO ";
		}

		//SubCatalogo CG_CAT_AREAS
		if (strTabla.equals("CG_CAT_AREAS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_area, ";
			strQuery += " d_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_cat_areas WITH(NOLOCK) ";
		}

		//Catalogo CG_CAT_AREAS
		if (strTabla.equals("VIMX_AREAS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_area, ";
			strQuery += " d_descripcion ";
			strQuery += " FROM ";
			strQuery += " vimx_areas WITH(NOLOCK) ";

		}

		// Catalogo AREAS con cobertura para reporte
		if (strTabla.equals("CG_CAT_AREAS_COB"))
		{
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
// 				strFrom = " " + strParamQ[0][0] + " ";
// 				strWhere = strParamQ[0][1] + " ";
// 				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			System.out.println("Fin de busqueda de cobertura, strFrom="+strFrom+", strWhere="+strWhere);
			
			strQuery =  " SELECT DISTINCT TOP " + strMaxReg;
			strQuery += " a.id_area, ";
			strQuery += " a.d_descripcion, ";
			strQuery += " ur.cUnidadResponsable ";
			strQuery += " FROM ";
			strQuery += " CG_CAT_AREAS a WITH(NOLOCK), ";
			strQuery += " tCatalogoUnidadResponsable ur WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " a.ID_AREA = ur.ID_AREA ";
			strQuery += strWhere;
			

			System.out.println("Fin de busqueda Consulta Areas");
		}


		// Catalogo AREAS ESTRUCTURA con cobertura para reporte
		if (strTabla.equals("CG_CAT_AREAS_COB_ESTRUC"))
		{
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			System.out.println("Fin de busqueda de cobertura, strFrom="+strFrom+", strWhere="+strWhere);

			strQuery =  " SELECT DISTINCT TOP " + strMaxReg;
			strQuery += " a.id_area, ";
			strQuery += " a.d_descripcion, ";
			strQuery += " a.area_estructura ";
			strQuery += " FROM ";
			strQuery += " cg_cat_empleado e WITH(NOLOCK), ";
			strQuery += " cg_usuario u WITH(NOLOCK) ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " u.u_login = e.ce_os_responsable ";
			strQuery += " AND e.id_area = a.id_area ";
			strQuery += strWhere;

			System.out.println("Fin de busqueda Consulta Areas");
		}

		// Consulta de USUARIOS entre usuarios
		if (strTabla.equals("USRUSR"))
		{
			strQuery = "SELECT ";
			strQuery += "co_from_clause, ";
			strQuery += "co_where_clause ";
			strQuery += "FROM ";
			strQuery += "cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += "cg_cobertura c WITH(NOLOCK) ";
			strQuery += "WHERE ";
			strQuery += "u.u_login = '" + strUsuario + "' ";
			strQuery += "AND u.id_tabla = c.id_tabla ";
			strQuery += "AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += "AND u.id_cobertura = c.id_cobertura ";
			strQuery += "AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u.u_login, ";
			strQuery += " e.nombrecompl, ";
			strQuery += " a.d_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_usuario U WITH(NOLOCK), ";
			strQuery += " vimx_empleado E WITH(NOLOCK) ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " u.u_login = e.ce_os_responsable ";
			strQuery += " AND e.id_area = a.id_area ";
			strQuery += strWhere;

			strParam = strParam.replace("E.NOMBRECOMPL LIKE '", "E.NOMBRECOMPL LIKE '%");
		}

		// Consulta de USUARIOS entre usuarios sin area
		if (strTabla.equals("USRUSRSA"))
		{
		// Select para cobertura de usuarios
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u.u_login, ";
			strQuery += " e.nombrecompl ";
			strQuery += " FROM ";
			strQuery += " cg_usuario U WITH(NOLOCK), ";
			strQuery += " vimx_empleado E WITH(NOLOCK) ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " u.u_login = e.ce_os_responsable ";
			strQuery += " AND e.id_area = a.id_area ";
			strQuery += strWhere;

			strParam = strParam.replace("E.NOMBRECOMPL LIKE '", "E.NOMBRECOMPL LIKE '%");
		}

		//SubCatalogo EMPLEADOS
		if (strTabla.equals("VIMX_EMPLEADO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " ce_os_responsable, ";
			strQuery += " nombrecompl ";
			strQuery += " FROM ";
			strQuery += " vimx_empleado WITH(NOLOCK) ";

			strParam = strParam.replace("NOMBRECOMPL LIKE '", "NOMBRECOMPL LIKE '%");
		}

///////////////////////////////////////////////////////////////////////////////////////////////
//CATALOGOS DE MANTENIMIENTO

		//Catalogo CAT_FIRMANTE
		if (strTabla.equals("M_CAT_FIRMANTE"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " 	(ca.D_DESCRIPCION) as MantoPArea,  ";
			strQuery += " 	(ce.CE_AP_PATERNO + ' ' + ce.CE_AP_MATERNO + ' ' + ce.CE_NOMBRE_COMPLETO) as NombreCompl1, ";
			strQuery += " f.U_LOGIN, ";
			strQuery += " f.ID_AREA, ";
			strQuery += " f.ID_FIRMANTE ";
			strQuery += " FROM ";
			strQuery += " cat_firmante f WITH(NOLOCK) ";
			strQuery += " LEFT OUTER JOIN CG_CAT_AREAS    ca WITH(NOLOCK) ON (ca.ID_AREA           = f.ID_AREA) ";
			strQuery += " LEFT OUTER JOIN CG_CAT_EMPLEADO ce WITH(NOLOCK) ON (ce.CE_OS_RESPONSABLE = f.U_LOGIN) ";

			strParam = strParam.replaceAll("NombreCompl1", "(ce.CE_AP_PATERNO + ' ' + ce.CE_AP_MATERNO + ' ' + ce.CE_NOMBRE_COMPLETO)");
		}
		//Catalogo CAT_DET_INSTRUCCION
		if (strTabla.equals("M_CAT_DET_INSTRUCCION"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " di_descripcion, ";
			strQuery += " id_det_instruccion ";
			strQuery += " FROM ";
			strQuery += " cat_det_instruccion WITH(NOLOCK) ";

			strParam = strParam.replaceAll("DI_DESCRIPCION LIKE '", "DI_DESCRIPCION LIKE '%");
		}

		//Catalogo CAT_ESTADOS
		if (strTabla.equals("M_CAT_ESTADOS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_estado, ";
			strQuery += " edo_nombre, ";
			strQuery += " edo_abreviatura ";
			strQuery += " FROM ";
			strQuery += " cat_estados WITH(NOLOCK) ";
		}

		//Catalogo CAT_LD_USR
		if (strTabla.equals("M_CAT_LD_USR"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u_login, ";
			strQuery += " id_ldistribucion ";
			strQuery += " FROM ";
			strQuery += " cat_ld_usr WITH(NOLOCK) ";
		}

		//Catalogo CAT_LDISTRIBUCION
		if (strTabla.equals("M_CAT_LDISTRIBUCION"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_ldistribucion, ";
			strQuery += " ld_nombre ";
			strQuery += " FROM ";
			strQuery += " cat_ldistribucion WITH(NOLOCK) ";
		}

		//Catalogo CAT_MUNICIPIO
		if (strTabla.equals("M_CAT_MUNICIPIO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_municipio, ";
			strQuery += " id_estado, ";
			strQuery += " mpo_nombre, ";
			strQuery += " mpo_abreviatura ";
			strQuery += " FROM ";
			strQuery += " cat_municipio WITH(NOLOCK) ";
		}

		//Catalogo CAT_PROCEDENCIA
		if (strTabla.equals("M_CAT_PROCEDENCIA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " p.pro_descripcion, ";
			strQuery += " tp.tp_descripcion, ";
			strQuery += " p.pro_abreviatura, ";
			strQuery += " p.id_procedencia, ";
			strQuery += " p.id_tipo_procedencia ";
			strQuery += " FROM ";
			strQuery += " cat_procedencia p WITH(NOLOCK), ";
			strQuery += " cg_tipo_procedencia tp WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " p.id_tipo_procedencia = tp.id_tipo_procedencia ";

			strParam = strParam.replaceAll("P.PRO_DESCRIPCION LIKE '","P.PRO_DESCRIPCION LIKE '%");
		}

		//Catalogo CG_TIPO_PROCEDENCIA
		if (strTabla.equals("M_CG_TIPO_PROCEDENCIA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_tipo_procedencia, ";
			strQuery += " tp_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_tipo_procedencia WITH(NOLOCK) ";
		}

		//Catalogo CAT_PUESTOS
		if (strTabla.equals("M_CAT_PUESTOS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " pto_nombre, ";
			strQuery += " id_puesto ";
			strQuery += " FROM ";
			strQuery += " cat_puestos WITH(NOLOCK) ";

			strParam = strParam.replaceAll("PTO_NOMBRE LIKE '","PTO_NOMBRE LIKE '%");
		}

		//Catalogo CAT_TIPO_DOCUMENTO
		if (strTabla.equals("M_CAT_TIPO_DOCUMENTO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " td_id, ";
			strQuery += " td_descripcion ";
			strQuery += " FROM ";
			strQuery += " cat_tipo_documento WITH(NOLOCK) ";
		}

		//Catalogo CG_CAT_PRIORIDAD
		if (strTabla.equals("M_CG_CAT_PRIORIDAD"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id, ";
			strQuery += " descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_cat_prioridad WITH(NOLOCK) ";
		}

		//Catalogo CG_CAT_REMITENTE (ï¿½NO SE USA?)
		if (strTabla.equals("M_CG_CAT_REMITENTE"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_remitente, ";
			strQuery += " d_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_cat_remitente WITH(NOLOCK) ";
		}

		//Catalogo CG_CAT_REMITENTE_AREA
		if (strTabla.equals("M_CG_CAT_REMITENTE_AREA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " cra_id_area, ";
			strQuery += " cra_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_cat_remitente_area WITH(NOLOCK) ";
		}

		//Catalogo CG_CAT_REMITENTE_PERSONA (ï¿½NO SE USA?)
		if (strTabla.equals("M_CG_CAT_REMITENTE_PERSONA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " crp_id_persona, ";
			strQuery += " crp_nombres, ";
			strQuery += " crp_apaterno, ";
			strQuery += " crp_amaterno, ";
			strQuery += " crp_id_area, ";
			strQuery += " crp_puesto ";
			strQuery += " FROM ";
			strQuery += " cg_cat_remitente_persona WITH(NOLOCK) ";
		}

		//Catalogo CG_CAT_REMITENTE_PERSONA (ï¿½NO SE USA?)
		if (strTabla.equals("CATALOGOCLABES_UR_CC"))
		{
			strQuery  = " SELECT TOP " + strMaxReg + " [id] ";
			strQuery += "     ,[cUnidadResponsable]+'h' ";
			strQuery += "     ,[cEntidadContable] ";
			strQuery += "     ,[cIdRFC] ";
			strQuery += "     ,[cBeneficiarioCuenta] ";
			strQuery += "     ,[cCLABE] ";
			strQuery += "     ,[cComentarios] ";
			strQuery += "     ,[cIdTipoCuenta] ";
			strQuery += " FROM [sai].[dbo].[tCatalogoCLABES_UR_CC] WITH(NOLOCK)  ";
		}

		//Catalogo CG_CAT_USUARIOS
		if (strTabla.equals("M_CAT_USUARIOS"))
		{
			//Query para identificar CBO de usuario en sesion
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u.u_login, ";
			strQuery += " u.u_password, ";
			strQuery += " e.salutacion, ";
			strQuery += " e.ce_nombre_completo + ' ' + e.ce_ap_paterno + ' ' + e.ce_ap_materno AS NombreCompl, ";
			strQuery += " u.u_email, ";
			strQuery += " a.d_descripcion, ";
			strQuery += " e.id_puesto, ";
			strQuery += " e.cargo ";
			strQuery += " FROM ";
			strQuery += " cg_usuario u WITH(NOLOCK) ";
			strQuery += " LEFT OUTER JOIN cg_cat_empleado e WITH(NOLOCK) ON (u.u_login = e.ce_os_responsable) ";
			strQuery += " LEFT OUTER JOIN cat_puestos p WITH(NOLOCK)  ON (e.id_puesto = p.id_puesto) ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " e.id_area = a.id_area ";
			strQuery += strWhere;

			strParam = strParam.replaceAll("NombreCompl", "e.ce_nombre_completo + ' ' + e.ce_ap_paterno + ' ' + e.ce_ap_materno");
		}

		//Catalogo TCAT_MENSAJES
		if (strTabla.equals("TCAT_MENSAJES"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " NOMBRE, ";
			strQuery += " mensaje, ";
			strQuery += " nAnio, ";
			strQuery += " idMensaje ";
			strQuery += " FROM ";
			strQuery += " tcat_mensajes WITH(NOLOCK) ";

			strParam = strParam.replaceAll("NOMBRE LIKE '","NOMBRE LIKE '%");
		}
		
		//Catalogo Cuentas bancoAmbiental
		if (strTabla.equals("TBANCOAMBIENTAL"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " dRFC, ";
			strQuery += " idBancario, ";
			strQuery += " CTAB ";
			strQuery += " FROM ";
			strQuery += " tBen_BancoAmbiental WITH(NOLOCK) ";

			strParam = strParam.replaceAll("RFC LIKE '","RFC LIKE '%");
		}
		//Catalogo M_VIMX_USUARIO
		if (strTabla.equals("M_VIMX_USUARIO"))
		{

			//Query para identificar CBO de usuario en sesion
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";
			
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
			
				
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += "	    V.U_LOGIN        ";
			strQuery += ",	    U.cNumeroEmpleado";
			strQuery += ",	    U.cRFC";
			strQuery += ",      V.CE_AP_PATERNO  ";
			strQuery += ",      V.CE_AP_MATERNO  ";
			strQuery += ",      V.CE_NOMBRE_COMPLETO ";
			strQuery += ",      V.CARGO          ";
			strQuery += ",      A.d_descripcion  ";
			strQuery += ",      c.co_descripcion ";
			strQuery += ",      r.R_DESCRIPCION  ";
			strQuery += ",      V.U_ESTATUS      ";
			strQuery += ",      V.U_EMAIL        ";
			strQuery += ",      V.SALUTACION     ";
			strQuery += ",      V.ID_AREA        ";
			strQuery += ",      V.U_PASSWORD     ";
			strQuery += ",      V.U_DESCRIPCION  ";
			strQuery += ",      V.ID_EMPLEADO    ";
			strQuery += ",      V.ID_PUESTO      ";
			strQuery += ",      V.id_cobertura   ";
			strQuery += ",      V.R_NOMBRE       ";			
			strQuery += ",      '' as EFISCAL      ";
			strQuery += " FROM                    ";
			strQuery += "       VIMX_USUARIO v  WITH(NOLOCK)  ";
			strQuery += " LEFT OUTER JOIN cg_cobertura c WITH(NOLOCK) ON (c.id_cobertura = v.id_cobertura) ";	
			strQuery += " LEFT OUTER JOIN cg_usuario u WITH(NOLOCK) ON (v.U_LOGIN = u.U_LOGIN) ";		
			strQuery += " LEFT OUTER JOIN cg_role r WITH(NOLOCK)      ON (r.r_nombre     = v.r_nombre) ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " a.id_area = v.id_area ";
			strQuery += strWhere;
			blnManto = true;
		}

// listado usuarios administradores para la delegacion de opciones
		if (strTabla.equals("CAT_USUARIO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " U.U_LOGIN ,";
			strQuery += " U.U_NOMBRE ";
			strQuery += " FROM CG_USUARIO U  WITH(NOLOCK) , CG_USUARIO_ROLE UR WITH(NOLOCK) ";
			strQuery += " WHERE U.U_LOGIN = UR.U_LOGIN AND UR.R_NOMBRE='ADMIN'  ";

		}
		
		if (strTabla.equals("CAT_EJERCICIO_FISCAL"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " AEJERCICIOFISCAL ,";
			strQuery += " CACTIVO ";
			strQuery += " FROM TEJERCICIOFISCAL";

		}
		

		//Catalogo CG_ROLE_OPCION (PARA SUPER-ADMINISTRADOR)
		if (strTabla.equals("M_CAT_OPERFIL"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " ro.r_nombre, ";
			strQuery += " ro.id_opcion, ";
			strQuery += " o.o_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_role_opcion RO WITH(NOLOCK), ";
			strQuery += " cg_opcion O WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " ro.id_opcion = o.id_opcion ";
		}

		//Catalogo CG_USUARIO_COBERTURA (FALTA COBERTURA -- PERO SE VA A DESHABILITAR)
		if (strTabla.equals("M_CAT_UCOBERTURA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " uc.u_login, ";
			strQuery += " co.co_descripcion, ";
			strQuery += " uc.id_cobertura, ";
			strQuery += " uc.id_tabla, ";
			strQuery += " uc.id_producto ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura uc WITH(NOLOCK), ";
			strQuery += " cg_cobertura co WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " uc.id_cobertura = co.id_cobertura ";
		}

		//Catalogo CG_COBERTURA (PARA SUPER-ADMINISTRADOR)
		if (strTabla.equals("M_CAT_COBERTURA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " id_cobertura, ";
			strQuery += " co_descripcion, descripcion= ";
			strQuery += " CASE ";
			strQuery += " WHEN co_descripcion = 'T'   THEN 'TODO' ";
			strQuery += " WHEN co_descripcion = 'H'   THEN 'HIJOS' ";
			strQuery += " WHEN co_descripcion = 'HH'  THEN 'HERMANOS-HIJOS' ";
			strQuery += " WHEN co_descripcion = 'PHH' THEN 'PADRE-HERMANOS-HIJOS' ";
			strQuery += " WHEN co_descripcion = 'PH'  THEN 'PADRE-HIJOS' ";
			strQuery += " END, ";
			strQuery += " id_tabla, ";
			strQuery += " id_producto ";
			strQuery += " FROM ";
			strQuery += " cg_cobertura WITH(NOLOCK) ";
			strQuery += " WHERE  co_descripcion != 'T'";

		}
			//Catalogo CG_OPCION (PARA SUPER-ADMINISTRADOR)
		if (strTabla.equals("M_CAT_OPCION"))
		{
			strQuery =  " SELECT ";
			strQuery += " o.id_opcion, ";
			strQuery += " o.o_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_opcion o WITH(NOLOCK) ";
			strQuery += " WHERE O_ESTAENMENU ='S'";
			strOrder = " order by o.O_ORDEN, o.O_ID_PARENT";

		}
			//Catalogo CG_ROLE (PARA SUPER-ADMINISTRADOR)
		if (strTabla.equals("M_CAT_ROLE_P"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " r_nombre, ";
			strQuery += " r_descripcion ";
			strQuery += " FROM ";
			strQuery += " cg_role WITH(NOLOCK) ";
			strQuery += " WHERE ADMIN_DUENO IN (SELECT UR.R_NOMBRE FROM CG_USUARIO_ROLE ur WHERE ur.U_LOGIN='"+strUsuario+"') AND r_nombre !='SUPER_ADMIN' ";

		}

		//Select catalogo categoria
		if (strTabla.equals("M_CG_CATEGORIA"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " CONSECUTIVO, valor";
			strQuery += "  ";
			strQuery += " FROM ";
			strQuery += " MXCATEGORIA WITH(NOLOCK) ";

		}

		//Catalogo CG_CAT_AREAS (PARA SUPER-ADMINISTRADOR)
		if (strTabla.equals("M_CG_CAT_AREAS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " a.d_descripcion, ";
			strQuery += " a.id_area, ";
			strQuery += " p.d_descripcion, ";
			strQuery += " p.id_area, ";
			strQuery += " a.prefijo_folio, ";
			strQuery += " a.folio_inicial, ";
			strQuery += " a.area_tiempo_lim, ";
			strQuery += " a.area_arch_max ";
			strQuery += " FROM ";
			strQuery += " cg_cat_areas a WITH(NOLOCK)  ";
			strQuery += " LEFT OUTER JOIN vimx_areas p WITH(NOLOCK) ON (a.id_area_padre = p.id_area )";
		}

		//Catalogo CG_CAT_EMPLEADO
		if (strTabla.equals("M_CG_CAT_EMPLEADO"))
		{
			//Query para identificar CBO de usuario en sesion
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " e.id_empleado, ";
			strQuery += " e.ce_nombre_completo, ";
			strQuery += " e.ce_ap_paterno, ";
			strQuery += " e.ce_ap_materno, ";
			strQuery += " e.ce_os_responsable, ";
			strQuery += " e.salutacion, ";
			strQuery += " e.id_area, ";
			strQuery += " a.d_descripcion, ";
			strQuery += " e.id_puesto, ";
			strQuery += " e.cargo ";
			strQuery += " FROM ";
			strQuery += " cg_cat_empleado e WITH(NOLOCK) ";
			strQuery += strFrom;
			//strQuery += "cg_cat_areas A ";
			strQuery += " WHERE ";
			strQuery += " e.id_area = a.id_area ";
			strQuery += strWhere;
		}

		//Catalogo M_CAT_LOGIN (ï¿½REPETIDO CON M_CAT_UCOBERTURA?)
		if (strTabla.equals("M_CAT_LOGIN"))
		{
			//Query para identificar CBO de usuario en sesion
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " e.ce_os_responsable, ";
			strQuery += " e.salutacion, ";
			strQuery += " e.ce_nombre_completo + ' ' + e.ce_ap_paterno + ' ' + e.ce_ap_materno AS NombreCompl, ";
			strQuery += " a.d_descripcion, ";
			strQuery += " e.id_puesto, ";
			strQuery += " e.cargo ";
			strQuery += " FROM ";
			strQuery += " cg_cat_empleado e WITH(NOLOCK) ";
			strQuery += strFrom;
			strQuery += " WHERE e.id_area = a.id_area ";
			strQuery += " AND Len(e.ce_os_responsable) <= 32 ";
			strQuery += strWhere;

			strParam = strParam.replaceAll("NombreCompl", "e.ce_nombre_completo + ' ' + e.ce_ap_paterno + ' ' + e.ce_ap_materno");
		}

		//Catalogo CG_SUPLANTACION
		if (strTabla.equals("M_CG_SUPLANTACION"))
		{

			//Query para identificar CBO de usuario en sesion
			strQuery =  " SELECT ";
			strQuery += " co_from_clause, ";
			strQuery += " co_where_clause ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_cobertura u WITH(NOLOCK), ";
			strQuery += " cg_cobertura c WITH(NOLOCK) ";
			strQuery += " WHERE ";
			strQuery += " u.u_login = '" + strUsuario + "' ";
			strQuery += " AND u.id_tabla = c.id_tabla ";
			strQuery += " AND u.id_tabla = " + TAB_CAT_AREAS + " ";
			strQuery += " AND u.id_cobertura = c.id_cobertura ";
			strQuery += " AND c.id_producto = " + PRD_GESTION + " ";

			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strFrom = ", " + strParamQ[0][0] + " ";
				strWhere = strParamQ[0][1] + " ";
				strWhere = strWhere.replaceAll("\\@u_login", "'" + strUsuario + "'");
			}

			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " s.SUP_LOGIN_A_SUPLANTAR ,";
			strQuery += " e.ce_ap_paterno + ' ' + e.ce_ap_materno + ' ' + e.ce_nombre_completo AS NombreCompl1, ";
			strQuery += " s.SUP_LOGIN_QUIEN_SUPLANTA ,";
			strQuery += " e2.ce_ap_paterno + ' ' + e2.ce_ap_materno  + ' ' + e2.ce_nombre_completo AS NombreCompl2, ";
			strQuery += " s.ID_SUPLANTACION ";
			strQuery += " FROM ";
			strQuery += " cg_suplantacion s WITH(NOLOCK) ";
			strQuery += " LEFT OUTER JOIN cg_cat_empleado e WITH(NOLOCK)  ON (s.SUP_LOGIN_A_SUPLANTAR 	= e.ce_os_responsable) ";
			strQuery += " LEFT OUTER JOIN cg_cat_empleado e2 WITH(NOLOCK) ON (s.SUP_LOGIN_QUIEN_SUPLANTA 	= e2.ce_os_responsable) ";
			strQuery += strFrom;
			strQuery += " WHERE ";
			strQuery += " e.id_area = a.id_area ";
			strQuery += strWhere;

			strParam = strParam.replaceAll("NombreCompl1", "e.ce_ap_paterno + ' ' + e.ce_ap_materno + ' ' + e.ce_nombre_completo");
			strParam = strParam.replaceAll("NombreCompl2", "e2.ce_ap_paterno + ' ' + e2.ce_ap_materno + ' ' + e2.ce_nombre_completo");

			strParam = strParam.replaceAll("ce_nombre_completo LIKE '", "ce_nombre_completo LIKE '%");
		}

		//Catalogo CG_GRUPO
		//DG
		if (strTabla.equals("M_CG_GRUPO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " g_nombre, ";
			strQuery += " g_descripcion, '' as EFISCAL";
			strQuery += " FROM ";
			strQuery += " cg_grupo WITH(NOLOCK) ";

			strParam = strParam.replaceAll("G_NOMBRE LIKE '","G_NOMBRE LIKE '%");
			blnManto = true;
			
		}
		//SELECT CATALOGO DE ROLES
		if (strTabla.equals("M_CG_ROLES"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " r_nombre, ";
			strQuery += " r_descripcion, ";
			strQuery += " admin_dueno, '' as EFISCAL ";
			strQuery += " FROM ";
			strQuery += " cg_role WITH(NOLOCK) ";
			strQuery += " WHERE admin_dueno IN (SELECT R_NOMBRE FROM CG_USUARIO_ROLE WITH(NOLOCK) WHERE U_LOGIN='"+strUsuario+"')";
			
			strParam = strParam.replaceAll("R_NOMBRE LIKE '","R_NOMBRE LIKE '%");
			if (strParam.startsWith("TODO")){
				strParam=strParam.replaceAll("TODO"," 1=1 ");
			}
			blnManto = true;
		}
		//Relacion usuario grupo
		if (strTabla.equals("M_CG_GRUPO_USUARIO"))
		{
			strQuery += "select TOP 1000 u_login,  g_nombre, '' as EFISCAL from cg_usuario_grupo ug with(nolock) "+
						"WHERE  ug.U_LOGIN in ( "+
						"select u.U_LOGIN "+
						"from "+
						"cg_usuario u WITH(NOLOCK) "+ 
						"inner join CG_USUARIO_ROLE ur WITH(NOLOCK) "+ 
						"ON u.U_LOGIN = ur.U_LOGIN "+
						"inner join CG_ROLE r with(nolock) "+
						"ON ur.R_NOMBRE = r.R_NOMBRE "+
						"where u.U_ESTATUS = 'A' and r.ADMIN_DUENO in (select ur.R_NOMBRE from CG_USUARIO_ROLE ur with(nolock) where ur.U_LOGIN='"+strUsuario+"')) ";

			strParam = strParam.replaceAll("G_NOMBRE LIKE '","G_NOMBRE LIKE '%");
			blnManto = true;
		}

		//Relacion usuario rol
		if (strTabla.equals("M_CG_ROL_USUARIO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " u_login, ";
			strQuery += " r.r_nombre, ";
			strQuery += " r.r_descripcion, '' as EFISCAL ";
			strQuery += " FROM ";
			strQuery += " cg_usuario_role ur WITH(NOLOCK) inner join cg_role r WITH(NOLOCK) on ur.r_nombre=r.r_nombre ";
			strParam = strParam.replaceAll("R_NOMBRE LIKE '","r.R_NOMBRE LIKE '%");
			strParam=strParam.concat(" and r.admin_dueno in (select o.R_NOMBRE from CG_USUARIO_ROLE o WITH(NOLOCK) WHERE o.U_LOGIN='"+strUsuario+"')");
			
			if (strParam.startsWith("TODO")){
				strParam=strParam.replaceAll("TODO"," 1=1 ");
			}
			
			blnManto = true;
		}
		
		if (strTabla.equalsIgnoreCase("M_VISTASUR"))//URVP.25062014 Relacion UsuarioVista
		{
			strQuery =  " SELECT usuario,modulo,ur FROM tVistasUR WITH (NOLOCK) ";
		}
		if (strTabla.equalsIgnoreCase("modulos"))//URVP.25062014 modulos para las vistas
		{
			strQuery =  " SELECT * FROM tVistasUrModulo WITH (NOLOCK) ";
		}
		
		if (strTabla.equals("RETENCIONESOBLIGARORIASGRID")) //URVP.02092014 Retenciones Obligatorias de Acuerdo a las Partidas
		{
			strQuery =   "SELECT DISTINCT cIdTipoRetencion,cTipoRetencion,nPorcRetencion FROM dbo.pCatalogoTipoRetencion catalogo WITH (NOLOCK) ";
			strQuery += "JOIN tRelacionPartidaRetencion Obligatorias WITH (NOLOCK) ON catalogo.cIdTipoRetencion = Obligatorias.idRetencion";
		}
		

		if (strTabla.equals("M_CG_GRUPO_USUARIO1"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " g_nombre ";
			strQuery += " FROM ";
			strQuery += " cg_grupo WITH(NOLOCK) ";

			strParam = strParam.replaceAll("G_NOMBRE LIKE '","G_NOMBRE LIKE '%");
		}
		//catalogo de gerencia de ventas
		if (strTabla.equals("M_CG_GERENCIA_VENTAS"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " consecutivo, valor ";
			strQuery += " FROM ";
			strQuery += " mxgerencia_ventas WITH(NOLOCK) ";

			strParam = strParam.replaceAll("valor LIKE '","valor LIKE '%");
		}

		//catalogo de Empaques
		if (strTabla.equals("M_CG_EMPAQUES"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " consecutivo, valor ";
			strQuery += " FROM ";
			strQuery += " mxempaque WITH(NOLOCK) ";

			strParam = strParam.replaceAll("valor LIKE '","valor LIKE '%");
		}

		//catalogo de Estatus Producto
		if (strTabla.equals("M_CG_ESTATUS_PRODUCTO"))
		{
			strQuery =  " SELECT TOP " + strMaxReg;
			strQuery += " consecutivo, valor ";
			strQuery += " FROM ";
			strQuery += " mxestatus_producto WITH(NOLOCK) ";

			strParam = strParam.replaceAll("valor LIKE '","valor LIKE '%");
		}
		//catalogo de Usuario Propiedad
		if (strTabla.equals("M_CG_USUARIO_PROPIEDADES"))
		{
			strQuery += "SELECT TOP 1000 ur.U_LOGIN, ur.up_nombre, ur.up_valor, ur.admin_dueno, '' AS EFISCAL FROM cg_usuario_propiedades ur WITH(NOLOCK) WHERE  ur.U_LOGIN in (select u.U_LOGIN from cg_usuario u WITH(NOLOCK) where u.U_ESTATUS = 'A') ";
			
			strParam = strParam.replaceAll("valor LIKE '","valor LIKE '%");

			if (strParam.startsWith("TODO")){
				strParam=strParam.replaceAll("TODO"," 1=1 ");
			}
			
			blnManto = true;
		}

		// Seccion para Multireporte
		if (strTabla.equals("RAMOEP")){
			strQuery = " SELECT cRamo,cRamo+' '+dRamo dRamo FROM tRamo WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("PARTIDA")){
			strQuery = " SELECT cPartida, hPartida FROM (SELECT  cPartida, dPartida,cPartida+' '+dPartida hPartida FROM tCatalogoPartida  WITH(NOLOCK) ) a ";
	 	}
	 	
	 	if (strTabla.equals("PRESTAMO")){
			strQuery = "select NumeroPrestamo,NombrePrestamo,Objetivo, id_prestamo from dprestamo WITH (nolock) where Estatus='A' ";
	 	}
	 	
	 	if (strTabla.equals("SOE")){
			strQuery = "select distinct numero_soe,Estatus from dsoe WITH (nolock) where upper(Estatus) not in ('B','C')  ";
	 	}
	 	
	 	if( strTabla.equals("R_SELECCIONA_EVENTO") ){
			strQuery = 
				" SELECT evento.cEvento,Evento.dEvento,Evento.nIdGrupoEvento from (select CONVERT( VARCHAR, e.nidgrupoevento)+'_'+ CONVERT( VARCHAR, e.nIdSubGrupoEvento )+'_'+convert(varchar,e.cevento) AS cEvento" + 
				"        ,devento,nidgrupoevento,e.cUR" +				
				" FROM   teventorelacion e with(nolock)  inner join teventomanual m  on " +
 				" e.nidgrupoevento=m.cidgrupoevento " +
 				" and e.nidsubgrupoevento=m.cidsubgrupoevento " +
 				" and e.cevento=m.cideventomanual " +
 				" where m.cpartida is null and m.ndocrenglon=1 and cModulo='CAJA'" +
 				" ) Evento ";
			
		}	

	 	// ****************** Poliza Manual ********************************	 	
	 	if (strTabla.equals("TCATALOGOPARTIDA")){
			strQuery = "select * from (select distinct  m.cPartida , convert(varchar,m.cPartida) +'   '+ dPartida descripcion,cIdGrupoEvento,cIdSubGrupoEvento,cIdEventoManual from tEventoManual  m WITH (nolock)inner join tCatalogoPartida p  WITH (nolock)on m.cPartida=p.cPartida ) l  ";
	 	}
	 	
	 	if (strTabla.equals("TCATALOGOGRUPO")){
			strQuery = "select *from (select nIdGrupoEvento, convert(varchar,nIdGrupoEvento)+'    '+cNombreGrupo descripcion from tGrupoEvento WITH (nolock)) g where nIdGrupoEvento not in(5,8,10,19,27,35)  ";
	 	}
	 	
	 	if (strTabla.equals("TCATALOGOSUBGRUPO")){
			strQuery = "select nIdGrupoEvento,nIdSubGrupoEvento,cNombreSubGrupo from tSubGrupoEvento WITH (nolock)";
			strParam=strParam.replace("%","").replace("LIKE","=");
						
			}
	 	if (strTabla.equals("TCATALOGOEVENTO")){
			strQuery = "select nIdGrupoEvento,nIdSubGrupoEvento,cEvento,dEvento from tEventoRelacion WITH (nolock)";
			strParam=strParam.replace("%","").replace("LIKE","=");
		}
		
	 	if (strTabla.equals("TCATALOGOCABMS")){
			strQuery="select * from (select evt.* ,cCABMS,cCUCOP,cCABMS+' '+cDescripcion cDescripcion from (select distinct cIdGrupoEvento,cIdSubGrupoEvento,cIdEventoManual"
			         +",cPartida from tEventoManual with(nolock)) evt inner join tCatalogoCABMS  cb with(nolock) on evt.cPartida=cb.cPartida)tr ";
			
		}
		if (strTabla.equals("PARTIDAPOL")){
			strQuery = " SELECT cPartida hPartidaPol, hPartida FROM (SELECT  cPartida, dPartida,cPartida+' '+dPartida hPartida FROM tCatalogoPartida  WITH(NOLOCK) ) a ";
	 	}
	 	if (strTabla.equals("PROYECTOPOL")){
			strQuery = " SELECT cFonden hProyectoPol, hProyecto FROM (SELECT cFonden, dFonden, cFonden + ' ' + dFonden hProyecto FROM tCatalogoFonden WITH (NOLOCK)) a ";
	 	}
			
			
			
	 	/////////////////////////////////////////////////////////////////////
	 	
		if (strTabla.equals("CARTERA")){
			strQuery = " select id, descripcion  from (SELECT cCartera+' '+dCartera descripcion,cCartera id FROM tCatalogoCartera  WITH(NOLOCK) ) a ";
	 	}
		
		if (strTabla.equals("CUENTASANALITICO")){
			strQuery = " select ID, DESCRIPCION from (SELECT nCuenta+' '+dCuenta DESCRIPCION, nCuenta ID FROM tCuentas  with (nolock) WHERE not(TipoBalance='P' and TipoCuenta='P') and nCuenta not like '0%') a  where 1=1 ";
		}
		if (strTabla.equals("CUENTASANALITICOCC")){
			strQuery = " select ID, DESCRIPCION, cSubcuenta,cCentroContable from ( SELECT distinct c.nCuenta+' '+c.dCuenta DESCRIPCION, c.nCuenta ID, c.cSubcuenta, s.cCentroContable from tCuentas c with (nolock) inner join tsaldos s with (nolock) on c.nCuenta=s.nCuenta where  not (c.nNivelBalanza between 1 and 4) and not(c.TipoBalance='P' and c.TipoCuenta='P') and c.cSubcuenta is not null and AplicacionCuenta='S' union SELECT distinct c.nCuenta+' '+c.dCuenta DESCRIPCION, c.nCuenta ID, c.cSubcuenta, '00' cCentroContable from tCuentas c with (nolock) inner join tsaldos s with (nolock) on c.nCuenta=s.nCuenta where not (c.nNivelBalanza between 1 and 4) and not(c.TipoBalance='P' and c.TipoCuenta='P') and c.cSubcuenta is not null and AplicacionCuenta='S' ) a ";
			System.out.println("strQuery"+strQuery);
			
		}
		if (strTabla.equals("CUENTASANALITICOCCAUXILIARES")){
			strQuery = " select ID, DESCRIPCION, cSubcuenta from ( SELECT distinct c.nCuenta+' '+c.dCuenta DESCRIPCION, c.nCuenta ID, c.cSubcuenta from tCuentas c with (nolock) WHERE c.nCuenta not like '0%' and not (TipoBalance='P' and TipoCuenta='P')  and c.cSubcuenta is not null and AplicacionCuenta='S' union SELECT distinct c.nCuenta+' '+c.dCuenta DESCRIPCION, c.nCuenta ID, c.cSubcuenta from tCuentas c with (nolock) WHERE c.nCuenta not like '0%' and c.nCuenta not like '8%' and c.AplicacionCuenta='N' and c.nCuenta not in (select c2.nCuentaPadre from tCuentas c2 with (nolock) WHERE c2.cSubcuenta is not NULL and c2.nCuenta not like '8%' and c2.nCuenta not like '0%' ) union SELECT distinct c.nCuenta+' '+c.dCuenta DESCRIPCION, c.nCuenta ID, c.cSubcuenta from tCuentas c with (nolock) WHERE c.nCuenta not like '0%' and c.nCuenta not like '8%' and c.AplicacionCuenta='S') a where 1=1  ";
		}
		if (strTabla.equals("BALBUSCARFC")){
			strQuery = " select ID, DESCRIPCION, buscaCuentaC, cCentroContable from ( select distinct dRFC ID, dRFC+' '+isnull(b.dNombre,'')+' '+isnull(b.dApellidoPaterno,'')+' '+isnull(b.dApellidoMaterno,'') Descripcion, c.nCuenta buscaCuentaC, s.cCentroContable from tsaldos s with (nolock) inner join tBeneficiario b with (nolock) on s.cSubcuenta=b.dRFC inner join tcuentas c with (nolock) on s.nCuenta=c.nCuenta WHERE c.cSubcuenta='RFC' and s.nCuenta not like '0%' and s.nCuenta not like '8%' union select distinct dRFC ID, dRFC+' '+isnull(b.dNombre,'')+' '+isnull(b.dApellidoPaterno,'')+' '+isnull(b.dApellidoMaterno,'') Descripcion, c.nCuenta buscaCuentaC, '00' cCentroContable from tsaldos s with (nolock) inner join tBeneficiario b with (nolock) on s.cSubcuenta=b.dRFC inner join tcuentas c with (nolock) on s.nCuenta=c.nCuenta WHERE c.cSubcuenta='RFC' and s.nCuenta not like '0%' and s.nCuenta not like '8%' ) a where 1=1  ";
		}
		if (strTabla.equals("BALBUSCAALM")){
			strQuery = " select ID, DESCRIPCION, buscaCuentaC, cCentroContable from ( select distinct nIdAlmacen ID, cast(nIdAlmacen as varchar(2))+' '+cAlmacen Descripcion, c.nCuenta buscaCuentaC, s.cCentroContable from tsaldos s with (nolock) inner join CAT_ALMACEN b with (nolock) on s.cSubcuenta=cast(b.nIdAlmacen as varchar(2)) and s.cCentroContable=b.cCentroContable inner join tcuentas c with (nolock) on s.nCuenta=c.nCuenta where c.cSubcuenta='ALM' and not (c.nNivelBalanza between 1 and 4) and not(c.TipoBalance='P' and c.TipoCuenta='P') union select distinct nIdAlmacen ID, cast(nIdAlmacen as varchar(2))+' '+cAlmacen Descripcion, c.nCuenta buscaCuentaC, '00' cCentroContable from tsaldos s with (nolock) inner join CAT_ALMACEN b with (nolock) on s.cSubcuenta=cast(b.nIdAlmacen as varchar(2)) and s.cCentroContable=b.cCentroContable inner join tcuentas c with (nolock) on s.nCuenta=c.nCuenta where c.cSubcuenta='ALM' and not (c.nNivelBalanza between 1 and 4) and not(c.TipoBalance='P' and c.TipoCuenta='P') ) a   ";
		}
		if (strTabla.equals("BALBUSCACTAB")){
			strQuery = " select ID, DESCRIPCION, buscaCuentaC, cCentroContable from ( select distinct strClabe ID, strClabe+' '+strRFC Descripcion, c.nCuenta buscaCuentaC, s.cCentroContable from tsaldos s with (nolock) inner join tUECuentasBancarias b with (nolock) on s.cSubcuenta=b.strClabe inner join tcuentas c with (nolock) on s.nCuenta=c.nCuenta where c.cSubcuenta='CTAB' and not (c.nNivelBalanza between 1 and 4) and not(c.TipoBalance='P' and c.TipoCuenta='P') union select distinct strClabe ID, strClabe+' '+strRFC Descripcion, c.nCuenta buscaCuentaC, '00' cCentroContable from tsaldos s with (nolock) inner join tuecuentasbancarias b with (nolock) on s.cSubcuenta=b.strClabe inner join tcuentas c with (nolock) on s.nCuenta=c.nCuenta where c.cSubcuenta='CTAB' and not (c.nNivelBalanza between 1 and 4) and not(c.TipoBalance='P' and c.TipoCuenta='P') ) a  ";
		}
		if (strTabla.equals("CONBUSCANCON")){
			if (esSAIAlterno){
				strQuery = "SELECT nFolioCuenta, "+
							" cInstitucion, "+
							" cNomInstitucion, "+ 
							" cCLABE, "+
							" cInstrumento, "+
							" ISNULL((CASE WHEN LEN(cCLABE) <> 18 THEN '10' ELSE (SELECT TOP (1) strCentroContable FROM dbo.tUECuentasBancarias (NOLOCK) WHERE strClabe = cCLABE AND strCentroContable = '10')  END),'00') AS cCentroContable, "+
							" cTipoAct, "+
							" CASE WHEN cTipoMov = 'B' THEN 'CANCELADA' ELSE '' END AS Estatus, "+
							" CASE WHEN cTipoMov = 'B' THEN CAST(fMovimiento AS VARCHAR(10)) ELSE '' END AS Fecha "+
							" FROM dbo.tSIIWEBCuentas (NOLOCK) "+
							" WHERE (cTipoAct <> 'DF') OR cInstrumento <> 'IE009' OR cNomInstitucion = 'HSBC') OR cCLABE = '072320002138455194'";
				System.out.println("strQuery "+strQuery);
			}
			else {
				strQuery = "SELECT nFolioCuenta, "+
							" cInstitucion, "+
							" cNomInstitucion, "+ 
							" cCLABE, "+
							" cInstrumento, "+
							" ISNULL((CASE WHEN LEN(cCLABE) <> 18 THEN '10' ELSE (SELECT TOP (1) strCentroContable FROM dbo.tUECuentasBancarias (NOLOCK) WHERE strClabe = cCLABE)  END),'00') AS cCentroContable, "+
							" cTipoAct, "+
							" CASE WHEN cTipoMov = 'B' THEN 'CANCELADA' ELSE '' END AS Estatus, "+
							" CASE WHEN cTipoMov = 'B' THEN CAST(fMovimiento AS VARCHAR(10)) ELSE '' END AS Fecha "+
							" FROM dbo.tSIIWEBCuentas (NOLOCK) "+
							" WHERE cTieneConciliacion = 'S'";
				System.out.println("strQuery "+strQuery);
			}
		}
		if (strTabla.equals("CEDE06CON")){
			strQuery = "SELECT nFolioeCs AS ID "+ 
							", nEjercicio Ejercicio "+
							", CASE nMes "+
								"WHEN 1 THEN 'Enero' "+
								"WHEN 2 THEN 'Febrero' "+
								"WHEN 3 THEN 'Marzo' "+
								"WHEN 4 THEN 'Abril' "+
								"WHEN 5 THEN 'Mayo' "+
								"WHEN 6 THEN 'Junio' "+
								"WHEN 7 THEN 'Julio' "+
								"WHEN 8 THEN 'Agosto' "+
								"WHEN 9 THEN 'Septiembre' "+
								"WHEN 10 THEN 'Octubre' "+
								"WHEN 11 THEN 'Noviembre' "+
								"WHEN 12 THEN 'Diciembre' "+
							  "END Mes "+
							", nVersion AS Version "+
							", 'Cedula E06 - ' + CASE nMes "+
								"WHEN 1 THEN 'Enero' "+
								"WHEN 2 THEN 'Febrero' "+
								"WHEN 3 THEN 'Marzo' "+
								"WHEN 4 THEN 'Abril' "+
								"WHEN 5 THEN 'Mayo' "+
								"WHEN 6 THEN 'Junio' "+
								"WHEN 7 THEN 'Julio' "+
								"WHEN 8 THEN 'Agosto' "+
								"WHEN 9 THEN 'Septiembre' "+
								"WHEN 10 THEN 'Octubre' "+
								"WHEN 11 THEN 'Noviembre' "+
								"WHEN 12 THEN 'Diciembre' "+
							 " END + ' '+CAST(nEjercicio AS VARCHAR(4)) + ' - Version ' + CAST(nVersion AS VARCHAR(3)) AS Descripcion "+
						"FROM dbo.tSIIWEBeCeroSeisEncabezado (NOLOCK) WHERE nMes > 0";
			System.out.println("strQuery "+strQuery);
		}
		if (strTabla.equals("VERSIONCON")){
			strQuery = "SELECT nFolioeCs AS ID "+ 
							", nEjercicio Ejercicio "+
							", CASE nMes "+
								"WHEN 1 THEN 'Enero' "+
								"WHEN 2 THEN 'Febrero' "+
								"WHEN 3 THEN 'Marzo' "+
								"WHEN 4 THEN 'Abril' "+
								"WHEN 5 THEN 'Mayo' "+
								"WHEN 6 THEN 'Junio' "+
								"WHEN 7 THEN 'Julio' "+
								"WHEN 8 THEN 'Agosto' "+
								"WHEN 9 THEN 'Septiembre' "+
								"WHEN 10 THEN 'Octubre' "+
								"WHEN 11 THEN 'Noviembre' "+
								"WHEN 12 THEN 'Diciembre' "+
							  "END Mes "+
							", nVersion AS Version "+
							", 'Conciliaciones - ' + CASE nMes "+
								"WHEN 1 THEN 'Enero' "+
								"WHEN 2 THEN 'Febrero' "+
								"WHEN 3 THEN 'Marzo' "+
								"WHEN 4 THEN 'Abril' "+
								"WHEN 5 THEN 'Mayo' "+
								"WHEN 6 THEN 'Junio' "+
								"WHEN 7 THEN 'Julio' "+
								"WHEN 8 THEN 'Agosto' "+
								"WHEN 9 THEN 'Septiembre' "+
								"WHEN 10 THEN 'Octubre' "+
								"WHEN 11 THEN 'Noviembre' "+
								"WHEN 12 THEN 'Diciembre' "+
							 " END + ' '+CAST(nEjercicio AS VARCHAR(4)) + ' - Version ' + CAST(nVersion AS VARCHAR(3)) AS Descripcion "+
							", CASE WHEN (SELECT COUNT(*) FROM dbo.tSIIWEB112Detalle (NOLOCK) WHERE nFolioeCs = e.nFolioeCs) > 0 THEN 'GENERADO' ELSE ' -- ' END AS Estatus112 "+
							", CASE WHEN (SELECT COUNT(*) FROM dbo.tSIIWEBOnce12Detalle (NOLOCK) WHERE nFolioeCs = e.nFolioeCs) > 0 THEN 'GENERADO' ELSE ' -- ' END AS Estatus1112 "+
						"FROM dbo.tSIIWEBeCeroSeisEncabezado e (NOLOCK) WHERE nMes > 0";
			System.out.println("strQuery "+strQuery);
		}
		if (strTabla.equals("PROGRAMAPRESUPUESTARIO")){
//			strQuery = " select cProgramaPresupuestario id, ( cProgramaPresupuestario+' '+dProgramaPresupuestario) descripcion from  tCatalogoProgramaPresupuestario ";
	strQuery = " select  id, descripcion from (SELECT cProgramaPresupuestario+' '+dProgramaPresupuestario descripcion,cProgramaPresupuestario id FROM tCatalogoProgramaPresupuestario)a ";
	 	}

		if (strTabla.equals("CARTERAOP")){
			strQuery = " select id, Descripcion";  
			if (strParam.indexOf("uUR LIKE") > -1 )
				strQuery += ", uUR ";
			else
				strQuery += ", '' uUR ";
			if (strParam.indexOf("Partida LIKE") > -1 )
				strQuery += ", Partida  ";
			else
				strQuery += ", '' Partida  ";
			if (strParam.indexOf("ProgramaPresupuestario LIKE") > -1 )
				strQuery += ", ProgramaPresupuestario ";
			else
				strQuery += ", '' ProgramaPresupuestario ";
			strQuery += " from (SELECT pp.cCartera+' '+dCartera descripcion,pp.cCartera id, cep.cunidadejecutora uUR, cep.cPartida Partida, cep.cProgramaPresupuestario ProgramaPresupuestario  FROM tCatalogoCartera  pp WITH(NOLOCK)   inner join tcatalogoep cep on cep.cCartera = pp.cCartera where cep.cProgramaPresupuestario like 'K%' and cPartida like '6%' ) a ";
	 	}
		
		if (strTabla.equals("PARTIDAOP")){
			strQuery = " SELECT distinct cPartida, hPartidaOP  ";
			if (strParam.indexOf("uUR LIKE") > -1 )
				strQuery += ", uUR ";
			else
				strQuery += ", '' uUR ";
			if (strParam.indexOf("ProgramaPresupuestario LIKE") > -1 )
				strQuery += ", ProgramaPresupuestario ";
			else
				strQuery += ", '' ProgramaPresupuestario ";
			if (strParam.indexOf("Cartera LIKE") > -1 )
				strQuery += ", Cartera ";
			else
				strQuery += ", '' Cartera ";
			strQuery += " FROM (SELECT  pp.cPartida, pp.dPartida, pp.cPartida+' '+ pp.dPartida hPartidaOP, cep.cunidadejecutora uUR, cep.cCartera Cartera, cep.cProgramaPresupuestario ProgramaPresupuestario  FROM tCatalogoPartida pp  WITH(NOLOCK) inner join tcatalogoep cep on cep.cPartida = pp.cPArtida where cep.cProgramaPresupuestario like 'K%' and pp.cPartida like '6%' ) a WHERE cPartida like '62%' ";
	 	}

		if (strTabla.equals("PROGRAMAPRESUPUESTARIOOP")){
		 //uUR LIKE '%B57%' AND Partida LIKE '%62601%' AND Cartera LIKE '%1216B000207%'
			strQuery = " select distinct id, descripcion";
			if (strParam.indexOf("uUR LIKE") > -1 )
				strQuery += ", uUR ";
			else
				strQuery += ", '' uUR ";
			if (strParam.indexOf("Partida LIKE") > -1 )
				strQuery += ", Partida  ";
			else
				strQuery += ", '' Partida  ";
			if (strParam.indexOf("Cartera LIKE") > -1 )
				strQuery += ", Cartera ";
			else
				strQuery += ", '' Cartera ";
			strQuery += " from (SELECT pp.cProgramaPresupuestario+' '+pp.dProgramaPresupuestario descripcion, pp.cProgramaPresupuestario id, cep.cunidadejecutora uUR, cep.cPartida Partida, cep.cCartera Cartera FROM tCatalogoProgramaPresupuestario pp with(nolock)  inner join tcatalogoep cep on cep.cprogramapresupuestario = pp.cprogramapresupuestario where pp.cProgramaPresupuestario like 'K%' and cPartida like '6%' )a ";
	 	}
	 	
		if (strTabla.equals("EP")){
			strQuery = "  select id,  descripcion, UnidadResponsable, ProgramaPresupuestario, Partida , Cartera, UnidadNormativa, UnidadEjecutora  from (select distinct EP id, EP descripcion, D.cUnidadResponsable+' '+D.D_DESCRIPCION UnidadResponsable, J.cProgramaPresupuestario+' '+J.dProgramaPresupuestario ProgramaPresupuestario, K.cPartida+' '+K.dPartida Partida ,  O.cCartera+' '+O.dCartera Cartera , P.cUnidadResponsable+' '+P.D_DESCRIPCION UnidadNormativa, Q.cUnidadResponsable+' '+Q.D_DESCRIPCION UnidadEjecutora from tCatalogoEP a  with (nolock) inner join tRamo C on a.cRamo=C.cRamo inner join TCATUNIDADRESPONSABLE D  with (nolock) on a.cUnidadResponsableEP=D.cUnidadResponsable inner join tCatalogoProgramaPresupuestario J  with (nolock) on a.cProgramaPresupuestario=J.cProgramaPresupuestario inner join tCatalogoPartida K  with (nolock) on a.cPartida=K.cPartida inner join tCatalogoCartera O  with (nolock) on a.cCartera=O.cCartera  inner join TCATUNIDADRESPONSABLE P  with (nolock) on a.cUnidadNorativa=P.cUnidadResponsable inner join TCATUNIDADRESPONSABLE Q  with (nolock) on a.cUnidadEjecutora=Q.cUnidadResponsable ) a    ";
	 	}

		//se utiliza para los catalogos de unidad normativa y unidad ejecutora en los reportes de creaci?el presupuesto y creaci?el presupuesto calendarizado
		if (strTabla.equals("UNIDADRESPONSABLE")){
			strQuery = "  select ID_AREA, hUnidadResponsable from (SELECT cUnidadResponsable+' '+D_DESCRIPCION hUnidadResponsable, cUnidadResponsable ID_AREA FROM TCATUNIDADRESPONSABLE  with (nolock) ) a ";
		}
		if (strTabla.equals("UR_NORMATIVA")){
			strQuery = "  select ID_AREA, hUnidadResponsable from (SELECT cUnidadResponsable+' '+D_DESCRIPCION hUnidadResponsable, cUnidadResponsable ID_AREA FROM TCATUNIDADRESPONSABLE  with (nolock) WHERE nAlcance = 1) a ";
		}
		if (strTabla.equals("PROGRAMAPRESUPUESTARIO_ANT")){
			strQuery = " select  id, descripcion from (SELECT cProgramaPresupuestario+' '+dProgramaPresupuestario descripcion,cProgramaPresupuestario id FROM tCatalogoProgramaPresupuestarioAnteproyecto)a ";
	 	}
	 	if (strTabla.equals("PARTIDA_ANT")){
			strQuery = " SELECT cPartida, hPartida FROM (SELECT  cPartida, dPartida,cPartida+' '+dPartida hPartida FROM tCatalogoPartidaAnteproyecto with (nolock) ) a ";
	 	}
	 	if (strTabla.equals("CARTERA_ANT")){
			strQuery = " select id, descripcion  from (SELECT cCartera+' '+dCartera descripcion,cCartera id FROM tCatalogoCarteraAnteproyecto  with (nolock) ) a ";
	 	}
	 	//mantenimiento a catalogos anteproyecto
	 	if (strTabla.equals("M_TCATALOGORELESTFUNPROGRAMA")){
			strQuery = " SELECT relacion as txtGridRelacionEFProg FROM vCatalogoEFPrograma WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL_ANT")){
			strQuery = " SELECT cGrupoFuncional,dGrupoFuncional FROM tCatalogoGpoFuncionalAnteproyecto WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("M_TCATALOGOFUNCION_ANT")){
			strQuery = " SELECT cGrupoFuncional,cFuncion,dFuncion FROM tCatalogoFuncionAnteproyecto WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("M_TCATALOGOSUBFUNCION_ANT")){
			strQuery = " SELECT cGrupoFuncional,cFuncion,cSubFuncion,dSubFuncion FROM tCatalogoSubfuncionAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL_ANT")){
			strQuery = " SELECT cProgramaGeneral,dProgramaGeneral FROM tCatalogoProgGralAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL_ANT")){
			strQuery = " SELECT cActividadInstitucional,dActividadInstitucional FROM tCatalogoAIAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO_ANT")){
			strQuery = " SELECT cProgramaPresupuestario, dProgramaPresupuestario FROM tCatalogoProgramaPresupuestarioAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOPARTIDA_ANT")){
			strQuery = " SELECT cPartida, dPartida FROM tCatalogoPartidaAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOTIPOGASTO_ANT")){
			strQuery = " SELECT cTipoGasto, dTipoGasto FROM tCatalogoTipoGastoAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO_ANT")){
			strQuery = " SELECT cFuenteFinanciamiento, dFuenteFinanciamiento FROM tCatalogoFuenteFinanciamientoAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOCARTERA_ANT")){
			strQuery = " SELECT cCartera, dCartera FROM tCatalogoCarteraAnteproyecto WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TUNIDADRESPONSABLE")){
			strQuery = " SELECT cUnidadResponsable, D_DESCRIPCION, nAlcance FROM TCATUNIDADRESPONSABLE with (nolock)";
		}
		if (strTabla.equals("M_TUNIDADRESPONSABLE_ANT")){
			strQuery = " SELECT cUnidadResponsable, D_DESCRIPCION, nAlcance FROM TCATUNIDADRESPONSABLEANTEPROYECTO with (nolock)";
		}
		// Seccion para ConsultaPolizas
		if (strTabla.equals("POLCTROCONTABLE")){
			strQuery = "  SELECT  ID,  Descripcion FROM (select '00' ID, '00 CONSOLIDADO' Descripcion union SELECT cCentroContable ID,cCentroContable+' '+cDescripcion Descripcion FROM tCatalogoCentroContable WITH(NOLOCK)) a ";
	 	}
		if (strTabla.equals("BALCTROCONTABLE")){
			strQuery = "  SELECT   distinct ID,  Descripcion FROM (select '00' ID, '00 CONSOLIDADO' Descripcion, '00' cUnidadResponsable  union SELECT ccc.cCentroContable ID, ccc.cCentroContable+' '+ccc.cDescripcion Descripcion, URCC.cUnidadResponsable FROM tCatalogoCentroContable ccc WITH(NOLOCK) inner join (SELECT cUnidadResponsable ,cCentroContable ,nConsecutivo  FROM tCatalogoURCC WITH(NOLOCK)) URCC on URCC.cCentroContable=ccc.cCentroContable WHERE ccc.cCentroContable!='0') a WHERE 1=1 ";
	 	}
		if (strTabla.equals("POLTIPO")){
			strQuery = " SELECT  ID,  Descripcion FROM (SELECT cTipoPoliza ID,cTipoPoliza+' '+cDescripcionPoliza Descripcion FROM tCatalogoTipoPoliza WITH(NOLOCK)) a ";
	 	}
		if (strTabla.equals("POLAUTORIZO")){
			strQuery = " SELECT  ID,  Descripcion FROM (SELECT U_LOGIN ID, U_LOGIN+' '+U_NOMBRE Descripcion FROM CG_USUARIO) a  WITH(NOLOCK)";
			System.out.println("strQuery"+strQuery);
	 	}
		if (strTabla.equals("CATALOGOSALARIO")){
			strQuery = "SELECT ID_CAT_SALARIO,CANT_SALARIO,round(SALARIO,2)SALARIO FROM mCatSalario  WITH(NOLOCK) ";
			if(strParam.equals("1=2")){
				strParam="";
			}
			System.out.println("strQuery"+strQuery);
	 	}
		
		if (strTabla.equals("M_TCATJUNTASCONCILIACIONARBITRAJE")){			
			strQuery = " SELECT cNombreJunta FROM tCatJuntasConciliacionArbitraje WITH(NOLOCK) ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}
		if (strTabla.equals("M_TCATALOGOOLI")){			
			strQuery = " SELECT idOLIOf, idProyecto, idCvePres, idEstado, Monto, idUAN, idUAE, lHabilitada FROM cnsOLIS WITH(NOLOCK) ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}		
		if (strTabla.equals("M_TSUBCUENTASFFM")){			
			strQuery = " SELECT cSubcuenta, cDescripcion FROM tSubcuentasFFM WITH(NOLOCK) ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}
		
		// Seccion para catalogos Presupuestales  
		if (strTabla.equals("M_TRAMO")){
			strQuery = " SELECT cRamo,dRamo FROM tRamo WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("M_TCATALOGOUNIDADRESPONSABLE")){
	 		strQuery = "SELECT cUnidadResponsable, ID_AREA, D_DESCRIPCION  FROM ( select ur.cUnidadResponsable, ur.ID_AREA,ca.D_DESCRIPCION FROM tCatalogoUnidadResponsable ur WITH(NOLOCK) inner join CG_CAT_AREAS ca WITH(NOLOCK) ON ur.ID_AREA = ca.ID_AREA )as tbl";
	 	}
		if (strTabla.equals("M_TCATALOGOUNIDADRESPONSABLEMAT")){
			strQuery ="SELECT cUnidadResponsable,t.ID_AREA,a.D_DESCRIPCION FROM tCatalogoUnidadResponsable t WITH(NOLOCK) inner join CG_CAT_AREAS a WITH(NOLOCK) on t.ID_AREA=a.ID_AREA";
	 	}
		if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL")){
			strQuery = " SELECT cGrupoFuncional,dGrupoFuncional FROM tCatalogoGrupoFuncional WITH(NOLOCK) ";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOFUNCION")){
			strQuery = " SELECT cGrupoFuncional,cFuncion,dFuncion FROM tCatalogoFuncion WITH(NOLOCK) ";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOSUBFUNCION")){
			strQuery = " SELECT cGrupoFuncional,cFuncion,cSubFuncion,dSubFuncion FROM tCatalogoSubFuncion WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL")){
			strQuery = " SELECT cProgramaGeneral,dProgramaGeneral FROM tCatalogoProgramaGeneral WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL")){
			strQuery = " SELECT cActividadInstitucional,dActividadInstitucional FROM tCatalogoActividadInstitucional WITH(NOLOCK) ";
		} 
		
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO")){
			strQuery = " SELECT cProgramaPresupuestario, dProgramaPresupuestario FROM tCatalogoProgramaPresupuestario WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCATALOGOPARTIDA")){
			strQuery = " SELECT cPartida, dPartida FROM tCatalogoPartida WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCATALOGOTIPOGASTO")){
			strQuery = " SELECT cTipoGasto, dTipoGasto FROM tCatalogoTipoGasto WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCATALOGOTIPOGASTO_CONAC")){
			strQuery = " SELECT cTipoGasto, cClave, dTipoGasto FROM tCatalogoTipoGastoCONAC WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO")){
			strQuery = " SELECT cFuenteFinanciamiento, dFuenteFinanciamiento FROM tCatalogoFuenteFinanciamiento WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTOCONAC")){
			strQuery = " SELECT cFuenteFinanciamiento, cClave, cSubclave, dFuenteFinanciamiento  FROM dbo.tCatalogoFuenteFinanciamientoCONAC WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOENTIDADFEDERATIVA")){
			strQuery = " SELECT cEntidadFederativa, dEntidadFederativa,dEntidadFederativaCorto FROM tCatalogoEntidadFederativa WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("M_TCATALOGOCARTERA")){
			strQuery = " SELECT cCartera, dCartera, esMeta FROM tCatalogoCartera WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("TCATALOGOTIPOCLC")){
			strQuery = " SELECT cTipoCLC, dTipoCLC FROM tCatalogoTipoCLC WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOCAUSAAVISO_REIN")){
			strQuery = " SELECT aCausaAviso,cCausaAviso,dCausaAviso,eCausaAviso,fCausaAviso FROM tCatalogoCausaAviso WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOTIPOCAUSAAVISO_REIN")){
			strQuery = " SELECT cTipoCausaAviso,dTipoCausaAviso FROM tCatalogoTipoCausaAviso WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOFORMAPAGO_REIN")){
			strQuery = " SELECT cFormaPago,dFormaPago FROM tCatalogoFormaPago WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCATALOGOMOVIMIENTO_REIN")){
			strQuery = " SELECT mvto,activo,trans,tipo,grupo,subgrupo,transaccion,descripcion FROM tCatalogoMvto WITH(NOLOCK) ";
		}
		if (strTabla.equals("TCATALOGOMOVIMIENTO_RECT")){
			strQuery = " SELECT cTipoTRAN,cTipoMOVTO,dTipoMOVTO FROM tCatalogoTipoMovimiento WITH(NOLOCK) WHERE cTipoTRAN = 8";
		}
		if (strTabla.equals("M_TEJERCICIOFISCAL")){
			strQuery = " SELECT aEjercicioFiscal FROM tEjercicioFiscal WITH(NOLOCK) ";
	 	}
	 	
		if (strTabla.equals("CM_TEJERCICIOFISCAL")){
			strQuery = " SELECT * FROM tCatalogoTipoActivo WITH(NOLOCK) ";
		}
		
		if (strTabla.equals("M_TCUENTAS")){
			strQuery = " SELECT nCuenta, dCuenta,TipoCuenta,nCuentaPadre,TipoBalance,VerificaSaldo,NaturalezaCuenta,NivelCuenta,AplicacionCuenta,cSubcuenta,nCuentaLike,cuentaBloqueada FROM tCuentas WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("CG_CAT_TCUENTAS")){
			strQuery = " SELECT nCuenta, dCuenta,TipoCuenta,nCuentaPadre,TipoBalance,VerificaSaldo,NaturalezaCuenta,NivelCuenta,AplicacionCuenta,cSubcuenta,nCuentaLike,cuentaBloqueada FROM tCuentas WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("M_TCUENTASCONTAB")){
			strQuery = "SELECT nCuenta, dCuenta,TipoCuenta,nCuentaPadre,TipoBalance,VerificaSaldo,NaturalezaCuenta,NivelCuenta,AplicacionCuenta,cSubcuenta,nCuentaLike,nOrdenBalanza,nNivelBalanza,BloqueaCuenta FROM tCuentas WITH(NOLOCK) WHERE nCuenta NOT Like '8%' ";
	 	}
		if (strTabla.equals("CG_CAT_SUBCUENTA_COB")){
			strQuery = "SELECT cSubcuenta FROM tTipoSubcuenta WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("M_TTIPOSUBCUENTA")){
			strQuery = "SELECT cSubcuenta FROM tTipoSubcuenta WITH(NOLOCK) ";
	 	}
		if (strTabla.equals("M_TTIPOSUBCUENTACONF")){
			strQuery = "SELECT cSubcuenta,aEjercicioFiscal,nOrden, Interno, NombreCatalogo, NombreCampo FROM tTipoSubcuentaConf WITH(NOLOCK) ";
	 	}

		//if (strTabla.equals("M_TCATALOGORESTRICCIONES")){
		if (strTabla.equals("M_TVALIDAPNRGP")){	
			strQuery = "SELECT v.iNumeralID , v.nNivel, v.iNumeralID, v.cTipoGasto, v.cPartida, v.cMensaje " +
						"  FROM tValidaPNRGP       v WITH(NOLOCK) " +
						"     , tCatalogoPartida   p WITH(NOLOCK) " +
						"	 , tCatalogoTipoGasto t WITH(NOLOCK) " +
						"	 , tCatalogoNumeral   n WITH(NOLOCK) " +
						" WHERE v.cTipoGasto = t.cTipoGasto " +
						"   AND v.cPartida   = p.cPartida " +
						"   AND v.iNumeralID = n.iID ";
	 	}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_FUNCIONAL")){	
			strQuery = "SELECT reduce,amplia,nivel FROM tvalidacion_adecuacion_funcional ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_PROGRAMATICA")){	
			strQuery = "SELECT reduce,amplia,nivel FROM tvalidacion_adecuacion_programatica ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}
	 	
		if (strTabla.equals("M_TVALIDACION_ADECUACION_ECONOMICA")){	
			strQuery = "SELECT reduce,amplia,nivel FROM tvalidacion_adecuacion_economica ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}
	 	
		if (strTabla.equals("M_TVALIDACION_ADECUACION_CLAVE_GRUPO")){	
			strQuery = "SELECT tipo, clave, grupo FROM tvalidacion_adecuacion_clave_grupo ";
			if(strParam.equals("1=2")){
				strParam="";
			}
	 	}
	 	
	 	
	 	
		if (strTabla.equals("CG_CAT_PARTIDA_COB")){
			strQuery = " SELECT cPartida, dPartida FROM tCatalogoPartida WITH(NOLOCK) ";
	 	}
	 	
		if (strTabla.equals("CG_CAT_EP_COB")){
			strQuery =  " SELECT  rtrim(Ltrim(u.cUnidadResponsable)) as cUnidadResponsable ";
			strQuery += " FROM CG_CAT_EMPLEADO c WITH(NOLOCK) ";
			strQuery += "      , tCatalogoUnidadResponsable u WITH(NOLOCK) ";
			strQuery += "      , CG_CAT_AREAS a WITH(NOLOCK) ";
			strQuery += " WHERE c.ID_AREA = a.ID_AREA ";
			strQuery += "   AND a.ID_AREA_PADRE = u.ID_AREA ";
			strQuery += "   AND c.CE_OS_RESPONSABLE = '" + strUsuario + "' ";

			strParamQ = ObjC.ArrCatalogos(strQuery);

			if (strParamQ.length > 0){
				strWhere += " WHERE cUnidadEjecutora ='" + strParamQ[0][0].toString() + "'";
			}
			strQuery = " SELECT  nCuenta, cSubCuenta, mSaldoArrastre, cUnidadEjecutora, nClaveCNA, nMes ";
            strQuery += "  FROM  vConsultaSaldos2012 WITH(NOLOCK) ";
            strQuery += strWhere;
	 	}
		if (strTabla.equals("CG_CAT_FUENTEFINANCIAMIENTO_COB")){
			strQuery = " SELECT cFuenteFinanciamiento, dFuenteFinanciamiento FROM tCatalogoFuenteFinanciamiento WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("M_TCATALOGOCOMPLEJIDADFF")){
	 		strQuery = " SELECT cReglasFF, cFuenteFinanciamientoOrigen, cFuenteFinanciamientoDestino, nNivelReglaFF FROM tCatalogoComplejidadFF WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("CG_CAT_PPC_COB")){
	 		strQuery = " SELECT cProgramaPresupuestario, dProgramaPresupuestario FROM  tCatalogoProgramaPresupuestario WITH(NOLOCK) ";
	 	}
	 			//DE IRD
		if (strTabla.equals("TCONTRATODIVERSOFACTURADOCCOMPREAD")){
	 		strQuery = " select DCD_FACTURA,fAplicacion DCD_FECHA_FACTURA,DCD_TBEN,DCD_CBEN,DCD_TIPO_OPE,dcd_tiva,DCD_IMP_BRUTO,DCD_IVADES,DCD_IVA,DCD_ISR,DCD_MIL5,DCD_MIL2,DCD_CONTRIBUCION,DCD_OTRAS_RET,DCD_PENALIZACION FROM v_pagosDocComprobatoria WITH(NOLOCK) ";
	 	}

		if (strTabla.equals("TDOCUMENTACIONCOMPROBATORIADETREAD")){
	 		strQuery = " select dc.DCD_FACTURA, convert(varchar(10), dc.fAplicacion, 103) DCD_FECHA_FACTURA, dc.DCD_TBEN, dc.DCD_CBEN, dc.DCD_TIPO_OPE,ti.valor, dc.DCD_IMP_BRUTO, DCD_IVADES, DCD_IVA, DCD_ISR, DCD_MIL5, DCD_MIL2, DCD_CONTRIBUCION, DCD_OTRAS_RET, DCD_PENALIZACION FROM v_pagosDocComprobatoria dc WITH(NOLOCK), CAT_TIPO_IVA ti WITH(NOLOCK) WHERE dc.dcd_tiva = ti.tiva ";
	 	}
		if (strTabla.equals("TCONTRATODIVERSOFACTURARETENCIONREAD")){
	 		strQuery = " select r.cIdTipoRetencion, cTipoRetencion, nPorcRetencion, mImporteRetencion, EP, isnull(nMes,0) nMes from tPAgoDiversoRetencion r WITH(NOLOCK), pCatalogoTipoRetencion tr WITH(NOLOCK) WHERE tr.cIdTipoRetencion = r.cIdTipoRetencion ";
	 	}
		if (strTabla.equals("TCONTRATODIVERSOFACTURADETALLER")){
	 		strQuery = " select * FROM v_movsfacturadiversos WITH(NOLOCK) ";
	 	}

		if (strTabla.equals("TIPORFCBENEFICIARIO")){
	 		strQuery = " SELECT cat.cTipoRFC, cat.cIdTipoRFC FROM pCatalogoTipoRFC cat WITH(NOLOCK),tBeneficiarioTipoRFC rel WITH(NOLOCK) WHERE cat.cIdTipoRFC = rel.cIdTipoRFC ";
	 	}

		if (strTabla.equals("UECUENTASBANCARIAS")){
	 		 strQuery = " SELECT DISTINCT strUnidadEjecutora,strRFC, strNombreBeneficiario, strClabe, strTipoCuenta FROM tUECuentasBancarias WITH(NOLOCK) ";
	 	}
		
		if (strTabla.equals("UECUENTASBANCARIAS_LAYOUTRG")){
	 		 strQuery = " SELECT DISTINCT strUnidadEjecutora,strRFC, strNombreBeneficiario, strClabe, strTipoCuenta FROM vUECuentasBancarias_LayOutRG WITH(NOLOCK) ";
	 	}

	 	if (strTabla.equals("BENEFICIARIOCUENTASBANCARIAS")){
	 		 strQuery = " SELECT dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico,nBCBEnviadoSICOP FROM tBeneficiarioCuentasBancarias WITH(NOLOCK)  WHERE cStatusCuenta = 1  AND nBCBEnviadoSICOP = 1 ";
	 	}
	 	if (strTabla.equals("BENEFICIARIOCUENTASBANCARIAS2")){
	 		 strQuery = " SELECT dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico,nBCBEnviadoSICOP FROM tBeneficiarioCuentasBancarias WITH(NOLOCK)  ";
	 	}
	 	if (strTabla.equals("BENEFICIARIOCUENTASBANCARIASTMP")){
	 		 strQuery = " SELECT dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico,nBCBEnviadoSICOP FROM tBeneficiarioCuentasBancariasTmp WITH(NOLOCK)  ";
	 	}
	 	if (strTabla.equals("CUENTASBANCARIASRADICADO")){
	 		 strQuery = " SELECT dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico,nBCBEnviadoSICOP FROM vCuentasBancoRadicado WHERE cStatusCuenta = 1 AND nBCBEnviadoSICOP = 1 ";
	 	}
	 	
	 	if(strTabla.equals( "BUSCA_PROVEEDOR_BENEFICIARIO" ) ){
					strQuery = "SELECT A.* "
								+ "FROM   (SELECT ALTA.cfolio, "
								+ "ALTA.cidrfc, "
								+ "CASE ALTA.cidtipopersona "
								+ "WHEN 1 THEN 'P.MORAL' "
								+ "WHEN 2 THEN 'P.FISICA' "
								+ "WHEN 3 THEN 'EMPLEADO' "
								+ "END AS Persona,"
								+ "ALTA.ctiporegistro, "
								+ "cat.cEstatusSICOP,  "
								+ "BEN.nEnviadoSICOP "
								+ "FROM   taltaproveedor ALTA with (nolock) "
								+" LEFT JOIN tBeneficiario BEN WITH (NOLOCK) "
								+" ON REPLACE(ALTA.cIdRFC,'-','')=dRFC "
								+" left join tCatEstatusEnviadoSICOP cat WITH (NOLOCK) "
								+" on cat.nEnviadoSICOP=BEN.nEnviadoSICOP "
								+ "WHERE  cdocumentohaplicado = 'S' and BEN.nEnviadoSICOP>0 "
								+" and 1=isnull((select  top 1 nBCBEnviadoSICOP from tBeneficiarioCuentasBancarias  with (nolock) where dRFC=replace(ALTA.cIdRFC,'-','') and nBCBEnviadoSICOP=0 ),1) "
								+" )AS A "
								+ "WHERE  1 = 1";

						
		}

		if (strTabla.equals("TPAGODIRECTOFACTURADETALLEREAD")){	 		
	 		strQuery = strQuery + "select ROW_NUMBER() OVER(ORDER BY tr.ep)/*nDocRenglon*/ as movto, tr.EP, TR.ID_TIPO_CONCEPTO TIPO_CONCEPTO, ISNULL(A.cAlmacen, ''), "; //URVP.09062014 se cambia "ROW_NUMBER() OVER(ORDER BY tr.ep)" por nDocRenglon para que muestre el renglon real y se agrega al Group By 
	 		strQuery = strQuery + "case TR.altaAlmacen when '0||' then '' else TR.altaAlmacen end as altaAlmacen, SUM(tr.mImporteMasIva) mComprometido, tr.nFolioPagoDirecto ";
 			strQuery = strQuery + "from tPagoDirectoDetalle tr LEFT JOIN CAT_ALMACEN A ON ALM = A.nIdAlmacen AND TR.cCentroContable = A.cCentroContable ";
 			strQuery = strQuery + "WHERE " + strMaxReg;
 			strQuery = strQuery + " group by /*nDocRenglon,*/ tr.EP, TR.ID_TIPO_CONCEPTO, TR.ALM, A.cAlmacen, TR.altaAlmacen, tr.nFolioPagoDirecto";
			
	 		//strQuery = " select * FROM v_movsfacturadirecto WITH(NOLOCK) ";
	 	}

	 	if (strTabla.equals("TCONTRATOOBRAFACTURADETALLE")){
	 		strQuery = " select * FROM tContratoObraFacturaDetalle WITH(NOLOCK) WHERE mmovimiento != 0 ";
	 	}

	 	//vista Cancelacion de documentos Rectificacion
		if (strTabla.equals("TRECTIFICACIONAUTENCABEZADO")){
		    strQuery =  " SELECT " ;
			strQuery += " * ";
			strQuery += " FROM ";
			strQuery += " tRectificacionAutEncabezado r WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
			
		}
		
		if (strTabla.equals("TPAGOANTICIPADODETALLE"))
		{
			strQuery =  " SELECT Ep, mImporte ";
			strQuery += " FROM  ";
			strQuery += " tPAnticipadoDetalle WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("TPAGOANTICIPADODESCRIPCION")){
			strQuery =  " SELECT Ep, NDocRenglon, descripcion, importe ";
			strQuery += " FROM  ";
			strQuery += " tPAnticipadoDescripcion WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("VRECTIFICACIONAUTDETALLE")){
			strQuery =  " SELECT * ";
			strQuery += " FROM  ";
			strQuery += " VRECTIFICACIONAUTDETALLE R WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		
		
		if (strTabla.equals("VREINTEGROAUTDETALLE")){
			strQuery =  " SELECT * ";
			strQuery += " FROM  ";
			strQuery += " VREINTEGROAUTDETALLE R WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("VRECTIFICACIONAUTDETALLEMIL")){
			strQuery =  " SELECT * ";
			strQuery += " FROM  ";
			strQuery += " VRECTIFICACIONAUTDETALLEMIL R WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("ADECUACIONDETALLEFIAF")){
		 	strQuery =  " SELECT nFolioAdecuacion,cTipoAdecuacion,nNivel,importe,U_LOGIN,fCarga,cUnidadResponsable";
			strQuery += " FROM  ";
			strQuery += " v_AdecuacionDetFIAF WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("ADECUACIONENCABEZADOFIAF")){
		 	strQuery =  " SELECT nFolioAdecuacion,cTipoAdecuacion,nNivel,importe,U_LOGIN,fCarga,cUnidadResponsable";
			strQuery += " FROM  ";
			strQuery += " v_AdecuacionEncFIAF WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("FIAFENCABEZADO")){
		 	strQuery =  " SELECT *";
			strQuery += " FROM  ";
			strQuery += " tFIAFEncabezado WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("ADECUACIONENCABEZADO")){
		 	strQuery =  " SELECT *";
			strQuery += " FROM  ";
			strQuery += " tAdeacuacionEncabezado WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("VREINTEGROAUTDETALLEMIL")){
			strQuery =  " SELECT * ";
			strQuery += " FROM  ";
			strQuery += " VREINTEGROAUTDETALLEMIL R WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("TSUBSIDIODETALLE"))
		{
			strQuery =  " SELECT EP,mEnero, mFebrero, mMarzo, mAbril, mMayo, mJunio, mJulio, mAgosto, mSeptiembre, mOctubre, mNoviembre, mDiciembre,mImporte";
			strQuery += " FROM  ";
			strQuery += " tSubsidiosDetalle WITH(NOLOCK) ";
			strQuery += strWhere;
			System.out.println("Cadena: " + strQuery );
		}
		
		if (strTabla.equals("TSUBSIDIODETALLEANEXO"))
		{
			strQuery ="SELECT ROW_NUMBER() OVER( ORDER BY a.fInicio DESC ),(a.estado +' '+ b.EDO_NOMBRE )as estado,(a.municipio+' '+c.MPO_NOMBRE )as municipio,(localidad+' '+ d.LCD_NOMBRE)as localidad, ";
	   		strQuery +=	"a.otro,a.fInicio,a.fFin,a.fConvenio,a.mImporte,a.mEstatal,a.mFederal,a.mMunicipal,a.mOtro,a.accionMetas,a.unidadMetas,a.cantidadMetas " ; 
 			strQuery += " FROM  tSubsidiosDetalleAnexo a WITH(NOLOCK) ";
			strQuery += " JOIN  CAT_ESTADOS b WITH(NOLOCK) ";
			strQuery +=  " ON cast(a.estado as int)=b.ID_ESTADO  ";
 			strQuery += " JOIN CAT_MUNICIPIO c WITH(NOLOCK) ";
 			strQuery += " ON cast (a.municipio as int)=c.ID_MUNICIPIO ";
 			strQuery += " JOIN CAT_LOCALIDAD d WITH(NOLOCK) ";
 			strQuery += " ON cast(a.localidad as int)=d.ID_LOCALIDAD AND d.ID_MUNICIPIO=c.ID_MUNICIPIO";
			strQuery += strWhere;
			
			System.out.println("Cadena Prueba------------------------: " + strQuery );
		}
		
	 	if (strTabla.equals("TCATALOGOEPDIVERSO")){
	 		strQuery = "select * from v_TCATALOGOEPDIVERSO WITH(NOLOCK) ";
	 	}

	 	//AGREGADO
	 	if (strTabla.equals("TCATALOGOEPOBRA")){
	 		strQuery = " SELECT * FROM vConsultaCompromiso2 WITH(NOLOCK)  ";
	 	}


	 	if (strTabla.equals("CONSULTASALDOS2012")){
	 		int laPos =0;
	 		laPos = strParam.indexOf("TIPO_CONCEPTO LIKE '")+20;
	 		String elTipo = "";
	 		elTipo = strParam.substring(laPos, laPos+2);
	 		strQuery = " select nClaveCNA, cSubcuenta, nCuentaP, montoanual, [MontoEnero], [MontoFebrero], [MontoMarzo], [MontoAbril], [MontoMayo], [MontoJunio], [MontoJulio], [MontoAgosto], [MontoSeptiembre], [MontoOctubre], [MontoNoviembre], [MontoDiciembre], TIPO_CONCEPTO, cUnidadEjecutora from fn_saldosAnualesxTConc('" + elTipo + "')  ";

	 	}

	 	if (strTabla.equals("SALDOS_DISPONIBLE_PAGO")){
	 		strQuery = " select nMes,CLAVESIAFF,SALDO FROM fn_SaldoDisponiblealMes(" + campos + " ) WHERE saldo > 0 ";

	 	}
		if (strTabla.equals("SALDOS_DISPONIBLE_PAGO_RADICADO")){
	 		strQuery = " select nMes,CLAVESIAFF,SALDO FROM fn_SaldoDisponiblealMes_Radicado(" + campos + " ) WHERE saldo > 0 ";
	 	}
	 	if (strTabla.equals("SALDOS_DISPONIBLE_PAGOOBRA")){

			strQuery = " select nMes,CLAVESIAFF,SALDO FROM fn_saldoComprObraalMes_N(" + campos + " ) WHERE saldo > 0 ";

	 	}

 	 	if (strTabla.equals("CARGA_ARCHIVO")){
 			strQuery = " exec.dbo.facturaNomina " + campos + "";
			makeCommit = true;
	 	}

	 	 if (strTabla.equals("CCARGA_ARCHIVO_DETALLE")){

	 		strQuery = " exec.dbo.facturaNominaProceso " + campos + "";
			System.out.println(strQuery);
	 	}

	 	if (strTabla.equals("VDISPONIBLEEP")){
	 		strQuery = "select nClaveCNA,ClaveSIAFF,ClaveInterna," + campos + " from vDisponibleEP WITH(NOLOCK) " + strMaxReg;
	 		System.out.println(strQuery);

	 	}
	 	
	 	if (strTabla.equals("VDISPONIBLEEPREGINGRESOS")){
	 		strQuery = "select nClaveCNA,ClaveSIAFF,ClaveInterna," + campos + " from vDisponibleEPIngresos WITH(NOLOCK) " + strMaxReg;
	 		System.out.println(strQuery);

	 	}
	 	
	 	if (strTabla.equals("VDISPONIBLEEPFF")){
	 		strQuery = "SELECT nClaveCNA, ClaveSIAFF ,ClaveInterna, mDisponible"
					  +" FROM " 
					   +"(SELECT  nClaveCNA,ClaveSIAFF ,ClaveInterna,"+ campos 
					    +" from vDisponibleEPIngresos WITH(NOLOCK) "
						+" "+strMaxReg+" ) p "
					 +"UNPIVOT"
					   +"(mDisponible FOR cSubCuenta IN" 
					   +"("+campos+")"
				 	+")AS unpvt";
	 		System.out.println(strQuery);
	 	}
	 	
	 	if (strTabla.equals("VDISPONIBLEEP_RADICADO")){
	 		strQuery = "select nClaveCNA,ClaveSIAFF,ClaveInterna," + campos + " from vDisponibleEP_Radicado WITH(NOLOCK) " + strMaxReg;
	 		System.out.println(strQuery);
	 	}

	 	//POLIZA
	 	if (strTabla.equals("CONSULTAPOLIZA")){
	 		strQuery = " select * from ( select nCuenta,dCuenta,isnull (cSubcuenta,' ') cSubcuenta  from tCuentas c  WITH(NOLOCK) where  not (c.nNivelBalanza between 1 and 4)  AND  not(c.TipoBalance='P' and c.TipoCuenta='P') AND AplicacionCuenta='S'  ) as tbl ";
	 		System.out.println(strQuery);
	 	}
	 	if (strTabla.equals("BUSCARMOV_POLIZA")){
	 		strQuery = 
					 "SELECT nfoliopoliza, " 
					+"       cdescripcionpoliza, " 
					+"       fcreacion, "
					+"       faplicacion, " 
					+"       mtotalcargo, "
					+"       dochaplicado, "
					+"       ctipopoliza, "
					+"       ctipodocumento, " 
					+"       ccentrocontable "
					+"FROM   tpoliza  WITH(NOLOCK) " 
					+"WHERE " + strParam;
	 	}
	 	if (strTabla.equals("BUSCARMOV_POLIZAMANUAL")){
	 		strQuery = " SELECT nFolioDocPoliza,cConcepto,fCarga,fAplicacion,mTotalCargos,cDocumentoHaplicado,cTipoPoliza,'POLIZA MANUAL' cTipoDocumento,cCentroContable FROM tDocPolizaEncabezado WITH(NOLOCK) WHERE " + strParam;

	 	}
	 	if (strTabla.equals("TPOLIZAMOVIMIENTOS")){
	 		strQuery = " SELECT tm.nCuenta,tm.nSubCuenta,tm.cTipoMovimiento,tm.mMovimiento,tc.dCuenta,tm.cDescripcionMovPol FROM tMovimiento tm WITH(NOLOCK), tCuentas tc WITH(NOLOCK)	WHERE  tm.nCuenta=tc.nCuenta AND  "+ strParam;

	 	}
	 	if (strTabla.equals("TDOCPOLIZAMOVIMIENTOS")){
	 		strQuery =  
					"SELECT	tpd.nCuenta, " +
					"      	tpd.nSubCuenta, " +
					"      	tpd.cEvento, " +
					"      	'$'+CONVERT(VARCHAR,tpd.mImporte, 1) mImporte, " +
					"      	tc.dCuenta, " +
					"      	tpd.cConcepto, " +
					"      	tpd.parcial " +
					"  FROM	tDocPolizaDetalle tpd WITH(NOLOCK), " +
					"      	tCuentas tc WITH(NOLOCK)  " +
					" WHERE	tc.nCuenta=tpd.nCuenta  " +
					"   AND	" + strParam;

	 	}
	 	
	 	//Ing J. Luis DR
	 	//************* POLIZA MANUAL con eventos **************************** 	
	 	if (strTabla.equals("TDOCPOLIZAMOVIMIENTOS_EVENTO")){
	 		strQuery =  
					"SELECT	tpd.nCuenta, " +
					"      	tpd.nSubCuenta, " +
					"      	tpd.cEvento, " +
					//"      	'$'+CONVERT(VARCHAR,tpd.mImporte, 1) mImporte, " +
					"       tpd.mImporte , " +
					"      	tc.dCuenta, " +
					"      	tpd.cConcepto, " +
					"      	tpd.parcial, " +
					"      	tpd.nIdGrupoEvento, " +
					"      	tpd.nIdSubGrupoEvento, " +
					"      	tpd.cIdEventoManual, " +
					"      	tpd.cPartida, " +
					"      	tpd.cCABMS, " +
					"      	tpd.cCUCOP, " +					
					"      	tpd.nNumeroEvento " +
					"  FROM	tDocPolizaDetalle tpd WITH(NOLOCK), " +
					"      	tCuentas tc WITH(NOLOCK)  " +
					" WHERE	tc.nCuenta=tpd.nCuenta  " +
					"   AND	" + strParam +" order by nNumeroEvento,cEvento desc";
					System.out.println("EL QUERY:  "+strQuery);
					strParam="";

	 	}	 	
	 	//********************************************************************
	 	
	 	/*
	 	 * Ayuda Catalogo Meses Contables para Poliza
	 	 */
		if( strTabla.equals("M_CAT_MESES_CONTABLES")){
			strQuery =
				"SELECT	nmes, mes, cCentroContable, "
				+ "		cdescripcion, fcierre, usuarioCerro, "
				+ "		aejerciciofiscal,mesAbierto, cUnidadResponsable "
				+ "FROM	vMesesContables WITH(NOLOCK) ";
		}

	 	if (strTabla.equals("TCATALOGOEP")){
	 		strQuery = " select TOP " + strMaxReg + " nClaveCNA, EP from tCatalagoEP WITH(NOLOCK) WHERE ep in (select distinct csubcuenta from tsaldos WITH(NOLOCK) WHERE ncuenta like '82103%') ";
	 		strQuery = " select distinct ep, ep from tCompromisoEncabezado ce WITH(NOLOCK), PcontratoDiverso CO WITH(NOLOCK), tCompromisoDetalle cd WITH(NOLOCK) WHERE cd.nFolioCompromiso = ce.nFolioCompromiso and co.cidcontrato = ce.cidcontrato and nEnviadoSICOP = 2 ";
	 	}
	 	if (strTabla.equals("SALDOS_COMPROMISO")){
	 		strQuery = " select * from [dbo].[fn_saldoCompralMes]("+strMaxReg+") order by cuenta desc ";
	 		System.out.println(strQuery);
	 	}
	 	if (strTabla.equals("SALDOS_OBRA")){
	 		strQuery = " select * from [dbo].[fn_saldoComprObraalMes]("+strMaxReg+") ";
	 	}
	 	if (strTabla.equals("PORCENTAJEIVA")){
	 		strQuery = " select ti.tiva, descripcion_20, nPorcIVA from conagua2011.dbo.pporcentajeiva piva, conagua2011.dbo.cat_tipo_iva ti WHERE ti.tiva = piva.tiva  ";
	 	}
	 	if (strTabla.equals("CALC_RETENCIONES_FACT_CONT_DIVERSO")){
			strQuery = "select distinct tr.cidtiporetencion, cTipoRetencion, cast(round(1*nporcretencion,6) as decimal(12,6)) " 
			         + "  from pContratoDiversoRetencion r WITH(NOLOCK), " 
			         + "       pCatalogoTipoRetencion tr WITH(NOLOCK)  " 
			         + " WHERE tr.cidtiporetencion = r.cidtiporetencion  " 
			         + "   and " + strParam.toUpperCase() + "  ";
	 	}
	 	if (strTabla.equals("CALC_RETENCIONES_FACT_PAGO_DIRECTO")){
	 		strQuery = "select distinct tr.cidtiporetencion, cTipoRetencion, cast(round(1*nporcretencion,6) as decimal(12,6)) from tPagoDirectoRetencion r WITH(NOLOCK), pCatalogoTipoRetencion tr WITH(NOLOCK) WHERE tr.cidtiporetencion = r.cidtiporetencion and " + strParam.toUpperCase() + "  ";
	 	}
		//DE IRD

	 	if (strTabla.equals("CO_FACT_RETENCION")){
	 		strQuery = " select cIdTipoRetencion,mImporteRetencion from tContratoObraFacturaRetencion WITH(NOLOCK) ";
	 	}

	 	if (strTabla.equals("CG_CAT_OGTOC_COB")){
	 		strQuery = " SELECT cPartida, dPartida FROM  tCatalogoPartida WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("CG_CAT_TGD_COB")){
	 		strQuery = " SELECT cTipoGasto, dTipoGasto FROM tCatalogoTipoGasto WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("CG_CAT_FFC_COB")){
	 		strQuery = " SELECT  cFuenteFinanciamiento, dFuenteFinanciamiento FROM tCatalogoFuenteFinanciamiento WITH(NOLOCK) ";
	 	}
	 	if (strTabla.equals("CG_CAT_UED_COB")){
	 		strQuery = " SELECT      ID_AREA, D_DESCRIPCION FROM CG_CAT_AREAS WITH(NOLOCK) ";
	 	}
	 	//Relacion de Gastos
	if (strTabla.equals("PFACTURARELACIONGASTO")){

			strQuery =  "SELECT TOP " + strMaxReg + " cIdRFC_RelacionGasto,cnombre ,cIdTipoPersonaRFC DCD_TBEN,CBEN DCD_CBEN  FROM v_PCATALOGORFC WITH(NOLOCK)  ";
		}

	//contra de obra			 DUBOIS aqui me quede
	 	if (strTabla.equals("PCONTRATOOBRA")){
	 		strQuery = " select cIdContrato, cIdRFC, cobjetocontrato, nPorcIVAAplicable, mTotal, Descrip_Concepto, mImporteBruto, mImporteAnticipoIVA, " +
	 		      " nFolioCompromiso, lHaySaldoAnticipo, CamInst, cSuspensionPago, cValidaEstimacion, IEsPasivo " +
	 		  	  " FROM vpContratoObraComp " ;

	 		strWhere += " WHERE cCentroContable = '" + cCentroContable + "'";

			strQuery += strWhere;

	 	}
	 	
		if (strTabla.equals("PCONTRATOS")){  
			StringBuilder query = new StringBuilder();
			query.append("SELECT	* ");
			query.append("  FROM	(");
			query.append("SELECT  cnt.cIdContrato AS cIdContrato,");
			query.append("        cnt.cIdRFC AS cIdRFC, ");
			query.append("        beneficiario.dNombre + ISNULL(' ' + beneficiario.dApellidoPaterno, '') + ISNULL(' ' + beneficiario.dApellidoMaterno, '') AS cobjetocontrato, ");
			query.append("        mTotal  AS mImporteTotal, ");
			query.append("        'OB' AS cIdTipoContrato ");
			query.append("  FROM  pContratoObra cnt WITH(NOLOCK) ");
			query.append("        INNER JOIN  ");
			query.append("        tBeneficiario beneficiario WITH(NOLOCK) ");
			query.append("        ON  ");
			query.append("        cnt.cIdRFC = beneficiario.dRFC ");
			query.append("UNION ");
			query.append(" SELECT  cnt.cIdContrato AS cIdContrato,");
			query.append("        cnt.cIdRFC AS cIdRFC, ");
			query.append("        beneficiario.dNombre + ISNULL(' ' + beneficiario.dApellidoPaterno, '') + ISNULL(' ' + beneficiario.dApellidoMaterno, '') AS cobjetocontrato, ");
			query.append("        mImporteTotal  AS mImporteTotal,");
			query.append("        'DI' AS cIdTipoContrato ");
			query.append("  FROM  pcontratodiverso cnt WITH(NOLOCK) ");
			query.append("        INNER JOIN  ");
			query.append("        tBeneficiario beneficiario WITH(NOLOCK) ");
			query.append("        ON  ");
			query.append("        cnt.cIdRFC = beneficiario.dRFC ");
			query.append(") AS tbl ");			
	 		strQuery =query.toString();

			strQuery += strWhere;

	 	}
		
		
	 	if (strTabla.equals("PCONTRATOFEDERALIZADO")){
	 		strQuery =  " SELECT  cIdContrato, cIdRFC, cobjetocontrato, nPorcIVAAplicable, mTotal, Descrip_Concepto, mImporteBruto, " +  
			 			" mImporteAnticipoIVA, nFolioCompromiso, lHaySaldoAnticipo, CamInst " +
			 			" FROM vpContratoFederalizadoComp WITH(NOLOCK) ";
			 		
	 		strWhere += " WHERE cCentroContable = '" + cCentroContable + "'";

			strQuery += strWhere;

	 	}

		if (strTabla.equals("PCONTRATODIVERSO")){
			strQuery = " SELECT  cIdContrato, cIdRFC, cobjetocontrato, nPorcIVAAplicable, mImporteTotal, Descrip_Concepto, mImporteBruto, " +
				" mImporteAnticipoIVA, nFolioCompromiso, lHaySaldoAnticipo, IEsPasivo,cCentroContable " + 
				" FROM vpContratoDiversoComp2 WITH (NOLOCK)" ;
			if ( !( uUR.equals("A02") || uUR.equals("A04") ) ){
				//strWhere += " WHERE cUnidadResponsable =  '" + uUR + "'"; //URVP.12012014 SE CAMBIA LA CONDICION DEL CC POR UR --> "WHERE cCentroContable = '" + cCentroContable + "'";
				strWhere += " WHERE ( cUnidadResponsable = '" + uUR + "' OR cUnidadResponsable IN (SELECT ur FROM dbo.tVistasUR WITH (NOLOCK) WHERE  modulo='MATERIALES' AND usuario = '" +strUsuario+"') )";
			}
			strQuery += strWhere;
	 	}
	 	
		if (strTabla.equals("PCONTRATODIVERSOE")){
			strQuery = " SELECT  cIDContratoExcepcion, cIdRFC, cobjetocontrato, nPorcIVAAplicable, mImporteTotal, Descrip_Concepto, mImporteBruto, " +
				" mImporteAnticipoIVA, nFolioCompromiso, lHaySaldoAnticipo, IEsPasivo,cCentroContable " + 
				" FROM vContratoDiversoCompromiso WITH (NOLOCK)" ;
			if ( !( uUR.equals("A02") || uUR.equals("A04") ) ){
				//strWhere += " WHERE cUnidadResponsable =  '" + uUR + "'"; //URVP.12012014 SE CAMBIA LA CONDICION DEL CC POR UR --> "WHERE cCentroContable = '" + cCentroContable + "'";
				strWhere += " WHERE ( cUnidadResponsable = '" + uUR + "' OR cUnidadResponsable IN (SELECT ur FROM dbo.tVistasUR WITH (NOLOCK) WHERE  modulo='MATERIALES' AND usuario = '" +strUsuario+"') )";
			}
			strQuery += strWhere;
	 	} 
		
		if (strTabla.equals( "v_contratosAutPagos".toUpperCase() )){
			strQuery = "SELECT  cIdContrato , "
					 + "        cIdRFC , "
					 + "        cRazonSocial "
					 +"  FROM	v_contratosAutPagos WITH(NOLOCK)" ;
				
				if ( !( uUR.equals("A02") || uUR.equals("A04") ) ){
					strWhere += " WHERE ( cIdUnidadAdministrativa = '" + uUR + "' OR cIdUnidadAdministrativa IN (SELECT ur FROM tVistasUR WITH (NOLOCK) WHERE  modulo='MATERIALES' AND usuario = '" +strUsuario+"') )";
				}
			
			strQuery += strWhere;
			
	 	}


	 	if (strTabla.equals("CO_FACT_RETENCION")){
	 		strQuery = " select cIdTipoRetencion,mImporteRetencion from tContratoObraFacturaRetencion ";
	 	}
	 	if (strTabla.equals("CUENTABANCPOLIZA")){
		 	strQuery = " select tb.dRFC,dBanco,dSucursal, dCuentaBancaria CTAB from tBeneficiarioCuentasBancarias tbb WITH(NOLOCK)   inner join tBeneficiario tb WITH(NOLOCK)  on tbb.dRFC=tb.dRFC ";
	 	}

		if (strTabla.equals("ALMPOLIZA")){
			
			strQuery =  " SELECT  ";
				strQuery += " nIdAlmacen,	cAlmacen , cCentroContable";
				strQuery += " FROM ";
				strQuery += " CAT_ALMACEN WITH(NOLOCK) ";
				strQuery += " WHERE	cCentroContable = '" + cCentroContable + "' ";
				System.out.println(strQuery);
				
		}

	if (strTabla.equals("CTABANCPOLIZA")){
		strQuery = 
			"SELECT	ROW_NUMBER() OVER (ORDER BY Clabe) AS idCuenta, "+ 
			"		clabe,  " +
			"		beneficiario, " + 
			"		rfc,  " +
			"		ccentrocontable " +
			"FROM	(		 " +
			"			SELECT	DISTINCT " +
			"					strClabe AS Clabe " +
			"			,		strNombreBeneficiario AS Beneficiario " +
			"			,		strRFC AS RFC " +
			"			,		strCentroContable AS cCentroContable " +
			"			FROM	tUECuentasBancarias WITH(NOLOCK)  " +
			"			WHERE	strCentroContable = '" + cCentroContable + "' " +
			"		) AS t WHERE 1=1 ";
	 }
	 
	 
	 if (strTabla.equals("CTABAN_SNP")){
		 strQuery = " exec.dbo.sp_CuentasBancarias_syc "+ cCentroContable + " ," + uUR;

	 }
	 
	 if (strTabla.equals("CTABAN_FFM")){
		 strQuery = " exec.dbo.sp_CuentasBancarias_FFM "+ cCentroContable + " ," + uUR;

	 }
	 
	 if (strTabla.equals("CTA_TODAS")){
		 strQuery = " exec.dbo.sp_CuentasBancarias_TODAS "+ cCentroContable + " ," + uUR;

	 }
	 
	 if( strTabla.equals("CTABANCPOLIZARED") ){
	 	strQuery = 
				  " SELECT	ROW_NUMBER() OVER (ORDER BY strClabe) AS id "
				+ " ,		strClabe AS Clabe                           "
				+ " ,		strNombreBeneficiario as Beneficiario       "
				+ " ,		strRFC AS RFC                               "
				+ " ,		strUnidadEjecutora as UE                    "
				+ " FROM	tUECuentasBancarias                         "
				+ " WHERE	strCentroContable = '" + cCentroContable + "'";
	 }

	if (strTabla.equals("CALC_RETENCIONES_FACT_CONT_OBRA")){
 		strQuery = " select distinct tr.cidtiporetencion, cTipoRetencion, cast(round(1*nporcretencion,6) as decimal(12,6)) from pContratoObraRetencion r, pCatalogoTipoRetencion tr WHERE tr.cidtiporetencion = r.cidtiporetencion and " + strParam.toUpperCase() + "  ";
 	}

	if (strTabla.equals("CALC_RETENCIONES_FACT_CONT_FED")){
 		strQuery = " select distinct tr.cidtiporetencion, cTipoRetencion, cast(round(1*nporcretencion,6) as decimal(12,6)) from pContratoFEDERALIZADORetencion r, pCatalogoTipoRetencion tr WHERE tr.cidtiporetencion = r.cidtiporetencion and " + strParam.toUpperCase() + "  ";
 	}

	if (strTabla.equals("CLAVES_RELACION_GASTO")){
	 		strQuery = strQuery + "select ROW_NUMBER() OVER(ORDER BY tr.ep) as movto, tr.EP, TR.ID_TIPO_CONCEPTO TIPO_CONCEPTO, ISNULL(A.cAlmacen, ''), ";
	 		strQuery = strQuery + "case TR.altaAlmacen when '0||' then '' else TR.altaAlmacen end as altaAlmacen, SUM(tr.mImporteMasIva) mComprometido, tr.nFolioRELACIONGASTOS, ISNULL(ctab.strNombreBeneficiario,'N/A') ,ISNULL(tr.CTAB,'N/A'), tr.mImporteHospedaje ";
 			strQuery = strQuery + "from tRELACIONGASTOSDetalle tr WITH(NOLOCK)  LEFT JOIN CAT_ALMACEN A WITH(NOLOCK)  ON ALM = A.nIdAlmacen AND TR.cCentroContable = A.cCentroContable ";
 			strQuery = strQuery + "INNER JOIN dbo.tRELACIONGASTOSEncabezado enc WITH(NOLOCK)  ON enc.nFolioRELACIONGASTOS = tr.nFolioRELACIONGASTOS ";
 			strQuery = strQuery + "LEFT OUTER JOIN tUECuentasBancarias ctab WITH(NOLOCK)  ON tr.CTAB=ctab.strClabe AND enc.cUnidadResponsable=ctab.strUnidadEjecutora ";
 			strQuery = strQuery + "WHERE " + strMaxReg;
 			strQuery = strQuery + " group by tr.EP, TR.ID_TIPO_CONCEPTO, TR.ALM, A.cAlmacen, TR.altaAlmacen, tr.nFolioRELACIONGASTOS, ctab.strNombreBeneficiario, tr.CTAB, tr.mImporteHospedaje";
	 	}
	
	if (strTabla.equals("CLAVES_OBRA")){
 		strQuery = "select DISTINCT tr.EP,vs.ClaveInterna,vs.nClaveCNA, tr.mComprometido,TR.ID_TIPO_CONCEPTO TIPO_CONCEPTO,TR.ID_TIPO_MOVIMIENTO TIPO_MOVIMIENTO from tPAGOOBRADetalle tr WITH(NOLOCK) , vSaldosAnuales vs  WHERE vs.EP=tr.EP ";

 	}
	if (strTabla.equals("CLAVES_FED")){
 		strQuery = "select DISTINCT tr.EP,vs.ClaveInterna,vs.nClaveCNA, tr.mComprometido,TR.ID_TIPO_CONCEPTO TIPO_CONCEPTO,TR.ID_TIPO_MOVIMIENTO TIPO_MOVIMIENTO from tPAGOFEDERALIZADODetalle tr WITH(NOLOCK) , vSaldosAnuales vs  WHERE vs.EP=tr.EP ";

 	}
	if (strTabla.equals("CLAVES_DIVERSO")){
	 		strQuery = "select DISTINCT tr.EP,vs.ClaveInterna,vs.nClaveCNA, tr.mComprometido,TR.ID_TIPO_CONCEPTO TIPO_CONCEPTO,TR.ID_TIPO_MOVIMIENTO TIPO_MOVIMIENTO from tPAGODIVERSODetalle tr WITH(NOLOCK) , vSaldosAnuales vs  WHERE vs.EP=tr.EP ";

	 	}
	if (strTabla.equals("CLAVES_DIVERSO_CARGA")){
		strQuery = "select DISTINCT vs.nClaveCNA, tr.EP, CONVERT(VARCHAR(64), CAST(tr.mImporteMasIva AS MONEY),103),nDocRenglon from tPAGODIVERSODetalle tr, vSaldosAnuales vs WHERE vs.EP=tr.EP " + strMaxReg + " ORDER BY nDocRenglon";
 	}

		if (strTabla.equals("CLAVES_NOMINA")){
	 		strQuery = "select DISTINCT top 10 tr.EP,vs.ClaveInterna,vs.nClaveCNA, tr.mComprometido,TR.ID_TIPO_CONCEPTO TIPO_CONCEPTO,TR.ID_TIPO_MOVIMIENTO TIPO_MOVIMIENTO from tNominaDetalle tr WITH(NOLOCK) , vSaldosAnuales vs  WHERE vs.EP=tr.EP ";

	 	}

	if (strTabla.equals("DOCUMENTACION_RELACION_GASTO")){
	 		strQuery = "SELECT DCD_FACTURA,DCD_FECHA_FACTURA,DCD_TBEN,DCD_CBEN,DCD_TIPO_OPE,DCD_TIVA,DCD_IMP_BRUTO,DCD_IVADES,DCD_IVA,DCD_ISR,DCD_MIL5 ,DCD_MIL2,DCD_CONTRIBUCION,DCD_OTRAS_RET,DCD_PENALIZACION FROM tDocumentacionComprobatoriaDet WITH(NOLOCK)  ";

	 	}

	 	if (strTabla.equals("DATOSSIAFF")){
				strQuery = "SELECT top 20 FOLIO_CLC,FOLIO_CLC,BENEFICIARIO,FECHA_CAPTURA,ESTATUS,TOTAL_MN FROM SIAFF_ENCABEZADO ";

		}
	 		//System.out.println(strTabla);

	if ("M_TVALIDAINVERCIONAGASTOCORRIENTE".equals(strTabla)){
	 		strQuery = "SELECT v.iID, v.cCapituloOri, OC.cDescripcion , v.cTipoGastoOri, OT.dTipoGasto " +
						"     , v.cCapituloDest, DC.cDescripcion, v.cTipoGastoDest, DT.dTipoGasto, v.iNivel, v.cMensaje " +
						"  FROM tValidaInvercionAGastoCorriente v " +
						"     , tCatalogoTipoGasto ot " +
						"	 , tCatalogoCapitulo  oc " +
						"     , tCatalogoTipoGasto dt " +
						"	 , tCatalogoCapitulo  dc " +
						" WHERE V.cTipoGastoOri = OT.cTipoGasto " +
						"   and V.cCapituloOri  = OC.cCapitulo " +
						"   and V.cTipoGastoDest = DT.cTipoGasto " +
						"   and V.cCapituloDest  = DC.cCapitulo";
	}


	 if ("M_TCATALOGOEP".equals(strTabla)){
	 	strQuery = "SELECT top(1000) aEjercicioFiscal,nClaveCNA,EP,ClaveSIAFF,ClaveInterna,cRamoEP,cUnidadResponsableEP,cGrupoFuncional,cFuncion,cSubFuncion,cProgramaGeneral,cActividadInstitucional ";
	 	strQuery += " ,cProgramaPresupuestario,cPartida,cTipoGasto,cFuenteFinanciamiento,cEntidadFederativa,cCartera,cUnidadNorativa,cUnidadEjecutora,cRamo";
	 	strQuery += "  FROM tCatalogoEP with(nolock) ";
	 }
	 if ("M_TCATALOGOCABMSCONTABLE".equals(strTabla)){
	 	strQuery = "select cc.cCABMS,cc.cCUCOP,cc.cPartida,cc.cDescripcion,cc.nCuenta,cc.nIdUnidadMedida ";
	 	strQuery += "  from tCatalogoCABMS cc with(nolock)  ";
	 }
	if (strTabla.equals("CG_CAT_TCUENTAS5")){
		strQuery = " SELECT substring(nCuenta,1,5) nCuenta, dCuenta,TipoCuenta,nCuentaPadre,TipoBalance,VerificaSaldo,NaturalezaCuenta,NivelCuenta,AplicacionCuenta,cSubcuenta,nCuentaLike,cuentaBloqueada FROM tCuentas WITH(NOLOCK) where NivelCuenta=1 and not (TipoCuenta='P' and TipoBalance='P') and nCuenta not like '0%'  ";
 	}
	 if ("M_OPMAPEOCAMBIOPROGRAMA".equals(strTabla)){
	 	strQuery = "select aEjercicioFiscal, cProgramaAnterior, cProgramaNuevo from tOPMapeoCambioPrograma with(nolock) ";
	 }
	 if ("M_TCATALOGOEPANTEPROY".equals(strTabla)){
	 	strQuery = "SELECT aEjercicioFiscal,nClaveCNA,EP,ClaveSIAFF,ClaveInterna,cRamoEP,cUnidadResponsableEP,cGrupoFuncional,cFuncion,cSubFuncion,cProgramaGeneral,cActividadInstitucional ";
	 	strQuery += "      ,cProgramaPresupuestario,cPartida,cTipoGasto,cFuenteFinanciamiento,cEntidadFederativa,cCartera,cUnidadNorativa,cUnidadEjecutora,cRamo";
	 	strQuery += "  FROM tCatalogoEPAnteProy WITH(NOLOCK)  ";
	 }

	 if ("M_HCAPITULO".equals(strTabla)){
	 	strQuery = "SELECT cCapitulo, cDescripcion FROM tCatalogoCapitulo";
	 }

	if ("TUNIDADNORMATIVA".equals(strTabla)){
			strQuery = " SELECT u.cUnidadResponsable, u.D_DESCRIPCION FROM tCatUnidadResponsable u with (nolock) WHERE	u.nAlcance = 1 ";
	}
	if ("TUNIDADEJECUTORA".equals(strTabla)){
			strQuery = " SELECT u.cUnidadResponsable, u.D_DESCRIPCION FROM tCatUnidadResponsable u with (nolock) WHERE	u.nAlcance = 0 AND u.cUnidadResponsable != 'RHQ'";
	}
		if (strTabla.equals("TTECHOUEJECUTORA")) {
			strQuery = " SELECT u.cUnidadResponsable, u.D_DESCRIPCION, mTechoMonto FROM tTechoUEjecutora e with (nolock), tCatUnidadResponsable u with (nolock) WHERE e.cUnidadResponsable = u.cUnidadResponsable AND u.nAlcance = 0 ";
		}

		if (strTabla.equals("TTECHOUNORMATIVA")) {
			strQuery = " SELECT cUnidadNormativa, mTechoMonto FROM tTechoUNormativa t with (nolock), tCatUnidadResponsable u with (nolock) WHERE t.cUnidadNormativa = u.cUnidadResponsable AND u.nAlcance = 1 ";
		}

		if (strTabla.equals("TTECHOSPARTIDA")) {
			strQuery = " SELECT p.cPartida, p.dPartida, g.cTipoGasto, g.dTipoGasto, mTechoPartida FROM tTechosPartida t with (nolock), tCatalogoPartida p with (nolock), tCatalogoTipoGasto g with (nolock) WHERE t.cTipoGasto = g.cTipoGasto AND t.cPartida   = p.cPartida ";
		}

		if (strTabla.equals("TTECHOPROGRAMAPRESUPUESTARIO")) {
			strQuery = " SELECT p.cProgramaPresupuestario, p.dProgramaPresupuestario, mTechoMonto FROM tTechoProgramaPresupuestario t with (nolock), tCatalogoProgramaPresupuestario p with (nolock) WHERE t.cProgramaPresupuestario = p.cProgramaPresupuestario ";
		}

		if (strTabla.equals("TTECHOENTIDADFEDERATIVA")) {
			strQuery = " SELECT t.cEntidadFederativa, e.dEntidadFederativa, mMontoTecho FROM tTechoEntidadFederativa t with (nolock), tCatalogoEntidadFederativa e with (nolock) WHERE t.cEntidadFederativa =  e.cEntidadFederativa ";
		}
	 //INICIO SAICYS SAI MODULO DE REQUISICIONES BIENES Y SERVICIOS

		if (strTabla.equals("M_TCATALOGOTIPOCONSOLIDADO")) {
			strQuery = " select * FROM mCatalogoTipoConsolidado ";
		}
		if (strTabla.equals("M_TCATALOGOESTADOPEDIDO")) {
			strQuery = " select nIdEstado,IMAGEN,cEstado  FROM mCatalogoEstadoPedido WITH(NOLOCK)  ";
		}
		if (strTabla.equals("M_TCATALOGOESTADOCONTRATO")) {
			strQuery = " select nIdEstado,IMAGEN,cEstado  FROM mCatalogoEstadoContrato WITH(NOLOCK)  ";
		}
		if ("MCATALOGOTIPOSOLICITUD".equals(strTabla)) {
			strQuery = "select cIdTipoSolicitud, cTiposolicitud FROM mCatalogoTipoSolicitud WITH(NOLOCK) ";
		}

		if ("MCATALOGOTIPOPEDIDO".equals(strTabla)) {
			strQuery = "select cIdTipoPedido, cTipoPedido FROM mCatalogoTipoPedido WITH(NOLOCK) ";
		}

		if ("MCATALOGOTIPOCONTRATO".equals(strTabla)) {
			strQuery = "select cIdTipoContrato, cTipoContrato FROM mCatalogoTipoContrato WITH(NOLOCK) ";
		}

		if ("MCATALOGOESTADOSOLICITUD".equals(strTabla)) {
			strQuery = "select imagen, cEstado FROM mCatalogoEstadoSolicitud";
		}
		if ("MCATALOGOESTADOPRECOMPROMETIDO".equals(strTabla)) {
			strQuery = "SELECT IMAGEN ,cEstadoPrecomprometido FROM mCatalogoEstadoPrecomprometido WITH(NOLOCK)  ";
		}
		if ("MCATALOGOESTADOCONSOLIDADO".equals(strTabla)) {
			strQuery = " SELECT IMAGEN, cEstado FROM mCatalogoEstadoConsolidado ";
		}
		if ("CATALOGOPROVEEDOR".equals(strTabla)) {
			strQuery = "SELECT cIdRFC,cRazonSocial,cgiro,cRepresentanteLegal FROM dbo.mCatalogoProveedor WITH(NOLOCK) ";
		}
		if ("CONTRATOSCONANTICIPO".equals(strTabla)) {
			strQuery = " select *from v_mContratosAnticipo  ";
		}
		if ("EMPLEADOS_NOMINA".equals(strTabla)) {
			strQuery = "select *from(SELECT CONVERT(VARCHAR,CLAVE)+' - '+rtrim(ltrim(NOMBREN))+' '+rtrim(ltrim(NOMBREP))+' '+rtrim(ltrim(NOMBREM)) ServidoresPublicos " 
					+",CLAVE c_Empleado,SUBSTRING(RFC,1,4)+'-'+SUBSTRING(RFC,5,6)+'-'+SUBSTRING(RFC,1,3) d_emplrfc,NOMBREN,NOMBREP,NOMBREM ,emp.c_plaza, DESCRIPCION_PUESTO "
					+",CURP,Sexo nIdSexo "
					+"FROM v_empleados_giro AS emp WITH(NOLOCK) " 
					+"where emp.n_emplstatus=1)sub  ";
		}
		if ("SERVIDORESPUBLICOS_ADQ_NNOMINA".equals(strTabla)) {
			strQuery = "select *from(SELECT CONVERT(VARCHAR,c_empleado)+' - '+rtrim(ltrim(d_emplnombre))+' '+rtrim(ltrim(d_emplap))+' '+rtrim(ltrim(d_emplam)) ServidoresPublicos "
					+" ,c_empleado,SUBSTRING(d_emplrfc,1,4)+'-'+SUBSTRING(d_emplrfc,5,6)+'-'+SUBSTRING(d_emplrfc,1,3) d_emplrfc,d_emplnombre,d_emplap,d_emplam,emp.c_plaza,plaza.d_plaza "
					+" , emp.cUadministrativa,emp.c_jefeinmediato FROM dbo.nom_empleado AS emp WITH(NOLOCK) "
 					+" INNER JOIN dbo.nom_plazas AS plaza(NOLOCK) "
 					+" ON emp.c_empresa = plaza.c_empresa "
 					+" AND emp.c_plaza = plaza.c_plaza "
					+" AND emp.c_puesto = plaza.c_puesto "
					+" AND emp.c_tipoempleado = plaza.c_TipoEmpleado where emp.n_emplstatus=1 "
					+"and emp.cUadministrativa='903' and emp.c_jefeinmediato in(53,55,4060,5010) )sub ";
		}
		if ("SERVIDORESPUBLICOS_NNOMINA".equals(strTabla)) {
			strQuery = "select *from(SELECT CONVERT(VARCHAR,c_empleado)+' - '+rtrim(ltrim(d_emplnombre))+' '+rtrim(ltrim(d_emplap))+' '+rtrim(ltrim(d_emplam)) ServidoresPublicos "
					+" ,c_empleado,SUBSTRING(d_emplrfc,1,4)+'-'+SUBSTRING(d_emplrfc,5,6)+'-'+SUBSTRING(d_emplrfc,1,3) d_emplrfc,d_emplnombre,d_emplap,d_emplam,emp.c_plaza,plaza.d_plaza "
					+" ,d_emplcurp,Sexo nIdSexo"
					+" FROM dbo.nom_empleado AS emp WITH(NOLOCK) "
 					+" INNER JOIN dbo.nom_plazas AS plaza(NOLOCK) "
 					+" ON emp.c_empresa = plaza.c_empresa "
 					+" AND emp.c_plaza = plaza.c_plaza "
					+" AND emp.c_puesto = plaza.c_puesto "
					+" AND emp.c_tipoempleado = plaza.c_TipoEmpleado where emp.n_emplstatus=1)sub ";
		}
		if ("RFCSERVIDORPUBLICO_NNOMINA".equals(strTabla)) {
			strQuery = "select *from (select d_emplrfc rfcServPub,d_emplnombre nombre "
						+",d_emplap apellidoPat,d_emplam apeellidoMat,c_empleado numEmpleado "
						+",emp.cUadministrativa,ue.cUejecutora,urcc.cCentroContable"
						+" from nom_empleado as emp with(nolock) "
						+" inner join nom_Unidad_Ejecutora as ue with(nolock) "
						+" on ue.cUadministrativa=emp.cUadministrativa "
						+" inner join tCatalogoURCC as urcc with(nolock) "
						+" on urcc.cUnidadResponsable=ue.cUejecutora "
						+" where n_emplstatus=1)sub ";
		}
		if ("MCATALOGOPROVEEDOR".equals(strTabla)) {
			strQuery = " SELECT cTipoPersona, cIdRFC, cRazonSocial, cRepresentante, cGiro, cNumeroRegistro "
					+ " , nIdPyme, cIdEntidadFederativa, cMunicipio, cCalle, cNumeroExterno, cNumeroInterno, cColonia, "
					+ " cCodigoPostal, cUrl, cEmail FROM mCatalogoProveedor WITH(NOLOCK) " ;
		}
		if ("MCATALOGOPROVEEDORCAP1000".equals(strTabla)) {
			strQuery = " SELECT  ltrim(cIdRFC + ' - ' + cRazonSocial) as cRazonSocial, cIdRFC  as cIdRfc"+
			" FROM mCatalogoProveedor WITH(NOLOCK)  ";
		}
		if ("CAT_CAPITULOS_SACEL".equals(strTabla)) {
			strQuery = " select ccapitulo,cIdCapitulo from mCatalogoCapitulo WITH(NOLOCK) ";

		}

		if ("CAT_PARTIDAS_SACEL".equals(strTabla)) {
			strQuery = "  select cidsubpartida + '-'+csubpartida as cidsubpartida from mCatalogoSubPartida WITH(NOLOCK) ";

		}

		if ("CAT_TIPOPROCESOS_SACEL".equals(strTabla)) {
			strQuery = "Select * from (select convert(varchar,p.nIdTipoProceso) + '-'+ p.cTipoProceso as cTipoProceso,nIdTipoProceso from mCatalogoTipoProceso p) tp";

		}

		if ("CATALOGOCABM".equals(strTabla)) {
			strQuery = " SELECT TOP " + strMaxReg;
			strQuery += " cc.cIdCABM,";
			strQuery += " cc.cCABM,";
			strQuery += " cc.cIdUnidadMedida,";
			strQuery += " c.cCapitulo,";
			strQuery += " par.cIdSubPartida+ ' - '+ par.cSubPartida as cSubPartida,";
			strQuery += " p.cTipoProceso,";
			strQuery += " ccaop.cCCAOP,";
			strQuery += " cc.cIdCUCOP ,";
			strQuery += " cc.nIdCABMSOP,";
			strQuery += " csop.cDescripcion,";
			strQuery += " cc.cIdCCAOP,";
			
			strQuery += " cc.cIdCapitulo,";
			strQuery += " cc.cIdSubPartida,";
			strQuery += " cc.nIdTipoProceso";
			
			strQuery += " from mCatalogoCABM cc";
			strQuery += " inner join mCatalogoSubPartida par on cc.cIdSubPartida=par.cIdSubPartida";
			strQuery += " inner join mCatalogoCapitulo c  on c.cIdCapitulo=cc.cIdCapitulo";
			strQuery += " inner join mCatalogoTipoProceso p on cc.nIdTipoProceso=p.nIdTipoProceso";
			strQuery += " inner join mCatalogoCCAOP ccaop on ccaop.cIdCCAOP = cc.cIdCCAOP";
			strQuery += " inner join mCatalogoCaBMSOP csop on csop.nIdCABMSOP = cc.nIdCABMSOP";
			
			
			if(strParam.equals("1=2")){
				strParam="";
			}
		}
		if ("CUCOP".equals(strTabla)) {
			strQuery = " SELECT TOP " + strMaxReg;
			strQuery += " cc.cIdCABM AS Cucop,";
			strQuery += " cc.cCABM,";
			strQuery += " cc.cIdUnidadMedida,";
			strQuery += " c.cCapitulo,";
			strQuery += " par.cIdSubPartida+ ' - '+ par.cSubPartida as cSubPartida,";
			strQuery += " p.cTipoProceso,";
			strQuery += " ccaop.cCCAOP,";
			strQuery += " cc.cIdCUCOP ,";
			strQuery += " cc.nIdCABMSOP,";
			strQuery += " cc.cCABM cDescripcion,";
			strQuery += " cc.cIdCCAOP,";
			
			strQuery += " cc.cIdCapitulo,";
			strQuery += " cc.cIdSubPartida,";
			strQuery += " cc.nIdTipoProceso";
			
			strQuery += " from mCatalogoCABM cc";
			strQuery += " inner join mCatalogoSubPartida par on cc.cIdSubPartida=par.cIdSubPartida";
			strQuery += " inner join tCatalogoCapitulo c  on c.cCapitulo=cc.cIdCapitulo";
			strQuery += " inner join mCatalogoTipoProceso p on cc.nIdTipoProceso=p.nIdTipoProceso";
			strQuery += " inner join mCatalogoCCAOP ccaop on ccaop.cIdCCAOP = cc.cIdCCAOP";
			System.out.println("strParam :"+strParam);
			if(strParam.equals("TODO") || strParam.equals("")){
				strQuery+=" where cc.cIdCapitulo=6";
			}else if(!strParam.equals("TODO") && !strParam.equals("")){
				strParam=strParam.replace("Cucop", "cc.cIdCABM");  //replace("Cucop", "cc.cIdCABM");
			}
			else if(strParam.equals("1=2")){
				strParam="";
			}
		}

		if ("CAT_CCAOP".equals(strTabla)) {
			strQuery = "select cidccaop + '-'+ cccaop  as ccaop,cidccaop from mCatalogoCCAOP";

		}

		if ("CONSULTAPROGRAMAANUAL".equals(strTabla)) {
		strQuery = "SELECT pa.cEjercicio,pa.cidUnidadEjecutora,pampc.d_descripcion,pampc.mMontoC2,pampc.mMontoC3,pampc.mMontoC5,pampc.mMontoTotal FROM mProgramaAnual pa INNER JOIN fn_mProgramaAnualMontosPorCapitulo() AS pampc ON pampc.cEjercicio = pa .cEjercicio AND pampc .cIdUnidadEjecutora = pa.cIdUnidadEjecutora ";

		}

		if ("CUCOPSGRID".equals(strTabla)) {
		strQuery = "select distinct cidcabm,cIdSubPartida,cCABM,cUnidadMedida,nCantidad,mPrecioUnitario,nPorcentajeIVA,mImporteBruto,mImporteNeto  from vCucopsGrid";

		}
		if ("AGREGACUCOPSGRID".equals(strTabla)) {
		strQuery = "SELECT cidcabm,ccabm,cidsubpartida,csubpartida,cunidadMedida,ctipoproceso FROM V_AGREGACUCOPS  ";

		}

		if ("CUCOPSPERIODOGRID".equals(strTabla)) {
			strQuery = "select cperiodo "+
			",cd.cDisponible as cantDisp"+
			",pd.nCantidad,pd.mPrecioUnitario,mimportebruto"+
			",mimporteneto, ncantidadensolicitudes, ncantidaddisponibilidad,mMontoNetoEnSolicitudes"+
			", mmontodisponibilidad,mMantener "+ 
			" from fn_mPagoAnualDetallePeriodo("+ strSelectFunc+") as pd "+
			" inner join[v_CucopsDisponibles] cd on cd.cEjercicio=pd.cEjercicio "+ 
			" and cd.cidUnidadEjecutora=pd.cIdUnidadEjecutora and cd.cidCABM=pd.cIdCABM and cd.cMes=pd.cPeriodo"+
			" order by cd.nIdPeriodo ";
       		
		}
		if ("CUCOPSPERIODOGRIDCAPMIL".equals(strTabla)) {
			strQuery = "select cperiodo "+
			",pd.ncantidad as cantDisp"+
			",pd.nCantidad,pd.mPrecioUnitario,mimportebruto"+
			",mimporteneto, ncantidadensolicitudes, ncantidaddisponibilidad,mMontoNetoEnSolicitudes"+
			", mmontodisponibilidad,nIdPeriodo "+ 
			" from fn_mPagoAnualDetallePeriodoCapMil("+ strSelectFunc+") as pd "+
			" order by pd.nIdPeriodo ";
       		
		}
        if ("CONSULTAPASIVO".equals(strTabla)) {
		strQuery = " EXEC pa_pasivoConsulta '" + campos + "' ";

		}
        
        if ("CONSULTALINEASSOLICITUD".equals(strTabla)) {
    		strQuery = " EXEC pa_mObtieneLineasSolicitudes " + campos + " ";
    	}
		
        if ("CONSULTAPASIVOPEDIDO".equals(strTabla)) {
			strQuery = " EXEC pa_pasivoConsultaPedido '" + campos + "' ";
		}
		if ("CONSULTALINEASCONSOLIDADO".equals(strTabla)) {
    		strQuery = "select *from v_mObtieneLineasConsolidado WHERE cIdConsolidado= " + campos + " ";
    		
    	}
		if ("AUTOCOMPLETECUCOP".equals(strTabla)) {
    		strQuery = " EXEC pa_mDesLineasMasUsadas " + campos ;
    	}
       

		if ("TABLASOLICITUDMES".equals(strTabla)) {
			strQuery = "select cidcabm, "
				+"  ccabm, "
				+"  cast(enero_pu as      varchar)	+'|'+ 	convert(varchar,enero_ds)		+'|'+ 	convert(varchar,enero_md, 1)      	as enero, "
				+"  cast(febrero_pu as    varchar)	+'|'+ 	convert(varchar,febrero_ds)		+'|'+ 	convert(varchar,febrero_md, 1)		as febrero, "
				+"  cast(marzo_pu as      varchar)	+'|'+ 	convert(varchar,marzo_ds)       +'|'+ 	convert(varchar,marzo_md, 1)		as marzo, "
				+"  cast(abril_pu as      varchar)	+'|'+ 	convert(varchar,abril_ds)       +'|'+ 	convert(varchar,abril_md, 1)		as abril, "
				+"  cast(mayo_pu as       varchar)	+'|'+ 	convert(varchar,mayo_ds)        +'|'+ 	convert(varchar,mayo_md, 1)		as mayo, "
				+"  cast(junio_pu as      varchar)	+'|'+ 	convert(varchar,junio_ds)       +'|'+ 	convert(varchar,junio_md, 1)		as junio, "
				+"  cast(julio_pu as      varchar)	+'|'+ 	convert(varchar,julio_ds)       +'|'+ 	convert(varchar,julio_md, 1)		as julio, "
				+"  cast(agosto_pu as     varchar)	+'|'+ 	convert(varchar,agosto_ds)      +'|'+ 	convert(varchar,agosto_md, 1)		as agosto, "
				+"  cast(septiembre_pu as varchar)	+'|'+ 	convert(varchar,septiembre_ds)  +'|'+ 	convert(varchar,septiembre_md, 1)	as septiembre, "
				+"  cast(octubre_pu as    varchar)	+'|'+ 	convert(varchar,octubre_ds)     +'|'+ 	convert(varchar,octubre_md, 1)		as octubre, "
				+"  cast(noviembre_pu as  varchar)	+'|'+ 	convert(varchar,noviembre_ds)   +'|'+ 	convert(varchar,noviembre_md, 1)	as noviembre, "
				+"  cast(diciembre_pu as  varchar)	+'|'+ 	convert(varchar,diciembre_ds)   +'|'+ 	convert(varchar,diciembre_md, 1)	as diciembre, "
				+" nPorcentajeIVA "
				+"from v_calendario_CUCOP (" + campos + ")";
		}

		if (strTabla.equals("TABLASOLICITUDLINEAS")) {
		  	strQuery = "SELECT lineaCopia,nIdLineaSolicitud,cIdCABM,cMes,cDescripcion,nCantidad,CONVERT(varchar,mPrecioUnitario,1),nPorcentajeIVA,CONVERT(varchar,mImporteNeto,1),cUnidadMedida,cDisponible,CONVERT(varchar,cMontoDisponible,1),lineaElimina,nIdPeriodo FROM view_mSolicitudLineas(" + campos + ")";
		}

	 	if (strTabla.equals("M_MCATALOGOENTIDADFEDERATIVA")){

	 		strQuery = " select * FROM mCatalogoEntidadFederativa ";
	 		strParam = strParam.replace("cIdEntidadFederativa LIKE '", "cIdEntidadFederativa LIKE '%");
	 		strParam = strParam.replace("cEntidadFederativa LIKE '", "cEntidadFederativa LIKE '%");
	 	}

	 	if ("MCATALOGOTIPOSOLICITUD".equals(strTabla)){
	 		strQuery = "select cIdTipoSolicitud, cTiposolicitud FROM mCatalogoTipoSolicitud";
	 		strParam = strParam.replace("cIdTipoSolicitud LIKE '", "cIdTipoSolicitud LIKE '%");
	 		strParam = strParam.replace("cTiposolicitud LIKE '", "cTiposolicitud LIKE '%");
		}
	 	if (strTabla.equals("M_TCATALOGOTIPOCONSOLIDADO")){
	 		strQuery = " select cIdTipoConsolidado, cTipoConsolidado FROM mCatalogoTipoConsolidado ";
	 		strParam = strParam.replace("cIdTipoConsolidado LIKE '", "cIdTipoConsolidado LIKE '%");
	 		strParam = strParam.replace("cTipoConsolidado LIKE '", "cTipoConsolidado LIKE '%");
	 	}
	 	if ("MCATALOGOTIPOPROCEDIMIENTO".equals(strTabla)){
	 		strQuery = "select cIdTipoProcedimiento, cTipoProcedimiento FROM mCatalogoTipoProcedimiento ";
			strQuery += " WHERE cIdTipoProcedimiento !='0'";
	 		strParam = strParam.replace("cIdTipoProcedimiento LIKE '", "cIdTipoProcedimiento LIKE '%");
	 		strParam = strParam.replace("cTipoProcedimiento LIKE '", "cTipoProcedimiento LIKE '%");
		}
	 	if ("MCATALOGOTIPOPEDIDO".equals(strTabla)){
	 		strQuery = "select cIdTipoPedido, cTipoPedido FROM mCatalogoTipoPedido";
	 		strParam = strParam.replace("cIdTipoPedido LIKE '", "cIdTipoPedido LIKE '%");
	 		strParam = strParam.replace("cTipoPedido LIKE '", "cTipoPedido LIKE '%");
		}
	 	if ("MCATALOGOTIPOCONTRATO".equals(strTabla)){
	 		strQuery = "select cIdTipoContrato, cTipoContrato FROM mCatalogoTipoContrato";
	 		strParam = strParam.replace("cIdTipoContrato LIKE '", "cIdTipoContrato LIKE '%");
	 		strParam = strParam.replace("cTipoContrato LIKE '", "cTipoContrato LIKE '%");
		}
	 	if ("MCATALOGOESTADOSOLICITUD".equals(strTabla)){
	 		strQuery = "select imagen, cEstado FROM mCatalogoEstadoSolicitud";
	 		strParam = strParam.replace("cEstado LIKE '", "cEstado LIKE '%");
		}
	 	if ("MCATALOGOESTADOPRECOMPROMETIDO".equals(strTabla)){
	 		strQuery = "SELECT IMAGEN ,cEstadoPrecomprometido FROM mCatalogoEstadoPrecomprometido ";
	 		strParam = strParam.replace("cEstadoPrecomprometido LIKE '", "cEstadoPrecomprometido LIKE '%");
		}
	 	if ("MCATALOGOESTADOCONSOLIDADO".equals(strTabla)){
	 		strQuery = " SELECT IMAGEN, cEstado FROM mCatalogoEstadoConsolidado ";
	 		strParam = strParam.replace("cEstado LIKE '", "cEstado LIKE '%");
		}
	 	if ("MCATALOGOESTADOPROCEDIMIENTO".equals(strTabla)){
	 		strQuery = " SELECT REPLACE(IMAGEN,'../../','../') as IMAGEN, cEstado FROM MCATALOGOESTADOPROCEDIMIENTO ";
	 		strParam = strParam.replace("cEstado LIKE '", "cEstado LIKE '%");
		}

	 	if (strTabla.equals("M_TCATALOGOESTADOPEDIDO")){
	 		strQuery = " select IMAGEN,cEstado  FROM mCatalogoEstadoPedido ";
	 		strParam = strParam.replace("cEstado LIKE '", "cEstado LIKE '%");
	 	}
	 	if (strTabla.equals("M_TCATALOGOESTADOCONTRATO")){
	 		strQuery = " select IMAGEN,cEstado  FROM mCatalogoEstadoContrato ";
	 		strParam = strParam.replace("cEstado LIKE '", "cEstado LIKE '%");
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOROLEOPCION"))
		{ 
			strQuery="SELECT ro.r_nombre,  id_opcion  FROM  cg_role_opcion ro, CG_ROLE r where ro.R_NOMBRE=r.R_NOMBRE ";
			strQuery +="and ADMIN_DUENO in(SELECT UR.R_NOMBRE FROM CG_USUARIO_ROLE ur WHERE ur.U_LOGIN='"+strUsuario+"')";

		}

		if ("M_MCATALOGOUNIDADMEDIDA".equals(strTabla)){
		 		strQuery = " SELECT cIdUnidadMedida, cUnidadMedida, cIdClave FROM mCatalogoUnidadMedida";
		 		strParam = strParam.replace("cIdUnidadMedida LIKE '", "cIdUnidadMedida LIKE '%");
		 		strParam = strParam.replace("cUnidadMedida LIKE '", "cUnidadMedida LIKE '%");
		 		strParam = strParam.replace("cIdClave LIKE '", "cIdClave LIKE '%");
		}
		if ("M_MCATALOGOUNIDADMEDIDA2".equals(strTabla)){
		 		strQuery = " SELECT cIdClave, cIdUnidadMedida, cUnidadMedida FROM mCatalogoUnidadMedida";
		 		strParam = strParam.replace("cIdUnidadMedida LIKE '", "cIdUnidadMedida LIKE '%");
		 		strParam = strParam.replace("cUnidadMedida LIKE '", "cUnidadMedida LIKE '%");
		 		strParam = strParam.replace("cIdClave LIKE '", "cIdClave LIKE '%");
		}
		// Aqui comienza cambios para modulo en desembolsos
		if ("D_CATAGENTEFINANCIERO".equals(strTabla)){
		 		strQuery = " SELECT ID_AgenteFinanciero, AgenteFinancieroCorto, AgenteFinancieroLargo FROM dCat_Agente_Financiero";
		 		strParam = strParam.replace("ID_AgenteFinanciero LIKE '", "ID_AgenteFinanciero LIKE '%");
		 		strParam = strParam.replace("AgenteFinancieroCorto LIKE '", "AgenteFinancieroCorto LIKE '%");
		 		strParam = strParam.replace("AgenteFinancieroLargo LIKE '", "AgenteFinancieroLargo LIKE '%");
		}
		if ("D_CATOFI".equals(strTabla)){
		 		strQuery = " SELECT id_OFI, OFICorto, OFILargo FROM dCat_OFI";
		 		strParam = strParam.replace("id_OFI LIKE '", "id_OFI LIKE '%");
		 		strParam = strParam.replace("OFICorto LIKE '", "OFICorto LIKE '%");
		 		strParam = strParam.replace("OFILargo LIKE '", "OFILargo LIKE '%");
		}
		if ("D_CATAREAEXECUTORAEXT".equals(strTabla)){
		 		strQuery = " SELECT ID_EntidadEjecResp, EntidadEjecRespCorto, EntidadEjecRespLargo FROM dCat_Entidad_Ejec_Resp";
		 		strParam = strParam.replace("ID_EntidadEjecResp LIKE '", "ID_EntidadEjecResp LIKE '%");
		 		strParam = strParam.replace("EntidadEjecRespCorto LIKE '", "EntidadEjecRespCorto LIKE '%");
		 		strParam = strParam.replace("EntidadEjecRespLargo LIKE '", "EntidadEjecRespLargo LIKE '%");
		}
		if ("D_DCATTIPOCAMBIO".equals(strTabla)){
		 		strQuery = " select ID_TipoCambio, convert(varchar(10),Fecha,103), TipoCambio, ID_AgenteFinanciero, AgenteFinancieroCorto  from vdCat_Tipo_Cambio";
		 		strParam = strParam.replace("ID_TipoCambio LIKE '", "ID_TipoCambio LIKE '%");
		 		strParam = strParam.replace("Fecha LIKE '", "convert(varchar(10),Fecha,103) LIKE '%");
		 		strParam = strParam.replace("TipoCambio LIKE '", "TipoCambio LIKE '%");
		 		strParam = strParam.replace("ID_AgenteFinanciero LIKE '", "ID_AgenteFinanciero LIKE '%");
		 		strParam = strParam.replace("AgenteFinancieroCorto LIKE '", "AgenteFinancieroCorto LIKE '%");
		}
		if ("D_DCATUNIDADRESPONSABLE".equals(strTabla)){
		 		strQuery = " select ID_ur, UnidadResponsable FROM dCat_Unidad_Responsable";
		 		strParam = strParam.replace("ID_ur LIKE '", "ID_ur LIKE '%");
		 		strParam = strParam.replace("UnidadResponsable LIKE '", "UnidadResponsable LIKE '%");
		}
		if ("D_HELPCATOFI".equals(strTabla)){
		 		strQuery = " SELECT Id_OFI, OFICorto FROM dCat_OFI";
		}
		if ("D_HELPCATUNIDADRESP".equals(strTabla)){
		 		strQuery = " select Id_UR, UnidadResponsable  from dCat_Unidad_Responsable";
		}
		if ("D_CATCONPONENTESTECNICOS".equals(strTabla)){
		 		strQuery = " SELECT Id_ComponenteTecnico, ComponenteTecnico FROM dCat_Componente_Tecnico ";
		 		strParam = strParam.replace("Id_Prestamo LIKE '", "Id_Prestamo LIKE '%");
		 		System.out.print("Desembolsos "+strQuery+strParam );
		}
		if ("D_CON_PRESTAMO".equals(strTabla)){
		 		strQuery = " Select Id_Prestamo, NombrePrestamo, Objetivo, Id_OFI, Id_AgenteFinanciero, FechaFirma, "; 
     		 	strQuery += " FechaEfectividad, FechaTerminacion, FechaLimiteDesembolsar, Id_UR, MontoOriginal ";
		 		strQuery += " from d_Prestamo ";
		}
        // Aqui Termina cambios para modulo en desembolsos
        
		  if ("M_MCATALOGOUNIDADMEDIDA_DESC".equals(strTabla)){
		 		strQuery = " SELECT cUnidadMedida FROM mCatalogoUnidadMedida";
		 		strParam = strParam.replace("cUnidadMedida LIKE '", "cUnidadMedida LIKE '%");
		}
		  if ("M_MCATALOGOUNIDADMEDIDA_CLAVE".equals(strTabla)){
		 		strQuery = " SELECT cIdClave FROM mCatalogoUnidadMedida";
		 		strParam = strParam.replace("cIdClave LIKE '", "cIdClave LIKE '%");
		}
		  if ("M_MCATALOGOSUBPARTIDA".equals(strTabla)){
		 		strQuery = " SELECT sp.cIdSubPartida,sp.cSubPartida,c.cCapitulo, sp.cSubPartidaCorto,sp.lRestaPresupuesto  " +
		 		" FROM mCatalogoSubPartida sp  "+
		 		" INNER JOIN mCatalogoCapitulo c " +
		 		" ON sp.cIdCapitulo = c.cIdCapitulo";
		 		strParam = strParam.replace("c.cCapitulo LIKE '", "c.cCapitulo LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartidaCorto LIKE '", "sp.cSubPartidaCorto LIKE '%");
		 		strParam = strParam.replace("sp.cIdSubPartida LIKE '", "sp.cIdSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartida LIKE '", "sp.cSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.lRestaPresupuesto LIKE '", "sp.lRestaPresupuesto LIKE '%");
		 		strParam = strParam.replace("sp.cIdCapitulo LIKE '", "sp.cIdCapitulo LIKE '%");
		}

		  if (strTabla.equals("M_MCATALOGOCATEGORIAPROCEDIMIENTO")){
		 		strQuery = " select cConvocaCompras, cConvocaServicios, nIdCategoria,cCategoria  FROM mCatalogoCategoriaProcedimiento ";
		 		strParam = strParam.replace("nIdCategoria LIKE '", "nIdCategoria LIKE '%");
		 		strParam = strParam.replace("cCategoria LIKE '", "cCategoria LIKE '%");
		 	}

		  if (strTabla.equals("M_CIDUNIDADMEDICA")){
		 		strQuery = " SELECT cIdUnidadMedida FROM mCatalogoUnidadMedida ";
		 	}
		  if (strTabla.equals("M_CUNIDADMEDIDA")){
		 		strQuery = " SELECT cUnidadMedida FROM mCatalogoUnidadMedida ";

		 	}
		  if (strTabla.equals("M_CIDCLAVE")){
		 		strQuery = " SELECT cIdClave FROM mCatalogoUnidadMedida ";

		 	}
		  if ("M_MCATALOGOSUBPARTIDA_ID".equals(strTabla)){
			  strQuery = " SELECT sp.cIdSubPartida,sp.cSubPartida,c.cCapitulo, sp.cSubPartidaCorto,sp.lRestaPresupuesto  " +
		 		" FROM mCatalogoSubPartida sp  "+
		 		" INNER JOIN mCatalogoCapitulo c " +
		 		" ON sp.cIdCapitulo = c.cIdCapitulo";
			  strParam = strParam.replace("c.cCapitulo LIKE '", "c.cCapitulo LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartidaCorto LIKE '", "sp.cSubPartidaCorto LIKE '%");
		 		strParam = strParam.replace("sp.cIdSubPartida LIKE '", "sp.cIdSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartida LIKE '", "sp.cSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.lRestaPresupuesto LIKE '", "sp.lRestaPresupuesto LIKE '%");
		 		strParam = strParam.replace("sp.cIdCapitulo LIKE '", "sp.cIdCapitulo LIKE '%");
		}
		  if ("M_MCATALOGOSUBPARTIDA_SP".equals(strTabla)){
			  strQuery = " SELECT sp.cIdSubPartida,sp.cSubPartida  " +
		 		" FROM mCatalogoSubPartida sp  ";
		 		strParam = strParam.replace("sp.cIdSubPartida LIKE '", "sp.cIdSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartida LIKE '", "sp.cSubPartida LIKE '%");
		}
		  if ("M_MCATALOGOCABMSOP".equals(strTabla)){
			  strQuery = " select mc.nidCABMSOP,mc.cDescripcion  " +
		 		" from mCatalogoCABMSOP mc  ";
		 		strParam = strParam.replace("mc.nidCABMSOP LIKE '", "mc.nidCABMSOP LIKE '%");
		 		strParam = strParam.replace("mc.cDescripcion LIKE '", "mc.cDescripcion LIKE '%");
		}
		if ("M_MCATALOGOSUBPARTIDA_ID".equals(strTabla)){
			  strQuery = " SELECT * FROM (SELECT sp.cIdSubPartida,sp.cIdSubPartida+'-'+sp.cSubPartida as cSubPartida,sp.cIdCapitulo, sp.cSubPartidaCorto,sp.lRestaPresupuesto  " +
		 		" FROM mCatalogoSubPartida sp  "+
		 		"  ) AS sp";
			  	//strParam = strParam.replace("c.cCapitulo LIKE '", "c.cCapitulo LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartidaCorto LIKE '", "sp.cSubPartidaCorto LIKE '%");
		 		strParam = strParam.replace("sp.cIdSubPartida LIKE '", "sp.cIdSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartida LIKE '", "sp.cSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.lRestaPresupuesto LIKE '", "sp.lRestaPresupuesto LIKE '%");
		 		strParam = strParam.replace("sp.cIdCapitulo LIKE '", "sp.cIdCapitulo LIKE '%");
		}
		  if ("M_MCATALOGOSUBPARTIDA_C".equals(strTabla)){
			  strQuery = " SELECT c.cCapitulo,sp.cIdSubPartida,sp.cSubPartida, sp.cSubPartidaCorto,sp.lRestaPresupuesto  " +
		 		" FROM mCatalogoSubPartida sp  "+
		 		" INNER JOIN mCatalogoCapitulo c " +
		 		" ON sp.cIdCapitulo = c.cIdCapitulo";
			  strParam = strParam.replace("c.cCapitulo LIKE '", "c.cCapitulo LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartidaCorto LIKE '", "sp.cSubPartidaCorto LIKE '%");
		 		strParam = strParam.replace("sp.cIdSubPartida LIKE '", "sp.cIdSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartida LIKE '", "sp.cSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.lRestaPresupuesto LIKE '", "sp.lRestaPresupuesto LIKE '%");
		 		strParam = strParam.replace("sp.cIdCapitulo LIKE '", "sp.cIdCapitulo LIKE '%");
		}
		 if ("M_MCATALOGOSUBPARTIDA_PC".equals(strTabla)){
			  strQuery = " SELECT sp.cSubPartidaCorto,sp.cIdSubPartida,sp.cSubPartida,c.cCapitulo, sp.lRestaPresupuesto  " +
		 		" FROM mCatalogoSubPartida sp  "+
		 		" INNER JOIN mCatalogoCapitulo c " +
		 		" ON sp.cIdCapitulo = c.cIdCapitulo";
			  strParam = strParam.replace("c.cCapitulo LIKE '", "c.cCapitulo LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartidaCorto LIKE '", "sp.cSubPartidaCorto LIKE '%");
		 		strParam = strParam.replace("sp.cIdSubPartida LIKE '", "sp.cIdSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.cSubPartida LIKE '", "sp.cSubPartida LIKE '%");
		 		strParam = strParam.replace("sp.lRestaPresupuesto LIKE '", "sp.lRestaPresupuesto LIKE '%");
		 		strParam = strParam.replace("sp.cIdCapitulo LIKE '", "sp.cIdCapitulo LIKE '%");
		}
		 if (strTabla.equals("M_MCATALOGOFIRMANTES")){

		 		strQuery = " SELECT f.cIdUnidadEjecutora,f.cNombre,f.cPaterno,f.cMaterno,f.cPuesto,f.lHabilitado,f.nIdFirmante,f.nNumeroEmpleado FROM mCatalogoFirmantes f";
		 		strParam = strParam.replace("cNombre LIKE '", "cNombre LIKE '%");
		 		strParam = strParam.replace("cPaterno LIKE '", "cPaterno LIKE '%");
		 		strParam = strParam.replace("cMaterno LIKE '", "cMaterno LIKE '%");
		 		strParam = strParam.replace("cPuesto LIKE '", "cPuesto LIKE '%");
		 		strParam = strParam.replace("lHabilitado LIKE '", "lHabilitado LIKE '%");
		 		strParam = strParam.replace("nNumeroEmpleado LIKE '", "nNumeroEmpleado LIKE '%");
		 }
		  if (strTabla.equals("M_MCATALOGOFIRMANTES_USUARIO")){

		 		strQuery = " SELECT f.cIdUnidadEjecutora,f.cNombre,f.cPaterno,f.cMaterno,f.cPuesto,f.lHabilitado,f.nIdFirmante,f.nNumeroEmpleado FROM mCatalogoFirmantes f ";
		 		strParam = strParam.replace("cNombre LIKE '", "cNombre LIKE '%");
		 		strParam = strParam.replace("cPaterno LIKE '", "cPaterno LIKE '%");
		 		strParam = strParam.replace("cMaterno LIKE '", "cMaterno LIKE '%");
		 		strParam = strParam.replace("cPuesto LIKE '", "cPuesto LIKE '%");
		 		strParam = strParam.replace("lHabilitado LIKE '", "lHabilitado LIKE '%");
		 		strParam = strParam.replace("nNumeroEmpleado LIKE '", "nNumeroEmpleado LIKE '%");
		 		
		 		if(strParam.endsWith("TODO")){
		 			strParam =  " cIdUnidadEjecutora = '" +u.getU_UR()+"'";
		 		}else{
		 			strParam +=  "AND cIdUnidadEjecutora = '" +u.getU_UR()+"'";
		 		}
		 }
		  if (strTabla.equals("M_MCATALOGOFIRMANTES_N")){
		 		strQuery = " SELECT cNombre,cPaterno,cMaterno,cPuesto,lHabilitado,nIdFirmante,cIdUnidadEjecutora FROM mCatalogoFirmantes ";
		 		strParam = strParam.replace("cNombre LIKE '", "cNombre LIKE '%");
		 		strParam = strParam.replace("cPaterno LIKE '", "cPaterno LIKE '%");
		 		strParam = strParam.replace("cMaterno LIKE '", "cMaterno LIKE '%");
		 		strParam = strParam.replace("cPuesto LIKE '", "cPuesto LIKE '%");
		 		strParam = strParam.replace("lHabilitado LIKE '", "lHabilitado LIKE '%");

		 	}
		  if (strTabla.equals("M_MCATALOGOFIRMANTES_AP")){
		 		strQuery = " SELECT cPaterno,cMaterno,cNombre,cPuesto,lHabilitado,nIdFirmante,cIdUnidadEjecutora FROM mCatalogoFirmantes ";
		 		strParam = strParam.replace("cNombre LIKE '", "cNombre LIKE '%");
		 		strParam = strParam.replace("cPaterno LIKE '", "cPaterno LIKE '%");
		 		strParam = strParam.replace("cMaterno LIKE '", "cMaterno LIKE '%");
		 		strParam = strParam.replace("cPuesto LIKE '", "cPuesto LIKE '%");
		 		strParam = strParam.replace("lHabilitado LIKE '", "lHabilitado LIKE '%");

		 	}
		  if (strTabla.equals("M_MCATALOGOFIRMANTES_AM")){
		 		strQuery = " SELECT cMaterno,cNombre,cPaterno,cPuesto,lHabilitado,nIdFirmante,cIdUnidadEjecutora FROM mCatalogoFirmantes ";
		 		strParam = strParam.replace("cNombre LIKE '", "cNombre LIKE '%");
		 		strParam = strParam.replace("cPaterno LIKE '", "cPaterno LIKE '%");
		 		strParam = strParam.replace("cMaterno LIKE '", "cMaterno LIKE '%");
		 		strParam = strParam.replace("cPuesto LIKE '", "cPuesto LIKE '%");
		 		strParam = strParam.replace("lHabilitado LIKE '", "lHabilitado LIKE '%");

		 	}
		  if (strTabla.equals("M_MCATALOGOFIRMANTES_P")){
		 		strQuery = " select distinct cPuesto from mCatalogoFirmantes ";
		 		strParam = strParam.replace("cPuesto LIKE '", "cPuesto LIKE '%");

		 	}
		  if (strTabla.equals("M_MCATALOGOFIRMANTES_CC")){
		 		strQuery = " SELECT cUnidadResponsable, D_DESCRIPCION FROM v_unidadEjecutoraPadre ";
		 		strParam = strParam.replace("cUnidadResponsable LIKE '", "cUnidadResponsable LIKE '%");
		 		strParam = strParam.replace("D_DESCRIPCION LIKE '", "D_DESCRIPCION LIKE '%");
		 	}


		  if (strTabla.equals("PROVEEDORESCOTIZACION")){
		 		strQuery = " select cIdRFC,cRazonSocial,nIdconsecutivoAdj from v_obtieneProveedoresCotizacion ";

		 	}
		 	if (strTabla.equals("PROVEEDORESCOTIZACIONES")){
		 		strQuery = ""
							+ "SELECT cidrfc, "
							+ "       CASE "
							+ "         WHEN cotizacion.cidtipopersona = 1 THEN cotizacion.crazonsocial "
							+ "         ELSE cotizacion.cnombre + ' ' + cotizacion.capellidopaterno + ' ' "
							+ "              + cotizacion.capellidomaterno "
							+ "       END AS RazonSocial, "
							+ "       cotizacion.cmonto,catSexo.cSexo "
							+ " FROM   mcotizacionesprocedimiento cotizacion with(Nolock) "
							+ " inner join mcatalogoSexo as catSexo with(Nolock) on cotizacion.nIdSexo=catSexo.nIdSexo";

		 	}


		   if (strTabla.equals("CUCOPSGRIDPROCEDIMIENTOCOTIZACION")){
		 		strQuery = " SELECT nIdLineaConsolidado,cIdCABM,cDescripcion,nCantidad,mMontoMaximoUnitario,mMontoMaximoBruto,nPorcentajeIVA,mMontoMaximo FROM fn_mProcedimientoPartidas (" + func + ") AS mProcedimientoAdjudicacionPartidas ";

		 	}
		   if (strTabla.equals("LINEASSOLICITUD")){
			 	strQuery = " select nIdLineaSolicitud from [dbo].[fn_mConsolidadoLineasSolicitudPreseleccionadas](  '" + campos + "') ";

			 }
			if (strTabla.equals("SOLICITUDESDISPPRESEL")){
				strQuery = "select cIdSolicitud, cIdSubPartida, cDescripcion from dbo.fn_mConsolidadoPreseleccionSolicitudes('" + campos + "') "+
				" WHERE cIdSolicitud NOT IN( select replace (cSolicitud,' ','') as cIdSolicitud from fn_mConsolidadoSolicitudesPreseleccionadas ('" + campos.split(",")[3].replaceAll("\'","") + "'))";

			}
			if (strTabla.equals("LINEASDISPPRESEL")){
				strQuery = "select *  from (select cSolicitud, nIdLineaSolicitud, cDescripcion, cIdConsolidado, (cSolicitud+'-'+convert(varchar,nIdLineaSolicitud)) as dia from dbo.fn_mConsolidadoPreseleccionLineasSolicitud('" + campos + "'))   a" +
		 				" WHERE a.dia not in (select replace(cSolicitud,' ','')+'-'+convert(varchar,nIdLineaSolicitud) as dia from fn_mConsolidadoLineasSolicitudPreseleccionadas ('" + campos.split(",")[0].replaceAll("\'","") + "') as b)";
			}
			if (strTabla.equals("CONSOLIDADOPRESOLICITUDESSELECCIONADAS")){
					strQuery = "select cSolicitud, cIdSubPartida, cDescripcion from [dbo].[fn_mConsolidadoSolicitudesPreseleccionadas]('" + campos + "') ";

			 }
			if (strTabla.equals("LINEASPRESEL")){
					strQuery = "select cSolicitud, nIdLineaSolicitud, cDescripcion,cIdConsolidado from [dbo].[fn_mConsolidadoLineasSolicitudPreseleccionadas]('" + campos + "') ";

			 }
			if (strTabla.equals("CONSOLIDADOELIMINALINEASCONSOLIDADO")){
					strQuery = " EXEC sp_mConsolidadoEliminaLineasConsolidado  '" + campos + "' ";

			 }
			if (strTabla.equals("CONSOLIDADOLINEASSOLICITUDDISPONIBLES")){
					strQuery = "SELECT cIdSolicitud, nIdLineaSolicitud, cDescripcion,cDescripcionAdicional, cIdUnidadMedida,nCantidad FROM dbo.fn_mConsolidadoLineasSolicitudDisponibles('" + campos + "') ";

			 }
			if (strTabla.equals("CONSOLIDADORESUMENPARTIDAS")){
					strQuery = " SELECT cDescripcion,cIdCABM,nIdLineaConsolidado,nCantidadLineas,mMontoLinea,mCantidadLinea,mMontoLineaIVA, '' AS btnElimina FROM dbo.fn_mConsolidadoConsultaLineasConsolidado(  '" + campos + "') ";

			 }
			 if (strTabla.equals("CONSOLIDADOSOLICITUDLINEARESUMEN")){
					strQuery = " SELECT cIdSolicitud,	nIdLineaSolicitud,	cDescripcion,	cIdCABM,	nCantidad,	mPrecioUnitario,	mSubTotal, '' AS btnElimina FROM dbo.fn_mConsolidadoConsultaLineasConsolidadoDetalle(  '" + campos + "') ";

			}
			
			if (strTabla.equals("PAGODIRECTOPARTIDA")){
					strQuery = "select DISTINCT cIdSubPartida+'-'+cSubPartida cSubPartida, cIdSubPartida FROM tEventoConcepto a WITH(NOLOCK) inner join mCatalogoSubPartida b WITH(NOLOCK)"+
								" on a.cOBGINI=b.cIdSubPartida "+strMaxReg+ " AND cIdCapitulo in(2,3,5) "+
					" union all  "+
					" SELECT DISTINCT cIdSubPartida+'-'+cSubPartida cSubPartida, cIdSubPartida FROM tEventoConcepto a WITH(NOLOCK) inner join mCatalogoSubPartida b WITH(NOLOCK)" + 
					" on a.cOBGINI=b.cIdSubPartida " +strMaxReg+ " and a.cOBGINI in ('44101','44104')";
					
			}
			
			
		 	if (strTabla.equals("VDISPONIBLEPAGODIRECTOEP")){
		 		strQuery = "select " + campos + " from vDisponibleEP WITH(NOLOCK) " + strMaxReg;
		 	}
		 	
		 	if (strTabla.equals("MONTOMODIFICADO")){
		 		strQuery = "select isnull(SUM(montoTotalNeto),0) sumaFoliosHidden from mPagoDirectoFacturaEncabezado " + strMaxReg;
		 	}
		 	
		 	if (strTabla.equals("MONTOMODIFICADODETALLE")){
		 		strQuery = " select isnull(SUM(montoNeto),0) from mPagoDirectoFacturaDetalle " + strMaxReg;
		 	}
		 	
		 	if (strTabla.equals("MONTOVALIDAPAA")){
		 		strQuery="select rtrim(cidpartida), SUM(montoNeto) from  mPagoDirectoFacturaDetalle " + strMaxReg+" group by cIdPartida";
				System.out.println(strQuery);
		 	}
		 	
		 	
		 	if (strTabla.equals("MONTOVALIDAPAARELG")){
		 		strQuery="select rtrim(rgd.cIdSubPartida), SUM(rgd.mImporteTotalFacturaDetalle) from  mRelacionGastosFacturasDetalle rgd with(nolock) " + strMaxReg+" group by cIdSubPartida";
				System.out.println(strQuery);
		 	}
		 
		 	
		  //FIN SAI MODULO DE REQUISICIONES BIENES Y SERVICIOS

		  	//Muestra Datos CXP
		  	if(strTabla.equals("TPAGODIRECTOENCABEZADO") || strTabla.equals("TPAGODIVERSOENCABEZADO") || strTabla.equals("TPAGOOBRAENCABEZADO") || strTabla.equals("TNOMINAENCABEZADO")  || strTabla.equals("TNOMINACLCENCABEZADO") || strTabla.equals("TPAGOFEDERALIZADOENCABEZADO") || strTabla.equals("TOPERAJENASENCABEZADO")){
		  		strQuery = "SELECT "+campos+" FROM "+strTabla+" WITH(NOLOCK) WHERE "+strParam;
		  	}
		  	if(strTabla.equals("TPAGODIRECTODETALLE") || strTabla.equals("TRELACIONGASTOSDETALLE") || strTabla.equals("TPAGODIVERSODETALLE") || strTabla.equals("TNOMINADETALLE") || strTabla.equals("TPAGOFEDERALIZADODETALLE") ){
		  		strQuery = "SELECT TOP 500 SUBSTRING(EP, 0,56) as EP,CONVERT(varchar,SUM(CONVERT(money,mImporteBruto)),1) AS importeBruto,CONVERT(varchar,SUM(CONVERT(money,mImporteIva)),1) AS importeIVA,CONVERT(varchar,SUM(CONVERT(money,mRetencion)),1) AS retencion,CONVERT(varchar,SUM(CONVERT(money,mImporteNeto)),1) AS importeNeto FROM "+strTabla+" WITH(NOLOCK) WHERE LEFT(ISNULL(cEvento, ''), 8) != 'ANTICIPO' AND "+campos;
		   	}
		  	if(strTabla.equals("TNOMINACLCDETALLE")){
		  		strQuery = "SELECT TOP 500 EP,CONVERT(varchar,SUM(CONVERT(money,mImporteNeto)),1) AS importeBruto,CONVERT(varchar,SUM(CONVERT(money,0)),1) AS importeIVA,CONVERT(varchar,SUM(CONVERT(money,0)),1) AS retencion,CONVERT(varchar,SUM(CONVERT(money,mImporteNeto)),1) AS importeNeto FROM "+strTabla+" WITH(NOLOCK) WHERE "+campos;
		   	}
			if(strTabla.equals("TPAGOOBRADETALLE")){
				strQuery = "SELECT TOP 500 EP,CONVERT(varchar,SUM(CONVERT(money,mImporteBruto)),1) AS importeBruto,CONVERT(varchar,SUM(CONVERT(money,mIva)),1) AS importeIVA,CONVERT(varchar,SUM(CONVERT(money,mRetencion)),1) AS retencion,CONVERT(varchar,SUM(CONVERT(money,mImporteNeto)),1) AS importeNeto FROM "+strTabla+" WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND "+campos;
			}
			if(strTabla.equals("TOPERAJENASDETALLE")){
				strQuery = "SELECT TOP 500 EP, '-' AS importeBruto, '-' AS importeIVA, '-' AS retencion, CONVERT(varchar,SUM(CONVERT(money,mTotal)),1) AS importeNeto FROM "+strTabla+" WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND "+campos;
			}
		  	if(strTabla.equals("TRELACIONGASTOSENCABEZADO")){
		  		strQuery = "SELECT "+campos+" FROM TRELACIONGASTOSENCABEZADO WITH(NOLOCK) WHERE "+strParam;
			}
		  	//Inicio Capitulo Mil
			if(strTabla.equals("TNOMINADETALLECAPITULOMILCLC")){
		  		strQuery = "SELECT tnomDet.nDocRenglon, tnomDet.EP, tnomDet.cMes, tnomDet.ID_TIPO_MOVIMIENTO, tnomDet.ID_TIPO_CONCEPTO, tnomDet.mImporteNeto, tnomDet.cIdRelacion FROM TNOMINADETALLE tnomDet WITH(NOLOCK) WHERE "+campos;
		  		
			}
			if(strTabla.equals("TNOMINACLCENCABEZADOMIL")){
				strQuery = "SELECT nFolioNOMINACLC, Activo, isnull((select cDocumentoHaplicado from tEjercidoEncabezado where caNoContrarrecibo = "+campos+" ),'-') as ejercido FROM TNOMINACLCENCABEZADO WHERE caNoContrarreciboCLC = "+campos;
			}
			if(strTabla.equals("TNOMINADEVENCABEZADOMIL")){
				strQuery = "SELECT nFolioNOMINACLC, ISNULL(cDocumentoHaplicado, 'C') AS Activo FROM TNOMINADEVENCABEZADO WHERE "+campos;
			}
			if(strTabla.equals("TNOMINACLCDETALLEMIL")){
				strQuery = "SELECT tCLC.nFolioNOMINA, tCLC.nDocRenglon, tCLC.caNoContrarrecibo, tCLC.EP, tCLC.cMes, tCLC.ID_TIPO_MOVIMIENTO, tCLC.ID_TIPO_CONCEPTO, tCLC.mImporteNeto, caNoCompromiso, tCLC.cEvento, tCLC.nDocRenglonCLC FROM TNOMINACLCDETALLE tCLC WITH(NOLOCK), tNOMINAEncabezado tNom WITH(NOLOCK) WHERE "+campos+ " AND tCLC.caNoContrarrecibo = tNom.caNoContrarrecibo ";			
				
			}if(strTabla.equals("V_NOMINACAPITULOMIL")){
			
				strQuery = "SELECT SUM(importeNeto) AS importeNeto FROM V_NOMINACAPITULOMIL WHERE "+campos;			
			}
			if(strTabla.equals("TNOMINAENCABEZADO_MIL")){
			
				strQuery = "SELECT * FROM TNOMINAENCABEZADO WHERE "+campos;
				System.out.println("t: "+strQuery);			
			}
			if(strTabla.equals("TPAGADOENCABEZADOR")){
		  		strQuery = "SELECT "+campos+" FROM TPAGADOENCABEZADO WITH(NOLOCK) WHERE "+strParam;
		  	}
		  	if(strTabla.equals("TCONSOLIDACIONANEXO1")){ //URVP.PARA REINTEGROS DE ANEXO1
		  		strQuery = "SELECT "+campos+" FROM tconsolidacionrelaciongastosEncabezado WITH(NOLOCK) WHERE cDescripcionPoliza LIKE '%ANEXO1%' AND "+strParam;
		  	}
		  	if(strTabla.equals("TCONSOLIDACIONRIF")){ //REINTEGROS DE INGRESO FISCAL POR FUERA
		  		strQuery = "SELECT "+campos+" FROM tconsolidacionrelaciongastosEncabezado WITH(NOLOCK) WHERE cDescripcionPoliza LIKE '%RIF%' AND "+strParam;
		  	}
		  	if(strTabla.equals("SPDDETALLEXFOLIO")){ //URVP.09062014 DATOS REALES CON NUMEROSDE NDOCRENGLON 
				strQuery = "SELECT " + campos + " FROM tPagoDirectoDetalle WITH(NOLOCK) WHERE " + strParam;
			}
			if(strTabla.equals("V_TPAGADO")){
			
				strQuery = "SELECT DISTINCT cTipoPago FROM v_TPagado WITH(NOLOCK) WHERE "+campos;			
			}
			if(strTabla.equals("V_TAPGADORADICADO")){
				strQuery = "SELECT DISTINCT cTipoPago FROM v_TPagadoRadicado WITH(NOLOCK) WHERE "+campos;	
			}
			if(strTabla.equals("TNOMINAERROR_MIL")){
				
				strQuery = "SELECT TOP 200 EP, elError, elMonto FROM tmpNominaError WITH(NOLOCK) WHERE "+campos;			
			}
			
			//Fin Capitulo Mil
			
			if(strTabla.equals("TLAYOUTSCREADOSRELACIONGASTOSHEADER2")){
				strQuery = "SELECT "+campos+" FROM tLayoutsCreadosRelacionGastosHeader as l INNER JOIN tRELACIONGASTOSEncabezado as r ON l.sNoContrarrecibo = r.caNoContrarrecibo WHERE "+strParam;
			}
			
			//Muestra Datos SICOP
			if(strTabla.equals("DATOSSICOP")){
				strQuery = "SELECT TOP 500 SICOP.NCLC_43,SIAFF.BENEFICIARIO,FECHA_APL AS FECHA_APL_SICOP,SICOP.FOLIO_SIAFF_112 AS CLC_SIAFF,FECHA_APLICACION AS FECHA_APLC_SIAFF,FECHA_PAGO AS FECHA_PAGO_SIAFF,"
						+" CONVERT(VARCHAR,SUM(CONVERT(money,IMP_NETO_107)),1) AS IMPORTE_NETO_SICOP,CONVERT(VARCHAR,CONVERT(money,RETENCION.IMP_RETE_49),1) AS RETENCIONES,CONVERT(VARCHAR,SUM(CONVERT(MONEY,IMPORTE_148)),1) AS TOTAL_EJERCIDO, SIAFF.ESTATUS_CLC, SIAFF.APLICACION_CONTABLE "
						+" FROM CLC_SICOP AS SICOP WITH(NOLOCK) INNER JOIN CLC_SIAFF_ENC AS SIAFF WITH(NOLOCK) ON SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC LEFT JOIN CLC_SICOP_RETENCION AS RETENCION WITH(NOLOCK) ON SICOP.NCLC_43=RETENCION.NCLC_74 "
						+" WHERE "+campos;
			}
			if(strTabla.equals("DATOSSICOP_NOMINA")){
				strQuery = "SELECT TOP 500 SICOP.NCLC_43,SIAFF.BENEFICIARIO,FECHA_APL AS FECHA_APL_SICOP,SICOP.FOLIO_SIAFF_112 AS CLC_SIAFF,FECHA_APLICACION AS FECHA_APLC_SIAFF,FECHA_PAGO AS FECHA_PAGO_SIAFF,"
						  +" CONVERT(VARCHAR,SUM(CONVERT(money,IMP_NETO_107)),1) AS IMPORTE_NETO_SICOP,'0.00' AS RETENCIONES,CONVERT(VARCHAR,SUM(CONVERT(MONEY,IMPORTE_148)),1) AS TOTAL_EJERCIDO, SIAFF.ESTATUS_CLC, SIAFF.APLICACION_CONTABLE "
						  +" FROM CLC_SICOP AS SICOP WITH(NOLOCK),CLC_SIAFF_ENC AS SIAFF WITH(NOLOCK) "
						  +" WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC "+campos;
			}
			if(strTabla.equals("DATOSSICOPDETALLES")){
				strQuery = " SELECT TOP 500 CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END AS EP, " //.'+SUBSTRING(CCAU_162,8,3)+'.'+CASE LEN(CCOP_163) WHEN 1 THEN 'B0'+CCOP_163 WHEN 2 THEN 'B'+CCOP_163 WHEN 3 THEN CCOP_163 END AS EP,"
							+" CONVERT(varchar,SUM(CONVERT(money,IMP_NETO_107)),1) AS IMPORTE_NETO_SICOP,CONVERT(varchar,SUM(CONVERT(money,IMP_RETE_26)),1) AS RETENCIONES,CONVERT(varchar,SUM(CONVERT(money,IMPORTE_148)),1) AS TOTAL_EJERCIDO "
							+" FROM CLC_SICOP WITH(NOLOCK)"
							+" WHERE "+campos;
			}
			/* Carga Masiva Layout Relacion Gastos */
			
			if(strTabla.equals("TRELACIONGASTOSENCABEZADO_TEMP")){
				strQuery = " SELECT cIdRelacion, convert(varchar,convert(money,mImporteNeto),1) as mImporteNeto, cIdRFC, convert(varchar,convert(date,fAplicacion),103), estatus FROM tRELACIONGASTOSEncabezado_temp WITH(NOLOCK)"
							+" WHERE "+campos;				
			}
			
			if(strTabla.equals("TPASIVOSCONTINGENTESLABORALES_TEMP")){
				strQuery = " SELECT cRFC, CONVERT(VARCHAR,CONVERT(MONEY,mImporte),1) AS mImporteNeto, cMes, estatus FROM tpasivosContingentesLaborales_temp WITH(NOLOCK)"				
							+" WHERE "+campos;				
			}
			
			if(strTabla.equals("TPASIVOSCONTINGENTESLABORALES_LIMPIA")){
				strQuery = " SELECT cRFC, CONVERT(VARCHAR,CONVERT(MONEY,mImporte),1) AS mImporteNeto, cMes, estatus FROM tpasivosContingentesLaborales_temp WITH(NOLOCK)"				
							+" WHERE 1 = 1";				
			}
			
			if(strTabla.equals("TCOMPROMISONOMINAENCABEZADO_DEV")){
				strQuery = " SELECT fCarga, cIdContrato, cTipoContrato, fAplicacion, cCentroContable, cRamo, cUnidadResponsable, cTipoPoliza, nMes, aEjercicioFiscal, cUnidadResponsable, cDescripcionPoliza, nFolioSICOP, isnull(cDocumentoHaplicado,'0') AS statusDocumento "
							+" FROM TCOMPROMISONOMINAENCABEZADO WITH(NOLOCK)"
							+" WHERE "+campos;
			}
			if(strTabla.equals("V_COMPROMISODEVCLC")){
				strQuery = " SELECT SUM(retencionTe) AS retencion FROM V_COMPROMISODEVCLC WITH(NOLOCK) WHERE "+campos;
			}
			if(strTabla.equals("V_NOMINACAPITULOMILDEV")){
				strQuery = " SELECT SUM(importeneto) FROM v_NominaCapituloMilDev WITH(NOLOCK) WHERE "+campos;
				//System.out.println("sum: "+strQuery);
			}
			if(strTabla.equals("TPAGADOENCABEZADO")){
				strQuery = "SELECT nFolioSicop, nFolioPagado FROM TPAGADOENCABEZADO "+campos;
				
			}
			if(strTabla.equals("TPAGADOENCABEZADOINTEGRACION")){
				strQuery = "SELECT e.nFolioSicop, e.nFolioPagado FROM tLayoutsCreadosRelacionGastosHeader as l INNER JOIN tPagadoEncabezado as e ON l.sNoContrarrecibo = e.caNoContrarrecibo "+campos;
			}
			//Acuerdos AMF
			if(strTabla.equals("FACTURA_ACUERDOS_ADMINISTRACION")){
				if(u.getU_UR().equals("A02")){
					strQuery = "SELECT tAE.nClaveAMF, tAD.cFolio, tAD.cUnidadResponsable, '$'+CONVERT(varchar,(CONVERT(money,tAD.mSaldo)),1), fVigencia FROM tAcuerdosMFEncabezado tAE INNER JOIN tAcuerdosMFDetalle tAD " 
					     +" ON tAE.cFolio = tAD.cFolio WHERE tAE.nClaveAMF = tAD.nClaveAMF AND tAD.mSaldo > 0 AND tAE.fVigencia >= CONVERT(date,GETDATE()) ";
				}else{
					strQuery = "SELECT tAE.nClaveAMF, tAD.cFolio, tAD.cUnidadResponsable, '$'+CONVERT(varchar,(CONVERT(money,tAD.mSaldo)),1), fVigencia FROM tAcuerdosMFEncabezado tAE INNER JOIN tAcuerdosMFDetalle tAD " 
					     +" ON tAE.cFolio = tAD.cFolio WHERE tAE.nClaveAMF = tAD.nClaveAMF AND tAD.mSaldo > 0 AND tAD.cUnidadResponsable = '" +u.getU_UR()+ "' AND tAE.fVigencia >= CONVERT(date,GETDATE()) ";
				}
			}
			// Ejercido Pagado Logs
			if(strTabla.equals("TDETALLEEJERCIDOPAGADO")){
				strQuery = "SELECT distinct tipoDocumento, cTipoPago, caNoContrarrecibo, clcSicop, CONVERT(varchar(10),fechaAplicacion,103) as fechaAplicacion, isnull(EP, '-') as EP, '$ '+CONVERT(varchar,(CONVERT(money,Importe)),1), descripcion FROM TDETALLEEJERCIDOPAGADO WHERE 1=1 "+campos;
				
			}
			// Ejercido Pagado Comparativo Ejercido SAI / SICOP
			if(strTabla.equals("V_EJERCIDOCOMPARATIVO")){
				strQuery = "SELECT SUM(sicop) sicop, CONVERT(varchar,SUM(convert(money, Total_Ejercido_SICOP)),1) totalEjercidoSICOP, SUM(CXP) cxp, CONVERT(varchar,SUM(convert(money,Total_Ejercido_SAI)),1) totalEjercidoSAI, CONVERT(varchar,SUM(convert(money, Total_Ejercido_SICOP)) - SUM(convert(money,Total_Ejercido_SAI)),1)  totalDiferencia FROM  v_EjercidoComparativo WHERE 1=1 ";
				
			}
			if(strTabla.equals("EJERCIDOCOMPARATIVOCAPITULO")){
			
				strQuery = " SELECT vEC.unidadSicop, "
									 +" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									 +"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									 +"		where vE.capituloSicop = 1 "
			 						 +"		 and vE.unidadSicop = vEC.unidadSicop "
		 							 +"		),0.00),1) as mil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 2 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			  						+"		),0.00),1) as dosMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 3 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			  						+"		),0.00),1) as tresMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 4 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			 						+"		),0.00),1) as cuatroMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 5 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			  						+"		),0.00),1) as cincoMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 6 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			 						+"		),0.00),1) as seisMil, "
			 						
									+" '$'+ CONVERT(varchar, ( "
		  							+"	  isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop ),0.00) " 
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+" ),1) as totalGeneral, "
								//sai
									+" vEC.unidadSicop, " 
									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+" 			where tEE.nFolioEjercido = tED.nFolioEjercido "
									+" 			and SUBSTRING(EP,57,3) = vEC.unidadSicop "
									+"			and tEE.cDocumentoHaplicado = 'S' "
			  						+" 			and SUBSTRING(EP,32,1) = 1 "
 									+" ),0.00),1) as milSAI, "
 		
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+" 			where tEE.nFolioEjercido = tED.nFolioEjercido "
									+" 			and SUBSTRING(EP,57,3) = vEC.unidadSicop "
									+" 			and tEE.cDocumentoHaplicado = 'S' "
			  						+" 			and SUBSTRING(EP,32,1) = 2 "
 									+" ),0.00),1) as dosMilSAI, "
 									
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+" 			where tEE.nFolioEjercido = tED.nFolioEjercido "
									+"			and SUBSTRING(EP,57,3) = vEC.unidadSicop "
									+"			and tEE.cDocumentoHaplicado = 'S' "
			  						+" 	  		and SUBSTRING(EP,32,1) = 3 "
 									+" ),0.00),1) as tresMilSAI, "
 									
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido "
									+"   		and SUBSTRING(EP,57,3) = vEC.unidadSicop "
									+"          and tEE.cDocumentoHaplicado = 'S' "
			 						+"	        and SUBSTRING(EP,32,1) = 4 "
 									+" ),0.00),1) as cuatroMilSAI, "
 									
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"		   where tEE.nFolioEjercido = tED.nFolioEjercido "
									+" 		   and SUBSTRING(EP,57,3) = vEC.unidadSicop "
									+"         and tEE.cDocumentoHaplicado = 'S' "
			 						+"		   and SUBSTRING(EP,32,1) = 5 "
 									+"	),0.00),1) as cincoMilSAI, "
 									
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"        where tEE.nFolioEjercido = tED.nFolioEjercido "
									+"		  and SUBSTRING(EP,57,3) = vEC.unidadSicop "
									+"        and tEE.cDocumentoHaplicado = 'S' "
			  								//and SUBSTRING(EP,27,4) = CPPT_156 "
			 						+"        and SUBSTRING(EP,32,1) = 6 "
 									+" ),0.00),1) as seisMilSAI, "
 							//totalGeneralSAI
 							
 									+" '$'+ CONVERT(varchar, (  isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " 
									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) "
 									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) "
 									+"  + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) "
 									+"  + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) "
 									+"  + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) "
 									+" ),1) as totalGeneralSAI, "
 									
 							//Diferencia
									
									+" '$'+ CONVERT(varchar, ( "
		  							+"	  isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop ),0.00) " 
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop ),0.00) "
									+"	) "
									+"	- "
									+"	( "
 									+"	  isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " 
									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) "
 									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) "
 									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) "
 									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) "
 									+"	+ isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) "
 		
 									+" ),1) as diferencia "
 									
 					+" FROM v_EjercidoComparativoCapitulo vEC WITH(NOLOCK) GROUP BY vEC.unidadSicop ORDER BY SUBSTRING(unidadSicop,2,2) ";
	
			}
			
			if(strTabla.equals("EJERCIDOCOMPARATIVOCAPITULODETALLE")){
			
				strQuery =  " SELECT vEC.unidadSicop, vEC.partidaSicop, "
									+"  '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+" 		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"  	where vE.capituloSicop = 1 " 
			  						+" 		and vE.unidadSicop = vEC.unidadSicop "
			  						+" 		and vE.partidaSicop = vEC.partidaSicop "
									+"		),0.00),1) as mil, "
		
									+"  '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 2 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			  						+"		and vE.partidaSicop = vEC.partidaSicop "
									+"		),0.00),1) as dosMil, "
		
									+"	'$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 3 "
									+"		and vE.unidadSicop = vEC.unidadSicop "
									+"		and vE.partidaSicop = vEC.partidaSicop "
									+"		),0.00),1) as tresMil, "
		
									+"  '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"  	from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 4 "
			 						+"		and vE.unidadSicop = vEC.unidadSicop "
			  						+"		and vE.partidaSicop = vEC.partidaSicop "
									+"		),0.00),1) as cuatroMil, "
		
									+"	'$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"		from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 5 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			 						+"		and vE.partidaSicop = vEC.partidaSicop "
									+"		),0.00),1) as cincoMil, "
		
									+"  '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) " 
									+"  	from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"		where vE.capituloSicop = 6 "
			  						+"		and vE.unidadSicop = vEC.unidadSicop "
			  						+"		and vE.partidaSicop = vEC.partidaSicop "
									+"		),0.00),1) as seisMil, "
									
									+" '$'+ CONVERT(varchar, ( "
		  							+"	 isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+"	+ isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+" ),1) as TotalGenera ,"
									
									//SAI
									
									+" vEC.unidadSicop, vEC.partidaSicop, "
									+"	'$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' "
			    					+"			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 1 "
 									+"	),0.00),1) as milSAI, "
 		
 									+"	'$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' "
			    					+"			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 2 "
 									+"	),0.00),1) as dosMilSAI, "
 		
 									+"	'$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' "
			    					+"			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 3 "
 									+"	),0.00),1) as tresMilSAI, "
 		
 									+"	'$ '+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' "
			   						+"			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 4 "
 									+"	),0.00),1) as cuatroMilSAI, "
 		
 									+"	'$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' "
			    					+"			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 5 "
 									+"	),0.00),1) as cincoMilSAI, "
 		
 									+"	'$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"			where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' "
			    					+"			and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 6 "
 									+"	),0.00),1) as seisMilSAI, "
 									
 									// totalGeneralDetalle
		
		  							+" '$'+ CONVERT(varchar, ( isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 1 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 2 ),0.00) "
 		                            +" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 3 ),0.00) " 
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 4 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 5 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 6 ),0.00) "
									+" ),1) as totalGeneralDetalle, "
									
									//Diferencia
		
									+" '$ '+ CONVERT(varchar, ( isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 and vE.unidadSicop = vEC.unidadSicop and vE.partidaSicop = vEC.partidaSicop ),0.00) "
									+"	) "
									+" - "
									+" ( isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 1 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 2 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 3 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 4 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 5 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and SUBSTRING(EP,57,3) = vEC.unidadSicop and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,27,4) = vEC.partidaSicop and SUBSTRING(EP,32,1) = 6 ),0.00) "
									+" ),1) as Diferencia "
									
								+" FROM v_EjercidoComparativoCapitulo vEC WITH(NOLOCK) "
								+" WHERE unidadSicop = '"+ campos +"' GROUP BY vEC.unidadSicop, vEC.partidaSicop " 
								+" ORDER BY SUBSTRING(unidadSicop,2,2) ";

			
			}
			
			if(strTabla.equals("EJERCIDOCOMPARATIVOTOTALCAPITULO")){
			
				strQuery = " SELECT	top 1 "
	    							+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) " 
									+" 								 from v_EjercidoComparativoCapitulo vE with(nolock) " 
									+"  							 where vE.capituloSicop = 1 "
									+" 	),0.00),1)as mil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"   							from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"   							where vE.capituloSicop = 2 "
									+" ),0.00),1) as dosMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"   							from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"   							where vE.capituloSicop = 3 "
									+" ),0.00),1) as tresMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"   							from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"   							where vE.capituloSicop = 4 "
			  						+" ),0.00),1) as cuatroMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"   							from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"   							where vE.capituloSicop = 5 "
									+" ),0.00),1) as cincoMil, "
		
									+" '$'+ CONVERT(varchar, isnull((select SUM(vE.importe_148Sicop) "
									+"   							from v_EjercidoComparativoCapitulo vE with(nolock) "
									+"   							where vE.capituloSicop = 6 "
									+" ),0.00),1) as seisMil, "
									
									+" '$'+ CONVERT(varchar, ( "
		  							+"   isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 ),0.00) " 
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 ),0.00) "
									+" ),1) as totalGeneral, "
		
							//SAI 
			
									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"  where tEE.nFolioEjercido = tED.nFolioEjercido  "
									+"	  and tEE.cDocumentoHaplicado = 'S' "
									+"	  and SUBSTRING(EP,32,1) = 1 "
 									+"  ),0.00),1) as milSAI, "
 		
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"  where tEE.nFolioEjercido = tED.nFolioEjercido  "
									+"		and tEE.cDocumentoHaplicado = 'S' "
									+"		and SUBSTRING(EP,32,1) = 2 "
 									+" ),0.00),1) as dosMilSAI, "
 		
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"   where tEE.nFolioEjercido = tED.nFolioEjercido "
									+"		 and tEE.cDocumentoHaplicado = 'S' "
									+"		 and SUBSTRING(EP,32,1) = 3 "
 									+" ),0.00),1) as tresMilSAI, "
 		
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"  where tEE.nFolioEjercido = tED.nFolioEjercido  "
									+"  and tEE.cDocumentoHaplicado = 'S' "
									+"  and SUBSTRING(EP,32,1) = 4 "
 									+" ),0.00),1) as cuatroMilSAI, "
 		
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"  where tEE.nFolioEjercido = tED.nFolioEjercido "
									+"  and tEE.cDocumentoHaplicado = 'S' "
									+"  and SUBSTRING(EP,32,1) = 5 "
 									+" ),0.00),1) as cincoMilSAI, "
 		
 									+" '$'+ CONVERT(varchar, isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) "
									+"   where tEE.nFolioEjercido = tED.nFolioEjercido  "
									+"   and tEE.cDocumentoHaplicado = 'S' "
			 						+"   and SUBSTRING(EP,32,1) = 6 "
 									+" ),0.00),1) as seisMilSAI, "
 									
 									+" '$'+ CONVERT(varchar, ( "
 		  							+" isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " 
									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) "
 									+" ),1) as totalGeneralSAI, "
 									
 									//Diferencia 
 		
 									+" '$'+ CONVERT(varchar, ( "
		  							+" isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 1 ),0.00) " 
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 2 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 3 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 4 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 5 ),0.00) "
									+" + isnull((select SUM(vE.importe_148Sicop) from v_EjercidoComparativoCapitulo vE with(nolock) where vE.capituloSicop = 6 ),0.00) "
									+" ) "
									+" - "
 									+" ( "
 		  							+" isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 1 ),0.00) " 
									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 2 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 3 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 4 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 5 ),0.00) "
 									+" + isnull((select SUM(mImporteNeto) as mImporteNetoSAI from tEjercidoEncabezado tEE with(nolock), tEjercidoDetalle tED with(nolock) where tEE.nFolioEjercido = tED.nFolioEjercido and tEE.cDocumentoHaplicado = 'S' and SUBSTRING(EP,32,1) = 6 ),0.00) "
 									+" ),1) as diferencia "
 		
 					+" FROM v_EjercidoComparativoCapitulo vEC "
					+" WITH(NOLOCK) " 
					+" GROUP BY vEC.capituloSicop ";
 			
			}
			
			// Ejercido Pagado Comparativo Ejercido SAI / SICOP
			if(strTabla.equals("V_EJERCIDOCOMPARATIVODATOS")){
				strQuery = "SELECT UnidadResponsableSicop, sicop, Total_Ejercido_SICOP, UnidadResponsableCXP, CXP, Total_Ejercido_SAI, diferencia FROM  v_EjercidoComparativo ";
				//System.out.println("steQuer:: "+strQuery);
			}
			// Cuentas Bancarias
			if(strTabla.equals("TCUENTABANCARIAENCABEZADOINF")){
				strQuery = "SELECT "+campos+" FROM TCUENTABANCARIAENCABEZADO WITH(NOLOCK) WHERE "+strParam ;
				
			}
			
			if(strTabla.equals("TCUENTABANCARIAENCABEZADOINFDET")){
				strQuery = "SELECT "+campos+" FROM TCUENTABANCARIAFIRMANTEDETALLE WITH(NOLOCK) WHERE "+strParam ;
				
			}
			if(strTabla.equals("TCUENTABANCARIAENCABEZADOMODINF")){
				strQuery = "SELECT "+campos+" FROM TCUENTABANCARIAMODENCABEZADO WITH(NOLOCK) WHERE "+strParam ;
				
			}
			if(strTabla.equals("TCUENTABANCARIAENCABEZADOMODINFDET")){
				strQuery = "SELECT "+campos+" FROM TCUENTABANCARIAFIRMANTEMODDETALLE WITH(NOLOCK) WHERE "+strParam ;
				
			}
			
			if(strTabla.equals("TCUENTABANCARIAENCABEZADOCANINF")){
				strQuery = "SELECT "+campos+" FROM TCUENTABANCARIACANENCABEZADO WITH(NOLOCK) WHERE "+strParam ;
				
			}
			// PAOP
			if(strTabla.equals("TPROGRAMAANUALOBRAPUBLICA")){
				strQuery = "SELECT cEjercicioFiscal,nMes ,nRenglon, cCVE_CUCOP, cCONCEPTO, mMultianualEstimado, mEstimadoMipymes, mEstimadoNoCubiertasTLC, nCantidad, nUnidadMedida, cTipoProcContratacion, nEntidadFederativa, nTrimestre1, nTrimestre2, nTrimestre3, nTrimestre4, CONVERT(VARCHAR, fInicialContrato, 103), nPlurianual, nEjerciciosFicales, mAnualEjercer, cComentario1, CONVERT(VARCHAR, fFinalContrato, 103), cComentario3, cTipoProcedimiento FROM TPROGRAMAANUALOBRAPUBLICA "+campos;
				System.out.println(strQuery);
			}if(strTabla.equals("TPROGRAMAANUALOBRAPUBLICAMAXIMO")){
				strQuery = "SELECT isnull(MAX(nRenglon)+1,1) AS MAX FROM TPROGRAMAANUALOBRAPUBLICA "+campos;
				System.out.println(strQuery);
			}
			//Avance Fisico  MLR RO-0011
			if(strTabla.equals("TSOLICITUDPAGO")){
				//strQuery = "SELECT noEstimacion,eFiscalPago, '$ '+CONVERT(VARCHAR,CAST( ISNULL(mMontoEstimacion,0) AS money),1) AS mMontoEstimacion,nPorceAvanceFisicoEstimado,nPorceAvanceFisicoEjecutado,nPorceAvanceFisicoProgramado,'$ '+CONVERT(VARCHAR,CAST( ISNULL(mmontoFisicoEjecutado,0) AS money),1) AS mmontoFisicoEjecutado,'$ '+CONVERT(VARCHAR,CAST( ISNULL(mmontoFisicoProgramado,0) AS money),1) AS mmontoFisicoProgramado, fEntregaVentanilla, isnull( mesEstimado,1) mesEstimado, fperiodoEstimacionIni, fperiodoEstimacionFin  FROM tObraPublicaAvanceFisico "+campos;
				strQuery = "SELECT distinct avfis.noEstimacion,avfis.eFiscalPago, '$ '+CONVERT(VARCHAR,CAST( ISNULL(avfis.mMontoEstimacion,0) AS money),1) AS mMontoEstimacion, nPorceAvanceFisicoEstimado,nPorceAvanceFisicoEjecutado,"
							+ " nPorceAvanceFisicoProgramado, '$ '+CONVERT(VARCHAR,CAST( ISNULL(mmontoFisicoEjecutado,0) AS money),1) AS mmontoFisicoEjecutado, '$ '+CONVERT(VARCHAR,CAST( ISNULL(mmontoFisicoProgramado,0) AS money),1) AS mmontoFisicoProgramado,"
							+ " case when CONVERT(VARCHAR(12), fEntregaVentanilla, 103) = '01/01/1900' THEN '' ELSE   CONVERT(VARCHAR(12), fEntregaVentanilla, 103) end, "
							+ " isnull( mesEstimado,1) mesEstimado,"
							+ "case when CONVERT(VARCHAR(12), fperiodoEstimacionIni, 103) = '01/01/1900' THEN '' ELSE   CONVERT(VARCHAR(12), fperiodoEstimacionIni, 103) end,"
							+ "case when CONVERT(VARCHAR(12), fperiodoEstimacionFin, 103) = '01/01/1900' THEN '' ELSE   CONVERT(VARCHAR(12), fperiodoEstimacionFin, 103) end,"
							+ " CASE WHEN siaff.FOLIO_CLC = sicop.FOLIO_SIAFF_112 and sicop.FOLIO_SIAFF_112 = pe.nFolioSIAFF and obrae.cDocumentoHaplicado = 'S' and siaff.ESTATUS_CLC = 'Pagada' THEN 'PAGADO'"
							+ " ELSE 'PENDIENTE'"
							+ " END,"
							+ " CASE WHEN siaff.FECHA_PAGO != 'null' then siaff.FECHA_PAGO else '' end "
							+",est.cEstatus,est.nIdEstatus"
							+",CASE WHEN est.nidestatus <> 1 THEN \'\' ELSE \'<input type=\"button\" id=\"btnEnviar\" name=\"btnEnviar\" value=\"Enviar\" onclick=\"enviarEstimacionAut(\'+avfis.noestimacion+\')\" class=\"btn btn-outline-primary btn-sm\"/>\'END botonEnviar"
							+ " FROM tObraPublicaAvanceFisico avfis with(Nolock)"
							+" left join tEstatusAvanceFisico est with(Nolock)	on est.nIdEstatus=avfis.nIdEstatus "
							+ " left join tpagoobraencabezado obrae with(Nolock) on avfis.noestimacion = obrae.cNoEstimacion and avfis.mmontoestimacion = obrae.mImporteMasIva and obrae.cDocumentoHaplicado != 'C'"
							+ " left join tpagoobraencabezado poe with(Nolock) on poe.cFolioContratoObra = avfis.ccvecontrato and poe.cnoestimacion = avfis.noestimacion and poe.cDocumentoHaplicado ='S'"
							+ " left join tpagadoencabezado pe with(Nolock) on pe.nFolioPAGO = obrae.nFolioPAGOOBRA and pe.cTipoPago ='PAGOOBRA'"
							+ " left join clc_sicop sicop with(Nolock) on sicop.FOLIO_SIAFF_112 = pe.nFolioSIAFF"
							+ " left join CLC_SIAFF_ENC siaff with(Nolock) on siaff.FOLIO_CLC = sicop.FOLIO_SIAFF_112 "
							+ campos;
				System.out.println(strQuery);
			}  
			if(strTabla.equals("MUESTRA_TODOS")){
				strQuery = "SELECT tOp 100 cIdRFC, cnombre FROM v_PCatalogoRFC";
			}
			// Registro Diario Bancos
			
			if(strTabla.equals("TBANCOSRDB")){
			
				strQuery = "SELECT top 150 dBancoAbreviado, dBanco, cBanco FROM tBancos WHERE dBancoAbreviado IN ('BBVA BANCOMER','SCOTIABANK','BANORTE') ";
				
			}
			if(strTabla.equals("TCATALOGOCONCEPTO_RDB")){
			
				strQuery = "SELECT top 150 sConcepto, idRdbCat_Concepto FROM tRdbCat_Concepto";
				
			}
			if(strTabla.equals("TCATALOGOMEDIOPAGO_RDB")){
			
				strQuery = "SELECT top 150 sMedioPago, idRdbCat_MedioPago FROM tRdbCat_MedioPago";
				
			}
			if(strTabla.equals("TCATALOGODOCORIGEN_RDB")){
			
				strQuery = "SELECT top 150 sDocumentoOrigen, idRdbCat_DocumentoOrigen FROM tRdbCat_DocumentoOrigen";
				
			}
			if(strTabla.equals("TCATALOGOORIGENPAGO_RDB")){
			
				strQuery = "SELECT top 150 sOrigenDeposito, idRdbCat_OrigenDeposito FROM tRdbCat_OrigenDeposito";
				
			}
			if(strTabla.equals("TRDBCARGAARCHIVO_RDB")){
			
				strQuery = "SELECT idCargaArchivoRDB, sReferencia, dFecha, mImporte, mImporteSinF, sBeneficiario, sBeneficiarioCompleto, sOrigenDeposito, sDocumentoOrigen, sConcepto, sMedioPago, sCuentaEntidadContable, sNotas, tipoMovimiento, isnull(folioSai,'-') as folioSai, sNombreFirmante, nFolio, sSucursalPlaza, sTipoPago, sReferenciaPago, banco, tipoBancomer, sMovimiento, cuentaBancaria, sTransaccionDescripcion, cuentaOrigen FROM v_RdbCargaArchivoRDB with(nolock) "+campos;
				//System.out.println("je: "+strQuery);
			}
			if(strTabla.equals("TRDBCAT_DOCUMENTOSOPORTE")){
			
				strQuery = "SELECT idRdbCat_DocumentoSoporte, DocumentoSoporte FROM tRdbCat_DocumentoSoporte with(nolock) "+campos;
				
			}
			if(strTabla.equals("TRDBCARGAARCHIVOSUMACIERRE_RDB")){
			
				strQuery = "SELECT * FROM tRdbCargaArchivoRDBSaldo with(nolock) "+campos;
				
			}
			if(strTabla.equals("TRDBCAT_CONCEPTOMEDIOPAGO")){
				
				strQuery = "SELECT tCMP.idRdbCat_ConceptoMedioPago, tC.sConcepto, tMP.sMedioPago, tCMP.activo FROM tRdbCat_ConceptoMedioPago tCMP with(nolock) ";
				strQuery += "INNER JOIN tRdbCat_Concepto tC with(nolock) ON tCMP.sConcepto =  tC.idRdbCat_Concepto ";
				strQuery += "INNER JOIN tRdbCat_MedioPago tMP with(nolock) ON tCMP.sMedioPago = tMP.idRdbCat_MedioPago " +campos;
				
			}if(strTabla.equals("TRDBCAT_EXISTEFOLIO")){
			
				strQuery = "SELECT * FROM tRdbCargaArchivoRDB with(nolock) "+campos;
				
			}if(strTabla.equals("TRDBCAT_FOLIOMAXIMO")){
			
				strQuery = "SELECT  max(convert(int, folioSai)) as folioSai FROM tRdbCargaArchivoRDB with(nolock) "+campos;
				
			}if(strTabla.equals("TRDBENCABEZADOEXISTE")){
			
				//strQuery = "SELECT fAplicacion, nFolioRDB, MAX(nDocRenglon + 1) as renglon FROM TRDBENCABEZADO with(nolock) "+campos;
				strQuery = "SELECT tE.fAplicacion, tE.nFolioRDB, MAX(isnull(nDocRenglon,0) + 1) as renglon, caNoRDB FROM TRDBENCABEZADO tE with(nolock) LEFT JOIN tRDBDetalle tD with(nolock)  ON  tE.nFolioRDB = tD.nFolioRDB  "+campos;
				//System.out.println("strQuery: "+strQuery);
				
			}if(strTabla.equals("V_RDBCARGAARCHIVORDB")){
			
				strQuery = "SELECT  nFolioRDB, fAplicacion, cUnidadResponsable, aEjercicioFiscal, '$'+CONVERT(VARCHAR,(CONVERT(money,sum(mImporteSinF))),1) importeTotal, idRdbCargaArchivoRDBSaldo FROM V_RDBCARGAARCHIVORDB_Terminados WITH(NOLOCK) "+campos+ " GROUP BY fAplicacion, idRdbCargaArchivoRDBSaldo, cUnidadResponsable, aEjercicioFiscal, nFolioRDB ";
			}
			if(strTabla.equals("V_RDBCARGAARCHIVORDB_DET")){
			
				strQuery = "SELECT folioSai, nFolio, banco, tipoBancomer, cuentaBancaria, sReferencia, isnull(sMovimiento,'-') sMovimiento, cConcepto, sBeneficiario, mImporte, tipoMovimiento FROM V_RDBCARGAARCHIVORDB_Terminados WITH(NOLOCK) "+campos;
			}
			if(strTabla.equals("V_RDBCARGAARCHIVORDB_TOTALES")){
				
				strQuery = "SELECT "; 
				strQuery +=	"(select '$'+convert(varchar,sum(convert(money,(mImporte))),1) mImporteEgreso from V_RDBCARGAARCHIVORDB_Terminados where "+campos+" AND tipoMovimiento = 'EGRESO' ) as  importeEgreso, "; 
				strQuery +=	"(select '$'+convert(varchar,sum(convert(money,(mImporte))),1) mImporteEgreso from V_RDBCARGAARCHIVORDB_Terminados where "+campos+" AND tipoMovimiento = 'INGRESO' ) as  importeIngreso ";
				strQuery +=	" FROM V_RDBCARGAARCHIVORDB_Terminados WITH(NOLOCK) group by tipoMovimiento ";
			
			}
			
			
			 // CXP Nomina Capitulo MIld
			 if (strTabla.equals("NOMINACAPITULOMIL"))
				{
					strQuery =  " SELECT caNoContrarrecibo, cUnidadResponsable, convert(varchar(10), faplicacion, 103) as fAplicacion, cIdRFC, mImporteNeto,  isnull(cDocumentoHaplicado, '') as docAplicado, nEnviadoSICOP, solicitudPago, cConcepto " ;
					strQuery += " FROM tNOMINAEncabezado WITH(NOLOCK) WHERE ISNULL(cDocumentoHaplicado,'') NOT IN ('C','N')";
				}
			if(strTabla.equals("REGISTROPASIVO")){
				if(u.getU_UR().equals("A02")){
				strQuery = "SELECT tre.clavePasivo, tre.referenciaAMF, tre.rfcAMF, tre.cuentaBancaria, tre.fechaCaptura, tre.fechaPago, tre.importePago, tre.estatus "
					 		+ " FROM  tRegistroPasivoEncabezado tre ";
					  		
				}else{
					strQuery = "SELECT tre.clavePasivo, tre.referenciaAMF, tre.rfcAMF, tre.cuentaBancaria, tre.fechaCaptura, tre.fechaPago, tre.importePago, tre.estatus"
				 		+ " FROM  tRegistroPasivoEncabezado tre "
				  		+ " WHERE  tre.cUnidadResponsable = '" +u.getU_UR()+ "' ";		
				}
			}
			
			if(strTabla.equals("BUSCA_ACUERDOS")){
				if(u.getU_UR().equals("A02")){
					strQuery = "SELECT top 100 tP.numPagoAMF, tP.nClaveAMF AS FacturaAcuerdosAdministracion, tP.numFolioAMF, tP.referenciaAMF, tP.rfcAMF, tP.cuentaBancaria, " 
								+ " CONVERT(VARCHAR,tP.fechaCaptura,103) AS fechaCaptura, CONVERT(VARCHAR,tP.fechaPago,103) AS fechaPago, '$'+CONVERT(VARCHAR,(CONVERT(money,tP.importePago)),1) AS importePago, "
								+ " '$'+CONVERT(VARCHAR,(CONVERT(money,tA.mSaldo))) as mSaldo, CONVERT(VARCHAR,tAE.fVigencia,103) AS fVigencia,estatus, nEnviadoSICOP "
								+ " FROM tPagoAMF AS tP, tAcuerdosMFEncabezado tAE,tAcuerdosMFDetalle AS tA "
								+ " WHERE tAE.nClaveAMF = tA.nClaveAMF AND tAE.cFolio = tA.cFolio AND tP.nClaveAMF = tA.nClaveAMF AND tP.numFolioAMF = tA.cFolio AND tP.cUnidadResponsable = tA.cUnidadResponsable ";
				}else{
					strQuery = "SELECT top 100 tP.numPagoAMF, tP.nClaveAMF AS FacturaAcuerdosAdministracion, tP.numFolioAMF, tP.referenciaAMF, tP.rfcAMF, tP.cuentaBancaria, " 
						+ " CONVERT(VARCHAR,tP.fechaCaptura,103) AS fechaCaptura, CONVERT(VARCHAR,tP.fechaPago,103) AS fechaPago, '$'+CONVERT(VARCHAR,(CONVERT(money,tP.importePago)),1) AS importePago, "
						+ " '$'+CONVERT(VARCHAR,(CONVERT(money,tA.mSaldo))) as mSaldo, CONVERT(VARCHAR,tAE.fVigencia,103) AS fVigencia,estatus, nEnviadoSICOP "
						+ " FROM tPagoAMF AS tP, tAcuerdosMFEncabezado tAE,tAcuerdosMFDetalle AS tA "
						+ " WHERE tAE.nClaveAMF = tA.nClaveAMF AND tAE.cFolio = tA.cFolio AND tP.nClaveAMF = tA.nClaveAMF AND tP.numFolioAMF = tA.cFolio AND tP.cUnidadResponsable = tA.cUnidadResponsable AND tP.cUnidadResponsable = '" +u.getU_UR()+ "'";
		
				}
			}
			if(strTabla.equals("TPAGOAMFCIERRE")){
				strQuery = "SELECT numPagoAMF FROM tPagoAMF WHERE "+campos;
			}
			if(strTabla.equals("CANCELA_TPAGOAMF")){
				strQuery = "SELECT CONVERT(varchar,(CONVERT(money,mSaldo)),1) FROM tAcuerdosMFDetalle WHERE "+campos;
			}
			if(strTabla.equals("VDOCUMENTOSPAGOAMF")){
				strQuery = "SELECT caNoContrarrecibo FROM VDOCUMENTOSPAGOAMF WHERE " +campos+ " AND cDocumentoHaplicado is not null AND nFolioPoliza > 0";
			}
			//OBRA PUBLICA     
			
			if(strTabla.equals("TOBRAPUBLICAAPARTADODETALLE")){
				strQuery = "SELECT EP,nDocRenglon,mImporte,cMes FROM tObraPublicaApartadoDetalle  WITH(NOLOCK) WHERE nFolioApartado = 1";
				}
							
			if(strTabla.equals("TOBRAPUBLICAPRECOMPROMISODETALLE")){
				strQuery = "SELECT EP,nDocRenglon,mImporte,cMes FROM tObraPublicaPrecompromisoDetalle  WITH(NOLOCK) WHERE nFolioPrecompromiso = 1"; 
				System.out.println("mi_query:"+strQuery);
				}
			
			if(strTabla.equals("TOBRAPUBLICACOMPROMISODETALLE")){
				strQuery = "SELECT EP,nDocRenglon,mImporte,cMes FROM tObraPublicaCompromisoDetalle WITH(NOLOCK) WHERE nFolioCompromiso = 1";
				}		
		
			
			if(strTabla.equals("TOBRAPUBLICACOMPROMISOENCABEZADO")){
				strQuery = "SELECT nFolioCompromiso,cIdContrato,fRecepcion,cIdUnidadAdministrativa,cIdTObra,cIdTipRec,mObra,mImporteBruto,nPorcIVAAplicable,mImporteIVA,mTotal,cCarteraProyec,cOLI,cDescripcionContrato,fAplicacion FROM tObraPublicaCompromisoEncabezado  WITH(NOLOCK) WHERE nFolioCompromiso = 1 ";
			}			
			
			if(strTabla.equals("TOBRAPUBLICA_EP")){
				strQuery = "SELECT EP,nDocRenglon FROM tObraPublicaPrecompromisoDetalle  WITH(NOLOCK) WHERE nFolioPrecompromiso = 1";
				}

			if(strTabla.equals("BUSCARTOBRAPUBLICAAPARTADODETALLE")){
				strQuery = "SELECT EP FROM tObraPublicaApartadoDetalle  WITH(NOLOCK) WHERE nFolioFolioOPAHeader = " ;
				}

			if(strTabla.equals("BUSCARTOBRAPUBLICAPRECOMPROMISODETALLE")){
				strQuery = "SELECT EP FROM tObraPublicaPreCompromisoDetalle  WITH(NOLOCK) WHERE nFolioPrecompromiso = 1 " ;
				}

			if(strTabla.equals("BUSCARTOBRAPUBLICACOMPROMISODETALLE")){
				strQuery = "SELECT EP FROM tObraPublicaCompromisoDetalle WITH(NOLOCK)  WHERE nFolioCompromiso = 1 " ;
				}

			//FIN OBRA PUBLICA
			
			if( strTabla.equals("BLOQUEO_CUENTAS")){
				strQuery = 	  "SELECT	nCuenta, "
							+ "		dCuenta, "
							+ "		cBloqueaAbonos, "
							+ "		cBloqueaCargos, "
							+ "		cNivelBloqueo  "
							+ "FROM	tcuentas where nCuenta not like '0%' and not (TipoBalance='P' and TipoCuenta='P') ";
							
							
								
			
			}
			if( strTabla.equals("BLOQUEO_CUENTAS_DESC")){
				strQuery = "SELECT	nCuenta, "
							+ "		dCuenta, "
							+ "		cBloqueaAbonos, "
							+ "		cBloqueaCargos, "
							+ "		cNivelBloqueo "
							+ "FROM	tcuentas "
							+ " where nCuenta not like '0%' and not (TipoBalance='P' and TipoCuenta='P') " + strParam;
			}
			// Pago Ministracion Fondos Detalle
			if(strTabla.equals("TPAGOAMFDETALLE")){
				strQuery = "SELECT numPagoAMF, EP , importePago FROM tPagoAMFDetalle WHERE " +campos;
			}
			
		
			
			if (strTabla.equals("TCONTRATOPLURIANUAL_EP")) 
			{
				strQuery =  "SELECT CICLO,EP,nIMPORTE FROM TCONTRATOPLURIANUAL_EP" +campos; 
		 		System.out.println("Cadena: "+ strQuery );
			}

			if (strTabla.equals("TCONTRATOPLURIANUAL_REPORTE")) 
			{
				strQuery =  " SELECT ciclo AS A,R,UR,F,FN,SF,RG,AI,CprogramaPres AS PGM,pta,TG,FF,EF,cve_ppi"
				         +  " FROM tContratoPlurianual_ep " +campos; 
		 		System.out.println("Cadena: "+ strQuery );
			}
		

			if (strTabla.equals("TCONTRATOPLURIANUALDETALLE")) 
			{ 
			//	strQuery =  " SELECT nConsecutivo,aEjercicioFiscal,aEjercicioFiscal,nMonto,nMontoMinimo,nMontoMaximo,nPeso,nPesoMinimo,nPesoMaximo,nAvance FROM TCONTRATOPLURIANUALDETALLE   " +campos;
				strQuery =  " SELECT aEjercicioFiscal,nMonto,nMontoMinimo,nMontoMaximo,nPeso,nPesoMinimo,nPesoMaximo,nAvance FROM TCONTRATOPLURIANUALDETALLE   " +campos;
		    	System.out.println("Cadena: "+ strQuery );
			}
			
			if (strTabla.equals("CONSULTA_EP_COMSOC_PAGO"))
			{
				strQuery =  
				            "   select cEjercicio, nMes,SUBSTRING(ep,32,5) as partida, '$' + CONVERT(varchar, CONVERT(MONEY,mImporteNeto),1) as Importe,EP, "
				        +   "    nFolioPAGODIVERSO as folio from tpagoDiversoDetalle  WITH(NOLOCK)  "  + campos ;
      	 		System.out.println("Cadena: "+ strQuery );
			}
			
			if (strTabla.equals("CONSULTA_EP_COMSOC_RELACION_GASTOS"))
			{
				strQuery =  
				            "   select cEjercicio, nMes,SUBSTRING(ep,32,5) as partida, '$' +CONVERT(varchar, CONVERT(MONEY,mImporteNeto),1) as Importe,EP, "
				        +   "   nFolioRELACIONGASTOS as folio from tRELACIONGASTOSDetalle WITH(NOLOCK)	" + campos;	
				System.out.println("Cadena: "+ strQuery );
					
				
				
				
			}
			
			if (strTabla.equals("CONSULTA_EP_COMSOC_PAGODIRECTO"))
			{
				strQuery =  "  select cEjercicio, cMes,SUBSTRING(ep,32,5) as partida, '$' +CONVERT(varchar, CONVERT(MONEY,mImporteNeto),1) as Importe,EP, "
				        +   "  nFolioPagoDirecto as folio from tPagoDirectoDetalle WITH(NOLOCK) " + campos;	
				System.out.println("Cadena: "+ strQuery );
					
				
			}
			
			
			
					 	
		 	//Catalogo de usuarios solo consulta
		 	if (strTabla.equals("M_CG_USUARIO")){
		 		strQuery = "SELECT U_LOGIN, U_NOMBRE FROM CG_USUARIO whith (NOLOCK) ";
		 		strOrder = " order by U_LOGIN";
		 	}
			
			if(strTabla.equals("TREGISTRODETALLE")){
				strQuery = "SELECT clavePasivo, EP , importePago FROM tRegistroPasivoDetalle WHERE " +campos;
			}
			
			//Catalogo para el checklist de requsitos del procedimiento cuando es alcance externo
			if (strTabla.equals("M_REQUISITOS_PROCEDIMIENTO")){
		 		strQuery = "SELECT cRequisito, cDescripcion, CASE cRequerido WHEN 'checked=\"checked\"' THEN 1 ELSE 0 END as cRequerido, cIdRequisito FROM mCatalogoRequisitosProcedimiento with (NOLOCK)";
		 		strParam = ""; 
		 		strOrder = "order by cIdRequisito desc";
		 	}
			if(strTabla.equals("ROLESUSUARIO")){
				strQuery = "SELECT ur.R_NOMBRE FROM CG_USUARIO_ROLE ur WHERE ur.U_LOGIN='"+strUsuario+"'";
			}
			
			if(strTabla.equals("ID_CENTRO_CONTABLE")){
				strQuery = 
   						"SELECT	* "
						+ "  FROM  ( "
						+ "		SELECT	cCentroContable AS IDCCENTROCONTABLE, "
						+ "				cDescripcion AS idCDescripcion"
						+ "		  FROM	tCatalogoCentroContable t WITH(NOLOCK) "
						+ "		 WHERE	cCentroContable <> '0' "
						+ "		) A WHERE 1=1 " + ( campos != null?  campos: "");
			}
			
			if(strTabla.equals("COMPROMISO_O_PUB_DETAIL")){
				strQuery = 
						"SELECT cmes, " 
						+ "       ep,  "
						+ "       mimporte " 
						+ "FROM   (SELECT nFolioOPComHeader, " 
						+ "               cmes,  "
						+ "               ep,  "
						+ "               '$' + CONVERT(VARCHAR, CONVERT(money,mimporte), 1) AS mimporte " 
						+ "        FROM   tObraPublicaCompromisoDetalle  WITH(NOLOCK) "  
						+ "        WHERE  cevento not in('OP_LP','ROP_LP')  ) AS a " 
						+ "WHERE  1 = 1  "
						+ ( campos != null?  campos: "");
			}
			
			if(strTabla.equals("RESULTADO_CONSULTA_OP_C_DETAIL")){
				strQuery = 
						"SELECT cmes, " 
						+ "       ep,  "
						+ "       mimporte " 
						+ "FROM   (SELECT nFolioOPComHeader, " 
						+ "               cmes,  "
						+ "               ep,  "
						+ "               '$' + CONVERT(VARCHAR, CONVERT(money,mimporte), 1) AS mimporte " 
						+ "        FROM   tObraPublicaCompromisoDetalle  WITH(NOLOCK) "
						+ "        WHERE cEvento not in('OP_LP','ROP_LP') "
						+ "       ) AS a " 
						+ "WHERE  1 = 1  "
						+ ( campos != null?  campos: "");
			}
			if(strTabla.equals("RESULTADO_CONSULTA_OP_A_DETAIL")){
				strQuery = 
						"SELECT cmes, " 
						+ "       ep,  "
						+ "       mimporte " 
						+ "FROM   (SELECT nFolioOPAHeader, " 
						+ "               cmes,  "
						+ "               ep,  "
						+ "               '$' + CONVERT(VARCHAR, CONVERT(money,mimporte), 1) AS mimporte " 
						+ "        FROM   tObraPublicaApartadoDetalle WITH(NOLOCK) ) AS a " 
						+ "WHERE  1 = 1  "
						+ ( campos != null?  campos: "");
			}
			if(strTabla.equals("RESULTADO_CONSULTA_OP_P_DETAIL")){
				strQuery = 
						"SELECT cmes, " 
						+ "       ep,  "
						+ "       mimporte " 
						+ "FROM   (SELECT nFolioOPPreComHeader, " 
						+ "               cmes,  "
						+ "               ep,  "
						+ "               '$' + CONVERT(VARCHAR, CONVERT(money,mimporte), 1) AS mimporte " 
						+ "        FROM   tObraPublicaPreCompromisoDetalle  WITH(NOLOCK) "
						+ "        WHERE  cEvento not in('OP_LA','ROP_LA')"
						+ "       ) AS a " 
						+ "WHERE  1 = 1  "
						+ ( campos != null?  campos: "");
			}

		if(strTabla.equals("READ_OBRA_PUBLICA_DETALLE")){
			strQuery = " exec.dbo.MontosApartadoObraPublicaEP "+ campos;
				System.out.println("strQuery"+strQuery);
		}

		if(strTabla.equals("SELECT_OBRA_PUBLICA_DETALLE")){
			strQuery = " exec.dbo.SelectObraPublicaDetail "+ campos;
				System.out.println("strQuery"+strQuery);
		}
		if(strTabla.equals("CONTRACTS_PROVEEDOR")){
			strQuery = " exec.dbo.sp_contratosProveedor "+ campos;
				System.out.println("strQuery"+strQuery);
		}
		if(strTabla.equals("CONTRACTS_ART25")){
			strQuery = " exec.dbo.sp_ContratosArt25 "+ campos;
				System.out.println("strQuery"+strQuery);
		}
		if(strTabla.equals( "BUSCA_CONTRATO_OBRA_PUBLICA_CONV_MODIF" ) ){
			strQuery =   "SELECT A.* "
						+"FROM   (SELECT ccvecontrato, "
						+"               cbeneficiario, "
						+"               '$' + CONVERT(VARCHAR, CONVERT(MONEY,nmontoconiva), 1) AS nMontoConIVA, "
						+"               foliosai, cSuspensionPagos, cCentroContable "
						+"        FROM   tobrapublicacompromisoencabezado WITH(NOLOCK)"
						+"        WHERE  cdocumentohaplicado = 'S' "
						+"               AND ( nfoliopolizacancelacion = 0 OR nfoliopolizacancelacion = '' OR nfoliopolizacancelacion IS NULL )) AS A "
						+"WHERE  1 = 1  ";
						
		}
		if(strTabla.equals( "BUSCA_CONTRATO_PAGO_PASIVO" ) ){//IRD 20131121	RO-0009
			strQuery =   "SELECT A.* FROM (SELECT ccvecontrato, cbeneficiario, '$' + CONVERT(VARCHAR, CONVERT(MONEY,nmontoconiva+mconveniomodif), 1) AS nMontoConIVA,  "
						+" foliosai, '$' + CONVERT(VARCHAR, CONVERT(MONEY,nmontoconiva + mconveniomodif - mestimacion), 1) mSaldo, cU_CC cCentroContable, cU_UR, cU_CC  "
						+"  FROM   tObraPublicaAniosAnteriores WITH(NOLOCK)  where mestimacion != 0 and nmontoconiva + mconveniomodif - mestimacion > 1 "
						+"  ) AS A WHERE  1 = 1 "
						+ " and ccvecontrato+'-PAS' not in (select ccvecontrato from tobrapublicaapartadoencabezado where cDocumentoHaplicado != 'C') "
						+ " and ccvecontrato+'-PAS' not in (select ccvecontrato from tobrapublicaprecompromisoencabezado where cDocumentoHaplicado != 'C') "
						+ " and ccvecontrato+'-PAS' not in (select ccvecontrato from tobrapublicacompromisoencabezado where cDocumentoHaplicado != 'C') "
						;
						
		}
		if(strTabla.equals( "CONTRATO_PLURIANUAL_OP" ) ){//MLR 20131201	RO-0010
			strQuery =   "SELECT A.* FROM (SELECT ccvecontrato, cbeneficiario, '$' + CONVERT(VARCHAR, CONVERT(MONEY,nmontoconiva), 1) AS nMontoConIVA,  "
						+" foliosai, '$' + CONVERT(VARCHAR, CONVERT(MONEY,nmontoconiva + mconveniomodif - mestimacion), 1) mSaldo, cU_CC cCentroContable, esplurianual, cU_UR, cU_CC  "
						+"  FROM   tObraPublicaAniosAnteriores WITH(NOLOCK) "
						+"  ) AS A WHERE  1 = 1 "
						+ " and ccvecontrato not in (select ccvecontrato from tobrapublicaapartadoencabezado where cDocumentoHaplicado != 'C') "
						+ " and ccvecontrato not in (select ccvecontrato from tobrapublicaprecompromisoencabezado where cDocumentoHaplicado != 'C') "
						+ " and ccvecontrato not in (select ccvecontrato from tobrapublicacompromisoencabezado where cDocumentoHaplicado != 'C') "
						+ " and esplurianual = '1'"
						;
						
		}
		if(strTabla.equals( "VOBRAPUBLICAPENDIENTES" ) ){
			strQuery =   "SELECT a.[cveContrato], a.[beneficiario], sum(a.[nMontoconIVA]) nMontoconIVA, a.[folioSAI], a.[momento] "
			
						+" FROM   fn_obraPublicaPendientes() AS A "
						+" WHERE  1 = 1  ";
						
		}
		if(strTabla.equals( "COMPROMISO_O_PUB_CONV_MOFI_DETAIL" ) ){
			strQuery =   
			"SELECT nmes, " 
			+"       ep,  "
			+"       '$' + CONVERT(VARCHAR, CONVERT(MONEY,mimporte), 1 ) AS monto " 
			+"FROM   tobrapublicaconvmodifdetalle   WITH(NOLOCK) "
			+"WHERE  1 = 1  "
			+ ( campos != null?  campos: "");						
		}
		if(strTabla.equals( "COMPROMISO_O_PUB_PAGO_PASIVO_DETAIL" ) ){//IRD 20131121	RO-0009
			strQuery =   
			"SELECT nmes, " 
			+"       ep,  "
			+"       '$' + CONVERT(VARCHAR, CONVERT(MONEY,mimporte), 1 ) AS monto " 
			+"FROM   tobrapublicapagopasivodetalle   WITH(NOLOCK) "
			+"WHERE  1 = 1  "
			+ ( campos != null?  campos: "");						
		}
		if(strTabla.equals( "COMPROMISO_O_PUB_PLURIANUAL_DETAIL" ) ){//MLR 20131206	    RO-0010
			strQuery =   
			"SELECT cCveContrato, " 
			+"       nAContrato,  "
			+"       '$' + CONVERT(VARCHAR, CONVERT(MONEY,mMontoContrato), 1 ) AS mMontoContrato " 
			+"FROM   tContratoMultiAnual   WITH(NOLOCK) "
			+"WHERE  1 = 1  "
			+ ( campos != null?  campos: "");						
		}


		if( strTabla.equals("OBRA_PUBLICA_CNT_MA")){
			strQuery = "SELECT nacontrato, "
					 + "       mmontocontrato "
					 + "FROM   tcontratomultianual "
					 + ( campos != null?  campos: "");
		}
		if(strTabla.equals( "COMPROMISO_O_PUB_RETENCIONES_DETAIL" ) ){
			strQuery =  " select a.cIdTipoRetencion, b.cTipoRetencion " 
						+ " from tObraPublicaCompromisoRetencion a  WITH(NOLOCK) "
						+ " join pCatalogoTipoRetencion b  WITH(NOLOCK) ON ( "
						+ " a.cIdTipoRetencion = b.cIdTipoRetencion "
						+ " )"
						+ ( campos != null?  campos: "");
		}
		if (strTabla.equals("CAT_UR")){
			strQuery = 
				  "SELECT idunidadresponsable, "
				+ "       opUnidadNormativa "
				+ "FROM   (SELECT cunidadresponsable + ' ' + d_descripcion AS opUnidadNormativa, " 
				+ "               cunidadresponsable                       AS idunidadresponsable "
				+ "        FROM   tcatunidadresponsable WITH (nolock)) a ";
		}

		if (strTabla.equals("M_CG_GRUPO_PROPIEDADES")){
			strQuery = "SELECT G_NOMBRE,GP_NOMBRE,gp_descripcion,gp_valor_permitido,GP_VALOR "
				+ "FROM CG_GRUPO_PROPIEDADES " ;
		}
		
		if (strTabla.equals("M_TCATALOGOESTPROGAUT")){
			strQuery = 
				  "SELECT aEjercicioFiscal,cRamo,nConsecutivo,cUnidadResponsable,cGrupoFuncional,cFuncion "
				+ ",cSubFuncion,cProgramaGeneral,cActividadInstitucional,cProgramaPresupuestario,cModalidad,cPrograma "
				+ "FROM tCatalogoEstProgAut " ;
		}
		
		if (strTabla.equals("M_TCATALOGORAMOOGTO")){
			strQuery = 
				  "select nConsecutivo,cRamo,cFuenteFinanciamiento,cPartida,cTipoGasto,aEjercicioFiscal "
 				+ "from tCatalogoRamoOGTO" ;
		}
		if(strTabla.equals("BUSCAR_CONTRATOS_OP")){
			strQuery =   "SELECT foliosai, "
						+"       CASE "
						+"         WHEN nocontrato IS NULL "
						+"               OR nocontrato = '' THEN '' "
						+"         ELSE nocontrato "
						+"       END AS noContrato, "
						+"       CASE "
						+"         WHEN beneficiariocompromiso IS NULL "
						+"               OR beneficiariocompromiso = '' THEN '' "
						+"         ELSE beneficiariocompromiso "
						+"       END AS beneficiariocompromiso, "
						+"       CASE "
						+"         WHEN montototalobra IS NULL THEN '' "
						+"         ELSE '$' + CONVERT( VARCHAR, montototalobra) "
						+"       END AS montoTotalObra, "
						+"       estatus_contrato, "
						+"       CASE "
						+"         WHEN desctipoobra IS NULL THEN '' "
						+"         ELSE desctipoobra "
						+"       END AS descTipoObra, "
						+"       CASE "
						+"         WHEN desctiporecurso IS NULL THEN '' "
						+"         ELSE desctiporecurso "
						+"       END AS descTipoRecurso, "
						+"       CASE "
						+"         WHEN oli IS NULL "
						+"               OR oli = '-1' THEN '' "
						+"         ELSE oli "
						+"       END AS oli, "
						+"       CASE "
						+"         WHEN cartera IS NULL "
						+"               OR cartera = '-1' THEN '' "
						+"         ELSE cartera "
						+"       END AS cartera "
						+"FROM   vinformacion_contrato_obra_publica  WITH(nolock) "
						+ ( campos != null?  campos: "");
		}
		if (strTabla.equals("M_TCATALOGOPARTIDAVALIDAPP")){
			strQuery = 
				  "select top(1000) nConsecutivo,cRamo,cPartida,cModalidad,cPrograma,cProgramaPresupuestario,aEjercicioFiscal "
 				+ "from tCatalogoPartidaValidaPP" ;
		}

		if (strTabla.equals("M_TCATALOGONUMERAL")){
			strQuery = "select iID, cNumeralDescripcion from tCatalogoNumeral " ;
		}
		if(strTabla.equals( "BUCAR_CONTRATOS_OP_PLURIANUAL" )){
			strQuery =    "SELECT a.nocontrato, "
						+ "       a.nacontrato,  "
						+ "       CONVERT(VARCHAR, CONVERT(MONEY, a.mmontocontrato, 1)) AS mmontocontrato  "
						+ "FROM   (SELECT c.ccvecontrato     AS noContrato, " 
						+ "               c.aejerciciofiscal AS nAContrato, " 
						+ "               c.nmonto           AS mMontoContrato  "
						+ "        FROM   tobrapublicacompromisoencabezado c WITH(nolock) " 
						+ "        UNION ALL  "
						+ "        SELECT ccvecontrato AS noContrato,  "
						+ "               nacontrato,  "
						+ "               mmontocontrato " 
						+ "        FROM   tcontratomultianual WITH(nolock))AS a  "
						+ ( campos != null?  campos: "");
						strOrder =     "ORDER  BY nocontrato,  "
						+ "          nacontrato  ";
		}
		if(strTabla.equals( "CONV_MOD_CONSULTA" )){
			strQuery =  "SELECT A.cnoconvenio, "
						+"       CASE "
						+"         WHEN a.ffechainicontr = '01/01/1900' THEN '' "
						+"         ELSE a.ffechainicontr "
						+"       END                AS ffechainicontr, "
						+"       CASE "
						+"         WHEN a.ffechafincontr = '01/01/1900' THEN '' "
						+"         ELSE a.ffechafincontr "
						+"       END                AS ffechafincontr, "
						+"       '$' + mmontoconiva AS mmontoconiva "
						+"FROM   (SELECT cnoconvenio, "
						+"               ccvecontrato, "
						+"               CONVERT(VARCHAR, ffechainicontr, 103)             AS "
						+"               ffechainicontr, "
						+"               CONVERT(VARCHAR, ffechafincontr, 103)             AS "
						+"               ffechafincontr, "
						+"               CONVERT(VARCHAR, CONVERT(MONEY, mmontoconiva, 1)) AS mmontoconiva "
						+"        FROM   tobrapublicaconvmodifencabezado WITH(NOLOCK)) AS A "
						+ ( campos != null?  campos: "");
		}
		if( strTabla.equals("PAGOS_CONSULTA_OP") ){
			strQuery = 
						 "SELECT a.cnoestimacion                                                       AS "
						+"       estimacion, "
						+"       '$' "
						+"       + CONVERT( VARCHAR, CONVERT( MONEY, Round( a.mimportemasiva, 2) ), 1) AS "
						+"       mImporteMasIva,"
						+"       IsNull(b.mobra5,0) AS retencion5pc, "
						+"       IsNull(b.mcnic,0) + IsNull(b.mcnic,0) retencion2pc, "
						+"       CONVERT( VARCHAR, CONVERT( DATE, a.faplicacion ), 103)                AS "
						+"       fechaAutorizacionPago, "
						+"       a.nporcamortizacion, "
						+"       a.mamortizacion, "
						+"       a.cfoliocontratoobra "
						+"FROM   tpagoobraencabezado a WITH(nolock) "
						+"       LEFT OUTER JOIN (SELECT nfoliopagoobra, "
						+"                               Sum(mobra5) AS mObra5, "
						+"                               Sum(mcnic)  AS mCNIC, "
						+"                               Sum(mimdt)  AS mIMDT "
						+"                        FROM   tpagoobradetalle WITH(nolock) "
						+"                        WHERE  cevento <> 'ANTICIPO' "
						+"                        GROUP  BY nfoliopagoobra) b "
						+"                    ON a.nfoliopagoobra = b.nfoliopagoobra "
						+"WHERE  a.cdocumentohaplicado = 'S' "
						+ ( campos != null?  campos: "");
			strOrder = " ORDER BY estimacion ";       
		}
		if (strTabla.equals("PARTIDASDOCUMENTO"))
		{
			strQuery =  "SELECT * FROM v_PartidasPedidoContrato WHERE "+strParam;
		}
		
		if (strTabla.equals("UNIDADMEDIDA"))
		{
			strQuery =  "SELECT cidUnidadMedida, cUnidadMedida FROM mcatalogoUnidadMedida";
		}
		
		
		if (strTabla.equals("LINEASPARTIDASPEDIDO"))
		{
			strQuery =  "SELECT * FROM v_lineasPartidaPedido WHERE "+strParam;
		}
		
		if (strTabla.equals("LINEASPARTIDASCONTRATO"))
		{
			strQuery =  "SELECT * FROM v_lineasPartidaContrato WHERE "+strParam;
		}
		
		if (strTabla.equals("CALENDARIZACIONAPARTADO"))
		{
			//strQuery =  "select EP, case when [1] is null then 0.00 else [1] end as Enero,case when [2] is null then 0.00 else [2] end as Febrero,case when [3] is null then 0.00 else [3] end as Marzo,case when [4] is null then 0.00 else [4] end as Abril,case when [5] is null then 0.00 else [5] end as Mayo,case when [6] is null then 0.00 else [6] end as Junio,case when [7] is null then 0.00 else [7] end as Julio,case when [8] is null then 0.00 else [8] end as Agosto,case when [9] is null then 0.00 else [9] end as Septiembre,case when [10] is null then 0.00 else [10] end as Octubre,case when [11] is null then 0.00 else [11] end as Noviembre,case when [12] is null then 0.00 else [12] end as Diciembre from (select ad.EP, ad.cMes, ad.mImporte from tApartadoEncabezado ae with(nolock) inner join tApartadoDetalle ad with(nolock) on ae.nFolioApartado=ad.nFolioApartado where ae.cIdSolicitud='"+strParam+"' and ae.cDocumentoHaplicado<>'C') as tabEP pivot (sum(mImporte) for cMes in ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12])) as pvt ";
			strQuery = "select tab.EP, tab.Enero-tab2.Enero as Enero, tab.Febrero-tab2.Febrero as Febrero, tab.Marzo-tab2.Marzo as Marzo, tab.Abril-tab2.Abril as Abril, "+ 
						"tab.Mayo-tab2.Mayo as Mayo, tab.Junio-tab2.Junio as Junio, tab.Julio-tab2.Julio as Julio, tab.Agosto-tab2.Agosto as Agosto, tab.Septiembre-tab2.Septiembre as Septiembre, "+ 
						"tab.Octubre-tab2.Octubre as Octubre, tab.Noviembre-tab2.Noviembre as Noviembre, tab.Diciembre-tab2.Diciembre as Diciembre from ("+
						"select EP, case when [1] is null then 0.00 else [1] end as Enero,case when [2] is null then 0.00 else [2] end as Febrero,case when [3] is null then 0.00 else [3] end as Marzo,case when [4] is null then 0.00 else [4] end as Abril,case when [5] is null then 0.00 else [5] end as Mayo,case when [6] is null then 0.00 else [6] end as Junio,case when [7] is null then 0.00 else [7] end as Julio,case when [8] is null then 0.00 else [8] end as Agosto,case when [9] is null then 0.00 else [9] end as Septiembre,case when [10] is null then 0.00 else [10] end as Octubre,case when [11] is null then 0.00 else [11] end as Noviembre,case when [12] is null then 0.00 else [12] end as Diciembre from (select ad.EP, ad.cMes, ad.mImporte from tApartadoEncabezado ae with(nolock) inner join tApartadoDetalle ad with(nolock) on ae.nFolioApartado=ad.nFolioApartado where ae.cIdSolicitud='"+strParam+"' and ae.cDocumentoHaplicado<>'C') as tabEP pivot (sum(mImporte) for cMes in ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12])) as pvt "+ 
						") as tab left join (select nIdClaveEgresos+'.'+ClaveInterna as EP,mes01 as Enero, mes02 as Febrero, mes03 as Marzo, mes04 as Abril, "+
						"mes05 as Mayo, mes06 as Junio, mes07 as Julio, mes08 as Agosto, mes09 as Septiembre, mes10 as Octubre, mes11 as Noviembre, mes12 as Diciembre "+
						"from mSolicitudLineasApartado with(nolock) where cIdSolicitud='"+strParam+"') as tab2 "+ 
						"on tab.EP=tab2.EP ";
			System.out.println(strQuery);
			strParam = "";
		}
		
		if( strTabla.equals("CONSULTA_TRAZA_COMPLETA") ){
			strQuery = "select FOLIO, ID_TRAMITE, DESC_TRAMITE, RESPONSABLE,FECHA_CHAR, FECHA, OPERACION, ID_OPER from vimx_seguimiento_tramite with (nolock) where 1 = 1   AND FOLIO = '" + strParam  + "' " + 
						" union  " + 
						"  select B_C_FOLIO, B_ID_TC, tc.TC_DESCRIPCION, B_CO_RESPONSABLE_SIGTE " + 
						" , CONVERT(VARCHAR, B_CO_FECHA_INI, 103) AS FECHA_CHAR  ,B_CO_FECHA_INI as fecha, " +  
						" o.O_DESCRIPCION AS OPERACION, b_ID_OPER " + 
						" from cg_bitacora  b with (nolock)  , CG_TIPO_CASO tc with (nolock), CG_OPERACION o with (nolock) " + 
						" where b.B_ID_TC = tc.ID_TC " + 
						" and o.ID_TC = tc.ID_TC " +
						" and b.B_ID_OPER = o.ID_OPER " +
					 	" and B_C_FOLIO = '" + strParam + "'" + 
					 	" order by FECHA , ID_OPER ";
						
			System.out.println(strQuery);
			strParam = "";
		}	
		
		if(strTabla.equals("PLANTILLAREPORTE")){
			strQuery = " select nIdConsecutivo, cIdCampo, nIdModulo, cDescripcion from mReportePredeterminadoDetalle where "+strParam+" order by nOrden asc";//order by nIdModulo asc
			strParam = "";
		}	
		
		
		//Catalogo para el Grupo de Eventos
		if (strTabla.equals("M_TGRUPOEVENTO")){
	 		strQuery = "select nIdGrupoEvento,	cNombreGrupo  from TGRUPOEVENTO with (NOLOCK)";
	 	}

		//Catalogo para el Grupo de Eventos
		if (strTabla.equals("CG_CAT_TGRUPOEVENTO")){
	 		strQuery = "select nIdGrupoEvento,	cNombreGrupo  from TGRUPOEVENTO with (NOLOCK)";
	 	}

		//Catalogo para el SubGrupo de Eventos
		if (strTabla.equals("M_TSUBGRUPOEVENTO")){
	 		strQuery = "select nIdSubGrupoEvento,	nIdGrupoEvento,	cNombreSubGrupo from TSUBGRUPOEVENTO with (NOLOCK) ";
	 	}

		if (strTabla.equals("CG_CAT_TSUBGRUPOEVENTO")){
	 		strQuery = "select nIdSubGrupoEvento,	nIdGrupoEvento,	cNombreSubGrupo from TSUBGRUPOEVENTO with (NOLOCK) ";
	 	}

		//Catalogo para el Evento Manual
		if (strTabla.equals("M_TEVENTOMANUAL")){
	 		strQuery = "select cIdGrupoEvento,	cIdSubGrupoEvento,	cIdEventoManual,	cPartida,	nCuenta,	cEvento,	nDocRenglon,	aEjercicioFiscal from tEventoManual with (NOLOCK) ";
	 	}

		//Catalogo para el Evento Relacion
		if (strTabla.equals("M_TEVENTORELACION")){
	 		strQuery = "select nIdGrupoEvento, nIdSubGrupoEvento, cEvento, dEvento, cUR from tEventoRelacion with (NOLOCK) ";
	 	}

		//Catalogo para el Evento Relacion
		if (strTabla.equals("CG_CAT_TEVENTORELACION")){
	 		strQuery = "select cEvento,	nIdGrupoEvento,	nIdSubGrupoEvento,	dEvento from tEventoRelacion with (NOLOCK) ";
	 	}

		//Catalogo para el Evento 
		if (strTabla.equals("M_TEVENTO")){
	 		strQuery = "select cEvento,	dEvento from tEvento with (NOLOCK) ";
	 	}

		//Catalogo para el Evento 
		if (strTabla.equals("CG_CAT_TEVENTO")){
	 		strQuery = "select cEvento,	dEvento from tEvento with (NOLOCK) ";
	 	}

		//Catalogo para el Evento Concepto
		if (strTabla.equals("M_TEVENTOCONCEPTO")){
	 		strQuery = "select cTCONC,	cOBGINI,	cCTGA,	cTUNR,	cEVTO,	cDevCTA,	cDevCTA_A,	cDevCTA_RET,	cEjeCTA,	cEjeCTA_A,	cEjeCTA_RET,	ID_DESTINO_GASTO from tEventoConcepto with (NOLOCK) ";
	 	}

		//Catalogo para el Evento Configuracion
		if (strTabla.equals("M_TEVENTOCONFIGURACION")){
	 		strQuery = "select cEvento,	nCuenta,	aEjercicioFiscal,	nDocRenglon,	TipoCuenta,	dComponente from tEventoConfiguracion with (NOLOCK) ";
	 	}

		//Catalogo para el Evento Configuracion Detalle
		if (strTabla.equals("M_TEVENTOCONFIGURADETALLE")){
	 		strQuery = "select cEvento,	nCuenta,	aEjercicioFiscal,	nDocRenglon,	nOrden,	dDetalleCuenta from tEventoConfiguraDetalle with (NOLOCK) ";
	 	}
	 	
		if (strTabla.equals("M_TPAQUETESCOMISION")){
		 		strQuery = "SELECT nIdPaquete, cNombre, substring(cDescripcion,1,180), nPorcentaje, nActivo  FROM tCatPaquetesComision (NOLOCK) ";
		 		
		}
	 	 	//Lectura de componentes - Desembolsos
		if (strTabla.equals("DCOMPONENTES")){
	 		strQuery = "SELECT id_componentetecnico,componentetecnico FROM dComponente_Tecnico  WITH(NOLOCK) ";
	 	}
	 			//Lectura de subcomponentes - Desembolsos
		if (strTabla.equals("DSUBCOMPONENTES")){
	 		strQuery = "SELECT id_componentetecnico,id_subcomponentetecnico,subcomponentetecnico FROM dSubcomponente_Tecnico  WITH(NOLOCK) ";
	 	}	 	
	 			//Lectura de categorias - Desembolsos
		if (strTabla.equals("DCATEGORIAS")){
	 		strQuery = "SELECT id_componentetecnico,NumeroCategoria,NombreCategoria,Monto,PorcentajeFinanciamiento,id_SubComponenteTecnico,id_categoriaInversionPrestamo FROM dCategoriaInversion_prestamo  WITH(NOLOCK) ";
	 	}
	 			//Lectura de Número prestamo - Desembolsos
		if (strTabla.equals("DNUMEROPRESTAMO")){
	 		strQuery = "SELECT NumeroPrestamo FROM dPrestamo  WITH(NOLOCK) ";
	 	}	
	 			//Lectura de nombre prestamo - Desembolsos
		if (strTabla.equals("DNOMBREPRESTAMO")){
	 		strQuery = "SELECT NombrePrestamo FROM dPrestamo  WITH(NOLOCK) ";
	 	} 	
	 			//Lectura de prestamo programa presupuestario - Desembolsos
		if (strTabla.equals("DPRESTAMOPROGRAMAPRESUPUESTARIO")){
	 		strQuery = "SELECT id_prestamo, cProgramaPresupuestario FROM dPrestamoProgramaPresupuestario  WITH(NOLOCK) ";
	 	} 	 	
	 			//Lectura de id_Contrato de contratos por id_prestamo - Desembolsos
		if (strTabla.equals("DNOCONTRATO")){
	 		strQuery = "SELECT id_Contrato FROM dContrato  WITH(NOLOCK) ";
	 	} 
	 			//Lectura de Beneficiarios de contratos por id_contrato - Desembolsos
		if (strTabla.equals("DRFCCONTRATO")){
	 		strQuery = "SELECT RFCBeneficiario + ' - ' + NombreBeneficiario AS Benef FROM dContrato WITH(NOLOCK)  ";
	 	} 	 	
	 			//Lectura de id_Contrato de contratos  - Desembolsos
		if (strTabla.equals("DNOCONVENIO")){
	 		strQuery = "SELECT id_convenioModifica FROM dConvenio_Modificatorio  WITH(NOLOCK) ";
	 	} 
	 			//Lectura de id_Factura de facturas id_Factura - Desembolsos
		if (strTabla.equals("DNOFACTURA")){
	 		strQuery = "SELECT id_Factura FROM dFactura  WITH(NOLOCK) ";
	 	} 
			//Lectura de SOE  - Desembolsos
		if (strTabla.equals("DSOE")){
				strQuery = "SELECT * FROM dSOE  WITH(NOLOCK) ";
			} 
			//Lectura de SOE por Transferencia - Desembolsos
		if (strTabla.equals("DSOET")){
		strQuery = "SELECT * FROM dSOETransfer  WITH(NOLOCK) ";
		} 
	 			//Lectura de numero_soe de SOE  - Desembolsos
		if (strTabla.equals("DSOEN")){
	 		strQuery = "SELECT numero_soe FROM dSOE  WITH(NOLOCK) ";
	 	} 
	 			//Lectura de posibles soes  - Desembolsos
		if (strTabla.equals("DSOEDISP")){
	 		strQuery = "SELECT IdPrestamo,IdContrato,IdFactura,NoContrato,NombreBeneficiario,NumFactura,ImporteFac,Componente,Categoria FROM vDatosSOEBuscar  WITH(NOLOCK) ";
	 	} 	
	 			//Lectura de posibles soes x transferencia  - Desembolsos
		if (strTabla.equals("DSOEDISPTRANSFER")){
	 		strQuery = "SELECT NumeroContrato, NumeroFactura, Beneficiario,ImporteFactura,ProgramaPresupuestario,Documento,Folio,CuentaXPagar,RFC,convert(Date,fAplicacion)fAplicacion,cUnidadResponsable,Entidad,Cartera FROM vSOETransPagos  WITH(NOLOCK) WHERE 1=1  and NumeroFactura not in(select factura from dSOETransfer with(nolock)) ";
	 	} 	 	
	 			//Lectura de  soes  - Desembolsos
		if (strTabla.equals("DSOEEXISTE")){
	 		strQuery = "SELECT IdPrestamo,IdContrato,IdFactura,NoContrato,NombreBeneficiario,NumFactura,ImporteFac,Componente,Categoria,FechaMov, Nombre FROM vDatosSOE  WITH(NOLOCK) ";
	 	}	 			
	 			//Lectura de  SOEs por Transferencia  - Desembolsos
		if (strTabla.equalsIgnoreCase("DSOETransferEXISTE")){
			
	 		strQuery = "select id_prestamo,'' sel,contrato,factura,Beneficiario,ImporteFactura ,ProgramaPresupuestario,documento,folio,CuentaXPagar,RFC,fAplicacion,cUnidadResponsable,cEntidadFed,cartera from dSOETransfer WITH(NOLOCK) ";
            
	 	} 	
	 			//Lectura numero  soes  - Desembolsos
		if (strTabla.equals("DNUMSOE")){
	 		strQuery = "SELECT RIGHT(REPLICATE('0', 4) + CAST(COUNT(*)+1 AS VARCHAR(2)), 4) FROM dSOE WITH(NOLOCK), dPrestamo WITH(NOLOCK) WHERE dSOE.id_prestamo = dPrestamo.id_prestamo   ";
	 	}
		if (strTabla.equals("DNUMSOET")){
	 		strQuery = "SELECT RIGHT(REPLICATE('0', 4) + CAST(COUNT(*)+1 AS VARCHAR(2)), 4) FROM dSOETransfer WITH(NOLOCK), dPrestamo WITH(NOLOCK) WHERE dSOETransfer.id_prestamo = dPrestamo.id_prestamo   ";
	 	} 	 	
	 		 			//Lectura numero  soes  - Desembolsos
		if (strTabla.equals("DNUMPRESTAMO")){
	 		strQuery = "SELECT NumeroPrestamo FROM dPrestamo WITH(NOLOCK)  ";
	 	} 
	 		 			//Trae el importe total del contrato y la suma de sus facturas - Desembolsos
		if (strTabla.equals("IMPORTETOTALCONTRATOSUMFACTURA")){
	 		strQuery = "SELECT ImporteTotal, ";
	 		strQuery = strQuery + "	(SELECT ISNULL(SUM( ImporteFederal) + SUM(ImporteEstatal) + SUM(ImporteMunicipal) + SUM(ImporteOtros),0) ";
	 		strQuery = strQuery + "		FROM dFactura  WITH(NOLOCK) WHERE dFactura.id_Contrato=dContrato.id_Contrato AND dFactura.id_prestamo=dContrato.id_prestamo) AS SumFacturas ";
	 		strQuery = strQuery + "FROM dContrato  WITH(NOLOCK)  WHERE 0=0 ";
	 	} 

 		 	
	 	if(strTabla.equals("VSALDOS_Y_CONTRATOSPLURIANAUALES_EP"))
	 	{
				  strQuery ="SELECT 	vSaldos.aEjercicioFiscal as ciclo, "
       					   +"vSaldos.EP, "
      					   +"sum(vSaldos.montoenero)      AS Enero, "
      					   +"sum(vSaldos.montofebrero)    AS Febrero, "
                           +"sum(vSaldos.montomarzo)      AS Marzo, "
     					   +"sum(vSaldos.montoabril)      AS Abril, "
    					   +"sum(vSaldos.montomayo)       AS Mayo, 	"
    					   +"sum(vSaldos.montojunio)      AS Junio,	" 
      					   +"sum(vSaldos.montojulio)      AS Jilio, "
     					   +"sum(vSaldos.montoagosto)     AS Agosto, "
     					   +"sum(vSaldos.montoseptiembre) AS Septiembre, "
     					   +"sum(vSaldos.montooctubre)    AS Octubre, " 
      					   +"sum(vSaldos.montonoviembre)  AS Noviembre, " 
      					   +"sum(vSaldos.montodiciembre)  AS Diciembre, " 
      					   +"sum(vSaldos.montoanual)      AS ImporteAnual_Disponible, "
      					   +"(select nImporte  "
                           +"from  tContratoPlurianual_EP  CpEp	 WITH(NOLOCK) "	  
        				   +"where  CpEp.CICLO = Vsaldos.aEjercicioFiscal " + strParam  
        				   +"and CpEp.ep = vSaldos.EP) as ImporteObtenido " 
						   +"FROM   vsaldosanuales vSaldos WITH(NOLOCK) INNER JOIN tContratoPlurianual_EP CpEp  WITH(NOLOCK)"
						   +"ON (vSaldos.Ep = CpEp.ep and CpEp.CICLO = Vsaldos.aEjercicioFiscal  " + strParam + ")"
						   +"group by vSaldos.aEjercicioFiscal, vSaldos.EP "
						   +" union "
						   +" select "
						   +" ciclo , EP, 0  AS Enero, 0   AS Febrero, 0     AS Marzo, 0  AS Abril, "
     					   +" 0 aS Mayo, 0     AS Junio, 0     AS Jilio, 0    AS Agosto, 0 AS Septiembre, 0  AS Octubre, 0  AS Noviembre, 0  AS Diciembre, "
     					   +" 0    AS ImporteAnual_Disponible, isnull(nImporte,0) as ImporteObtenido "
                           +" from tContratoPlurianual_EP WITH(NOLOCK)  "+ campos
						   +" order by 1 "; 
      					 
				System.out.println(strQuery);
				strParam = "";
			}  

		//Catalogo para el Evento Partida
		if (strTabla.equals("M_TEVENTOPARTIDA")){
	 		strQuery = "select cEvento,	cPartida from tEventoPartida  WITH(NOLOCK) ";
            System.out.println(strQuery);
	 	}

		//Precompromiso materiales-procedimiento
		if(strTabla.equals("MONTOS_U_PRECOM")){
	 		String consolidado = strParam;
		 	strQuery = "select  cidsubpartida,cidunidadEjecutoraSolicitud, SUM (total) total from ( "+
		 	" select a.nIdLineaConsolidado,a.cIdConsolidado,c.cIdProcedimiento,b.cIdUnidadEjecutoraSolicitud,b.nIdLineaSolicitud,"+
		 	" b.cIdSolicitud, s.cIdSubPartida, s.mPrecioUnitario, s.nCantidad, (d.mMontoNetoLinea) total from"+
   			" mConsolidadoLineas a  with(nolock) inner join mProcedimiento c with(nolock)  on c.cIdConsolidado=a.cIdConsolidado inner join mProcedimientoAdjudicacionPartidas d with(nolock) on "+
  			" c.cIdProcedimiento=d.cIdProcedimiento and d.nIdLineaConsolidado=a.nIdLineaConsolidado "+
 			" inner join mConsolidadoSolicitud b with(nolock) on a.cIdConsolidado=b.cIdConsolidado and b.nIdLineaConsolidado=d.nIdLineaConsolidado "+
			" inner join mSolicitudLineas s with(nolock) on b.cIdSolicitud=s.cIdSolicitud and s.nIdLineaSolicitud=b.nIdLineaSolicitud "+
			" where a.cIdConsolidado='"+consolidado+"' ) x  "+
			" group by  cIdUnidadEjecutoraSolicitud,cIdSubPartida";
			System.out.println(strQuery);
			strParam = "";
	 	} 
		
		if (!strParam.equals("TODO") && !strParam.equals("")){
			if ((strQuery != null) && strQuery.indexOf("WHERE")>= 0)
			{
				strQuery += " AND " + strParam.toUpperCase() + " ";
			}
			else
			{
				strQuery += " WHERE " + strParam.toUpperCase() ;
			}

		}
	
		if (strOrder != null && !strOrder.equals("")){
				strQuery +=  strOrder.toUpperCase() ;
		}

		
		if (strTabla.equals("BUSCA_POLIZA")){
	 		strQuery = " exec.dbo.sp_PolizaConagua '',"+ campos;
	 		System.out.println("Buscando Poliza: strQuery"+strQuery);

	 	}
	 	
        if (strTabla.equals("BUSCA_DATOS_TABLAS")){
	 		strQuery = " exec.dbo.sp_BuscaDatoCamposTabla_syc "+ campos;
	 		System.out.println(" strQuery"+strQuery);

	 	}
	 	 if (strTabla.equals("BUSCA_CONTRARRECIBO")){
	 	 System.out.println(strParam+"  Param \n");
	 	     strQuery ="SELECT caNoContrarrecibo,cCentroContable FROM("
				+"select distinct caNoContrarrecibo,cIdEntidadContable cCentroContable from  tPagoDirectoEncabezado te with(nolock)" 
				+" UNION "
				+"select distinct caNoContrarrecibo,cCentroContable from  tPAGODIVERSOEncabezado te with(nolock)" 
				+" UNION "
				+"select distinct caNoContrarrecibo,cCentroContable from  tPAGOOBRAEncabezado te with(nolock)" 
				+" UNION "
				+"select distinct caNoContrarrecibo,cCentroContable from  tPAGOFEDERALIZADOEncabezado  te with(nolock)" 
				+" UNION "
				+"select distinct caNoContrarrecibo,cCentroContable from  tRELACIONGASTOSEncabezado  te with(nolock)" 
				+" UNION "
				+"select distinct caNoContrarrecibo,cCentroContable from  tOperAjenasEncabezado  te with(nolock)"
				+" UNION "
				+" select distinct caNoContrarrecibo,cCentroContable from  tNOMINAEncabezado  te with(nolock)" 
				+")CR where "+strParam;
	 	}
	 	 if (strTabla.equals("BUSCA_CIDDOCUMENTO")){
	 	     strQuery ="select distinct cIdDocumento,cidEntidadContable from  tPagoDirectoEncabezado te with(nolock) WHERE "+strParam;
	 	}
	 	 if (strTabla.equals("BUSCA_FACTURA")){
	 	     strQuery ="SELECT cNoFactura,cCentroContable FROM("
					+"select distinct cNoFactura,cCentroContable  from  tPAGODIVERSOEncabezado te with(nolock)" 
					+" UNION"
					+" select distinct cNoFactura,cCentroContable from  tPAGOOBRAEncabezado te with(nolock)" 
					+" UNION"
					+" select distinct cNoFactura,cCentroContable from  tPAGOFEDERALIZADOEncabezado  te with(nolock)" 
					+" )CF WHERE "+strParam;
	 	}
	 		 if (strTabla.equals("BUSCA_CONTRATO")){
	 	     strQuery ="select distinct cIdContrato,cIdEntidadContable  from tContratoEP with(nolock) where "+strParam;
	 	}
	 		 if (strTabla.equals("CUENTAS_CONTABLES")){
	 	     strQuery ="select nCuenta,dCuenta  from tCuentas with(nolock) where AplicacionCuenta='S'and not (TipoBalance='P' and TipoCuenta='P') ";
	 	     if(strParam.compareTo("TODO")!=0)
              strQuery +=" and "+strParam;
	 	}
	 	if (strTabla.equals("BUSCA_CIDRELACION")){
	 	     strQuery ="select distinct cIdRelacion,cCentroContable from tRELACIONGASTOSEncabezado with(nolock) where "+strParam;
	 	    
	 	}
	 	if (strTabla.equals("BUSCA_COMPROMISO")){
	 	     strQuery ="select distinct caNoCompromiso,cCentroContable from tCompromisoEncabezado with(nolock) where  "+strParam;
	 	    
	 	}
	 	if (strTabla.equals("BUSCA_EP")){
	 	     strQuery ="select distinct ep,cCentroContable from tSaldos ts with(nolock) inner join tCatalogoEP ep with(nolock) on ts.cSubCuenta=ep.EP";
	 	    
	 	}
	 	if (strTabla.equals("BUSCA_MAP")){
	 	     strQuery ="select distinct nNumMAP from tADECUACIONAUTEncabezado TE with(nolock) inner join  tAdecuacionAutDetalle TD with(nolock) on te.nFolioAdecuacionaut=td.nFolioAdecuacionaut and "+strParam;
	 	    
	 	}
	 	if (strTabla.equals("BUSCA_SIAFF")){
	 	     strQuery ="select distinct nFolioSIAFF from tEjercidoEncabezado te with(nolock)  inner join tEjercidoDetalle td with(nolock) on te.nFolioEjercido=td.nFolioEjercido and  "+strParam;
	 	    
	 	}
	 	if (strTabla.equals("BUSCA_SICOP")){
	 	     strQuery ="select distinct nFolioSICOP from tEjercidoEncabezado te with(nolock)  inner join tEjercidoDetalle td with(nolock) on te.nFolioEjercido=td.nFolioEjercido and  "+strParam;
	 	    
	 	}
	 	if (strTabla.equals("BUSCA_BENEFICIARIO")){
	 	    strQuery = " select dRFC, dnombre from [dbo].[v_Beneficiarios] with(nolock) ";
            if(strParam.compareTo("TODO")!=0)
            strQuery +=" where "+strParam;
            System.out.println(": strQuery"+strQuery);
        }
        if (strTabla.equals("REPORTES_CONTABLES")){
	 	    strQuery = " select IdReporte, NombreReporte,TipoReporte,Action,Descripcion from cg_reporte_contable with(nolock) order by Orden  ";
	 	    System.out.println(": strQuery"+strQuery);
          }
	 	
	 	
	 	if (strTabla.equals("MONTOMODIFICADORELG")){
	 	    strQuery = "select  ISNULL(SUM(re.mImporteTotalFactura),0) as sumaFoliosHidden  from mRelacionGastosFacturasEncabezado re " + strMaxReg;
            System.out.println(strQuery);
   
	      }	
	 	
	 	if (strTabla.equals("MONTOMODIFICADODETALLERELG")){
	 	    strQuery = "select  ISNULL(SUM(re.mImporteTotalFacturaDetalle),0) as sumaFoliosHidden  from mRelacionGastosFacturasDetalle re " + strMaxReg;
            System.out.println(strQuery);
   
	      }	
	 	

	 	
	 	if( strTabla.equals("R_CARGA_EVENTO") ){
	 		strQuery ="select evt.* from("+
	 					" select"+  
	 					" nDocRenglon"+
	 					" ,cIdGrupoEvento"+
	 					" ,cIdSubGrupoEvento"+
	 					" ,cIdEventoManual"+
	 					" ,convert(varchar,cIdGrupoEvento)+'_'+convert(varchar,cIdSubGrupoEvento)+'_'+cIdEventoManual Evento"+
	 					" ,te.nCuenta"+
	 					" ,cEvento"+
	 					" ,te.cTipoPoliza"+
	 					" ,isnull(cSubcuenta,'')cSubcuenta "+
	 					" ,isnull(cBloqueaAbonos,'0000000000000') cBloqueaAbonos"+
	 					" ,isnull(cBloqueaCargos,'0000000000000') cBloqueaCargos"+ 
	 					" from "+
	 					"	tEventoManual TE  with(nolock)"+
	 					"		inner join "+
	 					"	tCuentas TC  with(nolock) "+
	 					"	on TE.nCuenta=tc.nCuenta WHERE TE.cModulo='CAJA'"+	 						
	 				    " )EVT "+
	 				    " inner join tEventoRelacion r WITH (NOLOCK) on evt.cIdGrupoEvento=r.nIdGrupoEvento and "+
	 					" evt.cIdSubGrupoEvento=r.nIdSubGrupoEvento and "+
	 					" evt.cIdEventoManual=r.cEvento "+campos;		 		
	 	}
	 	if( strTabla.equals("R_CARGA_CAJA") ){
	 		strQuery =" select TE.nFoliocaja,TE.cdescripcionpoliza,isnull(TE.cmotivorechazo,'') cmotivorechazo,TE.cunidadejecutora,TE.nidgrupoevento,ndocRenglon,cEvento,'$' + CONVERT(varchar(20), mImporte, 1) mImporte , '$' + CONVERT(varchar(20), mImporteNegativo, 1) mImporteNegativo ,ALM,CTAB,OBGT,RFC,TE.ctipopoliza,convert(varchar,te.mmontosolicitud ) mMontoSolicitud,TE. nFolioComprobacion,te2.cdescripcionpoliza,ve.mMontoRemanente,NCUENTABENEFICIARIO ctabBeneficiario"
	 		            +" ,TE.cNombreBeneficiarioCheque as Nombre_Cheque,TE.cAPaternoCheque as APaterno_Cheque,TE.cAMaternocheque as AMaterno_Cheque,FFM,idMeta AS meta from tcajaencabezado TE  with(nolock) inner join tcajadetalle TD with(nolock) on TE.nFoliocaja=TD.nFoliocaja"   
	 			  		+" left join tEstadoDeCuentaViaticosEncabezado VE on te.nFolioComprobacion=ve.nFolioCaja " 
	 			  		+" left join tcajaencabezado TE2 on ve.nFolioCaja=TE2.nFolioCaja "
	 			  		+" left join tAnticiposMetas META on TE.nFoliocaja = META.nFolioCaja " + campos;
	 	}
	 	
	 	if( strTabla.equals("R_CARGA_DEVOLUCIONES") ){
	 		strQuery ="SELECT TE.nFolioCaja,TE.cdescripcionpoliza , CONVERT(VARCHAR(32),VE.mMontoRemanente, 13) AS mMontoRemanente,td.RFC "
 		 				+" FROM tEstadoDeCuentaViaticosEncabezado VE WITH (NOLOCK) INNER JOIN tcajaencabezado TE WITH (NOLOCK) ON VE.nFolioCaja = TE.nfoliocaja inner join tcajadetalle td on te.nFoliocaja=td.nFoliocaja "+campos;
	 	}
	 	if( strTabla.equals("R_CARGA_BONIFICACION") ){
	 		strQuery ="SELECT id,nfoliocaja,cdescripcionpoliza,mmontosolicitud,remanente, disponible"
	 					+" FROM vComprobaciones2 WITH (NOLOCK)"
 		 				+campos;
	 	}
	 	if( strTabla.equals("R_CARGA_COMPLAU") ){
	 		strQuery ="SELECT nFoliocaja, cdescripcionpoliza, mImporte, tipoAnticipo FROM vComprobacionLaudos WITH (NOLOCK) "
	 				+campos;
	 	}
	 	if( strTabla.equals("R_CARGA_PLUOBRA") ){
	 		strQuery ="Select vcIdContrato,vcDescripcion,vcIdRFC,vcBeneficiario ,vmImporte,vmIVA,vmTotal from vContratoObraPlu WITH (NOLOCK) "
	 				+campos;
	 	}
	 	if( strTabla.equals("R_CARGA_REINTEGROS") ){
	 		strQuery ="SELECT det.nFoliocaja, cdescripcionpoliza, mMontoSolicitud"
	 					+" FROM dbo.tcajadetalle AS det WITH (NOLOCK)"
 		 				+" JOIN dbo.tcajaencabezado AS enc WITH (NOLOCK) ON det.nFoliocaja = enc.nFoliocaja "+campos;
	 	}
	 	if( strTabla.equals("R_CARGA_RENDIMIENTOS") ){
	 		strQuery ="SELECT det.nFoliocaja, cdescripcionpoliza, mMontoSolicitud"
	 					+" FROM dbo.tcajadetalle AS det WITH (NOLOCK)"
 		 				+" JOIN dbo.tcajaencabezado AS enc WITH (NOLOCK) ON det.nFoliocaja = enc.nFoliocaja "+campos;
	 	}
	 	if( strTabla.equals("R_CARGA_DEVOLUCIONES_L") ){
	 		strQuery ="SELECT nFolioCaja "
 					+"	, cdescripcionpoliza "
 					+"	, SUM(mImporte) - SUM(TBL.mImporteNeto) AS mMontoRemanente "
 					+"	, RFC "
 					+"	, cunidadejecutora "
 					+"FROM ( "
 					+"(SELECT ENC.nFoliocaja, " 
 					+"		ENC.cdescripcionpoliza, "
 					+"		mImporte + ISNULL(DEV.mImporteNegativo,0) AS mImporte, cEvento, ISNULL(mImporteNeto,0) AS mImporteNeto, "
 					+"		RFC, "
 					+"		cunidadejecutora "
 					+"FROM   tcajaencabezado ENC WITH (nolock) "
 					+"		INNER JOIN tcajadetalle DET WITH (nolock) "
 					+"	  		ON ENC.nFoliocaja = DET.nFoliocaja "
 					+"		LEFT JOIN (SELECT ENCDEV.nFolioComprobacion, mImporteNegativo " 
 					+"					FROM tcajaencabezado ENCDEV WITH (NOLOCK) "
 					+"						JOIN tcajadetalle DETDEV ON ENCDEV.nFoliocaja = DETDEV.nFoliocaja " 
 					+"					WHERE DETDEV.cEvento IN ('8_2_8','8_2_15') "
 					+"						AND ENCDEV.cdocumentohaplicado = 'S') DEV "
 					+"	  		ON ENC.nFoliocaja = DEV.nFolioComprobacion "
 					+"		LEFT JOIN tComprobacionLaudos COM WITH (NOLOCK) "
 					+"	  		ON ENC.nFoliocaja = COM.nFolioCaja "
 					+"WHERE  cEvento IN ('8_2_3','8_2_14') "
 					+"AND ENC.cdocumentohaplicado = 'S' "
 					+"AND ENC.nFoliocaja NOT IN(SELECT Isnull (nFolioCaja, 0) "
 					+"							FROM trelaciongastosencabezado WITH (nolock) " 					
 					+"							WHERE cDocumentoHaplicado = 'S')) "
 					+") TBL " 
 					+ campos
 					+"GROUP BY nFolioCaja, cdescripcionpoliza, RFC, cunidadejecutora"
 					+" ORDER BY nFolioCaja";
	 	}
	 	if( strTabla.equals("CONTACTOS") ){
	 		strQuery ="select nombre from contactos with(nolock) " + strQuery;
	 	}
	 	
	 		System.out.println(": "+strParam);
		
			System.out.println(": "+strQuery);
	 	
	 	if( strTabla.equals("DIRECCION") ){
	 		strQuery ="select nombre, direccion, celular, telefono from contactos with(nolock) WHERE " + strParam;
	 		
	 	}
	
		if( strTabla.equals("R_CARGA_COMISIONES") ){
	 		strQuery =	"	SELECT * "
	 					+" 	FROM dbo.v_ViaticosComisiones WITH (NOLOCK)" + campos;
	 	}
		
		if( strTabla.equals("R_CARGA_COMISION_CAJA") ){
	 		strQuery =	" SELECT	ViaticosComisiones.* " +
						" FROM		v_ViaticosComisiones ViaticosComisiones WITH (NOLOCK) " +
						" INNER JOIN  tRelacionComprobacionComisiones RelacionComprobacionComisiones WITH (NOLOCK) " +
						" ON (RelacionComprobacionComisiones.nIdComision = ViaticosComisiones.nIdComision)" + campos;
	 	}
	 	if ( strTabla.equals("CRFC")){
	 		strQuery = "SELECT cRFC " + 
						"	, cSubcuenta " +
						"	, esPatrimonial " +
						"	, nTipoPersona AS cIdTipoPersonaRFC " +
						"	, cRazonSocial " +
						"	, cNombre " +
						"	, cApPaterno " +
						"	, cApMaterno " +
						"	, esExtranjero " +
						"	, cNoExpediente " +
						"   , cFechaDemanda " +
						"	, cNoOficio " +
						"	, cFechaOficio " +
						"	, nMontoEstimado " +
						"   , cFechaCaptura " +
						"   , cFechaAlta " +
						"	, idPasivoContingente " +	 					
						"FROM dbo.tpasivosContingentes WITH (NOLOCK) WHERE Estatus IS NULL" 
						+ (  ( StringUtils.isEmpty( strParam  ) || "TODO".equalsIgnoreCase(strParam) ) ? "" : " AND " + strParam );
	 	}	
	 	if ( strTabla.equals("CCRI")){
	 		strQuery = "SELECT cClaveCRI AS cCRI " + 
						"	, cNombre " +							 				
						"FROM vCri_RazonSocial WITH (NOLOCK)";
	 	}	
	 	if ( strTabla.equals("CRFCLABORAL")){
	 		strQuery = "SELECT cRFC AS cRFCLaboral" + 						
						"	, esPatrimonial " +
						"	, nTipoPersona AS cIdTipoPersonaRFC " +
						"	, cRazonSocial " +
						"	, cNombre " +
						"	, cApPaterno " +
						"	, cApMaterno " +
						"	, esExtranjero " +						
						"	, pasivo.idPasivoContingente " +	
						"	, cMes " +
						"	, mImporte " +
						"	, cRFCBen " +
						"	, cNombreBen " +
						"	, cApPaternoBen " +
						"	, cApMaternoBen " +
						"FROM tpasivosContingentes AS pasivo WITH (NOLOCK) " + 
						"LEFT JOIN tpasivosContingentesLaborales AS laboral WITH (NOLOCK) ON pasivo.idPasivoContingente = laboral.idPasivoContingente " +
						"WHERE Estatus IS NULL AND esPatrimonial = 0" 
						+ (  ( StringUtils.isEmpty( strParam  ) || "TODO".equalsIgnoreCase(strParam) ) ? "" : " AND " + strParam );
	 	}	
	 		 	
	 	//catalogos de cfdi
	 	
	 	if(strTabla.equalsIgnoreCase("v_cEstado")){				
			strQuery = "SELECT	cEstado , "
					+ "	        cNombreEstado"
					+ "	 FROM	vCFDI_Cat_Estado WITH(NOLOCK) " 
					+ (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);			
		}
	 	if(strTabla.equalsIgnoreCase("vCFDI_Cat_CodigoPostal")){				
			strQuery = "SELECT	cCodigoPostal , "
					 + "        cEstado , "
					+ "	        cMunicipio , "
					+ "	        cNombreEstado , "
					+ "	        cNombreMunicipio "
					+ "	 FROM	vCFDI_Cat_CodigoPostal WITH(NOLOCK)";			
		}
	 	if(strTabla.equalsIgnoreCase("vCFDI_Cat_CodigoPostal")){				
			strQuery = "SELECT	cCodigoPostal , "
					 + "        cEstado , "
					+ "	        cMunicipio , "
					+ "	        cNombreEstado , "
					+ "	        cNombreMunicipio "
					+ "	 FROM	vCFDI_Cat_CodigoPostal WITH(NOLOCK)";			
		}
	 	if(strTabla.equals("TCFDI_CAT_FOLIOSERIE")){				
			strQuery =  "SELECT	cSerie, "
					 + "		cFolio " 
					 + "  FROM	tSerieFolio WITH(NOLOCK) ";			
		}
		
		if(strTabla.equals("TCFDI_CAT_ADUANA")){				
		strQuery = " SELECT cAduana, cNombreAduana FROM tCFDI_Cat_Aduana WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_CODIGOPOSTAL")){				
			strQuery = " SELECT cCodigoPostal, cEstado, cMunicipio, cLocalidad FROM tCFDI_Cat_CodigoPostal WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_CLAVE_UNIDAD")){				
			strQuery = " SELECT cClaveUnidad, cNombreUnidad, cDescripción, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_Clave_unidad WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_PRODUCTOSERVICIO")){				
			strQuery = " SELECT cClaveProdServ, cDescripcionProdServ, fFechaInicioVigencia, fFechaFinVigencia, lIncluirIVATrasladado, lIncluirIEPStrasladado, cCOmplementosIncluir FROM tCFDI_Cat_ProductoServicio WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_FORMADEPAGO")){				
			strQuery = " SELECT cFormaPago, cDescripcion, cBancarizado, cNumeroOperacion, cRFCEmisorCuentaOrdenante, cCuentaOrdenante, cPatronCuentaOrdenante, cRFCEmisorCuentaBeneficiario, cCuentaBenenficiario, cPatronCuentaBeneficiaria, cTipoCadenaPago, cNombreBancoEmisorCtaOrdenanteExtranjero, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_FormaDePago WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_MONEDA")){				
			strQuery = " SELECT cMoneda, cDescripcion, nDecimales, nPorcentajeVariacion, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_Moneda WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_METODOPAGO")){				
			strQuery = " SELECT cMetodoPago, cDescripcion, fFechaInicioVigencia, fFechaInicioVigencia FROM tCFDI_Cat_MetodoPago WITH(NOLOCK) ";			
		}
		if(strTabla.equals("TCFDI_CAT_IMPUESTOS")){				
			strQuery = " SELECT cImpuesto, cDescripcion, cRetencion, cTraslado, cLocalFederal, cEntidadAplica FROM tCFDI_Cat_Impuestos WITH(NOLOCK) ";			
		}
	 	if (strTabla.equals("M_TCFDI_CAT_USOCFDI")){
			strQuery = " SELECT cUsoCFDI, cDescripcion, cAplicaPersonaFisica, cAplicaPersonaMoral, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_UsoCFDI WITH(NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_TIPORELACION")){
			strQuery = " SELECT cTipoRelacion, cDescripcion, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_TipoRelacion WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_TIPOFACTOR")){
			strQuery = " SELECT cTipoFactor FROM tCFDI_Cat_TipoFactor WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_TIPOCOMPROBANTE")){
			strQuery = " SELECT cTipoComprobante, cDescripcion, nValorMaximo, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_TipoComprobante WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_TASACUOTA")){
			strQuery = " SELECT cEsRangoOFijo, cTasaOCuotaVMinimo, cTasaOCuotaVMaximo, cImpuesto, cFactor, cEsTraslado, cEsRetencion, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_TasaCuota WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_REGIMENFISCAL")){
			strQuery = " SELECT cRegimenFiscal, cDescripcion, cAplicaPFisica, cAplicaPMoral, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_RegimenFiscal WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_PATENTEADUANAL")){
			strQuery = " SELECT cPatenteAduanal, cIniciovigencia, cFinVigencia FROM tCFDI_Cat_PatenteAduanal WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_PAIS")){
			strQuery = " SELECT cPais, cDescripcion, cFormatoCodigoPostal, cFormatoRegistroIdentidadTributaria, cValidacionRegistroIdentidadTributaria, cAgrupaciones FROM tCFDI_Cat_Pais WITH (NOLOCK) ";
		}
		if (strTabla.equals("M_TCFDI_CAT_NUMPEDIMENTOADUANA")){
			strQuery = " SELECT cAduana, nPatente, nEjercicio, cCantidad, fFechaInicioVigencia, fFechaFinVigencia FROM tCFDI_Cat_NumPedimentoAduana WITH (NOLOCK) ";
		}	 	
		
		if (strTabla.equals("CAT_EMISOR_CFDI")){
			strQuery = " SELECT RFC cfdiEmisorRFC, cNombre cfdiEmisorNombre, nRegimenFiscal cfdiEmisorRegimenFiscal, cDescripcion cfdiEmisorRegimenFiscalDesc FROM CAT_EMISOR_CFDI emisor WITH (NOLOCK)" 
					+ " INNER JOIN dbo.tCFDI_Cat_RegimenFiscal rf (NOLOCK) "
					+ " ON emisor.nRegimenFiscal = rf.cRegimenFiscal";
		}	 	
		
		if (strTabla.equals("CAT_RECEPTOR_CFDI")){
			strQuery = " SELECT RFCReceptor cfdiReceptor, cNombre, cCorreo, RFCEmisor, cTipoPersonaRFC, cIdTipoPersonaRFC" 
					+ " FROM CAT_RECEPTOR_CFDI (NOLOCK)"
					+ " INNER JOIN  dbo.pCatalogoTipoPersonaRFC (NOLOCK) "
					+ " ON cIdTipoPersonaRFC = cTipoPersona";
		}
		if (strTabla.equals("TCONTRATOS")){
			strQuery = " SELECT nFolioContratoPlurianual, cDescripcionProyecto, cFolioMASCP, cOficioDG, mMontoTotalContrato" 
					+ " FROM tContratosPlurianualEncabezado (NOLOCK) where cDocumentoHaplicado IN ( 'S', 'M')"
					+ " UNION  " 
					+ " SELECT nFolioContratoPlurianual, cDescripcionProyecto, cFolioMASCP, cOficioDG, mMontoTotalContrato"
					+ " FROM sai_2022..tContratosPlurianualEncabezado (NOLOCK) where cDocumentoHaplicado IN ( 'S', 'M')";
		}
		if( "RFCPagoSinFacturaPermitido".equalsIgnoreCase(strTabla) ){
			strQuery ="SELECT	cRFC, "
					 +"      	cTipoPago, "
					 +"      	lImpuestoForzoso"
					 +"  FROM	tRFCPagoSinFacturaPermitido WITH(NOLOCK) "
					 + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);
		}
		//Catalogo para el CRI
		if (strTabla.equals("M_TCATALOGOCRI")){
	 		strQuery = "SELECT cRubro "
					  +", cTipo " 
					  +", cClase "
					  +", cConcepto "
					  +", cClaveCRI "
					  +", cNombre "
					  +", cTipoGasto "
					  +", cFuenteFinanciamiento "
					  +", cPartida "
					  +", cCuentaIngreso "
					  +", cAplica "
					  +" FROM tCatalogoCRI_v2 WITH (NOLOCK) "
					  + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);
	 	}
	 	if (strTabla.equals("M_VCRI")){
	 		strQuery = "SELECT DISTINCT OBGT "
						+" , TG "
						+" , FF "
						+" , cri "
						+" , cNombre "
						+" FROM ( "
	 					+" SELECT CASE WHEN LEN(cSubCuenta) = 5 THEN cSubCuenta ELSE SUBSTRING(cSubCuenta,32,5) END AS OBGT "
						+" , CASE WHEN LEN(cSubCuenta) = 5 THEN 1 ELSE SUBSTRING(cSubCuenta,38,1) END AS TG "
						+" , CASE WHEN LEN(cSubCuenta) = 5 THEN 4 ELSE SUBSTRING(cSubCuenta,40,1) END AS FF "
						+" , cri "
						+" , cNombre "
						+" FROM vCri_v2 WITH (NOLOCK) ) AS vCri"
	 					+ (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);
	 	}
	 	if (strTabla.equals("M_TCATCLASIFICACIONADMINISTRATIVA")){
	 		strQuery = "SELECT cClave "
					  +", cDescripcion "
					  +", cRamo "
					  +", cUR " 	 	 					
					  +" FROM tCatClasificacionAdministrativa WITH (NOLOCK) "
					  + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);
	 	}
	 	
	 	if( strTabla.equals("R_EVENTO_ORIGEN_REINTEGROCAJA") ){
			strQuery = 
				" SELECT evento.cEvento,Evento.dEvento,Evento.nIdGrupoEvento from (select CONVERT( VARCHAR, e.nidgrupoevento)+'_'+ CONVERT( VARCHAR, e.nIdSubGrupoEvento )+'_'+convert(varchar,e.cevento) AS cEvento" + 
				"        ,devento,nidgrupoevento,e.cUR" +				
				" FROM   teventorelacion e WITH (NOLOCK)  inner join teventomanual m WITH (NOLOCK) on " +
 				" e.nidgrupoevento=m.cidgrupoevento " +
 				" and e.nidsubgrupoevento=m.cidsubgrupoevento " +
 				" and e.cevento=m.cideventomanual " +
 				" where m.cpartida is null and m.ndocrenglon=1 and cModulo='REINTEGROCAJA'" +
 				" ) Evento ";			
 				
			if(strParam.compareTo("TODO")!=0)
	            strQuery +=" where " + strParam;//.replaceAll("%","");
				strQuery += " AND evento.cEvento LIKE '5_%1'";
		}
	 	
	 	if( strTabla.equals("R_EVENTO_DESTINO_REINTRGROCAJA") ){
			strQuery = 
				" SELECT evento.cEvento,Evento.dEvento,Evento.nIdGrupoEvento from (select CONVERT( VARCHAR, e.nidgrupoevento)+'_'+ CONVERT( VARCHAR, e.nIdSubGrupoEvento )+'_'+convert(varchar,e.cevento) AS cEvento" + 
				"        ,devento,nidgrupoevento,e.cUR" +				
				" FROM   teventorelacion e with(nolock)  inner join teventomanual m WITH (NOLOCK) on " +
 				" e.nidgrupoevento=m.cidgrupoevento " +
 				" and e.nidsubgrupoevento=m.cidsubgrupoevento " +
 				" and e.cevento=m.cideventomanual " +
 				" where m.cpartida is null and m.ndocrenglon=1 and cModulo='REINTEGROCAJA'" +
 				" ) Evento ";
			
			if(strParam.compareTo("TODO")!=0)
	            strQuery +=" where " + strParam;	
				strQuery += " AND evento.cEvento NOT LIKE '5_%1'";
		}
	 	
	 	if( strTabla.equals("R_CARGA_EVENTO_RECA") ){
	 		strQuery ="select evt.* from("+
	 					" select"+  
	 					" nDocRenglon"+
	 					" ,cIdGrupoEvento"+
	 					" ,cIdSubGrupoEvento"+
	 					" ,cIdEventoManual"+
	 					" ,convert(varchar,cIdGrupoEvento)+'_'+convert(varchar,cIdSubGrupoEvento)+'_'+cIdEventoManual Evento"+
	 					" ,te.nCuenta"+
	 					" ,cEvento"+
	 					" ,te.cTipoPoliza"+
	 					" ,isnull(cSubcuenta,'')cSubcuenta "+
	 					" ,isnull(cBloqueaAbonos,'0000000000000') cBloqueaAbonos"+
	 					" ,isnull(cBloqueaCargos,'0000000000000') cBloqueaCargos"+ 
	 					" from "+
	 					"	tEventoManual TE  with(nolock)"+
	 					"		inner join "+
	 					"	tCuentas TC  with(nolock) "+
	 					"	on TE.nCuenta=tc.nCuenta WHERE TE.cModulo='REINTEGROCAJA'"+	 						
	 				    " )EVT "+
	 				    " inner join tEventoRelacion r WITH (NOLOCK) on evt.cIdGrupoEvento=r.nIdGrupoEvento and "+
	 					" evt.cIdSubGrupoEvento=r.nIdSubGrupoEvento and "+
	 					" evt.cIdEventoManual=r.cEvento "+campos;		 		
	 	}
	 	
	 	if (strTabla.equals("M_TCATALOGOCLAVEINTERNA")){
			strQuery = " SELECT cClaveInterna FROM tCatalogoClaveInterna WITH(NOLOCK) "
					 + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);			
		}
	 	
	 	if (strTabla.equals("M_TCATALOGOMETAS")){
			strQuery = " SELECT cMeta "+
					   "		, dMeta "+
					   "		, cProgramaPresupuestario "+
					   "		, cCartera "+
					   "		, cUnidadEjecutora "+
					   "		, cUnidadNorativa "+					   
					   " FROM tCatalogoMetas WITH(NOLOCK) "
					 + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);			
		}
	 	
	 	if (strTabla.equalsIgnoreCase("vOrganization")){
			strQuery = "SELECT	idUnit, " +
						"		executiveUnit, " + 
						"		description, " +
						"		administrativeUnitName " + 
						"  FROM	organizationUnit WITH(NOLOCK) "
					 + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);			
		}
	 	
	 	if (strTabla.equalsIgnoreCase("vEmployees")){
			strQuery = "SELECT	clave,  "
					+"		nombren,  "
					+"		nombrep, "
					+"		nombrem,  "
					+"		cargo,  "
					+"		cUejecutora,  "
					+"		nombren + isnull(' ' + nombrep, '') + isnull(' ' + nombrem,'') AS employeeResponsibleName "
					+"  FROM	v_empleados_giro "
					+" WHERE	n_emplstatus = 1 "
					+ (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " AND " + strParam);			
		}
	 	
	 	if (strTabla.equalsIgnoreCase("vVehicles")){
			strQuery = "SELECT id, "
					 +"        inventory_code, "
					 +"        description, "
					 +"        brand, "
					 +"        sub_brand, "
					 +"        model, "
					 +"        liscence_plate," 
					 +"        id_inventory"
					 +" FROM   vehicle  "
					 + (strParam == null || "TODO".equalsIgnoreCase(strParam)?"": " WHERE " + strParam);			
		}
	 		 		 	

		if(blnManto){
			strQuery = getQuery( strTabla,  strQuery);
		}
		System.out.println("strQuery: "+strQuery);
		System.out.println("blnManto: "+blnManto);
		System.out.println("baseDatos: "+baseDatos);
		
		if(blnManto && baseDatos != null){
			jsonStringOrig = ObjC.readCatalogosManto(strQuery, makeCommit, baseDatos).toString(); // Se hace commit
		}
		else{
			jsonStringOrig = ObjC.readCatalogos(strQuery, makeCommit).toString(); // Se hace commit
		}
		
		jsonString     = jsonStringOrig;
		out.print(jsonString);
		
		
		
	}
	
	catch (Exception exc)
	{
		System.out.println(exc);
		System.out.println(exc.getMessage());
		strError = exc.getMessage();
		if(strError!=null){
		strError = strError.replaceAll("'","");
		strError = strError.replaceAll(":","");
		strError = strError.toUpperCase();
		}
		jsonString = "[{'Col1':'" + strError + "'}]";
		out.print(jsonString);
	}
%>

<%!


private String baseDatos = null;

private String getQuery(String strTabla, String strQuery){
		
		if (strQuery.indexOf("EFISCAL")!=-1){
			

			int idx = strQuery.lastIndexOf("EFISCAL") -4;
			int idxFin = -1;
						
			if(  (strTabla.equals("M_CG_GRUPO") && idx != 45)  ||(strTabla.equals("M_CG_ROLES") && idx != 59)  || (strTabla.equals("M_VIMX_USUARIO") && idx != 459)  || (strTabla.equals("M_CG_USUARIO_PROPIEDADES") && idx != 61)  || (strTabla.equals("M_CG_ROL_USUARIO") && idx != 59 )  ||
				(strTabla.equals("M_CG_GRUPO_USUARIO") && idx != 39)  ){
				if(strQuery.substring(idx+16).length()>10){ //Mas filtros que el ejercicio fiscal
					if(strQuery.indexOf("*")!=-1){
						baseDatos = strQuery.substring(idx+16,idx+20).trim().replaceAll("'%", "").replaceAll("%'", "");
						idxFin = idx + 22;
					} else if(strQuery.indexOf("PRUEBAS")!=-1){
						System.out.println("base: "+strQuery.substring(idx+20,idx+23));
						baseDatos = strQuery.substring(idx+20,idx+23).trim().replaceAll("'%", "").replaceAll("%'", "");
						idxFin = idx + 33;
					}else{
						baseDatos = strQuery.substring(idx+16,idx+23).trim().replaceAll("'%", "").replaceAll("%'", "");
						idxFin = idx + 25;
					}
				
				} else if(strQuery.indexOf("WHERE EFISCAL")!=-1){
					
					baseDatos = strQuery.substring(idx+16).trim().replaceAll("'%", "").replaceAll("%'", "");
					idx  = strQuery.indexOf("WHERE EFISCAL");
				}else{
					baseDatos = strQuery.substring(idx+16).trim().replaceAll("'%", "").replaceAll("%'", "");
				}
				
				
				if(idxFin==-1)
					strQuery = strQuery.substring(0,idx);
				else{
					if(strTabla.equals("M_CG_ROL_USUARIO") && idx==173){
						strQuery = strQuery.substring(0,idx-2) + strQuery.substring(idxFin);
					}else{
						strQuery = strQuery.substring(0,idx) + strQuery.substring(idxFin);
					}	
				}				
			}
		}
		
	return strQuery;



}


%>