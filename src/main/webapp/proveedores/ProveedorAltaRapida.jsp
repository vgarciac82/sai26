<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%!
	private static final Logger log = LogManager.getLogger("ProveedorAltaRapida.jsp");
%>
<%
Caso  c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);

if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

%>

<!doctype html>
<html lang="es">

  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alta Rápida de proveedores</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
      crossorigin="anonymous">
    <link href="../css/sai.css" rel="stylesheet">
  </head>

  <body>
    <div class="container mt-3 justify-content-center">
      <h1>Alta rapida de proveedores.</h1>
      <form id="proveedorForm" method="POST">

        <input type="hidden" name="clabe" id="clabe" >
        <input type="hidden" name="clabeEliminar" id="clabeEliminar" >
        <input type="hidden" name="constancia_adjunta" id="constancia_adjunta" value="0">
        <input type="hidden" name="cuenta_adjunta" id="cuenta_adjunta"  value="0">
        <input type="hidden"  id="rfc" >

        <div class="row g-3 justify-content-sm-start mb-3">
          <div class="col-sm-1 justify-content-sm-end text-end">
            <label for="folio" class="col-form-label">Folio:</label>
          </div>
          <div class="col-sm-2 justify-content-sm-start">
            <input type="text" readonly class="form-control-plaintext" id="folio" name="folio" value="" required>
          </div>
        </div>

        <div class="card w-100 mb-3">
          <div class="card-header">Datos Generales</div>
          <div class="card-body">
            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <label for="tipoPersona" class="form-label">Tipo Persona:</label>
                <input type="text" readonly class="form-control-plaintext" id="descTipoPersona" name="descTipoPersona" style="display: none; font-weight: bold;">
                <select id="tipoPersona" name="tipoPersona" class="form-select" required>
                  <option selected>Seleccione una opción</option>
                </select>
              </div>

              <div class="col-md-6">
                <label for="regimenFiscal" class="form-label">Regimen Fiscal:</label>
                <select id="regimenFiscal" name="regimenFiscal" class="form-select" required>
                  <option value="">Seleccione una opción</option>
                </select>
              </div>
            </div>

            <div class="row g-3 justify-content-sm-start mb-3">
              <div class="col-md-6">
                <label for="rfc1" class="form-label">
                  Registro Federal de Contribuyentes [RFC]:
                </label>

                <input type="text" readonly class="form-control-plaintext" id="rfcCompuesto" style="display: none; font-weight: bold;"> 

                <div class="input-group" id="rfcParts">
                    <input type="text" class="form-control mayusculas" size="4" id="rfc1" name="rfc1" required>
                    <span class="input-group-text">-</span>
                    <input type="text" class="form-control mayusculas" size="6" id="rfc2" name="rfc2" required>
                    <span class="input-group-text">-</span>
                    <input type="text" class="form-control mayusculas" size="3" id="rfc3" name="rfc3" required>
                </div>

              </div>
            </div>

            <div class="row g-3 justify-content-sm-start mb-3">
              

              <div class="col-md-4 personaMoral" style="display: none;">
                <label for="razonSocial" class="form-label">Razón Social:</label>
                <input type="text" class="form-control mayusculas" id="razonSocial" name="razonSocial"
                  placeholder="Razon Social"  >
              </div>
             
              <div class="col-md-12 personaMoral" style="display: none;">
                <h6 class="h6">Representante Legal;</h6>
              </div>

              <div class="col-md-4">
                <label for="nombre" class="form-label">Nombre:</label>
                <input type="text" class="form-control  mayusculas" id="nombre" name="nombre"
                  placeholder="Nombre(s)" required>
              </div>
              <div class="col-md-4">
                <label for="apellidoPaterno" class="form-label">
                  Apellido
                  Paterno:
                </label>
                <input type="text" class="form-control mayusculas" id="apellidoPaterno" name="apellidoPaterno"
                  placeholder="Apellido Paterno" required>
              </div>
              <div class="col-md-4">
                <label for="apellidoMaterno" class="form-label">
                  Apellido
                  Materno:
                </label>
                <input type="text" class="form-control mayusculas" id="apellidoMaterno" name="apellidoMaterno"
                  placeholder="Apellido Materno" required>
              </div>
              <div class="col-md-8">
                <label for="giro" class="form-label">Giro:</label>
                <input type="text" class="form-control mayusculas" id="giro" name="giro"
                  placeholder="Giro" required>
              </div>
              <div class="col-md-4">
                <label for="pyme" class="form-label">Pyme:</label>
                <select id="pyme"  name="pyme" class="form-select" required>
                  <option selected>Seleccione una opción</option>
                </select>
              </div>
              <div class="col-md-4 personaFisica" style="display: none;">
                <label for="curp" class="form-label">CURP:</label>
                <input type="text" class="form-control mayusculas" id="curp" name="curp"
                  placeholder="AAAA000000AAAAAA00"  >
              </div>
            </div>
          </div>
        </div>
        <div class="card w-100 mb-3">
          <div class="card-header">Domicilio Fiscal</div>
          <div class="card-body">
            <div class="row g-3 justify-content-sm-start mb-3">
              <div class="col-sm-6 justify-content-sm-start">
                <label for="estado" class="col-form-label">Estado:</label>
                <select id="estado" name="estado" class="form-select" required>
                  <option selected>Seleccione una opción</option>
                </select>
              </div>

              <div class="col-sm-6 justify-content-sm-start">
                <label for="municipio" class="col-form-label">Municipio:</label>
                <select id="municipio" name="municipio" class="form-select" required>
                  <option value="">Seleccione una opción</option>
                </select>
              </div>

              <div class="col-md-6">
                <label for="calle" class="form-label">Calle:</label>
                <input type="text" class="form-control mayusculas" id="calle"  name="calle"
                  placeholder="Calle" required>
              </div>
              <div class="col-md-3">
                <label for="noExterior" class="form-label"># Exterior:</label>
                <input type="text" class="form-control mayusculas" id="noExterior" name="noExterior"
                  placeholder required>
              </div>
              <div class="col-md-3">
                <label for="noInterior" class="form-label"># Interior:</label>
                <input type="text" class="form-control mayusculas" id="noInterior" name="noInterior"
                  placeholder>
              </div>
              <div class="col-md-6">
                <label for="colonia" class="form-label">Colonia:</label>
                <input type="text" class="form-control mayusculas" id="colonia" name="colonia"
                  placeholder="Colonia" required>
              </div>
              <div class="col-md-3">
                <label for="codigoPostal" class="form-label">Codigo Postal:</label>
                <input type="text" class="form-control mayusculas" id="codigoPostal" name="codigoPostal"
                  placeholder required>
              </div>
            </div>
          </div>
        </div>
        <div class="card w-100 mb-3">
          <div class="card-header">Contacto</div>
          <div class="card-body">
            <div class="row g-3 justify-content-sm-start mb-3">
              <div class="col-sm-12 justify-content-sm-start">
                <label for="correo" class="form-label">Correo:</label>
                <input type="email" class="form-control" id="correo" name="correo"
                  placeholder="proveedor@dominio" required>
              </div>
              <div class="col-md-12">
                <label for="tipoTelefono" class="form-label" >Teléfono:</label>
              </div>
              <div class="col-md-3">
                <select id="tipoTelefono" name="tipoTelefono" class="form-select" required>
                  <option selected>Seleccione una opción</option>
                </select>
              </div>
              <div class="col-md-4">
                <input type="text" class="form-control" id="telefono" name="telefono"
                  placeholder="0123456789" required>
              </div>
            </div>
          </div>
        </div>

        <div class="card w-100 mb-3">
          <div class="card-header">Cuentas Bancarias</div>
          <div class="card-body">
            <div class="row g-3 justify-content-sm-start mb-3">
              <div class="col-sm-12 justify-content-sm-start">
                <button class="btn btn-primary" type="button" role="button" id="agregarCtaBtn">Agregar Cuenta</button>
              </div>
              <div class="col-md-12">
                <div class="table-responsive">
                  <table class="table  align-middle" id="tablaCuentas">
                    <thead class="table-light">
                      <tr>
                        <th>
                          CLABE
                        </th>
                        <th>
                          Banco
                        </th>
                        <th>
                          Estatus
                        </th>
                        <th>
                          &nbsp;
                        </th>
                      </tr>
                    </thead>
                    <tbody>

                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>

      <div class="card w-100 mb-3">
        <div class="card-header">Documentación</div>
        <div class="card-body">
          <div class="row g-3 justify-content-sm-start mb-3">
            <div class="col-md-12">
              <div class="table-responsive">
                <table class="table  align-middle" id="tablaExpediente">
                  <thead class="table-light">
                    <tr>
                      <th>
                        Documento
                      </th>
                      <th>
                        Acciones
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                     
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card w-100 mb-3" id="observacionesDiv">
        <div class="card-header">Observaciones.</div>
        <div class="card-body">
          <div class="row g-3 justify-content-sm-start mb-3">
            
            
            <div class="col-sm-4 justify-content-sm-start">
              <label for="correo" class="form-label">Motivo de Rechazo:</label>
              <textarea class="form-control-plaintext" style="font-weight: bold;" id="observaciones" name="observaciones" rows="3" readonly></textarea>
            </div>
 
          </div>
        </div>
      </div>

      <div class="card w-100 mb-3">
        <div class="card-header">Acciones</div>
        <div class="card-body">
          <div class="row g-3 justify-content-sm-center mb-3">
            <div class="col-md-6 justify-content-sm-center" >
              <button class="btn btn-primary" type="button" role="button" id="cerrarBtn">Cerrar</button>  
              <span class="mr-1 pr-1"></span>
              <button class="btn btn-primary" type="button" role="button" id="guardarBtn">Guardar</button>  
              <span class="mr-1 pr-1"></span>
              <button class="btn btn-primary" type="button" role="button" id="enviarBtn" style="display: none;">Enviar</button>  
              <span class="mr-1 pr-1"></span>
              <button class="btn btn-danger" type="button" role="button" id="descartarBtn">Descartar</button>  
            </div>
          </div>
        </div>
      </div>

    </div>

    <div class="modal fade" id="nuevaCuenta" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="nuevaCuentaLabel" aria-hidden="true">
      <form id="agregaCuentaBancariaForm" enctype="multipart/form-data" method="post">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="nuevaCuentaLabel">Agregar nueva cuenta.</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"
                aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div class="container">
                <div class="row mb-3">
                  <div class="col-md-12">
                    <label for="clabe" class="form-label">CLABE:</label>
                    <input type="text" class="form-control" id="clabeStr" name="clabeStr" placeholder="012345678901234567">
                  </div>
                </div>

                <div class="row mb-3">
                  <div class="col-md-8 ">
                    <label for="clabe" class="form-label">Banco:</label>
                    <input type="text" class="form-control" id="bancoNombre" readonly >
                  </div>
                  <div class="col-md-4 ">
                    <label for="clabe" class="form-label">Plaza:</label>
                    <input type="text" class="form-control" id="plaza" readonly>
                  </div>
                </div>
                
                <div class="row mb-3 ">
                  <div class="col-md-5 ">
                    <label for="cuenta" class="form-label">Cuenta:</label>
                    <input type="text" class="form-control" id="cuenta" readonly>
                  </div>
                  <div class="col-md-3 ">
                    <label for="digitoControl" class="form-label">Dig. Control:</label>
                    <input type="text" class="form-control" id="digitoControl"
                      readonly>
                  </div>
                  <div class="col-md-2 ">
                    <label for="sucursal" class="form-label">Sucursal:</label>
                    <input type="text" class="form-control" id="sucursal"  >
                  </div>
                </div>

                <div class="row mb-3">
                  <div class="col-md-12 ">
                    <label for="formFile" class="form-label">Estado de Cuenta &lt; 3
                      meses</label>
                    <input class="form-control" type="file" id="formFile" name="formFile">
                  </div>
                  <div class="col-12">
                      <div class="progress">
                          <div class="progress-bar" id="carga-cuenta-progreso" role="progressbar" aria-label="Progreso de Carga" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
                      </div>
                  </div>
                </div>

              </div>

            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-primary" id="agregarCuentaBtn">Agregar</button>
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="cancelAgregarCuentaBtn">Cancelar</button>
            </div>
          </div>
        </div>
      </form>
    </div>

    <div class="modal fade" id="uploadDocument" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="uploadDocumentLabel" aria-hidden="true">
      <form id="fileUploadForm" enctype="multipart/form-data" method="post">
        <input type="hidden" id="documentSelect" name="documentSelect">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="uploadDocumentLabel">Agregar Documento</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">

              <div class="row mb-3">
                <label for="destino" class="col-sm-2 col-form-label">Destino:</label>
                <div class="col-sm-10">
                  <input type="text" readonly class="form-control-plaintext" id="destino" name="destino" value="">
                </div>
              </div>

              <div class="row mb-3">
              
                  <label for="file" class="col-sm-2 col-form-label">Documento:</label>
                  <div class="col-sm-10 ">
                    <input class="form-control" type="file" id="file"  name="file">
                  </div>
              </div>

              <div class="row mb-3">
                <div class="col-12">
                  <div class="progress">
                      <div class="progress-bar" id="upload-progress" role="progressbar" aria-label="Progreso de Carga" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
                  </div>
                </div>
              </div>

            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-primary" id="agregaArchivoBtn">Agregar</button>
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="cancelaAgregaArchivoBtn">Cancelar</button>
            </div>
          </div>
        </div>
     </form>
    </div>

    <script
      src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
      integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
      crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"
      integrity="sha256-JlqSTELeR4TLqP0OG9dxM7yDPqX1ox/HfgiSLBj8+kM="
      crossorigin="anonymous"></script>
      <script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
    <script src="js/ProveedorAltaRapida.js"></script>
    <script src="https://kit.fontawesome.com/aad2c3aad1.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    
    <script  >
	    const idCaso = <%=c.getIdCaso()%>;
      const folio = "<%=c.getFolio()%>";
      const idOper = "<%=c.getCasoOperacion( 0 ).getOperacion().getNumero()%>";
    </script>
  </body>

</html>