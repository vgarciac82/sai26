<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	   String user_login=usuario.getLogin();
    String roles="";
    Map rol =usuario.getRoles();
    String cEjercicio = "";
	String cIdTipoConsolidado = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		cIdTipoConsolidado = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consolidado</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
				@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
				@import "../css/demo_table_jui.css";
				@import "../css/demo_page.css";
		</style>
		<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />

		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				
				<%
				int tabla=0;
				int consulta=0;
			
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
				Map botones=nb.getBotones(roles,"Consolidado","ConsultaConsolidado");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}

				%>			
				var tabla='<%=tabla%>';
				var consulta='<%=consulta%>';
				querySelectPost("UnidadEjecutoraConsolidadoRead", "cIdUnidadEjecutora", {async: false });
				seleccionaDocumento();	
				
							
				queryFormPost("mSistema_cEjercicioRead", {async: false});
				if (consulta==0){
					mostrar();
				}
				
				$('#tblConsultaConsolidados tr').live('click', function() {         
					if (tabla==0){
						$(this).addClass('row_selected');
						var anSelected = fnGetSelected( oTable );
						if (anSelected != "") {
							var aData = oTable.fnGetData(anSelected[0]);
							window.location = aData[0];
						}
					}
				});				
			});
			
			function mostrar() {
				oTable = $("#tblVigenciasConsolidado").dataTable({
					sScrollX: "110%",
					sScrollXInner: "130%",
					bScrollCollapse: true,
					bDestroy: true,
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtado de _MAX_ registros)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					},
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_inboxPrecompromiso('<%=usuario.getU_UR()%>','<%=usuario.getLogin()%>','<%=usuario.getPropiedad("CCENTROCONTABLE")%>')",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ], [ 2, "asc" ],[ 3, "asc" ]] ,
					bAutoWidth: false,
					aoColumns: [	
								{ sName: "idcaso", bVisible:false},	
								{ sName: "folio"},
								{ sName: "numcontrato"},
								{ sName: "fechainicio"},
								{ sName: "tipotramite"},
								{ sName: "operacion"},
								{ sName: "fechaaplicacion"},
								{ sName: "importe"},
								{ sName: "operador"}
					]
        	});
		}
			
		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ ){
				if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		$('#tblVigenciasConsolidado tr').live('click', function() {
			$(this).addClass('row_selected');
			var anSelected = fnGetSelected( oTable );
			if (anSelected != "") {
				var aData = oTable.fnGetData(anSelected[0]);
				window.location = "Caso.jsp?tab=0&idCaso=" + aData[0];
			}
		});
			
		function seleccionaDocumento(){
			var documento=$("#cIdDocumento").val();
			if(documento==1){ //consolidado
				querySelectPost("mTipoConsolidadoRead", "cIdTipoDocumento", {async : false});	
				eliminaOpcionTipoProcedimiento();		
			}else{//procedimiento
				querySelectPost("TipoProcedimientoConsultarRead", "cIdTipoDocumento", {async: false });
				eliminaOpcionTipoProcedimiento();
			}
		}
		
		function eliminaOpcionTipoProcedimiento(){
		//elimina la opcion del combo tipo procedimiento FONDEN
		var objTipoProc = document.getElementById("cIdTipoDocumento");
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE FONDEN")
				objTipoProc.options[l]=null;
		}
		//elimina la opcion del combo tipo procedimiento CAPITULO 1000
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE CAPITULO1000")
				objTipoProc.options[l]=null;			
		}
		
		//elimina la opcion del combo tipo procedimiento DE ARRENDAMIENTO DE REGULARIZACION
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE ARRENDAMIENTO DE REGULARIZACION DE INMUEBLES")
				objTipoProc.options[l]=null;			
		}
		//elimina la opcion del combo tipo procedimiento DE SERVICIOS DE REGULARIZACION
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE SERVICIOS DE REGULARIZACION")
				objTipoProc.options[l]=null;			
		}
		//elimina la opcion del combo tipo procedimiento DE OBRA DE REGULARIZACION
		for(var l=0;l<objTipoProc.options.length;l++){ 
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE OBRA DE REGULARIZACION")
				objTipoProc.options[l]=null;			
		}
		
		//elimina la opcion del combo tipo procedimiento DE COMPRA DE REGULARIZACION
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE COMPRA DE REGULARIZACION")
				objTipoProc.options[l]=null;			
		}
	}
		</script>
	</head>
	<body id="dt_example" >
		<form>
		<div id="container" class="container">
			<fieldset>
				<legend>Consulta Ampliación de vigencias</legend>
				<table border="0" width="100%">
			    	<tr>
			    		<td><input id="cEjercicio" name="cEjercicio" type="hidden" size="4"></td>
			    	</tr>
			    	<tr>
			    		<td>
			    			<table align="left">
						   		<tr id="trArea" align="left">
									<td>Unidad Ejecutora:</td>
									<td><select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"style="width: 40em;"><option value="<%=usuario.getU_UR()%>" selected="selected"></select></td>
								</tr>
								<tr id="trTipoCons" align="left">
									<td>Documento:</td>
									<td><select id="cIdDocumento" name="cIdDocumento" style="width: 40em;" onchange="seleccionaDocumento();">
										<option id=0>*</option>
										<option id=1>Consolidado</option>
										<option id=2>Procedimiento</option>
									</select></td>
								</tr>
								<tr id="trTipoCons" align="left">
									<td>Tipo de Documento:</td>
									<td><select id="cIdTipoDocumento" name="cIdTipoDocumento" style="width: 40em;"></select></td>
								</tr>
								<tr id="trNumero" align="left"> 
									<td>N&uacute;mero:</td>
									<td><input type="text" name="cIdDocumento" id="cIdDocumento" style="width: 40em;" />	</td>
								</tr>
								<tr id="trDescripcion" align="left">
									<td>Descripci&oacute;n:</td>
									<td><input type="text" name="cDescripcion" id="cDescripcion" style="width: 40em;" /></td>
								</tr>
						    	<tr>
						    		<td colspan="2" align="center"><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" onclick="mostrar();" /></td>
						    	</tr>
						    </table>
			    		</td>
			    	</tr>
			    	<tr>
			    		<td width="700px" colspan="3" align="left">
				    		<div id="demo">
							    <table id="tblVigenciasConsolidado" class="display">
						        	<thead>
						        		<tr align="center"> 
						        			<th></th>
						        			<th>Folio</th>
						        			<th>N&uacute;mero Contrato</th>
						        			<th>Fecha Inicio</th>
						        			<th>Tipo Tr&aacute;mite</th>
						        			<th>Operaci&oacute;n</th>
						        			<th>Fecha Aplicaci&oacute;n</th>
						        			<th>Importe</th>
						        			<th>Operador</th>						        	
						        		</tr>
						        	</thead>
						        </table>
							</div>
			    		</td>
			    	</tr>
			    	<tr>
			    		<td>
						    <table align="left" width="80%">
					        	<tr>
					        		<td colspan="2"><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuario.getU_UR() %>"/></td>
					        	</tr>
					        </table>
			    		</td>
			    	</tr>
			    </table>
			</fieldset>
		</form>
		</div>				
	</body>
</html>