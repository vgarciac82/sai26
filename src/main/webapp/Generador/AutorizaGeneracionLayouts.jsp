<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
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
	String cUR = "";
	String RFCUsuario = "";
	String numeroEmpleado = "";
	String tipoAutorizacion = StringUtils.trimToEmpty(  request.getParameter("TYPE") );
	
	cLogin = usuario.getLogin();
	cUR = usuario.getU_UR();
	numeroEmpleado = usuario.getNumeroEmpleado();
	RFCUsuario = usuario.getuRFC();

	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}

	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(cabl.getSystemSetting("SAI_FONDEN"));

	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	String fielMsg = "";
	
	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 

	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
	
	session.removeAttribute("RESULT");
	boolean mostrarResultado = !StringUtils.isBlank(result);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Autorizar Generación de Layout's</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap 5 CSS -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
     
    <!-- DataTables + Bootstrap 5 CSS -->
    <link rel="stylesheet"
          href="https://cdn.datatables.net/2.3.5/css/dataTables.bootstrap5.min.css">
    <!-- SweetAlert2 CSS -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/sweetalert2@11.26.3/dist/sweetalert2.min.css">

    <!-- jQuery -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <!-- Bootstrap 5 JS bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Moment.js -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/moment.min.js"></script>
    <!-- Bootstrap Datetimepicker -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
    <!-- DataTables JS -->
    <script src="https://cdn.datatables.net/2.3.5/js/dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/2.3.5/js/dataTables.bootstrap5.min.js"></script>
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.26.3/dist/sweetalert2.all.min.js"></script>

    <!-- Scripts propios -->
    <script src="js/AutorizaGeneracionLayouts.js"></script>
    <script src="../Generador/js/crud.js"></script>
    <script src="../js/jsquery.js"></script>
    <script src="../js/catalogo/general.js"></script>

    <script type="text/javascript">
        var esSAIAlterno = <%=esSAIAlterno%>;
        var cUR = "<%=cUR%>";
        var cLogin = "<%=cLogin%>";
        var RFC = "<%=RFCUsuario%>";
        var numeroEmpleado = "<%=numeroEmpleado%>";
        var mostrarResultado = <%=mostrarResultado%>;
        var fielExpiringSoon = <%=fielExpiringSoon%>;

        var mensaje = "<%=msg%>";
        var tipoAutorizacion = "<%=tipoAutorizacion%>";
    </script>
</head>
<body id="dt_example" class="bg-light">
    <div id="container" class="container py-4" style="max-width: 80%;">

        <!-- FORM PRINCIPAL -->
        <form>
            <input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>"/>
            <input id="cUR" name="cUR" type="hidden" value="<%=cUR%>"/>
            <input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'"/>
            <input id="RFC" name="RFC" type="hidden" value=""/>

            <!-- Título general -->
            <div class="mb-3">
                <h3 class="text-center">
                    <label id="tituloOperacion"></label>
                </h3>
            </div>

            <!-- CARD: Datos Generales -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Datos Generales</h5>
                </div>
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 mb-2">
                            <label for="uEjecutora" class="form-label mb-0">Unidad Ejecutora:</label>
                        </div>
                        <div class="col-12 col-lg-6 col-md-6 col-sm-12 mb-2">
                            <select id="uEjecutora" name="uEjecutora"
                                    class="form-select form-select-sm"
                                    onchange="cargaGrid();"></select>
                        </div>
                    </div>
                </div>
            </div>

            <!-- CARD: Tipo de Pago -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Tipo de Pago</h5>
                </div>
                <div class="card-body">

                    <div class="row d-flex justify-content-center mb-2">
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago"
                                       class="form-check-input" value="DI"
                                       onclick="cargaGrid();" checked/>
                                <label for="tipoPago" class="form-check-label">Pago Directo</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago2"
                                       class="form-check-input" value="DV"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago2" class="form-check-label">Pago Diverso</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago3"
                                       class="form-check-input" value="FE"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago3" class="form-check-label">Pago Federalizado</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago4"
                                       class="form-check-input" value="RG"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago4" class="form-check-label">Relación Gastos</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago5"
                                       class="form-check-input" value="PO"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago5" class="form-check-label">Pago de Obra</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago6"
                                       class="form-check-input" value="IF"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago6" class="form-check-label">Ingreso Fiscal</label>
                            </div>
                        </div>
                    </div>

                    <div class="row d-flex justify-content-center">
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago7"
                                       class="form-check-input" value="PC"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago7" class="form-check-label">Penas Convencionales</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago8"
                                       class="form-check-input" value="CA"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago8" class="form-check-label">Caja</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago9"
                                       class="form-check-input" value="CV"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago9" class="form-check-label">Comisión Sin Viáticos</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago10"
                                       class="form-check-input" value="PM"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago10" class="form-check-label">Póliza Manual</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago11"
                                       class="form-check-input" value="OA"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago11" class="form-check-label">Operaciones Ajenas</label>
                            </div>
                        </div>
                        <div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago12"
                                       class="form-check-input" value="RC"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago12" class="form-check-label">Reintegros Caja</label>
                            </div>
                        </div>
                        
                    </div>
                    <div class="row ">
                   	 	<div class="col-12 col-lg-2 col-md-3 col-sm-12 p-1">
                            <div class="form-check form-check-inline">
                                <input type="radio" name="cTipoPago" id="tipoPago13"
                                       class="form-check-input" value="CO"
                                       onclick="cargaGrid();"/>
                                <label for="tipoPago13" class="form-check-label">Compromisos</label>
                            </div>
                        </div>
                    </div>

                    <!-- Link log oculto (si lo sigues usando) -->
                    <table id="logTable" style="display: none;">
                        <tr>
                            <td colspan="9" align="left">
                                <a href="#" onclick="muestraLog();return false;">Ver log</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <!-- CARD: Listado de Pagos -->
            <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Listado de Pagos</h5>
                    <!-- Aquí podrías poner filtros adicionales o un badge con totales -->
                </div>
                <div class="card-body">

                    <div class="row pt-2 mb-3">
                        <div class="col-12 col-md-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox"
                                       id="chkTodos" name="chkTodos" value="">
                                <label class="form-check-label" for="chkTodos">
                                    Seleccionar Todos
                                </label>
                            </div>
                        </div>
                    </div>

                    <div id="pagos" class="table-responsive">
                        <table id="dt_AutorizarLayouts"
                               class="table table-striped table-bordered text-nowrap w-100">
                            <thead>
                            <tr>
                                <th></th>
                                <th>UE</th>
                                <th>Folio</th>
                                <th>CXP</th>
                                <th>Nombre</th>
                                <th>Importe</th>
                                <th>Cuenta</th>
                                <th>Concepto de la solicitud</th>
                                <th>Fecha Captura</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div class="card-footer text-end">
                    <button type="button"
                            id="btn_Autoriza"
                            name="btn_Autoriza"
                            class="btn btn-primary"
                            onclick="AutorizaLayouts()">
                        Autorizar
                    </button>
                </div>
            </div>
        </form>

    
        

       

    </div> 
    
    <!-- /container -->
    <!-- MODAL: Resultado de la Operación / Log -->
	<div class="modal fade" id="dlg-Msg" tabindex="-1" aria-labelledby="dlgMsgLabel" aria-hidden="true">
	  <div class="modal-dialog modal-xl"> <!-- XL para ver bien el log -->
	    <div class="modal-content">
	
	      <div class="modal-header">
	        <h5 class="modal-title" id="dlgMsgLabel">Resultado de la Operación</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
	      </div>
	
	      <div class="modal-body">
	
	        <% if (fielExpiringSoon) { %>
	        <div id="fielWarning" class="alert alert-warning border p-2">
	            <strong>Firma a punto de expirar</strong><br>
	            <span id="msgWarning"><%=fielMsg%></span>
	        </div>
	        <% } %>
	
	        <div class="table-responsive">
	          <table id="mnLogTbl" class="table table-striped table-bordered table-sm">
	            <thead>
	              <tr></tr>
	            </thead>
	            <tbody>
	              <%=result%>
	            </tbody>
	          </table>
	        </div>
	
	      </div>
	
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary btn-sm" data-bs-dismiss="modal">Aceptar</button>
	      </div>
	
	    </div>
	  </div>
	</div>
	    
    
    <!-- MODAL FIEL -->
	<div class="modal fade" id="dlg-FIEL" tabindex="-1" aria-labelledby="dlgFielLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg"> <!-- modal-lg si quieres tamaño grande -->
	    <div class="modal-content">
	
	      <div class="modal-header">
	        <h5 class="modal-title" id="dlgFielLabel">Firma Electrónica (FIEL)</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
	      </div>
	
	      <div class="modal-body">
	        <form id="formFIEL" name="formFIEL"
	              method="POST" action="../firmaSolicitudPago"
	              enctype="multipart/form-data">
	
	          <input id="folder"              name="folder"              type="hidden">
	          <input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden">
	          <input id="loginFirma"          name="loginFirma"          type="hidden" value="<%=cLogin%>">
	          <input id="ueFirma"             name="ueFirma"             type="hidden" value="<%=cUR%>">
	          <input id="rfcFirma"            name="rfcFirma"            type="hidden" value="<%=RFCUsuario%>">
	          <input id="tipoAutorizacion"    name="tipoAutorizacion"    type="hidden" value="<%=tipoAutorizacion%>">
	          <input id="nFolios"             name="nFolios"             type="hidden">
	
	          <div class="row mb-3">
	            <div class="col-12 col-lg-10 offset-lg-1">
	              <label for="cerFile" class="form-label">Archivo *.cer</label>
	              <input type="file" id="cerFile" name="cerFile" class="form-control form-control-sm">
	            </div>
	          </div>
	
	          <div class="row mb-3">
	            <div class="col-12 col-lg-10 offset-lg-1">
	              <label for="keyFile" class="form-label">Archivo *.key</label>
	              <input type="file" id="keyFile" name="keyFile" class="form-control form-control-sm">
	            </div>
	          </div>
	
	          <div class="row mb-2">
	            <div class="col-12 col-lg-5 offset-lg-1">
	              <label for="passwordLlave" class="form-label">Password</label>
	              <input type="password" id="passwordLlave" name="passwordLlave"
	                     class="form-control form-control-sm">
	            </div>
	          </div>
	
	        </form>
	      </div>
	
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
	        <button type="button" class="btn btn-primary btn-sm" onclick="aceptarDlg()">Firmar</button>

	      </div>
	
	    </div>
	  </div>
	</div>
</body>
</html>
