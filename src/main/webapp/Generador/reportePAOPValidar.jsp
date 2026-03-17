<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page language="java" contentType="application/json"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.ejercido.pagado.ReportePAOP"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);


if (usuario != null) {
	try{
			
			String strMesAnt = request.getParameter("mesAnterior");
			String strMesNue = request.getParameter("mesNuevo");
			System.out.println(strMesAnt +"/"+ strMesNue);
			//int strMesAnt = 2;
			//int strMesNue = 	4;
			String strYear = 		request.getParameter("year");
			String strTipo = 		request.getParameter("tipo");
			
			ReportePAOP paop = new ReportePAOP();
			
			if(strTipo.equals("guardarNuevoMesPAOP")){
			
				JSONObject valor = paop.guardarNuevoMesPAOP(strMesAnt, strMesNue, strYear);
				System.out.println("aki mero");
				out.println(valor);
			}		
	}	
	catch(Exception e){
		System.out.println("catch_" +e);
		out.println("false");
	}
}else{
	out.println("{\"sinSesion\":\"sinSesion\"}");
}	
%>
