<%@ page language="java" contentType="application/json"%>
<%@ page import="com.syc.gestion.documental.CatalogosBusinessLogic"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>

<%@ page import="javax.xml.parsers.DocumentBuilderFactory, javax.xml.parsers.DocumentBuilder, org.w3c.dom.*"%>
<%@ page import="org.xml.sax.SAXException"%>
<%@ page import="java.io.IOException"%>


<%
//	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
//	String strUsuario = u.getLogin();
//	final String PRD_GESTION  = "3";
//	final String TAB_CAT_AREAS = "13";
//	String[][] strParamQ = null;
	
	String strQuery  = "";
	String strFrom = "";
	String strWhere = ""; 
	
	String strError  = "";
	
	String jndiName = "jdbc/gestion";
	
	String strDescriptor = request.getParameter("Descriptor");
	String strMaxReg = request.getParameter("MaxReg");
	

	//String strTabla  = request.getParameter("Tabla").toUpperCase();
	//String strParam  = request.getParameter("Param");
	
	/*
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
	*/
	
	String jsonStringOrig = "";
	String jsonString = "";
	response.setContentType("text/x-json; charset=UTF-8");
	
	try
	{
		CatalogosBusinessLogic ObjC = new CatalogosBusinessLogic(jndiName);
	
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse("c:/Generador/xml/" + strDescriptor + ".xml" );   //"c:/xml/hello.xml"
		

		NodeList nl = doc.getElementsByTagName("select");
		
		Node my_node = nl.item(0);
		
		strQuery = my_node.getFirstChild().getNodeValue();

/*	
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
			strQuery += "       cat_rem_externo R     "; 
			strQuery += " INNER  JOIN cat_procedencia P ";
			strQuery += " ON     r.id_procedencia = p.id_procedencia ";
			strQuery += " INNER  JOIN cg_tipo_procedencia TP ";
			strQuery += " ON     tp.id_tipo_procedencia = p.id_tipo_procedencia ";
			strQuery += " LEFT   OUTER JOIN cat_estados E ";
			strQuery += " ON     r.id_estado      = e.id_estado ";
			strQuery += " LEFT   OUTER JOIN cat_municipio M ";
			strQuery += " ON     r.id_municipio   = m.id_municipio ";
			
			strParam = strParam.replaceAll("R.RE_NOMBRE LIKE '","R.RE_NOMBRE LIKE '%");
			strParam = strParam.replaceAll("R.RE_CARGO LIKE '","R.RE_CARGO LIKE '%");
			strParam = strParam.replaceAll("R.RE_DIRECCION LIKE '","R.RE_DIRECCION LIKE '%");
		}

	
		if (!strParam.equals("TODO") && !strParam.equals(""))
		{
			if ((strQuery != null) && strQuery.indexOf("WHERE")>= 0)
			//if (strTabla.equals("REMINTERNO") || strTabla.equals("PERSONALIZADA") || strTabla.equals("COPIAPARA") || strTabla.equals("TURNADO") || strTabla.equals("CAT_REM_EXTERNO") || strTabla.equals("CAT_LDISTRIBUCION") || strTabla.equals("CAT_MUNICIPIO") || strTabla.equals("M_CAT_REM_EXTERNO") || strTabla.equals("M_CAT_LOGIN") || strTabla.equals("M_CAT_OPERFIL") || strTabla.equals("M_CAT_UCOBERTURA") || strTabla.equals("M_CAT_PROCEDENCIA") || strTabla.equals("CG_CAT_AREAS_COB") || strTabla.equals("M_CG_CAT_EMPLEADO") || strTabla.equals("M_CAT_USUARIOS") )
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
*/
		jsonStringOrig = ObjC.readCatalogos(strQuery).toString();
		jsonString     = jsonStringOrig;

		/*
		if ((strTabla.equals("REMINTERNO") || strTabla.equals("PERSONALIZADA") || strTabla.equals("COPIAPARA") || strTabla.equals("TURNADO")) && (strParamQ == null || strParamQ.length == 0))
		{
			jsonString = "[]";
		}
		*/

		out.print(jsonString);

	}
	catch (Exception exc) 
	{
		strError = exc.getMessage();
		strError = strError.replaceAll("'","");
		strError = strError.replaceAll(":","");
		strError = strError.toUpperCase();
		jsonString = "[{'Col1':'" + strError + "'}]";
		out.print(jsonString);
			//log(exc.getMessage());
			//throw new ServletException(exc);
	}

%>
