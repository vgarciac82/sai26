<%@ page language="java" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>
    <head>
    	<meta http-equiv="content-type" content="text/html;charset=utf-8" />

        <title>Making DataTable fully editable</title>

        <link href="css/demo_page.css" rel="stylesheet" type="text/css" />
        <link href="css/demo_table.css" rel="stylesheet" type="text/css" />
        <link href="css/demo_table_jui.css" rel="stylesheet" type="text/css" />
        <link href="themes/smoothness/jquery-ui-1.8.4.custom.css" type="text/css" rel="stylesheet" media="all" />

        <script src="js/jquery-1.6.2.min.js" type="text/javascript"></script>
        <script src="js/jquery-ui.core.js" type="text/javascript"></script>
        <script src="js/jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="js/jquery.validate-1.9.0.js" type="text/javascript"></script>
        <script type="text/javascript">
        $(document).ready(function () {
        	$("#companies").dataTable({
				sScrollY: "304",
				sScrollX: "100%",
				sScrollXInner: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=cat_estados",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aoColumns: [
					{
						sName: "id_estado",
						bSearchable: false,
						bSortable: false,
						bVisible: false
					},
					{ sName: "edo_nombre" },
					{ sName: "edo_abreviatura" }
				]
        	});
        });
        </script>
    </head>
    <body id="dt_example">
        <div id="container">
            <div id="demo_jui">
		        <table id="companies" class="display">
		            <thead>
		                <tr>
		                	<th>ID</th>
		                    <th>Estado</th>
		                    <th>Abreviatura</th>
		                </tr>
		            </thead>
		            <tbody/>
		        </table>
		    </div>
        </div>
    </body>
</html>