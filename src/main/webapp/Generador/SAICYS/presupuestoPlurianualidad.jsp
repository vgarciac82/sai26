<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
		Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		if (usuarioTab == null) {
			response.sendRedirect("../../index.jsp");
			return;
		}
		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Calendar c1 = Calendar.getInstance(); // today
		
		String today= sdf.format(c1.getTime());
		String cUR = "";
		String cRamo = "";
		boolean bAplicadoCont=false;
		
		String name_user=usuarioTab.getLogin();		
		Map rol =usuarioTab.getRoles();
		String role="";		
		String cCentroContable=usuarioTab.getPropiedad("CCENTROCONTABLE").getValor();
	
	String cEjercicio = "";
	String cIdContrato= "";
	String cIdUnidadEjecutora = "";
	String cIdContratoDefinitivo = "";
	String cIdTipoCambio = "";
	String cITipoContrato = "";
	
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoPlurianual);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjecPlurianual);
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
		cIdTipoCambio = (String)session.getAttribute(GestionInterface. ATT_tipoCambioPlurianual);
		cITipoContrato = (String)session.getAttribute(GestionInterface. ATT_tipoContratoPlurianual);
		System.out.println("tipo contrato " +cITipoContrato +" "+ cIdContratoDefinitivo+ " "+cEjercicio+"  "+ cIdContrato   );
						
	}else 
		response.sendRedirect("Plurianualidad.jsp?tab=0");
	
	
	
	
%>
<!doctype html>
<html>
  <head>    
    <title>Presupuesto</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	<script type="text/javascript" charset="utf-8">
				var oTableSuficiencia;
				var oTableClaves;
				var oTableCompromiso;
				var roles;
				$(document).ready(function() {
					tabb=3;
					showAndHideTabs();
				<%
					int aprobar=0;
				    int devolver=0;
					NegativaPestana NegPestana=new NegativaPestana();
					NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
					//botones
					NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						role += r.getKey().toString()+",";
					}
					if(role.length()>0){
						role = role.substring(0,role.length()-1);
					}
					
					Map botones=nb.getBotones(role,"PlurianualidadContratos","presupuestoPlurianualidad");
					Iterator btn = botones.entrySet().iterator();
					while (btn.hasNext()) {
						Map.Entry b = (Map.Entry)btn.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%
						String img=(String) b.getValue();
						if ("imgAprobarPlurianualidad".equals(img)){  
							%>
							$("#<%=img%>").attr("disabled", false);<%
							aprobar=1;
						}
						if ("imgDevolverPlurianualidad".equals(img)){
							%>
							$("#<%=img%>").attr("disabled", false);<%
							devolver=1; 
						} 
					}

				%>
				roles="<%=role%>";
				setReadOnly();
				headerQuery();
				initDataTable();
				validaCondicionesIniciales();
				loadClavesPresupuestalesContrato();
				loadEventClickHandlers();
				$("#mImporteTotal").formatCurrency();
				showLabel();
			});	
			function validaCondicionesIniciales(){
				//solo cuando esta capturado el pedido se puede agregar EPs
				if (parseInt($("#nIdEstado").val(),10)== 3 || parseInt($("#nIdEstado").val(),10)== 4 || parseInt($("#nIdEstado").val(),10)== 1){
					$("#imgDevolverPlurianualidad").hide();
				}
				if (parseInt($("#nIdEstado").val(),10)> 1 ){
					$("#nIdClaveEgresosXPlurianualidad").hide();
					$("#AgregarEPPlurianualidad").hide();
					$("#imgAprobarPlurianualidad").hide();
				}
			}
			function loadEventClickHandlers(){
				$('#dt_clavepresup').on('dblclick','tr', function() {        
						$(oTableClaves.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});						
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTableClaves );						
						var aData = oTableClaves.fnGetData(anSelected[0]);							
						$("#epAUX").val(aData[0]);				
						if ($("#nIdEstado").val() == "1" ){
							 var index=($("#epAUX").val());
							  var tmp = index.lastIndexOf( "\." );
					          var uEje= index.substring( tmp - 3, tmp);
					            $("#cIdUnidadEjecutora").val(uEje)
								queryFormPost("quitaEPContratoPasivoPlurianualDelete", {async: false});
								loadClavesPresupuestalesContrato();
					 	}
				});	
			}
				function loadClavesPresupuestalesContrato(){
					if(roles.toString().indexOf("ADMIN_RECMAT") < 0 &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0){
						var qw = " cEjercicio = '" + $("#cEjercicio").val() +
						 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
						 "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutoraEP").val() +
						 "' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";	
					 }else{
						 
						 var qw = " cEjercicio = '" + $("#cEjercicio").val() +
						 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
						 "' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";
					}
			 		
				oTableClaves=$('#dt_clavepresup').dataTable( {
						bPaginate: false,
	        			bLengthChange: false,
	        			bFilter: false,
	        			bInfo: false,
						bAutoWidth: true,
						bScrollCollapse: true,
						bJQueryUI: true,
						bDestroy : true,
						bServerSide: true,   
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
						aoColumns: [
							{ sName: "nIdClaveEgresos"},
							{ sName: "ClaveInterna" }
							]
				}) ;
			}
			function initDataTable(){
				$('#dt_clavepresup').dataTable(
					{         
		   			    "bPaginate": false,
	        			"bLengthChange": false,
	        			"bFilter": false,
	        			"bSort": false,
	        			"bInfo": false,
	        			"bAutoWidth": false, 
				        "bScrollCollapse": true,
						"bJQueryUI": true,    
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers"    
					} );
			}		
			function setReadOnly(){
				document.getElementById("lblNotas").style.readonly=true;
			}
		function headerQuery(){
			queryFormPost("mContratoPlurianualidadCaratulaRead", {async: false});
			queryFormPost("datosContratoPlurianual", {async: false});
			queryFormPost("cg_roleRead", {async: false});
			queryFormPost("getcIdUsuarioCreacionContrato", {async: false});
			//verifica si hay motivo rechazo
			queryFormPost("motivoRechazoPasivo", { async:false });
			
			if($("#nIdEstado").val()!="5"){
			$("#trNotas").hide();
			}
		}
		function fnClickAddRowC() {
			rowCount = $('#dt_clavepresup tr').length;
			var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes(); 
			var vep = $('#ep').val();
			if (vep == '') 
				return ;
			for(var i=0;i<aTrs.length;i++){
				// leer eps en tabla tPedidoEP_TMP y no inserta las que ya estan repetidas para evitar errores de primary key
				queryFormPost("epsReadContratoPlurianual", {async: false});
				if(parseInt($("#epsConsulta").val(),10)>0){
					swal("Ya existe est\u00e1 clave ingrese otra.","info",{ button: "Cerrar"});
					return;
				}
			}
			var tmp = vep.lastIndexOf( "\." );
			var uEje= vep.substring( tmp - 3, tmp);
			var cint = vep.substring( tmp -3 );
			$("#cIdUnidadEjecutoraEP").val(uEje);
			if(parseInt($("#nTipoPago").val(),10)==1){
				$("#unidadEjecutoraCA").val(uEje);
				queryFormPost("readCCentroContableUE", {async: false});
			}
			$("#ClaveInterna").val(cint);
			queryFormPost("agregaEPContratoCreatePasivoPlurianual", {async: false});
			loadClavesPresupuestalesContrato();
			$('#ep').val("");

		}
		
		
	
				
			function buscaClavePresupuestal(){
				window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
					+ '&cIdDocumento=' + $('#cIdContratoDefinitivoPlurianual').val()+ '&isPlurianual=1'
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px, top=10px');
			}
			function fnGetSelected( oTableLocal ){
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();			
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('row_selected') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}
			function guardaContrato(){
			 var imgAprobar='<%=aprobar%>';
			 if (imgAprobar==0){
					var proc=""+$("#cEjercicio").val()
						+","+$("#cContratoDefinitivo").val()
						//+","+$("#cIdSubPartida").val()
						+","+$("#cIdUnidadEjecutora").val()
						+","+$("#U_LOGIN").val()
						+","+$("#nTipoPago").val()+"";
					if(parseFloat($("#mTotalRemanente").val())<0){
						swal("No puedes aprobar el contrato plurianual por que sobrepasas el monto remanente del contrato.","info",{ button: "Cerrar"});
						return;
					}
					if($("#lHabilitadoProveedor").val()!=1){
						swal("El Proveedor se encuentra Des-Habilitado.","info",{ button: "Cerrar"});
						return;
					}	
        			 $.getJSON("../../servlet/ContratoPlurianualidadServlet?operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
			                case "-1":
			                		swal(j[i].MSG,"info",{ button: "Cerrar"});
			                	break;
							case "0": 
								swal({
									title: "",
									text: "El ContratoPlurianual se ha aprobado y está en el SAI",
									icon: "info",
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										$("#nIdClaveEgresosXPlurianualidad").hide();
										$("#AgregarEPPlurianualidad").hide();
										$("#imgAprobarPlurianualidad").hide();
										var imgDevolver='<%=devolver%>';
										if (imgDevolver==0){
											$("#imgDevolverPlurianualidad").show();
										}
										$("#nIdEstadoPed").val("2");
										window.location='Plurianualidad.jsp?tab='+3
								});
							break;
							case "1":  
								swal("Est\u00e9 ContratoPlurianual ya se encuentra aprobado","info",{ button: "Cerrar"});
							break;
							case "2": 
								swal("Est\u00e9 proveedor no se encuentra registrado en el sistema Financiero","info",{ button: "Cerrar"});
							break;
							case "3":  
								swal("No se han registrado EPs para este ContratoPlurianual","info",{ button: "Cerrar"});
							break;
							case "4":  
								swal("Ha ocurrido un error al registrar el ContratoPlurianual, por favor contacte a su administrador","info",{ button: "Cerrar"});
							break;
							case "5":  
								swal("Ha ocurrido un error al registrar las EPs, por favor contacte a su administrador","info",{ button: "Cerrar"});
							break;
							case "6","7":  
								swal("Ha ocurrido un error al actualizar el ContratoPlurianual, por favor contacte a su administrador","info",{ button: "Cerrar"});
							break;
	                 	}
				});
			 }else{
				 swal("Usted no tiene permisos para realizar esta accion, por favor contacte a su administrador","info",{ button: "Cerrar"});
			 }
			
		}
		function devuelveContrato(){
			var imgDevolver='<%=devolver%>';
			if (imgDevolver==0){
			if(parseInt($("#nIdEstado").val(),10)<3 || parseInt($("#nIdEstado").val(),10)==5 ){
			 var proc=""+$("#cEjercicio").val()
						+","+$("#cContratoDefinitivo").val()
						+","+$("#cIdUnidadEjecutora").val()
						+","+$("#cIdContratoMat").val()+"";
						
				$.getJSON("../../servlet/ContratoPlurianualidadServlet?operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
						case "0":  
							swal("Se ha devuelto el ContratoPlurianual","info",{ button: "Cerrar"});
							$("#nIdClaveEgresosXPlurianualidad").show();
							$("#AgregarEPPlurianualidad").show();
							var imgAprobar='<%=aprobar%>';
							if (imgAprobar==0){
								$("#imgAprobarPlurianualidad").show();
							}
							$("#imgDevolverPlurianualidad").hide();
							$("#nIdEstadoPed").val("1"); 
						break;
						case "1":  
							swal("No se puede regresar el ContratoPlurianual porque su estado no lo permite","info",{ button: "Cerrar"});
						break;
						case "2":  
							swal("Ha ocurrido un error al eliminar las EPs del Contrato, por favor contacte a su administrador","info",{ button: "Cerrar"});
						break;
						case "3":  
							swal("Ha ocurrido un error devolver el ContratoPlurianual, por favor contacte a su administrador","info",{ button: "Cerrar"});
						break;
						case "4":  
							swal("Ha ocurrido un error al cambiar el estado del ContratoPlurianual, por favor contacte a su administrador","info",{ button: "Cerrar"});
						break;
	                 	}
				});
			}
				window.location='Plurianualidad.jsp?tab='+3
		}else{
			 swal("Usted no tiene permisos para realizar esta acci\u00f3n, por favor contacte a su administrador","info",{ button: "Cerrar"});
		}
		
	}
	function showLabel(){
		$("#trmTotalRemanente").css("display","none");
		if(parseFloat($("#mTotalRemanente").val())<0){
			$("#trmTotalRemanente").css("display","");
		}
		
	}
		
	</script>
</head>  
<body >
	<form id="frmPresupuesto">
    	<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Presupuesto de la plurianualidad</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarPlurianualidad" name="imgAprobarPlurianualidad" 	value="Aprobar"	onclick="guardaContrato();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverPlurianualidad" name="imgDevolverPlurianualidad" 	value="Devolver"	onclick="devuelveContrato();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'Plurianualidad.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblcIdContratoDefinitivoPluri" id="lblcIdContratoDefinitivoPluri"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblcNoContratoCNET" id="lblcNoContratoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDescContrato" id="lblDescContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblOficioDG" id="lblOficioDG"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblFolioMASCP" id="lblFolioMASCP"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lbcContratoAbierto" id="lbcContratoAbierto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lbcContratoCentralizado" id="lbcContratoCentralizado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstatus" id="lblEstatus"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblMontoIVA" id="lblMontoIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col" id="trmTotalRemanente" style="color: red; display: none;">
						<span style="color: red;">El monto de tu plurianual no puede ser mayor al remanente.</span>
					</div>
				</div>
				<div class="row">
					<div class="col" id="trNotas" >
						<label for="lblNotas">Motivo del rechazo:</label>
						<textarea class="form-control" id="lblNotas" name="lblNotas" rows="3" readonly style="color:red;"></textarea>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Claves</legend>
			<div class="form-group">
				<div class="row">
					<div class="col-md-6">
						<label for="ep">E.P.</label>
						<input type="text" class="form-control" placeholder="Seleccione la nueva estructura presupuestal que desea agregar."  
						id="ep" name="ep" readonly>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="nIdClaveEgresosXPlurianualidad" name="nIdClaveEgresosXPlurianualidad" value="..." onclick="buscaClavePresupuestal()" 
						class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start" title="Dar clic para mostrar las estructuras presupuestales con recurso disponible."/>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="AgregarEPPlurianualidad" name="Add2" value="Agregar" onclick="fnClickAddRowC()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>C&oacute;digo SAI</th> 
									<th>Clave SHCP</th> 
							 	</tr> 
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>

        <!-- Hiddens de sesion -->
        <input type="hidden" name="cIdContratoMat" id="cIdContratoMat" value="<%=cIdContrato%>"/>
        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
         <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
         <input type="hidden" name="cIdTipoCambio" id="cIdTipoCambio" value="<%=cIdTipoCambio%>"/>
	     <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%= cIdContratoDefinitivo %>"   />
	     <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cITipoContrato%>"/>
	     		    
	    <input type="hidden" name="cIdSubPartida" id="cIdSubPartida"/>
	     <input type="hidden" id="nOrden" name="nOrden" value=""/>
	    <input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" id="ClaveInterna" name="ClaveInterna" />
	    <!--  Auxiliares para consulta -->		    
       	<input type="hidden" name="epAUX" id="epAUX" />
       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuarioTab.getU_UR() %>"/>
       	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	    
	    <!-- Resultado de consultas -->
	     <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
	     <input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable %>" />	
	    <input type="hidden" name="nTipoPago" id="nTipoPago" />
	    <input type="hidden" name="epsConsulta" id="epsConsulta" />	
		<input type="hidden" name="nIdEstado" id="nIdEstado"/>
		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cIdContrato %>"  />
   		<input type="hidden" name="cIdContratoDefinitivoPlurianual" id="cIdContratoDefinitivoPlurianual" value="<%=cIdContratoDefinitivo %>"  />
   		<input type="hidden" name="mTotalRemanente" id="mTotalRemanente" value="0"/>
   		<input type="hidden" name="unidadEjecutoraCA" id="unidadEjecutoraCA" value="0"/>
   		<input type="hidden" name="cIdRFC" id="cIdRFC"/>
   		<input type="hidden" name="lHabilitadoProveedor" id="lHabilitadoProveedor" value="0"/>
	</form>
  </body>
</html>
