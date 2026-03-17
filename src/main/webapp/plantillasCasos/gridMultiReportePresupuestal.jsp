<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String UE=usuario.getU_UR();//UR comparar con unidad ejecutora
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	String id="";
	if(request.getParameter("id")!=null)
	    id=request.getParameter("id");
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
			
			  #feedback { font-size: 1.4em; }
			  #selectable .ui-selecting { background: #FECA40; }
			  #selectable .ui-selected { background: #F39814; color: white; }
			  #selectable { list-style-type: none; margin: 0; padding: 0; width: 80%; }
			  #selectable li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }
		</style>

		<script type="text/javascript" src="../jq/js/jquery.js"></script>
		<script type="text/javascript" src="../jq/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

<!-- 		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script> -->
<!-- 		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script> -->
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="UTF-8">
		var oTable, oCurrentFocus;
		var asInitVals = new Array();
		var arrRet = null;
  	    var aSelected = [0];
		var UE = "<%= usuario.getU_UR()%>";
		var Bandera="";
		var epCheck = new Array();
		
		$(document).ready(function() {
   
			for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) { 
				oCurrentFocus = this; fnFilterColumn(evt);
				$("#pbAceptar").css("visibility","hidden");
			} );

			oTable = createDataTable("");

				$("#tblEP tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
				$("#pbAceptar").css("visibility","visible");			
				
				var anSelected = fnGetSelected( oTable );
				if(anSelected.length>0)
				{
					var sEP = 	anSelected[0].innerText.substr(0,4)+"."+
								anSelected[0].innerText.substr(4,2)+"."+
								anSelected[0].innerText.substr(7,3)+"."+
								anSelected[0].innerText.substr(11,1)+"."+
								anSelected[0].innerText.substr(12,1)+"."+
								anSelected[0].innerText.substr(13,2)+"."+
								anSelected[0].innerText.substr(15,2)+"."+
								anSelected[0].innerText.substr(17,3)+"."+
								anSelected[0].innerText.substr(20,4)+"."+
								anSelected[0].innerText.substr(24,5)+"."+
								anSelected[0].innerText.substr(29,1)+"."+
								anSelected[0].innerText.substr(30,1)+"."+
								anSelected[0].innerText.substr(31,2)+"."+
								anSelected[0].innerText.substr(33,11)+"."+
								anSelected[0].innerText.substr(44,3)+"."+
								anSelected[0].innerText.substr(48,3);

					var szWhere = " EP = '" + sEP + "' ";
				}		
			});

			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

			$("#pbAceptar").button().click(function() 
			{
				 for(var indice in asInitVals) 
 				 {
 		          	window.opener.catalogoEp.dataTable().fnAddData([asInitVals[indice]]);
 		         }
  				window.close();
			});		

			$("#pbCancelar").button().click(function() 
			{
				window.close();
			});	

		});

		function formSubmited() {
                alert("EP enviada");
            }

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
					"UnidadNorativa",
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
			}

		function windowStatus( texto )
		{
			window.status=texto
		}
		

		function createDataTable(w) {
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vGridMultiReportePresupuestal" + w,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				"sScrollX" : 900,
				"oTableTools": {
								"sRowSelect": "multi",
								"aButtons": [ "select_all", "select_none" ]
								},
				aoColumns: [
					{ sName: "chk"   },
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
					{ sName: "TipoGasto" },
					{ sName: "FuenteFinanciamiento" },
					{ sName: "EntidadFederativa" },
					{ sName: "Cartera" },
					{ sName: "UnidadEjecutora"	},
					{ sName: "UnidadNorativa" }
				]
        	});
		}		
		
		function seleccionaEP(id)
		{
			if( $("#"+id).attr("checked") )
			{
				asInitVals[id]=$("#"+id).val();
			}
			else
			{
				delete asInitVals[id];
			}
		}
</script>


</head>

<body id="dt_example" >
	<form id="MultiReporteHija" name="MultiReporteHija">
		<div id="container" class="container">
			<h1>Estructura Programática</h1>
				<input id="ep" name="ep" type="hidden" style="display: none;"  value="" size="4" maxlength="64" class=""/>
				<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" value="6"/>
				<input type="hidden" value="DIRECTO" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO">
				<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value=""/>
			<table id="tblEP" class="display"   class="display" >
	            <thead>
	                <tr>
	                	  <th>&nbsp;</th>
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
							<th>&nbsp;</th>
							<th><input type="text" name="col1_filter" id="col1_filter"  onMouseOver="windowStatus('Ejercicio Fiscal');" /></th>
							<th><input type="text" name="col2_filter" id="col2_filter"  onMouseOver="windowStatus('Ramo');" /></th>
							<th><input type="text" name="col3_filter" id="col3_filter"  onMouseOver="windowStatus('Unidad Responsable');" /></th>
							<th><input type="text" name="col4_filter" id="col4_filter"  onMouseOver="windowStatus('Grupo Funcional');" /></th>
							<th><input type="text" name="col5_filter" id="col5_filter"  onMouseOver="windowStatus('Funcion');" /></th>
							<th><input type="text" name="col6_filter" id="col6_filter"  onMouseOver="windowStatus('SubFuncion');" /></th>
							<th><input type="text" name="col7_filter" id="col7_filter"  onMouseOver="windowStatus('Programa General');" /></th>
							<th><input type="text" name="col8_filter" id="col8_filter"   onMouseOver="windowStatus('Actividad Institucional');" /></th>
							<th><input type="text" name="col9_filter" id="col9_filter"  onMouseOver="windowStatus('Programa Presupuestario');" /></th>
							<th><input type="text" name="col10_filter" id="col10_filter"   onMouseOver="windowStatus('Partida');" /></th>
							<th><input type="text" name="col11_filter" id="col11_filter"   onMouseOver="windowStatus('Tipo de Gasto');" /></th>
							<th><input type="text" name="col12_filter" id="col12_filter"   onMouseOver="windowStatus('Fuente Financiamiento');" /></th>
							<th><input type="text" name="col13_filter" id="col13_filter"   onMouseOver="windowStatus('Entidad Federativa');" /></th>
							<th><input type="text" name="col14_filter" id="col14_filter"   onMouseOver="windowStatus('Cartera');" /></th>
							<th><input type="text" name="col15_filter" id="col15_filter"  onMouseOver="windowStatus('Unidad Ejecutora');"/></th>
							<th><input type="text" name="col16_filter" id="col16_filter"  onMouseOver="windowStatus('Unidad Norativa');"/></th>
						</tr>
					</tfoot>
	        </table>
			<br/>
			<center>
				<input type="button" id="pbAceptar" style="visibility: hidden" value ="Aceptar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" style="visibility: hidden" value ="Cancelar"/>
			</center>
		</div>
	</form>
	</body>
</html>
