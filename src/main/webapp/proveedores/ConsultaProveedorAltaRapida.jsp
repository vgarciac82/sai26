<%@page import="com.syc.gestion.core.Caso" %>
<%@page import="com.axtel.user.entities.ExecutiveUnit" %>
<%@page import="com.syc.gestion.UsuarioBusinessLogic" %>
<%@page import="com.syc.gestion.servlet.GestionInterface" %>
<%@page import="com.syc.gestion.core.Usuario" %>
<%@page import="org.apache.log4j.LogManager" %>
<%@page import="org.apache.log4j.Logger" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%! private static final Logger log=LogManager.getLogger("ProveedorAltaRapida.jsp"); %>
<% Caso c=(Caso) session.getAttribute(GestionInterface.ATT_CASE); if (c==null) {
response.sendRedirect("../index.jsp"); return; } %>

<!doctype html>
<html lang="es">

  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta alta rápida de proveedores</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
      crossorigin="anonymous">
    <link href="../css/sai.css" rel="stylesheet">
    <style>
      .form-control-plaintext{ 
        font-weight: bold;
      }
    </style>
  </head>

  <body>
    <div class="container mt-3 justify-content-center">
      <h1>Alta Rapida de Proveedores Finalizada.</h1>
      <form id="proveedorForm" method="POST">
        <input type="hidden" id="tipoPersona">
      

        <div class="row g-3 justify-content-sm-start mb-3">
          
          <div class="col-sm-1 justify-content-sm-start text-start">
            <label for="folio" class="col-form-label" style="font-weight: bold;">Folio:</label>
          </div>
          
          <div class="col-sm-5 justify-content-sm-start">
            <input type="text" readonly class="form-control-plaintext"  id="folio" name="folio" >
          </div>
          
         
          
          <div class="col-sm-4 justify-content-sm-start text-start">
            &nbsp;
          </div>

          <div class="col-sm-1 justify-content-sm-start text-start">
            <label for="CBEN" class="col-form-label" style="font-weight: bold;">CBEN:</label>
          </div>
          <div class="col-sm-1 justify-content-sm-start">
            <input type="text" readonly class="form-control-plaintext" id="CBEN" name="CBEN" >
          </div>
        </div>

        <div class="card w-100 mb-3">
          <div class="card-header">Datos Generales</div>
          <div class="card-body">
            <div class="row g-3 mb-3">

              <div class="col-md-6">
                <label for="descTipoPersona" class="form-label">Tipo Persona:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="descTipoPersona"
                  name="descTipoPersona" >
              </div>

              <div class="col-md-6">
                <label for="descRegimenFiscal" class="form-label">Regimen
                  Fiscal:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="descRegimenFiscal"
                  name="descRegimenFiscal" >
              </div>
            </div>

            <div class="row g-3 justify-content-sm-start mb-3">
              <div class="col-md-6">
                <label for="rfc1" class="form-label">
                  Registro Federal de
                  Contribuyentes [RFC]:
                </label>
                <div class="input-group">
                  <input type="text" readonly class="form-control-plaintext"
                    id="rfc" name="rfc"
                    > 
                </div>
              </div>

              <div class="col-md-4 personaMoral" style="display: none;">
                <label for="razonSocial" class="form-label">Razón Social:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="razonSocial"
                  name="razonSocial" >
              </div>

            </div>

            <div class="row g-3 justify-content-sm-start mb-3">

              <div class="col-md-12 personaMoral" style="display: none;">
                <h6 class="h6">Representante Legal</h6>
              </div>

              <div class="col-md-4">
                <label for="nombre" class="form-label">Nombre:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="nombre" name="nombre"
                  >

              </div>
              <div class="col-md-4">
                <label for="apellidoPaterno" class="form-label">
                  Apellido
                  Paterno:
                </label>

                <input type="text" readonly class="form-control-plaintext"
                  id="apellidoPaterno"
                  name="apellidoPaterno" >

              </div>
              <div class="col-md-4">
                <label for="apellidoMaterno" class="form-label">
                  Apellido
                  Materno:
                </label>
                <input type="text" readonly class="form-control-plaintext"
                  id="apellidoMaterno"
                  name="apellidoMaterno" >
              </div>

              <div class="col-md-8">
                <label for="giro" class="form-label">Giro:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="giro"
                  name="giro" >
              </div>
              <div class="col-md-4">
                <label for="descPyme" class="form-label">Pyme:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="descPyme"
                  name="descPyme" >

              </div>
              <div class="col-md-4 personaFisica" style="display: none;">
                <label for="curp" class="form-label">CURP:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="curp" name="curp"
                  >
              </div>
            </div>
          </div>
        </div>
        <div class="card w-100 mb-3">
          <div class="card-header">Domicilio Fiscal</div>
          <div class="card-body">
            <div class="row g-3 justify-content-sm-start mb-3">
              <div class="col-sm-6 justify-content-sm-start">
                <label for="descEstado" class="col-form-label">Estado:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="descEstado"
                  name="descEstado" >
              </div>

              <div class="col-sm-6 justify-content-sm-start">
                <label for="descMunicipio" class="col-form-label">Municipio:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="descMunicipio"
                  name="descMunicipio" >
              </div>

              <div class="col-md-6">
                <label for="calle" class="form-label">Calle:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="calle" name="calle"
                  >
              </div>

              <div class="col-md-3">
                <label for="noExterior" class="form-label"># Exterior:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="noExterior"
                  name="noExterior" >
              </div>

              <div class="col-md-3">
                <label for="noInterior" class="form-label"># Interior:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="noInterior"
                  name="noInterior" >
              </div>

              <div class="col-md-6">
                <label for="colonia" class="form-label">Colonia:</label>
                <input type="text" readonly class="form-control-plaintext"
                  id="colonia"
                  name="colonia" >
              </div>

              <div class="col-md-3">
                <label for="codigoPostal" class="form-label">Codigo Postal:</label>
                <input type="text" class="form-control-plaintext"
                  id="codigoPostal"
                  name="codigoPostal" readonly>
              </div>
            </div>
          </div>
        </div>

        <div class="card w-100 mb-3">
          <div class="card-header">Contacto</div>
          <div class="card-body">
            <div class="row g-3 justify-content-sm-start mb-3">
              
              
              <div class="col-sm-4 justify-content-sm-start">
                <label for="correo" class="form-label">Correo:</label>
                <input type="email" class="form-control-plaintext" id="correo"
                  name="correo"
                  readonly>
              </div>

              <div class="col-sm-4">
                <label for="descTipoTelefono" class="form-label">Teléfono:</label>
                <input type="text" class="form-control-plaintext"
                  id="descTipoTelefono"
                  name="descTipoTelefono" readonly>
              </div>

              <div class="col-sm-4">
                <label for="descTipoTelefono" class="form-label">Número:</label>
                <input type="text" class="form-control-plaintext" id="telefono"
                  name="telefono"
                  readonly>
              </div>
            </div>
          </div>
        </div>

        <div class="card w-100 mb-3">
          <div class="card-header">Cuentas Bancarias</div>
          <div class="card-body">
            <div class="row g-3 justify-content-sm-start mb-3">
               
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
          <div class="row g-3 justify-content-sm-center mb-3">
            <div class="col-md-8">
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

          <div class="row g-3">

            <div class="col-sm-4">
              &nbsp;
            </div>
            
            <div class="col-sm-4">
              <button class="btn btn-primary" type="button" role="button" id="cerrarBtn">Cerrar</button>  
            </div>

            <div class="col-sm-4">
              &nbsp;
            </div>

          </div>
        </div>
      </div>
    

    </div>

     

    <script
      src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
      integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
      crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"
      integrity="sha256-JlqSTELeR4TLqP0OG9dxM7yDPqX1ox/HfgiSLBj8+kM="
      crossorigin="anonymous"></script>
    <script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
    <script src="js/ConsultaProveedorAltaRapida.js"></script>
    <script src="https://kit.fontawesome.com/aad2c3aad1.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
      const idCaso = <%=c.getIdCaso() %>;
      const folio = "<%=c.getFolio()%>";
    </script>
  </body>

</html>