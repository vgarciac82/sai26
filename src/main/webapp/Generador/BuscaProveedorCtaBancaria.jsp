<%@page import="com.syc.altaproveedor.AltaProveedorBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<% 
	AltaProveedorBusinessLogic apbl = new AltaProveedorBusinessLogic("jdbc/gestion");
	String accion = request.getParameter("accion");
	if ("buscar".equals(accion)) {

	}   
%>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>B&uacute;squeda de RFC</title>
    <link href="../admin/js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css" rel="stylesheet">
    <link href="css/demo_page.css" rel="stylesheet">
    <link href="css/demo_table_jui.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
    <script type="text/javascript" src="../admin/js/jq9/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
    <script type="text/javascript" src="../admin/js/jq9/jquery-ui-1.9.0.custom.js"></script>
    <script type="text/javascript" src="js/crud.js"></script>
    <script type="text/javascript" src="js/jquery.dataTables.js"></script>
    <script type="text/javascript">
        var dTable;
        var folioSAI = "";
        var lengParams = {
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
                sFirst: "Primero",
                sPrevious: "Ant.",
                sNext: "Sigte.",
                sLast: "&Uacute;ltimo"
            }
        };
        $(document).ready(function() {
            init();
        });

        function init() {
            dTable = $("#resultTable").dataTable({
                "bJQueryUI": true,
                "sScrollY": 250,
                "bPaginate": false,
                "oLanguage": lengParams
            });
            setDblClck();

            $("#searachButton").button().click(function() {
                search();
            });

            $("#yes")
                .button()
                .click(
                    function() {
                        $.blockUI({
                            message: "<h1>Espere ...</h1>"
                        });
                        $("#folioSAI").val(folioSAI);
                        queryFormPost({
                            queryName: 'readEstatusAltaProveedor',
                            async: false,
                            callback: function() {
                                var operAct = $("#OperacionActual").val();
                                if (operAct != "5") {
                                    alert("No se agregar cuentas Bancarias, ya que el caso esta en algun proceso");
                                    $.unblockUI();
                                } else {
                                    $("#mainForm").submit();
                                }
                            }
                        });
                    });

            $("#no").button().click(function() {
                $.unblockUI();
                folioSAI = "";
                $("#OperacionActual").val("");
            });

            $("#").focus();

        }

        function setDblClck() {
            $("#resultTable tbody").dblclick(function(evt) {

                var aPos = dTable.fnGetPosition(evt.target.parentNode);

                if (aPos instanceof Array)
                    currIndex = aPos[0];
                else
                    currIndex = aPos;

                var arr = dTable.fnGetData()[currIndex];
                folioSAI = arr[0];
                $.blockUI({
                    message: $('#question'),
                    css: {
                        width: '450px'
                    }
                });
            });
        }

        function search() {
            folioSAI = "";
            $("#OperacionActual").val("");
            if ($("#idRFC").val() == "") {
                alert("Debe ingresar el RFC");
                $("#idRFC").focus();
                return;
            } else {
                $
                    .blockUI({
                        message: "<p><img src='imagenes/wait24trans.gif' />&nbsp;&nbsp;Buscando. Por favor espere...</p>"
                    });
                var condition = " nEnviadoSICOP!=-2 and REPLACE(cidrfc,'-','') LIKE REPLACE('%" + $("#idRFC").val() + "%','-','')";
                var tableName = "BUSCA_PROVEEDOR_BENEFICIARIO";
                $.getJSON("../catalogos/SelectJson.jsp", {
                    Tabla: tableName,
                    Param: condition,
                    MaxReg: "",
                    ajax: 'false'
                }, function(data) {
                    dTable.fnClearTable();
                    for (var i = 0; i < data.length; i++) {
                        dTable.fnAddData([data[i].Col0, data[i].Col1, data[i].Col2,
                            data[i].Col3
                        ]);
                    }
                    $.unblockUI();
                    if (i == 0)
                        alert("No se encontraron coincidencias para : " + $("#idRFC").val());
                });
            }

        }
    </script>
</head>

<body id="dt_example">
    <form action="../ModificaProveedor" method="post" id="mainForm">
        <input type="hidden" value="" id="folioSAI" name="folioSAI">
        <input type="hidden" value="CtasBancarias" id="accion" name="accion">
        <input type="hidden" value="" id="OperacionActual" name="OperacionActual">
        <h1>
						<label id="titulo"> Cuentas Bancarias Proveedores Beneficiarios </label>
					</h1>
        <div id="container" class="container SyCData">
            <h5>Ingrese la siguiente informaci&oacute;n:</h5>
            <table>
                <tr>
                    <td colspan="2" align="right">RFC:</td>
                    <td colspan="2" align="left">
                        <input type="text" id="idRFC" size="30">
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="right">
                        <input type="button" id="searachButton" value="Buscar" class="btnInterfaceBG"/>
                    </td>
                </tr>
            </table>
            <br />
            <h6>Doble Click para agregar/modificar Cuenta Bancaria.</h6>
            <table id="resultTable" width="100%" class="display">
                <thead>
                    <tr>
                        <th width="40px">Folio SAI</th>
                        <th width="40px">RFC</th>
                        <th width="30px">Tipo Persona</th>
                        <th width="30px">Tipo Registro</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <div id="question" style="display: none; cursor: default">
                <h4>&#191;Desea continuar?</h4>
                <br>
                <h5>Se Editara el siguiente Beneficiario/Proveedor.</h5>
                <input type="button" id="yes" value="Continuar" class="btnInterfaceBG"/>
                <input type="button" id="no" value="Cancelar" class="btnInterfaceBG"/>
            </div>
        </div>
    </form>
</body>

</html>