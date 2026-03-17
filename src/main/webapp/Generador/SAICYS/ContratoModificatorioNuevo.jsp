<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab1.getLogin();
	String roles="";
	Map rol =usuarioTab1.getRoles();
	int cEjercicio = 0;
	cEjercicio = Calendar.getInstance().get(Calendar.YEAR);
	
%>

<!DOCTYPE html>
<html>
  <head>
    <title>Nueva Modificación al Contrato</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">

	var cEjercicio;
	var oTable;
	var roles="";
		$(document).ready(function() {
			tabb=0;
			showAndHideTabs();
			
			oTable=$("#tblContratosAprobados").dataTable({
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdConsecutivo" },
					{ sName: "cIdContrato" },
					{ sName: "cIdContratoDefinitivo" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoContrato" },
					{ sName: "mMontoBruto" },
					{ sName: "mMontoNeto" }
					],
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}			
        	});
		
			<%
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
				Map botones=nb.getBotones(roles,"ContratoModificatorio","nuevoContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

			%>		
			roles="<%=roles%>";
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			queryFormPost("mContratoModicadoChecaRolUsuario", {async: false});
			querySelectPost("tipoModificacion", "cIdTipoMod", {async: false });
			
			$('#tblContratosAprobados').on('dblclick', 'tr',function(){
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
					
				var anSelected = fnGetSelected( oTable );
				var aData = oTable.fnGetData(anSelected[0]);
				
				$('#cIdContrato').val(aData[2]);
				$('#isConvEjercicioAnt').val(aData[8]);
				
				$('#contratoDefinitivo').val(aData[3]);
				//(roles.toString().indexOf("ADMIN_RECMAT") < 0)
				if ((roles.toString().indexOf("ADMIN_RECMAT") < 0)) { 
					$("#usuarioCreacionOriginal").val('');
					queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
					if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val() && (roles.toString().indexOf("ANALISTA") < 0) && (roles.toString().indexOf("JEFES") < 0)&& (roles.toString().indexOf("Estatales") < 0)){
						swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
						return;
					} 
				}
				
				$('#cIdTipoModNot').val(1);
				if ($('#cIdTipoMod').val() == 1) {
					$('#cIdTipoModNot').val(0);	
				}
				$('#contratoPendiente').val('');
				queryFormPost("mContratoModificadoPendiente",{async: false });
				if ($('#contratoPendiente').val() == 'PENDIENTE'){
					swal("No es posible generar un modificatorio a este contrato ya que hay modificatorios pendientes o el tipo de modificación no esta permitido.",{icon:"info",button: "Cerrar"});
					return;
				}
				var cIdContratoDef=$("#contratoDefinitivo").val();
				if(cIdContratoDef.indexOf("PLU")>=0 && $('#esPorTotalPlurianual').is(':checked') ){
					$("#bEsXTotalPlu").val(1);
				}
		  		queryFormPost("totalContratoModificadoAnterior",{async: false });
		  		var msg="";
		  		if ($('#totalAnterior').val() != ''){
		  			$('#consecutivoMod').val(parseInt($('#consecutivoMod').val(),10) + 1);
		  			queryFormPost("mContratoModificadoCreate",{async: false });
		  			msg="Modificación " + $('#consecutivoMod').val() + " creada existosamente";
		  		}
		  		else {
		  			$('#totalAnterior').val(aData[7]);
		  			$('#consecutivoMod').val(1);
		  			queryFormPost("mContratoModificadoCreate",{async: false });
		  			msg="Primera modificación creada existosamente.";
		  		}
		  		
		  		var cMod = $('#consecutivoMod').val();
		  		var cDefinitivo = $('#contratoDefinitivo').val();
		  		var cContrato = $('#cIdContrato').val();
		  		var cEjer = $('#cEjercicio').val();
			  	
		  		swal({
		  			title: "",
		  			text: msg,
		  			icon: "info",
		  			buttons: {
		  				confirm : "Cerrar"
		  				},
		  			}).then((continuar) => {
		  				window.location = 'ContratoModificatorio.jsp?tab=2' + '&mod=' + cMod + '&cDefinitivo=' + cDefinitivo + '&cEjercicio=' + cEjer 
					  	+ '&cContrato=' + cContrato+ '&isConvEjercicioAnt=' + aData[8];
		  		});
		  		
			  	
			});
			
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback : function() {
					$("#cIdUnidadEjecutora").val($("#cIdUnidadResponsableUsuario").val());
				}
			});
		});		
		
		function mostrar() {
			var qw = " cEstado='APROBADO' ";  
			if($("#cIdUnidadEjecutora").val() != 0){
				qw += " and cIdUnidadEjecutora='" + $("#cIdUnidadEjecutora").val() + "'";
			}
	        if($("#cIdDefinitivo").val() != ""){
		        qw+=" and  cIdContratoDefinitivo LIKE '%25" + $("#cIdDefinitivo").val() + "%25'";
		    }
		    if($("#cDescripcion").val() != ""){
		          qw += " and  cConceptoContrato LIKE '%25" + $("#cDescripcion").val() + "%25'";
		    }
		    if($("#cContrato").val()!=""){
		          qw += " and  cIdContrato LIKE '%25" + $("#cContrato").val() + "%25'";
		    }
		    if($("#cIdRFC").val()!=""){
		          qw += " and  cIdRFC LIKE '%25" + $("#cIdRFC").val() + "%25'";
		    }

			oTable = $("#tblContratosAprobados").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay contratos",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_consultaContratosMod&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
				   	{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdConsecutivo" },
					{ sName: "cIdContrato" },
					{ sName: "cIdContratoDefinitivo" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoContrato" },
					{ sName: "mContratoMontoBruto" },
					{ sName: "mContratoMontoNeto" },
					{ sName: "isConvEjercicioAnt" ,bVisible: false}
					]
        	});	
		}
		
		function fnGetSelected( oTableLocal ) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ ) {
				if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		function cambiaCentrocontableUsuario(){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"info",button: "Cerrar"});
					}else{
						$("#cIdUnidadEjecutora1").val(j[0].unidadEjecutora);
						$("#cIdUnidadResponsableUsuario").val(j[0].unidadEjecutora);
						
					}
				}
			});
		}	
	</script>
</head>

  <body>
  	<form id="formNuevo">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Nuevo Contrato Modificatorio</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cContrato">N&uacute;mero de Contrato : </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CV-A04-1" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1" aria-describedby="basic-addon1"  name="cContrato" id="cContrato"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdDefinitivo">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CV-A04-1/2022" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1/2022" aria-describedby="basic-addon1"  name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cDescripcion">Concepto: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Objeto del contrato" aria-label="Objeto del contrato" aria-describedby="basic-addon1"  name="cDescripcion" id="cDescripcion"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdRFC">R.F.C: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="RFC del Proveedor" aria-label="RFC del Proveedor" aria-describedby="basic-addon1"  name="cIdRFC" id="cIdRFC"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscarContratos" name="btnBuscarContratos" 	value="Buscar"	onclick="mostrar();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend>Datos de captura para la generación del convenio modificatorio</legend>
			<div class="form-group">
				<div class="row" >
					<div class="input-group">
						<div class="col-2">		
							<label class="form-check-label" for="esPorTotalPlurianual">¿Es por el total plurianual el convenio modificatorio? </label>
						</div>
						<div class="col-4">
			  				<input class="form-check-input" type="checkbox" id="esPorTotalPlurianual" name="esPorTotalPlurianual" >
			  			</div>
		  			</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdTipoMod">Tipo de Modificaci&oacute;n: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdTipoMod" name="cIdTipoMod" >
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblContratosAprobados" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th >UNIDAD EJECUTORA</th>
						  		    	<th >#</th>
						  				<th >ID CONTRATO</th>
						  				<th >CONTRATO DEFINITIVO</th>
						  				<th >PROVEEDOR</th>
						  				<th >CONCEPTO</th>			
						  				<th >MONTO BRUTO</th>
						  				<th >MONTO NETO</th>
						  				<th style="display: none;" >Convenio Anterior</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
  		<input type="hidden" name="cEjercicio" id="cEjercicio" />
  		<input type="hidden" name="totalAnterior" id="totalAnterior" value="" />
  		<input type="hidden" name="consecutivoMod" id="consecutivoMod" />
  		<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" />
  		<input type="hidden" name="cEjercicioUpdate" id="cEjercicioUpdate" />
  		<input type="hidden" name="cIdContratoUpdate" id="cIdContratoUpdate" />
		<input type="hidden" name="cIdUnidadEjecutora1" id="cIdUnidadEjecutora1" value="<%=usuarioTab1.getU_UR()%>" />
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" />
  		<input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
  		<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
  		
  		<input type="hidden" name="cIdContrato" id="cIdContrato" />
  		<input type="hidden" name="contratoPendiente" id="contratoPendiente" value="" />
  		<input type="hidden" name="cIdTipoModNot" id="cIdTipoModNot" />
  		<input type="hidden" name="bEsXTotalPlu" id="bEsXTotalPlu" value="0"/>
  		<input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0"/>
  		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
  		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
  		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab1.getLogin()%>"/>
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
  	</form>
  </body>
  
</html>
