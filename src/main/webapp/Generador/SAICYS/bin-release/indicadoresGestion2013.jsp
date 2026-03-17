<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.reportes.reporteGraficas"%>
<%@page import="com.syc.adquisiciones.servlet.generaXML"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.io.*"%>
<%@page import="java.text.*" %>
<%@page import="java.io.*"%>
<%!
	private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}

	public String getValor(String data){
		return (data == null? "": data);
	}
	
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- saved from url=(0014)about:internet -->
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en"> 
    <!-- 
    Smart developers always View Source. 
    
    This application was built using Adobe Flex, an open source framework
    for building rich Internet applications that get delivered via the
    Flash Player or to desktops via Adobe AIR. 
    
    Learn more about Flex at http://flex.org 
    // -->
    <head>
        <title></title>
        <meta name="google" value="notranslate" />         
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <!-- Include CSS to eliminate any default margins/padding and set the height of the html element and 
             the body element to 100%, because Firefox, or any Gecko based browser, interprets percentage as 
             the percentage of the height of its parent container, which has to be set explicitly.  Fix for
             Firefox 3.6 focus border issues.  Initially, don't display flashContent div so it won't show 
             if JavaScript disabled.
        -->
        <style type="text/css" media="screen"> 
            html, body  { height:100%; }
            body { margin:0; padding:0; overflow:auto; text-align:center; 
                   background-color: #ffffff; }   
            object:focus { outline:none; }
            #flashContent { display:none; }
        </style>
        
        <!-- Enable Browser History by replacing useBrowserHistory tokens with two hyphens -->
        <!-- BEGIN Browser History required section -->
        <link rel="stylesheet" type="text/css" href="history/history.css" />
        <script type="text/javascript" src="history/history.js"></script>
        <!-- END Browser History required section -->  
            
        <script type="text/javascript" src="swfobject.js"></script>
        <script type="text/javascript">
            // For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection. 
            var swfVersionStr = "11.1.0";
            // To use express install, set to playerProductInstall.swf, otherwise the empty string. 
            var xiSwfUrlStr = "playerProductInstall.swf";
            var flashvars = {};
            var params = {};
            params.quality = "high";
            params.bgcolor = "#ffffff";
            params.allowscriptaccess = "sameDomain";
            params.allowfullscreen = "true";
            var attributes = {};
            attributes.id = "indicadoresGestion";
            attributes.name = "indicadoresGestion";
            attributes.align = "middle";
            swfobject.embedSWF(
                "indicadoresGestion2013.swf", "flashContent", 
               "1800", "1900",  
                swfVersionStr, xiSwfUrlStr, 
                flashvars, params, attributes);
            // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
            swfobject.createCSS("#flashContent", "display:block;text-align:left;");
           
			 <%			 		
				String ruta=getServletContext().getRealPath("Generador");
				DateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();	
				String cadenaFecha = formato.format(date);
			//	File fichero;
				/////////////fecha del fichero
				File fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\montosCapituloPA2013.xml");
				long ms = fichero.lastModified();
				Date d = new Date(ms);
				String fechaArchivo = formato.format(d);

				if (cadenaFecha.equals(fechaArchivo)){
					System.out.println("El fichero " + fichero + " existe");
				}else{
					String strXMLCap="";
			   		reporteGraficas rblCap = new reporteGraficas(jniName);
			   		strXMLCap = "<chart caption='Programa Anual por capitulo' subCaption='' bgAlpha='100'  showValues='1' numberPrefix='$' exportShowMenuItem='1' exportEnabled='1' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
			   		"\t";
			   		strXMLCap += rblCap.graficaPresupuesto(11);
			   		strXMLCap += "</chart>";
			   		generaXML xmlCap = new generaXML();//
			   		int xCap=xmlCap.generaArchivo(strXMLCap,"montosCapituloPA2013.xml",ruta);
				}
				fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\presupuestoUnidad2013.xml");
 				ms = fichero.lastModified();
				d = new Date(ms);
				fechaArchivo = formato.format(d);
				//if (fichero.exists()){
				if (cadenaFecha.equals(fechaArchivo)){
		 			System.out.println("El fichero " + fichero + " existe");
				}else{
					//programado por unidad ejecutora
			   		String strXML="";
			   		reporteGraficas rbl = new reporteGraficas(jniName);
			   		strXML = "<chart caption='Presupuesto Original' subCaption='Por Unidad Ejecutora' enableRotation='1'  exportShowMenuItem='1' exportEnabled='1' bgAlpha='100' showValues='1' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
			   		"\t";
			   		strXML += rbl.graficaPresupuesto(1);
			   		strXML += "</chart>";
			   		
			   		generaXML xml = new generaXML();//strXML,,ruta
			   		int x=xml.generaArchivo(strXML,"presupuestoUnidad2013.xml",ruta);
				}
				fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\programadoUnidadEjecutora2013.xml");
	 			ms = fichero.lastModified();
				d = new Date(ms);
				fechaArchivo = formato.format(d);
				//if (fichero.exists()){
				if (cadenaFecha.equals(fechaArchivo)){
		 			System.out.println("El fichero " + fichero + " existe");
				}else{
					/// tabla programa anual
			   		String strXMLPA="";
			   		reporteGraficas rblPA = new reporteGraficas(jniName);
			   		strXMLPA = "<?xml version='1.0' encoding='UTF-8'?>"+
			   		"\t"+
			   		"<programados>"+
			   		"\t";
			   		strXMLPA += rblPA.graficaPresupuesto(3);
			   		strXMLPA += "</programados>";
			   		generaXML xmlPA = new generaXML();
			   		int xPA=xmlPA.generaArchivo(strXMLPA,"programadoUnidadEjecutora2013.xml",ruta);
				}
			    fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\requisicionesUnidad2013.xml");
				ms = fichero.lastModified();
				d = new Date(ms);
				fechaArchivo = formato.format(d);
	
				if (cadenaFecha.equals(fechaArchivo)){
				//if (fichero.exists()){
					 System.out.println("El fichero " + fichero + " existe");
				}else{
				/////// REQUISICION/////////
			   		String strXMLReq="";
			   		reporteGraficas rblReq = new reporteGraficas(jniName);
			   		strXMLReq = "<chart caption='Montos en Requisiciones' subCaption='Por Capitulo' enableRotation='1'  exportEnabled='1' bgAlpha='100' showValues='1' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
			   		"\t";
			   		strXMLReq += rblReq.graficaPresupuesto(4);
			   		strXMLReq += "</chart>";
			   //		session.setAttribute(GestionInterface.ATT_cadenaXML,strXMLReq);
			   		generaXML xmlReq= new generaXML();//
			   		int xReq=xmlReq.generaArchivo(strXMLReq,"requisicionesUnidad2013.xml",ruta);
				}
				 fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\tablaRequisiciones2013.xml");
				 ms = fichero.lastModified();
				 d = new Date(ms);
				 fechaArchivo = formato.format(d);
				 //if (fichero.exists()){
				 if (cadenaFecha.equals(fechaArchivo)){
				 //if (fichero.exists()){
					 System.out.println("El fichero " + fichero + " existe");
				 }else{
					///tabla requisicion
			   		String strXMLReqTbl="";
			   		reporteGraficas rblReqTbl = new reporteGraficas(jniName);
			   		strXMLReqTbl = "<?xml version='1.0' encoding='UTF-8'?>"+
			   		"<requisiciones>"+
			   		"\t";
			   		strXMLReqTbl += rblReqTbl.graficaPresupuesto(5);
			   		strXMLReqTbl += "</requisiciones>";
			   		generaXML xmlReqTbl= new generaXML();//
			   		int xReqTbl=xmlReqTbl.generaArchivo(strXMLReqTbl,"tablaRequisiciones2013.xml",ruta);
				}
			 fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\consolidado2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			 fechaArchivo = formato.format(d);
			 if (cadenaFecha.equals(fechaArchivo)){
				System.out.println("El fichero " + fichero + " existe");
			 }else{
			///// CONSOLIDADO POR CAPITULO/////
		  		String strXMLConsol="";
		  		reporteGraficas rblConsol = new reporteGraficas(jniName);
		  		strXMLConsol = "<chart caption='Montos en Consolidado' subCaption='Por Capitulo' enableRotation='1'  exportEnabled='1' bgAlpha='100' showValues='1' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
		  		"\t";
		  		strXMLConsol += rblConsol.graficaPresupuesto(6);
		  		strXMLConsol += "</chart>";
		  		generaXML xmlConsol = new generaXML();//
		  		int xConsol=xmlConsol.generaArchivo(strXMLConsol,"consolidado2013.xml",ruta);
			}
			 fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\alcanceConsolidado2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			//if (fichero.exists()){
			if (cadenaFecha.equals(fechaArchivo)){
			//if (fichero.exists()){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
		   		///CONSOLIDADO POR ALCANCE
		  		 String strXMLConsolAlc="";
		  		reporteGraficas rblConsolAlc = new reporteGraficas(jniName);
		  		strXMLConsolAlc = "<chart caption='Montos en Consolidado' subCaption='Por Alcance' enableRotation='1'  exportEnabled='1' bgAlpha='100' showValues='1' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
		  		"\t";
		  		strXMLConsolAlc += rblConsolAlc.graficaPresupuesto(7);
		  		strXMLConsolAlc += "</chart>";
		  		generaXML xmlConsolAlc = new generaXML();//
		  		int xConsolAlc=xmlConsolAlc.generaArchivo(strXMLConsolAlc,"alcanceConsolidado2013.xml",ruta);
			}			   		
			fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\tablaConsolidado2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			//if (fichero.exists()){
			if (cadenaFecha.equals(fechaArchivo)){
			//if (fichero.exists()){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
			////TABLA CONSOLIDADO
		   		String strXMLConsolTbl="";
		   		reporteGraficas rblConsolTbl = new reporteGraficas(jniName);
		   		strXMLConsolTbl = "<?xml version='1.0' encoding='UTF-8'?>"+
		   		"<consolidados>\t";
		   		strXMLConsolTbl += rblConsolTbl.graficaPresupuesto(8);
		   		strXMLConsolTbl += "</consolidados>";
		   		generaXML xmlConsolTbl = new generaXML();//
		   		int xConsolTbl=xmlConsolTbl.generaArchivo(strXMLConsolTbl,"tablaConsolidado2013.xml",ruta);
		   	}			   	
			fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\procedimiento2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			//if (fichero.exists()){
			if (cadenaFecha.equals(fechaArchivo)){
			//if (fichero.exists()){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
		   		///PROCEDIMIENTO////
		   		String strXMLProc="";
		   		reporteGraficas rblProc = new reporteGraficas(jniName);
		   		strXMLProc = "<chart caption='Procedimientos realizados' subCaption='por tipo de procedimiento de adquisicion' enableRotation='1' exportShowMenuItem='1' exportEnabled='1'  bgAlpha='100' showValues='0' numberPrefix='$' showPercentInToolTip='0' formatNumber='1' formatNumberScale='0' decimals='2'>"+
		   		"\t";
		   		strXMLProc += rblProc.graficaPresupuesto(9);
		   		strXMLProc += "</chart>";
		   		generaXML xmlProc = new generaXML();//
		   		int xProc=xmlProc.generaArchivo(strXMLProc,"procedimiento2013.xml",ruta);
		   	}			   		
			fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\tablaProcedimiento2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			//if (fichero.exists()){
			if (cadenaFecha.equals(fechaArchivo)){
		//	if (fichero.exists()){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
				///tabla procedimiento
		   		String strXMLProcTbl="";
		   		reporteGraficas rblProcTbl = new reporteGraficas(jniName);
		   		strXMLProcTbl = "<?xml version='1.0' encoding='UTF-8'?>"+
		   		"<procedimientos>"+
		   		"\t";
		   		strXMLProcTbl += rblProcTbl.graficaPresupuesto(10);
		   		strXMLProcTbl += "</procedimientos>";
		   		generaXML xmlProcTbl = new generaXML();//
		   		int xProcTbl=xmlProcTbl.generaArchivo(strXMLProcTbl,"tablaProcedimiento2013.xml",ruta);
		   		
		   	}				   		
			fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\pedidoCapitulo2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			//if (fichero.exists()){
			if (cadenaFecha.equals(fechaArchivo)){
			//if (fichero.exists()){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
		
		   		////PEDIDOS//// grafica gral /// capitulo
		   		String strXMLPed="";
		   		reporteGraficas rblPed = new reporteGraficas(jniName);
		   		strXMLPed = "<chart caption='Montos en pedido' subCaption='Por capitulo' bgAlpha='100' enableRotation='1'  exportEnabled='1' exportShowMenuItem='1' showValues='1' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
		   		"\t";
		   		strXMLPed += rblPed.graficaPresupuesto(14);
		   		strXMLPed += "</chart>";
		   		generaXML xmlPed = new generaXML();//
		   		int xPed=xmlPed.generaArchivo(strXMLPed,"pedidoCapitulo2013.xml",ruta);
		   	}				   		
			fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\pedido2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			if (cadenaFecha.equals(fechaArchivo)){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
				 ///pedidos rfc
		   		String strXMLPedRFC="";
		   		reporteGraficas rblPedRFC = new reporteGraficas(jniName);
		   		strXMLPedRFC = "<chart caption='Proveedores con mayor monto adjudicado' subCaption='' bgAlpha='100' showValues='0' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
		   		"\t";
		   		strXMLPedRFC += rblPedRFC.graficaPresupuesto(12);
		   		strXMLPedRFC += ""+
		   		"</chart>";
		   		generaXML xmlPedRFC = new generaXML();//
		   		int xPedRFC=xmlPedRFC.generaArchivo(strXMLPedRFC,"pedido2013.xml",ruta);
		   	}				   		
			fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\tablaPedido2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			if (cadenaFecha.equals(fechaArchivo)){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
				//tabla de pedido
		   		String strXMLPedTbl="";
		   		reporteGraficas rblPedTbl = new reporteGraficas(jniName);
		   		strXMLPedTbl="<?xml version='1.0' encoding='UTF-8'?>"+
		   		"<pedidos>"+
		   		"\t";
		   		strXMLPedTbl += rblPedTbl.graficaPresupuesto(13);
		   		strXMLPedTbl += "</pedidos>";
		   		generaXML xmlPedTbl = new generaXML();//
		   		int xPedTbl=xmlPedTbl.generaArchivo(strXMLPedTbl,"tablaPedido2013.xml",ruta);
		   	}
		/////pedidos por categoria
		    fichero = new File(ruta+"\\SAICYS\\bin-release\\data\\tablaPedidosCat2013.xml");
			 ms = fichero.lastModified();
			 d = new Date(ms);
			fechaArchivo = formato.format(d);
			//if (fichero.exists()){
			if (cadenaFecha.equals(fechaArchivo)){
		//	if (fichero.exists()){
				 System.out.println("El fichero " + fichero + " existe");
			}else{
				//tabla de pedido
		  		String strXMLPedTbl="";
		  		reporteGraficas rblPedTbl = new reporteGraficas(jniName);
		  		strXMLPedTbl="<chart caption='Montos en pedido por categoria' subCaption='' bgAlpha='100' showValues='1' numberPrefix='$' showPercentInToolTip='1' formatNumber='1' formatNumberScale='0' decimals='2'>"+
		  		"\t";
		  		strXMLPedTbl += rblPedTbl.graficaPresupuesto(15);
		  		strXMLPedTbl += "</chart>";
		  		generaXML xmlPedTbl = new generaXML();//
		  		int xPedTbl=xmlPedTbl.generaArchivo(strXMLPedTbl,"tablaPedidosCat2013.xml",ruta);
		  	}
		%>

        </script>
    </head>
    <body>
        <!-- SWFObject's dynamic embed method replaces this alternative HTML content with Flash content when enough 
             JavaScript and Flash plug-in support is available. The div is initially hidden so that it doesn't show
             when JavaScript is disabled.
        -->
        <div id="flashContent">
            <p>
                To view this page ensure that Adobe Flash Player version 
                11.1.0 or greater is installed. 
            </p>
            <script type="text/javascript"> 
                var pageHost = ((document.location.protocol == "https:") ? "https://" : document.location.protocol + "//" ); 
                document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
                                + pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
            </script> 
        </div>
          <input id="cEjercicio" name="cEjercicio" value=""  type="hidden" size="10">
        <noscript>
            <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="1710" height="1800" id="indicadoresGestion">
                <param name="movie" value="indicadoresGestion2013.swf" />
                <param name="quality" value="high" />
                <param name="bgcolor" value="#ffffff" />
                <param name="allowScriptAccess" value="sameDomain" />
                <param name="allowFullScreen" value="true" />
                <!--[if !IE]>-->
                <object type="application/x-shockwave-flash" data="indicadoresGestion2013.swf" width="1710" height="1800">
                    <param name="quality" value="high" />
                    <param name="bgcolor" value="#ffffff" />
                    <param name="allowScriptAccess" value="sameDomain" />
                    <param name="allowFullScreen" value="true" />
                <!--<![endif]-->
                <!--[if gte IE 6]>-->
                    <p> 
                        Either scripts and active content are not permitted to run or Adobe Flash Player version
                        11.1.0 or greater is not installed.
                    </p>
                <!--<![endif]-->
                    <a href="http://www.adobe.com/go/getflashplayer">
                        <img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash Player" />
                    </a>
                <!--[if !IE]>-->
                </object>
                <!--<![endif]-->
            </object>
        </noscript>  
            
   </body>
</html>
