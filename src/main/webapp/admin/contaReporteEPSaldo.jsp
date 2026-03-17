<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reporte Presupuesto</title>
<script src="../js/datepickercontrol.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/jquery-1.2.6.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />

<script type="text/javascript">

	function limpiarSesion()
	{
		window.location.href="contaReporteEPSaldo.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
	}
	
	function fnPrintTituloReporte() {
			var principal = parent.parent.document;
			var asunto    = document.datawork;
			
			var ep= document.getElementById('ep').value;
			var EjercicioFiscal = document.getElementById('EjercicioFiscal').value;
			//var Cuenta    		= "81102";
			var Cuenta    				= document.getElementById('Cuenta').value;
			var UnidadEjecutora 		= document.getElementById('cUnidadEjecutora').value;
			var RamoEP					= document.getElementById('RamoEP').value;
			//var UnidadResponsableEP		= document.getElementById('UnidadResponsableEP').value;
			var GrupoFuncional			= document.getElementById('GrupoFuncional').value;
			var Funcion					= document.getElementById('Funcion').value;
			var SubFuncion				= document.getElementById('SubFuncion').value; 
            var ProgramaGeneral			= document.getElementById('ProgramaGeneral').value;
			var ProgramaPresupuestario	= document.getElementById('ProgramaPresupuestario').value;
			var ActividadInstitucional	= document.getElementById('ActividadInstitucional').value;
			var Partida					= document.getElementById('Partida').value;
			var TipoGasto				= document.getElementById('TipoGasto').value;
			var FuenteFinanciamiento	= document.getElementById('FuenteFinanciamiento').value;
			var EntidadFederativa 		= document.getElementById('EntidadFederativa').value;
			var Cartera 				= document.getElementById('Cartera').value;
			var UnidadNorativa 			= document.getElementById('UnidadNorativa').value;
            var ClaveCNA 				= document.getElementById('ClaveCNA').value;
            //var Mes						= document.getElementById('Mes').value;
            var cOrddeBy				= document.getElementById('orderBy').value;
			
			if (Cuenta==""){
				alert("Seleccione el Presupuesto a consultar. ");
				return false;	
			}
			if (EjercicioFiscal==""){
				alert("El ejercicio fiscal es requerido. ");
				return false;	
			}
			
			var param = "id=<%=request.getParameter("id")%>" 
				 + "&ep=" + ep 
				 + "&EjercicioFiscal=" + EjercicioFiscal   
				 + "&Cuenta=" + Cuenta   
				 + "&UnidadEjecutora=" + UnidadEjecutora   
				 + "&RamoEP=" + RamoEP   
//				 + "&UnidadResponsableEP=" + UnidadResponsableEP  
				 + "&GrupoFuncional=" + GrupoFuncional 
				 + "&Funcion=" + Funcion  
				 + "&SubFuncion=" + SubFuncion  
				 + "&ProgramaGeneral=" + ProgramaGeneral   
				 + "&ProgramaPresupuestario=" + ProgramaPresupuestario  
				 + "&ActividadInstitucional=" + ActividadInstitucional  
				 + "&Partida=" + Partida  
				 + "&TipoGasto=" + TipoGasto   
				 + "&FuenteFinanciamiento=" + FuenteFinanciamiento 
				 + "&EntidadFederativa=" + EntidadFederativa 
				 + "&Cartera=" + Cartera  
				 + "&UnidadNorativa=" + UnidadNorativa 
				 + "&ClaveCNA=" + ClaveCNA 
				 + "&cOrddeBy="+ cOrddeBy
				 + "&rn=reportePresupuesto.jasper";
					   
			
			//Esta jsp se creo con base en las jsp wrkflw-**********.jsp que estan en webcontent/admin
			//aqui habia un monton de codigo comentado que aparentemente era el cuerpo del reporte
			//pero parece que despues decidieron generarlo mejor en reporte_resultado.jsp
			//voy a seguir la misma manera de generar el resultado que en los reportes ya existentes

			//if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & UnidadResponsableEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNorativa=="" & ClaveCNA=="" )
			if (ep=="" & EjercicioFiscal=="" & Cuenta=="" & UnidadEjecutora=="" & RamoEP=="" & GrupoFuncional=="" & Funcion=="" & SubFuncion=="" & ProgramaGeneral=="" & ProgramaPresupuestario=="" & ActividadInstitucional=="" & Partida=="" & TipoGasto=="" & FuenteFinanciamiento=="" & EntidadFederativa=="" & Cartera=="" & UnidadNorativa=="" & ClaveCNA=="" )
			{
				alert("Capturar algun dato");
				return false;	
			} else {
				//alert("antes de ejecutar reportePresupuesto.jsp");
				parent.frames['resultado'].location.href = "reportePresupuesto.jsp?" + param;
				return true;	
			}
		}
</script>
<script type="text/javascript" >

		$(document).ready(
			function() 
			{
				$("input.AyudaSyC").subIniciaDlg();	
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			}
		);
</script>		
</head>
<body background="../imagenes/steel_BG.gif" >
	<div class="Contenido" style="height: 10%">
		<table class="TituloRutaCA">
			<tr>
				<td>
					<img src="../imagenes/iconos/reportes.png" alt="" width="16" height="16">
					<font color="#FFFFFF">
					<strong>
						Presupuesto Autorizado.
					</strong>
					</font>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
		</table>
	</div>
	<form id="form1" name="form1" method="post" target="formulario" action="./contaReporteEPSaldo.jsp?select=u_login=">
	<input type="hidden" id="UnidadEjecutora" name="UnidadEjecutora" value=""/>
		<table border="0" cellspacing="0" cellpadding="2">
			<tr>
				<td>Presupuesto:</td>
				<td colspan="2"><select name="Cuenta" id="Cuenta" onchange=""><option value=""></option>
						<option value="81101">ORIGINAL</option>
						<option value="81102">MODIFICADO</option>
						<option value="81103">AMPLIACION AUTORIZADA</option>
						<option value="81104">REDUCCION AUTORIZADA</option>
						<option value="81200">COMODIN ACREEDOR</option>
						<option value="82101">APARTADO</option>
						<option value="82102">PRECOMPROMETIDO</option>
						<option value="82103">COMPROMETIDO</option>
						<option value="82104">DEVENGADO</option>
						<option value="82105">EJERCIDO</option>
						<option value="82106">DISPONIBLE NETO</option>
						<option value="82107">DISPONIBLE BRUTO</option>
						<option value="82200">COMODIN DEUDOR</option>
				</select></td>
			</tr>
			<tr>
				<td>EP</td>
				<td><input id="ep"     name="ep"   value="" size="70"  maxlength="100"/></td>
				<td>&nbsp;&nbsp;Clave corta&nbsp;&nbsp;<input id="ClaveCNA"  name="ClaveCNA"  value="" size="2"  maxlength="5"/></td>
			</tr>
			
			<tr>
				<td>
					<table>
						<tr>
							<td>Ejercicio Fiscal</td>
							<td><input id="EjercicioFiscal"     name="EjercicioFiscal"   value="2012" size="4"  maxlength="5"/></td>
						</tr>
						<tr >
							<td>Ramo</td>
							<td><input id="RamoEP"     name="RamoEP"     value="" size="3"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Finalidad</td>
							<td><input id="GrupoFuncional"  name="GrupoFuncional"  value="" size="2"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Funcion</td>
							<td><input id="Funcion"  name="Funcion"  value="" size="2"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>SubFuncion</td>
							<td><input id="SubFuncion"  name="SubFuncion"  value="" size="2"  maxlength="5"/></td>
						</tr>
					</table>
				</td>
				<td>
					<table  align="center">
						<tr>
							<td>Reasignaci&oacute;n</td>
							<td><input id="ProgramaGeneral"  name="ProgramaGeneral"  value="" size="2"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Actividad Institucional</td>
							<td><input id="ActividadInstitucional"  name="ActividadInstitucional"  value="" size="4"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Programa Presupuestario</td>
							<td><input id="ProgramaPresupuestario"  name="ProgramaPresupuestario"  value="" size="4"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Partida</td>
							<td><input id="Partida"  name="Partida"  value="" size="5"  maxlength="7"/></td>
						</tr>
						<tr>
							<td>Tipo Gasto</td>
							<td><input id="TipoGasto"  name="TipoGasto"  value="" size="2"  maxlength="5"/></td>
						</tr>
					</table>
				</td>
				<td>
					<table>
						<tr>
							<td>Fuente Financiamiento</td>
							<td><input id="FuenteFinanciamiento"  name="FuenteFinanciamiento"  value="" size="2"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Entidad Federativa</td>
							<td><input id="EntidadFederativa"  name="EntidadFederativa"  value="" size="2"  maxlength="5"/></td>				
						</tr>
						<tr>
							<td>Cartera</td>
							<td><input id="Cartera"  name="Cartera"  value="" size="12"  maxlength="15"/></td>
						</tr>
						<tr>
							<td>Unidad Normativa</td>
							<td><input id="UnidadNorativa"  name="UnidadNorativa"  value="" size="3"  maxlength="5"/></td>
						</tr>
						<tr>
							<td>Unidad Ejecutora</td>
							<td><input id="cUnidadEjecutora"  name="cUnidadEjecutora"  value="" size="2"  maxlength="5"/></td>
						</tr>
					</table>				
				</td>
			</tr>
			
			<tr>
				<td>Ordenado por:</td>
				<td><select name="orderBy" id="orderBy" onchange=""><option value=""></option>
					<option value="nClaveCNA">Clave corta</option>
					<option value="ClaveSIAFF">Clave SIAFF</option>
					<option value="ClaveInterna">Clave Interna</option>
					<option value="aEjercicioFiscal">Ejercicio Fiscal</option>
					<option value="cRamoEP">Ramo</option>
					<!-- option value="cUnidadResponsableEP">Unidad Responsable</option> -->
					<option value="cGrupoFuncional">Finalidad</option>
					<option value="cFuncion">Funcion</option>
					<option value="cSubFuncion">Sub Funcion</option>
					<option value="cProgramaGeneral">Reasignaci&oacute;n</option>
					<option value="cProgramaPresupuestario">Programa Presupuestario</option>
					<option value="cActividadInstitucional">Actividad Institucional</option>
					<option value="cPartida">Partida</option>
					<option value="cTipoGasto">Tipo Gasto</option>
					<option value="cFuenteFinanciamiento">Fuente Financiamiento</option>
					<option value="cEntidadFederativa">Entidad Federativa</option>
					<option value="cCartera">Cartera</option>
					<option value="cUnidadNorativa">Unidad Norativa</option>
					<option value="cUnidadEjecutora">Unidad Ejecutora</option>
					</select></td>
			</tr>
			
		</table>
		
 	<table align="center">
		<tr>
			<td>
				<input type="button" value="Generar Reporte" onClick="return fnPrintTituloReporte();">
				<!--  input type="submit" name="button2" id="button2" value="Buscar" onclick="return fnValida();"/ -->
				<input type="button" value="Limpiar" onClick="limpiarSesion();"/>
			</td>
		</tr>
	</table>
	<p>&nbsp;</p>
	</form>
</body>
</html>