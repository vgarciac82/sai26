<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<body>
    <div class="container mt-3">
        <!-- Navegaci&oacute;n por pesta&ntilde;as -->
        <ul class="nav nav-tabs" id="tabContainer" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="projectDesc-tab" data-bs-toggle="tab" data-bs-target="#captura"
                    type="button" role="tab" aria-controls="captura" aria-selected="true">
                    Captura de Proyecto
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="projectDetail-tab" data-bs-toggle="tab" data-bs-target="#documentos"
                    type="button" role="tab" aria-controls="documentos" aria-selected="false">
                    Documentos
                </button>
            </li>
        </ul>

        <!-- Contenido de pesta&ntilde;as -->
        <div class="tab-content" id="registroProyecto">
            <!-- Pesta&ntilde;a de Captura de Proyecto -->

            <div class="tab-pane fade show active" id="captura" role="tabpanel" aria-labelledby="projectDesc-tab"
                tabindex="0">
                <!-- Formulario de Captura de Proyecto -->
                <form>
                    <div class="row m-3">

                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="folioInput">Folio:</label>
                                <div class="input-group">
                                    <input type="text" class="form-control" id="folioInput" placeholder="Folio"
                                        aria-label="Folio" aria-describedby="basic-addon2" readonly />

                                </div>
                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-group">
                                <label for="creationDate">Fecha de creaci&oacute;n:</label>
                                <input type="text" id="creationDate" class="form-control" value="" readonly />
                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-group">
                                <label for="lastModifiedDate">Fecha de &uacute;ltima modificaci&oacute;n:</label>
                                <input type="text" id="lastModifiedDate" class="form-control" value="" readonly />
                            </div>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="titleInput">T&iacute;tulo:</label>

                                <textarea class="form-control" id="titleInput" rows="2" aria-describedby="titleHelp"
                                    required onkeydown="return (event.keyCode != 13);" onblur="buscar()"></textarea>

                                <small id="titleHelp" class="form-text text-muted">Especificar si se trata de una
                                    asesor&iacute;a, estudio u
                                    otro servicio...</small>
                            </div>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="objectivesInput">Objetivos:</label>
                                <textarea class="form-control" id="objectivesInput" rows="4"
                                    aria-describedby="objectivesHelp" required onblur="buscar()"></textarea>
                                <small id="objectivesHelp" class="form-text text-muted">Tal y como se plantea en los
                                    t&eacute;rminos de
                                    referencia...</small>
                            </div>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="vinculacionInput">Vinculaci&oacute;n:</label>
                                <textarea class="form-control" id="vinculacionInput" rows="3"
                                    aria-describedby="vinculacionHelp"></textarea>
                                <small id="vinculacionHelp" class="form-text text-muted">Vinculaci&oacute;n con metas
                                    del programa anual de trabajo
                                    y/o presupuesto de egresos...</small>
                            </div>
                        </div>
                    </div>

                    <div class="row m-3" id="observationsDiv">
                        <div class="col-sm-12 text-center">
                            <a href="#" class="btn btn-light" id="showObservationsBtn"
                                onclick="processObservations()">Mostrar Observaciones</a>
                        </div>
                    </div>

                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label for="searchProjects">Coincidencia de proyectos</label>
                        </div>
                        <div class="col-sm-12" style="max-height: 300px; overflow-y: auto">
                            <table class="table table-striped table-light">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Folio</th>
                                        <th>T&iacute;tulo</th>
                                        <th>Objetivos</th>
                                        <th>Seleccionar</th>
                                    </tr>
                                </thead>
                                <tbody id="resultsTable">
                                    <td colspan="5">Sin Resultados</td>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Pesta&ntilde;a de Documentos -->
            <div class="tab-pane fade" id="documentos" role="tabpanel" aria-labelledby="documentos" tabindex="1">
                <!-- Formulario para la pesta&ntilde;a de Documentos -->

                <div class="m-3 row">
                    <div class="col-sm-12">
                        <label for="tipoProyecto">Tipo de documento:</label>
                        <select class="form-select mb-3" id="tipoProyecto" name="tipoProyecto"></select>
                    </div>
                </div>

                <!-- Nivel de confianza -->
                <div class="m-3 row">
                    <div class="col-sm-12">
                        <label for="confidentialityLevel">Nivel de confianza:</label>
                        <select class="form-select mb-3" id="confidentialityLevel"></select>
                    </div>
                </div>

                <div class="row m-3">
                    <div class="col-sm-12">
                        <label for="duracionProyecto" class="form-label">Duración:</label>
                        <div class="d-flex align-items-center">
                            <input type="text" class="form-control me-2" id="duracionProyecto" placeholder="Meses" style="width: 100px;" />
                            <span class="me-2">Meses</span>
                            <input type="text" class="form-control me-2" id="duracionProyectoDias" placeholder="Días" value="0" style="width: 100px;" />
                            <span>Días</span>
                        </div>
                    </div>
                </div>

                <div class="row m-3">
                    <div class="col-sm-12 col-md-6">
                        <div class="form-group">
                            <label for="servicioCoordinacion">Titular de la unidad administrativa o coordinaci&oacute;n
                                responsable:</label>
                            <input type="text" class="form-control" id="servicioCoordinacion" placeholder="" />
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-6">
                        <div class="form-group">
                            <label for="servicioGerencia">Titular de la gerencia responsable:</label>
                            <input type="text" class="form-control" id="servicioGerencia" placeholder="" />
                        </div>
                    </div>
                </div>

				<!-- 
                <hr />
                <form id="saveCVForm" enctype="multipart/form-data" method="post">
                    <div class="row m-3">
                        <div class="col-sm-12 col-md-6">
                            <div class="form-group">
                                <label for="procedimientoContratacion">Procedimiento de ContrataciÃ³n a seguir:</label>
                                <input type="text" class="form-control" id="procedimientoContratacion" placeholder=""
                                    required />
                            </div>
                        </div>

                        <div class="col-sm-12 col-md-6">
                            <div class="form-group">
                                <label for="nombrePostulante">Nombre/Razon Social Postulante:</label>
                                <input type="text" class="form-control" id="nombrePostulante" placeholder="" />
                            </div>
                        </div>

                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="justicacionPostulacion">JustificaciÃ³n de la PostulaciÃ³n:</label>
                                <textarea class="form-control" id="justicacionPostulacion"></textarea>
                            </div>
                        </div>

                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="cvFile">Curriculum Postulante:</label>
                                <input type="file" class="form-control" id="cvFile" name="cvFile" />
                            </div>
                        </div>

                        <div class="col-12">
                            <div class="progress">
                                <div class="progress-bar" id="upload-cvFile-progress" role="progressbar"
                                    aria-label="Progreso de Carga" style="width: 0%" aria-valuenow="0" aria-valuemin="0"
                                    aria-valuemax="100">
                                    0%
                                </div>
                            </div>
                        </div>
                        <div class="form-group text-center col-12 m-3">
                            <button type="button" class="btn btn-primary" id="saveCVFileTDRBtn">
                                Enviar CV
                            </button>
                        </div>

                    </div>
                </form>
                -->
                
                <!-- Territorio de aplicaci&oacute;n -->
                <hr />
                <form id="territoryForm">
                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label>Territorio de aplicaci&oacute;n:</label>
                        </div>
                    </div>

                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label for="state">Estado:</label>
                            <select class="form-select" id="state" name="state"
                                onchange="loadCatalogMunicipality()"></select>
                        </div>
                    </div>

                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label for="municipality">Municipio:</label>
                            <select class="form-select" id="municipality" name="municipality">
                                <option value="">Selecciona un municipio</option>
                            </select>
                        </div>
                    </div>
                </form>

                <div class="row m-3">
                    <div class="col-sm-12">
                        <div class="form-group text-center">
                            <button type="button" class="btn btn-light-" id="addTerritoryBtn" onclick="addTerritory()">
                                Agregar Territorio
                            </button>
                            <table class="table table-striped table-light" id="territoryTable">
                                <thead>
                                    <tr>
                                        <th>Estado</th>
                                        <th>Municipio</th>

                                        <th>&nbsp;</th>
                                    </tr>
                                </thead>
                                <tbody id="territoryTableBody">
                                    <td colspan="3">No se ha registrado territorio</td>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Productos esperados -->
                <hr />
                <form id="serviceProducts">
                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label>Productos esperados:</label>
                        </div>
                    </div>
                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label for="productClasification">Clasificaci&oacute;n de producto:</label>
                            <select class="form-select" id="productClasification" name="productClasification"
                                onchange="loadSubproduct()"></select>
                        </div>
                    </div>

                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label for="subProduct">Subclasificaci&oacute;n de producto</label>
                            <select class="form-select" id="subProduct" name="subProduct">
                                <option value="">Selecciona un producto</option>
                            </select>
                        </div>
                        <div class="col-sm-12">
                            <label for="productDescription">Descripcion</label>

                            <input type="text" class="form-control" id="productDescription"
                                placeholder="Breve descripci&oacute;n del producto" />
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12">
                            <div class="form-group text-center">
                                <button type="button" class="btn btn-light-" id="addProductBtn" onclick="addProduct()">
                                    Agregar Producto
                                </button>
                                <table class="table table-striped table-light" id="productTable">
                                    <thead>
                                        <tr>
                                            <th>Clase</th>
                                            <th>Producto</th>
                                            <th>Descripci&oacute;n</th>
                                            <th>&nbsp;</th>
                                        </tr>
                                    </thead>
                                    <tbody id="productTableBody">
                                        <td colspan="4">No se ha registrado productos</td>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </form>

                <!-- T&Eacute;RMINOS DE REFERENCIA -->
                <hr />
                <form id="saveTDRForm" enctype="multipart/form-data" method="post">
                    <div class="m-3 row">
                        <div class="col-sm-12">
                            <label>T&eacute;rminos de Referencia:</label>
                        </div>
                    </div>
                    <div class="row m-3">
                        <div class="col-sm-12">
                            <label for="tdrDescription" class="col-sm-12">Descripci&oacute;n:</label>
                            <input type="text" required class="form-control" aria-label="Descripci&oacute;n del Archivo"
                                id="tdrDescription" name="tdrDescription" />
                        </div>
                    </div>

                    <div class="row m-3 uploadFile">
                        <div class="input-group mb-3 col-sm-12">
                            <input type="file" class="form-control" required id="documentFile" name="documentFile" />
                        </div>

                        <div class="col-12">
                            <div class="progress">
                                <div class="progress-bar" id="upload-file-progress" role="progressbar"
                                    aria-label="Progreso de Carga" style="width: 0%" aria-valuenow="0" aria-valuemin="0"
                                    aria-valuemax="100">
                                    0%
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group text-center">
                        <button type="button" class="btn btn-primary" id="saveFileTDRBtn">
                            Guardar TDR
                        </button>
                    </div>
                </form>

                <div class="row m-3">
                    <div class="col-12">
                        <table class="table table-striped table-light" id="tdrTable">
                            <thead>
                                <tr>
                                    <th scope="col" width="75%">Archivo</th>
                                    <th scope="col" width="25%">Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="tdrTableBody"></tbody>
                        </table>
                    </div>
                </div>

                <!-- Actividades -->
                <hr />
                <form id="activitiesForm">
                    <div class="row m-3">
                        <div class="col-sm-12">
                            <label>Calendario de Actividades:</label>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12 col-md-2">
                            <label for="activityYear">A&ntilde;o:</label>
                            <input type="text" id="activityYear" class="form-control" value="" required />
                        </div>
                        <div class="col-sm-12 col-md-4">
                            <label for="activityMonth">Mes:</label>
                            <select id="activityMonth" class="form-select" required>
                                <option value="">Selecciona un mes</option>
                                <option value="1">Enero</option>
                                <option value="2">Febrero</option>
                                <option value="3">Marzo</option>
                                <option value="4">Abril</option>
                                <option value="5">Mayo</option>
                                <option value="6">Junio</option>
                                <option value="7">Julio</option>
                                <option value="8">Agosto</option>
                                <option value="9">Septiembre</option>
                                <option value="10">Octubre</option>
                                <option value="11">Noviembre</option>
                                <option value="12">Diciembre</option>
                            </select>
                        </div>
                    </div>
                    <div class="row m-3">
                        <div class="col-sm-12 col-md-4">
                            <label for="activityDescription">Descripci&oacute;n:</label>
                            <textarea class="form-control" id="activityDescription"></textarea>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-12 text-center">
                            <button type="button" class="btn btn-primary" id="saveActivitiesBtn">
                                Guardar Actividades
                            </button>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-12">
                            <table class="table table-striped table-light" id="activitiesTable">
                                <thead>
                                    <tr>
                                        <th scope="col" width="10%">A&ntilde;o</th>
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
                </form>

                <!-- Pagos -->
                <hr />
                <form id="paymentForm">
                    <div class="row m-3">
                        <div class="col-sm-12">
                            <label>Calendario de Pagos:</label>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12 col-md-2">
                            <label for="paymentYear">A&ntilde;o:</label>
                            <input type="text" id="paymentYear" class="form-control" value="" required />
                        </div>

                        <div class="col-sm-12 col-md-4">
                            <label for="paymentMonth">Mes:</label>
                            <select id="paymentMonth" class="form-select" required>
                                <option value="">Selecciona un mes</option>
                                <option value="1">Enero</option>
                                <option value="2">Febrero</option>
                                <option value="3">Marzo</option>
                                <option value="4">Abril</option>
                                <option value="5">Mayo</option>
                                <option value="6">Junio</option>
                                <option value="7">Julio</option>
                                <option value="8">Agosto</option>
                                <option value="9">Septiembre</option>
                                <option value="10">Octubre</option>
                                <option value="11">Noviembre</option>
                                <option value="12">Diciembre</option>
                            </select>
                        </div>

                        <div class="col-sm-12 col-md-6">
                            <label for="paymentAmount">Cantidad:</label>
                            <input type="text" class="form-control" id="paymentAmount" />
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-12 text-center">
                            <button type="button" class="btn btn-primary" id="savePaymentBtn">
                                Guardar Pago
                            </button>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-12">
                            <table class="table table-striped table-light" id="paymentsTable">
                                <thead>
                                    <tr>
                                        <th scope="col" width="25%">A&ntilde;o</th>
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
                </form>

                <!-- Clave Presupuestal -->
                <hr />
                <form id="budgetClasificationForm">
                    <div class="row m-3">
                        <div class="col-sm-12">
                            <label>Clave Presupuestal:</label>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12 col-md-4">
                            <label for="initialYear">A&ntilde;o Inicio:</label>
                            <input type="text" id="initialYear" class="form-control" value="" required />
                        </div>
                        <div class="col-sm-12 col-md-4">
                            <label for="endYear">A&ntilde;o Termino:</label>
                            <input type="text" id="endYear" class="form-control" value="" required />
                        </div>
                        <div class="col-sm-12 col-md-4">
                            <label for="budgetSection">Partida Presupuestal:</label>
                            <input type="text" id="budgetSection" class="form-control" value="" required />
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-sm-12 col-md-6">
                            <label for="administrativeUnit">Unidad Administrativa o Coordinaci&oacute;n
                                Responsable:</label>
                            <input type="text" id="administrativeUnit" class="form-control" value="" required />
                        </div>

                        <div class="col-sm-12 col-md-6">
                            <label for="responsibleManager">Gerencia Responsable:</label>
                            <input type="text" id="responsibleManager" class="form-control" value="" required />
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-12 text-center">
                            <button type="button" class="btn btn-primary" id="saveBudgetClasificationBtn">
                                Guardar Clave
                            </button>
                        </div>
                    </div>

                    <div class="row m-3">
                        <div class="col-12">
                            <table class="table table-striped table-light" id="budgetTable">
                                <thead>
                                    <tr>
                                        <th scope="col" width="10%">A&ntilde;o Inicio</th>
                                        <th scope="col" width="10%">A&ntilde;o Fin</th>
                                        <th scope="col" width="10%">Partida</th>
                                        <th scope="col" width="37%">
                                            Unidad/Coordinaci&oacute;n Responsable
                                        </th>
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
                </form>
            </div>
        </div>
    </div>

    <!-- Secci&oacute;n de botones -->
    <div class="row mt-4">
        <div class="col">
            <!-- Botones de acci&oacute;n centrados -->
            <div class="form-group text-center">
                <button type="button" class="btn btn-success" id="saveBtn">
                    Guardar
                </button>
                <button type="button" class="btn btn-info" style="display: none" id="printBtn"> 
                    Imprimir Formato
                </button>
                <button type="button" class="btn btn-info" style="display: none" id="sendBtn">
                    Enviar a revisi&oacute;n
                </button>
                <button type="button" class="btn btn-danger" style="display: none" id="deleteBtn">
                    Cancelar
                </button>
            </div>
        </div>
    </div>


    <!-- Modal para seleccionar el tipo de excepcion -->
    <div class="modal fade" id="exceptionModal" tabindex="-1" aria-labelledby="exceptionModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="exceptionModalLabel">Seleccionar Excepci&oacute;n</hjson>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="d-flex justify-content-center gap-2 mb-3">
                        <button type="button" class="btn btn-light" onclick="showExplanation('ampliacion')">
                            Ampliaci&oacute;n
                        </button>
                        <button type="button" class="btn btn-light" onclick="showExplanation('modificacion')">
                            Modificaci&oacute;n
                        </button>
                        <button type="button" class="btn btn-light" onclick="showExplanation('complemento')">
                            Complemento
                        </button>
                        <button type="button" class="btn btn-light" onclick="showExplanation('continuacion')">
                            Continuaci&oacute;n
                        </button>
                    </div>
                    <div class="text-center">
                        <p id="ampliacion_explicacion" class="lead excepcion" style="display: none;">
                            Ampliaci&oacute;n: Se refiere a la modificación de la duración de un proyecto ya existente.
                        </p>
                        <p id="modificacion_explicacion" class="lead excepcion" style="display: none;">
                            Modificaci&oacute;n: En este caso, se modificar&aacute;n los productos del mismo proyecto.
                        </p>
                        <p id="complemento_explicacion" class="lead excepcion" style="display: none;">
                            Complemento: En este caso, se modificarán los entregables del mismo proyecto.
                        </p>
                        <p id="continuacion_explicacion" class="lead excepcion" style="display: none;">
                            Continuaci&oacute;n: El proyecto continúa sin modificación, a excepción de la persona responsable.
                        </p>
                    </div>
                    
                    <div class="row m-3">
                    	<div class="col-sm-4">
                            <div class="form-group">
                                <label for="idOriginal">ID:</label>
                                <input type="text" class="form-control" id="idOriginal" readonly="readonly" />
                            </div>
                        </div>
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="titleNuevoInput">T&iacute;tulo Nuevo:</label>
                                <textarea class="form-control" id="titleNuevoInput" rows="2" aria-describedby="titleNuevoHelp" readonly="readonly"></textarea>
                            </div>
                        </div>
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="motivoExcepcionInput">Motivo excepción:</label>
                                <input type="text" class="form-control" id="motivoExcepcionInput" rows="2" aria-describedby="motivoExcepcionInput" readonly="readonly" />
                                <input type="hidden"  id="consecutivoNuevo" />
                            </div>
                        </div>
                    </div>
                    
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-primary" id="generarVersionBtn">Generar</button>
                  </div>
            
            </div>
        </div>
    </div>
     

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <script src="js/sisecop_commons.js"></script>
    <script src="js/CaptureProjectUI.js"></script>
</body>