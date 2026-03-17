<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
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
 %>

<!DOCTYPE html>
<html>
  <head>
   <title>Consulta Contrato</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		var cEjercicio;
		var oTable;
		var roles="";
		$(document).ready(function() {
			tabb=0;
			showAndHideTabs();
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
				Map botones=nb.getBotones(roles,"ContratoAnterior","nuevoContratoAnterior");
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
			initTable();
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback : function() {
					$("#cIdUnidadEjecutora").val("<%=usuarioTab1.getU_UR()%>");
					mostrar();
				}	
			});
			
			$('#tblContratosAprobados').on('dblclick', 'tr',function(){
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
					
				var anSelected = fnGetSelected( oTable );
				var aData = oTable.fnGetData(anSelected[0]);
				fillInputs(aData);
				
	  			queryFormPost({queryName: "mContratoRemanenteEjercicioAnteriorCreate",async: false,
	  				callback: function() {
	  					swal({
	  						title: "",
	  						text: "Proceso de Registro Creado Correctamente",
	  						icon: "info",
	  						buttons: {
	  							confirm : "Cerrar"
	  						},
	  					}).then((continuar) => {
  							var cDefinitivo = $('#contratoDefinitivo').val();
  					  		var cContrato = $('#cIdContrato').val();
  					  		var cEjer = $('#cEjercicio').val();
  						  	window.location = 'ContratoAnterior.jsp?tab=2' + '&cDefinitivo=' + cDefinitivo + '&cEjercicio=' + cEjer 
  						  	+ '&cContrato=' + cContrato+ '&cIdUnidadEjecutora=' + $("#cIdUnidadEjecutora").val();
	  					});
					}
				});
			});
		});//Fin del document ready
		function initTable(){
			oTable=$("#tblContratosAprobados").dataTable({
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
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
						{ sName: "cIdContrato" },
						{ sName: "cIdRFC" },
						{ sName: "cConceptoContrato" },
						{ sName: "mImporteTotal" },
						{ sName: "mImportePago" },
						{ sName: "mRemanenteContrato" },
						{ sName: "nIdFundamentoLeg",bVisible: false  },
						{ sName: "nIdCategoria",bVisible: false  },
						{ sName: "numProcedCNET",bVisible: false  },
						{ sName: "fFallo",bVisible: false  },
						{ sName: "cNoContratoCNET",bVisible: false  },
						{ sName: "cObservaciones",bVisible: false  },
						{ sName: "cOficioDG",bVisible: false  },
						{ sName: "cFolioMASCP",bVisible: false  },
						{ sName: "nCodContratoCNET",bVisible: false  },
						{ sName: "nCodExpedienteCNET",bVisible: false  },
						{ sName: "ITieneAnticipo",bVisible: false  }
						
					],
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}			
	    	});
		}
		function fillInputs(aData){
			$('#cIdContrato').val(aData[0]);				
			$('#contratoDefinitivo').val(aData[0]);		  			
  			$('#RFC').val(aData[1]);
  			$('#cConcepto').val(aData[2]);
  			$('#mTotalContratoAnterior').val(aData[3]);
  			$('#mTotalPagado').val(aData[4]);
  			$('#mTotalRemanente').val(aData[5]);
  			$('#nIdFundamentoLeg').val(aData[6]);
  			$('#nIdCategoria').val(aData[7]);
  			$('#numProcedCNET').val(aData[8]);
  			$('#fFallo').val(aData[9]);
  			$('#cNoContratoCNET').val(aData[10]);
  			$('#cObservaciones').val(aData[11]);
  			$('#cOficioDG').val(aData[12]);
  			$('#cFolioMASCP').val(aData[13]);
  			$('#nCodContratoCNET').val(aData[14]);
  			$('#nCodExpedienteCNET').val(aData[15]);
  			$('#ITieneAnticipo').val(aData[16]);	
		}
		function mostrar() { 
			$.blockUI({message: "Procesando espere ......"});
			var UE;
			if($("#cIdUnidadEjecutora").val() != 0){
				UE=$("#cIdUnidadEjecutora").val();
			}else{
				UE="%";
			}
			
		   if($("#cDescripcion").val() == ""){
		        $("#cDescripcion").val("%");
		    }
		    if($("#cContrato").val()==""){
		          $("#cContrato").val("%");
		    }
		    if($("#cIdRFC").val()==""){
		           $("#cIdRFC").val("%");
		    }
		    var zTabla = "REMANENTEEJERCICIOANTERIOR";		
			var campos = "'"+UE+"','"+$("#cDescripcion").val()+"','"+$("#cContrato").val()+"','"+$("#cIdRFC").val()+"'";
		    oTable.fnClearTable();
		    $('#tblContratosAprobados').dataTable().fnClearTable();
		    oTable=$("#tblContratosAprobados").dataTable({
		    	sScrollX: "100%",
				bAutoWidth : true,
				bPaginate:false,
				bFilter : false,
				bDestroy:true,
				bRetrive:true,
				bLengthChange: false,
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
					sSearch: "Buscar:"
					
				},
				bProcessing: true,
				bJQueryUI: true,
				aaSorting: [[ 1, "desc" ]] ,
				aoColumns: [
					{ sName: "cIdContrato" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoContrato" },
					{ sName: "mImporteTotal" },
					{ sName: "mImportePago" },
					{ sName: "mRemanenteContrato" },
					{ sName: "nIdFundamentoLeg",bVisible: false  },
					{ sName: "nIdCategoria",bVisible: false  },
					{ sName: "numProcedCNET",bVisible: false  },
					{ sName: "fFallo",bVisible: false  },
					{ sName: "cNoContratoCNET",bVisible: false  },
					{ sName: "cObservaciones",bVisible: false  },
					{ sName: "cOficioDG",bVisible: false  },
					{ sName: "cFolioMASCP",bVisible: false  },
					{ sName: "nCodContratoCNET",bVisible: false  },
					{ sName: "nCodExpedienteCNET",bVisible: false  },
					{ sName: "ITieneAnticipo",bVisible: false  }
				]			
	    	});
		    var  elParametro2 ='';		    
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: zTabla, Param: elParametro2,Campos:campos, MaxReg: "10", ajax: 'false'}, function(j){		    							
				arrayCompleto=new Array();
				for (var i = 0; i < j.length; i++){
					arrayCompleto [i]=[ j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5 
						,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12
						,j[i].Col13,j[i].Col14,j[i].Col15,j[i].Col16
						];
				}
				if(j.length>0){
					$('#tblContratosAprobados').dataTable().fnAddData(arrayCompleto);
					$("#tblContratosAprobados").dataTable().fnAdjustColumnSizing();
					$.unblockUI();
				}
					
			});	
			
		   if($("#cDescripcion").val() == "%"){
		        $("#cDescripcion").val("");
		    }
		    if($("#cContrato").val()=="%"){
		          $("#cContrato").val("");
		    }
		    if($("#cIdRFC").val()=="%"){
		           $("#cIdRFC").val("");
		    }
			
		}
	</script>
  </head>
  
  <body>
  <form id="formNewCont">
  		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Registro de Contrato con Remanente en Ejercicio Anterior</legend>
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
							<label for="cContrato">Contrato: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CV-A04-1" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1" aria-describedby="basic-addon1"  
							name="cContrato" id="cContrato"  />
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
							<input type="text" class="form-control" placeholder="Captura el objecto del contrato" aria-label="Captura el objecto del contrato" aria-describedby="basic-addon1"  
							name="cDescripcion" id="cDescripcion"  />
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
							<input type="text" class="form-control" placeholder="Captura el RFC del proveedor" aria-label="CCaptura el RFC del proveedor" aria-describedby="basic-addon1"  
							name="cIdRFC" id="cIdRFC"  />
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
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblContratosAprobados" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th >ID CONTRATO</th>
				        			<th >PROVEEDOR</th>
				        			<th >CONCEPTO</th>			
				        			<th >MONTO CONTRATO</th>
				        			<th >MONTO PAGADO</th>
				        			<th >MONTO REMANENTE</th>
				        			
				        			<th style="display: none;">Fundamento Leg</th>
				        			<th style="display: none;">CategoriaProced</th>
				        			<th style="display: none;">Proced CNET</th>
				        			<th style="display: none;">fechaFallo</th>
				        			<th style="display: none;">Contrato CNET</th>
				        			<th style="display: none;">Observaciones</th>
				        			<th style="display: none;">OficioDG</th>
				        			<th style="display: none;">FolioMASCP</th>
				        			<th style="display: none;">CodigoContratoCNET</th>
				        			<th style="display: none;">CodigoExpCNET</th>
				        			<th style="display: none;">TieneAnticipo</th>
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			
		</fieldset>
		<input type="hidden" name="cEjercicio" id="cEjercicio"/>
		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab1.getLogin()%>"/>
	    <input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
	    <input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
  		<input type="hidden" name="mTotalContratoAnterior" id="mTotalContratoAnterior" value="" />
  		<input type="hidden" name="mTotalPagado" id="mTotalPagado" value="" />
  		<input type="hidden" name="mTotalRemanente" id="mTotalRemanente" value="" />
  		<input type="hidden" name="cConcepto" id="cConcepto" value="" />
  		<input type="hidden" name="RFC" id="RFC" value="" />
  		<input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" />
  		<input type="hidden" name="cIdContrato" id="cIdContrato" />
  		
  		<input type="hidden" name="nIdFundamentoLeg" id="nIdFundamentoLeg" />
  		<input type="hidden" name="nIdCategoria" id="nIdCategoria" />
  		<input type="hidden" name="numProcedCNET" id="numProcedCNET" />
  		<input type="hidden" name="fFallo" id="fFallo" />
  		<input type="hidden" name="cNoContratoCNET" id="cNoContratoCNET" />
  		<input type="hidden" name="cObservaciones" id="cObservaciones" />
  		<input type="hidden" name="cOficioDG" id="cOficioDG" />
  		<input type="hidden" name="cFolioMASCP" id="cFolioMASCP" />
  		<input type="hidden" name="nCodContratoCNET" id="nCodContratoCNET" />
  		<input type="hidden" name="nCodExpedienteCNET" id="nCodExpedienteCNET" />
  		<input type="hidden" name="ITieneAnticipo" id="ITieneAnticipo" />
  	
   </form>
  </body>
</html>