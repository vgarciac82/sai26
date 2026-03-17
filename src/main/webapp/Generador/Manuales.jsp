<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cLogin = "";
	cLogin = usuario.getLogin();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>

<head>
    <title>Manuales de Operacion</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {

            var dTable = $("#dt_Manuales").dataTable();
            setDblClck();
            $("#u_Login").val("<%=cLogin%>");
            cargaGrid();

        }); //FIN DEL READY

        function cargaGrid() {

            $("#cWhere").val(generaCondicion());

            dTable = $("#dt_Manuales").dataTable({
                "bPaginate": true,
                "iDisplayLength": "20",
                "bLengthChange": true,
                "bFilter": true,
                "bSort": true,
                "bInfo": true,
                "bAutoWidth": false,
                "sScrollY": "100%",
                "sScrollYInner": "100%",
                "bJQueryUI": true,
                "bRetrive": true,
                "bDestroy": true,
                "sPaginationType": "full_numbers",
                "sScrollX": "100%",
                "sScrollXInner": "100%",
                "bScrollCollapse": true,
                "bServerSide": true,
                sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tManualesOperacion&qw=" + " " + encodeURI($("#cWhere").val()),
                aoColumns: [{
                    sName: "id_Manual",
                    bSearchable: false,
                    bSortable: false,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cNombre",
                    bSearchable: true,
                    bSortable: false,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cTipo",
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cGrupo",
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cModulo",
                    bSearchable: false,
                    bSortable: false,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cURL",
                    bSearchable: false,
                    bSortable: false,
                    bVisible: false,
                    sClass: "alignLeft"
                }],
                oLanguage: {
                    sProcessing: "Procesando...",
                    sLengthMenu: "Mostrar _MENU_ registros",
                    sZeroRecords: "No hay registros a mostrar",
                    sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
                    sLoadingRecords: "Cargando...",
                    sInfo: "Registros _START_ al _END_ de _TOTAL_",
                    sInfoEmpty: "Registro 0 al 0 de 0",
                    sInfoFiltered: "(filtered from _MAX_ total entries)",
                    sInfoPostFix: "",
                    sInfoThousands: ",",
                    sSearch: "Buscar:",
                    oPaginate: {
                        sFirst: "Primero",
                        sPrevious: "Ant.",
                        sNext: "Sigte.",
                        sLast: "&Uacute;ltimo"
                    }
                }
            });
        }

        function generaCondicion() {
            var where = "";
            var opt = $("input[name='grupo']:checked").val();
            var tipo = $('#stipo option:selected').val();
            if (tipo == 2)
                where = "ctipo = 'admin' and ";
            else if (tipo == 3)
                where = "ctipo = 'usuario' and ";
            if (opt == "All")
                where = where + "cmodulo like '%'";
            else if (opt == "Obra_Publica" || opt == "Materiales")
                where = where + "cModulo = '" + opt + "'";
            else
                where = where + "cGrupo = '" + opt + "'";

            return where;
        }

        function setDblClck() {
            $("#dt_Manuales tbody").dblclick(function(evt) {

                var aPos = dTable.fnGetPosition(evt.target.parentNode);

                if (aPos instanceof Array)
                    currIndex = aPos[0];
                else
                    currIndex = aPos;

                var arr = dTable.fnGetData()[currIndex];
                var url = arr[5];
                window.open(url);

            });
        }
    </script>
</head>

<body id="dt_example">
<br/>
    <div id="container" class="container" style="width: 80%">
   		<div class="card-header"> <h3> Manuales de Operacion </h3> </div>
		<hr class="mt-3"/>
        
        <form id="Manuales" name="Manuales">
            <input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
            <input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'">
            
            <div class="row d-flex justify-content">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<label for="stipo" class="form-label"> Tipo: </label>
					</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="stipo" name="stipo" class="form-select form-select-sm" onchange="cargaGrid();">
						<option id="1" value="1" selected>Todos</option>
		                <option id="2" value="2">Administrador</option>
		                <option id="3" value="3">Usuario</option>
					</select>
				</div>				
			</div>
			
            <br/>
            
            <h5>Grupo</h5>
			<hr class="mt-3">	
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="grupo" class="form-check-input" value="All" onclick="cargaGrid();" checked/>
						<label for="reporte1" class="form-check-label">Todos</label>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="grupo" class="form-check-input" value="Pago" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Pagos</label>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="grupo" class="form-check-input" value="Tramite" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Tramite</label>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="grupo" class="form-check-input" value="Contrato" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Contratos</label>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="modulo" class="form-check-input" value="Materiales" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Materiales</label>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="modulo" class="form-check-input" value="Obra_Publica" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Obra</label>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<div class="form-check form-check-inline">
						<input type="radio" name="grupo" id="modulo" class="form-check-input" value="Funcionalidad" onclick="cargaGrid();"/>
						<label for="reporte1" class="form-check-label">Funcionalidades</label>
					</div>
				</div>
			</div>
               
            <br/>
            
            <div id="notas" class="table-responsive">	    
				<table id="dt_Manuales" class="table table-striped">            
	                <thead>
	                    <tr>
	                        <th><font size="2"></font>
	                        </th>
	                        <th><font size="2">Nombre</font>
	                        </th>
	                        <th><font size="2">Tipo</font>
	                        </th>
	                        <th><font size="2">Grupo</font>
	                        </th>
	                        <th><font size="2">Modulo</font>
	                        </th>
	                    </tr>
	                </thead>
	            </table>
	        </div>
        </form>
    </div>
</body>

</html>