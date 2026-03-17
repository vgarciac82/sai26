<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%!private static final Logger log = LogManager.getLogger("UpdateJSON.jsp"); %>
<%@page language="java" contentType="application/json"%>
<%@page import="com.syc.gestion.documental.CatalogosBusinessLogic"%>
<%@page import="javax.servlet.ServletException"%>
<%@page import="com.syc.auditoria.AuditoriaBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="java.util.ArrayList"%>
<%

	String strQuery = "";
	String strError = "";
	String strTabla = request.getParameter("Tabla").toUpperCase();
	String strModulo =strTabla;//por si no entra a algun if donde se llena con el nombre adecuado
	String strParam = request.getParameter("Param");
	
	String strSetParam = request.getParameter("SetParam");
	String jndiName = "jdbc/gestion";
	String[][] strParamQ = null;
	String[] strParamSplit = null;
	String[] strSubParamSplit = null;
	String strValidador = "";

	boolean blnCatUsu = false;
	String basesDatos = "";
	
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

	strSetParam = strSetParam.replaceAll("Ã","Á");
	strSetParam = strSetParam.replaceAll("Ã¡","Á");
	strSetParam = strSetParam.replaceAll("Ã‰","É");
	strSetParam = strSetParam.replaceAll("Ã©","É");
	strSetParam = strSetParam.replaceAll("Ã","Í");
	strSetParam = strSetParam.replaceAll("Ã­","Í");
	strSetParam = strSetParam.replaceAll("Ã“","Ó");
	strSetParam = strSetParam.replaceAll("Ã³","Ó");
	strSetParam = strSetParam.replaceAll("Ãš","Ú");
	strSetParam = strSetParam.replaceAll("Ãº","Ú");
	strSetParam = strSetParam.replaceAll("Ã‘","Ñ");
	strSetParam = strSetParam.replaceAll("Ã±","Ñ");
	strSetParam = strSetParam.replaceAll("Ãœ","Ü");
	strSetParam = strSetParam.replaceAll("Ã¼","Ü");

	strSetParam = strSetParam.replaceAll("'NULL'","NULL");

	String jsonStringOrig = "";
	String jsonString = "";

	try
	{
		CatalogosBusinessLogic ObjC = new CatalogosBusinessLogic(jndiName);

		if( "tCFDI_Cat_FolioSerie".equalsIgnoreCase(  strTabla ) ){
			strQuery = "UPDATE	tSerieFolio "
					 + "   SET	" + strSetParam.toUpperCase() + " ";
		}
		//Catalogo tUECuentasBancarias
		if (strTabla.equals("M_TUECUENTASBANCARIAS"))
		{
			strQuery = "UPDATE ";
			strQuery += "tUECuentasBancarias ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="CuentasBancarias";
		}

		//Catalogo CAT_ALMACEN
		if (strTabla.equals("M_CAT_ALMACEN"))
		{
			strQuery = "UPDATE ";
			strQuery += "CAT_ALMACEN ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Almacen";
		}

		//Catalogo CAT_FIRMANTE
		if (strTabla.equals("M_CAT_FIRMANTE"))
		{
			strQuery = "UPDATE ";
			strQuery += "CAT_FIRMANTE ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Firmantes";
		}

		//Catalogo CAT_DET_INSTRUCCION
		if (strTabla.equals("M_CAT_DET_INSTRUCCION"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_det_instruccion ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Detalles de Instruccion";
		}

		//Catalogo CAT_ESTADOS
		if (strTabla.equals("M_CAT_ESTADOS"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_estados ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CAT_LD_USR
		if (strTabla.equals("M_CAT_LD_USR"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_ld_usr ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CAT_LDISTRIBUCION
		if (strTabla.equals("M_CAT_LDISTRIBUCION"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_ldistribucion ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CAT_MUNICIPIO
		if (strTabla.equals("M_CAT_MUNICIPIO"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_municipio ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CAT_PROCEDENCIA
		if (strTabla.equals("M_CAT_PROCEDENCIA"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_procedencia ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Procedencias";
		}

		//Catalogo CG_TIPO_PROCEDENCIA
		if (strTabla.equals("M_CG_TIPO_PROCEDENCIA"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_tipo_procedencia ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CAT_PUESTOS
		if (strTabla.equals("M_CAT_PUESTOS"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_puestos ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Puestos";
		}


		//Catalogo TCAT_MENSAJES
		if (strTabla.equals("TCAT_MENSAJES"))
		{
			strQuery =  " UPDATE ";
			strQuery += " tcat_mensajes WITH(NOLOCK) ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			
		}
		//Catalogo Banco Ambiental
		if (strTabla.equals("TBANCOAMBIENTAL"))
		{
			strQuery =  " UPDATE ";
			strQuery += " tben_bancoambiental WITH(NOLOCK) ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			
		}
		//Catalogo CAT_REM_EXTERNO
		if (strTabla.equals("M_CAT_REM_EXTERNO"))
		{
			strSetParam=strSetParam.toUpperCase();
			strParamSplit = strSetParam.split(",");
			strQuery = "UPDATE ";
			strQuery += "IMXEXPEDIENTES ";
			strQuery += "SET ";
			strSubParamSplit = strParamSplit[0].split("=");
			strQuery += "renombre=" + strSubParamSplit[1];
			strSubParamSplit = strParamSplit[4].split("=");
			strQuery += ",recargo=" + strSubParamSplit[1];
			strSubParamSplit = strParamSplit[5].split("=");
			strQuery += ",redireccion=" + strSubParamSplit[1];
			strSubParamSplit = strParamSplit[6].split("=");
			strQuery += ",relocalidad=" + strSubParamSplit[1];
			strQuery += " Where renombre=(select re_nombre from cat_rem_externo where " + strParam + ") ";
			strQuery += "   and recargo=(select re_cargo from cat_rem_externo where " + strParam + ")";
			strQuery += "   and redireccion=(select re_direccion from cat_rem_externo where " + strParam + ")";
			strQuery += "   and relocalidad=(select re_localidad from cat_rem_externo where " + strParam + ") ";


			strQuery += " UPDATE ";
			strQuery += "cat_rem_externo ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Remitentes Externos";
		}

		//Catalogo CAT_TIPO_DOCUMENTO
		if (strTabla.equals("M_CAT_TIPO_DOCUMENTO"))
		{
			strQuery = "UPDATE ";
			strQuery += "cat_tipo_documento ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CG_CAT_AREAS
		if (strTabla.equals("M_CG_CAT_AREAS"))
		{
			strParamSplit = strSetParam.split(",");
			strQuery =  " SELECT PREFIJO_FOLIO,FOLIO_INICIAL FROM cg_cat_areas WHERE " + strParam ;
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				strValidador = "PREFIJO_FOLIO='" + strParamQ[0][0] + "'";
				strQuery = "FOLIO_INICIAL=" + strParamQ[0][1];
				if ( strValidador.equals(strParamSplit[1].trim()))
				{
					throw new ServletException("El folio solo se podra cambiar si cambia el prefijo.");
				}
			}

			strQuery = "UPDATE ";
			strQuery += "cg_cat_areas ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Areas";
		}

		//Catalogo CG_CAT_PRIORIDAD
		if (strTabla.equals("M_CG_CAT_PRIORIDAD"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_cat_prioridad ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CG_CAT_REMITENTE
		if (strTabla.equals("M_CG_CAT_REMITENTE"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_cat_remitente ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CG_CAT_REMITENTE_AREA
		if (strTabla.equals("M_CG_CAT_REMITENTE_AREA"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_cat_remitente_area ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CG_CAT_REMITENTE_PERSONA
		if (strTabla.equals("M_CG_CAT_REMITENTE_PERSONA"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_cat_remitente_persona ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}

		//Catalogo CG_CAT_USUARIOS
		if (strTabla.equals("M_CAT_USUARIOS"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_usuario ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Usuarios";
		}

		//Catalogo VIMX_USUARIO
		if (strTabla.equals("M_VIMX_USUARIO"))
		{
		
			int idx = strParam.lastIndexOf("AND")-1;			
					
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
			strParam = strParam.substring(0,idx);
			
			//basesDatos
			strQuery = "UPDATE TOP (1) ";
			strQuery += "vimx_usuario ";
			strQuery += "SET ";
			strQuery += " "+ strSetParam.toUpperCase() +" ";
			strModulo="Usuarios";
			
				
	 
			blnCatUsu = true;
		}

		//Catalogo CG_CAT_EMPLEADO
		if (strTabla.equals("M_CG_CAT_EMPLEADO"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_cat_empleado ";
			strQuery += "SET ";
			strQuery += " "+ strSetParam.toUpperCase() +" ";
			strModulo="Cat Empleado";
		}

		//Catalogo CG_SUPLANTACION
		if (strTabla.equals("M_CG_SUPLANTACION"))
		{
			strQuery = "UPDATE ";
			strQuery += "CG_SUPLANTACION ";
			strQuery += "SET ";
			strQuery += " "+ strSetParam.toUpperCase() +" ";
			strModulo="Cuentas espejo";
		}

		//DG
		//Catalogo CG_GRUPO
		if (strTabla.equals("M_CG_GRUPO"))
		{
		
			int idx = strParam.lastIndexOf("AND")-1;			
					
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
			strParam = strParam.substring(0,idx);
			
			strQuery = "UPDATE ";
			strQuery += "cg_grupo ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Grupos";
			
			blnCatUsu = true;
		}
		
		//Update Catalogo Categoria
		if (strTabla.equals("M_CG_CATEGORIA"))
		{
		strQuery = "UPDATE ";
			strQuery += " MXCATEGORIA ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Categoria";
		}

				//DG
		//Catalogo CG_ROLES
		if (strTabla.equals("M_CG_ROLES"))
		{
		
									
			int idx = strParam.lastIndexOf("AND")-1;
			
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
				
			strParam = strParam.substring(0,idx);
			blnCatUsu = true;
			strQuery = "UPDATE ";
			strQuery += "cg_role ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Roles";
		}



		//relacuion grupo usuario
		if (strTabla.equals("M_CG_GRUPO_USUARIO"))
		{
		
			int idx = strParam.lastIndexOf("AND")-1;
			
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
				
			strParam = strParam.substring(0,idx);
			blnCatUsu = true;
			
			strQuery = "UPDATE ";
			strQuery += "cg_usuario_grupo ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Usuario Grupos";
		}
		
		
		
		//relacuion rol usuario
		if (strTabla.equals("M_CG_ROL_USUARIO"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_usuario_rol ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Usuario Roles";
		}
		if (strTabla.equals("CATALOGOSALARIO")){
			strQuery = "UPDATE ";
			strQuery += " mCatSalario ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			log.debug("strQuery: "+strQuery);
	 	}
		if (strTabla.equals("M_TCATALOGOROLEOPCION"))
		{
			strQuery = "UPDATE ";
			strQuery += "cg_role_opcion ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Role Opcion";
		}
		
		//gerencia de ventas
		if (strTabla.equals("M_CG_GERENCIA_VENTAS"))
		{
			strQuery = "UPDATE ";
			strQuery += "mxgerencia_ventas ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Gerencia Ventas";
		}
		//empaques
		if (strTabla.equals("M_CG_EMPAQUES"))
		{
			strQuery = "UPDATE ";
			strQuery += "mxempaque ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Empaques";
		}
		//estatus producto
		if (strTabla.equals("M_CG_ESTATUS_PRODUCTO"))
		{
			strQuery = "UPDATE ";
			strQuery += "mxestatus_producto ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Estatus Producto";
		}
		//usuario propiedad
		if (strTabla.equals("M_CG_USUARIO_PROPIEDADES"))
		{
		
							
			int idx = strParam.lastIndexOf("AND")-1;
			
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
				
			strParam = strParam.substring(0,idx);
			blnCatUsu = true;
			strQuery = "UPDATE ";
			strQuery += "cg_usuario_propiedades ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Usuario Propiedades";
		}
		
		//requisitos para el checklist de procedimiento cuando es alcance externo
		if (strTabla.equals("M_REQUISITOS_PROCEDIMIENTO")){
			
			
			strParamSplit = strSetParam.split(",");
			if (strParamSplit.length > 2){
			 	if (strParamSplit[2].trim().equals("cRequerido='1'")){
			 		strParamSplit[2] = "cRequerido='checked=\"checked\"'";
			 	}
			 	else {
			 		strParamSplit[2] = "cRequerido=''";
			 	}
			}
			
			strQuery = "UPDATE mCatalogoRequisitosProcedimiento SET ";
			strQuery += strParamSplit[0] + " , " + strParamSplit[1] + " , " + strParamSplit[2] + " where " + strParam;
			
			
		}
		// Seccion para catalogos Presupuestales
		if (strTabla.equals("M_TRAMO")){
			strQuery = " Update tRamo set " + strSetParam.toUpperCase() + " ";
			strModulo="Ramo";
	 	}
		if (strTabla.equals("M_TCATALOGOUNIDADRESPONSABLE")){
			strQuery = " Update tCatalogoUnidadResponsable set  " + strSetParam.toUpperCase() + " ";
			strModulo="Unidad Responsable";
	 	}
		if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL")){
			strQuery = " Update tCatalogoGrupoFuncional set  " + strSetParam.toUpperCase() + " ";
			strModulo="Grupo Funcional";
	 	}
	 	if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL_ANT")){
			strQuery = " Update tCatalogoGpoFuncionalAnteproyecto set  " + strSetParam.toUpperCase() + " ";
			strModulo="Grupo Funcional Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOFUNCION")){
			strQuery = " Update tCatalogoFuncion set " + strSetParam.toUpperCase() + " ";
			strModulo="Funcion";
	 	}
	 	if (strTabla.equals("M_TCATALOGOFUNCION_ANT")){
			strQuery = " Update tCatalogoFuncionAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="FuncionAnteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOSUBFUNCION")){
			strQuery = " Update tCatalogoSubFuncion set " + strSetParam.toUpperCase() + " ";
			strModulo="Sub Funcion";
		}
		if (strTabla.equals("M_TCATALOGOSUBFUNCION_ANT")){
			strQuery = " Update tCatalogoSubFuncionAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Sub Funcion Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL")){
			strQuery = " Update tCatalogoProgramaGeneral set " + strSetParam.toUpperCase() + " ";
			strModulo="Programa General";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL_ANT")){
			strQuery = " Update tCatalogoProgGralAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Programa General Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL")){
			strQuery = " Update tCatalogoActividadInstitucional set " + strSetParam.toUpperCase() + " ";
			strModulo="Actividad Institucional";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL_ANT")){
			strQuery = " Update tCatalogoAIAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Actividad Institucional Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO")){
			strQuery = " Update tCatalogoProgramaPresupuestario set " + strSetParam.toUpperCase() + " ";
			strModulo="Programa Presupuestario";
		}
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO_ANT")){
			strQuery = " Update tCatalogoProgramaPresupuestarioAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Programa Presupuestario Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOPARTIDA")){
			strQuery = " Update tCatalogoPartida set " + strSetParam.toUpperCase() + " ";
			strModulo="Partida";
		}
		if (strTabla.equals("M_TCATALOGOPARTIDA_ANT")){
			strQuery = " Update tCatalogoPartidaAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Partida Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOTIPOGASTO")){
			strQuery = " Update tCatalogoTipoGasto set " + strSetParam.toUpperCase() + " ";
			strModulo="Tipo Gasto";
		}
		
		if (strTabla.equals("M_TCATALOGOTIPOGASTO_CONAC")){
			strQuery = " Update tCatalogoTipoGastoCONAC set " + strSetParam.toUpperCase() + " ";
			strModulo="Tipo Gasto";
		}
		
		if (strTabla.equals("M_TCATALOGOTIPOGASTO_ANT")){
			strQuery = " Update tCatalogoTipoGastoAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Tipo Gasto Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO")){
			strQuery = " Update tCatalogoFuenteFinanciamiento set " + strSetParam.toUpperCase() + " ";
			strModulo="Fuente Financiamiento";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTOCONAC")){
			strQuery = " Update tCatalogoFuenteFinanciamientoCONAC set " + strSetParam.toUpperCase() + " ";
			strModulo="Fuente Financiamiento CONAC";
		}
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO_ANT")){
			strQuery = " Update tCatalogoFuenteFinanciamientoAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Fuente Financiamiento Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOCAUSAAVISO_REIN")){
			strQuery = " UPDATE tCatalogoCausaAviso set " + strSetParam.toUpperCase() + "  ";
			strModulo="Catalogo Causa Aviso Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOTIPOCAUSAAVISO_REIN")){
			strQuery = " UPDATE tCatalogoTipoCausaAviso set " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo Tipo Causa Aviso Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOFORMAPAGO_REIN")){
			strQuery = " UPDATE tCatalogoFormaPago set " + strSetParam.toUpperCase() + "  ";
			strModulo="Catalogo Forma Pago Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOMOVIMIENTO_REIN")){
			strQuery = " UPDATE tCatalogoMvto set " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo Movimientos Reintegro";
		}
		if (strTabla.equals("TCATALOGOMOVIMIENTO_RECT")){
			strQuery = " UPDATE tCatalogoTipoMovimiento set " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo Movimientos Rectificacion";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL")){
			strQuery = " Update tCatalogoActividadInstitucional set  " + strSetParam.toUpperCase() + " ";
			strModulo="Actividad Institucional";
		}
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL_ANT")){
			strQuery = " Update tCatalogoAIAnteproyecto set  " + strSetParam.toUpperCase() + " ";
			strModulo="Actividad Institucional Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOENTIDADFEDERATIVA")){
			strQuery = " Update tCatalogoEntidadFederativa set  " + strSetParam.toUpperCase() + " ";
			strModulo="Entidad Federativa";
		}
		if (strTabla.equals("M_TCATALOGOCARTERA")){
			strQuery = " Update tCatalogoCartera set " + strSetParam.toUpperCase() + " ";
			strModulo="Cartera";
		}
		if (strTabla.equals("M_TCATALOGOCARTERA_ANT")){
			strQuery = " Update tCatalogoCarteraAnteproyecto set " + strSetParam.toUpperCase() + " ";
			strModulo="Cartera Anteproyecto";
	 	}
	 	if (strTabla.equals("M_TUNIDADRESPONSABLE")){
	 		strQuery = " Update TCATUNIDADRESPONSABLE set " + strSetParam.toUpperCase() + " ";
			strModulo="Unidad Responsable";
		}
		if (strTabla.equals("M_TUNIDADRESPONSABLE_ANT")){
			strQuery = " Update TCATUNIDADRESPONSABLEANTEPROYECTO set " + strSetParam.toUpperCase() + " ";
			strModulo="Unidad Responsable Anteproyecto";
		}
		if (strTabla.equals("M_TEJERCICIOFISCAL")){
			strQuery = " Update tEjercicioFiscal set " + strSetParam.toUpperCase() + " ";
			strModulo="Ejercicio Fiscal";
	 	}
	 	if (strTabla.equals("TCATALOGOTIPOCLC")){
			strQuery = " UPDATE tCatalogoTipoCLC set " + strSetParam.toUpperCase() + " ";
			strModulo="Tipo CLC";
	 	}
	 	
		if (strTabla.equals("M_TCUENTAS")){
			if(strSetParam != null && strSetParam.toUpperCase().indexOf("CSUBCUENTA=''") >= 0 )
				strSetParam = strSetParam.replace("cSubcuenta=''","cSubcuenta=NULL");

			strQuery = " Update tCuentas set " + strSetParam.toUpperCase() + " ";
			strModulo="Cuentas";
	 	}
		if (strTabla.equals("M_TTIPOSUBCUENTA")){
			strQuery = " Update tTipoSubcuenta set " + strSetParam.toUpperCase() + " ";
			strModulo="Tipo de Sub Cuenta";
	 	}
		if (strTabla.equals("M_TTIPOSUBCUENTACONF")){
			strQuery = " Update tTipoSubcuentaConf set " + strSetParam.toUpperCase() + " ";
			strModulo="Configuración de Sub Cuenta";
	 	}

		//if (strTabla.equals("M_TCATALOGORESTRICCIONES")){
		if (strTabla.equals("M_TVALIDAPNRGP")){
			strQuery = " Update tValidaPNRGP set " + strSetParam.toUpperCase() + " ";
			strModulo="Normatividad de Partidas Restringidas";
	 	}
		if (strTabla.equals("M_TCATALOGOCOMPLEJIDADFF")){
			strQuery = " Update tCatalogoComplejidadFF set " + strSetParam.toUpperCase() + " ";
			strModulo="Complejidad de Fuente de Financiamiento";
	 	}
		if ("M_TVALIDAINVERCIONAGASTOCORRIENTE".equals(strTabla)){  // M_TCATALOGOCOMPLEJIDADGF CAMBIA POR M_TVALIDAINVERCIONAGASTOCORRIENTE
			strQuery = "Update tValidaInvercionAGastoCorriente set  "+ strSetParam.toUpperCase()  +" ";
			strModulo="Normatividad InvercionAGastoCorriente";
		}

		//SAI MODULO DE REQUISICIONES BIENES Y SERVICIOS

		if (strTabla.equals("M_MCATALOGOUNIDADMEDIDA")){
			strQuery = " UPDATE mCatalogoUnidadMedida SET " + strSetParam + " ";
			strModulo="Catalogo de Unidades de Medida";
	 	}
        
        // Aqui comienza cambios para catalogos en desembolsos
		if (strTabla.equals("D_CATAGENTEFINANCIERO")){
			strQuery = " UPDATE dCat_Agente_Financiero SET " + strSetParam + " ";
			strModulo="Catalogo de Agentes Financieros";
	 	}
		if (strTabla.equals("D_CATOFI")){
			strQuery = " UPDATE dCat_OFI SET " + strSetParam + " ";
			strModulo="Catalogo de Organismo Financiero Internacional";
	 	}
		if (strTabla.equals("D_CATAREAEXECUTORAEXT")){
			strQuery = " UPDATE dCat_Entidad_Ejec_Resp SET " + strSetParam + " ";
			strModulo="Catalogo de Entidad Ejecutora Externa";
	 	}
		if (strTabla.equals("D_DCATTIPOCAMBIO")){
			strQuery = " UPDATE dCat_Tipo_Cambio SET " + strSetParam + " ";
			strModulo="Catalogo de Tipo de Cambio";
	 	}
		// el Cat UR pertenece al sistema SAI
		//if (strTabla.equals("D_DCATUNIDADRESPONSABLE")){
			//strQuery = " UPDATE dCat_Unidad_Responsable SET " + strSetParam + " ";
			//strModulo="Catalogo de Unidad Responsable";
	 	//}

        // Aqui Termina cambios para catalogos en desembolsos
		
		if (strTabla.equals("M_MCATALOGOSUBPARTIDA")){
			strQuery = " UPDATE mCatalogoSubPartida SET " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo de Sub Partida";
	 	}

		if (strTabla.equals("M_MCATALOGOFIRMANTES")){
			strQuery = " UPDATE mCatalogoFirmantes SET " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo de mCatalogoFirmantes";
	 	}

		if (strTabla.equals("M_MCATALOGOFIRMANTES_USUARIO")){
			strQuery = " UPDATE mCatalogoFirmantes SET " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo de mCatalogoFirmantes";
	 	}

		if (strTabla.equals("CATALOGOCABM")){
			//Esto se hizo por que se quito el cIdCABM como llave
			int i=strSetParam.indexOf(',');
			String strSetParamAux=strSetParam.substring(i+1,strSetParam.length());
			strParam=strSetParam.substring(0,i);
			strSetParam=strSetParamAux;
			
			
			
			//Se busca el cIdCABM si ya existe
			String strQueryBusca="select *from mCatalogoCABM where "+ strParam.toUpperCase();
			strParamQ = ObjC.ArrCatalogos(strQueryBusca);
			
			if (strParamQ.length == 0) {
				throw new ServletException("El CABM no existe, hay que insertarlo para poder modificarlo");
			}
			else
				strQuery = " UPDATE mCatalogoCABM SET " + strSetParam.toUpperCase() + " ";

	 	}

	 	if (strTabla.equals("M_CAT_MESES_CONTABLES")){
			strQuery = " UPDATE tMesesContables SET " + strSetParam.toUpperCase() + " ";
	 	}

	 	if (strTabla.equals("M_TCATALOGOCENTROCONTABLE")){
			strQuery = " UPDATE tCatalogoCentroContable SET " + strSetParam.toUpperCase() + " ";
	 	}

		if (strTabla.equals("TTECHOENTIDADFEDERATIVA"))
		{
			strQuery = "UPDATE ";
			strQuery += "tTechoEntidadFederativa ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Techos Presupuestales por Entidad Federativa";
		}
		if (strTabla.equals("TTECHOPROGRAMAPRESUPUESTARIO"))
		{
			strQuery = "UPDATE ";
			strQuery += "tTechoProgramaPresupuestario ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Techos Presupuestales por Programa Presupuestario";
		}
		if (strTabla.equals("TTECHOSPARTIDA"))
		{
			strQuery = "UPDATE ";
			strQuery += "tTechosPartida ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Techos Presupuestales por Partida.";
		}
		if (strTabla.equals("TTECHOUEJECUTORA"))
		{
			strQuery = "UPDATE ";
			strQuery += "tTechoUEjecutora ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Techos Presupuestales por Unidad Ejecutora.";
		}

		if (strTabla.equals("TTECHOUNORMATIVA"))
		{
			strQuery = "UPDATE ";
			strQuery += "tTechoUNormativa ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Techos Presupuestales por Unidad Normativa.";
		}
		
		if (strTabla.equals("TFEDERALIZADOSLAYOUTAUT"))
		{
			strQuery = "UPDATE ";
			strQuery += "tFederalizadoLayoutAut ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
		}
		
//SAI FIN MODULO DE REQUISICIONES BIENES Y SERVICIOS

		if (strTabla.equals("M_CG_GRUPO_PROPIEDADES"))
		{
			strQuery = "UPDATE ";
			strQuery += "CG_GRUPO_PROPIEDADES ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Parametros Sistema";
		}
		if (strTabla.equals("M_TCATALOGOESTPROGAUT"))
		{
			strQuery = "UPDATE ";
			strQuery += "TCATALOGOESTPROGAUT ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Normatividad Estructura Programatica";
		}

		if (strTabla.equals("M_TCATALOGORAMOOGTO"))
		{
			strQuery = "UPDATE ";
			strQuery += "TCATALOGORAMOOGTO ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Normatividad financiamiento, partida y objeto del gasto";
		}
		
		if (strTabla.equals("M_TCATALOGOPARTIDAVALIDAPP"))
		{
			strQuery = "UPDATE ";
			strQuery += "TCATALOGOPARTIDAVALIDAPP ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Normatividad relacion Partida y Prog Presupuestal";
		}
		// Actualiza Eventos
		if (strTabla.equals("M_TEVENTO")){
			strQuery = "UPDATE ";
			strQuery += "       tEvento       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento";
		}
		// Actualiza Grupo de Eventos
		if (strTabla.equals("M_TGRUPOEVENTO")){
			strQuery = "UPDATE ";
			strQuery += "       TGRUPOEVENTO       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Grupo Evento";
		}
		// Actualiza SubGrupo de Eventos
		if (strTabla.equals("M_TSUBGRUPOEVENTO")){
			strQuery = "UPDATE ";
			strQuery += "       TSUBGRUPOEVENTO       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="SubGrupo Evento";
		}
		// Actualiza Evento Relacion
		if (strTabla.equals("M_TEVENTORELACION")){
			strQuery = "UPDATE ";
			strQuery += "       tEventoRelacion       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento Relacion";
			log.debug(strQuery);
		}
		// Actualiza Evento Manual
		if (strTabla.equals("M_TEVENTOMANUAL")){
			strQuery = "UPDATE ";
			strQuery += "       tEventoManual       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento Manual";
		}
		// Actualiza Evento Concepto
		if (strTabla.equals("M_TEVENTOCONCEPTO")){
			strQuery = "UPDATE ";
			strQuery += "       tEventoConcepto       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento Concepto";
		}
		
		// Actualiza Evento Configuracion
		if (strTabla.equals("M_TEVENTOCONFIGURACION")){
			strQuery = "UPDATE ";
			strQuery += "       tEventoConfiguracion       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento Configuracion";
		}
		// Actualiza Evento Configuracion Detalle
		if (strTabla.equals("M_TEVENTOCONFIGURADETALLE")){
			strQuery = "UPDATE ";
			strQuery += "       tEventoConfiguraDetalle       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento Configuracion Detalle";
		}
		// Actualiza Evento Partida
		if (strTabla.equals("M_TEVENTOPARTIDA")){
			strQuery = "UPDATE ";
			strQuery += "       tEventoPartida       ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Evento Partida";
		}

		// Actualiza Catalogo CABMS Contable
		if (strTabla.equals("M_TCATALOGOCABMSCONTABLE")){
			strQuery = "UPDATE ";
			strQuery += " tCatalogoCABMS  ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Catalogo CABMS Contable";
		}

		//Catalogo Concepto Registro Diario de Bancos
		if (strTabla.equals("TCATALOGOCONCEPTO_RDB"))
		{
			strQuery = "UPDATE ";
			strQuery += "tRdbCat_Concepto ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			log.debug(strQuery);
			strModulo="Registro Diario de Bancos";
		}
		//Catalogo Documento Origen Registro Diario de Bancos
		if (strTabla.equals("TCATALOGODOCORIGEN_RDB"))
		{
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
		
			strQuery = "UPDATE ";
			strQuery += "tRdbCat_DocumentoOrigen ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			log.debug(strQuery);
			strModulo="Registro Diario de Bancos";
		}
		//Catalogo Medio Pago Registro Diario de Bancos
		if (strTabla.equals("TCATALOGOMEDIOPAGO_RDB"))
		{
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
		
			strQuery = "UPDATE ";
			strQuery += "tRdbCat_MedioPago ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			log.debug(strQuery);
			strModulo="Registro Diario de Bancos";
		}
		if (strTabla.equals("TCATALOGOORIGENPAGO_RDB"))
		{
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
		
			strQuery = "UPDATE ";
			strQuery += "tRdbCat_OrigenDeposito ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			log.debug(strQuery);
			strModulo="Registro Diario de Bancos";
		}
		if ("RFCPagoSinFacturaPermitido".equalsIgnoreCase( strTabla ) )
		{
			strQuery = "UPDATE ";
			strQuery += "tRFCPagoSinFacturaPermitido ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Excepcion de RFC";
		}
/////////////////////////////////////// Desembolsos
		if (strTabla.equals("DSOE"))
		{
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
		
			strQuery = "UPDATE ";
			strQuery += "dSOE ";
			strQuery += "SET ";
			strQuery += " " + strSetParam.toUpperCase() + " ";
			strModulo="Registro Diario de Bancos";
		}
		
		if (strTabla.equals("DSOET"))
		{
		
			strQuery = " UPDATE ";
			strQuery += "dSOETransfer ";
			strQuery += "SET ";
			strQuery += " " + strSetParam+" ";
			//strQuery += " where " + strParam;
			strModulo="Registro Diario de Bancos";
			strTabla="XXdSOETransfer";
			log.debug("<<+++TABLA+++>>"+strTabla);
		}	
		
		if (strTabla.equals("M_TCATALOGOCRI")){		
			if(strSetParam != null && strSetParam.toUpperCase().indexOf("cClaveCRI=''") >= 0 )
				strSetParam = strSetParam.replace("cClaveCRI=''","cClaveCRI=NULL");	
				
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
			
			strQuery = "UPDATE tCatalogoCRI SET " + strSetParam + " ";
			log.debug(strQuery);
			strModulo="Catalogo CRI";
	 	}
	 	if (strTabla.equals("M_TCATCLASIFICACIONADMINISTRATIVA")){		
			if(strSetParam != null && strSetParam.toUpperCase().indexOf("cClave=''") >= 0 )
				strSetParam = strSetParam.replace("cClave=''","cClave=NULL");	
				
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
			
			strQuery = "UPDATE tCatClasificacionAdministrativa SET " + strSetParam + " ";
			log.debug(strQuery);
			strModulo="Catalogo Clave Adminsitrativa";
	 	}
	 	if (strTabla.equals("M_TPAQUETESCOMISION")){		
	 		if(strSetParam != null && strSetParam.toUpperCase().indexOf("nIdPaquete=''") >= 0 )
				strSetParam = strSetParam.replace("nIdPaquete=''","nIdPaquete=NULL");	
				
			log.debug("strParam: " + strParam);
			log.debug("strSetParam: " + strSetParam);
			
			strQuery = "UPDATE tCatPaquetesComision SET " + strSetParam + " ";
			log.debug(strQuery);
			strModulo="Paquetes Viaticos";
	 	}
	 		 		 			 
		if (!strParam.equals("TODO"))
		{
			if (strTabla.equals("REMINTERNO") || strTabla.equals("PERSONALIZADA") || strTabla.equals("COPIAPARA") || strTabla.equals("TURNADO") || strTabla.equals("CAT_REM_EXTERNO") || strTabla.equals("CAT_LDISTRIBUCION") || strTabla.equals("CAT_MUNICIPIO"))
			{
				strQuery += "AND " + strParam.toUpperCase() + " ";
			}
			else
			{
				strQuery += "WHERE " + strParam.toUpperCase() ;

			}
		}
		
		System.out.print(" En UPDATE Sql "+strQuery);
		//Ethiel, codigo para recuperar los valores originales.
		AuditoriaBusinessLogic ABL=new AuditoriaBusinessLogic(jndiName);
		ArrayList valores_origen_destino= new ArrayList();//ABL.getValores_origen_destino(strTabla.substring(2),strSetParam,strParam);



		//La siguiente linea hace el update
		
		if(blnCatUsu){ //Mantenimiento de catalogos de acceso
			Object[] obj = new Object[5];
	 		obj[0] = strParam;
	 		obj[1] = strModulo;
	 		obj[2] = session;
	 		obj[3] = new ArrayList(); //valores_origen_destino;
	 		
			jsonStringOrig = ObjC.InsertMantoCatalogos(strQuery, basesDatos, obj, GestionInterface.OPER_UPD);
			jsonString = "{\"arrResponse\":[{'Col1':'" + jsonStringOrig + "'}]}";
			System.out.print("Finalmente"+strQuery);
			out.print(jsonString);
			
		} else{
			jsonStringOrig = ObjC.InsertCatalogos(strQuery);	
		
		
			jsonString = "{\"arrResponse\":[{'Col1':'" + jsonStringOrig + "'}]}";		
			System.out.print("Finalmente"+strQuery);
			out.print(jsonString);
		
			if(strTabla.startsWith("M_")){
				//termina codigo para recuperar los valores originales.	
				//out.print(jsonString);
				if(jsonStringOrig.equals("S")){
					//Ethiel, para registrar la operacion en IMX_AUDITORIA
					Usuario objUsuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
					String usuario= objUsuario.getLogin();
					Empleado emp= (Empleado)session.getAttribute(GestionInterface.ATT_EMPLEADO);
					String area=emp.getClaveArea();
					String llave=strParam.toUpperCase();
					log.debug("Usuario:" +usuario + "  emp:" + emp +"   area:" + area +"   llave:" +llave);
					
					if(valores_origen_destino.size() > 0 && !"".equals(valores_origen_destino.get(0).toString())&&!"".equals(valores_origen_destino.get(0).toString()))
						ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Actualizar",
										"Para "+llave+" ten&iacute;a:<br>"+valores_origen_destino.get(0).toString(),
										"Para "+llave+" tiene:<br>"+valores_origen_destino.get(1).toString(),strQuery,llave);
				}
			}
		}
		
	}
	catch (Exception exc)
	{	log.error(exc,exc);
		strError = exc.getMessage();
		strError = strError.replaceAll("'","");
		strError = strError.replaceAll(":","");
		strError = strError.toUpperCase();
		jsonString = "{\"arrResponse\":[{'Col1':'" + strError + "'}]}";
		out.print(jsonString);
			//log(exc.getMessage());
			//throw new ServletException(exc);
	}

%>
