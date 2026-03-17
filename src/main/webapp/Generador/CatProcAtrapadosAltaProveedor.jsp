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
	String ur;
	if(usuario.getGrupo("VALIDA_PROVEEDOR")!=null)
		ur="%";
	else
		ur=usuario.getU_UR();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>

<head>
    <title>Catalogo de Procesos Atrapados de Alta Proveedor</title>


    <link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

    <style type="text/css" title="currentStyle">
        @import "themes/smoothness/jquery-ui-1.8.4.custom.css";
        @import "css/demo_table_jui.css";
        @import "css/demo_page.css";
    </style>

    <style type="text/css">
        #dt_example .container {
            width: 1024px;
        }
    </style>
    <link href="../admin/js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css" rel="stylesheet">
    <link href="css/demo_page.css" rel="stylesheet">
    <link href="css/demo_table_jui.css" rel="stylesheet">
    <script type="text/javascript" src="../admin/js/jq9/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
    <script type="text/javascript" src="../admin/js/jq9/jquery-ui-1.9.0.custom.js"></script>
    <script type="text/javascript" src="js/crud.js"></script>
    <script type="text/javascript" src="js/jquery.dataTables.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {

            var dTable = $("#dt_CatProv").dataTable();
           	setDblClck();
            $("#u_Login").val("<%=cLogin%>");
            cargaGrid();

        }); //FIN DEL READY

        function cargaGrid() {

            $("#cWhere").val(generaCondicion());

            dTable = $("#dt_CatProv").dataTable({
                "bPaginate": true,
                "iDisplayLength": "10",
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
                sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vProcesoAltaProveedorAtrapados&qw=" + " " + encodeURI($("#cWhere").val()),
                aoColumns: [{
                    sName: "cFolio",
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cIdRFC",
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "nombre",
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cTipoRegistro",
                    bSearchable: false,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "tipoPersona",
                    bSearchable: false,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "CO_RESPONSABLE",
                    bSearchable: false,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                },{
                    sName: "CO_FECHA_INI",
                    bSearchable: false,
                    bSortable: true,
                    bVisible: true,
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
			//Se solicito que pudieran visualizar todos los proveedores de todas unidades.
           // var where = "cFolio like 'PROV-"+$("#cUR").val()+"-%' "; 
           
           	var where ="1=1";         	
            return where;
        }
        
         function setDblClck() {
            $("#dt_CatProv tbody").dblclick(function(evt) {

                var aPos = dTable.fnGetPosition(evt.target.parentNode);

                if (aPos instanceof Array)
                    currIndex = aPos[0];
                else
                    currIndex = aPos;

                var arr = dTable.fnGetData()[currIndex];
                var folio = arr[0];
                $("#cFolio").val(folio);
                
               if(confirm("Esta seguro de liberar el proceso "+$("#cFolio").val())) 
               	liberaCaso();
                
                //window.open(url);

            });
        }
        
        function liberaCaso() {
			var liberado=false;
			queryFormPost({queryName: "cerrarTramiteAltaProveedor",
				async:false,
				callback: function() {
					liberado=true;
				}
			});
			if(!liberado)
				alert ("Hubo un problema al liberar. Favor de Reportar al administrador");
			else{
				alert ("Proceso liberado Correctamente");
				cargaGrid();
				}
		}
    </script>
</head>

<body id="dt_example">
    <div id="container" class="container" style="width: 75%" >
        <h1>Catalogo de Procesos Atrapados de Alta Proveedor</h1>
        <form id="CatAltaProv" name="CatAltaProv" >

            <input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
            <input id="cWhere" name="cWhere" type="hidden" value=" ">
            <input id="cUR" name="cUR" type="hidden" value="<%=ur%>">
            <input id="cFolio" name="cFolio" type="hidden" value="">
            <div align="center">
            <br>
            <br>
            </div>
            <br>
           <div id= "ntabla2" style=" visibility: visible;  width: 90%" class="container" >
            <table id="dt_CatProv" class="display" align="center">
                <thead>
                    <tr>
                        <th><font size="2">Folio</font>
                        </th>
                        <th><font size="2">RFC</font>
                        </th>
                        <th><font size="2" >Razon Social</font>
                        </th>
                        <th><font size="2">Tipo Registro</font>
                        </th>
                        <th><font size="2">Tipo Persona</font>
                        </th>
                        <th><font size="2">Responsable</font>
                        </th>
                        <th><font size="2">Fecha</font>
                        </th>
                    </tr>
                </thead>
            </table>
            </div>
        </form>
    </div>
</body>

</html>