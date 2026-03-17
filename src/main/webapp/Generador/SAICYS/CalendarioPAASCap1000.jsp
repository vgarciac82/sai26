<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	String role="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String editaCIdCABM=request.getParameter("editaCIdCABM");
	String cEjercicio=request.getParameter("cEjercicio");
	String desUnidadResponsable1=request.getParameter("desUnidadResponsable1");
	String editaCIdSubPartida=request.getParameter("editaCIdSubPartida");
	String editaCIdSubPartidaCapMil=request.getParameter("editaCIdSubPartidaCapMil");
	String descrip=request.getParameter("descrip");
	String CVE_UNI=request.getParameter("CVE_UNI");
	Map rol =usuario.getRoles();
	String ueOrig=usuario.getU_UR_Orig().toString();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    
    <title>'CalendarioPAAS'</title>
    <style type="text/css">
		.centerCls {
			text-align: center;
		}
		
		.rightCls {
			text-align: right;
		}
	</style>
		    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="../js/funciones.js"></script>
	<script type="text/javascript" src="../js/funcionesPAASCapMil.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script> 
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
	<script type="text/javascript" src="../../js/utils/syctools.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" charset="utf-8">
  		$(document).ready(function() {
  			<%
  				int tabla=0;
  				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
  				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role =(String)r.getKey();
					roles+= r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
  			%>
  			$("#tbs").val(1);
  			$("#CalendarioPAAS").show();
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    $("#esperar").dialog({
				autoOpen : false,
				height : "310px",
				width : "400 px",
				modal : true,
				open: function(event, ui){
					$(".ui-dialog-titlebar").hide();
				},
				close : function() {
				}
			});
  			var roles="<%=roles%>";
  			$("#editaCIdCABM").val("<%=editaCIdCABM%>");
  			$("#cEjercicio").val("<%=cEjercicio%>");
  			$("#desUnidadResponsable1").val("<%=desUnidadResponsable1%>");
  			$("#editaCIdSubPartida").val("<%=editaCIdSubPartida%>");
  			$("#editaCIdSubPartidaCapMil").val("<%=editaCIdSubPartidaCapMil%>");
  			$("#centroContablePrecarga").val("<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>");
  			
  			$("#esperar").dialog("open");
  			initTabl();
  			iniTablRemanente();
  			consultaDatosCalendario();
  		});//Fin del document ready
  		function datosTabla(){
			var tabla='<%=tabla%>';
			var meses = 0;
			var puPromedio="0.00";
			if (tabla==0){
				if($("#hayEP").val()==0){
					alert("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.");
				}
	  			if($("#cantidad_").val()==''){
					$("#cantidad").val('0');
	  				$("#descrip").val("<%=descrip%>");
	  				$("#CVE_UNI").val("<%=CVE_UNI%>");
	  				//alert("Elese"+$("#cantidad").val());
	  			}
	  			else{
	  				$("#cantidad").val($("#cantidad_").val());
	  				$("#descrip").val($("#descrip_").val());
	  				$("#CVE_UNI").val($("#CVE_UNI_").val() );//
	  				//alert("Elese"+$("#cantidad").val());
				}
				
			 	if(parseInt($("#plurianualidad").val(),10)>0){
					document.getElementById("chk_plurianualidad").checked=true;
					$("#plurianualidad").css("visibility","visible");
					$("#plurianualidadv").css("visibility","visible");
					$("#EP").css("visibility","visible");
					$("#ME").css("visibility","visible");
					$("#anos").css("visibility","visible");
				}
				var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#ue_usuarioEdita").val() + "', '" + $("#editaCIdCABM").val() + "','"+$("#partidaEditaCapMil").val()+"'";
				var color="";
				$('#tblcucopPeriodo').dataTable().fnClearTable(); 
				szTabla = "CUCOPSPERIODOGRIDCAPMIL";
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
					function(j){
						arrayCompleto=new Array();
						for (var i = 0; i < j.length; i++){
							if(parseFloat(j[i].Col2) > 0){
			    				color="#000000";
							}else{
			    				j[i].Col3 = puPromedio;
			    				color="#B4B4B4";
							}
				    		arrayCompleto [i]=[
								"<input type='text' id='cperiodo' name='cperiodo_'"+i +" value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
								"<input type='text' id='cantDisp' name='cantDisp_'"+i +" value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='ncantidad_"+i+"' name='ncantidad_'"+i +" value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
								"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");' onchange='cambiafrmt(this);' onKeyPress='return(onlyNumbers2(event));' />",
								"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
			                    "<input type='text' id='mmontodisponibilidad_"+i+"' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
			                     j[i].Col10
							];
						}
						$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);		    					   
						$('.editacucop').formatCurrency();
					});   							
			}
		}
		
			
		
	</script>

  </head>
  
  <body>
  	<div id="container" class="container" style="width: 98%;">
  		<fieldset>
  		<form action="">
  			<table align="center"> 
				<tr> 
					<td align="right"> 
						UE Consultada: 
					</td> 
					<td align="left"> 
						<input type="text" id="ue_usuarioEdita" name="ue_usuarioEdita" value="<%=desUnidadResponsable1%>" style="border: 0px none ;background:#FEFEFE; width: 30em;" maxlength="120" readonly="readonly" /> 
					</td> 
				</tr> 
				<tr> 
					<td align="right"> 
						Cucop: 
					</td> 
					<td align="left" colspan="2"> 
						<input type="text" id="cucopEdita" name="cucopEdita" value="<%=editaCIdCABM%>" readonly="readonly" style="border: 0px none ; width: 6em; color: red;background:#FEFEFE;" /> 
								-- 
						<input type="text" id="cCABMEdita" name="cCABMEdita" value="" readonly="readonly" style="border: 0px none ; width: 35em; color: red; background:#FEFEFE" /> 
					</td> 
				</tr>
				<tr> 
					<td align="right"> 
						Partida Cap Mil: 
					</td> 
					<td align="left"> 
						<input type="text" id="partidaEditaCapMil" value="<%=editaCIdSubPartidaCapMil %>" name="partidaEditaCapMil" style="border: 0px none ; width: 30em;background:#FEFEFE;" readonly="readonly" /> 
					</td> 
				</tr> 
				<tr>
					<td align="right"> 
						Partida: 
					</td> 
					<td align="left"> 
						<input type="text" id="partidaEdita" name="partidaEdita" value="<%=editaCIdSubPartida %>" style="border: 0px none ; width: 30em;background:#FEFEFE;" readonly="readonly" /> 
					</td>
				</tr>
				<tr> 
					<td align="right"> 
						Total: 
					</td> 
					<td align="left"> 
						<input type="text" id="totalEdita" name="totalEdita" readonly="readonly" style="border: 0px none ; width: 30em;background:#FEFEFE;" /> 
					</td> 
				</tr>
				<tr> 
				   <td align="right"> 
						<select id="ivaEdita" name="ivaEdita" style="width: 8em;"> 
						</select> 
					</td> 
					<td align="left" colspan=""> 
						IVA 
					</td> 
					<td> 
					</td> 
				</tr>
				<tr> 
				   <td align="right"> 
						$<input type="text" id="pymeEdita" name="pymeEdita" maxlength="15"  style="width: 7em;" onkeypress="return(onlyInteger(event));" maxlength="4" title="Se refiriere al monto que, del valor total estimado, se considera susceptible de compra a una micro, pequeña o mediana empresa. Nota: el valor mínimo para este campo es 0, no podrá dejarse vacío, introduzca únicamente números, sin comas y sin decimales." /> 
					</td> 
					<td align="left"> 
						Cantidad destinado a MIPyME  
					</td>
				</tr> 
				<tr> 
				   <td align="right"> 
						$<input type="text" id="porcentajeEdita" name="porcentajeEdita" maxlength="15"  style="width: 7em;" onkeypress="return(onlyInteger(event));" maxlength="4" title="Se refiriere al monto que, del valor total estimado, se considera para procedimientos no cubiertos bajo alguno de los tratados de libre comercio de los que México es parte. Nota: el valor mínimo para este campo es 0, no podrá dejarse vacío, introduzca únicamente números, sin comas y sin decimales."/> 
					</td> 
					<td align="left" colspan="2"> 
						estimado de compras no cubiertas por tratados 
					</td> 
				</tr>
				<tr> 
					<td align="right"> 
						<select id="tipoProcedimientoEdita" name="tipoProcedimientoEdita" style="width: 15em;"> 
						</select> 
					</td> 
					<td align="left"> 
						Procedencia 
					</td> 
				</tr>
				<tr>
					<td align="right"> 
						Plurianual: <input type="checkbox" id="chk_plurianualidad" name="chk_plurianualidad" onclick="datosPlurianualidad()"/> 
					</td> 
					<td align="left" colspan="6"> 
						<input name="EP" id="EP"  value="Ejercicio de Plurianualidad"size="25" style="border-width:0; background-color:transparent;visibility: hidden;"/>
						<input type="text" id="plurianualidad" name="plurianualidad" maxlength="2" style="width: 2em; visibility: hidden;" onkeypress="return(onlyInteger(event));" /> 
						<input name="anos" id="anos"  value="A&ntilde;os"size="8" style="border-width:0; background-color:transparent;visibility: hidden;" />
						<br><input name="ME" id="ME"  value="Monto sin IVA a Ejercer $" size="25" style="border-width:0; background-color:transparent; visibility: hidden;"  />
						<input type="text" id="plurianualidadv" name="plurianualidadv" maxlength="15" style="width: 7em; visibility: hidden;" onkeypress="return(onlyInteger(event));" />
					</td>
				</tr>
				<tr> 
					<td align="right">Tipo de Procedimiento</td> 
					<td align="left" colspan="6"> 
						<select id="tipoAdjudicacion" name="tipoAdjudicacion" style="width: 22em;"> 
						</select> 
					</td>
				</tr> 
				<tr> 
				<td width="33%"> 
					&nbsp; 
				</td> 
				<td align="left"> 
					<input type="button" id="pbGuardaEdita" name="pbGuardaEdita" value="Guardar" onclick="guardaCalendario();" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all" />
				</td> 
			</tr>
		</table>
		<table id="tblcucopPeriodo" class="display"> 
            <thead> 
				<tr> 
                	<th>Trimestre</th>
                	<th>Cantidad Disponible</th>
                	<th>Cantidad</th>	                     
                    <th>Precio Unitario</th> 
                    <th>Importe Bruto</th> 
                    <th>Importe Neto</th> 
                    <th>Cantidad En solicitudes</th> 
                    <th style="display: none;">Cantidad Disponible</th> 
                    <th>Monto en solicitudes</th>	 
                    <th>Monto Disponible</th>
                    <th style="display: none;"></th>
				</tr>
			</thead> 
		</table>
		<br/><br/>
		<table id="tblDisponible" class="display"> 
            <thead> 
                <tr> 
                	<th>UE</th>
                	<th>Partida</th>
                	<th>Monto Anual</th>	                     
                    <th>Enero</th> 
                    <th>Febrero</th> 
                    <th>Marzo</th> 
                    <th>Abril</th> 
                    <th>Mayo</th> 
                    <th>Junio</th>	 
                    <th>Julio</th>
                    <th>Agosto</th>
                    <th>Septiembre</th>	
                    <th>Octubre</th>	
                    <th>Noviembre</th>
                    <th>Diciembre</th>				                     
              </tr>
            </thead> 
        </table>
    	<input type="hidden" id="isPluri" name="isPluri" value="0" />
  		</form>
  		</fieldset>
  	</div>
  	<div id="esperar" align="center" title="Espera">
		<fieldset>
			<table>
				<tr>
					<td>Espere por favor.... <img border="0"src="../../imagenes/espera.gif" height="30"></td>
				</tr>
			</table>
		</fieldset>
	</div>
  </body>
</html>