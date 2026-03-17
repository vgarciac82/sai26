<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
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
	Map rol =usuario.getRoles();
	Calendar c1 = Calendar.getInstance(); // today
	int mesActual=c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String mesAct="";
	if(mesActual<10){
		mesAct="0"+mesActual;
	}else{
		mesAct=""+mesActual;
	}
	String ueOrig=usuario.getU_UR_Orig().toString();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>'ConsultaPAASCap1000.jsp'</title>
	<script type="text/javascript" charset="utf-8">
		var usuarioLogin="<%=usuario.getLogin()%>";
		var roles="<%=roles%>";
		var centroContable='<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>';
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
				Map pestanas=ebl.getPestana(role,"Programa anual");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);<%
					
					String pestana=(String)e.getValue();
					if ("calendarioPrograma".equals(pestana)){
						 tabla=1;
					}	
				}
  			%>
  			
		    if( roles.indexOf("ADMIN_RECMAT") >=0){
		    	$('#isAdmin').val(0);
		    }
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
		    consultaSelects();
		    initTbls();
		    $('#tblSeleccionaCucop tr').live('dblclick', function() { 
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTable );
				if (anSelected != "") {
					var aData = oTable.fnGetData(anSelected[0]);
					$("#agregaCIdCABM").val(aData[0]);
				    $("#agregaCIdSubPartida").val(aData[2]);
				    $("#cIdPartidaCapMil").val(aData[2]);
				    $("#agreganPorcentajeIVA").val('16');
				    $("#agregacIdProcedencia").val('');
				    $("#agregamPorcentajePyme").val('0');
				    $("#agregamPorcentajeNoTratados").val('0');
				    $("#agregaplurianualidad").val('0');
				    $("#agregaplurianualidadv").val('0');
				    $("#agregatipoAdjudicacion").val('LP');
				    $("#cIdPartida").val(aData[2]);
					if($("#hayEP").val()==0){
						swal("No hay estructura programatica para la partida '"+$("#cIdPartidaCapMil").val()+"'.\nSolicitar adecuación presupuestal.",{icon:"info",button: "Cerrar"});
						return;
					}
					//procedimiento almacenado para agregar cucops al programa anual
					queryFormPost("spAgregaCucopCapMil", {async: false });
					buscar();
					//se habilitan botones porque ya existe porlo menos un registro en la tabla
					$("#pbBorrarCucop").css("visibility","visible");
					$("#pbEditarCucop").css("visibility","visible");
					$(this).removeClass('row_selected');
				}
			});
			$("#tblCucops tr").live("dblclick", function() {
				var tabla='<%=tabla%>';
				var meses = 0;
				var puPromedio="0.00";
				if (tabla==0){
					if ($(this).hasClass('row_selected')){
						$(this).removeClass('row_selected'); 
					}else{
	               		$(this).addClass('row_selected'); 
						var aTrs =oTable2.dataTable().fnGetNodes();
			  			for ( var i=aTrs.length ; i>=0; i-- )  
						{       
							if ( $(aTrs[i]).hasClass('row_selected') )         
							{
								//Obtiene el CABM seleccionado
							  	$(this).removeClass('row_selected'); 
							  	aData = oTable2.fnGetData(aTrs[i]);
	                          	$("#editaCIdCABM").val($("#cIdCABM_"+aData[11]+"").val());
							  	$("#editaCIdSubPartida").val($("#cIdSubPartida_"+aData[11]+"").val());
							  	$("#editaCIdSubPartidaCapMil").val($("#cIdSubPartidaCapMil_"+aData[11]+"").val());
						   	  	queryFormPost("llenaDetalleCucopEditaCapMil",{async:false});
						   	  	$("#totalEdita").formatCurrency();
						   	  	$("#cIdPartida").val($("#cIdSubPartida_"+aData[11]+"").val());
								if($("#hayEP").val()==0){
									swal("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.",{icon:"info",button: "Cerrar"});
									return;
								}
								window.location = "ProgramaAnualCap1000.jsp?tab=1&editaCIdCABM="+$("#editaCIdCABM").val()+"&cEjercicio="+$("#cEjercicio").val()
								+"&desUnidadResponsable1="+$("#cIdUE").val()+" &editaCIdSubPartida="+$("#editaCIdSubPartida").val()+" &editaCIdSubPartidaCapMil="+$("#editaCIdSubPartidaCapMil").val()
								
								+"&CVE_UNI="+$("#cUnidadMedida_"+aData[11]+"").val()+"&descrip="+encodeURIComponent($("#cCABM_"+aData[11]+"").val());
							}					              
						}			
					}
				}
			});
			
  		});//Fin del document ready
  		
  	</script>
 	</head>
  
	<body >
		<form action="">
	    	<div id="container" class="container" style="width: 98%;">
	    		
	    		<fieldset>
	    			<table id="tblEncabezado" style="width: 98%;">
						<tr>
							<td>
								Unidad Ejecutora:<select id="cIdUE" name="cIdUE" style="width: 30em;" ></select>
							</td>
						</tr>
						<tr style="display: none;">
							<td>
								Partida Cap. Mil:<select id="cIdPartidaCapMil" name="cIdPartidaCapMil" style="width: 30em;" onchange="actualizaMontosCapitulo();"></select>
							</td>
						</tr>
						<tr id="trCargaManual" style="display: none;">
							<td>
								<fieldset>
									<legend>Carga Manual</legend>
									<input type="button" id="pbGuardaUE" name="pbGuardaUE" value="Agregar UE" onclick="agregaUE();" class="btnInterfaceBG" style="visibility: hidden;"/>
								</fieldset>
							</td>
						</tr>
					</table>
	    		</fieldset>
				<fieldset id="trConsulta" style="visibility: hidden;">
					<legend>Filtro de Consulta </legend>
					<table id="tblbuscaCucop">
						<tr> 
							<td align="right"> Capitulo: </td> 
							<td> 
								<select id="mCatalogoCapitulo" name="mCatalogoCapitulo" style="width: 30em;" onchange="cambiaComboPartida();"> </select> 
							</td> 
						</tr>
						<tr> 
							<td align="right"> 
								Partida: 
							</td> 
							<td> 
								<select id="mCatalogoSubPartida" name="mCatalogoSubPartida" style="width: 30em;"  onchange="addValue();">  
								</select>
							</td>
						</tr>
						<tr> 
							<td align="right"> 
								Cucop: 
							</td> 
							<td> 
								<input type="text" id="cucopPa" name="cucop_pa" style="width: 30em;" /> 
							</td> 
						</tr> 
						<tr> 
							<td align="right"> 
								Descripci&oacute;n: 
							</td> 
							<td> 
								<input type="text" id="desCucopPa" name="des_pa" style="width: 30em;" /> 
							</td> 
						</tr>
						<tr style="display: none;">
							<td align="left" colspan="7"> 
								Capitulo 2000: 
								<input type="text" id="mMontoC2" name="mMontoC2" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
								&nbsp;Capitulo 3000:&nbsp; 
								<input type="text" id="mMontoC3" name="mMontoC3" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
								&nbsp;Capitulo 5000: &nbsp; 
								<input type="text" id="mMontoC5" name="mMontoC" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
							</td> 
						</tr>
						<tr> 
							<td align="left" colspan="7"> 
								Total Capturado por Partida Cap. Mil: 
								<input type="text" id="mMontoPartida" name="mMontoPartida" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" /> 
								&nbsp;Presupuesto:&nbsp; 
								<input type="text" id="mTechoPresupuestal" name="mTechoPresupuestal" size="16" style="border: 0px none ;background:#FEFEFE" readonly="readonly" />
								&nbsp;&nbsp; 
								<input type="text" id="mAvisoTecho" name="mAvisoTecho" style="color: red; border: 0px none; width: 250px; font-weight: bold;background:#FEFEFE" readonly="readonly" />
							</td> 
						</tr>
						<tr align="left"> 
							<td colspan="2"> 
								Total Capturado:&nbsp; 
								<input type="text" id="mMontoTotal" name="mMontoTotal" style="border: 0px none ;background:#FEFEFE" size="25" readonly="readonly" /> 
							</td> 
						</tr>
						<tr> 
							<td width="33%"> 
								&nbsp; 
							</td> 
							<td width="33%" align="center">
								<input type="button" id="pbBuscacucop" name="pbBuscacucop" value="Buscar" onclick="buscar();" class="btnInterfaceBG  ui-button ui-widget ui-state-default ui-corner-all" />
							</td> 
							<td width="33%" align="right"> 
								&nbsp; 
						
							</td> 
						</tr> 
					</table>
				</fieldset>
				<fieldset id="trCucopsDisp" style="visibility: hidden;">
					<legend> Cucops Disponibles</legend>
					<table id="tblSeleccionaCucop" class="display"> 
						<thead> 
							<tr> 
				            	<th >CUCOP</th> 
				                <th >Descripcion</th> 
				                <th >Partida</th> 
				                <th >Descripcion Partida</th> 
				                <th >Unidad  de Medida</th>						                         
								<th >Tipo Proceso</th> 
							</tr> 
						</thead> 
					</table>
  				</fieldset>
  				<fieldset id="trCucopsSelec" style="visibility: hidden;">
					<legend>Cucops Seleccionados</legend>
  					<table  id="tblCucops" class="display"> 
						<thead> 
							<tr> 
								<th > 
									CUCOP 
								</th>
								<th> 
									Partida Cap. Mil
								</th> 
								<th> 
									Partida 
								</th> 
								<th > 
									Descripcion 
								</th> 
								<th > 
									Unidad de Medida 
								</th> 
								<th > 
									Cantidad 
								</th> 
								<th > 
									Precio Unitario Promedio
								</th> 
								<th > 
									IVA 
								</th> 
								<th > 
									Importe Bruto 
								</th> 
								<th > 
									Importe Neto 
								</th> 
								<th>Eliminar</th>
								<th style="display: none;"></th>
								<th style="display: none;"></th>
							</tr> 
						</thead> 
					</table> 
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
    	</form>
	</body>
</html>