<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String UE=usuario.getU_UR();//UR comparar con unidad ejecutora
	String login=usuario.getLogin();
	
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	
	String cFuenteFinanciamiento = null;
	if( request.getParameter("fuenteFinancimiento") != null )
		cFuenteFinanciamiento = request.getParameter("fuenteFinancimiento");
		
	String cTipoGasto = null;
	if( request.getParameter("tipoGasto") != null )
		cTipoGasto = request.getParameter("tipoGasto");
		
	String cEsRadicado = null;
	if( request.getParameter("cEsRadicado") != null )
		cEsRadicado = request.getParameter("cEsRadicado");
		
	String formName = StringUtils.isEmpty(request.getParameter("formName")) ? "formPagos":request.getParameter("formName");	
		
	String DestinoGasto = request.getParameter("destino")==null || "".equals(request.getParameter("destino"))? "":request.getParameter("destino");
	String tConcepto = request.getParameter("tipoConcepto")==null || "".equals(request.getParameter("tipoConcepto"))? "":request.getParameter("tipoConcepto");
	String cunidadej = request.getParameter("ue")==null || "".equals(request.getParameter("ue"))? "":request.getParameter("ue");
	
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
		var tmtecla;	
		var formName = "<%=formName%>";
		var destinoGasto = "<%=DestinoGasto%>";
		var tConcepto = "<%=tConcepto%>";
		var cunidadej = "<%=cunidadej%>";
		
		$(document).ready(function() {

		oTableSaldos = $("#tblSaldos").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,				
				sScrollX: "100%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
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
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aoColumns: [
					{ sName: "MontoEnero",		bSortable: false },
					{ sName: "MontoFebrero",		bSortable: false },
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
   
			$(".filtro" ).keyup( function(evt) { 
				clearInterval(tmtecla);
				tmtecla = setInterval(function(){fnFilterColumn(evt)}, 1000);
			} );

			$(".filtro" ).change( function(evt) { 
				fnFilterColumn(evt);
			} );

			for (var i = 1; i < 17; i++)
				$("#col"+i+"_filter" ).keyup( function(evt) { 
				oCurrentFocus = this; fnFilterColumn(evt);
				$("#pbAceptar").css("visibility","hidden");
				$('#tblSaldos').dataTable().fnClearTable(); 
			} );
			
			oTable = createDataTable("");
			
			$("#tblEP tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
				
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
						
				var szWhere = " EP = '" + sEP + "' ";
				
				if (<%=cEsRadicado%>=="1")
					szWhere = " 1=2 OR (nCuentaP='82109' AND Ep = '"+sEP+"')";
				
				var szTabla = "VSALDOSANUALES";                                                                                         
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 10, ajax: 'false'}, 
					function(j)
					{
						if (j.length>0)
							$("#pbAceptar").css("visibility","visible");
						else
							$("#pbAceptar").css("visibility","hidden");
							
    					for (var i = 0; i < j.length; i++) 
    					{  
    						$('#tblSaldos').dataTable().fnAddData(
    						[ formatCurrency(j[i].Col4), formatCurrency(j[i].Col5), formatCurrency(j[i].Col6), formatCurrency(j[i].Col7), formatCurrency(j[i].Col8), formatCurrency(j[i].Col9), formatCurrency(j[i].Col10), formatCurrency(j[i].Col11), formatCurrency(j[i].Col12),formatCurrency(j[i].Col13), formatCurrency(j[i].Col14), formatCurrency(j[i].Col15), formatCurrency(j[i].Col16) ]);
						}
		         });   		
			});

			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$("#pbAceptar")
			.button()
			.click(function() {
			
				var index = fnGetSelectedIndex(oTable);
				if( index >= 0 ){	
					var anSelected = oTable.fnGetData(index);
					
					var sEP =   $.trim( anSelected[0] )+ "."+
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
	}
	 				$("#ep").val(sEP);
	
					window.opener.<%=formName%>.EP.value=window.MultiReporteHija.ep.value ;
					window.close();
	    		
	   			//$(this).toggleClass('row_selected');

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


		function fnGetSelectedIndex( oTableLocal ) {
			var idx = -1;
			
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length && idx < 0 ; i++ ) {
				if ( $(aTrs[i]).hasClass('row_selected') ) {
					idx = i;
				}
			}
			
			return idx;
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
				clearInterval(tmtecla);
				
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
				//token = "&qw="; // LAOP - Este dato se adjunta despues del &qw
				token = " AND ";
				
				/*var charCode = evt.which ? evt.which : window.event.keyCode;
				if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
					if (charCode <= 13) return true;

					if (charCode < 96 || charCode > 106) { // Es teclado numerico?
						var keyChar = String.fromCharCode(charCode);
						var re = /[a-zA-Z0-9.]/;
						if (!re.test(keyChar)) return true;
					}
				}*/

				for (var i = 1; i < 17; i++) {
					value = $("#col"+i+"_filter" ).val();
					if (value !== "") {
						myWhere += token + column_name[i - 1] + " LIKE '%" + value + "%'";
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
		$('#ID_DESTINO_GASTO').val(destinoGasto); 
		$('#TIPO_CONCEPTO').val(tConcepto); 
		var tipodocto = $('#TO_TIPO_DOCTO').val(); 
		$('#cUnidadEjecutora').val(cunidadej);
		var nIdComision
						
		if (cunidadej == 'A02'){
			cunidadej = "";
		}
		else {
			//cunidadej = " and UnidadEjecutora='" + cunidadej + "'"; //URVP.25062014 se comentariza y se agrega siguiente linea para hacer uso de las vistas generadas Usuario/UR/Modulo
			cunidadej = " and UnidadEjecutora IN (SELECT ur FROM tVistasUR WHERE modulo='TESORERIA' AND usuario = '<%=login%>') "; 
		}

		$('#tblSaldos').dataTable().fnClearTable(); 
		
		var f = "v_EPconsultaPagos&qw=ID_DESTINO_GASTO = '" + destinoGasto + "' and cTCONC='" + tConcepto + "'" + cunidadej;
		//var epDisponibles = " AND REPLACE(EjercicioFiscal" + "%2b" + "Ramo" + "%2b" + "UnidadResponsable" + "%2b" + "GrupoFuncional" + "%2b" + "Funcion" + "%2b" + "SubFuncion" + "%2b" + "ProgramaGeneral" + "%2b" + "ActividadInstitucional" + "%2b" + "ProgramaPresupuestario" + "%2b" + "Partida" + "%2b" + "TipoGasto" + "%2b" + "FuenteFinanciamiento" + "%2b" + "EntidadFederativa" + "%2b" + "Cartera" + "%2b" + "UnidadEjecutora" + "%2b" + "UnidadNorativa,' ','') IN (SELECT REPLACE(EP,'.','') FROM dbo.vSaldosAnuales WITH(NOLOCK) WHERE MontoAnual>0) ";

		var cWhere = " ID_DESTINO_GASTO='" + destinoGasto + "' AND cTCONC='" + tConcepto + "'" + cunidadej + w <%=cFuenteFinanciamiento!=null && !"".equals(cFuenteFinanciamiento)?"+\" and fuentefinanciamiento = " + cFuenteFinanciamiento + "\"": "+\" and fuentefinanciamiento<>4 \"" %> + w<%= cTipoGasto!=null && !"".equals(cTipoGasto)?"+\" and TipoGasto = " + cTipoGasto + " and fuentefinanciamiento = " + cTipoGasto + "\"": " + \" AND FuenteFinanciamiento NOT IN (2,3) \"" %> + cunidadej;		
		
		if(destinoGasto = "NODR"){			
			cWhere = cWhere + " AND (SUBSTRING(Partida,1,1) = '1')";							
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
				"fnServerData": function ( sSource, aoData, fnCallback ) {
					$.ajax( {
								"dataType": 'json', 
								"type": "POST", 
								"url": sSource, 
								"data": aoData, 
								"success": fnCallback
					} );
				},
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_EPconsultaPagos&qw=" + encodeURIComponent(cWhere),  //+ epDisponibles, 
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
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
	<form id="MultiReporteHija" name="MultiReporteHija">
		<div id="container" class="container">
			<h1>Estructura Programática</h1>
				<input id="ep" name="ep" type="hidden" style="display: none;"  value="" size="4" maxlength="64" class=""/>
				<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" value="6"/>
				<input type="hidden" value="DIRECTO" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO">
				<input type="hidden" value="" id="ID_DESTINO_GASTO"	name="ID_DESTINO_GASTO">			
				<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value=""/>
				
			<label>Saldos Disponibles</label>	
			<table id="tblSaldos" class="display">
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
	                    <th>Septiembre</th>
	                    <th>Octubre</th>
	                    <th>Noviembre</th>
	                    <th>Diciembre</th>
	                    <th>Anual</th>
	                </tr>
	            </thead>
	        </table>
	        
	        <br>
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
							<th><input type="text" class="filtro" name="col1_filter" id="col1_filter"  onMouseOver="windowStatus('Ejercicio Fiscal');" /></th>
							<th><input type="text" class="filtro" name="col2_filter" id="col2_filter"  onMouseOver="windowStatus('Ramo');" /></th>
							<th><input type="text" class="filtro" name="col3_filter" id="col3_filter"  onMouseOver="windowStatus('Unidad Responsable');" /></th>
							<th><input type="text" class="filtro" name="col4_filter" id="col4_filter"  onMouseOver="windowStatus('Grupo Funcional');" /></th>
							<th><input type="text" class="filtro" name="col5_filter" id="col5_filter"  onMouseOver="windowStatus('Funcion');" /></th>
							<th><input type="text" class="filtro" name="col6_filter" id="col6_filter"  onMouseOver="windowStatus('SubFuncion');" /></th>
							<th><input type="text" class="filtro" name="col7_filter" id="col7_filter"  onMouseOver="windowStatus('Programa General');" /></th>
							<th><input type="text" class="filtro" name="col8_filter" id="col8_filter"  onMouseOver="windowStatus('Actividad Institucional');" /></th>
							<th><input type="text" class="filtro" name="col9_filter" id="col9_filter"  onMouseOver="windowStatus('Programa Presupuestario');" /></th>
							<th><input type="text" class="filtro" name="col10_filter" id="col10_filter"  onMouseOver="windowStatus('Partida');" /></th>
							<th><input type="text" class="filtro" name="col11_filter" id="col11_filter"  onMouseOver="windowStatus('Tipo de Gasto');" /></th>
							<th><input type="text" class="filtro" name="col12_filter" id="col12_filter"  onMouseOver="windowStatus('Fuente Financiamiento');" /></th>
							<th><input type="text" class="filtro" name="col13_filter" id="col13_filter"  onMouseOver="windowStatus('Entidad Federativa');" /></th>
							<th><input type="text" class="filtro" name="col14_filter" id="col14_filter"  onMouseOver="windowStatus('Cartera');" /></th>
							<th><input type="text" class="filtro" name="col15_filter" id="col15_filter"  onMouseOver="windowStatus('Unidad Ejecutora');"/></th>
							<th><input type="text" class="filtro" name="col16_filter" id="col16_filter"  onMouseOver="windowStatus('Unidad Norativa');"/></th>
						</tr>
					</tfoot>
	        </table>
			<br/>
			<center>
				<input type="button" id="pbAceptar" style="visibility: hidden" value = "Aceptar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" style="visibility: visible" value = "Cancelar"/>
			</center>
		</div>
	</form>
	</body>
</html>