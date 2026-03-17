<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<body>
    <div class="container mt-3">

        <nav>
            <div class="nav nav-tabs" id="nav-tab" role="tablist">
                <button class="nav-link active" id="projectDesc-tab" data-bs-toggle="tab" data-bs-target="#review"
                    type="button" role="tab" aria-controls="review" aria-selected="true">Proyecto</button>
                <button class="nav-link" id="projectDetail-tab" data-bs-toggle="tab" data-bs-target="#projectDetail"
                    type="button" role="tab" aria-controls="projectDetail" aria-selected="false">Detalle</button>
            </div>
        </nav>

        <div class="tab-content" id="nav-tabContent">
            <div class="tab-pane fade show active" id="review" role="tabpanel" aria-labelledby="review-tab"
                tabindex="0">

                <div class="row m-3">

                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="folioInput">Folio:</label>
                            <input type="text" class="form-control-plaintext" id="folioInput" readonly>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="creationDate">Fecha de creación:</label>
                            <input type="text" id="creationDate" class="form-control-plaintext" value="" readonly>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group ">
                            <label for="lastModifiedDate">Fecha de última modificación:</label>
                            <input type="text" id="lastModifiedDate" class="form-control-plaintext" value="" readonly>
                        </div>
                    </div>
                </div>

                <div class="row m-3">
                    <div class="col-sm-12">
                        <div class="form-group">
                            <label for="titleInput">Título:</label>

                            <textarea class="form-control-plaintext" id="titleInput" rows="2"
                                aria-describedby="titleHelp" readonly></textarea>
                        </div>
                    </div>
                </div>

                <div class="row m-3">
                    <div class="col-sm-12">
                        <div class="form-group">
                            <label for="objectivesInput">Objetivos:</label>
                            <textarea class="form-control-plaintext" id="objectivesInput" rows="4"
                                aria-describedby="objectivesHelp" readonly></textarea>

                        </div>
                    </div>
                </div>

                <div class="row m-3">
                    <div class="col-sm-12">
                        <div class="form-group">
                            <label for="vinculacionInput">Vinculación:</label>
                            <textarea class="form-control-plaintext" id="vinculacionInput" rows="3" readonly></textarea>
                        </div>
                    </div>
                </div>

                <div class="row m-3" id="proyectosTable">
                    <div class="col-sm-12">
                        <label for="searchProjects">Coincidencia de proyectos</label>
                    </div>
                    <div class="col-sm-12" style="max-height: 300px; overflow-y: auto;">
                        <table class="table table-striped table-light">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Folio</th>
                                    <th>Título</th>
                                    <th>Objetivos</th>
                                </tr>
                            </thead>
                            <tbody id="resultsTable">
                                <td colspan="4">Sin Resultados</td>
                            </tbody>
                        </table>
                    </div>
                </div>


            </div>





            <div class="tab-pane fade" id="projectDetail" role="tabpanel" aria-labelledby="projectDetail-tab"
                tabindex="0">




                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Generales Proyecto</h6>
                        <hr>
                    </div>
                </div>
                
                <div class="m-3 row">
                    <div class="col-sm-5">
                        <label for="tipoProyecto">Tipo de documento:</label>
                        <input type="text" id="tipoProyecto" class="form-control-plaintext" value="" readonly>
                    </div>
                    <div class="col-sm-5">
                        <label for="confidentialityLevel">Nivel de confianza:</label>
                        <input type="text" id="confidentialityLevel" class="form-control-plaintext" value="" readonly>
                    </div>
                </div>
                
                 <div class="row m-3">
			        
				        <div class="col-sm-12">
				            <label for="duracionProyecto" class="form-label">Duración</label>
				            <div class="d-flex align-items-center">
				                <input type="text" id="duracionProyecto" class="form-control form-control-plaintext me-2" placeholder="Meses" readonly style="width: 100px;" />
				                
				                <input type="text" id="duracionProyectoDias" class="form-control form-control-plaintext me-2" placeholder="Días" value="0" readonly style="width: 100px;" />
				                
				            </div>
				        </div>
			    </div>

                <div class="row m-3">
                    <div class="col-sm-12 col-md-6">
                        <div class="form-group">
                            <label for="servicioCoordinacion">Titular de la unidad administrativa o coordinación
                                responsable:</label>
                            <input type="text" id="servicioCoordinacion" class="form-control-plaintext" value=""
                                readonly>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-6">
                        <div class="form-group">
                            <label for="servicioGerencia">Titular de la gerencia responsable:</label>
                            <input type="text" id="servicioGerencia" class="form-control-plaintext" value="" readonly>
                        </div>
                    </div>
                </div>

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Territorio de aplicación </h6>
                        <hr>
                    </div>
                </div>
                <div class="row m-3">
                    <div class="col-sm-12">
                        <div class="form-group text-center">

                            <table class="table table-striped table-light" id="territoryTable">
                                <thead>
                                    <tr>
                                        <th>Estado</th>
                                        <th>Municipio</th>

                                        <th>&nbsp;</th>
                                    </tr>
                                </thead>
                                <tbody id="territoryTableBody">
                                    <td colspan="3">No se registró territorio</td>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Productos esperados </h6>
                        <hr>
                    </div>
                </div>
                <div class="row m-3">
                    <div class="col-sm-12">
                        <div class="form-group text-center">
                            <table class="table table-striped table-light" id="productTable">
                                <thead>
                                    <tr>
                                        <th>Clase</th>
                                        <th>Producto</th>
                                        <th>Descripción</th>
                                        <th>&nbsp;</th>
                                    </tr>
                                </thead>
                                <tbody id="productTableBody">
                                    <td colspan="4">No se registraron productos</td>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Terminos de Referencia </h6>
                        <hr>
                    </div>
                </div>
                <div class="row m-3">
                    <div class="col-12">
                        <table class="table table-striped table-light" id="tdrTable">
                            <thead>
                                <tr>
                                    <th scope="col" width="75%">Archivo</th>
                                    <th scope="col" width="25%">Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="tdrTableBody">

                            </tbody>
                        </table>

                    </div>
                </div>

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Calendario de Actividades </h6>
                        <hr>
                    </div>
                </div>
                <div class="row m-3">
                    <div class="col-12">
                        <table class="table table-striped table-light" id="activitiesTable">
                            <thead>
                                <tr>
                                    <th scope="col" width="10%">Año</th>
                                    <th scope="col" width="10%">Mes</th>
                                    <th scope="col" width="70%">Actividad</th>
                                    <th scope="col" width="10%">&nbsp;</th>
                                </tr>
                            </thead>
                            <tbody id="activitiesTableBody">
                                <tr>
                                    <td colspan="4">No se ha registrado actividades</td>
                                </tr>
                            </tbody>
                        </table>

                    </div>
                </div>

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Calendario de Pagos</h6>
                        <hr>
                    </div>
                </div>
                <div class="row m-3">
                    <div class="col-12">
                        <table class="table table-striped table-light" id="paymentsTable">
                            <thead>
                                <tr>
                                    <th scope="col" width="25%">Año</th>
                                    <th scope="col" width="25%">Mes</th>
                                    <th scope="col" width="40%">Cantidad</th>
                                    <th scope="col" width="10%">&nbsp;</th>
                                </tr>
                            </thead>
                            <tbody id="paymentsTableBody">
                                <tr>
                                    <td colspan="4">No se ha registrado pagos</td>
                                </tr>
                            </tbody>
                        </table>

                    </div>
                </div>

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <h6 class="h6">Clave Presupuestal</h6>
                        <hr>
                    </div>
                </div>
                <div class="row m-3">
                    <div class="col-12">
                        <table class="table table-striped table-light" id="budgetTable">
                            <thead>
                                <tr>
                                    <th scope="col" width="10%">Año Inicio</th>
                                    <th scope="col" width="10%">Año Fin</th>
                                    <th scope="col" width="10%">Partida</th>
                                    <th scope="col" width="37%">Unidad/Coordinacion Responsable</th>
                                    <th scope="col" width="30%">Gerencia Responsable</th>
                                    <th scope="col" width="3%">&nbsp;</th>
                                </tr>
                            </thead>
                            <tbody id="budgetTableBody">
                                <tr>
                                    <td colspan="6">No se ha registrado clave presupuestal</td>
                                </tr>
                            </tbody>
                        </table>

                    </div>
                </div>





            </div>



        </div>


        <!-- Sección de botones -->
        <div class="row mt-4">
            <div class="col">
                <!-- Botones de acción centrados -->
                <div class="form-group text-center">
	               <button type="button" class="btn btn-info" id="printBtn"> 
	                   Imprimir Formato
	               </button>
                    <button type="button" class="btn btn-success" id="saveBtn" style="display: none">Aprobar</button>
                    <button type="button" class="btn btn-info" style="display: none;" id="correctBtn">Enviar a
                        corrección</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal" tabindex="-1" id="observationsModal">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable"">
          <div class=" modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Motivo de Rechazo</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="observations">Motivo de Rechazo:</label>
                    <textarea class="form-control" id="observations" rows="3"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                <button type="button" class="btn btn-primary" id="btnReturnRequest">Enviar</button>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <script src="js/sisecop_commons.js"></script>
    <script src="js/ReviewProjectUI.js"></script>

</body>