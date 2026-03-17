<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>Consultas Din&aacute;micas</title>

	<style type="text/css" title="currentStyle">
		@import "../Generador/css/demo_page.css";
		@import "../Generador/css/demo_table_jui.css";
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>

<!-- 		<script type="text/javascript" src="../js/datepickercontrol.js"></script> -->
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>

<!-- 		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css"> -->
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css">
        <link rel="stylesheet" type="text/css" href="../css/reportes.css">
		
		
        <link rel="stylesheet" type="text/css" 	href="../css/menuContabilidad.css"></link>
   
  <%
  
  Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

String CentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor() ;
  
  %>     
   
		<script type="text/javascript">
	var dTable;
    var tipoReporte;
    var CentroContable="<%=CentroContable%>";
    
	$(document)
			.ready(
					function() {
						
						cambiaEtiqueta(0);
						
						$("input.AyudaSyC").subIniciaDlg();
		            $("input.autoCompletaSyC").subIniciaAutoCompleta();
					
						
     document.getElementById('hcCentroContable').value=CentroContable;
     document.getElementById('hcIdEntidadContable').value=CentroContable;
					 
     	
										
					});
					
function cambiaEtiqueta(mostrar)
{
var etiqueta=new Array();
etiqueta[0]="cIdContrato";
etiqueta[1]="ep";
etiqueta[2]="cuentas";
etiqueta[3]="contrarrecibo";
etiqueta[4]="compromiso";
etiqueta[5]="beneficiario";
etiqueta[6]="cidDocumento";
etiqueta[7]="NoFactura";
etiqueta[8]="idrelacion";
etiqueta[9]="foliosiaff";
etiqueta[10]="map";
etiqueta[11]="foliosicop";
etiqueta[12]="estimacion";
	
	var i=0;
	
	for(i=0;i<etiqueta.length;i++)
	{
	$('#'+etiqueta[i]).hide();
	}
	
	
	$('#'+etiqueta[mostrar]).show();
	
	tipoReporte=mostrar;
	
	
}

function crearReporte()
{

    document.getElementById('campo4').value='ccentrocontable';
	document.getElementById('campo5').value='entidadcontable';
	document.getElementById('campo6').value='';
	if(CentroContable==0)
	{
	document.getElementById('campo7').value='%';
	}
	else
	{
	document.getElementById('campo7').value=CentroContable;
	}
	
 
 
	if(tipoReporte==0)
	{
	document.getElementById('campo0').value='%cIdContrato%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value=''+document.getElementById('hbuscacIdContrato').value+'';
	}
  	if(tipoReporte==1)
	{
	document.getElementById('campo0').value='%EP%';
	document.getElementById('campo1').value='%subcuenta%';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaEP').value+'%';
	}
  	if(tipoReporte==2)
	{
	document.getElementById('campo0').value='%ncuenta%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaCuentas').value+'%';
	}
	if(tipoReporte==3)
	{
	document.getElementById('campo0').value='%caNoContrarrecibo%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaContrarrecibo').value+'%';
	}
  	if(tipoReporte==4)
	{
	document.getElementById('campo0').value='%compromiso%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaCompromiso').value+'%';
	}
	 if(tipoReporte==5)
	{
	document.getElementById('campo0').value='%dRFC%';
	document.getElementById('campo1').value='%subcuenta%';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscabeneficiario').value+'%';
	}
  		if(tipoReporte==6)
	{
	document.getElementById('campo0').value='%cidDocumento%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscacIdDocumento').value+'%';
	}
		if(tipoReporte==7)
	{
	document.getElementById('campo0').value='%NoFactura%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscacNoFactura').value+'%';
	
	}
		if(tipoReporte==8)
	{
	document.getElementById('campo0').value='cIdRelacion';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscacIdRelacion').value+'%';
	//document.getElementById('campo5').value='';
	}
		if(tipoReporte==9)
	{
	document.getElementById('campo0').value='nFolioSIAFF';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaSIAFF').value+'%';
	document.getElementById('campo4').value='cRamo';
	//document.getElementById('campo5').value='';
	document.getElementById('campo6').value='';
	document.getElementById('campo7').value='16';
	}
	if(tipoReporte==10)
	{
	document.getElementById('campo0').value='%MAP%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaMAP').value+'%';
	document.getElementById('campo4').value='cRamo';
	//document.getElementById('campo5').value='';
	document.getElementById('campo6').value='';
	document.getElementById('campo7').value='16';
	}
  		if(tipoReporte==11)
	{
	document.getElementById('campo0').value='nFolioSICOP';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%'+document.getElementById('hbuscaSICOP').value+'%';
	document.getElementById('campo4').value='cRamo';
	//document.getElementById('campo5').value='';
	document.getElementById('campo6').value='';
	document.getElementById('campo7').value='16';
	}
		if(tipoReporte==12)
	{
	document.getElementById('campo0').value='%Estimacion%';
	document.getElementById('campo1').value='';
	document.getElementById('campo2').value='';
	document.getElementById('campo3').value='%ANT%';
	}
	

  	document.getElementById('buscando').style.visibility="visible";
  	
  	document.getElementById('formulario').style.visibility="hidden";
  	
 document.formulario.submit();
   

}					
					
					
					
</script>
	</head>
	<body id="dt_example">
	
	<div id="buscando" style="visibility:hidden ">
	<div id="bloque">
	<img src="../imagenes/espera.gif"/><br>Por favor espere mientras se busca informaci&oacute;n en el sistema....
	</div>
	</div>
	
	
	<div id="formulario">
		<form action="reportesDinamicos.jsp" target="content-iframe"
			method="post" id="formulario" name="formulario">
			
			
				<h1>
				<label id="titulo">Consultas Din&aacute;micas</label>
			</h1>
			<div id="container" class="container SyCData">
				
		<table width="500px" border="0" id="tablaContenedora">
  <tr>
    <th width="150px" scope="col" class="fondotableTitle">Criterios de selecci&oacute;n:</th>
    <th width="150px"scope="col" class="fondotableTitle">Campos de B&uacute;squeda: </th>
  </tr>
  <tr>
    <td><table width="150px" border="0" id="tdocs">
      <tr>
        <td onclick="cambiaEtiqueta(0);" >CONTRATO</td>
      </tr>
      <tr class="lineaImpar">
        <td onclick="cambiaEtiqueta(1);">EP</td>
      </tr>
      <tr>
        <td onclick="cambiaEtiqueta(2);">CUENTA CONTABLE </td>
      </tr>
      <tr class="lineaImpar">
        <td onclick="cambiaEtiqueta(3);">CONTRARECIBO</td>
      </tr>
      <tr>
        <td onclick="cambiaEtiqueta(4);">COMPROMISO</td>
      </tr>
      <tr class="lineaImpar">
        <td onclick="cambiaEtiqueta(5);">RFC</td>
      </tr>
      <tr>
        <td onclick="cambiaEtiqueta(6);">ID DOCUMENTO </td>
      </tr>
      <tr class="lineaImpar">
        <td onclick="cambiaEtiqueta(7);" >FACTURA</td>
      </tr>
      <tr>
        <td onclick="cambiaEtiqueta(8);">ID RELACI&Oacute;N </td>
      </tr>
      <tr  class="lineaImpar">
        <td onclick="cambiaEtiqueta(9);">FOLIO SIAFF </td>
      </tr>
      <tr>
        <td onclick="cambiaEtiqueta(10);">FOLIO MAP </td>
      </tr>
      <tr class="lineaImpar">
        <td onclick="cambiaEtiqueta(11);">FOLIO SICOP </td>
      </tr>
      <tr >
        <td onclick="cambiaEtiqueta(12);">ESTIMACI&Oacute;N </td>
      </tr>
    </table></td>
    <td><table width="350px" border="0">
      <tr>
        <td><label id="nEtiqueta"></label> </td>
      </tr>
      
      <tr>
        <td>
          <input id="campo0"   name="campo0" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo1"   name="campo1" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo2"   name="campo2" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo3"   name="campo3" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo4"   name="campo4" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo5"   name="campo5" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo6"   name="campo6" value="" size="30" maxlength="30" class="" type="hidden">
          <input id="campo7"   name="campo7" value="" size="30" maxlength="30" class="" type="hidden">
         
        
         <input id="hcCentroContable"   name="hcCentroContable" value="0" size="2" maxlength="2" class="" type="hidden">
         <input id="hcIdEntidadContable" name="hcIdEntidadContable" value="0" size="2" maxlength="2" class="" type="hidden">
         <input id="hbuscadCuentas" name="hbuscadCuentas" value="" size="30" maxlength="30"class="" type="hidden"> 
        
     
        <div id="contrarrecibo">
        <label >Contrarrecibo</label> <br>
        <input id="hbuscaContrarrecibo" name="hbuscaContrarrecibo" value="" size="15" maxlength="20" class="AyudaSyC autoCompletaSyC " type="text">
        </div>
        
        <div id="cidDocumento">
        <label >Documento</label> <br>
        <input id="hbuscacIdDocumento" name="hbuscacIdDocumento" value="" size="20" maxlength="30" class="AyudaSyC autoCompletaSyC " type="text">
        </div>
         
        <div id="NoFactura">
        <label >No Factura</label> <br> 
        <input id="hbuscacNoFactura" name="hbuscacNoFactura" value="" size="10" maxlength="15" class="AyudaSyC autoCompletaSyC " type="text">
        </div>
        
        <div id="cIdContrato">
        <label >Contrato</label> <br>
        <input id="hbuscacIdContrato" name="hbuscacIdContrato" value="" size="30" maxlength="35" class="AyudaSyC autoCompletaSyC" type="text"> 
         </div>
         
        <div id="cuentas">
        <label >Cuenta</label><br> 
        <input id="hbuscaCuentas" name="hbuscaCuentas"  value="" size="25" maxlength="30" class="AyudaSyC autoCompletaSyC" type="text"> 
        </div>
        
        <div id="idrelacion">
        <label >Id Relación</label><br> 
        <input id="hbuscacIdRelacion" name="hbuscacIdRelacion" value="" size="20" maxlength="25" class="AyudaSyC autoCompletaSyC " type="text">  
        </div>
        
        <div id="compromiso">
        <label >Compromiso</label> <br>
        <input id="hbuscaCompromiso" name="hbuscaCompromiso" value="" size="15" maxlength="20" class=" AyudaSyC autoCompletaSyC" type="text">  
        </div>
        
        <div id="ep">
        <label >EP</label> <br>
        <input id="hbuscaEP" name="hbuscaEP" value="" size="62" maxlength="68" class="AyudaSyC autoCompletaSyC " type="text">  
        </div>
        
        <div id="map">
        <label >No. MAP</label> <br>
        <input id="hbuscaMAP" name="hbuscaMAP" value="" size="20" maxlength="25" class="AyudaSyC autoCompletaSyC " type="text">  
        </div>
        
        
        <div id="foliosiaff">
        <label >Folio SIAFF</label> <br>
        <input id="hbuscaSIAFF" name="hbuscaSIAFF" value="" size="6" maxlength="10" class="AyudaSyC autoCompletaSyC" type="text">  
        </div>
        
        
        <div id="foliosicop">
        <label >Folio SICOP</label> <br>
        <input id="hbuscaSICOP" name="hbuscaSICOP" value="" size="6" maxlength="10" class="AyudaSyC autoCompletaSyC" type="text">  
        </div>
        
        
        <div id="beneficiario">
        <label >RFC</label> <br>
        <input id="hbuscabeneficiario" name="hbuscabeneficiario" value="" size="15" maxlength="20" class="AyudaSyC autoCompletaSyC" type="text">  
        </div>
        
     
     
       </td>
      </tr>
      <tr>
        <td><input name="buscar" type="button" onclick="crearReporte()" id="buscar" value="Buscar" /></td>
      </tr>
      
    </table></td>
  </tr>
</table>	
			
			
	</div>
	
		</form>
		</div>
	</body>
</html>