<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%
	/**
	* versión MODIFICADA del MultiReporteGrid.jsp
	* modificada por Martha Aurora Sánchez Valdivieso
	* para SYC Constructores de Sistemas SA de CV
	* desarrollo gestion_conagua_sif
	* México D.F. 26/07/2012 - 14/08/2012
	*/
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;

	mntoCuentas = true;

	/*
	
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	*/
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
<!-- 		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script> -->
<!-- 		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script> -->
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
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

		$(document).ready(function() {
			for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) { oCurrentFocus = this; fnFilterColumn(evt);} );

			$("#tblEP tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('gradeA');
				});
				$(event.target.parentNode).addClass('gradeA');
				$("#pbDesplegar").css("visibility","visible");
			});

			oTable = createDataTable("");

			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

			// Click event handler
			$("#tblEP tbody tr").live('click', function () {
				var id = this.id;
				var index = jQuery.inArray(id, aSelected);
				if ( $(this).hasClass('row_selected') ) {
					var anSelected = fnGetSelected( oTable );
//					var sEP = anSelected[0].innerText.substr(0,63);
					var sEP = 	anSelected[0].innerText.substr(0,4)+"."+
								anSelected[0].innerText.substr(4,2)+"."+
								anSelected[0].innerText.substr(6,3)+"."+
								anSelected[0].innerText.substr(9,1)+"."+
								anSelected[0].innerText.substr(10,1)+"."+
								anSelected[0].innerText.substr(11,2)+"."+
								anSelected[0].innerText.substr(13,2)+"."+
								anSelected[0].innerText.substr(15,3)+"."+
								anSelected[0].innerText.substr(18,4)+"."+
								anSelected[0].innerText.substr(22,5)+"."+
								anSelected[0].innerText.substr(27,1)+"."+
								anSelected[0].innerText.substr(28,1)+"."+
								anSelected[0].innerText.substr(29,2)+"."+
								anSelected[0].innerText.substr(31,11)+"."+
								anSelected[0].innerText.substr(42,3)+"."+
								anSelected[0].innerText.substr(45,3);

					$("#ep").val(sEP);

					window.opener.frmReporteAdecuacionesUN.ep.value = window.Reporte.ep.value;
					window.close();
				}
				$(this).toggleClass('row_selected');
			} );
		});

		function formSubmited() {
			alert("EP enviada");
		}

		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();

			for ( var i=0 ; i<aTrs.length ; i++ ){
				if ( $(aTrs[i]).hasClass('gradeA') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
//			return oTableLocal.$('tr.row_selected');
		}

		function fnFilterColumn (evt){
			var value, myWhere, token;
			var column_name = [ "EjercicioFiscal",
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

		function windowStatus( texto ){
			window.status = texto;
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
					oPaginate: { sFirst:"Primero", sPrevious: "Ant.", sNext: "Sigte.", sLast: "&Uacute;ltimo" }
				},
				bServerSide: true,
       			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPAnteproyectoGrid" + w,
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
  <body id="dt_example" >
	<form id="Reporte" name="Reporte">
		<div id="container" class="container">
			<h1>Estructura Programática</h1>
			<input id="ep" name="ep" type="hidden" style="display: none;"  value="" size="4" maxlength="64" class=""/>
			<br/>
			<br/>
			<table id="tblEP" class="display"    class="display" >
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
						<th><input type="text" name= "col1_filter" id= "col1_filter" onMouseOver="windowStatus('Ejercicio Fiscal');" /></th>
						<th><input type="text" name= "col2_filter" id= "col2_filter" onMouseOver="windowStatus('Ramo');" /></th>
						<th><input type="text" name= "col3_filter" id= "col3_filter" onMouseOver="windowStatus('Unidad Responsable');" /></th>
						<th><input type="text" name= "col4_filter" id= "col4_filter" onMouseOver="windowStatus('Grupo Funcional');" /></th>
						<th><input type="text" name= "col5_filter" id= "col5_filter" onMouseOver="windowStatus('Funcion');" /></th>
						<th><input type="text" name= "col6_filter" id= "col6_filter" onMouseOver="windowStatus('SubFuncion');" /></th>
						<th><input type="text" name= "col7_filter" id= "col7_filter" onMouseOver="windowStatus('Programa General');" /></th>
						<th><input type="text" name= "col8_filter" id= "col8_filter" onMouseOver="windowStatus('Actividad Institucional');" /></th>
						<th><input type="text" name= "col9_filter" id= "col9_filter" onMouseOver="windowStatus('Programa Presupuestario');" /></th>
						<th><input type="text" name="col10_filter" id="col10_filter" onMouseOver="windowStatus('Partida');" /></th>
						<th><input type="text" name="col11_filter" id="col11_filter" onMouseOver="windowStatus('Tipo de Gasto');" /></th>
						<th><input type="text" name="col12_filter" id="col12_filter" onMouseOver="windowStatus('Fuente Financiamiento');" /></th>
						<th><input type="text" name="col13_filter" id="col13_filter" onMouseOver="windowStatus('Entidad Federativa');" /></th>
						<th><input type="text" name="col14_filter" id="col14_filter" onMouseOver="windowStatus('Cartera');" /></th>
						<th><input type="text" name="col15_filter" id="col15_filter" onMouseOver="windowStatus('Unidad Ejecutora');"/></th>
						<th><input type="text" name="col16_filter" id="col16_filter" onMouseOver="windowStatus('Unidad Norativa');"/></th>
					</tr>
				</tfoot>
			</table>
			<br/>
		</div>
	</form>
  </body>
</html>