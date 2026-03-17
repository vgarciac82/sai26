<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="com.syc.fortimax.core.AplicacionManager" %>
<%@ page import="com.syc.adquisiciones.DatosCaso" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println(basePath);
String idCaso = (String) request.getParameter("idCaso");

DatosCaso dc = AplicacionManager.selectDatosCaso(idCaso);

%>
<script>
	function aprobar(aprobar){
	
			if(aprobar == '0' && $("#motivo").val()==""){
				alert("Es necesario un motivo de rechazo");
				return;
			}
			
			
			$.ajax({

				url: "../../servlet/ConsolidadoServlet?motivo=" + $("#motivo").val() + "&folio=<%=dc.getFolioPrecompromiso()%>&aprobarFechaVigencia="+aprobar, 					
				async: false, 
				processData: false,
            	type:        'POST',
            	contentType: false,
				dataType: 'json', 
				data : "id=123",
				//Si el ajax fue success
				success: function(json){
						
					
					if(aprobar == '1')
						alert("Se ha aprobado la ampliación de vigencia");
					else			
						alert("Se ha rechazado la ampliación de vigencia");
				
						
					$("#divMotivo").css("display", "none");
					$("#divBotones").css("display", "none");	
					
				},
				error:function(json){
					alert("error");
				}
			});
	}
	
	function abrirDiv(){
	
		$("#divMotivo").css("display", "inline");
		
	}
	
	function cerrarDiv(){
	
		$("#divMotivo").css("display", "none");
	}

</script>
<table width="85%" align="center">

	<tr>
		<td width="40%" align="left" valign="top"><b>Folio del documento:</b></td>
		<td width="60%" align="left" valign="top"><%=dc.getFolioConsolidado() %></td>
	</tr>
	<tr>
		<td width="40%" align="left" valign="top"><b>Folio de Precompromiso:</b></td>
		<td width="60%" align="left" valign="top"><%=dc.getFolioPrecompromiso() %></td>
	</tr>
	<tr>
		<td width="40%" align="left" valign="top"><b>Descripci&oacute;n:</b></td>
		<td width="60%" align="left" valign="top"><%=dc.getDescripcion() %></td>
	</tr>
	<tr>
		<td width="40%" align="left" valign="top"><b>Fecha de Vigencia Solicitada:</b></td>
		<td width="60%" align="left" valign="top"><%=dc.getFechaFin() %></td>
	</tr>
</table>

<br>
<div id="divBotones" style="display: inline">
<table width="85%" align="center">

	<tr>
		<td width="40%" align="center" valign="top"><input type="button" value="Aprobar" onclick="aprobar('1');"></td>
		<td width="60%" align="center" valign="top"><input type="button" value="Rechazar" onclick="abrirDiv();"></td>
	</tr>
	
</table>
</div>
<div id="divMotivo" style="display: none">
	<table width="50%" align="right">
	
		<tr>
			<td align="center" valign="top">Motivo de Rechazo:</td>
		</tr>
		<tr>
			<td align="center" valign="top"><textarea id="motivo" name="motivo" cols="30" rows="5"></textarea></td>
		</tr>
		<tr>
			<td width="40%" align="center" valign="top">
			<input type="button" value="Enviar" onclick="aprobar('0');">&nbsp;&nbsp;
			<input type="button" value="Cancelar" onclick="cerrarDiv();"></td>
		</tr>
	</table>
	<br><br><br><br><br><br><br><br><br>
</div>

 <iframe name = "formulario" width="700px" height="600px" src="<%=basePath %>doDownload?idCaso=<%=idCaso%>">
 </iframe>
 

