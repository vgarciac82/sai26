<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page language="java" contentType="application/json"%>
<%@page import="com.syc.gestion.documental.CatalogosBusinessLogic"%>
<%@page import="javax.servlet.ServletException"%>
<%@page import="com.syc.auditoria.AuditoriaBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.util.Date"%>
<%@page import="com.syc.gestion.core.Empleado"%>

<%!
private static final Logger log = LoggerFactory.getLogger("DeleteJson.jsp");
 %>
<%
	String strQuery = "";
	String strError = "";
	String basesDatos = "";
	boolean blnCatUsu = false;
	String strTabla = request.getParameter("Tabla").toUpperCase();
	String strModulo =strTabla;//por si no entra a algun if donde se llena con el nombre adecuado
	//String strParam = request.getParameter("Param");
	String strParam  = "";
	
	try{
		strParam = request.getParameter("Param") == null ? "": new String( request.getParameter("Param").getBytes(), "UTF-8"); 		
	}catch(Exception e){
		strParam = request.getParameter("Param");
	}	
	String jndiName = "jdbc/gestion";
	String[] strParamSplit = null;
	
	
	/*strParam = strParam.replaceAll("Ã","Á");
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
	strParam = strParam.replaceAll("Ã¼","Ü");*/
	
	String[][] strParamQ = null;
	
	String jsonStringOrig = "";
	String jsonString = "";
	
	try
	{
		CatalogosBusinessLogic ObjC = new CatalogosBusinessLogic(jndiName);

		//catalogo M_TUECUENTASBANCARIAS
		if (strTabla.equals("M_TUECUENTASBANCARIAS"))
		{
			strQuery  = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tUECuentasBancarias ";
			strModulo="CuentasBancarias";
		}
		
		//catalogo M_CAT_ALMACEN
		
		if (strTabla.equals("M_CAT_ALMACEN"))
		{
			strQuery  = "DELETE ";
			strQuery += "FROM ";
			strQuery += "CAT_ALMACEN ";
			strModulo="Almacen";
		}
		
		if (strTabla.equals("M_OPMAPEOCAMBIOPROGRAMA"))
		{
			strQuery  = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tOPMapeoCambioPrograma ";
			strModulo="ObraPublica";
		}
		
		//Catalogo CAT_FIRMANTE
		if (strTabla.equals("M_CAT_FIRMANTE"))
		{
			strQuery  = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_firmante ";
			strModulo="Firmantes";
		}
	
		//Catalogo CAT_DET_INSTRUCCION	
		if (strTabla.equals("M_CAT_DET_INSTRUCCION"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_det_instruccion ";
			strModulo="Detalles de Instruccion";
		}

		//Catalogo CAT_ESTADOS
		if (strTabla.equals("M_CAT_ESTADOS"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_estados ";
		}
		
		//Catalogo CAT_LD_USR
		if (strTabla.equals("M_CAT_LD_USR"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_ld_usr ";
		}
		
		//Catalogo CAT_LDISTRIBUCION
		if (strTabla.equals("M_CAT_LDISTRIBUCION"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_ldistribucion ";
		}
		
		//Catalogo CAT_MUNICIPIO
		if (strTabla.equals("M_CAT_MUNICIPIO"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_municipio ";
		}

		//Catalogo CAT_PROCEDENCIA
		if (strTabla.equals("M_CAT_PROCEDENCIA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_procedencia ";	
			strModulo="Procedencias";
		}
		
		//Catalogo CG_TIPO_PROCEDENCIA
		if (strTabla.equals("M_CG_TIPO_PROCEDENCIA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_tipo_procedencia ";	
		}
		
		
		//Catalogo CAT_PUESTOS
		if (strTabla.equals("M_CAT_PUESTOS"))
		{
			strQuery =  " SELECT id_puesto FROM cg_cat_empleado WHERE " + strParam.toUpperCase() ;
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("No se puede borrar, por que existen empleados relacionados con este registro.");
			}
			
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "cat_puestos ";
			strModulo="Puestos";
		}
		
		//Catalogo CAT_REM_EXTERNO
		if (strTabla.equals("M_CAT_REM_EXTERNO"))
		{
			strQuery =  " SELECT renombre FROM IMXEXPEDIENTES WHERE renombre in ( SELECT re_nombre FROM cat_rem_externo WHERE " + strParam.toUpperCase() +")" ;
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("No se puede borrar, por que existen asuntos relacionados con este registro.");
			}		
		
			strQuery = "DELETE ";
			strQuery += "FROM cat_rem_externo ";
			//strQuery += "SELECT renombre FROM IMXEXPEDIENTES WHERE renombre =  ";
			strModulo="Remitentes Externos";
		}
	
		//Catalogo CAT_TIPO_DOCUMENTO
		if (strTabla.equals("M_CAT_TIPO_DOCUMENTO"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cat_tipo_documento ";
		}
		
		//Catalogo CG_CAT_AREAS
		if (strTabla.equals("M_CG_CAT_AREAS"))
		{
			strQuery =  " SELECT id_area FROM cg_cat_empleado WHERE " + strParam.toUpperCase() ;
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("No se puede borrar, por que existen empleados relacionados con este registro.");
			}
		
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_areas ";
			strModulo="Areas";
		}
		
		//Catalogo CG_CAT_PRIORIDAD
		if (strTabla.equals("M_CG_CAT_PRIORIDAD"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_prioridad ";
		}
		
		//Catalogo CG_CAT_REMITENTE
		if (strTabla.equals("M_CG_CAT_REMITENTE"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_remitente ";
		}
		
		//Catalogo CG_CAT_REMITENTE_AREA
		if (strTabla.equals("M_CG_CAT_REMITENTE_AREA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_remitente_area ";
		}
		
		//Catalogo CG_CAT_REMITENTE_PERSONA
		if (strTabla.equals("M_CG_CAT_REMITENTE_PERSONA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_remitente_persona ";
		}

		//Catalogo VIMX_USUARIO
		if (strTabla.equals("M_VIMX_USUARIO"))
		{
		
			int idx = strParam.lastIndexOf("AND")-1;
			basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			strParam = strParam.substring(0,idx);
			strParamSplit = strParam.split("=");
			strQuery =  " SELECT id_bitacora FROM cg_bitacora_operacion WHERE remitente_id = " + strParamSplit[1] + " or responsable_id =" + strParamSplit[1];
			
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("No se puede borrar el usuario, por que existen asuntos relacionados con este registro.");
			}
			
			strQuery = "DELETE top(1)";
			strQuery += "FROM ";
			strQuery += "vimx_usuario ";
			strModulo="Usuarios";
			blnCatUsu = true;
			
			
		}

		//Catalogo CG_CAT_USUARIOS
		if (strTabla.equals("M_CAT_USUARIOS"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_usuario ";
		}

		//Catalogo CG_CAT_AREAS
		if (strTabla.equals("M_CG_CAT_AREAS"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_areas ";
			strModulo="Areas";
		}
		
		//Catalogo CG_CAT_EMPLEADO
		if (strTabla.equals("M_CG_CAT_EMPLEADO"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_cat_empleado ";
		}
		
		//Catalogo CG_USUARIO_ROLE
			if (strTabla.equals("M_CAT_PUSUARIO"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_usuario_role ";
			
		}
		
		//Catalogo CG_ROLE_OPCION
			if (strTabla.equals("M_CAT_OPERFIL"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_role_opcion ";
			
		}
		
		if (strTabla.equalsIgnoreCase("M_VISTASUR"))//URVP.25062014 Relacion UsuarioVista
		{
			strQuery = "DELETE FROM tVistasUR ";
		}
		
		if (strTabla.equals("M_TCATALOGOROLEOPCION"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_role_opcion ";
			strParam = strParam.replaceAll("ro.", ""); 
			
		}
		
		//Catalogo CG_USUARIO_COBERTURA
			if (strTabla.equals("M_CAT_UCOBERTURA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "cg_usuario_cobertura ";
			
		}
		
		//Catalogo CG_SUPLANTACION
			if (strTabla.equals("M_CG_SUPLANTACION"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "CG_SUPLANTACION ";
			strModulo="Cuentas espejo";
		}
		
		//Catalogo CAT_GRUPO
		//DG
		if (strTabla.equals("M_CG_GRUPO"))
		{				
		
			int idx = strParam.lastIndexOf("AND")-1;
			
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
			System.out.println(basesDatos);
			strParam = strParam.substring(0,idx);
				
			strQuery =  " SELECT g_nombre FROM cg_usuario_grupo WHERE " + strParam.toUpperCase() ;
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("No se puede borrar, por que existen empleados relacionados con este grupo.");
			}
			
			
			
			
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "cg_grupo ";
			strModulo="Grupos";
		}
		
		//catalogo categoria
		if (strTabla.equals("M_CG_CATEGORIA"))
		{
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += " MXCATEGORIA ";
			strModulo="Categoria";
		}
		//Catalogo CAT_GRUPO
		//DG
		if (strTabla.equals("M_CG_ROLES"))
		{				
				
			int idx = strParam.lastIndexOf("AND")-1;
			
			if(strParam.indexOf("PRUEBAS")!=-1){
				strParam = strParam.replaceAll("-PRUEBAS", "");
				basesDatos = strParam.substring(idx+15).replaceAll("'", "").trim();	
			} else{
				basesDatos = strParam.substring(idx+13).replaceAll("'", "").trim();	
			}
			
			System.out.println(strParam);
			strParam = strParam.substring(0,idx);
			strQuery =  " SELECT r_nombre FROM cg_usuario_role WHERE " + strParam.toUpperCase() ;
			strParamQ = ObjC.ArrCatalogos(strQuery);
			if (strParamQ.length > 0)
			{
				throw new ServletException("No se puede borrar, por que existen empleados relacionados con este rol.");
			}
			
			blnCatUsu = true;	
			
			
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "cg_role ";
			strModulo="Roles";
		}
		
		//relacion grupo usurio
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
					
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "cg_usuario_grupo ";
			strModulo="Usuario Grupos";
		}
		//relacion grupo usurio
		if (strTabla.equals("M_CG_ROL_USUARIO"))
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
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "cg_usuario_role ";
			strModulo="Usuario Roles";
			
			
			
			
		}
		//gerencia de ventas
		if (strTabla.equals("M_CG_GERENCIA_VENTAS"))
		{					
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "mxgerencia_ventas ";
			strModulo="Gerencia Ventas";
		}
		//empaques
		if (strTabla.equals("M_CG_EMPAQUES"))
		{					
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "mxempaque ";
			strModulo="Gerencia Ventas";
		}
		//eestatus producto
		if (strTabla.equals("M_CG_ESTATUS_PRODUCTO"))
		{					
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "mxestatus_producto ";
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
	
			strQuery = "DELETE ";    
			strQuery += "FROM ";
			strQuery += "cg_usuario_propiedades ";
			strModulo="Usuario Propiedades";
			blnCatUsu = true;
			
		}		
		if (strTabla.equals("TBANCOAMBIENTAL")){
			strQuery = " DELETE  FROM tBen_BancoAmbiental ";
			strModulo="Mantenimiento del catalogo de Beneficiarios bancarios ambiental";
	 	}
		// Seccion para catalogos Presupuestales
		if (strTabla.equals("M_TRAMO")){
			strQuery = " DELETE  FROM tRamo ";
			strModulo="Ramo";
	 	}
		if (strTabla.equals("M_TCATALOGOUNIDADRESPONSABLE")){
			strQuery = " DELETE  FROM tCatalogoUnidadResponsable ";
			strModulo="Unidad Responsable";
	 	}
		if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL")){
			strQuery = " DELETE  FROM tCatalogoGrupoFuncional ";
			strModulo="Grupo Funcional";
	 	}
	 	if (strTabla.equals("M_TCATALOGOGRUPOFUNCIONAL_ANT")){
			strQuery = " DELETE  FROM tCatalogoGpoFuncionalAnteproyecto ";
			strModulo="Grupo Funcional Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOFUNCION")){
			strQuery = " DELETE  FROM tCatalogoFuncion ";
			strModulo="Funcion";
	 	}
	 	if (strTabla.equals("M_TCATALOGOFUNCION_ANT")){
			strQuery = " DELETE  FROM tCatalogoFuncionAnteproyecto ";
			strModulo="Funcion Anteproyecto";
	 	}
		if (strTabla.equals("M_TCATALOGOSUBFUNCION")){
			strQuery = " DELETE  FROM tCatalogoSubFuncion ";
			strModulo="Sub Funcion";
	 	}
	 	
	 	if (strTabla.equals("M_TCATALOGOSUBFUNCION_ANT")){
			strQuery = " DELETE  FROM tCatalogoSubFuncionAnteproyecto ";
			strModulo="Sub Funcion Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL")){
			strQuery = " DELETE  FROM tCatalogoProgramaGeneral ";
			strModulo="Programa General";
	 	}
	 	if (strTabla.equals("M_TCATALOGOPROGRAMAGENERAL_ANT")){
			strQuery = " DELETE  FROM tCatalogoProgGralAnteproyecto ";
			strModulo="Programa General Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL")){
			strQuery = " DELETE  FROM tCatalogoActividadInstitucional ";
			strModulo="Actividad Institucional";
	 	}
	 	if (strTabla.equals("M_TCATALOGOACTIVIDADINSTITUCIONAL_ANT")){
			strQuery = " DELETE  FROM tCatalogoAIAnteproyecto ";
			strModulo="Actividad Institucional Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO")){
			strQuery = " DELETE  FROM tCatalogoProgramaPresupuestario ";
			strModulo="Programa Presupuestario";
	 	}
	 	if (strTabla.equals("M_TCATALOGOPROGRAMAPRESUPUESTARIO_ANT")){
			strQuery = " DELETE  FROM tCatalogoProgramaPresupuestarioAnteproyecto ";
			strModulo="Programa Presupuestario Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOPARTIDA")){
			strQuery = " DELETE  FROM tCatalogoPartida ";
			strModulo="Partida";
	 	}
	 	if (strTabla.equals("M_TCATALOGOPARTIDA_ANT")){
			strQuery = " DELETE  FROM tCatalogoPartidaAnteproyecto ";
			strModulo="Partida Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOTIPOGASTO")){
			strQuery = " DELETE  FROM tCatalogoTipoGasto ";
			strModulo="Tipo Gasto";
	 	}
	 	
	 	if (strTabla.equals("M_TCATALOGOTIPOGASTO_CONAC")){
			strQuery = "DELETE  FROM tCatalogoTipoGastoCONAC";
			strModulo="Tipo Gasto";
	 	}
	 	
	 	if (strTabla.equals("M_TCATALOGOTIPOGASTO_ANT")){
			strQuery = " DELETE  FROM tCatalogoTipoGastoAnteproyecto ";
			strModulo="Tipo Gasto Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO")){
			strQuery = " DELETE  FROM tCatalogoFuenteFinanciamiento ";
			strModulo="Fuente Financiamiento";
	 	}
	 	if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTOCONAC")){
			strQuery = " DELETE  FROM tCatalogoFuenteFinanciamientoCONAC ";
			strModulo="Fuente Financiamiento Conac";
	 	}
	 	if (strTabla.equals("M_TCATALOGOFUENTEFINANCIAMIENTO_ANT")){
			strQuery = " DELETE  FROM tCatalogoFuenteFinanciamientoAnteproyecto ";
			strModulo="Fuente Financiamiento Anteproyecto";
	 	}
	 	
		if (strTabla.equals("M_TCATALOGOENTIDADFEDERATIVA")){
			strQuery = " DELETE FROM tCatalogoEntidadFederativa ";
			strModulo="Entidad Federativa";
	 	}
		if (strTabla.equals("M_TCATALOGOCARTERA")){
			strQuery = " DELETE FROM tCatalogoCartera ";
			strModulo="Cartera";
		}
		
		if (strTabla.equals("M_TCATALOGOCARTERA_ANT")){
			strQuery = " DELETE FROM tCatalogoCarteraAnteproyecto ";
			strModulo="Cartera Anteproyecto";
		}
		if (strTabla.equals("M_TUNIDADRESPONSABLE")){
			strQuery = " DELETE FROM TCATUNIDADRESPONSABLE ";
			strModulo="Unidad Responsable";
		}
		if (strTabla.equals("M_TUNIDADRESPONSABLE_ANT")){
			strQuery = " DELETE FROM TCATUNIDADRESPONSABLEANTEPROYECTO ";
			strModulo="Unidad Responsable Anteproyecto";
		}
		if (strTabla.equals("M_TCATALOGOCAUSAAVISO_REIN")){
			strQuery = " DELETE FROM tCatalogoCausaAviso ";
			strModulo="Catalogo Causa Aviso Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOTIPOCAUSAAVISO_REIN")){
			strQuery = " DELETE FROM tCatalogoTipoCausaAviso ";
			strModulo="Catalogo Tipo Causa Aviso Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOFORMAPAGO_REIN")){
			strQuery = " DELETE FROM tCatalogoFormaPago ";
			strModulo="Catalogo Forma Pago Reintegro";
		}
		if (strTabla.equals("M_TCATALOGOMOVIMIENTO_REIN")){
			strQuery = " DELETE FROM tCatalogoMvto ";
			strModulo="Catalogo Movimientos Reintegro";
		}
		if (strTabla.equals("TCATALOGOMOVIMIENTO_RECT")){
			strQuery = " DELETE FROM tCatalogoTipoMovimiento ";
			strModulo="Catalogo Movimientos de Rectificacion";
		}
		if (strTabla.equals("M_TEJERCICIOFISCAL")){
			strQuery = " DELETE FROM tEjercicioFiscal ";
			strModulo="Cartera";
	 	}
		if (strTabla.equals("M_TCUENTAS")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tCuentas ";
			strModulo="Cuentas";
		}
		if (strTabla.equals("M_TTIPOSUBCUENTA")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTipoSubcuenta ";
			strModulo="Tipo de Sub Cuenta";
		}
		if (strTabla.equals("M_TTIPOSUBCUENTACONF")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTipoSubcuentaConf ";
			strModulo="Configuracón de Sub Cuenta";
		}
		
		//if (strTabla.equals("M_TCATALOGORESTRICCIONES")){
		if (strTabla.equals("M_TVALIDAPNRGP")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tValidaPNRGP ";
			strModulo="Normatividad de Partidas Restringidas";
		}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_FUNCIONAL")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tvalidacion_adecuacion_funcional ";
			strModulo="Validacion de adecuaciones funcionales";
		}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_PROGRAMATICA")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tvalidacion_adecuacion_programatica ";
			strModulo="Validacion de adecuaciones programatica";
		}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_ECONOMICA")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tvalidacion_adecuacion_economica ";
			strModulo="Validacion de adecuaciones economica";
		}

		if (strTabla.equals("M_TVALIDACION_ADECUACION_CLAVE_GRUPO")){
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tvalidacion_adecuacion_clave_grupo ";
			strModulo="Validacion de adecuaciones clave Grupo";
		}

	 	if (strTabla.equals("M_TCATALOGOCOMPLEJIDADFF")){
	 		strQuery = " delete FROM tCatalogoComplejidadFF ";
			strModulo="Complejidad de Fuente de Financiamiento";
	 	}
	 	if ("M_TCATALOGOCOMPLEJIDADFF".equals(strTabla)){
	 		strQuery = " delete FROM tValidaInvercionAGastoCorriente ";
			strModulo="Validación de Inversión a Gasto Corriente.";
	 	}
	 	
	 	//SAI MODULO DE REQUISICIONES BIENES Y SERVICIOS	 	
	 	if (strTabla.equals("M_MCATALOGOUNIDADMEDIDA")){
	 		strQuery = " delete FROM mCatalogoUnidadMedida ";
	 		strModulo="Catalogo de Unidades de Medida";	
		}
        
        // Aqui comienza cambios para catalogos en desembolsos 
	 	if (strTabla.equals("D_CATAGENTEFINANCIERO")){
	 		strQuery = " delete FROM dCat_Agente_Financiero ";
	 		strModulo="Catalogo de Agente Financiero ";	
		}
	 	if (strTabla.equals("D_CATOFI")){
	 		strQuery = " delete FROM dCat_OFI ";
	 		strModulo="Catalogo de Organismo Financiero Internacional ";	
		}
	 	if (strTabla.equals("D_CATAREAEXECUTORAEXT")){
	 		strQuery = " delete FROM dCat_Entidad_Ejec_Resp ";
	 		strModulo="Catalogo de Entidad Ejecutora Externa ";	
		}
	 	if (strTabla.equals("D_DCATTIPOCAMBIO")){
	 		strQuery = " delete FROM dCat_Tipo_Cambio ";
	 		strModulo="Catalogo de Tipo de Cambio ";
		}

	    // Aqui Termina cambios para catalogos en desembolsos
	    
	 	if (strTabla.equals("M_MCATALOGOFIRMANTES")){
	 		strQuery = " delete FROM mCatalogoFirmantes ";
	 		strModulo="Catalogo de mCatalogoFirmantes";	
		}	
	 		 	
	 	if (strTabla.equals("M_MCATALOGOFIRMANTES_USUARIO")){
	 		strQuery = " delete FROM mCatalogoFirmantes ";
	 		strModulo="Catalogo de mCatalogoFirmantes";	
		}	
	 	if (strTabla.equals("CATALOGOCABM")){
	 		
	 		strParam = request.getParameter("SetParam");
	 		int i=strParam.indexOf(',');
	 		String strSetParamAux=strParam.substring(0,i-1);
	 		strParam=strSetParamAux;
	 		//System.out.println("StrAux"+strAux);
	 		strQuery = " delete FROM mCatalogoCABM ";
	 		strModulo="Catalogo de mCatalogoCABM";	
		}
	 	//FIN SAI MODULO DE REQUISICIONES BIENES Y SERVICIOS
	 	
	 	if (strTabla.equals("TCATALOGOTIPOCLC")){
			strQuery = " DELETE  FROM tCatalogoTipoCLC ";
			strModulo="Tipo CLC";
	 	}
	 	
		if (strTabla.equals("TTECHOUNORMATIVA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTechoUNormativa ";
		}
		if (strTabla.equals("TTECHOUEJECUTORA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTechoUEjecutora ";
		}
		if (strTabla.equals("TTECHOSPARTIDA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTechosPartida ";
		}
		if (strTabla.equals("TTECHOPROGRAMAPRESUPUESTARIO"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTechoProgramaPresupuestario ";
		}
		if (strTabla.equals("TTECHOENTIDADFEDERATIVA"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "tTechoEntidadFederativa ";
		}

		if (strTabla.equals("M_CG_GRUPO_PROPIEDADES"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "CG_GRUPO_PROPIEDADES ";
			strModulo="Parametros Sistema";
		}

		if (strTabla.equals("M_TCATALOGOESTPROGAUT"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "TCATALOGOESTPROGAUT ";
			strModulo="Normatividad Estructura Programatica";
		}
		
		if (strTabla.equals("M_TCATALOGORAMOOGTO"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "TCATALOGORAMOOGTO ";
			strModulo="Normatividad financiamiento, partida y objeto del gasto";
		}

		if (strTabla.equals("M_TCATALOGOPARTIDAVALIDAPP"))
		{
			strQuery = "DELETE ";
			strQuery += "FROM ";
			strQuery += "TCATALOGOPARTIDAVALIDAPP ";
			strModulo="Normatividad relacion Partida y Prog Presupuestal";
		}
		
		//Borrar un requisitos para el checklist de procedimiento
		if (strTabla.equals("M_REQUISITOS_PROCEDIMIENTO")) {
			strQuery = "DELETE FROM mCatalogoRequisitosProcedimiento ";
		}
		
		if ("M_TVALIDAINVERCIONAGASTOCORRIENTE".equals(strTabla)){
	 		strQuery = "delete FROM tValidaInvercionAGastoCorriente ";
			strModulo="Normatividad InvercionAGastoCorriente";
		}
		
		// Borrado Evento Manual
		if (strTabla.equals("M_TEVENTO")){
			strQuery = "delete FROM ";
			strQuery += "       tEvento       ";
			strModulo="Evento";
		}
		// Borrado Eventos
		if (strTabla.equals("M_TEVENTOMANUAL")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoManual       ";
			strModulo="Evento";
		}
		// Borrado Grupo de Eventos
		if (strTabla.equals("M_TGRUPOEVENTO")){
			strQuery = "delete FROM ";
			strQuery += "       TGRUPOEVENTO       ";
			strModulo="Grupo Evento";
		}
		// Borrado SubGrupo de Eventos
		if (strTabla.equals("M_TSUBGRUPOEVENTO")){
			strQuery = "delete FROM ";
			strQuery += "       TSUBGRUPOEVENTO       ";
			strModulo="SubGrupo Evento";
		}
		// Borrado Evento Relacion
		if (strTabla.equals("M_TEVENTORELACION")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoRelacion       ";
			strModulo="Evento Relacion";
		}
		// Borrado Evento Manual
		if (strTabla.equals("M_TEVENTOMANUAL")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoManual       ";
			strModulo="Evento Manual";
		}
		// Borrado Evento Concepto
		if (strTabla.equals("M_TEVENTOCONCEPTO")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoConcepto       ";
			strModulo="Evento Concepto";
		}
		
		// Borrado Evento Configuracion
		if (strTabla.equals("M_TEVENTOCONFIGURACION")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoConfiguracion       ";
			strModulo="Evento Configuracion";
		}
		// Borrado Evento Configuracion Detalle
		if (strTabla.equals("M_TEVENTOCONFIGURADETALLE")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoConfiguraDetalle       ";
			strModulo="Evento Configuracion Detalle";
		}
		// Borrado Evento Partida
		if (strTabla.equals("M_TEVENTOPARTIDA")){
			strQuery = "delete FROM ";
			strQuery += "       tEventoPartida       ";
			strModulo="Evento Partida";
		}
		// Borrado Catalogo CABMS Contable
		if (strTabla.equals("M_TCATALOGOCABMSCONTABLE")){
			strQuery = "delete FROM ";
			strQuery += " tCatalogoCABMS ";
			strModulo="Catalogo CABMS Contable";
		}
		// Borrado Registro Diario de Bancos
		if (strTabla.equals("TCATALOGOCONCEPTO_RDB")){
			strQuery = "delete FROM ";
			strQuery += "       tRdbCat_Concepto       ";
			strModulo="Registro Diario de Bancos";
		}
		if (strTabla.equals("TCATALOGODOCORIGEN_RDB")){
			strQuery = "delete FROM ";
			strQuery += "       tRdbCat_DocumentoOrigen       ";
			strModulo="Registro Diario de Bancos";
		}
		if (strTabla.equals("TCATALOGOMEDIOPAGO_RDB")){
			strQuery = "delete FROM ";
			strQuery += "       tRdbCat_MedioPago       ";
			strModulo="Registro Diario de Bancos";
		}
		if (strTabla.equals("TCATALOGOORIGENPAGO_RDB")){
			strQuery = "delete FROM ";
			strQuery += "       tRdbCat_OrigenDeposito       ";
			strModulo="Registro Diario de Bancos";
		}
		if (strTabla.equals("DPRESTAMOPROGRAMAPRESUPUESTARIO")){
			strQuery = "delete FROM ";
			strQuery += "       dPrestamoProgramaPresupuestario       ";
			strModulo="Desembolso de credito externo";
		}
		
		if(  "RFCPagoSinFacturaPermitido".equalsIgnoreCase( strTabla )  )
		{
			strQuery = "DELETE FROM tRFCPagoSinFacturaPermitido WITH(ROWLOCK) ";
			strModulo="Excepcion de RFC";
		}
		if(  "M_TCATALOGOCRI".equalsIgnoreCase( strTabla )  )
		{
			if(strParam != null && strParam.toUpperCase().indexOf("cClaveCRI=''") >= 0 )
				strParam = strParam.replace("cClaveCRI=''","cClaveCRI=NULL");	
				
			strQuery = "DELETE FROM tCatalogoCRI WITH(ROWLOCK) ";
			strModulo="Catalogo CRI";
		}
		
		if (strTabla.equals("M_TCATALOGOCLAVEINTERNA")){
			strParam = strParam.trim();
			System.out.println(strParam);
			if(strParam != null && strParam.toUpperCase().indexOf("cClaveInterna=''") >= 0 )
				strParam = strParam.replace("cClaveInterna=''","cClaveInterna=NULL");		
			
			strQuery = " DELETE FROM tCatalogoClaveInterna ";
			strModulo="Clave Interna";
	 	}
		
		if (strTabla.equals("M_TCATALOGOMETAS")){
			strParam = strParam.trim();
			System.out.println(strParam);
			if(strParam != null && strParam.toUpperCase().indexOf("cMeta=''") >= 0 )
				strParam = strParam.replace("cMeta=''","cMeta=NULL");		
			
			strQuery = " DELETE FROM tCatalogoMetas ";
			strModulo="Catalogo METAS";
	 	}
		
		if (strTabla.equals("M_TPAQUETESCOMISION")){
			strParam = strParam.trim();
			System.out.println(strParam);
			if(strParam != null && strParam.toUpperCase().indexOf("nIdPaquete=''") >= 0 )
				strParam = strParam.replace("nIdPaquete=''","nIdPaquete=NULL");		
			
			strQuery = " DELETE FROM tCatPaquetesComision ";
			strModulo="Paquetes Viaticos";
	 	}
		
		System.out.println(strParam);
		
		if (!strParam.equals("TODO"))
		{
			if (strTabla.equals("REMINTERNO") || strTabla.equals("PERSONALIZADA") || strTabla.equals("COPIAPARA") || strTabla.equals("TURNADO") || strTabla.equals("CAT_REM_EXTERNO") || strTabla.equals("CAT_LDISTRIBUCION") || strTabla.equals("CAT_MUNICIPIO"))
			{
				strQuery += "AND " + strParam.toUpperCase() + " ";
			}
			else
			{
				strQuery += "WHERE " + strParam.toUpperCase() ;
				//Case 1
				//strQuery += "WHERE di_descripcion like 'Z%' ";
				//Case 2
				//strQuery += "WHERE td_descripcion like '" + strParam.toUpperCase() + "%' ";
			}		 
		}
	
		System.out.println(strQuery);
	
		if(blnCatUsu){
		Object[] obj = new Object[5];
	 		obj[0] = strParam;
	 		obj[1] = strModulo;
	 		obj[2] = session;
	 	
			jsonStringOrig = ObjC.InsertMantoCatalogos(strQuery, basesDatos, obj,  GestionInterface.OPER_DEL);
			jsonString = "{\"arrResponse\":[{'Col1':'" + jsonStringOrig + "'}]}";
			out.print(jsonString);
		
		} else{
			jsonStringOrig = ObjC.InsertCatalogos(strQuery);
			jsonString = "{\"arrResponse\":[{'Col1':'" + jsonStringOrig + "'}]}";
			out.print(jsonString);
		
			if(strTabla.startsWith("M_")){
				if(jsonStringOrig.equals("S")){
					//Ethiel, para registrar la operacion en IMX_AUDITORIA
					AuditoriaBusinessLogic ABL=new AuditoriaBusinessLogic(jndiName);
					//String valores_insert=ABL.getValores_insert(strQuery,strParam);
					Usuario objUsuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
					String usuario= objUsuario.getLogin();
					Empleado emp= (Empleado)session.getAttribute(GestionInterface.ATT_EMPLEADO);
					String area=emp.getClaveArea();
					//ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Agregar","",valores_insert,strQuery,"");
				  	ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Borrar",strParam.toUpperCase(),"",strQuery,strParam.toUpperCase());
				}
			}
		}
	}
	catch (Exception exc) 
	{	exc.printStackTrace();
		log.error(exc.getMessage(), exc);
		strError = exc.getMessage();
		strError = strError.replaceAll("'","");
		strError = strError.replaceAll(":","");
		strError = strError.toUpperCase();
		jsonString = "{\"arrResponse\":[{'Col1':'" + strError + "'}]}";
		out.print(jsonString);
	}

%>
