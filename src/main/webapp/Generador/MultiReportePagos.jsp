<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	String cUR = "";
	String cRamo = "";
	String cCentroContable="";

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
		cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();

	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>MultiReposte Pagos</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
			
			$("input.AyudaSyC").subIniciaDlg(); 
    		$("input.autoCompletaSyC").subIniciaAutoCompleta();

    		querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
			querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async: false });
			
    		if ( "<%=cCentroContable%>" != "10" ){
    			$("#Campos").val( "<%=cUR%>" );
    			$("#cIdUnidadAdministrativa").val( "<%=cUR%>" );
    			$("#cIdUnidadAdministrativa").attr('disabled', true);
    		}
    					
			$("#tblMultilistador tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});

			oTable = $("#tblMultilistador").dataTable({
				bAutoWidth : true,
				sScrollX: "100%",
				sScrollY: "500",
				bPaginate: false,
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
				bRetrive: true,
				bDestroy: true,
				bServerSide: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true
        	});
			
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbFiltrar')
				.button()
				.click( function() {
					
					var szTemp = "";
					if ($("#cIdUnidadAdministrativa").val() != 'RHQ') szTemp += " cUnidadResponsable = '" + $("#cIdUnidadAdministrativa").val() + "'";
					
					$("#cIdContratoCompromiso").val($.trim($("#cIdContratoCompromiso").val()));										
					if ($("#cIdContratoCompromiso").val() != '') 
					{
						if (szTemp != '') szTemp = szTemp + " AND ";
						szTemp += " cIdDocumento = '" + $("#cIdContratoCompromiso").val() + "'";
					}
					
					if ($("#cIDRFC").val() != '') 
					{
						if (szTemp != '') szTemp = szTemp + " AND ";
						szTemp += " cIdRFC = '" + $("#cIDRFC").val() + "'";
					}
					
					$("#cCxP").val($.trim($("#cCxP").val()));										
					if ($("#cCxP").val() != '') 
					{
						if (szTemp != '') szTemp = szTemp + " AND ";
						szTemp += " caNoContrarrecibo = '" + $("#cCxP").val() + "'";
					}
					
					$("#cCxPH").val($.trim($("#cCxPH").val()));										
					if ($("#cCxPH").val() != '') 
					{
						if (szTemp != '') szTemp = szTemp + " AND ";
						szTemp += " caNoContrarrecibo = '" + $("#cCxPH").val() + "'";
					}
					
					$("#DESTINO_GASTO").val($.trim($("#DESTINO_GASTO").val()));										
					if ($("#DESTINO_GASTO").val() != '') 
					{
						if (szTemp != '') szTemp = szTemp + " AND ";
						szTemp += " ID_DESTINO_GASTO = '" + $("#DESTINO_GASTO").val() + "'";
					}
						
					
					$("#cEstatus").val($.trim($("#cEstatus").val()));										
					if ($("#cEstatus").val() != '') 
					{
						if (szTemp != '') szTemp = szTemp + " AND ";
						szTemp += " Estatus = '" + $("#cEstatus").val() + "'";
					}
					
					
					$("#szTemp").val(szTemp);
					
 					if (szTemp != '') szTemp = "&qw=" + szTemp;
    		
    				$("#tblMultilistador").dataTable({
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "500",
						bPaginate: false,
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
						bRetrive: true,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vMultilistadorPagos" + szTemp ,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "docto" },
							{ sName: "ID_DESTINO_GASTO" },
							{ sName: "nFolioPago"   },
							{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"   },
							{ sName: "cIdDocumento" },
							{ sName: "caNoContrarrecibo"	},
							{ sName: "nFolioSICOP"	},
							{ sName: "nFolioSIAFF"	},
							{ sName: "SolicitudPago"	},
							{ sName: "NumeroProceso"	},
							{ sName: "FechaPagado"	}, 
							{ sName: "cIdRFC"   },
							{ sName: "cnombre" },
							{ sName: "mImporteTotal" ,		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
							{ sName: "mImporteRetencion" ,	bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
							{ sName: "mImporteNeto" 	,	bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
							{ sName: "fAplicacion" },
							{ sName: "Estatus" }
						]
					});
				} );
			
			$("#pbLimpiar")
				.button()
				.click(function() {

					$("#cIdUnidadAdministrativa").val('RHQ');
					$("#cIdContratoCompromiso").val("");
					$("#cIDRFC").val("");
					$("#cCxP").val("");
					$("#cCxPH").val("");
					$("#cEstatus").val("");
					$("#DESTINO_GASTO").val(" ");
					$("#cnombre").val("");	

					$("#tblMultilistador").dataTable({
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "500",
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
						bRetrive: true,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vMultilistadorPagos&qw=cIdRFC = 'abc'"  ,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "docto" },
							{ sName: "ID_DESTINO_GASTO" },
							{ sName: "nFolioPago"   },
							{ sName: "cUnidadResponsable",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"   },
							{ sName: "cIdDocumento" },
							{ sName: "caNoContrarrecibo"	},
							{ sName: "nFolioSICOP", sClass: "alignCenter"	},
							{ sName: "nFolioSIAFF", sClass: "alignCenter"	},
							{ sName: "SolicitudPago", sClass: "alignCenter"	},
							{ sName: "NumeroProceso", sClass: "alignCenter"	},
							{ sName: "FechaPagado", sClass: "alignCenter"	}, 
							{ sName: "cIdRFC"   },
							{ sName: "cnombre" },
							{ sName: "mImporteTotal" , sClass: "alignRight"},
							{ sName: "mImporteRetencion" , sClass: "alignRight"},
							{ sName: "mImporteNeto" , sClass: "alignRight"},
							{ sName: "fAplicacion" },
							{ sName: "Estatus" }
						]
					});
					
				});			
			
			$("#pbExcel")
				.button()
				.click(function() {

					//document.ExportarForm.submit();
					

var numColumns = oTable.fnGetData(0).length;;  
var temp="";
var temp2="";

for(var r=0;r<oTable.fnGetData().length;r++)
    {
        for(var c=0;c < numColumns;c++)
        {
           temp2=oTable.fnGetData(r,c);
          temp+=temp2==null?"|":temp2+"|";   
        }
        
    }

document.getElementById("tabla0").value = temp;

temp="";
var nRow =  $('#tblMultilistador thead tr')[0];



document.getElementById("colsxTabla").value=oTable.fnGetData(0).length;
document.getElementById("encabezadosTabla0").value ="Tipo Documento|Tipo Destino|Folio|UE|Contrato|Cuenta por Pagar|SICOP|SIAFF|Solicitud Pago|Proceso|Fecha Pago|RFC|Beneficiario|Importe Total|Retenciones|Importe Neto|Fecha Aplicacion|Estatus";
document.getElementById("titulosxTabla").value = "Pagos";
document.getElementById("formatosTabla0").value = "String|String|String|String|String|String|String|String|String|String|String|String|String|String|String|String|String|String|";
document.getElementById("tTablas").value = "1";

document.formulario.submit();
					
});	
	
			$("#pbPdf")
				.button()
				.click(function() {
					if ( $("#szTemp").val() != "" ){
						window.open(
							"../admin/SeguridadCatalogos?"
								+ "catalogo=ANEXO"
								+ "&accion=run"
								+ "&rn=RepMultilistadorPagos.jasper"
								+ "&cWhereList=" + $("#szTemp").val(),  
							"Anexo",
							"scrollbars=1, resizable=yes, width=1024, height=768");
					}

				});	
		});
		
		function formSubmited() {
                alert("Beneficiario enviado!");
            }
		
		
		function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}

			function foco(elemento) {
			 elemento.style.border = "1px solid #FF0000";
			 }
			
			 function no_foco(elemento) {
			 elemento.style.border = "1px solid #CCCCCC";
			 }
		
		</script>
		

</head>

<body id="dt_example" >
	<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/generaLayoutMRPagosServlet" method="post"> 
		<div id="container" style="width:1000px; padding-left:100px" class="SyCData">	
			<input type="hidden" name="szTemp" id="szTemp">
			<input type="hidden" name="cDocumento" id="cDocumento" value="TODOS">
			<h1>Multilistador Pagos</h1>			
			<table id="clvcont" border="0">
				<tr>
					<td align="right">Contrato: </td>
					<td>
						<input type="text" class="AyudaSyC desahabilitado" maxlength="40" size="50" name="cIdContrato" id="cIdContratoCompromiso" readonly >
					</td>
				</tr>
				<tr >
					<td align="right">RFC:</td>
					<td valign="top" colspan="3">
						<input type="text" maxlength="15" size="15" name="cIDRFC" id="cIDRFC" readonly class="AyudaSyC desahabilitado" onChange="no_foco(this);"/>
						<input readonly type="text" maxlength="100" size="90" name="cnombre" ID="cnombre" onBlur="no_foco(this);" class="desahabilitado"/>
					</td>
				</tr>
				<tr align="right">
					<td valign="top" >Unidad Ejecutora:</td>
					<td valign="top"  colspan="3" align="left"> 
						<select name="cIdUnidadAdministrativa" id="cIdUnidadAdministrativa" style="width: 35em;" class="desahabilitado"><option value="RHQ"></option></select>
					</td>					
				</tr>
			</table>
			<table>
				<tr align="left">
					<td valign="top" >Cuenta por Pagar desde:</td>
					<td valign="top" >
						<input type="text" maxlength="15" size="15" name="cCxP" id="cCxP"  />
					</td>
					<td valign="top" >Hasta:</td> 
					<td valign="top" >
						<input type="text" maxlength="15" size="15" name="cCxPH" id="cCxPH"  />
					</td>
				</tr>
				<tr align="left">
					<td valign="top" >Tipo Destino:</td>
					<td valign="top" >
						<select id="DESTINO_GASTO" name="DESTINO_GASTO"></select>
					</td>
					<td valign="top" >Estatus:</td> 
					<td valign="top" >
						<select id="cEstatus" name="cEstatus">
							<option value="">Seleccionar</option>
							<option value="AUTORIZADO">Autorizado</option>
							<option value="CANCELADO">Cancelado</option>
							<option value="EJERCIDO">Ejercido</option>
							<option value="PAGADO">Pagado</option>
						</select>
					</td>
				</tr>
			</table>
			<table align="center">
				<tr>
					<td>
						<input type="button" id="pbFiltrar" value="Filtrar"/>
					</td>
					<td>
					<input type="button" id="pbLimpiar" value="Limpiar" />
					</td>
					<td>
					<input type="button" id="pbExcel" value="Excel">
					</td>
					<td>
					<input type="button" id="pbPdf" value="PDF">
					</td>
				</tr>
			</table>
			<table id="tblMultilistador" class="display"  >
	            <thead>
	                <tr>
	                	<th>Tipo Documento</th>
	                	<th>Tipo Destino</th>
	                	<th>Folio</th>
	                	<th>UE</th>
	                	<th width="200px" >Contrato</th>
	                    <th>Cuenta por Pagar</th>
						<th># SICOP</th>
						<th>#SIAFF</th>
						<th>#Solicitud Pago</th>
						<th># Proceso</th>
						<th>Fecha Pago</th> 
	                    <th>RFC</th>
	                	<th width="130px" align="center">Beneficiario</th>
	                    <th>Importe Total</th>
	                    <th>Retenciones</th>
	                    <th>Importe Neto</th>
	                    <th>Fecha Aplicación</th>
	                    <th>Estatus</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>
	</form>
	
	<form action="../reports/GeneraExcelTable" method="post" id="formulario" name="formulario">
    <input type="hidden" name="tabla0" id="tabla0" />
	<input type="hidden" name="colsxTabla" id="colsxTabla" />
	<input type="hidden" name="titulosxTabla" id="titulosxTabla" />
	<input type="hidden" name="encabezadosTabla0" id="encabezadosTabla0" />
	<input type="hidden" name="formatosTabla0" id="formatosTabla0" />
	<input type="hidden" name="tTablas" id="tTablas" />
	</form>
	
	
	</body>
</html>