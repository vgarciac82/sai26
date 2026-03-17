<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	String ur = usuario.getU_UR();
	String login = usuario.getLogin();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
			.getValor();

	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());

	String mensaje = (request.getParameter("mensaje") == null)
			? "vacio"
			: request.getParameter("mensaje");
	System.out.println("mensaje_" + mensaje);
	
	//7175 015510548020 
	String msgUpdate = "";
	
	if( session.getAttribute("MSG_RESP") != null ){
		msgUpdate = (String)session.getAttribute("MSG_RESP");
		session.removeAttribute("MSG_RESP");
	}
%>

<!DOCTYPE html PUBLIC>
<html>
<head>

<title>Carga Layout Operaciones Ajenas</title>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
	var urUsuario = "<%=ur%>";
	var loginUsuario = "<%=login%>";
	var oTableCargas;
	var oTableDetalle;
	var msgUpdate = "<%=msgUpdate%>";
	
	var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		}
	};
	
	$(document).ready(function(){				
		var mensaje = "<%=mensaje%>";

		$( "#btnAplicar" ).hide();
		$( "#btnLimpiar" ).hide();
		$( "#btnAplicar" ).button();
		$( "#btnLimpiar" ).button();
		$( "#btnEnviarLayout" ).button();

		if( mensaje != "vacio" ) {
			var ar = mensaje.split( ":" );
			mensaje = ar[ 0 ];
			var folio = ar[ 1 ];

			if( mensaje == "Error" ) {
				Swal.fire({ icon: "error",
							text: "Verificar el Archivo Cargado : \n - Numero de Columnas  \n -Formato Correcto Solicitado."});
				//alert( "Verificar el Archivo Cargado : \n - Numero de Columnas  \n -Formato Correcto Solicitado" );
			} else if( mensaje == "Detalle" ) {
				Swal.fire({ icon: "warning",
							text: "El Archivo se cargo con algunos detalles al momento de su validacion \n Corregir y Cargar " + folio});
				//alert( "El Archivo Se cargo con algunos Detalles al momento de su Validacion \n Corregir y Cargar " + folio );
				informacionCargadaTemp( folio );
				$( "#btnEnviarLayout" ).hide();
				$( "#btnLimpiar" ).show();
			} else {
				Swal.fire({ icon: "success",
							text: "Archivo Cargado Correctamente."});
				//alert( "Archivo Cargado Correctamente" );
				informacionCargadaTemp( folio );
				$( "#btnAplicar" ).show();
				$( "#btnEnviarLayout" ).hide();
				$( "#btnLimpiar" ).show();
			}

			mensaje = "vacio";

			$( "#folioTempGral" ).val( folio );
			$( "#dialog-procesar" ).dialog( "close" );
		}

		$( "#btnEnviarLayout" ).click( function() {
			cargarInformacion( this.form.flOperacionAjena.value );
		} );

		$( "#btnAplicar" ).click( function() {
			aplicarInformacion( this.form.flOperacionAjena.value );
		} );

		querySelectPost( "tEjercicioRead", "aEjercicioFiscal", {
			async : false
		} );
		
		$( '#tblCargaDatos' ).dataTable( {
			iDisplayLength : 20,
		 	sScrollXInner: "100%", 
			sScrollX: "100%",
			bPaginate : true,
			bLengthChange : false,
			bFilter : false,
			bSort : false,
			bInfo : false,
			bAutoWidth : false,
			bJQueryUI : true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType : "full_numbers",
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			},
			aoColumns : [ {
					bSearchable : true,
					bSortable : false,
					bVisible : true,
					sClass : "Left"
				}, {
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "Left"
				}, {
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "Center"
				}, {
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "Left"
				}, {
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "Left"
				}, {
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "Left"
				} ]
		} );

		$( '#dialog-procesar' ).dialog( {
			autoOpen : false,
			modal : true,
			resizable : true,
			width : 500,
			heigth : 900,
			title : 'Procesando',
			show : "blind",
			hide : "scale",
			closeOnEscape : true,
			overlay : {
				backgroundColor : '#FFF',
				opacity : 6.5
			}

		} );		
	} );

	function cargarInformacion( archivo ) {

		var ext = new Array( ".csv" );
		var correcto = false;
		var extension = ( archivo.substring( archivo.lastIndexOf( "." ) ) ).toLowerCase();

		if( ext[ 0 ] == extension ) {
			correcto = true;
		}

		if( !correcto ) {
			Swal.fire({ icon: "error",
						text: "Comprueba la extensión de los archivos a subir."});			
			return;

		} else {
			Swal.fire({
				 title: '¿Desea continuar?',
				 text: "Esta Seguro de Cargar Informacion.",
				 icon: 'warning',
				 showCancelButton: true,
				 confirmButtonColor: '#288BA8',
				 cancelButtonColor: '#e6e6e6',
				 confirmButtonText: 'Aceptar',
				 cancelButtonText: 'Cancelar'
			   }).then((result) => {
				 if (result.isConfirmed) {
					$( "#frmLayoutOA" ).attr( "action", "../gstnmngr/LayoutOperAjenaServlet" );
					$( "#frmLayoutOA" ).attr( "enctype", "multipart/form-data" );

					$( "#dialog-procesar" ).dialog( "open" );
					$( "#frmLayoutOA" ).submit();
				 } else if (result.dismiss === Swal.DismissReason.cancel) {
					return;
				 }
			   })
		}
				
	}

	function informacionCargadaTemp( folio ) {
		$( '#tblCargaDatos' ).dataTable().fnClearTable();
		var szTabla = "tpasivosContingentesLaborales_temp";
		var campos = " folioTempGral = " + folio;
		var elParametro = "";
		var order = "";

		$.getJSON( "../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Campos : campos,
		Param : elParametro,
		MaxReg : "10",
		Order : order,
		ajax : 'true'
		}, function( j ) {

			for( var i = 0; i < j.length; i++ ) {
				$( "#tblCargaDatos" ).dataTable().fnAddData( [ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2, j[ i ].Col3 ] );
			}
		} );

	}

	function limpiarTabla( ) {
		$( "#folioTempGral" ).val("");
		$( "#btnEnviarLayout" ).show();
		$( "#btnLimpiar" ).hide();
		$( "#btnAplicar" ).hide();
		mensaje = "";
		$( '#tblCargaDatos' ).dataTable().fnClearTable();
		var szTabla = "tpasivosContingentesLaborales_limpia";		
		var elParametro = "";
		var order = "";

		$.getJSON( "../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Campos : campos,
		Param : elParametro,
		MaxReg : "10",
		Order : order,
		ajax : 'true'
		}, function( j ) {

			for( var i = 0; i < j.length; i++ ) {
				$( "#tblCargaDatos" ).dataTable().fnAddData( [ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2, j[ i ].Col3 ] );
			}
		} );					
	}
	
	function aplicarInformacion() {
		if($("#folioTempGral").val() != 0){
			Swal.fire({
				 title: '¿Desea continuar?',
				 text: "Esta Seguro de Cargar Informacion.",
				 icon: 'warning',
				 showCancelButton: true,
				 confirmButtonColor: '#288BA8',
				 cancelButtonColor: '#e6e6e6',
				 confirmButtonText: 'Aceptar',
				 cancelButtonText: 'Cancelar'
			   }).then((result) => {
				 if (result.isConfirmed) {
					$( "#frmLayoutOA" ).attr( "action", "../gstnmngr/LayoutOperAjenaServlet" );
					$( "#frmLayoutOA" ).attr( "enctype", "multipart/form-data" );
	
					$( "#dialog-procesar" ).dialog( "open" );
					$( "#frmLayoutOA" ).submit();
				 } else if (result.dismiss === Swal.DismissReason.cancel) {
					return;
				 }
			   })
		}else{
			Swal.fire({ icon: "error",
						text: "Favor de cargar el lay out a aplicar."});			
		}		
	}
	
</script>
</head>

<body id="dt_example">
<br/>	
	<form method="post" id="frmLayoutOA" name="frmLayoutOA">
		<div id="container" class="container" style="width: 80%">
			<select style="visibility: hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"></select>
			<input type="hidden" name="nFolioCargaMasiva" id="nFolioCargaMasiva" value="" />
			<input type="hidden" name="mensaje" id="mensaje" value="JAJAJ" />
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=ur%>" />
			<input type="hidden" name="cUnidadResponsableContable" id="cUnidadResponsableContable" value="RHQ" />
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%= cCentroContable%>" />
			<input type="hidden" name="cRamo" id="cRamo" value="16" />
			<input type="hidden" name="login" id="login" value="<%=login%>" />
			<input type="hidden" name="folioTempGral" id="folioTempGral" />
			<input type="hidden" name="fAplicacion" id="fAplicacion" value="<%= today%>">

			<div class="card-header"> <h3> Layout Operaciones Ajenas (.csv) </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-6">									
					
			  		<input type="file" id="flOperacionAjena" name="flOperacionAjena" class="form-control form-control-sm" style="width: 30em;"/>				
				</div>
			
				<div class="col-4">															
					<input type="button" id="btnEnviarLayout" name="agrbtnEnviarLayoutegar" value="Cargar" class="btn btn-secondary"/>
					<input type="button" id="btnLimpiar" name="btnLimpiar" value="Limpiar" class="btn btn-secondary" onclick="limpiarTabla()"/>
					<input type="button" id="btnAplicar" name="btnAplicar" value="Guardar" class="btn btn-secondary" onclick="aplicarInformacion()"/>				
				</div>
			</div>

			<br/>
			
			<div id="datos" class="table-responsive">	
				<table id="tblCargaDatos" class="table table-striped">
					<thead>
						<tr align="center">
							<th>Cxp</th>																
							<th>EP</th>
							<th>Retencion</th>
							<th>Importe</th>
							<th>Mes</th>
							<th>Estatus</th>
						</tr>
					</thead>
				</table>
			</div>
									
		</div>
	</form>
</body>
</html>

