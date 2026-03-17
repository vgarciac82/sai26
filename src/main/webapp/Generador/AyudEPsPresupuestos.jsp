<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad(
				"CCENTROCONTABLE").getValor());
	}
	
	String cEsRadicado = null;
	if( request.getParameter("cEsRadicado") != null ){
		cEsRadicado = request.getParameter("cEsRadicado");
	}
	
	String cFuenteFinanciamiento = null;
	if( request.getParameter("FuenteFinanciamiento") != null )
		cFuenteFinanciamiento = request.getParameter("FuenteFinanciamiento");
	
	String tipoRectificacion = null;
	if( request.getParameter("tipoRectificacion") != null )
		tipoRectificacion = request.getParameter("tipoRectificacion");
	
	boolean fireChangeEvent = "true".equalsIgnoreCase( request.getParameter("fireChange") );
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Catálogo de Estructura Programática</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<style type="text/css" title="currentStyle">
@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";

@import "../Generador/css/demo_table_jui.css";

@import "../Generador/css/demo_page.css";
</style>

<script type="text/javascript" src="../jq/js/jquery.js"></script>
<script type="text/javascript" src="../jq/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>

<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>

<script type="text/javascript" charset="UTF-8">
		var oTable, oCurrentFocus;
		var asInitVals = new Array();
		var arrRet = null;
  	    var aSelected = [0];
  	    var esRadicado = "";
		var fireChangeEvent = <%=fireChangeEvent%>;
		var esIP = "";
		var currIndex;
		$(document).ready(function() {

			if (<%=cEsRadicado%> == "1"){
				esRadicado = "S";
			}else{
				esRadicado = "N";
			}
			
			if (<%=cFuenteFinanciamiento%> == "4"){
				esIP = "S";
			}else{
				esIP = "N";
			}
			
			for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) { oCurrentFocus = this; fnFilterColumn(evt);} );

			
			oTableSaldos = $("#tblSaldos").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,				
				sScrollX: "60%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : false,
				"bDestroy": true,
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
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aoColumns: [
					{ sName: "MontoEnero",		bSortable: false },
					{ sName: "MontoFebrero",	bSortable: false },
					{ sName: "MontoMarzo",		bSortable: false },
					{ sName: "MontoAbril",		bSortable: false },
					{ sName: "MontoMayo",		bSortable: false },
					{ sName: "MontoJunio",		bSortable: false },
					{ sName: "MontoJulio",		bSortable: false },
					{ sName: "MontoAgosto",		bSortable: false },
					{ sName: "MontoSeptiembre",		bSortable: false },
					{ sName: "MontoOctubre",		bSortable: false },
					{ sName: "MontoNoviembre",		bSortable: false },
					{ sName: "MontoDiciembre",		bSortable: false },
					{ sName: "MontoAnual",	bSortable: false }
				]
        	});

			oTable = createDataTable("");

			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
    		
    		// Click event handler
    		$("#tblEP tbody tr").live('click', function () {
	    		
	    		$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
				
				$("#pbAceptar").css("visibility","visible");
				$("#pbCancelar").css("visibility","visible");
    			$('#tblSaldos').dataTable().fnClearTable(); 
			
				var aPos = oTable.fnGetPosition(event.target.parentNode);

	            if( aPos instanceof Array )
	                currIndex = aPos[ 0 ];
	            else
	                currIndex = aPos;
 
	            var arrDatos = currIndex;
	            
				var anSelected = oTable.fnGetData(aPos);
				var sEP = 	$.trim( anSelected[0] )+ "."+
							$.trim( anSelected[1] ) + "."+
							$.trim( anSelected[2] ) + "."+
							$.trim( anSelected[3] ) + "."+
							$.trim( anSelected[4] ) + "."+
							$.trim( anSelected[5] ) + "."+
							$.trim( anSelected[6] ) + "."+
							$.trim( anSelected[7] ) + "."+
							$.trim( anSelected[8] )  + "."+
							$.trim( anSelected[9] ) + "."+
							$.trim( anSelected[10] ) + "."+
							$.trim( anSelected[11] ) + "."+
							$.trim( anSelected[12] ) + "."+
							$.trim( anSelected[13] ) + "."+
							$.trim( anSelected[14] ) + "." +
							$.trim( anSelected[15] );

 				$("#ep").val(sEP);
 				var szWhere = " EP = '" + sEP + "' ";
 				var szTabla;
 				if("<%=tipoRectificacion%>" == "DIRECTA"){
 					szTabla = "VSALDOSANUALES";
 				} else if("<%=tipoRectificacion%>" == "COMPROMISO"){
 					szTabla = "VSALDOSANUALESCOM";
 				}
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 10, ajax: 'false'}, 
					function(j)
					{
													
    					for (var i = 0; i < j.length; i++) 
    					{  
    						$('#tblSaldos').dataTable().fnAddData(
    						[ formatCurrency(j[i].Col4), formatCurrency(j[i].Col5), formatCurrency(j[i].Col6), formatCurrency(j[i].Col7), 
    						  formatCurrency(j[i].Col8), formatCurrency(j[i].Col9), formatCurrency(j[i].Col10), formatCurrency(j[i].Col11), 
    						  formatCurrency(j[i].Col12),formatCurrency(j[i].Col13), formatCurrency(j[i].Col14), formatCurrency(j[i].Col15), 
    						  formatCurrency(j[i].Col16) 
    						]);
						}
		         });
		     } );
		   
			$("#pbAceptar")
			.button()
			.click(function() {			
 				window.opener.editaEP.EPDebeDecir.value=window.MultiReporteHija.ep.value ;
 				if( fireChangeEvent   )
 					opener.$("#" + window.opener.editaEP.EPDebeDecir.id).trigger('change');
				
 				window.close();
   			});	
    		
    		$("#pbCancelar")
			.button()
			.click(function() {
				window.close();
			});

		});

		function formSubmited() {
                alert("EP enviada");
            }


		

			function fnFilterColumn (evt)
			{
				var value, myWhere, token;
				var column_name = [
					"EjercicioFiscal",
					"Ramo",
					"UnidadResponsable",
					"GrupoFuncional",
					"Funcion",
					"SubFuncion",
					"ProgramaGeneral",
					"ActividadInstitucional",
					"ProgramaPresupuestario",
					"Partida",
					"TipoGasto",
					"FuenteFinanciamiento",
					"EntidadFederativa",
					"Cartera",
					"UnidadEjecutora",
					"UnidadNorativa"
				];
				myWhere = "";
				token = "&qw=";

				var charCode = evt.which ? evt.which : window.event.keyCode;
				if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
					if (charCode <= 13) return true;

					if (charCode < 96 || charCode > 106) { // Es teclado numerico?
						var keyChar = String.fromCharCode(charCode);
						var re = /[a-zA-Z0-9.]/;
						if (!re.test(keyChar)) return true;
					}
				}

				for (var i = 1; i < 17; i++) {
					value = $("#col"+i+"_filter" ).val();
					if (value !== "") {
						myWhere += token + column_name[i - 1] + " LIKE '%25" + value + "%25'";
						token = " AND ";
					}
				}
				oTable = createDataTable(myWhere);
				return true;
			}

		function windowStatus( texto )
		{
			window.status=texto;
		}

		function createDataTable(w) {

				$('#tblSaldos').dataTable().fnClearTable();
				
				var urlAjaxSource = "";
				var fFinanciamiento = " FuenteFinanciamiento <> 4 ";
				
				if(esRadicado == "S"){
					urlAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPGrid &qw=" + encodeURIComponent(fFinanciamiento) + w;
				}else{
				 	if(esIP == "S"){
				 		fFinanciamiento = " FuenteFinanciamiento = 4 ";
				 	}
					urlAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPGrid &qw=" + encodeURIComponent(fFinanciamiento) + w;
				}
				
				return $("#tblEP").dataTable({
						"bDestroy": true,
						fnDrawCallback: function() {
							$(oCurrentFocus).focus(function() {
								if (this.createTextRange) {
									var r = this.createTextRange();
									r.collapse(false);
									r.select();
								}
								this.focus();
							});
							$(oCurrentFocus).focus();
						},
						bAutoWidth : false,
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
		       			sAjaxSource: urlAjaxSource,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						//aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "EjercicioFiscal"   },
							{ sName: "Ramo" },
							{ sName: "UnidadResponsable"	},
							{ sName: "GrupoFuncional" },
							{ sName: "Funcion"   },
							{ sName: "SubFuncion" },
							{ sName: "ProgramaGeneral"	},
							{ sName: "ActividadInstitucional" },
							{ sName: "ProgramaPresupuestario"   },
							{ sName: "Partida" },
							{ sName: "TipoGasto"	},
							{ sName: "FuenteFinanciamiento" },
							{ sName: "EntidadFederativa"   },
							{ sName: "Cartera" },
							{ sName: "UnidadEjecutora"	},
							{ sName: "UnidadNorativa" }
						]
		        	});
		}

</script>


</head>

<body id="dt_example">
	<form id="MultiReporteHija" name="MultiReporteHija">
		<div id="container" class="container">
			<h1>Estructura Programática</h1>
			<input id="ep" name="ep" type="hidden" style="display: none;"
				value="" size="4" maxlength="64" class="" />			 
			<label>Saldos Disponibles</label>
			<table id="tblSaldos" class="display"  >
	            <thead>
	                <tr>
	                	<th>Enero</th>
	                    <th>Febrero</th>
	                    <th>Marzo</th>
	                    <th>Abril</th>
	                    <th>Mayo</th>
	                    <th>Junio</th>
	                    <th>Julio</th>
	                    <th>Agosto</th>
	                    <th>Sep</th>
	                    <th>Oct</th>
	                    <th>Nov</th>
	                    <th>Dic</th>
	                    <th>Anual</th>
	                </tr>
	            </thead>
	        </table>
	        <br>
			<table id="tblEP" class="display" cellpadding="0" cellspacing="0"
				border="0" class="display">
				<thead>
					<tr>
						<th>EF</th>
						<th>Ra</th>
						<th>UR</th>
						<th>GF</th>
						<th>Fu</th>
						<th>SF</th>
						<th>PG</th>
						<th>AI</th>
						<th>PP</th>
						<th>Pa</th>
						<th>TG</th>
						<th>FF</th>
						<th>EF</th>
						<th>Ca</th>
						<th>UE</th>
						<th>UN</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<th><input type="text" name="col1_filter" id="col1_filter"
							onMouseOver="windowStatus('Ejercicio Fiscal');" />
						</th>
						<th><input type="text" name="col2_filter" id="col2_filter"
							onMouseOver="windowStatus('Ramo');" />
						</th>
						<th><input type="text" name="col3_filter" id="col3_filter"
							onMouseOver="windowStatus('Unidad Responsable');" />
						</th>
						<th><input type="text" name="col4_filter" id="col4_filter"
							onMouseOver="windowStatus('Grupo Funcional');" />
						</th>
						<th><input type="text" name="col5_filter" id="col5_filter"
							onMouseOver="windowStatus('Funcion');" />
						</th>
						<th><input type="text" name="col6_filter" id="col6_filter"
							onMouseOver="windowStatus('SubFuncion');" />
						</th>
						<th><input type="text" name="col7_filter" id="col7_filter"
							onMouseOver="windowStatus('Programa General');" />
						</th>
						<th><input type="text" name="col8_filter" id="col8_filter"
							onMouseOver="windowStatus('Actividad Institucional');" />
						</th>
						<th><input type="text" name="col9_filter" id="col9_filter"
							onMouseOver="windowStatus('Programa Presupuestario');" />
						</th>
						<th><input type="text" name="col10_filter" id="col10_filter"
							onMouseOver="windowStatus('Partida');" />
						</th>
						<th><input type="text" name="col11_filter" id="col11_filter"
							onMouseOver="windowStatus('Tipo de Gasto');" />
						</th>
						<th><input type="text" name="col12_filter" id="col12_filter"
							onMouseOver="windowStatus('Fuente Financiamiento');" />
						</th>
						<th><input type="text" name="col13_filter" id="col13_filter"
							onMouseOver="windowStatus('Entidad Federativa');" />
						</th>
						<th><input type="text" name="col14_filter" id="col14_filter"
							onMouseOver="windowStatus('Cartera');" />
						</th>
						<th><input type="text" name="col15_filter" id="col15_filter"
							onMouseOver="windowStatus('Unidad Ejecutora');" />
						</th>
						<th><input type="text" name="col16_filter" id="col16_filter"
							onMouseOver="windowStatus('Unidad Normativa')" />
						</th>
					</tr>
				</tfoot>
			</table>
			<br/>
			<br />
			<center>
				<input type="button" id="pbAceptar" style="visibility: hidden" value ="Aceptar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" style="visibility: hidden" value ="Cancelar"/>
			</center>
		</div>
	</form>
</body>
</html>