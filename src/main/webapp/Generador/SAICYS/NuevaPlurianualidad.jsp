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
	String role="";
	String roles="";
	Map rol =usuarioTab1.getRoles();
	int cEjercicio = 0;
	cEjercicio = Calendar.getInstance().get(Calendar.YEAR);//(String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
	
%>

<!doctype html>
<html>
  <head>
    <title>Nueva Plurianualidad</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var cEjercicio;
		var oTable;
		$(document).ready(function() {
			tabb=0;
			showAndHideTabs();
			// se deshabilitan las pestañas de presupuesto y precompromiso plurianual
			deshabilitaTabs();
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"PlurianualidadContratos","nuevaPlurianualidad");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

			%>		
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			$('#tblNuevaPlurianualidad').on('dblclick', 'tr',function(){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );
					var aData = oTable.fnGetData(anSelected[0]);
					$('#cejercicioPlurianual').val("<%=cEjercicio%>");
					if(aData[1].indexOf("PLU") >= 0){
						$('#cIdDefinitivoPlurianual').val(aData[1]);
					}else{
						$('#cIdDefinitivoPlurianual').val("PLU-"+aData[1]);
					}
					$('#cIdDefinitivo').val(aData[1]);
			  		if(aData!= ""){
				  		queryFormPost("sp_plurianualidadInserta", {async: false,
				  			callback : function() 
							{
								alert("Plurianulidad Creada.");
						  		// se habilitan las pestañas de presupuesto y precompromiso plurianual al momento del doble click del renglon
						  		window.location = 'Plurianualidad.jsp?tab=1';
				  			}
				  		});
			  		}
			  	//}
			});
			//Llenado de tabla y combo box por medio del CRUD
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			mostrar();
		});
		function mostrar() {
		 	var qw = " 1=1 ";  
			if($("#cIdUnidadEjecutora").val()!=0){
				qw += " and cIdUnidadEjecutora in('" + $("#cIdUnidadEjecutora").val()+"','A10')";
			}
	        if($("#cIdDefinitivo").val()!=""){
		        qw+=" and  cIdContratoDefinitivo LIKE '%25"+$("#cIdDefinitivo").val()+"%25'";
		    }
		    if($("#cDescripcion").val()!=""){
		          qw += " and  cConceptoContrato LIKE '%25"+$("#cDescripcion").val()+"%25'";
		    }
		    if($("#cIdRFC").val()!=""){
		          qw += " and  cIdRFC LIKE '%25"+$("#cIdRFC").val()+"%25'";
		    }
			oTable = $("#tblNuevaPlurianualidad").dataTable({
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
					sEmptyTable: "No hay convenios modificatorios",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtrado de _MAX_ registros)",
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
				bProcessing: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratosPlurianuales&qw="+ qw,
				sPaginationType: "full_numbers",
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
				   	{ sName: "cidunidadEjecutora" },
					{ sName: "cIdContratoDefinitivo" },
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial" },
					{ sName: "cConceptoContrato" },
					{ sName: "mMontoTotalPlurianual" },
					{ sName: "mMontoEjercido" },
					{ sName: "mMontoPorEjercer" },
					{ sName: "cIdProcedimiento" },
					{ sName: "cEjercicioOriginal" }
					]
        	});
		}
		function editarPorcentajePlurianualidad(cEjercicio, cIdContratoDefinitivo){
			if(confirm("\xBFEstas seguro de actualizar los datos?")){
				$("#cEjercicioUpdate").val(cEjercicio);
				$("#cIdContratoUpdate").val(cIdContratoDefinitivo);
               	$("#nPorcentajeIVA").val($("#"+cIdContratoDefinitivo+"-"+cEjercicio).val());
				queryFormPost("actualizaPorcentajePlurianualidad", {async: false});
			}
		}
       	function deshabilitaTabs(){
			$( "#presupuestoPlurianualidad" ).attr("disabled", true);
			$( "#precompromisoPlurianualidad" ).attr("disabled", true);
			$( "#caratulaPlurianualidad" ).attr("disabled", true);
			$( "#nuevasEps" ).attr("disabled", true);
		}
		function habilitaTabs(){
	 		$( "#presupuestoPlurianualidad" ).attr("disabled", false);
			$( "#precompromisoPlurianualidad" ).attr("disabled", false);
			$( "#caratulaPlurianualidad" ).attr("disabled", false);
			$( "#nuevasEps" ).attr("disabled", false);
			//$( "#ampliacionContratoPluri" ).attr("disabled", false);
			
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
		function cambiaCentrocontableUsuario(){
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						alert("No se hizo el cambio de centro contable y unidad ejecutora");
					}else{
						$("#cUnidadEjecutora").val(j[0].unidadEjecutora);
					}
				}
			});
		}
	</script>
</head>
  <body>
  	<form id="formNuevo">
  		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Nueva Plurianualidad</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
								<option value="<%=usuarioTab1.getU_UR()%>" selected="selected">
							</select>
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
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo PLU-CV-A04-1/2022" aria-label="Número de Contrato SAI, Ejemplo PLU-CV-A04-1/2022" aria-describedby="basic-addon1"  name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cDescripcion">Objeto del contrato: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Concepto de la contratación" aria-label="Concepto de la contratación" aria-describedby="basic-addon1"  name="cDescripcion" id="cDescripcion"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdRFC">RFC: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="RFC del proveedor" aria-label="RFC del proveedor" aria-describedby="basic-addon1"  name="cIdRFC" id="cIdRFC"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscarPlurianualidad" name="btnBuscarPlurianualidad" 	value="Buscar"	onclick="mostrar();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblNuevaPlurianualidad" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th>UNIDAD<BR/> EJECUTORA</th>
				        			<th>CONTRATO</th>
				        			<th>PROVEEDOR</th>
				        			<th>RAZ&Oacute;N<BR/> SOCIAL</th>
				        			<th>CONCEPTO</th>			
				        			<th>MONTO<BR/> TOTAL <BR/>PLURIANUAL</th>
				        			<th>MONTO<BR/> EJERCIDO</th>
				        			<th>MONTO POR<BR/> EJERCER</th>
				        			<th>PROCEDIMIENTO</th>
				        			<th>EJERCICIO<BR/> CONTRATACI&Oacute;N</th>
				        		</tr>										
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>
  		<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" />
  		<input type="hidden" name="cEjercicioUpdate" id="cEjercicioUpdate" />
  		<input type="hidden" name="cIdContratoUpdate" id="cIdContratoUpdate" />
  		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
		<input type="hidden" name="cIdUnidadEjecutora1" id="cIdUnidadEjecutora1" value="<%=usuarioTab1.getU_UR()%>" />
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" />
  		<input type="hidden" name="cIdContrato" id="cIdContrato" />
  		<input type="hidden" name="cIdDefinitivoPlurianual" id="cIdDefinitivoPlurianual" value="" />
  		<input type="hidden" name="cejercicioPlurianual" id="cejercicioPlurianual" value="" />
  		<input type="hidden" name="montoBruto" id="montoBruto" value="" />
  		<input type="hidden" name="porcentajeIva" id="porcentajeIva" value="" />
  		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
  		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab1.getLogin()%>"/>
  		<input type="hidden" name="nIdEstado" id="nIdEstado"/>
		<input type="hidden" name="lContratoAbierto" id="lContratoAbierto"/>
	</form>
  </body>
  
</html>
