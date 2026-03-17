<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.TablasBusinessLogic" %>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();
	Integer UR_Int = new Integer(UR.substring(1));
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			"jdbc/gestion");
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	String CentroContable = usuario.getPropiedad("CCENTROCONTABLE")
			.getValor();
%>
<%!private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log
						.info("Environment Entry \"dataSourceRefName\" nula usando default \""
								+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log
					.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
							+ jniName + "\"");
		}
	}

	public String getValor(String data) {
		return (data == null ? "" : data);
	}

	public int numeroTablas(ArrayList<ArrayList<String>> datos) {

		int numerofFilas = 0;
		String temp = "";

		for (int i = 0; i < datos.size(); i++) {
			if (temp.compareTo(datos.get(i).get(0)) != 0) {
				numerofFilas++;
				temp = datos.get(i).get(0);
			}
		}

		return numerofFilas;

	}

	public String getTableName(String nombre) {
		if (nombre.compareTo("TADECUACIONAUTDETALLE") == 0)
			return "Adecuaci&oacute;n Autorizada";
		else if (nombre.compareTo("TAPARTADODETALLE") == 0)
			return "Apartado";
		else if (nombre.compareTo("TOPERAJENASDETALLE") == 0)
			return "Operaciones Ajenas";
		else if (nombre.compareTo("TCANCELACOMPROMISODETALLE") == 0)
			return "Cancela Compromiso";
		else if (nombre.compareTo("TADECUACIONDETALLE") == 0)
			return "Detalle de Adecuaci&oacute;n";
		else if (nombre.compareTo("TSALDOS") == 0)
			return "Saldos";
		else if (nombre.compareTo("TEJERCIDODETALLE") == 0)
			return "Ejercido";
		else if (nombre.compareTo("TPAGADODETALLE") == 0)
			return "Pagado";
		else if (nombre.compareTo("TPAGOOBRADETALLE") == 0)
			return "Pago Obra";
		else if (nombre.compareTo("TCONTRATOEP") == 0)
			return "Contrato";
		else if (nombre.compareTo("TPRECOMPROMISOENCABEZADO") == 0)
			return "Precompromiso";
		else if (nombre.compareTo("MCONTRATO") == 0)
			return "MContrato";
		else if (nombre.compareTo("TCOMPROMISOENCABEZADO") == 0)
			return "Compromiso";
		else if (nombre.compareTo("PCONTRATODIVERSOANTICIPO") == 0)
			return "Anticipo Contrato Diverso";
		else if (nombre.compareTo("PCONTRATODIVERSO") == 0)
			return "Contrato Diverso";
		else if (nombre.compareTo("TDOCPOLIZADETALLE") == 0)
			return "P&oacute;liza";
		else if (nombre.compareTo("TSALDOSCONTABLES") == 0)
			return "Saldos Contables";
		else if (nombre.compareTo("TPAGODIRECTOENCABEZADO") == 0)
			return "Pago Directo";
		else if (nombre.compareTo("TPAGOOBRAENCABEZADO") == 0)
			return "Pago Obra";
		else if (nombre.compareTo("TRELACIONGASTOSDETALLE") == 0)
			return "Detalle Relaci&oacute;n de Gastos";
		else if (nombre.compareTo("TRELACIONGASTOSENCABEZADO") == 0)
			return "Relaci&oacute;n de Gastos ";
		else if (nombre.compareTo("TRELACIONGASTOFINANCIAMIENTO") == 0)
			return "Relaci&oacute;n de Gasto financiamiento ";
		else if (nombre.compareTo("TEJERCIDOENCABEZADO") == 0)
			return "Ejercido";
		else if (nombre.compareTo("TPAGADOENCABEZADO") == 0)
			return "Pagado";
		else if (nombre.compareTo("TADECUACIONAUTENCABEZADO") == 0)
			return "Adecuaci&oacute;n";
		else if (nombre.compareTo("TCOMPROMISODETALLE") == 0)
			return "Detalle de Compromiso";
		else if (nombre.compareTo("TOBRAPUBLICAAPARTADODETALLE") == 0)
			return "Apartado Obra P&uacute;blica";
		else if (nombre.compareTo("TOBRAPUBLICAPRECOMPROMISODETALLE") == 0)
			return "Precompromiso Obra P&uacute;blica";
		else if (nombre.compareTo("TOBRAPUBLICACOMPROMISODETALLE") == 0)
			return "Compromiso Obra P&uacute;blica";
        else if (nombre.compareTo("TNOMINACLCDETALLE") == 0)
			return "Detalle CLC N&oacute;mina";
        else if (nombre.compareTo("TNOMINADETALLE") == 0)
			return "N&oacute;mina";
        else if (nombre.compareTo("TCOMPROMISONOMINADETALLE") == 0)
			return "Detalle Compromiso N&oacute;mina";
        else if (nombre.compareTo("TCOMPROMISONOMINAENCABEZADO") == 0)
			return "Compromiso N&oacute;mina";
        else if (nombre.compareTo("PCONTRATODIVERSOCONVENIO") == 0)
			return "Convenio contrato diverso";
        else if (nombre.compareTo("TNOMINAENCABEZADO") == 0)
			return "N&oacute;mina";
		else if (nombre.compareTo("TCONTRARRECIBO") == 0)
			return "Contrarrecibo";	
        else if (nombre.compareTo("TCADENASPDETALLE") == 0)
			return "Cadenas Productivas";
        else if (nombre.compareTo("PCONTRATOOBRA") == 0)
			return "Contrato Obra";
        else if ((nombre.compareTo("TBENEFICIARIO") == 0)||(nombre.compareTo("CAT_BEN") == 0))
			return "Beneficiario";
       else if ((nombre.compareTo("TPAGODIRECTODETALLE") == 0))
			return "Pago Directo";
       else if (nombre.compareTo("TPAGOFEDERALIZADOENCABEZADO") == 0)
			return "Pago Federalizado";
       else if (nombre.compareTo("PCONTRATODIVERSORETENCION") == 0)
			return "Retenci&oacute;n Contrato Div";
       else if (nombre.compareTo("PCONTRATOOBRARETENCION") == 0)
			return "Retenci&oacute;n Contrato Obra";
       else if (nombre.compareTo("PCONTRATOOBRAANTICIPO") == 0)
			return "AnticipoContrato Obra";
       else if (nombre.compareTo("TREINTEGROAUTDETALLE") == 0)
			return "Reintegro Autorizado";
       else if (nombre.compareTo("TNOMINADEVDETALLE") == 0)
			return "Devolución N&oacute;mina";
       else if (nombre.compareTo("TREINTEGRODETALLE") == 0)
			return "Reintegro sin autorizar";
	   else if (nombre.compareTo("TPAGODIVERSOENCABEZADO") == 0)
			return "Pago Diverso";		
	   else if (nombre.compareTo("PCONTRATOFEDERALIZADO") == 0)
			return "Contrato Federalizado";		
	   else if (nombre.compareTo("PCONTRATOFEDERALIZADOANTICIPO") == 0)
			return "Ant. Cont. Federalizado";	
			
 
		return nombre;

	}%>	
	
	<%
				TablasBusinessLogic tb;
				String param[];

				tb = new TablasBusinessLogic(jniName);

				param = new String[8];

				for (int i = 0; i < 8; i++) {
					
					param[i] = request.getParameter("campo" + i);

				}

				ArrayList<ArrayList<String>> datos = tb.getTablas(param);
				tb = null;
				
				System.out.println("Tamaño datos:"+datos.size());

				ArrayList<String> fila;
				int tablas = numeroTablas(datos);

				ArrayList<String>[] encabezados = (ArrayList<String>[]) new ArrayList[tablas];
				ArrayList<String>[] tipoDatos = (ArrayList<String>[]) new ArrayList[tablas];
				String[] NombreTablas = new String[tablas];

				int numerofFilas = 0;
				int numTabla = 0;
				String temp = "";

				/*10101010101010110101010101010101010101010101010101010101*/
				/*Obteniendo numero de filas, encabezados y tipos de datos*/
				/*10101010101010110101010101010101010101010101010101010101*/
				for (int i = 0; i < datos.size(); i++) {

					if (temp.compareTo(temp = datos.get(i).get(0)) != 0) {

						NombreTablas[numTabla] = temp;
						numTabla++;

						encabezados[numerofFilas] = new ArrayList<String>();
						tipoDatos[numerofFilas] = new ArrayList<String>();

						for (int in = 2; in < datos.get(i).size(); in++) {

							encabezados[numerofFilas].add(datos.get(i).get(in));
							tipoDatos[numerofFilas].add(datos.get(i + 1).get(in));

						}

						numerofFilas++;
					}

				}
			%>
	

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>

	<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
	
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
	
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
	<link rel="stylesheet" type="text/css" 	href="../css/menuContabilidad.css"></link>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

 
<script>


<%
for (int t = 0; t < tablas; t++) {

}

%> 



function ntablas()
{
var numTablas=<%=tablas%> 
return numTablas;
}




<%int fil = 0;
			int k = 0;
			temp = "";

			for (int i = 2; i < datos.size(); i++) {
				if (temp.compareTo(datos.get(i).get(0)) != 0) {

					if (i > 2) {
						i += 2;
						++k;

					}
					fil = 0;
					out.println("var arraydataT" + k + "= new Array();");

					temp = datos.get(i).get(0);
				}

				out.print("arraydataT" + k + "[" + (fil++) + "]= new Array(");
				for (int c = 2; c < datos.get(i).size(); c++) {

					if (c > 2) {
						out.print(",");
					}
					out.print("'"
							+ datos.get(i).get(c).replaceAll("\\|","*").replaceAll("\n", "")
									.replaceAll("\r", "") + "'");
                    
				}
				System.out.println("");
				out.println(");");

			}

			datos = null;%>







 //Inicia llamado de pagina
	$(document).ready(function() {
	
   
   
<%for (int t = 0; t < tablas; t++) {%> 
		 
	var oTable<%=t%>=$("#datat<%=t%>").dataTable({
			bPaginate : false,
			bLengthChange :false,
			bInfo : false,
			sScrollX : "850px",
			sScrollY : "400px",
			bJQueryUI : true,
			bFilter : true,
			bSort : true,
			bAutoWidth : true,
			sWidth: "100%",
			
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "Por favor espere mientras se cargan los datos...",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtado de _MAX_ registros)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			},
			aaData:<%out.print("arraydataT" + t);%>
			
			});
			
           <%}%>		

		
		
		muestraTabla(0);
	
		}); //Finaliza llamado de pagina		





function muestraTabla(n)
{



 var i;
  for(i=0;i<ntablas();i++)
  {
  $('#datat'+i+'').parents('div.dataTables_wrapper').first().hide();
  }
  
  $('#datat'+n+'').parents('div.dataTables_wrapper').first().show();
  
 
}


function checks()
 {
  var i;
  var tablasCount=0;
  var nombreTablas="";
  var pipe="";
  var pipe2="";
  var numColsxTabla="";
  var oTable;
  var headers;
  var ColumnasHeader="";
  var totalColumnasTemp=0;
  var encabezadosTabla=new Array();
  var formatosTabla=new Array();
  var tableNames=new Array();
  var datosTemp="";
  var table=0;

<%String pipe = "|";
			String header = "";
			String formato = "";
			int t = 0;

			for (t = 0; t < tablas; t++) {
				out.print("tableNames[" + t + "]=\"");
				out.print(getTableName(NombreTablas[t]));
				out.println("\";");
				out.println();
			}

			for (t = 0; t < tablas; t++) {

				out.print("encabezadosTabla[" + t + "]=\"");

				header += encabezados[t].get(0);

				for (int h = 1; h < encabezados[t].size(); h++)
					header += pipe + encabezados[t].get(h);

				out.println(header + "\";");
				header = "";

			}

			for (t = 0; t < tablas; t++) {

				out.print("formatosTabla[" + t + "]=\"");
				formato += tipoDatos[t].get(0);

				for (int h = 1; h < tipoDatos[t].size(); h++)
					formato += pipe + tipoDatos[t].get(h);

				out.println(formato + "\";");

				formato = "";

			}%> 
  
  
  
  
   for(i=0;i<ntablas();i++)
     {
      if (document.getElementById('chk'+i+'').checked) {
      if(tablasCount>0)
      {
      pipe="|"
      }
       tablasCount++;
       
       ///////////////////////////////////////////////////////////////////////////////////////////////////////////
       //nombreTablas+=pipe+ document.getElementById('chk'+i+'').value; //llenando el nombre de las tablas
       nombreTablas+=pipe+tableNames[i];
       ///////////////////////////////////////////////////////////////////////////////////////////////////////////
       
       
       oTable = $('#datat'+i+'').dataTable();
       totalColumnasTemp=oTable.fnGetData(0).length;
       numColsxTabla+=pipe+totalColumnasTemp; //llenando el número de columnas por tabla
       document.getElementById('encabezadosTabla'+table+'').value=encabezadosTabla[i];//poniendo los encabezados a la tabla x
       document.getElementById('formatosTabla'+table+'').value=formatosTabla[i];//poniendo los formatos a la tabla x
      
       for(var r=0;r<oTable.fnGetData().length;r++)
    {
        
        for(var c=0;c <totalColumnasTemp;c++)
        {
        if((c>0)||(r>0)){pipe2="|";}
          datosTemp+=pipe2+oTable.fnGetData(r,c);   
        }
        pipe2="";
        
    }
        
       
       document.getElementById('tabla'+table+'').value = datosTemp;
      
       datosTemp="";
       
       table++;
    
    
     }
     
   }
   
   
   document.getElementById("tTablas").value = tablasCount;
   document.getElementById("titulosxTabla").value = nombreTablas;
   document.getElementById("colsxTabla").value=numColsxTabla
   
   document.formulario.submit();
}



</script>





<title>Reportes Dinamicos </title>
</head>



	<body id="dt_example" >
	

 <!--<div  id="container" class="container SyCData">-->




	<h1>Generaci&oacute;n de reportes Din&aacute;micos</h1>
	
     <form action="../reports/GeneraExcelTable" method="post" id="formulario" name="formulario">
    <input type="hidden" name="colsxTabla" id="colsxTabla" /> <!-- Número de columnas por cada tabla separada por pipes | -->
	<input type="hidden" name="titulosxTabla" id="titulosxTabla" /><!-- Titulo de cada tabla, seran los nombres de las hojas -->
	<input type="hidden" name="tTablas" id="tTablas" /><!-- El total de tablas -->


			<%
				for (t = 0; t <= tablas; t++) {

					out.println("<input type=\"hidden\" name=\"tabla" + t
							+ "\" id=\"tabla" + t + "\" />");
					out.println("<input type=\"hidden\" name=\"encabezadosTabla"
							+ t + "\" id=\"encabezadosTabla" + t + "\" />");
					out.println("<input type=\"hidden\" name=\"formatosTabla" + t
							+ "\" id=\"formatosTabla" + t + "\" />");

				}
			%>




			<div id="ContenedorDtables" >

				<div id="izquierda">


					<%
						for (t = 0; t < tablas; t++) {

							out.println("<table id=\"datat"+t+"\"  class=\"display\">");
							out.println("<thead>");
							out.println("<tr>");

							for (int h = 0; h < encabezados[t].size(); h++) {
								out.println("<th>" + encabezados[t].get(h)+ "</th>");
							}

							out.println(" </tr>");
							out.println("</thead>");
							out.println(" <tbody>");
							out.println("</tbody>");
							out.println("</table>");

						}
						encabezados = null;
					%>

				</div>
				<div id="derecha">
					Documentos:
					<table id="tdocs" border="0">
						<tr></tr>
						<%
							for (int i = 0; i < NombreTablas.length; i++) {
								if (i % 2 == 0)
									out.println("<tr id=\"lineaImpar\" onclick=\"muestraTabla("
											+ i + ")\">");
								else
									out.println("<tr onclick=\"muestraTabla(" + i + ")\">");
								out.println("<td width=\"20\">");
								out.println("<input name=\"chk" + i
										+ "\" type=\"checkbox\" id=\"chk" + i + "\" value=\""
										+ getTableName(NombreTablas[i]) + "\" />");
								out.println("</td >");
								out.println("<td ><label id=\"tabla" + i + "\">"
										+ getTableName(NombreTablas[i]) + "</label></td>");
								out.println("</tr>");

							}
							NombreTablas = null;
						%>


					</table>
					<input type="button" name="crearExcel" value="Crear Excel"
						onclick="checks()" />

				</div>
			</div>
  
  
		
</form>	
	
<!--  </div>-->



</body>

</html>
