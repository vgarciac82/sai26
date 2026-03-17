<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="jxl.write.Label"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>

<% 
	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	ArrayList arrAdecuacion=null;
	if(session.getAttribute("objAdecuacion")!=null)
		arrAdecuacion=(ArrayList)session.getAttribute("objAdecuacion");
		
	//session.removeAttribute("objAdecuacion");

	response.setContentType("application/vnd.ms-excel");
	String file_name=c.getFolio();
	response.addHeader("Content-Disposition", "inline; filename=\"LAYOUT-SICOP" + file_name + ".csv\";");
	
	//Para escribir en el archivo la fecha de tipo dd/mm/yyyy
	java.util.Date date = new java.util.Date(); 
	java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
	String fecha = sdf.format(date);
	
	jxl.write.WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(response.getOutputStream());  
	jxl.write.WritableSheet writableSheet = writableWorkbook.createSheet("Afecta 3", 0);
	
    writableSheet.addCell(new jxl.write.Label(0, 0, "H"));
    writableSheet.addCell(new jxl.write.Label(1, 0, fecha));
    writableSheet.addCell(new jxl.write.Label(2, 0, fecha));
    writableSheet.addCell(new jxl.write.Label(3, 0, "16"));
    writableSheet.addCell(new jxl.write.Label(4, 0, "16"));
    writableSheet.addCell(new jxl.write.Label(5, 0, "16"));
    writableSheet.addCell(new jxl.write.Label(6, 0, "RHQ"));
    writableSheet.addCell(new jxl.write.Label(7, 0, "RHQ"));
    writableSheet.addCell(new jxl.write.Label(8, 0, "000"));
    writableSheet.addCell(new jxl.write.Label(9, 0, "9"));
    writableSheet.addCell(new jxl.write.Label(10, 0, "099"));
    //writableSheet.addCell(new jxl.write.Label(10, 0, ""));
    //writableSheet.addCell(new jxl.write.Label(11, 0, ""));
    String folio = request.getParameter("f").split("-")[2];
    while(folio.length()<5){
    	folio="0"+folio;
    }
    writableSheet.addCell(new jxl.write.Label(12, 0, request.getParameter("n")+"_0"+folio));
    writableSheet.addCell(new jxl.write.Label(13, 0, request.getParameter("n")+"_0"+folio));
    writableSheet.addCell(new jxl.write.Label(14, 0, folio));
    
    boolean resp = false;
    Connection conn = null;
    
    try{
    conn = adecua.getConnection(GestionInterface.ATT_CONEXION);
    for(int i=6;i<arrAdecuacion.size()-5;i++){
		Saldo objSaldo =(Saldo)arrAdecuacion.get(i);
		String ep = objSaldo.getClaveSIAFF()+"."+objSaldo.getClaveInterna();
		
		if(objSaldo.getEp().equals("A")){
		 resp = adecua.esModificado(ep);
		}
		String claveEp = adecua.obtenClaveSicop(resp,objSaldo.getEp());
		
		String[] splitSiaff = ep.split("\\.");
		String[] splitClaveEp = claveEp.split("\\.");
		
		writableSheet.addCell(new jxl.write.Label(0, i-5, splitClaveEp[0]));
		writableSheet.addCell(new jxl.write.Label(1, i-5, splitClaveEp[1]));
		writableSheet.addCell(new jxl.write.Label(2, i-5, splitClaveEp[2]));
		writableSheet.addCell(new jxl.write.Label(3, i-5, splitClaveEp[3]));
		writableSheet.addCell(new jxl.write.Label(4, i-5, splitSiaff[1]));
		writableSheet.addCell(new jxl.write.Label(5, i-5, splitSiaff[2]));
		writableSheet.addCell(new jxl.write.Label(6, i-5, splitSiaff[0]));
		writableSheet.addCell(new jxl.write.Label(7, i-5, splitSiaff[3]));
		writableSheet.addCell(new jxl.write.Label(8, i-5, splitSiaff[4]));
		writableSheet.addCell(new jxl.write.Label(9, i-5, splitSiaff[5]));
		writableSheet.addCell(new jxl.write.Label(10, i-5, splitSiaff[6]));
		writableSheet.addCell(new jxl.write.Label(11, i-5, splitSiaff[7]));
		writableSheet.addCell(new jxl.write.Label(12, i-5, splitSiaff[8]));
		writableSheet.addCell(new jxl.write.Label(13, i-5, splitSiaff[9].substring(0,1)));
		writableSheet.addCell(new jxl.write.Label(14, i-5, splitSiaff[9].substring(1,2)));
		writableSheet.addCell(new jxl.write.Label(15, i-5, splitSiaff[9].substring(2,3)));
		writableSheet.addCell(new jxl.write.Label(16, i-5, splitSiaff[9].substring(3,5)));
		writableSheet.addCell(new jxl.write.Label(17, i-5, splitSiaff[10]));
		writableSheet.addCell(new jxl.write.Label(18, i-5, splitSiaff[11]));
		writableSheet.addCell(new jxl.write.Label(19, i-5, splitSiaff[12]));
		writableSheet.addCell(new jxl.write.Label(20, i-5, splitSiaff[13]));
		
		String clvInterna1 = splitSiaff[14];
		while(clvInterna1.length()<10){
	    	clvInterna1="0"+clvInterna1;
	    }
		
		writableSheet.addCell(new jxl.write.Label(21, i-5, clvInterna1));
		String clvInterna2 = splitSiaff[15];
		writableSheet.addCell(new jxl.write.Label(22, i-5, clvInterna2.substring(1,3)));
		writableSheet.addCell(new jxl.write.Label(23, i-5, "000"));
		writableSheet.addCell(new jxl.write.Label(24, i-5, "000"));
		writableSheet.addCell(new jxl.write.Label(25, i-5, "00000"));
		writableSheet.addCell(new jxl.write.Label(26, i-5, "00000"));
		writableSheet.addCell(new jxl.write.Label(27, i-5, "0000000000"));
		writableSheet.addCell(new jxl.write.Label(28, i-5, objSaldo.getMontoAnual()));
		writableSheet.addCell(new jxl.write.Label(29, i-5, "1"));
		writableSheet.addCell(new jxl.write.Label(30, i-5, objSaldo.getMontoEnero()));
		writableSheet.addCell(new jxl.write.Label(31, i-5, objSaldo.getMontoFebrero()));
		writableSheet.addCell(new jxl.write.Label(32, i-5, objSaldo.getMontoMarzo()));
		writableSheet.addCell(new jxl.write.Label(33, i-5, objSaldo.getMontoAbril()));
		writableSheet.addCell(new jxl.write.Label(34, i-5, objSaldo.getMontoMayo()));
		writableSheet.addCell(new jxl.write.Label(35, i-5, objSaldo.getMontoJunio()));
		writableSheet.addCell(new jxl.write.Label(36, i-5, objSaldo.getMontoJulio()));
		writableSheet.addCell(new jxl.write.Label(37, i-5, objSaldo.getMontoAgosto()));
		writableSheet.addCell(new jxl.write.Label(38, i-5, objSaldo.getMontoSeptiembre()));
		writableSheet.addCell(new jxl.write.Label(39, i-5, objSaldo.getMontoOctubre()));
		writableSheet.addCell(new jxl.write.Label(40, i-5, objSaldo.getMontoNoviembre()));
		writableSheet.addCell(new jxl.write.Label(41, i-5, objSaldo.getMontoDiciembre()));
    }
    writableWorkbook.write();  
    writableWorkbook.close(); 
    }catch(Exception ex){
    	ex.printStackTrace();
    }finally{
    	if(conn!=null)
    		conn.close();
    	conn = null;
    }
    /*out.println("<html>");
	out.println("<body>");
	out.println("<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" height=\"98%\">");
	out.println(" <tr>");
	out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\" border=\"1\">");
	out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
	out.println("			<table>");
	out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
	out.println("				</thead>");
	out.println("				<tbody class=\"scrollContent\">");
	out.println("				<tr>");
	out.println("				<th>H</th>");
	out.println("				<th>"+fecha+"</th>");
	out.println("				<th>"+fecha+"</th>");
	out.println("				<th>16</th>");
	out.println("				<th>16</th>");
	out.println("				<th>16</th>");
	out.println("				<th>RHQ</th>");
	out.println("				<th>RHQ</th>");
	out.println("				<th>'000</th>");
	out.println("				<th>9</th>");
	out.println("				<th>099</th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th>"+request.getParameter("n")+"_</th>");
	out.println("				<th>"+request.getParameter("n")+"_</th>");
	out.println("				<th>"+request.getParameter("f").split("-")[2]+"</th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				<th></th>");
	out.println("				</tr>");
	for(int i=6;i<arrAdecuacion.size()-5;i++){
		Saldo objSaldo =(Saldo)arrAdecuacion.get(i);
		String ep = objSaldo.getClaveSIAFF()+"."+objSaldo.getClaveInterna();
		String claveEp ="";
		String[] splitSiaff = ep.split("\\.");
		if(objSaldo.getEp().equals("R")){
			claveEp = "435.110_ETR3.M.35";
		}else if(objSaldo.getEp().equals("A")){
			claveEp = "437.112_ETM3.M.32";
		}
		String[] splitClaveEp = claveEp.split("\\.");
		out.println("				<tr>");
		out.println("				<th>"+splitClaveEp[0]+"</th>");
		out.println("				<th>"+splitClaveEp[1]+"</th>");
		out.println("				<th>"+splitClaveEp[2]+"</th>");
		out.println("				<th>"+splitClaveEp[3]+"</th>");
		out.println("				<th>"+splitSiaff[1]+"</th>");
		out.println("				<th>"+splitSiaff[2]+"</th>");
		out.println("				<th>"+splitSiaff[0]+"</th>");
		out.println("				<th>"+splitSiaff[3]+"</th>");
		out.println("				<th>"+splitSiaff[4]+"</th>");
		out.println("				<th>'"+splitSiaff[5]+"</th>");
		out.println("				<th>'"+splitSiaff[6]+"</th>");
		out.println("				<th>'"+splitSiaff[7]+"</th>");
		out.println("				<th>"+splitSiaff[8]+"</th>");
		out.println("				<th>"+splitSiaff[9].substring(0,1)+"</th>");
		out.println("				<th>"+splitSiaff[9].substring(1,2)+"</th>");
		out.println("				<th>"+splitSiaff[9].substring(2,3)+"</th>");
		out.println("				<th>'"+splitSiaff[9].substring(3,5)+"</th>");
		out.println("				<th>"+splitSiaff[10]+"</th>");
		out.println("				<th>"+splitSiaff[11]+"</th>");
		out.println("				<th>'"+splitSiaff[12]+"</th>");
		out.println("				<th>'"+splitSiaff[13]+"</th>");
		out.println("				<th>"+splitSiaff[14]+"</th>");
		//out.println("				<th>"+splitSiaff[15]+"</th>");
		out.println("				<th>'000</th>");
		out.println("				<th>'000</th>");
		out.println("				<th>'00000</th>");
		out.println("				<th>'00000</th>");
		out.println("				<th>'0000000000</th>");
		out.println("				<th>"+objSaldo.getMontoAnual()+"</th>");
		out.println("				<th>1</th>");
		out.println("				<th>"+objSaldo.getMontoEnero()+"</th>");
		out.println("				<th>"+objSaldo.getMontoFebrero()+"</th>");
		out.println("				<th>"+objSaldo.getMontoMarzo()+"</th>");
		out.println("				<th>"+objSaldo.getMontoAbril()+"</th>");
		out.println("				<th>"+objSaldo.getMontoMayo()+"</th>");
		out.println("				<th>"+objSaldo.getMontoJunio()+"</th>");
		out.println("				<th>"+objSaldo.getMontoJulio()+"</th>");
		out.println("				<th>"+objSaldo.getMontoAgosto()+"</th>");
		out.println("				<th>"+objSaldo.getMontoSeptiembre()+"</th>");
		out.println("				<th>"+objSaldo.getMontoOctubre()+"</th>");
		out.println("				<th>"+objSaldo.getMontoNoviembre()+"</th>");
		out.println("				<th>"+objSaldo.getMontoDiciembre()+"</th>");
		out.println("				</tr>");	
	}
	out.println("				</tbody>");
	out.println("			</table>");
	out.println("		</div>");
	out.println("	    </td>");
	out.println("  </tr>");
	out.println("</table>");

	out.println("</body>");
	out.println("</html>");
	out.flush();	
*/
/*try{
	
	//para dar el nombre del archivo
	java.util.Date date = new java.util.Date(); 
	java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
	String fecha = sdf.format(date);
	String sufijo = sdf.format(new Date(System.currentTimeMillis()));
	
	String layoutSicop = "LAYOUT-SICOP" + sufijo.trim() + ".csv";			
	String layoutSicopZip = "LAYOUT-SICOP" + sufijo.trim() + ".zip";
		
	java.io.BufferedWriter outp = new java.io.BufferedWriter(new java.io.FileWriter(layoutSicop));  			// Guarda el pago 
	StringBuffer archivoPago = new StringBuffer();
	for(int i=0; i <arrAdecuacion.size(); i++)
	{
		archivoPago.append(arrAdecuacion.get(i));
	}			
	String outTextPago = archivoPago.toString();  
	out.write(outTextPago);  
	out.close();			// fin de guarda pago
		
    byte[] bufPag = new byte[1024]; 												// Guarda el pago Zip
    try {	            
        java.util.zip.ZipOutputStream outPag = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(layoutSicopZip)); 
        java.io.FileInputStream inPag = new java.io.FileInputStream(layoutSicop);
        outPag.putNextEntry(new java.util.zip.ZipEntry(layoutSicop));
        
        int lenPag;
        while ((lenPag = inPag.read(bufPag)) > 0) {
        	outPag.write(bufPag, 0, lenPag);
        } 
		
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=\""+ layoutSicopZip +"\"");
        outPag.finish();
        outPag.close();												// Complete the ZIP file
        outPag.closeEntry();
        inPag.close();
    } catch (java.io.IOException e) {
    }

    java.io.File ficheroPag = new java.io.File(layoutSicop);	ficheroPag.delete();
    java.io.File ficheroPagZip = new java.io.File(layoutSicopZip);	ficheroPagZip.delete();

}		
catch (java.io.FileNotFoundException ex) {
	ex.printStackTrace();
}*/

%>
