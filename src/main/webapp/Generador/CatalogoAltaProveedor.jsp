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
    <title>Catalogo de Alta Proveedor</title>


    <link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

    <style type="text/css" title="currentStyle">
        @import "themes/smoothness/jquery-ui-1.8.4.custom.css";
        @import "css/demo_table_jui.css";
        @import "css/demo_page.css";
        @import "../css/interfaz.css";
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
                sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoAltaProveedor&qw=" + " " + encodeURI($("#cWhere").val()),
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
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "tipoPersona",
                    bSearchable: true,
                    bSortable: true,
                    bVisible: true,
                    sClass: "alignLeft"
                }, {
                    sName: "cDocumentoHaplicado",
                    bSearchable: true,
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
           
           	var where = "cFolio like 'PROV-%-%' ";
           	
            var registro = $("input[name='Registro']:checked").val();
            var tipo = $('#stipo option:selected').val();
            switch( parseInt(tipo,10)){
            case 1:
            	where =where+ " and cDocumentoHaplicado like '%' ";
            break;
            case 2:
            	where =where+ " and cDocumentoHaplicado = 'AUTORIZADO' ";
            break;
            case 3:
            	where =where+ " and cDocumentoHaplicado IN ('VALIDADO, ESPERA DE AUTORIZACION', 'VALIDADO, ESPERA DE AUTORIZACION CORRECCION')  ";
            break;
            case 4:
            	where =where+ " and cDocumentoHaplicado = 'CAPTURA RECHAZADO CUENTAS BANCARIAS'  ";
            break;
            case 5:
            	where =where+ " and cDocumentoHaplicado = 'CAPTURA RECHAZADO DATOS DOCUMENTACION' ";
            break;
            case 6:
            	where =where+ " and cDocumentoHaplicado IN ('CAPTURA CUENTA BANCARIA','CAPTURA DATOS')  ";
            break;
             case 7:
            	where =where+ " and cDocumentoHaplicado IN ('ESPERA DE VALIDACION','ESPERA DE VALIDACION CORRECCION')  ";
            break;
            }
            if(registro == "FISICA"||registro == "MORAL"||registro == "EMPLEADO")
                where = where + "and tipoPersona = '" + registro + "'";
            else if (registro == "PROVEEDOR"||registro == "BENEFICIARIO")
                where = where + "and ctipoRegistro = '" + registro + "'";                	
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
                queryFormPost("obtenDatosCatalogo", {async : false});
                alert("	Folio: "+$("#cFolio").val()+
                		"\n 	CBEN: "+$("#CBEN").val()+
                		"\n 	ENVIADO A SICOP: "+$("#SICOP").val()+
                		"\n 	OPERACION ACTUAL: "+$("#OPERACION").val()+
                		"\n 	SE ENCUENTRA EN: "+$("#ESTAEN").val()+
                		"\n 	ESTATUS: "+$("#ESTATUS").val() + 
                		"\n 	CONTRATOS RESCINDIDOS: "+$("#CONTRATOS_RESCINDIDOS").val()
                );

            });
        }
    </script>
</head>

<body id="dt_example">
    <div id="container" class="container" style="width: 75%" >
        <h1>Catalogo de Alta Proveedor</h1>
        <form id="CatAltaProv" name="CatAltaProv" >

            <input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
            <input id="cWhere" name="cWhere" type="hidden" value=" ">
            <input id="cUR" name="cUR" type="hidden" value="<%=ur%>">
            <input id="cFolio" name="cFolio" type="hidden" value="">
            
            <input id="CBEN" name="CBEN" type="hidden" value="">
            <input id="SICOP" name="SICOP" type="hidden" value="">
            <input id="OPERACION" name="OPERACION" type="hidden" value="">
            <input id="ESTAEN" name="ESTAEN" type="hidden" value="">
            <input id="ESTATUS" name="ESTATUS" type="hidden" value="">
            <input id="CONTRATOS_RESCINDIDOS" name="CONTRATOS_RESCINDIDOS" type="hidden" value="">
            
            <div align="center">
            Estatus:
            <select id="stipo" name="sTipo" onchange="cargaGrid();">
                <option id="1" value="1" selected>Todos</option>
                <option id="2" value="2">Autorizados</option>
                <option id="3" value="3">Validado</option>
                <option id="4" value="4">Rechazado Cuentas Bancarias</option>
                <option id="5" value="5">Rechazado Datos/Documentacion</option>
                <option id="6" value="6">Captura</option>
                <option id="7" value="7">Espera de Validacion</option>
            </select>
            <br>
            <br>
            </div>
            <fieldset>
                <legend>Grupo</legend>
                <table>
                    <tr>
                        <td style="width: 111px; ">
                            <input type="radio" id="1" name="Registro" value="All" onclick="cargaGrid();" checked="checked"> Todos
                        </td>
                        <td style="width: 145px; ">
                            <input type="radio" id="2" name="Registro" value="BENEFICIARIO" onclick="cargaGrid();"> Beneficiarios
                        </td>
                        <td style="width: 165px; ">
                            <input type="radio" id="3" name="Registro" value="PROVEEDOR" onclick="cargaGrid();"> Proveedores
                        </td>
                        <td style="width: 165px; ">
                            <input type="radio" id="4" name="Registro" value="FISICA" onclick="cargaGrid();"> Personas Fisicas
                        </td>
                        <td style="width: 165px; ">
                            <input type="radio" id="5" name="Registro" value="MORAL" onclick="cargaGrid();"> Personas Morales
                        </td>
                        <td style="width: 165px; ">
                            <input type="radio" id="6" name="Registro" value="EMPLEADO" onclick="cargaGrid();"> Empleados
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </fieldset>
            <br>
           <div id= "ntabla2" style=" visibility: visible;  width: 95%" class="container" >
            <table id="dt_CatProv" class="display" align="center" style="width: 1042px; ">
                <thead>
                    <tr>
                        <th><font size="2">Folio</font>
                        </th>
                        <th><font size="2">RFC</font>
                        </th>
                        <th><font size="2">Razon Social</font>
                        </th>
                        <th><font size="2">Tipo Registro</font>
                        </th>
                        <th><font size="2">Tipo Persona</font>
                        </th>
                        <th><font size="2">Estatus</font>
                        </th>
                    </tr>
                </thead>
            </table>
            </div>
        </form>
    </div>
</body>

</html>