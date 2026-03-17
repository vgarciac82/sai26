<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Seguimiento de Prestamo</title>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../js/masks.js"></script>
			<script type="text/javascript" src="js/jquery-1.2.6.min.js"></script>
		
		<script type="text/javascript">
				
			function activa_boton( )
			{
				if ($("#hPrestamo").val() != " "){
					document.frm.cmdExcel.disabled=false;
				} else {
					document.frm.cmdExcel.disabled=true;
				}
			}

			function activa_campo( )
			{
				
				if ($("#hPrestamo").val() != " "){
					document.frm.hPrestamo.disabled=false;
				} else {
					document.frm.hPrestamo.disabled=true;
				}
			}
			
			function imprimir(){

					window.open("../admin/SeguridadCatalogos?"
					+ "catalogo=RDB"
					+ "&accion=run"
					+ "&rn=SeguimientoDesembolso.jasper"
					+ "&Folio="+$("#Prestamo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768"
		 );
}

	 		$(document).ready(function() {
	 		
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();

			});
		</script>
	</head>
	
	<body>
		<h4>Seguimiento de Prestamo</h4>
		<br>
		<br>
		<br>
		<form action="" method="post" name="frm" id="frm">
			<br>
			<div >
				<table align="center" border="0" >
					<tr>
						<td>Folio Pr&eacute;stamo :</td>
						<td>
							<input id="hPrestamo" name="hPrestamo" type="text" value="" size="40" maxlength="50" class="AyudaSyC autoCompletaSyC" onChange="activa_boton( )" /> 
							<input id="Prestamo" name="Prestamo" type="hidden" value="" size="5" maxlength="5"  />
						</td>			
					</tr>	
					<tr>
						<td align="center" colspan="2">
							<br>
							<br>
							<input type="button" id="cmdExcel" name="cmdExcel" value="Generar Reporte" disabled onclick="imprimir();" />
						</td>
					</tr>		
				</table>
			</div>			
		</form>	
	</body>
</html>
