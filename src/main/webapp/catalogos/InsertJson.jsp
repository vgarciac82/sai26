<%@page language="java" contentType="application/json"%>
<%@page import="com.syc.gestion.documental.CatalogosBusinessLogic"%>
<%@page import="com.syc.auditoria.AuditoriaBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>

<%
	String strQuery = "";
	String strError = "";
	boolean blnCatUsu = false;
	String basesDatos = "";
	String strTabla = request.getParameter("Tabla");
	String strModulo =strTabla;//por si no entra a algun if donde se llena con el nombre adecuado
	String strParam = request.getParameter("Param");
	String jndiName = "jdbc/gestion";

	String[][] strParamQ = null;
	String[] strParamSplit = null;

	strParam = strParam.replaceAll("Ã","Á");
	strParam = strParam.replaceAll("Ã¡","Á");
	strParam = strParam.replaceAll("Ã‰","É");
	strParam = strParam.replaceAll("Ã©","É");
	strParam = strParam.replaceAll("Ã","Í");
	strParam = strParam.replaceAll("Ã­","Í");
	strParam = strParam.replaceAll("Ã“","Ó");
	strParam = strParam.replaceAll("Ã³","Ó");
	strParam = strParam.replaceAll("Ãš","Ú");
	strParam = strParam.replaceAll("Ãº","Ú");
	strParam = strParam.replaceAll("Ã‘","Ñ");
	strParam = strParam.replaceAll("Ã±","Ñ");
	strParam = strParam.replaceAll("Ãœ","Ü");
	strParam = strParam.replaceAll("Ã¼","Ü");

	strParam = strParam.replaceAll("'NULL'","NULL");

	String jsonStringOrig = "";
	String jsonString = "";

	try
	{

		CatalogosBusinessLogic ObjC = new CatalogosBusinessLogic(jndiName);
		
		if( "tCFDI_Cat_FolioSerie".equalsIgnoreCase(  strTabla ) ){
			strQuery = "INSERT INTO tSerieFolio( cSerie, cFolio ) "
					 + "VALUES(" + strParam + ")";
		}		
		//ep brenda	
		if (strTabla.equals("M_TCATALOGOEP"))
		{
			String [] strParamSeparados = strParam.replaceAll(" ","").split(",");
			String fiscal = strParamSeparados [0];
			String ep = strParamSeparados [0] + "." + strParamSeparados [15] + "." + strParamSeparados [1] + "." + strParamSeparados [2]+ "." + strParamSeparados [3]+ "." + strParamSeparados [4]+ "." + strParamSeparados [5]+ "." + strParamSeparados [6]+ "." + strParamSeparados [7]+ "." + strParamSeparados [8]+ "." + strParamSeparados [9]+ "." + strParamSeparados [10]+ "." + strParamSeparados [11]+ "." + strParamSeparados [12]+ "." + strParamSeparados [13]+ "." + strParamSeparados [14];
			String siaf = strParamSeparados [0] + "." + strParamSeparados [15] + "." + strParamSeparados [1] + "." + strParamSeparados [2]+ "." + strParamSeparados [3]+ "." + strParamSeparados [4]+ "." + strParamSeparados [5]+ "." + strParamSeparados [6]+ "." + strParamSeparados [7]+ "." + strParamSeparados [8]+ "." + strParamSeparados [9]+ "." + strParamSeparados [10]+ "." + strParamSeparados [11]+ "." + strParamSeparados [12];
			String interna =  strParamSeparados [13]+ "." + strParamSeparados [14];
			String ramoep = strParamSeparados [15];
			String strParamResto = strParamSeparados [1] + "," + strParamSeparados [2]+ "," + strParamSeparados [3]+ "," + strParamSeparados [4]+ "," + strParamSeparados [5]+ "," + strParamSeparados [6]+ "," + strParamSeparados [7]+ "," + strParamSeparados [8]+ "," + strParamSeparados [9]+ "," + strParamSeparados [10]+ "," + strParamSeparados [11]+ "," + strParamSeparados [12]+ "," + strParamSeparados [13]+ "," + strParamSeparados [14]+ "," + strParamSeparados [15];
			strQuery = "INSERT INTO tcatalogoep (aEjercicioFiscal, EP ,ClaveSIAFF,ClaveInterna,cRamoEP,cUnidadResponsableEP ,cGrupoFuncional,cFuncion,cSubFuncion,cProgramaGeneral,cActividadInstitucional,cProgramaPresupuestario,cPartida,cTipoGasto,cFuenteFinanciamiento,cEntidadFederativa,cCartera,cUnidadEjecutora,cUnidadNorativa,cRamo) VALUES ("+ fiscal + ",'" + ep.replace("'","") + "','" + siaf.replace("'","") + "','" +interna.replace("'","")+ "'," + ramoep + "," + strParamResto.toUpperCase() +")";
		}
	
		//Catalogo TUECUENTASBANCARIAS
		if (strTabla.equals("M_TUECUENTASBANCARIAS"))
		{
			strQuery = "INSERT INTO tUECuentasBancarias (strUnidadEjecutora,strCentroContable,strRFC,strNombreBeneficiario,strClabe,strTipoCuenta) VALUES ("+ strParam.toUpperCase() +")";
		}
		
		if (strTabla.equalsIgnoreCase("M_VISTASUR"))//URVP.25062014 Relacion UsuarioVista
		{
			strQuery = "INSERT INTO dbo.tVistasUR (usuario, ur, modulo ) VALUES  ("+ strParam.toUpperCase() +")";
		}
		
		//Catalogo CAT_ALMACEN
		if (strTabla.equals("M_CAT_ALMACEN"))
		{
			strQuery = "INSERT INTO CAT_ALMACEN (cCentroContable,	nIdAlmacen,	cAlmacen) VALUES ("+ strParam.toUpperCase() +")";
		}

		if (strTabla.equals("M_OPMAPEOCAMBIOPROGRAMA"))
		{
			strQuery = "INSERT INTO tOPMapeoCambioPrograma (aEjercicioFiscal, cProgramaAnterior, cProgramaNuevo) VALUES ("+ strParam.toUpperCase() +")";
		}
				
		//Catalogo CAT_DET_INSTRUCCION
		if (strTabla.equals("CAT_DET_INSTRUCCION"))
		{
			strQuery = "INSERT INTO cat_det_instruccion (di_descripcion) VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CAT_TIPO_DOCUMENTO
		if (strTabla.equals("CAT_TIPO_DOCUMENTO"))
		{
			strQuery = "INSERT INTO cat_tipo_documento (td_descripcion) VALUES ("+ strParam.toUpperCase() +") ";
		}

		// Catalogo Remitente Interno/Personalizada/Copia para/Turnado
		//strQuery = "";

		//Catalogo CAT_REM_EXTERNO
		if (strTabla.equals("CAT_REM_EXTERNO"))
		{
			strQuery = "INSERT INTO cat_rem_externo (id_procedencia, id_estado, id_municipio, re_nombre, re_cargo, re_direccion, re_localidad ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +") ";
		}
		if (strTabla.equals("TCONTRARECIBODIRECTOCREATE"))
		{
			strQuery = "insert into tcontrarrecibo(caNoContrarrecibo, cIdTipoOperacion, caNoAP, nTipoCambio, mImporteBruto, mImporteSancion, mImporteDevolucion, mAmortizacionAnticipo, mImporteIVA, mImporteRetencion, mImportePenalizacion, mImporteNeto, fProgramadaPago,cIdEntidadContable,aEjercicioFiscal,cIdDocumento,cIdTipoDocumento,cIdSubtipoDocumento,cIdRFC,mOtrosImpuestos) ";
			strQuery += " values("+ strParam.toUpperCase() +") ";
		//System.out.println(strQuery);
		}
		if (strTabla.equals("TCONTRARECIBODIVERSOSCREATE"))
		{
			strQuery = "insert into tcontrarrecibo(caNoContrarrecibo, cIdTipoOperacion, caNoAP, nTipoCambio, mImporteBruto, mImporteSancion, mImporteDevolucion, mAmortizacionAnticipo, mImporteIVA, mImporteRetencion, mImportePenalizacion, mImporteNeto, fProgramadaPago,cIdEntidadContable,aEjercicioFiscal,cIdDocumento,cIdTipoDocumento,cIdSubtipoDocumento,cIdRFC,mOtrosImpuestos) ";
			strQuery += " values("+ strParam.toUpperCase() +") ";
		    System.out.println(strQuery);
		}

		if (strTabla.equals("ACTUALIZARETENCIONDIVERSOS"))
		{
		strQuery = "update tPAGODIVERSODetalle set " +strParam.toUpperCase();

		}

		if (strTabla.equals("ACTUALIZARETENCIONOBRA"))
		{
		strQuery = "update tPAGOOBRADetalle set " +strParam.toUpperCase();

		}

		if (strTabla.equals("ACTUALIZARETENCIONFED"))
		{
		strQuery = "update tPAGOFEDERALIZADODetalle set " +strParam.toUpperCase();

		}


		//SubCatalogo CAT_PROCEDENCIA
		if (strTabla.equals("CAT_PROCEDENCIA"))
		{
			strQuery = "INSERT INTO cat_procedencia (pro_descripcion, id_tipo_procedencia, pro_abreviatura) VALUES ("+ strParam.toUpperCase() +") ";
		}

		//SubCatalogo Cat_Municipio
		//strQuery = "";

		//Catalogo CAT_LDISTRIBUCION
		if (strTabla.equals("CAT_LDISTRIBUCION"))
		{
			strQuery = "INSERT INTO cat_ldistribucion (id_ldistribucion, ld_nombre ) VALUES (" + strParam.toUpperCase() + ")";
		}

		//Catalogo CAT_LD_USR
		if (strTabla.equals("CAT_LD_USR"))
		{
			strQuery = "INSERT INTO cat_ld_usr (u_login, id_ldistribucion) VALUES (" + strParam.toUpperCase() + ")";
		}
		//DG
		if (strTabla.equals("M_CG_CAT_EMPLEADO")){
			//System.out.println(strParam.toUpperCase());
			strQuery = "INSERT INTO cg_usuario_grupo (u_login, g_nombre) VALUES (" + strParam.toUpperCase() + ")";
		}

///////////////////////////////////////////////////////////////////////////////////////////////
//CATALOGOS DE MANTENIMIENTO

		if (strTabla.equals("M_TCATALOGOCENTROCONTABLE")){
			strQuery = "INSERT INTO ";
			strQuery += " tCatalogoCentroContable ";
			strQuery += "(cCentroContable, ";
			strQuery += " cDescripcion, ";
			strQuery += " cCentroContableAbierto ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}
//OK

		//Catalogo CAT_FIRMANTE
		if (strTabla.equals("M_CAT_FIRMANTE"))
		{
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT id_area FROM cat_firmante where id_area="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("Ya existe un firmante para esa área.");
			}

			strQuery = "INSERT INTO ";
			strQuery += "cat_firmante ";
			strQuery += "(id_area, ";
			strQuery += " u_login) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Firmantes";
		}

		//Catalogo CAT_DET_INSTRUCCION
		if (strTabla.equals("M_CAT_DET_INSTRUCCION"))
		{
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT di_descripcion FROM cat_det_instruccion where di_descripcion="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El registro ya existe.");
			}
			strQuery = "INSERT INTO ";
			strQuery += "cat_det_instruccion ";
			//strQuery += "(id_det_instruccion, ";
			strQuery += "(di_descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Detalles de Instruccion";
		}

		//Catalogo CAT_ESTADOS
		if (strTabla.equals("M_CAT_ESTADOS"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cat_estados ";
			strQuery += "(id_estado, ";
			strQuery += "edo_nombre, ";
			strQuery += "edo_abreviatura) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CAT_LD_USR
		if (strTabla.equals("M_CAT_LD_USR"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cat_ld_usr ";
			strQuery += "(u_login, ";
			strQuery += "id_ldistribucion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CAT_LDISTRIBUCION
		if (strTabla.equals("M_CAT_LDISTRIBUCION"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cat_ldistribucion ";
			strQuery += "(id_ldistribucion, ";
			strQuery += "ld_nombre) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CAT_MUNICIPIO
		if (strTabla.equals("M_CAT_MUNICIPIO"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cat_municipio ";
			strQuery += "(id_municipio, ";
			strQuery += "id_estado, ";
			strQuery += "mpo_nombre, ";
			strQuery += "mpo_abreviatura) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

//OK
		//Catalogo CAT_PROCEDENCIA
		if (strTabla.equals("M_CAT_PROCEDENCIA"))
		{
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT pro_descripcion FROM cat_procedencia where pro_descripcion="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El nombre de la procedencia, ya existe.");
			}

			strQuery = "INSERT INTO ";
			strQuery += "cat_procedencia ";
			//strQuery += "(id_procedencia, ";
			strQuery += "(pro_descripcion, ";
			strQuery += "id_tipo_procedencia, ";
			strQuery += "pro_abreviatura) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Procedencias";
		}

		//Catalogo CG_TIPO_PROCEDENCIA
		if (strTabla.equals("M_CG_TIPO_PROCEDENCIA"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_tipo_procedencia ";
			strQuery += "(tp_descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

//OK
		//Catalogo CAT_PUESTOS
		if (strTabla.equals("M_CAT_PUESTOS"))
		{
			strParam = strParam.trim();
			strQuery =  " SELECT pto_nombre FROM cat_puestos where pto_nombre="+strParam.toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("La descripción del puesto ya existe.");
			}


			strQuery = "INSERT INTO ";
			strQuery += "cat_puestos ";
			//strQuery += "(id_puesto, ";
			strQuery += "(pto_nombre) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Puestos";
		}

//Catalogo TCAT_MENSAJES
		if (strTabla.equals("TCAT_MENSAJES"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "tcat_mensajes ";
			strQuery += "(nombre, ";
			strQuery += "mensaje, ";
			strQuery += "nAnio ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Mensajes";
		}
		//Catalogo TBEN_BANCOAMBIENTAL
		if (strTabla.equals("TBANCOAMBIENTAL"))
		{
		
			strParam = strParam.trim();
			strQuery =  " SELECT drfc FROM TBEN_BANCOAMBIENTAL where drfc="+strParam.split(",")[0] ;//strParam.toUpperCase() + "'";
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("Ese RFC ya existe en este catalogo, no puede insertarse de nuevo.");
			}


			strQuery = "INSERT INTO ";
			strQuery += "TBEN_BANCOAMBIENTAL ";
			strQuery += "(drfc, ";
			strQuery += "idBancario, ";
			strQuery += "CTAB ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Mantenimiento del catalogo de Beneficiarios bancarios ambiental";
		}
//OK
		//Catalogo CAT_REM_EXTERNO
		if (strTabla.equals("M_CAT_REM_EXTERNO"))
		{
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT re_nombre FROM cat_rem_externo where re_nombre="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El nombre del remitente externo, ya existe.");
			}

			strQuery = "INSERT INTO ";
			strQuery += "cat_rem_externo ";
			//strQuery += "(id_rem_externo, ";
			strQuery += "( ";
			strQuery += "re_nombre, ";
			strQuery += "id_procedencia, ";
			strQuery += "id_estado, ";
			strQuery += "id_municipio, ";
			strQuery += "re_cargo, ";
			strQuery += "re_direccion, ";
			strQuery += "re_localidad ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Remitentes Externos";
		}
		//System.out.println("SQL insercion Remitente Externo: ["+strQuery);

//OK
		//Catalogo CAT_TIPO_DOCUMENTO
		if (strTabla.equals("M_CAT_TIPO_DOCUMENTO"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cat_tipo_documento ";
			strQuery += "(td_id, ";
			strQuery += "td_descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CG_CAT_AREAS
		//if (strTabla.equals("M_CG_CAT_AREAS"))
		//{
		//	strQuery = "INSERT INTO ";
		//	strQuery += "cg_cat_areas ";
		//	strQuery += "(id_area, ";
		//	strQuery += "d_descripcion, ";
		//	strQuery += "tipo_area, ";
		//	strQuery += "prefijo_folio, ";
		//	strQuery += "id_area_padre, ";
		//	strQuery += "bandeja_compartida_in, ";
		//	strQuery += "bandeja_compartida_out, ";
		//	strQuery += "area_estructura) ";
		//	strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		//}

//OK
		//Catalogo CG_CAT_PRIORIDAD
		if (strTabla.equals("M_CG_CAT_PRIORIDAD"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_cat_prioridad ";
			strQuery += "(id, ";
			strQuery += "descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CG_CAT_REMITENTE
		if (strTabla.equals("M_CG_CAT_REMITENTE"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_cat_remitente ";
			strQuery += "(id_remitente, ";
			strQuery += "d_descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CG_CAT_REMITENTE_AREA
		if (strTabla.equals("M_CG_CAT_REMITENTE_AREA"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_cat_remitente_area ";
			strQuery += "(cra_id_area, ";
			strQuery += "cra_descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CG_CAT_REMITENTE_PERSONA
		if (strTabla.equals("M_CG_CAT_REMITENTE_PERSONA"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_cat_remitente_persona ";
			//strQuery += "(crp_id_persona, ";
			strQuery += "(crp_nombres, ";
			strQuery += "crp_apaterno, ";
			strQuery += "crp_amaterno, ";
			strQuery += "crp_id_area, ";
			strQuery += "crp_puesto) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CG_CAT_USUARIOS
		if (strTabla.equals("M_CAT_USUARIOS"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_usuario ";
			strQuery += "(u_login, ";
			strQuery += "u_password, ";
			strQuery += "u_nombre, ";
			strQuery += "u_email, ";
			strQuery += "u_estatus, ";
			strQuery += "cNumeroEmpleado) ";
			strParam+=",'S'";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";//Ethiel, es importante no agregar campos aqui, lo quite y lo puese en la linea de arriba
		}

		//Catalogo CG_CAT_AREAS
		if (strTabla.equals("M_CG_CAT_AREAS"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "CG_CAT_AREAS ";
		//	strQuery += "(ID_AREA, ";
			strQuery += "(D_DESCRIPCION, ";
			strQuery += "PREFIJO_FOLIO, ";
			strQuery += "ID_AREA_PADRE, ";
		//	strQuery += "AREA_TIEMPO_LIM, ";
		//	strQuery += "AREA_ARCH_MAX, ";
			strQuery += "FOLIO_INICIAL) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Areas";
		}

		//Catalogo CG_USUARIO_ROLE
		if (strTabla.equals("M_CAT_PUSUARIO"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "CG_USUARIO_ROLE ";
			strQuery += "(U_LOGIN, R_NOMBRE) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";

		}

		//Catalogo CG_ROLE_OPCION
		if (strTabla.equals("M_CAT_OPERFIL"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "CG_ROLE_OPCION ";
			strQuery += "( R_NOMBRE, ID_OPCION) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";

		}

		//Catalogo CG_USUARIO_COBERTURA
		if (strTabla.equals("M_CAT_UCOBERTURA"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "CG_USUARIO_COBERTURA ";
			strQuery += "( U_LOGIN, ID_COBERTURA, ID_TABLA, ID_PRODUCTO) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";

		}

		//Catalogo CG_CAT_EMPLEADO
		if (strTabla.equals("M_CG_CAT_EMPLEADO"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "cg_cat_empleado ";
			//strQuery += "(id_empleado, ";
			strQuery += "( ce_nombre_completo, ";
			strQuery += "ce_ap_paterno, ";
			strQuery += "ce_ap_materno, ";
			strQuery += "ce_os_responsable, ";
			strQuery += "salutacion, ";
			strQuery += "id_area, ";
			strQuery += "id_puesto, ";
			strQuery += "cargo) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}

		//Catalogo CG_CAT_EMPLEADO
		if (strTabla.equals("M_VIMX_USUARIO"))
		{
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT U_LOGIN FROM VIMX_USUARIO where U_LOGIN="+strParamSplit[0];
			
			/*
			strParamQ = ObjC.ArrCatalogos(strQuery);
			
			
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Id de usuario ya existe.");
			}
			*/
			
			strQuery =  "INSERT INTO               ";
			strQuery += "       VIMX_USUARIO       ";
			strQuery += "(	    U_LOGIN            ";
			strQuery += ",	    cNumeroEmpleado    ";
			strQuery += ",	    cRFC    		   ";
			strQuery += ",      CE_AP_PATERNO      ";
			strQuery += ",      CE_AP_MATERNO      ";
			strQuery += ",      CE_NOMBRE_COMPLETO ";
			//strQuery += ",      G_NOMBRE           ";
			strQuery += ",      CARGO              ";
			strQuery += ",      ID_AREA            ";
			strQuery += ",      U_DESCRIPCION      ";
			strQuery += ",      U_EMAIL            ";
			strQuery += ",      ID_EMPLEADO        ";
			strQuery += ",      SALUTACION         ";
			strQuery += ",      ID_PUESTO          ";
			strQuery += ",      id_cobertura       ";
			strQuery += ",      R_NOMBRE           ";
			strQuery += ",      U_ESTATUS         ";
			strQuery += ",      U_PASSWORD )          ";

			////System.out.println(strParam.toUpperCase());
			int idx = strParam.lastIndexOf(",")-1;
			basesDatos = strParam.substring(idx+2).replaceAll("'", "").trim();
		
			System.out.println("strParam : "+strParam);
			
			
			strParam = strParam.substring(0,idx);

			////System.out.println(strParam.toUpperCase());
			
			strParam+="  ,'1'";

			//strParam+="  ,'1' ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";//Ethiel, es importante no agregar campos aqui, los quite y los puese en la linea de arriba
			
			System.out.println("strParam : "+strQuery);
			
			strModulo="Usuarios";
			blnCatUsu = true;
		}

		//Catalogo CG_SUPLANTACION
		if (strTabla.equals("M_CG_SUPLANTACION"))
		{
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT SUP_LOGIN_A_SUPLANTAR FROM CG_SUPLANTACION where SUP_LOGIN_A_SUPLANTAR="+strParamSplit[0]+" and SUP_LOGIN_QUIEN_SUPLANTA=" +strParamSplit[1];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("registro ya existe.");
			}

			strQuery = "INSERT INTO ";
			strQuery += "CG_SUPLANTACION ";
			strQuery += "(SUP_LOGIN_A_SUPLANTAR, SUP_LOGIN_QUIEN_SUPLANTA) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Cuentas espejo";
		}


		//Catalogo CAT_GRUPO
		//DG
		if (strTabla.equals("M_CG_GRUPO"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT g_nombre FROM cg_grupo where g_nombre="+strParamSplit[0].toUpperCase()+" and g_descripcion="+strParamSplit[1].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
			
				throw new ServletException("El nombre de grupo ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery = "INSERT INTO ";
			strQuery += "cg_grupo ";
			//strQuery += "(id_puesto, ";
			strQuery += "(g_nombre, g_descripcion)";
			
			int idx = strParam.lastIndexOf(",")-1;
			basesDatos = strParam.substring(idx+2).replaceAll("'", "").trim();
		
			System.out.println("strParam : "+strParam);
			
			
			strParam = strParam.substring(0,idx);
			
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			
			strModulo="Grupos";
			blnCatUsu = true;
		}

		//Insert Catalogo Categorias
		if (strTabla.equals("M_CG_CATEGORIA"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT valor FROM MXCATEGORIA where valor="+strParamSplit[0].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El nombre de la categoría ya existe.");
			}


			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO mxcategoria (VALOR) VALUES ("+strParamSplit[0].toUpperCase()+")";
			strModulo="Categoria";
		}
		//Catalogo CAT_ROLE
		//DG
		if (strTabla.equals("M_CG_ROLES"))
		{
		
			strParam = strParam.trim();
			
			strParamSplit = strParam.split(",");
			
			int idx = strParam.lastIndexOf(",")-1;
			basesDatos = strParam.substring(idx+2).replaceAll("'", "").trim();
			blnCatUsu = true;
			
			
	
			strQuery =  " SELECT r_nombre FROM cg_role where r_nombre="+strParamSplit[0].toUpperCase()+" and r_descripcion="+strParamSplit[1].toUpperCase();
			/*
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El nombre de rol ya existe.");
			}
			*/
			////System.out.println(strParam.toUpperCase());
			strQuery = "INSERT INTO ";
			strQuery += "cg_role ";
			//strQuery += "(id_puesto, ";
			strQuery += "(r_nombre, r_descripcion, admin_dueno)";
			strQuery += "VALUES ("+ strParamSplit[0].toUpperCase() +"," +strParamSplit[1].toUpperCase()+","+ strParamSplit[2].toUpperCase() +")";
			strModulo="Roles";
			
		}

		//Relacion rol usuario
		//DG
		if (strTabla.equals("M_CG_ROL_USUARIO"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT r_nombre FROM cg_usuario_role where u_login="+strParamSplit[0].toUpperCase()+" and r_nombre="+strParamSplit[1].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("La relación usuario rol ya existe.");
			}
			strQuery =  " SELECT u_login FROM cg_usuario where u_login="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length ==0)
			{
				throw new ServletException("El usuario no existe.");
			}
			strQuery =  " SELECT r_nombre FROM cg_role where r_nombre="+strParamSplit[1].toUpperCase();
			////System.out.println(strQuery);
			
			/*
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length ==0)
			{
				throw new ServletException("El nombre del rol no existe.");
			}
			*/

			
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println("------>>>"+strParam.toUpperCase());
			int idx = strParam.lastIndexOf(",")-1;
			basesDatos = strParam.substring(idx+2).replaceAll("'", "").trim();
		
			blnCatUsu = true;
			strQuery = "INSERT INTO ";
			strQuery += "cg_usuario_role ";
			//strQuery += "(id_puesto, ";
			strQuery += "(u_login, r_nombre)";
			strQuery += "VALUES ("+ strParamSplit[0].toUpperCase() +"," +strParamSplit[1].toUpperCase()+")";
			strModulo="Usuario Roles";
			
			
			
			
			
		}
		//Relacion grupo usuario
		//DG
		if (strTabla.equals("M_CG_GRUPO_USUARIO"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT g_nombre FROM cg_usuario_grupo where u_login="+strParamSplit[0].toUpperCase()+" and g_nombre="+strParamSplit[1].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("La relación usuario grupo ya existe.");
			}
			strQuery =  " SELECT u_login FROM cg_usuario where u_login="+strParamSplit[0];
			System.out.println(strQuery);
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length ==0)
			{
				throw new ServletException("El usuario no existe.");
			}
			strQuery =  " SELECT g_nombre FROM cg_grupo where g_nombre="+strParamSplit[1].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);

			strParam = strParam.trim();
			
			strParamSplit = strParam.split(",");
			////System.out.println("------>>>"+strParam.toUpperCase());
			int idx = strParam.lastIndexOf(",")-1;
			basesDatos = strParam.substring(idx+2).replaceAll("'", "").trim();
		
			blnCatUsu = true;
			
			strQuery = "INSERT INTO ";
			strQuery += "cg_usuario_grupo ";
			//strQuery += "(id_puesto, ";
			strQuery += " (u_login, g_nombre) ";
			strQuery += "VALUES ("+ strParamSplit[0].toUpperCase() +"," +strParamSplit[1].toUpperCase()+")";
			System.out.println(strQuery);
			strModulo="Usuario Grupos";
			
		}


		if (strTabla.equals("M_TCATALOGOROLEOPCION")){
		    strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO cg_role_opcion(r_nombre,id_opcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo Role Opcion";
		}
		
		//gerencia de ventas
		if (strTabla.equals("M_CG_GERENCIA_VENTAS"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT consecutivo FROM mxgerencia_ventas where consecutivo="+strParamSplit[0].toUpperCase()+" and valor="+strParamSplit[1].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("Ya existe la gerencia de ventas");
			}
			strQuery =  " SELECT consecutivo FROM mxgerencia_ventas where consecutivo="+strParamSplit[0].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length >0)
			{
				throw new ServletException("El id de la gerencia ya existe.");
			}
			strQuery =  " SELECT valor FROM mxgerencia_ventas where valor="+strParamSplit[1].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length >0)
			{
				throw new ServletException("El nombre de la gerencia ya existe.");
			}


			////System.out.println(strParam.toUpperCase());
			strQuery = "INSERT INTO ";
			strQuery += "mxgerencia_ventas ";
			//strQuery += "(id_puesto, ";
			strQuery += "(consecutivo, valor)";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Gerencia Ventas";
		}
		//Insert Catalogo Empaques
		if (strTabla.equals("M_CG_EMPAQUES"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT valor FROM MXEMPAQUE where valor="+strParamSplit[0].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El nombre del empaque ya existe.");
			}


			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO mxempaque (VALOR) VALUES (" +strParamSplit[0].toUpperCase()+")";
			strModulo="Empaque";
		}
		//Insert Catalogo Estatus Producto
		if (strTabla.equals("M_CG_ESTATUS_PRODUCTO"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT valor FROM MXESTATUS_PRODUCTO where valor="+strParamSplit[0].toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El nombre del estatus producto ya existe.");
			}


			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO mxestatus_producto (VALOR) VALUES (" +strParamSplit[0].toUpperCase()+")";
			strModulo="Estatus Producto";
		}

		//Insert Catalogo Usuario Propiedad
		if (strTabla.equals("M_CG_USUARIO_PROPIEDADES"))
		{
			strParam = strParam.trim();
			
			strParamSplit = strParam.split(",");
			////System.out.println("------>>>"+strParam.toUpperCase());
					int idx = strParam.lastIndexOf(",")-1;
			basesDatos = strParam.substring(idx+2).replaceAll("'", "").trim();
		
			
			strQuery = " INSERT INTO cg_usuario_propiedades (u_login,up_nombre,up_valor, ADMIN_DUENO) VALUES ("+strParamSplit[0].toUpperCase()+","+strParamSplit[1].toUpperCase()+","+strParamSplit[2].toUpperCase()+","+strParamSplit[3].toUpperCase()+")";
			strModulo="Usuario Propiedades";
			blnCatUsu = true;
			
		}
		
		if (strTabla.equals("M_TCATJUNTASCONCILIACIONARBITRAJE"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatJuntasConciliacionArbitraje( cNombreJunta ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Juntas Juridicas";
	 	}
		
		if (strTabla.equals("M_TCATALOGOOLI"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO cnsOLIS( idOLIOf, idProyecto, idCvePres, idEstado, Monto, idUAN, idUAE, lHabilitada ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="OLIs";
	 	}
		if (strTabla.equals("M_TSUBCUENTASFFM"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tSubcuentasFFM( cSubcuenta, cDescripcion ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Subcuenta FFM";
	 	}
		
		// Seccion para catalogos Presupuestales
		if (strTabla.equals("M_TRAMO"))
		{
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tRamo(cRamo,dRamo) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Ramo";
	 	}
		if (strTabla.equals("M_TCATALOGOUNIDADRESPONSABLE")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoUnidadResponsable(cUnidadResponsable, ID_AREA, cramo)";
			strQuery += "VALUES ("+ strParam.toUpperCase() + ", 16" +")";
			strModulo="Unidad Responsable";
	 	}
		if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			////System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoGrupoFuncional(cGrupoFuncional,dGrupoFuncional) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Grupo Funcional";
			
	 	}
	 	if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoGpoFuncionalAnteproyecto(cGrupoFuncional,dGrupoFuncional) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Grupo Funcional Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOFUNCION")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoFuncion(cGrupoFuncional,cFuncion,dFuncion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Funcion";
	 	}
	 	if (strTabla.equals("M_TCATALOGOFUNCION_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoFuncionAnteproyecto(cGrupoFuncional,cFuncion,dFuncion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Funcion Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOSUBFUNCION")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoSubFuncion(cGrupoFuncional,cFuncion,cSubFuncion,dSubFuncion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Sub Funcion";
		}
		if (strTabla.equals("M_TCATALOGOSUBFUNCION_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoSubFuncionAnteproyecto(cGrupoFuncional,cFuncion,cSubFuncion,dSubFuncion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Sub Funcion Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoProgramaGeneral(cProgramaGeneral,dProgramaGeneral) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Programa General";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoProgGralAnteproyecto(cProgramaGeneral,dProgramaGeneral) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Programa General Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoActividadInstitucional(cActividadInstitucional,dActividadInstitucional) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Actividad Institucional";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoAIAnteproyecto(cActividadInstitucional,dActividadInstitucional) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Actividad Institucional Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoProgramaPresupuestario(cProgramaPresupuestario,dProgramaPresupuestario) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Programa Presupuestario";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoProgramaPresupuestarioAnteproyecto(cProgramaPresupuestario,dProgramaPresupuestario) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Programa Presupuestario Anteproyecto";
	 	}
	 	if (strTabla.equals("M_TUNIDADRESPONSABLE")){
	 		strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO TCATUNIDADRESPONSABLE(cUnidadResponsable, D_DESCRIPCION, nAlcance) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Unidad Responsable";
		}
		if (strTabla.equals("M_TUNIDADRESPONSABLE_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO TCATUNIDADRESPONSABLEANTEPROYECTO(cUnidadResponsable, D_DESCRIPCION, nAlcance) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Unidad Responsable Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOCAUSAAVISO_REIN")){
		    strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoCausaAviso(aCausaAviso,cCausaAviso,dCausaAviso,eCausaAviso,fCausaAviso) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo Causa Aviso Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOTIPOCAUSAAVISO_REIN")){
		    strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoTipoCausaAviso(cTipoCausaAviso,dTipoCausaAviso) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo Tipo Causa Aviso Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOFORMAPAGO_REIN")){
		    strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoTipoCausaAviso(cFormaPago,dFormaPago) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo Forma Pago Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOMOVIMIENTO_REIN")){
		    strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoMvto(mvto,activo,trans,tipo,grupo,subgrupo,transaccion,descripcion) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo Movimientos Reintegro";
		}
		if (strTabla.equals("TCATALOGOMOVIMIENTO_RECT")){
		    strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoTipoMovimiento (cTipoTRAN, cTipoMOVTO, dTipoMOVTO)";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo Movimientos de Rectificacion";
		}
		if (strTabla.equals("M_TCATALOGOPARTIDA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoPartida(cPartida,dPartida) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Partida";
		}
		if (strTabla.equals("M_TCATALOGOPARTIDA_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoPartidaAnteproyecto(cPartida,dPartida) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Partida Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOTIPOGASTO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoTipoGasto(cTipoGasto,dTipoGasto) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Tipo Gasto";
		}
		
		if (strTabla.equals("M_TCATALOGOTIPOGASTO_CONAC")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery = " INSERT INTO tCatalogoTipoGastoCONAC(cTipoGasto,cClave,dTipoGasto) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Tipo Gasto";
		}
		
		
		if (strTabla.equals("M_TCATALOGOTIPOGASTO_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoTipoGastoAnteproyecto(cTipoGasto,dTipoGasto) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Tipo Gasto Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoFuenteFinanciamiento(cFuenteFinanciamiento,dFuenteFinanciamiento) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Fuente Financiamiento";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTOCONAC")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoFuenteFinanciamientoConac(cFuenteFinanciamiento, cClave, cSubclave, dFuenteFinanciamiento) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Fuente Financiamiento Conac";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoFuenteFinanciamientoAnteproyecto(cFuenteFinanciamiento,dFuenteFinanciamiento) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Fuente Financiamiento Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOENTIDADFEDERATIVA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoEntidadFederativa(cEntidadFederativa,dEntidadFederativa,dEntidadFederativaCorto) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Entidad Federativa";
		}
		if (strTabla.equals("M_TCATALOGOCARTERA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoCartera(cCartera,dCartera, esMeta) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Cartera";
		}
		if (strTabla.equals("M_TCATALOGOCARTERA_ANT")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoCarteraAnteproyecto(cCartera,dCartera) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Cartera Anteproyecto";
	 	}
	 	if (strTabla.equals("TCATALOGOTIPOCLC")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoTipoCLC(cTipoCLC,dTipoCLC) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Tipo CLC";
	 	}
		if (strTabla.equals("M_TEJERCICIOFISCAL")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tEjercicioFiscal(aEjercicioFiscal) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Ejercicio Fiscal";
	 	}
		if (strTabla.equals("M_TPAQUETESCOMISION")){
			strParam = strParam.trim();						
 			strQuery = "INSERT INTO tCatPaquetesComision (nIdPaquete, cNombre, cDescripcion, nPorcentaje, nActivo) VALUES (" + strParam.toUpperCase() + ")";			
			System.out.println(strQuery);
	 	}
		if (strTabla.equals("M_RGINTEGRACION"))
		{
			strQuery = "INSERT INTO ";
			strQuery += "tRelacionGastosIntegracion ";
			strQuery += "(strUnidadEjecutora,intFolio,strRFC,mMonto,strCuentaBancaria,dtFechaProgramada ";
			strQuery += ",intIdLeyenda,strCxP,strFolioInterno,intStatusRelacionGastos ) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
		}
		if (strTabla.equals("M_TCUENTAS")){
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT nCuenta FROM tCuentas where nCuenta="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El numero de Cuenta ya existe.");
			}

			strQuery =  "INSERT INTO               ";
			strQuery += "       tCuentas       ";
			strQuery += "(	    nCuenta            ";
			strQuery += ",      dCuenta      ";
			strQuery += ",      TipoCuenta      ";
			strQuery += ",      nCuentaPadre ";
			strQuery += ",      TipoBalance              ";
			strQuery += ",      VerificaSaldo            ";
			strQuery += ",      NaturalezaCuenta      ";
			strQuery += ",      NivelCuenta            ";
			strQuery += ",      AplicacionCuenta        ";
			strQuery += ",      cSubcuenta         ";
			strQuery += ",      nCuentaLike          ";
			strQuery += ",      cuentaBloqueada)          ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";//Ethiel, es importante no agregar campos aqui, los quite y los puese en la linea de arriba
			strModulo="Cuentas";
		}
		if (strTabla.equals("M_TTIPOSUBCUENTA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tTipoSubcuenta(cSubcuenta) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Tipo de Sub Cuenta";
	 	}
		if (strTabla.equals("M_TTIPOSUBCUENTACONF")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tTipoSubcuentaConf(cSubcuenta,aEjercicioFiscal,nOrden, Interno, NombreCatalogo, NombreCampo) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Configuración de Sub Cuenta";
	 	}

		if (strTabla.equals("M_TVALIDAPNRGP")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery = " INSERT INTO tValidaPNRGP(iId,nNivel,cTipoGasto,cPartida,iNumeralID,cMensaje) ";
			strQuery += "VALUES ( (SELECT MAX(iId) + 1 FROM dbo.tValidaPNRGP), "+ strParam.toUpperCase() +")";
			strModulo="Normatividad de Partidas Restringidas";
			System.out.println(" " + strQuery );
	 	}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_FUNCIONAL")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery = " INSERT INTO tvalidacion_adecuacion_funcional (reduce,amplia,nivel) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Validacion de adecuaciones funcionales";
	 	}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_PROGRAMATICA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery = " INSERT INTO tvalidacion_adecuacion_programatica (reduce,amplia,nivel) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Validacion de adecuaciones programatica";
	 	}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_ECONOMICA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery = " INSERT INTO tvalidacion_adecuacion_economica (reduce,amplia,nivel) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Validacion de adecuaciones economica";
	 	}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_CLAVE_GRUPO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery = " INSERT INTO tvalidacion_adecuacion_clave_grupo (tipo, clave, grupo) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Validacion de adecuaciones clave Grupo";
	 	}

	 	if (strTabla.equals("M_TCATALOGOCOMPLEJIDADFF")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
	 		strQuery = "  INSERT INTO tCatalogoComplejidadFF (cReglasFF, cFuenteFinanciamientoOrigen, cFuenteFinanciamientoDestino, nNivelReglaFF) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Complejidad de Fuente de Financiamiento";
	 	}

	 	if (strTabla.equals("TDOCUMENTACIONCOMPROBATORIADETCREATE")){
	 		strQuery = "  insert into tDocumentacionComprobatoriaDet (DCD_CTOEXT, DCD_VALOR, DCD_FECHA_RECEPCION,id_ramo,nConsecutivo,caNoContrarrecibo,cEjercicio,cIdEntidadContable,DCD_FACTURA,DCD_FECHA_FACTURA,DCD_TBEN,DCD_CBEN,DCD_TIPO_OPE,DCD_TIVA,DCD_IMP_BRUTO,DCD_IVADES,DCD_IVA,DCD_ISR,DCD_MIL5,DCD_MIL2,DCD_CONTRIBUCION,DCD_OTRAS_RET,DCD_PENALIZACION,DCD_CONCEPTO)  ";
			strQuery += " VALUES ('0', 0, "+ strParam.toUpperCase() +")";
	 	}
	 	
	 	if (strTabla.equals("FEDERALIZADOSLAYOUTAUT")){
	 		strQuery = " INSERT INTO tFederalizadoLayoutAut (nFolioPagoFederalizado, dCuentaBancaria, idLeyenda, fProgramada) ";
			strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("TPAGODIRECTOENCABEZADOCREATE")){
	 		strQuery = " insert into tPagoDirectoEncabezado (nFolioPagoDirecto,fCarga,fAplicacion,cRamo,cUnidadResponsable,cEjercicio,cIdEntidadContable,cIdDocumento,cIdTipoDocumento,cIdTipoMontoDesembolso,cIdRFC,fRecepcion,fRevision,fProgramadaPago,cConcepto,fVigenciaIVA,nPorcIVA,mImporteBruto,mImporteIVA,mImporteRetencion,mImporteNeto,cIdUnidadAdministrativa,cIdGRegional,cIdGEstatal,cIdDistritoRiego,cIdTipoPagoDirecto,cIdTipoFondo,caNoContrarrecibo,caNoAP,cIdEstadoPagoDirecto,lContrarreciboImpreso,cReferenciaPRODDER,nIdConcepto,nPorcImpuestoCedular,lAplicaImpuestoCedular,cIdTipoLimiteDlls,nTipoCambio,cOficioDiferenciaCambiaria,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRevision,cIdUsuarioAprobacion,cIdUsuarioRechazo,ID_DESTINO_GASTO,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO)   ";
			strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
		if ("M_TVALIDAINVERCIONAGASTOCORRIENTE".equals(strTabla)){
			strQuery = " INSERT INTO tValidaInvercionAGastoCorriente (cCapituloOri,cTipoGastoOri,cCapituloDest,cTipoGastoDest,iNivel,cMensaje) VALUES ("+ strParam.toUpperCase()  +")";
			strModulo="Normatividad InvercionAGastoCorriente";
		}
	 	if (strTabla.equals("TTECHOUNORMATIVA")){
	 		strQuery = " insert into tTechoUNormativa (cUnidadNormativa, mTechoMonto)    ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("TTECHOUEJECUTORA")){
	 		strQuery = " insert into tTechoUEjecutora (cUnidadResponsable, mTechoMonto)  ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("TTECHOSPARTIDA")){
	 		strQuery = " insert into tTechosPartida (cTipoGasto, cPartida, mTechoPartida) ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("TTECHOPROGRAMAPRESUPUESTARIO")){
	 		strQuery = " insert into tTechoProgramaPresupuestario (cProgramaPresupuestario, mTechoMonto) ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("TTECHOENTIDADFEDERATIVA")){
	 		strQuery = " insert into tTechoEntidadFederativa (cEntidadFederativa, mMontoTecho) ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}

	 	//SAICYS
	 	if (strTabla.equals("M_MCATALOGOUNIDADMEDIDA")){
	 		strQuery = " insert into mCatalogoUnidadMedida (cIdUnidadMedida, cUnidadMedida, cIdClave)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";

	 	}
	 	
	 	// Aqui comienza cambios para catalogos en desembolsos
	 	if (strTabla.equals("D_CATAGENTEFINANCIERO")){
	 		strQuery = " insert into dCat_Agente_Financiero ( AgenteFinancieroCorto, AgenteFinancieroLargo)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("D_CATOFI")){
	 		strQuery = " insert into dCat_OFI ( OFICorto, OFILargo)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("D_CATAREAEXECUTORAEXT")){
	 		strQuery = " insert into dCat_Entidad_Ejec_Resp ( EntidadEjecRespCorto, EntidadEjecRespLargo)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("D_DCATTIPOCAMBIO")){
	 		strQuery = " insert into dCat_Tipo_Cambio ( Fecha, TipoCambio, ID_AgenteFinanciero)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("DPRESTAMOPROGRAMAPRESUPUESTARIO")){
	 		strQuery = " INSERT INTO dPrestamoProgramaPresupuestario ( id_prestamo, cProgramaPresupuestario)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}

        // Aqui Termina cambios para catalogos en desembolsos
        
	 	if (strTabla.equals("MCATALOGOPROVEEDOR")){
	 		strQuery = " insert into mCatalogoProveedor (cTipoPersona,cIdRFC,cRazonSocial,cRepresentante,cGiro,cNumeroRegistro,nIdPyme,cIdEntidadFederativa,cMunicipio,cCalle,cNumeroExterno,cNumeroInterno,cColonia,cCodigoPostal,cUrl,cEmail)   ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
		}
		if (strTabla.equals("M_CG_GRUPO_PROPIEDADES")){
			strQuery = " insert into CG_GRUPO_PROPIEDADES (G_NOMBRE,GP_NOMBRE,gp_descripcion,gp_valor_permitido,GP_VALOR)"
				+ " VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Parametros Sistema";
		}
		if (strTabla.equals("M_TCATALOGOESTPROGAUT")){
			strQuery = " insert into TCATALOGOESTPROGAUT (aEjercicioFiscal, cRamo, cUnidadResponsable, cGrupoFuncional, "
				+ " cFuncion,cSubFuncion, cProgramaGeneral, cActividadInstitucional,"
				+ " cProgramaPresupuestario, cModalidad, cPrograma)"
				+ " VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Normatividad Estructura Programatica";
		}
		
		if (strTabla.equals("M_TCATALOGORAMOOGTO")){
			strQuery = " insert into TCATALOGORAMOOGTO (cRamo,cFuenteFinanciamiento,cPartida,cTipoGasto,aEjercicioFiscal)"
				+ " VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Normatividad financiamiento, partida y objeto del gasto";
		}

		if (strTabla.equals("M_TCATALOGOPARTIDAVALIDAPP")){
			strQuery = " insert into TCATALOGOPARTIDAVALIDAPP (cRamo,cPartida,cModalidad,cPrograma,cProgramaPresupuestario,aEjercicioFiscal)"
				+ " VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Normatividad relacion Partida y Prog Presupuestal";
		}
		
	 	if (strTabla.equals("M_MCATALOGOFIRMANTES")){
	 		String wh []=strParam.split(",");
	 		String str_firmante = "";
	 		AuditoriaBusinessLogic abl =new AuditoriaBusinessLogic("jdbc/gestion");
			Connection conn=abl.getConnection();
			PreparedStatement pstmnt = conn.prepareStatement("Select ISNULL(MAX(nIdFirmante)+1,1) as nIdFirmante from mCatalogoFirmantes where cIdUnidadEjecutora =" + wh[0]);
			ResultSet rs = pstmnt.executeQuery();
					while (rs.next()) {
						str_firmante = "'"+rs.getString("nIdFirmante")+"'";
					}
			rs.close();
			pstmnt.close();
			pstmnt = null;
			conn.close();
			strParam = wh[0]+","+wh[1]+","+wh[2]+","+wh[3]+","+wh[4]+","+wh[5]+","+str_firmante+","+wh[7];
			//strParam+= str_firmante;

	 		strQuery = " insert into mCatalogoFirmantes (cIdUnidadEjecutora,cNombre,cPaterno,cMaterno,cPuesto,lHabilitado,nIdFirmante,nNumeroEmpleado) ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}
	 	if (strTabla.equals("M_MCATALOGOFIRMANTES_USUARIO")){
	 		String wh []=strParam.split(",");
	 		String str_firmante = "";
	 		AuditoriaBusinessLogic abl =new AuditoriaBusinessLogic("jdbc/gestion");
			Connection conn=abl.getConnection();
			PreparedStatement pstmnt = conn.prepareStatement("Select ISNULL(MAX(nIdFirmante)+1,1) as nIdFirmante from mCatalogoFirmantes where cIdUnidadEjecutora =" + wh[0]);
			ResultSet rs = pstmnt.executeQuery();
					while (rs.next()) {
						str_firmante = "'"+rs.getString("nIdFirmante")+"'";
					}
			rs.close();
			pstmnt.close();
			pstmnt = null;
			conn.close();
			strParam = wh[0]+","+wh[1]+","+wh[2]+","+wh[3]+","+wh[4]+","+wh[5]+","+str_firmante+","+wh[7];
			//strParam+= str_firmante;
	 		strQuery = " insert into mCatalogoFirmantes (cIdUnidadEjecutora,cNombre,cPaterno,cMaterno,cPuesto,lHabilitado,nIdFirmante,nNumeroEmpleado) ";
	 		strQuery += " VALUES ("+ strParam.toUpperCase() +")";
	 	}


	 	if (strTabla.equals("CATALOGOCABM")){

	 		String wh []=strParam.split(",");/*
	 		//0 cucop
	 		//1Capitulo
	 		wh[1]="'"+wh[1].trim().substring(1,2)+"'";
	 		//2cIdSubPartida
	 		int val=wh[2].indexOf("-");
	 		wh[2]=wh[2].substring(0,val) +"'";
			//3 CCAOP:
	 		wh[3] = "'0000'";
	 		//4 unidad
	 		//5 descripcion
	 		//6 procedimiento
	 		val=wh[6].indexOf("-");
	 		wh[6]=wh[6].substring(0,val) +"'";

	 		strParam="";
	 		for(int i=0;i<wh.length;i++)
	 		{
		 		strParam+= wh[i].toString()+",";
	 		}*/
	 		//strParam+=",'1'";
	 		//System.out.println("**************************************************************************"+strParam);
	 		String strQueryBusca="select *from mCatalogoCABM where cIdCABM="+ wh[0];
			strParamQ = ObjC.ArrCatalogos(strQueryBusca);
			
			if (strParamQ.length != 0) {
				throw new ServletException("El CABM ya existe, insertar otro");
			}
			else{
	 		strQuery = " IF EXISTS( SELECT * FROM mCatalogoCABM WHERE mCatalogoCABM .cIdCABM ="+wh[0]+" AND mCatalogoCABM .cIdSubPartida =" + wh[1] + " ) "+
	 		 " BEGIN UPDATE mCatalogoCABM SET  cCABM = "+wh[5]+" , cIdCCAOP ="+wh[3]+""+
	 		 " WHERE  cIdCABM = "+wh[0]+" AND cIdSubPartida = "+wh[1]+" AND cIdCapitulo = "+wh[2]+" AND cIdUnidadMedida = "+wh[4]+" AND nIdTipoProceso = "+wh[6]+" "+
	 		 " END "+
	 		 " ELSE "+
	 		 " BEGIN INSERT INTO mCatalogoCABM  ( cIdCABM , cIdSubPartida, cIdCapitulo, cIdCCAOP , cIdUnidadMedida , cCABM , nIdTipoProceso, nIdCABMSOP ) VALUES( "+ strParam +" ) END ";
			}
	 	}
	 	if (strTabla.equals("TCREANUEVOPROGRAMAANUAL")){

	 	    long time = System.currentTimeMillis();
           java.sql.Date date = new java.sql.Date(time);
    		String wh []=strParam.split(",");

	 	 strQuery="IF EXISTS( SELECT * FROM mProgramaAnual WHERE mProgramaAnual .cEjercicio ="+ wh[0]+" AND mProgramaAnual .cIdUnidadEjecutora ="+ wh[1]+" )BEGIN UPDATE mProgramaAnual SET cEjercicio ="+ wh[0]+" , cIdUnidadEjecutora ="+ wh[1]+" , cIdEntidadContable ="+ wh[2]+" , fCreacion ="+ date +" , cIdUsuarioCrea ='GREYES' WHERE mProgramaAnual .cEjercicio ="+ wh[0]+" AND mProgramaAnual .cIdUnidadEjecutora ="+ wh[1]+" END ELSE BEGIN INSERT INTO mProgramaAnual  ( cEjercicio , cIdUnidadEjecutora , cIdEntidadContable , cIdUsuarioCrea ,fCreacion  ) VALUES("+ strParam + ",'"+ date +"') END ";

	 	}
	 	//Requisitos para Checklist de Procedimiento
		if (strTabla.equals("M_REQUISITOS_PROCEDIMIENTO")) {
			strQuery = "select MAX(cIdRequisito) from mCatalogoRequisitosProcedimiento";
			strParamQ = ObjC.ArrCatalogos(strQuery);
			Integer cIdRequisito = 1;
			if (strParamQ.length > 0) {
				cIdRequisito = Integer.parseInt(strParamQ[0][0]) +1 ;
			}
			
			strParamSplit = strParam.split(",");
			if (strParamSplit.length > 2){
			 	if (strParamSplit[2].trim().equals("'1'")){
			 		strParamSplit[2] = "'checked=\"checked\"'";
			 	}
			 	else {
			 		strParamSplit[2] = "''";
			 	}
			}
			
			strQuery = "INSERT INTO mCatalogoRequisitosProcedimiento (cIdRequisito, cRequisito, cDescripcion, cRequerido) ";
			strQuery += "VALUES (" + cIdRequisito + "," + strParamSplit[0].toUpperCase() + "," + strParamSplit[1].toUpperCase() + "," + strParamSplit[2] + ")";
			strModulo=" ";
		}
		// Inserta Eventos
		if (strTabla.equals("M_TEVENTO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cEvento, dEvento FROM tEvento where cEvento="+strParamSplit[0];
			strQuery += " and dEvento="+strParamSplit[1];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       tEvento       ";
			strQuery += "(	    cEvento            ";
			strQuery += ",      dEvento)          ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento";
		}
		// Inserta Grupo de Eventos
		if (strTabla.equals("M_TGRUPOEVENTO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT nIdGrupoEvento FROM TGRUPOEVENTO where nIdGrupoEvento="+strParamSplit[0];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Grupo de Evento ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       TGRUPOEVENTO       ";
			strQuery += "(	    nIdGrupoEvento            ";
			strQuery += ",      cNombreGrupo)          ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Grupo Evento";
		}
		// Inserta SubGrupo de Eventos
		if (strTabla.equals("M_TSUBGRUPOEVENTO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT nIdSubGrupoEvento,nIdGrupoEvento FROM TSUBGRUPOEVENTO where nIdSubGrupoEvento="+strParamSplit[0];
			strQuery += " and nIdGrupoEvento="+strParamSplit[1];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El SubGrupo de Evento ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       TSUBGRUPOEVENTO       ";
			strQuery += "(	    nIdSubGrupoEvento     ";
			strQuery += ",      nIdGrupoEvento        ";
			strQuery += ",      cNombreSubGrupo)      ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="SubGrupo Evento";
		}
		// Inserta Evento Relacion
		if (strTabla.equals("M_TEVENTORELACION")){			
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT nIdGrupoEvento, nIdGrupoEvento, nIdSubGrupoEvento, cEvento, dEvento, cUR FROM tEventoRelacion with (NOLOCK) "; 
			strQuery += " where nIdGrupoEvento="+strParamSplit[0];
			strQuery += " and nIdSubGrupoEvento="+strParamSplit[1];
			strQuery += " and cEvento="+strParamSplit[2];
			strQuery += " and cUR="+strParamSplit[4];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento Relacion ya existe.");
			}

			strQuery =  "INSERT INTO tEventoRelacion (nIdGrupoEvento, nIdSubGrupoEvento, cEvento, dEvento, cUR)";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento Relacion";
			
		}
		// Inserta Evento Manual
		if (strTabla.equals("M_TEVENTOMANUAL")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " select cIdGrupoEvento,	cIdSubGrupoEvento,	cIdEventoManual,	cPartida,	nCuenta,	cEvento,	nDocRenglon,	aEjercicioFiscal from tEventoManual with (NOLOCK) "; 
			strQuery += " where cIdGrupoEvento="+strParamSplit[0];
			strQuery += " and cIdSubGrupoEvento="+strParamSplit[1];
			strQuery += " and cIdEventoManual="+strParamSplit[2];
			strQuery += " and cPartida="+strParamSplit[3];
			strQuery += " and nCuenta="+strParamSplit[4];
			strQuery += " and cEvento="+strParamSplit[5];
			strQuery += " and nDocRenglon="+strParamSplit[6];
			strQuery += " and aEjercicioFiscal="+strParamSplit[7];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento Manual ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       tEventoManual       ";
			strQuery += "(	    cIdGrupoEvento     ";
			strQuery += ",      cIdSubGrupoEvento        ";
			strQuery += ",      cIdEventoManual        ";
			strQuery += ",      cPartida        ";
			strQuery += ",      nCuenta        ";
			strQuery += ",      cEvento        ";
			strQuery += ",      nDocRenglon        ";
			strQuery += ",      aEjercicioFiscal)      ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento Manual";
		}
		// Inserta Evento Concepto
		if (strTabla.equals("M_TEVENTOCONCEPTO")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cTCONC,	cOBGINI,	cCTGA,	cTUNR,	cEVTO,	cDevCTA,	cDevCTA_A,	cDevCTA_RET,	cEjeCTA,	cEjeCTA_A,	cEjeCTA_RET,	ID_DESTINO_GASTO ";
			strQuery += " FROM tEventoConcepto "; 
			strQuery += " where cTCONC="+strParamSplit[0];
			strQuery += " and cOBGINI="+strParamSplit[1];
			strQuery += " and cCTGA="+strParamSplit[2];
			strQuery += " and cTUNR="+strParamSplit[3];
			strQuery += " and cEVTO="+strParamSplit[4];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento Concepto ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       tEventoConcepto       ";
			strQuery += "(	    cTCONC     ";
			strQuery += ",      cOBGINI        ";
			strQuery += ",      cCTGA        ";
			strQuery += ",      cTUNR        ";
			strQuery += ",      cEVTO        ";
			strQuery += ",      cDevCTA        ";
			strQuery += ",      cDevCTA_A        ";
			strQuery += ",      cDevCTA_RET        ";
			strQuery += ",      cEjeCTA        ";
			strQuery += ",      cEjeCTA_A        ";
			strQuery += ",      cEjeCTA_RET        ";
			strQuery += ",      ID_DESTINO_GASTO)      ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento Concepto";
		}
		
		// Inserta Evento Configuracion
		if (strTabla.equals("M_TEVENTOCONFIGURACION")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cEvento,	nCuenta,	aEjercicioFiscal,	nDocRenglon,	TipoCuenta,	dComponente ";
			strQuery += " FROM tEventoConfiguracion "; 
			strQuery += " where cEvento="+strParamSplit[0];
			strQuery += " and nCuenta="+strParamSplit[1];
			strQuery += " and aEjercicioFiscal="+strParamSplit[2];
			strQuery += " and nDocRenglon="+strParamSplit[3];
			strQuery += " and TipoCuenta="+strParamSplit[4];
			strQuery += " and dComponente="+strParamSplit[5];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento Configuracion ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       tEventoConfiguracion       ";
			strQuery += "(	    cEvento     ";
			strQuery += ",      nCuenta        ";
			strQuery += ",      aEjercicioFiscal        ";
			strQuery += ",      nDocRenglon        ";
			strQuery += ",      TipoCuenta        ";
			strQuery += ",      dComponente)      ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento Configuracion";
		}
		// Inserta Evento Configuracion Detalle
		if (strTabla.equals("M_TEVENTOCONFIGURADETALLE")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cEvento,	nCuenta,	aEjercicioFiscal,	nDocRenglon,	nOrden,	dDetalleCuenta ";
			strQuery += " FROM tEventoConfiguraDetalle "; 
			strQuery += " where cEvento="+strParamSplit[0];
			strQuery += " and nCuenta="+strParamSplit[1];
			strQuery += " and aEjercicioFiscal="+strParamSplit[2];
			strQuery += " and nDocRenglon="+strParamSplit[3];
			strQuery += " and nOrden="+strParamSplit[4];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento Configuracion Detalle ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       tEventoConfiguraDetalle       ";
			strQuery += "(	    cEvento     ";
			strQuery += ",      nCuenta        ";
			strQuery += ",      aEjercicioFiscal        ";
			strQuery += ",      nDocRenglon        ";
			strQuery += ",      nOrden        ";
			strQuery += ",      dDetalleCuenta)      ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento Configuracion Detalle";
		}
		// Inserta Evento Partida
		if (strTabla.equals("M_TEVENTOPARTIDA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cEvento,	cPartida ";
			strQuery += " FROM tEventoPartida "; 
			strQuery += " where cEvento="+strParamSplit[0];
			strQuery += " and cPartida="+strParamSplit[1];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Evento Partida ya existe.");
			}

			//System.out.println(strParam.toUpperCase());
			strQuery =  "INSERT INTO               ";
			strQuery += "       tEventoPartida       ";
			strQuery += "(	    cEvento     ";
			strQuery += ",      cPartida)      ";

			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Evento Partida";
		}
		// Inserta Catalogo CABMS
		if (strTabla.equals("M_TCATALOGOCABMSCONTABLE")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
		 	strQuery = "select cc.cCABMS,cc.cCUCOP,cc.cPartida,cc.cDescripcion,cc.nCuenta,cc.nIdUnidadMedida ";
		 	strQuery += " from tCatalogoCABMS cc with(nolock)  ";
			strQuery += " where (cc.cCABMS="+strParamSplit[0];
			strQuery += " or cc.cCUCOP="+strParamSplit[1];
			strQuery += " ) and cc.cPartida="+strParamSplit[2];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El elemento ya existe.");
			}

			//System.out.println(strQuery.toUpperCase());
			strQuery =  "INSERT INTO tCatalogoCABMS ";
			strQuery += "(cCABMS,cCUCOP,cPartida,cDescripcion,nCuenta,nIdUnidadMedida) ";
			strQuery += " VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Catalogo CABMS contable";
			//System.out.println(strQuery.toUpperCase());
		}
		
		//Catalogo Concepto Registro Diario de Bancos
		if (strTabla.equals("TCATALOGOCONCEPTO_RDB"))
		{
			strParam = strParam.trim();
			strQuery =  " SELECT sConcepto FROM tRdbCat_Concepto where sConcepto="+strParam.toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Concepto ya existe.");
			}


			strQuery = "INSERT INTO ";
			strQuery += "tRdbCat_Concepto ";
			//strQuery += "(id_puesto, ";
			strQuery += "(sConcepto) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Registro Diario de Bancos";
			
		}
		if (strTabla.equals("TCATALOGODOCORIGEN_RDB"))
		{
			strParam = strParam.trim();
			strQuery =  " SELECT sDocumentoOrigen FROM tRdbCat_DocumentoOrigen where sDocumentoOrigen="+strParam.toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Documento Origen ya existe.");
			}


			strQuery = "INSERT INTO ";
			strQuery += "tRdbCat_DocumentoOrigen ";
			//strQuery += "(id_puesto, ";
			strQuery += "(sDocumentoOrigen) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Registro Diario de Bancos";
			
		}
		if (strTabla.equals("M_TCATALOGOCRI")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cClaveCRI FROM tCatalogoCRI where cClaveCRI = " + strParamSplit[4];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El numero de CRI ya existe.");
			}

			strQuery =  "INSERT INTO               ";
			strQuery += "       tCatalogoCRI       ";			
			strQuery += "(	    cRubro            ";
			strQuery += ",      cTipo      ";
			strQuery += ",      cClase      ";
			strQuery += ",      cConcepto      ";
			strQuery += ",      cClaveCRI ";
			strQuery += ",      cNombre              ";
			strQuery += ",      cTipoGasto            ";
			strQuery += ",      cFuenteFinanciamiento      ";
			strQuery += ",      cPartida            ";
			strQuery += ",      cCuentaIngreso        ";
			strQuery += ",	    cAplica)            ";
			
			strQuery += "VALUES ("+ strParam +")";//Ethiel, es importante no agregar campos aqui, los quite y los puese en la linea de arriba
			strModulo="Catalogo CRI";
		}
		if (strTabla.equals("TCATALOGOMEDIOPAGO_RDB"))
		{
			strParam = strParam.trim();
			strQuery =  " SELECT sMedioPago FROM tRdbCat_MedioPago where sMedioPago="+strParam.toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Medio Pago ya existe.");
			}

			strQuery = "INSERT INTO ";
			strQuery += "tRdbCat_MedioPago ";
			//strQuery += "(id_puesto, ";
			strQuery += "(sMedioPago) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Registro Diario de Bancos";
			
		}
		if (strTabla.equals("TCATALOGOORIGENPAGO_RDB"))
		{
			strParam = strParam.trim();
			strQuery =  " SELECT sOrigenDeposito FROM tRdbCat_OrigenDeposito where sOrigenDeposito="+strParam.toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("El Origen Deposito Pago ya existe.");
			}

			strQuery = "INSERT INTO ";
			strQuery += "tRdbCat_OrigenDeposito ";
			//strQuery += "(id_puesto, ";
			strQuery += "(sOrigenDeposito) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Registro Diario de Bancos";
			
		}
		//FIN SAICYS
		
		if(  "RFCPagoSinFacturaPermitido".equals( strTabla )  )
		{
			strParam = strParam.trim();
			
			strQuery  = "INSERT INTO tRFCPagoSinFacturaPermitido(cRFC, cTipoPago, lImpuestoForzoso )";
			strQuery += "VALUES( " + strParam.toUpperCase() + ")";
			
			strModulo = "Excepcion de RFC";
		}
	 	if (strTabla.equals("M_TCATCLASIFICACIONADMINISTRATIVA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			strQuery =  " SELECT cClave FROM tCatClasificacionAdministrativa where cClave = " + strParamSplit[4];
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("La clave ya existe.");
			}

			strQuery =  "INSERT INTO               ";
			strQuery += "       tCatClasificacionAdministrativa       ";			
			strQuery += "(	    cClave            ";
			strQuery += "(	    cDescripcion      ";
			strQuery += "(	    cRamo             ";			
			strQuery += ",	    cUR)              ";
			
			strQuery += "VALUES ("+ strParam +")";
			strModulo="Catalogo Clave Adminsitrativa";
		}
	 	
	 	if (strTabla.equals("M_TCATALOGOCLAVEINTERNA")){
			strParam = strParam.trim();
			strParamSplit = strParam.split(",");
			//System.out.println(strParam.toUpperCase());
			strQuery = " INSERT INTO tCatalogoClaveInterna(cClaveInterna) ";
			strQuery += "VALUES ("+ strParam.toUpperCase() +")";
			strModulo="Clave Interna";
		}
	 	
	 	if (strTabla.equals("M_TCATALOGOMETAS"))
		{
	 		strParam = strParam.trim();
	 		String [] strParamSeparados = strParam.split(",");			
			String interna =  strParamSeparados[4].replace(" ","") + "." + strParamSeparados[5].replace(" ","");							
 			strQuery = "INSERT INTO tCatalogoMetas (cMeta, dMeta, cProgramaPresupuestario, cCartera, cUnidadEjecutora, cUnidadNorativa, cClaveInterna) VALUES (" + strParam.toUpperCase() + ",'" + interna.replace("'","") + "')";			
			System.out.println(strQuery);
		}
		 
	 	if(blnCatUsu){	//ACTUALIZACION PARA REQUERIMIENTO UNIFICACION DE USUARIOS
	 		Object[] obj = new Object[5];
	 		obj[0] = strParam;
	 		obj[1] = strModulo;
	 		obj[2] = session;
	 	
// 	 		if (strTabla.equals("M_CG_GRUPO"))  //Se replica el grupo en todas las bases de datos de los ejercicios fiscales
// 	 			basesDatos = "*";
		
			jsonStringOrig = ObjC.InsertMantoCatalogos(strQuery, basesDatos, obj, GestionInterface.OPER_INS);
			jsonString = "[{'Col1':'" + jsonStringOrig + "'}]";
			out.print(jsonString);
		} else{
			jsonStringOrig = ObjC.InsertCatalogos(strQuery);	
			jsonString = "[{'Col1':'" + jsonStringOrig + "'}]";
			out.print(jsonString);

			if (strTabla.equals("M_TCATALOGOEP"))
				strTabla = "TCATALOGOEP";
			

			if(strTabla.startsWith("M_")){
				if(jsonStringOrig.equals("S")){
					//Ethiel, para registrar la operacion en IMX_AUDITORIA
				
					AuditoriaBusinessLogic ABL=new AuditoriaBusinessLogic(jndiName);
					String valores_insert=ABL.getValores_insert(strQuery,strParam);
					Usuario objUsuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
					String usuario= objUsuario.getLogin();
					Empleado emp= (Empleado)session.getAttribute(GestionInterface.ATT_EMPLEADO);
					String area=emp.getClaveArea();
					ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Agregar","",valores_insert,strQuery,"");
				} else{
					AuditoriaBusinessLogic ABL=new AuditoriaBusinessLogic(jndiName);
					String valores_insert="No logró insertar";
					Usuario objUsuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
					String usuario= objUsuario.getLogin();
					Empleado emp= (Empleado)session.getAttribute(GestionInterface.ATT_EMPLEADO);
					String area=emp.getClaveArea();
					ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Agregar","",valores_insert,strQuery,"");	
				}
			}
		
		}
		
	}catch (Exception exc){
		strError = exc.getMessage();
		strError = strError.toUpperCase().trim();

		strError = strError.replaceAll("Á","A");
		strError = strError.replaceAll("É","E");
		strError = strError.replaceAll("Í","I");
		strError = strError.replaceAll("Ó","O");
		strError = strError.replaceAll("Ú","U");

		strError = strError.replaceAll("'","");
		strError = strError.replaceAll(":","");
		strError = strError.replaceAll("\\. "," ");
		strError = strError.replaceAll("\\."," ");
		jsonString = "[{'Col1':'" + strError.trim() + "'}]";
		out.print(jsonString);

			//log(exc.getMessage());
			//throw new ServletException(exc);
	}


%>
