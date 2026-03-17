<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();

 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>

    
    <title>ConsultaRecepcion</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(1);
			
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"RecepcionMaterial","ConsultaRecepcion");
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
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			querySelectPost("readEstatusRecepMat", "estatusRecepMat", {async: false});
			
			//selección de valores por default en los dropdownlist
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
			initTabla();
			
			$('#tblConsulta tr').live('dblclick', function() { 
				        
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableConsulta );
				if (anSelected != "") {
					var aData = oTableConsulta.fnGetData(anSelected[0]);
					window.location = aData[0];
				}
				
			});
		});
		function fnGetSelected( oTableLocal )
		{
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
		function  initTabla(){
			var qw="1=1 and cUnidadEjecutora='"+$("#cIdUnidadEjecutora").val()+"'";
			if($("#pedContrato").val()!=''){
				qw=qw+" and cIdpedContDef LIKE '%25"+$("#pedContrato").val()+"%25'";
			}
			if($("#recepMat").val()!=''){
				qw=qw+" and cIdRecepMat LIKE '%25"+$("#recepMat").val()+"%25'";
			}
			if($("#estatusRecepMat").val()!='0'){
				qw=qw+" and nIdEstadoRecepMat='"+$("#estatusRecepMat").val()+"'";
			}
			
			oTableConsulta = $("#tblConsulta").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mConsultaSolAbastecimiento&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 2, "asc" ],[3, "asc"]] ,
				aoColumns: [
					{sName: "cVinculo",bVisible: false},
					{sName: "nIdConsecutivoRecepM",bVisible: false},
					{sName: "cIdpedContDef"},
					{sName: "cIdRecepMat"},
					{sName: "cUnidadEjecutora"},
					{sName: "estatus"},
					{sName: "nCantidad"},
					{sName: "mMontoConIVA"},
					{sName: "mMontoSinIVA"},
					{sName: "mMontoIVA"},
					{sName: "mMontoConIVATotal"},
					{sName: "mMontoSinIVATotal"},
					{sName: "mMontoIVATotal"},
					{sName: "mDescuentoConIVATotal"},
					{sName: "mDescuentoSinIVATotal"},
					{sName: "mDescuentoIVATotal"}
				]
			});
		
		}
		function cambiaCentrocontableUsuario(){
			if($("#cIdUnidadEjecutora").val()!='0'){
				$.ajax({
					url: '../../servlet/CambiaPropiedadesUsuario',
					dataType: 'json',
					data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
					async : false,
					success : function(j) {
						if(j[0].error){
							swal("No se hizo el cambio de centro contable y unidad ejecutora.",{icon:"warning",button: "Cerrar"});
						}else{
							$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
						}
					}
				});
			}
		}
	</script>
  </head>
  
  <body>
    <form id="formConsultaRecep">
    	<div id="container" class="container" style="width: 90%;">
    		<div class="row">
    			<div class="col-3">
    				Unidad Ejecutora:
    			</div>
    			<div class="col-6">
    				<select name='cIdUnidadEjecutora' id='cIdUnidadEjecutora' class="form-select"  onchange="cambiaCentrocontableUsuario();"></select>
    			</div>
    		</div>
    		<div class="row">
    			<div class="col-3">
    				Pedido/Contrato:
    			</div>
    			<div class="col-6">
    				<input type="text" id="pedContrato" name="pedContrato" value="" class="form-control" />
    			</div>
    		</div>
    		<div class="row">
    			<div class="col-3">
    				Recepci&oacute;n Material:
    			</div>
    			<div class="col-6">
    				<input type="text" id="recepMat" name="recepMat" value="" class="form-control"/>
    			</div>
    		</div>
    		<div class="row">
    			<div class="col-3">
    				Estatus:
    			</div>
    			<div class="col-6">
    				<select name='estatusRecepMat' id='estatusRecepMat'  class="form-select"></select>
    			</div>
    			<div class="col-3">
    				<input type="button" id="buscarRecepMat" name="buscarRecepMat" value="Buscar" onclick="initTabla()" class="btn btn-secondary"/>
    			</div>
    		</div>
    		<br/>
    		<table id="tblConsulta" class="display" >
					<thead >
						<tr>
							<th style="display: none;"></th>
							<th style="display: none;"></th>
							<th align="center">Pedido/Contrato</th>
							<th align="center">Sol. Abastecimiento</th>
							<th align="center">Unidad<br />Ejecutora</th>
							<th align="center">Estatus</th>
							<th align="center">Cantidad</th>
							<th align="center">Monto Con<br/> IVA Amortizado</th>
							<th align="center">Monto Sin<br/> IVA Amortizado</th>
							<th align="center">Monto Iva<br/> Amortizado</th>
							<th align="center">Monto Con <br/> IVA Total</th>
							<th align="center">Monto Sin <br/> IVA Total</th>
							<th align="center">Monto Iva<br/> Total</th>
							<th align="center">Descuento Total  <br/> Con IVA </th>
							<th align="center">Descuento Total<br/>Sin IVA </th>
							<th align="center">Descuento Total<br/> IVA</th>													
						</tr>										
					</thead>
				</table>
    	</div>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    </form>
  </body>
</html>
