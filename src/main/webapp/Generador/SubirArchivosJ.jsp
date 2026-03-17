<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.fileupload.DiskFileUpload"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>

<%@page import="java.sql.Connection" %>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.CallableStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.syc.ejercido.pagado.SubirArchivosBase"%>
<%
out.println("<html>");
   out.println("<head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
   out.println("<title>Evidencia unica</title>");
   out.println("</head>");
   out.println("<body>");
   out.println("<div id='dv' align='center'>");
   out.println("<table width='60%' height='45%'>");
      
try{
	DiskFileUpload fu = new DiskFileUpload();
	List fileItems = fu.parseRequest(request);
	if(fileItems == null){System.out.println("Error: Archivo Vacio"); return;}
	//Iteramos por cada elemento
	Iterator i = fileItems.iterator();
	//fu.setSizeMax(1248 * 512 * 10); // 1024 K
        
	FileItem actual = null;
	String archivo = "";
	String ruta = "";
	String nomTabla = "";
	
	while(i.hasNext()){
		actual = (FileItem) i.next();
		File fichero = new File("/SubirArchivo/"+actual.getName());
		ruta = fichero.getAbsolutePath();
		archivo = actual.getFieldName();
		
		if(actual.getFieldName().equals("flArchivo1")){
			System.out.println("Nombre Archivo: " +actual.getFieldName());
			System.out.println("Ubicacion: " +fichero.getAbsolutePath());
      		nomTabla = "CLC_SICOP";
     	}
     	if(actual.getFieldName().equals("flArchivo2")){
			System.out.println("Nombre Archivo: " +actual.getFieldName());
			System.out.println("Ubicacion: " +fichero.getAbsolutePath());
      		nomTabla = "CLC_SIAFF_ENC";
     	}
     	if(actual.getFieldName().equals("flArchivo3")){
			System.out.println("Nombre Archivo: " +actual.getFieldName());
			System.out.println("Ubicacion: " +fichero.getAbsolutePath());
      		nomTabla = "CLC_SIAFF_DET";
     	}
     	
      		SubirArchivosBase valor = new SubirArchivosBase();
      		try{
				boolean v = valor.subirArchivo(nomTabla,ruta);
				//boolean v = valor.subirArchivo();
				if(v){
				 	out.println("<td>Agregados Correctamente</td>");
				}else{
					out.println("<td><table><tr><td>Error al Agregar Registros</td></tr>");
					out.println("<tr><td>Posible Errores:</td></tr>");
					out.println("<tr><td> -Verificar la Ruta. El archivo debe de existir en: C:/SubirArchivo/nombreArchivo.csv</td></tr>");
					out.println("<tr><td> -Error de Columnas</td></tr></table></td>");
				}
			}catch(Exception e){
				System.out.println("catch_" +e);
				out.println("<td>Error al Agregar Registros Intente De Nuevo o Verifique El Archivo</td>");
			}finally{
				System.out.println("final");
			}			  
		
	}
}catch(Exception e){
	System.out.println("Error: " +e );
}finally{
	System.out.println("finally");
}
out.println("<td><input type='button' value='Regresar' onClick=location.href='./SubirArchivo.jsp'></td>");
out.println("</table>");
out.println("</div>");
out.println("</body>");
out.println("</html>");
%>